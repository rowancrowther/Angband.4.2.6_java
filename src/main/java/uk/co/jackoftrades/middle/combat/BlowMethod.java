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

package uk.co.jackoftrades.middle.combat;

import uk.co.jackoftrades.middle.enums.MessageType;

import java.util.List;

/**
 * The definition of a monster attack "method" (as loaded from
 * {@code blow_methods.txt}) — how a blow is delivered (hit, bite, claw, …),
 * whether it can cut/stun/miss/is physical, and the flavour messages used to
 * describe it. This is the Java port of the C original's {@code struct blow_method}
 * ({@code src/monster.h}).
 *
 * @author ClaudeCode
 */
public class BlowMethod {
    /**
     * The method's name (e.g. "HIT", "BITE").
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * Whether this method can cause cuts.
     *
     * @author ClaudeCode
     */
    private boolean cut;
    /**
     * Whether this method can cause stunning.
     *
     * @author ClaudeCode
     */
    private boolean stun;
    /**
     * Whether this method can miss (vs. always landing).
     *
     * @author ClaudeCode
     */
    private boolean miss;
    /**
     * Whether this method is a physical attack.
     *
     * @author ClaudeCode
     */
    private boolean phys;
    /**
     * The message type/category used when reporting this blow.
     *
     * @author ClaudeCode
     */
    private MessageType mesgT;
    /**
     * Flavour messages describing the blow landing.
     *
     * @author ClaudeCode
     */
    private List<String> blowMessage;
    /**
     * Human-readable description of the method.
     *
     * @author ClaudeCode
     */
    private String desc;

    /**
     * Build a blow method from its parsed data-file fields.
     *
     * @param name        method name
     * @param cut         whether it can cut
     * @param stun        whether it can stun
     * @param miss        whether it can miss
     * @param phys        whether it is physical
     * @param mesgT       message category
     * @param blowMessage flavour messages
     * @param desc        description
     * @author ClaudeCode
     */
    public BlowMethod(String name, boolean cut, boolean stun, boolean miss, boolean phys, MessageType mesgT,
                      List<String> blowMessage, String desc) {
        this.name = name;
        this.cut = cut;
        this.stun = stun;
        this.miss = miss;
        this.phys = phys;
        this.mesgT = mesgT;
        this.blowMessage = blowMessage;
        this.desc = desc;
    }

    /**
     * @return this blow method's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }
}