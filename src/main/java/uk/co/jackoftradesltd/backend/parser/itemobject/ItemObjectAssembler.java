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
import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
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
 *   <li>{@code graphics:} reads the glyph token as a single code point and builds the
 *       {@link AngbandDisplayCharacter} through its {@code int} constructor when the code point is
 *       supplementary and its {@code char} one otherwise. C's parser refuses a {@code char} field of
 *       more than one character ({@code PARSE_ERROR_FIELD_TOO_LONG}); here that refusal is the
 *       lexer's, since the glyph token matches exactly one code point and anything longer never
 *       reaches the assembler, so {@code codePointAt(0)} is the whole token. The colour goes through
 *       {@link ColourEnum#fromCode(String)}: one character is a colour code, longer text a colour
 *       name, as C chooses between {@code color_char_to_attr} and {@code color_text_to_attr} on the
 *       length. One that does not resolve becomes {@link ColourEnum#COLOUR_WHITE}, as both C
 *       functions return white. A record with no glyph keeps a {@code null} cell, where C leaves
 *       {@code d_char} and {@code d_attr} zero;</li>
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
 *       half into a {@code Map<}{@link ElementEnum}{@code ,} {@link ElementInfo}{@code >}. A token
 *       is a modifier unless it starts {@code RES_}, and C's {@code obj_mods} table holds neither a
 *       {@code NONE} nor a {@code MAX}, so {@link ObjectModifier#OM_NONE} and
 *       {@link ObjectModifier#OM_MAX} are refused. A modifier's figure is read as dice; a
 *       {@code RES_} figure is read as C's {@code find_value_arg} reads it, in base 10 with no
 *       trailing text, and one at {@code INT_MIN} or {@code INT_MAX} is refused. C holds both
 *       halves in index-ordered arrays; these two maps are {@link HashMap}s, so their iteration
 *       order is unspecified;</li>
 *   <li>each {@code flags:} token is tried as an {@link ObjectFlag} ({@code OF_}), as an
 *       {@link ObjectKindFlag} ({@code KF_}) and as {@code IGNORE_<element>} or
 *       {@code HATES_<element>}, all three for every token as C's {@code parse_object_flags} does,
 *       and a token that none of them takes is an error. C's lookup starts at table index 1, which
 *       skips the {@code NONE} at index 0, so {@code NONE} is refused. Both C tables end in
 *       {@code MAX}, so C takes a {@code MAX} token and sets the sentinel bit in both; the
 *       lookups here find {@link ObjectFlag#OF_MAX} and {@link ObjectKindFlag#KF_MAX} and do
 *       the same. The element prefix chooses the bit, as it does in C's
 *       {@code grab_element_flag}: {@code IGNORE_} sets {@code EL_INFO_IGNORE} and {@code HATES_}
 *       sets {@code EL_INFO_HATES} in that element's {@link ElementInfo}, the same record the
 *       {@code RES_} level lives in. The base's kind flags are then added to the kind's own, as
 *       {@code finish_parse_object} does with {@code kf_union};</li>
 *   <li>brands, slays and curses resolve against their registries by code or name. Brands and slays
 *       are collected into {@link LinkedHashSet}s by walking {@link ObjectRegistry#getBrands()} and
 *       {@link ObjectRegistry#getSlays()} and keeping those the record named, so the sets come out
 *       in registry order, the order C's {@code brands[i]} and {@code slays[i]} boolean arrays are
 *       walked in, and not in the order the record listed them. Curses become a
 *       {@code Map<}{@link Curse}{@code ,} {@link CurseData}{@code >} keyed by curse, with the
 *       parsed power and a timeout of 0 at kind level; a curse declared at power 0 or below is
 *       dropped rather than stored, as C stores a curse only when its power is above 0, absence
 *       being how this port says "not cursed with this". A curse named twice keeps the later
 *       power, and a later entry at power 0 or below leaves the earlier one in place, as C's
 *       array does. The {@link ObjectKind} constructor copies the map into a {@link TreeMap}
 *       ordered by {@code Curse.CURSE_ORDER}, so the kind's curses are index-ordered as in C;</li>
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
 * as C's {@code PARSE_ERROR_*} results stop the load. Nothing is skipped; {@link EffectAssembler}
 * follows the same policy, so a bad effect ends the load as well. Hard grammar and lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * <p>The kind's index and sval are left at 0 here: {@link ObjectRegistry#addObjectKind} sets both
 * when the kind is registered, as C's {@code parse_object_type} and {@code finish_parse_object} do.
 * The flavour, the aware and unaware notes, and the aware, tried and everseen state belong to the
 * running game and start empty, and the ignore flags start empty.
 *
 * <p>Class ItemObjectAssembler coded before 260915, commented in full on 261107 (glyph, flag,
 * ordering and sentinel notes rewritten against {@code parse_object_*} and
 * {@code finish_parse_object}; the soft-error path was removed on 261105).
 *
 * @author Rowan Crowther
 */
public class ItemObjectAssembler implements Assembler<ItemObjectParseRecord, List<ObjectKind>> {
    /**
     * Class logger. Receives the fatal message of each {@link DataFileParseException} just before
     * it is thrown, so the message reaches the log as well as the caller. It is {@code static final}
     * and shared by every assembly; the assembler holds no other state, so one instance may
     * assemble any number of files.
     *
     * <p>Field logger coded before 260915, commented in full on 261107.
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
     * <p>The method does in one pass over each record what C does across the
     * {@code parse_object_*} callbacks and {@code finish_parse_object}, and it reads the
     * directives in a fixed order (type, graphics, the plain figures, dice, {@code alloc:},
     * {@code charges:}, {@code pile:}, flags, values, brands, slays, curses, {@code pval:},
     * {@code power:}, effects, {@code time:}), where C reads them in file order. The order only decides which of two bad
     * directives in one record is reported; every failure ends the load. Each record is built
     * into an {@link ObjectKind} with the base's kind flags added to its own
     * ({@code kf_union} in C), and the kind index is left 0 for
     * {@link ObjectRegistry#addObjectKind}. A {@code type:} whose tval has no base loads with
     * {@code base} {@code null}, where C hands back a zeroed base; the kind-flag union is skipped
     * then, which adds nothing, as the zeroed base's flags would.
     *
     * <p>Function assemble commented in full before 260915, provenance stamp added on 260915,
     * rewritten on 261026 for the fatal error policy, the {@code HATES_} flags and the curse and
     * {@code alloc:} handling, and on 261107 for the directive order, flag sentinels and the
     * unbased-tval case.
     *
     * @param records  the raw object records captured by the grammar
     * @param filename the file the records came from, named in every exception message
     * @return the assembled object kinds, one per record, in source order
     * @throws DataFileParseException if any record has an unknown {@code type:}, flag (including
     *                                {@code NONE}), modifier (including the sentinels), element,
     *                                brand, slay or curse, a missing {@code alloc:} range end or
     *                                pile amount, or a figure or dice string that does not parse
     */
    @Override
    public List<ObjectKind> assemble(@NotNull List<ItemObjectParseRecord> records,
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
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            for (String flag : record.flags()) {
                if (flag.equals("NONE")) {
                    String msg = "Object kind starting at line: " + line + " of file: " + filename + " has " +
                            "an unknown sentinel flag: " + flag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                
                ObjectFlag oFlag = ObjectFlag.lookup("OF_" + flag);
                ObjectKindFlag kFlag = ObjectKindFlag.lookup("KF_" + flag);
                
                if (oFlag != null) {
                    oFlags.on(oFlag);
                }
                if (kFlag != null) {
                    oKindFlags.on(kFlag);
                }
                
                boolean flagFound = ParseUtils.grabElementFlag(elInfo, flag);
                
                if (!flagFound && kFlag == null && oFlag == null) {
                    String msg = "Object kind starting at line: " + line + " of file: " + filename + " has " +
                            "an unknown flag: " + flag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                String enumFlag;
                if (key.startsWith("RES_")) {
                    enumFlag = "ELEM_" + key.substring(4);
                    ElementEnum elementEnum;
                
                    elementEnum = ElementEnum.lookup(enumFlag);
                    if (elementEnum == null) {
                        String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                "an illegal resistance element value: " + key;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    
                    if (elementEnum == ElementEnum.ELEM_MAX || elementEnum == ElementEnum.ELEM_NONE) {
                        String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                "an illegal sentinel resistance element value: " + key;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    if (ParseUtils.nonDigitFound(value, ParseUtils.Radix.DECIMAL)) {
                        String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                "a non-digit found in the element value: " + key;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    int resistance = ParseUtils.parseIntString("Object kind", value, line, filename, "resistance", ParseUtils.Radix.DECIMAL);
                    if (resistance == Integer.MIN_VALUE || resistance == Integer.MAX_VALUE) {
                        String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                "an out of bounds object modifier value: " + key;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    ElementInfo info = elInfo.computeIfAbsent(elementEnum, k -> new ElementInfo());
                    info.setResLevel(resistance);
                } else {
                    ObjectModifier om;
                    try {
                        om = ObjectModifier.valueOf("OM_" + key);
                        if (om == ObjectModifier.OM_NONE || om == ObjectModifier.OM_MAX) {
                            String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                                    "an illegal sentinel object modifier value: " + key;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
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
            Set<Brand> fileBrands = new HashSet<>();
            for (String b : record.brand()) {
                Brand brand = ObjectRegistry.lookupBrandCode(b);
                if (brand != null) {
                    fileBrands.add(brand);
                } else {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal brand value: " + b;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Set<Brand> brands = new LinkedHashSet<>();
            for (Brand b : ObjectRegistry.getBrands()) {
                if (fileBrands.contains(b)) {
                    brands.add(b);
                }                
            }
            Set<Slay> fileSlays = new HashSet<>();
            for (String s : record.slay()) {
                Slay slay = ObjectRegistry.lookupSlay(s);
                if (slay != null) {
                    fileSlays.add(slay);
                } else {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal slay value: " + s;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Set<Slay> slays = new LinkedHashSet<>();
            for (Slay slay : ObjectRegistry.getSlays()) {
                if (fileSlays.contains(slay)) {
                    slays.add(slay);
                }
            }
            Map<Curse, CurseData> curses = new HashMap<>();
            for (FullCurseDataParseRecord curseDataRecord : record.curse()) {
                String curseName = curseDataRecord.name();
                Curse curse = ObjectRegistry.lookupCurse(curseName);
                if (curse == null) {
                    String msg = "Object kind starting at line: " + line +" of file: " + filename + " has " +
                            "an illegal current curse name value: " + curseName;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else {
                    int power = ParseUtils.parseIntString("Object kind", curseDataRecord.power(), line, filename, "curse power");
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
            List<Effect> effects = EffectAssembler.assemble(record.effects(), filename);
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