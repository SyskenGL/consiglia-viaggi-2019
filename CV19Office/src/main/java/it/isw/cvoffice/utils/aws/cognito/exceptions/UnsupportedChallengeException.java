package it.isw.cvoffice.utils.aws.cognito.exceptions;


public class UnsupportedChallengeException extends RuntimeException {

    private final String cause;


    public UnsupportedChallengeException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}

