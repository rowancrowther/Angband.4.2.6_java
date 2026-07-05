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

import java.util.List;

/**
 * Immutable data-transfer object for a single {@code name:} block of
 * {@code lib/gamedata/ui_entry.txt}, produced by the {@code UIEntryGrammar}
 * parser and consumed by {@link UIEntryAssembler}.
 * <p>
 * Every field is a <em>raw</em> string (or list of strings) captured verbatim
 * from the file: the grammar deliberately performs no enum resolution, no
 * registry look-ups and no defaulting, so this record carries exactly what was
 * written, warts and all. All the domain interpretation - resolving the
 * renderer/combiner/flag/parameter names, defaulting the optional fields and
 * pulling in the template - is deferred to the assembler. Keeping the parse
 * output as a flat, dependency-free DTO is what lets the grammar be tested in
 * isolation from the game's globals.
 * <p>
 * Optional single-valued fields default to the empty string (never
 * {@code null}) when their directive line is absent; the two list-valued fields
 * default to an empty list. A record is only created once its {@code name:}
 * line has parsed, so {@code name} and {@code line} are always populated.
 *
 * @param name      the entry's symbolic name with any {@code <TAG>} suffix
 *                  removed (e.g. {@code resist_ui_compact_0}); used to link the
 *                  entry from {@code bindui} fields and {@code template:} lines.
 * @param template  the name of the {@code ui_entry_base.txt} template supplying
 *                  default values for this entry's unset fields, or {@code ""}
 *                  if the entry declares no {@code template:} line.
 * @param label     the label shown for the element, or {@code ""} if unset.
 * @param label5    a 5-character abbreviated-label override, or {@code ""} if
 *                  unset.
 * @param label2    a 2-character abbreviated-label override, or {@code ""} if
 *                  unset.
 * @param parameter the element or stat this entry is bound to. For a
 *                  <em>specialized</em> entry it is the upper-case {@code <TAG>}
 *                  from the name (e.g. {@code ACID}); for a <em>generic</em>
 *                  entry it is the literal word {@code element} or {@code stat}
 *                  from a {@code parameter:} line (a {@code parameter:} line
 *                  overrides a tag). Empty string when the entry has neither.
 * @param renderer  the {@code ui_entry_renderer.txt} renderer name, or
 *                  {@code ""} if unset.
 * @param combine   the combiner name (e.g. {@code ADD}, {@code RESIST_0}) used
 *                  to fold values from multiple sources, or {@code ""} if unset.
 * @param priority  the raw priority text - {@code index}, {@code negative_index}
 *                  or a decimal integer - or {@code ""} if unset. Left
 *                  unvalidated here; the assembler decides its meaning.
 * @param category  the UI categories this entry belongs to, collected in source
 *                  order across both of the grammar's {@code category:} slots;
 *                  empty when the entry lists none.
 * @param flags     the entry-flag names from a {@code flags:} line (the
 *                  {@code |}-separated names, minus the {@code ENTRY_FLAG_}
 *                  prefix), in order; empty when the entry has no {@code flags:}
 *                  line.
 * @param desc      the description text, concatenated from any repeated
 *                  {@code desc:} continuation lines, or {@code ""} if unset.
 * @param line      the 1-based source line of the {@code name:} line that opened
 *                  this record, used to anchor assembler error messages.
 * @author Rowan Crowther
 */
public record UIEntryParseRecord(String name, String template, String label, String label5, String label2,
                                 String parameter, String renderer,
                                 String combine, String priority, List<String> category,
                                 List<String> flags, String desc, int line) {
}
