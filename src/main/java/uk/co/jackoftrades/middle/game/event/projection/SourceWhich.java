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

package uk.co.jackoftrades.middle.game.event.projection;

import uk.co.jackoftrades.middle.cave.Trap;
import uk.co.jackoftrades.middle.objects.ChestTrap;
import uk.co.jackoftrades.middle.objects.ItemObject;

public sealed interface SourceWhich permits SourceWhich.TrapRecord, SourceWhich.IntRecord,
        SourceWhich.ObjectRecord, SourceWhich.ChestTrapRecord {
    record TrapRecord(Trap trap) implements SourceWhich {
    }

    record IntRecord(int monster) implements SourceWhich {
    }

    record ObjectRecord(ItemObject objct) implements SourceWhich {
    }

    record ChestTrapRecord(ChestTrap chestTrap) implements SourceWhich {
    }
}
