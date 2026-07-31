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

package uk.co.jackoftrades.backend.utils.combiners;

import uk.co.jackoftrades.backend.utils.Combiner;
import uk.co.jackoftrades.backend.utils.UIEntryCombinerState;

import java.util.List;

public class AddCombiner implements Combiner, Cloneable {
    private UIEntryCombinerState state;

    /**
     * @param v
     * @param a
     * @return
     */
    @Override
    public void init(int v, int a) {
        state = new UIEntryCombinerState();
        state.setNegAccum(0);
        state.setNegAccumAux(0);
        state.setAccum(v);
        state.setAccumAux(a);
    }

    /**
     * @param v
     * @param a
     * @return
     */
    @Override
    public void accum(int v, int a) {
        int accum = state.getAccum();
        int accumAux = state.getAccumAux();
        int newAccum = addCombineAccumHelp(v, accum);
        int newAccumAux = addCombineAccumHelp(a, accumAux);
        state.setAccum(newAccum);
        state.setAccumAux(newAccumAux);
    }

    /**
     * @return
     */
    @Override
    public UIEntryCombinerState finish() {
        return state;
    }

    /**
     * @param n
     * @param values
     * @param auxs
     * @param accum
     * @param accumAux
     * @return
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs, List<Integer> accum, List<Integer> accumAux) {
        int index;
        int accumPointer;
        int auxPointer;
        int result;
        UIEntryCombinerState returnValue = new UIEntryCombinerState();

        if (values.size() > n || auxs.size() > n || accum.size() > n || accumAux.size() > n) {
            return null;
        }

        if (n > 0)
            accumPointer = values.get(0);
        else
            accumPointer = UI_ENTRY_VALUE_NOT_PRESENT;

        for (index = 1; index < n; index++) {
            result = addCombineAccumHelp(values.get(index), accumPointer);
            accumPointer = result;

        }

        if (n > 0) auxPointer = auxs.get(0);
        else auxPointer = UI_ENTRY_VALUE_NOT_PRESENT;

        for (index = 1; index < n; index++) {
            result = addCombineAccumHelp(auxs.get(index), auxPointer);
            auxPointer = result;
        }

        returnValue.setAccum(accumPointer);
        returnValue.setAccumAux(auxPointer);

        return returnValue;
    }

    private int addCombineAccumHelp(int x, int accumValue) {
        int result = accumValue;

        if (x == UI_ENTRY_VALUE_NOT_PRESENT) {
            return accumValue;
        }

        if (x == UI_ENTRY_UNKNOWN_VALUE) {
            if (result == UI_ENTRY_VALUE_NOT_PRESENT) {
                result = UI_ENTRY_UNKNOWN_VALUE;
            }
            return result;
        }

        if (accumValue == UI_ENTRY_UNKNOWN_VALUE || accumValue == UI_ENTRY_VALUE_NOT_PRESENT) {
            return x;
        }

        /*
         * Just in case, guard against overflow or underflow.  Also guard
         * adding up to the special values which are equal to INT_MAX and
         * INT_MAX - 1.
         */
        if (x > 0) {
            if (result <= Integer.MAX_VALUE - 2 - x) {
                result += x;
            } else {
                result = Integer.MAX_VALUE - 2;
            }
        } else if (x < 0) {
            if (result >= Integer.MIN_VALUE - x) {
                result += x;
            } else {
                result = Integer.MIN_VALUE;
            }
        }

        return result;
    }

    /**
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}