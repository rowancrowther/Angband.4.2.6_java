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
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.*;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterGroupRole;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for {@link MonsterReader}: data-file text -> {@code MonsterLexer}/
 * {@code MonsterGrammar} -> {@link uk.co.jackoftradesltd.backend.parser.monster.MonsterParseRecord}
 * -> {@link uk.co.jackoftradesltd.backend.parser.monster.MonsterAssembler} -> {@link MonsterRace}.
 *
 * <p>The monster assembler resolves five lookups across {@link MonsterRegistry} and {@link ObjectRegistry} — monster bases,
 * blow methods, blow effects, object kinds (for drops and mimics) and the visuals cycler table — so
 * rather than seed each by reflection, {@link #bootstrap()} runs the whole {@code GameConstants.init(CoreChannel)}
 * chain (the same order the game loads them), which every other registry-dependent reader test in this
 * package does too. That leaves every lookup the assembler makes populated exactly as at runtime.
 *
 * <p>The suite is three layers:
 * <ol>
 *   <li><b>The happy path</b> — the real {@code monster.txt} loads all 624 races with no errors
 *       (itself proof that every base/blow/drop/mimic cross-reference resolved), plus field-level
 *       checks on two early races: {@code scrawny cat} (a plain full-blow monster) and
 *       {@code filthy street urchin} (two {@code flags:} lines, three {@code friends:} lines, and
 *       method-only blows — {@code blow:BEG} and {@code blow:TOUCH:EAT_GOLD}).</li>
 *   <li><b>A bad record ends the load</b> — every failure in the assembler throws a
 *       {@code DataFileParseException} naming the record's line, as C ends the load on the parser error,
 *       so one bad record loses the good ones around it.</li>
 *   <li><b>A selection of individual error sites</b> — one minimal file per reachable failure site,
 *       each otherwise valid so the injected fault is the only one, and the sentinel rows C does and
 *       does not accept.</li>
 * </ol>
 *
 * <p>Three more sections pin the reader's own contract against {@code mon-init.c} (file order with
 * {@code <player>} first, every real race's stats and entry counts read independently from the text of the
 * file, the entry points, the Java-only header, directives in any order) and the places where the grammar
 * ({@code docs/grammar_issues.md}) or the assembler ({@code docs/Assembler_Issues.md}) differs from C. A
 * test in those last two sections asserts what Java does today, and its name says what C does instead, so
 * fixing the difference turns that test red on purpose.
 *
 * <p>Each crafted record carries an explicit {@code glyph:t} and {@code color:w}: the glyph keeps the
 * record past the assembler's per-monster glyph check, and the colour past its colour check, so those
 * two mandatory legs never masquerade as the fault under test.
 *
 * <p><b>Not tested:</b> a non-numeric value in a scalar stat field is fronted by a {@code MON_INTEGER}
 * lexer token, so it is a hard grammar error that never reaches {@code ParseUtils.parseIntString}. An
 * integer too large for {@code int} does reach it and is clamped, which
 * {@link #anOverflowingIntegerIsClampedHereWhereCWraps()} exercises against {@code speed:}.
 *
 * <p>{@link MonsterRace} exposes only {@code getName()} and {@code hasMonsterRaceFlag(...)}, so other
 * field-level assertions read its private fields reflectively through {@link #field}.
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
     */
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
        real = new MonsterReader().parseWithResults(MONSTER_FILE);
    }

    /**
     * {@code GameConstants.init(CoreChannel)} populates the shared object-kind registries ({@code objectKinds}
     * and its {@code kindsByTvalSval} index) in place. Reset them to the empty baseline init() itself
     * assumes at its start, so this heavy load does not leak into order-sensitive suites — notably
     * {@code EgoItemReaderTest}'s numeric-sval test, which needs {@code kindsByTvalSval} empty for a
     * numeric sval to resolve to no kind.
     */
    @AfterAll
    static void cleanup() throws Exception {
        ObjectRegistry.reset();
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

    /**
     * Loads {@code content} and returns the {@link DataFileParseException} it must throw: every failure in
     * {@code MonsterAssembler} ends the load, as C ends it on the parser error.
     */
    private DataFileParseException refused(String fileName, String content) {
        return assertThrows(DataFileParseException.class, () -> load(fileName, content));
    }

    /**
     * Loads the one record {@code body} (header count 1) and checks the load ends with a message holding
     * {@code fragment}.
     */
    private void assertRefusedWith(String fileName, String body, String fragment) {
        DataFileParseException e = refused(fileName, withHeader(1, body));
        assertTrue(e.getMessage().contains(fragment), e::getMessage);
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

        assertTrue(cat.hasMonsterRaceFlag(MonsterRaceFlag.RF_RAND_25));  // flags:RAND_25

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
        assertTrue(urchin.hasMonsterRaceFlag(MonsterRaceFlag.RF_MALE));       // flags:MALE
        assertTrue(urchin.hasMonsterRaceFlag(MonsterRaceFlag.RF_RAND_25));    // flags:RAND_25 | ...
        assertTrue(urchin.hasMonsterRaceFlag(MonsterRaceFlag.RF_OPEN_DOOR));
        assertTrue(urchin.hasMonsterRaceFlag(MonsterRaceFlag.RF_TAKE_ITEM));

        // blow:BEG (method only) and blow:TOUCH:EAT_GOLD (method + effect, no dice).
        assertEquals(2, ((List<?>) field(urchin, "blow")).size());

        // C appends each blow, so blows run in file order (unlike drops, friends, mimics, shapes and
        // spell messages, which C prepends): BEG first, then TOUCH:EAT_GOLD.
        List<MonsterBlow> blows = urchin.getBlows();
        assertEquals("BEG", blows.get(0).getMethod().getName());
        assertEquals("NONE", blows.get(0).getEffect().getName(), "no effect given, so C uses NONE");
        assertEquals("TOUCH", blows.get(1).getMethod().getName());
        assertEquals("EAT_GOLD", blows.get(1).getEffect().getName());

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
        assertTrue(MonsterRegistry.lookupMonsterBase("townsfolk").getFlags().has(MonsterRaceFlag.RF_SPIRIT));

        assertTrue(urchin.hasMonsterRaceFlag(MonsterRaceFlag.RF_SPIRIT), "inherited from townsfolk base");
        assertTrue(urchin.hasMonsterRaceFlag(MonsterRaceFlag.RF_CLEAR_WEB), "inherited from townsfolk base");
        assertTrue(urchin.hasMonsterRaceFlag(MonsterRaceFlag.RF_MALE), "its own flags:MALE");
    }

    @Test
    void aFlagsOffLineRemovesAnInheritedBaseFlagButKeepsTheOthers() {
        // mold base declares ...IM_POIS | HURT_FIRE | ...; red mold has flags-off:HURT_FIRE, which must
        // subtract from the accumulated set (base union already applied) yet leave the siblings intact.
        MonsterRace redMold = byName(real, "red mold");
        assertNotNull(redMold);

        assertTrue(MonsterRegistry.lookupMonsterBase("mold").getFlags().has(MonsterRaceFlag.RF_HURT_FIRE),
                "the base carries HURT_FIRE, so there is something to turn off");

        assertFalse(redMold.hasMonsterRaceFlag(MonsterRaceFlag.RF_HURT_FIRE), "flags-off:HURT_FIRE removed it");
        assertTrue(redMold.hasMonsterRaceFlag(MonsterRaceFlag.RF_IM_POIS), "sibling inherited flag survives");
        assertTrue(redMold.hasMonsterRaceFlag(MonsterRaceFlag.RF_NEVER_MOVE), "sibling inherited flag survives");
    }

    @Test
    void aMonsterWithoutAGlyphLineInheritsItsBaseGlyph() throws Exception {
        // scrawny cat has no glyph: line; base feline is glyph:f. Its own color:U supplies the colour.
        MonsterRace cat = byName(real, "scrawny cat");
        assertNotNull(cat);

        AngbandDisplayCharacter adc = field(cat, "adc");
        assertNotNull(adc, "glyph must be seeded from the base, not left null");
        assertEquals('f', adc.getNarrowCharacter(), "inherited from feline base glyph:f");
    }

    @Test
    void anExplicitGlyphOverridesTheBaseGlyph() throws Exception {
        // potion mimic carries glyph:! -- one of only four monsters that override their base glyph.
        MonsterRace mimic = byName(real, "potion mimic");
        assertNotNull(mimic);

        AngbandDisplayCharacter adc = field(mimic, "adc");
        assertNotNull(adc);
        assertEquals('!', adc.getNarrowCharacter(), "explicit glyph:! overrides the base glyph");
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
        assertSame(MonsterRegistry.lookupBlowEffect("NONE"), effect, "the omitted effect is the NONE effect");
    }

    // ---- friends: two-pass name resolution, Same, substring fallback, role default ----

    @Test
    void friendNamesResolveToRacesIncludingForwardRefsAndSame() throws Exception {
        // Resolution runs after every race is loaded (C's finish_parse_monster), so assert against the
        // fully-loaded global registry. filthy street urchin's friends are:
        //   Scrawny cat / Scruffy little dog -- both defined LATER in the file (forward refs) and
        //   case-mismatched vs their lowercase race names -- and Same (the self-reference).
        // The list is in reverse file order, as C's head-pushed chain is: Same, dog, cat.
        MonsterRace urchin = MonsterRegistry.lookupMonsterRace("filthy street urchin");
        assertNotNull(urchin);

        List<?> friends = field(urchin, "friends");
        assertEquals(3, friends.size());

        assertSame(urchin, declaredField(friends.get(0), "race"), "Same -> the owning race itself");
        assertEquals("scruffy little dog",
                ((MonsterRace) declaredField(friends.get(1), "race")).getName(),
                "Scruffy little dog -> scruffy little dog (forward ref)");
        assertEquals("scrawny cat",
                ((MonsterRace) declaredField(friends.get(2), "race")).getName(),
                "Scrawny cat -> scrawny cat (forward ref, case-insensitive)");

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
        MonsterRace urchin = MonsterRegistry.lookupMonsterRace("filthy street urchin");
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
        assertEquals("scrawny cat", MonsterRegistry.lookupMonsterRace("Scrawny Cat").getName());

        // No race is exactly "spider"; friends:...:spider relies on C's my_stristr fallback, which
        // returns the first race whose name CONTAINS the query -- cave spider (the earliest one).
        MonsterRace spider = MonsterRegistry.lookupMonsterRace("spider");
        assertNotNull(spider, "must fall back to a substring match");
        assertEquals("cave spider", spider.getName());
    }

    // ---- shapes: base-vs-race resolution ----

    @Test
    void aSpecificRaceShapeResolvesToARaceAndNotABase() throws Exception {
        // Sauron, the Sorcerer: shape:Wolf-Sauron / Vampire-Sauron / Serpent-Sauron -- each a specific
        // unique race (not a base), and each defined LATER in the file, so this also exercises the
        // deferred forward-ref resolution.
        MonsterRace sauron = MonsterRegistry.lookupMonsterRace("Sauron, the Sorcerer");
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
        MonsterRace maia = MonsterRegistry.lookupMonsterRace("Maia of Varda");
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
    void spellMessagesShareOneListInReverseFileOrderWithTheTypesInterleaved() throws Exception {
        // C's add_alternate_spell_message pushes message-vis, message-invis and message-miss onto the
        // head of the one r->spell_msgs chain, so the chain is in reverse file order with the three
        // types interleaved as they appeared.
        ParseResult<MonsterRace> r = load("spell-messages.txt", withHeader(1, rec("Yeller",
                "spells:SHRIEK | BLINK",
                "message-vis:SHRIEK:It lets out a piercing shriek!",
                "message-invis:BLINK:Something blinks nearby.",
                "message-miss:SHRIEK:The shriek misses.")));
        assertFalse(r.hasErrors(), r.errors()::toString);

        MonsterRace yeller = byName(r, "Yeller");
        assertNotNull(yeller);

        List<MonsterSpellText> messages = field(yeller, "spellMessages");
        assertEquals(3, messages.size(), () -> messages.toString());

        assertEquals(MonsterSpellText.MonsterSpellMessageType.MISS, messages.get(0).getSpellType());
        assertEquals(MonsterSpell.RSF_SHRIEK, messages.get(0).getDamageType());
        assertEquals("The shriek misses.", messages.get(0).getSpellMessage());

        assertEquals(MonsterSpellText.MonsterSpellMessageType.INVIS, messages.get(1).getSpellType());
        assertEquals(MonsterSpell.RSF_BLINK, messages.get(1).getDamageType());
        assertEquals("Something blinks nearby.", messages.get(1).getSpellMessage());

        assertEquals(MonsterSpellText.MonsterSpellMessageType.VIS, messages.get(2).getSpellType());
        assertEquals(MonsterSpell.RSF_SHRIEK, messages.get(2).getDamageType());
        assertEquals("It lets out a piercing shriek!", messages.get(2).getSpellMessage());
    }

    @Test
    void spellMessagesDoNotLeakBetweenMonsters() throws Exception {
        // The list is built fresh per record; a regression once shared one across every monster.
        ParseResult<MonsterRace> r = load("spell-messages-two.txt", withHeader(2,
                rec("Yeller", "spells:SHRIEK", "message-vis:SHRIEK:Loud.")
                        + rec("Quiet", "spells:SHRIEK")));
        assertFalse(r.hasErrors(), r.errors()::toString);

        List<MonsterSpellText> yeller = field(byName(r, "Yeller"), "spellMessages");
        List<MonsterSpellText> quiet = field(byName(r, "Quiet"), "spellMessages");
        assertEquals(1, yeller.size());
        assertTrue(quiet.isEmpty(), () -> quiet.toString());
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
    // (2) A BAD RECORD ENDS THE LOAD
    // =====================================================================

    @Test
    void aBadRecordAfterGoodOnesEndsTheWholeLoadAndNamesItsLine() {
        // Every failure throws, as C ends the load on the parser error. The good records before the bad
        // one are lost with it, and the message names the bad record's name: line (line 8 here).
        String body = rec("Good One", "drop:scroll:Phase Door:100:1:1", "mimic:gold:copper")
                + "name:BadBase\nbase:notabase\ncolor:w\nglyph:t\n"
                + rec("Good Two");

        DataFileParseException e = refused("many-errors.txt", withHeader(3, body));

        assertTrue(e.getMessage().contains("Monster starting at line: 8 of file: "), e::getMessage);
        assertTrue(e.getMessage().contains("many-errors.txt"), e::getMessage);
        assertTrue(e.getMessage().contains("has an illegal base value: notabase"), e::getMessage);
    }

    @Test
    void theFirstBadRecordIsTheOneReportedWhenSeveralAreBad() {
        String body = rec("BadFlagRec", "flags:NOTAFLAG") + rec("BadEffectRec", "blow:CLAW:BADEFFECT:1d1");

        DataFileParseException e = refused("two-bad.txt", withHeader(2, body));

        assertTrue(e.getMessage().contains("illegal flag on value: NOTAFLAG"), e::getMessage);
        assertFalse(e.getMessage().contains("BADEFFECT"), e::getMessage);
    }

    // =====================================================================
    // (3) A SELECTION OF INDIVIDUAL ERROR SITES
    // =====================================================================

    @Test
    void anUnknownBaseEndsTheLoad() {
        DataFileParseException e = refused("bad-base.txt", withHeader(1,
                "name:Nope\nbase:notabase\ncolor:w\nglyph:t\n"));
        assertTrue(e.getMessage().contains("Monster starting at line: 2 of file: "), e::getMessage);
        assertTrue(e.getMessage().contains("has an illegal base value: notabase"), e::getMessage);
    }

    @Test
    void aBaseNameIsMatchedExactlyAsInCsStreq() {
        // lookup_monster_base uses streq, so "Townsfolk" is not "townsfolk".
        DataFileParseException e = refused("base-case.txt", withHeader(1,
                "name:Nope\nbase:Townsfolk\ncolor:w\nglyph:t\n"));
        assertTrue(e.getMessage().contains("illegal base value: Townsfolk"), e::getMessage);
    }

    @Test
    void anUnknownFlagEndsTheLoad() {
        assertRefusedWith("bad-flag.txt", rec("Nope", "flags:BADFLAGON"), "illegal flag on value: BADFLAGON");
    }

    @Test
    void anUnknownFlagsOffFlagEndsTheLoad() {
        assertRefusedWith("bad-flagoff.txt", rec("Nope", "flags-off:BADFLAGOFF"),
                "illegal flag off value: BADFLAGOFF");
    }

    @Test
    void aRaceFlagNamedNoneIsRefusedAsInCsFlagLookupStartsAfterIt() {
        // lookup_flag starts at FLAG_START (1), past the NONE row, so grab_flag and remove_flag both fail.
        assertRefusedWith("flag-none.txt", rec("Nope", "flags:NONE"), "sentinel monster race flag value: NONE");
        assertRefusedWith("flagoff-none.txt", rec("Nope", "flags-off:NONE"),
                "sentinel monster race flag value: NONE");
    }

    @Test
    void aRaceFlagNamedMaxIsRefusedAsInCsTableHasNoMaxRow() {
        // list-mon-race-flags.h has no RF(MAX) row, so the name matches nothing in C either.
        assertRefusedWith("flag-max.txt", rec("Nope", "flags:MAX"), "illegal flag on value: MAX");
    }

    @Test
    void anUnknownSpellEndsTheLoad() {
        assertRefusedWith("bad-spell.txt", rec("Nope", "spells:NOTASPELL"), "illegal spell value: NOTASPELL");
    }

    @Test
    void aSpellNamedNoneIsRefusedAsInCsFlagLookupStartsAfterIt() {
        assertRefusedWith("spell-none.txt", rec("Nope", "spells:NONE"), "sentinel monster spell value: NONE");
    }

    @Test
    void aSpellNamedMaxIsAcceptedAsInCsFlagTableHasAMaxRow() throws Exception {
        // list-mon-spells.h ends with RSF(MAX), so lookup_flag finds it and grab_flag sets that bit.
        ParseResult<MonsterRace> r = load("spell-max.txt", withHeader(1, rec("Solo", "spells:MAX")));
        Flag<MonsterSpell> spells = field(byName(r, "Solo"), "spells");
        assertTrue(spells.has(MonsterSpell.RSF_MAX));
    }

    @Test
    void anUnknownBlowMethodEndsTheLoad() {
        // A full blow with a valid effect and dice, so the method is the only thing wrong.
        assertRefusedWith("bad-method.txt", rec("Nope", "blow:BADMETHOD:HURT:1d1"),
                "illegal blow method value: BADMETHOD");
    }

    @Test
    void aBlowMethodIsMatchedExactlyAsInCsStreq() {
        assertRefusedWith("method-case.txt", rec("Nope", "blow:claw:HURT:1d1"), "illegal blow method value: claw");
    }

    @Test
    void anUnknownBlowEffectEndsTheLoad() {
        assertRefusedWith("bad-effect.txt", rec("Nope", "blow:CLAW:BADEFFECT:1d1"),
                "illegal blow effect value: BADEFFECT");
    }

    @Test
    void anUnknownDropTvalEndsTheLoad() {
        assertRefusedWith("bad-drop-tval.txt", rec("Nope", "drop:notatv:Thing:100:1:1"),
                "illegal drop type value: notatv");
    }

    @Test
    void anUnknownDropBaseTvalEndsTheLoad() {
        assertRefusedWith("bad-dropbase-tval.txt", rec("Nope", "drop-base:notatv:100:1:1"),
                "illegal drop type value: notatv");
    }

    @Test
    void anUnknownDropObjectNameEndsTheLoad() {
        // A valid tval (scroll) with an sval that resolves to no kind -- so the base-name leg is reached.
        assertRefusedWith("bad-drop-sval.txt", rec("Nope", "drop:scroll:NotARealScroll:100:1:1"),
                "illegal drop base value: NotARealScroll");
    }

    @Test
    void anUnknownMimicTvalEndsTheLoad() {
        assertRefusedWith("bad-mimic-tval.txt", rec("Nope", "mimic:notatv:Thing"), "illegal mimic value: notatv");
    }

    @Test
    void anUnknownMimicObjectNameEndsTheLoad() {
        assertRefusedWith("bad-mimic-sval.txt", rec("Nope", "mimic:potion:NotARealPotion"),
                "illegal mimic value: NotARealPotion");
    }

    @Test
    void aFriendChanceAboveOneHundredIsKeptAsWritten() throws Exception {
        // C's parse_monster_friends reads the chance with parser_getuint and applies no upper
        // bound, so 101 and 200 are valid and stored unchanged (they only ever behave as 100).
        ParseResult<MonsterRace> r = load("big-friend-chance.txt", withHeader(1, rec("Boss",
                "friends:101:1d1:Some Monster", "friends:200:1d1:Another Monster")));
        assertEquals(1, r.items().size());
        assertFalse(hasError(r, "out of bounds"), r.errors()::toString);
        List<?> friends = field(r.items().get(0), "friends");
        assertEquals(2, friends.size());
        // Reverse file order, as C's head-pushed chain holds them.
        assertEquals(Integer.valueOf(200), declaredField(friends.get(0), "percentChance"));
        assertEquals(Integer.valueOf(101), declaredField(friends.get(1), "percentChance"));
    }

    @Test
    void anUnknownFriendRoleEndsTheLoad() {
        assertRefusedWith("bad-friend-role.txt", rec("Nope", "friends:50:1d1:Some Monster:badrole"),
                "illegal friends role value: badrole");
    }

    @Test
    void anOverflowingIntegerIsClampedHereWhereCWraps() throws Exception {
        // speed: lexes as MON_INTEGER (all digits) but is above INT_MAX. C's strtol saturates at LONG_MAX
        // and the int field wraps; Java clamps to Integer.MAX_VALUE (docs/Accepted_Deviations.md, "An
        // integer too big for its C type is clamped, where C wraps it").
        ParseResult<MonsterRace> r = load("overflow-speed.txt", withHeader(1, rec("Solo", "speed:99999999999")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(Integer.MAX_VALUE, (int) field(byName(r, "Solo"), "speed"));
    }

    @Test
    void aGlyphThatIsNotExactlyOneCharacterEndsTheLoad() {
        // parse_monster_glyph reads one character, and parser_parse refuses a longer field.
        DataFileParseException e = refused("bad-glyph.txt", withHeader(1,
                "name:Nope\nbase:townsfolk\ncolor:w\nglyph:xy\n"));
        assertTrue(e.getMessage().contains("illegal glyph value: xy"), e::getMessage);
    }

    @Test
    void aWrongRecordCountHeaderIsReported() throws Exception {
        // One valid record but a header claiming 99 -- a soft error raised by GrammarDriver, outside
        // the assembler, so the good record still loads.
        ParseResult<MonsterRace> r = load("bad-count.txt", withHeader(99, rec("Solo")));
        assertEquals(1, r.items().size(), r.errors()::toString);
        assertTrue(hasError(r, "record-count header declares 99 records, but file contains 1"), r.errors()::toString);
    }

    // =====================================================================
    // (4) THE READER'S OWN CONTRACT (mon-init.c: run_parse_monster / finish_parse_monster)
    // =====================================================================

    /**
     * One race as C's parse_monster_* handlers would store it, read straight from the text of
     * monster.txt: the scalar stats and the number of entries each repeatable directive gave.
     */
    private record ExpectedRace(String name, Map<String, Integer> numbers) {
    }

    /**
     * Applies C's rules to the raw lines of a monster.txt-format file: hearing and smell are scaled
     * by {@code max_sight / 20}, and {@code drop:} and {@code drop-base:} share one list.
     */
    private static List<ExpectedRace> expectedFrom(List<String> lines) {
        Map<String, String> scalars = Map.of("speed", "speed", "hit-points", "averageHP", "light", "light",
                "hearing", "hearing", "smell", "smell", "armor-class", "ac", "sleepiness", "sleep",
                "depth", "level", "rarity", "rarity", "experience", "mexp");
        Map<String, String> counted = Map.of("blow", "blow", "drop", "drops", "drop-base", "drops",
                "friends", "friends", "friends-base", "friendsBase", "mimic", "mimicKinds", "shape", "shapes");
        List<ExpectedRace> out = new ArrayList<>();
        ExpectedRace current = null;
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("record-count:")) continue;
            int colon = line.indexOf(':');
            String key = line.substring(0, colon);
            String value = line.substring(colon + 1);
            if (key.equals("name")) {
                current = new ExpectedRace(value, new HashMap<>());
                out.add(current);
            } else if (scalars.containsKey(key)) {
                int n = Integer.parseInt(value.trim());
                if (key.equals("hearing") || key.equals("smell")) {
                    n = n * GameConstants.getPlayerMaxSight() / 20;
                }
                current.numbers().put(scalars.get(key), n);
                if (key.equals("depth")) {
                    // parse_monster_depth: "Level is default spell power". A later spell-power: overwrites it.
                    current.numbers().put("spellPower", n);
                }
            } else if (key.equals("spell-power")) {
                current.numbers().put("spellPower", Integer.parseInt(value.trim()));
            } else if (counted.containsKey(key)) {
                current.numbers().merge(counted.get(key), 1, Integer::sum);
            }
        }
        return out;
    }

    @Test
    void theRealFileComesBackInFileOrderWithThePlayerRecordFirst() {
        // C's chain is built head-first, but finish_parse_monster counts ridx down from r_max - 1 as
        // it walks from the head, so r_info is in file order. The expected names are the name:
        // lines of the data file itself.
        List<String> expected = fileNames(MONSTER_FILE);
        List<String> actual = real.items().stream().map(MonsterRace::getName).toList();

        assertEquals(EXPECTED_RACES, expected.size());
        assertEquals(expected, actual);
        assertEquals("<player>", actual.get(0));
        assertEquals("Morgoth, Lord of Darkness", actual.get(EXPECTED_RACES - 1));
    }

    private static List<String> fileNames(String file) {
        try {
            return Files.readAllLines(Path.of(file)).stream()
                    .filter(l -> l.startsWith("name:"))
                    .map(l -> l.substring("name:".length()))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void everyRealRacesStatsAndEntryCountsMatchAnIndependentReadOfTheText() throws Exception {
        // Spell power is included: depth: sets it to the level and spell-power: overwrites it, in file
        // order. A mismatch on any race would mean the grammar or assembler lost, repeated or rescaled
        // a directive.
        List<ExpectedRace> expected = expectedFrom(Files.readAllLines(Path.of(MONSTER_FILE)));
        assertEquals(EXPECTED_RACES, expected.size());
        assertEquals(EXPECTED_RACES, real.items().size());

        List<String> mismatches = new ArrayList<>();
        for (int i = 0; i < expected.size(); i++) {
            ExpectedRace e = expected.get(i);
            MonsterRace race = real.items().get(i);
            for (String fieldName : List.of("speed", "averageHP", "light", "hearing", "smell", "ac", "sleep",
                    "level", "rarity", "mexp", "spellPower")) {
                int actual = field(race, fieldName);
                int wanted = e.numbers().getOrDefault(fieldName, 0);
                if (actual != wanted) {
                    mismatches.add(e.name() + "." + fieldName + ": C " + wanted + ", Java " + actual);
                }
            }
            for (String listName : List.of("blow", "drops", "friends", "friendsBase", "mimicKinds", "shapes")) {
                int actual = ((List<?>) field(race, listName)).size();
                int wanted = e.numbers().getOrDefault(listName, 0);
                if (actual != wanted) {
                    mismatches.add(e.name() + "." + listName + ": C " + wanted + ", Java " + actual);
                }
            }
        }
        assertTrue(mismatches.isEmpty(), mismatches.size() + " differ, first ones: "
                + mismatches.subList(0, Math.min(10, mismatches.size())));
    }

    @Test
    void parseReturnsTheSameRacesAsParseWithResults() throws IOException {
        List<String> viaParse = new MonsterReader().parse(MONSTER_FILE).stream().map(MonsterRace::getName).toList();
        List<String> viaResults = real.items().stream().map(MonsterRace::getName).toList();

        assertEquals(viaResults, viaParse);
    }

    @Test
    void aMissingFileThrowsFromBothEntryPoints() {
        String absent = tempDir.resolve("absent.txt").toString();

        assertThrows(IOException.class, () -> new MonsterReader().parseWithResults(absent));
        assertThrows(IOException.class, () -> new MonsterReader().parse(absent));
    }

    @Test
    void aSyntaxErrorGivesAnEmptyListFromParseAndTheMessageFromParseWithResults() throws IOException {
        Path file = tempDir.resolve("syntax.txt");
        Files.writeString(file, withHeader(1, "name:Nope\nbase:townsfolk\nspeed:fast\n"));

        assertTrue(new MonsterReader().parse(file.toString()).isEmpty());
        ParseResult<MonsterRace> r = new MonsterReader().parseWithResults(file.toString());
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aMissingRecordCountHeaderIsAHardError() throws IOException {
        // The header is a Java-only requirement; the upstream file has none.
        ParseResult<MonsterRace> r = load("no-header.txt", rec("Solo"));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aDirectiveBeforeTheFirstNameIsAHardError() throws IOException {
        // C: every handler but parse_monster_name returns PARSE_ERROR_MISSING_RECORD_HEADER.
        ParseResult<MonsterRace> r = load("no-name.txt", withHeader(1, "speed:110\n" + rec("Solo")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void directivesInAnyOrderLoadTheSameAsC() throws Exception {
        // C registers each directive separately, so only name: has to come first.
        ParseResult<MonsterRace> r = load("any-order.txt", withHeader(1,
                "name:Shuffled\nspeed:120\nglyph:t\ndepth:7\ncolor:w\nhit-points:33\nbase:townsfolk\n"
                        + "experience:9\nrarity:2\n"));
        assertFalse(r.hasErrors(), r.errors()::toString);

        MonsterRace m = byName(r, "Shuffled");
        assertNotNull(m);
        assertEquals(120, (int) field(m, "speed"));
        assertEquals(7, (int) field(m, "level"));
        assertEquals(33, (int) field(m, "averageHP"));
        assertEquals(9, (int) field(m, "mexp"));
        assertEquals(2, (int) field(m, "rarity"));
    }

    @Test
    void aRepeatedScalarDirectiveTakesTheLastValueAsInC() throws Exception {
        ParseResult<MonsterRace> r = load("repeat.txt", withHeader(1, rec("Twice", "speed:100", "speed:130")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(130, (int) field(byName(r, "Twice"), "speed"));
    }

    @Test
    void duplicateNamesAreBothKeptInFileOrderAsInCsRInfo() throws IOException {
        // C's chain is reversed but r_info is filled from the top, so the first record is the
        // lower index; lookup_monster therefore finds the first.
        ParseResult<MonsterRace> r = load("dup.txt", withHeader(2,
                rec("Twin", "hit-points:1") + rec("Twin", "hit-points:2")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(2, r.items().size());
        assertEquals("Twin", r.items().get(0).getName());
        assertEquals("Twin", r.items().get(1).getName());
    }

    @Test
    void repeatedDescLinesAreJoinedEndToEndLikeCsStringAppend() throws Exception {
        ParseResult<MonsterRace> r = load("desc.txt", withHeader(1, rec("Wordy", "desc:It is a ", "desc:wordy monster.")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals("It is a wordy monster.", field(byName(r, "Wordy"), "text"));
    }

    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        // parser_parse skips leading isspace before reading the directive.
        ParseResult<MonsterRace> r = load("indent.txt", withHeader(1, rec("Solo", "  speed:110")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    // =====================================================================
    // (5) WHERE THE GRAMMAR DIFFERS FROM C (see docs/grammar_issues.md)
    // =====================================================================

    @Test
    void aRecordWithOnlyANameIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<MonsterRace> r = load("name-only.txt", withHeader(1, "name:Bare\n"));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aFileOfOnlyTheHeaderIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        ParseResult<MonsterRace> r = load("empty.txt", withHeader(0, ""));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void anEmptyPluralIsAHardErrorHereWhereCTakesItAsNoPlural() throws IOException {
        // plural is ?str in C, and an empty value leaves race->plural NULL.
        ParseResult<MonsterRace> r = load("plural-empty.txt", withHeader(1, rec("Solo", "plural:")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void anEmptyFlagsValueIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        // flags is ?str in C and parse_monster_flags returns when there is no value.
        ParseResult<MonsterRace> r = load("flags-empty.txt", withHeader(1, rec("Solo", "flags:")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void spaceSeparatedFlagsAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        // strtok(flags, " |") splits on a space as well as a bar.
        ParseResult<MonsterRace> r = load("flags-space.txt", withHeader(1, rec("Solo", "flags:MALE FEMALE")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aColourNameIsAHardErrorHereWhereCAcceptsOne() throws IOException {
        // parse_monster_color: a value longer than one character goes through color_text_to_attr.
        ParseResult<MonsterRace> r = load("colour-name.txt", withHeader(1,
                "name:Nope\nbase:townsfolk\nglyph:t\ncolor:white\n"));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aMessageWithNoTextIsAHardErrorHereWhereCTakesItAsEmpty() throws IOException {
        // message-vis is "sym spell ?str message" in C; a missing or blank message becomes "".
        ParseResult<MonsterRace> r = load("msg-notext.txt", withHeader(1,
                rec("Solo", "spells:SHRIEK", "message-vis:SHRIEK")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aMessageContainingAColonIsAHardErrorHereWhereCKeepsTheRestOfTheLine() throws IOException {
        // The last field of a directive takes the rest of the line in C, colons included.
        ParseResult<MonsterRace> r = load("msg-colon.txt", withHeader(1,
                rec("Solo", "spells:SHRIEK", "message-vis:SHRIEK:It says: hello")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aBareNumberFriendCountIsAHardErrorHereWhereCReadsItAsARandomValue() throws IOException {
        // parse_random accepts "5" (a base and no dice); the lexer reads it as FRIEND_INTEGER.
        ParseResult<MonsterRace> r = load("friend-bare.txt", withHeader(1, rec("Solo", "friends:100:5:Somebody")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aLeadingZeroSpeedIsReadAsOctalAsInC() throws Exception {
        // strtol(tok, &z, 0): 010 is 8 in C.
        ParseResult<MonsterRace> r = load("speed-octal.txt", withHeader(1, rec("Solo", "speed:010")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(8, (int) field(byName(r, "Solo"), "speed"));
    }

    @Test
    void aHexSpeedIsAHardErrorHereWhereCReadsItAsSixteen() throws IOException {
        ParseResult<MonsterRace> r = load("speed-hex.txt", withHeader(1, rec("Solo", "speed:0x10")));
        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aNegativeSpellPowerIsRefusedAsInCsUint() {
        // spell-power is "uint" in C and parser_parse refuses a leading minus.
        assertRefusedWith("power-negative.txt", rec("Solo", "spell-power:-1"), "illegal spell power value: -1");
    }

    @Test
    void aNegativeDropChanceIsRefusedAsInCsUint() {
        assertRefusedWith("drop-negative.txt", rec("Solo", "drop:scroll:Phase Door:-5:1:1"),
                "illegal percentage value: -5");
    }

    // =====================================================================
    // (6) WHERE THE ASSEMBLER DIFFERS FROM C (see docs/Assembler_Issues.md)
    // =====================================================================

    @Test
    void aMonsterWithNoSpellPowerLineGetsItsLevelAsSpellPower() throws Exception {
        // parse_monster_depth sets spell_power = level; spell-power: only overrides it afterwards.
        ParseResult<MonsterRace> r = load("power-default.txt", withHeader(1, rec("Solo", "depth:20")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(20, (int) field(byName(r, "Solo"), "spellPower"));
    }

    @Test
    void aMonsterWithNeitherDepthNorSpellPowerHasSpellPowerZero() throws Exception {
        // A zeroed race: level 0, so spell_power 0.
        ParseResult<MonsterRace> r = load("power-none.txt", withHeader(1, rec("Solo")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(0, (int) field(byName(r, "Solo"), "spellPower"));
    }

    @Test
    void theRealFilesRacesWithNoSpellPowerLineGetTheirLevelAsSpellPower() throws Exception {
        // grey mold is depth:1 with no spell-power: line, so C stores 1.
        MonsterRace mold = byName(real, "grey mold");
        assertNotNull(mold);
        assertEquals(1, (int) field(mold, "level"));
        assertEquals(1, (int) field(mold, "spellPower"));
    }

    @Test
    void aSpellPowerLineAfterDepthOverridesTheLevel() throws Exception {
        // parse_monster_spell_power runs after parse_monster_depth, so the explicit value wins.
        ParseResult<MonsterRace> r = load("depth-then-power.txt", withHeader(1,
                rec("Solo", "depth:20", "spell-power:5")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(5, (int) field(byName(r, "Solo"), "spellPower"));
    }

    @Test
    void aSpellPowerLineBeforeDepthIsOverwrittenByTheLevel() throws Exception {
        // C: depth: runs after spell-power: and sets spell_power = level, so C gives 20.
        ParseResult<MonsterRace> r = load("power-then-depth.txt", withHeader(1,
                rec("Solo", "spell-power:5", "depth:20")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(20, (int) field(byName(r, "Solo"), "spellPower"));
    }

    @Test
    void aSecondDepthLineResetsSpellPowerToTheNewLevel() throws Exception {
        // Each depth: line runs parse_monster_depth again, so it overwrites an earlier spell-power: too.
        ParseResult<MonsterRace> r = load("depth-power-depth.txt", withHeader(1,
                rec("Solo", "depth:10", "spell-power:5", "depth:20")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(20, (int) field(byName(r, "Solo"), "spellPower"));
    }

    @Test
    void aMonsterWithNoPluralLineHasANullPluralAsCDoes() throws Exception {
        ParseResult<MonsterRace> r = load("plural-absent.txt", withHeader(1, rec("Solo")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertNull(field(byName(r, "Solo"), "plural"));
    }

    @Test
    void aMonsterWithNoColourLineGetsDarkAsCLeavesTheAttrAtZero() throws Exception {
        ParseResult<MonsterRace> r = load("no-colour.txt", withHeader(1, "name:Grey\nbase:townsfolk\nglyph:t\n"));
        assertFalse(r.hasErrors(), r.errors()::toString);
        AngbandDisplayCharacter adc = field(byName(r, "Grey"), "adc");
        assertEquals(ColourEnum.COLOUR_DARK, adc.getAttributeColour());
        assertEquals('t', adc.getNarrowCharacter());
    }

    @Test
    void aMessageForAnUnknownSpellEndsTheLoadAsInC() {
        // C: grab_name fails -> PARSE_ERROR_INVALID_SPELL_NAME.
        assertRefusedWith("msg-unknown-spell.txt", rec("Solo", "spells:SHRIEK", "message-vis:NOTASPELL:Hello"),
                "illegal spell message: message-vis:NOTASPELL:Hello");
    }

    @Test
    void aMessageForTheNoneAndMaxRowsIsAcceptedAsInCsGrabName() throws Exception {
        // grab_name scans N_ELEMENTS(r_info_spell_flags) - 1 rows, which is every row from NONE to MAX.
        ParseResult<MonsterRace> r = load("msg-sentinels.txt", withHeader(1, rec("Solo",
                "message-vis:NONE:Nothing.", "message-miss:MAX:Everything.")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<MonsterSpellText> messages = field(byName(r, "Solo"), "spellMessages");
        assertEquals(2, messages.size());
        assertEquals(MonsterSpell.RSF_MAX, messages.get(0).getDamageType());
        assertEquals(MonsterSpell.RSF_NONE, messages.get(1).getDamageType());
    }

    @Test
    void aMessageOfOnlySpacesAndTabsIsStoredEmptyAsInC() throws Exception {
        // add_alternate_spell_message: a NULL message or one that contains_only_spaces becomes "".
        ParseResult<MonsterRace> r = load("msg-blank.txt", withHeader(1, rec("Solo",
                "message-vis:SHRIEK: \t ")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<MonsterSpellText> messages = field(byName(r, "Solo"), "spellMessages");
        assertEquals(1, messages.size());
        assertEquals("", messages.get(0).getSpellMessage());
    }

    @Test
    void aMessageWithTextAfterLeadingSpacesIsKeptWhole() throws Exception {
        ParseResult<MonsterRace> r = load("msg-spaced.txt", withHeader(1, rec("Solo",
                "message-vis:SHRIEK: a shriek ")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<MonsterSpellText> messages = field(byName(r, "Solo"), "spellMessages");
        assertEquals(" a shriek ", messages.get(0).getSpellMessage());
    }

    @Test
    void aMessageForASpellTheMonsterDoesNotCastIsKeptAsInC() throws Exception {
        // C keeps every message whose spell name is valid, whether or not spells: lists it.
        ParseResult<MonsterRace> r = load("msg-uncast-spell.txt", withHeader(1,
                rec("Solo", "spells:SHRIEK", "message-vis:BLINK:Hello")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<MonsterSpellText> messages = field(byName(r, "Solo"), "spellMessages");
        assertEquals(1, messages.size());
        assertEquals(MonsterSpell.RSF_BLINK, messages.get(0).getDamageType());
    }

    @Test
    void anUnknownColourCycleGroupIsSilentAsInC() throws Exception {
        // visuals_cycler_set_cycle_for_race returns quietly when the cycle is not found.
        ParseResult<MonsterRace> r = load("cycle-group.txt", withHeader(1,
                rec("Solo", "color-cycle:nogroup:nocycle")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(1, r.items().size());
        assertNull(field(byName(r, "Solo"), "cycler"));
    }

    @Test
    void anUnknownColourCycleNameInAKnownGroupIsSilentAsInC() throws Exception {
        ParseResult<MonsterRace> r = load("cycle-name.txt", withHeader(1,
                rec("Solo", "color-cycle:fancy:nocycle")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertNull(field(byName(r, "Solo"), "cycler"));
    }

    @Test
    void aDropWithMinOrMaxAbove99IsRefusedAsInCInvalidItemNumber() {
        assertRefusedWith("drop-big-min.txt", rec("Solo", "drop:scroll:Phase Door:100:100:5"),
                "out of bounds minimum value: 100");
        assertRefusedWith("drop-big-max.txt", rec("Solo", "drop:scroll:Phase Door:100:5:100"),
                "out of bounds maximum value: 100");
    }

    @Test
    void aDropWithMinAndMaxFrom0To99IsAcceptedAtBothEdges() throws Exception {
        ParseResult<MonsterRace> r = load("drop-edges.txt", withHeader(1, rec("Solo",
                "drop:scroll:Phase Door:0:0:99")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> drops = field(byName(r, "Solo"), "drops");
        assertEquals(Integer.valueOf(0), declaredField(drops.get(0), "min"));
        assertEquals(Integer.valueOf(99), declaredField(drops.get(0), "max"));
        assertEquals(Integer.valueOf(0), declaredField(drops.get(0), "percentChance"));
    }

    @Test
    void aDropWithANegativeMinOrMaxIsRefusedAsInCsUint() {
        assertRefusedWith("drop-neg-min.txt", rec("Solo", "drop:scroll:Phase Door:100:-1:5"),
                "illegal minimum value: -1");
        assertRefusedWith("drop-neg-max.txt", rec("Solo", "drop:scroll:Phase Door:100:5:-1"),
                "illegal maximum value: -1");
    }

    @Test
    void dropsAreInReverseFileOrderAsInCsChain() throws Exception {
        // parse_monster_drop pushes on the head of r->drops, so C walks the later line first.
        ParseResult<MonsterRace> r = load("drop-order.txt", withHeader(1, rec("Solo",
                "drop:scroll:Phase Door:10:1:1", "drop:potion:Cure Light Wounds:20:1:1")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> drops = field(byName(r, "Solo"), "drops");
        assertEquals(2, drops.size());
        assertEquals(Integer.valueOf(20), declaredField(drops.get(0), "percentChance"));
        assertEquals(Integer.valueOf(10), declaredField(drops.get(1), "percentChance"));
    }

    @Test
    void aDropFollowedByADropBaseHasTheDropBaseFirstAsInC() throws Exception {
        // drop: and drop-base: share one chain in C, each pushed on the head, so the later
        // drop-base: line is walked first. drop-base: carries no kind, drop: does.
        ParseResult<MonsterRace> r = load("drop-mixed.txt", withHeader(1, rec("Solo",
                "drop:scroll:Phase Door:100:1:1", "drop-base:mushroom:100:1:2")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> drops = field(byName(r, "Solo"), "drops");
        assertEquals(2, drops.size());
        assertNull(declaredField(drops.get(0), "kind"));
        assertNotNull(declaredField(drops.get(1), "kind"));
    }

    @Test
    void dropAndDropBaseLinesInterleaveInReverseFileOrderAsInC() throws Exception {
        // Three lines alternating kinds: the single chain is the whole file reversed, which a
        // drop:-list-then-drop-base:-list split could not reproduce.
        ParseResult<MonsterRace> r = load("drop-interleave.txt", withHeader(1, rec("Solo",
                "drop:scroll:Phase Door:10:1:1",
                "drop-base:mushroom:20:1:2",
                "drop:potion:Cure Light Wounds:30:1:1")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> drops = field(byName(r, "Solo"), "drops");
        assertEquals(3, drops.size());
        assertEquals(Integer.valueOf(30), declaredField(drops.get(0), "percentChance"));
        assertNotNull(declaredField(drops.get(0), "kind"));
        assertEquals(Integer.valueOf(20), declaredField(drops.get(1), "percentChance"));
        assertNull(declaredField(drops.get(1), "kind"));
        assertEquals(Integer.valueOf(10), declaredField(drops.get(2), "percentChance"));
        assertNotNull(declaredField(drops.get(2), "kind"));
    }

    @Test
    void friendsAreInReverseFileOrderAsInCsChain() throws Exception {
        ParseResult<MonsterRace> r = load("friend-order.txt", withHeader(1, rec("Solo",
                "friends:100:1d1:Alpha", "friends:100:1d1:Beta")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> friends = field(byName(r, "Solo"), "friends");
        assertEquals("Beta", declaredField(friends.get(0), "name"));
        assertEquals("Alpha", declaredField(friends.get(1), "name"));
    }

    @Test
    void friendsBaseAreInReverseFileOrderAsInCsChain() throws Exception {
        // parse_monster_friends_base pushes on the head of r->friends_base, so C walks the later
        // line first.
        ParseResult<MonsterRace> r = load("friend-base-order.txt", withHeader(1, rec("Solo",
                "friends-base:100:1d1:spider", "friends-base:100:1d1:ant")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> friendsBase = field(byName(r, "Solo"), "friendsBase");
        assertEquals(2, friendsBase.size());
        MonsterBase first = declaredField(friendsBase.get(0), "base");
        MonsterBase second = declaredField(friendsBase.get(1), "base");
        assertEquals("ant", first.getCodeName());
        assertEquals("spider", second.getCodeName());
    }

    @Test
    void mimicsAreInReverseFileOrderAsInCsChain() throws Exception {
        // parse_monster_mimic pushes on the head of r->mimic_kinds, so C walks the later line first.
        ParseResult<MonsterRace> r = load("mimic-order.txt", withHeader(1, rec("Solo",
                "mimic:gold:copper", "mimic:potion:Cure Light Wounds")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> mimics = field(byName(r, "Solo"), "mimicKinds");
        assertEquals(2, mimics.size());
        assertEquals("Cure Light Wounds", declaredField(mimics.get(0), "name"));
        assertEquals("copper", declaredField(mimics.get(1), "name"));
    }

    @Test
    void shapesAreInReverseFileOrderAsInCsChain() throws Exception {
        // parse_monster_shape pushes on the head of r->shapes, so C walks the later line first. The
        // names stay unresolved here: resolveShapes runs only once every race is loaded.
        ParseResult<MonsterRace> r = load("shape-order.txt", withHeader(1, rec("Solo",
                "shape:Alpha", "shape:Beta", "shape:Gamma")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> shapes = field(byName(r, "Solo"), "shapes");
        assertEquals(3, shapes.size());
        assertEquals("Gamma", declaredField(shapes.get(0), "name"));
        assertEquals("Beta", declaredField(shapes.get(1), "name"));
        assertEquals("Alpha", declaredField(shapes.get(2), "name"));
    }

    @Test
    void aFriendsBaseChanceAbove100IsKeptAsInC() throws Exception {
        // parse_monster_friends_base stores parser_getuint(chance) with no upper bound.
        ParseResult<MonsterRace> r = load("fb-chance.txt", withHeader(1, rec("Solo", "friends-base:150:1d2:spider")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> friendsBase = field(byName(r, "Solo"), "friendsBase");
        assertEquals(Integer.valueOf(150), declaredField(friendsBase.get(0), "percentChance"));
    }

    @Test
    void aFriendRoleInCapitalsIsRefusedAsInCsCaseSensitiveStreq() {
        // streq(role_name, "servant") in C, so "Servant" is PARSE_ERROR_INVALID_MONSTER_ROLE.
        assertRefusedWith("role-case.txt", rec("Solo", "friends:100:1d2:Somebody:Servant"),
                "illegal friends role value: Servant");
        assertRefusedWith("role-case-base.txt", rec("Solo", "friends-base:100:1d2:spider:Bodyguard"),
                "illegal friends role value: Bodyguard");
    }

    @Test
    void aNegativeFriendChanceIsRefusedAsInCsUint() {
        assertRefusedWith("friend-neg.txt", rec("Solo", "friends:-5:1d2:Somebody"), "illegal chance value: -5");
        assertRefusedWith("friend-base-neg.txt", rec("Solo", "friends-base:-5:1d2:spider"),
                "illegal chance value: -5");
    }

    @Test
    void aFriendsBaseNamingNoBaseEndsTheLoad() {
        assertRefusedWith("friend-base-unknown.txt", rec("Solo", "friends-base:100:1d2:notabase"),
                "illegal friends base value: notabase");
    }

    @Test
    void aFriendCountThatOverflowsIntEndsTheLoadAsInC() {
        // C: parse_random rejects a number above INT_MAX, so the friends line is a parser error.
        assertRefusedWith("die-overflow.txt", rec("Solo", "friends:100:99999999999d2:Somebody"),
                "illegal friend die value: 99999999999d2");
    }

    @Test
    void aFriendBaseCountThatOverflowsIntEndsTheLoadAsInC() {
        assertRefusedWith("die-base-overflow.txt", rec("Solo", "friends-base:100:99999999999d2:spider"),
                "illegal friends base die value: 99999999999d2");
    }

    @Test
    void twoBadFriendsBaseLinesStillEndTheLoadButTheLaterLineIsReportedFirst() {
        // C stops at the first bad line in file order (the die, at parse time). The grammar keeps
        // friends-base: entries in C's head-pushed (reverse file) order and the assembler walks that list,
        // so the later line's fault is the one named. Either way the load ends.
        DataFileParseException e = refused("friends-base-two-bad.txt", withHeader(1,
                rec("Solo",
                        "friends-base:100:99999999999d2:spider",
                        "friends-base:100:1d2:spider:wizard")));
        assertTrue(e.getMessage().contains("illegal friends role value: wizard"), e::getMessage);
    }

    @Test
    void aBadDieOnAFriendsBaseLineHidesThatLinesRoleErrorAsInC() {
        // C: parser_parse rejects the rand field before parse_monster_friends_base runs, so the
        // role is never looked at on a line whose die is bad.
        DataFileParseException e = refused("friends-base-two-faults.txt", withHeader(1,
                rec("Solo", "friends-base:100:99999999999d2:spider:wizard")));
        assertTrue(e.getMessage().contains("illegal friends base die value"), e::getMessage);
        assertFalse(e.getMessage().contains("friends role"), e::getMessage);
    }

    @Test
    void aBlowDamageThatOverflowsIntEndsTheLoadAsInC() {
        assertRefusedWith("blow-overflow.txt", rec("Solo", "blow:CLAW:HURT:99999999999d2"),
                "illegal blow damage value: 99999999999d2");
    }

    @Test
    void aFlagsOffLineBeforeBaseRemovesTheBaseFlagHereWhereCAddsItBack() throws Exception {
        // C: parse_monster_base does rf_union with the base's flags when base: is read, after
        // flags-off: has run, so SPIRIT (a townsfolk flag) is set in C.
        ParseResult<MonsterRace> r = load("flagsoff-first.txt", withHeader(1,
                "name:Solo\nflags-off:SPIRIT\nbase:townsfolk\ncolor:w\nglyph:t\n"));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertFalse(byName(r, "Solo").hasMonsterRaceFlag(MonsterRaceFlag.RF_SPIRIT));
    }

    @Test
    void aGlyphLineBeforeBaseKeepsItsGlyphHereWhereCOverwritesItWithTheBases() throws Exception {
        // C: parse_monster_base sets d_char from the base ('t' for townsfolk) when base: is read.
        ParseResult<MonsterRace> r = load("glyph-first.txt", withHeader(1,
                "name:Solo\nglyph:x\nbase:townsfolk\ncolor:w\n"));
        assertFalse(r.hasErrors(), r.errors()::toString);
        AngbandDisplayCharacter adc = field(byName(r, "Solo"), "adc");
        assertEquals('x', adc.getNarrowCharacter());
    }

    // =====================================================================
    // (7) SPELL POWER: parse_monster_depth AND parse_monster_spell_power
    // =====================================================================

    @Test
    void aNegativeDepthLoadsWithTheSameNegativeSpellPowerAsInC() throws Exception {
        // parse_monster_depth reads level as a signed int and copies it to spell_power with no sign check.
        ParseResult<MonsterRace> r = load("depth-negative.txt", withHeader(1, rec("Solo", "depth:-5")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(-5, (int) field(byName(r, "Solo"), "level"));
        assertEquals(-5, (int) field(byName(r, "Solo"), "spellPower"));
    }

    @Test
    void aNegativeDepthFollowedBySpellPowerKeepsTheSpellPower() throws Exception {
        ParseResult<MonsterRace> r = load("depth-neg-power.txt", withHeader(1,
                rec("Solo", "depth:-5", "spell-power:3")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(-5, (int) field(byName(r, "Solo"), "level"));
        assertEquals(3, (int) field(byName(r, "Solo"), "spellPower"));
    }

    @Test
    void aSpellPowerOfMinusZeroIsRefusedAsInCsLeadingMinusTest() {
        // parser_parse tests *tok == '-', not the value, so -0 is refused too.
        assertRefusedWith("power-minus-zero.txt", rec("Solo", "spell-power:-0"), "illegal spell power value: -0");
    }

    @Test
    void aNegativeSpellPowerAfterDepthIsRefusedAsInC() {
        assertRefusedWith("depth-then-neg-power.txt", rec("Solo", "depth:20", "spell-power:-1"),
                "illegal spell power value: -1");
    }

    @Test
    void aNegativeSpellPowerOverwrittenByALaterDepthLoadsHereWhereCRefusesIt() throws Exception {
        // ACCEPTED DEVIATION (docs/Accepted_Deviations.md, "Monster"): C refuses "spell-power:-1" as it
        // reads the line. The parse record keeps only the last of the two lines, so the depth wins.
        ParseResult<MonsterRace> r = load("neg-power-then-depth.txt", withHeader(1,
                rec("Solo", "spell-power:-1", "depth:20")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(20, (int) field(byName(r, "Solo"), "spellPower"));
    }

    // =====================================================================
    // (8) SENTINEL ROWS, ABSENT BASE, BLANK AND NEGATIVE FIELDS
    // =====================================================================

    @Test
    void aSpellNamedNoneAmongOthersOnASpellsLineIsStillRefused() throws Exception {
        // spells: goes through grab_flag, which refuses NONE wherever it falls in the list; message-*:
        // goes through grab_name, which accepts it (pinned above).
        assertRefusedWith("spells-none-again.txt", rec("Solo", "spells:SHRIEK | NONE"),
                "sentinel monster spell value: NONE");
    }

    @Test
    void aRecordWithNoBaseLineLoadsWithANullBaseAsInC() throws Exception {
        // parse_monster_base never runs, so r->base stays NULL and the flags and glyph come from the record.
        ParseResult<MonsterRace> r = load("no-base.txt", withHeader(1, "name:Solo\nglyph:t\ncolor:w\n"));
        assertFalse(r.hasErrors(), r.errors()::toString);
        MonsterRace solo = byName(r, "Solo");
        assertNull(field(solo, "base"));
        AngbandDisplayCharacter adc = field(solo, "adc");
        assertEquals('t', adc.getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, adc.getAttributeColour());
    }

    @Test
    void aDropBaseWithNoKindKeepsItsTvalAndFigures() throws Exception {
        ParseResult<MonsterRace> r = load("dropbase-fields.txt", withHeader(1, rec("Solo",
                "drop-base:mushroom:25:2:7")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        List<?> drops = field(byName(r, "Solo"), "drops");
        assertEquals(1, drops.size());
        assertNull(declaredField(drops.get(0), "kind"));
        assertEquals(Integer.valueOf(25), declaredField(drops.get(0), "percentChance"));
        assertEquals(Integer.valueOf(2), declaredField(drops.get(0), "min"));
        assertEquals(Integer.valueOf(7), declaredField(drops.get(0), "max"));
    }

    @Test
    void aMonsterWithNoDescLineHasNullTextAsInC() throws Exception {
        ParseResult<MonsterRace> r = load("no-desc.txt", withHeader(1, rec("Solo")));
        assertFalse(r.hasErrors(), r.errors()::toString);
        assertNull(field(byName(r, "Solo"), "text"));
    }
}
