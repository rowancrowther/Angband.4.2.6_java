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

package uk.co.jackoftradesltd.backend.parser.slay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.Slay;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link SlayParseRecord}s from {@code SlayGrammar} into domain
 * {@link Slay} objects, resolving the fields the grammar left as strings: the
 * target monster race flag or monster base, and the numeric multipliers and
 * power. This is the Java analogue of the {@code parse_slay_*} handlers in
 * {@code src/obj-init.c}, gathered into one place per the suite's
 * "grammars extract, assemblers resolve" split. The counting, the 254-entry
 * limit and the copy into {@code slays[]} in {@code finish_parse_slay} are not
 * done here: the list order is, and the rest is recorded on
 * {@code ObjectDataLoader.loadSlays}.
 *
 * <p><strong>A slay targets a race flag, a base, or neither.</strong> C refuses
 * a second target on one slay: {@code parse_slay_race_flag} and
 * {@code parse_slay_base} return {@code PARSE_ERROR_INVALID_SLAY} when
 * {@code race_flag} or {@code base} is already set. A record naming both is
 * therefore refused. A record naming neither is not: no handler and no check in
 * {@code finish_parse_slay} requires one, so it loads with both left
 * {@code null} on the {@link Slay}. Only the populated one is resolved.
 *
 * <p><strong>Every fault stops the load.</strong> C's first parser error ends
 * the whole file, and so does this: a fault is logged as fatal and thrown as a
 * {@link DataFileParseException}, no later record is read, and nothing is added
 * to the {@code errors} list. The faults are an unknown race flag (a miss, or
 * {@code NONE}, which {@code lookup_flag} in {@code datafile.c} never matches
 * because it starts at {@code FLAG_START}), an unknown monster base, both a
 * race flag and a base, a leading {@code -} on a number, and a number with no
 * digits.
 *
 * <p><strong>Absent directives.</strong> C allocates each slay zeroed, so a
 * directive left out keeps its zero: the three numbers load as 0, and
 * {@code name}, {@code melee-verb} and {@code range-verb} stay {@code NULL}.
 * The grammar hands over empty text for a directive it did not see, and the
 * assembler turns empty text into 0 for a number and into {@code null} for the
 * three strings.
 *
 * <p><strong>Accepted deviation (2026-10-29): {@code base:} is resolved.</strong>
 * In {@code init.c} the {@code slays} parser runs before {@code monster bases},
 * so in C the monster base list is still empty when {@code slay.txt} is read
 * and every {@code base:} line is refused with
 * {@code PARSE_ERROR_INVALID_MONSTER_BASE}. Java loads the monster bases first,
 * so a name that matches one resolves and the slay loads. No shipped slay uses
 * {@code base:}. See "Slay" in {@code docs/Accepted_Deviations.md}.
 *
 * <p>Class SlayAssembler coded before 260915, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class SlayAssembler implements Assembler<SlayParseRecord, List<Slay>> {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(SlayAssembler.class);

    /**
     * Resolve each parse record into a {@link Slay}, in the order C's {@code parse_slay_*} handlers
     * (and {@code finish_parse_slay}, {@code obj-init.c}) would leave them in {@code slays[]}. The
     * first fault ends the load: it is logged as fatal and thrown, and no later record is read.
     *
     * <p>Per record:
     * <ul>
     *   <li>{@code code} is stored as written and never taken apart, as {@code parse_slay_code} does.</li>
     *   <li>{@code name}, {@code melee-verb} and {@code range-verb} are stored as written, and empty
     *       text (the directive was absent) becomes {@code null}, as the zeroed record leaves
     *       {@code NULL}.</li>
     *   <li>The race flag is matched in exact case against the {@code RF_} constants, as
     *       {@code lookup_flag} matches with {@code streq}. An unknown name and {@code NONE} are
     *       refused. {@code MAX} has no constant, so it is refused as an unknown name.</li>
     *   <li>The base is matched in exact case against the loaded monster bases, and an unknown name
     *       is refused. C refuses every base (see the class block); here a matching one resolves.</li>
     *   <li>A race flag and a base together are refused. Neither is accepted, and both stay
     *       {@code null}.</li>
     *   <li>{@code multiplier}, {@code o-multiplier} and {@code power} are refused if the text starts
     *       with {@code -}, whatever the value, so {@code -0} goes too, as {@code parser_getuint}
     *       refuses it. Otherwise {@link ParseUtils#parseIntString} reads them in base 0 as
     *       {@code strtoul} does, so {@code 010} is 8, and empty text is 0. A value above
     *       {@code Integer.MAX_VALUE} is clamped where C wraps it (accepted deviation, see
     *       {@code ParseUtils}).</li>
     * </ul>
     *
     * <p>The result is in reverse file order, because C's parser prepends each slay to a list and
     * {@code finish_parse_slay} copies that list into {@code slays[]} from the head: the last
     * record in {@code slay.txt} comes first, as {@code slays[1]}. Consumers that keep the first of
     * several equivalent slays, such as {@code Rune.initRunes}, depend on this.
     *
     * <p>Function assemble coded before 260915, commented in full on 261029.
     *
     * @param records  the raw slay records from the parser
     * @param errors   not used: a fault is thrown, never added to this list
     * @param filename the file being read, for the fault messages
     * @return the assembled slays, in reverse file order (C's {@code slays[]} order)
     * @throws DataFileParseException on the first fault in any record
     */
    @Override
    public List<Slay> assemble(@NotNull List<SlayParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<Slay> slays = new ArrayList<>();

        for (SlayParseRecord record : records) {
            int line = record.line();
            String code = record.code();
            String name = record.name().isEmpty() ? null : record.name();
            String rawRaceFlag = record.raceFlag();
            String rawBase = record.base();
            // A second target on one slay is invalid; a race flag and a base together is the second.
            if (!rawRaceFlag.isEmpty() && !rawBase.isEmpty()) {
                String message = "Slay starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal state. Both monster race flag and monster base are not empty";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            // Resolve whichever one is present, leaving the other null; with neither, both stay null as in C.
            MonsterRaceFlag monsterRaceFlag = null;
            MonsterBase monsterBase = null;
            if (!rawRaceFlag.isEmpty()) {
                try {
                    monsterRaceFlag = MonsterRaceFlag.valueOf("RF_" + rawRaceFlag);
                    if (monsterRaceFlag == MonsterRaceFlag.RF_NONE) {
                        String message = "Slay starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel monster race flag value: " + rawRaceFlag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "Slay starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal monster race flag value: " + rawRaceFlag;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            } else if (!rawBase.isEmpty()) {
                monsterBase = MonsterRegistry.lookupMonsterBase(rawBase);
                if (monsterBase == null) {
                    String message = "Slay starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal monster base value: " + rawBase;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            // multiplier / o-multiplier / power are uint in C: a leading '-' is refused first, then
            // parseIntString reads base 0, gives 0 for an absent directive and clamps above INT_MAX.
            if (ParseUtils.minusFound(record.mult())) {
                String message = "Slay starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative multiplier value: " + record.mult();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int multiplier = ParseUtils.parseIntString("Slay", record.mult(), line, filename, "multiplier");
            if (ParseUtils.minusFound(record.oMult())) {
                String message = "Slay starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative o-multiplier value: " + record.oMult();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int oMult = ParseUtils.parseIntString("Slay", record.oMult(), line, filename, "o-multiplier");
            if (ParseUtils.minusFound(record.power())) {
                String message = "Slay starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative power value: " + record.power();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int power = ParseUtils.parseIntString("Slay", record.power(), line, filename, "power");
            String meleeVerb = record.meleeVerb().isEmpty() ? null : record.meleeVerb();
            String rangeVerb = record.rangeVerb().isEmpty() ? null : record.rangeVerb();

            slays.addFirst(new Slay(code, name, monsterBase, meleeVerb, rangeVerb,
                    monsterRaceFlag, multiplier, oMult, power));
        }

        return slays;
    }
}
