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

package uk.co.jackoftradesltd.backend.parser.blowmethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.enums.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Turns the raw {@link BlowMethodParseRecord}s the grammar produced into domain
 * {@link BlowMethod} objects - the interpretation half of the {@code blow_methods.txt}
 * pipeline, where the deliberately unconverted token text finally becomes booleans and
 * a {@link MessageType}. This is the Java port of the {@code parse_meth_*} handlers in
 * [C] {@code mon-init.c} ({@code parse_meth_name} through {@code parse_meth_desc}), and
 * of the order {@code finish_parse_meth} leaves the records in.
 * <p>
 * There are two conversions, and both reproduce a C reading that is easy to get subtly
 * wrong: the four flags are C {@code uint}s tested for non-zero, and {@code msg:} goes
 * through C's {@code message_lookup_by_name}, which takes a number as well as a name.
 * See {@link #assemble} for each.
 * <p>
 * As C's handlers end the whole {@code blow_methods.txt} load on their first parser error
 * ({@code PARSE_ERROR_NOT_NUMBER}, {@code PARSE_ERROR_INVALID_MESSAGE}), so does this
 * assembler: a bad value is logged and thrown as a {@link DataFileParseException}, and no
 * record is skipped.
 * <p>
 * Accepted deviation (decided 261009): C's {@code blow_methods} array keeps slot 0 blank
 * and starts its records at 1, so {@code z_info->blow_methods_max} is one more than the
 * record count. The list returned here holds only the real records, from index 0.
 *
 * <p>Class BlowMethodAssembler coded before 260915, commented in full on 261107, updated on 261105
 * for the removal of the soft-error path and on 261107 for the optional flags and the
 * {@code act:} order.
 *
 * @author Rowan Crowther
 */
public class BlowMethodAssembler implements Assembler<BlowMethodParseRecord, List<BlowMethod>> {
    /**
     * The logger this assembler writes to: a fatal message, with the same text, just before
     * each {@link DataFileParseException} it throws.
     *
     * <p>Field logger coded before 260915, commented in full on 261107.
     */
    private final static Logger logger = LogManager.getLogger(BlowMethodAssembler.class);

    /**
     * Resolves every raw {@link BlowMethodParseRecord} into a {@link BlowMethod}, and
     * returns them in <em>reverse</em> file order, as C does. C's {@code parse_meth_name}
     * pushes each new record onto the head of its list, and {@code finish_parse_meth}
     * copies that list into the array head first, so {@code blow_methods[1]} is the last
     * method in the file. {@code addFirst} reproduces that.
     *
     * <p>Field by field, against [C] {@code mon-init.c}:
     * <ul>
     *   <li><strong>{@code cut:}, {@code stun:}, {@code miss:} and {@code phys:}</strong>
     *       are each read by {@link ParseUtils#isTrue(String)}, as C's
     *       {@code parser_getuint} reads them with {@code strtoul} in base 0, and then
     *       stored as {@code val ? true : false}. So any non-zero value is true, and
     *       {@code cut:00} is false, as octal 0. A leading {@code -} ends the load, as
     *       {@code parser_parse} refuses one with {@code PARSE_ERROR_NOT_NUMBER}; the
     *       grammar's {@code INTEGER} token lets one through, so
     *       {@link ParseUtils#minusFound(String)} makes the check here, on the raw text, which
     *       is why {@code -0} is refused although its value is 0. A figure outside the range of
     *       an {@code int} ends the load too, where C wraps it into its 32-bit
     *       {@code unsigned int}: {@code cut:4294967296} is 0, and so false, in C, and
     *       {@code cut:4294967295} is non-zero, and so true. This is the accepted deviation
     *       "out-of-{@code int}-range numbers are refused", recorded on {@link ParseUtils}.</li>
     *   <li><strong>A flag the record never named is false.</strong> The directives may come
     *       in any order and any of them may be missing, as in C, where a missing line leaves
     *       the {@code mem_zalloc}'d zero. The grammar seeds each flag as {@code null} and
     *       the guard on each flag, {@code null} or empty, is the port of that zero. Each flag
     *       is guarded by its own field, so a record with {@code cut:0} and no {@code stun:}
     *       gives {@code stun} false and no error. Text that is present is never skipped:
     *       whitespace or a lone sign is still refused.</li>
     *   <li><strong>{@code msg:} is optional and defaults to {@code MSG_GENERIC}.</strong>
     *       C registers it as {@code "msg ?str msg"} and assigns {@code msgt} only when
     *       {@code parser_hasval} is true, leaving the {@code mem_zalloc}'d zero -
     *       {@code MSG_GENERIC}, the first entry in {@code list-message.h} - when it is not.
     *       The guard is the port of that {@code parser_hasval} check, and screens out
     *       {@code null} as well as {@code ""}, because the grammar produces both for an
     *       absent message (see {@link BlowMethodParseRecord}). The null test comes first,
     *       so {@code isEmpty()} is never called on a null.</li>
     *   <li><strong>A present {@code msg:}</strong> is resolved by
     *       {@link MessageType#lookupByName}, the port of [C] {@code message.c}'s
     *       {@code message_lookup_by_name}. That takes a name, matched ignoring case, or a
     *       number below C's {@code MSG_MAX}, so {@code msg:mon_hit} and {@code msg:34} both
     *       give {@code MSG_MON_HIT}. A value it cannot resolve ends the load, as
     *       {@code parse_meth_message_type} returns
     *       {@code PARSE_ERROR_INVALID_MESSAGE}.</li>
     *   <li><strong>{@code act:}</strong> strings are reversed, as C holds them. The grammar
     *       collects them in file order, but C's {@code parse_meth_act_msg} pushes each onto
     *       the head of {@code meth->messages}, so its list is in reverse file order, and
     *       {@code mon-blows.c} picks one by walking {@code randint0(num_messages)} steps
     *       along it. Reversing here makes the same random number pick the same message. The
     *       record's own list is not changed: {@code reversed()} is a view of it.</li>
     *   <li><strong>{@code name:} and {@code desc:}</strong> are copied as the parse record
     *       holds them. The grammar has already joined repeated {@code desc:} lines, as C's
     *       {@code parse_meth_desc} does with {@code string_append}, and a record with no
     *       {@code desc:} carries {@code ""} where C holds {@code NULL}.</li>
     *   <li><strong>Which error is reported first.</strong> C meets the directives in file order
     *       and ends the load at the first bad one. Here each record's values are checked in a
     *       fixed order - {@code cut:}, {@code stun:}, {@code miss:}, {@code phys:}, then
     *       {@code msg:} - after the grammar has read the whole record, so a record with two bad
     *       values can report a different one from C. The load ends either way, with no
     *       {@link BlowMethod} returned; only the text of the exception differs.</li>
     * </ul>
     *
     * <p>Function assemble coded before 260915, commented in full on 261107, rewritten for
     * the load-ending errors, base 0 flags and C's message lookup on 261023, absent flags,
     * the leading {@code -} check, out-of-range refusal and the {@code act:} reversal on 261107, error order noted 261107.
     *
     * @param records  the raw parse records, in file order
     * @param filename the name of the data file, used in the error messages
     * @return the assembled blow methods, in reverse file order
     * @throws DataFileParseException on a flag with a leading {@code -}, no digits or a figure
     *                                outside the range of an {@code int}, or a {@code msg:}
     *                                that names no message type
     */
    @Override
    public List<BlowMethod> assemble(@NotNull List<BlowMethodParseRecord> records,
                                     String filename) {
        List<BlowMethod> methods = new ArrayList<>();

        for (BlowMethodParseRecord record : records) {
            // Line number
            int line = record.line();
            
            // Name of blow method
            String name = record.name();
            
            // Blow - method can create a cut
            boolean cut = false;
            if (record.cut() != null && !record.cut().isEmpty()) {
                Optional<Boolean> cutVal = ParseUtils.isTrue(record.cut());
                if (cutVal.isEmpty() || ParseUtils.minusFound(record.cut())) {
                    String message = "Blow method beginning at line: " + line + " of file: " + filename
                            + " has an invalid cut value: " + record.cut();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                cut = cutVal.get();
            }
            
            // Stun - method can stun
            boolean stun = false;
            if (record.stun() != null && !record.stun().isEmpty()) {
                Optional<Boolean> stunVal = ParseUtils.isTrue(record.stun());
                if (ParseUtils.minusFound(record.stun()) || stunVal.isEmpty()) {
                    String message = "Blow method beginning at line: " + line + " of file: " + filename
                            + " has an invalid stun value: " + record.stun();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                stun = stunVal.get();
            }
            
            // Miss - method can miss
            boolean miss = false;
            if (record.miss() != null && !record.miss().isEmpty()) {
                Optional<Boolean> missVal = ParseUtils.isTrue(record.miss());
                if (ParseUtils.minusFound(record.miss()) || missVal.isEmpty()) {
                    String message = "Blow method beginning at line: " + line + " of file: " + filename
                            + " has an invalid miss value: " + record.miss();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                miss = missVal.get();
            }
            
            // Phys - method is physical
            boolean phys = false;
            if (record.phys() != null && !record.phys().isEmpty()) {
                Optional<Boolean> physVal = ParseUtils.isTrue(record.phys());
                if (ParseUtils.minusFound(record.phys()) || physVal.isEmpty()) {
                    String message = "Blow method beginning at line: " + line + " of file: " + filename
                            + " has an invalid physValue: " + record.phys();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                phys = physVal.get();
            }
            
            // Message type
            MessageType msgT = MessageType.MSG_GENERIC;
            if (record.msg() != null && !record.msg().isEmpty()) {
                msgT = MessageType.lookupByName(record.msg());
                if (msgT == null) {
                    String message = "Blow Method at line: " + line + " of file: " + filename + " has " +
                            "an unrecognized MessageType " + record.msg();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            
            // Act flavour text narrating the blows
            List<String> act = record.act().reversed();
            
            // Description
            String desc = record.desc();

            methods.addFirst(new BlowMethod(name, cut, stun, miss, phys, msgT, act, desc));
        }

        return methods;
    }
}
