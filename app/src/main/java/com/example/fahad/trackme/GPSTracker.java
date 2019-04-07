package com.example.fahad.trackme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.content.Context.LOCATION_SERVICE;

public class GPSTracker implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 1000 * 10 * 1, FASTEST_INTERVAL = 1000 * 10 * 1; // = 30 minutes
    private double latitude, longitude;
    private float bearing;
    private Location location;
    private LocationManager locationManager;

    public double getLat() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLng() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }


    public float getBearing(){
        if(location!=null){
            bearing = location.getBearing();
        }
        return bearing;
    }

    @SuppressLint("MissingPermission")
    public GPSTracker(Context ctx) {
        googleApiClient = new GoogleApiClient.Builder(ctx).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        if(location==null){
            locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            bearing=location.getBearing();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            Log.e("location GPS---->", location.getLatitude() + "," + location.getLongitude());
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }


}
