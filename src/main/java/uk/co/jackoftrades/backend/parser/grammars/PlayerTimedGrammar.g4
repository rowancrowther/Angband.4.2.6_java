/*
 * @author Rowan Crowther
 *
 * Parser for player_timed.txt. Each rule assembles one directive into a piece of a
 * PlayerTimedParseRecord; the top-level `file` rule requires a record-count header followed by one
 * or more `playerTimed` records, each opening with a `name` line and then accepting its directives
 * in any order. The rule actions build only the loss-less string/sub-record capture — all
 * resolution to domain types happens later in PlayerTimedAssembler. Grades carry an optional fifth
 * (down-message) field, and both effect blocks are captured as EffectParseRecords for the shared
 * EffectAssembler to resolve.
 */
parser grammar PlayerTimedGrammar;

options {tokenVocab = PlayerTimedLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.playertimed.PlayerTimedParseRecord;
    import uk.co.jackoftrades.backend.parser.playertimed.PlayerTimedParseRecord.PlayerTimedGradeParseRecord;
    import uk.co.jackoftrades.backend.parser.playertimed.PlayerTimedParseRecord.FailureParseRecord;
    import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameStr, int lineNo]
        :   NAME FLAG { $nameStr = $FLAG.getText(); $lineNo = $start.getLine(); }
        ;

desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

grade
        returns[String colour, String max,
                String status, String messageUp,
                String messageDown]
        @init {
            $messageDown = "";
            $messageUp = "";
        }
        :   GRADE c=DELIM_COLOUR_CODE DELIM_COLON
                  m=DELIM_INTEGER DELIM_COLON
                  s=DELIM_BETWEEN_COLONS DELIM_COLON
                  u=DELIM_BETWEEN_COLONS {
                  $colour      = $c.getText();
                  $max         = $m.getText();
                  $status      = $s.getText();
                  $messageUp   = $u.getText(); }
                  (DELIM_COLON
                  (d=DELIM_BETWEEN_COLONS {
                  $messageDown = $d.getText();
                  })?)?
        ;

onEnd
        returns[String message]
        :   ON_END STRING { $message = $STRING.getText(); }
        ;

onIncrease
        returns[String message]
        :   ON_INCREASE STRING { $message = $STRING.getText(); }
        ;

onDecrease
        returns[String message]
        :   ON_DECREASE STRING { $message = $STRING.getText(); }
        ;

msgt
        returns[String msgType]
        :   MSGT FLAG { $msgType = $FLAG.getText(); }
        ;

fail
        returns[String type, String value]
        :   FAIL t=DELIM_INTEGER DELIM_COLON v=DELIM_BETWEEN_COLONS
            {
                $type = $t.getText();
                $value = $v.getText();
            }
        ;

onBeginEffect
        returns[String type, String subType]
        :   ON_BEGIN_EFFECT t=DELIM_BETWEEN_COLONS { $type = $t.getText(); $subType = ""; }
            (DELIM_COLON s=DELIM_BETWEEN_COLONS { $subType = $s.getText(); })?
        ;

onEndEffectBlock
        returns[String type, String subType, String dice]
        :   ON_END_EFFECT t=DELIM_BETWEEN_COLONS { $type = $t.getText(); $subType = ""; }
            (DELIM_COLON s=DELIM_BETWEEN_COLONS { $subType = $s.getText(); })?
            (EFFECT_DICE INTEGER { $dice = $INTEGER.getText(); })?
        ;

resist
        returns[String res]
        :   RESIST FLAG { $res = $FLAG.getText(); }
        ;

brand
        returns[String b]
        :   BRAND FLAG { $b = $FLAG.getText(); }
        ;

slay
        returns[String s]
        :   SLAY FLAG { $s = $FLAG.getText(); }
        ;

flagSynonym
        returns[String objProp, String val]
        :   FLAG_SYNONYM op=DELIM_BETWEEN_COLONS
            DELIM_COLON v=DELIM_INTEGER
            { $objProp = $op.getText(); $val = $v.getText(); }
        ;

lowerBound
        returns[String bound]
        :   LOWER_BOUND INTEGER { $bound = $INTEGER.getText(); }
        ;

flags
        returns[List<String> flagList]
        @init {
            $flagList = new ArrayList<>();
        }
        :   FLAGS f1=FLAG { $flagList.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $flagList.add($f2.getText()); })*
        ;

playerTimed
        returns[PlayerTimedParseRecord timed]
        @init {
            String nameInit = "";
            String descInit = "";
            List<PlayerTimedGradeParseRecord> grades = new ArrayList<>();
            String onEndInit = "";
            String onIncreaseInit = "";
            String onDecreaseInit = "";
            String msgtInit = "";
            List<FailureParseRecord> fails = new ArrayList<>();
            EffectParseRecord onBeginEffectInit = null;
            EffectParseRecord onEndEffectInit = null;
            String resistInit = "";
            String brandInit = "";
            String slayInit = "";
            String flagSynonymFlagInit = "";
            String flagSynonymValueInit = "";
            String lowerBoundInit = "";
            List<String> flags = new ArrayList<>();
            int line = 0;
        }
        @after {
            $timed = new PlayerTimedParseRecord(nameInit, descInit, grades,
                    onEndInit, onIncreaseInit, onDecreaseInit, msgtInit,
                    fails, onBeginEffectInit, onEndEffectInit,
                    resistInit, brandInit, slayInit, flagSynonymFlagInit,
                    flagSynonymValueInit, lowerBoundInit, flags, line);
        }
        :   name { nameInit = $name.nameStr; line = $name.lineNo; }
        (   desc { descInit = $desc.descStr; }
        |   grade { String colour       = $grade.colour;
                    String max          = $grade.max;
                    String status       = $grade.status;
                    String messageUp    = $grade.messageUp;
                    String messageDown  = $grade.messageDown;
                    PlayerTimedGradeParseRecord gradeRecord =
                        new PlayerTimedGradeParseRecord(colour, max, status, messageDown, messageUp);
                    grades.add(gradeRecord);
                }
        |   onEnd { onEndInit = $onEnd.message; }
        |   onIncrease { onIncreaseInit = $onIncrease.message; }
        |   onDecrease { onDecreaseInit = $onDecrease.message; }
        |   msgt { msgtInit = $msgt.msgType; }
        |   fail { String failTypeInit = $fail.type;
                   String failValueInit = $fail.value;
                   fails.add(new FailureParseRecord(failTypeInit, failValueInit)); }
        |   onBeginEffect { onBeginEffectInit = new EffectParseRecord($onBeginEffect.type,
                            $onBeginEffect.subType, "", "", "",
                            "", "", "", "", "",
                            "", "", line); }
        |   onEndEffectBlock { onEndEffectInit = new EffectParseRecord($onEndEffectBlock.type,
                            $onEndEffectBlock.subType, "", "", $onEndEffectBlock.dice,
                            "", "", "", "", "",
                            "", "", line); }
        |   resist { resistInit = $resist.res; }
        |   brand { brandInit = $brand.b; }
        |   slay { slayInit = $slay.s; }
        |   flagSynonym { flagSynonymFlagInit = $flagSynonym.objProp;
                          flagSynonymValueInit = $flagSynonym.val; }
        |   lowerBound { lowerBoundInit = $lowerBound.bound; }
        |   flags { flags.addAll($flags.flagList); })+
        ;

file
        returns[String declaredRecordCount, List<PlayerTimedParseRecord> results]
        @init {
            $results = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (playerTimed { $results.add($playerTimed.timed); })+ EOF
        ;
