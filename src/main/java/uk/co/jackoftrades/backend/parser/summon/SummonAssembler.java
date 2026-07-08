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

package uk.co.jackoftrades.backend.parser.summon;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link SummonParseRecord}s produced by {@code SummonGrammar} into resolved
 * {@link Summon} domain objects, resolving each record's message type, monster bases, race flag and
 * fallback cross-reference. Java port of the interpretation done by {@code parse_summon_*} plus
 * {@code finish_parse_summon} in the original {@code mon-summon.c}.
 * <p>
 * Errors are soft (collected into the {@code errors} list, never thrown) so one bad record does not
 * abort the file, but there are two distinct failure behaviours: an unresolvable message type, base
 * or race flag <em>drops</em> the record, whereas an unresolvable fallback name is reported yet the
 * record is still kept (with a {@code null} fallback), mirroring the original, where a dangling
 * fallback simply resolves to the "no fallback" sentinel.
 *
 * @author Rowan Crowther
 */
public class SummonAssembler implements Assembler<SummonParseRecord, List<Summon>> {
    /**
     * Assemble the parsed summon records into {@link Summon} objects.
     *
     * @param records the raw parse records, in file order
     * @param errors  accumulator for soft (non-fatal) assembly errors; records that cannot be fully
     *                resolved are reported here and either skipped or kept per the class contract
     * @return the successfully assembled summons, with fallback references linked
     */
    @Override
    public List<Summon> assemble(@NotNull List<SummonParseRecord> records, @NotNull List<String> errors) {
        // First pass: build every summon that resolves cleanly, carrying its raw fallback name.
        // Second pass (below) links the fallback references, which can only be done once all
        // summons exist, since a fallback may name a summon defined later in the file.
        List<Summon> summons = new ArrayList<>();
        for (SummonParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String rawMsgt = record.msgType();
            MessageType messageType = MessageType.MSG_NONE;
            try {
                messageType = MessageType.valueOf("MSG_" + rawMsgt);
            } catch (IllegalArgumentException e) {
                errors.add("Block starting at line: " + line + " has " +
                        "an invalid message type: " + rawMsgt);
                continue;
            }
            boolean uniques = record.uniques().equals("1");
            List<MonsterBase> bases = new ArrayList<>();
            boolean illegalBase = false;
            for (String base : record.bases()) {
                MonsterBase monsterBase = GameConstants.getMonsterBase(base);
                if (monsterBase == null) {
                    errors.add("Block starting at line: " + line + " has " +
                            "an invalid monster base: " + base);
                    illegalBase = true;
                }
                bases.add(monsterBase);
            }
            if (illegalBase) continue;
            // The race flag is optional: an omitted one arrives as "" and means "no restriction"
            // (RF_NONE), so only a *present* value that fails to resolve is an error. Guarding on
            // isEmpty() rather than null matters - the grammar fills absent fields with "", not null.
            String rawRaceFlag = record.raceFlag();
            MonsterRaceFlag raceFlag = MonsterRaceFlag.RF_NONE;
            if (!rawRaceFlag.isEmpty()) {
                try {
                    raceFlag = MonsterRaceFlag.valueOf("RF_" + rawRaceFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Block beginning line: " + line + " has " +
                            "invalid race flag: " + rawRaceFlag);
                    continue;
                }
            }
            String fallBack = record.fallback();
            String desc = record.desc();

            // The fallback reference is left null here and resolved in the second pass; the raw name
            // is carried on the Summon so that pass can find its target.
            Summon fallbacksummon = null;

            summons.add(new Summon(name, messageType, uniques, bases,
                    raceFlag, fallbacksummon, fallBack, desc));
        }

        // Second pass: link fallback references now that every summon exists. An unresolvable name
        // is reported but the summon is kept with a null fallback (unlike the drop-on-failure cases
        // in the first pass).
        for (Summon summon : summons) {
            if (!summon.getFallbackName().isEmpty()) {
                String fallbackName = summon.getFallbackName();
                Summon sum = summons.stream()
                        .filter(s -> s.getName().equals(fallbackName))
                        .findFirst().orElse(null);
                if (sum == null) {
                    errors.add("Invalid fallback name: " + fallbackName + " found for " +
                            "block: " + summon.getName());
                    continue;
                }
                summon.setFallback(sum);
            }
        }

        return summons;
    }
}
