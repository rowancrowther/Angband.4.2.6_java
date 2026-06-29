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
 * Constants for a non-normal O critical calculation.
 * These calculations computer a 'power' of the critical
 * by scaling the chance of a hit, then they convert
 * the power to the chance of a hit by using the formula
 * (a * power) / (b * power + c) where the constants
 * a, b and c are specified here
 *
 * @param debuffToh                        Amount added to the to-hit value for calculating
 *                                         the power of a ranged critical when the target
 *                                         is debuffed
 * @param powerLaunchedTohScaleNumerator   The numerator for the scale factor applied to
 *                                         the combined to-hit value to get the power of
 *                                         the critical with a launched missile
 * @param powerLaunchedTohScaleDenominator The denominator for the scale factor applied to
 *                                         the combined to-hit value to get the power of
 *                                         the critical with a launched missile
 * @param powerThrownTohScaleNumerator     The numerator for the scale factor applied to
 *                                         the combined to-hit value to get the power of
 *                                         the critical with a thrown missile
 * @param powerThrownTohScaleDenominator   The denominator for the scale factor applied to
 *                                         the combined to-hit value to get the power of
 *                                         the critical with a thrown missile
 * @param chancePowerScaleNumerator        The scale factor for the critical's power in
 *                                         the numerator (a) for the chance of the critical
 * @param chancePowerScaleDenominator      The scale factor for the critical's power in
 *                                         the denominator (b) for the chance of the critical
 * @param chanceAddDenominator             Value of added terms in the denominator (c) for
 *                                         the chance of a critical
 * @author Rowan Crowther
 */
public record ORangedCriticalData(int debuffToh, int powerLaunchedTohScaleNumerator,
                                  int powerLaunchedTohScaleDenominator,
                                  int powerThrownTohScaleNumerator,
                                  int powerThrownTohScaleDenominator,
                                  int chancePowerScaleNumerator,
                                  int chancePowerScaleDenominator,
                                  int chanceAddDenominator) {
}
