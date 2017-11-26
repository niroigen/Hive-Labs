package com.example.niroigensuntharam.elec390application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LabDetail extends AppCompatActivity {

    TextView roomNumber;
    TextView lab_capacity;
    Button currentRoomButton;
    Button contactsButton;
    ArrayAdapter myCustomAdapter=null;
    ListView listView = null;
    static AlarmManager mgr;

    static PendingIntent pi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab_detail);

        Button viewNavigationButton = findViewById(R.id.navigateButton);

        viewNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ImageViewActivity.class);
                startActivity(intent);
            }
        });


        String _roomNumber = getIntent().getExtras().getString("room");

        ArrayList<String> ClassAndTime = new ArrayList<>();

        Room tempRoom = new Room();

        for (Room room: MainActivity.Rooms){
            if (room.getRoomNumber().equals(_roomNumber)){
                tempRoom = room;
                break;
            }
        }

        final Room individualLab = tempRoom;

        for (int i = 0; i < individualLab.getTimeList().size(); i++)
        {
            ClassAndTime.add(individualLab.getClassList().get(i) + "\n" + individualLab.getTimeList().get(i));
        }

        myCustomAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, ClassAndTime);

        listView = (ListView) findViewById(R.id.classScheduleList);
        listView.setAdapter(myCustomAdapter);
        listView.setCacheColorHint(Color.WHITE);

        roomNumber = (TextView) findViewById(R.id.roomNumber);
        roomNumber.setText(individualLab.getRoomNumber());
        lab_capacity = (TextView) findViewById(R.id.capacity);
        lab_capacity.setText(getString(R.string.room_capacity, individualLab.getCapacity()));
        currentRoomButton = (Button) findViewById(R.id.currentRoomButton);
        contactsButton = (Button) findViewById(R.id.contactsButton);

        if (MainActivity.currentRoom != null
                && MainActivity.currentRoom.getRoomNumber().equals(individualLab.getRoomNumber())
                || individualLab.getNextTime() == -1)
        {
            currentRoomButton.setEnabled(false);
        }

        currentRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mgr != null)
                    mgr.cancel(pi);

                MainActivity.currentRoom = individualLab;

                String startTime = Integer.toString(MainActivity.currentRoom.getNextTime());

                if (MainActivity.currentRoom.getNextTime() != -1) {
                    String nowTime = new SimpleDateFormat("HH:mm", Locale.CANADA).format(new Date());

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

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LabDetail.this, ContactsActivity.class);
                intent.putExtra("roomNumber", individualLab.getRoomNumber());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent stopHoverIntent = new Intent(LabDetail.this, SingleSectionHoverMenuService.class);
        stopService(stopHoverIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (MainActivity.isApplicationSentToBackground(this)){
            Intent startHoverIntent = new Intent(LabDetail.this, SingleSectionHoverMenuService.class);
            startService(startHoverIntent);
        }
    }
}