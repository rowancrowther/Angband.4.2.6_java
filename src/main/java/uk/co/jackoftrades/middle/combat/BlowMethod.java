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
 * @author Rowan Crowther
 */
public class BlowMethod {
    /**
     * The method's name (e.g. "HIT", "BITE").
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * Whether this method can cause cuts.
     *
     * @author Rowan Crowther
     */
    private boolean cut;
    /**
     * Whether this method can cause stunning.
     *
     * @author Rowan Crowther
     */
    private boolean stun;
    /**
     * Whether this method can miss (vs. always landing).
     *
     * @author Rowan Crowther
     */
    private boolean miss;
    /**
     * Whether this method is a physical attack.
     *
     * @author Rowan Crowther
     */
    private boolean phys;
    /**
     * The message type/category used when reporting this blow.
     *
     * @author Rowan Crowther
     */
    private MessageType mesgT;
    /**
     * Flavour messages describing the blow landing.
     *
     * @author Rowan Crowther
     */
    private List<String> blowMessage;
    /**
     * Human-readable description of the method.
     *
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * @return whether this method can inflict cuts on its target
     * @author Rowan Crowther
     */
    public boolean isCut() {
        return cut;
    }

    /**
     * @return whether this method can stun its target
     * @author Rowan Crowther
     */
    public boolean isStun() {
        return stun;
    }

    /**
     * Whether a failed blow of this kind is announced to the player. This is about
     * <em>reporting</em>, not accuracy: it is false for the methods where a miss would
     * read oddly (crawling, drooling, gazing, wailing), which simply stay silent.
     *
     * @return whether the player is told when this blow misses
     * @author Rowan Crowther
     */
    public boolean isMiss() {
        return miss;
    }

    /**
     * @return whether this method does physical damage
     * @author Rowan Crowther
     */
    public boolean isPhys() {
        return phys;
    }

    /**
     * The message channel this blow is reported on, which drives the sound and colour
     * the interface gives it. Defaults to {@link MessageType#MSG_GENERIC} for a method
     * whose data-file entry gave no {@code msg:}.
     *
     * @return this blow method's message type
     * @author Rowan Crowther
     */
    public MessageType getMesgT() {
        return mesgT;
    }

    /**
     * The flavour strings narrating a landed blow, one of which is chosen at random when
     * the blow is described - so this holds every alternative, not a sequence (most
     * methods ship one, INSULT and MOAN ship eight apiece). The {@code {target}}-style
     * braces are placeholders still awaiting expansion at display time.
     *
     * @return this blow method's action messages, in data-file order; may be empty
     * @author Rowan Crowther
     */
    public List<String> getBlowMessage() {
        return blowMessage;
    }

    /**
     * @return the short phrase naming this method in monster lore, e.g. "hit", "drool on you"
     * @author Rowan Crowther
     */
    public String getDesc() {
        return desc;
    }
}