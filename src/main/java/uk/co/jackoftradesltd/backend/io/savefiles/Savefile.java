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

package uk.co.jackoftradesltd.backend.io.savefiles;

import uk.co.jackoftradesltd.backend.io.AngFile;
import uk.co.jackoftradesltd.backend.io.enums.FileModeEnum;
import uk.co.jackoftradesltd.backend.io.enums.FileTypeEnum;

import java.nio.file.Path;

/**
 * Savefile-level operations, ported from {@code savefile.c}. At present this holds only the reader for the
 * {@code description} block, {@link #savefileGetDescription(Path)}, which the savefile picker uses to label each
 * entry. Loading and saving the blocks themselves are not ported yet.
 * <p>
 * C keeps the block machinery ({@code check_header}, {@code next_blockheader}, {@code skip_block},
 * {@code load_block}, {@code rd_string}) in the same file as static functions. The port has none of them yet, so
 * {@link #savefileGetDescription(Path)} is a partial port.
 * <p>
 * Class Savefile coded on 261017, commented in full on 261017.
 */
public class Savefile {
    /**
     * Try to get the {@code description} block from a savefile, failing gracefully. The picker in
     * {@link SavefileGetterImpl} passes each file name it finds to this and shows the result next to it.
     * <p>
     * Port of {@code savefile_get_description} in {@code savefile.c}. C opens the file for reading, returns
     * {@code NULL} if it cannot, then blanks its static 120-byte description buffer. It then runs
     * {@code check_header}: a bad header gives the text {@code "Invalid savefile"}; a good one makes it walk the
     * block headers, {@code skip_block} every block not named {@code "description"}, read that one with
     * {@code rd_string} and stop. The file is closed on every path that opened it, and the buffer is returned.
     * <p>
     * <b>Outstanding:</b> only the open step is ported. The file is opened and closed, {@code null} is returned
     * if it could not be opened (C's {@code NULL}), and otherwise the result is always the empty string, as the
     * description is never read. Header checking, block scanning and the 119-character cap that C's buffer
     * imposes are not done. The caller in {@link SavefileGetterImpl} passes the result straight to
     * {@code SavefileDetails.setDescription} without a null check.
     * <p>
     * Function savefileGetDescription coded on 261017, commented in full on 261017.
     * @param path the savefile to read
     * @return the text of the description block, {@code null} if the file could not be opened; at present the
     * empty string for any file that opens
     */
    public static String savefileGetDescription(Path path) {
        // Stub class
        // TODO Implement

        AngFile saveFile = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        
        boolean opened = saveFile.open(FileTypeEnum.FTYPE_TEXT);
        saveFile.close();
        
        if (!opened) return null;
        
        return "";
    }
}
