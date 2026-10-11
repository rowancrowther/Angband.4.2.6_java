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

package uk.co.jackoftradesltd.middle.monsters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.colour.ColourCycle;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;

import java.util.List;
import java.util.Map;

/**
 * The full definition of a kind of monster (as loaded from {@code monster.txt})
 * — name and flavour text, its {@link MonsterBase}, combat/sense stats, spell and
 * blow lists, generation level/rarity, display glyph, drops, companions, mimic
 * kinds, shapes and the accumulated {@link MonsterLore}. This is the shared
 * template behind every live {@link Monster}, and the Java port of the C
 * original's {@code struct monster_race} ({@code monster.h}).
 *
 * <p>Differences from the C struct worth knowing about:
 * <ul>
 *   <li>C's {@code ridx} and {@code next} fields are not carried. A race is found through
 *       {@link MonsterRegistry}, and the lore record lives on the race itself (see
 *       {@link #getLore()}) where C indexes the global {@code l_list} by {@code ridx}.</li>
 *   <li>C's {@code d_attr}/{@code d_char} pair is held as one {@link AngbandDisplayCharacter}, and
 *       C's {@code spell_flags} bitfield is the {@link Flag} of {@link MonsterSpell} called
 *       {@code spells}.</li>
 *   <li>C's singly linked lists become {@link List}s. C builds the blow list by appending, so blows
 *       run in file order, but it builds the friends, friends-base, drops, mimic and shape lists by
 *       prepending, so a walk from the head runs in <em>reverse</em> file order. The Java lists are
 *       all in file order, as the assembler adds them. Anything that ports a loop over one of those
 *       lists, and consumes random numbers as it goes, will see them in a different order from C.</li>
 *   <li>C's {@code finish_parse_monster} copies each race's blows into an array padded with blank
 *       records out to {@code mon_blows_max}. The Java blow list holds only the real blows.</li>
 * </ul>
 *
 * <p>The two <em>resolution</em> methods, {@link #resolveFriends()} and {@link #resolveShapes()},
 * stand in for the name-to-race conversion loop at the end of {@code finish_parse_monster}
 * ({@code mon-init.c}); {@code MonsterDataLoader} runs them once every race has been registered.
 *
 * <p>Class MonsterRace coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class MonsterRace {
    /**
     * Log4j logger for this class. Nothing in this class logs through it at present: a data problem
     * found while resolving friends or shapes is raised as an exception instead.
     *
     * <p>Field logger coded before 261010, commented in full on 261010.
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
     * Sets the number of this race currently alive on the level, C's {@code race->cur_num}. C
     * writes this field directly rather than through an accumulator — reset to zero on level
     * generation, decremented on death, incremented on generation ({@code mon-make.c}) — and this
     * setter carries the same plain assignment, with no clamping against {@link #maxNum}.
     *
     * <p>Function setCurNum coded on 260903, commented in full on 260903.
     *
     * @param num the new current population for this race
     */
    public void setCurNum(int num) {
        this.curNum = num;
    }

    /**
     * Sets the maximum number of this race permitted alive at once, C's {@code race->max_num}
     * (typically {@code 100} for an ordinary monster, {@code 1} for a unique, and {@code 0} once a
     * unique has been slain — see {@code load.c} and {@code mon-util.c}). C only ever writes this
     * field directly, never derives it, and this setter does the same with no range checking, even
     * though C declares the field as an unsigned byte.
     *
     * <p>Function setMaxNum coded on 260903, commented in full on 260903.
     *
     * @param num the new population cap for this race
     */
    public void setMaxNum(int num) {
        this.maxNum = num;
    }

    /**
     * Returns this race's melee blows, C's {@code race->blow}, in the order the {@code blow:} lines
     * appear in {@code monster.txt}. C appends each blow to the end of its list as it parses, so the
     * order agrees with C. The list holds only the real blows: C's {@code finish_parse_monster} then
     * copies them into an array padded with blank records out to {@code z_info->mon_blows_max}, and
     * that padding is not reproduced here, so the list's size is this race's blow count rather than
     * the global maximum.
     *
     * <p>The list is the live one, not a copy, so a caller that edits it edits the race.
     *
     * <p>Function getBlows coded before 261010, commented in full on 261010.
     *
     * @return this race's blows, in file order
     */
    public List<MonsterBlow> getBlows() {
        return blow;
    }

    /**
     * A visible / invisible / miss triple of spell-cast messages. <b>Nothing in the main code uses
     * it</b>: the assembler builds {@link MonsterSpellMessage}, a record with the same three
     * components under different names, and that is what the {@link #spellMessages} field holds.
     * This nested record is exercised only by {@code MonsterRaceResolutionTest}.
     *
     * <p>It is also not a port of anything in C. C's {@code monster_altmsg} holds <em>one</em>
     * message per record, with a {@code msg_type} saying which of the spell's messages it overrides
     * and an {@code index} saying which spell, so a spell with three overrides is three records.
     *
     * <p>Record MonsterSpellMessages coded before 261010, commented in full on 261010.
     *
     * @param visible   message shown when the casting monster is visible
     * @param invisible message shown when the caster is unseen but the spell is noticed
     * @param miss      message shown when the spell misses or fails
     * @author Rowan Crowther
     */
    public record MonsterSpellMessages(String visible, String invisible, String miss) {
    }

    /**
     * The monster's name, C's {@code race->name}, the text of the {@code name:} line. The registry's
     * {@code lookupMonsterRace} matches against it, and no two shipped races share a name.
     *
     * <p>Field name coded before 261010, commented in full on 261010.
     */
    private String name;
    /**
     * The monster's description, C's {@code race->text}: the free text shown by monster recall,
     * assembled from the {@code desc:} lines of {@code monster.txt}.
     *
     * <p>Field text coded before 261010, commented in full on 261010.
     */
    private String text;
    /**
     * The optional plural form of the name, C's {@code race->plural}. C leaves it {@code NULL} when
     * the {@code plural:} line is absent or empty, so a {@code null} here is meaningful, not a
     * missing value.
     *
     * <p>Field plural coded before 261010, commented in full on 261010.
     */
    private String plural;

    /**
     * The base type this race belongs to, C's {@code race->base}. The base supplies the default
     * glyph and the default race flags, which the {@code base:} line copies in before the race's own
     * {@code flags:} lines are applied.
     *
     * <p>Field base coded before 261010, commented in full on 261010.
     */
    private MonsterBase base;

    /**
     * Average hit points, C's {@code race->avg_hp}, from the {@code hit-points:} line.
     *
     * <p>Field averageHP coded before 261010, commented in full on 261010.
     */
    private int averageHP;

    /**
     * Armour class, C's {@code race->ac}.
     *
     * <p>Field ac coded before 261010, commented in full on 261010.
     */
    private int ac;

    /**
     * The base inactivity counter, C's {@code race->sleep}: how deeply the monster starts out asleep.
     * Higher means harder to wake.
     *
     * <p>Field sleep coded before 261010, commented in full on 261010.
     */
    private int sleep;
    /**
     * Hearing, C's {@code race->hearing}: how far away the monster notices noise. C's comment gives
     * the range as 1 to 100 with 20 standard, and its parser scales the data-file value by
     * {@code z_info->max_sight / 20}. The assembler does the same, multiplying by
     * {@code GameConstants.getPlayerMaxSight()} before dividing by 20 in integer arithmetic, so a
     * {@code max_sight} other than its default of 20 changes the stored value exactly as in C.
     *
     * <p>Field hearing coded before 261010, commented in full on 261010.
     */
    private int hearing;
    /**
     * Sense of smell, C's {@code race->smell}: how far away the monster can follow the player's
     * scent. C's comment gives the range as 0 to 50 with 20 standard.
     *
     * <p>Field smell coded before 261010, commented in full on 261010.
     */
    private int smell;
    /**
     * Base speed, C's {@code race->speed}. Normal speed is 110.
     *
     * <p>Field speed coded before 261010, commented in full on 261010.
     */
    private int speed;
    /**
     * Light intensity, C's {@code race->light}. Signed: positive lights the grids around the
     * monster, negative darkens them. See {@link #getLight()}.
     *
     * <p>Field light coded before 261010, commented in full on 261010.
     */
    private int light;

    /**
     * Experience value for a kill, C's {@code race->mexp}.
     *
     * <p>Field mexp coded before 261010, commented in full on 261010.
     */
    private int mexp;

    /**
     * Innate-attack frequency, C's {@code race->freq_innate}. C stores {@code 100 / pct} from the
     * {@code innate-freq:} line, so it holds the "one in N" figure rather than the percentage, and
     * leaves it zero when the line is absent. <b>Outstanding:</b> the assembler stores the raw line
     * value and defaults it to {@code 4} when absent, and I found no conversion to {@code 100 / pct}
     * in this class or the assembler, so this field does not yet hold what C's does.
     *
     * <p>Field freqInnate coded before 261010, commented in full on 261010.
     */
    private int freqInnate;
    /**
     * Spell-casting frequency, C's {@code race->freq_spell}. Same convention, and same outstanding
     * difference, as {@link #freqInnate}: C stores {@code 100 / pct} from the {@code spell-freq:}
     * line.
     *
     * <p>Field freqSpell coded before 261010, commented in full on 261010.
     */
    private int freqSpell;
    /**
     * The power of the monster's spells, C's {@code race->spell_power}, from the
     * {@code spell-power:} line.
     *
     * <p>Field spellPower coded before 261010, commented in full on 261010.
     */
    private int spellPower;

    /**
     * The race flags this monster has set, C's {@code race->flags}. Tested with
     * {@link #hasMonsterRaceFlag(MonsterRaceFlag)}.
     *
     * <p>Field flags coded before 261010, commented in full on 261010.
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * Intended to hold race flags cleared for this monster, overriding its base. <b>Dead:</b> C has
     * no such field (its {@code flags-off:} line removes the named flag from {@code flags}
     * directly, via {@code parse_monster_flags_off} in {@code mon-init.c}), and nothing in
     * this class assigns or reads this one, so it is always {@code null} for a race built by the
     * main constructor.
     *
     * <p>Field flagsOff coded before 261010, commented in full on 261010.
     */
    private Flag<MonsterRaceFlag> flagsOff;
    /**
     * The spells this monster can cast, C's {@code race->spell_flags}.
     *
     * <p>Field spells coded before 261010, commented in full on 261010.
     */
    private Flag<MonsterSpell> spells;

    /**
     * The monster's melee blows, C's {@code race->blow}, in file order. See {@link #getBlows()}.
     *
     * <p>Field blow coded before 261010, commented in full on 261010.
     */
    private List<MonsterBlow> blow;

    /**
     * The monster's native dungeon level, C's {@code race->level}, from the {@code depth:} line.
     *
     * <p>Field level coded before 261010, commented in full on 261010.
     */
    private int level;
    /**
     * Rarity of generation, C's {@code race->rarity}: a larger value makes the monster less likely
     * to be chosen.
     *
     * <p>Field rarity coded before 261010, commented in full on 261010.
     */
    private int rarity;

    /**
     * The default display glyph and colour, C's {@code race->d_char} and {@code race->d_attr}
     * folded into one value.
     *
     * <p>Field adc coded before 261010, commented in full on 261010.
     */
    private AngbandDisplayCharacter adc;

    /**
     * Maximum population allowed on a level, C's {@code race->max_num}: normally {@code 100}, and
     * {@code 1} for a unique. C declares it {@code uint8_t}; here it is an {@code int} and
     * {@link #setMaxNum(int)} does not range-check it.
     *
     * <p>Field maxNum coded before 261010, commented in full on 261010.
     */
    private int maxNum;
    /**
     * Population on the current level, C's {@code race->cur_num}. See {@link #setCurNum(int)}.
     *
     * <p>Field curNum coded before 261010, commented in full on 261010.
     */
    private int curNum;

    /**
     * Alternate spell-cast messages, standing in for C's {@code race->spell_msgs} list of
     * {@code monster_altmsg}. Keyed by spell name, with one {@link MonsterSpellMessage} (visible,
     * invisible and miss text) per spell, where C keeps a separate record for each overridden
     * message.
     *
     * <p>Field spellMessages coded before 261010, commented in full on 261010.
     */
    private Map<String, MonsterSpellMessage> spellMessages;
    /**
     * What the monster may drop on death, C's {@code race->drops}, in file order (C's list runs in
     * reverse file order, see the class block).
     *
     * <p>Field drops coded before 261010, commented in full on 261010.
     */
    private List<MonsterDrop> drops;

    /**
     * Companion races that may be generated with this monster, C's {@code race->friends}, in file
     * order (C's list runs in reverse). Each entry names its race as text until
     * {@link #resolveFriends()} points it at the race itself.
     *
     * <p>Field friends coded before 261010, commented in full on 261010.
     */
    private List<MonsterFriends> friends;
    /**
     * Companion base types that may be generated with this monster, C's {@code race->friends_base},
     * in file order (C's list runs in reverse). Unlike {@link #friends} these are resolved when
     * parsed, as C does, because a base is always loaded before any race.
     *
     * <p>Field friendsBase coded before 261010, commented in full on 261010.
     */
    private List<MonsterFriendsBase> friendsBase;

    /**
     * The object kinds this monster can pose as, C's {@code race->mimic_kinds}, in file order (C's
     * list runs in reverse). C wraps each kind in a {@code monster_mimic} record; here the list
     * holds the kinds directly.
     *
     * <p>Field mimicKinds coded before 261010, commented in full on 261010.
     */
    private List<ObjectKind> mimicKinds;

    /**
     * The shapes this monster can change into, C's {@code race->shapes}, in file order (C's list
     * runs in reverse). Each entry holds its name until {@link #resolveShapes()} fills in a base or
     * a race.
     *
     * <p>Field shapes coded before 261010, commented in full on 261010.
     */
    private List<MonsterShape> shapes;
    /**
     * The number of shapes, C's {@code race->num_shapes}, which C increments once per
     * {@code shape:} line. The assembler passes {@code shapes.size()}, so this always equals the
     * size of {@link #shapes}.
     *
     * <p>Field numShapes coded before 261010, commented in full on 261010.
     */
    private int numShapes;

    /**
     * The player's accumulated lore about this monster. C keeps these records in the global
     * {@code l_list}, one per race, found by {@code get_lore}; here the record is a field of the
     * race. It is {@code null} until {@link #setLore(MonsterLore)} is called. The only production
     * caller is the lore-file reader ({@code LoreGrammar.g4}), which attaches a record to the race
     * named in each lore entry; loading {@code monster.txt} does not attach one, where C's
     * {@code finish_parse_monster} allocates a blank record for every race. A race that no lore
     * entry names therefore has {@code null} here.
     *
     * <p>Field lore coded before 261010, commented in full on 261011.
     */
    private MonsterLore lore;

    /**
     * The colour-cycling animation driving this race's display glyph, resolved by group/cycle name
     * by the assembler before the constructor runs; {@code null} for a race whose glyph does not
     * animate. In C, the {@code color-cycle:} line calls {@code visuals_cycler_set_cycle_for_race}
     * at parse time.
     *
     * <p>Field cycler coded before 261010, commented in full on 261010.
     */
    private ColourCycle cycler;

    /**
     * Build a fully-specified monster race from its parsed data-file fields. Every argument is
     * stored as given: nothing is validated, scaled or defaulted here (the assembler does the
     * defaulting), and the lists and flag sets are kept by reference, not copied. The lore record
     * is not an argument; attach it with {@link #setLore(MonsterLore)}. Friend and shape names are
     * still unresolved text, to be resolved by {@link #resolveFriends()} and
     * {@link #resolveShapes()}.
     *
     * <p>Constructor coded before 261010, commented in full on 261010.
     *
     * @param name          monster name
     * @param text          flavour text
     * @param plural        plural name form, or {@code null} if the data gives none
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
     * Attach this race's accumulated lore record. This is the Java stand-in for the allocation of
     * C's {@code l_list} at the end of {@code finish_parse_monster}, which gives every race a lore
     * record with room for {@code mon_blows_max} blows, except that it is called by the lore-file
     * reader ({@code LoreGrammar.g4}) once per lore entry rather than once per race by the race
     * loader. Nothing checks the argument, so passing {@code null} clears the record.
     *
     * <p>Function setLore coded before 261010, commented in full on 261011.
     *
     * @param lore the lore to attach
     */
    public void setLore(MonsterLore lore) {
        this.lore = lore;
    }

    /**
     * Returns this race's accumulated lore record. C's {@code get_lore} instead indexes a global
     * {@code l_list} array by {@code race->ridx}; this class stores the record directly on the race
     * as a field (see {@link #setLore(MonsterLore)}) rather than reproducing that lookup table, so
     * this accessor is a plain field read rather than a search.
     *
     * <p>C's {@code get_lore} asserts that the race is not {@code NULL} and always hands back a
     * valid record, because {@code finish_parse_monster} allocates one for every race. This
     * accessor has no such guarantee: it returns {@code null} until {@link #setLore(MonsterLore)}
     * has been called (today only the lore-file reader calls it), so a caller that dereferences the
     * result, as {@code PlayerBirth} does, fails with a {@link NullPointerException} for any race
     * that was never given a record.
     *
     * <p>Function getLore coded on 260903, commented in full on 261011.
     *
     * @return this race's lore record, or {@code null} if none has been attached
     */
    public MonsterLore getLore() {
        return lore;
    }

    /**
     * Test-only constructor producing a bare race with an empty flag set. Every other field keeps
     * its default ({@code null} or zero), so the race can be used for {@link #hasMonsterRaceFlag}
     * and for the setters, but not for anything that reads its lists or {@link #name}.
     *
     * <p>Constructor coded before 261010, commented in full on 261010.
     */
    @TestOnly
    public MonsterRace() {
        flags = new Flag<>(MonsterRaceFlag.class);
    }

    /**
     * Returns this race's flag set directly. Private and not called anywhere in this class; use
     * {@link #hasMonsterRaceFlag(MonsterRaceFlag)} to test a flag.
     *
     * <p>Function getFlags coded before 261010, commented in full on 261010.
     *
     * @return this race's set race flags
     */
    private Flag<MonsterRaceFlag> getFlags() {
        return flags;
    }

    /**
     * Test whether this race has a given race flag set — the port of C's {@code rf_has}. C's macro
     * expands to {@code flag_has_dbg}, which adds only a bounds check on the flag's byte offset
     * that an enum argument cannot violate, so this is a single {@link Flag#has} lookup.
     *
     * <p>Function hasMonsterRaceFlag coded before 261010, commented in full on 261010.
     *
     * @param flag the race flag to test
     * @return true if the flag is set on this race
     */
    public boolean hasMonsterRaceFlag(MonsterRaceFlag flag) {
        return flags.has(flag);
    }

    /**
     * Returns this race's name, C's {@code race->name}. {@code null} only for a race built with the
     * test-only constructor.
     *
     * <p>Function getName coded before 261010, commented in full on 261010.
     *
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
     *
     * <p>This is the friends half of the conversion loop in C's {@code finish_parse_monster}. C
     * tests for {@code "same"} (any case) there and points the entry at the race itself by
     * identity; every other name goes through {@code lookup_monster}, which takes an exact
     * case-insensitive match if there is one and otherwise the first race whose name contains the
     * text. Here the substitution happens earlier and the lookup is {@code lookupMonsterRace},
     * which has the same exact-then-substring rule, so the two agree whenever race names are unique
     * (the shipped {@code monster.txt} has no duplicate names). A pair of races sharing a name would
     * resolve a {@code "same"} friend to the <em>first</em> of them, where C would pick the one
     * being resolved.
     *
     * <p>A name that matches no race throws {@link IllegalArgumentException} from
     * {@link MonsterFriends#setRace(String)}, standing in for C's {@code quit_fmt}. Its message names
     * the missing friend but not the race that asked for it, which C's does.
     *
     * <p>Function resolveFriends coded before 261010, commented in full on 261010.
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
     *
     * <p>This is the shapes half of the conversion loop in C's {@code finish_parse_monster}. C looks
     * the base up at parse time, with {@code lookup_monster_base} (an exact, case-sensitive match
     * with no substring fallback), and in the second pass looks up a race with {@code lookup_monster}
     * only for a shape whose base came back {@code NULL}. Doing both lookups here gives the same
     * answer, because every base is loaded before the races. A shape that names a base ends with
     * its base set and its race {@code null}; a shape that names a race ends with its race set and
     * its base {@code null}. Both agree with C's zero-filled record.
     *
     * <p>A name that is neither throws {@link InvalidTokenFoundDuringParse}, standing in for C's
     * {@code quit_fmt}; as with {@link #resolveFriends()} the message omits the race that asked.
     *
     * <p>Function resolveShapes coded before 261010, commented in full on 261010.
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
     * Returns this race's native dungeon level, C's {@code race->level}, from the {@code depth:}
     * line.
     *
     * <p>Function getLevel coded before 261010, commented in full on 261010.
     *
     * @return this race's native dungeon depth/level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the light intensity this race radiates, C's {@code race->light}. The value is signed
     * and is read directly wherever C reads the field: a positive value lights the grids around the
     * monster, zero means it neither lights nor darkens, and a negative value darkens them. C treats
     * the magnitudes as distinct, so callers should compare rather than test for truth &mdash; the
     * lore code distinguishes {@code > 1}, {@code == 1}, {@code == -1} and {@code < -1}, while the
     * attack code counts a monster with {@code light > 0} as visible even when it is not otherwise
     * seen.
     *
     * <p>There is no clamping or scaling on either side: the parser stores the {@code light:} line's
     * integer as written, and a race whose entry omits the line keeps zero.
     *
     * <p>Function getLight coded on 260828, commented in full on 261010.
     *
     * @return the light radius this race emits, negative for a race that darkens its surroundings
     */
    public int getLight() {
        return light;
    }
}
