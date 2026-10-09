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

package uk.co.jackoftradesltd.middle.gameinput;

/**
 * The outcome of asking the player to pick one effect from a list. C's
 * {@code get_effect_from_list} ({@code game-input.c}, with the text UI's
 * {@code textui_get_effect_from_list} in {@code ui-effect.c}) packs three different outcomes into a
 * single {@code int}: a zero-based list index, {@code -2} for the "random" option and {@code -1}
 * for a cancelled or invalid selection. The port makes the three outcomes distinct types, so a
 * caller can no longer mistake a sentinel for an index.
 *
 * <pre>{@code
 * sealed interface EffectChoice permits Index, Random, Aborted
 * record Index(int value)   // C: any result >= 0
 * record Random()           // C: -2
 * record Aborted()          // C: -1
 * }</pre>
 *
 * <p>The cases are consumed by {@code Command.getEffectFromList} (the port of C's
 * {@code cmd_get_effect_from_list} in {@code cmd-core.c}) and produced by
 * {@code GameInput.getEffectFromList}. The UI-less fallback in C returns {@code -2} when random is
 * allowed and {@code -1} otherwise; the Java equivalent is {@code DefaultGameInput}.
 *
 * <p>Class EffectChoice coded before 261009, commented in full on 261009.
 */
public sealed interface EffectChoice permits EffectChoice.Index,
        EffectChoice.Aborted, EffectChoice.Random {
    /**
     * The player picked the effect at a position in the list - C's non-negative return value.
     * The record does not itself check that {@code value} is in range, just as the C {@code int}
     * could not: the caller validates it against the list size, and treats an out-of-range index
     * as an abort.
     *
     * <p>Record Index coded before 261009, commented in full on 261009.
     *
     * @param value the zero-based position of the chosen effect in the list
     */
    record Index(int value) implements EffectChoice {
    }

    /**
     * The player took the extra "choose at random" option that is only offered when the caller
     * passes {@code allowRandom} - C's {@code -2} return value. It is a successful choice, not a
     * failure: the caller then rolls a random index itself.
     *
     * <p>Record Random coded before 261009, commented in full on 261009.
     */
    record Random() implements EffectChoice {
    }

    /**
     * The player cancelled, or made an invalid selection - C's {@code -1} return value. The
     * caller gives up on the whole effect, as C's {@code effect_do} returns {@code false}.
     *
     * <p>Record Aborted coded before 261009, commented in full on 261009.
     */
    record Aborted() implements EffectChoice {
    }
}