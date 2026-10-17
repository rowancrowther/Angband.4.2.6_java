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
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link EgoItem} accessors the ego-recognition code reads through —
 * {@code isEverSeen}, {@code setEverSeen}, {@code getFlags}, {@code getModifier}, {@code getElInfo},
 * {@code getBrands}, {@code getSlays} and {@code getCurses}.
 *
 * <p><b>Plain reads, but two of them carry a decision.</b> The collection getters hand back the
 * template's own objects rather than copies, which is right — an ego template is loaded once and
 * read for the rest of the game, and copying its brand set on every recognition test would be waste
 * — but it is a decision that a later defensive copy would quietly reverse, so it is asserted here
 * by identity.
 *
 * <p>{@link EgoItem#getModifier} is the one with real behaviour. C reads
 * {@code ego->modifiers[i]} out of a full-length array, so a modifier the ego does not touch comes
 * back as a zeroed {@code random_value}; the port holds a {@link Map} and answers {@code null}
 * instead. That difference is what
 * {@code PlayerKnowledge.knowsEgo} has to absorb, and the case is pinned
 * below so the shape of the answer is not in doubt.
 *
 * <p>Class EgoItemAccessorsTest coded on 260816, commented in full on 260816.
 *
 * @author Rowan Crowther
 */
class EgoItemAccessorsTest {

    /**
     * Builds an ego from the pieces a test cares about, defaulting the rest. The constructor takes
     * twenty-seven arguments because it is fed straight from the parsed data file; the fixture
     * exists so the tests below do not have to say so twenty-seven times.
     */
    private static EgoItem ego(Flag<ObjectFlag> flags,
                               Map<ObjectModifier, Random> modifiers,
                               Map<ElementEnum, ElementInfo> elInfo,
                               Set<Brand> brands,
                               Set<Slay> slays,
                               TreeMap<Curse, CurseData> curses,
                               boolean everSeen) {
        return new EgoItem("of Testing", "a test ego", 1, 0,
                flags, new Flag<>(ObjectFlag.class), new Flag<>(ObjectKindFlag.class),
                modifiers, new HashMap<>(), elInfo, brands, slays, curses,
                0, 0, 0, 0, new ArrayList<ObjectKind>(),
                null, null, null, 0, 0, 0,
                null, null, everSeen);
    }

    /**
     * An ego with nothing on it, for the cases that only read one member.
     */
    private static EgoItem bareEgo() {
        return ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new TreeMap<>(Curse.CURSE_ORDER), false);
    }

    @Test
    @DisplayName("everseen reports what it was built with")
    void everSeenIsCarried() {
        assertFalse(bareEgo().isEverSeen());
        assertTrue(ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new TreeMap<>(Curse.CURSE_ORDER), true).isEverSeen());
    }

    /**
     * C's {@code object_desc} writes {@code ego->everseen = true}, and {@code load.c} writes the
     * savefile's bit either way ({@code (flags & 0x02) ? true : false}), so the setter must take the
     * value as given in both directions.
     */
    @Test
    @DisplayName("setEverSeen stores the value given, true or false")
    void setEverSeenStoresValue() {
        EgoItem item = bareEgo();

        item.setEverSeen(true);
        assertTrue(item.isEverSeen());

        item.setEverSeen(false);
        assertFalse(item.isEverSeen());
    }

    /**
     * The flag is per ego type: marking one template seen says nothing about another, which is what
     * keeps the ignore menus from spoiling egos the player has not met.
     */
    @Test
    @DisplayName("setEverSeen on one ego leaves another unseen")
    void setEverSeenIsPerEgo() {
        EgoItem seen = bareEgo();
        EgoItem unseen = bareEgo();

        seen.setEverSeen(true);

        assertTrue(seen.isEverSeen());
        assertFalse(unseen.isEverSeen());
    }

    /**
     * A copy starts with the flag the original had, then the two are independent.
     */
    @Test
    @DisplayName("a copy carries the seen flag, then is independent of the original")
    void setEverSeenOnCopyIsIndependent() {
        EgoItem original = bareEgo();
        original.setEverSeen(true);

        EgoItem copy = original.copy();
        assertTrue(copy.isEverSeen());

        copy.setEverSeen(false);
        assertTrue(original.isEverSeen());
    }

    /**
     * The flag set is the template's own, and its contents are readable — both halves matter, since
     * {@code knowsEgo} tests it for subset against the player's knowledge.
     */
    @Test
    @DisplayName("the flag set is shared, not copied")
    void flagsAreShared() {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.on(ObjectFlag.OF_FEATHER);
        EgoItem item = ego(flags, new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new TreeMap<>(Curse.CURSE_ORDER), false);

        assertSame(flags, item.getFlags());
        assertTrue(item.getFlags().has(ObjectFlag.OF_FEATHER));
    }

    @Test
    @DisplayName("a modifier the ego grants comes back as its range")
    void grantedModifierIsReturned() {
        Random stealth = new Random(0, 0, 1, 4, false);
        Map<ObjectModifier, Random> modifiers = new HashMap<>();
        modifiers.put(ObjectModifier.OM_STEALTH, stealth);
        EgoItem item = ego(new Flag<>(ObjectFlag.class), modifiers, new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new TreeMap<>(Curse.CURSE_ORDER), false);

        assertSame(stealth, item.getModifier(ObjectModifier.OM_STEALTH));
    }

    /**
     * The divergence from C, stated as a test rather than left to be discovered. C would answer a
     * zeroed {@code random_value} here; the port answers {@code null}, and callers evaluating the
     * range at its extremes have to treat that null as the zero it stands for.
     */
    @Test
    @DisplayName("a modifier the ego does not touch comes back null, where C reads a zero")
    void untouchedModifierIsNull() {
        assertNull(bareEgo().getModifier(ObjectModifier.OM_STEALTH));
    }

    @Test
    @DisplayName("the element info is shared, not copied")
    void elInfoIsShared() {
        Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        elInfo.put(ElementEnum.ELEM_FIRE, fire);
        EgoItem item = ego(new Flag<>(ObjectFlag.class), new HashMap<>(), elInfo,
                new HashSet<>(), new HashSet<>(), new TreeMap<>(Curse.CURSE_ORDER), false);

        assertSame(elInfo, item.getElInfo());
        assertNotNull(item.getElInfo().get(ElementEnum.ELEM_FIRE));
        assertSame(fire, item.getElInfo().get(ElementEnum.ELEM_FIRE));
    }

    /**
     * The three remaining collections, all read the same way by {@code knowsEgo}. Each getter is a
     * read-only view onto the held collection, so a change to the collection shows through the view
     * but the view itself refuses writes.
     */
    @Test
    @DisplayName("brands, slays and curses are read-only views of the held collections")
    void collectionsAreShared() {
        Set<Brand> brands = new HashSet<>();
        Set<Slay> slays = new HashSet<>();
        TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
        EgoItem item = ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                brands, slays, curses, false);

        Brand fire = new Brand("FIRE_2", "fire", "burns", null, null, 2, 3, 20);
        Slay evil = new Slay("EVIL_2", "evil", null, "smite", "pierces", null, 2, 2, 5);
        brands.add(fire);
        slays.add(evil);
        assertTrue(item.getBrands().contains(fire), "the view sees the held set");
        assertTrue(item.getSlays().contains(evil), "the view sees the held set");
        assertThrows(UnsupportedOperationException.class, () -> item.getBrands().add(fire));
        assertThrows(UnsupportedOperationException.class, () -> item.getSlays().add(evil));

        Curse siren = CurseFixture.curse("siren", null, 0, null, null, null, null,
                0, 0, 0, null, null, null, null, 0);
        CurseData data = new CurseData(3, 0);
        curses.put(siren, data);
        assertSame(data, item.getCurses().get(siren), "the getter is a view of the held map");
        assertThrows(UnsupportedOperationException.class,
                () -> item.getCurses().put(siren, new CurseData(1, 0)));
    }

    /**
     * An ego that grants none of them reads as empty rather than null. Most egos are in this state
     * for most of these collections, and {@code knowsEgo} walks all three unconditionally — C guards
     * each with {@code if (ego->brands && …)} because its arrays may genuinely be absent, and the
     * port needs the empty collection so it does not have to.
     */
    @Test
    @DisplayName("an ego granting none of them reads as empty")
    void emptyCollectionsAreEmptyNotNull() {
        EgoItem item = bareEgo();

        assertTrue(item.getBrands().isEmpty());
        assertTrue(item.getSlays().isEmpty());
        assertTrue(item.getCurses().isEmpty());
        assertTrue(item.getElInfo().isEmpty());
    }

    /**
     * Because the collections are shared, a template loaded and later added to reads back the
     * addition. This is the useful side of sharing, and the reason it is not a defect.
     */
    @Test
    @DisplayName("a later addition to a shared collection is visible through the getter")
    void sharingIsLive() {
        Set<Slay> slays = new HashSet<>();
        EgoItem item = ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), slays, new TreeMap<>(Curse.CURSE_ORDER), false);

        assertTrue(item.getSlays().isEmpty());
        slays.add(null);

        assertFalse(item.getSlays().isEmpty());
    }

    /**
     * The suppressed-flag set and the ignore categories, both of which sit beside their more obvious
     * twins and would be easy to read the wrong one of.
     */
    @Nested
    @DisplayName("suppressed flags and ignore categories")
    class SuppressionAndIgnoring {

        /**
         * An ego has two flag sets: the ones it grants and the ones it takes away. They are adjacent
         * arguments of the same type in the constructor, so each is checked against its own
         * accessor with a flag the other does not carry.
         */
        @Test
        @DisplayName("granted and suppressed flags are separate sets")
        void grantedAndSuppressedAreSeparate() {
            Flag<ObjectFlag> granted = new Flag<>(ObjectFlag.class);
            granted.set(List.of(ObjectFlag.OF_FEATHER));
            Flag<ObjectFlag> suppressed = new Flag<>(ObjectFlag.class);
            suppressed.set(List.of(ObjectFlag.OF_AGGRAVATE));

            EgoItem item = new EgoItem("of Testing", "a test ego", 1, 0,
                    granted, suppressed, new Flag<>(ObjectKindFlag.class),
                    new HashMap<>(), new HashMap<>(), new HashMap<>(),
                    new HashSet<>(), new HashSet<>(), new TreeMap<>(Curse.CURSE_ORDER),
                    0, 0, 0, 0, new ArrayList<ObjectKind>(),
                    null, null, null, 0, 0, 0,
                    null, null, false);

            assertTrue(item.getFlags().has(ObjectFlag.OF_FEATHER));
            assertFalse(item.getFlags().has(ObjectFlag.OF_AGGRAVATE));

            assertTrue(item.getOffFlags().has(ObjectFlag.OF_AGGRAVATE));
            assertFalse(item.getOffFlags().has(ObjectFlag.OF_FEATHER));
        }

        /**
         * Marking an ignore category records it, which is how the player's "hide these" settings
         * reach the ignore machinery.
         */
        @Test
        @DisplayName("marking an ignore category records it")
        void markingRecordsTheCategory() {
            EgoItem item = bareEgo();

            item.setIgnoreType(IgnoreType.ITYPE_SHARP);

            assertTrue(item.getIgnoreType(IgnoreType.ITYPE_SHARP));
        }

        /**
         * Every category answers from the start, because the constructor fills the map with a
         * {@code false} for each one rather than leaving it sparse. That is what makes the
         * accessor's direct unboxing safe, and it holds for an ego nothing has been marked on.
         */
        @Test
        @DisplayName("every category answers on a freshly built ego")
        void everyCategoryAnswers() {
            EgoItem item = bareEgo();

            for (IgnoreType type : IgnoreType.values()) {
                assertFalse(item.getIgnoreType(type), type + " should start unmarked");
            }
        }

        /**
         * Marking touches only the category named. The opposite write, {@code clearIgnoreType}, is
         * tested in {@link EgoItemClearAndCopyTest}.
         */
        @Test
        @DisplayName("marking one category leaves the others alone")
        void markingOneLeavesOthers() {
            EgoItem item = bareEgo();

            item.setIgnoreType(IgnoreType.ITYPE_SHARP);

            assertTrue(item.getIgnoreType(IgnoreType.ITYPE_SHARP));
            assertFalse(item.getIgnoreType(IgnoreType.ITYPE_GREAT));
        }
    }
}
