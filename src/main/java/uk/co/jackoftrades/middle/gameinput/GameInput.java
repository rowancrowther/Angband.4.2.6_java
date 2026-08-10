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

package uk.co.jackoftrades.middle.gameinput;

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.magic.MagicSpell;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.GetItemFlags;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerAbility;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * The seam through which the game engine asks the user interface for an interactive value - the
 * port of C's {@code get_*_hook} indirection in {@code game-input.c}, gathered into a single
 * interface. Each method stands in for one C UI dispatcher, so the middle layer can request a
 * direction, an item, a quantity, a spell, and so on without knowing which front-end (textui, a
 * test harness, …) answers. The live implementation is held by {@link GameInputHolder} and can be
 * swapped: a real front-end for play, a fake for tests.
 *
 * <p>Every accessor returns an {@link Optional}: present is the chosen value, empty is the player
 * aborting (C's {@code false}/{@code CMD_ARG_ABORTED}). Where C wrote its result through an
 * out-parameter, the port returns it instead; {@link #getSpell} additionally needs to return the
 * book the player chose, so it hands back a {@link SpellSelection} pair.
 *
 * @author Rowan Crowther
 */
public interface GameInput {

    /**
     * The book-and-spell pair returned by {@link #getSpell}. C reported the spell through an
     * {@code int *} and the book through a {@code struct object **}; the port pairs them so both
     * come back from one call - the caller stores the book and the spell's index together.
     *
     * @param book  the book the spell was chosen from
     * @param spell the chosen spell
     */
    record SpellSelection(ItemObject book, MagicSpell spell) {
    }

    /**
     * The bounds of the visible map area returned by {@link #getPanel}. C wrote four coordinates
     * through out-parameters - {@code min_y}/{@code min_x} and {@code max_y}/{@code max_x}; since map
     * rows grow downward and columns rightward, the minima are the top-left corner and the maxima the
     * bottom-right, which the port pairs as two {@link Loc}s.
     *
     * @param topLeft     the top-left corner (C's {@code min_y}/{@code min_x})
     * @param bottomRight the bottom-right corner (C's {@code max_y}/{@code max_x})
     */
    record Rectangle(Loc topLeft, Loc bottomRight) {
    }

    /**
     * Asks the player a yes/no question - the port of C's {@code get_check}. Unlike the value
     * getters there is no abort channel; declining (or no UI) is simply "no".
     *
     * @param prompt the question to put to the player, taking the form "Query? "
     * @return {@code true} if the player answered yes, {@code false} otherwise
     */
    boolean getCheck(String prompt);

    /**
     * Prompts the player for a single keypress - the port of C's {@code get_com}. C wrote the key
     * through a {@code char *} out-parameter and returned whether the player accepted; here the key
     * is the {@link Optional} return.
     *
     * @param prompt the prompt to show, taking the form "Command: "
     * @return the key pressed, or empty if the player escaped
     */
    Optional<Character> getCom(String prompt);

    /**
     * Asks the player to choose a curse to remove from an object - the port of C's {@code get_curse}.
     * The result is the index of the chosen curse within the object's curses.
     *
     * @param object     the cursed object whose curses are offered
     * @param diceString the spell-strength text shown in the prompt (input only - C threads it into
     *                   the menu header for display, it is not written back)
     * @return the chosen curse index, or empty if the player aborted
     */
    Optional<Integer> getCurse(ItemObject object, String diceString);

    /**
     * Reports the bounds of the visible map area (the "panel") - the port of C's {@code get_panel}.
     * C wrote four coordinates through out-parameters; the port returns them as a {@link Rectangle}.
     *
     * @return the panel bounds, or empty when none is available (C's no-op leaves them unset)
     */
    Optional<Rectangle> getPanel();

    /**
     * Tests whether a map grid lies within the visible panel - the port of C's {@code panel_contains}.
     * There is no abort channel; note the absent-hook fall-back is {@code true}, so non-UI logic
     * treats every grid as on-panel rather than culling it.
     *
     * @param location the grid to test
     * @return {@code true} if the grid is within the panel
     */
    boolean panelContains(Loc location);

    /**
     * Reports whether the map is currently shown - the port of C's {@code map_is_visible}. As with
     * {@link #panelContains}, the absent-hook fall-back is {@code true}.
     *
     * @return {@code true} if the map is currently visible
     */
    boolean mapIsVisible();

    /**
     * Displays the player's abilities - the port of C's {@code view_ability_menu}. This is a
     * display-only action with no result; when no UI is installed it does nothing.
     *
     * @param abilityList the abilities to show
     */
    void viewAbilityMenu(List<PlayerAbility> abilityList);

    /**
     * Asks the player to confirm enabling the dangerous debug commands - the port of C's
     * {@code confirm_debug}. Unlike the other hooks this one carries its own fall-back: C's absent-hook
     * path is not a simple decline but a generic procedure, so this {@code default} reproduces it -
     * warn about the effects, flush the messages, then defer to {@link #getCheck}. An implementation
     * overrides it only to supply a UI-specific confirmation.
     *
     * @return {@code true} if the player confirmed
     */
    default boolean confirmDebug() {
        Message.message("%s", "You are about to use the dangerous, unsupported, debug commands!");
        Message.message("%s", "Your machine may crash, and your savefile may become corrupted!");
        Message.message("%s", "Your experience beyond this point is not guaranteed in any way!");

        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        return getCheck("Are you sure you want to use the debug commands? ");
    }
    /**
     * Asks for a repeated-movement direction - the port of C's {@code get_rep_dir}.
     *
     * @param allow5 whether the "5"/self answer is permitted (C's {@code allow_none})
     * @return the chosen direction, or empty if the player aborted
     */
    Optional<DirectionEnum> getRepDir(boolean allow5);

    /**
     * Asks for an aim direction or target - the port of C's {@code get_aim_dir}.
     *
     * @return the aimed direction, or empty if the player aborted
     */
    Optional<DirectionEnum> getAimDir();

    /**
     * Asks the player to select an item - the port of C's {@code get_item}. C returned the choice
     * through a {@code struct object **} out-parameter; here it is the {@link Optional} return.
     *
     * @param prompt       the selection prompt
     * @param errorMessage the message shown when nothing eligible exists (C's {@code str})
     * @param code         the command being resolved, which the picker may use for context
     * @param filter       the predicate an item must satisfy, or {@code null} to accept any
     * @param mode         the permitted source locations (equipment/inventory/quiver/floor)
     * @return the chosen item, or empty if the player aborted
     */
    Optional<ItemObject> getItem(String prompt, String errorMessage, CommandCode code, Predicate<ItemObject> filter,
                                 Flag<GetItemFlags> mode);

    /**
     * Asks for a quantity in {@code [1, max]} - the port of C's {@code get_quantity}. C signalled
     * cancel by returning {@code 0}; here that is an empty {@link Optional}.
     *
     * @param prompt the prompt to show, or {@code null} for the default (C's {@code NULL})
     * @param max    the largest quantity that may be entered
     * @return the chosen quantity, or empty if the player aborted
     */
    Optional<Integer> getQuantity(String prompt, int max);

    /**
     * Asks for a line of text, pre-filled with {@code userString} - the port of C's
     * {@code get_string}. An accepted empty string is a valid result; only an abort yields empty.
     *
     * @param prompt     the prompt label
     * @param userString the text the input field starts on (C's in/out buffer)
     * @return the entered text, or empty if the player aborted
     */
    Optional<String> getString(String prompt, String userString);

    /**
     * Asks the player to choose a spell from one known book - the port of C's
     * {@code get_spell_from_book}. The book is fixed; only the spell is chosen.
     *
     * @param player       the caster
     * @param verb         the action the spell is wanted for (e.g. "cast", "study")
     * @param magicBook    the book to choose from
     * @param errorMessage the message shown when the book offers no eligible spell
     * @param spellFilter  the predicate a spell must satisfy, tested against the player and spell
     * @return the chosen spell, or empty if the player aborted
     */
    Optional<MagicSpell> getSpellFromBook(Player player, String verb, ItemObject magicBook, String errorMessage,
                                          BiPredicate<Player, MagicSpell> spellFilter);

    /**
     * Asks the player to choose a book and then a spell from it - the port of C's {@code get_spell}.
     * Both halves come back in the {@link SpellSelection}, since C returned the spell through its
     * return value and the book through a {@code struct object **} out-parameter.
     *
     * @param player           the caster
     * @param verb             the action the spell is wanted for (e.g. "cast", "study")
     * @param bookFilter       the predicate a book must satisfy to be eligible
     * @param commandCode      the command being resolved, for the picker's context
     * @param bookErrorMessage the message shown when no eligible book exists
     * @param spellFilter      the predicate a spell must satisfy, tested against the player and spell
     * @param spellError       the message shown when no eligible spell exists
     * @param magicBook        a book to start the picker on, or {@code null} for none
     * @return the chosen book and spell, or empty if the player aborted
     */
    Optional<SpellSelection> getSpell(Player player, String verb, Predicate<ItemObject> bookFilter,
                                      CommandCode commandCode, String bookErrorMessage,
                                      BiPredicate<Player, MagicSpell> spellFilter, String spellError,
                                      ItemObject magicBook);

    /**
     * Asks the player to choose one effect from a list - the port of C's
     * {@code get_effect_from_list}. C returned an {@code int}: a list index, {@code -2} for the
     * random option, or {@code -1} for an abort; the port returns those three outcomes as the
     * {@link EffectChoice} cases {@link EffectChoice.Index}, {@link EffectChoice.Random} and
     * {@link EffectChoice.Aborted} respectively.
     *
     * @param prompt      the prompt to show, or {@code null} for the default
     * @param effects     the effects to choose among
     * @param count       how many of the effects to offer, or {@code -1} for all of them
     * @param allowRandom whether to offer an extra "choose at random" option
     * @return the chosen {@link EffectChoice} - an {@link EffectChoice.Index}, or
     *         {@link EffectChoice.Random} if the player took the random option, or
     *         {@link EffectChoice.Aborted} if they cancelled
     */
    EffectChoice getEffectFromList(String prompt, List<Effect> effects, int count, boolean allowRandom);
}
