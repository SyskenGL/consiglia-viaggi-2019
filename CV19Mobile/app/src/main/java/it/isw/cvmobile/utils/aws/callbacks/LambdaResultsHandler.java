package it.isw.cvmobile.utils.aws.callbacks;

import com.google.gson.JsonObject;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public interface LambdaResultsHandler {
    void onSuccess(JsonObject results);
    void onFailure(Exception exception);
}
