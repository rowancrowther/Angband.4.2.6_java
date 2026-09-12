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

package uk.co.jackoftradesltd.channel.messages.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link PlayerEventStatusUpdate#getPlayerStatusView()},
 * {@link PlayerEventStatusUpdate#updatePlayerStatusView} and the static
 * {@code updatePlayerStatus*} field setters that follow them. Moved here from
 * {@code middle.game.GameWorldPlayerStatusUpdatersTest} once the whole
 * {@code playerStatusView} snapshot and its accessors relocated from {@code GameWorld} to this
 * class, so the test now sits next to the code it exercises.
 *
 * <p>C has no equivalent of any of this: the sidebar is painted live from the {@code player} and
 * {@code cave} globals by the {@code prt_*} functions in {@code ui-display.c}, so there is no
 * cached snapshot to diff a port against. What can be checked instead is internal to the Java
 * side alone — {@link PlayerStatusView} is an immutable record, so every {@code updatePlayerStatus*}
 * method must rebuild the whole thing from the current one, and the risk that matters is a
 * transcription slip: a field landing in the wrong constructor slot, silently overwriting a
 * neighbour instead of the field the method is named for. {@link #updatingOneFieldLeavesEveryOtherFieldUntouched}
 * sweeps every one of the 35 setters against every one of the 38 record components to rule that
 * out.
 *
 * <p>{@code cachedPlayerStatusView} is a static field shared across the JVM, so each test saves it
 * beforehand and restores it afterwards, the same as
 * {@code uk.co.jackoftradesltd.middle.game.GameWorldSetCharacterDungeonTest} does for
 * {@code characterDungeon}.
 *
 * <p>Class PlayerEventStatusUpdateTest coded on 260912, commented in full on 260912.
 *
 * @author Rowan Crowther
 */
class PlayerEventStatusUpdateTest {

    /**
     * The snapshot in place before the test, restored afterwards.
     */
    private PlayerStatusView savedPlayerStatusView;

    /**
     * A {@link PlayerStatusView} with a distinct, recognisable value in every one of its 38
     * fields, so a field landing in the wrong slot after a rebuild shows up immediately.
     *
     * @return the baseline view
     */
    private static PlayerStatusView baseline() {
        return new PlayerStatusView(
                "Baseline Name",
                "Baseline Title",
                "Baseline Race",
                "Baseline Class",
                5,
                1000L,
                2000L,
                3000L,
                10,
                20,
                3,
                6,
                4,
                110,
                new int[]{10, 11, 12, 13, 14},
                new int[]{16, 17, 18, 19, 20},
                new String[]{"Baseline STR", "Baseline INT", "Baseline WIS", "Baseline DEX", "Baseline CON"},
                7,
                50,
                100,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                1,
                "Baseline Study",
                "Baseline Conditions",
                "Baseline Detection",
                "Baseline Resting",
                "Baseline Feeling",
                "Baseline Light",
                12);
    }

    /**
     * Calls one of the static field setters by name.
     *
     * @param methodName the setter's name
     * @param paramType  the setter's single parameter type
     * @param value      the value to pass
     */
    private static void invokeSetter(String methodName, Class<?> paramType, Object value)
            throws ReflectiveOperationException {
        Method setter = PlayerEventStatusUpdate.class.getMethod(methodName, paramType);
        setter.invoke(null, value);
    }

    /**
     * Asserts that {@code actual} matches {@code baseline} in every record component except
     * {@code changedComponent}, which must instead equal {@code expectedValue}.
     *
     * @param baseline         the view before the setter under test ran
     * @param actual           the view after it ran
     * @param changedComponent the record component the setter under test is named for
     * @param expectedValue    the value that component should now hold
     */
    private static void assertOnlyFieldChanged(PlayerStatusView baseline, PlayerStatusView actual,
                                               String changedComponent, Object expectedValue)
            throws ReflectiveOperationException {
        for (RecordComponent component : PlayerStatusView.class.getRecordComponents()) {
            Object actualValue = component.getAccessor().invoke(actual);
            Object expected = component.getName().equals(changedComponent)
                    ? expectedValue
                    : component.getAccessor().invoke(baseline);

            if (expected instanceof int[] expectedArray) {
                assertArrayEquals(expectedArray, (int[]) actualValue, component.getName());
            } else if (expected instanceof String[] expectedArray) {
                assertArrayEquals(expectedArray, (String[]) actualValue, component.getName());
            } else {
                assertEquals(expected, actualValue, component.getName());
            }
        }
    }

    /**
     * Every {@code updatePlayerStatus*} field setter: its declared name, its parameter type, a
     * value distinct from anything in {@link #baseline()}, and the {@link PlayerStatusView}
     * record component it is named for.
     *
     * @return one row per setter under test
     */
    private static Stream<Arguments> fieldUpdaters() {
        return Stream.of(
                Arguments.of("updatePlayerStatusPlayerName", String.class, "New Name", "name"),
                Arguments.of("updatePlayerStatusPlayerTitle", String.class, "New Title", "title"),
                Arguments.of("updatePlayerStatusRaceName", String.class, "New Race", "raceName"),
                Arguments.of("updatePlayerStatusClassName", String.class, "New Class", "className"),
                Arguments.of("updatePlayerStatusLevel", int.class, 99, "level"),
                Arguments.of("updatePlayerStatusExperience", long.class, 999L, "experience"),
                Arguments.of("updatePlayerStatusMaxExperience", long.class, 1999L, "maxExperience"),
                Arguments.of("updatePlayerStatusGold", long.class, 5000L, "gold"),
                Arguments.of("updatePlayerStatusCurrentHP", int.class, 77, "chp"),
                Arguments.of("updatePlayerStatusMaxHP", int.class, 88, "mhp"),
                Arguments.of("updatePlayerStatusCurrentSP", int.class, 33, "csp"),
                Arguments.of("updatePlayerStatusMaxSP", int.class, 44, "msp"),
                Arguments.of("updatePlayerStatusArmourClass", int.class, 22, "armourClass"),
                Arguments.of("updatePlayerStatusSpeed", int.class, 130, "speed"),
                Arguments.of("updatePlayerStatusCurrentStats", int[].class,
                        new int[]{1, 2, 3, 4, 5}, "currentStats"),
                Arguments.of("updatePlayerStatusMaxStats", int[].class,
                        new int[]{7, 8, 9, 10, 11}, "maxStats"),
                Arguments.of("updatePlayerStatusMonsterHealth", int.class, 40, "monsterHealth"),
                Arguments.of("updatePlayerStatusMaxMonsterHealth", int.class, 400, "maxMonsterHealth"),
                Arguments.of("updatePlayerStatusMonsterVisible", boolean.class, true, "monsterVisible"),
                Arguments.of("updatePlayerStatusPlayerHallucinating", boolean.class, true,
                        "playerHallucinating"),
                Arguments.of("updatePlayerStatusMonsterTracked", boolean.class, true, "monsterTracked"),
                Arguments.of("updatePlayerStatusMonTmdFear", boolean.class, true, "monsterTmdFear"),
                Arguments.of("updatePlayerStatusMonTmdDisen", boolean.class, true, "monsterTmdDisen"),
                Arguments.of("updatePlayerStatusMonTmdCommand", boolean.class, true, "monsterTmdCommand"),
                Arguments.of("updatePlayerStatusMonTmdConf", boolean.class, true, "monsterTmdConf"),
                Arguments.of("updatePlayerStatusMonTmdStun", boolean.class, true, "monsterTmdStun"),
                Arguments.of("updatePlayerStatusMonTmdSleep", boolean.class, true, "monsterTmdSleep"),
                Arguments.of("updatePlayerStatusMonTmdHold", boolean.class, true, "monsterTmdHold"),
                Arguments.of("updatePlayerStatusDepth", int.class, 250, "depth"),
                Arguments.of("updatePlayerStatusStudyStatus", String.class, "Study (3)", "studyStatus"),
                Arguments.of("updatePlayerStatusStudyConditions", String.class, "New Conditions",
                        "studyConditions"),
                Arguments.of("updatePlayerStatusDetectionStatus", String.class, "DTrap",
                        "detectionStatus"),
                Arguments.of("updatePlayerStatusRestingRepeatStatus", String.class, "Rest 50",
                        "restingRepeatingState"),
                Arguments.of("updatePlayerStatusLevelFeeling", String.class, "LF:5-3", "levelFeeling"),
                Arguments.of("updatePlayerStatusLightLevel", String.class, "Light 3", "lightLevel"),
                Arguments.of("updatePlayerStatusEquipSlotCount", int.class, 14, "equipmentSlotCount")
        );
    }

    /**
     * Records the current snapshot so it can be put back.
     */
    @BeforeEach
    void savePlayerStatusView() {
        savedPlayerStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
    }

    /**
     * Restores the snapshot the test found on entry.
     */
    @AfterEach
    void restorePlayerStatusView() {
        PlayerEventStatusUpdate.updatePlayerStatusView(savedPlayerStatusView);
    }

    /**
     * The ordinary path for every field setter, all 35 in one sweep: calling it changes exactly
     * the {@link PlayerStatusView} component it is named for, and leaves the other 37 exactly as
     * {@link #baseline()} held them.
     */
    @ParameterizedTest(name = "{0} changes only {3}")
    @MethodSource("fieldUpdaters")
    @DisplayName("a field setter changes its own field and nothing else")
    void updatingOneFieldLeavesEveryOtherFieldUntouched(String methodName, Class<?> paramType,
                                                        Object newValue, String changedComponent)
            throws ReflectiveOperationException {
        PlayerStatusView baseline = baseline();
        PlayerEventStatusUpdate.updatePlayerStatusView(baseline);

        invokeSetter(methodName, paramType, newValue);

        assertOnlyFieldChanged(baseline, PlayerEventStatusUpdate.getPlayerStatusView(), changedComponent, newValue);
    }

    /**
     * The boundary stage 1 turned up: none of these setters validate or clamp, matching C, whose
     * {@code prt_hp} ({@code ui-display.c:314}) formats {@code player->chp} with a bare
     * {@code "%4d"} regardless of sign. A current-HP value below zero — the state a character is
     * in for the instant between a killing blow and the death check running — must survive the
     * rebuild unchanged rather than being floored at zero.
     */
    @Test
    @DisplayName("a negative current HP passes through unclamped")
    void negativeCurrentHpIsNotClamped() {
        PlayerEventStatusUpdate.updatePlayerStatusView(baseline());

        PlayerEventStatusUpdate.updatePlayerStatusCurrentHP(-5);

        assertEquals(-5, PlayerEventStatusUpdate.getPlayerStatusView().chp());
    }

    /**
     * {@link PlayerEventStatusUpdate#getPlayerStatusView()} hands back the very instance
     * {@link PlayerEventStatusUpdate#updatePlayerStatusView} was given, not a copy — the
     * wholesale-replacement counterpart to the field-by-field setters above.
     */
    @Test
    @DisplayName("the whole-view setter is readable back as the same instance")
    void wholeViewSetIsReadableBackAsSameInstance() {
        PlayerStatusView view = baseline();

        PlayerEventStatusUpdate.updatePlayerStatusView(view);

        assertSame(view, PlayerEventStatusUpdate.getPlayerStatusView());
    }

    /**
     * A second wholesale replacement discards the first outright, with no attempt to merge the
     * two — unlike the field setters, which each preserve the other 37 fields.
     */
    @Test
    @DisplayName("a second whole-view set replaces the first outright")
    void secondWholeViewSetReplacesFirst() {
        PlayerStatusView first = baseline();
        PlayerStatusView second = new PlayerStatusView(
                "Second Name", "Second Title", "Second Race", "Second Class",
                1, 0L, 0L, 0L, 1, 1, 0, 0, 0, 110,
                new int[]{1, 1, 1, 1, 1}, new int[]{1, 1, 1, 1, 1},
                new String[]{"", "", "", "", ""}, 0,
                0, 0, false, false, false, false, false, false, false, false, false, false,
                0, "", "", "", "", "", "", 0);

        PlayerEventStatusUpdate.updatePlayerStatusView(first);
        PlayerEventStatusUpdate.updatePlayerStatusView(second);

        assertSame(second, PlayerEventStatusUpdate.getPlayerStatusView());
    }
}
