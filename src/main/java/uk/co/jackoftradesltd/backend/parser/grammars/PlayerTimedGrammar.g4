/*
 * @author Rowan Crowther
 *
 * Parser for player_timed.txt. Each rule reads one directive and returns it as plain strings; the
 * `playerTimed` rule gathers them into a PlayerTimedParseRecord, and `file` collects the records.
 *
 * === Shape of a file ===
 *
 * `file` takes zero or more `playerTimed` records, so an empty file loads empty. A record opens with
 * `name:` and then takes the other directives in any order and any number, until the next `name:`.
 * How repeats are kept:
 *   - desc:, on-end:, on-increase:, on-decrease: are joined onto what the record already holds, with
 *     no separator.
 *   - grade:, fail: and flags: each add to a list.
 *   - msgt:, resist:, brand:, slay:, flag-synonym: and lower-bound: keep the last line given.
 *   - the effect lines are described next.
 *
 * === Effect lines ===
 *
 * `on-begin-effect:` and `on-end-effect:` each open a new effect, and `effect-dice:`, `effect-yx:`,
 * `effect-expr:` and `effect-msg:` fill the effect opened last, as `ps->e` does in `player-timed.c`.
 * Because `ps->e` is only cleared by the next `name:`, the four directives may come any distance after
 * their effect line, with other directives between. Each of them is therefore an alternative of
 * `playerTimed` that rebuilds the last entry of `effectsInit`, and not part of a rule that ends at the
 * first other directive.
 *   - With no effect open (straight after `name:`, before the effect line, or after a new `name:`)
 *     the four directives are refused, as C returns PARSE_ERROR_MISSING_RECORD_HEADER. A semantic
 *     predicate on each alternative does this, so `effectsInit` is a parser field (see @members) and
 *     not a local of the rule: ANTLR puts predicates in a separate method that cannot see locals.
 *   - `effect-expr:` is also refused while the open effect has no dice, as C refuses it.
 *   - repeated `effect-msg:` lines are joined, and repeated `effect-expr:` lines are joined with ^
 *     between the parts, as EffectParseRecordType.appendMessage and amendExpression do.
 *   - a second `effect-dice:` or `effect-yx:` replaces the first.
 *
 * The effects of both chains are kept in the one list `effectsInit`, tagged "begin" or "end" in file
 * order, and split into the two chains when the record ends.
 *
 * === What the actions do not do ===
 *
 * The actions only capture text. Resolving names to domain types, parsing numbers and checking dice
 * strings happen later, in PlayerTimedAssembler and the shared EffectAssembler. A dice string is
 * kept as written. An effect with no effect-msg: line carries an empty string, where C's message
 * is NULL.
 */
parser grammar PlayerTimedGrammar;

options {tokenVocab = PlayerTimedLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.playertimed.PlayerTimedParseRecord;
    import uk.co.jackoftradesltd.backend.parser.playertimed.PlayerTimedParseRecord.PlayerTimedGradeParseRecord;
    import uk.co.jackoftradesltd.backend.parser.playertimed.PlayerTimedParseRecord.FailureParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecordType;

    import java.util.List;
    import java.util.ArrayList;
}

@members {
    // The effects of the record being read, both chains in file order. A field, not a local of
    // playerTimed, because the {!effectsInit.isEmpty()}? predicates run in a separate generated
    // method. playerTimed's @init gives each record a fresh list, which is what a new name: does to
    // ps->e in C.
    List<EffectParseRecordType> effectsInit;
}

/*
 * @author Rowan Crowther
 *
 * `name:` followed by the effect's name as a flag token (TMD_ constants without the prefix, as in
 * `name:FAST`). Opens a record. Returns the name and the line it is on, which is the line the
 * effects of the record are reported against.
 *
 * Rule name coded before 261124, commented in full on 261124.
 */
name
        returns[String nameStr, int lineNo]
        :   NAME FLAG { $nameStr = $FLAG.getText(); $lineNo = $start.getLine(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `desc:` followed by the rest of the line as text. Returns the text; `playerTimed` joins repeats.
 *
 * Rule desc coded before 261124, commented in full on 261124.
 */
desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `grade:` followed by colour code, maximum, status name, up-message and an optional down-message,
 * separated by colons. Returns each field as text. With no fifth field the down-message is null, and
 * with a colon but no text after it the same; the up-message is empty if absent.
 *
 * Rule grade coded before 261124, commented in full on 261124.
 */
grade
        returns[String colour, String max,
                String status, String messageUp,
                String messageDown]
        @init {
            $messageDown = null;
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

/*
 * @author Rowan Crowther
 *
 * `on-end:` and the text to the end of the line. Returns the text; `playerTimed` joins repeats.
 *
 * Rule onEnd coded before 261124, commented in full on 261124.
 */
onEnd
        returns[String message]
        :   ON_END STRING { $message = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `on-increase:` and the text to the end of the line. Returns the text; `playerTimed` joins repeats.
 *
 * Rule onIncrease coded before 261124, commented in full on 261124.
 */
onIncrease
        returns[String message]
        :   ON_INCREASE STRING { $message = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `on-decrease:` and the text to the end of the line. Returns the text; `playerTimed` joins repeats.
 *
 * Rule onDecrease coded before 261124, commented in full on 261124.
 */
onDecrease
        returns[String message]
        :   ON_DECREASE STRING { $message = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `msgt:` followed by a message-type name as a flag token. Returns it raw; the assembler looks
 * it up.
 *
 * Rule msgt coded before 261124, commented in full on 261124.
 */
msgt
        returns[String msgType]
        :   MSGT FLAG { $msgType = $FLAG.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `fail:` followed by the integer code and the flag name, separated by a colon. Returns both as
 * text; turning the code and name into a failure is the assembler's job. `playerTimed` puts each new
 * line at the front of the record's list.
 *
 * Rule fail coded before 261124, commented in full on 261124.
 */
fail
        returns[String type, String value]
        :   FAIL t=DELIM_INTEGER DELIM_COLON v=DELIM_BETWEEN_COLONS
            {
                $type = $t.getText();
                $value = $v.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * `on-begin-effect:` followed by the effect name and an optional sub-type, separated by a colon.
 * Returns both (the sub-type is empty if absent) and the tag "begin". The radius and other fields that
 * C's directive also reads are not read here: a third field is refused.
 *
 * Rule onBeginEffect coded before 261124, updated on 261124 when effects became alternatives of playerTimed, commented in full on 261124.
 */
onBeginEffect
        returns[String type, String subType, String effectType]
        :   ON_BEGIN_EFFECT t=DELIM_BETWEEN_COLONS { $type = $t.getText(); $subType = ""; }
            (DELIM_COLON s=DELIM_BETWEEN_COLONS { $subType = $s.getText(); })?
            { $effectType = "begin"; }
        ;

/*
 * @author Rowan Crowther
 *
 * `on-end-effect:` followed by the effect name and an optional sub-type, separated by a colon.
 * Returns both (the sub-type is empty if absent) and the tag "end". The radius and other fields that
 * C's directive also reads are not read here: a third field is refused.
 *
 * Rule onEndEffect coded before 261124, updated on 261124 when effects became alternatives of playerTimed, commented in full on 261124.
 */
onEndEffect
        returns[String type, String subType, String effectType]
        :   ON_END_EFFECT t=DELIM_BETWEEN_COLONS { $type = $t.getText(); $subType = ""; }
            (DELIM_COLON s=DELIM_BETWEEN_COLONS { $subType = $s.getText(); })?
            { $effectType = "end"; }
        ;

/*
 * @author Rowan Crowther
 *
 * `effect-dice:` followed by a dice string: a plain number, a simple dice string such as 1d4,
 * 1+1d3, M80 or -3d5, or a complex one with $ variables such as $B+d$S, which `effect-expr:` binds.
 * The three token kinds come from the imported DiceStrings lexer. The set is labelled `t` because ANTLR
 * matches a set of tokens without filling in the per-token fields, so naming the tokens in the action
 * would read null. Returns the text as written; checking it waits for the roll engine.
 *
 * Rule effectDice coded on 261124, commented in full on 261124.
 */
effectDice
        returns[String dice]
        :   EFFECT_DICE t=(INTEGER | SIMPLE_DICE_STRING | COMPLEX_DICE_STRING) { $dice = $t.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `effect-msg:` followed by the rest of the line as text. Returns the text; `playerTimed` joins
 * repeats onto the open effect.
 *
 * Rule effectMsg coded before 261124, commented in full on 261124.
 */
effectMsg
        returns[String effectMessage]
        :   EFFECT_MESSAGE STRING { $effectMessage = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `effect-yx:` followed by the y and x numbers, separated by a colon. Returns both as text, to be
 * read as numbers by the assembler.
 *
 * Rule effectYX coded on 261124, commented in full on 261124.
 */
effectYX
        returns[String yStr, String xStr]
        :   EFFECT_YX y=YX_INTEGER x=YX_INTEGER {
                $yStr = $y.getText();
                $xStr = $x.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * `effect-expr:` followed by a one-letter variable, a base name and an operations string, separated
 * by colons. Binds a $ variable of the open effect's dice string to a value computed from the base.
 * Returns the three parts as text.
 *
 * Rule effectExpr coded on 261124, commented in full on 261124.
 */
effectExpr
        returns[String exprChar, String baseName, String operation]
        :   EFFECT_EXPR ch=EXPR_CHAR EXPR_COLON base=EXPR_UCASE EXPR_COLON op=EXPR_OP {
                $exprChar = $ch.getText();
                $baseName = $base.getText();
                $operation = $op.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * `resist:` followed by an element name as a flag token. Returns it raw.
 *
 * Rule resist coded before 261124, commented in full on 261124.
 */
resist
        returns[String res]
        :   RESIST FLAG { $res = $FLAG.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `brand:` followed by a brand code as a flag token. Returns it raw.
 *
 * Rule brand coded before 261124, commented in full on 261124.
 */
brand
        returns[String b]
        :   BRAND FLAG { $b = $FLAG.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `slay:` followed by a slay code as a flag token. Returns it raw.
 *
 * Rule slay coded before 261124, commented in full on 261124.
 */
slay
        returns[String s]
        :   SLAY FLAG { $s = $FLAG.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `flag-synonym:` followed by an object flag code and an integer for "exact", separated by a
 * colon. Returns both as text.
 *
 * Rule flagSynonym coded before 261124, commented in full on 261124.
 */
flagSynonym
        returns[String objProp, String val]
        :   FLAG_SYNONYM op=DELIM_BETWEEN_COLONS
            DELIM_COLON v=DELIM_INTEGER
            { $objProp = $op.getText(); $val = $v.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `lower-bound:` followed by an integer. Returns it as text.
 *
 * Rule lowerBound coded before 261124, commented in full on 261124.
 */
lowerBound
        returns[String bound]
        :   LOWER_BOUND INTEGER { $bound = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * `flags:` followed by flag names separated by blanks, bars or both. A blank or bar run may come
 * before the first name or after the last, and the list may be empty, as C's strtok with " |" reads it.
 * Returns the names in a list created in @init.
 *
 * Rule flags coded before 261124, separator reworked on 261120, commented in full on 261124.
 */
flags
        returns[List<String> flagList]
        @init {
            $flagList = new ArrayList<>();
        }
        :   FLAGS (FLAG_OR+
        |   f1=FLAG { $flagList.add($f1.getText()); })?
            (FLAG_OR (f2=FLAG { $flagList.add($f2.getText()); })?)*
        ;

/*
 * @author Rowan Crowther
 *
 * One record: a `name:` line and then any directives in any order, repeated as the header describes.
 * The @init block gives each record a fresh `effectsInit`, and the @after block splits it into the
 * begin and end chains, converting each entry to an EffectParseRecord for EffectAssembler. The effect
 * alternatives rebuild the last entry of `effectsInit` because EffectParseRecordType is a record: each
 * amend method returns a changed copy, which is written back over the old one. Returns the whole record.
 *
 * Rule playerTimed coded before 261124, updated on 261124, commented in full on 261124.
 */
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
            effectsInit = new ArrayList<>();
            List<EffectParseRecord> onBeginEffectInit = new ArrayList<>();
            List<EffectParseRecord> onEndEffectInit = new ArrayList<>();
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
            for (EffectParseRecordType recordType : effectsInit) {
                if (recordType.type().equals("end"))
                    onEndEffectInit.add(recordType.convertToRecord());
                else
                    onBeginEffectInit.add(recordType.convertToRecord());
            }
            $timed = new PlayerTimedParseRecord(nameInit, descInit, grades,
                    onEndInit, onIncreaseInit, onDecreaseInit, msgtInit,
                    fails, onBeginEffectInit, onEndEffectInit,
                    resistInit, brandInit, slayInit, flagSynonymFlagInit,
                    flagSynonymValueInit, lowerBoundInit, flags, line);
        }
        :   name { nameInit = $name.nameStr; line = $name.lineNo; }
        (   desc { descInit = descInit + $desc.descStr; }
        |   grade { String colour       = $grade.colour;
                    String max          = $grade.max;
                    String status       = $grade.status;
                    String messageUp    = $grade.messageUp;
                    String messageDown  = $grade.messageDown;
                    PlayerTimedGradeParseRecord gradeRecord =
                        new PlayerTimedGradeParseRecord(colour, max, status, messageDown, messageUp);
                    grades.add(gradeRecord);
                }
        |   onEnd { onEndInit = onEndInit + $onEnd.message; }
        |   onIncrease { onIncreaseInit = onIncreaseInit + $onIncrease.message; }
        |   onDecrease { onDecreaseInit = onDecreaseInit + $onDecrease.message; }
        |   msgt { msgtInit = $msgt.msgType; }
        |   fail { String failTypeInit = $fail.type;
                   String failValueInit = $fail.value;
                   fails.addFirst(new FailureParseRecord(failTypeInit, failValueInit)); }
        |   onBeginEffect {
                effectsInit.add(new EffectParseRecordType("begin", $onBeginEffect.type,
                                $onBeginEffect.subType, "", "", "", "", "", "", "", "", "",
                                line));
            }
        |   onEndEffect {
                effectsInit.add(new EffectParseRecordType("end", $onEndEffect.type,
                                $onEndEffect.subType, "", "", "", "", "", "", "", "", "",
                                line));
            }
        |   {!effectsInit.isEmpty()}? effectDice {
                String dice = $effectDice.dice;
                int size = effectsInit.size() - 1;
                effectsInit.set(size, effectsInit.getLast().amendDice(dice));
            }
        |   {!effectsInit.isEmpty()}? effectYX {
                String y = $effectYX.yStr;
                String x = $effectYX.xStr;
                int size = effectsInit.size() - 1;
                effectsInit.set(size, effectsInit.getLast().amendYX(y, x));
            }
        |   {!effectsInit.isEmpty()}? effectMsg {
                String message = $effectMsg.effectMessage;
                int size = effectsInit.size() - 1;
                effectsInit.set(size, effectsInit.getLast().appendMessage(message));
            }
        |   {!effectsInit.isEmpty() && !effectsInit.getLast().diceString().isEmpty()}? effectExpr {
                EffectParseRecordType last = effectsInit.getLast();
                String currentChar = last.expressionChars().isEmpty() ? $effectExpr.exprChar : last.expressionChars() + "^" + $effectExpr.exprChar;
                String currentName = last.expressionChars().isEmpty() ? $effectExpr.baseName : last.expressionBases() + "^" + $effectExpr.baseName;
                String currentOper = last.expressionChars().isEmpty() ? $effectExpr.operation : last.expressionOperations() + "^" + $effectExpr.operation;
                int size = effectsInit.size() - 1;
                effectsInit.set(size, last.amendExpression(last.diceString(), currentChar, currentName, currentOper));
            }
        |   resist { resistInit = $resist.res; }
        |   brand { brandInit = $brand.b; }
        |   slay { slayInit = $slay.s; }
        |   flagSynonym { flagSynonymFlagInit = $flagSynonym.objProp;
                          flagSynonymValueInit = $flagSynonym.val; }
        |   lowerBound { lowerBoundInit = $lowerBound.bound; }
        |   flags { flags.addAll($flags.flagList); })*
        ;

/*
 * @author Rowan Crowther
 *
 * The whole file: zero or more records and then the end of the file. Returns the records in file
 * order.
 *
 * Rule file coded before 261124, commented in full on 261124.
 */
file
        returns[List<PlayerTimedParseRecord> results]
        @init {
            $results = new ArrayList<>();
        }
        :   (playerTimed { $results.add($playerTimed.timed); })* EOF
        ;
