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

package uk.co.jackoftrades.frontend.events.enums;

/**
 * The kinds of UI event the front end can deliver to the game, mirroring the
 * {@code EVT_*} event types of the C original's user-interface event system
 * ({@code src/ui-event.h}).
 *
 * @author ClaudeCode
 */
public enum UiEventType {
    /**
     * No event / empty event. @author ClaudeCode
     */
    EVT_NONE,
    /** A keyboard key press. @author ClaudeCode */
    EVT_KBRD,
    /** A mouse action (click/move). @author ClaudeCode */
    EVT_MOUSE,
    /** The display was resized. @author ClaudeCode */
    EVT_RESIZE,
    /** An on-screen button was activated. @author ClaudeCode */
    EVT_BUTTON,
    /** The user pressed escape / cancelled. @author ClaudeCode */
    EVT_ESCAPE,
    /** A movement command. @author ClaudeCode */
    EVT_MOVE,
    /** A menu/list selection. @author ClaudeCode */
    EVT_SELECT,
    /** A switch between contexts/panels. @author ClaudeCode */
    EVT_SWITCH
}