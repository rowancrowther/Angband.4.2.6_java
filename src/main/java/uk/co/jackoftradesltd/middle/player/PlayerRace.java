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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.HashMap;
import java.util.Map;

/**
 * A playable character race — Human, Half-Troll, High-Elf and so on — and the innate
 * modifiers it confers.
 *
 * <p>Ports the C {@code struct player_race} ({@code player.h}), defined by {@code p_race.txt}.
 * A race contributes to nearly every derived attribute at character creation and recalculation:
 * stat and skill adjustments, hit-dice and experience scaling, the physical ranges used to roll
 * age/height/weight, infravision radius, an equipment {@link PlayerBody}, innate object/player
 * flags, the background-history chart to roll on, and elemental resistance modifiers.
 *
 * <p><b>Why everything hangs off one immutable record:</b> race is fixed for a character's life
 * and combines with {@link PlayerClass} to produce the player's effective profile, so the port
 * keeps all of a race's contributions on a single value object — loaded once from data, then
 * only read.
 *
 * @author Rowan Crowther
 */
public class PlayerRace {
    /**
     * Logger for this type.
     */
    private static final Logger logger = LogManager.getLogger();

    /** Display name of the race, e.g. {@code "Half-Troll"} (C: {@code player_race.name}). */
    private String name;
    /** Stable race index/identifier (C: {@code player_race.ridx}). */
    private int rIndex;

    /** The race's share of maximum hit points (its hit-dice contribution). */
    private int raceMhp;
    /** Experience-point multiplier; a higher value means the race levels more slowly. */
    private int raceExp;

    /** Base starting age in years, before the random spread. */
    private int baseAge;
    /** Random spread added to {@link #baseAge} when rolling starting age. */
    private int modAge;

    /** Base height before the random spread. */
    private int baseHeight;
    /** Random spread added to {@link #baseHeight}. */
    private int modHeight;
    /** Base weight before the random spread. */
    private int baseWeight;
    /** Random spread added to {@link #baseWeight}. */
    private int modWeight;

    /** Infravision radius in grids — how far the race sees warm creatures in the dark. */
    private int infravision;

    /** The equipment-slot layout this race uses (see {@link PlayerBody}). */
    private PlayerBody body;

    /** Per-stat adjustments applied to a character of this race. */
    private Map<Stats, Integer> statsAdj;
    /** Per-skill adjustments applied to a character of this race. */
    private Map<PlayerSkill, Integer> skillsAdj;

    /** Innate object flags the race confers (e.g. sustain, free action). */
    private Flag<ObjectFlag> oFlags;
    /** Innate player flags the race confers (see {@link PlayerFlag}). */
    private Flag<PlayerFlag> pFlags;

    /** The background-history chart this race's biography generation starts from. */
    private PlayerHistoryChart history;

    /**
     * Resistance/element modifiers conferred by the race, keyed by {@link ElementEnum}.
     */
    private Map<ElementEnum, ElementInfo> resists;

    /**
     * Builds a race from its parsed attributes; each parameter populates the like-named field
     * (see those fields for detail).
     *
     * @param name        display name
     * @param rIndex      stable race index
     * @param raceMhp     hit-dice contribution
     * @param raceExp     experience multiplier
     * @param baseAge     base starting age
     * @param modAge      random age spread
     * @param baseHeight  base height
     * @param modHeight   random height spread
     * @param baseWeight  base weight
     * @param modWeight   random weight spread
     * @param infravision infravision radius
     * @param body        equipment-slot layout
     * @param statsAdj    per-stat adjustments
     * @param skillsAdj   per-skill adjustments
     * @param oFlags      innate object flags
     * @param pFlags      innate player flags
     * @param history     background-history starting chart
     * @param resists     resistance/element modifiers
     */
    public PlayerRace(String name, int rIndex, int raceMhp, int raceExp, int baseAge, int modAge, int baseHeight,
                      int modHeight, int baseWeight, int modWeight, int infravision, PlayerBody body,
                      Map<Stats, Integer> statsAdj, Map<PlayerSkill, Integer> skillsAdj, Flag<ObjectFlag> oFlags,
                      Flag<PlayerFlag> pFlags, PlayerHistoryChart history, Map<ElementEnum, ElementInfo> resists) {
        this.name = name;
        this.rIndex = rIndex;
        this.raceMhp = raceMhp;
        this.raceExp = raceExp;
        this.baseAge = baseAge;
        this.modAge = modAge;
        this.baseHeight = baseHeight;
        this.modHeight = modHeight;
        this.baseWeight = baseWeight;
        this.modWeight = modWeight;
        this.infravision = infravision;
        this.body = body;
        this.statsAdj = statsAdj;
        this.skillsAdj = skillsAdj;
        this.oFlags = oFlags;
        this.pFlags = pFlags;
        this.history = history;
        this.resists = resists;
    }

    /**
     * The port of C's {@code race->name} — the name as it appears in {@code p_race.txt} and on the
     * character sheet. Races are matched by {@code rIndex} rather than by name, so this is for
     * display; it is not an identity.
     *
     * @return the race's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Whether the race has an innate stake in an element, and so starts the game already able to
     * read that element's rune. The port of the test C makes inline in {@code player_learn_innate}
     * ({@code obj-knowledge.c:1450}):
     *
     * <pre>{@code
     * if (p->race->el_info[element].res_level != 0)
     * }</pre>
     *
     * <p><b>Non-zero, not positive.</b> A vulnerability is as learnable as a resistance — the rune
     * is the element's, not the sign's, and a character who burns easily has learned quite as much
     * about fire as one who does not. Testing {@code > 0} would leave a vulnerable race unable to
     * read a rune it plainly knows.
     *
     * <p>C reads a full {@code el_info[ELEM_MAX]} array, where an element the race file never
     * mentions is simply zero. This port's map is <em>sparse</em>: the assembler only creates an
     * entry for a {@code RES_} line actually present in the data, so most elements are absent for
     * most races, and absent has to mean the same as zero.
     *
     * @param element the element to ask about
     * @return true if the race's data gives this element a non-zero resistance level
     */
    public boolean getResistKnowledge(ElementEnum element) {
        ElementInfo info = resists.get(element);
        return (info != null && info.getResLevel() != 0);
    }

    /**
     * Whether the race confers an object flag innately, and so starts the game already able to read
     * that flag's rune. The flag counterpart of {@link #getResistKnowledge(ElementEnum)}, and the
     * port of C's membership test on {@code p->race->flags}.
     *
     * <p>C never asks this question one flag at a time — {@code player_learn_innate} walks the set
     * bits directly with {@code of_next(p->race->flags, FLAG_START)}. Asking per flag lets the
     * caller drive the loop from {@link ObjectFlag} instead, which reaches the same set at the cost
     * of visiting the flags the race does not have.
     *
     * @param objectFlag the flag to ask about
     * @return true if the race confers this flag
     */
    public boolean getObjectFlagKnowledge(ObjectFlag objectFlag) {
        return oFlags.has(objectFlag);
    }

    /**
     * The object flags every member of this race has innately — C's {@code race->flags}, the
     * {@code player-flags:} entries of {@code p_race.txt} that name object rather than player flags.
     *
     * <p>Gathered by {@code player_flags} into the same set the equipment contributes to, so a
     * consumer asks once whether the player has a flag rather than asking race, gear and status in
     * turn ({@code player.c:290-300}).
     *
     * <p>Function getoFlags commented in full on 260820.
     *
     * @return a read-only view of the race's innate object flags
     */
    public FlagView<ObjectFlag> getoFlags() {
        return oFlags;
    }

    /**
     * The race's infravision range, in units of ten feet — C's {@code race->infra}.
     *
     * <p>The starting value for the calculated state's infravision, set before equipment is looked
     * at ({@code player-calcs.c:1903}); worn items and the {@code TMD_SINFRA} status add to it from
     * there.
     *
     * <p>Function getInfravision commented in full on 260820.
     *
     * @return the innate infravision range
     */
    public int getInfravision() {
        return infravision;
    }

    /**
     * The race's contribution to one skill — C's {@code race->r_skills[skill]}.
     *
     * <p>Half of a skill's base: {@code calcBonuses} seeds each skill with the race's value plus the
     * class's before anything else touches it ({@code player-calcs.c:1904-1906}).
     *
     * <p>Function getSkill commented in full on 260820.
     *
     * @param skill the skill to read
     * @return the race's adjustment for that skill
     * @throws NullPointerException if the race has no entry for the skill, which a fully parsed
     *                              race always does
     */
    public int getSkill(PlayerSkill skill) {
        return skillsAdj.get(skill);
    }

    /**
     * The race's innate resistance to one element — C's {@code race->el_info[element].res_level}.
     *
     * <p>Three-way, not a scale of goodness: {@code -1} is a vulnerability, {@code 0} no opinion,
     * positive values successive grades of resistance. {@code calcBonuses} treats the vulnerability
     * specially, remembering it and applying it only after every other source has had its say, so
     * that a resistance from elsewhere is compared against the unpenalised level
     * ({@code player-calcs.c:1908-1913}).
     *
     * <p>An element the race says nothing about reads as {@code 0}, matching C's zeroed array.
     *
     * <p>Function getResistanceLevel commented in full on 260820.
     *
     * @param element the element to read
     * @return the race's resistance level for that element
     */
    public int getResistanceLevel(ElementEnum element) {
        ElementInfo info = resists.getOrDefault(element, null);

        if (info != null) return info.getResLevel();
        return 0;
    }

    /**
     * The player flags every member of this race has — C's {@code race->pflags}, the abilities and
     * quirks named on {@code p_race.txt}'s {@code player-flags:} line.
     *
     * <p>Copied into the calculated state before the class's are unioned in, which is why this one
     * is a copy and that one a union: the state's set is empty at that point, so one of the two has
     * to establish it ({@code player-calcs.c:1917-1919}).
     *
     * <p>Function getpFlags commented in full on 260820.
     *
     * @return a read-only view of the race's player flags
     */
    public FlagView<PlayerFlag> getpFlags() {
        return pFlags;
    }

    /**
     * The body layout members of this race are built with — the slots they can wear things in.
     *
     * <p>C stores an index into the global {@code bodies} array and copies the body onto the player
     * at birth ({@code player-birth.c}); the port resolves the index at load time and holds the body
     * itself. Callers wanting a player's own slots must not use this one: it is the shared template,
     * and a player copies it ({@code Player.embody}) before anything is worn.
     *
     * <p>Function getBody commented in full on 260820.
     *
     * @return the race's body template, not a copy
     */
    public PlayerBody getBody() {
        return body;
    }

    /**
     * The race's adjustment to one stat — C's {@code race->r_adj[stat]}.
     *
     * <p>Added to the class's adjustment and to whatever the equipment contributes, and the sum is
     * applied through {@code modify_stat_value} rather than by plain addition, because a point of
     * bonus is worth one below 18 and ten above it ({@code player-calcs.c:2058-2061}).
     *
     * <p>Function getStatAdjust commented in full on 260820.
     *
     * @param stat the stat to read
     * @return the race's adjustment in points
     * @throws NullPointerException if the race has no entry for the stat, which a fully parsed race
     *                              always does
     */
    public int getStatAdjust(Stats stat) {
        return statsAdj.get(stat);
    }

    /**
     * The race's base starting age in years — C's {@code race->b_age}, the first field of the
     * {@code age:base:mod} line of {@code p_race.txt} ({@code init.c:2698,2808}).
     *
     * <p>It is the floor of the starting age, never the whole of it: birth rolls
     * {@code p->age = race->b_age + randint1(race->m_age)} ({@code player-birth.c:356}), so the
     * base alone is one below the lowest age a character can actually start with.
     *
     * <p>Function getBaseAge commented in full on 260902.
     *
     * @return the base starting age in years
     */
    public int getBaseAge() {
        return baseAge;
    }

    /**
     * The random spread added to {@link #getBaseAge()} at birth — C's {@code race->m_age}, the second field
     * of the {@code age:base:mod} line of {@code p_race.txt} ({@code init.c:2699,2808}).
     *
     * <p>Used as the argument to {@code randint1}, which returns 1..m_age inclusive, so a race's
     * starting age lies in {@code b_age + 1 .. b_age + m_age} — the spread contributes at least
     * one year, and the top of the range is {@code b_age + m_age}, not {@code b_age + m_age - 1}.
     * The widest shipped spread is the Elf's 75 on a base of 75; the oldest possible starting
     * character is the High-Elf at {@code 100 + 30}.
     *
     * <p>Function getModAge commented in full on 260902.
     *
     * @return the random age spread in years
     */
    public int getModAge() {
        return modAge;
    }

    /**
     * The race's mean height in inches — C's {@code race->base_hgt}, the first field of the
     * {@code height:base_hgt:mod_hgt} line of {@code p_race.txt} ({@code init.c:2707,2809}).
     *
     * <p>Unlike {@link #getBaseAge()} this is a centre, not a floor. Birth rolls
     * {@code Rand_normal(race->base_hgt, race->mod_hgt)} ({@code player-birth.c:359}), which spreads
     * symmetrically about the mean, so roughly half of a race's characters start shorter than the
     * base and half taller.
     *
     * <p>The shipped range runs from the Hobbit's 34 inches to the Half-Troll's 90.
     *
     * <p>Function getBaseHeight commented in full on 260902.
     *
     * @return the mean starting height in inches
     */
    public int getBaseHeight() {
        return baseHeight;
    }

    /**
     * The race's height spread — C's {@code race->mod_hgt}, the second field of the
     * {@code height:base_hgt:mod_hgt} line of {@code p_race.txt} ({@code init.c:2708,2809}).
     *
     * <p>It is the standard deviation passed to {@code Rand_normal} as {@code stand}, not a plain
     * range. The offset drawn is {@code stand * low / RANDNOR_STD} with {@code low} in
     * {@code 0 .. 256} and {@code RANDNOR_STD} 64 ({@code z-rand.c:314}), so the spread reaches at
     * most four times this value either side of the base, and a {@code stand} below 1 short-circuits
     * the roll and returns the mean unchanged ({@code z-rand.c:296}).
     *
     * <p>Shipped values run from the Half-Orc's 2 — a race of nearly uniform height — to the
     * Half-Troll's 16, whose characters can be born anywhere from 26 to 154 inches.
     *
     * <p>Function getModHeight commented in full on 260902.
     *
     * @return the height standard deviation in inches
     */
    public int getModHeight() {
        return modHeight;
    }

    /**
     * The race's weight spread - C's {@code race->mod_wgt}, the second field of the
     * {@code weight:base_wgt:mod_wgt} line of {@code p_race.txt} ({@code init.c:2717,2810}).
     *
     * <p>It is the standard deviation handed to {@code Rand_normal} at birth
     * ({@code player-birth.c:360}), so the reachable weights run four deviations either side of the
     * base and a spread below one pins every character of the race to the base exactly
     * ({@code z-rand.c:296}).
     *
     * <p>Shipped values run from the 5 of the Hobbit, the Gnome and the Kobold - small races of
     * near-uniform build - to the Half-Troll's 60, which is wide enough that the low tail reaches
     * zero pounds.
     *
     * <p>Function getModWeight commented in full on 260902.
     *
     * @return the weight standard deviation in pounds
     */
    public int getModWeight() {
        return modWeight;
    }

    /**
     * The race's mean weight in pounds - C's {@code race->base_wgt}, the first field of the
     * {@code weight:base_wgt:mod_wgt} line of {@code p_race.txt} ({@code init.c:2716,2810}).
     *
     * <p>It is the mean handed to {@code Rand_normal} at birth ({@code player-birth.c:360}), not a
     * floor: the offset is added or subtracted with even odds ({@code z-rand.c:316}), so about half
     * a race's characters weigh less than this.
     *
     * <p>Shipped values run from the Hobbit's 55 to the Half-Troll's 240.
     *
     * <p>Function getBaseWeight commented in full on 260902.
     *
     * @return the mean starting weight in pounds
     */
    public int getBaseWeight() {
        return baseWeight;
    }

    /**
     * A copy of this race that shares no mutable structure with it — the value
     * {@code PlayerBirth.playerGenerate} gives a player as its own race
     * ({@code PlayerBirth.java:428}).
     *
     * <p><b>There is no C counterpart, and the divergence is deliberate.</b> C's
     * {@code player_generate} writes the pointer straight through — {@code p->race = r}
     * ({@code player-birth.c:991}) — because a race record is read-only data loaded once from
     * {@code p_race.txt} and shared by every character of that race. This port holds races in
     * {@link uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry} and hands out
     * objects with mutable interiors, so a shared reference would let one character write into the
     * registry's template and through it into every other character of the race. Copying at the
     * birth boundary is what keeps that from happening.
     *
     * <p><b>What is deep and why.</b> The two adjustment maps, both flag sets and the whole
     * resistance map are rebuilt: {@link ElementInfo#getFlags()} deliberately returns its
     * live set, matching C's {@code el_info[i].flags}, so an {@link ElementInfo} shared between
     * copies is writable through, and each is therefore copied in turn. {@link PlayerBody#copy()}
     * does the same for the equipment template. The maps come back as {@link HashMap}s whatever the
     * source used; every reader is a lookup ({@link #getStatAdjust}, {@link #getSkill},
     * {@link #getResistanceLevel}), so no ordering rests on it.
     *
     * <p><b>What is shared, and legitimately.</b> {@code name} is a {@link String}, and
     * {@link #history} is shared by reference: a {@link PlayerHistoryChart} is a node in the global
     * chart graph, linked to its successors, and C shares exactly that graph across every character
     * — copying one would mean copying the graph it leads to. The same reasoning makes
     * {@code PlayerClass.copy} share its {@code ClassMagic}.
     *
     * <p>{@code rIndex} is carried across unchanged, so a copy still answers to the identity races
     * are matched by; only the storage is the character's own.
     *
     * <p>Function copy coded on 260902, commented in full on 260902.
     *
     * @return a race equal to this one field for field, sharing only its name and history chart
     */
    public PlayerRace copy() {
        Map<Stats, Integer> statsAdjClone = new HashMap<>(this.statsAdj);
        Map<PlayerSkill, Integer> skillsAdjClone = new HashMap<>(this.skillsAdj);
        Flag<ObjectFlag> oFlagsClone = new Flag<>(ObjectFlag.class);
        oFlagsClone.copyFrom(oFlags);
        Flag<PlayerFlag> pFlagsClone = new Flag<>(PlayerFlag.class);
        pFlagsClone.copyFrom(pFlags);
        Map<ElementEnum, ElementInfo> resistsClone = new HashMap<>();
        for (ElementEnum element : resists.keySet()) {
            ElementInfo info = resists.get(element).copy();
            resistsClone.put(element, info);
        }

        return new PlayerRace(this.name, this.rIndex, this.raceMhp, this.raceExp,
                this.baseAge, this.modAge, this.baseHeight, this.modHeight, this.baseWeight,
                this.modWeight, this.infravision, this.body.copy(), statsAdjClone,
                skillsAdjClone, oFlagsClone, pFlagsClone, this.history, resistsClone);
    }

    /**
     * The race's contribution to a character's experience factor.
     *
     * <p>C reads {@code r_exp} directly, and in one place only:
     * {@code p->expfact = p->race->r_exp + p->class->c_exp} ({@code player-birth.c:997}). The race
     * supplies the bulk of the value — 100 for a Human, rising through the longer-lived and more
     * gifted races — and the class adds a smaller amount on top.
     *
     * <p>The number is a percentage, and it scales the cost of every level rather than the
     * experience awarded for a kill: the level thresholds in
     * {@code PlayerRegistry.playerExperience} are multiplied by it and divided by 100 wherever a
     * level is recomputed. A race at 100 pays the table price; one at 145 pays 45% more for every
     * level of its career.
     *
     * <p>Function getExpFactor commented in full on 260902.
     *
     * @return the race's experience factor, a percentage addend in C's {@code r_exp}
     */
    public int getExpFactor() {
        return raceExp;
    }

    /**
     * The race's contribution to the size of the character's hit die.
     *
     * <p>C reads {@code r_mhp} directly, and in one place only:
     * {@code p->hitdie = p->race->r_mhp + p->class->c_mhp} ({@code player-birth.c:1000}). The race
     * supplies the bulk of the figure and covers a narrow band — 7 for a Hobbit up to 12 for a
     * Half-Troll, with most races at 9 or 10 — while the class adds the wider-swinging remainder
     * on top.
     *
     * <p>The value is a die size, not a quantity of hit points:
     * {@link PlayerBirth#rollHP(Player)} rolls {@code randint1(hitdie)} for every level above the
     * first, so the race sets the floor under how fast a character can be expected to toughen up
     * rather than handing out anything at birth.
     *
     * <p>Function getMaxHitDie commented in full on 260902.
     *
     * @return the race's hit-die contribution, C's {@code r_mhp}
     */
    public int getMaxHitDie() {
        return this.raceMhp;
    }

    /**
     * The background chart the race's history is generated from — the port of reading C's
     * {@code p->race->history} ({@code player.h:200}).
     *
     * <p>A chart is the entry point to a chain, not a single table of text. Each entry it holds
     * carries a successor chart, so generating a history walks from this starting chart through as
     * many linked charts as the race's background has stages, concatenating a rolled line from
     * each. {@code player_generate} passes this straight to {@code get_history}
     * ({@code player-birth.c:1027}), whose result becomes the character's history string.
     *
     * <p>The chart belongs to the race definition rather than to the character: it is fixed data
     * describing what backgrounds that race can have, and it is read afresh every time the birth
     * screen regenerates the player, which is why choosing a different race there replaces the
     * history outright.
     *
     * <p>Function getHistory commented in full on 260902.
     *
     * @return the race's starting background chart, C's {@code history}
     */
    public PlayerHistoryChart getHistory() {
        return history;
    }
}