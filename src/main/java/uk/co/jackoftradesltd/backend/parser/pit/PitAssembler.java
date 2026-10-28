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

package uk.co.jackoftradesltd.backend.parser.pit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.PitProfile;
import uk.co.jackoftradesltd.middle.cave.enums.PitRoomType;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link PitParseRecord}s parsed from {@code pit.txt} into finished
 * {@link PitProfile}s, resolving each record's text fields to their typed forms: room-type digits
 * to {@link PitRoomType}, numeric fields to {@code int}, flag/spell codes to their enums, colour
 * codes to {@link ColourEnum}, and monster names to their {@link MonsterBase}/{@link MonsterRace}
 * registry objects.
 *
 * <p>It stands in for the {@code parse_pit_*} handlers in {@code mon-init.c}: {@code parse_pit_name}
 * (the profile and its name), {@code parse_pit_room}, {@code parse_pit_alloc},
 * {@code parse_pit_obj_rarity}, {@code parse_pit_mon_base}, {@code parse_pit_mon_ban},
 * {@code parse_pit_color}, {@code parse_pit_flags_req}, {@code parse_pit_flags_ban},
 * {@code parse_pit_innate_freq}, {@code parse_pit_spell_req} and {@code parse_pit_spell_ban}.
 * {@code finish_parse_pit} (the {@code pit_info} array, {@code pit_idx} and {@code z_info->pit_max})
 * is not done here; see {@code PitReader}.
 *
 * <p>Every problem ends the load. C's parser stops at the first error and the game quits, and here
 * each one is logged and thrown as a {@link DataFileParseException}, so a bad pit is never skipped
 * and a half-populated profile is never built. The ones C reports are:
 * <ul>
 *   <li>a {@code room:}, {@code alloc:} or {@code obj-rarity:} number starting with {@code -},
 *       which C refuses for a {@code uint} (the {@code *tok == '-'} test in {@code parser_parse}),
 *       even on {@code -0};</li>
 *   <li>a {@code mon-base:} name that is no loaded base ({@code PARSE_ERROR_UNRECOGNISED_TVAL});</li>
 *   <li>a flag or spell code that is in no table ({@code PARSE_ERROR_INVALID_FLAG}). C's
 *       {@code lookup_flag} starts at index 1, so {@code NONE} is refused in all four lists; the
 *       race-flag table has no {@code MAX} row, so {@code MAX} is refused as a race flag, but the
 *       spell table does have one, so {@code MAX} is accepted as a spell;</li>
 *   <li>an {@code innate-freq:} outside 1 to 100 ({@code PARSE_ERROR_INVALID_SPELL_FREQ}).</li>
 * </ul>
 *
 * <p>Three things are not errors, as in C. A {@code color:} code that names no colour is white
 * ({@code color_char_to_attr} falls back to {@code COLOUR_WHITE}). A {@code mon-ban:} name that
 * matches no monster is stored as a {@code null} entry, where C stores a {@code NULL} race that
 * {@code mon_pit_hook} in {@code gen-room.c} can never match. A directive a record leaves out keeps
 * the value {@code mem_zalloc} gives C: 0 for a number, {@link PitRoomType#PIT_TYPE_NONE}, an empty
 * flag set or list. Numbers are read as {@code strtoul} and {@code strtol} read them in base 0, so
 * {@code 010} is 8 and {@code 08} is 0.
 *
 * <p>{@code room:} is stored as the enum, not as the number written: 1, 2 and 3 are
 * {@link PitRoomType#PIT_TYPE_PIT}, {@link PitRoomType#PIT_TYPE_NEST} and
 * {@link PitRoomType#PIT_TYPE_OTHER}, and any other number is {@link PitRoomType#PIT_TYPE_NONE}.
 * C keeps the number as written, but {@code set_pit_type} in {@code gen-room.c} is only ever called
 * with 0 (any), 1 or 2, so every other number is eligible only when the type asked for is 0,
 * whichever of {@link PitRoomType#PIT_TYPE_NONE} and {@link PitRoomType#PIT_TYPE_OTHER} it becomes
 * here. {@code innate-freq:} is stored as
 * {@code 100 / pct}, the convention of a race's {@code freq_innate}. The lists inside a profile
 * (bases, colours, forbidden monsters) are in file order, where C pushes each entry on the head of
 * its list and holds them reversed; {@code mon_pit_hook} only asks whether something matches, so
 * the order never shows.
 *
 * <p>The names are resolved here, so {@code monster_base.txt} and {@code monster.txt} must already
 * be loaded: both lookups throw {@link IllegalStateException} if their registry is empty.
 * {@code lookup_monster_base} compares exactly, case-sensitively; {@code lookup_monster} matches the
 * whole name ignoring case and then falls back to the first name containing the text. A scalar
 * directive written twice reaches this class once, as the grammar keeps the last line, where C reads
 * (and so validates) both.
 *
 * <p>Accepted deviation (decided 261027): a number outside the range of an {@code int} is clamped
 * to {@link Integer#MAX_VALUE} by {@link ParseUtils#parseIntString}, where C keeps the low 32 bits
 * of the {@code strtoul} result. For {@code innate-freq:} the clamped value is above 100, so it is
 * refused as out of range. The shipped {@code pit.txt} has none.
 *
 * <p>Class PitAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class PitAssembler implements Assembler<PitParseRecord, List<PitProfile>> {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown, so a bad
     * number, code or name is logged with its line and file as well as thrown. An unknown flag or
     * spell code is logged with the {@link IllegalArgumentException} that {@code valueOf} raised.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger(PitAssembler.class);

    /**
     * Assemble every parsed pit record into a {@link PitProfile}, in file order. For each record the
     * fields are checked in this order, and the first failure ends the load: {@code room:} (a leading
     * {@code -} is refused, then the number is read), {@code alloc:}'s level and then its rarity,
     * {@code obj-rarity:}, the {@code flags-req:} and {@code flags-ban:} codes, {@code innate-freq:},
     * the {@code spell-req:} and {@code spell-ban:} codes, the {@code color:} codes, the
     * {@code mon-base:} names and the {@code mon-ban:} names.
     *
     * <p>C reads the lines in the order the file has them, so a record with two bad lines can name a
     * different one here, and {@code alloc:} is read rarity first in C. Which line is named is the only
     * difference: the load ends either way.
     *
     * <p>{@code innate-freq:} is range-checked after the number is read, so a negative value gets the
     * out-of-range message rather than the leading-{@code -} one (the field is an {@code int} in C,
     * not a {@code uint}); an absent line is 0, and a value from 1 to 100 becomes {@code 100 / pct}
     * with integer division (3 gives 33, 100 gives 1). Flag and spell codes are the tail of the enum
     * constant, so {@code RF_} or {@code RSF_} is put back to resolve them, and a sentinel ({@code NONE})
     * is refused. A {@code mon-ban:} name is looked up and added whether or not it resolves.
     *
     * <p>Every failure is fatal and the rest of the file is not read. {@code errors} is not written
     * to: it is part of the {@link Assembler} contract and nothing here is a soft error any more.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw pit records from the parser
     * @param errors   the soft-error sink, left untouched
     * @param filename the file being read, for the messages
     * @return one {@link PitProfile} per record, in file order
     * @throws DataFileParseException if a {@code room:}, {@code alloc:} or {@code obj-rarity:} number
     *                                starts with {@code -} or has no leading digits, a flag or
     *                                spell code is unknown or a sentinel, {@code innate-freq:} is
     *                                outside 1 to 100, or a {@code mon-base:} name is no loaded base
     * @throws IllegalStateException  if the monster bases or races have not been loaded yet
     */
    @Override
    public List<PitProfile> assemble(@NotNull List<PitParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<PitProfile> pits = new ArrayList<>();

        for (PitParseRecord record : records) {
            // line is captured up front purely so every error message below can point at the
            // record's source line even after its individual fields have been consumed.
            int line = record.line();
            String name = record.name();
            // room: is a number in the file (C's room_type 1/2/3); map it to the enum. An absent
            // room: leaves PIT_TYPE_NONE, and any other number falls through to NONE rather than
            // erroring (the lexer takes any integer, so 4 or 99 get this far).
            String roomType = record.pitRoomType();
            if (ParseUtils.minusFound(roomType)) {
                String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal netagive room type value: " + roomType;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int caseVal = ParseUtils.parseIntString("Pit record", roomType, line, filename, "room type");
            PitRoomType pitRoomType = PitRoomType.PIT_TYPE_NONE;
            if (!roomType.isEmpty()) {
                pitRoomType = switch (caseVal) {
                    case 1 -> PitRoomType.PIT_TYPE_PIT;
                    case 2 -> PitRoomType.PIT_TYPE_NEST;
                    case 3 -> PitRoomType.PIT_TYPE_OTHER;
                    default -> PitRoomType.PIT_TYPE_NONE;
                };
            }
            // The numeric fields are optional: an absent one is empty text, which reads as 0. A
            // leading '-' or a value outside int range ends the load, as C's uint fields do.
            if (ParseUtils.minusFound(record.averageDepth())) {
                String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal pit average depth value: " + record.averageDepth();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int ave = ParseUtils.parseIntString("Pit record", record.averageDepth(), line, filename, "pit average depth");
            if (ParseUtils.minusFound(record.rarity())) {
                String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal pit rarity value: " + record.rarity();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int rarity = ParseUtils.parseIntString("Pit record", record.rarity(), line, filename, "pit rarity");
            if (ParseUtils.minusFound(record.objRarity())) {
                String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal object rarity value: " + record.objRarity();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int objRarity = ParseUtils.parseIntString("Pit record", record.objRarity(), line, filename, "object rarity");
            // flags-req: each code is the bare tail of a MonsterRaceFlag constant (the file writes
            // ANIMAL, the enum is RF_ANIMAL), so we re-attach the RF_ prefix to resolve it. An
            // unknown code, or the NONE sentinel, ends the load on the first one found.
            Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
            for (String flagString : record.flags()) {
                try {
                    MonsterRaceFlag mrf = MonsterRaceFlag.valueOf("RF_" + flagString);
                    if (mrf == MonsterRaceFlag.RF_NONE) {
                        String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel monster race flag value: " + flagString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    flags.on(mrf);
                } catch (IllegalArgumentException e) {
                    String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal monster race flag value: " + flagString;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            // flags-ban: the excluding set, drawn from the same MonsterRaceFlag family as flags-req.
            Flag<MonsterRaceFlag> bannedFlags = new Flag<>(MonsterRaceFlag.class);
            for (String flagString : record.bannedFlags()) {
                try {
                    MonsterRaceFlag mrf = MonsterRaceFlag.valueOf("RF_" + flagString);
                    if (mrf == MonsterRaceFlag.RF_NONE) {
                        String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal banned monster race sentinel value: " + flagString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    bannedFlags.on(mrf);
                } catch (IllegalArgumentException e) {
                    String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal banned monster race flag value: " + flagString;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            int innateFreq;
            int percent = ParseUtils.parseIntString("Pit record", record.innateFreq(), line, filename, "innate frequency");
            if (record.innateFreq().isEmpty())
                innateFreq = 0;
            else {
                if (percent < 1 || percent > 100) {
                    String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                            "an out of range innate frequency value: " + record.innateFreq();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                innateFreq = 100 / percent;
            }
            // spell-req: as with flags, each code is the tail of a MonsterSpell constant, so the
            // RSF_ prefix is re-attached (BR_ACID -> RSF_BR_ACID). Multiple spell-req lines have
            // already been merged into one list by the grammar.
            Flag<MonsterSpell> spellFlags = new Flag<>(MonsterSpell.class);
            for (String spellString : record.spells()) {
                try {
                    MonsterSpell ms = MonsterSpell.valueOf("RSF_" + spellString);
                    if (ms == MonsterSpell.RSF_NONE) {
                        String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal spell sentinel value: " + spellString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    spellFlags.on(ms);
                } catch (IllegalArgumentException e) {
                    String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal spell value: " + spellString;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            // spell-ban: the excluding spell set. Note this reads record.bannedSpells() - reading
            // record.spells() here (an easy copy-paste slip) would silently ignore every spell-ban.
            Flag<MonsterSpell> bannedSpellFlags = new Flag<>(MonsterSpell.class);
            for (String spellString : record.bannedSpells()) {
                try {
                    MonsterSpell ms = MonsterSpell.valueOf("RSF_" + spellString);
                    if (ms == MonsterSpell.RSF_NONE) {
                        String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal banned spell sentinel value: " + spellString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    bannedSpellFlags.on(ms);
                } catch (IllegalArgumentException e) {
                    String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal banned spell value: " + spellString;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            // color: each entry is a single-character colour code; ColourEnum.fromCode resolves a
            // one-char string via the colour table and returns null for anything unrecognized,
            // which becomes white as in C's color_char_to_attr.
            List<ColourEnum> colours = new ArrayList<>();
            for (String colourString : record.colours()) {
                ColourEnum colourType = ColourEnum.fromCode(colourString);
                if (colourType == null) {
                    colourType = ColourEnum.COLOUR_WHITE;
                }
                colours.add(colourType);
            }
            // mon-base: allowed template types, resolved by name against the loaded monster bases.
            // This is why pit loading must run after monster_base.txt (lookupMonsterBase throws if
            // the base registry is not yet populated).
            List<MonsterBase> bases = new ArrayList<>();
            for (String base : record.monsterBases()) {
                MonsterBase mBase = MonsterRegistry.lookupMonsterBase(base);
                if (mBase == null) {
                    String msg = "Pit record starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal monster base value: " + base;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                bases.add(mBase);
            }
            // mon-ban: despite the "MonsterBases" field name, these name specific monsters and
            // resolve to MonsterRace, not MonsterBase - matching C, whose pit_profile stores
            // forbidden_monsters as a monster_race list. Hence lookupMonsterRace (which requires
            // monster.txt loaded first, another reason pits load late in the init chain). A name
            // that matches no monster is added as null, as C stores a NULL race and never checks it.
            List<MonsterRace> bannedRaces = new ArrayList<>();
            for (String race : record.bannedMonsterBases()) {
                MonsterRace monRace = MonsterRegistry.lookupMonsterRace(race);
                bannedRaces.add(monRace);
            }

            pits.add(new PitProfile(name, pitRoomType, ave, rarity, objRarity,
                    flags, bannedFlags, innateFreq, spellFlags, bannedSpellFlags,
                    bases, colours, bannedRaces));
        }

        return pits;
    }
}
