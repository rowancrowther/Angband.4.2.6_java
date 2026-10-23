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

package uk.co.jackoftradesltd.backend.parser.bloweffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.monsters.BlowEffect;
import uk.co.jackoftradesltd.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link BlowEffectParseRecord}s produced by the {@code blow_effects.txt}
 * grammar into domain {@link BlowEffect}s, resolving every string the data file carries
 * into the enum or object it names. This is the Java port of the {@code parse_eff_*}
 * handlers in [C] {@code mon-init.c} ({@code parse_eff_name} through
 * {@code parse_eff_lash_type}).
 * <p>
 * Three boundaries need care, because the C original leans on representation details the
 * port deliberately does not reproduce:
 * <ul>
 *   <li><strong>{@code resist:} is polymorphic.</strong> [C] {@code parse_eff_resist} stores
 *       it in a single {@code int} whose meaning depends on {@code effect_type}: an index
 *       into the merged element-then-projection name list for {@code element}, an object
 *       flag index for {@code flag}. The port splits that union into two separately typed
 *       fields. Each starts where C's zeroed {@code int} would read it: {@code ELEM_ACID}
 *       (element 0) and {@code OF_NONE} (flag 0).</li>
 *   <li><strong>{@code lash-type:} never fails.</strong> [C] {@code parse_eff_lash_type}
 *       does {@code type >= 0 ? type : PROJ_MISSILE}, so a name it cannot resolve silently
 *       becomes {@code PROJ_MISSILE}. That fallback is load-bearing for the shipped data, not
 *       defensive - see {@link #assemble}.</li>
 *   <li><strong>Field order does not matter here.</strong> [C] resolves {@code resist:}
 *       during parsing, so {@code effect-type:} must come first: a {@code resist:} read
 *       before it hands {@code streq} a {@code NULL} {@code effect_type}. The grammar
 *       accepts the directives in any order and this assembler resolves from the completed
 *       record, so the port has no such ordering constraint.</li>
 * </ul>
 * As C's {@code parse_eff_*} handlers end the whole {@code blow_effects.txt} load on their
 * first parser error, so does this assembler: a bad value is logged and thrown as a
 * {@link DataFileParseException} rather than reported as a soft error.
 *
 * <p>Class BlowEffectAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class BlowEffectAssembler implements Assembler<BlowEffectParseRecord, List<BlowEffect>> {
    /**
     * The logger this assembler writes to: a fatal message, with the same text, just before
     * each {@link DataFileParseException} it throws.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(BlowEffectAssembler.class);

    /**
     * Resolves every raw {@link BlowEffectParseRecord} field into its domain form and builds
     * the {@link BlowEffect}, keeping the records in file order. C builds its list in reverse
     * and {@code finish_parse_eff} copies it back into file order, so both end the same way.
     *
     * <p>Field by field, against [C] {@code mon-init.c}:
     * <ul>
     *   <li>{@code power:} and {@code eval:} default to 0, as in C's zeroed record. They are
     *       read by {@link ParseUtils#parseIntString(String, String, int, String, String)},
     *       as C's {@code parser_getint} reads them with {@code strtol} in base 0: a leading
     *       {@code 0} means octal, so {@code power:010} is 8. A figure too big for an
     *       {@code int} is clamped where C keeps its low 32 bits, the accepted deviation
     *       recorded on that class. Text with no digits would end the load, as C's
     *       {@code PARSE_ERROR_NOT_NUMBER} does, but the grammar's {@code INTEGER} token never
     *       produces it.</li>
     *   <li>{@code desc:} arrives already joined by the grammar, as C's
     *       {@code string_append} joins repeated lines.</li>
     *   <li>The three {@code lore-color-*:} colours follow {@code parse_eff_lore_color} and
     *       its two siblings, which never fail on a colour a Java string can hold. A name
     *       longer than one character goes to {@code color_text_to_attr} in C, which matches
     *       ignoring case and answers {@code COLOUR_WHITE} for anything it does not know. A
     *       single character goes to {@code color_char_to_attr}, which answers
     *       {@code COLOUR_DARK} for a space, the colour whose index character matches
     *       (case-sensitively), and otherwise {@code COLOUR_WHITE}. Its only error, a negative
     *       {@code char}, has no Java counterpart. So an unresolved colour here becomes
     *       {@code COLOUR_WHITE}, and so does {@code "Shade"}, which
     *       {@link ColourEnum#fromCode(String)} resolves but C's empty-named row does not. An
     *       absent colour stays {@code null}, where C's zeroed field reads
     *       {@code COLOUR_DARK}.</li>
     *   <li>{@code effect-type:} is matched exactly, as C's {@code streq} matches it; an
     *       absent or unknown type is {@code null}.</li>
     *   <li>{@code resist:} on any type but {@code element} or {@code flag} ends the load, as
     *       {@code parse_eff_resist} returns {@code PARSE_ERROR_MISSING_BLOW_EFF_TYPE}. A flag
     *       name is matched exactly, as {@code flag_index_by_name} does, and an unknown one is
     *       {@code OF_NONE} where C stores -1. An element name is matched ignoring case, as
     *       {@code proj_name_to_idx} does, and an unknown one is {@code ELEM_NONE} where C
     *       stores -1. C's list also holds the non-element projections, so in C an element
     *       resist naming one (say {@code MISSILE}) stores that projection's index; here it
     *       is {@code ELEM_NONE}. No shipped record does either.</li>
     *   <li>{@code lash-type:} defaults to {@code PROJ_ACID}, C's projection 0. A name is
     *       matched ignoring case, and an unknown one, or one with no loaded projection,
     *       falls back to {@code PROJ_MISSILE}.</li>
     * </ul>
     *
     * <p>Function assemble coded before 260915, commented in full on 260915, rewritten for
     * the load-ending errors, the white colour fallback and base 0 integers on 261023.
     *
     * @param records  the blow-effect records in file order, as produced by the grammar
     * @param errors   unused: every error ends the load by throwing instead
     * @param filename the name of the data file, used in the error messages
     * @return the assembled blow effects, in file order
     * @throws DataFileParseException on a {@code resist:} on an effect type that cannot carry
     *                                one, or a {@code power:} or {@code eval:} with no digits
     */
    @Override
    public List<BlowEffect> assemble(@NotNull List<BlowEffectParseRecord> records, @NotNull List<String> errors,
                                     String filename) {
        List<BlowEffect> effects = new ArrayList<>();

        for (BlowEffectParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String powerStr = record.power();
            int power = 0;
            if (!powerStr.isEmpty()) {
                power = ParseUtils.parseIntString("Blow Effect", record.power(), line, filename, "power");
            }
            String evalStr = record.eval();
            int eval = 0;
            if (!evalStr.isEmpty()) {
                eval = ParseUtils.parseIntString("Blow Effect", record.eval(), line, filename, "eval");
            }
            String desc = record.desc();
            // An absent lore colour is left null - the port models "no lore colour" as null. [C]
            // instead gets COLOUR_DARK for free from the mem_zalloc'd lore_attr fields (0 ==
            // COLOUR_DARK); resolving that concrete default is deferred to the renderer.
            ColourEnum base = null;
            if (!record.loreColourBase().isEmpty()) {
                base = ColourEnum.fromCode(record.loreColourBase());
                if (base == ColourEnum.COLOUR_SHADE) base = ColourEnum.COLOUR_WHITE;
                if (base == null)
                    base = ColourEnum.COLOUR_WHITE;
            }
            ColourEnum resist = null;
            if (!record.loreColourResist().isEmpty()) {
                resist = ColourEnum.fromCode(record.loreColourResist());
                if (resist == ColourEnum.COLOUR_SHADE) resist = ColourEnum.COLOUR_WHITE;
                if (resist == null)
                    resist = ColourEnum.COLOUR_WHITE;
                }
            ColourEnum immune = null;
            if (!record.loreColourImmune().isEmpty()) {
                immune = ColourEnum.fromCode(record.loreColourImmune());
                if (immune == ColourEnum.COLOUR_SHADE) immune = ColourEnum.COLOUR_WHITE;
                if (immune == null) 
                    immune = ColourEnum.COLOUR_WHITE;
            }
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
                    String message = "Blow Effect at line: " + line + " of file: " + filename + " has " +
                            "a resist for a non-resist effect type: " + effectTypeStr +
                            " " + resistStr;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            // Only one of these is ever populated; the other keeps the value [C]'s zeroed int would
            // read as (element 0 is ELEM_ACID, flag 0 is OF_NONE), which is how the port replaces
            // the single overloaded int [C] uses for both cases. The data
            // file names flags and elements without their enum prefixes, so those are bridged
            // back on here - "FREE_ACT" -> OF_FREE_ACT, "POIS" -> ELEM_POIS.
            ElementEnum elementEnumResist = ElementEnum.ELEM_ACID;
            ObjectFlag objectFlagResist = ObjectFlag.OF_NONE;
            if (!resistStr.isEmpty()) {
                if (effectType == BlowEffectType.BET_FLAG) {
                    try {
                        objectFlagResist = ObjectFlag.valueOf("OF_" + resistStr);
                    } catch (IllegalArgumentException e) {
                        objectFlagResist = ObjectFlag.OF_NONE;
                    }
                } else if (effectType == BlowEffectType.BET_ELEMENT) {
                    try {
                        elementEnumResist = ElementEnum.valueOf("ELEM_" + resistStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        elementEnumResist = ElementEnum.ELEM_NONE;
                    }
                }
            }
            String proj = record.lashType();
            Projection lash = WorldRegistry.lookupProjectionByLash(ProjectionEnum.PROJ_ACID);
            // Unlike every other resolution here, an unknown lash type is not an error: [C]
            // falls back to PROJ_MISSILE. The shipped file depends on it - SHATTER declares
            // lash-type:SHARDS while the projection code is SHARD, upstream included - so both
            // the unknown-name and no-such-projection paths land on MISSILE rather than adding
            // to errors. ProjectionEnum spans elements then projections, mirroring the merged
            // list [C] searches in proj_name_to_idx (project.c, proj_name_list).
            if (!proj.isEmpty()) {
                ProjectionEnum projEnum;
                try {
                    projEnum = ProjectionEnum.valueOf("PROJ_" + proj.toUpperCase());
                    lash = WorldRegistry.lookupProjectionByLash(projEnum);
                    if (lash == null) {
                        lash = WorldRegistry.lookupProjectionByLash(ProjectionEnum.PROJ_MISSILE);
                    }
                } catch (IllegalArgumentException e) {
                    lash = WorldRegistry.lookupProjectionByLash(ProjectionEnum.PROJ_MISSILE);
                }
            }

            effects.add(new BlowEffect(name, power, eval, desc, base,
                    resist, immune, effectType, objectFlagResist,
                    elementEnumResist, lash));
        }

        return effects;
    }
}
