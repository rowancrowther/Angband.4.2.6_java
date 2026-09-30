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

package uk.co.jackoftradesltd.backend.parser.terrainfeature;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link TerrainFeatureParseRecord}s produced by {@code TerrainFeatureGrammar} into
 * game-ready {@link Feature} objects - the interpretation half of the terrain pipeline, kept out of
 * the reader so the reader is pure lex/parse/collect. In the C original this work is split between
 * the {@code parse_feat_*} handlers, which fill one {@code struct feature} per record as each line
 * is read, and {@code finish_parse_feat()}, which runs once afterwards; both live in {@code init.c}.
 * Here it is one discrete pass over the already-parsed records, and it does the work of both.
 *
 * <p><strong>Resolution</strong> (the {@code parse_feat_*} half): reattach the enum prefixes the
 * grammar stripped ({@code FEAT_} for the code and mimic, {@code TF_} for terrain flags,
 * {@code RF_} for resist flags) and look the values up; parse {@code priority} and {@code digging}
 * as integers; and build the {@link AngbandDisplayCharacter} from the raw glyph and colour, which is
 * left {@code null} unless both were given. The mimic is left {@code null} when the record has no
 * {@code mimic:} line, never {@code FEAT_NONE}: {@link Feature#isMimicing()} tests for {@code null},
 * so a {@code FEAT_NONE} default would make every feature look like a mimic.
 *
 * <p><strong>Finishing</strong> (the {@code finish_parse_feat()} half):
 * <ul>
 *   <li>Shop numbers are handed out from 1, in record order, to each feature carrying the
 *       {@code SHOP} flag; every other feature gets 0. C walks the {@code f_info} table in feature
 *       order rather than file order, which is the same thing for the stock {@code terrain.txt}
 *       because the file follows the order of {@code list-terrain.h}.</li>
 *   <li>{@code look-prefix} and {@code look-in-preposition} get a trailing space when they are
 *       present and lack one; an absent value stays empty, so the callers' "a"/"an" and "on"
 *       fallbacks still apply.</li>
 *   <li>{@link GameConstants#setStoreMax(int)} is set to the number of shops handed out, as C sets
 *       {@code z_info->store_max}. This is a write to global state, unlike the rest of the class,
 *       which only builds and returns a list.</li>
 * </ul>
 * C's {@code fidx}, the feature's index in {@code f_info}, is not set here; {@code code.ordinal()}
 * gives the same value and {@code Feature.featureIndex} is deprecated.
 *
 * <p><strong>Error model:</strong> this is the soft channel. Any record that fails to resolve - an
 * unknown code, mimic, flag, resist flag, or an overflowing number - is <em>skipped</em>
 * ({@code continue}) with a message appended to {@code errors}; the remaining good records still
 * load. Hard grammar/lexer errors never reach here (they abort in the reader), so every message this
 * class adds is recoverable, per the suite-wide partial-results contract. A skipped record takes no
 * shop number, so a skipped shop entrance shifts the numbers of the shops after it and lowers the
 * {@code store_max} value; C has no such path because a bad record aborts the load.
 *
 * <p><strong>Absence convention:</strong> optional directives arrive as the empty string, so each
 * optional resolution is guarded by an {@code isEmpty()} check and left at its default rather than
 * attempting to resolve {@code ""}. The empty string is the Java form of C's NULL pointer for the
 * message and look strings.
 *
 * <p>Class TerrainFeatureAssembler coded before 260915, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class TerrainFeatureAssembler implements Assembler<TerrainFeatureParseRecord, List<Feature>> {

    /**
     * Resolve each parse record into a {@link Feature}, skipping (and reporting) any that carry an
     * unresolvable value, then set the shop count. This does the work of the C {@code parse_feat_*}
     * handlers and of {@code finish_parse_feat()} in {@code init.c}.
     *
     * <p>Each record is resolved in this order: the code, the mimic (left {@code null} when the
     * record has none), {@code priority}, {@code digging}, the terrain flags, the display character,
     * the messages, the look prefix and preposition (given a trailing space only when present and
     * lacking one), and the resist flags. A failure in the code, mimic, {@code priority} or
     * {@code digging} skips the record at once with one message. A bad terrain flag or resist flag
     * is reported and the loop carries on, so every bad flag on the line is reported, and the record
     * is then skipped. Either way a half-populated {@link Feature} is never produced.
     *
     * <p>A record that resolves cleanly is given a shop number: the next number from 1 if it carries
     * the {@code SHOP} flag, otherwise 0. Skipped records take no number. After the last record
     * {@link GameConstants#setStoreMax(int)} is set to the number of shops handed out, which is what C
     * stores in {@code z_info->store_max}; this happens even when {@code records} is empty, when it
     * is set to 0. That write to global state is the one effect this method has beyond building and
     * returning the list.
     *
     * <p>Function assemble coded before 260915, commented in full on 260930.
     *
     * @param records the raw terrain parse records, in file order
     * @param errors  the soft-error channel; at least one message is appended per skipped record,
     *                and one per bad flag when several flags on a record are bad
     * @return the features that resolved cleanly, in record order (may be shorter than
     *         {@code records})
     */
    @Override
    public List<Feature> assemble(@NotNull List<TerrainFeatureParseRecord> records, @NotNull List<String> errors) {
        List<Feature> result = new ArrayList<>();
        int shop = 1;

        for (TerrainFeatureParseRecord record : records) {
            int line = record.line();

            String rawCode = record.code();
            TerrainFlags flagCode;
            try {
                flagCode = TerrainFlags.valueOf("FEAT_" + rawCode);
            } catch (IllegalArgumentException e) {
                errors.add("Block beginning at line: " + line + " has illegal terrain" +
                        " feature code: " + rawCode);
                continue;
            }
            String name = record.name();
            String description = record.desc();
            String rawMimic = record.mimic();
            TerrainFlags flagMimic = null;
            if (!rawMimic.isEmpty()) {
                try {
                    flagMimic = TerrainFlags.valueOf("FEAT_" + rawMimic);
                } catch (IllegalArgumentException e) {

                    errors.add("Block beginning at line: " + line + " has illegal mimic" +
                            " feature code: " + rawMimic);
                    continue;
                }
            }
            int priority = 0;
            if (!record.priority().isEmpty()) {
                try {
                    priority = Integer.parseInt(record.priority());
                } catch (NumberFormatException e) {
                    errors.add("Block beginning at line: " + line + " has illegal number" +
                            " format on priority: " + record.priority());
                    continue;
                }
            }
            int digging = 0;
            if (!record.dig().isEmpty()) {
                try {
                    digging = Integer.parseInt(record.dig());
                } catch (NumberFormatException e) {
                    errors.add("Block beginning at line: " + line + " has illegal number" +
                            " format on digging: " + record.dig());
                    continue;
                }
            }
            // Two-level skip: a bad flag is logged and the inner loop continues (so every bad flag
            // in the line is reported), but illegalFlag then drops the whole record after the loop -
            // a Feature is never built from a partially-resolved flag set.
            Flag<TerrainFeatureFlags> flags = new Flag<>(TerrainFeatureFlags.class);
            boolean illegalFlag = false;
            for (String rawFlag : record.flags()) {
                TerrainFeatureFlags flag;
                try {
                    flag = TerrainFeatureFlags.valueOf("TF_" + rawFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Illegal terrain found in block " +
                            "beginning on line: " + line + " flag: " + rawFlag);
                    illegalFlag = true;
                    continue;
                }
                flags.on(flag);
            }
            if (illegalFlag) continue;
            // adc stays null unless BOTH glyph and colour were supplied; a lone half has no
            // meaningful display character, so Feature receives null rather than a partial glyph.
            String glyphStr = record.glyph();
            String colourStr = record.colour();
            char glyph;
            AngbandDisplayCharacter adc = null;
            if (!glyphStr.isEmpty() && !colourStr.isEmpty()) {
                glyph = glyphStr.charAt(0);
                adc = new AngbandDisplayCharacter(glyph, colourStr);
            }
            String walkMsg = record.walkMsg();
            String runMsg = record.runMsg();
            String hurtMsg = record.hurtMsg();
            String dieMsg = record.dieMsg();
            String confMsg = record.confusedMsg();
            String lookPre = record.lookPrefix();
            if (!lookPre.isEmpty()) {
                if (!lookPre.endsWith(" "))
                    lookPre = lookPre + " ";
            }
            String lookInP = record.lookInPreposition();
            if (!lookInP.isEmpty()) {
                if (!lookInP.endsWith(" "))
                    lookInP = lookInP + " ";
            }
            // Reset and reuse the same skip flag for the resist-flag loop (identical contract).
            illegalFlag = false;
            Flag<MonsterRaceFlag> resistFlags = new Flag<>(MonsterRaceFlag.class);
            for (String rawFlag : record.resistFlags()) {
                MonsterRaceFlag resistFlag;
                try {
                    resistFlag = MonsterRaceFlag.valueOf("RF_" + rawFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Illegal resist flag found in block " +
                            "beginning on line: " + line + " flag: " + rawFlag);
                    illegalFlag = true;
                    continue;
                }
                resistFlags.on(resistFlag);
            }
            if (illegalFlag) continue;

            int shopNum = 0;
            if (flags.has(TerrainFeatureFlags.TF_SHOP)) {
                shopNum = shop;
                shop++;
            }

            result.add(new Feature(flagCode, name, description, flagMimic, priority,
                    digging, flags, adc, walkMsg, runMsg, hurtMsg, dieMsg, confMsg,
                    lookPre, lookInP, resistFlags, shopNum));
        }

        GameConstants.setStoreMax(shop - 1);

        return result;
    }
}
