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
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.FavoritesPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import jp.wasabeef.blurry.Blurry;


@Completed
public class FavoritesFragment extends Fragment {

    private final NavigationActivity navigationActivity;
    private FavoritesPresenter favoritesPresenter;
    private RecyclerView favoritesRecyclerView;
    private LinearLayoutManager layoutManager;
    private ImageView background;
    private ImageView buttonRefresh;
    private View errorView;
    private View noResultsView;
    private AVLoadingIndicatorView loadingSpinnerInitialResults;
    private AVLoadingIndicatorView loadingSpinnerMoreResults;



    public FavoritesFragment(NavigationActivity navigationActivity ) {
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
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingSpinnerMoreResults = view.findViewById(R.id.fragment_favorites_loading_spinner_more_results);
        loadingSpinnerInitialResults = view.findViewById(R.id.fragment_favorites_loading_spinner_initial_results);
        favoritesRecyclerView = view.findViewById(R.id.fragment_favorites_horizontal_recycler_view);
        noResultsView = view.findViewById(R.id.fragment_favorites_no_results);
        errorView = view.findViewById(R.id.fragment_favorites_error);
        buttonRefresh = view.findViewById(R.id.fragment_favorites_refresh_button);
        background = view.findViewById(R.id.fragment_favorites_background);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(
                layoutManager = new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
        favoritesPresenter = new FavoritesPresenter(this);
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
        favoritesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                favoritesPresenter.onFavoritesRecyclerViewScrolled(
                        recyclerView.canScrollHorizontally(1),
                        layoutManager.findFirstCompletelyVisibleItemPosition()
                );
            }
        });
    }

    private void listenToClickEvents() {
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesPresenter.onButtonRefreshClicked();
            }
        });
    }

    public void maskFavoritesRecyclerViewItemTouch() {
        favoritesRecyclerView.addOnItemTouchListener(FullScreenActivity.ITEM_TOUCH_MASKER);
    }

    public void unmaskFavoritesRecyclerViewItemTouch() {
        favoritesRecyclerView.removeOnItemTouchListener(FullScreenActivity.ITEM_TOUCH_MASKER);
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

    public int getCurrentCompletelyVisibleFavoriteItem() {
        return layoutManager.findFirstCompletelyVisibleItemPosition();
    }

    public void setBlurryBackground(Bitmap bitmap, int radius, int sampling, int color) {
        Blurry.Composer composer = Blurry.with(navigationActivity);
        composer.radius(radius);
        composer.sampling(sampling);
        composer.color(color);
        composer.async().from(bitmap).into(background);
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

    public void setFavoritesRecyclerViewAdapter(AccommodationCardAdapter accommodationCardAdapter) {
        favoritesRecyclerView.setAdapter(accommodationCardAdapter);
    }

    public void setLoadingSpinnerInitialResultsVisibility(boolean visibility) {
        if(visibility) {
            loadingSpinnerInitialResults.setVisibility(View.VISIBLE);
        } else {
            loadingSpinnerInitialResults.setVisibility(View.GONE);
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
