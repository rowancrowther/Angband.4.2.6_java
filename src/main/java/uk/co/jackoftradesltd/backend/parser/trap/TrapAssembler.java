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

package uk.co.jackoftradesltd.backend.parser.trap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link TrapParseRecord}s from {@code TrapGrammar} into domain {@link TrapKind}s —
 * C's {@code struct trap_kind}, the trap template loaded from {@code trap.txt} (Java port of the
 * {@code parse_trap_*} handlers in {@code src/init.c}).
 *
 * <p>This is where interpretation happens:
 * <ul>
 *   <li>the {@code name:}/{@code desc:} crossover is preserved — the {@code name:} line's second
 *       field becomes {@link TrapKind}'s {@code description} (C {@code desc}, the
 *       {@code lookupTrap} key) while the {@code desc:} directive becomes its {@code text}
 *       (C {@code text});</li>
 *   <li>{@code graphics:} resolves its colour through {@link ColourEnum#fromCode(String)} into an
 *       {@link AngbandDisplayCharacter}. As in C's {@code parse_trap_graphics()}, a one-character
 *       colour is read as an index character ({@code color_char_to_attr()}) and a longer one as a
 *       name ({@code color_text_to_attr()}); both default an unknown colour to white, so neither
 *       language fails on a colour. A glyph outside the Basic Multilingual Plane is kept whole as a
 *       code point, as C's {@code parser_getchar} keeps a {@code wchar_t};</li>
 *   <li>the {@code appear:} triple becomes {@code rarity}/{@code minDepth}/{@code maxNum}. C reads
 *       each as a {@code uint}, refusing a leading {@code -} with {@code PARSE_ERROR_NOT_NUMBER} and
 *       otherwise reading it with {@code strtoul} in base 0, so the port refuses a minus with
 *       {@link ParseUtils#minusFound(String)} and reads the rest through
 *       {@link ParseUtils#parseIntString(String, String, int, String, String)}, which takes the same
 *       hexadecimal and octal prefixes;</li>
 *   <li>{@code visibility:} becomes the {@code power} {@link Random} through
 *       {@link Random#parseStr(String)}, the port of C's {@code dice_parse_string()} followed by
 *       {@code dice_random_value()}; text it cannot read ends the load, as C's
 *       {@code PARSE_ERROR_NOT_RANDOM} does;</li>
 *   <li>a directive the record leaves out takes C's zeroed value, because C's {@code mem_zalloc}
 *       leaves the fields at zero: {@code power} is {@link Random#Zero()} (five shipped kinds,
 *       {@code no trap}, {@code glyph of warding}, {@code decoy}, {@code door lock} and
 *       {@code web}, have no {@code visibility:} line), the display character is
 *       {@link AngbandDisplayCharacter#blank}, and the {@code appear:} numbers are 0 (the
 *       grammar leaves an absent field as empty text, which {@code parseIntString} reads as 0;
 *       {@code no trap} has no {@code appear:} line). The messages are the exception: an absent
 *       {@code msg:}, {@code msg-good:}, {@code msg-bad:} or {@code msg-xtra:} is empty text here,
 *       where C holds {@code NULL} and tests the pointer before printing;</li>
 *   <li>{@code flags:} resolve to {@link TrapEnum} ({@code TRF_}) and {@code save:} to
 *       {@link ObjectFlag} ({@code OF_}) — two distinct flag vocabularies. Both are matched
 *       case-exactly, as C's {@code lookup_flag()} matches with {@code streq}. Both sentinels are
 *       refused: C's {@code trap_flags} and {@code list_obj_flag_names} tables hold
 *       {@code "NONE"} at index 0, which {@code lookup_flag()} never reaches because it starts at
 *       {@code FLAG_START} (1), and neither table holds {@code MAX};</li>
 *   <li>primary and extra effects are delegated to {@link EffectAssembler}; and
 *       {@code trapKindIndex} is assigned by file position from 0, matching the {@code tidx} that
 *       {@code finish_parse_trap()} gives each kind as it walks its reversed list down from
 *       {@code trap_max - 1}. Index 0 is the first record, {@code no trap}, which
 *       {@code lookup_trap()} skips.</li>
 * </ul>
 *
 * <p><b>Error policy:</b> every bad value is fatal, as it is in C, which stops the load at the
 * first handler that returns an error. A negative {@code appear:} number, unreadable
 * {@code visibility:} dice, an unknown or sentinel flag or save flag, and a bad effect block (thrown
 * from inside {@link EffectAssembler}) each log a fatal message and throw a
 * {@link DataFileParseException}, so no record is dropped and no index is skipped. The
 * {@code errors} list is no longer written here; it is only passed on to {@link EffectAssembler},
 * which does not write it either. Hard grammar/lexer errors are handled upstream by
 * {@code GrammarDriver}.
 *
 * <p>Class TrapAssembler coded before 260915, commented in full on 261030 (first commented 260915,
 * updated 261020 for the zeroed defaults; rewritten 261030 once soft errors became fatal and the
 * sentinels were refused).
 *
 * @author Rowan Crowther
 */
public class TrapAssembler implements Assembler<TrapParseRecord, List<TrapKind>> {
    /**
     * Receives the fatal message logged before each {@link DataFileParseException} this class
     * throws.
     *
     * <p>Field logger coded before 260915, commented in full on 261030.
     */
    private static final Logger logger = LogManager.getLogger(TrapAssembler.class);

    /**
     * Assembles every parsed trap record into a {@link TrapKind}, in file order — the Java form of
     * the {@code parse_trap_*} handlers in {@code init.c} filling each {@code struct trap_kind},
     * followed by {@code finish_parse_trap()} numbering them. The checks run in this order for each
     * record: graphics, the three {@code appear:} numbers, {@code visibility:}, flags, save flags,
     * then the primary and extra effects. The first failure throws, so a file loads whole or not
     * at all, as in C.
     *
     * <p>A kind never leaves here with a {@code null} power, display character or effect list: an
     * absent {@code visibility:} line gives {@link Random#Zero()}, an absent {@code graphics:} line
     * gives {@link AngbandDisplayCharacter#blank}, and absent effects give an empty list.
     * {@code blank} is a space in {@code COLOUR_DARK}: the colour is C's zeroed {@code d_attr}, but
     * the character is a space where C's zeroed {@code d_char} is {@code '\0'}. No shipped record
     * is without {@code graphics:}, so the difference shows only in a modded file.
     *
     * <p>Function assemble coded before 260915, commented in full on 261030 (first commented
     * 260915, updated 261020 for the zeroed defaults; rewritten 261030 for the fatal errors).
     *
     * @param records  the raw trap records captured by the grammar
     * @param errors   unused here; formerly the soft-error channel, now only passed on to
     *                 {@link EffectAssembler}
     * @param filename the data file the records came from, for error messages
     * @return the assembled trap kinds, in source order, indexed from 0
     * @throws DataFileParseException if any appear number is negative or not a number, the
     *                                visibility dice cannot be read, a flag or save flag is unknown
     *                                or a sentinel, or an effect block fails to resolve
     */
    @Override
    public List<TrapKind> assemble(@NotNull List<TrapParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<TrapKind> traps = new ArrayList<>();
        int index = -1;

        for (TrapParseRecord record : records) {
            int line = record.line();
            String trapKindName = record.name();
            String trapText = record.text();
            String trapDescription = record.description();
            String trapMessage = record.message();
            String saveMessage = record.saveMessage();
            String failMessage = record.failMessage();
            String extraMessage = record.xtraMessage();
            char glyphChar;
            AngbandDisplayCharacter adc = AngbandDisplayCharacter.blank;
            String glyph = record.glyph();
            int wideGlyph = 0;
            String colour = record.colour();
            // Check that they both exist
            if (!glyph.isEmpty() && !colour.isEmpty()) {
                ColourEnum colourType = ColourEnum.fromCode(colour);
                if (colourType == null) {
                    colourType = ColourEnum.COLOUR_WHITE;
                }
                glyphChar = glyph.charAt(0);
                wideGlyph = Character.codePointAt(glyph, 0);
                if (Character.isSupplementaryCodePoint(wideGlyph))
                    adc = new AngbandDisplayCharacter(wideGlyph, colourType);
                else
                    adc = new AngbandDisplayCharacter(glyphChar, colourType);
            }
            if (ParseUtils.minusFound(record.rarity())) {
                String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative rarity value: " + record.rarity();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int rarity = ParseUtils.parseIntString("Trap", record.rarity(), line, filename, "rarity");
            if (ParseUtils.minusFound(record.minDepth())) {
                String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative minimum depth value: " + record.minDepth();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int minDepth = ParseUtils.parseIntString("Trap", record.minDepth(), line, filename, "minimum depth");
            if (ParseUtils.minusFound(record.maxNum())) {
                String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative maximum value: " + record.maxNum();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int maxNum = ParseUtils.parseIntString("Trap", record.maxNum(), line, filename, "maximum");
            Random power = Random.Zero();
            String powerStr = record.power();
            if (!powerStr.isEmpty()) {
                power = Random.parseStr(powerStr);
                if (power == null) {
                    String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal power value: " + record.power();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            Flag<TrapEnum> flags = new Flag<>(TrapEnum.class);
            for (String flag : record.flags()) {
                try {
                    TrapEnum trapEnum = TrapEnum.valueOf("TRF_" + flag);
                    if (trapEnum == TrapEnum.TRF_NONE || trapEnum == TrapEnum.TRF_MAX) {
                        String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel value: " + flag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    flags.on(TrapEnum.valueOf("TRF_" + flag));
                } catch (IllegalArgumentException e) {
                    String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal trap value: " + flag;
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            }
            Flag<ObjectFlag> saveFlags = new Flag<>(ObjectFlag.class);
            for (String flag : record.saveFlags()) {
                try {
                    ObjectFlag objFlag = ObjectFlag.valueOf("OF_" + flag);
                    if (objFlag == ObjectFlag.OF_MAX || objFlag == ObjectFlag.OF_NONE) {
                        String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel trap save value: " + flag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    saveFlags.on(objFlag);
                } catch (IllegalArgumentException e) {
                    String message = "Trap starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal trap save value: " + flag;
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            }
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            List<Effect> xtraEffects = EffectAssembler.assemble(record.xtraEffects(), errors, filename);
            index++;

            traps.add(new TrapKind(trapKindName, trapText, trapDescription,
                    trapMessage, saveMessage, failMessage, extraMessage,
                    index, adc, rarity, minDepth, maxNum, power, flags,
                    saveFlags, effects, xtraEffects));
        }
        
        return traps;
    }
}
