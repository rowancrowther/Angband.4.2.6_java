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

public class BlowMethod {
    private String name;
    private boolean cut;
    private boolean stun;
    private boolean miss;
    private boolean phys;
    private MessageType mesgT;
    private List<String> blowMessage;
    private String desc;

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

    public String getName() {
        return name;
    }
}