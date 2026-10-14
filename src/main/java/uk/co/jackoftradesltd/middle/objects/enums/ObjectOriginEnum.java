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
 * Where an object came from, used to build the "history" line in an item's
 * description (found on the floor, dropped by a monster, bought, an inheritance,
 * …). The Java form of C's {@code ORIGIN_*} constants, which C builds from the rows of
 * {@code list-origins.h} twice: once as the anonymous enum in {@code object.h}, and once as the
 * {@code origins[]} table in {@code obj-info.c}. {@code ItemObject} holds one in its {@code origin}
 * field, as C's {@code obj->origin}.
 *
 * <p>All 26 rows of the header were compared with this enum this pass and agree in name, order,
 * parameter count and template. {@link #ORIGIN_NONE} is the header's own first row, so an ordinal
 * here is the same number as the C constant, and C's {@code origins[origin]} lookup is
 * {@code values()[origin]} here. The one mismatch found, a {@code "floor in a cavern"} template
 * where the header has {@code "floor of a cavern"}, was corrected on 261014.
 *
 * <p>Where this differs from C:
 * <ul>
 *   <li>{@link #ORIGIN_MAX} is a count, not an origin. C's enum ends with it, but the table has no
 *   row for it, so C reading {@code origins[ORIGIN_MAX]} would run off the array. Java gives it
 *   {@code -1} and an empty template so that it reads like the other no-history rows.</li>
 *   <li>C's table row is {@code { type, args, desc }}. The {@code type} column is only the enum
 *   value again, so Java drops it and uses the ordinal; {@code args} is {@link #numParameters} and
 *   {@code desc} is {@link #name}.</li>
 * </ul>
 *
 * <p>How C uses the table: {@code describe_origin} in {@code obj-info.c} switches on the parameter
 * count. {@code -1} prints nothing and returns false, {@code 0} prints the template as it stands,
 * {@code 1} fills the template with the place ("at 250 feet (level 5)", or "in town" at depth 0)
 * and {@code 2} fills it with the monster's name and then the place. A mimic's drop is shown as
 * {@link #ORIGIN_FLOOR} while the object is still mimicking. The stats front-end
 * {@code main-stats.c} also writes the constant names into its database, and its header comment
 * says only the origins before {@link #ORIGIN_STATS} are considered.
 *
 * <p>Both fields are private and have no getter, and nothing in the Java port reads them yet:
 * {@code describe_origin} has no Java counterpart, so the history line is not built.
 *
 * <p>Enum ObjectOriginEnum coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public enum ObjectOriginEnum {
    /**
     * C's {@code ORIGIN_NONE}: no origin recorded, the value of a zeroed {@code obj->origin}.
     * {@code ItemObject.wipe} lands on it, and {@code store_create_random} and
     * {@code store_create_item} in {@code store.c} set it on stock a store has made ("no origin
     * yet"). It has no history line.
     *
     * <p>Constant ORIGIN_NONE coded before 261014, commented in full on 261014.
     */
    ORIGIN_NONE(-1, ""),
    /**
     * C's {@code ORIGIN_FLOOR}: an object or gold lying on the floor. The cave generators in
     * {@code gen-cave.c} place the ordinary room and corridor items with it, and digging out a
     * vein with treasure in {@code cmd-cave.c} uses it for the gold.
     *
     * <p>Constant ORIGIN_FLOOR coded before 261014, commented in full on 261014.
     */
    ORIGIN_FLOOR(1, "Found lying on the floor %s"),
    /**
     * C's {@code ORIGIN_CHEST}: treasure that came out of an opened chest, set by {@code chest_death} in
     * {@code obj-chest.c}, along with the chest's own origin depth.
     *
     * <p>Constant ORIGIN_CHEST coded before 261014, commented in full on 261014.
     */
    ORIGIN_CHEST(1, "Taken from a chest found %s"),
    /**
     * C's {@code ORIGIN_SPECIAL}: an object placed in a special room, by the room
     * templates and inner rooms of {@code gen-room.c} and by {@code vault_objects} in
     * {@code gen-util.c}.
     *
     * <p>Constant ORIGIN_SPECIAL coded before 261014, commented in full on 261014.
     */
    ORIGIN_SPECIAL(1, "Found lying on the floor of a special room %s"),
    /**
     * C's {@code ORIGIN_PIT}: an object placed in a monster pit or nest, by {@code build_nest} and the pit
     * builder in {@code gen-room.c}.
     *
     * <p>Constant ORIGIN_PIT coded before 261014, commented in full on 261014.
     */
    ORIGIN_PIT(1, "Found lying on the floor in a pit %s"),
    /**
     * C's {@code ORIGIN_VAULT}: an object or gold placed by a vault's map, by {@code gen-room.c}.
     *
     * <p>Constant ORIGIN_VAULT coded before 261014, commented in full on 261014.
     */
    ORIGIN_VAULT(1, "Found lying on the floor in a vault %s"),
    /**
     * C's {@code ORIGIN_LABYRINTH}: an object or gold placed in a labyrinth level, by
     * {@code gen-cave.c}.
     *
     * <p>Constant ORIGIN_LABYRINTH coded before 261014, commented in full on 261014.
     */
    ORIGIN_LABYRINTH(1, "Found lying on the floor of a labyrinth %s"),
    /**
     * C's {@code ORIGIN_CAVERN}: an object or gold placed in a cavern level, by
     * {@code gen-cave.c}. The template reads "of a cavern", as the header does; it read
     * "in a cavern" until 261014.
     *
     * <p>Constant ORIGIN_CAVERN coded before 261014, commented in full on 261014.
     */
    ORIGIN_CAVERN(1, "Found lying on the floor of a cavern %s"),
    /**
     * C's {@code ORIGIN_RUBBLE}: an object that turned up when rubble was cleared, a one in ten
     * chance below the town, from the tunnelling code in {@code cmd-cave.c} and from
     * {@code project-feat.c} when a spell destroys rubble.
     *
     * <p>Constant ORIGIN_RUBBLE coded before 261014, commented in full on 261014.
     */
    ORIGIN_RUBBLE(1, "Found under some rubble %s"),
    /**
     * C's {@code ORIGIN_MIXED}: a stack made of items with different origins. {@code obj-pile.c},
     * function {@code object_origin_combine}, sets it, and {@code ItemObject.originCombine} does
     * the same here. It has no history line.
     *
     * <p>Constant ORIGIN_MIXED coded before 261014, commented in full on 261014.
     */
    ORIGIN_MIXED(-1, ""),
    /**
     * C's {@code ORIGIN_DROP}: an ordinary monster drop. The two parameters are the monster's
     * name and the place.
     *
     * <p>Constant ORIGIN_DROP coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_DROP_SPECIAL}: a drop from a monster placed in a special room.
     *
     * <p>Constant ORIGIN_DROP_SPECIAL coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_SPECIAL(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_DROP_PIT}: a drop from a monster placed in a pit or nest.
     *
     * <p>Constant ORIGIN_DROP_PIT coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_PIT(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_DROP_VAULT}: a drop from a monster placed in a vault.
     *
     * <p>Constant ORIGIN_DROP_VAULT coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_VAULT(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_STATS}: not an origin an object is given. The header's own comment marks
     * it as the end of the origins {@code main-stats.c} considers ("only the above are
     * considered"), and that program sizes its per-origin arrays with it. It has no history
     * line.
     *
     * <p>Constant ORIGIN_STATS coded before 261014, commented in full on 261014.
     */
    ORIGIN_STATS(-1, ""),
    /**
     * C's {@code ORIGIN_ACQUIRE}: an object made by the acquirement effect, set in
     * {@code obj-make.c}.
     *
     * <p>Constant ORIGIN_ACQUIRE coded before 261014, commented in full on 261014.
     */
    ORIGIN_ACQUIRE(1, "Conjured forth by magic %s"),
    /**
     * C's {@code ORIGIN_STORE}: an object the player bought, set on the purchased copy by
     * {@code do_cmd_buy} in {@code store.c}. It takes no parameters, so the history line is the template as it stands.
     *
     * <p>Constant ORIGIN_STORE coded before 261014, commented in full on 261014.
     */
    ORIGIN_STORE(0, "Bought from a store"),
    /**
     * C's {@code ORIGIN_STOLEN}: an object a monster took from the player, set in
     * {@code mon-blows.c}. {@code monster_death} in {@code mon-util.c} does not count stolen gold
     * as a monster's own drop. It has no history line.
     *
     * <p>Constant ORIGIN_STOLEN coded before 261014, commented in full on 261014.
     */
    ORIGIN_STOLEN(-1, ""),
    /**
     * C's {@code ORIGIN_BIRTH}: the player's starting kit, set in {@code player-birth.c} and,
     * here, in {@code PlayerBirth}.
     *
     * <p>Constant ORIGIN_BIRTH coded before 261014, commented in full on 261014.
     */
    ORIGIN_BIRTH(0, "An inheritance from your family"),
    /**
     * C's {@code ORIGIN_CHEAT}: an object created by a debug command, set in
     * {@code cmd-wizard.c}.
     *
     * <p>Constant ORIGIN_CHEAT coded before 261014, commented in full on 261014.
     */
    ORIGIN_CHEAT(0, "Created by debug option"),
    /**
     * C's {@code ORIGIN_DROP_BREED}: a drop from a monster that a breeder made, set in
     * {@code mon-move.c}, function {@code multiply_monster}, when the new monster is placed.
     *
     * <p>Constant ORIGIN_DROP_BREED coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_BREED(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_DROP_SUMMON}: a drop from a monster that was summoned, set in
     * {@code mon-summon.c} when the summoned monster is placed.
     *
     * <p>Constant ORIGIN_DROP_SUMMON coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_SUMMON(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_DROP_UNKNOWN}: a drop from a monster the player could not see.
     * {@code monster_death} in {@code mon-util.c} overwrites the carried object's origin with it
     * when the monster is not visible, which is why it takes only the place and not the monster's
     * name.
     *
     * <p>Constant ORIGIN_DROP_UNKNOWN coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_UNKNOWN(1, "Dropped by an unknown monster %s"),
    /**
     * C's {@code ORIGIN_DROP_POLY}: a drop from a monster that a polymorph produced, set in
     * {@code project-mon.c} when the new monster is placed.
     *
     * <p>Constant ORIGIN_DROP_POLY coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_POLY(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_DROP_MIMIC}: the object a mimic carries, set in {@code mon-make.c}. While
     * the monster is still mimicking it, {@code describe_origin} shows it as
     * {@link #ORIGIN_FLOOR} instead.
     *
     * <p>Constant ORIGIN_DROP_MIMIC coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_MIMIC(2, "Dropped by %s %s"),
    /**
     * C's {@code ORIGIN_DROP_WIZARD}: a drop from a monster the wizard-mode commands summoned,
     * set in {@code cmd-wizard.c}.
     *
     * <p>Constant ORIGIN_DROP_WIZARD coded before 261014, commented in full on 261014.
     */
    ORIGIN_DROP_WIZARD(2, "Dropped by %s %s"),
    /**
     * The count of origins. C ends its enum with {@code ORIGIN_MAX} but has no table row for it;
     * it is not an origin, and nothing should give it to an object.
     *
     * <p>Constant ORIGIN_MAX coded before 261014, commented in full on 261014.
     */
    ORIGIN_MAX(-1, "");

    /**
     * Number of {@code %s} parameters the {@link #name} template expects: C's {@code args} column
     * in {@code origins[]}. {@code -1} marks an origin with no displayed history line, {@code 0}
     * a template with no placeholder, {@code 1} a place, and {@code 2} a monster name followed by
     * a place.
     *
     * <p>Field numParameters coded before 261014, commented in full on 261014.
     */
    private final int numParameters;
    /**
     * The history-line template for this origin (with {@code %s} placeholders): C's {@code desc}
     * column in {@code origins[]}. It is empty for the origins that have no history line.
     *
     * <p>Field name coded before 261014, commented in full on 261014.
     */
    private final String name;

    /**
     * Bind an origin to its parameter count and history template. Each constant above is one row
     * of {@code list-origins.h}, minus the {@code type} column that the ordinal replaces.
     *
     * <p>Constructor ObjectOriginEnum coded before 261014, commented in full on 261014.
     *
     * @param numParameters number of template parameters
     * @param name          the history-line template
     */
    ObjectOriginEnum(int numParameters, String name) {
        this.numParameters = numParameters;
        this.name = name;
    }
}