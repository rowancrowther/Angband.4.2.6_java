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

package uk.co.jackoftrades.backend.parser.bloweffect;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.game.Projection;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.BlowEffect;
import uk.co.jackoftrades.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link BlowEffectParseRecord}s produced by the {@code blow_effects.txt}
 * grammar into domain {@link BlowEffect}s, resolving every string the data file carries
 * into the enum or object it names. This is the Java port of the {@code parse_eff_*}
 * handlers in [C] {@code src/mon-init.c}.
 * <p>
 * Three seams need care, because the C original leans on representation details the port
 * deliberately does not reproduce:
 * <ul>
 *   <li><strong>{@code resist:} is polymorphic.</strong> [C] {@code parse_eff_resist}
 *       ({@code mon-init.c:388-400}) stores it in a single {@code int} whose meaning
 *       depends on {@code effect_type} - an element index for {@code element}, an object
 *       flag index for {@code flag}. The port splits that union into two separately typed
 *       fields, each with a "none" sentinel, so the ambiguity cannot outlive parsing.</li>
 *   <li><strong>{@code lash-type:} never fails.</strong> [C] {@code parse_eff_lash_type}
 *       ({@code mon-init.c:409}) does {@code type >= 0 ? type : PROJ_MISSILE}, so a name it
 *       cannot resolve silently becomes {@code PROJ_MISSILE}. That fallback is load-bearing
 *       for the shipped data, not defensive - see {@code assemble}.</li>
 *   <li><strong>Field order does not matter here.</strong> [C] resolves {@code resist:}
 *       during parsing and therefore requires {@code effect-type:} to appear first in the
 *       file. The grammar accepts the directives in any order and this assembler resolves
 *       from the completed record, so the port has no such ordering constraint.</li>
 * </ul>
 * Malformed records are reported into the {@code errors} list and skipped rather than
 * aborting the load, matching the partial-results contract the other assemblers follow.
 *
 * @author Rowan Crowther
 */
public class BlowEffectAssembler implements Assembler<BlowEffectParseRecord, List<BlowEffect>> {
    /**
     * Assemble the parsed blow-effect records into domain objects.
     *
     * @param records the blow-effect records in file order, as produced by the grammar
     * @param errors  collector for soft failures; a record named here is skipped, and a
     *                non-empty list makes the caller treat the whole file as invalid
     * @return the assembled blow effects, excluding any record reported in {@code errors}
     * @author Rowan Crowther
     */
    @Override
    public List<BlowEffect> assemble(@NotNull List<BlowEffectParseRecord> records, @NotNull List<String> errors) {
        List<BlowEffect> effects = new ArrayList<>();

        for (BlowEffectParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String powerStr = record.power();
            int power = 0;
            if (!powerStr.isEmpty()) {
                try {
                    power = Integer.parseInt(powerStr);
                } catch (NumberFormatException e) {
                    errors.add("Blow Effect at line: " + line + " has " +
                            "a malformed power: " + powerStr);
                    continue;
                }
            }
            String evalStr = record.eval();
            int eval = 0;
            if (!evalStr.isEmpty()) {
                try {
                    eval = Integer.parseInt(evalStr);
                } catch (NumberFormatException e) {
                    errors.add("Blow Effect at line: " + line + " has " +
                            "a malformed eval: " + evalStr);
                    continue;
                }
            }
            String desc = record.desc();
            // An absent lore colour resolves to COLOUR_TYPE_DARK, which is what [C] gets for
            // free from the mem_zalloc'd lore_attr fields (0 == COLOUR_DARK).
            ColourType base = ColourType.getColourType(record.loreColourBase());
            ColourType resist = ColourType.getColourType(record.loreColourResist());
            ColourType immune = ColourType.getColourType(record.loreColourImmune());
            String effectTypeStr = record.effectType();
            // Null for the eight records that ship no effect-type: line at all (NONE, HURT, the
            // four raw elements, SHATTER, BLACK_BREATH). That is legal, so every test below is
            // an equality check that a null simply fails rather than a switch that would throw.
            BlowEffectType effectType = BlowEffectType.getFromString(effectTypeStr);
            String resistStr = record.resist();
            // [C] parse_eff_resist returns PARSE_ERROR_MISSING_BLOW_EFF_TYPE when a resist is
            // given for any effect type other than element or flag, since it would have nothing
            // to resolve the name against.
            if (!resistStr.isEmpty()) {
                if (effectType != BlowEffectType.BET_FLAG && effectType != BlowEffectType.BET_ELEMENT) {
                    errors.add("Blow Effect at line: " + line + " has " +
                            "a resist for a non-resist effect type: " + effectTypeStr +
                            " " + resistStr);
                    continue;
                }
            }
            // Only one of these is ever populated; the other keeps its "none" sentinel, which is
            // how the port replaces the single overloaded int [C] uses for both cases. The data
            // file names flags and elements without their enum prefixes, so those are bridged
            // back on here - "FREE_ACT" -> OF_FREE_ACT, "POIS" -> ELEM_POIS.
            ElementEnum elementEnumResist = ElementEnum.ELEM_NONE;
            ObjectFlag objectFlagResist = ObjectFlag.OF_NONE;
            if (effectType == BlowEffectType.BET_FLAG) {
                try {
                    objectFlagResist = ObjectFlag.valueOf("OF_" + resistStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Blow Effect at line: " + line + " has " +
                            "an illegal Object Flag: " + resistStr);
                    continue;
                }
            } else if (effectType == BlowEffectType.BET_ELEMENT) {
                try {
                    elementEnumResist = ElementEnum.valueOf("ELEM_" + resistStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Blow Effect at line: " + line + " has " +
                            "an illegal Element: " + resistStr);
                    continue;
                }
            }
            String proj = record.lashType();
            Projection lash = null;
            // Unlike every other resolution here, an unknown lash type is not an error: [C]
            // falls back to PROJ_MISSILE. The shipped file depends on it - SHATTER declares
            // lash-type:SHARDS while the projection code is SHARD, upstream included - so both
            // the unknown-name and no-such-projection paths land on MISSILE rather than adding
            // to errors. ProjectionEnum spans elements then projections, mirroring the merged
            // list [C] searches in proj_name_to_idx (project.c:48-56).
            if (!proj.isEmpty()) {
                ProjectionEnum projEnum;
                try {
                    projEnum = ProjectionEnum.valueOf("PROJ_" + proj);
                    lash = GameConstants.lookupProjectionByLash(projEnum);
                    if (lash == null) {
                        lash = GameConstants.lookupProjectionByLash(ProjectionEnum.PROJ_MISSILE);
                    }
                } catch (IllegalArgumentException e) {
                    lash = GameConstants.lookupProjectionByLash(ProjectionEnum.PROJ_MISSILE);
                }
            }

            effects.add(new BlowEffect(name, power, eval, desc, base,
                    resist, immune, effectType, objectFlagResist,
                    elementEnumResist, lash));
        }

        return effects;
    }
}
