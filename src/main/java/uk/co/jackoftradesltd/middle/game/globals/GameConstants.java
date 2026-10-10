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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.loaders.*;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.UIEntryValueRegistry;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ResType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The global holder for the game's tunable constants and the startup entry point that loads all
 * game data — the Java port's equivalent of the C original's {@code z_info} constants
 * ({@code struct angband_constants}, {@code init.h}) and its {@code init_game_constants} /
 * {@code init_arrays} bootstrap in {@code init.c}. After the loader/registry refactor it holds:
 * <ul>
 *   <li>process-wide constants and paths: the {@code angband.ini} location, {@link #MAX_PVAL},
 *       {@link #MAX_COMMAND_ARGUMENTS} and {@link #ANGBAND_SYS};</li>
 *   <li>the scalar constants read from {@code constants.txt} (held in {@link #data} and exposed
 *       through the many {@code get*} accessors below — {@code mon-gen}, {@code dun-gen},
 *       {@code world}, {@code carry-cap}, {@code melee-critical}/{@code ranged-critical}, and so on),
 *       each of which names the {@code z_info} field it ports;</li>
 *   <li>the two array bounds the loaders record as they parse, {@link #getStoreMax()} and
 *       {@link #getCaveProfileMax()}, which C keeps in {@code z_info->store_max} and
 *       {@code z_info->profile_max};</li>
 *   <li>the {@link #init(Core)} pipeline, which calls each domain's {@code *DataLoader} in dependency
 *       order to populate the per-domain registries, then builds the object-power tables C declares
 *       statically in {@code obj-power.c} ({@code archery}, {@code flag_sets}, {@code element_sets}
 *       and {@code el_powers}).</li>
 * </ul>
 *
 * <p><b>Load order has drifted from C.</b> C's {@code pl[]} table in {@code init.c} fixes the parse
 * order of {@code slay.txt}, {@code brand.txt}, {@code pain.txt}, {@code monster_base.txt} and
 * {@code summon.txt} as slays → brands → pain → monster bases → summons. {@link #init(Core)}'s order
 * for that group is pain → monster bases → slays → brands → summons — monster bases moved ahead of
 * slays and brands. The tail of the table also differs: C parses traps, chest traps, quests, flavours,
 * hints, then names, where {@link #init(Core)} parses traps, quests, hints, names, flavours, then
 * chest traps. See the note on {@link #init(Core)}, and treat the ordering as provisional rather than
 * settled.
 *
 * <p>The per-type master lists (monsters, objects, features, projections, classes, …) and their
 * {@code lookup*} accessors <em>no longer live here</em>: they were split out into the
 * {@code registry} package (read side) and {@code loaders} package (write side), one slice per
 * domain, leaving this class as the constants holder plus the init orchestrator.
 *
 * <p>It is a static-only holder (private constructor). The scalar {@code get*} accessors read
 * {@link #data}, so they must not be called before {@link #init(Core)} has loaded
 * {@code constants.txt}: until then {@code data} is {@code null} and each accessor throws
 * {@link NullPointerException}. Unlike C, whose {@code z_info} fields are all {@code uint16_t} or
 * {@code int}, every constant here is an {@code int}.
 *
 * <p>The instance fields {@link #gameInProgress}, {@link #initialized}, {@link #screensaverActive},
 * {@link #canUseGraphics} and {@link #changeTileSize} cannot be reached: the class is never
 * instantiated and nothing reads or writes them.
 *
 * <p>Class GameConstants coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class GameConstants {
    /**
     * Logger used to report load failures and malformed {@code constants.txt} lines.
     *
     * <p>Field logger coded before 261010, commented in full on 261010.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Records the number of cave profiles loaded from {@code dungeon_profile.txt}, which C keeps in
     * {@code z_info->profile_max}. Called by {@code DungeonLoader} once the profiles are parsed; read
     * back through {@link #getCaveProfileMax()}.
     *
     * <p>Function setCaveProfileMax coded before 261010, commented in full on 261010.
     *
     * @param size the number of cave profiles loaded
     */
    public static void setCaveProfileMax(int size) {
        caveProfileMax = size;
    }

    /**
     * A <code>record</code> used to store String name, int value pairs for the <code>constants.txt</code> file.
     * Currently only used by {@link #getValues}, which nothing in this class calls. May be extended later.
     *
     * <p>Record NameValuePair coded before 261010, commented in full on 261010.
     *
     * @param name  the String key
     * @param value the int value
     */
    private record NameValuePair(@NotNull String name, Integer value) {
    }

    /**
     * The ceiling on an object's {@code pval} - charges on a wand or staff, and gold in a pile.
     * C's {@code MAX_PVAL} ({@code obj-util.h}, 32767), the same figure as a signed 16-bit maximum
     * because that is the width of the field it caps in the original.
     *
     * <p>Field MAX_PVAL coded before 261010, commented in full on 261010.
     */
    public static final int MAX_PVAL = 32_767;
    /**
     * How many arguments a queued command may carry - the port of C's {@code CMD_MAX_ARGS}
     * ({@code cmd-core.h}, 4), the size of the fixed argument array in {@code struct command}.
     *
     * <p>Field MAX_COMMAND_ARGUMENTS coded before 261010, commented in full on 261010.
     */
    public static final int MAX_COMMAND_ARGUMENTS = 4;

    /**
     * Full path to the {@code angband.ini} settings file.
     *
     * <p>Duplicated by {@link #iniFile} below, which is built from the same two parts; the pair
     * should be reduced to one.
     *
     * <p>Field ANGBAND_INI coded before 261010, commented in full on 261010.
     */
    public static final String ANGBAND_INI = AngbandDirs.BASE_DIR + "angband.ini";

    /**
     * Name of the platform-specific back end, C's {@code ANGBAND_SYS} ({@code init.c}, default
     * {@code "xxx"}), which uses it to pick system-specific data files. The placeholder value is C's
     * own default and stands in until the port has a real back end to name.
     *
     * <p>Field ANGBAND_SYS coded before 261010, commented in full on 261010.
     */
    public static final String ANGBAND_SYS = "xxx";

    /**
     * Full path to the {@code angband.ini} settings file. Identical to {@link #ANGBAND_INI}; see the
     * note there.
     *
     * <p>Field iniFile coded before 261010, commented in full on 261010.
     */
    public static final String iniFile = AngbandDirs.BASE_DIR + "angband.ini";
    /**
     * Number of cave profiles loaded from {@code dungeon_profile.txt} - C's
     * {@code z_info->profile_max}, the bound on the level builders. Written by
     * {@link #setCaveProfileMax(int)}, read by {@link #getCaveProfileMax()}; it holds the plain record
     * count, not C's count including a blank slot.
     *
     * <p>Field caveProfileMax coded before 261010, commented in full on 261010.
     */
    private static int caveProfileMax;
    /**
     * The scalar tunables read from {@code constants.txt}, which every {@code get*} accessor below
     * reads through.
     *
     * <p>Left {@code null} until {@link #loadGameConstants()} succeeds. A file that fails to parse is
     * not a soft failure: it is logged and rethrown, which aborts {@link #init(Core)}. The accessors
     * therefore assume {@link #init(Core)} has completed.
     *
     * <p>Field data coded before 261010, commented in full on 261010.
     */
    private static GameConstantsData data;
    /**
     * True once a character exists and the turn loop is running, rather than the game sitting in its
     * start-up screens. An instance field of a static-only class, so never reachable.
     *
     * <p>Field gameInProgress coded before 261010, commented in full on 261010.
     */
    public boolean gameInProgress = false;
    /**
     * True once the data files have been loaded and the registries filled. An instance field of a
     * static-only class, so never reachable.
     *
     * <p>Field initialized coded before 261010, commented in full on 261010.
     */
    public boolean initialized = false;
    /**
     * True while the game is running itself as a screensaver - a mode C's Windows front end offers.
     * An instance field of a static-only class, so never reachable.
     *
     * <p>Field screensaverActive coded before 261010, commented in full on 261010.
     */
    public boolean screensaverActive = false;

    /**
     * The number of stores, which C keeps in {@code z_info->store_max}. Written by
     * {@link #setStoreMax(int)} (the terrain-feature assembler calls it with the number of shops it
     * handed out) and read by {@link #getStoreMax()}. Most of C's other array bounds are not held
     * here, because the lists they bound are {@code ArrayList}s whose size is the bound.
     *
     * <p>Field storeMax coded before 261010, commented in full on 261010.
     */
    private static int storeMax;
    /**
     * True when the front end can display graphical tiles rather than characters. An instance field
     * of a static-only class, so never reachable.
     *
     * <p>Field canUseGraphics coded before 261010, commented in full on 261010.
     */
    public boolean canUseGraphics = false;
    /**
     * Set when the front end has been asked to change its tile size, and cleared once it has. An
     * instance field of a static-only class, so never reachable.
     *
     * <p>Field changeTileSize coded before 261010, commented in full on 261010.
     */
    public boolean changeTileSize = false;

    /**
     * Load the scalar tunables from {@code constants.txt} into {@link #data}, which the {@code get*}
     * accessors read. This is the port of C's {@code init_game_constants}, which runs the
     * {@code constants_parser} and fills {@code z_info}. A file with parse errors is logged as fatal and
     * rethrown as a {@link RuntimeException}, matching C's {@code quit_fmt} on a failed parse; an IO
     * error is logged and rethrown. Called first in {@link #init(Core)} because nothing else here
     * depends on it, but the running game reads these values throughout.
     *
     * <p>Function loadGameConstants coded before 261010, commented in full on 261010.
     *
     * @throws IOException if the file cannot be read
     */
    private static void loadGameConstants() throws IOException {
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "constants.txt";
        GameConstantsReader reader = new GameConstantsReader();
        GameConstantsParseResult result;

        try {
            result = reader.parse(filename);
            if (result.hasErrors()) {
                StringBuilder msg = new StringBuilder("Cannot initialize constants.txt\n");
                for (String error : result.getErrors()) {
                    msg.append(error).append("\n");
                }
                logger.fatal(msg.toString());
                throw new RuntimeException(msg.toString());
            }
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
     * <p>{@link ObjectRegistry#reset()} runs before any loader so the call is idempotent: the
     * object-kind table and its per-base sval counters are rebuilt from scratch, so a re-init (e.g.
     * between tests) does not double-register kinds. Any failure is logged and rethrown wrapped in a
     * {@link RuntimeException}, since the game cannot run with partially-loaded data.
     *
     * <p>After the loaders have run, the method builds the object-power tables that C declares as
     * static arrays in {@code obj-power.c}: {@code archery} (three ammunition rows), {@code flag_sets}
     * (three rows), {@code element_sets} (three rows) and {@code el_powers} (thirteen rows, acid to
     * disenchantment). Their values are copied from C row for row, with {@code INHIBIT_POWER}
     * (20000) as the immunities bonus. The tables are stored on {@link ObjectRegistry}, replaced
     * wholesale on every call.
     *
     * <p><b>The {@code bindui:} wiring, batched rather than interleaved.</b> Before
     * {@code player_property.txt} is loaded, this method blocks on the core's inbox for the front
     * end's {@code UIEntriesLoaded} message ({@code "ui_entry files"} below) — the same handshake
     * {@link uk.co.jackoftradesltd.middle.game.gameengine.Core#gameLoop()} otherwise loops over, so
     * any other message that arrives first is handed to {@link Core#handleChannelOutput} rather than
     * dropped. C has no such wait: {@code object_property.txt}'s {@code bindui:} lines bind
     * immediately, one call per directive, as {@code parse_object_property_bindui}
     * ({@code obj-init.c}) calls {@code bind_object_property_to_ui_entry_by_name}
     * ({@code ui-entry.c}); {@code player_property.txt}'s bind slightly later but still far
     * sooner than the port's — {@code parse_player_prop_bindui} ({@code init.c}) only
     * records each binding on an in-progress "embryo" ability, and it is {@code finish_parse_player_prop}
     * ({@code init.c}), running once {@code player_property.txt} itself is fully parsed
     * and well before {@code object_property.txt} is even opened, that walks those embryos and calls
     * {@code bind_player_ability_to_ui_entry_by_name} ({@code ui-entry.c}). Either way, C's
     * {@code struct ui_entry} to bind onto already exists, because {@code ui_entry.txt} is parsed
     * before either property file in C's own {@code init_arrays}. The port cannot do the same, because its
     * {@code UIEntry} registry lives on the front end and only crosses the channel as one message
     * once every entry is built — so instead of binding as each property parses, this method waits
     * for that message, loads {@code player_property.txt} and {@code object_property.txt} in full,
     * and only then — in the block below reading {@code "Deal with UIEntry bindings here"} — walks
     * every loaded {@link PlayerProperty} and {@link ObjectProperty}, groups them by the
     * {@link UIEntrySpec} their {@code bindui:} lines name, and hands each group to
     * {@link uk.co.jackoftradesltd.middle.game.globals.registry.UIEntryValueRegistry#addEntryBinding}
     * in one call per entry. The end state — every {@code ui_entry} knowing every property bound to
     * it — is the same either way; only the order the wiring happens in differs.
     *
     * <p><b>Load order drift from C's {@code pl[]}.</b> C's own array ({@code init.c}) parses
     * {@code slay.txt} and {@code brand.txt} before {@code monster_base.txt} exists, even though
     * {@code parse_slay_base} ({@code obj-init.c}) looks a monster base up by name and hard-fails with
     * {@code PARSE_ERROR_INVALID_MONSTER_BASE} if it isn't registered yet. As it stands today, this
     * method's order for that group of five files is:
     * <ol>
     *   <li>{@code pain.txt}</li>
     *   <li>{@code monster_base.txt} — moved ahead of slays and brands so {@code base:} lookups in
     *       {@code slay.txt} can resolve</li>
     *   <li>{@code slay.txt}</li>
     *   <li>{@code brand.txt}</li>
     *   <li>{@code summon.txt}</li>
     * </ol>
     * This has not been confirmed against a real {@code slay.txt} entry that uses a {@code base:}
     * directive, so treat the reordering as an untested fix for an apparent C hazard rather than a
     * settled one.
     *
     * <p>The files at the end of the table are also parsed in a different order from C: C's
     * {@code pl[]} runs traps, chest traps, quests, flavours, hints, names; this method runs traps,
     * quests, hints, names, flavours, chest traps. Flavours still follow the object kinds they attach
     * to, and quests still follow monsters.
     *
     * <p>Function init coded before 261010, commented in full on 261010.
     *
     * @param core the core whose inbox is read for the front end's {@code UIEntriesLoaded} message
     * @return {@code true} if another message handled during that wait asked the core to stop, in which
     *         case loading was abandoned part-way; {@code false} once every file is loaded and the
     *         object-power tables are built
     */
    public static boolean init(Core core) {
        String file = "";
        try {
            ChannelRegistry.setPYMaxLevel(PlayerRegistry.PY_MAX_LEVEL);

            // Signal EVENT_ENTER_INIT
            EventsHandler bus = GameEngine.getEventsBusHandler();
            bus.eventSignalString(GameEventType.EVENT_ENTER_INIT, "Entering Init");

            // Start from an empty kind registry so init() is idempotent: object kinds and their
            // per-base sval counters are rebuilt from scratch here, so a re-init (e.g. between tests)
            // does not double-register kinds or keep incrementing svals.
            ObjectRegistry.reset();

            file = "constants.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing game constants...");
            loadGameConstants();
            file = "world.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing world...");
            WorldDataLoader.loadWorld();                // world arraylist size determines maxRandDepth
            file = "projection.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing game projections...");
            WorldDataLoader.loadProjections();          // projections arrayList size determines projectionTypeMax

            file = "ui_entry files";
            // Wait for UIEntry loaders to finish and message to be sent from UI frontend
            UIMessage message = core.getCoreChannel().coreReceiver().receive();
            List<UIEntrySpec> entries = new ArrayList<>();
            while (true) {
                if (message instanceof UIMessage.UIEntriesLoaded uiEntriesLoadedMessage) {
                    entries = uiEntriesLoadedMessage.entrySpecs();
                    break;
                } else {
                    // Pass the message through to the GameLoop for it to handle
                    if (core.handleChannelOutput(message)) return true;
                }
                message = core.getCoreChannel().coreReceiver().receive();
            }

            file = "player_property.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing player properties...");
            PlayerDataLoader.loadPlayerProperties();     // Dependent on UIEntry
            file = "terrain.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing terrain features...");
            TerrainDataLoader.loadTerrainFeatures();
            file = "object_base.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing object bases...");
            ObjectDataLoader.loadObjectBases();
            file = "pain.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing pain messages...");
            MonsterDataLoader.loadPain();
            file = "monster_base.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing monster bases...");
            MonsterDataLoader.loadMonsterBases();         // Dependent on MonsterPain
            file = "slay.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing object slays...");
            ObjectDataLoader.loadSlays();                // Dependent on MonsterBases
            file = "brand.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing object brands...");
            ObjectDataLoader.loadBrands();
            file = "summon.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing monster summons...");
            MonsterDataLoader.loadSummons();              // Dependent on MonsterBases
            file = "curse.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing curses...");
            ObjectDataLoader.loadCurses();               // Dependent on ObjectBases, & Summons
            file = "shape.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing player shapes...");
            PlayerDataLoader.loadPlayerShapes();
            file = "object.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing objects...");
            ObjectDataLoader.loadItemObjects();          // Dependent on Summons, Curse, Brand, Slay & ObjectBase
            file = "activation.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing activations...");
            ObjectDataLoader.loadActivations();
            file = "ego_item.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing ego items...");
            ObjectDataLoader.loadEgoItems();             // Dependent on Activations, Brand, Slay & Curse
            file = "history.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing player histories...");
            PlayerDataLoader.loadPlayerHistories();
            file = "body.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing player bodies...");
            PlayerDataLoader.loadBodies();
            file = "p_race.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing player races...");
            PlayerDataLoader.loadPlayerRaces();          // Dependent on PlayerBodies & PlayerHistories
            file = "realm.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing magic...");
            PlayerDataLoader.loadMagicRealms();
            file = "class.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing player classes...");
            PlayerDataLoader.loadPlayerClasses();        // Dependent on ItemObjects, Summons, MagicRealms
            file = "artifact.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing artifacts...");
            ObjectDataLoader.loadArtifacts();            // Dependent on Activations, ObjectKind, Brand, Slay & Curse
            ObjectDataLoader.parseCurseKinds();          // Dependent on ItemObjects & Curse; C does this at the end of artifact loading
            file = "object_property.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing object properties...");
            ObjectDataLoader.loadObjectProperties();     // Dependent on UIEntry

            // Deal with UIEntry bindings here
            // Store the specs and create the list of player properties
            Map<UIEntrySpec, List<PlayerProperty>> specsToPlayerProps = new HashMap<>();
            for (PlayerProperty playerProperty : PlayerRegistry.getPlayerProperties()) {
                for (PlayerProperty.BindUI bindUI : playerProperty.getEntries()) {
                    String entryName = bindUI.uiEntry();
                    UIEntrySpec spec = entries.stream().filter(s -> s.entryName().equals(entryName))
                            .findFirst().orElse(null);
                    if (spec != null) {
                        if (specsToPlayerProps.containsKey(spec)) {
                            specsToPlayerProps.get(spec).add(playerProperty);
                        } else {
                            List<PlayerProperty> properties = new ArrayList<>();
                            properties.add(playerProperty);
                            specsToPlayerProps.put(spec, properties);
                        }
                    }
                }
            }

            // Now do the same for the object properties
            Map<UIEntrySpec, List<ObjectProperty>> propsToObjectProps = new HashMap<>();
            for (ObjectProperty objectProperty : ObjectRegistry.getObjectProperties()) {
                for (ObjectProperty.UIBinding binding : objectProperty.getBoundEntries()) {
                    String entryName = binding.entry();
                    UIEntrySpec spec = entries.stream().filter(s -> s.entryName().equals(entryName))
                            .findFirst().orElse(null);
                    if (spec != null) {
                        if (propsToObjectProps.containsKey(spec)) {
                            propsToObjectProps.get(spec).add(objectProperty);
                        } else {
                            List<ObjectProperty> properties = new ArrayList<>();
                            properties.add(objectProperty);
                            propsToObjectProps.put(spec, properties);
                        }
                    }
                }
            }

            // Read them out and store in UIEntryValueRegistry
            for (UIEntrySpec spec : specsToPlayerProps.keySet()) {
                UIEntryValueRegistry.addEntryBinding(spec.entryName(), null, specsToPlayerProps.get(spec),
                        spec.combinerName(), spec.entryFlags());
            }

            for (UIEntrySpec spec : propsToObjectProps.keySet()) {
                UIEntryValueRegistry.addEntryBinding(spec.entryName(), propsToObjectProps.get(spec), null,
                        spec.combinerName(), spec.entryFlags());
            }

            file = "player_timed.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing player times properties...");
            PlayerDataLoader.loadPlayerTimedProperties();
            file = "blow_methods.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing blow methods...");
            MonsterDataLoader.loadBlowMethods();
            file = "blow_effects.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing blow effects...");
            MonsterDataLoader.loadBlowEffects();          // Dependent on Projections
            file = "monster_spell.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing monster spell types...");
            MonsterDataLoader.loadMonsterSpellTypes();
            file = "visuals.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing colour tables...");
            MonsterDataLoader.loadVisualTables();
            file = "monster.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing monsters...");
            MonsterDataLoader.loadMonsters();             // Dependent on MonsterBase, VisualsCyclerTable, BlowMethods & VisualColours
            file = "pit.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing pit profiles...");
            MonsterDataLoader.loadPitProfiles();          // Dependent on Monsters, MonsterBase & MonsterSpellTypes
            // TODO: Add in lore parsing and uncomment below two lines
            //bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing lore...");
//            loadMonsterLore();          // Dependent on MonsterKind, MonsterBase & ObjectKind (amongst others)
            file = "trap.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing traps...");
            TerrainDataLoader.loadTraps();
            file = "quest.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing quests...");
            WorldDataLoader.loadQuests();               // Dependent on Monster
            file = "hints.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing hints...");
            MiscDataLoader.loadHints();
            file = "names.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing names...");
            MiscDataLoader.loadNames();
            file = "flavor.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing flavours...");
            MiscDataLoader.loadFlavours();
            file = "chest_trap.txt";
            bus.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing chest traps...");
            ObjectDataLoader.loadChestTraps();

            // Load global tables
            PlayerDataLoader.initializeExpLevel();
        } catch (Exception e) {
            String message = "Unable to load data from " + AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + file
                    + " error message: " + e.getMessage();
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

        return false;
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
     *
     * <p>Constructor GameConstants coded before 261010, commented in full on 261010.
     */
    @Contract(pure = true)
    private GameConstants() {
    }

    /**
     * Split one {@code constants.txt} value of the form {@code label:number} into a name and an
     * integer. Nothing in this class calls it: the {@code GameConstantsReader} now does the parsing,
     * so this is a leftover from before that reader existed.
     *
     * <p>Function getValues coded before 261010, commented in full on 261010.
     *
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
     * The number of stores, C's {@code z_info->store_max}. It is set from the terrain-feature
     * assembler rather than {@code constants.txt}, so it is 0 until {@code terrain.txt} has loaded.
     *
     * <p>Function getStoreMax coded before 261010, commented in full on 261010.
     *
     * @return the configured value of {@code storeMax}
     */
    public static int getStoreMax() {
        return storeMax;
    }

    /**
     * Records the number of stores, C's {@code z_info->store_max}. Called by the terrain-feature
     * assembler with the number of shops it handed out.
     *
     * <p>Function setStoreMax coded before 261010, commented in full on 261010.
     *
     * @param storeMax the number of stores
     */
    public static void setStoreMax(int storeMax) {
        GameConstants.storeMax = storeMax;
    }

    /**
     * The number of activations available to random artifacts, C's {@code z_info->act_max}.
     * Delegates to {@link ObjectRegistry#getRandartActivationsMax()}, which counts the loaded
     * activations; it is the plain record count, without C's blank slot.
     *
     * <p>Function getRandartActivationsMax coded before 261010, commented in full on 261010.
     *
     * @return the configured value of {@code randartActivationsMax}
     */
    public static int getRandartActivationsMax() {
        return ObjectRegistry.getRandartActivationsMax();
    }

    /**
     * The number of cave profiles, C's {@code z_info->profile_max}, as recorded by
     * {@link #setCaveProfileMax(int)}.
     *
     * <p>Function getCaveProfileMax coded before 261010, commented in full on 261010.
     *
     * @return the configured value of {@code caveProfileMax}
     */
    public static int getCaveProfileMax() {
        return caveProfileMax;
    }


    /**
     * The maximum number of monsters on one level, C's {@code z_info->level_monster_max}.
     *
     * <p>Function getLevelMaxMonsters coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code level-max:monsters}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getLevelMaxMonsters() {
        return data.levelMax().monsters();
    }

    /**
     * The one-in-this chance per game turn of generating a new monster, C's
     * {@code z_info->alloc_monster_chance}.
     *
     * <p>Function getMonGenChance coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:chance}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenChance() {
        return data.monGen().chance();
    }

    /**
     * The minimum number of monsters generated on a level, C's {@code z_info->level_monster_min}.
     *
     * <p>Function getMonGenLevelMin coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:level-min}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenLevelMin() {
        return data.monGen().levelMin();
    }

    /**
     * The number of townsfolk generated in the day, C's {@code z_info->town_monsters_day}.
     *
     * <p>Function getMonGenTownDay coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:town-day}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenTownDay() {
        return data.monGen().townDay();
    }

    /**
     * The number of townsfolk generated at night, C's {@code z_info->town_monsters_night}.
     *
     * <p>Function getMonGenTownNight coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:town-night}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenTownNight() {
        return data.monGen().townNight();
    }

    /**
     * The maximum number of breeding monsters allowed on a level, C's
     * {@code z_info->repro_monster_max}.
     *
     * <p>Function getMonGenReproMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:repro-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenReproMax() {
        return data.monGen().reproMax();
    }

    /**
     * The one-in-this chance that a generated monster's level is inflated, C's
     * {@code z_info->ood_monster_chance}.
     *
     * <p>Function getMonGenOodChance coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:ood-chance}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenOodChance() {
        return data.monGen().oodChance();
    }

    /**
     * The maximum out-of-depth amount for monster generation, C's
     * {@code z_info->ood_monster_amount}.
     *
     * <p>Function getMonGenOodAmount coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:ood-amount}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenOodAmount() {
        return data.monGen().oodAmount();
    }

    /**
     * The maximum number of monsters in a group, C's {@code z_info->monster_group_max}.
     *
     * <p>Function getMonGenGroupMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:group-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenGroupMax() {
        return data.monGen().groupMax();
    }

    /**
     * The maximum distance of a group of monsters from a related group, C's
     * {@code z_info->monster_group_dist}.
     *
     * <p>Function getMonGenGroupDist coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-gen:group-dist}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenGroupDist() {
        return data.monGen().groupDist();
    }

    /**
     * How hard it is for a monster to break a rune of protection, C's
     * {@code z_info->glyph_hardness}.
     *
     * <p>Function getMonPlayBreakGlyph coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-play:break-glyph}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayBreakGlyph() {
        return data.monPlay().breakGlyph();
    }

    /**
     * The monster multiplication rate, where a high value slows breeding, C's
     * {@code z_info->repro_monster_rate}.
     *
     * <p>Function getMonPlayMultRate coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-play:mult-rate}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayMultRate() {
        return data.monPlay().multRate();
    }

    /**
     * The percentage of the player's experience drained per hit, C's
     * {@code z_info->life_drain_percent}.
     *
     * <p>Function getMonPlayLifeDrain coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-play:life-drain}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayLifeDrain() {
        return data.monPlay().lifeDrain();
    }

    /**
     * How many grids out of sight a fleeing monster will run, C's {@code z_info->flee_range}.
     *
     * <p>Function getMonPlayFleeRange coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-play:flee-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayFleeRange() {
        return data.monPlay().fleeRange();
    }

    /**
     * The distance inside which a terrified monster slower than the character turns to fight, C's
     * {@code z_info->turn_range}.
     *
     * <p>Function getMonPlayTurnRange coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code mon-play:turn-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayTurnRange() {
        return data.monPlay().turnRange();
    }

    /**
     * The number of possible room centres (and hence rooms) on a normal level, C's
     * {@code z_info->level_room_max}.
     *
     * <p>Function getDunGenCentMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:cent-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenCentMax() {
        return data.dunGen().centMax();
    }

    /**
     * The number of possible door locations, C's {@code z_info->level_door_max}.
     *
     * <p>Function getDunGenDoorMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:door-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenDoorMax() {
        return data.dunGen().doorMax();
    }

    /**
     * The number of possible places to pierce room walls with tunnels, C's
     * {@code z_info->wall_pierce_max}.
     *
     * <p>Function getDunGenWallMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:wall-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenWallMax() {
        return data.dunGen().wallMax();
    }

    /**
     * The number of tunnel grids, C's {@code z_info->tunn_grid_max}.
     *
     * <p>Function getDunGenTunnMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:tunn-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenTunnMax() {
        return data.dunGen().tunnMax();
    }

    /**
     * The average number of objects to place in rooms, C's {@code z_info->room_item_av}.
     *
     * <p>Function getDunGenAmtRoom coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:amt-room}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtRoom() {
        return data.dunGen().amtRoom();
    }

    /**
     * The average number of objects to place in rooms and corridors, C's
     * {@code z_info->both_item_av}.
     *
     * <p>Function getDunGenAmtItem coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:amt-item}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtItem() {
        return data.dunGen().amtItem();
    }

    /**
     * The average amount of treasure to place in rooms and corridors, C's
     * {@code z_info->both_gold_av}.
     *
     * <p>Function getDunGenAmtGold coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:amt-gold}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtGold() {
        return data.dunGen().amtGold();
    }

    /**
     * The maximum number of pits or nests allowed per level, C's {@code z_info->level_pit_max}.
     *
     * <p>Function getDunGenPitMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code dun-gen:pit-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenPitMax() {
        return data.dunGen().pitMax();
    }

    /**
     * The maximum dungeon level, which must be at least 100, C's {@code z_info->max_depth}.
     *
     * <p>Function getWorldMaxDepth coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:max-depth}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldMaxDepth() {
        return data.world().maxDepth();
    }

    /**
     * The number of turns from dawn to dawn, C's {@code z_info->day_length}.
     *
     * <p>Function getWorldDayLength coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:day-length}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDayLength() {
        return data.world().dayLength();
    }

    /**
     * The maximum number of grids in each dungeon level vertically, C's
     * {@code z_info->dungeon_hgt}.
     *
     * <p>Function getWorldDungeonHgt coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:dungeon-hgt}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDungeonHgt() {
        return data.world().dungeonHgt();
    }

    /**
     * The maximum number of grids in each dungeon level horizontally, C's
     * {@code z_info->dungeon_wid}.
     *
     * <p>Function getWorldDungeonWid coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:dungeon-wid}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDungeonWid() {
        return data.world().dungeonWid();
    }

    /**
     * The maximum number of grids in the town level vertically, C's {@code z_info->town_hgt}.
     *
     * <p>Function getWorldTownHgt coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:town-hgt}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldTownHgt() {
        return data.world().townHgt();
    }

    /**
     * The maximum number of grids in the town level horizontally, C's {@code z_info->town_wid}.
     *
     * <p>Function getWorldTownWid coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:town-wid}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldTownWid() {
        return data.world().townWid();
    }

    /**
     * The total number of feeling squares per level, C's {@code z_info->feeling_total}.
     *
     * <p>Function getWorldFeelingTotal coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:feeling-total}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldFeelingTotal() {
        return data.world().feelingTotal();
    }

    /**
     * The number of squares that must be seen to get the first level feeling, C's
     * {@code z_info->feeling_need}.
     *
     * <p>Function getWorldFeelingNeed coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:feeling-need}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldFeelingNeed() {
        return data.world().feelingNeed();
    }

    /**
     * The number of levels each staircase skips, C's {@code z_info->stair_skip}.
     *
     * <p>Function getWorldStairSkip coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:stair-skip}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldStairSkip() {
        return data.world().stairSkip();
    }

    /**
     * The energy the player or a monster needs to move, C's {@code z_info->move_energy}.
     *
     * <p>Function getWorldMoveEnergy coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code world:move-energy}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldMoveEnergy() {
        return data.world().moveEnergy();
    }

    /**
     * The maximum number of pack slots for carrying inventory, C's {@code z_info->pack_size}.
     *
     * <p>Function getCarryCapPackSize coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code carry-cap:pack-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapPackSize() {
        return data.carryCap().packSize();
    }

    /**
     * The maximum number of quiver slots for carrying missiles, C's {@code z_info->quiver_size}.
     *
     * <p>Function getCarryCapQuiverSize coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code carry-cap:quiver-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapQuiverSize() {
        return data.carryCap().quiverSize();
    }

    /**
     * The maximum number of missiles per quiver slot, C's {@code z_info->quiver_slot_size}.
     *
     * <p>Function getCarryCapQuiverSlotSize coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code carry-cap:quiver-slot-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapQuiverSlotSize() {
        return data.carryCap().quiverSlotSize();
    }

    /**
     * The multiplier applied when computing quiver capacity for non-ammunition thrown items, C's
     * {@code z_info->thrown_quiver_mult}.
     *
     * <p>Function getCarryCapThrownQuiverMult coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code carry-cap:thrown-quiver-mult}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapThrownQuiverMult() {
        return data.carryCap().thrownQuiverMult();
    }

    /**
     * The maximum number of objects allowed in a single dungeon grid, C's
     * {@code z_info->floor_size}.
     *
     * <p>Function getCarryCapFloorSize coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code carry-cap:floor-size}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapFloorSize() {
        return data.carryCap().floorSize();
    }

    /**
     * The maximum number of discrete objects in a store's inventory, C's
     * {@code z_info->store_inven_max}.
     *
     * <p>Function getStoreInvenMax coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code store:inven-max}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreInvenMax() {
        return data.store().invenMax();
    }

    /**
     * The number of turns between store turnovers, C's {@code z_info->store_turns}.
     *
     * <p>Function getStoreTurns coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code store:turns}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreTurns() {
        return data.store().turns();
    }

    /**
     * The one-in-this chance per day of a store owner changing, C's {@code z_info->store_shuffle}.
     *
     * <p>Function getStoreShuffle coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code store:shuffle}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreShuffle() {
        return data.store().shuffle();
    }

    /**
     * The dungeon level at which magic is applied to objects for normal stores, C's
     * {@code z_info->store_magic_level}.
     *
     * <p>Function getStoreMagicLevel coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code store:magic-level}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreMagicLevel() {
        return data.store().magicLevel();
    }

    /**
     * The maximum depth used in object allocation, C's {@code z_info->max_obj_depth}.
     *
     * <p>Function getObjectMakeMaxDepth coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code obj-make:max-depth}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeMaxDepth() {
        return data.objMake().maxDepth();
    }

    /**
     * The one-in-this chance of inflating the requested object level, C's
     * {@code z_info->great_obj}.
     *
     * <p>Function getObjectMakeGreatObj coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code obj-make:great-obj}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeGreatObj() {
        return data.objMake().greatObj();
    }

    /**
     * The one-in-this chance of inflating the requested ego item level, C's
     * {@code z_info->great_ego}.
     *
     * <p>Function getObjectMakeGreatEgo coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code obj-make:great-ego}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeGreatEgo() {
        return data.objMake().greatEgo();
    }

    /**
     * The maximum amount of fuel in a torch, C's {@code z_info->fuel_torch}.
     *
     * <p>Function getObjectMakeFuelTorch coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code obj-make:fuel-torch}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeFuelTorch() {
        return data.objMake().fuelTorch();
    }

    /**
     * The maximum amount of fuel in a lantern, C's {@code z_info->fuel_lamp}.
     *
     * <p>Function getObjectMakeFuelLamp coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code obj-make:fuel-lamp}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeFuelLamp() {
        return data.objMake().fuelLamp();
    }

    /**
     * The default amount of fuel in a lantern, C's {@code z_info->default_lamp}.
     *
     * <p>Function getObjectMakeDefaultLamp coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code obj-make:default-lamp}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeDefaultLamp() {
        return data.objMake().defaultLamp();
    }

    /**
     * The maximum visual range, C's {@code z_info->max_sight}.
     *
     * <p>Function getPlayerMaxSight coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code player:max-sight}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerMaxSight() {
        return data.player().maxSight();
    }

    /**
     * The maximum missile and spell range, C's {@code z_info->max_range}.
     *
     * <p>Function getPlayerMaxRange coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code player:max-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerMaxRange() {
        return data.player().maxRange();
    }

    /**
     * The amount of gold (or value of equipment) the player starts with, C's
     * {@code z_info->start_gold}.
     *
     * <p>Function getPlayerStartGold coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code player:start-gold}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerStartGold() {
        return data.player().startGold();
    }

    /**
     * The number of turns that 1% of the player's food capacity feeds them for, C's
     * {@code z_info->food_value}.
     *
     * <p>Function getPlayerFoodValue coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code player:food-value}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerFoodValue() {
        return data.player().foodValue();
    }

    /**
     * The amount added to the to-hit value when working out the chance of a melee critical against a
     * debuffed target, C's {@code z_info->m_crit_debuff_toh}.
     *
     * <p>Function getMeleeCriticalDebuffToh coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:debuff-toh}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalDebuffToh() {
        return data.meleeCritical().debuffToh();
    }

    /**
     * The scale factor for the weapon's weight in the chance of a melee critical, C's
     * {@code z_info->m_crit_chance_weight_scl}.
     *
     * <p>Function getMeleeCriticalChanceWeightScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:chance-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceWeightScale() {
        return data.meleeCritical().chanceWeightScale();
    }

    /**
     * The scale factor for the overall to-hit value in the chance of a melee critical, C's
     * {@code z_info->m_crit_chance_toh_scl}.
     *
     * <p>Function getMeleeCriticalChanceTohScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:chance-toh-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceTohScale() {
        return data.meleeCritical().chanceTohScale();
    }

    /**
     * The scale factor for the player's level in the chance of a melee critical, C's
     * {@code z_info->m_crit_chance_level_scl}.
     *
     * <p>Function getMeleeCriticalChanceLevelScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:chance-level-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceLevelScale() {
        return data.meleeCritical().chanceLevelScale();
    }

    /**
     * The scale factor for the to-hit skill in the chance of a melee critical, C's
     * {@code z_info->m_crit_chance_toh_skill_scl}.
     *
     * <p>Function getMeleeCriticalChanceTohSkillScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:chance-toh-skill-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceTohSkillScale() {
        return data.meleeCritical().chanceTohSkillScale();
    }

    /**
     * The value added to the chance of a melee critical, C's {@code z_info->m_crit_chance_offset}.
     *
     * <p>Function getMeleeCriticalChanceOffset coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:chance-offset}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceOffset() {
        return data.meleeCritical().chanceOffset();
    }

    /**
     * The maximum range for the melee critical chance: with chance N and range M the probability of a
     * critical is {@code min(max(0, N), M) / M}. C's {@code z_info->m_crit_chance_range}.
     *
     * <p>Function getMeleeCriticalChanceRange coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:chance-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceRange() {
        return data.meleeCritical().chanceRange();
    }

    /**
     * The scale factor for the weapon's weight in the power of a melee critical, C's
     * {@code z_info->m_crit_power_weight_scl}.
     *
     * <p>Function getMeleeCriticalPowerWeightScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:power-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalPowerWeightScale() {
        return data.meleeCritical().powerWeightScale();
    }

    /**
     * The maximum of the random part of the power of a melee critical, C's
     * {@code z_info->m_crit_power_random}.
     *
     * <p>Function getMeleeCriticalPowerRandom coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code melee-critical:power-random}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalPowerRandom() {
        return data.meleeCritical().powerRandom();
    }

    /**
     * The amount added to the to-hit value when working out the chance of a ranged critical against a
     * debuffed target, C's {@code z_info->r_crit_debuff_toh}. Private, and not called within this class.
     *
     * <p>Function getRangedCriticalDebuffToh coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:debuff-toh}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalDebuffToh() {
        return data.rangedCritical().debuffToh();
    }

    /**
     * The scale factor for the missile's weight in the chance of a ranged critical, C's
     * {@code z_info->r_crit_chance_weight_scl}. Private, and not called within this class.
     *
     * <p>Function getRangedCriticalChanceWeightScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:chance-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceWeightScale() {
        return data.rangedCritical().chanceWeightScale();
    }

    /**
     * The scale factor for the overall to-hit value in the chance of a ranged critical, C's
     * {@code z_info->r_crit_chance_toh_scl}. Private, and not called within this class.
     *
     * <p>Function getRangedCriticalChanceTohScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:chance-toh-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceTohScale() {
        return data.rangedCritical().chanceTohScale();
    }

    /**
     * The scale factor for the player's level in the chance of a ranged critical, C's
     * {@code z_info->r_crit_chance_level_scl}. Private, and not called within this class.
     *
     * <p>Function getRangedCriticalChanceLevelScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:chance-level-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceLevelScale() {
        return data.rangedCritical().chanceLevelScale();
    }

    /**
     * The scale factor for the launched to-hit skill in the chance of a ranged critical when using a
     * launcher, C's {@code z_info->r_crit_chance_launched_toh_skill_scl}. Private, and not called
     * within this class.
     *
     * <p>Function getRangedCriticalChanceLaunchedTohSkillScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:chance-launched-toh-skill-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceLaunchedTohSkillScale() {
        return data.rangedCritical().chanceLaunchedTohSkillScale();
    }

    /**
     * The scale factor for the thrown to-hit skill in the chance of a ranged critical for a thrown
     * object, C's {@code z_info->r_crit_chance_thrown_toh_skill_scl}. Private, and not called within
     * this class.
     *
     * <p>Function getRangedCriticalChanceThrownTohSkillScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:chance-thrown-toh-skill-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceThrownTohSkillScale() {
        return data.rangedCritical().chanceThrownTohSkillScale();
    }

    /**
     * The value added to the chance of a ranged critical, C's {@code z_info->r_crit_chance_offset}.
     * Private, and not called within this class.
     *
     * <p>Function getRangedCriticalChanceOffset coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:chance-offset}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceOffset() {
        return data.rangedCritical().chanceOffset();
    }


    /**
     * The maximum range for the ranged critical chance, with the same {@code min(max(0, N), M) / M}
     * rule as the melee one. C's {@code z_info->r_crit_chance_range}. Private, and not called within
     * this class.
     *
     * <p>Function getRangedCriticalChanceRange coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:chance-range}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceRange() {
        return data.rangedCritical().chanceRange();
    }

    /**
     * The scale factor for the missile's weight in the power of a ranged critical, C's
     * {@code z_info->r_crit_power_weight_scl}. Private, and not called within this class.
     *
     * <p>Function getRangedCriticalPowerWeightScale coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:power-weight-scale}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalPowerWeightScale() {
        return data.rangedCritical().powerWeightScale();
    }

    /**
     * The maximum of the random part of the power of a ranged critical, C's
     * {@code z_info->r_crit_power_random}. Private, and not called within this class.
     *
     * <p>Function getRangedCriticalPowerRandom coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code ranged-critical:power-random}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalPowerRandom() {
        return data.rangedCritical().powerRandom();
    }

    /**
     * The amount added to the to-hit value when working out the power of an O-combat melee critical
     * against a debuffed target, C's {@code z_info->o_m_crit_debuff_toh}. Private, and not called
     * within this class.
     *
     * <p>Function getOMeleeCriticalDebuffToh coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-melee-critical:debuff-toh}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalDebuffToh() {
        return data.oMeleeCritical().debuffToh();
    }

    /**
     * The numerator of the scale factor applied to the combined to-hit value to get the power of an
     * O-combat melee critical, C's {@code z_info->o_m_crit_power_toh_scl_num}. Private, and not called
     * within this class.
     *
     * <p>Function getOMeleeCriticalPowerTohScaleNumerator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-melee-critical:power-toh-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalPowerTohScaleNumerator() {
        return data.oMeleeCritical().powerTohScaleNumerator();
    }

    /**
     * The denominator of the scale factor applied to the combined to-hit value to get the power of an
     * O-combat melee critical, C's {@code z_info->o_m_crit_power_toh_scl_den}. Private, and not called
     * within this class.
     *
     * <p>Function getOMeleeCriticalPowerTohScaleDenominator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-melee-critical:power-toh-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalPowerTohScaleDenominator() {
        return data.oMeleeCritical().powerTohScaleDenominator();
    }

    /**
     * The scale factor for the critical's power in the numerator of the chance of an O-combat melee
     * critical, C's {@code z_info->o_m_crit_chance_power_scl_num}. Private, and not called within
     * this class.
     *
     * <p>Function getOMeleeCriticalChancePowerScaleNumerator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-melee-critical:chance-power-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChancePowerScaleNumerator() {
        return data.oMeleeCritical().chancePowerScaleNumerator();
    }

    /**
     * The scale factor for the critical's power in the denominator of the chance of an O-combat melee
     * critical, C's {@code z_info->o_m_crit_chance_power_scl_den}. Private, and not called within
     * this class.
     *
     * <p>Function getOMeleeCriticalChancePowerScaleDenominator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-melee-critical:chance-power-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChancePowerScaleDenominator() {
        return data.oMeleeCritical().chancePowerScaleDenominator();
    }

    /**
     * The value of the added term in the denominator of the chance of an O-combat melee critical,
     * C's {@code z_info->o_m_crit_chance_add_den}. Private, and not called within this class.
     *
     * <p>Function getOMeleeCriticalChanceAddDenominator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-melee-critical:chance-add-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChanceAddDenominator() {
        return data.oMeleeCritical().chanceAddDenominator();
    }

    /**
     * The amount added to the to-hit value when working out the power of an O-combat ranged critical
     * against a debuffed target, C's {@code z_info->o_r_crit_debuff_toh}. Private, and not called
     * within this class.
     *
     * <p>Function getORangedCriticalDebuffToh coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:debuff-toh}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getORangedCriticalDebuffToh() {
        return data.oRangedCritical().debuffToh();
    }

    /**
     * The numerator of the scale factor applied to the combined to-hit value to get the power of an
     * O-combat critical with a launched missile, C's
     * {@code z_info->o_r_crit_power_launched_toh_scl_num}.
     *
     * <p>Function getORangedCriticalPowerLaunchedTohScaleNumerator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerLaunchedTohScaleNumerator() {
        return data.oRangedCritical().powerLaunchedTohScaleNumerator();
    }

    /**
     * The denominator of the scale factor applied to the combined to-hit value to get the power of an
     * O-combat critical with a launched missile, C's
     * {@code z_info->o_r_crit_power_launched_toh_scl_den}.
     *
     * <p>Function getORangedCriticalPowerLaunchedTohScaleDenominator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerLaunchedTohScaleDenominator() {
        return data.oRangedCritical().powerLaunchedTohScaleDenominator();
    }

    /**
     * The numerator of the scale factor applied to the combined to-hit value to get the power of an
     * O-combat critical with a thrown missile; set so that thrown missiles get more criticals. C's
     * {@code z_info->o_r_crit_power_thrown_toh_scl_num}.
     *
     * <p>Function getORangedCriticalPowerThrownTohScaleNumerator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:power-thrown-toh-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerThrownTohScaleNumerator() {
        return data.oRangedCritical().powerThrownTohScaleNumerator();
    }

    /**
     * The denominator of the scale factor applied to the combined to-hit value to get the power of an
     * O-combat critical with a thrown missile, C's {@code z_info->o_r_crit_power_thrown_toh_scl_den}.
     *
     * <p>Function getORangedCriticalPowerThrownTohScaleDenominator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:power-thrown-toh-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerThrownTohScaleDenominator() {
        return data.oRangedCritical().powerThrownTohScaleDenominator();
    }

    /**
     * The scale factor for the critical's power in the numerator of the chance of an O-combat ranged
     * critical, C's {@code z_info->o_r_crit_chance_power_scl_num}.
     *
     * <p>Function getORangedCriticalChancePowerScaleNumerator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:chance-power-scale-numerator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChancePowerScaleNumerator() {
        return data.oRangedCritical().chancePowerScaleNumerator();
    }

    /**
     * The scale factor for the critical's power in the denominator of the chance of an O-combat ranged
     * critical, C's {@code z_info->o_r_crit_chance_power_scl_den}.
     *
     * <p>Function getORangedCriticalChancePowerScaleDenominator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:chance-power-scale-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChancePowerScaleDenominator() {
        return data.oRangedCritical().chancePowerScaleDenominator();
    }

    /**
     * The value of the added term in the denominator of the chance of an O-combat ranged critical,
     * C's {@code z_info->o_r_crit_chance_add_den}.
     *
     * <p>Function getORangedCriticalChanceAddDenominator coded before 261010, commented in full on 261010.
     *
     * @return the value of {@code o-ranged-critical:chance-add-denominator}
     * read from the constants.txt file.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChanceAddDenominator() {
        return data.oRangedCritical().chanceAddDenominator();
    }
}
