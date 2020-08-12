package it.isw.cvmobile.presenters.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.UserDAO;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.exception.NoInternetConnectionException;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.fragments.ChangePasswordFragment;
import it.isw.cvmobile.views.fragments.ProfileFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


@Completed
public class ProfilePresenter {

    public final static int CHANGE_PROFILE_PICTURE_REQUEST_CODE = 9;

    private final ProfileFragment profileFragment;



    public ProfilePresenter(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        showUserAttributes();
    }

    public void onButtonChangePasswordClicked() {
        openChangePasswordFragment();
    }

    public void onRadioGroupPreferredUsernameChecked() {
        if(profileFragment.getCheckedPreferredUsernameValue().equals("nickname")) {
            updatePreferredUsername("nickname");
        } else {
            updatePreferredUsername("real_name");
        }
    }

    public void onButtonChangeProfilePictureClicked() {
        openGallery();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHANGE_PROFILE_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                updateProfilePicture(imageUri);
            }
        }
    }

    private void showUserAttributes() {
        User user = User.getInstance();
        profileFragment.setTextTotalReviews(String.valueOf(user.getTotalReviews()));
        profileFragment.setTextTotalFavorites(String.valueOf(user.getTotalFavorites()));
        profileFragment.setTextNickname(user.getNickname());
        profileFragment.setTextGivenName(user.getGivenName());
        profileFragment.setTextFamilyName(user.getFamilyName());
        profileFragment.setTextEmail(user.getEmail());
        profileFragment.setTextAccountCreatedDate(user.getCreationDate());
        if(user.getPreferredUsername().equals("nickname")) {
            profileFragment.setCheckedPreferredUsername(profileFragment.getRadioButtonNicknameId());
        } else {
            profileFragment.setCheckedPreferredUsername(profileFragment.getRadioButtonRealNameId());
        }
        showProfilePicture(user.getPicture());
    }

    private void showProfilePicture(String url) {
        profileFragment.setLoadingSpinnerVisibility(true);
        profileFragment.getProfilePicture().setVisibility(View.INVISIBLE);
        Glide.with(profileFragment.getNavigationActivity())
                .load(url)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        profileFragment.setLoadingSpinnerVisibility(false);
                        profileFragment.getProfilePicture().setImageResource(R.drawable.ic_error_sad);
                        profileFragment.getProfilePicture().setVisibility(View.VISIBLE);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model, Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        profileFragment.setLoadingSpinnerVisibility(false);
                        profileFragment.getProfilePicture().setImageDrawable(resource);
                        profileFragment.getProfilePicture().setVisibility(View.VISIBLE);
                        return true;
                    }

                }).into(profileFragment.getProfilePicture());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(EXTERNAL_CONTENT_URI, "image/*");
        profileFragment.startActivityForResult(intent, CHANGE_PROFILE_PICTURE_REQUEST_CODE);
    }

    private void openChangePasswordFragment() {
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment(profileFragment.getNavigationActivity());
        FragmentActivity fragmentActivity = profileFragment.getActivity();
        if (fragmentActivity != null) {
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            changePasswordFragment.show(fragmentManager, "CHANGE_PASSWORD_FRAGMENT");
        }
    }

    private void handleUpdateProfilePictureStateChanged(@NotNull TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
            showProfilePicture(User.getInstance().getPicture());
        } else if (state.equals(TransferState.FAILED)) {
            MotionToast.display(
                    profileFragment.getNavigationActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
    }

    private void handleUpdatePreferredUsernameFailure(@NotNull Exception exception) {
        if(exception.getClass() == NoInternetConnectionException.class) {
            MotionToast.display(
                    profileFragment.getNavigationActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        } else {
            MotionToast.display(
                    profileFragment.getNavigationActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
        if(profileFragment.getCheckedPreferredUsernameValue().equals("nickname")) {
            profileFragment.setCheckedPreferredUsername(profileFragment.getRadioButtonRealNameId());
        } else {
            profileFragment.setCheckedPreferredUsername(profileFragment.getRadioButtonNicknameId());
        }
    }

    @RequiresInternetConnection
    private void updatePreferredUsername(final String preferredUsername) {
        if(FullScreenActivity.isNetworkAvailable(profileFragment.getNavigationActivity())) {
            final User user = User.getInstance();
            UserDAO userDAO = DAOFactory.getUserDAO(profileFragment.getNavigationActivity());
            userDAO.updatePreferredUsername(preferredUsername, new UpdateAttributesHandler() {

                @Override
                public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
                    user.setPreferredUsername(preferredUsername);
                }

                @Override
                public void onFailure(Exception exception) {
                    handleUpdatePreferredUsernameFailure(exception);
                }

            });
        } else {
            handleUpdatePreferredUsernameFailure(new NoInternetConnectionException());
        }
    }

    @RequiresInternetConnection
    private void updateProfilePicture(Uri imageUri) {
        if (FullScreenActivity.isNetworkAvailable(profileFragment.getNavigationActivity())) {
            UserDAO userDAO = DAOFactory.getUserDAO(profileFragment.getNavigationActivity());
            userDAO.updateProfilePicture(imageUri, new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    handleUpdateProfilePictureStateChanged(state);
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    // ...
                }

                @Override
                public void onError(int id, Exception exception) {
                    MotionToast.display(
                            profileFragment.getNavigationActivity(),
                            R.string.toast_error_unknown_error,
                            MotionToastType.ERROR_MOTION_TOAST
                    );
                }

            });
        } else {
            MotionToast.display(
                    profileFragment.getNavigationActivity(),
                    R.string.toast_warning_internet_connection,
                    MotionToastType.WARNING_MOTION_TOAST
            );
        }
    }

}