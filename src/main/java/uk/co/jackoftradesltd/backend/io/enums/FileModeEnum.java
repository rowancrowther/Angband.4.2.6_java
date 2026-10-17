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
 * The kind of access required to a file, as passed to {@code file_open()} and recorded in the
 * {@code mode} field of C's {@code ang_file}. This is C's {@code file_mode} enum from {@code
 * z-file.h}, with the same three constants in the same order ({@code MODE_WRITE}, {@code
 * MODE_READ}, {@code MODE_APPEND}); no {@code MODE_NONE} or {@code MODE_MAX} is declared, as C has
 * none either.
 * <p>
 * The mode alone does not say how a file is opened: with {@link #MODE_WRITE} the {@link
 * FileTypeEnum} also matters ({@code FTYPE_SAVE} refuses to overwrite). {@code file_open()} runs
 * its open hook for every mode except {@link #MODE_READ}. The port's {@code FileHandler.open}
 * switches on this enum, and {@code AngFile} keeps one as its fixed access mode.
 * <p>
 * Enum FileModeEnum coded before 260914, commented in full on 261017.
 */
public enum FileModeEnum {
    /**
     * Write access required. C opens with {@code fopen(path, "wb")}: a missing file is created and
     * an existing one is truncated. Combined with {@code FTYPE_SAVE}, C instead opens exclusively
     * ({@code O_CREAT | O_EXCL}) and fails if the file already exists.
     * <p>
     * Constant MODE_WRITE coded before 260914, commented in full on 261017.
     */
    MODE_WRITE,

    /**
     * Read access required. C opens with {@code fopen(path, "rb")}: a missing file fails, and
     * nothing is created or truncated. This is the only mode that skips C's open hook.
     * <p>
     * Constant MODE_READ coded before 260914, commented in full on 261017.
     */
    MODE_READ,

    /**
     * Append access required. C opens with {@code fopen(path, "a+")}: a missing file is created, an
     * existing one keeps its contents, and writes go to the end.
     * <p>
     * Constant MODE_APPEND coded before 260914, commented in full on 261017.
     */
    MODE_APPEND
}
