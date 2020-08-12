package it.isw.cvmobile.views.fragments;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ResearchResultsPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import jp.wasabeef.blurry.Blurry;


@Completed
public class ResearchResultsFragment extends Fragment {

    private final NavigationActivity navigationActivity;
    private ResearchResultsPresenter researchResultsPresenter;
    private RecyclerView accommodationFacilitiesRecyclerView;
    private String keywords;
    private Map<String, String> filters;
    private LinearLayoutManager accommodationFacilitiesRecyclerViewLayoutManager;
    private ImageView background;
    private ImageView buttonBack;
    private ImageView buttonRefresh;
    private FloatingActionButton buttonFilters;
    private View noResultsView;
    private View errorView;
    private AVLoadingIndicatorView loadingSpinnerInitialResults;
    private AVLoadingIndicatorView loadingSpinnerMoreResults;



    public ResearchResultsFragment(NavigationActivity navigationActivity, String keywords) {
        this.navigationActivity = navigationActivity;
        this.keywords = keywords;
    }

    public ResearchResultsFragment(NavigationActivity navigationActivity, Map<String, String> filters) {
        this.navigationActivity = navigationActivity;
        this.filters = filters;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_research_results, container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingSpinnerMoreResults = view.findViewById(R.id.fragment_research_results_loading_spinner_more_results);
        loadingSpinnerInitialResults = view.findViewById(R.id.fragment_research_results_loading_spinner_initial_results);
        noResultsView = view.findViewById(R.id.fragment_research_results_no_results);
        errorView = view.findViewById(R.id.fragment_research_results_error);
        accommodationFacilitiesRecyclerView = view.findViewById(R.id.fragment_research_results_horizontal_recycler_view);
        buttonBack = view.findViewById(R.id.fragment_research_results_button_back);
        buttonRefresh = view.findViewById(R.id.fragment_research_results_refresh_button);
        background = view.findViewById(R.id.fragment_research_results_background);
        buttonFilters = view.findViewById(R.id.fragment_research_results_button_filters);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(accommodationFacilitiesRecyclerView);
        accommodationFacilitiesRecyclerView.setLayoutManager(
                accommodationFacilitiesRecyclerViewLayoutManager = new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
        if(keywords != null) {
            researchResultsPresenter = new ResearchResultsPresenter(this, keywords);
        } else {
            researchResultsPresenter = new ResearchResultsPresenter(this, filters);
        }
        initializeUserInterface();
        listenToScrollEvents();
        listenToClickEvents();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        researchResultsPresenter.onActivityResult(requestCode, resultCode, data);
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
        accommodationFacilitiesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                researchResultsPresenter.onFavoritesRecyclerViewScrolled(
                        recyclerView.canScrollHorizontally(1),
                        accommodationFacilitiesRecyclerViewLayoutManager.findFirstCompletelyVisibleItemPosition()
                );
            }
        });
    }

    private void listenToClickEvents() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                researchResultsPresenter.onButtonBackClicked();
            }
        });
        buttonFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                researchResultsPresenter.onButtonFiltersClicked();
            }
        });
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                researchResultsPresenter.onButtonRefreshClicked();
            }
        });
    }

    public void maskAccommodationFacilitiesRecyclerViewItemTouch() {
        accommodationFacilitiesRecyclerView.addOnItemTouchListener(FullScreenActivity.ITEM_TOUCH_MASKER);
    }

    public void unmaskAccommodationFacilitiesViewItemTouch() {
        accommodationFacilitiesRecyclerView.removeOnItemTouchListener(FullScreenActivity.ITEM_TOUCH_MASKER);
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

    public void setBlurryBackground(Bitmap bitmap, int radius, int sampling, int color) {
        Blurry.Composer composer = Blurry.with(navigationActivity);
        composer.radius(radius);
        composer.sampling(sampling);
        composer.color(color);
        composer.async().from(bitmap).into(background);
    }

    public void setAccommodationFacilitiesRecyclerViewAdapter(AccommodationCardAdapter accommodationCardAdapter) {
        accommodationFacilitiesRecyclerView.setAdapter(accommodationCardAdapter);
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

    public void setButtonFiltersVisibility(boolean visibility) {
        if(visibility) {
            buttonFilters.setVisibility(View.VISIBLE);
        } else {
            buttonFilters.setVisibility(View.GONE);
        }
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
