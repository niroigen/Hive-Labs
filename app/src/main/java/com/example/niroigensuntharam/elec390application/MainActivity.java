package com.example.niroigensuntharam.elec390application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.view.View;
import android.widget.AdapterView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

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
    
    // List of all available rooms that the user 
    // can enter currently
    static ArrayList<Room> RoomsNowAvailable = new ArrayList<>();
    static ListViewAdapter myCustomAdapter = null;
    ListView roomListView = null;
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

                    dialog.dismiss();
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
                    GenericTypeIndicator<ArrayList<Room>> _rooms = new GenericTypeIndicator<ArrayList<Room>>() {};

                    ArrayList<Room> rooms = dataSnapshot.getValue(_rooms);

                    Rooms.clear();

                    Rooms.addAll(rooms);

                    for(Room room: Rooms){
                        Room.VerifyIfAvalaible(room);
                    }

                    Room.SortRooms();

                    Room.EarliestAvailableTime();

                    myCustomAdapter.notifyDataSetChanged();

                    AllRooms.addAll(Rooms);

                    areRoomsInitialized = true;

                    dialog.dismiss();
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

                Rooms.set(Integer.parseInt(dataSnapshot.getKey()), room);

                myCustomAdapter.notifyDataSetChanged();
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

    // Will refresh to show the rooms the user can currently go to
    private void RefreshRooms() {
        // If after the user refreshes, and there is a change in the date
        // all the rooms will be initialized again

        if (dateChanged ||
                (earliestTime != null && Integer.parseInt(earliestTime) > Integer.parseInt(new SimpleDateFormat("HHmm", Locale.CANADA).format(new Date())))) {

            Rooms.clear();

            myCustomAdapter.notifyDataSetChanged();

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
        myCustomAdapter = new ListViewAdapter(this, android.R.layout.simple_list_item_1, Rooms);

        roomListView = findViewById(R.id.simpleListView);
        roomListView.setAdapter(myCustomAdapter);
        roomListView.setCacheColorHint(Color.WHITE);

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), LabDetail.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        roomListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                StringBuilder output = new StringBuilder();

                for (int j = 0; j < Applications.size(); j++)
                {
                    ArrayList<String> rooms = Applications.get(j).getRoomsToUse();

                    for (int k = 0; k < rooms.size(); k++)
                    {
                        if (rooms.get(k).substring(3,6).equals(Rooms.get(i).getRoomNumber()))
                        {
                            output.append(Applications.get(j).getApplication());
                            output.append("\n");
                        }
                    }
                }

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Software Available");
                alertDialog.setMessage(output);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return true;
            }
        });

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
            NetworkInfo netInfo =cm.getActiveNetworkInfo();
            if (netInfo!=null && netInfo.isConnected()){

                android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                return (mobile!=null && mobile.isConnectedOrConnecting())|| (wifi.isConnectedOrConnecting());
            }
            else
                return false;
        }
        else
            return false;
    }

    public  AlertDialog.Builder builderDialog(Context c)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(c);
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

        myCustomAdapter.notifyDataSetChanged();
    }
}
// Commented code

//        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }