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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#playerOutfit(Player)}, the port of C's {@code player_outfit}
 * ({@code player-birth.c:586-672}).
 *
 * <p>The expected values throughout are derived from the C original, not from the port: the gold
 * deducted for a fixed-price item is {@code kind.cost * quantity} ({@code obj-power.c:1101}'s
 * non-variable-power branch), and the four {@link StartOptionExclusion} cases in
 * {@link ExclusionOptions} are read straight off C's sign-encoded {@code eopts} walk
 * ({@code player-birth.c:621-639}) rather than off the port's own {@code if}/{@code else}. Every
 * item built here uses a fixed-price tval ({@code TV_FOOD}, {@code TV_POTION}, {@code TV_SCROLL}) so
 * that pricing goes through the simple {@code cost * quantity} route rather than the
 * {@code objectPower} curve every wearable tval takes — {@code playerOutfit} prices every start
 * item, wearable or not, so a variable-power item would drag the whole of {@code objectPower} into
 * this suite's fixture for no benefit to what is being pinned down here.
 *
 * <p>The registry lists {@code objectKinds}, {@code kindsByTvalSval} and {@code objectProperties}
 * are reset before every test, following {@code ObjectUtilsLookupKindTest}'s snapshot/restore
 * pattern, since {@code playerOutfit} reads both through {@link ObjectRegistry#lookupObjectProperty}
 * and {@link uk.co.jackoftradesltd.middle.objects.ObjectUtils#lookupKind}. The player itself is
 * built through {@link CalcBonusesFixture}, whose {@code plainClass()} shape is reused here with the
 * test's own {@link StartItem} list stitched in, and {@link GameState#setPlayer} is set before any
 * item is constructed — every {@link ItemObject} constructor reads it, as
 * {@code PlayerBirthWieldAllTest} already documents.
 *
 * <p>Class PlayerBirthPlayerOutfitTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthPlayerOutfitTest {

    private static final List<String> TOUCHED = List.of("objectKinds", "kindsByTvalSval", "objectProperties");
    private static final Map<String, Object> saved = new HashMap<>();

    private Player savedGamePlayer;

    @BeforeAll
    static void snapshot() throws Exception {
        for (String name : TOUCHED) {
            saved.put(name, field(name).get(null));
        }
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : TOUCHED) {
            field(name).set(null, saved.get(name));
        }
    }

    private static Field field(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * A fixed-price kind, registered so {@link uk.co.jackoftradesltd.middle.objects.ObjectUtils#lookupKind}
     * can find it by tval and sval name — the shape {@code ObjectUtilsLookupKindTest} already
     * establishes, built with {@link ItemFixture#kindWithDice}'s trivial dice so
     * {@code ObjectUtils.objectPrep} has non-null {@code toH}/{@code toD}/{@code toA}/{@code pVal}
     * to read regardless of tval.
     *
     * @param tValue     the kind's type
     * @param sValueName the sval name a {@link StartItem} refers to it by
     * @param cost       the kind's listed cost
     * @return the registered kind
     */
    private static ObjectKind kind(TValue tValue, String sValueName, int cost) {
        ObjectBase base = new ObjectBase(tValue, "test-base", null,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, 99);
        ObjectKind kind = new ObjectKind(null, cost, 0, 0, 0, "test", tValue, sValueName, base, false);
        for (String name : new String[]{"toH", "toD", "toA", "pVal"}) {
            ItemFixture.set(kind, name, new Random(0, 1, 1, 1, false));
        }
        ItemFixture.set(kind, "ignore", new Flag<>(uk.co.jackoftradesltd.middle.objects.enums.IgnoreFlag.class));
        ObjectRegistry.addObjectKind(kind);
        return kind;
    }

    /**
     * A class {@link StartItem} with no exclusions.
     */
    private static StartItem startItem(TValue tValue, String sValueName, int min, int max) {
        return new StartItem(tValue, sValueName, min, max, List.of());
    }

    /**
     * A class that contributes nothing beyond the given starting equipment — the same neutral shape
     * as {@link CalcBonusesFixture#plainClass()}, with the test's own {@link StartItem} list.
     */
    private static PlayerClass classWithItems(List<StartItem> items) {
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
        return new PlayerClass("Test Class", List.of(), stats, skills, extra, 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, items, ClassMagic.NONE);
    }

    /**
     * Flips one of the player's birth options — {@link PlayerOptions} exposes no setter, since C
     * reaches the array directly too.
     */
    private static void setOption(Player player, PlayerOptionEnum option, boolean on) throws Exception {
        Field optionsField = PlayerOptions.class.getDeclaredField("options");
        optionsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Flag<PlayerOptionEnum> flags = (Flag<PlayerOptionEnum>) optionsField.get(player.getPlayerOptions());
        if (on) flags.on(option);
        else flags.off(option);
    }

    private static boolean gearContainsKind(Player player, ObjectKind kind) {
        var it = player.getGear().getIterator();
        while (it.hasNext()) {
            if (it.next().getKind() == kind) return true;
        }
        return false;
    }

    private static ItemObject gearItemOfKind(Player player, ObjectKind kind) {
        var it = player.getGear().getIterator();
        while (it.hasNext()) {
            ItemObject candidate = it.next();
            if (candidate.getKind() == kind) return candidate;
        }
        return null;
    }

    @BeforeEach
    void isolateRegistry() throws Exception {
        field("objectKinds").set(null, new ArrayList<ObjectKind>());
        field("kindsByTvalSval").set(null, new HashMap<TValue, Map<Integer, ObjectKind>>());
        ObjectRegistry.setObjectProperties(new ArrayList<>());

        savedGamePlayer = GameState.getPlayer();
    }

    /**
     * Builds a fully-calculated character carrying the given starting equipment, and installs it as
     * {@link GameState}'s live player before any item is constructed.
     */
    private Player buildPlayer(List<StartItem> items) throws Exception {
        Player player = new Player();
        CalcBonusesFixture.plainCharacter(player).playerClass(classWithItems(items)).calculate();
        GameState.setPlayer(player);
        return player;
    }

    // ---- obvious knowledge -------------------------------------------------

    @Test
    @DisplayName("dd, ds and ac are set to 1 even with no starting equipment")
    void obviousKnowledgeAlwaysSet() throws Exception {
        Player player = buildPlayer(List.of());

        PlayerBirth.playerOutfit(player);

        assertEquals(1, player.getItemKnowledge().getDd());
        assertEquals(1, player.getItemKnowledge().getDs());
        assertEquals(1, player.getItemKnowledge().getAc());
    }

    // ---- the obvious-flag loop ----------------------------------------------

    @Test
    @DisplayName("a flag whose property subtype is OFT_LIGHT is learned as obvious")
    void obviousFlagLearnedForMatchingSubtype() throws Exception {
        Player player = buildPlayer(List.of());
        ObjectProperty property = new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlagType.OFT_LIGHT,
                ObjectFlagID.OFID_NORMAL,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_SEE_INVIS),
                0, 0, Map.of(), "test", "test", "test", "test", "test", List.of());
        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(property)));

        PlayerBirth.playerOutfit(player);

        assertTrue(player.getItemKnowledge().flagIsKnown(ObjectFlag.OF_SEE_INVIS));
    }

    @Test
    @DisplayName("a flag whose property subtype is not one of the four obvious kinds is not learned")
    void flagWithOtherSubtypeIsNotLearned() throws Exception {
        Player player = buildPlayer(List.of());
        ObjectProperty property = new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlagType.OFT_SUST,
                ObjectFlagID.OFID_NORMAL,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_IMPAIR_HP),
                0, 0, Map.of(), "test", "test", "test", "test", "test", List.of());
        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(property)));

        PlayerBirth.playerOutfit(player);

        assertFalse(player.getItemKnowledge().flagIsKnown(ObjectFlag.OF_IMPAIR_HP));
    }

    @Test
    @DisplayName("a flag with no registered property is skipped rather than crashing")
    void flagWithNoPropertyIsSkippedWithoutThrowing() throws Exception {
        Player player = buildPlayer(List.of());

        assertDoesNotThrow(() -> PlayerBirth.playerOutfit(player));

        assertFalse(player.getItemKnowledge().flagIsKnown(ObjectFlag.OF_SUST_STR));
    }

    // ---- the starting-equipment loop ----------------------------------------

    @Test
    @DisplayName("an ordinary start item is bought, carried and marked known")
    void ordinaryStartItemIsBoughtAndCarried() throws Exception {
        ObjectKind food = kind(TValue.TV_FOOD, "Ration of Food", 3);
        Player player = buildPlayer(List.of(startItem(TValue.TV_FOOD, "Ration of Food", 2, 2)));
        setOption(player, PlayerOptionEnum.OP_birth_start_kit, true);
        player.setAU(100);

        PlayerBirth.playerOutfit(player);

        ItemObject carried = gearItemOfKind(player, food);
        assertNotNull(carried, "the food item should have been carried");
        assertEquals(2, carried.getNumber());
        assertEquals(100 - 3 * 2, player.getAU(), "gold is the kind's fixed cost times the quantity");
        assertTrue(food.isEverseen());
        assertTrue(food.isAware(), "flavourAware should have run for the new item");
    }

    @Test
    @DisplayName("without birth_start_kit, only food and light are granted, forced to one")
    void withoutStartKitOnlyFoodIsGrantedAtOne() throws Exception {
        ObjectKind food = kind(TValue.TV_FOOD, "Ration of Food", 3);
        ObjectKind potion = kind(TValue.TV_POTION, "Cure Light Wounds", 5);
        Player player = buildPlayer(List.of(
                startItem(TValue.TV_FOOD, "Ration of Food", 3, 3),
                startItem(TValue.TV_POTION, "Cure Light Wounds", 2, 2)));
        setOption(player, PlayerOptionEnum.OP_birth_start_kit, false);
        player.setAU(100);

        PlayerBirth.playerOutfit(player);

        ItemObject carriedFood = gearItemOfKind(player, food);
        assertNotNull(carriedFood, "food is granted even without the start kit");
        assertEquals(1, carriedFood.getNumber(), "food is forced to one without the start kit");
        assertFalse(gearContainsKind(player, potion),
                "a non-food, non-light item is skipped entirely without the start kit");
    }

    @Test
    @DisplayName("gold is clamped to zero rather than going negative")
    void goldClampedToZeroWhenKitIsExpensive() throws Exception {
        kind(TValue.TV_FOOD, "Ration of Food", 50);
        Player player = buildPlayer(List.of(startItem(TValue.TV_FOOD, "Ration of Food", 3, 3)));
        setOption(player, PlayerOptionEnum.OP_birth_start_kit, true);
        player.setAU(10);

        PlayerBirth.playerOutfit(player);

        assertEquals(0, player.getAU());
    }

    @Test
    @DisplayName("a start item referencing an unregistered kind throws")
    void missingKindThrows() throws Exception {
        // A decoy kind, registered only so the registry is non-empty and the lookup takes the
        // real "no match" path rather than ObjectRegistry's own empty-list guard.
        kind(TValue.TV_POTION, "Decoy", 1);
        Player player = buildPlayer(List.of(startItem(TValue.TV_FOOD, "Nonexistent Ration", 1, 1)));
        setOption(player, PlayerOptionEnum.OP_birth_start_kit, true);

        assertThrows(RuntimeException.class, () -> PlayerBirth.playerOutfit(player));
    }

    // ---- the eopts exclusion logic (the fixed dangling-else bug) ------------

    /**
     * The four cases {@link PlayerBirth#playerOutfit}'s {@code eopts} walk has to distinguish -
     * negated x option-set - matching C's sign-encoded {@code eopts} array
     * ({@code player-birth.c:621-639}) exactly.
     */
    @org.junit.jupiter.api.Nested
    @DisplayName("exclusion options")
    class ExclusionOptions {

        private ObjectKind scrollKind;

        @BeforeEach
        void registerScroll() {
            scrollKind = kind(TValue.TV_SCROLL, "Word of Recall", 5);
        }

        private Player buildWithExclusion(boolean negated, boolean optionSet) throws Exception {
            StartItem scroll = new StartItem(TValue.TV_SCROLL, "Word of Recall", 1, 1,
                    List.of(new StartOptionExclusion(PlayerOptionEnum.OP_birth_no_recall, negated)));
            Player player = buildPlayer(List.of(scroll));
            setOption(player, PlayerOptionEnum.OP_birth_start_kit, true);
            setOption(player, PlayerOptionEnum.OP_birth_no_recall, optionSet);
            player.setAU(100);
            return player;
        }

        @Test
        @DisplayName("non-negated, option set: excluded")
        void nonNegatedOptionSetIsExcluded() throws Exception {
            Player player = buildWithExclusion(false, true);
            PlayerBirth.playerOutfit(player);
            assertFalse(gearContainsKind(player, scrollKind));
        }

        @Test
        @DisplayName("non-negated, option unset: included")
        void nonNegatedOptionUnsetIsIncluded() throws Exception {
            Player player = buildWithExclusion(false, false);
            PlayerBirth.playerOutfit(player);
            assertTrue(gearContainsKind(player, scrollKind));
        }

        @Test
        @DisplayName("negated, option set: included")
        void negatedOptionSetIsIncluded() throws Exception {
            Player player = buildWithExclusion(true, true);
            PlayerBirth.playerOutfit(player);
            assertTrue(gearContainsKind(player, scrollKind));
        }

        @Test
        @DisplayName("negated, option unset: excluded")
        void negatedOptionUnsetIsExcluded() throws Exception {
            Player player = buildWithExclusion(true, false);
            PlayerBirth.playerOutfit(player);
            assertFalse(gearContainsKind(player, scrollKind));
        }
    }
}
