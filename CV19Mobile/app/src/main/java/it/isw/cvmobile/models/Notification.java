package it.isw.cvmobile.models;

import com.google.gson.annotations.SerializedName;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class Notification {

    @SerializedName("user_id")
    private final String userId;
    @SerializedName("review_id")
    private final String reviewId;
    private final String title;
    private final String message;
    private final String type;



    public Notification(String userId,
                        String reviewId,
                        String title,
                        String message,
                        String type) {
        this.userId = userId;
        this.reviewId = reviewId;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getReviewId() {
        return reviewId;
    }

}





