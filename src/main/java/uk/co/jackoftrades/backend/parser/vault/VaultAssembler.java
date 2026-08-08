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

package uk.co.jackoftrades.backend.parser.vault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.RoomFlags;
import uk.co.jackoftrades.middle.cave.profiles.vault.Vault;
import uk.co.jackoftrades.middle.cave.roombuilders.RoomType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw, text-typed {@link VaultParseRecord}s from {@code vault.txt} into typed
 * {@link Vault}s — the port of the validation C spreads across {@code parse_vault_type}/
 * {@code parse_vault_rating}/{@code parse_vault_rows}/{@code parse_vault_columns}/
 * {@code parse_vault_min_depth}/{@code parse_vault_max_depth}/{@code parse_vault_flags}/
 * {@code parse_vault_d} in {@code generate.c} ([C] src/generate.c:479-593).
 *
 * <p>Follows the partial-results contract every {@link Assembler} does: a record with a problem is
 * skipped (via {@code continue}) rather than aborting the whole file, and the problem is appended
 * to {@code errors} so the caller can report it. Every other record still assembles. C by contrast
 * treats each of these as a fatal parse error, so a bad line there stops the game rather than
 * costing one vault.
 *
 * <p>Runs after {@code GameConstants}, because {@code max-depth:0} means "no maximum" and turning
 * that into a real number needs the world's maximum depth.
 *
 * @author Rowan Crowther
 */
public class VaultAssembler implements Assembler <VaultParseRecord, List<Vault>> {
    private static final Logger logger = LogManager.getLogger(VaultAssembler.class);

    /**
     * Validate and convert every record, skipping (and reporting) any that don't resolve cleanly.
     *
     * @param records the raw parse records to assemble
     * @param errors  soft-error sink; one message is appended per record that gets skipped
     * @return the successfully assembled vaults, in file order
     * @author Rowan Crowther
     */
    @Override
    public List<Vault> assemble(@NotNull List<VaultParseRecord> records, @NotNull List<String> errors) {
        List<Vault> vaults = new ArrayList<>();

        for (VaultParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String typeString = record.type();
            RoomType type;
            // The data-file names don't transform mechanically into the enum's constant names:
            // "Interesting room" drops a word to reach INTERESTING, and the three "(new)" types
            // move the qualifier to the front (NEW_LESSER_VAULT). Listing the seven names the
            // file may legally use is both the honest mapping and a check that no eighth appears.
            // C does the equivalent by streq against room_builders[i].name ([C] src/generate.c:517)
            // and treats no match as PARSE_ERROR_NO_ROOM_FOUND.
            switch (typeString.toUpperCase()) {
                case "LESSER VAULT" -> type = RoomType.LESSER_VAULT;
                case "MEDIUM VAULT" -> type = RoomType.MEDIUM_VAULT;
                case "GREATER VAULT" -> type = RoomType.GREATER_VAULT;
                case "INTERESTING ROOM" -> type = RoomType.INTERESTING;
                case "LESSER VAULT (NEW)" -> type = RoomType.NEW_LESSER_VAULT;
                case "MEDIUM VAULT (NEW)" -> type = RoomType.NEW_MEDIUM_VAULT;
                case "GREATER VAULT (NEW)" -> type = RoomType.NEW_GREATER_VAULT;
                default -> type = null;
            }
            if (type == null) {
                errors.add("Vault at line: " + line + " has " +
                        "an invalid vault type: " + typeString);
                continue;
            }
            int rows = 0;
            try {
                rows = Integer.parseInt(record.rows());
            } catch (NumberFormatException e) {
                errors.add("Vault profile at line: " + line + " has " +
                        "an invalid row integer: " + record.rows());
                continue;
            }
            // Mirrors C's parse_vault_rows, which rejects a vault taller than its own room
            // builder's max_height (PARSE_ERROR_VAULT_TOO_BIG, [C] src/generate.c:521). The cap is
            // per type, not global — a lesser vault gets 22 rows where a greater vault gets 44 —
            // which is why this has to run after the type has resolved.
            if (rows <= 0 || rows > type.getMaxHeight()) {
                errors.add("Vault profile at line: " + line + " has " +
                        "an invalid row value (should be between 1 " +
                        "and " + type.getMaxHeight() + ")");
                continue;
            }
            int columns = 0;
            try {
                columns = Integer.parseInt(record.cols());
            } catch (NumberFormatException e) {
                errors.add("Vault profile at line: " + line + " has " +
                        "an invalid column integer: " + record.cols());
                continue;
            }
            // As above, for parse_vault_columns ([C] src/generate.c:540).
            if (columns <= 0 || columns > type.getMaxWidth()) {
                errors.add("Vault profile at line: " + line + " has " +
                        "an invalid column value (should be between 1 " +
                        "and " + type.getMaxWidth() + ")");
                continue;
            }
            // The layout has to match what the record declared, or every later read of it is
            // wrong: the flat mapText is indexed as text[y * columns + x], so a single row of the
            // wrong length shifts the whole vault. C enforces the width line by line as it parses
            // (strlen(desc) != v->wid, [C] src/generate.c:588) and never checks the row count at
            // all; checking both here costs nothing and catches a truncated record too.
            boolean badMap = false;
            List<String> mapLines = record.map();
            for (String mapLine : mapLines) {
                if (mapLine.length() != columns) {
                    badMap = true;
                }
            }
            if (mapLines.size() != rows) badMap = true;
            if (badMap) {
                errors.add("Vault profile at line: " + line + " has " +
                        "a bad map - either the rows or the columns do not " +
                        "match the declared values.");
                continue;
            }
            String mapText = record.mapLines();
            int rating = 0;
            try {
                rating = Integer.parseInt(record.rating());
            } catch (NumberFormatException e) {
                errors.add("Vault profile at line: " + line + " has " +
                        "an invalid rating integer: " + record.rating());
                continue;
            }
            int minLevel = 0;
            try {
                minLevel = Integer.parseInt(record.minLevel());
            } catch (NumberFormatException e) {
                errors.add("Vault profile at line: " + line + " has " +
                        "an invalid minimum level integer: " + record.minLevel());
                continue;
            }
            int maxLevel = 0;
            try {
                maxLevel = Integer.parseInt(record.maxLevel());
            } catch (NumberFormatException e) {
                errors.add("Vault profile at line: " + line + " has " +
                        "an invalid maximum level integer: " + record.maxLevel());
                continue;
            }
            // max-depth:0 in the file means "no maximum", not "never appears". C rewrites it to
            // z_info->max_depth while parsing ([C] src/generate.c:561), so nothing downstream ever
            // sees a zero and no caller needs to special-case it; 129 of the 162 shipped records
            // rely on this. min-depth:0 needs no equivalent - zero is already the right floor.
            if (maxLevel == 0)
                maxLevel = GameConstants.getWorldMaxDepth();
            // A record may carry several flags: lines, each OR'd into the same set - the file's
            // own header says so, and C appends rather than replaces ([C] src/generate.c:565).
            // The grammar therefore hands over one flat list however many lines produced it.
            boolean badFlag = false;
            Flag<RoomFlags> flags = new Flag<>(RoomFlags.class);
            for (String flag : record.flags()) {
                try {
                    flags.on(RoomFlags.valueOf(flag));
                } catch (IllegalArgumentException e) {
                    errors.add("Vault at line: " + line + " has " +
                            "an invalid flag: " + flag);
                    badFlag = true;
                }
            }
            // Deliberately flagged rather than skipped inside the loop, so a record with three bad
            // flags reports all three in one run instead of one per re-run.
            if (badFlag) continue;
            
            vaults.add(new Vault(name, type, mapText, mapLines, flags, rating,
                    rows, columns, minLevel, maxLevel));
        }
        
        return vaults;
    }
}
