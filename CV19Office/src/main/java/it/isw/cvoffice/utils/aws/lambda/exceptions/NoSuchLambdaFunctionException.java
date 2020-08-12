package it.isw.cvoffice.utils.aws.lambda.exceptions;


public class NoSuchLambdaFunctionException extends RuntimeException {

    private final String cause;


    public NoSuchLambdaFunctionException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}