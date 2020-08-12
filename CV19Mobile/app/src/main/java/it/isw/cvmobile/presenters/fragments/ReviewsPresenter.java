package it.isw.cvmobile.presenters.fragments;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.ReviewDAO;
import it.isw.cvmobile.models.Review;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.Synchronizer;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ReviewsFragment;
import it.isw.cvmobile.widgets.scrollview.cards.review.ReviewCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.review.enumerations.ReviewCardAdapterMode;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class ReviewsPresenter {

    private final static int LIMIT = 5;

    private final ReviewsFragment reviewsFragment;
    private final ReviewCardAdapter reviewCardAdapter;
    private final List<Review> reviews;
    private final Map<String, String> filters;
    private final Map<String, String> sortingKeys;
    private boolean reviewsFinished;
    private int reviewsOffset;
    private Synchronizer synchronizer;



    public ReviewsPresenter(@NotNull ReviewsFragment reviewsFragment) {
        this.reviewsFragment = reviewsFragment;
        reviews = new ArrayList<>();
        filters = new HashMap<>();
        sortingKeys = new HashMap<>();
        sortingKeys.put("publication_date", "DESC");
        filters.put("user_id", User.getInstance().getUserId());
        filters.put("reviewer_id", User.getInstance().getUserId());
        reviewCardAdapter = new ReviewCardAdapter(
                reviewsFragment.getNavigationActivity(),
                reviews,
                ReviewCardAdapterMode.EXTRA_INFO_CARD
        );
        reviewsFragment.setReviewsRecyclerViewAdapter(reviewCardAdapter);
        synchronizer = new Synchronizer(true);
        fetchReviews(false);
    }

    public void onReviewsRecyclerViewScrolled(boolean canScrollVertically) {
        if(!reviewsFinished && !canScrollVertically) {
            fetchReviews(true);
        }
    }

    public void onButtonRefreshClicked() {
        reviewsFragment.startButtonRefreshSpinAnimation();
        fetchReviews(false);
    }

    private void handleFetchReviewsSuccess(@NotNull JsonObject results,
                                           ReviewDAO reviewDAO,
                                           boolean fetchMore) {
        reviewsFragment.clearButtonRefreshAnimation();
        reviewsFragment.setErrorViewVisibility(false);
        int retrieved = results.getAsJsonObject("data").get("retrieved").getAsInt();
        reviewsOffset += retrieved;
        if(retrieved != 0) {
            reviews.addAll(reviewDAO.parseResults(results));
            reviewCardAdapter.notifyDataSetChanged();
            if(retrieved < LIMIT) {
                reviewsFinished  = true;
            }
        } else {
            reviewsFinished = true;
            if(!fetchMore) {
                reviewsFragment.setNoResultsViewVisibility(true);
            }
        }
        if(!fetchMore) {
            reviewsFragment.setLoadingSpinnerInitialResultsVisibility(false);
        } else {
            reviewsFragment.setLoadingSpinnerMoreResultsVisibility(false);
        }
        reviewsFragment.unmaskReviewsRecyclerViewItemTouch();
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    private void handleFetchReviewsFailure(@NotNull Exception exception, boolean fetchMore) {
        reviewsFragment.clearButtonRefreshAnimation();
        if(exception.getClass() == NoInternetConnectionException.class) {
            if(fetchMore) {
                MotionToast.display(
                        reviewsFragment.getNavigationActivity(),
                        R.string.toast_warning_internet_connection,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            }
        } else {
            if(fetchMore) {
                MotionToast.display(
                        reviewsFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
        }
        if(!fetchMore) {
            reviewsFragment.setLoadingSpinnerInitialResultsVisibility(false);
            reviewsFragment.setErrorViewVisibility(true);
            reviewsFragment.maskReviewsRecyclerViewItemTouch();
        } else {
            reviewsFragment.setLoadingSpinnerMoreResultsVisibility(false);
            reviewsFragment.unmaskReviewsRecyclerViewItemTouch();
        }
        synchronized (synchronizer.getSynchronizer()){
            synchronizer.setSatisfied(true);
        }
    }

    @RequiresInternetConnection
    private void fetchReviews(final boolean fetchMore) {
        synchronized(synchronizer.getSynchronizer()) {
            if(!synchronizer.isSatisfied()) {
                return;
            } else {
                synchronizer.setSatisfied(false);
            }
        }
        if(FullScreenActivity.isNetworkAvailable(reviewsFragment.getNavigationActivity())) {
            if(fetchMore) reviewsFragment.setLoadingSpinnerMoreResultsVisibility(true);
            reviewsFragment.maskReviewsRecyclerViewItemTouch();
            final ReviewDAO reviewDAO = DAOFactory.getReviewDAO(reviewsFragment.getNavigationActivity());
            reviewDAO.getReviews(filters, sortingKeys, reviewsOffset, LIMIT, new LambdaResultsHandler() {

                @Override
                public void onSuccess(JsonObject results) {
                    handleFetchReviewsSuccess(results, reviewDAO, fetchMore);
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
