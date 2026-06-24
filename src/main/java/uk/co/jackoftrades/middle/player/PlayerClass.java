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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;

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
 * @author ClaudeCode
 */
public class PlayerClass {
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
    List<StartItem> startItems;

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
                       int minWeight, int attMultiplier, List<StartItem> startItems,
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
}
