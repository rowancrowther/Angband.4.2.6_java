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
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.monsters.MonsterSpellLevel;
import uk.co.jackoftrades.middle.monsters.MonsterSpellType;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link MonsterSpellParseRecord}s the grammar produced into domain
 * {@link MonsterSpellType} objects - the interpretation half of the {@code monster_spell.txt}
 * pipeline, where deliberately unconverted token text becomes enums, ints and effects.
 * <p>
 * Four conversions carry real risk of diverging from the C original, and each is commented at
 * the point it happens in {@link #assemble}:
 * <ul>
 *   <li>the spell <em>name</em>, which indexes {@code list-mon-spells.h} via the {@code RSF_}
 *       prefix;</li>
 *   <li>the <em>optional</em> {@code msgt:} directive - 49 of the 91 shipped records omit it,
 *       and [C] {@code parse_mon_spell_message_type} simply never runs for an absent line;</li>
 *   <li>the effect block, delegated to {@link EffectAssembler}, whose all-or-nothing contract
 *       means a single unresolvable effect fails the whole spell; and</li>
 *   <li>the per-power levels, delegated to {@link MonsterSpellLevelAssembler}.</li>
 * </ul>
 * Failures follow the suite's partial-results contract: an unresolvable value is a <em>soft</em>
 * error, appended to the caller's list with the offending line number, and that record alone is
 * skipped rather than aborting the load. (C is stricter - a bad record stops the file load
 * outright - but reporting every bad record in one pass is more useful while porting.)
 *
 * @author Rowan Crowther
 */
public class MonsterSpellAssembler implements Assembler<MonsterSpellParseRecord, List<MonsterSpellType>> {
    /**
     * Assemble every parsed monster-spell record into its domain object.
     *
     * @param records the raw parse records, in file order
     * @param errors  the soft-error sink, mutated in place
     * @return the assembled monster spells, excluding any record that was skipped
     * @author Rowan Crowther
     */
    @Override
    public List<MonsterSpellType> assemble(@NotNull List<MonsterSpellParseRecord> records, @NotNull List<String> errors) {
        List<MonsterSpellType> monsterSpellTypes = new ArrayList<>();
        MonsterSpellLevelAssembler assembler = new MonsterSpellLevelAssembler();

        for (MonsterSpellParseRecord record : records) {
            int line = record.line();
            String nameString = record.name();
            MonsterSpell nameType;
            try {
                nameType = MonsterSpell.valueOf("RSF_" + nameString);
            } catch (IllegalArgumentException e) {
                errors.add("Monster spell at line: " + line + " has " +
                        "an invalid name: " + nameString);
                continue;
            }
            // msgt: is optional. [C] parse_mon_spell_message_type never runs for an absent line,
            // leaving the mem_zalloc'd msgt of 0 - and list-message.h starts at MSG(GENERIC), so
            // zero is MSG_GENERIC. 49 of the 91 shipped records omit the directive, so treating
            // an empty value as an invalid message type silently discards most of the table.
            String messageT = record.messageType();
            MessageType messageType = MessageType.MSG_GENERIC;
            if (!record.messageType().isEmpty()) {
                try {
                    messageType = MessageType.valueOf("MSG_" + messageT);
                } catch (IllegalArgumentException e) {
                    errors.add("Monster spell at line: " + line + " has " +
                            "an invalid message type: " + messageT);
                    continue;
                }
            }
            int hit = 0;
            try {
                hit = Integer.parseInt(record.hit());
            } catch (NumberFormatException e) {
                errors.add("Monster spell at line: " + line + " has " +
                        "a malformed hit value: " + record.hit());
                continue;
            }
            // EffectAssembler is all-or-nothing: null means at least one effect in this spell's
            // block failed to resolve, and a spell missing one of its effects would be a subtly
            // wrong game object, so the whole record goes. Per-effect messages are already in
            // errors by this point.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors);
            if (effects == null) continue;
            List<MonsterSpellLevel> levels = assembler.assemble(record.levels(), errors);
            if (levels == null) continue;
            monsterSpellTypes.add(new MonsterSpellType(nameType, messageType, hit, effects, levels));
        }

        return monsterSpellTypes;
    }
}
