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

package uk.co.jackoftrades.middle.player.enums;

/**
 * The kinds of name the random name generator can make — the port of C's {@code randname_type}
 * ({@code randname.h}). Each constant is also a section of the name file, which is why the same
 * enum names the generator's input and indexes the loaded word lists.
 *
 * <p>C numbers the constants from one rather than zero, so the {@code section} number carried in
 * {@code names.txt} is the enum value itself. The constants are kept in C's order and that
 * numbering is left to {@link #fromIndex} rather than being restated as a field, so an ordinal
 * here is always one less than the value the C source and the data file use.
 *
 * <p>{@code RANDNAME_NUM_TYPES} is C's end-of-type marker and is not a name type. C leans on its
 * value twice — as the exclusive upper bound of a valid section, and as the width of the
 * {@code name_sections} array ({@code init.c}) — so it is carried here rather than dropped, and
 * {@link #values()}{@code .length} stands in for the second of those uses.
 *
 * <p>Enum RandnameType coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
public enum RandnameType {
    /**
     * Tolkienesque names, the section the player's birth name is drawn from; C's value 1.
     */
    RANDNAME_TOLKIEN,
    /**
     * The nonsense syllables scroll titles are built from; C's value 2.
     */
    RANDNAME_SCROLL,
    /**
     * End-of-type marker, not a valid name type; C's value 3.
     */
    RANDNAME_NUM_TYPES;

    /**
     * Maps a C-side name type value — equivalently, a {@code section} number from the name file —
     * onto its constant. One gives {@link #RANDNAME_TOLKIEN} and two {@link #RANDNAME_SCROLL},
     * matching the numbering in {@code randname.h}; the offset is the one place the enum's
     * zero-based ordinals meet C's one-based values.
     *
     * <p>The upper guard is {@link #values()}{@code .length} because the array carries
     * {@link #RANDNAME_NUM_TYPES} as its last element, which puts its length exactly one past the
     * last real type. The marker is therefore rejected along with anything out of range, and the
     * guard stays correct without amendment if a further section is ever added.
     *
     * <p>An unusable value returns {@code null} rather than throwing, leaving the caller to decide
     * what an out-of-range section means; {@code MiscRegistry.setNames} treats it as a bad data
     * file. That is a shade stricter than C at one value only: {@code parse_names_section}
     * ({@code init.c}) rejects a section at or above the marker but accepts zero, filing those
     * words in the {@code name_sections[0]} slot that nothing ever reads. Section zero is out of
     * range here instead. The shipped {@code names.txt} uses only sections one and two, so the
     * two versions load the same data.
     *
     * <p>Method fromIndex coded on 260831, commented in full on 260831.
     *
     * @param index the C name type value, equivalently the name file's section number
     * @return the matching constant, or {@code null} if {@code index} names no usable type
     */
    public static RandnameType fromIndex(int index) {
        if (index < 1 || index >= values().length) {
            return null;
        }
        return values()[index - 1];
    }
}
