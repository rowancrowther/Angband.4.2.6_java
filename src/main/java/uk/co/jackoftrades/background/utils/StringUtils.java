package uk.co.jackoftrades.background.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class StringUtils {
    private final static Logger logger = LogManager.getLogger();

    /**
     * This is a class solely used for string utilities, and as such should never be instantiated.
     */
    private StringUtils() {}

    /**
     * concatenate a string with a formated string
     * @param string The string to put at the front of the concatenated string
     * @param format The string to format and put at the end of the concatenated string
     * @param objects The objects to use to create the formatted string from format
     * @return A string consisting of string followed by format which has been formatted
     *         with the objects passed in.
     */
    @Contract(pure = true)
    public static @NonNull String strnfcat(String string, String format, Object... objects) {
        return string + String.format(format, objects);
    }

    /**
     * Create a formatted string which is max characters long
     * @param max the length of the resultant string
     * @param format the string to format
     * @param objects the objects to use to format the string
     * @return A formatted string of length max
     */
    public static @NonNull String vstrnfmt(int max, String format, Object... objects) {
        return String.format(format, objects).substring(0, max - 1);
    }

    /**
     * Create a standard formatted string and return it with no size limit
     * @param stringToFormat The string to format
     * @param objects The objects to format the string with
     * @return A formatted string
     */
    @Contract(pure = true)
    public static @NonNull String vformat(String stringToFormat, Object... objects) {
        return String.format(stringToFormat, objects);
    }

    /**
     * Return a substring of a formatted string from 0 to max - 1
     * @param stringToFormat The string to format
     * @param max The length of the return value
     * @param objects The objects to use in formatting the string
     * @return A formatted string of size max
     */
    public static @NonNull String strnfmt(String stringToFormat, int max, Object... objects){
        return String.format(stringToFormat, objects).substring(0, max - 1);
    }

    /**
     * Format a string with a set of objects
     * @param format The string to format
     * @param objects The objects used to format that string
     * @return A formatted string
     */
    @Contract(pure = true)
    public static @NonNull String format(String format, Object... objects) {
        return String.format(format, objects);
    }

    /**
     * Log a formatted string with no objects (a straight string) at level INFO
     * @param string the string to log
     */
    public static void plogFmt(String string) {
        logger.info(string);
    }

    /**
     * Log a formatted string at level INFO
     * @param format The string to format
     * @param objects The objects to format it with
     */
    public static void plogFmt(String format, Object... objects) {
        logger.info(format, objects);
    }

    /**
     * Quit the program using a formatted quit string
     *
     * TODO: Implement the quit function from the z-util.c file
     * @param toFormat The string to format
     * @param objects The objects used to format this string
     */
    public static void quitFmt(String toFormat, Object... objects) {
//        quit(String.format(toFormat, objects));
    }
}
