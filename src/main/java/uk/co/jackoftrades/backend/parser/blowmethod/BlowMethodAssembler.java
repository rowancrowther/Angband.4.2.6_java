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

package uk.co.jackoftrades.backend.parser.blowmethod;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link BlowMethodParseRecord}s the grammar produced into domain
 * {@link BlowMethod} objects - the interpretation half of the {@code blow_methods.txt}
 * pipeline, where the deliberately unconverted token text finally becomes booleans and
 * a {@link MessageType}.
 * <p>
 * There are only two conversions, but both reproduce a C behaviour that is easy to get
 * subtly wrong; see {@link #assemble} for each.
 * <p>
 * Failures follow the suite's partial-results contract: an unresolvable value is a
 * <em>soft</em> error, appended to the caller's list with the offending line number,
 * and the record is skipped rather than aborting the load. (C is stricter here - it
 * returns {@code PARSE_ERROR_INVALID_MESSAGE} and the file load fails outright - but
 * {@code GameConstants} gates on {@code hasErrors()} after the fact, so a bad file
 * still refuses to load; it just reports every problem in it rather than only the first.)
 *
 * @author Rowan Crowther
 */
public class BlowMethodAssembler implements Assembler<BlowMethodParseRecord, List<BlowMethod>> {
    /**
     * Assemble every parsed record into a {@link BlowMethod}, skipping any whose
     * {@code msg:} cannot be resolved.
     * <p>
     * <strong>The boolean directives use C truthiness.</strong> C stores these with
     * {@code val ? true : false} ({@code mon-init.c}, {@code parse_meth_cut} and its
     * siblings), so anything other than zero is true - not only {@code 1}. Testing
     * {@code !equals("0")} rather than {@code equals("1")} is what preserves that: a
     * hypothetical {@code cut:2} reads as true here exactly as it would in C.
     * <p>
     * <strong>{@code msg:} is optional and defaults to {@code MSG_GENERIC}.</strong> C
     * registers it as {@code "msg ?str msg"} and only assigns {@code msgt} when
     * {@code parser_hasval} is true, leaving the {@code mem_zalloc}'d zero - which is
     * {@code MSG_GENERIC}, the first entry in {@code list-message.h} - when it is not.
     * The guard below is the port of that {@code parser_hasval} check, and must screen
     * out {@code null} as well as {@code ""} because the grammar produces both for an
     * absent message (see {@link BlowMethodParseRecord}). Order matters: testing
     * {@code isEmpty()} first would dereference the null, and a null reaching
     * {@link MessageType#valueOf} would silently look up {@code "MSG_null"} and drop the
     * record as unrecognised.
     *
     * @param records the raw parse records, in file order
     * @param errors  the soft-error sink, mutated in place
     * @return the assembled blow methods, excluding any record that was skipped
     * @author Rowan Crowther
     */
    @Override
    public List<BlowMethod> assemble(@NotNull List<BlowMethodParseRecord> records, @NotNull List<String> errors) {
        List<BlowMethod> methods = new ArrayList<>();

        for (BlowMethodParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            boolean cut = !record.cut().equals("0");
            boolean stun = !record.stun().equals("0");
            boolean miss = !record.miss().equals("0");
            boolean phys = !record.phys().equals("0");
            MessageType msgT = MessageType.MSG_GENERIC;
            if (record.msg() != null && !record.msg().isEmpty()) {
                try {
                    msgT = MessageType.valueOf("MSG_" + record.msg());
                } catch (IllegalArgumentException e) {
                    errors.add("Blow Method at line: " + line + " has " +
                            "an unrecognized MessageType " + record.msg());
                    continue;
                }
            }
            List<String> act = record.act();
            String desc = record.desc();

            methods.add(new BlowMethod(name, cut, stun, miss, phys, msgT, act, desc));
        }

        return methods;
    }
}
