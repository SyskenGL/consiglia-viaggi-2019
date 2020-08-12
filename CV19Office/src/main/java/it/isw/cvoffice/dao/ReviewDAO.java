package it.isw.cvoffice.dao;

import it.isw.cvoffice.models.Review;
import java.util.List;
import java.util.Map;


public interface ReviewDAO {

    void getReviews(Map<String, String> filters,
                    Map<String, String> sortingKeys,
                    int offset,
                    int limit,
                    Object resultHandler);

    void updateReviewStatus(String reviewId, String status, Object resultHandler);

    List<Review> parseResult(Object result);

}