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

package uk.co.jackoftradesltd.backend.parser.chesttrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.objects.ChestTrap;
import uk.co.jackoftradesltd.middle.objects.enums.ChestTrapCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns each {@link ChestTrapParseRecord} parsed from {@code chest_trap.txt} into a
 * {@link ChestTrap}, resolving the fields the grammar left as text: the code, the level and the
 * {@code destroy:} and {@code magic:} flags, plus the effect blocks through
 * {@link EffectAssembler}. This is the Java port of the record handlers in [C]
 * {@code obj-chest.c}, {@code parse_chest_trap_name} through {@code parse_chest_trap_msg_death},
 * gathered into one place by the suite's "grammars extract, assemblers resolve" split.
 *
 * <p><strong>The file must match {@link ChestTrapCode} record for record.</strong> C gives each
 * trap its pval bit from its position in the file (the doubling in {@code parse_chest_trap_name})
 * and never reads {@code code:} again, so it loads any number of traps in any order. Here the bit
 * is {@code 1 << ordinal()} on {@link ChestTrapCode}, so a record whose code is not the constant
 * at its position, a file with more or fewer records than there are constants, or a level lower
 * than the record before it ends the load. That is the accepted deviation "{@code chest_trap.txt}
 * must match {@code ChestTrapCode} record for record, with levels that never descend" in
 * {@code docs/Accepted_Deviations.md}; it enforces the rules C relies on but only states in the
 * comments at the top of {@code chest_trap.txt}.
 *
 * <p><strong>Every error ends the load.</strong> As C's handlers return
 * {@code PARSE_ERROR_NOT_NUMBER} or an effect error from {@code grab_effect_data} and stop the
 * whole {@code chest_trap.txt} load, each problem is logged and thrown as a
 * {@link DataFileParseException} at the first record that has it. No partial list is ever
 * returned.
 *
 * <p>Class ChestTrapAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class ChestTrapAssembler implements Assembler<ChestTrapParseRecord, List<ChestTrap>> {
    /**
     * The logger this assembler writes to: a fatal message, with the same text, just before each
     * {@link DataFileParseException} it throws.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(ChestTrapAssembler.class);

    /**
     * Assembles the parsed chest-trap records into {@link ChestTrap}s, one per record, in file
     * order. File order is also C's {@code chest_traps} list order, because
     * {@code parse_chest_trap_name} appends each new trap to the tail.
     *
     * <p>For each record, in this order:
     * <ul>
     *   <li><strong>The code</strong> must name a {@link ChestTrapCode} constant, matched
     *       case-sensitively by {@code valueOf}, and must be the constant at the record's position.
     *       A record past the last constant ends the load before its code is compared. C stores
     *       {@code code:} as a plain string and checks none of this.</li>
     *   <li><strong>The level</strong> is read as C's {@code parser_getint} reads it,
     *       {@code strtol} in base 0, through
     *       {@link ParseUtils#parseIntString(String, String, int, String, String)}, so
     *       {@code level:010} is 8. It must not be lower than the previous record's level, and the
     *       first record's is compared against 0, so a negative first level ends the load. An
     *       absent {@code level:} gives 0, the value C's {@code mem_zalloc} leaves.</li>
     *   <li><strong>The effects</strong>, when the record has any, go to
     *       {@link EffectAssembler#assemble}. A {@code null} back means a block failed to resolve
     *       and ends the load, as an error from {@code grab_effect_data} ends it in C. A record
     *       with no effect block, such as {@code locked}, gets an empty list where C leaves
     *       {@code effect} {@code NULL}.</li>
     *   <li><strong>{@code destroy:} and {@code magic:}</strong> are true for any non-zero value,
     *       as {@code parse_chest_trap_destroy} and {@code parse_chest_trap_magic} test
     *       {@code if (val)}, and false when absent.</li>
     *   <li><strong>{@code msg:} and {@code msg-death:}</strong> are copied as they are, with
     *       {@code ""} when absent where C leaves {@code NULL}. Code that reads them must treat
     *       {@code ""} as C's {@code if (trap->msg)} treats {@code NULL}: no message.</li>
     * </ul>
     *
     * <p>A value too big for an {@code int} is clamped to {@link Integer#MAX_VALUE} where C wraps
     * it, the accepted deviation recorded in {@link ParseUtils}.
     *
     * <p>The {@code null} guards on the code, {@code destroy:} and {@code magic:} cover a
     * directive that was present but malformed, which the grammar passes on as {@code null}. They
     * cannot fire from {@code ChestTrapReader}, which throws on any syntax error before this runs;
     * {@code level:} has no such guard, so a {@code null} level built by hand would throw
     * {@link NullPointerException} rather than {@link DataFileParseException}.
     *
     * <p>Function assemble coded before 260915, rewritten for the load-ending errors on 261023,
     * commented in full on 261023.
     *
     * @param records  the raw {@link ChestTrapParseRecord}s, in file order, expected in
     *                 {@link ChestTrapCode} declaration order
     * @param errors   passed on to {@link EffectAssembler#assemble}, which adds the reason an
     *                 effect block failed before the load ends; this method adds nothing itself
     * @param filename the name of the data file, used in the error messages
     * @return the assembled chest traps, one per {@link ChestTrapCode} constant, in file order
     * @throws DataFileParseException on a code that is missing, names no {@link ChestTrapCode}
     *                                constant or is out of declaration order; on more or fewer
     *                                records than there are constants; on a level lower than the
     *                                one before it; on a number with no leading digits; or on an
     *                                effect block that fails to resolve
     */
    @Override
    public List<ChestTrap> assemble(@NotNull List<ChestTrapParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<ChestTrap> chestTraps = new ArrayList<>();
        ChestTrapCode[] expected = ChestTrapCode.values();
        int position = 0;
        int previousLevel = 0;

        for (ChestTrapParseRecord record : records) {
            int index = position++;

            int line = record.line();
            String name = record.name() == null ? "" : record.name();
            String codeStr = record.code();
            if (codeStr == null) {
                String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid code value: " + record.code();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            ChestTrapCode trapCode = null;
            if (!codeStr.isEmpty()) {
                try {
                    trapCode = ChestTrapCode.valueOf(codeStr);
                } catch (IllegalArgumentException e) {
                    String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid trap code value: " + record.code();
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            }
            if (index >= expected.length) {
                String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid index (out of bounds) value: " + index + " out of " + expected.length;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            if (trapCode != expected[index]) {
                String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid code value: " + record.code() + " should be " + expected[index];
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int level = ParseUtils.parseIntString("Chest trap", record.level(), line, filename, "level");
            if (level < previousLevel) {
                String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid level value: " + record.code() + " level values must not descend";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            previousLevel = level;
            List<Effect> effects = new ArrayList<>();
            if (record.effect() != null && !record.effect().isEmpty()) {
                effects = EffectAssembler.assemble(record.effect(), errors, filename);
                if (effects == null) {
                    String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                            "no valid effects";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            if (record.destroy() == null) {
                String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid destroy value: null";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int destroyInt = ParseUtils.parseIntString("Chest trap", record.destroy(), line, filename, "destroy");
            boolean destroy = destroyInt != 0;
            if (record.magic() == null) {
                String message = "Chest Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid magic value: null";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int magicInt = ParseUtils.parseIntString("Chest trap", record.magic(), line, filename, "magic");
            boolean magic = magicInt != 0;
            String msg = record.msg() == null ? "" : record.msg();
            String msgDeath = record.msgDeath() == null ? "" : record.msgDeath();

            chestTraps.add(new ChestTrap(name, trapCode, level, effects, destroy, magic, msg, msgDeath));
        }

        if (chestTraps.size() != expected.length) {
            String message = "Chest Trap datafile: " + filename + " has " +
                    "an invalid number of traps. Expected: " + expected.length +
                    " received: " + chestTraps.size();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        return chestTraps;
    }
}