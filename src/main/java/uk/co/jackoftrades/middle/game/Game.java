package uk.co.jackoftrades.middle.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import uk.co.jackoftrades.backend.io.parsers.*;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;

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

    /**
     * World is simply a string in a linked list. It starts and ends with null (instead of the C "none" tag). There
     * should be exactly 128 levels, as outlined in the maxRandDepth, and should be from the town (level 0) to Angband
     * 127.
     */
    private static LinkedList<World> world;
    private static ArrayList<Projection> projections;
    private static ArrayList<UIEntryRenderer> uiEntryRenderers;
    private static ArrayList<UIEntryBase> uiBases;
    private static ArrayList<UIEntry> uiEntries;

    public static @Nullable UIEntryRenderer getUIEntryRenderer(String name) {
        if (uiEntryRenderers == null) return null;

        for (UIEntryRenderer entry : uiEntryRenderers) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Initialise the game objects in the correct order
     */
    public void init() {
        loadWorld();
        loadProjections();
        loadUIEntryRenderers();
        loadUIEntryBases();
        loadUIEntries();
    }

    private void loadUIEntries() {
        uiEntries = new ArrayList<>();

        UIEntryParser parser = new UIEntryParser();

        try {
            // TODO - move and change this string from being hard-coded
            uiEntries = parser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\ui_entry.txt");
        } catch (Exception e) {
            logger.error("Error while loading UI entries!", e);
        }

        for (UIEntry entry : uiEntries) {
            logger.info(entry.toString());
        }
    }

    private void loadUIEntryBases() {
        uiBases = new ArrayList<>();

        UIEntryBaseParser parser = new UIEntryBaseParser();

        try {
            // TODO - move this string from being hard coded to being calculated from the run location
            uiBases = parser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\ui_entry_base.txt");
        } catch (Exception e) {
            logger.error("Error while loading UI entry base.", e);
        }

        for (UIEntryBase entry : uiBases) {
            logger.info(entry.toString());
        }
    }

    private void loadUIEntryRenderers() {
        uiEntryRenderers = new ArrayList<>();

        UIEntryRendParser renderer = new UIEntryRendParser();

        try {
            // TODO - move this string from being hard coded to being calculated from the run location
            uiEntryRenderers = renderer.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\ui_entry_renderer.txt");
        } catch (Exception e) {
            logger.error("Error while loading UI Entry Renderers", e);
        }

        /*
        for (UIEntryRenderer uiEntryRenderer : uiEntries) {
            logger.info(uiEntryRenderer.toString());
        }*/
    }

    private void loadProjections() {
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