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

package uk.co.jackoftradesltd.middle.gameinput;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.GetItemFlags;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * The always-installed default {@link GameInput}: every hook aborts (returns {@link Optional#empty()}).
 * It is the port of C's {@code else} branches in {@code game-input.c}, where a missing UI hook simply
 * fails the request. Installed by {@link GameInputHolder} from the start so callers never see a
 * {@code null} boundary, it lets the middle layer's non-UI logic run (and compile) before any real
 * front-end exists; a front-end or a test replaces it via {@link GameInputHolder#setInstance}.
 *
 * <p>Each method's parameter and return contract is documented on {@link GameInput}; here every one
 * reproduces C's absent-hook fall-back. Most decline - a value getter returns {@link Optional#empty()}
 * and {@link #getCheck} answers "no" - but the fall-backs are not uniform: {@link #panelContains} and
 * {@link #mapIsVisible} return {@code true}, and {@link #confirmDebug} is not overridden here at all -
 * it inherits {@link GameInput}'s generic fall-back (mention the effects, flush, then defer to
 * {@link #getCheck}), which given this stub's "no" answer declines.
 *
 * <p>In C each wrapper in {@code game-input.c} tests its own file-scope hook pointer
 * ({@code get_string_hook}, {@code get_check_hook}, …), all of which start {@code NULL} until
 * {@code textui_input_init()} installs the real ones. This class is every wrapper with its hook
 * left {@code NULL}: the {@code false}, {@code 0} and {@code -1} returns become
 * {@link Optional#empty()}, the out-parameter writes that never happen become an empty result, and
 * the {@code void} hooks do nothing. It holds no state, so one instance can be shared freely.
 *
 * <p>Class DefaultGameInput coded before 261012, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class DefaultGameInput implements GameInput {
    /**
     * Stands in for C's {@code get_rep_dir} with {@code get_rep_dir_hook} unset, where the wrapper
     * returns {@code false} and never writes {@code *dir}. The port reports that as empty: no
     * direction is chosen, whatever {@code allow5} (C's {@code allow_none}) says.
     *
     * <p>Function getRepDir coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<DirectionEnum> getRepDir(boolean allow5) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_aim_dir} with {@code get_aim_dir_hook} unset, where the wrapper
     * returns {@code false} and never writes {@code *dir}. The port reports that as empty: no aim
     * direction or target is chosen.
     *
     * <p>Function getAimDir coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<DirectionEnum> getAimDir() {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_item} with {@code get_item_hook} unset, where the wrapper returns
     * {@code false} and leaves {@code *choice} untouched. The port reports that as empty. Unlike the
     * real picker, which shows {@code errorMessage} when nothing is eligible, this default displays
     * nothing, as the C wrapper does; the arguments are all ignored.
     *
     * <p>Function getItem coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<ItemObject> getItem(String prompt, String errorMessage, CommandCode code, Predicate<ItemObject> filter, Flag<GetItemFlags> mode) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_quantity} with {@code get_quantity_hook} unset, where the wrapper
     * returns {@code 0}. Since a real answer is always in {@code [1, max]}, C's {@code 0} means
     * "cancelled", and the port reports it as empty whatever {@code max} is.
     *
     * <p>Function getQuantity coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<Integer> getQuantity(String prompt, int max) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_string} with {@code get_string_hook} unset, where the wrapper
     * returns {@code false} and leaves the caller's buffer as it was. The port reports that as
     * empty - not as {@code userString} handed back, and not as an empty string, which a real UI
     * would treat as an accepted answer.
     *
     * <p>Function getString coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<String> getString(String prompt, String userString) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_spell_from_book} with {@code get_spell_from_book_hook} unset,
     * where the wrapper returns {@code -1}. The port reports that negative "selection failed" value
     * as empty; the player, book, filter and error message are all ignored.
     *
     * <p>Function getSpellFromBook coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<MagicSpell> getSpellFromBook(Player player, String verb, ItemObject magicBook, String errorMessage, BiPredicate<Player, MagicSpell> spellFilter) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_spell} with {@code get_spell_hook} unset, where the wrapper
     * returns {@code -1} and never writes {@code *rtn_book}. The port reports that as empty, so
     * there is no half-answer: neither a book nor a spell comes back, and the {@code magicBook}
     * the caller offered is not echoed.
     *
     * <p>Function getSpell coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<SpellSelection> getSpell(Player player, String verb, Predicate<ItemObject> bookFilter, CommandCode commandCode, String bookErrorMessage, BiPredicate<Player, MagicSpell> spellFilter, String spellError, ItemObject magicBook) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_effect_from_list} with {@code get_effect_from_list_hook} unset.
     * This is the one value hook with a fall-back that is not a plain abort: C returns
     * {@code allow_random ? -2 : -1}, so the port returns {@link EffectChoice.Random} when
     * {@code allowRandom} is set and {@link EffectChoice.Aborted} otherwise. It never returns an
     * {@link EffectChoice.Index}, and {@code prompt}, {@code effects} and {@code count} are ignored.
     *
     * <p>Function getEffectFromList coded before 261009, commented in full on 261014.
     */
    @Override
    public EffectChoice getEffectFromList(String prompt, List<Effect> effects, int count, boolean allowRandom) {
        return allowRandom ? new EffectChoice.Random()
                : new EffectChoice.Aborted();
    }

    /**
     * Stands in for C's {@code get_check} with {@code get_check_hook} unset, where the wrapper
     * returns {@code false}: the prompt is not shown and the answer is "no". Because
     * {@link GameInput#confirmDebug} defers to this method, the debug commands are also refused
     * under this default.
     *
     * <p>Function getCheck coded before 261012, commented in full on 261014.
     */
    @Override
    public boolean getCheck(String prompt) {
        return false;
    }

    /**
     * Stands in for C's {@code get_com} with {@code get_com_hook} unset, where the wrapper returns
     * {@code false} and never writes {@code *command}. The port reports that as empty: no keypress
     * is offered, which the caller treats as the player escaping.
     *
     * <p>Function getCom coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<Character> getCom(String prompt) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_curse} with {@code get_curse_hook} unset, where the wrapper
     * returns {@code false} and never writes {@code *choice}. The port reports that as empty, so no
     * curse index comes back; the object and dice string are ignored.
     *
     * <p>Function getCurse coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<Integer> getCurse(ItemObject object, String diceString) {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code get_panel} with {@code get_panel_hook} unset, where the {@code void}
     * wrapper does nothing and leaves the four coordinate out-parameters as the caller set them.
     * The port has no out-parameters to leave alone, so it reports empty - "no bounds available" -
     * and a caller must not read that as a zero-sized panel.
     *
     * <p>Function getPanel coded before 261012, commented in full on 261014.
     */
    @Override
    public Optional<GameInput.Rectangle> getPanel() {
        return Optional.empty();
    }

    /**
     * Stands in for C's {@code panel_contains} with {@code panel_contains_hook} unset, where the
     * wrapper returns {@code true}: with no UI to consult, every grid is treated as on the panel,
     * so non-UI logic does not cull anything. The location is ignored, and this is one of the two
     * hooks (with {@link #mapIsVisible}) whose fall-back is "yes" rather than a decline.
     *
     * <p>Function panelContains coded before 261012, commented in full on 261014.
     */
    @Override
    public boolean panelContains(Loc location) {
        return true;
    }

    /**
     * Stands in for C's {@code map_is_visible} with {@code map_is_visible_hook} unset, where the
     * wrapper returns {@code true}: with no UI to consult, the map is treated as visible.
     *
     * <p>Function mapIsVisible coded before 261012, commented in full on 261014.
     */
    @Override
    public boolean mapIsVisible() {
        return true;
    }

    /**
     * {@inheritDoc} This default does nothing, as C's {@code view_ability_menu} does when
     * {@code view_abilities_hook} is unset: the {@code void} wrapper has no else branch, so no menu
     * is shown and the list is neither read nor changed. There is no abort channel to report.
     *
     * <p>Function viewAbilityMenu coded before 261012, commented in full on 261014.
     */
    @Override
    public void viewAbilityMenu(List<PlayerProperty> propertyList) {
        
    }
}
