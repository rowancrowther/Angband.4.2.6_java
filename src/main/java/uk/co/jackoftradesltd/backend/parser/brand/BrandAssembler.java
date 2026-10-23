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

package uk.co.jackoftradesltd.backend.parser.brand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.Brand;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns each {@link BrandParseRecord} parsed from {@code brand.txt} into a {@link Brand}, resolving
 * the fields the grammar left as text: the resist and vulnerability monster race flags, and the
 * multiplier, o-multiplier and power. This is the Java port of the record handlers in [C]
 * {@code obj-init.c}, {@code parse_brand_code} through {@code parse_brand_vuln_flag}, gathered into
 * one place by the suite's "grammars extract, assemblers resolve" split.
 *
 * <p><strong>The two flags are independent.</strong> Unlike a slay's race flag and base, which
 * exclude each other, a brand may carry a resist flag, a vuln flag, or both:
 * {@code parse_brand_resist_flag} and {@code parse_brand_vuln_flag} make no cross-check. Every
 * shipped brand has a {@code resist-flag}; {@code vuln-flag} is optional, and when absent is left
 * as {@link MonsterRaceFlag#RF_NONE}, the 0 that C's {@code mem_zalloc} leaves in
 * {@code vuln_flag}.
 *
 * <p><strong>The name is stored verbatim.</strong> A brand's {@code name} is a display word
 * ({@code lightning}, {@code poison}) that differs from the element in its code ({@code ELEC},
 * {@code POIS}); C never checks it against the element list, so neither does this.
 *
 * <p><strong>Every error ends the load.</strong> As C's handlers return
 * {@code PARSE_ERROR_INVALID_FLAG} or {@code PARSE_ERROR_NOT_NUMBER} and stop the whole
 * {@code brand.txt} load, the bad value is logged and thrown as a {@link DataFileParseException}
 * rather than reported as a soft error, and nothing is added to the caller's error list.
 *
 * <p>Class BrandAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class BrandAssembler implements Assembler<BrandParseRecord, List<Brand>> {
    /**
     * The logger this assembler writes to: a fatal message, with the same text, just before each
     * {@link DataFileParseException} it throws.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(BrandAssembler.class);

    /**
     * Assembles the parsed brand records into {@link Brand}s, one per record, and returns them in
     * <em>reverse</em> file order. C's {@code parse_brand_code} pushes each new brand onto the head
     * of its list and {@code finish_parse_brand} copies that list into {@code brands[]} from the
     * head, so the last record in {@code brand.txt} becomes {@code brands[1]}; the grammar hands
     * the records over in file order, so each is added with {@code addFirst}. Consumers that keep
     * the first of several brands with one name, such as {@code Rune.initRunes}, depend on this.
     *
     * <p>The two flags are resolved as C's {@code lookup_flag} ({@code datafile.c}) resolves them
     * against the {@code mon_race_flags[]} table built from {@code list-mon-race-flags.h}:
     * <ul>
     *   <li><strong>A listed flag</strong> such as {@code IM_FIRE} or {@code HURT_FIRE} becomes
     *       {@code RF_IM_FIRE} or {@code RF_HURT_FIRE}. The match is case-sensitive in both, as C
     *       uses {@code streq} and {@code valueOf} is exact.</li>
     *   <li><strong>{@code NONE}</strong> ends the load. C's table does hold it, at index 0, but
     *       {@code lookup_flag} starts its search at {@code FLAG_START} (1) and returns
     *       {@code FLAG_END}, which both handlers reject. {@code RF_NONE} exists in the Java enum,
     *       so it is refused here by name.</li>
     *   <li><strong>Any other flag</strong> ends the load, through the
     *       {@link IllegalArgumentException} that {@code valueOf} throws. {@code MAX} is among
     *       them: C's table has no such row, and {@link MonsterRaceFlag} has no {@code RF_MAX}.</li>
     * </ul>
     *
     * <p>The three numbers are registered as {@code uint} in C's {@code init_parse_brand}, so
     * {@code parser.c} reads them with {@code strtoul} in base 0 and ends the load with
     * {@code PARSE_ERROR_NOT_NUMBER} on text that starts with {@code -}. The same reading is done
     * here by {@link ParseUtils#parseIntString(String, String, int, String, String)}, with a
     * leading {@code 0} read as octal ({@code multiplier:010} is 8). A leading {@code -} is refused
     * on the raw text through {@link ParseUtils#minusFound}, not on the value, so {@code -0}
     * ends the load as it does in C. A value too big for an {@code int} is clamped to
     * {@link Integer#MAX_VALUE} where C wraps it, the accepted deviation recorded in
     * {@link ParseUtils}.
     *
     * <p>Function assemble coded before 260915, commented in full on 261013, rewritten for the
     * load-ending errors on 261023.
     *
     * @param records  the raw {@link BrandParseRecord}s, in file order
     * @param errors   unused: every error ends the load by throwing instead
     * @param filename the name of the data file, used in the error messages
     * @return the assembled brands, in reverse file order (C's {@code brands[]} order)
     * @throws DataFileParseException on a flag of {@code NONE} or one that names no
     *                                {@link MonsterRaceFlag} member, or a number with a leading
     *                                {@code -}
     */
    @Override
    public List<Brand> assemble(@NotNull List<BrandParseRecord> records, @NotNull List<String> errors,
                                String filename) {
        List<Brand> brands = new ArrayList<>();

        for (BrandParseRecord record : records) {
            int line = record.line();
            String code = record.code();
            String name = record.name();
            String verb = record.verb();
            // resist-flag is present on every brand, so resolve it unconditionally.
            String rawRFlag = record.resistFlag();
            MonsterRaceFlag rFlag;
            if (!record.resistFlag().equalsIgnoreCase("none")) {
                try {
                    rFlag = MonsterRaceFlag.valueOf("RF_" + rawRFlag);
                } catch (IllegalArgumentException e) {
                    String message = "Brand starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid resist flag value: " + record.resistFlag();
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            } else {
                String message = "Brand starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid resist flag value: " + record.resistFlag();
                logger.fatal(message);
                throw new DataFileParseException(message);                
            }
            // vuln-flag is optional; absent leaves the RF_NONE sentinel, present must resolve.
            String rawVFlag = record.vulnFlag();
            
            MonsterRaceFlag vFlag = MonsterRaceFlag.RF_NONE;
            if (!rawVFlag.isEmpty()) {
                if (!rawVFlag.equalsIgnoreCase("NONE")) {
                    try {
                        vFlag = MonsterRaceFlag.valueOf("RF_" + rawVFlag);
                    } catch (IllegalArgumentException e) {
                        String message = "Brand starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid vulnerable flag value: " + record.vulnFlag();
                        logger.fatal(message, e);
                        throw new DataFileParseException(message);
                    }
                } else {
                    String message = "Brand starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid vulnerable flag value: " + record.vulnFlag();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            // multiplier / o-multiplier / power are C uints: a too-large value is clamped, and a
            // leading '-' (even on -0) ends the load, as strtoul's caller in parser.c refuses it.
            int mult = ParseUtils.parseIntString("Brand", record.multiplier(), line, filename, "multiplier");
            if (ParseUtils.minusFound(record.multiplier())) {
                String message = "Brand starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid multiplier value: " + record.multiplier();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int oMult = ParseUtils.parseIntString("Brand", record.oMultiplier(), line, filename, "o-multiplier");
            if (ParseUtils.minusFound(record.oMultiplier())) {
                String message = "Brand starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid o-multiplier value: " + record.oMultiplier();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int power = ParseUtils.parseIntString("Brand", record.power(), line, filename, "power");
            if (ParseUtils.minusFound(record.power())) {
                String message = "Brand starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid power value: " + record.power();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }

            brands.addFirst(new Brand(code, name, verb, rFlag, vFlag, mult, oMult, power));
        }

        return brands;
    }
}
