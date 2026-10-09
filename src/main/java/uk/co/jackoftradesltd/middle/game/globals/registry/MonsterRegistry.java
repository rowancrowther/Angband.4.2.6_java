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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.frontend.colour.FlickerTable;
import uk.co.jackoftradesltd.frontend.colour.VisualsCycler;
import uk.co.jackoftradesltd.middle.cave.PitProfile;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.monsters.*;

import java.util.Collections;
import java.util.List;

/**
 * Runtime holder for all monster-domain game data — races, bases, pain messages, summons, blow
 * methods and effects, spell types, pit profiles, lore, and the visuals cycler/flicker tables —
 * together with the derived {@code *Max} counters and the name/index lookups the running game
 * queries against.
 *
 * <p>This is the read side of the monster slice: it is populated once at startup by
 * {@link uk.co.jackoftradesltd.middle.game.globals.loaders.MonsterDataLoader} (driven from
 * {@code GameConstants.init()} in dependency order) and thereafter only read. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor, so the monster data
 * has a single cohesive home rather than living among every other data type.
 *
 * <p>Accepted deviation from C, decided 261009 "for the time being": indexing and counters. C builds
 * each of these tables as an array with one spare slot, and the matching {@code z_info->*_max} counter
 * includes it. The spare slot is slot 0 for the pain messages ({@code pain_messages}, whose first record
 * is {@code type:1}) and the blow methods ({@code blow_methods}, searched from slot 1 by {@code findmeth}).
 * It is the last slot for the races ({@code r_info}, where slot 0 is the real {@code <player>} record),
 * the blow effects ({@code blow_effects}), the pit profiles ({@code pit_info}) and the summons
 * ({@code summons}). Every list here is base 0, holds only real records, and has a counter equal to its
 * size. So each {@code *Max} counter is one lower than C's, nothing is stored for the blank slot, and a
 * lookup of the blank slot's index finds nothing. Nothing outside this class reads the counters yet. Do not
 * use one as a loop bound copied from C without allowing for that.
 *
 * <p>Class MonsterRegistry coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class MonsterRegistry {
    /**
     * Logger for the fatal "accessed before it was loaded" guards in the lookups.
     *
     * <p>Field logger coded before 261009, commented in full on 261009.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The loaded monster races in {@code monster.txt} order, the list {@link #lookupMonsterRace} scans. Element 0
     * is the real {@code <player>} record, as {@code r_info[0]} is in C; C's blank slot after the last race is not
     * held (see the class block). Null until {@link #setMonsterRaces} runs.
     *
     * <p>Field monsterRaces coded before 261009, commented in full on 261009.
     */
    public static List<MonsterRace> monsterRaces;
    /**
     * The number of loaded races. C's {@code z_info->r_max}, set in {@code finish_parse_monster} ({@code mon-init.c}),
     * is one higher because it counts the blank slot after the last race; this is the plain list size (see the class
     * block).
     *
     * <p>Field monsterRaceMax coded before 261009, commented in full on 261009.
     */
    public static int monsterRaceMax;
    /**
     * The number of loaded pain records. C's {@code z_info->mp_max}, set in {@code finish_parse_pain}
     * ({@code mon-init.c}), is the highest pain index plus one, because slot 0 is blank. This is the plain list size,
     * which is the highest index only while the indices run 1 to n without a gap, as the shipped twelve do.
     *
     * <p>Field monsterPainMsgMax coded before 261009, commented in full on 261009.
     */
    public static int monsterPainMsgMax;
    /**
     * The number of loaded pit profiles. C's {@code z_info->pit_max}, set in {@code finish_parse_pit}
     * ({@code mon-init.c}), is one higher because it counts the blank slot after the last profile; this is the plain
     * list size.
     *
     * <p>Field monsterPitTypeMax coded before 261009, commented in full on 261009.
     */
    private static int monsterPitTypeMax;
    /**
     * The largest number of blows any loaded race has. It is C's {@code z_info->mon_blows_max}, which
     * {@code finish_parse_monster} ({@code mon-init.c}) takes as the longest blow list over all races, and unlike the
     * other counters it is the same value in C and Java.
     *
     * <p>Field monsterBlowsMax coded before 261009, commented in full on 261009.
     */
    private static int monsterBlowsMax;
    /**
     * The number of loaded blow methods, set by {@link #setBlowMethods}. C's {@code z_info->blow_methods_max} is one
     * higher because slot 0 of {@code blow_methods} is blank (see the class block).
     *
     * <p>Field monsterBlowsMethodsMax coded before 261009, commented in full on 261009.
     */
    private static int monsterBlowsMethodsMax;
    /**
     * The number of loaded blow effects, set by {@link #setBlowEffects}. C's {@code z_info->blow_effects_max} is one
     * higher because it counts the blank slot after the last effect (see the class block).
     *
     * <p>Field monsterBlowsEffectsMax coded before 261009, commented in full on 261009.
     */
    private static int monsterBlowsEffectsMax;
    /**
     * The loaded pain records, in file order. C holds them in {@code pain_messages}, indexed directly by pain
     * index with slot 0 blank; here the position in the list is not the pain index, so a record is found by
     * comparing {@code getPainIndex()}. Null until {@link #setMonsterPains} runs.
     *
     * <p>Field monsterPains coded before 261009, commented in full on 261009.
     */
    private static List<MonsterPain> monsterPains;
    /**
     * The loaded monster bases, the templates races reference. C keeps them in the linked list {@code rb_info};
     * {@link #lookupMonsterBase} walks this list instead. Null until {@link #setMonsterBases} runs.
     *
     * <p>Field monsterBases coded before 261009, commented in full on 261009.
     */
    private static List<MonsterBase> monsterBases;
    /**
     * The loaded summon specifications. C holds them in the {@code summons} array of {@code mon-summon.c}, found
     * by index through {@code summon_name_to_idx}; {@link #lookupSummon} returns the record itself instead. Null
     * until {@link #setSummons} runs.
     *
     * <p>Field summons coded before 261009, commented in full on 261009.
     */
    private static List<Summon> summons;
    /**
     * The loaded blow methods, which say how a monster blow is delivered. C holds them in {@code blow_methods}, with
     * slot 0 blank and the real records from slot 1. Null until {@link #setBlowMethods} runs.
     *
     * <p>Field blowMethods coded before 261009, commented in full on 261009.
     */
    private static List<BlowMethod> blowMethods;
    /**
     * The loaded blow effects, which say what a monster blow does. C holds them in {@code blow_effects}, with the
     * first real effect, {@code NONE}, in slot 0. Null until {@link #setBlowEffects} runs.
     *
     * <p>Field blowEffects coded before 261009, commented in full on 261009.
     */
    private static List<BlowEffect> blowEffects;
    /**
     * The loaded monster spell types. It is stored by {@link #setMonsterSpellTypes} and this class has no accessor
     * for it.
     *
     * <p>Field monsterSpellTypes coded before 261009, commented in full on 261009.
     */
    private static List<MonsterSpellType> monsterSpellTypes;
    /**
     * The loaded pit and nest profiles used by level generation. C holds them in {@code pit_info}. It is stored by
     * {@link #setMonsterPitProfiles} and this class has no accessor for it.
     *
     * <p>Field monsterPitProfiles coded before 261009, commented in full on 261009.
     */
    private static List<PitProfile> monsterPitProfiles;
    /**
     * Monster lore. Nothing in this class assigns it or reads it, so it is always null here.
     *
     * <p>Field monsterLore coded before 261009, commented in full on 261009.
     */
    private static List<MonsterLore> monsterLore;
    /**
     * The colour-cycling table for animated monster colours. Null until {@link #setVisualsCyclerTable} runs.
     *
     * <p>Field visualsCyclerTable coded before 261009, commented in full on 261009.
     */
    public static VisualsCycler visualsCyclerTable = null;
    /**
     * The colour-flicker table for flickering monster colours. Null until {@link #setVisualsFlickerTable} runs.
     *
     * <p>Field visualsFlickerTable coded before 261009, commented in full on 261009.
     */
    public static FlickerTable visualsFlickerTable = null;

    /**
     * Stores the colour-cycling (visuals) table; called once by {@code MonsterDataLoader}.
     *
     * <p>Function setVisualsCyclerTable coded before 261009, commented in full on 261009.
     *
     * @param visualsCyclerTable the table to store
     */
    public static void setVisualsCyclerTable(VisualsCycler visualsCyclerTable) {
        MonsterRegistry.visualsCyclerTable = visualsCyclerTable;
    }

    /**
     * Stores the colour-flicker table; called once by {@code MonsterDataLoader}.
     *
     * <p>Function setVisualsFlickerTable coded before 261009, commented in full on 261009.
     *
     * @param visualsFlickerTable the table to store
     */
    public static void setVisualsFlickerTable(FlickerTable visualsFlickerTable) {
        MonsterRegistry.visualsFlickerTable = visualsFlickerTable;
    }

    /**
     * Stores the pain records and sets {@code monsterPainMsgMax} to the list size. This stands in for
     * {@code finish_parse_pain} in {@code mon-init.c}, which also sizes {@code pain_messages} and files each record
     * under its own index; the list here is left in the order it was given. Called once by {@code MonsterDataLoader}.
     *
     * <p>Function setMonsterPains coded before 261009, commented in full on 261009.
     *
     * @param monsterPains the pain records, in file order
     */
    public static void setMonsterPains(List<MonsterPain> monsterPains) {
        MonsterRegistry.monsterPains = monsterPains;
        monsterPainMsgMax = monsterPains.size();
    }

    /**
     * Stores the monster bases that {@link #lookupMonsterBase} searches; called once by {@code MonsterDataLoader}.
     *
     * <p>Function setMonsterBases coded before 261009, commented in full on 261009.
     *
     * @param monsterBases the bases to store
     */
    public static void setMonsterBases(List<MonsterBase> monsterBases) {
        MonsterRegistry.monsterBases = monsterBases;
    }

    /**
     * Stores the summon records that {@link #lookupSummon} searches. No count is kept, where C's {@code summon_max}
     * is the record count plus one. Called once by {@code MonsterDataLoader}.
     *
     * <p>Function setSummons coded before 261009, commented in full on 261009.
     *
     * @param summons the summon records, or {@code null} to leave the registry unloaded
     */
    public static void setSummons(@Nullable List<Summon> summons) {
        MonsterRegistry.summons = summons;
    }

    /**
     * Stores the blow methods and sets {@code monsterBlowsMethodsMax} to the list size. This stands in for
     * {@code finish_parse_meth} in {@code mon-init.c}, whose {@code blow_methods_max} is one higher because slot 0
     * is blank (see the class block).
     *
     * <p>Function setBlowMethods coded before 261009, commented in full on 261009.
     *
     * @param methods the blow methods, in file order
     */
    public static void setBlowMethods(List<BlowMethod> methods) {
        MonsterRegistry.blowMethods = methods;
        monsterBlowsMethodsMax = methods.size();
    }

    /**
     * Stores the blow effects and sets {@code monsterBlowsEffectsMax} to the list size. This stands in for
     * {@code finish_parse_eff} in {@code mon-init.c}, whose {@code blow_effects_max} is one higher because it counts
     * the blank slot after the last effect (see the class block).
     *
     * <p>Function setBlowEffects coded before 261009, commented in full on 261009.
     *
     * @param blowEffects the blow effects, in file order
     */
    public static void setBlowEffects(List<BlowEffect> blowEffects) {
        MonsterRegistry.blowEffects = blowEffects;        
        monsterBlowsEffectsMax = blowEffects.size();
    }

    /**
     * Gives read access to the race list without letting the caller change it. Element 0 is the {@code <player>}
     * record.
     *
     * <p>Function getMonsterRaces coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the loaded monster races
     * @throws NullPointerException if the races have not been loaded, since {@link Collections#unmodifiableList}
     *                              rejects {@code null}
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static List<MonsterRace> getMonsterRaces() {
        return Collections.unmodifiableList(monsterRaces);
    }

    /**
     * Stores the race list, sets {@code monsterRaceMax} to its size, and sets {@code monsterBlowsMax} to the longest
     * blow list of any race. The blow count is the same value as the scan in {@code finish_parse_monster}
     * ({@code mon-init.c}) gives for {@code z_info->mon_blows_max}, but {@code monsterRaceMax} is one lower than C's
     * {@code r_max} (see the class block). Called once by {@code MonsterDataLoader}.
     *
     * <p>Function setMonsterRaces coded before 261009, commented in full on 261009.
     *
     * @param monsterRaces the races, in file order and starting with {@code <player>}
     */
    public static void setMonsterRaces(List<MonsterRace> monsterRaces) {
        MonsterRegistry.monsterRaces = monsterRaces;
        monsterRaceMax = monsterRaces.size();
        
        int maxBlowCount = 0;
        for (MonsterRace monsterRace : monsterRaces) {
            if (maxBlowCount < monsterRace.getBlows().size())
                maxBlowCount = monsterRace.getBlows().size();
        }
        
        MonsterRegistry.monsterBlowsMax = maxBlowCount;
    }

    /**
     * Stores the pit and nest profiles and sets {@code monsterPitTypeMax} to the list size. This stands in for
     * {@code finish_parse_pit} in {@code mon-init.c}, whose {@code pit_max} is one higher (see the class block).
     * Called once by {@code MonsterDataLoader}.
     *
     * <p>Function setMonsterPitProfiles coded before 261009, commented in full on 261009.
     *
     * @param monsterPitProfiles the pit profiles, in file order
     */
    public static void setMonsterPitProfiles(List<PitProfile> monsterPitProfiles) {
        MonsterRegistry.monsterPitProfiles = monsterPitProfiles;
        MonsterRegistry.monsterPitTypeMax = monsterPitProfiles.size();
    }

    /**
     * Stores the monster spell types; called once by {@code MonsterDataLoader}. This class has no lookup over them.
     *
     * <p>Function setMonsterSpellTypes coded before 261009, commented in full on 261009.
     *
     * @param monsterSpellTypes the spell types to store
     */
    public static void setMonsterSpellTypes(List<MonsterSpellType> monsterSpellTypes) {
        MonsterRegistry.monsterSpellTypes = monsterSpellTypes;
    }

    /**
     * Find a monster race by name, mirroring C's {@code lookup_monster}: an exact case-insensitive
     * match wins, and failing that the first race whose name <em>contains</em> the query (also
     * case-insensitive) is returned as the closest match. Used to resolve friend and shape references,
     * and by the lore and pit parsers.
     *
     * <p>The scan covers every race, including the {@code <player>} record at element 0, so the query
     * {@code "player"} finds it as C does. A race with a null name is skipped, as C skips a record whose name is
     * {@code NULL}. An empty query matches nothing, because C's {@code my_stristr} ({@code z-util.c}) never reports
     * a match for an empty pattern, where Java's {@code contains("")} is always true; the {@code !name.isEmpty()}
     * test restores that.
     *
     * <p>Function lookupMonsterRace coded before 261009, null-name and empty-query guards added on 261009,
     * commented in full on 261009.
     *
     * @param name the race name to look up
     * @return the exact match, the closest substring match, or {@code null} if neither exists
     * @throws IllegalStateException if the monster races have not been loaded yet
     */
    @Nullable
    public static MonsterRace lookupMonsterRace(@NotNull String name) {
        if (monsterRaces == null) {
            String message = "Invalid attempt to access monsterRaces when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        MonsterRace closest = null;

        for (MonsterRace monsterRace : monsterRaces) {
            if (monsterRace == null) continue;
            if (name.equalsIgnoreCase(monsterRace.getName())) return monsterRace;
            if (closest == null 
                    && monsterRace.getName() != null 
                    && monsterRace.getName().toLowerCase().contains(name.toLowerCase()) && !name.isEmpty())
                closest = monsterRace;
        }

        return closest;
    }

    /**
     * Finds a summon by name, the Java form of {@code summon_name_to_idx} in {@code mon-summon.c}. The first summon
     * whose name equals the argument exactly (case-sensitive) is returned. C returns the summon's index, or -1 for
     * no match, and this returns the record itself, or {@code null}. Because the list is base 0 and has no blank
     * slot, C's scan of the blank last slot has no counterpart here.
     *
     * <p>Function lookupSummon coded before 261009, commented in full on 261009.
     *
     * @param summonName the name of the summon, such as {@code KIN}
     * @return the summon with that name, or {@code null} if none matches
     * @throws IllegalStateException if the summons have not been loaded yet
     */
    @Nullable
    @CheckReturnValue
    public static Summon lookupSummon(String summonName) {
        if (summons == null) {
            String message = "Invalid attempt to access summons when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return summons.stream()
                .filter(s -> s.getName().equals(summonName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a monster base by its code name, the Java form of {@code lookup_monster_base} in {@code mon-util.c}.
     * The first base whose code name equals the argument exactly (case-sensitive) is returned, and there is no
     * substring fallback.
     *
     * <p>Function lookupMonsterBase coded before 261009, commented in full on 261009.
     *
     * @param name the monster base code name
     * @return the matching {@link MonsterBase}, or {@code null} if none matches
     * @throws IllegalStateException if monster bases have not been loaded
     */
    @Nullable
    public static MonsterBase lookupMonsterBase(@NotNull String name) {
        if (monsterBases == null) {
            String message = "Invalid attempt to access monsterBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return monsterBases.stream().filter(b -> name.equals(b.getCodeName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the pain record with a given pain index, the Java form of reading {@code pain_messages[pain_idx]} in
     * {@code parse_mon_base_pain} ({@code mon-init.c}). C accepts any index from 0 to {@code mp_max - 1}; slot 0 is
     * blank, so index 0 yields an empty record there. Here the list holds only real records, so index 0 finds
     * nothing unless a record carries that index (none of the shipped twelve does), and an index below 0 or above
     * the list size also finds nothing. The shipped indices 1 to 12 are all found.
     *
     * <p>Function lookupMonsterPain coded before 261009, lower bound changed from 1 to 0 on 261009, commented in
     * full on 261009.
     *
     * @param monsterType the pain index named by a {@code pain:} line in {@code monster_base.txt}
     * @return the pain record with that index, or {@code null} if none matches
     * @throws IllegalStateException if the pain records have not been loaded yet
     */
    @Nullable
    @Contract(pure = true)
    @CheckReturnValue
    public static MonsterPain lookupMonsterPain(int monsterType) {
        if (monsterPains == null) {
            String message = "Invalid attempt to access monsterPain when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        if (monsterType < 0 || monsterType > monsterPains.size())
            return null;

        return monsterPains.stream()
                .filter(e -> e.getPainIndex() == monsterType)
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a monster blow effect by name, the Java form of {@code findeff} in {@code mon-init.c}. The first effect
     * whose name equals the argument exactly (case-sensitive) is returned. C starts at {@code blow_effects[0]}, the
     * real {@code NONE} effect, and so does this list.
     *
     * <p>Function lookupBlowEffect coded before 261009, commented in full on 261009.
     *
     * @param effectName the blow effect name
     * @return the matching {@link BlowEffect}, or {@code null} if none matches
     * @throws IllegalStateException if blow effects have not been loaded
     */
    @Nullable
    public static BlowEffect lookupBlowEffect(@NotNull String effectName) {
        if (blowEffects == null) {
            String message = "Invalid attempt to access blowEffects when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return blowEffects.stream().filter(b -> effectName.equals(b.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Look up a monster blow method by name, the Java form of {@code findmeth} in {@code mon-init.c}. The first
     * method whose name equals the argument exactly (case-sensitive) is returned. C starts at
     * {@code blow_methods[1]} because slot 0 is blank, and this list has no blank slot, so it starts at element 0
     * (see the class block).
     *
     * <p>Function lookupBlowMethod coded before 261009, commented in full on 261009.
     *
     * @param methodName the blow method name
     * @return the matching {@link BlowMethod}, or {@code null} if none matches
     * @throws IllegalStateException if blow methods have not been loaded
     */
    @Nullable
    public static BlowMethod lookupBlowMethod(@NotNull String methodName) {
        if (blowMethods == null) {
            String message = "Invalid attempt to access blowMethods when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return blowMethods.stream().filter(b -> methodName.equals(b.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Find a monster pain record by its index (linear scan), without the range test that
     * {@link #lookupMonsterPain} applies first. As there, index 0 finds nothing unless a record carries it, where
     * C's {@code pain_messages[0]} is a blank record.
     *
     * <p>Function getPainFromIndex coded before 261009, commented in full on 261009.
     *
     * @param index the pain-record index
     * @return the matching {@link MonsterPain}, or {@code null} if none matches
     * @throws IllegalStateException if the pain records have not been loaded yet
     */
    public static @Nullable MonsterPain getPainFromIndex(int index) {
        if (monsterPains == null) {
            String message = "Invalid attempt to access monsterPain when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        for (MonsterPain monsterPain : monsterPains) {
            if (monsterPain.getPainIndex() == index) {
                return monsterPain;
            }
        }

        return null;
    }

    /**
     * Gives the number of loaded races, which is one lower than C's {@code z_info->r_max} (see the class block).
     *
     * <p>Function getMonsterRaceMax coded before 261009, commented in full on 261009.
     *
     * @return the value of {@code monsterRaceMax}, or 0 before the races are loaded
     */
    public static int getMonsterRaceMax() {
        return monsterRaceMax;
    }

    /**
     * Gives the number of loaded pain records, which is one lower than C's {@code z_info->mp_max} (see the class
     * block).
     *
     * <p>Function getMonsterPainMsgMax coded before 261009, commented in full on 261009.
     *
     * @return the value of {@code monsterPainMsgMax}, or 0 before the pain records are loaded
     */
    public static int getMonsterPainMsgMax() {
        return monsterPainMsgMax;
    }

    /**
     * Gives the number of loaded pit profiles, which is one lower than C's {@code z_info->pit_max} (see the class
     * block).
     *
     * <p>Function getMonsterPitTypeMax coded before 261009, commented in full on 261009.
     *
     * @return the value of {@code monsterPitTypeMax}, or 0 before the pit profiles are loaded
     */
    public static int getMonsterPitTypeMax() {
        return monsterPitTypeMax;
    }

    /**
     * Gives the longest blow list of any race, the same value as C's {@code z_info->mon_blows_max}.
     *
     * <p>Function getMonsterBlowsMax coded before 261009, commented in full on 261009.
     *
     * @return the value of {@code monsterBlowsMax}, or 0 before the races are loaded
     */
    public static int getMonsterBlowsMax() {
        return monsterBlowsMax;
    }

    /**
     * Gives the number of loaded blow methods, which is one lower than C's {@code z_info->blow_methods_max} (see the
     * class block).
     *
     * <p>Function getMonsterBlowsMethodsMax coded before 261009, commented in full on 261009.
     *
     * @return the value of {@code monsterBlowsMethodsMax}, or 0 before the blow methods are loaded
     */
    public static int getMonsterBlowsMethodsMax() {
        return monsterBlowsMethodsMax;
    }

    /**
     * Gives the number of loaded blow effects, which is one lower than C's {@code z_info->blow_effects_max} (see the
     * class block).
     *
     * <p>Function getMonsterBlowsEffectsMax coded before 261009, commented in full on 261009.
     *
     * @return the value of {@code monsterBlowsEffectsMax}, or 0 before the blow effects are loaded
     */
    public static int getMonsterBlowsEffectsMax() {
        return monsterBlowsEffectsMax;
    }

    /**
     * Find a monster base by its code name (linear scan). It only calls {@link #lookupMonsterBase}.
     *
     * <p>Function getBaseFromName coded before 261009, commented in full on 261009.
     *
     * @param name the monster base code name
     * @return the matching {@link MonsterBase}, or {@code null} if none matches
     */
    public static @Nullable MonsterBase getBaseFromName(String name) {
        return lookupMonsterBase(name);
    }

    /**
     * Gives the colour-cycling table.
     *
     * <p>Function getVisualsCyclerTable coded before 261009, commented in full on 261009.
     *
     * @return the loaded colour-cycling table, or {@code null} before it is loaded
     */
    public static VisualsCycler getVisualsCyclerTable() {
        return visualsCyclerTable;
    }

}
