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

package uk.co.jackoftradesltd.middle.objects.enums;

/**
 * The identity of each kind of chest trap, and through its position the bit that marks the trap in a
 * chest's {@code pval}. The port of the {@code code:} lines of {@code chest_trap.txt} and of the
 * {@code pval} that C's {@code parse_chest_trap_name} ({@code obj-chest.c}) assigns to each
 * {@code struct chest_trap}.
 *
 * <p>C builds the trap list while it reads the file: the first {@code name:} record gets
 * {@code pval} 1 and every later one doubles its predecessor's, so a trap's bit is decided by its
 * position in {@code chest_trap.txt}. This port reverses the dependency. The bit is
 * {@code 1 << ordinal()}, so the declaration order below <em>is</em> the file order, and
 * {@code ChestTrapAssembler} rejects a data file whose {@code code:} lines do not follow it. A
 * chest's pval is the OR of the bits of the traps it carries, which is what {@code pick_chest_traps}
 * builds and {@code chest_trap_name} tests.
 *
 * <p>C stores the code as a string and never reads it again after parsing; the effects, level and
 * messages drive everything at run time. Here it is the key that ties a {@code ChestTrap} to its bit.
 * The constant names are the data-file tokens and so keep C's US spelling ({@code PARALYZE}).
 *
 * <p>Class ChestTrapCode coded before 260815, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public enum ChestTrapCode {
    /**
     * The "locked" entry: a chest that is locked but carries no trap. Bit 1, so a chest pval of
     * exactly 1 is "locked, untrapped" ({@code is_trapped_chest} answers false for it). It must lead
     * the enum because C starts trap selection at {@code chest_traps->next}, skipping it.
     *
     * <p>Constant NO_TRAP coded before 260815, commented in full on 261008.
     */
    NO_TRAP,
    /**
     * The green-gas trap: poisons the character. Bit 2, minimum chest level 1.
     *
     * <p>Constant POISON coded before 260815, commented in full on 261008.
     */
    POISON,
    /**
     * The poison needle that drains strength. Bit 4, minimum chest level 2.
     *
     * <p>Constant LOSE_STR coded before 260815, commented in full on 261008.
     */
    LOSE_STR,
    /**
     * The poison needle that drains constitution. Bit 8, minimum chest level 3.
     *
     * <p>Constant LOSE_CON coded before 260815, commented in full on 261008.
     */
    LOSE_CON,
    /**
     * The summoning runes: a magical trap that summons monsters. Bit 16, minimum chest level 15.
     *
     * <p>Constant SUMMON coded before 260815, commented in full on 261008.
     */
    SUMMON,
    /**
     * The yellow-gas trap that paralyses the character. Bit 32, minimum chest level 19.
     *
     * <p>Constant PARALYZE coded before 260815, commented in full on 261008.
     */
    PARALYZE,
    /**
     * The explosion device: damages the character and destroys the chest's contents. Bit 64,
     * minimum chest level 25.
     *
     * <p>Constant EXPLODE coded before 260815, commented in full on 261008.
     */
    EXPLODE;

    /**
     * The most traps {@code chest_trap.txt} may define, "no more than 14 traps total" in that file's
     * own header. C does not enforce it; it follows from the chest's {@code int16_t pval}, which
     * leaves room for 14 trap bits once the lowest and highest bits are reserved. Seven constants are
     * declared today, so the highest bit in use is 64.
     *
     * <p>Field MAX_TRAPS coded before 260815, commented in full on 261008.
     */
    private final static int MAX_TRAPS = 14;
    /**
     * The bit this trap sets in a chest's {@code pval}: C's {@code chest_trap.pval}. Always
     * {@code 1 << ordinal()}, a distinct power of two starting at 1 for {@link #NO_TRAP}.
     *
     * <p>Field pval coded before 260815, commented in full on 261008.
     */
    private final int pval;

    /**
     * Derives the pval bit from the declaration position, reproducing the doubling C does while it
     * walks {@code chest_trap.txt} (first record 1, each later record twice the one before).
     *
     * <p>Constructor ChestTrapCode coded before 260815, commented in full on 261008.
     */
    ChestTrapCode() {
        this.pval = 1 << ordinal();
    }

    /**
     * The ceiling on the number of traps the data file may define.
     *
     * <p>Function getMaxTraps coded before 260815, commented in full on 261008.
     *
     * @return 14, the limit stated in the header of {@code chest_trap.txt}
     */
    public static int getMaxTraps() {
        return MAX_TRAPS;
    }

    /**
     * The bit that marks this trap in a chest's {@code pval}. A chest carrying several traps has the
     * OR of their bits, so test with {@code (chestPval & code.getPval()) != 0}, as
     * {@code chest_trap_name} does.
     *
     * <p>Function getPval coded before 260815, commented in full on 261008.
     *
     * @return 1 for {@link #NO_TRAP}, then 2, 4, 8 and so on in declaration order
     */
    public int getPval() {
        return pval;
    }
}