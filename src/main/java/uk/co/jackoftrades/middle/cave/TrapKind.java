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
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagName;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TrapKind trapKind = (TrapKind) o;
        return trapKindIndex == trapKind.trapKindIndex && rarity == trapKind.rarity && minDepth == trapKind.minDepth && maxDepth == trapKind.maxDepth && Objects.equals(trapKindName, trapKind.trapKindName) && Objects.equals(text, trapKind.text) && Objects.equals(getDescription(), trapKind.getDescription()) && Objects.equals(messageOnSave, trapKind.messageOnSave) && Objects.equals(messageOnFailure, trapKind.messageOnFailure) && Objects.equals(messageOnExtraEffect, trapKind.messageOnExtraEffect) && Objects.equals(angbandDisplayCharacter, trapKind.angbandDisplayCharacter) && Objects.equals(power, trapKind.power) && Objects.equals(flags, trapKind.flags) && Objects.equals(saveFlags, trapKind.saveFlags) && Objects.equals(effect, trapKind.effect) && Objects.equals(effectXtra, trapKind.effectXtra);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(trapKindName);
        result = 31 * result + Objects.hashCode(text);
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(messageOnSave);
        result = 31 * result + Objects.hashCode(messageOnFailure);
        result = 31 * result + Objects.hashCode(messageOnExtraEffect);
        result = 31 * result + trapKindIndex;
        result = 31 * result + Objects.hashCode(angbandDisplayCharacter);
        result = 31 * result + rarity;
        result = 31 * result + minDepth;
        result = 31 * result + maxDepth;
        result = 31 * result + Objects.hashCode(power);
        result = 31 * result + Objects.hashCode(flags);
        result = 31 * result + Objects.hashCode(saveFlags);
        result = 31 * result + Objects.hashCode(effect);
        result = 31 * result + Objects.hashCode(effectXtra);
        return result;
    }
}