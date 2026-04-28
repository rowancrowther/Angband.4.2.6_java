package uk.co.jackoftrades.middle.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

public class Game {
    private static Logger logger = LogManager.getLogger();

    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less that 128 may prevent some objects being created
     */
    private final static int maxRandDepth = 128;

    /*
     * The various holders for the \lib\gamedata files. All of these are static as they hold the main data files.
     * They have to be read in carefully, in the order that they appear here, so running the static method init() will
     *  pull them
     * all in and log a message to the trace log that they have been read in correctly.
     */

    /**
     * World is simply a string in a linked list. It starts and ends with null (instead of the C "none" tag). There
     * should be exactly 128 levels, as outlined in the maxRandDepth, and should be from the town (level 0) to Angband
     * 127.
     */
    private static LinkedList<String> world;


}