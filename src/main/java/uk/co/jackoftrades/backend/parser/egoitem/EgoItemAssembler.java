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

package uk.co.jackoftrades.backend.parser.egoitem;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EgoItemAssembler implements Assembler<EgoItemParseRecord, List<EgoItem>> {
    /**
     *
     * @param records List of R_ParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of R objects
     */
    @Override
    public List<EgoItem> assemble(@NotNull List<EgoItemParseRecord> records, @NotNull List<String> errors) {
        List<EgoItem> items = new ArrayList<>();

        for (EgoItemParseRecord record : records) {
            int line = record.line();

            String name = record.name();
            List<ObjectKind> possItems = new ArrayList<>();
            boolean illegalTValue = false;
            for (String tVal : record.tVals()) {
                try {
                    TValue tValue = TValue.valueOf("TV_" + tVal.toUpperCase().replace(" ", "_"));
                    List<ObjectKind> o = GameConstants.lookupObjectKind(tValue);
                    if (!o.isEmpty()) {
                        possItems.addAll(o);
                    } else {
                        errors.add("EgoItem beginning at line: " + line + " has " +
                                "no known ObjectKinds associated with TValue: " + tVal);
                        illegalTValue = true;
                    }
                } catch (IllegalArgumentException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an unknown type TValue: " + tVal);
                    illegalTValue = true;
                }
            }
            if (illegalTValue) continue;
            int cost = 0;
            if (!record.cost().isEmpty()) {
                try {
                    cost = Integer.parseInt(record.cost());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed cost integer: " + record.cost());
                    continue;
                }
            }
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            Flag<ObjectKindFlag> kFlags = new Flag<>(ObjectKindFlag.class);
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            boolean illegalFlag = false;
            for (String flag : record.flags()) {
                if (flag.startsWith("IGNORE_")) {
                    String elemName = "ELEM_" + flag.substring(7);
                    try {
                        ElementEnum eFlag = ElementEnum.valueOf(elemName);
                        elInfo.computeIfAbsent(eFlag, k -> new ElementInfo())
                                .on(ElementInfoEnum.EL_INFO_IGNORE);
                    } catch (IllegalArgumentException e) {
                        errors.add("EgoItem beginning at line: " + line + " has " +
                                "an unknown element flag: " + flag);
                        illegalFlag = true;
                    }
                } else {
                    try {
                        ObjectFlag oFlag = ObjectFlag.valueOf("OF_" + flag);
                        oFlags.on(oFlag);
                    } catch (IllegalArgumentException e) {
                        try {
                            ObjectKindFlag kFlag = ObjectKindFlag.valueOf("KF_" + flag);
                            kFlags.on(kFlag);
                        } catch (IllegalArgumentException ex) {
                            errors.add("EgoItem beginning at line: " + line + " has " +
                                    "an unknown flag: " + flag);
                            illegalFlag = true;
                        }
                    }
                }
            }
            if (illegalFlag) continue;
            Flag<ObjectFlag> oFlagsOff = new Flag<>(ObjectFlag.class);
            boolean illegalFlagOff = false;
            for (String flag : record.flagsOff()) {
                try {
                    ObjectFlag oFlag = ObjectFlag.valueOf("OF_" + flag);
                    oFlagsOff.on(oFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an unknown flag-off: " + flag);
                    illegalFlagOff = true;
                }
            }
            if (illegalFlagOff) continue;
            boolean illegalValueMap = false;
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                if (key.startsWith("RES_")) {
                    key = "ELEM_" + key.substring(4);
                    try {
                        ElementEnum eEnum = ElementEnum.valueOf(key);
                        int val = Integer.parseInt(value);
                        ElementInfo info = elInfo.computeIfAbsent(eEnum, k -> new ElementInfo());
                        info.setResLevel(val);
                    } catch (NumberFormatException e) {
                        errors.add("EgoItem beginning at line: " + line + " has " +
                                "a malformed integer value: " + value);
                        illegalValueMap = true;
                    } catch (IllegalArgumentException e) {
                        errors.add("EgoItem beginning at line: " + line + " has " +
                                "an unknown value flag: " + "RES_" + key.substring(5));
                        illegalValueMap = true;
                    }
                } else {
                    Random dice = Random.parseStr(value);
                    if (dice == null) {
                        errors.add("EgoItem beginning at line: " + line + " has " +
                                "a malformed dice value: " + value);
                        illegalValueMap = true;
                    } else {
                        key = "OM_" + key;
                        try {
                            ObjectModifier oMod = ObjectModifier.valueOf(key);
                            modifiers.put(oMod, dice);
                        } catch (IllegalArgumentException e) {
                            errors.add("EgoItem beginning at line: " + line + " has " +
                                    "an unknown modifier: " + key.substring(3));
                            illegalValueMap = true;
                        }
                    }
                }
            }
            if (illegalValueMap) continue;
            Map<ObjectModifier, Integer> minModifiers = new HashMap<>();
            boolean illegalMinModifier = false;
            for (String key : record.minValues().keySet()) {
                String value = record.minValues().get(key);
                try {
                    ObjectModifier oMod = ObjectModifier.valueOf("OM_" + key);
                    int val = Integer.parseInt(value);
                    minModifiers.put(oMod, val);
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed min value: " + value);
                    illegalMinModifier = true;
                } catch (IllegalArgumentException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an unknown min value name: " + key);
                    illegalMinModifier = true;
                }
            }
            if (illegalMinModifier) continue;
            Map<Brand, Boolean> brands = new HashMap<>();
            boolean illegalBrand = false;
            for (String brand : record.brands()) {
                Brand b = GameConstants.lookupBrandCode(brand);
                if (b == null) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an unknown brand: " + brand);
                    illegalBrand = true;
                } else
                    brands.put(b, true);
            }
            if (illegalBrand) continue;
            Map<Slay, Boolean> slays = new HashMap<>();
            boolean illegalSlay = false;
            for (String slay : record.slays()) {
                Slay s = GameConstants.lookupSlay(slay);
                if (s == null) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an unknown slay: " + slay);
                    illegalSlay = true;
                } else
                    slays.put(s, true);
            }
            if (illegalSlay) continue;
            Map<Curse, CurseData> curses = new HashMap<>();
            boolean illegalCurse = false;
            for (String key : record.curses().keySet()) {
                String cursePower = record.curses().get(key);
                Curse c = GameConstants.lookupCurse(key);
                if (c == null) {
                    errors.add("EgoItem beginning at line:" + line + " has " +
                            "an unknown curse: " + key);
                    illegalCurse = true;
                } else {
                    try {
                        int power = Integer.parseInt(cursePower);
                        CurseData data = new CurseData(power, 0);
                        curses.put(c, data);
                    } catch (NumberFormatException e) {
                        errors.add("EgoItem beginning at line: " + line + " has " +
                                "an malformed curse power: " + cursePower);
                        illegalCurse = true;
                    }
                }
            }
            if (illegalCurse) continue;
            int rating = 0;
            if (!record.rating().isEmpty()) {
                try {
                    rating = Integer.parseInt(record.rating());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed rating on its info line:" + record.rating());
                    continue;
                }
            }
            int commonness = 0;
            if (!record.commonness().isEmpty()) {
                try {
                    commonness = Integer.parseInt(record.commonness());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed commonness on its alloc line:" + record.commonness());
                    continue;
                }
            }
            int lower = 0;
            if (!record.lower().isEmpty()) {
                try {
                    lower = Integer.parseInt(record.lower());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed lower bound on its alloc line:" + record.lower());
                    continue;
                }
            }
            int upper = 0;
            if (!record.upper().isEmpty()) {
                try {
                    upper = Integer.parseInt(record.upper());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed upper bound on its alloc line:" + record.upper());
                    continue;
                }
            }
            boolean illegalTvalSvalPair = false;
            for (EgoItemParseRecord.ItemRef item : record.itemRefs()) {
                try {
                    TValue tValue = TValue.valueOf("TV_" + item.tVal().toUpperCase().replace(" ", "_"));
                    ObjectKind o = GameConstants.lookupObjectKind(tValue, item.sVal());
                    if (o != null) {
                        possItems.add(o);
                    } else {
                        errors.add("EgoItem beginning at line: " + line + " has " +
                                "an illegal sValue in its item: " + item.sVal());
                        illegalTvalSvalPair = true;
                    }
                } catch (IllegalArgumentException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an illegal TValue in its item: " + item.tVal());
                    illegalTvalSvalPair = true;
                }
            }
            if (illegalTvalSvalPair) continue;
            Random toH = null;
            if (!record.toH().isEmpty()) {
                toH = Random.parseStr(record.toH());
                if (toH == null) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed combat dice format: " + record.toH());
                    continue;
                }
            }
            Random toD = null;
            if (!record.toD().isEmpty()) {
                toD = Random.parseStr(record.toD());
                if (toD == null) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed combat dice format: " + record.toD());
                    continue;
                }
            }
            Random toA = null;
            if (!record.toA().isEmpty()) {
                toA = Random.parseStr(record.toA());
                if (toA == null) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed combat dice format: " + record.toA());
                    continue;
                }
            }
            int minToh = 0;
            if (!record.minToH().isEmpty()) {
                try {
                    minToh = Integer.parseInt(record.minToH());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed min combat int format: " + record.minToH());
                    continue;
                }
            }
            int minTod = 0;
            if (!record.minToD().isEmpty()) {
                try {
                    minTod = Integer.parseInt(record.minToD());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed min combat int format: " + record.minToD());
                    continue;
                }
            }
            int minToa = 0;
            if (!record.minToA().isEmpty()) {
                try {
                    minToa = Integer.parseInt(record.minToA());
                } catch (NumberFormatException e) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "a malformed min combat int format: " + record.minToA());
                    continue;
                }
            }
            Activation activation = null;
            if (!record.act().isEmpty()) {
                activation = GameConstants.lookupActivation(record.act());
                if (activation == null) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an unknown activation type: " + record.act());
                    continue;
                }
            }
            Random time = null;
            if (!record.timeout().isEmpty()) {
                time = Random.parseStr(record.timeout());
                if (time == null) {
                    errors.add("EgoItem beginning at line: " + line + " has " +
                            "an unknown time dice: " + record.timeout());
                    continue;
                }
            }
            String desc = record.desc();

            items.add(new EgoItem(name, desc, 0,
                    cost, oFlags, oFlagsOff, kFlags,
                    modifiers, minModifiers, elInfo, brands,
                    slays, curses, rating, commonness, lower,
                    upper, possItems, toH, toD, toA, minToh,
                    minTod, minToa, activation, time, false));
        }

        return items;
    }
}
