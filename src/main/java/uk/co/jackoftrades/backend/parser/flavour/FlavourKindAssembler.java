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

package uk.co.jackoftrades.backend.parser.flavour;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.Flavour;
import uk.co.jackoftrades.middle.objects.FlavourKind;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link FlavourKindParseRecord}s of {@code flavor.txt} into
 * {@link FlavourKind} domain objects. For each block it resolves the tval and
 * glyph, delegates the block's individual flavours to {@link FlavourAssembler},
 * and then — because only this level knows the tval — resolves each
 * {@code fixed:} flavour's sval symbol against the object-kind table (C's
 * {@code lookup_sval}).
 *
 * <p>Because the sval lookup needs the object-kind table populated, flavours must
 * be loaded after object kinds and their artifact-synthesised special kinds
 * (the {@code fixed:} svals point exclusively at those); {@code init()} orders
 * {@code loadFlavours()} after {@code loadArtifacts()} for exactly this reason.
 *
 * <p>Failure handling is soft but coarse: a bad tval or non-single-character
 * glyph drops the whole block, and an unresolvable {@code fixed:} sval also drops
 * the whole block (a broken pin is treated as fatal to its type rather than
 * silently mis-mapped).
 *
 * @author Rowan Crowther
 */
public class FlavourKindAssembler implements Assembler<FlavourKindParseRecord, List<FlavourKind>> {
    /**
     * Assembles every kind block in the file.
     *
     * @param records the raw {@code kind:} blocks parsed from the file
     * @param errors  the soft-error sink, appended to when a block is dropped
     * @return the assembled flavour kinds
     * @author Rowan Crowther
     */
    @Override
    public List<FlavourKind> assemble(@NotNull List<FlavourKindParseRecord> records, @NotNull List<String> errors) {
        List<FlavourKind> flavourKinds = new ArrayList<>();

        for (FlavourKindParseRecord record : records) {
            int line = record.line();
            String tValString = record.tVal();
            // fromName matches on the enum constant name (TV_RING, ...), so the
            // data-file tval ("ring") needs the TV_ prefix, as every caller adds.
            TValue tVal = TValue.fromName("TV_" + tValString);
            if (tVal == null) {
                errors.add("Kind at line: " + line + " has " +
                        "an unknown tValue: " + tValString);
                continue;
            }
            String glyph = record.glyph();
            if (glyph == null || glyph.length() != 1) {
                errors.add("Kind at line: " + line + " has " +
                        "a null or multi-character glyph: " + record.glyph());
                continue;
            }
            List<Flavour> flavours = new FlavourAssembler().assemble(record.records(), errors);

            // Resolve the fixed: flavours' sval symbols now that the tval is known
            // (a random flavour keeps sVal 0 = SV_UNKNOWN). A symbol that names no
            // kind is fatal to the whole block, not just the one flavour.
            boolean badSval = false;
            for (Flavour flavour : flavours) {
                if (flavour.isFixed()) {
                    ObjectKind objectKind = GameConstants.lookupObjectKind(tVal, flavour.getsValStr());
                    if (objectKind == null) {
                        errors.add("Invalid object sValue found on flavour: " + flavour.getText());
                        badSval = true;
                        continue;
                    }
                    flavour.setsVal(objectKind.getsVal());
                }
            }
            if (badSval) continue;

            flavourKinds.add(new FlavourKind(tVal, glyph.charAt(0), flavours));
        }

        return flavourKinds;
    }
}
