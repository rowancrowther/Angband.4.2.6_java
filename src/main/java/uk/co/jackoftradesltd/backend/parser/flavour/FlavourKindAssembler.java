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

package uk.co.jackoftradesltd.backend.parser.flavour;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Flavour;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Turns the raw {@link FlavourKindParseRecord}s of {@code flavor.txt} into
 * {@link FlavourKind} domain objects. For each block it resolves the tval and
 * glyph, delegates the block's individual flavours to {@link FlavourAssembler},
 * and then — because only this level knows the tval — resolves each
 * {@code fixed:} flavour's sval symbol against the object-kind table (C's
 * {@code lookup_sval}).
 *
 * <p>Because the sval lookup needs the object-kind table populated, flavours must
 * be loaded after object kinds and their artifact-synthesized special kinds
 * (the {@code fixed:} svals point exclusively at those); {@code init()} orders
 * {@code loadFlavours()} after {@code loadArtifacts()} for exactly this reason.
 *
 * <p>The block-level work is C's {@code parse_flavor_kind} in {@code init.c}, and the sval
 * resolution is the {@code lookup_sval} call ({@code obj-util.c}) inside
 * {@code parse_flavor_flavor}. Its outcomes are:
 * <ul>
 *   <li>A tval that resolves to nothing, or to {@link TValue#TV_NONE}, ends the load with
 *       {@link DataFileParseException}, as C's {@code PARSE_ERROR_UNRECOGNISED_TVAL} does for
 *       {@code tval <= 0}.</li>
 *   <li>A glyph that is not exactly one code point ends the load too, as C's
 *       {@code PARSE_ERROR_FIELD_TOO_LONG} (or missing field) does. The code point is kept whole,
 *       as C keeps a {@code wchar_t}.</li>
 *   <li>An index or colour fault in a block's flavours ends the load from inside
 *       {@link FlavourAssembler}. Nothing in this class is a soft error, so the {@code errors} list
 *       is never appended to.</li>
 *   <li>A {@code fixed:} sval that names no kind is not an error: C's {@code lookup_sval} answers
 *       -1, which the {@code uint8_t sval} of {@code struct flavor} holds as 255, so the flavour is
 *       kept with 255. A literal number is stored as it stands, wrapped to eight bits, whether or
 *       not any kind carries it; see {@link #assemble(List, List, String)}.</li>
 * </ul>
 *
 * <p>Accepted deviation (2026-10-25, "A {@code fixed:} sval with trailing spaces or tabs is looked
 * up as a name" in {@code docs/Accepted_Deviations.md}): C's {@code strtoul} reading lets a tail of
 * spaces and tabs through, so {@code "5 "} is 5, whereas here the tail makes the symbol a name, which
 * misses, and the flavour stores 255. All 8 {@code fixed:} lines in {@code flavor.txt} name their
 * kind in words.
 *
 * <p>Class FlavourKindAssembler coded before 260915, commented in full on 261025.
 *
 * @author Rowan Crowther
 */
public class FlavourKindAssembler implements Assembler<FlavourKindParseRecord, List<FlavourKind>> {
    /**
     * The log the fatal tval and glyph messages are written to just before
     * {@link DataFileParseException} is thrown. Nothing else is logged here.
     *
     * <p>Field logger coded before 260915, commented in full on 261025.
     */
    private static final Logger logger = LogManager.getLogger(FlavourKindAssembler.class);

    /**
     * Assembles every kind block in the file, in file order. For each block it resolves the tval
     * with {@link TValue#fromName(String)} (which takes the display name {@code "ring"}, a number
     * or the {@code TV_} identifier) and the glyph, hands the block's flavours to
     * {@link FlavourAssembler}, and then gives each {@code fixed:} flavour its sval now that the
     * tval is known. A random ({@code flavor:}) flavour is left at 0, C's {@code SV_UNKNOWN}.
     *
     * <p>The sval symbol of a {@code fixed:} flavour goes down one of two paths, following
     * {@code lookup_sval} in {@code obj-util.c}:
     * <ul>
     *   <li><b>Number.</b> If {@link Integer#parseInt(String)} accepts the symbol, that number is
     *       the answer and no kind has to carry it. A negative figure is -1 and so stored as 255
     *       (C's {@code strtoul} wraps {@code -5} far above {@code INT_MAX}, which
     *       {@code lookup_sval} refuses); any other is reduced modulo 256, the {@code uint8_t}
     *       store. {@code +5} is 5, {@code 300} is 44, {@code 256} is 0 and {@code 2147483646} is
     *       254. The kind lookup on the wrapped figure only reads its own sval back, so it changes
     *       nothing, and a throwaway {@link ObjectKind} carries the figure when none exists.</li>
     *   <li><b>Name.</b> Anything {@code parseInt} refuses goes to
     *       {@link ObjectRegistry#lookupObjectKind(TValue, String)}, which matches the kind's
     *       sval name without regard to case, within the block's own tval. A miss is 255. That
     *       takes in a figure too big for an {@code int}, which C's {@code r < INT_MAX} test
     *       also sends to -1, and trailing text such as {@code 12abc}, which C refuses
     *       outright; neither can match a real kind name.</li>
     * </ul>
     * {@code 2147483647} is read as a number here and gives 255 only because its low eight bits
     * are all ones; C refuses it as {@code INT_MAX} and gives 255 as well.
     *
     * <p>The flavours of a block come out newest-first inside its {@link FlavourKind}, as C's list
     * does; the blocks themselves stay in file order.
     *
     * <p>Because {@link ObjectRegistry} refuses a lookup while the kind table is empty, the object
     * kinds have to be loaded before this runs, whichever path an sval takes; that throws
     * {@link IllegalStateException}, where C's loop over an empty {@code k_info} would just
     * answer -1.
     *
     * <p>Function assemble coded before 260915, updated on 261025 to follow {@code lookup_sval}
     * for literal numbers, commented in full on 261025.
     *
     * @param records  the raw {@code kind:} blocks parsed from the file
     * @param errors   the soft-error sink, passed to {@link FlavourAssembler} and not appended to
     * @param filename the file being read, for the messages
     * @return the assembled flavour kinds
     * @throws DataFileParseException if a block's tval resolves to nothing or to {@code TV_NONE},
     *                                its glyph is not exactly one code point, or one of its
     *                                flavours has a bad index
     * @throws IllegalStateException  if the object kinds have not been loaded and a block has a
     *                                {@code fixed:} flavour
     */
    @Override
    public List<FlavourKind> assemble(@NotNull List<FlavourKindParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<FlavourKind> flavourKinds = new ArrayList<>();

        for (FlavourKindParseRecord record : records) {
            int line = record.line();
            String tValString = record.tVal();
            // fromName takes the data-file name ("ring"), a number or the TV_ identifier
            TValue tVal = TValue.fromName(tValString);
            if (tVal == null || tVal == TValue.TV_NONE) {
                String msg = "Flavour Kind starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal TValue value: " + record.tVal();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            String glyph = record.glyph();
            IntStream glyphStream = glyph.codePoints();
            int [] glyphInts = glyphStream.toArray();
            if (glyphInts.length != 1) {
                String msg = "Flavour Kind starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal glyph value: " + record.glyph();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            
            List<Flavour> flavours = new FlavourAssembler().assemble(record.records(), errors, filename);

            // Resolve the fixed: flavours' sval symbols now that the tval is known
            // (a random flavour keeps sVal 0 = SV_UNKNOWN). A symbol that names no
            // kind is not an error: the flavour is kept with 255, C's -1 in a uint8_t.
            for (Flavour flavour : flavours) {
                if (flavour.isFixed()) {
                    String svalString = flavour.getsValStr();
                    
                    int sValInt;
                    ObjectKind objectKind;
                    int b;
                    
                    try {
                        sValInt = Integer.parseInt(svalString);
                        // a negative figure is -1 to lookup_sval, which becomes 255 below
                        if (sValInt < 0)
                            b = -1;
                        else
                            // wrap the integer modulo 256
                            b = sValInt % 256;
                        objectKind = ObjectRegistry.lookupObjectKind(tVal, b);
                        if (objectKind == null) {
                            objectKind = new ObjectKind();
                            objectKind.setTval(tVal);
                            if (b == -1) b = 255;
                            objectKind.setsVal(b);
                        }
                    } catch (NumberFormatException e) {
                        objectKind = ObjectRegistry.lookupObjectKind(tVal, svalString);
                        if (objectKind == null) {
                            objectKind = new ObjectKind();
                            objectKind.setTval(tVal);
                            objectKind.setsVal(255);
                        }
                    }
                    
                    flavour.setsVal(objectKind.getsVal());
                }
            }

            flavourKinds.add(new FlavourKind(tVal, glyphInts[0], flavours));
        }

        return flavourKinds;
    }
}
