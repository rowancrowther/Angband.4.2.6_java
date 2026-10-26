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

package uk.co.jackoftradesltd.backend.parser.itemobject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.*;

/**
 * Turns the raw {@link ItemObjectParseRecord}s from {@code ItemObjectGrammar} into domain
 * {@link ObjectKind}s — C's {@code struct object_kind}, the item <em>template</em> loaded from
 * {@code object.txt} (Java port of {@code parse_object_*} in {@code obj-init.c}).
 *
 * <p>This is where interpretation happens:
 * <ul>
 *   <li>{@code type:} resolves to an {@link ObjectBase} by tval
 *       ({@link TValue#fromName} then {@link ObjectRegistry#getBaseFromTVal}); a record with no
 *       {@code type:} keeps C's zero tval, {@code TV_NONE}, and a {@code null} base, as C leaves
 *       {@code k->base} {@code NULL} when {@code parse_object_type} never runs;</li>
 *   <li>{@code graphics:} takes the first code point of the glyph token, as C's
 *       {@code parser_getchar} takes the first wide character and ignores the rest, and builds the
 *       {@link AngbandDisplayCharacter} through its {@code int} constructor when the code point is
 *       supplementary and its {@code char} one otherwise. The colour goes through
 *       {@link ColourEnum#fromCode(String)}, and one that does not resolve becomes
 *       {@link ColourEnum#COLOUR_WHITE}, as C's {@code color_char_to_attr} and
 *       {@code color_text_to_attr} return white. A record with no glyph keeps a {@code null} cell,
 *       where C leaves {@code d_char} and {@code d_attr} zero;</li>
 *   <li>the rand fields (attack {@code hd}/to-h/to-d, armour to-a, {@code charges}, pile stack,
 *       {@code pval}) become {@link Random}s via {@code Random.parseStr}, while the scalar fields
 *       (level/weight/cost/power, armour base {@code ac}, alloc, pile chance) are parsed as ints.
 *       An absent rand or int directive is 0, which is what C's zeroed {@code struct object_kind}
 *       holds;</li>
 *   <li>{@code alloc:} reads its commonness in base 0, as {@code parser_getint} does, and the two
 *       ends of the depth range in base 10, as {@code grab_int_range} in {@code datafile.c} reads
 *       them, so {@code 010} is ten there. A commonness without both range ends is an error;</li>
 *   <li>the {@code values:} line is fanned out into its two C destinations — the {@code obj_mods}
 *       half into a {@code Map<}{@link ObjectModifier}{@code , Random>} and the {@code RES_}-prefixed
 *       half into a {@code Map<}{@link ElementEnum}{@code ,} {@link ElementInfo}{@code >};</li>
 *   <li>each {@code flags:} token is tried as an {@link ObjectFlag} ({@code OF_}), then as an
 *       {@link ObjectKindFlag} ({@code KF_}), then as {@code IGNORE_<element>} or
 *       {@code HATES_<element>}. C tries all three tables for every token, but the two flag lists
 *       share no name apart from their {@code NONE} and {@code MAX} sentinels, so the first match is
 *       the only one. The element prefix chooses the bit, as it does in C's
 *       {@code grab_element_flag}: {@code IGNORE_} sets {@code EL_INFO_IGNORE} and {@code HATES_}
 *       sets {@code EL_INFO_HATES} in that element's {@link ElementInfo}, the same record the
 *       {@code RES_} level lives in. The base's kind flags are then added to the kind's own, as
 *       {@code finish_parse_object} does with {@code kf_union};</li>
 *   <li>brands, slays and curses resolve against their registries by code or name. Curses become a
 *       {@code Map<}{@link Curse}{@code ,} {@link CurseData}{@code >} keyed by curse, with the
 *       parsed power and a timeout of 0 at kind level; a curse declared at power 0 or below is
 *       dropped rather than stored, as C stores a curse only when its power is above 0, absence
 *       being how this port says "not cursed with this";</li>
 *   <li>base damage dice/sides ({@code dd}/{@code ds}) are derived from the attack {@code hd},
 *       and effects are delegated to {@link EffectAssembler};</li>
 *   <li>the record's own {@code time:} dice string becomes the kind's {@code time} {@link Random}
 *       (zero if the line is absent), so the kind carries C's {@code kind->time} and the effects
 *       carry none.</li>
 * </ul>
 *
 * <p><b>Error policy:</b> every failure is fatal. An unknown {@code type:}, an unknown flag,
 * modifier, element, brand, slay or curse, and a figure or dice string that does not parse each log
 * a fatal message and throw a {@link DataFileParseException} naming the record's line and the file,
 * as C's {@code PARSE_ERROR_*} results stop the load. Nothing is skipped and the {@code errors}
 * list is never written to; {@link EffectAssembler} follows the same policy, so a bad effect ends
 * the load as well. Hard grammar and lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * <p>The kind's index and sval are left at 0 here: {@link ObjectRegistry#addObjectKind} sets both
 * when the kind is registered, as C's {@code parse_object_type} and {@code finish_parse_object} do.
 * The flavour, the aware and unaware notes, and the aware, tried and everseen state belong to the
 * running game and start empty, and the ignore flags start empty.
 *
 * <p>Class ItemObjectAssembler coded before 260915, commented in full on 261026.
 *
 * @author Rowan Crowther
 */
public class ItemObjectAssembler implements Assembler<ItemObjectParseRecord, List<ObjectKind>> {
    /**
     * Class logger. Receives the fatal message of each {@link DataFileParseException} just before
     * it is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261026.
     */
    private static final Logger logger = LogManager.getLogger(ItemObjectAssembler.class);

    /**
     * Assembles every parsed object record into an {@link ObjectKind}, in source order, applying
     * the per-directive rules set out in the class description. The first record that fails to
     * resolve ends the load: nothing is skipped and no partial list is returned.
     *
     * <p>A record with no {@code type:} loads as {@code TV_NONE} with a {@code null} base, as C
     * leaves {@code k->base} {@code NULL} when {@code parse_object_type} never runs. A record with
     * no {@code pval:} gets a zero {@link Random}, but a {@code pval:} that does not parse is an
     * error, as C's {@code PARSE_ERROR_NOT_RANDOM} is. {@code dd} and {@code ds} are the dice and
     * sides of the attack {@code hd}, as C's {@code parse_object_attack} takes them.
     *
     * <p>Function assemble commented in full before 260915, provenance stamp added on 260915,
     * rewritten on 261026 for the fatal error policy, the {@code HATES_} flags and the curse and
     * {@code alloc:} handling.
     *
     * @param records  the raw object records captured by the grammar
     * @param errors   not written to; kept so the signature matches {@link Assembler}, and passed
     *                 on to {@link EffectAssembler}, which does not write to it either
     * @param filename the file the records came from, named in every exception message
     * @return the assembled object kinds, one per record, in source order
     * @throws DataFileParseException if any record has an unknown {@code type:}, flag, modifier,
     *                                element, brand, slay or curse, a missing {@code alloc:} range
     *                                end or pile amount, or a figure or dice string that does not
     *                                parse
     */
    @Override
    public List<ObjectKind> assemble(@NotNull List<ItemObjectParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<ObjectKind> itemObjects = new ArrayList<>();

        for (ItemObjectParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            ObjectBase base;
            TValue tValue;
            if (!record.tValue().isEmpty()) {
                String tVal = record.tValue();
                try {
                    tValue = TValue.fromName(tVal);
                    if (tValue == null) {
                        String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                "an illegal tValue: " + record.tValue();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    base = ObjectRegistry.getBaseFromTVal(tValue);
                } catch (IllegalArgumentException e) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal tValue: " + record.tValue();
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            } else {
                tValue = TValue.TV_NONE;
                base = null;
            }
            String glyphChar = record.glyph();
            String glyphColour = record.colour();
            AngbandDisplayCharacter adc = null;
            if (!glyphChar.isEmpty()) {
                ColourEnum colourType = ColourEnum.fromCode(glyphColour);
                if (colourType == null) {
                    colourType = ColourEnum.COLOUR_WHITE;
                }
                if (Character.isSupplementaryCodePoint(glyphChar.codePointAt(0)))
                    adc = new AngbandDisplayCharacter(glyphChar.codePointAt(0), colourType);   
                else
                    adc = new AngbandDisplayCharacter(glyphChar.charAt(0), colourType);
            }
            int level = ParseUtils.parseIntString("Object kind", record.level(), line, filename, "level");
            int weight = ParseUtils.parseIntString("Object kind", record.weight(), line, filename, "weight");
            int cost = ParseUtils.parseIntString("Object kind", record.cost(), line, filename, "cost");
            String attackBase = record.attackBaseDamage();
            String attackToh = record.attackToh();
            String attackTod = record.attackTod();
            Random attBase = Random.Zero();
            if (!attackBase.isEmpty()) {
                attBase = Random.parseStr(attackBase);
                if (attBase == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal base attack value: " + record.attackBaseDamage();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Random attToh = Random.Zero();
            if (!attackToh.isEmpty()) {
                attToh = Random.parseStr(attackToh);
                if (attToh == null) {String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal attack-to-hit value: " + record.attackToh();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Random attTod = Random.Zero();
            if (!attackTod.isEmpty()) {
                attTod = Random.parseStr(attackTod);
                if (attTod == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal attack-to-damage value: " + record.attackTod();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            int ac = ParseUtils.parseIntString("Object kind", record.armourBaseAC(), line, filename, "base armour class");
            Random acToa = Random.Zero();
            if (!record.armourToa().isEmpty()) {
                acToa = Random.parseStr(record.armourToa());
                if (acToa == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal to ac value: " + record.armourToa();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            int allocComm = 0;
            int allocLower = 0;
            int allocUpper = 0;
            if (!record.allocCommon().isEmpty()) {
                if (record.allocLower().isEmpty() || record.allocUpper().isEmpty()) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal alloc value: " + record.allocCommon() + ":" + record.allocLower() + " to " +
                            record.allocUpper();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                allocComm = ParseUtils.parseIntString("Object kind", record.allocCommon(), 
                        line, filename, "commonness alloc");
                allocLower = ParseUtils.parseIntString("Object kind", record.allocLower(),
                        line, filename, "lower alloc", ParseUtils.Radix.DECIMAL);
                allocUpper = ParseUtils.parseIntString("Object kind", record.allocUpper(),
                        line, filename, "upper alloc", ParseUtils.Radix.DECIMAL);
            }
            Random charges = Random.Zero();
            if (!record.charges().isEmpty()) {
                charges = Random.parseStr(record.charges());
                if (charges == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal charges value: " + record.charges();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            int pileChance = 0;
            Random pileAmount = Random.Zero();
            if (!record.pileChance().isEmpty()) {
                pileChance = ParseUtils.parseIntString("Object kind", record.pileChance(), line, filename, "pile chance");
                
                if (record.pileNumber().isEmpty()) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "a pile chance but no pile amount value";
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                pileAmount = Random.parseStr(record.pileNumber());
                if (pileAmount == null) {String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal pile amount value: " + record.pileNumber();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            Flag<ObjectKindFlag> oKindFlags = new Flag<>(ObjectKindFlag.class);
            Flag<ElementEnum> eIFlags = new Flag<>(ElementEnum.class);
            Flag<ElementEnum> eHFlags = new Flag<>(ElementEnum.class);
            for (String flag : record.flags()) {
                ObjectFlag oFlag;
                ObjectKindFlag kFlag;
                ElementEnum eFlag;
                try {
                    oFlag = ObjectFlag.valueOf("OF_" + flag);
                    oFlags.on(oFlag);
                } catch (IllegalArgumentException e) {
                    try {
                        kFlag = ObjectKindFlag.valueOf("KF_" + flag);
                        oKindFlags.on(kFlag);
                    } catch (IllegalArgumentException e1) {
                        try {
                            if (flag.startsWith("IGNORE_")) {
                                eFlag = ElementEnum.valueOf("ELEM_" + flag.substring(7));
                                eIFlags.on(eFlag);
                            } else if (flag.startsWith("HATES_")) {
                                eFlag = ElementEnum.valueOf("ELEM_" + flag.substring(6));
                                eHFlags.on(eFlag);
                            } else {
                                String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                        "an unknown flag: " + flag;
                                logger.fatal(msg);
                                throw new DataFileParseException(msg);
                            }
                        } catch (IllegalArgumentException e2) {
                            String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                    "an unknown flag: " + flag;
                            logger.fatal(msg, e2);
                            throw new DataFileParseException(msg);
                        }
                    }
                }
            }
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                String enumFlag;
                if (key.startsWith("RES_")) {
                    enumFlag = "ELEM_" + key.substring(4);
                    ElementEnum elementEnum;
                    try {
                        elementEnum = ElementEnum.valueOf(enumFlag);
                        ElementInfo info = elInfo.computeIfAbsent(elementEnum, k -> new ElementInfo());
                        info.setResLevel(ParseUtils.parseIntString("Object kind", value, line, filename, "resistance"));
                    } catch (IllegalArgumentException e) {
                        String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                "an illegal resistance element value: " + key;
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                } else {
                    ObjectModifier om;
                    try {
                        om = ObjectModifier.valueOf("OM_" + key);
                        Random rnd = Random.parseStr(value);
                        if (rnd == null) {
                            String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                    "an illegal object modifier value: " + key;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        } else
                            modifiers.put(om, rnd);
                    } catch (IllegalArgumentException e) {
                        String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                "an illegal object modifier value: " + key;
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                }
            }
            Set<Brand> brands = new HashSet<>();
            for (String brand : record.brand()) {
                Brand b = ObjectRegistry.lookupBrandCode(brand);
                if (b == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal brand value: " + brand;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else
                    brands.add(b);
            }
            Set<Slay> slays = new HashSet<>();
            for (String slay : record.slay()) {
                Slay s = ObjectRegistry.lookupSlay(slay);
                if (s == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal slay value: " + slay;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else
                    slays.add(s);
            }
            Map<Curse, CurseData> curses = new HashMap<>();
            for (String curseName : record.curse().keySet()) {
                Curse curse = ObjectRegistry.lookupCurse(curseName);
                if (curse == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal current curse name value: " + curseName;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else {
                    int power = ParseUtils.parseIntString("Object kind", record.curse().get(curseName), line, filename, "curse power");
                    if (power > 0)
                        curses.put(curse, new CurseData(power, 0));
                }
            }
            Random pVal;
            if (!record.pVal().isEmpty()) {
                pVal = Random.parseStr(record.pVal());
                if (pVal == null) {
                    String msg = "Object kind starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal parameter value: " + record.pVal();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            } else {
                pVal = Random.Zero();                
            }
            int power = ParseUtils.parseIntString("Object kind", record.power(), line, filename, "power");
            String description = record.desc();
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            int dd = attBase.getDice();
            int ds = attBase.getSides();

            Random time = Random.Zero();
            if (!record.time().isEmpty()) {
                time = Random.parseStr(record.time());
                if (time == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal time value: " + record.time();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }

            Flag<IgnoreFlag> ignoreFlags = new Flag<>(IgnoreFlag.class);
            
            if (base != null) {
                oKindFlags.union(base.getKindFlags());
            }
            
            for (ElementEnum elem : eIFlags) {
                ElementInfo info = elInfo.computeIfAbsent(elem, k -> new ElementInfo());
                info.getFlags().on(ElementInfoEnum.EL_INFO_IGNORE);
            }

            for (ElementEnum elem : eHFlags) {
                ElementInfo info = elInfo.computeIfAbsent(elem, k -> new ElementInfo());
                info.getFlags().on(ElementInfoEnum.EL_INFO_HATES);
            }
            
            itemObjects.add(new ObjectKind(name, description, base, 0, pVal,
                    attToh, attTod, acToa, ac, attBase, dd, ds,
                    weight, cost, oFlags, oKindFlags, modifiers, elInfo,
                    brands, slays, curses, adc, allocComm, allocLower, allocUpper,
                    level, new ArrayList<>(), effects, record.message(),
                    record.visMessage(), time, charges, pileChance, pileAmount,
                    null, null, null, false, false,
                    ignoreFlags, false, tValue, power));
        }

        return itemObjects;
    }
}