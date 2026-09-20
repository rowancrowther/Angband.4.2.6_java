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

package uk.co.jackoftradesltd.frontend.ui.entrybase.assembler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.globals.UIGlobals;
import uk.co.jackoftradesltd.frontend.ui.entry.EmbryonicCategoryReferencies;
import uk.co.jackoftradesltd.frontend.ui.entry.UIEntryEmbryo;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.HelperFunctions;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Second stage of the {@code ui_entry_base.txt} pipeline: turns the raw string
 * {@link UIEntryBaseParseRecord}s emitted by the grammar into fully resolved
 * {@link UIEntryBase} template objects. This is where the domain look-ups that
 * the grammar deliberately avoids finally happen &mdash; the {@code renderer:}
 * name is resolved against the loaded {@code ui_entry_renderer.txt} renderers,
 * and the {@code combine:} name against the {@link CombinerName} enum.
 * <p>
 * Assembly is best-effort and error-collecting rather than fail-fast: a record
 * whose renderer or combiner cannot be resolved is skipped with a message
 * appended to {@code errors} (quoting its source line), and processing
 * continues with the next record, so one bad block does not hide the rest.
 *
 * @author Rowan Crowther
 */
public class UIEntryBaseAssembler implements Assembler<UIEntryBaseParseRecord, List<UIEntryBase>> {
    /**
     * Logger for the fatal, non-recoverable states {@link #parseEachEntry(List, UIEntry)} throws on
     * - an override record whose parameter is non-null, or a record with no combiner - both of which
     * mirror a C {@code quit()}/{@code assert} rather than a soft, error-collecting failure.
     *
     * <p>Field logger coded before 260920, commented in full on 260920.
     */
    private static final Logger logger = LogManager.getLogger(UIEntryBaseAssembler.class);

    /**
     * Resolves each {@link UIEntryBaseParseRecord} into a {@link UIEntryBase},
     * skipping (and reporting) any record whose renderer name is unknown or
     * whose combine name is not a valid {@link CombinerName}.
     *
     * @param records the raw template records produced by the grammar
     * @param errors  collector to which per-record resolution failures are
     *                appended; passed on to the builder
     * @return the successfully assembled {@link UIEntryBase} templates, in
     * source order, excluding any records that failed to resolve
     */
    @Override
    public List<UIEntryBase> assemble(@NotNull List<UIEntryBaseParseRecord> records,
                                      @NotNull List<String> errors) {
        List<UIEntryBase> results = new ArrayList<>();
        List<UIEntry> entries = new ArrayList<>();

        for (UIEntryBaseParseRecord record : records) {
            int line = record.lineNumber();

            String name = record.name();
            String rendererName = record.renderer();
            UIEntryRenderer renderer = UIRegistry.getUIEntryRenderer(rendererName, errors);
            if (renderer == null) {
                errors.add("Block starting on line: " + line +
                        " has renderer " + rendererName +
                        " not found for record " + name);
                continue;
            }

            CombinerName combinerName;
            try {
                combinerName = CombinerName.valueOf(record.combine());
            } catch (IllegalArgumentException e) {
                errors.add("Line: " + line + ": Illegal combine name for record " + record.name());
                continue;
            }
            List<String> categories = record.categories();
            String flags = record.flags();
            String desc = record.desc();

            UIEntryBase base = new UIEntryBase(name, renderer, combinerName, categories, flags, desc);

            List<UIEntryCategory> cats = new ArrayList<>();

            for (String category : categories) {
                cats.add(new UIEntryCategory(category, 0, false));
            }

            Flag<ChannelEntryFlag> flag = new Flag<>(ChannelEntryFlag.class);
            flag.on(ChannelEntryFlag.valueOf("ENTRY_FLAG_" + flags.toUpperCase()));

            UIEntry entry = new UIEntry(name, null, null, renderer, combinerName, cats, 0,
                    flag, desc, "", "", "");

            parseEachEntry(entries, entry);

            results.add(base);
        }

        UIRegistry.setUIEntries(entries);
        return results;
    }

    /**
     * Folds one {@code ui_entry_base.txt} record's raw {@link UIEntry} into the shared entry list,
     * either merging it into an already-inserted entry of the same name (the override path) or
     * turning it into a fresh, {@code TEMPLATE_ONLY}-flagged entry (the create path). Together with
     * the flag it sets on every path, this is the Java form of C's {@code hatch_embryo}
     * ({@code [C] ui-entry.c:1756-1863}) as it applies to the base file specifically, and of
     * {@code run_parse_ui_entry}'s post-base-file loop that OR's {@code ENTRY_FLAG_TEMPLATE_ONLY}
     * into every entry parsed so far ({@code [C] ui-entry.c:2276-2278}) - done here per entry rather
     * than in one pass afterwards.
     * <p>
     * The override path (an {@code entries} lookup by name finds an existing entry) mirrors
     * {@code hatch_embryo}'s {@code embryo->exists} branch: the renderer, combiner and flags are
     * overwritten outright, and only categories not already present are added, at the existing
     * entry's own default priority and with {@code priority_set} left {@code false} - matching
     * {@code insert_embryo_category}'s call from {@code parse_entry_category}
     * ({@code [C] ui-entry.c:1447-1561, 2114-2130}). It throws if the incoming entry carries a
     * non-null parameter, since C's {@code parse_entry_parameter} refuses to parameterise an entry
     * that already exists ({@code [C] ui-entry.c:1996-1998}) - a case {@code ui_entry_base.txt} never
     * produces, since no record in it repeats an earlier record's name.
     * <p>
     * The create path builds one {@link EmbryonicCategoryReferencies} per category (mirroring
     * {@code embryo->categories} before it is parameterised) and then expands the entry once per
     * element or per stat if the source record's parameter says to, or once outright otherwise -
     * the same three-way shape as {@code hatch_embryo}'s parameterised-name loop
     * ({@code [C] ui-entry.c:1771-1822}), though {@code ui_entry_base.txt} only ever takes the
     * "otherwise" branch, since none of its records set a {@code parameter:}. It throws if the
     * entry has no combiner, mirroring the required-field check at the top of
     * {@code hatch_embryo} ({@code [C] ui-entry.c:1762-1769}).
     *
     * <p>Function parseEachEntry coded before 260920, commented in full on 260920.
     *
     * @param entries the shared entry list being built up across all of {@code ui_entry_base.txt};
     *                mutated in place
     * @param entry   the raw {@link UIEntry} {@link #assemble} built from one parse record, not yet
     *                folded into {@code entries}
     */
    private void parseEachEntry(List<UIEntry> entries, UIEntry entry) {
        Flag<ChannelEntryFlag> fullFlags = new Flag<>(ChannelEntryFlag.class);
        fullFlags.copyFrom(entry.getEntryFlag());
        fullFlags.on(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
        entry.setEntryFlags(fullFlags);
        UIEntry existing = entries.stream().filter(e -> e.getName().equals(entry.getName()))
                .findFirst().orElse(null);
        if (existing != null) {
            UIEntryCategory lastCategory;
            // Override path
            if (entry.getParameter() != null) {
                String message = "Invalid entry found - existing UIEntry with non-null parameter";
                logger.fatal(message);
                throw new RuntimeException(message);
            }

            // At present UIEntryBases don't have templates, ignore this part
            existing.setRenderer(entry.getRenderer());
            existing.setCombinerType(entry.getCombineType());
            existing.setEntryFlags(fullFlags);

            lastCategory = null;
            for (UIEntryCategory category : entry.getCategories()) {
                if (existing.getCategories().stream()
                        .noneMatch(c -> c.getName().equals(category.getName()))) {
                    UIEntryCategory newCategory = new UIEntryCategory(category.getName(), existing.getDefaultPriority(),
                            false);

                    existing.getCategories().add(newCategory);

                    lastCategory = newCategory;
                }
            }
        } else {
            EmbryonicCategoryReferencies lastCategory;
            // Create path
            UIEntry nullEntry = new UIEntry(entry.getName(), ElementEnum.ELEM_NONE, StatElemType.NONE,
                    null, null, new ArrayList<>(), 0, new Flag<>(ChannelEntryFlag.class),
                    "", "", "", "");

            List<EmbryonicCategoryReferencies> embCats = new ArrayList<>();
            ElementEnum paramTable = ElementEnum.ELEM_NONE;
            int statIndex = -1;
            StatElemType statType = StatElemType.NONE;
            UIEntryPriorityScheme pSource = UIEntryPriorityScheme.PRIORITY_SCHEME_NONE;
            int priority = 0;
            EmbryonicCategoryReferencies lastCat = null;

            // Directives in file order
            // No template in UIEntryBase

            if (entry.getStatOrElement() == StatElemType.ELEMENT) {
                paramTable = entry.getParameter();
                statIndex = -1;
                statType = StatElemType.ELEMENT;
            } else if (entry.getStatOrElement() == StatElemType.STAT) {
                paramTable = ElementEnum.ELEM_NONE;
                statIndex = entry.getStatParameter();
                statType = StatElemType.STAT;
            }

            nullEntry.setStatOrElement(statType);
            nullEntry.setElementParameter(paramTable);
            nullEntry.setStatParameter(statIndex);

            nullEntry.setRenderer(entry.getRenderer());
            nullEntry.setCombinerType(entry.getCombineType());
            nullEntry.setLabel(entry.getLabel());
            nullEntry.setLabel2(entry.getLabel2());
            nullEntry.setLabel5(entry.getLabel5());
            FlagView<ChannelEntryFlag> flags = entry.getEntryFlag();
            Flag<ChannelEntryFlag> newFlags = new Flag<>(ChannelEntryFlag.class);
            newFlags.copyFrom(flags);
            nullEntry.setEntryFlags(newFlags);

            for (UIEntryCategory category : entry.getCategories()) {
                if (embCats.stream().noneMatch(e -> e.getCategory().getName().equals(category.getName()))) {
                    EmbryonicCategoryReferencies embCat = new EmbryonicCategoryReferencies(category, pSource, priority, false);
                    embCats.add(embCat);
                    lastCategory = embCat;
                }
            }

            // Hatch
            if (entry.getCombineType() == null) {
                String message = "Fatal error occurred while parsing entry " + entry.getName() + " no combiner found!";
                logger.fatal(message);
                throw new RuntimeException(message);
            }

            int numOfParams = 1;
            if (entry.getStatOrElement() == StatElemType.ELEMENT) {
                for (ElementEnum paramEnum : ElementEnum.values()) {
                    if (!paramEnum.isHasResistRune()) continue;

                    String pName = paramEnum.name().substring(5);
                    UIEntry out = nullEntry.copy();
                    String name = (paramTable == ElementEnum.ELEM_NONE) ? entry.getName()
                            : entry.getName() + "<" + pName + ">";
                    out.setName(name);
                    String label = entry.getLabel().isEmpty() ? pName : entry.getLabel();
                    out.setLabel(label);
                    for (int index = 0; index < 10; index++) {
                        String shortStr = entry.getShortenedLabel(index);
                        if (shortStr == null) shortStr = "";
                        out.setShortenedLabel(index, shortStr);
                    }
                    int defPriority = (pSource == UIEntryPriorityScheme.PRIORITY_SCHEME_NONE)
                            ? entry.getDefaultPriority()
                            : pSource.getPriority(paramEnum.ordinal());
                    out.setDefaultPriority(defPriority);
                    out.setParamIndex(paramTable == ElementEnum.ELEM_NONE ? -1 : paramEnum.ordinal());

                    for (EmbryonicCategoryReferencies embCat : embCats) {
                        String catName = embCat.getCategory().getName();
                        int catPriority = embCat.isPrioritySet()
                                ? (embCat.getpSourceIndex() != UIEntryPriorityScheme.PRIORITY_SCHEME_NONE)
                                ? embCat.getpSourceIndex().getPriority(paramEnum.ordinal())
                                : embCat.getPriority()
                                : 0;
                        boolean isSet = embCat.isPrioritySet();
                        UIEntryCategory cat = new UIEntryCategory(catName, catPriority, isSet);
                        out.getCategories().add(cat);
                    }

                    entries.add(out);
                }
            } else if (entry.getStatOrElement() == StatElemType.STAT) {
                for (int index = 0; index < 5; index++) {
                    String pName = PlayerEventStatusUpdate.getPlayerStatusView().statString()[index];
                    UIEntry out = nullEntry.copy();
                    String name = (statIndex == -1) ? entry.getName()
                            : entry.getName() + "<" + pName + ">";
                    out.setName(name);
                    String label = entry.getLabel().isEmpty() ? pName : entry.getLabel();
                    out.setLabel(label);
                    for (int ind = 0; ind < 10; ind++) {
                        String shortStr = entry.getShortenedLabel(ind);
                        if (shortStr == null) shortStr = "";
                        out.setShortenedLabel(ind, shortStr);
                    }
                    int defPriority = (pSource == UIEntryPriorityScheme.PRIORITY_SCHEME_NONE)
                            ? entry.getDefaultPriority()
                            : pSource.getPriority(index);
                    out.setDefaultPriority(defPriority);
                    out.setParamIndex(statIndex == -1 ? -1 : index);

                    for (EmbryonicCategoryReferencies embCat : embCats) {
                        String catName = embCat.getCategory().getName();
                        int catPriority = embCat.isPrioritySet()
                                ? (embCat.getpSourceIndex() != UIEntryPriorityScheme.PRIORITY_SCHEME_NONE)
                                ? embCat.getpSourceIndex().getPriority(index)
                                : embCat.getPriority()
                                : 0;
                        boolean isSet = embCat.isPrioritySet();
                        UIEntryCategory cat = new UIEntryCategory(catName, catPriority, isSet);
                        out.getCategories().add(cat);
                    }

                    entries.add(out);
                }
            } else {
                UIEntry out = nullEntry.copy();
                String name = entry.getName();
                out.setName(name);
                String label = entry.getLabel();
                out.setLabel(label);
                for (int index = 0; index < 10; index++) {
                    String shortStr = entry.getShortenedLabel(index);
                    if (shortStr == null) shortStr = "";
                    out.setShortenedLabel(index, shortStr);
                }
                int defPriority = entry.getDefaultPriority();

                for (EmbryonicCategoryReferencies embCat : embCats) {
                    String catName = embCat.getCategory().getName();
                    int catPriority = embCat.isPrioritySet()
                            ? (embCat.getpSourceIndex() != UIEntryPriorityScheme.PRIORITY_SCHEME_NONE)
                            ? embCat.getpSourceIndex().getPriority(0)
                            : embCat.getPriority()
                            : 0;
                    boolean isSet = embCat.isPrioritySet();
                    UIEntryCategory cat = new UIEntryCategory(catName, catPriority, isSet);
                    out.getCategories().add(cat);
                }
                out.setParamIndex(-1);

                entries.add(out);
            }
        }
    }
}
