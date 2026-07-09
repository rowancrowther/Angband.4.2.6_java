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

package uk.co.jackoftrades.frontend.screen.enums;

/**
 * The strategies for combining multiple contributing values into a single
 * displayed value in a UI entry (for example merging several sources of the same
 * stat). Mirrors the combiner modes of the C original's UI-entry system.
 *
 * @author Rowan Crowther
 */
public enum CombinerName {
    /**
     * No combination (single value, or combining disabled). @author Rowan Crowther
     */
    NONE,
    /**
     * Sum the contributing values. @author Rowan Crowther
     */
    ADD,
    /** Bitwise-OR the contributing values together. @author Rowan Crowther */
    BITWISE_OR,
    /** Take the first contributing value. @author Rowan Crowther */
    FIRST,
    /** Take the largest contributing value. @author Rowan Crowther */
    LARGEST,
    /** Take the last contributing value. @author Rowan Crowther */
    LAST,
    /** Logical-OR (true if any contributor is true). @author Rowan Crowther */
    LOGICAL_OR,
    /** Logical-OR but with a cancelling rule for opposing values. @author Rowan Crowther */
    LOGICAL_OR_WITH_CANCEL,
    /** Resistance combination treating zero specially. @author Rowan Crowther */
    RESIST_0,
    /** Take the smallest contributing value. @author Rowan Crowther */
    SMALLEST
}
