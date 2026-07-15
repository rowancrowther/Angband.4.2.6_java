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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns a class's raw {@link ClassMagicParseRecord} into a domain {@link ClassMagic} — the caster
 * profile. It parses the three {@code magic:} scalars (first-spell level, spell weight, book count)
 * and delegates the book list to {@link ClassSpellBookAssembler}, which does the heavier
 * cross-file resolution.
 *
 * <p>Although the {@link Assembler} contract is list-shaped, a class has at most one {@code magic:}
 * record; {@link PlayerClassAssembler} wraps the single record in a list and unwraps the single
 * result. A malformed scalar reports a soft error and skips that record.
 *
 * @author Rowan Crowther
 */
public class ClassMagicAssembler implements Assembler<ClassMagicParseRecord, List<ClassMagic>> {
    /**
     * Assembles the (at most one) parsed {@code magic:} record into a {@link ClassMagic}, parsing
     * its scalars and building its books via {@link ClassSpellBookAssembler}.
     *
     * @param records the raw magic record(s) for one class
     * @param errors  the soft-error channel; malformed scalars and book failures are appended here
     * @return the resolved caster profile(s)
     * @author Rowan Crowther
     */
    @Override
    public List<ClassMagic> assemble(@NotNull List<ClassMagicParseRecord> records, @NotNull List<String> errors) {
        List<ClassMagic> results = new ArrayList<>();

        for (ClassMagicParseRecord record : records) {
            int line = record.line();
            int firstSpell = 0;
            try {
                firstSpell = Integer.parseInt(record.firstSpell());
            } catch (NumberFormatException e) {
                errors.add("Line: " + line + " has " +
                        "an invalid first spell number: " + record.firstSpell());
                continue;
            }
            int spellWeight = 0;
            try {
                spellWeight = Integer.parseInt(record.spellWeight());
            } catch (NumberFormatException e) {
                errors.add("Line: " + line + " has " +
                        "an invalid spell weight number: " + record.spellWeight());
                continue;
            }
            int numBooks = 0;
            try {
                numBooks = Integer.parseInt(record.numBooks());
            } catch (NumberFormatException e) {
                errors.add("Line: " + line + " has " +
                        "an invalid number of books: " + record.numBooks());
                continue;
            }
            List<MagicBook> magicBooks = new ClassSpellBookAssembler().assemble(record.books(), errors);

            results.add(new ClassMagic(firstSpell, spellWeight, numBooks, magicBooks));
        }

        return results;
    }
}
