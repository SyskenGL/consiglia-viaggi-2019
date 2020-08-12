package it.isw.cvoffice.utils.aws.cognito.exceptions;


public class UnsupportedAuthenticationFlowException extends RuntimeException {

    private final String cause;


    public UnsupportedAuthenticationFlowException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}
