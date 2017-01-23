package com.micronet.obc5_gpstest;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by brigham.diaz on 1/18/2017.
 *
 * A fragment that displays the NMEA
 */
public class NmeaFragment extends Fragment implements Gps.OnNmeaUpdateListener {
    private final String TAG = "NmeaFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView textView;
    private TextView txtTimeToGetNMEA;
    private FloatingActionButton fab;
    private volatile boolean updateUI = true;
    private Gps gps;
    private long timespan;

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
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        gps.removeNmeaUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        gps = Gps.get(getActivity());
        gps.setOnNmeaUpdateListeners(this);
        cdt.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        gps.removeNmeaUpdateListener(this);
    }

    CountDownTimer cdt = new CountDownTimer(Long.MAX_VALUE, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            timespan = Long.MAX_VALUE - millisUntilFinished;
            txtTimeToGetNMEA.setText(String.format("TIME TO GET NMEA:%s", Utils.formatTimespan(timespan)));
        }

        @Override
        public void onFinish() {
            txtTimeToGetNMEA.setText(String.format("TIME TO GET NMEA:%s", Utils.formatTimespan(timespan)));
        }
    };


    @Override
    public void onNmeaUpdate(String nmea, long timestamp) {
        nmea = trimNMEASentence(nmea);
        nmea = separateSentenceGroups(nmea);
        addNMEA(nmea);
    }

    private String trimNMEASentence(String nmea) {
        if (nmea.length() <= 2) {
            return nmea;
        }
        // remove "$GP"
        nmea = nmea.substring(3, nmea.length());
        // remove checksum
        nmea = nmea.substring(0, nmea.length() - 6) + "\n";
        return nmea;
    }

    private String separateSentenceGroups(String nmea) {
        if(nmea.startsWith("GGA")) {
            return "\n" + nmea;
        }
        return nmea;
    }

    public void addNMEA(String sentence) {
        cdt.cancel();
        cdt.onFinish();

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

        txtTimeToGetNMEA = (TextView) rootView.findViewById(R.id.txtTimeToGetNMEA);
        return rootView;
    }


}

