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

package uk.co.jackoftradesltd.backend.parser.chesttrap;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.Assembler;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.objects.ChestTrap;
import uk.co.jackoftradesltd.middle.objects.enums.ChestTrapCode;

import java.util.ArrayList;
import java.util.List;

public class ChestTrapAssembler implements Assembler<ChestTrapParseRecord, List<ChestTrap>> {

    @Override
    public List<ChestTrap> assemble(@NotNull List<ChestTrapParseRecord> records, @NotNull List<String> errors) {
        List<ChestTrap> chestTraps = new ArrayList<>();
        ChestTrapCode[] expected = ChestTrapCode.values();
        boolean dropped = false;
        int position = 0;
        int previousLevel = 0;

        for (ChestTrapParseRecord record : records) {
            int index = position++;

            int line = record.line();
            String name = record.name() == null ? "" : record.name();
            String codeStr = record.code();
            if (codeStr == null) {
                errors.add("Chest trap at line " + line + " has " +
                        "a null code value");
                dropped = true;
                continue;
            }
            ChestTrapCode trapCode = null;
            if (!codeStr.isEmpty()) {
                try {
                    trapCode = ChestTrapCode.valueOf(codeStr);
                } catch (IllegalArgumentException e) {
                    errors.add("Chest trap at line: " + line + " has " +
                            "an invalid trap code: " + codeStr);
                    dropped = true;
                    continue;
                }
            }
            if (index >= expected.length) {
                errors.add("Chest trap at line: " + line + " is " +
                        "record " + (index + 1) + " but only " +
                        expected.length + " trap codes exist");
                dropped = true;
                continue;
            }
            if (trapCode != expected[index]) {
                errors.add("Chest trap at line: " + line + " has " +
                        "code " + codeStr + " but record " + index +
                        " must be " + expected[index]);
                dropped = true;
                continue;
            }
            int level = 0;
            if (record.level() != null && !record.level().isEmpty()) {
                try {
                    level = Integer.parseInt(record.level());
                } catch (NumberFormatException e) {
                    errors.add("Chest trap at line: " + line + " has " +
                            "an invalid level integer: " + record.level());
                    dropped = true;
                    continue;
                }
            }
            if (level < previousLevel) {
                errors.add("Chest trap at line: " + line + " is " +
                        "of a level lower than the previous level: " +
                        previousLevel);
                dropped = true;
                continue;
            }
            previousLevel = level;
            List<Effect> effects = new ArrayList<>();
            if (record.effect() != null && !record.effect().isEmpty()) {
                effects = EffectAssembler.assemble(record.effect(), errors);
                if (effects == null) {
                    errors.add("Chest trap at line " + line + " has " +
                            "no valid effects");
                    dropped = true;
                    continue;
                }
            } else if (record.effect() == null) {
                errors.add("Chest trap at line: " + line + " has " +
                        "no effect");
                dropped = true;
                continue;
            }
            boolean destroy = "1".equals(record.destroy());
            boolean magic = "1".equals(record.magic());
            String msg = record.msg() == null ? "" : record.msg();
            String msgDeath = record.msgDeath() == null ? "" : record.msgDeath();

            chestTraps.add(new ChestTrap(name, trapCode, level, effects, destroy, magic, msg, msgDeath));
        }

        if (dropped) return List.of();

        if (chestTraps.isEmpty()) return List.of();

        if (chestTraps.size() != expected.length) {
            errors.add("chest_trap.txt has " + chestTraps.size() + " trap" +
                    (chestTraps.size() > 1 ? "s" : "") + ", " +
                    "expected " + expected.length);
            return List.of();
        }

        return chestTraps;
    }
}
