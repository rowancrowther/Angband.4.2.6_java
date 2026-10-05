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

package uk.co.jackoftradesltd.middle.objects;

import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.Objects;

/**
 * One entry of {@code slay.txt}: a bonus-damage multiplier that a weapon or launcher applies against
 * a class of monster, with the verbs used when it triggers and a power rating for object valuation.
 * This is the Java port of the C original's {@code struct slay} ({@code object.h}).
 *
 * <p>C keeps its slays in one global array and refers to them by index, so "which slay" is an
 * {@code int}. The port holds the registry's {@code Slay} objects and passes them around instead,
 * which is why this class carries {@link #copy()}, {@link #equals} and {@link #hashCode()}, none of
 * which has a C counterpart.
 *
 * <p>A slay picks out its victims by exactly one of two routes: a {@link MonsterRaceFlag}
 * ({@code raceFlag}) or a {@link MonsterBase} ({@code base}); the other is {@code null}. The slay
 * parser enforces this, as C's {@code parse_slay_race_flag} and {@code parse_slay_base} do. No slay
 * in 4.2.6's {@code slay.txt} uses a base.
 *
 * <p>The {@code code} (for example {@code EVIL_2}) is the key that {@code ego_item.txt},
 * {@code object.txt}, {@code artifact.txt} and {@code player_timed.txt} use to name the slay. C stores
 * it as a plain string; the port also splits it into {@code monsterType} and {@code monsterLevel}
 * at construction, which C never does.
 *
 * <p>Class Slay coded before 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
public class Slay {
    /**
     * The slay's code as written in {@code slay.txt}, such as {@code EVIL_2}: the monster type, an
     * underscore, then a number. Other data files name the slay by this string. Ports C's
     * {@code slay->code}.
     *
     * <p>Field code coded before 261005, commented in full on 261005.
     */
    private String code;
    /**
     * The monster type read from the part of {@link #code} before the underscore, as the
     * {@code RF_} flag of that name (so {@code EVIL_2} gives {@code RF_EVIL}). The constructor
     * derives it; C has no such field. It is read only by {@link #equals} and {@link #hashCode()}.
     *
     * <p>Field monsterType coded before 261005, commented in full on 261005.
     */
    private MonsterRaceFlag monsterType;
    /**
     * The number read from the part of {@link #code} after the underscore (so {@code EVIL_2} gives
     * {@code 2}). The constructor derives it; C has no such field. It is read only by
     * {@link #equals} and {@link #hashCode()}.
     *
     * <p>Field monsterLevel coded before 261005, commented in full on 261005.
     */
    private int monsterLevel;
    /**
     * The name of the slain creatures, used in object descriptions (for example
     * {@code evil creatures}). Ports C's {@code slay->name}.
     *
     * <p>Field name coded before 261005, commented in full on 261005.
     */
    private String name;
    /**
     * The monster base this slay affects, or {@code null} when the slay targets a race flag
     * instead. C holds the base's name as a string and compares it with {@code streq}; the port
     * holds the {@link MonsterBase} the registry resolved that name to. Ports C's
     * {@code slay->base}.
     *
     * <p>Field base coded before 261005, commented in full on 261005.
     */
    private MonsterBase base;
    /**
     * The verb used when a susceptible monster is hit in melee (for example {@code smite}). Ports
     * C's {@code slay->melee_verb}.
     *
     * <p>Field meleeVerb coded before 261005, commented in full on 261005.
     */
    private String meleeVerb;
    /**
     * The verb used when a susceptible monster is hit at range (for example {@code pierces}). Ports
     * C's {@code slay->range_verb}.
     *
     * <p>Field rangedVerb coded before 261005, commented in full on 261005.
     */
    private String rangedVerb;
    /**
     * The monster race flag this slay affects, or {@code null} when the slay targets a base
     * instead. C's {@code slay->race_flag} is an {@code int} where zero means "none"; the port uses
     * {@code null} for that. Ports C's {@code slay->race_flag}.
     *
     * <p>Field raceFlag coded before 261005, commented in full on 261005.
     */
    private MonsterRaceFlag raceFlag;
    /**
     * The multiplier applied to the damage dice in the standard combat system. Ports C's
     * {@code slay->multiplier}.
     *
     * <p>Field multiplier coded before 261005, commented in full on 261005.
     */
    private int multiplier;
    /**
     * The multiplier applied to the damage dice in O-combat. Ports C's
     * {@code slay->o_multiplier}.
     *
     * <p>Field oMultiplier coded before 261005, commented in full on 261005.
     */
    private int oMultiplier;
    /**
     * The slay's weighting in the object power calculation, where 100 is neutral. Ports C's
     * {@code slay->power}.
     *
     * <p>Field power coded before 261005, commented in full on 261005.
     */
    private int power;

    /**
     * Builds a slay from the fields the slay parser read out of one {@code slay.txt} record,
     * storing each as given and additionally splitting {@code code} at its underscore into
     * {@code monsterType} and {@code monsterLevel}.
     *
     * <p>The split is the port's own: C stores the code as a string and never takes it apart. For
     * every entry in 4.2.6's {@code slay.txt} ({@code EVIL_2}, {@code ORC_3}, {@code DEMON_5} and so
     * on) the split succeeds. A code with no underscore, a text before the underscore that is not an
     * {@code RF_} flag name, or a non-numeric level throws, which C would never do.
     *
     * <p>Constructor Slay coded before 261005, commented in full on 261005.
     *
     * @param code        slay code (monster type, underscore, number, e.g. {@code EVIL_2})
     * @param name        name of the slain creatures
     * @param base        targeted monster base, or {@code null} if the slay uses a race flag
     * @param meleeVerb   verb used on a melee hit
     * @param rangedVerb  verb used on a ranged hit
     * @param raceFlag    targeted race flag, or {@code null} if the slay uses a base
     * @param multiplier  standard damage multiplier
     * @param oMultiplier O-combat damage multiplier
     * @param power       power rating
     * @throws ArrayIndexOutOfBoundsException if {@code code} contains no underscore
     * @throws IllegalArgumentException       if the text before the underscore is not an
     *                                        {@code RF_} flag name
     * @throws NumberFormatException          if the text after the underscore is not an integer
     */
    public Slay(String code, String name, MonsterBase base, String meleeVerb, String rangedVerb,
                MonsterRaceFlag raceFlag, int multiplier, int oMultiplier, int power) {
        this.code = code;
        String[] splits = this.code.split("_");
        this.monsterType = MonsterRaceFlag.valueOf("RF_" + splits[0]);
        this.monsterLevel = Integer.parseInt(splits[1]);
        this.name = name;
        this.base = base;
        this.meleeVerb = meleeVerb;
        this.rangedVerb = rangedVerb;
        this.raceFlag = raceFlag;
        this.multiplier = multiplier;
        this.oMultiplier = oMultiplier;
        this.power = power;
    }

    /**
     * Returns the slay's code as written in {@code slay.txt}, the string other data files use to
     * name this slay.
     *
     * <p>Function getCode coded before 261005, commented in full on 261005.
     *
     * @return the slay's code, such as {@code EVIL_2}
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the name of the slain creatures, as used in object descriptions.
     *
     * <p>Function getName coded before 261005, commented in full on 261005.
     *
     * @return the slay's name, such as {@code evil creatures}
     */
    public String getName() {
        return name;
    }

    /**
     * Describes the slay for logs and test failure messages by listing the nine constructor fields.
     * {@code monsterType} and {@code monsterLevel}, which the constructor derives from the code, are
     * left out. C has no counterpart.
     *
     * <p>Function toString coded before 261005, commented in full on 261005.
     *
     * @return a string of the form {@code Slay{code='EVIL_2', name='evil creatures', ...}}
     */
    @Override
    public String toString() {
        return "Slay{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", base=" + base +
                ", meleeVerb='" + meleeVerb + '\'' +
                ", rangedVerb='" + rangedVerb + '\'' +
                ", raceFlag=" + raceFlag +
                ", multiplier=" + multiplier +
                ", oMultiplier=" + oMultiplier +
                ", power=" + power +
                '}';
    }

    /**
     * Whether this slay and another kill exactly the same monsters — that is, whether they target
     * the same race flag and the same monster base. Ports C's {@code same_monsters_slain}
     * ({@code src/obj-slays.c}).
     *
     * <p>Note that the name is deliberately not compared. Two slays with different names and
     * different multipliers are "the same" by this test if they pick out the same monsters, which
     * is what makes it the right grouping for runes: the player learns which monsters a weapon is
     * good against, not how good it is against them. This is why slay runes group by this method
     * where brand runes group by name.
     *
     * <p>The base comparison is by identity, which is correct while bases are interned by the
     * registry — and moot in 4.2.6's data, where no slay declares a base at all and every
     * comparison is {@code null} against {@code null}. C compares the base <em>names</em> with
     * {@code streq} and spells out the null cases one by one (both absent is a match, exactly one
     * absent is not); a single {@code ==} on the references covers the same four cases.
     *
     * <p>Function sameMonsterSlain coded before 261005, commented in full on 261005.
     *
     * @param other the slay to compare against
     * @return {@code true} if both slays kill the same monsters
     */
    public boolean sameMonsterSlain(Slay other) {
        if (this.raceFlag != other.raceFlag) return false;
        return this.base == other.base;
    }

    /**
     * Returns the multiplier applied to the damage dice in the standard combat system. Three or
     * less is an ordinary slay; more than three makes it a kill, which C's {@code obj-power.c}
     * counts separately from the ordinary slays when it rates an object. The same figure decides
     * which of two slays on the same monsters wins in C's {@code append_slay}.
     *
     * <p>Function getMultiplier coded before 261005, commented in full on 261005.
     *
     * @return the standard-combat damage multiplier
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Returns this slay's power rating, the weighting {@code slay.txt} gives it in the object power
     * calculation (100 is neutral). {@code ItemObject.slayPower} takes the best of this figure
     * across an object's brands and slays.
     *
     * <p>Function getPower coded before 261005, commented in full on 261005.
     *
     * @return the slay's power rating
     */
    public int getPower() {
        return power;
    }

    /**
     * Builds a second {@code Slay} with the same field values as this one.
     *
     * <p>The copy is a distinct instance that {@link #equals} treats as equal to the original, so a
     * {@code Set<Slay>} holding one will find the other. The {@code base} reference is shared, not
     * copied. The constructor re-parses {@code monsterType} and {@code monsterLevel} from the code;
     * the copy then overwrites both with this slay's own values.
     *
     * <p>Function copy coded before 261003, commented in full on 261003.
     *
     * @return a new slay equal to this one
     */
    public Slay copy() {
        Slay copySlay = new Slay(code, name, base, meleeVerb, rangedVerb, raceFlag,
                multiplier, oMultiplier, power);
        copySlay.monsterType = this.monsterType;
        copySlay.monsterLevel = this.monsterLevel;

        return copySlay;
    }

    /**
     * Value equality over every field of the slay: code, monster type and level, name, base, both
     * verbs, race flag, both multipliers and power. This lets an {@code ItemObject}'s
     * {@code Set<Slay>} treat a {@link #copy()} as the same entry, so {@code addSlay} and
     * {@code removeSlay} work on a copy as on the original.
     *
     * <p>This is deliberately stricter than {@link #sameMonsterSlain}, which is C's grouping and
     * compares only the race flag and the base. Two slays that kill the same monsters at different
     * strengths are "the same" to {@code sameMonsterSlain} and unequal here, as {@code Brand.equals}
     * is stricter than the name match C uses for brand runes. The base is compared with
     * {@code equals}, where {@code sameMonsterSlain} uses {@code ==}. {@code MonsterBase} does not
     * override {@code equals}, so for a base the two come to the same test; in 4.2.6's data no slay
     * declares a base at all.
     *
     * <p>Function equals coded before 261003, commented in full on 261005.
     *
     * @param o the object to compare against
     * @return {@code true} if {@code o} is a slay with identical fields
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Slay slay)) return false;
        return monsterLevel == slay.monsterLevel && getMultiplier() == slay.getMultiplier()
                && oMultiplier == slay.oMultiplier && getPower() == slay.getPower()
                && Objects.equals(getCode(), slay.getCode()) && monsterType == slay.monsterType
                && Objects.equals(getName(), slay.getName()) && Objects.equals(base, slay.base)
                && Objects.equals(meleeVerb, slay.meleeVerb) && Objects.equals(rangedVerb, slay.rangedVerb)
                && raceFlag == slay.raceFlag;
    }

    /**
     * A hash over the same eleven fields {@link #equals} compares, so equal slays hash alike.
     *
     * <p>Function hashCode coded before 261003, commented in full on 261003.
     *
     * @return a hash consistent with {@link #equals}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCode(), monsterType, monsterLevel, getName(), base, meleeVerb, rangedVerb, raceFlag,
                getMultiplier(), oMultiplier, getPower());
    }
}