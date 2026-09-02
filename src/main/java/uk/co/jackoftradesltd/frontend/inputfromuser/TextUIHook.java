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

package uk.co.jackoftradesltd.frontend.inputfromuser;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.GetItemFlags;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * The boundary through which the game engine asks the user interface for an interactive value - the
 * port of C's {@code get_*_hook} indirection in {@code game-input.c}. Each method stands in for one
 * C UI hook that the middle layer calls without knowing which front-end (textui, test harness, …)
 * answers it. Every accessor returns an {@link Optional}: present is the chosen value, empty is the
 * player aborting (C's {@code false}/{@code CMD_ARG_ABORTED}).
 *
 * <p>The methods are all stubs for now - they log the call and abort - so the middle layer compiles
 * and its non-UI logic can be exercised; the real front-end fills them in later.
 *
 * @author Rowan Crowther
 */
public class TextUIHook {
    /**
     * Asks the UI for a repeated-movement direction - the port of C's {@code get_rep_dir}. Backs
     * {@link uk.co.jackoftradesltd.middle.game.gameengine.Command#getDirection}.
     *
     * @param allow5 whether the "5"/self answer is permitted (C's {@code allow_none})
     * @return the chosen direction, or empty if the player aborted
     */
    public static Optional<DirectionEnum> getRepDir(boolean allow5) {
        // TODO Stub Function
        System.out.println("Stub function getRepDir called. Allow5: " + (Boolean.toString(allow5)));

        return Optional.empty();
    }

    /**
     * Asks the UI to select an item - the port of C's {@code get_item}. Backs {@link
     * uk.co.jackoftradesltd.middle.game.gameengine.Command#getItem}. C returned the chosen object
     * through a {@code struct object **} out-parameter; here it is the {@link Optional} return.
     *
     * @param prompt the selection prompt
     * @param string the message shown when nothing eligible exists (C's {@code str})
     * @param code   the command being resolved, which the picker may use for context
     * @param filter the predicate an item must satisfy, or {@code null} to accept any
     * @param mode   the permitted source locations (equipment/inventory/quiver/floor)
     * @return the chosen item, or empty if the player aborted
     */
    public static Optional<ItemObject> getItem(String prompt, String string,
                                               CommandCode code, Predicate<ItemObject> filter, Flag<GetItemFlags> mode) {
        // TODO Stub Function
        System.out.println("Stub function getItem called. Prompt: " + prompt + ", string: " + string);

        return Optional.empty();
    }

    /**
     * Asks the UI for an aim direction/target - the port of C's {@code get_aim_dir}. Backs {@link
     * uk.co.jackoftradesltd.middle.game.gameengine.Command#getTarget}.
     *
     * @return the aimed target, or empty if the player aborted
     */
    public static Optional<DirectionEnum> getAimDir() {
        // TODO Stub Function
        System.out.println("Stub function getAimDir called.");

        return Optional.empty();
    }

    /**
     * Asks the UI for a quantity in {@code [1, max]} - the port of C's {@code get_quantity}. Backs
     * {@link uk.co.jackoftradesltd.middle.game.gameengine.Command#getQuantity}. C signalled cancel by
     * returning {@code 0}; here that is an empty {@link Optional}.
     *
     * @param prompt the prompt to show, or {@code null} for the default (C's {@code NULL})
     * @param max    the largest quantity that may be entered
     * @return the chosen quantity, or empty if the player aborted
     */
    public static Optional<Integer> getQuantity(String prompt, int max) {
        // TODO Stub Function
        return Optional.empty();
    }

    /**
     * Prompts for a direction, seeded with {@code initial} - the port of C's {@code get_string}
     * flow specialised to a direction answer.
     *
     * @param prompt  the prompt label
     * @param initial the direction the field starts on
     * @return the chosen direction, or empty if the player aborted
     */
    public static Optional<DirectionEnum> getDirection(String prompt, DirectionEnum initial) {
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

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
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

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
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

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
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        // TODO: Flesh out this stub method

        return Optional.empty();
    }
}
