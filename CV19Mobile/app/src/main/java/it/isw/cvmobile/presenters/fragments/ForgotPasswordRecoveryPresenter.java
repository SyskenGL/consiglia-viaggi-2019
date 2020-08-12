package it.isw.cvmobile.presenters.fragments;

import android.content.res.Resources;
import android.os.Handler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidentityprovider.model.LimitExceededException;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.Cognito;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ForgotPasswordRecoveryFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class ForgotPasswordRecoveryPresenter {

    private final ForgotPasswordRecoveryFragment forgotPasswordRecoveryFragment;
    private ForgotPasswordContinuation forgotPasswordContinuation;



    public ForgotPasswordRecoveryPresenter(ForgotPasswordRecoveryFragment forgotPasswordRecoveryFragment) {
        this.forgotPasswordRecoveryFragment = forgotPasswordRecoveryFragment;
        recoverAccount();
    }

    public void onEditTextPasswordTextChanged() {
        String password = forgotPasswordRecoveryFragment.getPassword();
        if(password.length() >= 6) {
            forgotPasswordRecoveryFragment.setEnabledCodeInputView(true);
        } else {
            forgotPasswordRecoveryFragment.setEnabledCodeInputView(false);
        }
    }

    public void onCodeCompleted() {
        String code = forgotPasswordRecoveryFragment.getCode();
        String password = forgotPasswordRecoveryFragment.getPassword();
        if(forgotPasswordContinuation != null) {
            forgotPasswordContinuation.setVerificationCode(code);
            forgotPasswordContinuation.setPassword(password);
            forgotPasswordContinuation.continueTask();
        }
    }

    private void handleRecoverAccountFailure(@NotNull Exception exception) {
        Resources resources = forgotPasswordRecoveryFragment.getStartActivity().getResources();
        forgotPasswordRecoveryFragment.cleanTextInputPasswordError();
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    forgotPasswordRecoveryFragment.getStartActivity(),
                    R.string.toast_error_network_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
            forgotPasswordRecoveryFragment.dismiss();
        } else if(exception.getClass() == CodeMismatchException.class) {
            forgotPasswordRecoveryFragment.setIncorrectCodeError(
                    resources.getString(R.string.fragment_forgot_password_recovery_incorrect_code)
            );
            forgotPasswordRecoveryFragment.cleanCodeInputViewError();
        } else if(exception.getClass() == InvalidPasswordException.class){
            forgotPasswordRecoveryFragment.setTextInputPasswordError(
                    resources.getString(R.string.fragment_forgot_password_recovery_invalid_pass)
            );
            forgotPasswordRecoveryFragment.setIncorrectCodeError(
                    resources.getString(R.string.fragment_forgot_password_recovery_enter_again)
            );
            forgotPasswordRecoveryFragment.cleanCodeInputViewError();
        } else {
            if(exception.getClass() == LimitExceededException.class) {
                MotionToast.display(
                        forgotPasswordRecoveryFragment.getStartActivity(),
                        R.string.toast_info_limit_exceeded,
                        MotionToastType.INFO_MOTION_TOAST
                );
            } else {
                MotionToast.display(
                        forgotPasswordRecoveryFragment.getStartActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
            forgotPasswordRecoveryFragment.dismiss();
        }
    }

    @RequiresInternetConnection
    private void recoverAccount() {
        final Resources resources = forgotPasswordRecoveryFragment.getStartActivity().getResources();
        if(FullScreenActivity.isNetworkAvailable(forgotPasswordRecoveryFragment.getStartActivity())) {
            String email = forgotPasswordRecoveryFragment.getEmail();
            Cognito cognito = Cognito.getInstance(forgotPasswordRecoveryFragment.getStartActivity());
            CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
            CognitoUser user = cognitoUserPool.getUser(email);
            forgotPasswordRecoveryFragment.setEnabledCodeInputView(false);
            user.forgotPasswordInBackground(new ForgotPasswordHandler() {

                @Override
                public void onSuccess() {
                    forgotPasswordRecoveryFragment.setCorrectCodeNotification(
                            resources.getString(R.string.fragment_forgot_password_recovery_correct_code)
                    );
                    MotionToast.display(
                            forgotPasswordRecoveryFragment.getStartActivity(),
                            R.string.toast_success_password_changed,
                            MotionToastType.SUCCESS_MOTION_TOAST
                    );
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            forgotPasswordRecoveryFragment.dismiss();
                        }
                    }, 1500);
                }

                @Override
                public void getResetCode(ForgotPasswordContinuation continuation) {
                    forgotPasswordContinuation = continuation;
                }

                @Override
                public void onFailure(Exception exception) {
                    handleRecoverAccountFailure(exception);
                }

            });
        } else {
            handleRecoverAccountFailure(new NoInternetConnectionException());
        }
    }

}
