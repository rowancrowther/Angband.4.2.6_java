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
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.StartItem;
import uk.co.jackoftradesltd.middle.player.StartOptionExclusion;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ClassEquipParseRecord}s captured from a class's {@code equip:} lines into
 * domain {@link StartItem}s. This is the birth-equipment leg of the class loader and the port of
 * {@code parse_class_equip()} in {@code init.c}.
 *
 * <p>It is <em>not</em> self-contained: each entry's subtype is resolved against the loaded object
 * kinds through {@link ObjectRegistry}, as C's {@code lookup_sval()} does at parse time, so the
 * object kinds must be loaded before any class file. {@link ObjectRegistry#lookupObjectKind} throws
 * {@link IllegalStateException} if they are not. The {@link StartItem} still stores the subtype by
 * name; the kind found here is used only to prove the entry resolves.
 *
 * <p>A malformed entry is fatal, as it is in C, where the matching {@code PARSE_ERROR_*} stops the
 * parse: the problem is logged and a {@link DataFileParseException} naming the line and file is
 * thrown. That includes a bad exclusion option, which in C fails the whole line with
 * {@code PARSE_ERROR_INVALID_OPTION} (see {@link #parseEopts}). The {@code errors} list that the
 * {@link Assembler} contract passes in is never written to.
 *
 * <p>Accepted deviations (decided 261028), both in the numeric form of the subtype, which no
 * shipped {@code class.txt} reaches. C's {@code lookup_sval()} refuses a number of
 * {@code INT_MAX} or more, where this class accepts any run of digits. C's {@code strtoul} reads
 * {@code -0} as 0 and accepts it, where this class refuses any leading {@code -}. Numbers outside
 * the {@code int} range in {@code min} and {@code max} are refused rather than wrapped, as
 * everywhere in the port.
 *
 * <p>Class ClassEquipAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class ClassEquipAssembler implements Assembler<ClassEquipParseRecord, List<StartItem>> {
    /**
     * Receives the fatal message for each rejected {@code equip:} line before the
     * {@link DataFileParseException} carrying the same text is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private final static Logger logger = LogManager.getLogger(ClassEquipAssembler.class);
    /**
     * Assembles every parsed {@code equip:} line into a {@link StartItem}, resolving the tval and
     * subtype, parsing the quantity range and expanding the exclusion options.
     *
     * <p><b>The subtype</b> is read as C's {@code lookup_sval()} reads it. Text that is a number,
     * after leading whitespace, an optional {@code +} and trailing spaces or tabs, is taken as the
     * sval itself and is <em>not</em> checked against the loaded kinds, so {@code sword:99:1:1:none}
     * is accepted. Anything else is matched by name, case-insensitively, through
     * {@link ObjectRegistry#lookupObjectKind(TValue, String)}. The {@link StartItem} keeps the
     * trimmed text with any {@code +} removed.
     *
     * <p>The line is refused with a {@link DataFileParseException} when:
     * <ul>
     *   <li>the tval name is unknown (C: {@code PARSE_ERROR_UNRECOGNISED_TVAL});</li>
     *   <li>the subtype begins with {@code -}, or is a name that no kind with that tval carries (C:
     *       {@code PARSE_ERROR_UNRECOGNISED_SVAL});</li>
     *   <li>{@code min} or {@code max} is empty, which C reports as a missing field;</li>
     *   <li>{@code min} or {@code max} is not an integer;</li>
     *   <li>{@code min} or {@code max} is negative, which C's {@code uint} field rejects as not a
     *       number;</li>
     *   <li>{@code min} or {@code max} is above 99 (C: {@code PARSE_ERROR_INVALID_ITEM_NUMBER}); or</li>
     *   <li>the {@code eopts} clause names an option that is unknown or not a birth option (C:
     *       {@code PARSE_ERROR_INVALID_OPTION}), even if its other options are valid.</li>
     * </ul>
     * Like C, it does not check that {@code min} is no greater than {@code max}. The checks run in
     * a different order from C, which reads {@code min} and {@code max} before the callback runs,
     * so a line with two faults can report a different one first.
     *
     * <p>Accepted deviations: a numeric subtype of {@code INT_MAX} or more is accepted, where C
     * refuses it, and {@code -0} is refused, where C reads it as 0 (decided 261028).
     *
     * <p><b>Order.</b> The result is in <em>reverse</em> file order. C pushes each entry onto the head
     * of the class's {@code start_items} chain, and {@code player_outfit()} walks that chain
     * head-first, so the last {@code equip:} line is given out first.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw equipment lines for one class, in file order
     * @param errors   unused; part of the {@link Assembler} contract, and failures are thrown, not appended
     * @param filename the class file being read, for the error message
     * @return the resolved starting items in reverse file order
     * @throws DataFileParseException if any line is malformed, as listed above
     * @throws IllegalStateException  if a subtype given by name is looked up before the object kinds are loaded
     */
    @Override
    public List<StartItem> assemble(@NotNull List<ClassEquipParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<StartItem> results = new ArrayList<>();

        for (ClassEquipParseRecord record : records) {
            int line = record.line();
            TValue tVal = TValue.fromName(record.tValue());
            if (tVal == null) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal tValue value: " + record.tValue();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            String sValue = record.sValue();
            String parsedSValue = ParseUtils.trimRearOnlySpaceAndTab(ParseUtils.cTrimFront(sValue));
            int sign = ParseUtils.signFound(parsedSValue);
            if (sign < 0) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an negative sValue number: " + record.sValue();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            if (sign > 0) 
                parsedSValue = parsedSValue.substring(1);
            if (!ParseUtils.onlyDigits(parsedSValue)) {
                ObjectKind kind = ObjectRegistry.lookupObjectKind(tVal, sValue);
                    if (kind == null) {
                        String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sValue number: " + record.sValue();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }    
            }
            if (ParseUtils.minusFound(record.min())) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative minimum value: " + record.min();
                logger.fatal(msg);
                throw new DataFileParseException(msg);                
            }
            if (record.min().isEmpty()) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an blank minimum value: " + record.min();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int min = ParseUtils.parseIntString("Class equipment", record.min(), line, filename, "minimum");
            if (min > 99) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an out of range minimum value: " + record.min();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            if (ParseUtils.minusFound(record.max())) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative maximum value: " + record.max();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            if (record.max().isEmpty()) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an blank maximum value: " + record.max();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int max = ParseUtils.parseIntString("Class equipment", record.max(), line, filename, "maximum");
            if (max > 99) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an out of range maximum value: " + record.max();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            List<StartOptionExclusion> eopts = parseEopts(record.eopts(), errors, line, filename);
            
            results.add(new StartItem(tVal, parsedSValue, min, max, eopts));
        }

        return new ArrayList<>(results.reversed());
    }

    /**
     * Parses the {@code eopts} clause of an {@code equip:} line into a list of
     * {@link StartOptionExclusion}s — the birth options that suppress (or, when {@code NOT-}
     * prefixed, require) this item. The literal {@code none} and empty tokens are ignored; the
     * clause may separate options with spaces or {@code |}.
     *
     * <p>Only birth options are valid here, as in {@code parse_class_equip()} ({@code init.c}), so a
     * non-birth or unknown option is an error. C returns {@code PARSE_ERROR_INVALID_OPTION} and
     * stops the parse, so this method does the same: the first bad token is logged and a
     * {@link DataFileParseException} is thrown, and no partial list is returned.
     *
     * <p>Tokens are split on spaces and {@code |} only, as C's {@code strtok(eopts, " |")} does, so
     * a tab is part of a token. {@code NOT-none} is ignored along with {@code none}, because C
     * strips the {@code NOT-} before it tests for {@code none}. A bare {@code NOT-}, and a doubled
     * {@code NOT-NOT-}, name no option and are errors.
     *
     * <p>Function parseEopts coded before 260915, commented in full on 261028.
     *
     * @param eopts    the raw exclusion clause text
     * @param errors   unused; failures are thrown rather than appended
     * @param line     the 1-based source line, for the error message
     * @param filename the class file being read, for the error message
     * @return the resolved exclusions, empty for {@code none}; never {@code null}
     * @throws DataFileParseException if any token names an unknown or non-birth option
     */
    private List<StartOptionExclusion> parseEopts(@NotNull String eopts, @NotNull List<String> errors, int line, String filename) {
        List<StartOptionExclusion> result = new ArrayList<>();
        String[] eoptPart = eopts.split("[ |]+");

        for (String part : eoptPart) {
            if (part.equals("NOT-none") || part.equals("none"))
                continue;
            boolean negated = part.startsWith("NOT-");
            String suffix = negated ? part.substring(4) : part;
            if (part.isEmpty()) continue;
            
            String partFlag = "OP_" + suffix;
            PlayerOptionEnum partOption;
            try {
                partOption = PlayerOptionEnum.valueOf(partFlag);
                if (partOption.getPlayerOptionType() != PlayerOptionTypes.BIRTH) {
                    String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal non-birth option value: " + partFlag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else
                    result.add(new StartOptionExclusion(partOption, negated));
            } catch (IllegalArgumentException e) {
                String msg = "Class equipment starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal birth option value: " + part;
                logger.fatal(msg, e);
                throw new DataFileParseException(msg);
            }
        }
        
        return result;
    }
}