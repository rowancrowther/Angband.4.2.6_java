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

package uk.co.jackoftrades.backend.parser.monsterspell;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.monsters.MonsterSpellLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the per-power blocks of a monster spell into domain {@link MonsterSpellLevel} objects.
 * <p>
 * A "level" is one tier of lore and messages that applies from a given spell power upwards. The
 * structure has one quirk worth knowing, and it is the reason the first record in the list may
 * carry no power at all: the C original allocates a level the moment it reads a {@code name:}
 * line ({@code mon-init.c:596}), so the {@code lore:} and {@code message-*:} directives that
 * appear <em>before</em> the first {@code power-cutoff:} belong to an implicit level whose power
 * is zero. The grammar reproduces that by making {@code power-cutoff:} optional at the head of a
 * block, which surfaces here as a null or empty {@code power} that defaults to 0.
 * <p>
 * Colours follow the same C default: a level with no {@code lore-color-*} directive keeps the
 * zeroed {@code lore_attr}, which {@code ColourType.getColourType} yields as
 * {@code COLOUR_TYPE_DARK} for an empty string.
 *
 * @author Rowan Crowther
 */
public class MonsterSpellLevelAssembler implements Assembler<MonsterSpellParseRecord.MonsterSpellLevelParseRecord, List<MonsterSpellLevel>> {
    /**
     * Assemble every power tier of one monster spell, in file order.
     * <p>
     * <b>All-or-nothing contract:</b> a spell's tiers are only meaningful as a set, so if any
     * single block fails to resolve this returns {@code null} rather than a partial list - a
     * spell with a hole in its power tiers would silently show the wrong lore and messages at
     * that power. Callers must treat {@code null} as a reason to drop the entire owning spell.
     *
     * @param records the parsed level blocks for a single spell
     * @param errors  the soft-error sink, mutated in place
     * @return the assembled levels in file order, or {@code null} if any block failed
     * @author Rowan Crowther
     */
    @Override
    public List<MonsterSpellLevel> assemble(@NotNull List<MonsterSpellParseRecord.MonsterSpellLevelParseRecord> records, @NotNull List<String> errors) {
        List<MonsterSpellLevel> monsterSpellLevels = new ArrayList<>();

        for (MonsterSpellParseRecord.MonsterSpellLevelParseRecord record : records) {
            int line = record.pcLine();
            String power = record.power();
            int powerLevel = 0;
            if (power != null && !power.isEmpty()) {
                try {
                    powerLevel = Integer.parseInt(power);
                } catch (NumberFormatException e) {
                    errors.add("Monster spell level at line: " + line + " has " +
                            "a malformed power-cutoff: " + power);
                    return null;
                }
            }
            String lore = record.loreDesc();
            ColourType base = ColourType.getColourType(record.loreBaseColour());
            ColourType resist = ColourType.getColourType(record.loreResistColour());
            ColourType immune = ColourType.getColourType(record.loreImmuneColour());
            String msgSave = record.saveMessage();
            String msgVis = record.visMessage();
            String msgInvis = record.invisMessage();
            String msgMiss = record.missMessage();

            monsterSpellLevels.add(new MonsterSpellLevel(powerLevel, lore, base,
                    resist, immune, msgVis, msgInvis, msgMiss, msgSave));
        }

        return monsterSpellLevels;
    }
}
