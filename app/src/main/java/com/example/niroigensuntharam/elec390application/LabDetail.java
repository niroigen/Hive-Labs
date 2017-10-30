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

    TextView lab_name;
    TextView lab_capacity;
    Button lab_week_schedule_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_detail);

        Databasehelper myDbHelper = new Databasehelper(getApplicationContext());
        myDbHelper = new Databasehelper(this);
        //Intent intent = getIntent();
        String s = getIntent().getExtras().getString("LAB_ID");
        Log.v("Lab : ", s);
        String temp[] = s.split(" ");
        //room = myDbHelper.getDataByName(temp[1].toString());
        final Room individualLab = MainActivity.Rooms.get(0);
        Log.v("Data : ", individualLab.getClassList().get(0));

        lab_name = (TextView)findViewById(R.id.lab_name);
        lab_name.setText(individualLab.getClassList().get(0));
        lab_capacity = (TextView) findViewById(R.id.capacity);
        lab_capacity.setText(individualLab.capacity);
        lab_week_schedule_url = (Button) findViewById(R.id.btn_week_schedule);
        lab_week_schedule_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uri uri = Uri.parse(individualLab.getLab_week_schedule_url()); // missing 'http://' will cause crashed
                //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //startActivity(intent);
            }
        });
    }
}
