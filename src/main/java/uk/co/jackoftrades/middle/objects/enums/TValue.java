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

package uk.co.jackoftrades.middle.objects.enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.util.ArrayList;
import java.util.List;

/**
 * The item "type value" (tval) of an object — its broad category (chest, weapon,
 * armour, ring, potion, spellbook, …) that drives stacking, shop placement and
 * which slot it fits. Each constant carries its display name. This is the Java
 * port of the C original's {@code TV_*} type values ({@code src/list-tvals.h});
 * the helper predicates classify groups of tvals.
 *
 * @author Rowan Crowther
 */
public enum TValue {
    /**
     * A null TValue - not yet assigned
     */
    TV_NONE("none"),

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
     * Armor fashioned from dragon scale
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
    TV_GOLD("gold");

    /**
     * Shared logger for the lookup helpers below, which warn rather than throw when a data file
     * names a type that does not exist.
     */
    private static final Logger logger = LogManager.getLogger(TValue.class);

    /**
     * The display name of this item type.
     */
    private final String name;
    /**
     * Bind a tval to its display name.
     *
     * @param name the display name
     */
    TValue(String name) {
        this.name = name;
    }

    /**
     * Get the string name of this TValue — the text the data files use for it, which is the
     * {@code string_name} column of C's {@code list-tvals.h} and the column {@code tval_find_idx}
     * matches against. For most tvals it is the identifier lower-cased, but not for
     * {@link #TV_DIGGING} ({@code "digger"}) or {@link #TV_DRAG_ARMOR} ({@code "dragon armor"}).
     *
     * @return The type name of this TValue
     */
    public String getName() {
        return name;
    }

    /**
     * @return whether this tval is a staff
     */
    public boolean isStaff() {
        return this == TV_STAFF;
    }

    /**
     * @return whether this tval is a wand
     */
    public boolean isWand() {
        return this == TV_WAND;
    }

    /**
     * @return whether this tval is a rod
     */
    public boolean isRod() {
        return this == TV_ROD;
    }

    /**
     * @return whether this tval is a potion
     */
    public boolean isPotion() {
        return this == TV_POTION;
    }

    /**
     * @return whether this tval is a scroll
     */
    public boolean isScroll() {
        return this == TV_SCROLL;
    }

    /**
     * @return whether this tval is (non-mushroom) food
     */
    public boolean isFood() {
        return this == TV_FOOD;
    }

    /**
     * @return whether this tval is a mushroom
     */
    public boolean isMushroom() {
        return this == TV_MUSHROOM;
    }

    /**
     * @return whether this tval is a light source
     */
    public boolean isLight() {
        return this == TV_LIGHT;
    }

    /**
     * @return whether this tval is a ring
     */
    public boolean isRing() {
        return this == TV_RING;
    }

    /**
     * @return whether this tval is a chest
     */
    public boolean isChest() {
        return this == TV_CHEST;
    }

    /**
     * @return whether this tval is fuel (a flask of oil)
     */
    public boolean isFuel() {
        return this == TV_FLASK;
    }

    /**
     * @return whether this tval is money (gold)
     */
    public boolean isMoney() {
        return this == TV_GOLD;
    }

    /**
     * @return whether this tval is a digging tool
     */
    public boolean isDigger() {
        return this == TV_DIGGING;
    }

    /**
     * @return whether items of this tval can provide nourishment (food, potions, mushrooms)
     */
    public boolean canHaveNourishment() {
        return this == TV_FOOD || this == TV_POTION
                || this == TV_MUSHROOM;
    }

    /**
     * @return whether items of this tval hold a number of charges (staves and wands)
     */
    public boolean canHaveCharges() {
        return this == TV_STAFF || this == TV_WAND;
    }

    /**
     * @return whether items of this tval recharge on a timeout (rods)
     */
    public boolean canHaveTimeout() {
        return this == TV_ROD;
    }

    /**
     * @return whether this tval is body armour (soft, hard or dragon-scale armour)
     */
    public boolean isBodyArmour() {
        return switch (this) {
            case TV_SOFT_ARMOR, TV_HARD_ARMOR, TV_DRAG_ARMOR -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is head armour (a helm or crown)
     */
    public boolean isHeadArmour() {
        return this == TV_CROWN || this == TV_HELM;
    }

    /**
     * @return whether this tval is ammunition (shots, arrows or bolts)
     */
    public boolean isAmmo() {
        return switch (this) {
            case TV_SHOT, TV_ARROW, TV_BOLT -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is a sharp missile (an arrow or bolt)
     */
    public boolean isSharpMissile() {
        return switch (this) {
            case TV_ARROW, TV_BOLT -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is a crossbow bolt
     */
    public boolean isBolt() {
        return this == TV_BOLT;
    }

    /**
     * @return whether this tval is a missile launcher (a bow, sling or crossbow)
     */
    public boolean isLauncher() {
        return this == TV_BOW;
    }

    /**
     * @return whether items of this tval can be "used" (rods, wands, staves, scrolls, potions, food, mushrooms)
     */
    public boolean isUseable() {
        return switch (this) {
            case TV_ROD, TV_WAND, TV_STAFF, TV_SCROLL, TV_POTION, TV_FOOD, TV_MUSHROOM -> true;
            default -> false;
        };
    }

    /**
     * @return whether use of this tval can fail based on the device skill (staves, wands, rods)
     */
    public boolean canHaveFailure() {
        return switch (this) {
            case TV_STAFF, TV_WAND, TV_ROD -> true;
            default -> false;
        };
    }

    /**
     * Resolves a type's data-file name to its numeric tval - the port of C's {@code tval_find_idx}
     * ({@code obj-util.c}).
     *
     * <p>The number is the enum's ordinal, which is why the constants are declared in C's order and
     * why {@code TV_NONE} sits first: C's tval 0 means "no type", and a port that reordered the
     * constants would quietly change every number the data files rely on.
     *
     * <p>Delegates the name lookup to {@link #fromName(String)}, so an unknown name is handled
     * there rather than here.
     *
     * <p>Function findIndex commented in full on 260827.
     *
     * @param tvalName the type's name as the data files spell it
     * @return the numeric tval for that name
     */
    public static int findIndex(String tvalName) {
        TValue tValue = TValue.fromName(tvalName);
        return tValue.ordinal();
    }

    /**
     * @return whether this tval is a weapon (melee weapon, launcher or ammunition)
     */
    public boolean isWeapon() {
        return switch (this) {
            case TV_SWORD, TV_HAFTED, TV_POLEARM, TV_DIGGING, TV_BOW,
                 TV_BOLT, TV_ARROW, TV_SHOT -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is any kind of armour (body, shield, cloak, headgear, boots or gloves)
     */
    public boolean isArmour() {
        return switch (this) {
            case TV_DRAG_ARMOR, TV_HARD_ARMOR, TV_SOFT_ARMOR, TV_SHIELD,
                 TV_CLOAK, TV_CROWN, TV_HELM, TV_BOOTS, TV_GLOVES -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is a melee weapon (sword, hafted, polearm or digger)
     */
    public boolean isMeleeWeapon() {
        return switch (this) {
            case TV_SWORD, TV_HAFTED, TV_POLEARM, TV_DIGGING -> true;
            default -> false;
        };
    }

    /**
     * @return whether items of this tval have a variable power/pval (weapons, armour, lights and jewelry)
     */
    public boolean hasVariablePower() {
        return switch (this) {
            case TV_SHOT, TV_ARROW, TV_BOLT, TV_BOW, TV_DIGGING, TV_HAFTED, TV_POLEARM,
                 TV_SWORD, TV_BOOTS, TV_GLOVES, TV_HELM, TV_CROWN, TV_SHIELD, TV_CLOAK,
                 TV_SOFT_ARMOR, TV_HARD_ARMOR, TV_DRAG_ARMOR, TV_LIGHT, TV_RING, TV_AMULET -> true;
            default -> false;
        };
    }

    /**
     * @return whether items of this tval can be worn or wielded (weapons, armour, lights and jewelry)
     */
    public boolean isWearable() {
        return switch (this) {
            case TV_BOW, TV_DIGGING, TV_HAFTED, TV_POLEARM,
                 TV_SWORD, TV_BOOTS, TV_GLOVES, TV_HELM, TV_CROWN, TV_SHIELD, TV_CLOAK,
                 TV_SOFT_ARMOR, TV_HARD_ARMOR, TV_DRAG_ARMOR, TV_LIGHT, TV_RING, TV_AMULET -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is edible (food or mushroom)
     */
    public boolean isEdible() {
        return this == TV_FOOD || this == TV_MUSHROOM;
    }

    /**
     * @return whether items of this type get a randomised "flavour" (e.g. an
     * unidentified potion's colour) — amulets, rings, the magic devices,
     * potions, mushrooms and scrolls
     */
    public boolean canHaveFlavour() {
        return switch (this) {
            case TV_AMULET, TV_RING, TV_STAFF, TV_WAND, TV_ROD, TV_POTION, TV_MUSHROOM, TV_SCROLL -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is a spellbook of any realm
     */
    public boolean isBook() {
        return switch (this) {
            case TV_MAGIC_BOOK, TV_PRAYER_BOOK, TV_NATURE_BOOK, TV_SHADOW_BOOK, TV_OTHER_BOOK -> true;
            default -> false;
        };
    }

    /**
     * @return whether this tval is a "zapper" device (wand or staff)
     */
    public boolean isZapper() {
        return this == TV_WAND || this == TV_STAFF;
    }

    /**
     * Resolves a tval from its data-file text — the port of C's {@code tval_find_idx}
     * ({@code obj-tval.c}). Three forms are accepted, tried in C's order:
     *
     * <ol>
     *     <li><b>A number.</b> {@code "5"} gives the tval with that value, as C's {@code strtoul}
     *     fast path does. Surrounding whitespace is ignored, so {@code " 5 "} also resolves, but
     *     any other trailing text is rejected — {@code "5x"} gives {@code null}, which is what C's
     *     {@code contains_only_spaces} guard is there to enforce.</li>
     *     <li><b>A display name.</b> {@code "magic book"}, {@code "digger"}, case-insensitively.
     *     This is the form every {@code lib/gamedata} file uses. It is matched against
     *     {@link #getName()} rather than the constant's identifier because two rows differ between
     *     the two: {@link #TV_DIGGING} is written {@code "digger"}, and {@link #TV_DRAG_ARMOR} is
     *     written {@code "dragon armor"}.</li>
     *     <li><b>An underscored name.</b> {@code "soft_armor"} or {@code "SOFT_ARMOR"} — the display
     *     name with its spaces turned into underscores, which is how the constants themselves are
     *     spelled. A convenience for port-side callers, with no equivalent in C. The {@code TV_}
     *     prefix is supplied here, so a caller must <em>not</em> pass one of its own: a literal
     *     {@code "TV_SWORD"} does not resolve.</li>
     * </ol>
     *
     * <p>British spellings are bridged before anything else, so {@code "dragon armour"} — used by
     * three artifacts in {@code artifact.txt} — reaches {@link #TV_DRAG_ARMOR}. The rewrite has to
     * precede the name match rather than follow it, exactly as C calls {@code de_armour} before
     * looping over {@code tval_names[]}; on the {@code dragon armour} row the later identifier
     * match cannot rescue it, because the constant is {@code DRAG_ARMOR} and not
     * {@code DRAGON_ARMOR}.
     *
     * <p>Note that {@code "none"} and {@code "0"} are <em>successful</em> lookups returning
     * {@link #TV_NONE}: that is a real tval, carried by curse objects and by the synthesised
     * artifact kinds. Only text that resolves to no tval at all returns {@code null}, which is this
     * port's equivalent of C returning {@code -1}. Failures are silent — every caller already
     * reports them against the offending data-file line.
     *
     * @param name the tval text to resolve: a number, a display name, or an enum identifier
     * @return the matching {@link TValue}, or {@code null} if the text resolves to no tval
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static @Nullable TValue fromName(@NotNull String name) {
        String toSearch;
        // logger.debug("Trying to parse {} as a TV name", name);

        if (name.toUpperCase().contains("RMOUR")) {
            toSearch = name.replace("RMOUR", "RMOR")
                    .replace("rmour", "rmor");
        } else
            toSearch = name;

        String isNumber = toSearch.trim();

        try {
            int value = Integer.parseInt(isNumber);
            return fromName(value);
        } catch (NumberFormatException e) {
            // Fall through to name lookup
        }

        for (TValue value : TValue.values()) {
            if (value.getName().equalsIgnoreCase(toSearch))
                return value;
        }

        toSearch = "TV_" + toSearch.toUpperCase().replace(" ", "_");

        try {
            return TValue.valueOf(toSearch);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Resolves a tval from its numeric value — the raw {@code TV_*} integer that C stores in
     * {@code object->tval}. Each constant's {@link #ordinal()} <em>is</em> its C value, because this
     * enum is declared in {@code list-tvals.h} order; that correspondence is what makes this lookup
     * a simple index, and it is the reason the declaration order above must not be disturbed.
     *
     * <p>Both bounds mirror C's guard in {@code tval_find_idx}: negatives are rejected, and so is
     * anything from the constant count upwards — C tests {@code r < TV_MAX}, and since this port
     * has no {@code TV_MAX} constant, {@code values().length} plays that role.
     *
     * @param i the numeric tval, from {@code 0} ({@link #TV_NONE}) to {@code values().length - 1}
     * @return the tval with that value, or {@code null} if {@code i} falls outside that range
     */
    public static TValue fromName(int i) {
        if (i < 0 || i >= TValue.values().length)
            return null;

        return TValue.values()[i];
    }

    /**
     * Counts the object kinds ({@code object.txt}) carrying the named tval — the port of C's
     * {@code tval_sval_count}. Its one caller in C sizes the money table during object generation
     * ({@code obj-make.c}), which asks for {@code "gold"}.
     *
     * <p>Kinds with no tval are skipped, matching C's {@code if (!kind->tval) continue;}. In C that
     * test excludes tval {@code 0}, so both {@link #TV_NONE} and the port-only {@code null} are
     * passed over here — which means asking for {@code "none"} counts nothing, as it does in C.
     * An unresolvable name likewise counts nothing, C returning 0 for a tval of {@code -1}.
     *
     * @param name the tval text, in any of the forms {@link #fromName(String)} accepts
     * @return how many object kinds carry that tval; {@code 0} if the name resolves to none
     */
    public static int tValSValCount(String name) {
        TValue tValue = fromName(name);
        if (tValue == null) return 0;

        return (int) ObjectRegistry.getObjectKinds().stream()
                .filter(t -> t.gettValue() != null && t.gettValue() != TV_NONE && tValue == t.gettValue())
                .count();
    }

    /**
     * Lists the svals of every object kind ({@code object.txt}) carrying the named tval — the port
     * of C's {@code tval_sval_list}. Kinds come back in registry order, so the size of the result
     * agrees with {@link #tValSValCount(String)} for the same name.
     *
     * <p>C writes into a buffer the caller allocates, and takes a {@code max_size} so it cannot
     * overrun it; a {@link List} grows on demand, so that parameter has no purpose here and is
     * dropped. The {@code !kind->tval} skip is kept, and a name that resolves to no tval yields an
     * empty list rather than {@code null}, mirroring C's early {@code return 0}.
     *
     * @param name the tval text, in any of the forms {@link #fromName(String)} accepts
     * @return the svals of the matching kinds, empty if the name resolves to none
     */
    @NotNull
    public static List<Integer> tvalSvalList(String name) {
        TValue tValue = fromName(name);
        List<Integer> list = new ArrayList<>();

        if (tValue == null) return list;

        for (ObjectKind kind : ObjectRegistry.getObjectKinds()) {
            if (kind.gettValue() == null || kind.gettValue() == TV_NONE) continue;
            if (kind.gettValue() != tValue) continue;
            list.add(kind.getsVal());
        }

        return list;
    }

    /**
     * @return whether this tval is jewelry (a ring or amulet)
     */
    public boolean isJewellery() {
        return this == TV_RING || this == TV_AMULET;
    }
}