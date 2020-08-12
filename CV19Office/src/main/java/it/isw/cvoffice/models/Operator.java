package it.isw.cvoffice.models;

import it.isw.cvoffice.utils.annotations.Singleton;
import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;


@Singleton
public class Operator {

    private final String name;
    private Credentials credentials;
    private AuthenticationResultType authenticationBundle;
    private static Operator operator;
    private static boolean loggedIn;


    private Operator(String name, Credentials credentials, AuthenticationResultType authenticationBundle) {
        this.name = name;
        this.authenticationBundle = authenticationBundle;
        this.credentials = credentials;
    }

    public static void initializeOperator(String name,
                                          AuthenticationResultType authenticationBundle,
                                          Credentials credentials) {
        operator = new Operator(name, credentials, authenticationBundle);
        loggedIn = true;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public String getName() {
        return name;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public AuthenticationResultType getAuthenticationBundle() {
        return authenticationBundle;
    }

    public static Operator getInstance() {
        return operator;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Operator.loggedIn = loggedIn;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public void setAuthenticationBundle(AuthenticationResultType authenticationBundle) {
        this.authenticationBundle = authenticationBundle;
    }

}