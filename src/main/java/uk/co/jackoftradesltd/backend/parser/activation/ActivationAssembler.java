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

package uk.co.jackoftradesltd.backend.parser.activation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.HelperFunctions;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.effect.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ActivationParseRecord}s produced by the grammar into resolved
 * domain {@link Activation}s. It is the Java port of the {@code parse_act_name},
 * {@code parse_act_aim}, {@code parse_act_level} and {@code parse_act_power} handlers in
 * {@code obj-init.c}, plus the ordering and numbering that {@code finish_parse_act} does in
 * the same file. The grammar only captures text; this is where the deferred reading happens:
 * {@code aim:}, {@code level:} and {@code power:} become numbers, and the {@code effect:}
 * blocks are handed to {@link EffectAssembler}. The {@code msg:} and {@code desc:} text
 * arrives already joined by the grammar, as C's {@code string_append} joins repeated lines,
 * and passes through unchanged ({@code null} when the line is absent, as C leaves it).
 *
 * <p>The other handlers in the same {@code obj-init.c} block are not ported here:
 * {@code parse_act_msg} and {@code parse_act_desc} are done by the {@code activation} rule in
 * {@code ActivationsGrammar.g4}, and {@code parse_act_effect}, {@code parse_act_effect_yx},
 * {@code parse_act_dice} and {@code parse_act_expr} by the {@code effectBlock} rule it imports and
 * by {@link EffectAssembler}. The grammar keeps only the last {@code aim:}, {@code level:} or
 * {@code power:} line of a record, as C's handlers each overwrite their field, so this class only
 * ever sees the final value.
 *
 * <p><b>Reading the numbers.</b> The three fields are read as C's {@code strtol} and
 * {@code strtoul} read them, with base 0:
 * <ul>
 *   <li>A leading {@code 0} means octal, digits {@code 0} to {@code 7}; anything else is decimal.
 *       Reading stops at the first character that is not a digit of that base, so {@code 08} is 0,
 *       {@code 019} is 1 and {@code 12abc} is 12. Text with no digits at all stops the load.</li>
 *   <li>{@code aim:} and {@code power:} are {@code uint} in C, so a leading {@code -} stops the
 *       load, even for {@code -0}. {@code level:} is an {@code int} and may be negative.</li>
 *   <li>{@code aim:} is true for any non-zero number, as {@code val ? true : false} makes it.
 *       An absent {@code aim:}, {@code level:} or {@code power:} is false or 0, as C's
 *       {@code mem_zalloc} leaves it.</li>
 * </ul>
 *
 * <p><b>Error policy.</b> The first bad record stops the load: a number that cannot be read, a
 * negative {@code aim:} or {@code power:}, or an effect {@link EffectAssembler} cannot resolve
 * throws {@link DataFileParseException}, as C's {@code print_error} in {@code datafile.c} quits.
 * Nothing is skipped and nothing goes to the soft-error channel for later. Records are checked in
 * file order, and within a record {@code aim:}, then {@code level:}, then {@code power:}, then the
 * effects. {@link EffectAssembler} still appends to {@code errors} and returns {@code null} for a
 * bad effect, and this class turns that {@code null} into the throw. Hard grammar and lexer errors
 * are handled upstream by {@code GrammarDriver}.
 *
 * <p><b>Accepted deviations</b> (all in {@code docs/Accepted_Deviations.md}, section Activation):
 * <ul>
 *   <li>A number too big for its C type is clamped to {@code Integer.MIN_VALUE} or
 *       {@code Integer.MAX_VALUE}, where C keeps the low 32 bits. This covers {@code aim:},
 *       {@code level:} and {@code power:}.</li>
 *   <li>Every message names the line of the record's {@code name:}, where C names the line of the
 *       directive that failed.</li>
 *   <li>A record with two faults reports the first the assembler reaches, in the order above, not
 *       the one nearest the top of the block.</li>
 * </ul>
 * The list is also base 0 and holds only real records, the accepted base-0 deviation, so each
 * activation's index is one lower than C's {@code activations[]} slot.
 *
 * <p>Class ActivationAssembler coded before 260915, commented in full on 261022.
 *
 * @author Rowan Crowther
 */
public class ActivationAssembler implements Assembler<ActivationParseRecord, List<Activation>> {
    /**
     * The logger this assembler writes its fatal messages to, just before it throws
     * {@link DataFileParseException}. The thrown message carries the same text.
     *
     * <p>Field logger coded before 260915, commented in full on 261022.
     */
    private static final Logger logger = LogManager.getLogger(ActivationAssembler.class);

    /**
     * Assemble the parsed activation records into resolved {@link Activation}s, one per record.
     * A record that cannot be resolved stops the load with a {@link DataFileParseException} that
     * names the file and the line the record starts on; no record is skipped.
     *
     * <p>For each record in file order it checks {@code aim:}, {@code level:} and {@code power:}
     * (see the class block for how the numbers are read and which values are refused), hands the
     * effect blocks to {@link EffectAssembler}, and builds the {@link Activation} with the
     * {@code msg:} and {@code desc:} text as the grammar joined it.
     *
     * <p>The result is in reverse file order, because C's parser prepends each activation to a list
     * and {@code finish_parse_act} ({@code obj-init.c}) copies that list into {@code activations[]}
     * from the head: the last record in {@code activation.txt} comes first, as {@code activations[1]}.
     * Each activation is then numbered by its place in that list, from 0 where C counts from 1.
     *
     * <p>Function assemble coded before 260915, commented in full on 261022, updated on 261022 to
     * stop the load on a bad record.
     *
     * @param records  the raw activation records captured by the grammar
     * @param errors   the soft-error channel, passed on to {@link EffectAssembler}, which appends to it
     *                 before returning {@code null} for a bad effect; this method never skips a record
     *                 and adds nothing to it itself
     * @param filename the file the records came from, named in every stop-the-load message
     * @return the assembled activations, in reverse file order (C's {@code activations[]} order)
     * @throws DataFileParseException if a number cannot be read, {@code aim:} or {@code power:} is negative,
     *                                or an effect does not resolve
     */
    @Override
    public List<Activation> assemble(@NotNull List<ActivationParseRecord> records, @NotNull List<String> errors,
                                     String filename) {
        List<Activation> activations = new ArrayList<>();

        for (ActivationParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            boolean aim = false;
            if (!record.aim().isEmpty()) {
                if (record.aim().charAt(0) == '-') {
                    String message = "Invalid value for aim: for Activation beginning at " +
                            "line: " + line + " of file: " + filename + " value is: '" +
                            record.aim() + "'";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                int aimValue = HelperFunctions.parseIntString("Activation", record.aim(), line, filename, "aim");
                aim = aimValue != 0;
            }
            int level = 0;
            if (!record.level().isEmpty()) {
                level = HelperFunctions.parseIntString("Activation", record.level(), line, filename, "level");
            }
            int power = 0;
            if (!record.power().isEmpty()) {
                if (record.power().charAt(0) == '-') {
                    String message = "Activation beginning at line: " + line +
                            " of file: " + filename + " has " +
                            "a negative power: " + record.power();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                power = HelperFunctions.parseIntString("Activation", record.power(), line, filename, "power");
            }
            if (power < 0) {
                String message = "Activation beginning at line: " + line +
                        " of file: " + filename + " has " +
                        "a negative power: " + power;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            // EffectAssembler is all-or-nothing: if any single effect in the block
            // fails to resolve it returns null (not a partial list), so one bad effect
            // stops the load rather than silently loading the activation minus a power.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            if (effects == null) {
                String message = "A malformed effect was found in the Activation beginning at line: " +
                        line + " of file: " + filename;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            String message = record.message();
            String desc = record.desc();

            activations.addFirst(new Activation(name, 0, aim, level,
                    power, effects, message, desc));
        }

        int index = 0;
        for (Activation a : activations) {
            a.setIndex(index);
            index++;
        }
        
        return activations;
    }
}
