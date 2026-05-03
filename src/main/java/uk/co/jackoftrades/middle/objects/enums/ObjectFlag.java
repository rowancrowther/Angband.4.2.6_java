package uk.co.jackoftrades.middle.objects.enums;

import org.jetbrains.annotations.Contract;

public enum ObjectFlag {
    OF_NONE(""),
    OF_SUST_STR(" sStr"),
    OF_SUST_INT(" sInt"),
    OF_SUST_WIS(" sWis"),
    OF_SUST_DEX(" sDex"),
    OF_SUST_CON(" sCon"),
    OF_PROT_FEAR("pFear"),
    OF_PROT_BLIND("pBlnd"),
    OF_PROT_CONF("pConf"),
    OF_PROT_STUN("pStun"),
    OF_SLOW_DIGEST("S.Dig"),
    OF_FEATHER("Feath"),
    OF_REGEN("Regen"),
    OF_TELEPATHY("  ESP"),
    OF_SEE_INVIS("S.Inv"),
    OF_FREE_ACT("FrAct"),
    OF_HOLD_LIFE("HLife"),
    OF_IMPACT("Impct"),
    OF_BLESSED(" Bless"),
    OF_BURNS_OUT("BuOut"),
    OF_TAKES_FUEL("TFuel"),
    OF_NO_FUEL("NFuel"),
    OF_IMPAIR_HP("ImpHP"),
    OF_IMPAIR_MANA("ImpSP"),
    OF_AFRAID(" Fear"),
    OF_NO_TELEPORT("NoTel"),
    OF_AGGRAVATE("Aggrv"),
    OF_DRAIN_EXP("DrExp"),
    OF_STICKY("Stick"),
    OF_FRAGILE("Fragl"),
    OF_LIGHT_2("Lght2"),
    OF_LIGHT_3("Lght3"),
    OF_DIG_1(" Dig1"),
    OF_DIG_2(" Dig2"),
    OF_DIG_3(" Dig3"),
    OF_EXPLODE("Expld"),
    OF_TRAP_IMMUNE("TrpIm"),
    OF_THROWING("Throw"),
    OF_MULTIPLY_WEIGHT("MulWg"),
    OF_MAX("");

    private String flag;

    @Contract(pure = true)
    ObjectFlag(String flag) {
        this.flag = flag;
    }

    @Contract(pure = true)
    public String getFlag() {
        return flag;
    }
}
