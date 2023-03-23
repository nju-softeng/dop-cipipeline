package com.example.agent.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static boolean isTimeDifferenceGreaterThan30Seconds(String timeString1, String timeString2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time1 = LocalDateTime.parse(timeString1, formatter);
        LocalDateTime time2 = LocalDateTime.parse(timeString2, formatter);
        Duration duration = Duration.between(time1, time2);
        long seconds = Math.abs(duration.getSeconds());
        return seconds>30;

    }
}
