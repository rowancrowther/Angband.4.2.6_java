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

package uk.co.jackoftradesltd.backend.parser.trap;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

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
 *   <li>{@code graphics:} resolves its colour through {@link ColourEnum} into an
 *       {@link AngbandDisplayCharacter}; {@code visibility:} becomes the {@code power}
 *       {@link Random}; the {@code appear:} triple becomes {@code rarity}/{@code minDepth}/
 *       {@code maxNum};</li>
 *   <li>a directive the record leaves out takes C's zeroed value, because C's {@code mem_zalloc}
 *       leaves the fields at zero: {@code power} is {@link Random#Zero()} (five shipped kinds,
 *       {@code no trap}, {@code glyph of warding}, {@code decoy}, {@code door lock} and
 *       {@code web}, have no {@code visibility:} line), the display character is
 *       {@link AngbandDisplayCharacter#blank}, and the {@code appear:} numbers are 0. The messages
 *       are the exception: an absent {@code msg:}, {@code msg-good:}, {@code msg-bad:} or
 *       {@code msg-xtra:} is empty text here, where C holds {@code NULL} and tests the pointer
 *       before printing;</li>
 *   <li>{@code flags:} resolve to {@link TrapEnum} ({@code TRF_}) and {@code save:} to
 *       {@link ObjectFlag} ({@code OF_}) — two distinct flag vocabularies;</li>
 *   <li>primary and extra effects are delegated to {@link EffectAssembler}; and
 *       {@code trapKindIndex} is assigned by position among the records that assemble, mirroring C's
 *       {@code tidx}. Because a dropped record takes no index, every later kind moves down one, so
 *       index 0 stays the first kept record (normally {@code no trap}, which {@code lookup_trap()}
 *       skips).</li>
 * </ul>
 *
 * <p><b>Error policy:</b> soft errors follow the suite's skip-and-continue contract — an
 * unresolvable colour, flag or save flag, a non-integer {@code appear:}/{@code visibility:}
 * value, or a primary or extra effect block that {@link EffectAssembler} rejects (it returns
 * {@code null}), logs a message to {@code errors} and drops that one trap ({@code continue}),
 * leaving the rest of the file to load. C stops the load at a bad effect; the port skips the
 * record instead, as it does for every other bad value. A dropped record takes no
 * {@code trapKindIndex}, because the index is only advanced once the record has fully assembled.
 * Hard grammar/lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * <p>Still accepted here that C rejects: the sentinels, so {@code flags:MAX} and {@code save:MAX}
 * load, because {@code valueOf} finds {@code TRF_MAX} and {@code OF_MAX}. {@code NONE} is valid in
 * both languages.
 *
 * <p>Class TrapAssembler coded before 260915, commented in full on 260915, updated on 261020 when
 * absent {@code power} and {@code graphics:} stopped being {@code null} and a bad effect started
 * dropping the record.
 *
 * @author Rowan Crowther
 */
public class TrapAssembler implements Assembler<TrapParseRecord, List<TrapKind>> {
    /**
     * Assembles every parsed trap record into a {@link TrapKind}, skipping (with a logged soft
     * error) any record whose colour, flags, save flags, numeric {@code appear:}/
     * {@code visibility:} fields or effect blocks fail to resolve.
     *
     * <p>A kind never leaves here with a {@code null} power, display character or effect list: an
     * absent {@code visibility:} line gives {@link Random#Zero()}, an absent {@code graphics:} line
     * gives {@link AngbandDisplayCharacter#blank}, and a bad effect block drops the record.
     * {@code blank} is a space in white, where C's zeroed {@code d_attr} is {@code COLOUR_DARK};
     * no shipped record is without {@code graphics:}, so the difference shows only in a modded file.
     *
     * <p>Function assemble coded before 260915, commented in full on 260915, updated on 261020 for
     * the zeroed defaults and the dropped bad-effect record.
     *
     * @param records the raw trap records captured by the grammar
     * @param errors  the soft-error channel; assembly failures are appended here and the offending
     *                record is dropped rather than aborting the whole file
     * @return the successfully assembled trap kinds, in source order
     */
    @Override
    public List<TrapKind> assemble(@NotNull List<TrapParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
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
            char glyphChar;
            AngbandDisplayCharacter adc = AngbandDisplayCharacter.blank;
            String glyph = record.glyph();
            String colour = record.colour();
            // Check that they both exist
            if (!glyph.isEmpty() && !colour.isEmpty()) {
                ColourEnum colourType = ColourEnum.fromCode(colour);
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
            Random power = Random.Zero();
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
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            if (effects == null) continue;
            List<Effect> xtraEffects = EffectAssembler.assemble(record.xtraEffects(), errors, filename);
            if (xtraEffects == null) continue;
            index++;

            traps.add(new TrapKind(trapKindName, trapText, trapDescription,
                    trapMessage, saveMessage, failMessage, extraMessage,
                    index, adc, rarity, minDepth, maxNum, power, flags,
                    saveFlags, effects, xtraEffects));
        }
        
        return traps;
    }
}
