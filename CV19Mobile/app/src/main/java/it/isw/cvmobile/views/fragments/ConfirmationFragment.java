package it.isw.cvmobile.views.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.raycoarana.codeinputview.CodeInputView;
import com.raycoarana.codeinputview.OnCodeCompleteListener;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.ConfirmationPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.StartActivity;


@Completed
public class ConfirmationFragment extends DialogFragment {

    private final StartActivity startActivity;
    private ConfirmationPresenter confirmationPresenter;
    private Fragment parent;
    private Button buttonResendCode;
    private CodeInputView codeInputView;
    private String email;



    public ConfirmationFragment(String email, StartActivity startActivity, Fragment parent) {
        this.email = email;
        this.startActivity = startActivity;
        this.parent = parent;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonResendCode = view.findViewById(R.id.fragment_confirmation_button_resend);
        codeInputView = view.findViewById(R.id.fragment_confirmation_code_input);
        TextView textViewEmail = view.findViewById(R.id.fragment_confirmation_text_view_email);
        textViewEmail.setText(email);
        confirmationPresenter = new ConfirmationPresenter(this);
        initializeUserInterface();
        listenToClickEvents();
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
    }

    private void listenToClickEvents() {
        buttonResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationPresenter.onButtonResendCodeClicked();
            }
        });
    }

    private void listenToCompleteEvents() {
        codeInputView.addOnCompleteListener(new OnCodeCompleteListener() {
            @Override
            public void onCompleted(String code) {
                confirmationPresenter.onCodeCompleted();
            }
        });
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

    public StartActivity getStartActivity() {
        return startActivity;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return codeInputView.getCode();
    }

    public Fragment getParent() {
        return parent;
    }

    public void setIncorrectCodeError(String message) {
        codeInputView.setError(message);
    }

    public void setCorrectCodeNotification(String message) {
        codeInputView.setErrorColor(R.color.livelyGreen, null);
        codeInputView.setErrorTextColor(R.color.livelyGreen, null);
        codeInputView.setError(message);
    }

}
