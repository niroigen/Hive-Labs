package com.example.niroigensuntharam.elec390application;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class ScheduledService extends IntentService{

    public ScheduledService() {
        super("My service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification();
    }

    @TargetApi(21)
    public void sendNotification() {
        //Get an instance of NotificationManager//

        Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this.getApplicationContext(),
                0,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getApplicationContext())
                .setSmallIcon(R.drawable.desktop_icon)
                .setContentTitle("Change Room")
                .setContentText("Room in this lab soon")
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[] { 1000, 500, 1000, 500 })
                .setLights(Color.MAGENTA, 1000, 1000)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[3];

        inboxStyle.setBigContentTitle("Reminder for Room Description");

        events[0] = "Room number: " + MainActivity.currentRoom.getRoomNumber();
        events[1] = "Course: " + MainActivity.currentRoom.getNextClass();
        events[2] = "Time: " + MainActivity.currentRoom.getNextTime();

        for (String event: events) {
            inboxStyle.addLine(event);
        }

        mBuilder.setStyle(inboxStyle);

        // Gets an instance of the NotificationManager service//
        NotificationManager mNotificationManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//

        if (mNotificationManager != null)
            mNotificationManager.notify(001, mBuilder.build());
    }
}
