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
 * Record to hold constants dealing with object generation
 *
 * @param maxDepth    Maximum depth used in object creation
 * @param greatObj    1/chance of inflating the requested
 *                    object level
 * @param greatEgo    1/chance of inflating the requested
 *                    ego level
 * @param fuelTorch   Maximum amount of fuel in a torch
 * @param fuelLamp    Maximum amount of fuel in a lantern
 * @param defaultLamp Default amount of fuel in a lantern
 * @author Rowan Crowther
 */
public record ObjMakeData(int maxDepth, int greatObj, int greatEgo, int fuelTorch,
                          int fuelLamp, int defaultLamp) {
}
