package com.example.niroigensuntharam.elec390application;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Timer;

public class LabDetail extends AppCompatActivity {

    TextView roomNumber;
    TextView class_name;
    TextView lab_capacity;
    Button currentRoomButton;
    ArrayAdapter myCustomAdapter=null;
    ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab_detail);

        Databasehelper myDbHelper = new Databasehelper(getApplicationContext());
        myDbHelper = new Databasehelper(this);
        //Intent intent = getIntent();
        int position = getIntent().getExtras().getInt("position");
        //room = myDbHelper.getDataByName(temp[1].toString());
        final Room individualLab = MainActivity.Rooms.get(position);

        ArrayList<String> ClassAndTime = new ArrayList<>();

        for (int i = 0; i < individualLab.getTimeList().size(); i++)
        {
            ClassAndTime.add(individualLab.getClassList().get(i) + "\n" + individualLab.getTimeList().get(i));
        }

        myCustomAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, ClassAndTime);

        listView = (ListView) findViewById(R.id.classScheduleList);
        listView.setAdapter(myCustomAdapter);
        listView.setCacheColorHint(Color.WHITE);

//        String c = "";
//
//        for (int i = 0; i < individualLab.getClassList().size(); i++)
//        {
//            c += individualLab.getClassList().get(i) + "\n" + individualLab.getTimeList().get(i) + "\n\n";
//        }

        roomNumber = (TextView) findViewById(R.id.roomNumber);
        roomNumber.setText(individualLab.roomNumber);
//        class_name = (TextView)findViewById(R.id.listOfClasses);
//        class_name.setText(c);
        lab_capacity = (TextView) findViewById(R.id.capacity);
        lab_capacity.setText("Capacity: " + individualLab.capacity);
        currentRoomButton = (Button) findViewById(R.id.currentRoomButton);

        if (MainActivity.currentRoom != null
                && MainActivity.currentRoom.roomNumber == individualLab.roomNumber)
        {
            currentRoomButton.setEnabled(false);
        }

        currentRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentRoom = individualLab;


                NotificationHelper.t = new Timer();

                new NotificationHelper(LabDetail.this);

                currentRoomButton.setEnabled(false);
            }
        });
    }
}