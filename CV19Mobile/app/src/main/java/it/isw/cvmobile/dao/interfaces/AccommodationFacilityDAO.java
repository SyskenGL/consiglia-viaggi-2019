package it.isw.cvmobile.dao.interfaces;

import java.util.List;
import java.util.Map;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public interface AccommodationFacilityDAO {

    void addToFavorites(String accommodationFacilityId, String userId, Object resultsHandler);

    void deleteFromFavorites(String accommodationFacilityId, String userId, Object resultsHandler);

    void getFavorites(String userId, int offset, int limit, Object resultsHandler);

    void getAccommodationFacilities(Map<String, String> filters,
                                    Map<String, String> sortingKeys,
                                    String keywords,
                                    int offset,
                                    int limit,
                                    Object resultsHandler);

    void addToHistory(String accommodationFacilityId, String userId, Object resultsHandler);

    void getHistory(String userId, int offset, int limit, Object resultsHandler);

    List<AccommodationFacility> parseResults(Object results);

}