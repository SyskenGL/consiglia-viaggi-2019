package it.isw.cvoffice.dao.concrete.lambda.exceptions;

public class OperatorNotAuthenticatedException extends RuntimeException {

    private final String cause;


    public OperatorNotAuthenticatedException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}