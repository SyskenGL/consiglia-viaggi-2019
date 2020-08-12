package it.isw.cvmobile.views.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.material.chip.Chip;
import com.jaredrummler.materialspinner.MaterialSpinner;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import cn.refactor.library.SmoothCheckBox;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ReviewsFiltersPresenter;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class ReviewsFiltersFragment extends DialogFragment {

    private final AccommodationFacilityFragment accommodationFacilityFragment;
    private ReviewsFiltersPresenter reviewsFilterPresenter;
    private Map<String, String> filters;
    private Map<String, String> sortingKeys;
    private SmoothCheckBox smoothCheckBoxFiveStars;
    private SmoothCheckBox smoothCheckBoxFourStars;
    private SmoothCheckBox smoothCheckBoxThreeStars;
    private SmoothCheckBox smoothCheckBoxTwoStars;
    private SmoothCheckBox smoothCheckBoxOneStar;
    private RoundCornerProgressBar roundCornerProgressBarFiveStars;
    private RoundCornerProgressBar roundCornerProgressBarFourStars;
    private RoundCornerProgressBar roundCornerProgressBarThreeStars;
    private RoundCornerProgressBar roundCornerProgressBarTwoStars;
    private RoundCornerProgressBar roundCornerProgressBarOneStar;
    private TextView textViewTotalFiveStars;
    private TextView textViewTotalFourStars;
    private TextView textViewTotalThreeStars;
    private TextView textViewTotalTwoStars;
    private TextView textViewTotalOneStar;
    private Button buttonApply;
    private Button buttonReset;
    private Chip chipWinter;
    private Chip chipSummer;
    private Chip chipSpring;
    private Chip chipAutumn;
    private boolean chipWinterChecked;
    private boolean chipSummerChecked;
    private boolean chipSpringChecked;
    private boolean chipAutumnChecked;
    private MaterialSpinner materialSpinnerRating;
    private MaterialSpinner materialSpinnerPublicationDate;
    private MaterialSpinner materialSpinnerAppreciation;



    public ReviewsFiltersFragment(Map<String,String> filters,
                                  Map<String,String> sortingKeys ,
                                  AccommodationFacilityFragment accommodationFacilityFragment) {
        this.filters = filters;
        this.sortingKeys = sortingKeys;
        this.accommodationFacilityFragment = accommodationFacilityFragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviews_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        smoothCheckBoxFiveStars = view.findViewById(R.id.fragment_reviews_filters_checkbox_five_stars);
        smoothCheckBoxFourStars = view.findViewById(R.id.fragment_reviews_filters_checkbox_four_stars);
        smoothCheckBoxThreeStars = view.findViewById(R.id.fragment_reviews_filters_checkbox_three_stars);
        smoothCheckBoxTwoStars = view.findViewById(R.id.fragment_reviews_filters_checkbox_two_stars);
        smoothCheckBoxOneStar = view.findViewById(R.id.fragment_reviews_filters_checkbox_one_star);
        buttonApply = view.findViewById(R.id.fragment_reviews_filters_button_apply);
        buttonReset = view.findViewById(R.id.fragment_reviews_filters_button_reset);
        chipWinter = view.findViewById(R.id.fragment_reviews_filters_winter_filter);
        chipSummer = view.findViewById(R.id.fragment_reviews_filters_summer_filter);
        chipSpring = view.findViewById(R.id.fragment_reviews_filters_spring_filter);
        chipAutumn = view.findViewById(R.id.fragment_reviews_filters_autumn_filter);
        roundCornerProgressBarFiveStars = view.findViewById(R.id.fragment_reviews_filters_total_bar_five_stars);
        roundCornerProgressBarFourStars = view.findViewById(R.id.fragment_reviews_filters_total_bar_four_stars);
        roundCornerProgressBarThreeStars = view.findViewById(R.id.fragment_reviews_filters_total_bar_three_stars);
        roundCornerProgressBarTwoStars = view.findViewById(R.id.fragment_reviews_filters_total_bar_two_stars);
        roundCornerProgressBarOneStar = view.findViewById(R.id.fragment_reviews_filters_total_bar_one_star);
        textViewTotalFiveStars = view.findViewById(R.id.fragment_reviews_filters_amount_five_stars);
        textViewTotalFourStars = view.findViewById(R.id.fragment_reviews_filters_amount_four_stars);
        textViewTotalThreeStars = view.findViewById(R.id.fragment_reviews_filters_amount_three_stars);
        textViewTotalTwoStars = view.findViewById(R.id.fragment_reviews_filters_amount_two_stars);
        textViewTotalOneStar = view.findViewById(R.id.fragment_reviews_filters_amount_one_star);
        materialSpinnerRating = view.findViewById(R.id.fragment_reviews_filters_rating_sort);
        materialSpinnerPublicationDate = view.findViewById(R.id.fragment_reviews_filters_publication_date_sort);
        materialSpinnerAppreciation = view.findViewById(R.id.fragment_reviews_filters_appreciation_sort);
        reviewsFilterPresenter = new ReviewsFiltersPresenter(
                this,
                accommodationFacilityFragment.getAccommodationFacility(),
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
                reviewsFilterPresenter.onButtonApplyClicked();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onButtonResetClicked();
            }
        });
        smoothCheckBoxFiveStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onCheckBoxFiveStarsClicked();
            }
        });
        smoothCheckBoxFourStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onCheckBoxFourStarsClicked();
            }
        });
        smoothCheckBoxThreeStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onCheckBoxThreeStarsClicked();
            }
        });
        smoothCheckBoxTwoStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onCheckBoxTwoStarsClicked();
            }
        });
        smoothCheckBoxOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onCheckBoxOneStarClicked();
            }
        });
        chipSummer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onChipSummerClicked();
            }
        });
        chipWinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onChipWinterClicked();
            }
        });
        chipAutumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onChipAutumnClicked();
            }
        });
        chipSpring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsFilterPresenter.onChipSpringClicked();
            }
        });
    }

    private void listenToItemSelectedEvents() {
        materialSpinnerRating.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                reviewsFilterPresenter.onMaterialSpinnerRatingItemSelected(item);
            }
        });
        materialSpinnerPublicationDate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                reviewsFilterPresenter.onMaterialSpinnerPublicationDateSelected(item);
            }
        });
        materialSpinnerAppreciation.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                reviewsFilterPresenter.onMaterialSpinnerAppreciationSelected(item);
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

    public boolean isChipWinterChecked() {
        return chipWinterChecked;
    }

    public boolean isChipSummerChecked() {
        return chipSummerChecked;
    }

    public boolean isChipSpringChecked() {
        return chipSpringChecked;
    }

    public boolean isChipAutumnChecked() {
        return chipAutumnChecked;
    }

    public AccommodationFacilityFragment getAccommodationFacilityFragment() {
        return accommodationFacilityFragment;
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

    public void setChipWinterBackgroundColor(int colorId) {
        chipWinter.setChipBackgroundColorResource(colorId);
    }

    public void setChipAutumnBackgroundColor(int colorId) {
        chipAutumn.setChipBackgroundColorResource(colorId);
    }

    public void setChipSpringBackgroundColor(int colorId) {
        chipSpring.setChipBackgroundColorResource(colorId);
    }

    public void setChipSummerBackgroundColor(int colorId) {
        chipSummer.setChipBackgroundColorResource(colorId);
    }

    public void setChipWinterChecked(boolean chipWinterChecked) {
        this.chipWinterChecked = chipWinterChecked;
    }

    public void setChipSummerChecked(boolean chipSummerChecked) {
        this.chipSummerChecked = chipSummerChecked;
    }

    public void setChipSpringChecked(boolean chipSpringChecked) {
        this.chipSpringChecked = chipSpringChecked;
    }

    public void setChipAutumnChecked(boolean chipAutumnChecked) {
        this.chipAutumnChecked = chipAutumnChecked;
    }

    public void setRoundCornerProgressBarFiveStarsProgress(int progress) {
        roundCornerProgressBarFiveStars.setProgress(progress);
    }

    public void setRoundCornerProgressBarFourStarsProgress(int progress) {
        roundCornerProgressBarFourStars.setProgress(progress);
    }

    public void setRoundCornerProgressBarThreeStarsProgress(int progress) {
        roundCornerProgressBarThreeStars.setProgress(progress);
    }

    public void setRoundCornerProgressBarTwoStarsProgress(int progress) {
        roundCornerProgressBarTwoStars.setProgress(progress);
    }

    public void setRoundCornerProgressBarOneStarProgress(int progress) {
        roundCornerProgressBarOneStar.setProgress(progress);
    }

    public void setMaterialSpinnerRatingItems(List<String> items) {
        materialSpinnerRating.setItems(items);
    }

    public void setMaterialSpinnerPublicationDateItems(List<String> items) {
        materialSpinnerPublicationDate.setItems(items);
    }

    public void setMaterialSpinnerAppreciationItems(List<String> items) {
        materialSpinnerAppreciation.setItems(items);
    }

    public void setTextFiveStars(String totalFiveStars) {
        textViewTotalFiveStars.setText(totalFiveStars);
    }

    public void setTextFourStars(String totalFourStars) {
        textViewTotalFourStars.setText(totalFourStars);
    }

    public void setTextThreeStars(String totalThreeStars) {
        textViewTotalThreeStars.setText(totalThreeStars);
    }

    public void setTextTwoStars(String totalTwoStars) {
        textViewTotalTwoStars.setText(totalTwoStars);
    }

    public void setTextOneStar(String totalOneStar) {
        textViewTotalOneStar.setText(totalOneStar);
    }

    public void setSelectedMaterialSpinnerRating(int position) {
        materialSpinnerRating.setSelectedIndex(position);
    }

    public void setSelectedMaterialSpinnerPublicationDate(int position) {
        materialSpinnerPublicationDate.setSelectedIndex(position);
    }

    public void setSelectedMaterialSpinnerAppreciation(int position) {
        materialSpinnerAppreciation.setSelectedIndex(position);
    }

}