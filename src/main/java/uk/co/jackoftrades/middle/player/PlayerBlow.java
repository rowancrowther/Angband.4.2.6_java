package uk.co.jackoftrades.middle.player;

import org.jetbrains.annotations.Contract;

public class PlayerBlow {
    private String blowName;

    @Contract(pure = true)
    public PlayerBlow(String blowName) {
        this.blowName = blowName;
    }

    @Contract(pure = true)
    public String getBlowName() {
        return blowName;
    }
}
