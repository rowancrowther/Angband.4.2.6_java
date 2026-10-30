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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
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
 * here by name before the enum lookup. {@code NONE} differs by table. It is valid for the code and
 * the mimic, because {@code lookup_feat_code()} searches {@code feat_code_list} from index 0. It is
 * refused for the terrain flags and the resist flag, because those go through {@code lookup_flag()}
 * ({@code datafile.c}), which starts at {@code FLAG_START} (1) and so never matches the
 * {@code NONE} row at index 0. {@code MonsterRaceFlag} has no {@code RF_MAX}, so no {@code MAX}
 * check is needed for the resist flag.
 *
 * <p><strong>Repeated codes:</strong> C's {@code parse_feat_code()} points the parser at the fixed
 * {@code f_info} slot for the code, so a second record with the same {@code code:} carries on
 * filling the same {@code struct feature} rather than starting a new one. This class does the same
 * by building a replacement {@link Feature} from the earlier one and putting it in the earlier one's
 * place, so the list keeps the order in which each code first appeared. Field by field, as in C:
 * <ul>
 *   <li>{@code name:} - kept from the earlier record; a second name when the earlier record already
 *       had one is fatal, as {@code parse_feat_name()} returns
 *       {@code PARSE_ERROR_REPEATED_DIRECTIVE}; a name on a later record is taken when the earlier
 *       one had none.</li>
 *   <li>{@code desc:}, the five messages, {@code look-prefix} and {@code look-in-preposition} -
 *       appended to the earlier text with no separator, as {@code string_append()} ({@code z-virt.c})
 *       does.</li>
 *   <li>{@code flags:} - added to the earlier flag set.</li>
 *   <li>{@code graphics:} - replaced; the grammar requires it on every record.</li>
 *   <li>{@code mimic:}, {@code priority:}, {@code digging:}, {@code resist-flag:} - replaced when the
 *       later record has the line, otherwise kept from the earlier record.</li>
 * </ul>
 * The flag set is shared with the earlier {@link Feature}, not copied; that is safe because the
 * earlier one is dropped from the list.
 *
 * <p><strong>Finishing</strong> (the part of {@code finish_parse_feat()} that lives here): after
 * every record is in, a second pass gives {@code look-prefix} and {@code look-in-preposition} a
 * trailing space when they are present and lack one. It has to wait for that pass, as in C, so that
 * text from a repeated code is joined raw ({@code "a"} then {@code "b"} gives {@code "ab "}, not
 * {@code "a b "}). An absent value stays empty, the Java form of C's NULL check, so the callers'
 * "a"/"an" and "on" fallbacks still apply.
 * Shop numbering is not done here: {@code TerrainDataLoader.loadTerrainFeatures()} hands the numbers
 * out from 1, in record order, to each feature carrying the {@code SHOP} flag, and sets the store
 * count. C walks the {@code f_info} table in feature order rather than file order, which is the same
 * thing for the stock {@code terrain.txt} because the file follows the order of
 * {@code list-terrain.h}. C's {@code fidx}, the feature's index in {@code f_info}, is not set
 * anywhere; {@code code.ordinal()} gives the same value and {@code Feature.featureIndex} is
 * deprecated.
 *
 * <p><strong>Error model:</strong> every error is fatal, as it is in C, where any
 * {@code parse_feat_*} error ends the load through {@code parse_file_quit_not_found()}. Each one is
 * logged at fatal and thrown as a {@link DataFileParseException}: an unknown or sentinel code, mimic,
 * terrain flag or resist flag; a negative or unparseable {@code priority}; a {@code digging} outside
 * 1 to 5 or unparseable; an empty glyph; and a second {@code name:} for a repeated code. The first
 * bad terrain flag on a line throws, as C's {@code parse_feat_flags()} stops at the first. Nothing
 * is added to the {@code errors} list, and no record is skipped. An unknown colour is not an error:
 * like C's {@code color_text_to_attr()} and {@code color_char_to_attr()} ({@code z-color.c}), it
 * falls back to white.
 *
 * <p><strong>Absence convention:</strong> optional directives arrive as the empty string, so each
 * optional resolution is guarded by an {@code isEmpty()} check and left at its default rather than
 * attempting to resolve {@code ""}. The defaults are C's zeroed {@code f_info}: no mimic
 * ({@code null}), priority 0, digging 0, no flags and {@code RF_NONE}. The empty string is the Java
 * form of C's NULL pointer for the name, description, message and look strings.
 *
 * <p>Class TerrainFeatureAssembler coded before 260915, reworked to fatal errors and repeated codes
 * on 261030, commented in full on 261030.
 *
 * @author Rowan Crowther
 */
public class TerrainFeatureAssembler implements Assembler<TerrainFeatureParseRecord, List<Feature>> {
    /**
     * Logger for the fatal line this class writes before each {@link DataFileParseException} it
     * throws, one per error (see the class comment for the list). The line carries the same text as
     * the exception, so the cause is in the log even if a caller swallows the exception. C has no
     * counterpart: it reports through the parser's own error text.
     *
     * <p>Field logger coded before 261013, commented in full on 261030.
     */
    private static final Logger logger = LogManager.getLogger(TerrainFeatureAssembler.class);

    /**
     * Resolve each parse record into a {@link Feature}, throwing on the first value that cannot be
     * resolved. This does the work of the C {@code parse_feat_*} handlers and the look-string part
     * of {@code finish_parse_feat()}, both in {@code init.c}; the shop numbers are left to
     * {@code TerrainDataLoader}.
     *
     * <p>Each record is resolved in this order: the code, then whether an earlier record had the
     * same code (see "Repeated codes" in the class comment), the name, the description, the mimic,
     * {@code priority}, {@code digging}, the terrain flags, the colour and display character, the
     * messages, the look prefix and preposition, and the resist flag. The code and mimic are looked
     * up case-sensitively, as C's {@code streq()} does. {@code MAX} is refused for the code, the
     * mimic and each terrain flag, and {@code NONE} for each terrain flag and the resist flag (see
     * the class comment). After the loop, a second pass adds the trailing space to the look prefix
     * and preposition, as {@code finish_parse_feat()} does.
     *
     * <p>Digging must be 1 to 5 when given, which is C's {@code DIGGING_RUBBLE + 1} up to
     * {@code DIGGING_MAX}; absent digging is 0. Priority must not be negative, as
     * {@code parser_getuint()} refuses, including {@code -0}; absent priority is 0. A priority above
     * the {@code int} range is clamped to {@link Integer#MAX_VALUE} by
     * {@code ParseUtils.parseIntString}, where C's {@code strtoul} result wraps into the 32-bit
     * field; this is the accepted deviation of 261027, and no data file reaches it. A digging value
     * that large is clamped too, and then refused as out of range. An absent resist flag is
     * {@code RF_NONE}, C's zeroed default.
     *
     * <p>Function assemble coded before 260915, reworked to fatal errors and repeated codes on
     * 261030, commented in full on 261030.
     *
     * @param records  the raw terrain parse records, in file order
     * @param errors   the soft-error channel the {@link Assembler} contract supplies; not written to,
     *                 since every error here is fatal
     * @param filename the data file the records came from, quoted in every error message
     * @return one feature per distinct code, in the order each code first appeared
     * @throws DataFileParseException on any unresolvable value, as listed in the class comment
     */
    @Override
    public List<Feature> assemble(@NotNull List<TerrainFeatureParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<Feature> result = new ArrayList<>();
        List<String> allFlagsSeen = new ArrayList<>();
        for (TerrainFeatureParseRecord record : records) {
            int line = record.line();

            String rawCode = record.code();
            TerrainFlags flagCode;
            try {
                if (rawCode.equals("MAX")) {
                    String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal sentinel terrain feature value: " + rawCode;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                flagCode = TerrainFlags.valueOf("FEAT_" + rawCode);
            } catch (IllegalArgumentException e) {
                String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal terrain feature value: " + rawCode;
                logger.fatal(message, e);
                throw new DataFileParseException(message);
            }

            String name = record.name();
            
            boolean noName = name.isEmpty();
            boolean duplicateCode = result.stream().anyMatch(f -> f.getTerrainFlag().equals(flagCode));
            Feature previous = result.stream().filter(f -> f.getTerrainFlag().equals(flagCode)).findFirst().orElse(null);
            
            if (previous != null && duplicateCode) {
                if (!name.isEmpty() && previous.getName().isEmpty())
                    name = record.name();
                else
                    name = previous.getName();
            }

            if (duplicateCode && previous != null && !previous.getName().isEmpty() && !noName) {
                String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                        "a second name value: " + record.name() + " for a duplicate terrain feature value: " + rawCode;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            
            String description = "";
            if (previous != null)
                description = previous.getDescription();
            description = description + record.desc();
            TerrainFlags flagMimic = null;
            if (duplicateCode && previous != null && record.mimic().isEmpty()) {
                flagMimic = previous.getMimicTerrainFlags();
            } else {
                if (!record.mimic().isEmpty()) {
                    try {
                        if (record.mimic().equals("MAX")) {
                            String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel mimic value: " + record.mimic();
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                        }
                        flagMimic = TerrainFlags.valueOf("FEAT_" + record.mimic());
                    } catch (IllegalArgumentException e) {
                        String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal mimic value: " + record.mimic();
                        logger.fatal(message, e);
                        throw new DataFileParseException(message);
                    }
                }
            }
            int priority;
            if (record.priority().isEmpty() && previous != null) {
                priority = previous.getPriority();
            } else {
                if (ParseUtils.minusFound(record.priority())) {
                    String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative priority value: " + record.priority();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                priority = ParseUtils.parseIntString("Terrain feature", record.priority(), line, filename, "priority");
            }
            int digging = 0;
            if (record.dig().isEmpty() && previous != null) {
                digging = previous.getDig();
            } else if (!record.dig().isEmpty()) {
                digging = ParseUtils.parseIntString("Terrain feature", record.dig(), line, filename, "digging");
                if (digging < 1 || digging > 5) {
                    String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                            "an out of range digging value: " + record.dig();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            // A repeated code adds to the earlier flag set, as C ORs into the same f_info slot; the
            // first bad flag throws, as parse_feat_flags() stops at the first.
            Flag<TerrainFeatureFlags> flags;
            if (duplicateCode && previous != null) {
                flags = previous.getFlags();
            } else {
                flags = new Flag<>(TerrainFeatureFlags.class);
            }
            for (String rawFlag : record.flags()) {
                TerrainFeatureFlags flag;
                try {
                    if (rawFlag.equals("MAX") || rawFlag.equals("NONE")) {
                        String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel terrain flag value: " + rawFlag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    flag = TerrainFeatureFlags.valueOf("TF_" + rawFlag);
                } catch (IllegalArgumentException e) {
                    String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal terrain flag value: " + rawFlag;
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
                flags.on(flag);
            }
            // The grammar requires graphics:, so glyph and colour always arrive together and adc is
            // always built; the isEmpty() guard below is defensive and keeps a lone half from
            // becoming a partial display character.
            String glyphStr = record.glyph();
            String colourStr = record.colour();
            int wideGlyph = 0;
            char glyph = '\0';
            ColourEnum colourEnum = ColourEnum.fromCode(colourStr);
            if (colourEnum == null) {
                colourEnum = ColourEnum.COLOUR_WHITE;
            }
            AngbandDisplayCharacter adc;
            if (glyphStr == null || glyphStr.isEmpty()) {
                String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal empty glyph value: " + record.glyph();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            wideGlyph = Character.codePointAt(glyphStr, 0);
            if (!Character.isSupplementaryCodePoint(wideGlyph)) {
                glyph = glyphStr.charAt(0);
                adc = new AngbandDisplayCharacter(glyph, colourEnum);
            } else {
                adc = new AngbandDisplayCharacter(wideGlyph, colourEnum);
            }
            String walkMsg = "";
            if (duplicateCode && previous != null)
                walkMsg = previous.getWalkMsg();
            walkMsg = walkMsg + record.walkMsg();
            String runMsg = "";
            if (duplicateCode && previous != null) 
                runMsg = previous.getRunMsg();
            runMsg = runMsg + record.runMsg();
            String hurtMsg = "";
            if (duplicateCode && previous != null)
                hurtMsg = previous.getHurtMsg();
            hurtMsg = hurtMsg + record.hurtMsg();
            String dieMsg = "";
            if (duplicateCode && previous != null)
                dieMsg = previous.getDieMsg();
            dieMsg = dieMsg + record.dieMsg();
            String confMsg = "";
            if (duplicateCode && previous != null)
                confMsg = previous.getConfusedMsg();
            confMsg = confMsg + record.confusedMsg();
            String lookPre = "";
            if (duplicateCode && previous != null) {
                lookPre = previous.getLookPrefix();
            }
            lookPre = lookPre + record.lookPrefix();
            String lookInP = "";
            if (duplicateCode && previous != null) {
                lookInP = previous.getLookInPreposition();
            }
            lookInP = lookInP + record.lookInPreposition();
            
            
            MonsterRaceFlag resistFlag = MonsterRaceFlag.RF_NONE;
            if (duplicateCode && previous != null)
                resistFlag = previous.getResistFlag();
            if (!record.resistFlags().isEmpty()) {
                try {
                    resistFlag = MonsterRaceFlag.valueOf("RF_" + record.resistFlags());
                    if (resistFlag == MonsterRaceFlag.RF_NONE) {
                        String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel monster race flag value: " + record.resistFlags();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "Terrain feature starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal monster race flag value: " + record.resistFlags();
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            }
            
            Feature feature = new Feature(flagCode, name, description, flagMimic, priority,
                    digging, flags, adc, walkMsg, runMsg, hurtMsg, dieMsg, confMsg,
                    lookPre, lookInP, resistFlag);
            
            if (duplicateCode) {
                int index = result.indexOf(previous);
                result.remove(previous);
                result.add(index, feature);
            } else
                result.add(feature);
        }
        
        // Second pass to add the space to the end of the preposition/prefix
        for (Feature feature : result) {
            String lookPre = feature.getLookPrefix();
            if (!lookPre.isEmpty() && lookPre.charAt(lookPre.length() - 1) != ' ') 
                lookPre = lookPre + ' ';
            
            String lookInP = feature.getLookInPreposition();
            if (!lookInP.isEmpty() && lookInP.charAt(lookInP.length() - 1) != ' ')
                lookInP = lookInP + ' ';
            
            feature.setLookPrefix(lookPre);
            feature.setLookInPreposition(lookInP);
        }
        
        return result;
    }
}
