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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link TrapReader}: file text -> {@code TrapLexer}/
 * {@code TrapGrammar} -> {@code TrapParseRecord} -> {@code TrapAssembler} -> {@link TrapKind},
 * the exact chain {@code TerrainDataLoader.loadTraps()} drives. The expected values are worked out
 * from the C original, {@code parse_trap_*} in {@code init.c}, and from the upstream
 * {@code lib/gamedata/trap.txt}, never from the Java output.
 *
 * <p>The suite has five parts:
 * <ol>
 *   <li><b>The real file</b> - all forty kinds load with no errors, and {@link #UPSTREAM_ROWS} (one
 *       row per kind, worked out from the upstream file and C's colour table) agrees with every
 *       field the reader fills.</li>
 *   <li><b>The assembler half</b> - the {@code name:}/{@code desc:} crossover, {@code save:} as
 *       <em>object</em> flags against {@code flags:} as <em>trap</em> flags, effect lists, and
 *       {@code trapKindIndex}.</li>
 *   <li><b>The reader's contract</b> - file order, the two error channels, the header, and a
 *       dropped record closing the gap in the indexes.</li>
 *   <li><b>Where Java matches C</b> - repeated lines join or overwrite as C's {@code string_append}
 *       and plain assignments do, flags accumulate, directives take any order, colour names ignore
 *       case.</li>
 *   <li><b>Where Java differs from C</b> - one test per difference, named
 *       {@code ...HereWhereC...}, written up in {@code docs/grammar_issues.md} and
 *       {@code docs/Assembler_Issues.md}.</li>
 * </ol>
 *
 * <p>The shipped {@code trap.txt} uses {@code EF_SUMMON} effects (the summoning runes), which
 * {@code EffectAssembler} resolves via {@link MonsterRegistry#lookupSummon}; loading summons needs
 * monster bases, which need monster pains. Rather than run the whole heavy {@code init()},
 * {@link #seed()} loads {@code pain.txt}, {@code monster_base.txt} and {@code summon.txt} through
 * their readers and injects them into the private static registries by reflection, restoring them
 * afterwards so no global state leaks to other suites.
 *
 * <p>{@link TrapKind} exposes no getters for its colour, numbers, power or messages, so those
 * fields are read reflectively via {@link #field}.
 *
 * @author Rowan Crowther
 */
class TrapReaderTest {

    private static final String REAL_FILE = "lib/gamedata/trap.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";

    private static Object savedPains;
    private static Object savedBases;
    private static Object savedSummons;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seed() throws Exception {
        // Order matters: summons resolve monster bases, bases resolve pains.
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        savedPains = setStatic("monsterPains", pains);

        List<MonsterBase> bases = new MonsterBaseReader().parseWithResults(BASE_FILE).items();
        savedBases = setStatic("monsterBases", bases);

        List<Summon> summons = new SummonReader().parseWithResults(SUMMON_FILE).items();
        savedSummons = setStatic("summons", summons);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
        setStatic("summons", savedSummons);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static List<TrapKind> loadReal() throws IOException {
        return new TrapReader().parseWithResults(REAL_FILE).items();
    }

    private static TrapKind byDescription(List<TrapKind> traps, String description) {
        return traps.stream()
                .filter(t -> description.equals(t.getDescription()))
                .findFirst().orElse(null);
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAll40TrapsWithNoErrors() throws IOException {
        ParseResult<TrapKind> result = new TrapReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(40, result.items().size());
    }

    @Test
    void crossoverDescriptionsResolveOnTheRealFile() throws IOException {
        // description <- name:'s 2nd field; the three "pit" records share field 1 but keep
        // distinct descriptions, so resolving them by description proves it is field 2, not field 1.
        List<TrapKind> traps = loadReal();

        assertNotNull(byDescription(traps, "trap door"));
        assertNotNull(byDescription(traps, "glyph of warding"));
        assertNotNull(byDescription(traps, "pit"));
        assertNotNull(byDescription(traps, "spiked pit"));
        assertNotNull(byDescription(traps, "poison pit"));
        assertNotNull(byDescription(traps, "no trap"));
    }

    @Test
    void textHoldsDescDirectiveWhileDescriptionHoldsNameField() throws IOException {
        // The crossover, the other way round: text <- desc: directive, description <- name: field 2.
        List<TrapKind> traps = loadReal();

        TrapKind trapDoor = byDescription(traps, "trap door");
        assertEquals("trap door", trapDoor.getDescription());
        assertEquals("It looks like floor, but step incautiously and you will fall through.",
                trapDoor.getText());

        // "no trap" carries no desc: line, so text accumulates to the empty string.
        assertEquals("", byDescription(traps, "no trap").getText());
    }

    @Test
    void saveFlagsAreObjectFlagsAndTrapFlagsAreSeparate() throws IOException {
        // trap door: flags:TRAP | FLOOR | DOWN  and  save:FEATHER.
        TrapKind trapDoor = byDescription(loadReal(), "trap door");

        // save: -> object flags (OF_), not trap flags.
        assertTrue(trapDoor.getSaveFlags().has(ObjectFlag.OF_FEATHER));

        // flags: -> trap flags (TRF_); the DOWN/FLOOR/TRAP set is present, MAGICAL is not.
        assertTrue(trapDoor.getFlags().has(TrapEnum.TRF_TRAP));
        assertTrue(trapDoor.getFlags().has(TrapEnum.TRF_FLOOR));
        assertTrue(trapDoor.getFlags().has(TrapEnum.TRF_DOWN));
        assertFalse(trapDoor.getFlags().has(TrapEnum.TRF_MAGICAL));
    }

    @Test
    void effectsAndXtraEffectsAccumulatePerRecord() throws IOException {
        List<TrapKind> traps = loadReal();

        // blast trap: four back-to-back effect: lines (LIGHT/SOUND/FIRE/FORCE), no effect-xtra.
        TrapKind blast = byDescription(traps, "blast trap");
        assertEquals(4, blast.getEffect().size());
        assertEquals(0, blast.getEffectXtra().size());

        // brain smashing trap: DAMAGE + four TIMED_INC effects.
        assertEquals(5, byDescription(traps, "brain smashing trap").getEffect().size());

        // spiked pit: one primary effect, two effect-xtra (DAMAGE, TIMED_INC:CUT).
        TrapKind spiked = byDescription(traps, "spiked pit");
        assertEquals(1, spiked.getEffect().size());
        assertEquals(2, spiked.getEffectXtra().size());
    }

    @Test
    void trapKindIndicesAreDistinctAndFileOrdered() throws IOException {
        List<TrapKind> traps = loadReal();

        Set<Integer> indices = traps.stream()
                .map(TrapKind::getTrapKindIndex)
                .collect(Collectors.toSet());
        assertEquals(40, indices.size(), "indices must be distinct");
        assertEquals(0, Collections.min(indices));
        assertEquals(39, Collections.max(indices));

        // File position, 0-based: no trap first, glyph of warding second, trap door sixth.
        assertEquals(0, byDescription(traps, "no trap").getTrapKindIndex());
        assertEquals(1, byDescription(traps, "glyph of warding").getTrapKindIndex());
        assertEquals(5, byDescription(traps, "trap door").getTrapKindIndex());
    }

    // ---- Soft-error drops (synthetic fixtures) ---------------------------

    @Test
    void unknownTrapFlagIsSoftErrorAndDropsTheRecord() throws IOException {
        String path = tempFile("bad-flag.txt",
                "record-count:1\n" +
                        "name:test:test trap\n" +
                        "graphics:^:w\n" +
                        "flags:TRAP | NOTAFLAG\n");

        ParseResult<TrapKind> result = new TrapReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("unknown flag") && e.contains("NOTAFLAG")),
                () -> result.errors().toString());
    }

    @Test
    void unknownSaveFlagIsSoftErrorAndDropsTheRecord() throws IOException {
        String path = tempFile("bad-save.txt",
                "record-count:1\n" +
                        "name:test:test trap\n" +
                        "graphics:^:w\n" +
                        "save:NOTAFLAG\n");

        ParseResult<TrapKind> result = new TrapReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("unknown save flag") && e.contains("NOTAFLAG")),
                () -> result.errors().toString());
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    @SuppressWarnings("unchecked")
    private static <T> T field(TrapKind kind, String name) throws Exception {
        Field f = TrapKind.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(kind);
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    private ParseResult<TrapKind> load(String name, String content) throws IOException {
        return new TrapReader().parseWithResults(tempFile(name, content));
    }

    private static boolean hasError(ParseResult<TrapKind> r, String fragment) {
        return r.errors().stream().anyMatch(e -> e.contains(fragment));
    }

    private static void assertHardError(ParseResult<TrapKind> r) {
        assertTrue(r.hasErrors());
        assertTrue(r.items().isEmpty(), "a hard error must not leak the kinds that did parse");
    }

    private static String namesOf(List<TrapKind> traps) {
        return traps.stream().map(TrapKind::getDescription).collect(Collectors.joining(","));
    }

    private static String effectNames(List<Effect> effects) throws Exception {
        List<String> names = new java.util.ArrayList<>();
        for (Effect e : effects) {
            Field f = Effect.class.getDeclaredField("index");
            f.setAccessible(true);
            names.add(f.get(e).toString().substring("EF_".length()));
        }
        return String.join(",", names);
    }

    /**
     * Renders a kind the way {@link #UPSTREAM_ROWS} does, so one string compare covers every
     * field the reader fills. {@code none} stands for a {@code null} power.
     */
    private static String rowOf(TrapKind t) throws Exception {
        AngbandDisplayCharacter adc = field(t, "angbandDisplayCharacter");
        Random power = field(t, "power");
        StringBuilder flags = new StringBuilder();
        for (TrapEnum f : TrapEnum.values()) {
            if (f == TrapEnum.TRF_NONE || f == TrapEnum.TRF_MAX || !t.getFlags().has(f)) continue;
            if (!flags.isEmpty()) flags.append('+');
            flags.append(f.name().substring("TRF_".length()));
        }
        StringBuilder save = new StringBuilder();
        for (ObjectFlag f : ObjectFlag.values()) {
            if (f == ObjectFlag.OF_NONE || f == ObjectFlag.OF_MAX || !t.getSaveFlags().has(f)) continue;
            if (!save.isEmpty()) save.append('+');
            save.append(f.name().substring("OF_".length()));
        }
        return String.join("|",
                String.valueOf(t.getTrapKindIndex()), t.getTrapKindName(), t.getDescription(),
                String.valueOf(adc.getCharacter()), ColourEnum.attributeToString(adc.getAttributeColour()),
                String.valueOf((int) field(t, "rarity")), String.valueOf((int) field(t, "minDepth")),
                String.valueOf((int) field(t, "maxNum")),
                power == null ? "none" : power.getBase() + "," + power.getDice() + ","
                        + power.getSides() + "," + power.getMBonus(),
                flags, save, t.getText(),
                field(t, "message"), field(t, "messageOnSave"), field(t, "messageOnFailure"),
                field(t, "messageOnExtraEffect"),
                effectNames(t.getEffect()), effectNames(t.getEffectXtra()));
    }

    /**
     * One row per kind of the upstream {@code trap.txt}, worked out from that file and from C's
     * {@code color_table} in {@code z-color.c}, not from the Java reader. The fields are
     * index, {@code name:} field one, {@code name:} field two, glyph, colour name, the three
     * {@code appear:} numbers, {@code base,dice,sides,m_bonus} of {@code visibility:} ({@code none}
     * where the record has no such line), trap flags, save flags, the joined {@code desc:} text,
     * then {@code msg:}, {@code msg-good:}, {@code msg-bad:}, {@code msg-xtra:}, the effect types
     * and the extra effect types.
     */
    private static final String[] UPSTREAM_ROWS = {
            "0|no trap|no trap| |White|0|0|0|none|||||||||",
            "1|glyph of warding|glyph of warding|;|Yellow|0|0|0|none|GLYPH+VISIBLE+FLOOR||A magical rune which monsters cannot pass without difficulty.||||||",
            "2|decoy|decoy|;|Light Green|0|0|0|none|GLYPH+VISIBLE+FLOOR||A magical decoy which sounds and looks like the player.||||||",
            "3|door lock|door lock| |White|0|0|0|none|INVISIBLE+LOCK||A device which makes a closed door harder to open.||||||",
            "4|web|web|%|Yellow|0|0|0|none|VISIBLE+WEB||A tangle of sticky spider webs.||||||",
            "5|trap door|trap door|^|White|1|2|0|0,2,10,50|TRAP+FLOOR+DOWN|FEATHER|It looks like floor, but step incautiously and you will fall through.|You fall through a trap door!|You float gently down to the next level.|||DAMAGE|",
            "6|pit|pit|^|Slate|1|2|0|90,0,0,0|TRAP+FLOOR+PIT|FEATHER|A hole dug to snare the unwary.|You fall into a pit!|You float gently to the bottom of the pit.|||DAMAGE|",
            "7|pit|spiked pit|^|Slate|1|2|0|80,0,0,0|TRAP+FLOOR+PIT|FEATHER|A hole dug to snare the unwary, with spikes.|You fall into a spiked pit!|You float gently to the bottom of the pit, neatly avoiding the spikes.||You are impaled!|DAMAGE|DAMAGE,TIMED_INC",
            "8|pit|poison pit|^|Slate|1|2|0|70,0,0,0|TRAP+FLOOR+PIT|FEATHER|A hole dug to snare the unwary, with poisoned spikes.|You fall into a spiked pit!|You float gently to the bottom of the pit, neatly avoiding the spikes.||You are impaled on poisonous spikes!|DAMAGE|TIMED_INC,TIMED_INC",
            "9|strange rune|rune of summon foe|^|Light Blue|1|3|0|0,0,0,0|TRAP+FLOOR+ONETIME+MAGICAL||A magical rune which calls a monster to attack you.|A new foe appears from nowhere!||||SUMMON|",
            "10|strange rune|rune of summoning|^|Light Blue|2|3|0|0,0,0,0|TRAP+FLOOR+ONETIME+MAGICAL||A magical rune which calls up monsters to attack you.|You are enveloped in a cloud of smoke!||||SUMMON|",
            "11|strange rune|rune of necromancy|^|Light Blue|1|20|0|0,0,0,0|TRAP+FLOOR+ONETIME+MAGICAL||A magical rune which calls the undead to attack you.|A sense of dread washes over you...||||SUMMON|",
            "12|strange rune|rune of dragonsong|^|Light Blue|1|20|0|0,0,0,0|TRAP+FLOOR+ONETIME+MAGICAL||A magical rune which calls dragons to attack you.|You hear a dragon's call!||||SUMMON|",
            "13|strange rune|hellhole|^|Light Blue|1|20|0|0,0,0,0|TRAP+FLOOR+ONETIME+MAGICAL||A magical rune which calls demons to attack you.|Demons appear!||||SUMMON|",
            "14|strange rune|teleport rune|^|Orange|1|1|0|30,1,30,0|TRAP+FLOOR+MAGICAL||A magical rune which teleports you to a random location.|You hit a teleport trap!||||TELEPORT|",
            "15|discolored spot|fire trap|^|Umber|1|2|0|20,0,0,0|TRAP+FLOOR+MAGICAL||A strange coloured spot on the ground - beware its fiery attack.|You are enveloped in flames!||||SPOT|",
            "16|discolored spot|acid trap|^|Umber|1|2|0|0,0,0,0|TRAP+FLOOR+MAGICAL||A strange coloured spot on the ground - beware its acidic attack.|You are splashed with acid!||||SPOT|",
            "17|dart trap|slow dart|^|Red|1|2|0|0,0,0,50|TRAP+FLOOR+SAVE_ARMOR||A trap which shoots slowing darts.||A small dart barely misses you.|A small dart hits you!||DAMAGE,TIMED_INC_NO_RES|",
            "18|dart trap|strength loss dart|^|Red|2|6|0|30,0,0,0|TRAP+FLOOR+SAVE_ARMOR||A trap which shoots damaging darts.||A small dart barely misses you.|A small dart hits you!||DAMAGE,DRAIN_STAT|",
            "19|dart trap|dexterity loss dart|^|Red|2|6|0|30,0,0,0|TRAP+FLOOR+SAVE_ARMOR||A trap which shoots damaging darts.||A small dart barely misses you.|A small dart hits you!||DAMAGE,DRAIN_STAT|",
            "20|dart trap|constitution loss dart|^|Red|2|6|0|30,0,0,0|TRAP+FLOOR+SAVE_ARMOR||A trap which shoots damaging darts.||A small dart barely misses you.|A small dart hits you!||DAMAGE,DRAIN_STAT|",
            "21|gas trap|blinding gas trap|^|Green|1|2|0|20,1,5,0|TRAP+FLOOR||A trap that releases gas to blind you.|You are surrounded by a black gas!||||TIMED_INC|",
            "22|gas trap|confusion gas trap|^|Green|1|1|0|20,1,5,0|TRAP+FLOOR||A trap that releases gas to confuse you.|You are surrounded by a gas of scintillating colors!||||TIMED_INC|",
            "23|gas trap|poison gas trap|^|Green|1|2|0|20,1,5,0|TRAP+FLOOR||A trap that releases gas to poison you.|You are surrounded by a pungent green gas!||||TIMED_INC|",
            "24|gas trap|sleep gas trap|^|Green|1|2|0|20,1,5,0|TRAP+FLOOR||A trap that releases gas to put you to sleep.|You are surrounded by a strange white mist!||||TIMED_INC|",
            "25|alarm|aggravation trap|^|Light Red|1|5|0|0,0,0,50|TRAP+FLOOR+MAGICAL||A trap that wakes nearby monsters and impels them toward you.|A high-pitched whistle blares through the dungeon!||||WAKE,PROJECT_LOS|",
            "26|alarm|siren|^|Light Red|1|1|0|60,1,10,0|TRAP+FLOOR+MAGICAL||A trap that wakes nearby monsters.|A horn blast sounds!||||WAKE|",
            "27|explosion|mine trap|^|Mustard|1|15|0|10,1,5,0|TRAP+FLOOR+ONETIME||A trap that blows up the floor.|The floor explodes around you!||||SPOT|",
            "28|explosion|blast trap|^|Mustard|2|25|0|0,0,0,0|TRAP+FLOOR+ONETIME||A trap that creates an explosion.|The air seems to catch fire!||||SPOT,SPOT,SPOT,SPOT|",
            "29|mind blast trap|mind blasting trap|^|Light Green|1|20|0|30,1,10,0|TRAP+FLOOR+MAGICAL+SAVE_THROW||A trap that blasts your senses.|You feel pressure on your mind.|You shake your head and are back to normal.|Your mind is blasted by psionic energy!||DAMAGE,TIMED_INC|",
            "30|mind blast trap|brain smashing trap|^|Light Green|2|40|0|30,1,10,0|TRAP+FLOOR+MAGICAL+SAVE_THROW||A trap that blasts your senses.|You feel pressure on your mind.|You shake your head and are back to normal.|Your mind is blasted by psionic energy!||DAMAGE,TIMED_INC,TIMED_INC,TIMED_INC,TIMED_INC|",
            "31|ancient mechanism|rock fall trap|^|Light Umber|1|4|0|40,1,5,0|TRAP+FLOOR+ONETIME||A trap that drops a boulder on you.|A rock falls on your head!||||DAMAGE,TIMED_INC_NO_RES,RUBBLE|",
            "32|ancient mechanism|earthquake trap|^|Light Umber|1|30|0|30,1,5,0|TRAP+FLOOR||A trap that causes an earthquake.|The ground shakes.||||DAMAGE,EARTHQUAKE|",
            "33|ancient mechanism|block fall trap|^|Light Umber|1|4|0|40,1,5,0|TRAP+FLOOR+ONETIME+DELAY||A pressure plate that drops a stone block behind you.|A section of the ceiling falls in behind you!||||GRANITE|",
            "34|ancient mechanism|area blast trap|^|Light Umber|2|40|0|20,1,5,0|TRAP+FLOOR+ONETIME||A trap that clears the surrounding area.|The trap explodes, blasting the area clear!||||BALL,BALL,RUBBLE|",
            "35|trap|blinding flash trap|^|Light Yellow|1|5|0|30,1,5,0|TRAP+FLOOR+ONETIME+MAGICAL||A trap that creates a ball of light.|A blazing light descends on you!||||BALL|",
            "36|trap|blinding trap|^|Light Slate|1|5|0|20,1,5,0|TRAP+FLOOR+ONETIME+MAGICAL||A trap that envelops you in darkness.|Everything starts to fade...||||BALL,DRAIN_LIGHT|",
            "37|trap|mana drain trap|^|Light Purple|1|5|0|50,1,10,0|TRAP+FLOOR+MAGICAL||A trap that drains your mana.|You feel your energies sap away.||||DRAIN_MANA|",
            "38|trap|knife trap|^|Yellow|1|20|0|20,1,5,0|TRAP+FLOOR||A trap designed to bleed you to death.|Blades whirl around you, slicing your skin!||||TIMED_INC,DAMAGE|",
            "39|trap|petrifying trap|^|Blue|1|5|0|20,1,5,0|TRAP+FLOOR+MAGICAL||A trap that stuns you and gives you stoneskin.|You feel yourself turning to stone!||||TIMED_INC,TIMED_INC|",
    };

    // =====================================================================
    // (1) THE REAL FILE AGAINST THE UPSTREAM TABLE
    // =====================================================================

    @Test
    void everyShippedKindAgreesWithTheRowWorkedOutFromTheUpstreamFile() throws Exception {
        List<TrapKind> traps = loadReal();

        assertEquals(UPSTREAM_ROWS.length, traps.size());
        for (int i = 0; i < UPSTREAM_ROWS.length; i++) {
            assertEquals(UPSTREAM_ROWS[i], rowOf(traps.get(i)), "kind " + i);
        }
    }

    /**
     * The shipped file adds a {@code record-count:40} header and the blank line after it to the
     * upstream file; every other line is identical. The tests above rely on the header.
     */
    @Test
    void theShippedFileDiffersFromUpstreamOnlyByTheRecordCountHeader() throws IOException {
        List<String> lines = Files.readAllLines(Path.of(REAL_FILE));

        assertEquals(1, lines.stream().filter(l -> l.startsWith("record-count:")).count());
        assertTrue(lines.contains("record-count:40"));
    }

    // =====================================================================
    // (3) THE READER'S CONTRACT
    // =====================================================================

    @Test
    void parseReturnsTheSameTrapsAsParseWithResults() throws Exception {
        List<TrapKind> items = new TrapReader().parse(REAL_FILE);

        assertEquals(namesOf(loadReal()), namesOf(items));
    }

    /** C's {@code parse_file_quit_not_found} quits; the port throws so the loader can stop. */
    @Test
    void aMissingFileIsAnIOException() {
        assertThrows(IOException.class,
                () -> new TrapReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void aHardGrammarErrorReturnsNoTrapsAndTheMessages() throws IOException {
        ParseResult<TrapKind> r = load("hard.txt", withHeader(2,
                "name:a:good\ngraphics:^:w\nname:b:bad\nnonsense:1\n"));

        assertHardError(r);
    }

    @Test
    void aMissingRecordCountHeaderIsAHardError() throws IOException {
        assertHardError(load("no-header.txt", "name:a:b\ngraphics:^:w\n"));
    }

    @Test
    void aRecordCountMismatchIsASoftErrorAndTheTrapsStillLoad() throws IOException {
        ParseResult<TrapKind> r = load("count.txt", withHeader(5, "name:a:b\ngraphics:^:w\n"));

        assertTrue(r.hasErrors());
        assertEquals(1, r.items().size());
    }

    /** C's list is newest first, but {@code finish_parse_trap} fills {@code trap_info} from the back. */
    @Test
    void trapsComeBackInFileOrderForASyntheticFile() throws IOException {
        ParseResult<TrapKind> r = load("order.txt", withHeader(3,
                "name:z:third\ngraphics:^:w\nname:a:first\ngraphics:^:w\nname:m:second\ngraphics:^:w\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals("third,first,second", namesOf(r.items()));
        assertEquals(0, r.items().get(0).getTrapKindIndex());
        assertEquals(2, r.items().get(2).getTrapKindIndex());
    }

    /**
     * C stops the load on a bad kind. The port drops it and loads the rest, so every kind after it
     * takes a lower {@code tidx} than the file order suggests.
     */
    @Test
    void aDroppedTrapMovesTheLaterTrapsDownOneIndex() throws IOException {
        ParseResult<TrapKind> r = load("dropped.txt", withHeader(3,
                "name:a:first\ngraphics:^:w\n"
                        + "name:b:second\ngraphics:^:w\nflags:NOTAFLAG\n"
                        + "name:c:third\ngraphics:^:w\n"));

        assertTrue(hasError(r, "NOTAFLAG"), r.errors()::toString);
        assertEquals("first,third", namesOf(r.items()));
        assertEquals(1, r.items().get(1).getTrapKindIndex());
    }

    // =====================================================================
    // (4) WHERE THE GRAMMAR AND ASSEMBLER MATCH init.c
    // =====================================================================

    /** C: {@code string_append} on {@code text}, {@code msg}, {@code msg_good}, {@code msg_bad}, {@code msg_xtra}. */
    @Test
    void repeatedTextAndMessageLinesJoinAsInC() throws Exception {
        ParseResult<TrapKind> r = load("join.txt", withHeader(1,
                "name:a:b\ndesc:D1\ndesc: D2\nmsg:A\nmsg:B\nmsg-good:G\nmsg-good:H\n"
                        + "msg-bad:X\nmsg-bad:Y\nmsg-xtra:P\nmsg-xtra:Q\n"));

        TrapKind t = r.items().getFirst();
        assertEquals("D1 D2", t.getText());
        assertEquals("AB", field(t, "message"));
        assertEquals("GH", field(t, "messageOnSave"));
        assertEquals("XY", field(t, "messageOnFailure"));
        assertEquals("PQ", field(t, "messageOnExtraEffect"));
    }

    /** C: {@code graphics}, {@code appear} and {@code visibility} assign, so the last line wins. */
    @Test
    void aRepeatedGraphicsAppearOrVisibilityLineTakesTheLastValueAsInC() throws Exception {
        ParseResult<TrapKind> r = load("last.txt", withHeader(1,
                "name:a:b\ngraphics:^:w\ngraphics:%:r\nappear:1:2:3\nappear:4:5:6\n"
                        + "visibility:10\nvisibility:20\n"));

        TrapKind t = r.items().getFirst();
        AngbandDisplayCharacter adc = field(t, "angbandDisplayCharacter");
        assertEquals('%', adc.getCharacter());
        assertEquals("Red", ColourEnum.attributeToString(adc.getAttributeColour()));
        assertEquals(4, (int) field(t, "rarity"));
        assertEquals(5, (int) field(t, "minDepth"));
        assertEquals(6, (int) field(t, "maxNum"));
        Random power = field(t, "power");
        assertEquals(20, power.getBase());
    }

    /** C: {@code grab_flag} turns bits on and never off, so repeated lines accumulate. */
    @Test
    void repeatedFlagAndSaveLinesAccumulateAsInC() throws IOException {
        ParseResult<TrapKind> r = load("accumulate.txt", withHeader(1,
                "name:a:b\nflags:TRAP\nflags:FLOOR | PIT\nsave:FEATHER\nsave:FREE_ACT\n"));

        TrapKind t = r.items().getFirst();
        assertTrue(t.getFlags().has(TrapEnum.TRF_TRAP));
        assertTrue(t.getFlags().has(TrapEnum.TRF_FLOOR));
        assertTrue(t.getFlags().has(TrapEnum.TRF_PIT));
        assertTrue(t.getSaveFlags().has(ObjectFlag.OF_FEATHER));
        assertTrue(t.getSaveFlags().has(ObjectFlag.OF_FREE_ACT));
    }

    /** C: every line after {@code name:} is its own handler, with no required order. */
    @Test
    void directivesAfterNameMayComeInAnyOrderAsInC() throws Exception {
        ParseResult<TrapKind> r = load("any-order.txt", withHeader(1,
                "name:a:b\ndesc:T\nflags:TRAP\ngraphics:^:w\nmsg:M\nappear:1:2:3\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        TrapKind t = r.items().getFirst();
        assertEquals("T", t.getText());
        assertEquals("M", field(t, "message"));
        assertEquals(1, (int) field(t, "rarity"));
    }

    /** C: {@code strtok(flags, " |")} splits on a bare bar as well as on {@code " | "}. */
    @Test
    void flagsSeparatedByABareBarSplitAsInC() throws IOException {
        ParseResult<TrapKind> r = load("bare-bar.txt", withHeader(1, "name:a:b\nflags:TRAP|FLOOR\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertTrue(r.items().getFirst().getFlags().has(TrapEnum.TRF_FLOOR));
    }

    /** C: {@code NONE} is the first name in {@code trap_flags}, so {@code flags:NONE} is accepted. */
    @Test
    void flagNoneLoadsAsInC() throws IOException {
        ParseResult<TrapKind> r = load("flag-none.txt", withHeader(1, "name:a:b\nflags:NONE\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(1, r.items().size());
    }

    /** C: a record with no {@code appear:} line is zeroed. */
    @Test
    void aMissingAppearLineLeavesZeroesAsInC() throws Exception {
        TrapKind t = load("no-appear.txt", withHeader(1, "name:a:b\ngraphics:^:w\n")).items().getFirst();

        assertEquals(0, (int) field(t, "rarity"));
        assertEquals(0, (int) field(t, "minDepth"));
        assertEquals(0, (int) field(t, "maxNum"));
    }

    /** C: {@code color_text_to_attr} compares with {@code my_stricmp}; a single character is case-sensitive. */
    @Test
    void colourNamesIgnoreCaseButColourCharactersDoNotAsInC() throws Exception {
        TrapKind lower = load("c-lower.txt", withHeader(1, "name:a:b\ngraphics:^:light yellow\n")).items().getFirst();
        TrapKind mixed = load("c-mixed.txt", withHeader(1, "name:a:b\ngraphics:^:Light Yellow\n")).items().getFirst();
        TrapKind upperChar = load("c-char.txt", withHeader(1, "name:a:b\ngraphics:^:Y\n")).items().getFirst();
        TrapKind lowerChar = load("c-char2.txt", withHeader(1, "name:a:b\ngraphics:^:y\n")).items().getFirst();

        assertEquals("Light Yellow", colourOf(lower));
        assertEquals("Light Yellow", colourOf(mixed));
        assertEquals("Light Yellow", colourOf(upperChar));
        assertEquals("Yellow", colourOf(lowerChar));
    }

    private static String colourOf(TrapKind t) throws Exception {
        AngbandDisplayCharacter adc = field(t, "angbandDisplayCharacter");
        return ColourEnum.attributeToString(adc.getAttributeColour());
    }

    // =====================================================================
    // (5) WHERE THE GRAMMAR OR ASSEMBLER DIFFERS FROM init.c
    // =====================================================================

    /** C: a {@code name:} line alone allocates a zeroed kind, and nothing else is required. */
    @Test
    void aNameOnlyRecordIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError(load("name-only.txt", withHeader(1, "name:a:b\n")));
    }

    /** C: a file with no {@code name:} line leaves the list empty and loads without error. */
    @Test
    void aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        assertHardError(load("no-records.txt", withHeader(0, "")));
    }

    /** C: the second field of {@code name:} is a {@code str}, which takes the rest of the line. */
    @Test
    void aColonInTheNameDescriptionIsAHardErrorHereWhereCTakesTheRestOfTheLine() throws IOException {
        assertHardError(load("name-colon.txt", withHeader(1, "name:a:b: c\ngraphics:^:w\n")));
    }

    /** C: a colon in a {@code msg:} line is part of the text, and the port agrees. */
    @Test
    void aColonInAMessageLoadsAsInC() throws Exception {
        TrapKind t = load("msg-colon.txt", withHeader(1, "name:a:b\nmsg:Look: a trap\n")).items().getFirst();

        assertEquals("Look: a trap", field(t, "message"));
    }

    @Test
    void flagsSeparatedByASpaceAloneAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        assertHardError(load("space-flags.txt", withHeader(1, "name:a:b\nflags:TRAP FLOOR\n")));
    }

    @Test
    void saveFlagsSeparatedByASpaceAloneAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        assertHardError(load("space-save.txt", withHeader(1, "name:a:b\nsave:FEATHER FREE_ACT\n")));
    }

    /** C: {@code flags} is {@code ?str}, so the value may be missing. */
    @Test
    void anEmptyFlagsLineIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError(load("empty-flags.txt", withHeader(1, "name:a:b\nflags:\n")));
    }

    /** C: {@code parse_trap_effect_yx_xtra} sets the position of the last extra effect. */
    @Test
    void effectYxXtraIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError(load("yx-xtra.txt", withHeader(1,
                "name:a:b\neffect-xtra:DAMAGE\neffect-yx-xtra:1:2\n")));
    }

    /** C: with no effect yet, {@code parse_trap_dice} returns {@code PARSE_ERROR_NONE} and does nothing. */
    @Test
    void aDiceLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError(load("dice-first.txt", withHeader(1, "name:a:b\ndice:1d4\n")));
    }

    @Test
    void aDiceXtraLineBeforeAnyExtraEffectIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError(load("dice-xtra-first.txt", withHeader(1, "name:a:b\ndice-xtra:1d4\n")));
    }

    /** C: {@code parse_trap_expr} returns {@code PARSE_ERROR_NONE} when the last effect has no dice. */
    @Test
    void anExprLineWithNoDiceBeforeItIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError(load("expr-no-dice.txt", withHeader(1,
                "name:a:b\neffect:DAMAGE\nexpr:S:DUNGEON_LEVEL:/ 2\n")));
    }

    /** C: {@code parse_trap_dice} walks the primary list, so it still reaches {@code effect:} after an extra effect. */
    @Test
    void aDiceLineAfterAnExtraEffectIsAHardErrorHereWhereCReachesThePrimaryEffect() throws IOException {
        assertHardError(load("dice-after-xtra.txt", withHeader(1,
                "name:a:b\neffect:DAMAGE\neffect-xtra:DAMAGE\ndice:1d4\n")));
    }

    /** C: {@code effect-yx} and {@code dice} both act on the last effect, in either order. */
    @Test
    void effectYxAndDiceOnTheSameEffectAreAHardErrorHereWhereCTakesBoth() throws IOException {
        assertHardError(load("yx-and-dice.txt", withHeader(1,
                "name:a:b\neffect:DAMAGE\neffect-yx:1:2\ndice:1d4\n")));
    }

    /** C: {@code init_parse_trap} registers no {@code effect-msg}, so it is an undefined directive. */
    @Test
    void effectMsgIsAcceptedHereWhereCHasNoSuchDirective() throws IOException {
        ParseResult<TrapKind> r = load("effect-msg.txt", withHeader(1,
                "name:a:b\neffect:DAMAGE\ndice:1d4\neffect-msg:hi\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(1, r.items().getFirst().getEffect().size());
    }

    /** C: {@code appear} is three {@code uint}s, and {@code parser_parse} rejects a leading minus. */
    @Test
    void aNegativeAppearValueLoadsHereWhereCRejectsIt() throws Exception {
        TrapKind t = load("neg-appear.txt", withHeader(1, "name:a:b\nappear:-1:-2:-3\n")).items().getFirst();

        assertEquals(-1, (int) field(t, "rarity"));
        assertEquals(-2, (int) field(t, "minDepth"));
        assertEquals(-3, (int) field(t, "maxNum"));
    }

    /** C: {@code dice_parse_string} skips whitespace, so {@code 2d10 M50} is {@code 2d10M50}. */
    @Test
    void aSpaceInsideTheVisibilityDiceIsAHardErrorHereWhereCSkipsIt() throws IOException {
        assertHardError(load("vis-space.txt", withHeader(1, "name:a:b\nvisibility:2d10 M50\n")));
    }

    /** C: {@code color_text_to_attr} answers white for a name it does not know. */
    @Test
    void anUnknownColourNameIsAHardErrorHereWhereCDrawsItWhite() throws IOException {
        assertHardError(load("colour-name.txt", withHeader(1, "name:a:b\ngraphics:^:nonsense\n")));
    }

    /** C: {@code parser_parse} skips leading whitespace on a line. */
    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        assertHardError(load("indented.txt", withHeader(1, "name:a:b\n  graphics:^:w\n")));
    }

    /** C: {@code list_obj_flag_names} and {@code trap_flags} hold no {@code MAX}. */
    @Test
    void flagMaxLoadsHereWhereCRejectsIt() throws IOException {
        ParseResult<TrapKind> r = load("flag-max.txt", withHeader(1, "name:a:b\nflags:MAX\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(1, r.items().size());
    }

    @Test
    void saveMaxLoadsHereWhereCRejectsIt() throws IOException {
        ParseResult<TrapKind> r = load("save-max.txt", withHeader(1, "name:a:b\nsave:MAX\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(1, r.items().size());
    }

    /** C: {@code mem_zalloc} leaves {@code d_char} and {@code d_attr} at 0. */
    @Test
    void aKindWithNoGraphicsLineHasNoDisplayCharacterHereWhereCHasZeroes() throws Exception {
        TrapKind t = load("no-graphics.txt", withHeader(1, "name:a:b\ndesc:x\n")).items().getFirst();

        assertNull(field(t, "angbandDisplayCharacter"));
    }

    /** C: {@code mem_zalloc} leaves {@code power} as a zero {@code random_value}, which {@code randcalc} accepts. */
    @Test
    void aKindWithNoVisibilityLineHasNullPowerHereWhereCHasAZeroRandom() throws Exception {
        TrapKind t = load("no-vis.txt", withHeader(1, "name:a:b\ngraphics:^:w\n")).items().getFirst();

        assertNull(field(t, "power"));
    }

    /** The shipped file: five of the forty kinds, the ones with no {@code visibility:} line. */
    @Test
    void fiveShippedKindsHaveNullPowerHereWhereCHasAZeroRandom() throws Exception {
        List<String> noPower = new java.util.ArrayList<>();
        for (TrapKind t : loadReal()) {
            if (field(t, "power") == null) noPower.add(t.getDescription());
        }

        assertEquals(List.of("no trap", "glyph of warding", "decoy", "door lock", "web"), noPower);
    }

    /** C: an absent {@code msg:} is {@code NULL}, and {@code trap.c} tests the pointer; here it is empty text. */
    @Test
    void anAbsentMessageIsEmptyTextHereWhereCHasNull() throws Exception {
        TrapKind t = load("no-msg.txt", withHeader(1, "name:a:b\ngraphics:^:w\n")).items().getFirst();

        assertEquals("", field(t, "message"));
        assertEquals("", field(t, "messageOnSave"));
        assertEquals("", field(t, "messageOnFailure"));
        assertEquals("", field(t, "messageOnExtraEffect"));
        assertEquals("", t.getText());
    }

    /**
     * C: a bad effect stops the load. {@code EffectAssembler.assemble} answers {@code null} for a
     * block with a bad effect, its contract says the caller drops the record, and
     * {@code TrapAssembler} builds the kind anyway, so {@code getEffect()} throws.
     */
    @Test
    void aBadEffectKeepsTheTrapWithNullEffectsHereWhereCQuits() throws Exception {
        ParseResult<TrapKind> r = load("bad-effect.txt", withHeader(1,
                "name:a:b\ngraphics:^:w\neffect:NOTANEFFECT\ndice:1\n"));

        assertTrue(hasError(r, "NOTANEFFECT"), r.errors()::toString);
        assertEquals(1, r.items().size());
        assertNull(field(r.items().getFirst(), "effect"));
        assertThrows(NullPointerException.class, () -> r.items().getFirst().getEffect());
    }

    @Test
    void aBadExtraEffectKeepsTheTrapWithNullExtraEffectsHereWhereCQuits() throws Exception {
        ParseResult<TrapKind> r = load("bad-xtra.txt", withHeader(1,
                "name:a:b\ngraphics:^:w\neffect-xtra:NOTANEFFECT\ndice-xtra:1\n"));

        assertTrue(hasError(r, "NOTANEFFECT"), r.errors()::toString);
        assertEquals(1, r.items().size());
        assertNull(field(r.items().getFirst(), "effectXtra"));
    }
}
