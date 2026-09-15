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

package uk.co.jackoftradesltd.backend.parser.egoitem;

import java.util.List;
import java.util.Map;

/**
 * Immutable extraction record for one {@code ego_item.txt} entry: the raw, still-unresolved
 * fields parsed by the grammar, later turned into the {@code EgoItem} domain type by
 * {@link EgoItemAssembler}.
 *
 * @param name        the ego item's display name
 * @param cost        the ego's cost adjustment, unparsed
 * @param rating      the ego's power rating, unparsed
 * @param commonness  the allocation commonness weight, unparsed
 * @param lower       the allocation minimum depth, unparsed
 * @param upper       the allocation maximum depth, unparsed
 * @param toH         the combat to-hit dice string, or {@code ""} if none
 * @param toD         the combat to-damage dice string, or {@code ""} if none
 * @param toA         the combat to-AC dice string, or {@code ""} if none
 * @param minToH      the minimum combat to-hit bound, unparsed
 * @param minToD      the minimum combat to-damage bound, unparsed
 * @param minToA      the minimum combat to-AC bound, unparsed
 * @param tVals       whole-tvalue {@code item:} lines; every
 *                    {@link uk.co.jackoftradesltd.middle.objects.ObjectKind} of each named
 *                    tvalue is eligible to carry this ego
 * @param itemRefs    specific tvalue/svalue {@code item:} pairs naming individual object kinds
 * @param flags       the {@code flags:} directive's raw tokens; an {@code IGNORE_}-prefixed
 *                    token sets an element-ignore flag, everything else is an object or
 *                    object-kind flag
 * @param flagsOff    object flag names this ego strips from its base item
 * @param values      the {@code values:} directive as label/value text pairs; a
 *                    {@code RES_}-prefixed key is an element resistance level, everything
 *                    else a dice-valued object modifier
 * @param minValues   the minimum floor for each dice-valued modifier in {@code values}
 * @param act         the activation's name, or {@code ""} if this ego has none
 * @param timeout     the activation recharge-time dice string, or {@code ""} if none
 * @param brands      the brand codes this ego carries, resolved by registry lookup
 * @param slays       the slay codes this ego carries, resolved by registry lookup
 * @param curses      the curse names mapped to their raw power text
 * @param desc        the ego's flavour/description text
 * @param line        the source line the record begins on, for error reporting
 * @author Rowan Crowther
 */
public record EgoItemParseRecord(String name,
                                 String cost, String rating,
                                 String commonness, String lower, String upper,
                                 String toH, String toD, String toA,
                                 String minToH, String minToD, String minToA,
                                 List<String> tVals,
                                 List<ItemRef> itemRefs,
                                 List<String> flags,
                                 List<String> flagsOff,
                                 Map<String, String> values,
                                 Map<String, String> minValues,
                                 String act,
                                 String timeout,
                                 List<String> brands,
                                 List<String> slays,
                                 Map<String, String> curses,
                                 String desc,
                                 int line) {

    /**
     * One specific tvalue/svalue {@code item:} pair naming an individual
     * {@link uk.co.jackoftradesltd.middle.objects.ObjectKind} eligible to carry this ego, as
     * opposed to a whole-tvalue reference.
     *
     * @param tVal the object base type's tvalue name
     * @param sVal the object kind's svalue name
     */
    public record ItemRef(String tVal, String sVal) {
    }
}
