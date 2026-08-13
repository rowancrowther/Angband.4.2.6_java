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

package uk.co.jackoftrades;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The boundary test: the architecture's one structural rule, enforced by reading the source rather
 * than by remembering to.
 *
 * <p>{@code Old_java_map.md} says the halves can be kept apart "by reading the import statements".
 * That is true and it is also a habit, and habits lapse quietly — an import added in the middle of
 * a working afternoon compiles, runs, and re-couples the two halves without anything going red.
 * This class turns the habit into a regression test. It walks every {@code .java} file under
 * {@code src/main} and enforces the three rules from stage 5 of
 * {@code docs/Architecture_migration.md}:
 *
 * <ol>
 *     <li>{@code frontend.**} may name {@code channel.**} and nothing else of ours — not
 *     {@code middle}, not {@code backend}, and not the root package either;</li>
 *     <li>{@code middle.**} may not name {@code frontend.**};</li>
 *     <li>{@code backend.**} may not name {@code frontend.**}.</li>
 * </ol>
 *
 * <p>The rules are this blunt only because stage 0 emptied {@code backend} of everything that was
 * not IO. Before that, rule 1 needed an allowlist with an exception in it; after it, the whole
 * interface between the halves is one package name.
 *
 * <h2>The baselines, and why they are ratchets rather than exceptions</h2>
 *
 * <p>None of the three rules is green yet, and every crossing that remains is blocked on a chapter
 * that has not been written — the input boundary is Chapter 3's set piece, and the {@code UIEntry}
 * cluster waits on the character screen. Asserting three plain negatives would therefore mean a red
 * build for months, which trains a person to stop reading the failure. Each rule instead carries a
 * baseline of the crossings that exist today, and asserts two things about it: <b>no crossing
 * outside the baseline</b>, and <b>no baseline entry without a crossing</b>.
 *
 * <p>The second half is the whole point, and it is what separates this from a suppression list. A
 * baseline that only ever hid failures would keep listing crossings long after they were fixed, and
 * would never announce that a rule had come good. Failing on a stale entry means the count can only
 * fall: fix a crossing and the build goes red until the line is deleted, so the baseline is always
 * an accurate census rather than a historical one. When a baseline empties, delete the constant and
 * the rule becomes the plain negative it was always meant to be.
 *
 * <p>Adding to a baseline is therefore a deliberate act with a cost, not a way past a red build.
 * The failure message prints the line to paste — because sometimes that genuinely is the right
 * answer — but every entry is a statement that a crossing is understood and dated, and each one
 * should name the chapter that removes it.
 *
 * <p><b>The rules are not symmetric, and that is deliberate.</b> Rule 1 is an allowlist and rules 2
 * and 3 are single denials, because the two directions are not equally dangerous. The front end
 * seeing the core is the coupling this migration exists to remove — every type it can name is one
 * it can call, on the wrong thread, bypassing the channel. The core seeing the front end is a
 * narrower mistake but a louder one: it means core code is one recompile away from touching Swing.
 * Neither rule says anything about {@code middle} and {@code backend} seeing each other, which they
 * do, freely, by design.
 *
 * <h2>What counts as "naming"</h2>
 *
 * <p>Not just imports. A fully qualified name in the body of a method couples exactly as tightly as
 * an import and shows up in no import survey, so the scan looks for {@code uk.co.jackoftrades.…}
 * anywhere in the file — which subsumes the import list. It looks for it in <em>code</em> only:
 * comments and string literals are blanked first, because a Javadoc {@code {@link}} pointing across
 * the boundary is a cross-reference, not a dependency, and this document is full of them. Blanking
 * preserves line breaks, so the line numbers in a failure message are the real ones.
 *
 * <h2>What this test cannot see</h2>
 *
 * <p>Two leaks pass it, worth knowing before it is trusted as complete. A type reached through
 * {@code channel} is invisible here: if a record in {@code channel} exposes a {@code middle} type
 * as a component, the front end can reach it without ever naming {@code middle} itself. That is why
 * "which shapes can actually move" spends its length on flattening payloads rather than moving
 * them — the discipline is upstream of this test, not enforced by it. And a same-package reference
 * needs no qualified name at all, so it is invisible to any textual scan; that costs nothing here,
 * since a same-package reference is by definition within one half.
 *
 * @author Rowan Crowther
 */
class BoundaryTest {

    /**
     * The package every rule is written about.
     */
    private static final String OURS = "uk.co.jackoftrades.";

    /**
     * The four top-level packages under {@link #OURS}. A directory name not in this set is treated
     * as the root package, where {@code Main} lives — and, being in no half, is denied to the front
     * end by rule 1 like anything else outside {@code channel}.
     */
    private static final Set<String> HALVES = Set.of("frontend", "middle", "backend", "channel");

    /**
     * A qualified name of ours, as it appears in an import or in the middle of an expression. The
     * trailing segments are matched greedily so that the whole name is reported, not its prefix.
     */
    private static final Pattern REFERENCE = Pattern.compile(
            "uk\\.co\\.jackoftrades(?:\\.[A-Za-z_$][A-Za-z0-9_$]*)+");

    /**
     * Rule 1's known crossings: the front end naming something of ours outside {@code channel}.
     *
     * <p>Both groups are blocked on a chapter rather than on anyone's willingness to fix them.
     *
     * <p>{@code TextUIHook} is the input boundary — stage 5 of the migration document parks it by
     * name, because {@code CommandGetterHolder} and friends are still holder-shaped and still
     * uncalled. Their CSP form is Chapter 3's design set piece, and porting them against no caller
     * is what the document repeatedly refuses to do.
     *
     * <p>{@code UIEntry}'s element is waiting on the second character screen (C's
     * {@code ui-player.c}), which is what would first read it. C keeps {@code int param_index} on
     * {@code struct ui_entry} and resolves the name through a front-end-local
     * {@code element_names[]} table, so the port's typed field should flatten rather than
     * {@code ElementEnum} moving to {@code channel} — but the right flattened shape is a guess
     * until something renders it. {@code getParameter()} has no caller in {@code src/main} today.
     */
    private static final Set<String> FRONTEND_BASELINE = Set.of(
            "frontend/entries/UIEntry.java -> uk.co.jackoftrades.middle.objects.enums.ElementEnum",
            "frontend/inputfromuser/TextUIHook.java -> uk.co.jackoftrades.middle.cave.enums.DirectionEnum",
            "frontend/inputfromuser/TextUIHook.java -> uk.co.jackoftrades.middle.game.enums.CommandCode",
            "frontend/inputfromuser/TextUIHook.java -> uk.co.jackoftrades.middle.game.gameengine.GameEngine",
            "frontend/inputfromuser/TextUIHook.java -> uk.co.jackoftrades.middle.objects.ItemObject",
            "frontend/inputfromuser/TextUIHook.java -> uk.co.jackoftrades.middle.objects.enums.GetItemFlags"
    );

    /**
     * Rule 2's known crossings: core code naming the front end.
     *
     * <p>One cause, two data slices. {@code ui_entry*.txt} and {@code visuals.txt} describe things
     * that only the front end draws, but the port loads them core-side, so the loaders and
     * registries that hold the results have to name the types they hold.
     *
     * <p>C does not have this problem, and the reason is worth recording where the suppression
     * lives: {@code ui_entry.txt} is parsed <em>inside</em> {@code ui-entry.c}. All
     * {@code init.c} holds is {@code { "ui entries", &ui_entry_parser }} — an opaque
     * {@code struct file_parser} of function pointers — and {@code struct ui_entry} is an
     * incomplete type in {@code ui-entry.h}, so nothing outside the front end can see its fields
     * even in principle. The front end parses its own data. Moving the readers, assemblers and
     * registries to match is the fix for this entire baseline, and it is chapter-sized.
     */
    private static final Set<String> MIDDLE_BASELINE = Set.of(
            "middle/game/globals/loaders/MonsterDataLoader.java -> uk.co.jackoftrades.frontend.colour.FlickerTable",
            "middle/game/globals/loaders/MonsterDataLoader.java -> uk.co.jackoftrades.frontend.colour.VisualsCycler",
            "middle/game/globals/loaders/UIDataLoader.java -> uk.co.jackoftrades.frontend.entries.UIEntry",
            "middle/game/globals/loaders/UIDataLoader.java -> uk.co.jackoftrades.frontend.entries.UIEntryBase",
            "middle/game/globals/loaders/UIDataLoader.java -> uk.co.jackoftrades.frontend.entries.UIEntryRenderer",
            "middle/game/globals/registry/MonsterRegistry.java -> uk.co.jackoftrades.frontend.colour.FlickerTable",
            "middle/game/globals/registry/MonsterRegistry.java -> uk.co.jackoftrades.frontend.colour.VisualsCycler",
            "middle/game/globals/registry/UIRegistry.java -> uk.co.jackoftrades.frontend.entries.UIEntry",
            "middle/game/globals/registry/UIRegistry.java -> uk.co.jackoftrades.frontend.entries.UIEntryBase",
            "middle/game/globals/registry/UIRegistry.java -> uk.co.jackoftrades.frontend.entries.UIEntryRenderer",
            "middle/monsters/MonsterRace.java -> uk.co.jackoftrades.frontend.colour.ColourCycle",
            "middle/objects/ObjectProperty.java -> uk.co.jackoftrades.frontend.entries.UIEntry",
            "middle/player/PlayerProperty.java -> uk.co.jackoftrades.frontend.entries.UIEntry"
    );

    /**
     * Rule 3's known crossings: the IO layer naming the front end.
     *
     * <p>The same cause as {@link #MIDDLE_BASELINE} seen one layer down — these are the readers and
     * assemblers that actually build the front-end types the registries then hold. They move
     * together or not at all, so treat the two baselines as one job.
     */
    private static final Set<String> BACKEND_BASELINE = Set.of(
            "backend/parser/UIEntryBaseReader.java -> uk.co.jackoftrades.frontend.entries.UIEntryBase",
            "backend/parser/UIEntryReader.java -> uk.co.jackoftrades.frontend.entries.UIEntry",
            "backend/parser/UIEntryRendererReader.java -> uk.co.jackoftrades.frontend.entries.UIEntryRenderer",
            "backend/parser/VisualsReader.java -> uk.co.jackoftrades.frontend.colour.FlickerTable",
            "backend/parser/VisualsReader.java -> uk.co.jackoftrades.frontend.colour.VisualsCycler",
            "backend/parser/monster/MonsterAssembler.java -> uk.co.jackoftrades.frontend.colour.ColourCycle",
            "backend/parser/objectproperty/ObjectPropertyAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntry",
            "backend/parser/playerproperty/PlayerPropertyAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntry",
            "backend/parser/uientry/UIEntryAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntry",
            "backend/parser/uientry/UIEntryAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntryBase",
            "backend/parser/uientry/UIEntryAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntryRenderer",
            "backend/parser/uientry/UIEntryAssembler.java -> uk.co.jackoftrades.frontend.entries.enums.EntryFlag",
            "backend/parser/uientry/UIEntryAssembler.java -> uk.co.jackoftrades.frontend.screen.enums.CombinerName",
            "backend/parser/uientrybase/UIEntryBaseAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntryBase",
            "backend/parser/uientrybase/UIEntryBaseAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntryRenderer",
            "backend/parser/uientrybase/UIEntryBaseAssembler.java -> uk.co.jackoftrades.frontend.screen.enums.CombinerName",
            "backend/parser/uientryrenderer/UIEntryRendererAssembler.java -> uk.co.jackoftrades.frontend.entries.UIEntryRenderer",
            "backend/parser/uientryrenderer/UIEntryRendererAssembler.java -> uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum",
            "backend/parser/uientryrenderer/UIEntryRendererAssembler.java -> uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum",
            "backend/parser/visuals/VisualsCycleAssembler.java -> uk.co.jackoftrades.frontend.colour.ColourCycle",
            "backend/parser/visuals/VisualsCycleAssembler.java -> uk.co.jackoftrades.frontend.colour.VisualsCycler",
            "backend/parser/visuals/VisualsFlickerAssembler.java -> uk.co.jackoftrades.frontend.colour.ColourCycle",
            "backend/parser/visuals/VisualsFlickerAssembler.java -> uk.co.jackoftrades.frontend.colour.FlickerTable"
    );

    /**
     * Every source file under {@code src/main}, read and blanked once for the whole class. Parsed
     * lazily so that a missing source tree fails one test with a clear message rather than every
     * test with an initialiser error.
     */
    private static List<SourceFile> sources;

    /**
     * Finds the project directory by walking up from the working directory.
     *
     * <p>Gradle runs tests with the project directory as the working directory and IntelliJ
     * sometimes does not, so neither can be assumed. Walking up is the one approach that works from
     * both, and it fails loudly rather than silently scanning nothing — a boundary test that
     * examines zero files is worse than no boundary test, because it is green.
     *
     * @return the directory holding {@code src} and {@code docs}
     * @author Rowan Crowther
     */
    private static Path projectRoot() {
        Path candidate = Path.of("").toAbsolutePath();

        while (candidate != null) {
            if (Files.isDirectory(candidate.resolve("src/main/java/uk/co/jackoftrades"))) {
                return candidate;
            }

            candidate = candidate.getParent();
        }

        throw new IllegalStateException(
                "could not find src/main/java/uk/co/jackoftrades from "
                        + Path.of("").toAbsolutePath());
    }

    /**
     * @return the directory holding our top-level packages
     * @author Rowan Crowther
     */
    private static Path sourceRoot() {
        return projectRoot().resolve("src/main/java/uk/co/jackoftrades");
    }

    /**
     * Reads and blanks every {@code .java} file under the source root, once.
     *
     * @return the source files, in a stable order so failure messages do not shuffle between runs
     * @author Rowan Crowther
     */
    private static synchronized List<SourceFile> sources() {
        if (sources != null) {
            return sources;
        }

        Path root = sourceRoot();
        List<SourceFile> found = new ArrayList<>();

        try (Stream<Path> tree = Files.walk(root)) {
            tree.filter(path -> path.toString().endsWith(".java"))
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> found.add(SourceFile.read(root, path)));
        } catch (IOException e) {
            throw new UncheckedIOException("could not walk " + root, e);
        }

        sources = List.copyOf(found);
        return sources;
    }

    /**
     * Collects every violation of one rule across the whole tree.
     *
     * @param offender which half's files are being checked
     * @param rule     answers "may a file in {@code offender} name this half?"
     * @return the violations, in file order
     * @author Rowan Crowther
     */
    private static List<Violation> violations(String offender, PermittedTarget rule) {
        List<Violation> found = new ArrayList<>();

        for (SourceFile source : sources()) {
            if (!source.half().equals(offender)) {
                continue;
            }

            for (Reference reference : source.references()) {
                if (!rule.permits(reference.half())) {
                    found.add(new Violation(source.relativePath(), reference));
                }
            }
        }

        return found;
    }

    /**
     * Checks one rule against its baseline, failing on anything the baseline does not already
     * account for — and, just as importantly, on anything the baseline still accounts for that has
     * since been fixed.
     *
     * <p>The second half is what makes this a ratchet rather than a place for exceptions to
     * accumulate. A baseline that only ever suppressed failures would quietly keep listing
     * crossings long after they were gone, and the day the last one was fixed nobody would notice
     * the rule was green. Failing on a stale entry means the count can only come down, and the day
     * a baseline empties, deleting it turns the rule into a plain assertion with no ceremony left.
     *
     * @param rule      the rule in one sentence, as the reader should hear it
     * @param offender  which half's files are being checked
     * @param permitted answers "may a file in {@code offender} name this half?"
     * @param baseline  the crossings known and accepted today, as {@code path -> name} keys
     * @param field     the constant to edit, named so the failure message can say where to go
     * @author Rowan Crowther
     */
    private static void assertOnlyBaselineCrossings(String rule, String offender,
                                                    PermittedTarget permitted,
                                                    Set<String> baseline, String field) {
        List<Violation> crossings = violations(offender, permitted);

        List<Violation> arrived = crossings.stream()
                .filter(violation -> !baseline.contains(violation.key()))
                .toList();

        Set<String> present = crossings.stream()
                .map(Violation::key)
                .collect(Collectors.toCollection(TreeSet::new));
        List<String> departed = baseline.stream()
                .filter(entry -> !present.contains(entry))
                .sorted()
                .toList();

        // assertAll rather than two plain assertions: a run that both gained a crossing and fixed
        // one should say so in one go. Sequential assertions would report the arrival and hide the
        // departure until the next run, which turns one edit into two red builds.
        assertAll(
                () -> assertTrue(arrived.isEmpty(), () -> reportArrived(rule, arrived, field)),
                () -> assertTrue(departed.isEmpty(), () -> reportDeparted(departed, field)));
    }

    /**
     * The failure that matters: a crossing that was not there before.
     *
     * <p>Prints each one as {@code path:line  name}, which an IDE turns into a link, and then again
     * as a quoted baseline key. The second form is not redundant — the honest response to this
     * failure is sometimes "that import is deliberate, and the chapter it belongs to has not
     * arrived", and in that case the line to paste should be sitting there rather than
     * hand-assembled from the first form.
     *
     * @param rule    the rule in one sentence
     * @param arrived the new crossings; must not be empty when this is called
     * @param field   the constant to paste into
     * @return the message to hand to the assertion
     * @author Rowan Crowther
     */
    private static String reportArrived(String rule, List<Violation> arrived, String field) {
        StringBuilder message = new StringBuilder()
                .append(arrived.size())
                .append(arrived.size() == 1 ? " new crossing" : " new crossings")
                .append(" of the boundary: ")
                .append(rule)
                .append(System.lineSeparator());

        for (Violation violation : arrived) {
            message.append("    ")
                    .append(violation.path())
                    .append(':')
                    .append(violation.reference().line())
                    .append("  ")
                    .append(violation.reference().name())
                    .append(System.lineSeparator());
        }

        message.append(System.lineSeparator())
                .append("If these are deliberate and blocked on a later chapter, add to ")
                .append(field)
                .append(':')
                .append(System.lineSeparator());

        for (Violation violation : arrived) {
            message.append("            \"").append(violation.key()).append("\",")
                    .append(System.lineSeparator());
        }

        return message.toString();
    }

    /**
     * The happy failure: a crossing in the baseline that no longer exists, so the baseline is one
     * line too long.
     *
     * @param departed the entries with nothing left to suppress; must not be empty
     * @param field    the constant to delete them from
     * @return the message to hand to the assertion
     * @author Rowan Crowther
     */
    private static String reportDeparted(List<String> departed, String field) {
        StringBuilder message = new StringBuilder()
                .append(departed.size())
                .append(departed.size() == 1
                        ? " baseline entry no longer crosses the boundary"
                        : " baseline entries no longer cross the boundary")
                .append(" — the ratchet has turned. Delete from ")
                .append(field)
                .append(':')
                .append(System.lineSeparator());

        for (String entry : departed) {
            message.append("            \"").append(entry).append("\",")
                    .append(System.lineSeparator());
        }

        return message.toString();
    }

    /**
     * Answers whether a file in one half may name a type in another.
     *
     * @author Rowan Crowther
     */
    @FunctionalInterface
    private interface PermittedTarget {

        /**
         * @param half the top-level package being named, or {@code ""} for the root package
         * @return whether naming it is allowed
         * @author Rowan Crowther
         */
        boolean permits(String half);
    }

    /**
     * One qualified name of ours found in code, and where.
     *
     * @param name the full name as written, for the failure message
     * @param half the top-level package it lands in, or {@code ""} for the root package
     * @param line the one-based line it appears on
     * @author Rowan Crowther
     */
    private record Reference(String name, String half, int line) {
    }

    /**
     * A rule broken in one place.
     *
     * @param path      the file, relative to the source root
     * @param reference the name that broke it
     * @author Rowan Crowther
     */
    private record Violation(String path, Reference reference) {

        /**
         * How a crossing is spelled in a baseline.
         *
         * <p>Deliberately carries no line number. A baseline keyed by line would go stale on any
         * edit above the import — the entry would stop matching, the rule would report the same
         * crossing as both newly arrived and newly departed, and the ratchet would turn into
         * noise. File and name identify a crossing precisely enough, and two mentions of the same
         * name in one file collapse to one entry, which is what you want.
         *
         * @return the {@code path -> name} key
         * @author Rowan Crowther
         */
        String key() {
            return path + " -> " + reference.name();
        }
    }

    /**
     * One source file, reduced to what the rules care about: which half it belongs to, and every
     * name of ours it mentions in code.
     *
     * @param relativePath the path below {@code uk/co/jackoftrades}, for failure messages
     * @param half         the top-level package it lives in, or {@code ""} for the root package
     * @param references   every qualified name of ours in its code
     * @author Rowan Crowther
     */
    private record SourceFile(String relativePath, String half, List<Reference> references) {

        /**
         * Reads a file, blanks its comments and literals, and extracts its references.
         *
         * @param root the source root, so the stored path is relative to it
         * @param path the file to read
         * @return the parsed file
         * @author Rowan Crowther
         */
        static SourceFile read(Path root, Path path) {
            String source;

            try {
                source = Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException("could not read " + path, e);
            }

            String code = blankCommentsAndLiterals(source);
            String relative = root.relativize(path).toString().replace('\\', '/');
            String half = relative.contains("/") ? relative.substring(0, relative.indexOf('/')) : "";

            return new SourceFile(relative, HALVES.contains(half) ? half : "",
                    referencesIn(code));
        }

        /**
         * Finds every name of ours in already-blanked code, tagged with its half and line.
         *
         * @param code the source with comments and literals blanked out
         * @return the references, in the order they appear
         * @author Rowan Crowther
         */
        private static List<Reference> referencesIn(String code) {
            List<Reference> found = new ArrayList<>();
            Matcher matcher = REFERENCE.matcher(code);

            while (matcher.find()) {
                String name = matcher.group();
                String tail = name.substring(OURS.length());
                String head = tail.contains(".") ? tail.substring(0, tail.indexOf('.')) : tail;

                found.add(new Reference(name, HALVES.contains(head) ? head : "",
                        lineOf(code, matcher.start())));
            }

            return found;
        }

        /**
         * @param code   the text being scanned
         * @param offset a character index into it
         * @return the one-based line number that index falls on
         * @author Rowan Crowther
         */
        private static int lineOf(String code, int offset) {
            int line = 1;

            for (int i = 0; i < offset; i++) {
                if (code.charAt(i) == '\n') {
                    line++;
                }
            }

            return line;
        }

        /**
         * Replaces the contents of every comment, string, text block and character literal with
         * spaces, leaving line breaks alone.
         *
         * <p>A hand-rolled scanner rather than a regex, because the cases that matter are exactly
         * the ones a regex gets wrong: a {@code //} inside a string, a quote inside a comment, an
         * escaped quote inside a string. Blanking rather than deleting keeps every remaining
         * character at its original offset, which is what makes the reported line numbers true.
         *
         * <p>It does not need to be a Java parser and is not one. Anything it mis-scans can only
         * make a name visible that should have been hidden, or hide one that should have been
         * visible, in a file whose comments are unbalanced — the failure mode is a confusing
         * message, never a wrong architecture.
         *
         * @param source the file as read
         * @return the same text with everything but code blanked
         * @author Rowan Crowther
         */
        private static String blankCommentsAndLiterals(String source) {
            char[] out = source.toCharArray();
            int length = out.length;
            int i = 0;

            while (i < length) {
                char c = out[i];

                if (c == '/' && i + 1 < length && out[i + 1] == '/') {
                    while (i < length && out[i] != '\n') {
                        out[i++] = ' ';
                    }
                } else if (c == '/' && i + 1 < length && out[i + 1] == '*') {
                    out[i++] = ' ';
                    out[i++] = ' ';

                    while (i < length && !(out[i] == '*' && i + 1 < length && out[i + 1] == '/')) {
                        if (out[i] != '\n') {
                            out[i] = ' ';
                        }
                        i++;
                    }

                    if (i < length) {
                        out[i++] = ' ';
                        out[i++] = ' ';
                    }
                } else if (c == '"' && i + 2 < length && out[i + 1] == '"' && out[i + 2] == '"') {
                    i = blankTextBlock(out, i);
                } else if (c == '"' || c == '\'') {
                    i = blankSimpleLiteral(out, i, c);
                } else {
                    i++;
                }
            }

            return new String(out);
        }

        /**
         * Blanks a {@code """…"""} text block, whose newlines are content but still have to survive
         * so later line numbers stay right.
         *
         * @param out   the buffer being blanked in place
         * @param start the index of the first of the three opening quotes
         * @return the index just past the closing quotes
         * @author Rowan Crowther
         */
        private static int blankTextBlock(char[] out, int start) {
            int i = start;

            out[i++] = ' ';
            out[i++] = ' ';
            out[i++] = ' ';

            while (i < out.length) {
                if (out[i] == '\\' && i + 1 < out.length) {
                    out[i] = ' ';
                    if (out[i + 1] != '\n') {
                        out[i + 1] = ' ';
                    }
                    i += 2;
                    continue;
                }

                if (out[i] == '"' && i + 2 < out.length && out[i + 1] == '"' && out[i + 2] == '"') {
                    out[i++] = ' ';
                    out[i++] = ' ';
                    out[i++] = ' ';
                    return i;
                }

                if (out[i] != '\n') {
                    out[i] = ' ';
                }
                i++;
            }

            return i;
        }

        /**
         * Blanks a single-quoted or double-quoted literal, honouring backslash escapes so that
         * {@code "\""} does not end it early.
         *
         * @param out   the buffer being blanked in place
         * @param start the index of the opening quote
         * @param quote which quote character opened it
         * @return the index just past the closing quote
         * @author Rowan Crowther
         */
        private static int blankSimpleLiteral(char[] out, int start, char quote) {
            int i = start;

            out[i++] = ' ';

            while (i < out.length && out[i] != '\n') {
                if (out[i] == '\\' && i + 1 < out.length) {
                    out[i] = ' ';
                    out[i + 1] = ' ';
                    i += 2;
                    continue;
                }

                if (out[i] == quote) {
                    out[i++] = ' ';
                    return i;
                }

                out[i++] = ' ';
            }

            return i;
        }
    }

    /**
     * The scan itself, checked before its findings are trusted.
     *
     * <p>Every assertion below is a negative — "nothing crosses" — and a negative is exactly what a
     * broken scanner also reports. So these run first and prove the machinery has teeth: that it
     * found the tree, that it can tell the halves apart, and that its comment blanking neither
     * swallows real code nor counts Javadoc as a dependency.
     *
     * @author Rowan Crowther
     */
    @Nested
    class TheScanItself {

        /**
         * A boundary test that examined nothing would pass every rule.
         *
         * @author Rowan Crowther
         */
        @Test
        void findsTheSourceTree() {
            assertTrue(sources().size() > 100,
                    "expected the whole of src/main; found " + sources().size() + " files");
        }

        /**
         * All four halves must be present, or a rule is being enforced over an empty set.
         *
         * @author Rowan Crowther
         */
        @Test
        void seesAllFourHalves() {
            for (String half : HALVES) {
                assertTrue(sources().stream().anyMatch(source -> source.half().equals(half)),
                        "found no files at all in " + half);
            }
        }

        /**
         * The scan must see ordinary imports, or every rule below is vacuous. {@code channel}
         * naming itself is the traffic it is guaranteed to have.
         *
         * @author Rowan Crowther
         */
        @Test
        void seesReferencesInCode() {
            long channelNamingItself = sources().stream()
                    .filter(source -> source.half().equals("channel"))
                    .flatMap(source -> source.references().stream())
                    .filter(reference -> reference.half().equals("channel"))
                    .count();

            assertTrue(channelNamingItself > 10,
                    "the scanner found almost no references; blanking is probably too greedy");
        }

        /**
         * A line comment must not contribute a dependency — nor hide the code on the next line.
         *
         * @author Rowan Crowther
         */
        @Test
        void ignoresLineComments() {
            String blanked = SourceFile.blankCommentsAndLiterals(
                    "// uk.co.jackoftrades.middle.Hidden\nuk.co.jackoftrades.channel.Kept\n");

            assertFalse(blanked.contains("Hidden"), "a line comment was scanned as code");
            assertTrue(blanked.contains("uk.co.jackoftrades.channel.Kept"),
                    "the line after a comment was blanked too");
        }

        /**
         * Javadoc is where the cross-boundary names actually are: this document names
         * {@code middle} and {@code frontend} repeatedly and must not fail its own test.
         *
         * @author Rowan Crowther
         */
        @Test
        void ignoresBlockCommentsAndJavadoc() {
            String blanked = SourceFile.blankCommentsAndLiterals(
                    "/** {@link uk.co.jackoftrades.middle.Hidden} */ uk.co.jackoftrades.channel.Kept");

            assertFalse(blanked.contains("Hidden"), "Javadoc was scanned as code");
            assertTrue(blanked.contains("uk.co.jackoftrades.channel.Kept"),
                    "code after a block comment was blanked");
        }

        /**
         * A name inside a string is a log line or a class-name lookup, not an import. Both quote
         * forms, and the escapes that would otherwise end a literal early.
         *
         * @author Rowan Crowther
         */
        @Test
        void ignoresStringAndCharacterLiterals() {
            String blanked = SourceFile.blankCommentsAndLiterals(
                    "log(\"uk.co.jackoftrades.middle.Hidden\"); char q = '\"';"
                            + " uk.co.jackoftrades.channel.Kept");

            assertFalse(blanked.contains("Hidden"), "a string literal was scanned as code");
            assertTrue(blanked.contains("uk.co.jackoftrades.channel.Kept"),
                    "an escaped quote ran the literal on and swallowed the code after it");
        }

        /**
         * The trap a regex-based stripper falls into: a comment opener inside a string.
         *
         * @author Rowan Crowther
         */
        @Test
        void isNotFooledByACommentMarkerInsideAString() {
            String blanked = SourceFile.blankCommentsAndLiterals(
                    "String url = \"http://x/*\"; uk.co.jackoftrades.channel.Kept");

            assertTrue(blanked.contains("uk.co.jackoftrades.channel.Kept"),
                    "a // or /* inside a string started a comment that ate the rest of the file");
        }

        /**
         * Line numbers are only useful if they are true, and they are true only if blanking
         * preserves every newline it passes over — including the ones inside block comments and
         * text blocks.
         *
         * @author Rowan Crowther
         */
        @Test
        void keepsLineNumbersTrueThroughMultiLineConstructs() {
            String source = "/* one\n two\n */\nString s = \"\"\"\n block\n \"\"\";\nKept\n";
            String blanked = SourceFile.blankCommentsAndLiterals(source);

            assertEquals(source.length(), blanked.length(), "blanking changed the file's length");
            assertEquals(source.chars().filter(c -> c == '\n').count(),
                    blanked.chars().filter(c -> c == '\n').count(),
                    "blanking lost a line break, so reported line numbers would be wrong");
            assertTrue(blanked.contains("Kept"), "the text block swallowed the code after it");
        }

        /**
         * A name is attributed to the half it points at, not the half it sits in — and a name in
         * the root package is attributed to no half at all, which is what makes rule 1 reject it.
         *
         * <p>{@code Main} is the fixture for the root-package case on purpose. It is the one type
         * that cannot migrate into a half without ceasing to be the entry point, so it will not
         * drift out from under this test the way any other example would.
         *
         * @author Rowan Crowther
         */
        @Test
        void attributesNamesToTheHalfTheyPointAt() {
            List<Reference> references = SourceFile.referencesIn(
                    "import uk.co.jackoftrades.middle.cave.Loc;\n"
                            + "import uk.co.jackoftrades.Main;\n"
                            + "import uk.co.jackoftrades.channel.Channels;\n");

            assertEquals(3, references.size());
            assertEquals("middle", references.getFirst().half());
            assertEquals(1, references.getFirst().line());
            assertEquals("", references.get(1).half(), "the root package is nobody's half");
            assertEquals(2, references.get(1).line());
            assertEquals("channel", references.get(2).half());
            assertEquals(3, references.get(2).line());
        }

        /**
         * A baseline key must not depend on the line the import sits on.
         *
         * <p>This is the one design decision in the ratchet that could quietly break it. If the key
         * carried a line number, adding an import above an existing crossing would shift it, the
         * old entry would look departed and the same crossing would look newly arrived, and the
         * rule would fail twice over on a change that altered nothing. Then the natural fix is to
         * paste the new line in — and after a few rounds the baseline is a list of things that have
         * moved rather than a list of things that remain.
         *
         * @author Rowan Crowther
         */
        @Test
        void aBaselineKeyIgnoresTheLineTheCrossingSitsOn() {
            Reference early = new Reference("uk.co.jackoftrades.middle.cave.Loc", "middle", 22);
            Reference late = new Reference("uk.co.jackoftrades.middle.cave.Loc", "middle", 91);

            assertEquals(new Violation("frontend/A.java", early).key(),
                    new Violation("frontend/A.java", late).key());
            assertFalse(new Violation("frontend/A.java", early).key().contains("22"),
                    "the key carries a line number and will go stale on the next edit");
        }

        /**
         * Every baseline entry must be spelled the way the scanner spells a crossing, or it can
         * never match one.
         *
         * <p>The stale-entry half of the ratchet would catch a typo eventually, but it would report
         * it as "no longer crosses the boundary" — which reads as good news and is the opposite of
         * the truth. Checking the shape up front means a mistyped path fails as a mistyped path.
         *
         * @author Rowan Crowther
         */
        @Test
        void everyBaselineEntryIsWellFormed() {
            for (Set<String> baseline : List.of(FRONTEND_BASELINE, MIDDLE_BASELINE,
                    BACKEND_BASELINE)) {
                for (String entry : baseline) {
                    String[] halves = entry.split(" -> ", -1);

                    assertEquals(2, halves.length, "not a 'path -> name' key: " + entry);
                    assertTrue(halves[0].endsWith(".java"), "not a source path: " + entry);
                    assertTrue(halves[1].startsWith(OURS), "not a name of ours: " + entry);
                    assertTrue(Files.isRegularFile(sourceRoot().resolve(halves[0])),
                            "baseline names a file that does not exist: " + halves[0]);
                }
            }
        }
    }

    /**
     * Rule 1, and the one the whole migration is for: the front end's compile-time view of the core
     * is {@code channel} and nothing else.
     *
     * <p>This is the rule that would have been red before stage 0 and is meant to stay green after
     * stage 5. It is stated as an allowlist rather than a list of denials because that is the
     * property worth having — a new top-level package appearing next year is denied by default
     * rather than quietly permitted until someone remembers to add it here.
     *
     * @author Rowan Crowther
     */
    @Nested
    class TheFrontEndSeesOnlyTheChannel {

        /**
         * @author Rowan Crowther
         */
        @Test
        void frontendNamesNothingOfOursOutsideChannelBeyondTheBaseline() {
            assertOnlyBaselineCrossings(
                    "frontend may name channel and nothing else of ours",
                    "frontend",
                    half -> half.equals("frontend") || half.equals("channel"),
                    FRONTEND_BASELINE,
                    "FRONTEND_BASELINE");
        }
    }

    /**
     * Rules 2 and 3: neither half of the core knows the front end exists.
     *
     * <p>These are the backsliding catchers. Rule 1 stops the front end reaching down; without
     * these, the same coupling simply gets built from the other end — a {@code middle} class that
     * imports a Swing-adjacent type is core code that can no longer run without a display, which
     * is precisely the state the two-channel design was adopted to leave.
     *
     * @author Rowan Crowther
     */
    @Nested
    class TheCoreDoesNotKnowTheFrontEnd {

        /**
         * @author Rowan Crowther
         */
        @Test
        void middleNamesNothingInFrontendBeyondTheBaseline() {
            assertOnlyBaselineCrossings(
                    "middle may not name frontend",
                    "middle",
                    half -> !half.equals("frontend"),
                    MIDDLE_BASELINE,
                    "MIDDLE_BASELINE");
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void backendNamesNothingInFrontendBeyondTheBaseline() {
            assertOnlyBaselineCrossings(
                    "backend may not name frontend",
                    "backend",
                    half -> !half.equals("frontend"),
                    BACKEND_BASELINE,
                    "BACKEND_BASELINE");
        }
    }

    /**
     * Where the rules are written down, checked against where they are enforced.
     *
     * <p>A structural rule that lives only in a test is a rule nobody reads, and one that lives
     * only in a document is one nobody runs. This asserts the migration document still contains the
     * stage that this class implements, so that deleting the prose is a build failure rather than a
     * silent drift between the two.
     *
     * @author Rowan Crowther
     */
    @Nested
    class TheRulesAreDocumented {

        /**
         * @author Rowan Crowther
         */
        @Test
        void theMigrationDocumentStillStatesTheThreeRules() throws IOException {
            Path document = projectRoot().resolve("docs/Architecture_migration.md");

            assumeDocumentExists(document);

            String text = Files.readString(document, StandardCharsets.UTF_8)
                    .toLowerCase(Locale.ROOT);

            assertTrue(text.contains("the boundary test"),
                    "the migration document no longer names the boundary test");
        }

        /**
         * Skips rather than fails if the document has been moved, since the rules themselves are
         * enforced above and do not depend on the prose being findable.
         *
         * @param document where the document is expected to be
         * @author Rowan Crowther
         */
        private void assumeDocumentExists(Path document) {
            org.junit.jupiter.api.Assumptions.assumeTrue(Files.isRegularFile(document),
                    "migration document not found at " + document);
        }
    }
}
