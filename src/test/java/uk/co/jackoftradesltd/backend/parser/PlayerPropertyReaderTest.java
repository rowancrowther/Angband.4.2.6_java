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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the player-property pipeline: text file → ANTLR
 * lexer/parser ({@link uk.co.jackoftradesltd.backend.parser.grammars.playerproperty.PlayerPropertyLexer}
 * / {@code PlayerPropertyGrammar}) → {@link PlayerPropertyReader} →
 * {@link uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyAssembler}
 * → resolved {@link PlayerProperty} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>The happy-path test runs against the real shipped
 * {@code lib/gamedata/player_property.txt}; a clean load with no soft errors is
 * itself the assertion that every {@code type}, {@code code} flag and
 * {@code bindui} reference resolved. A {@code type:element} record is a template, not a
 * finished property - {@code PlayerPropertyAssembler.spreadPlayerPropertyOut} expands it into
 * one {@link PlayerProperty} per real {@link ElementEnum} (C: {@code finish_parse_player_prop},
 * {@code init.c:1332-1352}), so the file's 44 declared records yield more than 44 assembled
 * properties; see {@link #ELEMENT_COUNT} and the exact-count assertion below. That expansion
 * resolves each element's display text through {@link WorldRegistry#lookupProjectionByCode}, so
 * {@link #seed()} loads the real {@code projection.txt} before any test runs. It also spot-checks
 * the three record shapes the format supports (a bare {@code player} flag, an {@code object}
 * flag carrying a resolved {@code bindui}, and an {@code element} record with a
 * {@code value}), and pins a <em>digit-bearing</em> code ({@code BRAVERY_30}) so
 * the {@code FLAG} lexer rule is exercised on a flag name containing digits.
 *
 * <p>The remaining tests inject one defect each to exercise both error channels:
 * a hard grammar error (missing {@code record-count}) fails closed with an empty
 * list; the soft errors (record-count mismatch, an unresolvable {@code code})
 * are reported while the offending record still survives, per the assembler's
 * best-effort contract. A final test puts two <em>different</em> soft-error
 * types in a single load and confirms both are reported together.
 *
 * <p>The assembler no longer resolves {@code bindui} targets against a UI-entry
 * registry: it stores the raw {@code name + tag} string verbatim, leaving the
 * look-up to the UI layer (via {@code UIRegistryLoader}) at the point the entry
 * is actually needed. A {@code bindui} naming a UI entry that does not exist is
 * therefore no longer an error at this stage.
 *
 * @author Rowan Crowther
 */
class PlayerPropertyReaderTest {

    private static final String REAL_FILE = "lib/gamedata/player_property.txt";
    private static final String PROJECTION_FILE = "lib/gamedata/projection.txt";

    /**
     * The number of real elements a {@code type:element} record expands into - every
     * {@link ElementEnum} constant except the {@code NONE} placeholder and the {@code MAX}
     * sentinel, neither of which {@code PlayerPropertyAssembler} expands (it skips them the same
     * way C's loop bound excludes the list's trailing NULL).
     */
    private static final int ELEMENT_COUNT = (int) Arrays.stream(ElementEnum.values())
            .filter(e -> e != ElementEnum.ELEM_NONE && e != ElementEnum.ELEM_MAX)
            .count();

    private static Object savedProjections;

    @TempDir
    Path tempDir;

    /**
     * {@code type:element} expansion resolves each element's display text through
     * {@link WorldRegistry#lookupProjectionByCode}, so the projection table has to be populated
     * before any element property can be assembled - mirrors the seeding
     * {@link BlowEffectReaderTest} does for the same registry.
     */
    @BeforeAll
    static void seed() throws Exception {
        List<Projection> projections = new ProjectionReader().parseWithResults(PROJECTION_FILE).items();
        savedProjections = setStatic("projections", projections);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("projections", savedProjections);
    }

    /**
     * Overwrite a private static on the owning registry (resolved via {@link RegistrySeeding}),
     * returning the previous value so {@link #restore()} can put the real table back and leave
     * the suite's shared statics as they were found.
     */
    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
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
        // The element record expands into one PlayerProperty per real ElementEnum
        // (PlayerPropertyAssembler.spreadPlayerPropertyOut), so the three records yield
        // 2 + ELEMENT_COUNT results, not 3.
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
                desc:You resist
                value:1
                """);

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2 + ELEMENT_COUNT, result.items().size());

        // Player flag with a digit-bearing code: exercises the FLAG lexer rule on a name
        // containing digits, and confirms it resolves through to the enum constant.
        PlayerProperty relentless = byName(result.items(), "Relentless");
        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER, relentless.getPlayerPropertyType());
        assertEquals(PlayerFlag.PF_BRAVERY_30, relentless.getpCode());
        assertEquals("You become immune to fear at level 30.", relentless.getDescription());
        assertTrue(relentless.getEntries().isEmpty());

        // Object flag with a bindui: "pfear_ui_compact_0:0:1" -> not aux, value 1, name+tag
        // captured verbatim (no tag here, so just the bare name).
        PlayerProperty fear = byName(result.items(), "Fear Immunity");
        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT, fear.getPlayerPropertyType());
        assertEquals(ObjectFlag.OF_PROT_FEAR, fear.getoCode());
        assertEquals(1, fear.getEntries().size());
        PlayerProperty.BindUI bind = fear.getEntries().get(0);
        assertEquals("pfear_ui_compact_0", bind.uiEntry());
        assertFalse(bind.aux());
        assertFalse(bind.special());
        assertEquals(1, bind.value());

        // Element record: value:1 -> RESISTANCE, expanded per element (C:
        // finish_parse_player_prop, init.c:1332-1352). Spot-check the COLD expansion: name/desc
        // built from the projection's own name ("cold"), capitalised only on its first letter,
        // plus this record's own name/desc text - "Cold Resistance" / "You resist cold.",
        // matching C's format("%s %s", capitalised, ability.name) /
        // format("%s %s.", ability.desc, name). The bindui is re-suffixed with the element's own
        // code, <COLD>, not the projection's display name.
        PlayerProperty coldResistance = byName(result.items(), "Cold Resistance");
        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT, coldResistance.getPlayerPropertyType());
        assertEquals(ElementEnum.ELEM_COLD, coldResistance.geteCode());
        assertEquals(PlayerProperty.PlayerPropertyValue.RESISTANCE, coldResistance.getValue());
        assertEquals("You resist cold.", coldResistance.getDescription());
        assertEquals(1, coldResistance.getEntries().size());
        assertEquals("resist_ui_compact_0<COLD>", coldResistance.getEntries().get(0).uiEntry());
    }

    /**
     * The real {@code player_property.txt} declares 44 records and loads with no soft errors.
     * Its three {@code type:element} records (Resistance/Immunity/Vulnerability) each expand into
     * one {@link PlayerProperty} per real {@link ElementEnum}, so the assembled count is
     * {@code 44 - 3 + 3 * ELEMENT_COUNT}, not 44. In particular the five {@code type:object}
     * stat-sustain records bind to {@code stat_mod_ui_compact_0<STR..CON>}: since the assembler
     * no longer looks the name up against a registry, the {@code <TAG>}-decorated name is
     * captured verbatim regardless of whether a matching UI entry exists.
     */
    @Test
    void realFileLoadsCleanlyAndExpandsElementRecords() throws IOException {
        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(REAL_FILE);

        assertEquals(44 - 3 + 3 * ELEMENT_COUNT, result.items().size());
        assertEquals(List.of(), result.errors(), () -> "expected a clean load but got: " + result.errors());

        // The stat-sustain bindui's name+tag is captured verbatim.
        PlayerProperty sustStr = byName(result.items(), "Sustain Strength");
        assertEquals("stat_mod_ui_compact_0<STR>", sustStr.getEntries().get(0).uiEntry());

        // Each of the three element-type records expands correctly and independently -
        // spot-checked against values derived from C's finish_parse_player_prop and
        // player_property.txt's own name:/desc: text, not from the Java implementation.
        PlayerProperty coldResistance = byName(result.items(), "Cold Resistance");
        assertEquals(PlayerProperty.PlayerPropertyValue.RESISTANCE, coldResistance.getValue());
        assertEquals("You resist cold.", coldResistance.getDescription());

        PlayerProperty fireImmunity = byName(result.items(), "Fire Immunity");
        assertEquals(PlayerProperty.PlayerPropertyValue.IMMUNITY, fireImmunity.getValue());
        assertEquals("You are immune to fire.", fireImmunity.getDescription());

        PlayerProperty poisonVulnerability = byName(result.items(), "Poison Vulnerability");
        assertEquals(PlayerProperty.PlayerPropertyValue.VULNERABILITY, poisonVulnerability.getValue());
        assertEquals("You are vulnerable to poison.", poisonVulnerability.getDescription());
    }

    /**
     * {@code spreadPlayerPropertyOut} contributes nothing for an element whose
     * {@link ElementEnum#getProjectionEnum()} cannot be resolved against the loaded projection
     * table - the branch C cannot take, since its parser refuses to load {@code projection.txt}
     * unless the two lists align position for position. Simulated here by seeding a projection
     * table with the {@code COLD} entry removed: the element-type record must still expand into a
     * property for every other real element, but silently skip {@code ELEM_COLD} rather than
     * failing the whole load.
     */
    @Test
    void elementWithNoMatchingProjectionIsSkippedNotFailed() throws Exception {
        Field field = RegistrySeeding.resolve("projections");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Projection> withCold = (List<Projection>) field.get(null);
        List<Projection> withoutCold = withCold.stream()
                .filter(p -> p.getProjection() != ElementEnum.ELEM_COLD.getProjectionEnum())
                .toList();
        Object saved = setStatic("projections", withoutCold);
        try {
            String path = tempFile("element-only.txt", """
                    record-count:1
                    type:element
                    bindui:resist_ui_compact_0:0:1
                    name:Resistance
                    desc:You resist
                    value:1
                    """);

            ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(ELEMENT_COUNT - 1, result.items().size());
            assertTrue(result.items().stream().noneMatch(p -> "Cold Resistance".equals(p.getName())));
            assertTrue(result.items().stream().anyMatch(p -> "Fire Resistance".equals(p.getName())));
        } finally {
            setStatic("projections", saved);
        }
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
    void binduiIsCapturedVerbatimEvenWhenNoMatchingUiEntryExists() throws IOException {
        // The assembler no longer validates a bindui's target against a UI-entry registry - it
        // just stores the raw name+tag string for the UI layer to resolve later via
        // UIRegistryLoader. A name with no corresponding UI entry is not an error at this stage.
        String path = tempFile("no-such-entry.txt",
                "record-count:1\ntype:player\ncode:ROCK\nbindui:no_such_ui_entry:0:1\nname:Rock\n");

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(1, result.items().get(0).getEntries().size());
        assertEquals("no_such_ui_entry", result.items().get(0).getEntries().get(0).uiEntry());
    }

    // ---- two different error types in one load -----------------------------------------------

    @Test
    void twoDifferentSoftErrorsAreBothReported() throws IOException {
        // Two independent soft errors: a record-count mismatch (reader-level) and a malformed
        // bindui integer value (assembler-level, per-binding). Both are reported and the record
        // still survives, minus the dropped binding.
        String path = tempFile("two-errors.txt",
                "record-count:5\ntype:player\ncode:ROCK\nbindui:also_bad:0:notanumber\nname:Rock\n");

        ParseResult<PlayerProperty> result = new PlayerPropertyReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).getEntries().isEmpty(),
                "the malformed binding should be dropped");
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("illegal integer value")),
                result.errors()::toString);
    }
}
