package com.micronet.obc5_gpstest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;

/**
 * Created by brigham.diaz on 1/19/2017.
 */

public class GpsBackgroundService extends Service implements Gps.OnLocationUpdateListener {
    private Gps gps;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        gps = Gps.get(getApplicationContext());
        gps.setOnLocationUpdateListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public void createNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_bw)
                .setContentTitle("OBC 5 GPS Test")
                .setContentText("Running");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        // Gets an instance of the NotificationManager service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(21, mBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationUpdate(Location location, String locationStr) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_bw)
                .setContentTitle(String.format("%09.6f\t\t%10.6f", location.getLatitude(), location.getLongitude()))
        ;
        // Builds the notification and issues it.
        notificationManager.notify(21, mBuilder.build());

    }
}
