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

public class ObjectInfo {
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
    // Not final as this is dependent on birth options
    public static Map<IgnoreType, QualityValueEnum> ignoreLevel;

    static {
        ignoreLevel = new HashMap<>();
        for (IgnoreType ignoreType : IgnoreType.values()) {
            ignoreLevel.put(ignoreType, QualityValueEnum.IGNORE_NONE);
        }
    }

    public record QualityMapping(IgnoreType ignoreType, TValue tval, String identifier) {
    }
}
