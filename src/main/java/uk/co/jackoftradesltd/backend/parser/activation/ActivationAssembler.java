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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.effect.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ActivationParseRecord}s produced by the grammar into resolved
 * domain {@link Activation}s (Java port of {@code parse_activation_*} in
 * {@code src/obj-init.c}). This is where the deferred interpretation happens: the
 * textual {@code level}/{@code power} fields become integers, the {@code aim} flag
 * becomes a boolean, and the effect blocks are handed off to {@link EffectAssembler}.
 *
 * <p><b>Error policy:</b> soft errors follow the suite's skip-and-continue contract — a
 * record with a malformed integer or an unassemblable effect is reported into
 * {@code errors} and dropped ({@code continue}), leaving the remaining activations to
 * load. Hard grammar/lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * @author Rowan Crowther
 */
public class ActivationAssembler implements Assembler<ActivationParseRecord, List<Activation>> {
    /**
     * Assemble the parsed activation records into resolved {@link Activation}s, skipping
     * (with a logged soft error) any record whose integer fields or effects fail to resolve.
     *
     * <p>The result is in reverse file order, because C's parser prepends each activation to a list
     * and {@code finish_parse_act} ({@code obj-init.c}) copies that list into {@code activations[]}
     * from the head: the last record in {@code activation.txt} comes first, as {@code activations[1]}.
     *
     * <p>Function assemble coded before 260915, commented in full on 261013.
     *
     * @param records the raw activation records captured by the grammar
     * @param errors  the soft-error channel; assembly failures are appended here and the
     *                offending record is dropped rather than aborting the whole file
     * @return the successfully assembled activations, in reverse file order (C's {@code activations[]} order)
     */
    @Override
    public List<Activation> assemble(@NotNull List<ActivationParseRecord> records, @NotNull List<String> errors) {
        List<Activation> activations = new ArrayList<>();

        for (ActivationParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            boolean aim = record.aim().equals("1");
            int level = 0;
            if (!record.level().isEmpty()) {
                try {
                    level = Integer.parseInt(record.level());
                } catch (NumberFormatException e) {
                    errors.add("Activation beginning at line: " + line + " has " +
                            "a malformed level integer: " + record.level());
                    continue;
                }
            }
            int power = 0;
            if (!record.power().isEmpty()) {
                try {
                    power = Integer.parseInt(record.power());
                } catch (NumberFormatException e) {
                    errors.add("Activation beginning at line: " + line + " has " +
                            "a malformed power: " + record.power());
                    continue;
                }
            }
            // EffectAssembler is all-or-nothing: if any single effect in the block
            // fails to resolve it returns null (not a partial list), so one bad effect
            // drops the entire activation rather than silently loading it minus a power.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors);
            if (effects == null) continue;
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
