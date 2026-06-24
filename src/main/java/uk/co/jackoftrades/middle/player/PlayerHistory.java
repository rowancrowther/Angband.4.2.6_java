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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The player's generated background history.
 *
 * <p>In the C original a character's biography is produced at birth by rolling through the
 * {@link PlayerHistoryChart} / {@link PlayerHistoryEntry} graph and is stored as the resulting
 * text; this type is the player-side holder for that outcome.
 *
 * <p><b>Status:</b> currently a stub — only the logger is in place while the history system is
 * being ported.
 *
 * @author ClaudeCode
 */
public class PlayerHistory {
    /**
     * Logger for this type.
     */
    private static final Logger logger = LogManager.getLogger();
}
