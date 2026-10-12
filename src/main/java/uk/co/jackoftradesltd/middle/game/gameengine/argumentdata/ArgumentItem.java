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

package uk.co.jackoftradesltd.middle.game.gameengine.argumentdata;

import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgumentData;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

/**
 * A command-argument value referring to a game object - the {@code arg_ITEM} variant of
 * {@link CommandArgumentData}, from the {@code struct object *obj} member of C's {@code union
 * cmd_arg_data} (cmd-core.h). {@code Command.setArgItem} and {@code Command.getArgItem} are the
 * ports of {@code cmd_set_arg_item} and {@code cmd_get_arg_item} that wrap and unwrap it, and
 * {@code CommandQueue.disableRepeatFloorItem} is the port of {@code cmd_disable_repeat_floor_item},
 * the one C reader that looks inside the stored object.
 *
 * <p>Where C held a raw pointer, the port holds an {@link ItemObject} reference, the item the
 * command acts on (the potion to quaff, the weapon to wield, a spellbook to cast from, and so on).
 * The record stores the reference unchecked, as {@code cmd_set_arg_item} did, and leaves it to the
 * consumer to decide whether the item is usable; {@code cmd_get_item} re-runs the item filter on
 * whatever it reads back.
 *
 * <p>A {@code null} is a legal value. C stores {@code NULL} under the name {@code "original_item"}
 * in cmd-wizard.c to drop a reference to a deleted preserved copy, and
 * {@code cmd_disable_repeat_floor_item} tests the pointer before reading its grid. Unset is a
 * different state, expressed by the argument being absent from the command.
 *
 * <p>The record holds the object itself, not a snapshot of it, so the command sees later changes
 * to the item, and a floor item that has since moved or been destroyed leaves the reference stale.
 * That is why {@code cmd_disable_repeat_floor_item} forbids repeating a command that acted on one.
 * {@link ItemObject} does not override {@code equals}, so two records are equal only when they
 * hold the very same object.
 *
 * <p>Class ArgumentItem coded before 261012, commented in full on 261012.
 *
 * @param value the referenced item, stored as given; may be {@code null}
 * @author Rowan Crowther
 */
public record ArgumentItem(ItemObject value) implements CommandArgumentData {
    /**
     * Reports the {@code arg_ITEM} tag for this variant, the tag C's {@code cmd_set_arg_item}
     * stores beside the {@code obj} member and {@code cmd_get_arg_item} checks before it reads it.
     * Every instance returns the same tag whatever it holds, a {@code null} item included.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return {@link CommandArgumentType#arg_ITEM}
     */
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_ITEM;
    }

    /**
     * Returns the referenced item, the same reference as {@link #value()}. It is a bean-style
     * alias of the record's own accessor and has no C counterpart; {@code
     * CommandQueue.disableRepeatFloorItem} reads the item through it. Like C's
     * {@code cmd_get_arg_item}, which copies the pointer out, it hands back the stored object
     * itself and not a copy, and the result may be {@code null}.
     *
     * <p>Function getValue coded before 261012, commented in full on 261012.
     *
     * @return the stored item, or {@code null} if the argument holds none
     */
    public ItemObject getValue() {
        return value;
    }

    /**
     * Returns a new record referring to the same item, the port of the per-argument step of C's
     * {@code cmd_copy}. C copies the whole command by struct assignment and deep-copies only the
     * {@code arg_STRING} arguments, so an item argument is copied as a bare pointer: the copied
     * command and the original refer to the same object. The port matches this and does not clone
     * the {@link ItemObject}, which a repeated command relies on to act on the same item, and which
     * keeps the floor-item guard of {@code cmd_disable_repeat_floor_item} meaningful for the copy.
     * The new record equals this one, and a {@code null} item copies as {@code null}.
     *
     * <p>Function copy coded before 261012, commented in full on 261012.
     *
     * @return a new {@code ArgumentItem} holding the same {@link #value()} reference
     */
    @Override
    public CommandArgumentData copy() {
        return new ArgumentItem(value);
    }
}
