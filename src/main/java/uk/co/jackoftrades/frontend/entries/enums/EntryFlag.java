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

package uk.co.jackoftrades.frontend.entries.enums;

/**
 * Behavioural flags for a UI entry definition, mirroring the {@code ENTRY_FLAG_*}
 * constants of the C original's UI-entry system.
 *
 * @author Rowan Crowther
 */
public enum EntryFlag {
    /**
     * Treat the entry's timed value as auxiliary (secondary) data. @author Rowan Crowther
     */
    ENTRY_FLAG_TIMED_AS_AUX,
    /**
     * Entry is a template only and is not itself displayed. @author Rowan Crowther
     */
    ENTRY_FLAG_TEMPLATE_ONLY
}
