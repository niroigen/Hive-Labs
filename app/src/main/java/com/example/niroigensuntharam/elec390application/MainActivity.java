package com.example.niroigensuntharam.elec390application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
    String timeString = new SimpleDateFormat("HHmm").format(new Date());

    static String[][] AvailableRooms = {{"807","811","813","815","817","819","821","823","825","827","831"
                                        ,"833","835","837","841","843","845","847","849","854","862"},
                                        {"16","20","16","18","22","20","16","20","16","16","30","16"
                                         ,"16","15","15","20","16","20","23","16","12"}};

    static ArrayList<Room> Rooms = new ArrayList<>();
    static ArrayList<Room> RoomsNowAvailable = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeRooms();
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