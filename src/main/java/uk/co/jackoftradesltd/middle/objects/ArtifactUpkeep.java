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

package uk.co.jackoftradesltd.middle.objects;

/**
 * Per-artifact state that changes over the course of play — the port of C's
 * {@code struct artifact_upkeep} ({@code object.h}), which C keeps as one parallel array
 * ({@code aup_info}) indexed alongside {@code a_info}. This carries the three fields that are
 * saved to the save file: whether the artifact has been created, whether it has been seen this
 * game, and whether it has ever been seen.
 *
 * <p>C's {@code aidx} field is not ported: it exists only so a {@code struct artifact_upkeep}
 * can assert it is talking to the right slot of the parallel array
 * ({@code aup_info[i].aidx == i}). The Java port has no parallel array to cross-check against, so
 * there is nothing for that field to guard.
 *
 * <p>Class ArtifactUpkeep coded on 260902, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
public class ArtifactUpkeep {
    /**
     * Whether this artifact has been created — C's {@code aup_info[i].created}.
     */
    private boolean created;
    /**
     * Whether this artifact has been seen this game — C's {@code aup_info[i].seen}.
     */
    private boolean seen;
    /**
     * Whether this artifact has ever been seen — C's {@code aup_info[i].everseen}.
     */
    private boolean everseen;

    /**
     * @return whether this artifact has been created
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * @param created whether this artifact has been created
     */
    public void setCreated(boolean created) {
        this.created = created;
    }

    /**
     * @return whether this artifact has been seen this game
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @param seen whether this artifact has been seen this game
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * @return whether this artifact has ever been seen
     */
    public boolean isEverseen() {
        return everseen;
    }

    /**
     * @param everseen whether this artifact has ever been seen
     */
    public void setEverseen(boolean everseen) {
        this.everseen = everseen;
    }
}
