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

package uk.co.jackoftrades.backend.parser.playertimed;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerTimedEffect;
import uk.co.jackoftrades.middle.player.TimedFailure;
import uk.co.jackoftrades.middle.player.TimedGrade;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.middle.player.enums.TimedEffectReasonType;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves the raw {@link PlayerTimedParseRecord}s captured from {@code player_timed.txt} into
 * domain {@link PlayerTimedEffect}s — the assembler half of the reader pipeline, porting the
 * interpretation the C {@code parse_player_timed_*} handlers do in {@code player-timed.c}. Each
 * still-textual field is resolved to its domain type: the name and {@code fail:}/{@code resist:}
 * tokens to their enums, {@code grade:} colours/maxima to {@link TimedGrade}s, brand/slay codes to
 * registry entries, and the begin/end effect blocks (via {@link EffectAssembler}) to {@link Effect}s.
 *
 * <p><b>Skip-and-continue:</b> a record that fails any resolution is dropped and the failure is
 * appended to {@code errors}, so one bad record never aborts the load and every problem in the file
 * is surfaced in a single pass. Callers decide how to treat a non-empty {@code errors} list.
 *
 * <p><b>Load-order dependency:</b> the {@code ATT_*} statuses resolve a brand/slay code through
 * {@link GameConstants#lookupBrandCode}/{@link GameConstants#lookupSlay}, so {@code brand.txt} and
 * {@code slay.txt} must already be loaded when this runs.
 *
 * @author Rowan Crowther
 */
public class PlayerTimedAssembler implements Assembler<PlayerTimedParseRecord, List<PlayerTimedEffect>> {
    /**
     * Resolve every parsed record into a {@link PlayerTimedEffect}, dropping (and logging) any that
     * fail to resolve.
     *
     * @param records the parsed timed-effect records, in file order
     * @param errors  the soft-error channel; one message is appended per dropped record
     * @return the successfully resolved effects, in order (records that failed are omitted)
     * @author Rowan Crowther
     */
    @Override
    public List<PlayerTimedEffect> assemble(@NotNull List<PlayerTimedParseRecord> records, @NotNull List<String> errors) {
        List<PlayerTimedEffect> effects = new ArrayList<>();

        for (PlayerTimedParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            TimedEffect timedEffect = TimedEffect.TMD_NONE;
            if (!name.isEmpty()) {
                try {
                    timedEffect = TimedEffect.valueOf("TMD_" + name.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Player timed effect at line: " + line + " has " +
                            "an invalid timed effect name: " + name);
                    continue;
                }
            }
            String description = record.description();
            List<TimedGrade> grades = new ArrayList<>();
            boolean illegalGrade = false;
            int count = -1;
            for (PlayerTimedParseRecord.PlayerTimedGradeParseRecord g : record.grades()) {
                count++;
                String colour = g.colour();
                int max = 0;
                String status = g.status().length() > 1 ? g.status() : null;
                String msgDown = g.msgDown().length() > 1 ? g.msgDown() : null;
                String msgUp = g.msgUp().length() > 1 ? g.msgUp() : null;
                try {
                    ColourType colourType = ColourType.getColourType(colour);
                    max = Integer.parseInt(g.max());
                    grades.add(new TimedGrade(count, colourType, max, status, msgUp, msgDown));
                } catch (NumberFormatException e) {
                    errors.add("Player Timed record at line: " + line + " has " +
                            "a malformed max integer: " + g.max());
                    illegalGrade = true;
                }
            }
            if (illegalGrade) continue;
            String onEnd = record.onEnd();
            String onIncrease = record.onIncrease();
            String onDecrease = record.onDecrease();
            String msg = record.msgT();
            MessageType messageType = MessageType.MSG_NONE;
            if (!msg.isEmpty()) {
                try {
                    messageType = MessageType.valueOf("MSG_" + msg);
                } catch (IllegalArgumentException e) {
                    errors.add("Player Timed record at line: " + line + " has " +
                            "an unknown message type: " + msg);
                    continue;
                }
            }
            boolean illegalFail = false;
            List<TimedFailure> fails = new ArrayList<>();
            for (PlayerTimedParseRecord.FailureParseRecord fail : record.fail()) {
                String failType = fail.type();
                String failValue = fail.value();
                try {
                    switch (failType) {
                        case "1":
                            // object flag
                            ObjectFlag objectFlag = ObjectFlag.valueOf("OF_" + failValue);
                            fails.add(new TimedFailure(objectFlag, TimedEffectReasonType.TYPE_OBJECT_FLAG));
                            break;
                        case "2":
                            // resist element
                            ElementEnum elementEnum_2 = ElementEnum.valueOf("ELEM_" + failValue);
                            fails.add(new TimedFailure(elementEnum_2, TimedEffectReasonType.TYPE_RESIST));
                            break;
                        case "3":
                            // vulnerable element
                            ElementEnum elementEnum_3 = ElementEnum.valueOf("ELEM_" + failValue);
                            fails.add(new TimedFailure(elementEnum_3, TimedEffectReasonType.TYPE_VULN));
                            break;
                        case "4":
                            // Player flag
                            PlayerFlag playerFlag = PlayerFlag.valueOf("PF_" + failValue);
                            fails.add(new TimedFailure(playerFlag, TimedEffectReasonType.TYPE_PLAYER_FLAG));
                            break;
                        case "5":
                            // Timed flag
                            TimedEffect effect = TimedEffect.valueOf("TMD_" + failValue);
                            fails.add(new TimedFailure(effect, TimedEffectReasonType.TYPE_TIMED_EFFECT));
                            break;
                        default:
                            errors.add("Player Timed Record at line: " + line + " has " +
                                    "an unknown fail type: " + failType);
                            illegalFail = true;
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    errors.add("Player Timed Record at line: " + line + " has " +
                            "an invalid fail line: fail:" + failType + ":" + failValue);
                    illegalFail = true;
                }
            }
            if (illegalFail) continue;
            List<Effect> begin;
            Effect onBeginEffect = null;
            if (record.onBeginEffect() != null && !record.onBeginEffect().typeInit().isEmpty()) {
                begin = EffectAssembler.assemble(List.of(record.onBeginEffect()), errors);
                if (begin == null) continue;      // bad effect/subtype — error already logged
                onBeginEffect = begin.getFirst();
            }
            List<Effect> end;
            Effect onEndEffect = null;
            if (record.onEndEffect() != null && !record.onEndEffect().typeInit().isEmpty()) {
                end = EffectAssembler.assemble(List.of(record.onEndEffect()), errors);
                if (end == null) continue;
                onEndEffect = end.getFirst();
            }
            ElementEnum resist = ElementEnum.ELEM_NONE;
            if (!record.resist().isEmpty()) {
                try {
                    resist = ElementEnum.valueOf("ELEM_" + record.resist());
                } catch (IllegalArgumentException e) {
                    errors.add("Player Timed Record at line: " + line + " has " +
                            "an unknown resist element: " + record.resist());
                    continue;
                }
            }
            String brand = record.brand();
            Brand b = null;
            if (!brand.isEmpty()) {
                b = GameConstants.lookupBrandCode(brand);
                if (b == null) {
                    errors.add("Player Timed Record at line: " + line + " has " +
                            "an unknown brand: " + brand);
                    continue;
                }
            }
            Slay s = null;
            String slay = record.slay();
            if (!slay.isEmpty()) {
                s = GameConstants.lookupSlay(slay);
                if (s == null) {
                    errors.add("Player Timed Record at line: " + line + " has " +
                            "an unknown slay: " + slay);
                    continue;
                }
            }
            boolean exactlySyn = !record.flagSynonymValue().equals("0");
            ObjectFlag synFlag = ObjectFlag.OF_NONE;
            if (!record.flagSynonymFlag().isEmpty()) {
                try {
                    synFlag = ObjectFlag.valueOf("OF_" + record.flagSynonymFlag());
                } catch (IllegalArgumentException e) {
                    errors.add("Player Timed Record at line: " + line + " has " +
                            "an unknown flag-synonym: " + record.flagSynonymFlag());
                    continue;
                }
            }
            int lowerBound = 0;
            if (!record.lowerBound().isEmpty()) {
                try {
                    lowerBound = Integer.parseInt(record.lowerBound());
                } catch (NumberFormatException e) {
                    errors.add("Player Timed Record at line: " + line + " has " +
                            "a malformed lower bound integer: " + record.lowerBound());
                    continue;
                }
            }
            boolean nonStacking = false;
            // Only currently understands NONSTACKING flag
            for (String flag : record.flags()) {
                if (flag.equals("NONSTACKING")) {
                    nonStacking = true;
                    break;
                }
            }

            effects.add(new PlayerTimedEffect(timedEffect, description, onEnd,
                    onIncrease, onDecrease, messageType, fails, grades,
                    onBeginEffect, onEndEffect, nonStacking, lowerBound,
                    synFlag, exactlySyn, resist, b, s));
        }

        return effects;
    }
}