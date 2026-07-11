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

public class ActivationAssembler implements Assembler<ActivationParseRecord, List<Activation>> {
    /**
     *
     * @param records List of R_ParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of R objects
     */
    @Override
    public List<Activation> assemble(@NotNull List<ActivationParseRecord> records, @NotNull List<String> errors) {
        List<Activation> activations = new ArrayList<>();

        for (ActivationParseRecord record : records) {
            int line = record.line();
            String name = record.name();
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
