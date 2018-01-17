package com.itslegit.niroigensuntharam.hivelabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static com.itslegit.niroigensuntharam.hivelabs.IntroActivity.MY_PREFS_NAME;

public class MainActivity extends AppCompatActivity{

    // Retrieving the date and time when the application is being launches
    public static String dateString = new SimpleDateFormat("yyyyMMdd", Locale.CANADA).format(new Date());
    public static String timeString = new SimpleDateFormat("HHmm", Locale.CANADA).format(new Date());

    // All rooms which will store the room number and its capacity
    static String[][] AvailableRooms = {{"807","811","813","815","817","819","821","823","825","827","831"
                                        ,"833","835","837","841","843","845","847","849","854","862","903"
                                        ,"907","909","911","913","915","917","921","929","962","966","967"
                                        ,"968"},
                                        {"16","20","16","18","22","20","16","20","16","16","30","16"
                                         ,"16","15","15","20","16","20","23","16","12","42","42","20"
                                         ,"16","16","16","27","16","50","24","19","50","20"}};

    // A list of all the rooms information
    // Ex: Capacity, Courses, and Time Slots
    static ArrayList<Room> Rooms = new ArrayList<>();

    private static final int REQUEST_CODE_HOVER_PERMISSION = 1000;

    private boolean mPermissionsRequested = false;
    static ViewPager viewPager;
    SwipeRefreshLayout swipeRefreshLayout;
    private final int CODE_PERMISSIONS = 0;

    static IALocationManager mIaLocationManager;

    IALocationListener mIALocationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation iaLocation) {
            Log.d(TAG, "Lattitude: " + iaLocation.getLatitude());
            Log.d(TAG, "Longitude: " + iaLocation.getLongitude());
            Log.d(TAG, "Floor number: " + iaLocation.getFloorLevel());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
    };

    static final Comparator<Room> TIME_COMPARATOR = new Comparator<Room>() {
        @Override
        public int compare(Room room1, Room room2) {
            int compareTime = room1.getNextTime();
            return compareTime - room2.getNextTime();
        }
    };

    public static ViewPagerAdapter adapter;

    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        IARegion mCurrentFloorPlan = null;

        @Override
        public void onEnterRegion(IARegion iaRegion) {
            if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN){
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
    public static Map<String, Coordinate> coordinates = new HashMap<String, Coordinate>();
    public static String earliestTime;
    public static ArrayList<Room> AllRooms = new ArrayList<>();
    public static boolean areRoomsInitialized = false;
    public static boolean areApplicationsInitialized = false;
    public static boolean areCoordinatesInitialized = false;
    public static boolean dateChanged = false;

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference mRootRef = database.getReference();
    public static DatabaseReference mRoomRef = mRootRef.child("rooms");
    public static DatabaseReference mAppRef = mRootRef.child("apps");
    public static DatabaseReference mDateRef = mRootRef.child("date");
    public static DatabaseReference mCoordRef = mRootRef.child("coordinates");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean doneTutorial = prefs.getBoolean("doneTutorial", false);

        if (!doneTutorial) {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            finish();
        }

        boolean doneRequests = prefs.getBoolean("requestAsked", false);
        if (!doneRequests){
            String[] neededPermissions = {
                    CHANGE_WIFI_STATE,
                    ACCESS_WIFI_STATE,
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
            };
            ActivityCompat.requestPermissions( this, neededPermissions, CODE_PERMISSIONS );

            // Request GPS locations
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setContentView(R.layout.activity_main);

        if (!isConnected(MainActivity.this))
            builderDialog(MainActivity.this).show();
        else
            setContentView(R.layout.activity_main);

        Initialization();

        mAppRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!areApplicationsInitialized) {

                    Map<String, Application> apps = new HashMap<String, Application>();

                    for (DataSnapshot jobSnapshot: dataSnapshot.getChildren()) {
                        Application app = jobSnapshot.getValue(Application.class);
                        apps.put(jobSnapshot.getKey(), app);
                    }

                    ArrayList<Application> _apps = new ArrayList<>(apps.values());

                    Applications.clear();

                    Applications.addAll(_apps);

                    areApplicationsInitialized = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!areRoomsInitialized) {

                    Map<String, Room> td = new HashMap<String, Room>();

                    for (DataSnapshot jobSnapshot: dataSnapshot.getChildren()) {
                        Room room = jobSnapshot.getValue(Room.class);
                        td.put(jobSnapshot.getKey(), room);
                    }

                    ArrayList<Room> rooms = new ArrayList<>(td.values());

                    Rooms = rooms;

                    for (Room room: Rooms) {
                        Room.VerifyIfAvalaible(room);
                    }

                    adapter.notifyDataSetChanged();

                    Room.EarliestAvailableTime();

                    AllRooms.addAll(Rooms);

//                    showSearchPrompt();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCoordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!areCoordinatesInitialized) {

                    for (DataSnapshot jobSnapshot: dataSnapshot.getChildren()) {
                        Coordinate coordinate = jobSnapshot.getValue(Coordinate.class);
                        coordinates.put(jobSnapshot.getKey(), coordinate);
                    }

                    areCoordinatesInitialized = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override

            public boolean onQueryTextSubmit(String s) {

                VerifyQuery(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                VerifyQuery(s);

                return false;
            }
        });

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRoomRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Room room = dataSnapshot.getValue(Room.class);

                for (int i = 0 ; i < Rooms.size(); i++){
                    if (Rooms.get(i).getRoomNumber().equals(room.getRoomNumber())){
                        Rooms.set(i, room);
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dateStored = dataSnapshot.getValue(String.class);

                if (dateStored != null && !dateStored.equals(dateString)){ // Get all the data for the current date

                    Toast.makeText(MainActivity.this, "Date Changed Please Update", Toast.LENGTH_SHORT).show();

                    dateString = dateStored;

                    dateChanged = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIaLocationManager.requestLocationUpdates(IALocationRequest.create(),
                mIALocationListener);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.rgb(147, 33, 56)));

//        // On Android M and above we need to ask the user for permission to display the Hover
//        // menu within the "alert window" layer.  Use OverlayPermission to check for the permission
//        // and to request it.
//        if (!mPermissionsRequested && !OverlayPermission.hasRuntimePermissionToDrawOverlay(this)) {
//            @SuppressWarnings("NewApi")
//            Intent myIntent = OverlayPermission.createIntentToRequestOverlayPermission(this);
//            startActivityForResult(myIntent, REQUEST_CODE_HOVER_PERMISSION);
//        }
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

    private void Initialization()
    {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();

        // Give the
        // TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                switch (tab.getPosition()) {
                    case 0:
                        Log.e("TAG","TAB1");
                        break;
                    case 1:
                        Log.e("TAG","TAB2");
                        break;
                    case 2:
                        Log.e("TAG","TAB3");
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

        // Initialize contacts
        // Attach the adapter to the recyclerview to populate items

        // That's all!

        mIaLocationManager = IALocationManager.create(this);
        mIaLocationManager.registerRegionListener(mRegionListener);
    }

    public boolean isConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null)
        {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo!=null && netInfo.isConnected()){

                android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                return (mobile!=null && mobile.isConnectedOrConnecting())|| (wifi.isConnectedOrConnecting());
            }
            else
                return false;
        }
        else
            return false;
    }

    public AlertDialog.Builder builderDialog(Context c)
    {
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

    public static void SetAllRooms(ArrayList<Room> rooms){
        AllRooms.addAll(rooms);
    }

    private void VerifyQuery(String query){
        if (!query.isEmpty()) {
            ArrayList<String> rooms = new ArrayList<>();

            for (Application app : Applications) {
                if (app.getApplication().toUpperCase().contains(query.toUpperCase())) {
                    rooms.addAll(app.getRoomsToUse());
                }
            }

            ArrayList<Room> roomsWithApp = new ArrayList<>();

            for (String _room : rooms) {
                for (Room room : AllRooms) {
                    if (room.getRoomNumber().contains(_room.substring(3, 6))) {
                        roomsWithApp.add(room);
                        break;
                    }
                }
            }

            Rooms.clear();
            Rooms.addAll(roomsWithApp);
        }
        else{
            Rooms.clear();
            Rooms.addAll(AllRooms);
        }

        adapter.notifyDataSetChanged();
    }

    public void showSearchPrompt()
    {
        new MaterialTapTargetPrompt.Builder(this)
                .setPrimaryText(R.string.search_prompt_title)
                .setSecondaryText(R.string.search_prompt_description)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setIcon(R.drawable.ic_search)
                .setTarget(findViewById(R.id.action_search))
                .show();
    }

        //Setting View Pager
    private void setupViewPager() {

            RoomFragment roomFragment1 = new RoomFragment();

            Bundle bundle1 = new Bundle();
            bundle1.putString("Type","Available");

            roomFragment1.setArguments(bundle1);

            RoomFragment roomFragment2 = new RoomFragment();

            Bundle bundle2 = new Bundle();
            bundle2.putString("Type","Upcoming");

            roomFragment2.setArguments(bundle2);

            RoomFragment roomFragment3 = new RoomFragment();

            Bundle bundle3 = new Bundle();
            bundle3.putString("Type","In Progress");

            roomFragment3.setArguments(bundle3);

            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFrag(roomFragment1, "Available");
            adapter.addFrag(roomFragment2, "Upcoming");
            adapter.addFrag(roomFragment3, "In Progress");
            viewPager.setAdapter(adapter);
        }
    }

    //View Pager fragments setting adapter class
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();//fragment arraylist
        private final List<String> mFragmentTitleList = new ArrayList<>();//title arraylist

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        //adding fragments and title method
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }