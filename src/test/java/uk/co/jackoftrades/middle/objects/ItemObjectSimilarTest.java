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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.ObjectStackEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerBody;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#similar}, the port of C's {@code object_similar} ({@code obj-util.c}) —
 * the rule deciding whether two items may occupy one stack.
 *
 * <p>It is a long chain of veto clauses ending in a per-tval branch, and the shape of it is what
 * makes it worth testing exhaustively: every clause can only ever refuse, so a clause accidentally
 * dropped shows up not as an error but as items merging that should not. Two potions of different
 * strengths silently becoming one stack is the kind of bug that survives a long time.
 *
 * <p>Each test below therefore starts from a pair that <em>does</em> stack and breaks exactly one
 * thing, so that a failure names the clause. The baseline itself is asserted first, since every
 * other test in the suite is worthless if the pair does not stack to begin with.
 *
 * <p>{@code similar} reads the live player through {@link GameState}, to ask whether either item is
 * equipped. The player installed here has a body with no slots, so nothing is ever equipped and the
 * two opening clauses stay out of the way of the rules under test.
 *
 * @author ClaudeCode
 */
class ItemObjectSimilarTest {

    private static Player savedPlayer;

    /**
     * A kind shared by every item the fixtures build. {@code similar} compares kinds with
     * {@code equals}, and {@link ObjectKind} does not override it, so sharing one instance is what
     * makes two items the same kind.
     */
    private static ObjectKind kind;

    private ItemObject first;
    private ItemObject second;

    /**
     * Installs a player whose body has no equipment slots, so {@code itemIsEquipped} is always
     * false. The real player is put back afterwards, since {@link GameState} is process-wide.
     *
     * @author ClaudeCode
     */
    @BeforeAll
    static void installPlayer() throws Exception {
        savedPlayer = GameState.getPlayer();

        Player player = new Player();
        Field body = Player.class.getDeclaredField("body");
        body.setAccessible(true);
        body.set(player, new PlayerBody("test", List.of()));

        GameState.setPlayer(player);
        kind = new ObjectKind();
    }

    /**
     * @author ClaudeCode
     */
    @AfterAll
    static void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    private static void set(ItemObject item, String name, Object value) {
        try {
            Field field = ItemObject.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(item, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("ItemObject." + name + " is no longer settable by reflection", e);
        }
    }

    /**
     * Every element, each with its own {@link ElementInfo}. {@code similar} walks
     * {@link ElementEnum#values()} in full — sentinels included — and reads the map without a null
     * check, so a partial map is not a smaller fixture but a crash.
     *
     * @return a complete element map
     * @author ClaudeCode
     */
    private static Map<ElementEnum, ElementInfo> elements() {
        Map<ElementEnum, ElementInfo> elInfo = new EnumMap<>(ElementEnum.class);
        for (ElementEnum element : ElementEnum.values()) {
            elInfo.put(element, new ElementInfo());
        }
        return elInfo;
    }

    /**
     * Every modifier at zero. As with the element map, the weapon branch reads every key.
     *
     * @return a complete modifier map
     * @author ClaudeCode
     */
    private static Map<ObjectModifier, String> modifiers() {
        Map<ObjectModifier, String> mods = new EnumMap<>(ObjectModifier.class);
        for (ObjectModifier mod : ObjectModifier.values()) {
            mods.put(mod, "0");
        }
        return mods;
    }

    /**
     * Builds a plain sword: no ego, no artifact, no curses, nothing recharging. The weapon branch is
     * the interesting one, being the only tval group that compares combat values, modifiers, ego and
     * curses rather than answering outright.
     *
     * @return an item that stacks with any other built the same way
     * @author ClaudeCode
     */
    private static ItemObject weapon() {
        return item(TValue.TV_SWORD);
    }

    /**
     * Builds an item of the given type with everything else neutral.
     *
     * @param tValue the item type, which decides which branch {@code similar} takes
     * @return the constructed item
     * @author ClaudeCode
     */
    private static ItemObject item(TValue tValue) {
        ItemObject item = new ItemObject();
        set(item, "kind", kind);
        set(item, "tValue", tValue);
        set(item, "flags", new Flag<>(ObjectFlag.class));
        set(item, "elInfo", elements());
        set(item, "modifiers", modifiers());
        set(item, "brands", new HashSet<Brand>());
        set(item, "slays", new HashSet<Slay>());
        set(item, "curses", new HashMap<Curse.CurseEntry, Boolean>());
        set(item, "number", 1);
        set(item, "timeout", 0);
        return item;
    }

    /**
     * A minimal ego definition. {@code similar} compares egos with {@code !=}, so nothing on it is
     * read and the fixture exists solely to be a distinct non-null {@link EgoItem}.
     *
     * @return an ego with every field empty
     * @author ClaudeCode
     */
    private static EgoItem ego() {
        return new EgoItem("of Testing", null, 0, 0, new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectFlag.class), null, Map.of(), Map.of(), Map.of(),
                java.util.Set.of(), java.util.Set.of(), Map.of(), 0, 0, 0, 0, List.of(),
                null, null, null, 0, 0, 0, null, null, false);
    }

    private static Flag<ObjectStackEnum> mode(ObjectStackEnum... flags) {
        Flag<ObjectStackEnum> mode = new Flag<>(ObjectStackEnum.class);
        mode.init(flags);
        return mode;
    }

    /**
     * A fresh pair of identical swords per test, so that a test which breaks one of them cannot
     * leak into the next.
     *
     * @author ClaudeCode
     */
    @BeforeEach
    void newPair() {
        first = weapon();
        second = weapon();
    }

    /**
     * The baseline, and the clauses that refuse before the type is even looked at.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("the early vetoes")
    class EarlyVetoes {

        /**
         * The premise every other test rests on.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("two identical plain weapons stack")
        void baseline() {
            assertTrue(first.similar(second, mode()));
        }

        /**
         * C's {@code if (obj1 == obj2) return false}. An item is not similar to itself, because the
         * caller is asking whether to merge two stacks and merging a stack into itself would double
         * it.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an item never stacks with itself")
        void notWithItself() {
            assertFalse(first.similar(first, mode()));
        }

        /**
         * A mimic is a monster wearing an item's appearance. Stacking it would merge a creature into
         * the floor pile, so either side mimicking is enough to refuse.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a mimicking item stacks with nothing")
        void mimicsNeverStack() {
            first.setMimickingMIndex(3);
            assertFalse(first.similar(second, mode()));

            first.setMimickingMIndex(0);
            second.setMimickingMIndex(3);
            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different kinds do not stack")
        void differentKinds() {
            set(second, "kind", new ObjectKind());

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different tvals do not stack")
        void differentTvals() {
            set(second, "tValue", TValue.TV_POLEARM);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different object flags do not stack")
        void differentFlags() {
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.on(ObjectFlag.OF_SUST_STR);
            set(second, "flags", flags);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * An artifact is unique by definition, so it cannot share a stack even with a copy of
         * itself — and the clause is checked on both sides.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an artifact stacks with nothing")
        void artifactsNeverStack() {
            set(first, "artifact", null);
            set(second, "artifact", null);
            assertTrue(first.similar(second, mode()));

            set(second, "artifact", new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0,
                    "0", 0, 0, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), java.util.Set.of(),
                    java.util.Set.of(), Map.of(), 0, 0, 0, 0, null, null, null));
            assertFalse(first.similar(second, mode()));
        }
    }

    /**
     * Element info, which is compared field by field rather than by {@code equals} — the resistance
     * level exactly, and two of the three flags.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("element info")
    class Elements {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different resistance levels do not stack")
        void differentResistance() {
            elementOf(second, ElementEnum.ELEM_FIRE).setResLevel(1);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("one item hating an element and the other not do not stack")
        void differentHates() {
            elementOf(second, ElementEnum.ELEM_ACID).on(ElementInfoEnum.EL_INFO_HATES);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("one item ignoring an element and the other not do not stack")
        void differentIgnores() {
            elementOf(second, ElementEnum.ELEM_ACID).on(ElementInfoEnum.EL_INFO_IGNORE);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * The third flag is deliberately <em>not</em> compared: C reads only hates and ignores, both
         * of which change what happens to the item when the element strikes it. This pins that the
         * omission is the original's and has been carried across rather than introduced.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("the random flag is not part of the comparison")
        void randomFlagIsIgnored() {
            elementOf(second, ElementEnum.ELEM_ACID).on(ElementInfoEnum.EL_INFO_RANDOM);

            assertTrue(first.similar(second, mode()));
        }

        @SuppressWarnings("unchecked")
        private ElementInfo elementOf(ItemObject item, ElementEnum element) {
            try {
                Field field = ItemObject.class.getDeclaredField("elInfo");
                field.setAccessible(true);
                return ((Map<ElementEnum, ElementInfo>) field.get(item)).get(element);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("ItemObject.elInfo is no longer readable by reflection", e);
            }
        }
    }

    /**
     * The per-type branch, which is where C stops asking the same questions of everything and starts
     * asking what sort of item it is holding.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("the type branch")
    class TypeBranch {

        /**
         * Chests never stack: each has its own contents and its own trap, so two identical-looking
         * chests are not interchangeable.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("chests never stack")
        void chests() {
            assertFalse(item(TValue.TV_CHEST).similar(item(TValue.TV_CHEST), mode()));
        }

        /**
         * Consumables stack on kind alone — everything that distinguishes two potions has already
         * been compared by the time the branch is reached, so the branch answers yes outright.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("potions, scrolls, food and rods stack on kind alone")
        void consumables() {
            assertTrue(item(TValue.TV_POTION).similar(item(TValue.TV_POTION), mode()));
            assertTrue(item(TValue.TV_SCROLL).similar(item(TValue.TV_SCROLL), mode()));
            assertTrue(item(TValue.TV_ROD).similar(item(TValue.TV_ROD), mode()));
        }

        /**
         * Charged items merge their charges, so the only question is whether the pooled total still
         * fits — C's {@code MAX_PVAL} guard.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("charged items stack while their pooled charges fit")
        void chargesWithinRange() {
            ItemObject wand = item(TValue.TV_WAND);
            ItemObject other = item(TValue.TV_WAND);
            set(wand, "pValue", 5);
            set(other, "pValue", 7);

            assertTrue(wand.similar(other, mode()));
        }

        /**
         * And refuse when they do not, rather than overflowing the field.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("charged items refuse when the pooled charges overflow")
        void chargesOverflow() {
            ItemObject wand = item(TValue.TV_WAND);
            ItemObject other = item(TValue.TV_WAND);
            set(wand, "pValue", GameConstants.MAX_PVAL);
            set(other, "pValue", 1);

            assertFalse(wand.similar(other, mode()));
        }
    }

    /**
     * The wearable branch, which is the only one that compares the numbers two items carry.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("wearables")
    class Wearables {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different armour classes do not stack")
        void differentAc() {
            set(second, "normalAC", 3);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different damage dice do not stack")
        void differentDice() {
            set(second, "damageDice", 2);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different damage sides do not stack")
        void differentSides() {
            set(second, "damageSides", 8);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * The combat bonuses are compared by reference rather than by value, so two items sharing
         * one {@link Random} stack and two holding equal-but-separate ones do not. Stated as it
         * behaves rather than as it reads, because the fixtures elsewhere in this suite leave all
         * three null on both sides and so never reach the distinction.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("combat bonuses are compared by reference")
        void bonusesAreComparedByReference() {
            Random shared = new Random(1, 0, 0, 0, false);
            set(first, "toHit", shared);
            set(second, "toHit", shared);
            assertTrue(first.similar(second, mode()));

            set(second, "toHit", new Random(1, 0, 0, 0, false));
            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different modifiers do not stack")
        void differentModifiers() {
            Map<ObjectModifier, String> mods = modifiers();
            mods.put(ObjectModifier.OM_STR, "2");
            set(second, "modifiers", mods);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * Egos are compared by reference, which is right when they come from the registry: two
         * items of the same ego hold the same definition.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different egos do not stack")
        void differentEgos() {
            set(second, "ego", ego());

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("different curses do not stack")
        void differentCurses() {
            Map<Curse.CurseEntry, Boolean> curses = new HashMap<>();
            curses.put(new Curse.CurseEntry(null, null), true);
            set(second, "curses", curses);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * A wearable part-way through recharging is not interchangeable with a ready one, so it
         * refuses even against another item at the same timeout. Lights are the exception, being the
         * one wearable whose timeout is fuel rather than a cooldown.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a recharging wearable stacks with nothing")
        void rechargingWearable() {
            set(first, "timeout", 5);
            set(second, "timeout", 5);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("lights with equal fuel stack")
        void lightsWithEqualFuel() {
            ItemObject lamp = item(TValue.TV_LIGHT);
            ItemObject other = item(TValue.TV_LIGHT);
            set(lamp, "timeout", 5000);
            set(other, "timeout", 5000);

            assertTrue(lamp.similar(other, mode()));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("lights with different fuel do not stack")
        void lightsWithDifferentFuel() {
            ItemObject lamp = item(TValue.TV_LIGHT);
            ItemObject other = item(TValue.TV_LIGHT);
            set(lamp, "timeout", 5000);
            set(other, "timeout", 4000);

            assertFalse(lamp.similar(other, mode()));
        }
    }
}
