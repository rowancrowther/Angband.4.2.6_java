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
 * uk.co.jackoftradesltd.backend.parser.dungeonprofile.TunnelAssembler} and {@link
 * uk.co.jackoftradesltd.backend.parser.egoitem.EgoItemAssembler}. The grammar hands over an
 * integer as text, and these functions turn it into an {@code int} the way {@code parser.c} does
 * for the {@code int} and {@code uint} fields of a data-file line: {@code parser_getint} reads
 * with {@code strtol} and {@code parser_getuint} with {@code strtoul}, both in base 0, so a
 * leading {@code 0} means octal and anything else decimal.
 *
 * <p>There are three ways in. The five-argument {@code parseIntString} is the base 0 reading
 * above. The six-argument one ending in {@code boolean octal} takes the base from its caller
 * instead of from the text, for the figures C reads in a fixed base: the two numbers of an
 * {@code alloc:} range are read by {@code grab_int_range} in {@code datafile.c} with
 * {@code strtol} in base 10, so {@code alloc:1:010 to 020} is ten to twenty there, where base 0
 * would make it eight to sixteen. The six-argument one ending in {@code int def} is the base 0
 * reading with a caller-chosen value for empty text. All three end in the same digit-by-digit
 * reader.
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
 * <p>Not read, because the {@code INTEGER} token in {@code EffectBlockLexer.g4} is only an
 * optional {@code -} and decimal digits, so the text never holds them: whitespace, and the
 * {@code 0x} hexadecimal prefix that base 0 allows ({@code 0x10} would give 0 here and 16 in C).
 * A single leading {@code +} is read, as {@code strtol} reads it ({@code +5} is 5 and
 * {@code +010} is 8 in base 0), though the lexer does not produce one either. Text with trailing
 * characters after the digits is read up to them, as {@code strtol} does.
 *
 * <p>The number readers never refuse a sign. For the {@code uint} fields, where C does, the
 * caller asks {@link #minusFound(String)} about the raw text and ends the load itself.
 *
 * <p>The remaining public functions are small tests on the raw text, with no single C function
 * behind them: {@link #signFound(String)} reports the leading sign, {@link #signOnly(String)}
 * whether the text is nothing but a sign, {@link #onlyDigits(String)} whether it is nothing but
 * digits, and {@link #nonDigitFound(String)} whether anything after an optional sign is not a
 * digit. {@code nonDigitFound} stands in for the {@code *tc != ']'} test in {@code datafile.c},
 * function {@code find_value_arg()}, which the ego-item {@code values:} and {@code min-values:}
 * lines go through, so trailing text in a bracketed figure ends the load where
 * {@link #parseIntString} on its own would read up to it.
 *
 * <p>Class ParseUtils coded on 261022, commented in full on 261024 (third way in added 261023,
 * sign and digit tests added 261024).
 */
public class ParseUtils {
    /** Receives the fatal message before each {@link DataFileParseException} is thrown. */
    private static final Logger logger = LogManager.getLogger(ParseUtils.class);

    /**
     * Read the text of an {@code aim:}, {@code level:} or {@code power:} line as C's {@code strtol}
     * with base 0 reads it: strip one leading {@code -} or {@code +} ({@link #signFound(String)}),
     * choose octal if the digits start with {@code 0} and decimal otherwise, and hand over to the
     * matching {@code parseString}, which applies the sign before it clamps. Empty text, which is
     * an absent directive, gives 0, and so does a lone sign, which the lexer never produces.
     *
     * <p>It does not refuse a negative number: {@code ActivationAssembler.assemble} rejects a
     * leading {@code -} on {@code aim:} and {@code power:} first (C's {@code strtoul} path refuses
     * it as {@code PARSE_ERROR_NOT_NUMBER}), so only {@code level:} arrives here negative.
     *
     * <p>Function parseIntString coded on 261022, commented in full on 261024 (leading
     * {@code +} read 261024).
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
        if (string.isEmpty())
            return 0;

        int sign = signFound(string);
        
        if (sign != 0)
            string = string.substring(1);

        if (string.isEmpty())
            return 0;

        if (string.charAt(0) == '0')
            return parseString(type, string, line, filename, token, sign < 0, 8);
        else
            return parseString(type, string, line, filename, token, sign < 0);
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
     * {@link #nonDigitFound(String)} has refused trailing text.
     *
     * <p>Function parseIntString (fixed base) coded on 261022, commented in full on 261024.
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
        if (string.isEmpty())
            return 0;
        
        int sign = signFound(string);
        if (sign != 0) {
            string = string.substring(1);
        }
        
        if (string.isEmpty())
            return 0;
        
        if (octal)
            return parseString(type, string, line, filename, token, sign < 0, 8);
        else
            return parseString(type, string, line, filename, token, sign < 0);
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
     * <p>If no digit is read, which for text that starts with {@code 0} read in octal cannot
     * happen, the digit string is empty and the {@link NumberFormatException} becomes a
     * {@link DataFileParseException}, as C's {@code PARSE_ERROR_NOT_NUMBER} stops the load. The
     * message opens with {@code type}, so each assembler's errors name their own record kind.
     *
     * <p>Function parseString coded on 261022, commented in full on 261022.
     *
     * @param type       the kind of record being read, for the message
     * @param string     the text with any leading sign already removed
     * @param line       the line the record starts on, for the message
     * @param filename   the file being read, for the message
     * @param token      the directive name, for the message
     * @param foundMinus whether a leading {@code -} was removed
     * @param radix     the base to build the number in, 3 to 10
     * @return the value, clamped to the range of an {@code int}
     * @throws DataFileParseException if {@code radix} is out of range or no digit could be read
     */
    private static int parseString(String type, String string, int line, String filename, String token, boolean foundMinus, int radix) {
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
     * Read a decimal number: {@link #parseString(String, String, int, String, String, boolean, int)} with
     * radix 10. Used for text that does not start with {@code 0}, and for every figure the
     * six-argument {@code parseIntString} is asked to read in base 10.
     *
     * <p>Function parseString (decimal) coded on 261022, commented in full on 261022.
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
        return parseString(type, string, line, filename, token, foundMinus, 10);
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
     * the sign), {@link #minusFound(String)}, {@link #nonDigitFound(String)} and
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
     * <p>Function nonDigitFound coded on 261024, commented in full on 261024.
     *
     * @param string the text between the brackets, not {@code null}
     * @return {@code true} if the entry would be refused for what follows the optional sign
     */
    public static boolean nonDigitFound(String string) {
        if (string.isEmpty())
            return false;
        
        if (signOnly(string))
            return true;
        
        if (signFound(string) != 0) string = string.substring(1);

        for (char c : string.toCharArray()) {
            if (c > '9' || c < '0')
                return true;
        }
        
        return false;
    }
    
    /**
     * Whether the text is one or more decimal digits and nothing else, with no sign. There is no C
     * function behind it: it is a plain test for a caller that wants to refuse a sign as well as
     * trailing text, where {@link #nonDigitFound(String)} allows one sign. Empty text gives
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
}
