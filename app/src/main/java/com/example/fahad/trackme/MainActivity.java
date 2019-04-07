package com.example.fahad.trackme;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GPSTracker tracker;
    private Marker marker;
    private float v;
    private GoogleMap mMap;
    private int emission =0;
    private List<LatLng> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tracker=new GPSTracker(this);

        try{
            list=new ArrayList<>();
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(tracker.getLat()!=0.0) {
                        list.add(new LatLng(tracker.getLat(), tracker.getLng()));
                    }
                }
            },0,1000*10*1);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLng(list.get(0));
                        mMap.animateCamera(mCameraUpdate);
                        marker = mMap.addMarker(new MarkerOptions().position(list.get(0))
                                .flat(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_black_24dp)));
                        marker.setPosition(list.get(0));*/
                        LatLng sydney = new LatLng(-33.852, 151.211);
                        googleMap.addMarker(new MarkerOptions().position(sydney)
                                .title("Marker in Sydney"));
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .zoom(15.5f)
                                .build()
                        ));

                    }
                });
            }
        },0,1000*10*1);

    }


    private void animateCar(final List<LatLng> latLngs) {
        try{
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLng(latLngs.get(0));
            mMap.animateCamera(mCameraUpdate);
            if (emission == 1) {
                marker = mMap.addMarker(new MarkerOptions().position(latLngs.get(0))
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_black_24dp)));
            }
            for(LatLng latLng:latLngs){
                Log.e("Location--->", String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
            }
            marker.setPosition(latLngs.get(0));
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    v = valueAnimator.getAnimatedFraction();
                    double lng = v * latLngs.get(1).longitude + (1 - v)
                            * latLngs.get(0).longitude;
                    double lat = v * latLngs.get(1).latitude + (1 - v)
                            * latLngs.get(0).latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    marker.setPosition(newPos);
                    marker.setAnchor(0.5f, 0.5f);
                    marker.setRotation(getBearing(latLngs.get(0), newPos));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                            (new CameraPosition.Builder().target(newPos)
                                    .zoom(15.5f).build()));
                }
            });
            valueAnimator.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void animateCarOnMap(final List<LatLng> latLngs) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);

        mMap.animateCamera(mCameraUpdate);
        if (emission == 1) {
            marker = mMap.addMarker(new MarkerOptions().position(latLngs.get(0))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_car_black_24dp)));
        }
        marker.setPosition(latLngs.get(0));
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                v = valueAnimator.getAnimatedFraction();
                double lng = v * latLngs.get(1).longitude + (1 - v)
                        * latLngs.get(0).longitude;
                double lat = v * latLngs.get(1).latitude + (1 - v)
                        * latLngs.get(0).latitude;
                LatLng newPos = new LatLng(lat, lng);
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(getBearing(latLngs.get(0), newPos));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder().target(newPos)
                                .zoom(15.5f).build()));
            }
        });
        valueAnimator.start();
    }
        private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
}
