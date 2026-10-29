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

package uk.co.jackoftradesltd.backend.parser.playerrace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryChart;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assembles the raw {@link PlayerRaceParseRecord}s parsed from {@code p_race.txt} into the
 * finished {@link PlayerRace} domain objects (reader &rarr; ParseRecord &rarr; assembler
 * &rarr; domain).
 *
 * <p>This is the Java form of the {@code parse_p_race_*} handlers in {@code init.c}, plus the
 * numbering half of {@code finish_parse_p_race}. The grammar has already split the file into
 * records, so what is left to do here is what C's handlers do with each value: read the numbers,
 * turn the flag, skill, stat and resist names into their enums, and look up the history chart.
 *
 * <p>Where C reports a parser error, which ends the load, this class throws a
 * {@link DataFileParseException}. That covers an unreadable number, an unknown or sentinel flag,
 * skill, stat or element name, a {@code values:} name without the {@code RES_} prefix, and a
 * negative {@code history:} (C reads it as a {@code uint}).
 *
 * <p>Where C does not report an error, this class does not either, and takes C's zeroed record as
 * the default. A race with no {@code stats:} line has all five stat adjustments 0, a race with
 * no {@code skill-*} line has that skill 0, a missing scalar line leaves its field 0, and a
 * {@code history:} that names no chart, or no {@code history:} line at all, gives a null history.
 *
 * <p>Class PlayerRaceAssembler coded before 260915, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class PlayerRaceAssembler implements Assembler<PlayerRaceParseRecord, List<PlayerRace>> {
    /**
     * Logger for this type; every fatal data-file error is logged here before the exception is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(PlayerRaceAssembler.class);

    /**
     * Assembles every parsed race record into a {@link PlayerRace}, in file order.
     *
     * <p>Each record's scalar fields are read with {@link ParseUtils#parseIntString}, which gives 0
     * for the empty text a missing line leaves. The stat and skill maps start with every real stat
     * and skill at 0, as C's zeroed record does, and the parsed values overwrite them; a repeated
     * line takes the later value. Object and player flags are resolved against the {@code "OF_"} and
     * {@code "PF_"} prefixes and switched on, so repeated lines accumulate. A resist name must start
     * with {@code RES_} and its remainder is resolved against {@code ELEM_}; only elements the race
     * names get an entry, so the resist map is sparse, and an element left out reads as 0.
     *
     * <p>C's flag and element tables start past their {@code NONE} entry and have no {@code MAX}, so
     * {@code OF_NONE}, {@code OF_MAX}, {@code PF_NONE}, {@code ELEM_NONE} and {@code ELEM_MAX} are
     * refused here although the Java enums carry them. {@code PF_MAX} is not in {@code PlayerFlag}
     * and fails as an unknown name.
     *
     * <p>The history chart is looked up by number through {@link PlayerRegistry}. A number that
     * names no chart gives a null history, which is what C's {@code findchart} stores. The body is
     * always the one at index 0, a copy of the first body in {@code body.txt}, as C sets
     * {@code r->body = 0}. The race index is its position in the file, 0 for the first race, which
     * is how {@code finish_parse_p_race} numbers {@code ridx}.
     *
     * <p>Function assemble coded before 260915, updated on 261029 to match C on the race index,
     * history, sentinels, resist prefix and absent stat and skill lines, commented in full on 261029.
     *
     * @param records  the raw race records, in file order
     * @param errors   not used: the {@link Assembler} contract carries a soft-error list, but every
     *                 failure here ends the load by exception, as C's parser errors do
     * @param filename the file being read, named in every error message
     * @return one race per record, in file order
     * @throws DataFileParseException    for any value C's parser would reject
     * @throws IllegalStateException     if the body or history registries have not been loaded
     * @throws IndexOutOfBoundsException if no body has been loaded at index 0
     */
    @Override
    public List<PlayerRace> assemble(@NotNull List<PlayerRaceParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<PlayerRace> races = new ArrayList<>();
        
        int index = 0;

        for (PlayerRaceParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            int hitdie = ParseUtils.parseIntString("Player race", record.hitdie(), line, filename, "hit die");
            int exp = ParseUtils.parseIntString("Player race", record.exp(), line, filename, "experience");
            int baseAge = ParseUtils.parseIntString("Player race", record.ageBase(), line, filename, "base age");
            int ageModifier = ParseUtils.parseIntString("Player race", record.ageModifier(), line, filename, "age modifier");
            int baseHeight = ParseUtils.parseIntString("Player race", record.baseHeight(), line, filename, "base height");
            int heightModifier = ParseUtils.parseIntString("Player race", record.heightModifier(), line, filename, "height modifier");
            int baseWeight = ParseUtils.parseIntString("Player race", record.baseWeight(), line, filename, "base weight");
            int weightModifier = ParseUtils.parseIntString("Player race", record.weightModifier(), line, filename, "weight modifier");
            int infravision = ParseUtils.parseIntString("Player race", record.infraVision(), line, filename, "infravision");
            PlayerBody body = PlayerRegistry.lookupPlayerBody(0);
            Map<Stats, Integer> statsAdj = new HashMap<>();
            for (Stats stat : Stats.values()) {
                if (stat == Stats.STAT_MAX || stat == Stats.STAT_NONE)
                    continue;
                statsAdj.put(stat, 0);
            }
            for (String stat : record.stats().keySet()) {
                String statValue = record.stats().get(stat);
                try {
                    Stats key = Stats.valueOf(stat);
                    int value = ParseUtils.parseIntString("Player race", statValue, line, filename, "stat integer");
                    statsAdj.put(key, value);
                } catch (IllegalArgumentException e) {
                    String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal stat value: " + stat;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Map<PlayerSkill, Integer> skillsAdj = new HashMap<>();
            for (PlayerSkill skill : PlayerSkill.values()) {
                if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX)
                    continue;
                skillsAdj.put(skill, 0);
            }
            for (String skillName : record.playerSkills().keySet()) {
                String skillValueStr = record.playerSkills().get(skillName);
                try {
                    int skillValue = ParseUtils.parseIntString("Player race", skillValueStr, line, filename, "skill"); 
                    PlayerSkill skill = PlayerSkill.valueOf(skillName);
                    skillsAdj.put(skill, skillValue);
                } catch (IllegalArgumentException e) {
                    String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal skill name value: " + skillName;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            for (String oFlag : record.objectFlags()) {
                try {
                    ObjectFlag flag = ObjectFlag.valueOf("OF_" + oFlag);
                    if (flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX) {
                        String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel object flag value: " + oFlag;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    oFlags.on(flag);
                } catch (IllegalArgumentException e) {
                    String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal object flag value: " + oFlag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
            for (String pFlag : record.playerFlags()) {
                try {
                    PlayerFlag flag = PlayerFlag.valueOf("PF_" + pFlag);
                    if (flag == PlayerFlag.PF_NONE) {
                        String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel player flag value: " + pFlag;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);    
                    }
                    pFlags.on(flag);
                } catch (IllegalArgumentException e) {
                    String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal player flag value: " + pFlag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            if (ParseUtils.minusFound(record.history())) {
                String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative player history value: " + record.history();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int historyChart = ParseUtils.parseIntString("Player race", record.history(), line, filename, "history chart");
            PlayerHistoryChart history = PlayerRegistry.lookupPlayerHistoryChart(historyChart);
            Map<ElementEnum, ElementInfo> resists = new HashMap<>();
            for (String resistTag : record.values().keySet()) {
                String resistValue = record.values().get(resistTag);
                if (!resistTag.startsWith("RES_")) {
                    String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal resist value: " + resistTag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                try {
                    int resistVal = ParseUtils.parseIntString("Player race", resistValue, line, filename, "resist integer");
                    ElementEnum element = ElementEnum.valueOf("ELEM_" + resistTag.substring(4));
                    if (element == ElementEnum.ELEM_MAX || element == ElementEnum.ELEM_NONE) {
                        String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel element value: " + resistTag.substring(4);
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    ElementInfo info = resists.computeIfAbsent(element, k -> new ElementInfo());
                    info.setResLevel(resistVal);
                } catch (IllegalArgumentException e) {
                    String msg = "Player race starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal resist name value: " + resistTag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }

            races.add(new PlayerRace(name, index, hitdie, exp, baseAge,
                    ageModifier, baseHeight, heightModifier, baseWeight,
                    weightModifier, infravision, body, statsAdj,
                    skillsAdj, oFlags, pFlags, history, resists));
            
            index++;
        }

        return races;
    }
}
