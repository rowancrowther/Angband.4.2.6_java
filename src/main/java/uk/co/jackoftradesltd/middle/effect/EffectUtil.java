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

package uk.co.jackoftradesltd.middle.effect;

import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.game.event.projection.Source;

/**
 * Free-standing helpers for firing effects — the port landing spot for the convenience wrappers in
 * C's {@code effects.c}.
 *
 * <p><b>Status:</b> a stub landed to unblock the game loop. The real implementation is gated on the
 * dice/expression and effects engine, the deepest blocker in the port — every {@code effect:} in
 * every data file is inert until it exists — so this does nothing yet.
 *
 * @author Rowan Crowther
 */
public class EffectUtil {
    /**
     * Fire a single effect with no dice object, building one from a dice string on the fly — the port
     * of C's {@code effect_simple}. It is the one-shot convenience path callers use when they don't
     * hold a parsed {@code struct effect}, e.g. the dwarven ore-detection in the player-processing
     * pass.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the effects engine is ported.
     *
     * @param index      which effect to run
     * @param origin     the source of the effect (a {@code null} origin ports C's {@code source_none()})
     * @param diceString the dice expression the effect's magnitude is parsed from
     * @param wrapper    the effect subtype, disambiguating effects that share a handler
     * @param radius     the effect radius, for area effects
     * @param other      the spare per-effect parameter (C's {@code other})
     * @param y          the target grid row
     * @param x          the target grid column
     * @param ident      out-parameter flag set when the effect reveals information; {@code null} when
     *                   the caller does not care, as C passes {@code NULL}
     */
    public static void effectSimple(EffectEnum index,
                                    Source origin,
                                    String diceString,
                                    EffectSubTypeWrapper wrapper,
                                    int radius,
                                    int other,
                                    int y,
                                    int x,
                                    Boolean ident) {
        // Stub class TODO: implement
    }
}
