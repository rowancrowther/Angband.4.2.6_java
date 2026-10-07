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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the pack-order rules of {@link ItemObject#earlierObject} that {@link ItemObjectOrderingTest}
 * does not reach - usable ammunition, flavour awareness, light fuel and value - together with
 * {@link ItemObject#objectIsInQuiver} and {@link ItemObject#nullKnown}. Expected values come from
 * C's {@code earlier_object} ({@code player-calcs.c}), {@code object_is_in_quiver}
 * ({@code obj-gear.c}) and the {@code obj->known = NULL} assignment in {@code combine_pack}
 * ({@code obj-gear.c}).
 *
 * <p>The comparisons run in C's order and each returns as soon as it separates the two objects, so
 * every fixture below ties on everything the rule under test comes after. The objects carry no
 * known half, which sends {@code object_value} down its base-price route: an aware kind is worth
 * its kind's cost, and an unaware one a flat figure for its type, zero for a weapon. That makes the
 * value rule testable with nothing more than a kind cost.
 *
 * <p>Class ItemObjectEarlierObjectRulesTest coded on 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectEarlierObjectRulesTest {

    /**
     * The player the game held before each test.
     */
    private Player savedPlayer;

    /**
     * The player installed for the test, so a test can choose its launcher's ammunition.
     */
    private Player player;

    /**
     * A class with no books, which the browse test in {@code earlierObject} needs to find.
     *
     * @return the class
     */
    private static PlayerClass bookless() {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<PlayerSkill, Integer> extra = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
            extra.put(skill, 0);
        }

        return new PlayerClass("Test Warrior", List.of(), stats, skills, extra, 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), new ClassMagic(1, 300, 1, List.of()));
    }

    /**
     * Calls {@code earlierObject}; the receiver plays no part in the ordering.
     *
     * @param orig  the object holding the position
     * @param cand  the candidate
     * @param store {@code true} when ordering a shop's stock
     * @return {@code true} if the candidate should come before the original
     */
    private static boolean earlier(ItemObject orig, ItemObject cand, boolean store) {
        return new ItemObject().earlierObject(orig, cand, store);
    }

    /**
     * A kind of the given type, aware or not, with a cost.
     *
     * @param tValue the object type
     * @param cost   the kind's listed cost
     * @param aware  whether the player knows the kind
     * @return the kind
     */
    private static ObjectKind kind(TValue tValue, int cost, boolean aware) {
        ObjectKind kind = new ObjectKind(null, cost, 0, 0, 0, "test", tValue, "test", null, false);
        kind.setAware(aware);
        return kind;
    }

    /**
     * An item of the given kind, sub-type and fuel.
     *
     * @param tValue the object type
     * @param kind   the item's kind
     * @param sVal   the sub-type
     * @param pVal   the pval, which for a light is its fuel
     * @return the item
     */
    private static ItemObject item(TValue tValue, ObjectKind kind, int sVal, int pVal) {
        ItemObject item = ItemFixture.item(tValue).kind(kind).number(1).build();
        item.setsValue(sVal);
        item.setpValue(pVal);
        return item;
    }

    /**
     * Installs a player with a calculated state, which the ammunition rule reads.
     */
    @BeforeEach
    void installPlayer() {
        savedPlayer = GameState.getPlayer();

        player = new Player();
        ItemFixture.set(player, "state", new PlayerState());
        ItemFixture.set(player, "playerClass", bookless());
        GameState.setPlayer(player);
    }

    /**
     * Puts the game's player back.
     */
    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * The rule that puts the ammunition the player's launcher fires ahead of other ammunition,
     * which runs even in a store.
     */
    @Nested
    @DisplayName("usable ammunition")
    class UsableAmmunition {

        /**
         * With a bow loaded for bolts, a bolt displaces an arrow and an arrow does not displace a
         * bolt. Without the rule, tval order alone would put the arrow first.
         */
        @Test
        @DisplayName("ammunition the launcher fires comes before other ammunition")
        void usableAmmoFirst() {
            player.getPlayerState().setAmmoTValue(TValue.TV_BOLT);
            ItemObject arrow = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 0, true), 1, 0);
            ItemObject bolt = item(TValue.TV_BOLT, kind(TValue.TV_BOLT, 0, true), 1, 0);

            assertTrue(earlier(arrow, bolt, false));
            assertFalse(earlier(bolt, arrow, false));
        }

        /**
         * The other half: with the launcher firing arrows, the usable ammunition wins whichever
         * side it is on, so the answer flips with the player rather than with the tval order.
         */
        @Test
        @DisplayName("the preference follows the launcher, not the type order")
        void preferenceFollowsTheLauncher() {
            player.getPlayerState().setAmmoTValue(TValue.TV_ARROW);
            ItemObject arrow = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 0, true), 1, 0);
            ItemObject bolt = item(TValue.TV_BOLT, kind(TValue.TV_BOLT, 0, true), 1, 0);

            assertFalse(earlier(arrow, bolt, false));
            assertTrue(earlier(bolt, arrow, false));
        }

        /**
         * The ammunition test is outside C's {@code !store} blocks, so a shop orders its ammunition
         * the same way.
         */
        @Test
        @DisplayName("a store applies the ammunition preference too")
        void storeKeepsTheAmmoRule() {
            player.getPlayerState().setAmmoTValue(TValue.TV_BOLT);
            ItemObject arrow = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 0, true), 1, 0);
            ItemObject bolt = item(TValue.TV_BOLT, kind(TValue.TV_BOLT, 0, true), 1, 0);

            assertTrue(earlier(arrow, bolt, true));
        }

        /**
         * The rule applies only when both objects are ammunition. A sword is separated from a bolt
         * by type, whatever the launcher fires.
         */
        @Test
        @DisplayName("the rule is not applied unless both objects are ammunition")
        void bothMustBeAmmo() {
            player.getPlayerState().setAmmoTValue(TValue.TV_BOLT);
            ItemObject sword = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 0, true), 1, 0);
            ItemObject bolt = item(TValue.TV_BOLT, kind(TValue.TV_BOLT, 0, true), 1, 0);

            // list-tvals.h declares TV_BOLT before TV_SWORD, so by decreasing type the sword is
            // first; the usable-ammunition preference must not drag the bolt ahead of it.
            assertFalse(earlier(sword, bolt, false));
            assertTrue(earlier(bolt, sword, false));
        }

        /**
         * With no launcher, the player's ammunition type is C's zero and matches nothing, so the
         * rule is silent and the order falls to the type.
         */
        @Test
        @DisplayName("with no launcher the rule is silent")
        void noLauncher() {
            ItemObject arrow = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 0, true), 1, 0);
            ItemObject bolt = item(TValue.TV_BOLT, kind(TValue.TV_BOLT, 0, true), 1, 0);

            // Decreasing tval order: the later-declared type is the earlier in the pack.
            assertEquals(TValue.TV_BOLT.ordinal() > TValue.TV_ARROW.ordinal(),
                    earlier(arrow, bolt, false));
        }
    }

    /**
     * The rule that puts unidentified flavoured items last in the pack.
     */
    @Nested
    @DisplayName("flavour awareness")
    class FlavourAwareness {

        /**
         * Same type and sub-type, so the type and sub-type rules tie. The aware object displaces
         * the unaware one, and not the reverse.
         */
        @Test
        @DisplayName("an unaware kind comes after an aware one of the same type")
        void unawareComesLast() {
            ItemObject aware = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 0, true), 1, 0);
            ItemObject unaware = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 0, false), 1, 0);

            assertTrue(earlier(unaware, aware, false));
            assertFalse(earlier(aware, unaware, false));
        }

        /**
         * The rule sits inside C's {@code !store} block, so a shop falls through to the later
         * rules; with equal values there is then no preference.
         */
        @Test
        @DisplayName("a store ignores awareness")
        void storeIgnoresAwareness() {
            ItemObject aware = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 0, true), 1, 0);
            ItemObject unaware = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 0, false), 1, 0);

            assertFalse(earlier(unaware, aware, true));
            assertFalse(earlier(aware, unaware, true));
        }

        /**
         * Awareness is tested before sub-type, so an unaware kind with the lower sub-type still
         * comes after an aware one with the higher.
         */
        @Test
        @DisplayName("awareness outranks sub-type")
        void awarenessBeforeSubType() {
            ItemObject awareHigh = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 0, true), 9, 0);
            ItemObject unawareLow = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 0, false), 1, 0);

            assertTrue(earlier(unawareLow, awareHigh, false));
            assertFalse(earlier(awareHigh, unawareLow, false));
        }
    }

    /**
     * The lights-by-fuel rule.
     */
    @Nested
    @DisplayName("light fuel")
    class LightFuel {

        /**
         * Lights sort by decreasing fuel: the fuller light stays first, and displaces the emptier.
         */
        @Test
        @DisplayName("the light with more fuel comes first")
        void moreFuelFirst() {
            ObjectKind torch = kind(TValue.TV_LIGHT, 0, true);
            ItemObject full = item(TValue.TV_LIGHT, torch, 1, 5000);
            ItemObject low = item(TValue.TV_LIGHT, torch, 1, 3000);

            assertTrue(earlier(low, full, false));
            assertFalse(earlier(full, low, false));
        }

        /**
         * Equal fuel ties and the value rule decides; with equal values there is no preference.
         */
        @Test
        @DisplayName("equal fuel gives no preference")
        void equalFuel() {
            ObjectKind torch = kind(TValue.TV_LIGHT, 0, true);

            assertFalse(earlier(item(TValue.TV_LIGHT, torch, 1, 4000),
                    item(TValue.TV_LIGHT, torch, 1, 4000), false));
        }

        /**
         * The fuel rule is inside C's {@code !store} block, so a shop ignores it.
         */
        @Test
        @DisplayName("a store ignores fuel")
        void storeIgnoresFuel() {
            ObjectKind torch = kind(TValue.TV_LIGHT, 0, true);
            ItemObject full = item(TValue.TV_LIGHT, torch, 1, 5000);
            ItemObject low = item(TValue.TV_LIGHT, torch, 1, 3000);

            assertFalse(earlier(low, full, true));
        }

        /**
         * The fuel rule is for lights only; the pval of anything else is not compared.
         */
        @Test
        @DisplayName("fuel is not compared for other types")
        void onlyLights() {
            ObjectKind sword = kind(TValue.TV_SWORD, 0, true);

            assertFalse(earlier(item(TValue.TV_SWORD, sword, 1, 3000),
                    item(TValue.TV_SWORD, sword, 1, 5000), false));
        }
    }

    /**
     * The final rule, value: decreasing for everything except ammunition, which goes by increasing
     * value so the cheap stacks are shot first.
     */
    @Nested
    @DisplayName("value")
    class Value {

        /**
         * The dearer weapon stays first and displaces the cheaper.
         */
        @Test
        @DisplayName("the dearer object comes first")
        void dearerFirst() {
            ItemObject dear = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 100, true), 1, 0);
            ItemObject cheap = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 50, true), 1, 0);

            assertTrue(earlier(cheap, dear, false));
            assertFalse(earlier(dear, cheap, false));
        }

        /**
         * Ammunition reverses it: the cheaper stack displaces the dearer.
         */
        @Test
        @DisplayName("the cheaper ammunition comes first")
        void cheaperAmmoFirst() {
            ItemObject dear = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 5, true), 1, 0);
            ItemObject cheap = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 2, true), 1, 0);

            assertTrue(earlier(dear, cheap, false));
            assertFalse(earlier(cheap, dear, false));
        }

        /**
         * Equal values are no preference in either direction, ammunition or not.
         */
        @Test
        @DisplayName("equal values give no preference")
        void equalValues() {
            assertFalse(earlier(item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 70, true), 1, 0),
                    item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 70, true), 1, 0), false));
            assertFalse(earlier(item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 3, true), 1, 0),
                    item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 3, true), 1, 0), false));
        }

        /**
         * The value rule is outside C's {@code !store} blocks, so a shop orders by it too.
         */
        @Test
        @DisplayName("a store orders by value as well")
        void storeUsesValue() {
            ItemObject dear = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 100, true), 1, 0);
            ItemObject cheap = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, 50, true), 1, 0);

            assertTrue(earlier(cheap, dear, true));
        }
    }

    /**
     * {@code object_is_in_quiver}: identity within the player's quiver slots.
     */
    @Nested
    @DisplayName("objectIsInQuiver")
    class InQuiver {

        /**
         * Installs a four-slot quiver, whatever the game constants say, with empty slots.
         *
         * @return the quiver array
         */
        private ItemObject[] quiver() {
            ItemObject[] slots = new ItemObject[4];
            ItemFixture.set(player.getPlayerUpkeep(), "quiverObjects", slots);
            return slots;
        }

        @Test
        @DisplayName("an item in a quiver slot is in the quiver")
        void inSlot() {
            ItemObject[] slots = quiver();
            ItemObject arrows = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 0, true), 1, 0);
            slots[2] = arrows;

            assertTrue(arrows.objectIsInQuiver(player));
        }

        /**
         * Identity, not equality: a second stack just like the first is not the quiver's.
         */
        @Test
        @DisplayName("an identical stack that is not in the quiver is not in it")
        void lookalikeIsNot() {
            ItemObject[] slots = quiver();
            ObjectKind kind = kind(TValue.TV_ARROW, 0, true);
            slots[0] = item(TValue.TV_ARROW, kind, 1, 0);
            ItemObject twin = item(TValue.TV_ARROW, kind, 1, 0);

            assertFalse(twin.objectIsInQuiver(player));
        }

        /**
         * An empty quiver, with every slot null, holds nothing.
         */
        @Test
        @DisplayName("an empty quiver holds nothing")
        void emptyQuiver() {
            quiver();

            assertFalse(item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 0, true), 1, 0)
                    .objectIsInQuiver(player));
        }
    }

    /**
     * {@code obj->known = NULL}, as {@code combine_pack} does to the absorbed stack.
     */
    @Nested
    @DisplayName("nullKnown")
    class NullKnown {

        /**
         * The link goes and the known object itself is untouched: it keeps its count.
         */
        @Test
        @DisplayName("clears the link and leaves the known object alone")
        void clearsTheLink() {
            ItemObject known = ItemFixture.item(TValue.TV_ARROW)
                    .kind(kind(TValue.TV_ARROW, 0, true)).number(7).build();
            ItemObject real = ItemFixture.item(TValue.TV_ARROW)
                    .kind(kind(TValue.TV_ARROW, 0, true)).number(7).known(known).build();
            assertNotNull(real.getKnown());

            real.nullKnown();

            assertNull(real.getKnown());
            assertEquals(7, known.getNumber());
        }

        /**
         * Clearing an item that has no known half is harmless, as it is when applied to a known
         * object itself.
         */
        @Test
        @DisplayName("is harmless on an item with no known half")
        void harmlessWithoutKnown() {
            ItemObject bare = item(TValue.TV_ARROW, kind(TValue.TV_ARROW, 0, true), 1, 0);

            bare.nullKnown();

            assertNull(bare.getKnown());
        }
    }
}
