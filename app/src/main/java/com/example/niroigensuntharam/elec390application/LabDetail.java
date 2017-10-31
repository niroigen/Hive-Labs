package com.example.niroigensuntharam.elec390application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class LabDetail extends AppCompatActivity {

    TextView roomNumber;
    TextView class_name;
    TextView lab_capacity;
    Button lab_week_schedule_url;

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

        String c = "";

        for (int i = 0; i < individualLab.getClassList().size(); i++)
        {
            c += individualLab.getClassList().get(i) + "\n" + individualLab.getTimeList().get(i) + "\n\n";
        }

        roomNumber = (TextView) findViewById(R.id.roomNumber);
        roomNumber.setText(individualLab.roomNumber);
        class_name = (TextView)findViewById(R.id.listOfClasses);
        class_name.setText(c);
        lab_capacity = (TextView) findViewById(R.id.capacity);
        lab_capacity.setText("Capacity: " + individualLab.capacity);
    }
}
