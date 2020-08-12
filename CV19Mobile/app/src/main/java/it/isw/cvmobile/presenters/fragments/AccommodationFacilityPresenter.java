package it.isw.cvmobile.presenters.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.TranslateAnimation;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.dao.interfaces.ReviewDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.Review;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.Synchronizer;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.fragments.AccommodationFacilityFragment;
import it.isw.cvmobile.views.fragments.MapFragment;
import it.isw.cvmobile.views.fragments.PostReviewFragment;
import it.isw.cvmobile.views.fragments.ReviewsFiltersFragment;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.enumerations.AccommodationCardAdapterMode;
import it.isw.cvmobile.widgets.scrollview.cards.review.ReviewCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.review.enumerations.ReviewCardAdapterMode;
import it.isw.cvmobile.widgets.slider.SliderAdapter;
import it.isw.cvmobile.widgets.slider.SliderItem;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class AccommodationFacilityPresenter {

    private static final int REVIEWS_LIMIT = 5;

    private final AccommodationFacilityFragment accommodationFacilityFragment;
    private final AccommodationFacility accommodationFacility;
    private List<Review> reviews;
    private ReviewCardAdapter reviewCardAdapter;
    private Map<String, String> reviewsFilters;
    private Map<String, String> reviewsSortingKeys;
    private boolean reviewsFinished;
    private int reviewsOffset;
    private int lastScrollOffset;
    private float typeFlagMarginEnd;
    private float ratingBarMarginStart;
    private Synchronizer synchronizer;



    public AccommodationFacilityPresenter(AccommodationFacilityFragment accommodationFacilityFragment,
                                          AccommodationFacility accommodationFacility) {
        this.accommodationFacilityFragment = accommodationFacilityFragment;
        this.accommodationFacility = accommodationFacility;
        accommodationFacilityFragment.getNavigationActivity().setToolbarVisibility(false);
        accommodationFacilityFragment.getNavigationActivity().closeSideNavigationMenu();
        accommodationFacilityFragment.getNavigationActivity().setLockedNavigationMenu(true);
        initializeReviewsCardAdapter();
        showAccommodationFacilityData();
        fetchRelatedPlaces();
        fetchReviews(false);
    }

    private void initializeReviewsCardAdapter() {
        reviewsFilters = new HashMap<>();
        reviewsSortingKeys = new HashMap<>();
        reviewsFilters.put("accommodation_facility_id", accommodationFacility.getAccommodationFacilityId());
        reviewsFilters.put("status", "approved");
        synchronizer = new Synchronizer(true);
        if(!User.isLoggedIn()) {
            accommodationFacilityFragment.setButtonFavoriteVisibility(false);
            accommodationFacilityFragment.setButtonWriteReviewVisibility(false);
        } else {
            if(accommodationFacility.isFavorite()) {
                accommodationFacilityFragment.setCheckedButtonFavorite(true);
            }
            if(accommodationFacility.isCommented()) {
                accommodationFacilityFragment.setButtonWriteReviewVisibility(false);
            }
            reviewsFilters.put("user_id", User.getInstance().getUserId());
        }
        reviews = new ArrayList<>();
        reviewCardAdapter = new ReviewCardAdapter(
                accommodationFacilityFragment.getNavigationActivity(),
                reviews,
                ReviewCardAdapterMode.DEFAULT_INFO_CARD
        );
        accommodationFacilityFragment.setReviewsRecyclerViewAdapter(reviewCardAdapter);
    }

    public void onOffsetChangedListenerAppBarLayout(int verticalOffset) {
        int totalScrollRange = accommodationFacilityFragment.getAppBarLayoutTotalScrollRange();
        animateRatingBar(verticalOffset, totalScrollRange);
        animateOverlay(verticalOffset, totalScrollRange);
        animateTypeFlag(verticalOffset, totalScrollRange);
        lastScrollOffset = verticalOffset;
    }

    public void onButtonFavoriteClicked(boolean buttonState) {
        if(buttonState){
            addFavorite(accommodationFacility);
        }else {
            deleteFavorite(accommodationFacility);
        }
    }

    public void onButtonReviewsFiltersClicked() {
        ReviewsFiltersFragment reviewsFiltersFragment = new ReviewsFiltersFragment(
                reviewsFilters,
                reviewsSortingKeys,
                accommodationFacilityFragment
        );
        FragmentManager fragmentManager = accommodationFacilityFragment.getFragmentManager();
        if(fragmentManager != null) reviewsFiltersFragment.show(fragmentManager, "REVIEWS_FILTERS_FRAGMENT");
    }

    public void onNestedScrollViewScrolled(boolean canScrollVertically) {
        if(!reviewsFinished && !canScrollVertically) {
            fetchReviews(true);
        }
    }

    public void onButtonWriteReviewClicked() {
        openPostReviewFragment();
    }

    public void onButtonMapClicked() {
        openMapFragment();
    }

    public void onButtonBackClicked() {
        final FragmentManager fragmentManager = accommodationFacilityFragment.getFragmentManager();
        if(fragmentManager != null) {
            fragmentManager.popBackStack();
            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if(fragmentManager.getBackStackEntryCount() == 0) {
                        accommodationFacilityFragment.getNavigationActivity().setLockedNavigationMenu(false);
                        accommodationFacilityFragment.getNavigationActivity().setToolbarVisibility(true);
                    }
                    fragmentManager.removeOnBackStackChangedListener(this);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ReviewsFiltersPresenter.REVIEWS_FILTERS_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if(bundle != null) {
                reviewsFilters = (Map<String, String>) bundle.get("filters");
                reviewsSortingKeys = (Map<String, String>) bundle.get("sorting_keys");
                fetchReviews(false);
            }
        }
    }

    private void openMapFragment() {
        NavigationActivity navigationActivity = accommodationFacilityFragment.getNavigationActivity();
        MapFragment mapFragment = new MapFragment(
                navigationActivity,
                Double.parseDouble(accommodationFacility.getLatitude()),
                Double.parseDouble(accommodationFacility.getLongitude())
        );
        mapFragment.setBottomOpening(true);
        FragmentTransaction fragmentTransaction = navigationActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_up,
                R.anim.exit_to_bottom,
                R.anim.enter_from_bottom,
                R.anim.exit_to_up
        );
        fragmentTransaction.add(R.id.activity_navigation_fragment_container, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openPostReviewFragment() {
        NavigationActivity navigationActivity = accommodationFacilityFragment.getNavigationActivity();
        PostReviewFragment postReviewFragment = new PostReviewFragment(
                navigationActivity,
                accommodationFacilityFragment
        );
        FragmentTransaction fragmentTransaction = navigationActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_up,
                R.anim.exit_to_bottom,
                R.anim.enter_from_bottom,
                R.anim.exit_to_up
        );
        fragmentTransaction.add(R.id.activity_navigation_fragment_container, postReviewFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void animateOverlay(int verticalOffset, int totalScrollRange) {
        float scrollDelta = lastScrollOffset - verticalOffset;
        float scrollPercentage = scrollDelta/totalScrollRange*100.0f;
        int newOverlayAlpha = Math.round(255/100.0f*scrollPercentage);
        int oldOverlayAlpha = accommodationFacilityFragment.getImageSliderForegroundAlpha();
        accommodationFacilityFragment.setImageSliderForegroundAlpha(
                Math.min(Math.max(0, oldOverlayAlpha + newOverlayAlpha), 255)
        );
    }

    private void animateRatingBar(int verticalOffset, int totalScrollRange) {
        NavigationActivity navigationActivity = accommodationFacilityFragment.getNavigationActivity();
        int ratingBarLeftMargin = accommodationFacilityFragment.getLeftMarginRatingBar();
        int ratingBarWidth = accommodationFacilityFragment.getWidthRatingBar();
        float ratingBarAnimationRun = navigationActivity.getScreenWidth()/2.0f- ratingBarLeftMargin-ratingBarWidth/2.0f;
        float scrollDelta = lastScrollOffset - verticalOffset;
        float scrollPercentage = scrollDelta/totalScrollRange*100.0f;
        float scrollShift = ratingBarAnimationRun/100.0f*scrollPercentage;
        TranslateAnimation translateAnimation = new TranslateAnimation(
                ratingBarMarginStart,
                ratingBarMarginStart += scrollShift,
                0,
                0
        );
        translateAnimation.setFillAfter(true);
        accommodationFacilityFragment.startAnimationRatingBar(translateAnimation);
    }

    private void animateTypeFlag(int verticalOffset, int totalScrollRange) {
        NavigationActivity navigationActivity = accommodationFacilityFragment.getNavigationActivity();
        int typeFlagRightMargin = accommodationFacilityFragment.getRightMarginTypeFlag();
        int typeFlagWidth = accommodationFacilityFragment.getWidthTypeFlag();
        float typeFlagAnimationRun = navigationActivity.getScreenWidth()/2.0f- typeFlagRightMargin-typeFlagWidth/2.0f;
        float scrollDelta = lastScrollOffset - verticalOffset;
        float scrollPercentage = scrollDelta/totalScrollRange*100.0f;
        float scrollShift = typeFlagAnimationRun/100.0f*scrollPercentage;
        TranslateAnimation translateAnimation = new TranslateAnimation(
                typeFlagMarginEnd,
                typeFlagMarginEnd -= scrollShift,
                0,
                0
        );
        translateAnimation.setFillAfter(true);
        accommodationFacilityFragment.startAnimationTypeFlag(translateAnimation);
    }

    private void showAccommodationFacilityData() {
        accommodationFacilityFragment.setRating(accommodationFacility.getRating());
        if(accommodationFacility.getName() != null)
            accommodationFacilityFragment.setTextName(accommodationFacility.getName());
        accommodationFacilityFragment.setTextTotalFavorites(
                String.valueOf(accommodationFacility.getTotalFavorites())
        );
        accommodationFacilityFragment.setTextTotalViews(
                String.valueOf(accommodationFacility.getTotalViews())
        );
        accommodationFacilityFragment.setTextTotalReviews(
                String.valueOf(accommodationFacility.getTotalReviews())
        );
        accommodationFacilityFragment.setTextTotalReviewsSecond("("+accommodationFacility.getTotalReviews()+")");
        if(accommodationFacility.getDescription() != null) {
            accommodationFacilityFragment.setTextDescription(accommodationFacility.getDescription());
        }
        if(accommodationFacility.getAddress() != null) {
            accommodationFacilityFragment.setTextAddress(accommodationFacility.getAddress());
        }
        if(accommodationFacility.getEmail() != null) {
            accommodationFacilityFragment.setTextEmail(accommodationFacility.getEmail());
        }
        if(accommodationFacility.getPhone() != null) {
            accommodationFacilityFragment.setTextPhone(accommodationFacility.getPhone());
        }
        if(accommodationFacility.getWebsite() != null) {
            accommodationFacilityFragment.setTextWebsite(accommodationFacility.getWebsite());
        }
        showTypeFlag(accommodationFacility.getType());
        if(accommodationFacility.getImages() != null)  {
            setImageSliderAdapter();
        }
    }

    private void showTypeFlag(String type) {
        if(type != null) {
            if(type.equals("lodging")) {
                accommodationFacilityFragment.setTypeFlagImage(R.drawable.flag_hotel);
            } else if(type.equals("restaurant")) {
                accommodationFacilityFragment.setTypeFlagImage(R.drawable.flag_restaurant);
            } else {
                accommodationFacilityFragment.setTypeFlagImage(R.drawable.flag_tourist_attraction);
            }
        }
    }

    private void setImageSliderAdapter() {
        ArrayList<SliderItem> sliderItems = new ArrayList<>();
        for (String image : accommodationFacility.getImages()) {
            if(image != null) {
                sliderItems.add(new SliderItem(image));
            }
        }
        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems);
        accommodationFacilityFragment.setImageSliderAdapter(sliderAdapter);
    }

    private void handleFetchRelatedPlacesFailure(Exception exception) {
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    accommodationFacilityFragment.getNavigationActivity(),
                    R.string.toast_error_network_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        } else {
            MotionToast.display(
                    accommodationFacilityFragment.getNavigationActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
        accommodationFacilityFragment.setRelatedPlacesContainerVisibility(false);
        accommodationFacilityFragment.setLoadingSpinnerRelatedPlacesVisibility(false);
    }

    private void handleFetchRelatedPlacesSuccess(JsonObject results,
                                                 AccommodationFacilityDAO accommodationFacilityDAO) {
        ArrayList<AccommodationFacility> relatedPlaces = new ArrayList<>(accommodationFacilityDAO.parseResults(results));
        if(relatedPlaces.size() > 0) {
            Iterator<AccommodationFacility> accommodationFacilitiesIterator = relatedPlaces.iterator();
            while (accommodationFacilitiesIterator.hasNext()) {
                AccommodationFacility relatedPlace = accommodationFacilitiesIterator.next();
                if(accommodationFacility.getType() != null &&
                        accommodationFacility.getType().equals(relatedPlace.getType())) {
                    accommodationFacilitiesIterator.remove();
                }
            }
            AccommodationCardAdapter accommodationCardAdapter = new AccommodationCardAdapter(
                    accommodationFacilityFragment.getNavigationActivity(),
                    relatedPlaces,
                    AccommodationCardAdapterMode.SOFT_ADAPTER
            );
            accommodationFacilityFragment.setRelatedPlacesRecyclerViewAdapter(accommodationCardAdapter);
        } else {
            accommodationFacilityFragment.setRelatedPlacesContainerVisibility(false);
        }
        accommodationFacilityFragment.setLoadingSpinnerRelatedPlacesVisibility(false);
    }

    private void handleFetchReviewsSuccess(JsonObject results, ReviewDAO reviewDAO) {
        int retrieved = results.getAsJsonObject("data").get("retrieved").getAsInt();
        reviewsOffset += retrieved;
        if(retrieved != 0) {
            reviews.addAll(reviewDAO.parseResults(results));
            reviewCardAdapter.notifyDataSetChanged();
            if(retrieved < REVIEWS_LIMIT) {
                reviewsFinished = true;
            }
        } else {
            reviewsFinished = true;
            accommodationFacilityFragment.setReviewsContainerVisibility(false);
        }
        accommodationFacilityFragment.setLoadingSpinnerReviewsVisibility(false);
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    private void handleFetchReviewsFailure(Exception exception, boolean fetchMore) {
        if(exception.getClass() == NoInternetConnectionException.class) {
            if(fetchMore) {
                MotionToast.display(
                        accommodationFacilityFragment.getNavigationActivity(),
                        R.string.toast_warning_internet_connection,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            }
        } else {
            if(fetchMore) {
                MotionToast.display(
                        accommodationFacilityFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
        }
        accommodationFacilityFragment.setLoadingSpinnerReviewsVisibility(false);
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    @RequiresInternetConnection
    private void fetchRelatedPlaces() {
        if(FullScreenActivity.isNetworkAvailable(accommodationFacilityFragment.getNavigationActivity())) {
            Map<String, String> filters = new HashMap<>();
            Map<String, String> sortingKeys = new HashMap<>();
            String latitude = accommodationFacility.getLatitude();
            String longitude = accommodationFacility.getLongitude();
            if(User.isLoggedIn()) {
                filters.put("user_id", User.getInstance().getUserId());
            }
            filters.put("latitude", latitude);
            filters.put("longitude", longitude);
            filters.put("radius", "10000");
            sortingKeys.put("rating", "DESC");
            sortingKeys.put("total_favorites", "DESC");
            sortingKeys.put("total_views", "DESC");
            final AccommodationFacilityDAO accommodationFacilityDAO =
                    DAOFactory.getAccommodationFacilityDAO(accommodationFacilityFragment.getNavigationActivity());
            accommodationFacilityDAO.getAccommodationFacilities(filters, sortingKeys, null, 0, 10, new LambdaResultsHandler() {

                @Override
                public void onSuccess(JsonObject results) {
                    handleFetchRelatedPlacesSuccess(results,accommodationFacilityDAO);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleFetchRelatedPlacesFailure(exception);
                }

            });
        }else {
            handleFetchRelatedPlacesFailure(new NoInternetConnectionException());
        }
    }

    @RequiresInternetConnection
    private void deleteFavorite(final AccommodationFacility accommodationFacility) {
        if(FullScreenActivity.isNetworkAvailable(accommodationFacilityFragment.getNavigationActivity())) {
            AccommodationFacilityDAO accommodationFacilityDAO =
                    DAOFactory.getAccommodationFacilityDAO(accommodationFacilityFragment.getNavigationActivity());
            accommodationFacilityDAO.deleteFromFavorites(
                    accommodationFacility.getAccommodationFacilityId(),
                    User.getInstance().getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            User.getInstance().setTotalFavorites(User.getInstance().getTotalFavorites()-1);
                            accommodationFacility.setTotalFavorites(accommodationFacility.getTotalFavorites()-1);
                            accommodationFacilityFragment.setTextTotalFavorites(
                                    String.valueOf(accommodationFacility.getTotalFavorites())
                            );
                            accommodationFacility.setFavorite(false);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            accommodationFacilityFragment.setCheckedButtonFavorite(true);
                            MotionToast.display(
                                    accommodationFacilityFragment.getNavigationActivity(),
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    });
        } else{
            MotionToast.display(
                    accommodationFacilityFragment.getNavigationActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    @RequiresInternetConnection
    private void addFavorite(final AccommodationFacility accommodationFacility) {
        if(FullScreenActivity.isNetworkAvailable(accommodationFacilityFragment.getNavigationActivity())) {
            AccommodationFacilityDAO accommodationFacilityDAO =
                    DAOFactory.getAccommodationFacilityDAO(accommodationFacilityFragment.getNavigationActivity());
            accommodationFacilityDAO.addToFavorites(
                    accommodationFacility.getAccommodationFacilityId(),
                    User.getInstance().getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            User.getInstance().setTotalFavorites(User.getInstance().getTotalFavorites()+1);
                            accommodationFacility.setTotalFavorites(accommodationFacility.getTotalFavorites()+1);
                            accommodationFacilityFragment.setTextTotalFavorites(
                                    String.valueOf(accommodationFacility.getTotalFavorites())
                            );
                            accommodationFacility.setFavorite(true);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            accommodationFacilityFragment.setCheckedButtonFavorite(false);
                            MotionToast.display(
                                    accommodationFacilityFragment.getNavigationActivity(),
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    });
        } else {
            accommodationFacilityFragment.setCheckedButtonFavorite(false);
            MotionToast.display(
                    accommodationFacilityFragment.getNavigationActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    @RequiresInternetConnection
    private void fetchReviews(final boolean fetchMore) {
        synchronized(synchronizer.getSynchronizer()) {
            if(!synchronizer.isSatisfied())
                return;
            else synchronizer.setSatisfied(false);
        }
        if(FullScreenActivity.isNetworkAvailable(accommodationFacilityFragment.getNavigationActivity())) {
            if(fetchMore){
                accommodationFacilityFragment.setLoadingSpinnerReviewsVisibility(true);
            } else {
                reviewsOffset = 0;
                reviews.clear();
                reviewsFinished = false;
            }
            final ReviewDAO reviewDAO = DAOFactory.getReviewDAO(accommodationFacilityFragment.getNavigationActivity());
            reviewDAO.getReviews(reviewsFilters, reviewsSortingKeys, reviewsOffset, REVIEWS_LIMIT, new LambdaResultsHandler() {

                @Override
                public void onSuccess(JsonObject results) {
                    handleFetchReviewsSuccess(results, reviewDAO);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleFetchReviewsFailure(exception, fetchMore);
                }

            });
        } else {
            handleFetchReviewsFailure(new NoInternetConnectionException(), fetchMore);
        }
    }

}
