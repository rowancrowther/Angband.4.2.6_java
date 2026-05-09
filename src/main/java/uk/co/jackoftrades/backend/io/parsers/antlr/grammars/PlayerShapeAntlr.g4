grammar PlayerShapeAntlr;

@header {
    import java.util.ArrayList;
    import java.util.HashMap;

    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.Effect;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.ValueEnum;
    import uk.co.jackoftrades.middle.Expression;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.player.PlayerBlow;
    import uk.co.jackoftrades.middle.player.PlayerShape;
}

name
        returns[String shapeName]
        :   NAME TEXT {
            $shapeName = $TEXT.getText();
        };

combat
        returns[int shapeToHit, int shapeToDam, int shapeToAC]
        :   COMBAT toHit=TEXT COLON toDam=TEXT COLON toAC=TEXT {
            $shapeToHit = Integer.parseInt($toHit.getText());
            $shapeToDam = Integer.parseInt($toDam.getText());
            $shapeToAC = Integer.parseInt($toAC.getText());
        };

skillDisarmPhys
        returns[int shapeDisarmPhys]
        :   SKILL_DISARM_PHYS TEXT {
            $shapeDisarmPhys = Integer.parseInt($TEXT.getText());
        };

skillDisarmMagic
        returns[int shapeDisarmMagic]
        :   SKILL_DISARM_MAGIC TEXT {
            $shapeDisarmMagic = Integer.parseInt($TEXT.getText());
        };

skillSave
        returns[int shapeSave]
        :   SKILL_SAVE TEXT {
            $shapeSave = Integer.parseInt($TEXT.getText());
        };

skillStealth
        returns[int shapeStealth]
        :   SKILL_STEALTH TEXT {
            $shapeStealth = Integer.parseInt($TEXT.getText());
        };

skillSearch
        returns[int shapeSearch]
        :   SKILL_SEARCH TEXT {
            $shapeSearch = Integer.parseInt($TEXT.getText());
        };

skillMelee
        returns[int shapeMelee]
        :   SKILL_MELEE TEXT {
            $shapeMelee = Integer.parseInt($TEXT.getText());
        };

skillThrow
        returns[int shapeThrow]
        :   SKILL_THROW TEXT {
            $shapeThrow = Integer.parseInt($TEXT.getText());
        };

skillDig
        returns[int shapeDig]
        :   SKILL_DIG TEXT {
            $shapeDig = Integer.parseInt($TEXT.getText());
        };

objFlags
        returns[ArrayList<ObjectFlag> objectFlags]
        @init {
            $objectFlags = new ArrayList<>();
        }
        :   OBJ_FLAGS flag1=TEXT {
            String flag1String = "OF_" + $flag1.getText().trim();
            $objectFlags.add(ObjectFlag.valueOf(flag1String));
        } (OR flag2=TEXT{
            String flag2String = "OF_" + $flag2.getText().trim();
            $objectFlags.add(ObjectFlag.valueOf(flag2String));
        })*;

playerFlags
        returns[ArrayList<PlayerFlag> playerFlgs]
        @init {
            $playerFlgs = new ArrayList<>();
        }
        :   PLAYER_FLAGS flag1=TEXT {
            String flagString1 = "PF_" + $flag1.getText().trim();
            $playerFlgs.add(PlayerFlag.valueOf(flagString1));
        } (OR flag2=TEXT {
            String flagString2 = "PF_" + $flag2.getText().trim();
            $playerFlgs.add(PlayerFlag.valueOf(flagString2));
        })*;

values
        returns[HashMap<ValueEnum, Integer> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   VALUES enum1=TEXT LBRACKET val1=TEXT RBRACKET {
            String valueEnum1Str = "CV_" + $enum1.getText().trim();
            ValueEnum valueEnum1 = ValueEnum.valueOf(valueEnum1Str);
            int valueVal1 = Integer.parseInt($val1.getText().trim());
            $valueMap.put(valueEnum1, valueVal1);
        } (OR enum2=TEXT LBRACKET val2=TEXT RBRACKET {
            String valueEnum2Str = "CV_" + $enum2.getText().trim();
            ValueEnum valueEnum2 = ValueEnum.valueOf(valueEnum2Str);
            int valueVal2 = Integer.parseInt($val2.getText().trim());
            $valueMap.put(valueEnum2, valueVal2);
        })*;

effect
        returns[EffectEnum effectEnum,
                TimedEffect timedEffect,
                ObjectFlag objectFlag,
                ProjectionEnum projectionEnum,
                String errorValue]
        @init {
            String type = "";
            $errorValue = "";
            $effectEnum = EffectEnum.EF_NONE;
            $timedEffect = TimedEffect.TMD_NULL;
            $objectFlag = ObjectFlag.OF_NONE;
            $projectionEnum = ProjectionEnum.PROJ_NONE;
        }
        :   EFFECT proj1=TEXT {
            type = $proj1.getText();
            $effectEnum = EffectEnum.valueOf("EF_" + type);
        } (COLON proj2=TEXT {
            String secondFlag = $proj2.getText();
            if (type.equals("CURE")) {
                $timedEffect = TimedEffect.valueOf("TMD_" + secondFlag);
            } else if (type.startsWith("TIMED")) {
                $timedEffect = TimedEffect.valueOf("TMD_" + secondFlag);
            } else if (type.startsWith("PROJ")) {
                $projectionEnum = ProjectionEnum.valueOf("PROJ_" + secondFlag);
            } else {
                $errorValue = type + ":" + secondFlag;
            }
        })?;

dice
        returns[String diceText]
        :   DICE TEXT {
            $diceText = $TEXT.getText();
        };

expr
        returns[Expression expression]
        :   EXPR char=TEXT COLON flag=TEXT COLON val=TEXT {
            String expFlagString = "EFB_" + $flag.getText();
            $expression = new Expression($char.getText().charAt(0),
                                         EffectBaseType.valueOf(expFlagString),
                                         $val.getText());
        };

effectMsg
        returns[String message]
        :   EFFECT_MSG TEXT {
            $message = $TEXT.getText();
        };

blow
        returns[String blowType]
        :   BLOW TEXT {
            $blowType = $TEXT.getText();
        };

shape
        returns[PlayerShape playerShape]
        @init{
            String nameInit = "";
            int toAcInit = 0;
            int toHitInit = 0;
            int toDamInit = 0;
            HashMap<PlayerSkill, Integer> skillsInit = new HashMap<>();

            for (PlayerSkill playerSkill : PlayerSkill.values())
                skillsInit.put(playerSkill, 0);

            Flag<ObjectFlag> flagsInit = new Flag<ObjectFlag>(ObjectFlag.class);
            Flag<PlayerFlag> pFlagsInit = new Flag<PlayerFlag>(PlayerFlag.class);

            HashMap<ValueEnum, Integer> valueInit = new HashMap<>();

            Expression exprInit = null;

            ArrayList<PlayerBlow> blowInit = new ArrayList<>();
            String diceStringInit = "";
            String expressionDiceStringInit = "";

            String effectMessageInit = "";

            EffectEnum effectEnumInit = EffectEnum.EF_NONE;
            TimedEffect timedEffectInit = TimedEffect.TMD_NULL;
            ObjectFlag objectFlagInit = ObjectFlag.OF_NONE;
            ProjectionEnum projEnumInit = ProjectionEnum.PROJ_NONE;
            String errorValInit = "";
        }
        @after {
            Object otherParameter;
            if (timedEffectInit != TimedEffect.TMD_NULL)
                otherParameter = timedEffectInit;
            else if (objectFlagInit != ObjectFlag.OF_NONE)
                otherParameter = objectFlagInit;
            else
                otherParameter = projEnumInit;

            $playerShape = new PlayerShape(nameInit,
                                           toAcInit,
                                           toHitInit,
                                           toDamInit,
                                           skillsInit,
                                           flagsInit,
                                           pFlagsInit,
                                           null,
                                           null,
                                           new ArrayList<ElementEnum>(),
                                           new Effect(effectEnumInit,
                                                      expressionDiceStringInit,
                                                      0, 0,
                                                      null, 0,
                                                      otherParameter,
                                                      effectMessageInit
                                           ),
                                           blowInit.size(),
                                           blowInit);
        }
        :   name { nameInit = $name.shapeName; }
            (combat {
                toAcInit = $combat.shapeToAC;
                toHitInit = $combat.shapeToHit;
                toDamInit = $combat.shapeToDam;
            })?
            (skillDisarmPhys{
                skillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, $skillDisarmPhys.shapeDisarmPhys);
            })?
            (skillDisarmMagic {
                skillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, $skillDisarmMagic.shapeDisarmMagic);
            })?
            (skillSave {
                skillsInit.put(PlayerSkill.SKILL_SAVE, $skillSave.shapeSave);
            })?
            (skillStealth {
                skillsInit.put(PlayerSkill.SKILL_STEALTH, $skillStealth.shapeStealth);
            })?
            (skillSearch {
                skillsInit.put(PlayerSkill.SKILL_SEARCH, $skillSearch.shapeSearch);
            })?
            (skillMelee {
                skillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, $skillMelee.shapeMelee);
            })?
            (skillThrow {
                skillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, $skillThrow.shapeThrow);
            })?
            (skillDig {
                skillsInit.put(PlayerSkill.SKILL_DIGGING, $skillDig.shapeDig);
            })?
            (objFlags {
                for (ObjectFlag oFlag : $objFlags.objectFlags)
                    flagsInit.on(oFlag);
            })*
            (playerFlags {
                for (PlayerFlag pFlag : $playerFlags.playerFlgs)
                    pFlagsInit.on(pFlag);
            })*
            (values {
                valueInit = $values.valueMap;
            })*
            (effectMsg {
                effectMessageInit = $effectMsg.message;
            })*
            (effect {
                effectEnumInit = $effect.effectEnum;
                timedEffectInit = $effect.timedEffect;
                objectFlagInit = $effect.objectFlag;
                projEnumInit = $effect.projectionEnum;
                errorValInit = $effect.errorValue;
            })*
            (dice {
                String diceString = $dice.diceText;
                if (diceString.contains("$")) {
                    expressionDiceStringInit = diceString;
                } else {
                    diceStringInit = diceString;
                }
            })?
            (expr {
                exprInit = $expr.expression;
            })?
            (effect {
                effectEnumInit = $effect.effectEnum;
                timedEffectInit = $effect.timedEffect;
                objectFlagInit = $effect.objectFlag;
                projEnumInit = $effect.projectionEnum;
                errorValInit = $effect.errorValue;
            })*
            (effectMsg {
                effectMessageInit = $effectMsg.message;
            })?
            (effect {
                effectEnumInit = $effect.effectEnum;
                timedEffectInit = $effect.timedEffect;
                objectFlagInit = $effect.objectFlag;
                projEnumInit = $effect.projectionEnum;
                errorValInit = $effect.errorValue;
            })*
            (dice {
                String diceString = $dice.diceText;
                if (diceString.contains("$")) {
                    expressionDiceStringInit = diceString;
                } else {
                    diceStringInit = diceString;
                }
            })?
            (blow {
                blowInit.add(new PlayerBlow($blow.blowType));
            })*
        ;

shapes
        returns[ArrayList<PlayerShape> shapeArray]
        @init {
            $shapeArray = new ArrayList<>();
        }
        :   (shape {
            $shapeArray.add($shape.playerShape);
        })+ EOF
        ;

COMMENT
        :   '#' ~':' (~'\n')* '\n'+ -> skip
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
        :   'effect-msg:'
        ;

BLOW
        :   'blow:'
        ;

TEXT
        :   [a-z0-9A-Z _$/+-]+
        ;

COLON
        :   ':'
        ;

OR
        :   '|' | '| ' | ' | ' | ' |'
        ;

LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        ;