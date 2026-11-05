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
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entry.EmbryonicCategoryReferencies;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Second stage of the {@code ui_entry_base.txt} pipeline: turns the raw string
 * {@link UIEntryBaseParseRecord}s emitted by the grammar into fully resolved
 * {@link UIEntry} template objects. This is where the domain look-ups that
 * the grammar deliberately avoids finally happen &mdash; the {@code renderer:}
 * name is resolved against the loaded {@code ui_entry_renderer.txt} renderers,
 * and the {@code combine:} name against the {@link CombinerName} enum.
 * <p>
 * Assembly is fail-fast: the first record that cannot be resolved ends the
 * load, and nothing is skipped. An unknown renderer name makes
 * {@link UIRegistry#getUIEntryRenderer} throw an {@link IllegalArgumentException};
 * an unknown combiner or flag name, a repeated name carrying a parameter, or a
 * record with no combiner throws a {@link DataFileParseException} naming the file
 * (and, for the combiner and flag, the record's line).
 *
 * <p>Class UIEntryBaseAssembler coded before 260920, commented in full on 260922, updated on 261105
 * for the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class UIEntryBaseAssembler implements Assembler<UIEntryBaseParseRecord, List<UIEntry>> {
    /**
     * Logger for the fatal states this class throws on: an unknown combiner or flag name in
     * {@link #assemble}, and, in {@code parseEachEntry}, an override record whose parameter is
     * non-null or a record with no combiner. Each is logged at {@code fatal} just before its
     * {@link DataFileParseException}, standing in for C's {@code quit()}/{@code assert}.
     *
     * <p>Field logger coded before 260920, commented in full on 260920, updated on 261105 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger(UIEntryBaseAssembler.class);

    /**
     * Resolves each {@link UIEntryBaseParseRecord} into a {@link UIEntry} and folds it into the
     * result list through {@code parseEachEntry}. The renderer name is looked up with
     * {@link UIRegistry#getUIEntryRenderer}, the combine name with {@link CombinerName#valueOf}, and
     * each {@code |}-separated flag as {@code ENTRY_FLAG_<NAME>}.
     *
     * <p>The first record that fails ends the load; none is skipped and no partial list is
     * returned.
     *
     * <p>Function assemble coded before 260920, commented in full on 260922, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param records  the raw template records produced by the grammar
     * @param filename the file the records were read from, named in every error message
     * @return the assembled {@link UIEntry} templates, in source order
     * @throws IllegalArgumentException if a renderer name matches no loaded renderer
     * @throws DataFileParseException   if a combine or flag name is unknown, or
     *                                  {@code parseEachEntry} refuses the record
     */
    @Override
    public List<UIEntry> assemble(@NotNull List<UIEntryBaseParseRecord> records,
                                      @NotNull String filename) {
        List<UIEntry> entries = new ArrayList<>();

        for (UIEntryBaseParseRecord record : records) {
            int line = record.lineNumber();

            String name = record.name();
            String rendererName = record.renderer();
            UIEntryRenderer renderer = UIRegistry.getUIEntryRenderer(rendererName);
            
            if (renderer == null) {
                String message = "UI entry base starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal renderer name value: " + record.renderer();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }

            CombinerName combinerName;
            try {
                combinerName = CombinerName.valueOf(record.combine());
            } catch (IllegalArgumentException e) {
                String message = "UI entry base starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal combiner name value: " + record.combine();
                logger.fatal(message, e);
                throw new DataFileParseException(message);
            }
            List<String> categories = record.categories();
            String flags = record.flags();
            String desc = record.desc();

            List<UIEntryCategory> cats = new ArrayList<>();

            for (String category : categories) {
                cats.add(new UIEntryCategory(category, 0, false));
            }

            Flag<ChannelEntryFlag> flag = new Flag<>(ChannelEntryFlag.class);
            String[] flagStrings = flags.split("\\|");
            for (String flagString : flagStrings) {
                flagString = flagString.trim();
                try {
                    ChannelEntryFlag channelEntryFlag = ChannelEntryFlag.valueOf("ENTRY_FLAG_"
                            + flagString.toUpperCase());
                    flag.on(channelEntryFlag);
                } catch (IllegalArgumentException e) {
                    String message = "UI entry base starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal flag value: " + flagString;
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            }

            UIEntry entry = new UIEntry(name, null,
                    null, null, renderer, combinerName, cats,
                    0, "", flag, desc, "", "", "");

            parseEachEntry(entries, entry, filename);
        }

        return entries;
    }

    /**
     * Folds one {@code ui_entry_base.txt} record's raw {@link UIEntry} into the shared entry list,
     * either merging it into an already-inserted entry of the same name (the override path) or
     * turning it into a fresh, {@code TEMPLATE_ONLY}-flagged entry (the create path). Together with
     * the flag it sets on every path, this is the Java form of C's {@code hatch_embryo}
     * (in {@code ui-entry.c}) as it applies to the base file specifically, and of
     * {@code run_parse_ui_entry}'s post-base-file loop that OR's {@code ENTRY_FLAG_TEMPLATE_ONLY}
     * into every entry parsed so far - done here per entry rather than in one pass afterwards.
     * <p>
     * The override path (an {@code entries} lookup by name finds an existing entry) mirrors
     * {@code hatch_embryo}'s {@code embryo->exists} branch: the renderer, combiner and flags are
     * overwritten outright, and only categories not already present are added, at the existing
     * entry's own default priority and with {@code priority_set} left {@code false} - matching
     * {@code insert_embryo_category}'s call from {@code parse_entry_category}. It throws if the
     * incoming entry carries a non-null parameter, since C's {@code parse_entry_parameter} refuses
     * to parameterize an entry that already exists - a case {@code ui_entry_base.txt} never
     * produces, since no record in it repeats an earlier record's name.
     * <p>
     * The create path builds one {@link EmbryonicCategoryReferencies} per category (mirroring
     * {@code embryo->categories} before it is parameterized) and then expands the entry once per
     * element or per stat if the source record's parameter says to, or once outright otherwise -
     * the same three-way shape as {@code hatch_embryo}'s parameterized-name loop, though
     * {@code ui_entry_base.txt} only ever takes the "otherwise" branch, since none of its records
     * set a {@code parameter:}. It throws if the entry has no combiner, mirroring the
     * required-field check at the top of {@code hatch_embryo}.
     * <p>
     * Both throws are fatal and end the load, following the 261104 decision that every data-file
     * error is; neither message carries a line number, because the {@link UIEntry} passed in no
     * longer holds the record's line.
     *
     * <p>Function parseEachEntry coded before 260920, commented in full on 260920, updated on 261105
     * for the removal of the soft-error path.
     *
     * @param entries  the shared entry list being built up across all of {@code ui_entry_base.txt};
     *                 mutated in place
     * @param entry    the raw {@link UIEntry} {@link #assemble} built from one parse record, not yet
     *                 folded into {@code entries}
     * @param filename the file the record was read from, named in both error messages
     * @throws DataFileParseException if an entry of the same name already exists and {@code entry}
     *                                carries a non-null parameter, or if a new entry has no combiner
     */
    private void parseEachEntry(List<UIEntry> entries, UIEntry entry, String filename) {
        Flag<ChannelEntryFlag> fullFlags = new Flag<>(ChannelEntryFlag.class);
        fullFlags.copyFrom(entry.getEntryFlag());
        fullFlags.on(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
        entry.setEntryFlags(fullFlags);
        UIEntry existing = entries.stream().filter(e -> e.getName().equals(entry.getName()))
                .findFirst().orElse(null);
        if (existing != null) {
            Flag<ChannelEntryFlag> existingFlags = new Flag<>(ChannelEntryFlag.class);
            existingFlags.copyFrom(existing.getEntryFlag());
            for (ChannelEntryFlag flg : existingFlags)
                fullFlags.on(flg);
            UIEntryCategory lastCategory;
            // Override path
            if (entry.getParameter() != null) {
                String message = "UI entry base " + entry.getName() + " of file: " + filename + " has " +
                        "an illegal value, existing UIEntry with a non-null parameter";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }

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
            UIEntry nullEntry = new UIEntry(entry.getName(), null, ElementEnum.ELEM_NONE, StatElemType.NONE,
                    null, null, new ArrayList<>(), 0, "",
                    new Flag<>(ChannelEntryFlag.class), "", "", "", "");

            List<EmbryonicCategoryReferencies> embCats = new ArrayList<>();
            ElementEnum paramTable = ElementEnum.ELEM_NONE;
            int statIndex = -1;
            StatElemType statType = StatElemType.NONE;
            UIEntryPriorityScheme pSource = UIEntryPriorityScheme.PRIORITY_SCHEME_NONE;
            int priority = 0;
            EmbryonicCategoryReferencies lastCat = null;

            // Directives in file order

            if (entry.getStatOrElement() == StatElemType.ELEMENT) {
                paramTable = entry.getParameter();
                statType = StatElemType.ELEMENT;
            } else if (entry.getStatOrElement() == StatElemType.STAT) {
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
                String message = "UI entry base: " + entry.getName() + " of file: " + filename + " has " +
                        "caused a fatal error: No combiner found!";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }

            if (entry.getStatOrElement() == StatElemType.ELEMENT) {
                for (ElementEnum paramEnum : ElementEnum.values()) {
                    if (paramEnum == ElementEnum.ELEM_NONE || paramEnum == ElementEnum.ELEM_MAX) continue;

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
                out.setDefaultPriority(entry.getDefaultPriority());
                
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
