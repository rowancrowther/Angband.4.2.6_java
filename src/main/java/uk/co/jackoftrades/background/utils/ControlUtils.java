package uk.co.jackoftrades.background.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.background.utils.quit.Quit;

public class ControlUtils {
    private static Logger logger = LogManager.getLogger();

    /**
     * Log a formatted string with no objects (a straight string) at level INFO
     * @param string the string to log
     */
    public static void plogFmt(@NotNull String string) {
        logger.warn(string);
    }

    /**
     * Log a formatted string at level INFO
     * @param format The string to format
     * @param objects The objects to format it with
     */
    public static void plogFmt(@NotNull String format, Object... objects) {
        logger.warn(format, objects);
    }

    /**
     * Log a warning message to the log file
     * @param string The warning message to log
     */
    public static void plog(String string) { logger.warn(string); }

    /**
     * Quit the program using a formatted quit string
     * @param toFormat The string to format
     * @param objects The objects used to format this string
     */
    public static void quitFmt(String toFormat, Object... objects) {
        Quit q = new Quit();
        q.quit(String.format(toFormat, objects));
    }

    /**
     * Quit the program using a formatted quit string
     */
    public static void quit() {
        Quit q = new Quit();
        q.quit("");
    }

    /**
     * Quit the program using a straight string
     * @param string THe string message to give out
     */
    public static void quit(String string) {
        Quit q = new Quit();
        q.quit(string);
    }
}