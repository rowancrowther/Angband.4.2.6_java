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

package uk.co.jackoftradesltd.middle.player;

import uk.co.jackoftradesltd.middle.enums.Stats;

import java.util.HashMap;
import java.util.Map;

/**
 * A snapshot of a character mid-roll, kept so the birth screen can step back to it - the port of C's
 * {@code struct birther} ({@code player-birth.c:93-109}). C keeps two static instances of it,
 * {@code prev} and {@code quickstart_prev}, for the "flick between two rolls" undo and for restoring a
 * save file's character at the quickstart screen; neither static instance is ported yet - only the
 * struct shape and {@link PlayerBirth#saveRollerData}, the method that fills one, exist so far.
 *
 * <p>Fields correspond one-for-one with C's, with the renames Java forces: {@code class} becomes
 * {@link #playerClass} (a reserved word), and {@code wt}/{@code ht} become {@link #weight}/
 * {@link #height} to match {@link Player}'s own naming. {@link #race} and {@link #playerClass} stay
 * bare references to the registry's shared race and class records, exactly as C's
 * {@code const struct player_race *}/{@code const struct player_class *} do -
 * {@link PlayerBirth#saveRollerData} assigns {@link Player#getRace()} and
 * {@link Player#getPlayerClass()} directly, with no defensive copy.
 *
 * <p>C's fixed {@code int16_t stat[STAT_MAX]} is a {@link Map} here, keyed the same way
 * {@link PlayerBirth#getStats} and {@link Player#getStatBirth} key theirs - {@link #stat} only ever
 * holds meaningful entries for the five real stats, never {@code STAT_NONE} or {@code STAT_MAX}, though
 * nothing in this class enforces that itself. C's {@code char *history} and fixed
 * {@code char name[PLAYER_NAME_LEN]} are both plain {@link String}s, needing no length cap and no
 * manual allocation - see {@link Player#setHistoryBirth} for the {@code string_free}/{@code my_strcpy}
 * bookkeeping the port's garbage collector takes over instead.
 *
 * <p>C's {@code int16_t age}/{@code wt}/{@code ht}/{@code sc} widen to {@code int}, and
 * {@code int32_t au} widens to {@code long} - the same widening {@link Player} already uses for the
 * corresponding fields; see {@link Player#getAUBirth()} for why the {@code long} in particular.
 *
 * <p>Class Birther coded on 260906 / commented in full on 260906.
 *
 * @author Rowan Crowther
 */
public class Birther {
    /**
     * The race this snapshot was rolled with - the port of C's {@code birther.race}
     * ({@code player-birth.c:95}).
     */
    private PlayerRace race;

    /**
     * The class this snapshot was rolled with - the port of C's {@code birther.class}
     * ({@code player-birth.c:96}), renamed since {@code class} is a reserved word in Java.
     */
    private PlayerClass playerClass;

    /**
     * The age this snapshot was rolled with - the port of C's {@code birther.age}
     * ({@code player-birth.c:98}).
     */
    private int age;

    /**
     * The weight this snapshot was rolled with - the port of C's {@code birther.wt}
     * ({@code player-birth.c:99}).
     */
    private int weight;

    /**
     * The height this snapshot was rolled with - the port of C's {@code birther.ht}
     * ({@code player-birth.c:100}).
     */
    private int height;

    /**
     * This snapshot's social-class score - the port of C's {@code birther.sc}
     * ({@code player-birth.c:101}). Vestigial on both sides; see {@link #getSc()}.
     */
    private int sc;

    /**
     * The gold this snapshot was rolled with - the port of C's {@code birther.au}
     * ({@code player-birth.c:103}).
     */
    private long au;

    /**
     * The stats this snapshot was rolled with, one entry per real stat - the port of C's fixed
     * {@code birther.stat[STAT_MAX]} array ({@code player-birth.c:105}).
     */
    private Map<Stats, Integer> stat;

    /**
     * The background text this snapshot was rolled with - the port of C's {@code birther.history}
     * ({@code player-birth.c:107}).
     */
    private String history;

    /**
     * The name this snapshot was rolled with - the port of C's fixed
     * {@code birther.name[PLAYER_NAME_LEN]} buffer ({@code player-birth.c:108}).
     */
    private String name;

    /**
     * Creates an empty snapshot with nothing rolled yet - the port of declaring a {@code birther}
     * instance ({@code player-birth.c:93-109}).
     *
     * <p>C leaves a fresh struct's fields as whatever memory already held, except where an instance is
     * a zero-initialised {@code static} ({@code prev}, {@code quickstart_prev}); the port always starts
     * {@link #stat} as an empty map and every other field at Java's own default ({@code null} or zero),
     * regardless of which C behaviour a given call site would otherwise have relied on. No caller of
     * this constructor exists yet beyond the test suite, so the difference has nothing to trip over so
     * far.
     *
     * <p>Constructor Birther coded on 260906 / commented in full on 260906.
     */
    public Birther() {
        stat = new HashMap<>();
    }

    /**
     * Returns the race this snapshot was rolled with - the port of reading C's {@code birther.race}
     * ({@code player-birth.c:95}).
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, and it copies
     * {@link Player#getRace()} straight across with no defensive copy - the same reference the live
     * player holds, matching C's pointer-copy {@code tosave->race = player->race}
     * ({@code player-birth.c:152}).
     *
     * <p>Function getRace commented in full on 260906.
     *
     * @return the snapshotted race, or {@code null} before it has been set
     */
    public PlayerRace getRace() {
        return race;
    }

    /**
     * Sets the race this snapshot was rolled with - the port of writing C's {@code birther.race}
     * ({@code player-birth.c:95}). See {@link #getRace()} for the one write site and why the reference
     * is shared rather than copied.
     *
     * <p>Function setRace commented in full on 260906.
     *
     * @param race the race to snapshot, normally the live player's own
     */
    public void setRace(PlayerRace race) {
        this.race = race;
    }

    /**
     * Returns the class this snapshot was rolled with - the port of reading C's {@code birther.class}
     * ({@code player-birth.c:96}). Named {@code playerClass} rather than {@code class}, a reserved word
     * in Java.
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, and it copies
     * {@link Player#getPlayerClass()} straight across with no defensive copy, matching C's pointer-copy
     * {@code tosave->class = player->class} ({@code player-birth.c:153}).
     *
     * <p>Function getPlayerClass commented in full on 260906.
     *
     * @return the snapshotted class, or {@code null} before it has been set
     */
    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    /**
     * Sets the class this snapshot was rolled with - the port of writing C's {@code birther.class}
     * ({@code player-birth.c:96}). See {@link #getPlayerClass()} for the one write site and the reason
     * for the field's name.
     *
     * <p>Function setPlayerClass commented in full on 260906.
     *
     * @param playerClass the class to snapshot, normally the live player's own
     */
    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    /**
     * Returns the age this snapshot was rolled with - the port of reading C's {@code birther.age}
     * ({@code player-birth.c:98}), stored there as {@code int16_t} and here as {@code int}; see
     * {@link Player#setWeight} for why the wider type costs nothing for a value this small.
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, copying
     * {@link Player#getAge()} straight across, matching C's {@code tosave->age = player->age}
     * ({@code player-birth.c:154}).
     *
     * <p>Function getAge commented in full on 260906.
     *
     * @return the snapshotted age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age this snapshot was rolled with - the port of writing C's {@code birther.age}
     * ({@code player-birth.c:98}). See {@link #getAge()} for the one write site.
     *
     * <p>Function setAge commented in full on 260906.
     *
     * @param age the age in years to snapshot
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns the weight this snapshot was rolled with - the port of reading C's {@code birther.wt}
     * ({@code player-birth.c:99}). Named {@code weight} to match {@link Player#getWeightBirth}, the
     * field it is copied from, rather than keeping C's abbreviation.
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, and reads the birth copy, not
     * the live weight - {@link Player#getWeightBirth()} - matching C's
     * {@code tosave->wt = player->wt_birth} ({@code player-birth.c:155}).
     *
     * <p>Function getWeight commented in full on 260906.
     *
     * @return the snapshotted weight in pounds
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight this snapshot was rolled with - the port of writing C's {@code birther.wt}
     * ({@code player-birth.c:99}). See {@link #getWeight()} for the one write site and the field's
     * name.
     *
     * <p>Function setWeight commented in full on 260906.
     *
     * @param weight the weight in pounds to snapshot
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Returns the height this snapshot was rolled with - the port of reading C's {@code birther.ht}
     * ({@code player-birth.c:100}). Named {@code height} to match {@link Player#getHeightBirth}, the
     * field it is copied from, rather than keeping C's abbreviation.
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, and reads the birth copy, not
     * the live height - {@link Player#getHeightBirth()} - matching C's
     * {@code tosave->ht = player->ht_birth} ({@code player-birth.c:156}).
     *
     * <p>Function getHeight commented in full on 260906.
     *
     * @return the snapshotted height in inches
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height this snapshot was rolled with - the port of writing C's {@code birther.ht}
     * ({@code player-birth.c:100}). See {@link #getHeight()} for the one write site and the field's
     * name.
     *
     * <p>Function setHeight commented in full on 260906.
     *
     * @param height the height in inches to snapshot
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns this snapshot's social-class score - the port of reading C's {@code birther.sc}
     * ({@code player-birth.c:101}).
     *
     * <p>Vestigial on both sides: neither {@code save_roller_data} nor {@code load_roller_data} reads
     * or writes {@code sc} anywhere in this version of the file, so nothing ever gives it a value, in C
     * or in the port. {@link PlayerBirth#saveRollerData} carries the same gap - the field is simply
     * never touched.
     *
     * <p>Function getSc commented in full on 260906.
     *
     * @return the snapshotted social class; always the type's default until something writes one
     */
    public int getSc() {
        return sc;
    }

    /**
     * Sets this snapshot's social-class score - the port of writing C's {@code birther.sc}
     * ({@code player-birth.c:101}). See {@link #getSc()} for why nothing calls this yet either.
     *
     * <p>Function setSc commented in full on 260906.
     *
     * @param sc the social class to snapshot
     */
    public void setSc(int sc) {
        this.sc = sc;
    }

    /**
     * Returns the gold this snapshot was rolled with - the port of reading C's {@code birther.au}
     * ({@code player-birth.c:103}), stored there as {@code int32_t} and here as {@code long}; see
     * {@link Player#getAUBirth()} for why the widening happens at this boundary rather than on
     * {@link Player}'s own field.
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, and reads the birth copy, not
     * the live purse - {@link Player#getAUBirth()} - matching C's {@code tosave->au = player->au_birth}
     * ({@code player-birth.c:157}).
     *
     * <p>Function getAu commented in full on 260906.
     *
     * @return the snapshotted gold in gold pieces
     */
    public long getAu() {
        return au;
    }

    /**
     * Sets the gold this snapshot was rolled with - the port of writing C's {@code birther.au}
     * ({@code player-birth.c:103}). See {@link #getAu()} for the one write site and the widened type.
     *
     * <p>Function setAu commented in full on 260906.
     *
     * @param au the gold in gold pieces to snapshot
     */
    public void setAu(long au) {
        this.au = au;
    }

    /**
     * Returns the whole map of stats this snapshot was rolled with - the port of reading C's fixed
     * {@code birther.stat[STAT_MAX]} array ({@code player-birth.c:105}) as a single object rather than
     * one index at a time.
     *
     * <p>Holds an entry for each of the five real stats once {@link #setStat} has been called for it;
     * nothing populates the two sentinels {@code STAT_NONE}/{@code STAT_MAX}, and nothing in this class
     * stops a caller from doing so, unlike C's array, which is sized to hold only the real stats and
     * could never be indexed by a sentinel in the first place. The caller owns the returned map;
     * nothing here defends against outside mutation.
     *
     * <p>Function getStat commented in full on 260906.
     *
     * @return the stat map, one entry per stat that has been set
     */
    public Map<Stats, Integer> getStat() {
        return stat;
    }

    /**
     * Records one stat's value in this snapshot - the port of writing one element of C's
     * {@code birther.stat[STAT_MAX]} array ({@code player-birth.c:105}), {@code birther.stat[i] =
     * value}.
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, and calls this once per real
     * stat in {@code Stats.values()} order, skipping {@code STAT_NONE}/{@code STAT_MAX} - matching C's
     * {@code for (i = 0; i < STAT_MAX; i++) tosave->stat[i] = player->stat_birth[i]}
     * ({@code player-birth.c:159-160}).
     *
     * <p>Function setStat commented in full on 260906.
     *
     * @param stat  the stat to record; the caller is expected to pass one of the five real stats,
     *              never {@code STAT_NONE} or {@code STAT_MAX}
     * @param value the value that stat was rolled with
     */
    public void setStat(Stats stat, int value) {
        this.stat.put(stat, value);
    }

    /**
     * Returns the background text this snapshot was rolled with - the port of reading C's
     * {@code birther.history} ({@code player-birth.c:107}).
     *
     * <p>Named {@code getHistory} rather than {@code getHistoryBirth}, even though its setter is
     * {@link #setHistoryBirth} - the setter's name follows {@link Player#setHistoryBirth}, the field it
     * reads from, while this getter follows the field it stores into, {@link #history}, so the pair
     * does not share a name the way the other snapshot accessors do.
     * {@code PlayerBirthSaveRollerDataTest.HistoryHandoff} pins the pair down for exactly this reason:
     * an earlier version of the port had the read and the null-out that follows it landing on two
     * unrelated fields.
     *
     * <p>Function getHistory commented in full on 260906.
     *
     * @return the snapshotted background text, or {@code null} before it has been set
     */
    public String getHistory() {
        return history;
    }

    /**
     * Sets the background text this snapshot was rolled with - the port of writing C's
     * {@code birther.history} ({@code player-birth.c:107}). See {@link #getHistory()} for why this
     * setter's name does not match its getter's.
     *
     * <p>{@link PlayerBirth#saveRollerData} is the one write site so far, and it hands over
     * {@link Player#getHistoryBirth()} then nulls that same field on the player -
     * {@code toSave.setHistoryBirth(player.getHistoryBirth()); player.setHistoryBirth(null);} -
     * matching C's pointer handoff {@code tosave->history = player->history; player->history = NULL;}
     * ({@code player-birth.c:166-167}). C also frees {@code tosave->history} first if a previous string
     * is already held there ({@code player-birth.c:163-164}); the port has nothing to do at that point,
     * since the old reference is simply replaced rather than leaked.
     *
     * <p>Function setHistoryBirth commented in full on 260906.
     *
     * @param history the background text to snapshot
     */
    public void setHistoryBirth(String history) {
        this.history = history;
    }

    /**
     * Returns the name this snapshot was rolled with - the port of reading C's fixed
     * {@code birther.name[PLAYER_NAME_LEN]} buffer ({@code player-birth.c:108}) as a plain
     * {@link String}, with no length cap to stand in for the fixed buffer.
     *
     * <p>{@code PlayerBirth.saveRollerData} is the one write site so far, and copies
     * {@link Player#getFullName()} straight across, matching C's
     * {@code my_strcpy(tosave->name, player->full_name, sizeof(tosave->name))}
     * ({@code player-birth.c:168}).
     *
     * <p>Function getName commented in full on 260906.
     *
     * @return the snapshotted name, or {@code null} before it has been set
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name this snapshot was rolled with - the port of writing C's fixed
     * {@code birther.name[PLAYER_NAME_LEN]} buffer ({@code player-birth.c:108}). See {@link #getName()}
     * for the one write site.
     *
     * <p>Function setName commented in full on 260906.
     *
     * @param name the name to snapshot
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Produces an independent snapshot holding this one's values - the port of what C's raw struct
     * assignment {@code *prev_player = temp;} does to a whole {@code birther}
     * ({@code player-birth.c:223}). C has no named function for this; a plain struct assignment's
     * compiler-generated memberwise copy is the closest thing to compare against, and this method
     * follows what that memberwise copy actually does to each field rather than applying one copy
     * rule throughout.
     *
     * <p>{@link #race} and {@link #playerClass} are pointer fields in C, so the struct assignment
     * copies the pointer rather than what it points to - the result shares the very same race and
     * class records as the source, exactly as {@link #setRace} and {@link #setPlayerClass} already
     * do elsewhere in this class. {@link #stat}, by contrast, is C's <em>embedded</em>
     * {@code int16_t stat[STAT_MAX]} array, so the struct assignment copies its contents element by
     * element into an independent array; the port copies the map's entries into a fresh
     * {@link HashMap} for the same reason - sharing the reference the way {@link #race} is handled
     * would leave the two snapshots aliasing one mutable map. {@link #age}, {@link #weight},
     * {@link #height}, {@link #sc} and {@link #au} are plain scalar fields, copied by value either
     * side. {@link #history} and {@link #name} are a pointer and a fixed buffer in C, copied by
     * pointer and by byte respectively; the port copies both by reference, which comes out the same
     * as C's byte copy since {@link String} is immutable and neither snapshot can mutate the other's
     * text out from under it afterwards.
     *
     * <p>Outstanding: nothing calls this yet. It exists for the same reason C's raw struct
     * assignment does - to hand a caller's own snapshot the values just displaced from the live
     * player - but the one place that pattern is needed so far,
     * {@link PlayerBirth#LoadRollerData(Birther, Birther)}, copies the individual fields across
     * itself rather than calling this method.
     *
     * <p>Function copy coded on 260906, commented in full on 260907.
     *
     * @return a new snapshot holding this one's values; independent of it except for {@link #race}
     * and {@link #playerClass}, which remain the very references this snapshot held
     */
    public Birther copy() {
        Birther result = new Birther();

        result.race = race;
        result.playerClass = playerClass;
        result.age = age;
        result.weight = weight;
        result.height = height;
        result.sc = sc;
        result.au = au;
        Map<Stats, Integer> temp = new HashMap<>(stat);
        for (Stats statIndex : stat.keySet()) {
            result.stat.put(statIndex, temp.get(statIndex));
        }
        result.history = history;
        result.name = name;
        return result;
    }
}
