grammar Shape;

@header {
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlagName;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.enums.ValueEnum;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.Expression;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
    import uk.co.jackoftrades.middle.enums.Projection;
    import uk.co.jackoftrades.middle.player.PlayerShape;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.combat.enums.Element;
    import uk.co.jackoftrades.middle.player.PlayerBlow;
    import uk.co.jackoftrades.middle.Effect;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.monsters.Summon;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

combat
        returns[int tohNum, int todNum, int toaNum]
        :   COMBAT toh=STRING COLON tod=STRING COLON toa=STRING {
                $tohNum = Integer.parseInt($toh.getText());
                $todNum = Integer.parseInt($tod.getText());
                $toaNum = Integer.parseInt($toa.getText());
        };

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

obj_flags
        returns[Flag<ObjectFlagName> oFlags]
        @init {
            $oFlags = new Flag<>(ObjectFlagName.class);
        }
        :   OBJ_FLAGS f1=FLAG { $oFlags.on(ObjectFlagName.valueOf("OF_" + $f1.getText())); }
            (OR f2=FLAG  { $oFlags.on(ObjectFlagName.valueOf("OF_" + $f2.getText())); })*
        ;

player_flags
        returns[Flag<PlayerFlag> pFlags]
        @init {
            $pFlags = new Flag<>(PlayerFlag.class);
        }
        :   PLAYER_FLAGS f1=FLAG { $pFlags.on(PlayerFlag.valueOf("PF_" + $f1.getText())); }
            (OR f2=FLAG { $pFlags.on(PlayerFlag.valueOf("PF_" + $f2.getText())); })*
        ;

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

effect              // Options are CURE, TIMED_INC, <GENERIC>, PROJECT_LOS
        returns[EffectEnum effectEnum, TimedEffect timedEffect, Projection projectionEffect]
        @init {
            $effectEnum = EffectEnum.EF_NONE;
            $timedEffect = TimedEffect.TMD_NONE;
            $projectionEffect = Projection.PROJ_NONE;
        }
        :   EFFECT f1=FLAG {
                $effectEnum = EffectEnum.valueOf("EF_" + $f1.getText());
            }
            (COLON f2=FLAG {
                if ($effectEnum == EffectEnum.EF_CURE || $effectEnum == EffectEnum.EF_TIMED_INC) {
                    $timedEffect = TimedEffect.valueOf("TMD_" + $f2.getText());
                } else {
                    $projectionEffect = Projection.valueOf("PROJ_" + $f2.getText());
                }
            })?
        ;

dice
        returns[String diceStr]
        :   DICE STRING { $diceStr = $STRING.getText(); }
        ;

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

effect_msg
        returns[String effMsgStr]
        :   EFFECT_MSG STRING { $effMsgStr = $STRING.getText(); }
        ;

effect_block
        returns[Effect effObj]
        @init {
            EffectEnum effectEnum = EffectEnum.EF_NONE;
            String diceInit = "";
            int xInit = 0;
            int yInit = 0;
            TimedEffect timedEffect = TimedEffect.TMD_NONE;
            Projection projectionInit = Projection.PROJ_NONE;
            Stats statsInit = Stats.STAT_NONE;
            EffectNourish nourInit = EffectNourish.EN_NONE;
            EffectEnchant encInit = EffectEnchant.EE_NONE;
            Summon summInit = null;
            String radiusInit = "";
            String otherParameter = "";
            String msgInit = "";
            String visMsgInit = "";
            Expression expInit = null;
        }
        @after {
            $effObj = new Effect(effectEnum, diceInit, yInit, xInit,
                                 timedEffect, projectionInit, statsInit,
                                 nourInit, encInit, summInit,
                                 radiusInit, otherParameter, msgInit,
                                 visMsgInit, expInit);
        }
        :   effect {
                effectEnum = $effect.effectEnum;
                timedEffect = $effect.timedEffect;
                projectionInit = $effect.projectionEffect;
            }
        |   effect effect_msg {
                effectEnum = $effect.effectEnum;
                timedEffect = $effect.timedEffect;
                projectionInit = $effect.projectionEffect;
                msgInit = $effect_msg.effMsgStr;
            }
        |   effect dice {
                effectEnum = $effect.effectEnum;
                timedEffect = $effect.timedEffect;
                projectionInit = $effect.projectionEffect;
                diceInit = $dice.diceStr;
            }
        |   effect dice effect_msg {
                effectEnum = $effect.effectEnum;
                timedEffect = $effect.timedEffect;
                projectionInit = $effect.projectionEffect;
                diceInit = $dice.diceStr;
                msgInit = $effect_msg.effMsgStr;
            }
        |   effect dice expr {
                effectEnum = $effect.effectEnum;
                timedEffect = $effect.timedEffect;
                projectionInit = $effect.projectionEffect;
                diceInit = $dice.diceStr;
                expInit = $expr.expression;
            }
        |   effect dice expr effect_msg {
                effectEnum = $effect.effectEnum;
                timedEffect = $effect.timedEffect;
                projectionInit = $effect.projectionEffect;
                diceInit = $dice.diceStr;
                expInit = $expr.expression;
                msgInit = $effect_msg.effMsgStr;
            }
        ;

blow
        returns[String blowStr]
        :   BLOW STRING { $blowStr = $STRING.getText(); }
        ;

shape
        returns[PlayerShape shapeObj]
        @init {
            String nameInit = "";
            int toAcInit = 0;
            int toHitInit = 0;
            int toDamInit = 0;
            Map<PlayerSkill, Integer> skillInit = new HashMap<>();
            Flag<ObjectFlagName> oFlagsInit = new Flag<>(ObjectFlagName.class);
            Flag<PlayerFlag> pFlagsInit = new Flag<>(PlayerFlag.class);
            Map<ValueEnum, Integer> valueModInit = new HashMap<>();
            List<Element> resistInit = new ArrayList<>();
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
                skillInit.put(PlayerSkill.SKILL_STEALTH, $skill_save.skillNum);
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

file
        returns[List<PlayerShape> shapes]
        @init {
            $shapes = new ArrayList<>();
        }
        :   (shape { $shapes.add($shape.shapeObj); })+ EOF
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

LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        ;

COLON
        :   ':'
        ;

OR
        :   ' | '
        ;

FLAG
        :   ('A'..'Z' | '_')+
        ;

STRING
        :   ('a'..'z' | ' ' | 'B' | 'P' | 'S' | '-' | '0'..'9' | '+' | '/' | '$')+
        ;