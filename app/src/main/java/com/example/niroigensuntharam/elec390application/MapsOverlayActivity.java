package com.example.niroigensuntharam.elec390application;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by abdullahalsaeed on 2017-11-26.
 */

public class MapsOverlayActivity extends FragmentActivity implements LocationListener {
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 42;
    private static final String TAG = "IndoorAtlasExample";


    /* used to decide when bitmap should be downscaled */
    private static final int MAX_DIMENSION = 2048;

    private static double latitude;
    private static String room;
    private static double longitude;
    private GoogleMap mMap;
    private Circle mCircle;
    private IARegion mOverlayFloorPlan = null;
    private GroundOverlay mGroundOverlay = null;
    private IALocationManager mIALocationManager;
    private IAResourceManager mResourceManager;
    private IATask<IAFloorPlan> mFetchFloorPlanTask;
    private Target mLoadTarget;
    private boolean mCameraPositionNeedsUpdating = true; // update on first location
    private boolean mShowIndoorLocation = false;
    private static LatLng center;
    Polyline line;

    LatLng[] Corners = {new LatLng(45.49719166, -73.57933402), new LatLng(45.49756392, -73.57899204),
                        new LatLng(45.49734207, -73.57851461), new LatLng(45.49697639, -73.57886866)};

    private void showLocationCircle(LatLng center, double accuracyRadius) {
        if (mCircle == null) {
            // location can received before map is initialized, ignoring those updates
            if (mMap != null) {
                mCircle = mMap.addCircle(new CircleOptions()
                        .center(center)
                        .radius(1)
                        .fillColor(Color.GREEN)
                        .strokeColor(Color.GREEN)
                        .zIndex(1.0f)
                        .visible(true)
                        .strokeWidth(5.0f));
            }
        } else {
            // move existing markers position to received location
            mCircle.setCenter(center);
            mCircle.setRadius(1);
        }

        this.center = center;

        FindTwoClosestCorners();




//        if (line == null){
//        // ... get a map.
//        // Add a thin red line from London to New York.
//            line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(latitude, longitude), center)
//                .width(5)
//                .color(Color.RED));
//
//
//
//        }
//        else{
//            line = null;
//        }
    }




    private IALocationListener mListener = new IALocationListenerSupport() {

        /**
         * Location changed, move marker and camera position.
         */
        @Override
        public void onLocationChanged(IALocation location) {

            Log.d(TAG, "new location received with coordinates: " + location.getLatitude()
                    + "," + location.getLongitude());

            if (mMap == null) {
                // location received before map is initialized, ignoring update here
                return;
            }

            final LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

            if (mShowIndoorLocation) {
                showLocationCircle(center, location.getAccuracy());
            }

            // our camera position needs updating if location has significantly changed
            if (mCameraPositionNeedsUpdating) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 17.5f));
                mCameraPositionNeedsUpdating = false;
            }
        }
    };


    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                final String newId = region.getId();
                // Are we entering a new floor plan or coming back the floor plan we just left?
                if (mGroundOverlay == null || !region.equals(mOverlayFloorPlan)) {
                    mCameraPositionNeedsUpdating = true; // entering new fp, need to move camera
                    if (mGroundOverlay != null) {
                        mGroundOverlay.remove();
                        mGroundOverlay = null;
                    }
                    mOverlayFloorPlan = region; // overlay will be this (unless error in loading)
                    fetchFloorPlan(newId);
                } else {
                    mGroundOverlay.setTransparency(0.0f);
                }

                mShowIndoorLocation = true;
                showInfo("Showing IndoorAtlas SDK\'s location output");
            }
            showInfo("Enter " + (region.getType() == IARegion.TYPE_VENUE
                    ? "VENUE "
                    : "FLOOR_PLAN ") + region.getId());
        }

        @Override
        public void onExitRegion(IARegion region) {
            if (mGroundOverlay != null) {
                // Indicate we left this floor plan but leave it there for reference
                // If we enter another floor plan, this one will be removed and another one loaded
                mGroundOverlay.setTransparency(0.5f);
            }

            mShowIndoorLocation = false;
            showInfo("Exit " + (region.getType() == IARegion.TYPE_VENUE
                    ? "VENUE "
                    : "FLOOR_PLAN ") + region.getId());
        }

    };

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startListeningPlatformLocations();
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!mShowIndoorLocation) {
            Log.d(TAG, "new LocationService location received with coordinates: " + location.getLatitude()
                    + "," + location.getLongitude());

            showLocationCircle(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    location.getAccuracy());
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.googlemap);

            latitude = getIntent().getExtras().getDouble("lat");
            longitude = getIntent().getExtras().getDouble("lon");
            room = getIntent().getExtras().getString("room");

            setUpMapIfNeeded();
        }
        catch (Exception ex){
            ex.getMessage();
        }
        // prevent the screen going to sleep while app is on foreground
        findViewById(android.R.id.content).setKeepScreenOn(true);

        // instantiate IALocationManager and IAResourceManager
        mIALocationManager = IALocationManager.create(this);
        mResourceManager = IAResourceManager.create(this);

        // Request GPS locations
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        startListeningPlatformLocations();






    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remember to clean up after ourselves
        mIALocationManager.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null) {


            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return;
            }
            mMap.setMyLocationEnabled(false);
        }

        // start receiving location updates & monitor region changes
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mListener);
        mIALocationManager.registerRegionListener(mRegionListener);

        // Setup long click to share the traceId
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                ExampleUtils.shareText(MapsOverlayActivity.this,
                        mIALocationManager.getExtraInfo().traceId, "traceId");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister location & region changes
        mIALocationManager.removeLocationUpdates(mListener);
        mIALocationManager.registerRegionListener(mRegionListener);
    }


    /**
     * Sets bitmap of floor plan as ground overlay on Google Maps
     */
    private void setupGroundOverlay(IAFloorPlan floorPlan, Bitmap bitmap) {

        if (mGroundOverlay != null) {
            mGroundOverlay.remove();
        }

        if (mMap != null) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            IALatLng iaLatLng = floorPlan.getCenter();
            LatLng center = new LatLng(iaLatLng.latitude, iaLatLng.longitude);
            GroundOverlayOptions fpOverlay = new GroundOverlayOptions()
                    .image(bitmapDescriptor)
                    .zIndex(0.0f)
                    .position(center, floorPlan.getWidthMeters(), floorPlan.getHeightMeters())
                    .bearing(floorPlan.getBearing());

            mGroundOverlay = mMap.addGroundOverlay(fpOverlay);
        }
    }

    /**
     * Download floor plan using Picasso library.
     */
    private void fetchFloorPlanBitmap(final IAFloorPlan floorPlan) {

        final String url = floorPlan.getUrl();

        if (mLoadTarget == null) {
            mLoadTarget = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d(TAG, "onBitmap loaded with dimensions: " + bitmap.getWidth() + "x"
                            + bitmap.getHeight());
                    setupGroundOverlay(floorPlan, bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    // N/A
                }

                @Override
                public void onBitmapFailed(Drawable placeHolderDraweble) {
                    showInfo("Failed to load bitmap");
                    mOverlayFloorPlan = null;
                }
            };
        }

        RequestCreator request = Picasso.with(this).load(url);

        final int bitmapWidth = floorPlan.getBitmapWidth();
        final int bitmapHeight = floorPlan.getBitmapHeight();

        if (bitmapHeight > MAX_DIMENSION) {
            request.resize(0, MAX_DIMENSION);
        } else if (bitmapWidth > MAX_DIMENSION) {
            request.resize(MAX_DIMENSION, 0);
        }

        request.into(mLoadTarget);
    }


    /**
     * Fetches floor plan data from IndoorAtlas server.
     */
    private void fetchFloorPlan(String id) {

        // if there is already running task, cancel it
        cancelPendingNetworkCalls();

        final IATask<IAFloorPlan> task = mResourceManager.fetchFloorPlanWithId(id);

        task.setCallback(new IAResultCallback<IAFloorPlan>() {

            @Override
            public void onResult(IAResult<IAFloorPlan> result) {

                if (result.isSuccess() && result.getResult() != null) {
                    // retrieve bitmap for this floor plan metadata
                    fetchFloorPlanBitmap(result.getResult());
                } else {
                    // ignore errors if this task was already canceled
                    if (!task.isCancelled()) {
                        // do something with error
                        showInfo("Loading floor plan failed: " + result.getError());
                        mOverlayFloorPlan = null;
                    }
                }
            }
        }, Looper.getMainLooper()); // deliver callbacks using main looper

        // keep reference to task so that it can be canceled if needed
        mFetchFloorPlanTask = task;

    }

    /**
     * Helper method to cancel current task if any.
     */
    private void cancelPendingNetworkCalls() {
        if (mFetchFloorPlanTask != null && !mFetchFloorPlanTask.isCancelled()) {
            mFetchFloorPlanTask.cancel();
        }
    }

    private void showInfo(String text) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.button_close, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void startListeningPlatformLocations() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    private void setUpMapIfNeeded()
    {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void FindTwoClosestCorners(){

        double[] distances = {1000,1000,1000,1000};


        for (int i = 0; i < 4; i++){

                double d_lat = Corners[i].latitude - center.latitude;
                double d_lon = Corners[i].longitude - center.longitude;

                double distance = Math.sqrt(Math.pow(d_lat,2) + Math.pow(d_lon, 2));

                distances[i] = distance;
        }

        LatLng corner1 = Corners[0];
        LatLng corner2 = Corners[0];
        LatLng corner3 = Corners[0];
        double min = 1000;

        for (int i = 0; i < 4; i++){
            if (distances[i] < min){
                min = distances[i];
                corner2 = corner1;
                corner1 = Corners[i];
            }
        }

        double d1;
        double d2;
        double dest_lat = 45.49730729;
        double dest_lon = -73.57848912;

        d1 = Math.sqrt(Math.pow(corner1.latitude - dest_lat,2) + ((Math.pow(corner1.longitude - dest_lon,2))));
        d2 = Math.sqrt(Math.pow(corner2.latitude - dest_lat,2) + ((Math.pow(corner2.longitude - dest_lon,2))));

        if (d1 < d2)
            corner3 = Corners[2];
        else
            corner3 = Corners[3];

        //45.49730729, -73.57848912

        LatLng dhsg=new  LatLng(latitude, longitude);
// Drawing path between coordinates
        mMap.addPolyline(new PolylineOptions().add(


                new LatLng(center.latitude, center.longitude),
                new LatLng(corner1.latitude, corner1.longitude),
                new LatLng(corner3.latitude, corner3.longitude),
                dhsg

                )
                        .width(20)
                        .color(Color.BLUE)


        );

        String x = "";
    }

    private void setUpMap()
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Room: " + room));


        // user 45.49701344, -73.57896275
        // dest 45.49739941, -73.57863262



//
//
//        LatLng dhsg=new  LatLng(latitude, longitude);
//// Drawing path between coordinates





//        // ... get a map.
//        // Add a thin red line from London to New York.
//        Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(latitude, longitude), new LatLng(40.7, -74.0))
//                .width(5)
//                .color(Color.RED));


    }



}
