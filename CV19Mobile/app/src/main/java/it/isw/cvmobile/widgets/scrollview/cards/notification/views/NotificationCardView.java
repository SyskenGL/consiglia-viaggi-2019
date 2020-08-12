package it.isw.cvmobile.widgets.scrollview.cards.notification.views;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.NotificationDAO;
import it.isw.cvmobile.models.Notification;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.widgets.scrollview.cards.notification.NotificationCardAdapter;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class NotificationCardView extends NotificationCardAdapter.CardViewHolder {

    private final NotificationCardAdapter notificationCardAdapter;
    private final Activity activity;
    private final ImageView notificationIcon;
    private final ImageView notificationIconBackground;
    private final ImageView buttonDelete;
    private final TextView textViewTitle;
    private final TextView textViewMessage;



    public NotificationCardView(@NonNull View itemView,
                                NotificationCardAdapter notificationCardAdapter,
                                Activity activity) {
        super(itemView);
        this.activity = activity;
        this.notificationCardAdapter = notificationCardAdapter;
        notificationIcon = itemView.findViewById(R.id.card_notification_icon);
        notificationIconBackground = itemView.findViewById(R.id.card_notification_icon_background);
        buttonDelete = itemView.findViewById(R.id.card_notification_button_delete);
        textViewTitle = itemView.findViewById(R.id.card_notification_title);
        textViewMessage = itemView.findViewById(R.id.card_notification_message);
    }

    @Override
    public void bindViewHolder(NotificationCardAdapter.CardViewHolder cardViewHolder,
                               @NotNull final Notification notification) {
        setNotificationCardStyle(notification.getType());
        textViewTitle.setText(notification.getTitle());
        textViewMessage.setText(notification.getMessage());
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonDeleteClicked(notification);
            }
        });
    }

    @RequiresInternetConnection
    private void onButtonDeleteClicked(final Notification notification) {
        if(FullScreenActivity.isNetworkAvailable(activity)) {
            NotificationDAO notificationDAO = DAOFactory.getNotificationDAO(activity);
            notificationDAO.deleteNotification(
                    notification.getReviewId(),
                    notification.getUserId(),
                    new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            notificationCardAdapter.removeNotification(notification);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            MotionToast.display(
                                    activity,
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    }
            );
        } else {
            MotionToast.display(
                    activity,
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

    private void setNotificationCardStyle(@NotNull String notificationType) {
        if(notificationType.equals("positive")) {
            notificationIcon.setImageResource(R.drawable.ic_success);
            notificationIconBackground.setBackground(ContextCompat.getDrawable(
                    activity,
                    R.drawable.card_notification_approved_background)
            );
        } else {
            notificationIcon.setImageResource(R.drawable.ic_error);
            notificationIconBackground.setBackground(ContextCompat.getDrawable(
                    activity,
                    R.drawable.card_notification_disapproved_background)
            );
        }
    }

}