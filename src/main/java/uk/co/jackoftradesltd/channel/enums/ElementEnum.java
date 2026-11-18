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

package uk.co.jackoftradesltd.channel.enums;

import java.util.Arrays;

/**
 * The damage elements as referenced by object code (resistances, brands, slays).
 * Mirrors the C original's {@code ELEM_*} list; the constants are self-describing
 * and documented collectively here. {@code ELEM_MAX} is the count sentinel.
 *
 * <p>The declaration order matches {@code src/list-elements.h} exactly, and must: C's projection
 * parser checks each of the first {@code ELEM_MAX} entries of {@code projection.txt} against the
 * element of the same position and refuses to load if they disagree, so the two files are locked
 * together. {@link #getProjectionEnum} relies on the same correspondence, by name rather than by
 * position.
 *
 * <p>Unlike C, this enum carries an {@code ELEM_NONE} zero placeholder, so an element's
 * {@link #ordinal()} is one greater than its C value. Nothing in the port depends on the numeric
 * value — runes and properties hold the constant itself — but the difference matters when reading
 * the original alongside this.
 *
 * <p>The same placeholders are visible to {@link #lookup(String)}, which finds an element by its
 * name with or without the {@code ELEM_} prefix: C's bare name ({@code "FIRE"}) and the whole
 * constant name ({@code "ELEM_FIRE"}) find the same constant. C's name list holds only the real
 * elements, so {@link #ELEM_NONE} and {@link #ELEM_MAX} are answers that lookup can give and C's
 * search cannot; a caller that parses data files must refuse them itself.
 *
 * <p>Each constant is built with three flags, in the order {@code isBase}, {@code isHigh},
 * {@code hasResistRune}. The four base elements (acid to cold) are base and resistable; poison to
 * disenchantment are "high" and resistable; water to arrow are none of the three, and so are the
 * two placeholders. The flags stand in for the ordinal ranges C delimits in {@code src/object.h}
 * with {@code ELEM_BASE_MIN}/{@code ELEM_BASE_MAX} and {@code ELEM_HIGH_MIN}/{@code ELEM_HIGH_MAX}.
 *
 * <p>Enum ElementEnum coded before 260817, commented in full on 261115.
 *
 * @author Rowan Crowther
 */
public enum ElementEnum {
    ELEM_NONE(false, false, false),
    ELEM_ACID(true, false, true),
    ELEM_ELEC(true, false, true),
    ELEM_FIRE(true, false, true),
    ELEM_COLD(true, false, true),
    ELEM_POIS(false, true, true),
    ELEM_LIGHT(false, true, true),
    ELEM_DARK(false, true, true),
    ELEM_SOUND(false, true, true),
    ELEM_SHARD(false, true, true),
    ELEM_NEXUS(false, true, true),
    ELEM_NETHER(false, true, true),
    ELEM_CHAOS(false, true, true),
    ELEM_DISEN(false, true, true),
    ELEM_WATER(false, false, false),
    ELEM_ICE(false, false, false),
    ELEM_GRAVITY(false, false, false),
    ELEM_INERTIA(false, false, false),
    ELEM_FORCE(false, false, false),
    ELEM_TIME(false, false, false),
    ELEM_PLASMA(false, false, false),
    ELEM_METEOR(false, false, false),
    ELEM_MISSILE(false, false, false),
    ELEM_MANA(false, false, false),
    ELEM_HOLY_ORB(false, false, false),
    ELEM_ARROW(false, false, false),
    ELEM_MAX(false, false, false);

    /**
     * Whether this is a "base" element — the four physical damage types (acid, electricity, fire,
     * cold) that objects can ignore/be affected by as a group. Ports the ordinal range C fixes with
     * {@code ELEM_BASE_MIN}/{@code ELEM_BASE_MAX} ({@code src/object.h}) into a per-constant flag;
     * used e.g. when a dungeon spellbook is set to ignore every base element.
     *
     * <p>Exposed via {@link #isBase()}.
     *
     * <p>Field isBase coded before 260817, commented in full on 261107.
     */
    private final boolean isBase;

    /**
     * Whether this is a "high" element — one of the resistable elements beyond the four base
     * physical types, running from poison to disenchantment. Ports the stretch C delimits with
     * {@code ELEM_HIGH_MIN} and {@code ELEM_HIGH_MAX} ({@code src/object.h}), which it expresses
     * as an ordinal range because the constants are ordered to make that work.
     *
     * <p>Recorded per constant rather than derived from {@link #ordinal()} for the same reason as
     * {@link #hasResistRune}: it keeps the port from depending on declaration order. Note that
     * "high" and "resistable" are not the same question — {@link #hasResistRune} is true for the
     * base elements as well — which is why both are carried.
     *
     * <p>Exposed via {@link #isHigh()}.
     *
     * <p>Field isHigh coded before 260817, commented in full on 260915.
     */
    private final boolean isHigh;

    /**
     * Whether objects can resist this element, and so whether it has a resistance rune. True for
     * the base elements and the "high" elements up to and including disenchantment; false for the
     * remainder, which are damage types used by spells and monster attacks but which nothing grants
     * resistance to.
     *
     * <p>Ports C's {@code ELEM_HIGH_MAX} bound, which relies on the {@code ELEM_*} constants being
     * ordered so that everything resistable comes first. Making it a per-constant flag rather than
     * an ordinal comparison keeps this port independent of that ordering.
     *
     * <p>Exposed via {@link #isHasResistRune()}.
     *
     * <p>Field hasResistRune coded before 260817, commented in full on 261107.
     */
    private final boolean hasResistRune;

    /**
     * Builds one element constant from its three classification flags. The flags are supplied per
     * constant rather than computed from {@link #ordinal()}, so that they hold whatever the
     * declaration order, in place of the ordinal ranges C delimits in {@code src/object.h}. The
     * values are never changed after construction.
     *
     * <p>Constructor ElementEnum coded before 260817, commented in full on 261107.
     *
     * @param isBase       whether this element is one of the four base (physical) damage types
     * @param isHigh        whether this element is one of the resistable elements above the base four
     * @param hasResistRune whether objects can resist this element
     */
    ElementEnum(boolean isBase, boolean isHigh, boolean hasResistRune) {
        this.isBase = isBase;
        this.isHigh = isHigh;
        this.hasResistRune = hasResistRune;
    }

    /**
     * Whether this is a "base" element — one of the four physical damage types (acid,
     * electricity, fire, cold) that objects can ignore or be affected by as a group, such as a
     * dungeon spellbook set to ignore every base element in one flag check. Ports the ordinal
     * range C fixes with {@code ELEM_BASE_MIN}/{@code ELEM_BASE_MAX} ({@code src/object.h}) into
     * a per-constant flag, so the port does not depend on {@link #ordinal()} to answer the
     * question.
     *
     * <p>Method isBase coded before 260817, commented in full on 260915.
     *
     * @return {@code true} if this is a base (physical) element
     */
    public boolean isBase() {
        return isBase;
    }

    /**
     * Whether this is a "high" element — one of the resistable elements beyond the four base
     * physical types, running from poison to disenchantment. Ports the stretch C delimits with
     * {@code ELEM_HIGH_MIN} and {@code ELEM_HIGH_MAX} ({@code src/object.h}), which it expresses
     * as an ordinal range because the constants are ordered to make that work.
     *
     * <p>"High" and "resistable" are not the same question — {@link #isHasResistRune()} is true
     * for the base elements as well — which is why both are exposed.
     *
     * <p>Method isHigh coded on 260915, commented in full on 260915.
     *
     * @return {@code true} if this is one of the resistable elements above the base four
     */
    public boolean isHigh() {
        return isHigh;
    }

    /**
     * Whether objects can resist this element, and so whether it has a corresponding resistance
     * rune. True for the four base elements and the "high" elements up to and including
     * disenchantment ({@link #ELEM_ACID} through {@link #ELEM_DISEN}); false for the remainder,
     * which are damage types used only by spells and monster attacks and which nothing in the
     * game grants resistance to.
     *
     * <p>Ports C's {@code ELEM_HIGH_MAX} bound ({@code src/object.h}), which relies on the
     * {@code ELEM_*} constants being ordered so that everything resistable comes first
     * (the {@code for (i = 0; i < ELEM_HIGH_MAX; i++)} loops in {@code obj-knowledge.c}).
     * Recording it as a per-constant flag rather than an ordinal comparison keeps this port
     * independent of that ordering.
     *
     * <p>Method isHasResistRune coded before 260817, commented in full on 260915.
     *
     * @return {@code true} if objects can resist this element, and so if it has a resistance rune
     */
    public boolean isHasResistRune() {
        return hasResistRune;
    }

    /**
     * Finds the projection this element is dealt as — {@link #ELEM_FIRE} gives
     * {@link ProjectionEnum#PROJ_FIRE}, and so on. Every element has one: C's projection parser
     * refuses to load {@code projection.txt} unless its leading entries match the element list
     * position for position, so the elements are simply the first stretch of the projection list.
     *
     * <p>C exploits that alignment directly, subscripting its projection array with an element's
     * value. Here the correspondence is resolved by name instead, which keeps the port clear of
     * both enums' {@link #ordinal()}s — each carries a {@code NONE} placeholder that C lacks, so
     * neither set of ordinals matches the original's numbering anyway.
     *
     * <p>Lives here rather than on {@link ProjectionEnum} because the question it answers is
     * "how is this element delivered?", which is a property of the element, not of the
     * projection it resolves to.
     *
     * <p>The placeholders resolve too: {@link #ELEM_NONE} gives {@link ProjectionEnum#PROJ_NONE}
     * and {@link #ELEM_MAX} gives {@link ProjectionEnum#PROJ_MAX}, since both enums carry them.
     *
     * <p>Method getProjectionEnum coded before 261107, commented in full on 261107.
     *
     * @return the matching projection, or {@code null} if no constant of that name exists, which
     * means the two enums have drifted apart
     */
    public ProjectionEnum getProjectionEnum() {
        try {
            return ProjectionEnum.valueOf("PROJ_" + name().substring(5));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Finds the element named by {@code flag}, with or without the {@code ELEM_} prefix. The
     * steps are:
     * <ol>
     *   <li>if {@code flag} does not start with {@code ELEM_}, that prefix is put on the front;</li>
     *   <li>the constant whose name is exactly the result is searched for and returned;</li>
     *   <li>if there is no such constant, {@code null} is returned.</li>
     * </ol>
     * So {@code "FIRE"} and {@code "ELEM_FIRE"} both give {@link #ELEM_FIRE}, and
     * {@code "HOLY_ORB"} and {@code "ELEM_HOLY_ORB"} both give {@link #ELEM_HOLY_ORB}.
     *
     * <p>Stands in for the {@code streq} search of {@code element_names} in {@code obj-init.c},
     * function {@code grab_element_flag()}. C's names have no prefix, so the bare form is the one
     * C finds; accepting the whole constant name as well is a deliberate widening, because the
     * callers ({@code ParseUtils.grabElementFlag}, {@code ItemObjectAssembler}) hold the prefixed
     * name. The comparison is the same {@code streq} one after the prefix is settled: exact and
     * case-sensitive, so {@code "ELEM_fire"}, {@code "fire"} and {@code "elem_fire"} all find
     * nothing. The prefix test is case-sensitive too, so {@code "elem_fire"} is treated as a bare
     * name and searched for as {@code "ELEM_elem_fire"}.
     *
     * <p>Unlike C's name list, the search can return the placeholders {@link #ELEM_NONE} and
     * {@link #ELEM_MAX}, because the constants exist here; {@code lookup("NONE")} and
     * {@code lookup("ELEM_NONE")} both give {@link #ELEM_NONE}. The caller must refuse them, as
     * {@code ParseUtils.grabElementFlag} does.
     *
     * <p>No trimming, suffixes or partial names are accepted, so the same inputs that find nothing
     * in C find nothing here: the empty string, a lower-case or mixed-case name, and a name with
     * leading or trailing space. A doubled prefix ({@code "ELEM_ELEM_FIRE"}) finds nothing, since
     * it already starts with {@code ELEM_} and is searched for as given. A {@code null} argument
     * also gives {@code null}; C never reaches the search with a null name. Each name is unique, so
     * the order of the constants does not affect the answer.
     *
     * <p>Method lookup coded before 261105, commented in full on 261115, rewritten for the optional
     * prefix on 261115.
     *
     * @param flag the element name, with or without the {@code ELEM_} prefix
     * @return the matching constant, or {@code null} if there is none
     */
    public static ElementEnum lookup(String flag) {
        if (flag == null) return null;
        
        String search;
        if (!flag.startsWith("ELEM_"))
            search = "ELEM_" + flag;
        else
            search = flag;
        
        return Arrays.stream(ElementEnum.values()).filter(e -> e.name().equals(search))
                .findFirst().orElse(null);
    }
    
    /**
     * Finds the element named by {@code element}, with or without the {@code ELEM_} prefix, and
     * lets the caller choose whether the comparison ignores case. The steps are:
     * <ol>
     *   <li>a {@code null} {@code element} gives {@code null}, whichever way {@code ignoreCase} is
     *       set;</li>
     *   <li>if {@code ignoreCase} is {@code false}, the answer is exactly that of
     *       {@link #lookup(String)}: exact and case-sensitive;</li>
     *   <li>if it is {@code true}, {@code ELEM_} is put on the front of a name that does not already
     *       start with it, and the constant whose name equals the result ignoring case is returned,
     *       or {@code null} if there is none.</li>
     * </ol>
     * So with {@code ignoreCase} true, {@code "FIRE"}, {@code "fire"}, {@code "Fire"},
     * {@code "ELEM_FIRE"} and {@code "ELEM_fire"} all give {@link #ELEM_FIRE}, and
     * {@code "holy_orb"} gives {@link #ELEM_HOLY_ORB}.
     *
     * <p>There are two forms because C compares element names in two different ways. The exact
     * form stands in for the {@code streq} search of {@code element_names} in {@code obj-init.c},
     * function {@code grab_element_flag()}, which the object data files go through. The ignore-case
     * form stands in for {@code proj_name_to_idx()} in {@code project.c}, which compares with
     * {@code my_stricmp} against {@code proj_name_list}. That list begins with the elements of
     * {@code list-elements.h}, so for an element name the position it returns is the element's. It
     * is the comparison behind the blow effect {@code resist:} line ({@code parse_eff_resist()} in
     * {@code mon-init.c}), and {@code BlowEffect2Assembler} is the caller that asks for it. The
     * object callers must keep using {@link #lookup(String)}, because C refuses a lower-case
     * element there.
     *
     * <p>The prefix test is case-sensitive even when the comparison is not. {@code "ELEM_fire"}
     * already starts with {@code ELEM_} and is compared as given, so it finds
     * {@link #ELEM_FIRE}. {@code "elem_fire"} and {@code "Elem_Fire"} do not start with it, so
     * they are searched for as {@code "ELEM_elem_fire"} and {@code "ELEM_Elem_Fire"} and find
     * nothing. C's names carry no prefix at all, so it finds nothing for either.
     *
     * <p>As with {@link #lookup(String)}, the search can return the placeholders
     * {@link #ELEM_NONE} and {@link #ELEM_MAX}, because the constants exist here:
     * {@code "none"}, {@code "NONE"} and {@code "ELEM_none"} give {@link #ELEM_NONE}, and
     * {@code "max"} gives {@link #ELEM_MAX}. C's list has no {@code NONE} entry, so
     * {@code proj_name_to_idx()} returns -1 for it, and its trailing {@code "MAX"} entry gives the
     * {@code PROJ_MAX} index, which is no element. The caller decides what to do with them;
     * {@code BlowEffect2Assembler} treats both as no element.
     *
     * <p>Only elements are searched. A projection that is not an element, such as
     * {@code "KILL_WALL"}, finds nothing here, where {@code proj_name_to_idx()} finds its index;
     * {@link ProjectionEnum#lookup(String)} is the search for those. Nothing is trimmed and no
     * partial name is accepted, so the empty string, a name with leading or trailing space, a
     * truncated name and a doubled prefix ({@code "ELEM_ELEM_FIRE"}) all find nothing.
     *
     * <p>The case folding is {@link String#equalsIgnoreCase}, which also folds a few non-ASCII
     * letters, where {@code my_stricmp} folds ASCII letters only. The data files are ASCII, so
     * this is the same accepted deviation as on {@code MessageType.lookupByName} (decided
     * 261023).
     *
     * <p>Method lookup(String, boolean) coded on 261117, commented in full on 261117.
     *
     * @param element    the element name, with or without the {@code ELEM_} prefix
     * @param ignoreCase {@code true} to compare ignoring case, as {@code proj_name_to_idx()} does;
     *                   {@code false} for the exact comparison of {@link #lookup(String)}
     * @return the matching constant, or {@code null} if there is none
     */
    public static ElementEnum lookup(String element, boolean ignoreCase) {
        if (element == null) return null;
        
        if (ignoreCase) {
            String search;
            
            if (!element.startsWith("ELEM_"))
                search = "ELEM_" + element;
            else
                search = element;
            
            return Arrays.stream(ElementEnum.values()).filter(e -> e.name().equalsIgnoreCase(search))
                    .findFirst().orElse(null);
        }
        
        else 
            return lookup(element);
    }
}
