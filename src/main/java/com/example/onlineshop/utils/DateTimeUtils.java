package com.example.onlineshop.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

    private DateTimeFormatter dateFormatter;

    public DateTimeUtils(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public boolean isValid(String dateStr) {
        try {
            LocalDateTime.parse(dateStr, this.dateFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static String getCurrentDateUTC() {
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public static String getDateUTCPlus(Integer plusDates) {
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, plusDates);
        return df.format(c.getTime());
    }
}
