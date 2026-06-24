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

package uk.co.jackoftrades.middle.game.event;

import java.util.ArrayList;

/**
 * {@link GameEventData} payload describing one stage of character creation
 * ("birth") — a hint plus the list of choices (with help text) the player must
 * pick from, the default selection, and any extra stage-specific data. Drives the
 * birth UI.
 *
 * @author ClaudeCode
 */
public class EventDataBirthStage implements GameEventData {
    /**
     * Whether this stage resets earlier choices.
     *
     * @author ClaudeCode
     */
    private boolean reset;
    /**
     * A hint prompting the player for this stage's decision.
     *
     * @author ClaudeCode
     */
    private final String hint;
    /**
     * The number of choices available at this stage.
     *
     * @author ClaudeCode
     */
    private int nChoices;
    /**
     * The index of the initially selected choice.
     *
     * @author ClaudeCode
     */
    private int initialChoice;
    /**
     * The selectable choices for this stage.
     *
     * @author ClaudeCode
     */
    private ArrayList<String> choices;
    /**
     * Help text for each choice (parallel to {@link #choices}).
     *
     * @author ClaudeCode
     */
    private ArrayList<String> helpTexts;
    /**
     * Extra stage-specific data, if any.
     *
     * @author ClaudeCode
     */
    private Object xtra;

    /**
     * Build a birth-stage payload.
     *
     * @param reset         whether this stage resets earlier choices
     * @param hint          the prompt hint
     * @param nChoices      number of choices
     * @param initialChoice default-selected choice index
     * @param choices       the choices
     * @param helpTexts     per-choice help text
     * @param xtra          extra stage-specific data
     * @author ClaudeCode
     */
    public EventDataBirthStage(boolean reset,
                               String hint,
                               int nChoices,
                               int initialChoice,
                               ArrayList<String> choices,
                               ArrayList<String> helpTexts,
                               Object xtra) {
        this.reset = reset;
        this.hint = hint;
        this.nChoices = nChoices;
        this.initialChoice = initialChoice;
        this.choices = choices;
        this.helpTexts = helpTexts;
        this.xtra = xtra;
    }

    /**
     * @return whether this stage resets earlier choices
     * @author ClaudeCode
     */
    public boolean isReset() {
        return reset;
    }

    /**
     * @return the prompt hint
     * @author ClaudeCode
     */
    public String getHint() {
        return hint;
    }

    /**
     * @return the number of choices
     * @author ClaudeCode
     */
    public int getnChoices() {
        return nChoices;
    }

    /**
     * @return the default-selected choice index
     * @author ClaudeCode
     */
    public int getInitialChoice() {
        return initialChoice;
    }

    /**
     * @return the selectable choices
     * @author ClaudeCode
     */
    public ArrayList<String> getChoices() {
        return choices;
    }

    /**
     * @return the per-choice help text
     * @author ClaudeCode
     */
    public ArrayList<String> getHelpTexts() {
        return helpTexts;
    }

    /**
     * @return the extra stage-specific data
     * @author ClaudeCode
     */
    public Object getXtra() {
        return xtra;
    }
}