package com.itslegit.niroigensuntharam.hivelabs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.itslegit.niroigensuntharam.hivelabs.Application;
import com.itslegit.niroigensuntharam.hivelabs.Coordinate;
import com.itslegit.niroigensuntharam.hivelabs.R;
import com.itslegit.niroigensuntharam.hivelabs.Room;
import com.itslegit.niroigensuntharam.hivelabs.RoomFragment;
import com.itslegit.niroigensuntharam.hivelabs.ViewPagerAdapter;
import com.itslegit.niroigensuntharam.hivelabs.helper.FirebaseHelper;
import com.itslegit.niroigensuntharam.hivelabs.helper.MainHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;

public class MainActivity extends AppCompatActivity {

    // Retrieving the date and time when the application is being launches
    public static String dateString = new SimpleDateFormat("yyyyMMdd", Locale.CANADA).format(new Date());
    public static String timeString = new SimpleDateFormat("HHmm", Locale.CANADA).format(new Date());

    // All rooms which will store the room number and its capacity
    public static String[][] AvailableRooms = {{"807", "811", "813", "815", "817", "819", "821", "823", "825", "827", "831"
            , "833", "835", "837", "841", "843", "845", "847", "849", "854", "862", "903"
            , "907", "909", "911", "913", "915", "917", "921", "929", "962", "966", "967"
            , "968"},
            {"16", "20", "16", "18", "22", "20", "16", "20", "16", "16", "30", "16"
                    , "16", "15", "15", "20", "16", "20", "23", "16", "12", "42", "42", "20"
                    , "16", "16", "16", "27", "16", "50", "24", "19", "50", "20"}};

    // A list of all the rooms information
    // Ex: Capacity, Courses, and Time Slots
    public static ArrayList<Room> Rooms = new ArrayList<>();

    static ViewPager viewPager;
    SwipeRefreshLayout swipeRefreshLayout;
    private MainHelper helper;

    static IALocationManager mIaLocationManager;

    IALocationListener mIALocationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation iaLocation) {
            Log.d(TAG, "Latitude: " + iaLocation.getLatitude());
            Log.d(TAG, "Longitude: " + iaLocation.getLongitude());
            Log.d(TAG, "Floor number: " + iaLocation.getFloorLevel());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
    };

    public static ViewPagerAdapter adapter;

    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        IARegion mCurrentFloorPlan = null;

        @Override
        public void onEnterRegion(IARegion iaRegion) {
            if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
                Log.d(TAG, "Entered " + iaRegion.getName());
                Log.d(TAG, "floor plan ID: " + iaRegion.getId());
                mCurrentFloorPlan = iaRegion;
            }
        }

        @Override
        public void onExitRegion(IARegion iaRegion) {

        }
    };

    private String TAG = "MainActivity";

    // List of all available rooms that the user
    // can enter currently
    public static ArrayList<Room> RoomsNowAvailable = new ArrayList<>();
    public static Room currentRoom;
    public static ArrayList<Application> Applications = new ArrayList<>();
    public static Map<String, Coordinate> coordinates = new HashMap<>();
    public static String earliestTime;
    public static ArrayList<Room> AllRooms = new ArrayList<>();
    public static boolean areRoomsInitialized = false;
    public static boolean areApplicationsInitialized = false;
    public static boolean areCoordinatesInitialized = false;
    public static boolean dateChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        helper = new MainHelper(this);

        FirebaseHelper firebaseHelper = new FirebaseHelper(this);

        firebaseHelper.initializeDatabase();

        SharedPreferences prefs = getSharedPreferences(IntroActivity.MY_PREFS_NAME, MODE_PRIVATE);
        boolean doneTutorial = prefs.getBoolean("doneTutorial", false);

        if (!doneTutorial) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        }

        boolean doneRequests = prefs.getBoolean("requestAsked", false);
        if (!doneRequests) {
            String[] neededPermissions = {
                    CHANGE_WIFI_STATE,
                    ACCESS_WIFI_STATE,
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
            };
            int CODE_PERMISSIONS = 0;
            ActivityCompat.requestPermissions(this, neededPermissions, CODE_PERMISSIONS);

            // Request GPS locations
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        viewPager = findViewById(R.id.viewpager);

        setContentView(R.layout.activity_main);

        if (!helper.isConnected()) {
            builderDialog(MainActivity.this).show();
        } else {
            setContentView(R.layout.activity_main);
        }

        Initialization();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchDoorItem = menu.findItem(R.id.action_search_room);

        SearchView searchDoorView = (SearchView) searchDoorItem.getActionView();

        searchDoorView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                helper.verifyQueryRoom(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                helper.verifyQueryRoom(s);
                return false;
            }
        });

        MenuItem searchApplicationItem = menu.findItem(R.id.action_search_application);

        SearchView searchApplicationView = (SearchView) searchApplicationItem.getActionView();

        searchApplicationView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                helper.verifyQueryApplication(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                helper.verifyQueryApplication(s);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIaLocationManager.requestLocationUpdates(IALocationRequest.create(),
                mIALocationListener);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(Color.rgb(147, 33, 56)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mIaLocationManager.requestLocationUpdates(IALocationRequest.create(),
                mIALocationListener);
    }

    private void Initialization() {
        viewPager = findViewById(R.id.viewpager);
        setupViewPager();

        // Give the
        // TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                switch (tab.getPosition()) {
                    case 0:
                        Log.e("TAG", "TAB1");
                        break;
                    case 1:
                        Log.e("TAG", "TAB2");
                        break;
                    case 2:
                        Log.e("TAG", "TAB3");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        mIaLocationManager = IALocationManager.create(this);
        mIaLocationManager.registerRegionListener(mRegionListener);
    }

    public AlertDialog.Builder builderDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        // Display internet connection
        builder.setTitle("No Connection");
        builder.setMessage("You need to have Mobile Data or wifi");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return builder;
    }

    public static void SetAllRooms(ArrayList<Room> rooms) {
        AllRooms.addAll(rooms);
    }

    public static void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void setupViewPager() {

        RoomFragment roomFragment1 = new RoomFragment();

        Bundle bundle1 = new Bundle();
        bundle1.putString("Type", "Available");

        roomFragment1.setArguments(bundle1);

        RoomFragment roomFragment2 = new RoomFragment();

        Bundle bundle2 = new Bundle();
        bundle2.putString("Type", "Upcoming");

        roomFragment2.setArguments(bundle2);

        RoomFragment roomFragment3 = new RoomFragment();

        Bundle bundle3 = new Bundle();
        bundle3.putString("Type", "In Progress");

        roomFragment3.setArguments(bundle3);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(roomFragment1, "Available");
        adapter.addFrag(roomFragment2, "Upcoming");
        adapter.addFrag(roomFragment3, "In Progress");
        viewPager.setAdapter(adapter);
    }
}