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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.activation.ActivationAssembler;
import uk.co.jackoftrades.backend.parser.activation.ActivationParseRecord;
import uk.co.jackoftrades.middle.Activation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ActivationAssembler} (grammar-suite assembler track).
 *
 * <p>These construct {@link ActivationParseRecord}s directly - no grammar, no reader - to
 * exercise the assembler in isolation. Under the split architecture the grammar is now
 * extraction-only: it hands the assembler raw strings ({@code level}, {@code power},
 * {@code aim}) and leaves every numeric conversion and default to this stage. That is
 * precisely the behaviour pinned here, and it can only be reached by feeding the assembler
 * records the grammar itself could never emit: the {@code INTEGER} token guarantees a run of
 * digits, so a genuinely non-numeric {@code level}/{@code power} never survives lexing and
 * has to be injected by hand to test the conversion's failure branch.
 *
 * <p>Two behaviours are the focus:
 * <ul>
 *   <li>the empty-string guards - an absent {@code level:}/{@code power:} directive arrives as
 *       {@code ""} and must default to {@code 0} rather than blow up;</li>
 *   <li>the suite-wide skip-and-continue policy - a record whose {@code level}/{@code power}
 *       fails to parse is dropped with a line-tagged error while the remaining records still
 *       assemble.</li>
 * </ul>
 *
 * <p>Every record here carries an <em>empty</em> effect list, so the tests stay independent of
 * {@code EffectAssembler} and the {@code GameConstants} registries it consults; effect
 * resolution has its own coverage.
 *
 * @author Rowan Crowther
 */
class ActivationAssemblerTest {

    /**
     * Builds a parse record with the given raw directive strings and an empty effect list,
     * mirroring what an extraction-only grammar would hand the assembler. {@code message} and
     * {@code desc} are left blank as they need no conversion.
     *
     * @param name  the activation code name
     * @param aim   the raw {@code aim:} value ("0"/"1")
     * @param level the raw {@code level:} value (may be "" for absent)
     * @param power the raw {@code power:} value (may be "" for absent)
     * @param line  the source line the record is reported against
     * @return a fully populated {@link ActivationParseRecord} with no effects
     */
    private static ActivationParseRecord rec(String name, String aim, String level,
                                             String power, int line) {
        return new ActivationParseRecord(name, aim, level, power, "", "", List.of(), line);
    }

    @Test
    void cleanRecordAssemblesToOneActivation() {
        List<String> errors = new ArrayList<>();

        List<Activation> out = new ActivationAssembler()
                .assemble(List.of(rec("CURE_LIGHT", "0", "5", "10", 3)), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        assertEquals("CURE_LIGHT", out.get(0).getName());
    }

    @Test
    void absentLevelAndPowerDefaultToZeroWithoutError() {
        List<String> errors = new ArrayList<>();

        // An absent level:/power: directive reaches the assembler as the empty string; the
        // empty-string guard must treat it as 0 rather than attempt to parse it.
        List<Activation> out = new ActivationAssembler()
                .assemble(List.of(rec("NO_NUMBERS", "1", "", "", 2)), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        assertEquals("NO_NUMBERS", out.get(0).getName());
    }

    @Test
    void malformedLevelIsReportedAndRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Activation> out = new ActivationAssembler()
                .assemble(List.of(rec("BAD_LEVEL", "0", "high", "1", 7)), errors);

        assertTrue(out.isEmpty());
        assertEquals(1, errors.size());
        String error = errors.get(0);
        assertTrue(error.contains("malformed level"), error);
        assertTrue(error.contains("7"), error);
    }

    @Test
    void malformedPowerIsReportedAndRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Activation> out = new ActivationAssembler()
                .assemble(List.of(rec("BAD_POWER", "0", "5", "lots", 9)), errors);

        assertTrue(out.isEmpty());
        assertEquals(1, errors.size());
        String error = errors.get(0);
        assertTrue(error.contains("malformed power"), error);
        assertTrue(error.contains("9"), error);
    }

    @Test
    void oneBadRecordDoesNotStopTheRest() {
        List<String> errors = new ArrayList<>();

        // The first record's level is unparseable; the skip-and-continue policy drops it with
        // an error but must still assemble the clean record that follows.
        List<Activation> out = new ActivationAssembler().assemble(
                List.of(rec("BROKEN", "0", "nope", "1", 4),
                        rec("FINE", "0", "5", "1", 8)),
                errors);

        assertEquals(1, out.size());
        assertEquals("FINE", out.get(0).getName());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("malformed level"), errors.get(0));
    }
}
