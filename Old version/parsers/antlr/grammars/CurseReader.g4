grammar CurseReader;

@header {
    import java.util.HashMap;
    import java.util.ArrayList;

    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.game.GameEnginee.Game;
    import uk.co.jackoftrades.middle.enums.ValueEnum;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
}

name
        returns[String nameStr]
        :   NAME TEXT {
            $nameStr = $TEXT.getText();
        };

type
        returns[ObjectBase typeBase]
        :   TYPE TEXT {
            String tValName = $TEXT.getText();
            TValue tVal = TValue.fromName(tValName);
            $typeBase = Game.getBaseFromTVal(tVal);
        };

/**
 * Never used
weight
        :   WEIGHT TEXT
        ;
 */

combat
        returns[int toHitVal, int toDamVal, int acVal]
        :   COMBAT toHit=TEXT COLON toDam=TEXT COLON ac=TEXT {
            $toHitVal = Integer.parseInt($toHit.getText());
            $toDamVal = Integer.parseInt($toDam.getText());
            $acVal = Integer.parseInt($ac.getText());
        };

effect
        returns[EffectEnum effectEnum, TimedEffect timedEnum, MonsterRaceFlag summon]
        @init {
            $timedEnum = TimedEffect.TMD_NULL;
            $effectEnum = EffectEnum.EF_NONE;
            $summon = MonsterRaceFlag.RF_NONE;
            String effectType = "Timed";
        }
        :   EFFECT effEnum=TEXT {
            String effectEnumStr = "EF_" + $effEnum.getText().trim();
            $effectEnum = EffectEnum.valueOf(effectEnumStr);
            if (effectEnumStr.equals("EF_SUMMON"))
                effectType = "Summon";
        } (COLON tmdEnum=TEXT {
            if ($tmdEnum != null) {
                if (effectType.equals("Timed"))
                    $timedEnum = TimedEffect.valueOf("TMD_" + $tmdEnum.getText().trim());
                else
                    $summon = MonsterRaceFlag.valueOf("RF_" + $tmdEnum.getText().trim());
            }
        })?;

dice
        returns[String diceString]
        :   DICE TEXT {
            $diceString = $TEXT.getText();
        };

expr
        returns[char letter, EffectBaseType effectBaseType, String operation]
        :   EXPR codeLetter=TEXT COLON efbType=TEXT COLON operations=TEXT {
            $letter = $codeLetter.getText().charAt(0);

            String efbString = "EFB_" + $efbType.getText().trim();
            $effectBaseType = EffectBaseType.valueOf(efbString);

            $operation = $operations.getText();
        };

time
        returns[String timeString]
        :   TIME TEXT {
            $timeString = $TEXT.getText();
        };

flags
        returns[ArrayList<ObjectFlag> flagArray]
        @init {
            $flagArray = new ArrayList<>();
        }
        :   FLAGS flag1=TEXT {
            String flag1String = "OF_" + $flag1.getText().trim();
            ObjectFlag objectFlag1 = ObjectFlag.valueOf(flag1String);
            $flagArray.add(objectFlag1);
        }
        (OR flag2=TEXT {
            String flag2String = "OF_" + $flag2.getText().trim();
            ObjectFlag objectFlag2 = ObjectFlag.valueOf(flag2String);
            $flagArray.add(objectFlag2);
        })*;

values
        returns[HashMap<ValueEnum, Integer> valueCollection]
        @init {
            $valueCollection = new HashMap<>();
        }
        :   VALUES tag1=TEXT val1=VALUE {
            String cv1String = $tag1.getText();
            String val1String = $val1.getText();
            ValueEnum cv1 = ValueEnum.valueOf("CV_" + cv1String.trim());
            int int1 = Integer.parseInt(val1String.substring(1, val1String.length() - 1));
            $valueCollection.put(cv1, int1);
        } (OR tag2=TEXT val2=VALUE{
            String cv2String = $tag2.getText();
            String val2String = $val2.getText();
            ValueEnum cv2 = ValueEnum.valueOf("CV_" + cv2String.trim());
            int int2 = Integer.parseInt(val2String.substring(1, val2String.length() - 1));
            $valueCollection.put(cv2, int2);
        })*;

msg
        returns[String message]
        :   MSG TEXT {
            $message = $TEXT.getText();
        };

desc
        returns[String description]
        :   DESC TEXT {
            $description = $TEXT.getText();
        };

conflict
        returns[String conflictString]
        :   CONFLICT TEXT {
            $conflictString = $TEXT.getText();
        };

conflict_flags
        returns[ArrayList<ObjectFlag> conflictFlags]
        @init {
            $conflictFlags = new ArrayList<>();
        }
        :   CONFLICT_FLAGS flag1=TEXT {
            String flag1Name = "OF_" + $flag1.getText().trim();
            ObjectFlag oFlag1 = ObjectFlag.valueOf(flag1Name);
            $conflictFlags.add(oFlag1);
        }
        (OR flag2=TEXT{
            String flag2Name = "OF_" + $flag2.getText().trim();
            ObjectFlag oFlag2 = ObjectFlag.valueOf(flag2Name);
            $conflictFlags.add(oFlag2);
        })*;

curse
        returns[Curse crs]
        @init{
            String curseName = "";
            boolean cursePoss = false;
            ArrayList<ObjectBase> curseBases = new ArrayList<>();
            ArrayList<ObjectFlag> curseFlags = new ArrayList<>();
            String curseConflict = "";
            ArrayList<ObjectFlag> curseConflictFlags = new ArrayList<>();
            String curseDice = "";
            String curseTime = "";
            String curseDesc = "";
            EffectEnum curseEffect = EffectEnum.EF_NONE;
            TimedEffect curseTimedEffect = TimedEffect.TMD_NULL;
            int curseComToHit = 0;
            int curseComToDam = 0;
            int curseComAC = 0;
            char curseExpressionChar = '\0';
            EffectBaseType curseEFB = EffectBaseType.EFB_NULL;
            String expressionOperation = "";
            HashMap<ValueEnum, Integer> curseValueCollection = new HashMap<>();
            String curseMessage = "";
        }
        @after{
            $crs = new Curse(curseName, cursePoss, curseBases, curseFlags,
                             curseConflict, curseConflictFlags, curseDice, curseTime,
                             curseDesc, curseEffect, curseTimedEffect,
                             curseComToHit, curseComToDam, curseComAC,
                             curseExpressionChar, curseEFB, expressionOperation,
                             curseValueCollection, curseMessage);
        }
        :   name {
                curseName = $name.nameStr;
            }
            (((combat {
                        curseComToHit = $combat.toHitVal;
                        curseComToDam = $combat.toDamVal;
                        curseComAC = $combat.acVal;
            })? (type {
                        curseBases.add($type.typeBase);
                      })+) | (type {
                        curseBases.add($type.typeBase);})+
                        (combat {
                        curseComToHit = $combat.toHitVal;
                        curseComToDam = $combat.toDamVal;
                        curseComAC = $combat.acVal;
                        })?)
            (effect {
                curseEffect = $effect.effectEnum;
                curseTimedEffect = $effect.timedEnum;
            })?
            (dice {
                curseDice = $dice.diceString;
            })?
            (expr {
                curseExpressionChar = $expr.letter;
                curseEFB = $expr.effectBaseType;
                expressionOperation = $expr.operation;
            })?
            (time {
                curseTime = $time.timeString;
            })?
            (((flags {
                for (ObjectFlag flag : $flags.flagArray)
                    curseFlags.add(flag);
            })? (values {
                for (ValueEnum value : $values.valueCollection.keySet()) {
                    curseValueCollection.put(value, $values.valueCollection.get(value));
                }
            })?) | ((values {
                for (ValueEnum value : $values.valueCollection.keySet()) {
                    curseValueCollection.put(value, $values.valueCollection.get(value));
            }})? (flags {
                for (ObjectFlag flag : $flags.flagArray)
                    curseFlags.add(flag);
            })))
            msg?
            ((desc {
                curseDesc = $desc.description;
            }
            (conflict {
                curseConflict = $conflict.conflictString;
            })?
            (conflict_flags {
                for (ObjectFlag flag : $conflict_flags.conflictFlags)
                    curseConflictFlags.add(flag);
            })?) |
            ((conflict  {
                curseConflict = $conflict.conflictString;
            })? (conflict_flags {
                for (ObjectFlag flag : $conflict_flags.conflictFlags)
                    curseConflictFlags.add(flag);
            })? desc {
                curseDesc = $desc.description;
            }))
        ;

curses
        returns[ArrayList<Curse> curseList]
        @init {
            $curseList = new ArrayList<>();
        }
        :   (curse {
            $curseList.add($curse.crs);
        })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   '\r'* '\n' ' '* -> skip
        ;

NAME
        :   'name:'
        ;

TYPE
        :   'type:'
        ;

WEIGHT
        :   'weight'
        ;

COMBAT
        :   'combat:'
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

TIME
        :   'time:'
        ;

FLAGS
        :   'flags:'
        ;

VALUES
        :   'values:'
        ;

MSG
        :   'msg:'
        ;

DESC
        :   'desc:'
        ;

CONFLICT
        :   'conflict:'
        ;

CONFLICT_FLAGS
        :   'conflict-flags:'
        ;

VALUE
        :   '[' '-'?[0-9]+ ']'
        ;

TEXT
        :   [a-zA-Z0-9_.!,+ $\-]+
        ;

COLON
        :   ':'
        ;

OR
        :   ' | ' | '| '
        ;