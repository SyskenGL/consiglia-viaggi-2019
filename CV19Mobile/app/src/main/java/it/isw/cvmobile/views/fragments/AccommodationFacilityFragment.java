package it.isw.cvmobile.views.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wang.avi.AVLoadingIndicatorView;
import it.isw.cvmobile.R;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.presenters.fragments.AccommodationFacilityPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.review.ReviewCardAdapter;
import it.isw.cvmobile.widgets.slider.SliderAdapter;


@Completed
public class AccommodationFacilityFragment extends Fragment {

    private final NavigationActivity navigationActivity;
    private final AccommodationFacility accommodationFacility;
    private AccommodationFacilityPresenter accommodationFacilityPresenter;
    private SparkButton buttonFavorite;
    private ImageView buttonBack;
    private Button buttonMap;
    private Button buttonReviewsFilters;
    private FloatingActionButton buttonWriteReview;
    private TextView textViewName;
    private TextView textViewTotalViews;
    private TextView textViewTotalFavorites;
    private TextView textViewTotalReviews;
    private TextView textViewTotalReviewsSecond;
    private TextView textViewDescription;
    private TextView textViewAddress;
    private TextView textViewPhone;
    private TextView textViewEmail;
    private TextView textViewWebsite;
    private NestedScrollView nestedScrollView;
    private RecyclerView relatedPlacesRecyclerView;
    private RecyclerView reviewsRecyclerView;
    private AVLoadingIndicatorView loadingSpinnerReviews;
    private AVLoadingIndicatorView loadingSpinnerRelatedPlaces;
    private ImageView typeFlag;
    private SliderView imageSlider;
    private RatingBar ratingBar;
    private View reviewsContainer;
    private View relatedPlacesContainer;
    private AppBarLayout appBarLayout;



    public AccommodationFacilityFragment(NavigationActivity navigationActivity,
                                         AccommodationFacility accommodationFacility) {
        this.accommodationFacility = accommodationFacility;
        this.navigationActivity = navigationActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accommodation_facility, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonFavorite = view.findViewById(R.id.fragment_accommodation_facility_button_favorite);
        buttonBack = view.findViewById(R.id.fragment_accommodation_facility_button_back);
        buttonMap = view.findViewById(R.id.fragment_accommodation_facility_button_map);
        buttonReviewsFilters = view.findViewById(R.id.fragment_accommodation_facility_button_reviews_filters);
        buttonWriteReview = view.findViewById(R.id.fragment_accommodation_facility_button_write_review);
        textViewName = view.findViewById(R.id.fragment_accommodation_facility_name);
        textViewTotalFavorites = view.findViewById(R.id.fragment_accommodation_facility_total_favorites);
        textViewTotalReviews = view.findViewById(R.id.fragment_accommodation_facility_total_reviews);
        textViewTotalViews = view.findViewById(R.id.fragment_accommodation_facility_total_views);
        textViewTotalReviewsSecond = view.findViewById(R.id.fragment_accommodation_facility_total_reviews_second);
        textViewDescription = view.findViewById(R.id.fragment_accommodation_facility_text_view_description);
        textViewAddress = view.findViewById(R.id.fragment_accommodation_facility_text_view_address);
        textViewEmail = view.findViewById(R.id.fragment_accommodation_facility_text_view_email);
        textViewPhone = view.findViewById(R.id.fragment_accommodation_facility_text_view_phone);
        textViewWebsite = view.findViewById(R.id.fragment_accommodation_facility_text_view_website);
        nestedScrollView = view.findViewById(R.id.fragment_accommodation_facility_nested_scroll_view);
        relatedPlacesRecyclerView = view.findViewById(R.id.fragment_accommodation_facility_related_places_recycler_view);
        relatedPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(navigationActivity, RecyclerView.HORIZONTAL, false));
        new LinearSnapHelper().attachToRecyclerView(relatedPlacesRecyclerView);
        reviewsRecyclerView = view.findViewById(R.id.fragment_accommodation_facility_reviews_recycler_view);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(navigationActivity, RecyclerView.VERTICAL, false));
        loadingSpinnerRelatedPlaces = view.findViewById(R.id.fragment_accommodation_facility_loading_spinner_related_places);
        loadingSpinnerReviews = view.findViewById(R.id.fragment_accommodation_facility_loading_spinner_reviews);
        typeFlag = view.findViewById(R.id.fragment_accommodation_facility_type_flag);
        imageSlider = view.findViewById(R.id.fragment_accommodation_facility_image_slider);
        imageSlider.getForeground().setAlpha(0);
        ratingBar = view.findViewById(R.id.fragment_accommodation_facility_rating);
        appBarLayout = view.findViewById(R.id.fragment_accommodation_facility_app_bar_layout);
        reviewsContainer = view.findViewById(R.id.fragment_accommodation_facility_container);
        relatedPlacesContainer = view.findViewById(R.id.fragment_accommodation_facility_related_places_container);
        accommodationFacilityPresenter = new AccommodationFacilityPresenter(this, accommodationFacility);
        initializeImageSlider();
        listenToScrollEvents();
        listenToClickEvents();
    }

    private void listenToScrollEvents() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                accommodationFacilityPresenter.onOffsetChangedListenerAppBarLayout(verticalOffset);
            }
        });
        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                accommodationFacilityPresenter.onNestedScrollViewScrolled(
                        nestedScrollView.canScrollVertically(1)
                );
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        accommodationFacilityPresenter.onActivityResult(requestCode, resultCode, data);
    }

    private void listenToClickEvents() {
        buttonFavorite.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                accommodationFacilityPresenter.onButtonFavoriteClicked(buttonState);
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
        buttonReviewsFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accommodationFacilityPresenter.onButtonReviewsFiltersClicked();
            }
        });
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accommodationFacilityPresenter.onButtonMapClicked();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accommodationFacilityPresenter.onButtonBackClicked();
            }
        });
        buttonWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accommodationFacilityPresenter.onButtonWriteReviewClicked();
            }
        });
    }

    private void initializeImageSlider() {
        imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM);
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        imageSlider.setScrollTimeInSec(3);
        imageSlider.setIndicatorSelectedColor(Color.WHITE);
        imageSlider.setIndicatorUnselectedColor(Color.GRAY);
    }

    public void startAnimationRatingBar(TranslateAnimation translateAnimation) {
        ratingBar.startAnimation(translateAnimation);
    }

    public void startAnimationTypeFlag(TranslateAnimation translateAnimation) {
        typeFlag.startAnimation(translateAnimation);
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public AccommodationFacility getAccommodationFacility() {
        return accommodationFacility;
    }

    public int getImageSliderForegroundAlpha() {
        return imageSlider.getForeground().getAlpha();
    }

    public int getLeftMarginRatingBar() {
        return ((ViewGroup.MarginLayoutParams) ratingBar.getLayoutParams()).leftMargin;
    }

    public int getWidthRatingBar() {
        return ((ViewGroup.MarginLayoutParams) ratingBar.getLayoutParams()).width;
    }

    public int getWidthTypeFlag() {
        return ((ViewGroup.MarginLayoutParams) typeFlag.getLayoutParams()).width;
    }

    public int getRightMarginTypeFlag() {
        return ((ViewGroup.MarginLayoutParams) typeFlag.getLayoutParams()).rightMargin;
    }

    public int getAppBarLayoutTotalScrollRange() {
        return appBarLayout.getTotalScrollRange();
    }

    public void setImageSliderForegroundAlpha(int alpha) {
        imageSlider.getForeground().setAlpha(alpha);
    }

    public void setTextName(String name) {
        textViewName.setText(name);
    }

    public void setRating(int rating) {
        ratingBar.setRating(rating);
    }

    public void setTextTotalViews(String totalViews) {
        textViewTotalViews.setText(totalViews);
    }

    public void setTextTotalFavorites(String totalFavorites) {
        textViewTotalFavorites.setText(totalFavorites);
    }

    public void setTextTotalReviews(String totalReviews) {
        textViewTotalReviews.setText(totalReviews);
    }

    public void setTextTotalReviewsSecond(String totalReviewsSecond) {
        textViewTotalReviewsSecond.setText(totalReviewsSecond);
    }

    public void setTextDescription(String description) {
        textViewDescription.setText(description);
    }

    public void setTextAddress(String address) {
        textViewAddress.setText(address);
    }

    public void setTextPhone(String phone) {
        textViewPhone.setText(phone);
    }

    public void setTextEmail(String email) {
        textViewEmail.setText(email);
    }

    public void setTextWebsite(String website) {
        textViewWebsite.setText(website);
    }

    public void setTypeFlagImage(int resource) {
        typeFlag.setImageResource(resource);
    }

    public void setImageSliderAdapter(SliderAdapter sliderAdapter) {
        imageSlider.setSliderAdapter(sliderAdapter);
    }

    public void setRelatedPlacesRecyclerViewAdapter(AccommodationCardAdapter accommodationCardAdapter) {
        relatedPlacesRecyclerView.setAdapter(accommodationCardAdapter);
    }

    public void setButtonFavoriteVisibility(boolean visibility) {
        if(visibility) {
            buttonFavorite.setVisibility(View.VISIBLE);
        } else {
            buttonFavorite.setVisibility(View.GONE);
        }
    }

    public void setButtonWriteReviewVisibility(boolean visibility) {
        if(visibility) {
            buttonWriteReview.setVisibility(View.VISIBLE);
        } else {
            buttonWriteReview.setVisibility(View.GONE);
        }
    }

    public void setReviewsContainerVisibility(boolean visibility) {
        if(visibility) {
            reviewsContainer.setVisibility(View.VISIBLE);
        } else {
            reviewsContainer.setVisibility(View.GONE);
        }
    }

    public void setRelatedPlacesContainerVisibility(boolean visibility) {
        if(visibility) {
            relatedPlacesContainer.setVisibility(View.VISIBLE);
        } else {
            relatedPlacesContainer.setVisibility(View.GONE);
        }
    }

    public void setReviewsRecyclerViewAdapter(ReviewCardAdapter reviewCardAdapter) {
        reviewsRecyclerView.setAdapter(reviewCardAdapter);
    }

    public void setLoadingSpinnerRelatedPlacesVisibility(boolean visibility) {
        if(visibility) {
            loadingSpinnerRelatedPlaces.setVisibility(View.VISIBLE);
        } else {
            loadingSpinnerRelatedPlaces.setVisibility(View.GONE);
        }
    }

    public void setLoadingSpinnerReviewsVisibility(boolean visibility) {
        if(visibility) {
            loadingSpinnerReviews.setVisibility(View.VISIBLE);
        } else {
            loadingSpinnerReviews.setVisibility(View.GONE);
        }
    }

    public void setCheckedButtonFavorite(boolean checked) {
        buttonFavorite.setChecked(checked);
    }

}