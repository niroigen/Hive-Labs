package com.itslegit.niroigensuntharam.hivelabs.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import com.itslegit.niroigensuntharam.hivelabs.ScheduledService;
import com.itslegit.niroigensuntharam.hivelabs.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by niro on 2018-02-12.
 */

public class RoomHelper {
    private AlarmManager alarmManager;
    private Context context;
    private PendingIntent pendingIntent = null;

    public RoomHelper(Context context) {
        this.context = context;
    }

    public void setCurrentRoom(String roomNumber) {
        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

        String startTime = Integer.toString(MainActivity.currentRoom.getNextTime());

        if (MainActivity.currentRoom.getNextTime() != -1) {
            String nowTime = new SimpleDateFormat("HH:mm", Locale.CANADA).format(new Date());

            long MinutesStartTime = Long.parseLong(startTime.substring(0, 2)) * 60 + Long.parseLong(startTime.substring(2, 4));

            long MinutesNowTime = Long.parseLong(nowTime.split(":")[0]) * 60 + Long.parseLong(nowTime.split(":")[1].trim());

            long TimeLeft = MinutesStartTime - MinutesNowTime - 15;

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, ScheduledService.class);
            pendingIntent = PendingIntent.getService(context, 0, i, 0);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeLeft * 60 * 1000, pendingIntent);

            Toast.makeText(context, "Current room set to " + roomNumber, Toast.LENGTH_SHORT).show();
        }
    }
}
