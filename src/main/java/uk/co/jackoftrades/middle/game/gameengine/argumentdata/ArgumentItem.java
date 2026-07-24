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

package uk.co.jackoftrades.middle.game.gameengine.argumentdata;

import uk.co.jackoftrades.middle.game.enums.CommandArgumentType;
import uk.co.jackoftrades.middle.game.gameengine.CommandArgumentData;
import uk.co.jackoftrades.middle.objects.ItemObject;

/**
 * A command-argument value referring to a game object - the {@code arg_ITEM} variant of
 * {@link CommandArgumentData}, from the union's {@code obj} pointer in C.
 *
 * <p>C held a raw {@code struct object *}; the port holds an {@link ItemObject} reference, the
 * item the command acts on (the potion to quaff, the weapon to wield, and so on).
 *
 * @param value the referenced item
 * @author Rowan Crowther
 */
public record ArgumentItem(ItemObject value) implements CommandArgumentData {
    /**
     * The argument-type tag corresponding to this variant, so a value can be matched against a
     * requested type without a downcast.
     *
     * @return this variant's {@link CommandArgumentType}
     */
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_ITEM;
    }

    public ItemObject getValue() {
        return value;
    }
}
