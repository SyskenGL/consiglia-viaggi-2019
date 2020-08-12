package it.isw.cvmobile.views.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ReviewsPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.widgets.scrollview.cards.review.ReviewCardAdapter;
import jp.wasabeef.blurry.Blurry;


@Completed
public class ReviewsFragment extends Fragment {

    private final NavigationActivity navigationActivity;
    private ReviewsPresenter reviewsPresenter;
    private RecyclerView reviewsRecyclerView;
    private ImageView buttonRefresh;
    private ImageView background;
    private View noResultsView;
    private View errorView;
    private AVLoadingIndicatorView loadingSpinnerInitialResults;
    private AVLoadingIndicatorView loadingSpinnerMoreResults;



    public ReviewsFragment (NavigationActivity navigationActivity) {
        this.navigationActivity = navigationActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingSpinnerInitialResults = view.findViewById(R.id.fragment_reviews_loading_spinner_initial_results);
        loadingSpinnerMoreResults = view.findViewById(R.id.fragment_reviews_loading_spinner_more_results);
        reviewsRecyclerView = view.findViewById(R.id.fragment_reviews_container_reviews);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        background = view.findViewById(R.id.fragment_reviews_background);
        noResultsView = view.findViewById(R.id.fragment_reviews_no_results);
        errorView = view.findViewById(R.id.fragment_reviews_error);
        buttonRefresh = view.findViewById(R.id.fragment_reviews_refresh_button);
        reviewsPresenter = new ReviewsPresenter(this);
        initializeUserInterface();
        listenToScrollEvents();
        listenToClickEvents();
    }

    private void initializeUserInterface() {
        Bitmap bitmap = BitmapFactory.decodeResource(
                navigationActivity.getResources(),
                R.drawable.background_home_two
        );
        if(bitmap != null) {
            Blurry.Composer composer = Blurry.with(getContext());
            composer.radius(25);
            composer.sampling(25);
            composer.color(Color.argb(70, 255, 255, 255));
            composer.async().from(bitmap).into(background);
        }
    }

    private void listenToScrollEvents() {
        reviewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                reviewsPresenter.onReviewsRecyclerViewScrolled(recyclerView.canScrollVertically(1));
            }
        });
    }

    private void listenToClickEvents() {
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewsPresenter.onButtonRefreshClicked();
            }
        });
    }

    public void maskReviewsRecyclerViewItemTouch() {
        reviewsRecyclerView.addOnItemTouchListener(FullScreenActivity.ITEM_TOUCH_MASKER);
    }

    public void unmaskReviewsRecyclerViewItemTouch() {
        reviewsRecyclerView.removeOnItemTouchListener(FullScreenActivity.ITEM_TOUCH_MASKER);
    }

    public void startButtonRefreshSpinAnimation() {
        RotateAnimation rotate = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );
        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        buttonRefresh.startAnimation(rotate);
    }

    public void clearButtonRefreshAnimation() {
        buttonRefresh.clearAnimation();
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public void setReviewsRecyclerViewAdapter(ReviewCardAdapter reviewCardAdapter) {
        reviewsRecyclerView.setAdapter(reviewCardAdapter);
    }

    public void setLoadingSpinnerInitialResultsVisibility(boolean visibility) {
        if(visibility) {
            loadingSpinnerInitialResults.setVisibility(View.VISIBLE);
        } else {
            loadingSpinnerInitialResults.setVisibility(View.GONE);
        }
    }

    public void setNoResultsViewVisibility(boolean visibility) {
        if(visibility) {
            noResultsView.setVisibility(View.VISIBLE);
        } else {
            noResultsView.setVisibility(View.GONE);
        }
    }

    public void setErrorViewVisibility(boolean visibility) {
        if(visibility) {
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    public void setLoadingSpinnerMoreResultsVisibility(boolean visibility) {
        if(visibility) {
            loadingSpinnerMoreResults.setVisibility(View.VISIBLE);
        } else {
            loadingSpinnerMoreResults.setVisibility(View.GONE);
        }
    }

}















