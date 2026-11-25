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

package uk.co.jackoftradesltd.middle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Activation#copy()}: the copy owns a list of its own, holds its own {@link Effect}
 * instances, and carries the other fields across.
 *
 * <p>C has no function that copies a {@code struct activation}; the Java copy exists because
 * {@link Effect} is mutable and a shared effect list would let a change to one activation show
 * up on the other. The expected values are therefore the contract in the {@code copy()} Javadoc,
 * not a C result. {@link Effect} has no getters, so independence is checked by instance identity.
 *
 * <p>The index is pinned in {@code ActivationIndexTest}, not here.
 *
 * <p>Class ActivationCopyTest coded on 261125, commented in full on 261125.
 *
 * @author Rowan Crowther
 */
class ActivationCopyTest {

    /**
     * An effect with every field the copy has to rebuild: dice, a sub-type payload and an empty
     * expression list.
     */
    private static Effect effect() {
        return effect(EffectEnum.EF_NONE);
    }

    /**
     * As {@link #effect()}, with the given effect type so that two effects can be told apart.
     */
    private static Effect effect(EffectEnum type) {
        return new Effect(type, new Random(0, 1, 1, 1, false), "1d1", 0, 0,
                EffectSubTypeEnum.EST_PROJ, new EffectSubTypeWrapper(ProjectionEnum.PROJ_ACID),
                0, 0, new ArrayList<>(), "a message");
    }

    /**
     * An activation holding two distinct effects, in a list the caller still owns.
     */
    private static Activation activation(List<Effect> effects) {
        return new Activation("TEST", 5, true, 12, 34, effects, "It fires.", "fires");
    }

    @Test
    @DisplayName("the copy is a new activation, not the original")
    void copyIsANewObject() {
        Activation original = activation(new ArrayList<>(List.of(effect())));

        assertNotSame(original, original.copy());
    }

    @Test
    @DisplayName("the copy's effects are new instances, one for each original, in the same order")
    void copyHoldsItsOwnEffectInstances() throws Exception {
        Effect first = effect();
        Effect second = effect();
        Activation original = activation(new ArrayList<>(List.of(first, second)));

        Activation copy = original.copy();

        List<Effect> copied = effectsOf(copy);
        assertEquals(2, copied.size());
        assertNotSame(first, copied.get(0));
        assertNotSame(second, copied.get(1));
        assertNotSame(copied.get(0), copied.get(1));
    }

    @Test
    @DisplayName("the copy's effect list is its own: growing it leaves the original's list alone")
    void copyOwnsItsEffectList() throws Exception {
        Activation original = activation(new ArrayList<>(List.of(effect())));

        Activation copy = original.copy();
        effectsOf(copy).add(effect());

        assertEquals(1, effectsOf(original).size());
        assertEquals(2, effectsOf(copy).size());
    }

    @Test
    @DisplayName("an activation with no effects copies to one with no effects")
    void emptyEffectListCopiesToEmpty() throws Exception {
        Activation copy = activation(new ArrayList<>()).copy();

        assertTrue(effectsOf(copy).isEmpty());
    }

    @Test
    @DisplayName("the copy keeps the name and the power")
    void copyKeepsNameAndPower() {
        Activation copy = activation(new ArrayList<>()).copy();

        assertEquals("TEST", copy.getName());
        assertEquals(34, copy.getPower());
    }

    @Test
    @DisplayName("the copy keeps aim, level, message and description")
    void copyKeepsTheOtherFields() throws Exception {
        Activation copy = activation(new ArrayList<>()).copy();

        assertEquals(true, fieldOf(copy, "aim"));
        assertEquals(12, fieldOf(copy, "level"));
        assertEquals("It fires.", fieldOf(copy, "message"));
        assertEquals("fires", fieldOf(copy, "desc"));
    }

    @Test
    @DisplayName("the copy keeps the effects in file order: C runs the first effect line first")
    void copyKeepsEffectOrder() throws Exception {
        Activation original = activation(new ArrayList<>(List.of(
                effect(EffectEnum.EF_RANDOM), effect(EffectEnum.EF_NONE), effect(EffectEnum.EF_MAX))));

        List<Effect> copied = effectsOf(original.copy());

        assertEquals(EffectEnum.EF_RANDOM, fieldOf(copied.get(0), "index"));
        assertEquals(EffectEnum.EF_NONE, fieldOf(copied.get(1), "index"));
        assertEquals(EffectEnum.EF_MAX, fieldOf(copied.get(2), "index"));
    }

    @Test
    @DisplayName("growing the original's list after the copy is made leaves the copy alone")
    void changeToOriginalDoesNotReachTheCopy() throws Exception {
        Activation original = activation(new ArrayList<>(List.of(effect())));

        Activation copy = original.copy();
        effectsOf(original).add(effect());

        assertEquals(2, effectsOf(original).size());
        assertEquals(1, effectsOf(copy).size());
    }

    @Test
    @DisplayName("a record with no msg: or desc: line (C's NULL) copies with null message and description")
    void nullMessageAndDescriptionCopyAsNull() throws Exception {
        Activation original = new Activation("TEST", 0, false, 0, 0, new ArrayList<>(), null, null);

        Activation copy = original.copy();

        assertNull(fieldOf(copy, "message"));
        assertNull(fieldOf(copy, "desc"));
    }

    @Test
    @DisplayName("the constructor keeps the caller's list itself, as its Javadoc says: no defensive copy")
    void constructorKeepsTheCallersListItself() throws Exception {
        List<Effect> callers = new ArrayList<>();
        Activation a = activation(callers);

        assertSame(callers, effectsOf(a));
    }

    @Test
    @DisplayName("an activation built with a null effect list cannot be copied: NullPointerException")
    void nullEffectListCannotBeCopied() {
        Activation a = new Activation("TEST", 0, false, 0, 0, null, null, null);

        assertThrows(NullPointerException.class, a::copy);
    }

    /**
     * Reads an activation's private {@code effects} list.
     */
    @SuppressWarnings("unchecked")
    private static List<Effect> effectsOf(Activation activation) throws Exception {
        return (List<Effect>) fieldOf(activation, "effects");
    }

    /**
     * Reads a private field of an activation; it has no getters for these yet.
     */
    private static Object fieldOf(Activation activation, String name) throws Exception {
        java.lang.reflect.Field f = Activation.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(activation);
    }

    /**
     * Reads a private field of an effect; {@link Effect} has no getters for its type.
     */
    private static Object fieldOf(Effect effect, String name) throws Exception {
        java.lang.reflect.Field f = Effect.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(effect);
    }
}
