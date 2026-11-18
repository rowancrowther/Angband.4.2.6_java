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

package uk.co.jackoftradesltd.backend.parser.vault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw, text-typed {@link VaultParseRecord}s from {@code vault.txt} into typed
 * {@link Vault}s, C's {@code struct vault} (Java port of the {@code parse_vault_*} handlers in
 * {@code generate.c}: {@code parse_vault_name()}, {@code parse_vault_type()},
 * {@code parse_vault_rating()}, {@code parse_vault_rows()}, {@code parse_vault_columns()},
 * {@code parse_vault_min_depth()}, {@code parse_vault_max_depth()}, {@code parse_vault_flags()} and
 * {@code parse_vault_d()}).
 *
 * <p>This is where interpretation happens:
 * <ul>
 *   <li>{@code type:} resolves to a {@link RoomType} by C's exact room-builder name, as written in
 *       {@code list-rooms.h} ({@code "Lesser vault"}, {@code "Interesting room"},
 *       {@code "room of chambers"}). C compares with {@code streq} in {@code parse_vault_rows()} and
 *       {@code parse_vault_columns()}, so the match is case-sensitive, and any of the nineteen
 *       builders is accepted, not only the vault types. No match is C's
 *       {@code PARSE_ERROR_NO_ROOM_FOUND}. The name is not checked when {@code type:} is read,
 *       only when {@code rows:} or {@code columns:} is, as in C, so a record that never gives
 *       {@code rows:} or {@code columns:} loads with a {@code null} type;</li>
 *   <li>{@code rows:} and {@code columns:} are C {@code uint}s: a leading {@code -} is refused,
 *       as C's {@code PARSE_ERROR_NOT_NUMBER}, using {@link ParseUtils#minusFound(String)}, and the
 *       rest is read through {@link ParseUtils#parseIntString(String, String, int, String, String)}.
 *       C keeps each in a {@code uint8_t}, so the number is narrowed to 0 to 255 before it is
 *       compared or stored, and 256 reads as 0. Each is capped by the type's own
 *       {@code max_height} or {@code max_width}, which is itself allowed
 *       ({@code PARSE_ERROR_VAULT_TOO_BIG} only above it). There is no lower bound, so zero
 *       loads as it does in C;</li>
 *   <li>every {@code D:} line must be exactly as long as {@code columns} was when the line is
 *       read, C's {@code PARSE_ERROR_VAULT_DESC_WRONG_LENGTH}, so a {@code D:} line ahead of
 *       {@code columns:} is compared with 0. As in C, the number of {@code D:} lines is
 *       never compared with {@code rows}. The lines are kept as a list and also joined into the
 *       flat text C keeps in {@code v->text}; a record with no {@code D:} line keeps a
 *       {@code null} flat text, as C's {@code text} stays {@code NULL};</li>
 *   <li>{@code rating:} is a signed {@code int} when C reads it but C stores it in a
 *       {@code uint8_t}, so the number is narrowed to 0 to 255 and a negative rating wraps
 *       ({@code -1} loads as 255);</li>
 *   <li>{@code min-depth:} and {@code max-depth:} are {@code uint}s and refuse a minus as rows and
 *       columns do, and both are narrowed to 0 to 255 as C's {@code uint8_t} fields do.
 *       {@code max-depth:0} means "no maximum", and becomes
 *       {@link GameConstants#getWorldMaxDepth()} ({@code z_info->max_depth}), as C rewrites it.
 *       C tests the number for zero before the narrowing and stores the replacement in the same
 *       {@code uint8_t}, so {@code max-depth:256} loads as 0, and the world maximum is narrowed
 *       too (a world maximum of 300 would load as 44);</li>
 *   <li>{@code flags:} resolve to {@link RoomFlags}, matched case-exactly as C's
 *       {@code lookup_flag()} matches with {@code streq}. {@code NONE} is refused, because C's
 *       {@code room_flags} table holds it at index 0, which {@code lookup_flag()} never reaches from
 *       {@code FLAG_START} (1). {@code MAX} is accepted and set, because C's table holds
 *       {@code "MAX"} from {@code list-room-flags.h} and {@code grab_flag()} turns it on.</li>
 * </ul>
 *
 * <p>The directives are handled in the order the file wrote them, one at a time, as C's handlers
 * are. After {@code name:} the grammar lets {@code type:}, {@code rating:}, {@code rows:},
 * {@code columns:}, {@code min-depth:}, {@code max-depth:}, {@code flags:} and {@code D:} come in
 * any order, and repeat. So the order matters exactly where it does in C: {@code rows:} and
 * {@code columns:} need a {@code type:} before them, and a {@code D:} line is measured against the
 * {@code columns:} seen so far. A repeated directive overwrites the earlier value (every
 * {@code flags:} line adds to the same set, and every {@code D:} line adds a row). Where C would
 * crash on a missing {@code type:} (it passes a {@code NULL} to {@code streq}), this class throws a
 * {@link DataFileParseException} naming the line.
 *
 * <p>A number outside the {@code int} range is clamped by {@code parseIntString} where C wraps it
 * (a {@code uint} truncated to 32 bits, or a {@code long} narrowed to an {@code int}). This is an
 * accepted deviation; the shipped data cannot reach it.
 *
 * <p>Each vault is put at the front of the list, so the list comes back in reverse file order, as
 * C's {@code parse_vault_name()} builds its list by pointing each new vault at the previous head.
 *
 * <p><b>Error policy:</b> every bad value is fatal, as it is in C, which stops the load at the
 * first handler that returns an error. Each one logs a fatal message and throws a
 * {@link DataFileParseException}, so no record is dropped. Hard grammar and lexer errors are
 * handled upstream.
 *
 * <p>Runs after {@code GameConstants}, because turning {@code max-depth:0} into a real number
 * needs the world's maximum depth.
 *
 * <p>Class VaultAssembler coded before 260915, commented in full on 261118 (first commented
 * 260915; rewritten 261030 once soft errors became fatal, every room builder name was accepted
 * and the {@code uint} minus checks were added; updated on 261105 for the removal of the
 * soft-error path; rewritten 261118 for file-order handling, the 8-bit narrowing and the
 * per-handler blocks).
 *
 * @author Rowan Crowther
 */
public class VaultAssembler implements Assembler <VaultParseRecord, List<Vault>> {
    /**
     * Receives the fatal message logged before each {@link DataFileParseException} this class
     * throws.
     *
     * <p>Field logger coded before 260915, commented in full on 261030 (first commented 260915).
     */
    private static final Logger logger = LogManager.getLogger(VaultAssembler.class);

    /**
     * Assembles every parsed vault record into a {@link Vault}, the Java form of the
     * {@code parse_vault_*} handlers in {@code generate.c} filling each {@code struct vault}. Each
     * record starts from an empty vault (no name, no type, an empty flag set and map list, every
     * number 0), then its directives are handed one at a time, in file order, to the handler for
     * that directive name ({@code name}, {@code type}, {@code rating}, {@code rows},
     * {@code columns}, {@code min-depth}, {@code max-depth}, {@code flag}, {@code map}, the last
     * two being the names the grammar gives to {@code flags:} and {@code D:}). Every directive
     * except {@code name} first goes through {@link #checkNameFound}. A name the switch does not
     * know throws, which the grammar never produces. The first failure throws, so a file loads whole or
     * not at all, as in C. Each finished vault goes at the front of the list.
     *
     * <p>Function assemble coded before 260915, commented in full on 261118 (first commented
     * 260915; rewritten 261030 for the fatal errors and the C-exact type names; rewritten 261118
     * for file-order handling).
     *
     * @param records  the raw parse records to assemble
     * @param filename the data file the records came from, for error messages
     * @return the assembled vaults, in reverse file order, as C's linked list holds them
     * @throws DataFileParseException if a directive has the wrong number of values, comes before a
     *                                name, rows or columns come before a type that is a room
     *                                builder name, rows, columns or a depth starts with a minus,
     *                                rows or columns exceed the type's limit, a {@code D:} line is
     *                                not as long as {@code columns}, a number cannot be read, or a
     *                                flag is unknown or {@code NONE}
     */
    @Override
    public List<Vault> assemble(@NotNull List<VaultParseRecord> records,
                                    String filename) {
        List<Vault> vaults = new ArrayList<>();

        for (VaultParseRecord record : records) {
            Flag<RoomFlags> startingFlags = new Flag<>(RoomFlags.class);
            Vault vault = new Vault(null, null, null, new ArrayList<>(), startingFlags, 0, 0, 0, 0, 0);

            for (Directive directive : record.directives()) {
                switch (directive.name()) {
                    case "name" -> {
                        parseName(vault, directive, filename);
                    }
                    case "type" -> {
                        checkNameFound(vault, directive, filename);
                        parseType(vault, directive, filename);
                    }
                    case "rating" -> {
                        checkNameFound(vault, directive, filename);
                        parseRating(vault, directive, filename);
                    }
                    case "rows" -> {
                        checkNameFound(vault, directive, filename);
                        parseRows(vault, directive, filename);
                    }
                    case "columns" -> {
                        checkNameFound(vault, directive, filename);
                        parseColumns(vault, directive, filename);
                    }
                    case "min-depth" -> {
                        checkNameFound(vault, directive, filename);
                        parseMinDepth(vault, directive, filename);
                    }
                    case "max-depth" -> {
                        checkNameFound(vault, directive, filename);
                        parseMaxDepth(vault, directive, filename);
                    }
                    case "flag" -> {
                        checkNameFound(vault, directive, filename);
                        parseFlag(vault, directive, filename);
                    }
                    case "map" -> {
                        checkNameFound(vault, directive, filename);
                        parseMap(vault, directive, filename);
                    }
                    default -> {
                        String message = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                                "a unknown directive: " + directive.name();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            
            vaults.addFirst(vault);
        }
        
        return vaults;
    }
    
    /**
     * Refuses a directive that arrives before any {@code name:}, which is C's
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER} ({@code if (!v)} at the top of every
     * {@code parse_vault_*} handler except {@code parse_vault_name()}). The vault has no name until
     * {@link #parseName} has run, so a {@code null} name stands for C's {@code NULL} vault. The
     * grammar opens every record with {@code name:}, so a parsed file cannot reach this; it guards
     * a hand-built record.
     *
     * <p>Method checkNameFound coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled
     * @param directive the directive about to be handled, for the message
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the vault has no name yet
     */
    private void checkNameFound(Vault vault, Directive directive, String filename) {
        if (vault.getName() == null) {
            String message = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                    "a " + directive.name() + " directive before a name directive";
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
    }
    
    /**
     * Java form of {@code parse_vault_name()}: gives the vault its name. C also allocates the
     * vault and links it to the one before; here {@link #assemble} creates the vault and puts it
     * in the list, so only the name is set. A second {@code name:} inside a record would simply
     * replace the first, but the grammar starts a new record at every {@code name:}.
     *
     * <p>Method parseName coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled
     * @param directive the {@code name:} directive; exactly one value
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value
     */
    private void parseName(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of name values (should be 1): ";
        checkValues(msg, directive, 1);
        
        vault.setName(directive.values().getFirst());
    }
    
    /**
     * Java form of {@code parse_vault_type()}: resolves the type text to a {@link RoomType} by the
     * exact, case-sensitive room-builder names in {@code list-rooms.h} (nineteen of them, so
     * {@code "Lesser vault"} and {@code "simple room"} both resolve, {@code "lesser vault"} does
     * not). C stores the text unchecked and compares it later, in {@code parse_vault_rows()} and
     * {@code parse_vault_columns()}; here a name that matches nothing stores {@code null} with no
     * error, and {@link #parseRows} and {@link #parseColumns} refuse the {@code null}. A later
     * {@code type:} replaces an earlier one.
     *
     * <p>Method parseType coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled
     * @param directive the {@code type:} directive; exactly one value
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value
     */
    private void parseType(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of type values (should be 1): ";
        checkValues(msg, directive, 1);
        
        RoomType roomType = switch (directive.values().getFirst()) {
            case "staircase room" -> RoomType.STAIRCASE;
            case "simple room" -> RoomType.SIMPLE;
            case "moria room" -> RoomType.MORIA;
            case "large room" -> RoomType.LARGE;
            case "crossed room" -> RoomType.CROSSED;
            case "circular room" -> RoomType.CIRCULAR;
            case "overlap room" -> RoomType.OVERLAP;
            case "room template" -> RoomType.TEMPLATE;
            case "Interesting room" -> RoomType.INTERESTING;
            case "monster pit" -> RoomType.PIT;
            case "monster nest" -> RoomType.NEST;
            case "huge room" -> RoomType.HUGE;
            case "room of chambers" -> RoomType.CHAMBERS;
            case "Lesser vault" -> RoomType.LESSER_VAULT;
            case "Medium vault" -> RoomType.MEDIUM_VAULT;
            case "Greater vault" -> RoomType.GREATER_VAULT;
            case "Lesser vault (new)" -> RoomType.NEW_LESSER_VAULT;
            case "Medium vault (new)" -> RoomType.NEW_MEDIUM_VAULT;
            case "Greater vault (new)" -> RoomType.NEW_GREATER_VAULT;
            default -> null;
        };
        vault.setType(roomType);
    }
    
    /**
     * Java form of {@code parse_vault_rating()}: reads the rating as a signed number
     * ({@code parser_getint}, so a minus is allowed) and narrows it to 0 to 255, because C stores
     * it in a {@code uint8_t}. {@code -1} loads as 255 and {@code 256} as 0.
     *
     * <p>Method parseRating coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled
     * @param directive the {@code rating:} directive; exactly one value
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value, or the text
     *                                is not a number
     */
    private void parseRating(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of rating values (should be 1): ";
        checkValues(msg, directive, 1);
        int rating = ParseUtils.parseIntString("Vault", directive.values().getFirst(), directive.line(), filename, "rating");
        rating = Math.floorMod(rating, 256);
        vault.setRating(rating);
    }
    
    /**
     * Java form of {@code parse_vault_rows()}: reads the height as a {@code uint} (a leading
     * {@code -} is refused, even {@code -0}), narrows it to 0 to 255 as C's {@code uint8_t}
     * {@code hgt} does, finds the vault's room builder and refuses a height above its
     * {@code max_height}. The cap itself is allowed, and a builder whose cap is 0 (every
     * non-vault builder except {@code "room template"} and {@code "Interesting room"}) allows only
     * 0. The checks run in C's order: the number first, then the type, then the cap. A vault with
     * no known type is C's {@code PARSE_ERROR_NO_ROOM_FOUND}, or a crash when the type was never
     * given; here both throw.
     *
     * <p>Method parseRows coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled; its type must already be set
     * @param directive the {@code rows:} directive; exactly one value
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value, the text
     *                                starts with a minus or is not a number, the vault has no known
     *                                type, or the height is above the type's cap
     */
    private void parseRows(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of row values (should be 1): ";
        checkValues(msg, directive, 1);
        
        String rowString = directive.values().getFirst();
        if (ParseUtils.minusFound(rowString)) {
            String message = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                    "an illegal negative row value: " + rowString;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        int rows = ParseUtils.parseIntString("Vault", rowString, directive.line(), filename, "rows");
        rows = Math.floorMod(rows, 256);
        
        RoomType type = vault.getType();
        if (type == null) {
            String message = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                    "a row directive before a known type directive: " + rowString;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        if (rows > type.getMaxHeight()) {
            String message = "Vault starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an illegal number of rows (should be less than " + (type.getMaxHeight() + 1) + ")";
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        
        vault.setHeight(rows);
    }
    
    /**
     * Java form of {@code parse_vault_columns()}, the same as {@link #parseRows} with the width
     * and the room builder's {@code max_width}: a {@code uint} (minus refused), narrowed to 0 to
     * 255 as C's {@code uint8_t} {@code wid} does, then checked against the type's cap. The width
     * stored here is what each later {@code D:} line is measured against.
     *
     * <p>Method parseColumns coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled; its type must already be set
     * @param directive the {@code columns:} directive; exactly one value
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value, the text
     *                                starts with a minus or is not a number, the vault has no known
     *                                type, or the width is above the type's cap
     */
    private void parseColumns(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of column values (should be 1): ";
        checkValues(msg, directive, 1);
        
        String columnString = directive.values().getFirst();

        if (ParseUtils.minusFound(columnString)) {
            String message = "Vault starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an illegal negative column value: " + columnString;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        int columns = ParseUtils.parseIntString("Vault", columnString, directive.line(), filename, "columns");
        columns = Math.floorMod(columns, 256);
        
        RoomType type = vault.getType();
        if (type == null) {
            String message = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                    "a column directive before a type directive: " + columnString;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        if (columns > type.getMaxWidth()) {
            String message = "Vault starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an illegal number of columns (should be less than " + (type.getMaxWidth() + 1) + ")";
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        
        vault.setWidth(columns);
    }
    
    /**
     * Java form of {@code parse_vault_min_depth()}: a {@code uint} (minus refused) narrowed to 0
     * to 255 as C's {@code uint8_t} {@code min_lev} does. 0 means no minimum and is kept as 0.
     *
     * <p>Method parseMinDepth coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled
     * @param directive the {@code min-depth:} directive; exactly one value
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value, or the text
     *                                starts with a minus or is not a number
     */
    private void parseMinDepth(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of min-depth values (should be 1): ";
        checkValues(msg, directive, 1);

        String minDepthStr = directive.values().getFirst();
        if (ParseUtils.minusFound(minDepthStr)) {
            String message = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                    "an illegal negative min-depth value: " + minDepthStr;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        int minLevel = ParseUtils.parseIntString("Vault", minDepthStr, directive.line(), filename, "minimum level");
        minLevel = Math.floorMod(minLevel, 256);

        vault.setMinLevel(minLevel);
    }
    
    /**
     * Java form of {@code parse_vault_max_depth()}: a {@code uint} (minus refused) where 0 means
     * "no maximum". C tests the number for zero, replaces a zero with {@code z_info->max_depth}
     * ({@link GameConstants#getWorldMaxDepth()}), and stores the result in a {@code uint8_t}
     * {@code max_lev}. The same order is followed here: zero is replaced first, then the result is
     * narrowed to 0 to 255. So {@code 0} loads as the world maximum (128 in the shipped data),
     * {@code 256} loads as 0 because it was not zero when tested, and the world maximum is itself
     * narrowed.
     *
     * <p>Method parseMaxDepth coded before 261118, commented in full on 261118 (narrowing of the
     * world maximum added 261118).
     *
     * @param vault     the vault being filled
     * @param directive the {@code max-depth:} directive; exactly one value
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value, or the text
     *                                starts with a minus or is not a number
     */
    private void parseMaxDepth(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of max-depth values (should be 1): ";
        checkValues(msg, directive, 1);

        String maxDepthStr = directive.values().getFirst();
        if (ParseUtils.minusFound(maxDepthStr)) {
            String message = "Vault starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an illegal negative maximum level value: " + maxDepthStr;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        int maxLevel = ParseUtils.parseIntString("Vault", maxDepthStr, directive.line(), filename, "maximum level");
        if (maxLevel == 0)
            maxLevel = GameConstants.getWorldMaxDepth();
        
        maxLevel = Math.floorMod(maxLevel, 256);
        
        vault.setMaxLevel(maxLevel);
    }
    
    /**
     * Java form of {@code parse_vault_flags()}: turns on each named flag in the vault's set, in
     * the order written, so repeated {@code flags:} lines and repeated names accumulate. C walks
     * the text with {@code strtok(s, " |")} and {@code grab_flag()}; here the grammar has already
     * split the list, so each name is looked up with {@link RoomFlags#lookup(String)}
     * (case-exact). A name that finds nothing is C's {@code PARSE_ERROR_INVALID_FLAG}.
     * {@code NONE} is refused too: {@link RoomFlags#lookup(String)} returns it (an accepted
     * deviation), but C's search starts after it and so cannot find it. {@code MAX} is accepted
     * and set, as C does. Flags turned on before a bad name are left on, which cannot matter as
     * the load ends.
     *
     * <p>Method parseFlag coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled
     * @param directive the {@code flags:} directive (named {@code flag} by the grammar); its values
     *                  are the flag names
     * @param filename  the data file, for the message
     * @throws DataFileParseException if a name is not a room flag, or is {@code NONE}
     */
    private void parseFlag(Vault vault, Directive directive, String filename) {
        List<String> flagList = directive.values();
        Flag<RoomFlags> flags = vault.getFlags();
        
        for (String flag : flagList) {
            RoomFlags roomFlag = RoomFlags.lookup(flag);
            if (roomFlag == null) {
                String message = "Vault starting at line: " + directive.line() + " of file: " + filename + " has " +
                        "an unknown room flag value: " + flag;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            if (roomFlag == RoomFlags.NONE) {
                String message = "Vault starting at line: " + directive.line() + " of file: " + filename + " has " +
                        "an illegal sentinel room flag value: " + flag;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            
            flags.on(roomFlag);
        }
        
        vault.setFlags(flags);
    }
    
    /**
     * Java form of {@code parse_vault_d()}: checks one {@code D:} line against the vault's width as
     * it stands now and appends it. A line whose length differs is C's
     * {@code PARSE_ERROR_VAULT_DESC_WRONG_LENGTH}, so a {@code D:} line before {@code columns:} is
     * compared with 0. The line is added to the map list and to the flat text
     * ({@code v->text}, which C builds with {@code string_append}, starting from {@code NULL}).
     * The number of lines is never compared with the height, as in C.
     *
     * <p>Method parseMap coded before 261118, commented in full on 261118.
     *
     * @param vault     the vault being filled
     * @param directive the {@code D:} directive (named {@code map} by the grammar); exactly one
     *                  value, the row text with its padding
     * @param filename  the data file, for the message
     * @throws DataFileParseException if the directive does not have exactly one value, or the line
     *                                is not as long as the vault's width
     */
    private void parseMap(Vault vault, Directive directive, String filename) {
        String msg = "Vault at line: " + directive.line() + " of file: " + filename + " has " +
                "an incorrect number of map values (should be 1): ";
        checkValues(msg, directive, 1);
        
        String mapLine = directive.values().getFirst();
        List<String> currentRoomMap = vault.getMap();
        String currentRoomMapString = vault.getMapLines();
        if (currentRoomMapString == null) currentRoomMapString = "";
        int vaultWidth = vault.getWidth();
        
        if (mapLine.length() != vaultWidth) {
            String message = "Vault starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "a map line of a different length than expected. Line: " + mapLine + " expected length: " + vaultWidth;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        
        currentRoomMap.add(mapLine);
        currentRoomMapString = currentRoomMapString + mapLine;
        vault.setMapLines(currentRoomMapString);
        vault.setMap(currentRoomMap);
    }
    
    /**
     * Refuses a directive whose number of values is not the one expected. C's parser would have
     * refused the line itself ({@code parser_reg()} fixes each line's fields); here the grammar
     * hands over a list, so the count is checked. The message is the caller's text followed by the
     * values found, comma-separated.
     *
     * <p>Method checkValues coded before 261118, commented in full on 261118.
     *
     * @param msg          the message start, naming the vault line, file and directive
     * @param directive    the directive whose values are counted
     * @param correctValue how many values the directive must have
     * @throws DataFileParseException if the count differs
     */
    private void checkValues(String msg, Directive directive, int correctValue) {
        if (directive.values().size() != correctValue) {
            StringBuilder names = new StringBuilder();
            for (String name : directive.values()) {
                names.append(name);
                names.append(", ");
            }

            if (!names.isEmpty()) names = new StringBuilder(names.substring(0, names.length() - 2));

            msg = msg + names;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
    }
}
