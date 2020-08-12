package it.isw.cvmobile.presenters.fragments;

import android.content.res.Resources;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.UserDAO;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ForgotPasswordRecoveryFragment;
import it.isw.cvmobile.views.fragments.ForgotPasswordVerificationFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class ForgotPasswordVerificationPresenter {

    private final ForgotPasswordVerificationFragment forgotPasswordVerificationFragment;



    public ForgotPasswordVerificationPresenter(ForgotPasswordVerificationFragment forgotPasswordVerificationFragment) {
        this.forgotPasswordVerificationFragment = forgotPasswordVerificationFragment;
    }

    public void onButtonSendCodeClicked() {
        Resources resources = forgotPasswordVerificationFragment.getStartActivity().getResources();
        forgotPasswordVerificationFragment.setEnabledButtonSendCode(false);
        String email = forgotPasswordVerificationFragment.getEmail();
        if(email.length() == 0) {
            forgotPasswordVerificationFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_forgot_password_verification_enter_email)
            );
            forgotPasswordVerificationFragment.setEnabledButtonSendCode(true);
        } else if(!isValidEmail(email)) {
            forgotPasswordVerificationFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_forgot_password_verification_invalid_email)
            );
            forgotPasswordVerificationFragment.setEnabledButtonSendCode(true);
        } else {
            checkIfUserExists(email);
        }
    }

    private void openForgotPasswordRecoveryFragment(String email) {
        ForgotPasswordRecoveryFragment forgotPasswordRecoveryFragment =
                new ForgotPasswordRecoveryFragment(email, forgotPasswordVerificationFragment.getStartActivity());
        FragmentManager fragmentManager = forgotPasswordVerificationFragment.getStartActivity().getSupportFragmentManager();
        forgotPasswordRecoveryFragment.show(fragmentManager, "FORGOT_PASSWORD_RECOVERY");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void handleUserValidationSuccess(final String email, @NotNull JsonObject results) {
        Resources resources = forgotPasswordVerificationFragment.getStartActivity().getResources();
        if (results.get("data").getAsString().equals("true")) {
            forgotPasswordVerificationFragment.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openForgotPasswordRecoveryFragment(email);
                }
            }, 500);
        } else {
            forgotPasswordVerificationFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_forgot_password_verification_email_not_exists)
            );
            forgotPasswordVerificationFragment.setEnabledButtonSendCode(true);
        }
    }

    private void handleUserValidationFailure(@NotNull Exception exception) {
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    forgotPasswordVerificationFragment.getStartActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        } else {
            MotionToast.display(
                    forgotPasswordVerificationFragment.getStartActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
        forgotPasswordVerificationFragment.setEnabledButtonSendCode(true);
    }

    @RequiresInternetConnection
    private void checkIfUserExists(final String email) {
        if(FullScreenActivity.isNetworkAvailable(forgotPasswordVerificationFragment.getStartActivity())) {
            UserDAO userDAO = DAOFactory.getUserDAO(forgotPasswordVerificationFragment.getStartActivity());
            userDAO.isValidUser(email, new LambdaResultsHandler() {

                @Override
                public void onSuccess(JsonObject results) {
                    handleUserValidationSuccess(email, results);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleUserValidationFailure(exception);
                }

            });
        } else {
            handleUserValidationFailure(new NoInternetConnectionException());
        }
    }

}
