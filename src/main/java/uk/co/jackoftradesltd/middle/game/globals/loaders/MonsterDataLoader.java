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
import uk.co.jackoftradesltd.frontend.colour.FlickerTable;
import uk.co.jackoftradesltd.frontend.colour.VisualsCycler;
import uk.co.jackoftradesltd.middle.cave.PitProfile;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
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
 * bases, bases before summons, summons before spell types, and blow methods, blow effects and
 * visuals before monsters, with monsters before pit profiles. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * <p>Each loader stands in for one C file parser and its {@code finish_parse_*} function:
 * {@code pain_parser}, {@code mon_base_parser}, {@code meth_parser}, {@code eff_parser},
 * {@code mon_spell_parser}, {@code monster_parser} and {@code pit_parser} in {@code mon-init.c},
 * {@code summon_parser} in {@code mon-summon.c}, and the visuals file parser in {@code ui-visuals.c}.
 * C's {@code init_arrays} ({@code init.c}) runs them in the same order. The spell types are not a
 * dependency of monsters or pits: both resolve their {@code spells:} and {@code spell-req:} codes
 * against the {@code MonsterSpell} enum, as C resolves them against the static
 * {@code r_info_spell_flags} names.
 *
 * <p>Every error is fatal, as in C, and the loaders all behave alike. C's run functions call
 * {@code parse_file_quit_not_found} ({@code datafile.c}), which quits on a missing file, and
 * {@code init_arrays} quits on any parser error. There are three ways a load fails here, and only
 * the first is handled by the loaders:
 * <ul>
 *   <li>an {@link IOException} from the reader is logged at {@code FATAL} and rethrown wrapped in a
 *       {@link RuntimeException}, so none escapes a loader;</li>
 *   <li>a grammar or lexer error is thrown by the reader as a {@code ParseCancellationException}
 *       ({@code ParseErrors.throwIfAny});</li>
 *   <li>a record the assembler refuses is thrown as a {@code DataFileParseException}.</li>
 * </ul>
 * The last two are unchecked and pass through every loader unlogged and unwrapped;
 * {@code GameConstants.init()} catches them and names the file. Nothing is registered from a file
 * that fails, so the registry never holds a partial list.
 *
 * <p>Class MonsterDataLoader coded before 261011, commented in full on 261013, failure paragraph
 * rewritten on 261104 for the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class MonsterDataLoader {
    /**
     * Log4j logger for this class, used by every loader to report an IO failure at {@code FATAL}
     * before it is rethrown. Grammar and assembler errors are not logged here (see the class block).
     *
     * <p>Field logger coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger(MonsterDataLoader.class);

    /**
     * Load the monster races from {@code monster.txt} into {@link MonsterRegistry}, then run a second
     * pass over every race that stands in for the end of C's {@code finish_parse_monster}
     * ({@code mon-init.c}). Must run after bases, blow methods, blow effects and visuals, which the
     * monster assembler looks up while it builds each race (a race with a blow looks up both its
     * method and its effect). It does not need the spell types: {@code spells:} resolves against the
     * {@code MonsterSpell} enum.
     * <p>
     * The second pass does three things for each race, in this order, now that every race exists:
     * <ol>
     *   <li>{@link MonsterRace#resolveFriends()} points each friend entry at its race.</li>
     *   <li>{@link MonsterRace#resolveShapes()} points each shape entry at its base or race.</li>
     *   <li>The race is given a blank {@link MonsterLore} if it has none, and
     *       {@link MonsterLore#initblows(int)} gives that lore one blank blow per blow the race has.
     *       This is the Java form of C's {@code l_list} allocation, which gives every race a lore
     *       record and sizes its {@code blows} array. After this pass {@link MonsterRace#getLore()}
     *       can be called on every loaded race, including the {@code <player>} record at element 0.</li>
     * </ol>
     * C finishes all the name resolution before it allocates any lore, where this loop does the three
     * steps race by race. Resolution only looks up races that are already registered, so the result
     * is the same. C sizes each lore's blows to the longest blow list of any race
     * ({@code z_info->mon_blows_max}); this sizes each to its own race's blow count, as the race's
     * blow list is not padded either (see {@link MonsterLore}).
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block). The second
     * pass is the exception to "nothing registered": it runs after the races are registered, and a
     * friend or shape reference that cannot be resolved throws from
     * {@link MonsterRace#resolveFriends()} or {@link MonsterRace#resolveShapes()}. That is not caught
     * here, so it stops the load part-way, with the races registered and only those before it given
     * lore.
     * <p>
     * A race that already has a lore record keeps it, and only has its blows sized.
     *
     * <p>Function loadMonsters coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code monster.txt} cannot be read; or, unwrapped, if it holds a
     *                          grammar or assembler error or a reference that cannot be resolved
     */
    public static void loadMonsters() {
        MonsterReader parser = new MonsterReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "monster.txt";

        try {
            ParseResult<MonsterRace> result = parser.parseWithResults(filename);

            MonsterRegistry.setMonsterRaces(result.items());
            
            // Second pass: friend and shape references to other races can only be resolved once every
            // race exists (a monster may reference one defined later in the file). Mirrors C's
            // finish_parse_monster.
            for (MonsterRace race : MonsterRegistry.getMonsterRaces()) {
                race.resolveFriends();
                race.resolveShapes();
                
                if (!race.hasLore())
                    race.setLore(new MonsterLore());
                race.getLore().initblows(race.getBlows().size());
            }
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the pit/nest profiles from {@code pit.txt} into {@link MonsterRegistry}, standing in for
     * {@code pit_parser} and {@code finish_parse_pit} in {@code mon-init.c}. Must run after bases
     * and monsters: the pit assembler resolves {@code mon-base:} through
     * {@link MonsterRegistry#lookupMonsterBase} and {@code mon-ban:} through
     * {@link MonsterRegistry#lookupMonsterRace}. It does not need the spell types, as
     * {@code spell-req:} and {@code spell-ban:} resolve against the {@code MonsterSpell} enum.
     * <p>
     * C's {@code finish_parse_pit} also copies the profiles into {@code pit_info} and numbers each
     * with {@code pit_idx} (file order), with a blank slot after the last and {@code pit_max} one
     * higher than the record count. The list here is in file order with no blank slot, no index is
     * stored (nothing else in the C source reads {@code pit_idx}), and
     * {@link MonsterRegistry#getMonsterPitTypeMax()} is the plain size (see the registry's class
     * block).
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block).
     * {@code MonsterRegistry} has no accessor for the profiles yet, so nothing reads the result
     * back.
     *
     * <p>Function loadPitProfiles coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code pit.txt} cannot be read; or, unwrapped, if it holds a
     *                          grammar or assembler error
     */
    public static void loadPitProfiles() {
        PitReader parser = new PitReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "pit.txt";

        try {
            ParseResult<PitProfile> result = parser.parseWithResults(filename);

            MonsterRegistry.setMonsterPitProfiles(result.items());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the colour cycler and flicker tables from {@code visuals.txt} into {@link MonsterRegistry}.
     * Must run before monsters, whose {@code color-cycle:} lines are resolved against the cycler
     * table. This is the Java form of the visuals file parser in {@code ui-visuals.c}, which C runs
     * from {@code ui_visuals_module_init} (it "needs to load before monsters and objects") rather
     * than from {@code init_arrays}. C's {@code visuals_file_parser_finish} fails the load, and the
     * module quits, if no cycler table came out of the parse.
     * <p>
     * The odd one out: {@code visuals.txt} is parsed <em>twice</em>, once for cyclers and once for
     * flickers, so this is the only loader that gets two {@link ParseResult}s. Any error in either
     * parse is fatal (see the class block).
     * <p>
     * Both registrations take {@code items().getFirst()}, so unlike its neighbours this loader
     * needs a non-empty parse. A failed parse throws inside the reader, so {@code getFirst()} is
     * only reached after a clean one; the {@link java.util.NoSuchElementException} can only come
     * from a clean parse that still produced no table, and it is not caught here. Both files are
     * parsed before either table is registered, so a failed flicker parse leaves the cyclers unset
     * as well.
     *
     * <p>Function loadVisualTables coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code visuals.txt} cannot be read; or, unwrapped, if either parse
     *                          holds a grammar or assembler error
     */
    public static void loadVisualTables() {
        VisualsReader parser = new VisualsReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "visuals.txt";

        try {
            ParseResult<VisualsCycler> cyclers = parser.parseCyclerWithResults(filename);
            ParseResult<FlickerTable> flickers = parser.parseFlickerWithResults(filename);
            
            MonsterRegistry.setVisualsCyclerTable(cyclers.items().getFirst());
            MonsterRegistry.setVisualsFlickerTable(flickers.items().getFirst());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the monster spell types from {@code monster_spell.txt} into {@link MonsterRegistry},
     * standing in for {@code mon_spell_parser} and {@code finish_parse_mon_spell} in
     * {@code mon-init.c}. C's finish function only keeps the parsed list as {@code monster_spells}.
     * <p>
     * Must run after {@link #loadSummons()}: 16 of the 91 spells in {@code lib/gamedata} carry a
     * {@code SUMMON} effect, whose subtype the effect assembler resolves through
     * {@link MonsterRegistry#lookupSummon}, which throws if the summons have not been loaded.
     * Loading summons in turn needs monster bases, which need monster pains.
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block).
     * {@code MonsterRegistry} has no accessor for the spell types yet, and neither the monster nor
     * the pit loader depends on them.
     *
     * <p>Function loadMonsterSpellTypes coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code monster_spell.txt} cannot be read; or, unwrapped, if it
     *                          holds a grammar or assembler error
     */
    public static void loadMonsterSpellTypes() {
        MonsterSpellReader parser = new MonsterSpellReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "monster_spell.txt";

        try {
            ParseResult<MonsterSpellType> result = parser.parseWithResults(filename);

            MonsterRegistry.setMonsterSpellTypes(result.items());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the blow effects — what a monster attack does — from {@code blow_effects.txt} into
     * {@link MonsterRegistry}, standing in for {@code eff_parser} and {@code finish_parse_eff} in
     * {@code mon-init.c}. Must run after projections, whose {@code lash-type:} the blow-effect
     * assembler resolves, and before monsters, whose blows look an effect up by name.
     * <p>
     * C's {@code finish_parse_eff} files the effects into {@code blow_effects} in file order, with
     * a blank slot after the last and {@code blow_effects_max} one higher than the record count.
     * The list here has no blank slot and {@link MonsterRegistry#getMonsterBlowsEffectsMax()} is the
     * plain size (see the registry's class block).
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block), so
     * {@link #loadMonsters()} never reaches {@link MonsterRegistry#lookupBlowEffect} with the
     * table unloaded.
     *
     * <p>Function loadBlowEffects coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code blow_effects.txt} cannot be read; or, unwrapped, if it
     *                          holds a grammar or assembler error
     */
    public static void loadBlowEffects() {
        BlowEffectReader parser = new BlowEffectReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "blow_effects.txt";

        try {
            ParseResult<BlowEffect> result = parser.parseWithResults(filename);
            
            MonsterRegistry.setBlowEffects(result.items());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the blow methods from {@code blow_methods.txt} into {@link MonsterRegistry}, standing in
     * for {@code meth_parser} and {@code finish_parse_meth} in {@code mon-init.c}. Must run before
     * monsters, whose attacks reference a method by name.
     * <p>
     * C's {@code finish_parse_meth} copies the methods into {@code blow_methods} from slot 1, leaving
     * slot 0 blank, and {@code blow_methods_max} is one higher than the record count. The list here
     * has no blank slot and {@link MonsterRegistry#getMonsterBlowsMethodsMax()} is the plain size
     * (see the registry's class block).
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block), so
     * {@link #loadMonsters()} never reaches {@link MonsterRegistry#lookupBlowMethod} with the
     * table unloaded.
     *
     * <p>Function loadBlowMethods coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code blow_methods.txt} cannot be read; or, unwrapped, if it
     *                          holds a grammar or assembler error
     */
    public static void loadBlowMethods() {
        BlowMethodReader parser = new BlowMethodReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "blow_methods.txt";

        try {
            ParseResult<BlowMethod> result = parser.parseWithResults(filename);
            
            MonsterRegistry.setBlowMethods(result.items());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the summon types from {@code summon.txt} into {@link MonsterRegistry}, standing in for
     * {@code summon_parser} and {@code finish_parse_summon} in {@code mon-summon.c}. Must run after
     * monster bases, and before every file whose effects can name a {@code SUMMON} subtype (the
     * monster spells, and per the {@code GameConstants.init()} comments the curses, objects and
     * classes), which resolve it through {@link MonsterRegistry#lookupSummon}.
     * <p>
     * C's finish function also back-patches each summon's fallback index from its {@code fallback:}
     * name, with -1 for a name that matches nothing. Here the assembler's second pass links the
     * {@code Summon} references instead, and an unresolvable fallback name is not a fault: the
     * summon is kept with a {@code null} fallback, without a report, as C stores -1. C's {@code summon_max} is one higher than the
     * record count and the registry keeps no count.
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block):
     * {@code lookupSummon} throws {@link IllegalStateException} while the summons are unloaded, so
     * continuing past a failure here would only relocate the crash.
     *
     * <p>Function loadSummons coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code summon.txt} cannot be read; or, unwrapped, if it holds a
     *                          grammar or assembler error
     */
    public static void loadSummons() {
        SummonReader parser = new SummonReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "summon.txt";

        try {
            ParseResult<Summon> result = parser.parseWithResults(filename);

            MonsterRegistry.setSummons(result.items());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the monster bases from {@code monster_base.txt} into {@link MonsterRegistry}, standing in
     * for {@code mon_base_parser} and {@code finish_parse_mon_base} in {@code mon-init.c}. Must run
     * after pains, whose index each base's {@code pain:} line resolves, and before slays, summons,
     * monsters and pit profiles, all of which resolve a base by name. C's finish function only keeps
     * the parsed list as {@code rb_info}.
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block); with four
     * loaders downstream this is one to stop on rather than let resurface as a wall of unresolved
     * base names.
     *
     * <p>Function loadMonsterBases coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code monster_base.txt} cannot be read; or, unwrapped, if it
     *                          holds a grammar or assembler error
     */
    public static void loadMonsterBases() {
        MonsterBaseReader parser = new MonsterBaseReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "monster_base.txt";

        try {
            ParseResult<MonsterBase> result = parser.parseWithResults(filename);

            MonsterRegistry.setMonsterBases(result.items());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the monster pain-message sets from {@code pain.txt} into {@link MonsterRegistry},
     * standing in for {@code pain_parser} and {@code finish_parse_pain} in {@code mon-init.c}. The
     * first monster-side loader to run, and a prerequisite for monster bases.
     * <p>
     * C's {@code finish_parse_pain} files each set into {@code pain_messages} under its own pain
     * index, with slot 0 blank, and sets {@code mp_max} to the highest index plus one. The records
     * here stay in file order, are found by comparing their index, and
     * {@link MonsterRegistry#getMonsterPainMsgMax()} is the list size (see the registry's class
     * block). The shipped {@code pain.txt} has twelve sets, indices 1 to 12.
     * <p>
     * Any error in the file is fatal and nothing is registered (see the class block), stopping the
     * load at the root of the monster chain.
     *
     * <p>Function loadPain coded before 261011, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws RuntimeException if {@code pain.txt} cannot be read; or, unwrapped, if it holds a
     *                          grammar or assembler error
     */
    public static void loadPain() {
        PainReader parser = new PainReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "pain.txt";

        try {
            ParseResult<MonsterPain> result = parser.parseWithResults(filename);

            MonsterRegistry.setMonsterPains(result.items());
        } catch (IOException e) {
            String message = "Error while parsing file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
