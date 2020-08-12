package it.isw.cvoffice.utils.aws.cognito.concrete;

import it.isw.cvoffice.utils.aws.cognito.Authenticator;
import it.isw.cvoffice.utils.aws.cognito.Cognito;
import it.isw.cvoffice.utils.aws.cognito.callbacks.AuthenticationHandler;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.*;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import java.util.HashMap;
import java.util.Map;


public class AuthenticatorRefreshTokenAuth implements Authenticator {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private final CognitoIdentityClient cognitoIdentityClient;


    public AuthenticatorRefreshTokenAuth() {
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
                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .authParameters(authParameters)
                    .build();
            try {
                InitiateAuthResponse initiateAuthResponse = cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);
                if(initiateAuthResponse.challengeName() != null) {
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