package it.isw.cvoffice.utils.aws.lambda.callbacks;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;


public interface LambdaResultHandler {

    default void onSuccess(JsonObject result) {
        System.out.println("result from lambda: " + result);
    }

    default void onFailure(@NotNull Exception exception) {
        System.out.println("lambda failed: " + exception);
    };

}