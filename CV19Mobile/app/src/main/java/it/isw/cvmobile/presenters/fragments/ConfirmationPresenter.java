package it.isw.cvmobile.presenters.fragments;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.Cognito;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ConfirmationFragment;
import it.isw.cvmobile.views.fragments.SignInFragment;
import it.isw.cvmobile.views.fragments.SignUpFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class ConfirmationPresenter {

    private final ConfirmationFragment confirmationFragment;



    public ConfirmationPresenter(ConfirmationFragment confirmationFragment) {
        this.confirmationFragment = confirmationFragment;
    }

    public void onCodeCompleted() {
        confirmUser(confirmationFragment.getEmail(), confirmationFragment.getCode());
    }

    public void onButtonResendCodeClicked() {
        String email = confirmationFragment.getEmail();
        Cognito cognito = Cognito.getInstance(confirmationFragment.getStartActivity());
        CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
        CognitoUser cognitoUser = cognitoUserPool.getUser(email);
        new ResendCodeTask(confirmationFragment).execute(cognitoUser);
    }

    private void closeSignUpFragment(@NotNull SignUpFragment signUpFragment) {
        signUpFragment.startClosingTransition();
        signUpFragment.getStartActivity().startOpeningTransition();
        FragmentManager fragmentManager = signUpFragment.getStartActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.remove(signUpFragment).commit();
    }

    private void openSignInFragment() {
        SignInFragment signInFragment = new SignInFragment(confirmationFragment.getStartActivity());
        FragmentManager fragmentManager = confirmationFragment.getStartActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.add(R.id.activity_start_fragment_container, signInFragment).commit();
    }

    private void handleConfirmUserSuccess() {
        Resources resources = confirmationFragment.getStartActivity().getResources();
        confirmationFragment.setCorrectCodeNotification(
                resources.getString(R.string.fragment_confirmation_correct_code)
        );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment parentFragment = confirmationFragment.getParent();
                confirmationFragment.dismiss();
                if(parentFragment instanceof SignUpFragment) {
                    closeSignUpFragment((SignUpFragment) parentFragment);
                    openSignInFragment();
                }
            }
        }, 1500);
    }

    private void handleConfirmUserFailure(@NotNull Exception exception) {
        Resources resources = confirmationFragment.getStartActivity().getResources();
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    confirmationFragment.getStartActivity(),
                    R.string.toast_error_network_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
            confirmationFragment.dismiss();
        } else if (exception.getClass() == CodeMismatchException.class) {
            confirmationFragment.setIncorrectCodeError(
                    resources.getString(R.string.fragment_confirmation_incorrect_code)
            );
            confirmationFragment.cleanCodeInputViewError();
        } else {
            MotionToast.display(
                    confirmationFragment.getStartActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
            confirmationFragment.dismiss();
        }
    }

    @RequiresInternetConnection
    private void confirmUser(String email, String code) {
        if(FullScreenActivity.isNetworkAvailable(confirmationFragment.getStartActivity())) {
            Cognito cognito = Cognito.getInstance(confirmationFragment.getStartActivity());
            CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
            CognitoUser cognitoUser =  cognitoUserPool.getUser(email);
            cognitoUser.confirmSignUpInBackground(code, false, new GenericHandler() {

                @Override
                public void onSuccess() {
                    handleConfirmUserSuccess();
                }

                @Override
                public void onFailure(Exception exception) {
                    handleConfirmUserFailure(exception);
                }

            });
        } else {
            handleConfirmUserFailure(new NoInternetConnectionException());
        }
    }



    private static class ResendCodeTask extends AsyncTask<CognitoUser, Void, String> {

        private ConfirmationFragment confirmationFragment;



        private ResendCodeTask(ConfirmationFragment confirmationFragment) {
            this.confirmationFragment = confirmationFragment;
        }

        @Override
        @RequiresInternetConnection
        protected String doInBackground(CognitoUser... cognitoUsers) {
            if(FullScreenActivity.isNetworkAvailable(confirmationFragment.getStartActivity())) {
                final String[] result = new String[1];
                VerificationHandler resendCodeHandler = new VerificationHandler() {

                    @Override
                    public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                            MotionToast.display(
                                    confirmationFragment.getStartActivity(),
                                    R.string.toast_success_resend_code,
                                    MotionToastType.SUCCESS_MOTION_TOAST
                            );
                    }

                    @Override
                    public void onFailure(Exception exception) {
                            MotionToast.display(
                                    confirmationFragment.getStartActivity(),
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                    }

                };
                cognitoUsers[0].resendConfirmationCode(resendCodeHandler);
                return result[0];
            } else {
                MotionToast.display(
                        confirmationFragment.getStartActivity(),
                        R.string.toast_warning_internet_connection,
                        MotionToastType.WARNING_MOTION_TOAST
                );
            }
            return null;
        }

    }

}
