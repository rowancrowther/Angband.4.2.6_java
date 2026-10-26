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
 * Shared number reading for the assemblers, so far used by {@link
 * uk.co.jackoftradesltd.backend.parser.activation.ActivationAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.artifact.ArtifactAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffectAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.blowmethod.BlowMethodAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.brand.BrandAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.chesttrap.ChestTrapAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.curse.CurseAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.dungeonprofile.RoomAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.dungeonprofile.StreamerAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.dungeonprofile.TunnelAssembler}, {@link
 * uk.co.jackoftradesltd.backend.parser.egoitem.EgoItemAssembler} and {@link
 * uk.co.jackoftradesltd.backend.parser.flavour.FlavourAssembler}. The grammar hands over an
 * integer as text, and these functions turn it into an {@code int} the way {@code parser.c} does
 * for the {@code int} and {@code uint} fields of a data-file line: {@code parser_getint} reads
 * with {@code strtol} and {@code parser_getuint} with {@code strtoul}, both in base 0, so a
 * leading {@code 0} means octal and anything else decimal.
 *
 * <p>There are four ways in. The five-argument {@code parseIntString} is the base 0 reading
 * above. The six-argument one ending in {@code boolean octal} takes the base from its caller
 * instead of from the text, for the figures C reads in a fixed base: the two numbers of an
 * {@code alloc:} range are read by {@code grab_int_range} in {@code datafile.c} with
 * {@code strtol} in base 10, so {@code alloc:1:010 to 020} is ten to twenty there, where base 0
 * would make it eight to sixteen. The six-argument one ending in {@code int def} is the base 0
 * reading with a caller-chosen value for empty text. The six-argument one ending in
 * {@link Radix} is the base 0 reading split in two: the caller looks at the prefix and says
 * which of {@code 0x}, {@code 0} or neither it saw, and this reads the digits after it. All four
 * end in the same digit-by-digit reader.
 *
 * <p>Two further differences from C, neither reachable from a data file: a lone {@code -} or
 * {@code +} gives 0 here where C refuses it as {@code PARSE_ERROR_NOT_NUMBER}, and
 * {@code grab_int_range} rejects a
 * number at {@code INT_MIN} or {@code INT_MAX} outright where this clamps it, which the caller's
 * 0 to 255 range check then refuses all the same.
 *
 * <p>Accepted deviation (2026-10-22, "An {@code aim:}, {@code level:} or {@code power:} too big
 * for its C type is clamped, where C wraps it" in {@code docs/Accepted_Deviations.md}): C stores
 * the 64-bit result in a 32-bit field with no range check and keeps the low 32 bits, so
 * {@code level:3000000000} becomes -1294967296. Here the value is clamped to
 * {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}, so it becomes 2147483647.
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
 * function {@code find_value_arg()}, which the ego-item {@code values:} and {@code min-values:}
 * lines go through, so trailing text in a bracketed figure ends the load where
 * {@link #parseIntString} on its own would read up to it.
 *
 * <p>Class ParseUtils coded on 261022, commented in full on 261025 (third way in added 261023,
 * sign and digit tests added 261024, fourth way in and the {@code 0x} reading added 261025).
 */
public class ParseUtils {
    /** Receives the fatal message before each {@link DataFileParseException} is thrown. */
    private static final Logger logger = LogManager.getLogger(ParseUtils.class);

    /**
     * Read the text of an {@code aim:}, {@code level:} or {@code power:} line as C's {@code strtol}
     * with base 0 reads it: skip leading whitespace, strip one leading {@code -} or {@code +}
     * ({@link #signFound(String)}), choose hexadecimal if the digits start with {@code 0x} or
     * {@code 0X}, octal if they start with {@code 0} and decimal otherwise, and hand over to
     * {@code parseString}, which applies the sign before it clamps. Empty text, which is an absent
     * directive, gives 0, and so does a lone sign, which the lexer never produces. Text that is
     * only whitespace has no digits and ends the load.
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
     * <p>Function parseIntString coded on 261022, commented in full on 261025 (leading
     * {@code +} read 261024, prefix and whitespace rewritten 261025).
     *
     * @param type     the kind of record being read, for the message ({@code "Activation"},
     *                 {@code "Artifact"})
     * @param string   the raw text from the parse record
     * @param line     the line the record starts on, for the message
     * @param filename the file being read, for the message
     * @param token    the directive name, for the message
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if the text has no leading digits
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
        
        if (sign != 0)
            string = string.substring(1);

        if (string.isEmpty())
            return 0;

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
     * and the clamp are as in the five-argument form. The caller still has to check the range: C
     * refuses a figure outside 0 to 255 for {@code alloc:} ({@code PARSE_ERROR_OUT_OF_BOUNDS}), and
     * this does not. {@code EgoItemAssembler} also reads through this form with {@code octal} false
     * for the figures C reads in {@code find_value_arg()} ({@code strtol} in base 10), after
     * {@link #nonDigitFound(String, Radix)} has refused trailing text.
     *
     * <p>No prefix is stripped: a fixed base 10 reading of {@code 0x10} gives 0 (the {@code x}
     * stops it), as {@code strtol(..., 10)} does, and a fixed base 8 reading of {@code 08} gives 0.
     * Text with no digit of the base ({@code abc}, {@code 8} in octal) throws.
     *
     * <p>Function parseIntString (fixed base) coded on 261022, commented in full on 261025.
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

        int sign = signFound(string);
        if (sign != 0) {
            string = string.substring(1);
        }

        if (string.isEmpty())
            return 0;
        
//        if (string.charAt(0) != '0' && octal) {
//            String message = type + " beginning at line: " + line + " of file: " + filename
//                    + " has an invalid " + token + " value: " + holder;
//            logger.fatal(message);
//            throw new DataFileParseException(message);
//        }
       
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
     * exists for {@code FlavourAssembler}, whose {@code index} is a {@code uint} that
     * {@code parser_parse()} in {@code parser.c} reads with {@code strtoul} in base 0.
     *
     * <p>One leading {@code -} or {@code +} is stripped here ({@link #signFound(String)}) before
     * the prefix is looked for, so the prefix test is on the unsigned text; a caller that chooses
     * the radix from the raw text has to look past the sign as well. Empty text and a lone sign give
     * 0, as in the five-argument form.
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
     * <p>Function parseIntString (with radix) coded before 261025, commented in full on 261025.
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
        if (string.isEmpty())
            return 0;

        int sign = signFound(string);
        if (sign != 0) {
            string = string.substring(1);
        }

        if (string.isEmpty())
            return 0;
        
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
     * lone {@code -} is 0 (not {@code def}), and text with no leading digits ends the load.
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
     * and the only one a caller uses ({@code EgoItemAssembler} passes it). {@link Radix#OCTAL},
     * {@link Radix#BINARY} and {@link Radix#HEX} apply the same test to that base's digits, with no
     * {@code 0x} prefix allowed, which is a Java extension and not a C test.
     *
     * <p>Function nonDigitFound coded on 261024, commented in full on 261025.
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
     * <p>Outstanding: nothing under {@code src/main} calls it yet.
     *
     * <p>Function onlyDigits coded on 261024, commented in full on 261024.
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
     * {@code parser.c}, or the {@code *tc != ']'} test in {@code find_value_arg()}); here
     * {@link #parseIntString} gives 0 for it instead, so a caller that wants C's refusal asks this
     * first. Empty text gives {@code false}.
     *
     * <p>Function signOnly coded on 261024, commented in full on 261024.
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
     * choice in line, and {@code FlavourAssembler} makes it before calling the {@link Radix}
     * overload.
     *
     * <p>Function checkRadix coded before 261025, commented in full on 261025.
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
     * The bases the readers work in. {@code strtol} base 0 chooses between {@link #HEX},
     * {@link #OCTAL} and {@link #DECIMAL}; {@link #BINARY} is not a base C reads and is here for
     * {@link #nonDigitFound(String, Radix)} and the private {@code stripStart} only.
     *
     * <p>Enum Radix coded before 261025, commented in full on 261025.
     */
    public enum Radix {
        BINARY, OCTAL, DECIMAL, HEX
    }
}
