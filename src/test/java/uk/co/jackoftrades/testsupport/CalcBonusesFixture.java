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

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.CurseData;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.KnownObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.EquipSlot;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerClass;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.PlayerState;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A builder for the character {@code calcBonuses} needs, and the state it fills.
 *
 * <p><b>Why a fixture rather than a helper method.</b> {@code calcBonuses} derives everything at
 * once from race, class, level, stats, every worn item and its curses, the shape and the running
 * statuses. A test that wants to ask one question — what does a speed ring give? — still has to
 * supply all of it, and most of what it supplies must contribute <em>nothing</em>, or the answer is
 * buried in a total. So the default character here is deliberately null: an empty race, an empty
 * class, no equipment, no statuses, and stats chosen so their table adjustments are zero. Anything
 * a test then sets is the only thing in the answer.
 *
 * <p><b>Reflection, and why.</b> {@link Player} exposes no setters for race, class, level or the stat
 * maps, and {@link EquipSlot} none for the item it holds — the real routes in are character creation
 * and the wield command, neither of which exists yet. The fields are set directly rather than
 * building those subsystems first. Every such write is confined to this class, so when the setters
 * arrive there is one place to change.
 *
 * <p><b>The defaults are chosen, not arbitrary.</b> Depth is 1 rather than 0, which keeps
 * {@code calcLight} out of the town-daytime branch that would otherwise need a loaded world clock;
 * {@code update} is true, so the calculation is the real one rather than the hypothetical variant
 * whose stat indices are nudged by whatever the incoming state held. A test wanting the hypothetical
 * path calls {@link #calculateHypothetical()} and gets it explicitly.
 *
 * <p>Class CalcBonusesFixture coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
public final class CalcBonusesFixture {

    /**
     * The stat value whose table adjustments are all zero, so an unset stat contributes nothing.
     */
    public static final int NEUTRAL_STAT = 10;

    /**
     * The character under construction.
     */
    private final Player player;
    /**
     * The body whose slots equipment is placed in.
     */
    private final PlayerBody body;
    /**
     * The state each calculation fills.
     */
    private final PlayerState state = new PlayerState();

    /**
     * Builds a character that contributes nothing of its own.
     *
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    private CalcBonusesFixture() throws ReflectiveOperationException {
        player = new Player();
        body = SeededPlayerRegistry.humanoidBody();

        set("body", body);
        set("race", SeededPlayerRegistry.plainRace(body));
        set("playerClass", plainClass());
        set("itemKnowledge", new KnownObject());
        set("state", new PlayerState());
        set("level", 1);
        set("depth", 1);
        set("shape", null);

        Map<Stats, Integer> cur = new HashMap<>();
        Map<Stats, Integer> max = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            cur.put(stat, NEUTRAL_STAT);
            max.put(stat, NEUTRAL_STAT);
        }
        set("statCur", new HashMap<>(cur));
        set("statMax", new HashMap<>(max));
    }

    /**
     * @return a character with an empty race, an empty class, no gear and no statuses
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    public static CalcBonusesFixture plainCharacter() throws ReflectiveOperationException {
        return new CalcBonusesFixture();
    }

    /**
     * A class that adds nothing: no skills, no stat adjustments, no flags, and no magic.
     *
     * <p>Every skill and stat is present at zero rather than absent, because the accessors index
     * their maps directly and an absent key would throw rather than read as nothing.
     *
     * @return the class
     */
    public static PlayerClass plainClass() {
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
                5, 30, 5, List.of(), ClassMagic.NONE);
    }

    /**
     * A bare item: a real kind, a type, and nothing else. Every quantity the equipment walk reads is
     * present and zero, so a test sets only what it is asking about.
     *
     * @param tValue the item's type
     * @return the item
     */
    public static ItemObject item(TValue tValue) {
        return item(tValue, 0);
    }

    /**
     * A bare item with a recharge counter — the state a fuelled light or a charging wand is in.
     *
     * @param tValue  the item's type
     * @param timeout the recharge counter; for a light, the fuel remaining
     * @return the item
     */
    public static ItemObject item(TValue tValue, int timeout) {
        return new ItemObject(new ObjectKind(), null, null, null, Loc.zero, tValue, 0,
                "0", 0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<ObjectModifier, Integer>(),
                new HashMap<ElementEnum, ElementInfo>(), java.util.Set.of(), java.util.Set.of(),
                new LinkedHashMap<Curse, CurseData>(), List.of(), null, List.of(), "0", timeout, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, "");
    }

    /**
     * A bare item paired with a known counterpart that agrees with it entirely — an item the
     * character has fully identified.
     *
     * <p>Needed for anything asking about {@code knownOnly}: without a counterpart every
     * {@code known*} test reads zero and the item contributes nothing to the restricted calculation.
     *
     * @param tValue the item's type
     * @return the item, with its counterpart attached
     * @throws ReflectiveOperationException if the counterpart cannot be attached
     */
    public static ItemObject identifiedItem(TValue tValue) throws ReflectiveOperationException {
        ItemObject item = item(tValue);
        Field field = ItemObject.class.getDeclaredField("known");
        field.setAccessible(true);
        field.set(item, item(tValue));
        return item;
    }

    /**
     * Copies an item's combat values onto its known counterpart, so the character is treated as
     * having learned them.
     *
     * @param item the item, which must already have a counterpart
     * @throws ReflectiveOperationException if the counterpart cannot be reached
     */
    public static void learnCombatValues(ItemObject item) throws ReflectiveOperationException {
        ItemObject known = item.getKnown();
        known.setToAC(item.getToAC());
        known.setToHit(item.getToHit());
        known.setToDam(item.getToDam());
        known.setBaseAC(item.getBaseAC());
    }

    /**
     * A list holding one element, for the registry seeds.
     *
     * @param item the element
     * @param <T>  the element type
     * @return a mutable list of one
     */
    public static <T> List<T> listOf(T item) {
        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }

    /**
     * Sets the character's level, which scales the class's per-level skills.
     *
     * @param value the level
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    public CalcBonusesFixture level(int value) throws ReflectiveOperationException {
        set("level", value);
        return this;
    }

    /**
     * Sets the character's depth. Zero is the town, which changes what {@code calcLight} does.
     *
     * @param value the depth in levels
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    public CalcBonusesFixture depth(int value) throws ReflectiveOperationException {
        set("depth", value);
        return this;
    }

    /**
     * Sets one stat's current and maximum value together, which is what an undrained character has.
     *
     * @param stat  the stat to set
     * @param value the value, on the 3-to-18-then-tens scale
     * @return this fixture
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    @SuppressWarnings("unchecked")
    public CalcBonusesFixture stat(Stats stat, int value) throws ReflectiveOperationException {
        ((Map<Stats, Integer>) read("statCur")).put(stat, value);
        ((Map<Stats, Integer>) read("statMax")).put(stat, value);
        return this;
    }

    /**
     * Replaces the character's race.
     *
     * @param race the race
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    public CalcBonusesFixture race(PlayerRace race) throws ReflectiveOperationException {
        set("race", race);
        return this;
    }

    /**
     * Replaces the character's class.
     *
     * @param playerClass the class
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    public CalcBonusesFixture playerClass(PlayerClass playerClass) throws ReflectiveOperationException {
        set("playerClass", playerClass);
        return this;
    }

    /**
     * Puts the character into a shape.
     *
     * @param shape the shape, or {@code null} for none
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    public CalcBonusesFixture shape(PlayerShape shape) throws ReflectiveOperationException {
        set("shape", shape);
        return this;
    }

    /**
     * Sets a timed effect's counter.
     *
     * @param effect the effect
     * @param value  the counter value; zero is dormant
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    public CalcBonusesFixture timed(TimedEffect effect, int value) throws ReflectiveOperationException {
        ((Map<TimedEffect, Integer>) read("timed")).put(effect, value);
        return this;
    }

    /**
     * Teaches the character to read the given modifiers — C's {@code p->obj_k->modifiers[]}.
     *
     * <p>Necessary before any modifier on any item counts for anything: {@code calcBonuses}
     * multiplies every modifier it reads by this knowledge, so a test that equips a speed ring
     * without calling this will find the speed unchanged and the reason invisible.
     *
     * @param modifiers the modifiers the character can now read
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    public CalcBonusesFixture knows(ObjectModifier... modifiers) throws ReflectiveOperationException {
        KnownObject knowledge = (KnownObject) read("itemKnowledge");
        for (ObjectModifier modifier : modifiers) {
            knowledge.learnModifier(modifier);
        }
        return this;
    }

    /**
     * Places an item in the named body slot, as wielding it would.
     *
     * @param slotName the slot's name from {@code body.txt} — "weapon", "shooting", "body", …
     * @param item     the item to wear
     * @return this fixture
     * @throws ReflectiveOperationException if the field cannot be reached
     * @throws IllegalArgumentException     if the body has no slot of that name
     */
    public CalcBonusesFixture wear(String slotName, ItemObject item) throws ReflectiveOperationException {
        for (EquipSlot slot : body.getSlots()) {
            if (slot.getName().equals(slotName)) {
                Field field = EquipSlot.class.getDeclaredField("item");
                field.setAccessible(true);
                field.set(slot, item);
                return this;
            }
        }
        throw new IllegalArgumentException("no slot named " + slotName);
    }

    /**
     * Runs the real calculation — C's {@code calc_bonuses(p, state, false, true)}.
     *
     * @return the filled state
     */
    public PlayerState calculate() {
        player.calcBonuses(state, false, true);
        return state;
    }

    /**
     * Runs the calculation restricted to what the character has learned — C's
     * {@code known_only} pass, the one the character sheet displays.
     *
     * @return the filled state
     */
    public PlayerState calculateKnownOnly() {
        player.calcBonuses(state, true, true);
        return state;
    }

    /**
     * Runs the hypothetical calculation — C's {@code update == false} pass, which must not write
     * anything back to the character and whose stat indices are nudged by whatever the incoming
     * state held.
     *
     * @return the filled state
     */
    public PlayerState calculateHypothetical() {
        player.calcBonuses(state, false, false);
        return state;
    }

    /**
     * @return the character being built, for assertions about what the calculation wrote back to it
     */
    public Player player() {
        return player;
    }

    /**
     * @return the body, for tests that need a slot directly
     */
    public PlayerBody body() {
        return body;
    }

    /**
     * Sets one of the player's private fields.
     *
     * @param name  the field's name
     * @param value the value to set
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void set(String name, Object value) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * Reads one of the player's private fields.
     *
     * @param name the field's name
     * @return the field's value
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private Object read(String name) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(player);
    }
}
