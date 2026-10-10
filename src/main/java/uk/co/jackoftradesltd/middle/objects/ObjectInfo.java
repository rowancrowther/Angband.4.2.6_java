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

import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.QualityValueEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Static tables for the object-ignore machinery, the Java home of the data C keeps at the top of
 * {@code obj-ignore.c}.
 *
 * <p>Despite the name, nothing here comes from {@code obj-info.c}, which describes objects to the
 * player. This class holds two things from {@code obj-ignore.c}: {@link #qualityMapping}, the
 * constant table that sorts a kind of item into an ignore category, and {@link #ignoreLevel}, the
 * player's mutable choice of how much of each category to hide. Each has its own block below, along
 * with {@link QualityMapping}, the row type. C's two name tables, {@code quality_choices} and
 * {@code quality_values}, are not here.
 *
 * <p>Both tables are read by {@code ObjectIgnore}, {@code ItemObject} and {@code EgoItem}, and the
 * class has no behaviour of its own.
 *
 * <p>Class ObjectInfo coded before 261008, commented in full on 261010.
 */
public class ObjectInfo {
    /**
     * Which ignore category each kind of item falls under - the port of C's {@code quality_mapping[]}
     * in {@code obj-ignore.c}.
     *
     * <p>Each row says: an item of this tval whose kind name contains this identifier belongs to this
     * ignore category. An empty identifier matches every kind of the tval. Order matters where one tval
     * has several rows, because C stops at the first match: "Elven" is tried before the catch-all
     * cloak row, and the "Chaos" dragon armour row sits in the high group. C's own comment states the
     * rule: any row with an identifier must appear above the row with the same tval and no identifier.
     *
     * <p>Two readers use it differently. {@code ItemObject.getIgnoreTypeOf} (C's {@code ignore_type_of})
     * takes the first row that fits, so it depends on the order. {@code EgoItem.egoHasIgnoreType}
     * (C's {@code ego_has_ignore_type}) tests every row against the wanted category and so does not.
     *
     * <p>The rows are copied from C in the same order, 38 in all.
     *
     * <p>Field qualityMapping coded before 261008, commented in full on 261010.
     */
    public static final QualityMapping[] qualityMapping = {
            new QualityMapping(IgnoreType.ITYPE_GREAT, TValue.TV_SWORD, "Chaos"),
            new QualityMapping(IgnoreType.ITYPE_GREAT, TValue.TV_POLEARM, "Slicing"),
            new QualityMapping(IgnoreType.ITYPE_GREAT, TValue.TV_HAFTED, "Disruption"),
            new QualityMapping(IgnoreType.ITYPE_SHARP, TValue.TV_SWORD, ""),
            new QualityMapping(IgnoreType.ITYPE_SHARP, TValue.TV_POLEARM, ""),
            new QualityMapping(IgnoreType.ITYPE_BLUNT, TValue.TV_HAFTED, ""),
            new QualityMapping(IgnoreType.ITYPE_SLING, TValue.TV_BOW, "Sling"),
            new QualityMapping(IgnoreType.ITYPE_BOW, TValue.TV_BOW, "Bow"),
            new QualityMapping(IgnoreType.ITYPE_CROSSBOW, TValue.TV_BOW, "Crossbow"),
            new QualityMapping(IgnoreType.ITYPE_SHOT, TValue.TV_SHOT, ""),
            new QualityMapping(IgnoreType.ITYPE_ARROW, TValue.TV_ARROW, ""),
            new QualityMapping(IgnoreType.ITYPE_BOLT, TValue.TV_BOLT, ""),
            new QualityMapping(IgnoreType.ITYPE_ROBE, TValue.TV_SOFT_ARMOR, "Robe"),
            new QualityMapping(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Black"),
            new QualityMapping(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Blue"),
            new QualityMapping(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "White"),
            new QualityMapping(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Red"),
            new QualityMapping(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Green"),
            new QualityMapping(IgnoreType.ITYPE_MULTI_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Multi"),
            new QualityMapping(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Shining"),
            new QualityMapping(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Law"),
            new QualityMapping(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Gold"),
            new QualityMapping(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Chaos"),
            new QualityMapping(IgnoreType.ITYPE_BALANCE_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Balance"),
            new QualityMapping(IgnoreType.ITYPE_POWER_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Power"),
            new QualityMapping(IgnoreType.ITYPE_BODY_ARMOR, TValue.TV_HARD_ARMOR, ""),
            new QualityMapping(IgnoreType.ITYPE_BODY_ARMOR, TValue.TV_SOFT_ARMOR, ""),
            new QualityMapping(IgnoreType.ITYPE_ELVEN_CLOAK, TValue.TV_CLOAK, "Elven"),
            new QualityMapping(IgnoreType.ITYPE_CLOAK, TValue.TV_CLOAK, ""),
            new QualityMapping(IgnoreType.ITYPE_SHIELD, TValue.TV_SHIELD, ""),
            new QualityMapping(IgnoreType.ITYPE_HEADGEAR, TValue.TV_HELM, ""),
            new QualityMapping(IgnoreType.ITYPE_HEADGEAR, TValue.TV_CROWN, ""),
            new QualityMapping(IgnoreType.ITYPE_HANDGEAR, TValue.TV_GLOVES, ""),
            new QualityMapping(IgnoreType.ITYPE_FEET, TValue.TV_BOOTS, ""),
            new QualityMapping(IgnoreType.ITYPE_DIGGER, TValue.TV_DIGGING, ""),
            new QualityMapping(IgnoreType.ITYPE_RING, TValue.TV_RING, ""),
            new QualityMapping(IgnoreType.ITYPE_AMULET, TValue.TV_AMULET, ""),
            new QualityMapping(IgnoreType.ITYPE_LIGHT, TValue.TV_LIGHT, "")
    };
    /**
     * How much of each ignore category the player has chosen to hide - the port of C's
     * {@code ignore_level[ITYPE_MAX]} in {@code obj-ignore.c}.
     *
     * <p>C holds a {@code uint8_t} per {@link IgnoreType} and indexes the array by it; here the map is
     * keyed by the enum and holds {@link QualityValueEnum} constants in place of the bytes. An item in
     * a category is ignored when its quality grade is at or below the category's level, and
     * {@code IGNORE_NONE} hides nothing on quality. Every {@link IgnoreType} is given
     * {@code IGNORE_NONE} at start-up, which is what C's zero-filled array reads as. The
     * {@code ITYPE_MAX} key has no slot in C's array, and {@code ObjectIgnore.ignoreBirthInit} leaves
     * it alone.
     *
     * <p>Read by {@code ObjectIgnore.isIgnored} (C's {@code object_is_ignored}) and reset by
     * {@code ObjectIgnore.ignoreBirthInit}. C also sets it from the ignore menus ({@code ui-options.c}
     * and {@code ui-object.c}) and from the savefile ({@code load.c}).
     *
     * <p>It is not final because it is per-character state rather than a constant table. Nothing in
     * {@code src/main} reassigns it; the tests swap in a copy and restore the original afterwards.
     *
     * <p>Field ignoreLevel coded before 261008, commented in full on 261010.
     */
    public static Map<IgnoreType, QualityValueEnum> ignoreLevel;

    static {
        // Every category starts at IGNORE_NONE, as C's zero-filled array does
        ignoreLevel = new HashMap<>();
        for (IgnoreType ignoreType : IgnoreType.values()) {
            ignoreLevel.put(ignoreType, QualityValueEnum.IGNORE_NONE);
        }
    }

    /**
     * One row of {@link #qualityMapping} - the port of C's {@code quality_ignore_struct}.
     *
     * <p>Declared as a record rather than the C struct's three fields so that the row is immutable and
     * read through accessors. C's {@code ignore_type_t} and {@code int} tval become the two enums, and
     * the {@code const char *} identifier becomes a {@link String}. The identifier is tested with
     * {@link String#contains}, as C uses {@code strstr}, so the match is a case-sensitive substring of
     * the kind's name, and {@code ""} occurs in every name.
     *
     * <p>Record QualityMapping coded before 261008, commented in full on 261010.
     *
     * @param ignoreType the ignore category the row assigns
     * @param tval       the item type the row applies to
     * @param identifier a substring the kind's name must contain; empty to match any kind of the tval
     */
    public record QualityMapping(IgnoreType ignoreType, TValue tval, String identifier) {
    }
}
