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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;

public class ElementInfo {
    private Flag<ElementInfoEnum> flags;
    private int resLevel;

    public int getResLevel() {
        return resLevel;
    }

    public Flag<ElementInfoEnum> getFlags() {
        return flags;
    }
}
