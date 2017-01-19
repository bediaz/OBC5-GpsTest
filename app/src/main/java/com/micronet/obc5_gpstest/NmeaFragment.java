package com.micronet.obc5_gpstest;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by brigham.diaz on 1/18/2017.
 *
 * A fragment that displays the NMEA
 */
public class NmeaFragment extends Fragment implements Gps.OnUpdateListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView textView;
    private FloatingActionButton fab;
    private volatile boolean updateUI = true;
    private Gps gps;

    public NmeaFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NmeaFragment newInstance(int sectionNumber) {
        NmeaFragment instance = new NmeaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gps = Gps.get(getActivity());
        gps.setOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gps != null) {
            gps.removeLocationListener();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        gps = Gps.get(getActivity());
        gps.setOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (gps != null) {
            gps.removeLocationListener();
        }
    }


    @Override
    public void onUpdate(String nmea, long timestamp) {
        addNMEA(trimNMEA(nmea));
    }

    private String trimNMEA(String nmea) {
        if(nmea.length() <= 2) { return nmea;}

        // remove "$GP"
        nmea = nmea.substring(3, nmea.length());
        // remove checksum
        nmea = nmea.substring(0, nmea.length() - 6) + "\n";
        return nmea;
    }

    public void addNMEA(String message) {
        class OneShotTextUpdate implements Runnable {
            private String message;

            private OneShotTextUpdate(String message) {
                this.message = message;
            }

            public void run() {
                textView.append(message);
            }
        }
        if (updateUI) {
            getActivity().runOnUiThread(new OneShotTextUpdate(message));
        }
    }

    private View.OnClickListener fabNmeaUIStatusClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateUI = !updateUI;
            if (updateUI) {
                fab.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                fab.setImageResource(android.R.drawable.ic_media_play);
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nmea, container, false);
        textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setMovementMethod(new ScrollingMovementMethod());

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(fabNmeaUIStatusClick);
        return rootView;
    }


}

