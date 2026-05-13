package uk.co.jackoftrades.frontend.screen;

import javafx.scene.text.Font;

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
}
