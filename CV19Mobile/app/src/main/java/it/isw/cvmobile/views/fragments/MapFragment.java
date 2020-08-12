package it.isw.cvmobile.views.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.jetbrains.annotations.NotNull;
import it.isw.cvmobile.R;
import it.isw.cvmobile.presenters.fragments.MapPresenter;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.views.activities.NavigationActivity;


@Completed
public class MapFragment extends Fragment {

    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLng NAPLES = new LatLng(40.853294, 14.305573);

    private final NavigationActivity navigationActivity;
    private GoogleMap googleMap;
    private MapPresenter mapPresenter;
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton floatingActionButtonGPS;
    private FloatingActionButton floatingActionButtonSearch;
    private ImageView buttonBack;
    private SupportMapFragment supportMapFragment;
    private boolean bottomOpening;
    private LatLng initialCameraFocus = NAPLES;



    public MapFragment(NavigationActivity navigationActivity) {
        this.navigationActivity = navigationActivity;
    }

    public MapFragment(NavigationActivity navigationActivity,
                       double latitude,
                       double longitude) {
        this(navigationActivity);
        initialCameraFocus = new LatLng(latitude, longitude);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_container);
        floatingActionsMenu = view.findViewById(R.id.fragment_map_floating_actions_menu);
        floatingActionButtonGPS = view.findViewById(R.id.fragment_map_floating_action_gps);
        floatingActionButtonSearch = view.findViewById(R.id.fragment_map_floating_action_search);
        buttonBack = view.findViewById(R.id.fragment_map_button_back);
        initializeUserInterface();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mapPresenter.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeUserInterface() {
        if(bottomOpening) {
            buttonBack.setRotation(-90f);
        }
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                        navigationActivity,
                        R.raw.style_map
                ));
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setBuildingsEnabled(true);
                googleMap.setMinZoomPreference(DEFAULT_ZOOM);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialCameraFocus));
                mapPresenter = new MapPresenter(MapFragment.this);
                listenToClickEvents();
                listenToMoveCameraEvents();
            }
        });
    }

    private void listenToClickEvents() {
        floatingActionButtonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapPresenter.onFloatingActionButtonGPSClicked();
            }
        });
        floatingActionButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapPresenter.onFloatingActionButtonSearchClicked();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapPresenter.onButtonBackClicked();
            }
        });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker != null){
                    mapPresenter.onMarkerInfoClicked(marker);
                }
            }
        });
    }

    private void listenToMoveCameraEvents() {
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mapPresenter.onMapCameraMoved();
            }
        });
    }

    public void collapseFloatingActionMenu() {
        floatingActionsMenu.collapse();
    }

    public void moveCamera(LatLng position) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(position);
        googleMap.moveCamera(cameraUpdate);
    }

    public void cleanMap() {
        googleMap.clear();
    }

    public Marker addMarker(MarkerOptions markerOptions) {
        return googleMap.addMarker(markerOptions);
    }

    public Circle addCircle(LatLng position, int radius, int strokeColor, int fillColor) {
        return googleMap.addCircle(
                new CircleOptions().center(position)
                        .radius(radius)
                        .strokeColor(strokeColor)
                        .fillColor(fillColor)
        );
    }

    public NavigationActivity getNavigationActivity() {
        return navigationActivity;
    }

    public LatLng getCameraFocus() {
        return googleMap.getCameraPosition().target;
    }

    public LatLng getTopLeftCornerLatLng() {
        return googleMap.getProjection().fromScreenLocation(new Point(0, 0));
    }

    public LatLng getTopRightCornerLatLng() {
        return googleMap.getProjection().fromScreenLocation(
                new Point(navigationActivity.getScreenWidth(), 0)
        );
    }

    public LatLng getBottomLeftCornerLatLng() {
        return googleMap.getProjection().fromScreenLocation(
                new Point(0, navigationActivity.getScreenHeight())
        );
    }

    public LatLng getBottomRightCornerLatLng() {
        return googleMap.getProjection().fromScreenLocation(new Point(
                navigationActivity.getScreenWidth(),
                navigationActivity.getScreenHeight())
        );
    }

    public void setBottomOpening(boolean fromBottom) {
        this.bottomOpening = fromBottom;
    }

}