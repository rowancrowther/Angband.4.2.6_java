grammar Curse;

@header {
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlagName;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.ValueEnum;
    import uk.co.jackoftrades.middle.objects.Curse;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

type
        returns[ObjectBase baseObj]
        :   TYPE STRING { $baseObj = GameConstants.lookupObjectBase($STRING.getText()); }
        ;

weight
        returns[int weightAdj]
        :   WEIGHT STRING { $weightAdj = Integer.parseInt($STRING.getText()); }
        ;

combat
        returns[int toHit, int toDam, int toAC]
        :   COMBAT toh=STRING COLON tod=STRING COLON toa=STRING {
                $toHit = Integer.parseInt($toh.getText());
                $toDam = Integer.parseInt($tod.getText());
                $toAC  = Integer.parseInt($toa.getText());
            }
        ;

effect
        returns[EffectEnum effectEnum, MonsterRaceFlag summonMon, TimedEffect timedEffect]
        @init {
            $summonMon   = MonsterRaceFlag.RF_NONE;
            $timedEffect = TimedEffect.TMD_NONE;
        }
        :   EFFECT eff=STRING {
                $effectEnum = EffectEnum.valueOf("EF_" + $eff.getText());
            } (COLON opt=STRING {
                $summonMon = MonsterRaceFlag.RF_NONE;
                $timedEffect = TimedEffect.TMD_NONE;

                switch ($eff.getText()) {
                    case "SUMMON":
                        $summonMon = MonsterRaceFlag.valueOf("RF_" + $opt.getText());
                        break;

                    case "TIMED_INC":
                        $timedEffect = TimedEffect.valueOf("TMD_" + $opt.getText());
                        break;
                }
            })?
        ;

dice
        returns[String diceStr]
        :   DICE STRING { $diceStr = $STRING.getText(); }
        ;

expr
        returns[char exprChar, EffectBaseType evBase, String effectString]
        :   EXPR ch=STRING COLON base=STRING COLON eff=STRING {
                String raw = $ch.getText();
                if (raw.length() != 1)
                    throw new IllegalArgumentException("Expression character code may only have one character.");

                $exprChar = $ch.getText().charAt(0);
                $evBase = EffectBaseType.valueOf("EFB_" + $base.getText());
                $effectString = $eff.getText();
            }
        ;

time
        returns[String timeStr]
        :   TIME STRING { $timeStr = $STRING.getText(); }
        ;

flags
        returns[List<ObjectFlagName> flagList]
        @init { $flagList = new ArrayList<>(); }
        :   FLAGS flg1=STRING {
                $flagList.add(ObjectFlagName.valueOf("OF_" + $flg1.getText().trim()));
            } (' | ' flg2=STRING {
                $flagList.add(ObjectFlagName.valueOf("OF_" + $flg2.getText().trim()));
            })*
        ;

values
        returns[Map<ValueEnum, Integer> vals]
        @init { $vals = new HashMap<>(); }
        :   VALUES cde1=STRING LBRACKET val1=STRING RBRACKET {
                ValueEnum c1 = ValueEnum.valueOf("CV_" + $cde1.getText().trim());
                int v1 = Integer.parseInt($val1.getText());
                $vals.put(c1, v1);
            } (' | ' cde2=STRING LBRACKET val2=STRING RBRACKET {
                ValueEnum c2 = ValueEnum.valueOf("CV_" + $cde2.getText().trim());
                int v2 = Integer.parseInt($val2.getText());
                $vals.put(c2, v2);
            })*
        ;

msg
        returns[String msgStr]
        :   MSG STRING { $msgStr = $STRING.getText(); }
        ;

desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

conflict
        returns[String confStr]
        :   CONFLICT STRING { $confStr = $STRING.getText(); }
        ;

conflict_flags
        returns[List<ObjectFlagName> cFlags]
        @init { $cFlags = new ArrayList<>(); }
        :   CONFLICT_FLAGS flg1=STRING {
                $cFlags.add(ObjectFlagName.valueOf("OF_" + $flg1.getText().trim()));
            }
        ('|' flg2=STRING {
                $cFlags.add(ObjectFlagName.valueOf("OF_" + $flg2.getText().trim()));
            })*
        ;

curse
        returns[Curse cur]
        @init {
            String nameInit = "";
            boolean possInit = false;
            List<ObjectBase> basesInit = new ArrayList<>();
            int weightInit = 0;
            int tohInit = 0;
            int todInit = 0;
            int toaInit = 0;
            EffectEnum effectEnumInit = EffectEnum.EF_NONE;
            MonsterRaceFlag summonMonInit = MonsterRaceFlag.RF_NONE;
            TimedEffect timedEffectInit = TimedEffect.TMD_NONE;
            String diceInit = "";
            char exprCharInit = '\0';
            EffectBaseType evBaseInit = EffectBaseType.EFB_NONE;
            String effectStringInit = "";
            String timeInit = "";
            List<ObjectFlagName> flagsInit = new ArrayList<>();
            Map<ValueEnum, Integer> valsInit = new HashMap<>();
            String msgInit = "";
            String descInit = "";
            String conflictInit = "";
            List<ObjectFlagName> confFlagsInit = new ArrayList<>();
        }
        @after {
            $cur = new Curse(nameInit, possInit, basesInit, weightInit, flagsInit,
                             conflictInit, confFlagsInit, diceInit, timeInit,
                             descInit, effectEnumInit, summonMonInit,
                             timedEffectInit, tohInit, todInit, toaInit,
                             exprCharInit, evBaseInit, effectStringInit, valsInit,
                             msgInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   type { basesInit.add($type.baseObj); }
        |   weight { weightInit = $weight.weightAdj; }
        |   combat {
                tohInit = $combat.toHit;
                todInit = $combat.toDam;
                toaInit = $combat.toAC;
            }
        |   effect {
                effectEnumInit = $effect.effectEnum;
                summonMonInit = $effect.summonMon;
                timedEffectInit = $effect.timedEffect;
            }
        |   dice { diceInit = $dice.diceStr; }
        |   expr {
                exprCharInit = $expr.exprChar;
                evBaseInit = $expr.evBase;
                effectStringInit = $expr.effectString;
            }
        |   time { timeInit = $time.timeStr; }
        |   flags {
                flagsInit.addAll($flags.flagList);
            }
        |   values { valsInit.putAll($values.vals); }
        |   msg { msgInit = $msg.msgStr; }
        |   desc { descInit = $desc.descStr; }
        |   conflict { conflictInit = $conflict.confStr; }
        |   conflict_flags {
                confFlagsInit.addAll($conflict_flags.cFlags);
        })+;

file
        returns[List<Curse> curses]
        @init {
            $curses = new ArrayList<>();
        }
        :   (curse { $curses.add($curse.cur); })+ EOF
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

TYPE
        :   'type:'
        ;

WEIGHT
        :   'weight:'
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

STRING
        :   ('a'..'z' | ' ' | 'A'..'Z' | '!' | '+' | '0'..'9' | '-' | '.' | ',' | '_' | '$')+
        ;

COLON
        :   ':'
        ;

LBRACKET
        : '['
        ;

RBRACKET
        : ']'
        ;