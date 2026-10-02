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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;

import java.util.ArrayList;
import java.util.List;

/**
 * The item "type value" (tval) of an object — its broad category (chest, weapon,
 * armour, ring, potion, spellbook, …). Each constant carries its display name, the
 * text the data files use for it.
 *
 * <p>This is the Java port of two pieces of C. The constants are C's {@code TV_*} values,
 * which C builds from the {@code TV(code_name, string_name)} rows of {@code list-tvals.h}
 * (expanded into an enum in {@code obj-properties.h}). The methods are the port of
 * {@code obj-tval.c}: the {@code tval_is_*} and {@code tval_can_have_*} predicates, the
 * name lookup {@code tval_find_idx} and its reverse {@code tval_find_name}, and the kind
 * counters {@code tval_sval_count} and {@code tval_sval_list}.
 *
 * <p>Each constant's {@link #ordinal()} is its C value, so the declaration order must stay
 * exactly as {@code list-tvals.h} has it. C's {@code TV_MAX} has no constant here;
 * {@code values().length} stands in for it.
 *
 * <p>C's predicates take a whole {@code struct object} or {@code struct object_kind} and
 * read its {@code tval}. Here they are instance methods on the tval itself, so the
 * {@code _k} variants ({@code tval_is_food_k}, {@code tval_is_light_k} and so on) collapse
 * into the same method as their object twins.
 *
 * <p>Class TValue coded before 260827, commented in full on 261002, updated on 261002 to
 * add {@code tval_find_name}.
 *
 * @author Rowan Crowther
 */
public enum TValue {
    /**
     * No type — C's {@code TV_NULL}, value {@code 0}, written {@code "none"} in the data files.
     * It is a real tval, not just "unassigned": curse objects carry it, and C's kind counters
     * skip it.
     *
     * <p>Constant TV_NONE coded before 260827, commented in full on 261002.
     */
    TV_NONE("none"),

    /**
     * A chest, which can hold objects and be trapped.
     *
     * <p>Constant TV_CHEST coded before 260827, commented in full on 261002.
     */
    TV_CHEST("chest"),

    /**
     * Sling ammunition: a pebble or iron shot, magical or not.
     *
     * <p>Constant TV_SHOT coded before 260827, commented in full on 261002.
     */
    TV_SHOT("shot"),

    /**
     * Bow ammunition: an arrow, magical or not.
     *
     * <p>Constant TV_ARROW coded before 260827, commented in full on 261002.
     */
    TV_ARROW("arrow"),

    /**
     * Crossbow ammunition: a bolt, magical or not.
     *
     * <p>Constant TV_BOLT coded before 260827, commented in full on 261002.
     */
    TV_BOLT("bolt"),

    /**
     * A missile launcher. Slings and crossbows are this tval too; the launcher kind is told
     * apart by its sval, not its tval.
     *
     * <p>Constant TV_BOW coded before 260827, commented in full on 261002.
     */
    TV_BOW("bow"),

    /**
     * A digging tool such as a shovel or pick. Its data-file name is {@code "digger"}, not
     * {@code "digging"}, one of two rows where name and identifier differ.
     *
     * <p>Constant TV_DIGGING coded before 260827, commented in full on 261002.
     */
    TV_DIGGING("digger"),

    /**
     * A hafted weapon, such as a mace or a flail.
     *
     * <p>Constant TV_HAFTED coded before 260827, commented in full on 261002.
     */
    TV_HAFTED("hafted"),

    /**
     * A polearm, such as a spear or a halberd.
     *
     * <p>Constant TV_POLEARM coded before 260827, commented in full on 261002.
     */
    TV_POLEARM("polearm"),

    /**
     * A bladed weapon, from a dagger to a blade of chaos.
     *
     * <p>Constant TV_SWORD coded before 260827, commented in full on 261002.
     */
    TV_SWORD("sword"),

    /**
     * Boots or sandals, worn on the feet.
     *
     * <p>Constant TV_BOOTS coded before 260827, commented in full on 261002.
     */
    TV_BOOTS("boots"),

    /**
     * Gloves or gauntlets, worn on the hands.
     *
     * <p>Constant TV_GLOVES coded before 260827, commented in full on 261002.
     */
    TV_GLOVES("gloves"),

    /**
     * A helm or hat. Counts as head armour alongside {@link #TV_CROWN}.
     *
     * <p>Constant TV_HELM coded before 260827, commented in full on 261002.
     */
    TV_HELM("helm"),

    /**
     * A crown. Counts as head armour alongside {@link #TV_HELM}.
     *
     * <p>Constant TV_CROWN coded before 260827, commented in full on 261002.
     */
    TV_CROWN("crown"),

    /**
     * A shield, carried on the arm.
     *
     * <p>Constant TV_SHIELD coded before 260827, commented in full on 261002.
     */
    TV_SHIELD("shield"),

    /**
     * A cloak, worn on the back.
     *
     * <p>Constant TV_CLOAK coded before 260827, commented in full on 261002.
     */
    TV_CLOAK("cloak"),

    /**
     * Soft body armour, such as cloth or leather. The data files spell it both
     * {@code "soft armor"} and {@code "soft armour"}; {@link #fromName(String)} accepts both.
     *
     * <p>Constant TV_SOFT_ARMOR coded before 260827, commented in full on 261002.
     */
    TV_SOFT_ARMOR("soft armor"),

    /**
     * Hard body armour, such as mail or plate. The data files spell it both
     * {@code "hard armor"} and {@code "hard armour"}; {@link #fromName(String)} accepts both.
     *
     * <p>Constant TV_HARD_ARMOR coded before 260827, commented in full on 261002.
     */
    TV_HARD_ARMOR("hard armor"),

    /**
     * Body armour made from dragon scales. Its data-file name is {@code "dragon armor"}, not
     * {@code "drag armor"}, one of two rows where name and identifier differ.
     *
     * <p>Constant TV_DRAG_ARMOR coded before 260827, commented in full on 261002.
     */
    TV_DRAG_ARMOR("dragon armor"),

    /**
     * A light source, such as a torch or a lantern.
     *
     * <p>Constant TV_LIGHT coded before 260827, commented in full on 261002.
     */
    TV_LIGHT("light"),

    /**
     * An amulet, worn around the neck. A flavoured type.
     *
     * <p>Constant TV_AMULET coded before 260827, commented in full on 261002.
     */
    TV_AMULET("amulet"),

    /**
     * A ring, worn on a finger. A flavoured type.
     *
     * <p>Constant TV_RING coded before 260827, commented in full on 261002.
     */
    TV_RING("ring"),

    /**
     * A staff: a charged device that affects the area around the user.
     *
     * <p>Constant TV_STAFF coded before 260827, commented in full on 261002.
     */
    TV_STAFF("staff"),

    /**
     * A wand: a charged device that is aimed.
     *
     * <p>Constant TV_WAND coded before 260827, commented in full on 261002.
     */
    TV_WAND("wand"),

    /**
     * A rod: a device that recharges on a timeout rather than holding charges.
     *
     * <p>Constant TV_ROD coded before 260827, commented in full on 261002.
     */
    TV_ROD("rod"),

    /**
     * A scroll, read once and used up.
     *
     * <p>Constant TV_SCROLL coded before 260827, commented in full on 261002.
     */
    TV_SCROLL("scroll"),

    /**
     * A potion, quaffed once and used up. Potions also nourish.
     *
     * <p>Constant TV_POTION coded before 260827, commented in full on 261002.
     */
    TV_POTION("potion"),

    /**
     * A flask of oil: fuel for a lantern, and throwable.
     *
     * <p>Constant TV_FLASK coded before 260827, commented in full on 261002.
     */
    TV_FLASK("flask"),

    /**
     * Food, eaten for nourishment.
     *
     * <p>Constant TV_FOOD coded before 260827, commented in full on 261002.
     */
    TV_FOOD("food"),

    /**
     * A mushroom: edible like food, but flavoured and with an effect.
     *
     * <p>Constant TV_MUSHROOM coded before 260827, commented in full on 261002.
     */
    TV_MUSHROOM("mushroom"),

    /**
     * A book of arcane magic.
     *
     * <p>Constant TV_MAGIC_BOOK coded before 260827, commented in full on 261002.
     */
    TV_MAGIC_BOOK("magic book"),

    /**
     * A book of divine prayers.
     *
     * <p>Constant TV_PRAYER_BOOK coded before 260827, commented in full on 261002.
     */
    TV_PRAYER_BOOK("prayer book"),

    /**
     * A book of nature magic.
     *
     * <p>Constant TV_NATURE_BOOK coded before 260827, commented in full on 261002.
     */
    TV_NATURE_BOOK("nature book"),

    /**
     * A book of shadow (necromantic) magic.
     *
     * <p>Constant TV_SHADOW_BOOK coded before 260827, commented in full on 261002.
     */
    TV_SHADOW_BOOK("shadow book"),

    /**
     * A spellbook belonging to none of the four realms above.
     *
     * <p>Constant TV_OTHER_BOOK coded before 260827, commented in full on 261002.
     */
    TV_OTHER_BOOK("other book"),

    /**
     * Money. The last tval: its ordinal, {@code 35}, is the highest valid tval, and C's
     * {@code TV_MAX} would follow it.
     *
     * <p>Constant TV_GOLD coded before 260827, commented in full on 261002.
     */
    TV_GOLD("gold");

    /**
     * Logger for this enum. Nothing currently logs through it: the one call, a debug trace in
     * {@link #fromName(String)}, is commented out, and lookup failures are left to the caller
     * to report.
     *
     * <p>Field logger coded before 260827, commented in full on 261002.
     */
    private static final Logger logger = LogManager.getLogger(TValue.class);

    /**
     * The display name of this tval: the {@code string_name} column of C's {@code list-tvals.h},
     * and the text the data files use.
     *
     * <p>Field name coded before 260827, commented in full on 261002.
     */
    private final String name;
    /**
     * Binds a tval to its display name.
     *
     * <p>Constructor TValue coded before 260827, commented in full on 261002.
     *
     * @param name the display name, as {@code list-tvals.h} spells it
     */
    TValue(String name) {
        this.name = name;
    }

    /**
     * Resolves a tval's data-file text to its numeric tval — the port of C's
     * {@code tval_find_idx} ({@code obj-tval.c}).
     *
     * <p>The number is the enum's ordinal, which is why the constants are declared in C's order and
     * why {@code TV_NONE} sits first: C's tval 0 means "no type", and a port that reordered the
     * constants would quietly change every number the data files rely on.
     *
     * <p>The parsing is done by {@link #fromName(String)}, which accepts the same forms. When the
     * text resolves to no tval, {@code fromName} returns {@code null} and this method turns that
     * into {@code -1}, which is what C returns.
     *
     * <p>Function findIndex coded before 260827, commented in full on 260827, updated on 261002
     * to return {@code -1} for an unknown name instead of throwing, commented in full on 261002.
     *
     * @param tvalName the tval text, in any of the forms {@link #fromName(String)} accepts
     * @return the numeric tval for that text, or {@code -1} if it resolves to no tval
     */
    public static int findIndex(String tvalName) {
        TValue tValue = TValue.fromName(tvalName);
        if (tValue == null) {
            return -1;
        }
        return tValue.ordinal();
    }

    /**
     * Gives the display name of a numeric tval — the port of C's {@code tval_find_name}
     * ({@code obj-tval.c}), the reverse of {@link #findIndex(String)}.
     *
     * <p>C searches {@code tval_names[]} for a row whose tval equals the argument. Because that
     * table has one row for each value from {@code 0} to {@code TV_MAX - 1}, in order, the
     * search is the same as indexing by ordinal with a bounds check, which is what this does.
     * {@code 0} gives {@code "none"}; anything below {@code 0} or from {@code values().length}
     * upwards gives C's {@code "unknown"}.
     *
     * <p>For a {@code TValue} already in hand, {@link #getName()} gives the same text without
     * the check.
     *
     * <p>Function findName coded on 261002, commented in full on 261002.
     *
     * @param tvalIndex the numeric tval
     * @return the tval's display name, or {@code "unknown"} if the number is not a tval
     */
    public static String findName(int tvalIndex) {
        if (tvalIndex < 0 || tvalIndex >= TValue.values().length) {
            return "unknown";
        }

        return TValue.values()[tvalIndex].getName();
    }

    /**
     * Resolves a tval from its data-file text — the port of C's {@code tval_find_idx}
     * ({@code obj-tval.c}). Three forms are accepted, tried in this order:
     *
     * <ol>
     *     <li><b>A number.</b> {@code "5"} gives the tval with that value, as C's {@code strtoul}
     *     fast path does. Leading whitespace is skipped, using C's whitespace set (space,
     *     {@code \t}, {@code \n}, {@code \r}, {@code \f} and vertical tab). Only spaces and tabs
     *     may follow the digits, which is C's {@code contains_only_spaces} rule: {@code " 5\t"}
     *     resolves, but {@code "5x"} and {@code "5\n"} give {@code null}. A sign is allowed, so
     *     {@code "+5"} resolves and {@code "-1"} is out of range.</li>
     *     <li><b>A display name.</b> {@code "magic book"}, {@code "digger"}, case-insensitively.
     *     This is the form every {@code lib/gamedata} file uses. It is matched against
     *     {@link #getName()} rather than the constant's identifier because two rows differ between
     *     the two: {@link #TV_DIGGING} is written {@code "digger"}, and {@link #TV_DRAG_ARMOR} is
     *     written {@code "dragon armor"}.</li>
     *     <li><b>An identifier.</b> {@code "soft_armor"}, {@code "SOFT ARMOR"} or
     *     {@code "drag_armor"}: the constant's name without its {@code TV_} prefix, in any case,
     *     with spaces accepted in place of underscores. A convenience for port-side callers, with
     *     no equivalent in C. A caller must <em>not</em> pass the prefix: a literal
     *     {@code "TV_SWORD"} does not resolve.</li>
     * </ol>
     *
     * <p>British spellings are rewritten first by {@link #changeArmour(String)}, so
     * {@code "dragon armour"} reaches {@link #TV_DRAG_ARMOR}. The data files use the British
     * spelling 23 times across the three body armours, including three artifacts in
     * {@code artifact.txt}. The rewrite has to come before the name match, as C calls
     * {@code de_armour} before looping over {@code tval_names[]}: the identifier match cannot
     * rescue {@code "dragon armour"}, because the constant is {@code DRAG_ARMOR}.
     *
     * <p>Note that {@code "none"} and {@code "0"} are <em>successful</em> lookups returning
     * {@link #TV_NONE}: that is a real tval, carried by curse objects. Only text that resolves to
     * no tval at all returns {@code null}, which is this port's equivalent of C returning
     * {@code -1}. This method logs nothing; reporting a bad name is left to the caller.
     *
     * <p>Known differences from C, none reachable from a data file: the identifier form above
     * is extra; {@link Integer#parseInt(String)} also reads non-ASCII Unicode digits, which
     * {@code strtoul} does not; and C copies the name into a 40-character buffer before the
     * armour rewrite, which only matters for longer input.
     *
     * <p>Function fromName coded before 260827, updated on 261002 to match C's armour rewrite
     * and whitespace rules, commented in full on 261002.
     *
     * @param name the tval text to resolve: a number, a display name, or an identifier
     * @return the matching {@link TValue}, or {@code null} if the text resolves to no tval
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static @Nullable TValue fromName(@NotNull String name) {
        String toSearch;
        // logger.debug("Trying to parse {} as a TV name", name);
        toSearch = changeArmour(name);

        String isNumber = removeTabsAndSpaces(toSearch);

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
     * Rewrites the British spelling {@code armour} to the American {@code armor}, so that a
     * name like {@code "dragon armour"} matches the American display names. The port of C's
     * {@code de_armour} ({@code obj-tval.c}).
     *
     * <p>C's behaviour is kept exactly:
     * <ul>
     *     <li>Only lowercase {@code "armour"} triggers the rewrite, as C's {@code strstr} is
     *     case-sensitive. {@code "Dragon Armour"} is left alone and does not resolve.</li>
     *     <li>Only the first match is rewritten.</li>
     *     <li>Everything after the match is dropped, because C writes the closing {@code "r"}
     *     and a terminator over the {@code "ur"}. So {@code "dragon armourX"} becomes
     *     {@code "dragon armor"}.</li>
     * </ul>
     *
     * <p>Text with no lowercase {@code "armour"} is returned unchanged.
     *
     * <p>Function changeArmour coded on 261002, commented in full on 261002.
     *
     * @param name the tval text as given
     * @return the text with its first {@code armour} rewritten, or {@code name} unchanged
     */
    private static String changeArmour(@NotNull String name) {
        if (name.contains("armour")) {
            int armourPos = name.toUpperCase().indexOf("ARMOUR");
            if (armourPos != -1) {
                if (name.substring(armourPos + 1, armourPos + 6).equals("RMOUR"))
                    name = name.substring(0, armourPos + 1) + "RMOR";
                else if (name.substring(armourPos + 1, armourPos + 6).equals("rmour"))
                    name = name.substring(0, armourPos + 1) + "rmor";
            }
        }
        return name;
    }

    /**
     * Strips the whitespace C tolerates around a number, so the result can go straight to
     * {@link Integer#parseInt(String)}. The two ends follow different rules, as they do in C:
     * <ul>
     *     <li>Trailing: spaces and tabs only. That is C's {@code contains_only_spaces}
     *     ({@code z-util.c}), checked on whatever {@code strtoul} left after the digits. A
     *     trailing newline is kept, so {@code "5\n"} still fails to parse.</li>
     *     <li>Leading: the six characters C's {@code isspace} accepts (space, {@code \t},
     *     {@code \n}, {@code \r}, {@code \f} and vertical tab), which {@code strtoul} skips before
     *     the digits.</li>
     * </ul>
     *
     * <p>Only the number path uses the stripped text. The name match uses the text unstripped,
     * as C does, so {@code " sword"} does not resolve.
     *
     * <p>Function removeTabsAndSpaces coded on 261002, commented in full on 261002.
     *
     * @param toSearch the tval text, after the armour rewrite
     * @return the text with trailing spaces and tabs and leading C whitespace removed
     */
    private static String removeTabsAndSpaces(String toSearch) {
        while (toSearch.endsWith(" ") || toSearch.endsWith("\t")) {
            toSearch = toSearch.substring(0, toSearch.length() - 1);
        }

        while (toSearch.startsWith(" ") || toSearch.startsWith("\t") || toSearch.startsWith("\n")
                || toSearch.startsWith("\r") || toSearch.startsWith("\f") || toSearch.startsWith("\013")) {
            toSearch = toSearch.substring(1);
        }

        return toSearch;
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
     * <p>Function fromName(int) coded before 260827, commented in full on 261002.
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
     * {@code tval_sval_count} ({@code obj-tval.c}). Its one caller in C sizes the money table
     * during object generation ({@code obj-make.c}), which asks for {@code "gold"}.
     *
     * <p>Kinds with no tval are skipped, matching C's {@code if (!kind->tval) continue;}. In C that
     * test excludes tval {@code 0}, so both {@link #TV_NONE} and the port-only {@code null} are
     * passed over here — which means asking for {@code "none"} counts nothing, as it does in C.
     * An unresolvable name likewise counts nothing, C returning 0 for a tval of {@code -1}.
     *
     * <p>Function tValSValCount coded before 260830, commented in full on 261002.
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
     * of C's {@code tval_sval_list} ({@code obj-tval.c}). Kinds come back in registry order, so
     * the size of the result agrees with {@link #tValSValCount(String)} for the same name.
     *
     * <p>C writes into a buffer the caller allocates, and takes a {@code max_size} so it cannot
     * overrun it; a {@link List} grows on demand, so that parameter has no purpose here and is
     * dropped. The {@code !kind->tval} skip is kept, and a name that resolves to no tval yields an
     * empty list rather than {@code null}, mirroring C's early {@code return 0}.
     *
     * <p>Function tvalSvalList coded before 260830, commented in full on 261002.
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
     * Get the string name of this TValue — the text the data files use for it, which is the
     * {@code string_name} column of C's {@code list-tvals.h} and the column {@code tval_find_idx}
     * matches against. For most tvals it is the identifier lower-cased, but not for
     * {@link #TV_DIGGING} ({@code "digger"}) or {@link #TV_DRAG_ARMOR} ({@code "dragon armor"}).
     *
     * <p>This is also what C's {@code tval_find_name} returns for a valid tval. To start from
     * a number, which may not be a tval, use {@link #findName(int)}.
     *
     * <p>Function getName coded before 260827, commented in full on 261002, updated on 261002
     * once {@code findName} existed.
     *
     * @return The type name of this TValue
     */
    public String getName() {
        return name;
    }

    /**
     * Whether this is a staff. Port of C's {@code tval_is_staff} ({@code obj-tval.c}).
     *
     * <p>Function isStaff coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_STAFF}
     */
    public boolean isStaff() {
        return this == TV_STAFF;
    }

    /**
     * Whether this is a wand. Port of C's {@code tval_is_wand} ({@code obj-tval.c}).
     *
     * <p>Function isWand coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_WAND}
     */
    public boolean isWand() {
        return this == TV_WAND;
    }

    /**
     * Whether this is a rod. Port of C's {@code tval_is_rod} ({@code obj-tval.c}).
     *
     * <p>Function isRod coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_ROD}
     */
    public boolean isRod() {
        return this == TV_ROD;
    }

    /**
     * Whether this is a potion. Port of C's {@code tval_is_potion} ({@code obj-tval.c}).
     *
     * <p>Function isPotion coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_POTION}
     */
    public boolean isPotion() {
        return this == TV_POTION;
    }

    /**
     * Whether this is a scroll. Port of C's {@code tval_is_scroll} ({@code obj-tval.c}).
     *
     * <p>Function isScroll coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_SCROLL}
     */
    public boolean isScroll() {
        return this == TV_SCROLL;
    }

    /**
     * Whether this is food. Mushrooms are a separate tval and do not count; use
     * {@link #isEdible()} for both. Port of C's {@code tval_is_food} and {@code tval_is_food_k}
     * ({@code obj-tval.c}).
     *
     * <p>Function isFood coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_FOOD}
     */
    public boolean isFood() {
        return this == TV_FOOD;
    }

    /**
     * Whether this is a mushroom. Port of C's {@code tval_is_mushroom} and
     * {@code tval_is_mushroom_k} ({@code obj-tval.c}).
     *
     * <p>Function isMushroom coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_MUSHROOM}
     */
    public boolean isMushroom() {
        return this == TV_MUSHROOM;
    }

    /**
     * Whether this is a light source. Port of C's {@code tval_is_light} and
     * {@code tval_is_light_k} ({@code obj-tval.c}).
     *
     * <p>Function isLight coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_LIGHT}
     */
    public boolean isLight() {
        return this == TV_LIGHT;
    }

    /**
     * Whether this is a ring. Port of C's {@code tval_is_ring} ({@code obj-tval.c}).
     *
     * <p>Function isRing coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_RING}
     */
    public boolean isRing() {
        return this == TV_RING;
    }

    /**
     * Whether this is a chest. Port of C's {@code tval_is_chest} ({@code obj-tval.c}).
     *
     * <p>Function isChest coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_CHEST}
     */
    public boolean isChest() {
        return this == TV_CHEST;
    }

    /**
     * Whether this is fuel. The name does not match the tval: fuel is the flask of oil. Port of
     * C's {@code tval_is_fuel} ({@code obj-tval.c}).
     *
     * <p>Function isFuel coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_FLASK}
     */
    public boolean isFuel() {
        return this == TV_FLASK;
    }

    /**
     * Whether this is money. Port of C's {@code tval_is_money} and {@code tval_is_money_k}
     * ({@code obj-tval.c}).
     *
     * <p>Function isMoney coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_GOLD}
     */
    public boolean isMoney() {
        return this == TV_GOLD;
    }

    /**
     * Whether this is a digging tool. Port of C's {@code tval_is_digger} ({@code obj-tval.c}).
     *
     * <p>Function isDigger coded before 260830, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_DIGGING}
     */
    public boolean isDigger() {
        return this == TV_DIGGING;
    }

    /**
     * Whether objects of this tval can nourish: food, potions and mushrooms. Port of C's
     * {@code tval_can_have_nourishment} ({@code obj-tval.c}).
     *
     * <p>Function canHaveNourishment coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_FOOD}, {@link #TV_POTION} or {@link #TV_MUSHROOM}
     */
    public boolean canHaveNourishment() {
        return this == TV_FOOD || this == TV_POTION
                || this == TV_MUSHROOM;
    }

    /**
     * Whether objects of this tval hold charges: staves and wands. Rods use a timeout instead.
     * Port of C's {@code tval_can_have_charges} ({@code obj-tval.c}).
     *
     * <p>Function canHaveCharges coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_STAFF} or {@link #TV_WAND}
     */
    public boolean canHaveCharges() {
        return this == TV_STAFF || this == TV_WAND;
    }

    /**
     * Whether objects of this tval recharge on a timeout: rods only. Port of C's
     * {@code tval_can_have_timeout} ({@code obj-tval.c}).
     *
     * <p>Function canHaveTimeout coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_ROD}
     */
    public boolean canHaveTimeout() {
        return this == TV_ROD;
    }

    /**
     * Whether this is body armour: soft, hard or dragon-scale. Port of C's
     * {@code tval_is_body_armor} ({@code obj-tval.c}).
     *
     * <p>Function isBodyArmour coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_SOFT_ARMOR}, {@link #TV_HARD_ARMOR} or
     * {@link #TV_DRAG_ARMOR}
     */
    public boolean isBodyArmour() {
        return switch (this) {
            case TV_SOFT_ARMOR, TV_HARD_ARMOR, TV_DRAG_ARMOR -> true;
            default -> false;
        };
    }

    /**
     * Whether this is head armour: a helm or a crown. Port of C's {@code tval_is_head_armor}
     * ({@code obj-tval.c}).
     *
     * <p>Function isHeadArmour coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_HELM} or {@link #TV_CROWN}
     */
    public boolean isHeadArmour() {
        return this == TV_CROWN || this == TV_HELM;
    }

    /**
     * Whether this is ammunition: shots, arrows or bolts. Port of C's {@code tval_is_ammo}
     * ({@code obj-tval.c}).
     *
     * <p>Function isAmmo coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_SHOT}, {@link #TV_ARROW} or {@link #TV_BOLT}
     */
    public boolean isAmmo() {
        return switch (this) {
            case TV_SHOT, TV_ARROW, TV_BOLT -> true;
            default -> false;
        };
    }

    /**
     * Whether this is a sharp missile: an arrow or a bolt, but not a shot. Port of C's
     * {@code tval_is_sharp_missile} ({@code obj-tval.c}).
     *
     * <p>Function isSharpMissile coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_ARROW} or {@link #TV_BOLT}
     */
    public boolean isSharpMissile() {
        return switch (this) {
            case TV_ARROW, TV_BOLT -> true;
            default -> false;
        };
    }

    /**
     * Whether this is a crossbow bolt. Port of C's {@code tval_is_bolt} ({@code obj-tval.c}).
     *
     * <p>Function isBolt coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_BOLT}
     */
    public boolean isBolt() {
        return this == TV_BOLT;
    }

    /**
     * Whether this is a missile launcher. Slings, bows and crossbows all share
     * {@link #TV_BOW}, so that is the only member. Port of C's {@code tval_is_launcher}
     * ({@code obj-tval.c}).
     *
     * <p>Function isLauncher coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_BOW}
     */
    public boolean isLauncher() {
        return this == TV_BOW;
    }

    /**
     * Whether objects of this tval can be used up or activated by the use commands: rods,
     * wands, staves, scrolls, potions, food and mushrooms. Port of C's {@code tval_is_useable}
     * ({@code obj-tval.c}).
     *
     * <p>Function isUseable coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is one of the seven useable tvals
     */
    public boolean isUseable() {
        return switch (this) {
            case TV_ROD, TV_WAND, TV_STAFF, TV_SCROLL, TV_POTION, TV_FOOD, TV_MUSHROOM -> true;
            default -> false;
        };
    }

    /**
     * Whether using this tval can fail on the player's device skill: staves, wands and rods.
     * Port of C's {@code tval_can_have_failure} ({@code obj-tval.c}).
     *
     * <p>Function canHaveFailure coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_STAFF}, {@link #TV_WAND} or {@link #TV_ROD}
     */
    public boolean canHaveFailure() {
        return switch (this) {
            case TV_STAFF, TV_WAND, TV_ROD -> true;
            default -> false;
        };
    }

    /**
     * Whether this is a weapon: a melee weapon, the launcher, or ammunition. Port of C's
     * {@code tval_is_weapon} ({@code obj-tval.c}).
     *
     * <p>Function isWeapon coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is a sword, hafted weapon, polearm, digger, bow, bolt, arrow or
     * shot
     */
    public boolean isWeapon() {
        return switch (this) {
            case TV_SWORD, TV_HAFTED, TV_POLEARM, TV_DIGGING, TV_BOW,
                 TV_BOLT, TV_ARROW, TV_SHOT -> true;
            default -> false;
        };
    }

    /**
     * Whether this is any piece of armour: the three body armours, shield, cloak, crown, helm,
     * boots or gloves. Port of C's {@code tval_is_armor} ({@code obj-tval.c}).
     *
     * <p>Function isArmour coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is one of the nine armour tvals
     */
    public boolean isArmour() {
        return switch (this) {
            case TV_DRAG_ARMOR, TV_HARD_ARMOR, TV_SOFT_ARMOR, TV_SHIELD,
                 TV_CLOAK, TV_CROWN, TV_HELM, TV_BOOTS, TV_GLOVES -> true;
            default -> false;
        };
    }

    /**
     * Whether this is a melee weapon: sword, hafted weapon, polearm or digger. The digger
     * counts. Port of C's {@code tval_is_melee_weapon} ({@code obj-tval.c}).
     *
     * <p>Function isMeleeWeapon coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_SWORD}, {@link #TV_HAFTED}, {@link #TV_POLEARM}
     * or {@link #TV_DIGGING}
     */
    public boolean isMeleeWeapon() {
        return switch (this) {
            case TV_SWORD, TV_HAFTED, TV_POLEARM, TV_DIGGING -> true;
            default -> false;
        };
    }

    /**
     * Whether objects of this tval have variable power: everything {@link #isWearable()}
     * accepts, plus the three ammunition tvals. Port of C's {@code tval_has_variable_power}
     * ({@code obj-tval.c}).
     *
     * <p>Function hasVariablePower coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is wearable or ammunition
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
     * Whether objects of this tval can be worn or wielded: the launcher, the melee weapons, the
     * nine armour tvals, lights, rings and amulets. Ammunition is not wearable. Port of C's
     * {@code tval_is_wearable} ({@code obj-tval.c}).
     *
     * <p>Function isWearable coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is one of the seventeen wearable tvals
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
     * Whether this can be eaten: food or a mushroom. Port of C's {@code tval_is_edible}
     * ({@code obj-tval.c}).
     *
     * <p>Function isEdible coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_FOOD} or {@link #TV_MUSHROOM}
     */
    public boolean isEdible() {
        return this == TV_FOOD || this == TV_MUSHROOM;
    }

    /**
     * Whether kinds of this tval get a randomised flavour, such as an unidentified potion's
     * colour: amulets, rings, staves, wands, rods, potions, mushrooms and scrolls. Port of C's
     * {@code tval_can_have_flavor_k} ({@code obj-tval.c}).
     *
     * <p>Function canHaveFlavour coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is one of the eight flavoured tvals
     */
    public boolean canHaveFlavour() {
        return switch (this) {
            case TV_AMULET, TV_RING, TV_STAFF, TV_WAND, TV_ROD, TV_POTION, TV_MUSHROOM, TV_SCROLL -> true;
            default -> false;
        };
    }

    /**
     * Whether this is a spellbook of any realm. Port of C's {@code tval_is_book_k}
     * ({@code obj-tval.c}).
     *
     * <p>Function isBook coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is one of the five book tvals
     */
    public boolean isBook() {
        return switch (this) {
            case TV_MAGIC_BOOK, TV_PRAYER_BOOK, TV_NATURE_BOOK, TV_SHADOW_BOOK, TV_OTHER_BOOK -> true;
            default -> false;
        };
    }

    /**
     * Whether this is a "zapper": a wand or a staff. Rods are not zappers. Port of C's
     * {@code tval_is_zapper} ({@code obj-tval.c}).
     *
     * <p>Function isZapper coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_WAND} or {@link #TV_STAFF}
     */
    public boolean isZapper() {
        return this == TV_WAND || this == TV_STAFF;
    }

    /**
     * Whether this is jewellery: a ring or an amulet. Port of C's {@code tval_is_jewelry}
     * ({@code obj-tval.c}).
     *
     * <p>Function isJewellery coded before 260827, commented in full on 261002.
     *
     * @return whether this tval is {@link #TV_RING} or {@link #TV_AMULET}
     */
    public boolean isJewellery() {
        return this == TV_RING || this == TV_AMULET;
    }
}