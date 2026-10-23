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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.HelperFunctions;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
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
 * <p>Integers from the record are read through {@link HelperFunctions#parseIntString}, which
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
     * {@link uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier}s and element resist
     * levels the same way {@link uk.co.jackoftradesltd.backend.parser.gameconstants.GameConstantsAssembler}
     * fans out its {@code RES_}-prefixed keys), brands, slays, curses (by registry lookup),
     * and an optional activation/timeout pair.
     *
     * <p>Also resolves the artifact's backing {@link ObjectKind}. An artifact reuses the kind for
     * its base type when one exists, or creates and registers a new one. A kind at or above the
     * ordinary kind limit is special, as C tests {@code kidx >= z_info->ordinary_kind_max}. A new
     * kind takes the level that was in force when the {@code base-object:} line was read, or 0 if
     * no {@code level:} line came before it, because C's {@code write_dummy_object_record} copies
     * {@code art->level} at that moment. The grammar records both facts as
     * {@link ArtifactParseRecord#levelFirst()} and {@link ArtifactParseRecord#baseObjectLevel()}. Every
     * shipped record puts {@code level:} after {@code base-object:}, so every special kind is
     * level 0, and a second run at birth finds the kind and leaves its level alone. A record with
     * two {@code base-object:} lines is an accepted deviation: C makes or finds a kind for each,
     * and here only the last counts. A special kind takes the artifact's weight and cost only when the record has a
     * {@code weight:} or {@code cost:} line. A special light also takes the activation and time
     * that the record carries, and the artifact keeps none. Its activations are cleared and refilled
     * only when the record has an {@code act:} line, and its time is set only when the record has a
     * {@code time:} line. An unknown {@code act:} name is a warning, not an error, and leaves no
     * activation, as {@code findact} returns {@code NULL} in C. A record with no {@code msg:} line
     * holds a {@code null} message, as C's {@code alt_msg} starts {@code NULL}.
     *
     * <p>As C's {@code artifact_parser} does, the load ends at the first bad field. That covers an
     * unknown tvalue or base object, a malformed integer, an unknown flag, modifier, element,
     * brand, slay or curse, a {@code values:} figure of {@code INT_MAX} or {@code INT_MIN} or
     * beyond, an {@code alloc:} figure outside 0 to 255, a malformed {@code time:} dice string, and
     * a {@code graphics:} line on a kind without {@code KF_INSTA_ART}. Each one is logged and
     * thrown as a {@link DataFileParseException}.
     *
     * <p>Function assemble coded before 260915, commented in full on 260915, rewritten for the
     * load-ending errors on 261023, special-kind level note added on 261023.
     *
     * @param records  the raw artifact records from the grammar, in file order
     * @param errors   unused: every error ends the load by throwing instead
     * @param filename the name of the data file, used in the error messages
     * @return the assembled artifacts, in file order
     * @throws DataFileParseException on the first record with a field C would refuse
     */
    @Override
    public List<Artifact> assemble(@NotNull List<ArtifactParseRecord> records, @NotNull List<String> errors,
                                   String filename) {
        List<Artifact> artifacts = new ArrayList<>();

        for (ArtifactParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String text = record.desc();
            String tValStr = record.tValue();
            TValue tVal = null;
            if (!tValStr.isEmpty()) {
                tVal = TValue.fromName(tValStr);
                if (tVal == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown base-obj tvalue: " + tValStr;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            String sVal = record.sValue();
            int toh = HelperFunctions.parseIntString("Artifact", record.toh(), line, filename, "To hit");
            int tod = HelperFunctions.parseIntString("Artifact", record.tod(), line, filename, "To damage");
            int baseAC = HelperFunctions.parseIntString("Artifact", record.baseAC(), line, filename, "Base AC");
            int toa = HelperFunctions.parseIntString("Artifact", record.toa(), line, filename, "To AC");
            String baseDamageString = record.baseDamage();
            int weight = HelperFunctions.parseIntString("Artifact", record.weight(), line, filename, "Weight");
            int cost = HelperFunctions.parseIntString("Artifact", record.cost(), line, filename, "Cost");
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            Map<ElementEnum, Set<ElementInfoEnum>> elements = new HashMap<>();
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
                    try {
                        if (!flag.contains("_")) {
                            String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                                    "an unknown flag: " + flag;
                            logger.fatal(message, e);
                            throw new DataFileParseException(message);
                        }
                        String[] flagParts = flag.split("_", 2);
                        if (flagParts.length == 2) {
                            if (flagParts[1].equals("NONE") || flagParts[1].equals("MAX")) {
                                String message = "Artifact at line: " + line + " of file: " + filename + " has " +
                                        "an illegal sentinel flag: " + flag;
                                logger.fatal(message, e);
                                throw new DataFileParseException(message);
                            }
                        }
                        if ("IGNORE".startsWith(flagParts[0])) {
                            ElementEnum element = ElementEnum.valueOf("ELEM_" + flagParts[1]);
                            if (elements.containsKey(element)) {
                                elements.get(element).add(ElementInfoEnum.EL_INFO_IGNORE);
                            } else {
                                Set<ElementInfoEnum> set = new HashSet<>();
                                set.add(ElementInfoEnum.EL_INFO_IGNORE);
                                elements.put(element, set);
                            }
                        } else if ("HATES".startsWith(flagParts[0])) {
                            ElementEnum element = ElementEnum.valueOf("ELEM_" + flagParts[1]);
                            if (elements.containsKey(element)) {
                                elements.get(element).add(ElementInfoEnum.EL_INFO_HATES);
                            } else {
                                Set<ElementInfoEnum> set = new HashSet<>();
                                set.add(ElementInfoEnum.EL_INFO_HATES);
                                elements.put(element, set);
                            }
                        } else {
                            String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                                    "an unknown flag: " + flag;
                            logger.fatal(message, e);
                            throw new DataFileParseException(message);    
                        }
                    } catch (IllegalArgumentException e1) {
                        String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                                "an unknown flag: " + flag;
                        logger.fatal(message, e1);
                        throw new DataFileParseException(message);
                    }
                }
            }
            Map<ObjectModifier, Integer> modifiers = new HashMap<>();
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
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
            for (ElementEnum e : Arrays.stream(ElementEnum.values()).filter(ElementEnum::isBase)
                    .toList()) {
                ElementInfo ei = elInfo.computeIfAbsent(e, k -> new ElementInfo());
                ei.on(ElementInfoEnum.EL_INFO_IGNORE);
            }
            for (ElementEnum ee : ElementEnum.values()) {
                if (elements.containsKey(ee)) {
                    ElementInfo ei = elInfo.computeIfAbsent(ee, k -> new ElementInfo());
                    for (ElementInfoEnum eie : elements.get(ee)) {
                        ei.on(eie);
                    }
                }
            }
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
            TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
            for (String key : record.curse().keySet()) {
                Curse curse = ObjectRegistry.lookupCurse(key);
                if (curse == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown curse: " + key;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                } else {
                    int power;
                    CurseData curseData;
                    String value = record.curse().get(key);
                    power = HelperFunctions.parseIntString("Artifact", value, line, filename, "power");
                    if (power > 0) {
                        curseData = new CurseData(power, 0);
                        curses.put(curse, curseData);
                    }
                }
            }
            int level = HelperFunctions.parseIntString("Artifact", record.level(), line, filename, "Level");
            int commonness = HelperFunctions.parseIntString("Artifact", record.commonness(), line, filename, "Commonness");
            int min = HelperFunctions.parseIntString("Artifact", record.min(), line, filename, "Min", false);
            if (0 > min || min > 255) {
                String message = "Artifact beginning at line: " + line + " of file: " + filename
                        + " has an unknown error with token min value: " + record.min();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int max = HelperFunctions.parseIntString("Artifact", record.max(), line, filename, "Max", false);
            if (0 > max || max > 255) {
                String message = "Artifact beginning at line: " + line + " of file: " + filename
                        + " has an unknown error with token max value: " + record.max();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            Activation activation = null;
            if (!record.activation().isEmpty()) {
                activation = ObjectRegistry.lookupActivation(record.activation());
                if (activation == null) {
                    String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                            "an unknown activation: " + record.activation();
                    logger.warn(message);
                }
            }
            String activationMsg = record.msg().isEmpty() ? null : record.msg();
            
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

            ObjectBase objectBase;
            if (tVal != null)
                objectBase = ObjectRegistry.getBaseFromTVal(tVal);
            else
                objectBase = null;

            if (objectBase == null) {
                String message = "Artifact at line: "+ line + " of file: " + filename + " has " +
                        "an unknown object type: " + tVal;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            
            ObjectKind objectKind = ObjectRegistry.lookupObjectKind(tVal, sVal);
            
            boolean special = (objectKind == null) 
                    || (objectKind != null && objectKind.getKindIndex() >= ObjectRegistry.getObjectBaseKindMax());

            boolean specialLight = (objectKind == null && tVal == TValue.TV_LIGHT) 
                    || (special && tVal == TValue.TV_LIGHT);

            Activation artActivation = activation;
            Random artTime = time;
            Activation kindActivation = null;
            Random kindTime = null;

            if (specialLight) {
                if (!record.time().isEmpty())
                    kindTime = artTime;
                artTime = Random.Zero();
                kindActivation = artActivation;
                artActivation = null;
            }

            Artifact artifact = new Artifact(name, text, tVal, sVal, toh, tod, toa,
                    baseAC, baseDamageString, weight, cost, flags, modifiers, elInfo,
                    brands, slays, curses, level, commonness, min, max, artActivation,
                    activationMsg, artTime);

            if (objectKind == null) {
                objectKind = new ObjectKind(artifact, sVal, objectBase);
                if (record.levelFirst()) {
                    level = HelperFunctions.parseIntString("Artifact", record.baseObjectLevel(), line,
                            filename, "Level");
                    objectKind.setLevel(level);
                }
                else objectKind.setLevel(0);
                ObjectRegistry.addObjectKind(objectKind);
            }

            if (special && !record.weight().isEmpty())
                objectKind.setWeight(weight);
            if (special && !record.cost().isEmpty())
                objectKind.setCost(cost);

            if (specialLight && !record.activation().isEmpty()) {
                List<Activation> kindActivations = objectKind.getActivations();
                kindActivations.clear();
                if (kindActivation != null)
                    kindActivations.add(kindActivation);
            }

            if (kindTime != null) {
                objectKind.setTime(kindTime);
            }            

            if (!record.glyph().isEmpty()) {
                if (objectKind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART)) {
                    objectKind.setCharacter(new AngbandDisplayCharacter(record.glyph().charAt(0), record.colour()));
                } else {
                    String message = "Artifact beginning at line: " + line + " of file: " + filename
                            + " has an unknown error with token glyph value: " + record.glyph();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }

            artifacts.add(artifact);
        }

        return artifacts;
    }
}
