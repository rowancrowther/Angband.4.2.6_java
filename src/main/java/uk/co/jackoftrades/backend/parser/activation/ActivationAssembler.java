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

package uk.co.jackoftrades.backend.parser.activation;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.effect.Effect;

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
     * @param records the raw activation records captured by the grammar
     * @param errors  the soft-error channel; assembly failures are appended here and the
     *                offending record is dropped rather than aborting the whole file
     * @return the successfully assembled activations, in source order
     * @author Rowan Crowther
     */
    @Override
    public List<Activation> assemble(@NotNull List<ActivationParseRecord> records, @NotNull List<String> errors) {
        List<Activation> activations = new ArrayList<>();

        for (ActivationParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            // C assigns each activation its position in the flat activations[] table
            // during a final pass (obj-init.c: activations[count].index = count). That
            // flatten-and-number step isn't ported yet, so index is left 0 for now.
            int index = 0;
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

            activations.add(new Activation(name, index, aim, level,
                    power, effects, message, desc));
        }

        return activations;
    }
}
