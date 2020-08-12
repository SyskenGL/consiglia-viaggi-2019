package it.isw.cvmobile.utils.aws.enumerations;

import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public enum PayloadType {
    UPDATE_NOTIFICATION,
    DELETE_FAVORITE,
    DELETE_FEEDBACK,
    DELETE_NOTIFICATION,
    GET_NOTIFICATIONS,
    GET_HISTORY,
    GET_FAVORITES,
    GET_REVIEWS,
    GET_ACCOMMODATION_FACILITIES,
    GET_USER_DATA,
    GET_USER_VALIDITY,
    POST_FAVORITE,
    POST_FEEDBACK,
    POST_REVIEW,
    POST_VIEW
}
