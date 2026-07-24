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

package uk.co.jackoftrades.middle.game;

import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.objects.ItemObject;

import java.util.Optional;

/**
 * The text front-end's interactive prompts - the value-editing counterparts of C's
 * {@code get_string}/{@code get_aim_dir} family, each seeded with an initial value the player may
 * accept or edit. Every prompt returns an {@link Optional}: present is the accepted value (an empty
 * string included), empty is the player aborting.
 *
 * <p>All four are stubs for now - they flush pending messages and abort - so the middle layer
 * compiles ahead of the real front-end. {@link #getString} is the one currently wired into {@link
 * uk.co.jackoftrades.middle.game.gameengine.Command#getString}.
 *
 * @author Rowan Crowther
 */
public class TextUI {
    /**
     * Prompts for a direction, seeded with {@code initial} - the port of C's {@code get_string}
     * flow specialised to a direction answer.
     *
     * @param prompt  the prompt label
     * @param initial the direction the field starts on
     * @return the chosen direction, or empty if the player aborted
     */
    public static Optional<DirectionEnum> getDirection(String prompt, DirectionEnum initial) {
        EventsHandler.eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        // TODO: Flesh out this stub method

        return Optional.empty();
    }

    /**
     * Prompts for an item, seeded with {@code initial}.
     *
     * @param prompt  the prompt label
     * @param initial the item the field starts on
     * @return the chosen item, or empty if the player aborted
     */
    public static Optional<ItemObject> getItem(String prompt, ItemObject initial) {
        EventsHandler.eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        // TODO: Flesh out this stub method

        return Optional.empty();
    }

    /**
     * Prompts for a line of text, seeded with {@code initial} - the port of C's {@code get_string}.
     * An accepted empty string is a valid result; only an abort yields empty.
     *
     * @param prompt  the prompt label
     * @param initial the text the input field is pre-filled with
     * @return the entered text, or empty if the player aborted
     */
    public static Optional<String> getString(String prompt, String initial) {
        EventsHandler.eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        // TODO: Flesh out this stub method

        return Optional.empty();
    }

    /**
     * Prompts for an aim target, seeded with {@code initial}.
     *
     * @param prompt  the prompt label
     * @param initial the target code the field starts on
     * @return the chosen target, or empty if the player aborted
     */
    public static Optional<Integer> getTarget(String prompt, int initial) {
        EventsHandler.eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        // TODO: Flesh out this stub method

        return Optional.empty();
    }
}
