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
 *  Java code copyright (c) 2026 Rowan Crowther, Jack of Trades Ltd.
 */

package uk.co.jackoftrades.frontend.sounds;

import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.io.File;

public enum MessageBoxFlags {
    MB_OK(""),
    MB_OKCANCEL(""),
    MB_ABORTRETRYIGNORE(""),
    MB_YESNOCANCEL(""),
    MB_YESNO(""),
    MB_RETRYCANCEL(""),
    MB_CANCELTRYCONTINUE(""),
    MB_ICONHAND(""),
    MB_ICONQUESTION(""),
    MB_ICONEXCLAMATION(""),
    MB_ICONASTERISK(GameConstants.ANGBAND_DIR_SOUNDS + "Windows Background.wav"),
    MB_USERICON(""),
    MB_ICONWARNING(""),
    MB_ICONERROR(""),
    MB_ICONINFORMATION(""),
    MB_ICONSTOP(""),
    MB_DEFBUTTON1(""),
    MB_DEFBUTTON2(""),
    MB_DEFBUTTON3(""),
    MB_DEFBUTTON4(""),
    MB_APPLMODAL(""),
    MB_SYSTEMMODAL(""),
    MB_TASKMODAL(""),
    MB_HELP(""),
    MB_SETFOREGROUND(""),
    MB_DEFAULT_DESKTOP_ONLY(""),
    MB_TOPMOST(""),
    MB_RIGHT(""),
    MB_RTLREADING("");

    private File file;

    MessageBoxFlags(String path) {
        file = new File(path);
    }

    public File getFileName() {
        return file;
    }
}
