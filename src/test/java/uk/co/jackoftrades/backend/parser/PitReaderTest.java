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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.PitProfile;
import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link PitReader}: file text -> {@code PitLexer}/{@code PitGrammar}
 * -> {@code PitParseRecord} -> {@code PitAssembler} -> {@link PitProfile}.
 *
 * <p>The pit assembler resolves {@code mon-base:} names against the loaded monster bases,
 * {@code mon-ban:} names against the loaded monster races, and {@code spell-*}/{@code flags-*}/
 * {@code color:} tokens against their enums/colour table. Those registries come from the full
 * {@link GameConstants#init()} chain (which loads both {@code monster_base.txt} and
 * {@code monster.txt}), mirroring {@code MonsterReaderTest}'s bootstrap.
 *
 * <p>{@link PitProfile} exposes no getters, so field-level assertions read its private fields
 * reflectively through {@link #field}.
 *
 * @author Rowan Crowther
 */
class PitReaderTest {

    private static final String REAL_FILE = "lib/gamedata/pit.txt";

    /**
     * pit.txt's {@code record-count:} header, and the number of {@code name:} records in it.
     */
    private static final int EXPECTED_PITS = 40;

    /**
     * A minimal record touching every directive the error fixtures perturb, used as the clean
     * neighbour so each of those can assert that one bad record costs only itself. Every name in it
     * resolves against the registries {@link #bootstrap()} loads.
     */
    private static final String GOOD_RECORD = String.join("\n",
            "name:Good pit",
            "room:1",
            "alloc:1:25",
            "obj-rarity:0",
            "color:r",
            "mon-base:orc",
            "mon-ban:Horned Reaper",
            "flags-req:ANIMAL",
            "flags-ban:UNIQUE",
            "innate-freq:5",
            "spell-req:BR_ACID",
            "spell-ban:BR_ELEC");

    @TempDir
    Path tempDir;

    @BeforeAll
    static void bootstrap() {
        GameConstants.init();
    }

    /**
     * {@link GameConstants#init()} populates the shared object-kind registries in place; reset them
     * to the empty baseline so this heavy load does not leak into order-sensitive suites (matching
     * {@code MonsterReaderTest}'s cleanup).
     */
    @AfterAll
    static void cleanup() throws Exception {
        ObjectRegistry.reset();
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(PitProfile target, String name) throws Exception {
        Field f = PitProfile.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    /**
     * Reads a private static field off {@link MonsterRegistry} (no getter exists for the pit registry).
     */
    @SuppressWarnings("unchecked")
    private static <T> T staticField(String name) throws Exception {
        Field f = MonsterRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(null);
    }

    private static PitProfile byName(List<PitProfile> pits, String name) throws Exception {
        for (PitProfile p : pits) {
            if (name.equals(field(p, "name"))) return p;
        }
        return null;
    }

    /**
     * @return the names of the pits that survived assembly, in file order
     */
    private static List<String> names(ParseResult<PitProfile> result) throws Exception {
        List<String> names = new ArrayList<>();
        for (PitProfile pit : result.items()) {
            names.add(field(pit, "name"));
        }
        return names;
    }

    /**
     * Writes {@code content} to a file in the temp dir and returns its absolute path.
     */
    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllPitsWithNoErrors() throws IOException {
        ParseResult<PitProfile> result = new PitReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(EXPECTED_PITS, result.items().size());
    }

    /**
     * The genuine startup path: {@link GameConstants#init()} (run in {@link #bootstrap()}) calls
     * {@code loadPitProfiles()}, which only stores into {@code monsterPitProfiles} when the parse is
     * error-free — otherwise it logs fatal and leaves the field {@code null}. So a non-null registry
     * of the expected size proves pit.txt loaded cleanly as part of the real init chain.
     */
    @Test
    void initPopulatesThePitRegistry() throws Exception {
        List<PitProfile> registry = staticField("monsterPitProfiles");

        assertNotNull(registry, "init() left monsterPitProfiles null - loadPitProfiles reported errors");
        assertEquals(EXPECTED_PITS, registry.size());
    }

    /**
     * Spot-checks a few records that exercise every non-trivial leg: multi-line spell-req
     * accumulation, spell-ban, multiple colours, banned flags, and a mon-ban race lookup.
     */
    @Test
    void representativeRecordsResolveCorrectly() throws Exception {
        List<PitProfile> pits = new PitReader().parseWithResults(REAL_FILE).items();

        // Multi-hued dragons: two spell-req lines accumulate into one flag set; no spell-ban.
        PitProfile multi = byName(pits, "Multi-hued dragons");
        assertNotNull(multi);
        assertEquals(PitRoomType.PIT_TYPE_PIT, field(multi, "roomType"));
        Flag<MonsterSpell> multiSpells = field(multi, "spellsFlags");
        for (String s : List.of("RSF_BR_ACID", "RSF_BR_ELEC", "RSF_BR_FIRE", "RSF_BR_COLD", "RSF_BR_POIS")) {
            assertTrue(multiSpells.has(MonsterSpell.valueOf(s)), s + " should be set");
        }

        // Warriors: three colours, two banned spells.
        PitProfile warriors = byName(pits, "Warriors");
        assertNotNull(warriors);
        List<ColourEnum> cols = field(warriors, "colours");
        assertEquals(3, cols.size());
        Flag<MonsterSpell> banned = field(warriors, "forbiddenSpellFlags");
        assertTrue(banned.has(MonsterSpell.RSF_DARKNESS), "DARKNESS should be banned");
        assertTrue(banned.has(MonsterSpell.RSF_ARROW), "ARROW should be banned");

        // Demons: the sole mon-ban line resolves to a real monster race.
        PitProfile demons = byName(pits, "Demons");
        assertNotNull(demons);
        List<MonsterRace> forbidden = field(demons, "forbiddenMonsters");
        assertEquals(1, forbidden.size());

        // Jelly: multiple mon-base entries all resolve.
        PitProfile jelly = byName(pits, "Jelly");
        assertNotNull(jelly);
        List<MonsterBase> bases = field(jelly, "bases");
        assertEquals(4, bases.size());
    }

    // ---- Fixtures: the error paths ---------------------------------------

    /**
     * The fixture the error tests build on loads cleanly by itself — so a failure below is the
     * injected defect, not the fixture.
     */
    @Test
    void theFixtureRecordLoadsCleanly() throws Exception {
        String path = tempFile("good.txt", "record-count:1\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        PitProfile good = result.items().getFirst();
        assertEquals("Good pit", field(good, "name"));
        assertEquals(PitRoomType.PIT_TYPE_PIT, field(good, "roomType"));
        // alloc:1:25 is rarity first, average depth second - the two are easy to transpose.
        int rarity = field(good, "rarity");
        int ave = field(good, "ave");
        int innateFreq = field(good, "freqInnate");
        assertEquals(1, rarity);
        assertEquals(25, ave);
        assertEquals(5, innateFreq);
        List<ColourEnum> colours = field(good, "colours");
        assertEquals(List.of(ColourEnum.COLOUR_RED), colours);
        List<MonsterBase> bases = field(good, "bases");
        assertEquals(1, bases.size());
        List<MonsterRace> banned = field(good, "forbiddenMonsters");
        assertEquals(1, banned.size());
    }

    /**
     * An unknown {@code flags-req:} code. The codes are the bare tails of {@link MonsterRaceFlag}
     * constants, so the assembler re-attaches {@code RF_} before resolving; anything that does not
     * name a constant drops the record, and every bad code in the record is reported before it goes.
     */
    @Test
    void anUnknownRequiredFlagDropsTheRecordAndReportsEveryBadCode() throws Exception {
        String badFlags = GOOD_RECORD.replace("name:Good pit", "name:Bad flags")
                .replace("flags-req:ANIMAL", "flags-req:NOT_A_FLAG | ALSO_NOT_A_FLAG");
        String path = tempFile("bad-flags.txt",
                "record-count:2\n" + badFlags + "\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertEquals(List.of("Good pit"), names(result));
        assertEquals(2, result.errors().stream()
                        .filter(e -> e.contains("an unknown flag")).count(),
                result.errors()::toString);
    }

    /**
     * {@code flags-ban:} resolves out of the same {@link MonsterRaceFlag} family and fails the same
     * way — worth pinning separately, since the two are separate loops in the assembler and only a
     * test tells you the second one reads {@code bannedFlags()} rather than {@code flags()}.
     */
    @Test
    void anUnknownBannedFlagDropsTheRecord() throws Exception {
        String badBan = GOOD_RECORD.replace("name:Good pit", "name:Bad flag ban")
                .replace("flags-ban:UNIQUE", "flags-ban:NOT_A_FLAG");
        String path = tempFile("bad-flag-ban.txt",
                "record-count:2\n" + badBan + "\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertEquals(List.of("Good pit"), names(result));
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("an unknown flag: NOT_A_FLAG")),
                result.errors()::toString);
    }

    /**
     * The spell codes take the {@code RSF_} prefix rather than {@code RF_}, and {@code spell-req:}
     * and {@code spell-ban:} are again separate loops over separate record fields.
     */
    @Test
    void anUnknownSpellDropsTheRecordOnEitherSpellLine() throws Exception {
        String badReq = GOOD_RECORD.replace("name:Good pit", "name:Bad spell req")
                .replace("spell-req:BR_ACID", "spell-req:BR_NOTHING");
        String badBan = GOOD_RECORD.replace("name:Good pit", "name:Bad spell ban")
                .replace("spell-ban:BR_ELEC", "spell-ban:BR_NOTHING_ELSE");
        String path = tempFile("bad-spells.txt",
                "record-count:3\n" + badReq + "\n" + badBan + "\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertEquals(List.of("Good pit"), names(result));
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("an unknown spell: BR_NOTHING")),
                result.errors()::toString);
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("an unknown spell: BR_NOTHING_ELSE")),
                result.errors()::toString);
    }

    /**
     * {@code mon-base:} names are resolved against the loaded monster bases and {@code mon-ban:}
     * against the loaded monster races — different registries, different messages, and the reason
     * pit loading has to run after both {@code monster_base.txt} and {@code monster.txt}.
     */
    @Test
    void unresolvableMonsterNamesDropTheRecord() throws Exception {
        String badBase = GOOD_RECORD.replace("name:Good pit", "name:Bad base")
                .replace("mon-base:orc", "mon-base:hippogriff");
        String badBan = GOOD_RECORD.replace("name:Good pit", "name:Bad ban")
                .replace("mon-ban:Horned Reaper", "mon-ban:Norman the Unlikely");
        String path = tempFile("bad-monsters.txt",
                "record-count:3\n" + badBase + "\n" + badBan + "\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertEquals(List.of("Good pit"), names(result));
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("an unknown monster base: hippogriff")),
                result.errors()::toString);
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("an unknown banned monster base: Norman the Unlikely")),
                result.errors()::toString);
    }

    /**
     * The numeric directives are lexed as integers, so the only way to reach the assembler's
     * {@code NumberFormatException} arms is a value too large for an {@code int}. All four are
     * checked together because each is a separate {@code try} block with its own message, and a
     * copy-paste slip between them would otherwise go unseen.
     */
    @Test
    void anOverflowingNumberDropsTheRecord() throws Exception {
        String badRarity = GOOD_RECORD.replace("name:Good pit", "name:Huge rarity")
                .replace("alloc:1:25", "alloc:99999999999:25");
        String badDepth = GOOD_RECORD.replace("name:Good pit", "name:Huge depth")
                .replace("alloc:1:25", "alloc:1:99999999999");
        String badObjRarity = GOOD_RECORD.replace("name:Good pit", "name:Huge obj-rarity")
                .replace("obj-rarity:0", "obj-rarity:99999999999");
        String badFreq = GOOD_RECORD.replace("name:Good pit", "name:Huge freq")
                .replace("innate-freq:5", "innate-freq:99999999999");
        String path = tempFile("overflow.txt", "record-count:5\n" + badRarity + "\n" + badDepth
                + "\n" + badObjRarity + "\n" + badFreq + "\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertEquals(List.of("Good pit"), names(result));
        for (String fragment : List.of("a malformed alloc first integer",
                "a malformed alloc second integer",
                "a malformed obj-rarity integer",
                "a malformed innate frequency integer")) {
            assertTrue(result.errors().stream().anyMatch(e -> e.contains(fragment)),
                    () -> fragment + " not reported: " + result.errors());
        }
    }

    /**
     * Every directive except {@code name:} is optional, and the grammar accepts them in any order —
     * C's parser does too. A record carrying nothing but {@code name:} is the one shape the grammar
     * rejects, since {@code pitRecord} needs at least one directive after the name.
     */
    @Test
    void directivesAreOptionalAndUnordered() throws Exception {
        String reordered = String.join("\n",
                "name:Backwards",
                "spell-ban:BR_ELEC",
                "mon-base:orc",
                "alloc:2:30",
                "room:2");
        String path = tempFile("reordered.txt", "record-count:1\n" + reordered + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        PitProfile pit = result.items().getFirst();
        assertEquals(PitRoomType.PIT_TYPE_NEST, field(pit, "roomType"));
        int rarity = field(pit, "rarity");
        int ave = field(pit, "ave");
        assertEquals(2, rarity);
        assertEquals(30, ave);
        // The omitted directives leave their neutral defaults rather than nulls.
        int objectRarity = field(pit, "objectRarity");
        int innateFreq = field(pit, "freqInnate");
        assertEquals(0, objectRarity);
        assertEquals(0, innateFreq);
        List<ColourEnum> colours = field(pit, "colours");
        assertTrue(colours.isEmpty());
    }

    /**
     * A {@code room:} digit outside 1..3 is deliberately <em>not</em> an error: it falls through to
     * {@link PitRoomType#PIT_TYPE_NONE}, the same value an absent {@code room:} leaves.
     */
    @Test
    void anUnrecognisedRoomDigitFallsBackToNoneRatherThanErroring() throws Exception {
        String oddRoom = GOOD_RECORD.replace("room:1", "room:9");
        String noRoom = GOOD_RECORD.replace("name:Good pit", "name:No room").replace("room:1\n", "");
        String path = tempFile("room-digits.txt",
                "record-count:2\n" + oddRoom + "\n" + noRoom + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(PitRoomType.PIT_TYPE_NONE, field(result.items().getFirst(), "roomType"));
        assertEquals(PitRoomType.PIT_TYPE_NONE, field(result.items().getLast(), "roomType"));
    }

    /**
     * A record with a {@code name:} and nothing else is a grammar error, and hard errors fail the
     * whole file closed rather than costing one record.
     */
    @Test
    void aRecordWithNoDirectivesFailsClosed() throws IOException {
        String path = tempFile("name-only.txt",
                "record-count:2\nname:Nothing but a name\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * A wrong {@code record-count:} is soft: reported, but the records that parsed still load. C's
     * own parser never validates this header at all, so this check is stricter than the original.
     */
    @Test
    void recordCountMismatchIsReportedButPitsStillLoad() throws IOException {
        String path = tempFile("bad-count.txt", "record-count:7\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertEquals(1, result.items().size(), "the count check must not cost the file its records");
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 7") && e.contains("contains 1")),
                result.errors()::toString);
    }

    /**
     * The assembler's unknown-colour arm has no fixture because it cannot be reached from a file:
     * {@code COLOUR_CHAR} only matches the 28 codes the shared {@code Colours} alphabet lists, and
     * {@code ColourEnum.fromCode} resolves every one of them. A colour the lexer does not know is a
     * hard lexer error instead, and the file fails closed.
     */
    @Test
    void anUnknownColourCodeIsALexerErrorNotASoftOne() throws IOException {
        String badColour = GOOD_RECORD.replace("color:r", "color:q");
        String path = tempFile("bad-colour.txt", "record-count:1\n" + badColour + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * {@link PitReader#parse} is {@link PitReader#parseWithResults} with the messages dropped: same
     * items, no way to see what went wrong.
     */
    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        PitReader reader = new PitReader();

        assertEquals(reader.parseWithResults(REAL_FILE).items().size(),
                reader.parse(REAL_FILE).size());
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new PitReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }
}
