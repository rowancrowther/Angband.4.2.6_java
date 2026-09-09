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
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
         * {@code hasFlavour} is the null check on the same field, spelled out because the knowledge
         * code reads it directly rather than testing {@link ObjectKind#getFlavour} for null itself.
         * A bare kind has none.
         */
        @Test
        @DisplayName("a bare kind reports having no flavour")
        void bareKindHasFlavourIsFalse() {
            assertFalse(kind.hasFlavour());
        }

        /**
         * Once a flavour is attached, {@code hasFlavour} reports it — the disguise a sword never
         * needs and a potion does until identified.
         */
        @Test
        @DisplayName("a kind with a flavour reports having one")
        void flavouredKindHasFlavourIsTrue() {
            set(kind, "flavour", new Flavour("murky", ColourEnum.COLOUR_WHITE, 0));

            assertTrue(kind.hasFlavour());
        }

        /**
         * {@code setFlavour} is the port of C's direct {@code kind->flavor = f} field write — the
         * assignment {@code flavor_assign_fixed} and {@code flavor_assign_random} both perform inline,
         * with no dedicated C setter. The flavour given is stored exactly, unmodified.
         */
        @Test
        @DisplayName("setFlavour stores the flavour given")
        void setFlavourStoresGiven() {
            Flavour flavour = new Flavour("murky", ColourEnum.COLOUR_WHITE, 0);

            kind.setFlavour(flavour);

            assertSame(flavour, kind.getFlavour());
        }

        /**
         * The same field write also does the opposite job: {@code flavor_init}'s new-player reset
         * scrubs every kind's flavour back to {@code NULL} ({@code obj-util.c:169}). Passing
         * {@code null} to the setter must clear a previously-attached flavour, not merely leave it
         * unset.
         */
        @Test
        @DisplayName("setFlavour(null) clears a previously-attached flavour")
        void setFlavourNullClears() {
            kind.setFlavour(new Flavour("murky", ColourEnum.COLOUR_WHITE, 0));

            kind.setFlavour(null);

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
         * {@link ObjectKind#setEverSeen} writes the field as given, matching C's direct
         * {@code kind->everseen = true}/{@code false} assignments at every call site — there is no
         * dedicated C setter, so the port takes the value both ways rather than only ever setting
         * {@code true}.
         */
        @Test
        @DisplayName("setEverSeen stores the value given")
        void everSeenStored() {
            kind.setEverSeen(true);
            assertTrue(kind.isEverseen());

            kind.setEverSeen(false);
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

        /**
         * A bare kind starts untried — C's {@code object_kind} is zero-initialised, so
         * {@code kind->tried} is {@code false} until something sets it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a kind starts untried")
        void startsUntried() throws Exception {
            assertFalse(triedField());
        }

        /**
         * {@link ObjectKind#setTried} writes the field as given, matching C's raw
         * {@code kind->tried = true} in {@code object_flavor_tried} — the artifact guard that
         * wrapper adds is not this method's concern.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("setTried stores the value given")
        void triedStored() throws Exception {
            kind.setTried(true);
            assertEquals(true, triedField());

            kind.setTried(false);
            assertEquals(false, triedField());
        }

        /**
         * Reads the {@code tried} field, which has a setter but no getter.
         *
         * @return its value
         * @throws Exception if the field cannot be reached
         */
        private boolean triedField() throws Exception {
            Field field = ObjectKind.class.getDeclaredField("tried");
            field.setAccessible(true);
            return field.getBoolean(kind);
        }

        /**
         * The two autoinscription notes, C's {@code note_aware} and {@code note_unaware}. Neither
         * has a setter — only the data-file constructor and {@link ObjectIgnore} write them — so a
         * bare kind is given values by reflection, as elsewhere in this class.
         */
        @Test
        @DisplayName("a bare kind has neither autoinscription note")
        void bareKindHasNoNotes() {
            assertNull(kind.getNoteAware());
            assertNull(kind.getNoteUnaware());
        }

        /**
         * The aware and unaware notes are separate fields, not a fallback pair: setting one leaves
         * the other {@code null}.
         */
        @Test
        @DisplayName("the aware and unaware notes are independent")
        void notesAreIndependent() {
            set(kind, "noteAware", "{ blessed}");

            assertEquals("{ blessed}", kind.getNoteAware());
            assertNull(kind.getNoteUnaware());
        }

        /**
         * And the same the other way round.
         */
        @Test
        @DisplayName("setting only the unaware note leaves the aware note null")
        void unawareNoteAloneLeavesAwareNull() {
            set(kind, "noteUnaware", "{ tried}");

            assertEquals("{ tried}", kind.getNoteUnaware());
            assertNull(kind.getNoteAware());
        }
    }

    /**
     * Weight, the two per-item recharge/charge dice, and the modifier lookup — the fields
     * {@link ObjectUtils#objectPrep} copies onto every item built from a kind.
     */
    @Nested
    @DisplayName("weight, timing/charge dice and modifiers")
    class WeightTimeChargeAndModifiers {

        /**
         * A bare kind has no weight — C's zero-initialised {@code object_kind} leaves
         * {@code kind->weight} at {@code 0} until {@code object.txt} sets it.
         */
        @Test
        @DisplayName("a bare kind has no weight")
        void bareKindHasNoWeight() {
            assertEquals(0, kind.getWeight());
        }

        /**
         * The weight setter and getter read and write the same field.
         */
        @Test
        @DisplayName("weight round-trips through the setter")
        void weightRoundTrips() {
            kind.setWeight(37);

            assertEquals(37, kind.getWeight());
        }

        /**
         * {@link ObjectKind#getTime} reads back exactly what {@link ObjectKind#setTime} stored,
         * with no copying in between.
         */
        @Test
        @DisplayName("getTime reads back what setTime stored")
        void timeRoundTrips() {
            Random recharge = new Random(20, 1, 1, 20, false);
            kind.setTime(recharge);

            assertSame(recharge, kind.getTime());
        }

        /**
         * A bare kind has no charge dice, since the no-argument constructor never touches the
         * field — unlike {@code weight}, there is no C zero-value to fall back on because the
         * dice are a {@link Random}, not a primitive.
         */
        @Test
        @DisplayName("a bare kind has no charge dice")
        void bareKindHasNoCharge() {
            assertNull(kind.getCharge());
        }

        /**
         * {@link ObjectKind#getCharge} reads back the stored dice as given, with no getter to
         * exercise a setter through — the field is parser-written, so the value goes in by
         * reflection.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("getCharge reads back the stored dice")
        void chargeStored() throws Exception {
            Random charge = new Random(0, 1, 1, 12, false);
            set(kind, "charge", charge);

            assertSame(charge, kind.getCharge());
        }

        /**
         * A modifier the kind actually carries comes back as the exact dice stored for it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("getModifier returns the dice a kind carries")
        void modifierPresentReturnsItsDice() throws Exception {
            Random str = new Random(1, 5, 0, 0, false);
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            modifiers.put(ObjectModifier.OM_STR, str);
            set(kind, "modifiers", modifiers);

            assertSame(str, kind.getModifier(ObjectModifier.OM_STR));
        }

        /**
         * C keeps every modifier in a fixed {@code OBJ_MOD_MAX}-length array
         * ({@code object.h}), zero-allocated before parsing; {@code parse_object_values}
         * ({@code obj-init.c}) only overwrites the indices a kind's {@code values:} line names,
         * so an index it never mentions still reads back as a valid, zero-value
         * {@code random_value} rather than as an absence. Real data bears this out — most kinds
         * in {@code object.txt} carry no {@code values:} line at all, let alone one naming every
         * modifier.
         *
         * <p>This kind keeps modifiers in a {@link Map} instead, populated only for the
         * modifiers a {@code values:} line names, so a plain lookup would return {@code null}
         * for the common case above — which is exactly what previously made
         * {@link ObjectUtils#objectPrep} throw a {@link NullPointerException} for such a kind.
         * {@link ObjectKind#getModifier} now falls back to a fresh zero-dice {@link Random}
         * instead, whose {@link Random#randCalc} comes out {@code 0} for every aspect that does
         * not require loaded world data to evaluate, matching what C's zeroed array slot would
         * compute. ({@code AVERAGE} routes through {@link uk.co.jackoftradesltd.middle.game.globals.GameConstants#getWorldMaxDepth}
         * and is left to whatever exercises that path with the game data actually loaded.)
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("getModifier falls back to a zero dice for a modifier the kind does not carry")
        void modifierAbsentFallsBackToZeroDice() throws Exception {
            set(kind, "modifiers", new HashMap<ObjectModifier, Random>());

            Random fallback = kind.getModifier(ObjectModifier.OM_STEALTH);

            assertEquals(0, fallback.randCalc(50, DamageAspect.MINIMIZE));
            assertEquals(0, fallback.randCalc(50, DamageAspect.MAXIMIZE));
            assertEquals(0, fallback.randCalc(50, DamageAspect.EXTREMIFY));
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

    /**
     * The slays, brands and curses every item of this kind carries — {@link ObjectKind#getSlays},
     * {@link ObjectKind#getBrands} and {@link ObjectKind#getCurses}. Both constructors initialise
     * these to empty rather than {@code null} ({@code ObjectKind.java:289-291,355-357,500-502}), so
     * unlike the {@code time}/{@code charge} dice a bare kind still has something to iterate.
     */
    @Nested
    @DisplayName("slays, brands and curses")
    class SlaysBrandsAndCurses {

        /**
         * A bare kind (either constructor) has none of the three, but a fresh, non-null, empty
         * collection rather than {@code null} — there is no C zero-value here since C's
         * {@code kind->brands}/{@code slays} are {@code NULL} pointers until parsed, but this port
         * always has a container to add to.
         */
        @Test
        @DisplayName("a bare kind has empty slays, brands and curses")
        void bareKindHasEmptyCollections() {
            assertTrue(kind.getSlays().isEmpty());
            assertTrue(kind.getBrands().isEmpty());
            assertTrue(kind.getCurses().isEmpty());
        }

        /**
         * {@link ObjectKind#getSlays} reads back exactly what the field holds, with no copying.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("getSlays reads back the stored set")
        void getSlaysReadsBackStoredSet() throws Exception {
            Slay slay = new Slay("EVIL_2", "evil", null, "smite", "pierces",
                    null, 2, 2, 5);
            Set<Slay> slays = new HashSet<>();
            slays.add(slay);
            set(kind, "slays", slays);

            assertSame(slays, kind.getSlays());
            assertTrue(kind.getSlays().contains(slay));
        }

        /**
         * {@link ObjectKind#getBrands} reads back exactly what the field holds, with no copying.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("getBrands reads back the stored set")
        void getBrandsReadsBackStoredSet() throws Exception {
            Brand brand = new Brand("FIRE", "fire", "burns", null, null, 3, 3, 5);
            Set<Brand> brands = new HashSet<>();
            brands.add(brand);
            set(kind, "brands", brands);

            assertSame(brands, kind.getBrands());
            assertTrue(kind.getBrands().contains(brand));
        }

        /**
         * {@link ObjectKind#getCurses} reads back exactly what the field holds — including the
         * {@link CurseData} each curse maps to — with no copying.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("getCurses reads back the stored map")
        void getCursesReadsBackStoredMap() throws Exception {
            Curse curse = new Curse("siren", null, 0, null, null, null, null,
                    0, 0, 0, null, null, null, null);
            CurseData data = new CurseData(3, 0);
            Map<Curse, CurseData> curses = new HashMap<>();
            curses.put(curse, data);
            set(kind, "curses", curses);

            assertSame(curses, kind.getCurses());
            assertSame(data, kind.getCurses().get(curse));
        }
    }

    /**
     * {@link ObjectKind#wipeIgnoreFlags}, the port of the {@code kind->ignore = 0} line shared by
     * C's {@code kind_ignore_clear} and {@code ignore_birth_init}.
     */
    @Nested
    @DisplayName("wipeIgnoreFlags")
    class WipeIgnoreFlags {

        /**
         * A kind with neither ignore flag set clearing to no-op, matching {@code 0 = 0} in C.
         */
        @Test
        @DisplayName("wiping a kind with no flags set leaves both flags off")
        void wipingWithNoFlagsSetLeavesBothOff() {
            kind.wipeIgnoreFlags();

            assertFalse(kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_AWARE));
            assertFalse(kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_UNAWARE));
        }

        /**
         * Only {@code IGNORE_IF_AWARE} set beforehand still comes back off, not just unchanged -
         * this is the branch that would catch a wipe implemented as "off the flags I know are on"
         * rather than a true clear.
         */
        @Test
        @DisplayName("wiping clears a lone IGNORE_IF_AWARE flag")
        void wipingClearsLoneAwareFlag() {
            kind.setIgnoreFlag(IgnoreFlag.IGNORE_IF_AWARE);

            kind.wipeIgnoreFlags();

            assertFalse(kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_AWARE));
            assertFalse(kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_UNAWARE));
        }

        /**
         * Both flags set beforehand, mirroring C's {@code kind->ignore == IGNORE_IF_AWARE |
         * IGNORE_IF_UNAWARE} (0x03) going to 0 in one assignment rather than bit by bit.
         */
        @Test
        @DisplayName("wiping clears both flags at once")
        void wipingClearsBothFlags() {
            kind.setIgnoreFlag(IgnoreFlag.IGNORE_IF_AWARE);
            kind.setIgnoreFlag(IgnoreFlag.IGNORE_IF_UNAWARE);

            kind.wipeIgnoreFlags();

            assertFalse(kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_AWARE));
            assertFalse(kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_UNAWARE));
        }
    }
}
