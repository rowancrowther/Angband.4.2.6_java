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

package uk.co.jackoftradesltd.channel.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

/**
 * Shared number and text reading for the assemblers: every assembler whose data file holds a
 * number goes through it, from {@link uk.co.jackoftradesltd.backend.parser.activation.ActivationAssembler}
 * to {@link uk.co.jackoftradesltd.backend.parser.dungeonprofile.TunnelAssembler}. The grammar
 * hands over an integer as text, and these functions turn it into an {@code int} the way
 * {@code parser.c}, function {@code parser_parse()}, does for the {@code int} and {@code uint}
 * fields of a data-file line: it reads an {@code int} with {@code strtol} and a {@code uint} with
 * {@code strtoul}, both in base 0 ({@code parser_getint} and {@code parser_getuint} only hand the
 * stored result back), so a leading {@code 0} means octal and anything else decimal.
 *
 * <p>There are four ways in. The five-argument {@code parseIntString} is the base 0 reading
 * above. The six-argument one ending in {@code boolean octal} takes the base from its caller
 * instead of from the text, for the figures C reads in a fixed base: the two numbers of an
 * {@code alloc:} range are read by {@code grab_int_range} in {@code datafile.c} with
 * {@code strtol} in base 10, so {@code alloc:1:010 to 020} is ten to twenty there, where base 0
 * would make it eight to sixteen. It skips leading whitespace as {@code strtol} does, and text of
 * nothing but whitespace ends the load. The six-argument one ending in {@code int def} is the
 * base 0 reading with a caller-chosen value for empty text. The six-argument one ending in
 * {@link Radix} reads the digits after a prefix the caller has already looked at, which suits
 * the base 10 reading of {@link Radix#DECIMAL} and the two prefixed bases alike. All four end in
 * the same digit-by-digit reader.
 *
 * <p>A lone {@code -} or {@code +} has no digit, so every form refuses it as C does
 * ({@code PARSE_ERROR_NOT_NUMBER}); only empty text gives 0. One further difference from C, not
 * reachable from a data file: {@code grab_int_range} rejects a number at {@code INT_MIN} or
 * {@code INT_MAX} outright where this clamps it, which the caller's 0 to 255 range check then
 * refuses all the same.
 *
 * <p>Accepted deviation (2026-10-22 for the activation fields, extended 2026-10-23 to every data
 * file; "An integer too big for its C type is clamped, where C wraps it" in
 * {@code docs/Accepted_Deviations.md}): C stores the 64-bit result in a 32-bit field with no
 * range check and keeps the low 32 bits, so {@code level:3000000000} becomes -1294967296 and
 * {@code aim:4294967296} becomes 0. Here the value is clamped to {@link Integer#MIN_VALUE} and
 * {@link Integer#MAX_VALUE}, so both become 2147483647.
 *
 * <p>Base 0 reads what {@code strtol} reads: the whitespace {@code isspace} skips (space, tab,
 * newline, vertical tab, form feed and carriage return) before the sign, one leading {@code -} or
 * {@code +}, then {@code 0x} or {@code 0X} for hexadecimal, a leading {@code 0} for octal and
 * anything else decimal ({@code 0x10} is 16, {@code +010} is 8, {@code +5} is 5). Text with
 * trailing characters after the digits is read up to them, as {@code strtol} does. A {@code 0} or
 * {@code 0x} prefix with no digit of its base after it is itself the digit that {@code strtol}
 * reads, so {@code 0}, {@code 08}, {@code 0x} and {@code 0xg} are all 0; text with no digit at all
 * ({@code abc}, {@code +-5}) ends the load as {@code PARSE_ERROR_NOT_NUMBER} does. Most of the
 * assemblers' grammars never produce whitespace, a {@code +} or a {@code 0x} prefix, but the
 * reader does not rely on that.
 *
 * <p>The number readers never refuse a sign. For the {@code uint} fields, where C does, the
 * caller asks {@link #minusFound(String)} about the raw text and ends the load itself.
 *
 * <p>The remaining public functions are small tests on the raw text, with no single C function
 * behind them: {@link #signFound(String)} reports the leading sign, {@link #signOnly(String)}
 * whether the text is nothing but a sign, {@link #onlyDigits(String)} whether it is nothing but
 * digits, and {@link #nonDigitFound(String, Radix)} whether anything after an optional sign is not a
 * digit. {@code nonDigitFound} stands in for the {@code *tc != ']'} test in {@code datafile.c},
 * function {@code find_value_arg()}, which the bracketed figures of the ego-item {@code values:}
 * and {@code min-values:} lines and of the object-kind {@code values:} line go through, so
 * trailing text in a bracketed figure ends the load where {@link #parseIntString} on its own
 * would read up to it.
 *
 * <p>The text helpers at the foot of the class work on strings and not on numbers.
 * {@link #onlySpaceAndTab(String)} is the port of {@code contains_only_spaces} in {@code
 * z-util.c}, which counts only space and tab. {@link #onlyCSpace(String)} and the {@code cTrim}
 * family ({@link #cTrim(String)}, {@link #cTrimFront(String)}, {@link #cTrimRear(String)}) use
 * the wider set that C's {@code isspace} accepts in the C locale (space, tab, newline, vertical
 * tab, form feed, carriage return), which is what {@code parser_parse()} in {@code parser.c}
 * skips in front of a line and {@code strtol} skips in front of a number. The {@code
 * trimOnlySpaceAndTab} family does the same job for the narrower space and tab set. None of the
 * trimmers has a single C function behind it: C trims by moving a pointer, and these return the
 * shorter string.
 *
 * <p>{@link #grabElementFlag(Map, String)} is the one function here that reads a flag word and not
 * a number or a trim: it ports {@code grab_element_flag} in {@code obj-init.c}, which turns an
 * {@code IGNORE_<element>} or {@code HATES_<element>} word into a flag on that element.
 *
 * <p>{@link #parseADCGlyph(String)} and the {@link GlyphCharacter} it returns are the other
 * function here that is neither a number nor a trim. C reads a {@code PARSE_T_CHAR} field in
 * {@code parser_parse()} ({@code parser.c}) by converting the first character of the token to a
 * {@code wchar_t} with {@code text_mbstowcs} (and refuses a token with more than one character),
 * and the {@code graphics:} callbacks in {@code obj-init.c} take it with {@code parser_getchar}.
 * Java has no {@code wchar_t}, so the one code point of the glyph text comes back as a
 * {@code char} when it fits in one and as a wide code point when it needs two, and text of any
 * other length comes back as {@code null}. Its one caller in {@code src/main} is
 * {@code ArtifactAssembler}.
 *
 * <p>{@link #isTrue(String)} is the flag reader: it reads a figure with {@code strtoul} base 0 rules
 * and answers whether it is non-zero, as {@code parse_act_aim} in {@code obj-init.c} does for an
 * {@code aim:} line, with {@code Optional.empty()} standing for C's {@code PARSE_ERROR_NOT_NUMBER}.
 *
 * <p>Class ParseUtils coded on 261022, commented in full on 261107 (third way in added 261023,
 * sign and digit tests added 261024, fourth way in and the {@code 0x} reading added 261025,
 * {@code onlySpaceAndTab} added by 261026; the whitespace skipping in the fixed-base reading and
 * the trimmers were in place before 261028; {@code grabElementFlag} noted 261105; reader wording
 * and the caller lists corrected and the glyph reader described 261107; {@code isTrue}
 * described 261107).
 */
public class ParseUtils {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown, so a bad
     * data-file figure is in the log as well as in the exception. Shared by every reader in the
     * class; the text helpers and the glyph reader never log.
     *
     * <p>Field logger coded before 261105, commented in full on 261107.
     */
    private static final Logger logger = LogManager.getLogger(ParseUtils.class);

    /**
     * Read the text of an {@code aim:}, {@code level:} or {@code power:} line as C's {@code strtol}
     * with base 0 reads it: skip leading whitespace, strip one leading {@code -} or {@code +}
     * ({@link #signFound(String)}), choose hexadecimal if the digits start with {@code 0x} or
     * {@code 0X}, octal if they start with {@code 0} and decimal otherwise, and hand over to
     * {@code parseString}, which applies the sign before it clamps. Empty text, which is an absent
     * directive, gives 0. Text that is only whitespace, or only a sign, has no digits and ends the
     * load, as C's {@code z == tok} does.
     *
     * <p>A consumed prefix is itself a digit to {@code strtol}: {@code 0}, {@code 08}, {@code 0x}
     * and {@code 0xg} all read the {@code 0} and give 0, so the empty digit string after the
     * prefix returns 0 here, while text that has no digit and no prefix ({@code abc}, {@code +-5},
     * {@code x10}) throws, as {@code z == tok} gives {@code PARSE_ERROR_NOT_NUMBER}. This is why
     * the decision is made here at the prefix and not in {@code parseString}, which cannot tell the
     * two apart.
     *
     * <p>It does not refuse a negative number: {@code ActivationAssembler.assemble} rejects a
     * leading {@code -} on {@code aim:} and {@code power:} first (C's {@code strtoul} path refuses
     * it as {@code PARSE_ERROR_NOT_NUMBER}), so only {@code level:} arrives here negative.
     *
     * <p>Function parseIntString coded on 261022, commented in full on 261028 (leading
     * {@code +} read 261024, prefix and whitespace rewritten 261025, lone sign made fatal
     * 261028).
     *
     * @param type     the kind of record being read, for the message ({@code "Activation"},
     *                 {@code "Artifact"})
     * @param string   the raw text from the parse record
     * @param line     the line the record starts on, for the message
     * @param filename the file being read, for the message
     * @param token    the directive name, for the message
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if the text has no leading digits, or is only a sign
     */
    public static int parseIntString(String type, String string, int line, String filename, String token) {
        String holder = string;
        
        if (string.isEmpty())
            return 0;
        
        while (!string.isEmpty() && 
                (string.charAt(0) == ' ' || string.charAt(0) == '\t'
                        || string.charAt(0) == '\r' || string.charAt(0) == '\013'
                || string.charAt(0) == '\n' || string.charAt(0) == '\f')) {
            string = string.substring(1);
        }
        
        if (string.isEmpty()) {
            String message = type + " beginning at line: " + line + " of file: " + filename
                    + " has an invalid " + token + " value: " + holder;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        int sign = signFound(string);
        
        if (sign != 0) {
            string = string.substring(1);
            if (string.isEmpty()) {
                String message = type + " beginning at line: " + line + " of file: " + filename
                        + " the " + token + " value consists solely of a sign: " + holder;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
        }

        Radix radix = Radix.DECIMAL;
        
        if (string.startsWith("0x") || string.startsWith("0X")) {
            radix = Radix.HEX;
            string = string.substring(2);
            String stripped = stripStart(string, radix);
            if (stripped.isEmpty()) return 0;
        } else if (string.startsWith("0")) {
            radix = Radix.OCTAL;
            string = string.substring(1);
            String stripped = stripStart(string, radix);
            if (stripped.isEmpty()) return 0;
        } else {
            String stripped = stripStart(string, radix);
            if (stripped.isEmpty()) {
                String message = type + " beginning at line: " + line + " of file: " + filename
                        + " has an invalid " + token + " value: " + holder;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
        }
        
        return parseString(type, string, line, filename, token, sign < 0, radix);
    }
    
    /**
     * Read the text of an {@code int} figure in a base the caller chooses, where the five-argument
     * form chooses from the text. With {@code octal} false the digits are read in base 10 whatever
     * they start with, as C's {@code strtol(range, &pe, 10)} reads the two numbers of an
     * {@code alloc:} range in {@code grab_int_range}: {@code 010} is ten and {@code 08} is eight.
     * With {@code octal} true they are read in base 8 whatever they start with, which C never does
     * and nothing calls for yet.
     *
     * <p>The sign (a leading {@code -} or {@code +}), the empty text, the lone sign, the digit limit
     * and the clamp are as in the five-argument form, and so is the leading whitespace, skipped with
     * {@link #cTrimFront(String)}. Text of nothing but whitespace stops the load with an "empty"
     * message, where {@code strtol} reads no digits ({@code pe == range} in
     * {@code grab_int_range}). The caller still has to check the range: C refuses a figure outside
     * 0 to 255 for an ego-item or artifact {@code alloc:} ({@code PARSE_ERROR_OUT_OF_BOUNDS} in
     * {@code obj-init.c}), where an object kind's {@code alloc:} has no such check, and this does
     * not. {@code EgoItemAssembler} reads its {@code alloc:} range through this form with
     * {@code octal} false, and so does {@code ArtifactAssembler} for the artifact's, each followed
     * by its own 0 to 255 test. {@code EgoItemAssembler} also uses it for the figures C reads in
     * {@code find_value_arg()} ({@code strtol} in base 10), after
     * {@link #nonDigitFound(String, Radix)} has refused trailing text.
     *
     * <p>No prefix is stripped: a fixed base 10 reading of {@code 0x10} gives 0 (the {@code x}
     * stops it), as {@code strtol(..., 10)} does, and a fixed base 8 reading of {@code 08} gives 0.
     * Text with no digit of the base ({@code abc}, {@code 8} in octal) throws.
     *
     * <p>Function parseIntString (fixed base) coded on 261022, commented in full on 261107
     * (whitespace skipping noted 261028; range note and callers corrected 261107).
     *
     * @param type     the kind of record being read, for the message ({@code "Activation"},
     *                 {@code "Artifact"})
     * @param string   the raw text from the parse record
     * @param line     the line the record starts on, for the message
     * @param filename the file being read, for the message
     * @param token    the directive name, for the message
     * @param octal    {@code true} to read in base 8, {@code false} to read in base 10
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if the text has no leading digits in that base
     */
    public static int parseIntString(String type, String string, int line, String filename, String token, boolean octal) {
        String holder = string;
        if (string.isEmpty())
            return 0;
        
        string = cTrimFront(string);
        if (onlyCSpace(string)) {
            String message = type + " beginning at line: " + line + " of file: " + filename
                    + " has an empty " + token + " value";
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        int sign = signFound(string);
        if (sign != 0) {
            string = string.substring(1);
        }

        if (string.isEmpty()) {
            String message = type + " beginning at line: " + line + " of file: " + filename
                    + " the " + token + " value consists solely of a sign: " + holder;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
       
        String stripped;
        if (octal)
            stripped = stripStart(string, Radix.OCTAL);
        else
            stripped = stripStart(string, Radix.DECIMAL);
        
        if (stripped.isEmpty()) {
            String message = type + " beginning at line: " + line + " of file: " + filename
                    + " has an invalid " + token + " value: " + holder;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        if (octal)
            return parseString(type, stripped, line, filename, token, sign < 0, Radix.OCTAL);
        else
            return parseString(type, stripped, line, filename, token, sign < 0, Radix.DECIMAL);
    }

    /**
     * Read the digits of an {@code int} figure after a prefix the caller has already looked at,
     * which is the base 0 reading of {@code strtol} split in two: the caller chooses the
     * {@link Radix} from the prefix it sees and this strips the prefix and reads the digits. It
     * was written for {@code FlavourAssembler}, whose {@code index} is a {@code uint} that
     * {@code parser_parse()} in {@code parser.c} reads with {@code strtoul} in base 0, but that
     * assembler now uses the five-argument form. Every caller in {@code src/main} passes
     * {@link Radix#DECIMAL}: {@code ItemObjectAssembler} for the two figures of an object kind's
     * {@code alloc:} range, the base 10 reading of {@code grab_int_range} in {@code datafile.c},
     * and for a resistance figure, {@code ShapeAssembler} for a resist element and an object
     * modifier, {@code ProjectionAssembler} for {@code denominator:}, {@code PlayerPropertyAssembler}
     * for {@code bindui} and {@code UIEntryAssembler} for {@code priority:}. No caller chooses
     * {@link Radix#HEX} or {@link Radix#OCTAL}, so those two arms are reached only from tests.
     *
     * <p>One leading {@code -} or {@code +} is stripped here ({@link #signFound(String)}) before
     * the prefix is looked for, so the prefix test is on the unsigned text; a caller that chooses
     * the radix from the raw text has to look past the sign as well. Empty text gives 0 and a lone
     * sign ends the load, as in the five-argument form.
     *
     * <p>By {@code radix}:
     * <ul>
     *   <li>{@link Radix#HEX}: the text must start with {@code 0x} or {@code 0X}, which is
     *       stripped, then hexadecimal digits are read. {@code 0x} and {@code 0xg} give 0, since
     *       the {@code 0} is the digit {@code strtol} reads.</li>
     *   <li>{@link Radix#OCTAL}: the text must start with {@code 0}, which is stripped, then octal
     *       digits are read. {@code 0} and {@code 08} give 0.</li>
     *   <li>{@link Radix#DECIMAL}: nothing is stripped and decimal digits are read. Text with no
     *       digit ({@code abc}, {@code x10}, and {@code +-5} once the first sign is stripped) throws, as
     *       {@code PARSE_ERROR_NOT_NUMBER} stops the load.</li>
     *   <li>{@link Radix#BINARY}: not a base C reads and nothing passes it. It takes the decimal
     *       checks and then reads binary digits.</li>
     * </ul>
     *
     * <p>HEX text with no {@code 0x} and OCTAL text with no leading {@code 0} throw. A literal
     * {@code strtol} in base 16 or 8 would read {@code 10} as 16 or 8, but base 0 would not, and the
     * caller chooses HEX or OCTAL only after seeing the prefix, so the text without one is a
     * programming error and not data.
     *
     * <p>Function parseIntString (with radix) coded before 261025, commented in full on 261107
     * (callers corrected 261107).
     *
     * @param type     the kind of record being read, for the message ({@code "Flavour"})
     * @param string   the raw text from the parse record, with any leading whitespace already
     *                 removed by the caller
     * @param line     the line the record starts on, for the message
     * @param filename the file being read, for the message
     * @param token    the directive name, for the message
     * @param radix    the base the caller chose from the prefix
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if the text has no prefix that {@code radix} needs, or has no
     *                                digit in decimal
     */
    public static int parseIntString(String type, String string, int line, String filename, String token, Radix radix) {
        String holder = string;
        
        if (string.isEmpty())
            return 0;

        int sign = signFound(string);
        if (sign != 0) {
            string = string.substring(1);
            if (string.isEmpty()) {
                String message = type + " beginning at line: " + line + " of file: " + filename
                        + " the " + token + " value consists solely of a sign: " + holder;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
        }
        
        String stripped;
        
        switch (radix) {
            case HEX:
                if (string.startsWith("0x") || string.startsWith("0X"))
                    string = string.substring(2);
                else {
                    String message = type + " beginning at line: " + line + " of file: " + filename
                            + " has an illegal hex token " + token + " value: " + string;
                    logger.fatal(message);
                    throw new DataFileParseException(message);                    
                }
                stripped = stripStart(string, Radix.HEX);
                if (stripped.isEmpty())
                    return 0;
                break;
            case OCTAL:
                if (string.startsWith("0"))
                    string = string.substring(1);
                else {
                    String message = type + " beginning at line: " + line + " of file: " + filename
                            + " has an illegal octal token " + token + " value: " + string;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                stripped = stripStart(string, Radix.OCTAL);
                if (stripped.isEmpty())
                    return 0;
                break;
            default:
                stripped = stripStart(string, Radix.DECIMAL);
                if (stripped.isEmpty()) {
                    String message = type + " beginning at line: " + line + " of file: " + filename
                            + " has an unknown error with token " + token + " value: " + string;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
        }

        return parseString(type, stripped, line, filename, token, sign < 0, radix);
    }
    
    /**
     * Build the number from the leading digits of {@code string} in {@code radix}. Reading stops
     * at the first character that is not a digit of that base, as {@code strtol} stops, so in octal
     * {@code 08} is 0, {@code 019} is 1 and {@code 017} is 15, in hexadecimal {@code 1g} is 1 and
     * in decimal {@code 12abc} is 12. The sign is applied first and the result is then clamped to
     * the range of an {@code int}, the accepted deviation from C's wrap to 32 bits (see the class
     * block). Digits are gathered with {@code BigInteger}, so a figure of any length is read before
     * it is clamped.
     *
     * <p>Every caller has already stripped the prefix and checked that at least one digit follows,
     * returning 0 or throwing itself, so the empty digit string that returns 0 here is not reached
     * from them. It stays as the answer for a prefix with nothing after it, the same answer
     * {@code strtol} gives. The {@link NumberFormatException} and any other exception become a
     * {@link DataFileParseException}; the message opens with {@code type}, so each assembler's
     * errors name their own record kind.
     *
     * <p>Function parseString coded on 261022, commented in full on 261025.
     *
     * @param type       the kind of record being read, for the message
     * @param string     the text with any leading sign and prefix already removed
     * @param line       the line the record starts on, for the message
     * @param filename   the file being read, for the message
     * @param token      the directive name, for the message
     * @param foundMinus whether a leading {@code -} was removed
     * @param radix      the base to build the number in
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if the digits cannot be built into a number
     */
    private static int parseString(String type, String string, int line, String filename, String token, boolean foundMinus, Radix radix) {
        String stripped = stripStart(string, radix);
        
        if (stripped.isEmpty())
            return 0;
        
        int radixValue = switch (radix) {
            case HEX -> 16;
            case OCTAL ->  8;
            case DECIMAL -> 10;
            case BINARY -> 2;
        };
        
        try {
            BigInteger bigValue = new BigInteger(stripped, radixValue);
            bigValue = foundMinus ? bigValue.negate() : bigValue;
            bigValue = bigValue.min(BigInteger.valueOf(Integer.MAX_VALUE));
            bigValue = bigValue.max(BigInteger.valueOf(Integer.MIN_VALUE));
            return bigValue.intValue();
        } catch (NumberFormatException e) {
            String message = type + " beginning at line: " + line + " of file: " + filename
                    + " has an invalid " + token + " value: " + string;
            logger.fatal(message, e);
            throw new DataFileParseException(message);
        } catch (Exception e) {
            String message = type + " beginning at line: " + line + " of file: " + filename
                    + " has an unknown error with token " + token + " value: " + string;
            logger.fatal(message, e);
            throw new DataFileParseException(message);
        }
    }

    /**
     * Read a decimal number: {@link #parseString(String, String, int, String, String, boolean, Radix)}
     * with {@link Radix#DECIMAL}.
     *
     * <p>Outstanding: nothing calls it; every {@code parseIntString} passes its {@link Radix} to the
     * seven-argument form directly.
     *
     * <p>Function parseString (decimal) coded on 261022, commented in full on 261025.
     *
     * @param type       the kind of record being read, for the message
     * @param string     the text with any leading sign already removed
     * @param line       the line the record starts on, for the message
     * @param filename   the file being read, for the message
     * @param token      the directive name, for the message
     * @param foundMinus whether a leading {@code -} was removed
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if no digit could be read
     */
    private static int parseString(String type, String string, int line, String filename, String token, boolean foundMinus) {
        return parseString(type, string, line, filename, token, foundMinus, Radix.DECIMAL);
    }

    /**
     * Whether the raw text of a {@code uint} field starts with {@code -}: the second half of the
     * test in {@code parser.c}, function {@code parser_parse()}, which ends the load with
     * {@code PARSE_ERROR_NOT_NUMBER} when {@code strtoul} reads no digits <em>or</em> the token's
     * first character is {@code -}. Only the first character is looked at, as C's
     * {@code *tok == '-'} does, so {@code -0} counts although its value is 0, which is why the
     * callers test the text and not the number {@link #parseIntString} gives back.
     *
     * <p>Empty text, an absent optional field, gives {@code false}: C never reaches the test then,
     * and its {@code '\0'} would not match anyway. Text with a leading space is not looked past,
     * also as in C, though the lexer never produces one.
     *
     * <p>Function minusFound coded on 261023, commented in full on 261023.
     *
     * @param string the raw text from the parse record, not {@code null}
     * @return {@code true} if the first character is {@code -}
     */
    public static boolean minusFound(String string) {
        if (string.isEmpty())
            return false;

        return signFound(string) < 0;
    }

    /**
     * Read the text of an {@code int} field as C's {@code parser_getint} does ({@code strtol} in
     * base 0, from {@code parser_parse} in {@code parser.c}), but give {@code def} for empty text
     * instead of the five-argument form's 0. Anything else goes to the five-argument form
     * unchanged, so {@code 15} is 15, {@code -1} is -1, {@code 010} is 8 and {@code 08} is 0, a
     * lone {@code -} ends the load (it is not {@code def}), and so does text with no leading digits.
     *
     * <p>C has no empty {@code int} field: a missing one fails the line before the callback runs.
     * Empty text never comes from the grammars either, whose {@code INTEGER} tokens hold at least
     * one digit, so {@code def} is only reached by a caller that passes an empty string on
     * purpose. {@code DungeonProfileAssembler} passes 0, the value {@code mem_zalloc} leaves in
     * C's field. An absent optional directive is {@code null}, not empty, and the caller has to
     * deal with that before calling, as {@code DungeonProfileAssembler} does for {@code min-level:}
     * and {@code alloc:}.
     *
     * <p>Function parseIntString (with default) coded on 261023, commented in full on 261023.
     *
     * @param type     the kind of record being read, for the message ({@code "Dungeon profile"})
     * @param string   the raw text from the parse record, not {@code null}
     * @param line     the line the record starts on, for the message
     * @param filename the file being read, for the message
     * @param token    the directive name, for the message
     * @param def      the value to give for empty text
     * @return {@code def} for empty text, otherwise the value, clamped to the range of an
     *         {@code int}
     * @throws DataFileParseException if the text is not empty and has no leading digits
     */
    public static int parseIntString(String type, String string, int line, String filename, String token, int def) {
        if (string.isEmpty())
            return def;

        return parseIntString(type, string, line, filename, token); 
    }
    
    /**
     * The sign at the front of the raw text, looking at the first character only. C's {@code strtol}
     * and {@code strtoul} take one optional {@code -} or {@code +} before the digits, and
     * {@code parser_parse()} in {@code parser.c} tests {@code *tok == '-'} on the {@code uint}
     * fields; this is the same first-character look, shared by {@link #parseIntString} (to strip
     * the sign), {@link #minusFound(String)}, {@link #nonDigitFound(String, Radix)} and
     * {@link #signOnly(String)}.
     *
     * <p>Empty text gives 0, as does any first character that is not a sign. Nothing past the first
     * character is looked at, so {@code --5} is -1 and {@code +-5} is 1; the second sign is left for
     * the digit reader to refuse, as {@code strtol} refuses it.
     *
     * <p>Function signFound coded on 261024, commented in full on 261024.
     *
     * @param string the raw text from the parse record, not {@code null}
     * @return -1 if the first character is {@code -}, 1 if it is {@code +}, otherwise 0
     */
    public static int signFound(String string) {
        if (string.isEmpty())
            return 0;

        if (string.charAt(0) == '-')
            return -1;
        
        if (string.charAt(0) == '+')
            return 1;
        
        return 0;
    }
    
    /**
     * Whether the text is anything other than an optional sign followed by digits: the Java form of
     * the {@code *tc != ']'} test in {@code datafile.c}, function {@code find_value_arg()}. C reads
     * the figure inside the brackets of an ego-item {@code values:} or {@code min-values:} entry
     * with {@code strtol} in base 10 and refuses the entry if the next character is not the closing
     * bracket, so {@code [5x]} and {@code [-]} fail where {@link #parseIntString} on its own would
     * give 5 and 0. The caller hands over the text between the brackets.
     *
     * <p>Empty text gives {@code false}: C's {@code strtol} reads nothing from {@code []}, stops on
     * the {@code ]} and accepts it as 0, and {@link #parseIntString} gives 0 for empty text. A lone
     * sign gives {@code true} ({@link #signOnly(String)}), since C stops on the sign and refuses.
     * One leading {@code -} or {@code +} is skipped before the digits are checked, so {@code +-5}
     * is {@code true}. Whitespace anywhere gives {@code true}, where C's {@code strtol} would skip
     * leading whitespace; the lexer never produces any.
     *
     * <p>Range is not checked here: C also refuses a figure at {@code INT_MIN} or {@code INT_MAX},
     * which the caller tests on the value {@link #parseIntString} gives back.
     *
     * <p>{@code radix} says which characters count as digits: {@link Radix#DECIMAL} is the C test
     * and the only one a caller uses ({@code EgoItemAssembler} and {@code ItemObjectAssembler} pass
     * it). {@link Radix#OCTAL},
     * {@link Radix#BINARY} and {@link Radix#HEX} apply the same test to that base's digits, with no
     * {@code 0x} prefix allowed, which is a Java extension and not a C test.
     *
     * <p>Function nonDigitFound coded on 261024, commented in full on 261107 (callers corrected
     * 261107).
     *
     * @param string the text between the brackets, not {@code null}
     * @param radix  the base whose digits are allowed after the optional sign
     * @return {@code true} if the entry would be refused for what follows the optional sign
     */
    public static boolean nonDigitFound(String string, Radix radix) {
        if (string.isEmpty())
            return false;
        
        if (signOnly(string))
            return true;
        
        char radChar = '0';
        
        switch (radix) {
            case BINARY -> radChar = '1';
            case DECIMAL -> radChar = '9';
            case OCTAL -> radChar = '7';
        }
        
        if (signFound(string) != 0) string = string.substring(1);

        if (radix == Radix.HEX) {
            for (char c : string.toCharArray()) {
                if (!((c <= '9' && c >= '0') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')))
                    return true;
            }            
        } else {
            for (char c : string.toCharArray()) {
                if (c > radChar || c < '0')
                    return true;
            }
        }
        
        return false;
    }
    
    /**
     * Whether the text is one or more decimal digits and nothing else, with no sign. There is no C
     * function behind it: it is a plain test for a caller that wants to refuse a sign as well as
     * trailing text, where {@link #nonDigitFound(String, Radix)} allows one sign. Empty text gives
     * {@code false}.
     *
     * <p>{@code EffectAssembler} uses it to tell a number from a name in an effect sub-type, and
     * {@code ClassEquipAssembler} to tell a numeric {@code sval} from a name. {@code
     * PlayerPropertyAssembler} (the {@code bindui} figure), {@code ProjectionAssembler} (the
     * {@code denominator:} figure), {@code RoomProfileAssembler} (a tval) and
     * {@code UIEntryAssembler} (the {@code priority:} figure) call it as well.
     *
     * <p>Function onlyDigits coded on 261024, commented in full on 261107 (callers listed 261107).
     *
     * @param string the raw text from the parse record, not {@code null}
     * @return {@code true} if every character is {@code 0} to {@code 9} and there is at least one
     */
    public static boolean onlyDigits(String string) {
        if (string.isEmpty())
            return false;
        
        for (char c : string.toCharArray()) {
            if (c > '9' || c < '0')
                return false;
        }
        
        return true;
    }
    
    /**
     * Whether the text is a single {@code -} or {@code +} and nothing else. C's {@code strtol} reads
     * no digits from it, so the figure is refused ({@code PARSE_ERROR_NOT_NUMBER} in
     * {@code parser.c}, or the {@code *tc != ']'} test in {@code find_value_arg()}); every form of
     * {@link #parseIntString} refuses it now too, so this is a test a caller could make before
     * reading and not a guard that reading needs. Empty text gives {@code false}.
     *
     * <p>Outstanding: nothing outside {@code ParseUtils} calls it; {@link #nonDigitFound(String,
     * Radix)} is the one caller.
     *
     * <p>Function signOnly coded on 261024, commented in full on 261107 (caller noted 261107).
     *
     * @param string the raw text from the parse record, not {@code null}
     * @return {@code true} if the text is exactly one sign character
     */
    public static boolean signOnly(String string) {
        if (string.isEmpty())
            return false;
        
        return string.length() == 1 && signFound(string) != 0;
    }
    
    /**
     * The leading digits of {@code string} in {@code radix}, up to the first character that is not
     * one: the run {@code strtol} would read. Empty if the text does not start with a digit.
     *
     * <p>Function stripStart coded before 261025, commented in full on 261025.
     *
     * @param string the text with any sign and prefix already removed
     * @param radix  the base whose digits are kept
     * @return the leading digits, possibly empty
     */
    private static String stripStart(String string, Radix radix) {
        return switch (radix) {
            case BINARY -> stripBoolean(string);
            case OCTAL -> stripOctal(string);
            case DECIMAL -> stripDecimal(string);
            case HEX -> stripHex(string);
        };
    }
    
    /**
     * The leading run of {@code 0}-{@code 9}, {@code a}-{@code f} and {@code A}-{@code F}.
     *
     * <p>Function stripHex coded before 261025, commented in full on 261025.
     *
     * @param string the text to read from
     * @return the leading hexadecimal digits, possibly empty
     */
    private static String stripHex(String string) {
        StringBuilder builder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))
                builder.append(c);
            else 
                break;
        }
        
        return builder.toString();
    }
    
    /**
     * The leading run of {@code 0}-{@code 9}.
     *
     * <p>Function stripDecimal coded before 261025, commented in full on 261025.
     *
     * @param string the text to read from
     * @return the leading decimal digits, possibly empty
     */
    private static String stripDecimal(String string) {
        StringBuilder builder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (c >= '0' && c <= '9')
                builder.append(c);
            else
                break;
        }
        
        return builder.toString();
    }
    
    /**
     * The leading run of {@code 0}-{@code 7}, so {@code 08} gives {@code 0} and {@code 8} gives
     * the empty string.
     *
     * <p>Function stripOctal coded before 261025, commented in full on 261025.
     *
     * @param string the text to read from
     * @return the leading octal digits, possibly empty
     */
    private static String stripOctal(String string) {
        StringBuilder builder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (c >= '0' && c <= '7')
                builder.append(c);
            else
                break;
        }
        
        return builder.toString();
    }
    
    /**
     * The leading run of {@code 0} and {@code 1}, for {@link Radix#BINARY}, which no caller uses.
     *
     * <p>Function stripBoolean coded before 261025, commented in full on 261025.
     *
     * @param string the text to read from
     * @return the leading binary digits, possibly empty
     */
    private static String stripBoolean(String string) {
        StringBuilder builder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (c == '0' || c == '1')
                builder.append(c);
            else 
                break;
        }
        
        return builder.toString();
    }
    
    /**
     * The base {@code strtol} would choose for the text with base 0: {@link Radix#HEX} for
     * {@code 0x} or {@code 0X}, {@link Radix#OCTAL} for a leading {@code 0}, otherwise
     * {@link Radix#DECIMAL}. The text must have no sign or leading whitespace.
     *
     * <p>Outstanding: nothing calls it; the five-argument {@code parseIntString} makes the same
     * choice in line, and no caller of the {@link Radix} overload chooses a prefixed base any more.
     *
     * <p>Function checkRadix coded before 261025, commented in full on 261028.
     *
     * @param string the unsigned text
     * @return the base that base 0 reading would pick
     */
    private static Radix checkRadix(String string) {
        if (string.startsWith("0x") || string.startsWith("0X"))
            return Radix.HEX;
        
        if (string.startsWith("0"))
            return Radix.OCTAL;
        
        return Radix.DECIMAL;
    }
    
    /**
     * Whether the text holds nothing but spaces and tabs, the port of C's {@code contains_only_spaces}
     * ({@code z-util.c}), which {@code strspn}s over {@code " \t"} and tests that the next character is
     * the terminator. The empty text counts, as it does in C, and no other whitespace does: a newline,
     * carriage return, vertical tab or form feed makes the answer {@code false}.
     *
     * <p>{@code MonsterAssembler.assemble} uses it to turn a {@code message-vis:}, {@code message-invis:}
     * or {@code message-miss:} text of only spaces and tabs into {@code ""}, as C's
     * {@code add_alternate_spell_message} does. A {@code null} argument throws a
     * {@link NullPointerException}; C does not accept {@code NULL} here either, and the caller tests for
     * it first.
     *
     * <p>Function onlySpaceAndTab coded before 261026, commented in full on 261026.
     *
     * @param string the text to test, not {@code null}
     * @return {@code true} if every character is a space or a tab, or the text is empty
     */
    public static boolean onlySpaceAndTab(String string) {
        if (string.isEmpty())
            return true;
        
        for (char c : string.toCharArray()) {
            if (c != ' ' && c != '\t')
                return false;
        }
        
        return true;
    }

    /**
     * The text with the spaces and tabs removed from both ends, the narrow set that
     * {@link #onlySpaceAndTab(String)} tests. Newlines, carriage returns, vertical tabs and form
     * feeds are not removed, so {@code " \n x"} loses only its leading space. Text of nothing but
     * spaces and tabs, and empty text, gives {@code ""}. There is no C function behind it.
     *
     * <p>Outstanding: nothing under {@code src/main} calls it, and only {@code ParseUtilsWhitespaceTest}
     * does under {@code src/test}; the wider {@link #cTrim(String)} is the one in use.
     *
     * <p>Function trimOnlySpaceAndTab coded before 261028, commented in full on 261028.
     *
     * @param string the text to trim, or {@code null}
     * @return the trimmed text, or {@code null} if {@code string} is {@code null}
     */
    public static String trimOnlySpaceAndTab(String string) {
        if (string == null) return null;
        if (onlySpaceAndTab(string)) return "";

        return trimFrontOnlySpaceAndTab(trimRearOnlySpaceAndTab(string));
    }

    /**
     * The text with the trailing spaces and tabs removed and the front left alone. Text of
     * nothing but spaces and tabs, and empty text, gives {@code ""}.
     *
     * <p>{@code ClassEquipAssembler} calls it, after {@link #cTrimFront(String)}, to clear the
     * padding off the end of an {@code sval}; {@link #trimOnlySpaceAndTab(String)} calls it too.
     *
     * <p>Function trimRearOnlySpaceAndTab coded before 261028, commented in full on 261107
     * (caller corrected 261107).
     *
     * @param string the text to trim, or {@code null}
     * @return the trimmed text, or {@code null} if {@code string} is {@code null}
     */
    public static String trimRearOnlySpaceAndTab(String string) {
        if (string == null) return null;
        if (onlySpaceAndTab(string)) return "";

        int trimTo = string.length() - 1;

        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.charAt(i) != ' ' && string.charAt(i) != '\t') {
                trimTo = i;
                break;
            }
        }

        return string.substring(0, trimTo + 1);
    }

    /**
     * The text with the leading spaces and tabs removed and the rear left alone. Text of nothing
     * but spaces and tabs, and empty text, gives {@code ""}.
     *
     * <p>Outstanding: only {@link #trimOnlySpaceAndTab(String)} calls it in {@code src/main}.
     *
     * <p>Function trimFrontOnlySpaceAndTab coded before 261028, commented in full on 261028.
     *
     * @param string the text to trim, or {@code null}
     * @return the trimmed text, or {@code null} if {@code string} is {@code null}
     */
    public static String trimFrontOnlySpaceAndTab(String string) {
        if (string == null) return null;
        if (onlySpaceAndTab(string)) return "";

        while (!string.isEmpty()) {
            if (string.charAt(0) == ' ' || string.charAt(0) == '\t')
                string = string.substring(1);
            else
                break;
        }

        return string;
    }
    
    /**
     * Whether the text holds nothing but the characters C's {@code isspace} accepts in the C
     * locale: space, tab, newline, vertical tab, form feed and carriage return. Unlike
     * {@link #onlySpaceAndTab(String)}, which stands for {@code contains_only_spaces} in
     * {@code z-util.c}, this is the set {@code strtol} skips and {@code parser_parse()} in
     * {@code parser.c} skips in front of a line. The empty text counts. A {@code null} argument
     * throws a {@link NullPointerException}.
     *
     * <p>Function onlyCSpace coded before 261028, commented in full on 261028.
     *
     * @param string the text to test, not {@code null}
     * @return {@code true} if every character is C whitespace, or the text is empty
     */
    public static boolean onlyCSpace(String string) {
        for (char c : string.toCharArray()) {
            if (c != ' '  && c != '\t' &&
                c != '\f' && c != '\n' &&
                c != '\r' && c != '\013')
                return false;
        }
        return true;
    }

    /**
     * The text with the C whitespace of {@link #onlyCSpace(String)} removed from both ends. Text
     * of nothing but C whitespace, and empty text, gives {@code ""}.
     *
     * <p>Outstanding: nothing under {@code src/main} calls it. {@code ClassEquipAssembler}, which
     * clears the padding off an {@code sval} before looking at its sign and digits, calls
     * {@link #cTrimFront(String)} and {@link #trimRearOnlySpaceAndTab(String)} instead.
     *
     * <p>Function cTrim coded before 261028, commented in full on 261107 (caller claim corrected
     * 261107).
     *
     * @param string the text to trim, or {@code null}
     * @return the trimmed text, or {@code null} if {@code string} is {@code null}
     */
    public static String cTrim(String string) {
        return cTrimFront(cTrimRear(string));
    }

    /**
     * The text with the trailing C whitespace of {@link #onlyCSpace(String)} removed and the
     * front left alone. Text of nothing but C whitespace, and empty text, gives {@code ""}.
     *
     * <p>Outstanding: only {@link #cTrim(String)} calls it, and nothing under {@code src/main}
     * calls that.
     *
     * <p>Function cTrimRear coded before 261028, commented in full on 261107 (callers noted 261107).
     *
     * @param string the text to trim, or {@code null}
     * @return the trimmed text, or {@code null} if {@code string} is {@code null}
     */
    public static String cTrimRear(String string) {
        if (string == null) return null;
        if (onlyCSpace(string)) return "";

        int trimTo = string.length() - 1;

        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.charAt(i) != ' ' && string.charAt(i) != '\t' &&
                string.charAt(i) != '\f' && string.charAt(i) != '\n' &&
                string.charAt(i) != '\r' && string.charAt(i) != '\013') {
                trimTo = i;
                break;
            }
        }

        return string.substring(0, trimTo + 1);
    }

    /**
     * The text with the leading C whitespace of {@link #onlyCSpace(String)} removed and the rear
     * left alone, which is the skip {@code strtol} makes before the sign. Text of nothing but C
     * whitespace, and empty text, gives {@code ""}. The fixed-base {@code parseIntString} uses it,
     * and so do {@code ClassEquipAssembler} (on an {@code sval}) and {@code UIEntryAssembler} (on
     * the {@code priority:} figure).
     *
     * <p>Function cTrimFront coded before 261028, commented in full on 261107 (callers listed 261107).
     *
     * @param string the text to trim, or {@code null}
     * @return the trimmed text, or {@code null} if {@code string} is {@code null}
     */
    public static String cTrimFront(String string) {
        if (string == null) return null;
        if (onlyCSpace(string)) return "";

        while (!string.isEmpty()) {
            if (string.charAt(0) == ' ' || string.charAt(0) == '\t' ||
                string.charAt(0) == '\f' || string.charAt(0) == '\n' || 
                string.charAt(0) == '\r' || string.charAt(0) == '\013')
                string = string.substring(1);
            else
                break;
        }

        return string;
    }

    /**
     * Reads one {@code IGNORE_<element>} or {@code HATES_<element>} word of a {@code values:}-style
     * line and switches the matching {@link ElementInfoEnum} flag on for that element. Ports
     * {@code grab_element_flag} in {@code obj-init.c}, which every object-side parser tries on a
     * flag word before it gives up on it: the object-base, curse, object-kind, ego-item and artifact
     * readers all call it.
     *
     * <p>The word is cut at its first underscore. Nothing before the underscore means the word is
     * not for this function and gives {@code false}, so a word with no underscore at all is refused.
     * The text after the underscore must be an element name in upper case, exactly as C spells it,
     * and it keeps any underscore of its own ({@code HATES_HOLY_ORB} is the holy orb, not an element
     * called {@code ORB}). The text is looked up as {@code "ELEM_" + name} through
     * {@link ElementEnum#lookup}, which takes the whole constant name. The
     * placeholders {@link ElementEnum#ELEM_NONE} and {@link ElementEnum#ELEM_MAX} are Java
     * additions that C's name list does not hold, so {@code IGNORE_NONE} and {@code HATES_MAX}
     * give {@code false} here as they do there.
     *
     * <p>The text before the underscore is not compared whole. C calls {@code strncmp} with the
     * length of that text, so the text only has to be a <em>prefix</em> of {@code IGNORE} or
     * {@code HATES}: {@code IGN_FIRE} and {@code H_FIRE} are accepted, and so is {@code _FIRE},
     * whose empty prefix matches {@code IGNORE} first and so ignores. Text longer than the word
     * ({@code IGNORED_FIRE}, {@code HATESX_FIRE}) fails at the terminating NUL, and text that is a
     * prefix of neither ({@code RESIST_FIRE}) gives {@code false}. {@code IGNORE} is tried before
     * {@code HATES}, as in C. {@link String#startsWith(String)} on the word gives the same answers
     * as {@code strncmp}.
     *
     * <p>C writes into a fixed array of {@code struct element_info}, one slot per element, and the
     * flag is OR-ed in. Here the slot is created in {@code elInfo} the first time an element is
     * named, and a repeated word finds the flag already set: the result is still {@code true},
     * because {@link uk.co.jackoftradesltd.channel.utils.Flag#on} answering {@code false} for a flag
     * that was already on is not a failure to read the word. Nothing is added to {@code elInfo} for
     * a word that returns {@code false}.
     *
     * <p>Function grabElementFlag coded before 261105, commented in full on 261105 (lookup form
     * described on 261105).
     *
     * @param elInfo the per-element flags to update, keyed by element; a slot is created for an
     *               element on its first flag
     * @param flag   the flag word, such as {@code IGNORE_ACID} or {@code HATES_HOLY_ORB}
     * @return {@code true} if the word named an element and a known {@code IGNORE}/{@code HATES}
     * prefix and the flag is now on; {@code false} if it was not such a word
     */
    public static boolean grabElementFlag(Map<ElementEnum, ElementInfo> elInfo, String flag) {
        if (!flag.contains("_")) return false;
        
        int underscorePosition = flag.indexOf("_");
        String token = flag.substring(0, underscorePosition);
        String name = flag.substring(underscorePosition + 1);
        
        ElementEnum element = ElementEnum.lookup("ELEM_" + name);
        if (element == null || element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) return false;
        
        if ("IGNORE".startsWith(token)) {
            elInfo.computeIfAbsent(element, k -> new ElementInfo()).on(ElementInfoEnum.EL_INFO_IGNORE);
            return true;
        }
        
        if ("HATES".startsWith(token)) {
            elInfo.computeIfAbsent(element, k -> new ElementInfo()).on(ElementInfoEnum.EL_INFO_HATES);
            return true;
        }
        
        return false;
    }
    
    /**
     * Reads the display glyph of a {@code graphics:} line as C reads a {@code PARSE_T_CHAR} field:
     * the glyph is exactly one character, and it is kept whole. In C that is
     * {@code text_mbstowcs(&v->u.cval, tok, 1)} in {@code parser_parse()} ({@code parser.c}), a
     * {@code wchar_t} that the {@code graphics:} callbacks in {@code obj-init.c} store in
     * {@code d_char}. Java cannot hold a code point above {@code U+FFFF} in a {@code char}, so the
     * answer is split: a code point in the basic plane comes back as {@code glyphChar}
     * with {@code wideGlyphChar} 0, and a supplementary code point comes back as
     * {@code wideGlyphChar} with {@code glyphChar} {@code '\0'}. A caller tests {@code wideGlyphChar}
     * for 0 to know which half holds the glyph, as {@code ArtifactAssembler} does.
     *
     * <p>Text of more than one code point is refused, with {@code null}. C's {@code parser_parse()}
     * skips one code point with {@code utf8_fskip} and, if anything but a {@code :} follows, fails
     * the line with {@code PARSE_ERROR_FIELD_TOO_LONG}, so {@code "ab"} never reaches a callback.
     * The text given here is the glyph token alone, with no {@code :} after it, so every code point
     * past the first is "too long". A letter with a combining mark after it ({@code "e"} then
     * {@code U+0301}) is two code points in both languages and is refused in both. The count is
     * of code points and not {@code char}s, so one supplementary glyph is accepted.
     *
     * <p>{@code null} and empty text give {@code null} as well. C has no such case: an empty
     * character field is a missing field, which {@code parser_parse()} refuses
     * ({@code PARSE_ERROR_MISSING_FIELD}) before any callback runs. All three refusals reach the
     * caller as the same {@code null}, which it turns into a fatal error.
     *
     * <p>Function parseADCGlyph coded before 261107, commented in full on 261107 (rewritten the same
     * day to say that more than one code point is refused, not ignored).
     *
     * @param glyph the glyph text from the parse record, or {@code null}
     * @return the one code point of {@code glyph}, split as above, or {@code null} if {@code glyph}
     * is {@code null}, empty, or longer than one code point
     */
    public static GlyphCharacter parseADCGlyph(String glyph) {
        if (glyph == null || glyph.isEmpty()) return null;
        int noWideChars = glyph.codePointCount(0, glyph.length());
        if (noWideChars > 1) return null;
        int wideChar = glyph.codePointAt(0);
        if (Character.isSupplementaryCodePoint(wideChar)) {
            return new GlyphCharacter('\0', wideChar);
        } else {
            return new GlyphCharacter(glyph.charAt(0), 0);
        }
    }

    /**
     * Reads one {@code aim:}-style flag figure and says whether it is non-zero. C has no function of
     * this name: the behaviour is {@code parse_act_aim} in {@code obj-init.c}, which takes
     * {@code parser_getuint(p, "aim")} and stores {@code val ? true : false}, and the number itself
     * is read in {@code parser_parse()} ({@code parser.c}) with {@code strtoul(tok, &z, 0)}. This
     * method reproduces that read and that test together, with the same base 0 rules as the other
     * readers here:
     *
     * <ul>
     *   <li>leading C whitespace is skipped ({@link #cTrimFront(String)});</li>
     *   <li>an optional {@code +} or {@code -} comes next, and the sign never changes the answer,
     *   since only zero or non-zero is asked: {@code "-5"} and {@code "+5"} are {@code true};</li>
     *   <li>{@code 0x} or {@code 0X} selects base 16, any other leading {@code 0} selects base 8,
     *   and anything else is base 10, so {@code "01"}, {@code "07"} and {@code "012"} are
     *   {@code true} and {@code "00"} is {@code false};</li>
     *   <li>reading stops at the first character that is not a digit of the base, and anything
     *   after it is ignored, so {@code "5abc"} is {@code true} and {@code "08"} is {@code false}
     *   (the {@code 0} is read, the {@code 8} is not an octal digit).</li>
     * </ul>
     *
     * <p>{@code Optional.empty()} stands for C's {@code PARSE_ERROR_NOT_NUMBER}, which
     * {@code parser_parse()} raises only when {@code strtoul} read no characters at all
     * ({@code z == tok}). That is the empty text, text of only whitespace, a sign with no digits
     * ({@code "-"}, {@code "- 5"}, {@code "--5"}) and text that starts with a non-digit
     * ({@code "abc"}). A prefix with nothing readable after it is not a failure: {@code "0x"},
     * {@code "0xg"} and {@code "08"} read the leading {@code 0} as the value zero, so they are
     * {@code false}, as in C. A {@code null} argument also gives empty; C has no such case.
     *
     * <p>C's refusal of a leading {@code -} on an unsigned field ({@code *tok == '-'} in
     * {@code parser_parse()}) is not made here: it tests the raw token before whitespace is skipped,
     * so the caller has to make it. This method answers {@code true} for {@code "-5"}.
     *
     * <p>Accepted deviation (261027, numbers outside the {@code int} range): C reads a 64-bit
     * {@code strtoul} result and stores it in a 32-bit {@code uval}, which wraps ({@code 4294967296}
     * is zero in C and {@code 2147483648} is non-zero). Here a figure that does not fit in an
     * {@code int} makes {@code Integer.parseInt} throw, and the answer is empty. Do not flag.
     *
     * <p>Outstanding: nothing in {@code src/main} or {@code src/test} calls it.
     *
     * <p>Function isTrue coded before 261107, commented in full on 261107.
     *
     * @param string the figure as it appears in the data file, or {@code null}
     * @return {@code Optional.of(true)} if the figure reads as a non-zero number,
     * {@code Optional.of(false)} if it reads as zero, and {@code Optional.empty()} if C would
     * report it as not a number
     */
    public static Optional<Boolean> isTrue(String string) {
        try {
            if (signOnly(string)) return Optional.empty();
            string = cTrimFront(string);
            if (string.isEmpty()) return Optional.empty();

            int sign = signFound(string);
            if (sign != 0) string = string.substring(1);

            Radix radix = Radix.DECIMAL;
            if (string.charAt(0) == '0') {
                if (string.equals("0"))
                    return Optional.of(false);
                if (string.length() > 1 && (string.charAt(1) == 'x' || string.charAt(1) == 'X')) {
                    string = string.substring(2);
                    radix = Radix.HEX;
                } else {
                    radix = Radix.OCTAL;
                    string = string.substring(1);
                }
            }
            
            int radixValue = 10;
            String stripped;
            switch (radix) {
                case HEX -> {
                    radixValue = 16;
                    stripped = stripHex(string);
                    if (stripped.isEmpty()) return Optional.of(false);
                }
                case OCTAL -> {
                    radixValue = 8;
                    stripped = stripOctal(string);
                    if (stripped.isEmpty()) return Optional.of(false);
                }
                case DECIMAL -> {
                    stripped = stripDecimal(string);
                }
                default -> {
                    return Optional.empty();
                }
            }
            
            int value = Integer.parseInt(stripped, radixValue);
            
            return Optional.of(value != 0);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * The bases the readers work in. {@code strtol} base 0 chooses between {@link #HEX},
     * {@link #OCTAL} and {@link #DECIMAL}; {@link #BINARY} is not a base C reads and is here for
     * {@link #nonDigitFound(String, Radix)} and the readers that take a {@code Radix}, which give
     * it the decimal checks and then read binary digits. Nothing in {@code src/main} passes it.
     *
     * <p>Enum Radix coded before 261025, commented in full on 261107 (BINARY's reach corrected
     * 261107).
     */
    public enum Radix {
        BINARY, OCTAL, DECIMAL, HEX
    }

    /**
     * One display glyph, split because Java's {@code char} cannot hold every code point that C's
     * {@code wchar_t} does. Exactly one half is meaningful: a glyph in the basic plane is in
     * {@code glyphChar} with {@code wideGlyphChar} 0, and a supplementary-plane glyph is in
     * {@code wideGlyphChar} with {@code glyphChar} {@code '\0'}. The port of the {@code wchar_t}
     * that C's {@code PARSE_T_CHAR} field holds; {@link #parseADCGlyph(String)} builds it.
     *
     * <p>Record GlyphCharacter coded before 261107, commented in full on 261107.
     *
     * @param glyphChar     the glyph when it fits in a {@code char}, otherwise {@code '\0'}
     * @param wideGlyphChar the glyph's code point when it does not fit in a {@code char},
     *                      otherwise 0
     */
    public record GlyphCharacter (char glyphChar,
                                  int wideGlyphChar) {}
}
