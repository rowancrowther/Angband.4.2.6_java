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

package uk.co.jackoftrades.backend.parser.objectbase;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

public class ObjectBaseAssembler implements Assembler<ObjectBaseParseRecord, List<ObjectBase>> {
    /**
     *
     * @param records List of R_ParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of R objects
     */
    @Override
    public List<ObjectBase> assemble(@NotNull List<ObjectBaseParseRecord> records, @NotNull List<String> errors) {
        List<ObjectBase> results = new ArrayList<>();

        for (ObjectBaseParseRecord record : records) {
            String name = record.name();
            String rawTVal = record.tVal().trim().toUpperCase();
            if (rawTVal.contains(" ")) rawTVal = rawTVal.replace(" ", "_");
            TValue tVal;
            try {
                tVal = TValue.valueOf("TV_" + rawTVal);
            } catch (IllegalArgumentException e) {
                errors.add("Block starting at line: " + record.line() +
                        " has an invalid TValue " + rawTVal);
                continue;
            }
            String rawColour = record.colour();
            ColourType colour;
            try {
                colour = ColourType.getColourType(rawColour);
            } catch (IllegalArgumentException e) {
                errors.add("Block starting at line: " + record.line() +
                        " has an invalid Colour " + rawColour);
                continue;
            }
            Flag<ObjectKindFlag> kindFlag = new Flag<>(ObjectKindFlag.class);
            Flag<ElementEnum> hatesFlag = new Flag<>(ElementEnum.class);
            boolean illegalFlag = false;
            for (String flag : record.flags()) {
                if (flag.startsWith("HATES_")) {
                    String rawFlag = flag.substring(6);
                    try {
                        ElementEnum ee = ElementEnum.valueOf("ELEM_" + rawFlag);
                        hatesFlag.on(ee);
                    } catch (IllegalArgumentException e) {
                        errors.add("Block starting at line: " + record.line() +
                                " has an invalid HATES_ flag: " + flag);
                        illegalFlag = true;
                    }
                } else {
                    try {
                        ObjectKindFlag kf = ObjectKindFlag.valueOf("KF_" + flag);
                        kindFlag.on(kf);
                    } catch (IllegalArgumentException e) {
                        errors.add("Block starting at line: " + record.line() +
                                " has an invalid non-HATES_ flag: " + flag);
                        illegalFlag = true;
                    }
                }
            }
            if (illegalFlag) continue;
            int breakChance;
            try {
                breakChance = Integer.parseInt(record.breakChance());
            } catch (NumberFormatException e) {
                errors.add("Block starting at line: " + record.line() +
                        " has an invalid break chance: " + record.breakChance());
                continue;
            }
            int maxStack;
            try {
                maxStack = Integer.parseInt(record.maxStack());
            } catch (NumberFormatException e) {
                errors.add("Block starting at line: " + record.line() +
                        " has an invalid max stack: " + record.maxStack());
                continue;
            }

            results.add(new ObjectBase(tVal, name, colour, kindFlag, hatesFlag, breakChance, maxStack));
        }
        return results;
    }
}
