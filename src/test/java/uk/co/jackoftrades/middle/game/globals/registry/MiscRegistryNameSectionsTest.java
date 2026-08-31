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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.game.Name;
import uk.co.jackoftrades.middle.player.enums.RandnameType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code MiscRegistry.setNames} and {@code MiscRegistry.getNameSection} — together the port
 * of C's {@code finish_parse_names} ({@code src/init.c:1466}) and of the
 * {@code name_sections[name_type]} lookup that {@code randname_make} ({@code src/randname.c:77})
 * performs on the result.
 *
 * <p><b>The expectations come from the C, not from the port.</b> What C's
 * {@code finish_parse_names} guarantees is: one word list per section number, holding every
 * {@code word:} line filed under that section by {@code parse_names_word}, and nothing else. The
 * section number is the {@code randname_type} value itself, so 1 is {@code RANDNAME_TOLKIEN} and
 * 2 is {@code RANDNAME_SCROLL} ({@code randname.h:26}).
 *
 * <p><b>Two known divergences are pinned here rather than left to be rediscovered.</b> First,
 * order: C prepends each word to a linked list and then walks that list, so its sections come out
 * in reverse file order, while the port appends. That is invisible to the only consumer,
 * {@code build_prob}, which counts letter transitions; the tests therefore assert membership and
 * count as the C-derived contract, and pin the port's file order separately as a decision.
 * Second, section zero: {@code parse_names_section} ({@code src/init.c:1430}) rejects only
 * sections at or above {@code RANDNAME_NUM_TYPES}, so it accepts zero and files those words in
 * the {@code name_sections[0]} slot that no caller ever reads; the port rejects zero outright.
 * The shipped {@code names.txt} opens with {@code section:1} and uses only sections 1 and 2, so
 * no real data file can tell the two apart.
 *
 * @author Rowan Crowther
 */
@DisplayName("MiscRegistry — random name sections")
class MiscRegistryNameSectionsTest {

    /**
     * A section number as the data file and {@code randname.h} spell it: 1 for Tolkien.
     */
    private static final int TOLKIEN_SECTION = 1;
    /**
     * A section number as the data file and {@code randname.h} spell it: 2 for scroll titles.
     */
    private static final int SCROLL_SECTION = 2;

    private static Name name(int section, String... words) {
        return new Name(section, new ArrayList<>(List.of(words)));
    }

    @Nested
    @DisplayName("setNames — filing words under their section")
    class Filing {

        @Test
        @DisplayName("each section receives exactly the words filed under it")
        void sectionsAreSeparate() {
            MiscRegistry.setNames(List.of(
                    name(TOLKIEN_SECTION, "beleg", "turin"),
                    name(SCROLL_SECTION, "aar", "ulk")));

            assertEquals(List.of("beleg", "turin"),
                    MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
            assertEquals(List.of("aar", "ulk"),
                    MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
        }

        @Test
        @DisplayName("several records for one section accumulate, as C's repeated section: blocks do")
        void recordsForOneSectionAccumulate() {
            MiscRegistry.setNames(List.of(
                    name(TOLKIEN_SECTION, "beleg"),
                    name(SCROLL_SECTION, "aar"),
                    name(TOLKIEN_SECTION, "turin", "huor")));

            List<String> tolkien = MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN);
            assertEquals(3, tolkien.size());
            assertTrue(tolkien.containsAll(List.of("beleg", "turin", "huor")));
            assertEquals(List.of("aar"), MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
        }

        @Test
        @DisplayName("duplicate words are kept, C counting each occurrence separately")
        void duplicatesAreKept() {
            MiscRegistry.setNames(List.of(name(TOLKIEN_SECTION, "beleg", "beleg")));

            assertEquals(List.of("beleg", "beleg"),
                    MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        }

        @Test
        @DisplayName("a section with no words gives an empty list, not a null one")
        void unmentionedSectionIsEmpty() {
            MiscRegistry.setNames(List.of(name(TOLKIEN_SECTION, "beleg")));

            assertEquals(List.of(), MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
        }

        @Test
        @DisplayName("no records at all leaves every section empty")
        void noRecordsLeavesEverySectionEmpty() {
            MiscRegistry.setNames(List.of());

            for (RandnameType type : RandnameType.values()) {
                assertEquals(List.of(), MiscRegistry.getNameSection(type), type.name());
            }
        }

        @Test
        @DisplayName("the records themselves are stored, and getNames returns them")
        void recordsAreStored() {
            Name tolkien = name(TOLKIEN_SECTION, "beleg");
            MiscRegistry.setNames(List.of(tolkien));

            assertEquals(List.of(tolkien), MiscRegistry.getNames());
        }

        @Test
        @DisplayName("a second load replaces the word lists rather than adding to them")
        void reloadReplaces() {
            MiscRegistry.setNames(List.of(name(TOLKIEN_SECTION, "beleg", "turin")));
            MiscRegistry.setNames(List.of(name(TOLKIEN_SECTION, "huor")));

            assertEquals(List.of("huor"),
                    MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        }

        @Test
        @DisplayName("a section emptied by a second load does not keep its old words")
        void reloadEmptiesSectionsNoLongerMentioned() {
            MiscRegistry.setNames(List.of(name(SCROLL_SECTION, "aar", "ulk")));
            MiscRegistry.setNames(List.of(name(TOLKIEN_SECTION, "beleg")));

            assertEquals(List.of(), MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
        }

        @Test
        @DisplayName("words are appended in file order, where C's linked list reverses them")
        void wordsKeepFileOrder() {
            MiscRegistry.setNames(List.of(name(TOLKIEN_SECTION, "first", "second", "third")));

            assertEquals(List.of("first", "second", "third"),
                    MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        }
    }

    @Nested
    @DisplayName("setNames — section numbers out of range")
    class OutOfRange {

        @Test
        @DisplayName("RANDNAME_NUM_TYPES is C's PARSE_ERROR_OUT_OF_BOUNDS boundary and is rejected")
        void markerSectionIsRejected() {
            List<Name> names = List.of(name(RandnameType.values().length, "beleg"));

            assertThrows(IllegalArgumentException.class, () -> MiscRegistry.setNames(names));
        }

        @Test
        @DisplayName("a section beyond the marker is rejected")
        void sectionAboveMarkerIsRejected() {
            List<Name> names = List.of(name(RandnameType.values().length + 1, "beleg"));

            assertThrows(IllegalArgumentException.class, () -> MiscRegistry.setNames(names));
        }

        @Test
        @DisplayName("section zero is rejected — stricter than C, which files it in an unread slot")
        void sectionZeroIsRejected() {
            List<Name> names = List.of(name(0, "beleg"));

            assertThrows(IllegalArgumentException.class, () -> MiscRegistry.setNames(names));
        }

        @Test
        @DisplayName("a negative section is rejected")
        void negativeSectionIsRejected() {
            List<Name> names = List.of(name(-1, "beleg"));

            assertThrows(IllegalArgumentException.class, () -> MiscRegistry.setNames(names));
        }

        @Test
        @DisplayName("a bad section is caught even when good records precede it")
        void badSectionAmongGoodOnesIsRejected() {
            List<Name> names = List.of(name(TOLKIEN_SECTION, "beleg"), name(0, "turin"));

            assertThrows(IllegalArgumentException.class, () -> MiscRegistry.setNames(names));
        }
    }

    @Nested
    @DisplayName("getNameSection")
    class Reading {

        @Test
        @DisplayName("the end-of-type marker has no words of its own")
        void markerSectionIsEmpty() {
            MiscRegistry.setNames(List.of(
                    name(TOLKIEN_SECTION, "beleg"),
                    name(SCROLL_SECTION, "aar")));

            assertEquals(List.of(),
                    MiscRegistry.getNameSection(RandnameType.RANDNAME_NUM_TYPES));
        }

        @Test
        @DisplayName("the returned list is a read-only view")
        void returnedListIsUnmodifiable() {
            MiscRegistry.setNames(List.of(name(TOLKIEN_SECTION, "beleg")));
            List<String> tolkien = MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN);

            assertThrows(UnsupportedOperationException.class, () -> tolkien.add("turin"));
        }

        @Test
        @DisplayName("editing the list handed to setNames does not reach the stored section")
        void callerCannotEditTheStoredSectionAfterwards() {
            List<String> words = new ArrayList<>(List.of("beleg"));
            MiscRegistry.setNames(List.of(new Name(TOLKIEN_SECTION, words)));

            words.add("turin");

            assertEquals(List.of("beleg"),
                    MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        }
    }
}
