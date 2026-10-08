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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests what {@link ObjectKind}'s constructors set up, and what {@link ObjectKind#copy} carries
 * across, against the C routines each one follows.
 *
 * <p>The expected values are read off C, not off the port. The ten-argument constructor is checked
 * against {@code write_book_kind} ({@code init.c}): a zeroed kind given one damage die of one side,
 * weight 30, a union with the base's kind flags, and, for a dungeon book only, {@code EL_INFO_IGNORE}
 * on the four elements from {@code ELEM_BASE_MIN} up to but not including {@code ELEM_BASE_MAX}
 * (acid, lightning, fire, cold) and {@code KF_GOOD}. The artifact constructor is checked against
 * {@code write_dummy_object_record} ({@code obj-init.c}): the name {@code "& %s~"}, the artifact's
 * level, the base's tval, a {@code '*'} glyph in red, copies of the base's flags, kind flags and
 * element info with {@code KF_INSTA_ART} added, and everything else zero. The sub-type name is
 * checked against the stripping {@code lookup_sval} does through {@code obj_desc_name_format}.
 *
 * <p>The long constructor does not union the base's kind flags, as C's {@code finish_parse_object}
 * does when it copies the parsed list into {@code k_info}; the object loader does that before it
 * calls it, so a test here pins the contract that the set is stored exactly as given.
 *
 * <p>Class ObjectKindConstructorsTest coded on 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class ObjectKindConstructorsTest {

    /**
     * A base whose kind flags hold {@code KF_EASY_KNOW}, whose object flags are empty, and which
     * hates fire.
     *
     * @return the constructed base
     */
    private static ObjectBase base() {
        Flag<ObjectKindFlag> kindFlags = new Flag<>(ObjectKindFlag.class, ObjectKindFlag.KF_EASY_KNOW);
        return new ObjectBase(TValue.TV_SWORD, "sword", ColourEnum.COLOUR_WHITE, kindFlags,
                new Flag<>(ElementEnum.class, ElementEnum.ELEM_FIRE), 0, 40);
    }

    /**
     * A partly-specified kind of the sort {@code write_book_kind} builds.
     *
     * @param base      the base, or {@code null} for none
     * @param isDungeon whether to build a dungeon book
     * @return the constructed kind
     */
    private static ObjectKind book(ObjectBase base, boolean isDungeon) {
        return new ObjectKind(null, 25, 5, 1, 30, "Magic for Beginners", TValue.TV_MAGIC_BOOK,
                "Magic for Beginners", base, isDungeon);
    }

    /**
     * A kind synthesised to back a special artifact of level 7.
     *
     * @param base the base to build it from
     * @return the constructed kind
     */
    private static ObjectKind artifactKind(ObjectBase base) {
        Artifact artifact = new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0",
                0, 0, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(),
                Map.of(), 7, 0, 0, 0, null, null, null);
        return new ObjectKind(artifact, "Test", base);
    }

    /**
     * A kind from the long constructor, with the given name and kind flags and everything else
     * empty.
     *
     * @param name      the kind's name, markers included
     * @param kindFlags the kind flags to pass
     * @return the constructed kind
     */
    private static ObjectKind longKind(String name, Flag<ObjectKindFlag> kindFlags) {
        return new ObjectKind(name, "", base(), 0, Random.Zero(), Random.Zero(), Random.Zero(),
                Random.Zero(), 0, Random.Zero(), 1, 1, 30, 0, new Flag<>(ObjectFlag.class),
                kindFlags, new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(),
                new HashMap<>(), null, 0, 0, 0, 0, new ArrayList<>(), new ArrayList<>(), null,
                null, Random.Zero(), Random.Zero(), 0, Random.Zero(), null, null, null, false,
                false, new Flag<>(IgnoreFlag.class), false, TValue.TV_SWORD, 0);
    }

    /**
     * The ten-argument constructor, which follows {@code write_book_kind}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the partly-specified constructor (write_book_kind)")
    class PartlySpecified {

        /**
         * C zeroes the kind, then sets one damage die of one side and weight 30. The cost, level and
         * allocation range are this port's arguments, stored as given.
         */
        @Test
        @DisplayName("seeds 1d1 damage and weight 30, and stores its arguments")
        void defaults() {
            ObjectKind kind = book(base(), false);

            assertAll(
                    () -> assertEquals(1, kind.getDamageDice()),
                    () -> assertEquals(1, kind.getDamageSides()),
                    () -> assertEquals(30, kind.getWeight()),
                    () -> assertEquals(25, kind.getCost()),
                    () -> assertEquals("Magic for Beginners", kind.getName()),
                    () -> assertEquals("Magic for Beginners", kind.getsValueName()),
                    () -> assertEquals(TValue.TV_MAGIC_BOOK, kind.gettValue()),
                    () -> assertFalse(kind.isSpecialArtifactKind()),
                    () -> assertFalse(kind.isAware()),
                    () -> assertFalse(kind.isEverseen()));
        }

        /**
         * C's {@code memset} leaves every {@code random_value} zero, so none may be {@code null}.
         */
        @Test
        @DisplayName("every dice field is a zero value, not null")
        void diceAreZero() {
            ObjectKind kind = book(base(), false);

            assertAll(
                    () -> assertNotNull(kind.getPVal()),
                    () -> assertEquals(0, kind.getToH().getBase()),
                    () -> assertEquals(0, kind.getToD().getBase()),
                    () -> assertEquals(0, kind.getToA().getBase()),
                    () -> assertEquals(0, kind.getTime().getBase()),
                    () -> assertEquals(0, kind.getCharge().getBase()));
        }

        /**
         * {@code kf_union(kind->kind_flags, kb_info[kind->tval].kind_flags)}: the base's flags are
         * on the kind even though the kind names none of its own.
         */
        @Test
        @DisplayName("inherits the base's kind flags")
        void inheritsBaseKindFlags() {
            ObjectKind kind = book(base(), false);

            assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
        }

        /**
         * A union copies; it does not alias. The dungeon branch turns {@code KF_GOOD} on the kind
         * only, so the base must stay as it was.
         */
        @Test
        @DisplayName("keeps its own kind-flag set, so a dungeon book's KF_GOOD does not reach the base")
        void kindFlagsAreItsOwn() {
            ObjectBase base = base();

            book(base, true);

            assertFalse(base.getKindFlags().has(ObjectKindFlag.KF_GOOD));
        }

        /**
         * A kind with no base has no flags to inherit. The port tolerates that where C, which always
         * has a {@code kb_info} entry, cannot arise.
         */
        @Test
        @DisplayName("a null base is tolerated and contributes no kind flags")
        void nullBase() {
            assertDoesNotThrow(() -> book(null, false));
            assertTrue(book(null, false).getKindFlags().isEmpty());
        }

        /**
         * The ignore, good and element changes sit inside {@code if (book->dungeon)}.
         */
        @Test
        @DisplayName("an ordinary book is not good and ignores no element")
        void ordinaryBook() {
            ObjectKind kind = book(base(), false);

            assertAll(
                    () -> assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD)),
                    () -> assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE)),
                    () -> assertFalse(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE)));
        }

        /**
         * The loop runs {@code ELEM_BASE_MIN} (acid) up to but not including {@code ELEM_BASE_MAX}
         * ({@code ELEM_COLD + 1}), so poison, the first element after cold, is outside it.
         */
        @Test
        @DisplayName("a dungeon book is good and ignores acid, lightning, fire and cold only")
        void dungeonBook() {
            ObjectKind kind = book(base(), true);

            assertAll(
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD)),
                    () -> assertTrue(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE)),
                    () -> assertTrue(kind.getElInfo(ElementEnum.ELEM_ELEC).has(ElementInfoEnum.EL_INFO_IGNORE)),
                    () -> assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE)),
                    () -> assertTrue(kind.getElInfo(ElementEnum.ELEM_COLD).has(ElementInfoEnum.EL_INFO_IGNORE)),
                    () -> assertFalse(kind.getElInfo(ElementEnum.ELEM_POIS).has(ElementInfoEnum.EL_INFO_IGNORE)));
        }

        /**
         * The dungeon branch adds to the inherited flags; it does not replace them.
         */
        @Test
        @DisplayName("a dungeon book keeps the base's flags as well as KF_GOOD")
        void dungeonBookKeepsInheritedFlags() {
            ObjectKind kind = book(base(), true);

            assertAll(
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW)),
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD)));
        }

        /**
         * C's {@code memset} leaves {@code kind->ignore} 0, so the ignore queries are answerable
         * and false. Before the constructor set the field they threw.
         */
        @Test
        @DisplayName("starts with no ignore setting, and the ignore queries answer")
        void ignoreStartsClear() {
            ObjectKind kind = book(base(), false);

            assertAll(
                    () -> assertFalse(kind.isIgnoredAware()),
                    () -> assertFalse(kind.isIgnoredUnaware()),
                    () -> assertFalse(kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_AWARE)));
        }
    }

    /**
     * The artifact constructor, which follows {@code write_dummy_object_record}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the artifact constructor (write_dummy_object_record)")
    class ArtifactConstructor {

        /**
         * {@code strnfmt(mod_name, ..., "& %s~", name)}, and the sub-type name is the bare name.
         */
        @Test
        @DisplayName("the name is \"& <sval>~\" and the sub-type name is the bare sval")
        void naming() {
            ObjectKind kind = artifactKind(base());

            assertAll(
                    () -> assertEquals("& Test~", kind.getName()),
                    () -> assertEquals("Test", kind.getsValueName()));
        }

        /**
         * {@code dummy->tval = art->tval} and {@code dummy->base = &kb_info[dummy->tval]}. The level
         * the constructor copies from the artifact has no getter on the kind, so it is not checked
         * here.
         */
        @Test
        @DisplayName("takes the base's tval and keeps the base itself")
        void tvalAndBase() {
            ObjectBase base = base();
            ObjectKind kind = artifactKind(base);

            assertEquals(TValue.TV_SWORD, kind.gettValue());
            assertSame(base, kind.getBase());
        }

        /**
         * {@code dummy->d_char = '*'; dummy->d_attr = COLOUR_RED}.
         */
        @Test
        @DisplayName("is drawn as a red asterisk until the artifact's graphics line says otherwise")
        void defaultGlyph() {
            ObjectKind kind = artifactKind(base());

            assertEquals('*', kind.getCharacter().getCharacter());
            assertEquals(ColourEnum.COLOUR_RED, kind.getCharacter().getAttributeColour());
        }

        /**
         * {@code kf_copy} from the base, then {@code kf_on(KF_INSTA_ART)}. The mark goes on the
         * kind's copy, never the base's own set.
         */
        @Test
        @DisplayName("copies the base's kind flags and adds KF_INSTA_ART to the copy only")
        void kindFlags() {
            ObjectBase base = base();
            ObjectKind kind = artifactKind(base);

            assertAll(
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW)),
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)),
                    () -> assertFalse(base.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)));
        }

        /**
         * {@code of_copy(dummy->flags, kb_info[i].flags)}: a snapshot at construction. Later
         * changes to the base are not seen.
         */
        @Test
        @DisplayName("copies the base's object flags as they stand at construction")
        void objectFlags() {
            ObjectBase base = base();
            base.getFlags().on(ObjectFlag.OF_FREE_ACT);

            ObjectKind kind = artifactKind(base);
            base.getFlags().on(ObjectFlag.OF_SEE_INVIS);

            assertAll(
                    () -> assertTrue(kind.getFlags().has(ObjectFlag.OF_FREE_ACT)),
                    () -> assertFalse(kind.getFlags().has(ObjectFlag.OF_SEE_INVIS)));
        }

        /**
         * {@code memcpy} of the base's {@code el_info}: the kind starts with the base's entries but
         * owns them, so changing one does not reach the base.
         */
        @Test
        @DisplayName("copies the base's element info, and owns the copy")
        void elementInfo() {
            ObjectBase base = base();
            ObjectKind kind = artifactKind(base);

            assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_HATES));

            kind.getElInfo(ElementEnum.ELEM_FIRE).on(ElementInfoEnum.EL_INFO_IGNORE);

            assertFalse(base.getElementMap().get(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        }

        /**
         * Everything {@code write_dummy_object_record} does not set stays at {@code memset}'s zero.
         * The weight and cost are set afterwards by {@code parse_artifact_weight} and
         * {@code parse_artifact_cost}, not here.
         */
        @Test
        @DisplayName("everything else is zero, with no sval until registered")
        void zeroed() {
            ObjectKind kind = artifactKind(base());

            assertAll(
                    () -> assertEquals(0, kind.getWeight()),
                    () -> assertEquals(0, kind.getCost()),
                    () -> assertEquals(0, kind.getAc()),
                    () -> assertEquals(0, kind.getDamageDice()),
                    () -> assertEquals(0, kind.getDamageSides()),
                    () -> assertEquals(0, kind.getsVal()),
                    () -> assertEquals(0, kind.getKindIndex()),
                    () -> assertEquals(0, kind.getToH().getBase()),
                    () -> assertFalse(kind.hasFlavour()),
                    () -> assertTrue(kind.getActivations().isEmpty()),
                    () -> assertTrue(kind.getEffect().isEmpty()));
        }

        /**
         * The ignore settings are answerable and clear, as {@code memset}'s zero byte is.
         */
        @Test
        @DisplayName("starts with no ignore setting")
        void ignoreStartsClear() {
            ObjectKind kind = artifactKind(base());

            assertFalse(kind.isIgnoredAware());
            assertFalse(kind.isIgnoredUnaware());
        }
    }

    /**
     * What the long constructor derives rather than takes.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the long constructor")
    class LongConstructor {

        /**
         * {@code lookup_sval} runs {@code obj_desc_name_format} over each kind's name before it
         * compares: {@code '&'} and the spaces after it are dropped, and with no plural wanted so is
         * {@code '~'}.
         */
        @Test
        @DisplayName("the sub-type name is the name without its & and ~ markers")
        void subTypeNameIsStripped() {
            assertAll(
                    () -> assertEquals("Long Sword", longKind("& Long Sword~", new Flag<>(ObjectKindFlag.class)).getsValueName()),
                    () -> assertEquals("Flask of Oil", longKind("Flask~ of Oil", new Flag<>(ObjectKindFlag.class)).getsValueName()),
                    () -> assertEquals("Dagger", longKind("Dagger", new Flag<>(ObjectKindFlag.class)).getsValueName()));
        }

        /**
         * C builds the union in {@code finish_parse_object}; the port's loader does it before
         * calling the constructor, so the constructor itself must neither add to nor drop from what
         * it is given. The base here holds {@code KF_EASY_KNOW}, and the kind does not gain it.
         */
        @Test
        @DisplayName("stores the kind flags exactly as given, without adding the base's")
        void kindFlagsAsGiven() {
            ObjectKind kind = longKind("& Long Sword~", new Flag<>(ObjectKindFlag.class, ObjectKindFlag.KF_GOOD));

            assertAll(
                    () -> assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD)),
                    () -> assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW)));
        }

        /**
         * The numeric sval and the special-artifact mark are not arguments: they are zero and false
         * until the registry and the artifact constructor say otherwise.
         */
        @Test
        @DisplayName("starts with no sval and is not a special artifact kind")
        void notRegisteredNotSpecial() {
            ObjectKind kind = longKind("& Long Sword~", new Flag<>(ObjectKindFlag.class));

            assertEquals(0, kind.getsVal());
            assertFalse(kind.isSpecialArtifactKind());
        }
    }

    /**
     * What {@link ObjectKind#copy} carries across for a fully built kind.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        /**
         * The special-artifact mark is a field like any other, and a copy that dropped it would
         * make the player unsure of an item that is its own artifact.
         */
        @Test
        @DisplayName("a copied artifact kind is still a special artifact kind with KF_INSTA_ART")
        void keepsArtifactMark() {
            ObjectKind copy = artifactKind(base()).copy();

            assertAll(
                    () -> assertTrue(copy.isSpecialArtifactKind()),
                    () -> assertTrue(copy.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)),
                    () -> assertEquals("& Test~", copy.getName()),
                    () -> assertEquals('*', copy.getCharacter().getCharacter()));
        }

        /**
         * One base serves every kind of its tval and holds the sval count, so the copy points at
         * the same object rather than a duplicate.
         */
        @Test
        @DisplayName("the copy shares the base")
        void sharesBase() {
            ObjectBase base = base();
            ObjectKind kind = artifactKind(base);

            assertSame(base, kind.copy().getBase());
        }

        /**
         * The ignore flags are copied into a set of their own, so the player ignoring the copy's
         * kind is not the player ignoring the original.
         */
        @Test
        @DisplayName("the copy's ignore settings are its own")
        void ignoreIsIndependent() {
            ObjectKind original = artifactKind(base());
            ObjectKind copy = original.copy();

            copy.setIgnoredAware(true);

            assertAll(
                    () -> assertTrue(copy.isIgnoredAware()),
                    () -> assertFalse(original.isIgnoredAware()));
        }

        /**
         * The element info is rebuilt entry by entry, so a change made through the copy does not
         * show in the original.
         */
        @Test
        @DisplayName("the copy's element info is its own")
        void elementInfoIsIndependent() {
            ObjectKind original = artifactKind(base());
            ObjectKind copy = original.copy();

            copy.getElInfo(ElementEnum.ELEM_FIRE).on(ElementInfoEnum.EL_INFO_IGNORE);

            assertFalse(original.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        }

        /**
         * A kind copied before the player learns it stays unaware when the original is later
         * learned.
         */
        @Test
        @DisplayName("awareness set on the original afterwards does not reach the copy")
        void awarenessIsASnapshot() {
            ObjectKind original = artifactKind(base());
            ObjectKind copy = original.copy();

            original.setAware(true);

            assertFalse(copy.isAware());
        }
    }
}
