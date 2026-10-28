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

package uk.co.jackoftradesltd.backend.parser.objectbase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles the raw {@link ObjectBaseParseRecord}s parsed from {@code object_base.txt} into the
 * finished {@link ObjectBase} domain objects (reader &rarr; ParseRecord &rarr; assembler &rarr;
 * domain).
 *
 * <p>It stands in for the resolving half of the {@code parse_object_base_*} handlers in
 * {@code obj-init.c}: {@code parse_object_base_name} (the tval and the name),
 * {@code parse_object_base_graphics} (the colour), {@code parse_object_base_break},
 * {@code parse_object_base_max_stack} and {@code parse_object_base_flags}, together with the
 * zeroed slot {@code finish_parse_object_base} leaves in {@code kb_info} for a tval the file
 * does not mention. The other half of those handlers, copying the file-wide {@code default:}
 * values into each new record, is done by the grammar before a record reaches this class.
 *
 * <p>Every field that cannot be resolved ends the load. C's handler returns a parser error and
 * {@code init_angband} quits, so this class logs the message at fatal level and throws
 * {@link DataFileParseException} naming the record's line and the file; no record is skipped and
 * the {@code errors} list is never written to. The cases are an unknown tval, a flag that is
 * neither an object flag, a kind flag nor an element flag, a {@code HATES_} or {@code IGNORE_}
 * flag whose element does not exist, the bare token {@code NONE}, and a break chance or maximum
 * stack that {@link ParseUtils#parseIntString} cannot read. An unknown colour is the one
 * unresolved field that is not an error: C falls back to white, and so does this class.
 *
 * <p>The checks run in a different order from C's, which meets the lines of a record in file
 * order: here the tval is checked first, then the flags, then the two numbers. A record with more
 * than one fault therefore reports a different one first, and the load ends either way.
 *
 * <p>Class ObjectBaseAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class ObjectBaseAssembler implements Assembler<ObjectBaseParseRecord, List<ObjectBase>> {
    /**
     * Receives the message of each fatal path, at fatal level, just before the
     * {@link DataFileParseException} carrying the same text is thrown. Nothing else is logged.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger(ObjectBaseAssembler.class);

    /**
     * Resolve every parsed {@code object_base.txt} record into an {@link ObjectBase}: the tval
     * name to {@link TValue}, the colour to {@link ColourEnum}, each flag to an element, a kind
     * flag or an object flag, and the two numeric fields to {@code int}s. An unresolvable field
     * ends the load with a {@link DataFileParseException} (see the class comment). After every
     * record is processed, a synthetic {@code TV_NONE} base is appended unconditionally; it is
     * not driven by any parsed record.
     *
     * <p><b>Tval.</b> {@link TValue#fromName} stands in for {@code tval_find_idx}, so a number, a
     * display name in any case and either spelling of "armour" all resolve, and anything else is
     * fatal. An empty plural name becomes a {@code null} name, as C's {@code name} stays
     * {@code NULL} when the optional {@code name:} argument is absent.
     *
     * <p><b>Colour.</b> {@link ColourEnum#fromCode(String)} reads a single character as an index
     * character and anything longer as a name matched without regard to case, as
     * {@code parse_object_base_graphics} chooses between {@code color_char_to_attr} and
     * {@code color_text_to_attr}. Both answer {@code null} for an unknown colour where C answers
     * white, so a {@code null} is turned into {@link ColourEnum#COLOUR_WHITE} here.
     *
     * <p><b>Flags.</b> {@code parse_object_base_flags} offers each token to three tables and the
     * token is valid if any accepts it. A token with an underscore whose text before the first
     * underscore is a prefix of {@code HATES} (or of {@code IGNORE}), because C compares only that
     * many characters with {@code strncmp}, is an element flag: the text after the underscore must
     * name one of the real elements, exactly as spelled, and sets the matching hates (or ignores)
     * bit on the base. {@code ELEM_NONE} and {@code ELEM_MAX} are not elements in C's table, so
     * {@code HATES_NONE} and {@code IGNORE_MAX} are fatal. Every other token is tried as a kind
     * flag, then as an object flag. {@code NONE} is fatal, because C's lookup starts past the
     * placeholder at index 0 of both tables. {@code MAX} is the one name that sits in both tables
     * after that placeholder, so it switches on {@code KF_MAX} and {@code OF_MAX} together, and no
     * other name is in both, which is why trying kind before object is the same as C trying both.
     * C tests {@code IGNORE} before {@code HATES}, which matters only for an empty tag, and the
     * grammar's letter-initial flag token cannot produce one. A token that is an element-style
     * prefix but names no element is fatal here, where C would go on to the other two tables;
     * no object or kind flag in C begins with a prefix of {@code HATES} or {@code IGNORE} and an
     * underscore, so the two agree on every flag that exists.
     *
     * <p><b>Numbers.</b> {@link ParseUtils#parseIntString} reads the break chance and maximum
     * stack as {@code parser_getint} does. Empty text gives 0, which is what C stores when a
     * file supplies only one of the two {@code default:} values, and a negative value is stored
     * as given. Accepted deviation ("An integer too big for its C type is clamped, where C wraps
     * it" in {@code docs/Accepted_Deviations.md}): a value outside the range of an {@code int} is
     * clamped to it, where C keeps the low 32 bits.
     *
     * <p><b>The sentinel.</b> C's {@code finish_parse_object_base} copies each record into
     * {@code kb_info}, one slot per tval, so a tval the file does not mention is left as a
     * zeroed slot. The appended {@code TV_NONE} base is that zeroed slot for tval 0: no name,
     * {@link ColourEnum#COLOUR_DARK}, no flags, no break chance and no stack limit. The list
     * itself is in file order. C indexes by tval, so order does not matter to it, and when two
     * records name the same tval it keeps the earlier one, which a first-match search of this list
     * finds too.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw object-base records from the grammar, in file order, with the
     *                 {@code default:} values already folded in
     * @param errors   unused: every fault ends the load, so nothing is appended
     * @param filename the file the records came from, named in the message of each failure
     * @return the assembled object bases in file order, followed by the {@code TV_NONE} sentinel
     * @throws DataFileParseException if a tval, flag, break chance or maximum stack cannot be
     *                                resolved
     */
    @Override
    public List<ObjectBase> assemble(@NotNull List<ObjectBaseParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<ObjectBase> results = new ArrayList<>();

        for (ObjectBaseParseRecord record : records) {
            int line = record.line();
            
            String name = record.name().isEmpty() ? null : record.name();
            String rawTVal = record.tVal();
            TValue tVal;
            tVal = TValue.fromName(rawTVal);
            if (tVal == null) {
                String msg = "Object base starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal tValue value: " + rawTVal;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            String rawColour = record.colour();
            ColourEnum colour;
            colour = ColourEnum.fromCode(rawColour);
            if (colour == null) {
                colour = ColourEnum.COLOUR_WHITE;
            }
            Flag<ObjectKindFlag> kindFlag = new Flag<>(ObjectKindFlag.class);
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            Flag<ElementEnum> hatesFlag = new Flag<>(ElementEnum.class);
            Flag<ElementEnum> ignoreFlag = new Flag<>(ElementEnum.class);
            boolean uScoreFound;
            for (String flag : record.flags()) {
                uScoreFound = flag.contains("_");
                String tag = "";
                if (uScoreFound) {
                    tag = flag.substring(0, flag.indexOf("_"));
                }
                if (uScoreFound && "HATES".startsWith(tag)) {
                    String rawFlag = flag.substring(tag.length() + 1);
                    try {
                        ElementEnum ee = ElementEnum.valueOf("ELEM_" + rawFlag);
                        if (ee == ElementEnum.ELEM_NONE || ee == ElementEnum.ELEM_MAX) {
                            String msg = "Object base starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel element flag value: " + flag;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);    
                        }
                        hatesFlag.on(ee);
                    } catch (IllegalArgumentException e) {
                        String msg = "Object base starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal hates element flag value: " + flag;
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                } else if (uScoreFound && "IGNORE".startsWith(tag)) {
                    String rawFlag = flag.substring(tag.length() + 1);
                    try {
                        ElementEnum ee = ElementEnum.valueOf("ELEM_" + rawFlag);
                        if (ee == ElementEnum.ELEM_NONE || ee == ElementEnum.ELEM_MAX) {
                            String msg = "Object base starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel element flag value: " + flag;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                        ignoreFlag.on(ee);
                    } catch (IllegalArgumentException e) {
                        String msg = "Object base starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal ignore element flag value: " + flag;
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                } else {
                    if (flag.equals("NONE")) {
                        String msg = "Object base starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinal object/object kind flag value: " + flag;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);                       
                    } else if (flag.equals("MAX")) {
                        kindFlag.on(ObjectKindFlag.KF_MAX);
                        oFlags.on(ObjectFlag.OF_MAX);
                    } else {
                        try {
                            ObjectKindFlag kf = ObjectKindFlag.valueOf("KF_" + flag);
                            kindFlag.on(kf);
                        } catch (IllegalArgumentException e) {
                            try {
                                ObjectFlag of = ObjectFlag.valueOf("OF_" + flag);
                                oFlags.on(of);
                            } catch (IllegalArgumentException ex) {
                                String msg = "Object base starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal flag value: " + flag;
                                logger.fatal(msg, ex);
                                throw new DataFileParseException(msg);
                            }
                        }
                    }
                }
            }
            int breakChance = ParseUtils.parseIntString("Object base", record.breakChance(), line, filename, "break chance");
            int maxStack = ParseUtils.parseIntString("Object base", record.maxStack(), line, filename, "max stack");

            results.add(new ObjectBase(tVal, name, colour, kindFlag, hatesFlag, ignoreFlag, breakChance, maxStack, oFlags));
        }

        results.add(new ObjectBase(TValue.TV_NONE, null, ColourEnum.COLOUR_DARK,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), new Flag<>(ElementEnum.class),
                0, 0, new Flag<>(ObjectFlag.class)));

        return results;
    }
}
