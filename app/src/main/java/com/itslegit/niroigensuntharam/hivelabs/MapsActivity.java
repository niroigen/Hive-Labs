package com.itslegit.niroigensuntharam.hivelabs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;

/**
 * Created by abdullahalsaeed on 2017-11-26.
 */

public class MapsActivity extends FragmentActivity implements IALocationListener {



    private static final float HUE_IABLUE = 200.0f;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mMarker;
    private IALocationManager mIALocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemap);
        mIALocationManager = IALocationManager.create(this);

        // optional setup of floor plan id
        // if setLocation is not called, then location manager tries to find  location automatically
        final String floorPlanId = getString(R.string.indooratlas_floor_plan_id);
        if (!TextUtils.isEmpty(floorPlanId)) {
            final IALocation FLOOR_PLAN_ID = IALocation.from(IARegion.floorPlan(floorPlanId));
            mIALocationManager.setLocation(FLOOR_PLAN_ID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIALocationManager.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                        }
                    });
        }
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                ExampleUtils.shareText(MapsActivity.this, mIALocationManager.getExtraInfo().traceId,
                        "traceId");

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIALocationManager != null) {
            mIALocationManager.removeLocationUpdates(this);
        }

    }




    /**
     * Callback for receiving locations.
     * This is where location updates can be handled by moving markers or the camera.
     */
    public void onLocationChanged(IALocation location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMarker == null) {
            if (mMap != null) {
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(HUE_IABLUE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
            }
        } else {
            mMarker.setPosition(latLng);
        }
//        LatLng dhsg=new  LatLng(45.49750842, -73.57884518);
//
//        mMap.addPolyline(new PolylineOptions().add(
//
//                latLng,
//                new LatLng(45.49748218, -73.57886627),
//                new LatLng(45.49746615, -73.57887670),
//                dhsg
//
//                )
//                .width(1)
//                .color(Color.RED)
//
//
//        );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // N/A
    }
}






