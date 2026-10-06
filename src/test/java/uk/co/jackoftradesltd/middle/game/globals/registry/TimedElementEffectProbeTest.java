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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.uichannel.UIEntryValue;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.middle.game.globals.cached.CachedPlayerData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Throwaway probe for get_timed_element_effect via computeForPlayer. Expected values come from C:
 * player_timed.txt gives OPP_ACID/ELEC/FIRE/COLD/POIS their own resist: element, every other
 * effect has temp_resist -1.
 */
@ExtendWith(SeededPlayerRegistry.class)
class TimedElementEffectProbeTest {
    private static final String ENTRY = "PROBE";
    private static final Map<TimedEffect, ElementEnum> RESIST = Map.of(
            TimedEffect.TMD_OPP_ACID, ElementEnum.ELEM_ACID,
            TimedEffect.TMD_OPP_ELEC, ElementEnum.ELEM_ELEC,
            TimedEffect.TMD_OPP_FIRE, ElementEnum.ELEM_FIRE,
            TimedEffect.TMD_OPP_COLD, ElementEnum.ELEM_COLD,
            TimedEffect.TMD_OPP_POIS, ElementEnum.ELEM_POIS);

    private Object saved;

    private static Field field() throws ReflectiveOperationException {
        Field f = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        f.setAccessible(true);
        return f;
    }

    private static PlayerShape plainShape() {
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<ObjectModifier, Integer> mods = new HashMap<>();
        Map<ElementEnum, ElementInfo> els = new HashMap<>();
        for (ElementEnum e : ElementEnum.values()) {
            if (e == ElementEnum.ELEM_NONE || e == ElementEnum.ELEM_MAX) continue;
            els.put(e, new ElementInfo());
        }
        return new PlayerShape("S", 0, 0, 0, skills, new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), mods, els, List.of(), 0, List.of());
    }

    @BeforeEach
    void seed() throws Exception {
        saved = field().get(null);
        List<PlayerTimedEffect> list = new ArrayList<>();
        for (TimedEffect t : TimedEffect.values()) {
            if (t == TimedEffect.TMD_NONE) continue;
            list.add(new PlayerTimedEffect(t, "", "", "", "", null, List.of(), List.of(), null, null,
                    false, 0, ObjectFlag.OF_NONE, false, RESIST.get(t), null, null));
        }
        field().set(null, list);
        UIEntryValueRegistry.clearEntryBindings();
    }

    @AfterEach
    void restore() throws Exception {
        field().set(null, saved);
    }

    /**
     * aux channel for an element entry bound as non-aux, TIMED_AS_AUX, with the given effect active.
     */
    private int aux(ElementEnum asked, TimedEffect... active) {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);
        PlayerProperty prop = new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT, null, null,
                asked, null, List.of(new PlayerProperty.BindUI(ENTRY, 0, true, false)),
                "test", "", PlayerProperty.PlayerPropertyValue.NONE);
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(), List.of(prop), CombinerName.ADD, flags);

        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        player.setRace(SeededPlayerRegistry.plainRace(SeededPlayerRegistry.humanoidBody()));
        player.setShape(plainShape());
        // CachedPlayerData.populateFlags goes through Player.playerFlags, which needs a class and a state.
        player.setClass(new PlayerClass("Test Class", List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 0, 0, 0, List.of(), null));
        player.setState(new PlayerState());
        for (TimedEffect t : active) player.putTimed(t, 10);
        UIEntryValue r = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData());
        return r.auxVal();
    }

    @Test
    void oppAcidResistsAcidNotElec() {
        assertEquals(1, aux(ElementEnum.ELEM_ACID, TimedEffect.TMD_OPP_ACID));
        assertEquals(0, aux(ElementEnum.ELEM_ELEC, TimedEffect.TMD_OPP_ACID));
    }

    @Test
    void oppElecResistsElecNotAcid() {
        assertEquals(1, aux(ElementEnum.ELEM_ELEC, TimedEffect.TMD_OPP_ELEC));
        assertEquals(0, aux(ElementEnum.ELEM_ACID, TimedEffect.TMD_OPP_ELEC));
    }

    @Test
    void eachOppEffectMapsToItsOwnElement() {
        for (Map.Entry<TimedEffect, ElementEnum> e : RESIST.entrySet()) {
            for (ElementEnum asked : RESIST.values()) {
                assertEquals(asked == e.getValue() ? 1 : 0, aux(asked, e.getKey()),
                        e.getKey() + " asked about " + asked);
            }
        }
    }

    @Test
    void lastEffectInFileDoesNotThrow() {
        assertEquals(0, aux(ElementEnum.ELEM_ACID, TimedEffect.TMD_FREE_ACT));
    }

    @Test
    void nonResistingEffectsContributeNothing() {
        assertEquals(0, aux(ElementEnum.ELEM_ACID, TimedEffect.TMD_FAST, TimedEffect.TMD_SLOW,
                TimedEffect.TMD_STEALTH));
    }

    @Test
    void noEffectsActive() {
        assertEquals(0, aux(ElementEnum.ELEM_ACID));
    }

    @Test
    void definitionMissingDoesNotThrow() throws Exception {
        field().set(null, new ArrayList<PlayerTimedEffect>());
        assertEquals(0, aux(ElementEnum.ELEM_ACID, TimedEffect.TMD_OPP_ACID));
    }
}
