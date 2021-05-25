package platform.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FormatDataTime {

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";

    public FormatDataTime() {}

    public static String getFormatterDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        return localDateTime.format(formatter);
    }

    public LocalDateTime getDateTimeFromString(String localDateTime) {
        return LocalDateTime.parse(localDateTime);
    }

}
