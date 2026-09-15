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

package uk.co.jackoftradesltd.channel.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsLexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GrammarDriver}.
 *
 * <p>Two groups of behaviour are exercised:
 * <ul>
 *   <li>{@link GrammarDriver#checkRecordCount}, driven directly with hand-picked
 *       {@code declared}/{@code actual} pairs. This method has no C original - the
 *       {@code record-count} header is a Java-only addition to this port's grammars, per its
 *       Javadoc - so the expected values are derived from the method's own documented contract
 *       rather than from the C source. The 260915 sentinel-collision fix (a declared {@code "-1"}
 *       silently treated as "no count declared") and the earlier double-error regression it
 *       briefly introduced are both covered directly;</li>
 *   <li>{@link GrammarDriver#run}'s file-not-found branch, which the per-grammar reader test
 *       suites (e.g. {@code ActivationReaderTest}) don't cover, since they only ever pass real
 *       or temp-written files. {@code run}'s other branches (clean parse, hard grammar error,
 *       soft record-count mismatch riding out with partial results) are already exercised end to
 *       end by those reader suites and are not duplicated here.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class GrammarDriverTest coded on 260915, commented in full on 260915.
 */
class GrammarDriverTest {

    private static final Logger logger = LogManager.getLogger();

    @Test
    void matchingCountAddsNoError() {
        List<String> errors = new ArrayList<>();

        GrammarDriver.checkRecordCount("3", 3, errors);

        assertTrue(errors.isEmpty(), errors.toString());
    }

    @Test
    void mismatchedCountAddsOneError() {
        List<String> errors = new ArrayList<>();

        GrammarDriver.checkRecordCount("5", 1, errors);

        assertEquals(1, errors.size());
        String error = errors.get(0);
        assertTrue(error.contains("declares 5"), error);
        assertTrue(error.contains("contains 1"), error);
    }

    @Test
    void nonNumericDeclaredAddsExactlyOneFormatError() {
        // Before the 260915 fix, a non-numeric header could add a second, fabricated
        // "declares -1" mismatch line on top of the format error, since count fell back to a
        // sentinel of -1 on the catch. The method now returns immediately after reporting the
        // format problem, so only one error is ever added here.
        List<String> errors = new ArrayList<>();

        GrammarDriver.checkRecordCount("abc", 5, errors);

        assertEquals(1, errors.size());
        String error = errors.get(0);
        assertTrue(error.contains("Invalid number format"), error);
        assertTrue(error.contains("abc"), error);
    }

    @Test
    void declaredNegativeOneIsTreatedAsARealMismatchNotASentinel() {
        // Before the 260915 fix, count was pre-seeded to -1 as an "unset" sentinel, so a
        // *parsed* declared value of -1 collided with it and the mismatch check was silently
        // skipped even though a count was genuinely declared. actual is always >= 0 in
        // production (it is a parsed-record List#size()), so declared="-1" can never
        // legitimately match and must now be reported.
        List<String> errors = new ArrayList<>();

        GrammarDriver.checkRecordCount("-1", 5, errors);

        assertEquals(1, errors.size());
        String error = errors.get(0);
        assertTrue(error.contains("declares -1"), error);
        assertTrue(error.contains("contains 5"), error);
    }

    @Test
    void declaredNegativeOneMatchingActualAddsNoError() {
        // actual can't really be -1 in production, but this isolates the equality check
        // itself: with the sentinel gone, count == actual is what governs the outcome, for
        // any pair of values including -1.
        List<String> errors = new ArrayList<>();

        GrammarDriver.checkRecordCount("-1", -1, errors);

        assertTrue(errors.isEmpty(), errors.toString());
    }

    @Test
    void runPropagatesIOExceptionForAMissingFile() {
        GrammarDriver.Extractor<ActivationsGrammar, Object> extractor = (parser, errorCatcher, errs) -> {
            fail("extractor should not run when the file cannot be opened");
            return List.of();
        };
        Assembler<Object, List<Object>> assembler = (records, errs) -> {
            fail("assembler should not run when the file cannot be opened");
            return List.of();
        };

        assertThrows(IOException.class, () -> GrammarDriver.run(
                "no-such-file-should-exist.txt",
                ActivationsLexer::new,
                ActivationsGrammar::new,
                extractor,
                assembler,
                logger));
    }
}
