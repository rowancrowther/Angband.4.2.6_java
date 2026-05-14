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

package uk.co.jackoftrades.frontend.screen;

import uk.co.jackoftrades.frontend.events.Event;
import uk.co.jackoftrades.frontend.screen.enums.Sidebar;
import uk.co.jackoftrades.frontend.screen.hooks.TermEventHook;

import java.util.ArrayList;

public class Term {
    private Object user;

    private TermData data;

    private boolean userFlag;
    private boolean dataFlag;

    private boolean activeFlag;
    private boolean mappedFlag;
    private boolean totalErase;
    private boolean fixedShape;
    private boolean ickyCorner;
    private boolean softCursor;
    private boolean alwaysPict;
    private boolean higherPict;
    private boolean alwaysText;
    private boolean neverBored;
    private boolean neverFrosh;

    private Sidebar sidebarMode;

    private boolean complexInput;

    private ArrayList<Event> keyQueue;

    private int keyHead;
    private int keyTail;
    private int keyXtra;
    private int keySize;

    private int wid;
    private int hgt;

    private int y1;
    private int y2;

    private ArrayList<Integer> x1;
    private ArrayList<Integer> x2;

    private int offsetX;
    private int offsetY;

    private TermWin old;
    private TermWin scr;

    private TermWin tmp;
    private TermWin mem;

    private int saved;

    private Object initHook;
    private Object nukeHook;

    private TermEventHook xtraHook;
    private TermEventHook cursHook;
    private TermEventHook bigcursHook;
    private TermEventHook wipeHook;
    private TermEventHook textHook;
    private TermEventHook pictHook;
    private TermEventHook viewMapHook;
    private TermEventHook dblhHook;

    public void termInit(int width, int height, int keys) {
        user = null;
        data = null;

        userFlag = false;
        dataFlag = false;
        activeFlag = false;
        mappedFlag = false;
        totalErase = false;
        fixedShape = false;
        ickyCorner = false;
        softCursor = false;
        alwaysPict = false;
        higherPict = false;
        alwaysText = false;
        neverBored = false;
        neverFrosh = false;
        sidebarMode = Sidebar.SIDEBAR_LEFT;
        complexInput = false;
        keyQueue = new ArrayList<>();
        keyHead = 0;
        keyTail = 0;
        keyXtra = 0;
        keySize = keys;
        wid = width;
        hgt = height;
        x1 = new ArrayList<>();
        x2 = new ArrayList<>();

        old = new TermWin();
        old.init(width, height);

        scr = new TermWin();
        scr.init(width, height);

        if (x1.isEmpty()) {
            initArrays(height);
        } else {
            for (int index = 0; index < height; index++) {
                x1.set(index, 0);
                x2.set(index, width - 1);
            }

            y1 = 0;
            y2 = height - 1;

            totalErase = true;
            saved = 0;
        }

        initHook = null;
        nukeHook = null;
        textHook = null;
        pictHook = null;
        viewMapHook = null;
        dblhHook = null;
        xtraHook = null;
        cursHook = null;
        bigcursHook = null;
        wipeHook = null;
    }

    private void initArrays(int height) {
        for (int y = 0; y < height; y++) {
            x1.add(0);
            x2.add(0);
        }
    }

    public void setSoftCursor(boolean softCursor) {
        this.softCursor = softCursor;
    }

    public void setHigherPict(boolean higherPict) {
        this.higherPict = higherPict;
    }

    public void setComplexInput(boolean complexInput) {
        this.complexInput = complexInput;
    }

    public void setXtraHook(TermEventHook xtraHook) {
        this.xtraHook = xtraHook;
    }

    public void setCursHook(TermEventHook cursHook) {
        this.cursHook = cursHook;
    }

    public void setBigcursHook(TermEventHook bigcursHook) {
        this.bigcursHook = bigcursHook;
    }

    public void setWipeHook(TermEventHook wipeHook) {
        this.wipeHook = wipeHook;
    }

    public void setTextHook(TermEventHook textHook) {
        this.textHook = textHook;
    }

    public void setPictHook(TermEventHook pictHook) {
        this.pictHook = pictHook;
    }

    public void setDblhHook(TermEventHook dblhHook) {
        this.dblhHook = dblhHook;
    }

    public void setViewMapHook(TermEventHook viewMapHook) {
        this.viewMapHook = viewMapHook;
    }

    public void setData(TermData data) {
        this.data = data;
    }

    public TermData getTermData() {
        return data;
    }
}