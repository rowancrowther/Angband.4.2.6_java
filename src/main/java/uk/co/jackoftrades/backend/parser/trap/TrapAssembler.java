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

package uk.co.jackoftrades.backend.parser.trap;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.cave.TrapKind;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link TrapParseRecord}s from {@code TrapGrammar} into domain {@link TrapKind}s —
 * C's {@code struct trap_kind}, the trap template loaded from {@code trap.txt} (Java port of the
 * {@code parse_trap_*} handlers in {@code src/init.c}).
 *
 * <p>This is where interpretation happens:
 * <ul>
 *   <li>the {@code name:}/{@code desc:} crossover is preserved — the {@code name:} line's second
 *       field becomes {@link TrapKind}'s {@code description} (C {@code desc}, the
 *       {@code lookupTrap} key) while the {@code desc:} directive becomes its {@code text}
 *       (C {@code text});</li>
 *   <li>{@code graphics:} resolves its colour through {@link ColourType} into an
 *       {@link AngbandDisplayCharacter}; {@code visibility:} becomes the {@code power}
 *       {@link Random}; the {@code appear:} triple becomes {@code rarity}/{@code minDepth}/
 *       {@code maxNum};</li>
 *   <li>{@code flags:} resolve to {@link TrapEnum} ({@code TRF_}) and {@code save:} to
 *       {@link ObjectFlag} ({@code OF_}) — two distinct flag vocabularies;</li>
 *   <li>primary and extra effects are delegated to {@link EffectAssembler}; and
 *       {@code trapKindIndex} is assigned by source position, mirroring C's {@code tidx}.</li>
 * </ul>
 *
 * <p><b>Error policy:</b> soft errors follow the suite's skip-and-continue contract — an
 * unresolvable colour, flag or save flag, or a non-integer {@code appear:}/{@code visibility:}
 * value, logs a message to {@code errors} and drops that one trap ({@code continue}), leaving the
 * rest of the file to load. Hard grammar/lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * @author Rowan Crowther
 */
public class TrapAssembler implements Assembler<TrapParseRecord, List<TrapKind>> {
    /**
     * Assembles every parsed trap record into a {@link TrapKind}, skipping (with a logged soft
     * error) any record whose colour, flags, save flags, or numeric {@code appear:}/
     * {@code visibility:} fields fail to resolve.
     *
     * @param records the raw trap records captured by the grammar
     * @param errors  the soft-error channel; assembly failures are appended here and the offending
     *                record is dropped rather than aborting the whole file
     * @return the successfully assembled trap kinds, in source order
     * @author Rowan Crowther
     */
    @Override
    public List<TrapKind> assemble(@NotNull List<TrapParseRecord> records, @NotNull List<String> errors) {
        List<TrapKind> traps = new ArrayList<>();
        int index = -1;

        for (TrapParseRecord record : records) {
            int line = record.line();
            String trapKindName = record.name();
            String trapText = record.text();
            String trapDescription = record.description();
            String trapMessage = record.message();
            String saveMessage = record.saveMessage();
            String failMessage = record.failMessage();
            String extraMessage = record.xtraMessage();
            String trapIndexStr = record.index();
            index++;
            char glyphChar;
            AngbandDisplayCharacter adc = null;
            String glyph = record.glyph();
            String colour = record.colour();
            // Check that they both exist
            if (!glyph.isEmpty() && !colour.isEmpty()) {
                ColourType colourType = ColourType.getColourType(colour);
                if (colourType == null) {
                    errors.add("Trap at line: " + line + " has " +
                            "an unknown colour: " + colour);
                    continue;
                }
                glyphChar = glyph.charAt(0);
                adc = new AngbandDisplayCharacter(glyphChar, colourType);
            }
            String rarityStr = record.rarity();
            int rarity = 0;
            if (!rarityStr.isEmpty()) {
                try {
                    rarity = Integer.parseInt(rarityStr);
                } catch (NumberFormatException e) {
                    errors.add("Trap at line " + line + " has " +
                            "an invalid appear first integer: " + rarityStr);
                    continue;
                }
            }
            String minDepthStr = record.minDepth();
            int minDepth = 0;
            if (!minDepthStr.isEmpty()) {
                try {
                    minDepth = Integer.parseInt(minDepthStr);
                } catch (NumberFormatException e) {
                    errors.add("Trap at line: " + line + " has " +
                            "an invalid appear second integer: " + minDepthStr);
                    continue;
                }
            }
            int maxNum = 0;
            String maxNumStr = record.maxNum();
            if (!maxNumStr.isEmpty()) {
                try {
                    maxNum = Integer.parseInt(maxNumStr);
                } catch (NumberFormatException e) {
                    errors.add("Trap at line: " + line + " has " +
                            "an invalid appear third integer: " + maxNumStr);
                    continue;
                }
            }
            Random power = null;
            String powerStr = record.power();
            if (!powerStr.isEmpty()) {
                power = Random.parseStr(powerStr);
                if (power == null) {
                    errors.add("Trap at line: " + line + " has " +
                            "an invalid visibility line: " + powerStr);
                    continue;
                }
            }
            Flag<TrapEnum> flags = new Flag<>(TrapEnum.class);
            boolean badFlag = false;
            for (String flag : record.flags()) {
                try {
                    flags.on(TrapEnum.valueOf("TRF_" + flag));
                } catch (IllegalArgumentException e) {
                    errors.add("Trap at line: " + line + " has " +
                            "an unknown flag: " + flag);
                    badFlag = true;
                }
            }
            if (badFlag) continue;
            Flag<ObjectFlag> saveFlags = new Flag<>(ObjectFlag.class);
            boolean badSaveFlag = false;
            for (String flag : record.saveFlags()) {
                try {
                    saveFlags.on(ObjectFlag.valueOf("OF_" + flag));
                } catch (IllegalArgumentException e) {
                    errors.add("Trap at line: " + line + " has " +
                            "an unknown save flag: " + flag);
                    badSaveFlag = true;
                }
            }
            if (badSaveFlag) continue;
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors);
            List<Effect> xtraEffects = EffectAssembler.assemble(record.xtraEffects(), errors);

            traps.add(new TrapKind(trapKindName, trapText, trapDescription,
                    trapMessage, saveMessage, failMessage, extraMessage,
                    index, adc, rarity, minDepth, maxNum, power, flags,
                    saveFlags, effects, xtraEffects));
        }

        return traps;
    }
}
