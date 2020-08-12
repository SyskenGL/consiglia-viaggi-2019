package it.isw.cvmobile.dao.interfaces;

import java.util.List;
import it.isw.cvmobile.models.Notification;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public interface NotificationDAO {

    void getNotifications(String userId, String status, int offset, int limit, Object resultsHandler);

    void deleteNotification(String reviewId, String userId, Object resultsHandler);

    void updateNotificationStatus(String userId, Object resultsHandler);

    List<Notification> parseResults(Object results);

}
