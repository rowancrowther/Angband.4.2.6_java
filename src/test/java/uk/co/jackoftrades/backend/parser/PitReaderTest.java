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
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.cave.PitProfile;
import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.io.IOException;
import java.lang.reflect.Field;
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
        GameConstants.objectKinds.clear();
        Field f = GameConstants.class.getDeclaredField("kindsByTvalSval");
        f.setAccessible(true);
        ((java.util.Map<?, ?>) f.get(null)).clear();
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(PitProfile target, String name) throws Exception {
        Field f = PitProfile.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    /**
     * Reads a private static field off {@link GameConstants} (no getter exists for the pit registry).
     */
    @SuppressWarnings("unchecked")
    private static <T> T staticField(String name) throws Exception {
        Field f = GameConstants.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(null);
    }

    private static PitProfile byName(List<PitProfile> pits, String name) throws Exception {
        for (PitProfile p : pits) {
            if (name.equals(field(p, "name"))) return p;
        }
        return null;
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
        List<ColourType> cols = field(warriors, "colours");
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
}
