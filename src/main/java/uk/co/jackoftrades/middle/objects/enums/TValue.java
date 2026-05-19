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

package uk.co.jackoftrades.middle.objects.enums;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TValue {
    /**
     * A null TValue - not yet assigned
     */
    TV_NULL("none"),

    /**
     * A chest
     */
    TV_CHEST("chest"),

    /**
     * a pebble or iron shot, normal or otherwise, used in a sling
     */
    TV_SHOT("shot"),

    /**
     * A normal or magic arrow, used in a bow
     */
    TV_ARROW("arrow"),

    /**
     * A crossbow bolt, magic or otherwise
     */
    TV_BOLT("bolt"),

    /**
     * A bow
     */
    TV_BOW("bow"),

    /**
     * A type of digging implement, such as shovel or pick
     */
    TV_DIGGING("digger"),

    /**
     * A weapon with a haft such as a halberd
     */
    TV_HAFTED("hafted"),

    /**
     * A polearm
     */
    TV_POLEARM("polearm"),

    /**
     * A sword
     */
    TV_SWORD("sword"),

    /**
     * Boots or sandals
     */
    TV_BOOTS("boots"),

    /**
     * Gloves
     */
    TV_GLOVES("gloves"),

    /**
     * Helm, or hat
     */
    TV_HELM("helm"),

    /**
     * A crown
     */
    TV_CROWN("crown"),

    /**
     * A shield
     */
    TV_SHIELD("shield"),

    /**
     * A cloak, worn on the back
     */
    TV_CLOAK("cloak"),

    /**
     * Soft armor, such as cloth or leather
     */
    TV_SOFT_ARMOR("soft armor"),

    /**
     * Hard armor, such as chain or plate
     */
    TV_HARD_ARMOR("hard armor"),

    /**
     * Amor fashioned from dragon scale
     */
    TV_DRAG_ARMOR("dragon armor"),

    /**
     * A form of light, such as torches or an oil lamp
     */
    TV_LIGHT("light"),

    /**
     * An amulet to wear around your neck
     */
    TV_AMULET("amulet"),

    /**
     * A ring to wear around your finger
     */
    TV_RING("ring"),

    /**
     * A staff, knob available or not
     */
    TV_STAFF("staff"),

    /**
     * A wand to zap
     */
    TV_WAND("wand"),

    /**
     * A rod, different from a wand, honestly
     */
    TV_ROD("rod"),

    /**
     * A scroll to read
     */
    TV_SCROLL("scroll"),

    /**
     * A potion to quaff
     */
    TV_POTION("potion"),

    /**
     * A flask to drink from
     */
    TV_FLASK("flask"),

    /**
     * Food to eat
     */
    TV_FOOD("food"),

    /**
     * Magic mushrooms - no, not that kind
     */
    TV_MUSHROOM("mushroom"),

    /**
     * A spellbook for mages
     */
    TV_MAGIC_BOOK("magic book"),

    /**
     * A spellbook for priests
     */
    TV_PRAYER_BOOK("prayer book"),

    /**
     * A spellbook for druids
     */
    TV_NATURE_BOOK("nature book"),

    /**
     * A spell book of the dark arts
     */
    TV_SHADOW_BOOK("shadow book"),

    /**
     * Another type of spell book
     */
    TV_OTHER_BOOK("other book"),

    /**
     * Gold, gold, gold, gold, Gold, gold, gold, gold (repeat ad nauseam)
     */
    TV_GOLD("gold"),

    /**
     * Marker for the largest TValue (probably not needed)
     */
    TV_MAX("");

    private final String name;

    TValue(String name) {
        this.name = name;
    }

    /**
     * Get the string name of this TValue
     *
     * @return The type name of this TValue
     */
    public String getName() {
        return name;
    }

    public boolean isStaff() {
        return this == TV_STAFF;
    }

    public boolean isWand() {
        return this == TV_WAND;
    }

    public boolean isRod() {
        return this == TV_ROD;
    }

    public boolean isPotion() {
        return this == TV_POTION;
    }

    public boolean isScroll() {
        return this == TV_SCROLL;
    }

    public boolean isFood() {
        return this == TV_FOOD;
    }

    public boolean isMushroom() {
        return this == TV_MUSHROOM;
    }

    public boolean isLight() {
        return this == TV_LIGHT;
    }

    public boolean isRing() {
        return this == TV_RING;
    }

    public boolean isChest() {
        return this == TV_CHEST;
    }

    public boolean isFuel() {
        return this == TV_FLASK;
    }

    public boolean isMoney() {
        return this == TV_GOLD;
    }

    public boolean isDigger() {
        return this == TV_DIGGING;
    }

    public boolean canHaveNourishment() {
        return this == TV_FOOD || this == TV_POTION
                || this == TV_MUSHROOM;
    }

    public boolean canHaveCharges() {
        return this == TV_STAFF || this == TV_WAND;
    }

    public boolean canHaveTimeout() {
        return this == TV_ROD;
    }

    public boolean isBodyArmour() {
        return switch (this) {
            case TV_SOFT_ARMOR, TV_HARD_ARMOR, TV_DRAG_ARMOR -> true;
            default -> false;
        };
    }

    public boolean isHeadArmour() {
        return this == TV_CROWN || this == TV_HELM;
    }

    public boolean isAmmo() {
        return switch (this) {
            case TV_SHOT, TV_ARROW, TV_BOLT -> true;
            default -> false;
        };
    }

    public boolean isSharpMissile() {
        return switch (this) {
            case TV_ARROW, TV_BOLT -> true;
            default -> false;
        };
    }

    public boolean isBolt() {
        return this == TV_BOLT;
    }

    public boolean isLauncher() {
        return this == TV_BOW;
    }

    public boolean isUseable() {
        return switch (this) {
            case TV_ROD, TV_WAND, TV_STAFF, TV_SCROLL, TV_POTION, TV_FOOD, TV_MUSHROOM -> true;
            default -> false;
        };
    }

    public boolean canHaveFailure() {
        return switch (this) {
            case TV_STAFF, TV_WAND, TV_ROD -> true;
            default -> false;
        };
    }

    public boolean isJewelry() {
        return this == TV_RING || this == TV_AMULET;
    }

    public boolean isWeapon() {
        return switch (this) {
            case TV_SWORD, TV_HAFTED, TV_POLEARM, TV_DIGGING, TV_BOW,
                 TV_BOLT, TV_ARROW, TV_SHOT -> true;
            default -> false;
        };
    }

    public boolean isArmour() {
        return switch (this) {
            case TV_DRAG_ARMOR, TV_HARD_ARMOR, TV_SOFT_ARMOR, TV_SHIELD,
                 TV_CLOAK, TV_CROWN, TV_HELM, TV_BOOTS, TV_GLOVES -> true;
            default -> false;
        };
    }

    public boolean isMeleeWeapon() {
        return switch (this) {
            case TV_SWORD, TV_HAFTED, TV_POLEARM, TV_DIGGING -> true;
            default -> false;
        };
    }

    public boolean hasVariablePower() {
        return switch (this) {
            case TV_SHOT, TV_ARROW, TV_BOLT, TV_BOW, TV_DIGGING, TV_HAFTED, TV_POLEARM,
                 TV_SWORD, TV_BOOTS, TV_GLOVES, TV_HELM, TV_CROWN, TV_SHIELD, TV_CLOAK,
                 TV_SOFT_ARMOR, TV_HARD_ARMOR, TV_DRAG_ARMOR, TV_LIGHT, TV_RING, TV_AMULET -> true;
            default -> false;
        };
    }

    public boolean isWearable() {
        return switch (this) {
            case TV_BOW, TV_DIGGING, TV_HAFTED, TV_POLEARM,
                 TV_SWORD, TV_BOOTS, TV_GLOVES, TV_HELM, TV_CROWN, TV_SHIELD, TV_CLOAK,
                 TV_SOFT_ARMOR, TV_HARD_ARMOR, TV_DRAG_ARMOR, TV_LIGHT, TV_RING, TV_AMULET -> true;
            default -> false;
        };
    }

    public boolean isEdible() {
        return this == TV_FOOD || this == TV_MUSHROOM;
    }

    public boolean canHaveFlavour() {
        return switch (this) {
            case TV_AMULET, TV_RING, TV_STAFF, TV_WAND, TV_ROD, TV_POTION, TV_MUSHROOM, TV_SCROLL -> true;
            default -> false;
        };
    }

    public boolean isBook() {
        return switch (this) {
            case TV_MAGIC_BOOK, TV_PRAYER_BOOK, TV_NATURE_BOOK, TV_SHADOW_BOOK, TV_OTHER_BOOK -> true;
            default -> false;
        };
    }

    public boolean isZapper() {
        return this == TV_WAND || this == TV_STAFF;
    }

    /**
     * Value of for the name string as opposed to the name
     *
     * @param name The name that we are searching for
     * @return the TValue of the given name, or null if it wasn't
     * found
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static @Nullable TValue fromName(@NotNull String name) {
        for (TValue value : TValue.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return null;
    }
}