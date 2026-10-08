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
 * The categories used to group monster race flags by purpose (obvious property,
 * display, generation, lore note, behaviour, drops, detection, environment
 * alteration, race type, vulnerability/resistance, …).
 *
 * <p>Mirrors the C original's {@code enum monster_flag_type} in {@code monster.h}. Each
 * {@code RF_} flag is tagged with exactly one of these in {@code list-mon-race-flags.h}
 * (the {@code type} member of C's {@code struct monster_flag}); the Java equivalent is the
 * {@code MonsterRaceFlag} constructor argument. The lore code in {@code mon-lore.c} uses the
 * categories to build flag masks via {@code create_mon_flag_mask()}, which selects every flag
 * whose category is in the list it is given.
 *
 * <p>{@link #RFT_NONE} and {@link #RFT_MAX} are the usual enum bookends. In C,
 * {@code create_mon_flag_mask()} also relies on {@code RFT_MAX} as the terminator of its
 * variadic argument list, so it is never a real category.
 *
 * <p>Class MonsterRaceCategory coded before 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public enum MonsterRaceCategory {
    /**
     * Placeholder category, carried only by {@code RF_NONE}.
     *
     * <p>Constant RFT_NONE coded before 261008, commented in full on 261008.
     */
    RFT_NONE,
    /**
     * An obvious property: one the player can see without lore (unique, questor, sex,
     * group AI, name punctuation).
     *
     * <p>Constant RFT_OBV coded before 261008, commented in full on 261008.
     */
    RFT_OBV,
    /**
     * Flags for display purposes (clear character or attribute, random, multi-hued or
     * flickering colour).
     *
     * <p>Constant RFT_DISP coded before 261008, commented in full on 261008.
     */
    RFT_DISP,
    /**
     * Flags related to generation (forced depth, forced sleep, forced extra monsters, seasonal).
     *
     * <p>Constant RFT_GEN coded before 261008, commented in full on 261008.
     */
    RFT_GEN,
    /**
     * Flags especially noteworthy for lore (unaware, multiplies, regenerates).
     *
     * <p>Constant RFT_NOTE coded before 261008, commented in full on 261008.
     */
    RFT_NOTE,
    /**
     * Behaviour-related flags (movement, blows, intelligence, fear).
     *
     * <p>Constant RFT_BEHAV coded before 261008, commented in full on 261008.
     */
    RFT_BEHAV,
    /**
     * Drop details (gold or items only, drop chance and count, quality).
     *
     * <p>Constant RFT_DROP coded before 261008, commented in full on 261008.
     */
    RFT_DROP,
    /**
     * Detection properties (invisible, cold-blooded, empty mind, weird mind).
     *
     * <p>Constant RFT_DET coded before 261008, commented in full on 261008.
     */
    RFT_DET,
    /**
     * Environment shaping: flags for monsters that alter or pass through the map (open or
     * bash doors, pass or kill walls, move or kill bodies, take or kill items, clear or pass
     * webs).
     *
     * <p>Constant RFT_ALTER coded before 261008, commented in full on 261008.
     */
    RFT_ALTER,
    /**
     * Types of monster, as a noun ("an orc", "a dragon").
     *
     * <p>Constant RFT_RACE_N coded before 261008, commented in full on 261008.
     */
    RFT_RACE_N,
    /**
     * Types of monster, as an adjective ("evil", "undead").
     *
     * <p>Constant RFT_RACE_A coded before 261008, commented in full on 261008.
     */
    RFT_RACE_A,
    /**
     * Vulnerabilities with no corresponding resistance flag.
     *
     * <p>Constant RFT_VULN coded before 261008, commented in full on 261008.
     */
    RFT_VULN,
    /**
     * Vulnerabilities that do have a corresponding resistance flag (for example
     * {@code RF_HURT_FIRE}, paired with {@code RF_IM_FIRE}); the lore code selects this
     * together with {@link #RFT_VULN}.
     *
     * <p>Constant RFT_VULN_I coded before 261008, commented in full on 261008.
     */
    RFT_VULN_I,
    /**
     * Elemental resistances (the {@code RF_IM_} flags).
     *
     * <p>Constant RFT_RES coded before 261008, commented in full on 261008.
     */
    RFT_RES,
    /**
     * Immunity from status effects (fear, stun, confusion, sleep, hold, slow).
     *
     * <p>Constant RFT_PROT coded before 261008, commented in full on 261008.
     */
    RFT_PROT,

    /**
     * Sentinel, not a category. Marks the end of the category list, and is the terminator of
     * the variadic argument list taken by C's {@code create_mon_flag_mask()}.
     *
     * <p>Constant RFT_MAX coded before 261008, commented in full on 261008.
     */
    RFT_MAX
}
