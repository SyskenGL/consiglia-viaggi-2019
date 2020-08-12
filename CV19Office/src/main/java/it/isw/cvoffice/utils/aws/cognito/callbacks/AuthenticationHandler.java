package it.isw.cvoffice.utils.aws.cognito.callbacks;


import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

public interface AuthenticationHandler {

    default void onAuthenticationSuccess(AuthenticationResultType authenticationBundle, Credentials credentials) {
        System.out.println("authentication succeeded");
    }

    default void onNewPasswordObtained(String newPassword) {
        System.out.println("new password: " + newPassword);
    };

    default void onNewPasswordObtained(AuthenticationResultType authenticationBundle, Credentials credentials, String newPassword) {
        System.out.println("new password: " + newPassword);
    };

    default void onAuthenticationFailure(Exception exception) {
        System.out.println("authentication failed: " + exception);
    };

}