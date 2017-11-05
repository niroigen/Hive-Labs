package com.example.niroigensuntharam.elec390application;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.common.annotation.KeepForSdkWithFieldsAndMethods;

import java.io.IOException;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity{

    // Retrieving the date and time when the application is being launces
    String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
    String timeString = new SimpleDateFormat("HHmm").format(new Date());

    // All rooms which will store the room number and its capacity
    static String[][] AvailableRooms = {{"807","811","813","815","817","819","821","823","825","827","831"
                                        ,"833","835","837","841","843","845","847","849","854","862"},
                                        {"16","20","16","18","22","20","16","20","16","16","30","16"
                                         ,"16","15","15","20","16","20","23","16","12"}};

    // A list of all the rooms informations
    // Ex: Capacity, Courses, and Time Slots
    static ArrayList<Room> Rooms = new ArrayList<>();
    
    // List of all available rooms that the user 
    // can enter currently
    static ArrayList<Room> RoomsNowAvailable = new ArrayList<>();

    ListViewAdapter myCustomAdapter=null;
    ListView listView = null;
    SwipeRefreshLayout layout = null;
    Databasehelper db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        InitializeRooms();

        myCustomAdapter= new ListViewAdapter(this, android.R.layout.simple_list_item_1, Rooms);

        listView = (ListView) findViewById(R.id.simpleListView);
        listView.setAdapter(myCustomAdapter);
        listView.setCacheColorHint(Color.WHITE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view.findViewById(R.id.labName);
                Intent intent = new Intent(getBaseContext(), LabDetail.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

//        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }

    }

    // Initializing to get all the rooms information
    private void InitializeRooms()
    {
        // Clearing all the rooms and available rooms
        Rooms.clear();
        RoomsNowAvailable.clear();

        // Looping through all the rooms
        // and getting its information
        for (int i = 0; i < AvailableRooms[0].length; i ++)
        {
            // Passing in the room number and its capacity
            Room room = new Room(AvailableRooms[0][i],AvailableRooms[1][i]);

            // Verifying whether the room is currently available or not
            VerifyIfAvalaible(room);

            // Adding the room to the list of rooms
            Rooms.add(room);
        }
    }

    // Will be used to verify whether a room is currently available
    // and if it is, then it will be added to the RoomsNowAvailable list
    private void VerifyIfAvalaible(Room room)
    {
        // The availability of a certain lab
        boolean isAvailable = true;

        boolean isNextClass = false;

        for (int i = 0; i < room.TimeList.size(); i++)
        {
            // Getting the time of a certain course
            // Ex: 12:45 - 13:55
            String[] time = room.TimeList.get(i).split("-");

            // Retrieving the start time
            // Ex: 12:45
            String[] startTime = time[0].split(":");

            // Retrieving the end time
            // Ex: 13:55
            String[] endTime = time[1].split(":");

            // Getting an integer value for the timeString
            // Ex: 1500
            int TimeNow = Integer.parseInt(timeString);
            
            // Getting an integer value for the startTime
            // Ex: 1245
            int StartTime = Integer.parseInt(startTime[0].trim() + startTime[1].trim());
            
            // Getting an integer value for the endTime
            // Ex: 1355
            int EndTime = Integer.parseInt(endTime[0].trim() + endTime[1].trim());

            if (TimeNow < StartTime && !isNextClass)
            {
                room.setNextClass(room.getClassList().get(i));
                room.setNextClassTime(room.getTimeList().get(i));
                isNextClass = true;
            }

            // Verifying whether the room is currently unavailable
            if (TimeNow >= StartTime && TimeNow < EndTime)
            {
                // Set the availability to false
                isAvailable = false;

                room.setCurrentClass(room.getClassList().get(i));
                
                // Break from the loop
                break;
            }
        }

        // If the room is available add it to the list of available rooms
        if (isAvailable) {
            RoomsNowAvailable.add(room);
        }
    }

    // Will refresh to show the rooms the user can currently go to
    private void RefreshRooms()
    {
        String tempDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String tempTime = new SimpleDateFormat("HHmm").format(new Date());

        // If after the user refreshes, and there is a change in the date
        // all the rooms will be initialized again
        if (!tempDate.equals(dateString))
            InitializeRooms();

        if (!timeString.equals(tempTime))
        {
            RoomsNowAvailable.clear();

            for (int i = 0; i < Rooms.size(); i++)
                VerifyIfAvalaible(Rooms.get(i));

            myCustomAdapter = new ListViewAdapter(this, android.R.layout.simple_list_item_1, Rooms);
        }

        layout.setRefreshing(false);
    }
}
