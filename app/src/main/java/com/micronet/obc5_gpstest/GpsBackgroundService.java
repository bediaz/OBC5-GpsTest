package com.micronet.obc5_gpstest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Created by brigham.diaz on 1/19/2017.
 */

public class GpsBackgroundService extends Service implements Gps.OnLocationUpdateListener {
    private Gps gps;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private int ic_location_icon = 0;

    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        gps = Gps.get(getApplicationContext());
        gps.setOnLocationUpdateListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public void createNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(getSmallIcon())
                .setContentTitle("No Position Determined")
                .setContentText("Searching")
                .setShowWhen(false);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(getSmallIcon())
                .setContentTitle("Location Determined by " + location.getProvider().toUpperCase())
                .setContentText(String.format("%s\t\t%09.6f\t\t%10.6f", Utils.formatDate(location.getTime()), location.getLatitude(), location.getLongitude()))
                .setContentIntent(pendingIntent)
                .setShowWhen(false);

        // Builds the notification and issues it.
        notificationManager.notify(21, mBuilder.build());
    }

    private int getSmallIcon() {
        return (++ic_location_icon % 5 == 0) ? R.drawable.ic_stat_no_satellite : R.drawable.ic_stat_satellite;
    }
}
