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

package uk.co.jackoftradesltd.backend.parser.objectbase;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles the raw {@link ObjectBaseParseRecord}s parsed from {@code object_base.txt} into the
 * finished {@link ObjectBase} domain objects (reader &rarr; ParseRecord &rarr; assembler &rarr;
 * domain).
 *
 * @author Rowan Crowther
 */
public class ObjectBaseAssembler implements Assembler<ObjectBaseParseRecord, List<ObjectBase>> {
    /**
     * Resolve every parsed {@code object_base.txt} record into an {@link ObjectBase}: the tval
     * name to {@link TValue}, the colour code to {@link ColourEnum}, each flag either to an
     * {@link ElementEnum} (for a {@code HATES_}-prefixed flag) or an {@link ObjectKindFlag}, and
     * the two numeric fields to {@code int}s. An unresolvable field is a soft error and drops the
     * whole record. After every record is processed, a synthetic {@code TV_NONE} base is appended
     * unconditionally as a sentinel "no base" entry - it is not driven by any parsed record.
     *
     * <p>Function assemble coded before 260915, commented in full on 260915.
     *
     * @param records the raw object-base records from the grammar, in file order
     * @param errors  the soft-error sink; a message is appended for each unresolvable field and
     *                the offending record is skipped rather than aborting the whole load
     * @return the successfully assembled object bases, in file order, plus the trailing
     * {@code TV_NONE} sentinel
     */
    @Override
    public List<ObjectBase> assemble(@NotNull List<ObjectBaseParseRecord> records, @NotNull List<String> errors) {
        List<ObjectBase> results = new ArrayList<>();

        for (ObjectBaseParseRecord record : records) {
            String name = record.name();
            String rawTVal = record.tVal().trim();
            TValue tVal;
            tVal = TValue.fromName(rawTVal);
            if (tVal == null) {
                errors.add("Block starting at line: " + record.line() +
                        " has an invalid TValue " + rawTVal);
                continue;
            }
            String rawColour = record.colour();
            ColourEnum colour;
            colour = ColourEnum.fromCode(rawColour);
            if (colour == null) {
                errors.add("Block starting at line: " + record.line() + " has " +
                        "an invalid colour " + rawColour);
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

        results.add(new ObjectBase(TValue.TV_NONE, "none", ColourEnum.COLOUR_DARK,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, 0));

        return results;
    }
}
