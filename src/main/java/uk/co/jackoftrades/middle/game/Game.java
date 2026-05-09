package uk.co.jackoftrades.middle.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import uk.co.jackoftrades.backend.io.parsers.*;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.cave.TerrainFeature;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.PlayerProperty;
import uk.co.jackoftrades.middle.player.PlayerShape;

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
    private static ArrayList<Summon> summons;
    private static ArrayList<Curse> curses;
    private static ArrayList<PlayerShape> playerShapes;

    public static @Nullable ObjectBase getBaseFromTVal(TValue tVal) {
        for (ObjectBase base : objectBases) {
            if (base.gettVal() == tVal) {
                return base;
            }
        }

        return null;
    }

    public static @Nullable MonsterBase getBaseFromName(String name) {
        for (MonsterBase monsterBase : monsterBases) {
            if (monsterBase.getCodeName().equals(name)) {
                return monsterBase;
            }
        }

        return null;
    }

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
        loadSummons();
        loadCurses();
        loadPlayerShapes();
    }

    private void loadPlayerShapes() {
        PlayerShapeParser parser = new PlayerShapeParser();

        try {
            playerShapes = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "shape.txt");
        } catch (Exception e) {
            logger.error("Error while parsing shape file", e);
        }

//        for (PlayerShape shape : playerShapes) {
//                logger.info(shape.toString());
//        }
    }

    private void loadCurses() {
        CurseReader curseReader = new CurseReader();

        try {
            curses = curseReader.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "curse.txt");
        } catch (Exception e) {
            logger.error("Error while parsing curses file", e);
        }

//        for (Curse curse : curses) {
//            logger.info(curse.toString());
//        }
    }

    private void loadSummons() {
        SummonParser parser = new SummonParser();

        try {
            summons = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "summon.txt");
        } catch (Exception e) {
            logger.error("Error while parsing summons file!", e);
        }

//        for (Summon summon : summons) {
//            logger.info(summon.toString());
//        }
    }

    private void loadMonsterBases() {
        MonsterBaseParser monsterBaseParser = new MonsterBaseParser();

        try {
            monsterBases = monsterBaseParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "monster_base.txt");
        } catch (Exception e) {
            logger.error("Error while parsing MonsterBase", e);
        }

        /* for (MonsterBase monsterBase : monsterBases) {
            logger.info(monsterBase.toString());
        } */
    }

    private void loadPain() {
        MonsterPainParser parser = new MonsterPainParser();

        try {
            monsterPains = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "pain.txt");
        } catch (Exception e) {
            logger.error("Exception while parsing pain.txt", e);
        }

/*        for (MonsterPain monsterPain : monsterPains) {
            logger.info(monsterPain.toString());
        }*/
    }

    private void loadBrands() {
        BrandParser brandParser = new BrandParser();

        try {
            brands = brandParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "brand.txt");
        } catch (Exception e) {
            logger.error("Exception while parsing brands.txt", e);
        }

        /* for (Brand brand : brands) {
            logger.info(brand.toString());
        } */
    }

    private void loadSlays() {
        SlayFormatter slayFormatter = new SlayFormatter();

        try {
            slays = slayFormatter.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "slay.txt");
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
            objectBases = objectBaseParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "object_base.txt");
        } catch (IOException e) {
            logger.error("Error while loading object_base.txt file", e);
        }

/*        for (ObjectBase objectBase : objectBases) {
            logger.info(objectBase.toString());
        } */
    }

    private void loadTerrainFeatures() {
        terrainFeatures = new ArrayList<>();
        TerrainFeatureReader parser = new TerrainFeatureReader();

        try {
            // TODO - move and change this string from being hard-coded
            terrainFeatures = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "terrain.txt");
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
            playerProperties = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "player_property.txt");
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
            uiEntries = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "ui_entry.txt");
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
            uiBases = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "ui_entry_base.txt");
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
            uiEntryRenderers = renderer.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "ui_entry_renderer.txt");
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
            projections = projectionParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "projection.txt");
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
            worlds = worldParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "world.txt");
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