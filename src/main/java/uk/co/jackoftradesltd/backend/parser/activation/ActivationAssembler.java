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
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.effect.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
 *   <li>{@code power:} is a {@code uint} in C, so a leading {@code -} stops the load, even for
 *       {@code -0}. {@code level:} is an {@code int} and may be negative. {@code aim:} is also a
 *       {@code uint} in C, but here its sign is read and not refused (see the accepted deviations
 *       below).</li>
 *   <li>{@code aim:} is true for any non-zero number, as {@code val ? true : false} makes it.
 *       An absent {@code aim:}, {@code level:} or {@code power:} is false or 0, as C's
 *       {@code mem_zalloc} leaves it. The empty text of an absent {@code aim:} is never handed to
 *       {@link ParseUtils#isTrue(String)}, which would call it not a number.</li>
 * </ul>
 *
 * <p><b>Error policy.</b> The first bad record stops the load: a number that cannot be read, a
 * negative {@code power:}, or an effect {@link EffectAssembler} cannot resolve
 * throws {@link DataFileParseException}, as C's {@code print_error} in {@code datafile.c} quits.
 * Nothing is skipped. Records are checked in
 * file order, and within a record {@code aim:}, then {@code level:}, then {@code power:}, then the
 * effects. A bad effect throws from inside {@link EffectAssembler#assemble}, which names the
 * effect's own line; that method never returns a partial list and never returns {@code null}.
 * Hard grammar and lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * <p><b>Outstanding.</b> Three things in this file, all checked against the code and against
 * {@code obj-init.c} and {@code parser.c}:
 * <ul>
 *   <li>The grammar keeps only the last {@code aim:} or {@code power:} line of a record, but C
 *       reads every line and stops at the first one it refuses. A record with {@code power:-3}
 *       followed by {@code power:5} stops the load in C ({@code *tok == '-'} in
 *       {@code parser_parse()} gives {@code PARSE_ERROR_NOT_NUMBER}) and loads with power 5 here,
 *       because this class never sees the first line. The fix is in {@code ActivationsGrammar.g4}
 *       and {@link ActivationParseRecord}, not here, and it is not yet in
 *       {@code docs/Accepted_Deviations.md}. None on shipped data.</li>
 *   <li>The {@code effects == null} test in {@link #assemble} cannot run: it follows an
 *       {@link EffectAssembler#assemble} that no longer returns {@code null}.</li>
 *   <li>Text with white space in front of the {@code -} ({@code " -5"}) gets past the first-character
 *       {@code -} test on {@code power:}, as it does in C, but the reading then makes it negative
 *       and the {@code power < 0} test refuses it. C's {@code strtoul} wraps it to a huge unsigned
 *       number, which the {@code int} field keeps as -5, so C loads it. The lexer never produces
 *       white space in a number, so no data file reaches this; the test is otherwise unreachable.</li>
 * </ul>
 *
 * <p><b>Accepted deviations</b> (all in {@code docs/Accepted_Deviations.md}, section Activation):
 * <ul>
 *   <li>A {@code level:} or {@code power:} too big for its C type is clamped to
 *       {@code Integer.MIN_VALUE} or {@code Integer.MAX_VALUE}, where C keeps the low 32 bits.</li>
 *   <li>An {@code aim:} outside the {@code int} range ({@code 2147483648}, {@code 4294967296}) stops
 *       the load, where C keeps the low 32 bits and reads {@code 4294967296} as 0, so false. This is
 *       the 261027 refusal of out-of-range numbers, made by {@link ParseUtils#isTrue(String)}.</li>
 *   <li>A leading {@code -} on {@code aim:} is read as a sign and not refused (accepted 261107):
 *       {@code aim:-1} loads as true and {@code aim:-0} as false, where C stops the load on
 *       any {@code uint} token that starts with {@code -}. {@code aim:-} with no digit and
 *       {@code aim:-abc} are still refused.</li>
 *   <li>Every message names the line of the record's {@code name:}, where C names the line of the
 *       directive that failed.</li>
 *   <li>A record with two faults reports the first the assembler reaches, in the order above, not
 *       the one nearest the top of the block.</li>
 * </ul>
 * The list is also base 0 and holds only real records, the accepted base-0 deviation, so each
 * activation's index is one lower than C's {@code activations[]} slot. For the same reason there is
 * no counter to port: C's {@code finish_parse_act} sets {@code z_info->act_max} to the record count
 * plus one for the blank slot 0, and here the count is simply the size of the returned list.
 *
 * <p>Class ActivationAssembler coded before 260915, commented in full on 261102 (error policy
 * brought into line with {@link EffectAssembler} throwing, outstanding notes added), updated on
 * 261105 for the removal of the soft-error path, updated on 261107 for the absent {@code aim:}
 * guard and the two {@code aim:} deviations, re-checked against {@code obj-init.c} and
 * {@code parser.c} on 261107 (the {@code act_max} note added).
 *
 * @author Rowan Crowther
 */
public class ActivationAssembler implements Assembler<ActivationParseRecord, List<Activation>> {
    /**
     * The logger this assembler writes its fatal messages to, just before it throws
     * {@link DataFileParseException}. The thrown message carries the same text.
     *
     * <p>Field logger coded before 260915, commented in full on 261102.
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
     * The {@code 0} handed to the {@link Activation} constructor is only a placeholder: the index
     * is not known until the list is complete, so the second loop sets the real one with
     * {@link Activation#setIndex(int)}, as {@code finish_parse_act} sets {@code index} when it
     * copies each node.
     *
     * <p>An absent {@code aim:} (empty text) leaves {@code aim} false without calling
     * {@link ParseUtils#isTrue(String)}; any other text that does not read as a number stops the
     * load, as does a negative {@code power:}. A {@code power:} whose first character is
     * {@code -} is refused before it is read, as C refuses a {@code uint} token that starts with
     * {@code -}; the {@code power < 0} test after it catches only the white-space case described in
     * the class block, so for text the lexer can produce it never fires.
     *
     * <p>Function assemble coded before 260915, commented in full on 261102, updated on 261022 to
     * stop the load on a bad record, updated on 261107 for the absent {@code aim:} guard, re-checked
     * and expanded on 261107 (placeholder index and the two {@code power:} tests explained).
     *
     * @param records  the raw activation records captured by the grammar
     * @param filename the file the records came from, named in every stop-the-load message
     * @return the assembled activations, in reverse file order (C's {@code activations[]} order)
     * @throws DataFileParseException if a number cannot be read, {@code power:} is negative,
     *                                or an effect does not resolve (thrown by {@link EffectAssembler})
     */
    @Override
    public List<Activation> assemble(@NotNull List<ActivationParseRecord> records, 
                                     String filename) {
        List<Activation> activations = new ArrayList<>();

        for (ActivationParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            boolean aim = false;
            if (!record.aim().isEmpty()) {
                Optional<Boolean> aimVal = ParseUtils.isTrue(record.aim());
                if (aimVal.isEmpty()) {
                    String message = "Activation beginning at " + "line: " + line + " of file: " + filename + " has " +
                            "an invalid aim value: '" + record.aim() + "'";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                aim = aimVal.get();
            }
            int level = 0;
            if (!record.level().isEmpty()) {
                level = ParseUtils.parseIntString("Activation", record.level(), line, filename, "level");
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
                power = ParseUtils.parseIntString("Activation", record.power(), line, filename, "power");
            }
            if (power < 0) {
                String message = "Activation beginning at line: " + line +
                        " of file: " + filename + " has " +
                        "a negative power: " + power;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            // EffectAssembler is all-or-nothing: if any single effect in the block
            // fails to resolve it throws (not a partial list), so one bad effect
            // stops the load rather than silently loading the activation minus a power.
            // The null test below is a leftover from when it returned null instead.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), filename);
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
