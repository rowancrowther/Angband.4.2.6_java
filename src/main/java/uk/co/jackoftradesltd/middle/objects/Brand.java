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

import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.Objects;

/**
 * A weapon/ammo brand (as loaded from {@code brand.txt}) — extra elemental damage
 * a weapon deals, with the damage verb, the monster flags that resist or are
 * vulnerable to it, the damage multipliers (standard and O-combat) and a power
 * rating. This is the Java port of the C original's {@code struct brand}
 * ({@code src/object.h}).
 *
 * <p>All eight data fields of {@code struct brand} are carried over one for one ({@code code},
 * {@code name}, {@code verb}, {@code resist_flag}, {@code vuln_flag}, {@code multiplier},
 * {@code o_multiplier}, {@code power}). The C {@code next} pointer is dropped: it only chains the
 * records while {@code finish_parse_brand} counts them and copies them into the global
 * {@code brands[]} array, and the port's list ({@code ObjectRegistry}) replaces both.
 * {@code resist_flag} and {@code vuln_flag} are C {@code int}s indexing the monster race flags;
 * here they are {@link MonsterRaceFlag} constants, and a brand with no {@code vuln-flag:} line
 * carries {@link MonsterRaceFlag#RF_NONE}, as C's zero-filled record carries flag 0.
 *
 * <p>The class is an immutable value: fields are set once by the constructor and never written
 * again, which is what lets {@link #copy()} be a plain field-by-field copy.
 *
 * <p>Class Brand coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class Brand {
    /**
     * The brand's code (used for cross-references): the {@code code:} line of {@code brand.txt},
     * such as {@code ACID_3}, which {@code artifact.txt}, {@code ego_item.txt}, {@code object.txt}
     * and {@code player_timed.txt} use to name the brand. Counterpart of C's {@code brand->code}.
     *
     * <p>Field code coded before 261009, commented in full on 261009.
     */
    private String code;
    /**
     * The brand's display name: the {@code name:} line, a word for the element such as
     * {@code acid} or {@code lightning}. It deliberately differs from the code, and is what the
     * rune and knowledge code compare when grouping brands of one element at different strengths.
     * Counterpart of C's {@code brand->name}.
     *
     * <p>Field name coded before 261009, commented in full on 261009.
     */
    private String name;
    /**
     * The verb describing the brand's damage (e.g. "burns"): the {@code verb:} line, used in the
     * hit message when a susceptible monster is struck. Counterpart of C's {@code brand->verb}.
     *
     * <p>Field verb coded before 261009, commented in full on 261009.
     */
    private String verb;
    /**
     * Monster flag granting resistance to this brand: the {@code resist-flag:} line, a monster
     * race flag such as {@code RF_IM_ACID}. Counterpart of C's {@code brand->resist_flag}.
     *
     * <p>Field resistFlag coded before 261009, commented in full on 261009.
     */
    private MonsterRaceFlag resistFlag;
    /**
     * Monster flag marking vulnerability to this brand: the optional {@code vuln-flag:} line.
     * {@link MonsterRaceFlag#RF_NONE} when the record has none. Counterpart of C's
     * {@code brand->vuln_flag}.
     *
     * <p>Field vulnerableFlag coded before 261009, commented in full on 261009.
     */
    private MonsterRaceFlag vulnerableFlag;

    /**
     * Damage multiplier in the standard combat system: the {@code multiplier:} line, applied to the
     * weapon's damage dice. Counterpart of C's {@code brand->multiplier}.
     *
     * <p>Field multiplier coded before 261009, commented in full on 261009.
     */
    private int multiplier;
    /**
     * Damage multiplier in the O-combat system: the {@code o-multiplier:} line. Counterpart of C's
     * {@code brand->o_multiplier}.
     *
     * <p>Field oMultiplier coded before 261009, commented in full on 261009.
     */
    private int oMultiplier;
    /**
     * The brand's power rating (for item valuation): the {@code power:} line, a weighting in the
     * object power calculation where 100 is neutral. Counterpart of C's {@code brand->power}.
     *
     * <p>Field power coded before 261009, commented in full on 261009.
     */
    private int power;

    /**
     * Build a brand from its parsed data-file fields.
     *
     * <p>C builds the record field by field across the {@code parse_brand_*} handlers in
     * {@code obj-init.c}, into a zero-filled {@code struct brand}; {@code BrandAssembler} gathers
     * those handlers and calls this once per record, passing {@link MonsterRaceFlag#RF_NONE} for a
     * missing vulnerability flag. No validation happens here — an unresolved flag or number never
     * reaches the constructor, because the assembler skips the record.
     *
     * <p>Constructor Brand coded before 261009, commented in full on 261009.
     *
     * @param code           brand code
     * @param name           display name
     * @param verb           damage verb
     * @param resistFlag     resisting monster flag
     * @param vulnerableFlag vulnerable monster flag
     * @param multiplier     standard damage multiplier
     * @param oMultiplier    O-combat damage multiplier
     * @param power          power rating
     */
    public Brand(String code, String name, String verb, MonsterRaceFlag resistFlag, MonsterRaceFlag vulnerableFlag, int multiplier, int oMultiplier, int power) {
        this.code = code;
        this.name = name;
        this.verb = verb;
        this.resistFlag = resistFlag;
        this.vulnerableFlag = vulnerableFlag;
        this.multiplier = multiplier;
        this.oMultiplier = oMultiplier;
        this.power = power;
    }

    /**
     * Gives the brand's display name, the element word from {@code brand.txt}'s {@code name:} line.
     * Rune grouping and the knowledge fan-out compare brands by this, not by {@link #getCode()}.
     *
     * <p>Function getName coded before 261009, commented in full on 261009.
     *
     * @return the brand's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Gives the brand's code, the key the other data files use to refer to it. {@code
     * ObjectRegistry}'s code lookup matches a requested name against this.
     *
     * <p>Function getCode coded before 261009, commented in full on 261009.
     *
     * @return the brand's code
     */
    public String getCode() {
        return code;
    }

    /**
     * Renders every field, for logs and test failure messages. C has no counterpart.
     *
     * <p>Function toString coded before 261009, commented in full on 261009.
     *
     * @return a debug string listing this brand's fields
     */
    @Override
    public String toString() {
        return "Brand{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", verb='" + verb + '\'' +
                ", resistFlag=" + resistFlag +
                ", vulnerableFlag=" + vulnerableFlag +
                ", multiplier=" + multiplier +
                ", oMultiplier=" + oMultiplier +
                ", power=" + power +
                '}';
    }

    /**
     * Value equality over every field. Note this is stricter than the test used to group brands
     * into runes, which compares names alone — two brands can be the same property at different
     * strengths, and so share a rune, while remaining unequal here. C has no counterpart: its
     * brands are compared by index into {@code brands[]}, so identity is the array slot.
     *
     * <p>Function equals coded before 261009, commented in full on 261009.
     *
     * @param o the object to compare against
     * @return {@code true} if {@code o} is a brand with identical fields
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Brand brand)) return false;
        return multiplier == brand.multiplier
                && oMultiplier == brand.oMultiplier
                && power == brand.power
                && Objects.equals(code, brand.code)
                && Objects.equals(name, brand.name)
                && Objects.equals(verb, brand.verb)
                && resistFlag == brand.resistFlag
                && vulnerableFlag == brand.vulnerableFlag;
    }

    /**
     * Hashes the same eight fields {@link #equals} compares, so equal brands hash alike and a
     * {@code Set<Brand>} (as {@link ItemObject#getBrands()} holds) treats a {@link #copy()} as
     * the same member.
     *
     * <p>Function hashCode coded before 261009, commented in full on 261009.
     *
     * @return a hash consistent with {@link #equals}
     */
    @Override
    public int hashCode() {
        return Objects.hash(code, name, verb, resistFlag, vulnerableFlag, multiplier, oMultiplier, power);
    }

    /**
     * Gives the power rating from {@code brand.txt}'s {@code power:} line (100 is neutral). The
     * object power calculation, {@code ItemObject.slayPower}, takes the best figure across an
     * object's brands and slays. It is not the strength test used when two brands of one name
     * compete — that is {@link #getMultiplier()}.
     *
     * <p>Function getPower coded before 261009, commented in full on 261009.
     *
     * @return this brand's power rating
     */
    public int getPower() {
        return power;
    }

    /**
     * Gives the standard-combat damage multiplier from {@code brand.txt}'s {@code multiplier:}
     * line. The O-combat figure is a separate field with no getter yet, as nothing in the port
     * reads it. {@code ObjectUtils.copyBrands} and {@code ItemObject} compare this to decide which
     * of two same-named brands is stronger.
     *
     * <p>Function getMultiplier coded before 261009, commented in full on 261009.
     *
     * @return the damage multiplier in the standard combat system
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Returns a value-equal, reference-distinct copy of this brand.
     *
     * <p>C has nothing to port here: {@code obj->brands} is a {@code bool} array indexed into the
     * fixed global {@code brands[]} table ({@code obj-slays.c}'s {@code copy_brands}), so C never
     * duplicates a {@code struct brand} — it only turns a bit on. This port stores the brand itself
     * in each object's {@link ItemObject#getBrands() brand set}, so {@code
     * ObjectUtils.copyBrands} needs an actual instance to put in the destination set when a
     * stronger-multiplier brand of the same name displaces a weaker one there, and calls this
     * rather than sharing the source's reference.
     *
     * <p>Every field is a primitive, a {@code String}, or an enum constant, all immutable, so
     * copying each by value is exactly as deep as copying needs to be.
     *
     * <p>Function copy coded on 260904, commented in full on 261009.
     *
     * @return a new brand with the same code, name, verb, flags, multipliers and power as this one
     */
    public Brand copy() {
        return new Brand(this.code, this.name, this.verb, this.resistFlag, this.vulnerableFlag,
                this.multiplier, this.oMultiplier, this.power);
    }
}