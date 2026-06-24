// Parser+lexer for lib/gamedata/activation.txt - every activatable effect
// usable by random artifacts and rods/staves/wands (CURE_POISON,
// CURE_BLINDNESS, ...): whether it needs aiming, difficulty level, power
// (for object-power calc and recharge time), the effect itself (with dice/
// expr/coordinates/message), and a description. Cf. src/obj-init.c:
// struct file_parser act_parser (obj-init.c:1678), directive table around
// obj-init.c:1600-1630, and finish_parse_act (obj-init.c:1635) which
// assigns each activation a sequential index used for array-based lookups
// elsewhere in the engine.

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
    import uk.co.jackoftrades.middle.effect.Earthquake;
    import uk.co.jackoftrades.middle.enums.GlyphType;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.Activation;

    import java.util.List;
    import java.util.ArrayList;
}

// "name:<CODE>" - starts a new activation record; the code other data
// files (artifact.txt, ego_item.txt) reference to grant the activation.
name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

// "aim:0|1" - whether this activation requires aiming.
aim
        returns[boolean aimBool]
        :   AIM NUMBER { $aimBool = $NUMBER.getText().equals("1"); }
        ;

// "level:<value>" - activation difficulty.
level
        returns[int levelInt]
        :   LEVEL NUMBER { $levelInt = Integer.parseInt($NUMBER.getText()); }
        ;

// "power:<value>" - relative power for object-power calc / recharge time.
// Matched as a prefix inside `effect_block` (see top-of-file problem #1
// re: this value never reaching `activation`'s own power field).
power
        returns[int powerInt]
        :   POWER NUMBER { $powerInt = Integer.parseInt($NUMBER.getText()); }
        ;

// "effect:<TYPE>[:<SUBTYPE>[:<radius>[:<other>]]]" - the activation's
// effect; one alternative per field count (1-4).
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

                case EST_EARTHQUAKE:
                    $wrapper = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + entry2));
                    break;

                case EST_GLYPH:
                    $wrapper = new EffectSubTypeWrapper(GlyphType.valueOf("GLYPH_" + entry2));
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

// "effect-yx:<y>:<x>" - sets a coordinate on the effect.
effect_yx
        returns[int yInt, int xInt]
        :   EFFECT_YX y=NUMBER COLON x=NUMBER {
                $yInt = Integer.parseInt($y.getText());
                $xInt = Integer.parseInt($x.getText());
            }
        ;

// "dice:<dice string>" - dice for the effect; accepts free text, a bare
// number, or an all-caps token (covering plain numeric dice as well as
// $-variable-free forms tokenized under UCASE/STRING).
dice
        returns[String diceStr]
        :   DICE STRING { $diceStr = $STRING.getText(); }
        |   DICE NUMBER { $diceStr = $NUMBER.getText(); }
        |   DICE UCASE  { $diceStr = $UCASE.getText(); }
        ;

// "expr:<letter>:<EFB_BASE>:<operation>" - binds a dice-string variable
// used in the preceding dice: line.
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

// "msg:<text>" - activation message.
msg
        returns[String msgStr]
        :   MSG STRING { $msgStr = $STRING.getText(); }
        ;

// Groups an optional leading power: line with one-or-more of effect:/
// dice:/expr:/effect-yx:/msg: (any order) into a single Effect. See top-of-
// file problem #1 re: this rule's own (correctly parsed) `powerInit` never
// propagating up to `activation`'s separate, same-named field.
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
        @after {                 // Hardcoded in values prior to adding in EffectBlock import
            $effObj = new Effect(effInit, null, diceInit, yInit, xInit,
                                 subTypeInit, wrapper, effRadStr,
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

// "desc:<text>" - activation description.
desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

// One full activation record: name, then any mix of aim/level/effect_block/
// desc in any order/quantity.
activation
        returns[Activation activationRecord]
        @init {
            String nameInit = "";
            // BUG: never reassigned below - every Activation ends up with
            // index == 0. See top-of-file problem #2.
            int indexInit = 0;
            boolean aimInit = false;
            int levelInit = 0;
            // BUG: never reassigned below either, despite effect_block correctly
            // parsing its own (differently-scoped) powerInit from the leading
            // power: line. See top-of-file problem #1.
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
        |   power { powerInit = $power.powerInt; }
        |   level { levelInit = $level.levelInt; }
        |   effect_block {
                effectsInit.add($effect_block.effObj);
                message = $effect_block.effObj.getMessage();
            }
        |   desc { descInit = $desc.descStr; })+
        ;

// Top-level rule: the whole file is one or more activation records.
file
        returns[List<Activation> activations]
        @init {
            $activations = new ArrayList<>();
        }
        :   (activation {
                $activations.add($activation.activationRecord);
            })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
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

// A bare non-negative integer.
NUMBER
        :   ('0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for name:/
// effect:'s type and subtype segments, and one of dice:'s forms.
UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// Field separator within effect:/effect-yx:/expr: lines.
COLON
        :   ':'
        ;

// The operator/operand text for an expr: line's trailing operation, e.g.
// "/ 2" or "+ 1".
EXPR_OPERATORS
        :   ('*' | ' ' | '0'..'9' | '+' | '/')+
        ;

// Free-running general-purpose text - used for dice:/msg:/desc:'s text
// fields and effect:'s "other parameter" segment.
STRING
        :   ('a'..'z' | ' ' | 'A'..'Z' | '0'..'9' | ',' | '{' | '}' | '.' | '%' | '+' | '-' | '\''
            | '$' | '(' | ')' | '!')+
        ;