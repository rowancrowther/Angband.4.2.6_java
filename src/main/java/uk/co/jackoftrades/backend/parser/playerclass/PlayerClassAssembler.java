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

package uk.co.jackoftrades.backend.parser.playerclass;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerClass;
import uk.co.jackoftrades.middle.player.StartItem;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The top-level assembler for {@code class.txt}: it turns each raw {@link PlayerClassParseRecord}
 * into a domain {@link PlayerClass}, delegating the nested structures to the specialised
 * assemblers — {@link ClassEquipAssembler} for starting gear and {@link ClassMagicAssembler} (which
 * fans out to books, spells and effects) for casters.
 *
 * <p>It resolves the file's own token names to enums ({@link Stats}, {@link PlayerSkill},
 * {@link ObjectFlag}, {@link PlayerFlag}) and parses every scalar. Following the shared soft-error
 * contract, any record with an unknown enum name or a malformed integer is reported to
 * {@code errors} and skipped with {@code continue}, so one broken class never sinks the rest of the
 * file. A non-caster's {@link ClassMagic} is normalised to the {@link ClassMagic#NONE} sentinel
 * rather than left null.
 *
 * @author Rowan Crowther
 */
public class PlayerClassAssembler implements Assembler<PlayerClassParseRecord, List<PlayerClass>> {
    /**
     * Assembles every parsed class record into a {@link PlayerClass}, resolving its stats, skills,
     * flags and scalars, and building its equipment and (for casters) magic through the nested
     * assemblers.
     *
     * @param records the raw class records, in file order
     * @param errors  the soft-error channel; every unknown name or malformed value is appended here
     * @return the resolved classes, minus any dropped for a resolution failure
     * @author Rowan Crowther
     */
    @Override
    public List<PlayerClass> assemble(@NotNull List<PlayerClassParseRecord> records, @NotNull List<String> errors) {
        List<PlayerClass> playerClasses = new ArrayList<>();
        List<StartItem> startItems;

        for (PlayerClassParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            Map<Stats, Integer> stats = new HashMap<>();
            boolean illegalStat = false;
            for (String key : record.stats().keySet()) {
                String value = record.stats().get(key);
                Stats stat;
                int val = 0;
                try {
                    stat = Stats.valueOf(key);
                    val = Integer.parseInt(value);
                    stats.put(stat, val);
                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed stat integer: " + key);
                    illegalStat = true;
                } catch (IllegalArgumentException e) {
                    errors.add("Line: " + line + " ERROR - CODE INCORRECT RE STAT ENUM NAME");
                    illegalStat = true;
                }
            }
            if (illegalStat) continue;
            Map<PlayerSkill, Integer> baseSkills = new HashMap<>();
            boolean illegalBaseSkill = false;
            for (String key : record.baseSkills().keySet()) {
                String value = record.baseSkills().get(key);
                PlayerSkill skill;
                try {
                    skill = PlayerSkill.valueOf(key);
                    baseSkills.put(skill, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed integer for PlayerSkill: " + key);
                    illegalBaseSkill = true;
                } catch (IllegalArgumentException e) {
                    errors.add("Line: " + line + " ERROR - CODE INCORRECT RE SKILL ENUM NAME");
                    illegalBaseSkill = true;
                }
            }
            if (illegalBaseSkill) continue;
            Map<PlayerSkill, Integer> extraSkills = new HashMap<>();
            boolean illegalExtraSkill = false;
            for (String key : record.skillIncrements().keySet()) {
                String value = record.skillIncrements().get(key);
                PlayerSkill skill;
                try {
                    skill = PlayerSkill.valueOf(key);
                    extraSkills.put(skill, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed integer for PlayerSkillIncrement: " + key);
                    illegalExtraSkill = true;
                } catch (IllegalArgumentException e) {
                    errors.add("Line: " + line + " ERROR - CODE INCORRECT RE SKILL ENUM NAME");
                    illegalExtraSkill = true;
                }
            }
            if (illegalExtraSkill) continue;
            int hpAdj = 0;
            if (!record.hitdie().isEmpty()) {
                try {
                    hpAdj = Integer.parseInt(record.hitdie());
                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed integer for hitdie: " + record.hitdie());
                    continue;
                }
            }
            int expAdj = 0;
            if (!record.exp().isEmpty()) {
                try {
                    expAdj = Integer.parseInt(record.exp());
                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed integer for exp: " + record.exp());
                    continue;
                }
            }
            int maxAttacks = 0;
            if (!record.maxAttacks().isEmpty()) {
                try {
                    maxAttacks = Integer.parseInt(record.maxAttacks());
                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed integer for maxAttacks: " + record.maxAttacks());
                    continue;
                }
            }
            int minWeight = 0;
            if (!record.minWeight().isEmpty()) {
                try {
                    minWeight = Integer.parseInt(record.minWeight());

                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed integer for minWeight: " + record.minWeight());
                    continue;
                }
            }
            int strengthMult = 0;
            if (!record.strengthMult().isEmpty()) {
                try {
                    strengthMult = Integer.parseInt(record.strengthMult());
                } catch (NumberFormatException e) {
                    errors.add("Class beginning at line: " + line + " has " +
                            "a malformed integer for strengthMult: " + record.strengthMult());
                    continue;
                }
            }
            startItems = new ClassEquipAssembler().assemble(record.equipment(), errors);
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            boolean illegalOFlag = false;
            for (String oFlag : record.oFlags()) {
                String flagString = "OF_" + oFlag;
                try {
                    oFlags.on(ObjectFlag.valueOf(flagString));
                } catch (IllegalArgumentException e) {
                    illegalOFlag = true;
                    errors.add("Class beginning at line: " + line + " has " +
                            "an unknown obj-flag: " + oFlag);
                }
            }
            if (illegalOFlag) continue;
            Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
            boolean illegalPFlag = false;
            for (String pFlag : record.pFlags()) {
                String flagString = "PF_" + pFlag;
                try {
                    pFlags.on(PlayerFlag.valueOf(flagString));
                } catch (IllegalArgumentException e) {
                    illegalPFlag = true;
                    errors.add("Class beginning at line: " + line + " has " +
                            "an unknown player-flag: " + pFlag);
                }
            }
            if (illegalPFlag) continue;
            List<String> titles = record.titles();
            List<ClassMagic> magic = new ArrayList<>();
            if (record.magic() != null) {
                List<ClassMagicParseRecord> cmpr = new ArrayList<>();
                cmpr.add(record.magic());
                magic = new ClassMagicAssembler().assemble(cmpr, errors);
            }

            ClassMagic classMagic = magic.isEmpty() ? ClassMagic.NONE : magic.getFirst();

            playerClasses.add(new PlayerClass(name, titles, stats, baseSkills,
                    extraSkills, hpAdj, expAdj, oFlags, pFlags, maxAttacks,
                    minWeight, strengthMult, startItems, classMagic));
        }

        return playerClasses;
    }
}
