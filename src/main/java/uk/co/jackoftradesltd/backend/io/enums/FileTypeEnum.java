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

package uk.co.jackoftradesltd.backend.io.enums;

/**
 * What kind of thing a file is when it is being written, as passed to {@code file_open()}. This is C's
 * {@code file_type} enum from {@code z-file.h}, with the same four constants in the same order ({@code
 * FTYPE_TEXT}, {@code FTYPE_SAVE}, {@code FTYPE_RAW}, {@code FTYPE_HTML}), followed by one constant C does not have,
 * {@link #FTYPE_NULL}.
 * <p>
 * C numbers its constants from 1 ({@code FTYPE_TEXT = 1}), so {@link #ordinal()} is one lower than the C value here.
 * Nothing in the port reads the number, so the shift is harmless, but do not use {@code ordinal()} as a C value or
 * write it to a file.
 * <p>
 * In C the type does one thing inside {@code file_open()}: {@code FTYPE_SAVE} with {@code MODE_WRITE} opens the file
 * exclusively, failing if it already exists and creating it owner-only. Every other type opens as a plain write, and is
 * only handed on to {@code file_open_hook}, a platform hook for setting file types that C calls after any open but a
 * read and that the port does not have. {@code FileHandler.open} follows the same rule, and records {@code FTYPE_SAVE}
 * as its {@code isSave} flag.
 * <p>
 * C's header says to pass {@code -1} as the type when reading. No {@code file_type} constant is {@code -1}, and every C
 * caller that reads passes {@code FTYPE_TEXT} instead, as the port's callers do.
 * <p>
 * Enum FileTypeEnum coded before 260914, commented in full on 261017.
 */
public enum FileTypeEnum {
    /**
     * A text file. C's {@code FTYPE_TEXT}, value 1, and the type nearly every caller passes: the data files, the
     * preference and character dump writers, the spoiler files.
     * <p>
     * Constant FTYPE_TEXT coded before 260914, commented in full on 261017.
     */
    FTYPE_TEXT,

    /**
     * A save file. C's {@code FTYPE_SAVE}, value 2, passed only by {@code savefile.c} when it writes a new savefile.
     * With {@code MODE_WRITE} it is the one type that changes how {@code file_open()} behaves: the open is exclusive
     * ({@code O_CREAT | O_EXCL}, owner read and write only) and fails if the file exists. With {@code MODE_APPEND} it is
     * an ordinary {@code "a+"} open, as for any type.
     * <p>
     * Constant FTYPE_SAVE coded before 260914, commented in full on 261017.
     */
    FTYPE_SAVE,

    /**
     * A raw binary file. C's {@code FTYPE_RAW}, value 3, passed by {@code score.c} for the high-score file and its lock
     * file. It opens as a plain write.
     * <p>
     * Constant FTYPE_RAW coded before 260914, commented in full on 261017.
     */
    FTYPE_RAW,

    /**
     * An HTML file. C's {@code FTYPE_HTML}, value 4. No caller in the 4.2.6 C source passes it; it is declared for
     * completeness and opens as a plain write.
     * <p>
     * Constant FTYPE_HTML coded before 260914, commented in full on 261017.
     */
    FTYPE_HTML,

    /**
     * A placeholder that sits after C's four types, in the place a {@code _MAX} would. C has no such constant, and
     * nothing in the port returns, passes or tests for it, so it behaves as nothing in particular. In particular
     * it is not what a failed open produces: C returns a {@code NULL} {@code ang_file}, and {@code FileHandler.open}
     * returns {@code false}.
     * <p>
     * Constant FTYPE_NULL coded before 260914, commented in full on 261017.
     */
    FTYPE_NULL
}
