package it.isw.cvoffice.utils.aws.cognito;

import it.isw.cvoffice.utils.aws.cognito.callbacks.AuthenticationHandler;
import it.isw.cvoffice.utils.aws.cognito.concrete.AuthenticatorRefreshTokenAuth;
import it.isw.cvoffice.utils.aws.cognito.concrete.AuthenticatorUserPasswordAuth;
import it.isw.cvoffice.utils.aws.cognito.exceptions.UnsupportedAuthenticationFlowException;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeResponse;
import java.util.Map;


public interface Authenticator {

    static Authenticator builder(@NotNull AuthFlowType authFlowType) {
        switch (authFlowType) {
            case USER_SRP_AUTH:
                throw new UnsupportedAuthenticationFlowException("USER_SRP_AUTH auth flow unsupported");
            case REFRESH_TOKEN_AUTH:
                return new AuthenticatorRefreshTokenAuth();
            case REFRESH_TOKEN:
                throw new UnsupportedAuthenticationFlowException("REFRESH_TOKEN auth flow unsupported");
            case CUSTOM_AUTH:
                throw new UnsupportedAuthenticationFlowException("CUSTOM_AUTH auth flow unsupported");
            case ADMIN_NO_SRP_AUTH:
                throw new UnsupportedAuthenticationFlowException("ADMIN_NO_SRP_AUTH auth flow unsupported");
            case USER_PASSWORD_AUTH:
                return new AuthenticatorUserPasswordAuth();
            case ADMIN_USER_PASSWORD_AUTH:
                throw new UnsupportedAuthenticationFlowException("ADMIN_USER_PASSWORD_AUTH auth flow unsupported");
            case UNKNOWN_TO_SDK_VERSION:
                throw new UnsupportedAuthenticationFlowException("UNKNOWN_TO_SDK_VERSION auth flow unsupported");
            default:
                throw new UnsupportedAuthenticationFlowException("Unknown auth flow");
        }
    }

    default void respondToAuthChallenge(@NotNull InitiateAuthResponse initiateAuthResponse,
                                        AuthenticationHandler authenticationHandler) {
        switch (initiateAuthResponse.challengeName()) {
            case SMS_MFA:
                respondToSMSMFAAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case SOFTWARE_TOKEN_MFA:
                respondToSoftwareTokenMFAAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case SELECT_MFA_TYPE:
                respondToSelectMFATypeAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case MFA_SETUP:
                respondToMFASetupAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case PASSWORD_VERIFIER:
                respondToPasswordVerifierAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case CUSTOM_CHALLENGE:
                respondToCustomAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case DEVICE_SRP_AUTH:
                respondToDeviceSRPAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case DEVICE_PASSWORD_VERIFIER:
                respondToDevicePasswordVerifierAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case ADMIN_NO_SRP_AUTH:
                respondToAdminNoSRPAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case NEW_PASSWORD_REQUIRED:
                respondToNewPasswordRequiredAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
            case UNKNOWN_TO_SDK_VERSION:
                respondToUnknownToSdkVersionAuthChallenge(initiateAuthResponse, authenticationHandler);
                break;
        }
    }

    default void respondToAuthChallenge(@NotNull RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                        AuthenticationHandler authenticationHandler) {
        switch (respondToAuthChallengeResponse.challengeName()) {
            case SMS_MFA:
                respondToSMSMFAAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case SOFTWARE_TOKEN_MFA:
                respondToSoftwareTokenMFAAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case SELECT_MFA_TYPE:
                respondToSelectMFATypeAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case MFA_SETUP:
                respondToMFASetupAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case PASSWORD_VERIFIER:
                respondToPasswordVerifierAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case CUSTOM_CHALLENGE:
                respondToCustomAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case DEVICE_SRP_AUTH:
                respondToDeviceSRPAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case DEVICE_PASSWORD_VERIFIER:
                respondToDevicePasswordVerifierAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case ADMIN_NO_SRP_AUTH:
                respondToAdminNoSRPAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case NEW_PASSWORD_REQUIRED:
                respondToNewPasswordRequiredAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
            case UNKNOWN_TO_SDK_VERSION:
                respondToUnknownToSdkVersionAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
                break;
        }
    }

    default void respondToSMSMFAAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                              AuthenticationHandler authenticationHandler) {
         throw new UnsupportedAuthenticationFlowException("SMS_MFA unsupported auth flow");
    }

    default void respondToSMSMFAAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                              AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("SMS_MFA unsupported auth flow");
    }

    default void respondToSoftwareTokenMFAAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                        AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("SOFTWARE_TOKEN_MFA unsupported auth flow");
    }

    default void respondToSoftwareTokenMFAAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                        AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("SOFTWARE_TOKEN_MFA unsupported auth flow");
    }

    default void respondToSelectMFATypeAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                     AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("SELECT_MFA_TYPE unsupported auth flow");
    }

    default void respondToSelectMFATypeAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                     AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("SELECT_MFA_TYPE unsupported auth flow");
    }

    default void respondToMFASetupAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("MFA_SETUP unsupported auth flow");
    }

    default void respondToMFASetupAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("MFA_SETUP unsupported auth flow");
    }

    default void respondToPasswordVerifierAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                        AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("PASSWORD_VERIFIER unsupported auth flow");
    }

    default void respondToPasswordVerifierAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                        AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("PASSWORD_VERIFIER unsupported auth flow");
    }

    default void respondToCustomAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                              AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("CUSTOM_CHALLENGE unsupported auth flow");
    }

    default void respondToCustomAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                              AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("CUSTOM_CHALLENGE unsupported auth flow");
    }

    default void respondToDeviceSRPAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                 AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("DEVICE_SRP_AUTH unsupported auth flow");
    }

    default void respondToDeviceSRPAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                 AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("DEVICE_SRP_AUTH unsupported auth flow");
    }

    default void respondToDevicePasswordVerifierAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                              AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("DEVICE_PASSWORD_VERIFIER unsupported auth flow");
    }

    default void respondToDevicePasswordVerifierAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                              AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("DEVICE_PASSWORD_VERIFIER unsupported auth flow");
    }

    default void respondToAdminNoSRPAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                  AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("ADMIN_NO_SRP_AUTH unsupported auth flow");
    }

    default void respondToAdminNoSRPAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                  AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("ADMIN_NO_SRP_AUTH unsupported auth flow");
    }

    default void respondToNewPasswordRequiredAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                           AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("NEW_PASSWORD_REQUIRED unsupported auth flow");
    }

    default void respondToNewPasswordRequiredAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                           AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("NEW_PASSWORD_REQUIRED unsupported auth flow");
    }

    default void respondToUnknownToSdkVersionAuthChallenge(InitiateAuthResponse initiateAuthResponse,
                                                           AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("UNKNOWN_TO_SDK_VERSION unsupported auth flow");
    }

    default void respondToUnknownToSdkVersionAuthChallenge(RespondToAuthChallengeResponse respondToAuthChallengeResponse,
                                                           AuthenticationHandler authenticationHandler) {
        throw new UnsupportedAuthenticationFlowException("UNKNOWN_TO_SDK_VERSION unsupported auth flow");
    }

    void authenticate(Map<String, String> authParameters, AuthenticationHandler authenticationHandler);

    default void signOut(String accessToken) {
        throw new UnsupportedOperationException();
    }

}