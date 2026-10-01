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

package uk.co.jackoftradesltd.middle.game.event.projection;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.enums.MessageType;

/**
 * The definition of one projection type, as loaded from {@code projection.txt} — its identity, the
 * descriptions used in various contexts, the damage-scaling fraction, divisor and cap, the message
 * and display colour, and whether it is obvious or wakes monsters.
 *
 * <p>This is the Java form of {@code struct projection} in {@code project.h}, which
 * {@code obj-init.c} builds record by record in its {@code parse_projection_*} functions. The C
 * {@code index} is carried by the {@link ProjectionEnum} in {@link #projection}, the C {@code type}
 * string by a {@link ProjectionType}, and the C {@code color} integer by a {@link ColourEnum}. C's
 * single {@code random_value} denominator is split in two: a plain integer is held in
 * {@link #denominator} and a dice expression in {@link #diceDenominator}, one constructor each.
 * The class holds data only and carries no behaviour of its own.
 *
 * <p>Class Projection coded before 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public class Projection {
    /**
     * The projection's identity, standing in for the C {@code index} member and the key the
     * projection is looked up by.
     *
     * <p>Field projection coded before 261001, commented in full on 261001.
     */
    private ProjectionEnum projection;
    /**
     * The projection's name, from the {@code name:} line of {@code projection.txt}; the C
     * {@code name} member, which is {@code NULL} when the line is absent.
     *
     * <p>Field name coded before 261001, commented in full on 261001.
     */
    private String name;
    /**
     * The projection's broad category (element, environs or monster), standing in for the C
     * {@code type} string.
     *
     * <p>Field type coded before 261001, commented in full on 261001.
     */
    private ProjectionType type;
    /**
     * General description of the projection; the C {@code desc} member.
     *
     * <p>Field description coded before 261001, commented in full on 261001.
     */
    private String description;
    /**
     * Description used when the projection affects the player; the C {@code player_desc} member.
     *
     * <p>Field playerDescription coded before 261001, commented in full on 261001.
     */
    private String playerDescription;
    /**
     * Description used when the player is blind; the C {@code blind_desc} member.
     *
     * <p>Field blindDescription coded before 261001, commented in full on 261001.
     */
    private String blindDescription;
    /**
     * Description used for the "lash" (short beam) form; the C {@code lash_desc} member.
     *
     * <p>Field lashDescription coded before 261001, commented in full on 261001.
     */
    private String lashDescription;
    /**
     * Numerator of the damage-scaling fraction; the C {@code numerator} member, which is zero when
     * the line is absent.
     *
     * <p>Field numerator coded before 261001, commented in full on 261001.
     */
    private int numerator;
    /**
     * Integer denominator of the damage-scaling fraction, used when {@code denominator:} is a plain
     * number. It is {@code 0} when a dice denominator is held in {@link #diceDenominator} instead,
     * matching the zero C leaves in the unused {@code random_value}.
     *
     * <p>Field denominator coded before 261001, commented in full on 261001.
     */
    private int denominator;
    /**
     * Dice-expression denominator of the damage-scaling fraction, such as {@code 8+1d4}; the dice
     * form of the C {@code denominator} member. It is {@code null} when the integer constructor was
     * used.
     *
     * <p>Field diceDenominator coded before 261001, commented in full on 261001.
     */
    private Random diceDenominator;
    /**
     * Divisor applied to scaled damage; the C {@code divisor} member.
     *
     * <p>Field divisor coded before 261001, commented in full on 261001.
     */
    private int divisor;
    /**
     * Maximum damage this projection can deal; the C {@code damage_cap} member.
     *
     * <p>Field damageCap coded before 261001, commented in full on 261001.
     */
    private int damageCap;
    /**
     * The message type shown when this projection is used; the C {@code msgt} member, which is
     * {@code MSG_GENERIC} when the {@code msgt:} line is absent.
     *
     * <p>Field msgt coded before 261001, commented in full on 261001.
     */
    private MessageType msgt;
    /**
     * Whether the projection's effect is obvious to the player; the C {@code obvious} member.
     *
     * <p>Field isObvious coded before 261001, commented in full on 261001.
     */
    private boolean isObvious;
    /**
     * Whether the projection wakes sleeping monsters; the C {@code wake} member.
     *
     * <p>Field willWake coded before 261001, commented in full on 261001.
     */
    private boolean willWake;
    /**
     * The colour used to draw this projection; the C {@code color} member.
     *
     * <p>Field colour coded before 261001, commented in full on 261001.
     */
    private ColourEnum colour;

    /**
     * Build a projection whose damage denominator is a dice expression. The integer
     * {@link #denominator} is set to {@code 0}, as C leaves the unused part of its
     * {@code random_value}, and the arguments are stored as given.
     *
     * <p>Constructor Projection (dice denominator) coded before 261001, commented in full on 261001.
     *
     * @param projection       projection code
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
                      ColourEnum colour) {
        this.projection = projection;
        this.name = name;
        this.type = type;
        this.description = description;
        this.playerDescription = playerDescription;
        this.blindDescription = blindDescription;
        this.lashDescription = lashDescription;
        this.numerator = numerator;
        this.denominator = 0;
        this.diceDenominator = diceDenominator;
        this.divisor = divisor;
        this.damageCap = damageCap;
        this.msgt = message;
        this.isObvious = isObvious;
        this.willWake = willwake;
        this.colour = colour;
    }

    /**
     * Build a projection whose damage denominator is a fixed integer. The
     * {@link #diceDenominator} is set to {@code null} and the arguments are stored as given.
     *
     * <p>Constructor Projection (integer denominator) coded before 261001, commented in full on 261001.
     *
     * @param projection       projection code
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
                      ColourEnum colour) {
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
     * Reads the projection's name — for the elemental projections, the name a resistance rune is
     * displayed under.
     *
     * <p>Function getName coded before 261001, commented in full on 261001.
     *
     * @return the projection's name
     */
    public String getName() {
        return name;
    }

    /**
     * Reads the projection's broad category, which {@code finish_parse_projection} in
     * {@code obj-init.c} counts to check the number of elements.
     *
     * <p>Function getType coded before 261001, commented in full on 261001.
     *
     * @return the projection's broad category (element/environs/monster)
     */
    public ProjectionType getType() {
        return type;
    }

    /**
     * Reads the description used for the "lash" (short beam) form of the projection; the C
     * {@code lash_desc} member.
     *
     * <p>Function getLashDescription coded before 261001, commented in full on 261001.
     *
     * @return the lash-form description
     */
    public String getLashDescription() {
        return lashDescription;
    }

    /**
     * Reads the projection's code, which stands in for the C {@code index} member.
     *
     * <p>Function getProjection coded before 261001, commented in full on 261001.
     *
     * @return the projection's code — its identity, and the key it is looked up by
     */
    public ProjectionEnum getProjection() {
        return projection;
    }

    /**
     * Reads the description used when the projection affects the player; the C
     * {@code player_desc} member.
     *
     * <p>Function getPlayerDescription coded before 261001, commented in full on 261001.
     *
     * @return the player-affected description
     */
    public String getPlayerDescription() {
        return playerDescription;
    }

    /**
     * Builds a debug string listing every field of this projection. C has no counterpart.
     *
     * <p>Function toString coded before 261001, commented in full on 261001.
     *
     * @return a debug string listing this projection's fields
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
