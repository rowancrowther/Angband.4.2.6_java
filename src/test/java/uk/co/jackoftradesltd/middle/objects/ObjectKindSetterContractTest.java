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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the contract of the {@link ObjectKind} setters that {@code ArtifactAssembler} uses in place
 * of the old artifact constructor, and of the no-argument shell they work on.
 *
 * <p>The expected values are read off C, not off the port. {@code write_dummy_object_record}
 * ({@code obj-init.c}) names the kind with {@code strnfmt(mod_name, ..., "& %s~", name)}, points
 * {@code base} at {@code kb_info[tval]}, {@code memcpy}s the base's {@code el_info}, and sets flags
 * and kind flags one set at a time with {@code of_copy}, {@code kf_copy} and {@code kf_on}. A
 * {@code memset}-zeroed kind has no flags, zero dice and no ignore setting, which is what the
 * no-argument shell must read back as. The ports of those single-bit operations are
 * {@code setFlag} and {@code setKindFlag}: each turns one bit on, is a no-op on a bit already set,
 * and touches only its own set.
 *
 * <p>Where the port stores a reference rather than copying, as {@code setElInfo} does, the test
 * pins the contract the Javadoc states, so a caller that relies on it is not surprised.
 *
 * <p>Class ObjectKindSetterContractTest coded on 261107, commented in full on 261107.
 *
 * @author Rowan Crowther
 */
class ObjectKindSetterContractTest {

    /**
     * A glyph to give a kind so that {@link ObjectKind#copy} has something to copy.
     *
     * @return a white {@code '|'}
     */
    private static AngbandDisplayCharacter glyph() {
        return new AngbandDisplayCharacter('|', ColourEnum.COLOUR_WHITE);
    }

    /**
     * What the no-argument constructor sets up: a zeroed kind, as C's {@code memset} leaves one.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the no-argument shell")
    class Shell {

        /**
         * A {@code memset}-zeroed {@code random_value} is {@code {0, 0, 0, 0}}; the shell's dice must
         * be zero values, not {@code null}, so a caller can read them before any are assigned.
         */
        @Test
        @DisplayName("every dice field is a zero value, never null")
        void diceAreZero() {
            ObjectKind kind = new ObjectKind();

            assertAll(
                    () -> assertNotNull(kind.getTime()),
                    () -> assertEquals(0, kind.getTime().getBase()),
                    () -> assertEquals(0, kind.getTime().getDice()),
                    () -> assertEquals(0, kind.getToH().getBase()),
                    () -> assertEquals(0, kind.getToD().getBase()),
                    () -> assertEquals(0, kind.getToA().getBase()),
                    () -> assertEquals(0, kind.getPVal().getBase()),
                    () -> assertEquals(0, kind.getCharge().getBase()),
                    () -> assertEquals(0, kind.getModifier(ObjectModifier.OM_STR).getBase()));
        }

        /**
         * Zeroed flag arrays, no brand, slay or curse, no ignore setting, and not an instanced
         * artifact's kind ({@code kidx} is 0, below {@code ordinary_kind_max}).
         */
        @Test
        @DisplayName("has no flags, no brands, slays or curses, no ignore setting, and is not special")
        void emptyCollections() {
            ObjectKind kind = new ObjectKind();

            assertAll(
                    () -> assertFalse(kind.getFlags().has(ObjectFlag.OF_FREE_ACT)),
                    () -> assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)),
                    () -> assertTrue(kind.getBrands().isEmpty()),
                    () -> assertTrue(kind.getSlays().isEmpty()),
                    () -> assertTrue(kind.getCurses().isEmpty()),
                    () -> assertTrue(kind.getActivations().isEmpty()),
                    () -> assertTrue(kind.getEffect().isEmpty()),
                    () -> assertFalse(kind.isIgnoredAware()),
                    () -> assertFalse(kind.isIgnoredUnaware()),
                    () -> assertFalse(kind.isSpecialArtifactKind()));
        }

        /**
         * The Javadoc says only the display character stands between the shell and a copy: the
         * dice, maps and sets are all in place.
         */
        @Test
        @DisplayName("can be copied once it has a display character, and not before")
        void copyNeedsOnlyTheGlyph() {
            ObjectKind kind = new ObjectKind();

            assertThrows(NullPointerException.class, kind::copy);

            kind.setCharacter(glyph());
            ObjectKind duplicate = kind.copy();

            assertAll(
                    () -> assertNotSame(kind, duplicate),
                    () -> assertNotNull(duplicate.getTime()),
                    () -> assertNotSame(kind.getTime(), duplicate.getTime()));
        }

        /**
         * The ten-argument constructor stores its glyph argument as given. With one, the kind
         * copies; with {@code null} it cannot, because of the glyph and not the dice.
         */
        @Test
        @DisplayName("a ten-argument kind copies when given a glyph and fails only when given none")
        void tenArgumentGlyph() {
            ObjectKind withGlyph = new ObjectKind(glyph(), 0, 0, 0, 0, "test", TValue.TV_SWORD,
                    "long sword", null, false);
            ObjectKind withoutGlyph = new ObjectKind(null, 0, 0, 0, 0, "test", TValue.TV_SWORD,
                    "long sword", null, false);

            assertDoesNotThrow(withGlyph::copy);
            assertThrows(NullPointerException.class, withoutGlyph::copy);
        }
    }

    /**
     * {@code setsValueName}: the {@code "& %s~"} of {@code write_dummy_object_record}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("setsValueName")
    class SetsValueName {

        /**
         * {@code "& %s~"} with the name substituted.
         */
        @Test
        @DisplayName("builds \"& <name>~\" and keeps the bare name")
        void buildsName() {
            ObjectKind kind = new ObjectKind();

            kind.setsValueName("Phial of Galadriel");

            assertEquals("& Phial of Galadriel~", kind.getName());
            assertEquals("Phial of Galadriel", kind.getsValueName());
        }

        /**
         * C builds the name into a fresh buffer, so whatever name the kind had before is replaced.
         */
        @Test
        @DisplayName("replaces a name the kind already had")
        void overwritesName() {
            ObjectKind kind = new ObjectKind();
            kind.setsValueName("First");

            kind.setsValueName("Second");

            assertEquals("& Second~", kind.getName());
            assertEquals("Second", kind.getsValueName());
        }

        /**
         * {@code strnfmt} with an empty {@code %s} gives {@code "& ~"}; nothing is checked.
         */
        @Test
        @DisplayName("an empty name gives \"& ~\"")
        void emptyName() {
            ObjectKind kind = new ObjectKind();

            kind.setsValueName("");

            assertEquals("& ~", kind.getName());
            assertEquals("", kind.getsValueName());
        }

        /**
         * The sval is a separate step in C (the {@code num_svals} count), so naming does not move it.
         */
        @Test
        @DisplayName("leaves the numeric sval alone")
        void leavesSval() {
            ObjectKind kind = new ObjectKind();
            kind.setsVal(5);

            kind.setsValueName("Test");

            assertEquals(5, kind.getsVal());
        }
    }

    /**
     * {@code setElInfo}: the stand-in for {@code memcpy} of {@code el_info}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("setElInfo")
    class SetElInfo {

        /**
         * The map is kept as given, so the entry read back is the very instance passed in.
         */
        @Test
        @DisplayName("keeps the entries it was given, not copies of them")
        void keepsGivenEntries() {
            ElementInfo fire = new ElementInfo();
            fire.on(ElementInfoEnum.EL_INFO_HATES);
            Map<ElementEnum, ElementInfo> given = new HashMap<>();
            given.put(ElementEnum.ELEM_FIRE, fire);
            ObjectKind kind = new ObjectKind();

            kind.setElInfo(given);

            assertSame(fire, kind.getElInfo(ElementEnum.ELEM_FIRE));
        }

        /**
         * An element the map does not name reads as a zeroed {@code element_info}: no flags.
         */
        @Test
        @DisplayName("an element not in the map reads back as zero")
        void absentElementIsZero() {
            ObjectKind kind = new ObjectKind();
            kind.setElInfo(new HashMap<>());

            assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_HATES));
            assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        }

        /**
         * A second call replaces the map outright; entries of the first are gone.
         */
        @Test
        @DisplayName("a second call replaces the first map")
        void replacesMap() {
            ElementInfo acid = new ElementInfo();
            acid.on(ElementInfoEnum.EL_INFO_IGNORE);
            Map<ElementEnum, ElementInfo> first = new HashMap<>();
            first.put(ElementEnum.ELEM_ACID, acid);
            ObjectKind kind = new ObjectKind();
            kind.setElInfo(first);

            kind.setElInfo(new HashMap<>());

            assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        }
    }

    /**
     * {@code setSpecialArtifactKind}: C has no field, so this is a plain flag write.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("setSpecialArtifactKind")
    class SetSpecialArtifactKind {

        /**
         * A kind is not special until told it is, and can be told it is not.
         */
        @Test
        @DisplayName("goes on and comes off")
        void roundTrips() {
            ObjectKind kind = new ObjectKind();
            assertFalse(kind.isSpecialArtifactKind());

            kind.setSpecialArtifactKind(true);
            assertTrue(kind.isSpecialArtifactKind());

            kind.setSpecialArtifactKind(false);
            assertFalse(kind.isSpecialArtifactKind());
        }
    }

    /**
     * {@code setBase}: {@code dummy->base = &kb_info[dummy->tval]}, a bare pointer write.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("setBase")
    class SetBase {

        /**
         * A base to hand over; its contents do not matter to the setter.
         *
         * @return a sword base
         */
        private ObjectBase sword() {
            return new ObjectBase(TValue.TV_SWORD, "sword", ColourEnum.COLOUR_WHITE,
                    new Flag<>(ObjectKindFlag.class, ObjectKindFlag.KF_EASY_KNOW), new HashMap<>(), 0, 40,
                    new Flag<>(ObjectFlag.class, ObjectFlag.OF_FREE_ACT));
        }

        /**
         * The base is kept by reference, and nothing else on the kind moves with it: not the tval,
         * the sval, the object flags or the kind flags.
         */
        @Test
        @DisplayName("keeps the base itself and moves nothing else")
        void keepsBaseOnly() {
            ObjectBase base = sword();
            ObjectKind kind = new ObjectKind();
            kind.setsVal(3);

            kind.setBase(base);

            assertAll(
                    () -> assertSame(base, kind.getBase()),
                    () -> assertNull(kind.gettValue()),
                    () -> assertEquals(3, kind.getsVal()),
                    () -> assertFalse(kind.getFlags().has(ObjectFlag.OF_FREE_ACT)),
                    () -> assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW)));
        }

        /**
         * No check either side: {@code null} clears it, as an assignment of {@code NULL} would.
         */
        @Test
        @DisplayName("null clears the base")
        void nullClears() {
            ObjectKind kind = new ObjectKind();
            kind.setBase(sword());

            kind.setBase(null);

            assertNull(kind.getBase());
        }
    }

    /**
     * {@code setFlag} and {@code setKindFlag}: {@code of_on} and {@code kf_on}, one bit each.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("setFlag and setKindFlag")
    class SingleBitSetters {

        /**
         * Setting an object flag turns that bit on and leaves the kind flags empty.
         */
        @Test
        @DisplayName("setFlag turns on an object flag and no kind flag")
        void setFlagOnlyTouchesObjectFlags() {
            ObjectKind kind = new ObjectKind();

            kind.setFlag(ObjectFlag.OF_FREE_ACT);

            assertAll(
                    () -> assertTrue(kind.getFlags().has(ObjectFlag.OF_FREE_ACT)),
                    () -> assertFalse(kind.getFlags().has(ObjectFlag.OF_SEE_INVIS)),
                    () -> assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)));
        }

        /**
         * Setting a kind flag turns that bit on and leaves the object flags empty.
         */
        @Test
        @DisplayName("setKindFlag turns on a kind flag and no object flag")
        void setKindFlagOnlyTouchesKindFlags() {
            ObjectKind kind = new ObjectKind();

            kind.setKindFlag(ObjectKindFlag.KF_INSTA_ART);

            assertAll(
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)),
                    () -> assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD)),
                    () -> assertFalse(kind.getFlags().has(ObjectFlag.OF_FREE_ACT)));
        }

        /**
         * Turning on a bit that is already on changes nothing, and earlier bits survive later ones;
         * this is how the assembler copies a whole set one flag at a time.
         */
        @Test
        @DisplayName("setting a flag twice is harmless, and flags accumulate")
        void idempotentAndCumulative() {
            ObjectKind kind = new ObjectKind();

            kind.setFlag(ObjectFlag.OF_FREE_ACT);
            kind.setFlag(ObjectFlag.OF_FREE_ACT);
            kind.setFlag(ObjectFlag.OF_SEE_INVIS);
            kind.setKindFlag(ObjectKindFlag.KF_GOOD);
            kind.setKindFlag(ObjectKindFlag.KF_GOOD);
            kind.setKindFlag(ObjectKindFlag.KF_INSTA_ART);

            assertAll(
                    () -> assertTrue(kind.getFlags().has(ObjectFlag.OF_FREE_ACT)),
                    () -> assertTrue(kind.getFlags().has(ObjectFlag.OF_SEE_INVIS)),
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD)),
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)));
        }
    }

    /**
     * {@code setIgnoredAware}: the port of {@code kind_ignore_when_aware}, with a boolean.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("setIgnoredAware")
    class SetIgnoredAware {

        /**
         * C ORs in {@code IGNORE_IF_AWARE} alone, so the unaware bit is not disturbed either way.
         */
        @Test
        @DisplayName("sets and clears only the aware bit")
        void touchesOnlyAwareBit() {
            ObjectKind kind = new ObjectKind();
            kind.setIgnoredUnaware(true);

            kind.setIgnoredAware(true);
            assertTrue(kind.isIgnoredAware());
            assertTrue(kind.isIgnoredUnaware());

            kind.setIgnoredAware(false);
            assertFalse(kind.isIgnoredAware());
            assertTrue(kind.isIgnoredUnaware());
        }
    }
}
