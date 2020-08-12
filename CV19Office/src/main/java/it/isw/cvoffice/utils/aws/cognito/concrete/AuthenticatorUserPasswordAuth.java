package it.isw.cvoffice.utils.aws.cognito.concrete;

import it.isw.cvoffice.utils.SecurePasswordGenerator;
import it.isw.cvoffice.utils.aws.cognito.Authenticator;
import it.isw.cvoffice.utils.aws.cognito.Cognito;
import it.isw.cvoffice.utils.aws.cognito.callbacks.AuthenticationHandler;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.*;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import java.util.HashMap;
import java.util.Map;


public class AuthenticatorUserPasswordAuth implements Authenticator {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private final CognitoIdentityClient cognitoIdentityClient;


    public AuthenticatorUserPasswordAuth() {
        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(Cognito.REGION)
                .build();
        cognitoIdentityClient = CognitoIdentityClient.builder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(Cognito.REGION)
                .build();
    }

    @Override
    public void authenticate(Map<String, String> authParameters, AuthenticationHandler authenticationHandler) {
        new Thread(() -> {
            InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                    .clientId(Cognito.CLIENT_ID)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(authParameters)
                    .build();
            try {
                InitiateAuthResponse initiateAuthResponse = cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);
                if (initiateAuthResponse.challengeName() != null) {
                    respondToAuthChallenge(initiateAuthResponse, authenticationHandler);
                } else {
                    authenticationHandler.onAuthenticationSuccess(
                            initiateAuthResponse.authenticationResult(),
                            getCredentials(initiateAuthResponse.authenticationResult())
                    );
                }
            } catch (Exception exception) {
                authenticationHandler.onAuthenticationFailure(exception);
            }
        }).start();
    }

    @Override
    public void signOut(String accessToken) {
        GlobalSignOutRequest globalSignOutRequest = GlobalSignOutRequest.builder()
                .accessToken(accessToken)
                .build();
        cognitoIdentityProviderClient.globalSignOut(globalSignOutRequest);
    }

    @Override
    public void respondToNewPasswordRequiredAuthChallenge(@NotNull InitiateAuthResponse initiateAuthResponse,
                                                          @NotNull AuthenticationHandler authenticationHandler) {
        String newPassword = SecurePasswordGenerator.generate(15);
        Map<String, String> challengeResponses = new HashMap<>();
        challengeResponses.put("USERNAME", initiateAuthResponse.challengeParameters().get("USER_ID_FOR_SRP"));
        challengeResponses.put("NEW_PASSWORD", newPassword);
        RespondToAuthChallengeRequest respondToAuthChallengeRequest = RespondToAuthChallengeRequest.builder()
                .clientId(Cognito.CLIENT_ID)
                .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .challengeResponses(challengeResponses)
                .session(initiateAuthResponse.session())
                .build();
        RespondToAuthChallengeResponse respondToAuthChallengeResponse =
                cognitoIdentityProviderClient.respondToAuthChallenge(respondToAuthChallengeRequest);
        if(respondToAuthChallengeResponse.challengeName() != null) {
            authenticationHandler.onNewPasswordObtained(newPassword);
            respondToAuthChallenge(respondToAuthChallengeResponse, authenticationHandler);
        } else {
            authenticationHandler.onNewPasswordObtained(
                    respondToAuthChallengeResponse.authenticationResult(),
                    getCredentials(respondToAuthChallengeResponse.authenticationResult()),
                    newPassword
            );
        }
    }

    private Credentials getCredentials(@NotNull AuthenticationResultType authenticationResult) {
        Map<String, String> logins = new HashMap<>();
        logins.put(String.format(Cognito.PROVIDER, Cognito.REGION, Cognito.USER_POOL_ID),
                authenticationResult.idToken()
        );
        GetIdRequest getIdRequest = GetIdRequest.builder()
                .identityPoolId(Cognito.IDENTITY_POOL_ID)
                .logins(logins)
                .build();
        GetIdResponse getIdResponse = cognitoIdentityClient.getId(getIdRequest);
        GetCredentialsForIdentityRequest getCredentialsForIdentityRequest = GetCredentialsForIdentityRequest.builder()
                .identityId(getIdResponse.identityId())
                .logins(logins)
                .build();
        GetCredentialsForIdentityResponse getCredentialsForIdentityResponse = cognitoIdentityClient.getCredentialsForIdentity(
                getCredentialsForIdentityRequest
        );
        return getCredentialsForIdentityResponse.credentials();
    }

}