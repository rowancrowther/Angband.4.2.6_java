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

package uk.co.jackoftradesltd.middle.enums;

/**
 * One bit of an "enchant item" effect's target: to-hit, to-damage or armour class. An effect's
 * enchant sub-type is a {@code Flag<EffectEnchant>}, a set of these, because C stores it as one
 * {@code int} whose bits {@code effect_handler_ENCHANT} tests one by one: {@code ENCH_TOHIT} is
 * {@code 0x01}, {@code ENCH_TODAM} is {@code 0x02} and {@code ENCH_TOAC} is {@code 0x04}
 * ({@code effect-handler.h}). C's {@code ENCH_TOBOTH} is {@code 0x03}, to-hit and to-damage
 * together, so it has no constant here: the data-file name {@code TOBOTH} turns on both
 * {@link #EE_TOHIT} and {@link #EE_TODAM}.
 *
 * <p>Class EffectEnchant coded before 261125, updated on 261125 for the move from one mode to a
 * set of bits (the {@code EE_TOBOTH} constant went), commented in full on 261125.
 *
 * @author Rowan Crowther
 */
public enum EffectEnchant {
    /**
     * Enchant to-hit: C's {@code ENCH_TOHIT}, {@code 0x01}.
     *
     * <p>Constant EE_TOHIT coded before 261125, commented in full on 261125.
     *
     * @author Rowan Crowther
     */
    EE_TOHIT,
    /**
     * Enchant to-damage: C's {@code ENCH_TODAM}, {@code 0x02}.
     *
     * <p>Constant EE_TODAM coded before 261125, commented in full on 261125.
     *
     * @author Rowan Crowther
     */
    EE_TODAM,
    /**
     * Enchant armour class: C's {@code ENCH_TOAC}, {@code 0x04}.
     *
     * <p>Constant EE_TOAC coded before 261125, commented in full on 261125.
     *
     * @author Rowan Crowther
     */
    EE_TOAC
}
