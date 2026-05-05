package uk.co.jackoftrades.backend.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.ConstantTxtParser;

public class GlobalUtils {
    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less that 128 may prevent some objects being created
     */
    public final static int maxRandDepth = 128;

    private static final Logger logger = LogManager.getLogger();

    /**
     * private constructor to ensure that there is no access to this class apart from by its static methods
     */
    private GlobalUtils() {
    }

    public static void loadConstantsValue() {
        ConstantTxtParser ctp = new ConstantTxtParser();
        try {
            ctp.parse();
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}