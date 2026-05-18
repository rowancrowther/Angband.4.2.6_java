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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;

/**
 * Stem class to hold the c angband object
 */

public class ItemObject {
    private Object artifact;
    private ObjectKind kind;
    private Loc location;

    private Flag<ObjectNotice> notice;

    private int heldMIndex;
    private int mimickingMIndex;

    public ItemObject() {
        notice = new Flag<>(ObjectNotice.class);
    }

    public void setGrid(Loc grid) {
        location = grid;
    }

    public void orNotice(ObjectNotice notice) {
        this.notice.on(notice);
    }

    public boolean isArtifact() {
        return artifact != null;
    }

    public ObjectKind getKind() {
        return kind;
    }

    public void setHeldMIndex(int heldMIndex) {
        this.heldMIndex = heldMIndex;
    }

    public void setMimickingMIndex(int mimickingMIndex) {
        this.mimickingMIndex = mimickingMIndex;
    }
}