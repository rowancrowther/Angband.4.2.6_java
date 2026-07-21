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
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.*;
import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for {@link MonsterReader}: data-file text -> {@code MonsterLexer}/
 * {@code MonsterGrammar} -> {@link uk.co.jackoftrades.backend.parser.monster.MonsterParseRecord}
 * -> {@link uk.co.jackoftrades.backend.parser.monster.MonsterAssembler} -> {@link MonsterRace}.
 *
 * <p>The monster assembler resolves five registries through {@link GameConstants} — monster bases,
 * blow methods, blow effects, object kinds (for drops and mimics) and the visuals cycler table — so
 * rather than seed each by reflection, {@link #bootstrap()} runs the whole {@link GameConstants#init()}
 * chain (the same order the game loads them), which every other registry-dependent reader test in this
 * package does too. That leaves every lookup the assembler makes populated exactly as at runtime.
 *
 * <p>The suite is three layers:
 * <ol>
 *   <li><b>The happy path</b> — the real {@code monster.txt} loads all 624 races with no soft errors
 *       (itself proof that every base/blow/drop/mimic cross-reference resolved), plus field-level
 *       checks on two early races: {@code scrawny cat} (a plain full-blow monster) and
 *       {@code filthy street urchin} (two {@code flags:} lines, three {@code friends:} lines, and
 *       method-only blows — {@code blow:BEG} and {@code blow:TOUCH:EAT_GOLD}).</li>
 *   <li><b>Many errors in one run</b> — a single file mixing four differently-broken records with two
 *       good ones, pinning the assembler's "collect every failure, drop the bad record, carry on"
 *       contract, and proving a good record's drop and mimic still resolve alongside the failures.</li>
 *   <li><b>A selection of individual error sites</b> — one minimal file per reachable soft-error site,
 *       each otherwise valid so the injected fault is the only error reported.</li>
 * </ol>
 *
 * <p>Each crafted record carries an explicit {@code glyph:t} and {@code color:w}: the glyph keeps the
 * record past the assembler's per-monster glyph check, and the colour past its colour check, so those
 * two mandatory legs never masquerade as the fault under test.
 *
 * <p><b>Not tested:</b> the twelve {@code NumberFormatException} guards on the scalar stat fields are
 * each fronted by a {@code MON_INTEGER} lexer token, so a non-numeric value is a hard grammar error
 * that never reaches {@code parseInt}. The one way to reach such a guard through a data file is an
 * integer too large for {@code int}, which {@link #anOverflowingIntegerIsReportedAsAMalformedInteger()}
 * exercises against {@code speed:}; the other eleven are the same shape and are taken as covered.
 *
 * <p>{@link MonsterRace} exposes only {@code getName()} and {@code getFlags()}, so other field-level
 * assertions read its private fields reflectively through {@link #field}.
 *
 * @author Rowan Crowther
 */
class MonsterReaderTest {

    private static final String MONSTER_FILE = "lib/gamedata/monster.txt";

    /**
     * monster.txt's {@code record-count:} header, and the number of {@code name:} records in it,
     * including the {@code <player>} placeholder race (C's {@code r_info[0]}).
     */
    private static final int EXPECTED_RACES = 624;

    /**
     * The one shared load of the real file; the happy-path tests read from this.
     */
    private static ParseResult<MonsterRace> real;

    @TempDir
    Path tempDir;

    /**
     * Loads every registry the assembler resolves against (via the full init chain), then loads the
     * real monster.txt once for the happy-path tests.
     *
     * @author Rowan Crowther
     */
    @BeforeAll
    static void bootstrap() throws IOException {
        GameConstants.init();
        real = new MonsterReader().parseWithResults(MONSTER_FILE);
    }

    /**
     * {@link GameConstants#init()} populates the shared object-kind registries ({@code objectKinds}
     * and its {@code kindsByTvalSval} index) in place. Reset them to the empty baseline init() itself
     * assumes at its start, so this heavy load does not leak into order-sensitive suites — notably
     * {@code EgoItemReaderTest}'s numeric-sval test, which needs {@code kindsByTvalSval} empty for a
     * numeric sval to resolve to no kind.
     *
     * @author Rowan Crowther
     */
    @AfterAll
    static void cleanup() throws Exception {
        GameConstants.objectKinds.clear();
        Field f = GameConstants.class.getDeclaredField("kindsByTvalSval");
        f.setAccessible(true);
        ((Map<?, ?>) f.get(null)).clear();
    }

    // ---- fixture + reflection helpers ------------------------------------

    /**
     * A minimal record that passes every mandatory leg (resolvable base, valid colour, one-char
     * glyph), with {@code extraLines} appended for the field under test.
     */
    private static String rec(String name, String... extraLines) {
        StringBuilder sb = new StringBuilder("name:").append(name).append('\n')
                .append("base:townsfolk\n")
                .append("color:w\n")
                .append("glyph:t\n");
        for (String line : extraLines) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * Writes {@code content} to a temp file and drives it through the reader.
     */
    private ParseResult<MonsterRace> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new MonsterReader().parseWithResults(file.toString());
    }

    private static boolean hasError(ParseResult<MonsterRace> r, String fragment) {
        return r.errors().stream().anyMatch(s -> s.contains(fragment));
    }

    private static MonsterRace byName(ParseResult<MonsterRace> r, String name) {
        return r.items().stream().filter(m -> name.equals(m.getName())).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(MonsterRace target, String name) throws Exception {
        Field f = MonsterRace.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    /**
     * As {@link #field}, but reads a declared field off any object (e.g. an element of a list).
     */
    @SuppressWarnings("unchecked")
    private static <T> T declaredField(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    // =====================================================================
    // (1) HAPPY PATH
    // =====================================================================

    @Test
    void theRealFileLoadsAll624MonstersWithNoErrors() {
        // A clean load is the end-to-end proof: any unresolved base, blow method/effect, drop, mimic
        // or colour cycle would have appended a soft error and dropped that race.
        assertFalse(real.hasErrors(), () -> real.errors().toString());
        assertEquals(EXPECTED_RACES, real.items().size());
    }

    @Test
    void aSimpleMonstersScalarFieldsFlagsAndSingleBlowAreAssembled() throws Exception {
        MonsterRace cat = byName(real, "scrawny cat");
        assertNotNull(cat, "scrawny cat should load once glyph-inheritance and optional blows are handled");

        assertNotNull(field(cat, "base"), "base:feline must resolve");
        assertEquals(2, (int) field(cat, "averageHP"));   // hit-points:2
        assertEquals(1, (int) field(cat, "ac"));          // armor-class:1
        assertEquals(10, (int) field(cat, "sleep"));      // sleepiness:10
        assertEquals(30, (int) field(cat, "hearing"));    // hearing:30
        assertEquals(30, (int) field(cat, "smell"));      // smell:30
        assertEquals(110, (int) field(cat, "speed"));     // speed:110
        assertEquals(0, (int) field(cat, "level"));       // depth:0
        assertEquals(3, (int) field(cat, "rarity"));      // rarity:3
        assertEquals(0, (int) field(cat, "mexp"));        // experience:0

        assertTrue(cat.getFlags().has(MonsterRaceFlag.RF_RAND_25));  // flags:RAND_25

        List<?> blows = field(cat, "blow");               // blow:CLAW:HURT:1d1
        assertEquals(1, blows.size());

        assertTrue(((List<?>) field(cat, "drops")).isEmpty());
        assertTrue(((List<?>) field(cat, "friends")).isEmpty());
        assertTrue(((List<?>) field(cat, "mimicKinds")).isEmpty());
    }

    @Test
    void aMonsterWithTwoFlagLinesThreeFriendsAndMethodOnlyBlowsIsAssembled() throws Exception {
        MonsterRace urchin = byName(real, "filthy street urchin");
        assertNotNull(urchin, "urchin should load once method-only blows (blow:BEG) are handled");

        // Two separate flags: lines must accumulate into one flag set, not overwrite.
        assertTrue(urchin.getFlags().has(MonsterRaceFlag.RF_MALE));       // flags:MALE
        assertTrue(urchin.getFlags().has(MonsterRaceFlag.RF_RAND_25));    // flags:RAND_25 | ...
        assertTrue(urchin.getFlags().has(MonsterRaceFlag.RF_OPEN_DOOR));
        assertTrue(urchin.getFlags().has(MonsterRaceFlag.RF_TAKE_ITEM));

        // blow:BEG (method only) and blow:TOUCH:EAT_GOLD (method + effect, no dice).
        assertEquals(2, ((List<?>) field(urchin, "blow")).size());

        // friends:50:2d1:Scrawny cat / friends:50:2d1:Scruffy little dog / friends:100:3d4:Same
        assertEquals(3, ((List<?>) field(urchin, "friends")).size());
    }

    @Test
    void aColourCyclingMonsterResolvesItsCycler() throws Exception {
        // color-cycle:fancy:crystal -- the assembler must look the "crystal" cycle up within the
        // "fancy" group (keyed by cycle name, not group name) and attach the resolved ColourCycle.
        MonsterRace drake = byName(real, "great crystal drake");
        assertNotNull(drake, "great crystal drake should load");
        assertNotNull(field(drake, "cycler"), "fancy:crystal must resolve to a ColourCycle, not null");
    }

    // ---- base inheritance: flags union, flags-off subtraction, glyph seed/override ----

    @Test
    void aMonsterInheritsItsBaseFlagsAndAddsItsOwn() {
        // townsfolk base declares SPIRIT | CLEAR_WEB; filthy street urchin names neither but adds MALE.
        // The assembled set must be base-union-own.
        MonsterRace urchin = byName(real, "filthy street urchin");
        assertNotNull(urchin);

        // Proof the base is the only source of these two (the urchin record never names them).
        assertTrue(GameConstants.lookupMonsterBase("townsfolk").getFlags().has(MonsterRaceFlag.RF_SPIRIT));

        assertTrue(urchin.getFlags().has(MonsterRaceFlag.RF_SPIRIT), "inherited from townsfolk base");
        assertTrue(urchin.getFlags().has(MonsterRaceFlag.RF_CLEAR_WEB), "inherited from townsfolk base");
        assertTrue(urchin.getFlags().has(MonsterRaceFlag.RF_MALE), "its own flags:MALE");
    }

    @Test
    void aFlagsOffLineRemovesAnInheritedBaseFlagButKeepsTheOthers() {
        // mold base declares ...IM_POIS | HURT_FIRE | ...; red mold has flags-off:HURT_FIRE, which must
        // subtract from the accumulated set (base union already applied) yet leave the siblings intact.
        MonsterRace redMold = byName(real, "red mold");
        assertNotNull(redMold);

        assertTrue(GameConstants.lookupMonsterBase("mold").getFlags().has(MonsterRaceFlag.RF_HURT_FIRE),
                "the base carries HURT_FIRE, so there is something to turn off");

        assertFalse(redMold.getFlags().has(MonsterRaceFlag.RF_HURT_FIRE), "flags-off:HURT_FIRE removed it");
        assertTrue(redMold.getFlags().has(MonsterRaceFlag.RF_IM_POIS), "sibling inherited flag survives");
        assertTrue(redMold.getFlags().has(MonsterRaceFlag.RF_NEVER_MOVE), "sibling inherited flag survives");
    }

    @Test
    void aMonsterWithoutAGlyphLineInheritsItsBaseGlyph() throws Exception {
        // scrawny cat has no glyph: line; base feline is glyph:f. Its own color:U supplies the colour.
        MonsterRace cat = byName(real, "scrawny cat");
        assertNotNull(cat);

        AngbandDisplayCharacter adc = field(cat, "adc");
        assertNotNull(adc, "glyph must be seeded from the base, not left null");
        assertEquals('f', adc.getCharacter(), "inherited from feline base glyph:f");
    }

    @Test
    void anExplicitGlyphOverridesTheBaseGlyph() throws Exception {
        // potion mimic carries glyph:! -- one of only four monsters that override their base glyph.
        MonsterRace mimic = byName(real, "potion mimic");
        assertNotNull(mimic);

        AngbandDisplayCharacter adc = field(mimic, "adc");
        assertNotNull(adc);
        assertEquals('!', adc.getCharacter(), "explicit glyph:! overrides the base glyph");
    }

    @Test
    void aMethodOnlyBlowGetsTheNoneEffectRatherThanNull() throws Exception {
        // blow:BEG names no effect; C resolves an omitted effect to findeff("NONE"), never a null, so
        // downstream combat/lore can dereference blow.effect unconditionally. The urchin's blows are,
        // in file order: [0] blow:BEG (method only), [1] blow:TOUCH:EAT_GOLD.
        MonsterRace urchin = byName(real, "filthy street urchin");
        assertNotNull(urchin);

        List<?> blows = field(urchin, "blow");
        Object begBlow = blows.get(0);

        BlowEffect effect = declaredField(begBlow, "effect");
        assertNotNull(effect, "an omitted blow effect must default to NONE, not null");
        assertSame(GameConstants.lookupBlowEffect("NONE"), effect, "the omitted effect is the NONE effect");
    }

    // ---- friends: two-pass name resolution, Same, substring fallback, role default ----

    @Test
    void friendNamesResolveToRacesIncludingForwardRefsAndSame() throws Exception {
        // Resolution runs after every race is loaded (C's finish_parse_monster), so assert against the
        // fully-loaded global registry. filthy street urchin's friends are:
        //   Scrawny cat / Scruffy little dog -- both defined LATER in the file (forward refs) and
        //   case-mismatched vs their lowercase race names -- and Same (the self-reference).
        MonsterRace urchin = GameConstants.lookupMonsterRace("filthy street urchin");
        assertNotNull(urchin);

        List<?> friends = field(urchin, "friends");
        assertEquals(3, friends.size());

        assertEquals("scrawny cat",
                ((MonsterRace) declaredField(friends.get(0), "race")).getName(),
                "Scrawny cat -> scrawny cat (forward ref, case-insensitive)");
        assertEquals("scruffy little dog",
                ((MonsterRace) declaredField(friends.get(1), "race")).getName(),
                "Scruffy little dog -> scruffy little dog (forward ref)");
        assertSame(urchin, declaredField(friends.get(2), "race"), "Same -> the owning race itself");

        // None of the three names a role, so each must take the MEMBER default (not NONE).
        for (Object f : friends) {
            assertEquals(MonsterGroupRole.MON_GROUP_MEMBER, declaredField(f, "role"),
                    "an omitted friend role defaults to MEMBER");
        }
    }

    @Test
    void aSameFriendIsSubstitutedToTheOwnerAndResolvesBackToIt() throws Exception {
        // filthy street urchin has friends:100:3d4:Same. The assembler rewrites "Same" to the owning
        // race's own name, so the second pass resolves it back to the urchin with no special-casing
        // in resolveFriends. Found by that substituted name rather than by list position, so this
        // guards the resolution even if the friend order changes.
        MonsterRace urchin = GameConstants.lookupMonsterRace("filthy street urchin");
        assertNotNull(urchin);

        boolean foundSelf = false;
        for (Object f : (List<?>) field(urchin, "friends")) {
            if ("filthy street urchin".equals(declaredField(f, "name"))) {
                assertSame(urchin, declaredField(f, "race"), "the Same self-reference resolves to its owner");
                foundSelf = true;
            }
        }
        assertTrue(foundSelf, "expected the Same friend to carry the owning race's substituted name");
    }

    @Test
    void raceLookupIsCaseInsensitiveWithASubstringFallback() {
        // Exact, case-insensitive (C my_stricmp).
        assertEquals("scrawny cat", GameConstants.lookupMonsterRace("Scrawny Cat").getName());

        // No race is exactly "spider"; friends:...:spider relies on C's my_stristr fallback, which
        // returns the first race whose name CONTAINS the query -- cave spider (the earliest one).
        MonsterRace spider = GameConstants.lookupMonsterRace("spider");
        assertNotNull(spider, "must fall back to a substring match");
        assertEquals("cave spider", spider.getName());
    }

    // ---- shapes: base-vs-race resolution ----

    @Test
    void aSpecificRaceShapeResolvesToARaceAndNotABase() throws Exception {
        // Sauron, the Sorcerer: shape:Wolf-Sauron / Vampire-Sauron / Serpent-Sauron -- each a specific
        // unique race (not a base), and each defined LATER in the file, so this also exercises the
        // deferred forward-ref resolution.
        MonsterRace sauron = GameConstants.lookupMonsterRace("Sauron, the Sorcerer");
        assertNotNull(sauron);

        List<?> shapes = field(sauron, "shapes");
        assertFalse(shapes.isEmpty());

        List<String> raceNames = new ArrayList<>();
        for (Object o : shapes) {
            MonsterShape s = (MonsterShape) o;
            assertNull(s.getBase(), "a specific-race shape must not resolve to a base");
            assertNotNull(s.getRace(), "so it must resolve to a race");
            raceNames.add(s.getRace().getName());
        }
        assertTrue(raceNames.contains("Wolf-Sauron"), () -> raceNames.toString());
    }

    @Test
    void aBaseShapeResolvesToABaseAndNotARace() throws Exception {
        // Maia of Varda: shape:elemental and shape:vortex -- both base families (generic shapechange),
        // resolved by name against the monster bases, never to a race.
        MonsterRace maia = GameConstants.lookupMonsterRace("Maia of Varda");
        assertNotNull(maia);

        List<?> shapes = field(maia, "shapes");
        assertEquals(2, shapes.size());

        List<String> baseCodeNames = new ArrayList<>();
        for (Object o : shapes) {
            MonsterShape s = (MonsterShape) o;
            assertNull(s.getRace(), "a base shape must not resolve to a race");
            assertNotNull(s.getBase(), "elemental/vortex are bases");
            baseCodeNames.add(s.getBase().getCodeName());
        }
        assertTrue(baseCodeNames.contains("elemental"), () -> baseCodeNames.toString());
        assertTrue(baseCodeNames.contains("vortex"), () -> baseCodeNames.toString());
    }

    // ---- spell messages, drop base-vs-specific, friends-base, explicit role, zero-dice ----

    @Test
    void perSpellMessagesAreAssembledOntoTheOwningMonster() throws Exception {
        // The spell-message map is built per record; a regression once shared one map across every
        // monster. Assert the messages land keyed by spell, and an absent leg stays null.
        ParseResult<MonsterRace> r = load("spell-messages.txt", withHeader(1, rec("Yeller",
                "spells:SHRIEK",
                "message-vis:SHRIEK:It lets out a piercing shriek!",
                "message-invis:SHRIEK:Something shrieks nearby.")));
        assertFalse(r.hasErrors(), r.errors()::toString);

        MonsterRace yeller = byName(r, "Yeller");
        assertNotNull(yeller);

        Map<String, MonsterSpellMessage> messages = field(yeller, "spellMessages");
        MonsterSpellMessage m = messages.get("SHRIEK");
        assertNotNull(m, () -> messages.keySet().toString());
        assertEquals("It lets out a piercing shriek!", m.vis());
        assertEquals("Something shrieks nearby.", m.invis());
        assertNull(m.miss(), "no message-miss line -> null");
    }

    @Test
    void aSpecificDropKeepsItsKindWhileADropBaseHasNullKind() throws Exception {
        // The core MonsterDrop semantic: drop: resolves to a specific ObjectKind; drop-base: is
        // "by base type" and carries a null kind. Both live in the one drops list.
        ParseResult<MonsterRace> r = load("drops.txt", withHeader(1, rec("Dropper",
                "drop:scroll:Phase Door:100:1:1",
                "drop-base:mushroom:100:1:2")));
        assertFalse(r.hasErrors(), r.errors()::toString);

        MonsterRace dropper = byName(r, "Dropper");
        assertNotNull(dropper);

        List<?> drops = field(dropper, "drops");
        assertEquals(2, drops.size());

        int nullKinds = 0, resolvedKinds = 0;
        for (Object d : drops) {
            if (declaredField(d, "kind") == null) nullKinds++;
            else resolvedKinds++;
        }
        assertEquals(1, resolvedKinds, "drop:scroll:Phase Door keeps its resolved kind");
        assertEquals(1, nullKinds, "drop-base:mushroom is by base type -> null kind");
    }

    @Test
    void aFriendsBaseResolvesToItsBaseWithAnExplicitServantRole() throws Exception {
        // friends-base resolves its base at assembly time (bases load first), takes an explicit
        // servant/bodyguard role, and parses its count as dice.
        ParseResult<MonsterRace> r = load("friends-base.txt", withHeader(1, rec("Leader",
                "friends-base:100:1d2:spider:servant")));
        assertFalse(r.hasErrors(), r.errors()::toString);

        MonsterRace leader = byName(r, "Leader");
        assertNotNull(leader);

        List<?> friendsBase = field(leader, "friendsBase");
        assertEquals(1, friendsBase.size());
        Object entry = friendsBase.get(0);

        MonsterBase base = declaredField(entry, "base");
        assertNotNull(base);
        assertEquals("spider", base.getCodeName());
        assertEquals(MonsterGroupRole.MON_GROUP_SERVANT, declaredField(entry, "role"));
        assertEquals(1, (int) declaredField(entry, "numberDice"));   // 1d2
        assertEquals(2, (int) declaredField(entry, "numberSides"));
    }

    @Test
    void anExplicitFriendRoleMapsToTheGroupRole() throws Exception {
        // The friends loop's explicit-role mapping: servant -> MON_GROUP_SERVANT (bodyguard is the
        // same code path). Race resolution is deferred, so the friend's race stays null here; only
        // the role, set at assembly, is under test.
        ParseResult<MonsterRace> r = load("friend-role.txt", withHeader(1, rec("Boss",
                "friends:100:1d1:Some Monster:servant")));
        assertFalse(r.hasErrors(), r.errors()::toString);

        MonsterRace boss = byName(r, "Boss");
        assertNotNull(boss);

        List<?> friends = field(boss, "friends");
        assertEquals(1, friends.size());
        assertEquals(MonsterGroupRole.MON_GROUP_SERVANT, declaredField(friends.get(0), "role"));
    }

    @Test
    void anOmittedBlowDamageDefaultsToAZeroDiceRandomNotNull() throws Exception {
        // filthy street urchin's blow:BEG has no dice; C leaves dice as 0d0, not null, so combat can
        // roll it unconditionally.
        MonsterRace urchin = byName(real, "filthy street urchin");
        assertNotNull(urchin);

        List<?> blows = field(urchin, "blow");
        Random dice = declaredField(blows.get(0), "dice");   // blow:BEG, method only
        assertNotNull(dice, "an omitted blow damage must be a zero Random, not null");
        assertEquals(0, dice.getDice());
        assertEquals(0, dice.getSides());
    }

    // =====================================================================
    // (2) MANY ERRORS IN ONE RUN
    // =====================================================================

    @Test
    void multipleBadRecordsInOneRunAreEachReportedAndDroppedWhileGoodOnesSurvive() throws Exception {
        // Six records: two clean, four each broken a different way. The run must report exactly the
        // four faults, drop exactly the four bad records, and keep both good ones -- proof the
        // assembler collects failures and carries on rather than failing the whole file.
        String body = rec("Good One", "drop:scroll:Phase Door:100:1:1", "mimic:gold:copper")
                + "name:BadBase\nbase:notabase\ncolor:w\nglyph:t\n"
                + rec("BadFlagRec", "flags:NOTAFLAG")
                + rec("BadEffectRec", "blow:CLAW:BADEFFECT:1d1")
                + rec("BadDropRec", "drop:notatv:Thing:100:1:1")
                + rec("Good Two");

        ParseResult<MonsterRace> r = load("many-errors.txt", withHeader(6, body));

        // Only the two good records survive.
        assertEquals(2, r.items().size(), r.errors()::toString);
        assertNotNull(byName(r, "Good One"));
        assertNotNull(byName(r, "Good Two"));
        assertNull(byName(r, "BadBase"));
        assertNull(byName(r, "BadFlagRec"));

        // Exactly one error per broken record, and each names its fault.
        assertEquals(4, r.errors().size(), r.errors()::toString);
        assertTrue(hasError(r, "invalid monster-base: notabase"), r.errors()::toString);
        assertTrue(hasError(r, "unknown flag name: NOTAFLAG"), r.errors()::toString);
        assertTrue(hasError(r, "unknown blow effect: BADEFFECT"), r.errors()::toString);
        assertTrue(hasError(r, "unknown TV type: notatv"), r.errors()::toString);

        // The surviving good record's drop and mimic resolved against the real object kinds.
        MonsterRace good = byName(r, "Good One");
        assertEquals(1, ((List<?>) field(good, "drops")).size());       // drop:scroll:Phase Door
        assertEquals(1, ((List<?>) field(good, "mimicKinds")).size());  // mimic:gold:copper
    }

    // =====================================================================
    // (3) A SELECTION OF INDIVIDUAL ERROR SITES
    // =====================================================================

    @Test
    void anUnknownBaseIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-base.txt", withHeader(1,
                "name:Nope\nbase:notabase\ncolor:w\nglyph:t\n"));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "invalid monster-base: notabase"), r.errors()::toString);
    }

    @Test
    void anUnknownFlagIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-flag.txt", withHeader(1, rec("Nope", "flags:BADFLAGON")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown flag name: BADFLAGON"), r.errors()::toString);
    }

    @Test
    void anUnknownFlagsOffFlagIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-flagoff.txt", withHeader(1, rec("Nope", "flags-off:BADFLAGOFF")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown flag name: BADFLAGOFF"), r.errors()::toString);
    }

    @Test
    void anUnknownSpellIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-spell.txt", withHeader(1, rec("Nope", "spells:NOTASPELL")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown spell: NOTASPELL"), r.errors()::toString);
    }

    @Test
    void anUnknownBlowMethodIsReportedAndTheRecordDropped() throws Exception {
        // A full blow with a valid effect and dice, so the method is the only thing wrong.
        ParseResult<MonsterRace> r = load("bad-method.txt", withHeader(1, rec("Nope", "blow:BADMETHOD:HURT:1d1")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown blow method: BADMETHOD"), r.errors()::toString);
    }

    @Test
    void anUnknownBlowEffectIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-effect.txt", withHeader(1, rec("Nope", "blow:CLAW:BADEFFECT:1d1")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown blow effect: BADEFFECT"), r.errors()::toString);
    }

    @Test
    void anUnknownDropTvalIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-drop-tval.txt", withHeader(1, rec("Nope", "drop:notatv:Thing:100:1:1")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown TV type: notatv"), r.errors()::toString);
    }

    @Test
    void anUnknownDropObjectNameIsReportedAndTheRecordDropped() throws Exception {
        // A valid tval (scroll) with an sval that resolves to no kind -- so the base-name leg is reached.
        ParseResult<MonsterRace> r = load("bad-drop-sval.txt", withHeader(1, rec("Nope", "drop:scroll:NotARealScroll:100:1:1")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown drop base: NotARealScroll"), r.errors()::toString);
    }

    @Test
    void anUnknownMimicTvalIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-mimic-tval.txt", withHeader(1, rec("Nope", "mimic:notatv:Thing")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown mimic type value: notatv"), r.errors()::toString);
    }

    @Test
    void anUnknownMimicObjectNameIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-mimic-sval.txt", withHeader(1, rec("Nope", "mimic:potion:NotARealPotion")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown mimic name: NotARealPotion"), r.errors()::toString);
    }

    @Test
    void aFriendChanceOutOfBoundsIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-friend-chance.txt", withHeader(1, rec("Nope", "friends:200:1d1:Some Monster")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "friends chance value out of bounds"), r.errors()::toString);
    }

    @Test
    void anUnknownFriendRoleIsReported() throws Exception {
        // NOTE: a bad friend role sets only the local badRole, which skips the friend but does NOT set
        // badFriend -- so the record is NOT dropped, unlike every other error site here. Asserting the
        // error alone; the surviving-record asymmetry is flagged separately for MonsterAssembler.
        ParseResult<MonsterRace> r = load("bad-friend-role.txt", withHeader(1, rec("Nope", "friends:50:1d1:Some Monster:badrole")));
        assertTrue(hasError(r, "unknown friends role: badrole"), r.errors()::toString);
    }

    @Test
    void anOverflowingIntegerIsReportedAsAMalformedInteger() throws Exception {
        // speed: lexes as MON_INTEGER (all digits) but overflows int, so it is the one data-file route
        // to the otherwise-unreachable NumberFormatException guards on the scalar stat fields.
        ParseResult<MonsterRace> r = load("overflow-speed.txt", withHeader(1, rec("Nope", "speed:99999999999")));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "malformed integer for speed"), r.errors()::toString);
    }

    @Test
    void aGlyphThatIsNotExactlyOneCharacterIsReportedAndTheRecordDropped() throws Exception {
        ParseResult<MonsterRace> r = load("bad-glyph.txt", withHeader(1,
                "name:Nope\nbase:townsfolk\ncolor:w\nglyph:xy\n"));
        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "illegal glyph character: xy"), r.errors()::toString);
    }

    @Test
    void aWrongRecordCountHeaderIsReported() throws Exception {
        // One valid record but a header claiming 99 -- a soft error raised by GrammarDriver, outside
        // the assembler, so the good record still loads.
        ParseResult<MonsterRace> r = load("bad-count.txt", withHeader(99, rec("Solo")));
        assertEquals(1, r.items().size(), r.errors()::toString);
        assertTrue(hasError(r, "record-count header declares 99 records, but file contains 1"), r.errors()::toString);
    }
}
