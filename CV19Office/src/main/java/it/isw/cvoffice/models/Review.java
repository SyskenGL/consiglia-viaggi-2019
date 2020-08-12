package it.isw.cvoffice.models;

import com.google.gson.annotations.SerializedName;


public class Review {

    @SerializedName("review_id")
    private final int reviewId;
    @SerializedName("accommodation_facility_id")
    private final int accommodationFacilityId;
    @SerializedName("accommodation_facility_name")
    private final String accommodationFacilityName;
    private final String title;
    private final String description;
    private final String status;
    @SerializedName("expected_cancellation_date")
    private final String expectedCancellationDate;
    private final int rating;
    @SerializedName("date_of_stay")
    private final String dateOfStay;
    @SerializedName("publication_date")
    private final String publicationDate;
    @SerializedName("total_likes")
    private final int totalLikes;
    @SerializedName("total_dislikes")
    private final int totalDislikes;
    @SerializedName("reviewer_id")
    private final String reviewerId;
    @SerializedName("reviewer_username")
    private final String reviewerUsername;
    @SerializedName("reviewer_profile_picture")
    private final String reviewerProfilePicture;
    private final String[] images;


    public Review(int reviewId,
                  int accommodationFacilityId,
                  String accommodationFacilityName,
                  String title,
                  String description,
                  String status,
                  String expectedCancellationDate,
                  int rating,
                  String dateOfStay,
                  String publicationDate,
                  int totalLikes,
                  int totalDislikes,
                  String reviewerId,
                  String reviewerUsername,
                  String reviewerProfilePicture,
                  String[] images) {
        this.reviewId = reviewId;
        this.accommodationFacilityId = accommodationFacilityId;
        this.accommodationFacilityName = accommodationFacilityName;
        this.title = title;
        this.description = description;
        this.status = status;
        this.expectedCancellationDate = expectedCancellationDate;
        this.rating = rating;
        this.dateOfStay = dateOfStay;
        this.publicationDate = publicationDate;
        this.reviewerId = reviewerId;
        this.totalLikes = totalLikes;
        this.totalDislikes = totalDislikes;
        this.reviewerUsername = reviewerUsername;
        this.reviewerProfilePicture = reviewerProfilePicture;
        this.images = images;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getAccommodationFacilityId() {
        return accommodationFacilityId;
    }

    public String getAccommodationFacilityName() {
        return accommodationFacilityName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getExpectedCancellationDate() {
        return expectedCancellationDate;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public int getRating() {
        return rating;
    }

    public String getDateOfStay() {
        return dateOfStay;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public int getTotalDislikes() {
        return totalDislikes;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public String getReviewerProfilePicture() {
        return reviewerProfilePicture;
    }

    public String[] getImages() {
        return images;
    }

}