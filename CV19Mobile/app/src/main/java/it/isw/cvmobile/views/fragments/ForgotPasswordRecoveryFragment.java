package it.isw.cvmobile.views.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raycoarana.codeinputview.CodeInputView;
import com.raycoarana.codeinputview.OnCodeCompleteListener;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ForgotPasswordRecoveryPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.StartActivity;


@Completed
public class ForgotPasswordRecoveryFragment extends DialogFragment {

    private final StartActivity startActivity;
    private ForgotPasswordRecoveryPresenter forgotPasswordRecoveryPresenter;
    private CodeInputView codeInputView;
    private TextInputLayout textInputPassword;
    private TextInputEditText ediTextPassword;
    private TextView textViewEmail;
    private String email;



    public ForgotPasswordRecoveryFragment(String email, StartActivity startActivity){
        this.email = email;
        this.startActivity = startActivity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password_recovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        codeInputView = view.findViewById(R.id.fragment_forgot_password_recovery_code_input);
        textInputPassword = view.findViewById(R.id.fragment_forgot_password_recovery_text_input_password_layout);
        ediTextPassword = view.findViewById(R.id.fragment_forgot_password_recovery_text_input_password);
        textViewEmail = view.findViewById(R.id.fragment_forgot_password_recovery_subtitle_section_third);
        forgotPasswordRecoveryPresenter = new ForgotPasswordRecoveryPresenter(this);
        initializeUserInterface();
        listenToChangeTextEvents();
        listenToCompleteEvents();
    }

    private void initializeUserInterface() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if(getDialog() != null) {
            Window window = getDialog().getWindow();
            if(window != null) {
                window.getAttributes().windowAnimations = R.style.DialogFragmentAnimation;
            }
        }
        textViewEmail.setText(email);
    }

    private void listenToChangeTextEvents() {
        ediTextPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ...
            }

            @Override
            public void afterTextChanged(Editable s) {
                forgotPasswordRecoveryPresenter.onEditTextPasswordTextChanged();
            }

        });
    }

    private void listenToCompleteEvents() {
        codeInputView.addOnCompleteListener(new OnCodeCompleteListener() {
            @Override
            public void onCompleted(String code) {
                forgotPasswordRecoveryPresenter.onCodeCompleted();
            }
        });
    }

    public void cleanTextInputPasswordError() {
        textInputPassword.setErrorEnabled(false);
    }

    public void cleanCodeInputViewError() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                codeInputView.clearError();
                codeInputView.setCode("");
                codeInputView.setEditable(true);
                codeInputView.setShowKeyboard(true);
            }
        }, 1500);
    }

    public String getPassword() {
        EditText passwordEditText = textInputPassword.getEditText();
        if(passwordEditText == null) return "";
        return passwordEditText.getText().toString();
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return codeInputView.getCode();
    }

    public StartActivity getStartActivity() {
        return startActivity;
    }

    public void setIncorrectCodeError(String message) {
        codeInputView.setError(message);
    }

    public void setCorrectCodeNotification(String message) {
        codeInputView.setErrorColor(R.color.livelyGreen, null);
        codeInputView.setErrorTextColor(R.color.livelyGreen, null);
        codeInputView.setError(message);
    }

    public void setTextInputPasswordError(String error) {
        textInputPassword.setErrorEnabled(true);
        textInputPassword.setError(error);
    }

    public void setEnabledCodeInputView(boolean enabled) {
        codeInputView.setEditable(enabled);
    }

}
