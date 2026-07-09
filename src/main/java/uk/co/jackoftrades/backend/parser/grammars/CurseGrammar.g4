parser grammar CurseGrammar;

options { tokenVocab = CurseLexer; }

import EffectBlock;

@header {
    import uk.co.jackoftrades.backend.parser.curse.CurseParseRecord;
    import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

    import java.util.Map;
    import java.util.List;
    import java.util.HashMap;
    import java.util.ArrayList;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameStr, int line]
        :   NAME FREE_TEXT { $nameStr = $FREE_TEXT.getText();
                          $line = $start.getLine(); }
        ;

curseType
        returns[String typeStr]
        :   TYPE FREE_TEXT { $typeStr = $FREE_TEXT.getText(); }
        ;

weight
        returns[String weightAdj]
        :   WEIGHT INTEGER { $weightAdj = $INTEGER.getText(); }
        ;

combat
        returns[String toh, String toa, String tod]
        :   COMBAT h=INTEGER COLON d=INTEGER COLON a=INTEGER {
                $toh = $h.getText();
                $tod = $d.getText();
                $toa = $a.getText();
            }
        ;

// EffectBlock imported, no need to define

flags
        returns[List<String> flagList]
        :   { $flagList = new ArrayList<>(); }
            FLAGS f1=FLAG { $flagList.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $flagList.add($f2.getText()); })*
        ;

values
        returns[Map<String, String> valueMap]
        :   { $valueMap = new HashMap<>(); }
            VALUES vf1=VALUE_FLAG LBRACKET vi1=VALUE_INT RBRACKET {
                $valueMap.put($vf1.getText(), $vi1.getText());
            }
            (VALUE_OR vf2=VALUE_FLAG LBRACKET vi2=VALUE_INT RBRACKET {
                $valueMap.put($vf2.getText(), $vi2.getText());
            })*
        ;

msg
        returns[String msgStr]
        :   CURSE_MSG FREE_TEXT { $msgStr = $FREE_TEXT.getText(); }
        ;

desc
        returns[String description]
        :   DESC FREE_TEXT { $description = $FREE_TEXT.getText(); }
        ;

conflict
        returns[String conflictingCurses]
        :   CONFLICT FREE_TEXT { $conflictingCurses = $FREE_TEXT.getText(); }
        ;

conflictFlags
        returns[List<String> cFlags]
        :   { $cFlags = new ArrayList<>(); }
            CONFLICT_FLAGS f1=FLAG { $cFlags.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $cFlags.add($f2.getText()); })*
        ;

curseRecord
        returns[CurseParseRecord record]
        @init {
            int blockLine = 0;
            String nameInit = "";
            String tohInit = "";
            String todInit = "";
            String toaInit = "";
            String msgInit = "";
            List<String> curseTypeInit = new ArrayList<>();
            List<EffectParseRecord> effects = new ArrayList<>();
            List<String> flagsListInit = new ArrayList<>();
            Map<String, String> valuesInit = new HashMap<>();
            List<String> descInit = new ArrayList<>();
            List<String> conflictInit = new ArrayList<>();
            List<String> cFlagsInit = new ArrayList<>();
            String weightAdjustmentInit = "";
        }
        @after {
            $record = new CurseParseRecord(nameInit, curseTypeInit, weightAdjustmentInit,
                        tohInit, todInit, toaInit, effects, flagsListInit, valuesInit,
                        msgInit, descInit, conflictInit, conflictInit, cFlagsInit, blockLine);
        }
        :   name { nameInit = $name.nameStr;
                   blockLine = $name.line; }
        (   curseType { curseTypeInit.add($curseType.typeStr); }
        |   weight { weightAdjustmentInit = $weight.weightAdj; }
        |   combat { tohInit = $combat.toh;
                     todInit = $combat.tod;
                     toaInit = $combat.toa; }
        |   effectBlock { effects.add(new EffectParseRecord($effectBlock.typeInit,
                $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                $effectBlock.expressionChars, $effectBlock.expressionBase,
                $effectBlock.expressionOperation, $effectBlock.timeDiceString,
                $effectBlock.effectMessage, $effectBlock.start.getLine())); }
        |   flags { flagsListInit.addAll($flags.flagList); }
        |   values { valuesInit.putAll($values.valueMap); }
        |   msg { msgInit = $msg.msgStr; }
        |   desc { descInit.add($desc.description); }
        |   conflict { conflictInit.add($conflict.conflictingCurses); }
        |   conflictFlags { cFlagsInit.addAll($conflictFlags.cFlags); })+
        ;

file
        returns[List<CurseParseRecord> records, String declaredCount]
        :   recordCount { $declaredCount = $recordCount.count;
                          $records = new ArrayList<>(); }
            (curseRecord { $records.add($curseRecord.record); })+ EOF
        ;