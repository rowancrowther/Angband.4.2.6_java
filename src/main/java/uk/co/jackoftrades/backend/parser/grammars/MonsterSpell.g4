grammar MonsterSpell;

@header {
    import uk.co.jackoftrades.backend.numerics.Random;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.effect.Earthquake;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.effect.SummonType;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.EffectMonTimed;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.GlyphType;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.TeleportEnum;
    import uk.co.jackoftrades.middle.monsters.MonsterSpellLevel;
    import uk.co.jackoftrades.middle.monsters.MonsterSpellType;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import java.util.ArrayList;
    import java.util.List;
}

@members {
    private static final Logger logger = LogManager.getLogger();
}

name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

msgt
        returns[MessageType msgtEnum]
        :   MSGT UCASE {
                String raw = $UCASE.getText();
                $msgtEnum = MessageType.valueOf("MSG_" + raw);
            }
        ;

hit
        returns[int hitInt]
        :   HIT INTEGER { $hitInt = Integer.parseInt($INTEGER.getText()); }
        ;

effect
        returns[EffectEnum parm1, String parm2, String parm3, String parm4]
        @init {
            String eeRaw = "";
            $parm2 = "";
            $parm3 = "";
            $parm4 = "";
        }
        @after {
            if (!eeRaw.isEmpty())
                $parm1 = EffectEnum.valueOf("EF_" + eeRaw);
        }
        :   EFFECT pa1=UCASE COLON pa2=UCASE COLON pa3=INTEGER COLON pa4=INTEGER {
                eeRaw = $pa1.getText();
                $parm2 = $pa2.getText();
                $parm3 = $pa3.getText();
                $parm4 = $pa4.getText();
            }
        |   EFFECT pb1=UCASE COLON pb2=UCASE COLON pb3=INTEGER {
                eeRaw = $pb1.getText();
                $parm2 = $pb2.getText();
                $parm3 = $pb3.getText();
            }
        |   EFFECT pc1=UCASE COLON pc2=UCASE {
                eeRaw = $pc1.getText();
                $parm2 = $pc2.getText();
            }
        |   EFFECT pd1=UCASE { eeRaw = $pd1.getText(); }
        ;

effectYX
        returns[int y, int x]
        :   EFFECT_YX yInt=INTEGER COLON xInt=INTEGER {
                $y = Integer.parseInt($yInt.getText());
                $x = Integer.parseInt($xInt.getText());
            }
        ;

dice
        returns[String diceString]
        :   DICE {
                $diceString = $DICE.getText().substring(5);
            }
        ;

expr
        returns[Expression e]
        :   EXPR {
                // Set the defaults
                char defChar = 'D';
                EffectBaseType defType = EffectBaseType.EFB_NONE;
                String defOperation = "";

                String[] exprStrings = $EXPR.getText().split(":");
                // Ignore the first string
                if (exprStrings.length > 1)
                    defChar = exprStrings[1].charAt(0);
                if (exprStrings.length > 2)
                    defType = EffectBaseType.valueOf("EFB_" + exprStrings[2]);
                if (exprStrings.length > 3)
                    defOperation = exprStrings[3];

                $e = new Expression(defChar, defType, defOperation);
            }
        ;

effectBlock
        returns[Effect eff]
        @init {
            int yInit = 0;
            int xInit = 0;
            String diceStringInit = "";
            Expression expInit = null;
            EffectEnum parm1Init = EffectEnum.EF_NONE;
            String parm2Init = "";
            EffectSubTypeWrapper wrapperInit = null;
            String radiusInit = "";
            String parm4Init = "";
        }
        @after {
            if (!parm2Init.isEmpty()) {
                // Create the wrapper
                switch (parm1Init.getSubType()) {
                    case EST_PROJ:
                        wrapperInit = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + parm2Init));
                        break;

                    case EST_TMD:
                        wrapperInit = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + parm2Init));
                        break;

                    case EST_NOURISH:
                        wrapperInit = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + parm2Init));
                        break;

                    case EST_MON_TMD:
                        wrapperInit = new EffectSubTypeWrapper(EffectMonTimed.valueOf("MON_TMD_" + parm2Init));
                        break;

                    case EST_SUMMON:
                        wrapperInit = new EffectSubTypeWrapper(SummonType.valueOf("SUM_" + parm2Init));
                        break;

                    case EST_STAT:
                        wrapperInit = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + parm2Init));
                        break;

                    case EST_ENCHANT:
                        wrapperInit = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + parm2Init));
                        break;

                    case EST_EARTHQUAKE:
                        wrapperInit = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + parm2Init));
                        break;

                    case EST_GLYPH:
                        wrapperInit = new EffectSubTypeWrapper(GlyphType.valueOf("GLYPH_" + parm2Init));
                        break;

                    case EST_TELEPORT:
                        wrapperInit = new EffectSubTypeWrapper(TeleportEnum.valueOf("TELE_TELEPORT_" + parm2Init), false);
                        break;

                    case EST_TELEPORT_TO:
                        wrapperInit = new EffectSubTypeWrapper(TeleportEnum.valueOf("TELE_TELEPORT_" + parm2Init), true);
                        break;

                    default:
                        logger.error("Invalid subtype found in Effect Block.");
                }
            }

            Random radiusRnd = Random.parseStr(radiusInit);

            $eff = new Effect(parm1Init, yInit, xInit, diceStringInit,
                              parm1Init.getSubType(), wrapperInit,
                              radiusRnd, parm4Init, expInit);
        }
        :   effect {
                parm1Init = $effect.parm1;
                parm2Init = $effect.parm2;
                radiusInit = $effect.parm3;
                parm4Init = $effect.parm4;
            }
            (effectYX {
                yInit = $effectYX.y;
                xInit = $effectYX.x;
            })?
            (dice {
                diceStringInit = $dice.diceString;
            })?
            (expr {
                expInit = $expr.e;
            })?
        ;

powerCutoff
        returns[int powerInt]
        :   POWER_CUTOFF INTEGER {
                $powerInt = Integer.parseInt($INTEGER.getText());
            }
        ;

lore
        returns[String loreStr]
        :   LORE LCASE { $loreStr = $LCASE.getText(); }
        ;

loreColourBase
        returns[ColourType baseCol]
        :   LORE_COLOUR_BASE LCASE {
                $baseCol = ColourType.findColourType($LCASE.getText());
            }
        ;

loreColourResist
        returns[ColourType resCol]
        :   LORE_COLOUR_RESIST LCASE {
                $resCol = ColourType.findColourType($LCASE.getText());
            }
        ;

loreColourImmune
        returns[ColourType immCol]
        :   LORE_COLOUR_IMMUNE LCASE {
                $immCol = ColourType.findColourType($LCASE.getText());
            }
        ;

messageSave
        returns[String saveMsgStr]
        :   MESSAGE_SAVE LCASE {
                $saveMsgStr = $LCASE.getText();
            }
        ;

messageVis
        returns[String visMsgStr]
        :   MESSAGE_VIS LCASE {
                $visMsgStr = $LCASE.getText();
            }
        ;

messageInvis
        returns[String invisMsgStr]
        :   MESSAGE_INVIS LCASE {
                $invisMsgStr = $LCASE.getText();
            }
        ;

messageMiss
        returns[String missMsgStr]
        :   MESSAGE_MISS LCASE {
                $missMsgStr = $LCASE.getText();
            }
        ;

monsterSpellLevel
        returns[MonsterSpellLevel spellLevel]
        @init{
            int powerCutOffInit = 0;
            String loreDescInit = "";
            ColourType loreColTypeInit = ColourType.COLOUR_TYPE_DARK;
            ColourType loreResistColTypeInit = ColourType.COLOUR_TYPE_DARK;
            ColourType loreImmuneColTypeInit = ColourType.COLOUR_TYPE_DARK;
            String messageInit = "";
            String blindMessageInit = "";
            String missMessageInit = "";
            String saveMessageInit = "";
        }
        @after {
            $spellLevel = new MonsterSpellLevel(powerCutOffInit, loreDescInit,
                              loreColTypeInit, loreResistColTypeInit,
                              loreImmuneColTypeInit, messageInit,
                              blindMessageInit, missMessageInit,
                              saveMessageInit);
        }
        :   (powerCutoff {
                powerCutOffInit = $powerCutoff.powerInt;
            })?
        (   lore {
                loreDescInit = loreDescInit + $lore.loreStr;
            }
        |   loreColourBase {
                loreColTypeInit = $loreColourBase.baseCol;
            }
        |   loreColourResist {
                loreResistColTypeInit = $loreColourResist.resCol;
            }
        |   loreColourImmune {
                loreImmuneColTypeInit = $loreColourImmune.immCol;
            }
        |   messageVis {
                messageInit = messageInit + $messageVis.visMsgStr;
            }
        |   messageInvis {
                blindMessageInit = blindMessageInit + $messageInvis.invisMsgStr;
            }
        |   messageMiss {
                missMessageInit = missMessageInit + $messageMiss.missMsgStr;
            }
        |   messageSave {
                saveMessageInit = saveMessageInit + $messageSave.saveMsgStr;
            })+
        ;

monsterSpellType
        returns[MonsterSpellType spellType]
        @init {
            MonsterSpell indexInit = MonsterSpell.RSF_NONE;
            MessageType msgInit = MessageType.MSG_NONE;
            int hitInit = 0;
            List<Effect> effectsInit = new ArrayList<>();
            List<MonsterSpellLevel> levels = new ArrayList<>();
        }
        @after {
            $spellType = new MonsterSpellType(indexInit, msgInit,
                             hitInit, effectsInit, levels);
        }
        :   name {
                String raw = $name.nameStr;
                indexInit = MonsterSpell.valueOf("RSF_" + raw);
            }
        (   msgt {
                msgInit = $msgt.msgtEnum;
            }
        |   hit {
                hitInit = $hit.hitInt;
            })+
            (effectBlock {
                effectsInit.add($effectBlock.eff);
            })*
            (monsterSpellLevel {
                levels.add($monsterSpellLevel.spellLevel);
            })*
        ;

file
        returns[List<MonsterSpellType> spellTypes]
        @init {
            $spellTypes = new ArrayList<>();
        }
        :   (monsterSpellType {
                $spellTypes.add($monsterSpellType.spellType);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

MSGT
        :   'msgt:'
        ;

HIT
        :   'hit:'
        ;

EFFECT
        :   'effect:'
        ;

EFFECT_YX
        :   'effect-yx:'
        ;

DICE
        :   'dice:' ('0'..'9' | 'd' | '+' | '-' | 'M' | ',' | 'S' | 'D' | 'B' | '$')+
        ;

EXPR
        :   'expr:' ('S' | 'D' | 'B' | 'M') ':'? ('A'..'Z' | '_')* ':'? (' ' | '0'..'9' | '+' | '/' | '-' | '*')*
        ;

POWER_CUTOFF
        :   'power-cutoff:'
        ;


LORE
        :   'lore:'
        ;

LORE_COLOUR_BASE
        :   'lore-color-base:'
        ;

LORE_COLOUR_RESIST
        :   'lore-color-resist:'
        ;

LORE_COLOUR_IMMUNE
        :   'lore-color-immune:'
        ;

MESSAGE_SAVE
        :   'message-save:'
        ;

MESSAGE_VIS
        :   'message-vis:'
        ;

MESSAGE_INVIS
        :   'message-invis:'
        ;

MESSAGE_MISS
        :   'message-miss:'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

UCASE
        :   ('A'..'Z' | '_')+
        ;

LCASE
        :   ('a'..'z' | ' ' | '+' | '-' | '/' | '*' | 'A'..'Z' | '{' | '}'
            | '.' | ',' | '!' | '\'')+
        ;

COLON
        :   ':'
        ;
