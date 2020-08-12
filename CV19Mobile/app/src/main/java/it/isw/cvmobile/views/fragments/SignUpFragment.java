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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import com.scwang.wave.MultiWaveHeader;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.SignUpPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.StartActivity;


@Completed
public class SignUpFragment extends Fragment {

    private final StartActivity startActivity;
    private SignUpPresenter signUpPresenter;
    private ImageView overlay;
    private ImageView logo;
    private MultiWaveHeader waves;
    private TextInputLayout textInputName;
    private TextInputLayout textInputSurname;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputNickname;
    private TextInputLayout textInputPassword;
    private Button buttonSignUp;



    public SignUpFragment(StartActivity startActivity) {
        this.startActivity = startActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        overlay = view.findViewById(R.id.fragment_sign_up_overlay);
        waves = view.findViewById(R.id.fragment_sign_up_waves);
        textInputName = view.findViewById(R.id.fragment_sign_up_text_input_name_layout);
        textInputSurname = view.findViewById(R.id.fragment_sign_up_text_input_surname_layout);
        textInputEmail = view.findViewById(R.id.fragment_sign_up_text_input_email_layout);
        textInputNickname = view.findViewById(R.id.fragment_sign_up_text_input_nickname_layout);
        textInputPassword = view.findViewById(R.id.fragment_sign_up_text_input_password_layout);
        buttonSignUp = view.findViewById(R.id.fragment_sign_up_button_sign_up);
        logo = view.findViewById(R.id.fragment_sign_up_logo);
        signUpPresenter = new SignUpPresenter(this);
        initializeUserInterface();
        listenToClickEvents();
    }

    private void initializeUserInterface() {
        int screenHeight = startActivity.getScreenHeight();
        FullScreenActivity.setViewMargins(waves, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(logo, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textInputEmail, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textInputPassword, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textInputNickname, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textInputName, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(textInputSurname, 0, screenHeight, 0, 0);
        FullScreenActivity.setViewMargins(buttonSignUp, 0, screenHeight, 0, 0);
        final ViewPropertyAnimator wavesAnimator = waves.animate().translationY(-screenHeight/100f*90f).setDuration(1250);
        wavesAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                logo.animate().alpha(1).setDuration(2000);
                wavesAnimator.setListener(null);
            }
        });
        logo.animate().translationY(-screenHeight/100f*82f).setDuration(1250);
        textInputName.animate().translationY(-screenHeight/100f*66f).setDuration(1500);
        textInputSurname.animate().translationY(-screenHeight/100f*55.5f).setDuration(1750);
        textInputNickname.animate().translationY(-screenHeight/100f*45f).setDuration(2000);
        textInputEmail.animate().translationY(-screenHeight/100f*34.5f).setDuration(2250);
        textInputPassword.animate().translationY(-screenHeight/100f*24f).setDuration(2500);
        buttonSignUp.animate().translationY(-screenHeight/100f*12f).setDuration(2750);
    }

    public void startClosingTransition() {
        int screenHeight = startActivity.getScreenHeight();
        waves.animate().translationY(screenHeight/100f*90f).setDuration(3000);
        logo.animate().translationY(screenHeight/100f*82f).setDuration(2750);
        textInputName.animate().translationY(screenHeight/100f*66f).setDuration(2500);
        textInputSurname.animate().translationY(screenHeight/100f*55.5f).setDuration(2250);
        textInputNickname.animate().translationY(screenHeight/100f*45f).setDuration(2000);
        textInputEmail.animate().translationY(screenHeight/100f*34.5f).setDuration(1750);
        textInputPassword.animate().translationY(screenHeight/100f*24f).setDuration(1500);
        buttonSignUp.animate().translationY(screenHeight/100f*12f).setDuration(1250);
    }

    private void listenToClickEvents() {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpPresenter.onButtonSignUpClicked();
            }
        });
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpPresenter.onOverlayClicked();
            }
        });
        waves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpPresenter.onWavesClicked();
            }
        });
    }

    public void cleanTextInputErrors() {
        textInputName.setErrorEnabled(false);
        textInputSurname.setErrorEnabled(false);
        textInputEmail.setErrorEnabled(false);
        textInputNickname.setErrorEnabled(false);
        textInputPassword.setErrorEnabled(false);
    }

    public StartActivity getStartActivity() {
        return startActivity;
    }

    public String getName() {
        EditText nameEditText = textInputName.getEditText();
        if(nameEditText == null) return "";
        return nameEditText.getText().toString();
    }

    public String getSurname() {
        EditText surnameEditText = textInputSurname.getEditText();
        if(surnameEditText == null) return "";
        return surnameEditText.getText().toString();
    }

    public String getEmail() {
        EditText emailEditText = textInputEmail.getEditText();
        if(emailEditText == null) return "";
        return emailEditText.getText().toString();
    }

    public String getNickname() {
        EditText nicknameEditText = textInputNickname.getEditText();
        if(nicknameEditText == null) return "";
        return nicknameEditText.getText().toString();
    }

    public String getPassword() {
        EditText passwordEditText = textInputPassword.getEditText();
        if(passwordEditText == null) return "";
        return passwordEditText.getText().toString();
    }

    public void setEnabledOverlay(boolean enabled) {
        overlay.setEnabled(enabled);
    }

    public void setEnabledButtonSignUp(boolean enabled) {
        buttonSignUp.setEnabled(enabled);
    }

    public void setTextInputNameError(String error) {
        textInputName.setErrorEnabled(true);
        textInputName.setError(error);
    }

    public void setTextInputSurnameError(String error) {
        textInputSurname.setErrorEnabled(true);
        textInputSurname.setError(error);
    }

    public void setTextInputEmailError(String error) {
        textInputEmail.setErrorEnabled(true);
        textInputEmail.setError(error);
    }

    public void setTextInputNicknameError(String error) {
        textInputNickname.setErrorEnabled(true);
        textInputNickname.setError(error);
    }

    public void setTextInputPasswordError(String error) {
        textInputPassword.setErrorEnabled(true);
        textInputPassword.setError(error);
    }

}
