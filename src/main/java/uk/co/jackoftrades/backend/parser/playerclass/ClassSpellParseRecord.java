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

package uk.co.jackoftrades.backend.parser.playerclass;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.List;

/**
 * The raw capture of one {@code spell:} block from {@code class.txt}, together with the
 * {@code desc:} text and {@code effect:} blocks that follow it. Scalar fields are held as
 * verbatim file text and turned into a {@link uk.co.jackoftrades.middle.magic.MagicSpell} by
 * {@link ClassSpellAssembler}; the {@link #effects} are the already-structured effect DTOs the
 * shared {@code EffectBlock} grammar produced, handed on to
 * {@link uk.co.jackoftrades.backend.parser.grammars.EffectAssembler} for resolution.
 *
 * <p>Format: {@code spell:name:level:mana:fail:exp} followed by its {@code effect:}/{@code desc:} lines.
 *
 * @param name    the spell's display name
 * @param level   the character level at which the spell becomes castable, as raw text
 * @param mana    the mana cost, as raw text
 * @param fail    the base failure percentage, as raw text
 * @param desc    the concatenated {@code desc:} description text
 * @param effects the parsed effect blocks produced when the spell is cast, in file order
 * @param exp     the first-cast experience reward, as raw text
 * @param line    the 1-based source line, retained so soft errors can point back at the file
 * @author Rowan Crowther
 */
public record ClassSpellParseRecord(String name,
                                    String level,
                                    String mana,
                                    String fail,
                                    String desc,
                                    List<EffectParseRecord> effects,
                                    String exp,
                                    int line) {
}
