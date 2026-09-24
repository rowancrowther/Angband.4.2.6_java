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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.uichannel.UIEntryValue;
import uk.co.jackoftradesltd.channel.utils.Combiner;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.cached.CachedPlayerData;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link UIEntryValueRegistry}, the registry of {@link ObjectProperty}/{@link PlayerProperty}
 * bindings to named UI entries and the port of C's {@code compute_ui_entry_values_for_object},
 * {@code compute_ui_entry_values_for_player} and {@code is_ui_entry_for_known_rune}
 * ({@code ui-entry.c}).
 *
 * <p>Expected values throughout are derived from the C originals directly (a curse only
 * contributes with non-zero {@code power}, {@code UI_ENTRY_UNKNOWN_VALUE} for a property the
 * player cannot identify, {@code level / 3} for {@code PF_FAST_SHOT}, {@code timed / 20} for
 * {@code OM_BLOWS}'s timed contribution, and so on), not by mirroring what the port happens to
 * compute, so a test failing here means the two have actually diverged.
 *
 * <p>Every test uses the {@code CombinerName#ADD} combiner ({@link uk.co.jackoftradesltd.channel.utils.combiners.AddCombiner}),
 * which simply sums the value and auxiliary channels independently, so the expected totals below
 * are plain arithmetic over the individual contributions described in each test.
 *
 * <p>Class UIEntryValueRegistryTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class UIEntryValueRegistryTest {

    private static final String ENTRY = "TEST_ENTRY";

    private static Flag<ChannelEntryFlag> noEntryFlags() {
        return new Flag<>(ChannelEntryFlag.class);
    }

    // --- shared construction helpers -----------------------------------------------------

    private static Flag<ChannelEntryFlag> timedAsAuxEntryFlags() {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);
        return flags;
    }

    private static ObjectProperty.UIBinding uiBinding(Integer value, boolean aux) {
        return new ObjectProperty.UIBinding(ENTRY, value, aux);
    }

    private static ObjectProperty statOrModProperty(ObjPropertyType type, ObjectModifier modifier,
                                                    Integer value, boolean aux) {
        return new ObjectProperty(type, null, null,
                new ObjectPropertyTypeWrapper(type, modifier), 0, 0, Map.of(),
                "test", "", "", "", "", List.of(uiBinding(value, aux)));
    }

    private static ObjectProperty flagProperty(ObjectFlag flag, Integer value, boolean aux) {
        return new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, null, null,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag), 0, 0, Map.of(),
                "test", "", "", "", "", List.of(uiBinding(value, aux)));
    }

    private static ObjectProperty elementProperty(ObjPropertyType type, ElementEnum element,
                                                  Integer value, boolean aux) {
        return new ObjectProperty(type, null, null,
                new ObjectPropertyTypeWrapper(type, element), 0, 0, Map.of(),
                "test", "", "", "", "", List.of(uiBinding(value, aux)));
    }

    private static ObjectProperty unrecognisedTypeProperty() {
        return new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_NONE, null, null,
                null, 0, 0, Map.of(), "test", "", "", "", "", List.of(uiBinding(null, false)));
    }

    private static PlayerProperty playerFlagProperty(PlayerFlag pFlag, int value, boolean special, boolean aux) {
        return new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER, pFlag, null, null, null,
                List.of(new PlayerProperty.BindUI(ENTRY, value, special, aux)),
                "test", "", PlayerProperty.PlayerPropertyValue.NONE);
    }

    private static PlayerProperty objectFlagPlayerProperty(ObjectFlag oFlag, boolean aux) {
        return new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT, null, oFlag, null, null,
                List.of(new PlayerProperty.BindUI(ENTRY, 0, true, aux)),
                "test", "", PlayerProperty.PlayerPropertyValue.NONE);
    }

    private static PlayerProperty elementPlayerProperty(ElementEnum eCode, boolean aux) {
        return new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT, null, null, eCode, null,
                List.of(new PlayerProperty.BindUI(ENTRY, 0, true, aux)),
                "test", "", PlayerProperty.PlayerPropertyValue.NONE);
    }

    /**
     * An item built straight from the long constructor, with every collection live and the given
     * flags/modifiers/element-info/curses, following {@code ObjectUtilsObjectFlagIsKnownTest}'s
     * pattern.
     */
    private static ItemObject rawItem(Flag<ObjectFlag> flags, Map<ObjectModifier, Integer> modifiers,
                                      Map<ElementEnum, ElementInfo> elInfo,
                                      LinkedHashMap<Curse, CurseData> curses, ItemObject known) {
        return new ItemObject(null, null, null, known, Loc.zero, TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                flags, modifiers, elInfo, new HashSet<>(), new HashSet<>(), curses,
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, "");
    }

    private static ItemObject plainItem(ObjectFlag... flagsOn) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        for (ObjectFlag flag : flagsOn) flags.on(flag);
        return rawItem(flags, new HashMap<>(), new HashMap<>(), new LinkedHashMap<>(), null);
    }

    /**
     * An item whose flag is not knowable through any of {@code objectFlagIsKnown}'s three routes:
     * not fully known (a mismatched to-hit bonus against its own known-shadow, mirroring
     * {@code ObjectUtilsObjectFlagIsKnownTest#notFullyKnownItem}), the shadow does not carry the
     * flag, and the flag is raised only on the real object.
     */
    private static ItemObject itemWithUnknownFlag(ObjectFlag flag) {
        ItemObject known = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new LinkedHashMap<>(), null);
        ItemObject obj = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new LinkedHashMap<>(), known);
        obj.setToHit(3);
        obj.getFlags().on(flag);
        return obj;
    }

    private static Curse curseWithFlag(ObjectFlag flag) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.on(flag);
        return new Curse("Test Curse", List.of(), 0, null, flags, Map.of(), Map.of(),
                0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "");
    }

    private static Player newPlayer() {
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        return player;
    }

    /**
     * A race that contributes nothing except the given digging skill and infravision, following
     * {@link SeededPlayerRegistry#plainRace}'s "empty except what the test asks about" pattern.
     */
    private static PlayerRace race(PlayerBody body, int diggingSkill, int infravision) {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        skills.put(PlayerSkill.SKILL_DIGGING, diggingSkill);
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, infravision, body,
                stats, skills, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, new HashMap<>());
    }

    /**
     * A shape that contributes nothing: every skill and object modifier at zero, every element at
     * its neutral {@link ElementInfo}, no flags. Individual tests mutate the maps this returns
     * before use, since {@link PlayerShape} stores them by reference.
     */
    private static PlayerShape plainShape() {
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        Map<ObjectModifier, Integer> objectMods = new HashMap<>();
        for (ObjectModifier modifier : ObjectModifier.values()) objectMods.put(modifier, 0);
        Map<ElementEnum, ElementInfo> elMods = new HashMap<>();
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            elMods.put(element, new ElementInfo());
        }
        return new PlayerShape("Test Shape", 0, 0, 0, skills, new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), objectMods, elMods, List.of(), 0, List.of());
    }

    @BeforeEach
    void resetRegistry() {
        UIEntryValueRegistry.clearEntryBindings();
    }

    // --- registration: addEntryBinding / clearEntryBindings -------------------------------

    @Nested
    @DisplayName("an entry that was never registered")
    class UnregisteredEntry {

        @Test
        @DisplayName("computeForObject reports it as not present")
        void computeForObjectNotPresent() {
            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, plainItem(), null, new ObjectValueCache());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("computeForPlayer reports it as not present")
        void computeForPlayerNotPresent() {
            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, newPlayer(), new CachedPlayerData(), 0, 0);
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("isKnownRune reports false")
        void isKnownRuneFalse() {
            assertFalse(UIEntryValueRegistry.isKnownRune(ENTRY, newPlayer()));
        }
    }

    @Nested
    @DisplayName("addEntryBinding")
    class AddEntryBinding {

        @Test
        @DisplayName("a null object-property list on a later call keeps the one already registered")
        void nullObjectPropertiesPreservesTheExistingList() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());
            UIEntryValueRegistry.addEntryBinding(ENTRY, null, List.of(), CombinerName.ADD, noEntryFlags());

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, plainItem(ObjectFlag.OF_FREE_ACT),
                    null, new ObjectValueCache());
            assertEquals(1, result.val());
        }

        @Test
        @DisplayName("a non-null object-property list on a later call replaces the previous one")
        void nonNullObjectPropertiesReplacesThePreviousList() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_SEE_INVIS, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, plainItem(ObjectFlag.OF_FREE_ACT),
                    null, new ObjectValueCache());
            assertEquals(0, result.val(), "the OF_FREE_ACT binding should have been replaced, not merged");
        }
    }

    @Nested
    @DisplayName("clearEntryBindings")
    class ClearEntryBindings {

        @Test
        @DisplayName("removes a previously registered entry entirely")
        void removesAPreviouslyRegisteredEntry() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());
            UIEntryValueRegistry.clearEntryBindings();

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, plainItem(ObjectFlag.OF_FREE_ACT),
                    null, new ObjectValueCache());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
        }
    }

    // --- isKnownRune ------------------------------------------------------------------------

    @Nested
    @DisplayName("isKnownRune")
    class IsKnownRune {

        @Test
        @DisplayName("true when every bound object and player property is known")
        void trueWhenEverythingIsKnown() {
            Player player = newPlayer();
            player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_SEE_INVIS);
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_COLD);

            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(statOrModProperty(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STR, null, false),
                            flagProperty(ObjectFlag.OF_FREE_ACT, null, false),
                            elementProperty(ObjPropertyType.OBJ_PROPERTY_RESIST, ElementEnum.ELEM_FIRE, null, false)),
                    List.of(playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 0, true, false),
                            objectFlagPlayerProperty(ObjectFlag.OF_SEE_INVIS, false),
                            elementPlayerProperty(ElementEnum.ELEM_COLD, false)),
                    CombinerName.ADD, noEntryFlags());

            assertTrue(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }

        @Test
        @DisplayName("false when a bound stat/mod property is unknown")
        void falseWhenStatOrModUnknown() {
            Player player = newPlayer();
            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(statOrModProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            assertFalse(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }

        @Test
        @DisplayName("false when a bound flag property is unknown")
        void falseWhenFlagUnknown() {
            Player player = newPlayer();
            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            assertFalse(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }

        @Test
        @DisplayName("false when a bound ignore/resist/vuln/imm property is unknown")
        void falseWhenResistFamilyUnknown() {
            Player player = newPlayer();
            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(elementProperty(ObjPropertyType.OBJ_PROPERTY_IGNORE, ElementEnum.ELEM_FIRE, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            assertFalse(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }

        @Test
        @DisplayName("false for an object property of an unrecognised type")
        void falseForUnrecognisedObjectPropertyType() {
            Player player = newPlayer();
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(unrecognisedTypeProperty()),
                    List.of(), CombinerName.ADD, noEntryFlags());

            assertFalse(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }

        @Test
        @DisplayName("a PROP_TYPE_PLAYER player property is skipped and cannot force the result false")
        void playerTypePropertyIsSkipped() {
            Player player = newPlayer();
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 0, true, false)),
                    CombinerName.ADD, noEntryFlags());

            assertTrue(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }

        @Test
        @DisplayName("false when a PROP_TYPE_OBJECT player property's flag is unknown")
        void falseWhenObjectTypePlayerPropertyUnknown() {
            Player player = newPlayer();
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(objectFlagPlayerProperty(ObjectFlag.OF_SEE_INVIS, false)),
                    CombinerName.ADD, noEntryFlags());

            assertFalse(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }

        @Test
        @DisplayName("false when a PROP_TYPE_ELEMENT player property's element is unknown")
        void falseWhenElementTypePlayerPropertyUnknown() {
            Player player = newPlayer();
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(elementPlayerProperty(ElementEnum.ELEM_COLD, false)),
                    CombinerName.ADD, noEntryFlags());

            assertFalse(UIEntryValueRegistry.isKnownRune(ENTRY, player));
        }
    }

    // --- computeForObject ---------------------------------------------------------------------

    @Nested
    @DisplayName("computeForObject")
    class ComputeForObject {

        @Test
        @DisplayName("a null item reports not present")
        void nullItemNotPresent() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, null, null, new ObjectValueCache());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("a bound flag present on the item contributes 1")
        void boundFlagContributesItsRealValue() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, plainItem(ObjectFlag.OF_FREE_ACT),
                    null, new ObjectValueCache());
            assertEquals(1, result.val());
            assertEquals(0, result.auxVal());
        }

        @Test
        @DisplayName("a fixed bound value stands in for a non-zero real one")
        void fixedBoundValueOverridesTheRealOne() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, 5, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, plainItem(ObjectFlag.OF_FREE_ACT),
                    null, new ObjectValueCache());
            assertEquals(5, result.val());
        }

        @Test
        @DisplayName("an aux-bound property contributes to the auxiliary total, not the main one")
        void auxBoundPropertyRoutesToTheAuxiliaryTotal() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, true)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, plainItem(ObjectFlag.OF_FREE_ACT),
                    null, new ObjectValueCache());
            assertEquals(0, result.val(), "every bound property is auxiliary, so the main total is forced to 0");
            assertEquals(1, result.auxVal());
        }

        @Test
        @DisplayName("a curse with non-zero power contributes its own flag value")
        void curseWithNonZeroPowerContributes() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            Curse curse = curseWithFlag(ObjectFlag.OF_FREE_ACT);
            LinkedHashMap<Curse, CurseData> curses = new LinkedHashMap<>();
            curses.put(curse, new CurseData(1, 0));
            ItemObject item = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), curses, null);

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, item, null, new ObjectValueCache());
            assertEquals(1, result.val());
        }

        @Test
        @DisplayName("a curse with zero power contributes nothing")
        void curseWithZeroPowerContributesNothing() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            Curse curse = curseWithFlag(ObjectFlag.OF_FREE_ACT);
            LinkedHashMap<Curse, CurseData> curses = new LinkedHashMap<>();
            curses.put(curse, new CurseData(0, 0));
            ItemObject item = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), curses, null);

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, item, null, new ObjectValueCache());
            assertEquals(0, result.val());
        }

        @Test
        @DisplayName("a property unknown to the player contributes UI_ENTRY_UNKNOWN_VALUE")
        void unknownPropertyContributesTheUnknownSentinel() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(flagProperty(ObjectFlag.OF_FREE_ACT, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            Player player = newPlayer();
            ItemObject item = itemWithUnknownFlag(ObjectFlag.OF_FREE_ACT);

            UIEntryValue result = UIEntryValueRegistry.computeForObject(ENTRY, item, player, new ObjectValueCache());
            assertEquals(Combiner.UI_ENTRY_UNKNOWN_VALUE, result.val());
        }
    }

    // --- computeForPlayer ----------------------------------------------------------------------

    @Nested
    @DisplayName("computeForPlayer")
    class ComputeForPlayer {

        @Test
        @DisplayName("a null player reports not present")
        void nullPlayerNotPresent() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 0, true, false)),
                    CombinerName.ADD, noEntryFlags());

            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, null, new CachedPlayerData(), 0, 0);
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("PF_FAST_SHOT contributes 0 with no bow wielded")
        void fastShotContributesZeroWithNoLauncher() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(playerFlagProperty(PlayerFlag.PF_FAST_SHOT, 0, true, false)),
                    CombinerName.ADD, noEntryFlags());

            Player player = newPlayer();
            player.setLevel(30);
            PlayerState state = new PlayerState();
            state.playerFlagOn(PlayerFlag.PF_FAST_SHOT);
            player.setState(state);

            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData(), 0, 0);
            assertEquals(0, result.val());
        }

        @Test
        @DisplayName("PF_FAST_SHOT contributes level/3 with a bow that shoots arrows wielded")
        void fastShotContributesLevelDividedByThreeWithALauncher() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(playerFlagProperty(PlayerFlag.PF_FAST_SHOT, 0, true, false)),
                    CombinerName.ADD, noEntryFlags());

            Player player = newPlayer();
            player.setLevel(31);
            PlayerState state = new PlayerState();
            state.playerFlagOn(PlayerFlag.PF_FAST_SHOT);
            player.setState(state);

            ObjectKind bowKind = new ObjectKind();
            Flag<ObjectKindFlag> kindFlags = new Flag<>(ObjectKindFlag.class);
            kindFlags.on(ObjectKindFlag.KF_SHOOTS_ARROWS);
            ItemFixture.set(bowKind, "kindFlags", kindFlags);
            ItemObject bow = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                    new LinkedHashMap<>(), null);
            ItemFixture.set(bow, "kind", bowKind);
            player.getPlayerBody().getSlots().stream().filter(s -> "shooting".equals(s.getName()))
                    .findFirst().orElseThrow().setItem(bow);

            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData(), 0, 0);
            assertEquals(31 / 3, result.val());
        }

        @Test
        @DisplayName("PF_BRAVERY_30 contributes 0 below level 30 and 1 from level 30")
        void braveryThirtyContributesAtTheLevelThreshold() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 0, true, false)),
                    CombinerName.ADD, noEntryFlags());

            Player below = newPlayer();
            below.setLevel(29);
            PlayerState belowState = new PlayerState();
            belowState.playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            below.setState(belowState);
            assertEquals(0, UIEntryValueRegistry.computeForPlayer(ENTRY, below, new CachedPlayerData(), 0, 0).val());

            Player atThreshold = newPlayer();
            atThreshold.setLevel(30);
            PlayerState atState = new PlayerState();
            atState.playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            atThreshold.setState(atState);
            assertEquals(1, UIEntryValueRegistry.computeForPlayer(ENTRY, atThreshold, new CachedPlayerData(), 0, 0).val());
        }

        @Test
        @DisplayName("a PROP_TYPE_OBJECT property sums the untimed cache flag and the shape's own known flag")
        void objectTypePropertySumsCacheAndShapeContributions() {
            UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                    List.of(objectFlagPlayerProperty(ObjectFlag.OF_FREE_ACT, false)),
                    CombinerName.ADD, noEntryFlags());

            Player player = newPlayer();
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            PlayerShape shape = plainShape();
            shape.getFlags().on(ObjectFlag.OF_FREE_ACT);
            player.setShape(shape);

            CachedPlayerData cache = new CachedPlayerData();
            cache.onUntimedFlag(ObjectFlag.OF_FREE_ACT);

            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, cache, 0, 0);
            assertEquals(2, result.val(), "untimed cache (1) plus the shape's own known flag (1)");
        }

        @Test
        @DisplayName("an OM_TUNNEL stat/mod property adds the racial digging-skill contribution")
        void tunnelModifierAddsTheRacialSkillContribution() {
            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(statOrModProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_TUNNEL, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            Player player = newPlayer();
            player.setRace(race(player.getPlayerBody(), 100, 0));
            PlayerShape shape = plainShape();
            shape.getObjectValueModifiers().put(ObjectModifier.OM_TUNNEL, 3);
            player.setShape(shape);

            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData(), 0, 0);
            assertEquals(3 + (100 * 1) / 20, result.val(), "shape modifier plus race digging skill / 20");
        }

        @Test
        @DisplayName("an OM_INFRA stat/mod property adds the race's own infravision")
        void infraModifierAddsTheRacesInfravision() {
            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(statOrModProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_INFRA, null, false)),
                    List.of(), CombinerName.ADD, noEntryFlags());

            Player player = newPlayer();
            player.setRace(race(player.getPlayerBody(), 0, 4));
            PlayerShape shape = plainShape();
            player.setShape(shape);

            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData(), 0, 0);
            assertEquals(4, result.val(), "shape modifier (0) plus the race's own infravision (4)");
        }

        @Test
        @DisplayName("ENTRY_FLAG_TIMED_AS_AUX substitutes the timed-effect value as the auxiliary channel")
        void timedAsAuxSubstitutesTheTimedEffectAsTheAuxiliaryChannel() {
            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(statOrModProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_BLOWS, null, false)),
                    List.of(), CombinerName.ADD, timedAsAuxEntryFlags());

            Player player = newPlayer();
            player.setRace(race(player.getPlayerBody(), 0, 0));
            PlayerShape shape = plainShape();
            player.setShape(shape);
            player.putTimed(TimedEffect.TMD_BLOODLUST, 40);

            UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData(), 0, 0);
            assertEquals(0, result.val(), "the shape's own OM_BLOWS modifier");
            assertEquals(40 / 20, result.auxVal(), "TMD_BLOODLUST / 20, per get_timed_modifier_effect");
        }
    }
}
