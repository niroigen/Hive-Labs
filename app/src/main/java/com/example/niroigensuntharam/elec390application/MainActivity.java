package com.example.niroigensuntharam.elec390application;

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
import java.io.IOException;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity{

    String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
    String timeString = new SimpleDateFormat("HHmm").format(new Date());

    static String[][] AvailableRooms = {{"807","811","813","815","817","819","821","823","825","827","831"
                                        ,"833","835","837","841","843","845","847","849","854","862"},
                                        {"16","20","16","18","22","20","16","20","16","16","30","16"
                                         ,"16","15","15","20","16","20","23","16","12"}};

    static ArrayList<Room> Rooms = new ArrayList<>();
    static ArrayList<Room> RoomsNowAvailable = new ArrayList<>();

    ListViewAdapter myCustomAdapter=null;
    ListView listView=null;
    Databasehelper db=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        //cars=myDbHelper.getData();

        InitializeRooms();

        myCustomAdapter= new ListViewAdapter(this, android.R.layout.simple_list_item_1, Rooms);

        listView = (ListView)findViewById(R.id.simpleListView);
        listView.setAdapter(myCustomAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView v = (TextView) view.findViewById(R.id.labName);
                Intent intent = new Intent(getBaseContext(), LabDetail.class);
                intent.putExtra("LAB_ID", v.getText());
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

    private void InitializeRooms()
    {
        Rooms.clear();
        RoomsNowAvailable.clear();

        for (int i = 0; i < AvailableRooms[0].length; i ++)
        {
            Room room = new Room(AvailableRooms[0][i],AvailableRooms[1][i]);

            VerifyIfAvalaible(room);

            Rooms.add(room);
        }
    }

    private void VerifyIfAvalaible(Room room)
    {
        boolean isAvailable = true;

        for (int i = 0; i < room.TimeList.size(); i++)
        {
            String[] time = room.TimeList.get(i).split("-");

            String[] startTime = time[0].split(":");

            String[] endTime = time[1].split(":");

            int TimeNow = Integer.parseInt(timeString);
            int StartTime = Integer.parseInt(startTime[0].trim() + startTime[1].trim());
            int EndTime = Integer.parseInt(endTime[0].trim() + endTime[1].trim());

            if (TimeNow >= StartTime && TimeNow < EndTime)
            {
                isAvailable = false;
                break;
            }
        }

        if (isAvailable)
            RoomsNowAvailable.add(room);
    }

    private void RefreshRooms()
    {
        String tempDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String tempTime = new SimpleDateFormat("HH:mm").format(new Date());

        // If after the user refreshes, and there is a change in the date
        // all the rooms will be initialized again
        if (tempDate !=dateString)
            InitializeRooms();

        else if (timeString != tempTime)
        {
            RoomsNowAvailable.clear();

            for (int i = 0; i < Rooms.size(); i++)
                VerifyIfAvalaible(Rooms.get(i));
        }
    }
}