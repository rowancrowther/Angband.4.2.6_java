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

package uk.co.jackoftradesltd.frontend.ui.entry.assembler;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.ui.entry.EmbryonicCategoryReferencies;
import uk.co.jackoftradesltd.frontend.ui.entry.UIEntryEmbryo;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryNameParameter;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Second stage of the {@code ui_entry.txt} pipeline: turns the raw string
 * {@link UIEntryParseRecord}s emitted by the {@code UIEntryGrammar} parser into
 * fully resolved {@link UIEntry} objects. This is where the domain look-ups the
 * grammar deliberately avoids finally happen - resolving the {@code parameter}
 * element, the {@code renderer} and {@code template} against the
 * {@code GameConstants} registries, and the {@code combine} and {@code flags}
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
    private final static Logger logger = LogManager.getLogger(UIEntryAssembler.class);

    private static UIEntryEmbryo embryo;
    
    private static List<UIEntryCategory> buildCategories(List<String> before, List<String> after, int priority) {
        List<UIEntryCategory> categories = new ArrayList<>();
        for (String category : before) categories.add(new UIEntryCategory(category, priority, true));
        for (String category : after) categories.add(new UIEntryCategory(category, priority, true));
        return categories;
    }

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
        embryo = null;

        for (UIEntryParseRecord record : records) {
            int line = record.line();
            UIEntry uiEntry = null;

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
            StatElemType statElemType = StatElemType.fromValue(record.parameter());
            if (statElemType == null) {
                errors.add("Block starting on line: " + line +
                        " has illegal parameter kind: " + record.parameter());
                continue;
            }
            String rendStr = record.renderer();
            UIEntryRenderer renderer;
            if (!rendStr.isEmpty()) {
                renderer = UIRegistry.getUIEntryRenderer(rendStr, errors);
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
                if (record.priority().equals("index")) {
                    priorityStr = record.priority();
                    priorityNum = 1;
                } else if (record.priority().equals("negative_index")) {
                    priorityStr = record.priority();
                    priorityNum = -1;
                } else {
                    priorityStr = null;
                    try {
                        priorityNum = Integer.parseInt(record.priority());
                    } catch (NumberFormatException e) {
                        errors.add("Block starting on line: " + line +
                                " has illegal priority number value: " + record.priority());
                        continue;
                    }
                }
            }
            Flag<ChannelEntryFlag> flag = new Flag<>(ChannelEntryFlag.class);
            if (!record.flags().isEmpty()) {
                for (String flagName : record.flags()) {
                    try {
                        ChannelEntryFlag flagType = ChannelEntryFlag.valueOf("ENTRY_FLAG_" + flagName);
                        flag.on(flagType);
                    } catch (IllegalArgumentException e) {
                        errors.add("Block starting on line: " + line +
                                " has illegal entry flag value: " + record.flags().getFirst());
                        continue;
                    }
                }
            }
            String desc = record.desc();
            String label = record.label();
            String label2 = record.label2();
            String label5 = record.label5();
            List<String> categoriesNames1 = record.categoriesBeforePriority();
            List<String> categoriesNames2 = record.categoriesAfterPriority();
            UIEntryBase template;
            if (record.template().isEmpty())
                template = null;
            else {
                template = UIRegistry.getUIEntryBase(record.template());
                if (template == null) {
                    errors.add("Block starting on line: " + line +
                            " has illegal template name: " + record.template());
                    continue;
                }
            }
            if (statElemType == StatElemType.STAT) {
                for (int i = 0; i < 5; i++) {
                    int newPriorityNum;
                    if (!PlayerEventStatusUpdate.getPlayerStatusView().statString()[i].isEmpty()) {
                        if ("negative_index".equals(priorityStr))
                            newPriorityNum = -i;
                        else if ("index".equals(priorityStr))
                            newPriorityNum = i;
                        else
                            newPriorityNum = 0;
                        List<UIEntryCategory> categories = buildCategories(categoriesNames1, categoriesNames2, newPriorityNum);
                        String statStr = PlayerEventStatusUpdate.getPlayerStatusView().statString()[i];
                        uiEntry = new UIEntry(name + "<" + statStr + ">", parameter, statElemType,
                                renderer, combinerName, categories, newPriorityNum, flag,
                                desc, label, label2, label5);
                    }
                }
            } else if (statElemType == StatElemType.ELEMENT) {
                for (ElementEnum elementEnum : ElementEnum.values()) {
                    if (elementEnum == ElementEnum.ELEM_MAX || elementEnum == ElementEnum.ELEM_NONE)
                        continue;

                    int newPriorityNum;
                    int indexNum = elementEnum.ordinal() - 1;
                    if ("negative_index".equals(priorityStr))
                        newPriorityNum = -indexNum;
                    else if ("index".equals(priorityStr))
                        newPriorityNum = indexNum;
                    else
                        newPriorityNum = 0;
                    List<UIEntryCategory> categories = buildCategories(categoriesNames1, categoriesNames2, 0);
                    uiEntry = new UIEntry(name + "<" + elementEnum.name().substring(5) + ">",
                            elementEnum, statElemType, renderer, combinerName, categories, newPriorityNum, flag,
                            desc, label, label2, label5);
                }
            } else {
                List<UIEntryCategory> categories = buildCategories(categoriesNames1, categoriesNames2, priorityNum);
                uiEntry = new UIEntry(name, parameter, statElemType,
                        renderer, combinerName, categories,
                        priorityNum, flag,
                        desc, label, label2, label5);
            }

//            if (embryo == null) {
            // First time through 
            // deal with this as if we have just hatched an embryo
            newEmbryo(uiEntry);
//            } else if (embryo.exists()) {
            // Check to see if the current embryo has a category reference match for this
//                if (uiEntry != null && embryoHasMatch(uiEntry)) {
//                    embryo.setExists(true);
            // add the data into the embryo
//                } else {
//                    hatchEmbryo(results);
//                    newEmbryo(uiEntry);
//                }                
//            } else {
            // Should this situation ever be reached?
//            }

            // Hatching an embryo automatically inserts it into the results array
            // if (uiEntry != null) results.add(uiEntry);
        }

        return results;
    }

    private void newEmbryo(UIEntry uiEntry) {
        embryo = new UIEntryEmbryo(uiEntry, new ArrayList<>(), UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE,
                UIEntryPriorityScheme.PRIORITY_SCHEME_NONE, 0, false);
    }

    private void hatchEmbryo(List<UIEntry> results) {
        UIEntry entry = null;

        if (!embryo.exists()) {
            int total;
            int index;

            if (embryo.getUiEntry().getCombineType() == CombinerName.NONE) {
                return;
            }

            // Parameterise the name generating multiple entries
            total = embryo.getParmIndex().getCount();
            for (index = 1; index < total - 1; index++) {
                entry = new UIEntry("", ElementEnum.ELEM_NONE, StatElemType.NONE,
                        null, null, new ArrayList<>(), 0,
                        new Flag<>(ChannelEntryFlag.class), "", "", "", "");
                String name = embryo.getParmIndex().getParamName(index);
                entry.setName(String.format("%s<%s>", embryo.getUiEntry().getName(), name));
                if (embryo.getpSourceIndex() != null) {
                    entry.setPriorityNum(embryo.getpSourceIndex().getPriority(index));
                } else {
                    entry.setPriorityNum(embryo.getUiEntry().getDefaultPriority());
                }
                parameteriseCategoryList(embryo.getCategories(), embryo.getUiEntry().getCategories().size(),
                        index, entry);
                // Object props and player skill are set on ObjectProperties/PlayerProperties
                if (!embryo.getUiEntry().getLabel().isEmpty()) {
                    entry.setLabel(embryo.getUiEntry().getLabel());
                } else {
                    entry.setLabel(" ".repeat(UIRegistry.MAX_ENTRY_LABEL));
                }

                copyShortenedLabels(entry, embryo.getUiEntry());
                entry.setRenderer(embryo.getUiEntry().getRenderer());
                entry.setCombinerType(embryo.getUiEntry().getCombineType());
                entry.setParamIndex(index);
                entry.setEntryFlags(embryo.getUiEntry().getEntryFlag());
                uiEntryInsert(entry, results);
            }

            // For the last one, reuse the structure stored in the embryo 
            // to save some effort
            if (embryo.getParmIndex() == UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE) {
                // No parameterisation
                embryo.getUiEntry().setParamIndex(-1);
            } else {
                String name = embryo.getParmIndex().getParamName(total - 1);
                embryo.getUiEntry().setName(String.format("%s<%s>", embryo.getParmIndex().getName(), name));

                if (embryo.getUiEntry().getLabel().isEmpty()) {
                    embryo.getUiEntry().setLabel(" ".repeat(UIRegistry.MAX_ENTRY_LABEL));
                }

                embryo.getUiEntry().setParamIndex(total - 1);
                if (embryo.getpSourceIndex() != null) {
                    embryo.getUiEntry().setDefaultPriority(embryo.getpSourceIndex().getPriority(total - 1));
                }
            }
            parameteriseCategoryList(embryo.getCategories(), embryo.getUiEntry().getNCategory(), total - 1,
                    embryo.getUiEntry());
            uiEntryInsert(embryo.getUiEntry(), results);
        }
    }

    private void uiEntryInsert(@NotNull UIEntry entry, List<UIEntry> results) {
        if (results.stream().anyMatch(r -> r.getName().equals(entry.getName()))) {
            logger.error("Attempted to insert UI entry with the same name");
            throw new RuntimeException("Attempted to insert UI entry with the same name");
        }

        results.add(entry);
    }


    private void copyShortenedLabels(UIEntry dest, UIEntry src) {
        for (int index = 0; index < UIRegistry.MAX_SHORTENED; index++) {
            dest.setShortenedLabel(index, src.getShortenedLabel(index));
        }
    }

    private void parameteriseCategoryList(List<EmbryonicCategoryReferencies> categories, int size,
                                          int index, UIEntry entry) {
        for (int i = 0; i < size; i++) {
            entry.getCategories().get(i).setName(categories.get(i).getName());
            if (categories.get(i).isPrioritySet()) {
                if (categories.get(i).getpSourceIndex() != UIEntryPriorityScheme.PRIORITY_SCHEME_NONE)
                    entry.getCategories().get(i).setPriority(categories.get(i).getpSourceIndex().getPriority(index));
                else
                    entry.getCategories().get(i).setPriority(categories.get(i).getPriority());
                entry.getCategories().get(i).setPrioritySet(true);
            } else {
                entry.getCategories().get(i).setPriority(0);
                entry.getCategories().get(i).setPrioritySet(false);
            }
        }
    }

    private boolean embryoHasMatch(UIEntry uiEntry) {
        String entryName = uiEntry.getName();

        for (EmbryonicCategoryReferencies cat : embryo.getCategories()) {
            if (cat.getName().equals(entryName)) {
                return true;
            }
        }

        return false;
    }
}