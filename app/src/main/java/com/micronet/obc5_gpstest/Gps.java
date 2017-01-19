package com.micronet.obc5_gpstest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by brigham.diaz on 1/18/2017.
 */

public class Gps implements LocationListener {
    private final String TAG = "Gps";
    private static Gps gps;
    private static final long INTERVAL_ONE_SECOND = 1000; // minimum time interval between location updates, in milliseconds
    private LocationManager locationManager = null;
    private NmeaListener nmeaListener = null;
    private OnNmeaListener onNmeaListener = null;

    private OnUpdateListener onUpdateListener;

    public Gps() {
    }

    public static Gps get(Context appContext) {
        if (gps == null) {
            gps = new Gps(appContext.getApplicationContext());
        }
        return gps;
    }

    private Gps(Context appContext) {
        addLocationListener(appContext);
    }

    public void setOnUpdateListener(Fragment fragment) {
        this.onUpdateListener = (OnUpdateListener) fragment;
    }

    public interface OnUpdateListener {
        public void onUpdate(String nmea, long timestamp);
    }

    private void addLocationListener(Context appContext) {
        this.locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Unable to register LocationListener, ACCESS_FINE_LOCATION denied.");
            // TODO: Show error on ui
            return;
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_ONE_SECOND, 0, this);
        Log.i(TAG, "Requesting Location Updates Successful");

        boolean nmeaResult = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(onNmeaListener == null) {
                onNmeaListener = new OnNmeaListener();
                this.locationManager.addNmeaListener(onNmeaListener);
            }
        } else {
            if(nmeaListener == null) {
                nmeaListener = new NmeaListener();
                this.locationManager.addNmeaListener(nmeaListener);
            }
        }
    }


    private class NmeaListener implements GpsStatus.NmeaListener {

        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            Log.i(TAG, String.format("T:%d, %s", timestamp, nmea));
            if(onUpdateListener != null) {
                onUpdateListener.onUpdate(nmea, timestamp);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class OnNmeaListener implements OnNmeaMessageListener{
        @Override
        public void onNmeaMessage(String nmea, long timestamp) {
            Log.i(TAG, String.format("T:%d, %s", timestamp, nmea));
            if(onUpdateListener != null) {
                onUpdateListener.onUpdate(nmea, timestamp);
            }
        }
    }

    public void removeLocationListener() {
        if (this.locationManager != null) {
            try {
                // remove locaiton listener
                this.locationManager.removeUpdates(this);
                // remove nmea listener
                if(nmeaListener != null) {
                    this.locationManager.removeNmeaListener(nmeaListener);
                } else if(onNmeaListener != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.locationManager.removeNmeaListener(onNmeaListener);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            this.locationManager = null;
            Log.i(TAG, "Deregistered Location Listener");
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.toString());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, provider);
    }
}