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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@code freq_innate} and {@code freq_spell} as C's {@code parse_monster_innate_freq},
 * {@code parse_monster_spell_freq} and {@code parse_monster_spells} ({@code mon-init.c}) leave them.
 *
 * <p>C's rule: an {@code innate-freq:} or {@code spell-freq:} line of {@code pct} stores
 * {@code 100 / pct}, a percentage outside 1 to 100 is a parse error, and a field with no line is
 * 0 unless the race has a spell of the matching kind, in which case it is 4. The innate kind is any
 * spell tagged {@code RST_INNATE} in {@code list-mon-spells.h}; the spell kind is any spell that is
 * neither innate nor a breath (every breath is also innate).
 *
 * <p>The expected values here are computed from the raw lines of the real {@code monster.txt} and
 * from the 27 innate spell names transcribed from {@code list-mon-spells.h}, not from the Java
 * {@code MonsterSpell} table, so a mis-filed category in the enum fails the test.
 *
 * @author Rowan Crowther
 */
class MonsterAssemblerFrequencyTest {

    private static final String MONSTER_FILE = "lib/gamedata/monster.txt";

    /**
     * Every spell tagged {@code RST_INNATE} in C's {@code list-mon-spells.h}.
     */
    private static final Set<String> INNATE = Set.of(
            "SHRIEK", "WHIP", "SPIT", "SHOT", "ARROW", "BOLT",
            "BR_ACID", "BR_ELEC", "BR_FIRE", "BR_COLD", "BR_POIS", "BR_NETH", "BR_LIGHT", "BR_DARK",
            "BR_SOUN", "BR_CHAO", "BR_DISE", "BR_NEXU", "BR_TIME", "BR_INER", "BR_GRAV", "BR_SHAR",
            "BR_PLAS", "BR_WALL", "BR_MANA", "BOULDER", "WEAVE");

    private static ParseResult<MonsterRace> real;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void bootstrap() throws IOException {
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
        real = new MonsterReader().parseWithResults(MONSTER_FILE);
    }

    @AfterAll
    static void cleanup() throws Exception {
        ObjectRegistry.reset();
    }

    private static int intField(MonsterRace race, String name) throws Exception {
        Field f = MonsterRace.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.getInt(race);
    }

    /**
     * One race's frequency-relevant lines, read straight from the data file.
     */
    private record Expected(String name, int innate, int spell) {
    }

    /**
     * Applies C's rule to the raw lines of a monster.txt-format file.
     */
    private static List<Expected> expectedFrom(List<String> lines) {
        List<Expected> out = new ArrayList<>();
        String name = null;
        int innatePct = 0;
        int spellPct = 0;
        boolean innateSpell = false;
        boolean otherSpell = false;
        for (int i = 0; i <= lines.size(); i++) {
            String line = i < lines.size() ? lines.get(i) : "name:";
            if (line.startsWith("name:")) {
                if (name != null) {
                    int innate = innatePct != 0 ? 100 / innatePct : (innateSpell ? 4 : 0);
                    int spell = spellPct != 0 ? 100 / spellPct : (otherSpell ? 4 : 0);
                    out.add(new Expected(name, innate, spell));
                }
                name = line.substring(5).trim();
                innatePct = 0;
                spellPct = 0;
                innateSpell = false;
                otherSpell = false;
            } else if (line.startsWith("innate-freq:")) {
                innatePct = Integer.parseInt(line.substring(12).trim());
            } else if (line.startsWith("spell-freq:")) {
                spellPct = Integer.parseInt(line.substring(11).trim());
            } else if (line.startsWith("spells:")) {
                for (String token : line.substring(7).split("[ |]+")) {
                    if (token.isEmpty()) continue;
                    if (INNATE.contains(token)) innateSpell = true;
                    else otherSpell = true;
                }
            }
        }
        return out;
    }

    @Test
    @DisplayName("every race in the real monster.txt stores C's freq_innate and freq_spell")
    void everyRealRaceMatchesC() throws Exception {
        Map<String, MonsterRace> byName = new HashMap<>();
        for (MonsterRace r : real.items()) {
            byName.put(r.getName(), r);
        }
        List<String> mismatches = new ArrayList<>();
        int checked = 0;
        for (Expected e : expectedFrom(Files.readAllLines(Path.of(MONSTER_FILE)))) {
            MonsterRace race = byName.get(e.name());
            if (race == null) {
                mismatches.add(e.name() + ": not loaded");
                continue;
            }
            checked++;
            int innate = intField(race, "freqInnate");
            int spell = intField(race, "freqSpell");
            if (innate != e.innate() || spell != e.spell()) {
                mismatches.add(e.name() + ": C innate/spell " + e.innate() + "/" + e.spell()
                        + ", Java " + innate + "/" + spell);
            }
        }
        assertTrue(checked > 600, "expected to compare most of the 624 races, compared " + checked);
        assertTrue(mismatches.isEmpty(), mismatches.size() + " of " + checked + " differ, first ones: "
                + mismatches.subList(0, Math.min(10, mismatches.size())));
    }

    private ParseResult<MonsterRace> load(String content) throws IOException {
        Path file = tempDir.resolve("freq.txt");
        Files.writeString(file, "record-count:1\nname:test\nbase:townsfolk\ncolor:w\nglyph:t\n" + content);
        return new MonsterReader().parseWithResults(file.toString());
    }

    @Test
    @DisplayName("spell-freq:5 stores 20 and innate-freq:25 stores 4")
    void percentageIsDividedInto100() throws Exception {
        ParseResult<MonsterRace> r = load("innate-freq:25\nspell-freq:5\nspells:BLINK | BR_FIRE\n");
        assertTrue(r.errors().isEmpty(), r.errors().toString());
        assertEquals(4, intField(r.items().get(0), "freqInnate"));
        assertEquals(20, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("freq 100 stores 1 and freq 1 stores 100")
    void boundaryPercentages() throws Exception {
        ParseResult<MonsterRace> r = load("innate-freq:100\nspell-freq:1\nspells:BLINK | BR_FIRE\n");
        assertEquals(1, intField(r.items().get(0), "freqInnate"));
        assertEquals(100, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("a race with no spells and no freq lines stores 0 and 0")
    void noSpellsMeansZero() throws Exception {
        ParseResult<MonsterRace> r = load("");
        assertEquals(0, intField(r.items().get(0), "freqInnate"));
        assertEquals(0, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("only a breath: innate defaults to 4, spell stays 0")
    void onlyABreath() throws Exception {
        ParseResult<MonsterRace> r = load("spells:BR_FIRE\n");
        assertEquals(4, intField(r.items().get(0), "freqInnate"));
        assertEquals(0, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("only a ball spell: spell defaults to 4, innate stays 0")
    void onlyABall() throws Exception {
        ParseResult<MonsterRace> r = load("spells:BA_FIRE\n");
        assertEquals(0, intField(r.items().get(0), "freqInnate"));
        assertEquals(4, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("a bolt spell that is not innate (BO_FIRE) counts as a spell, not an innate attack")
    void nonInnateBoltIsASpell() throws Exception {
        ParseResult<MonsterRace> r = load("spells:BO_FIRE\n");
        assertEquals(0, intField(r.items().get(0), "freqInnate"));
        assertEquals(4, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("one innate and one ordinary spell, no freq lines: both default to 4")
    void mixedSpellsDefaultBoth() throws Exception {
        ParseResult<MonsterRace> r = load("spells:BR_FIRE | BLINK\n");
        assertEquals(4, intField(r.items().get(0), "freqInnate"));
        assertEquals(4, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("a spell set with the matching kind in the middle still defaults both")
    void matchingKindInTheMiddle() throws Exception {
        ParseResult<MonsterRace> r = load("spells:SHRIEK | BLINK | BR_FIRE | HEAL | BA_FIRE\n");
        assertEquals(4, intField(r.items().get(0), "freqInnate"));
        assertEquals(4, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("an explicit innate-freq with an ordinary spell: innate keeps its value, spell defaults to 4")
    void explicitOneDefaultsTheOther() throws Exception {
        ParseResult<MonsterRace> r = load("innate-freq:10\nspells:BR_FIRE | BLINK\n");
        assertEquals(10, intField(r.items().get(0), "freqInnate"));
        assertEquals(4, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("a percentage outside 1 to 100 ends the load, as PARSE_ERROR_INVALID_SPELL_FREQ does")
    void outOfRangeIsAnError() {
        DataFileParseException innate = assertThrows(DataFileParseException.class,
                () -> load("innate-freq:0\nspells:BR_FIRE\n"));
        assertTrue(innate.getMessage().contains("illegal out of range innate frequency value: 0"),
                innate::getMessage);
        DataFileParseException spell = assertThrows(DataFileParseException.class,
                () -> load("spell-freq:101\nspells:BLINK\n"));
        assertTrue(spell.getMessage().contains("illegal out of range spell frequency value: 101"),
                spell::getMessage);
    }

    @Test
    @DisplayName("the edges 1 and 100 are the last values C accepts, giving 100 and 1")
    void theEdgesOfTheRangeAreAccepted() throws Exception {
        ParseResult<MonsterRace> r = load("innate-freq:1\nspell-freq:100\nspells:BR_FIRE | BLINK\n");
        assertEquals(100, intField(r.items().get(0), "freqInnate"));
        assertEquals(1, intField(r.items().get(0), "freqSpell"));
    }

    @Test
    @DisplayName("a negative percentage is outside 1 to 100 and ends the load")
    void aNegativePercentageIsAnError() {
        assertThrows(DataFileParseException.class, () -> load("innate-freq:-5\nspells:BR_FIRE\n"));
    }

    @Test
    @DisplayName("spells:MAX is accepted, as C's lookup_flag finds the MAX row, and counts as a plain spell")
    void theMaxRowCountsAsAPlainSpell() throws Exception {
        // RSF_MAX has no type, so it is neither innate nor a breath: parse_monster_spells defaults
        // freq_spell to 4 and leaves freq_innate at 0.
        ParseResult<MonsterRace> r = load("spells:MAX\n");
        assertEquals(0, intField(r.items().get(0), "freqInnate"));
        assertEquals(4, intField(r.items().get(0), "freqSpell"));
    }
}
