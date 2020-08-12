package it.isw.cvoffice.utils.aws.lambda;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.isw.cvoffice.utils.aws.cognito.Cognito;
import it.isw.cvoffice.utils.aws.lambda.callbacks.LambdaResultHandler;
import it.isw.cvoffice.utils.aws.lambda.enumerations.PayloadType;
import it.isw.cvoffice.utils.aws.lambda.exceptions.LambdaFailedException;
import it.isw.cvoffice.utils.aws.lambda.exceptions.NoSuchLambdaFunctionException;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import java.nio.charset.StandardCharsets;


public class LambdaInvoker {

    private static LambdaClient lambdaClient;


    public LambdaInvoker(@NotNull Credentials credentials) {
        AwsSessionCredentials awsSessionCredentials = AwsSessionCredentials.create(
                credentials.accessKeyId(),
                credentials.secretKey(),
                credentials.sessionToken()
        );
        lambdaClient = LambdaClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsSessionCredentials))
                .region(Cognito.REGION)
                .build();
    }

    private @NotNull String getFunctionName(@NotNull PayloadType payloadType) {
        switch (payloadType) {
            case GET_REVIEWS:
                return "getReviewsOperator";
            case POST_NOTIFICATION:
                return "postNotification";
            case UPDATE_REVIEW_STATUS:
                return "updateReviewStatus";
            default:
                throw new NoSuchLambdaFunctionException("no lambda function for the specified payload type");
        }
    }

    public void invoke(@NotNull Payload payload, LambdaResultHandler lambdaResultHandler) {
        new Thread(() -> {
            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(getFunctionName(payload.getPayloadType()))
                    .payload(SdkBytes.fromString(new Gson().toJson(payload.getPayloadBody()), StandardCharsets.UTF_8))
                    .build();
            InvokeResponse invokeResponse = null;
            try {
                invokeResponse = lambdaClient.invoke(invokeRequest);
                JsonObject result = JsonParser.parseString(invokeResponse.payload().asUtf8String()).getAsJsonObject();
                if(result != null && result.has("status") && result.get("status").getAsString().equals("success")) {
                    if(lambdaResultHandler != null) {
                        lambdaResultHandler.onSuccess(result);
                    }
                } else {
                    if(lambdaResultHandler != null) {
                        lambdaResultHandler.onFailure(new LambdaFailedException("lambda failed with an unknown error"));
                    }
                }
            } catch (Exception exception) {
                if(lambdaResultHandler != null) {
                    lambdaResultHandler.onFailure(exception);
                }
            }
        }).start();
    }

}