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
 * live {@code struct embryonic_ui_entry} (the {@code parse_entry_*} callbacks in {@code ui-entry.c}),
 * this port receives the
 * whole record pre-parsed as one {@link UIEntryParseRecord} and does that same directive-by-directive
 * work - template inheritance, renderer/combiner/label/flag resolution, category/priority placement, and
 * {@code parameter:element}/{@code parameter:stat} expansion into per-value entries - in one pass across
 * {@link #assemble} and {@link #parseEachEntry}. The per-value expansion is the Java form of
 * {@code hatch_embryo}'s parameterized-name loop ({@code ui-entry.c}); folding into an
 * already-registered entry of the same name is the Java form of {@code hatch_embryo}'s
 * {@code embryo->exists} branch - exercised here (unlike in {@code UIEntryBaseAssembler}) whenever a
 * {@code ui_entry.txt} record's name already appears in the registry. Unlike
 * {@code UIEntryBaseAssembler}'s override branch, this one is live: the thirteen
 * {@code resist_ui_compact_0<TAG>} specialization records (e.g. {@code resist_ui_compact_0<ACID>})
 * match the per-element names the earlier {@code parameter:element} record for
 * {@code resist_ui_compact_0} fanned out, so all thirteen take this branch (see
 * {@code docs/precis/260920.md}, "Correction: the create path is not the common case").
 * <p>
 * Assembly is best-effort and error-collecting, like {@code UIEntryBaseAssembler}: a record whose
 * renderer, combiner, template or parameter cannot be resolved is skipped with a message appended to
 * {@code errors}, and processing continues with the next record.
 * <p>
 * The port still diverges from C in several latent ways that no shipped {@code ui_entry.txt} record
 * reaches; each is recorded on the method it lives in ({@link #buildCategories}, {@link #assemble},
 * {@link #parseEachEntry}) and collected in {@code docs/precis/260929.md}.
 *
 * <p>Class UIEntryAssembler coded before 260922, commented in full on 260929.
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
     * <p>Field logger coded before 260922, commented in full on 260929.
     */
    private final static Logger logger = LogManager.getLogger(UIEntryAssembler.class);

    /**
     * The in-progress {@link UIEntryEmbryo} for the record currently being folded by
     * {@link #parseEachEntry(List, UIEntry)}'s create path - the Java form of C's
     * {@code struct embryonic_ui_entry} ({@code ui-entry.c}) while a parser works through
     * one record's directives. Reassigned (never read back across calls) by the create path each time
     * {@link #parseEachEntry} folds in an entry whose name is not already in the registry - once per
     * expanded value for a {@code parameter:} record - and reset to {@code null} at the top of
     * {@link #assemble}; left unused by the override path, which mutates the existing {@link UIEntry}
     * directly instead. Being static, it is shared across assembler instances, so the class is not
     * safe to run concurrently.
     *
     * <p>Field embryo coded before 260922, commented in full on 260929.
     */
    private static UIEntryEmbryo embryo;

    /**
     * Builds the resolved category list for one {@link UIEntryParseRecord}, splitting on where the
     * record's (at most one) {@code priority:} line falls: categories in {@code before} were written
     * with no priority yet in force, categories in {@code after} were written once the priority from
     * that line was in force. The Java form of the repeated {@code parse_entry_category}/
     * {@code parse_entry_priority} directive calls C performs while parsing one record
     * ({@code ui-entry.c}), collapsed here into a single pass over the pre-parsed record.
     * <p>
     * Only the last entry in {@code before} is given {@code priority}/{@code prioritySet=true}
     * directly - matching C's {@code embryo->last_category_index} always pointing at the most recently
     * seen category ({@code parse_entry_category}) - every other {@code before} category is left
     * {@code prioritySet=false} so {@code UIDataLoader}'s finishing pass fills it from the entry's own
     * default priority later, the same as C's finishing pass in {@code ui-entry.c}.
     * <p>
     * <b>Known divergence (found 260922, unfixed):</b> every {@code after} category is also stamped
     * with {@code priority} here (with {@code prioritySet=false}, so it is really only waiting on the
     * entry's default priority). That is correct only when {@code before} is empty - i.e. when the
     * {@code priority:} line had no preceding category and so really did set the record's default
     * ({@code parse_entry_priority}'s {@code last_category_index == -1} branch). When {@code before} is
     * non-empty, C leaves {@code default_priority} at 0, because the {@code priority:}
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
     * <p>Function buildCategories coded before 260922, commented in full on 260929.
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
     * Unlike the {@code parameter:element} loop, which threads its resolved
     * {@link ElementEnum} straight through the {@link UIEntry} constructor's
     * {@code parameter} argument, the {@code UIEntry} constructor has no slot for a
     * stat index, so the {@code parameter:stat} loop stamps it on afterward with
     * {@link UIEntry#setStatParameter(int)} - the Java form of C's
     * {@code entry->param_index = i} assignment in {@code hatch_embryo}'s
     * parameterized-name loop ({@code ui-entry.c}). Without it, every
     * per-stat entry silently carries Java's default {@code int} value ({@code 0},
     * STR's index) regardless of which of the five stats it actually is - found and
     * fixed 260922, once a regression test for the override-path fix below caught it.
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
     * <p><b>Outstanding (found 260922):</b> the {@code parameter:stat} expansion loop only creates
     * an entry for stat index {@code i} when
     * {@code PlayerEventStatusUpdate.getPlayerStatusView().statString()[i]} is non-empty - a guard
     * C's {@code hatch_embryo} has no counterpart for, since it parameterizes unconditionally over
     * all of {@code get_stat_count()}'s entries ({@code ui-entry.c}) and
     * {@code stat_names[]} is a fixed five-element array that is never empty. Currently inert:
     * {@code statString} is seeded from the same hardcoded {@code {"STR","INT","WIS","DEX","CON"}}
     * order in {@code PlayerEventStatusUpdate} and is never actually empty at any index, so
     * the guard never filters anything out in practice.
     *
     * <p><b>Outstanding (found 260929):</b> two smaller divergences from C's error handling. A
     * {@code priority:} value outside the {@code int} range makes {@code Integer.parseInt} throw, so
     * the record is rejected with an error, where C's {@code strtol} clamps it to {@code INT_MIN} or
     * {@code INT_MAX}. And when the create path fails for a {@code parameter:element} record (no
     * combiner), the loop appends one {@code "Bad entry"} message per element, where C's
     * {@code hatch_embryo} fails the record once. The bad-flag message also quotes the record's first
     * flag rather than the one that failed. All are latent, not live.
     *
     * <p>Function assemble coded before 260922, commented in full on 260929.
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
                        UIEntry entry = new UIEntry(name + "<" + statStr + ">", template, parameter, statElemType,
                                renderer, combinerName, categories, newPriorityNum, priorityStr,
                                flag, desc, newLabel, label5, label2);
                        // The constructor has no stat-index slot (unlike elementParameter above);
                        // stamp it directly so an override merge (parseEachEntry) can later resolve
                        // an index/negative_index priority scheme against it.
                        entry.setStatParameter(i);
                        if (!parseEachEntry(results, entry)) {
                            errors.add("Bad entry: " + name + " no combiner found.");
                            continue;
                        }
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
                    if (!parseEachEntry(results, new UIEntry(name + "<" + elementEnum.name().substring(5) + ">",
                            template, elementEnum, statElemType, renderer, combinerName,
                            categories, newPriorityNum, priorityStr, flag, desc, newLabel, label5, label2))) {
                        errors.add("Bad entry: " + name + " no combiner found.");
                        continue;
                    }
                }
            } else {
                List<UIEntryCategory> categories
                        = buildCategories(categoriesNames1, categoriesNames2, priorityNum, 0);

                if (!parseEachEntry(results, new UIEntry(name, template, parameter, statElemType,
                        renderer, combinerName, categories,
                        priorityNum, priorityStr, flag,
                        desc, label, label5, label2))) {
                    errors.add("Bad entry: " + name + " no combiner found.");
                    continue;
                }
            }
        }

        return results;
    }

    /**
     * Folds one resolved {@link UIEntry} - built by {@link #assemble} from a single
     * {@link UIEntryParseRecord} - into the shared {@code results} list, either merging it into an
     * already-registered entry of the same name (the override path) or turning it into a fresh entry
     * via an {@link UIEntryEmbryo} (the create path). Together these are the Java form of
     * {@code hatch_embryo} and the individual {@code parse_entry_*}
     * directive callbacks ({@code ui-entry.c}) that mutate a record's
     * {@code embryonic_ui_entry} while it is being read, collapsed here into one pass over an
     * already-fully-parsed record.
     * <p>
     * The override path (a {@code results} lookup by name finds an existing entry) is live - the
     * thirteen {@code resist_ui_compact_0<TAG>} records reach it, since their names match what the
     * earlier {@code parameter:element resist_ui_compact_0} record's fan-out already inserted. It
     * throws if the incoming entry still carries a stat or element parameter, mirroring
     * {@code parse_entry_parameter}'s refusal to parameterize an entry that already exists
     * ({@code ui-entry.c}) - that specific throw is unreached by shipped data, since
     * none of the thirteen records carries its own {@code parameter:} line (and {@link #assemble}
     * rejects such a record before it gets here). It then applies the record's template (if
     * any), renderer, combiner, label, shortened labels and flags directly onto the existing entry -
     * each an unconditional overwrite in C ({@code parse_entry_renderer}/{@code parse_entry_combine}/
     * {@code parse_entry_label}) except flags, which C only ever ORs
     * in ({@code parse_entry_flags}) - before merging in categories
     * not already present and applying the record's priority to either the existing entry's default
     * priority or its last-merged category, matching {@code parse_entry_priority}'s
     * {@code last_category_index} branch.
     * <p>
     * The create path builds a blank {@link UIEntry} carrying the record's own flags, wraps it in a
     * {@link UIEntryEmbryo}, applies the record's template the same way {@code parse_entry_template}
     * does, then applies renderer/combiner/label/shortened-labels/categories from the
     * record, and finally rejects the entry (returning {@code false}) if it still has no combiner, as
     * {@code hatch_embryo} does. Carrying the record's flags onto the blank entry was fixed 260929: it
     * had been dropping them, which lost {@code TIMED_AS_AUX} on all thirteen
     * {@code resist_ui_compact_0<TAG>} entries. The categories were already given their resolved priority by
     * {@link #buildCategories(List, List, int, int)} before this method ever sees them; see that
     * method's Javadoc for a known divergence in how a category-attached (rather than record-default)
     * priority is threaded through here. The blank entry's constructor carries across {@code entry}'s
     * resolved element parameter directly, but has no slot for a stat index, so
     * {@link UIEntry#getStatParameter()} is copied across separately with
     * {@link UIEntry#setStatParameter(int)} - without it, an override targeting a stat-parameterized
     * entry (see the {@code existing.getStatParameter()} read in the override path) would resolve against the
     * wrong (default {@code 0}) index; found and fixed 260922 via {@code assemble}'s
     * {@code parameter:stat} loop.
     * <p>
     * <b>Outstanding (found 260929), override path:</b> {@code lastCategory} is the last category of
     * the whole merged list, so a {@code priority:} written before any category sets that last
     * category's priority where C sets the entry's default priority; a template's flags are never
     * copied onto the existing entry, where C sets {@code flags = template flags & ~TEMPLATE_ONLY};
     * and an {@code index} or {@code negative_index} scheme on a non-parameterized entry resolves to
     * {@code 0} where C uses {@code param_index} ({@code -1}).
     * <p>
     * <b>Outstanding (found 260929), create path:</b> the template's own default priority and each
     * template category's priority and {@code priority_set} are replaced by the record's priority and
     * {@code prioritySet=false}, where C copies them from the template; a template's flags replace the
     * record's own flags rather than being OR'd with them, so a record with both a {@code template:}
     * and {@code flags:} line would lose the latter; and categories stay in insertion order where C
     * keeps them sorted by name. All are latent: {@code ui_entry_base.txt} gives no base entry a
     * priority or a category priority, and no shipped record has both a template and flags.
     *
     * @param results the shared entry list being built up across all of {@code ui_entry.txt} (seeded
     *                from the registry's existing entries, including the {@code ui_entry_base.txt}
     *                placeholders); mutated in place
     * @param entry   the resolved {@link UIEntry} {@link #assemble} built from one parse record, not
     *                yet folded into {@code results}
     * @return {@code true} if the entry was merged or added; {@code false} if the create path rejected
     * it for having no combiner, in which case nothing is added to {@code results}
     *
     * <p>Function parseEachEntry coded before 260922, commented in full on 260929.
     */
    private boolean parseEachEntry(List<UIEntry> results, UIEntry entry) {
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
                if (existing.getStatOrElement() == StatElemType.ELEMENT) {
                    parmIndex = existing.getParameter().ordinal() - 1;
                } else if (existing.getStatOrElement() == StatElemType.STAT) {
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
            Flag<ChannelEntryFlag> entryFlags = new Flag<>(ChannelEntryFlag.class);
            entryFlags.copyFrom(entry.getEntryFlag());
            UIEntry blank = new UIEntry(entry.getName(), null, entry.getParameter(), entry.getStatOrElement(),
                    null, null, new ArrayList<>(), entry.getPriorityNum(), entry.getPriorityString(),
                    entryFlags, "To keep alive", null, null, null);
            // The constructor above carries entry's element parameter across but has no slot for a
            // stat index; copy it separately so a later override merge resolves against it correctly.
            blank.setStatParameter(entry.getStatParameter());

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

            if (embryo.getUiEntry().getCombineType() == null
                    || embryo.getUiEntry().getCombineType() == CombinerName.NONE) {
                return false;
            }
            
            results.add(embryo.getUiEntry());
        }

        return true;
    }
}