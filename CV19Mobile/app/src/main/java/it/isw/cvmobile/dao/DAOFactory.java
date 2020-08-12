package it.isw.cvmobile.dao;

import android.content.Context;
import it.isw.cvmobile.dao.concrete.lambda.AccommodationFacilityDAOLambda;
import it.isw.cvmobile.dao.concrete.lambda.NotificationDAOLambda;
import it.isw.cvmobile.dao.concrete.lambda.ReviewDAOLambda;
import it.isw.cvmobile.dao.concrete.lambda.UserDAOLambda;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.dao.interfaces.NotificationDAO;
import it.isw.cvmobile.dao.interfaces.ReviewDAO;
import it.isw.cvmobile.dao.interfaces.UserDAO;
import it.isw.cvmobile.dao.enumerations.IndexingRetrievingType;
import it.isw.cvmobile.dao.exceptions.UnsupportedDAOException;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public abstract class DAOFactory {

    private static IndexingRetrievingType indexingRetrievingType = IndexingRetrievingType.LAMBDA;



    public static UserDAO getUserDAO(Context context) {
        switch (indexingRetrievingType) {
            case LAMBDA:
                return new UserDAOLambda(context);
            default:
                throw new UnsupportedDAOException(indexingRetrievingType + " unsupported");
        }
    }

    public static AccommodationFacilityDAO getAccommodationFacilityDAO(Context context) {
        switch (indexingRetrievingType) {
            case LAMBDA:
                return new AccommodationFacilityDAOLambda(context);
            default:
                throw new UnsupportedDAOException(indexingRetrievingType + " unsupported");
        }
    }

    public static ReviewDAO getReviewDAO(Context context) {
        switch (indexingRetrievingType) {
            case LAMBDA:
                return new ReviewDAOLambda(context);
            default:
                throw new UnsupportedDAOException(indexingRetrievingType + " unsupported");
        }
    }

    public static NotificationDAO getNotificationDAO(Context context) {
        switch (indexingRetrievingType) {
            case LAMBDA:
                return new NotificationDAOLambda(context);
            default:
                throw new UnsupportedDAOException(indexingRetrievingType + " unsupported");
        }
    }

    public static IndexingRetrievingType getIndexingRetrievingType() {
        return indexingRetrievingType;
    }

    public static void setIndexingRetrievingType(IndexingRetrievingType newIndexingRetrievingType) {
        indexingRetrievingType = newIndexingRetrievingType;
    }

}






















