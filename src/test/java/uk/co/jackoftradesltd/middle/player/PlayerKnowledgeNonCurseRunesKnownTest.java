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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerKnowledge#nonCurseRunesKnown(ItemObject)}, the port of C's
 * {@code object_non_curse_runes_known} ({@code obj-knowledge.c}).
 *
 * <p>The function compares an object with its own known counterpart, and C's comparisons are not all
 * the same shape, which is what the cases are built around:
 *
 * <ul>
 *   <li><b>Exact, both ways:</b> {@code to_a}, {@code to_h}, {@code to_d} and every modifier, all
 *       tested with {@code !=}. A counterpart that shows a figure the object lacks fails just as one
 *       that hides a figure the object has.</li>
 *   <li><b>One way, the counterpart must cover the object:</b> elements (the object's non-zero
 *       level against the counterpart's zero), brands, slays and flags. The counterpart may carry
 *       more.</li>
 *   <li><b>Not compared at all:</b> curses, which {@code object_runes_known} checks separately.</li>
 * </ul>
 *
 * <p>The port holds sparse maps where C holds dense arrays, so a missing modifier must read as
 * zero and a missing element as a resistance level of zero on either side.
 *
 * <p>Null items and items with no counterpart are covered in {@code PlayerScalarStateTest}; the
 * cases here start from a pair that is identical and move one thing at a time.
 *
 * <p>Class PlayerKnowledgeNonCurseRunesKnownTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class PlayerKnowledgeNonCurseRunesKnownTest {

    private static final Brand FIRE = new Brand("FIRE_2", "fire", "burns", null, null, 2, 2, 1);
    private static final Slay ANIMAL = new Slay("ANIMAL_2", "animal", null, "smite", "smite",
            MonsterRaceFlag.RF_ANIMAL, 2, 2, 1);

    private ItemObject item;
    private ItemObject known;

    /**
     * Two empty objects that match everything, which is what a fresh item and its fresh counterpart are.
     */
    private void freshPair() {
        item = new ItemObject();
        known = new ItemObject();
        item.setKnown(known);
    }

    private static Map<ObjectModifier, Integer> modifier(ObjectModifier which, int value) {
        Map<ObjectModifier, Integer> modifiers = new LinkedHashMap<>();
        modifiers.put(which, value);
        return modifiers;
    }

    private static Map<ElementEnum, ElementInfo> element(ElementEnum which, int resLevel) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(resLevel);
        Map<ElementEnum, ElementInfo> elements = new LinkedHashMap<>();
        elements.put(which, info);
        return elements;
    }

    private static Flag<ObjectFlag> flags(ObjectFlag... flags) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (flags.length > 0) result.set(List.of(flags));
        return result;
    }

    @Test
    @DisplayName("an object with nothing on it is known against an equally blank counterpart")
    void blankPairIsKnown() {
        freshPair();

        assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
    }

    @Test
    @DisplayName("an object with no counterpart is not known")
    void noCounterpart() {
        item = new ItemObject();

        assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("combat bonuses must match exactly")
    class CombatBonuses {

        @Test
        @DisplayName("an unlearned to-AC leaves the object unknown, a learned one does not")
        void toAC() {
            freshPair();
            item.setToAC(3);
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));

            known.setToAC(3);
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("an unlearned to-hit leaves the object unknown, a learned one does not")
        void toHit() {
            freshPair();
            item.setToHit(-2);
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));

            known.setToHit(-2);
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("an unlearned to-damage leaves the object unknown, a learned one does not")
        void toDam() {
            freshPair();
            item.setToDam(4);
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));

            known.setToDam(4);
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a counterpart showing more than the object has is not known either")
        void counterpartHasMore() {
            freshPair();
            known.setToAC(2);
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));

            freshPair();
            known.setToHit(2);
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));

            freshPair();
            known.setToDam(2);
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("modifiers must match exactly")
    class Modifiers {

        @Test
        @DisplayName("a modifier the counterpart does not show leaves the object unknown")
        void hidden() {
            freshPair();
            item.setModifiers(modifier(ObjectModifier.OM_STR, 2));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a modifier the counterpart shows at the same value is known")
        void shown() {
            freshPair();
            item.setModifiers(modifier(ObjectModifier.OM_STR, 2));
            known.setModifiers(modifier(ObjectModifier.OM_STR, 2));

            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a modifier shown at the wrong value is not known")
        void wrongValue() {
            freshPair();
            item.setModifiers(modifier(ObjectModifier.OM_STR, 2));
            known.setModifiers(modifier(ObjectModifier.OM_STR, 1));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a modifier the counterpart shows and the object lacks is not known — C compares with !=")
        void extraOnCounterpart() {
            freshPair();
            known.setModifiers(modifier(ObjectModifier.OM_DEX, 2));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("an explicit zero on one side and no entry on the other are the same thing")
        void zeroEqualsAbsent() {
            freshPair();
            known.setModifiers(modifier(ObjectModifier.OM_DEX, 0));
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));

            freshPair();
            item.setModifiers(modifier(ObjectModifier.OM_DEX, 0));
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("elements: the counterpart must cover the object, and may carry more")
    class Elements {

        @Test
        @DisplayName("a resistance the counterpart has no entry for leaves the object unknown")
        void noEntry() {
            freshPair();
            item.setElInfo(element(ElementEnum.ELEM_FIRE, 1));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a resistance the counterpart holds at level zero leaves the object unknown")
        void entryAtZero() {
            freshPair();
            item.setElInfo(element(ElementEnum.ELEM_FIRE, 1));
            known.setElInfo(element(ElementEnum.ELEM_FIRE, 0));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a resistance the counterpart shows is known")
        void shown() {
            freshPair();
            item.setElInfo(element(ElementEnum.ELEM_FIRE, 1));
            known.setElInfo(element(ElementEnum.ELEM_FIRE, 1));

            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("any non-zero level on the counterpart covers the object, since C tests only for zero")
        void anyNonZeroCovers() {
            freshPair();
            item.setElInfo(element(ElementEnum.ELEM_FIRE, -1));
            known.setElInfo(element(ElementEnum.ELEM_FIRE, 1));

            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("an element the object names at level zero asks nothing of the counterpart")
        void objectLevelZero() {
            freshPair();
            item.setElInfo(element(ElementEnum.ELEM_FIRE, 0));

            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("an element the counterpart shows and the object lacks does not matter")
        void extraOnCounterpart() {
            freshPair();
            known.setElInfo(element(ElementEnum.ELEM_COLD, 1));

            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a different element on the counterpart does not cover the one the object has")
        void wrongElement() {
            freshPair();
            item.setElInfo(element(ElementEnum.ELEM_FIRE, 1));
            known.setElInfo(element(ElementEnum.ELEM_COLD, 1));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("brands, slays and flags: the counterpart must cover the object, and may carry more")
    class Containments {

        @Test
        @DisplayName("a brand the counterpart does not show leaves the object unknown")
        void brandHidden() {
            freshPair();
            item.appendBrand(FIRE);

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a brand the counterpart shows is known, and an extra one does not matter")
        void brandShown() {
            freshPair();
            item.appendBrand(FIRE);
            known.appendBrand(FIRE);
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));

            freshPair();
            known.appendBrand(FIRE);
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a slay the counterpart does not show leaves the object unknown")
        void slayHidden() {
            freshPair();
            item.appendSlay(ANIMAL);

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a slay the counterpart shows is known, and an extra one does not matter")
        void slayShown() {
            freshPair();
            item.appendSlay(ANIMAL);
            known.appendSlay(ANIMAL);
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));

            freshPair();
            known.appendSlay(ANIMAL);
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a flag the counterpart does not show leaves the object unknown")
        void flagHidden() {
            freshPair();
            item.setFlagsTo(flags(ObjectFlag.OF_FEATHER));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("a flag the counterpart shows is known, and an extra one does not matter")
        void flagShown() {
            freshPair();
            item.setFlagsTo(flags(ObjectFlag.OF_FEATHER));
            known.setFlagsTo(flags(ObjectFlag.OF_FEATHER));
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));

            freshPair();
            known.setFlagsTo(flags(ObjectFlag.OF_FEATHER, ObjectFlag.OF_SEE_INVIS));
            assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
        }

        @Test
        @DisplayName("one flag covered does not excuse another that is not")
        void flagPartlyCovered() {
            freshPair();
            item.setFlagsTo(flags(ObjectFlag.OF_FEATHER, ObjectFlag.OF_SEE_INVIS));
            known.setFlagsTo(flags(ObjectFlag.OF_FEATHER));

            assertFalse(PlayerKnowledge.nonCurseRunesKnown(item));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Test
    @DisplayName("curses are not looked at: an object carrying one the counterpart lacks can still answer true")
    void cursesAreIgnored() {
        freshPair();
        Curse curse = CurseFixture.curse("siren", List.of(), 0, null, flags(), new HashMap<>(),
                new HashMap<>(), 0, 0, 0, List.of(), flags(), "wakes monsters", "The curse fires.", 0);
        item.addCurse(curse, 20, 0);

        assertTrue(PlayerKnowledge.nonCurseRunesKnown(item));
    }
}
