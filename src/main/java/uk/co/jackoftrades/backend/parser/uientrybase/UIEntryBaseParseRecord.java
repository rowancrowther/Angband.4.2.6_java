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

import java.util.List;

/**
 * Immutable carrier for one raw {@code ui_entry_base.txt} template record, as
 * produced by {@link uk.co.jackoftrades.backend.parser.grammars.uientrybase.UIEntryBaseGrammar}'s
 * {@code entryBase} rule. Every field is still the plain text lexed from the
 * data file: no enum resolution, renderer look-up, or validation has happened
 * yet. That second stage is {@link UIEntryBaseAssembler}'s job, which turns each
 * record into a {@link uk.co.jackoftrades.frontend.entries.UIEntryBase}. Keeping
 * the parse output as dumb strings lets the grammar stay ignorant of the domain
 * and lets the assembler report errors against the original source line.
 *
 * @param name       the {@code name:} value; the symbolic key by which
 *                   {@code ui_entry.txt} entries pull this template in
 * @param renderer   the {@code renderer:} value; must name a renderer defined
 *                   in {@code ui_entry_renderer.txt}
 * @param combine    the {@code combine:} value; the raw name of a
 *                   {@link uk.co.jackoftrades.frontend.screen.enums.CombinerName}
 *                   describing how values from multiple sources are merged
 * @param flags      the {@code flags:} value affecting this template's behaviour
 * @param desc       the concatenation of every {@code desc:} line for this
 *                   record, joined by the grammar into a single description
 * @param categories the ordered list of {@code category:} values (at least one);
 *                   the UI categories this template belongs to
 * @param lineNumber the 1-based line in {@code ui_entry_base.txt} on which this
 *                   record's {@code name:} line starts, used for error messages
 * @author Rowan Crowther
 */
public record UIEntryBaseParseRecord(
        String name, String renderer, String combine,
        String flags, String desc, List<String> categories,
        int lineNumber) {
}
