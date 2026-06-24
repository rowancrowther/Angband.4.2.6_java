// Parser+lexer for lib/gamedata/shape.txt - player shapechange forms
// (werewolf, bear, etc): combat/skill modifiers, gained object/player
// flags and resistances, an on-shapechange effect, and bonus melee blows.
// Cf. src/init.c: struct file_parser shape_parser (init.c:3335), directive
// table registered in init_parse_shape() (init.c:3278-3300):
// name/combat/skill-*/obj-flags/player-flags/values/effect/effect-yx/dice/
// expr/effect-msg/blow -> parse_shape_*.

grammar Shape;

@header {
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.enums.ValueEnum;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.player.PlayerShape;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum
    import uk.co.jackoftrades.middle.player.PlayerBlow;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.monsters.Summon;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

// "name:<shape name>" - starts a new shape record.
name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

// "combat:<to-hit>:<to-dam>:<to-ac>" - cf. parse_shape_combat (init.c:2980).
combat
        returns[int tohNum, int todNum, int toaNum]
        :   COMBAT toh=STRING COLON tod=STRING COLON toa=STRING {
                $tohNum = Integer.parseInt($toh.getText());
                $todNum = Integer.parseInt($tod.getText());
                $toaNum = Integer.parseInt($toa.getText());
        };

// "skill-<name>:<modifier>" family (skill_disarm_phys through skill_dig) -
// per-skill birth modifiers gained from this shapechange. One near-
// identical rule per skill name in the data file.
skill_disarm_phys
        returns[int skillNum]
        :   SKILL_DISARM_PHYS STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

skill_disarm_magic
        returns[int skillNum]
        :   SKILL_DISARM_MAGIC STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

skill_save
        returns[int skillNum]
        :   SKILL_SAVE STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

skill_stealth
        returns[int skillNum]
        :   SKILL_STEALTH STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

skill_search
        returns[int skillNum]
        :   SKILL_SEARCH STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

skill_melee
        returns[int skillNum]
        :   SKILL_MELEE STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

skill_throw
        returns[int skillNum]
        :   SKILL_THROW STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

skill_dig
        returns[int skillNum]
        :   SKILL_DIG STRING { $skillNum = Integer.parseInt($STRING.getText()); }
        ;

// "obj-flags:<OF_FLAG> [| <OF_FLAG> ...]" - object flags gained from this
// shapechange - cf. parse_shape_obj_flags.
obj_flags
        returns[Flag<ObjectFlag> oFlags]
        @init {
            $oFlags = new Flag<>(ObjectFlag.class);
        }
        :   OBJ_FLAGS f1=FLAG { $oFlags.on(ObjectFlag.valueOf("OF_" + $f1.getText())); }
            (OR f2=FLAG  { $oFlags.on(ObjectFlag.valueOf("OF_" + $f2.getText())); })*
        ;

// "player-flags:<PF_FLAG> [| <PF_FLAG> ...]" - player flags gained from this
// shapechange - cf. parse_shape_play_flags.
player_flags
        returns[Flag<PlayerFlag> pFlags]
        @init {
            $pFlags = new Flag<>(PlayerFlag.class);
        }
        :   PLAYER_FLAGS f1=FLAG { $pFlags.on(PlayerFlag.valueOf("PF_" + $f1.getText())); }
            (OR f2=FLAG { $pFlags.on(PlayerFlag.valueOf("PF_" + $f2.getText())); })*
        ;

// "values:<CV_MODIFIER>[<value>] [| ...]" - resistances/modifiers gained
// from this shapechange - cf. parse_shape_values.
values
        returns[Map <ValueEnum, Integer> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   VALUES v1=FLAG LBRACKET val1=STRING RBRACKET {
                ValueEnum v1Enum = ValueEnum.valueOf("CV_" + $v1.getText());
                int val1Num = Integer.parseInt($val1.getText());
                $valueMap.put(v1Enum, val1Num);
            } (OR v2=FLAG LBRACKET val2=STRING RBRACKET {
                ValueEnum v2Enum = ValueEnum.valueOf("CV_" + $v2.getText());
                int val2Num = Integer.parseInt($val2.getText());
                $valueMap.put(v2Enum, val2Num);
            })*
        ;

// "effect:<TYPE>[:<SUBTYPE>]" - the on-shapechange effect.
effect
        returns[EffectEnum effectEnum, EffectSubTypeWrapper wrapper]
        @init {
            $wrapper = null;
        }
        :   EFFECT f1=FLAG {
                $effectEnum = EffectEnum.valueOf("EF_" + $f1.getText());
            }
            (COLON f2=FLAG {
                // PROBLEM: only EF_CURE/EF_TIMED_INC are special-cased; every
                // other effect type's subtype is assumed to be a
                // ProjectionEnum. See top-of-file problem #2.
                if ($effectEnum == EffectEnum.EF_CURE || $effectEnum == EffectEnum.EF_TIMED_INC) {
                    $wrapper = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + $f2.getText()));
                } else {
                    $wrapper = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + $f2.getText()));
                }
            })?
        ;

// "dice:<dice string>" - dice for the effect: line - cf. parse_shape_dice.
dice
        returns[String diceStr]
        :   DICE STRING { $diceStr = $STRING.getText(); }
        ;

// "expr:<letter>:<EFB_BASE>:<operation>" - binds a dice-string variable used
// in the dice: line - cf. parse_shape_expr.
expr
        returns[Expression expression]
        @init {
            char chInit;
            EffectBaseType baseInit;
            String operationInit;
        }
        @after {
            $expression = new Expression(chInit, baseInit, operationInit);
        }
        :   EXPR ch=FLAG COLON base=FLAG COLON st=STRING {
            String rawCh = $ch.getText();
            if (rawCh.length() != 1)
                throw new IllegalArgumentException("Expression character code may only have one character.");

            chInit = rawCh.charAt(0);

            baseInit = EffectBaseType.valueOf("EFB_" + $base.getText());
            operationInit = $st.getText();
        };

// "effect-msg:<text>" - message shown when the effect: triggers - cf.
// parse_shape_effect_msg.
effect_msg
        returns[String effMsgStr]
        :   EFFECT_MSG STRING { $effMsgStr = $STRING.getText(); }
        ;

// Groups one effect: line with whichever of dice:/expr:/effect-msg: follow
// it into a single Effect - one alternative per combination actually seen
// in shape.txt (no expr-without-dice, no effect-msg-without-effect, etc).
effect_block
        returns[Effect effObj]
        @init {
            EffectEnum effectEnum = EffectEnum.EF_NONE;
            String diceInit = "";
            int xInit = 0;
            int yInit = 0;
            EffectSubTypeWrapper value = null;
            String radiusInit = "";
            String otherParameter = "";
            String msgInit = "";
            String visMsgInit = "";
            Expression expInit = null;
        }
        @after {
            $effObj = new Effect(effectEnum, diceInit, yInit, xInit,
                                 value,
                                 radiusInit, otherParameter, msgInit,
                                 visMsgInit, expInit);
        }
        :   effect {
                effectEnum = $effect.effectEnum;
                value = $effect.wrapper;
            }
        |   effect effect_msg {
                effectEnum = $effect.effectEnum;
                value = $effect.wrapper;
                msgInit = $effect_msg.effMsgStr;
            }
        |   effect dice {
                effectEnum = $effect.effectEnum;
                value = $effect.wrapper;
                diceInit = $dice.diceStr;
            }
        |   effect dice effect_msg {
                effectEnum = $effect.effectEnum;
                value = $effect.wrapper;
                diceInit = $dice.diceStr;
                msgInit = $effect_msg.effMsgStr;
            }
        |   effect dice expr {
                effectEnum = $effect.effectEnum;
                value = $effect.wrapper;
                diceInit = $dice.diceStr;
                expInit = $expr.expression;
            }
        |   effect dice expr effect_msg {
                effectEnum = $effect.effectEnum;
                value = $effect.wrapper;
                diceInit = $dice.diceStr;
                expInit = $expr.expression;
                msgInit = $effect_msg.effMsgStr;
            }
        ;

// "blow:<verb>" - one bonus melee blow this shape grants; can repeat (see
// `shape`'s blowInit list) for shapes with multiple natural attacks - cf.
// parse_shape_blow.
blow
        returns[String blowStr]
        :   BLOW STRING { $blowStr = $STRING.getText(); }
        ;

// One full shape record: name, then any mix of combat/skills/flags/values/
// effect/blow in the order they appear in the file.
shape
        returns[PlayerShape shapeObj]
        @init {
            String nameInit = "";
            int toAcInit = 0;
            int toHitInit = 0;
            int toDamInit = 0;
            Map<PlayerSkill, Integer> skillInit = new HashMap<>();
            Flag<ObjectFlag> oFlagsInit = new Flag<>(ObjectFlag.class);
            Flag<PlayerFlag> pFlagsInit = new Flag<>(PlayerFlag.class);
            Map<ValueEnum, Integer> valueModInit = new HashMap<>();
            List<ElementEnum> resistInit = new ArrayList<>();
            Effect effectInit = null;
            int numBlows = 0;
            List<PlayerBlow> blowInit = new ArrayList<>();
        }
        @after {
            $shapeObj = new PlayerShape(nameInit, toAcInit, toHitInit, toDamInit,
                                        skillInit, oFlagsInit, pFlagsInit,
                                        valueModInit, resistInit,
                                        effectInit, blowInit.size(), blowInit);
        }
        :   name {
                nameInit = $name.nameStr;
            }
        (   combat {
                toAcInit  = $combat.toaNum;
                toDamInit = $combat.todNum;
                toHitInit = $combat.tohNum;
            }
        |   skill_disarm_phys {
                skillInit.put(PlayerSkill.SKILL_DISARM_PHYS, $skill_disarm_phys.skillNum);
            }
        |   skill_disarm_magic {
                skillInit.put(PlayerSkill.SKILL_DISARM_MAGIC, $skill_disarm_magic.skillNum);
            }
        |   skill_save {
                skillInit.put(PlayerSkill.SKILL_SAVE, $skill_save.skillNum);
            }
        |   skill_stealth {
                skillInit.put(PlayerSkill.SKILL_STEALTH, $skill_stealth.skillNum);
            }
        |   skill_search {
                skillInit.put(PlayerSkill.SKILL_SEARCH, $skill_search.skillNum);
            }
        |   skill_melee {
                skillInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, $skill_melee.skillNum);
            }
        |   skill_throw {
                skillInit.put(PlayerSkill.SKILL_TO_HIT_THROW, $skill_throw.skillNum);
            }
        |   skill_dig {
                skillInit.put(PlayerSkill.SKILL_DIGGING, $skill_dig.skillNum);
            }
        |   obj_flags {
                oFlagsInit.union($obj_flags.oFlags);
            }
        |   player_flags {
                pFlagsInit.union($player_flags.pFlags);
            }
        |   values { valueModInit.putAll($values.valueMap); }
        |   effect_block { effectInit = $effect_block.effObj; }
        |   blow { blowInit.add(new PlayerBlow($blow.blowStr));  }
        )*;

// Top-level rule: the whole file is one or more shape records.
file
        returns[List<PlayerShape> shapes]
        @init {
            $shapes = new ArrayList<>();
        }
        :   (shape { $shapes.add($shape.shapeObj); })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

COMBAT
        :   'combat:'
        ;

SKILL_DISARM_PHYS
        :   'skill-disarm-phys:'
        ;

SKILL_DISARM_MAGIC
        :   'skill-disarm-magic:'
        ;

SKILL_SAVE
        :   'skill-save:'
        ;

SKILL_STEALTH
        :   'skill-stealth:'
        ;

SKILL_SEARCH
        :   'skill-search:'
        ;

SKILL_MELEE
        :   'skill-melee:'
        ;

SKILL_THROW
        :   'skill-throw:'
        ;

SKILL_DIG
        :   'skill-dig:'
        ;

OBJ_FLAGS
        :   'obj-flags:'
        ;

PLAYER_FLAGS
        :   'player-flags:'
        ;

VALUES
        :   'values:'
        ;

EFFECT
        :   'effect:'
        ;

DICE
        :   'dice:'
        ;

EXPR
        :   'expr:'
        ;

EFFECT_MSG
        : 'effect-msg:'
        ;

BLOW
        :   'blow:'
        ;

// Brackets around a values: modifier's integer argument, e.g. "[1]".
LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        ;

// Field separator used throughout most directives.
COLON
        :   ':'
        ;

// The " | " separator between flag/value names on an obj-flags:/
// player-flags:/values: line.
OR
        :   ' | '
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for flag/effect/
// value names.
FLAG
        :   ('A'..'Z' | '_')+
        ;

// Free-running text used for name:/combat:/skill-*:/dice:/expr:'s text
// fields - lowercase letters, spaces, a handful of dice-string characters
// (the stray 'B'/'P'/'S' letters and '$' support things like "$B" dice
// variables appearing inside a STRING-typed field).
STRING
        :   ('a'..'z' | ' ' | 'B' | 'P' | 'S' | '-' | '0'..'9' | '+' | '/' | '$')+
        ;