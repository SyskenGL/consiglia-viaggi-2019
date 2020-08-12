package it.isw.cvmobile.dao.concrete.lambda;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.LambdaInvoker;
import it.isw.cvmobile.utils.aws.Payload;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.utils.aws.enumerations.PayloadType;


@Completed
public class AccommodationFacilityDAOLambda implements AccommodationFacilityDAO {

    private Context context;



    public AccommodationFacilityDAOLambda(Context context) {
        this.context = context;
    }

    @Override
    @RequiresInternetConnection
    public void addToFavorites(String accommodationFacilityId, String userId, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.POST_FAVORITE);
        payload.setValue("accommodation_facility_id", accommodationFacilityId);
        payload.setValue("user_id", userId);
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void deleteFromFavorites(String accommodationFacilityId, String userId, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.DELETE_FAVORITE);
        payload.setValue("accommodation_facility_id", accommodationFacilityId);
        payload.setValue("user_id", userId);
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void getFavorites(String userId, int offset, int limit, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.GET_FAVORITES);
        payload.setFilter("user_id", userId);
        payload.setOffset(String.valueOf(offset));
        payload.setLimit(String.valueOf(limit));
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void getAccommodationFacilities(Map<String, String> filters,
                                           Map<String, String> sortingKeys,
                                           String keywords,
                                           int offset,
                                           int limit,
                                           Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.GET_ACCOMMODATION_FACILITIES);
        if(filters != null) {
            for(Map.Entry<String, String> entry: filters.entrySet()) {
                for(String filter: entry.getValue().split(";")) {
                    payload.setFilter(entry.getKey(), filter);
                }
            }
        }
        if(sortingKeys != null) {
            for(Map.Entry<String, String> entry: sortingKeys.entrySet()) {
                payload.setSortingKeys(entry.getKey(), entry.getValue());
            }
        }
        payload.setKeywords(keywords);
        payload.setOffset(String.valueOf(offset));
        payload.setLimit(String.valueOf(limit));
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public void addToHistory(String accommodationFacilityId, String userId, Object resultsHandler) {
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.POST_VIEW);
        payload.setValue("accommodation_facility_id", accommodationFacilityId);
        if(userId != null) {
            payload.setValue("user_id", userId);
        }
        lambdaInvoker.invoke(payload, null);
    }

    @Override
    @RequiresInternetConnection
    public void getHistory(String userId, int offset, int limit, Object resultsHandler) {
        if(!(resultsHandler instanceof LambdaResultsHandler)) {
            throw new IllegalArgumentException();
        }
        LambdaInvoker lambdaInvoker = new LambdaInvoker(context);
        Payload payload = new Payload(PayloadType.GET_HISTORY);
        payload.setFilter("user_id", userId);
        payload.setOffset(String.valueOf(offset));
        payload.setLimit(String.valueOf(limit));
        lambdaInvoker.invoke(payload, (LambdaResultsHandler) resultsHandler);
    }

    @Override
    @RequiresInternetConnection
    public List<AccommodationFacility> parseResults(Object results) {
        if(!(results instanceof JsonObject)) {
            throw new IllegalArgumentException();
        }
        JsonObject jsonResults = (JsonObject) results;
        ArrayList<AccommodationFacility> accommodationFacilities = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        JsonArray jsonAccommodationFacilities = jsonResults.getAsJsonObject("data").getAsJsonArray("accommodation_facilities");
        int retrieved = jsonResults.getAsJsonObject("data").get("retrieved").getAsInt();
        for(int accommodationFacilityIndex = 0; accommodationFacilityIndex < retrieved; accommodationFacilityIndex++) {
            AccommodationFacility accommodationFacility =
                    gson.fromJson(jsonAccommodationFacilities.get(accommodationFacilityIndex), AccommodationFacility.class);
            accommodationFacilities.add(accommodationFacility);
        }
        return accommodationFacilities;
    }

}
