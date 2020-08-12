package it.isw.cvmobile.views.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import com.scwang.wave.MultiWaveHeader;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.SignInPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.StartActivity;


@Completed
public class SignInFragment extends Fragment {

    private final StartActivity startActivity;
    private SignInPresenter signInPresenter;
    private MultiWaveHeader waves;
    private ImageView logo;
    private ImageView overlay;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextView textViewForgotPassword;
    private Button buttonSignIn;
    private Button buttonForgotPassword;



    public SignInFragment(StartActivity startActivity) {
        this.startActivity = startActivity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        waves = view.findViewById(R.id.fragment_sign_in_waves);
        textInputEmail = view.findViewById(R.id.fragment_sign_in_text_input_email_layout);
        textInputPassword = view.findViewById(R.id.fragment_sign_in_text_input_password_layout);
        buttonSignIn = view.findViewById(R.id.fragment_sign_in_button_sign_in);
        buttonForgotPassword = view.findViewById(R.id.fragment_sign_in_button_forgot_password);
        textViewForgotPassword = view.findViewById(R.id.fragment_sign_in_text_view_forgot_password);
        overlay = view.findViewById(R.id.fragment_sign_in_overlay);
        logo = view.findViewById(R.id.fragment_sign_in_logo);
        signInPresenter = new SignInPresenter(this);
        initializeUserInterface();
        listenToClickEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        buttonForgotPassword.setEnabled(true);
    }

    private void initializeUserInterface() {
        int screenHeight = startActivity.getScreenHeight();
        FullScreenActivity.setViewHeight(waves, screenHeight+100);
        FullScreenActivity.setViewMargins(waves, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(logo, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textInputEmail, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textInputPassword, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(buttonSignIn, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(buttonForgotPassword, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textViewForgotPassword, 0, screenHeight, 0, 0);
        final ViewPropertyAnimator wavesAnimator = waves.animate().translationY(-screenHeight/100f*90f).setDuration(1250);
        wavesAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                logo.animate().alpha(1).setDuration(2000);
                wavesAnimator.setListener(null);
            }
        });
        logo.animate().translationY(-screenHeight/100f*78f).setDuration(1500);
        textInputEmail.animate().translationY(-screenHeight/100f*52.5f).setDuration(1750);
        textInputPassword.animate().translationY(-screenHeight/100f*42f).setDuration(2000);
        buttonSignIn.animate().translationY(-screenHeight/100f*27f).setDuration(2250);
        buttonForgotPassword.animate().translationY(-screenHeight/100f*17f).setDuration(2500);
        textViewForgotPassword.animate().translationY(-screenHeight/100f*17f).setDuration(2500);
    }

    public void startClosingTransition(boolean ultimately, Animator.AnimatorListener animatorListener) {
        int screenHeight = startActivity.getScreenHeight();
        if(ultimately) {
            buttonSignIn.setEnabled(false);
            buttonForgotPassword.setEnabled(false);
            overlay.setEnabled(false);
            logo.animate().alpha(0.0f).setDuration(1000);
            buttonSignIn.animate().alpha(0.0f).setDuration(1000);
            textInputEmail.animate().alpha(0.0f).setDuration(1000);
            textInputPassword.animate().alpha(0.0f).setDuration(1000);
            buttonForgotPassword.animate().alpha(0.0f).setDuration(1000);
            textViewForgotPassword.animate().alpha(0.0f).setDuration(1000);
            ViewPropertyAnimator animator = waves.animate().translationY(-screenHeight-100).setDuration(1000);
            if(animatorListener != null) {
                animator.setListener(animatorListener);
            }
        } else {
            waves.animate().translationY(screenHeight/100f*90f).setDuration(2250);
            logo.animate().translationY(screenHeight/100f*78f).setDuration(2250);
            textInputEmail.animate().translationY(screenHeight/100f*52.5f).setDuration(2000);
            textInputPassword.animate().translationY(screenHeight/100f*42f).setDuration(1750);
            buttonSignIn.animate().translationY(screenHeight/100f*27f).setDuration(1500);
            buttonForgotPassword.animate().translationY(screenHeight/100f*17f).setDuration(1250);
            textViewForgotPassword.animate().translationY(screenHeight/100f*17f).setDuration(1250);
        }
    }

    private void listenToClickEvents() {
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInPresenter.onButtonSignInClicked();
            }
        });
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInPresenter.onButtonForgotPasswordClicked();
            }
        });
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInPresenter.onOverlayClicked();
            }
        });
        waves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInPresenter.onWavesClicked();
            }
        });
    }

    public void cleanTextInputErrors() {
        textInputEmail.setErrorEnabled(false);
        textInputPassword.setErrorEnabled(false);
    }

    public StartActivity getStartActivity() {
        return startActivity;
    }

    public String getEmail() {
        EditText passwordEditText = textInputEmail.getEditText();
        if(passwordEditText == null) return "";
        return passwordEditText.getText().toString();
    }

    public String getPassword() {
        EditText passwordEditText = textInputPassword.getEditText();
        if(passwordEditText == null) return "";
        return passwordEditText.getText().toString();
    }

    public void setEnabledButtonSignIn(boolean enabled) {
        buttonSignIn.setEnabled(enabled);
    }

    public void setEnabledForgotPassword(boolean enabled) {
        buttonForgotPassword.setEnabled(enabled);
    }

    public void setEnabledOverlay(boolean enabled) {
        overlay.setEnabled(enabled);
    }

    public void setTextInputEmailError(String error) {
        textInputEmail.setErrorEnabled(true);
        textInputEmail.setError(error);
    }

    public void setTextInputPasswordError(String error) {
        textInputPassword.setErrorEnabled(true);
        textInputPassword.setError(error);
    }

}






























