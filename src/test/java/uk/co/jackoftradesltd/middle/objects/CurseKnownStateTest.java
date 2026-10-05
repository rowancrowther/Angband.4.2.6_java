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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the state a freshly built {@link Curse} starts in and the way its known-view setters store
 * what they are given — the parts of C's {@code curse->obj->known} that {@code CurseIsFullyKnownTest}
 * and {@code CurseNullMapsTest} reach only indirectly.
 *
 * <p>In C {@code write_curse_kinds} ({@code obj-init.c}) gives each curse a zeroed
 * {@code known} object, so a new curse knows nothing: zero combat figures, no modifiers, no elements,
 * no flags, no effect. The port must start the same way, and the three setters differ in what they do
 * with their argument — {@code setKnownObjectFlags} copies into an owned set (C's {@code of_wipe}
 * then copy), while {@code setKnownModifiers} and {@code setKnownElInfo} store the caller's map.
 * Those differences are what these tests pin.
 *
 * <p>Class CurseKnownStateTest coded on 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
class CurseKnownStateTest {

    /**
     * A curse with no properties at all.
     *
     * @return the curse
     */
    private static Curse bare() {
        return new Curse("bare", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * What a new curse knows.
     */
    @Nested
    @DisplayName("a new curse")
    class Fresh {

        /**
         * The known containers exist and are empty, as C's zeroed {@code known} object is. The
         * accessors must never answer {@code null}.
         */
        @Test
        @DisplayName("knows no flags and no elements")
        void emptyKnownView() {
            Curse curse = bare();

            assertAll(
                    () -> assertNotNull(curse.getKnownObjectFlags()),
                    () -> assertTrue(curse.getKnownObjectFlags().isEmpty()),
                    () -> assertNotNull(curse.getKnownElInfo()),
                    () -> assertTrue(curse.getKnownElInfo().isEmpty()));
        }

        /**
         * The curse's own object is made once and handed out unchanged, standing for C's
         * {@code curse->obj}.
         */
        @Test
        @DisplayName("has one stable, non-null own object")
        void ownObjectIsStable() {
            Curse curse = bare();

            assertAll(
                    () -> assertNotNull(curse.getItemObject()),
                    () -> assertSame(curse.getItemObject(), curse.getItemObject()));
        }

        /**
         * The conflict list is unresolved until the assembler's second pass: C has no such list, so
         * there is nothing for it to default to.
         */
        @Test
        @DisplayName("has no resolved conflict list until it is set")
        void conflictUnresolved() {
            Curse curse = bare();
            List<Curse> resolved = List.of(bare());

            assertNull(curse.getConflict());
            curse.setConflict(resolved);
            assertSame(resolved, curse.getConflict());
        }

        /**
         * With no {@code effect:} block the curse has no effect, as C's {@code curse->obj->effect}
         * is null there.
         */
        @Test
        @DisplayName("without an effect block it has no effect")
        void noEffect() {
            assertNull(bare().getEffect());
        }
    }

    /**
     * How the setters take what they are handed.
     */
    @Nested
    @DisplayName("the known-view setters")
    class Setters {

        /**
         * {@code setKnownObjectFlags} copies: switching a flag on in the caller's set afterwards
         * must not reach the curse, because the known flags are derived afresh and must not alias
         * the player's own knowledge set.
         */
        @Test
        @DisplayName("setKnownObjectFlags copies the flags rather than keeping the caller's set")
        void flagsAreCopied() {
            Curse curse = bare();
            Flag<ObjectFlag> given = new Flag<>(ObjectFlag.class);
            given.set(List.of(ObjectFlag.OF_AFRAID));

            curse.setKnownObjectFlags(given);
            given.set(List.of(ObjectFlag.OF_IMPAIR_HP));

            assertAll(
                    () -> assertTrue(curse.getKnownObjectFlags().has(ObjectFlag.OF_AFRAID)),
                    () -> assertFalse(curse.getKnownObjectFlags().has(ObjectFlag.OF_IMPAIR_HP)));
        }

        /**
         * C wipes the known flags before copying, so a second call replaces the first rather than
         * adding to it — a flag the player can no longer read must disappear.
         */
        @Test
        @DisplayName("setKnownObjectFlags wipes before copying, so a flag no longer given disappears")
        void flagsAreWiped() {
            Curse curse = bare();
            Flag<ObjectFlag> first = new Flag<>(ObjectFlag.class);
            first.set(List.of(ObjectFlag.OF_AFRAID));
            Flag<ObjectFlag> second = new Flag<>(ObjectFlag.class);
            second.set(List.of(ObjectFlag.OF_IMPAIR_HP));

            curse.setKnownObjectFlags(first);
            curse.setKnownObjectFlags(second);

            assertAll(
                    () -> assertFalse(curse.getKnownObjectFlags().has(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(curse.getKnownObjectFlags().has(ObjectFlag.OF_IMPAIR_HP)));
        }

        /**
         * The known flags are the curse's own set for the whole of its life, so a reference taken
         * before a later call still sees what that call wrote.
         */
        @Test
        @DisplayName("setKnownObjectFlags keeps the same set instance")
        void flagsKeepInstance() {
            Curse curse = bare();
            Flag<ObjectFlag> before = curse.getKnownObjectFlags();
            Flag<ObjectFlag> given = new Flag<>(ObjectFlag.class);
            given.set(List.of(ObjectFlag.OF_AFRAID));

            curse.setKnownObjectFlags(given);

            assertSame(before, curse.getKnownObjectFlags());
        }

        /**
         * {@code setKnownElInfo} stores the map it is given, so
         * {@code putKnownElementInfo} afterwards writes into the caller's map — the documented
         * hand-over.
         */
        @Test
        @DisplayName("setKnownElInfo stores the caller's map, and put writes into it")
        void elInfoIsStored() {
            Curse curse = bare();
            Map<ElementEnum, ElementInfo> given = new HashMap<>();
            ElementInfo fire = new ElementInfo();
            fire.setResLevel(1);

            curse.setKnownElInfo(given);
            curse.putKnownElementInfo(ElementEnum.ELEM_FIRE, fire);

            assertAll(
                    () -> assertSame(given, curse.getKnownElInfo()),
                    () -> assertSame(fire, given.get(ElementEnum.ELEM_FIRE)));
        }

        /**
         * A later {@code setKnownElInfo} replaces the whole view rather than merging, so an element
         * the player has stopped being able to read goes away.
         */
        @Test
        @DisplayName("setKnownElInfo replaces rather than merges")
        void elInfoReplaces() {
            Curse curse = bare();
            ElementInfo fire = new ElementInfo();
            fire.setResLevel(1);
            curse.putKnownElementInfo(ElementEnum.ELEM_FIRE, fire);

            curse.setKnownElInfo(new HashMap<>());

            assertTrue(curse.getKnownElInfo().isEmpty());
        }

        /**
         * A curse is only fully known once a resistance it really has is also known, and a known
         * modifier map that does not name a modifier the curse does carry reads as zero for it. Here
         * the curse has strength -2: the empty known map reads zero and mismatches, and a map
         * naming it matches.
         */
        @Test
        @DisplayName("setKnownModifiers stores the map, and replacing it can undo knowledge")
        void modifiersReplace() {
            Curse curse = new Curse("weakness", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                    Map.of(ObjectModifier.OM_STR, -2), Map.of(), 0, 0, 0, List.of(),
                    new Flag<>(ObjectFlag.class), "", "", 0);
            Map<ObjectModifier, Integer> known = new HashMap<>();
            known.put(ObjectModifier.OM_STR, -2);

            assertFalse(curse.isFullyKnown());
            curse.setKnownModifiers(known);
            assertTrue(curse.isFullyKnown());
            curse.setKnownModifiers(new HashMap<>());
            assertFalse(curse.isFullyKnown());
        }
    }

    /**
     * {@link Curse#getTime()} on an effect that lacks timing dice, the case the Javadoc records.
     */
    @Nested
    @DisplayName("getTime on an effect without a time line")
    class TimeWithoutDice {

        /**
         * {@code EffectAssembler} leaves an effect's time {@code null} when its block has no
         * {@code time:} line, and {@code getTime} delegates to it, so the answer is {@code null}
         * rather than the zero {@link uk.co.jackoftradesltd.middle.numerics.Random} C's zeroed
         * {@code curse->obj->time} would give. No curse in {@code curse.txt} has an
         * {@code effect:} block without a {@code time:} line, so the data never exercises it; this
         * pins the behaviour so a change to it is deliberate.
         */
        @Test
        @DisplayName("answers null, unlike a curse with no effect at all")
        void nullTime() {
            Effect effect = new Effect(null, null, "", 0, 0, null, null, 0, 0, null, List.of(), "");
            Curse curse = new Curse("timeless", List.of(), 0, effect, new Flag<>(ObjectFlag.class),
                    Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);

            assertNull(curse.getTime());
            assertEquals(effect, curse.getEffect());
        }
    }
}
