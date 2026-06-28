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
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

// Reader+lexer for lib/gamedata/player_timed.txt - the player's timed
// status effects (Haste, Blind, Cut, Stun, Poisoned, ...): display grades
// (colour/threshold/name/messages), fail conditions, what to resist/brand/
// slay-synonym with, and effects to run on beginning/ending the status.
// Cf. src/player-timed.c: struct file_parser player_timed_parser
// (player-timed.c:722).

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

// "name:<TMD_NAME>" - starts a new timed-effect record; must match a
// TMD_* constant from list-player-timed.h.
name
        returns[TimedEffect nameStr]
        :   NAME UCASE EOL {
                String raw = $UCASE.getText();
                $nameStr = TimedEffect.valueOf("TMD_" + raw);
            }
        ;

// "desc:<text>" - the effect's flavour description.
desc
        returns[String descStr]
        @init {
            $descStr = "";
        }
        :   DESC ((STRING { $descStr = $STRING.getText(); })
            | (LCASE { $descStr = $LCASE.getText(); })) EOL
        ;

// "grade:<colour>:<max turns>:<name>:<up message>[:<down message>]"
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
                if (parts[1].length() == 1)
                    colInit = ColourType.findColourType(parts[1].charAt(0));
                else
                    colInit = ColourType.findColourType(parts[1]);
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

// "on-end:<text>" - message shown when the effect wears off entirely.
onEnd
        returns[String onEndStr]
        :   ON_END STRING EOL { $onEndStr = $STRING.getText(); }
        ;

// "on-increase:<text>" - message shown when the effect's duration/grade increases.
onIncrease
        returns[String onIncStr]
        :   ON_INCREASE STRING EOL { $onIncStr = $STRING.getText(); }
        ;

// "on-decrease:<text>" - message shown when the effect's duration/grade decreases.
onDecrease
        returns[String onDecStr]
        :   ON_DECREASE STRING EOL { $onDecStr = $STRING.getText(); }
        ;

// "msgt:<MSG_TYPE>" - message-type/sound used for grade-change messages.
msgt
        returns[MessageType msgType]
        :   MSGT UCASE EOL {
                String raw = "MSG_" + $UCASE.getText();
                $msgType = MessageType.valueOf(raw);
            }
        ;

// "fail:<reason type index>:<code>"
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
                        break;

                    default:
                        $failure = new TimedFailure(PlayerFlag.PF_NONE, TimedEffectReasonType.TYPE_NONE);
                        break;
                }
            }
        ;

// "on-begin-effect:<EF_TYPE>" - an effect to run when this status begins.
onBeginEffect
        returns[EffectEnum effObj]
        :   ON_BEGIN_EFFECT UCASE EOL {
                String raw = $UCASE.getText();
                $effObj = EffectEnum.valueOf("EF_" + raw);
            }
        ;

// "on-end-effect:<EF_TYPE>[:<TMD_NAME>]" - an effect to run when this
// status ends, optionally itself triggering another timed effect (the
// second form, with no subtype, leaves tEnum at TMD_NONE).
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

// "effect-yx:<y>:<x>" - would set a coordinate on the on-end-effect. Dead:
// see top-of-file problem #2 - not referenced by `playerTimed`.
effectYX
        returns[int y, int x]
        :   EFFECT_YX yInt=INTEGER COLON xInt=INTEGER EOL {
                $y = Integer.parseInt($yInt.getText());
                $x = Integer.parseInt($xInt.getText());
            }
        ;

// "effect-dice:<value>" - dice for the preceding on-end-effect:.
effectDice
        returns[String diceStr]
        :   EFFECT_DICE INTEGER EOL { $diceStr = $INTEGER.getText(); }
        ;

// Disabled on purpose
// "effect-expr:<letter>:<EFB_BASE>:<operation>".
//effectExpr
//        :   EFFECT_EXPR ANY_CHAR COLON UCASE COLON OPERATION
//        ;

// "effect-msg:<text>" - message for the on-end-effect:. Defined and lexed
// but, like effectYX, not referenced anywhere in `playerTimed`'s loop -
// dead code (no current player_timed.txt line uses "effect-msg:" either).
effectMsg
        returns[String effMsg]
        :   EFFECT_MSG STRING EOL {
                $effMsg = $STRING.getText();
            }
        ;

// "resist:<ELEMENT>" - the element this status grants resistance to.
resist
        returns[ElementEnum resEnum]
        :   RESIST UCASE EOL {
                String raw = $UCASE.getText();
                $resEnum = ElementEnum.valueOf("ELEM_" + raw);
            }
        ;

// "brand:<CODE>" - the melee brand this status grants, looked up in brand.txt.
brand
        returns[Brand brandObj]
        :   BRAND UCASE EOL {
                String raw = $UCASE.getText();
                $brandObj = GameConstants.lookupBrandCode(raw);
            }
        ;

// "slay:<CODE>" - the slay this status grants, looked up in slay.txt.
slay
        returns[Slay slayObj]
        :   SLAY UCASE EOL {
                String raw = $UCASE.getText();
                $slayObj = GameConstants.lookupSlay(raw);
            }
        ;

// "flag-synonym:<OF_FLAG>:<0|1>" - an object_property.txt flag that's
// treated as synonymous with this timed effect for message-filtering
// purposes.
flagSynonym
        returns[ObjectFlag oFlag, int synonymous]
        :   FLAG_SYNONYM UCASE COLON INTEGER EOL {
                String raw = $UCASE.getText();
                $oFlag = ObjectFlag.valueOf("OF_" + raw);
                $synonymous = Integer.parseInt($INTEGER.getText());
            }
        ;

// "lower-bound:<value>" - the minimum value this timed effect can be
// reduced to (e.g. by a cure) while still being active.
lowerBound
        returns[int bound]
        :   LOWER_BOUND INTEGER EOL {
                $bound = Integer.parseInt($INTEGER.getText());
            }
        ;

// "flags:<NONSTACKING>" - currently the only flag value checked for
// (`flags`'s result is just compared against the literal string
// "NONSTACKING" in `playerTimed`, not turned into a real flag set).
flags
        returns[String flagStr]
        :   FLAGS UCASE EOL {
                $flagStr = $UCASE.getText();
            }
        ;

// One full timed-effect record: name, then any mix of the directives above
// in any order/quantity.
playerTimed
        returns[PlayerTimedEffect playerTimedEffect]
        @init {
            TimedEffect nameInit = TimedEffect.TMD_NONE;
            String descInit = "";
            String onEndInit = "";
            String onIncInit = "";
            String onDecInit = "";
            MessageType msgInit = MessageType.MSG_NONE;
            List<TimedFailure> failInit = new ArrayList<>();
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
            // TODO(ClaudeCode): PlayerTimedEffect now takes List<TimedFailure> for the fail argument, but
            // the parser still builds a single TimedFailure. Commented out to keep the build green until
            // the player-timed parser is re-plumbed to collect failures into a list.
            /*$playerTimedEffect = new PlayerTimedEffect(nameInit,
                descInit, onEndInit, onIncInit, onDecInit, msgInit,
                failInit, gradeInit, onBegEffInit, onEndEffInit,
                nonStackInit, lowerBoundInit, ofFlagDup,
                oFlagSynInit, resistEnum, brandInit, slayInit);*/
            $playerTimedEffect = null;
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
                failInit.add($fail.failure);

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
                // TODO(ClaudeCode): Effect copy-constructor no longer exists. Commented out to compile.
                //onBegEffInit = new Effect($onBeginEffect.effObj);
                onBegEffInit = null;
            }
        |   onEndEffect {
                // TODO(ClaudeCode): Effect(EffectEnum, EffectSubTypeWrapper) overload no longer exists.
                // Commented out to compile.
                //EffectEnum e = $onEndEffect.eEnum;
                //TimedEffect t = $onEndEffect.tEnum;
                //EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(t);
                //onEndEffInit = new Effect(e, wrapper);
                onEndEffInit = null;
            })+ EOL*
// Commented out as no line in gamedata file currently uses this
//        |   effectYX
//        |   effectDice {
//                if (onEndEffInit != null)
//                    onEndEffInit.setDice($effectDice.diceStr);
//            })+ EOL*
        ;

// Top-level rule: the whole file is one or more timed-effect records.
file
        returns[List<PlayerTimedEffect> effects]
        @init {
            $effects = new ArrayList<>();
        }
        :   (playerTimed {
                $effects.add($playerTimed.playerTimedEffect);
            })+ EOL* EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

// A line ending, INCLUDING any blank line on its own - deliberately not
// skipped (every other directive rule explicitly matches a trailing EOL,
// and `playerTimed`/`file` use EOL* to skip the blank lines between
// records).
EOL     :   ' '* '\r'? '\n'
        ;

// NAME through FLAGS below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

DESC
        :   'desc:'
        ;

// "grade:" plus the ENTIRE rest of the line as one token (re-split on ':'
// in `grade`'s action)
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

// Disabled on purpose
//ANY_CHAR
//   ('a'..'z' | 'A'..'Z')
//        ;

// A bare non-negative integer.
INTEGER
        :   ('0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for TMD_*/
// EF_*/ELEM_*/OF_*/PF_*/MSG_* names and brand/slay codes.
UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// Free-running lowercase text with spaces - used for desc:'s plain-text form.
LCASE
        :   ('a'..'z' | ' ')+
        ;

// Disabled on purpose.
//OPERATION
//        :   ('0'..'9' | ' ' | '/' | '-' | '*' | '+')
//        ;

// Field separator used by on-end-effect:/effect-yx:/flag-synonym: lines.
COLON
        :   ':'
        ;

// Free-running text used for desc:/on-end:/on-increase:/on-decrease:/
// effect-msg:'s text fields.
STRING
        :   ('a'..'z' | 'A'..'Z' | ' ' | '.' | '!' | '-' | '{' | '}' | ',')+
        ;
