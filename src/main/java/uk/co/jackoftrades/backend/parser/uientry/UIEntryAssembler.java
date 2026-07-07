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

package uk.co.jackoftrades.backend.parser.uientry;


import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Second stage of the {@code ui_entry.txt} pipeline: turns the raw string
 * {@link UIEntryParseRecord}s emitted by the {@code UIEntryGrammar} parser into
 * fully resolved {@link UIEntry} objects. This is where the domain look-ups the
 * grammar deliberately avoids finally happen - resolving the {@code parameter}
 * element, the {@code renderer} and {@code template} against the
 * {@link GameConstants} registries, and the {@code combine} and {@code flags}
 * names against their enums.
 * <p>
 * The mapping is not strictly one entry per record. A record carrying
 * {@code parameter:stat} is <em>expanded</em> here into one entry per player
 * stat - {@code stat_mod_ui_compact_0<STR>} through {@code <CON>} - each with the
 * per-stat priority resolved from the record's priority scheme, mirroring the C
 * loader's {@code parameter:} expansion in {@code ui-entry.c}. This is what lets a
 * later {@code bindui} look-up by the full tagged name resolve. Every other record
 * maps to a single entry. (Element expansion is not yet ported; the specialized
 * {@code resist_ui_compact_0<TAG>} rows are carried through individually.)
 * <p>
 * Every field the record left blank is treated as optional and defaulted here
 * rather than rejected: an empty {@code parameter} becomes
 * {@link ElementEnum#ELEM_NONE}, an empty {@code combine} becomes
 * {@link CombinerName#NONE}, and an empty {@code renderer}/{@code template}/
 * {@code flags}/{@code priority} leaves the corresponding value {@code null},
 * zero or absent. Only a field that is <em>present but unresolvable</em> is an
 * error.
 * <p>
 * Assembly is best-effort and error-collecting rather than fail-fast: a record
 * whose renderer, combiner, parameter, priority, flag or template is present
 * but cannot be resolved is skipped with a message appended to {@code errors}
 * (quoting its source line), and processing continues with the next record, so
 * one bad block does not hide the rest.
 *
 * @author Rowan Crowther
 */
public class UIEntryAssembler implements Assembler<UIEntryParseRecord, List<UIEntry>> {
    /**
     * Resolve each {@link UIEntryParseRecord} into one or more {@link UIEntry}
     * objects, skipping (never throwing on) any record whose present fields fail
     * to resolve. Most records yield a single entry; a {@code parameter:stat}
     * record yields one per player stat (see the class comment on expansion).
     *
     * @param records the raw parse records, in file order, from the grammar.
     * @param errors  the soft-error sink; one message is appended, quoting the
     *                record's source line, for each record dropped because a
     *                present field could not be resolved. Mutated in place.
     * @return the successfully assembled {@link UIEntry} objects in file order -
     * the per-stat entries of an expanded {@code parameter:stat} record appear
     * consecutively where that record sat - omitting any record that was skipped.
     */
    @Override
    public List<UIEntry> assemble(@NotNull List<UIEntryParseRecord> records,
                                  @NotNull List<String> errors) {
        List<UIEntry> results = new ArrayList<>();

        for (UIEntryParseRecord record : records) {
            int line = record.line();

            ElementEnum parameter = ElementEnum.ELEM_NONE;
            if (!record.nameTag().isEmpty()) {
                try {
                    parameter = ElementEnum.valueOf("ELEM_" + record.nameTag());
                } catch (IllegalArgumentException e) {
                    errors.add("Block starting on line " + line + ": " +
                            "has illegal parameter element " + record.nameTag());
                    continue;
                }
            }
            String name = record.name();
            UIEntry.StatElemType statElemType = switch (record.parameter()) {
                case "stat" -> UIEntry.StatElemType.STAT;
                case "element" -> UIEntry.StatElemType.ELEMENT;
                case "" -> UIEntry.StatElemType.NONE;
                default -> {
                    errors.add("Block starting on line: " + line +
                            " has illegal parameter kind: " + record.parameter());
                    yield null;
                }
            };
            if (statElemType == null) continue;
            String rendStr = record.renderer();
            UIEntryRenderer renderer;
            if (!rendStr.isEmpty()) {
                renderer = GameConstants.getUIEntryRenderer(rendStr, errors);
                if (renderer == null) continue;
            } else {
                renderer = null;
            }
            CombinerName combinerName;
            if (record.combine().isEmpty())
                combinerName = CombinerName.NONE;
            else {
                try {
                    combinerName = CombinerName.valueOf(record.combine());
                } catch (IllegalArgumentException e) {
                    errors.add("Block starting on line: " + line
                            + " has illegal combiner enum value: " + record.combine());
                    continue;
                }
            }
            // priority must be index or negative_index or an integer
            int priorityNum = 0;
            String priorityStr = "";
            if (!record.priority().isEmpty()) {
                if (record.priority().equals("index") || record.priority().equals("negative_index")) {
                    priorityStr = record.priority();
                    priorityNum = 0;
                } else {
                    priorityStr = "";
                    try {
                        priorityNum = Integer.parseInt(record.priority());
                    } catch (NumberFormatException e) {
                        errors.add("Block starting on line: " + line +
                                " has illegal priority number value: " + record.priority());
                        continue;
                    }
                }
            }
            EntryFlag flag = null;
            if (!record.flags().isEmpty()) {
                try {
                    flag = EntryFlag.valueOf("ENTRY_FLAG_" + record.flags().getFirst());
                } catch (IllegalArgumentException e) {
                    errors.add("Block starting on line: " + line +
                            " has illegal entry flag value: " + record.flags().getFirst());
                    continue;
                }
            }
            String desc = record.desc();
            String label = record.label();
            String label2 = record.label2();
            String label5 = record.label5();
            List<String> categories = record.category();
            UIEntryBase template;
            if (record.template().isEmpty())
                template = null;
            else {
                template = GameConstants.getUIEntryBase(record.template());
                if (template == null) {
                    errors.add("Block starting on line: " + line +
                            " has illegal template name: " + record.template());
                    continue;
                }
            }

            if (statElemType != UIEntry.StatElemType.STAT) {
                results.add(new UIEntry(name, parameter, statElemType,
                        renderer, combinerName, categories,
                        priorityNum, flag,
                        desc, label, label2, label5, template));
            } else {
                for (Stats stat : Stats.values()) {
                    int newPriorityNum = 0;

                    if (!stat.getStatString().isEmpty()) {
                        if (priorityStr.equals("negative_index"))
                            newPriorityNum = -1 * stat.getValue();
                        else if (priorityStr.equals("index"))
                            newPriorityNum = stat.getValue();
                        else
                            newPriorityNum = 0;

                        results.add(new UIEntry(name + "<" + stat.getStatString() + ">", parameter, statElemType,
                                renderer, combinerName, categories,
                                newPriorityNum, flag,
                                desc, label, label2, label5, template));
                    }
                }
            }
        }

        return results;
    }
}