package uk.co.jackoftrades.middle.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import uk.co.jackoftrades.backend.io.parsers.*;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.cave.TerrainFeature;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.player.PlayerProperty;

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
    private static ArrayList<PlayerProperty> playerProperties;
    private static ArrayList<TerrainFeature> terrainFeatures;
    private static ArrayList<ObjectBase> objectBases;
    private static ArrayList<Slay> slays;
    private static ArrayList<Brand> brands;
    private static ArrayList<MonsterPain> monsterPains;
    private static ArrayList<MonsterBase> monsterBases;

    public static @Nullable MonsterPain getPainFromIndex(int index) {
        for (MonsterPain monsterPain : monsterPains) {
            if (monsterPain.getPainIndex() == index) {
                return monsterPain;
            }
        }

        return null;
    }

    public static @Nullable UIEntryRenderer getUIEntryRenderer(String name) {
        if (uiEntryRenderers == null) return null;

        for (UIEntryRenderer entry : uiEntryRenderers) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }

        return null;
    }

    public static @Nullable UIEntry getUIEntry(String name) {
        if (uiEntries == null) return null;
        for (UIEntry entry : uiEntries) {
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
        loadPlayerProperties();
        loadTerrainFeatures();
        loadObjectBases();
        loadSlays();
        loadBrands();
        loadPain();
        loadMonsterBases();
    }

    private void loadMonsterBases() {
        MonsterBaseParser monsterBaseParser = new MonsterBaseParser();

        try {
            monsterBases = monsterBaseParser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\monster_base.txt");
        } catch (Exception e) {
            logger.error("Error while parsing MonsterBase", e);
        }

        for (MonsterBase monsterBase : monsterBases) {
            logger.info(monsterBase.toString());
        }
    }

    private void loadPain() {
        monsterPains = new ArrayList<>();
        MonsterPainParser parser = new MonsterPainParser();

        try {
            monsterPains = parser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\pain.txt");
        } catch (Exception e) {
            logger.error("Exception while parsing pain.txt", e);
        }

/*        for (MonsterPain monsterPain : monsterPains) {
            logger.info(monsterPain.toString());
        }*/
    }

    private void loadBrands() {
        brands = new ArrayList<>();
        BrandParser brandParser = new BrandParser();

        try {
            brands = brandParser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\brand.txt");
        } catch (Exception e) {
            logger.error("Exception while parsing brands.txt", e);
        }

        /* for (Brand brand : brands) {
            logger.info(brand.toString());
        } */
    }

    private void loadSlays() {
        slays = new ArrayList<>();
        SlayFormatter slayFormatter = new SlayFormatter();

        try {
            slays = slayFormatter.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\slay.txt");
        } catch (Exception e) {
            logger.error("Exception thrown during loading Slays.", e);
        }

/*        for (Slay slay : slays) {
            logger.info(slay.toString());
        } */
    }

    private void loadObjectBases() {
        objectBases = new ArrayList<>();
        ObjectBaseParser objectBaseParser = new ObjectBaseParser();

        try {
            objectBases = objectBaseParser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\object_base.txt");
        } catch (IOException e) {
            logger.error("Error while loading object_base.txt file", e);
        }

        /* for (ObjectBase objectBase : objectBases) {
            logger.info(objectBase.toString());
        } */
    }

    private void loadTerrainFeatures() {
        terrainFeatures = new ArrayList<>();
        TerrainFeatureReader parser = new TerrainFeatureReader();

        try {
            // TODO - move and change this string from being hard-coded
            terrainFeatures = parser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\terrain.txt");
        } catch (Exception e) {
            logger.error("Error while loading terrain properties!", e);
        }

        /* for (TerrainFeature terrainFeature : terrainFeatures) {
            logger.info(terrainFeature.toString());
        } */
    }

    private void loadPlayerProperties() {
        playerProperties = new ArrayList<>();
        PlayerPropertyReader parser = new PlayerPropertyReader();

        try {
            // TODO - move and change this string from being hard-coded
            playerProperties = parser.parse("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\player_property.txt");
        } catch (Exception e) {
            logger.error("Error while loading player properties!", e);
        }

        /* for (PlayerProperty property : playerProperties) {
            logger.info(property.toString());
        } */
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

        /* for (UIEntry entry : uiEntries) {
            logger.info(entry.toString());
        } */
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

        /* for (UIEntryBase entry : uiBases) {
            logger.info(entry.toString());
        } */
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

            World thisWorld = new World(i, readWorld.getFirst(), prev, next);
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