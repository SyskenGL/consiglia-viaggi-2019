package it.isw.cvmobile.views.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import com.eftimoff.androipathview.PathView;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.activities.OpeningPresenter;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class OpeningActivity extends FullScreenActivity {

    private OpeningPresenter openingPresenter;
    private View layout;
    private PathView logo;
    private TextView textViewAppName;
    private TextView poweredBy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        layout = findViewById(R.id.activity_opening);
        logo = findViewById(R.id.activity_opening_app_logo);
        textViewAppName = findViewById(R.id.activity_opening_app_name);
        poweredBy = findViewById(R.id.activity_opening_app_powered_by);
        openingPresenter = new OpeningPresenter(this);
        initializeUserInterface();
    }

    private void initializeUserInterface() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(FullScreenActivity.SYSTEM_UI_FLAG);
        AnimationDrawable gradientBackground = (AnimationDrawable) layout.getBackground();
        gradientBackground.setExitFadeDuration(2000);
        gradientBackground.setEnterFadeDuration(10);
        textViewAppName.animate().alpha(1).setDuration(2000);
        textViewAppName.animate().translationY(-175).setDuration(1500);
        poweredBy.animate().alpha(1).setDuration(2000);
        poweredBy.animate().translationY(-175).setDuration(2000);
        gradientBackground.start();
        logo.setFillAfter(true);
        PathView.AnimatorBuilder animatorBuilder = logo.getPathAnimator();
        animatorBuilder.delay(100);
        animatorBuilder.duration(2500);
        animatorBuilder.interpolator(new AccelerateDecelerateInterpolator());
        animatorBuilder.start();
        animatorBuilder.listenerEnd(new PathView.AnimatorBuilder.ListenerEnd() {
            @Override
            public void onAnimationEnd() {
                openingPresenter.onPresentationEnd();
            }
        });
    }

}
