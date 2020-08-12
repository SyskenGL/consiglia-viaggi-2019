package it.isw.cvmobile.dao.interfaces;

import android.net.Uri;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.models.Review;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public interface ReviewDAO {

    void getReviews(Map<String, String> filters,
                    Map<String, String> sortingKeys,
                    int offset,
                    int limit,
                    Object resultsHandler);

    void addFeedback(boolean isLike, String reviewId, String userId, Object resultsHandler);

    void deleteFeedback(String reviewId, String userId, Object resultsHandler);

    void addReview(String title,
                   String description,
                   String rating,
                   String dateOfStay,
                   int totalImages,
                   String accommodationFacilityId,
                   String userId,
                   Object resultsHandler);

    void addReviewImage(Uri imageUri,
                        String reviewId,
                        String accommodationFacilityId,
                        int imageIndex,
                        Object resultsHandler);

    List<Review> parseResults(Object results);

}
