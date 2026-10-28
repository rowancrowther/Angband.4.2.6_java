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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ClassSpellParseRecord}s of one spellbook into domain {@link MagicSpell}s.
 * The four scalar casting parameters (level, mana, fail, exp) are parsed here; the spell's
 * effects are delegated to the shared {@link EffectAssembler}, which resolves each
 * {@code effect:} block against the effect registries (projections, timed effects, shapes, …).
 *
 * <p>This class is the port of {@code parse_class_spell()} and {@code parse_class_desc()} in
 * {@code init.c}, together with the {@code parse_class_effect()} family that attaches effects to
 * the spell, which {@link EffectAssembler} covers. C registers the line as
 * {@code spell sym name int level int mana int fail int exp}, so all four figures are plain
 * {@code int} fields read with {@code strtol} in base 0. {@link ClassSpellBookAssembler} calls
 * this class once per book and then hands each spell the book's realm; C sets the realm, and the
 * spell's index within the class ({@code sidx}) and its book's index ({@code bidx}), in
 * {@code parse_class_spell()} itself. The two indices are not stored (see {@link MagicSpell}).
 *
 * <p>A malformed figure is fatal, as it is in C, where the matching {@code PARSE_ERROR_*} stops
 * the parse: the problem is logged and a {@link DataFileParseException} naming the line and file
 * is thrown, so no spell is skipped and the load carried on. The {@code errors} list that the
 * {@link Assembler} contract passes in is never written to here; it is only handed on to
 * {@link EffectAssembler}, which does not write to it either.
 *
 * <p>Differences from C, none of which a shipped {@code class.txt} reaches:
 * <ul>
 *   <li>A negative figure, and {@code -0}, is refused for level, mana, fail and exp, where C
 *       stores it as given (accepted deviation, decided 261028).</li>
 *   <li>The four figures are checked level, fail, mana, exp, where C reads them level, mana,
 *       fail, exp. A line with two bad figures can report a different one, but both versions end
 *       the load. The figures reach this class already separated out by the grammar, so nothing
 *       else depends on the order (accepted deviation, decided 261028).</li>
 *   <li>A figure outside the range of an {@code int} is clamped, where C wraps it into its
 *       32-bit field (see {@code docs/Accepted_Deviations.md}).</li>
 *   <li>A spell with no {@code desc:} line carries an empty description, where C leaves the
 *       spell's {@code text} {@code NULL}.</li>
 * </ul>
 *
 * <p>Class ClassSpellAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class ClassSpellAssembler implements Assembler<ClassSpellParseRecord, List<MagicSpell>> {
    /**
     * Receives the fatal message for each rejected {@code spell:} block before the
     * {@link DataFileParseException} carrying the same text is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private final static Logger logger = LogManager.getLogger(ClassSpellAssembler.class);

    /**
     * Assembles every parsed {@code spell:} block in a book into a {@link MagicSpell}, parsing its
     * four scalar fields and resolving its effect blocks through {@link EffectAssembler}.
     *
     * <p><b>The figures</b> are C's four {@code int} fields. Each is first checked for a leading
     * {@code -} with {@link ParseUtils#minusFound(String)}, which is refused (a deviation from C,
     * see the class comment), and the rest is read by
     * {@link ParseUtils#parseIntString(String, String, int, String, String)}, the base-0 reading
     * of {@code strtol}: {@code 010} is 8 and {@code 08} is 0. The grammar's
     * {@code DELIM_INTEGER} token is decimal digits with an optional {@code -}, so the prefix
     * forms, a {@code +} and leading whitespace cannot arrive from a data file. There is no upper
     * bound on any figure, as in C, so a failure rate of 150 loads; one past
     * {@code Integer.MAX_VALUE} is clamped to it.
     *
     * <p>The effects are resolved once per spell, after the figures, so a bad figure stops the
     * load before the spell's effects are looked at. The description is the concatenation of the
     * spell's {@code desc:} lines, which the grammar joins with no separator as C's
     * {@code string_append} does, and is passed on unchanged.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw spell blocks for one book, in file order
     * @param errors   not written to by this method; it is passed on to {@link EffectAssembler}
     * @param filename the class file being read, for the error message
     * @return one {@link MagicSpell} per record, in file order; the realm is not set
     * @throws DataFileParseException if a figure is negative or has no digits, or an effect block
     *                                cannot be resolved
     */
    @Override
    public List<MagicSpell> assemble(@NotNull List<ClassSpellParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MagicSpell> spells = new ArrayList<>();

        for (ClassSpellParseRecord record : records) {
            int line = record.line();
            String spellName = record.name();
            if (ParseUtils.minusFound(record.level())) {
                String msg = "Class spell starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative level value: " + record.level();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int level = ParseUtils.parseIntString("Class spell", record.level(), line, filename, "level");
            if (ParseUtils.minusFound(record.fail())) {
                String msg = "Class spell starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative fail value: " + record.fail();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int fail = ParseUtils.parseIntString("Class spell", record.fail(), line, filename, "fail");
            if (ParseUtils.minusFound(record.mana())) {
                String msg = "Class spell starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative mana value: " + record.mana();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int mana = ParseUtils.parseIntString("Class spell", record.mana(), line, filename, "mana");
            if (ParseUtils.minusFound(record.exp())) {
                String msg = "Class spell starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative experience value: " + record.exp();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int exp = ParseUtils.parseIntString("Class spell", record.exp(), line, filename, "experience");
            
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            String spellDescription = record.desc();

            spells.add(new MagicSpell(spellName, level, fail, mana, exp, effects, spellDescription));
        }

        return spells;
    }
}
