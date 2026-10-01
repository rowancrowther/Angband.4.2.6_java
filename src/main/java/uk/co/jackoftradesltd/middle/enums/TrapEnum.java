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

import org.jetbrains.annotations.Contract;

/**
 * The trap flags, mirroring the C original's {@code TRF_*} constants, generated in C from
 * {@code list-trap-flags.h} ({@code trap.h}). Each constant carries the description string from
 * the same header. The declaration order matches C, so the order is the C numbering; a flag set
 * is held as a {@code Flag<TrapEnum>}. {@code TRF_NONE} is a real member here (it is C's flag 0
 * too), and {@code TRF_MAX} is the count sentinel.
 *
 * <p>Enum TrapEnum coded on 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public enum TrapEnum {
    /**
     * No trap appears here
     */
    TRF_NONE(""),

    /**
     * This square/object is trapped by a glyph
     */
    TRF_GLYPH("Is a glyph"),

    /**
     * The player has set a trap here
     */
    TRF_TRAP("Is a player trap"),

    /**
     * This trap is visible
     */
    TRF_VISIBLE("Is visible"),

    /**
     * This trap is invisible - this flag is never used
     */
    TRF_INVISIBLE("Is invisible"), // UNUSED

    /**
     * This trap can be set on a floor
     */
    TRF_FLOOR("Can be set on a floor"),

    /**
     * This trap teleports the player down a level
     */
    TRF_DOWN("Takes the player down a level"),

    /**
     * This trap moves the player
     */
    TRF_PIT("Moves the player onto the trap"),

    /**
     * This trap disappears after being activated
     */
    TRF_ONETIME("Disappears after being activated"),

    /**
     * This trap is magical, if this trap flag is not set then the trap is physical
     */
    TRF_MAGICAL("Has magical activation (absence of this flag means physical)"),

    /**
     * The player can make a saving throw to avoid all effects
     */
    TRF_SAVE_THROW("Allows a save from all effects by standard saving throw"),

    /**
     * The player can avoid effects based on their AC
     */
    TRF_SAVE_ARMOR("Allows a save from all effects due to AC"),

    /**
     * This trap is set on a lock on a door
     */
    TRF_LOCK("Is a door lock"),

    /**
     * This trap's effect occurs a certain time after it is tripped
     */
    TRF_DELAY("Has a delayed effect"),

    /**
     * This trap is a web
     */
    TRF_WEB("Is a web"),

    /**
     * Count sentinel, C's {@code TRF_MAX}; not a real flag. Its description is empty.
     */
    TRF_MAX("");

    /**
     * The text C's {@code list-trap-flags.h} gives the flag, kept word for word (the empty string
     * for {@code TRF_NONE} and {@code TRF_MAX}).
     *
     * <p>Field description coded on 261001, commented in full on 261001.
     */
    private final String description;

    /**
     * Binds each constant to its description.
     *
     * <p>Constructor TrapEnum coded on 261001, commented in full on 261001.
     *
     * @param description the description string from {@code list-trap-flags.h}
     */
    @Contract(pure = true)
    TrapEnum(String description) {
        this.description = description;
    }

    /**
     * Gets the description string for this trap flag, as C's {@code list-trap-flags.h} words it.
     *
     * <p>Method getDescription coded on 261001, commented in full on 261001.
     *
     * @return the description string for this trap flag
     */
    @Contract(pure = true)
    public String getDescription() {
        return description;
    }
}
