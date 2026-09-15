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

/**
 * Holds the information that can be read back from a {@link SavefileGetterImpl}
 * for one savefile found while enumerating the savefile directory.
 * <p>
 * This is the Java port of {@code struct savefile_details} from the original C
 * source ({@code src/ui-game.h}). The C struct is a plain data holder with no
 * behaviour of its own &mdash; it is populated field-by-field by {@code
 * got_savefile()} in {@code src/ui-game.c} &mdash; so this class mirrors that
 * shape as a plain JavaBean rather than porting any control flow. Where the C
 * struct leaves its fields zero-initialised (via {@code mem_zalloc}) until
 * populated, this class starts {@link #fileName} and {@link #description} as
 * empty strings instead of {@code null}, since Java code at the boundary should
 * not need to null-check a details object it has already been handed.
 * <p>
 * Class SavefileDetails coded before 260915, commented in full on 260915.
 *
 * @author Rowan Crowther
 */
public class SavefileDetails {
    /**
     * The file name component of the savefile's path, without any directory
     * prefix. Corresponds to {@code fnam} in {@code struct savefile_details}.
     * <p>
     * Field fileName coded before 260915, commented in full on 260915.
     */
    private String fileName;
    /**
     * The human-readable description of the savefile, as produced by the C
     * original's {@code savefile_get_description()}. Corresponds to {@code
     * desc} in {@code struct savefile_details}.
     * <p>
     * Field description coded before 260915, commented in full on 260915.
     */
    private String description;
    /**
     * The offset into {@link #fileName} at which the player-specific prefix
     * ends, so callers can skip past it when displaying the name. Corresponds
     * to {@code foff} in {@code struct savefile_details}, which is a {@code
     * size_t} in C; this class narrows it to {@code int} since the prefix is
     * always a short string length and never negative.
     * <p>
     * Field offset coded before 260915, commented in full on 260915.
     */
    private int offset;

    /**
     * Creates an empty details holder, with {@link #fileName} and {@link
     * #description} set to the empty string and {@link #offset} set to zero.
     * The C struct has no equivalent constructor &mdash; it relies on {@code
     * got_savefile()} to zero-allocate and then populate it &mdash; so this
     * constructor exists only to give the Java port a well-defined starting
     * state before its setters are called.
     * <p>
     * Function SavefileDetails coded before 260915, commented in full on
     * 260915.
     */
    public SavefileDetails() {
        this.fileName = "";
        this.description = "";
        this.offset = 0;
    }

    /**
     * Returns the file name component of the savefile's path.
     * <p>
     * Function getFileName coded before 260915, commented in full on 260915.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name component of the savefile's path.
     * <p>
     * Function setFileName coded before 260915, commented in full on 260915.
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the human-readable description of the savefile.
     * <p>
     * Function getDescription coded before 260915, commented in full on
     * 260915.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the human-readable description of the savefile.
     * <p>
     * Function setDescription coded before 260915, commented in full on
     * 260915.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the offset into {@link #fileName} at which the player-specific
     * prefix ends.
     * <p>
     * Function getOffset coded before 260915, commented in full on 260915.
     * @return the prefix offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset into {@link #fileName} at which the player-specific
     * prefix ends.
     * <p>
     * Function setOffset coded before 260915, commented in full on 260915.
     * @param offset the prefix offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
