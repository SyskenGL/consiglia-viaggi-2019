package it.isw.cvmobile.views.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.stone.vega.library.VegaLayoutManager;
import com.wang.avi.AVLoadingIndicatorView;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.NotificationsPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.widgets.scrollview.cards.notification.NotificationCardAdapter;
import jp.wasabeef.blurry.Blurry;


@Completed
public class NotificationsFragment extends Fragment {

    private final NavigationActivity navigationActivity;
    private NotificationsPresenter notificationsPresenter;
    private ImageView buttonRefresh;
    private ImageView background;
    private View noResultsView;
    private View errorView;
    private RecyclerView notificationsRecyclerView;
    private AVLoadingIndicatorView loadingSpinner;



    public NotificationsFragment(NavigationActivity navigationActivity) {
        this.navigationActivity = navigationActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationsRecyclerView = view.findViewById(R.id.fragment_notification_vertical_recycler_view);
        background = view.findViewById(R.id.fragment_notifications_background);
        loadingSpinner = view.findViewById(R.id.fragment_notifications_loading_spinner);
        noResultsView = view.findViewById(R.id.fragment_notifications_no_results);
        errorView = view.findViewById(R.id.fragment_notifications_error);
        buttonRefresh = view.findViewById(R.id.fragment_notifications_refresh_button);
        notificationsRecyclerView.setLayoutManager(new VegaLayoutManager());
        notificationsPresenter =  new NotificationsPresenter(this);
        navigationActivity.setNotificationDefaultIcon();
        initializeUserInterface();
        listenToClickEvents();
    }

    private void initializeUserInterface() {
        Bitmap bitmap = BitmapFactory.decodeResource(
                navigationActivity.getResources(),
                R.drawable.background_home_two
        );
        if(bitmap != null) {
            Blurry.Composer composer = Blurry.with(getContext());
            composer.radius(25);
            composer.sampling(25);
            composer.color(Color.argb(70, 255, 255, 255));
            composer.async().from(bitmap).into(background);
        }
    }

    private void listenToClickEvents() {
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsPresenter.onButtonRefreshClicked();
            }
        });
    }

    public void startButtonRefreshSpinAnimation() {
        RotateAnimation rotate = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );
        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        buttonRefresh.startAnimation(rotate);
    }

    public void clearButtonRefreshAnimation() {
        buttonRefresh.clearAnimation();
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public void setLoadingSpinnerVisibility(boolean visibility) {
        if(visibility) {
            loadingSpinner.setVisibility(View.VISIBLE);
        } else {
            loadingSpinner.setVisibility(View.GONE);
        }
    }

    public void setNoResultsViewVisibility(boolean visibility) {
        if(visibility) {
            noResultsView.setVisibility(View.VISIBLE);
        } else {
            noResultsView.setVisibility(View.GONE);
        }
    }

    public void setErrorViewVisibility(boolean visibility) {
        if(visibility) {
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    public void setNotificationsRecyclerViewAdapter(NotificationCardAdapter notificationCardAdapter) {
        notificationsRecyclerView.setAdapter(notificationCardAdapter);
    }

}