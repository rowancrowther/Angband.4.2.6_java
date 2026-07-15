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

package uk.co.jackoftrades.backend.parser.playerclass;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.game.enums.OptionType;
import uk.co.jackoftrades.middle.game.enums.Options;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.StartItem;
import uk.co.jackoftrades.middle.player.StartOptionExclusion;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ClassEquipParseRecord}s captured from a class's {@code equip:} lines into
 * domain {@link StartItem}s. This is the birth-equipment leg of the class loader; it is
 * self-contained — the item subtype is kept by name and resolved to a concrete kind later (see the
 * note in {@link ClassEquipParseRecord}), so this assembler touches no cross-file registry.
 *
 * <p>Per the shared soft-error contract, a malformed entry appends a message to {@code errors} and
 * is skipped rather than aborting the whole class.
 *
 * @author Rowan Crowther
 */
public class ClassEquipAssembler implements Assembler<ClassEquipParseRecord, List<StartItem>> {
    /**
     * Assembles every parsed {@code equip:} line into a {@link StartItem}, resolving the tval and
     * parsing the quantity range and exclusion options; entries with an unknown tval or a malformed
     * integer are reported and dropped.
     *
     * @param records the raw equipment lines for one class, in file order
     * @param errors  the soft-error channel; per-entry failures are appended here
     * @return the resolved starting items, minus any that failed to resolve
     * @author Rowan Crowther
     */
    @Override
    public List<StartItem> assemble(@NotNull List<ClassEquipParseRecord> records, @NotNull List<String> errors) {
        List<StartItem> results = new ArrayList<>();

        for (ClassEquipParseRecord record : records) {
            int line = record.line();
            TValue tVal = TValue.fromName("TV_" + record.tValue());
            if (tVal == null) {
                errors.add("Starting equipment at line: " + line + " has " +
                        "an invalid TValue: " + record.tValue());
                continue;
            }
            String sValue = record.sValue();
            int min = 0;
            if (!record.min().isEmpty()) {
                try {
                    min = Integer.parseInt(record.min());
                } catch (NumberFormatException e) {
                    errors.add("Starting equipment at line: " + line + " has " +
                            "a malformed minimum integer: " + record.min());
                    continue;
                }
            }
            int max = 0;
            if (!record.max().isEmpty()) {
                try {
                    max = Integer.parseInt(record.max());
                } catch (NumberFormatException e) {
                    errors.add("Starting equipment at line: " + line + " has " +
                            "a malformed maximum integer: " + record.max());
                    continue;
                }
            }
            List<StartOptionExclusion> eopts = parseEopts(record.eopts(), errors);

            results.add(new StartItem(tVal, sValue, min, max, eopts));
        }

        return results;
    }

    /**
     * Parses the {@code eopts} clause of an {@code equip:} line into a list of
     * {@link StartOptionExclusion}s — the birth options that suppress (or, when {@code NOT-}
     * prefixed, require) this item. The literal {@code none} and empty tokens are ignored; the
     * clause may separate options with spaces or {@code |}. Only {@link uk.co.jackoftrades.middle.game.enums.OptionType#OP_BIRTH}
     * options are valid here, so a non-birth or unknown option is reported and skipped, mirroring
     * C's rejection of non-birth options in {@code init_equip}.
     *
     * @param eopts  the raw exclusion clause text
     * @param errors the soft-error channel; unknown/non-birth options are appended here
     * @return the resolved exclusions (possibly empty)
     * @author Rowan Crowther
     */
    @Contract("_, _ -> new")
    @NotNull
    private List<StartOptionExclusion> parseEopts(@NotNull String eopts, @NotNull List<String> errors) {
        List<StartOptionExclusion> result = new ArrayList<>();
        String[] eoptPart = eopts.split("[ |]+");

        for (String part : eoptPart) {
            part = part.trim();
            if (part.isEmpty() || part.equals("none")) continue;

            boolean negated = part.startsWith("NOT-");
            String suffix = negated ? part.substring(4) : part;
            String partFlag = "OPT_" + suffix;
            Options partOption;
            try {
                partOption = Options.valueOf(partFlag);
                if (partOption.getType() != OptionType.OP_BIRTH) {
                    errors.add("Non-birth options are not supported: " + partFlag);
                    continue;
                } else
                    result.add(new StartOptionExclusion(partOption, negated));
            } catch (IllegalArgumentException e) {
                errors.add("Invalid birth option found: " + part);
                continue;
            }
        }

        return result;
    }
}