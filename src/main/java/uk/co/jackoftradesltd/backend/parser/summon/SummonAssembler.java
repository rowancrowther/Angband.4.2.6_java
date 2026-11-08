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
 * {@code datafile.c} starts at {@code FLAG_START} and so never matches the {@code NONE} row.
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
 * <p><strong>{@code uniques:} is true for any non-zero number.</strong> {@code parse_summon_unique}
 * reads an {@code int} with {@code parser_getint} and sets {@code unique_allowed} when it is non-zero,
 * so {@code 2}, {@code -1} and the octal {@code 010} are all true and {@code 0} and {@code 00} are
 * false. The text is read by {@link ParseUtils#isTrue}, which follows C's base 0 rules but refuses a
 * figure outside the range of an {@code int} where C wraps it (an accepted deviation, decided
 * 261027; see {@code docs/Accepted_Deviations.md}), so {@code uniques:4294967296} is 0 and false in C
 * and stops the load here.
 *
 * <p><strong>Absent and ordered values.</strong> An omitted {@code race-flag:} arrives from the
 * grammar as empty text and gives {@link MonsterRaceFlag#RF_NONE}, C's zero. The {@code base:} lines
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
 * of the bases corrected on 261108 when the list was reversed).
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
     * The first pass takes each record in file order and resolves its fields in the order C handles
     * the lines: {@code msgt:}, then {@code uniques:}, then each {@code base:}, then {@code race-flag:}.
     * A message type, base or race flag that does not resolve stops the load. The {@link Summon} is built
     * with a {@code null} fallback and the raw {@code fallback:} text. The second pass runs once every
     * summon exists, because a {@code fallback:} may name a summon defined later in the file, which is
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
     * Faults are reported in the order C meets them. Within a record the checks run {@code msgt:},
     * {@code uniques:}, {@code base:}, {@code race-flag:}, which is the order of the lines in
     * {@code summon.txt} (the grammar fixes it), and the records run in file order, so the first fault
     * in the file is the one thrown, as C's first parser error ends the load. Nothing is read after it,
     * and the second pass never runs for a file that faulted in the first.
     *
     * <p>Function assemble coded before 260915, commented in full on 261107, updated on 261108 for the
     * reversed bases.
     *
     * @param records  the raw parse records, in file order
     * @param filename the file the records were read from, named in the message of a fault
     * @return the assembled summons in file order, with fallback references linked where they resolve
     * @throws DataFileParseException if a {@code msgt:}, {@code base:} or {@code race-flag:} does not
     *                                resolve, or a {@code uniques:} figure has no digits or is outside
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
            MessageType messageType = MessageType.lookupByName(record.msgType());
            if (messageType == null) {
                String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal message type value: " + record.msgType();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            Optional<Boolean> unique = ParseUtils.isTrue(record.uniques());
            if (unique.isEmpty()) {
                String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal unique value: " + record.uniques();
                logger.fatal(message);
                throw new DataFileParseException(message);                
            }
            boolean uniques = unique.get();
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
            // The race flag is optional: an omitted one arrives as "" and means "no restriction"
            // (RF_NONE), so only a *present* value that fails to resolve is an error. Guarding on
            // isEmpty() rather than null matters - the grammar fills absent fields with "", not null.
            String rawRaceFlag = record.raceFlag();
            MonsterRaceFlag raceFlag = MonsterRaceFlag.RF_NONE;
            if (!rawRaceFlag.isEmpty()) {
                try {
                    raceFlag = MonsterRaceFlag.valueOf("RF_" + rawRaceFlag);
                    if (raceFlag == MonsterRaceFlag.RF_NONE) {
                        String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel monster race flag value: " + record.raceFlag();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "Summon starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal monster race flag value: " + record.raceFlag();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
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
