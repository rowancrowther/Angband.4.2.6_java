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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.cave.PitProfile;
import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link PitParseRecord}s parsed from {@code pit.txt} into finished
 * {@link PitProfile}s, resolving each record's text fields to their typed forms: room-type digits
 * to {@link PitRoomType}, numeric fields to {@code int}, flag/spell codes to their enums, colour
 * codes to {@link ColourType}, and monster names to their {@link MonsterBase}/{@link MonsterRace}
 * registry objects.
 *
 * <p><strong>Skip-and-continue contract.</strong> Resolution is fail-soft per record: any
 * unresolvable field appends a message to {@code errors}, and the whole record is then dropped
 * (via the {@code bad*} guards) so a single bad pit never aborts the load and never produces a
 * half-populated profile. Every other record still assembles - this mirrors the partial-results
 * behaviour the rest of the port's assemblers use. This is the port of the assembly half of the C
 * original's pit parser in {@code src/mon-init.c}.
 *
 * @author Rowan Crowther
 */
public class PitAssembler implements Assembler<PitParseRecord, List<PitProfile>> {
    /**
     * Assemble every parsed pit record into a {@link PitProfile}, dropping (with a soft error) any
     * record that carries an unresolvable field.
     *
     * @param records the raw pit records from the parser
     * @param errors  the soft-error sink; a message is appended for each unresolvable field and the
     *                offending record is skipped rather than aborting the whole load
     * @return the successfully assembled pit profiles, in file order
     * @author Rowan Crowther
     */
    @Override
    public List<PitProfile> assemble(@NotNull List<PitParseRecord> records, @NotNull List<String> errors) {
        List<PitProfile> pits = new ArrayList<>();

        for (PitParseRecord record : records) {
            // line is captured up front purely so every error message below can point at the
            // record's source line even after its individual fields have been consumed.
            int line = record.line();
            String name = record.name();
            // room: is a bare digit in the file (C's room_type 1/2/3); map it to the enum. An
            // absent room: leaves PIT_TYPE_NONE, and an unrecognised digit falls through to NONE
            // rather than erroring, since the digit is already constrained by the lexer.
            String roomType = record.pitRoomType();
            PitRoomType pitRoomType = PitRoomType.PIT_TYPE_NONE;
            if (!roomType.isEmpty()) {
                pitRoomType = switch (roomType) {
                    case "1" -> PitRoomType.PIT_TYPE_PIT;
                    case "2" -> PitRoomType.PIT_TYPE_NEST;
                    case "3" -> PitRoomType.PIT_TYPE_OTHER;
                    default -> PitRoomType.PIT_TYPE_NONE;
                };
            }
            // The numeric fields are optional; each is parsed only when present. An overflow /
            // non-numeric value is a soft error that drops the record (continue), never a crash.
            int ave = 0;
            if (!record.averageDepth().isEmpty()) {
                try {
                    ave = Integer.parseInt(record.averageDepth());
                } catch (NumberFormatException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "a malformed alloc second integer: " + record.averageDepth());
                    continue;
                }
            }
            int rariety = 0;
            if (!record.rarity().isEmpty()) {
                try {
                    rariety = Integer.parseInt(record.rarity());
                } catch (NumberFormatException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "a malformed alloc first integer: " + record.rarity());
                    continue;
                }
            }
            int objRarity = 0;
            if (!record.objRarity().isEmpty()) {
                try {
                    objRarity = Integer.parseInt(record.objRarity());
                } catch (NumberFormatException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "a malformed obj-rarity integer: " + record.objRarity());
                    continue;
                }
            }
            // flags-req: each code is the bare tail of a MonsterRaceFlag constant (the file writes
            // ANIMAL, the enum is RF_ANIMAL), so we re-attach the RF_ prefix to resolve it. An
            // unknown code sets badFlag, and the record is dropped after the loop so we still report
            // every bad code in the line rather than bailing on the first.
            Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
            boolean badFlag = false;
            for (String flagString : record.flags()) {
                try {
                    flags.on(MonsterRaceFlag.valueOf("RF_" + flagString));
                } catch (IllegalArgumentException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "an unknown flag: " + flagString);
                    badFlag = true;
                }
            }
            if (badFlag) continue;
            // flags-ban: the excluding set, drawn from the same MonsterRaceFlag family as flags-req.
            Flag<MonsterRaceFlag> bannedFlags = new Flag<>(MonsterRaceFlag.class);
            boolean badBannedFlag = false;
            for (String flagString : record.bannedFlags()) {
                try {
                    bannedFlags.on(MonsterRaceFlag.valueOf("RF_" + flagString));
                } catch (IllegalArgumentException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "an unknown flag: " + flagString);
                    badBannedFlag = true;
                }
            }
            if (badBannedFlag) continue;
            int innateFreq = 0;
            if (!record.innateFreq().isEmpty()) {
                try {
                    innateFreq = Integer.parseInt(record.innateFreq());
                } catch (NumberFormatException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "a malformed innate frequency integer: " + record.innateFreq());
                    continue;
                }
            }
            // spell-req: as with flags, each code is the tail of a MonsterSpell constant, so the
            // RSF_ prefix is re-attached (BR_ACID -> RSF_BR_ACID). Multiple spell-req lines have
            // already been merged into one list by the grammar.
            Flag<MonsterSpell> spellFlags = new Flag<>(MonsterSpell.class);
            boolean badSpell = false;
            for (String spellString : record.spells()) {
                try {
                    spellFlags.on(MonsterSpell.valueOf("RSF_" + spellString));
                } catch (IllegalArgumentException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "an unknown spell: " + spellString);
                    badSpell = true;
                }
            }
            if (badSpell) continue;
            // spell-ban: the excluding spell set. Note this reads record.bannedSpells() - reading
            // record.spells() here (an easy copy-paste slip) would silently ignore every spell-ban.
            Flag<MonsterSpell> bannedSpellFlags = new Flag<>(MonsterSpell.class);
            boolean badBannedSpell = false;
            for (String spellString : record.bannedSpells()) {
                try {
                    bannedSpellFlags.on(MonsterSpell.valueOf("RSF_" + spellString));
                } catch (IllegalArgumentException e) {
                    errors.add("Pit at line: " + line + " has " +
                            "an unknown spell: " + spellString);
                    badBannedSpell = true;
                }
            }
            if (badBannedSpell) continue;
            // color: each entry is a single-character colour code; getColourType resolves a
            // one-char string via the colour table and returns null for anything unrecognised.
            List<ColourType> colours = new ArrayList<>();
            boolean badColour = false;
            for (String colourString : record.colours()) {
                ColourType colourType = ColourType.getColourType(colourString);
                if (colourType == null) {
                    errors.add("Pit at line: " + line + " has " +
                            "an unknown colour: " + colourString);
                    badColour = true;
                }
                colours.add(colourType);
            }
            if (badColour) continue;
            // mon-base: allowed template types, resolved by name against the loaded monster bases.
            // This is why pit loading must run after monster_base.txt (lookupMonsterBase throws if
            // the base registry is not yet populated).
            List<MonsterBase> bases = new ArrayList<>();
            boolean badMonsterBase = false;
            for (String base : record.monsterBases()) {
                MonsterBase mBase = GameConstants.lookupMonsterBase(base);
                if (mBase == null) {
                    errors.add("Pit at line: " + line + " has " +
                            "an unknown monster base: " + base);
                    badMonsterBase = true;
                }
                bases.add(mBase);
            }
            if (badMonsterBase) continue;
            // mon-ban: despite the "MonsterBases" field name, these name specific monsters and
            // resolve to MonsterRace, not MonsterBase - matching C, whose pit_profile stores
            // forbidden_monsters as a monster_race list. Hence lookupMonsterRace (which requires
            // monster.txt loaded first, another reason pits load late in the init chain).
            List<MonsterRace> bannedRaces = new ArrayList<>();
            boolean badMonsterRace = false;
            for (String race : record.bannedMonsterBases()) {
                MonsterRace monRace = GameConstants.lookupMonsterRace(race);
                if (monRace == null) {
                    errors.add("Pit at line: " + line + " has " +
                            "an unknown banned monster base: " + race);
                    badMonsterRace = true;
                }
                bannedRaces.add(monRace);
            }
            if (badMonsterRace) continue;

            pits.add(new PitProfile(name, pitRoomType, ave, rariety, objRarity,
                    flags, bannedFlags, innateFreq, spellFlags, bannedSpellFlags,
                    bases, colours, bannedRaces));
        }

        return pits;
    }
}
