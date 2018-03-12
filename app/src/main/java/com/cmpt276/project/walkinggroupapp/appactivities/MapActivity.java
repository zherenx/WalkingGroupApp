package com.cmpt276.project.walkinggroupapp.appactivities;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.cmpt276.project.walkinggroupapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


/**
 * Simple test app to show a Google Map.
 * - If using the emulator, Create an Emulator from the API 26 image.
 *   (API27's doesn't/didn't support maps; nor will 24 or before I believe).
 * - Accessing Google Maps requires an API key: You can request one for free (and should!)
 *   see /res/values/google_maps_api.xml
 * - More notes at the end of this file.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private static final int DEFAULT_ZOOM= 12;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initializes google api client if its null
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("App", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onMapReady");
        mMap = googleMap;

        //create map listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                placeMarkerOnMap(latLng);
            }
        });


        //add zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //allows clicking on marker to show its title
        mMap.setOnMarkerClickListener(this);


        // Add a marker from
        LatLng randomPlace = new LatLng(37.35,-122.1);
        placeMarkerOnMap(randomPlace);

        // Add a marker in Ney York and move camera to it
        LatLng myPlace = new LatLng(40.73, -73.99);  // this is New York
        placeMarkerOnMap(myPlace);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Initiates a background connection of the client to Google Play services.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Closes the connection to Google Play services if the client is not null and is connected
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }


    private void setUpMap() {

        //Ask for USer Permission if USer has no ACCESS_FINE_LOCATION Permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        //get Users last known location and zoom to it
        mMap.setMyLocationEnabled(true);
        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            //determines the availability of location data on the device
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //gives you the most recent location currently available
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                placeMarkerOnMap(currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));
            }
        }
    }



    protected void placeMarkerOnMap(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location)

                                                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location));
        String titleStr = getAddressOfMarker(location);
        markerOptions.title(titleStr);

        mMap.addMarker(markerOptions);

    }



    private String getAddressOfMarker(LatLng latLng ) {
        //Creates a geoCoder object to turn a latitude and longitude coordinate into an address and vice versa
        Geocoder geocoder = new Geocoder( this );
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {
            //Asks the geoCoder to get the address from the location passed to the method.
            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );
            //If the response contains any address, then append it to a string and return.
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e ) {
        }
        return addressText;
    }

}


