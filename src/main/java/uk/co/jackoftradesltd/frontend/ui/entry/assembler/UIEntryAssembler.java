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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entry.EmbryonicCategoryReferencies;
import uk.co.jackoftradesltd.frontend.ui.entry.UIEntryEmbryo;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.HelperFunctions;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.ArrayList;
import java.util.Comparator;
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
 * Assembly is fail-fast: a record whose parameter, renderer, combiner, template, priority or flags cannot
 * be resolved ends the load with a {@link DataFileParseException}, as a failed {@code parse_entry_*}
 * callback ends {@code parse_file} in C. A renderer that cannot be found is also appended to
 * {@code errors} by {@link UIRegistry#getUIEntryRenderer} before the exception is thrown.
 * <p>
 * The label directives are cut as C cuts them: {@code label} to {@code MAX_ENTRY_LABEL} (80)
 * characters, {@code label2} to 2 and {@code label5} to 5. {@link #registerLabel} stores the explicit
 * shortened labels and the prefixes of the full label; the slots left empty are filled in the
 * finishing pass, as {@code fill_out_shortened} does in C.
 * <p>
 * The port still diverges from C in several ways that no shipped {@code ui_entry.txt} record reaches.
 * Each is an accepted deviation, recorded under "UI entry" in {@code docs/Accepted_Deviations.md}: a
 * repeated adjacent {@code name:}, the timing of the no-combiner failure, the file order of
 * {@code template:}/{@code renderer:}/{@code combine:}/{@code flags:}/{@code priority:} lines, the
 * interim priority of a category added to an existing entry, the missing {@code label1} to
 * {@code label10} lines (only {@code label}, {@code label2} and {@code label5} are read), and a
 * {@code priority:} of {@code -2147483648} or below loading as {@code -2147483647}.
 *
 * <p>Class UIEntryAssembler coded before 260922, commented in full on 261102.
 *
 * @author Rowan Crowther
 */
public class UIEntryAssembler implements Assembler<UIEntryParseRecord, List<UIEntry>> {    
    /**
     * Logger for every fatal state this assembler throws a {@link DataFileParseException} on: an illegal
     * parameter, renderer, combiner, flag, priority or template, a pre-existing parameterized name, a
     * new entry with no combiner, and an override record whose incoming entry still carries a stat or
     * element parameter. Each mirrors a failed {@code parse_entry_*} callback, or a C
     * {@code quit()}/{@code assert}, that ends the load rather than a soft, error-collecting failure.
     *
     * <p>Field logger coded before 260922, commented in full on 261102.
     */
    private final static Logger logger = LogManager.getLogger(UIEntryAssembler.class);

    /**
     * The longest {@code label:} text kept, in characters - the Java form of C's {@code MAX_ENTRY_LABEL}
     * ({@code ui-entry.c}), the limit {@code parse_entry_label} passes to {@code text_mbstowcs}.
     * Text past it is dropped without an error.
     *
     * <p>Field MAX_ENTRY_LABEL coded before 261102, commented in full on 261102.
     */
    private final int MAX_ENTRY_LABEL = 80;

    /**
     * The name of the file being assembled, set at the top of {@link #assemble} and quoted in every
     * error message {@link #assemble} and {@link #parseEachEntry} throw. Replaced on each call, so an
     * instance reports the file of its latest call only.
     *
     * <p>Field filename coded before 261102, commented in full on 261102.
     */
    private String filename;

    /**
     * The in-progress {@link UIEntryEmbryo} for the record currently being folded by
     * {@link #parseEachEntry(List, UIEntry, int, List)}'s create path - the Java form of C's
     * {@code struct embryonic_ui_entry} ({@code ui-entry.c}) while a parser works through
     * one record's directives. Reassigned (never read back across calls) by the create path each time
     * {@link #parseEachEntry} folds in an entry whose name is not already in the registry - once per
     * expanded value for a {@code parameter:} record - and reset to {@code null} at the top of
     * {@link #assemble}; left unused by the override path, which mutates the existing {@link UIEntry}
     * directly instead. Being static, it is shared across assembler instances, so the class is not
     * safe to run concurrently.
     *
     * <p>Field embryo coded before 260922, commented in full on 261102.
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
     * Every {@code after} category is also stamped with {@code priority}, but with
     * {@code prioritySet=false}, so the value is only a placeholder: C adds such a category with
     * {@code priority_set = false} too, {@code hatch_embryo} resets it to 0, and the finishing pass
     * gives it the entry's final default priority. Whether that default is the record's priority or the
     * template's is decided in {@link #parseEachEntry}, which passes 0 as the new entry's default
     * when a category came before the {@code priority:} line ({@code parse_entry_priority}'s
     * {@code last_category_index != -1} branch), so the final values agree with C. The placeholder
     * itself is not the value C holds in between; nothing reads it before the finishing pass (see "A
     * category added to an existing entry gets a different interim priority" in
     * {@code docs/Accepted_Deviations.md}).
     *
     * @param before          category names seen before the record's priority (if any) took effect
     * @param after           category names seen after the record's priority took effect
     * @param priority        the record's resolved numeric priority
     * @param defaultPriority the priority given to every {@code before} category except the last;
     *                        always {@code 0} at the call sites, and moot regardless since
     *                        {@code prioritySet=false} means the finishing pass overwrites it
     * @param priorityFound   {@code true} if the record had a {@code priority:} line, which is what
     *                        makes the last {@code before} category carry {@code priority} with
     *                        {@code prioritySet=true}
     * @return the resolved category list, in {@code before} then {@code after} order
     *
     * <p>Function buildCategories coded before 260922, commented in full on 261102.
     */
    private static List<UIEntryCategory> buildCategories(List<String> before, List<String> after, int priority,
                                                         int defaultPriority, boolean priorityFound) {
        List<UIEntryCategory> categories = new ArrayList<>();
        for (int index = 0; index < before.size(); index++) {
            if (index == before.size() - 1 && priorityFound) {
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
     * objects and fold them into the shared list, throwing a {@link DataFileParseException} on the
     * first record whose present fields fail to resolve (an unknown {@code parameter:}, renderer,
     * combiner, template or flag, a {@code priority:} that is neither {@code index},
     * {@code negative_index} nor a signed decimal, or a new entry with no combiner). A
     * {@code priority:} number is refused if anything but digits follows the sign, as
     * {@code parse_entry_priority}'s {@code strtol} end check refuses it
     * ({@code PARSE_ERROR_NOT_NUMBER}), and a number past the {@code int} range is clamped.
     * Most records yield a single entry; a {@code parameter:stat}
     * record yields one per player stat and a {@code parameter:element} record one per element
     * (see the class comment on expansion). Each entry's {@code label}, {@code label2} and
     * {@code label5} are cut to 80, 2 and 5 characters, and its shortened-label slots are
     * filled by {@link #registerLabel}.
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
     * @param records  the raw parse records, in file order, from the grammar.
     * @param errors   the error sink; {@link UIRegistry#getUIEntryRenderer} appends a message to it
     *                 when a renderer cannot be found, just before this method throws. Mutated in
     *                 place.
     * @param filename the file being assembled, quoted in every error message
     * @return the entries seeded from {@link UIRegistry#getUIEntries()} (the
     * {@code ui_entry_base.txt} placeholders) plus every entry created from the records, sorted by
     * name as C's {@code entries[]} is - the per-stat entries of an expanded {@code parameter:stat}
     * record are therefore not kept where that record sat
     * @throws DataFileParseException on the first record that cannot be resolved, or whose
     *                                {@code parameter:} names an entry that already exists
     *
     * <p><b>Accepted deviation (261102):</b> a record that repeats the previous record's name
     * is resolved on its own here, where C's {@code parse_entry_name} returns at once and the second
     * record keeps editing the first embryo. If the first record had no parameter, a {@code parameter:}
     * line on the second ends the load as a pre-existing name; if it had one, the second record makes
     * a separate bare-name entry, and a second {@code parameter:} line ends the load in
     * {@code parseEachEntry}. A first entry that came from an earlier file is refused in C as well.
     * None of the shipped data repeats an adjacent name. See "UI entry" in
     * {@code docs/Accepted_Deviations.md}.
     *
     * <p><b>Accepted deviation (261102):</b> a new record with no combiner throws as soon as the record
     * is handled, quoting that record's line. C checks {@code combiner_index} first thing in
     * {@code hatch_embryo}, but that only runs when the next {@code name:} line arrives or the file
     * ends, so C reports the failure against the next record (or none) and fails a {@code parameter:}
     * record once, where Java fails on its first value. Every new shipped record has a {@code combine:}
     * or {@code template:} line. See "UI entry" in {@code docs/Accepted_Deviations.md}.
     *
     * <p><b>Accepted deviation (261102):</b> only {@code label}, {@code label2} and {@code label5} are
     * read, where C registers {@code label1} to {@code label10}. Shipped data uses only those three.
     * See "UI entry" in {@code docs/Accepted_Deviations.md}.
     *
     * <p><b>Accepted deviation (261102):</b> a {@code priority:} of {@code -2147483648} or below loads as
     * {@code -2147483647}, because {@link ParseUtils#parseIntString} clamps the digits to
     * {@code INT_MAX} before the sign is applied, where C's {@code strtol} clamps to {@code INT_MIN}.
     * The lowest shipped priority is {@code -12}. See "UI entry" in
     * {@code docs/Accepted_Deviations.md}.
     *
     * <p><b>Accepted deviation (261102):</b> the file order of the {@code template:}, {@code renderer:},
     * {@code combine:}, {@code flags:} and {@code priority:} lines is not kept, so the template is
     * always applied first (see {@link #parseEachEntry}). See "UI entry" in
     * {@code docs/Accepted_Deviations.md}.
     *
     * <p>Function assemble coded before 260922, commented in full on 261102.
     */
    @Override
    public List<UIEntry> assemble(@NotNull List<UIEntryParseRecord> records,
                                  @NotNull List<String> errors,
                                  @NotNull String filename) {
        this.filename = filename;
        List<UIEntry> results = new ArrayList<>(UIRegistry.getUIEntries());
        embryo = null;

        for (UIEntryParseRecord record : records) {
            boolean priorityFound = !record.priority().isEmpty();
            int line = record.line();

            ElementEnum parameter = ElementEnum.ELEM_NONE;
            String name = record.name();
            StatElemType statElemType = StatElemType.fromValue(record.parameter());
            if (statElemType == null) {
                String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal parameter value: " + record.parameter();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            String rendStr = record.renderer();
            UIEntryRenderer renderer;
            if (!rendStr.isEmpty()) {
                renderer = UIRegistry.getUIEntryRenderer(rendStr, errors);
                if (renderer == null) {
                    String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal renderer value: " + record.renderer();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            } else {
                renderer = null;
            }
            CombinerName combinerName;
            if (record.combine().isEmpty())
                combinerName = CombinerName.NONE;
            else {
                try {
                    combinerName = CombinerName.valueOf(record.combine());
                    if (combinerName == CombinerName.NONE) {
                        String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel combiner name value: " + record.combine();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal combiner name value: " + record.combine();
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            }

            // Check for existance
            UIEntry existingEntry = results.stream().filter(r -> r.getName().equals(name))
                    .findFirst().orElse(null);

            if ((statElemType == StatElemType.ELEMENT || statElemType == StatElemType.STAT)
                    && existingEntry != null) {
                String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                        "an pre-existing stat/parameter name: " + name;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }

            // priority must be either "index", "negative_index" or an integer
            int priorityNum = 0;
            String priorityStr = "";
            if (!record.priority().isEmpty()) {
                if (record.priority().equals("index") || record.priority().equals("negative_index")) {
                    priorityStr = record.priority();
                } else {
                    priorityStr = record.priority();
                    int sign = ParseUtils.signFound(priorityStr);
                    if (sign != 0) priorityStr = priorityStr.substring(1);
                    if (!ParseUtils.onlyDigits(priorityStr)) {
                        String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal priority value: " + record.priority();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    priorityNum = ParseUtils.parseIntString("UI entry", priorityStr, line, filename, "priority", ParseUtils.Radix.DECIMAL);
                    if (sign != 0) priorityNum = sign * priorityNum;
                }
            }
            Flag<ChannelEntryFlag> flag = new Flag<>(ChannelEntryFlag.class);
            if (!record.flags().isEmpty()) {
                for (String flagName : record.flags()) {
                    try {
                        ChannelEntryFlag flagType = ChannelEntryFlag.valueOf("ENTRY_FLAG_" + flagName);
                        if (flagType == ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY) {
                            String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal entry flag value: " + flagName;
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                        }
                        flag.on(flagType);
                    } catch (IllegalArgumentException e) {
                        String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                                "an unknown entry flag value: " + flagName;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            String desc = record.desc();
            String label = record.label();
            String label5 = record.label5();
            String label2 = record.label2();
            List<String> categoriesNames1 = record.categoriesBeforePriority();
            List<String> categoriesNames2 = record.categoriesAfterPriority();
            UIEntry template;
            if (record.template().isEmpty())
                template = null;
            else {
                template = results.stream().filter(r -> r.getName().equals(record.template()))
                        .findFirst().orElse(null);
                if (template == null) {
                    String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal template value: " + record.template();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            if (statElemType == StatElemType.STAT) {
                for (int i = 0; i < HelperFunctions.stats.length; i++) {
                    int newPriorityNum = switch (priorityStr) {
                        case "negative_index" -> -i;
                        case "index" -> i;
                        case "" -> 0;
                        default -> priorityNum;
                    };
                    String statStr = HelperFunctions.stats[i];
                    List<UIEntryCategory> categories =
                            buildCategories(categoriesNames1, categoriesNames2, newPriorityNum, 0, priorityFound);
                    String newLabel = record.label();
                    if (record.label().isEmpty())
                        newLabel = statStr;
                    int labelLength = Math.min(MAX_ENTRY_LABEL, newLabel.length());
                    newLabel = newLabel.substring(0, labelLength);
                    UIEntry entry = new UIEntry(name + "<" + statStr + ">", template, parameter, statElemType,
                            renderer, combinerName, categories, newPriorityNum, priorityStr,
                            flag, desc, newLabel, label5, label2);
                    registerLabel(entry, newLabel, label5, label2);
                    // The constructor has no stat-index slot (unlike elementParameter above);
                    // stamp it directly so an override merge (parseEachEntry) can later resolve
                    // an index/negative_index priority scheme against it.
                    entry.setStatParameter(i);
                    if (!parseEachEntry(results, entry, line, record.categoriesBeforePriority())) {
                        String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                                "no combiner";
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }

                }
            } else if (statElemType == StatElemType.ELEMENT) {
                for (ElementEnum elementEnum : ElementEnum.values()) {
                    if (elementEnum == ElementEnum.ELEM_MAX || elementEnum == ElementEnum.ELEM_NONE)
                        continue;

                    int newPriorityNum;
                    int indexNum = elementEnum.ordinal() - 1;
                    newPriorityNum = switch (priorityStr) {
                        case "negative_index" -> -indexNum;
                        case "index" -> indexNum;
                        case "" -> 0;
                        default -> priorityNum;
                    };
                    List<UIEntryCategory> categories =
                            buildCategories(categoriesNames1, categoriesNames2, newPriorityNum, 0, priorityFound);
                    String newLabel = label;
                    if (record.label().isEmpty())
                        newLabel = elementEnum.name().substring(5);
                    int newLabelLength = Math.min(MAX_ENTRY_LABEL, newLabel.length());
                    newLabel = newLabel.substring(0, newLabelLength);
                    UIEntry entry = new UIEntry(name + "<" + elementEnum.name().substring(5) + ">",
                            template, elementEnum, statElemType, renderer, combinerName,
                            categories, newPriorityNum, priorityStr, flag, desc, newLabel, label5, label2);
                    registerLabel(entry, label, label5, label2);

                    if (!parseEachEntry(results, entry, line, record.categoriesBeforePriority())) {
                        String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                                "no combiner";
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            } else {
                List<UIEntryCategory> categories
                        = buildCategories(categoriesNames1, categoriesNames2, priorityNum, 0, priorityFound);
                
                int labelLength = Math.min(MAX_ENTRY_LABEL, label.length());
                label = label.substring(0, labelLength);
                
                UIEntry entry = new UIEntry(name, template, parameter, statElemType,
                        renderer, combinerName, categories,
                        priorityNum, priorityStr, flag,
                        desc, label, label5, label2);

                registerLabel(entry, label, label5, label2);

                if (!parseEachEntry(results, entry, line, record.categoriesBeforePriority())) {
                    String message = "UI entry starting at line: " + line + " of file: " + filename + " has " +
                            "no combiner";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
        }
        results.sort(Comparator.comparing(UIEntry::getName));
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
     * each an overwrite in C when its line is present ({@code parse_entry_renderer}/
     * {@code parse_entry_combine}/{@code parse_entry_label}) except flags, which C only ever ORs
     * in ({@code parse_entry_flags}); a value the record left empty is skipped here, as an absent
     * line is skipped in C - before merging in categories
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
     * {@link #buildCategories(List, List, int, int, boolean)} before this method ever sees them; the
     * blank entry's default priority is the record's priority only when no category came before the
     * {@code priority:} line, and 0 otherwise. The blank entry's constructor carries across {@code entry}'s
     * resolved element parameter directly, but has no slot for a stat index, so
     * {@link UIEntry#getStatParameter()} is copied across separately with
     * {@link UIEntry#setStatParameter(int)} - without it, an override targeting a stat-parameterized
     * entry (see the {@code existing.getStatParameter()} read in the override path) would resolve against the
     * wrong (default {@code 0}) index; found and fixed 260922 via {@code assemble}'s
     * {@code parameter:stat} loop.
     * <p>
     * Where the two paths agree with C:
     * <ul>
     *   <li>The template's renderer, combiner, default priority and flags (with
     *       {@code TEMPLATE_ONLY} masked off) are copied first, and its categories are added with
     *       their own priority and {@code prioritySet}, as {@code parse_entry_template} does.</li>
     *   <li>The record's own flags are OR'd in, never replaced.</li>
     *   <li>A {@code priority:} number or scheme goes to the entry's default priority when no category
     *       came before the line, and otherwise to the last category before it
     *       ({@code categoriesBeforePriority}), as {@code parse_entry_priority} does with
     *       {@code last_category_index}. On the override path an {@code index} or
     *       {@code negative_index} scheme is resolved against the existing entry's parameter, or
     *       {@code -1} if it has none, as C's {@code param_index} is.</li>
     *   <li>Categories end up sorted by name, as {@code insert_embryo_category} keeps them.</li>
     *   <li>An empty {@code label:} or shortened label on the override path leaves the existing one
     *       alone, as an absent line does in C.</li>
     * </ul>
     * <p>
     * <b>Accepted deviations (261102):</b> the template is always applied before the record's own
     * renderer, combiner, flags and priority, where C applies them in file order; and a category
     * added to an existing entry carries a different placeholder priority than C's until the finishing
     * pass overwrites it. Both are recorded under "UI entry" in {@code docs/Accepted_Deviations.md}.
     *
     * @param results                  the shared entry list being built up across all of {@code ui_entry.txt} (seeded
     *                                 from the registry's existing entries, including the {@code ui_entry_base.txt}
     *                                 placeholders); mutated in place
     * @param entry                    the resolved {@link UIEntry} {@link #assemble} built from one parse record, not
     *                                 yet folded into {@code results}
     * @param line                     the line the record starts on, quoted in the duplicate-parameter error
     * @param categoriesBeforePriority the names of the categories written before the record's
     *                                 {@code priority:} line, used to find the category that
     *                                 line applies to and to decide whether it sets the entry's
     *                                 default priority instead
     * @return {@code true} if the entry was merged or added; {@code false} if the create path rejected
     * it for having no combiner, in which case nothing is added to {@code results}
     * @throws DataFileParseException if the override path is given an entry that still carries a stat
     *                                or element parameter
     *
     * <p>Function parseEachEntry coded before 260922, commented in full on 261102.
     */
    private boolean parseEachEntry(List<UIEntry> results, UIEntry entry, int line, List<String> categoriesBeforePriority) {
        UIEntry existing = results.stream()
                .filter(e -> e.getName().equals(entry.getName())).findFirst().orElse(null);
        if (existing != null) {
            // Override path
            if ((entry.getStatOrElement() == StatElemType.ELEMENT && entry.getParameter() != null) ||
                    (entry.getStatOrElement() == StatElemType.STAT && entry.getStatParameter() != -1)) {
                String message = "UI Entry starting at line: " + line + " of file: " + filename + " is " +
                        "a duplicate of a non null parameter";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }

            // Templates
            UIEntryCategory lastCategory = null;
            UIEntry template = entry.getTemplate();
            if (template != null) {
                Flag<ChannelEntryFlag> flagSet = new Flag<>(ChannelEntryFlag.class);
                flagSet.copyFrom(template.getEntryFlag());
                flagSet.off(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
                existing.setEntryFlags(flagSet);
                existing.setRenderer(template.getRenderer());
                existing.setCombinerType(template.getCombineType());
                existing.setDefaultPriority(template.getDefaultPriority());
                for (UIEntryCategory category : template.getCategories()) {
                    if (existing.getCategories().stream()
                            .noneMatch(c -> c.getName().equals(category.getName()))) {
                        existing.getCategories().add(category.copy());
                    }
                }
            }

            if (entry.getRenderer() != null)
                existing.setRenderer(entry.getRenderer());
            if (entry.getCombineType() != null && entry.getCombineType() != CombinerName.NONE)
                existing.setCombinerType(entry.getCombineType());
            if (entry.getLabel() != null && !entry.getLabel().isEmpty())
                existing.setLabel(entry.getLabel());

            for (int index = 0; index < 10; index++) {
                if (entry.getShortenedLabel(index) != null && !entry.getShortenedLabel(index).isEmpty())
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
                if (!categoriesBeforePriority.isEmpty()) {
                    String nameToCheck = categoriesBeforePriority.getLast();
                    lastCategory = existing.getCategories().stream()
                            .filter(c -> c.getName().equals(nameToCheck))
                            .findFirst().orElse(null);
                } else {
                    lastCategory = null;
                }
            }
            
            existing.getCategories().sort(Comparator.comparing(UIEntryCategory::getName));

            String scheme = entry.getPriorityString();
            int value;
            if (scheme.equals("negative_index") || scheme.equals("index")) {
                int parmIndex = -1;
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
            if (!scheme.isEmpty()) {
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
            int catPriorityToPass = entry.getPriorityNum();
            if (!categoriesBeforePriority.isEmpty())
                catPriorityToPass = 0;
            UIEntry blank = new UIEntry(entry.getName(), null, entry.getParameter(), entry.getStatOrElement(), 
                    null, null, new ArrayList<>(), catPriorityToPass, entry.getPriorityString(),
                    entryFlags, "To keep alive", null, null, null);
            // The constructor above carries entry's element parameter across but has no slot for a
            // stat index; copy it separately so a later override merge resolves against it correctly.
            blank.setStatParameter(entry.getStatParameter());

            UIEntryPriorityScheme pSourceIndex;
            String priorityString = entry.getPriorityString();
            if (priorityString.equals("negative_index")) {
                pSourceIndex = UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX;
            } else if (priorityString.equals("index")) {
                pSourceIndex = UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX;
            } else {
                pSourceIndex = UIEntryPriorityScheme.PRIORITY_SCHEME_NONE;
            }

            embryo = new UIEntryEmbryo(blank, new ArrayList<>(),
                    null, pSourceIndex, null, false);

            UIEntry template = entry.getTemplate();
            if (template != null) {
                embryo.getUiEntry().setRenderer(template.getRenderer());
                embryo.getUiEntry().setCombinerType(template.getCombineType());
                embryo.getUiEntry().setDefaultPriority(template.getDefaultPriority());
                if (!entry.getPriorityString().isEmpty() && categoriesBeforePriority.isEmpty()) {
                    embryo.getUiEntry().setDefaultPriority(entry.getPriorityNum());
                }
                Flag<ChannelEntryFlag> newFlags = new Flag<>(ChannelEntryFlag.class);
                newFlags.copyFrom(entry.getEntryFlag());
                for (ChannelEntryFlag flg : template.getEntryFlag()) {
                    newFlags.on(flg);
                }
                newFlags.off(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
                embryo.getUiEntry().setEntryFlags(newFlags);
                if (!template.getCategories().isEmpty()) {
                    for (UIEntryCategory category : template.getCategories()) {
                        UIEntryCategory copyCategory = category.copy();
                        EmbryonicCategoryReferencies embCat = new EmbryonicCategoryReferencies(copyCategory,
                                UIEntryPriorityScheme.PRIORITY_SCHEME_NONE,
                                embryo.getUiEntry().getDefaultPriority(), false);
                        embryo.getCategories().add(embCat);
                        embryo.getUiEntry().getCategories().add(copyCategory);
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
                } else if (category.isPrioritySet()) {
                    embCat.getCategory().setPriority(category.getPriority());
                    embCat.getCategory().setPrioritySet(true);
                }
            }
            
            embryo.getUiEntry().getCategories().sort(Comparator.comparing(UIEntryCategory::getName));

            if (embryo.getUiEntry().getCombineType() == null
                    || embryo.getUiEntry().getCombineType() == CombinerName.NONE) {
                return false;
            }

            results.add(embryo.getUiEntry());
        }

        return true;
    }
    
    /**
     * Stores the shortened labels a record gives an entry, cutting {@code label5} to 5 characters and
     * {@code label2} to 2 as {@code parse_entry_shortened_label} cuts them with its limit of N. The
     * Java form of that callback, run once per record on the finished {@link UIEntry}.
     * <p>
     * Slots are indexed by length minus one, as {@link UIEntry#setShortenedLabel(int, String)} takes
     * them:
     * <ul>
     *   <li>Slots 5 to 8 (6 to 9 characters) get prefixes of {@code label}.</li>
     *   <li>Slots 2 to 4 get prefixes of the cut {@code label5}, so slot 4 is {@code label5} itself,
     *       and slot 3 and slot 2 are its first 4 and 3 characters.</li>
     *   <li>Slots 0 and 1 get prefixes of the cut {@code label2}, so slot 1 is {@code label2} itself
     *       and slot 0 is its first character.</li>
     * </ul>
     * A slot whose source is empty is stored as {@code ""}, which is how {@link UIEntry} marks a slot
     * as unset ({@code nshortened == 0} in C). {@code UIDataLoader.fillOutShortened} then fills it in
     * the finishing pass from the nearest longer set slot, or the full label, as
     * {@code fill_out_shortened} does, so an absent {@code label5} leaves slots 2 to 4 to be derived
     * from the label. Slot 9 (10 characters) is left unset and filled the same way.
     * <p>
     * The full label itself is not cut here; {@link #assemble} cuts it to {@code MAX_ENTRY_LABEL}
     * before the entry is built. {@code label5} and {@code label2} are reassigned locally, so the
     * caller's copies are unchanged.
     *
     * <p>Function registerLabel coded on 261102, commented in full on 261102.
     *
     * @param entry  the entry whose shortened labels are set; its slots 0 to 8 are overwritten
     * @param label  the entry's full label, already cut, which slots 5 to 8 are prefixes of
     * @param label5 the record's {@code label5:} text, or {@code ""} if it had none
     * @param label2 the record's {@code label2:} text, or {@code ""} if it had none
     */
    private void registerLabel(UIEntry entry, String label, String label5, String label2) {
        for (int index = 8; index >= 5; index--) {
            int currLength = Math.min(label.length(), index + 1);
            String currLabel = label.substring(0, currLength);
            entry.setShortenedLabel(index, currLabel);
        }
        if (!label5.isEmpty()) {
            int l5Length = Math.min(label5.length(), 5);
            label5 = label5.substring(0, l5Length);
        }
        for (int index = 4; index >= 2; index--) {
            int currLength = Math.min(label5.length(), index + 1);
            String currLabel = label5.substring(0, currLength);
            entry.setShortenedLabel(index, currLabel);
        }
        if (!label2.isEmpty()) {
            int l2Length = Math.min(label2.length(), 2);
            label2 = label2.substring(0, l2Length);
        }
        for (int index = 1; index >= 0; index--) {
            int currLength = Math.min(label2.length(), index + 1);
            String currLabel = label2.substring(0, currLength);
            entry.setShortenedLabel(index, currLabel);
        }
    }
}