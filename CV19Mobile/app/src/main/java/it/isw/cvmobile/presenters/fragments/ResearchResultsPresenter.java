package it.isw.cvmobile.presenters.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.Synchronizer;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ResearchResultsFiltersFragment;
import it.isw.cvmobile.views.fragments.ResearchResultsFragment;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.enumerations.AccommodationCardAdapterMode;
import it.isw.cvmobile.widgets.slider.listeners.OnImageChangedListener;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class ResearchResultsPresenter {

    private final static int LIMIT = 5;

    private final ResearchResultsFragment researchResultsFragment;
    private final List<AccommodationFacility> accommodationFacilities;
    private AccommodationCardAdapter accommodationCardAdapter;
    private String keywords;
    private Map<String, String> filters;
    private Map<String, String> sortingKeys;
    private boolean accommodationFacilitiesFinished;
    private int lastCardViewPosition;
    private int accommodationFacilitiesOffset;
    private Synchronizer synchronizer;



    private ResearchResultsPresenter(@NotNull ResearchResultsFragment researchResultsFragment) {
        this.researchResultsFragment = researchResultsFragment;
        accommodationFacilities = new ArrayList<>();
        synchronizer = new Synchronizer(true);
        researchResultsFragment.getNavigationActivity().setToolbarVisibility(false);
        researchResultsFragment.getNavigationActivity().closeSideNavigationMenu();
        researchResultsFragment.getNavigationActivity().setLockedNavigationMenu(true);
    }

    public ResearchResultsPresenter(ResearchResultsFragment researchResultsFragment,
                                    String keywords) {
        this(researchResultsFragment);
        filters = new HashMap<>();
        sortingKeys = new HashMap<>();
        this.keywords = keywords;
        if(User.isLoggedIn()) {
            filters.put("user_id", User.getInstance().getUserId());
        }
        accommodationCardAdapter = new AccommodationCardAdapter(
                researchResultsFragment.getNavigationActivity(),
                accommodationFacilities,
                AccommodationCardAdapterMode.HEAVY_ADAPTER,
                keywords
        );
        setOnImageChangedListener();
        researchResultsFragment.setAccommodationFacilitiesRecyclerViewAdapter(accommodationCardAdapter);
        fetchAccommodationFacilities(false);
    }

    public ResearchResultsPresenter(ResearchResultsFragment researchResultsFragment,
                                    Map<String, String> filters) {
        this(researchResultsFragment);
        this.filters = filters;
        sortingKeys = new HashMap<>();
        if(User.isLoggedIn()) {
            this.filters.put("user_id", User.getInstance().getUserId());
        }
        accommodationCardAdapter = new AccommodationCardAdapter(
                researchResultsFragment.getNavigationActivity(),
                accommodationFacilities,
                AccommodationCardAdapterMode.HEAVY_ADAPTER
        );
        setOnImageChangedListener();
        researchResultsFragment.setAccommodationFacilitiesRecyclerViewAdapter(accommodationCardAdapter);
        fetchAccommodationFacilities(false);
    }

    public void onButtonBackClicked() {
        FragmentManager fragmentManager = researchResultsFragment.getFragmentManager();
        if(fragmentManager != null) {
            fragmentManager.popBackStackImmediate();
            if(fragmentManager.getBackStackEntryCount() == 0) {
                researchResultsFragment.getNavigationActivity().setLockedNavigationMenu(false);
                researchResultsFragment.getNavigationActivity().setToolbarVisibility(true);
            }
        }
    }

    public void onButtonFiltersClicked() {
        ResearchResultsFiltersFragment researchResultsFiltersFragment = new ResearchResultsFiltersFragment(
                filters,
                sortingKeys,
                researchResultsFragment
        );
        FragmentManager fragmentManager = researchResultsFragment.getFragmentManager();
        if(fragmentManager != null) {
            researchResultsFiltersFragment.show(fragmentManager, "RESEARCH_RESULTS_FILTERS_FRAGMENT");
        }
    }

    public void onButtonRefreshClicked() {
        researchResultsFragment.startButtonRefreshSpinAnimation();
        fetchAccommodationFacilities(false);
    }

    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ResearchResultsFiltersPresenter.RESEARCH_RESULTS_FILTERS_REQUEST_CODE
                && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if(bundle != null) {
                filters = (Map<String, String>) bundle.get("filters");
                sortingKeys = (Map<String, String>) bundle.get("sorting_keys");
                fetchAccommodationFacilities(false);
            }
        }
    }

    public void onFavoritesRecyclerViewScrolled(boolean canScrollHorizontally,
                                                int currentCardViewPosition) {
        if(currentCardViewPosition >= 0 && currentCardViewPosition != lastCardViewPosition) {
            lastCardViewPosition = currentCardViewPosition;
            setBlurryBackground(accommodationFacilities.get(lastCardViewPosition).getImages()[0]);
        }
        if(!accommodationFacilitiesFinished && !canScrollHorizontally) {
            fetchAccommodationFacilities(true);
        }
    }

    private void setOnImageChangedListener() {
        accommodationCardAdapter.setImageChangedListener(new OnImageChangedListener() {
            @Override
            public void onImageChanged(String currentImageUrl) {
                setBlurryBackground(currentImageUrl);
            }
        });
    }

    private void setBlurryBackground(String url) {
        Glide.with(researchResultsFragment.getNavigationActivity())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(url)
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        researchResultsFragment.setBlurryBackground(
                                resource,
                                8,
                                8,
                                Color.argb(0, 0, 0, 0)
                        );
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        researchResultsFragment.setBlurryBackground(
                                BitmapFactory.decodeResource(
                                        researchResultsFragment.getNavigationActivity().getResources(),
                                        R.drawable.ic_error_sad
                                ),
                                8,
                                8,
                                Color.argb(0, 0, 0, 0)
                        );
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}

                });
    }

    private void handleAccommodationFacilitiesSuccess(@NotNull JsonObject results,
                                                      AccommodationFacilityDAO accommodationFacilityDAO,
                                                      final boolean fetchMore) {
        researchResultsFragment.clearButtonRefreshAnimation();
        researchResultsFragment.setErrorViewVisibility(false);
        int retrieved = results.getAsJsonObject("data").get("retrieved").getAsInt();
        accommodationFacilitiesOffset += retrieved;
        if(retrieved != 0) {
            accommodationFacilities.addAll(accommodationFacilityDAO.parseResults(results));
            accommodationCardAdapter.notifyDataSetChanged();
            if(retrieved < LIMIT) {
                accommodationFacilitiesFinished = true;
            }
            if(!fetchMore) {
                researchResultsFragment.setButtonFiltersVisibility(true);
                String[] backgroundAvailable = accommodationFacilities.get(0).getImages();
                if(backgroundAvailable != null && backgroundAvailable.length > 0) {
                    setBlurryBackground(backgroundAvailable[0]);
                }
            }
        } else {
            accommodationFacilitiesFinished = true;
            if(!fetchMore) {
                researchResultsFragment.setNoResultsViewVisibility(true);
            }
        }
        if(!fetchMore) {
            researchResultsFragment.setLoadingSpinnerInitialResultsVisibility(false);
        } else {
            researchResultsFragment.setLoadingSpinnerMoreResultsVisibility(false);
        }
        researchResultsFragment.unmaskAccommodationFacilitiesViewItemTouch();
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    private void handleAccommodationFacilitiesFailure(@NotNull Exception exception, final boolean fetchMore) {
        researchResultsFragment.clearButtonRefreshAnimation();
        if(exception.getClass() == NoInternetConnectionException.class) {
            if(fetchMore) {
                MotionToast.display(
                        researchResultsFragment.getNavigationActivity(),
                        R.string.toast_warning_internet_connection,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            }
        } else {
            if(fetchMore) {
                MotionToast.display(
                        researchResultsFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
        }
        if(!fetchMore) {
            researchResultsFragment.setLoadingSpinnerInitialResultsVisibility(false);
            researchResultsFragment.setErrorViewVisibility(true);
            researchResultsFragment.maskAccommodationFacilitiesRecyclerViewItemTouch();
        } else {
            researchResultsFragment.setLoadingSpinnerMoreResultsVisibility(false);
            researchResultsFragment.unmaskAccommodationFacilitiesViewItemTouch();
        }
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    @RequiresInternetConnection
    private void fetchAccommodationFacilities(final boolean fetchMore) {
        synchronized(synchronizer.getSynchronizer()) {
            if(!synchronizer.isSatisfied())
                return;
            else synchronizer.setSatisfied(false);
        }
        if(FullScreenActivity.isNetworkAvailable(researchResultsFragment.getNavigationActivity())) {
            if(fetchMore) {
                researchResultsFragment.setLoadingSpinnerMoreResultsVisibility(true);
            } else {
                accommodationFacilitiesOffset = 0;
                accommodationFacilities.clear();
                accommodationFacilitiesFinished = false;
            }
            researchResultsFragment.maskAccommodationFacilitiesRecyclerViewItemTouch();
            final AccommodationFacilityDAO accommodationFacilityDAO =
                    DAOFactory.getAccommodationFacilityDAO(researchResultsFragment.getNavigationActivity());
            accommodationFacilityDAO.getAccommodationFacilities(
                    filters,
                    sortingKeys,
                    keywords,
                    accommodationFacilitiesOffset,
                    LIMIT,
                    new LambdaResultsHandler() {

                    @Override
                    public void onSuccess(JsonObject results) {
                        handleAccommodationFacilitiesSuccess(results, accommodationFacilityDAO, fetchMore);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        handleAccommodationFacilitiesFailure(exception, fetchMore);
                    }

            });
        } else {
            handleAccommodationFacilitiesFailure(new NoInternetConnectionException(), fetchMore);
        }
    }

}