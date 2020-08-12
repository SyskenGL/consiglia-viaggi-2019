package it.isw.cvmobile.models;

import com.google.gson.annotations.SerializedName;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class AccommodationFacility {

    @SerializedName("accommodation_facility_id")
    private final String accommodationFacilityId;
    private final String name;
    private final String type;
    private final String country;
    @SerializedName("administrative_area_level_1")
    private final String administrativeAreaLevel1;
    @SerializedName("administrative_area_level_2")
    private final String administrativeAreaLevel2;
    @SerializedName("administrative_area_level_3")
    private final String administrativeAreaLevel3;
    private final String locality;
    private final String address;
    private final String email;
    private final String website;
    private final String phone;
    private final String description;
    private final String latitude;
    private final String longitude;
    private final int rating;
    @SerializedName("total_favorites")
    private int totalFavorites;
    @SerializedName("total_views")
    private int totalViews;
    @SerializedName("total_reviews")
    private int totalReviews;
    private final String tags;
    @SerializedName("total_five_stars_reviews")
    private int totalFiveStarsReviews;
    @SerializedName("total_four_stars_reviews")
    private int totalFourStarsReviews;
    @SerializedName("total_three_stars_reviews")
    private int totalThreeStarsReviews;
    @SerializedName("total_two_stars_reviews")
    private int totalTwoStarsReviews;
    @SerializedName("total_one_stars_reviews")
    private int totalOneStarReviews;
    @SerializedName("matched_in")
    private final String matchedIn;
    @SerializedName("total_matched_reviews")
    private final int totalMatchedReviews;
    @SerializedName("matched_review_sample")
    private final String matchedReviewSample;
    private final String[] images;
    private boolean commented;
    private boolean favorite;


    public AccommodationFacility(String accommodationFacilityId,
                                 String name,
                                 String type,
                                 String country,
                                 String administrativeAreaLevel1,
                                 String administrativeAreaLevel2,
                                 String administrativeAreaLevel3,
                                 String locality,
                                 String address,
                                 String email,
                                 String website,
                                 String phone,
                                 String description,
                                 String latitude,
                                 String longitude,
                                 int rating,
                                 int totalFavorites,
                                 int totalViews,
                                 int totalReviews,
                                 String tags,
                                 int totalFiveStarsReviews,
                                 int totalFourStarsReviews,
                                 int totalThreeStarsReviews,
                                 int totalTwoStarsReviews,
                                 int totalOneStarsReviews,
                                 String matchedIn,
                                 int totalMatchedReviews,
                                 String matchedReviewSample,
                                 String[] images,
                                 boolean commented,
                                 boolean favorite) {
        this.accommodationFacilityId = accommodationFacilityId;
        this.name = name;
        this.type = type;
        this.country = country;
        this.administrativeAreaLevel1 = administrativeAreaLevel1;
        this.administrativeAreaLevel2 = administrativeAreaLevel2;
        this.administrativeAreaLevel3 = administrativeAreaLevel3;
        this.locality = locality;
        this.address = address;
        this.email = email;
        this.website = website;
        this.phone = phone;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.totalFavorites = totalFavorites;
        this.totalViews = totalViews;
        this.totalReviews = totalReviews;
        this.tags = tags;
        this.totalFiveStarsReviews = totalFiveStarsReviews;
        this.totalFourStarsReviews = totalFourStarsReviews;
        this.totalThreeStarsReviews = totalThreeStarsReviews;
        this.totalTwoStarsReviews = totalTwoStarsReviews;
        this.totalOneStarReviews = totalOneStarsReviews;
        this.matchedIn = matchedIn;
        this.totalMatchedReviews = totalMatchedReviews;
        this.matchedReviewSample = matchedReviewSample;
        this.images = images;
        this.commented = commented;
        this.favorite = favorite;
    }

    public String getAccommodationFacilityId() {
        return accommodationFacilityId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public String getAdministrativeAreaLevel1() {
        return administrativeAreaLevel1;
    }

    public String getAdministrativeAreaLevel2() {
        return administrativeAreaLevel2;
    }

    public String getAdministrativeAreaLevel3() {
        return administrativeAreaLevel3;
    }

    public String getLocality() {
        return locality;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getRating() {
        return rating;
    }

    public int getTotalFavorites() {
        return totalFavorites;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public String getTags() {
        return tags;
    }

    public int getTotalFiveStarsReviews() {
        return totalFiveStarsReviews;
    }

    public int getTotalFourStarsReviews() {
        return totalFourStarsReviews;
    }

    public int getTotalThreeStarsReviews() {
        return totalThreeStarsReviews;
    }

    public int getTotalTwoStarsReviews() {
        return totalTwoStarsReviews;
    }

    public int getTotalOneStarReviews() {
        return totalOneStarReviews;
    }

    public String getMatchedIn() {
        return matchedIn;
    }

    public int getTotalMatchedReviews() {
        return totalMatchedReviews;
    }

    public String getMatchedReviewSample() {
        return matchedReviewSample;
    }

    public String[] getImages() {
        return images;
    }

    public boolean isCommented() {
        return commented;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setTotalFavorites(int totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public void setTotalFiveStarsReviews(int totalFiveStarsReviews) {
        this.totalFiveStarsReviews = totalFiveStarsReviews;
    }

    public void setTotalFourStarsReviews(int totalFourStarsReviews) {
        this.totalFourStarsReviews = totalFourStarsReviews;
    }

    public void setTotalThreeStarsReviews(int totalThreeStarsReviews) {
        this.totalThreeStarsReviews = totalThreeStarsReviews;
    }

    public void setTotalTwoStarsReviews(int totalTwoStarsReviews) {
        this.totalTwoStarsReviews = totalTwoStarsReviews;
    }

    public void setTotalOneStarReviews(int totalOneStarsReviews) {
        this.totalOneStarReviews = totalOneStarsReviews;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}