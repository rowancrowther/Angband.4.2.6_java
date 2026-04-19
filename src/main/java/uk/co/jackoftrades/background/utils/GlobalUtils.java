package uk.co.jackoftrades.background.utils;

public abstract class GlobalUtils {
    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less that 128 may prevent some objects being created
     */
    public final static int maxRandDepth = 128;
}
