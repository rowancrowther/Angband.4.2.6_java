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

package uk.co.jackoftrades.backend.parser.slay;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Slay;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link SlayParseRecord}s from {@code SlayGrammar} into domain
 * {@link Slay} objects, resolving the fields the grammar left as strings: the
 * target monster race flag or monster base, and the numeric multipliers and
 * power. This is the Java analogue of the {@code parse_slay_*} handlers in
 * {@code src/obj-init.c}, gathered into one place per the suite's
 * "grammars extract, assemblers resolve" split.
 * <p>
 * <strong>Race flag and base are mutually exclusive.</strong> A slay targets
 * exactly one of a race flag or a monster base (mirroring C's
 * {@code PARSE_ERROR_INVALID_SLAY} guard in {@code parse_slay_race_flag}/
 * {@code parse_slay_base}). A record naming both - or neither - is rejected;
 * otherwise only the populated one is resolved and the other is left
 * {@code null} on the {@link Slay}.
 * <p>
 * <strong>Errors are soft and per-record.</strong> Any unresolved field appends
 * a message to {@code errors} and skips just that record ({@code continue}), so
 * one malformed slay never aborts the rest of the file - the partial-results
 * contract the {@code GrammarDriver} pipeline relies on.
 *
 * @author Rowan Crowther
 */
public class SlayAssembler implements Assembler<SlayParseRecord, List<Slay>> {
    /**
     * Resolve each parse record into a {@link Slay}, skipping (with a soft error)
     * any record whose race-flag/base choice is invalid or whose numeric fields
     * do not parse.
     *
     * @param records the raw slay records from the parser
     * @param errors  the soft-error sink, appended to in place; never thrown
     * @return the successfully assembled slays, in file order
     */
    @Override
    public List<Slay> assemble(@NotNull List<SlayParseRecord> records, @NotNull List<String> errors) {
        List<Slay> slays = new ArrayList<>();

        for (SlayParseRecord record : records) {
            int line = record.line();
            String code = record.code();
            String name = record.name();
            String rawRaceFlag = record.raceFlag();
            String rawBase = record.base();
            // A slay targets exactly one of a race flag or a base; naming both is invalid.
            if (!rawRaceFlag.isEmpty() && !rawBase.isEmpty()) {
                errors.add("Block beginning line: " + line + " has both " +
                        "race flag and base");
                continue;
            }
            // Resolve whichever one is present, leaving the other null; neither is meaningless.
            MonsterRaceFlag monsterRaceFlag = null;
            MonsterBase monsterBase = null;
            if (!rawRaceFlag.isEmpty()) {
                try {
                    monsterRaceFlag = MonsterRaceFlag.valueOf("RF_" + rawRaceFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Block beginning line: " + line + " has " +
                            "unknown monster race flag: " + rawRaceFlag);
                    continue;
                }
            } else if (!rawBase.isEmpty()) {
                monsterBase = GameConstants.getMonsterBase(rawBase);
                if (monsterBase == null) {
                    errors.add("Block beginning line: " + line + " has unknown monster base");
                    continue;
                }
            } else {
                errors.add("Block beginning line: " + line + " has neither " +
                        "monster base nor monster race flag. Ignoring.");
                continue;
            }
            // multiplier / o-multiplier / power are INTEGER tokens, so parseInt can only fail on a
            // value too large for int.
            int multiplier = 0;
            try {
                multiplier = Integer.parseInt(record.mult());
            } catch (NumberFormatException e) {
                errors.add("Block beginning line: " + line + " has" +
                        " invalid integer format for multiplier: " + record.mult());
                continue;
            }
            int oMult = 0;
            try {
                oMult = Integer.parseInt(record.oMult());
            } catch (NumberFormatException e) {
                errors.add("Block beginning line: " + line + " has " +
                        "invalid integer format for oMult: " + record.oMult());
                continue;
            }
            int power = 0;
            try {
                power = Integer.parseInt(record.power());
            } catch (NumberFormatException e) {
                errors.add("Block beginning line: " + line + " has " +
                        "invalid integer format for power: " + record.power());
                continue;
            }
            String meleeVerb = record.meleeVerb();
            String rangeVerb = record.rangeVerb();

            slays.add(new Slay(code, name, monsterBase, meleeVerb, rangeVerb,
                    monsterRaceFlag, multiplier, oMult, power));
        }

        return slays;
    }
}
