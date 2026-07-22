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

package uk.co.jackoftrades.backend.parser.itemobject;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.Curse.CurseEntry;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the raw {@link ItemObjectParseRecord}s from {@code ItemObjectGrammar} into domain
 * {@link ObjectKind}s — C's {@code struct object_kind}, the item <em>template</em> loaded from
 * {@code object.txt} (Java port of {@code parse_object_*} in {@code obj-init.c}).
 *
 * <p>This is where interpretation happens:
 * <ul>
 *   <li>{@code type:} resolves to an {@link ObjectBase} by tval
 *       ({@code TValue.valueOf("TV_"+…)} then {@link GameConstants#getBaseFromTVal});</li>
 *   <li>the rand fields (attack {@code hd}/to-h/to-d, armour to-a, {@code charges}, pile stack,
 *       {@code pval}) become {@link Random}s via {@code Random.parseStr}, while the scalar fields
 *       (level/weight/cost/power, armour base {@code ac}, alloc, pile chance) are parsed as ints;</li>
 *   <li>the {@code values:} line is fanned out into its two C destinations — the {@code obj_mods}
 *       half into a {@code Map<}{@link ObjectModifier}{@code , Random>} and the {@code RES_}-prefixed
 *       half into a {@code Map<}{@link ElementEnum}{@code ,} {@link ElementInfo}{@code >};</li>
 *   <li>flags split between {@link ObjectFlag} ({@code OF_}) and {@link ObjectKindFlag}
 *       ({@code KF_}); brands/slays/curses resolve against their registries, curses becoming
 *       {@link Curse.CurseEntry}s of {@link CurseData} (power, timeout 0 at kind level);</li>
 *   <li>base damage dice/sides ({@code dd}/{@code ds}) are derived from the attack {@code hd},
 *       and effects are delegated to {@link EffectAssembler}.</li>
 * </ul>
 *
 * <p><b>Error policy:</b> soft errors follow the suite's skip-and-continue contract — an
 * unresolvable type, flag, modifier, element, brand, slay or curse logs a message to
 * {@code errors} and drops that one kind ({@code continue}), leaving the rest of the file to load.
 * Hard grammar/lexer errors are handled upstream by {@code GrammarDriver}.
 *
 * @author Rowan Crowther
 */
public class ItemObjectAssembler implements Assembler<ItemObjectParseRecord, List<ObjectKind>> {
    /**
     * Assembles every parsed object record into an {@link ObjectKind}, skipping (with a logged
     * soft error) any record whose type, flags, values, brands, slays or curses fail to resolve.
     *
     * @param records the raw object records captured by the grammar
     * @param errors  the soft-error channel; assembly failures are appended here and the
     *                offending record is dropped rather than aborting the whole file
     * @return the successfully assembled object kinds, in source order
     */
    @Override
    public List<ObjectKind> assemble(@NotNull List<ItemObjectParseRecord> records, @NotNull List<String> errors) {
        List<ObjectKind> itemObjects = new ArrayList<>();

        for (ItemObjectParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            ObjectBase base = null;
            TValue tValue = null;
            if (!record.tValue().isEmpty()) {
                String tVal = "TV_" + record.tValue().toUpperCase().replace(" ", "_");
                try {
                    tValue = TValue.valueOf(tVal);
                    base = GameConstants.getBaseFromTVal(tValue);
                } catch (IllegalArgumentException e) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown type: " + record.tValue());
                    continue;
                }
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
                ColourType colourType = ColourType.getColourType(glyphColour);
                if (colourType == null) {
                    errors.add("Object kind at line: " + line + " has " +
                            "an invalid colour: " + glyphColour);
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
            Random attBase = null;
            if (!attackBase.isEmpty()) {
                attBase = Random.parseStr(attackBase);
                if (attBase == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "a malformed combat attack base: " + record.attackBaseDamage());
                    continue;
                }
            }
            Random attToh = null;
            if (!attackToh.isEmpty()) {
                attToh = Random.parseStr(attackToh);
                if (attToh == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "a malformed to-hit value: " + record.attackToh());
                    continue;
                }
            }
            Random attTod = null;
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
            Random acToa = null;
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
            Random charges = null;
            if (!record.charges().isEmpty()) {
                charges = Random.parseStr(record.charges());
                if (charges == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "invalid charges: " + record.charges());
                    continue;
                }
            }
            int pileChance = 0;
            Random pileAmount = null;
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
            for (String flag : record.flags()) {
                ObjectFlag oFlag;
                ObjectKindFlag kFlag;
                try {
                    oFlag = ObjectFlag.valueOf("OF_" + flag);
                    oFlags.on(oFlag);
                } catch (IllegalArgumentException e) {
                    try {
                        kFlag = ObjectKindFlag.valueOf("KF_" + flag);
                        oKindFlags.on(kFlag);
                    } catch (IllegalArgumentException e1) {
                        errors.add("Object kind starting at line: " + line + " has " +
                                "an unknown flag: " + flag);
                        illegalFlag = true;
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
            Map<Brand, Boolean> brands = new HashMap<>();
            boolean illegalBrand = false;
            for (String brand : record.brand()) {
                Brand b = GameConstants.lookupBrandCode(brand);
                if (b == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown brand: " + brand);
                    illegalBrand = true;
                } else
                    brands.put(b, true);
            }
            if (illegalBrand) continue;
            Map<Slay, Boolean> slays = new HashMap<>();
            boolean illegalSlay = false;
            for (String slay : record.slay()) {
                Slay s = GameConstants.lookupSlay(slay);
                if (s == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown slay: " + slay);
                    illegalSlay = true;
                } else
                    slays.put(s, true);
            }
            if (illegalSlay) continue;
            Map<Curse, CurseEntry> curses = new HashMap<>();
            boolean illegalCurse = false;
            for (String curseName : record.curse().keySet()) {
                Curse curse = GameConstants.lookupCurse(curseName);
                if (curse == null) {
                    errors.add("Object kind starting at line: " + line + " has " +
                            "an unknown curse: " + curseName);
                    illegalCurse = true;
                } else {
                    try {
                        int power = Integer.parseInt(record.curse().get(curseName));
                        if (power > 0) {
                            CurseData curseData = new CurseData(power, 0);
                            CurseEntry curseEntry = new CurseEntry(curse, curseData);
                            curses.put(curse, curseEntry);
                        }
                    } catch (NumberFormatException e) {
                        errors.add("Object kind starting at line: " + line + " has " +
                                "an invalid curse power format: " + record.curse().get(curseName));
                        illegalCurse = true;
                    }
                }
            }
            if (illegalCurse) continue;
            Random pVal = null;
            if (!record.pVal().isEmpty()) {
                pVal = Random.parseStr(record.pVal());
                if (pVal == null) {
                    errors.add("Object kind starting line: " + line + " has " +
                            "an invalid pVal integer format: " + record.pVal());
                    continue;
                }
            }
            String description = record.desc();
            // EffectAssembler is all-or-nothing: if any single effect in the block fails
            // to resolve it returns null, so one bad effect drops the entire object kind
            // rather than loading it with an effect silently missing.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors);
            if (effects == null) continue;
            int dd = attBase == null ? 0 : attBase.getDice();
            int ds = attBase == null ? 0 : attBase.getSides();

            itemObjects.add(new ObjectKind(name, description, base, 0, pVal,
                    attToh, attTod, acToa, ac, attBase, dd, ds,
                    weight, cost, oFlags, oKindFlags, modifiers, elInfo,
                    brands, slays, curses, adc, allocComm, allocLower, allocUpper,
                    level, new ArrayList<>(), effects, record.message(),
                    record.visMessage(), "", charges, pileChance, pileAmount,
                    null, null, null, false, false,
                    0, false, tValue));
        }

        return itemObjects;
    }
}