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
import it.isw.cvmobile.views.fragments.HistoryFragment;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.enumerations.AccommodationCardAdapterMode;
import it.isw.cvmobile.widgets.slider.listeners.OnImageChangedListener;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class HistoryPresenter {

    private final static int LIMIT = 5;

    private final HistoryFragment historyFragment;
    private final AccommodationCardAdapter accommodationCardAdapter;
    private final List<AccommodationFacility> accommodationFacilities;
    private boolean historyFinished;
    private int lastCardViewPosition;
    private int historyOffset;
    private Synchronizer synchronizer;



    public HistoryPresenter(@NotNull HistoryFragment historyFragment) {
        this.historyFragment = historyFragment;
        accommodationFacilities = new ArrayList<>();
        accommodationCardAdapter = new AccommodationCardAdapter(
                historyFragment.getNavigationActivity(),
                accommodationFacilities,
                AccommodationCardAdapterMode.HEAVY_ADAPTER
        );
        historyFragment.setHistoryRecyclerViewAdapter(accommodationCardAdapter);
        synchronizer = new Synchronizer(true);
        setOnImageChangedListener();
        fetchHistory(false);
    }

    public void onHistoryRecyclerViewScrolled(boolean canScrollHorizontally,
                                              int currentCardViewPosition) {
        if(currentCardViewPosition >= 0 && currentCardViewPosition != lastCardViewPosition) {
            lastCardViewPosition = currentCardViewPosition;
            setBlurryBackground(accommodationFacilities.get(lastCardViewPosition).getImages()[0]);
        }
        if(!historyFinished && !canScrollHorizontally) {
            fetchHistory(true);
        }
    }

    public void onButtonRefreshClicked() {
        historyFragment.startButtonRefreshSpinAnimation();
        fetchHistory(false);
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
        Glide.with(historyFragment.getNavigationActivity())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(url)
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        historyFragment.setBlurryBackground(
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

    private void handleFetchHistorySuccess(@NotNull JsonObject results,
                                           AccommodationFacilityDAO accommodationFacilityDAO,
                                           final boolean fetchMore) {
        int retrieved = results.getAsJsonObject("data").get("retrieved").getAsInt();
        historyOffset += retrieved;
        historyFragment.clearButtonRefreshAnimation();
        historyFragment.setErrorViewVisibility(false);
        if(retrieved != 0) {
            accommodationFacilities.addAll(accommodationFacilityDAO.parseResults(results));
            accommodationCardAdapter.notifyDataSetChanged();
            if(retrieved < LIMIT) {
                historyFinished = true;
            }
            if(!fetchMore) {
                String[] backgroundAvailable = accommodationFacilities.get(0).getImages();
                if(backgroundAvailable != null && backgroundAvailable.length > 0) {
                    setBlurryBackground(backgroundAvailable[0]);
                }
            }
        } else {
            historyFinished = true;
            if(!fetchMore) {
                historyFragment.setNoResultsViewVisibility(true);
            }
        }
        if(!fetchMore) {
            historyFragment.setLoadingSpinnerInitialResultsVisibility(false);
        } else {
            historyFragment.setLoadingSpinnerMoreResultsVisibility(false);
        }
        historyFragment.unmaskHistoryRecyclerViewItemTouch();
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    private void handleFetchHistoryFailure(@NotNull Exception exception, final boolean fetchMore) {
        historyFragment.clearButtonRefreshAnimation();
        if(exception.getClass() == NoInternetConnectionException.class) {
            if(fetchMore) {
                MotionToast.display(
                        historyFragment.getNavigationActivity(),
                        R.string.toast_warning_internet_connection,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            }
        } else {
            if(fetchMore) {
                MotionToast.display(
                        historyFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
        }
        if(!fetchMore) {
            historyFragment.setLoadingSpinnerInitialResultsVisibility(false);
            historyFragment.setErrorViewVisibility(true);
            historyFragment.maskHistoryRecyclerViewItemTouch();
        } else {
            historyFragment.setLoadingSpinnerMoreResultsVisibility(false);
            historyFragment.unmaskHistoryRecyclerViewItemTouch();
        }
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    @RequiresInternetConnection
    private void fetchHistory(final boolean fetchMore) {
        synchronized(synchronizer.getSynchronizer()) {
            if(!synchronizer.isSatisfied())
                return;
            else synchronizer.setSatisfied(false);
        }
        if(FullScreenActivity.isNetworkAvailable(historyFragment.getNavigationActivity())) {
            if(fetchMore) {
                historyFragment.setLoadingSpinnerMoreResultsVisibility(true);
            }
            historyFragment.maskHistoryRecyclerViewItemTouch();
            final AccommodationFacilityDAO accommodationFacilityDAO =
                    DAOFactory.getAccommodationFacilityDAO(historyFragment.getNavigationActivity());
            accommodationFacilityDAO.getHistory(
                    User.getInstance().getUserId(),
                    historyOffset,
                    LIMIT,
                    new LambdaResultsHandler() {

                @Override
                public void onSuccess(JsonObject results) {
                    handleFetchHistorySuccess(results, accommodationFacilityDAO, fetchMore);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleFetchHistoryFailure(exception, fetchMore);
                }

            });
        } else {
            handleFetchHistoryFailure(new NoInternetConnectionException(), fetchMore);
        }
    }

}
