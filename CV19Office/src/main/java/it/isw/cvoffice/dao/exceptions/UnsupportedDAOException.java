package it.isw.cvoffice.dao.exceptions;


public class UnsupportedDAOException extends RuntimeException {

    private final String cause;


    public UnsupportedDAOException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}
