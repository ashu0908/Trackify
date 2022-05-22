package com.android.arijit.firebase.walker.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static LocalDate calenderToLocalDate(Calendar calendar) {
        return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    public static Date calendarToDate(Calendar calendar) {
        LocalDate date = calenderToLocalDate(calendar);
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
