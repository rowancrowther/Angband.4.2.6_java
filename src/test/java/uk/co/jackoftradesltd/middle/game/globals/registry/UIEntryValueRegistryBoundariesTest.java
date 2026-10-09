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
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.cached.CachedPlayerData;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Boundary cases for {@link UIEntryValueRegistry} that {@link UIEntryValueRegistryTest} does not reach:
 * every object-property family in {@code compute_ui_entry_values_for_object} (modifier, stat, ignore,
 * resist, immunity, vulnerability), the {@code have_value} override and the unknown sentinel for each,
 * the auxiliary and {@code ENTRY_FLAG_TIMED_AUX} bookkeeping, curse order, and, for
 * {@code compute_ui_entry_values_for_player}, the ability types, the shape and race contributions,
 * {@code get_timed_modifier_effect} and {@code modifier_to_skill}.
 *
 * <p>Expected values come from {@code ui-entry.c} and {@code ui-entry-combiner.c}, not from running the
 * port. Every test uses {@link CombinerName#ADD} except the one that checks curse order, which uses
 * {@link CombinerName#LAST} because only an order-sensitive combiner can show it. C's ADD combiner
 * ignores {@code UI_ENTRY_UNKNOWN_VALUE} added to a real number and takes a real number over
 * {@code UI_ENTRY_VALUE_NOT_PRESENT}; the expected totals below follow that.
 *
 * <p>Class UIEntryValueRegistryBoundariesTest coded on 261006, commented in full on 261006.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class UIEntryValueRegistryBoundariesTest {
    private static final String ENTRY = "BOUNDARY_ENTRY";

    private static Flag<ChannelEntryFlag> noEntryFlags() {
        return new Flag<>(ChannelEntryFlag.class);
    }

    // --- construction helpers ------------------------------------------------------------------

    private static Flag<ChannelEntryFlag> timedAsAuxEntryFlags() {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);
        return flags;
    }

    private static ObjectProperty modProperty(ObjPropertyType type, ObjectModifier modifier, Integer value,
                                              boolean aux) {
        return new ObjectProperty(type, null, null, new ObjectPropertyTypeWrapper(type, modifier), 0, 0,
                Map.of(), "test", "", "", "", "",
                List.of(new ObjectProperty.UIBinding(ENTRY, value, aux)));
    }

    private static ObjectProperty flagProperty(ObjectFlag flag, Integer value, boolean aux) {
        return new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, null, null,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag), 0, 0, Map.of(),
                "test", "", "", "", "", List.of(new ObjectProperty.UIBinding(ENTRY, value, aux)));
    }

    private static ObjectProperty elementProperty(ObjPropertyType type, ElementEnum element, Integer value,
                                                  boolean aux) {
        return new ObjectProperty(type, null, null, new ObjectPropertyTypeWrapper(type, element), 0, 0,
                Map.of(), "test", "", "", "", "",
                List.of(new ObjectProperty.UIBinding(ENTRY, value, aux)));
    }

    private static PlayerProperty playerFlagProperty(PlayerFlag pFlag, int value, boolean special, boolean aux) {
        return new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER, pFlag, null, null, null,
                List.of(new PlayerProperty.BindUI(ENTRY, value, special, aux)),
                "test", "", 0);
    }

    private static PlayerProperty objectFlagPlayerProperty(ObjectFlag oFlag, boolean aux) {
        return new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT, null, oFlag, null, null,
                List.of(new PlayerProperty.BindUI(ENTRY, 0, true, aux)),
                "test", "", 0);
    }

    private static PlayerProperty elementPlayerProperty(ElementEnum eCode, boolean aux) {
        return new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT, null, null, eCode, null,
                List.of(new PlayerProperty.BindUI(ENTRY, 0, true, aux)),
                "test", "", 0);
    }

    private static ItemObject rawItem(Flag<ObjectFlag> flags, Map<ObjectModifier, Integer> modifiers,
                                      Map<ElementEnum, ElementInfo> elInfo, ItemObject known) {
        return new ItemObject(null, null, null, known, Loc.zero, TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                flags, modifiers, elInfo, new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, "");
    }

    /**
     * An item with a blank known shadow, which {@code object_flags_known} needs when a player is given.
     */
    private static ItemObject plainItem() {
        ItemObject known = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), null);
        return rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), known);
    }

    private static ItemObject itemWithModifier(ObjectModifier modifier, int value) {
        ItemObject item = plainItem();
        item.putModifier(modifier, value);
        return item;
    }

    private static ItemObject itemWithElement(ElementEnum element, int resLevel, boolean ignored) {
        ItemObject item = plainItem();
        ElementInfo info = new ElementInfo();
        info.setResLevel(resLevel);
        if (ignored) info.on(ElementInfoEnum.EL_INFO_IGNORE);
        item.putElInfo(element, info);
        return item;
    }

    /**
     * An item that is not fully known (its real to-hit is off its known shadow's), so a player's
     * knowledge of {@code element} is the only way to see its resistance.
     */
    private static ItemObject itemWithUnknownResist(ElementEnum element, int resLevel) {
        ItemObject known = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), null);
        ItemObject item = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), known);
        item.setToHit(3);
        ElementInfo info = new ElementInfo();
        info.setResLevel(resLevel);
        item.putElInfo(element, info);
        return item;
    }

    private static Curse curse(int index, ItemObject curseObject) {
        return new Curse("Boundary Curse " + index, List.of(), curseObject, List.of(),
                new Flag<>(ObjectFlag.class), "", index);
    }

    private static void carry(ItemObject item, Curse curse, int power) {
        Map<Curse, CurseData> curses = new LinkedHashMap<>(item.getCurses());
        curses.put(curse, new CurseData(power, 0));
        item.setCurses(curses);
    }

    private static Player newPlayer() {
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        player.setClass(new PlayerClass("Test Class", List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 0, 0, 0, List.of(), null));
        // playerFlags() reads the race's flags, as C's player_flags reads p->race->flags, and the
        // constructor leaves the race null, as C's init_player does.
        player.setRace(SeededPlayerRegistry.plainRace(player.getPlayerBody()));
        player.setState(new PlayerState());
        return player;
    }

    private static PlayerRace race(PlayerBody body, int diggingSkill, Map<ElementEnum, ElementInfo> resists) {
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
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body, stats, skills,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), null, resists);
    }

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

    /**
     * A player with a plain race and shape, ready for a stat/mod entry.
     */
    private static Player modifierPlayer(int diggingSkill) {
        Player player = newPlayer();
        player.setRace(race(player.getPlayerBody(), diggingSkill, new HashMap<>()));
        player.setShape(plainShape());
        return player;
    }

    private static UIEntryValue forObject(ItemObject item, Player player) {
        return UIEntryValueRegistry.computeForObject(ENTRY, item, player, new ObjectValueCache());
    }

    private static UIEntryValue forPlayer(Player player) {
        return UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData());
    }

    private static void bindObject(CombinerName combiner, Flag<ChannelEntryFlag> flags,
                                   ObjectProperty... properties) {
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(properties), List.of(), combiner, flags);
    }

    private static void bindPlayer(Flag<ChannelEntryFlag> flags, PlayerProperty... properties) {
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(), List.of(properties), CombinerName.ADD, flags);
    }

    @BeforeEach
    void resetRegistry() {
        UIEntryValueRegistry.clearEntryBindings();
    }

    // --- computeForObject: every property family ---------------------------------------------------

    @Nested
    @DisplayName("computeForObject, one property family at a time")
    class ObjectFamilies {

        @Test
        @DisplayName("a modifier property reads the item's modifier, negative values included")
        void modifierReadsTheItemsValue() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));

            assertEquals(3, forObject(itemWithModifier(ObjectModifier.OM_STEALTH, 3), null).val());
            assertEquals(-2, forObject(itemWithModifier(ObjectModifier.OM_STEALTH, -2), null).val());
        }

        @Test
        @DisplayName("a stat property is read from the modifier table the same way")
        void statReadsTheModifierTable() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STR, null, false));

            assertEquals(2, forObject(itemWithModifier(ObjectModifier.OM_STR, 2), null).val());
        }

        @Test
        @DisplayName("a modifier the item does not have contributes nothing, so the entry has no value")
        void absentModifierContributesNothing() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));

            UIEntryValue result = forObject(plainItem(), null);
            assertEquals(0, result.val());
            assertEquals(0, result.auxVal());
        }

        @Test
        @DisplayName("a fixed value replaces a non-zero modifier but cannot make a zero one contribute")
        void fixedValueOnAModifier() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, 7, false));

            assertEquals(7, forObject(itemWithModifier(ObjectModifier.OM_STEALTH, -2), null).val());
            assertEquals(0, forObject(plainItem(), null).val());
        }

        @Test
        @DisplayName("an unlearned modifier is unknown, a learned one is read, a zero one is always read")
        void modifierKnowledge() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));
            Player player = newPlayer();

            assertEquals(Combiner.UI_ENTRY_UNKNOWN_VALUE,
                    forObject(itemWithModifier(ObjectModifier.OM_STEALTH, 2), player).val());
            assertEquals(0, forObject(plainItem(), player).val(),
                    "no modifier on the item means there is nothing to hide");

            player.getItemKnowledge().learnModifier(ObjectModifier.OM_STEALTH);
            assertEquals(2, forObject(itemWithModifier(ObjectModifier.OM_STEALTH, 2), player).val());
        }

        @Test
        @DisplayName("an ignore property reads EL_INFO_IGNORE, not the resistance level")
        void ignoreReadsTheIgnoreFlag() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    elementProperty(ObjPropertyType.OBJ_PROPERTY_IGNORE, ElementEnum.ELEM_FIRE, null, false));

            assertEquals(1, forObject(itemWithElement(ElementEnum.ELEM_FIRE, 0, true), null).val());
            assertEquals(0, forObject(itemWithElement(ElementEnum.ELEM_FIRE, 1, false), null).val(),
                    "a resistance without the ignore flag is not an ignore");
            assertEquals(0, forObject(plainItem(), null).val());
        }

        @Test
        @DisplayName("an ignore property with a fixed value reports that value when the flag is set")
        void ignoreWithAFixedValue() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    elementProperty(ObjPropertyType.OBJ_PROPERTY_IGNORE, ElementEnum.ELEM_FIRE, 4, false));

            assertEquals(4, forObject(itemWithElement(ElementEnum.ELEM_FIRE, 0, true), null).val());
        }

        @Test
        @DisplayName("resist, immunity and vulnerability read the resistance level: 1, 3 and -1")
        void resistFamilyReadsTheResistanceLevel() {
            for (ObjPropertyType type : List.of(ObjPropertyType.OBJ_PROPERTY_RESIST,
                    ObjPropertyType.OBJ_PROPERTY_IMM, ObjPropertyType.OBJ_PROPERTY_VULN)) {
                bindObject(CombinerName.ADD, noEntryFlags(),
                        elementProperty(type, ElementEnum.ELEM_COLD, null, false));

                assertEquals(1, forObject(itemWithElement(ElementEnum.ELEM_COLD, 1, false), null).val(), type + " resist");
                assertEquals(3, forObject(itemWithElement(ElementEnum.ELEM_COLD, 3, false), null).val(), type + " immune");
                assertEquals(-1, forObject(itemWithElement(ElementEnum.ELEM_COLD, -1, false), null).val(), type + " vulnerable");
                assertEquals(0, forObject(plainItem(), null).val(), type + " absent");
            }
        }

        @Test
        @DisplayName("a fixed value replaces a vulnerability's -1 as well as a resistance's 1")
        void fixedValueOnAResistance() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    elementProperty(ObjPropertyType.OBJ_PROPERTY_VULN, ElementEnum.ELEM_COLD, 9, false));

            assertEquals(9, forObject(itemWithElement(ElementEnum.ELEM_COLD, -1, false), null).val());
        }

        @Test
        @DisplayName("an element the player has not learned is unknown; learning it shows the real level")
        void elementKnowledge() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    elementProperty(ObjPropertyType.OBJ_PROPERTY_RESIST, ElementEnum.ELEM_ACID, null, false));
            Player player = newPlayer();

            assertEquals(Combiner.UI_ENTRY_UNKNOWN_VALUE,
                    forObject(itemWithUnknownResist(ElementEnum.ELEM_ACID, 1), player).val());

            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_ACID);
            assertEquals(1, forObject(itemWithUnknownResist(ElementEnum.ELEM_ACID, 1), player).val());
        }

        @Test
        @DisplayName("a curse's modifier adds to the item's own")
        void curseModifierAdds() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));
            ItemObject curseObject = new ItemObject();
            curseObject.putModifier(ObjectModifier.OM_STEALTH, -3);
            ItemObject item = itemWithModifier(ObjectModifier.OM_STEALTH, 5);
            carry(item, curse(1, curseObject), 1);

            assertEquals(2, forObject(item, null).val(), "5 from the item plus -3 from the curse");
        }

        @Test
        @DisplayName("a curse's modifier is ignored at power zero")
        void unpoweredCurseModifierIsIgnored() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));
            ItemObject curseObject = new ItemObject();
            curseObject.putModifier(ObjectModifier.OM_STEALTH, -3);
            ItemObject item = itemWithModifier(ObjectModifier.OM_STEALTH, 5);
            carry(item, curse(1, curseObject), 0);

            assertEquals(5, forObject(item, null).val());
        }

        @Test
        @DisplayName("a curse's resistance level is read off the curse's own object")
        void curseResistanceAdds() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    elementProperty(ObjPropertyType.OBJ_PROPERTY_RESIST, ElementEnum.ELEM_FIRE, null, false));
            ItemObject curseObject = new ItemObject();
            ElementInfo info = new ElementInfo();
            info.setResLevel(-1);
            curseObject.putElInfo(ElementEnum.ELEM_FIRE, info);
            ItemObject item = itemWithElement(ElementEnum.ELEM_FIRE, 1, false);
            carry(item, curse(1, curseObject), 1);

            assertEquals(0, forObject(item, null).val(), "1 from the item plus -1 from the curse");
        }

        @Test
        @DisplayName("curses are visited in ascending curse index, whatever order they were added in")
        void cursesAreVisitedInIndexOrder() {
            bindObject(CombinerName.LAST, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));
            ItemObject lowObject = new ItemObject();
            lowObject.putModifier(ObjectModifier.OM_STEALTH, 3);
            ItemObject highObject = new ItemObject();
            highObject.putModifier(ObjectModifier.OM_STEALTH, 4);
            ItemObject item = itemWithModifier(ObjectModifier.OM_STEALTH, 1);
            carry(item, curse(2, highObject), 1);
            carry(item, curse(1, lowObject), 1);

            assertEquals(4, forObject(item, null).val(),
                    "item, then index 1 (3), then index 2 (4); LAST keeps the final contribution");
        }
    }

    // --- computeForObject: auxiliary bookkeeping ---------------------------------------------------

    @Nested
    @DisplayName("computeForObject, auxiliary properties")
    class ObjectAuxiliary {

        @Test
        @DisplayName("a stat's modifier and its sustain flag fill the two totals separately")
        void modifierAndSustainSplit() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR, null, false),
                    flagProperty(ObjectFlag.OF_SUST_STR, null, true));
            ItemObject item = itemWithModifier(ObjectModifier.OM_STR, 2);
            item.setFlag(ObjectFlag.OF_SUST_STR);

            UIEntryValue result = forObject(item, null);
            assertEquals(2, result.val());
            assertEquals(1, result.auxVal());
        }

        @Test
        @DisplayName("with only auxiliary properties the main total is forced to 0 even if nothing is present")
        void onlyAuxiliaryForcesMainToZero() {
            bindObject(CombinerName.ADD, noEntryFlags(), flagProperty(ObjectFlag.OF_SUST_STR, null, true));

            UIEntryValue result = forObject(plainItem(), null);
            assertEquals(0, result.val());
            assertEquals(0, result.auxVal());
        }

        @Test
        @DisplayName("an unknown auxiliary property makes the auxiliary total unknown and leaves the main at 0")
        void unknownAuxiliary() {
            bindObject(CombinerName.ADD, noEntryFlags(), flagProperty(ObjectFlag.OF_SUST_STR, null, true));
            ItemObject known = plainItem();
            ItemObject item = rawItem(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), known);
            item.setToHit(3);
            item.setFlag(ObjectFlag.OF_SUST_STR);

            UIEntryValue result = forObject(item, newPlayer());
            assertEquals(0, result.val());
            assertEquals(Combiner.UI_ENTRY_UNKNOWN_VALUE, result.auxVal());
        }

        @Test
        @DisplayName("ENTRY_FLAG_TIMED_AUX skips auxiliary properties entirely, so the auxiliary total stays 0")
        void timedAuxSkipsAuxiliaryProperties() {
            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR, null, false),
                    flagProperty(ObjectFlag.OF_SUST_STR, null, true));
            ItemObject item = itemWithModifier(ObjectModifier.OM_STR, 2);
            item.setFlag(ObjectFlag.OF_SUST_STR);

            UIEntryValue result = forObject(item, null);
            assertEquals(2, result.val());
            assertEquals(0, result.auxVal());
        }

        @Test
        @DisplayName("ENTRY_FLAG_TIMED_AUX with only auxiliary properties leaves both totals at 0")
        void timedAuxWithOnlyAuxiliaryProperties() {
            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(), flagProperty(ObjectFlag.OF_SUST_STR, null, true));
            ItemObject item = plainItem();
            item.setFlag(ObjectFlag.OF_SUST_STR);

            UIEntryValue result = forObject(item, null);
            assertEquals(0, result.val());
            assertEquals(0, result.auxVal());
        }
    }

    // --- computeForPlayer: abilities ---------------------------------------------------------------

    @Nested
    @DisplayName("computeForPlayer, abilities")
    class PlayerAbilities {

        @Test
        @DisplayName("a player ability the player lacks contributes nothing")
        void absentAbilityIsSkipped() {
            bindPlayer(noEntryFlags(), playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 4, false, false));

            UIEntryValue result = forPlayer(newPlayer());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("a bound value is used as it stands, with the other channel not present")
        void boundValueIsUsedAsItStands() {
            bindPlayer(noEntryFlags(), playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 4, false, false));
            Player player = newPlayer();
            PlayerState state = new PlayerState();
            state.playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            player.setState(state);

            UIEntryValue result = forPlayer(player);
            assertEquals(4, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("an auxiliary bound value lands in the auxiliary channel")
        void auxiliaryBoundValueSwaps() {
            bindPlayer(noEntryFlags(), playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 4, false, true));
            Player player = newPlayer();
            PlayerState state = new PlayerState();
            state.playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            player.setState(state);

            UIEntryValue result = forPlayer(player);
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(4, result.auxVal());
        }

        @Test
        @DisplayName("a special ability with no case of its own contributes nothing")
        void unhandledSpecialAbilityContributesNothing() {
            bindPlayer(noEntryFlags(), playerFlagProperty(PlayerFlag.PF_SEE_ORE, 0, true, false));
            Player player = newPlayer();
            PlayerState state = new PlayerState();
            state.playerFlagOn(PlayerFlag.PF_SEE_ORE);
            player.setState(state);

            UIEntryValue result = forPlayer(player);
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("ENTRY_FLAG_TIMED_AUX skips an auxiliary ability before it looks at the player")
        void timedAuxSkipsAnAuxiliaryAbility() {
            bindPlayer(timedAsAuxEntryFlags(), playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 4, false, true));
            Player player = newPlayer();
            PlayerState state = new PlayerState();
            state.playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            player.setState(state);

            UIEntryValue result = forPlayer(player);
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("ENTRY_FLAG_TIMED_AUX skips an auxiliary object-flag ability")
        void timedAuxSkipsAnAuxiliaryObjectAbility() {
            bindPlayer(timedAsAuxEntryFlags(), objectFlagPlayerProperty(ObjectFlag.OF_FREE_ACT, true));

            UIEntryValue result = forPlayer(newPlayer());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("an object-flag ability the shape grants counts only once the player has learned the flag")
        void shapeFlagNeedsKnowledge() {
            bindPlayer(noEntryFlags(), objectFlagPlayerProperty(ObjectFlag.OF_FREE_ACT, false));
            Player player = newPlayer();
            PlayerShape shape = plainShape();
            shape.getFlags().on(ObjectFlag.OF_FREE_ACT);
            player.setShape(shape);

            assertEquals(0, forPlayer(player).val(), "the shape grants it but the player cannot read it");

            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            assertEquals(1, forPlayer(player).val());
        }

        @Test
        @DisplayName("an element ability adds the race's resistance and, once learned, the shape's")
        void elementAbilityRaceAndShape() {
            bindPlayer(noEntryFlags(), elementPlayerProperty(ElementEnum.ELEM_FIRE, false));
            Player player = newPlayer();
            ElementInfo raceFire = new ElementInfo();
            raceFire.setResLevel(1);
            Map<ElementEnum, ElementInfo> resists = new HashMap<>();
            resists.put(ElementEnum.ELEM_FIRE, raceFire);
            player.setRace(race(player.getPlayerBody(), 0, resists));
            PlayerShape shape = plainShape();
            shape.getElementValueModifiers().get(ElementEnum.ELEM_FIRE).setResLevel(1);
            player.setShape(shape);

            assertEquals(1, forPlayer(player).val(), "the race's 1 alone while the player has not learned fire");

            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            assertEquals(2, forPlayer(player).val(), "race 1 plus shape 1");
        }

        @Test
        @DisplayName("an auxiliary element ability puts both contributions in the auxiliary channel")
        void auxiliaryElementAbilitySwaps() {
            bindPlayer(noEntryFlags(), elementPlayerProperty(ElementEnum.ELEM_FIRE, true));
            Player player = newPlayer();
            ElementInfo raceFire = new ElementInfo();
            raceFire.setResLevel(1);
            Map<ElementEnum, ElementInfo> resists = new HashMap<>();
            resists.put(ElementEnum.ELEM_FIRE, raceFire);
            player.setRace(race(player.getPlayerBody(), 0, resists));
            PlayerShape shape = plainShape();
            shape.getElementValueModifiers().get(ElementEnum.ELEM_FIRE).setResLevel(1);
            player.setShape(shape);
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);

            UIEntryValue result = forPlayer(player);
            assertEquals(0, result.val());
            assertEquals(2, result.auxVal());
        }
    }

    // --- computeForPlayer: modifiers and timed effects --------------------------------------------

    @Nested
    @DisplayName("computeForPlayer, modifiers and timed effects")
    class PlayerModifiers {

        private void bindSpeedAsTimedAux() {
            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_SPEED, null, false));
        }

        private int timedSpeed(TimedEffect... effects) {
            Player player = modifierPlayer(0);
            for (TimedEffect effect : effects) player.putTimed(effect, 5);
            return forPlayer(player).auxVal();
        }

        @Test
        @DisplayName("get_timed_modifier_effect for speed: +10 for fast or sprint, +10 for terror, -5 stoneskin, -10 slow")
        void timedSpeedTable() {
            bindSpeedAsTimedAux();

            assertEquals(0, timedSpeed());
            assertEquals(10, timedSpeed(TimedEffect.TMD_FAST));
            assertEquals(10, timedSpeed(TimedEffect.TMD_SPRINT));
            assertEquals(10, timedSpeed(TimedEffect.TMD_FAST, TimedEffect.TMD_SPRINT), "the two do not stack");
            assertEquals(5, timedSpeed(TimedEffect.TMD_FAST, TimedEffect.TMD_STONESKIN));
            assertEquals(-10, timedSpeed(TimedEffect.TMD_SLOW));
            assertEquals(0, timedSpeed(TimedEffect.TMD_FAST, TimedEffect.TMD_SLOW));
            assertEquals(10, timedSpeed(TimedEffect.TMD_TERROR));
            assertEquals(5, timedSpeed(TimedEffect.TMD_FAST, TimedEffect.TMD_STONESKIN, TimedEffect.TMD_SLOW,
                    TimedEffect.TMD_TERROR), "10 - 5 - 10 + 10");
        }

        @Test
        @DisplayName("get_timed_modifier_effect for stealth, infravision and blows")
        void timedStealthInfraAndBlows() {
            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));
            Player stealthy = modifierPlayer(0);
            stealthy.putTimed(TimedEffect.TMD_STEALTH, 1);
            assertEquals(10, forPlayer(stealthy).auxVal());

            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_INFRA, null, false));
            Player seeing = modifierPlayer(0);
            seeing.putTimed(TimedEffect.TMD_SINFRA, 1);
            assertEquals(5, forPlayer(seeing).auxVal());

            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_BLOWS, null, false));
            for (int bloodlust : new int[]{19, 20, 45}) {
                Player raging = modifierPlayer(0);
                raging.putTimed(TimedEffect.TMD_BLOODLUST, bloodlust);
                assertEquals(bloodlust / 20, forPlayer(raging).auxVal(), "bloodlust " + bloodlust);
            }
        }

        @Test
        @DisplayName("a modifier with no timed effect gets 0 in the auxiliary channel")
        void timedModifierDefaultsToZero() {
            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR, null, false));
            Player player = modifierPlayer(0);
            player.putTimed(TimedEffect.TMD_FAST, 5);

            assertEquals(0, forPlayer(player).auxVal());
        }

        @Test
        @DisplayName("the timed contribution is only asked for when the entry has ENTRY_FLAG_TIMED_AUX")
        void timedEffectIgnoredWithoutTheFlag() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_SPEED, null, false));
            Player player = modifierPlayer(0);
            player.putTimed(TimedEffect.TMD_FAST, 5);

            assertEquals(0, forPlayer(player).auxVal());
        }

        @Test
        @DisplayName("the shape's own modifier and the timed one fill the two channels")
        void shapeModifierAndTimedSplit() {
            bindSpeedAsTimedAux();
            Player player = modifierPlayer(0);
            player.getShape().getObjectValueModifiers().put(ObjectModifier.OM_SPEED, 3);
            player.putTimed(TimedEffect.TMD_FAST, 5);

            UIEntryValue result = forPlayer(player);
            assertEquals(3, result.val());
            assertEquals(10, result.auxVal());
        }

        @Test
        @DisplayName("ENTRY_FLAG_TIMED_AUX skips an auxiliary modifier property")
        void timedAuxSkipsAnAuxiliaryModifier() {
            bindObject(CombinerName.ADD, timedAsAuxEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_SPEED, null, true));

            UIEntryValue result = forPlayer(modifierPlayer(0));
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("an auxiliary modifier swaps the shape's value into the auxiliary channel")
        void auxiliaryModifierSwaps() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_SPEED, null, true));
            Player player = modifierPlayer(0);
            player.getShape().getObjectValueModifiers().put(ObjectModifier.OM_SPEED, 3);

            UIEntryValue result = forPlayer(player);
            assertEquals(0, result.val());
            assertEquals(3, result.auxVal());
        }

        @Test
        @DisplayName("object properties other than stat and mod add nothing to the player's value")
        void nonModifierObjectPropertiesAreIgnored() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    flagProperty(ObjectFlag.OF_FREE_ACT, null, false),
                    elementProperty(ObjPropertyType.OBJ_PROPERTY_RESIST, ElementEnum.ELEM_FIRE, null, false));

            UIEntryValue result = forPlayer(modifierPlayer(0));
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.val());
            assertEquals(Combiner.UI_ENTRY_VALUE_NOT_PRESENT, result.auxVal());
        }

        @Test
        @DisplayName("modifier_to_skill: only OM_TUNNEL takes a racial skill, divided by 20 and truncated")
        void racialSkillConversion() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_TUNNEL, null, false));

            assertEquals(0, forPlayer(modifierPlayer(19)).val(), "19 / 20 truncates to 0");
            assertEquals(1, forPlayer(modifierPlayer(39)).val(), "39 / 20 truncates to 1");
            assertEquals(2, forPlayer(modifierPlayer(40)).val());
            assertEquals(-1, forPlayer(modifierPlayer(-25)).val(), "C truncates -25 / 20 toward zero");
        }

        @Test
        @DisplayName("modifier_to_skill: stealth has no racial contribution, whatever the digging skill")
        void stealthTakesNoRacialSkill() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH, null, false));
            Player player = modifierPlayer(100);
            player.getShape().getObjectValueModifiers().put(ObjectModifier.OM_STEALTH, 2);

            assertEquals(2, forPlayer(player).val());
        }

        @Test
        @DisplayName("an auxiliary OM_TUNNEL puts the shape's value and the racial skill in the auxiliary channel")
        void auxiliaryRacialSkillSwaps() {
            bindObject(CombinerName.ADD, noEntryFlags(),
                    modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_TUNNEL, null, true));
            Player player = modifierPlayer(100);
            player.getShape().getObjectValueModifiers().put(ObjectModifier.OM_TUNNEL, 3);

            UIEntryValue result = forPlayer(player);
            assertEquals(0, result.val());
            assertEquals(3 + 100 / 20, result.auxVal());
        }
    }

    // --- registration ------------------------------------------------------------------------------

    @Nested
    @DisplayName("addEntryBinding")
    class Registration {

        @Test
        @DisplayName("a null player list keeps the existing one while the combiner and flags are replaced")
        void nullPlayerListKeepsTheExistingOne() {
            PlayerProperty bravery = playerFlagProperty(PlayerFlag.PF_BRAVERY_30, 4, false, false);
            UIEntryValueRegistry.addEntryBinding(ENTRY, null, List.of(bravery), CombinerName.ADD, noEntryFlags());
            UIEntryValueRegistry.addEntryBinding(ENTRY,
                    List.of(modProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR, null, false)),
                    null, CombinerName.LAST, noEntryFlags());

            Player player = modifierPlayer(0);
            PlayerState state = new PlayerState();
            state.playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            player.setState(state);
            player.getShape().getObjectValueModifiers().put(ObjectModifier.OM_STR, 6);

            UIEntryValue result = forPlayer(player);
            assertEquals(6, result.val(), "LAST, so the modifier's 6 replaces the ability's 4; both lists survived");
        }
    }
}
