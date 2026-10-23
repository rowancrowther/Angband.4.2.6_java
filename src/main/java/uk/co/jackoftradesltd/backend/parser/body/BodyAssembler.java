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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.PlayerBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns each {@link BodyParseRecord} parsed from {@code body.txt} into a {@link PlayerBody} —
 * a named, ordered set of equipment slots. This is the Java port of the two record handlers in
 * [C] {@code init.c}, {@code parse_body_body} and {@code parse_body_slot}.
 *
 * <p>Every record is one body: a name plus its {@code slot:} lines in file order. For each slot the
 * raw type string is resolved to an {@link EquipmentSlotsEnum} ({@code "EQUIP_" + type}) and paired
 * with its display name into an {@link EquipSlot}. The slots are kept in declared order, as C's
 * {@code parse_body_slot} appends each new slot to the tail of the body's list, and a slot's index
 * is its identity (C's gear system addresses equipment by slot number).
 *
 * <p>As C's {@code parse_body_slot} ends the whole {@code body.txt} load with
 * {@code PARSE_ERROR_INVALID_FLAG} on a slot type it cannot find, so does this assembler: the bad
 * value is logged and thrown as a {@link DataFileParseException} rather than reported as a soft
 * error, and nothing is added to the caller's error list.
 *
 * <p>Class BodyAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class BodyAssembler implements Assembler<BodyParseRecord, List<PlayerBody>> {
    /**
     * The logger this assembler writes to: a fatal message, with the same text, just before each
     * {@link DataFileParseException} it throws.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(BodyAssembler.class);

    /**
     * Assembles the parsed body records into {@link PlayerBody} templates, one per record, in the
     * order the records arrive. That order is already C's: {@code parse_body_body} pushes each new
     * body onto the head of its list, so {@code bodies} runs in <em>reverse</em> file order, and the
     * grammar reproduces that with {@code addFirst}; this method does not reorder them.
     *
     * <p>Each slot's type is resolved as C's {@code parse_body_slot} resolves it with
     * {@code lookup_flag} against the {@code slots[]} table built from {@code list-equip-slots.h}:
     * <ul>
     *   <li><strong>A listed type</strong> such as {@code WEAPON} or {@code BODY_ARMOR} becomes
     *       {@code EQUIP_WEAPON} or {@code EQUIP_BODY_ARMOR}. The match is case-sensitive in both,
     *       as C uses {@code streq} and {@code valueOf} is exact.</li>
     *   <li><strong>{@code NONE}</strong> ends the load. C's table does hold it, at index 0, but
     *       {@code lookup_flag} starts its search at {@code FLAG_START} (1), runs off the end and
     *       returns {@code FLAG_END} (0), which {@code parse_body_slot} rejects. {@code EQUIP_NONE}
     *       exists in the Java enum, so it is refused here by name.</li>
     *   <li><strong>{@code MAX}</strong> ends the load. It is not in C's table at all, but
     *       {@code EQUIP_MAX} is a member of the Java enum, so it too is refused by name.</li>
     *   <li><strong>Any other type</strong> ends the load, through the
     *       {@link IllegalArgumentException} that {@code valueOf} throws.</li>
     * </ul>
     * A repeated type is kept as a separate slot, as C keeps it: the stock Humanoid body has two
     * {@code RING} slots. The slot count, C's {@code b->count++} per slot, is the size of the list
     * handed to {@link PlayerBody#setSlots}.
     *
     * <p>Function assemble coded before 260915, commented in full on 260915, rewritten for the
     * load-ending errors on 261023.
     *
     * @param records  the raw {@link BodyParseRecord}s, in the order the grammar produced them
     * @param errors   unused: every error ends the load by throwing instead
     * @param filename the name of the data file, used in the error messages
     * @return the assembled {@link PlayerBody}s, in record order, each with its slots in declared
     *         order
     * @throws DataFileParseException on a slot type of {@code NONE}, {@code MAX}, or one that names
     *                                no {@link EquipmentSlotsEnum} member
     */
    @Override
    public List<PlayerBody> assemble(@NotNull List<BodyParseRecord> records, @NotNull List<String> errors,
                                     String filename) {
        List<PlayerBody> bodies = new ArrayList<>();

        for (BodyParseRecord record : records) {
            int line = record.line();
            String bodyName = record.bodyName();
            List<EquipSlot> equipSlots = new ArrayList<>();
            for (BodyParseRecord.BodySlotRecord slot : record.slots()) {
                String slotType = slot.slotType();
                String slotName = slot.slotName();
                String errorMessage = "Body beginning at line: " + line + " of file: " + filename
                        + " has an invalid equipment slot value: " + slot.slotName() + ":" + slot.slotType();

                try {
                    if (slotType.equals("NONE") || slotType.equals("MAX")) {
                        logger.fatal(errorMessage);
                        throw new DataFileParseException(errorMessage);
                    }
                    EquipmentSlotsEnum slotEquipType = EquipmentSlotsEnum.valueOf("EQUIP_" + slotType);
                    equipSlots.add(new EquipSlot(slotEquipType, slotName));
                } catch (IllegalArgumentException e) {
                    logger.fatal(errorMessage);
                    throw new DataFileParseException(errorMessage);
                }
            }

            PlayerBody body = new PlayerBody(bodyName);
            body.setSlots(equipSlots);
            bodies.add(body);
        }

        return bodies;
    }
}
