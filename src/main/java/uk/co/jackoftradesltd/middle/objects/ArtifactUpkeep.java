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
 * <p>C zeroes the whole {@code aup_info} array when it is allocated ({@code obj-init.c}), so every
 * flag starts clear. A new {@code ArtifactUpkeep} gets the same starting state from Java's
 * default of {@code false} for a {@code boolean} field, with no constructor needed. The flags are
 * independent: nothing in {@code obj-util.c} ties one to another.
 *
 * <p>Class ArtifactUpkeep coded on 260902, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class ArtifactUpkeep {
    /**
     * Whether this artifact has been created — C's {@code aup_info[i].created}. Written by C's
     * {@code mark_artifact_created()} and read by {@code is_artifact_created()}, both in
     * {@code obj-util.c}; the save file stores it as one byte (0 or 1).
     *
     * <p>Field created coded on 260902, commented in full on 261009.
     */
    private boolean created;
    /**
     * Whether this artifact has been seen this game — C's {@code aup_info[i].seen}. Written by C's
     * {@code mark_artifact_seen()} and read by {@code is_artifact_seen()}, both in
     * {@code obj-util.c}; the save file stores it as one byte (0 or 1).
     *
     * <p>Field seen coded on 260902, commented in full on 261009.
     */
    private boolean seen;
    /**
     * Whether this artifact has ever been seen — C's {@code aup_info[i].everseen}. Written by C's
     * {@code mark_artifact_everseen()} and read by {@code is_artifact_everseen()}, both in
     * {@code obj-util.c}; the save file stores it as one byte (0 or 1).
     *
     * <p>Field everseen coded on 260902, commented in full on 261009.
     */
    private boolean everseen;

    /**
     * Reports whether this artifact has been created — the read of C's
     * {@code aup_info[art->aidx].created}, as done by {@code is_artifact_created()} in
     * {@code obj-util.c}. C's {@code assert(art->aidx == aup_info[art->aidx].aidx)} has no
     * counterpart; see the class Javadoc.
     *
     * <p>Function isCreated coded on 260902, commented in full on 261009.
     *
     * @return whether this artifact has been created
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * Records whether this artifact has been created — the write of C's
     * {@code aup_info[art->aidx].created}, as done by {@code mark_artifact_created()} in
     * {@code obj-util.c}. The flag can be cleared as well as set.
     *
     * <p>Function setCreated coded on 260902, commented in full on 261009.
     *
     * @param created whether this artifact has been created
     */
    public void setCreated(boolean created) {
        this.created = created;
    }

    /**
     * Reports whether this artifact has been seen this game — the read of C's
     * {@code aup_info[art->aidx].seen}, as done by {@code is_artifact_seen()} in
     * {@code obj-util.c}.
     *
     * <p>Function isSeen coded on 260902, commented in full on 261009.
     *
     * @return whether this artifact has been seen this game
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * Records whether this artifact has been seen this game — the write of C's
     * {@code aup_info[art->aidx].seen}, as done by {@code mark_artifact_seen()} in
     * {@code obj-util.c}. The flag can be cleared as well as set.
     *
     * <p>Function setSeen coded on 260902, commented in full on 261009.
     *
     * @param seen whether this artifact has been seen this game
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * Reports whether this artifact has ever been seen — the read of C's
     * {@code aup_info[art->aidx].everseen}, as done by {@code is_artifact_everseen()} in
     * {@code obj-util.c}.
     *
     * <p>Function isEverseen coded on 260902, commented in full on 261009.
     *
     * @return whether this artifact has ever been seen
     */
    public boolean isEverseen() {
        return everseen;
    }

    /**
     * Records whether this artifact has ever been seen — the write of C's
     * {@code aup_info[art->aidx].everseen}, as done by {@code mark_artifact_everseen()} in
     * {@code obj-util.c}. The flag can be cleared as well as set.
     *
     * <p>Function setEverseen coded on 260902, commented in full on 261009.
     *
     * @param everseen whether this artifact has ever been seen
     */
    public void setEverseen(boolean everseen) {
        this.everseen = everseen;
    }
}
