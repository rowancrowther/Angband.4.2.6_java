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

package uk.co.jackoftrades.middle.objects.enums;

public enum ObjectFlagName {
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

    private final String name;

    ObjectFlagName(String name) {
        this.name = name;
    }
}
