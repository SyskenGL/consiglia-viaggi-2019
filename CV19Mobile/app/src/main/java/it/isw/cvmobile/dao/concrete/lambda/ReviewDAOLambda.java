package it.isw.cvmobile.dao.concrete.lambda;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.dao.interfaces.ReviewDAO;
import it.isw.cvmobile.models.Review;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.LambdaInvoker;
import it.isw.cvmobile.utils.aws.Payload;
import it.isw.cvmobile.utils.aws.S3Uploader;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.aws.enumerations.PayloadType;


@Completed
public class ReviewDAOLambda implements ReviewDAO {

    private Context context;



    public ReviewDAOLambda(Context context) {
        this.context = context;
    }

    @Override
    @RequiresInternetConnection
    public void getReviews(Map<String, String> filters,
                           Map<String, String> sortingKeys,
                           int offset,
                           int limit,
                           Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.GET_REVIEWS);
        if(filters != null) {
            for(Map.Entry<String, String> entry : filters.entrySet()) {
                for(String filter : entry.getValue().split(";")) {
                    payload.setFilter(entry.getKey(), filter);
                }
            }
        }
        if(sortingKeys != null) {
            for (Map.Entry<String, String> entry : sortingKeys.entrySet()) {
                payload.setSortingKeys(entry.getKey(), entry.getValue());
            }
        }
        payload.setOffset(String.valueOf(offset));
        payload.setLimit(String.valueOf(limit));
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void addFeedback(boolean isLike, String reviewId, String userId, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.POST_FEEDBACK);
        payload.setValue("user_id", String.valueOf(userId));
        payload.setValue("review_id", String.valueOf(reviewId));
        if(isLike) {
            payload.setValue("type", "thumb_up");
        } else {
            payload.setValue("type", "thumb_down");
        }
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void deleteFeedback(String reviewId, String userId, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.DELETE_FEEDBACK);
        payload.setValue("user_id", String.valueOf(userId));
        payload.setValue("review_id", String.valueOf(reviewId));
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void addReview(String title,
                          String description,
                          String rating,
                          String dateOfStay,
                          int totalImages,
                          String accommodationFacilityId,
                          String userId,
                          Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.POST_REVIEW);
        payload.setValue("title", title);
        payload.setValue("description", description);
        payload.setValue("rating", rating);
        payload.setValue("date_of_stay", dateOfStay);
        payload.setValue("total_images", String.valueOf(totalImages));
        payload.setValue("accommodation_facility_id", accommodationFacilityId);
        payload.setValue("user_id", userId);
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void addReviewImage(Uri imageUri,
                               String reviewId,
                               String accommodationFacilityId,
                               int imageIndex,
                               Object resultsHandler) {
        if(!(resultsHandler instanceof TransferListener)) {
            throw new IllegalArgumentException();
        }
        InputStream imageStream;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            imageStream = contentResolver.openInputStream(imageUri);
            if(imageStream != null) {
                String url = "accommodation_facilities/accommodation_facility_" + accommodationFacilityId
                           + "/reviews/review_" + reviewId + "/image_" + imageIndex + ".png";
                File tmp = File.createTempFile("tmp", null);
                tmp.deleteOnExit();
                OutputStream outputStream = new FileOutputStream(tmp);
                IOUtils.copy(imageStream, outputStream);
                S3Uploader s3Uploader = new S3Uploader(context);
                s3Uploader.upload(url,
                        tmp,
                        CannedAccessControlList.PublicRead,
                        (TransferListener) resultsHandler
                );
            }
        } catch (IOException exception) {
            ((TransferListener) resultsHandler).onError(0, exception);
        }
    }

    @Override
    @RequiresInternetConnection
    public List<Review> parseResults(Object results) {
        if(!(results instanceof JsonObject)) {
            throw new IllegalArgumentException();
        }
        JsonObject jsonResults = (JsonObject) results;
        ArrayList<Review> reviews = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        JsonArray jsonReviews = jsonResults.getAsJsonObject("data").getAsJsonArray("reviews");
        int retrieved = jsonResults.getAsJsonObject("data").get("retrieved").getAsInt();
        for(int reviewIndex = 0; reviewIndex < retrieved; reviewIndex++) {
            Review review = gson.fromJson(jsonReviews.get(reviewIndex), Review.class);
            reviews.add(review);
        }
        return reviews;
    }

}