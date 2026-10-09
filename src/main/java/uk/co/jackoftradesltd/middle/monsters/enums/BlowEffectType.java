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

package uk.co.jackoftradesltd.middle.monsters.enums;

import java.util.Arrays;

/**
 * What kind of player attribute protects against a monster blow effect, as named by the
 * {@code effect-type:} directive in {@code blow_effects.txt}.
 * <p>
 * The C original keeps this as a bare {@code char *} ({@code effect_type}) on
 * {@code struct blow_effect}, declared in {@code mon-blows.h}, and compares it with
 * {@code streq} at each use - in {@code mon-lore.c}, function {@code blow_color()}, and in
 * {@code mon-init.c}, function {@code parse_eff_resist()}. Modelling it as an enum lets the port
 * resolve the string once, at load time. The seven constants are exactly the seven spellings
 * the shipped data file uses and the seven that {@code blow_color()} tests for; C has no
 * other values, so there is no {@code BET_NONE} or {@code BET_MAX}.
 * <p>
 * A blow effect that names no {@code effect-type:} has no constant at all: C leaves
 * {@code effect_type} unset, and the port leaves {@code BlowEffect.getEffectType()} null.
 * {@code blow_color()} then falls through to the effect's base lore colour, so callers must
 * treat null as "nothing protects against this".
 * <p>
 * Only {@link #BET_ELEMENT} and {@link #BET_FLAG} carry an accompanying {@code resist:} - the
 * rest identify a protection that is computed rather than named, such as the dexterity
 * check behind {@link #BET_THEFT}. C enforces this in {@code parse_eff_resist()}, which
 * returns {@code PARSE_ERROR_MISSING_BLOW_EFF_TYPE} for a {@code resist:} on any other type.
 * <p>
 * The wire strings are the data file's spellings and are deliberately inconsistent:
 * {@code eat-food} and {@code eat-light} are hyphenated but {@code all_sustains} is
 * underscored, upstream included. They must be matched exactly as written, and the match is
 * case-sensitive.
 * <p>
 * Class BlowEffectType coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public enum BlowEffectType {
    /**
     * Protected against by a resistance to one projection element, named by {@code resist:}
     * ({@code element}). {@code blow_color()} shows the resisted colour when the player's
     * known resistance level for that element is above zero.
     * <p>
     * Constant BET_ELEMENT coded before 261009, commented in full on 261009.
     */
    BET_ELEMENT("element"),
    /**
     * Protected against by one object flag, named by {@code resist:} ({@code flag}).
     * {@code blow_color()} shows the resisted colour when the player's known flags include it.
     * <p>
     * Constant BET_FLAG coded before 261009, commented in full on 261009.
     */
    BET_FLAG("flag"),
    /**
     * A charge-draining blow ({@code drain}). Nothing is named by {@code resist:}; the
     * protection is having nothing to drain. {@code blow_color()} shows the base colour when
     * the pack holds an object that can have charges and has some, and the resisted colour
     * otherwise.
     * <p>
     * Constant BET_DRAIN coded before 261009, commented in full on 261009.
     */
    BET_DRAIN("drain"),
    /**
     * A stealing blow ({@code theft}). The protection is the dexterity-based saving throw:
     * {@code blow_color()} shows the resisted colour when the player's level plus the
     * {@code adj_dex_safe} entry for their known dexterity reaches 100.
     * <p>
     * Constant BET_THEFT coded before 261009, commented in full on 261009.
     */
    BET_THEFT("theft"),
    /**
     * A blow that eats food ({@code eat-food}). The protection is carrying nothing edible:
     * {@code blow_color()} shows the base colour when any pack slot holds food, and the
     * resisted colour otherwise.
     * <p>
     * Constant BET_EAT_FOOD coded before 261009, commented in full on 261009.
     */
    BET_EAT_FOOD("eat-food"),
    /**
     * A blow that drains light fuel ({@code eat-light}). The protection is having nothing to
     * drain: {@code blow_color()} shows the base colour when the light slot holds an object
     * with fuel remaining that is not {@code NO_FUEL}, and the resisted colour otherwise.
     * <p>
     * Constant BET_EAT_LIGHT coded before 261009, commented in full on 261009.
     */
    BET_EAT_LIGHT("eat-light"),
    /**
     * A blow that drains every stat ({@code all_sustains}). The protection is sustaining all
     * five stats: {@code blow_color()} shows the resisted colour only when the player's known
     * flags include all of {@code SUST_STR}, {@code SUST_INT}, {@code SUST_WIS},
     * {@code SUST_DEX} and {@code SUST_CON}.
     * <p>
     * Constant BET_ALL_SUSTAINS coded before 261009, commented in full on 261009.
     */
    BET_ALL_SUSTAINS("all_sustains");

    /**
     * The spelling this type has in {@code blow_effects.txt}, and so the value C stores in
     * {@code effect_type} and compares with {@code streq}.
     * <p>
     * Field type coded before 261009, commented in full on 261009.
     */
    private final String type;

    /**
     * Bind a constant to its data-file spelling.
     * <p>
     * Constructor BlowEffectType coded before 261009, commented in full on 261009.
     *
     * @param type the data-file spelling this constant is known by
     */
    BlowEffectType(String type) {
        this.type = type;
    }

    /**
     * Resolve a data-file {@code effect-type:} value to its constant. This is the port's
     * replacement for the {@code streq} chain in {@code blow_color()}: the string is resolved
     * once, when the effect is assembled, instead of at each use. The match is exact and
     * case-sensitive, as {@code streq} is.
     * <p>
     * An unrecognised value is not an error here. C also stores any {@code effect-type:} string
     * without checking it, and only {@code blow_color()}'s fall-through ever sees it, so the
     * port likewise hands back null and leaves the caller to decide.
     * <p>
     * Function getFromString coded before 261009, commented in full on 261009.
     *
     * @param type the spelling read from the data file
     * @return the matching constant, or {@code null} if none matches - which includes the
     * empty string, since an effect is allowed to name no {@code effect-type:} at all
     */
    public static BlowEffectType getFromString(String type) {
        return Arrays.stream(BlowEffectType.values()).filter(s -> s.getType().equals(type))
                .findFirst().orElse(null);
    }

    /**
     * The spelling a data file uses for this type.
     * <p>
     * Function getType coded before 261009, commented in full on 261009.
     *
     * @return the data-file spelling of this effect type
     */
    public String getType() {
        return type;
    }
}