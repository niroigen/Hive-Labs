package com.example.niroigensuntharam.elec390application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by niroigensuntharam on 2017-11-05.
 */

public class NotificationHelper {

    Context mContext;

    public Timer t = new Timer();

    NotificationHelper(Context context)
    {
        mContext = context;

        String[] laterTime = MainActivity.currentRoom.getNextTime().split("-")[0].split(":");

        String startTime = new SimpleDateFormat("HHmm").format(new Date());

        long TimeLeft = (Integer.parseInt(laterTime[0].trim()+laterTime[1].trim()) - 15) - Integer.parseInt(startTime);

        Timer t = new Timer();

        t.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                //code that runs when timer is done
                sendNotification();
            }
        }, TimeLeft * 1000 * 60);
    }

    public void sendNotification() {
        //Get an instance of NotificationManager//

        Intent notificationIntent = new Intent(mContext, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(
                mContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.desktop_icon)
                .setContentTitle("Change Room")
                .setContentText("Room in this lab soon")
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[2];

        inboxStyle.setBigContentTitle("Reminder for Room Description");

        events[0] = "Room number: " + MainActivity.currentRoom.getRoomNumber();
        events[1] = "Course: " + MainActivity.currentRoom.getNextClass();

        for (int i = 0; i < events.length; i++)
        {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//

        //NotificationManager.notify().

        mNotificationManager.notify(001, mBuilder.build());
    }
}
