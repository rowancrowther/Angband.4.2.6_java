package uk.co.jackoftrades.frontend.screen;

import uk.co.jackoftrades.frontend.events.Event;

import java.util.ArrayList;

public class Term {
    private Object user;

    private Object data;

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
    private int sidebarMode;

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

    private Object xtraHook;
    private Object cursHook;
    private Object bigcursHook;
    private Object wipeHook;
    private Object textHook;
    private Object pictHook;
    private Object viewMapHook;
    private Object dblhHook;
}