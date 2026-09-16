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

package uk.co.jackoftradesltd.frontend.sounds;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MessageBoxFlags}, exercising the fact that C's {@code Term_xtra_win_noise}
 * ({@code [C] src/main-win.c}) only ever calls {@code MessageBeep(MB_ICONASTERISK)} -
 * no other {@code MB_*} flag is ever raised as a sound anywhere in the C original, so
 * {@link MessageBoxFlags#MB_ICONASTERISK} must be the only constant carrying a real
 * sound file, and that file must actually be present and readable as audio.
 *
 * @author Rowan Crowther
 */
class MessageBoxFlagsTest {

    /**
     * Every {@code MB_*} flag other than {@link MessageBoxFlags#MB_ICONASTERISK} is never
     * passed to {@code MessageBeep} anywhere in {@code main-win.c}, so none of them should
     * carry a sound file.
     */
    @Test
    void onlyIconAsteriskCarriesASoundFile() {
        for (MessageBoxFlags flag : EnumSet.complementOf(EnumSet.of(MessageBoxFlags.MB_ICONASTERISK))) {
            assertEquals("", flag.getFileName().getPath(),
                    flag + " should carry no sound file, only MB_ICONASTERISK does");
        }
    }

    /**
     * {@link MessageBoxFlags#MB_ICONASTERISK} is the flag C's {@code Term_xtra_win_noise}
     * always raises, so its file must resolve under the sounds directory C calls
     * {@code ANGBAND_DIR_SOUNDS} ({@code [C] src/init.c}).
     */
    @Test
    void iconAsteriskFileResolvesUnderTheSoundsDirectory() {
        File file = MessageBoxFlags.MB_ICONASTERISK.getFileName();

        assertEquals(AngbandDirs.ANGBAND_DIRS.SOUNDS.getPath() + "message.wav", file.getPath());
    }

    /**
     * The file backing {@link MessageBoxFlags#MB_ICONASTERISK} must actually exist and be
     * readable as audio - if it were missing or corrupt, {@code termXtraWinNoise} would
     * silently fail every time, unlike C's {@code MessageBeep} which reliably makes a sound.
     */
    @Test
    void iconAsteriskFileExistsAndIsReadableAsAudio() {
        File file = MessageBoxFlags.MB_ICONASTERISK.getFileName();

        assertTrue(file.exists(), "expected " + file + " to exist");

        assertDoesNotThrow(() -> {
            try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
                assertTrue(stream.getFormat().getSampleRate() > 0);
            }
        });
    }
}
