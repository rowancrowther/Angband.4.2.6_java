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

package uk.co.jackoftrades.backend.parser.terrainfeature;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.Feature;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link TerrainFeatureParseRecord}s produced by {@code TerrainFeatureGrammar} into
 * game-ready {@link Feature} objects - the interpretation half of the terrain pipeline, kept out of
 * the reader so the reader is pure lex/parse/collect. This mirrors what the C {@code parse_feat_*}
 * handlers in {@code src/init.c} do inline, but as a discrete pass over already-parsed records.
 *
 * <p>Its job is resolution: reattach the enum prefixes the grammar stripped ({@code FEAT_} for the
 * code and mimic, {@code TF_} for terrain flags, {@code RF_} for resist flags) and look the values
 * up; parse {@code priority}/{@code digging} as integers; and build the {@link AngbandDisplayCharacter}
 * from the raw glyph/colour.
 *
 * <p><strong>Error model:</strong> this is the soft channel. Any record that fails to resolve - an
 * unknown code, mimic, flag, resist flag, or an overflowing number - is <em>skipped</em>
 * ({@code continue}) with a message appended to {@code errors}; the remaining good records still
 * load. Hard grammar/lexer errors never reach here (they abort in the reader), so every message this
 * class adds is recoverable, per the suite-wide partial-results contract.
 *
 * <p><strong>Absence convention:</strong> optional directives arrive as the empty string, so each
 * optional resolution is guarded by an {@code isEmpty()} check and left at its default rather than
 * attempting to resolve {@code ""}.
 *
 * @author Rowan Crowther
 */
public class TerrainFeatureAssembler implements Assembler<TerrainFeatureParseRecord, List<Feature>> {

    /**
     * Resolve each parse record into a {@link Feature}, skipping (and reporting) any that carry an
     * unresolvable value. A record is emitted only once every mandatory and present-optional field
     * has resolved cleanly; the first failure in a record short-circuits it via {@code continue} so
     * a half-populated {@link Feature} is never produced.
     *
     * @param records the raw terrain parse records, in file order
     * @param errors  the soft-error channel; one message is appended per skipped record
     * @return the features that resolved cleanly (may be shorter than {@code records})
     * @author Rowan Crowther
     */
    @Override
    public List<Feature> assemble(@NotNull List<TerrainFeatureParseRecord> records, @NotNull List<String> errors) {
        List<Feature> result = new ArrayList<>();

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
            TerrainFlags flagMimic = TerrainFlags.FEAT_NONE;
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
            String lookInP = record.lookInPreposition();
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

            result.add(new Feature(flagCode, name, description, flagMimic, priority,
                    digging, flags, adc, walkMsg, runMsg, hurtMsg, dieMsg, confMsg,
                    lookPre, lookInP, resistFlags));
        }

        return result;
    }
}
