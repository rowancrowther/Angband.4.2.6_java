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

package uk.co.jackoftrades.middle.monsters;

import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.VisualsColourCyclesByRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.util.List;

/**
 * The full definition of a kind of monster (as loaded from {@code monster.txt})
 * — name and flavour text, its {@link MonsterBase}, combat/sense stats, spell and
 * blow lists, generation level/rarity, display glyph, drops, companions, mimic
 * kinds, shapes and the accumulated {@link MonsterLore}. This is the shared
 * template behind every live {@link Monster}, and the Java port of the C
 * original's {@code struct monster_race} ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterRace {
    /**
     * The monster's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * Flavour/description text.
     *
     * @author Rowan Crowther
     */
    private String text;
    /**
     * The plural form of the name.
     *
     * @author Rowan Crowther
     */
    private String plural;

    /**
     * The base type this race belongs to.
     *
     * @author Rowan Crowther
     */
    private MonsterBase base;

    /**
     * Average hit points.
     *
     * @author Rowan Crowther
     */
    private int averageHP;

    /**
     * Armour class.
     *
     * @author Rowan Crowther
     */
    private int ac;

    /**
     * Sleepiness/alertness (higher = sleeps more deeply).
     *
     * @author Rowan Crowther
     */
    private int sleep;
    /**
     * Hearing acuity (range it detects noise).
     *
     * @author Rowan Crowther
     */
    private int hearing;
    /**
     * Sense of smell (range it tracks scent).
     *
     * @author Rowan Crowther
     */
    private int smell;
    /**
     * Base movement speed.
     *
     * @author Rowan Crowther
     */
    private int speed;
    /**
     * Light radius the monster emits.
     *
     * @author Rowan Crowther
     */
    private int light;

    /**
     * Experience awarded for killing the monster.
     *
     * @author Rowan Crowther
     */
    private int mexp;

    /**
     * Frequency of innate (non-spell) attacks.
     *
     * @author Rowan Crowther
     */
    private int freqInnate;
    /**
     * Frequency of spell casting.
     *
     * @author Rowan Crowther
     */
    private int freqSpell;
    /**
     * The monster's spell power.
     *
     * @author Rowan Crowther
     */
    private int spellPower;

    /**
     * Race flags this monster has set.
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * Race flags explicitly cleared for this monster (overriding its base).
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterRaceFlag> flagsOff;
    /**
     * The spells this monster can cast.
     *
     * @author Rowan Crowther
     */
    private List<MonsterSpell> spells;

    /**
     * The monster's melee blows.
     *
     * @author Rowan Crowther
     */
    private List<MonsterBlow> blow;

    /**
     * Native dungeon level.
     *
     * @author Rowan Crowther
     */
    private int level;
    /**
     * Rarity weighting for generation.
     *
     * @author Rowan Crowther
     */
    private int rarity;

    /**
     * The display glyph and colour.
     *
     * @author Rowan Crowther
     */
    private AngbandDisplayCharacter adc;

    /**
     * Maximum number that may exist at once (1 for uniques).
     *
     * @author Rowan Crowther
     */
    private int maxNum;
    /**
     * Current number alive.
     *
     * @author Rowan Crowther
     */
    private int curNum;

    /**
     * Alternate spell-cast messages for this monster.
     *
     * @author Rowan Crowther
     */
    private List<MonsterAltmsg> spellMessages;
    /**
     * Possible item drops on death.
     *
     * @author Rowan Crowther
     */
    private List<MonsterDrop> drops;

    /**
     * Companion races that may be generated with this monster.
     *
     * @author Rowan Crowther
     */
    private List<MonsterFriends> friends;
    /**
     * Companion base types that may be generated with this monster.
     *
     * @author Rowan Crowther
     */
    private List<MonsterFriendsBase> friendsBase;

    /**
     * Object kinds this monster can mimic.
     *
     * @author Rowan Crowther
     */
    private List<ObjectKind> mimicKinds;

    /**
     * Names of the shapes this monster can change into.
     *
     * @author Rowan Crowther
     */
    private List<String> shapes;
    /**
     * The number of available shapes.
     *
     * @author Rowan Crowther
     */
    private int numShapes;

    /**
     * The player's accumulated lore about this monster.
     *
     * @author Rowan Crowther
     */
    private MonsterLore lore;

    /**
     * Build a fully-specified monster race from its parsed data-file fields, and
     * register its colour-cycling animation by group/cycle name.
     *
     * @param name          monster name
     * @param text          flavour text
     * @param plural        plural name form
     * @param base          base type
     * @param averageHP     average hit points
     * @param ac            armour class
     * @param sleep         sleepiness
     * @param hearing       hearing acuity
     * @param smell         sense of smell
     * @param speed         base speed
     * @param light         emitted light radius
     * @param mexp          experience for killing
     * @param freqInnate    innate-attack frequency
     * @param freqSpell     spell frequency
     * @param spellPower    spell power
     * @param flags         set race flags
     * @param flagsOff      cleared race flags
     * @param spells        castable spells
     * @param blow          melee blows
     * @param level         native level
     * @param rarity        rarity weighting
     * @param adc           display glyph/colour
     * @param maxNum        maximum simultaneous count
     * @param curNum        current count alive
     * @param spellMessages alternate spell messages
     * @param drops         death drops
     * @param friends       companion races
     * @param friendsBase   companion base types
     * @param mimicKinds    mimic object kinds
     * @param shapes        shape names
     * @param numShapes     number of shapes
     * @param groupName     colour-cycle group name
     * @param cycleName     colour-cycle name
     * @param lore          accumulated lore
     * @author Rowan Crowther
     */
    public MonsterRace(String name, String text, String plural, MonsterBase base, int averageHP, int ac, int sleep,
                       int hearing, int smell, int speed, int light, int mexp, int freqInnate, int freqSpell,
                       int spellPower, Flag<MonsterRaceFlag> flags, Flag<MonsterRaceFlag> flagsOff,
                       List<MonsterSpell> spells, List<MonsterBlow> blow, int level, int rarity,
                       AngbandDisplayCharacter adc, int maxNum, int curNum, List<MonsterAltmsg> spellMessages,
                       List<MonsterDrop> drops, List<MonsterFriends> friends, List<MonsterFriendsBase> friendsBase,
                       List<ObjectKind> mimicKinds, List<String> shapes, int numShapes,
                       String groupName, String cycleName, MonsterLore lore) {
        this.name = name;
        this.text = text;
        this.plural = plural;
        this.base = base;
        this.averageHP = averageHP;
        this.ac = ac;
        this.sleep = sleep;
        this.hearing = hearing;
        this.smell = smell;
        this.speed = speed;
        this.light = light;
        this.mexp = mexp;
        this.freqInnate = freqInnate;
        this.freqSpell = freqSpell;
        this.spellPower = spellPower;
        this.flags = flags;
        this.flagsOff = flagsOff;
        this.spells = spells;
        this.blow = blow;
        this.level = level;
        this.rarity = rarity;
        this.adc = adc;
        this.maxNum = maxNum;
        this.curNum = curNum;
        this.spellMessages = spellMessages;
        this.drops = drops;
        this.friends = friends;
        this.friendsBase = friendsBase;
        this.mimicKinds = mimicKinds;
        this.shapes = shapes;
        this.numShapes = numShapes;
        this.lore = lore;
        VisualsColourCyclesByRace.setCycleForRace(this, groupName, cycleName);
    }

    /**
     * Set this race's accumulated lore.
     *
     * @param lore the lore to attach
     * @author Rowan Crowther
     */
    public void setLore(MonsterLore lore) {
        this.lore = lore;
    }

    /**
     * Test-only constructor producing a bare race with an empty flag set.
     *
     * @author Rowan Crowther
     */
    @TestOnly
    public MonsterRace() {
        flags = new Flag<>(MonsterRaceFlag.class);
    }

    /**
     * @return this race's set race flags
     * @author Rowan Crowther
     */
    public Flag<MonsterRaceFlag> getFlags() {
        return flags;
    }

    /**
     * @return this race's name
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * Resolve this race's companion ("friends") entries by pointing each back at
     * this race. Called after all races are loaded.
     *
     * @author Rowan Crowther
     */
    public void setFriends() {
        for (MonsterFriends friend : friends) {
            friend.setRace(this);
        }
    }
}