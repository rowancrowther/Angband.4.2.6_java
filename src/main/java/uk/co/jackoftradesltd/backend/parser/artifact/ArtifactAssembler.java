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

package uk.co.jackoftradesltd.backend.parser.artifact;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;
import uk.co.jackoftradesltd.backend.parser.gameconstants.GameConstantsAssembler;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

/**
 * Assembles the raw {@link ArtifactParseRecord}s parsed from {@code artifact.txt} into the
 * finished {@link Artifact} domain objects, resolving names, base kinds, curses and element
 * info as it goes (reader &rarr; ParseRecord &rarr; assembler &rarr; domain). It does the work of
 * the {@code parse_artifact_*} handlers in C's {@code obj-init.c}.
 *
 * <p>Integers from the record are read through {@link ParseUtils#parseIntString}, which
 * reads them as C's {@code strtol} with base 0 does, so a leading zero is octal. The figures in
 * {@code values:} are decimal, as {@code find_value_arg} in {@code datafile.c} reads them.
 *
 * <p>Brands and slays are stored in C's order, not file order. C keeps them as flag arrays
 * indexed by registry position, so the assembler collects the named ones and then walks the
 * registry to fill a {@link LinkedHashSet}.
 *
 * <p>Class ArtifactAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class ArtifactAssembler implements Assembler<ArtifactParseRecord, List<Artifact>> {
    /**
     * The logger this assembler writes its messages to: a fatal one just before it throws
     * {@link DataFileParseException}, carrying the same text, and a warning for an unknown
     * {@code act:} name, which does not end the load.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(ArtifactAssembler.class);
    
    /**
     * Resolves every raw {@link ArtifactParseRecord} field into its domain form and builds
     * the {@link Artifact}: the base object tvalue, numeric to-hit/to-damage/AC/weight/cost/
     * level/alloc-range fields, object flags, the {@code values:} map (fanned out into
     * {@link ObjectModifier}s and element resist
     * levels the same way {@link GameConstantsAssembler}
     * fans out its {@code RES_}-prefixed keys), brands, slays, curses (by registry lookup),
     * and an optional activation/timeout pair.
     *
     * <p>Also resolves the artifact's backing {@link ObjectKind}. Each {@code base-object:} line
     * is handled as C's {@code parse_artifact_base_object} handles it: the kind for that type and
     * sub-type is reused when one exists, and otherwise a new special kind is made and registered,
     * as {@code write_dummy_object_record} does. A kind at or above the ordinary kind limit is
     * special, as C tests {@code kidx >= z_info->ordinary_kind_max}.
     *
     * <p>A new kind takes the level that was in force when its {@code base-object:} line was read,
     * or 0 if no {@code level:} line came before it, because {@code write_dummy_object_record}
     * copies {@code art->level} at that moment. The grammar takes that snapshot into
     * {@link ArtifactParseRecord.ArtifactBaseObjectParseRecord#level()}, one per
     * {@code base-object:} line. Every shipped record puts {@code level:} after
     * {@code base-object:}, so every special kind is level 0, and a second run at birth finds the
     * kind and leaves its level alone.
     *
     * <p>A record with two {@code base-object:} lines makes or finds a kind for each, as C does.
     * The artifact's own base type, and every later weight, cost, activation, time and glyph
     * write, go to the last one. That is an accepted deviation: C sends each of those to
     * whichever kind was current at the line.
     *
     * <p>A special kind takes the artifact's weight and cost only when the record has a
     * {@code weight:} or {@code cost:} line. A special light also takes the activation and time
     * that the record carries, and the artifact keeps none. Its activations are cleared and refilled
     * only when the record has an {@code act:} line, and its time is set only when the record has a
     * {@code time:} line. An unknown {@code act:} name is a warning, not an error, and leaves no
     * activation, as {@code findact} returns {@code NULL} in C.
     *
     * <p>A record with no {@code desc:} line holds {@code null} text, as C's {@code text} starts
     * {@code NULL}, and the grammar delivers that {@code null}, which the assembler passes
     * straight through. A record with no {@code msg:} line holds a {@code null} message for the
     * same reason, but the grammar delivers an empty string there, and the assembler turns it into
     * {@code null}. A {@code curse:} line with power 0 adds nothing, and leaves any earlier line
     * for the same curse in place, as C's {@code parse_artifact_curse} does.
     *
     * <p>A record with no {@code base-object:} line is refused, where C zero-fills the artifact
     * and loads it as tval 0. This is an accepted deviation, recorded in
     * {@code docs/Accepted_Deviations.md} under "Artifact". Every record in the shipped
     * {@code artifact.txt} has the line, so only a modded file reaches the error.
     *
     * <p>A numeric {@code base-object:} sval that names no kind is also an accepted deviation,
     * recorded under "Artifact" in the same file. C's {@code lookup_sval} returns an all-digit name
     * as the sval and loads the artifact with no kind. Here the kind lookup would miss, a kind would
     * be made under the next free sval, and the lookup after the loop would miss again, so the
     * special-kind test would throw a {@link NullPointerException} rather than a
     * {@link DataFileParseException}. The grammar refuses a numeric sval first, so only a record
     * built by hand reaches it.
     *
     * <p>As C's {@code artifact_parser} does, the load ends at the first bad field. That covers an
     * unknown tvalue or base object, a malformed integer, an unknown flag, modifier, element,
     * brand, slay or curse, a {@code values:} figure of {@code INT_MAX} or {@code INT_MIN} or
     * beyond, an {@code alloc:} figure outside 0 to 255, a malformed {@code time:} dice string, and
     * a {@code graphics:} line on a kind without {@code KF_INSTA_ART}. Each one is logged and
     * thrown as a {@link DataFileParseException}.
     *
     * <p>Function assemble coded before 260915, commented in full on 260915, rewritten for the
     * load-ending errors on 261023, special-kind level note added on 261023, absent-text,
     * zero-power curse and no-base-object notes added on 261106, rewritten for the per-line level
     * snapshot, the msg/desc null note and the numeric-sval deviation on 261107.
     *
     * @param records  the raw artifact records from the grammar, in file order
     * @param filename the name of the data file, used in the error messages
     * @return the assembled artifacts, in file order
     * @throws DataFileParseException on the first record with a field C would refuse
     */
    @Override
    public List<Artifact> assemble(@NotNull List<ArtifactParseRecord> records,
                                   String filename) {
        List<Artifact> artifacts = new ArrayList<>();

        for (ArtifactParseRecord record : records) {
            // Line number of the record
            int line = record.line();
            
            // Name of the artifact
            String name = record.name();
            
            // description of the artifact
            String text = record.desc();
            
            // Integer to Hit            
            int toh = ParseUtils.parseIntString("Artifact", record.toh(), line, filename, "To hit");
            
            // Integer to Damage
            int tod = ParseUtils.parseIntString("Artifact", record.tod(), line, filename, "To damage");
            
            // Integer base armour class
            int baseAC = ParseUtils.parseIntString("Artifact", record.baseAC(), line, filename, "Base AC");
            
            // Integer to armour class
            int toa = ParseUtils.parseIntString("Artifact", record.toa(), line, filename, "To AC");
            
            // String Base damage
            String baseDamageString = record.baseDamage();
            
            // Integer weight
            int weight = ParseUtils.parseIntString("Artifact", record.weight(), line, filename, "Weight");
            
            // Integer cost
            int cost = ParseUtils.parseIntString("Artifact", record.cost(), line, filename, "Cost");
            
            // Object/element flags
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            for (String flag : record.flagList()) {
                try {
                    ObjectFlag oFlag = ObjectFlag.valueOf("OF_" + flag);
                    if (oFlag != ObjectFlag.OF_NONE && oFlag != ObjectFlag.OF_MAX)
                        flags.on(oFlag);
                    else {
                        String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                                "an unknown flag: " + flag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    boolean eFlagFound = ParseUtils.grabElementFlag(elInfo, flag);
                    if (!eFlagFound) {
                        String message = "Artifact at line: " + line + " of file: " + filename + " has " +
                                "an unknown ignore/hates flag: " + flag;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            
            // Object modifier/value flags
            Map<ObjectModifier, Integer> modifiers = new HashMap<>();
            for (String key : record.values().keySet()) {
                try {
                    int value = Integer.parseInt(record.values().get(key));
                    if (value == Integer.MIN_VALUE || value == Integer.MAX_VALUE) {
                        String message = "Artifact at line: " + line + " of file: " + filename + " has " +
                                "a value out of bounds: " + value;
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    if (key.startsWith("RES_")) {
                        key = key.substring(4);
                        if (key.equals("NONE") || key.equals("MAX")) {
                            String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                                "an unknown value: " + "RES_" + key;
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                        }
                        ElementEnum element = ElementEnum.valueOf("ELEM_" + key);
                        ElementInfo eInfo = elInfo.computeIfAbsent(element, k -> new ElementInfo());
                        eInfo.setResLevel(value);
                    } else {
                        if (key.equals("NONE") || key.equals("MAX")) {
                            String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                                    "an unknown value: " + key;
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                        }
                        ObjectModifier mod = ObjectModifier.valueOf("OM_" + key);
                        modifiers.put(mod, value);
                    }
                } catch (NumberFormatException e) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "a malformed value integer: " + record.values().get(key);
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                } catch (IllegalArgumentException e) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown modifier/element name: " + key;
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }
            }
            
            // Ignored elements
            for (ElementEnum e : Arrays.stream(ElementEnum.values()).filter(ElementEnum::isBase)
                    .toList()) {
                ElementInfo ei = elInfo.computeIfAbsent(e, k -> new ElementInfo());
                ei.on(ElementInfoEnum.EL_INFO_IGNORE);
            }
            
            // Brands
            Set<Brand> fileBrands = new HashSet<>();
            Set<Brand> brands = new LinkedHashSet<>();
            for (String b : record.brand()) {
                Brand brand = ObjectRegistry.lookupBrandCode(b);
                if (brand != null) {
                    fileBrands.add(brand);
                } else {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown brand: " + b;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            for (Brand brand : ObjectRegistry.getBrands()) {
                if (fileBrands.contains(brand)) 
                    brands.add(brand);
            }
            
            // Slays
            Set<Slay> fileSlays = new HashSet<>();
            Set<Slay> slays = new LinkedHashSet<>();
            for (String s : record.slay()) {
                Slay slay = ObjectRegistry.lookupSlay(s);
                if (slay != null) {
                    fileSlays.add(slay);
                } else {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown slay: " + s;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            for (Slay slay : ObjectRegistry.getSlays()) {
                if (fileSlays.contains(slay))
                    slays.add(slay);
            }
            
            // Curses
            TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
            for (FullCurseDataParseRecord curseDataRecord : record.curse()) {
                String key = curseDataRecord.name();
                Curse curse = ObjectRegistry.lookupCurse(key);
                if (curse == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown curse: " + key;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                } else {
                    int power;
                    CurseData curseData;
                    String value = curseDataRecord.power();
                    power = ParseUtils.parseIntString("Artifact", value, line, filename, "power");
                    if (power > 0) {
                        curseData = new CurseData(power, 0);
                        curses.put(curse, curseData);
                    }
                }
            }
            
            // Level
            int level = ParseUtils.parseIntString("Artifact", record.level(), line, filename, "Level");
            
            // Commonness
            int commonness = ParseUtils.parseIntString("Artifact", record.commonness(), line, filename, "Commonness");
            
            // Min level
            int min = ParseUtils.parseIntString("Artifact", record.min(), line, filename, "Min", false);
            if (0 > min || min > 255) {
                String message = "Artifact beginning at line: " + line + " of file: " + filename
                        + " has an unknown error with token min value: " + record.min();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            
            // Max level
            int max = ParseUtils.parseIntString("Artifact", record.max(), line, filename, "Max", false);
            if (0 > max || max > 255) {
                String message = "Artifact beginning at line: " + line + " of file: " + filename
                        + " has an unknown error with token max value: " + record.max();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            
            // Activation
            Activation activation = null;
            if (!record.activation().isEmpty()) {
                activation = ObjectRegistry.lookupActivation(record.activation());
                if (activation == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown activation: " + record.activation();
                    logger.warn(message);
                }
            }
            
            // Activation message
            String activationMsg = record.msg().isEmpty() ? null : record.msg();
            
            // Time
            Random time = Random.Zero();
            if (!record.time().isEmpty()) {
                time = Random.parseStr(record.time());
                if (time == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "a malformed time dice string: " + record.time();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            
            // Guard against a empty objectBases
            if (record.baseObjects().isEmpty()) {
                String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                        "no assigned bases";
                logger.fatal(message);
                throw new DataFileParseException(message);                
            }
            
            // Get the last object base details
            ArtifactParseRecord.ArtifactBaseObjectParseRecord baseParseRecord = record.baseObjects().getLast();

            // TValue, SValue & appropriate level for records
            TValue tVal;
            ObjectKind kind;
            String sValStr;
            int levelInt;
            List<ArtifactParseRecord.ArtifactBaseObjectParseRecord> objectBases = record.baseObjects();
            for (ArtifactParseRecord.ArtifactBaseObjectParseRecord objectBaseParseRecord : objectBases) {
                String tValStr = objectBaseParseRecord.tValue();
                sValStr = objectBaseParseRecord.sValue();
                String levelStr = objectBaseParseRecord.level();
                levelInt = ParseUtils.parseIntString("Artifact", levelStr, line, filename, "level");

                tVal = TValue.fromName(tValStr);
                if (tVal == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown base-obj tvalue: " + tValStr;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }

                ObjectBase base = ObjectRegistry.getBaseFromTVal(tVal);
                if (base == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown base from tvalue: " + tValStr;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                kind = ObjectRegistry.lookupObjectKind(tVal, sValStr);
                if (kind == null) {
                    kind = new ObjectKind();
                    kind.setBase(base);
                    kind.setTval(tVal);
                    kind.setsValueName(sValStr);
                    Map<ElementEnum, ElementInfo> elementInfoMap = new HashMap<>();
                    kind.setLevel(levelInt);
                    for (ElementEnum elementEnum : base.getElementMap().keySet()) {
                        elementInfoMap.put(elementEnum, base.getElementMap().get(elementEnum).copy());
                    }
                    kind.setElInfo(elementInfoMap);
                    Flag<ObjectFlag> oFlags = base.getFlags();
                    for (ObjectFlag oFlag : oFlags) {
                        kind.setFlag(oFlag);
                    }
                    Flag<ObjectKindFlag> kFlags = base.getKindFlags();
                    for (ObjectKindFlag kFlag : kFlags) {
                        kind.setKindFlag(kFlag);
                    }
                    kind.setCharacter(new AngbandDisplayCharacter('*', ColourEnum.COLOUR_RED));
                    kind.setKindFlag(ObjectKindFlag.KF_INSTA_ART);
                    ObjectRegistry.addObjectKind(kind);
                    kind.setSpecialArtifactKind(kind.getKindIndex() >= ObjectRegistry.getObjectBaseKindMax());
                }
            }

            // Object bases noted in this artifact
            TValue lastTValue = TValue.fromName(baseParseRecord.tValue());

            // Special light
            if (lastTValue == null) {
                lastTValue = TValue.TV_NONE;
            }
            kind = ObjectRegistry.lookupObjectKind(lastTValue, baseParseRecord.sValue());
            boolean special = kind.getKindIndex() >= ObjectRegistry.getObjectBaseKindMax();
            boolean specialLight = special && lastTValue == TValue.TV_LIGHT;

            // Artifact activation & time initial values
            Activation artActivation = activation;
            Random artTime = time;
            Activation kindActivation = null;
            Random kindTime = null;

            // Artifact activation & time special light values
            if (specialLight) {
                if (!record.time().isEmpty())
                    kindTime = artTime;
                artTime = Random.Zero();
                kindActivation = artActivation;
                artActivation = null;
            }

            // Artifact constructor
            Artifact artifact = new Artifact(name, text, lastTValue, baseParseRecord.sValue(), toh, tod, toa,
                    baseAC, baseDamageString, weight, cost, flags, modifiers, elInfo,
                    brands, slays, curses, level, commonness, min, max, artActivation,
                    activationMsg, artTime);
            
            // Weight for special kinds
            if (special && !record.weight().isEmpty())
                kind.setWeight(weight);
            
            // Cost for special kinds
            if (special && !record.cost().isEmpty())
                kind.setCost(cost);

            // Special kind activations list
            if (specialLight && !record.activation().isEmpty()) {
                List<Activation> kindActivations = kind.getActivations();
                kindActivations.clear();
                if (kindActivation != null)
                    kindActivations.add(kindActivation);
            }

            // Kind time, if not already set
            if (kindTime != null) {
                kind.setTime(kindTime);
            }            

            // Agnband Display Character
            if (!record.glyph().isEmpty() && !record.colour().isEmpty()) {
                ParseUtils.GlyphCharacter glyphCharacter = ParseUtils.parseADCGlyph(record.glyph());

                if (glyphCharacter == null) {
                    String message = "Artifact beginning at line: " + line + " of file: " + filename
                            + " has an unknown error with token glyph value: " + record.glyph();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }

                if (kind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)) {
                    ColourEnum colour = ColourEnum.fromCode(record.colour());
                    if (colour == null) colour = ColourEnum.COLOUR_WHITE;
                    if (glyphCharacter.wideGlyphChar() == 0)
                        kind.setCharacter(new AngbandDisplayCharacter(glyphCharacter.glyphChar(), colour));
                    else
                        kind.setCharacter(new AngbandDisplayCharacter(glyphCharacter.wideGlyphChar(), colour));
                } else {
                    String message = "Artifact beginning at line: " + line + " of file: " + filename
                            + " has an unknown error with token glyph value: " + record.glyph();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }

            // Add to the results list
            artifacts.add(artifact);
        }

        // Return the results list
        return artifacts;
    }
}
