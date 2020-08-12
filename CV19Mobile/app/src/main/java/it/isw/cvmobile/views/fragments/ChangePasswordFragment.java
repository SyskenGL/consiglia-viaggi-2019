package it.isw.cvmobile.views.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ChangePasswordPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.NavigationActivity;


@Completed
public class ChangePasswordFragment extends DialogFragment {

    private final NavigationActivity navigationActivity;
    private ChangePasswordPresenter changePasswordPresenter;
    private TextInputLayout textInputOldPassword;
    private TextInputLayout textInputNewPassword;
    private Button buttonSubmit;



    public ChangePasswordFragment(NavigationActivity navigationActivity) {
        this.navigationActivity = navigationActivity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textInputOldPassword = view.findViewById(R.id.fragment_change_password_text_input_old_password_layout);
        textInputNewPassword = view.findViewById(R.id.fragment_change_password_text_input_new_password_layout);
        buttonSubmit = view.findViewById(R.id.fragment_change_password_button_submit);
        changePasswordPresenter = new ChangePasswordPresenter(this);
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
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordPresenter.onButtonSubmitClicked();
            }
        });
    }

    public void cleanTextInputPasswordErrors() {
        textInputOldPassword.setErrorEnabled(false);
        textInputNewPassword.setErrorEnabled(false);
    }

    public String getOldPassword() {
        EditText oldPasswordEditText = textInputOldPassword.getEditText();
        if(oldPasswordEditText == null) return "";
        return oldPasswordEditText.getText().toString();
    }

    public String getNewPassword() {
        EditText newPasswordEditText = textInputNewPassword.getEditText();
        if(newPasswordEditText == null) return "";
        return newPasswordEditText.getText().toString();
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public void setEnabledButtonSubmit(boolean enabled) {
        buttonSubmit.setEnabled(enabled);
    }

    public void setTextInputOldPasswordError(String error) {
        textInputOldPassword.setErrorEnabled(true);
        textInputOldPassword.setError(error);
    }

    public void setTextInputNewPasswordError(String error) {
        textInputNewPassword.setErrorEnabled(true);
        textInputNewPassword.setError(error);
    }

}

















