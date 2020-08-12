package it.isw.cvmobile.views.activities;

import android.animation.Animator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import com.scwang.wave.MultiWaveHeader;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.activities.StartPresenter;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class  StartActivity extends FullScreenActivity {

    private StartPresenter startPresenter;
    private Button buttonSignUp;
    private Button buttonSignIn;
    private Button buttonSkip;
    private MultiWaveHeader waves;
    private ImageView sun;
    private View layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        buttonSignIn = findViewById(R.id.activity_start_button_sign_in);
        buttonSignUp = findViewById(R.id.activity_start_button_sign_up);
        buttonSkip = findViewById(R.id.activity_start_button_skip);
        waves = findViewById(R.id.activity_start_waves);
        sun = findViewById(R.id.activity_start_sun);
        layout = findViewById(R.id.activity_start);
        if(getIntent().getBooleanExtra("SIGN_IN", false)) {
            startPresenter = new StartPresenter(this, true);
        } else {
            startPresenter = new StartPresenter(this, false);
        }
        initializeUserInterface();
        listenToClickEvents();
    }

    private void initializeUserInterface() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(FullScreenActivity.SYSTEM_UI_FLAG);
        setViewHeight(waves, getScreenHeight()+200);
        setViewMargins(waves, 0, getScreenHeight(), 0, 0);
        buttonSignIn.setAlpha(0.0f);
        buttonSignUp.setAlpha(0.0f);
        buttonSkip.setAlpha(0.0f);
        buttonSignIn.animate().alpha(1.0f).setDuration(2000);
        buttonSignUp.animate().alpha(1.0f).setDuration(2500);
        buttonSkip.animate().alpha(1.0f).setDuration(3000);
        AnimationDrawable gradientBackground = (AnimationDrawable) layout.getBackground();
        gradientBackground.setExitFadeDuration(2000);
        gradientBackground.setEnterFadeDuration(10);
        gradientBackground.start();
        sun.animate().setDuration(5000).translationY(getScreenHeight()/100f*57f);
    }

    private void listenToClickEvents() {
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPresenter.onButtonSignInClicked();
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPresenter.onButtonSignUpClicked();
            }
        });
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPresenter.onButtonSkipClicked();
            }
        });
    }

    public void startClosingTransition(boolean ultimately, Animator.AnimatorListener animatorListener) {
        buttonSignIn.setEnabled(false);
        buttonSignUp.setEnabled(false);
        buttonSkip.setEnabled(false);
        buttonSignIn.animate().alpha(0.0f).setDuration(1000);
        buttonSignUp.animate().alpha(0.0f).setDuration(750);
        buttonSkip.animate().alpha(0.0f).setDuration(500);
        if(ultimately) {
            ViewPropertyAnimator animator = waves.animate().translationY(-getScreenHeight()-200).setDuration(1500);
            if(animatorListener != null) {
                animator.setListener(animatorListener);
            }
        }
    }

    public void startOpeningTransition() {
        buttonSignIn.setEnabled(true);
        buttonSignUp.setEnabled(true);
        buttonSkip.setEnabled(true);
        buttonSignIn.animate().alpha(1.0f).setDuration(2000);
        buttonSignUp.animate().alpha(1.0f).setDuration(2500);
        buttonSkip.animate().alpha(1.0f).setDuration(3000);
    }

}