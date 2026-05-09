package uk.co.jackoftrades.middle.objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.Parser;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.Expression;
import uk.co.jackoftrades.middle.enums.EffectBaseType;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.ValueEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class Curse {
    private final static Logger logger = LogManager.getLogger();

    private String name;
    private boolean poss;
    private ArrayList<ObjectBase> objectBases;
    private String conflict;
    private ArrayList<ObjectFlag> flags;
    private ArrayList<ObjectFlag> conflictFlags;
    private Random dice;
    private Expression diceExpression;
    private ArrayList<TimedEffect> timedEffects;
    private Random time;
    private String description;
    private EffectEnum effect;
    private TimedEffect timedEffect;
    private int combatToHit;
    private int combatDam;
    private int combatAC;
    private Expression expression;
    private HashMap<ValueEnum, Integer> valueCollection;
    private String message;

    public Curse(String name,
                 boolean poss,
                 ArrayList<ObjectBase> objectBases,
                 ArrayList<ObjectFlag> flags,
                 String conflict,
                 ArrayList<ObjectFlag> conflictFlags,
                 String dice,
                 String time,
                 String description,
                 EffectEnum effect,
                 TimedEffect timedEffect,
                 int combatToHit,
                 int combatDam,
                 int combatAC,
                 char expressionChar,
                 EffectBaseType expressionEffect,
                 String expressionOperation,
                 HashMap<ValueEnum, Integer> valueCollection,
                 String message) {
        this.name = name;
        this.poss = poss;
        this.objectBases = objectBases;
        this.flags = flags;
        this.conflict = conflict;
        this.conflictFlags = conflictFlags;
        if (dice.isBlank()) {
            this.dice = null;
            this.diceExpression = null;
        } else if (!dice.startsWith("$")) {
            this.dice = Parser.parseDiceString(dice);
            this.diceExpression = null;
        } else {
            this.dice = null;
            this.diceExpression = new Expression(dice.substring(1).charAt(0), expressionEffect, expressionOperation);
        }
        if (time.isBlank())
            this.time = null;
        else
            this.time = Parser.parseDiceString(time);
        this.description = description;
        this.effect = effect;
        this.timedEffect = timedEffect;
        this.combatToHit = combatToHit;
        this.combatDam = combatDam;
        this.combatAC = combatAC;
        this.expression = new Expression(expressionChar, expressionEffect, expressionOperation);
        this.valueCollection = valueCollection;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Curse{" +
                "name='" + name + '\'' +
                ", poss=" + poss +
                ", objectBases=" + objectBases +
                ", conflict='" + conflict + '\'' +
                ", flags=" + flags +
                ", conflictFlags=" + conflictFlags +
                ", dice=" + dice +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", effect=" + effect +
                ", timedEffect=" + timedEffect +
                ", combatToHit=" + combatToHit +
                ", combatDam=" + combatDam +
                ", combatAC=" + combatAC +
                ", expression=" + expression +
                ", valueCollection=" + valueCollection +
                ", message='" + message + '\'' +
                '}';
    }
}