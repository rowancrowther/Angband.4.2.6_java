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
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.ui.entry.EmbryonicCategoryReferencies;
import uk.co.jackoftradesltd.frontend.ui.entry.UIEntryEmbryo;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Second stage of the {@code ui_entry.txt} pipeline: turns the raw string {@link UIEntryParseRecord}s
 * emitted by the grammar into fully resolved {@link UIEntry} objects and folds them into the single
 * shared registry list, alongside the {@code ui_entry_base.txt}-derived placeholders
 * {@code UIEntryBaseAssembler} already produced there - one list, as in C's {@code entries[]}, per the
 * 260919 decision to drop {@code UIEntryBase}'s own registry.
 * <p>
 * Where the C original resolves and folds a record's fields one parser directive at a time against a
 * live {@code struct embryonic_ui_entry} ({@code [C] ui-entry.c:1888-2191}), this port receives the
 * whole record pre-parsed as one {@link UIEntryParseRecord} and does that same directive-by-directive
 * work - template inheritance, renderer/combiner/label/flag resolution, category/priority placement, and
 * {@code parameter:element}/{@code parameter:stat} expansion into per-value entries - in one pass across
 * {@link #assemble} and {@link #parseEachEntry}. The per-value expansion is the Java form of
 * {@code hatch_embryo}'s parameterised-name loop ({@code [C] ui-entry.c:1762-1869}); folding into an
 * already-registered entry of the same name is the Java form of {@code hatch_embryo}'s
 * {@code embryo->exists} branch - exercised here (unlike in {@code UIEntryBaseAssembler}) whenever a
 * {@code ui_entry.txt} record's name already appears in the registry. Unlike
 * {@code UIEntryBaseAssembler}'s override branch, this one is live: the thirteen
 * {@code resist_ui_compact_0<TAG>} specialisation records (e.g. {@code resist_ui_compact_0<ACID>})
 * match the per-element names the earlier {@code parameter:element} record for
 * {@code resist_ui_compact_0} fanned out, so all thirteen take this branch (see
 * {@code docs/precis/260920.md}, "Correction: the create path is not the common case").
 * <p>
 * Assembly is best-effort and error-collecting, like {@code UIEntryBaseAssembler}: a record whose
 * renderer, combiner, template or parameter cannot be resolved is skipped with a message appended to
 * {@code errors}, and processing continues with the next record.
 *
 * <p>Class UIEntryAssembler coded before 260922, commented in full on 260922.
 *
 * @author Rowan Crowther
 */
public class UIEntryAssembler implements Assembler<UIEntryParseRecord, List<UIEntry>> {
    /**
     * Logger for the fatal, non-recoverable state {@link #parseEachEntry(List, UIEntry)} throws on -
     * an override record whose incoming entry still carries a stat or element parameter, which
     * mirrors a C {@code quit()}/{@code assert} rather than a soft, error-collecting failure, the same
     * as {@code UIEntryBaseAssembler}'s equivalent check.
     *
     * <p>Field logger coded before 260922, commented in full on 260922.
     */
    private final static Logger logger = LogManager.getLogger(UIEntryAssembler.class);

    /**
     * The in-progress {@link UIEntryEmbryo} for the record currently being folded by
     * {@link #parseEachEntry(List, UIEntry)}'s create path - the Java form of C's
     * {@code struct embryonic_ui_entry} ({@code [C] ui-entry.c:170-177}) while a parser works through
     * one record's directives. Reassigned (never read back across calls) each time {@link #assemble}
     * processes a record that does not already exist in the registry; left unused by the override
     * path, which mutates the existing {@link UIEntry} directly instead.
     *
     * <p>Field embryo coded before 260922, commented in full on 260922.
     */
    private static UIEntryEmbryo embryo;

    /**
     * Builds the resolved category list for one {@link UIEntryParseRecord}, splitting on where the
     * record's (at most one) {@code priority:} line falls: categories in {@code before} were written
     * with no priority yet in force, categories in {@code after} were written once the priority from
     * that line was in force. The Java form of the repeated {@code parse_entry_category}/
     * {@code parse_entry_priority} directive calls C performs while parsing one record
     * ({@code [C] ui-entry.c:2120-2191}), collapsed here into a single pass over the pre-parsed record.
     * <p>
     * Only the last entry in {@code before} is given {@code priority}/{@code prioritySet=true}
     * directly - matching C's {@code embryo->last_category_index} always pointing at the most recently
     * seen category ({@code [C] ui-entry.c:2134}) - every other {@code before} category is left
     * {@code prioritySet=false} so {@code UIDataLoader}'s finishing pass fills it from the entry's own
     * default priority later, the same as C's finishing pass ({@code [C] ui-entry.c:2325-2331}).
     * <p>
     * <b>Known divergence (found 260922, unfixed):</b> every {@code after} category is also stamped
     * with {@code priority} here (with {@code prioritySet=false}, so it is really only waiting on the
     * entry's default priority). That is correct only when {@code before} is empty - i.e. when the
     * {@code priority:} line had no preceding category and so really did set the record's default
     * ({@code [C] ui-entry.c:2177-2179}). When {@code before} is non-empty, C leaves
     * {@code default_priority} at 0 ({@code [C] ui-entry.c:2181-2188}), because the {@code priority:}
     * line attached to that category instead - but {@link #assemble} still passes the same
     * {@code priority} value on as the record's own {@code priorityNum} at each of its three call
     * sites, so an {@code after} category would incorrectly inherit the {@code before} category's
     * priority instead of 0. No shipped {@code ui_entry.txt} record has a {@code category:} line
     * before a {@code priority:} line, so this is latent, not live.
     *
     * @param before          category names seen before the record's priority (if any) took effect
     * @param after           category names seen after the record's priority took effect
     * @param priority        the record's resolved numeric priority
     * @param defaultPriority the priority given to every {@code before} category except the last;
     *                        always {@code 0} at the one call site, and moot regardless since
     *                        {@code prioritySet=false} means the finishing pass overwrites it
     * @return the resolved category list, in {@code before} then {@code after} order
     *
     * <p>Function buildCategories coded before 260922, commented in full on 260922.
     */
    private static List<UIEntryCategory> buildCategories(List<String> before, List<String> after, int priority,
                                                         int defaultPriority) {
        List<UIEntryCategory> categories = new ArrayList<>();
        for (int index = 0; index < before.size(); index++) {
            if (index == before.size() - 1) {
                categories.add(new UIEntryCategory(before.get(index), priority, true));
            } else {
                categories.add(new UIEntryCategory(before.get(index), defaultPriority, false));
            }
        }
        for (String category : after) categories.add(new UIEntryCategory(category, priority, false));
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
     *
     * <p><b>Outstanding (found 260922):</b> the priority value computed here for a record is passed
     * both into {@link #buildCategories(List, List, int, int)} (correct) and into the {@link UIEntry}
     * constructor as the record's own default priority (only correct when the record has no category
     * before its {@code priority:} line) - see {@link #buildCategories(List, List, int, int)}'s
     * Javadoc for the full account. Latent, not live: no shipped {@code ui_entry.txt} record has a
     * {@code category:} line before a {@code priority:} line.
     *
     * <p>Function assemble coded before 260922, commented in full on 260922.
     */
    @Override
    public List<UIEntry> assemble(@NotNull List<UIEntryParseRecord> records,
                                  @NotNull List<String> errors) {
        List<UIEntry> results = new ArrayList<>(UIRegistry.getUIEntries());
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

            // Check for existance
            UIEntry existingEntry = results.stream().filter(r -> r.getName().equals(name))
                    .findFirst().orElse(null);

            if ((statElemType == StatElemType.ELEMENT || statElemType == StatElemType.STAT)
                    && existingEntry != null) {
                errors.add("Block starting on line: " + line + " has " +
                        "a stat/parameter set on an already existing entry name");
                continue;
            }

            // priority must be either "index", "negative_index" or an integer
            int priorityNum = 0;
            String priorityStr = "";
            if (!record.priority().isEmpty()) {
                if (record.priority().equals("index") || record.priority().equals("negative_index")) {
                    priorityStr = record.priority();
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
            boolean badFlag = false;
            if (!record.flags().isEmpty()) {
                for (String flagName : record.flags()) {
                    try {
                        ChannelEntryFlag flagType = ChannelEntryFlag.valueOf("ENTRY_FLAG_" + flagName);
                        flag.on(flagType);
                    } catch (IllegalArgumentException e) {
                        errors.add("Block starting on line: " + line +
                                " has illegal entry flag value: " + record.flags().getFirst());
                        badFlag = true;
                    }
                }
            }
            if (badFlag) {
                continue;
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
                        else if ("".equals(priorityStr))
                            newPriorityNum = 0;
                        else
                            newPriorityNum = priorityNum;
                        String statStr = PlayerEventStatusUpdate.getPlayerStatusView().statString()[i];
                        List<UIEntryCategory> categories =
                                buildCategories(categoriesNames1, categoriesNames2, newPriorityNum, 0);
                        String newLabel = record.label();
                        if (record.label() == null || record.label().isEmpty())
                            newLabel = statStr;
                        parseEachEntry(results, new UIEntry(name + "<" + statStr + ">", template, parameter, statElemType,
                                renderer, combinerName, categories, newPriorityNum, priorityStr,
                                flag, desc, newLabel, label5, label2));
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
                    else if ("".equals(priorityStr))
                        newPriorityNum = 0;
                    else
                        newPriorityNum = priorityNum;
                    List<UIEntryCategory> categories =
                            buildCategories(categoriesNames1, categoriesNames2, newPriorityNum, 0);
                    String newLabel = label;
                    if (record.label() == null || record.label().isEmpty())
                        newLabel = elementEnum.name().substring(5);
                    parseEachEntry(results, new UIEntry(name + "<" + elementEnum.name().substring(5) + ">",
                            template, elementEnum, statElemType, renderer, combinerName,
                            categories, newPriorityNum, priorityStr, flag, desc, newLabel, label5, label2));
                }
            } else {
                List<UIEntryCategory> categories
                        = buildCategories(categoriesNames1, categoriesNames2, priorityNum, 0);

                parseEachEntry(results, new UIEntry(name, template, parameter, statElemType,
                        renderer, combinerName, categories,
                        priorityNum, priorityStr, flag,
                        desc, label, label5, label2));
            }
        }

        return results;
    }

    /**
     * Folds one resolved {@link UIEntry} - built by {@link #assemble} from a single
     * {@link UIEntryParseRecord} - into the shared {@code results} list, either merging it into an
     * already-registered entry of the same name (the override path) or turning it into a fresh entry
     * via an {@link UIEntryEmbryo} (the create path). Together these are the Java form of
     * {@code hatch_embryo} ({@code [C] ui-entry.c:1762-1869}) and the individual {@code parse_entry_*}
     * directive callbacks ({@code [C] ui-entry.c:1888-2191}) that mutate a record's
     * {@code embryonic_ui_entry} while it is being read, collapsed here into one pass over an
     * already-fully-parsed record.
     * <p>
     * The override path (a {@code results} lookup by name finds an existing entry) is live - the
     * thirteen {@code resist_ui_compact_0<TAG>} records reach it, since their names match what the
     * earlier {@code parameter:element resist_ui_compact_0} record's fan-out already inserted. It
     * throws if the incoming entry still carries a stat or element parameter, mirroring
     * {@code parse_entry_parameter}'s refusal to parameterise an entry that already exists
     * ({@code [C] ui-entry.c:1996-1998}) - that specific throw is unreached by shipped data, since
     * none of the thirteen records carries its own {@code parameter:} line. It then applies the record's template (if
     * any), renderer, combiner, label, shortened labels and flags directly onto the existing entry -
     * each an unconditional overwrite in C ({@code parse_entry_renderer}/{@code parse_entry_combine}/
     * {@code parse_entry_label}, {@code [C] ui-entry.c:2021-2078}) except flags, which C only ever ORs
     * in ({@code parse_entry_flags}, {@code [C] ui-entry.c:2194-2228}) - before merging in categories
     * not already present and applying the record's priority to either the existing entry's default
     * priority or its last-merged category, matching {@code parse_entry_priority}'s
     * {@code last_category_index} branch ({@code [C] ui-entry.c:2177-2189}).
     * <p>
     * The create path builds a blank {@link UIEntry}, wraps it in a {@link UIEntryEmbryo}, applies the
     * record's template the same way {@code parse_entry_template} does ({@code [C]
     * ui-entry.c:1952-1986}), then applies renderer/combiner/label/shortened-labels/categories from the
     * record. The categories were already given their resolved priority by
     * {@link #buildCategories(List, List, int, int)} before this method ever sees them; see that
     * method's Javadoc for a known divergence in how a category-attached (rather than record-default)
     * priority is threaded through here.
     *
     * @param results the shared entry list being built up across all of {@code ui_entry.txt} (seeded
     *                from the registry's existing entries, including the {@code ui_entry_base.txt}
     *                placeholders); mutated in place
     * @param entry   the resolved {@link UIEntry} {@link #assemble} built from one parse record, not
     *                yet folded into {@code results}
     *
     *                <p>Function parseEachEntry coded before 260922, commented in full on 260922.
     */
    private void parseEachEntry(List<UIEntry> results, UIEntry entry) {
        UIEntry existing = results.stream()
                .filter(e -> e.getName().equals(entry.getName())).findFirst().orElse(null);
        if (existing != null) {
            // Override path
            if ((entry.getStatOrElement() == StatElemType.ELEMENT && entry.getParameter() != null) ||
                    (entry.getStatOrElement() == StatElemType.STAT && entry.getStatParameter() != -1)) {
                String message = "Invalid entry found - existing UIEntry with non-null parameter";
                logger.fatal(message);
                throw new RuntimeException(message);
            }

            // Templates
            UIEntryCategory lastCategory = null;
            UIEntryBase template = entry.getTemplate();
            if (template != null) {
                existing.setRenderer(template.getRenderer());
                existing.setCombinerType(template.getCombine());
                existing.setDefaultPriority(0); // May need to change if ever UIEntryBases gain priorities
                for (String catStr : template.getCategories()) {
                    UIEntryCategory cat = new UIEntryCategory(catStr, existing.getDefaultPriority(), false);
                    if (existing.getCategories().stream()
                            .noneMatch(c -> c.getName().equals(catStr))) {
                        existing.getCategories().add(cat);
                    }
                }
            }

            if (entry.getRenderer() != null)
                existing.setRenderer(entry.getRenderer());
            if (entry.getCombineType() != null && entry.getCombineType() != CombinerName.NONE)
                existing.setCombinerType(entry.getCombineType());
            if (entry.getLabel() != null)
                existing.setLabel(entry.getLabel());

            for (int index = 0; index < 10; index++) {
                if (entry.getShortenedLabel(index) != null)
                    existing.setShortenedLabel(index, entry.getShortenedLabel(index));
            }
            FlagView<ChannelEntryFlag> entryFlags = entry.getEntryFlag();
            if (!entryFlags.isEmpty()) {
                Flag<ChannelEntryFlag> newFlags = new Flag<>(ChannelEntryFlag.class);
                newFlags.copyFrom(entryFlags);
                Flag<ChannelEntryFlag> oldFlags = new Flag<>(ChannelEntryFlag.class);
                oldFlags.copyFrom(existing.getEntryFlag());
                for (ChannelEntryFlag flg : newFlags) {
                    oldFlags.on(flg);
                }
                existing.setEntryFlags(oldFlags);
            }

            for (UIEntryCategory cat : entry.getCategories()) {
                UIEntryCategory category = null;
                if (existing.getCategories().stream().
                        noneMatch(c -> c.getName().equals(cat.getName()))) {
                    int priorityNum = (entry.getPriorityString() == null || entry.getPriorityString().isEmpty())
                            ? existing.getDefaultPriority() : 0;
                    category = new UIEntryCategory(cat.getName(),
                            priorityNum, false);
                    existing.getCategories().add(category);
                } else {
                    category = existing.getCategories().stream()
                            .filter(c -> c.getName().equals(cat.getName()))
                            .findFirst().orElse(null);
                }
                lastCategory = category;
            }

            String scheme = entry.getPriorityString();
            int value;
            if (scheme != null) {
                int parmIndex = 0;
                if (entry.getStatOrElement() == StatElemType.ELEMENT) {
                    parmIndex = existing.getParameter().ordinal() - 1;
                } else if (entry.getStatOrElement() == StatElemType.STAT) {
                    parmIndex = existing.getStatParameter();
                }
                if (scheme.equals("negative_index")) {
                    parmIndex = -parmIndex;
                }
                value = parmIndex;
            } else {
                value = entry.getPriorityNum();
            }
            if (scheme == null || !scheme.isEmpty()) {
                if (lastCategory == null) {
                    existing.setDefaultPriority(value);
                } else {
                    lastCategory.setPriority(value);
                    lastCategory.setPrioritySet(true);
                }
            }
        } else {
            // Create path
            UIEntry blank = new UIEntry(entry.getName(), null, entry.getParameter(), entry.getStatOrElement(),
                    null, null, new ArrayList<>(), entry.getPriorityNum(), entry.getPriorityString(),
                    new Flag<>(ChannelEntryFlag.class), "To keep alive", null, null, null);

            UIEntryPriorityScheme pSourceIndex;
            String priorityString = entry.getPriorityString();
            if (priorityString == null || priorityString.isEmpty()) {
                pSourceIndex = UIEntryPriorityScheme.PRIORITY_SCHEME_NONE;
            } else if (priorityString.equals("negative_index")) {
                pSourceIndex = UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX;
            } else {
                pSourceIndex = UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX;
            }

            embryo = new UIEntryEmbryo(blank, new ArrayList<>(),
                    null, pSourceIndex, null, false);

            UIEntryBase template = entry.getTemplate();
            if (template != null) {
                embryo.getUiEntry().setRenderer(template.getRenderer());
                embryo.getUiEntry().setCombinerType(template.getCombine());
                embryo.getUiEntry().setDefaultPriority(entry.getPriorityNum());
                Flag<ChannelEntryFlag> newFlags = new Flag<>(ChannelEntryFlag.class);
                newFlags.copyFrom(template.getFlags());
                newFlags.off(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
                embryo.getUiEntry().setEntryFlags(newFlags);

                if (!template.getCategories().isEmpty()) {
                    for (String catName : template.getCategories()) {
                        UIEntryCategory category = new UIEntryCategory(catName,
                                embryo.getUiEntry().getDefaultPriority(), false);
                        embryo.getUiEntry().getCategories().add(category);
                        EmbryonicCategoryReferencies embCat = new EmbryonicCategoryReferencies(category,
                                UIEntryPriorityScheme.PRIORITY_SCHEME_NONE,
                                embryo.getUiEntry().getDefaultPriority(), false);
                        embryo.getCategories().add(embCat);
                    }
                }
            }
            if (entry.getRenderer() != null)
                embryo.getUiEntry().setRenderer(entry.getRenderer());
            if (entry.getCombineType() != null && entry.getCombineType() != CombinerName.NONE)
                embryo.getUiEntry().setCombinerType(entry.getCombineType());
            if (entry.getLabel() != null)
                embryo.getUiEntry().setLabel(entry.getLabel());
            for (int index = 0; index < 10; index++) {
                if (entry.getShortenedLabel(index) != null)
                    embryo.getUiEntry().setShortenedLabel(index, entry.getShortenedLabel(index));
            }

            for (UIEntryCategory category : entry.getCategories()) {
                String catName = category.getName();
                EmbryonicCategoryReferencies embCat = embryo.getCategories().stream()
                        .filter(e -> e.getCategory().getName().equals(catName))
                        .findFirst().orElse(null);
                if (embCat == null) {
                    embCat = new EmbryonicCategoryReferencies(category, null, 0, false);
                    embryo.getCategories().add(embCat);
                    embryo.getUiEntry().getCategories().add(embCat.getCategory());
                }
            }

            results.add(embryo.getUiEntry());
        }
    }
}