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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the static initializer that seeds {@link PlayerEventStatusUpdate}'s
 * {@code cachedPlayerStatusView} field at class load. Moved here from
 * {@code middle.game.GameWorldPlayerStatusViewDefaultTest} once the snapshot and its static
 * initializer relocated from {@code GameWorld} to this class.
 * {@code cachedPlayerStatusView} is a JVM-wide static shared by every other test in the suite (see
 * {@link PlayerEventStatusUpdateTest}'s save/restore fixture), so by the time any of them run it
 * has usually already been overwritten. The only way to see what the static initializer itself
 * produced is to load {@link PlayerEventStatusUpdate} into a fresh, disposable
 * {@link URLClassLoader} whose parent is the platform loader rather than the shared
 * system/application one: that defines an independent {@code Class} object with its own
 * {@code <clinit>}, decoupled from every other test's mutations of the copy the rest of the suite
 * shares.
 *
 * <p>C has nothing to cross-reference here: its {@code player} global is zero-initialised static
 * storage from process start, so there is no C initializer to port, only the Java-side claim (see
 * the static initializer's Javadoc in {@code PlayerEventStatusUpdate.java}) that this block
 * reproduces that all-zero starting state one field at a time.
 *
 * <p>Class PlayerEventStatusUpdateDefaultTest coded on 260912, commented in full on 260912.
 *
 * @author Rowan Crowther
 */
class PlayerEventStatusUpdateDefaultTest {

    /**
     * Loads a fresh copy of {@link PlayerEventStatusUpdate} in an isolated classloader and reads
     * its {@code cachedPlayerStatusView} field, triggering that copy's own static initialization
     * rather than reading the (possibly already-mutated) copy the rest of the suite shares.
     *
     * @return the freshly-seeded {@code PlayerStatusView} instance, typed as {@code Object}
     * because it was loaded by a different classloader than this test class was
     * @throws ReflectiveOperationException   if the field cannot be found or read
     * @throws java.net.MalformedURLException if a classpath entry cannot be turned into a URL
     */
    private static Object freshlySeededPlayerStatusView() throws Exception {
        String classpath = System.getProperty("java.class.path");
        URL[] urls = Arrays.stream(classpath.split(File.pathSeparator))
                .map(path -> {
                    try {
                        return new File(path).toURI().toURL();
                    } catch (java.net.MalformedURLException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .toArray(URL[]::new);

        try (URLClassLoader isolated = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader())) {
            Class<?> freshClass = isolated.loadClass(
                    "uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate");
            Field field = freshClass.getDeclaredField("cachedPlayerStatusView");
            field.setAccessible(true);
            return field.get(null);
        }
    }

    /**
     * Every field the static initializer seeds is at its zero-equivalent default: {@code null}
     * for every {@code String} field, {@code 0}/{@code 0L}/{@code false} for every numeric or
     * boolean one, a five-element all-zero array (matching C's {@code STAT_MAX}) for both stat
     * arrays, and the fixed stat-name labels for {@code statString} — that field is schema data
     * (mirroring {@link uk.co.jackoftradesltd.middle.enums.Stats#getStatString()}), the same for
     * every player, not a per-character value, so it is seeded with real names rather than blanks.
     */
    @Test
    @DisplayName("the static initializer seeds an all-default snapshot before any player exists")
    void staticInitializerSeedsAllDefaultSnapshot() throws Exception {
        Object freshView = freshlySeededPlayerStatusView();
        assertNotNull(freshView);

        for (RecordComponent component : freshView.getClass().getRecordComponents()) {
            Object value = component.getAccessor().invoke(freshView);
            switch (component.getName()) {
                case "currentStats", "maxStats" ->
                        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, (int[]) value, component.getName());
                case "statString" ->
                        assertArrayEquals(new String[]{"STR", "INT", "WIS", "DEX", "CON"}, (String[]) value,
                                component.getName());
                case "level", "chp", "mhp", "csp", "msp", "armourClass", "speed", "monsterHealth",
                     "maxMonsterHealth", "depth", "equipmentSlotCount", "bodyCount" ->
                        assertEquals(0, value, component.getName());
                case "experience", "maxExperience", "gold" -> assertEquals(0L, value, component.getName());
                case "monsterVisible", "playerHallucinating", "monsterTracked", "monsterTmdFear",
                     "monsterTmdDisen", "monsterTmdCommand", "monsterTmdConf", "monsterTmdStun",
                     "monsterTmdSleep", "monsterTmdHold" -> assertEquals(false, value, component.getName());
                default -> assertNull(value, component.getName());
            }
        }
    }
}
