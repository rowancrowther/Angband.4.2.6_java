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

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Effect;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagName;

public class TrapKind {
    private String trapKindName;
    private String text;
    private String description;
    private String messageOnSave;
    private String messageOnFailure;
    private String messageOnExtraEffect;

    private int trapKindIndex;

    private AngbandDisplayCharacter angbandDisplayCharacter;

    private int rarity;
    private int minDepth;
    private int maxDepth;
    private Random power;

    private Flag<TrapEnum> flags;
    private Flag<ObjectFlagName> saveFlags;

    private Effect effect;
    private Effect effectXtra;

    public String getDescription() {
        return description;
    }

    public static TrapKind lookupTrap(String description) {
        return GameConstants.lookupTrap(description);
    }
}