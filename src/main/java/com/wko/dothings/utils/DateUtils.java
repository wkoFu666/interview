package com.wko.dothings.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String nowDateStr() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
        return format.format(date.getTime());
    }
}
