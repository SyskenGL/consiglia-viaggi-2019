package it.isw.cvoffice.utils.aws.lambda;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.isw.cvoffice.utils.aws.lambda.enumerations.PayloadType;
import it.isw.cvoffice.utils.aws.lambda.exceptions.UnsupportedPayloadTypeException;
import org.jetbrains.annotations.NotNull;


public class Payload {

    private final static String GET_REVIEWS_MODEL =
            "{" +
                "\"parameters\":{" +
                    "\"filters\":{" +
                        "\"status\":[]" +
                    "}," +
                    "\"index\":{" +
                        "\"offset\":null," +
                        "\"limit\":null" +
                    "}" +
                "}" +
            "}";

    private final static String UPDATE_REVIEW_STATUS_MODEL =
            "{" +
                "\"parameters\":{" +
                    "\"review_id\": null," +
                    "\"status\": null" +
                "}" +
            "}";

    private final static String POST_NOTIFICATION_MODEL =
            "{" +
                "\"parameters\":{" +
                    "\"review_id\": null," +
                    "\"user_id\": null," +
                    "\"title\": null," +
                    "\"message\": null," +
                    "\"type\": null" +
                "}" +
            "}";

    private final PayloadType payloadType;
    private final JsonObject payloadBody;


    public Payload(@NotNull PayloadType payloadType) {
        this.payloadType = payloadType;
        switch (payloadType) {
            case GET_REVIEWS:
                payloadBody = JsonParser.parseString(GET_REVIEWS_MODEL).getAsJsonObject();
                break;
            case POST_NOTIFICATION:
                payloadBody = JsonParser.parseString(POST_NOTIFICATION_MODEL).getAsJsonObject();
                break;
            case UPDATE_REVIEW_STATUS:
                payloadBody = JsonParser.parseString(UPDATE_REVIEW_STATUS_MODEL).getAsJsonObject();
                break;
            default:
                throw new UnsupportedPayloadTypeException("Unsupported payload type");
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

    public JsonObject getPayloadBody(){
        return payloadBody;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

}