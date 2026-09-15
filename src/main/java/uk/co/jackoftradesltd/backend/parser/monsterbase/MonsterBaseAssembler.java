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

package uk.co.jackoftradesltd.backend.parser.monsterbase;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles the raw {@link MonsterBaseParseRecord}s parsed from {@code monster_base.txt} into
 * the finished {@link MonsterBase} domain objects (reader &rarr; ParseRecord &rarr; assembler
 * &rarr; domain).
 *
 * @author Rowan Crowther
 */
public class MonsterBaseAssembler implements Assembler<MonsterBaseParseRecord, List<MonsterBase>> {
    /**
     * Resolve every parsed {@code monster_base.txt} record into a {@link MonsterBase}: race
     * flag codes to {@link MonsterRaceFlag} (via the {@code RF_} prefix), the glyph to a single
     * {@code char}, and the pain-message serial to the matching {@link MonsterPain} already
     * loaded into the {@link MonsterRegistry} (so {@code pain.txt} must be read first). Any
     * unresolvable field appends a message to {@code errors} and drops the whole record, rather
     * than assembling a half-built base.
     *
     * <p>Function assemble coded before 260915, commented in full on 260915.
     *
     * @param records the raw monster-base records from the grammar, in file order
     * @param errors  the soft-error sink; a message is appended for each unresolvable field and
     *                the offending record is skipped rather than aborting the whole load
     * @return the successfully assembled monster bases, in file order
     */
    @Override
    public List<MonsterBase> assemble(@NotNull List<MonsterBaseParseRecord> records, @NotNull List<String> errors) {
        List<MonsterBase> results = new ArrayList<>();

        for (MonsterBaseParseRecord record : records) {
            int line = record.line();
            String name = record.codeName();
            String inGameName = record.inGameName();
            Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
            boolean illegalFlag = false;
            for (String raceFlag : record.flags()) {
                MonsterRaceFlag flag;
                try {
                    flag = MonsterRaceFlag.valueOf("RF_" + raceFlag);
                    flags.on(flag);
                } catch (IllegalArgumentException e) {
                    errors.add("Block starting at line: " + line + " has an " +
                            "invalid Monster Race flag: " + raceFlag);
                    illegalFlag = true;
                }
            }
            if (illegalFlag) continue;
            if (record.glyph().length() != 1) {
                errors.add("Block starting at line: " + line + " has an " +
                        "invalid Monster Race glyph: " + record.glyph());
                continue;
            }
            char glyph = record.glyph().charAt(0);
            int type;
            try {
                type = Integer.parseInt(record.pain());
            } catch (NumberFormatException e) {
                errors.add("Block starting at line: " + line + " has an " +
                        "invalid pain reference: " + record.pain());
                continue;
            }
            MonsterPain pain = MonsterRegistry.lookupMonsterPain(type);
            if (pain == null) {
                errors.add("Block at line: " + line + " has an " +
                        "unknown pain reference: " + record.pain());
                continue;
            }
            String desc = record.description();

            results.add(new MonsterBase(name, inGameName, flags, glyph,
                    pain, desc));
        }

        return results;
    }
}
