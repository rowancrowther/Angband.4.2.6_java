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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests what {@link Curse}'s constructor does with the object it is handed, and the plain accessors
 * that return the rest of what it was given.
 *
 * <p>C's {@code struct curse} carries a pointer to the curse's own {@code struct object}
 * ({@code curse->obj}), and {@code modify_weight_for_curse()} in {@code obj-curse.c} reads the
 * weight and flags through that pointer each time it runs. The port keeps that shape: the object is
 * built by the caller, stored by reference and read live. These tests pin both halves — that the
 * very instance comes back, and that a change made to it after construction is what the weight
 * calculation sees — because a constructor that copied the object, or a method that cached its
 * weight, would pass every test that builds the curse and calls it once.
 *
 * <p>Class CurseConstructionTest coded on 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
class CurseConstructionTest {

    /**
     * A curse around the given object, with a name, description and index to read back.
     *
     * @param object the curse's own object
     * @return the curse
     */
    private static Curse curseAround(ItemObject object) {
        return new Curse("anti-teleportation", List.of(), object, List.of(),
                new Flag<>(ObjectFlag.class), "Prevents teleportation.", 7);
    }

    /**
     * The object comes back as the same instance, not a copy, on every call.
     */
    @Test
    @DisplayName("the curse's object is stored by reference")
    void objectIsTheInstanceGiven() {
        ItemObject object = new ItemObject();
        Curse curse = curseAround(object);

        assertAll(
                () -> assertSame(object, curse.getItemObject()),
                () -> assertSame(curse.getItemObject(), curse.getItemObject()));
    }

    /**
     * The name, description and index are returned as given. The index is whatever the caller
     * passed; the port does not add C's offset of one.
     */
    @Test
    @DisplayName("name, description and index come back as given")
    void scalarsRoundTrip() {
        Curse curse = curseAround(new ItemObject());

        assertAll(
                () -> assertEquals("anti-teleportation", curse.getName()),
                () -> assertEquals("Prevents teleportation.", curse.getDescription()),
                () -> assertEquals(7, curse.getIndex()));
    }

    /**
     * {@code modify_weight_for_curse()} reads {@code curse->obj->weight} each time it runs, so a
     * change to the object's weight after the curse was built has to show in the next result. An
     * item of weight 10 under a curse of +5 weighs 15; once the curse object's weight is 20 it
     * weighs 30.
     */
    @Test
    @DisplayName("the weight calculation reads the object's weight live")
    void weightIsReadLive() {
        ItemObject object = new ItemObject();
        object.setWeight(5);
        Curse curse = curseAround(object);

        assertEquals(15, curse.modifyWeightForCurse(10));

        object.setWeight(20);

        assertEquals(30, curse.modifyWeightForCurse(10));
    }

    /**
     * The same holds for the flag that picks the branch. With the object's weight at 150 an item of
     * 10 gains 150 while the flag is off and becomes 15 once it is on, because C tests
     * {@code of_has(curse_obj->flags, OF_MULTIPLY_WEIGHT)} on every call.
     */
    @Test
    @DisplayName("the weight calculation reads the object's flags live")
    void flagIsReadLive() {
        ItemObject object = new ItemObject();
        object.setWeight(150);
        Curse curse = curseAround(object);

        assertEquals(160, curse.modifyWeightForCurse(10));

        Flag<ObjectFlag> multiply = new Flag<>(ObjectFlag.class);
        multiply.on(ObjectFlag.OF_MULTIPLY_WEIGHT);
        object.setFlagsTo(multiply);

        assertEquals(15, curse.modifyWeightForCurse(10));
    }
}
