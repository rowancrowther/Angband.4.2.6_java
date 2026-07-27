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

package uk.co.jackoftrades.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.parser.*;
import uk.co.jackoftrades.frontend.colour.FlickerTable;
import uk.co.jackoftrades.frontend.colour.VisualsCycler;
import uk.co.jackoftrades.middle.cave.PitProfile;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftrades.middle.monsters.*;

import java.io.IOException;

/**
 * Startup loader for the monster slice: parses the monster-domain gamedata files
 * ({@code pain.txt}, {@code monster_base.txt}, {@code summon.txt}, {@code monster_spell.txt},
 * {@code blow_methods.txt}, {@code blow_effects.txt}, {@code visuals.txt}, {@code monster.txt},
 * {@code pit.txt}) and populates {@link uk.co.jackoftrades.middle.game.globals.registry.MonsterRegistry}
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
    private static final Logger logger = LogManager.getLogger();


    /**
     * Load the monster races from {@code monster.txt} into {@link MonsterRegistry}, then run a second
     * pass resolving each race's friend and shape references (mirroring C's {@code finish_parse_monster})
     * now that every race exists. Must run after bases, visuals, blow methods and spell types.
     */
    public static void loadMonsters() {
        MonsterReader parser = new MonsterReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "monster.txt";

        try {
            ParseResult<MonsterRace> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

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
     */
    public static void loadPitProfiles() {
        PitReader parser = new PitReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "pit.txt";

        try {
            ParseResult<PitProfile> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MonsterRegistry.setMonsterPitProfiles(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the colour cycler and flicker tables from {@code visuals.txt} into {@link MonsterRegistry}.
     * Must run before monsters, whose colours reference these tables.
     */
    public static void loadVisualTables() {
        VisualsReader parser = new VisualsReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "visuals.txt";

        try {
            ParseResult<VisualsCycler> cyclers = parser.parseCyclerWithResults(filename);
            ParseResult<FlickerTable> flickers = parser.parseFlickerWithResults(filename);

            if (cyclers.hasErrors() || flickers.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

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
     * Note that a file with soft errors logs and returns without populating the field, leaving
     * it null rather than partially filled - the same shape as the loaders around it.
     *
     * @author Rowan Crowther
     */
    public static void loadMonsterSpellTypes() {
        MonsterSpellReader parser = new MonsterSpellReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "monster_spell.txt";

        try {
            ParseResult<MonsterSpellType> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MonsterRegistry.setMonsterSpellTypes(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the blow effects — what a monster attack does — from {@code blow_effects.txt} into
     * {@link MonsterRegistry}. Must run after projections, whose {@code lash-type:} the blow-effect
     * assembler resolves.
     *
     * @author Rowan Crowther
     */
    public static void loadBlowEffects() {
        BlowEffectReader parser = new BlowEffectReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "blow_effects.txt";

        try {
            ParseResult<BlowEffect> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MonsterRegistry.setBlowEffects(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the blow methods from {@code blow_methods.txt} into {@link MonsterRegistry}. Must run
     * before monsters, whose attacks reference a method by name.
     */
    public static void loadBlowMethods() {
        BlowMethodReader parser = new BlowMethodReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "blow_methods.txt";

        try {
            ParseResult<BlowMethod> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MonsterRegistry.setBlowMethods(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the Summon information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadSummons() throws IOException {
        SummonReader parser = new SummonReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "summon.txt";

        try {
            ParseResult<Summon> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MonsterRegistry.setSummons(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the MonsterBase information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadMonsterBases() throws IOException {
        MonsterBaseReader parser = new MonsterBaseReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "monster_base.txt";

        try {
            ParseResult<MonsterBase> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MonsterRegistry.setMonsterBases(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Pain information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadPain() throws IOException {
        PainReader parser = new PainReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "pain.txt";

        try {
            ParseResult<MonsterPain> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MonsterRegistry.setMonsterPains(result.items());
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }
}
