package it.isw.cvmobile.views.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import cn.refactor.library.SmoothCheckBox;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ResearchResultsFiltersPresenter;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class ResearchResultsFiltersFragment extends DialogFragment {

    private ResearchResultsFiltersPresenter researchResultsFiltersPresenter;
    private ResearchResultsFragment researchResultsFragment;
    private Map<String, String> filters;
    private Map<String, String> sortingKeys;
    private SmoothCheckBox smoothCheckBoxFiveStars;
    private SmoothCheckBox smoothCheckBoxFourStars;
    private SmoothCheckBox smoothCheckBoxThreeStars;
    private SmoothCheckBox smoothCheckBoxTwoStars;
    private SmoothCheckBox smoothCheckBoxOneStar;
    private Button buttonApply;
    private Button buttonReset;
    private MaterialSpinner materialSpinnerRating;
    private MaterialSpinner materialSpinnerFavorites;
    private MaterialSpinner materialSpinnerViews;



    public ResearchResultsFiltersFragment(Map<String,String> filters,
                                          Map<String,String> sortingKeys ,
                                          ResearchResultsFragment researchResultsFragment) {
        this.filters = filters;
        this.sortingKeys = sortingKeys;
        this.researchResultsFragment = researchResultsFragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_research_results_filters, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        smoothCheckBoxFiveStars = view.findViewById(R.id.fragment_research_results_filters_checkbox_five_stars);
        smoothCheckBoxFourStars = view.findViewById(R.id.fragment_research_results_filters_checkbox_four_stars);
        smoothCheckBoxThreeStars = view.findViewById(R.id.fragment_research_results_filters_checkbox_three_stars);
        smoothCheckBoxTwoStars = view.findViewById(R.id.fragment_research_results_filters_checkbox_two_stars);
        smoothCheckBoxOneStar = view.findViewById(R.id.fragment_research_results_filters_checkbox_one_star);
        buttonApply = view.findViewById(R.id.fragment_research_results_filters_button_apply);
        buttonReset = view.findViewById(R.id.fragment_research_results_filters_button_reset);
        materialSpinnerRating = view.findViewById(R.id.fragment_research_results_filters_rating_sort);
        materialSpinnerFavorites = view.findViewById(R.id.fragment_research_results_filters_favorites_sort);
        materialSpinnerViews = view.findViewById(R.id.fragment_research_results_filters_views_sort);
        researchResultsFiltersPresenter = new ResearchResultsFiltersPresenter(
                this,
                filters,
                sortingKeys
        );
        initializeUserInterface();
        listenToClickEvents();
        listenToItemSelectedEvents();
    }

    public void initializeUserInterface() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if(getDialog() != null) {
            Window window = getDialog().getWindow();
            if(window != null) {
                window.getAttributes().windowAnimations = R.style.DialogFragmentAnimation;
            }
        }
    }

    private void listenToClickEvents() {
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                researchResultsFiltersPresenter.onButtonApplyClicked();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                researchResultsFiltersPresenter.onButtonResetClicked();
            }
        });
        smoothCheckBoxFiveStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                researchResultsFiltersPresenter.onCheckBoxFiveStarsClicked();
            }
        });
        smoothCheckBoxFourStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                researchResultsFiltersPresenter.onCheckBoxFourStarsClicked();
            }
        });
        smoothCheckBoxThreeStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                researchResultsFiltersPresenter.onCheckBoxThreeStarsClicked();
            }
        });
        smoothCheckBoxTwoStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                researchResultsFiltersPresenter.onCheckBoxTwoStarsClicked();
            }
        });
        smoothCheckBoxOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                researchResultsFiltersPresenter.onCheckBoxOneStarClicked();
            }
        });
    }

    private void listenToItemSelectedEvents() {
        materialSpinnerRating.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                researchResultsFiltersPresenter.onMaterialSpinnerRatingItemSelected(item);
            }
        });
        materialSpinnerFavorites.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                researchResultsFiltersPresenter.onMaterialSpinnerFavoritesSelected(item);
            }
        });
        materialSpinnerViews.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                researchResultsFiltersPresenter.onMaterialSpinnerViewsSelected(item);
            }
        });
    }

    public boolean isSmoothCheckBoxFiveStarsChecked() {
        return smoothCheckBoxFiveStars.isChecked();
    }

    public boolean isSmoothCheckBoxFourStarsChecked() {
        return smoothCheckBoxFourStars.isChecked();
    }

    public boolean isSmoothCheckBoxThreeStarsChecked() {
        return smoothCheckBoxThreeStars.isChecked();
    }

    public boolean isSmoothCheckBoxTwoStarsChecked() {
        return smoothCheckBoxTwoStars.isChecked();
    }

    public boolean isSmoothCheckBoxOneStarChecked() {
        return smoothCheckBoxOneStar.isChecked();
    }

    public ResearchResultsFragment getResearchResultsFragment() {
        return researchResultsFragment;
    }

    public void setSmoothCheckBoxFiveStarsChecked(boolean checked, boolean withAnimation) {
        smoothCheckBoxFiveStars.setChecked(checked, withAnimation);
    }

    public void setSmoothCheckBoxFourStarsChecked(boolean checked, boolean withAnimation) {
        smoothCheckBoxFourStars.setChecked(checked, withAnimation);
    }

    public void setSmoothCheckBoxThreeStarsChecked(boolean checked, boolean withAnimation) {
        smoothCheckBoxThreeStars.setChecked(checked, withAnimation);
    }

    public void setSmoothCheckBoxTwoStarsChecked(boolean checked, boolean withAnimation) {
        smoothCheckBoxTwoStars.setChecked(checked, withAnimation);
    }

    public void setSmoothCheckBoxOneStarChecked(boolean checked, boolean withAnimation) {
        smoothCheckBoxOneStar.setChecked(checked, withAnimation);
    }

    public void setMaterialSpinnerRatingItems(List<String> items) {
        materialSpinnerRating.setItems(items);
    }

    public void setMaterialSpinnerFavoritesItems(List<String> items) {
        materialSpinnerFavorites.setItems(items);
    }

    public void setMaterialSpinnerViewsItems(List<String> items) {
        materialSpinnerViews.setItems(items);
    }

    public void setSelectedMaterialSpinnerRating(int position) {
        materialSpinnerRating.setSelectedIndex(position);
    }

    public void setSelectedMaterialSpinnerFavorites(int position) {
        materialSpinnerFavorites.setSelectedIndex(position);
    }

    public void setSelectedMaterialSpinnerViews(int position) {
        materialSpinnerViews.setSelectedIndex(position);
    }

}