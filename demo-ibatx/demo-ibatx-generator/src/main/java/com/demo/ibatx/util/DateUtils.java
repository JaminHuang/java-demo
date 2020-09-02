package com.demo.ibatx.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");


    public static String getDateString(){

        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }


}
