package it.isw.cvmobile.views.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ForgotPasswordVerificationPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.StartActivity;


@Completed
public class ForgotPasswordVerificationFragment extends DialogFragment {

    private final StartActivity startActivity;
    private ForgotPasswordVerificationPresenter forgotPasswordVerificationPresenter;
    private FloatingActionButton buttonSendCode;
    private TextInputLayout textInputEmail;



    public ForgotPasswordVerificationFragment(StartActivity startActivity) {
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
        return inflater.inflate(R.layout.fragment_forgot_password_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSendCode = view.findViewById(R.id.fragment_forgot_password_verification_button_send_code);
        textInputEmail = view.findViewById(R.id.fragment_forgot_password_verification_text_input_email_layout);
        forgotPasswordVerificationPresenter = new ForgotPasswordVerificationPresenter(this);
        initializeUserInterface();
        listenToClickEvents();
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
    }

    private void listenToClickEvents() {
        buttonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordVerificationPresenter.onButtonSendCodeClicked();
            }
        });
    }

    public String getEmail() {
        EditText passwordEditText = textInputEmail.getEditText();
        if(passwordEditText == null) return "";
        return passwordEditText.getText().toString();
    }

    public StartActivity getStartActivity() {
        return startActivity;
    }

    public void setEnabledButtonSendCode(boolean enabled) {
        buttonSendCode.setEnabled(enabled);
    }

    public void setTextInputEmailError(String error) {
        textInputEmail.setErrorEnabled(true);
        textInputEmail.setError(error);
    }

}
