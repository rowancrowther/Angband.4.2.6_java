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
import uk.co.jackoftradesltd.channel.parser.ErrorParsing;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
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
 * side). Its loaders are invoked by {@code GameConstants.init()} in dependency order — bases before
 * slays/brands/curses; curses and item objects before ego items and artifacts. {@code loadItemObjects}
 * registers the ordinary object kinds and {@code loadArtifacts} synthesizes the special artifact
 * kinds into the same table, so their order is load-bearing. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * <p>One member is not a file loader: {@link #parseCurseKinds()} finishes the curses once the kind
 * table is complete, giving each curse's own object the {@code <curse object>} kind. It runs after
 * {@link #loadArtifacts()}, where C does the same at the end of {@code finish_parse_artifact} in
 * {@code obj-init.c}.
 *
 * <p>Class ObjectDataLoader coded before 261005, class description updated for the curse-kind pass
 * on 261005.
 *
 * @author Rowan Crowther
 */
public class ObjectDataLoader {
    private static final Logger logger = LogManager.getLogger(ObjectDataLoader.class);

    /**
     * Load the object properties from {@code object_property.txt} into {@link ObjectRegistry}. Must
     * run after the UI entries, which the property assembler resolves {@code bindui} targets against.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the properties that
     * did assemble are registered regardless, per the partial-results contract. An IO failure is
     * logged and <em>swallowed</em>; only the renderer reads these, so the cost is a blank entry on
     * the character sheet rather than a failed load.
     */
    public static void loadObjectProperties() {
        ObjectPropertyReader parser = new ObjectPropertyReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "object_property.txt";

        try {
            ParseResult<ObjectProperty> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            ObjectRegistry.setObjectProperties(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the chest traps from {@code chest_trap.txt} into {@link ObjectRegistry}. Must run after
     * summons, which the {@code SUMMON} trap's effect resolves against - and so, transitively, after
     * monster bases and pains. Nothing else in the object slice depends on this list, so it may run
     * late.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and whatever assembled is
     * registered regardless, per the partial-results contract. Note that this file is validated as a
     * whole rather than record by record: if it breaks any of the structural rules stated at the top
     * of {@code chest_trap.txt}, the assembler returns an empty list and the registry is left with no
     * traps at all rather than a partial set. An IO failure is logged and <em>swallowed</em>; the
     * cost is chests that generate untrapped.
     */
    public static void loadChestTraps() {
        ChestTrapReader parser = new ChestTrapReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "chest_trap.txt";

        try {
            ParseResult<ChestTrap> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            ObjectRegistry.setChestTraps(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the artifacts from {@code artifact.txt} into {@link ObjectRegistry}, synthesizing a special
     * {@link uk.co.jackoftradesltd.middle.objects.ObjectKind} for each into the shared object-kind table.
     * Must run after item objects (so the ordinary kinds are already registered) and after
     * activations, brands, slays and curses, which artifacts reference.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the artifacts that
     * did assemble are registered regardless, per the partial-results contract. An IO failure is
     * logged and <em>swallowed</em>. Note the synthesis side effect: a dropped artifact also means
     * its special object kind is never added, so the kind table silently differs from the C
     * original's - worth remembering when comparing counts.
     */
    public static void loadArtifacts() {
        ArtifactReader parser = new ArtifactReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "artifact.txt";

        try {
            ParseResult<Artifact> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            ObjectRegistry.setArtifacts(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the ego items from {@code ego_item.txt} into {@link ObjectRegistry}. Must run after
     * activations, brands, slays and curses, and after the object kinds its {@code poss-items:}
     * lines resolve against.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the ego items that
     * did assemble are registered regardless, per the partial-results contract. An IO failure is
     * logged and <em>swallowed</em>, leaving item generation with fewer ego types to choose from.
     */
    public static void loadEgoItems() {
        EgoItemReader reader = new EgoItemReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "ego_item.txt";

        try {
            ParseResult<EgoItem> results = reader.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, results, logger);

            ObjectRegistry.setEgoItems(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the activations from {@code activation.txt} into {@link ObjectRegistry}. Must run before
     * ego items and artifacts, which name an activation directly.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the activations that
     * did assemble are registered regardless, per the partial-results contract. An IO failure is
     * logged and <em>swallowed</em>; a dropped activation surfaces as an unresolved name in the two
     * loaders that follow.
     */
    public static void loadActivations() {
        ActivationReader reader = new ActivationReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "activation.txt";

        try {
            ParseResult<Activation> results = reader.parseWithResult(filename);

            ErrorParsing.reportAndCheck(filename, results, logger);

            ObjectRegistry.setActivations(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the object kinds from {@code object.txt} into {@link ObjectRegistry}. Must run after
     * object bases, summons, curses, brands and slays, and before the artifacts, ego items,
     * flavours and player classes that resolve a kind.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the kinds that did
     * assemble are registered regardless, per the partial-results contract - note this loader adds
     * kinds one at a time rather than setting a list, so a partial parse leaves a genuinely partial
     * table. An IO failure is logged and <em>rethrown</em>: this is the widest dependency in the
     * suite, and continuing without it turns one failure into many.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadItemObjects() throws IOException {
        ItemObjectReader parser = new ItemObjectReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "object.txt";

        try {
            ParseResult<ObjectKind> results = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, results, logger);

            for (ObjectKind kind : results.items()) {
                ObjectRegistry.addObjectKind(kind);
            }

            ObjectRegistry.updateObjectBaseKindMax();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the curses from {@code curse.txt} into {@link ObjectRegistry}. Must run after object
     * bases and summons, and before the object kinds, ego items and artifacts that carry a curse.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the curses that did
     * assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} rather than {@code IOException} and <em>rethrows</em>, so any failure stops
     * the load.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadCurses() throws IOException {
        CurseReader parser = new CurseReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "curse.txt";

        try {
            ParseResult<Curse> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            ObjectRegistry.setCurses(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the brands from {@code brand.txt} into {@link ObjectRegistry}. Must run before the
     * object kinds, ego items and artifacts that name a brand.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the brands that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>rethrown</em>, since three later loaders resolve against this list.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadBrands() throws IOException {
        BrandReader parser = new BrandReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "brand.txt";

        try {
            ParseResult<Brand> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            ObjectRegistry.setBrands(result.items());
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the slays from {@code slay.txt} into {@link ObjectRegistry}. Must run after monster
     * bases, whose names each slay targets, and before the object kinds, ego items and artifacts
     * that name a slay.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the slays that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>rethrown</em>, matching brands - the symmetric loader beside it - since three later
     * loaders resolve against this list. (It previously caught {@code Exception} without
     * rethrowing, which swallowed unchecked exceptions too; that made it the one loader out of step
     * with its neighbours.)
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadSlays() throws IOException {
        SlayReader parser = new SlayReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "slay.txt";

        try {
            ParseResult<Slay> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            ObjectRegistry.setSlays(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the object bases (the per-tval defaults every object kind inherits) from
     * {@code object_base.txt} into {@link ObjectRegistry}. The first object-side loader to run, and
     * a prerequisite for curses, object kinds and everything downstream of them.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the bases that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>rethrown</em>.
     * <p>
     * This is the loader whose earlier swallow-and-skip behaviour proved the case for the
     * partial-results contract: a rejected file left {@link ObjectRegistry} holding {@code null}
     * rather than an empty list, and the failure surfaced fifteen loaders later as an
     * {@code IllegalStateException} from an unrelated assembler.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadObjectBases() throws IOException {
        ObjectBaseReader parser = new ObjectBaseReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "object_base.txt";

        try {
            ParseResult<ObjectBase> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            ObjectRegistry.setObjectBases(result.items());
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
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
            // Mark it as tounched, so it can be fully known
            curse.getItemObject().getKnown().setNoticeOn(ObjectNotice.OBJ_NOTICE_ASSESSED);
        }
    }
}
