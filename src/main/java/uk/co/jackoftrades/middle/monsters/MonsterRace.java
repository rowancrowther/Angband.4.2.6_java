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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.frontend.colour.ColourCycle;
import uk.co.jackoftrades.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.util.List;
import java.util.Map;

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
     * Log4j logger for this class, used to report data problems encountered while building or
     * resolving monster races.
     */
    public static final Logger logger = LogManager.getLogger();

    /**
     * Returns an independent copy of this race.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the monster subsystem in chapter 6; returns
     * {@code null} for every input.
     *
     * <p>Callers should be sure they want a copy at all before this is filled in. C never copies a
     * race: {@code monster_race} is a shared template that every monster and every dropped object
     * points at, and identity is what tells two references apart - {@code object_origin_combine}
     * compares {@code origin_race} pointers directly. Any implementation that returns a new instance
     * makes two objects from the same monster compare as coming from different ones, so sharing the
     * reference is usually what a caller wants instead.
     *
     * <p>Function copy stubbed before 260827, commented in full on 260827.
     *
     * @return {@code null} until implemented
     */
    public MonsterRace copy() {
        // STUB class: TODO Implement in chapter 6
        return null;
    }

    /**
     * The three message variants shown when a monster casts a spell, chosen by what the player can
     * perceive: {@code visible} when the caster is seen, {@code invisible} when only the effect is
     * noticed, and {@code miss} when the spell fails to connect. Groups the C original's parallel
     * message strings into a single value.
     *
     * @param visible   message shown when the casting monster is visible
     * @param invisible message shown when the caster is unseen but the spell is noticed
     * @param miss      message shown when the spell misses or fails
     * @author Rowan Crowther
     */
    public record MonsterSpellMessages(String visible, String invisible, String miss) {
    }

    /**
     * The monster's name.
     */
    private String name;
    /**
     * Flavour/description text.
     */
    private String text;
    /**
     * The plural form of the name.
     */
    private String plural;

    /**
     * The base type this race belongs to.
     */
    private MonsterBase base;

    /**
     * Average hit points.
     */
    private int averageHP;

    /**
     * Armour class.
     */
    private int ac;

    /**
     * Sleepiness/alertness (higher = sleeps more deeply).
     */
    private int sleep;
    /**
     * Hearing acuity (range it detects noise).
     */
    private int hearing;
    /**
     * Sense of smell (range it tracks scent).
     */
    private int smell;
    /**
     * Base movement speed.
     */
    private int speed;
    /**
     * Light radius the monster emits.
     */
    private int light;

    /**
     * Experience awarded for killing the monster.
     */
    private int mexp;

    /**
     * Frequency of innate (non-spell) attacks.
     */
    private int freqInnate;
    /**
     * Frequency of spell casting.
     */
    private int freqSpell;
    /**
     * The monster's spell power.
     */
    private int spellPower;

    /**
     * Race flags this monster has set.
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * Race flags explicitly cleared for this monster (overriding its base).
     */
    private Flag<MonsterRaceFlag> flagsOff;
    /**
     * The spells this monster can cast.
     */
    private Flag<MonsterSpell> spells;

    /**
     * The monster's melee blows.
     */
    private List<MonsterBlow> blow;

    /**
     * Native dungeon level.
     */
    private int level;
    /**
     * Rarity weighting for generation.
     */
    private int rarity;

    /**
     * The display glyph and colour.
     */
    private AngbandDisplayCharacter adc;

    /**
     * Maximum number that may exist at once (1 for uniques).
     */
    private int maxNum;
    /**
     * Current number alive.
     */
    private int curNum;

    /**
     * Alternate spell-cast messages for this monster.
     */
    private Map<String, MonsterSpellMessage> spellMessages;
    /**
     * Possible item drops on death.
     */
    private List<MonsterDrop> drops;

    /**
     * Companion races that may be generated with this monster.
     */
    private List<MonsterFriends> friends;
    /**
     * Companion base types that may be generated with this monster.
     */
    private List<MonsterFriendsBase> friendsBase;

    /**
     * Object kinds this monster can mimic.
     */
    private List<ObjectKind> mimicKinds;

    /**
     * Names of the shapes this monster can change into.
     */
    private List<MonsterShape> shapes;
    /**
     * The number of available shapes.
     */
    private int numShapes;

    /**
     * The player's accumulated lore about this monster.
     */
    private MonsterLore lore;

    /**
     * The colour-cycling animation driving this race's display glyph, resolved by group/cycle name at
     * construction; {@code null} for a race whose glyph does not animate.
     */
    private ColourCycle cycler;

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
     * @param spells        castable spells
     * @param blow          melee blows
     * @param level         native level
     * @param rarity        rarity weighting
     * @param adc           display glyph/colour
     * @param spellMessages alternate spell messages
     * @param drops         death drops
     * @param friends       companion races
     * @param friendsBase   companion base types
     * @param mimicKinds    mimic object kinds
     * @param shapes        shape names
     * @param numShapes     number of shapes
     * @param cycler        colour-cycling animation for the display glyph, or {@code null} if it does
     *                      not animate
     */
    public MonsterRace(String name, String text, String plural, MonsterBase base,
                       int averageHP, int ac, int sleep, int hearing, int smell,
                       int speed, int light, int mexp, int freqInnate, int freqSpell,
                       int spellPower, Flag<MonsterRaceFlag> flags,
                       Flag<MonsterSpell> spells,
                       List<MonsterBlow> blow, int level, int rarity,
                       AngbandDisplayCharacter adc,
                       Map<String, MonsterSpellMessage> spellMessages,
                       List<MonsterDrop> drops, List<MonsterFriends> friends,
                       List<MonsterFriendsBase> friendsBase,
                       List<ObjectKind> mimicKinds, List<MonsterShape> shapes, int numShapes,
                       ColourCycle cycler) {
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
        this.spells = spells;
        this.blow = blow;
        this.level = level;
        this.rarity = rarity;
        this.adc = adc;
        this.spellMessages = spellMessages;
        this.drops = drops;
        this.friends = friends;
        this.friendsBase = friendsBase;
        this.mimicKinds = mimicKinds;
        this.shapes = shapes;
        this.numShapes = numShapes;
        this.cycler = cycler;
    }

    /**
     * Set this race's accumulated lore.
     *
     * @param lore the lore to attach
     */
    public void setLore(MonsterLore lore) {
        this.lore = lore;
    }

    /**
     * Test-only constructor producing a bare race with an empty flag set.
     */
    @TestOnly
    public MonsterRace() {
        flags = new Flag<>(MonsterRaceFlag.class);
    }

    /**
     * @return this race's set race flags
     */
    private Flag<MonsterRaceFlag> getFlags() {
        return flags;
    }

    /**
     * Test whether this race has a given race flag set — the port of C's {@code rf_has}.
     *
     * @param flag the race flag to test
     * @return true if the flag is set on this race
     */
    public boolean hasMonsterRaceFlag(MonsterRaceFlag flag) {
        return flags.has(flag);
    }

    /**
     * @return this race's name
     */
    public String getName() {
        return name;
    }

    /**
     * Resolve each companion ("friends") entry to its race by name, now that every race has loaded. A
     * {@code "same"} self-reference has already been substituted for this race's own name in the
     * assembler, so every entry here is a concrete race name resolved by a global lookup. This must
     * run as a second pass because a friend may name a race defined later in {@code monster.txt} (a
     * forward reference).
     */
    public void resolveFriends() {
        for (MonsterFriends f : friends) {
            f.setRace(f.getName());
        }
    }

    /**
     * Resolve each shape entry, now that every base and race has loaded. Each shape name is tried
     * first as a monster base (a generic, whole-family shapechange) and, failing that, as a specific
     * race; a name that is neither is a fatal data error. Like {@link #resolveFriends()} this is a
     * second pass, since a shape may name a race defined later in {@code monster.txt}.
     */
    public void resolveShapes() {
        for (MonsterShape s : shapes) {
            MonsterBase base = MonsterRegistry.lookupMonsterBase(s.getName());
            if (base == null) {
                MonsterRace race = MonsterRegistry.lookupMonsterRace(s.getName());
                if (race == null) {
                    String message = "Unknown shape name: " + s.getName();
                    throw new InvalidTokenFoundDuringParse(message);
                } else {
                    s.setRace(race);
                    s.setBase(null);
                }
            } else {
                s.setBase(base);
                s.setRace(null);
            }
        }
    }

    /**
     * @return this race's native dungeon depth/level
     */
    public int getLevel() {
        return level;
    }
}