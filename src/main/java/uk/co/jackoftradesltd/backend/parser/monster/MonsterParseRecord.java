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

package uk.co.jackoftradesltd.backend.parser.monster;

import java.util.List;
import java.util.Map;

/**
 * The raw, unresolved result of parsing one {@code monster.txt} record: every field is captured as the
 * plain text the grammar read, and cross-references (base, blows, drops, mimics, friends, shapes) are
 * left as names or strings for the {@code MonsterAssembler} to resolve into domain objects. The nested
 * records hold the multi-part directives. This is the DTO layer between the ANTLR grammar and the
 * assembler; {@code line} carries the source line number for error messages.
 *
 * @param name             the monster's internal/display name ({@code name:})
 * @param plural           the plural form of the name ({@code plural:}), empty if not given
 * @param base             the {@code base:} monster-base name, resolved by the assembler
 * @param glyph            the display glyph as written ({@code glyph:}), empty if inherited from
 *                         the base
 * @param colour           the display colour code ({@code color:})
 * @param speed            the speed value as text ({@code speed:})
 * @param hitPoints        the hit-point value as text ({@code hp:}), empty if not given
 * @param light            the light radius as text ({@code light:}), empty if not given
 * @param hearing          the hearing value as text ({@code hearing:}), empty if not given
 * @param smell            the smell value as text ({@code smell:}), empty if not given
 * @param shape            the {@code shape:} base/race names this monster can polymorph into,
 *                         unresolved
 * @param colourCycleGroup the visuals-cycler group name ({@code color-cycle:}), empty if none
 * @param colourCycleName  the visuals-cycler entry name within that group, empty if none
 * @param armourClass      the armour class as text ({@code ac:}), empty if not given
 * @param sleepiness       the sleepiness value as text ({@code sleepiness:}), empty if not given
 * @param depth            the native depth as text ({@code depth:}), empty if not given
 * @param rarity           the rarity value as text ({@code rarity:}), empty if not given
 * @param experience       the experience value as text ({@code exp:}), empty if not given
 * @param blows            the {@code blow:} entries, each a raw method/effect/damage triple
 * @param flagsOn          the race flag codes to add ({@code flags:}), each without its
 *                         {@code RF_} prefix, unresolved
 * @param flagsOff         the race flag codes to remove ({@code flags-off:}), unresolved
 * @param innateFreq       the innate-attack frequency as text ({@code innate-freq:}), empty if
 *                         not given (defaults to 4 in the assembler)
 * @param spellFreq        the spell-cast frequency as text ({@code spell-freq:}), empty if not
 *                         given (defaults to 4 in the assembler)
 * @param spellPower       the spell power as text ({@code spell-power:}), empty if not given
 * @param spells           the monster spell codes ({@code spells:}), each without its
 *                         {@code RSF_} prefix, unresolved
 * @param messageVis       per-spell "seen" cast messages, keyed by the spell code
 * @param messageInvis     per-spell "unseen" cast messages, keyed by the spell code
 * @param messageMiss      per-spell miss messages, keyed by the spell code
 * @param desc             the monster's descriptive text ({@code desc:})
 * @param drops            the {@code drop:} entries naming a specific object kind
 * @param dropsBase        the {@code drop-base:} entries naming only a tval (any kind of that
 *                         type)
 * @param mimics           the {@code mimic:} entries this monster can disguise itself as
 * @param friends          the {@code friends:} entries: other races/bases summoned alongside
 *                         this one
 * @param friendsBase      the {@code friends-base:} entries: base-templated allies summoned
 *                         alongside this one
 * @param line             the source line the record's {@code name:} directive started on, for
 *                         error messages
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

    /**
     * One {@code mimic:} entry: the tval/sval pair of an object kind this monster can disguise
     * itself as, unresolved.
     *
     * @param tVal the object type value as text
     * @param sVal the object sub-value (kind name) as text
     * @author Rowan Crowther
     */
    public record MonsterMimicParseRecord(String tVal,
                                          String sVal) {
    }

    /**
     * One {@code blow:} entry: a monster attack's method, effect and damage dice, all
     * unresolved. {@code effect} and {@code damage} may be empty - an omitted effect resolves to
     * {@code NONE} and an omitted damage to a zeroed die in {@link MonsterAssembler}.
     *
     * @param method the blow method name, resolved against the loaded {@code BlowMethod}s
     * @param effect the blow effect name, empty if none given
     * @param damage the damage dice expression as text, empty if none given
     * @author Rowan Crowther
     */
    public record MonsterBlowParseRecord(String method,
                                         String effect,
                                         String damage) {
    }

    /**
     * One {@code drop:} entry: a specific object kind this monster can drop, with its
     * allocation chance and quantity range, all unresolved.
     *
     * @param type   the object tval name as text
     * @param name   the object kind name (sval) as text
     * @param chance the drop chance percentage as text
     * @param min    the minimum quantity as text
     * @param max    the maximum quantity as text
     * @author Rowan Crowther
     */
    public record MonsterDropParseRecord(String type,
                                         String name,
                                         String chance,
                                         String min,
                                         String max) {
    }

    /**
     * One {@code drop-base:} entry: an unresolved-kind drop naming only a tval, with its
     * allocation chance and quantity range, all unresolved.
     *
     * @param type   the object tval name as text
     * @param chance the drop chance percentage as text
     * @param min    the minimum quantity as text
     * @param max    the maximum quantity as text
     * @author Rowan Crowther
     */
    public record MonsterDropBaseParseRecord(String type,
                                             String chance,
                                             String min,
                                             String max) {
    }

    /**
     * One {@code friends:} or {@code friends-base:} entry: an ally summoned alongside this
     * monster, with its chance, count die and group role, all unresolved. Shared between both
     * directives - for {@code friends-base:} {@code name} is a monster-base name rather than a
     * race name.
     *
     * @param chance the summon chance percentage as text
     * @param number the count-die expression as text (e.g. {@code "1d2"}), empty if not given
     * @param name   the ally's race name, or ({@code friends-base:}) monster-base name; may be
     *               {@code "same"} to mean this monster's own race
     * @param role   the group role ({@code servant}/{@code bodyguard}), empty for the default
     *               plain member role
     * @author Rowan Crowther
     */
    public record MonsterFriendsParseRecord(String chance,
                                            String number,
                                            String name,
                                            String role) {
    }
}
