package org.codespeak.distribution.client.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A utility class containing methods that work on dates and times
 *
 * @author Vector
 */
public class DateUtil {

    private static final String DEFAULT_DATETIME_FORMAT = "MMMM d, yyyy";
    
    private static String ensureISOFormat(String str) {
        return str.replace(" ", "T") + "Z";
    }
    
    /**
     * Returns a string representing the specified instant in the local time
     * zone using the default date time format
     * @param instant the instant to format
     * @return a string representing an instant in the local time zone according
     * to the default date time format
     */
    public static String formatInstant(Instant instant) {
        return formatInstant(instant, DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * Returns a string representing the specified instant in the local time
     * zone according to a specified format
     * @param instant the instant to format
     * @param format the specified format to use
     * @return a string representing an instant in the local time zone according
     * to a specified format
     */
    public static String formatInstant(Instant instant, String format) {
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());

        return zdt.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Gets an instant from the specified date time string
     * @param dateTimeString date time string to get an instant from
     * @return instant from a date time string
     */
    public static Instant getInstant(String dateTimeString) {
        Instant instant = null;
        
        try {
            instant = Instant.parse(dateTimeString);
        } catch (DateTimeParseException ex) {
            // Must be in the old date and time format, so convert to the
            // new format
            dateTimeString = ensureISOFormat(dateTimeString);
            
            instant = Instant.parse(dateTimeString);
        }
        
        return instant;
    }
    
}
