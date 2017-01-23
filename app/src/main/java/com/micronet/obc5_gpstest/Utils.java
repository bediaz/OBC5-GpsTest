package com.micronet.obc5_gpstest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by brigham.diaz on 1/20/2017.
 */

public class Utils {
    public static String formatDate(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }

    public static String formatTimespan(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        long ms = time - seconds * 1000;

        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }
}
