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

package uk.co.jackoftradesltd.channel.messages.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.middle.cave.Loc;

import java.util.ArrayList;

/**
 * {@link GameEventData} payload describing an explosion ("ball") effect — the
 * projection type, the affected grids with their distances from the centre and
 * per-grid visibility, and the blast centre. Consumed by the display layer to
 * animate the blast radius.
 * <p>
 * Port of C's anonymous {@code explosion} struct nested in {@code game_event_data}
 * ({@code game-event.h}), populated and dispatched by
 * {@code event_signal_blast} ({@code game-event.c}), which is itself called
 * once, from {@code project.c}, with {@code EVENT_EXPLOSION} as the event type.
 * C holds {@code distance_to_grid}, {@code player_sees_grid} and {@code blast_grid}
 * as raw pointers into caller-owned arrays, each indexed {@code 0 <= i < num_grids}
 * and kept parallel to one another; the port carries the same three as parallel
 * {@link ArrayList}s rather than borrowed arrays. That is purely a representational
 * change — {@code event_signal_blast} only reads through the pointers it is handed
 * before the dispatch returns, so there is no aliasing behaviour lost by copying
 * into lists instead.
 * <p>
 * Class EventDataExplosion coded before 260915, commented in full on 260915, C line numbers
 * removed on 261012.
 *
 * @author Rowan Crowther
 */
public class EventDataExplosion implements GameEventData {
    private static final Logger logger = LogManager.getLogger(EventDataExplosion.class);
    
    /**
     * The projection type of the explosion, the port of C's {@code proj_type}.
     * Currently a raw integer, like the C original — likely to be replaced by an
     * enum once the projection types themselves are ported.
     * <p>
     * Field projType coded before 260915, commented in full on 260915.
     */
    private int projType;
    /**
     * Number of grids affected by the blast, the port of C's {@code num_grids}.
     * Also the valid length of {@link #distanceToGrid}, {@link #playerSeesGrid}
     * and {@link #blastGrid}, which C indexes {@code 0 <= i < num_grids}.
     * <p>
     * Field numGrids coded before 260915, commented in full on 260915.
     */
    private int numGrids;
    /**
     * Distance from the centre for each affected grid (parallel to
     * {@link #blastGrid}), the port of C's {@code distance_to_grid} array.
     * <p>
     * Field distanceToGrid coded before 260915, commented in full on 260915.
     */
    private ArrayList<Integer> distanceToGrid;
    /**
     * Whether the explosion is currently being drawn, the direct port of C's
     * {@code drawing}.
     * <p>
     * Field drawing coded before 260915, commented in full on 260915.
     */
    private boolean drawing;
    /**
     * Whether the player sees each affected grid (parallel to {@link #blastGrid}),
     * the port of C's {@code player_sees_grid} array.
     * <p>
     * Field playerSeesGrid coded before 260915, commented in full on 260915.
     */
    private ArrayList<Boolean> playerSeesGrid;
    /**
     * The grids affected by the blast, the port of C's {@code blast_grid} array. Neither the list
     * nor any element is ever {@code null}; the constructor rejects both.
     * <p>
     * Field blastGrid coded before 260915, commented in full on 260915, null rule added on 261012.
     */
    private ArrayList<Loc> blastGrid;
    /**
     * The centre of the explosion, the direct port of C's {@code centre}. Never {@code null}; the
     * constructor rejects it.
     * <p>
     * Field centre coded before 260915, commented in full on 260915, null rule added on 261012.
     */
    private Loc centre;

    /**
     * Build an explosion payload, the port of {@code event_signal_blast}'s seven
     * data parameters ({@code game-event.c}, excluding the leading
     * {@code game_event_type}) collected into one payload object rather than
     * assigned into a shared union field by field.
     *
     * <p>Rejects a {@code null} {@code blastGrid}, a {@code null} {@code centre} and a
     * {@code null} element of {@code blastGrid}. C passes {@code centre} as a {@code struct loc} by
     * value and fills {@code blast_grid} with {@code num_grids} real grids, so neither can be null
     * there, and (0, 0) is a real grid on the map rather than "none": substituting
     * {@link Loc#zero} would draw the blast in the top-left corner and hide the caller's bug.
     * Each failure is logged at fatal level and thrown as an {@link IllegalArgumentException}
     * naming the argument, and for an element the index of the first null. The other lists are
     * stored as given and are not checked, and nor is their length against {@code numGrids}.
     *
     * <p>Constructor EventDataExplosion coded before 260915, commented in full on 260915; the
     * stamp moved above the tags, the null checks documented and the C line numbers removed on
     * 261012.
     *
     * @param projType       the projection type
     * @param numGrids       number of affected grids
     * @param distanceToGrid per-grid distance from centre
     * @param drawing        whether it is being drawn
     * @param playerSeesGrid per-grid visibility
     * @param blastGrid      the affected grids; stored by reference, not copied
     * @param centre         the blast centre
     * @throws IllegalArgumentException if {@code blastGrid} or {@code centre} is {@code null}, or
     *                                  if {@code blastGrid} holds a {@code null} element
     */
    public EventDataExplosion(int projType, int numGrids, ArrayList<Integer> distanceToGrid, boolean drawing,
                              ArrayList<Boolean> playerSeesGrid, ArrayList<Loc> blastGrid, Loc centre) {
        this.projType = projType;
        this.numGrids = numGrids;
        this.distanceToGrid = distanceToGrid;
        this.drawing = drawing;
        this.playerSeesGrid = playerSeesGrid;
        if (blastGrid == null) {
            String msg = "Unable to deal with null values for blastGrid in constructor";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
        if (centre == null) {
            String msg = "Unable to deal with null values for centre in constructor";
            logger.fatal(msg);
            throw new IllegalArgumentException(msg);
        }
        for (Loc loc : blastGrid) {
            if (loc == null) {
                String msg = "Unable to deal with null for value: " + blastGrid.indexOf(loc) + 
                        " of blastGrid in constructor";
                logger.fatal(msg);
                throw new IllegalArgumentException(msg);
            }
        }
        this.blastGrid = blastGrid;
        this.centre = centre;
    }

    /**
     * The projection type, the port of C's {@code data->explosion.proj_type}.
     * <p>
     * Method getProjType coded before 260915, commented in full on 260915.
     *
     * @return the projection type
     */
    public int getProjType() {
        return projType;
    }

    /**
     * The number of affected grids, the port of C's {@code data->explosion.num_grids}.
     * <p>
     * Method getNumGrids coded before 260915, commented in full on 260915.
     *
     * @return the number of affected grids
     */
    public int getNumGrids() {
        return numGrids;
    }

    /**
     * The per-grid distances from the centre, the port of C's
     * {@code data->explosion.distance_to_grid}.
     * <p>
     * Method getDistanceToGrid coded before 260915, commented in full on 260915.
     *
     * @return the per-grid distances from the centre
     */
    public ArrayList<Integer> getDistanceToGrid() {
        return distanceToGrid;
    }

    /**
     * Whether the explosion is being drawn, the port of C's
     * {@code data->explosion.drawing}.
     * <p>
     * Method isDrawing coded before 260915, commented in full on 260915.
     *
     * @return whether the explosion is being drawn
     */
    public boolean isDrawing() {
        return drawing;
    }

    /**
     * The per-grid visibility flags, the port of C's
     * {@code data->explosion.player_sees_grid}.
     * <p>
     * Method getPlayerSeesGrid coded before 260915, commented in full on 260915.
     *
     * @return the per-grid visibility flags
     */
    public ArrayList<Boolean> getPlayerSeesGrid() {
        return playerSeesGrid;
    }

    /**
     * The affected grids, the port of C's {@code data->explosion.blast_grid}.
     * <p>
     * Method getBlastGrid coded before 260915, commented in full on 260915.
     *
     * @return the affected grids
     */
    public ArrayList<Loc> getBlastGrid() {
        return blastGrid;
    }

    /**
     * The blast centre, the direct port of C's {@code data->explosion.centre}.
     * <p>
     * Method getCentre coded before 260915, commented in full on 260915.
     *
     * @return the blast centre
     */
    public Loc getCentre() {
        return centre;
    }
}