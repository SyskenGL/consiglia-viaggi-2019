package it.isw.cvoffice.dao;


public interface NotificationDAO {

    void postNotification(String reviewId,
                          String userId,
                          String title,
                          String message,
                          String type,
                          Object resultHandler);

}
