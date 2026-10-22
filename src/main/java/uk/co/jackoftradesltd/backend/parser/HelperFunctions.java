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

package uk.co.jackoftradesltd.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;

import java.math.BigInteger;

/**
 * Shared number reading for the assemblers, so far used only by {@link
 * uk.co.jackoftradesltd.backend.parser.activation.ActivationAssembler}. The grammar hands over an
 * integer as text, and these functions turn it into an {@code int} the way {@code parser.c} does
 * for the {@code int} and {@code uint} fields of a data-file line: {@code parser_getint} reads
 * with {@code strtol} and {@code parser_getuint} with {@code strtoul}, both in base 0, so a
 * leading {@code 0} means octal and anything else decimal.
 *
 * <p>Accepted deviation (2026-10-22, "An {@code aim:}, {@code level:} or {@code power:} too big
 * for its C type is clamped, where C wraps it" in {@code docs/Accepted_Deviations.md}): C stores
 * the 64-bit result in a 32-bit field with no range check and keeps the low 32 bits, so
 * {@code level:3000000000} becomes -1294967296. Here the value is clamped to
 * {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}, so it becomes 2147483647.
 *
 * <p>Not read, because the {@code INTEGER} token in {@code EffectBlockLexer.g4} is only an
 * optional {@code -} and decimal digits, so the text never holds them: a leading {@code +},
 * whitespace, and the {@code 0x} hexadecimal prefix that base 0 allows ({@code 0x10} would give 0
 * here and 16 in C). Text with trailing characters after the digits is read up to them, as
 * {@code strtol} does.
 *
 * <p>Class HelperFunctions coded on 261022, commented in full on 261022.
 */
public class HelperFunctions {
    /** Receives the fatal message before each {@link DataFileParseException} is thrown. */
    private static final Logger logger = LogManager.getLogger(HelperFunctions.class);

    /**
     * Read the text of an {@code aim:}, {@code level:} or {@code power:} line as C's {@code strtol}
     * with base 0 reads it: strip one leading {@code -}, choose octal if the digits start with
     * {@code 0} and decimal otherwise, and hand over to the matching {@code parseString}, which
     * applies the sign before it clamps. Empty text, which is an absent directive, gives 0, and so
     * does a lone {@code -}, which the lexer never produces.
     *
     * <p>It does not refuse a negative number: {@code ActivationAssembler.assemble} rejects a
     * leading {@code -} on {@code aim:} and {@code power:} first (C's {@code strtoul} path refuses
     * it as {@code PARSE_ERROR_NOT_NUMBER}), so only {@code level:} arrives here negative.
     *
     * <p>Function parseIntString coded on 261022, commented in full on 261022.
     *
     * @param string   the raw text from the parse record
     * @param line     the line the record starts on, for the message
     * @param filename the file being read, for the message
     * @param token    the directive name, for the message
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if the text has no leading digits
     */
    public static int parseIntString(String string, int line, String filename, String token) {
        if (string.isEmpty())
            return 0;

        boolean foundMinus = false;
        if (string.charAt(0) == '-') {
            foundMinus = true;
            string = string.substring(1);
        }

        if (string.isEmpty())
            return 0;

        if (string.charAt(0) == '0')
            return parseString(string, line, filename, token, foundMinus, 8);
        else
            return parseString(string, line, filename, token, foundMinus);
    }
    
    /**
     * Read a number in {@code radix} from the leading digits of {@code string}. Reading stops at
     * the first character that is not a digit of that base, as {@code strtol} stops, so in octal
     * {@code 08} is 0, {@code 019} is 1 and {@code 017} is 15, and in decimal {@code 12abc} is 12.
     * The sign is applied first and the result is then clamped to the range of an {@code int}, the
     * accepted deviation from C's wrap to 32 bits (see the class block).
     *
     * <p>The accepted digits run from {@code 0} to {@code radix - 1}, so the radix must be between
     * 3 and 10: anything else is a programming error and throws a {@link DataFileParseException}
     * before any text is read. {@code parseIntString} passes 8 for text starting with {@code 0}
     * and, through the five-argument overload, 10 for everything else.
     *
     * <p>If no digit is read, which for octal cannot happen because the text starts with
     * {@code 0}, the digit string is empty and the {@link NumberFormatException} becomes a
     * {@link DataFileParseException}, as C's {@code PARSE_ERROR_NOT_NUMBER} stops the load. The
     * message names an activation, because the activation assembler is the only caller.
     *
     * <p>Function parseString coded on 261022, commented in full on 261022.
     *
     * @param string     the text with any leading {@code -} already removed
     * @param line       the line the record starts on, for the message
     * @param filename   the file being read, for the message
     * @param token      the directive name, for the message
     * @param foundMinus whether a leading {@code -} was removed
     * @param radix      the base to build the number in, 3 to 10
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if {@code radix} is out of range or no digit could be read
     */
    private static int parseString(String string, int line, String filename, String token, boolean foundMinus, int radix) {
        StringBuilder newString = new StringBuilder();
        
        if (radix > 10 || radix <= 2) {
            String message = "Invalid radix passed to parseString while parsing file: " + filename 
                    + " radix: " + radix;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        
        char radixChar = (char) ((radix - 1) + '0');

        for (char c : string.toCharArray()) {
            if (c <= radixChar && c >= '0')
                newString.append(c);
            else
                break;
        }

        String values = newString.toString();

        try {
            BigInteger bigValue = new BigInteger(values, radix);
            bigValue = foundMinus ? bigValue.negate() : bigValue;
            bigValue = bigValue.min(BigInteger.valueOf(Integer.MAX_VALUE));
            bigValue = bigValue.max(BigInteger.valueOf(Integer.MIN_VALUE));
            return bigValue.intValue();
        } catch (NumberFormatException e) {
            String message = "Activation beginning at line: " + line + " of file: " + filename
                    + " has an invalid " + token + " value: " + string;
            logger.fatal(message, e);
            throw new DataFileParseException(message);
        } catch (Exception e) {
            String message = "Activation beginning at line: " + line + " of file: " + filename
                    + " has an unknown error with token " + token + " value: " + string;
            logger.fatal(message, e);
            throw new DataFileParseException(message);
        }
    }

    /**
     * Read a decimal number: {@link #parseString(String, int, String, String, boolean, int)} with
     * radix 10. Used for text that does not start with {@code 0}.
     *
     * <p>Function parseString (decimal) coded on 261022, commented in full on 261022.
     *
     * @param string     the text with any leading {@code -} already removed
     * @param line       the line the record starts on, for the message
     * @param filename   the file being read, for the message
     * @param token      the directive name, for the message
     * @param foundMinus whether a leading {@code -} was removed
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if no digit could be read
     */
    private static int parseString(String string, int line, String filename, String token, boolean foundMinus) {
        return parseString(string, line, filename, token, foundMinus, 10);
    }
}
