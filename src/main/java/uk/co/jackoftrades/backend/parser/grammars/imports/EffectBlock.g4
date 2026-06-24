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

/*
 * @Author Rowan Crowther
 *
 * A standalone, domain-class-free parser for the type Effect looking at:
 *   - effect:type[:subtype[:radius[:other parameter]]]
 *   - time:<dice-string>
 *   - effect-yx:<integer>:<integer>
 *   - dice <==> expression couplet. In this case the dice consists of a
 *     complex dice structure, including at least one $<char> variable
 *     placeholder. This links to one or more expression lines, with
 *     each one starting with a character that links to the relevant
 *     $<char> variable placeholder. If there are no expression lines
 *     then the dice is simple, and does not have a $ in it.
 *   - expr:<expressionChar>:<base>:<operation>.
 *   - expr-msg:<free text>
 *
 * It should be noted that different grammar files use different parts of
 * the Effect system, but all should create an Effect object.
 *
 * It will be the job of the calling ANTLR4 grammar to ensure these entries
 * are of appropriate types and confirm that real data has been read in.
 * No EffectEnum, EffectSubTypeWrapper, etc. have been added in, only String
 * values.
 */

parser grammar EffectBlock;
options { tokenVocab = EffectBlockLexer; }

/*
 * @Author Rowan Crowther
 *
 * "effect:<TYPE>[:<SUBTYPE>[:<radius>[:<other parameter>]]]"
 *
 *  - TYPE is an entry in EffectEnum
 *  - SUBTYPE is an EffectSubTypeWrapper value, whose type switches based
 *    upon the subtype of TYPE
 *  - radius is an integer
 *  - other parameter, a (signed) integer which is passed as a second parameter
 *    (after radius) to the relevant command.
 */
effect
        returns[String type, String wrapper, String radius, String other]
        :   EFFECT t=UCASE {
                $type = $t.getText();
            }
            (COLON st=UCASE {
                $wrapper = $st.getText().toUpperCase();
            } (COLON rad=INTEGER {
                $radius = $rad.getText();
            } (COLON oth=INTEGER {
                $other = $oth.getText();
            })?)?)?
        ;

/*
 * @Author Rowan Crowther
 *
 * "time:<dice string>" - the storage of the random string of the
 * time part of the effect. This is a straight dice string with
 * no '$' variable names present.
 */
time
        returns[String timeStr]
        : TIME DICE_SIMPLE_VALUE {
                $timeStr = $DICE_SIMPLE_VALUE.getText();
            }
        ;

/*
 * @Author Rowan Crowther
 *
 * "effect-yx:<y>:<x>" - the range of the effect in the
 * verical (y) and horizontal (x) directions about the effects
 * centre.
 */
effectYX
        returns[String y, String x]
        :   EFFECT_YX yVal=INTEGER COLON xVal=INTEGER {
                $y = $yVal.getText();
                $x = $xVal.getText();
            }
        ;

/*
 * @Author Rowan Crowther
 *
 * "dice:<simple dice string>" or "dice:<complex dice string> <expr>"
 *
 * a simple dice string is one which is based on the full dice string
 * "-<int>+<int>d<int>M<int>"
 * a complex dice string is similar to a simple dice string, but any of
 * the integers can be replaced by a '$B' (or other character) character
 * placeholder for situations where the relevant value is dependent on
 * a monster or player variable.
 *
 * The value of this is dependent on the required expr rule following.
 *
 * See notes on expr below.
 */
dice
        returns[String diceString, String exprChar, String baseName, String operation]
        @init {
            String charHolder = "";
            String baseHolder = "";
            String operHolder = "";
        }
        @after {
            $exprChar  = charHolder;
            $baseName  = baseHolder;
            $operation = operHolder;
        }
        :   DICE ((val=DICE_COMPLEX_VALUE {
                $diceString = $val.getText();
            } (expr {
                if (charHolder.isEmpty()) {
                    charHolder = $expr.exprChar;
                    baseHolder = $expr.baseName;
                    operHolder = $expr.operation;
                } else {
                    charHolder = charHolder + "^" + $expr.exprChar;
                    baseHolder = baseHolder + "^" + $expr.baseName;
                    operHolder = operHolder + "^" + $expr.operation;
                }
            })+)
            |   val=DICE_SIMPLE_VALUE {
                $diceString = $val.getText();
            })
        ;
/*
 * @Author Rowan Crowther
 *
 * "expr:<letter>:<BASE_NAME>:<operation>"
 *
 * Letter is a character which links into the preceding dice token
 * for example, a B character here, would relate to a complex dice
 * string of the form "$B+2d6"
 *
 * BASE_NAME is the name from the EffectBaseType enum, which states
 * which base quantity to use
 *
 * operation is a (operand integer)* space delimited string
 */
expr
        returns[String exprChar, String baseName, String operation]
        :   EXPR ch=EXPR_CHAR EXPR_COLON base=EXPR_UCASE EXPR_COLON op=EXPR_OP {
                $exprChar = $ch.getText();
                $baseName = $base.getText();
                $operation = $op.getText();
            }
        ;

/*
 * @Author Rowan Crowther
 *
 * "expr:<letter>:<BASE_NAME>:<operation>"
 *
 */
effectMsg
        returns[String message]
        :   EFFECT_MESSAGE FREE_TEXT { $message = $FREE_TEXT.getText(); }
        ;

// @Author ClaudeCode
// Groups one effect: line with its optional dice:/expr: lines - the most
// common/general shape across the 7 surveyed files (matches Curse.g4's
// actual dice/expr optionality, and is what TrapGrammar.g4's own
// effectBlock should look like per that file's own documented problem
// #4). Does not cover Shape.g4's effect_msg or Activations.g4's power/
// effect_yx/msg interleaving - those directive types aren't tokenized by
// this module at all (by design - see top-of-file comment), so a consumer
// needing them must override this rule entirely rather than extend it.

// @Author RowanCrowther
// As there can be multiple expressions per effect, this has been changed
// into three caret deliminated strings, one for each of the expression char
// the expression base name, and the expression operation.
effectBlock
        returns[String typeInit, String subtypeWrapperInit, String radius, String other,
                String diceString, String yVal, String xVal, String expressionChars, String expressionBase,
                String expressionOperation, String timeDiceString, String effectMessage]
        @init {
            String expressionString = "";
            String baseString = "";
            String opString = "";
        }
        @after {
            $expressionChars = expressionString;
            $expressionBase = baseString;
            $expressionOperation = opString;
        }
        :   effect {
                $typeInit = $effect.type;
                $subtypeWrapperInit = $effect.wrapper;
                $radius = $effect.radius;
                $other = $effect.other;
            }
            ((effectYX {
                $yVal = $effectYX.y;
                $xVal = $effectYX.x;
            }) | ((dice {
                $diceString = $dice.diceString;
                expressionString = $dice.exprChar;
                baseString = $dice.baseName;
                opString = $dice.operation;
            })?))
            (time {
                $timeDiceString = $time.timeStr;
            })?
            (effectMsg { $effectMessage = $effectMsg.message; })?
        ;