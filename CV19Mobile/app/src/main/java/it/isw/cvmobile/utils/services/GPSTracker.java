package it.isw.cvmobile.utils.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import it.isw.cvmobile.utils.annotations.Completed;
import pub.devrel.easypermissions.EasyPermissions;


@Completed
public class GPSTracker {

    public final static int GPS_REQUEST_CODE = 0;
    public final static int RESOLVABLE_API_REQUEST_CODE = 1;

    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final long regularUpdateInterval;
    private final long fastestUpdateInterval;
    private final Context context;
    private LocationCallback locationCallback;



    public GPSTracker(Context context,
                      long regularUpdateInterval,
                      long fastestUpdateInterval) {
        this.context = context;
        this.regularUpdateInterval = regularUpdateInterval;
        this.fastestUpdateInterval = fastestUpdateInterval;
        this.locationCallback = new LocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(regularUpdateInterval);
        request.setFastestInterval(fastestUpdateInterval);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return (locationManager != null) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void startLocationUpdate(final Activity activity) {
        requestLocationPermission(activity);
        final LocationRequest request = createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(request);
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> response = settingsClient.checkLocationSettings(builder.build());
        response.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                fusedLocationProviderClient.requestLocationUpdates(
                        request,
                        locationCallback,
                        Looper.getMainLooper()
                );
            }
        });
        response.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(activity, RESOLVABLE_API_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException sendException) {
                        // ...
                    }
                }
            }
        });

    }

    public void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void requestLocationPermission(Activity activity) {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        if (!EasyPermissions.hasPermissions(context, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, GPS_REQUEST_CODE);
        }
    }

    public void setLocationCallback(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
    }

}

