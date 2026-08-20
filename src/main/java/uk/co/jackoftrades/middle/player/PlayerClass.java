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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.channel.utils.FlagView;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.*;

/**
 * A playable character class — Warrior, Mage, Priest, Ranger and so on — and the abilities,
 * proficiencies and combat parameters it grants.
 *
 * <p>Ports the C {@code struct player_class} ({@code player.h}), defined by {@code class.txt}.
 * Class is the counterpart to {@link PlayerRace}: where race sets innate physical traits, class
 * determines the character's profession — its per-level titles, stat and skill adjustments, hit
 * point and experience scaling, innate flags, melee blow parameters, starting equipment, and
 * (for casters) its spellcasting via {@link ClassMagic}.
 *
 * <p><b>Why the blow parameters live here:</b> {@link #maxAttacks}, {@link #minWeight} and
 * {@link #attMultiplier} are the class-specific inputs to the melee blows-per-round calculation
 * (heavier weapons and lighter classes yield fewer blows), so they are intrinsic to the class
 * rather than to any weapon. Keeping them with the rest of the class data gathers that formula's
 * inputs in one place.
 *
 * @author Rowan Crowther
 */
public class PlayerClass {
    /**
     * Logger for this class.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * Display name of the class, e.g. {@code "Ranger"} (C: {@code player_class.name}).
     */
    private String name;

    /** Per-level titles shown for the class as the character advances. */
    private List<String> titles;
    /** Per-stat adjustments applied to a character of this class. */
    private Map<Stats, Integer> stats;
    /** Base skill levels granted by the class. */
    private Map<PlayerSkill, Integer> classSkills;
    /** Additional skill gained per level — the class's skill growth rate. */
    private Map<PlayerSkill, Integer> extraSkills;

    /** Hit-point adjustment contributed by the class. */
    private int hpAdj;
    /** Experience-point multiplier; a higher value means the class levels more slowly. */
    private int expAdj;

    /** Innate object flags the class confers. */
    private Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
    /** Innate player flags the class confers (see {@link PlayerFlag}). */
    private Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);

    /** Maximum melee blows per round the class can reach. */
    private int maxAttacks;
    /** Weapon weight (tenths of a pound) at or below which the class incurs no blow penalty. */
    private int minWeight;
    /** Multiplier in the blows-per-round formula tuning how readily the class earns extra blows. */
    private int attMultiplier;

    /** The equipment a character of this class begins with (see {@link StartItem}). */
    private List<StartItem> startItems;

    /** Spellcasting definition for the class, or {@code null} for a non-caster (see {@link ClassMagic}). */
    private ClassMagic magic;

    /**
     * Builds a class from its parsed attributes; each parameter populates the like-named field
     * (see those fields for detail).
     *
     * @param name          display name
     * @param titles        per-level titles
     * @param stats         per-stat adjustments
     * @param classSkills   base skill levels
     * @param extraSkills   per-level skill growth
     * @param hpAdj         hit-point adjustment
     * @param expAdj        experience multiplier
     * @param oFlags        innate object flags
     * @param pFlags        innate player flags
     * @param maxAttacks    maximum melee blows per round
     * @param minWeight     no-penalty weapon weight threshold
     * @param attMultiplier blows-per-round formula multiplier
     * @param startItems    starting equipment
     * @param magic         spellcasting definition, or {@code null} for a non-caster
     */
    public PlayerClass(String name, List<String> titles, Map<Stats, Integer> stats,
                       Map<PlayerSkill, Integer> classSkills,
                       Map<PlayerSkill, Integer> extraSkills, int hpAdj, int expAdj,
                       Flag<ObjectFlag> oFlags, Flag<PlayerFlag> pFlags, int maxAttacks,
                       int minWeight, int attMultiplier,
                       List<StartItem> startItems,
                       ClassMagic magic) {
        this.name = name;
        this.titles = titles;
        this.stats = stats;
        this.classSkills = classSkills;
        this.extraSkills = extraSkills;
        this.hpAdj = hpAdj;
        this.expAdj = expAdj;
        this.oFlags = oFlags;
        this.pFlags = pFlags;
        this.maxAttacks = maxAttacks;
        this.minWeight = minWeight;
        this.attMultiplier = attMultiplier;
        this.startItems = startItems;
        this.magic = magic;
    }

    /**
     * The class's blow multiplier — C's {@code class->att_multiply}, the numerator of the
     * strength-versus-weight ratio behind blows per turn.
     *
     * <p>Scales how much a class gets out of its strength: {@code calcBlows} computes
     * {@code adjStrBlow[STR] * this / weapon weight} and uses the result as a table rung, so a
     * warrior's higher multiplier reaches a faster row with the same weapon and arm
     * ({@code player-calcs.c:1717-1718}).
     *
     * <p>Function getAttMultiply commented in full on 260820.
     *
     * @return the class's attack multiplier
     */
    public int getAttMultiply() {
        return attMultiplier;
    }

    /**
     * @return the class's spellcasting definition, or {@code null} for a non-caster
     */
    public ClassMagic getMagic() {
        return magic;
    }

    /**
     * The object flags this class has innately — C's {@code class->flags}.
     *
     * <p>Unioned with the race's by {@code player_flags} into the same set the equipment fills, so a
     * flag from either source counts once ({@code player.c:290-300}).
     *
     * <p>Function getoFlags commented in full on 260820.
     *
     * @return a read-only view of the class's innate object flags
     */
    public FlagView<ObjectFlag> getoFlags() {
        return oFlags;
    }

    /**
     * The class's flat contribution to one skill — C's {@code class->c_skills[skill]}.
     *
     * <p>The half of a skill's base that does not grow: added to the race's value at the start of
     * {@code calcBonuses} ({@code player-calcs.c:1904-1906}), where {@link #getXSkill} supplies the
     * part that scales with level.
     *
     * <p>Function getSkill commented in full on 260820.
     *
     * @param skill the skill to read
     * @return the class's flat adjustment for that skill
     */
    public int getSkill(PlayerSkill skill) {
        return classSkills.get(skill);
    }

    /**
     * The player flags this class has — C's {@code class->pflags}, the class abilities named in
     * {@code class.txt}.
     *
     * <p>Unioned onto the race's in the calculated state ({@code player-calcs.c:1919}). Some are
     * conditional on level rather than outright: {@code PF_BRAVERY_30} is carried from the start but
     * only confers fear protection at level 30, and the threshold lives in code rather than data
     * ({@code player.c:294-297}).
     *
     * <p>Function getpFlags commented in full on 260820.
     *
     * @return a read-only view of the class's player flags
     */
    public FlagView<PlayerFlag> getpFlags() {
        return pFlags;
    }

    /**
     * The class's adjustment to one stat — C's {@code class->c_adj[stat]}. Added to the race's
     * before the pair is applied through {@code modify_stat_value}
     * ({@code player-calcs.c:2058-2061}).
     *
     * <p>Function getStatsAdj commented in full on 260820.
     *
     * @param stat the stat to read
     * @return the class's adjustment in points
     */
    public int getStatsAdj(Stats stat) {
        return stats.get(stat);
    }

    /**
     * The weapon weight below which this class gains nothing further — C's
     * {@code class->min_weight}, in tenth-pounds.
     *
     * <p>A floor on the divisor rather than a limit on what may be wielded: {@code calcBlows}
     * divides by {@code max(weapon weight, this)}, so a weapon lighter than this is treated as if it
     * weighed this much and stops earning extra blows ({@code player-calcs.c:1714-1715}). It is what
     * keeps a weightless weapon from dividing by zero, and what stops classes with a high minimum
     * from profiting endlessly by wielding daggers.
     *
     * <p>Function getMinWeight commented in full on 260820.
     *
     * @return the minimum effective weapon weight in tenth-pounds
     */
    public int getMinWeight() {
        return minWeight;
    }

    /**
     * The greatest number of blows per turn this class can reach — C's
     * {@code class->max_attacks}.
     *
     * <p>Caps the table result before extra blows from equipment are added, so a modifier can carry
     * a character past the class ceiling where strength and dexterity alone cannot
     * ({@code player-calcs.c:1728-1732}).
     *
     * <p>Function getMaxAttacks commented in full on 260820.
     *
     * @return the class's maximum blows per turn, unscaled
     */
    public int getMaxAttacks() {
        return maxAttacks;
    }

    /**
     * The distinct magic realms this class draws on — the port of C's {@code class_magic_realms}
     * ({@code player-class.c}).
     *
     * <p>A realm is a property of a spellbook, and a class's books may repeat one or span two, so
     * this collects them and removes the duplicates. C builds a freshly allocated linked list and
     * expects the caller to free it; the port returns a set, which is the same intent expressed by
     * the type.
     *
     * <p>Deduplication is by realm <em>name</em>, not by identity — the map keyed on
     * {@link MagicRealm#getName} is doing that work — which matches C's
     * {@code lookup_realm}, itself a name match.
     *
     * <p>The result feeds {@code averageSpellStat}, which averages the governing stat's table index
     * across the realms; a class with two realms is therefore limited by the mean of two stats.
     * An empty set is returned for a non-caster, and the caller must not divide by its size.
     *
     * <p>Function magicRealm commented in full on 260820.
     *
     * @return the realms this class casts from, without duplicates; empty for a non-caster
     */
    public Set<MagicRealm> magicRealm() {
        if (getMagic().getTotalSpells() == 0)
            return Set.of();

        Set<MagicRealm> result = new HashSet<>();
        Map<String, MagicRealm> test = new HashMap<>();

        for (MagicBook book : getMagic().getMagicBooks()) {
            test.put(book.getRealm().getName(), book.getRealm());
        }

        return new HashSet<>(test.values());
    }

    /**
     * The class's per-level contribution to one skill — C's {@code class->x_skills[skill]}.
     *
     * <p>Applied as {@code value * level / 10} near the end of {@code calcBonuses}
     * ({@code player-calcs.c:2245-2246}), so the stored number is tenths of a point per level and a
     * class earns whole points of skill only every few levels.
     *
     * <p>Function getXSkill commented in full on 260820.
     *
     * @param skill the skill to read
     * @return the class's per-level adjustment, in tenths of a point
     */
    public int getXSkill(PlayerSkill skill) {
        return extraSkills.get(skill);
    }
}
