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
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the state a freshly built {@link Curse} starts in and the way the known half of its own
 * object stores what it is given — the parts of C's {@code curse->obj->known} that
 * {@code CurseIsFullyKnownTest} reaches only indirectly.
 *
 * <p>In C {@code write_curse_kinds} ({@code obj-init.c}) gives each curse a zeroed
 * {@code known} object, so a new curse knows nothing: zero combat figures, no modifiers, no elements,
 * no flags, no effect. The port holds that object as {@code curse.getItemObject().getKnown()}, and the
 * setters {@code PlayerKnowledge.knowObject(Player, Curse)} writes through differ in what they do
 * with their argument — {@code setFlagsTo} copies into an owned set (C's {@code of_wipe} then copy),
 * while {@code setModifiers} and {@code setElInfo} store the caller's map. Those differences are what
 * these tests pin.
 *
 * <p>Class CurseKnownStateTest coded on 261005, commented in full on 261005, moved onto the curse's
 * object on 261005.
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
        return CurseFixture.curse("bare", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * What a new curse knows.
     */
    @Nested
    @DisplayName("a new curse")
    class Fresh {

        /**
         * The known object exists, is marked assessed as {@code write_curse_kinds} leaves it, and
         * holds nothing: the accessors must never answer {@code null}.
         */
        @Test
        @DisplayName("has an assessed, empty known object")
        void emptyKnownView() {
            ItemObject known = bare().getItemObject().getKnown();

            assertNotNull(known);
            assertAll(
                    () -> assertTrue(known.getNoticeHas(ObjectNotice.OBJ_NOTICE_ASSESSED)),
                    () -> assertTrue(known.getFlags().isEmpty()),
                    () -> assertTrue(known.getElInfo().isEmpty()),
                    () -> assertTrue(known.getModifiers().isEmpty()),
                    () -> assertTrue(known.getEffect().isEmpty()),
                    () -> assertTrue(known.getToHit() == 0 && known.getToDam() == 0 && known.getToAC() == 0));
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
         * With no {@code effect:} block the curse's object has no effect: an empty list, as
         * {@link ItemObject#setEffect} turns a {@code null} into one, where C's
         * {@code curse->obj->effect} is null.
         */
        @Test
        @DisplayName("without an effect block its object has an empty effect list")
        void noEffect() {
            assertTrue(bare().getItemObject().getEffect().isEmpty());
        }
    }

    /**
     * How the known object's setters take what they are handed.
     */
    @Nested
    @DisplayName("the known object's setters")
    class Setters {

        /**
         * {@code setFlagsTo} copies: switching a flag on in the caller's set afterwards must not
         * reach the curse, because the known flags are derived afresh and must not alias the
         * player's own knowledge set.
         */
        @Test
        @DisplayName("setFlagsTo copies the flags rather than keeping the caller's set")
        void flagsAreCopied() {
            ItemObject known = bare().getItemObject().getKnown();
            Flag<ObjectFlag> given = new Flag<>(ObjectFlag.class);
            given.set(List.of(ObjectFlag.OF_AFRAID));

            known.setFlagsTo(given);
            given.set(List.of(ObjectFlag.OF_IMPAIR_HP));

            assertAll(
                    () -> assertTrue(known.getFlags().has(ObjectFlag.OF_AFRAID)),
                    () -> assertFalse(known.getFlags().has(ObjectFlag.OF_IMPAIR_HP)));
        }

        /**
         * C wipes the known flags before copying, so a second call replaces the first rather than
         * adding to it — a flag the player can no longer read must disappear.
         */
        @Test
        @DisplayName("setFlagsTo wipes before copying, so a flag no longer given disappears")
        void flagsAreWiped() {
            ItemObject known = bare().getItemObject().getKnown();
            Flag<ObjectFlag> first = new Flag<>(ObjectFlag.class);
            first.set(List.of(ObjectFlag.OF_AFRAID));
            Flag<ObjectFlag> second = new Flag<>(ObjectFlag.class);
            second.set(List.of(ObjectFlag.OF_IMPAIR_HP));

            known.setFlagsTo(first);
            known.setFlagsTo(second);

            assertAll(
                    () -> assertFalse(known.getFlags().has(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(known.getFlags().has(ObjectFlag.OF_IMPAIR_HP)));
        }

        /**
         * {@code setElInfo} stores the map it is given, so {@code putElInfo} afterwards writes into
         * the caller's map — the documented hand-over.
         */
        @Test
        @DisplayName("setElInfo stores the caller's map, and put writes into it")
        void elInfoIsStored() {
            ItemObject known = bare().getItemObject().getKnown();
            Map<ElementEnum, ElementInfo> given = new HashMap<>();
            ElementInfo fire = new ElementInfo();
            fire.setResLevel(1);

            known.setElInfo(given);
            known.putElInfo(ElementEnum.ELEM_FIRE, fire);

            assertAll(
                    () -> assertSame(given, known.getElInfo()),
                    () -> assertSame(fire, given.get(ElementEnum.ELEM_FIRE)));
        }

        /**
         * A later {@code setElInfo} replaces the whole view rather than merging, so an element the
         * player has stopped being able to read goes away.
         */
        @Test
        @DisplayName("setElInfo replaces rather than merges")
        void elInfoReplaces() {
            ItemObject known = bare().getItemObject().getKnown();
            ElementInfo fire = new ElementInfo();
            fire.setResLevel(1);
            known.putElInfo(ElementEnum.ELEM_FIRE, fire);

            known.setElInfo(new HashMap<>());

            assertTrue(known.getElInfo().isEmpty());
        }

        /**
         * A curse is only fully known once a resistance it really has is also known, and a known
         * modifier map that does not name a modifier the curse does carry reads as zero for it. Here
         * the curse has strength -2: the empty known map reads zero and mismatches, and a map
         * naming it matches.
         */
        @Test
        @DisplayName("setModifiers stores the map, and replacing it can undo knowledge")
        void modifiersReplace() {
            Curse curse = CurseFixture.curse("weakness", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                    Map.of(ObjectModifier.OM_STR, -2), Map.of(), 0, 0, 0, List.of(),
                    new Flag<>(ObjectFlag.class), "", "", 0);
            ItemObject known = curse.getItemObject().getKnown();
            Map<ObjectModifier, Integer> given = new HashMap<>();
            given.put(ObjectModifier.OM_STR, -2);

            assertFalse(curse.getItemObject().isFullyKnown());
            known.setModifiers(given);
            assertSame(given, known.getModifiers());
            assertTrue(curse.getItemObject().isFullyKnown());
            known.setModifiers(new HashMap<>());
            assertFalse(curse.getItemObject().isFullyKnown());
        }
    }
}
