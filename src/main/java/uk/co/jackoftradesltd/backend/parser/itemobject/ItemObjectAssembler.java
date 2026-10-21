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

package uk.co.jackoftradesltd.backend.parser.itemobject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.*;

/**
 * Turns the raw {@link ItemObjectParseRecord}s from {@code ItemObjectGrammar} into domain
 * {@link ObjectKind}s — C's {@code struct object_kind}, the item <em>template</em> loaded from
 * {@code object.txt} (Java port of {@code parse_object_*} in {@code obj-init.c}).
 *
 * <p>This is where interpretation happens:
 * <ul>
 *   <li>{@code type:} resolves to an {@link ObjectBase} by tval
 *       ({@link TValue#fromName} then {@link ObjectRegistry#getBaseFromTVal}); a record with no
 *       {@code type:} keeps C's zero tval, {@code TV_NONE}, and a {@code null} base, as C leaves
 *       {@code k->base} {@code NULL} when {@code parse_object_type} never runs;</li>
 *   <li>the rand fields (attack {@code hd}/to-h/to-d, armour to-a, {@code charges}, pile stack,
 *       {@code pval}) become {@link Random}s via {@code Random.parseStr}, while the scalar fields
 *       (level/weight/cost/power, armour base {@code ac}, alloc, pile chance) are parsed as ints;</li>
 *   <li>the {@code values:} line is fanned out into its two C destinations — the {@code obj_mods}
 *       half into a {@code Map<}{@link ObjectModifier}{@code , Random>} and the {@code RES_}-prefixed
 *       half into a {@code Map<}{@link ElementEnum}{@code ,} {@link ElementInfo}{@code >};</li>
 *   <li>flags split between {@link ObjectFlag} ({@code OF_}) and {@link ObjectKindFlag}
 *       ({@code KF_}); brands/slays/curses resolve against their registries, curses becoming a
 *       {@code Map<}{@link Curse}{@code ,} {@link CurseData}{@code >} keyed by curse, with the
 *       parsed power and a timeout of 0 at kind level; a curse declared at power 0 is dropped
 *       rather than stored, absence being how this port says "not cursed with this";</li>
 *   <li>base damage dice/sides ({@code dd}/{@code ds}) are derived from the attack {@code hd},
 *       and effects are delegated to {@link EffectAssembler};</li>
 *   <li>the record's own {@code time:} dice string becomes the kind's {@code time} {@link Random}
 *       (zero if the line is absent, a soft error dropping the kind if it does not parse), so the
 *       kind carries C's {@code kind->time} and the effects carry none.</li>
 * </ul>
 *
 * <p><b>Error policy:</b> soft errors follow the suite's skip-and-continue contract — an
 * unresolvable flag, modifier, element, brand, slay or curse logs a message to
 * {@code errors} and drops that one kind ({@code continue}), leaving the rest of the file to load.
 * An unknown {@code type:} is not one of them: C's {@code parse_object_type} returns
 * {@code PARSE_ERROR_UNRECOGNISED_TVAL} and C stops the load, so here the assembler throws
 * {@link InvalidTokenFoundDuringParse} naming the tval, the line and the file.
 * Hard grammar/lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * @author Rowan Crowther
 */
public class ItemObjectAssembler implements Assembler<ItemObjectParseRecord, List<ObjectKind>> {
    private static final Logger logger = LogManager.getLogger(ItemObjectAssembler.class);
    
    /**
     * Assembles every parsed object record into an {@link ObjectKind}, skipping (with a logged
     * soft error) any record whose flags, values, brands, slays or curses fail to resolve. An
     * unknown {@code type:} throws instead, and a missing one loads as {@code TV_NONE} with a
     * {@code null} base.
     *
     * <p>Function assemble commented in full before 260915, provenance stamp added on 260915.
     *
     * @param records  the raw object records captured by the grammar
     * @param errors   the soft-error channel; assembly failures other than an unknown type are
     *                 appended here and the offending record is dropped rather than aborting the
     *                 whole file
     * @param filename the file the records came from, named in the message of the exception thrown
     *                 for an unknown type
     * @return the successfully assembled object kinds, in source order
     * @throws InvalidTokenFoundDuringParse if a record's {@code type:} names no known tval
     */
    @Override
    public List<ObjectKind> assemble(@NotNull List<ItemObjectParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<ObjectKind> itemObjects = new ArrayList<>();

        for (ItemObjectParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            ObjectBase base = null;
            TValue tValue = null;
            if (!record.tValue().isEmpty()) {
                String tVal = record.tValue();
                try {
                    tValue = TValue.fromName(tVal);
                    if (tValue == null) {
                        String message = "TValue: " + tVal + " is not valid for object at line: " + line 
                                + " of file: " + filename;
                        logger.error(message);
                        throw new InvalidTokenFoundDuringParse(message);
                    }
                    base = ObjectRegistry.getBaseFromTVal(tValue);
                } catch (IllegalArgumentException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown type: " + record.tValue());
                    continue;
                }
            } else {
                tValue = TValue.TV_NONE;
                base = null;
            }
            String glyphChar = record.glyph();
            String glyphColour = record.colour();
            AngbandDisplayCharacter adc = null;
            if (!glyphChar.isEmpty()) {
                if (glyphChar.length() != 1) {
                    errors.add("Object kind starting at line: " + line + " glyph " +
                            "characters must contain exactly one character '" + glyphChar + "'");
                    continue;
                }
                char c = glyphChar.charAt(0);
                ColourEnum colourType = ColourEnum.fromCode(glyphColour);
                if (colourType == null) {
                    errors.add("Object kind at line: " + line + " has " +
                            "an invalid colour: " + glyphColour);
                    continue;
                }
                adc = new AngbandDisplayCharacter(c, colourType);
            }
            int level = 0;
            if (!record.level().isEmpty()) {
                try {
                    level = Integer.parseInt(record.level());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an invalid level integer: " + record.level());
                    continue;
                }
            }
            int weight = 0;
            if (!record.weight().isEmpty()) {
                try {
                    weight = Integer.parseInt(record.weight());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an invalid weight integer: " + record.weight());
                    continue;
                }
            }
            int cost = 0;
            if (!record.cost().isEmpty()) {
                try {
                    cost = Integer.parseInt(record.cost());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an invalid cost integer: " + record.cost());
                    continue;
                }
            }
            String attackBase = record.attackBaseDamage();
            String attackToh = record.attackToh();
            String attackTod = record.attackTod();
            Random attBase = Random.Zero();
            if (!attackBase.isEmpty()) {
                attBase = Random.parseStr(attackBase);
                if (attBase == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "a malformed combat attack base: " + record.attackBaseDamage());
                    continue;
                }
            }
            Random attToh = Random.Zero();
            if (!attackToh.isEmpty()) {
                attToh = Random.parseStr(attackToh);
                if (attToh == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "a malformed to-hit value: " + record.attackToh());
                    continue;
                }
            }
            Random attTod = Random.Zero();
            if (!attackTod.isEmpty()) {
                attTod = Random.parseStr(attackTod);
                if (attTod == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "a malformed to-dam value: " + record.attackTod());
                    continue;
                }
            }
            int ac = 0;
            if (!record.armourBaseAC().isEmpty()) {
                try {
                    ac = Integer.parseInt(record.armourBaseAC());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an illegal base ac: " + record.armourBaseAC());
                    continue;
                }
            }
            Random acToa = Random.Zero();
            if (!record.armourToa().isEmpty()) {
                acToa = Random.parseStr(record.armourToa());
                if (acToa == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "a malformed to-a value: " + record.armourToa());
                    continue;
                }
            }
            int allocComm = 0;
            int allocLower = 0;
            int allocUpper = 0;
            if (!record.allocCommon().isEmpty()) {
                if (record.allocLower().isEmpty() || record.allocUpper().isEmpty()) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "invalid alloc directive: alloc:" + record.allocCommon() +
                            ":" + record.allocLower() + " to " + record.allocUpper());
                    continue;
                }
                try {
                    allocComm = Integer.parseInt(record.allocCommon());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "invalid alloc common: " + record.allocCommon());
                    continue;
                }
                try {
                    allocLower = Integer.parseInt(record.allocLower());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "invalid alloc lower: " + record.allocLower());
                    continue;
                }
                try {
                    allocUpper = Integer.parseInt(record.allocUpper());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "invalid alloc upper: " + record.allocUpper());
                    continue;
                }
            }
            Random charges = Random.Zero();
            if (!record.charges().isEmpty()) {
                charges = Random.parseStr(record.charges());
                if (charges == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "invalid charges: " + record.charges());
                    continue;
                }
            }
            int pileChance = 0;
            Random pileAmount = Random.Zero();
            if (!record.pileChance().isEmpty()) {
                try {
                    pileChance = Integer.parseInt(record.pileChance());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "invalid pile chance: " + record.pileChance());
                    continue;
                }
                if (record.pileNumber().isEmpty()) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "a pile chance, but no pile amount.");
                    continue;
                }
                pileAmount = Random.parseStr(record.pileNumber());
                if (pileAmount == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an illformed pile amount: " + record.pileNumber());
                    continue;
                }
            }
            boolean illegalFlag = false;
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            Flag<ObjectKindFlag> oKindFlags = new Flag<>(ObjectKindFlag.class);
            Flag<ElementEnum> eFlags = new Flag<>(ElementEnum.class);
            for (String flag : record.flags()) {
                ObjectFlag oFlag;
                ObjectKindFlag kFlag;
                ElementEnum eFlag;
                try {
                    oFlag = ObjectFlag.valueOf("OF_" + flag);
                    oFlags.on(oFlag);
                } catch (IllegalArgumentException e) {
                    try {
                        kFlag = ObjectKindFlag.valueOf("KF_" + flag);
                        oKindFlags.on(kFlag);
                    } catch (IllegalArgumentException e1) {
                        try {
                            if (flag.startsWith("IGNORE_")) {
                                eFlag = ElementEnum.valueOf("ELEM_" + flag.substring(7));
                                eFlags.on(eFlag);
                            } else {
                                errors.add("Object kind starting at line: " + line + " has " +
                                        "an unknown flag: " + flag);
                                illegalFlag = true;
                            }
                        } catch (IllegalArgumentException e2) {
                            errors.add("Object kind starting at line: " + line + " has " +
                                    "an unknown flag: " + flag);
                            illegalFlag = true;
                        }
                    }
                }
            }
            if (illegalFlag) continue;
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            boolean illegalEFlag = false;
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                String enumFlag;
                if (key.startsWith("RES_")) {
                    enumFlag = "ELEM_" + key.substring(4);
                    ElementEnum elementEnum;
                    try {
                        elementEnum = ElementEnum.valueOf(enumFlag);
                        ElementInfo info = elInfo.computeIfAbsent(elementEnum, k -> new ElementInfo());
                        info.setResLevel(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        errors.add("Object kind starting at line: " + line + " has " +
                                "an unknown integer format for value: " + value);
                        illegalEFlag = true;
                    } catch (IllegalArgumentException e) {
                        errors.add("Object kind starting at line: " + line + " has " +
                                "an unknown value name: " + key);
                        illegalEFlag = true;
                    }
                } else {
                    ObjectModifier om;
                    try {
                        om = ObjectModifier.valueOf("OM_" + key);
                        Random rnd = Random.parseStr(value);
                        if (rnd == null) {
                            errors.add("Object kind starting at line: " + line + " has " +
                                    "an unknown value: " + value);
                            illegalEFlag = true;
                        } else
                            modifiers.put(om, rnd);
                    } catch (IllegalArgumentException e) {
                        errors.add("Object kind starting at line: " + line + " has " +
                                "an unknown value key: " + key);
                        illegalEFlag = true;
                    }
                }
            }
            if (illegalEFlag) continue;
            Set<Brand> brands = new HashSet<>();
            boolean illegalBrand = false;
            for (String brand : record.brand()) {
                Brand b = ObjectRegistry.lookupBrandCode(brand);
                if (b == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown brand: " + brand);
                    illegalBrand = true;
                } else
                    brands.add(b);
            }
            if (illegalBrand) continue;
            Set<Slay> slays = new HashSet<>();
            boolean illegalSlay = false;
            for (String slay : record.slay()) {
                Slay s = ObjectRegistry.lookupSlay(slay);
                if (s == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown slay: " + slay);
                    illegalSlay = true;
                } else
                    slays.add(s);
            }
            if (illegalSlay) continue;
            Map<Curse, CurseData> curses = new HashMap<>();
            boolean illegalCurse = false;
            for (String curseName : record.curse().keySet()) {
                Curse curse = ObjectRegistry.lookupCurse(curseName);
                if (curse == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown curse: " + curseName);
                    illegalCurse = true;
                } else {
                    try {
                        int power = Integer.parseInt(record.curse().get(curseName));
                        if (power > 0) {
                            CurseData curseData = new CurseData(power, 0);
                            curses.put(curse, curseData);
                        }
                    } catch (NumberFormatException e) {
                        errors.add("Object kind starting at line: " + line + " has " +
                                "an invalid curse power format: " + record.curse().get(curseName));
                        illegalCurse = true;
                    }
                }
            }
            if (illegalCurse) continue;
            Random pVal = Random.Zero();
            if (!record.pVal().isEmpty()) {
                pVal = Random.parseStr(record.pVal());
                if (pVal == null)
                    pVal = Random.Zero();
            }
            int power = 0;
            if (!record.power().isEmpty()) {
                try {
                    power = Integer.parseInt(record.power());
                } catch (NumberFormatException e) {
                    errors.add("Object kind starting line: " + line + " has " +
                            "an invalid power format: " + record.power());
                    continue;
                }
            }
            String description = record.desc();
            // EffectAssembler is all-or-nothing: if any single effect in the block fails
            // to resolve it returns null, so one bad effect drops the entire object kind
            // rather than loading it with an effect silently missing.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            if (effects == null) continue;
            int dd = attBase.getDice();
            int ds = attBase.getSides();

            Random time = Random.Zero();
            if (!record.time().isEmpty()) {
                time = Random.parseStr(record.time());
                if (time == null) {
                    errors.add("Object kind starting line: " + line + " has " +
                            "an invalid time string: " + record.time());
                    continue;
                }
            }

            Flag<IgnoreFlag> ignoreFlags = new Flag<>(IgnoreFlag.class);
            
            if (base != null) {
                oKindFlags.union(base.getKindFlags());
            }
            
            for (ElementEnum elem : eFlags) {
                ElementInfo info = elInfo.computeIfAbsent(elem, k -> new ElementInfo());
                info.getFlags().on(ElementInfoEnum.EL_INFO_IGNORE);
            }
            
            itemObjects.add(new ObjectKind(name, description, base, 0, pVal,
                    attToh, attTod, acToa, ac, attBase, dd, ds,
                    weight, cost, oFlags, oKindFlags, modifiers, elInfo,
                    brands, slays, curses, adc, allocComm, allocLower, allocUpper,
                    level, new ArrayList<>(), effects, record.message(),
                    record.visMessage(), time, charges, pileChance, pileAmount,
                    null, null, null, false, false,
                    ignoreFlags, false, tValue, power));
        }

        return itemObjects;
    }
}