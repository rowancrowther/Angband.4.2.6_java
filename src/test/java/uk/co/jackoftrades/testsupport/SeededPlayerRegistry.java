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

package uk.co.jackoftrades.testsupport;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.EquipSlot;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.PlayerTimedEffect;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A JUnit extension that guarantees the registries hold the entries a
 * {@link uk.co.jackoftrades.middle.player.Player} needs — a body, a race, a curse list and a
 * timed-effect list — so that a test class can build one, and calculate with it, without depending
 * on another suite having run first.
 *
 * <p><b>The problem it solves.</b> {@code Player}'s constructor calls
 * {@code PlayerRegistry.lookupPlayerBody(0)} and {@code getFirstPlayerRace()}, both of which throw
 * when their list has never been loaded. The registry is global static state, so a test class that
 * constructs a player passes when the whole suite runs — some reader test loaded the real data files
 * first — and throws when run on its own. That is a false green: the suite's result depends on
 * execution order, and a developer running one class from the IDE sees a failure that has nothing to
 * do with their change.
 *
 * <p><b>It defers to real data.</b> Each list is seeded only if it is currently {@code null}, so
 * when the full suite runs and a reader has already loaded {@code body.txt} and {@code p_race.txt},
 * those are left in place and this does nothing. Anything it did seed is set back to {@code null}
 * afterwards, so a class using this extension cannot leave a stub behind for a later class to find.
 *
 * <p>Use it as {@code @ExtendWith(SeededPlayerRegistry.class)} on the test class. The fixtures are
 * also available directly through {@link #humanoidBody()} and {@link #plainRace(PlayerBody)} for
 * tests that want to build their own variations.
 *
 * <p>Class SeededPlayerRegistry coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
public class SeededPlayerRegistry implements BeforeAllCallback, AfterAllCallback {

    /**
     * Whether this extension seeded the body list and so is responsible for clearing it.
     */
    private boolean seededBodies;
    /**
     * Whether this extension seeded the race list and so is responsible for clearing it.
     */
    private boolean seededRaces;
    /**
     * Whether this extension seeded the curse list and so is responsible for clearing it. The list
     * is needed not by {@code Player} itself but by {@link uk.co.jackoftrades.middle.objects.KnownObject},
     * which sizes its curse map from the registry the way C sizes {@code obj_k}'s arrays from
     * {@code z_info->curse_max}.
     */
    private boolean seededCurses;
    /**
     * Whether this extension seeded the timed-effect list and so is responsible for clearing it.
     * {@code calcBonuses} looks every timed effect up through the registry — {@code flagsTimed} to
     * find the object flags a running status duplicates, {@code timedGradeEq} to find which band a
     * counter is in — and the lookup throws rather than answering when nothing has been loaded.
     * Seeding an empty list gives the neutral answer: no effect has a definition, so no status
     * contributes anything.
     */
    private boolean seededTimedEffects;

    /**
     * The twelve-slot humanoid layout from {@code body.txt} — the only body the shipped data
     * defines, and the one every race uses.
     *
     * <p>The full set of slots matters for anything that walks equipment: a body with one slot would
     * let a calculation pass that only ever examines the first, and the two ring slots in particular
     * are the case where a slot's <em>type</em> is not unique.
     *
     * @return a fresh humanoid body, with every slot empty
     */
    public static PlayerBody humanoidBody() {
        List<EquipSlot> slots = new ArrayList<>();
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_BOW, "shooting"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "right hand"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "left hand"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_AMULET, "neck"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_LIGHT, "light"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, "body"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_CLOAK, "back"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_SHIELD, "arm"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_HAT, "head"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_GLOVES, "hands"));
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_BOOTS, "feet"));
        return new PlayerBody("Humanoid", slots);
    }

    /**
     * A race that contributes nothing: no stat adjustments, no skills, no flags, no resistances and
     * no infravision.
     *
     * <p>Deliberately empty rather than realistic. A test asking what a piece of equipment
     * contributes wants the race's contribution to be zero, so that any non-zero total is the
     * equipment's doing; a test that wants a race's contribution sets the fields it cares about.
     *
     * @param body the body the race is built with
     * @return the race
     */
    public static PlayerRace plainRace(PlayerBody body) {
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
        Map<ElementEnum, ElementInfo> resists = new HashMap<>();
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                stats, skills, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, resists);
    }

    /**
     * Reaches one of the registry's private static lists.
     *
     * @param name the field's name
     * @return the accessible field
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Field registryField(String name) throws ReflectiveOperationException {
        Field field = PlayerRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * Reaches one of the object registry's private static lists.
     *
     * @param name the field's name
     * @return the accessible field
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Field objectRegistryField(String name) throws ReflectiveOperationException {
        Field field = ObjectRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (registryField("playerBodies").get(null) == null) {
            List<PlayerBody> bodies = new ArrayList<>();
            bodies.add(humanoidBody());
            registryField("playerBodies").set(null, bodies);
            seededBodies = true;
        }
        if (registryField("playerRaces").get(null) == null) {
            List<PlayerRace> races = new ArrayList<>();
            races.add(plainRace(humanoidBody()));
            registryField("playerRaces").set(null, races);
            seededRaces = true;
        }
        if (objectRegistryField("curses").get(null) == null) {
            ObjectRegistry.setCurses(new ArrayList<Curse>());
            seededCurses = true;
        }
        if (registryField("playerTimedEffects").get(null) == null) {
            registryField("playerTimedEffects").set(null, new ArrayList<PlayerTimedEffect>());
            seededTimedEffects = true;
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (seededBodies) {
            registryField("playerBodies").set(null, null);
            seededBodies = false;
        }
        if (seededRaces) {
            registryField("playerRaces").set(null, null);
            seededRaces = false;
        }
        if (seededCurses) {
            objectRegistryField("curses").set(null, null);
            seededCurses = false;
        }
        if (seededTimedEffects) {
            registryField("playerTimedEffects").set(null, null);
            seededTimedEffects = false;
        }
    }
}
