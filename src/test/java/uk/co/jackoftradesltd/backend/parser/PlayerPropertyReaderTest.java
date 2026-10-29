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
import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyAssembler;
import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyParseRecord;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
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
 * one {@link PlayerProperty} per real {@link ElementEnum} (C: {@code finish_parse_player_prop}
 * in {@code init.c}), so the file's 44 declared records yield more than 44 assembled
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
 * <p>The tests from "the contract of the reader itself" onward were added on 261018 for the port
 * check of {@link PlayerPropertyReader} against {@code run_parse_player_prop} and the parsers of
 * {@code init.c}. {@link #UPSTREAM} is the 44-record table worked out from the upstream
 * {@code player_property.txt}, with each code's position in C's flag list taken from
 * {@code list-player-flags.h} and {@code list-object-flags.h}; the shipped file is checked against it
 * record by record, element expansion included. The tests named "...HereWhereC..." pin a place where
 * the grammar or assembler differs from C, written up in {@code docs/grammar_issues.md} and
 * {@code docs/Assembler_Issues.md}.
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
        // finish_parse_player_prop in init.c). Spot-check the COLD expansion: name/desc
        // built from the projection's own name ("cold"), capitalised only on its first letter,
        // plus this record's own name/desc text - "Cold Resistance" / "You resist cold.",
        // matching C's format("%s %s", capitalised, ability.name) /
        // format("%s %s.", ability.desc, name). The bindui is re-suffixed with the element's own
        // code, <COLD>, not the projection's display name.
        PlayerProperty coldResistance = byName(result.items(), "Cold Resistance");
        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT, coldResistance.getPlayerPropertyType());
        assertEquals(ElementEnum.ELEM_COLD, coldResistance.geteCode());
        assertEquals(1, coldResistance.getValue());
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
        assertEquals(1, coldResistance.getValue());
        assertEquals("You resist cold.", coldResistance.getDescription());

        PlayerProperty fireImmunity = byName(result.items(), "Fire Immunity");
        assertEquals(3, fireImmunity.getValue());
        assertEquals("You are immune to fire.", fireImmunity.getDescription());

        PlayerProperty poisonVulnerability = byName(result.items(), "Poison Vulnerability");
        assertEquals(-1, poisonVulnerability.getValue());
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
    void anUnresolvableObjectCodeIsAHardErrorAsInC() {
        // "NOT_A_REAL_FLAG" is not in list_obj_flag_names, so code_index_in_array returns -1 and
        // C ends the load with PARSE_ERROR_INVALID_PLAY_PROP_CODE.
        assertAssemblerRefuses("record-count:1\ntype:object\ncode:NOT_A_REAL_FLAG\nname:Bogus\n",
                "starting at line: 2", "illegal object flag value: NOT_A_REAL_FLAG");
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
    void anAssemblerRefusalIsTheOnlyThingReportedAndSkipsTheRecordCountCheck() {
        // The count says 5 and the file holds 1, but the bad binding value ends the load first.
        DataFileParseException thrown = assertAssemblerRefuses(
                "record-count:5\ntype:player\ncode:ROCK\nbindui:also_bad:0:notanumber\nname:Rock\n",
                "invalid bindui value: notanumber");
        assertFalse(thrown.getMessage().contains("declares"), thrown::getMessage);
    }

    // ---- the contract of the reader itself ----------------------------------------------------

    /** One {@code player_property.txt} record, worked out from the upstream file. */
    private record Row(String type, String code, int cIndex, String name, String desc, int value,
                       List<String> binds) {
    }

    private static Row row(String type, String code, int cIndex, String name, String desc, String value,
                           String... binds) {
        return new Row(type, code, cIndex, name, desc, Integer.parseInt(value), List.of(binds));
    }

    /**
     * The 44 records of the upstream {@code lib/gamedata/player_property.txt}, in file order. The
     * {@code cIndex} is the position of the code in C's {@code player_info_flags} (for {@code player})
     * or {@code list_obj_flag_names} (for {@code object}, whose list starts with {@code NONE}), worked
     * out from {@code list-player-flags.h} and {@code list-object-flags.h}; it is -1 where there is no
     * code. A {@code bindui} is {@code name:aux:value}.
     */
    private static final List<Row> UPSTREAM = List.of(
            row("player", "ROCK", 13, "Rock", "You are made of rock.", "0"),
            row("player", "KNOW_MUSHROOM", 7, "Mushroom Lore", "You easily recognize mushrooms.", "0"),
            row("player", "KNOW_ZAPPER", 8, "Device Lore", "You easily recognize magic devices.", "0"),
            row("player", "SEE_ORE", 9, "Miner", "You can sense ore in the walls.", "0"),
            row("player", "FAST_SHOT", 1, "Extra Shots", "Your shooting speed improves every 3 levels.", "0", "shots_ui_compact_0:0:special"),
            row("player", "BRAVERY_30", 2, "Relentless [30]", "You become immune to fear at level 30.", "0", "pfear_ui_compact_0:0:special"),
            row("player", "BLESS_WEAPON", 3, "Blessed Fighter", "You get improved melee with blessed or hafted weapons.", "0"),
            row("player", "ZERO_FAIL", 4, "Full Spellcaster", "You may obtain a perfect success rate with magic.", "0"),
            row("player", "BEAM", 5, "Extra Spell Beaming", "Your bolt spells frequently turn into beams.", "0"),
            row("player", "CHOOSE_SPELLS", 6, "Spell Choice", "You may choose your own spells to study.", "0"),
            row("player", "NO_MANA", 10, "No Magic", "You cannot cast spells.", "0"),
            row("player", "CHARM", 11, "Charms", "You are extra persuasive to monsters.", "0"),
            row("player", "UNLIGHT", 12, "Unlight", "You gain stealth in, can see in, and resist the dark.", "0", "resist_ui_compact_0<DARK>:0:1"),
            row("player", "STEAL", 14, "Pickpocketing", "You can steal from monsters.", "0"),
            row("player", "SHIELD_BASH", 15, "Shield Bash", "You can bash monsters with a shield in melee.", "0"),
            row("player", "EVIL", 16, "Evil", "You resist nether, but are hurt by holy attacks.", "0", "resist_ui_compact_0<NETHER>:0:1"),
            row("player", "COMBAT_REGEN", 17, "Combat Regeneration", "You draw power from the thrill of combat, represented by Spell Points (SP).  You gain SP when damaged by an enemy or when you attack in melee.  As your blood cools your power fades; you lose SP at half the rate other classes gain them.  When you spend SP by casting a spell you regain some health, and fading SP replenish your health more efficiently.  The more damaged you are, the bigger these health gains will be.", "0", "imphp_ui_compact_0:0:1"),
            row("object", "SUST_STR", 1, "Sustain Strength", "Your strength is sustained.", "0", "stat_mod_ui_compact_0<STR>:1:1"),
            row("object", "SUST_INT", 2, "Sustain Intelligence", "Your intelligence is sustained.", "0", "stat_mod_ui_compact_0<INT>:1:1"),
            row("object", "SUST_WIS", 3, "Sustain Wisdom", "Your wisdom is sustained.", "0", "stat_mod_ui_compact_0<WIS>:1:1"),
            row("object", "SUST_DEX", 4, "Sustain Dexterity", "Your dexterity is sustained.", "0", "stat_mod_ui_compact_0<DEX>:1:1"),
            row("object", "SUST_CON", 5, "Sustain Constitution", "Your constitution is sustained.", "0", "stat_mod_ui_compact_0<CON>:1:1"),
            row("object", "PROT_FEAR", 6, "Fear Immunity", "You are immune to fear.", "0", "pfear_ui_compact_0:0:1"),
            row("object", "PROT_BLIND", 7, "Blindness Immunity", "You cannot be blinded.", "0", "pblind_ui_compact_0:0:1"),
            row("object", "PROT_CONF", 8, "Confusion Immunity", "You cannot be confused.", "0", "pconf_ui_compact_0:0:1"),
            row("object", "PROT_STUN", 9, "Stun Immunity", "You cannot be stunned.", "0", "pstun_ui_compact_0:0:1"),
            row("object", "SLOW_DIGEST", 10, "Slow Digestion", "You have slow metabolism.", "0", "slow_digestion_ui_compact_0:0:1"),
            row("object", "FEATHER", 11, "Feather Falling", "You fall lightly.", "0", "feather_falling_ui_compact_0:0:1"),
            row("object", "REGEN", 12, "Regeneration", "You regenerate quickly.", "0", "regen_ui_compact_0:0:1"),
            row("object", "TELEPATHY", 13, "Telepathy", "You have telepathy.", "0", "esp_ui_compact_0:0:1"),
            row("object", "SEE_INVIS", 14, "See Invisible", "You can see invisible creatures.", "0", "see_invis_ui_compact_0:0:1"),
            row("object", "FREE_ACT", 15, "Free Action", "You are immune to paralysis.", "0", "free_action_ui_compact_0:0:1"),
            row("object", "HOLD_LIFE", 16, "Hold Life", "Your life force is sustained.", "0", "holdlife_ui_compact_0:0:1"),
            row("object", "IMPACT", 17, "Earthquakes", "You sometimes create earthquakes on impact.", "0"),
            row("object", "IMPAIR_HP", 22, "Slow Regeneration", "Your hitpoint recovery is impaired.", "0", "imphp_ui_compact_0:0:1"),
            row("object", "IMPAIR_MANA", 23, "Slow Mana Regeneration", "Your mana recovery is impaired.", "0", "impsp_ui_compact_0:0:1"),
            row("object", "AFRAID", 24, "Constant Fear", "You are afraid of melee, and bad at shooting and casting spells.", "0", "fear_ui_compact_0:0:1"),
            row("object", "NO_TELEPORT", 25, "Teleport Ban", "You cannot teleport.", "0", "noteleport_ui_compact_0:0:1"),
            row("object", "AGGRAVATE", 26, "Aggravate", "You aggravate creatures nearby.", "0", "aggravate_ui_compact_0:0:1"),
            row("object", "DRAIN_EXP", 27, "Experience Drain", "Your experience constantly drains away.", "0", "drainxp_ui_compact_0:0:1"),
            row("object", "TRAP_IMMUNE", 36, "Trap Immune", "You are immune to traps.", "0", "trap_immunity_ui_compact_0:0:1"),
            row("element", "", -1, "Resistance", "You resist", "1", "resist_ui_compact_0:0:1"),
            row("element", "", -1, "Immunity", "You are immune to", "3", "resist_ui_compact_0:0:3"),
            row("element", "", -1, "Vulnerability", "You are vulnerable to", "-1", "resist_ui_compact_0:0:-1")
    );

    /** C's {@code list_element_names} (from {@code list-elements.h}), in order. */
    private static final List<String> C_ELEMENTS = List.of("ACID", "ELEC", "FIRE", "COLD", "POIS", "LIGHT",
            "DARK", "SOUND", "SHARD", "NEXUS", "NETHER", "CHAOS", "DISEN", "WATER", "ICE", "GRAVITY",
            "INERTIA", "FORCE", "TIME", "PLASMA", "METEOR", "MISSILE", "MANA", "HOLY_ORB", "ARROW");

    /** The first 25 {@code name:} lines of the upstream {@code projection.txt}, which C indexes in step with the elements. */
    private static final List<String> C_PROJECTION_NAMES = List.of("acid", "lightning", "fire", "cold",
            "poison", "light", "dark", "sound", "shards", "nexus", "nether", "chaos", "disenchantment", "water",
            "ice", "gravity", "inertia", "force", "time", "plasma", "meteors", "magic missiles", "mana",
            "holy power", "arrows");

    private ParseResult<PlayerProperty> load(String content) throws IOException {
        return new PlayerPropertyReader().parseWithResults(tempFile("case.txt", content));
    }

    /**
     * The assembler refuses a field C refuses by throwing, and the reader lets the exception
     * through, so the caller sees the message rather than a {@link ParseResult}.
     */
    private DataFileParseException assertAssemblerRefuses(String content, String... fragments) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> load(content));
        for (String fragment : fragments) {
            assertTrue(thrown.getMessage().contains(fragment), thrown::getMessage);
        }
        return thrown;
    }

    private void assertHardError(String content) throws IOException {
        ParseResult<PlayerProperty> result = load(content);
        assertTrue(result.items().isEmpty(), () -> "expected no properties but got " + result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        PlayerPropertyReader reader = new PlayerPropertyReader();
        List<PlayerProperty> viaParse = reader.parse(REAL_FILE);
        List<PlayerProperty> viaResults = reader.parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.size(), viaParse.size());
        for (int i = 0; i < viaParse.size(); i++) {
            assertEquals(viaResults.get(i).getName(), viaParse.get(i).getName());
        }
    }

    @Test
    void parseDiscardsTheErrorChannel() throws IOException {
        // A hard grammar error: parse() cannot tell it from an empty file.
        assertEquals(List.of(), new PlayerPropertyReader().parse(tempFile("no-header.txt", "type:player\ncode:ROCK\nname:Rock\n")));
    }

    @Test
    void aMissingFileIsAnIoExceptionWhereCQuits() {
        String missing = tempDir.resolve("no-such-file.txt").toString();

        assertThrows(IOException.class, () -> new PlayerPropertyReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new PlayerPropertyReader().parse(missing));
    }

    @Test
    void theShippedFileDiffersFromUpstreamInTheRecordCountHeaderAndThreeCommentLines() throws IOException {
        // lib/gamedata/player_property.txt carries "record-count:44" where the upstream file has no
        // header, and "gameEngine" for "game" in three comment lines. The record lines are identical,
        // so the table below, worked out from the upstream file, applies to it.
        String shipped = Files.readString(Path.of(REAL_FILE));

        assertTrue(shipped.contains("record-count:44"));
        assertEquals(44, shipped.lines().filter(l -> l.startsWith("type:")).count());
        assertEquals(3, shipped.lines().filter(l -> l.startsWith("#") && l.contains("gameEngine")).count());
    }

    /**
     * Every record of the shipped file agrees with the upstream table: the 41 {@code player} and
     * {@code object} records load one property each, in file order, and each of the three
     * {@code element} records loads 25, one per element in C's {@code list_element_names} order,
     * 116 in all. An element's name is the capitalized projection name and the record's own name, its
     * description is the record's text, a space, the projection name and a full stop, and each binding
     * gains {@code <ELEMENT>}.
     */
    @Test
    void everyShippedRecordAgreesWithTheUpstreamTable() throws IOException {
        List<PlayerProperty> items = new PlayerPropertyReader().parseWithResults(REAL_FILE).items();

        List<String> expected = new java.util.ArrayList<>();
        List<String> actual = new java.util.ArrayList<>();
        for (PlayerProperty p : items) {
            actual.add(describe(p));
        }
        for (Row r : UPSTREAM) {
            if (r.type().equals("element")) {
                for (int i = 0; i < C_ELEMENTS.size(); i++) {
                    String tag = "<" + C_ELEMENTS.get(i) + ">";
                    String proj = C_PROJECTION_NAMES.get(i);
                    String name = Character.toUpperCase(proj.charAt(0)) + proj.substring(1) + " " + r.name();
                    expected.add(line("PROP_TYPE_ELEMENT", "PF_NONE", "OF_NONE", "ELEM_" + C_ELEMENTS.get(i), name,
                            r.desc() + " " + proj + ".", r.value(),
                            r.binds().stream().map(b -> bind(b, tag)).toList()));
                }
            } else {
                boolean player = r.type().equals("player");
                expected.add(line(player ? "PROP_TYPE_PLAYER" : "PROP_TYPE_OBJECT",
                        player ? "PF_" + r.code() : "PF_NONE", player ? "OF_NONE" : "OF_" + r.code(), "ELEM_NONE",
                        r.name(), r.desc(), 0, r.binds().stream().map(b -> bind(b, "")).toList()));
            }
        }

        assertEquals(116, items.size());
        assertEquals(expected, actual);
    }

    private static String bind(String spec, String tag) {
        String[] f = spec.split(":");
        boolean special = f[2].equals("special");
        return f[0] + tag + "/" + (special ? 0 : Integer.parseInt(f[2])) + "/" + special + "/" + !f[1].equals("0");
    }

    private static String line(String type, String p, String o, String e, String name, String desc, int value,
                               List<String> binds) {
        return String.join("|", type, p, o, e, name, desc, String.valueOf(value), binds.toString());
    }

    private static String describe(PlayerProperty p) {
        return line(p.getPlayerPropertyType().name(), p.getpCode().name(), p.getoCode().name(), p.geteCode().name(),
                p.getName(), p.getDescription(), p.getValue(),
                p.getEntries().stream()
                        .map(b -> b.uiEntry() + "/" + b.value() + "/" + b.special() + "/" + b.aux()).toList());
    }

    /**
     * The position of a code in C's {@code player_info_flags} or {@code list_obj_flag_names} is the
     * property's {@code index}; the Java flag enums carry the same constants in the same order, so the
     * ordinal has to agree.
     */
    @Test
    void everyShippedCodeHasTheOrdinalOfItsPositionInCsFlagList() throws IOException {
        List<PlayerProperty> items = new PlayerPropertyReader().parseWithResults(REAL_FILE).items();

        int checked = 0;
        for (Row r : UPSTREAM) {
            if (r.type().equals("element")) {
                continue;
            }
            PlayerProperty p = items.stream().filter(x -> r.name().equals(x.getName())).findFirst().orElseThrow();
            int ordinal = r.type().equals("player") ? p.getpCode().ordinal() : p.getoCode().ordinal();
            assertEquals(r.cIndex(), ordinal, r.code());
            checked++;
        }
        assertEquals(41, checked);
    }

    @Test
    void anElementRecordCountsOnceTowardsTheRecordCount() throws IOException {
        // record-count: counts type: lines, so one element record is one record although it expands
        // to 25 properties.
        ParseResult<PlayerProperty> result = load("record-count:1\ntype:element\nname:Resistance\ndesc:You resist\nvalue:1\n");

        assertEquals(List.of(), result.errors());
        assertEquals(ELEMENT_COUNT, result.items().size());
    }

    @Test
    void anElementValueOtherThanOneThreeOrMinusOneIsKeptAsGiven() throws IOException {
        // C stores parser_getint's result without a range check.
        ParseResult<PlayerProperty> result = load("record-count:1\ntype:element\nname:Resistance\ndesc:You resist\nvalue:2\n");

        assertEquals(List.of(), result.errors());
        assertEquals(2, byName(result.items(), "Fire Resistance").getValue());
    }

    @Test
    void anElementRecordWithNoValueLineHasValueZero() throws IOException {
        ParseResult<PlayerProperty> result = load("record-count:1\ntype:element\nname:Resistance\ndesc:You resist\n");

        assertEquals(0, byName(result.items(), "Fire Resistance").getValue());
    }

    @Test
    void everyBindingOfAnElementRecordGainsTheElementTagInFileOrder() throws IOException {
        ParseResult<PlayerProperty> result = load(
                "record-count:1\ntype:element\nbindui:first_ui:0:1\nbindui:second_ui:1:2\nname:Resistance\ndesc:You resist\nvalue:1\n");

        PlayerProperty holy = byName(result.items(), "Holy power Resistance");
        assertEquals(ElementEnum.ELEM_HOLY_ORB, holy.geteCode());
        assertEquals(List.of("first_ui<HOLY_ORB>", "second_ui<HOLY_ORB>"),
                holy.getEntries().stream().map(PlayerProperty.BindUI::uiEntry).toList());
        assertFalse(holy.getEntries().get(0).aux());
        assertTrue(holy.getEntries().get(1).aux());
        assertEquals(2, holy.getEntries().get(1).value());
    }

    @Test
    void aPlayerRecordWithTwoBindingsKeepsBothInFileOrder() throws IOException {
        ParseResult<PlayerProperty> result = load(
                "record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:0:1\nbindui:c_d:1:2\nname:Rock\n");

        assertEquals(List.of("a_b", "c_d"),
                result.items().get(0).getEntries().stream().map(PlayerProperty.BindUI::uiEntry).toList());
    }

    @Test
    void aBindingTagIsKeptWithTheNameAsOneString() throws IOException {
        // C's "sym ui" is the whole text up to the colon, tag included.
        ParseResult<PlayerProperty> result = load(
                "record-count:1\ntype:player\ncode:ROCK\nbindui:resist_ui_compact_0<DARK>:0:1\nname:Rock\n");

        assertEquals("resist_ui_compact_0<DARK>", result.items().get(0).getEntries().get(0).uiEntry());
    }

    @Test
    void aBindingValueOfSpecialIsSpecialWithValueZero() throws IOException {
        ParseResult<PlayerProperty> result = load(
                "record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:0:special\nname:Rock\n");

        PlayerProperty.BindUI bind = result.items().get(0).getEntries().get(0);
        assertTrue(bind.special());
        assertEquals(0, bind.value());
    }

    @Test
    void aNonZeroAuxFlagIsAux() throws IOException {
        ParseResult<PlayerProperty> result = load(
                "record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:1:1\nbindui:c_d:2:1\nname:Rock\n");

        assertTrue(result.items().get(0).getEntries().get(0).aux());
        assertTrue(result.items().get(0).getEntries().get(1).aux());
    }

    /**
     * C rejects a binding value of INT_MIN or INT_MAX so that it need not check errno; the
     * values either side of them load.
     */
    @Test
    void aBindingValueOfIntMaxIsAHardErrorAsInC() {
        assertAssemblerRefuses("record-count:1\ntype:player\ncode:ROCK\nbindui:max_ui:0:2147483647\nname:Rock\n",
                "out of range bindui value: 2147483647");
    }

    @Test
    void aBindingValueBeyondIntMaxIsAHardErrorAsInC() {
        // C: strtol gives 2147483648 (or LONG_MAX), and v >= INT_MAX is refused.
        assertAssemblerRefuses("record-count:1\ntype:player\ncode:ROCK\nbindui:max_ui:0:99999999999999999999\nname:Rock\n",
                "out of range bindui value");
    }

    @Test
    void theBindingValuesNextToTheLimitsLoad() throws IOException {
        ParseResult<PlayerProperty> result = load("""
                record-count:1
                type:player
                code:ROCK
                bindui:max_less_ui:0:2147483646
                bindui:min_more_ui:0:-2147483647
                name:Rock
                """);

        List<PlayerProperty.BindUI> entries = result.items().get(0).getEntries();
        assertEquals(2147483646, entries.get(0).value());
        assertEquals(-2147483647, entries.get(1).value());
    }

    @Test
    void aBindingValueOfIntMinLoadsAsMinusIntMaxHereWhereCRejectsIt() throws IOException {
        // Accepted deviation 261029: only the positive limit is refused. C: v <= INT_MIN is
        // PARSE_ERROR_INVALID_VALUE.
        assertEquals(-2147483647,
                load("record-count:1\ntype:player\ncode:ROCK\nbindui:min_ui:0:-2147483648\nname:Rock\n")
                        .items().get(0).getEntries().get(0).value());
    }

    @Test
    void aBindingValueOfMinusZeroIsZero() throws IOException {
        PlayerProperty.BindUI bind = load("record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:0:-0\nname:Rock\n")
                .items().get(0).getEntries().get(0);
        assertEquals(0, bind.value());
        assertFalse(bind.special());
    }

    @Test
    void aSpecialBindingKeepsItsAuxFlag() throws IOException {
        PlayerProperty.BindUI bind = load("record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:1:special\nname:Rock\n")
                .items().get(0).getEntries().get(0);
        assertTrue(bind.special());
        assertTrue(bind.aux());
        assertEquals(0, bind.value());
    }

    @Test
    void aTextWithColonsInTheNameIsKeptWhole() throws IOException {
        // "name str desc" takes the rest of the line.
        assertEquals("A:B", load("record-count:1\ntype:player\ncode:ROCK\nname:A:B\n").items().get(0).getName());
    }

    @Test
    void trailingSpacesInTheNameAreKept() throws IOException {
        assertEquals("A  ", load("record-count:1\ntype:player\ncode:ROCK\nname:A  \n").items().get(0).getName());
    }

    @Test
    void descLinesJoinWithNoSeparator() throws IOException {
        // C: string_append, so any space has to be written into the data.
        assertEquals("one two",
                load("record-count:1\ntype:player\ncode:ROCK\nname:A\ndesc:one \ndesc:two\n").items().get(0).getDescription());
        assertEquals("onetwo",
                load("record-count:1\ntype:player\ncode:ROCK\nname:A\ndesc:one\ndesc:two\n").items().get(0).getDescription());
    }

    @Test
    void anEmptyNameLineIsAHardErrorAsInC() throws IOException {
        // C: strtok finds no field, so PARSE_ERROR_MISSING_FIELD.
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nname:\n");
    }

    @Test
    void anEmptyDescLineIsAHardErrorAsInC() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nname:A\ndesc:\n");
    }

    @Test
    void aValueThatIsNotANumberIsAHardErrorAsInC() throws IOException {
        // C: PARSE_ERROR_NOT_NUMBER.
        assertHardError("record-count:1\ntype:element\nname:R\ndesc:You resist\nvalue:x\n");
    }

    @Test
    void aBindingAuxThatIsNotANumberIsAHardErrorAsInC() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:x:1\nname:A\n");
    }

    @Test
    void aCodeInTheOtherFlagListIsAHardErrorAsInC() {
        // PROT_FEAR is an object flag; for type:player C's code_index_in_array finds nothing and the
        // load stops.
        assertAssemblerRefuses("record-count:1\ntype:player\ncode:PROT_FEAR\nname:A\n",
                "illegal player flag value: PROT_FEAR");
    }

    @Test
    void aBadBindingValueIsAHardErrorAsInC() {
        // C: strtol leaves *end non-zero, so PARSE_ERROR_NOT_NUMBER.
        assertAssemblerRefuses(
                "record-count:1\ntype:player\ncode:ROCK\nbindui:good_ui:0:1\nbindui:bad_ui:0:abc\nname:A\n",
                "starting at line: 2", "invalid bindui value: abc");
    }

    @Test
    void codeNoneIsValidForAPlayerRecordAsInC() throws IOException {
        // C's player_info_flags and list_obj_flag_names both begin with NONE.
        assertEquals(PlayerFlag.PF_NONE, load("record-count:1\ntype:player\ncode:NONE\nname:A\n").items().get(0).getpCode());
        assertEquals(ObjectFlag.OF_NONE, load("record-count:1\ntype:object\ncode:NONE\nname:A\n").items().get(0).getoCode());
    }

    @Test
    void codeMaxIsRejectedForAPlayerRecordAsInC() {
        assertAssemblerRefuses("record-count:1\ntype:player\ncode:MAX\nname:A\n", "illegal player flag value: MAX");
    }

    @Test
    void aLowerCaseCodeIsAHardError() throws IOException {
        // C compares with streq and finds nothing; here the lexer rejects it.
        assertHardError("record-count:1\ntype:player\ncode:rock\nname:A\n");
    }

    @Test
    void aHardErrorLateInTheFileDiscardsTheRecordsBeforeIt() throws IOException {
        ParseResult<PlayerProperty> result = load("""
                record-count:2
                type:player
                code:ROCK
                name:Rock
                type:player
                code:SEE_ORE
                bogus:line
                name:Miner
                """);

        assertEquals(0, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHardErrorSuppressesTheRecordCountCheck() throws IOException {
        ParseResult<PlayerProperty> result = load("record-count:9\ntype:player\ncode:ROCK\nname:A\nbogus:line\n");

        assertTrue(result.errors().stream().noneMatch(e -> e.contains("declares")), result.errors()::toString);
    }

    @Test
    void aRecordCountThatIsNotANumberIsAHardError() throws IOException {
        assertHardError("record-count:many\ntype:player\ncode:ROCK\nname:A\n");
    }

    @Test
    void aMatchingRecordCountReportsNothing() throws IOException {
        assertEquals(List.of(), load("record-count:2\ntype:player\ncode:ROCK\nname:A\ntype:player\ncode:SEE_ORE\nname:B\n").errors());
    }

    @Test
    void aTooHighRecordCountIsASoftErrorAndTheRecordsLoad() throws IOException {
        ParseResult<PlayerProperty> result = load("record-count:3\ntype:player\ncode:ROCK\nname:A\n");

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 3") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void aRecordWithNoCodeLoadsWithTheNoneFlag() throws IOException {
        // C leaves the index at zero, which is NONE in both flag lists.
        PlayerProperty p = load("record-count:1\ntype:player\nname:A\n").items().get(0);

        assertEquals(PlayerFlag.PF_NONE, p.getpCode());
        assertEquals(ObjectFlag.OF_NONE, p.getoCode());
    }

    @Test
    void aRecordWithNoDescHasEmptyDescriptionHereWhereCHasNull() throws IOException {
        assertEquals("", load("record-count:1\ntype:player\ncode:ROCK\nname:A\n").items().get(0).getDescription());
    }

    @Test
    void aRecordWithNoValueLineHasValueZero() throws IOException {
        assertEquals(0, load("record-count:1\ntype:player\ncode:ROCK\nname:A\n").items().get(0).getValue());
    }

    // ---- differences from C: the grammar -------------------------------------------------------

    @Test
    void aBindingAfterTheNameIsAHardErrorHereWhereCAcceptsAnyOrder() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nname:Rock\nbindui:a_b:0:1\n");
    }

    @Test
    void aDescBeforeTheNameIsAHardErrorHereWhereCAcceptsAnyOrder() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\ndesc:x\nname:A\n");
    }

    @Test
    void aValueBeforeTheDescIsAHardErrorHereWhereCAcceptsAnyOrder() throws IOException {
        assertHardError("record-count:1\ntype:element\nname:A\nvalue:1\ndesc:x\n");
    }

    @Test
    void aRecordWithNoNameIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\n");
    }

    @Test
    void aRepeatedNameIsAHardErrorHereWhereCKeepsTheLast() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nname:A\nname:B\n");
    }

    @Test
    void aRepeatedValueIsAHardErrorHereWhereCKeepsTheLast() throws IOException {
        assertHardError("record-count:1\ntype:element\nname:A\ndesc:x\nvalue:1\nvalue:3\n");
    }

    @Test
    void aRepeatedCodeIsAHardErrorHereWhereCKeepsTheLast() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\ncode:SEE_ORE\nname:A\n");
    }

    @Test
    void aDescSplitByAnotherLineIsAHardErrorHereWhereCAppendsIt() throws IOException {
        assertHardError("record-count:1\ntype:element\nname:A\ndesc:x\nvalue:1\ndesc:y\n");
    }

    @Test
    void aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        assertHardError("record-count:0\n");
    }

    @Test
    void anUnknownTypeIsAHardErrorHereWhereCTakesAnyText() throws IOException {
        assertHardError("record-count:1\ntype:thing\nname:A\n");
    }

    @Test
    void anUpperCaseBindingNameIsAHardErrorHereWhereCTakesAnySymbol() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nbindui:A_B:0:1\nname:A\n");
    }

    @Test
    void aBindingNameWithAHyphenIsAHardErrorHereWhereCTakesAnySymbol() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nbindui:a-b:0:1\nname:A\n");
    }

    @Test
    void aBindingTagWithAnUnderscoreIsAHardErrorHereWhereCTakesAnySymbol() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nbindui:a_b<HOLY_ORB>:0:1\nname:A\n");
    }

    @Test
    void aLowerCaseBindingTagIsAHardErrorHereWhereCTakesAnySymbol() throws IOException {
        assertHardError("record-count:1\ntype:player\ncode:ROCK\nbindui:a_b<dark>:0:1\nname:A\n");
    }

    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        assertHardError("record-count:1\n  type:player\ncode:ROCK\nname:A\n");
    }

    @Test
    void aHexadecimalValueIsAHardErrorHereWhereCReadsBaseZero() throws IOException {
        // C: strtol(tok, &z, 0), so 0x3 is 3.
        assertHardError("record-count:1\ntype:element\nname:A\ndesc:x\nvalue:0x3\n");
    }

    @Test
    void textAfterTheValueNumberIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        // C: parse stops at the first non-digit and no one checks z, so 1x is 1.
        assertHardError("record-count:1\ntype:element\nname:A\ndesc:x\nvalue:1x\n");
    }

    // ---- differences from C: the assembler ------------------------------------------------------

    @Test
    void aCodeOnAnElementRecordIsAHardErrorAsInC() {
        // C: parse_player_prop_code leaves index at -1 for any type but player and object, so the
        // load stops with PARSE_ERROR_INVALID_PLAY_PROP_CODE.
        assertAssemblerRefuses("record-count:1\ntype:element\ncode:ACID\nname:R\ndesc:You resist\nvalue:1\n",
                "starting at line: 2", "illegal code for an element value: ACID");
    }

    @Test
    void aValueOnAPlayerRecordIsDroppedAsInC() throws IOException {
        // C: finish_parse_player_prop copies type, index, desc and name for a record that is not an
        // element, but not value, which stays zero. The line is still read, so a bad one is an error.
        assertEquals(0, load("record-count:1\ntype:player\ncode:ROCK\nname:A\nvalue:3\n").items().get(0).getValue());
    }

    @Test
    void aValueOnAnObjectRecordIsDroppedAsInC() throws IOException {
        assertEquals(0, load("record-count:1\ntype:object\ncode:FEATHER\nname:A\nvalue:-1\n").items().get(0).getValue());
    }

    @Test
    void anAuxOfDoubleZeroIsNotAuxAsInC() throws IOException {
        // C: strtol("00") is 0, so isaux is false.
        assertFalse(load("record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:00:1\nname:A\n").items().get(0).getEntries().get(0).aux());
    }

    @Test
    void anAuxOfMinusZeroIsNotAuxAsInC() throws IOException {
        assertFalse(load("record-count:1\ntype:player\ncode:ROCK\nbindui:a_b:-0:1\nname:A\n").items().get(0).getEntries().get(0).aux());
    }

    @Test
    void codeMaxIsRejectedForAnObjectRecordAsInC() {
        // C's list_obj_flag_names holds NONE and the real flags, with no MAX.
        assertAssemblerRefuses("record-count:1\ntype:object\ncode:MAX\nname:A\n", "illegal sentinel object flag value: MAX");
    }

    @Test
    void anAuxThatIsNotANumberNamesTheBindingInTheMessage() {
        // Reached past the lexer by a hand-built record: the grammar only lets digits through.
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () ->
                new PlayerPropertyAssembler().assemble(List.of(new PlayerPropertyParseRecord("player", "ROCK",
                        List.of(List.of("a_b", "", "x", "1")), "A", "", "", 7)), new java.util.ArrayList<>(), "hand.txt"));
        assertTrue(thrown.getMessage().contains("line: 7"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("hand.txt"), thrown::getMessage);
    }

    @Test
    void aHandBuiltRecordOfAnUnknownTypeIsAnObjectModifierProperty() {
        // No counterpart in C, which stores any type string; the grammar can't produce one.
        List<PlayerProperty> items = new PlayerPropertyAssembler().assemble(List.of(
                new PlayerPropertyParseRecord("modifier", "STR", List.of(), "A", "", "", 1)),
                new java.util.ArrayList<>(), "hand.txt");

        assertEquals(PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT_MODIFIER, items.get(0).getPlayerPropertyType());
        assertEquals(ObjectModifier.OM_STR, items.get(0).getomCode());
    }

    @Test
    void aHandBuiltObjectModifierWithTheNoneSentinelIsRefused() {
        assertThrows(DataFileParseException.class, () -> new PlayerPropertyAssembler().assemble(List.of(
                new PlayerPropertyParseRecord("modifier", "NONE", List.of(), "A", "", "", 1)),
                new java.util.ArrayList<>(), "hand.txt"));
    }

    @Test
    void aBindingToAnUnknownEntryIsKeptHereWhereCDropsTheBindingQuietly() throws IOException {
        // C: bind_player_ability_to_ui_entry_by_name returns 1 when ui_entry_search finds nothing
        // and the caller ignores it. Here the raw name is stored for the UI layer.
        ParseResult<PlayerProperty> result = load("record-count:1\ntype:player\ncode:ROCK\nbindui:no_such_entry:0:1\nname:A\n");

        assertEquals("no_such_entry", result.items().get(0).getEntries().get(0).uiEntry());
    }
}
