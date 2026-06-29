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

package uk.co.jackoftrades.middle.game.globals;

/**
 * Constants for a non-normal (O) critical calculation
 *
 * <p>These calculations in general compute a 'power' of the critical
 * by scaling the chance of a hit. This is then converted to a chance
 * of a critical using (a * power) / (b * power + c) where a, b and c
 * are constants specified here
 *
 * @param debuffToh                   Amount added to the to-hit value
 *                                    for calculating the power of a
 *                                    melee attack when the target is
 *                                    debuffed
 * @param powerTohScaleNumerator      Numerator for the scale factor
 *                                    applied to the combined to-hit
 *                                    value to get the power of the
 *                                    critical
 * @param powerTohScaleDenominator    Denominator for the scale factor
 *                                    applied to the combined to-hit
 *                                    value to get the power of the
 *                                    critical
 * @param chancePowerScaleNumerator   scale factor for the critical's
 *                                    power in the numerator for the
 *                                    chance of the critical (a)
 * @param chancePowerScaleDenominator scale factor for the critical's
 *                                    power in the denominator for the
 *                                    chance of the critical (b)
 * @param chanceAddDenominator        Value of an added term in the
 *                                    denominator (c)
 * @author Rowan Crowther
 */
public record OMeleeCriticalData(int debuffToh, int powerTohScaleNumerator,
                                 int powerTohScaleDenominator, int chancePowerScaleNumerator,
                                 int chancePowerScaleDenominator, int chanceAddDenominator) {
}
