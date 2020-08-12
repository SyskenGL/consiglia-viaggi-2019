package it.isw.cvmobile.presenters.fragments;

import android.app.Activity;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.fragments.ResearchResultsFiltersFragment;


@Completed
public class ResearchResultsFiltersPresenter {

    public static final int RESEARCH_RESULTS_FILTERS_REQUEST_CODE = 10;

    private final ResearchResultsFiltersFragment researchResultsFiltersFragment;
    private Map<String, String> temporaryFilters;
    private Map<String, String> temporarySortingKeys;
    private Map<String, String> effectiveFilters;
    private Map<String, String> effectiveSortingKeys;



    public ResearchResultsFiltersPresenter(ResearchResultsFiltersFragment researchResultsFiltersFragment,
                                           Map<String, String> effectiveFilters,
                                           Map<String, String> effectiveSortingKeys) {
        this.researchResultsFiltersFragment = researchResultsFiltersFragment;
        this.effectiveFilters = effectiveFilters;
        this.effectiveSortingKeys = effectiveSortingKeys;
        temporaryFilters = new HashMap<>();
        temporarySortingKeys = new HashMap<>();
        temporaryFilters.putAll(effectiveFilters);
        temporarySortingKeys.putAll(effectiveSortingKeys);
        initializeMaterialSpinners();
        showFilters();
        showSortingKeys();
    }

    private void showFilters() {
        showRatingFilter();
    }

    private void showRatingFilter() {
        String rating = effectiveFilters.get("rating");
        if(rating != null) {
            if(rating.contains("5;")) researchResultsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(true, false);
            if(rating.contains("4;")) researchResultsFiltersFragment.setSmoothCheckBoxFourStarsChecked(true, false);
            if(rating.contains("3;")) researchResultsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(true, false);
            if(rating.contains("2;")) researchResultsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(true, false);
            if(rating.contains("1;")) researchResultsFiltersFragment.setSmoothCheckBoxOneStarChecked(true, false);
        } else {
            researchResultsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(false, false);
            researchResultsFiltersFragment.setSmoothCheckBoxFourStarsChecked(false, false);
            researchResultsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(false, false);
            researchResultsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(false, false);
            researchResultsFiltersFragment.setSmoothCheckBoxOneStarChecked(false, false);
        }
    }

    private void showSortingKeys() {
        showRatingSortingKey();
        showFavoriteSortingKey();
        showViewSortingKey();
    }

    private void showRatingSortingKey() {
        String ratingSortingKey = effectiveSortingKeys.get("rating");
        if(ratingSortingKey != null) {
            if(ratingSortingKey.equals("ASC")){
                researchResultsFiltersFragment.setSelectedMaterialSpinnerRating(1);
            } else{
                researchResultsFiltersFragment.setSelectedMaterialSpinnerRating(2);
            }
        } else {
            researchResultsFiltersFragment.setSelectedMaterialSpinnerRating(0);
        }
    }

    private void showFavoriteSortingKey() {
        String FavoriteSortingKey = effectiveSortingKeys.get("total_favorites");
        if(FavoriteSortingKey != null){
            if(FavoriteSortingKey.equals("ASC")){
                researchResultsFiltersFragment.setSelectedMaterialSpinnerFavorites(1);
            } else {
                researchResultsFiltersFragment.setSelectedMaterialSpinnerFavorites(2);
            }
        } else {
            researchResultsFiltersFragment.setSelectedMaterialSpinnerFavorites(0);
        }
    }

    private void showViewSortingKey() {
        String ViewSortingKey = effectiveSortingKeys.get("total_views");
        if(ViewSortingKey != null) {
            if(ViewSortingKey.equals("ASC")){
                researchResultsFiltersFragment.setSelectedMaterialSpinnerViews(1);
            } else{
                researchResultsFiltersFragment.setSelectedMaterialSpinnerViews(2);
            }
        } else {
            researchResultsFiltersFragment.setSelectedMaterialSpinnerViews(0);
        }
    }

    private void initializeMaterialSpinners() {
        List<String> sortingKeysValues = new ArrayList<>();
        sortingKeysValues.add("NONE");
        sortingKeysValues.add("ASC");
        sortingKeysValues.add("DESC");
        researchResultsFiltersFragment.setMaterialSpinnerFavoritesItems(sortingKeysValues);
        researchResultsFiltersFragment.setMaterialSpinnerRatingItems(sortingKeysValues);
        researchResultsFiltersFragment.setMaterialSpinnerViewsItems(sortingKeysValues);
    }

    public void onButtonApplyClicked() {
        Intent intent = new Intent();
        effectiveFilters.clear();
        effectiveFilters.putAll(temporaryFilters);
        effectiveSortingKeys.clear();
        effectiveSortingKeys.putAll(temporarySortingKeys);
        intent.putExtra("filters", (HashMap) effectiveFilters);
        intent.putExtra("sorting_keys", (HashMap) effectiveSortingKeys);
        researchResultsFiltersFragment.setTargetFragment(
                researchResultsFiltersFragment.getResearchResultsFragment(),
                RESEARCH_RESULTS_FILTERS_REQUEST_CODE
        );
        if (researchResultsFiltersFragment.getTargetFragment() != null) {
            researchResultsFiltersFragment.getTargetFragment().onActivityResult(
                    researchResultsFiltersFragment.getTargetRequestCode(),
                    Activity.RESULT_OK,
                    intent
            );
        }
        researchResultsFiltersFragment.dismiss();
    }

    public void onButtonResetClicked() {
        effectiveFilters = new HashMap<>();
        effectiveSortingKeys = new HashMap<>();
        if(User.isLoggedIn()) {
            temporaryFilters.put("user_id", User.getInstance().getUserId());
        }
        initializeMaterialSpinners();
        showFilters();
        showSortingKeys();
    }

    public void onCheckBoxFiveStarsClicked() {
        if(researchResultsFiltersFragment.isSmoothCheckBoxFiveStarsChecked()) {
            researchResultsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(false, true);
            setRatingFilter("5", true);
        } else {
            researchResultsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(true, true);
            setRatingFilter("5", false);
        }
    }

    public void onCheckBoxFourStarsClicked() {
        if(researchResultsFiltersFragment.isSmoothCheckBoxFourStarsChecked()) {
            researchResultsFiltersFragment.setSmoothCheckBoxFourStarsChecked(false, true);
            setRatingFilter("4", true);
        } else {
            researchResultsFiltersFragment.setSmoothCheckBoxFourStarsChecked(true, true);
            setRatingFilter("4", false);
        }
    }

    public void onCheckBoxThreeStarsClicked() {
        if(researchResultsFiltersFragment.isSmoothCheckBoxThreeStarsChecked()) {
            researchResultsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(false, true);
            setRatingFilter("3", true);
        } else {
            researchResultsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(true, true);
            setRatingFilter("3", false);
        }
    }

    public void onCheckBoxTwoStarsClicked() {
        if(researchResultsFiltersFragment.isSmoothCheckBoxTwoStarsChecked()) {
            researchResultsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(false, true);
            setRatingFilter("2", true);
        } else {
            researchResultsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(true, true);
            setRatingFilter("2", false);
        }
    }

    public void onCheckBoxOneStarClicked() {
        if(researchResultsFiltersFragment.isSmoothCheckBoxOneStarChecked()) {
            researchResultsFiltersFragment.setSmoothCheckBoxOneStarChecked(false, true);
            setRatingFilter("1", true);
        } else {
            researchResultsFiltersFragment.setSmoothCheckBoxOneStarChecked(true, true);
            setRatingFilter("1", false);
        }
    }

    public void onMaterialSpinnerRatingItemSelected(@NotNull Object itemSelected) {
        if(itemSelected.equals("NONE")) {
            temporarySortingKeys.remove("rating");
        }else if(itemSelected.equals("ASC")){
            temporarySortingKeys.put("rating", "ASC");
        }else{
            temporarySortingKeys.put("rating", "DESC");
        }
    }

    public void onMaterialSpinnerFavoritesSelected(@NotNull Object itemSelected) {
        if(itemSelected.equals("NONE")){
            temporarySortingKeys.remove("total_favorites");
        }else if(itemSelected.equals("ASC")){
            temporarySortingKeys.put("total_favorites", "ASC");
        }else{
            temporarySortingKeys.put("total_favorites", "DESC");
        }
    }

    public void onMaterialSpinnerViewsSelected(@NotNull Object itemSelected) {
        if(itemSelected.equals("NONE")){
            temporarySortingKeys.remove("total_views");
        } else if(itemSelected.equals("ASC")){
            temporarySortingKeys.put("total_views", "ASC");
        } else{
            temporarySortingKeys.put("total_views", "DESC");
        }
    }

    private void setRatingFilter(String value, boolean isChecked) {
        String rating = temporaryFilters.get("rating");
        value += ";";
        if(isChecked){
            if(rating != null) {
                rating = rating.replace(value, "");
                if(rating.equals("")){
                    temporaryFilters.remove("rating");
                } else {
                    temporaryFilters.put("rating", rating);
                }
            }
        } else {
            if(rating != null){
                rating += value;
                temporaryFilters.put("rating", rating);
            } else{
                temporaryFilters.put("rating", value);
            }
        }
    }

}