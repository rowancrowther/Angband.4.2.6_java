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

package uk.co.jackoftrades.frontend.events;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.events.enums.UiEventType;

/**
 * Marker/base type for UI events, carrying the set of {@link UiEventType}s an
 * event may represent. The Java port of the C original's {@code ui_event}
 * abstraction ({@code src/ui-event.h}); the shared {@link Flag} captures which
 * event categories are in play.
 *
 * @author ClaudeCode
 */
public interface Event {
    /**
     * The set of event categories this event belongs to.
     *
     * @author ClaudeCode
     */
    Flag<UiEventType> uiEventType = new Flag<>(UiEventType.class);
}
