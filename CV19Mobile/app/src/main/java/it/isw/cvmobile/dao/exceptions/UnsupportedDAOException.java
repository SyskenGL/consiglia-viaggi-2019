package it.isw.cvmobile.dao.exceptions;

import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class UnsupportedDAOException extends RuntimeException{

    private String cause;



    public UnsupportedDAOException(String cause) {
        this.cause = cause;
    }

    @NotNull
    @Override
    public String toString() {
        return "cause: " + cause;
    }

}
