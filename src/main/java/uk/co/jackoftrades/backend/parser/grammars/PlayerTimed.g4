grammar PlayerTimed;

@header {
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.player.PlayerTimedEffect;
    import uk.co.jackoftrades.middle.player.TimedFailure;
    import uk.co.jackoftrades.middle.player.TimedGrade;
    import uk.co.jackoftrades.middle.player.enums.TimedEffectReasonType;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.ArrayList;
    import java.util.List;
}

name
        returns[TimedEffect nameStr]
        :   NAME UCASE EOL {
                String raw = $UCASE.getText();
                $nameStr = TimedEffect.valueOf("TMD_" + raw);
            }
        ;

desc
        returns[String descStr]
        @init {
            $descStr = "";
        }
        :   DESC ((STRING { $descStr = $STRING.getText(); })
            | (LCASE { $descStr = $LCASE.getText(); })) EOL
        ;

grade
        returns[TimedGrade timedGrade]
        @init {
            int gradeInit = 0;
            ColourType colInit = ColourType.COLOUR_TYPE_DARK;
            int maxInit = 0;
            String nameInit = "";
            String upMsgInit = "";
            String downMsgInit = "";
        }
        @after {
            $timedGrade = new TimedGrade(gradeInit, colInit, maxInit, nameInit, upMsgInit,
                                         downMsgInit);
        }
        :   GRADE EOL {
                String [] parts = $GRADE.getText().split(":");
                // Ignore the first part
                // Second part is a single character which relates to a colour
                colInit = ColourType.findColourType(parts[1].charAt(0));
                // Third part is the maximum turns this can apply to
                maxInit = Integer.parseInt(parts[2]);
                // Next come three strings - Name of the grade,
                // the message when the effect occurs, and the
                // message when it finishes.
                //
                // The third message is optional
                nameInit = parts[3];
                upMsgInit = parts[4];

                if (parts.length > 5)
                    downMsgInit = parts[5];
            }
        ;

onEnd
        returns[String onEndStr]
        :   ON_END STRING EOL { $onEndStr = $STRING.getText(); }
        ;

onIncrease
        returns[String onIncStr]
        :   ON_INCREASE STRING EOL { $onIncStr = $STRING.getText(); }
        ;

onDecrease
        returns[String onDecStr]
        :   ON_DECREASE STRING EOL { $onDecStr = $STRING.getText(); }
        ;

msgt
        returns[MessageType msgType]
        :   MSGT UCASE EOL {
                String raw = "MSG_" + $UCASE.getText();
                $msgType = MessageType.valueOf(raw);
            }
        ;

fail
        returns[TimedFailure failure]
        :   FAIL idx=INTEGER COLON code=UCASE EOL {
                String raw = $code.getText();
                int indexVal = Integer.parseInt($idx.getText());
                TimedEffectReasonType codeRes = TimedEffectReasonType.fromValue(indexVal);

                switch (codeRes) {
                    case TYPE_TIMED_EFFECT:
                        TimedEffect tValue = TimedEffect.valueOf("TMD_" + raw);
                        $failure = new TimedFailure(tValue, codeRes);
                        break;

                    case TYPE_OBJECT_FLAG:
                        ObjectFlag oValue = ObjectFlag.valueOf("OF_" + raw);
                        $failure = new TimedFailure(oValue, codeRes);
                        break;

                    case TYPE_VULN:
                    case TYPE_RESIST:
                        ElementEnum eValue = ElementEnum.valueOf("ELEM_" + raw);
                        $failure = new TimedFailure(eValue, codeRes);
                        break;

                    case TYPE_PLAYER_FLAG:
                        PlayerFlag pValue = PlayerFlag.valueOf("PF_" + raw);
                        $failure = new TimedFailure(pValue, codeRes);
                }
            }
        ;

onBeginEffect
        returns[EffectEnum effObj]
        :   ON_BEGIN_EFFECT UCASE EOL {
                String raw = $UCASE.getText();
                $effObj = EffectEnum.valueOf("EF_" + raw);
            }
        ;

onEndEffect
        returns[EffectEnum eEnum, TimedEffect tEnum]
        :   ON_END_EFFECT eff=UCASE COLON tef=UCASE EOL {
                $eEnum = EffectEnum.valueOf("EF_" + $eff.getText());
                $tEnum = TimedEffect.valueOf("TMD_" + $tef.getText());
            }
        |   ON_END_EFFECT UCASE EOL {
                $eEnum = EffectEnum.valueOf("EF_" + $UCASE.getText());
                $tEnum = TimedEffect.TMD_NONE;
            }
        ;

effectYX
        returns[int y, int x]
        :   EFFECT_YX yInt=INTEGER COLON xInt=INTEGER EOL {
                $y = Integer.parseInt($yInt.getText());
                $x = Integer.parseInt($xInt.getText());
            }
        ;

effectDice
        returns[String diceStr]
        :   EFFECT_DICE INTEGER EOL { $diceStr = $INTEGER.getText(); }
        ;

//effectExpr
//        :   EFFECT_EXPR ANY_CHAR COLON UCASE COLON OPERATION
//        ;

effectMsg
        returns[String effMsg]
        :   EFFECT_MSG STRING EOL {
                $effMsg = $STRING.getText();
            }
        ;

resist
        returns[ElementEnum resEnum]
        :   RESIST UCASE EOL {
                String raw = $UCASE.getText();
                $resEnum = ElementEnum.valueOf("ELEM_" + raw);
            }
        ;

brand
        returns[Brand brandObj]
        :   BRAND UCASE EOL {
                String raw = $UCASE.getText();
                $brandObj = GameConstants.lookupBrandCode(raw);
            }
        ;

slay
        returns[Slay slayObj]
        :   SLAY UCASE EOL {
                String raw = $UCASE.getText();
                $slayObj = GameConstants.lookupSlay(raw);
            }
        ;

flagSynonym
        returns[ObjectFlag oFlag, int synonymous]
        :   FLAG_SYNONYM UCASE COLON INTEGER EOL {
                String raw = $UCASE.getText();
                $oFlag = ObjectFlag.valueOf("OF_" + raw);
                $synonymous = Integer.parseInt($INTEGER.getText());
            }
        ;

lowerBound
        returns[int bound]
        :   LOWER_BOUND INTEGER EOL {
                $bound = Integer.parseInt($INTEGER.getText());
            }
        ;

flags
        returns[String flagStr]
        :   FLAGS UCASE EOL {
                $flagStr = $UCASE.getText();
            }
        ;

playerTimed
        returns[PlayerTimedEffect playerTimedEffect]
        @init {
            TimedEffect nameInit = TimedEffect.TMD_NONE;
            String descInit = "";
            String onEndInit = "";
            String onIncInit = "";
            String onDecInit = "";
            MessageType msgInit = MessageType.MSG_NONE;
            TimedFailure failInit = null;
            List<TimedGrade> gradeInit = new ArrayList<>();
            Effect onBegEffInit = null;
            Effect onEndEffInit = null;
            boolean nonStackInit = false;
            int lowerBoundInit = 0;
            int ofFlagDup = 0;
            ObjectFlag oFlagSynInit = ObjectFlag.OF_NONE;
            ElementEnum resistEnum = ElementEnum.ELEM_NONE;
            Brand brandInit = null;
            Slay slayInit = null;
        }
        @after {
            $playerTimedEffect = new PlayerTimedEffect(nameInit,
                descInit, onEndInit, onIncInit, onDecInit, msgInit,
                failInit, gradeInit, onBegEffInit, onEndEffInit,
                nonStackInit, lowerBoundInit, ofFlagDup,
                oFlagSynInit, resistEnum, brandInit, slayInit);
        }
        :   EOL*
            name {
                nameInit = $name.nameStr;
            }
        (   desc {
                descInit = $desc.descStr;
            }
        |   grade {
                int count = gradeInit.size();
                TimedGrade tmdG = $grade.timedGrade;
                tmdG.setGrade(count);
                gradeInit.add(tmdG);
            }
        |   onEnd {
                onEndInit = $onEnd.onEndStr;
            }
        |   onIncrease {
                onIncInit = $onIncrease.onIncStr;
            }
        |   onDecrease {
                onDecInit = $onDecrease.onDecStr;
            }
        |   msgt {
                msgInit = $msgt.msgType;
            }
        |   fail {
                failInit = $fail.failure;
            }
        |   resist {
                resistEnum = $resist.resEnum;
            }
        |   brand {
                brandInit = $brand.brandObj;
            }
        |   slay {
                slayInit = $slay.slayObj;
            }
        |   flagSynonym {
                oFlagSynInit = $flagSynonym.oFlag;
                ofFlagDup = $flagSynonym.synonymous;
            }
        |   flags {
                nonStackInit = ($flags.flagStr.equals("NONSTACKING"));
            }
        |   lowerBound {
                lowerBoundInit = $lowerBound.bound;
            }
        |   onBeginEffect {
                onBegEffInit = new Effect($onBeginEffect.effObj);
            }
        |   onEndEffect {
                EffectEnum e = $onEndEffect.eEnum;
                TimedEffect t = $onEndEffect.tEnum;
                EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(t);
                onEndEffInit = new Effect(e, wrapper);
            }
//        |   effectYX
        |   effectDice {
                if (onEndEffInit != null)
                    onEndEffInit.setDice($effectDice.diceStr);
            })+ EOL*
        ;

file
        returns[List<PlayerTimedEffect> effects]
        @init {
            $effects = new ArrayList<>();
        }
        :   (playerTimed {
                $effects.add($playerTimed.playerTimedEffect);
            })+ EOL* EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   ' '* '\r'? '\n'
        ;

NAME
        :   'name:'
        ;

DESC
        :   'desc:'
        ;

GRADE
        :   'grade:' ('a'..'z' | 'A'..'Z' | '0'..'9' | ' ' | '!' | ',' | '.' | ':' | '{' | '}')+
        ;

ON_END
        :   'on-end:'
        ;

ON_INCREASE
        :   'on-increase:'
        ;

ON_DECREASE
        :   'on-decrease:'
        ;

MSGT
        :   'msgt:'
        ;

FAIL
        :   'fail:'
        ;

ON_BEGIN_EFFECT
        :   'on-begin-effect:'
        ;

ON_END_EFFECT
        :   'on-end-effect:'
        ;

EFFECT_YX
        :   'effect-yx:'
        ;

EFFECT_DICE
        :   'effect-dice:'
        ;

EFFECT_EXPR
        :   'effect-expr:'
        ;

EFFECT_MSG
        :   'effect-msg:'
        ;

RESIST
        :   'resist:'
        ;

BRAND
        :   'brand:'
        ;

SLAY
        :   'slay:'
        ;

FLAG_SYNONYM
        :   'flag-synonym:'
        ;

LOWER_BOUND
        :   'lower-bound:'
        ;

FLAGS
        :   'flags:'
        ;

ANY_CHAR
        :   ('a'..'z' | 'A'..'Z')
        ;

INTEGER
        :   ('0'..'9')+
        ;

UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

LCASE
        :   ('a'..'z' | ' ')+
        ;

//OPERATION
//        :   ('0'..'9' | ' ' | '/' | '-' | '*' | '+')
//        ;

COLON
        :   ':'
        ;

STRING
        :   ('a'..'z' | 'A'..'Z' | ' ' | '.' | '!' | '-' | '{' | '}' | ',')+
        ;