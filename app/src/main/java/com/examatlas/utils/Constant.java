package com.examatlas.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Constant {

//    public static final String BASE_URL2 = "https://examatlas.com/api/";
    public static final String BASE_URL = "https://examatlas-backend.onrender.com/api/";
    public static final String PAYMENT_URL = "https://api.razorpay.com/v1/";

    public static String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String getAddedTime(String time1, String time2) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date1 = null;
        try {
            date1 = timeFormat.parse(time1);
            Date date2 = timeFormat.parse(time2);
            long sum = date1.getTime() + date2.getTime();
            return timeFormat.format(new Date(sum));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getConvertedDate(String date) {
        //yyyy-MM-dd
        String day = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date2 = formatter.parse(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            day = dateFormat.format(date2);
            System.out.println("New Converted Date: " + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

}
