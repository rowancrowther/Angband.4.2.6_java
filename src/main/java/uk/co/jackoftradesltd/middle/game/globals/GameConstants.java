/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.game.globals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import uk.co.jackoftradesltd.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftradesltd.backend.parser.GameConstantsParseResult;
import uk.co.jackoftradesltd.backend.parser.GameConstantsReader;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.loaders.*;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Archery;
import uk.co.jackoftradesltd.middle.objects.ElementPowers;
import uk.co.jackoftradesltd.middle.objects.ElementSet;
import uk.co.jackoftradesltd.middle.objects.FlagSet;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ResType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The global holder for the game's tunable constants and the startup entry point that loads all
 * game data — the Java port's equivalent of the C original's {@code z-info} constants and its
 * {@code init_*} bootstrap. After the loader/registry refactor it holds:
 * <ul>
 *   <li>process-wide constants and paths: the {@code angband.ini} location, {@link #MAX_PVAL},
 *       {@link #MAX_COMMAND_ARGUMENTS}, and the mutable window/graphics state flags;</li>
 *   <li>the scalar constants read from {@code constants.txt} (held in {@link #data} and exposed
 *       through the many {@code get*} accessors below — {@code mon-gen}, {@code dun-gen},
 *       {@code world}, {@code carry-cap}, {@code melee}/{@code ranged-critical}, and so on), which
 *       mirror one-to-one the per-value documentation in the {@code backend.utils.globalvalues}
 *       classes;</li>
 *   <li>the {@link #init()} pipeline, which calls each domain's {@code *DataLoader} in dependency
 *       order to populate the per-domain registries.</li>
 * </ul>
 *
 * <p>The per-type master lists (monsters, objects, features, projections, classes, …) and their
 * {@code lookup*} accessors <em>no longer live here</em>: they were split out into the
 * {@code registry} package (read side) and {@code loaders} package (write side), one slice per
 * domain, leaving this class as the constants holder plus the init orchestrator.
 *
 * <p>It is a static-only holder (private constructor). The scalar {@code get*} accessors read
 * {@link #data}, so they must not be called before {@link #init()} has loaded {@code constants.txt}.
 *
 * @author Rowan Crowther
 */
public class GameConstants {
    /**
     * Logger used to report load failures and premature/invalid access.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * A <code>record</code> used to store Sting name, int value pairs for the <code>constants.txt</code> file.
     * Currently only used for the <code>loadGameConstants</code> method. May be extended later.
     *
     * @param name  the String key
     * @param value the int value
     */
    private record NameValuePair(@NotNull String name, Integer value) {
    }

    /**
     * The ceiling on an object's {@code pval} - charges on a wand or staff, and gold in a pile.
     * C's {@code MAX_PVAL}, and the same figure as a signed 16-bit maximum because that is the width
     * of the field it caps in the original.
     */
    public static final int MAX_PVAL = 32_767;
    /**
     * How many arguments a queued command may carry - the port of the fixed argument array in C's
     * {@code struct command}.
     */
    public static final int MAX_COMMAND_ARGUMENTS = 4;

    /**
     * Full path to the {@code angband.ini} settings file.
     *
     * <p>Duplicated by {@link #iniFile} below, which is built from the same two parts; the pair
     * should be reduced to one.
     */
    public static final String ANGBAND_INI = AngbandDirs.BASE_DIR + "angband.ini";

    /**
     * Name of the platform-specific back end, C's {@code ANGBAND_SYS}, which uses it to pick
     * system-specific data files. The placeholder value stands in until the port has a real one to
     * name.
     */
    public static final String ANGBAND_SYS = "xxx";

    /**
     * Full path to the {@code angband.ini} settings file. Identical to {@link #ANGBAND_INI}; see the
     * note there.
     */
    public static final String iniFile = AngbandDirs.BASE_DIR + "angband.ini";
    /**
     * Number of cave profiles loaded from {@code dungeon_profile.txt} - the bound on the level
     * builders. See the note above on why these counts may not be needed.
     */
    private static int caveProfileMax;
    /**
     * The scalar tunables read from {@code constants.txt}, which every {@code get*} accessor below
     * reads through.
     *
     * <p>Left unset when the file fails to parse - that is a soft failure, logged and skipped - so
     * the accessors have to cope with its absence rather than assuming a load succeeded.
     */
    private static GameConstantsData data;
    /**
     * True once a character exists and the turn loop is running, rather than the game sitting in its
     * start-up screens.
     */
    public boolean gameInProgress = false;
    /**
     * True once the data files have been loaded and the registries filled.
     */
    public boolean initialised = false;
    /**
     * True while the game is running itself as a screensaver - a mode C's Windows front end offers.
     */
    public boolean screensaverActive = false;

    /*
     * Array bounds from C Not sure that these are still needed as most of the 'things' are stored in ArrayLists. Need to set
     * them on the load function,
     */
    private static int storeMax;
    /**
     * True when the front end can display graphical tiles rather than characters.
     */
    public boolean canUseGraphics = false;
    /**
     * Set when the front end has been asked to change its tile size, and cleared once it has.
     */
    public boolean changeTileSize = false;

    /**
     * Load the scalar tunables from {@code constants.txt} into {@link #data}, which the {@code get*}
     * accessors read. A file with parse errors is logged and skipped, leaving {@code data} unset
     * (soft failure); an IO error is logged and rethrown. Called first in {@link #init()} because
     * nothing else here depends on it, but the running game reads these values throughout.
     *
     * @throws IOException if the file cannot be read
     */
    private static void loadGameConstants() throws IOException {
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "constants.txt";
        GameConstantsReader reader = new GameConstantsReader();
        GameConstantsParseResult result;

        try {
            result = reader.parse(filename);
            if (result.hasErrors()) return;
        } catch (IOException e) {
            logger.error("Error while reading file {}", filename, e);
            throw e;
        }

        data = result.getData();
    }

    /**
     * Load every game-data file at startup, in dependency order, by delegating to each domain's
     * {@code *DataLoader}. The ordering is load-bearing: a loader that resolves cross-references
     * against another domain must run after that domain is populated — hence the inline
     * {@code // Dependent on …} notes on the calls below (for example monster bases before summons,
     * item objects before ego items and artifacts, monsters before quests).
     *
     * <p>{@link ObjectRegistry#reset()} runs first so the call is idempotent: the object-kind table
     * and its per-base sval counters are rebuilt from scratch, so a re-init (e.g. between tests) does
     * not double-register kinds. Any failure is logged and rethrown wrapped in a
     * {@link RuntimeException}, since the game cannot run with partially-loaded data.
     */
    public static void init() {
        try {
            // Signal EVENT_ENTER_INIT
            EventsHandler bus = GameEngine.getEventsBusHandler();
            bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");

            // Start from an empty kind registry so init() is idempotent: object kinds and their
            // per-base sval counters are rebuilt from scratch here, so a re-init (e.g. between tests)
            // does not double-register kinds or keep incrementing svals.
            ObjectRegistry.reset();

            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising game constants...");
            loadGameConstants();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising world...");
            WorldDataLoader.loadWorld();                // world arraylist size determines maxRandDepth
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising game projections...");
            WorldDataLoader.loadProjections();          // projections arrayList size determines projectionTypeMax
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising UI Entry Renderers...");
            UIDataLoader.loadUIEntryRenderers();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising UI Entry Bases...");
            UIDataLoader.loadUIEntryBases();         // Dependent on UIEntryRenderers
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising UI Entries...");
            UIDataLoader.loadUIEntries();            // Dependent on UIEntryBase & UIEntryRenderers
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising player properties...");
            PlayerDataLoader.loadPlayerProperties();     // Dependent on UIEntry
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising terrain features...");
            TerrainDataLoader.loadTerrainFeatures();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising object bases...");
            ObjectDataLoader.loadObjectBases();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising pain messages...");
            MonsterDataLoader.loadPain();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising monster bases...");
            MonsterDataLoader.loadMonsterBases();         // Dependent on MonsterPain
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising object slays...");
            ObjectDataLoader.loadSlays();                // Dependent on MonsterBases
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising object brands...");
            ObjectDataLoader.loadBrands();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising monster summons...");
            MonsterDataLoader.loadSummons();              // Dependent on MonsterBases
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising curses...");
            ObjectDataLoader.loadCurses();               // Dependent on ObjectBases, & Summons
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising player shapes...");
            PlayerDataLoader.loadPlayerShapes();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising objects...");
            ObjectDataLoader.loadItemObjects();          // Dependent on Summons, Curse, Brand, Slay & ObjectBase
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising activations...");
            ObjectDataLoader.loadActivations();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising ego items...");
            ObjectDataLoader.loadEgoItems();             // Dependent on Activations, Brand, Slay & Curse
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising player histories...");
            PlayerDataLoader.loadPlayerHistories();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising player bodies...");
            PlayerDataLoader.loadBodies();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising player races...");
            PlayerDataLoader.loadPlayerRaces();          // Dependent on PlayerBodies & PlayerHistories
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising magic...");
            PlayerDataLoader.loadMagicRealms();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising player classes...");
            PlayerDataLoader.loadPlayerClasses();        // Dependent on ItemObjects, Summons, MagicRealms
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising artifacts...");
            ObjectDataLoader.loadArtifacts();            // Dependent on Activations, ObjectKind, Brand, Slay & Curse
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising object properties...");
            ObjectDataLoader.loadObjectProperties();     // Dependent on UIEntry
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising player times properties...");
            PlayerDataLoader.loadPlayerTimedProperties();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising blow methods...");
            MonsterDataLoader.loadBlowMethods();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising blow effects...");
            MonsterDataLoader.loadBlowEffects();          // Dependent on Projections
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising monster spell types...");
            MonsterDataLoader.loadMonsterSpellTypes();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising colour tables...");
            MonsterDataLoader.loadVisualTables();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising monsters...");
            MonsterDataLoader.loadMonsters();             // Dependent on MonsterBase, VisualsCyclerTable, BlowMethods & VisualColours
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising pit profiles...");
            MonsterDataLoader.loadPitProfiles();          // Dependent on Monsters, MonsterBase & MonsterSpellTypes
            // TODO: Add in lore parsing and uncomment below two lines
            //bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising lore...");
//            loadMonsterLore();          // Dependent on MonsterKind, MonsterBase & ObjectKind (amongst others)
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising traps...");
            TerrainDataLoader.loadTraps();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising quests...");
            WorldDataLoader.loadQuests();               // Dependent on Monster
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising hints...");
            MiscDataLoader.loadHints();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising names...");
            MiscDataLoader.loadNames();
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising flavours...");
            MiscDataLoader.loadFlavours();
            // TODO: Add chest traps

            // Load global tables
            PlayerDataLoader.initialiseExpLevel();
        } catch (Exception e) {
            String message = "Unable to load data from " + AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + " error message: " + e.getMessage();
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        // Put together the various table constants

        // Archery
        ObjectRegistry.archery = new HashMap<>();
        Archery arch = new Archery(TValue.TV_SHOT, 10, 9, 4);
        ObjectRegistry.archery.put(TValue.TV_SHOT, arch);
        arch = new Archery(TValue.TV_ARROW, 12, 9, 5);
        ObjectRegistry.archery.put(TValue.TV_ARROW, arch);
        arch = new Archery(TValue.TV_BOLT, 14, 9, 7);
        ObjectRegistry.archery.put(TValue.TV_BOLT, arch);

        ObjectRegistry.flagSets = new HashMap<>();
        FlagSet flagSet = new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains");
        ObjectRegistry.flagSets.put(ObjectFlagType.OFT_SUST, flagSet);
        flagSet = new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections");
        ObjectRegistry.flagSets.put(ObjectFlagType.OFT_PROT, flagSet);
        flagSet = new FlagSet(ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities");
        ObjectRegistry.flagSets.put(ObjectFlagType.OFT_MISC, flagSet);

        ObjectRegistry.elementSets = new ArrayList<>();
        ElementSet elementSet = new ElementSet(ResType.T_LRES, 3, 6, ObjectRegistry.INHIBIT_POWER, 4, 0, "immunities");
        ObjectRegistry.elementSets.add(elementSet);
        elementSet = new ElementSet(ResType.T_LRES, 1, 1, 10, 4, 0, "low resists");
        ObjectRegistry.elementSets.add(elementSet);
        elementSet = new ElementSet(ResType.T_HRES, 1, 2, 10, 9, 0, "high resists");
        ObjectRegistry.elementSets.add(elementSet);

        ObjectRegistry.elementPowers = new ArrayList<>();
        ElementPowers elementPower = new ElementPowers(ElementEnum.ELEM_ACID, "acid", ResType.T_LRES, 3, -6, 5, 38);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_ELEC, "electricity", ResType.T_LRES, 1, -6, 6, 35);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_FIRE, "fire", ResType.T_LRES, 3, -6, 6, 40);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_COLD, "cold", ResType.T_LRES, 1, -6, 6, 37);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_POIS, "poison", ResType.T_HRES, 0, 0, 28, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_LIGHT, "light", ResType.T_HRES, 0, 0, 6, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_DARK, "dark", ResType.T_HRES, 0, 0, 16, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_SOUND, "sound", ResType.T_HRES, 0, 0, 14, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_SHARD, "shards", ResType.T_HRES, 0, 0, 8, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_NEXUS, "nexus", ResType.T_HRES, 0, 0, 15, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_NETHER, "nether", ResType.T_HRES, 0, 0, 20, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_CHAOS, "chaos", ResType.T_HRES, 0, 0, 20, 0);
        ObjectRegistry.elementPowers.add(elementPower);
        elementPower = new ElementPowers(ElementEnum.ELEM_DISEN, "disenchantment", ResType.T_HRES, 0, 0, 20, 0);
        ObjectRegistry.elementPowers.add(elementPower);
    }

    /**
     * Loads the level-generation templates, announcing progress as it goes.
     *
     * <p>Named for the wider job it will do: at present it loads the dungeon profiles only, which is
     * one of the {@code run_parser} calls C makes from {@code init_arrays}
     * ({@code generate.c:644}). The room and vault templates are loaded elsewhere.
     *
     * <p>Function runTemplateParser commented in full on 260827.
     */
    public static void runTemplateParser() {
        // Signal EVENT_ENTER_INIT
        EventsHandler bus = GameEngine.getEventsBusHandler();
        bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initialising arrays... (dungeon profiles)");
        DungeonLoader.loadDungeonProfiles();

    }


    //    private static void loadMonsterLore() {
//        LoreReader loreReader = new LoreReader();
//        String filename = AngbandDirs.ANGBAND_DIRS.USER.getPath() + "lore.txt";
//
//        try {
//            MonsterRegistry.monsterLore = loreReader.parse(filename);
//        } catch (IOException e) {
//            logger.error("Error while loading file {}", filename, e);
//        }
//    }

    /**
     * Private constructor preventing instantiation of this static-only registry.
     */
    @Contract(pure = true)
    private GameConstants() {
    }

    /**
     * Convert a String key, String value record into a String key int value record
     * @param value the String value to convert to a int value
     * @param key the String key to match the new int value against
     * @return A new String key int value pair
     * @throws InvalidTokenFoundDuringParse Either the wrong number of tokens in the value or a badly formatted integer
     * in the value String
     */
    @NotNull
    @Contract("_, _ -> new")
    private static NameValuePair getValues(@NotNull String value, @NotNull String key) throws InvalidTokenFoundDuringParse {
        String tag = key + ":";
        String[] results = value.split(":");

        if (results.length != 2) {
            String message = "Invalid number of arguments found in incoming line from constants.txt. Line was " + key + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val;

        try {
            val = Integer.parseInt(results[1]);
            return new NameValuePair(name, val);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + value;
            logger.error(message, e);
            throw e;
        }
    }

    /**
     * @return the configured value of {@code storeMax}
     */
    public static int getStoreMax() {
        return storeMax;
    }

    /**
     * @return the configured value of {@code randartActivationsMax}
     */
    public static int getRandartActivationsMax() {
        return ObjectRegistry.getRandartActivationsMax();
    }

    /**
     * @return the configured value of {@code caveProfileMax}
     */
    public static int getCaveProfileMax() {
        return caveProfileMax;
    }


    /**
     * @return the value of {@code level-max:monsters}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getLevelMaxMonsters() {
        return data.levelMax().monsters();
    }

    /**
     * @return the value of {@code mon-gen:chance}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenChance() {
        return data.monGen().chance();
    }

    /**
     * @return the value of {@code mon-gen:level-min}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenLevelMin() {
        return data.monGen().levelMin();
    }

    /**
     * @return the value of {@code mon-gen:town-day}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenTownDay() {
        return data.monGen().townDay();
    }

    /**
     * @return the value of {@code mon-gen:town-night}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenTownNight() {
        return data.monGen().townNight();
    }

    /**
     * @return the value of {@code mon-gen:repro-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenReproMax() {
        return data.monGen().reproMax();
    }

    /**
     * @return the value of {@code mon-gen:ood-chance}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenOodChance() {
        return data.monGen().oodChance();
    }

    /**
     * @return the value of {@code mon-gen:ood-amount}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenOodAmount() {
        return data.monGen().oodAmount();
    }

    /**
     * @return the value of {@code mon-gen:group-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenGroupMax() {
        return data.monGen().groupMax();
    }

    /**
     * @return the value of {@code mon-gen:group-dist}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenGroupDist() {
        return data.monGen().groupDist();
    }

    /**
     * @return the value of {@code mon-play:break-glyph}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayBreakGlyph() {
        return data.monPlay().breakGlyph();
    }

    /**
     * @return the value of {@code mon-play:mult-rate}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayMultRate() {
        return data.monPlay().multRate();
    }

    /**
     * @return the value of {@code mon-play:life-drain}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayLifeDrain() {
        return data.monPlay().lifeDrain();
    }

    /**
     * @return the value of {@code mon-play:flee-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayFleeRange() {
        return data.monPlay().fleeRange();
    }

    /**
     * @return the value of {@code mon-play:turn-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayTurnRange() {
        return data.monPlay().turnRange();
    }

    /**
     * @return the value of {@code dun-gen:cent-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenCentMax() {
        return data.dunGen().centMax();
    }

    /**
     * @return the value of {@code dun-gen:door-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenDoorMax() {
        return data.dunGen().doorMax();
    }

    /**
     * @return the value of {@code dun-gen:wall-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenWallMax() {
        return data.dunGen().wallMax();
    }

    /**
     * @return the value of {@code dun-gen:tunn-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenTunnMax() {
        return data.dunGen().tunnMax();
    }

    /**
     * @return the value of {@code dun-gen:amt-room}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtRoom() {
        return data.dunGen().amtRoom();
    }

    /**
     * @return the value of {@code dun-gen:amt-item}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtItem() {
        return data.dunGen().amtItem();
    }

    /**
     * @return the value of {@code dun-gen:amt-gold}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtGold() {
        return data.dunGen().amtGold();
    }

    /**
     * @return the value of {@code dun-gen:pit-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenPitMax() {
        return data.dunGen().pitMax();
    }

    /**
     * @return the value of {@code world:max-depth}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldMaxDepth() {
        return data.world().maxDepth();
    }

    /**
     * @return the value of {@code world:day-length}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDayLength() {
        return data.world().dayLength();
    }

    /**
     * @return the value of {@code world:dungeon-hgt}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDungeonHgt() {
        return data.world().dungeonHgt();
    }

    /**
     * @return the value of {@code world:dungeon-wid}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDungeonWid() {
        return data.world().dungeonWid();
    }

    /**
     * @return the value of {@code world:town-hgt}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldTownHgt() {
        return data.world().townHgt();
    }

    /**
     * @return the value of {@code world:town-wid}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldTownWid() {
        return data.world().townWid();
    }

    /**
     * @return the value of {@code world:feeling-total}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldFeelingTotal() {
        return data.world().feelingTotal();
    }

    /**
     * @return the value of {@code world:feeling-need}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldFeelingNeed() {
        return data.world().feelingNeed();
    }

    /**
     * @return the value of {@code world:stair-skip}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldStairSkip() {
        return data.world().stairSkip();
    }

    /**
     * @return the value of {@code world:move-energy}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldMoveEnergy() {
        return data.world().moveEnergy();
    }

    /**
     * @return the value of {@code carry-cap:pack-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapPackSize() {
        return data.carryCap().packSize();
    }

    /**
     * @return the value of {@code carry-cap:quiver-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapQuiverSize() {
        return data.carryCap().quiverSize();
    }

    /**
     * @return the value of {@code carry-cap:quiver-slot-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapQuiverSlotSize() {
        return data.carryCap().quiverSlotSize();
    }

    /**
     * @return the value of {@code carry-cap:thrown-quiver-mult}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapThrownQuiverMult() {
        return data.carryCap().thrownQuiverMult();
    }

    /**
     * @return the value of {@code carry-cap:floor-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapFloorSize() {
        return data.carryCap().floorSize();
    }

    /**
     * @return the value of {@code store:magic-level}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreInvenMax() {
        return data.store().invenMax();
    }

    /**
     * @return the value of {@code store:turns}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreTurns() {
        return data.store().turns();
    }

    /**
     * @return the value of {@code store:shuffle}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreShuffle() {
        return data.store().shuffle();
    }

    /**
     * @return the value of {@code store:magic-level}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreMagicLevel() {
        return data.store().magicLevel();
    }

    /**
     * @return the value of {@code obj-make:max-depth}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeMaxDepth() {
        return data.objMake().maxDepth();
    }

    /**
     * @return the value of {@code obj-make:great-obj}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeGreatObj() {
        return data.objMake().greatObj();
    }

    /**
     * @return the value of {@code obj-make:great-ego}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeGreatEgo() {
        return data.objMake().greatEgo();
    }

    /**
     * @return the value of {@code obj-make:fuel-torch}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeFuelTorch() {
        return data.objMake().fuelTorch();
    }

    /**
     * @return the value of {@code obj-make:fuel-lamp}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeFuelLamp() {
        return data.objMake().fuelLamp();
    }

    /**
     * @return the value of {@code obj-make:default-lamp}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeDefaultLamp() {
        return data.objMake().defaultLamp();
    }

    /**
     * @return the value of {@code player:max-sight}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerMaxSight() {
        return data.player().maxSight();
    }

    /**
     * @return the value of {@code player:max-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerMaxRange() {
        return data.player().maxRange();
    }

    /**
     * @return the value of {@code player:start-gold}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerStartGold() {
        return data.player().startGold();
    }

    /**
     * @return the value of {@code player:food-value}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerFoodValue() {
        return data.player().foodValue();
    }

    /**
     * @return the value of {@code melee-ranged:debuff-toh}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalDebuffToh() {
        return data.meleeCritical().debuffToh();
    }

    /**
     * @return the value of {@code melee-ranged:chance-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceWeightScale() {
        return data.meleeCritical().chanceWeightScale();
    }

    /**
     * @return the value of {@code melee-ranged:chance-toh-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceTohScale() {
        return data.meleeCritical().chanceTohScale();
    }

    /**
     * @return the value of {@code melee-ranged:chance-level-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceLevelScale() {
        return data.meleeCritical().chanceLevelScale();
    }

    /**
     * @return the value of {@code melee-ranged:chance-toh-skill-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceTohSkillScale() {
        return data.meleeCritical().chanceTohSkillScale();
    }
    /**
     * @return the value of {@code melee-ranged:chance-offset}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceOffset() {
        return data.meleeCritical().chanceOffset();
    }

    /**
     * @return the value of {@code melee-ranged:chance-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceRange() {
        return data.meleeCritical().chanceRange();
    }

    /**
     * @return the value of {@code melee-ranged:power-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalPowerWeightScale() {
        return data.meleeCritical().powerWeightScale();
    }

    /**
     * @return the value of {@code melee-ranged:power-random}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalPowerRandom() {
        return data.meleeCritical().powerRandom();
    }

    /**
     * @return the value of {@code ranged-critical:chance-level-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalDebuffToh() {
        return data.rangedCritical().debuffToh();
    }

    /**
     * @return the value of {@code ranged-critical:chance-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceWeightScale() {
        return data.rangedCritical().chanceWeightScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-toh-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceTohScale() {
        return data.rangedCritical().chanceTohScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-level-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceLevelScale() {
        return data.rangedCritical().chanceLevelScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-launched-toh-skill-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceLaunchedTohSkillScale() {
        return data.rangedCritical().chanceLaunchedTohSkillScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-thrown-toh-skill-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceThrownTohSkillScale() {
        return data.rangedCritical().chanceThrownTohSkillScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-offset}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceOffset() {
        return data.rangedCritical().chanceOffset();
    }


    /**
     * @return the value of {@code ranged-critical:chance-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceRange() {
        return data.rangedCritical().chanceRange();
    }

    /**
     * @return the value of {@code ranged-critical:power-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalPowerWeightScale() {
        return data.rangedCritical().powerWeightScale();
    }

    /**
     * @return the value of {@code ranged-critical:power-random}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalPowerRandom() {
        return data.rangedCritical().powerRandom();
    }

    /**
     * @return the value of {@code o-melee-critical:debuff-toh}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalDebuffToh() {
        return data.oMeleeCritical().debuffToh();
    }

    /**
     * @return the value of {@code o-melee-critical:power-toh-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalPowerTohScaleNumerator() {
        return data.oMeleeCritical().powerTohScaleNumerator();
    }

    /**
     * @return the value of {@code o-melee-critical:power-toh-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalPowerTohScaleDenominator() {
        return data.oMeleeCritical().powerTohScaleDenominator();
    }

    /**
     * @return the value of {@code o-melee-critical:chance-add-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChancePowerScaleNumerator() {
        return data.oMeleeCritical().chancePowerScaleNumerator();
    }

    /**
     * @return the value of {@code o-melee-critical:chance-power-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChancePowerScaleDenominator() {
        return data.oMeleeCritical().chancePowerScaleDenominator();
    }

    /**
     * @return the value of {@code o-melee-critical:chance-add-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChanceAddDenominator() {
        return data.oMeleeCritical().chanceAddDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getORangedCriticalDebuffToh() {
        return data.oRangedCritical().debuffToh();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerLaunchedTohScaleNumerator() {
        return data.oRangedCritical().powerLaunchedTohScaleNumerator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerLaunchedTohScaleDenominator() {
        return data.oRangedCritical().powerLaunchedTohScaleDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-thrown-toh-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerThrownTohScaleNumerator() {
        return data.oRangedCritical().powerThrownTohScaleNumerator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-thrown-toh-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerThrownTohScaleDenominator() {
        return data.oRangedCritical().powerThrownTohScaleDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:chance-power-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChancePowerScaleNumerator() {
        return data.oRangedCritical().chancePowerScaleNumerator();
    }

    /**
     * @return the value of {@code o-ranged-critical:chance-power-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChancePowerScaleDenominator() {
        return data.oRangedCritical().chancePowerScaleDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:chance-add-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChanceAddDenominator() {
        return data.oRangedCritical().chanceAddDenominator();
    }
}