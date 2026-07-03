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

package uk.co.jackoftrades.backend.parser.uientrybase;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

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

        for (UIEntryBaseParseRecord record : records) {
            int line = record.lineNumber();

            String name = record.name();
            String rendererName = record.renderer();
            UIEntryRenderer renderer = GameConstants.getUIEntryRenderer(rendererName, errors);
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

            results.add(new UIEntryBase(name, renderer, combinerName,
                    categories, flags, desc));
        }

        return results;
    }
}
