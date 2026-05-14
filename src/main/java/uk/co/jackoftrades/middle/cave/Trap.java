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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.cave;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.TrapEnum;

public class Trap {
    private int trapIndex;      // Probably not needed as we will be pointing directly to traps or using arrays
    private TrapKind kind;

    private Loc grid;

    private int power;
    private int timeout;

    private Flag<TrapEnum> flags;
}
