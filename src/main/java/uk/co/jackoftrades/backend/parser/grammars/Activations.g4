grammar Activations;

@header {
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
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
        returns[EffectEnum type, EffectSubTypeEnum subType,
                EffectSubTypeWrapper wrapper, String radiusStr, String parmStr]
        @init {
            $type = EffectEnum.EF_NONE;
            $radiusStr = "";
            $parmStr = "";
            $wrapper = null;

            String entry1 = "";
            String entry2 = "";
            String entry3 = "";
            String entry4 = "";
        }
        @after {
            $type = EffectEnum.valueOf("EF_" + entry1);
            $subType = $type.getSubType();

            switch($subType) {
                case EST_PROJ:
                    $wrapper = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + entry2));
                    break;

                case EST_TMD:
                    $wrapper = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + entry2));
                    break;

                case EST_STAT:
                    $wrapper = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + entry2));
                    break;

                case EST_NOURISH:
                    $wrapper = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + entry2));
                    break;

                case EST_ENCHANT:
                    $wrapper = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + entry2));
                    break;

                case EST_SUMMON:
                    $wrapper = new EffectSubTypeWrapper(GameConstants.lookupSummon(entry2));
                    break;
            }

            $radiusStr = entry3;
            $parmStr = entry4;
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
            EffectSubTypeWrapper wrapper = null;
            EffectSubTypeEnum subTypeInit = EffectSubTypeEnum.EST_NONE;
            int powerInit = 0;
            String msgInit = "";
            String visMsgInit = "";
            String timeInit = "";
            Expression exprObj = null;
        }
        @after {
            $effObj = new Effect(effInit, diceInit, yInit, xInit,
                                 wrapper, effRadStr,
                                 effParmStr, powerInit, msgInit, visMsgInit, timeInit,
                                 exprObj);
        }
        :   (power { powerInit = $power.powerInt; })? (
            effect {
                effInit = $effect.type;
                subTypeInit = $effect.subType;
                wrapper = $effect.wrapper;
                effRadStr = $effect.radiusStr;
                effParmStr = $effect.parmStr;
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