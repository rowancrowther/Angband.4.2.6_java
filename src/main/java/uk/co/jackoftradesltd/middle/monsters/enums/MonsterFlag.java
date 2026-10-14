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

package uk.co.jackoftradesltd.middle.monsters.enums;

/**
 * The transient per-monster status flags (in view, active, visible, aware, handled this turn,
 * tracking, …): the Java form of C's {@code MFLAG_*} constants, which C builds from the rows of
 * {@code list-mon-temp-flags.h} in the anonymous enum of {@code monster.h}. A monster carries them
 * in {@code mflag}, a bit array of {@code MFLAG_SIZE} bits, and the Java {@code Monster} holds the
 * same set as a {@code Flag<MonsterFlag>}.
 *
 * <p>All twelve entries (the eleven flags of the header and the closing {@code MAX}) were compared
 * with the header this pass and agree in name, order and description. {@link #MFLAG_NONE} is the
 * header's own first row, so an ordinal here is the same number as C's index for the flag.
 *
 * <p>Where this differs from C:
 * <ul>
 *   <li>C's header gives each row a description, but the {@code MFLAG(a, b)} macro in
 *   {@code monster.h} throws the second argument away, so C never holds the text at run time. Java
 *   keeps it in {@link #description}.</li>
 *   <li>{@link #MFLAG_MAX} is a count, not a flag. C uses it only to size the array
 *   ({@code MFLAG_SIZE}) and to write that size into the savefile; Java's {@code Flag} takes its
 *   size from the enum, so nothing reads it.</li>
 * </ul>
 *
 * <p>The description is private and has no getter, so nothing outside this enum can read it.
 *
 * <p>C calls these flags temporary, but {@code save.c} still writes the monster's {@code mflag}
 * bytes and {@code load.c} reads them back, refusing a savefile whose flag array is larger than
 * {@code MFLAG_SIZE}.
 *
 * <p>Enum MonsterFlag coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public enum MonsterFlag {
    /**
     * C's {@code MFLAG_NONE}: the placeholder first row, with an empty description. It is never set
     * on a monster and exists so that the real flags start at index 1.
     *
     * <p>Constant MFLAG_NONE coded before 261014, commented in full on 261014.
     */
    MFLAG_NONE(""),
    /**
     * C's {@code MFLAG_VIEW}: the monster is in the player's line of sight. {@code update_mon} in
     * {@code mon-util.c} sets and clears it, and {@code monster_is_in_view} in
     * {@code mon-predicate.c} reads it.
     *
     * <p>Constant MFLAG_VIEW coded before 261014, commented in full on 261014.
     */
    MFLAG_VIEW("Monster is in line of sight"),
    /**
     * C's {@code MFLAG_ACTIVE}: the monster is in active mode. {@code monster_check_active} in
     * {@code mon-move.c} sets it when the monster can reach, see, hear or smell the player, is hurt
     * or is taking terrain damage, and clears it otherwise. {@code process_monster_timed} in the same
     * file and {@code group_monster_tracking} in {@code mon-group.c} read it.
     *
     * <p>Constant MFLAG_ACTIVE coded before 261014, commented in full on 261014.
     */
    MFLAG_ACTIVE("Monster is in active mode"),
    /**
     * C's {@code MFLAG_NICE}: the monster waits for the player and will not attack or cast. Set by
     * {@code place_new_monster_one} in {@code mon-make.c} for a race with {@code RF_FORCE_SLEEP},
     * tested by {@code monster_can_cast} in {@code mon-attack.c} and cleared for every monster by
     * {@code process_player_cleanup} in {@code game-world.c}.
     *
     * <p>Constant MFLAG_NICE coded before 261014, commented in full on 261014.
     */
    MFLAG_NICE("Monster is still being nice"),
    /**
     * C's {@code MFLAG_SHOW}: the monster was memorized this turn, by a detection effect, so its
     * mark is not yet to be dropped. {@code detect_monsters} in {@code effect-handler-general.c}
     * sets it together with {@link #MFLAG_MARK}, and {@code process_player_cleanup} in
     * {@code game-world.c} clears it for every monster.
     *
     * <p>Constant MFLAG_SHOW coded before 261014, commented in full on 261014.
     */
    MFLAG_SHOW("Monster is recently memorized"),
    /**
     * C's {@code MFLAG_MARK}: the monster is currently memorized, so it is displayed even when out
     * of sight. {@code detect_monsters} sets it, {@code process_player_cleanup} in
     * {@code game-world.c} clears it for a monster that is marked but not {@link #MFLAG_SHOW}n, and
     * {@code update_mon} in {@code mon-util.c} and the {@code READ_MINDS} effect handler read it.
     *
     * <p>Constant MFLAG_MARK coded before 261014, commented in full on 261014.
     */
    MFLAG_MARK("Monster is currently memorized"),
    /**
     * C's {@code MFLAG_VISIBLE}: the player can see the monster. {@code update_mon} in
     * {@code mon-util.c} sets and clears it, and {@code monster_is_visible} in
     * {@code mon-predicate.c} reads it. In Java, setting or clearing it through {@code Monster}
     * also refreshes the cached view of the monster.
     *
     * <p>Constant MFLAG_VISIBLE coded before 261014, commented in full on 261014.
     */
    MFLAG_VISIBLE("Monster is \"visible\""),
    /**
     * C's {@code MFLAG_CAMOUFLAGE}: the player does not know this is a monster, as with a mimic.
     * {@code place_new_monster_one} in {@code mon-make.c} sets it for a race with {@code RF_UNAWARE}
     * and clears it for any other, {@code become_aware} in {@code mon-util.c} clears it when the
     * player discovers the monster, and {@code monster_is_camouflaged} in {@code mon-predicate.c}
     * reads it.
     *
     * <p>Constant MFLAG_CAMOUFLAGE coded before 261014, commented in full on 261014.
     */
    MFLAG_CAMOUFLAGE("Player doesn't know this is a monster"),
    /**
     * C's {@code MFLAG_AWARE}: the monster has noticed the player. {@code monster_wake} in
     * {@code mon-util.c} sets it, and so does {@code process_monster_timed} in {@code mon-move.c}
     * for an awake, active monster, one turn in ten. {@code monster_group_rouse} in
     * {@code mon-group.c} returns early for a monster that lacks it.
     *
     * <p>Constant MFLAG_AWARE coded before 261014, commented in full on 261014.
     */
    MFLAG_AWARE("Monster is aware of the player"),
    /**
     * C's {@code MFLAG_HANDLED}: the monster has already been processed this game turn.
     * {@code process_monsters} in {@code mon-move.c} skips a monster that has it, sets it before
     * the monster moves, and {@code reset_monsters} clears it for all of them afterwards.
     *
     * <p>Constant MFLAG_HANDLED coded before 261014, commented in full on 261014.
     */
    MFLAG_HANDLED("Monster has been processed this turn"),
    /**
     * C's {@code MFLAG_TRACKING}: the monster is following the player by sound or scent. It is set
     * and cleared by {@code get_move} in {@code mon-move.c}, and {@code group_monster_tracking} in
     * {@code mon-group.c} looks for a group member that is both tracking and {@link #MFLAG_ACTIVE}.
     *
     * <p>Constant MFLAG_TRACKING coded before 261014, commented in full on 261014.
     */
    MFLAG_TRACKING("Monster is tracking the player by sound or scent"),
    /**
     * C's {@code MFLAG_MAX}: the number of flags, not a flag. C uses it for {@code MFLAG_SIZE} and
     * never sets it on a monster, and nothing in Java reads it.
     *
     * <p>Constant MFLAG_MAX coded before 261014, commented in full on 261014.
     */
    MFLAG_MAX("");

    /**
     * Human-readable description of what this flag means. This is the second column of the
     * {@code MFLAG()} rows in {@code list-mon-temp-flags.h}, which C discards when it builds the
     * enum, so the field has no C counterpart. It has no getter.
     *
     * <p>Field description coded before 261014, commented in full on 261014.
     */
    private final String description;

    /**
     * Bind a monster flag to its description.
     *
     * <p>Each constant passes the second column of its row in {@code list-mon-temp-flags.h}, with
     * {@code MFLAG_NONE} and {@code MFLAG_MAX} taking the empty string.
     *
     * <p>Constructor MonsterFlag coded before 261014, commented in full on 261014.
     *
     * @param description the flag's description
     */
    MonsterFlag(String description) {
        this.description = description;
    }
}