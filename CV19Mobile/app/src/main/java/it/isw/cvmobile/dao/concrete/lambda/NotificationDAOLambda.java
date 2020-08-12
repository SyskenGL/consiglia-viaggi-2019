package it.isw.cvmobile.dao.concrete.lambda;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import it.isw.cvmobile.dao.interfaces.NotificationDAO;
import it.isw.cvmobile.models.Notification;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.LambdaInvoker;
import it.isw.cvmobile.utils.aws.Payload;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.aws.enumerations.PayloadType;


@Completed
public class NotificationDAOLambda implements NotificationDAO {

    private Context context;



    public NotificationDAOLambda(Context context) {
        this.context = context;
    }

    @Override
    @RequiresInternetConnection
    public void getNotifications(String userId, String status, int offset, int limit, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.GET_NOTIFICATIONS);
        payload.setFilter("user_id", userId);
        if(status != null) {
            payload.setFilter("status", status);
        }
        payload.setOffset(String.valueOf(offset));
        payload.setLimit(String.valueOf(limit));
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void deleteNotification(String reviewId, String userId, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.DELETE_NOTIFICATION);
        payload.setValue("user_id", userId);
        payload.setValue("review_id", reviewId);
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void updateNotificationStatus(String userId, Object resultsHandler) {
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.UPDATE_NOTIFICATION);
        payload.setValue("user_id", userId);
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public List<Notification> parseResults(Object results) {
        if(!(results instanceof JsonObject)) {
            throw new IllegalArgumentException();
        }
        JsonObject jsonResults = (JsonObject) results;
        ArrayList<Notification> notifications = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        JsonArray jsonNotifications = jsonResults.getAsJsonObject("data").getAsJsonArray("notifications");
        int retrieved = jsonResults.getAsJsonObject("data").get("retrieved").getAsInt();
        for(int notificationIndex = 0; notificationIndex < retrieved; notificationIndex++) {
            Notification notification = gson.fromJson(jsonNotifications.get(notificationIndex), Notification.class);
            notifications.add(notification);
        }
        return notifications;
    }

}
