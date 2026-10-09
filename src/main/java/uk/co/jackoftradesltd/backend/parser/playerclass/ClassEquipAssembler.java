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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.StartItem;
import uk.co.jackoftradesltd.middle.player.StartOptionExclusion;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ClassEquipParseRecord}s captured from a class's {@code equip:} lines into
 * domain {@link StartItem}s. This is the birth-equipment leg of the class loader and the port of
 * {@code parse_class_equip()} in {@code init.c}.
 *
 * <p>It is <em>not</em> self-contained: each entry's subtype is resolved against the loaded object
 * kinds through {@link ObjectRegistry}, as C's {@code lookup_sval()} does at parse time, so the
 * object kinds must be loaded before any class file. {@link ObjectRegistry#lookupObjectKind} throws
 * {@link IllegalStateException} if they are not. The {@link StartItem} still stores the subtype by
 * name; the kind found here is used only to prove the entry resolves.
 *
 * <p>Per the shared soft-error contract, a malformed entry appends a message to {@code errors} and
 * is skipped rather than aborting the whole class, where C stops the parse with the matching
 * {@code PARSE_ERROR_*}. That includes a bad exclusion option: C discards the whole line with
 * {@code PARSE_ERROR_INVALID_OPTION}, and so does this class (see {@link #parseEopts}).
 *
 * <p>Class ClassEquipAssembler coded before 260915, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class ClassEquipAssembler implements Assembler<ClassEquipParseRecord, List<StartItem>> {
    /**
     * Assembles every parsed {@code equip:} line into a {@link StartItem}, resolving the tval and
     * subtype, parsing the quantity range and expanding the exclusion options.
     *
     * <p>An entry is reported and dropped, leaving its neighbours intact, when:
     * <ul>
     *   <li>the tval name is unknown (C: {@code PARSE_ERROR_UNRECOGNISED_TVAL});</li>
     *   <li>no kind has that tval and subtype (C: {@code PARSE_ERROR_UNRECOGNISED_SVAL});</li>
     *   <li>{@code min} or {@code max} is empty, which C reports as a missing field;</li>
     *   <li>{@code min} or {@code max} is not an integer;</li>
     *   <li>{@code min} or {@code max} is negative, which C's {@code uint} field rejects as not a
     *       number; or</li>
     *   <li>{@code min} or {@code max} is above 99 (C: {@code PARSE_ERROR_INVALID_ITEM_NUMBER}); or</li>
     *   <li>the {@code eopts} clause names an option that is unknown or not a birth option (C:
     *       {@code PARSE_ERROR_INVALID_OPTION}), in which case the whole line is dropped even if its
     *       other options are valid.</li>
     * </ul>
     * Like C, it does not check that {@code min} is no greater than {@code max}.
     *
     * <p><b>Order.</b> The result is in <em>reverse</em> file order. C pushes each entry onto the head
     * of the class's {@code start_items} chain, and {@code player_outfit()} walks that chain
     * head-first, so the last {@code equip:} line is given out first.
     *
     * <p>Function assemble coded before 260915, commented in full on 261009.
     *
     * @param records the raw equipment lines for one class, in file order
     * @param errors  the soft-error channel; per-entry failures are appended here
     * @return the resolved starting items in reverse file order, minus any that failed to resolve
     */
    @Override
    public List<StartItem> assemble(@NotNull List<ClassEquipParseRecord> records, @NotNull List<String> errors) {
        List<StartItem> results = new ArrayList<>();

        for (ClassEquipParseRecord record : records) {
            int line = record.line();
            TValue tVal = TValue.fromName(record.tValue());
            if (tVal == null) {
                errors.add("Starting equipment at line: " + line + " has " +
                        "an invalid TValue: " + record.tValue());
                continue;
            }
            String sValue = record.sValue();
            ObjectKind kind = ObjectRegistry.lookupObjectKind(tVal, sValue);
            if (kind == null) {
                errors.add("Starting equipment at line: " + line + " has " +
                        "an unknown tval/sval combination\n" +
                        "TValue: " + record.tValue() +
                        " SValue: " + record.sValue());
                continue;
            }
            int min;
            if (!record.min().isEmpty()) {
                try {
                    min = Integer.parseInt(record.min());
                    if (min < 0 || min > 99) {
                        errors.add("Invalid minimum number for equipment at line: " + line + 
                                " number: " + record.min());
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Starting equipment at line: " + line + " has " +
                            "a malformed minimum integer: " + record.min());
                    continue;
                }
            } else {
                errors.add("Starting equipment at line: " + line + " has " +
                        "no minimum value");
                continue;
            }
            int max;
            if (!record.max().isEmpty()) {
                try {
                    max = Integer.parseInt(record.max());
                    if (max > 99 || max < 0) {
                        errors.add("Invalid maximum number for equipment at line: " + line +
                                " number: " + record.max());
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Starting equipment at line: " + line + " has " +
                            "a malformed maximum integer: " + record.max());
                    continue;
                }
            } else {
                errors.add("Starting equipment at line: " + line + " has " +
                        "no maximum value");
                continue;
            }
            List<StartOptionExclusion> eopts = parseEopts(record.eopts(), errors);
            if (eopts == null) {
                errors.add("Starting equipment at line: " + line + " has " +
                        "a bad eopts line: " + record.eopts());
                continue;
            }

            results.add(new StartItem(tVal, sValue, min, max, eopts));
        }

        return new ArrayList<>(results.reversed());
    }

    /**
     * Parses the {@code eopts} clause of an {@code equip:} line into a list of
     * {@link StartOptionExclusion}s — the birth options that suppress (or, when {@code NOT-}
     * prefixed, require) this item. The literal {@code none} and empty tokens are ignored; the
     * clause may separate options with spaces or {@code |}.
     *
     * <p>Only birth options are valid here, as in {@code parse_class_equip()} ({@code init.c}), so a
     * non-birth or unknown option is an error. C returns {@code PARSE_ERROR_INVALID_OPTION} and
     * discards the whole line, so this method does the same: every token is still checked and each
     * bad one is reported, but the result is {@code null} if any was bad, and the caller drops the
     * entry rather than building it with the exclusions that did resolve.
     *
     * <p>Function parseEopts coded before 260915, commented in full on 261009.
     *
     * @param eopts  the raw exclusion clause text
     * @param errors the soft-error channel; each unknown or non-birth option is appended here
     * @return the resolved exclusions, empty for {@code none}, or {@code null} if any option was bad
     */
    @Nullable
    private List<StartOptionExclusion> parseEopts(@NotNull String eopts, @NotNull List<String> errors) {
        List<StartOptionExclusion> result = new ArrayList<>();
        boolean badEopt = false;
        String[] eoptPart = eopts.split("[ |]+");

        for (String part : eoptPart) {
            part = part.trim();
            if (part.isEmpty() || part.equals("none")) continue;

            boolean negated = part.startsWith("NOT-");
            String suffix = negated ? part.substring(4) : part;
            String partFlag = "OP_" + suffix;
            PlayerOptionEnum partOption;
            try {
                partOption = PlayerOptionEnum.valueOf(partFlag);
                if (partOption.getPlayerOptionType() != PlayerOptionTypes.BIRTH) {
                    errors.add("Non-birth options are not supported: " + partFlag);
                    badEopt = true;
                    continue;
                } else
                    result.add(new StartOptionExclusion(partOption, negated));
            } catch (IllegalArgumentException e) {
                errors.add("Invalid birth option found: " + part);
                badEopt = true;
                continue;
            }
        }
        
        if (badEopt) return null;
        
        return result;
    }
}