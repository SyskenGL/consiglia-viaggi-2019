package it.isw.cvmobile.dao.interfaces;

import android.net.Uri;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public interface UserDAO {

    void isValidUser(String userId, Object resultsHandler);

    void getUserData(String userId, Object resultsHandler);

    void updatePreferredUsername(String preferredUsername, Object resultsHandler);

    void updateProfilePicture(Uri imageUri, Object resultsHandler);

    void updatePassword(String oldPassword, String newPassword, Object resultsHandler);

}
