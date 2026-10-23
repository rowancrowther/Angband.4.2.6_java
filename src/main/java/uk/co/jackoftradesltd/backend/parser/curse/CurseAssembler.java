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

package uk.co.jackoftradesltd.backend.parser.curse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the raw {@link CurseParseRecord}s produced by the grammar into fully
 * resolved {@link Curse} domain objects — the assembler half of the curse loader
 * (Java port of {@code parse_curse_*} in {@code src/obj-init.c}).
 * <p>
 * The work is split into two passes, mirroring the Summon loader:
 * <ol>
 *   <li><b>First pass</b> — for each record, resolve every field against its
 *       enum and build a {@link Curse}. Any malformed field ends the whole load with a
 *       {@link DataFileParseException} naming the file and the line the record starts on, as
 *       C's {@code run_parser} stops at the first parse error (the "Bad record" decision of
 *       2026-10-21 in {@code docs/Issue_Approach_Order.md}).</li>
 *   <li><b>Second pass</b> — once every curse exists, link each curse's
 *       {@code conflict:} names to the actual {@link Curse} instances. Deferring
 *       this to a second pass lets forward references resolve. C instead keeps the
 *       names as a {@code |name|} string ({@code parse_curse_conflict}) and searches it
 *       by name at use ({@code curses_conflict} in {@code obj-curse.c}).</li>
 * </ol>
 * The {@code type:} line is kept as a list of {@link TValue}s, the counterpart of C's
 * {@code curse->poss[tval]} array: any name {@code tval_find_idx} accepts loads, including a
 * tval with no object base ({@code none}, {@code other book}), whose entry simply never matches
 * an object, as in C.
 * Two data lines each fan out across <em>two</em> destinations, following the C
 * originals:
 * <ul>
 *   <li>the {@code flags:} line ({@code parse_curse_flags}) — {@code HATES_}/
 *       {@code IGNORE_} tokens set element flags on {@code elInfo}; everything
 *       else is an {@link ObjectFlag};</li>
 *   <li>the {@code values:} line ({@code parse_curse_values}) — {@code RES_}
 *       tokens set a per-element resistance level on {@code elInfo}; everything
 *       else is an additive {@link ObjectModifier}.</li>
 * </ul>
 * Both the flag and value writers reach an element through
 * {@link Map#computeIfAbsent}, so an element touched by both lines shares one
 * {@link ElementInfo} instead of one write clobbering the other.
 * <p>
 * <b>Load-order precondition:</b> the shipped {@code curse.txt} uses
 * {@code EF_SUMMON}, which {@code EffectAssembler} resolves via
 * {@link MonsterRegistry#lookupSummon}. The summon registry must therefore be
 * populated before curse assembly runs (mirroring C's summons-before-curses
 * order).
 * <p>
 * <b>Load-time checks carried over from C</b> ({@code obj-init.c}, functions
 * {@code parse_curse_weight} and {@code finish_parse_curse}):
 * <ul>
 *   <li>the {@code weight:} adjustment must fit a signed 16-bit value
 *       (-32768 to 32767); it is range-checked, never clamped, so a negative
 *       adjustment is preserved;</li>
 *   <li>a curse with {@code MULTIPLY_WEIGHT} in its {@code flags:} must not have a
 *       negative weight adjustment;</li>
 *   <li>no more than {@value #MAX_CURSES} curses may be defined.</li>
 * </ul>
 * Each of these ends the load, as the matching C error does.
 * <p>
 * <b>Ordering and index:</b> C builds its curse list by prepending, so
 * {@code curses[1]} is the <em>last</em> curse in the file. The assembled list is
 * reversed to match, and each {@link Curse} is then given its position in that
 * reversed list as its index. C numbers curses from 1 ({@code curses[0]} is an
 * unused slot and {@code curse_max} is the count plus one); this port numbers from
 * 0.
 * <p>
 * <b>Known divergences from C:</b>
 * <ul>
 *   <li>the {@code NONE}/{@code MAX} sentinels of {@link ElementEnum} and
 *       {@link ObjectModifier} resolve by name, so {@code flags:HATES_MAX} or
 *       {@code values:MAX[1]} load here where C rejects them as unknown
 *       (accepted, as the enums carry sentinels C lacks);</li>
 *   <li>a {@code conflict:} name that matches no curse ends the load; C never validates it and
 *       the name simply never matches. Accepted 2026-10-23, because conflicts are held as
 *       resolved {@link Curse} references rather than a name string: see "A {@code conflict:}
 *       name that matches no curse ends the load" in {@code docs/Accepted_Deviations.md}.</li>
 * </ul>
 * <b>Time:</b> the record's own {@code time:} dice string is parsed and set on the curse's
 * {@link ItemObject}, where C's {@code parse_curse_time} stores it in {@code curse->obj->time}. An
 * absent line gives a zero {@link Random}, and an unparseable one ends the load. The {@link Effect}s
 * carry no time of their own.
 *
 * <p>Class CurseAssembler coded before 260915, commented in full on 261023, every fault made fatal
 * and {@code type:} held as {@link TValue}s on 261023.
 *
 * @author Rowan Crowther
 */
public class CurseAssembler implements Assembler<CurseParseRecord, List<Curse>> {
    /**
     * Logs each fault at {@code FATAL} just before the {@link DataFileParseException} that ends the
     * load, standing in for C's {@code print_error} in {@code datafile.c}.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(CurseAssembler.class);

    /**
     * The most curses {@code curse.txt} may define: 254, as in C's
     * {@code finish_parse_curse}, which fails with {@code PARSE_ERROR_TOO_MANY_ENTRIES}
     * on the 255th. {@link #assemble} counts each record before checking anything else in it, so
     * the 255th record ends the load whatever it holds.
     *
     * <p>Field MAX_CURSES coded before 260915, commented in full on 261023, count changed from
     * survivors to records on 261021.
     */
    private static final int MAX_CURSES = 254;

    /**
     * Assemble the parsed curse records into resolved {@link Curse}s (Java port of
     * the {@code parse_curse_*} functions and {@code finish_parse_curse} in
     * {@code obj-init.c}). The first fault ends the load; nothing is skipped.
     * <p>
     * The load ends on: a 255th record; a missing or empty {@code name:}; a {@code type:} name
     * that is not a tval; a non-integer or out-of-range ({@code -32768..32767}) {@code weight:};
     * an effect that fails to resolve; an unknown {@code flags:} token; an unknown or non-integer
     * {@code values:} entry; a non-integer {@code combat:} field; an unknown
     * {@code conflict-flags:} token; {@code MULTIPLY_WEIGHT} combined with a negative weight; an
     * unparseable {@code time:} dice string; or, in the second pass, a {@code conflict:} name that
     * matches no curse (an accepted deviation, see the class comment). An absent {@code weight:}
     * or {@code combat:} means 0, and an absent {@code time:} means a zero {@link Random}. The
     * {@code desc:} lines are concatenated with no separator, as C does.
     * <p>
     * Once every record is processed, the curses are reversed to match C's prepend-built list and
     * re-indexed from 0 (see the class comment), then each curse's conflict names are linked to
     * {@link Curse} instances in a second pass. The match is exact and case-sensitive, as C's
     * {@code strstr} is.
     *
     * <p>Function assemble coded before 260915, commented in full on 261023, every fault made
     * fatal and {@code type:} held as {@link TValue}s on 261023.
     *
     * @param records  the raw parse records from the grammar
     * @param errors   receives the per-effect messages from {@link EffectAssembler#assemble}; a
     *                 failed effect then ends the load here, so the list never carries a
     *                 skipped record
     * @param filename the file being read, for the messages
     * @return the assembled curses in C's order (last curse in the file first), with
     *         conflict links resolved
     * @throws DataFileParseException on the first fault listed above; the message names the file
     *                                and the line the record starts on, or the curse's name for a
     *                                conflict fault
     */
    @Override
    public List<Curse> assemble(@NotNull List<CurseParseRecord> records, @NotNull List<String> errors,
                                String filename) {
        List<Curse> results = new ArrayList<>();

        int curseIndex = 0;
        int numCurses = 0;
        
        // First pass
        for (CurseParseRecord record : records) {
            int line = record.line();
            numCurses++;
            if (numCurses > MAX_CURSES) {
                String msg = "Too many Curses found starting at line: " + line + " of file: " + filename;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            String name = record.name();
            if (name == null || name.isEmpty()) {
                String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid name value: " + name;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            List<TValue> types = new ArrayList<>();
            for (String tVal : record.type()) {
                TValue tValue = TValue.fromName(tVal);
                if (tValue != null) {
                    types.add(tValue);
                } else {
                    String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid tValue value: " + tVal;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            // weight: is optional — an absent line arrives blank and means no
            // adjustment (0), which must not be parsed as a malformed integer.
            int weightAdjustment = 0;
            if (!record.weightAdjustment().isEmpty()) {
                weightAdjustment = ParseUtils.parseIntString("Curse", record.weightAdjustment(), line, filename, "weight adjustment");
                if (weightAdjustment < Short.MIN_VALUE || weightAdjustment > Short.MAX_VALUE) {
                    String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid weight adjustment (out of range) value: " + weightAdjustment;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            // EffectAssembler is all-or-nothing: a null return means at least one effect
            // in the block failed to resolve (its messages are already in errors). C's
            // parse_curse_effect returns grab_effect_data's error, which stops the load, so this does too.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            if (effects == null && !record.effects().isEmpty()) {
                String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid effect list starting at line: " + record.effects().getFirst().line();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }            
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            Flag<ObjectFlag> objectFlags = new Flag<>(ObjectFlag.class);
            // flags: line fans out — HATES_/IGNORE_ set element flags on elInfo,
            // everything else is an object flag.
            for (String flag : record.flags()) {
                if (flag.startsWith("HATES_") || flag.startsWith("IGNORE_")) {
                    boolean hates = flag.startsWith("HATES_");
                    String elemName = "ELEM_" + flag.substring(hates ? 6 : 7);
                    try {
                        ElementEnum elementEnum = ElementEnum.valueOf(elemName);
                        ElementInfo info = elInfo.computeIfAbsent(elementEnum, k -> new ElementInfo());
                        info.on(hates ? ElementInfoEnum.EL_INFO_HATES : ElementInfoEnum.EL_INFO_IGNORE);
                    } catch (IllegalArgumentException e) {
                        String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid Element flag value: " + flag;
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                } else {
                    ObjectFlag objectFlag;
                    try {
                        objectFlag = ObjectFlag.valueOf("OF_" + flag);
                        objectFlags.on(objectFlag);
                    } catch (IllegalArgumentException e) {
                        String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid object flag value: " + flag;
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                }
            }
            Map<ObjectModifier, Integer> modifiers = new HashMap<>();
            // values: line fans out — RES_* set a per-element resistance level on
            // elInfo, everything else is an additive object modifier.
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                if (key.startsWith("RES_")) {
                    String elemFlag = "ELEM_" + key.substring(4);
                    try {
                        ElementEnum element = ElementEnum.valueOf(elemFlag);
                        int val = ParseUtils.parseIntString("Curse", value, line, filename, "integer");
                        ElementInfo info = elInfo.computeIfAbsent(element, k -> new ElementInfo());
                        info.setResLevel(val);
                    } catch (IllegalArgumentException e) {
                        String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid Element value: " + key + "[" + value + "]";
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                } else {
                    try {
                        ObjectModifier om = ObjectModifier.valueOf("OM_" + key);
                        int val = ParseUtils.parseIntString("Curse", value, line, filename, "integer");
                        modifiers.put(om, val);
                    } catch (IllegalArgumentException e) {
                        String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid Object Modifier flag value: " + key + "[" + value + "]";
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                }
            }
            int toh = ParseUtils.parseIntString("Curse", record.combatToH(), line, filename, "combat to Hit");
            int tod = ParseUtils.parseIntString("Curse", record.combatToD(), line, filename, "combat to Damage");
            int toa = ParseUtils.parseIntString("Curse", record.combatToA(), line, filename, "combat to AC");
            List<String> conflictingCurses = new ArrayList<>(record.conflict());
            Flag<ObjectFlag> cFlags = new Flag<>(ObjectFlag.class);
            for (String cFlag : record.cFlag()) {
                try {
                    ObjectFlag objectFlag = ObjectFlag.valueOf("OF_" + cFlag);
                    cFlags.on(objectFlag);
                } catch (IllegalArgumentException e) {
                    String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid conflict flag value: " + cFlag;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String desc : record.desc())
                sb.append(desc);
            String description = sb.toString();
            String message = record.message();

            if (objectFlags.has(ObjectFlag.OF_MULTIPLY_WEIGHT) && weightAdjustment < 0) {
                String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                        "a negative weight adjustment and a multiply weight flag";
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            String timeString = record.time();
            Random time = Random.Zero();
            if (!timeString.isEmpty()) {
                time = Random.parseStr(timeString);
                if (time == null) {
                    String msg = "Curse starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid time value: " + record.time();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            
            ItemObject itemObject = new ItemObject();
            itemObject.setWeight(weightAdjustment);
            itemObject.setEffect(effects);
            itemObject.setFlagsTo(objectFlags);
            itemObject.setModifiers(modifiers);
            itemObject.setElInfo(elInfo);
            itemObject.setToHit(toh);
            itemObject.setToDam(tod);
            itemObject.setToAC(toa);
            itemObject.setEffectMessage(message);
            itemObject.setTime(time);

            results.add(new Curse(name, types, itemObject,
                    conflictingCurses, cFlags,
                    description, curseIndex));
            curseIndex++;
        }

        // Reverse the list to make it fit the same order as C
        results = results.reversed();

        int index = 0;
        for (Curse curse : results) {
            curse.setIndex(index);
            index++;
        }

        // Second pass - link the conflicting curses
        for (Curse curse : results) {
            List<Curse> conflicts = new ArrayList<>();

            for (String conflictName : curse.getConflictNames()) {
                Curse c = results.stream()
                        .filter(r -> r.getName().equals(conflictName))
                        .findFirst().orElse(null);
                if (c == null) {
                    String message = "Curse: " + curse.getName() + " of file: " + filename + " has " +
                            "an invalid conflict value: " + conflictName;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                conflicts.add(c);
            }

            curse.setConflict(conflicts);
        }

        return results;
    }
}
