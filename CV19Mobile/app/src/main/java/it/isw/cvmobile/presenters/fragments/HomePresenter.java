package it.isw.cvmobile.presenters.fragments;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.isw.cvmobile.R;
import it.isw.cvmobile.dao.DAOFactory;
import it.isw.cvmobile.dao.interfaces.AccommodationFacilityDAO;
import it.isw.cvmobile.models.AccommodationFacility;
import it.isw.cvmobile.models.User;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;
import it.isw.cvmobile.utils.aws.callbacks.LambdaResultsHandler;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.fragments.HomeFragment;
import it.isw.cvmobile.views.fragments.MapFragment;
import it.isw.cvmobile.views.fragments.ResearchResultsFragment;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.AccommodationCardAdapter;
import it.isw.cvmobile.widgets.scrollview.cards.accommodation.enumerations.AccommodationCardAdapterMode;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class HomePresenter {

    public static final int AUTOCOMPLETE_HOTEL_REQUEST_CODE = 2;
    public static final int AUTOCOMPLETE_RESTAURANTS_REQUEST_CODE = 3;
    public static final int AUTOCOMPLETE_TOURIST_ATTRACTIONS_REQUEST_CODE = 4;

    private final HomeFragment homeFragment;



    public HomePresenter(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
        fetchBestDestinations();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == AutocompleteActivity.RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if(place.getAddressComponents() != null) {
                Map<String, String> filters = getFiltersFromAddressComponents(place.getAddressComponents());
                switch (requestCode) {
                    case AUTOCOMPLETE_HOTEL_REQUEST_CODE:
                        filters.put("type", "lodging");
                        break;
                    case AUTOCOMPLETE_RESTAURANTS_REQUEST_CODE:
                        filters.put("type", "restaurant");
                        break;
                    case AUTOCOMPLETE_TOURIST_ATTRACTIONS_REQUEST_CODE:
                        filters.put("type", "tourist_attraction");
                        break;
                }
                if(User.isLoggedIn()) {
                    filters.put("user_id", User.getInstance().getUserId());
                }
                openResearchResultsFragment(filters);
            } else {
                MotionToast.display(
                        homeFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            MotionToast.display(
                    homeFragment.getNavigationActivity(),
                    R.string.toast_error_unknown_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
    }

    public void onButtonHotelClicked() {
        onBackgroundClicked();
        List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS_COMPONENTS);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
        ).build(homeFragment.getNavigationActivity());
        homeFragment.startActivityForResult(intent, AUTOCOMPLETE_HOTEL_REQUEST_CODE);
    }

    public void onButtonRestaurantsClicked() {
        onBackgroundClicked();
        List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS_COMPONENTS);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
        ).build(homeFragment.getNavigationActivity());
        homeFragment.startActivityForResult(intent, AUTOCOMPLETE_RESTAURANTS_REQUEST_CODE);
    }

    public void onButtonTouristAttractionsClicked() {
        onBackgroundClicked();
        List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS_COMPONENTS);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
        ).build(homeFragment.getNavigationActivity());
        homeFragment.startActivityForResult(intent, AUTOCOMPLETE_TOURIST_ATTRACTIONS_REQUEST_CODE);
    }

    public void onButtonMapsClicked() {
        onBackgroundClicked();
        openMapFragment();
    }

    public void onBackgroundClicked() {
        if(homeFragment.getNavigationActivity().getCurrentFocus() instanceof TextInputEditText) {
            FullScreenActivity.hideKeyboard(homeFragment.getNavigationActivity());
            FullScreenActivity.cleanFocus(homeFragment.getNavigationActivity());
        }
    }

    public boolean onResearchDone(int actionId) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            FullScreenActivity.hideKeyboard(homeFragment.getNavigationActivity());
            FullScreenActivity.cleanFocus(homeFragment.getNavigationActivity());
            String keywords = homeFragment.getKeywords();
            keywords = keywords.trim();
            if(keywords.length() > 0) {
                openResearchResultsFragment(keywords);
            } else {
                homeFragment.cleanEditTextResearch();
            }
            return true;
        }
        return false;
    }

    public void onButtonRefreshClicked() {
        homeFragment.startButtonRefreshSpinAnimation();
        fetchBestDestinations();
    }

    private Map<String, String> getFiltersFromAddressComponents(@NotNull AddressComponents addressComponents) {
        Map<String, String> filters = new HashMap<>();
        for(AddressComponent addressComponent: addressComponents.asList()) {
            switch (addressComponent.getTypes().get(0)) {
                case "locality":
                    filters.put("locality", addressComponent.getName());
                    break;
                case "administrative_area_level_1":
                    filters.put("administrative_area_level_1", addressComponent.getName());
                    break;
                case "administrative_area_level_2":
                    filters.put("administrative_area_level_2", addressComponent.getName());
                    break;
                case "administrative_area_level_3":
                    filters.put("administrative_area_level_3", addressComponent.getName());
                    break;
                case "country":
                    filters.put("country", addressComponent.getName());
                    break;
            }
        }
        return filters;
    }

    private void openMapFragment() {
        NavigationActivity navigationActivity = homeFragment.getNavigationActivity();
        MapFragment mapFragment = new MapFragment(navigationActivity);
        FragmentTransaction fragmentTransaction = navigationActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_right
        );
        fragmentTransaction.replace(R.id.activity_navigation_fragment_container, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openResearchResultsFragment(String keySearch) {
        NavigationActivity navigationActivity = homeFragment.getNavigationActivity();
        ResearchResultsFragment researchResultsFragment = new ResearchResultsFragment(navigationActivity, keySearch);
        FragmentTransaction fragmentTransaction = navigationActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_right
        );
        fragmentTransaction.replace(R.id.activity_navigation_fragment_container, researchResultsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openResearchResultsFragment(Map<String, String> filters) {
        NavigationActivity navigationActivity = homeFragment.getNavigationActivity();
        ResearchResultsFragment researchResultsFragment = new ResearchResultsFragment(navigationActivity, filters);
        FragmentTransaction fragmentTransaction = navigationActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_right
        );
        fragmentTransaction.replace(R.id.activity_navigation_fragment_container, researchResultsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showBestDestinations(JsonObject results,
                                      @NotNull AccommodationFacilityDAO accommodationFacilityDAO) {
        List<AccommodationFacility> accommodationFacilities = accommodationFacilityDAO.parseResults(results);
        AccommodationCardAdapter accommodationCardAdapter = new AccommodationCardAdapter(
                homeFragment.getNavigationActivity(),
                accommodationFacilities,
                AccommodationCardAdapterMode.SOFT_ADAPTER
        );
        homeFragment.setBestDestinationsRecyclerViewAdapter(accommodationCardAdapter);
        homeFragment.setLoadingSpinnerVisibility(false);
        homeFragment.setBestDestinationsRecyclerViewVisibility(true);
    }

    @RequiresInternetConnection
    private void fetchBestDestinations() {
        final AccommodationFacilityDAO accommodationFacilityDAO =
                DAOFactory.getAccommodationFacilityDAO(homeFragment.getNavigationActivity());
        Map<String, String> sortingKeys = new HashMap<>();
        Map<String, String> filters = new HashMap<>();
        if(User.isLoggedIn()) {
            filters.put("user_id", User.getInstance().getUserId());
        }
        sortingKeys.put("rating", "DESC");
        sortingKeys.put("total_favorites", "DESC");
        sortingKeys.put("total_view", "DESC");
        accommodationFacilityDAO.getAccommodationFacilities(filters, sortingKeys, null, 0, 10, new LambdaResultsHandler() {

            @Override
            public void onSuccess(JsonObject results) {
                homeFragment.clearButtonRefreshAnimation();
                homeFragment.setErrorViewVisibility(false);
                homeFragment.setBestDestinationsContainerVisibility(true);
                showBestDestinations(results, accommodationFacilityDAO);
            }

            @Override
            public void onFailure(Exception exception) {
                homeFragment.clearButtonRefreshAnimation();
                homeFragment.setErrorViewVisibility(true);
                homeFragment.setLoadingSpinnerVisibility(false);
            }

        });
    }

}
