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

package uk.co.jackoftradesltd.backend.parser.body;

import java.util.List;

/**
 * Immutable extraction record for one {@code body.txt} entry: the raw, still-unresolved fields
 * (body name plus its ordered equip slots) parsed by the grammar, later turned into the
 * {@code PlayerBody} domain type by {@link BodyAssembler}.
 *
 * @param bodyName the body's display name
 * @param slots    the body's {@code slot:} lines, in declared order — order is significant,
 *                 since a slot's index in this list becomes its identity in the assembled
 *                 {@code PlayerBody}
 * @param line     the source line the record begins on, for error reporting
 * @author Rowan Crowther
 */
public record BodyParseRecord(String bodyName,
                              List<BodySlotRecord> slots,
                              int line) {
    /**
     * One raw {@code slot:} line within a body record: an equipment slot type paired with its
     * display name, not yet resolved to an
     * {@link uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum}.
     *
     * @param slotType the slot type name (bare, no {@code EQUIP_} prefix)
     * @param slotName the slot's display name, e.g. {@code "on body"}
     */
    public record BodySlotRecord(String slotType, String slotName) {
    }
}
