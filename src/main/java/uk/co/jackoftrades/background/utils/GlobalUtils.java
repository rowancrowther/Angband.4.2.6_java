package uk.co.jackoftrades.background.utils;

import uk.co.jackoftrades.background.io.parsers.ConstantTxtParser;

public class GlobalUtils {
    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less that 128 may prevent some objects being created
     */
    public final static int maxRandDepth = 128;

    /**
     * private constructor to ensure that there is no access to this class apart from by its static methods
     */
    private GlobalUtils() {
    }

    public static void loadConstantsValue() {
        ConstantTxtParser ctp = new ConstantTxtParser();
        try {
            ctp.parse();
        } catch (Exception ignored) {
            // TODO: At present this block is ignored. We may have to change this as we progress further
        }
    }
}