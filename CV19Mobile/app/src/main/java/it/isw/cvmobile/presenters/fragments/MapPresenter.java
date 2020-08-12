package it.isw.cvmobile.presenters.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
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
import it.isw.cvmobile.utils.services.GPSTracker;
import it.isw.cvmobile.views.activities.FullScreenActivity;
import it.isw.cvmobile.views.activities.NavigationActivity;
import it.isw.cvmobile.views.fragments.AccommodationFacilityFragment;
import it.isw.cvmobile.views.fragments.MapFragment;
import it.isw.cvmobile.widgets.toast.MotionToast;
import it.isw.cvmobile.widgets.toast.MotionToastType;


@Completed
public class MapPresenter {

    private static final int TRIGGER_RADIUS = 10000;
    public static final int AUTOCOMPLETE_REQUEST_CODE = 5;

    private final MapFragment mapFragment;
    private Map<Marker, Integer> markers;
    private List<AccommodationFacility> accommodationFacilities;
    private LatLng currentCameraFocus;
    private Marker userPosition;
    private Circle userPositionArea;



    public MapPresenter(@NotNull final MapFragment mapFragment) {
        this.mapFragment = mapFragment;
        mapFragment.getNavigationActivity().setToolbarVisibility(false);
        mapFragment.getNavigationActivity().closeSideNavigationMenu();
        mapFragment.getNavigationActivity().setLockedNavigationMenu(true);
        currentCameraFocus = mapFragment.getCameraFocus();
        fetchAccommodationFacilities();
    }

    public void onFloatingActionButtonGPSClicked() {
        mapFragment.collapseFloatingActionMenu();
        final GPSTracker tracker = new GPSTracker(mapFragment.getNavigationActivity(), 1000, 1000);
        tracker.setLocationCallback(new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                tracker.stopLocationUpdate();
                Location userLocation = locationResult.getLastLocation();
                currentCameraFocus = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                fetchAccommodationFacilities();
                mapFragment.moveCamera(currentCameraFocus);
                if(userPosition != null) {
                    userPosition.remove();
                    userPositionArea.remove();
                }
                userPosition = addMarker(currentCameraFocus, "Me", "me");
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if(!locationAvailability.isLocationAvailable() && tracker.isGPSEnabled()) {
                    MotionToast.display(
                            mapFragment.getNavigationActivity(),
                            R.string.toast_warning_gps_disabled,
                            MotionToastType.WARNING_MOTION_TOAST
                    );
                }
            }
        });
        tracker.startLocationUpdate(mapFragment.getNavigationActivity());
    }

    public void onFloatingActionButtonSearchClicked() {
        mapFragment.collapseFloatingActionMenu();
        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
        ).build(mapFragment.getNavigationActivity());
        mapFragment.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public void onButtonBackClicked() {
        FragmentManager fragmentManager = mapFragment.getFragmentManager();
        if(fragmentManager != null) {
            fragmentManager.popBackStackImmediate();
            if(fragmentManager.getBackStackEntryCount() == 0) {
                mapFragment.getNavigationActivity().setLockedNavigationMenu(false);
                mapFragment.getNavigationActivity().setToolbarVisibility(true);
            }
        }
    }

    public void onMapCameraMoved() {
        LatLng topLeftCorner = mapFragment.getTopLeftCornerLatLng();
        LatLng topRightCorner = mapFragment.getTopRightCornerLatLng();
        LatLng bottomLeftCorner = mapFragment.getBottomLeftCornerLatLng();
        LatLng bottomRightCorner = mapFragment.getBottomRightCornerLatLng();
        if(getDistanceBetweenCoordinates(currentCameraFocus, topLeftCorner) > TRIGGER_RADIUS ||
           getDistanceBetweenCoordinates(currentCameraFocus, topRightCorner) > TRIGGER_RADIUS ||
           getDistanceBetweenCoordinates(currentCameraFocus, bottomLeftCorner) > TRIGGER_RADIUS ||
           getDistanceBetweenCoordinates(currentCameraFocus, bottomRightCorner) > TRIGGER_RADIUS) {
            mapFragment.cleanMap();
            currentCameraFocus = mapFragment.getCameraFocus();
            fetchAccommodationFacilities();
        }
    }

    public void onMarkerInfoClicked(Marker marker) {
        Integer indexAccommodation = markers.get(marker);
        if(indexAccommodation != null) {
            AccommodationFacility accommodationFacility = accommodationFacilities.get(indexAccommodation);
            if(accommodationFacility != null) {
                accommodationFacilities.add(accommodationFacility);
                openAccommodationFacilityFragment(accommodationFacility);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                currentCameraFocus = place.getLatLng();
                mapFragment.moveCamera(currentCameraFocus);
                addMarker(currentCameraFocus, place.getName(), "city");
                fetchAccommodationFacilities();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                MotionToast.display(
                        mapFragment.getNavigationActivity(),
                        R.string.toast_error_unknown_error,
                        MotionToastType.ERROR_MOTION_TOAST
                );
            }
        }
    }

    private BitmapDescriptor vectorToBitmapDescriptor(Context context, int vectorId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorId);
        if(vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(
                    vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
    }

    private Marker addMarker(LatLng position, String title, @NotNull String type) {
        int iconId;
        MarkerOptions markerOptions = new MarkerOptions();
        switch (type) {
            case "lodging":
                iconId = R.drawable.map_placeholder_hotel;
                break;
            case "restaurant":
                iconId = R.drawable.map_placeholder_restaurant;
                break;
            case "tourist_attraction":
                iconId = R.drawable.map_placeholder_tourist_attraction;
                break;
            case "me":
                userPositionArea = mapFragment.addCircle(
                        position,
                        30,
                        Color.argb(60, 219, 143, 182),
                        Color.argb(60, 219, 143, 182)
                );
                iconId = R.drawable.map_placeholder_user;
                break;
            default:
                iconId = R.drawable.map_placeholder_city;
                break;
        }
        markerOptions.position(position);
        markerOptions.icon(vectorToBitmapDescriptor(mapFragment.getNavigationActivity(), iconId));
        markerOptions.title(title);
        return mapFragment.addMarker(markerOptions);
    }

    private float getDistanceBetweenCoordinates(@NotNull LatLng coordinatesA,
                                                @NotNull LatLng coordinatesB) {
        Location locationA = new Location("");
        locationA.setLatitude(coordinatesA.latitude);
        locationA.setLongitude(coordinatesA.longitude);
        Location locationB = new Location("");
        locationB.setLatitude(coordinatesB.latitude);
        locationB.setLongitude(coordinatesB.longitude);
        return locationA.distanceTo(locationB);
    }

    private void handleFetchAccommodationFacilitiesSuccess(JsonObject results,
                                                           @NotNull AccommodationFacilityDAO accommodationFacilityDAO) {
        markers = new HashMap<>();
        accommodationFacilities = accommodationFacilityDAO.parseResults(results);
        int accommodationFacilityIndex = 0;
        for (AccommodationFacility accommodationFacility : accommodationFacilities) {
            Marker marker = addMarker(new LatLng(
                    Double.parseDouble(accommodationFacility.getLatitude()),
                    Double.parseDouble(accommodationFacility.getLongitude())
            ), accommodationFacility.getName(), accommodationFacility.getType());
            markers.put(marker, accommodationFacilityIndex);
            accommodationFacilityIndex++;
        }
    }

    private void openAccommodationFacilityFragment(AccommodationFacility accommodationFacility) {
        NavigationActivity navigationActivity = mapFragment.getNavigationActivity();
        AccommodationFacilityFragment accommodationFacilityFragment =
                new AccommodationFacilityFragment(navigationActivity, accommodationFacility);
        FragmentTransaction fragmentTransaction = navigationActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_right
        );
        fragmentTransaction.add(R.id.activity_navigation_fragment_container, accommodationFacilityFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @RequiresInternetConnection
    private void fetchAccommodationFacilities() {
        if(FullScreenActivity.isNetworkAvailable(mapFragment.getNavigationActivity())) {
            Map<String, String> filters = new HashMap<>();
            filters.put("latitude",String.valueOf(currentCameraFocus.latitude));
            filters.put("longitude", String.valueOf(currentCameraFocus.longitude));
            filters.put("radius",String.valueOf(TRIGGER_RADIUS));
            if(User.isLoggedIn()) {
                filters.put("user_id", User.getInstance().getUserId());
            }
            final AccommodationFacilityDAO accommodationFacilityDAO =
                    DAOFactory.getAccommodationFacilityDAO(mapFragment.getNavigationActivity());
            accommodationFacilityDAO.getAccommodationFacilities(filters, null, null, 0, 1000, new LambdaResultsHandler() {

                        @Override
                        public void onSuccess(JsonObject results) {
                            handleFetchAccommodationFacilitiesSuccess(results, accommodationFacilityDAO);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            MotionToast.display(
                                    mapFragment.getNavigationActivity(),
                                    R.string.toast_error_unknown_error,
                                    MotionToastType.ERROR_MOTION_TOAST
                            );
                        }

                    }
            );
        } else {
            MotionToast.display(
                    mapFragment.getNavigationActivity(),
                    R.string.toast_error_network_error,
                    MotionToastType.ERROR_MOTION_TOAST
            );
        }
    }

}