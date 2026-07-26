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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
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
 * The always-installed default {@link GameInput}: every hook aborts (returns {@link Optional#empty()}).
 * It is the port of C's {@code else} branches in {@code game-input.c}, where a missing UI hook simply
 * fails the request. Installed by {@link GameInputHolder} from the start so callers never see a
 * {@code null} seam, it lets the middle layer's non-UI logic run (and compile) before any real
 * front-end exists; a front-end or a test replaces it via {@link GameInputHolder#setInstance}.
 *
 * <p>Each method's parameter and return contract is documented on {@link GameInput}; here every one
 * reproduces C's absent-hook fall-back. Most decline - a value getter returns {@link Optional#empty()}
 * and {@link #getCheck} answers "no" - but the fall-backs are not uniform: {@link #panelContains} and
 * {@link #mapIsVisible} return {@code true}, and {@link #confirmDebug}, whose C fall-back does real
 * work this stub cannot yet reproduce, throws rather than lie.
 *
 * @author Rowan Crowther
 */
public class DefaultGameInput implements GameInput {
    private static final Logger logger = LogManager.getLogger();

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<DirectionEnum> getRepDir(boolean allow5) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<DirectionEnum> getAimDir() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<ItemObject> getItem(String prompt, String errorMessage, CommandCode code, Predicate<ItemObject> filter, Flag<GetItemFlags> mode) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<Integer> getQuantity(String prompt, int max) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<String> getString(String prompt, String userString) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<MagicSpell> getSpellFromBook(Player player, String verb, ItemObject magicBook, String errorMessage, BiPredicate<Player, MagicSpell> spellFilter) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<SpellSelection> getSpell(Player player, String verb, Predicate<ItemObject> bookFilter, CommandCode commandCode, String bookErrorMessage, BiPredicate<Player, MagicSpell> spellFilter, String spellError, ItemObject magicBook) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default always aborts.
     */
    @Override
    public Optional<Integer> getEffectFromList(String prompt, List<Effect> effects, int count, boolean allowRandom) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default declines - it answers "no" (C's absent-hook fall-back).
     */
    @Override
    public boolean getCheck(String prompt) {
        return false;
    }

    /**
     * {@inheritDoc} This default aborts - no keypress is offered.
     */
    @Override
    public Optional<Character> getCom(String prompt) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default aborts - no curse is chosen.
     */
    @Override
    public Optional<Integer> getCurse(ItemObject object, String diceString) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default reports no panel bounds (C's no-op leaves them unset).
     */
    @Override
    public Optional<GameInput.Rectangle> getPanel() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc} This default returns {@code true}, faithfully mirroring C's absent-hook
     * fall-back: with no UI to consult, every grid is treated as on the panel.
     */
    @Override
    public boolean panelContains(Loc location) {
        return true;
    }

    /**
     * {@inheritDoc} This default returns {@code true}, faithfully mirroring C's absent-hook
     * fall-back: with no UI to consult, the map is treated as visible.
     */
    @Override
    public boolean mapIsVisible() {
        return true;
    }

    /**
     * {@inheritDoc} This default shows nothing - the menu is a no-op, as in C when no UI is installed.
     */
    @Override
    public void viewAbilityMenu(List<PlayerAbility> abilityList) {

    }

    /**
     * {@inheritDoc} This default cannot faithfully stand in: C's fall-back warns the player and then
     * defers to {@code get_check}, which needs the message-output seam and {@link #getCheck}. Rather
     * than silently return a plausible-but-wrong answer, it logs and throws so the gap is loud.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean confirmDebug() {
        String message = "confirm_debug fallback needs getCheck + the message-output seam";
        UnsupportedOperationException uoe = new UnsupportedOperationException(message);
        logger.warn(message, uoe);
        throw uoe;
    }
}
