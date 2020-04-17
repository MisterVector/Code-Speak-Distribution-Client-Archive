package org.codespeak.distribution.client.util;

/**
 * Class containing various string methods
 *
 * @author Vector
 */
public class StringUtil {
    
    /**
     * Returns true if the specified string is null or empty
     * @param s the string to test
     * @return whether the string is null or empty
     */
    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.isEmpty());
    }
    
}
