package it.isw.cvoffice.utils.aws.lambda.exceptions;


public class UnsupportedPayloadTypeException extends RuntimeException {

    private final String cause;


    public UnsupportedPayloadTypeException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}