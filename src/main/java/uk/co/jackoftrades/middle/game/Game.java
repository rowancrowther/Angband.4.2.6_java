package uk.co.jackoftrades.middle.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.background.io.parsers.ProjectionParser;
import uk.co.jackoftrades.background.io.parsers.UIEntryRendParser;
import uk.co.jackoftrades.background.io.parsers.WorldParser;
import uk.co.jackoftrades.frontend.screen.UIEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Game {
    private static final Logger logger = LogManager.getLogger();

    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less than 128 may prevent some objects being created
     */
    private final static int maxRandDepth = 128;

    /*
     * The various holders for the \lib\gamedata files. All of these are static as they hold the main data files.
     * They have to be read in carefully, in the order that they appear here, so running the static method init() will
     * pull them all in and log a message to the trace log that they have been read in correctly.
     */

    /**
     * World is simply a string in a linked list. It starts and ends with null (instead of the C "none" tag). There
     * should be exactly 128 levels, as outlined in the maxRandDepth, and should be from the town (level 0) to Angband
     * 127.
     */
    private static LinkedList<World> world;
    private static ArrayList<Projection> projections;
    private static ArrayList<UIEntry> uiEntries;

    /**
     * Initialise the game objects in the correct order
     */
    public void init() {
        loadWorld();
        loadProjection();
        loadUIEntryRenderer();
    }

    private void loadUIEntryRenderer() {
        uiEntries = new ArrayList<>();

        UIEntryRendParser renderer = new UIEntryRendParser();

        try {
            // TODO - move this string from being hard coded to being calculated from the run location
            uiEntries = renderer.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\ui_entry_renderer.txt");
        } catch (Exception e) {
            logger.error("Error while loading UI Entry Renderers", e);
        }

        for (UIEntry uiEntry : uiEntries) {
            logger.info(uiEntry.toString());
        }
    }

    private void loadProjection() {
        projections = new ArrayList<>();

        ProjectionParser projectionParser = new ProjectionParser();

        try {
            // TODO - move this string from being hard coded to being calculated from the run location
            projections = projectionParser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\projection.txt");
        } catch (Exception e) {
            logger.error("Error while loading projections", e);
        }

        /* logger.info("Loaded " + projections.size() + " projections");
        for (Projection projection : projections) {
            logger.info(projection.toString());
        } */
    }

    private void loadWorld() {
        world = new LinkedList<>();

        WorldParser worldParser = new WorldParser();
        HashMap<Integer, ArrayList<String>> worlds = new HashMap<>();

        try {
            // TODO - move this string from being hard coded to being calculated from the run location
            worlds = worldParser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\world.txt");
        } catch (IOException e) {
            logger.error("Error while loading world", e);
        }

        for (int i : worlds.keySet()) {
            ArrayList<String> readWorld = worlds.get(i);

            int prev = i - 1;
            int next = i < maxRandDepth - 1 ? i + 1 : -1;

            World thisWorld = new World(i, readWorld.get(0), prev, next);
            world.add(thisWorld);
        }

        /* Logged the results of this load - worked perfectly
        logger.info("Loaded " + world.size() + " worlds");

        for (World w : world) {
            logger.info(w.toString());
        }
        */
    }
}