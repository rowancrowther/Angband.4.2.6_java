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

package uk.co.jackoftrades.backend.parser.pain;

import java.util.List;

/**
 * Raw parse record for one {@code type:} block of {@code pain.txt}, produced by {@code PainGrammar}
 * and consumed by {@link PainAssembler}. Following the pipeline's raw-string DTO convention, it
 * holds only unresolved text: no serial or message is interpreted until assembly.
 *
 * <p>{@code messages} arrives as {@code [typeNum, msg1..msg7]} - the {@code type:} serial number is
 * carried as element 0 ahead of the seven graduated pain messages, for the assembler to peel off.
 * {@code line} is the source line of the {@code type:} header, used to anchor any soft error the
 * assembler reports back to the file.
 *
 * @param messages the serial number followed by the seven pain-message strings, all raw
 * @param line     the 1-based source line of this block's {@code type:} header
 * @author Rowan Crowther
 */
public record PainParseRecord(List<String> messages, int line) {
}
