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

package uk.co.jackoftrades.middle.game;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
import uk.co.jackoftrades.middle.enums.MessageType;

/**
 * The definition of one projection type (as loaded from {@code projection.txt})
 * — its identity, the descriptions used in various contexts, the damage-scaling
 * fraction/divisor/cap, the message and display colour, and whether it is obvious
 * or wakes monsters. This is the Java port of the C original's
 * {@code struct projection} ({@code src/project.h}). Two constructors allow the
 * damage denominator to be given either as a fixed integer or as a dice
 * expression ({@link #diceDenominator}).
 *
 * @author Rowan Crowther
 */
public class Projection {
    /**
     * The projection's identity/code.
     *
     * @author Rowan Crowther
     */
    private ProjectionEnum projection;
    /**
     * The projection's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * The projection's broad category (element/environs/monster).
     *
     * @author Rowan Crowther
     */
    private ProjectionType type;
    /**
     * General description of the projection.
     *
     * @author Rowan Crowther
     */
    private String description;
    /**
     * Description used when the projection affects the player.
     *
     * @author Rowan Crowther
     */
    private String playerDescription;
    /**
     * Description used when the player is blind.
     *
     * @author Rowan Crowther
     */
    private String blindDescription;
    /**
     * Description used for the "lash" (short beam) form.
     *
     * @author Rowan Crowther
     */
    private String lashDescription;
    /**
     * Numerator of the damage-scaling fraction.
     *
     * @author Rowan Crowther
     */
    private int numerator;
    /**
     * Integer denominator of the damage-scaling fraction ({@code -1} when a dice
     * denominator is used instead).
     *
     * @author Rowan Crowther
     */
    private int denominator;
    /**
     * Dice-expression denominator of the damage-scaling fraction (used instead of
     * {@link #denominator} for the dice-based constructor).
     *
     * @author Rowan Crowther
     */
    private Random diceDenominator;
    /**
     * Divisor applied to scaled damage.
     *
     * @author Rowan Crowther
     */
    private int divisor;
    /**
     * Maximum damage this projection can deal.
     *
     * @author Rowan Crowther
     */
    private int damageCap;
    /**
     * The message shown when this projection is used.
     *
     * @author Rowan Crowther
     */
    private MessageType msgt;
    /**
     * Whether the projection's effect is obvious to the player.
     *
     * @author Rowan Crowther
     */
    private boolean isObvious;
    /**
     * Whether the projection wakes sleeping monsters.
     *
     * @author Rowan Crowther
     */
    private boolean willWake;
    /**
     * The colour used to draw this projection.
     *
     * @author Rowan Crowther
     */
    private ColourType colour;

    /**
     * Build a projection whose damage denominator is a dice expression.
     *
     * @param projection        projection code
     * @param name              name
     * @param type              broad category
     * @param description       general description
     * @param playerDescription player-affected description
     * @param blindDescription  blind description
     * @param lashDescription   lash-form description
     * @param numerator         damage fraction numerator
     * @param diceDenominator   damage fraction denominator as dice
     * @param divisor           damage divisor
     * @param damageCap         maximum damage
     * @param message           use message
     * @param isObvious         whether the effect is obvious
     * @param willwake          whether it wakes monsters
     * @param colour            display colour
     * @author Rowan Crowther
     */
    public Projection(ProjectionEnum projection,
                      String name,
                      ProjectionType type,
                      String description,
                      String playerDescription,
                      String blindDescription,
                      String lashDescription,
                      int numerator,
                      Random diceDenominator,
                      int divisor,
                      int damageCap,
                      MessageType message,
                      boolean isObvious,
                      boolean willwake,
                      ColourType colour) {
        this.projection = projection;
        this.name = name;
        this.type = type;
        this.description = description;
        this.playerDescription = playerDescription;
        this.blindDescription = blindDescription;
        this.lashDescription = lashDescription;
        this.numerator = numerator;
        this.denominator = -1;
        this.diceDenominator = diceDenominator;
        this.divisor = divisor;
        this.damageCap = damageCap;
        this.msgt = message;
        this.isObvious = isObvious;
        this.willWake = willwake;
        this.colour = colour;
    }

    /**
     * Build a projection whose damage denominator is a fixed integer.
     *
     * @param projection        projection code
     * @param name              name
     * @param type              broad category
     * @param description       general description
     * @param playerDescription player-affected description
     * @param blindDescription  blind description
     * @param lashDescription   lash-form description
     * @param numerator         damage fraction numerator
     * @param denominator       damage fraction denominator
     * @param divisor           damage divisor
     * @param damageCap         maximum damage
     * @param message           use message
     * @param isObvious         whether the effect is obvious
     * @param willWake          whether it wakes monsters
     * @param colour            display colour
     * @author Rowan Crowther
     */
    public Projection(ProjectionEnum projection,
                      String name,
                      ProjectionType type,
                      String description,
                      String playerDescription,
                      String blindDescription,
                      String lashDescription,
                      int numerator,
                      int denominator,
                      int divisor,
                      int damageCap,
                      MessageType message,
                      boolean isObvious,
                      boolean willWake,
                      ColourType colour) {
        this.projection = projection;
        this.name = name;
        this.type = type;
        this.description = description;
        this.playerDescription = playerDescription;
        this.blindDescription = blindDescription;
        this.lashDescription = lashDescription;
        this.numerator = numerator;
        this.denominator = denominator;
        this.diceDenominator = null;
        this.divisor = divisor;
        this.damageCap = damageCap;
        this.msgt = message;
        this.isObvious = isObvious;
        this.willWake = willWake;
        this.colour = colour;
    }

    /**
     * @return the lash-form description
     * @author Rowan Crowther
     */
    public String getLashDescription() {
        return lashDescription;
    }

    /**
     * @return a debug string listing this projection's fields
     * @author Rowan Crowther
     */
    @Override
    public String toString() {
        return "Projection{" +
                "projection=" + projection +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", playerDescription='" + playerDescription + '\'' +
                ", blindDescription='" + blindDescription + '\'' +
                ", lashDescription='" + lashDescription + '\'' +
                ", numerator=" + numerator +
                ", denominator=" + denominator +
                ", diceDenominator=" + diceDenominator +
                ", divisor=" + divisor +
                ", damageCap=" + damageCap +
                ", isObvious=" + isObvious +
                ", willwake=" + willWake +
                ", colour=" + colour +
                '}';
    }
}
