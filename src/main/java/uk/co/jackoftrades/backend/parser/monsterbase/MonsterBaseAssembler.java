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

package uk.co.jackoftrades.backend.parser.monsterbase;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

public class MonsterBaseAssembler implements Assembler<MonsterBaseParseRecord, List<MonsterBase>> {
    /**
     *
     * @param records List of R_ParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of R objects
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
            MonsterPain pain = GameConstants.lookupMonsterPain(type);
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
