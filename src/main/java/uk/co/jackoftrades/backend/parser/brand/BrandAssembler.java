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

package uk.co.jackoftrades.backend.parser.brand;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Brand;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link BrandParseRecord}s from {@code BrandGrammar} into domain
 * {@link Brand} objects, resolving the fields the grammar left as strings: the
 * resist and vulnerability monster race flags, and the numeric multipliers and
 * power. This is the Java analogue of the {@code parse_brand_*} handlers in
 * {@code src/obj-init.c}, gathered into one place per the suite's
 * "grammars extract, assemblers resolve" split.
 * <p>
 * <strong>The two flags are independent.</strong> Unlike Slay's race-flag/base
 * exclusivity, a brand may carry a resist flag, a vuln flag, or both (C's
 * {@code parse_brand_resist_flag}/{@code parse_brand_vuln_flag} apply no
 * cross-check). {@code resist-flag} is present on every shipped brand and is
 * resolved unconditionally; {@code vuln-flag} is optional, so it is resolved only
 * when present and otherwise left as the {@link MonsterRaceFlag#RF_NONE} sentinel.
 * <p>
 * <strong>The name is stored verbatim.</strong> A brand's {@code name} is a
 * display word (e.g. {@code lightning}, {@code poison}) that deliberately differs
 * from the element code ({@code ELEC}, {@code POIS}); C never validates it
 * against the element list, so neither do we.
 * <p>
 * <strong>Errors are soft and per-record.</strong> Any unresolved field appends
 * a message to {@code errors} and skips just that record ({@code continue}), so
 * one malformed brand never aborts the rest of the file.
 *
 * @author Rowan Crowther
 */
public class BrandAssembler implements Assembler<BrandParseRecord, List<Brand>> {
    /**
     * Resolve each parse record into a {@link Brand}, skipping (with a soft
     * error) any record whose flags or numeric fields do not resolve.
     *
     * @param records the raw brand records from the parser
     * @param errors  the soft-error sink, appended to in place; never thrown
     * @return the successfully assembled brands, in file order
     */
    @Override
    public List<Brand> assemble(@NotNull List<BrandParseRecord> records, @NotNull List<String> errors) {
        List<Brand> brands = new ArrayList<>();

        for (BrandParseRecord record : records) {
            int line = record.line();
            String code = record.code();
            String name = record.name();
            String verb = record.verb();
            // resist-flag is present on every brand, so resolve it unconditionally.
            String rawRFlag = record.resistFlag();
            MonsterRaceFlag rFlag;
            try {
                rFlag = MonsterRaceFlag.valueOf("RF_" + rawRFlag);
            } catch (IllegalArgumentException e) {
                errors.add("Block starting at line: " + line + " has " +
                        "an illegal resist flag: " + rawRFlag);
                continue;
            }
            // vuln-flag is optional; absent leaves the RF_NONE sentinel, present must resolve.
            String rawVFlag = record.vulnFlag();
            MonsterRaceFlag vFlag = MonsterRaceFlag.RF_NONE;
            if (!rawVFlag.isEmpty()) {
                try {
                    vFlag = MonsterRaceFlag.valueOf("RF_" + rawVFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Block starting at line: " + line + " has " +
                            "an illegal vulnerable flag: " + rawVFlag);
                    continue;
                }
            }
            // multiplier / o-multiplier / power are INTEGER tokens, so parseInt can only fail on a
            // value too large for int.
            int mult = 0;
            try {
                mult = Integer.parseInt(record.multiplier());
            } catch (NumberFormatException e) {
                errors.add("Block starting at line: " + line + " has " +
                        "an invalid multiplier: " + record.multiplier());
                continue;
            }
            int oMult = 0;
            try {
                oMult = Integer.parseInt(record.oMultiplier());
            } catch (NumberFormatException e) {
                errors.add("Block starting at line: " + line + " has " +
                        "an invalid o-multiplier: " + record.oMultiplier());
                continue;
            }
            int power = 0;
            try {
                power = Integer.parseInt(record.power());
            } catch (NumberFormatException e) {
                errors.add("Block starting at line: " + line + " has " +
                        "an invalid power: " + record.power());
                continue;
            }

            brands.add(new Brand(code, name, verb, rFlag, vFlag, mult, oMult, power));
        }

        return brands;
    }
}
