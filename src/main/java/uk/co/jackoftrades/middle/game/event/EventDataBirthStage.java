package uk.co.jackoftrades.middle.game.event;

import java.util.ArrayList;

public class EventDataBirthStage implements GameEventData {
    private boolean reset;
    private final String hint;
    private int nChoices;
    private int initialChoice;
    private ArrayList<String> choices;
    private ArrayList<String> helpTexts;
    private Object xtra;

    public EventDataBirthStage(boolean reset,
                               String hint,
                               int nChoices,
                               int initialChoice,
                               ArrayList<String> choices,
                               ArrayList<String> helpTexts,
                               Object xtra) {
        this.reset = reset;
        this.hint = hint;
        this.nChoices = nChoices;
        this.initialChoice = initialChoice;
        this.choices = choices;
        this.helpTexts = helpTexts;
        this.xtra = xtra;
    }

    public boolean isReset() {
        return reset;
    }

    public String getHint() {
        return hint;
    }

    public int getnChoices() {
        return nChoices;
    }

    public int getInitialChoice() {
        return initialChoice;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public ArrayList<String> getHelpTexts() {
        return helpTexts;
    }

    public Object getXtra() {
        return xtra;
    }
}