package com.itslegit.niroigensuntharam.hivelabs.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.itslegit.niroigensuntharam.hivelabs.Coordinate;
import com.itslegit.niroigensuntharam.hivelabs.MapsOverlayActivity;
import com.itslegit.niroigensuntharam.hivelabs.R;
import com.itslegit.niroigensuntharam.hivelabs.Room;
import com.itslegit.niroigensuntharam.hivelabs.ScheduledService;
import com.itslegit.niroigensuntharam.hivelabs.databinding.ActivityLabDetailBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LabDetailActivity extends AppCompatActivity {


    ActivityLabDetailBinding binding;
    double lat;
    double lon;
    String room;
    ArrayAdapter<String> myCustomAdapter=null;
    static AlarmManager mgr;

    static PendingIntent pi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_detail);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lab_detail);

        Button viewNavigationButton = findViewById(R.id.navigateButton);

        viewNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapsOverlayActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("room", room);
                try {
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        });


        room = getIntent().getExtras().getString("room");

        ArrayList<String> ClassAndTime = new ArrayList<>();

        Room tempRoom = new Room();

        for (Room room : MainActivity.Rooms) {
            if (room.getRoomNumber().equals(this.room)) {
                tempRoom = room;
                break;
            }
        }

        if (MainActivity.coordinates.containsKey(tempRoom.getRoomNumber())) {
            Coordinate co = MainActivity.coordinates.get(tempRoom.getRoomNumber());

            lat = co.getLatitude();
            lon = co.getLongitude();
        }

        final Room individualLab = tempRoom;

        for (int i = 0; i < individualLab.getTimeList().size(); i++) {
            ClassAndTime.add(individualLab.getClassList().get(i) + "\n" + individualLab.getTimeList().get(i));
        }

        myCustomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ClassAndTime);

        binding.classScheduleList.setAdapter(myCustomAdapter);
        binding.classScheduleList.setCacheColorHint(Color.WHITE);

        binding.roomNumber.setText(individualLab.getRoomNumber());

        if (MainActivity.currentRoom != null
                && MainActivity.currentRoom.getRoomNumber().equals(individualLab.getRoomNumber())
                || individualLab.getNextTime() == -1) {
            binding.currentRoomButton.setEnabled(false);
        }

        binding.currentRoomButton.setOnClickListener(new View.OnClickListener() {
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
                    Intent i = new Intent(LabDetailActivity.this, ScheduledService.class);
                    pi = PendingIntent.getService(LabDetailActivity.this, 0, i, 0);
                    mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeLeft * 60 * 1000, pi);

                    binding.currentRoomButton.setEnabled(false);

                    Toast.makeText(getApplicationContext(), "Current room set to " + binding.roomNumber.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(LabDetailActivity.this, ContactsActivity.class);
                    intent.putExtra("roomNumber", individualLab.getRoomNumber());
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        });
    }
}