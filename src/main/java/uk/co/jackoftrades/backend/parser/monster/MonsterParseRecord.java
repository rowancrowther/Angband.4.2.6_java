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

package uk.co.jackoftrades.backend.parser.monster;

import java.util.List;
import java.util.Map;

/**
 * The raw, unresolved result of parsing one {@code monster.txt} record: every field is captured as the
 * plain text the grammar read, and cross-references (base, blows, drops, mimics, friends, shapes) are
 * left as names or strings for the {@code MonsterAssembler} to resolve into domain objects. The nested
 * records hold the multi-part directives. This is the DTO layer between the ANTLR grammar and the
 * assembler; {@code line} carries the source line number for error messages.
 *
 * @author Rowan Crowther
 */
public record MonsterParseRecord(String name,
                                 String plural,
                                 String base,
                                 String glyph,
                                 String colour,
                                 String speed,
                                 String hitPoints,
                                 String light,
                                 String hearing,
                                 String smell,
                                 List<String> shape,
                                 String colourCycleGroup,
                                 String colourCycleName,
                                 String armourClass,
                                 String sleepiness,
                                 String depth,
                                 String rarity,
                                 String experience,
                                 List<MonsterBlowParseRecord> blows,
                                 List<String> flagsOn,
                                 List<String> flagsOff,
                                 String innateFreq,
                                 String spellFreq,
                                 String spellPower,
                                 List<String> spells,
                                 Map<String, String> messageVis,
                                 Map<String, String> messageInvis,
                                 Map<String, String> messageMiss,
                                 String desc,
                                 List<MonsterDropParseRecord> drops,
                                 List<MonsterDropBaseParseRecord> dropsBase,
                                 List<MonsterMimicParseRecord> mimics,
                                 List<MonsterFriendsParseRecord> friends,
                                 List<MonsterFriendsParseRecord> friendsBase,
                                 int line) {

    public record MonsterMimicParseRecord(String tVal,
                                          String sVal) {
    }

    public record MonsterBlowParseRecord(String method,
                                         String effect,
                                         String damage) {
    }

    public record MonsterDropParseRecord(String type,
                                         String name,
                                         String chance,
                                         String min,
                                         String max) {
    }

    public record MonsterDropBaseParseRecord(String type,
                                             String chance,
                                             String min,
                                             String max) {
    }

    public record MonsterFriendsParseRecord(String chance,
                                            String number,
                                            String name,
                                            String role) {
    }
}
