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

package uk.co.jackoftrades.frontend.screen;

import javafx.scene.text.Font;
import uk.co.jackoftrades.frontend.screen.hooks.TermEventHook;
import uk.co.jackoftrades.frontend.screen.hooks.TermXtraWin;

public class TermData {
    private Screen screen;

    private Term t;

    private final String s;

    private int keys;

    private int rows;
    private int cols;

    private int posX;
    private int posY;
    private int sizeWidth;
    private int sizeHeight;
    private int sizeOW1;
    private int sizeOH1;
    private int sizeOW2;
    private int sizeOH2;

    private boolean sizeHack;
    private boolean xtraHack;

    private boolean visible;
    private boolean maximized;
    private boolean bizarre;

    private Font font;
    private int fontWidth;
    private int fontHeight;

    private int tileWidth;
    private int tileHeight;

    private int mapTileWidth;
    private int mapTileHeight;

    private boolean mapActive;

    public TermData() {
        s = "";
    }

    public boolean isMapActive() {
        return mapActive;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getSizeOH2() {
        return sizeOH2;
    }

    public int getSizeOW2() {
        return sizeOW2;
    }

    public int getSizeOH1() {
        return sizeOH1;
    }

    public int getSizeOW1() {
        return sizeOW1;
    }

    public void termDataLink(Term term) {
        if (term == null) {
            t = new Term();
        } else {
            t = term;
        }

        t.termInit(cols, rows, keys);

        t.setSoftCursor(true);
        t.setComplexInput(true);
        t.setHigherPict(true);

        TermEventHook hook = new TermXtraWin();
        TermXtraWin win = new TermXtraWin();

        t.setXtraHook(win);
        t.setCursHook(hook);
        t.setBigcursHook(hook);
        t.setWipeHook(hook);
        t.setTextHook(hook);
        t.setPictHook(hook);
        t.setDblhHook(hook);
        t.setViewMapHook(hook);

        t.setData(this);
    }

    public Term getTerm() {

        return t;
    }
}
