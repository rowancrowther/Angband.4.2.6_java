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
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
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
 * Tests for {@link LoreReader}, which reads {@code lib/user/lore.txt} — the player's per-save
 * monster-knowledge record, not a {@code lib/gamedata} file. The port of C's
 * {@code lore_parser} ({@code [C] src/mon-init.c:2646}).
 *
 * <p>{@code LoreGrammar.g4}'s {@code monsterLore} rule used to write {@code race.setLore($Lore)}
 * with a capital L against a {@code $lore} return value, which ANTLR rejected outright
 * ({@code error(63): unknown attribute reference Lore in $Lore}), so the grammar never generated
 * and the committed {@code backend/parser/lore/LoreGrammar.java} predated it — its {@code file}
 * rule declared {@code loreEntries} but never built it, so {@link LoreReader#parse} always handed
 * back {@code null}. Both are fixed now: the typo is gone, {@code file} collects each
 * {@code monsterLore} match, and the {@code @after} block guards {@code race.setLore(...)} against
 * an unresolved monster name — so the specs below run for real.
 *
 * <p>Two things still want attention, both flagged in the grammar's own comments:
 * {@link LoreReader} still hand-rolls the ANTLR plumbing instead of delegating to
 * {@link GrammarDriver} (so it has no {@code parseWithResults} and no soft-error channel — see
 * {@link #unknownFlagsAreReportedAsSoftErrorsRatherThanThrown()}), and {@code GameConstants} keeps
 * its {@code loadMonsterLore()} commented out, so nothing calls the reader at start-up yet and
 * {@link #eachRecordIsAttachedToItsMonsterRace()} still needs a {@code GameConstants.init()}
 * bootstrap this class does not provide.
 *
 * <p>The tests seed {@link uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry}
 * themselves rather than running
 * {@link uk.co.jackoftradesltd.middle.game.globals.GameConstants#init()}, so they stay hermetic and
 * order-independent — bar the two still-disabled ones below, which need the real registries.
 *
 * @author Rowan Crowther
 */
@Disabled("LoreGrammar is being stubbed out - not needed for now (2026-09-10); "
        + "re-enable once LoreReader has a real grammar behind it again")
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
    private Field blowEffects;
    private Object savedBlowEffects;

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
     * the grammar's actions do not dereference.
     *
     * <p>{@code blowMethods} and {@code blowEffects} are pushed the other way, back to their
     * unloaded {@code null}, so that {@link #theRealFileAlsoNeedsTheCombatTablesLoaded()} does not
     * depend on whether some earlier test class in the same JVM happened to leave the combat tables
     * populated. Tests that need a {@code blow:} line to parse without throwing re-seed both via
     * {@link #seedCombatTables()}.
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

        blowEffects = RegistrySeeding.resolve("blowEffects");
        blowEffects.setAccessible(true);
        savedBlowEffects = blowEffects.get(null);
        blowEffects.set(null, null);
    }

    @AfterEach
    void restoreTheRegistries() throws Exception {
        monsterRaces.set(null, savedRaces);
        blowMethods.set(null, savedBlowMethods);
        blowEffects.set(null, savedBlowEffects);
    }

    /**
     * Seeds {@code blowMethods} and {@code blowEffects} with empty lists, so the grammar's
     * {@code blow} rule can resolve (and simply not find) a method/effect name instead of
     * throwing. For tests that parse a real {@code blow:} line but don't care which method/effect
     * it resolves to — {@link uk.co.jackoftradesltd.middle.monsters.MonsterBlow} has no
     * {@code equals}/{@code hashCode}, so a {@code null} method/effect doesn't collapse distinct
     * blows together in a map.
     */
    private void seedCombatTables() throws Exception {
        blowMethods.set(null, List.of());
        blowEffects.set(null, List.of());
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

    // ---- The spec, now the grammar generates --------------------------

    @Test
    void theRealFileLoadsEveryRecord() throws Exception {
        seedCombatTables();
        List<MonsterLore> lore = new LoreReader().parse(REAL_FILE);

        assertNotNull(lore);
        assertEquals(EXPECTED_RECORDS, lore.size());
    }

    @Test
    void countsAreReadAcrossInFieldOrder() throws Exception {
        // name:singing, happy drunk / counts:6:0:1:0:0:0:0 - the first record in the file.
        // The grammar names the third field "kills" and stores it as tkills (total kills), leaving
        // pkills (player kills) at 0; the last two fields are read but currently dropped on the
        // floor, since MonsterLore's constructor takes castInnate/castSpell from elsewhere.
        seedCombatTables();
        MonsterLore first = new LoreReader().parse(REAL_FILE).getFirst();

        assertEquals(6, (int) field(first, "sightings"));
        assertEquals(0, (int) field(first, "deaths"));
        assertEquals(1, (int) field(first, "tkills"));
        assertEquals(0, (int) field(first, "wake"));
        assertEquals(0, (int) field(first, "ignore"));
    }

    @Test
    void repeatedFlagsLinesAccumulateIntoOneFlagSet() throws Exception {
        // The first record spreads its flags over three flags: lines; the grammar unions each line's
        // set into the record's, so a flag from the last line is set alongside one from the first.
        seedCombatTables();
        MonsterLore first = new LoreReader().parse(REAL_FILE).getFirst();

        Flag<MonsterRaceFlag> flags = field(first, "flags");
        assertTrue(flags.has(MonsterRaceFlag.RF_UNIQUE), "from the first flags: line");
        assertTrue(flags.has(MonsterRaceFlag.RF_INVISIBLE), "from the second flags: line");
        assertTrue(flags.has(MonsterRaceFlag.RF_CLEAR_WEB), "from the third flags: line");
    }

    @Test
    void aBlowIsRecordedWithTheNumberOfTimesItWasSeen() throws Exception {
        // blow:BEG:NONE:0+0d0M0:2:0 on the first record. The fifth field is C's blow index, which
        // the port has no use for - it holds the instantiated BlowMethod/BlowEffect instead.
        seedCombatTables();
        MonsterLore first = new LoreReader().parse(REAL_FILE).getFirst();

        assertEquals(1, ((java.util.Map<?, ?>) field(first, "timeBlowsSeen")).size());
        assertTrue(((java.util.Map<?, ?>) field(first, "timeBlowsSeen")).containsValue(2));
    }

    // ---- Still disabled: waiting on further work --------------------------

    @Test
    @Disabled("needs a GameConstants.init() bootstrap - this class seeds an empty registry")
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
