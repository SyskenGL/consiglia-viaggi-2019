package it.isw.cvmobile.presenters.activities;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.google.android.libraries.places.api.Places;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.NotificationDAO;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.Cognito;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.services.GPSTracker;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.activities.StartActivity;
import it.isw.cvmobile.views.fragments.FavoritesFragment;
import it.isw.cvmobile.views.fragments.HistoryFragment;
import it.isw.cvmobile.views.fragments.HomeFragment;
import it.isw.cvmobile.views.fragments.NotificationsFragment;
import it.isw.cvmobile.views.fragments.ProfileFragment;
import it.isw.cvmobile.views.fragments.ReviewsFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


@Completed
public class NavigationPresenter {

    private static final String CHANNEL_ID = "CV19Mobile_CH_0";
    private static final String API_KEY = "GOOGLE_API_KEY";
    private static final Locale LOCALE = Locale.ITALIAN;

    private final NavigationActivity navigationActivity;
    private boolean userNotified;
    private Fragment currentFragment;



    public NavigationPresenter(final NavigationActivity navigationActivity) {
        Places.initialize(navigationActivity, API_KEY, LOCALE);
        this.navigationActivity = navigationActivity;
        openFragment(currentFragment = new HomeFragment(navigationActivity));
        if(User.isLoggedIn()) {
            createNotificationChannel();
            listenToNotifications();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onMenuDragged(false);
            }
        }, 500);
    }

    public void onUserInterfaceInitialization() {
        if (User.isLoggedIn()) {
            navigationActivity.setUserLoggedInNavigationMode();
        } else {
            navigationActivity.setUserNotLoggedInNavigationMode();
        }
    }

    public void onSideNavigationMenuAdapterItemSelected(int position) {
        closeFragment(currentFragment);
        navigationActivity.closeSideNavigationMenu();
        if (User.isLoggedIn()) {
            if(currentFragment instanceof NotificationsFragment) {
                userNotified = false;
            }
            switch (position) {
                case NavigationActivity.BUTTON_HOME:
                    currentFragment = new HomeFragment(navigationActivity);
                    break;
                case NavigationActivity.BUTTON_MY_PROFILE:
                    currentFragment = new ProfileFragment(navigationActivity);
                    break;
                case NavigationActivity.BUTTON_MY_FAVORITES:
                    currentFragment = new FavoritesFragment(navigationActivity);
                    break;
                case NavigationActivity.BUTTON_MY_REVIEWS:
                    currentFragment = new ReviewsFragment(navigationActivity);
                    break;
                case NavigationActivity.BUTTON_MY_NOTIFICATIONS:
                    currentFragment = new NotificationsFragment(navigationActivity);
                    break;
                case NavigationActivity.BUTTON_MY_HISTORY:
                    currentFragment = new HistoryFragment(navigationActivity);
                    break;
                case NavigationActivity.BUTTON_SIGN_OUT + 1:
                    onSignOutClicked();
                    return;
            }
            openFragment(currentFragment);
        } else {
            switch (position) {
                case NavigationActivity.BUTTON_HOME:
                    currentFragment = new HomeFragment(navigationActivity);
                    break;
                case NavigationActivity.BUTTON_SIGN_IN + 1:
                    onSignInClicked();
                    return;
            }
            openFragment(currentFragment);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onMenuDragged(false);
            }
        }, 500);
    }

    public void onMenuDragged(boolean isMenuOpened) {
        if(!isMenuOpened) {
            if(currentFragment.getView() != null) {
                navigationActivity.setSideNavigationBlurryBackground(
                        currentFragment.getView(),
                        15,
                        15,
                        Color.argb(50, 255, 255, 255)
                );
            }
        }
    }

    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == GPSTracker.GPS_REQUEST_CODE) {
            MotionToast.display(
                    navigationActivity,
                    R.string.toast_success_permission_granted,
                    MotionToastType.SUCCESS_MOTION_TOAST
            );
        }
    }

    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(requestCode == GPSTracker.GPS_REQUEST_CODE) {
            if(EasyPermissions.somePermissionPermanentlyDenied(navigationActivity, perms)) {
                new AppSettingsDialog.Builder(navigationActivity).build().show();
            } else {
                MotionToast.display(
                        navigationActivity,
                        R.string.toast_warning_permission_denied,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GPSTracker.RESOLVABLE_API_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_CANCELED) {
                MotionToast.display(
                        navigationActivity,
                        R.string.toast_warning_gps_disabled,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            } else if(resultCode == Activity.RESULT_OK) {
                MotionToast.display(
                        navigationActivity,
                        R.string.toast_success_gps_enabled,
                        MotionToastType.SUCCESS_MOTION_TOAST
                );
            }
        }
    }

    private void onSignOutClicked() {
        Cognito cognito = Cognito.getInstance(navigationActivity);
        CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
        CognitoUser cognitoUser = cognitoUserPool.getCurrentUser();
        cognitoUser.signOut();
        User.finalizeUser();
        Intent intent = new Intent(navigationActivity, StartActivity.class);
        navigationActivity.startActivity(intent);
        navigationActivity.finish();
    }

    private void onSignInClicked() {
        Intent intent = new Intent(navigationActivity, StartActivity.class);
        intent.putExtra("SIGN_IN", true);
        navigationActivity.startActivity(intent);
        navigationActivity.finish();
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = navigationActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_navigation_fragment_container, fragment).commit();
    }

    private void closeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = navigationActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    private void handleNewNotificationReceived(int records) {
        navigationActivity.setNotificationAlertIcon();
        if(!userNotified) {
            userNotified = true;
            Spannable title = new SpannableString("Hey, I have news for you!");
            title.setSpan(
                    new StyleSpan(android.graphics.Typeface.BOLD),
                    0,
                    title.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            sendNotification(
                    title,
                    "You have received " + records + " new notifications, check the Notifications section."
            );
        }
    }

    private void createNotificationChannel() {
        CharSequence name = navigationActivity.getString(R.string.notification_channel_name);
        String description = navigationActivity.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = navigationActivity.getSystemService(NotificationManager.class);
        if(notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(CharSequence title, CharSequence message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(navigationActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.cv_app_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(navigationActivity);
        notificationManager.notify(0, builder.build());
    }

    @RequiresInternetConnection
    private void listenToNotifications() {
        if(User.isLoggedIn()) {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    NotificationDAO notificationDAO = DAOFactory.getNotificationDAO(navigationActivity);
                    notificationDAO.getNotifications(
                            User.getInstance().getUserId(),
                            "unmarked",
                            0,
                            0,
                            new LambdaResultsHandler() {

                                @Override
                                public void onSuccess(JsonObject results) {
                                    int records = results.get("data").getAsJsonObject().get("records").getAsInt();
                                    if(records > 0) {
                                        handleNewNotificationReceived(records);
                                    }
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    // ...
                                }

                            });
                }
            }, 10, 300, TimeUnit.SECONDS);
        }
    }

}