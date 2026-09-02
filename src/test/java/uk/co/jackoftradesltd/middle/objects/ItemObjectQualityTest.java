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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.QualityValueEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;

import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#ignoreLevelOf()} and {@link ItemObject#isInQuiver(Player)}.
 *
 * <p>{@code ignoreLevelOf} answers the quality band an item would be hidden at, and it is the
 * jewellery branch that carries the judgement: a ring is only ever bad or average, never good,
 * because a ring's worth is in what it does rather than in its combat bonuses. One positive modifier
 * or one positive combat value is enough to lift it out of "bad" — and a ring with nothing either
 * way is average rather than bad, which is the case a reader is most likely to get backwards.
 *
 * <p>An item the player knows nothing about answers the sentinel at the top of the scale, so an
 * unidentified object is never hidden by a quality setting.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectQualityTest {

    /**
     * The constants holder as it was before this class ran.
     */
    private static Object savedConstants;

    /**
     * The player as it was before this class ran.
     */
    private static Player savedPlayer;

    /**
     * Seeds the carry capacities the upkeep's arrays are sized from, and a player for the quiver
     * test to look into.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeAll
    static void seedGlobals() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        savedConstants = data.get(null);
        data.set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null));

        savedPlayer = GameState.getPlayer();
    }

    /**
     * Puts both back.
     *
     * @throws Exception if a field cannot be reached
     */
    @AfterAll
    static void restoreGlobals() throws Exception {
        GameState.setPlayer(savedPlayer);
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, savedConstants);
    }

    /**
     * An item of the given type with a known half attached, since every quality answer bar the
     * sentinel needs one.
     *
     * @param tValue the object type
     * @return the item
     * @throws Exception if a field cannot be reached
     */
    private static ItemObject knownItem(TValue tValue) {
        return ItemFixture.item(tValue).kind(ItemFixture.kindWithDice(tValue)).fullyKnown().build();
    }

    /**
     * An item whose known half disagrees with its reality, so that it is known but not fully known —
     * the state an unidentified but handled object is in.
     *
     * @param tValue the object type
     * @return the item
     * @throws Exception if a field cannot be reached
     */
    private static ItemObject partlyKnown(TValue tValue) {
        ItemObject item = knownItem(tValue);
        set(item, "toHit", 4);
        set(item.getKnown(), "toHit", 0);
        return item;
    }

    /**
     * The quality band.
     */
    @Nested
    @DisplayName("ignoreLevelOf")
    class Quality {

        /**
         * An item with no known half answers the top of the scale, so nothing the player has not
         * identified is hidden by a quality setting.
         */
        @Test
        @DisplayName("an unknown item answers the top of the scale")
        void unknownAnswersMax() {
            assertEquals(QualityValueEnum.IGNORE_MAX, new ItemObject().ignoreLevelOf());
        }

        /**
         * A ring with nothing positive or negative about it is average, not bad — the case most
         * easily got backwards, since "no bonuses" sounds like the bottom of the scale.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a plain ring is average, not bad")
        void plainRingIsAverage() throws Exception {
            assertEquals(QualityValueEnum.IGNORE_AVERAGE, knownItem(TValue.TV_RING).ignoreLevelOf());
        }

        /**
         * One positive modifier lifts a ring out of "bad", whatever else it carries.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("one positive modifier makes a ring average")
        void positiveModifierIsAverage() throws Exception {
            ItemObject ring = knownItem(TValue.TV_RING);
            ring.getKnown().putModifier(ObjectModifier.OM_STEALTH, 2);

            assertEquals(QualityValueEnum.IGNORE_AVERAGE, ring.ignoreLevelOf());
        }

        /**
         * One positive combat value does the same, so the two tests are alternatives rather than
         * both being required.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("one positive combat value makes a ring average")
        void positiveCombatValueIsAverage() throws Exception {
            ItemObject ring = knownItem(TValue.TV_RING);
            set(ring.getKnown(), "toHit", 3);

            assertEquals(QualityValueEnum.IGNORE_AVERAGE, ring.ignoreLevelOf());
        }

        /**
         * A negative combat value with nothing positive anywhere makes it bad — the only route to
         * the bottom band for jewellery.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a negative combat value with nothing positive makes a ring bad")
        void negativeCombatValueIsBad() throws Exception {
            ItemObject ring = knownItem(TValue.TV_RING);
            set(ring.getKnown(), "toAC", -4);

            assertEquals(QualityValueEnum.IGNORE_BAD, ring.ignoreLevelOf());
        }

        /**
         * A positive modifier beats a negative combat value, because the positive test runs first
         * and returns. A cursed ring that still does something useful is average.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a positive modifier outweighs a negative combat value")
        void positiveBeatsNegative() throws Exception {
            ItemObject ring = knownItem(TValue.TV_RING);
            ring.getKnown().putModifier(ObjectModifier.OM_STEALTH, 2);
            set(ring.getKnown(), "toAC", -4);

            assertEquals(QualityValueEnum.IGNORE_AVERAGE, ring.ignoreLevelOf());
        }

        /**
         * An amulet takes the same branch as a ring, since the jewellery test covers both.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an amulet is judged as jewellery too")
        void amuletIsJewellery() throws Exception {
            assertEquals(QualityValueEnum.IGNORE_AVERAGE, knownItem(TValue.TV_AMULET).ignoreLevelOf());
        }

        /**
         * A weapon is not jewellery, so it goes down the other branch — and a weapon whose known
         * half matches its reality is <em>fully known</em>, which means it is judged on its bonuses.
         * One no better than the worst its kind could roll is average.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a fully known, unremarkable weapon is average")
        void fullyKnownPlainWeaponIsAverage() throws Exception {
            assertEquals(QualityValueEnum.IGNORE_AVERAGE, knownItem(TValue.TV_SWORD).ignoreLevelOf());
        }

        /**
         * A fully known weapon carrying better bonuses than its kind is good — the band that stops
         * a quality setting hiding something worth keeping.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a fully known weapon with real bonuses is good")
        void fullyKnownGoodWeaponIsGood() throws Exception {
            ItemObject sword = knownItem(TValue.TV_SWORD);
            set(sword, "toDam", 5);
            set(sword.getKnown(), "toDam", 5);

            assertEquals(QualityValueEnum.IGNORE_GOOD, sword.ignoreLevelOf());
        }

        /**
         * A weapon the player has <em>not</em> fully learned takes the other branch entirely, and
         * one that has not been assessed answers the top of the scale — nothing unexamined is hidden
         * by a quality setting.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a partly known, unassessed weapon answers the top of the scale")
        void partlyKnownUnassessedAnswersMax() throws Exception {
            ItemObject sword = partlyKnown(TValue.TV_SWORD);

            assertEquals(QualityValueEnum.IGNORE_MAX, sword.ignoreLevelOf());
        }

        /**
         * Once assessed, the same partly known weapon answers the "everything" band, which is what
         * lets a player hide every ordinary weapon they have looked at without identifying.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a partly known but assessed weapon answers the everything band")
        void partlyKnownAssessedAnswersAll() throws Exception {
            ItemObject sword = partlyKnown(TValue.TV_SWORD);
            sword.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);

            assertEquals(QualityValueEnum.IGNORE_ALL, sword.ignoreLevelOf());
        }
    }

    /**
     * The quiver membership test, which decides which stacking limits apply to an item.
     */
    @Nested
    @DisplayName("isInQuiver")
    class InQuiver {

        /**
         * An item nobody has put in the quiver is not in it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an item not in the quiver is not in it")
        void looseItemIsNotInQuiver() throws Exception {
            Player player = new Player();

            assertFalse(knownItem(TValue.TV_ARROW).isInQuiver(player));
        }

        /**
         * An item written into a quiver slot is in it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an item in a quiver slot is in it")
        void quiveredItemIsInIt() throws Exception {
            Player player = new Player();
            ItemObject arrows = knownItem(TValue.TV_ARROW);
            player.getPlayerUpkeep().getQuiver()[2] = arrows;

            assertTrue(arrows.isInQuiver(player));
        }

        /**
         * Membership is by identity, not equality: an identical stack of arrows sitting elsewhere is
         * a different stack, and the quiver limits do not apply to it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an identical stack elsewhere is not the quivered one")
        void identicalStackIsNotTheSame() throws Exception {
            Player player = new Player();
            ItemObject quivered = knownItem(TValue.TV_ARROW);
            ItemObject loose = knownItem(TValue.TV_ARROW);
            player.getPlayerUpkeep().getQuiver()[0] = quivered;

            assertTrue(quivered.isInQuiver(player));
            assertFalse(loose.isInQuiver(player), "a stack like it is still not it");
        }
    }
}
