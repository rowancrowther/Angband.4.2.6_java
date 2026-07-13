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

package uk.co.jackoftrades.backend.parser.playerrace;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerRaceAssembler implements Assembler<PlayerRaceParseRecord, List<PlayerRace>> {
    /**
     *
     * @param records List of R_ParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of R objects
     */
    @Override
    public List<PlayerRace> assemble(@NotNull List<PlayerRaceParseRecord> records, @NotNull List<String> errors) {
        List<PlayerRace> races = new ArrayList<>();

        for (PlayerRaceParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            int hitdie = 0;
            if (!record.hitdie().isEmpty()) {
                try {
                    hitdie = Integer.parseInt(record.hitdie());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid hitdie value: " + record.hitdie());
                    continue;
                }
            }
            int exp = 0;
            if (!record.exp().isEmpty()) {
                try {
                    exp = Integer.parseInt(record.exp());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid exp value: " + record.exp());
                    continue;
                }
            }
            int baseAge = 0;
            if (!record.ageBase().isEmpty()) {
                try {
                    baseAge = Integer.parseInt(record.ageBase());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid base age value: " + record.ageBase());
                    continue;
                }
            }
            int ageModifier = 0;
            if (!record.ageModifier().isEmpty()) {
                try {
                    ageModifier = Integer.parseInt(record.ageModifier());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid age modifier value: " + record.ageModifier());
                    continue;
                }
            }
            int baseHeight = 0;
            if (!record.baseHeight().isEmpty()) {
                try {
                    baseHeight = Integer.parseInt(record.baseHeight());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid base height value: " + record.baseHeight());
                    continue;
                }
            }
            int heightModifier = 0;
            if (!record.heightModifier().isEmpty()) {
                try {
                    heightModifier = Integer.parseInt(record.heightModifier());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid height modifier value: " + record.heightModifier());
                    continue;
                }
            }
            int baseWeight = 0;
            if (!record.baseWeight().isEmpty()) {
                try {
                    baseWeight = Integer.parseInt(record.baseWeight());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid base weight value: " + record.baseWeight());
                    continue;
                }
            }
            int weightModifier = 0;
            if (!record.weightModifier().isEmpty()) {
                try {
                    weightModifier = Integer.parseInt(record.weightModifier());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid weight modifier value: " + record.weightModifier());
                    continue;
                }
            }
            int infravision = 0;
            if (!record.infraVision().isEmpty()) {
                try {
                    infravision = Integer.parseInt(record.infraVision());
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid infravision value: " + record.infraVision());
                    continue;
                }
            }
            PlayerBody body = GameConstants.lookupPlayerBody(0);
            Map<Stats, Integer> statsAdj = new HashMap<Stats, Integer>();
            boolean statError = false;
            for (String stat : record.stats().keySet()) {
                String statValue = record.stats().get(stat);
                try {
                    Stats key = Stats.valueOf(stat);
                    int value = Integer.parseInt(statValue);
                    statsAdj.put(key, value);
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid stat value for " +
                            stat.substring(5).toLowerCase() +
                            ": " + statValue);
                    statError = true;
                } catch (IllegalArgumentException e) {
                    errors.add("ILLEGAL STAT NAME IN CODE: " + stat);
                    statError = true;
                }
            }
            if (statError) continue;
            Map<PlayerSkill, Integer> skillsAdj = new HashMap<>();
            boolean skillError = false;
            for (String skillName : record.playerSkills().keySet()) {
                String skillValueStr = record.playerSkills().get(skillName);
                try {
                    int skillValue = Integer.parseInt(skillValueStr);
                    PlayerSkill skill = PlayerSkill.valueOf(skillName);
                    skillsAdj.put(skill, skillValue);
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid skill value for " +
                            skillName + ": " + skillValueStr);
                    skillError = true;
                } catch (IllegalArgumentException e) {
                    errors.add("ILLEGAL SKILL NAME IN CODE: " + skillName);
                    skillError = true;
                }
            }
            if (skillError) continue;
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            boolean oFlagError = false;
            for (String oFlag : record.objectFlags()) {
                try {
                    ObjectFlag flag = ObjectFlag.valueOf("OF_" + oFlag);
                    oFlags.on(flag);
                } catch (IllegalArgumentException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid object flag value: " + oFlag);
                    oFlagError = true;
                }
            }
            if (oFlagError) continue;
            Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
            boolean pFlagError = false;
            for (String pFlag : record.playerFlags()) {
                try {
                    PlayerFlag flag = PlayerFlag.valueOf("PF_" + pFlag);
                    pFlags.on(flag);
                } catch (IllegalArgumentException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid player flag value: " + pFlag);
                    pFlagError = true;
                }
            }
            if (pFlagError) continue;
            PlayerHistoryChart history;
            try {
                int historyChart = Integer.parseInt(record.history());
                history = GameConstants.lookupPlayerHistoryChart(historyChart);
                if (history == null) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid history chart value: " + historyChart);
                    continue;
                }
            } catch (NumberFormatException e) {
                errors.add("Race beginning at line: " + line + " has " +
                        "an invalid history chart integer format: " + record.history());
                continue;
            }
            Map<ElementEnum, ElementInfo> resists = new HashMap<>();
            boolean resistError = false;
            for (String resistTag : record.values().keySet()) {
                String resistValue = record.values().get(resistTag);
                try {
                    int resistVal = Integer.parseInt(resistValue);
                    ElementEnum element = ElementEnum.valueOf("ELEM_" + resistTag.substring(4));
                    ElementInfo info = resists.computeIfAbsent(element, k -> new ElementInfo());
                    info.setResLevel(resistVal);
                } catch (NumberFormatException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid resist value: " + resistValue);
                    resistError = true;
                } catch (IllegalArgumentException e) {
                    errors.add("Race beginning at line: " + line + " has " +
                            "an invalid resist name: " + resistTag);
                    resistError = true;
                }
            }
            if (resistError) continue;

            races.add(new PlayerRace(name, 0, hitdie, exp, baseAge,
                    ageModifier, baseHeight, heightModifier, baseWeight,
                    weightModifier, infravision, body, statsAdj,
                    skillsAdj, oFlags, pFlags, history, resists));
        }

        return races;
    }
}
