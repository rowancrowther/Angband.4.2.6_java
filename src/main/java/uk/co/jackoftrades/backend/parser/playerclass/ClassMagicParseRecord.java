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

import java.util.List;

/**
 * The raw capture of a class's {@code magic:} line together with all the {@code book:} blocks that
 * follow it — the caster half of a class definition. {@link ClassMagicAssembler} turns this into a
 * {@link uk.co.jackoftrades.middle.magic.ClassMagic}; a non-caster class produces no such record
 * (its {@link PlayerClassParseRecord#magic()} is {@code null}).
 *
 * <p>Format: {@code magic:firstSpell:spellWeight:numBooks} followed by one or more {@code book:} blocks.
 *
 * @param firstSpell  the character level of the class's first spell, as raw text
 * @param spellWeight the per-book weight that burdens spellcasting, as raw text
 * @param numBooks    the declared number of books the class uses, as raw text
 * @param books       the parsed spellbook blocks, in file order
 * @param line        the 1-based source line, retained so soft errors can point back at the file
 * @author Rowan Crowther
 */
public record ClassMagicParseRecord(String firstSpell,
                                    String spellWeight,
                                    String numBooks,
                                    List<ClassSpellBookParseRecord> books,
                                    int line) {
}
