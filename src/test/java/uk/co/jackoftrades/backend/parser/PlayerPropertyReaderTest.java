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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerProperty;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the player-property pipeline: text file → ANTLR
 * lexer/parser ({@link uk.co.jackoftrades.backend.parser.grammars.playerproperty.PlayerPropertyLexer}
 * / {@code PlayerPropertyGrammar}) → {@link PlayerPropertyReader} →
 * {@link uk.co.jackoftrades.backend.parser.playerproperty.PlayerPropertyAssembler}
 * → resolved {@link PlayerProperty} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>The happy-path test runs against the real shipped
 * {@code lib/gamedata/player_property.txt}; a clean load of all 44 records is
 * itself the assertion that every {@code type}, {@code code} flag and
 * {@code bindui} reference resolved. It also spot-checks the three record
 * shapes the format supports (a bare {@code player} flag, an {@code object}
 * flag carrying a resolved {@code bindui}, and an {@code element} record with a
 * {@code value}), and pins a <em>digit-bearing</em> code ({@code BRAVERY_30}) so
 * the {@code FLAG} lexer rule is exercised on a flag name containing digits.
 *
 * <p>The remaining tests inject one defect each to exercise both error channels:
 * a hard grammar error (missing {@code record-count}) fails closed with an empty
 * list; the soft errors (record-count mismatch, an unresolvable {@code code},
 * an unresolvable {@code bindui} target) are reported while the offending record
 * still survives, per the assembler's best-effort contract. A final test puts
 * two <em>different</em> soft-error types in a single record and confirms both
 * are reported together.
 *
 * <p>The assembler resolves {@code bindui} targets against the {@code GameConstants}
 * UI-entry registry, so {@link #seedRegistries()} loads the real renderer, base
 * and entry files and injects them into {@code GameConstants}' private static
 * fields via reflection, independent of full-game init order.
 *
 * @author Rowan Crowther
 */
class PlayerPropertyReaderTest {

    private static final String REAL_FILE = "lib/gamedata/player_property.txt";

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seedRegistries() throws Exception {
        // The UI-entry pipeline resolves bottom-up: renderers, then bases (which
        // resolve renderers), then entries (which resolve both). The player-property
        // assembler's bindui look-ups need the finished entry list seeded.
        List<UIEntryRenderer> renderers = new UIEntryRendererReader()
                .parseWithResults("lib/gamedata/ui_entry_renderer.txt").items();
        setStatic("uiEntryRenderers", renderers);
        List<UIEntryBase> bases = new UIEntryBaseReader()
                .parseWithResults("lib/gamedata/ui_entry_base.txt").items();
        setStatic("uiEntryBases", bases);
        List<UIEntry> entries = new UIEntryReader()
                .parseWithResults("lib/gamedata/ui_entry.txt").items();
        setStatic("uiEntries", entries);
    }

    private static void setStatic(String field, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(null, value);
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    private static PlayerProperty byName(List<PlayerProperty> items, String name) {
        return items.stream().filter(p -> name.equals(p.getName())).findFirst()
                .orElseThrow(() -> new AssertionError("no PlayerProperty named " + name));
    }

    // ---- happy path -------------------------------------------------------------------------

    @Test
    void cleanLoadOfAllThreeRecordShapesResolvesWithoutErrors() throws IOException {
        // One record of each type the format supports, each using only fields that
        // resolve cleanly: a player flag with a digit-bearing code and desc (no bindui);
        // an object flag with a bindui to a generic (untagged) UI entry that exists; and
        // an element record with a value and a generic bindui. Every bindui target here
        // is one this pipeline can actually resolve, so this is a genuinely clean load.
        String path = tempFile("clean.txt", """
                record-count:3
                type:player
                code:BRAVERY_30
                name:Relentless
                desc:You become immune to fear at level 30.
                type:object
                code:PROT_FEAR
                bindui:pfear_ui_compact_0:0:1
                name:Fear Immunity
                type:element
                bindui:resist_ui_compact_0:0:1
                name:Resistance
                value:1
                """);

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(3, result.items().size());

        // Player flag with a digit-bearing code: exercises the FLAG lexer rule on a name
        // containing digits, and confirms it resolves through to the enum constant.
        PlayerProperty relentless = byName(result.items(), "Relentless");
        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER, relentless.getPlayerPropertyType());
        assertEquals(PlayerFlag.PF_BRAVERY_30, relentless.getpCode());
        assertEquals("You become immune to fear at level 30.", relentless.getDescription());
        assertTrue(relentless.getEntries().isEmpty());

        // Object flag with a resolvable bindui: "pfear_ui_compact_0:0:1" -> not aux, value 1,
        // target resolves to a real UI entry from the seeded registry.
        PlayerProperty fear = byName(result.items(), "Fear Immunity");
        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT, fear.getPlayerPropertyType());
        assertEquals(ObjectFlag.OF_PROT_FEAR, fear.getoCode());
        assertEquals(1, fear.getEntries().size());
        PlayerProperty.BindUI bind = fear.getEntries().get(0);
        assertNotNull(bind.uiEntry(), "bindui target should resolve to a real UI entry");
        assertFalse(bind.aux());
        assertFalse(bind.special());
        assertEquals(1, bind.value());

        // Element record: value:1 -> RESISTANCE, no code, generic bindui resolves.
        PlayerProperty resistance = byName(result.items(), "Resistance");
        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT, resistance.getPlayerPropertyType());
        assertEquals(PlayerProperty.PlayerPropertyValue.RESISTANCE, resistance.getValue());
        assertNotNull(resistance.getEntries().get(0).uiEntry());
    }

    /**
     * The real {@code player_property.txt} loads cleanly: all 44 records resolve with no soft
     * errors. In particular the five {@code type:object} stat-sustain records bind to
     * {@code stat_mod_ui_compact_0<STR..CON>}, which now resolve because {@code UIEntryAssembler}
     * expands the single {@code stat_mod_ui_compact_0} + {@code parameter:stat} entry into one
     * tagged entry per stat ({@code <STR>}..{@code <CON>}), mirroring the C loader's
     * parameter expansion in {@code ui-entry.c}. (Previously only the generic tagless entry
     * existed, so these five failed the exact-name lookup and were reported as soft errors -
     * the gap this test used to pin.)
     */
    @Test
    void realFileLoadsAll44RecordsWithNoErrors() throws IOException {
        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(REAL_FILE);

        assertEquals(44, result.items().size());
        assertEquals(List.of(), result.errors(), () -> "expected a clean load but got: " + result.errors());

        // The stat-sustain bindui now resolves to the expansion-generated per-stat entry.
        PlayerProperty sustStr = byName(result.items(), "Sustain Strength");
        UIEntry statEntry = sustStr.getEntries().get(0).uiEntry();
        assertNotNull(statEntry, "SUST_STR bindui should resolve to a UIEntry");
        assertEquals("stat_mod_ui_compact_0<STR>", statEntry.getName());
    }

    // ---- hard error (fail-closed: empty list) -----------------------------------------------

    @Test
    void missingRecordCountHeaderFailsClosed() throws IOException {
        // No record-count directive -> the file rule can't match -> grammar error via ParseErrors.
        String path = tempFile("no-header.txt", "type:player\ncode:ROCK\nname:Rock\n");

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- soft errors (partial results survive) ----------------------------------------------

    @Test
    void recordCountMismatchIsLoggedButValidRecordSurvives() throws IOException {
        String path = tempFile("bad-count.txt", "record-count:5\ntype:player\ncode:ROCK\nname:Rock\n");

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertEquals(PlayerFlag.PF_ROCK, result.items().get(0).getpCode());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void unresolvableCodeIsLoggedAndRecordSkipped() throws IOException {
        // "OF_NOT_A_REAL_FLAG" is not an ObjectFlag constant -> illegal code. For type:object (and
        // type:player) the code is the property's flag identity, so an unresolvable code makes the
        // record meaningless and the assembler skips it entirely, logging the error.
        String path = tempFile("bad-code.txt",
                "record-count:1\ntype:object\ncode:NOT_A_REAL_FLAG\nname:Bogus\n");

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertEquals(0, result.items().size());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("illegal code") && e.contains("NOT_A_REAL_FLAG")),
                result.errors()::toString);
    }

    @Test
    void unresolvableBinduiTargetIsLoggedButRecordSurvives() throws IOException {
        // The bindui names a UI entry that isn't in the seeded registry -> illegal UIEntry. Unlike a
        // bad code, a bad bindui is not the property's identity: the offending binding is dropped
        // (mirroring C's silent discard of a failed bind) while the record itself still loads.
        String path = tempFile("bad-bindui.txt",
                "record-count:1\ntype:player\ncode:ROCK\nbindui:no_such_ui_entry:0:1\nname:Rock\n");

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).getEntries().isEmpty(),
                "the unresolvable binding should be dropped, leaving no bindings");
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("illegal UIEntry")),
                result.errors()::toString);
    }

    // ---- two different error types in one record --------------------------------------------

    @Test
    void twoDifferentSoftErrorsAreBothReported() throws IOException {
        // One record with two bad bindui lines: the first names a UI entry that isn't seeded
        // (illegal UIEntry), the second has a non-numeric value (illegal integer value). Both
        // bindui validations are per-binding skips, so both fire and the record still survives
        // (with no bindings). A valid code (ROCK) is used so the record is not skipped earlier.
        String path = tempFile("two-errors.txt",
                "record-count:1\ntype:player\ncode:ROCK\n"
                        + "bindui:no_such_ui_entry:0:1\nbindui:also_bad:0:notanumber\nname:Rock\n");

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).getEntries().isEmpty(),
                "both unresolvable bindings should be dropped");
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("illegal UIEntry")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("illegal integer value")),
                result.errors()::toString);
    }
}
