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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link TerrainFeatureParseRecord}s produced by {@code TerrainFeatureGrammar} into
 * game-ready {@link Feature} objects - the interpretation half of the terrain pipeline, kept out of
 * the reader so the reader is pure lex/parse/collect. In the C original this work is split between
 * the {@code parse_feat_*} handlers, which fill one {@code struct feature} per record as each line
 * is read, and {@code finish_parse_feat()}, which runs once afterwards; both live in {@code init.c}.
 * Here it is one discrete pass over the already-parsed records. It does all of the
 * {@code parse_feat_*} work and the look-string half of {@code finish_parse_feat()}; the shop
 * numbers and {@code z_info->store_max} are set afterwards by {@code TerrainDataLoader}.
 *
 * <p><strong>Resolution</strong> (the {@code parse_feat_*} half): reattach the enum prefixes the
 * grammar stripped ({@code FEAT_} for the code and mimic, {@code TF_} for terrain flags,
 * {@code RF_} for resist flags) and look the values up; parse {@code priority} and {@code digging}
 * as integers; and build the {@link AngbandDisplayCharacter} from the raw glyph and colour. The
 * grammar makes {@code graphics:} mandatory, so the display character is always built in practice.
 * The mimic is left {@code null} when the record has no {@code mimic:} line, never
 * {@code FEAT_NONE}: {@link Feature#isMimicing()} tests for {@code null}, so a {@code FEAT_NONE}
 * default would make every feature look like a mimic. An explicit {@code mimic:NONE} is valid, as it
 * is in C, where {@code FEAT_NONE} is index 0 of {@code list-terrain.h}.
 *
 * <p><strong>Sentinels:</strong> {@code FEAT_MAX} and {@code TF_MAX} exist as Java constants but are
 * not codes or flags. C's {@code lookup_feat_code()} ({@code cave.c}) and the {@code terrain_flags}
 * table ({@code init.c}) are built from {@code list-terrain.h} and {@code list-terrain-flags.h} and
 * have no {@code MAX} entry, so {@code code:MAX}, {@code mimic:MAX} and {@code flags:MAX} are refused
 * here by name before the enum lookup. {@code NONE} is a real entry in all three C tables, and
 * stays valid for the code, the mimic and the flags.
 *
 * <p><strong>Finishing</strong> (the part of {@code finish_parse_feat()} that lives here):
 * {@code look-prefix} and {@code look-in-preposition} get a trailing space when they are present and
 * lack one; an absent value stays empty, so the callers' "a"/"an" and "on" fallbacks still apply.
 * Shop numbering is not done here: {@code TerrainDataLoader.loadTerrainFeatures()} hands the numbers
 * out from 1, in record order, to each feature carrying the {@code SHOP} flag, and sets the store
 * count. C walks the {@code f_info} table in feature order rather than file order, which is the same
 * thing for the stock {@code terrain.txt} because the file follows the order of
 * {@code list-terrain.h}. C's {@code fidx}, the feature's index in {@code f_info}, is not set
 * anywhere; {@code code.ordinal()} gives the same value and {@code Feature.featureIndex} is
 * deprecated.
 *
 * <p><strong>Error model:</strong> this is the soft channel, with one exception. Any record that
 * fails to resolve - an unknown or sentinel code, mimic or flag, an unknown resist flag or colour, or
 * an overflowing number - is <em>skipped</em> ({@code continue}) with a message appended to
 * {@code errors}; the remaining good records still load. Hard grammar/lexer errors never reach here
 * (they abort in the reader), so these messages are recoverable, per the suite-wide partial-results
 * contract. A skipped record is simply missing from the result, so a skipped shop entrance means one
 * fewer shop for the loader to number; C has no such path because a bad record aborts the load. The
 * exception is a {@code code:} that appears twice, which is logged at fatal and thrown as a
 * {@link RuntimeException}, as C quits on it: its {@code parse_feat_name} returns
 * {@code PARSE_ERROR_REPEATED_DIRECTIVE} for a second record on the same {@code f_info} slot.
 * The colour is also checked more strictly than C: {@code color_text_to_attr()} falls back to white
 * for an unknown name, where this drops the record.
 *
 * <p><strong>Absence convention:</strong> optional directives arrive as the empty string, so each
 * optional resolution is guarded by an {@code isEmpty()} check and left at its default rather than
 * attempting to resolve {@code ""}. The empty string is the Java form of C's NULL pointer for the
 * message and look strings.
 *
 * <p>Class TerrainFeatureAssembler coded before 260915, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
public class TerrainFeatureAssembler implements Assembler<TerrainFeatureParseRecord, List<Feature>> {
    /**
     * Logger for the fatal lines this class writes: one before it throws on a repeated terrain code,
     * and one each time a {@code MAX} sentinel is refused as a code, mimic or flag. The sentinel
     * lines are logged at fatal but do not stop the load; the record is skipped and the message goes
     * to the {@code errors} list that {@code assemble} is given, like every other problem. C has no
     * counterpart: it reports through the parser's own error text.
     *
     * <p>Field logger coded before 261013, commented in full on 261013.
     */
    private static final Logger logger = LogManager.getLogger(TerrainFeatureAssembler.class);

    /**
     * Resolve each parse record into a {@link Feature}, skipping (and reporting) any that carry an
     * unresolvable value. This does the work of the C {@code parse_feat_*} handlers and the
     * look-string part of {@code finish_parse_feat()}, both in {@code init.c}; the shop numbers are
     * left to {@code TerrainDataLoader}.
     *
     * <p>Each record is resolved in this order: the code, the mimic (left {@code null} when the
     * record has none), {@code priority}, {@code digging}, the terrain flags, the colour and display
     * character, the messages, the look prefix and preposition (given a trailing space only when
     * present and lacking one), and the resist flag. A failure in the code, mimic, {@code priority},
     * {@code digging} or colour skips the record at once with one message. The code and mimic are
     * looked up case-sensitively, as C's {@code streq()} does, and {@code MAX} is refused for the
     * code, the mimic and each flag (see the class comment); {@code NONE} is accepted. A bad terrain
     * flag is reported and the loop carries on, so every bad flag on the line is reported, and the
     * record is then skipped; C stops at the first. A bad resist flag skips the record with one
     * message. Either way a half-populated {@link Feature} is never produced.
     *
     * <p>Digging must be 1 to 5 when given, which is C's {@code DIGGING_RUBBLE + 1} up to
     * {@code DIGGING_MAX}; absent digging is 0. Priority must not be negative, as
     * {@code parser_getuint()} refuses; absent priority is 0. An absent resist flag is
     * {@code RF_NONE}, C's zeroed default.
     *
     * <p>Function assemble coded before 260915, commented in full on 261013.
     *
     * @param records the raw terrain parse records, in file order
     * @param errors  the soft-error channel; at least one message is appended per skipped record,
     *                and one per bad flag when several flags on a record are bad
     * @return the features that resolved cleanly, in record order (could be shorter than
     *         {@code records})
     * @throws RuntimeException if two records carry the same {@code code:}
     */
    @Override
    public List<Feature> assemble(@NotNull List<TerrainFeatureParseRecord> records, @NotNull List<String> errors) {
        List<Feature> result = new ArrayList<>();
        List<TerrainFlags> allFlagsSeen = new ArrayList<>();
        for (TerrainFeatureParseRecord record : records) {
            int line = record.line();

            String rawCode = record.code();
            TerrainFlags flagCode;
            try {
                if (rawCode.equals("MAX")) {
                    String message = "Max terrain code is undefined";
                    logger.fatal(message);
                    throw new IllegalArgumentException(message);
                }
                flagCode = TerrainFlags.valueOf("FEAT_" + rawCode);
            } catch (IllegalArgumentException e) {
                errors.add("Block beginning at line: " + line + " has illegal terrain" +
                        " feature code: " + rawCode);
                continue;
            }

            if (allFlagsSeen.contains(flagCode)) {
                String message = "Duplicate value found in block beginning on line: " + line +
                        " code: " + flagCode;
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            
            allFlagsSeen.add(flagCode);

            String name = record.name();
            String description = record.desc();
            String rawMimic = record.mimic();
            TerrainFlags flagMimic = null;
            if (!rawMimic.isEmpty()) {
                try {
                    if (rawMimic.equals("MAX"))  {
                        String message = "Max mimic terrain code is undefined";
                        logger.fatal(message);
                        throw new IllegalArgumentException(message);
                    }
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
                    if (priority < 0) {
                        errors.add("Block beginning at line: " + line + " " +
                                "has negative priority: " + record.priority());
                        continue;
                    }
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
                if (digging < 1 || digging > 5) {
                    errors.add("Block beginning at line: " + line + " has " +
                            "an out of range digging value: " + digging);
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
                    if (rawFlag.equals("MAX")) {
                        String message = "Max terrain feature code is undefined";
                        logger.fatal(message);
                        throw new IllegalArgumentException(message);
                    }
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
            // The grammar requires graphics:, so glyph and colour always arrive together and adc is
            // always built; the isEmpty() guard below is defensive and keeps a lone half from
            // becoming a partial display character.
            String glyphStr = record.glyph();
            String colourStr = record.colour();
            ColourEnum colourEnum = ColourEnum.fromCode(colourStr);
            if (colourEnum == null) {
                errors.add("Unknown colour found in block beginning " +
                        "on line: " + line + " colour: " + colourStr);
                continue;
            }
            char glyph;
            AngbandDisplayCharacter adc = null;
            if (!glyphStr.isEmpty()) {
                glyph = glyphStr.charAt(0);
                adc = new AngbandDisplayCharacter(glyph, colourEnum);
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

            MonsterRaceFlag resistFlag = MonsterRaceFlag.RF_NONE;
            if (!record.resistFlags().isEmpty()) {
                try {
                    resistFlag = MonsterRaceFlag.valueOf("RF_" + record.resistFlags());
                } catch (IllegalArgumentException e) {
                    errors.add("Illegal resist flag found in block " +
                            "beginning on line: " + line + " flag: " + record.resistFlags());
                    continue;
                }
            }
            
            Feature feature = new Feature(flagCode, name, description, flagMimic, priority,
                    digging, flags, adc, walkMsg, runMsg, hurtMsg, dieMsg, confMsg,
                    lookPre, lookInP, resistFlag);
            result.add(feature);
        }
        return result;
    }
}
