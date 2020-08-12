package it.isw.cvmobile.presenters.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
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
import it.isw.cvmobile.views.fragments.FavoritesFragment;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.enumerations.AccommodationCardAdapterMode;
import it.isw.cvmobile.widgets.scrollview.cards.listeners.OnItemRemovedListener;
import it.isw.cvmobile.widgets.slider.listeners.OnImageChangedListener;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class FavoritesPresenter {

    private final static int LIMIT = 5;

    private final FavoritesFragment favoritesFragment;
    private final AccommodationCardAdapter accommodationCardAdapter;
    private final List<AccommodationFacility> accommodationFacilities;
    private boolean favoritesFinished;
    private int lastPosition;
    private int offset;
    private Synchronizer synchronizer;



    public FavoritesPresenter(@NotNull FavoritesFragment favoritesFragment) {
        this.favoritesFragment = favoritesFragment;
        accommodationFacilities = new ArrayList<>();
        accommodationCardAdapter = new AccommodationCardAdapter(
                favoritesFragment.getNavigationActivity(),
                accommodationFacilities,
                AccommodationCardAdapterMode.HEAVY_ADAPTER_FAVORITES
        );
        favoritesFragment.setFavoritesRecyclerViewAdapter(accommodationCardAdapter);
        synchronizer = new Synchronizer(true);
        setOnFavoriteRemovedListener();
        setOnImageChangedListener();
        fetchFavorites(false);
    }

    private void setOnFavoriteRemovedListener() {
        accommodationCardAdapter.setItemRemovedListener(new OnItemRemovedListener() {
            @Override
            public void onItemRemoved() {
                if(accommodationFacilities.size() == 0) {
                    offset = 0;
                    favoritesFragment.setLoadingSpinnerInitialResultsVisibility(true);
                    fetchFavorites(false);
                } else {
                    setBlurryBackground(
                            accommodationFacilities.get(favoritesFragment.getCurrentCompletelyVisibleFavoriteItem())
                                .getImages()[0]
                    );
                }
            }
        });
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
        Glide.with(favoritesFragment.getNavigationActivity())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(url)
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        favoritesFragment.setBlurryBackground(
                                resource,
                                8,
                                8,
                                Color.argb(0, 0, 0, 0)
                        );
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}

                });
    }

    public void onFavoritesRecyclerViewScrolled(boolean canScrollHorizontally,
                                                int currentPosition) {
        if(currentPosition >= 0 && currentPosition != lastPosition) {
            lastPosition = currentPosition;
            setBlurryBackground(accommodationFacilities.get(lastPosition).getImages()[0]);
        }
        if(!favoritesFinished && !canScrollHorizontally) {
            fetchFavorites(true);
        }
    }

    public void onButtonRefreshClicked() {
        favoritesFragment.startButtonRefreshSpinAnimation();
        fetchFavorites(false);
    }

    private void handleFetchFavoritesSuccess(@NotNull JsonObject results,
                                             AccommodationFacilityDAO accommodationFacilityDAO,
                                             final boolean fetchMore) {
        favoritesFragment.clearButtonRefreshAnimation();
        favoritesFragment.setErrorViewVisibility(false);
        int retrieved = results.getAsJsonObject("data").get("retrieved").getAsInt();
        offset += retrieved;
        if(retrieved != 0) {
            accommodationFacilities.addAll(accommodationFacilityDAO.parseResults(results));
            accommodationCardAdapter.notifyDataSetChanged();
            if(retrieved < LIMIT) {
                favoritesFinished = true;
            }
            if(!fetchMore) {
                String[] backgroundAvailable = accommodationFacilities.get(0).getImages();
                if(backgroundAvailable != null && backgroundAvailable.length > 0) {
                    setBlurryBackground(backgroundAvailable[0]);
                }
            }
        } else {
            favoritesFinished = true;
            if(!fetchMore) {
                favoritesFragment.setNoResultsViewVisibility(true);
            }
        }
        if(!fetchMore) {
            favoritesFragment.setLoadingSpinnerInitialResultsVisibility(false);
        } else {
            favoritesFragment.setLoadingSpinnerMoreResultsVisibility(false);
        }
        favoritesFragment.unmaskFavoritesRecyclerViewItemTouch();
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    private void handleFetchFavoritesFailure(@NotNull Exception exception, final boolean fetchMore) {
        favoritesFragment.clearButtonRefreshAnimation();
        if(exception.getClass() == NoInternetConnectionException.class) {
            if(fetchMore) {
                MotionToast.display(
                        favoritesFragment.getNavigationActivity(),
                        R.string.toast_warning_internet_connection,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            }
        } else {
            if(fetchMore) {
                MotionToast.display(
                        favoritesFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
        }
        if(!fetchMore) {
            favoritesFragment.setLoadingSpinnerInitialResultsVisibility(false);
            favoritesFragment.setErrorViewVisibility(true);
            favoritesFragment.maskFavoritesRecyclerViewItemTouch();
        } else {
            favoritesFragment.setLoadingSpinnerMoreResultsVisibility(false);
            favoritesFragment.unmaskFavoritesRecyclerViewItemTouch();
        }
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    @RequiresInternetConnection
    private void fetchFavorites(final boolean fetchMore) {
        synchronized(synchronizer.getSynchronizer()) {
            if(!synchronizer.isSatisfied())
                return;
            else synchronizer.setSatisfied(false);
        }
        if(FullScreenActivity.isNetworkAvailable(favoritesFragment.getNavigationActivity())) {
            favoritesFragment.maskFavoritesRecyclerViewItemTouch();
            if(fetchMore) {
                favoritesFragment.setLoadingSpinnerMoreResultsVisibility(true);
            }
            final AccommodationFacilityDAO accommodationFacilityDAO =
                    DAOFactory.getAccommodationFacilityDAO(favoritesFragment.getNavigationActivity());
            accommodationFacilityDAO.getFavorites(User.getInstance().getUserId(), offset, LIMIT, new LambdaResultsHandler() {

                @Override
                public void onSuccess(JsonObject results) {
                    handleFetchFavoritesSuccess(results, accommodationFacilityDAO, fetchMore);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleFetchFavoritesFailure(exception, fetchMore);
                }

            });
        } else {
            handleFetchFavoritesFailure(new NoInternetConnectionException(), fetchMore);
        }
    }

}
