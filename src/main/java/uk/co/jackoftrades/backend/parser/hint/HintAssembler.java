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

package uk.co.jackoftrades.backend.parser.hint;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.game.Hint;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link HintParseRecord}s from {@code hints.txt} into domain {@link Hint}s.
 *
 * <p>The simplest assembler in the suite: a hint needs no conversion or cross-reference resolution,
 * so this is a straight one-to-one copy of each record's text into a {@link Hint}. The {@code errors}
 * sink is therefore never written to - it is kept only to satisfy the {@link Assembler} contract
 * shared with the readers that do validate.
 *
 * @author Rowan Crowther
 */
public class HintAssembler implements Assembler<HintParseRecord, List<Hint>> {
    /**
     * Copy every parsed record straight into a {@link Hint}, preserving file order.
     *
     * @param records the raw hint records in file order
     * @param errors  the soft-error sink (unused here - hints cannot fail assembly)
     * @return the hints, one per record, in file order
     */
    @Override
    public List<Hint> assemble(@NotNull List<HintParseRecord> records, @NotNull List<String> errors) {
        List<Hint> hints = new ArrayList<>();

        for (HintParseRecord record : records) {
            hints.add(new Hint(record.hint()));
        }

        return hints;
    }
}
