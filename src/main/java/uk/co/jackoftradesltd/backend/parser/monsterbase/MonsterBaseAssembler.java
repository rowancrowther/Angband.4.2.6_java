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

package uk.co.jackoftradesltd.backend.parser.monsterbase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles the raw {@link MonsterBaseParseRecord}s parsed from {@code monster_base.txt} into
 * the finished {@link MonsterBase} domain objects (reader &rarr; ParseRecord &rarr; assembler
 * &rarr; domain).
 *
 * <p>It stands in for the handlers {@code parse_mon_base_name}, {@code parse_mon_base_glyph},
 * {@code parse_mon_base_pain}, {@code parse_mon_base_flags} and {@code parse_mon_base_desc} in
 * {@code mon-init.c}, which fill one {@code struct monster_base} a line at a time. Here the grammar
 * has already gathered each record's text, so this class only does the part that can fail: turning
 * flag codes, the glyph and the pain serial into real values.
 *
 * <p>Every unresolvable field is fatal, as in C, where each of these handlers returns a parser
 * error and the load stops: an unknown race flag, a glyph that is not exactly one code point and a
 * pain serial with no loaded set each log and throw {@link DataFileParseException}. The
 * {@code errors} list that {@link #assemble} receives is therefore never written to by this class.
 *
 * <p>The bases come back in C's order, not file order. {@code parse_mon_base_name} pushes each new
 * record on the head of the chain ({@code rb->next = h}), so {@code rb_info} starts with the last
 * record in the file, and the assembler puts each base at the front of its list to match. It
 * matters only when a name is used twice: {@code lookup_monster_base} in {@code mon-util.c} then
 * answers the later record, and a first-match search over this list does the same.
 *
 * <p>Class MonsterBaseAssembler coded before 260915, commented in full on 261027.
 *
 * @author Rowan Crowther
 */
public class MonsterBaseAssembler implements Assembler<MonsterBaseParseRecord, List<MonsterBase>> {
    /**
     * Logger that records the reason just before a {@link DataFileParseException} is thrown, so the
     * failing record's line and value are in the log as well as in the exception.
     *
     * <p>Field logger coded before 260915, commented in full on 261027.
     */
    private static final Logger logger = LogManager.getLogger(MonsterBaseAssembler.class);

    /**
     * Resolve every parsed {@code monster_base.txt} record into a {@link MonsterBase}:
     * <ul>
     *   <li><b>Flags.</b> Each code gets {@code RF_} put in front and is looked up in
     *       {@link MonsterRaceFlag}, the match being case-sensitive as C's {@code grab_flag} is.
     *       {@code NONE} resolves, as it is a row of {@code list-mon-race-flags.h}; a code with no
     *       row is fatal, as C's {@code PARSE_ERROR_INVALID_FLAG} is. A record with no
     *       {@code flags:} line gets an empty set, and several lines accumulate into one.</li>
     *   <li><b>Glyph.</b> Must be exactly one code point, else fatal (C rejects a longer value with
     *       {@code PARSE_ERROR_FIELD_TOO_LONG}). C keeps it in a {@code wchar_t}; here a code point
     *       up to {@code 0xFFFF} goes in the narrow {@code char} half and a supplementary one goes
     *       in the wide {@code int} half, with the narrow half left at {@code '\0'}.</li>
     *   <li><b>Pain.</b> The serial is parsed as a decimal integer and looked up through
     *       {@link MonsterRegistry#lookupMonsterPain}, so {@code pain.txt} must be loaded first. C
     *       accepts any serial below {@code z_info->mp_max} and points at
     *       {@code pain_messages[idx]}; here a serial that is not a number, or that no loaded set
     *       carries, is fatal. C's blank slot 0 is the accepted blank-slot deviation recorded in
     *       the {@link MonsterRegistry} class block, so serial 0 is fatal unless a record carries
     *       it.</li>
     *   <li><b>Text.</b> The {@code desc:} text is stored as both the in-game name and the
     *       description, as {@link MonsterBase} records.</li>
     * </ul>
     * The first bad field throws, naming the record's starting line, the file and the value; no
     * record is skipped and no partial list is returned. The result is built by adding each base at
     * the front, so it ends up in the reverse of file order, which is the order of C's
     * {@code rb_info} chain.
     *
     * <p>Function assemble coded before 260915, commented in full on 261027.
     *
     * @param records  the raw monster-base records from the grammar, in file order
     * @param errors   the soft-error sink required by {@link Assembler}; this assembler never
     *                 writes to it, because every failure is fatal
     * @param filename the data file being read, named in each exception
     * @return the assembled monster bases, in the reverse of file order
     * @throws DataFileParseException if a race flag is unknown, a glyph is not exactly one code
     *                                point, or a pain serial is not a number or has no loaded set
     */
    @Override
    public List<MonsterBase> assemble(@NotNull List<MonsterBaseParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MonsterBase> results = new ArrayList<>();

        for (MonsterBaseParseRecord record : records) {
            int line = record.line();
            String name = record.codeName();
            String inGameName = record.inGameName();
            Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
            for (String raceFlag : record.flags()) {
                MonsterRaceFlag flag;
                try {
                    flag = MonsterRaceFlag.valueOf("RF_" + raceFlag);
                    flags.on(flag);
                } catch (IllegalArgumentException e) {
                    String msg = "Monster base starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal monster race value: " + raceFlag;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            int codePointCount = record.glyph().codePointCount(0, record.glyph().length());
            if (codePointCount != 1) {
                String msg = "Monster base starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal glyph value: " + record.glyph();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            
            char glyph = '\0';
            int wideGlyph = 0;
            if (!Character.isSupplementaryCodePoint(record.glyph().codePointAt(0))) {
                glyph = record.glyph().charAt(0);
            } else {
                wideGlyph = record.glyph().codePointAt(0);
            }
            int type;
            try {
                type = Integer.parseInt(record.pain());
            } catch (NumberFormatException e) {
                String msg = "Monster base starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal pain value: " + record.pain();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            MonsterPain pain = MonsterRegistry.lookupMonsterPain(type);
            if (pain == null) {
                String msg = "Monster base starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal pain value: " + record.pain();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            String desc = record.description();
            
            results.addFirst(new MonsterBase(name, inGameName, flags, glyph, wideGlyph, pain, desc));
        }

        return results;
    }
}
