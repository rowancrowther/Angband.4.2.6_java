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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.cave.PitProfile;
import uk.co.jackoftradesltd.middle.cave.enums.PitRoomType;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link PitReader}: file text -> {@code PitLexer}/{@code PitGrammar}
 * -> {@code PitParseRecord} -> {@code PitAssembler} -> {@link PitProfile}.
 *
 * <p>The pit assembler resolves {@code mon-base:} names against the loaded monster bases,
 * {@code mon-ban:} names against the loaded monster races, and {@code spell-*}/{@code flags-*}/
 * {@code color:} tokens against their enums/colour table. Those registries come from the full
 * {@code GameConstants.init(CoreChannel)} chain (which loads both {@code monster_base.txt} and
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
     * neighbour of a bad record so the fixture shows that the bad one alone ends the load. Every
     * name in it resolves against the registries {@link #bootstrap()} loads.
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
    static void bootstrap() throws IOException {
        // Mirrors UILoop's EVENT_ENTER_INIT handler: the real init chain now blocks partway
        // through GameConstants.init() for this ack, so the front end's UIEntry load has to
        // actually happen before it is sent, or the assemblers below find an empty UIRegistry.
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    /**
     * {@code GameConstants.init(CoreChannel)} populates the shared object-kind registries in place; reset them
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
     * The genuine startup path: {@code GameConstants.init(CoreChannel)} (run in {@link #bootstrap()}) calls
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
        // innate-freq:5 is stored as 100 / 5, as parse_pit_innate_freq does, not as written.
        assertEquals(20, innateFreq);
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
     * name a constant ends the load, as {@code PARSE_ERROR_INVALID_FLAG} does in C, and the first bad
     * code is the one named.
     */
    @Test
    void anUnknownRequiredFlagEndsTheLoadAtTheFirstBadCode() throws Exception {
        String badFlags = GOOD_RECORD.replace("name:Good pit", "name:Bad flags")
                .replace("flags-req:ANIMAL", "flags-req:NOT_A_FLAG | ALSO_NOT_A_FLAG");
        String path = tempFile("bad-flags.txt",
                "record-count:2\n" + badFlags + "\n" + GOOD_RECORD + "\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new PitReader().parseWithResults(path));

        assertTrue(e.getMessage().endsWith("an illegal monster race flag value: NOT_A_FLAG"), e.getMessage());
    }

    /**
     * {@code flags-ban:} resolves out of the same {@link MonsterRaceFlag} family and fails the same
     * way - worth pinning separately, since the two are separate loops in the assembler and only a
     * test tells you the second one reads {@code bannedFlags()} rather than {@code flags()}.
     */
    @Test
    void anUnknownBannedFlagEndsTheLoad() throws Exception {
        String badBan = GOOD_RECORD.replace("name:Good pit", "name:Bad flag ban")
                .replace("flags-ban:UNIQUE", "flags-ban:NOT_A_FLAG");
        String path = tempFile("bad-flag-ban.txt",
                "record-count:2\n" + badBan + "\n" + GOOD_RECORD + "\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new PitReader().parseWithResults(path));

        assertTrue(e.getMessage().endsWith("an illegal banned monster race flag value: NOT_A_FLAG"),
                e.getMessage());
    }

    /**
     * The spell codes take the {@code RSF_} prefix rather than {@code RF_}, and {@code spell-req:}
     * and {@code spell-ban:} are again separate loops over separate record fields. Each ends the
     * load on its own.
     */
    @Test
    void anUnknownSpellEndsTheLoadOnEitherSpellLine() throws Exception {
        String badReq = GOOD_RECORD.replace("name:Good pit", "name:Bad spell req")
                .replace("spell-req:BR_ACID", "spell-req:BR_NOTHING");
        String badBan = GOOD_RECORD.replace("name:Good pit", "name:Bad spell ban")
                .replace("spell-ban:BR_ELEC", "spell-ban:BR_NOTHING_ELSE");
        String reqPath = tempFile("bad-spell-req.txt", "record-count:2\n" + badReq + "\n" + GOOD_RECORD + "\n");
        String banPath = tempFile("bad-spell-ban.txt", "record-count:2\n" + badBan + "\n" + GOOD_RECORD + "\n");

        DataFileParseException req = assertThrows(DataFileParseException.class,
                () -> new PitReader().parseWithResults(reqPath));
        DataFileParseException ban = assertThrows(DataFileParseException.class,
                () -> new PitReader().parseWithResults(banPath));

        assertTrue(req.getMessage().endsWith("an illegal spell value: BR_NOTHING"), req.getMessage());
        assertTrue(ban.getMessage().endsWith("an illegal banned spell value: BR_NOTHING_ELSE"), ban.getMessage());
    }

    /**
     * {@code mon-base:} names are resolved against the loaded monster bases and {@code mon-ban:}
     * against the loaded monster races - different registries, and the reason pit loading has to run
     * after both {@code monster_base.txt} and {@code monster.txt}. They also part ways on a name that
     * resolves to nothing: {@code parse_pit_mon_base} returns {@code PARSE_ERROR_UNRECOGNISED_TVAL},
     * which ends the load, but {@code parse_pit_mon_ban} stores the {@code NULL} race that
     * {@code lookup_monster} gave it and the load goes on.
     */
    @Test
    void anUnresolvableMonBaseEndsTheLoadButAnUnresolvableMonBanLoadsAsNull() throws Exception {
        String badBase = GOOD_RECORD.replace("name:Good pit", "name:Bad base")
                .replace("mon-base:orc", "mon-base:hippogriff");
        String badBan = GOOD_RECORD.replace("name:Good pit", "name:Bad ban")
                .replace("mon-ban:Horned Reaper", "mon-ban:Norman the Unlikely");
        String basePath = tempFile("bad-base.txt", "record-count:2\n" + badBase + "\n" + GOOD_RECORD + "\n");
        String banPath = tempFile("bad-ban.txt", "record-count:2\n" + badBan + "\n" + GOOD_RECORD + "\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new PitReader().parseWithResults(basePath));
        ParseResult<PitProfile> result = new PitReader().parseWithResults(banPath);

        assertTrue(e.getMessage().endsWith("an illegal monster base value: hippogriff"), e.getMessage());
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("Bad ban", "Good pit"), names(result));
        List<MonsterRace> banned = field(byName(result.items(), "Bad ban"), "forbiddenMonsters");
        assertEquals(1, banned.size());
        assertNull(banned.getFirst());
    }

    /**
     * parse_pit_innate_freq rejects a percentage outside 1 to 100 and stores {@code 100 / pct}.
     * 0 and 101 end the load; 1, 4 and 100 are accepted and stored as 100, 25 and 1.
     */
    @Test
    void innateFreqIsRangeCheckedAndStoredAsHundredOverPercent() throws Exception {
        for (String bad : List.of("0", "101")) {
            String record = GOOD_RECORD.replace("innate-freq:5", "innate-freq:" + bad);
            String badPath = tempFile("freq-" + bad + ".txt", "record-count:1\n" + record + "\n");

            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> new PitReader().parseWithResults(badPath));

            assertTrue(e.getMessage().endsWith("an out of range innate frequency value: " + bad), e.getMessage());
        }
        String one = GOOD_RECORD.replace("name:Good pit", "name:One").replace("innate-freq:5", "innate-freq:1");
        String four = GOOD_RECORD.replace("name:Good pit", "name:Four").replace("innate-freq:5", "innate-freq:4");
        String hundred = GOOD_RECORD.replace("name:Good pit", "name:Hundred").replace("innate-freq:5", "innate-freq:100");
        String path = tempFile("freq-range.txt", "record-count:3\n" + one + "\n" + four + "\n" + hundred + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertEquals(List.of("One", "Four", "Hundred"), names(result));
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        int storedOne = field(result.items().get(0), "freqInnate");
        int storedFour = field(result.items().get(1), "freqInnate");
        int storedHundred = field(result.items().get(2), "freqInnate");
        assertEquals(100, storedOne);
        assertEquals(25, storedFour);
        assertEquals(1, storedHundred);
    }

    /**
     * A number too large for an {@code int} is clamped to {@link Integer#MAX_VALUE} in the three
     * {@code uint} fields (accepted deviation, decided 261027: C keeps the low 32 bits), and the
     * clamped {@code innate-freq:} is far above 100, so it ends the load as out of range.
     */
    @Test
    void anOverflowingNumberIsClampedAndAnOverflowingInnateFreqIsOutOfRange() throws Exception {
        String hugeUints = GOOD_RECORD.replace("name:Good pit", "name:Huge")
                .replace("alloc:1:25", "alloc:99999999999:99999999999")
                .replace("obj-rarity:0", "obj-rarity:99999999999");
        String badFreq = GOOD_RECORD.replace("name:Good pit", "name:Huge freq")
                .replace("innate-freq:5", "innate-freq:99999999999");
        String path = tempFile("overflow.txt", "record-count:1\n" + hugeUints + "\n");
        String freqPath = tempFile("overflow-freq.txt", "record-count:1\n" + badFreq + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new PitReader().parseWithResults(freqPath));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        PitProfile pit = result.items().getFirst();
        int rarity = field(pit, "rarity");
        int ave = field(pit, "ave");
        int objectRarity = field(pit, "objectRarity");
        assertEquals(Integer.MAX_VALUE, rarity);
        assertEquals(Integer.MAX_VALUE, ave);
        assertEquals(Integer.MAX_VALUE, objectRarity);
        assertTrue(e.getMessage().endsWith("an out of range innate frequency value: 99999999999"), e.getMessage());
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
     * hard lexer error instead, and the file fails closed. C never rejects a colour:
     * {@code color_char_to_attr} answers {@code COLOUR_WHITE} for a letter that is not in
     * {@code color_table}, so {@code color:q} loads there as white.
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

    // ---- The reader against the upstream pit.txt -------------------------

    /**
     * Every record of the upstream {@code pit.txt}, worked out by hand from the file and from
     * {@code parse_pit_*} in {@code mon-init.c}, not read back from the Java parser. One row per record,
     * in file order, ';'-separated: name, room digit, alloc rarity, alloc level, obj-rarity,
     * {@code freq_innate} (100 / the innate-freq percentage, 0 when absent), flags-req, flags-ban,
     * spell-req, spell-ban, mon-base names, color letters, mon-ban names. The multi-value columns are
     * ','-separated.
     */
    private static final String[] UPSTREAM = {
            "Orc;1;1;25;0;0;;;;;orc;;",
            "Troll;1;1;45;0;0;;;;;troll;;",
            "Giant;1;1;65;0;0;;;;;giant;;",
            "Acid dragons;1;6;85;0;0;;;BR_ACID;BR_ELEC;dragon,ancient dragon;;",
            "Electric dragons;1;6;85;0;0;;;BR_ELEC;BR_ACID;dragon,ancient dragon;;",
            "Fire dragons;1;6;85;0;0;;;BR_FIRE;BR_COLD;dragon,ancient dragon;;",
            "Ice dragons;1;6;85;0;0;;;BR_COLD;BR_FIRE;dragon,ancient dragon;;",
            "Poison dragons;1;6;85;0;0;;;BR_POIS;BR_FIRE;dragon,ancient dragon;;",
            "Multi-hued dragons;1;6;85;0;0;;;BR_ACID,BR_ELEC,BR_FIRE,BR_COLD,BR_POIS;;dragon,ancient dragon;;",
            "Demons;1;1;105;0;0;;;;;major demon;;Horned Reaper",
            "Jelly;2;1;20;5;0;;;;;icky thing,jelly,mold,mushroom;;",
            "Animals;2;1;50;10;0;ANIMAL;;;;;;",
            "Undead;2;1;80;20;0;UNDEAD;;;;;;",
            "Ants;2;1;15;5;0;;KILL_BODY;;;ant;;",
            "Kobolds;1;1;15;0;0;;;;;kobold;;",
            "Creepy crawlies;2;1;20;5;0;;;;BLINK,CONF,SLOW;centipede,killer beetle,spider;;",
            "Minor demons;1;1;20;5;0;;;;S_DEMON;minor demon;;",
            "Spellcasters;1;1;20;0;0;;;MISSILE;;;;",
            "Archers;1;1;50;0;25;;;ARROW;;;;",
            "Naga;1;1;25;0;0;;;;;naga;;",
            "Thieves;1;1;25;0;0;;;;;person;b;",
            "Warriors;1;1;25;0;0;;;;DARKNESS,ARROW;person;u,s,M;",
            "Dark dwarves;1;1;40;0;0;;;;BRAIN_SMASH;humanoid;D,B,u,P,s,R;",
            "Eyes;1;1;30;5;0;;;;S_UNDEAD,S_KIN;eye;;",
            "Ogres;1;1;30;0;0;;;;;ogre;;",
            "Believers;1;1;35;0;0;;;HEAL;S_ANIMAL;person;;",
            "Serpents;2;1;40;5;0;;;;BA_POIS,BR_FIRE;snake,naga,hydra;;",
            "Lesser undead;2;1;40;10;0;;;;BO_NETH,TELE_AWAY;skeleton,zombie,wraith;;",
            "Wizards;1;1;45;0;0;;;CONF;;person;;",
            "Vampires;1;1;55;0;0;UNDEAD;;;;bat,vampire;;",
            "Hydra;2;1;60;8;0;;;;;hydra;;",
            "Golems;1;1;65;10;0;;DRAGON,UNDEAD,DEMON;;;golem;;",
            "Gold dragons;1;6;85;0;0;;;BR_SOUN;BR_SHAR;dragon,ancient dragon;;",
            "Ainur;1;1;90;0;0;;;;;ainu;;",
            "Insects;3;2;15;0;0;;;;;ant,centipede,dragon fly,insect,killer beetle;;",
            "Reptiles;3;1;20;0;0;;;;;naga,snake,reptile;;",
            "Cave dwellers;3;1;25;0;0;;;;;yeek,kobold,orc,troll,ogre;;",
            "Moria dwellers;3;8;30;0;0;;;;;orc,ogre,troll,giant;;",
            "Creatures of Earth;3;8;35;0;0;;;;;xorn;;",
            "Elemental creatures;3;1;40;0;0;;;;;elemental,vortex;;",
    };

    /**
     * The colour letters pit.txt uses, with the colour each names in {@code color_table} in
     * {@code z-color.c}: 'b' Blue, 'u' Umber, 's' Slate, 'M' Mustard, 'D' Light Dark, 'B' Light Blue,
     * 'P' Light Purple and 'R' Light Red.
     */
    private static final Map<String, ColourEnum> COLOUR_LETTERS = Map.of(
            "b", ColourEnum.COLOUR_BLUE,
            "u", ColourEnum.COLOUR_UMBER,
            "s", ColourEnum.COLOUR_SLATE,
            "M", ColourEnum.COLOUR_MUSTARD,
            "D", ColourEnum.COLOUR_LIGHT_DARK,
            "B", ColourEnum.COLOUR_LIGHT_BLUE,
            "P", ColourEnum.COLOUR_LIGHT_PURPLE,
            "R", ColourEnum.COLOUR_LIGHT_RED);

    private static List<String> split(String column) {
        return column.isEmpty() ? List.of() : List.of(column.split(","));
    }

    /**
     * Every field of every record, against {@link #UPSTREAM}. The profiles must also come back in file
     * order, which is {@code pit_info} order in C: {@code parse_pit_name} pushes each record on the head
     * of a chain and {@code finish_parse_pit} fills the array downward from the end, so slot 0 is the
     * first record. The room digit is C's {@code room_type} and {@link PitRoomType} is declared in the
     * same order, so the digit is the ordinal.
     */
    @Test
    void realFileMatchesTheUpstreamFileRecordForRecordInFileOrder() throws Exception {
        List<PitProfile> pits = new PitReader().parseWithResults(REAL_FILE).items();

        assertEquals(UPSTREAM.length, pits.size());
        for (int i = 0; i < UPSTREAM.length; i++) {
            String[] row = UPSTREAM[i].split(";", -1);
            assertEquals(13, row.length, "fixture row " + i);
            String label = "record " + i + " (" + row[0] + ")";
            PitProfile pit = pits.get(i);

            assertEquals(row[0], field(pit, "name"), label);
            assertEquals(PitRoomType.values()[Integer.parseInt(row[1])], field(pit, "roomType"), label);
            int rarity = field(pit, "rarity");
            int ave = field(pit, "ave");
            int objectRarity = field(pit, "objectRarity");
            int freqInnate = field(pit, "freqInnate");
            assertEquals(Integer.parseInt(row[2]), rarity, label + " rarity");
            assertEquals(Integer.parseInt(row[3]), ave, label + " ave");
            assertEquals(Integer.parseInt(row[4]), objectRarity, label + " obj-rarity");
            assertEquals(Integer.parseInt(row[5]), freqInnate, label + " freq_innate");

            assertFlags(row[6], field(pit, "flags"), "RF_", label + " flags-req");
            assertFlags(row[7], field(pit, "forbiddenFlags"), "RF_", label + " flags-ban");
            assertSpells(row[8], field(pit, "spellsFlags"), label + " spell-req");
            assertSpells(row[9], field(pit, "forbiddenSpellFlags"), label + " spell-ban");

            List<MonsterBase> bases = field(pit, "bases");
            assertEquals(split(row[10]), bases.stream().map(MonsterBase::getCodeName).toList(), label + " mon-base");
            List<ColourEnum> colours = field(pit, "colours");
            assertEquals(split(row[11]).stream().map(COLOUR_LETTERS::get).toList(), colours, label + " color");
            List<MonsterRace> banned = field(pit, "forbiddenMonsters");
            // monster.txt spells the race "horned reaper"; lookup_monster matches the file's "Horned Reaper"
            // to it ignoring case, so the comparison here does too.
            assertEquals(split(row[12]).stream().map(String::toLowerCase).toList(),
                    banned.stream().map(r -> r.getName().toLowerCase()).toList(), label + " mon-ban");
        }
    }

    private static void assertFlags(String column, Flag<MonsterRaceFlag> flags, String prefix, String label) {
        List<String> expected = split(column);
        assertEquals(expected.size(), flags.count(), label);
        for (String code : expected) {
            assertTrue(flags.has(MonsterRaceFlag.valueOf(prefix + code)), label + " " + code);
        }
    }

    private static void assertSpells(String column, Flag<MonsterSpell> flags, String label) {
        List<String> expected = split(column);
        assertEquals(expected.size(), flags.count(), label);
        for (String code : expected) {
            assertTrue(flags.has(MonsterSpell.valueOf("RSF_" + code)), label + " " + code);
        }
    }

    // ---- Where the directives meet: what C keeps, what it overwrites -----

    /**
     * Writes a file of the given records with a matching {@code record-count:} header and loads it.
     */
    private ParseResult<PitProfile> load(String name, String... records) throws IOException {
        String path = tempFile(name, "record-count:" + records.length + "\n" + String.join("\n", records) + "\n");
        return new PitReader().parseWithResults(path);
    }

    /**
     * {@code parse_pit_room}, {@code parse_pit_alloc}, {@code parse_pit_obj_rarity} and
     * {@code parse_pit_innate_freq} each assign their fields, so a second line replaces the first.
     */
    @Test
    void aRepeatedScalarDirectiveKeepsTheLastValue() throws Exception {
        ParseResult<PitProfile> result = load("repeat-scalars.txt", String.join("\n",
                "name:Twice",
                "room:1", "room:3",
                "alloc:1:2", "alloc:5:60",
                "obj-rarity:1", "obj-rarity:7",
                "innate-freq:10", "innate-freq:50"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        PitProfile pit = result.items().getFirst();
        assertEquals(PitRoomType.PIT_TYPE_OTHER, field(pit, "roomType"));
        int rarity = field(pit, "rarity");
        int ave = field(pit, "ave");
        int objectRarity = field(pit, "objectRarity");
        int freqInnate = field(pit, "freqInnate");
        assertEquals(5, rarity);
        assertEquals(60, ave);
        assertEquals(7, objectRarity);
        assertEquals(2, freqInnate);
    }

    /**
     * {@code flags-req}, {@code flags-ban}, {@code spell-req} and {@code spell-ban} set bits in the
     * profile's bit set, and {@code mon-base}, {@code mon-ban} and {@code color} push onto a list, so a
     * repeated line adds to what is there. A flag named on two lines is one bit, not two.
     */
    @Test
    void aRepeatedListDirectiveAccumulates() throws Exception {
        ParseResult<PitProfile> result = load("repeat-lists.txt", String.join("\n",
                "name:Lists",
                "flags-req:ANIMAL", "flags-req:EVIL | ANIMAL",
                "flags-ban:UNIQUE", "flags-ban:UNDEAD",
                "spell-req:BR_ACID", "spell-req:BR_FIRE",
                "spell-ban:BR_COLD", "spell-ban:BR_POIS | BR_COLD",
                "mon-base:orc", "mon-base:troll",
                "mon-ban:Horned Reaper", "mon-ban:Grip, Farmer Maggot's Dog",
                "color:r", "color:g"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        PitProfile pit = result.items().getFirst();
        Flag<MonsterRaceFlag> flags = field(pit, "flags");
        Flag<MonsterRaceFlag> forbiddenFlags = field(pit, "forbiddenFlags");
        Flag<MonsterSpell> spells = field(pit, "spellsFlags");
        Flag<MonsterSpell> forbiddenSpells = field(pit, "forbiddenSpellFlags");
        assertEquals(2, flags.count());
        assertEquals(2, forbiddenFlags.count());
        assertEquals(2, spells.count());
        assertEquals(2, forbiddenSpells.count());
        assertEquals(2, ((List<?>) field(pit, "bases")).size());
        assertEquals(2, ((List<?>) field(pit, "forbiddenMonsters")).size());
        assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN), field(pit, "colours"));
    }

    /**
     * C pushes each base, colour and forbidden monster on the head of its list, so it holds them in
     * reverse file order; the Java lists are in file order. {@code mon_pit_hook} only asks whether a
     * race matches some entry, so the difference is not observable, and this pins the Java order so
     * it is not rediscovered as a mismatch.
     */
    @Test
    void listsInsideAProfileAreInFileOrderHereWhereCHoldsThemReversed() throws Exception {
        ParseResult<PitProfile> result = load("list-order.txt", String.join("\n",
                "name:Order",
                "mon-base:orc", "mon-base:troll", "mon-base:giant",
                "color:r", "color:g", "color:b"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        List<MonsterBase> bases = field(result.items().getFirst(), "bases");
        assertEquals(List.of("orc", "troll", "giant"), bases.stream().map(MonsterBase::getCodeName).toList());
        assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_BLUE),
                field(result.items().getFirst(), "colours"));
    }

    /**
     * {@code parse_pit_name} never looks for an existing record of the same name, so two records called
     * the same both load, in file order.
     */
    @Test
    void twoRecordsWithTheSameNameBothLoad() throws Exception {
        ParseResult<PitProfile> result = load("dup-names.txt",
                "name:Same\nalloc:1:10", "name:Same\nalloc:2:20");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("Same", "Same"), names(result));
        int first = field(result.items().get(0), "ave");
        int second = field(result.items().get(1), "ave");
        assertEquals(10, first);
        assertEquals(20, second);
    }

    /**
     * {@code lookup_monster} matches the exact name case-insensitively (and otherwise the first name
     * containing the text), so a {@code mon-ban:} in upper case bans the race {@code monster.txt}
     * spells in lower case.
     */
    @Test
    void aMonBanNameMatchesCaseInsensitivelyAsLookupMonsterDoes() throws Exception {
        ParseResult<PitProfile> result = load("ban-case.txt", "name:Case\nmon-ban:HORNED REAPER");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        List<MonsterRace> banned = field(result.items().getFirst(), "forbiddenMonsters");
        assertEquals("horned reaper", banned.getFirst().getName());
    }

    /**
     * {@code lookup_monster_base} compares with {@code streq}, so {@code mon-base:Orc} names no base.
     * C stops the load with {@code PARSE_ERROR_UNRECOGNISED_TVAL}, and so does the port.
     */
    @Test
    void aMonBaseNameIsCaseSensitive() throws Exception {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("base-case.txt", "name:Case\nmon-base:Orc", GOOD_RECORD));

        assertTrue(e.getMessage().endsWith("an illegal monster base value: Orc"), e.getMessage());
    }

    // ---- Where the grammar is stricter than C ----------------------------
    // Each of these loads in C. They are pinned so the difference is on record; see docs/grammar_issues.md.

    /**
     * {@code flags-req ?str flags}: the value is optional, and {@code parse_pit_flags_req} returns
     * without error when it is absent. The grammar needs at least one code after the colon.
     */
    @Test
    void anEmptyFlagListIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<PitProfile> result = load("empty-flags.txt", "name:Empty\nflags-req:", GOOD_RECORD);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * The handlers split the value with {@code strtok(flags, " |")}, so a space separates codes as well
     * as a bar, and leading, trailing and doubled separators are skipped. The lexer only knows
     * {@code FLAG_OR}, a bar with an optional space either side.
     */
    @Test
    void spaceSeparatedOrPaddedFlagListsAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        for (String line : List.of("flags-req:ANIMAL EVIL", "flags-req: ANIMAL", "flags-req:ANIMAL ",
                "flags-req:ANIMAL |", "spell-ban:BR_FIRE BR_COLD")) {
            ParseResult<PitProfile> result = load("padded-flags.txt", "name:Padded\n" + line);

            assertTrue(result.hasErrors(), line);
            assertTrue(result.items().isEmpty(), line);
        }
    }

    /**
     * {@code parse_pit_color} takes a colour name longer than one character through
     * {@code color_text_to_attr}, which matches {@code color_table} names case-insensitively. The
     * lexer takes one letter only.
     */
    @Test
    void aColourNameIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<PitProfile> result = load("colour-name.txt", "name:Named\ncolor:Red");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * {@code room}, {@code alloc} and {@code obj-rarity} are {@code uint} in {@code init_parse_pit}, and
     * {@code parser_parse} refuses a token that starts with '-'. The lexer takes a leading minus, and
     * the assembler stops the load on it, in each of the four fields.
     */
    @Test
    void aNegativeUnsignedValueStopsTheLoad() {
        for (String line : List.of("room:-1", "alloc:-2:3", "alloc:2:-3", "obj-rarity:-4")) {
            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> load("negative.txt", "name:Negative\n" + line), line);

            assertTrue(e.getMessage().startsWith("Pit record starting at line: 2 of file: "), e.getMessage());
        }
    }

    /**
     * {@code finish_parse_pit} copes with a chain of no records ({@code pit_max} becomes 1 and
     * {@code pit_info} holds the blank slot), so an empty file loads. The grammar needs one record.
     */
    @Test
    void aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing() throws IOException {
        String path = tempFile("no-records.txt", "record-count:0\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    // ---- Hard errors, which C also treats as fatal -----------------------

    /**
     * A directive before the first {@code name:} has no record to fill. C's handlers return
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}; the grammar has no rule that starts with anything but
     * {@code name:}.
     */
    @Test
    void aDirectiveBeforeAnyNameIsAHardError() throws IOException {
        String path = tempFile("no-header.txt", "record-count:1\nroom:1\n" + GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * {@code name str name} is not optional, so C refuses a {@code name:} line with nothing after it
     * ({@code PARSE_ERROR_MISSING_FIELD}); so does the grammar.
     */
    @Test
    void aNameLineWithNoNameIsAHardError() throws IOException {
        ParseResult<PitProfile> result = load("empty-name.txt", "name:\nroom:1");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * {@code alloc uint rarity uint level} needs both fields; with one, C returns
     * {@code PARSE_ERROR_MISSING_FIELD}.
     */
    @Test
    void anAllocLineWithOneNumberIsAHardError() throws IOException {
        ParseResult<PitProfile> result = load("alloc-one.txt", "name:One\nalloc:3");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * {@code grab_flag} compares with {@code streq} against the upper-case names in
     * {@code r_info_flags}, so a lower-case code names no flag and C stops the load with
     * {@code PARSE_ERROR_INVALID_FLAG}. The lexer's flag alphabet is upper case, so the file fails
     * closed here.
     */
    @Test
    void aLowerCaseFlagCodeIsAHardError() throws IOException {
        ParseResult<PitProfile> result = load("lower-flag.txt", "name:Lower\nflags-req:animal");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * The {@code record-count:} header is the Java port's own and C has no such line, so the driver's
     * soft check cannot help when it is missing: the grammar's first rule is the header, and a file
     * without one is a syntax error that loads nothing.
     */
    @Test
    void aMissingRecordCountHeaderIsAHardError() throws IOException {
        String path = tempFile("no-count.txt", GOOD_RECORD + "\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * {@code parser_parse} skips blank lines and lines starting with '#' wherever they fall, and a file
     * with no final newline or with Windows line ends is read like any other.
     */
    @Test
    void blankLinesCommentsCrlfAndNoFinalNewlineAreAllRead() throws Exception {
        String text = "# header\r\n\r\nrecord-count:1\r\n\r\n# note\r\nname:Plain\r\n\r\nroom:2\r\n# tail\r\nalloc:3:4";

        ParseResult<PitProfile> result = new PitReader().parseWithResults(tempFile("layout.txt", text));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("Plain"), names(result));
        assertEquals(PitRoomType.PIT_TYPE_NEST, field(result.items().getFirst(), "roomType"));
        int ave = field(result.items().getFirst(), "ave");
        assertEquals(4, ave);
    }

    /**
     * A hard error and a soft error in the same file: the hard one wins, and the result is empty with
     * its message, as the extractor's ordering promises.
     */
    @Test
    void aHardErrorDiscardsTheItemsAndTheCountMismatchIsNotAlsoReported() throws IOException {
        String path = tempFile("hard-and-count.txt",
                "record-count:9\n" + GOOD_RECORD + "\nname:Broken\nalloc:3\n");

        ParseResult<PitProfile> result = new PitReader().parseWithResults(path);

        assertTrue(result.items().isEmpty(), result.items()::toString);
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().noneMatch(e -> e.contains("record-count header declares")),
                result.errors()::toString);
    }
}
