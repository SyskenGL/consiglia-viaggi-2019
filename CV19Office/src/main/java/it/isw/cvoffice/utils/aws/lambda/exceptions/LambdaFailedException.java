package it.isw.cvoffice.utils.aws.lambda.exceptions;


public class LambdaFailedException extends Exception {

    private final String cause;


    public LambdaFailedException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}