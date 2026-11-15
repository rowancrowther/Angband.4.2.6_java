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

package uk.co.jackoftradesltd.backend.parser.summon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Turns the raw {@link SummonParseRecord}s produced by {@code SummonGrammar} into resolved
 * {@link Summon} domain objects, resolving each record's message type, monster bases, race flag and
 * fallback cross-reference. Java port of the interpretation done by {@code parse_summon_*} plus
 * {@code finish_parse_summon} in the original {@code mon-summon.c}.
 *
 * <p><strong>Every fault stops the load.</strong> C's first parser error ends the whole file, and so
 * does this: a fault is logged as fatal and thrown as a {@link DataFileParseException}, and no
 * later record is read. The faults are a {@code msgt:}
 * that {@link MessageType#lookupByName} cannot resolve ({@code parse_summon_message_type} returns
 * {@code PARSE_ERROR_INVALID_MESSAGE}), a {@code base:} that matches no monster base
 * ({@code PARSE_ERROR_INVALID_MONSTER_BASE}), and a {@code race-flag:} that names no flag
 * ({@code PARSE_ERROR_INVALID_FLAG}). {@code race-flag:NONE} is one of these: {@code lookup_flag} in
 * {@code datafile.c} starts at {@code FLAG_START} and so never matches the {@code NONE} row. A
 * {@code uniques:} figure that cannot be read is the fourth; see below.
 *
 * <p><strong>An unresolved {@code fallback:} is not a fault here (accepted deviation, decided
 * 261107).</strong> {@code finish_parse_summon} sets each {@code fallback} to
 * {@code summon_name_to_idx(fallback_name)}, which gives -1 for a summon with no {@code fallback:}
 * line, because its {@code fallback_name} is NULL and the {@code name &&} guard skips the search. A
 * name that is present but matches no summon is different. {@code finish_parse_summon} adds 1 to
 * {@code summon_max} before the fallback loop, so {@code summons} ends in a blank slot whose
 * {@code name} is NULL, and {@code summon_name_to_idx} guards its argument but not that field. The
 * search therefore ends in {@code strcmp(name, NULL)}, which is undefined behaviour and in practice a
 * crash, never a quiet -1. Here the {@link Summon} is kept with a {@code null} fallback and its
 * {@link Summon#getFallbackName} still holds the text. This cannot bite today: both {@code fallback:}
 * lines in the shipped {@code summon.txt} name {@code HI_UNDEAD}, which resolves. Do not flag. Names
 * are matched exactly and case-sensitively, and when two summons share a name the first in the file
 * wins, as in C.
 *
 * <p><strong>{@code uniques:} is true if any line is non-zero.</strong> {@code parse_summon_unique}
 * reads an {@code int} with {@code parser_getint} and sets {@code unique_allowed} when it is non-zero.
 * It never sets the flag back to false, so with several {@code uniques:} lines one non-zero figure
 * makes the summon true wherever it comes: {@code 1} then {@code 0} and {@code 0} then {@code 1} are
 * both true, and only all zeros, or no line at all, is false. {@code 2}, {@code -1} and the octal
 * {@code 010} are non-zero; {@code 0} and {@code 00} are zero. Every line is read by
 * {@link ParseUtils#isTrue}, whatever the lines before it gave. That follows C's base 0 rules but
 * refuses a figure outside the range of an {@code int} where C wraps it (an accepted deviation, decided
 * 261027; see {@code docs/Accepted_Deviations.md}), so {@code uniques:4294967296} is 0 and false in C
 * and stops the load here, even after a {@code uniques:1}.
 *
 * <p><strong>Absent, repeated and ordered values.</strong> Every directive but {@code name:} may be
 * left out, because C's handlers each fill one field of a zeroed record. The grammar hands over the
 * matching default: no {@code msgt:} gives {@link MessageType#MSG_GENERIC}, C's zero; no
 * {@code uniques:} gives false; no {@code race-flag:} gives {@link MonsterRaceFlag#RF_NONE}, C's
 * zero; no {@code base:} gives an empty list; no {@code fallback:} arrives as empty text and links
 * nothing. No {@code desc:} arrives as empty text, where C leaves a NULL string. A record with only a
 * {@code name:} line therefore loads, and so does a file with no records at all, which gives an empty
 * list. A repeated {@code fallback:} or {@code desc:} overwrites the field in C, so the last one wins;
 * the record handed over holds only the text of the last. A repeated {@code msgt:} or
 * {@code race-flag:} overwrites too, so the last one resolved is the value used. The record holds every
 * one of these lines and each is resolved, because C refuses an unresolvable line when it reads it,
 * even if a later line would have replaced it. The {@code base:} lines from every group in the record
 * are held in the reverse of file order: {@code parse_summon_base} prepends each base to a linked
 * list, so the base written last is held first, and the list is built with {@code addFirst} to match.
 * {@code summon_specific_okay} only tests membership, so play never sees the order. The records
 * themselves stay in file order, because {@code finish_parse_summon} fills the {@code summons} array
 * from the end of a chain that was itself built backwards. The list returned holds only real records,
 * with no blank slot at the end and so no counterpart of {@code summon_max}; this is the accepted
 * deviation recorded on {@code MonsterRegistry}.
 *
 * <p>Class SummonAssembler coded before 260915, commented in full on 261107 (updated on 261105 for
 * the removal of the soft-error path; the unresolved-fallback account corrected on 261107; the order
 * of the bases corrected on 261108 when the list was reversed; updated on 261112 for directives in
 * any order, repeated or left out, and for the several {@code uniques:} lines; updated on 261113 for
 * every {@code msgt:} and {@code race-flag:} line being resolved).
 *
 * @author Rowan Crowther
 */
public class SummonAssembler implements Assembler<SummonParseRecord, List<Summon>> {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261107.
     */
    private final static Logger logger = LogManager.getLogger(SummonAssembler.class);

    /**
     * Assemble the parsed summon records into {@link Summon} objects, in two passes.
     * <p>
     * The first pass takes each record in file order and resolves its fields in a fixed order:
     * each {@code msgt:} line, then each {@code uniques:} line, then each {@code base:} line, then each
     * {@code race-flag:} line. The order of the lines in the file does not matter, because the grammar
     * takes the directives in any order. A message type, base or race flag that does not resolve, even
     * one that a later line would have replaced, or a {@code uniques:} figure that cannot be read,
     * stops the load. The last {@code msgt:} and the last {@code race-flag:} give the values used. The
     * {@link Summon} is built with a {@code null} fallback and the raw {@code fallback:} text. The
     * second pass runs once every summon exists, because a {@code fallback:} may name a summon defined
     * later in the file, which is
     * what {@code finish_parse_summon} in {@code mon-summon.c} does after the chain has been copied into
     * the {@code summons} array. It looks each non-empty fallback name up with the equivalent of
     * {@code summon_name_to_idx} and links the first match. A non-empty name that matches nothing leaves
     * the fallback {@code null} without a report; C has no such outcome, because its search runs into the
     * blank last slot of {@code summons} and fails in {@code strcmp} (an accepted deviation, recorded in
     * the class block).
     * <p>
     * Each resolved base is put at the head of the summon's list with {@code addFirst}, as
     * {@code parse_summon_base} does, so a summon's bases come back last-written first.
     * <p>
     * The class block records the rules for {@code uniques:}, the sentinel race flag, the order of
     * the bases and the unresolved fallback.
     * <p>
     * The records run in file order, so the first record with a fault is the one thrown, as C's first
     * parser error ends the load. Nothing is read after it, and the second pass never runs for a file
     * that faulted in the first. Within one record the checks run {@code msgt:}, {@code uniques:},
     * {@code base:}, {@code race-flag:} whatever order the lines came in, with the lines of each kind
     * taken in file order. A record with two faults can therefore report a different one from the one
     * C meets first; the load stops either way.
     *
     * <p>Function assemble coded before 260915, commented in full on 261107, updated on 261108 for the
     * reversed bases, on 261112 for directives in any order and several {@code uniques:} lines, and on
     * 261113 for every {@code msgt:} and {@code race-flag:} line being resolved.
     *
     * @param records  the raw parse records, in file order; may be empty, which gives an empty list
     * @param filename the file the records were read from, named in the message of a fault
     * @return the assembled summons in file order, with fallback references linked where they resolve
     * @throws DataFileParseException if a {@code msgt:}, {@code base:} or {@code race-flag:} does not
     *                                resolve, or any {@code uniques:} figure has no digits or is outside
     *                                the range of an {@code int}
     */
    @Override
    public List<Summon> assemble(@NotNull List<SummonParseRecord> records,
                                    String filename) {
        // First pass: build every summon, carrying its raw fallback name.
        // Second pass (below) links the fallback references, which can only be done once all
        // summons exist, since a fallback may name a summon defined later in the file.
        List<Summon> summons = new ArrayList<>();
        for (SummonParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            MessageType messageType = MessageType.MSG_GENERIC;
            if (!record.msgType().isEmpty()) {
                for (String msgType : record.msgType()) {
                    messageType = MessageType.lookupByName(msgType);
                    if (messageType == null) {
                        String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal message type value: " + msgType;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            boolean uniques = false;
            if (!record.uniques().isEmpty()) {
                for (String unique : record.uniques()) {
                    Optional<Boolean> uniqueBool = ParseUtils.isTrue(unique);
                    if (uniqueBool.isEmpty()) {
                        String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal unique value: " + unique;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    if (uniqueBool.get())
                        uniques = true;
                }
            }
            List<MonsterBase> bases = new ArrayList<>();
            for (String base : record.bases()) {
                MonsterBase monsterBase = MonsterRegistry.lookupMonsterBase(base);
                if (monsterBase == null) {
                    String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal monster base value: " + base;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                bases.addFirst(monsterBase);
            }
            // The race flag is optional: with no race-flag: line the list is empty and the flag stays
            // RF_NONE, "no restriction". Every line that is present is resolved, so a bad one stops the
            // load even if a later line would replace it, as in C; the last one resolved is kept.
            MonsterRaceFlag raceFlag = MonsterRaceFlag.RF_NONE;
            if (!record.raceFlag().isEmpty()) {
                for (String rawRaceFlag : record.raceFlag()) {
                    try {
                        raceFlag = MonsterRaceFlag.valueOf("RF_" + rawRaceFlag);
                        if (raceFlag == MonsterRaceFlag.RF_NONE) {
                            String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel monster race flag value: " + rawRaceFlag;
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                        }
                    } catch (IllegalArgumentException e) {
                        String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal monster race flag value: " + rawRaceFlag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            String fallBack = record.fallback();
            String desc = record.desc();

            // The fallback reference is left null here and resolved in the second pass; the raw name
            // is carried on the Summon so that pass can find its target.
            Summon fallbacksummon = null;

            summons.add(new Summon(name, messageType, uniques, bases,
                    raceFlag, fallbacksummon, fallBack, desc));
        }

        // Second pass: link fallback references now that every summon exists. An unresolvable name
        // is no fault here: the summon is kept with a null fallback. C does not do this (its search
        // runs into the blank last slot and fails in strcmp); accepted deviation, see the class block.
        for (Summon summon : summons) {
            if (!summon.getFallbackName().isEmpty()) {
                String fallbackName = summon.getFallbackName();
                Summon sum = summons.stream()
                        .filter(s -> s.getName().equals(fallbackName))
                        .findFirst().orElse(null);
                
                summon.setFallback(sum);
            }
        }

        return summons;
    }
}
