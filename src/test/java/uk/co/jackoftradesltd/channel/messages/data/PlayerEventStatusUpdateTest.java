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
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link PlayerEventStatusUpdate#getPlayerStatusView()}/{@link PlayerEventStatusUpdate#getPlayerCharSheetView()},
 * {@link PlayerEventStatusUpdate#updatePlayerStatusView}/{@link PlayerEventStatusUpdate#updatePlayerCharSheetView}
 * and the static {@code updatePlayerStatus*}/{@code updatePlayerCharSheet*} field setters that
 * follow them. Moved here from {@code middle.game.GameWorldPlayerStatusUpdatersTest} once the
 * whole {@code playerStatusView} snapshot and its accessors relocated from {@code GameWorld} to
 * this class, so the test now sits next to the code it exercises; the char-sheet coverage
 * followed once those fields split out into their own {@link PlayerCharSheetView} record.
 *
 * <p>C has no equivalent of any of this: the sidebar is painted live from the {@code player} and
 * {@code cave} globals by the {@code prt_*} functions in {@code ui-display.c}, and the character
 * sheet likewise from the {@code display_panel} family in {@code ui-player.c}, so there is no
 * cached snapshot to diff a port against. What can be checked instead is internal to the Java
 * side alone — both records are immutable, so every field setter must rebuild the whole thing
 * from the current one, and the risk that matters is a transcription slip: a field landing in the
 * wrong constructor slot, silently overwriting a neighbour instead of the field the method is
 * named for. {@link #updatingOneFieldLeavesEveryOtherFieldUntouched} and
 * {@link #updatingOneCharSheetFieldLeavesEveryOtherFieldUntouched} sweep every setter against
 * every record component of its own record to rule that out.
 *
 * <p>Both caches are static fields shared across the JVM, so each test saves them beforehand and
 * restores them afterwards, the same as
 * {@code uk.co.jackoftradesltd.middle.game.GameWorldSetCharacterDungeonTest} does for
 * {@code characterDungeon}.
 *
 * <p>Class PlayerEventStatusUpdateTest coded on 260912, commented in full on 260925.
 *
 * @author Rowan Crowther
 */
class PlayerEventStatusUpdateTest {

    /**
     * The sidebar snapshot in place before the test, restored afterwards.
     */
    private PlayerStatusView savedPlayerStatusView;

    /**
     * The character-sheet snapshot in place before the test, restored afterwards.
     */
    private PlayerCharSheetView savedPlayerCharSheetView;

    /**
     * The nine skills-panel fields ({@code saveSkill} through {@code optionEffectiveSpeed}) that
     * every {@code updatePlayerCharSheet*} setter currently resets to its type's zero value —
     * {@code 0} or {@code false} — instead of carrying forward from
     * {@link PlayerEventStatusUpdate#cachedPlayerCharSheetView}, unlike the other 24 fields. Added
     * with the fields themselves ahead of the record shape {@code getPanelSkills()} is really meant
     * to arrive through, a placeholder Rowan chose deliberately over building throw-away plumbing
     * for a shape about to change again. {@link #assertOnlyFieldChanged} treats every name in this
     * set as "resets to default" rather than "must equal the baseline", so
     * {@link #updatingOneCharSheetFieldLeavesEveryOtherFieldUntouched} documents today's real
     * behaviour instead of asserting the untouched-field invariant the other 24 fields keep. When
     * the setters start carrying these nine forward, this set — and the branch in
     * {@link #assertOnlyFieldChanged} that reads it — should be deleted, not extended.
     */
    private static final Set<String> CHAR_SHEET_PLACEHOLDER_RESET_FIELDS = Set.of(
            "saveSkill", "stealthSkill", "disarmPhysSkill", "disarmMagicSkill", "deviceSkill",
            "searchSkill", "infra", "calcSpeed", "optionEffectiveSpeed");

    /**
     * A {@link PlayerStatusView} with a distinct, recognisable value in every one of its 42
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
                15,
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
                2,
                "Baseline Study",
                "Baseline Conditions",
                "Baseline Detection",
                "Baseline Resting",
                "Baseline Feeling",
                "Baseline Light",
                12,
                13,
                14,
                15);
    }

    /**
     * A {@link PlayerCharSheetView} with a distinct, recognisable value in every one of its
     * thirty-three fields, so a field landing in the wrong slot after a rebuild shows up
     * immediately.
     *
     * @return the baseline view
     */
    private static PlayerCharSheetView charSheetBaseline() {
        return new PlayerCharSheetView(
                7,
                false,
                new int[]{101, 102, 103, 104, 105},
                new int[]{111, 112, 113, 114, 115},
                new int[]{121, 122, 123, 124, 125},
                new int[]{131, 132, 133, 134, 135},
                new int[]{141, 142, 143, 144, 145},
                999,
                888,
                new long[]{151L, 152L, 153L},
                777,
                778,
                779,
                780,
                781,
                782,
                783,
                784,
                785,
                786,
                787,
                788,
                789,
                790,
                791,
                792,
                793,
                794,
                795,
                796,
                797,
                798,
                true);
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
     * {@code changedComponent}, which must instead equal {@code expectedValue} — the
     * {@link PlayerStatusView} sweep's entry point, where every other field really is expected to
     * survive untouched.
     *
     * @param baseline         the view before the setter under test ran
     * @param actual           the view after it ran
     * @param changedComponent the record component the setter under test is named for
     * @param expectedValue    the value that component should now hold
     */
    private static void assertOnlyFieldChanged(Record baseline, Record actual,
                                               String changedComponent, Object expectedValue)
            throws ReflectiveOperationException {
        assertOnlyFieldChanged(baseline, actual, changedComponent, expectedValue, Set.of());
    }

    /**
     * Asserts that {@code actual} matches {@code baseline} in every record component except
     * {@code changedComponent} (which must instead equal {@code expectedValue}) and every name in
     * {@code resetToDefaultFields} (which must instead equal its type's zero value — {@code 0} for
     * an {@code int}, {@code false} for a {@code boolean}). Works against either
     * {@link PlayerStatusView} or {@link PlayerCharSheetView}, reading the components from
     * {@code baseline}'s own runtime class rather than a hardcoded one, since both sweeps
     * ({@link #updatingOneFieldLeavesEveryOtherFieldUntouched} and
     * {@link #updatingOneCharSheetFieldLeavesEveryOtherFieldUntouched}) share this same check;
     * {@code resetToDefaultFields} is only ever non-empty for the latter, via
     * {@link #CHAR_SHEET_PLACEHOLDER_RESET_FIELDS}.
     *
     * @param baseline              the view before the setter under test ran
     * @param actual                the view after it ran
     * @param changedComponent      the record component the setter under test is named for
     * @param expectedValue         the value that component should now hold
     * @param resetToDefaultFields  component names known to come back at their zero value
     *                              regardless of what {@code baseline} held, rather than surviving
     *                              untouched
     */
    private static void assertOnlyFieldChanged(Record baseline, Record actual,
                                               String changedComponent, Object expectedValue,
                                               Set<String> resetToDefaultFields)
            throws ReflectiveOperationException {
        for (RecordComponent component : baseline.getClass().getRecordComponents()) {
            Object actualValue = component.getAccessor().invoke(actual);
            Object expected;
            if (component.getName().equals(changedComponent)) {
                expected = expectedValue;
            } else if (resetToDefaultFields.contains(component.getName())) {
                expected = component.getType() == boolean.class ? false : 0;
            } else {
                expected = component.getAccessor().invoke(baseline);
            }

            if (expected instanceof int[] expectedArray) {
                assertArrayEquals(expectedArray, (int[]) actualValue, component.getName());
            } else if (expected instanceof String[] expectedArray) {
                assertArrayEquals(expectedArray, (String[]) actualValue, component.getName());
            } else if (expected instanceof long[] expectedArray) {
                assertArrayEquals(expectedArray, (long[]) actualValue, component.getName());
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
                Arguments.of("updatePlayerStatusStatsString", String[].class,
                        new String[]{"New STR", "New INT", "New WIS", "New DEX", "New CON"}, "statString"),
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
                Arguments.of("updatePlayerStatusMaxDepth", int.class, 350, "maxDepth"),
                Arguments.of("updatePlayerStatusStudyStatus", String.class, "Study (3)", "studyStatus"),
                Arguments.of("updatePlayerStatusStudyConditions", String.class, "New Conditions",
                        "studyConditions"),
                Arguments.of("updatePlayerStatusDetectionStatus", String.class, "DTrap",
                        "detectionStatus"),
                Arguments.of("updatePlayerStatusRestingRepeatStatus", String.class, "Rest 50",
                        "restingRepeatingState"),
                Arguments.of("updatePlayerStatusLevelFeeling", String.class, "LF:5-3", "levelFeeling"),
                Arguments.of("updatePlayerStatusLightLevel", String.class, "Light 3", "lightLevel"),
                Arguments.of("updatePlayerStatusEquipSlotCount", int.class, 14, "equipmentSlotCount"),
                Arguments.of("updatePlayerStatusTurn", int.class, 9999, "turn"),
                Arguments.of("updatePlayerStatusTotalEnergy", int.class, 8888, "totalEnergy"),
                Arguments.of("updatePlayerStatusRestingEnergy", int.class, 7777, "restingTurn")
        );
    }

    /**
     * Every {@code updatePlayerCharSheet*} field setter: its declared name, its parameter type, a
     * value distinct from anything in {@link #charSheetBaseline()}, and the
     * {@link PlayerCharSheetView} record component it is named for.
     *
     * @return one row per setter under test
     */
    private static Stream<Arguments> charSheetFieldUpdaters() {
        return Stream.of(
                Arguments.of("updatePlayerCharSheetBodyCount", int.class, 21, "bodyCount"),
                Arguments.of("updatePlayerCharSheetIsPlaying", boolean.class, true, "playerIsPlaying"),
                Arguments.of("updatePlayerCharSheetRaceStatBonuses", int[].class,
                        new int[]{-1, 0, 1, 2, 3}, "playerRaceStatBonuses"),
                Arguments.of("updatePlayerCharSheetClassStatBonuses", int[].class,
                        new int[]{-2, -1, 0, 1, 2}, "playerClassStatBonuses"),
                Arguments.of("updatePlayerCharSheetEquipStatBonuses", int[].class,
                        new int[]{5, -5, 10, -10, 0}, "playerEquipStatBonuses"),
                Arguments.of("updatePlayerCharSheetTotalStatBonuses", int[].class,
                        new int[]{18, 19, 20, 21, 22}, "playerTotalStatBonuses"),
                Arguments.of("updatePlayerCharSheetCurrentStatBonuses", int[].class,
                        new int[]{17, 18, 19, 20, 21}, "playerCurrModStat"),
                Arguments.of("updatePlayerCharSheetTotalWeight", int.class, 315, "totalWeight"),
                Arguments.of("updatePlayerCharSheetExpToLevel", long[].class,
                        new long[]{10L, 25L, 45L}, "expToLevel"),
                Arguments.of("updatePlayerCharSheetExpFactor", int.class, 275, "expFactor"),
                Arguments.of("updatePlayerCharSheetHeight", int.class, 68, "height"),
                Arguments.of("updatePlayerCharSheetWeight", int.class, 165, "weight"),
                Arguments.of("updatePlayerCharSheetAge", int.class, 45, "age"),
                Arguments.of("updatePlayerCharSheetToA", int.class, 91, "toA"),
                Arguments.of("updatePlayerCharSheetToD", int.class, 92, "toD"),
                Arguments.of("updatePlayerCharSheetToH", int.class, 93, "toH"),
                Arguments.of("updatePlayerCharSheetMeleeSkill", int.class, 94, "meleeSkill"),
                Arguments.of("updatePlayerCharSheetShootSkill", int.class, 95, "shootSkill"),
                Arguments.of("updatePlayerCharSheetBthPlusAdj", int.class, 96, "bthPlusAdj"),
                Arguments.of("updatePlayerCharSheetMeleeDice", int.class, 97, "meleeDice"),
                Arguments.of("updatePlayerCharSheetMeleeSides", int.class, 98, "meleeSides"),
                Arguments.of("updatePlayerCharSheetnumBlows", int.class, 99, "numBlows"),
                Arguments.of("updatePlayerCharSheetNumShots", int.class, 100, "numShots")
        );
    }

    /**
     * Records both current snapshots so they can be put back.
     */
    @BeforeEach
    void saveSnapshots() {
        savedPlayerStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
        savedPlayerCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();
    }

    /**
     * Restores both snapshots the test found on entry.
     */
    @AfterEach
    void restoreSnapshots() {
        PlayerEventStatusUpdate.updatePlayerStatusView(savedPlayerStatusView);
        PlayerEventStatusUpdate.updatePlayerCharSheetView(savedPlayerCharSheetView);
    }

    /**
     * The ordinary path for every sidebar field setter, all 41 in one sweep: calling it changes
     * exactly the {@link PlayerStatusView} component it is named for, and leaves the other 41
     * exactly as {@link #baseline()} held them.
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
     * The ordinary path for every character-sheet field setter, all twenty-three in one sweep:
     * calling it changes exactly the {@link PlayerCharSheetView} component it is named for, leaves
     * the other twenty-three exactly as {@link #charSheetBaseline()} held them, and resets the nine
     * {@link #CHAR_SHEET_PLACEHOLDER_RESET_FIELDS} to their zero value regardless of what the
     * baseline held — the {@link PlayerCharSheetView} counterpart to
     * {@link #updatingOneFieldLeavesEveryOtherFieldUntouched}, except for that last part, which
     * documents today's placeholder behaviour rather than an invariant worth keeping.
     */
    @ParameterizedTest(name = "{0} changes only {3}")
    @MethodSource("charSheetFieldUpdaters")
    @DisplayName("a char-sheet field setter changes its own field and resets the placeholder fields")
    void updatingOneCharSheetFieldLeavesEveryOtherFieldUntouched(String methodName, Class<?> paramType,
                                                                 Object newValue, String changedComponent)
            throws ReflectiveOperationException {
        PlayerCharSheetView baseline = charSheetBaseline();
        PlayerEventStatusUpdate.updatePlayerCharSheetView(baseline);

        invokeSetter(methodName, paramType, newValue);

        assertOnlyFieldChanged(baseline, PlayerEventStatusUpdate.getPlayerCharSheetView(), changedComponent,
                newValue, CHAR_SHEET_PLACEHOLDER_RESET_FIELDS);
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
     * {@link PlayerEventStatusUpdate#getPlayerCharSheetView()} hands back the very instance
     * {@link PlayerEventStatusUpdate#updatePlayerCharSheetView} was given, not a copy — the
     * {@link PlayerCharSheetView} counterpart to {@link #wholeViewSetIsReadableBackAsSameInstance}.
     */
    @Test
    @DisplayName("the whole char-sheet-view setter is readable back as the same instance")
    void wholeCharSheetViewSetIsReadableBackAsSameInstance() {
        PlayerCharSheetView view = charSheetBaseline();

        PlayerEventStatusUpdate.updatePlayerCharSheetView(view);

        assertSame(view, PlayerEventStatusUpdate.getPlayerCharSheetView());
    }

    /**
     * A second wholesale replacement discards the first outright, with no attempt to merge the
     * two — unlike the field setters, which each preserve the other 38 fields.
     */
    @Test
    @DisplayName("a second whole-view set replaces the first outright")
    void secondWholeViewSetReplacesFirst() {
        PlayerStatusView first = baseline();
        PlayerStatusView second = new PlayerStatusView(
                "Second Name", "Second Title", "Second Race", "Second Class",
                1, 1, 0L, 0L, 0L, 1, 1, 0, 0, 0, 110,
                new int[]{1, 1, 1, 1, 1}, new int[]{1, 1, 1, 1, 1},
                new String[]{"", "", "", "", ""},
                0, 0, false, false, false, false, false, false, false, false, false, false,
                0, 0, "", "", "", "", "", "", 0, 0, 0, 0);

        PlayerEventStatusUpdate.updatePlayerStatusView(first);
        PlayerEventStatusUpdate.updatePlayerStatusView(second);

        assertSame(second, PlayerEventStatusUpdate.getPlayerStatusView());
    }

    /**
     * A second wholesale replacement discards the first outright, with no attempt to merge the
     * two — the {@link PlayerCharSheetView} counterpart to {@link #secondWholeViewSetReplacesFirst}.
     */
    @Test
    @DisplayName("a second whole char-sheet-view set replaces the first outright")
    void secondWholeCharSheetViewSetReplacesFirst() {
        PlayerCharSheetView first = charSheetBaseline();
        PlayerCharSheetView second = new PlayerCharSheetView(
                0, false,
                new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}, 0, 0, new long[]{}, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, false);

        PlayerEventStatusUpdate.updatePlayerCharSheetView(first);
        PlayerEventStatusUpdate.updatePlayerCharSheetView(second);

        assertSame(second, PlayerEventStatusUpdate.getPlayerCharSheetView());
    }
}
