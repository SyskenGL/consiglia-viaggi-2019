package it.isw.cvmobile.presenters.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Resources;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.UserDAO;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.Cognito;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.fragments.ConfirmationFragment;
import it.isw.cvmobile.views.fragments.ForgotPasswordVerificationFragment;
import it.isw.cvmobile.views.fragments.SignInFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class SignInPresenter {

    private final SignInFragment signInFragment;



    public SignInPresenter(SignInFragment signInFragment) {
        this.signInFragment = signInFragment;
    }

    public void onButtonSignInClicked() {
        Resources resources = signInFragment.getStartActivity().getResources();
        signInFragment.setEnabledForgotPassword(false);
        signInFragment.setEnabledOverlay(false);
        signInFragment.setEnabledButtonSignIn(false);
        signInFragment.cleanTextInputErrors();
        String email = signInFragment.getEmail();
        String password = signInFragment.getPassword();
        if(email.length() == 0) {
            signInFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_sign_in_enter_email)
            );
            signInFragment.setEnabledForgotPassword(true);
            signInFragment.setEnabledOverlay(true);
            signInFragment.setEnabledButtonSignIn(true);
        } else if(!isValidEmail(email)) {
            signInFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_sign_in_invalid_email)
            );
            signInFragment.setEnabledForgotPassword(true);
            signInFragment.setEnabledOverlay(true);
            signInFragment.setEnabledButtonSignIn(true);
        } else if(password.length() == 0) {
            signInFragment.setTextInputPasswordError(
                    resources.getString(R.string.fragment_sign_in_enter_pass)
            );
            signInFragment.setEnabledForgotPassword(true);
            signInFragment.setEnabledOverlay(true);
            signInFragment.setEnabledButtonSignIn(true);
        } else {
            signIn(email, password);
        }
    }

    public void onButtonForgotPasswordClicked() {
        openForgotPasswordVerificationFragment();
    }

    public void onOverlayClicked() {
        closeSignInFragment();
    }

    public void onWavesClicked() {
        if(signInFragment.getStartActivity().getCurrentFocus() instanceof TextInputEditText) {
            FullScreenActivity.hideKeyboard(signInFragment.getStartActivity());
            FullScreenActivity.cleanFocus(signInFragment.getStartActivity());
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void openConfirmationFragment(String email)  {
        ConfirmationFragment confirmationFragment = new ConfirmationFragment(
                email,
                signInFragment.getStartActivity(),
                signInFragment
        );
        FragmentManager fragmentManager = signInFragment.getStartActivity().getSupportFragmentManager();
        confirmationFragment.setCancelable(false);
        confirmationFragment.show(fragmentManager, "CONFIRM_CODE");
    }

    private void openForgotPasswordVerificationFragment() {
        ForgotPasswordVerificationFragment forgotPasswordVerificationFragment = new ForgotPasswordVerificationFragment(
                signInFragment.getStartActivity()
        );
        FragmentManager fragmentManager = signInFragment.getStartActivity().getSupportFragmentManager();
        forgotPasswordVerificationFragment.show(fragmentManager, "FORGOT_PASSWORD_VERIFICATION");
    }

    private void openNavigationActivity() {
        signInFragment.startClosingTransition(true, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent = new Intent(signInFragment.getStartActivity(), NavigationActivity.class);
                signInFragment.getStartActivity().startActivity(intent);
                signInFragment.getStartActivity().finish();
            }
        });
    }

    private void closeSignInFragment() {
        FullScreenActivity.hideKeyboard(signInFragment.getStartActivity());
        signInFragment.startClosingTransition(false, null);
        signInFragment.getStartActivity().startOpeningTransition();
        FragmentManager fragmentManager = signInFragment.getStartActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.remove(signInFragment).commit();
    }

    private void initializeUser(@NotNull JsonObject results, CognitoUserSession cognitoUserSession) {
        JsonObject jsonUser = results.getAsJsonObject("data").getAsJsonObject("user");
        User.initializeUser(
                jsonUser.get("user_id").getAsString(),
                jsonUser.get("nickname").getAsString(),
                jsonUser.get("given_name").getAsString(),
                jsonUser.get("family_name").getAsString(),
                jsonUser.get("email").getAsString(),
                jsonUser.get("create_date").getAsString(),
                jsonUser.get("picture").getAsString(),
                cognitoUserSession,
                jsonUser.get("preferred_username").getAsString(),
                jsonUser.get("total_reviews").getAsInt(),
                jsonUser.get("total_favorites").getAsInt()
        );
    }

    private void handleSignInFailure(@NotNull Exception exception, String email) {
        Resources resources = signInFragment.getStartActivity().getResources();
        signInFragment.cleanTextInputErrors();
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    signInFragment.getStartActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        } else if(exception.getClass() == NotAuthorizedException.class) {
            signInFragment.setTextInputEmailError(
                    resources.getString(R.string.fragment_sign_in_invalid_auth)
            );
        } else if(exception.getClass() == UserNotConfirmedException.class) {
            openConfirmationFragment(email);
        } else {
            MotionToast.display(
                    signInFragment.getStartActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
        signInFragment.setEnabledForgotPassword(true);
        signInFragment.setEnabledOverlay(true);
        signInFragment.setEnabledButtonSignIn(true);
    }

    private void handleGetUserDataFailure(String userId, int messageId) {
        Cognito cognito = Cognito.getInstance(signInFragment.getStartActivity());
        CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
        CognitoUser user = cognitoUserPool.getUser(userId);
        user.signOut();
        MotionToast.display(
                signInFragment.getStartActivity(),
                messageId,
                MotionToastType.ERROR_MOTION_TOAST
        );
        signInFragment.setEnabledForgotPassword(true);
        signInFragment.setEnabledOverlay(true);
        signInFragment.setEnabledButtonSignIn(true);
    }

    @RequiresInternetConnection
    private void getUserData(final String userId, final CognitoUserSession cognitoUserSession) {
        if(FullScreenActivity.isNetworkAvailable(signInFragment.getStartActivity())) {
            User.setLoggedIn(true);
            UserDAO userDAO = DAOFactory.getUserDAO(signInFragment.getStartActivity());
            userDAO.getUserData(userId, new LambdaResultsHandler() {
                @Override
                public void onSuccess(JsonObject results) {
                    initializeUser(results, cognitoUserSession);
                    openNavigationActivity();
                }
                @Override
                public void onFailure(Exception exception) {
                    handleGetUserDataFailure(userId, R.string.toast_error_unknown_error);
                }
            });
        } else {
            handleGetUserDataFailure(userId, R.string.toast_error_network_error);
        }
    }

    @RequiresInternetConnection
    private void signIn(final String email, final String password) {
        if(FullScreenActivity.isNetworkAvailable(signInFragment.getStartActivity())) {
            Cognito cognito = Cognito.getInstance(signInFragment.getStartActivity());
            CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
            final CognitoUser user = cognitoUserPool.getUser(email);
            user.signOut();
            user.getSessionInBackground(new AuthenticationHandler() {

                @Override
                public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                    Cognito.getInstance(signInFragment.getStartActivity()).setLogin(userSession);
                    getUserData(email, userSession);
                }

                @Override
                public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                    AuthenticationDetails authDetails = new AuthenticationDetails(userId, password, null);
                    authenticationContinuation.setAuthenticationDetails(authDetails);
                    authenticationContinuation.continueTask();
                }

                @Override
                public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                    // ...
                }

                @Override
                public void authenticationChallenge(ChallengeContinuation continuation) {
                    // ...
                }

                @Override
                public void onFailure(Exception exception) {
                    handleSignInFailure(exception, email);
                }

            });
        } else {
            handleSignInFailure(new NoInternetConnectionException(), null);
        }
    }

}