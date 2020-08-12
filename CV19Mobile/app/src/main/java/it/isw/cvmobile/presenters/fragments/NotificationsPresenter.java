package it.isw.cvmobile.presenters.fragments;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.NotificationDAO;
import it.isw.cvmobile.models.Notification;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.NotificationsFragment;
import it.isw.cvmobile.widgets.scrollview.cards.notification.NotificationCardAdapter;


@Completed
public class NotificationsPresenter {

    private final static int LIMIT = 100;

    private final NotificationsFragment notificationsFragment;
    private final NotificationCardAdapter notificationCardAdapter;
    private final List<Notification> notifications;



    public NotificationsPresenter(@NotNull NotificationsFragment notificationsFragment) {
        this.notificationsFragment = notificationsFragment;
        notifications = new ArrayList<>();
        notificationCardAdapter = new NotificationCardAdapter(
                notificationsFragment.getNavigationActivity(),
                notifications
        );
        notificationsFragment.setNotificationsRecyclerViewAdapter(notificationCardAdapter);
        markNotifications();
        fetchNotifications();
    }

    private void markNotifications() {
        NotificationDAO notificationDAO =
                DAOFactory.getNotificationDAO(notificationsFragment.getNavigationActivity());
        notificationDAO.updateNotificationStatus(User.getInstance().getUserId(), null);
    }

    public void onButtonRefreshClicked() {
        notificationsFragment.startButtonRefreshSpinAnimation();
        fetchNotifications();
    }

    private void handleFetchNotificationsSuccess(@NotNull JsonObject results,
                                                 NotificationDAO notificationDAO) {
        notificationsFragment.clearButtonRefreshAnimation();
        notificationsFragment.setErrorViewVisibility(false);
        int retrieved = results.getAsJsonObject("data").get("retrieved").getAsInt();
        if(retrieved != 0) {
            notifications.addAll(notificationDAO.parseResults(results));
            notificationCardAdapter.notifyDataSetChanged();
        } else {
            notificationsFragment.setNoResultsViewVisibility(true);
        }
        notificationsFragment.setLoadingSpinnerVisibility(false);
    }

    private void handleFetchNotificationsFailure() {
        notificationsFragment.clearButtonRefreshAnimation();
        notificationsFragment.setErrorViewVisibility(true);
        notificationsFragment.setLoadingSpinnerVisibility(false);
    }

    @RequiresInternetConnection
    private void fetchNotifications() {
        if(FullScreenActivity.isNetworkAvailable(notificationsFragment.getNavigationActivity())) {
            final NotificationDAO notificationDAO =
                    DAOFactory.getNotificationDAO(notificationsFragment.getNavigationActivity());
            notificationDAO.getNotifications(
                    User.getInstance().getUserId(),
                    null,
                    0,
                    LIMIT,
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            handleFetchNotificationsSuccess(results, notificationDAO);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            handleFetchNotificationsFailure();
                        }

            });
        } else {
            handleFetchNotificationsFailure();
        }
    }

}