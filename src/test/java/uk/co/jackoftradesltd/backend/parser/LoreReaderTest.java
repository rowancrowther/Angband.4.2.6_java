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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.MonsterLore;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stub tests for {@link LoreReader}, which reads {@code lib/user/lore.txt} — the player's
 * per-save monster-knowledge record, not a {@code lib/gamedata} file. The port of C's
 * {@code lore_parser} ({@code [C] src/mon-init.c:2646}).
 *
 * <p><strong>Why these are stubs.</strong> The reader cannot be tested for what it is meant to do
 * yet, because two things are broken upstream of it and both have to be fixed together:
 *
 * <ol>
 *   <li>{@code LoreGrammar.g4} does not generate. ANTLR rejects it with
 *       {@code error(63): unknown attribute reference Lore in $Lore} — the {@code monsterLore}
 *       rule's {@code @after} block writes {@code race.setLore($Lore)} with a capital L, where the
 *       rule's return value is {@code $lore}.</li>
 *   <li>The committed {@code backend/parser/lore/LoreGrammar.java} therefore predates the current
 *       grammar. Its {@code file} rule declares {@code loreEntries} but has no {@code @init} and no
 *       action adding each {@code monsterLore} match to it, so the list is never built and
 *       {@link LoreReader#parse} hands back {@code null} — in defiance of its {@code @NotNull}.</li>
 * </ol>
 *
 * <p>{@link #parseCurrentlyReturnsNull()} pins that state deliberately, so fixing the grammar makes
 * this class fail rather than pass quietly; at that point the {@link Disabled} tests below are the
 * spec to enable, and the {@code @Disabled} annotations come off.
 *
 * <p>Two further things will want attention at the same time, both flagged in the grammar's own
 * comments: {@link LoreReader} still hand-rolls the ANTLR plumbing instead of delegating to
 * {@link GrammarDriver} (so it has no {@code parseWithResults} and no soft-error channel), and
 * {@code GameConstants} keeps its {@code loadMonsterLore()} commented out, so nothing calls the
 * reader at start-up yet.
 *
 * <p>The active tests below are the ones that hold regardless: the IO contract, and the fact that
 * the grammar resolves monster names against {@link uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry}
 * mid-parse. They seed the registry themselves rather than running
 * {@link uk.co.jackoftradesltd.middle.game.globals.GameConstants#init()}, so they stay hermetic and
 * order-independent.
 *
 * @author Rowan Crowther
 */
class LoreReaderTest {

    /**
     * The shipped save-side file, relative to the Gradle working directory (project root). Note it
     * lives under {@code lib/user}, not {@code lib/gamedata}.
     */
    private static final String REAL_FILE = "lib/user/lore.txt";

    /**
     * The number of {@code name:} records in {@link #REAL_FILE}. Unlike the gamedata files, lore.txt
     * carries no {@code record-count:} header for the reader to check this against.
     */
    private static final int EXPECTED_RECORDS = 10;

    /**
     * A minimal record: the two directives that need nothing but the monster name looked up.
     */
    private static final String ONE_RECORD = String.join("\n",
            "name:large white snake",
            "counts:1:0:1:0:0:0:0",
            "");

    @TempDir
    Path tempDir;

    private Field monsterRaces;
    private Object savedRaces;
    private Field blowMethods;
    private Object savedBlowMethods;

    @SuppressWarnings("unchecked")
    private static <T> T field(MonsterLore target, String name) throws Exception {
        Field f = MonsterLore.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    /**
     * The grammar calls {@code MonsterRegistry.lookupMonsterRace} from inside its {@code name}
     * action, and that throws outright when the registry has never been loaded. Seeding an empty
     * list is enough to get past it — the lookup then simply finds nothing and returns null, which
     * the (stale) generated parser does not dereference.
     *
     * <p>{@code blowMethods} is pushed the other way, back to its unloaded {@code null}, so that
     * {@link #theRealFileAlsoNeedsTheCombatTablesLoaded()} does not depend on whether some earlier
     * test class in the same JVM happened to leave the combat tables populated.
     */
    @BeforeEach
    void seedTheRegistriesTheGrammarReachesInto() throws Exception {
        monsterRaces = RegistrySeeding.resolve("monsterRaces");
        monsterRaces.setAccessible(true);
        savedRaces = monsterRaces.get(null);
        monsterRaces.set(null, List.of());

        blowMethods = RegistrySeeding.resolve("blowMethods");
        blowMethods.setAccessible(true);
        savedBlowMethods = blowMethods.get(null);
        blowMethods.set(null, null);
    }

    @AfterEach
    void restoreTheRegistries() throws Exception {
        monsterRaces.set(null, savedRaces);
        blowMethods.set(null, savedBlowMethods);
    }

    /**
     * Writes {@code content} to a file in the temp dir and returns its absolute path.
     */
    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- Active: what holds today ----------------------------------------

    /**
     * The tripwire for defect (2) in this class's notes. {@link LoreReader#parse} is annotated
     * {@code @NotNull} and returns {@code null}, because the committed parser's {@code file} rule
     * never collects the records it matches.
     *
     * <p>When {@code LoreGrammar.g4} is fixed and regenerated this assertion will fail. That is the
     * point: the failure is the signal to delete this test and enable the disabled ones below.
     */
    @Test
    void parseCurrentlyReturnsNull() throws IOException {
        String path = tempFile("one-record.txt", ONE_RECORD);

        assertNull(new LoreReader().parse(path),
                "LoreGrammar's file rule now collects its records - enable the disabled tests below "
                        + "and delete this one");
    }

    /**
     * The real file goes further than the fixture and needs more than the monster races: its
     * {@code blow:} lines resolve their method and effect against the combat tables while parsing.
     * So the ordering requirement is not one registry but several, and the reader gives no useful
     * message when they are missing — the exception comes from the registry, mid-action.
     *
     * <p>Reading the real file end to end therefore belongs with the disabled tests below, behind a
     * {@code GameConstants.init()} bootstrap; what is pinned here is the dependency itself.
     */
    @Test
    void theRealFileAlsoNeedsTheCombatTablesLoaded() {
        IllegalStateException thrown =
                assertThrows(IllegalStateException.class, () -> new LoreReader().parse(REAL_FILE));

        assertTrue(thrown.getMessage().contains("blowMethods"), thrown::getMessage);
    }

    /**
     * A coupling worth pinning: the grammar resolves each {@code name:} against
     * {@code MonsterRegistry} while parsing, so lore cannot be read before the monster races are
     * loaded. C has the same ordering requirement — {@code lore_parser} reuses monster.txt's own
     * parser.
     */
    @Test
    void parsingBeforeTheMonsterRacesAreLoadedThrows() throws Exception {
        monsterRaces.set(null, null);
        String path = tempFile("unloaded.txt", ONE_RECORD);

        assertThrows(IllegalStateException.class, () -> new LoreReader().parse(path));
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new LoreReader().parse(tempDir.resolve("absent.txt").toString()));
    }

    // ---- Disabled: the spec, once the grammar generates --------------------

    @Test
    @Disabled("LoreGrammar.g4 does not generate: $Lore should be $lore in monsterLore's @after")
    void theRealFileLoadsEveryRecord() throws IOException {
        List<MonsterLore> lore = new LoreReader().parse(REAL_FILE);

        assertNotNull(lore);
        assertEquals(EXPECTED_RECORDS, lore.size());
    }

    @Test
    @Disabled("LoreGrammar.g4 does not generate: $Lore should be $lore in monsterLore's @after")
    void countsAreReadAcrossInFieldOrder() throws Exception {
        // name:singing, happy drunk / counts:6:0:1:0:0:0:0 - the first record in the file.
        // The grammar names the third field "kills" and stores it as tkills (total kills), leaving
        // pkills (player kills) at 0; the last two fields are read but currently dropped on the
        // floor, since MonsterLore's constructor takes castInnate/castSpell from elsewhere.
        MonsterLore first = new LoreReader().parse(REAL_FILE).getFirst();

        assertEquals(6, (int) field(first, "sightings"));
        assertEquals(0, (int) field(first, "deaths"));
        assertEquals(1, (int) field(first, "tkills"));
        assertEquals(0, (int) field(first, "wake"));
        assertEquals(0, (int) field(first, "ignore"));
    }

    @Test
    @Disabled("LoreGrammar.g4 does not generate: $Lore should be $lore in monsterLore's @after")
    void repeatedFlagsLinesAccumulateIntoOneFlagSet() throws Exception {
        // The first record spreads its flags over three flags: lines; the grammar unions each line's
        // set into the record's, so a flag from the last line is set alongside one from the first.
        MonsterLore first = new LoreReader().parse(REAL_FILE).getFirst();

        Flag<MonsterRaceFlag> flags = field(first, "flags");
        assertTrue(flags.has(MonsterRaceFlag.RF_UNIQUE), "from the first flags: line");
        assertTrue(flags.has(MonsterRaceFlag.RF_INVISIBLE), "from the second flags: line");
        assertTrue(flags.has(MonsterRaceFlag.RF_CLEAR_WEB), "from the third flags: line");
    }

    @Test
    @Disabled("LoreGrammar.g4 does not generate: $Lore should be $lore in monsterLore's @after")
    void aBlowIsRecordedWithTheNumberOfTimesItWasSeen() throws Exception {
        // blow:BEG:NONE:0+0d0M0:2:0 on the first record. The fifth field is C's blow index, which
        // the port has no use for - it holds the instantiated BlowMethod/BlowEffect instead.
        MonsterLore first = new LoreReader().parse(REAL_FILE).getFirst();

        assertEquals(1, ((java.util.Map<?, ?>) field(first, "timeBlowsSeen")).size());
        assertTrue(((java.util.Map<?, ?>) field(first, "timeBlowsSeen")).containsValue(2));
    }

    @Test
    @Disabled("LoreGrammar.g4 does not generate: $Lore should be $lore in monsterLore's @after")
    void eachRecordIsAttachedToItsMonsterRace() {
        // The monsterLore rule's @after calls race.setLore(...), so a parsed record is reachable
        // from the race as well as from the returned list. Needs the real monster races loaded
        // (GameConstants.init()), not the empty registry this class seeds.
        fail("enable with a GameConstants.init() bootstrap once the grammar generates");
    }

    @Test
    @Disabled("LoreReader still hand-rolls its ANTLR plumbing - no ParseResult, no soft errors")
    void unknownFlagsAreReportedAsSoftErrorsRatherThanThrown() {
        // Today an unrecognised flag name reaches MonsterRaceFlag.valueOf inside a grammar action
        // and throws IllegalArgumentException straight out of parse(). Once the reader moves onto
        // GrammarDriver it should report and skip, per the partial-results contract every other
        // reader follows.
        fail("enable once LoreReader delegates to GrammarDriver and gains parseWithResults");
    }
}
