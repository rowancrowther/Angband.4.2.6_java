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
import uk.co.jackoftradesltd.frontend.colour.FlickerTable;
import uk.co.jackoftradesltd.frontend.colour.VisualsCycler;
import uk.co.jackoftradesltd.middle.cave.PitProfile;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.*;

import java.io.IOException;

/**
 * Startup loader for the monster slice: parses the monster-domain gamedata files
 * ({@code pain.txt}, {@code monster_base.txt}, {@code summon.txt}, {@code monster_spell.txt},
 * {@code blow_methods.txt}, {@code blow_effects.txt}, {@code visuals.txt}, {@code monster.txt},
 * {@code pit.txt}) and populates {@link uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry}
 * through its setters.
 *
 * <p>This is the write side of the monster slice, paired with {@code MonsterRegistry} (the read
 * side). Its loaders are invoked by {@code GameConstants.init()} in dependency order — pain before
 * bases, bases before summons, visuals/blow-methods/spell-types before monsters, monsters before
 * pit profiles. It was split out of {@code GameConstants} as one domain slice of the loader/registry
 * refactor.
 *
 * @author Rowan Crowther
 */
public class MonsterDataLoader {
    private static final Logger logger = LogManager.getLogger(MonsterDataLoader.class);

    /**
     * Load the monster races from {@code monster.txt} into {@link MonsterRegistry}, then run a second
     * pass resolving each race's friend and shape references (mirroring C's {@code finish_parse_monster})
     * now that every race exists. Must run after bases, visuals, blow methods and spell types.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the races that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>. Watch the second pass here: friend and shape references are resolved
     * against the races just registered, so a race dropped by a soft error becomes an unresolvable
     * reference for its neighbours rather than a silent absence.
     */
    public static void loadMonsters() {
        MonsterReader parser = new MonsterReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "monster.txt";

        try {
            ParseResult<MonsterRace> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setMonsterRaces(result.items());
            MonsterRegistry.monsterRaceMax = MonsterRegistry.monsterRaces.size();

            // Second pass: friend and shape references to other races can only be resolved once every
            // race exists (a monster may reference one defined later in the file). Mirrors C's
            // finish_parse_monster.
            for (MonsterRace race : MonsterRegistry.getMonsterRaces()) {
                race.resolveFriends();
                race.resolveShapes();
            }
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the pit/nest profiles from {@code pit.txt} into {@link MonsterRegistry}. Must run after
     * monsters, bases and spell types, which the pit assembler resolves against.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the profiles that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>: pits are a dungeon-generation flourish, so losing them costs variety
     * rather than the game.
     */
    public static void loadPitProfiles() {
        PitReader parser = new PitReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "pit.txt";

        try {
            ParseResult<PitProfile> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setMonsterPitProfiles(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the colour cycler and flicker tables from {@code visuals.txt} into {@link MonsterRegistry}.
     * Must run before monsters, whose colours reference these tables.
     * <p>
     * The odd one out: {@code visuals.txt} is parsed <em>twice</em>, once for cyclers and once for
     * flickers, so this is the only loader that reports two {@link ParseResult}s - both are passed
     * through {@link ErrorParsing#reportAndCheck} so neither parse's errors are lost. An IO failure
     * is logged and <em>swallowed</em>.
     * <p>
     * Note both registrations take {@code items().getFirst()}, so unlike its neighbours this loader
     * needs a non-empty parse: an empty table would fail on the {@code getFirst()} rather than
     * degrade. That makes it the clearest candidate for acting on the helper's return value.
     */
    public static void loadVisualTables() {
        VisualsReader parser = new VisualsReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "visuals.txt";

        try {
            ParseResult<VisualsCycler> cyclers = parser.parseCyclerWithResults(filename);
            ParseResult<FlickerTable> flickers = parser.parseFlickerWithResults(filename);

            ErrorParsing.reportAndCheck(filename, cyclers, logger);
            ErrorParsing.reportAndCheck(filename, flickers, logger);

            MonsterRegistry.setVisualsCyclerTable(cyclers.items().getFirst());
            MonsterRegistry.setVisualsFlickerTable(flickers.items().getFirst());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the monster spell types from {@code monster_spell.txt} into {@link MonsterRegistry}.
     * <p>
     * Must run after {@link #loadSummons()}: two of the 91 spells carry a {@code SUMMON} effect,
     * whose subtype the effect assembler resolves through {@link MonsterRegistry#lookupSummon}, which throws if
     * the summon table is still empty. Loading summons in turn needs monster bases, which need
     * monster pains.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the spells that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>.
     */
    public static void loadMonsterSpellTypes() {
        MonsterSpellReader parser = new MonsterSpellReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "monster_spell.txt";

        try {
            ParseResult<MonsterSpellType> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setMonsterSpellTypes(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the blow effects — what a monster attack does — from {@code blow_effects.txt} into
     * {@link MonsterRegistry}. Must run after projections, whose {@code lash-type:} the blow-effect
     * assembler resolves.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the effects that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>; a missing blow effect surfaces the first time a monster attacks with
     * it.
     */
    public static void loadBlowEffects() {
        BlowEffectReader parser = new BlowEffectReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "blow_effects.txt";

        try {
            ParseResult<BlowEffect> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setBlowEffects(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the blow methods from {@code blow_methods.txt} into {@link MonsterRegistry}. Must run
     * before monsters, whose attacks reference a method by name.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the methods that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>, leaving the monsters that name a missing method to report it.
     */
    public static void loadBlowMethods() {
        BlowMethodReader parser = new BlowMethodReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "blow_methods.txt";

        try {
            ParseResult<BlowMethod> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setBlowMethods(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the summon types from {@code summon.txt} into {@link MonsterRegistry}. Must run after
     * monster bases, and before the spells and effects whose {@code SUMMON} subtype resolves
     * through {@link MonsterRegistry#lookupSummon}.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the summons that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>rethrown</em>: {@code lookupSummon} throws on an empty table, so continuing past a
     * failure here only relocates the crash.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadSummons() throws IOException {
        SummonReader parser = new SummonReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "summon.txt";

        try {
            ParseResult<Summon> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setSummons(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the monster bases from {@code monster_base.txt} into {@link MonsterRegistry}. Must run
     * after pains, and before slays, summons and the monsters themselves, all of which resolve a
     * base by name.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the bases that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>rethrown</em>; with four loaders downstream this is one to stop on rather than let
     * resurface as a wall of unresolved base names.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadMonsterBases() throws IOException {
        MonsterBaseReader parser = new MonsterBaseReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "monster_base.txt";

        try {
            ParseResult<MonsterBase> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setMonsterBases(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the monster pain-message sets from {@code pain.txt} into {@link MonsterRegistry}. The
     * first monster-side loader to run, and a prerequisite for monster bases.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the pain sets that
     * did assemble are registered regardless, per the partial-results contract. An IO failure is
     * logged and <em>rethrown</em>, stopping the load at the root of the monster chain.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadPain() throws IOException {
        PainReader parser = new PainReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "pain.txt";

        try {
            ParseResult<MonsterPain> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            MonsterRegistry.setMonsterPains(result.items());
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }
}
