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

package uk.co.jackoftrades.backend.parser.playerclass;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.magic.MagicSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ClassSpellParseRecord}s of one spellbook into domain {@link MagicSpell}s.
 * The scalar casting parameters (level, mana, fail, exp) are parsed here; the spell's effects are
 * delegated to the shared {@link EffectAssembler}, which resolves each {@code effect:} block
 * against the effect registries (projections, timed effects, shapes, …).
 *
 * <p>Per the shared soft-error contract, a spell with a malformed integer is reported and skipped.
 *
 * @author Rowan Crowther
 */
public class ClassSpellAssembler implements Assembler<ClassSpellParseRecord, List<MagicSpell>> {
    /**
     * Assembles every parsed {@code spell:} block in a book into a {@link MagicSpell}, parsing its
     * scalar fields and resolving its effect blocks through {@link EffectAssembler}.
     *
     * @param records the raw spell blocks for one book, in file order
     * @param errors  the soft-error channel; malformed scalars and unresolved effects are appended here
     * @return the resolved spells, minus any dropped for a malformed scalar
     * @author Rowan Crowther
     */
    @Override
    public List<MagicSpell> assemble(@NotNull List<ClassSpellParseRecord> records, @NotNull List<String> errors) {
        List<MagicSpell> spells = new ArrayList<>();

        for (ClassSpellParseRecord record : records) {
            int line = record.line();
            String spellName = record.name();
            int level = 0;
            if (!record.level().isEmpty()) {
                try {
                    level = Integer.parseInt(record.level());
                } catch (NumberFormatException e) {
                    errors.add("Spell at line: " + line + " has " +
                            "a malformed level: " + record.level());
                    continue;
                }
            }
            int fail = 0;
            if (!record.fail().isEmpty()) {
                try {
                    fail = Integer.parseInt(record.fail());
                } catch (NumberFormatException e) {
                    errors.add("Spell at line: " + line + " has " +
                            "a malformed fail: " + record.fail());
                    continue;
                }
            }
            int mana = 0;
            if (!record.mana().isEmpty()) {
                try {
                    mana = Integer.parseInt(record.mana());
                } catch (NumberFormatException e) {
                    errors.add("Spell at line: " + line + " has " +
                            "a malformed mana amount: " + record.mana());
                    continue;
                }
            }
            int exp = 0;
            if (!record.exp().isEmpty()) {
                try {
                    exp = Integer.parseInt(record.exp());
                } catch (NumberFormatException e) {
                    errors.add("Spell at line: " + line + " has " +
                            "a malformed exp: " + record.exp());
                    continue;
                }
            }
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors);
            String spellDescription = record.desc();

            spells.add(new MagicSpell(spellName, level, fail, mana, exp, effects, spellDescription));
        }

        return spells;
    }
}
