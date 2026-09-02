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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.util.HashMap;

import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link ObjectKind}'s accessors — the template every object of a type is built from, and the
 * port of C's {@code object_kind} ({@code object.h}).
 *
 * <p>Two things make these worth asserting rather than assuming. The three allocation figures are
 * adjacent {@code int} setters with names differing by three characters, so a transposition compiles
 * and shows up only as an item generated at the wrong depth. And the three combat bonuses are
 * {@link Random} dice rather than numbers, because a kind describes a <em>range</em> — the ignore
 * code compares an item's actual bonus against the worst its kind could have rolled, so reading the
 * wrong one of the three would misjudge every item of that type.
 *
 * @author Rowan Crowther
 */
class ObjectKindAccessorsTest {

    /**
     * The kind under test, fresh for each test since these are all mutable.
     */
    private ObjectKind kind;

    /**
     * A bare kind, as the no-argument constructor leaves it.
     */
    @BeforeEach
    void newKind() {
        kind = new ObjectKind();
    }

    /**
     * The three allocation figures, which decide where in the dungeon a kind appears.
     */
    @Nested
    @DisplayName("allocation figures")
    class Allocation {

        /**
         * Each setter writes its own field. The values are all different so a transposition cannot
         * pass, and they are read back through the constructor-facing getters where those exist.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("each allocation figure is its own")
        void allocationFiguresAreDistinct() throws Exception {
            kind.setAlloc_prob(11);
            kind.setAlloc_min(12);
            kind.setAlloc_max(13);

            assertEquals(11, intField("alloc_prob"));
            assertEquals(12, intField("alloc_min"));
            assertEquals(13, intField("alloc_max"));
        }

        /**
         * The kind index is the registry position, and separate from every allocation figure beside
         * it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the kind index is independent of the allocation figures")
        void kindIndexIsIndependent() throws Exception {
            kind.setAlloc_prob(11);
            kind.setKindIndex(42);

            assertEquals(42, kind.getKindIndex());
            assertEquals(11, intField("alloc_prob"));
        }

        /**
         * The cost is likewise its own field, and the one the unaware-object pricing returns
         * directly.
         */
        @Test
        @DisplayName("the cost round-trips")
        void costRoundTrips() {
            kind.setCost(450);

            assertEquals(450, kind.getCost());
        }

        /**
         * Reads an {@code int} field that has a setter but no getter.
         *
         * @param name the field's name
         * @return its value
         * @throws Exception if the field cannot be reached
         */
        private int intField(String name) throws Exception {
            Field field = ObjectKind.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.getInt(kind);
        }
    }

    /**
     * The three combat bonus dice, which describe a range rather than a value.
     */
    @Nested
    @DisplayName("combat bonus dice")
    class CombatDice {

        /**
         * A fresh kind has none of the three, so a caller reading them has to cope with that — an
         * object built on a bare kind has no rolled bonuses to compare against.
         */
        @Test
        @DisplayName("a bare kind has no bonus dice")
        void bareKindHasNoDice() {
            assertNull(kind.getToH());
            assertNull(kind.getToD());
            assertNull(kind.getToA());
        }

        /**
         * Each of the three is its own field. They are the same type and adjacent in every
         * constructor, which is where a transposition would live; giving each a distinguishable
         * die makes one visible.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the three bonus dice do not share a field")
        void diceAreDistinct() throws Exception {
            setDice("toH", new Random(0, 1, 1, 4, false));
            setDice("toD", new Random(0, 1, 1, 6, false));
            setDice("toA", new Random(0, 1, 1, 8, false));

            assertEquals(4, kind.getToH().getSides());
            assertEquals(6, kind.getToD().getSides());
            assertEquals(8, kind.getToA().getSides());
        }

        /**
         * Writes one of the bonus dice, none of which has a setter.
         *
         * @param name the field's name
         * @param dice the dice to store
         * @throws Exception if the field cannot be reached
         */
        private void setDice(String name, Random dice) throws Exception {
            Field field = ObjectKind.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(kind, dice);
        }
    }

    /**
     * The damage dice, which unlike the bonuses are plain numbers: a kind's damage is fixed, and it
     * is the ego or artifact built on it that varies.
     */
    @Nested
    @DisplayName("damage dice")
    class DamageDice {

        /**
         * A kind built through the data-file constructor starts at {@code 1d1}, which is C's
         * default for a kind with no {@code dice:} line rather than zero — a weapon that rolled no
         * dice at all would deal nothing.
         */
        @Test
        @DisplayName("the data-file constructor defaults the damage to 1d1")
        void damageDefaultsToOne() {
            ObjectKind fromFile = new ObjectKind(null, 0, 0, 0, 0, "test", TValue.TV_SWORD,
                    "long sword", null, false);

            assertEquals(1, fromFile.getDamageDice());
            assertEquals(1, fromFile.getDamageSides());
        }

        /**
         * The count and the faces are separate fields, and the accessors do not cross. Neither has
         * a setter — they are written by the parser — so the values go in by reflection.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("dice count and sides are separate")
        void countAndSidesAreSeparate() throws Exception {
            setInt("damageDice", 3);
            setInt("damageSides", 5);

            assertEquals(3, kind.getDamageDice());
            assertEquals(5, kind.getDamageSides());
        }

        /**
         * Writes an {@code int} field that has a getter but no setter.
         *
         * @param name  the field's name
         * @param value the value to store
         * @throws Exception if the field cannot be reached
         */
        private void setInt(String name, int value) throws Exception {
            Field field = ObjectKind.class.getDeclaredField(name);
            field.setAccessible(true);
            field.setInt(kind, value);
        }
    }

    /**
     * The presentation and knowledge fields.
     */
    @Nested
    @DisplayName("presentation and knowledge")
    class Presentation {

        /**
         * The display character is stored as given. There is no getter — the display code reads the
         * field — so the assertion reads it back by reflection.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the display character is stored as given")
        void characterStored() throws Exception {
            AngbandDisplayCharacter glyph = new AngbandDisplayCharacter('|', ColourEnum.COLOUR_WHITE);
            kind.setCharacter(glyph);

            Field field = ObjectKind.class.getDeclaredField("character");
            field.setAccessible(true);
            assertSame(glyph, field.get(kind));
        }

        /**
         * The flavour is what an unidentified object of this kind is described by. A kind with none
         * is one that needs no disguise — a sword is a sword on sight.
         */
        @Test
        @DisplayName("a bare kind has no flavour")
        void bareKindHasNoFlavour() {
            assertNull(kind.getFlavour());
        }

        /**
         * Whether the player has ever seen this kind starts false and is knowledge, not data: it
         * belongs to the save file rather than to {@code object.txt}.
         */
        @Test
        @DisplayName("a kind starts unseen")
        void startsUnseen() {
            assertFalse(kind.isEverseen());
        }

        /**
         * The recharge dice, which a wand or staff of this kind is given when it is created. Stored
         * as given, and read back by reflection since there is no getter.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the recharge dice are stored as given")
        void timeStored() throws Exception {
            Random recharge = new Random(20, 1, 1, 20, false);
            kind.setTime(recharge);

            Field field = ObjectKind.class.getDeclaredField("time");
            field.setAccessible(true);
            assertSame(recharge, field.get(kind));
        }
    }

    /**
     * The copy, and what it requires of the kind it is given.
     *
     * <p>{@link ObjectKind#copy()} builds the duplicate member by member and calls {@code copy()} on
     * roughly a dozen of them, so it needs a kind whose dice, flavour and display character have all
     * been filled in — that is, one the parser has finished with. A half-built kind fails on the
     * first of them, which is worth pinning: it means the method is for duplicating loaded kinds and
     * not for cloning scratch ones.
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        /**
         * A kind straight from the data-file constructor is not yet copyable — the parser fills the
         * dice afterwards, and the copy dereferences them.
         */
        @Test
        @DisplayName("a half-built kind cannot be copied")
        void halfBuiltKindCannotBeCopied() {
            ObjectKind partial = new ObjectKind(null, 0, 0, 0, 0, "test", TValue.TV_SWORD,
                    "long sword", null, false);

            assertThrows(NullPointerException.class, partial::copy);
        }

        /**
         * With those fields filled in, the copy carries the scalars across and gives the duplicate
         * its own dice.
         */
        @Test
        @DisplayName("a fully built kind copies, with its own dice")
        void fullyBuiltKindCopies() {
            ObjectKind original = new ObjectKind(null, 0, 0, 0, 0, "test", TValue.TV_SWORD,
                    "long sword", null, false);
            fillDice(original);
            original.setCharacter(new AngbandDisplayCharacter('|', ColourEnum.COLOUR_WHITE));
            set(original, "flavour", new Flavour("murky", ColourEnum.COLOUR_WHITE, 0));
            original.setCost(450);
            original.setKindIndex(9);

            ObjectKind duplicate = original.copy();

            assertNotSame(original, duplicate);
            assertEquals(450, duplicate.getCost());
            assertEquals(9, duplicate.getKindIndex());
            assertEquals(TValue.TV_SWORD, duplicate.gettValue());
            assertNotSame(original.getToH(), duplicate.getToH(),
                    "the bonus dice are the copy's own");
            assertEquals(4, duplicate.getToH().getSides());
        }

        /**
         * Fills every dice-valued field the copy dereferences, plus the two flag sets and the
         * modifier map it copies rather than tests.
         *
         * <p>{@link ItemFixture#kindWithDice} covers the four the quality rules read; the rest are
         * the copy's own, and are spelled out here because this is the test that is about the copy.
         *
         * @param kind the kind to fill
         */
        private void fillDice(ObjectKind kind) {
            set(kind, "pVal", new Random(0, 1, 1, 2, false));
            set(kind, "toH", new Random(0, 1, 1, 4, false));
            set(kind, "toD", new Random(0, 1, 1, 6, false));
            set(kind, "toA", new Random(0, 1, 1, 8, false));
            set(kind, "baseDamage", new Random(0, 1, 1, 10, false));
            kind.setTime(new Random(20, 1, 1, 20, false));
            set(kind, "charge", new Random(0, 1, 1, 12, false));
            set(kind, "stackSize", new Random(0, 1, 1, 14, false));
            set(kind, "flags", new Flag<>(ObjectFlag.class));
            set(kind, "ignore", new Flag<>(IgnoreFlag.class));
            set(kind, "modifiers", new HashMap<ObjectModifier, Random>());
        }

    }
}
