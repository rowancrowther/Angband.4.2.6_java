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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#isFullyKnown()} and {@link ItemObject#hasStandardToH()} on the object
 * a {@link Curse} owns ({@link Curse#getItemObject()}), against the questions C
 * asks of a curse object: {@code object_fully_known}, which is {@code object_runes_known} followed
 * by {@code object_effect_is_known}, and {@code object_non_curse_runes_known} beneath it.
 *
 * <p>C does ask these of a curse. {@code write_curse_kinds} in {@code obj-init.c} gives every curse
 * object the {@code <curse object>} kind and a known counterpart marked assessed, so
 * {@code player_know_object} runs its fully-known block on one. A curse object has no brands, slays
 * or curses, so what remains of the checklist is the three combat figures, every modifier, every
 * element, the flags and the effect. Every expected value below is read off that checklist, with a
 * missing modifier or element read as zero, as C's zeroed arrays do.
 *
 * <p>The known figures are written on {@code curse.getItemObject().getKnown()}, where
 * {@code PlayerKnowledge.knowObject(Player, Curse)} writes them.
 *
 * <p>Class CurseIsFullyKnownTest coded on 261004, commented in full on 261004, moved onto the curse's object
 * on 261005.
 *
 * @author Rowan Crowther
 */
@DisplayName("a curse object: isFullyKnown and hasStandardToH")
class CurseIsFullyKnownTest {

    /**
     * Gives {@link ObjectRegistry} an empty curse list. {@code ItemObject.isFullyKnown()} compares
     * the curses of the object and its known twin by walking the registry's curses, and the registry
     * holds {@code null} until something loads or sets them, so a test that reached this class
     * first in the JVM threw a {@link NullPointerException} while one that ran after another test
     * had seeded it passed. None of these tests puts a curse on an item, so empty is the right list.
     */
    @BeforeEach
    void seedRegistry() {
        ObjectRegistry.setCurses(new ArrayList<>());
    }

    /**
     * A curse with the given combat figures and nothing else, in {@code curse.txt}'s
     * {@code combat:to-h:to-d:to-a} order.
     */
    private static Curse curse(int toHit, int toDam, int toAC) {
        return curse(toHit, toDam, toAC, null, Map.of(), Map.of(), new Flag<>(ObjectFlag.class));
    }

    private static Curse curse(int toHit, int toDam, int toAC, Effect effect,
                               Map<ObjectModifier, Integer> modifiers,
                               Map<ElementEnum, ElementInfo> elements,
                               Flag<ObjectFlag> flags) {
        return CurseFixture.curse("test curse", List.of(), 0, effect, flags, modifiers, elements,
                toHit, toDam, toAC, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    private static Flag<ObjectFlag> flags(ObjectFlag... flags) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (flags.length > 0) result.set(List.of(flags));
        return result;
    }

    private static Effect effect() {
        return new Effect(EffectEnum.EF_NONE, null, null, 0, 0, null, null, 0, 0, List.of(), null);
    }

    /**
     * {@code object_has_standard_to_h} for a curse object: the kind is non-null and not body armour,
     * so the answer is {@code obj->to_h == 0}. The three curses in {@code curse.txt} with a non-zero
     * to-hit are enveloping, irritation and air swing.
     */
    @Nested
    @DisplayName("hasStandardToH")
    class StandardToH {

        @Test
        @DisplayName("a zero to-hit is standard")
        void zero() {
            assertTrue(curse(0, 0, 0).getItemObject().hasStandardToH());
        }

        @Test
        @DisplayName("enveloping, irritation and air swing are not standard")
        void nonZero() {
            assertFalse(curse(-5, -5, 20).getItemObject().hasStandardToH());
            assertFalse(curse(-15, -15, 0).getItemObject().hasStandardToH());
            assertFalse(curse(-20, 0, 0).getItemObject().hasStandardToH());
        }

        @Test
        @DisplayName("only the to-hit decides it, not the other two figures")
        void otherFiguresIgnored() {
            assertTrue(curse(0, -50, 20).getItemObject().hasStandardToH());
        }
    }

    @Nested
    @DisplayName("combat figures")
    class Combat {

        @Test
        @DisplayName("a curse with all figures zero is fully known with nothing learned")
        void bare() {
            assertTrue(curse(0, 0, 0).getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a to-hit the player cannot read blocks it; the real figure completes it")
        void toHit() {
            Curse c = curse(-5, 0, 0);
            assertFalse(c.getItemObject().isFullyKnown());

            c.getItemObject().getKnown().setToHit(-5);
            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a to-damage the player cannot read blocks it; the real figure completes it")
        void toDam() {
            Curse c = curse(0, -5, 0);
            assertFalse(c.getItemObject().isFullyKnown());

            c.getItemObject().getKnown().setToDam(-5);
            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a to-AC the player cannot read blocks it; the real figure completes it")
        void toAC() {
            Curse c = curse(0, 0, -50);
            assertFalse(c.getItemObject().isFullyKnown());

            c.getItemObject().getKnown().setToAC(-50);
            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("the figures are compared exactly, so a wrong known figure also blocks it")
        void exactComparison() {
            Curse c = curse(0, 0, 20);
            c.getItemObject().getKnown().setToAC(19);

            assertFalse(c.getItemObject().isFullyKnown());
        }
    }

    /**
     * C compares every modifier slot, so a modifier absent from either map reads as zero. The
     * sentinels are not slots in C.
     */
    @Nested
    @DisplayName("modifiers")
    class Modifiers {

        @Test
        @DisplayName("a curse conferring no modifiers is fully known before knowObject has run")
        void emptyKnownMap() {
            assertTrue(curse(0, 0, 0).getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a modifier the player cannot read blocks it; the real value completes it")
        void unlearned() {
            Curse c = curse(0, 0, 0, null, Map.of(ObjectModifier.OM_STEALTH, -3), Map.of(),
                    flags());
            assertFalse(c.getItemObject().isFullyKnown());

            c.getItemObject().getKnown().setModifiers(Map.of(ObjectModifier.OM_STEALTH, -3));
            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a known modifier the curse does not carry blocks it, as C compares both slots")
        void knownButNotCarried() {
            Curse c = curse(0, 0, 0);
            c.getItemObject().getKnown().setModifiers(Map.of(ObjectModifier.OM_STR, 2));

            assertFalse(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("an explicit zero on the known side equals an absent one on the curse side")
        void explicitZero() {
            Curse c = curse(0, 0, 0);
            c.getItemObject().getKnown().setModifiers(Map.of(ObjectModifier.OM_STR, 0));

            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a full known map of zeros, as knowObject writes it, still reads as fully known")
        void fullZeroMap() {
            Map<ObjectModifier, Integer> known = new HashMap<>();
            for (ObjectModifier modifier : ObjectModifier.values()) {
                known.put(modifier, 0);
            }
            Curse c = curse(0, 0, 0);
            c.getItemObject().getKnown().setModifiers(known);

            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("the sentinels are not modifier slots, so a value on one is ignored")
        void sentinelsIgnored() {
            Map<ObjectModifier, Integer> known = new HashMap<>();
            known.put(ObjectModifier.OM_MAX, 7);
            known.put(ObjectModifier.OM_NONE, 7);
            Curse c = curse(0, 0, 0);
            c.getItemObject().getKnown().setModifiers(known);

            assertTrue(c.getItemObject().isFullyKnown());
        }
    }

    @Nested
    @DisplayName("elements")
    class Elements {

        private ElementInfo resist(int level) {
            ElementInfo info = new ElementInfo();
            info.setResLevel(level);
            return info;
        }

        @Test
        @DisplayName("a resistance with no known entry blocks it")
        void noKnownEntry() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(ElementEnum.ELEM_FIRE, resist(1)), flags());

            assertFalse(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a known entry at level zero is not knowledge of a resistance")
        void knownAtZero() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(ElementEnum.ELEM_FIRE, resist(1)), flags());
            c.getItemObject().getKnown().putElInfo(ElementEnum.ELEM_FIRE, resist(0));

            assertFalse(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a known entry with a resistance level completes it")
        void knownResistance() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(ElementEnum.ELEM_FIRE, resist(1)), flags());
            c.getItemObject().getKnown().putElInfo(ElementEnum.ELEM_FIRE, resist(1));

            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a vulnerability needs knowing as much as a resistance does")
        void vulnerability() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(ElementEnum.ELEM_FIRE, resist(-1)), flags());
            assertFalse(c.getItemObject().isFullyKnown());

            c.getItemObject().getKnown().putElInfo(ElementEnum.ELEM_FIRE, resist(-1));
            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("an element the curse names at level zero needs no knowledge")
        void zeroLevelEntry() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(ElementEnum.ELEM_FIRE, resist(0)), flags());

            assertTrue(c.getItemObject().isFullyKnown());
        }
    }

    /**
     * C's test is {@code of_is_subset(obj->known->flags, obj->flags)}: the known flags must contain
     * every real flag, and may carry more.
     */
    @Nested
    @DisplayName("flags")
    class Flags {

        @Test
        @DisplayName("a flag the player has not learned blocks it")
        void unlearned() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(), flags(ObjectFlag.OF_AFRAID));

            assertFalse(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("learning the flag completes it")
        void learned() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(), flags(ObjectFlag.OF_AFRAID));
            c.getItemObject().getKnown().setFlagsTo(flags(ObjectFlag.OF_AFRAID));

            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("the known flags may carry more than the curse has")
        void knownSuperset() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(), flags(ObjectFlag.OF_AFRAID));
            c.getItemObject().getKnown().setFlagsTo(flags(ObjectFlag.OF_AFRAID, ObjectFlag.OF_IMPAIR_HP));

            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("knowing a different flag does not count")
        void wrongFlag() {
            Curse c = curse(0, 0, 0, null, Map.of(), Map.of(), flags(ObjectFlag.OF_AFRAID));
            c.getItemObject().getKnown().setFlagsTo(flags(ObjectFlag.OF_IMPAIR_HP));

            assertFalse(c.getItemObject().isFullyKnown());
        }
    }

    /**
     * {@code object_effect_is_known} is {@code obj->effect == obj->known->effect}, a pointer
     * comparison, so two equal effects that are not the same one do not satisfy it.
     */
    @Nested
    @DisplayName("effect")
    class Effects {

        @Test
        @DisplayName("an effect the player has not been given blocks it")
        void unknown() {
            Curse c = curse(0, 0, 0, effect(), Map.of(), Map.of(), flags());

            assertFalse(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("the same effect on the known side completes it")
        void sameInstance() {
            Effect e = effect();
            Curse c = curse(0, 0, 0, e, Map.of(), Map.of(), flags());
            c.getItemObject().getKnown().setEffect(List.of(e));

            assertTrue(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("an equal but distinct effect does not complete it, as C compares pointers")
        void distinctInstance() {
            Curse c = curse(0, 0, 0, effect(), Map.of(), Map.of(), flags());
            c.getItemObject().getKnown().setEffect(List.of(effect()));

            assertFalse(c.getItemObject().isFullyKnown());
        }

        @Test
        @DisplayName("a curse with no effect and no known effect agrees")
        void bothNull() {
            assertTrue(curse(0, 0, 0).getItemObject().isFullyKnown());
        }
    }
}
