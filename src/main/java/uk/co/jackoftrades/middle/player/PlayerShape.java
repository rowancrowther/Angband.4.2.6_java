package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Effect;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerShape {
    private final Logger logger = LogManager.getLogger();

    private String name;
    // private int sidx;
    private int toAc;
    private int toHit;
    private int toDam;

    private HashMap<PlayerSkill, Integer> skills;
    private Flag<ObjectFlag> flags;
    private Flag<PlayerFlag> pflags;
    private HashMap<PlayerSkill, Integer> skillModifiers;
    private HashMap<Stats, Integer> statModifiers;
    private ArrayList<ElementEnum> resists;

    private Effect effect;

    private int numBlows;
    private ArrayList<PlayerBlow> playerBlow;

    public PlayerShape(String name,
                       // int sidx,
                       int toAc,
                       int toHit,
                       int toDam,
                       HashMap<PlayerSkill, Integer> skills,
                       Flag<ObjectFlag> flags,
                       Flag<PlayerFlag> pflags,
                       HashMap<PlayerSkill, Integer> skillModifiers,
                       HashMap<Stats, Integer> statModifiers,
                       ArrayList<ElementEnum> resists,
                       Effect effect,
                       int numBlows,
                       ArrayList<PlayerBlow> playerBlow) {
        this.name = name;
        // this.sidx = sidx;
        this.toAc = toAc;
        this.toHit = toHit;
        this.toDam = toDam;
        this.skills = skills;
        this.flags = flags;
        this.pflags = pflags;
        this.skillModifiers = skillModifiers;
        this.statModifiers = statModifiers;
        this.resists = resists;
        this.effect = effect;
        this.numBlows = numBlows;
        this.playerBlow = playerBlow;
    }

    @Override
    public String toString() {
        return "PlayerShape{" +
                "logger=" + logger +
                ", name='" + name + '\'' +
                // ", sidx=" + sidx +
                ", toAc=" + toAc +
                ", toHit=" + toHit +
                ", toDam=" + toDam +
                ", skills=" + skills +
                ", flags=" + flags +
                ", pflags=" + pflags +
                ", skillModifiers=" + skillModifiers +
                ", statModifiers=" + statModifiers +
                ", resists=" + resists +
                ", effect=" + effect +
                ", numBlows=" + numBlows +
                ", playerBlow=" + playerBlow +
                '}';
    }
}