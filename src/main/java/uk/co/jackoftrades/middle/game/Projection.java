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
 *  Java code copyright (c) 2026 Rowan Crowther, Jack of Trades Ltd.
 */

package uk.co.jackoftrades.middle.game;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
import uk.co.jackoftrades.middle.enums.MessageEnum;

public class Projection {
    private ProjectionEnum projection;
    private String name;
    private ProjectionType type;
    private String description;
    private String playerDescription;
    private String blindDescription;
    private String lashDescription;
    private int numerator;
    private int denominator;
    private Random diceDenominator;
    private int divisor;
    private int damageCap;
    private MessageEnum msgt;
    private boolean isObvious;
    private boolean willWake;
    private ColourType colour;

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
                      MessageEnum message,
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
                      MessageEnum message,
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
