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

package uk.co.jackoftrades.backend.parser.shape;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.player.PlayerBlow;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the raw {@link ShapeParseRecord}s from {@code ShapeGrammar} into domain
 * {@link PlayerShape}s (Java port of {@code parse_shape_*} in {@code init.c}).
 *
 * <p>This is where interpretation happens: integers are parsed, enum tokens are resolved, and the
 * {@code values:} line is fanned out into its two C destinations — the {@code obj_mods} half
 * (unprefixed, e.g. {@code STR}, {@code SPEED}) into a {@code Map<}{@link ObjectModifier}{@code ,
 * Integer>}, and the {@code RES_}-prefixed half into a {@code Map<}{@link ElementEnum}{@code ,}
 * {@link ElementInfo}{@code >} carrying the per-element resistance level. Effects are delegated to
 * {@link EffectAssembler}.
 *
 * <p><b>Error policy:</b> soft errors follow the suite's skip-and-continue contract — an
 * unresolvable flag, modifier or element name logs a message to {@code errors} and drops that one
 * shape ({@code continue}), leaving the rest of the file to load. The plain {@code errors} list is
 * mutated in place; hard grammar/lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * @author Rowan Crowther
 */
public class ShapeAssembler implements Assembler<ShapeParseRecord, List<PlayerShape>> {

    /**
     * Assembles every parsed shape record into a {@link PlayerShape}, skipping (with a logged
     * soft error) any record whose flags, modifiers or elements fail to resolve.
     *
     * @param records the raw shape records captured by the grammar
     * @param errors  the soft-error channel; assembly failures are appended here and the
     *                offending record is dropped rather than aborting the whole file
     * @return the successfully assembled shapes, in source order
     */
    @Override
    public List<PlayerShape> assemble(@NotNull List<ShapeParseRecord> records, @NotNull List<String> errors) {
        List<PlayerShape> shapes = new ArrayList<>();

        for (ShapeParseRecord record : records) {
            int line = record.line();

            String name = record.name();
            String tohStr = record.combatToh();
            int toh = 0;
            if (!tohStr.isEmpty()) {
                try {
                    toh = Integer.parseInt(tohStr);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid combat to hit value: " + tohStr);
                    continue;
                }
            }
            String todStr = record.combatTod();
            int tod = 0;
            if (!todStr.isEmpty()) {
                try {
                    tod = Integer.parseInt(todStr);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid combat to dam value: " + todStr);
                    continue;
                }
            }
            String toaStr = record.combatToa();
            int toa = 0;
            if (!toaStr.isEmpty()) {
                try {
                    toa = Integer.parseInt(toaStr);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid combat to AC value: " + toaStr);
                    continue;
                }
            }
            Map<PlayerSkill, Integer> skills = new HashMap<>();
            String sdp = record.skillDisarmPhys();
            int skillDisarmPhys = 0;
            if (!sdp.isEmpty()) {
                try {
                    skillDisarmPhys = Integer.parseInt(sdp);
                    skills.put(PlayerSkill.SKILL_DISARM_PHYS, skillDisarmPhys);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill disarm phys value: " + sdp);
                    continue;
                }
            }
            String sdm = record.skillDisarmMagic();
            int skillDisarmMagic = 0;
            if (!sdm.isEmpty()) {
                try {
                    skillDisarmMagic = Integer.parseInt(sdm);
                    skills.put(PlayerSkill.SKILL_DISARM_MAGIC, skillDisarmMagic);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill disarm magic value: " + sdm);
                    continue;
                }
            }
            String ssa = record.skillSave();
            int skillSave = 0;
            if (!ssa.isEmpty()) {
                try {
                    skillSave = Integer.parseInt(ssa);
                    skills.put(PlayerSkill.SKILL_SAVE, skillSave);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill save value: " + ssa);
                    continue;
                }
            }
            String sse = record.skillSearch();
            int skillSearch = 0;
            if (!sse.isEmpty()) {
                try {
                    skillSearch = Integer.parseInt(sse);
                    skills.put(PlayerSkill.SKILL_SEARCH, skillSearch);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill search value: " + sse);
                    continue;
                }
            }
            String sst = record.skillStealth();
            int skillStealth = 0;
            if (!sst.isEmpty()) {
                try {
                    skillStealth = Integer.parseInt(sst);
                    skills.put(PlayerSkill.SKILL_STEALTH, skillStealth);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill stealth value: " + sst);
                    continue;
                }
            }
            String sme = record.skillMelee();
            int skillMelee = 0;
            if (!sme.isEmpty()) {
                try {
                    skillMelee = Integer.parseInt(sme);
                    skills.put(PlayerSkill.SKILL_TO_HIT_MELEE, skillMelee);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill melee value: " + sme);
                    continue;
                }
            }
            String sth = record.skillThrow();
            int skillThrow = 0;
            if (!sth.isEmpty()) {
                try {
                    skillThrow = Integer.parseInt(sth);
                    skills.put(PlayerSkill.SKILL_TO_HIT_THROW, skillThrow);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill throw value: " + sth);
                    continue;
                }
            }
            String sdg = record.skillDig();
            int skillDig = 0;
            if (!sdg.isEmpty()) {
                try {
                    skillDig = Integer.parseInt(sdg);
                    skills.put(PlayerSkill.SKILL_DIGGING, skillDig);
                } catch (NumberFormatException e) {
                    errors.add("Shape beginning line: " + line + " has " +
                            "an invalid skill dig value: " + sdg);
                    continue;
                }
            }
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            boolean illegalOFlag = false;
            for (String oFlag : record.oFlags()) {
                ObjectFlag objectFlag = null;
                try {
                    objectFlag = ObjectFlag.valueOf("OF_" + oFlag);
                    oFlags.on(objectFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Shape beginning at line: " + line + " has " +
                            "an invalid object flag: " + oFlag);
                    illegalOFlag = true;
                }
            }
            if (illegalOFlag) continue;
            Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
            boolean illegalPFlag = false;
            for (String pFlag : record.pFlags()) {
                PlayerFlag playerFlag = null;
                try {
                    playerFlag = PlayerFlag.valueOf("PF_" + pFlag);
                    pFlags.on(playerFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Shape beginning at line: " + line + " has " +
                            "an invalid player flag: " + pFlag);
                    illegalPFlag = true;
                }
            }
            if (illegalPFlag) continue;
            Map<ObjectModifier, Integer> values = new HashMap<>();
            Map<ElementEnum, ElementInfo> elementValues = new HashMap<>();
            boolean illegalValue = false;
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                String pair = key + "[" + value + "]";
                if (key.startsWith("RES_")) {
                    String keyStr = "ELEM_" + key.substring(4);
                    try {
                        ElementEnum keyVal = ElementEnum.valueOf(keyStr);
                        ElementInfo info = elementValues.computeIfAbsent(keyVal, k -> new ElementInfo());
                        info.setResLevel(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        errors.add("Shape beginning at line: " + line + " has " +
                                "an invalid element value: " + pair);
                        illegalValue = true;
                    } catch (IllegalArgumentException e) {
                        errors.add("Shape beginning at line: " + line + " has " +
                                "an invalid element name: " + pair);
                        illegalValue = true;
                    }
                } else {
                    String omStr = "OM_" + key;
                    try {
                        ObjectModifier omMod = ObjectModifier.valueOf(omStr);
                        int intVal = Integer.parseInt(value);
                        values.put(omMod, intVal);
                    } catch (NumberFormatException e) {
                        errors.add("Shape beginning at line: " + line + " has " +
                                "an invalid modifier value: " + pair);
                        illegalValue = true;
                    } catch (IllegalArgumentException e) {
                        errors.add("Shape beginning at line: " + line + " has " +
                                "an invalid modifier name: " + pair);
                        illegalValue = true;
                    }
                }
            }
            if (illegalValue) continue;
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors);
            List<PlayerBlow> blowList = new ArrayList<>();
            for (String blow : record.blowMethod()) {
                blowList.add(new PlayerBlow(blow));
            }

            shapes.add(new PlayerShape(name, toa, toh, tod, skills, oFlags, pFlags,
                    values, elementValues, effects,
                    record.blowMethod().size(), blowList));
        }

        return shapes;
    }
}
