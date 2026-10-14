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

import uk.co.jackoftradesltd.middle.objects.enums.TValue;

/**
 * A {@link TValue}-to-name pairing used to group and label related object types in lists and
 * menus. This is the Java port of the C original's {@code struct grouper} ({@code z-type.h}), a
 * {@code (tval, name)} pair whose own comment says the member names are "historical".
 *
 * <p>C uses the struct for a handful of static tables, each a run of rows read in order:
 * <ul>
 *   <li>{@code group_item} and {@code group_artifact} in {@code wiz-spoil.c}, which head the
 *       sections of the item and artifact spoiler files;</li>
 *   <li>{@code object_text_order} in {@code ui-knowledge.c}, which orders and names the groups of
 *       the object knowledge menu (the {@code kind_name} function returns its names).</li>
 * </ul>
 * C's {@code tval_names} table in {@code obj-tval.c} also has the type, but Java does not use a
 * {@code Grouper} for it: each {@link TValue} constant carries its own data-file name, so
 * {@code tval_find_idx} and {@code tval_find_name} are methods of {@link TValue}.
 *
 * <p>In C, a row whose name is {@code NULL} continues the group opened by the nearest row above
 * it that has a name (the spoiler tables put {@code TV_SHOT}, {@code TV_ARROW} and {@code TV_BOLT}
 * under one "Ammo" heading, for instance). Java stores {@code null} as given and does no check of
 * its own, so a caller must test {@link #getName()} for {@code null} where C tests for
 * {@code NULL}. C also ends each table with a row whose tval is 0 and loops until it reaches it;
 * a Java list of {@code Grouper} needs no such sentinel row, and {@link TValue#TV_NONE} is tval 0.
 *
 * <p>The class is a plain immutable holder. Nothing constructs or reads a {@code Grouper} yet;
 * the tables above belong to the spoiler writers and the knowledge menu, which are later chapters.
 *
 * <p>Class Grouper coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class Grouper {
    /**
     * The item type this grouper covers, C's {@code tval} member (an {@code int} there, a
     * {@link TValue} here, whose ordinal is the C value). The tables that use it look rows up by
     * comparing this against an object's tval.
     *
     * <p>Field tValue coded before 261014, commented in full on 261014.
     */
    private final TValue tValue;
    /**
     * The group's display name, C's {@code name} member. It may be {@code null}: C writes
     * {@code NULL} for a row that belongs to the group above it and has no heading of its own.
     *
     * <p>Field name coded before 261014, commented in full on 261014.
     */
    private final String name;

    /**
     * Build one row of a grouping table, the Java form of a C initialiser such as
     * {@code { TV_SHOT, "Ammo" }}. Neither argument is checked: {@code name} may be {@code null}
     * to mark a continuation row, as C's {@code NULL} does.
     *
     * <p>Constructor Grouper coded before 261014, commented in full on 261014.
     *
     * @param tValue the item type this row covers
     * @param name   the group heading, or {@code null} if the row continues the group above it
     */
    public Grouper(TValue tValue, String name) {
        this.name = name;
        this.tValue = tValue;
    }

    /**
     * Returns the group name of this row, C's {@code name} member. It is {@code null} for a
     * continuation row (see the class block), where C's {@code NULL} would be.
     *
     * <p>Method getName coded before 261014, commented in full on 261014.
     *
     * @return the group heading, or {@code null} if this row has none of its own
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the item type of this row, C's {@code tval} member. The name is spelt
     * {@code gettValue} to match the {@code tValue} field, as {@link ObjectKind#gettValue()} is.
     *
     * <p>Method gettValue coded before 261014, commented in full on 261014.
     *
     * @return the item type this row covers
     */
    public TValue gettValue() {
        return tValue;
    }
}
