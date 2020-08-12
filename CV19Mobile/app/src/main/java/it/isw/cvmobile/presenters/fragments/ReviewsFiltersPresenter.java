package it.isw.cvmobile.presenters.fragments;

import android.app.Activity;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.R;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.fragments.ReviewsFiltersFragment;


@Completed
public class ReviewsFiltersPresenter {

    public static final int REVIEWS_FILTERS_REQUEST_CODE = 11;

    private final ReviewsFiltersFragment reviewsFiltersFragment;
    private final AccommodationFacility accommodationFacility;
    private Map<String, String> temporaryFilters;
    private Map<String, String> temporarySortingKeys;
    private Map<String, String> effectiveFilters;
    private Map<String, String> effectiveSortingKeys;



    public ReviewsFiltersPresenter(ReviewsFiltersFragment reviewsFiltersFragment,
                                   AccommodationFacility accommodationFacility,
                                   Map<String, String> effectiveFilters,
                                   Map<String, String> effectiveSortingKeys) {
        this.reviewsFiltersFragment = reviewsFiltersFragment;
        this.accommodationFacility = accommodationFacility;
        this.effectiveFilters = effectiveFilters;
        this.effectiveSortingKeys = effectiveSortingKeys;
        this.temporaryFilters = new HashMap<>();
        this.temporarySortingKeys = new HashMap<>();
        temporaryFilters.putAll(effectiveFilters);
        temporarySortingKeys.putAll(effectiveSortingKeys);
        initializeMaterialSpinners();
        showFilters();
        showSortingKeys();
    }

    private void showFilters() {
        showRatingFilter();
        showSeasonFilter();
        showReviewsStats();
    }

    private void showRatingFilter() {
        String rating = effectiveFilters.get("rating");
        if(rating != null) {
            if(rating.contains("5;")) {
                reviewsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(true, false);
            }
            if(rating.contains("4;")) {
                reviewsFiltersFragment.setSmoothCheckBoxFourStarsChecked(true, false);
            }
            if(rating.contains("3;")) {
                reviewsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(true, false);
            }
            if(rating.contains("2;")) {
                reviewsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(true, false);
            }
            if(rating.contains("1;")) {
                reviewsFiltersFragment.setSmoothCheckBoxOneStarChecked(true, false);
            }
        } else {
            reviewsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(false, false);
            reviewsFiltersFragment.setSmoothCheckBoxFourStarsChecked(false, false);
            reviewsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(false, false);
            reviewsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(false, false);
            reviewsFiltersFragment.setSmoothCheckBoxOneStarChecked(false, false);
        }
    }

    private void showSeasonFilter() {
        String season = effectiveFilters.get("season");
        if(season != null){
            if(season.contains("summer;")) {
                reviewsFiltersFragment.setChipSummerBackgroundColor(R.color.peach);
                reviewsFiltersFragment.setChipSummerChecked(true);
            }
            if(season.contains("spring;")) {
                reviewsFiltersFragment.setChipSpringBackgroundColor(R.color.peach);
                reviewsFiltersFragment.setChipSpringChecked(true);
            }
            if(season.contains("autumn;")) {
                reviewsFiltersFragment.setChipAutumnBackgroundColor(R.color.peach);
                reviewsFiltersFragment.setChipAutumnChecked(true);
            }
            if(season.contains("winter;")) {
                reviewsFiltersFragment.setChipWinterBackgroundColor(R.color.peach);
                reviewsFiltersFragment.setChipWinterChecked(true);
            }
        } else {
            reviewsFiltersFragment.setChipSummerBackgroundColor(R.color.darkBlue);
            reviewsFiltersFragment.setChipSpringBackgroundColor(R.color.darkBlue);
            reviewsFiltersFragment.setChipAutumnBackgroundColor(R.color.darkBlue);
            reviewsFiltersFragment.setChipWinterBackgroundColor(R.color.darkBlue);
        }
    }

    private void showReviewsStats() {
        int totalReview = accommodationFacility.getTotalReviews();
        int totalReviewFiveStars = accommodationFacility.getTotalFiveStarsReviews();
        int totalReviewFourStars = accommodationFacility.getTotalFourStarsReviews();
        int totalReviewThreeStars = accommodationFacility.getTotalThreeStarsReviews();
        int totalReviewTwoStars = accommodationFacility.getTotalTwoStarsReviews();
        int totalReviewOneStar = accommodationFacility.getTotalOneStarReviews();
        reviewsFiltersFragment.setRoundCornerProgressBarFiveStarsProgress(totalReviewFiveStars*100/totalReview);
        reviewsFiltersFragment.setRoundCornerProgressBarFourStarsProgress(totalReviewFourStars*100/totalReview);
        reviewsFiltersFragment.setRoundCornerProgressBarThreeStarsProgress(totalReviewThreeStars*100/totalReview);
        reviewsFiltersFragment.setRoundCornerProgressBarTwoStarsProgress(totalReviewTwoStars*100/totalReview);
        reviewsFiltersFragment.setRoundCornerProgressBarOneStarProgress(totalReviewOneStar*100/totalReview);
        reviewsFiltersFragment.setTextFiveStars(String.valueOf(totalReviewFiveStars));
        reviewsFiltersFragment.setTextFourStars(String.valueOf(totalReviewFourStars));
        reviewsFiltersFragment.setTextThreeStars(String.valueOf(totalReviewThreeStars));
        reviewsFiltersFragment.setTextTwoStars(String.valueOf(totalReviewTwoStars));
        reviewsFiltersFragment.setTextOneStar(String.valueOf(totalReviewOneStar));
    }

    private void showSortingKeys() {
        showAppreciationSortingKey();
        showPublicationDateSortingKey();
        showRatingSortingKey();
    }

    private void showAppreciationSortingKey() {
        String appreciationSortingKey = effectiveSortingKeys.get("total_likes");
        if(appreciationSortingKey != null) {
            if(appreciationSortingKey.equals("ASC")){
                reviewsFiltersFragment.setSelectedMaterialSpinnerAppreciation(1);
            } else{
                reviewsFiltersFragment.setSelectedMaterialSpinnerAppreciation(2);
            }
        } else {
            reviewsFiltersFragment.setSelectedMaterialSpinnerAppreciation(0);
        }
    }

    private void showPublicationDateSortingKey() {
        String publicationDateSortingKey = effectiveSortingKeys.get("publication_date");
        if(publicationDateSortingKey != null){
            if(publicationDateSortingKey.equals("ASC")){
                reviewsFiltersFragment.setSelectedMaterialSpinnerPublicationDate(1);
            } else {
                reviewsFiltersFragment.setSelectedMaterialSpinnerPublicationDate(2);
            }
        } else {
            reviewsFiltersFragment.setSelectedMaterialSpinnerPublicationDate(0);
        }
    }

    private void showRatingSortingKey() {
        String ratingSortingKey = effectiveSortingKeys.get("rating");
        if(ratingSortingKey != null) {
            if(ratingSortingKey.equals("ASC")){
                reviewsFiltersFragment.setSelectedMaterialSpinnerRating(1);
            } else{
                reviewsFiltersFragment.setSelectedMaterialSpinnerRating(2);
            }
        } else {
            reviewsFiltersFragment.setSelectedMaterialSpinnerRating(0);
        }
    }

    private void initializeMaterialSpinners() {
        List<String> sortingKeysValues = new ArrayList<>();
        sortingKeysValues.add("NONE");
        sortingKeysValues.add("ASC");
        sortingKeysValues.add("DESC");
        reviewsFiltersFragment.setMaterialSpinnerPublicationDateItems(sortingKeysValues);
        reviewsFiltersFragment.setMaterialSpinnerRatingItems(sortingKeysValues);
        reviewsFiltersFragment.setMaterialSpinnerAppreciationItems(sortingKeysValues);
    }

    public void onButtonApplyClicked() {
        Intent intent = new Intent();
        effectiveFilters.clear();
        effectiveFilters.putAll(temporaryFilters);
        effectiveSortingKeys.clear();
        effectiveSortingKeys.putAll(temporarySortingKeys);
        intent.putExtra("filters", (HashMap) effectiveFilters);
        intent.putExtra("sorting_keys", (HashMap) effectiveSortingKeys);
        reviewsFiltersFragment.setTargetFragment(
                reviewsFiltersFragment.getAccommodationFacilityFragment(),
                REVIEWS_FILTERS_REQUEST_CODE
        );
        if(reviewsFiltersFragment.getTargetFragment() != null) {
            reviewsFiltersFragment.getTargetFragment().onActivityResult(
                    reviewsFiltersFragment.getTargetRequestCode(),
                    Activity.RESULT_OK,
                    intent
            );
        }
        reviewsFiltersFragment.dismiss();
    }

    public void onButtonResetClicked() {
        effectiveFilters = new HashMap<>();
        effectiveSortingKeys = new HashMap<>();
        temporaryFilters.put("accommodation_facility_id", accommodationFacility.getAccommodationFacilityId());
        if(User.isLoggedIn()) {
            temporaryFilters.put("user_id", User.getInstance().getUserId());
        }
        initializeMaterialSpinners();
        showFilters();
        showSortingKeys();
    }

    public void onCheckBoxFiveStarsClicked() {
        if(reviewsFiltersFragment.isSmoothCheckBoxFiveStarsChecked()) {
            reviewsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(false, true);
            setRatingFilter("5", true);
        } else {
            reviewsFiltersFragment.setSmoothCheckBoxFiveStarsChecked(true, true);
            setRatingFilter("5", false);
        }
    }

    public void onCheckBoxFourStarsClicked() {
        if(reviewsFiltersFragment.isSmoothCheckBoxFourStarsChecked()) {
            reviewsFiltersFragment.setSmoothCheckBoxFourStarsChecked(false, true);
            setRatingFilter("4", true);
        } else {
            reviewsFiltersFragment.setSmoothCheckBoxFourStarsChecked(true, true);
            setRatingFilter("4", false);
        }
    }

    public void onCheckBoxThreeStarsClicked() {
        if(reviewsFiltersFragment.isSmoothCheckBoxThreeStarsChecked()) {
            reviewsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(false, true);
            setRatingFilter("3", true);
        } else {
            reviewsFiltersFragment.setSmoothCheckBoxThreeStarsChecked(true, true);
            setRatingFilter("3", false);
        }
    }

    public void onCheckBoxTwoStarsClicked() {
        if(reviewsFiltersFragment.isSmoothCheckBoxTwoStarsChecked()) {
            reviewsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(false, true);
            setRatingFilter("2", true);
        } else {
            reviewsFiltersFragment.setSmoothCheckBoxTwoStarsChecked(true, true);
            setRatingFilter("2", false);
        }
    }

    public void onCheckBoxOneStarClicked() {
        if(reviewsFiltersFragment.isSmoothCheckBoxOneStarChecked()) {
            reviewsFiltersFragment.setSmoothCheckBoxOneStarChecked(false, true);
            setRatingFilter("1", true);
        } else {
            reviewsFiltersFragment.setSmoothCheckBoxOneStarChecked(true, true);
            setRatingFilter("1", false);
        }
    }

    public void onChipSummerClicked() {
        if(reviewsFiltersFragment.isChipSummerChecked()) {
            reviewsFiltersFragment.setChipSummerBackgroundColor(R.color.darkBlue);
            reviewsFiltersFragment.setChipSummerChecked(false);
            setSeasonFilter("summer", false);
        } else {
            reviewsFiltersFragment.setChipSummerBackgroundColor(R.color.peach);
            reviewsFiltersFragment.setChipSummerChecked(true);
            setSeasonFilter("summer", true);
        }
    }

    public void onChipSpringClicked() {
        if(reviewsFiltersFragment.isChipSpringChecked()) {
            reviewsFiltersFragment.setChipSpringBackgroundColor(R.color.darkBlue);
            reviewsFiltersFragment.setChipSpringChecked(false);
            setSeasonFilter("spring", false);
        } else {
            reviewsFiltersFragment.setChipSpringBackgroundColor(R.color.peach);
            reviewsFiltersFragment.setChipSpringChecked(true);
            setSeasonFilter("spring", true);
        }
    }

    public void onChipAutumnClicked() {
        if(reviewsFiltersFragment.isChipAutumnChecked()) {
            reviewsFiltersFragment.setChipAutumnBackgroundColor(R.color.darkBlue);
            reviewsFiltersFragment.setChipAutumnChecked(false);
            setSeasonFilter("autumn", false);
        } else {
            reviewsFiltersFragment.setChipAutumnBackgroundColor(R.color.peach);
            reviewsFiltersFragment.setChipAutumnChecked(true);
            setSeasonFilter("autumn", true);
        }
    }

    public void onChipWinterClicked() {
        if(reviewsFiltersFragment.isChipWinterChecked()) {
            reviewsFiltersFragment.setChipWinterBackgroundColor(R.color.darkBlue);
            reviewsFiltersFragment.setChipWinterChecked(false);
            setSeasonFilter("winter", false);
        } else {
            reviewsFiltersFragment.setChipWinterBackgroundColor(R.color.peach);
            reviewsFiltersFragment.setChipWinterChecked(true);
            setSeasonFilter("winter", true);
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

    public void onMaterialSpinnerPublicationDateSelected(@NotNull Object itemSelected) {
        if(itemSelected.equals("NONE")){
            temporarySortingKeys.remove("publication_date");
        }else if(itemSelected.equals("ASC")){
            temporarySortingKeys.put("publication_date", "ASC");
        }else{
            temporarySortingKeys.put("publication_date", "DESC");
        }
    }

    public void onMaterialSpinnerAppreciationSelected(@NotNull Object itemSelected) {
        if(itemSelected.equals("NONE")){
            temporarySortingKeys.remove("total_likes");
            temporarySortingKeys.remove("total_dislikes");
        } else if(itemSelected.equals("ASC")){
            temporarySortingKeys.put("total_likes", "ASC");
            temporarySortingKeys.put("total_dislikes", "DESC");
        } else{
            temporarySortingKeys.put("total_dislikes", "ASC");
            temporarySortingKeys.put("total_likes", "DESC");
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

    private void setSeasonFilter(String value, boolean isChecked) {
        String season = temporaryFilters.get("season");
        value += ";";
        if(isChecked) {
            if(season != null){
                season += value;
                temporaryFilters.put("season", season);
            } else{
                temporaryFilters.put("season", value);
            }
        } else {
            if(season != null) {
                season = season.replace(value, "");
                if(season.equals("")){
                    temporaryFilters.remove("season");
                } else {
                    temporaryFilters.put("season", season);
                }
            }
        }
    }

}