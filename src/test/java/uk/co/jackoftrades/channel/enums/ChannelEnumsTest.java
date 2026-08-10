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

package uk.co.jackoftrades.channel.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the vocabularies the channel messages are written in — the enums that say what a
 * message <em>means</em>, as against the records that say what shape it is.
 *
 * <p>Every one of these is a transcription of a list in the C source, and a transcription has one
 * interesting failure mode: something dropped, duplicated or mistyped in the middle, where nobody
 * looks. The counts and the boundary constants are pinned here so that a slip during a merge or a
 * hand-edit is caught at build time rather than by a display drawing the wrong thing much later.
 *
 * <p>The tests are deliberately about <b>structure</b>, not about naming every constant: listing
 * all 65 event types again would just be a second transcription, wrong in its own way. Counting
 * them, checking the ends, and checking for duplicates catches the realistic mistakes without
 * doubling the maintenance.
 *
 * @author Rowan Crowther
 */
class ChannelEnumsTest {

    /**
     * @param constants the constants to check
     * @author Rowan Crowther
     */
    private static void assertNoDuplicates(Enum<?>[] constants) {
        long distinct = Arrays.stream(constants).map(Enum::name).distinct().count();

        assertEquals(constants.length, distinct, "a constant appears more than once");
        assertTrue(distinct > 0, "the enum should not be empty");
    }

    /**
     * Tests for {@link GameEventType}, the port of C's {@code game_event_type}
     * ({@code src/game-event.h}).
     *
     * @author Rowan Crowther
     */
    @Nested
    class GameEvents {

        /**
         * C's enum runs from {@code EVENT_MAP} to {@code EVENT_END} and has 65 members. A port
         * that has drifted by one has almost certainly lost a constant in the middle.
         *
         * @author Rowan Crowther
         */
        @Test
        void thereAreAsManyEventTypesAsInTheCOriginal() {
            assertEquals(65, GameEventType.values().length,
                    "C's game_event_type has 65 members (src/game-event.h)");
        }

        /**
         * The ends of the list, which is where a truncated paste shows up.
         *
         * @author Rowan Crowther
         */
        @Test
        void theListStartsAndEndsWhereCsDoes() {
            GameEventType[] all = GameEventType.values();

            assertSame(GameEventType.EVENT_MAP, all[0], "C's list opens with EVENT_MAP");
            assertSame(GameEventType.EVENT_END, all[all.length - 1], "C's list closes with EVENT_END");
        }

        /**
         * The events this migration actually uses, named individually because stage 2 and stage 3
         * reference them by name and a rename would otherwise fail somewhere less obvious.
         *
         * @author Rowan Crowther
         */
        @Test
        void theEventsThisMigrationDependsOnExist() {
            assertNotNull(GameEventType.EVENT_ENTER_INIT);
            assertNotNull(GameEventType.EVENT_LEAVE_INIT);
            assertNotNull(GameEventType.EVENT_INITSTATUS);
            assertNotNull(GameEventType.EVENT_MESSAGE);
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void noEventTypeIsListedTwice() {
            assertNoDuplicates(GameEventType.values());
        }
    }

    /**
     * Tests for {@link ProjectionEnum}, the port of C's {@code PROJ_*} enum, which C builds by
     * including {@code list-elements.h} and then {@code list-projections.h}.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Projections {

        /**
         * C has 25 elements followed by 31 projections and no zero placeholder, giving 56. This
         * port adds {@code PROJ_NONE} at the front, so it has 57 — and therefore every ordinal is
         * one higher than C's.
         *
         * <p>That difference is deliberate and harmless only while nothing indexes by ordinal. C
         * does exactly that, in the place this enum is heading: {@code projections[typ]},
         * {@code proj_to_attr[typ][motion]}, {@code proj_to_char[typ][motion]}. If those tables
         * are ever ported, this test is the reminder that the index must be adjusted or resolved
         * by name.
         *
         * @author Rowan Crowther
         */
        @Test
        void theProjectionListIsCsPlusAZeroPlaceholder() {
            assertEquals(57, ProjectionEnum.values().length,
                    "25 elements + 31 projections from C, plus this port's PROJ_NONE");
            assertSame(ProjectionEnum.PROJ_NONE, ProjectionEnum.values()[0],
                    "PROJ_NONE is the port's own addition and sits at ordinal 0");
        }

        /**
         * The elements are the first stretch of the list, in {@code list-elements.h} order, and
         * the projection parser in C refuses to load {@code projection.txt} unless that stays
         * true. Pinned as a run rather than one by one, because it is the <em>order</em> that
         * carries the meaning.
         *
         * @author Rowan Crowther
         */
        @Test
        void theElementsComeFirstInTheirCOrder() {
            List<String> expected = List.of(
                    "PROJ_ACID", "PROJ_ELEC", "PROJ_FIRE", "PROJ_COLD", "PROJ_POIS",
                    "PROJ_LIGHT", "PROJ_DARK", "PROJ_SOUND", "PROJ_SHARD", "PROJ_NEXUS",
                    "PROJ_NETHER", "PROJ_CHAOS", "PROJ_DISEN", "PROJ_WATER", "PROJ_ICE",
                    "PROJ_GRAVITY", "PROJ_INERTIA", "PROJ_FORCE", "PROJ_TIME", "PROJ_PLASMA",
                    "PROJ_METEOR", "PROJ_MISSILE", "PROJ_MANA", "PROJ_HOLY_ORB", "PROJ_ARROW");

            List<String> actual = Arrays.stream(ProjectionEnum.values())
                    .skip(1)
                    .limit(expected.size())
                    .map(Enum::name)
                    .collect(Collectors.toList());

            assertEquals(expected, actual, "the elements must open the projection list, in "
                    + "list-elements.h order - C's parser checks this and refuses to load if it drifts");
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void noProjectionIsListedTwice() {
            assertNoDuplicates(ProjectionEnum.values());
        }

        /**
         * Every element resolves to the projection of the same name. This is the correspondence
         * C relies on positionally and this port resolves by name; if the two lists drift apart,
         * the lookup starts returning null and this is where it shows.
         *
         * @author Rowan Crowther
         */
        @Test
        void everyRealElementHasAMatchingProjection() {
            for (ElementEnum element : ElementEnum.values()) {
                if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) {
                    continue;
                }

                assertNotNull(element.getProjectionEnum(),
                        element.name() + " has no matching projection - the element and projection "
                                + "lists have drifted apart");
            }
        }
    }

    /**
     * Tests for {@link UiEventType}, the port of C's {@code ui_event_type}
     * ({@code src/ui-event.h}).
     *
     * @author Rowan Crowther
     */
    @Nested
    class UiEvents {

        /**
         * C has eight kinds plus {@code EVT_NONE}. The three basic ones and {@code EVT_BUTTON}
         * come first, then the four "abstract" menu events.
         *
         * @author Rowan Crowther
         */
        @Test
        void theUiEventKindsMatchTheCOriginal() {
            assertEquals(List.of("EVT_NONE", "EVT_KBRD", "EVT_MOUSE", "EVT_RESIZE", "EVT_BUTTON",
                            "EVT_ESCAPE", "EVT_MOVE", "EVT_SELECT", "EVT_SWITCH"),
                    Arrays.stream(UiEventType.values()).map(Enum::name).collect(Collectors.toList()));
        }
    }

    /**
     * Tests for the two lifecycle vocabularies, which are this port's own — nothing in C
     * corresponds to them, because C has no channels to send them on.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Lifecycles {

        /**
         * Kept small on purpose. Every constant added here is a state the two halves have to agree
         * about, so growth should be a decision rather than a drift.
         *
         * @author Rowan Crowther
         */
        @Test
        void theLifecycleVocabulariesAreMinimal() {
            assertEquals(List.of("START", "SAVE_AND_STOP"),
                    Arrays.stream(UILifecycleEvent.values()).map(Enum::name).collect(Collectors.toList()));
            assertEquals(List.of("STOPPED"),
                    Arrays.stream(CoreLifecycleEvent.values()).map(Enum::name).collect(Collectors.toList()));
        }
    }
}