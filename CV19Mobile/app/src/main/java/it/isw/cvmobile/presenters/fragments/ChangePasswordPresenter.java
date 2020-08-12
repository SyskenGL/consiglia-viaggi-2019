package it.isw.cvmobile.presenters.fragments;

import android.content.res.Resources;
import android.os.Handler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidentityprovider.model.LimitExceededException;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.UserDAO;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ChangePasswordFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class ChangePasswordPresenter {

    private final ChangePasswordFragment changePasswordFragment;



    public ChangePasswordPresenter(ChangePasswordFragment changePasswordFragment) {
        this.changePasswordFragment = changePasswordFragment;

    }

    public void onButtonSubmitClicked() {
        Resources resources = changePasswordFragment.getNavigationActivity().getResources();
        changePasswordFragment.cleanTextInputPasswordErrors();
        changePasswordFragment.setEnabledButtonSubmit(false);
        String oldPassword = changePasswordFragment.getOldPassword();
        String newPassword = changePasswordFragment.getNewPassword();
        if(oldPassword.length() == 0) {
            changePasswordFragment.setTextInputOldPasswordError(
                    resources.getString(R.string.fragment_change_password_enter_old_pass)
            );
            changePasswordFragment.setEnabledButtonSubmit(true);
        } else if(newPassword.length() == 0) {
            changePasswordFragment.setTextInputNewPasswordError(
                    resources.getString(R.string.fragment_change_password_enter_new_pass)
            );
            changePasswordFragment.setEnabledButtonSubmit(true);
        } else if(oldPassword.equals(newPassword)) {
            changePasswordFragment.setTextInputOldPasswordError(
                    resources.getString(R.string.fragment_change_password_same_pass)
            );
            changePasswordFragment.setEnabledButtonSubmit(true);
        } else {
            changePassword(oldPassword, newPassword);
        }
    }

    private void handleChangePasswordFailure(@NotNull Exception exception, String oldPassword) {
        Resources resources = changePasswordFragment.getNavigationActivity().getResources();
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    changePasswordFragment.getNavigationActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        } else if(exception.getClass() == NotAuthorizedException.class) {
            changePasswordFragment.setTextInputOldPasswordError(
                    resources.getString(R.string.fragment_change_password_incorrect_pass)
            );
        } else if(exception.getClass() == InvalidPasswordException.class) {
            changePasswordFragment.setTextInputNewPasswordError(
                    resources.getString(R.string.fragment_change_password_invalid_pass)
            );
        } else if(exception.getClass() == InvalidParameterException.class) {
            if(oldPassword.length() < 6) {
                changePasswordFragment.setTextInputOldPasswordError(
                        resources.getString(R.string.fragment_change_password_invalid_pass_len)
                );
            } else {
                changePasswordFragment.setTextInputNewPasswordError(
                        resources.getString(R.string.fragment_change_password_invalid_pass_len)
                );
            }
        } else if(exception.getClass() == LimitExceededException.class) {
            MotionToast.display(
                    changePasswordFragment.getNavigationActivity(),
                    R.string.toast_info_limit_exceeded,
                    MotionToastType.INFO_MOTION_TOAST
            );
        } else {
            MotionToast.display(
                    changePasswordFragment.getNavigationActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
        changePasswordFragment.setEnabledButtonSubmit(true);
    }

    @RequiresInternetConnection
    private void changePassword(final String oldPassword, final String newPassword) {
        if(FullScreenActivity.isNetworkAvailable(changePasswordFragment.getNavigationActivity())) {
            UserDAO userDAO = DAOFactory.getUserDAO(changePasswordFragment.getNavigationActivity());
            userDAO.updatePassword(oldPassword, newPassword, new GenericHandler() {

                @Override
                public void onSuccess() {
                    MotionToast.display(
                            changePasswordFragment.getNavigationActivity(),
                            R.string.toast_success_password_changed,
                            MotionToastType.SUCCESS_MOTION_TOAST
                    );
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changePasswordFragment.dismiss();
                        }
                    }, 750);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleChangePasswordFailure(exception, oldPassword);
                }

            });
        } else {
            handleChangePasswordFailure(new NoInternetConnectionException(), oldPassword);
        }
    }

}
