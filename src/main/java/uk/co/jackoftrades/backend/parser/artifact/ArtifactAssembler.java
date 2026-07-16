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

package uk.co.jackoftrades.backend.parser.artifact;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtifactAssembler implements Assembler<ArtifactParseRecord, List<Artifact>> {
    /**
     *
     * @param records List of R_ParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of R objects
     */
    @Override
    public List<Artifact> assemble(@NotNull List<ArtifactParseRecord> records, @NotNull List<String> errors) {
        List<Artifact> artifacts = new ArrayList<>();

        for (ArtifactParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String text = record.desc();
            int index = 0;
            String tValStr = record.tValue();
            TValue tVal = null;
            if (!tValStr.isEmpty()) {
                tVal = TValue.fromName("TV_" + tValStr);
                if (tVal == null) {
                    errors.add("Artifact at line: " + line + " has " +
                            "an unknown base-obj tvalue: " + tValStr);
                    continue;
                }
            }
            String sVal = record.sValue();
            int toh = 0;
            if (!record.toh().isEmpty()) {
                try {
                    toh = Integer.parseInt(record.toh());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed to hit number: " + record.toh());
                    continue;
                }
            }
            int tod = 0;
            if (!record.tod().isEmpty()) {
                try {
                    tod = Integer.parseInt(record.tod());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed to damage number: " + record.tod());
                    continue;
                }
            }
            int baseAC = 0;
            if (!record.baseAC().isEmpty()) {
                try {
                    baseAC = Integer.parseInt(record.baseAC());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed base AC: " + record.baseAC());
                    continue;
                }
            }
            int toa = 0;
            if (!record.toa().isEmpty()) {
                try {
                    toa = Integer.parseInt(record.toa());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed attack toac number: " + record.toa());
                    continue;
                }
            }
            String baseDamageString = record.baseDamage();
            int weight = 0;
            if (!record.weight().isEmpty()) {
                try {
                    weight = Integer.parseInt(record.weight());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed weight: " + record.weight());
                    continue;
                }
            }
            int cost = 0;
            if (!record.cost().isEmpty()) {
                try {
                    cost = Integer.parseInt(record.cost());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed cost: " + record.cost());
                    continue;
                }
            }
            boolean illegalFlag = false;
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            for (String flag : record.flagList()) {
                ObjectFlag oFlag = null;
                try {
                    oFlag = ObjectFlag.valueOf("OF_" + flag.toUpperCase());
                    flags.on(oFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "an unknown flag: " + flag);
                    illegalFlag = true;
                }
            }
            if (illegalFlag) continue;
            boolean illegalModifier = false;
            Map<ObjectModifier, Integer> modifiers = new HashMap<ObjectModifier, Integer>();
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            for (String key : record.values().keySet()) {
                try {
                    int value = Integer.parseInt(record.values().get(key));
                    if (key.startsWith("RES_")) {
                        key = key.substring(4);
                        ElementEnum element = ElementEnum.valueOf("ELEM_" + key);
                        ElementInfo eInfo = elInfo.computeIfAbsent(element, k -> new ElementInfo());
                        eInfo.setResLevel(value);
                    } else {
                        ObjectModifier mod = ObjectModifier.valueOf("OM_" + key.toUpperCase());
                        modifiers.put(mod, value);
                    }
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformend value integer: " + record.values().get(key));
                    illegalModifier = true;
                } catch (IllegalArgumentException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "an unknown modifier/element name: " + key);
                    illegalModifier = true;
                }
            }
            if (illegalModifier) continue;
            List<Brand> brands = new ArrayList<>();
            boolean illegalBrand = false;
            for (String b : record.brand()) {
                Brand brand = GameConstants.lookupBrandCode(b);
                if (brand != null) {
                    brands.add(brand);
                } else {
                    errors.add("Artifact at line: " + line + " has " +
                            "an unknown brand: " + b);
                    illegalBrand = true;
                }
            }
            if (illegalBrand) continue;
            boolean illegalSlay = false;
            List<Slay> slays = new ArrayList<>();
            for (String s : record.slay()) {
                Slay slay = GameConstants.lookupSlay(s);
                if (slay != null) {
                    slays.add(slay);
                } else {
                    errors.add("Artifact at line: " + line + " has " +
                            "an unknown slay: " + s);
                    illegalSlay = true;
                }
            }
            if (illegalSlay) continue;
            boolean illegalCurses = false;
            Map<Curse, CurseData> curses = new HashMap<>();
            for (String key : record.curse().keySet()) {
                String value = record.curse().get(key);
                Curse curse = GameConstants.lookupCurse(key);
                int power = 0;
                try {
                    power = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed curse power: " + value);
                    illegalCurses = true;
                }
                if (!illegalCurses) {
                    if (curse == null) {
                        illegalCurses = true;
                        errors.add("Artifact at line: " + line + " has " +
                                "an unknown curse: " + key);
                    } else {
                        int p = power;
                        curses.computeIfAbsent(curse, c -> new CurseData(p, 0));
                    }
                }
            }
            if (illegalCurses) continue;
            int level = 0;
            if (!record.level().isEmpty()) {
                try {
                    level = Integer.parseInt(record.level());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed level: " + record.level());
                    continue;
                }
            }
            int commonness = 0;
            if (!record.commonness().isEmpty()) {
                try {
                    commonness = Integer.parseInt(record.commonness());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed alloc chance: " + record.commonness());
                    continue;
                }
            }
            int min = 0;
            if (!record.min().isEmpty()) {
                try {
                    min = Integer.parseInt(record.min());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed alloc min: " + record.min());
                    continue;
                }
            }
            int max = 0;
            if (!record.max().isEmpty()) {
                try {
                    max = Integer.parseInt(record.max());
                } catch (NumberFormatException e) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed alloc max: " + record.max());
                    continue;
                }
            }
            Activation activation = null;
            if (!record.activation().isEmpty()) {
                activation = GameConstants.lookupActivation(record.activation());
                if (activation == null) {
                    errors.add("Artifact at line: " + line + " has " +
                            "an unknown activation: " + activation);
                    continue;
                }
            }
            String activationMsg = record.msg();
            Random time = null;
            if (!record.time().isEmpty()) {
                time = Random.parseStr(record.time());
                if (time == null) {
                    errors.add("Artifact at line: " + line + " has " +
                            "a malformed time dice string: " + record.time());
                    continue;
                }
            }

            artifacts.add(new Artifact(name, text, index, tVal, sVal, toh, tod, toa,
                    baseAC, baseDamageString, weight, cost, flags, modifiers, elInfo,
                    brands, slays, curses, level, commonness, min, max, activation,
                    activationMsg, time));
        }

        return artifacts;
    }
}
