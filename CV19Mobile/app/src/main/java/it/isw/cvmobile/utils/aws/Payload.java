package it.isw.cvmobile.utils.aws;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.aws.enumerations.PayloadType;


@Completed
public class Payload {

    private final static String UPDATE_NOTIFICATION_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"user_id\":null" +
                                                "}" +
                                            "}";

    private final static String POST_VIEW_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"user_id\":null," +
                                                    "\"accommodation_facility_id\":null" +
                                                "}" +
                                            "}";

    private final static String POST_REVIEW_MODEL =
                                             "{" +
                                                "\"parameters\":{" +
                                                    "\"title\":null," +
                                                    "\"description\":null," +
                                                    "\"rating\":null," +
                                                    "\"date_of_stay\":null," +
                                                    "\"total_images\":null," +
                                                    "\"accommodation_facility_id\":null," +
                                                    "\"user_id\":null" +
                                                "}" +
                                            "}";

    private final static String POST_FEEDBACK_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"review_id\":null," +
                                                    "\"user_id\":null," +
                                                    "\"type\":null" +
                                                "}" +
                                            "}";

    private final static String POST_FAVORITE_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                "\"user_id\":null," +
                                                    "\"accommodation_facility_id\":null" +
                                                "}" +
                                            "}";

    private final static String GET_USER_VALIDITY_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"filters\":{" +
                                                        "\"user_id\":null" +
                                                    "}" +
                                                "}" +
                                            "}";

    private final static String GET_USER_DATA_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"filters\":{" +
                                                        "\"user_id\":null" +
                                                    "}" +
                                                "}" +
                                            "}";

    private final static String GET_REVIEWS_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"filters\":{" +
                                                        "\"review_id\":null," +
                                                        "\"accommodation_facility_id\":null," +
                                                        "\"reviewer_id\":null," +
                                                        "\"user_id\":null," +
                                                        "\"rating\":[]," +
                                                        "\"season\":[]," +
                                                        "\"status\":[]" +
                                                    "}," +
                                                    "\"sort\":{" +
                                                        "\"title\":null," +
                                                        "\"rating\":null," +
                                                        "\"date_of_stay\":null," +
                                                        "\"publication_date\":null," +
                                                        "\"total_likes\":null," +
                                                        "\"total_dislikes\":null" +
                                                    "}," +
                                                    "\"index\":{" +
                                                        "\"offset\":null," +
                                                        "\"limit\":null" +
                                                    "}" +
                                                "}" +
                                            "}";

    private final static String GET_NOTIFICATIONS_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"filters\":{" +
                                                        "\"user_id\":null," +
                                                        "\"status\":null" +
                                                    "}," +
                                                    "\"index\":{" +
                                                        "\"offset\":null," +
                                                        "\"limit\":null" +
                                                    "}" +
                                                "}" +
                                            "}";

    private final static String GET_HISTORY_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"filters\":{" +
                                                        "\"user_id\":null" +
                                                    "}," +
                                                    "\"index\":{" +
                                                        "\"offset\":null," +
                                                        "\"limit\":null" +
                                                    "}" +
                                                "}" +
                                            "}";

    private final static String GET_FAVORITES_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"filters\":{" +
                                                        "\"user_id\":null" +
                                                    "}," +
                                                    "\"index\":{" +
                                                        "\"offset\":null," +
                                                        "\"limit\":null" +
                                                    "}" +
                                                "}" +
                                            "}";

    private final static String GET_ACCOMMODATION_FACILITIES_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"keywords\":null," +
                                                    "\"filters\":{" +
                                                        "\"accommodation_facility_id\":null," +
                                                        "\"name\":null," +
                                                        "\"type\":null," +
                                                        "\"country\":null," +
                                                        "\"administrative_area_level_1\":null," +
                                                        "\"administrative_area_level_2\":null," +
                                                        "\"administrative_area_level_3\":null," +
                                                        "\"locality\":null," +
                                                        "\"latitude\":null," +
                                                        "\"longitude\":null," +
                                                        "\"radius\":null," +
                                                        "\"rating\":[]," +
                                                        "\"tags\":[]," +
                                                        "\"user_id\":null" +
                                                    "}," +
                                                    "\"sort\":{" +
                                                        "\"name\":null," +
                                                        "\"rating\":null," +
                                                        "\"total_favorites\":null," +
                                                        "\"total_views\":null" +
                                                    "}," +
                                                    "\"index\":{" +
                                                        "\"offset\":null," +
                                                        "\"limit\":null" +
                                                    "}" +
                                                "}" +
                                            "}";

    private final static String DELETE_FEEDBACK_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"review_id\":null," +
                                                    "\"user_id\":null" +
                                                "}" +
                                            "}";

    private final static String DELETE_FAVORITE_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"user_id\":null," +
                                                    "\"accommodation_facility_id\":null" +
                                                "}" +
                                            "}";

    private final static String DELETE_NOTIFICATION_MODEL =
                                            "{" +
                                                "\"parameters\":{" +
                                                    "\"review_id\":null," +
                                                    "\"user_id\":null" +
                                                "}" +
                                            "}";

    private final PayloadType payloadType;
    private final JsonObject payloadBody;


    public Payload(PayloadType payloadType) {
        this.payloadType = payloadType;
        switch (payloadType) {
            case UPDATE_NOTIFICATION:
                payloadBody =  new JsonParser().parse(UPDATE_NOTIFICATION_MODEL).getAsJsonObject();
                break;
            case DELETE_FAVORITE:
                payloadBody =  new JsonParser().parse(DELETE_FAVORITE_MODEL).getAsJsonObject();
                break;
            case DELETE_FEEDBACK:
                payloadBody =  new JsonParser().parse(DELETE_FEEDBACK_MODEL).getAsJsonObject();
                break;
            case DELETE_NOTIFICATION:
                payloadBody =  new JsonParser().parse(DELETE_NOTIFICATION_MODEL).getAsJsonObject();
                break;
            case GET_HISTORY:
                payloadBody =  new JsonParser().parse(GET_HISTORY_MODEL).getAsJsonObject();
                break;
            case GET_ACCOMMODATION_FACILITIES:
                payloadBody =  new JsonParser().parse(GET_ACCOMMODATION_FACILITIES_MODEL).getAsJsonObject();
                break;
            case GET_REVIEWS:
                payloadBody =  new JsonParser().parse(GET_REVIEWS_MODEL).getAsJsonObject();
                break;
            case GET_FAVORITES:
                payloadBody =  new JsonParser().parse(GET_FAVORITES_MODEL).getAsJsonObject();
                break;
            case GET_USER_DATA:
                payloadBody =  new JsonParser().parse(GET_USER_DATA_MODEL).getAsJsonObject();
                break;
            case GET_USER_VALIDITY:
                payloadBody =  new JsonParser().parse(GET_USER_VALIDITY_MODEL).getAsJsonObject();
                break;
            case GET_NOTIFICATIONS:
                payloadBody =  new JsonParser().parse(GET_NOTIFICATIONS_MODEL).getAsJsonObject();
                break;
            case POST_FAVORITE:
                payloadBody =  new JsonParser().parse(POST_FAVORITE_MODEL).getAsJsonObject();
                break;
            case POST_VIEW:
                payloadBody =  new JsonParser().parse(POST_VIEW_MODEL).getAsJsonObject();
                break;
            case POST_REVIEW:
                payloadBody =  new JsonParser().parse(POST_REVIEW_MODEL).getAsJsonObject();
                break;
            default:
                payloadBody =  new JsonParser().parse(POST_FEEDBACK_MODEL).getAsJsonObject();
                break;
        }
    }

    public void setFilter(String key, String value) {
        JsonObject parameters = payloadBody.getAsJsonObject("parameters");
        if(parameters.has("filters")) {
            JsonObject filters = parameters.getAsJsonObject("filters");
            if(filters.has(key)) {
                if(filters.get(key).isJsonArray()) {
                    JsonArray array = filters.getAsJsonArray(key);
                    array.add(value);
                } else {
                    filters.addProperty(key, value);
                }
            }
        }
    }

    public void setSortingKeys(String key, String value) {
        JsonObject parameters = payloadBody.getAsJsonObject("parameters");
        if(parameters.has("sort")) {
            JsonObject filters = parameters.getAsJsonObject("sort");
            if (filters.has(key)) {
                filters.addProperty(key, value);
            }
        }
    }

    public void setValue(String key, String value) {
        JsonObject parameters = payloadBody.getAsJsonObject("parameters");
        if(parameters.has(key)){
            parameters.addProperty(key, value);
        }
    }

    public void setLimit(String limit) {
        JsonObject parameters = payloadBody.getAsJsonObject("parameters");
        if(parameters.has("index")) {
            JsonObject index = parameters.getAsJsonObject("index");
            index.addProperty("limit", limit);
        }
    }

    public void setOffset(String offset) {
        JsonObject parameters = payloadBody.getAsJsonObject("parameters");
        if(parameters.has("index")) {
            JsonObject index = parameters.getAsJsonObject("index");
            index.addProperty("offset", offset);
        }
    }

    public void setKeywords(String keywords) {
        JsonObject parameters = payloadBody.getAsJsonObject("parameters");
        if(parameters.has("keywords")){
            parameters.addProperty("keywords", keywords);
        }
    }

    public JsonObject getPayloadBody(){
        return payloadBody;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

}