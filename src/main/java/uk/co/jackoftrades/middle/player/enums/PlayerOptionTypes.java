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

package uk.co.jackoftrades.middle.player.enums;

/**
 * The categories a {@link PlayerOption} can belong to, controlling how and where an
 * option is presented and how it is treated by birth and scoring.
 *
 * <p>Mirrors the C option-page groupings. The category decides which options menu an
 * entry appears on and whether enabling it has side effects (e.g. flagging a character
 * as having cheated).
 *
 * @author Rowan Crowther
 */
public enum PlayerOptionTypes {
    /**
     * Internal / non-user options, e.g. the {@code OP_none} placeholder.
     */
    SPECIAL,
    /**
     * Wizard/debug "cheat" options; using them marks the character.
     */
    CHEAT,
    /**
     * Birth options — chosen at character creation and fixed for that game.
     */
    BIRTH,
    /**
     * Competition/score options that affect the high-score table.
     */
    SCORE,
    /**
     * Interface and display preferences.
     */
    INTERFACE
}
