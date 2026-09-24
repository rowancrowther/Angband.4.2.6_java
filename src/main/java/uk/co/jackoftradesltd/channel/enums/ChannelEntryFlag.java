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

package uk.co.jackoftradesltd.channel.enums;

/**
 * The per-entry configuration flags a UI entry's {@code flags} field can carry — the Java form of
 * C's anonymous {@code entry_flag} enum ({@code ui-entry.c:76-81}: {@code ENTRY_FLAG_TIMED_AUX = 1},
 * {@code ENTRY_FLAG_TEMPLATE_ONLY = (1UL << 20)}) and the {@code entry_flags[]} table
 * ({@code ui-entry.c:86-88}) that pairs the one settable flag's data-file string with its bit.
 *
 * <p>Held as a {@link uk.co.jackoftradesltd.channel.utils.Flag} of this enum rather than as raw
 * bits, so — unlike C, which packs {@link #ENTRY_FLAG_TIMED_AS_AUX} at bit 0 and
 * {@link #ENTRY_FLAG_TEMPLATE_ONLY} at bit 20 purely to keep the two from colliding — membership is
 * what every caller tests, and neither constant's ordinal carries any meaning.
 *
 * <p>Class ChannelEntryFlag coded before 260919, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
public enum ChannelEntryFlag {
    /**
     * Marks an entry as existing only to be copied from via a {@code template:} directive, never to
     * be displayed or computed on its own — the Java form of C's {@code ENTRY_FLAG_TEMPLATE_ONLY}
     * ({@code ui-entry.c:80}). No {@code flags:} directive can set it: C's own comment calls it
     * "used internally; not set from within the configuration files". Instead, {@code run_parse_ui_entry}
     * ({@code ui-entry.c:1988-2006}) stamps every entry loaded from {@code ui_entry_base.txt} with it
     * before {@code ui_entry.txt} is parsed, and {@code parse_entry_template}
     * ({@code ui-entry.c:1965-2006}) clears it again on the real entry a {@code template:} directive
     * builds from one of those bases — {@code tentry->flags & ~ENTRY_FLAG_TEMPLATE_ONLY}. The port
     * keeps the same pairing: {@code UIEntryBaseAssembler} sets it on every base it assembles, and
     * {@code UIEntryAssembler} clears it on a real entry that copies a base's flags, which is what
     * lets {@code UIEntryCode} skip templates when it walks the registry for display.
     *
     * <p>Constant ENTRY_FLAG_TEMPLATE_ONLY coded before 260919, commented in full on 260924.
     */
    ENTRY_FLAG_TEMPLATE_ONLY,

    /**
     * Treats the entry's timed value as an auxiliary (secondary) figure rather than its primary
     * one — the Java form of C's {@code ENTRY_FLAG_TIMED_AUX}, bound to the data-file string
     * {@code "TIMED_AS_AUX"} in C's {@code entry_flags[]} table ({@code ui-entry.c:87}) and switched
     * on by a record's own {@code flags:} directive naming it. This constant is named after that
     * data-file string rather than after C's internal identifier, which is why the two spellings
     * differ.
     *
     * <p>Constant ENTRY_FLAG_TIMED_AS_AUX coded before 260916, commented in full on 260924.
     */
    ENTRY_FLAG_TIMED_AS_AUX
}
