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

package uk.co.jackoftrades.backend.parser.body;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.player.EquipSlot;
import uk.co.jackoftrades.middle.player.PlayerBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns each {@link BodyParseRecord} parsed from {@code body.txt} into a {@link PlayerBody} —
 * a named, ordered set of equipment slots.
 *
 * <p>Every record is one body: a name plus its {@code slot:} lines in file order. For each slot the
 * raw type string is resolved to an {@link EquipmentSlotsEnum} ({@code "EQUIP_" + type}) and paired
 * with its display name into an {@link EquipSlot}; the slots are kept in declared order because a
 * slot's index is its identity (C's gear system addresses equipment by slot number).
 *
 * <p>An unresolvable slot type is a soft error, but it rejects the <em>whole body</em> rather than
 * just the slot: dropping one slot would shift every later slot's index and silently corrupt the
 * layout, so the offending body is reported and skipped while any other bodies still load (the
 * partial-results contract). Mirrors C's {@code parse_body_slot} returning {@code INVALID_FLAG} on
 * an unknown slot symbol ({@code init.c}).
 *
 * @author Rowan Crowther
 */
public class BodyAssembler implements Assembler<BodyParseRecord, List<PlayerBody>> {
    /**
     * Assembles the parsed body records into {@link PlayerBody} templates.
     *
     * @param records the raw {@link BodyParseRecord}s in source order
     * @param errors  the soft-error sink; a body with an unresolvable slot type is reported here and
     *                skipped rather than aborting the load
     * @return the assembled {@link PlayerBody}s, each with its slots in declared order
     * @author Rowan Crowther
     */
    @Override
    public List<PlayerBody> assemble(@NotNull List<BodyParseRecord> records, @NotNull List<String> errors) {
        List<PlayerBody> bodies = new ArrayList<>();

        for (BodyParseRecord record : records) {
            int line = record.line();
            String bodyName = record.bodyName();
            List<EquipSlot> equipSlots = new ArrayList<>();
            boolean illegalSlot = false;
            for (BodyParseRecord.BodySlotRecord slot : record.slots()) {
                String slotType = slot.slotType();
                String slotName = slot.slotName();

                try {
                    EquipmentSlotsEnum slotEquipType = EquipmentSlotsEnum.valueOf("EQUIP_" + slotType);
                    equipSlots.add(new EquipSlot(slotEquipType, slotName));
                } catch (IllegalArgumentException e) {
                    errors.add("Body at line: " + line + " has " +
                            "Invalid equipment slot type: " + slotType);
                    illegalSlot = true;
                }
            }

            if (illegalSlot) continue;

            bodies.add(new PlayerBody(bodyName, equipSlots));
        }

        return bodies;
    }
}
