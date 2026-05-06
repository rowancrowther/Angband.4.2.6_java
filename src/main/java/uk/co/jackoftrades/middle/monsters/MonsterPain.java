package uk.co.jackoftrades.middle.monsters;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class MonsterPain {
    private int painIndex;
    private ArrayList<String> messages;

    public MonsterPain(int painIndex, ArrayList<String> messages) {
        this.painIndex = painIndex;
        this.messages = messages;
    }

    @Override
    @Contract(pure = true)
    public String toString() {
        StringBuilder result = new StringBuilder("MonsterPain{" +
                "painIndex=" + painIndex +
                ", messages=");

        for (String message : messages) {
            result.append("'").append(message).append("', ");
        }

        return result.toString();
    }

    public int getPainIndex() {
        return painIndex;
    }
}
