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

package uk.co.jackoftradesltd.backend.parser.shape;

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
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.PlayerBlow;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

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
 * <p><b>Error policy:</b> the first thing that does not resolve ends the load. A number that does
 * not parse, an unknown or sentinel flag name, an unknown or sentinel {@code values:} name, or an
 * effect that does not resolve throws a {@link DataFileParseException} naming the file and the
 * line the shape starts on, and no shape is returned. This is C's behaviour: each
 * {@code parse_shape_*} handler returns a parse error and {@code init.c} quits on the first one.
 * Hard grammar and lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * <p><b>Order:</b> the shapes come back in file order, so a shape's position in the list is the
 * {@code sidx} C gives it ({@code z_info->shape_max++} in {@code parse_shape_name}). C also keeps
 * its {@code shapes} list in reverse file order; {@code PlayerRegistry} records that difference as
 * deliberate and finds a shape by position where C searches for the {@code sidx}. Each shape's
 * blows are the other way round: {@code parse_shape_blow} pushes every {@code blow:} line on the
 * head of the chain, and the player-attack code walks that chain from the head, so the blows are
 * held in reverse file order and index 0 is the last {@code blow:} line.
 *
 * <p>Class ShapeAssembler coded before 260915, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class ShapeAssembler implements Assembler<ShapeParseRecord, List<PlayerShape>> {
    /**
     * Logger the assembler writes to before it throws: every failure is logged at fatal level with
     * the same message the {@link DataFileParseException} carries.
     *
     * <p>Field logger coded before 260915, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(ShapeAssembler.class);

    /**
     * Assembles every parsed shape record into a {@link PlayerShape}, in file order. The port of
     * the {@code parse_shape_*} handlers in {@code init.c}, run record by record.
     *
     * <p>Per record: the three {@code combat:} figures and the eight {@code skill-*} figures are
     * parsed as integers. An absent line reaches here as an empty string and is 0, which is what
     * C's zero-filled {@code struct player_shape} holds. The skills map always holds all eight
     * skills (disarm physical, disarm magic, save, search, stealth, melee, throw, digging), zeros
     * included. Any other skill is absent here and 0 in C's array.
     *
     * <p>Each {@code obj-flags:} and {@code player-flags:} name is resolved against
     * {@link ObjectFlag} and {@link PlayerFlag} with the prefix put back, case-exactly as C's
     * {@code streq} compares. {@code NONE} is refused for both and {@code MAX} for the object
     * flags, since C's lookup starts past the {@code NONE} slot and the tables have no {@code MAX}.
     * Each {@code values:} entry whose name starts {@code RES_} sets the resistance level of that
     * element; any other name is an {@link ObjectModifier} (the stats first, then the modifiers),
     * and its figure is read as a decimal. The {@code NONE} and {@code MAX} names are refused for
     * both. The effects go to {@link EffectAssembler}, and the blows are described in the class
     * comment. The blow count is the number of {@code blow:} lines, duplicates included.
     *
     * <p>The shape's {@code sidx} is not stored, as {@link PlayerShape} describes, and the count
     * C keeps in {@code z_info->shape_max} is taken from the size of this list by
     * {@code PlayerRegistry.setPlayerShape}.
     *
     * <p>Function assemble coded before 260915, commented in full on 261029.
     *
     * @param records  the raw shape records captured by the grammar
     * @param errors   passed to {@link EffectAssembler#assemble}, which no longer appends to it;
     *                 this method adds nothing to it either
     * @param filename the data file the records came from, for the error messages
     * @return the assembled shapes in file order, one per record
     * @throws DataFileParseException if a number, flag name, {@code values:} name or effect does
     *                                not resolve
     */
    @Override
    public List<PlayerShape> assemble(@NotNull List<ShapeParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<PlayerShape> shapes = new ArrayList<>();

        for (ShapeParseRecord record : records) {
            int line = record.line();

            String name = record.name();
            int toh = ParseUtils.parseIntString("Shape", record.combatToh(), line, filename, "combat to hit");
            int tod = ParseUtils.parseIntString("Shape", record.combatTod(), line, filename, "combat to damage");
            int toa = ParseUtils.parseIntString("Shape", record.combatToa(), line, filename, "combat to AC");
            Map<PlayerSkill, Integer> skills = new HashMap<>();
            int skillDisarmPhys = ParseUtils.parseIntString("Shape", record.skillDisarmPhys(), line, filename, "disarm physical skill");
            skills.put(PlayerSkill.SKILL_DISARM_PHYS, skillDisarmPhys);
            int skillDisarmMagic = ParseUtils.parseIntString("Shape", record.skillDisarmMagic(), line, filename, "disarm magic skill");
            skills.put(PlayerSkill.SKILL_DISARM_MAGIC, skillDisarmMagic);
            int skillSave = ParseUtils.parseIntString("Shape", record.skillSave(), line, filename, "save skill");
            skills.put(PlayerSkill.SKILL_SAVE, skillSave);
            int skillSearch = ParseUtils.parseIntString("Shape", record.skillSearch(), line, filename, "search skill");
            skills.put(PlayerSkill.SKILL_SEARCH, skillSearch);
            int skillStealth = ParseUtils.parseIntString("Shape", record.skillStealth(), line, filename, "stealth skill");
            skills.put(PlayerSkill.SKILL_STEALTH, skillStealth);
            int skillMelee = ParseUtils.parseIntString("Shape", record.skillMelee(), line, filename, "melee skill");
            skills.put(PlayerSkill.SKILL_TO_HIT_MELEE, skillMelee);
            int skillThrow = ParseUtils.parseIntString("Shape", record.skillThrow(), line, filename, "throw skill");
            skills.put(PlayerSkill.SKILL_TO_HIT_THROW, skillThrow);
            int skillDig = ParseUtils.parseIntString("Shape", record.skillDig(), line, filename, "digging skill");
            skills.put(PlayerSkill.SKILL_DIGGING, skillDig);
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            for (String oFlag : record.oFlags()) {
                ObjectFlag objectFlag;
                try {
                    objectFlag = ObjectFlag.valueOf("OF_" + oFlag);
                    if (objectFlag == ObjectFlag.OF_NONE || objectFlag == ObjectFlag.OF_MAX) {
                        String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel object flag value: " + oFlag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    oFlags.on(objectFlag);
                } catch (IllegalArgumentException e) {
                    String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal object flag value: " + oFlag;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
            for (String pFlag : record.pFlags()) {
                PlayerFlag playerFlag;
                try {
                    playerFlag = PlayerFlag.valueOf("PF_" + pFlag);
                    if (playerFlag == PlayerFlag.PF_NONE) {
                        String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel player flag value: " + pFlag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    pFlags.on(playerFlag);
                } catch (IllegalArgumentException e) {
                    String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal player flag value: " + pFlag;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            Map<ObjectModifier, Integer> values = new HashMap<>();
            Map<ElementEnum, ElementInfo> elementValues = new HashMap<>();
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                if (key.startsWith("RES_")) {
                    String keyStr = "ELEM_" + key.substring(4);
                    try {
                        ElementEnum keyVal = ElementEnum.valueOf(keyStr);
                        if (keyVal == ElementEnum.ELEM_NONE || keyVal == ElementEnum.ELEM_MAX) {
                            String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel element value: " + key;
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                        }
                        int resLevel = ParseUtils.parseIntString("Shape", value, line, filename, "resist element", ParseUtils.Radix.DECIMAL);
                        ElementInfo info = elementValues.computeIfAbsent(keyVal, k -> new ElementInfo());
                        info.setResLevel(resLevel);
                    } catch (IllegalArgumentException e) {
                        String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal element name value: " + key;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } else {
                    String omStr = "OM_" + key;
                    try {
                        ObjectModifier omMod = ObjectModifier.valueOf(omStr);
                        if (omMod == ObjectModifier.OM_NONE || omMod == ObjectModifier.OM_MAX) {
                            String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel object modifier value: " + key;
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                        }
                        int intVal = ParseUtils.parseIntString("Shape", value, line, filename, "object modifier", ParseUtils.Radix.DECIMAL);
                        values.put(omMod, intVal);
                    } catch (IllegalArgumentException e) {
                        String message = "Shape starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal object modifier name value: " + key;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            // EffectAssembler is all-or-nothing: the first effect that fails to resolve throws,
            // so a shape is never built with an effect silently missing.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            List<PlayerBlow> blowList = new ArrayList<>();
            for (String blow : record.blowMethod()) {
                blowList.addFirst(new PlayerBlow(blow));
            }

            shapes.add(new PlayerShape(name, toa, toh, tod, skills, oFlags, pFlags,
                    values, elementValues, effects,
                    record.blowMethod().size(), blowList));
        }

        return shapes;
    }
}