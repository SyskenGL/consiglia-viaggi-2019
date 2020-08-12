package it.isw.cvoffice.dao.concrete.lambda;

import com.google.gson.*;
import it.isw.cvoffice.dao.ReviewDAO;
import it.isw.cvoffice.dao.concrete.lambda.exceptions.OperatorNotAuthenticatedException;
import it.isw.cvoffice.models.Operator;
import it.isw.cvoffice.models.Review;
import it.isw.cvoffice.utils.aws.lambda.LambdaInvoker;
import it.isw.cvoffice.utils.aws.lambda.Payload;
import it.isw.cvoffice.utils.aws.lambda.callbacks.LambdaResultHandler;
import it.isw.cvoffice.utils.aws.lambda.enumerations.PayloadType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReviewDAOLambda implements ReviewDAO {

    @Override
    public void getReviews(Map<String, String> filters,
                           Map<String, String> sortingKeys,
                           int offset,
                           int limit,
                           Object resultHandler) {
        if(!(resultHandler instanceof LambdaResultHandler)) {
            throw new IllegalArgumentException(
                    "resultHandler: expected LambdaResultHandler -> passed "
                            + resultHandler.getClass()
            );
        }
        if(!Operator.isLoggedIn()) {
            throw new OperatorNotAuthenticatedException("operator is not authenticated");
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(Operator.getInstance().getCredentials());
        Payload payload = new Payload(PayloadType.GET_REVIEWS);
        if(filters != null) {
            for(Map.Entry<String, String> filter: filters.entrySet()) {
                for(String filterValue: filter.getValue().split(";")) {
                    payload.setFilter(filter.getKey(), filterValue);
                }
            }
        }
        if(sortingKeys != null) {
            for(Map.Entry<String, String> sortingKey: sortingKeys.entrySet()) {
                for(String sortingKeyValue: sortingKey.getValue().split(";")) {
                    payload.setFilter(sortingKey.getKey(), sortingKeyValue);
                }
            }
        }
        payload.setOffset(String.valueOf(offset));
        payload.setLimit(String.valueOf(limit));
        lambdaInvoker.invoke(payload, (LambdaResultHandler) resultHandler);
    }

    @Override
    public void updateReviewStatus(String reviewId, String status, Object resultHandler) {
        if(!(resultHandler instanceof LambdaResultHandler)) {
            throw new IllegalArgumentException(
                    "resultHandler: expected LambdaResultHandler -> passed "
                    + resultHandler.getClass()
            );
        }
        if(!Operator.isLoggedIn()) {
            throw new OperatorNotAuthenticatedException("operator is not authenticated");
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(Operator.getInstance().getCredentials());
        Payload payload = new Payload(PayloadType.UPDATE_REVIEW_STATUS);
        payload.setValue("review_id", reviewId);
        payload.setValue("status", status);
        lambdaInvoker.invoke(payload, (LambdaResultHandler) resultHandler);
    }

    @Override
    public List<Review> parseResult(Object result) {
        if(!(result instanceof JsonObject)) {
            throw new IllegalArgumentException("result: expected JsonObject -> passed " + result.getClass());
        }
        Gson gson = new GsonBuilder().create();
        List<Review> reviews = new ArrayList<>();
        JsonArray jsonReviews = ((JsonObject)result).getAsJsonObject("data").getAsJsonArray("reviews");
        for(JsonElement review: jsonReviews) {
            reviews.add(gson.fromJson(review, Review.class));
        }
        return reviews;
    }

}