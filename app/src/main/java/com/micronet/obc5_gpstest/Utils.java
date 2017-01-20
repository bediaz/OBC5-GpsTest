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
}
