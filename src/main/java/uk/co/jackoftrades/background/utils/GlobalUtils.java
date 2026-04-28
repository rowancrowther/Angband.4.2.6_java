package uk.co.jackoftrades.background.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.background.io.parsers.ConstantTxtParser;
import uk.co.jackoftrades.background.io.parsers.ObjectBaseParser;
import uk.co.jackoftrades.middle.objects.ObjectBase;

import java.util.ArrayList;

public class GlobalUtils {
    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less that 128 may prevent some objects being created
     */
    public final static int maxRandDepth = 128;

    private static final Logger logger = LogManager.getLogger();
    public static ArrayList<ObjectBase> bases;

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

    /**
     * Loads the file object_base.txt and stores its array list in this class
     */
    public static void loadObjectBases() {
        ObjectBaseParser parser = new ObjectBaseParser();
        try {
            parser.parse();
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }

        ArrayList<ObjectBase> bases = parser.getBases();
    }
}