package com.example.niroigensuntharam.elec390application;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    // Retrieving the date and time when the application is being launches
    String dateString = "20171101";//new SimpleDateFormat("yyyyMMdd").format(new Date());
    public static String timeString = "1000";//new SimpleDateFormat("HHmm").format(new Date());

    // All rooms which will store the room number and its capacity
    static String[][] AvailableRooms = {{"807","811","813","815","817","819","821","823","825","827","831"
                                        ,"833","835","837","841","843","845","847","849","854","862"},
                                        {"16","20","16","18","22","20","16","20","16","16","30","16"
                                         ,"16","15","15","20","16","20","23","16","12"}};

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
    Databasehelper db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync(this);

        getRoomInfoAsync.execute(dateString);

        Initialization();

        sendNotification();
    }

    public void sendNotification() {
        //Get an instance of NotificationManager//

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.desktop_icon)
                        .setContentTitle("Change Room")
                        .setContentText("Room in this lab soon")
                        .setContentIntent(contentIntent);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[2];

        inboxStyle.setBigContentTitle("Reminder for Room Description");

        events[0] = "Room number: 823";// + currentRoom.getRoomNumber();
        events[1] = "Course: COEN 390";// + currentRoom.getNextClass();

        for (int i = 0; i < events.length; i++)
        {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//

        //NotificationManager.notify().

        mNotificationManager.notify(001, mBuilder.build());
    }

    // Will refresh to show the rooms the user can currently go to
    private void RefreshRooms()
    {
        String tempDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String tempTime = new SimpleDateFormat("HHmm").format(new Date());

        // If after the user refreshes, and there is a change in the date
        // all the rooms will be initialized again
        //if (!tempDate.equals(dateString))

        GetRoomInfoAsync getRoomInfoAsync = new GetRoomInfoAsync(this);

        getRoomInfoAsync.execute(tempDate);
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

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRooms();
            }
        });
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
