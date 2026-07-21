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

package uk.co.jackoftrades.backend.parser.pit;

import java.util.List;

/**
 * The raw, unresolved carrier for a single {@code pit.txt} record, produced by {@code PitGrammar}
 * and consumed by {@link uk.co.jackoftrades.backend.parser.pit.PitAssembler}. It is the DTO half of
 * the reader pipeline (text -> lexer/parser -> {@code PitParseRecord} -> assembler -> {@code PitProfile}).
 *
 * <p>Every field is a plain {@link String} (or list of strings) holding exactly what the grammar
 * captured, with no interpretation: numbers are still text, flag/spell/colour codes are still their
 * data-file spellings, and monster names are unresolved. Deferring all validation and
 * enum/registry resolution to the assembler keeps the grammar a pure transcriber and gives one
 * place where a bad value becomes a reported error - matching how every other reader in this port
 * splits parse from assemble. This is the parse-time counterpart of the C original's
 * {@code struct pit_profile} as it is being filled in {@code src/mon-init.c}'s pit parser.
 *
 * @param name               the pit's name ({@code name:})
 * @param pitRoomType        room-type digit as text - {@code "1"}/{@code "2"}/{@code "3"} ({@code room:})
 * @param rarity             allocation rarity, first {@code alloc:} field, as text
 * @param averageDepth       average native level, second {@code alloc:} field, as text
 * @param objRarity          per-square object rarity percentage ({@code obj-rarity:}), as text
 * @param flags              required monster race flag codes ({@code flags-req:}), unresolved
 * @param bannedFlags        forbidden monster race flag codes ({@code flags-ban:}), unresolved
 * @param innateFreq         required innate-attack frequency ({@code innate-freq:}), as text
 * @param spells             required monster spell codes ({@code spell-req:}), unresolved
 * @param bannedSpells       forbidden monster spell codes ({@code spell-ban:}), unresolved
 * @param colours            theme colour codes ({@code color:}), one single-char code per entry
 * @param monsterBases       allowed monster base names ({@code mon-base:}), unresolved
 * @param bannedMonsterBases forbidden monster names ({@code mon-ban:}) - resolved later as races,
 *                           not bases, since C's {@code forbidden_monsters} is a {@code monster_race} list
 * @param line               the source line the record's {@code name:} sits on, for error messages
 * @author Rowan Crowther
 */
public record PitParseRecord(String name,
                             String pitRoomType,
                             String rarity,
                             String averageDepth,
                             String objRarity,
                             List<String> flags,
                             List<String> bannedFlags,
                             String innateFreq,
                             List<String> spells,
                             List<String> bannedSpells,
                             List<String> colours,
                             List<String> monsterBases,
                             List<String> bannedMonsterBases,
                             int line) {
}
