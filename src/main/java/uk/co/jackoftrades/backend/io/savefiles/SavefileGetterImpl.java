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

package uk.co.jackoftrades.backend.io.savefiles;

import uk.co.jackoftrades.backend.io.AngDir;
import uk.co.jackoftrades.channel.directories.AngbandDirs;

public class SavefileGetterImpl {
    private AngDir directory;
    private SavefileDetails details;
    private boolean haveDetails;
    private boolean haveSaveDir;

    public SavefileGetterImpl() {
        directory = new AngDir(AngbandDirs.ANGBAND_DIRS.SAVE.getPath());
        haveSaveDir = true;
    }

    public boolean gotSavefile() {
        String filename = "";
        details = null;

        if (directory == null) {
            haveSaveDir = false;
            return false;
        }

        haveSaveDir = true;
        details = new SavefileDetails();
        details.setOffset(0);

        return true;
    }
}
