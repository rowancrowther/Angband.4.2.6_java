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

package uk.co.jackoftrades.backend.parser.quest;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.player.Quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link QuestParseRecord}s from {@code quest.txt} into domain {@link Quest}s.
 *
 * <p>This is the "assemble" half of the reader pipeline: it does the interpretation the grammar
 * deliberately skipped - parsing the integer fields and resolving each {@code race:} name to a real
 * {@link MonsterRace} against the loaded registry. It mirrors the behaviour of C's
 * {@code parse_quest_*} functions ({@code player-quest.c}), which likewise fill an in-memory quest
 * from the same four directives.
 *
 * <p><b>Dependency:</b> race resolution needs {@code monster.txt} already loaded, which is why
 * {@code loadQuests()} runs late in {@link GameConstants}' init order (after {@code loadMonsters()}).
 *
 * @author Rowan Crowther
 */
public class QuestAssembler implements Assembler<QuestParseRecord, List<Quest>> {
    /**
     * Assemble every parsed record into a {@link Quest}, dropping (never aborting on) any record
     * whose data cannot be resolved.
     *
     * <p>Each record is validated in turn; a failure appends a message to {@code errors} and
     * {@code continue}s to the next record, honouring the suite's partial-results contract. Three
     * things can drop a record:
     * <ul>
     *   <li>a {@code level} that overflows {@code int} - note the grammar already guarantees the text
     *       is all digits, so the only reachable {@link NumberFormatException} is overflow, not
     *       stray letters;</li>
     *   <li>a {@code race} name that resolves to no monster (see
     *       {@link GameConstants#lookupMonsterRace}, exact-then-substring, faithful to C
     *       {@code lookup_monster});</li>
     *   <li>a {@code number} that overflows {@code int}, as with {@code level}.</li>
     * </ul>
     *
     * <p>The {@code index} is advanced only just before a surviving quest is built, so it stays
     * contiguous over the assembled list even when earlier records were dropped (C stamps
     * {@code quest.index} = final array position). Each quest's kill tally starts empty, so
     * {@code currentNumber} is seeded to 0 (C {@code cur_num}); only {@code maxNumber} comes from the
     * file.
     *
     * @param records the raw quest records in file order
     * @param errors  the soft-error sink, appended to in place for every dropped record
     * @return the assembled quests, in file order, minus any that were dropped
     */
    @Override
    public List<Quest> assemble(@NotNull List<QuestParseRecord> records, @NotNull List<String> errors) {
        List<Quest> quests = new ArrayList<>();
        int index = -1;

        for (QuestParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            int level = 0;
            try {
                level = Integer.parseInt(record.level());
            } catch (NumberFormatException e) {
                errors.add("Quest at line " + line + " has " +
                        "an invalid level integer: " + record.level());
                continue;
            }
            MonsterRace race = GameConstants.lookupMonsterRace(record.race());
            if (race == null) {
                errors.add("Quest at line: " + line + " has " +
                        "an unknown monster race: " + record.race());
                continue;
            }
            int currentNumber = 0;
            int maxNumber = 0;
            try {
                maxNumber = Integer.parseInt(record.number());
            } catch (NumberFormatException e) {
                errors.add("Quest at line: " + line + " has " +
                        "an invalid number integer: " + record.number());
                continue;
            }

            index++;
            quests.add(new Quest(index, name, level, race,
                    currentNumber, maxNumber));
        }

        return quests;
    }
}
