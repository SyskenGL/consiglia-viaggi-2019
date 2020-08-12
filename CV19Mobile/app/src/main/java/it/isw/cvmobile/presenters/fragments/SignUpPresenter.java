package it.isw.cvmobile.presenters.fragments;

import android.content.res.Resources;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.google.android.material.textfield.TextInputEditText;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.Cognito;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ConfirmationFragment;
import it.isw.cvmobile.views.fragments.SignUpFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class SignUpPresenter {

    private final SignUpFragment signUpFragment;



    public SignUpPresenter(SignUpFragment signUpFragment) {
        this.signUpFragment = signUpFragment;
    }

    public void onButtonSignUpClicked() {
        Resources resources = signUpFragment.getStartActivity().getResources();
        signUpFragment.setEnabledOverlay(false);
        signUpFragment.setEnabledButtonSignUp(false);
        signUpFragment.cleanTextInputErrors();
        String name = signUpFragment.getName();
        String surname = signUpFragment.getSurname();
        String email = signUpFragment.getEmail();
        String nickname = signUpFragment.getNickname();
        String password = signUpFragment.getPassword();
        if(name.length() == 0) {
            signUpFragment.setTextInputNameError(
                    resources.getString(R.string.fragment_sign_up_enter_name)
            );
            signUpFragment.setEnabledOverlay(true);
            signUpFragment.setEnabledButtonSignUp(true);
        } else if(surname.length() == 0) {
            signUpFragment.setTextInputSurnameError(
                    resources.getString(R.string.fragment_sign_up_enter_surname)
            );
            signUpFragment.setEnabledOverlay(true);
            signUpFragment.setEnabledButtonSignUp(true);
        } else if(nickname.length() == 0) {
            signUpFragment.setTextInputNicknameError(
                    resources.getString(R.string.fragment_sign_up_enter_nickname)
            );
            signUpFragment.setEnabledOverlay(true);
            signUpFragment.setEnabledButtonSignUp(true);
        } else if(email.length() == 0) {
            signUpFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_sign_up_enter_email)
            );
            signUpFragment.setEnabledOverlay(true);
            signUpFragment.setEnabledButtonSignUp(true);
        } else if(!isValidEmail(email)) {
            signUpFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_sign_up_invalid_email)
            );
            signUpFragment.setEnabledOverlay(true);
            signUpFragment.setEnabledButtonSignUp(true);
        } else if(password.length() == 0) {
            signUpFragment.setTextInputPasswordError(
                    resources.getString(R.string.fragment_sign_in_enter_pass)
            );
            signUpFragment.setEnabledOverlay(true);
            signUpFragment.setEnabledButtonSignUp(true);
        } else {
            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            userAttributes.addAttribute("given_name", name);
            userAttributes.addAttribute("family_name", surname);
            userAttributes.addAttribute("email", email);
            userAttributes.addAttribute("nickname", nickname);
            userAttributes.addAttribute("preferred_username", "nickname");
            signUp(userAttributes, email, password);
        }
    }

    public void onOverlayClicked() {
        closeSignUpFragment();
    }

    public void onWavesClicked() {
        if(signUpFragment.getStartActivity().getCurrentFocus() instanceof TextInputEditText) {
            FullScreenActivity.hideKeyboard(signUpFragment.getStartActivity());
            FullScreenActivity.cleanFocus(signUpFragment.getStartActivity());
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void openConfirmationFragment(String email)  {
        ConfirmationFragment confirmationFragment = new ConfirmationFragment(
                email,
                signUpFragment.getStartActivity(),
                signUpFragment
        );
        FragmentManager fragmentManager = signUpFragment.getStartActivity().getSupportFragmentManager();
        confirmationFragment.setCancelable(false);
        confirmationFragment.show(fragmentManager, "CONFIRM_CODE");
    }

    private void closeSignUpFragment() {
        FullScreenActivity.hideKeyboard(signUpFragment.getStartActivity());
        signUpFragment.startClosingTransition();
        signUpFragment.getStartActivity().startOpeningTransition();
        FragmentManager fragmentManager = signUpFragment.getStartActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.remove(signUpFragment).commit();
    }

    private void handleSignUpFailure(@NotNull Exception exception) {
        Resources resources = signUpFragment.getStartActivity().getResources();
        String password = signUpFragment.getPassword();
        signUpFragment.cleanTextInputErrors();
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    signUpFragment.getStartActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        } else if(exception.getClass() == UsernameExistsException.class) {
            signUpFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_sign_up_user_exists)
            );
        } else if(exception.getClass() == InvalidPasswordException.class) {
            signUpFragment.setTextInputPasswordError(
                    resources.getString(R.string.fragment_sign_up_invalid_pass)
            );
        } else if(exception.getClass() == InvalidParameterException.class) {
            if(password.length() < 6) {
                signUpFragment.setTextInputPasswordError(
                        resources.getString(R.string.fragment_sign_up_invalid_pass_len)
                );
            } else {
                signUpFragment.setTextInputEmailError(
                        resources.getString(R.string.fragment_sign_up_invalid_email)
                );
            }
        } else {
            MotionToast.display(
                    signUpFragment.getStartActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
        signUpFragment.setEnabledOverlay(true);
        signUpFragment.setEnabledButtonSignUp(true);
    }

    @RequiresInternetConnection
    private void signUp(CognitoUserAttributes userAttributes,
                        final String email,
                        final String password) {
        if(FullScreenActivity.isNetworkAvailable(signUpFragment.getStartActivity())) {
            Cognito cognito = Cognito.getInstance(signUpFragment.getStartActivity());
            CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
            cognitoUserPool.signUpInBackground(email, password, userAttributes, null, new SignUpHandler() {

                @Override
                public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                    signUpFragment.setEnabledOverlay(true);
                    signUpFragment.setEnabledButtonSignUp(true);
                    openConfirmationFragment(email);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleSignUpFailure(exception);
                }

            });
        } else{
            handleSignUpFailure(new NoInternetConnectionException());
        }
    }

}

