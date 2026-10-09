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

package uk.co.jackoftradesltd.middle.objects.enums;

/**
 * The kinds of item that quality and ego ignoring work by. When you ignore "average
 * Bows" or "all egos of Resistance on Shields", the "Bows" and "Shields" are these.
 * Each constant carries the menu name the ignore menus show for it.
 *
 * <p>This is the Java port of C's {@code ignore_type_t}, which {@code obj-ignore.h}
 * builds from the {@code ITYPE(index, description)} rows of {@code list-ignore-types.h}.
 * The same rows also fill C's {@code quality_choices} table in {@code obj-ignore.c},
 * pairing each value with its name. Here the name is a field on the constant instead, so
 * {@link #getName()} replaces that table.
 *
 * <p>Each constant's {@link #ordinal()} is its C value. C sizes the per-item-kind
 * bitflags ({@code ITYPE_SIZE}) and the {@code quality_choices} and
 * {@code ego_ignore_types} arrays by {@code ITYPE_MAX}, so the declaration order must
 * stay exactly as {@code list-ignore-types.h} has it.
 *
 * <p>This is not the same thing as {@link QualityValueEnum}, which ports C's quality
 * levels ({@code IGNORE_BAD}, {@code IGNORE_GOOD} and so on). An ignore setting pairs one
 * of each: a kind of item from here, and a quality level from there.
 *
 * <p>Class IgnoreType coded before 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
public enum IgnoreType {
    /**
     * Slot zero, a placeholder with an empty name. It is a real C row, the first in
     * {@code list-ignore-types.h}, so it takes up an index in every array sized by
     * {@code ITYPE_MAX}, and the save and load code in {@code save.c} and {@code load.c}
     * writes and reads its entries with the rest. Nothing assigns it to an item: C's
     * {@code ignore_type_of()} returns {@link #ITYPE_MAX}, not this, for an object of no
     * kind. C's {@code ignore_name_for_type()} and the ego ignore menu in
     * {@code ui-options.c} both start after it, so asking C for this value's name gives
     * {@code "unknown"}, not the empty string.
     *
     * <p>Constant ITYPE_NONE coded before 261002, commented in full on 261002.
     */
    ITYPE_NONE(""),
    /**
     * Swords and polearms, apart from the great weapons. C's {@code quality_mapping}
     * table in {@code obj-ignore.c} gives every {@code TV_SWORD} and {@code TV_POLEARM}
     * this type unless {@link #ITYPE_GREAT} claims it first.
     *
     * <p>Constant ITYPE_SHARP coded before 261002, commented in full on 261002.
     */
    ITYPE_SHARP("Sharp Melee Weapons"),

    /**
     * Hafted weapons ({@code TV_HAFTED}), apart from the Mace of Disruption, which is a
     * great weapon.
     *
     * <p>Constant ITYPE_BLUNT coded before 261002, commented in full on 261002.
     */
    ITYPE_BLUNT("Blunt Melee Weapons"),

    /**
     * The three great weapons: the Blade of Chaos, the Scythe of Slicing and the Mace of
     * Disruption. C finds them by searching the kind name for {@code "Chaos"},
     * {@code "Slicing"} and {@code "Disruption"}, and lists those rows before the plain
     * sword, polearm and hafted rows so they match first.
     *
     * <p>Constant ITYPE_GREAT coded before 261002, commented in full on 261002.
     */
    ITYPE_GREAT("Great Weapons"),

    /**
     * Launchers ({@code TV_BOW}) whose kind name contains {@code "Sling"}.
     *
     * <p>Constant ITYPE_SLING coded before 261002, commented in full on 261002.
     */
    ITYPE_SLING("Slings"),

    /**
     * Launchers ({@code TV_BOW}) whose kind name contains {@code "Bow"}: the Short Bow
     * and the Long Bow. C checks this row before the crossbow row, but C's
     * {@code strstr()} is case-sensitive and {@code "Crossbow"} has a lower-case
     * {@code b}, so the crossbows fall through to {@link #ITYPE_CROSSBOW}.
     *
     * <p>Constant ITYPE_BOW coded before 261002, commented in full on 261002.
     */
    ITYPE_BOW("Bows"),

    /**
     * Launchers ({@code TV_BOW}) whose kind name contains {@code "Crossbow"}.
     *
     * <p>Constant ITYPE_CROSSBOW coded before 261002, commented in full on 261002.
     */
    ITYPE_CROSSBOW("Crossbows"),

    /**
     * Sling ammunition, every {@code TV_SHOT}.
     *
     * <p>Constant ITYPE_SHOT coded before 261002, commented in full on 261002.
     */
    ITYPE_SHOT("Shots and Pebbles"),

    /**
     * Bow ammunition, every {@code TV_ARROW}.
     *
     * <p>Constant ITYPE_ARROW coded before 261002, commented in full on 261002.
     */
    ITYPE_ARROW("Arrows"),

    /**
     * Crossbow ammunition, every {@code TV_BOLT}.
     *
     * <p>Constant ITYPE_BOLT coded before 261002, commented in full on 261002.
     */
    ITYPE_BOLT("Bolts"),

    /**
     * Soft body armour ({@code TV_SOFT_ARMOR}) whose kind name contains {@code "Robe"}.
     *
     * <p>Constant ITYPE_ROBE coded before 261002, commented in full on 261002.
     */
    ITYPE_ROBE("Robes"),

    /**
     * All hard body armour ({@code TV_HARD_ARMOR}), and soft body armour that is not a
     * robe.
     *
     * <p>Constant ITYPE_BODY_ARMOR coded before 261002, commented in full on 261002.
     */
    ITYPE_BODY_ARMOR("Body Armor"),

    /**
     * Dragon armour ({@code TV_DRAG_ARMOR}) of the five basic colours: Black, Blue, White,
     * Red and Green.
     *
     * <p>Constant ITYPE_BASIC_DRAGON_ARMOR coded before 261002, commented in full on 261002.
     */
    ITYPE_BASIC_DRAGON_ARMOR("Basic Dragon Scale Mail"),

    /**
     * Multi-Hued dragon armour, matched on {@code "Multi"} in the kind name.
     *
     * <p>Constant ITYPE_MULTI_DRAGON_ARMOR coded before 261002, commented in full on 261002.
     */
    ITYPE_MULTI_DRAGON_ARMOR("Multi-Hued Dragon Scale Mail"),

    /**
     * The higher dragon armours: Shining, Law, Gold and Chaos.
     *
     * <p>Constant ITYPE_HIGH_DRAGON_ARMOR coded before 261002, commented in full on 261002.
     */
    ITYPE_HIGH_DRAGON_ARMOR("High Dragon Scale Mail"),

    /**
     * Balance dragon armour, matched on {@code "Balance"} in the kind name.
     *
     * <p>Constant ITYPE_BALANCE_DRAGON_ARMOR coded before 261002, commented in full on 261002.
     */
    ITYPE_BALANCE_DRAGON_ARMOR("Balance Dragon Scale Mail"),

    /**
     * Power dragon armour, matched on {@code "Power"} in the kind name.
     *
     * <p>Constant ITYPE_POWER_DRAGON_ARMOR coded before 261002, commented in full on 261002.
     */
    ITYPE_POWER_DRAGON_ARMOR("Power Dragon Scale Mail"),

    /**
     * Cloaks ({@code TV_CLOAK}) that are not Elven.
     *
     * <p>Constant ITYPE_CLOAK coded before 261002, commented in full on 261002.
     */
    ITYPE_CLOAK("Cloaks"),

    /**
     * Cloaks whose kind name contains {@code "Elven"}. C lists this row before the plain
     * cloak row so that it matches first.
     *
     * <p>Constant ITYPE_ELVEN_CLOAK coded before 261002, commented in full on 261002.
     */
    ITYPE_ELVEN_CLOAK("Elven Cloaks"),

    /**
     * Every shield ({@code TV_SHIELD}).
     *
     * <p>Constant ITYPE_SHIELD coded before 261002, commented in full on 261002.
     */
    ITYPE_SHIELD("Shields"),

    /**
     * Helms and crowns ({@code TV_HELM} and {@code TV_CROWN}).
     *
     * <p>Constant ITYPE_HEADGEAR coded before 261002, commented in full on 261002.
     */
    ITYPE_HEADGEAR("Headgear"),

    /**
     * Gloves and gauntlets ({@code TV_GLOVES}).
     *
     * <p>Constant ITYPE_HANDGEAR coded before 261002, commented in full on 261002.
     */
    ITYPE_HANDGEAR("Handgear"),

    /**
     * Boots ({@code TV_BOOTS}).
     *
     * <p>Constant ITYPE_FEET coded before 261002, commented in full on 261002.
     */
    ITYPE_FEET("Footgear"),

    /**
     * Digging tools ({@code TV_DIGGING}).
     *
     * <p>Constant ITYPE_DIGGER coded before 261002, commented in full on 261002.
     */
    ITYPE_DIGGER("Diggers"),

    /**
     * Rings ({@code TV_RING}).
     *
     * <p>Constant ITYPE_RING coded before 261002, commented in full on 261002.
     */
    ITYPE_RING("Rings"),

    /**
     * Amulets ({@code TV_AMULET}).
     *
     * <p>Constant ITYPE_AMULET coded before 261002, commented in full on 261002.
     */
    ITYPE_AMULET("Amulets"),

    /**
     * Light sources ({@code TV_LIGHT}).
     *
     * <p>Constant ITYPE_LIGHT coded before 261002, commented in full on 261002.
     */
    ITYPE_LIGHT("Lights"),

    /**
     * The count sentinel, C's {@code ITYPE_MAX}. It is not a row of
     * {@code list-ignore-types.h}; {@code obj-ignore.h} adds it after the list, so its
     * ordinal is the number of real types and sizes the C arrays. It also means "no
     * type": C's {@code ignore_type_of()} returns it for an object no row matches, and
     * callers in {@code obj-ignore.c} and {@code ui-object.c} test for it before using a
     * type. C gives it no name; the empty string here is a Java filler.
     *
     * <p>Constant ITYPE_MAX coded before 261002, commented in full on 261002.
     */
    ITYPE_MAX("");

    /**
     * The menu name, the {@code description} column of {@code list-ignore-types.h}, which
     * C keeps in the {@code name} member of its {@code quality_choices} table.
     *
     * <p>Field name coded before 261002, commented in full on 261002.
     */
    private final String name;

    /**
     * Builds a constant with its menu name.
     *
     * <p>Constructor IgnoreType coded before 261002, commented in full on 261002.
     *
     * @param name the menu name, as {@code list-ignore-types.h} gives it
     */
    IgnoreType(String name) {
        this.name = name;
    }

    /**
     * Returns the menu name, such as {@code "Sharp Melee Weapons"}. This reads the
     * {@code quality_choices[type].name} entry directly, which is not quite the same as
     * C's {@code ignore_name_for_type()}. That function skips {@link #ITYPE_NONE} and
     * returns {@code "unknown"} for it and for {@link #ITYPE_MAX}, whereas this returns
     * the empty string for both.
     *
     * <p>Method getName coded before 261002, commented in full on 261002.
     *
     * @return the menu name; empty for {@link #ITYPE_NONE} and {@link #ITYPE_MAX}
     */
    public String getName() {
        return name;
    }
}
