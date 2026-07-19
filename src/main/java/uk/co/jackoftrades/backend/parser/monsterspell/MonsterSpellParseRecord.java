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

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.List;

/**
 * One {@code name:} block of {@code monster_spell.txt}, exactly as the grammar read it.
 * <p>
 * This is the flat, uninterpreted hand-off between the parser and
 * {@link MonsterSpellAssembler}: every field is the raw token text, with no lookup, conversion
 * or validation applied. {@code hit} is a {@link String} rather than an {@code int} for that
 * reason - turning it into a number is the assembler's job, so that a malformed value becomes a
 * reported soft error naming the file line rather than an exception thrown mid-parse.
 * <p>
 * An absent optional directive arrives as an empty string rather than null, because the grammar
 * initialises each accumulator before the alternation that fills it. {@code messageType} is the
 * one that matters: it is empty for the 49 shipped records that carry no {@code msgt:} line.
 *
 * @param name        the spell's name, without its {@code RSF_} prefix
 * @param messageType the message type without its {@code MSG_} prefix, empty if not given
 * @param hit         the base to-hit chance as written, 0-100
 * @param effects     the spell's effect blocks, in file order
 * @param levels      the spell's power tiers, in file order, always at least one
 * @param line        the file line of the {@code name:} directive, for error messages
 * @author Rowan Crowther
 */
public record MonsterSpellParseRecord(String name,
                                      String messageType,
                                      String hit,
                                      List<EffectParseRecord> effects,
                                      List<MonsterSpellLevelParseRecord> levels,
                                      int line) {

    /**
     * One power tier within a spell block - the lore and messages that apply from a given spell
     * power upwards.
     * <p>
     * {@code power} is null for the first tier of a record when no {@code power-cutoff:}
     * directive preceded it. That is not a defect: the C original allocates a level at
     * {@code name:} time ({@code mon-init.c:596}), so the lore and messages before the first
     * cutoff belong to an implicit tier whose power is zero. Every shipped record has one.
     * <p>
     * The free-text fields arrive already concatenated where a directive was repeated, matching
     * the C original's {@code string_append} rather than an overwrite.
     *
     * @param power            the power cutoff as written, or null for the implicit first tier
     * @param loreDesc         the lore text, empty if not given
     * @param loreBaseColour   the normal lore colour name, empty if not given
     * @param loreResistColour the resisted lore colour name, empty if not given
     * @param loreImmuneColour the immune lore colour name, empty if not given
     * @param visMessage       the message shown when the cast is seen, empty if not given
     * @param invisMessage     the message shown when the player is blind, empty if not given
     * @param missMessage      the message shown when the spell misses, empty if not given
     * @param saveMessage      the message shown when the player saves, empty if not given
     * @param pcLine           the file line the tier began on, for error messages
     * @author Rowan Crowther
     */
    public record MonsterSpellLevelParseRecord(String power,
                                               String loreDesc,
                                               String loreBaseColour,
                                               String loreResistColour,
                                               String loreImmuneColour,
                                               String visMessage,
                                               String invisMessage,
                                               String missMessage,
                                               String saveMessage,
                                               Integer pcLine) {
    }
}
