package com.micronet.obc5_gpstest;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by brigham.diaz on 1/18/2017.
 */

public class LocationFragment extends Fragment implements Gps.OnLocationUpdateListener, Gps.OnGpsStatusListener{
    private final String TAG = "LocationFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView textView;
    private TextView txtSatellites;
    private FloatingActionButton fab;
    private volatile boolean updateUI = true;
    private Gps gps;

    public LocationFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LocationFragment newInstance(int sectionNumber) {
        LocationFragment instance = new LocationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        gps.removeGpsStatusListener(this);
        gps.removeLocationUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        gps = Gps.get(getActivity());
        gps.setOnGpsStatusListeners(this);
        gps.setOnLocationUpdateListeners(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        gps.removeGpsStatusListener(this);
        gps.removeLocationUpdateListener(this);
    }

    public void addNMEA(String sentence) {
        class OneShotTextUpdate implements Runnable {
            private String message;

            private OneShotTextUpdate(String message) {
                this.message = message;
            }

            public void run() {
                if(textView.length() > 10000) {
                    textView.setText(message);
                } else {
                    textView.append(message);
                }
            }
        }
        if (updateUI) {
            getActivity().runOnUiThread(new OneShotTextUpdate(sentence));
        }
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        textView = (TextView) rootView.findViewById(R.id.txtLocation);
        txtSatellites = (TextView) rootView.findViewById(R.id.txtSatellites);
        return rootView;
    }


    @Override
    public void onLocationUpdate(Location location, String locationStr) {
        textView.setText(locationStr);
    }

    @Override
    public void onStatusChanged(String status) {
        txtSatellites.setText(status);
    }

}

