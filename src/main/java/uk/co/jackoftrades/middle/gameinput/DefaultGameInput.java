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

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.magic.MagicSpell;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.GetItemFlags;
import uk.co.jackoftrades.middle.player.Player;

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
 * behaves identically - it declines, as if the player had aborted.
 *
 * @author Rowan Crowther
 */
public class DefaultGameInput implements GameInput {

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
}
