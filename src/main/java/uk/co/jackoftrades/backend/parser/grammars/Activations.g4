grammar Activations;

@header {
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.Effect;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.Expression;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.enums.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.Activation;

    import java.util.List;
    import java.util.ArrayList;
}

@members {
    record EffectRecord(EffectEnum type, EffectSubTypeEnum subType, TimedEffect tmdEff,
                        ProjectionEnum projType, EffectNourish nourType, Stats statType,
                        EffectEnchant effEnc, Summon summType, String radiusStr,
                        String parmStr) {}
}

name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

aim
        returns[boolean aimBool]
        :   AIM NUMBER { $aimBool = $NUMBER.getText().equals("1"); }
        ;

level
        returns[int levelInt]
        :   LEVEL NUMBER { $levelInt = Integer.parseInt($NUMBER.getText()); }
        ;
power
        returns[int powerInt]
        :   POWER NUMBER { $powerInt = Integer.parseInt($NUMBER.getText()); }
        ;

effect
        returns[EffectRecord effectEntry]
        @init {
            String effRadStr = "";
            String effParmStr = "";
            EffectEnum effType = EffectEnum.EF_NONE;
            EffectSubTypeEnum subTypeEnum = EffectSubTypeEnum.EST_NONE;
            TimedEffect timedInit = TimedEffect.TMD_NONE;
            ProjectionEnum projInit = ProjectionEnum.PROJ_NONE;
            EffectNourish nourishInit = EffectNourish.EN_NONE;
            Stats statInit = Stats.STAT_NONE;
            EffectEnchant effeInit = EffectEnchant.EE_NONE;
            Summon effSummon = null;
            
            String entry1 = "";
            String entry2 = "";
            String entry3 = "";
            String entry4 = "";
        }
        @after {
            effType = EffectEnum.valueOf("EF_" + entry1);
            subTypeEnum = effType.getSubType();

            switch(subTypeEnum) {
                case EST_PROJ:
                    projInit = ProjectionEnum.valueOf("PROJ_" + entry2);
                    break;
                case EST_TMD:
                    timedInit = TimedEffect.valueOf("TMD_" + entry2);
                    break;
                case EST_STAT:
                    statInit = Stats.valueOf("STAT_" + entry2);
                    break;
                case EST_NOURISH:
                    nourishInit = EffectNourish.valueOf("EN_" + entry2);
                    break;
                case EST_ENCHANT:
                    effeInit = EffectEnchant.valueOf("EE_" + entry2);
                    break;
                case EST_SUMMON:
                    effSummon = GameConstants.lookupSummon(entry2);
                    break;
            }

            effRadStr = entry3;
            effParmStr = entry4;

            $effectEntry = new EffectRecord(effType, subTypeEnum, timedInit, projInit,
                                            nourishInit, statInit, effeInit, effSummon,
                                            effRadStr, effParmStr);
        }
        :   EFFECT effType1=UCASE COLON subType1=UCASE COLON effRad1=NUMBER COLON effO1=STRING {
                entry1 = $effType1.getText();
                entry2 = $subType1.getText();
                entry3 = $effRad1.getText();
                entry4 = $effO1.getText();
            }
        |   EFFECT effType2=UCASE COLON subType2=UCASE COLON effRad2=NUMBER COLON effO2=NUMBER {
                entry1 = $effType2.getText();
                entry2 = $subType2.getText();
                entry3 = $effRad2.getText();
                entry4 = $effO2.getText();
            }
        |   EFFECT effType3=UCASE COLON subType3=UCASE COLON effRad3=NUMBER {
                entry1 = $effType3.getText();
                entry2 = $subType3.getText();
                entry3 = $effRad3.getText();
            }
        |   EFFECT effType4=UCASE COLON subType4=UCASE {
                entry1 = $effType4.getText();
                entry2 = $subType4.getText();
            }
        |   EFFECT effType5=UCASE {
                entry1 = $effType5.getText();
            }
        ;

effect_yx
        returns[int yInt, int xInt]
        :   EFFECT_YX y=NUMBER COLON x=NUMBER {
                $yInt = Integer.parseInt($y.getText());
                $xInt = Integer.parseInt($x.getText());
            }
        ;

dice
        returns[String diceStr]
        :   DICE STRING { $diceStr = $STRING.getText(); }
        |   DICE NUMBER { $diceStr = $NUMBER.getText(); }
        |   DICE UCASE  { $diceStr = $UCASE.getText(); }
        ;

expr
        returns[Expression exprObj]
        :   EXPR char=UCASE COLON func=UCASE COLON oper=EXPR_OPERATORS {
                String rawCh = $char.getText();
                if (rawCh.length() != 1) {
                    String message = "Invalid exression string. expression:" + rawCh + ":" + $func.getText() + ":" + $oper.getText();
                    throw new InvalidTokenFoundDuringParse(message);
                }

                EffectBaseType exp = EffectBaseType.valueOf("EFB_" + $func.getText());
                String operations = $oper.getText();

                $exprObj = new Expression(rawCh.charAt(0), exp, operations);
            }
        ;

msg
        returns[String msgStr]
        :   MSG STRING { $msgStr = $STRING.getText(); }
        ;

effect_block
        returns[Effect effObj]
        @init {
            String effRadStr = "";
            String effParmStr = "";
            EffectEnum effInit = EffectEnum.EF_NONE;
            String diceInit = "";
            int yInit = 0;
            int xInit = 0;
            EffectSubTypeEnum subTypeInit = EffectSubTypeEnum.EST_NONE;
            TimedEffect timedInit = TimedEffect.TMD_NONE;
            ProjectionEnum projInit = ProjectionEnum.PROJ_NONE;
            EffectNourish effNourish = EffectNourish.EN_NONE;
            EffectEnchant effEnchant = EffectEnchant.EE_NONE;
            Summon effSummon = null;
            Stats effStat = Stats.STAT_NONE;
            int powerInit = 0;
            String msgInit = "";
            String visMsgInit = "";
            String timeInit = "";
            Expression exprObj = null;
        }
        @after {
            $effObj = new Effect(effInit, diceInit, yInit, xInit, timedInit, projInit,
                                 effStat, effNourish, effEnchant, effSummon, effRadStr,
                                 effParmStr, powerInit, msgInit, visMsgInit, timeInit,
                                 exprObj);
        }
        :   (power { powerInit = $power.powerInt; })? (
            effect {
                EffectRecord entry = $effect.effectEntry;
                effInit = entry.type();
                subTypeInit = entry.subType();
                timedInit = entry.tmdEff();
                projInit = entry.projType();
                effNourish = entry.nourType();
                effStat = entry.statType();
                effEnchant = entry.effEnc();
                effSummon = entry.summType();
                effRadStr = entry.radiusStr();
                effParmStr = entry.parmStr();
            }
        |   dice { diceInit = $dice.diceStr; }
        |   expr { exprObj = $expr.exprObj; }
        |   effect_yx { yInit = $effect_yx.yInt; xInit = $effect_yx.xInt; }
        |   msg { msgInit = $msg.msgStr; })+
        ;

desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

activation
        returns[Activation activationRecord]
        @init {
            String nameInit = "";
            int indexInit = 0;
            boolean aimInit = false;
            int levelInit = 0;
            int powerInit = 0;
            List<Effect> effectsInit = new ArrayList<>();
            String message = "";
            String descInit = "";
        }
        @after {
            $activationRecord = new Activation(nameInit, indexInit,
                                               aimInit, levelInit,
                                               powerInit, effectsInit,
                                               message, descInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   aim { aimInit = $aim.aimBool; }
        |   level { levelInit = $level.levelInt; }
        |   effect_block {
                effectsInit.add($effect_block.effObj);
                message = $effect_block.effObj.getMessage();
            }
        |   desc { descInit = $desc.descStr; })+
        ;

file
        returns[List<Activation> activations]
        @init {
            $activations = new ArrayList<>();
        }
        :   (activation {
                $activations.add($activation.activationRecord);
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

AIM
        :   'aim:'
        ;

LEVEL
        :   'level:'
        ;

POWER
        :   'power:'
        ;

EFFECT
        :   'effect:'
        ;

EFFECT_YX
        :   'effect-yx:'
        ;

DICE
        :   'dice:'
        ;

EXPR
        :   'expr:'
        ;

MSG
        :   'msg:'
        ;

DESC
        :   'desc:'
        ;

NUMBER
        :   ('0'..'9')+
        ;

UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

COLON
        :   ':'
        ;

EXPR_OPERATORS
        :   ('*' | ' ' | '0'..'9' | '+' | '/')+
        ;

STRING
        :   ('a'..'z' | ' ' | 'A'..'Z' | '0'..'9' | ',' | '{' | '}' | '.' | '%' | '+' | '-' | '\''
            | '$' | '(' | ')' | '!')+
        ;