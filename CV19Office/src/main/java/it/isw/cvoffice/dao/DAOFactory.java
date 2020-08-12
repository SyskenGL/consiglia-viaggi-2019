package it.isw.cvoffice.dao;

import it.isw.cvoffice.dao.concrete.lambda.NotificationDAOLambda;
import it.isw.cvoffice.dao.concrete.lambda.ReviewDAOLambda;
import it.isw.cvoffice.dao.exceptions.UnsupportedDAOException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public abstract class DAOFactory {

    private final static String CONFIG_FILE_NAME = "config/config.properties";
    private final static String INDEXING_RETRIEVING_TYPE;


    static {
        Properties properties = new Properties();
        InputStream inputStream = DAOFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        if(inputStream != null) {
            try {
                properties.load(inputStream);
                INDEXING_RETRIEVING_TYPE = properties.getProperty("indexingRetrievingType");
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(CONFIG_FILE_NAME + " loading failed");
            }
        } else {
            throw new RuntimeException(CONFIG_FILE_NAME + " file not found");
        }
    }


    public static ReviewDAO getReviewDAO() {
        switch (INDEXING_RETRIEVING_TYPE) {
            case "lambda":
                return new ReviewDAOLambda();
            default:
                throw new UnsupportedDAOException(INDEXING_RETRIEVING_TYPE + " unsupported DAO");
        }
    }

    public static NotificationDAO getNotificationDAO() {
        switch (INDEXING_RETRIEVING_TYPE) {
            case "lambda":
                return new NotificationDAOLambda();
            default:
                throw new UnsupportedDAOException(INDEXING_RETRIEVING_TYPE + " unsupported DAO");
        }
    }

}