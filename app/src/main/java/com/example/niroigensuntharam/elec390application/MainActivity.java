package com.example.niroigensuntharam.elec390application;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import io.mattcarroll.hover.overlay.OverlayPermission;

import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;

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

    private RecyclerView roomsView;
    private final int CODE_PERMISSIONS = 0;
    private RoomsAdapter adapter;

    private static final int REQUEST_CODE_HOVER_PERMISSION = 1000;

    private boolean mPermissionsRequested = false;

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
    static ArrayList<Room> RoomsNowAvailable = new ArrayList<>();
    static SwipeRefreshLayout swipeRefreshLayout = null;
    static Room currentRoom;
    static ArrayList<Application> Applications = new ArrayList<>();
    static AlertDialog dialog;
    static String earliestTime;
    private static ArrayList<Room> AllRooms = new ArrayList<>();
    private static boolean areRoomsInitialized = false;
    private static boolean areApplicationsInitialized = false;
    private static boolean dateChanged = false;

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference mRootRef = database.getReference();
    static DatabaseReference mRoomRef = mRootRef.child("rooms");
    static DatabaseReference mAppRef = mRootRef.child("apps");
    static DatabaseReference mDateRef = mRootRef.child("date");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (!isConnected(MainActivity.this))
            builderDialog(MainActivity.this).show();
        else
            setContentView(R.layout.activity_main);


        dialog = new SpotsDialog(this);
        dialog.show();

        String[] neededPermissions = {
                CHANGE_WIFI_STATE,
                ACCESS_WIFI_STATE,
                ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions( this, neededPermissions, CODE_PERMISSIONS );

        Initialization();

        mAppRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!areApplicationsInitialized) {
                    GenericTypeIndicator<ArrayList<Application>> apps = new GenericTypeIndicator<ArrayList<Application>>() {
                    };

                    ArrayList<Application> _apps = dataSnapshot.getValue(apps);

                    Applications.clear();

                    Applications.addAll(_apps);

                    areApplicationsInitialized = true;
                }

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!areRoomsInitialized) {
                    GenericTypeIndicator<ArrayList<Room>> _rooms = new GenericTypeIndicator<ArrayList<Room>>() {};

                    ArrayList<Room> rooms = dataSnapshot.getValue(_rooms);

                    Rooms.clear();

                    Rooms.addAll(rooms);

                    for(Room room: Rooms){
                        Room.VerifyIfAvalaible(room);
                    }

                    Room.SortRooms();

                    Room.EarliestAvailableTime();

                    AllRooms.addAll(Rooms);

                    areRoomsInitialized = true;

                    dialog.dismiss();

                    adapter.notifyDataSetChanged();

                    showSearchPrompt();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Handle if any of the permissions are denied, in grantResults
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

                Rooms.set(Integer.parseInt(dataSnapshot.getKey()), room);
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

        Intent stopHoverIntent = new Intent(MainActivity.this, SingleSectionHoverMenuService.class);
        stopService(stopHoverIntent);

        // On Android M and above we need to ask the user for permission to display the Hover
        // menu within the "alert window" layer.  Use OverlayPermission to check for the permission
        // and to request it.
        if (!mPermissionsRequested && !OverlayPermission.hasRuntimePermissionToDrawOverlay(this)) {
            @SuppressWarnings("NewApi")
            Intent myIntent = OverlayPermission.createIntentToRequestOverlayPermission(this);
            startActivityForResult(myIntent, REQUEST_CODE_HOVER_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_HOVER_PERMISSION == requestCode) {
            mPermissionsRequested = true;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
            super.onPause();
        mIaLocationManager.requestLocationUpdates(IALocationRequest.create(),
                mIALocationListener);

        if (isApplicationSentToBackground(this)){
            Intent startHoverIntent = new Intent(MainActivity.this, SingleSectionHoverMenuService.class);
            startService(startHoverIntent);
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    // Will refresh to show the rooms the user can currently go to
    private void RefreshRooms() {
        // If after the user refreshes, and there is a change in the date
        // all the rooms will be initialized again

        if (dateChanged ||
                (earliestTime != null && Integer.parseInt(earliestTime) < Integer.parseInt(new SimpleDateFormat("HHmm", Locale.CANADA).format(new Date())))) {

            Rooms.clear();

            timeString = new SimpleDateFormat("HHmm", Locale.CANADA).format(new Date());

            GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync(this);

            getRoomInfoAsync.execute(new SimpleDateFormat("yyyyMMdd", Locale.CANADA).format(new Date()));

            dateChanged = false;
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void Initialization()
    {

        roomsView = findViewById(R.id.rooms);

        // Initialize contacts
        adapter = new RoomsAdapter(this, Rooms);
        // Attach the adapter to the recyclerview to populate items
        roomsView.setAdapter(adapter);
        // Set layout manager to position the items
        roomsView.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

        mIaLocationManager = IALocationManager.create(this);
        mIaLocationManager.registerRegionListener(mRegionListener);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRooms();
            }
        });
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

        Room.SortRooms();

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
}