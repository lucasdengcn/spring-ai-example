package com.example.demo.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Method as Tools in Spring Ai
 */
@Slf4j
public class DateTimeTools {

    public static final String GET_CURRENT_DATE_TIME = "getCurrentDateTime";
    public static final String SET_ALARM = "setAlarm";

    @Tool(name = GET_CURRENT_DATE_TIME, description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(name = SET_ALARM, description = "Set a user alarm for the given time, provided in ISO-8601 format, UTC+8")
    public void setAlarm(@ToolParam(description = "Time in ISO-8601 format, UTC+8") String time){
        log.info("Setting alarm for: {}", time);
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        log.info("Alarm set for: {}", alarmTime);
    }
}
