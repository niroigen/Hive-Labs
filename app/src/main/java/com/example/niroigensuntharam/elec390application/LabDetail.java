package com.example.niroigensuntharam.elec390application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class LabDetail extends AppCompatActivity {

    TextView roomNumber;
    TextView lab_capacity;
    Button currentRoomButton;
    ArrayAdapter myCustomAdapter=null;
    ListView listView = null;
    static AlarmManager mgr;

    static PendingIntent pi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab_detail);

        int position = getIntent().getExtras().getInt("position");

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


        roomNumber = (TextView) findViewById(R.id.roomNumber);
        roomNumber.setText(individualLab.roomNumber);
        lab_capacity = (TextView) findViewById(R.id.capacity);
        lab_capacity.setText("Capacity: " + individualLab.capacity);
        currentRoomButton = (Button) findViewById(R.id.currentRoomButton);

        if (MainActivity.currentRoom != null
                && MainActivity.currentRoom.roomNumber == individualLab.roomNumber
                || individualLab.getNextTime() == -1)
        {
            currentRoomButton.setEnabled(false);
        }

        currentRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mgr != null)
                    mgr.cancel(pi);

                ScheduledService.mContext = LabDetail.this;

                MainActivity.currentRoom = individualLab;

                String startTime = Integer.toString(MainActivity.currentRoom.getNextTime());

                if (MainActivity.currentRoom.getNextTime() != -1) {
                    String nowTime = new SimpleDateFormat("HH:mm").format(new Date());

                    long MinutesStartTime = Long.parseLong(startTime.substring(0, 2)) * 60 + Long.parseLong(startTime.substring(2, 4));

                    long MinutesNowTime = Long.parseLong(nowTime.split(":")[0]) * 60 + Long.parseLong(nowTime.split(":")[1].trim());

                    long TimeLeft = MinutesStartTime - MinutesNowTime - 15;

                    mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(LabDetail.this, ScheduledService.class);
                    pi = PendingIntent.getService(LabDetail.this, 0, i, 0);
                    mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeLeft * 60 * 1000, pi);

                    currentRoomButton.setEnabled(false);
                }
            }
        });
    }
}