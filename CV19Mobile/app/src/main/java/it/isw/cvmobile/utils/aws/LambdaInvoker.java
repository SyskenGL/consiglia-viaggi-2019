package it.isw.cvmobile.utils.aws;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.regions.Region;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.aws.enumerations.PayloadType;


@Completed
public class LambdaInvoker {

    private final AWSLambdaClient lambdaClient;



    public LambdaInvoker(Context context) {
        Cognito cognito = Cognito.getInstance(context);
        lambdaClient = new AWSLambdaClient(cognito.getCognitoCachingCredentialsProvider());
        lambdaClient.setRegion(Region.getRegion(Cognito.REGION));
    }

    private static String getLambdaFunctionName(PayloadType payloadType) {
        switch (payloadType) {
            case UPDATE_NOTIFICATION:
                return "updateNotificationStatus";
            case DELETE_FAVORITE:
                return "deleteFavorite";
            case DELETE_FEEDBACK:
                return "deleteFeedback";
            case DELETE_NOTIFICATION:
                return "deleteNotification";
            case GET_NOTIFICATIONS:
                return "getNotifications";
            case GET_HISTORY:
                return "getHistory";
            case GET_FAVORITES:
                return "getFavorites";
            case GET_REVIEWS:
                return "getReviews";
            case GET_ACCOMMODATION_FACILITIES:
                return "getAccommodationFacilities";
            case GET_USER_DATA:
                return "getUserData";
            case GET_USER_VALIDITY:
                return "getUserValidity";
            case POST_FAVORITE:
                return "postFavorite";
            case POST_FEEDBACK:
                return "postFeedback";
            case POST_REVIEW:
                return "postReview";
            case POST_VIEW:
                return "postView";
            default:
                return "undefined";
        }
    }

    @RequiresInternetConnection
    public void invoke(Payload payload, LambdaResultsHandler resultsHandler) {
        new LambdaInvocation(payload, resultsHandler).execute(lambdaClient);
    }



    private static class LambdaInvocation extends AsyncTask<AWSLambdaClient, Void, InvokeResult> {

        private final Payload payload;
        private final LambdaResultsHandler resultsHandler;



        private LambdaInvocation(Payload payload, LambdaResultsHandler resultsHandler) {
            this.payload = payload;
            this.resultsHandler = resultsHandler;
        }

        @Override
        protected InvokeResult doInBackground(AWSLambdaClient... lambdaClients) {
            InvokeResult invokeResult = null;
            String payloadString = payload.getPayloadBody().toString();
            ByteBuffer payloadByteBuffer = ByteBuffer.wrap(payloadString.getBytes(StandardCharsets.UTF_8));
            InvokeRequest request = new InvokeRequest();
            request.setFunctionName(getLambdaFunctionName(payload.getPayloadType()));
            request.setPayload(payloadByteBuffer);
            try {
                invokeResult = lambdaClients[0].invoke(request);
            } catch (Exception exception) {
                // ...
            }
            return invokeResult;
        }

        @Override
        protected void onPostExecute(InvokeResult results) {
            if(resultsHandler != null) {
                if(results != null) {
                    String decodedResults = StandardCharsets.UTF_8.decode(results.getPayload()).toString();
                    JsonObject jsonResults = new JsonParser().parse(decodedResults).getAsJsonObject();
                    if(jsonResults.has("status") && jsonResults.get("status").getAsString().equals("success")) {
                        Log.println(Log.INFO, "LambdaInvoker", "LAMBDA REPORT \n[PAYLOAD]: "
                                + payload.getPayloadBody() + "\n[RESULTS]: " + jsonResults);
                        resultsHandler.onSuccess(jsonResults);
                    } else {
                        Log.println(Log.ERROR, "LambdaInvoker", "LAMBDA REPORT \n[PAYLOAD]: "
                                + payload.getPayloadBody() + "\n[FAILED]: " + jsonResults);
                        resultsHandler.onFailure(new LambdaFunctionException("err", "err"));
                    }
                } else {
                    Log.println(Log.ERROR, "LambdaInvoker", "LAMBDA REPORT \n[PAYLOAD]: "
                            + payload.getPayloadBody() + "\n[FAILED]: null");
                    resultsHandler.onFailure(new LambdaFunctionException("err", "err"));
                }
            }
        }

    }

}