package it.isw.cvmobile.dao.concrete.lambda;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import it.isw.cvmobile.dao.interfaces.UserDAO;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.Cognito;
import it.isw.cvmobile.utils.aws.LambdaInvoker;
import it.isw.cvmobile.utils.aws.Payload;
import it.isw.cvmobile.utils.aws.S3Uploader;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.aws.enumerations.PayloadType;


@Completed
public class UserDAOLambda implements UserDAO {

    private Context context;



    public UserDAOLambda(Context context) {
        this.context = context;
    }

    @Override
    @RequiresInternetConnection
    public void isValidUser(String userId, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.GET_USER_VALIDITY);
        payload.setFilter("user_id", userId);
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void getUserData(String userId, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.GET_USER_DATA);
        payload.setFilter("user_id", userId);
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void updatePreferredUsername(String preferredUsername, Object resultsHandler) {
        if(!(resultsHandler instanceof UpdateAttributesHandler)) {
            throw new IllegalArgumentException();
        }
        if(User.isLoggedIn()) {
            CognitoUserPool cognitoUserPool = Cognito.getInstance(context).getCognitoUserPool();
            CognitoUser user = cognitoUserPool.getCurrentUser();
            CognitoUserAttributes cognitoUserAttributes = new CognitoUserAttributes();
            cognitoUserAttributes.addAttribute("preferred_username", preferredUsername);
            user.updateAttributesInBackground(cognitoUserAttributes, (UpdateAttributesHandler) resultsHandler);
        }
    }

    @Override
    @RequiresInternetConnection
    public void updateProfilePicture(Uri imageUri, Object resultsHandler) {
        if(!(resultsHandler instanceof TransferListener)) {
            throw new IllegalArgumentException();
        }
        if(User.isLoggedIn()) {
            InputStream imageStream;
            ContentResolver contentResolver = context.getContentResolver();
            try {
                imageStream = contentResolver.openInputStream(imageUri);
                if(imageStream != null) {
                    final User user = User.getInstance();
                    File tmp = File.createTempFile("tmp", null);
                    tmp.deleteOnExit();
                    OutputStream outputStream = new FileOutputStream(tmp);
                    IOUtils.copy(imageStream, outputStream);
                    S3Uploader s3Uploader = new S3Uploader(context);
                    System.out.println(user.getPicture());
                    s3Uploader.upload(
                            user.getPicture().substring(user.getPicture().indexOf("users")),
                            tmp,
                            CannedAccessControlList.PublicRead,
                            (TransferListener) resultsHandler
                    );
                }
            } catch (IOException exception) {
                ((TransferListener) resultsHandler).onError(0, exception);
            }
        }
    }

    @Override
    @RequiresInternetConnection
    public void updatePassword(String oldPassword, String newPassword, Object resultsHandler) {
        if(!(resultsHandler instanceof GenericHandler)) {
            throw new IllegalArgumentException();
        }
        Cognito cognito = Cognito.getInstance(context);
        CognitoUserPool cognitoUserPool = cognito.getCognitoUserPool();
        CognitoUser cognitoUser = cognitoUserPool.getCurrentUser();
        cognitoUser.changePasswordInBackground(oldPassword, newPassword, (GenericHandler) resultsHandler);
    }

}
