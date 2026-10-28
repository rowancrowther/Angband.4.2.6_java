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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.jackoftradesltd.backend.parser.PainReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonsterPain} against {@code struct monster_pain} in {@code monster.h} and the twelve
 * {@code type:} blocks of 4.2.6's own {@code lib/gamedata/pain.txt}, which C's {@code parse_pain_type}
 * and {@code parse_pain_message} in {@code mon-init.c} turn into it.
 *
 * <p>The expected rows in {@link #C_ROWS} were read off the {@code message:} lines of the C
 * distribution's data file, not taken from the Java loader's output. The index order of the seven
 * messages follows {@code get_message_text} in {@code mon-msg.c}, which reads {@code messages[0]}
 * for {@code MON_MSG_95} (more than 95% of the health left) down to {@code messages[6]} for
 * {@code MON_MSG_0} (10% or less), so a message that lands one slot out would change what the
 * player reads at the wrong health.
 *
 * @author Rowan Crowther
 */
class MonsterPainTest {

    private static final String PAIN_FILE = "lib/gamedata/pain.txt";

    /**
     * The twelve records of C's {@code pain.txt}, in file order. Row {@code i} has pain index
     * {@code i + 1}, because the serial numbers run from 1.
     */
    private static final String[][] C_ROWS = {
            {"shrug[s] off the attack.", "grunt[s] with pain.", "cr[ies|y] out in pain.",
                    "scream[s] in pain.", "scream[s] in agony.", "writhe[s] in agony.",
                    "cr[ies|y] out feebly."},
            {"barely notice[s].", "flinch[es].", "squelch[es].", "quiver[s] in pain.",
                    "writhe[s] about.", "writhe[s] in agony.", "jerk[s] limply."},
            {"shrug[s] off the attack.", "snarl[s] with pain.", "yelp[s] in pain.",
                    "howl[s] in pain.", "howl[s] in agony.", "writhe[s] in agony.",
                    "yelp[s] feebly."},
            {"ignore[s] the attack.", "grunt[s] with pain.", "squeal[s] in pain.",
                    "shriek[s] in pain.", "shriek[s] in agony.", "writhe[s] in agony.",
                    "cr[ies|y] out feebly."},
            {"barely notice[s].", "hiss[es].", "rear[s] up in anger.", "hiss[es] furiously.",
                    "writhe[s] about.", "writhe[s] in agony.", "jerk[s] limply."},
            {"shrug[s] off the attack.", "snarl[s].", "growl[s] angrily.", "hiss[es] in pain.",
                    "mewl[s] in pain.", "hiss[es] in agony.", "mewl[s] pitifully."},
            {"ignore[s] the attack.", "drone[s] angrily.", "scuttle[s] about.",
                    "twitch[es] in pain.", "jerk[s] in pain.", "jerk[s] in agony.",
                    "jerk[s] feebly."},
            {"shrug[s] off the attack.", "flap[s] angrily.", "jeer[s] in pain.",
                    "squawk[s] with pain.", "twitter[s] in agony.", "flutter[s] about.",
                    "chirp[s] feebly."},
            {"ignore[s] the attack.", "jerk[s].", "rattle[s].", "clatter[s].", "shake[s].",
                    "stagger[s].", "crumple[s]."},
            {"ignore[s] the attack.", "grunt[s].", "jerk[s].", "moan[s].", "groan[s].",
                    "hesitate[s].", "stagger[s]."},
            {"ignore[s] the attack.", "spin[s] fiercely.", "swirl[s] about.", "twist[s] around.",
                    "spin[s] slowly.", "swirl[s] weakly.", "twist[s] limply."},
            {"laugh[s] off the attack.", "sneer[s].", "scowl[s].", "bellow[s] in rage.",
                    "scream[s] in fury.", "grunt[s].", "wince[s]."},
    };

    private static List<String> seven() {
        return new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g"));
    }

    /**
     * Every message of every C record is returned at the level C reads it at, and the index is the
     * one the record was built with.
     */
    @Test
    void everyCRowReadsBackAtItsCLevel() {
        List<Executable> checks = new ArrayList<>();
        for (int row = 0; row < C_ROWS.length; row++) {
            final int index = row + 1;
            final String[] expected = C_ROWS[row];
            MonsterPain pain = new MonsterPain(index, Arrays.asList(expected));
            checks.add(() -> assertEquals(index, pain.getPainIndex()));
            for (int level = 0; level < 7; level++) {
                final int lvl = level;
                checks.add(() -> assertEquals(expected[lvl], pain.getMessage(lvl),
                        "type " + index + " level " + lvl));
            }
        }
        assertAll(checks);
    }

    /**
     * The records the Java loader builds from the data file are C's twelve records, in C's order,
     * with every message at the level C holds it at.
     */
    @Test
    void loaderOutputMatchesCRows() throws Exception {
        List<MonsterPain> loaded = new PainReader().parseWithResults(PAIN_FILE).items();

        assertEquals(C_ROWS.length, loaded.size());
        List<Executable> checks = new ArrayList<>();
        for (int row = 0; row < C_ROWS.length; row++) {
            final MonsterPain pain = loaded.get(row);
            final int index = row + 1;
            final String[] expected = C_ROWS[row];
            checks.add(() -> assertEquals(index, pain.getPainIndex()));
            for (int level = 0; level < 7; level++) {
                final int lvl = level;
                checks.add(() -> assertEquals(expected[lvl], pain.getMessage(lvl),
                        "type " + index + " level " + lvl));
            }
        }
        assertAll(checks);
    }

    /**
     * The mildest message is level 0 and the worst is level 6, the order {@code get_message_text}
     * maps {@code MON_MSG_95} and {@code MON_MSG_0} onto. Type 1 is the first record in the file.
     */
    @Test
    void levelZeroIsMildestAndSixIsWorst() {
        MonsterPain pain = new MonsterPain(1, Arrays.asList(C_ROWS[0]));

        assertEquals("shrug[s] off the attack.", pain.getMessage(0));
        assertEquals("cr[ies|y] out feebly.", pain.getMessage(6));
    }

    /**
     * The bracket markers are stored as written; resolving the singular or plural form is done
     * later, when C's {@code get_message_text} builds the line.
     */
    @Test
    void pluralMarkersAreKeptUnresolved() {
        MonsterPain pain = new MonsterPain(1, Arrays.asList(C_ROWS[0]));

        assertEquals("cr[ies|y] out in pain.", pain.getMessage(2));
    }

    /**
     * Levels 0 and 6 are the ends of C's array; anything either side has no C counterpart and is
     * refused.
     */
    @Test
    void levelOutsideZeroToSixIsRefused() {
        MonsterPain pain = new MonsterPain(1, seven());

        assertAll(
                () -> assertEquals("a", pain.getMessage(0)),
                () -> assertEquals("g", pain.getMessage(6)),
                () -> assertThrows(IllegalArgumentException.class, () -> pain.getMessage(-1)),
                () -> assertThrows(IllegalArgumentException.class, () -> pain.getMessage(7)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> pain.getMessage(Integer.MIN_VALUE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> pain.getMessage(Integer.MAX_VALUE)));
    }

    /**
     * C refuses an eighth message with {@code PARSE_ERROR_TOO_MANY_ENTRIES}; a list of eight is
     * refused here too.
     */
    @Test
    void eightMessagesAreRefused() {
        List<String> eight = seven();
        eight.add("h");

        assertThrows(IllegalStateException.class, () -> new MonsterPain(1, eight));
    }

    /**
     * Decided 261028 (option A): the port copies C here. {@code parse_pain_message} only refuses an
     * eighth message, so a record with six, one or none is built and the constructor does not
     * refuse it. The messages it was given are readable as given.
     */
    @Test
    void fewerThanSevenMessagesAreAccepted() {
        MonsterPain six = new MonsterPain(1, seven().subList(0, 6));
        MonsterPain one = new MonsterPain(2, List.of("a"));
        MonsterPain none = new MonsterPain(3, List.of());

        assertAll(
                () -> assertEquals(1, six.getPainIndex()),
                () -> assertEquals("f", six.getMessage(5)),
                () -> assertEquals(2, one.getPainIndex()),
                () -> assertEquals("a", one.getMessage(0)),
                () -> assertEquals(3, none.getPainIndex()));
    }

    /**
     * C leaves the slots a short entry does not fill {@code NULL}. The constructor pads a short
     * list with {@code null} up to seven itself, so a slot past the end of the list reads back as
     * {@code null} whether or not the caller (as {@code PainAssembler} does) padded it first, and
     * {@code getMessage} never throws for a level inside 0 to 6.
     */
    @Test
    void emptySlotsReadBackAsNullWhetherOrNotTheCallerPadsThem() {
        List<String> padded = new ArrayList<>(seven().subList(0, 3));
        while (padded.size() < 7) {
            padded.add(null);
        }
        MonsterPain pain = new MonsterPain(1, padded);
        MonsterPain unpadded = new MonsterPain(2, seven().subList(0, 3));

        assertAll(
                () -> assertEquals("c", pain.getMessage(2)),
                () -> assertNull(pain.getMessage(3)),
                () -> assertNull(pain.getMessage(6)),
                () -> assertEquals("c", unpadded.getMessage(2)),
                () -> assertNull(unpadded.getMessage(3)),
                () -> assertNull(unpadded.getMessage(6)));
    }

    /**
     * An empty list is the shortest C accepts (a {@code type:} line with no {@code message:}
     * lines): all seven slots are {@code NULL}, and the level bounds still apply.
     */
    @Test
    void anEmptyListLeavesAllSevenSlotsNull() {
        MonsterPain pain = new MonsterPain(5, List.of());

        List<Executable> checks = new ArrayList<>();
        for (int level = 0; level < 7; level++) {
            final int lvl = level;
            checks.add(() -> assertNull(pain.getMessage(lvl), "level " + lvl));
        }
        checks.add(() -> assertThrows(IllegalArgumentException.class, () -> pain.getMessage(7)));
        assertAll(checks);
    }

    /**
     * Exactly seven is the boundary between a full record and one the constructor pads; the
     * seventh message sits at level 6 and nothing is added after it.
     */
    @Test
    void exactlySevenMessagesAreNotPadded() {
        MonsterPain pain = new MonsterPain(1, seven());

        assertAll(
                () -> assertEquals("g", pain.getMessage(6)),
                () -> assertFalse(pain.toString().contains("null"), pain.toString()));
    }

    /**
     * The failure for an eighth message names the pain index, so a bad {@code type:} block can be
     * found.
     */
    @Test
    void refusalOfAnEighthNamesThePainIndex() {
        List<String> eight = seven();
        eight.add("h");

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new MonsterPain(42, eight));

        assertTrue(e.getMessage().contains("42"), e.getMessage());
    }

    /**
     * C copies the string with {@code string_make}, so a later change to the caller's list cannot
     * reach the record; the constructor copies the list for the same reason.
     */
    @Test
    void laterChangesToTheCallersListDoNotReachTheRecord() {
        List<String> source = seven();
        MonsterPain pain = new MonsterPain(1, source);

        source.set(0, "changed");
        source.add("extra");

        assertAll(
                () -> assertEquals("a", pain.getMessage(0)),
                () -> assertEquals("g", pain.getMessage(6)));
    }

    /**
     * A {@code null} in the middle of a full list is kept, as C keeps a {@code NULL} slot; nothing
     * in the constructor looks at the contents of the list.
     */
    @Test
    void aNullMessageIsKept() {
        List<String> withNull = seven();
        withNull.set(3, null);

        MonsterPain pain = new MonsterPain(1, withNull);

        assertAll(
                () -> assertEquals("c", pain.getMessage(2)),
                () -> assertNull(pain.getMessage(3)),
                () -> assertEquals("e", pain.getMessage(4)));
    }

    /**
     * The index is kept as given, including values C's blank slot 0 or a repeated serial would
     * use; checking that it is unique or in order is not this class's job, as in C.
     */
    @Test
    void painIndexIsKeptAsGiven() {
        assertAll(
                () -> assertEquals(0, new MonsterPain(0, seven()).getPainIndex()),
                () -> assertEquals(12, new MonsterPain(12, seven()).getPainIndex()),
                () -> assertEquals(1000, new MonsterPain(1000, seven()).getPainIndex()));
    }

    /**
     * The debug string carries the index and every message.
     */
    @Test
    void toStringNamesTheIndexAndEveryMessage() {
        String text = new MonsterPain(7, Arrays.asList(C_ROWS[6])).toString();

        List<Executable> checks = new ArrayList<>();
        checks.add(() -> assertTrue(text.startsWith("MonsterPain{painIndex=7"), text));
        for (String message : C_ROWS[6]) {
            checks.add(() -> assertTrue(text.contains("'" + message + "'"), text));
        }
        assertAll(checks);
    }

    /**
     * A short record prints seven entries: the filled ones quoted, the unfilled slots as
     * {@code 'null'}.
     */
    @Test
    void toStringShowsUnfilledSlotsAsNull() {
        String text = new MonsterPain(3, List.of("a", "b")).toString();

        assertEquals("MonsterPain{painIndex=3, messages='a', 'b', 'null', 'null', 'null', 'null', "
                + "'null', }", text);
    }
}
