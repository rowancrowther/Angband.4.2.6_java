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

package uk.co.jackoftradesltd.middle.monsters;

import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

/**
 * One possible death drop for a monster race: an item type (and optionally a specific
 * {@link ObjectKind}), the percentage chance it drops, and the quantity range. When no specific
 * kind is given the drop is "by base type", meaning any object of that tval. This is the Java
 * port of the C original's {@code struct monster_drop} ({@code monster.h}), the record that
 * {@code parse_monster_drop} ({@code drop:} lines) and {@code parse_monster_drop_base}
 * ({@code drop-base:} lines) build in {@code mon-init.c}.
 *
 * <p>Differences from the C struct worth knowing about:
 * <ul>
 *   <li>C's {@code next} pointer is not carried. The drops of a race are held in a
 *       {@code List<MonsterDrop>} on {@link MonsterRace} (and on {@link MonsterLore}), where C
 *       chains them. C prepends each entry as it parses, so its chain runs in reverse file order;
 *       the Java list on a race is in the same reverse order, and {@code drop:} and
 *       {@code drop-base:} lines share the one list (see the class block of {@link MonsterRace}).
 *       A loop that rolls {@code randint0} for each drop, as {@code mon_create_drop_count} and
 *       {@code mon_create_drop} in {@code mon-make.c} do, sees the entries in the same order as C.</li>
 *   <li>C leaves {@code tval} at 0 for a specific drop and takes the type from
 *       {@code kind->tval}; it sets {@code tval} only for a base drop. Java records the tval on
 *       both, and for a specific drop it is always the tval of the kind.</li>
 *   <li>C's {@code unsigned int} members are {@code int} here.</li>
 * </ul>
 *
 * <p>The class is a plain holder: nothing reads the members yet, and there are no getters. Their
 * readers are {@code mon_create_drop_count} and {@code mon_create_drop} in {@code mon-make.c},
 * which roll the chance and the quantity, and the drop output of the lore save in
 * {@code mon-lore.c}, which are later chapters.
 *
 * <p>Class MonsterDrop coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class MonsterDrop {
    /**
     * The item type this drop produces, C's {@code tval} member. For a base drop it is the whole
     * of the selection, passed to {@code make_object} in {@code mon_create_drop}; for a specific
     * drop it equals the tval of {@link #kind}, where C leaves the member at 0.
     *
     * <p>Field tVal coded before 261014, commented in full on 261014.
     */
    private TValue tVal;
    /**
     * The specific object kind dropped, C's {@code kind} member, or {@code null} for a
     * by-base-type drop. C tells the two apart by testing this member for {@code NULL}, as
     * {@link #isBase()} does.
     *
     * <p>Field kind coded before 261014, commented in full on 261014.
     */
    private ObjectKind kind;
    /**
     * Percentage chance the drop occurs, C's {@code percent_chance}. C rolls
     * {@code randint0(100)} against it, so a value of 100 or more always drops and 0 never does.
     *
     * <p>Field percentChance coded before 261014, commented in full on 261014.
     */
    private int percentChance;
    /**
     * Minimum quantity dropped, C's {@code min}. C draws the quantity as
     * {@code randint0(max - min) + min}, so the maximum itself is never reached, and equal
     * bounds give exactly the minimum.
     *
     * <p>Field min coded before 261014, commented in full on 261014.
     */
    private int min;
    /**
     * Maximum quantity dropped, C's {@code max}. It is an exclusive bound on the draw (see
     * {@link #min}), but {@code mon_create_drop_count} adds it whole when it works out the most a
     * monster can carry.
     *
     * <p>Field max coded before 261014, commented in full on 261014.
     */
    private int max;

    /**
     * Build a drop of a specific object kind, the Java form of the record
     * {@code parse_monster_drop} fills for a {@code drop:} line.
     *
     * <p>Constructor MonsterDrop (specific kind) coded before 261014, commented in full on 261014.
     *
     * @param tVal          the item type
     * @param kind          the specific object kind
     * @param percentChance the drop chance
     * @param min           minimum quantity
     * @param max           maximum quantity
     */
    public MonsterDrop(TValue tVal, ObjectKind kind, int percentChance, int min, int max) {
        this.tVal = tVal;
        this.kind = kind;
        this.percentChance = percentChance;
        this.min = min;
        this.max = max;
    }

    /**
     * Build a by-base-type drop, the Java form of the record {@code parse_monster_drop_base}
     * fills for a {@code drop-base:} line. The kind is left {@code null}.
     *
     * <p>Constructor MonsterDrop (by base type) coded before 261014, commented in full on 261014.
     *
     * @param tVal          the item type
     * @param percentChance the drop chance
     * @param min           minimum quantity
     * @param max           maximum quantity
     */
    public MonsterDrop(TValue tVal, int percentChance, int min, int max) {
        this.tVal = tVal;
        this.kind = null;
        this.percentChance = percentChance;
        this.min = min;
        this.max = max;
    }

    /**
     * Whether this is a by-base-type drop, which is C's {@code drop->kind == NULL} test in
     * {@code mon_create_drop} and the lore save. A base drop is created with
     * {@code make_object} from the tval alone; a specific drop is prepped from its kind.
     *
     * <p>Function isBase coded before 261014, commented in full on 261014.
     *
     * @return true if this is a by-base-type drop (no specific kind)
     */
    public boolean isBase() {
        return this.kind == null;
    }
}