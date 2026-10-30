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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
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
 *       {@code PARSE_ERROR_NO_ROOM_FOUND};</li>
 *   <li>{@code rows:} and {@code columns:} are C {@code uint}s: a leading {@code -} is refused,
 *       as C's {@code PARSE_ERROR_NOT_NUMBER}, using {@link ParseUtils#minusFound(String)}, and the
 *       rest is read through {@link ParseUtils#parseIntString(String, String, int, String, String)}.
 *       Each is capped by the type's own {@code max_height} or {@code max_width}, which is itself
 *       allowed ({@code PARSE_ERROR_VAULT_TOO_BIG} only above it). There is no lower bound, so zero
 *       loads as it does in C;</li>
 *   <li>every {@code D:} line must be exactly {@code columns} characters long, C's
 *       {@code PARSE_ERROR_VAULT_DESC_WRONG_LENGTH}. As in C, the number of {@code D:} lines is
 *       never compared with {@code rows}. The lines are kept as a list and also joined into the
 *       flat text C keeps in {@code v->text};</li>
 *   <li>{@code rating:} is a signed {@code int} in C, so a negative rating loads;</li>
 *   <li>{@code min-depth:} and {@code max-depth:} are {@code uint}s and refuse a minus as rows and
 *       columns do. {@code max-depth:0} means "no maximum", and becomes
 *       {@link GameConstants#getWorldMaxDepth()} ({@code z_info->max_depth}), as C rewrites it;</li>
 *   <li>{@code flags:} resolve to {@link RoomFlags}, matched case-exactly as C's
 *       {@code lookup_flag()} matches with {@code streq}. {@code NONE} is refused, because C's
 *       {@code room_flags} table holds it at index 0, which {@code lookup_flag()} never reaches from
 *       {@code FLAG_START} (1). {@code MAX} is accepted and set, because C's table holds
 *       {@code "MAX"} from {@code list-room-flags.h} and {@code grab_flag()} turns it on.</li>
 * </ul>
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
 * {@link DataFileParseException}, so no record is dropped. The {@code errors} list is not written.
 * Hard grammar and lexer errors are handled upstream.
 *
 * <p>Runs after {@code GameConstants}, because turning {@code max-depth:0} into a real number
 * needs the world's maximum depth.
 *
 * <p>Class VaultAssembler coded before 260915, commented in full on 261030 (first commented
 * 260915; rewritten 261030 once soft errors became fatal, every room builder name was accepted
 * and the {@code uint} minus checks were added).
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
     * {@code parse_vault_*} handlers in {@code generate.c} filling each {@code struct vault}. The
     * checks run in this order for each record: type, rows, columns, the width of each
     * {@code D:} line, rating, minimum depth, maximum depth, then flags. C checks the type only
     * when it reaches {@code rows:} or {@code columns:}, but the grammar requires those lines, so
     * an unknown type fails in both languages. The first failure throws, so a file loads whole or
     * not at all, as in C.
     *
     * <p>Function assemble coded before 260915, commented in full on 261030 (first commented
     * 260915; rewritten 261030 for the fatal errors and the C-exact type names).
     *
     * @param records  the raw parse records to assemble
     * @param errors   unused; formerly the soft-error channel
     * @param filename the data file the records came from, for error messages
     * @return the assembled vaults, in reverse file order, as C's linked list holds them
     * @throws DataFileParseException if the type is not a room builder name, rows, columns or a
     *                                depth starts with a minus, rows or columns exceed the type's
     *                                limit, a {@code D:} line is not {@code columns} long, a number
     *                                cannot be read, or a flag is unknown or {@code NONE}
     */
    @Override
    public List<Vault> assemble(@NotNull List<VaultParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<Vault> vaults = new ArrayList<>();

        for (VaultParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            // The names are C's room_builders names from list-rooms.h, case and all, because C
            // matches them with streq in parse_vault_rows and parse_vault_columns
            // ([C] src/generate.c) and treats no match as PARSE_ERROR_NO_ROOM_FOUND. All nineteen
            // builders are listed, not only the vaults, because C accepts any of them.
            RoomType type = switch (record.type()) {
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
            if (type == null) {
                String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal vault type value: " + record.type();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            if (ParseUtils.minusFound(record.rows())) {
                String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative row value: " + record.rows();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int rows = ParseUtils.parseIntString("Vault", record.rows(), line, filename, "rows");
            // Mirrors C's parse_vault_rows, which rejects a vault taller than its own room
            // builder's max_height (PARSE_ERROR_VAULT_TOO_BIG, [C] src/generate.c). The cap is
            // per type, not global — a lesser vault gets 22 rows where a greater vault gets 44 —
            // which is why this has to run after the type has resolved.
            if (rows > type.getMaxHeight()) {
                String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal number of rows (should be less than " + (type.getMaxHeight() + 1) + ")";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            if (ParseUtils.minusFound(record.cols())) {
                String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative column value: " + record.cols();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int columns = ParseUtils.parseIntString("Vault", record.cols(), line, filename, "columns");
            // As above, for parse_vault_columns ([C] src/generate.c).
            if (columns > type.getMaxWidth()) {
                String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal number of columns (should be less than " + (type.getMaxWidth() + 1) + ")";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            // The layout has to match what the record declared, or every later read of it is
            // wrong: the flat mapText is indexed as text[y * columns + x], so a single row of the
            // wrong length shifts the whole vault. C enforces the width line by line as it parses
            // (strlen(desc) != v->wid, parse_vault_d, [C] src/generate.c) and never checks the
            // number of lines against rows, so neither does this.
            StringBuilder map = new StringBuilder();
            List<String> mapLines = record.map();
            for (String mapLine : record.map()) {
                map.append(mapLine);
                if (mapLine.length() != columns) {
                    String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal number of map columns (should be " + record.cols() + " was " +
                            mapLine.length() + ")";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            String mapText = map.toString();
            
            int rating = ParseUtils.parseIntString("Vault", record.rating(), line, filename, "rating");
            if (ParseUtils.minusFound(record.minLevel())) {
                String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative minimum level value: " + record.minLevel();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int minLevel = ParseUtils.parseIntString("Vault", record.minLevel(), line, filename, "minimum level");
            if (ParseUtils.minusFound(record.maxLevel())) {
                String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative maximum level value: " + record.maxLevel();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int maxLevel = ParseUtils.parseIntString("Vault", record.maxLevel(), line, filename, "maximum level");
            // max-depth:0 in the file means "no maximum", not "never appears". C rewrites it to
            // z_info->max_depth while parsing ([C] src/generate.c), so nothing downstream ever
            // sees a zero and no caller needs to special-case it; 129 of the 162 shipped records
            // rely on this. min-depth:0 needs no equivalent - zero is already the right floor.
            if (maxLevel == 0)
                maxLevel = GameConstants.getWorldMaxDepth();
            // A record may carry several flags: lines, each OR'd into the same set - the file's
            // own header says so, and C appends rather than replaces ([C] src/generate.c).
            // The grammar therefore hands over one flat list however many lines produced it.
            Flag<RoomFlags> flags = new Flag<>(RoomFlags.class);
            for (String flag : record.flags()) {
                try {
                    RoomFlags roomFlag = RoomFlags.valueOf(flag);
                    if (roomFlag == RoomFlags.NONE) {
                        String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel room flag value: " + flag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    flags.on(RoomFlags.valueOf(flag));
                } catch (IllegalArgumentException e) {
                    String message = "Vault starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal room flag value: " + flag;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            
            vaults.addFirst(new Vault(name, type, mapText, mapLines, flags, rating,
                    rows, columns, minLevel, maxLevel));
        }
        
        return vaults;
    }
}
