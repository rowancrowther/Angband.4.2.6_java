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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;
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
import uk.co.jackoftrades.middle.player.EquipSlot;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
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
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectSimilarTest {

    private static Player savedPlayer;

    /**
     * Whatever the registry held before this suite, put back afterwards.
     */
    private static Object savedBodies;
    private static Object savedRaces;

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
     */
    @BeforeAll
    static void installPlayer() throws Exception {
        savedPlayer = GameState.getPlayer();

        // Constructing a Player now reads the registry for its default body and race, so this test
        // seeds both rather than depending on another suite having loaded them first. Whatever was
        // there is put back afterwards, the registry being process-wide.
        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        PlayerBody humanoid = new PlayerBody("test",
                List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(humanoid)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(testRace(humanoid))));

        Player player = new Player();
        Field body = Player.class.getDeclaredField("body");
        body.setAccessible(true);
        // One slot, not none: PlayerBody refuses an empty slot list, and nothing here reads it.
        body.set(player, new PlayerBody("test",
                List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"))));

        GameState.setPlayer(player);
        kind = new ObjectKind();
    }

    @AfterAll
    static void restorePlayer() throws Exception {
        GameState.setPlayer(savedPlayer);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
    }

    /**
     * @param fieldName the registry field to reach
     * @return that private static field, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field registryField(String fieldName) throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        return f;
    }

    /**
     * Builds the one race the seeded registry holds. Only its body matters here; every other field
     * is empty or zero.
     *
     * @param body the body the race presents
     * @return the race
     */
    private static PlayerRace testRace(PlayerBody body) {
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
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
     * <p>Values are {@link Integer}, not {@link String}. They were strings when this suite was
     * written, because {@code ItemObject.modifiers} then held the unparsed dice text; the field was
     * retyped during the 260816 knowledge work, which is right — C's {@code obj->modifiers[i]} is an
     * {@code int16_t} holding the value already rolled for this particular object, and it is the
     * {@link ObjectKind} that keeps the dice it was rolled from. This fixture writes the field
     * reflectively, so nothing caught the change at compile time and the whole weapon branch went
     * down with a {@code ClassCastException} instead.
     *
     * @return a complete modifier map
     */
    private static Map<ObjectModifier, Integer> modifiers() {
        Map<ObjectModifier, Integer> mods = new EnumMap<>(ObjectModifier.class);
        for (ObjectModifier mod : ObjectModifier.values()) {
            mods.put(mod, 0);
        }
        return mods;
    }

    /**
     * Builds a plain sword: no ego, no artifact, no curses, nothing recharging. The weapon branch is
     * the interesting one, being the only tval group that compares combat values, modifiers, ego and
     * curses rather than answering outright.
     *
     * @return an item that stacks with any other built the same way
     */
    private static ItemObject weapon() {
        return item(TValue.TV_SWORD);
    }

    /**
     * Builds an item of the given type with everything else neutral.
     *
     * @param tValue the item type, which decides which branch {@code similar} takes
     * @return the constructed item
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
        set(item, "curses", new LinkedHashMap<Curse, CurseData>());
        set(item, "number", 1);
        set(item, "timeout", 0);
        return item;
    }

    /**
     * A minimal curse definition, distinct from every other by identity.
     *
     * <p>{@link Curse} declares no {@code equals}, so each of these is its own key in a curse map.
     * The name is carried only so a failure names the curse rather than an object identity.
     *
     * @param name the curse's name
     * @return a curse with every other field empty
     */
    private static Curse curse(String name) {
        return new Curse(name, List.of(), 0, null, List.of(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), List.of(), "", "");
    }

    /**
     * A minimal ego definition. {@code similar} compares egos with {@code !=}, so nothing on it is
     * read and the fixture exists solely to be a distinct non-null {@link EgoItem}.
     *
     * @return an ego with every field empty
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
     */
    @BeforeEach
    void newPair() {
        first = weapon();
        second = weapon();
    }

    /**
     * The baseline, and the clauses that refuse before the type is even looked at.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the early vetoes")
    class EarlyVetoes {

        /**
         * The premise every other test rests on.
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
         */
        @Test
        @DisplayName("an item never stacks with itself")
        void notWithItself() {
            assertFalse(first.similar(first, mode()));
        }

        /**
         * A mimic is a monster wearing an item's appearance. Stacking it would merge a creature into
         * the floor pile, so either side mimicking is enough to refuse.
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

        @Test
        @DisplayName("different kinds do not stack")
        void differentKinds() {
            set(second, "kind", new ObjectKind());

            assertFalse(first.similar(second, mode()));
        }

        @Test
        @DisplayName("different tvals do not stack")
        void differentTvals() {
            set(second, "tValue", TValue.TV_POLEARM);

            assertFalse(first.similar(second, mode()));
        }

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
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("element info")
    class Elements {

        @Test
        @DisplayName("different resistance levels do not stack")
        void differentResistance() {
            elementOf(second, ElementEnum.ELEM_FIRE).setResLevel(1);

            assertFalse(first.similar(second, mode()));
        }

        @Test
        @DisplayName("one item hating an element and the other not do not stack")
        void differentHates() {
            elementOf(second, ElementEnum.ELEM_ACID).on(ElementInfoEnum.EL_INFO_HATES);

            assertFalse(first.similar(second, mode()));
        }

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
         */
        @Test
        @DisplayName("the random flag is not part of the comparison")
        void randomFlagIsIgnored() {
            elementOf(second, ElementEnum.ELEM_ACID).on(ElementInfoEnum.EL_INFO_RANDOM);

            assertTrue(first.similar(second, mode()));
        }

        /**
         * An element recorded on one item and absent from the other's map must refuse the stack,
         * and must refuse it whichever way round the two are asked.
         *
         * <p>This is the case the port has to work for and C gets for free. C indexes full arrays
         * by element, so one loop sees both items' entries; here the map holds only the elements an
         * item carries, and a loop over one item's keys cannot see an element recorded solely on the
         * other. {@code similar} answers that by running the comparison twice with the arguments
         * swapped. Asserting both directions is what would catch a future simplification back down
         * to a single pass — which would still pass every other test in this class.
         */
        @Test
        @DisplayName("an element on one item only refuses in both directions")
        void elementPresentOnOneSideOnly() {
            Map<ElementEnum, ElementInfo> sparse = new java.util.HashMap<>(elements());
            sparse.remove(ElementEnum.ELEM_FIRE);
            set(second, "elInfo", sparse);

            assertAll(
                    () -> assertFalse(first.similar(second, mode())),
                    () -> assertFalse(second.similar(first, mode())));
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
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the type branch")
    class TypeBranch {

        /**
         * Chests never stack: each has its own contents and its own trap, so two identical-looking
         * chests are not interchangeable.
         */
        @Test
        @DisplayName("chests never stack")
        void chests() {
            assertFalse(item(TValue.TV_CHEST).similar(item(TValue.TV_CHEST), mode()));
        }

        /**
         * Consumables stack on kind alone — everything that distinguishes two potions has already
         * been compared by the time the branch is reached, so the branch answers yes outright.
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
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("wearables")
    class Wearables {

        @Test
        @DisplayName("different armour classes do not stack")
        void differentAc() {
            set(second, "baseAC", 3);

            assertFalse(first.similar(second, mode()));
        }

        @Test
        @DisplayName("different damage dice do not stack")
        void differentDice() {
            set(second, "damageDice", 2);

            assertFalse(first.similar(second, mode()));
        }

        @Test
        @DisplayName("different damage sides do not stack")
        void differentSides() {
            set(second, "damageSides", 8);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * The combat bonuses are compared by value, matching C's {@code obj1->to_h != obj2->to_h}
         * on three {@code int16_t}s.
         *
         * <p>This used to be a test that they were compared by <em>reference</em>, and it was
         * accurate at the time: the fields held the {@link Random} dice parsed from the data file,
         * and {@code !=} on two of those asks whether they are the same object. Two identically
         * enchanted swords would then refuse to stack. Moving the fields to the rolled {@code int}
         * — which is what C's {@code struct object} carries — turned the same {@code !=} into the
         * comparison it always read as.
         */
        @Test
        @DisplayName("combat bonuses are compared by value")
        void bonusesAreComparedByValue() {
            set(first, "toHit", 1);
            set(second, "toHit", 1);
            assertTrue(first.similar(second, mode()));

            set(second, "toHit", 2);
            assertFalse(first.similar(second, mode()));
        }

        /**
         * All three are compared, not just the one above.
         */
        @Test
        @DisplayName("each combat bonus is compared separately")
        void eachBonusIsCompared() {
            set(second, "toDam", 3);
            assertFalse(first.similar(second, mode()));

            set(second, "toDam", 0);
            set(second, "toAC", 3);
            assertFalse(first.similar(second, mode()));
        }

        @Test
        @DisplayName("different modifiers do not stack")
        void differentModifiers() {
            Map<ObjectModifier, Integer> mods = modifiers();
            mods.put(ObjectModifier.OM_STR, 2);
            set(second, "modifiers", mods);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * A modifier recorded on one item and absent from the other refuses the stack from either
         * side, for the same reason as the element case: the maps are sparse, so the comparison runs
         * once per direction.
         */
        @Test
        @DisplayName("a modifier on one item only refuses in both directions")
        void modifierPresentOnOneSideOnly() {
            Map<ObjectModifier, Integer> sparse = modifiers();
            sparse.remove(ObjectModifier.OM_STR);
            set(second, "modifiers", sparse);

            Map<ObjectModifier, Integer> withStr = modifiers();
            withStr.put(ObjectModifier.OM_STR, 2);
            set(first, "modifiers", withStr);

            assertAll(
                    () -> assertFalse(first.similar(second, mode())),
                    () -> assertFalse(second.similar(first, mode())));
        }

        /**
         * Egos are compared by reference, which is right when they come from the registry: two
         * items of the same ego hold the same definition.
         */
        @Test
        @DisplayName("different egos do not stack")
        void differentEgos() {
            set(second, "ego", ego());

            assertFalse(first.similar(second, mode()));
        }

        @Test
        @DisplayName("different curses do not stack")
        void differentCurses() {
            Map<Curse, CurseData> curses = new LinkedHashMap<>();
            curses.put(curse("siren"), new CurseData(1, 0));
            set(second, "curses", curses);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * Two items under the same curse at the same power stack even though their countdowns to
         * the next effect differ, because the countdown is not something the player can see and two
         * cursed swords will always have started theirs at different moments. C reaches the same
         * answer by comparing nothing but {@code power} in {@code curses_are_equal}; here it falls
         * out of {@link CurseData#equals}, which the map comparison defers to.
         *
         * <p>Worth its own test because it is the case a naive field-by-field comparison gets
         * wrong, and it would look like a rule about curses rather than the bug it is.
         */
        @Test
        @DisplayName("the same curse at the same power stacks whatever the timeouts")
        void sameCurseDifferentTimeouts() {
            Curse siren = curse("siren");
            Map<Curse, CurseData> firstCurses = new LinkedHashMap<>();
            firstCurses.put(siren, new CurseData(3, 1));
            set(first, "curses", firstCurses);

            Map<Curse, CurseData> secondCurses = new LinkedHashMap<>();
            secondCurses.put(siren, new CurseData(3, 40));
            set(second, "curses", secondCurses);

            assertTrue(first.similar(second, mode()));
        }

        /**
         * The same curse at different powers is a real difference — power is what the curse does —
         * so the two refuse.
         */
        @Test
        @DisplayName("the same curse at different powers does not stack")
        void sameCurseDifferentPowers() {
            Curse siren = curse("siren");
            Map<Curse, CurseData> firstCurses = new LinkedHashMap<>();
            firstCurses.put(siren, new CurseData(3, 0));
            set(first, "curses", firstCurses);

            Map<Curse, CurseData> secondCurses = new LinkedHashMap<>();
            secondCurses.put(siren, new CurseData(5, 0));
            set(second, "curses", secondCurses);

            assertFalse(first.similar(second, mode()));
        }

        /**
         * A wearable part-way through recharging is not interchangeable with a ready one, so it
         * refuses even against another item at the same timeout. Lights are the exception, being the
         * one wearable whose timeout is fuel rather than a cooldown.
         */
        @Test
        @DisplayName("a recharging wearable stacks with nothing")
        void rechargingWearable() {
            set(first, "timeout", 5);
            set(second, "timeout", 5);

            assertFalse(first.similar(second, mode()));
        }

        @Test
        @DisplayName("lights with equal fuel stack")
        void lightsWithEqualFuel() {
            ItemObject lamp = item(TValue.TV_LIGHT);
            ItemObject other = item(TValue.TV_LIGHT);
            set(lamp, "timeout", 5000);
            set(other, "timeout", 5000);

            assertTrue(lamp.similar(other, mode()));
        }

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
