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

package uk.co.jackoftradesltd.backend.parser.egoitem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

/**
 * Assembles the raw {@link EgoItemParseRecord}s parsed from {@code ego_item.txt} into the
 * finished {@link EgoItem} domain objects (reader &rarr; ParseRecord &rarr; assembler &rarr;
 * domain). Each record becomes one {@link EgoItem}, in file order.
 *
 * <p>The C counterpart is the {@code parse_ego_*} handlers in {@code obj-init.c}, which build one
 * {@code struct ego_item} per {@code name:} line, and {@code finish_parse_ego}, which numbers
 * them by their place in the file. C resolves each line as it is read and ends the load on the
 * first one that fails. Here the grammar collects a whole record first and this class resolves
 * it afterwards, but the outcome is the same: a bad field ends the load (see {@link #assemble}).
 *
 * <p>Class EgoItemAssembler coded before 260915, commented in full on 261024.
 *
 * @author Rowan Crowther
 */
public class EgoItemAssembler implements Assembler<EgoItemParseRecord, List<EgoItem>> {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261024.
     */
    private static final Logger logger = LogManager.getLogger(EgoItemAssembler.class);
    
    /**
     * Resolves every raw {@link EgoItemParseRecord} field into its domain form and builds one
     * {@link EgoItem} per record, in file order. Each directive follows the matching
     * {@code parse_ego_*} handler in {@code obj-init.c}.
     *
     * <p><b>Stopping the load.</b> A field that cannot be resolved (an unknown tvalue, svalue,
     * flag, modifier, element, brand, slay or curse name, a bad number or dice string, an
     * {@code alloc:} depth outside 0 to 255) throws a {@link DataFileParseException} naming the
     * record's {@code name:} line and {@code filename}, as C ends the load on the first parse
     * error. No record is skipped, so {@code errors} is never written to; it stays in the
     * signature because {@link Assembler} requires it. For the same reason an ego's index is its
     * place in the file counted from 0, as {@code finish_parse_ego} numbers it.
     *
     * <p><b>Eligible kinds.</b> {@code possItems} follows C's head-pushed {@code poss_items}
     * chain. The grammar hands over the {@code type:} and {@code item:} lines last first. An
     * {@code item:} adds its one kind, and a {@code type:} adds every kind of that tvalue in
     * reverse table order. An unknown tvalue or svalue, or a tvalue with no kinds, stops the load.
     *
     * <p><b>Flags.</b> A {@code flags:} token is an element flag when the text before its first
     * underscore is a leading part of {@code IGNORE} or {@code HATES} and the rest names an
     * element. {@code grab_element_flag} compares that text with {@code strncmp} over its own
     * length, so {@code IGN_FIRE} and {@code H_FIRE} count, and {@code IGNORE} is tried first.
     * Any other token is tried as an object flag and then as a kind flag. {@code NONE} is refused,
     * as C's flag lookup starts after it, and {@code MAX} is accepted, as C's tables carry it.
     * The element names {@code NONE} and {@code MAX} are refused, since C's element names carry
     * neither. {@code flags-off:} takes object flags only, with the same {@code NONE} rule.
     *
     * <p><b>Values.</b> In {@code values:} a {@code RES_<element>[n]} entry sets that element's
     * resistance level from an integer, and every other entry is a modifier whose bracket is a
     * dice string. The {@code min-values:} brackets are integers too. C reads those integers with
     * {@code strtol} in base 10 and wants the closing bracket straight after them
     * ({@code find_value_arg} in {@code datafile.c}), so the text must be one optional sign and
     * then digits only ({@link ParseUtils#nonDigitFound}), read in base 10. A figure at or past
     * {@code INT_MIN} or {@code INT_MAX} is refused, as C refuses it; the clamp in
     * {@link ParseUtils#parseIntString} marks it with those two values. Inside that range a
     * {@code RES_} figure is stored the way C stores it in the {@code int16_t res_level} of
     * {@code struct element_info}, so it keeps only its low 16 bits ({@code 40000} becomes
     * {@code -25536}), while a {@code min-values:} figure is kept whole. A repeated key keeps its
     * last line, as C's assignment does. A leading {@code +}, which {@code strtol} accepts, is
     * accepted here too, but such a line never arrives: the lexer's dice token cannot open with
     * one, so it is a syntax error (the "Numbers" item in {@code docs/Issue_Approach_Order.md}).
     *
     * <p><b>Brands, slays and curses.</b> The {@code brand:} and {@code slay:} lines name codes,
     * and the sets are built by walking {@link ObjectRegistry#getBrands()} and
     * {@link ObjectRegistry#getSlays()}, so they come out in registry order, C's index order and
     * the reverse of file order, whatever order the lines were written in. A repeated code changes
     * nothing, as C sets one flag per index. Each {@code curse:} line is applied in file order, as
     * {@code parse_ego_curse} does: an unknown name stops the load, a power above 0 stores the
     * curse with a timeout of 0 (replacing an earlier line for the same curse), and a power of 0
     * or below is ignored, so it never removes an earlier line.
     *
     * <p><b>Numbers.</b> The {@code info:} figures and the {@code alloc:} commonness are read in
     * base 0, as {@code parser_getint} reads them. The two {@code alloc:} depths are read in
     * base 10, as {@code grab_int_range} reads them, and must lie in 0 to 255
     * ({@code PARSE_ERROR_OUT_OF_BOUNDS}). A figure too big for an {@code int} is clamped where C
     * wraps it, the accepted deviation recorded under {@code ParseUtils} in
     * {@code docs/Accepted_Deviations.md}.
     *
     * <p><b>Absent directives.</b> A missing directive takes the value C's zeroed record holds:
     * the {@code combat:} and {@code time:} dice are a zero {@link Random}, the three
     * {@code min-combat:} floors are 255 ({@code NO_MINIMUM}), and the description is
     * {@code null}. An ego with no {@code act:} line, or one naming an unknown activation, has a
     * {@code null} activation with no error, as {@code findact} answers {@code NULL}.
     *
     * <p>Function assemble coded before 260915, commented in full on 261024.
     *
     * @param records  the raw ego-item records from the grammar, in file order
     * @param errors   never written to, because every failure throws; see above
     * @param filename the file the records were read from, named in the message of a failure
     * @return the assembled ego items, one per record, in file order
     * @throws DataFileParseException if any field of any record cannot be resolved
     */
    @Override
    public List<EgoItem> assemble(@NotNull List<EgoItemParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<EgoItem> items = new ArrayList<>();
        
        int index = 0;

        for (EgoItemParseRecord record : records) {
            int line = record.line();

            String name = record.name();
            List<ObjectKind> possItems = new ArrayList<>();
            int cost = ParseUtils.parseIntString("EgoItem", record.cost(), line, filename, "cost");
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            Flag<ObjectKindFlag> kFlags = new Flag<>(ObjectKindFlag.class);
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            for (String flag : record.flags()) {
                String eFlagName = "";
                String oFlagName = "";
                String kFlagName = "";
                boolean ignore = false;
                boolean hates = false;
                if (flag.contains("_") && "IGNORE".startsWith(flag.substring(0, flag.indexOf('_')))) {
                    eFlagName = "ELEM_" + flag.substring(flag.indexOf('_') + 1);
                    ignore = true;
                } else if (flag.contains("_") && "HATES".startsWith(flag.substring(0, flag.indexOf('_')))) {
                    eFlagName = "ELEM_" + flag.substring(flag.indexOf('_') + 1);
                    hates = true;
                } else {
                    oFlagName = "OF_" + flag;
                    kFlagName = "KF_" + flag;
                }
                boolean eFlagFound = false;
                boolean oFlagFound = false;
                boolean kFlagFound = false;

                ElementEnum eFlag = ElementEnum.ELEM_NONE;
                ObjectFlag oFlag = ObjectFlag.OF_NONE;
                ObjectKindFlag kFlag = ObjectKindFlag.KF_NONE;

                try {
                    eFlag = ElementEnum.valueOf(eFlagName);
                    if (eFlag == ElementEnum.ELEM_NONE || eFlag == ElementEnum.ELEM_MAX) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel element name: " + flag;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    eFlagFound = true;
                } catch (DataFileParseException d) {
                    throw d;
                } catch (Exception e) {
                    eFlagFound = false;
                }

                try {
                    oFlag = ObjectFlag.valueOf(oFlagName);
                    if (oFlag == ObjectFlag.OF_NONE) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel object flag name: " + flag;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    oFlagFound = true;
                } catch (DataFileParseException d) {
                    throw d;
                } catch (Exception e) {
                    oFlagFound = false;
                }

                try {
                    kFlag = ObjectKindFlag.valueOf(kFlagName);
                    kFlagFound = true;
                } catch (Exception e) {
                    kFlagFound = false;
                }

                if (eFlagFound && !oFlagFound && !kFlagFound) {
                    if (ignore && !hates) {
                        elInfo.computeIfAbsent(eFlag, k -> new ElementInfo())
                                .on(ElementInfoEnum.EL_INFO_IGNORE);
                    } else if (!ignore && hates) {
                        elInfo.computeIfAbsent(eFlag, k -> new ElementInfo())
                                .on(ElementInfoEnum.EL_INFO_HATES);
                    }
                } else if (!eFlagFound) {
                    if (oFlagFound) {
                        oFlags.on(oFlag);
                    }
                    if (kFlagFound) {
                        kFlags.on(kFlag);
                    } 
                    if (!kFlagFound && !oFlagFound) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal flag value: " + flag;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
            }
            Flag<ObjectFlag> oFlagsOff = new Flag<>(ObjectFlag.class);
            for (String flag : record.flagsOff()) {
                try {
                    ObjectFlag oFlag = ObjectFlag.valueOf("OF_" + flag);
                    if (oFlag == ObjectFlag.OF_NONE) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel object flag name: " + flag;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    oFlagsOff.on(oFlag);
                } catch (IllegalArgumentException e) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid object flag (off) name: " + flag;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                if (key.startsWith("RES_")) {
                    key = "ELEM_" + key.substring(4);
                    try {
                        ElementEnum eEnum = ElementEnum.valueOf(key);
                        if (eEnum == ElementEnum.ELEM_NONE || eEnum == ElementEnum.ELEM_MAX) {
                            String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                    "a sentinel element name: " + key;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                        if (ParseUtils.nonDigitFound(value)) {
                            String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                    "an non digit in the element value: " + key;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                        int val = ParseUtils.parseIntString("EgoItem", value, line, filename, "element resistance", false);
                        if (val == Integer.MIN_VALUE || val == Integer.MAX_VALUE) {
                            String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                    "an value out of bounds: " + value;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                        short sVal = (short) val;
                        ElementInfo info = elInfo.computeIfAbsent(eEnum, k -> new ElementInfo());
                        info.setResLevel(sVal);
                    } catch (IllegalArgumentException e) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid element modifier name: " + key;
                        logger.fatal(msg, e);
                        throw new DataFileParseException(msg);
                    }
                } else {
                    Random dice = Random.parseStr(value);
                    if (dice == null) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid object modifier dice value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    } else {
                        key = "OM_" + key;
                        try {
                            ObjectModifier oMod = ObjectModifier.valueOf(key);
                            if (oMod == ObjectModifier.OM_NONE || oMod == ObjectModifier.OM_MAX) {
                                String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                        "a sentinel object modifier name: " + key;
                                logger.fatal(msg);
                                throw new DataFileParseException(msg);
                            }
                            modifiers.put(oMod, dice);
                        } catch (IllegalArgumentException e) {
                            String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                    "an invalid object modifier name: " + key;
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                    }
                }
            }
            Map<ObjectModifier, Integer> minModifiers = new HashMap<>();
            for (String key : record.minValues().keySet()) {
                String value = record.minValues().get(key);
                try {
                    ObjectModifier oMod = ObjectModifier.valueOf("OM_" + key);
                    if (oMod == ObjectModifier.OM_NONE || oMod == ObjectModifier.OM_MAX) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel object modifier name: " + key;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    if (ParseUtils.nonDigitFound(value)) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "an non digit in the modifier value: " + key;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    int val = ParseUtils.parseIntString("EgoItem", value, line, filename, "minimum modifier value", false);
                    if (val == Integer.MIN_VALUE || val == Integer.MAX_VALUE) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "an out of range modifier value: " + key;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);    
                    }
                    minModifiers.put(oMod, val);
                } catch (IllegalArgumentException e) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid modifier value: " + key;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            Set<Brand> brands = new LinkedHashSet<>();
            Set<Brand> tempBrands = new LinkedHashSet<>();
            for (String brand : record.brands()) {
                Brand b = ObjectRegistry.lookupBrandCode(brand);
                if (b == null) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid brand: " + brand;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else
                    tempBrands.add(b);
            }
            for (Brand brand : ObjectRegistry.getBrands()) {
                if (tempBrands.contains(brand)) {
                    brands.add(brand);
                }
            }
            Set<Slay> slays = new LinkedHashSet<>();
            Set<Slay> tempSlays = new LinkedHashSet<>();
            for (String slay : record.slays()) {
                Slay s = ObjectRegistry.lookupSlay(slay);
                if (s == null) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid slay: " + slay;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else
                    tempSlays.add(s);
            }
            for (Slay slay : ObjectRegistry.getSlays()) {
                if (tempSlays.contains(slay)) {
                    slays.add(slay);
                }
            }
            TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
            for (FullCurseDataParseRecord curseDataRecord : record.curses()) {
                String cursePower = curseDataRecord.power();
                Curse c = ObjectRegistry.lookupCurse(curseDataRecord.name());
                if (c == null) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid to curse name value: " + curseDataRecord.name();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else {
                    int power = ParseUtils.parseIntString("EgoItem", cursePower, line, filename, "curse power");
                    if (power > 0) {
                        CurseData data = new CurseData(power, 0);
                        curses.put(c, data);
                    }
                }
            }
            int rating = ParseUtils.parseIntString("EgoItem", record.rating(), line, filename, "rating info");
            int commonness = ParseUtils.parseIntString("EgoItem", record.commonness(), line, filename, "alloc commonness");
            int lower = ParseUtils.parseIntString("EgoItem", record.lower(), line, filename, "alloc lower bound", false);
            if (0 > lower || lower > 255) {
                String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                        "an out of bounds alloc lower bound: " + record.lower();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int upper = ParseUtils.parseIntString("EgoItem", record.upper(), line, filename, "alloc upper bound", false);
            if (0 > upper || upper > 255) {
                String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                        "an out of bounds alloc upper bound: " + record.upper();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            for (EgoItemParseRecord.ItemRef item : record.itemRefs()) {
                try {
                    TValue tValue = TValue.fromName(item.tVal());
                    if (tValue == null) {
                        String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid tValue: " + item.tVal();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    ObjectKind o;
                    if (!item.sVal().isEmpty()) {
                        o = ObjectRegistry.lookupObjectKind(tValue, item.sVal());
                        if (o == null) {
                            String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                    "an invalid sValue: " + item.sVal();
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        } else
                            possItems.add(o);
                    } else {
                        List<ObjectKind> objectKinds = ObjectRegistry.lookupObjectKind(tValue);
                        if (objectKinds.isEmpty()) {
                            String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                                    "an invalid tvalue: " + item.tVal();
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        } else {
                            possItems.addAll(objectKinds.reversed());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid tValue/sValue pair: " + item.tVal() + "/" + item.sVal();
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            Random toH = Random.Zero();
            if (!record.toH().isEmpty()) {
                toH = Random.parseStr(record.toH());
                if (toH == null) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid to hit dice value: " + record.toH();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Random toD = Random.Zero();
            if (!record.toD().isEmpty()) {
                toD = Random.parseStr(record.toD());
                if (toD == null) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid to damage dice value: " + record.toD();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Random toA = Random.Zero();
            if (!record.toA().isEmpty()) {
                toA = Random.parseStr(record.toA());
                if (toA == null) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid to AC dice value: " + record.toA();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            int minToh = ParseUtils.parseIntString("EgoItem", record.minToH(), line, filename, "min combat to hit", 255);
            int minTod = ParseUtils.parseIntString("EgoItem", record.minToD(), line, filename, "min combat to damage", 255);
            int minToa = ParseUtils.parseIntString("EgoItem", record.minToA(), line, filename, "min combat to AC", 255);
            Activation activation = null;
            if (!record.act().isEmpty()) {
                activation = ObjectRegistry.lookupActivation(record.act());
            }
            Random time = Random.Zero();
            if (!record.timeout().isEmpty()) {
                time = Random.parseStr(record.timeout());
                if (time == null) {
                    String msg = "EgoItem starting at line: " + line + " of file: " + filename + " has " +
                            "an invalid time dice value: " + record.timeout();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            String desc = record.desc().isEmpty() ? null : record.desc();

            items.add(new EgoItem(name, desc, index,
                    cost, oFlags, oFlagsOff, kFlags,
                    modifiers, minModifiers, elInfo, brands,
                    slays, curses, rating, commonness, lower,
                    upper, possItems, toH, toD, toA, minToh,
                    minTod, minToa, activation, time, false));
            index++;
        }

        return items;
    }
}