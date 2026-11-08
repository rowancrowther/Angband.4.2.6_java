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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.backend.parser.*;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;

/**
 * Startup loader for the object slice: parses the object-domain gamedata files
 * ({@code object_base.txt}, {@code slay.txt}, {@code brand.txt}, {@code curse.txt},
 * {@code object.txt}, {@code activation.txt}, {@code ego_item.txt}, {@code artifact.txt},
 * {@code object_property.txt}, {@code chest_trap.txt}) and populates
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry} through its setters and
 * {@code addObjectKind}.
 *
 * <p>This is the write side of the object slice, paired with {@code ObjectRegistry} (the read
 * side). Each file loader is the port of one C {@code file_parser} in {@code obj-init.c} (or, for
 * chest traps, {@code obj-chest.c}): the reader stands in for the parser's {@code init_parse_*} and
 * {@code run_parse_*}, and the registry setter stands in for its {@code finish_parse_*}, which in C
 * copies the parsed linked list into the direct-access array and sets the {@code z_info} counter.
 * The loaders are invoked by {@code GameConstants.init()} in dependency order — bases before
 * slays/brands/curses; curses and item objects before ego items and artifacts. {@code loadItemObjects}
 * registers the ordinary object kinds, and {@code loadArtifacts} synthesizes the special artifact
 * kinds into the same table, so their order is load-bearing. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * <p>Every error is fatal, as in C: a missing or unreadable file, or a bad line anywhere in it,
 * stops the load, as {@code parse_file_quit_not_found} and {@code init_arrays} ({@code init.c})
 * quit the game. There are three ways a load fails, and only the first is handled here:
 * <ul>
 *   <li>an {@link IOException} from the reader is logged at {@code FATAL} and rethrown wrapped in a
 *       {@link RuntimeException};</li>
 *   <li>a grammar or lexer error is thrown by the reader as a {@code ParseCancellationException}
 *       ({@code ParseErrors.throwIfAny});</li>
 *   <li>a record the assembler refuses is thrown as a {@code DataFileParseException}.</li>
 * </ul>
 * The last two are unchecked and pass through every loader except {@link #loadCurses()} unlogged
 * and unwrapped; {@code GameConstants.init()} catches them and names the file. Nothing is
 * registered from a file that fails, so the registry never holds a partial list. The user-directory
 * override that C's {@code parse_file} tries first is not ported: only the gamedata directory is
 * read.
 *
 * <p>The {@code *Max} counters the registry keeps are the plain record counts. C's {@code z_info}
 * counters are one higher for the placeholder slot its arrays carry (see {@link ObjectRegistry}),
 * which is an accepted deviation, not a skipped record.
 *
 * <p>List order is C's slot order, which is not always file order. C's parsers prepend each slay,
 * brand, curse and activation to a linked list and the matching {@code finish_parse_*} copies from
 * the head, so slot 1 of those four arrays holds the <em>last</em> record in the file. Object
 * kinds, ego items, artifacts and object properties are copied tail-first and keep file order, as
 * do the chest traps. The loader reorders nothing: the slay, brand, curse and activation
 * assemblers hand back lists already reversed, and the loader stores what it is given.
 *
 * <p>One member is not a file loader: {@link #parseCurseKinds()} finishes the curses once the kind
 * table is complete, giving each curse's own object the {@code <curse object>} kind. It runs after
 * {@link #loadArtifacts()}, where C does the same at the end of {@code finish_parse_artifact} in
 * {@code obj-init.c}.
 *
 * <p>Class ObjectDataLoader coded before 261005, class description updated for the curse-kind pass
 * on 261005, commented in full on 261013, failure paragraph rewritten on 261104 for the removal of
 * the soft-error path.
 *
 * @author Rowan Crowther
 */
public class ObjectDataLoader {
    /**
     * The log every loader here writes to: the {@code FATAL} line that precedes a wrapped
     * {@link RuntimeException}. Grammar and assembler errors are not logged here, except in
     * {@link #loadCurses()}, which catches every exception.
     *
     * <p>Field logger coded before 261005, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger(ObjectDataLoader.class);

    /**
     * Load the object properties from {@code object_property.txt} into {@link ObjectRegistry} — the
     * port of C's {@code object_property_parser} ({@code obj-init.c}), whose
     * {@code finish_parse_object_property} builds {@code obj_properties}. Must run after the UI
     * entries, which the property assembler resolves {@code bindui} targets against.
     * <p>
     * Every error stops the load. An IO failure is logged and <em>rethrown</em> as a
     * {@link RuntimeException}, as C quits when this file cannot be read; a grammar or assembler
     * error passes through unwrapped (see the class description).
     *
     * <p>Function loadObjectProperties coded before 261005, commented in full on 261013, updated on
     * 261104 for the removal of the soft-error path.
     */
    public static void loadObjectProperties() {
        ObjectPropertyReader parser = new ObjectPropertyReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "object_property.txt";

        try {
            ParseResult<ObjectProperty> result = parser.parseWithResults(filename);

            ObjectRegistry.setObjectProperties(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the chest traps from {@code chest_trap.txt} into {@link ObjectRegistry} — the port of C's
     * {@code chest_trap_parser} ({@code obj-chest.c}), whose {@code finish_parse_chest_trap} does
     * nothing beyond destroying the parser. Must run after summons, which the {@code SUMMON} trap's
     * effect resolves against - and so, transitively, after monster bases and pains. Nothing else in
     * the object slice depends on this list, so it may run late.
     * <p>
     * Every error stops the load. A file that breaks any of the structural rules stated at the top
     * of {@code chest_trap.txt} is refused by {@code ChestTrapAssembler} with a
     * {@code DataFileParseException}, which passes through unwrapped, so the registry is never left
     * holding a partial trap list. A grammar error passes through the same way. An IO failure is
     * logged and <em>rethrown</em> as a {@link RuntimeException}, as C quits when this file cannot be
     * read.
     *
     * <p>Function loadChestTraps coded before 261005, commented in full on 261013, updated on 261104
     * for the removal of the soft-error path.
     */
    public static void loadChestTraps() {
        ChestTrapReader parser = new ChestTrapReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "chest_trap.txt";

        try {
            ParseResult<ChestTrap> result = parser.parseWithResults(filename);

            ObjectRegistry.setChestTraps(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the artifacts from {@code artifact.txt} into {@link ObjectRegistry} — the port of C's
     * {@code artifact_parser} ({@code obj-init.c}). Artifacts whose {@code base-object:} names no
     * kind in {@code object.txt} get a special
     * {@link uk.co.jackoftradesltd.middle.objects.ObjectKind} synthesized into the shared object-kind
     * table, as C's {@code write_dummy_object_record} does while parsing. Must run after item objects
     * (so the ordinary kinds are already registered) and after activations, brands, slays and
     * curses, which artifacts reference.
     * <p>
     * Every error stops the load. An IO failure is logged and <em>rethrown</em> as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description).
     * <p>
     * This is also the loader {@code PlayerBirth} runs a second time to restore the standard
     * artifacts after a randart set, as C re-runs {@code artifact_parser} in
     * {@code player-birth.c}. The artifact assembler only synthesizes a kind when its lookup of the
     * artifact's {@code base-object:} finds none, so the second run finds the special kinds the first
     * left behind and does not register them again. Unlike C's {@code finish_parse_artifact}, the
     * second run does not repeat the curse-kind pass; the curses are not reloaded, so
     * {@link #parseCurseKinds()} has nothing to redo.
     *
     * <p>Function loadArtifacts coded before 261005, commented in full on 261013, updated on 261104
     * for the removal of the soft-error path.
     */
    public static void loadArtifacts() {
        ArtifactReader parser = new ArtifactReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "artifact.txt";

        try {
            ParseResult<Artifact> result = parser.parseWithResults(filename);

            ObjectRegistry.setArtifacts(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the ego items from {@code ego_item.txt} into {@link ObjectRegistry} — the port of C's
     * {@code ego_parser} ({@code obj-init.c}), whose {@code finish_parse_ego} builds {@code e_info}.
     * Must run after activations, brands, slays and curses, and after the object kinds its
     * {@code poss-items:} lines resolve against.
     * <p>
     * Every error stops the load. An IO failure is logged and <em>rethrown</em> as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description).
     *
     * <p>Function loadEgoItems coded before 261005, commented in full on 261013, updated on 261104
     * for the removal of the soft-error path.
     */
    public static void loadEgoItems() {
        EgoItemReader reader = new EgoItemReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "ego_item.txt";

        try {
            ParseResult<EgoItem> results = reader.parseWithResults(filename);

            ObjectRegistry.setEgoItems(results.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the activations from {@code activation.txt} into {@link ObjectRegistry} — the port of C's
     * {@code act_parser} ({@code obj-init.c}), whose {@code finish_parse_act} builds
     * {@code activations}. Must run before ego items and artifacts, which name an activation
     * directly (C's {@code findact}).
     * <p>
     * Every error stops the load. An IO failure is logged and <em>rethrown</em> as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description).
     * <p>
     * The list is stored in reverse file order, as the assembler returns it, which is C's
     * {@code activations[]} order. C's {@code finish_parse_act} also numbers each activation by its
     * slot ({@code activations[count].index}), which the save file records. The loader does not do
     * that: {@code ActivationAssembler} numbers the list through {@code Activation.setIndex} once
     * it is in order, from 0 at the head, where C counts from 1. {@code Activation} has no getter
     * for the index yet, because the savefile is not ported.
     *
     * <p>Function loadActivations coded before 261005, commented in full on 261013, updated on 261104
     * for the removal of the soft-error path.
     */
    public static void loadActivations() {
        ActivationReader reader = new ActivationReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "activation.txt";

        try {
            ParseResult<Activation> results = reader.parseWithResults(filename);
            
            ObjectRegistry.setActivations(results.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the object kinds from {@code object.txt} into {@link ObjectRegistry} — the port of C's
     * {@code object_parser} ({@code obj-init.c}), whose {@code finish_parse_object} builds
     * {@code k_info} and records {@code ordinary_kind_max}. Must run after object bases, summons,
     * curses, brands and slays, and before the artifacts, ego items, flavours and player classes
     * that resolve a kind.
     * <p>
     * Each kind is registered through {@link ObjectRegistry#addObjectKind}, which hands it the next
     * sval under its base and its index in the table, as C's parser does while reading the file
     * (the kind's base kind flags are merged in earlier, at assembly, as {@code finish_parse_object}
     * does). Once the file's kinds are in, {@link ObjectRegistry#updateObjectBaseKindMax} records the
     * ordinary-kind ceiling - C's {@code ordinary_kind_max} - so that the special artifact kinds
     * {@link #loadArtifacts()} adds later fall above it.
     * <p>
     * {@link ObjectRegistry#loadUnknownKinds} then resolves the {@code <unknown item>} and
     * {@code <unknown treasure>} sentinels. C does that at the end of {@code finish_parse_artifact},
     * after the artifacts; the port does it here because the kinds it names are ordinary ones and
     * nothing between the two reads them. C resolves a third sentinel there, {@code pile_kind}, which
     * has no Java counterpart yet.
     * <p>
     * Every error stops the load. An IO failure is logged and <em>rethrown</em> as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description). Either way the throw comes before the first {@code addObjectKind}, so a failed
     * file adds no kinds. This is the widest dependency in the suite, and continuing without it
     * would turn one failure into many.
     *
     * <p>Function loadItemObjects coded before 261005, commented in full on 261013, updated on 261104
     * for the removal of the soft-error path.
     *
     * @throws IOException declared but never thrown: the {@code IOException} from the parse is caught
     *                     inside and rethrown wrapped in a {@link RuntimeException}
     */
    public static void loadItemObjects() throws IOException {
        ItemObjectReader parser = new ItemObjectReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "object.txt";

        try {
            ParseResult<ObjectKind> results = parser.parseWithResults(filename);

            for (ObjectKind kind : results.items()) {
                ObjectRegistry.addObjectKind(kind);
            }

            ObjectRegistry.updateObjectBaseKindMax();
            ObjectRegistry.loadUnknownKinds();
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the curses from {@code curse.txt} into {@link ObjectRegistry} — the port of C's
     * {@code curse_parser} ({@code obj-init.c}), whose {@code finish_parse_curse} builds
     * {@code curses}. Must run after object bases and summons, and before the object kinds, ego
     * items and artifacts that carry a curse. The curse objects loaded here have no kind yet;
     * {@link #parseCurseKinds()} gives them one once the kind table is complete.
     * <p>
     * Every error stops the load. Unlike the other loaders here, the catch is on {@code Exception}
     * rather than {@code IOException}, so a grammar error ({@code ParseCancellationException}) or an
     * assembler refusal ({@code DataFileParseException}) is logged at {@code FATAL} and wrapped too:
     * the exception that escapes reads "Error while loading file" with the original as its cause.
     * <p>
     * The list is stored in reverse file order, as {@code CurseAssembler} returns it, which is C's
     * {@code curses[]} order. The two checks C makes in {@code finish_parse_curse} - no more than
     * 254 curses, and no {@code MULTIPLY_WEIGHT} curse with a negative weight - are made in that
     * assembler, not here.
     *
     * <p>Function loadCurses coded before 261005, commented in full on 261013, updated on 261104 for
     * the removal of the soft-error path.
     *
     * @throws IOException declared but never thrown: every failure, the {@code IOException}
     *                     included, is caught inside and rethrown wrapped in a
     *                     {@link RuntimeException}
     */
    public static void loadCurses() throws IOException {
        CurseReader parser = new CurseReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "curse.txt";

        try {
            ParseResult<Curse> result = parser.parseWithResults(filename);

            ObjectRegistry.setCurses(result.items());
        } catch (Exception e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the brands from {@code brand.txt} into {@link ObjectRegistry} — the port of C's
     * {@code brand_parser} ({@code obj-init.c}), whose {@code finish_parse_brand} builds
     * {@code brands}. Must run before the object kinds, ego items and artifacts that name a brand.
     * <p>
     * Every error stops the load, which matters here because three later loaders resolve against
     * this list. An IO failure is logged and <em>rethrown</em> as a {@link RuntimeException}; a
     * grammar or assembler error passes through unwrapped (see the class description).
     * <p>
     * The list is stored in reverse file order, as the assembler returns it, which is C's
     * {@code brands[]} order. C's {@code finish_parse_brand} also fails the parse when the file
     * holds more than 254 brands ({@code PARSE_ERROR_TOO_MANY_ENTRIES}); {@code BrandAssembler}
     * applies that limit and throws on the 255th record, so the load stops before the registry is
     * touched.
     *
     * <p>Function loadBrands coded before 261005, commented in full on 261013, updated on 261104 for
     * the removal of the soft-error path.
     *
     * @throws IOException declared but never thrown: the {@code IOException} from the parse is caught
     *                     inside and rethrown wrapped in a {@link RuntimeException}
     */
    public static void loadBrands() throws IOException {
        BrandReader parser = new BrandReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "brand.txt";

        try {
            ParseResult<Brand> result = parser.parseWithResults(filename);

            ObjectRegistry.setBrands(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the slays from {@code slay.txt} into {@link ObjectRegistry} — the port of C's
     * {@code slay_parser} ({@code obj-init.c}), whose {@code finish_parse_slay} builds {@code slays}.
     * Must run after monster bases, whose names each slay targets, and before the object kinds, ego
     * items and artifacts that name a slay.
     * <p>
     * Every error stops the load, matching brands - the symmetric loader beside it - since three
     * later loaders resolve against this list. An IO failure is logged and <em>rethrown</em> as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description).
     * <p>
     * The list is stored in reverse file order, as the assembler returns it, which is C's
     * {@code slays[]} order. C's {@code finish_parse_slay} also fails the parse when the file holds
     * more than 254 slays ({@code PARSE_ERROR_TOO_MANY_ENTRIES}); neither this loader nor
     * {@code SlayAssembler} applies that limit, so a longer file would register in full.
     *
     * <p>Function loadSlays coded before 261005, commented in full on 261013, updated on 261104 for
     * the removal of the soft-error path.
     *
     * @throws IOException declared but never thrown: the {@code IOException} from the parse is caught
     *                     inside and rethrown wrapped in a {@link RuntimeException}
     */
    public static void loadSlays() throws IOException {
        SlayReader parser = new SlayReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "slay.txt";

        try {
            ParseResult<Slay> result = parser.parseWithResults(filename);

            ObjectRegistry.setSlays(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the object bases (the per-tval defaults every object kind inherits) from
     * {@code object_base.txt} into {@link ObjectRegistry} — the port of C's
     * {@code object_base_parser} ({@code obj-init.c}), whose {@code finish_parse_object_base} fills
     * {@code kb_info}, indexed by tval. The first object-side loader to run, and a prerequisite for
     * curses, object kinds and everything downstream of them.
     * <p>
     * Every error stops the load. An IO failure is logged and <em>rethrown</em> as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description).
     * <p>
     * This is the loader whose earlier swallow-and-skip behaviour showed why a failure must stop the
     * load: a rejected file left {@link ObjectRegistry} holding {@code null} rather than an empty
     * list, and the failure surfaced fifteen loaders later as an {@code IllegalStateException} from
     * an unrelated assembler.
     *
     * <p>Function loadObjectBases coded before 261005, commented in full on 261013, updated on 261104
     * for the removal of the soft-error path.
     *
     * @throws IOException declared but never thrown: the {@code IOException} from the parse is caught
     *                     inside and rethrown wrapped in a {@link RuntimeException}
     */
    public static void loadObjectBases() throws IOException {
        ObjectBaseReader parser = new ObjectBaseReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "object_base.txt";

        try {
            ParseResult<ObjectBase> result = parser.parseWithResults(filename);

            ObjectRegistry.setObjectBases(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Finishes the curses by giving each one's own object its kind — the port of the tail of C's
     * {@code finish_parse_artifact} in {@code obj-init.c}, which looks up
     * {@code curse_object_kind} and then calls {@code write_curse_kinds}.
     *
     * <p>The kind is the {@code <curse object>} entry of {@code object.txt}, found by tval
     * {@link TValue#TV_NONE} and sval name; {@code ObjectUtils.lookupKind} reports a miss as a game
     * message and answers {@code null}, as C's {@code lookup_kind} does. The work is split from
     * {@link #writeCurseKinds(ObjectKind)} only so the lookup and the writes are separate steps, as
     * they are in C.
     *
     * <p>This has to run after {@link #loadItemObjects()}, because the kind lives in
     * {@code object.txt}, and cannot be done while the curses load, because {@link #loadCurses()}
     * runs first. It is called after {@link #loadArtifacts()} to follow C; nothing between the two
     * reads a curse's object.
     *
     * <p>Function parseCurseKinds coded on 261005, commented in full on 261005.
     */
    public static void parseCurseKinds() {
        ObjectKind curseObjectKind = ObjectUtils.lookupKind(TValue.TV_NONE, "<curse object>");
        writeCurseKinds(curseObjectKind);
    }

    /**
     * Writes the {@code <curse object>} kind onto every curse's object and its known twin — the
     * port of C's {@code write_curse_kinds} in {@code obj-init.c}.
     *
     * <p>For each curse, its {@link Curse#getItemObject() object} takes the kind and the kind's sval.
     * Its known object is created if the curse has none, takes the same kind and sval, and has
     * {@link ObjectNotice#OBJ_NOTICE_ASSESSED} switched on "so it can be fully known" (C's comment).
     * That is what lets {@code player_know_object} treat a curse object as an ordinary item. An
     * existing known object is kept, as C tolerates one left over from a restart that redoes the
     * artifacts.
     *
     * <p>A {@code null} {@code curseObjectKind} is the kind missing from the kind table, not a
     * missing curse list. It is carried through as C carries it: every object and known twin gets a
     * {@code null} kind and an sval of {@code -1}, which is what C's {@code lookup_sval} answers for
     * a name it cannot find, and nothing throws.
     *
     * <p>C's loop starts at 1 because slot 0 of {@code curses[]} is a dummy. The port's curse list
     * has no dummy, so this walks all of {@link ObjectRegistry#getCurses()}.
     *
     * <p>Function writeCurseKinds coded on 261005, commented in full on 261005.
     *
     * @param curseObjectKind the {@code <curse object>} kind, or {@code null} if the kind table has none
     */
    private static void writeCurseKinds(ObjectKind curseObjectKind) {
        int sVal = curseObjectKind == null ? -1 : curseObjectKind.getsVal();

        for (Curse curse : ObjectRegistry.getCurses()) {
            curse.getItemObject().setKind(curseObjectKind);
            curse.getItemObject().setsValue(sVal);
            // Tolerate an already allocated known version
            // restarting without exiting and redoing the
            // artifacts in doCmdAcceptCharacter()
            if (curse.getItemObject().getKnown() == null)
                curse.getItemObject().setKnown(new ItemObject());
            curse.getItemObject().getKnown().setKind(curseObjectKind);
            curse.getItemObject().getKnown().setsValue(sVal);
            // Mark it as touched, so it can be fully known
            curse.getItemObject().getKnown().setNoticeOn(ObjectNotice.OBJ_NOTICE_ASSESSED);
        }
    }
}
