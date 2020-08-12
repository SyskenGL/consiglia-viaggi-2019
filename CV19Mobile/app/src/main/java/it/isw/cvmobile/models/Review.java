package it.isw.cvmobile.models;

import com.google.gson.annotations.SerializedName;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class Review {


    @SerializedName("review_id")
    private final String reviewId;
    @SerializedName("accommodation_facility_id")
    private final String accommodationFacilityId;
    @SerializedName("accommodation_facility_name")
    private final String accommodationFacilityName;
    private final String title;
    private final String description;
    private final String status;
    private final int rating;
    @SerializedName("date_of_stay")
    private final String dateOfStay;
    @SerializedName("publication_date")
    private final String publicationDate;
    @SerializedName("total_likes")
    private int totalLikes;
    @SerializedName("total_dislikes")
    private int totalDislikes;
    private String feedback;
    @SerializedName("reviewer_username")
    private final String reviewerUsername;
    @SerializedName("reviewer_profile_picture")
    private final String reviewerProfilePicture;
    private final String[] images;



    public Review(String reviewId,
                  String accommodationFacilityId,
                  String accommodationFacilityName,
                  String title,
                  String description,
                  String status,
                  int rating,
                  String dateOfStay,
                  String publicationDate,
                  int totalLikes,
                  int totalDislikes,
                  String feedback,
                  String reviewerUsername,
                  String reviewerProfilePicture,
                  String[] images) {
        this.reviewId = reviewId;
        this.accommodationFacilityId = accommodationFacilityId;
        this.accommodationFacilityName = accommodationFacilityName;
        this.title = title;
        this.description = description;
        this.status = status;
        this.rating = rating;
        this.dateOfStay = dateOfStay;
        this.publicationDate = publicationDate;
        this.totalLikes = totalLikes;
        this.totalDislikes = totalDislikes;
        this.feedback = feedback;
        this.reviewerUsername = reviewerUsername;
        this.reviewerProfilePicture = reviewerProfilePicture;
        this.images = images;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getAccommodationFacilityId() {
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

    public String getFeedback() {
        return feedback;
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

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public void setTotalDislikes(int totalDislikes) {
        this.totalDislikes = totalDislikes;
    }

}
