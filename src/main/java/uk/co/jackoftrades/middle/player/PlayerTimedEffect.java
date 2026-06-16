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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.List;

public class PlayerTimedEffect {
    private TimedEffect name;
    private int flagRedraw;
    private int flagUpdate;

    private String description;
    private String onEnd;
    private String onIncrease;
    private String onDecrease;
    private MessageType msgT;
    private TimedFailure fail;
    private List<TimedGrade> grade;
    private Effect onBeginEffect;
    private Effect onEndEffect;
    private boolean NonStacking;
    private int lowerBound;
    private int ofFlagDup;
    private ObjectFlag oFlagSyn;
    private ElementEnum tempResist;
    private Brand tempBrand;
    private Slay tempSlay;

    public PlayerTimedEffect(TimedEffect name, String description,
                             String onEnd, String onIncrease,
                             String onDecrease, MessageType msgT,
                             TimedFailure fail, List<TimedGrade> grade,
                             Effect onBeginEffect, Effect onEndEffect,
                             boolean nonStacking, int lowerBound,
                             int ofFlagDup, ObjectFlag oFlagSyn,
                             ElementEnum tempResist, Brand tempBrand,
                             Slay tempSlay) {
        this.name = name;
        this.description = description;
        this.onEnd = onEnd;
        this.onIncrease = onIncrease;
        this.onDecrease = onDecrease;
        this.msgT = msgT;
        this.fail = fail;
        this.grade = grade;
        this.onBeginEffect = onBeginEffect;
        this.onEndEffect = onEndEffect;
        NonStacking = nonStacking;
        this.lowerBound = lowerBound;
        this.ofFlagDup = ofFlagDup;
        this.oFlagSyn = oFlagSyn;
        this.tempResist = tempResist;
        this.tempBrand = tempBrand;
        this.tempSlay = tempSlay;
    }
}