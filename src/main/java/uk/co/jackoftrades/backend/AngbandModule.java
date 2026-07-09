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

package uk.co.jackoftrades.backend;

/**
 * Common lifecycle contract for the game's pluggable subsystems (quark tables,
 * data stores, etc.). It mirrors the C original's pattern of pairing an
 * {@code init_*}/{@code cleanup_*} function with each module so they can be
 * brought up and torn down uniformly during game start-up and shutdown.
 *
 * @author Rowan Crowther
 */
public interface AngbandModule {
    /**
     * @return the human-readable name of this module
     * @author Rowan Crowther
     */
    String getName();

    /**
     * Allocate and prepare this module's state, making it ready for use.
     *
     * @author Rowan Crowther
     */
    void init();

    /**
     * Release this module's resources so it can be safely discarded or re-initialised.
     *
     * @author Rowan Crowther
     */
    void cleanup();
}