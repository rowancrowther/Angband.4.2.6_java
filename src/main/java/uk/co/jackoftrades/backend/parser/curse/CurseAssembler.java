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

package uk.co.jackoftrades.backend.parser.curse;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the raw {@link CurseParseRecord}s produced by the grammar into fully
 * resolved {@link Curse} domain objects — the assembler half of the curse loader
 * (Java port of {@code parse_curse_*} in {@code src/obj-init.c}).
 * <p>
 * The work is split into two passes, mirroring the Summon loader:
 * <ol>
 *   <li><b>First pass</b> — for each record, resolve every field against its
 *       registry/enum and build a {@link Curse}. A record with any malformed
 *       field is reported into {@code errors} and skipped ({@code continue}); the
 *       remaining records still load.</li>
 *   <li><b>Second pass</b> — once every curse exists, link each curse's
 *       {@code conflict:} names to the actual {@link Curse} instances. Deferring
 *       this to a second pass lets forward references resolve, mirroring C, which
 *       stores the conflict as a name string and matches by name rather than
 *       holding a pointer.</li>
 * </ol>
 * Two data lines each fan out across <em>two</em> destinations, following the C
 * originals:
 * <ul>
 *   <li>the {@code flags:} line ({@code parse_curse_flags}) — {@code HATES_}/
 *       {@code IGNORE_} tokens set element flags on {@code elInfo}; everything
 *       else is an {@link ObjectFlag};</li>
 *   <li>the {@code values:} line ({@code parse_curse_values}) — {@code RES_}
 *       tokens set a per-element resistance level on {@code elInfo}; everything
 *       else is an additive {@link ObjectModifier}.</li>
 * </ul>
 * Both the flag and value writers reach an element through
 * {@link Map#computeIfAbsent}, so an element touched by both lines shares one
 * {@link ElementInfo} instead of one write clobbering the other.
 * <p>
 * <b>Load-order precondition:</b> the shipped {@code curse.txt} uses
 * {@code EF_SUMMON}, which {@code EffectAssembler} resolves via
 * {@link GameConstants#lookupSummon}. The summon registry must therefore be
 * populated before curse assembly runs (mirroring C's summons-before-curses
 * order).
 *
 * @author Rowan Crowther
 */
public class CurseAssembler implements Assembler<CurseParseRecord, List<Curse>> {
    /**
     * Assemble the parsed curse records into resolved {@link Curse}s. Malformed
     * records are reported into {@code errors} and skipped rather than aborting
     * the whole file; an unresolvable {@code conflict:} name is a soft error that
     * still leaves the curse loaded (with that one link omitted).
     *
     * @param records the raw parse records from the grammar
     * @param errors  accumulating list of soft (skip-and-continue) error messages
     * @return the assembled curses, with conflict links resolved
     * @author Rowan Crowther
     */
    @Override
    public List<Curse> assemble(@NotNull List<CurseParseRecord> records, @NotNull List<String> errors) {
        List<Curse> results = new ArrayList<>();

        // First pass
        for (CurseParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            List<ObjectBase> types = new ArrayList<>();
            boolean badType = false;
            // type: names an object base by its tval (e.g. "cloak", "soft armor"),
            // not its display name — resolve the tval, then fetch the base for it.
            for (String base : record.type()) {
                ObjectBase objectBase = null;
                try {
                    TValue tValue = TValue.valueOf("TV_" + base.toUpperCase().replace(" ", "_"));
                    objectBase = GameConstants.getBaseFromTVal(tValue);
                } catch (IllegalArgumentException ignored) {
                    // falls through to the null check below with the same error
                }
                if (objectBase == null) {
                    errors.add("Curse starting at line: " + line + " has " +
                            "an unknown type: " + base);
                    badType = true;
                } else {
                    types.add(objectBase);
                }
            }
            if (badType) continue;
            // weight: is optional — an absent line arrives blank and means no
            // adjustment (0), which must not be parsed as a malformed integer.
            int weightAdjustment = 0;
            if (!record.weightAdjustment().isEmpty()) {
                try {
                    weightAdjustment = Integer.parseInt(record.weightAdjustment());
                } catch (NumberFormatException e) {
                    errors.add("Curse starting at line: " + line + " has " +
                            "invalid weight adjustment value: " + record.weightAdjustment());
                    continue;
                }
            }
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors);
            boolean badEffect = (effects == null);
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            List<ObjectFlag> objectFlags = new ArrayList<>();
            boolean illegalFlag = false;
            // flags: line fans out — HATES_/IGNORE_ set element flags on elInfo,
            // everything else is an object flag.
            for (String flag : record.flags()) {
                if (flag.startsWith("HATES_") || flag.startsWith("IGNORE_")) {
                    boolean hates = flag.startsWith("HATES_");
                    String elemName = "ELEM_" + flag.substring(hates ? 6 : 7);
                    try {
                        ElementEnum elementEnum = ElementEnum.valueOf(elemName);
                        ElementInfo info = elInfo.computeIfAbsent(elementEnum, k -> new ElementInfo());
                        info.on(hates ? ElementInfoEnum.EL_INFO_HATES : ElementInfoEnum.EL_INFO_IGNORE);
                    } catch (IllegalArgumentException e) {
                        errors.add("Curse starting at line: " + line + " has " +
                                "an unknown element flag: " + flag);
                        illegalFlag = true;
                    }
                } else {
                    ObjectFlag objectFlag;
                    try {
                        objectFlag = ObjectFlag.valueOf("OF_" + flag);
                        objectFlags.add(objectFlag);
                    } catch (IllegalArgumentException e) {
                        errors.add("Curse starting at line: " + line + " has " +
                                "an unknown object flag: " + flag);
                        illegalFlag = true;
                    }
                }
            }
            if (illegalFlag || badEffect) continue;
            Map<ObjectModifier, Integer> modifiers = new HashMap<>();
            boolean illegalValue = false;
            // values: line fans out — RES_* set a per-element resistance level on
            // elInfo, everything else is an additive object modifier.
            for (String key : record.values().keySet()) {
                String value = record.values().get(key);
                if (key.startsWith("RES_")) {
                    String elemFlag = "ELEM_" + key.substring(4);
                    try {
                        ElementEnum element = ElementEnum.valueOf(elemFlag);
                        int val = Integer.parseInt(value);
                        ElementInfo info = elInfo.computeIfAbsent(element, k -> new ElementInfo());
                        info.setResLevel(val);
                    } catch (NumberFormatException e) {
                        errors.add("Curse starting at line: " + line + " has " +
                                "a badly formed integer value: " + key + "[" + value + "]");
                        illegalValue = true;
                    } catch (IllegalArgumentException e) {
                        errors.add("Curse starting at line: " + line + " has " +
                                "an unknown element: " + key + "[" + value + "]");
                        illegalValue = true;
                    }
                } else {
                    try {
                        ObjectModifier om = ObjectModifier.valueOf("OM_" + key);
                        int val = Integer.parseInt(value);
                        modifiers.put(om, val);
                    } catch (NumberFormatException e) {
                        errors.add("Curse starting at line: " + line + " has " +
                                "a badly formed integer value: " + key + "[" + value + "]");
                        illegalValue = true;
                    } catch (IllegalArgumentException e) {
                        errors.add("Curse starting at line: " + line + " has " +
                                "an unknown modifier: " + key + "[" + value + "]");
                        illegalValue = true;
                    }
                }
            }
            if (illegalValue) continue;
            int toh = 0;
            if (!record.combatToH().isEmpty()) {
                try {
                    toh = Integer.parseInt(record.combatToH());
                } catch (NumberFormatException e) {
                    errors.add("Curse starting at line: " + line + " has " +
                            "invalid combat toh value : " + record.combatToH());
                    continue;
                }
            }
            int tod = 0;
            if (!record.combatToD().isEmpty()) {
                try {
                    tod = Integer.parseInt(record.combatToD());
                } catch (NumberFormatException e) {
                    errors.add("Curse starting at line: " + line + " has " +
                            "invalid combat tod value : " + record.combatToD());
                    continue;
                }
            }
            int toa = 0;
            if (!record.combatToA().isEmpty()) {
                try {
                    toa = Integer.parseInt(record.combatToA());
                } catch (NumberFormatException e) {
                    errors.add("Curse starting at line: " + line + " has " +
                            "invalid combat toa value : " + record.combatToA());
                    continue;
                }
            }
            List<String> conflictingCurses = new ArrayList<>(record.conflict());
            List<ObjectFlag> cFlags = new ArrayList<>();
            boolean illegalCFlag = false;
            for (String cFlag : record.cFlag()) {
                try {
                    ObjectFlag objectFlag = ObjectFlag.valueOf("OF_" + cFlag);
                    cFlags.add(objectFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Curse starting at line: " + line + " has " +
                            "invalid conflict flag: " + cFlag);
                    illegalCFlag = true;
                }
            }
            if (illegalCFlag) continue;
            StringBuilder sb = new StringBuilder();
            for (String desc : record.desc())
                sb.append(desc);
            String description = sb.toString();
            String message = record.message();

            results.add(new Curse(name, types, weightAdjustment,
                    effects, objectFlags, modifiers, elInfo,
                    toh, tod, toa, conflictingCurses, cFlags,
                    description, message));
        }

        // Second pass - link the conflicting curses
        for (Curse curse : results) {
            List<Curse> conflicts = new ArrayList<>();

            for (String conflictName : curse.getConflictNames()) {
                Curse c = results.stream()
                        .filter(r -> r.getName().equals(conflictName))
                        .findFirst().orElse(null);
                if (c == null) {
                    errors.add("Invalid curse conflict name: " + conflictName +
                            " found for block: " + curse.getName());
                    continue;
                }
                conflicts.add(c);
            }

            curse.setConflict(conflicts);
        }

        return results;
    }
}
