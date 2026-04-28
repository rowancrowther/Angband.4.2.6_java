package uk.co.jackoftrades.middle.objects.enums;

import org.jetbrains.annotations.Contract;

public enum ObjectKindFlag {
    KF_NONE(""),
    KF_RAND_HI_RES(""),
    KF_RAND_SUSTAIN(""),
    KF_RAND_POWER(""),
    KF_INSTA_ART(""),
    KF_QUEST_ART(""),
    KF_EASY_KNOW(""),
    KF_GOOD(""),
    KF_SHOW_DICE(""),
    KF_SHOW_MULT(""),
    KF_SHOOTS_SHOTS(""),
    KF_SHOOTS_ARROWS(""),
    KF_SHOOTS_BOLTS(""),
    KF_RAND_BASE_RES(""),
    KF_RAND_RES_POWER(""),
    KF_MAX("");

    private String flag;

    @Contract(pure = true)
    ObjectKindFlag(String flag) {
        this.flag = flag;
    }

    @Contract(pure = true)
    public String getFlag() {
        return flag;
    }
}