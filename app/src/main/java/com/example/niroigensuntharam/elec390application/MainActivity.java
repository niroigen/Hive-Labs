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
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity{

    // Retrieving the date and time when the application is being launches
    String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
    public static String timeString = new SimpleDateFormat("HHmm").format(new Date());

    // All rooms which will store the room number and its capacity
    static String[][] AvailableRooms = {{"807","811","813","815","817","819","821","823","825","827","831"
                                        ,"833","835","837","841","843","845","847","849","854","862","903"
                                        ,"907","909","911","913","915","917","921","929","962","966","967"
                                        ,"968"},
                                        {"16","20","16","18","22","20","16","20","16","16","30","16"
                                         ,"16","15","15","20","16","20","23","16","12","42","42","20"
                                         ,"16","16","16","27","16","50","24","19","50","20"}};

    // A list of all the rooms informations
    // Ex: Capacity, Courses, and Time Slots
    public static ArrayList<Room> Rooms = new ArrayList<>();
    
    // List of all available rooms that the user 
    // can enter currently
    public static ArrayList<Room> RoomsNowAvailable = new ArrayList<>();
    public static ListViewAdapter myCustomAdapter=null;
    ListView roomListView = null;
    static public SwipeRefreshLayout swipeRefreshLayout = null;
    static public Room currentRoom;
    static public ArrayList<Application> Applications = new ArrayList<>();
    static public AlertDialog dialog;
    static public String earliestTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!isConnected(MainActivity.this))builderDialog(MainActivity.this).show();
        else {
            setContentView(R.layout.activity_main);
        }

        dialog = new SpotsDialog(this);
        dialog.show();

        GetRoomInfoAsync.isNetworkAvailable(this);

        GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync(this);

        getRoomInfoAsync.execute(dateString);
        Initialization();
    }

    // Will refresh to show the rooms the user can currently go to
    private void RefreshRooms()
    {
        String tempDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String tempTime = new SimpleDateFormat("HHmm").format(new Date());

        // If after the user refreshes, and there is a change in the date
        // all the rooms will be initialized again

        //Log.d("Time ",""+earliestTime);

        if (earliestTime != null && tempTime != null) {
            if (!tempTime.equals(timeString)) {

            GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync(this);

            timeString = tempTime;

            getRoomInfoAsync.execute(tempDate);
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void Initialization()
    {
        myCustomAdapter = new ListViewAdapter(this, android.R.layout.simple_list_item_1, Rooms);

        roomListView = (ListView) findViewById(R.id.simpleListView);
        roomListView.setAdapter(myCustomAdapter);
        roomListView.setCacheColorHint(Color.WHITE);

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view.findViewById(R.id.labName);
                Intent intent = new Intent(getBaseContext(), LabDetail.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        roomListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                String output = "";

                for (int j = 0; j < Applications.size(); j++)
                {
                    ArrayList<String> rooms = Applications.get(j).getRoomsToUse();

                    for (int k = 0; k < rooms.size(); k++)
                    {
                        if (rooms.get(k).substring(3,6).equals(Rooms.get(i).roomNumber))
                        {
                            output += Applications.get(j).getApplication() + "\n";
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

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRooms();
            }
        });
    }


    //

    public boolean isConnected(Context context)
    {
        ConnectivityManager cm=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo =cm.getActiveNetworkInfo();
        if (netinfo!=null&&netinfo.isConnected()){
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile!=null && mobile.isConnectedOrConnecting())|| (wifi.isConnectedOrConnecting()))
                return true; else return false;
        }else return false;
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


}
// Commented code


//rgb(121, 49, 0)

//        Databasehelper myDbHelper = new Databasehelper(getApplicationContext());
//        myDbHelper = new Databasehelper(this);
//        try {
//            myDbHelper.createDatabase();
//        } catch (IOException ioe) {
//            throw new Error("Unable to create database");
//        }
//        try {
//            myDbHelper.openDatabase();
//        }catch(SQLException sqle){
//            //throw sqle;
//        }


//db = new Databasehelper(this);
//        cars=myDbHelper.getData();

//        layout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
//
//        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                RefreshRooms();
//            }
//        });


//        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }