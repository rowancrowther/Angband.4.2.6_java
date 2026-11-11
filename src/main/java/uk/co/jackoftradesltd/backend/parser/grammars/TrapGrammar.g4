parser grammar TrapGrammar;

options { tokenVocab = TrapLexer; }

import EffectBlock;

@header {
    import uk.co.jackoftradesltd.backend.parser.trap.TrapParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

@members {
    // EffectParseRecord is a record, so "updating" the last effect means
    // swapping in a copy with one or more fields changed
    void replaceLast(List<EffectParseRecord> list, EffectParseRecord updated) {
        list.set(list.size() - 1, updated);
    }
}

name
        returns[String nameStr, String textStr, int line]
        :   NAME n=DELIMITED_STRING
            DELIMITER_COLON t=DELIMITED_STRING
            { $nameStr = $n.getText(); $textStr = $t.getText(); $line = $start.getLine(); }
        ;

graphics
        returns[String glyph, String colour]
        :   GRAPHICS g=GLYPH_VALUES GLYPH_COLON_SWITCH c=COLOUR_VALUES {
            $glyph = $g.getText(); $colour = $c.getText(); }
        ;

appear
        returns[String rarity, String minDepth, String maxNum]
        :   APPEAR r=DELIMITED_INTEGER
            DELIMITER_COLON n=DELIMITED_INTEGER
            DELIMITER_COLON x=DELIMITED_INTEGER
            {
                $rarity = $r.getText();
                $minDepth = $n.getText();
                $maxNum = $x.getText();
            }
        ;

visibility
        returns[String vis]
        :   VISIBILITY d=(DICE_SIMPLE_VALUE | DICE_COMPLEX_VALUE) { $vis = $d.getText(); }
        ;

desc
        returns[String line]
        :   DESC STRING { $line = $STRING.getText(); }
        ;

flags
        returns[List<String> flagList]
        :   { $flagList = new ArrayList<>(); }
            FLAGS f1=FLAG { $flagList.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $flagList.add($f2.getText()); })*
        ;

msg
        returns[String msgStr]
        :   MSG STRING { $msgStr = $STRING.getText(); }
        ;

save
        returns[List<String> saveFlags]
        :   { $saveFlags = new ArrayList<>(); }
            SAVE f1=FLAG { $saveFlags.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $saveFlags.add($f2.getText()); })*
        ;

msgGood
        returns[String goodMsg]
        :   MSG_GOOD STRING { $goodMsg = $STRING.getText(); }
        ;

msgBad
        returns[String badMsg]
        :   MSG_BAD STRING { $badMsg = $STRING.getText(); }
        ;


msgXtra
        returns[String msgExtra]
        :   MSG_XTRA STRING { $msgExtra = $STRING.getText(); }
        ;

effectXtra
        returns[String effectType, String effectSubtype, String radius, String parameter]
        :   { $effectSubtype = ""; $radius = ""; $parameter = ""; }
            EFFECT_XTRA t=UCASE { $effectType = $t.getText(); }
            (COLON s=UCASE { $effectSubtype = $s.getText(); }
            (COLON r=INTEGER { $radius = $r.getText(); }
            (COLON p=INTEGER { $parameter = $p.getText(); })?)?)?
        ;

diceXtra
        returns[String complexDiceValue]
        :   DICE_XTRA d=(DICE_SIMPLE_VALUE | DICE_COMPLEX_VALUE) { $complexDiceValue = $d.getText(); }
        ;

exprXtra
        returns[String exprChar, String baseName, String op]
        :   EXPR_XTRA EXPR_XTRA_CHAR
            EXPR_XTRA_COLON EXPR_XTRA_UCASE
            EXPR_XTRA_COLON EXPR_XTRA_OP
            {
                $exprChar = $EXPR_XTRA_CHAR.getText();
                $baseName = $EXPR_XTRA_UCASE.getText();
                $op = $EXPR_XTRA_OP.getText();
            }
        ;

effectYxXtra
        returns[String y, String x]
        :   EFFECT_YX_XTRA yVal=INTEGER COLON xVal=INTEGER
            { $y = $yVal.getText(); $x = $xVal.getText(); }
        ;

effectXtraBlock
        returns[String effectType, String effectSubtype, String radius, String parameter, String complexDiceValue,
                String exprChar, String baseName, String op, String msgExtra, String yVal, String xVal]
        :   { $complexDiceValue = ""; $exprChar = ""; $baseName = ""; $op = ""; $msgExtra = "";
              $yVal = ""; $xVal = ""; }
            effectXtra { $effectType = $effectXtra.effectType;
                          $effectSubtype = $effectXtra.effectSubtype;
                          $radius = $effectXtra.radius;
                          $parameter = $effectXtra.parameter; }
            
        ;

trapRecord
        returns[TrapParseRecord trap]
        @init {
            String nameInit = "";
            String descInit = "";
            StringBuilder messageSB = new StringBuilder();
            StringBuilder saveMessageSB = new StringBuilder();
            StringBuilder failMessageSB = new StringBuilder();
            StringBuilder xtraMessageSB = new StringBuilder();
            String glyphInit = "";
            String colourInit = "";
            String rarityInit = "";
            String minDepthInit = "";
            String maxNumInit = "";
            String powerInit = "";
            StringBuilder descSB = new StringBuilder();
            List<String> flagsInit = new ArrayList<>();
            List<String> saveFlagsInit = new ArrayList<>();
            List<EffectParseRecord> effectInit = new ArrayList<>();
            List<EffectParseRecord> xtraEffectInit = new ArrayList<>();
            int line = 0;
        }
        @after {
            $trap = new TrapParseRecord(nameInit, descSB.toString(),
                descInit, messageSB.toString(), saveMessageSB.toString(),
                failMessageSB.toString(), xtraMessageSB.toString(),
                glyphInit, colourInit, rarityInit,
                minDepthInit, maxNumInit, powerInit, flagsInit,
                saveFlagsInit, effectInit, xtraEffectInit, line);
        }
        :   name { line = $name.line; nameInit = $name.nameStr; descInit = $name.textStr; }
        (   graphics { glyphInit = $graphics.glyph; colourInit = $graphics.colour; }
        |   appear { rarityInit = $appear.rarity;
                     minDepthInit = $appear.minDepth;
                     maxNumInit = $appear.maxNum; }
        |   visibility { powerInit = $visibility.vis; }
        |   flags { flagsInit.addAll($flags.flagList); }
        |   save { saveFlagsInit.addAll($save.saveFlags); }
        |   desc { descSB.append($desc.line); }
        |   msg { messageSB.append($msg.msgStr); }
        |   msgGood { saveMessageSB.append($msgGood.goodMsg); }
        |   msgBad { failMessageSB.append($msgBad.badMsg); }
        |   msgXtra { xtraMessageSB.append($msgXtra.msgExtra); }
        |   effectBlock { effectInit.add(new EffectParseRecord($effectBlock.typeInit,
                                        $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                                        $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                                        $effectBlock.expressionChars, $effectBlock.expressionBase,
                                        $effectBlock.expressionOperation, 
                                        null, $effectBlock.start.getLine())); }
        |   effectYX { if (!effectInit.isEmpty()) {
                           EffectParseRecord last = effectInit.getLast();
                           EffectParseRecord updated = last.amendYX($effectYX.y, $effectYX.x);
                           replaceLast(effectInit, updated);
                        }
            }
        |   dice {
                if (!effectInit.isEmpty()) {
                    EffectParseRecord last = effectInit.getLast();
                    EffectParseRecord updated = last.amendExpression($dice.diceString,
                                                                     $dice.exprChar,
                                                                     $dice.baseName,
                                                                     $dice.operation);
                    replaceLast(effectInit, updated);
                }
            }
        |   expr {
                if (!effectInit.isEmpty() && (!effectInit.getLast().diceString().isEmpty())) {
                    EffectParseRecord last = effectInit.getLast();
                    String lastExprChar = last.expressionChars().isEmpty() ? $expr.exprChar 
                                                                           : last.expressionChars() + "^" + $expr.exprChar;
                    String lastExprBase = last.expressionBases().isEmpty() ? $expr.baseName
                                                                           : last.expressionBases() + "^" + $expr.baseName;
                    String lastExprOps = last.expressionOperations().isEmpty() ? $expr.operation 
                                                                           : last.expressionOperations() + "^" + $expr.operation;
                    String lastDiceString = last.diceString();
                    EffectParseRecord updated = last.amendExpression(lastDiceString,
                                                                     lastExprChar,
                                                                     lastExprBase,
                                                                     lastExprOps);
                    replaceLast(effectInit, updated);
                }
            }
        |   effectXtraBlock { xtraEffectInit.add(new EffectParseRecord(
                                        $effectXtraBlock.effectType, $effectXtraBlock.effectSubtype,
                                        $effectXtraBlock.radius, $effectXtraBlock.parameter,
                                        $effectXtraBlock.complexDiceValue,
                                        $effectXtraBlock.yVal, $effectXtraBlock.xVal,
                                        $effectXtraBlock.exprChar, $effectXtraBlock.baseName,
                                        $effectXtraBlock.op, "", $effectXtraBlock.start.getLine()));
                            } 
        |   effectYxXtra { 
                if (!xtraEffectInit.isEmpty()) {
                    EffectParseRecord last = xtraEffectInit.getLast();
                    EffectParseRecord updated = last.amendYX($effectYxXtra.y, $effectYxXtra.x);
                    replaceLast(xtraEffectInit, updated); 
                }
            }
        |   diceXtra {
                if (!xtraEffectInit.isEmpty()) {
                    String complexDiceVal = $diceXtra.complexDiceValue;
                    EffectParseRecord last = xtraEffectInit.getLast();
                    String chars = "";
                    String base = "";
                    String ops = "";
                    EffectParseRecord updated = last.amendExpression(complexDiceVal, chars, base, ops);
                    replaceLast(xtraEffectInit, updated);
                }
            }
        |   exprXtra {
                if (!xtraEffectInit.isEmpty() && (!xtraEffectInit.getLast().diceString().isEmpty())) {
                    EffectParseRecord last = xtraEffectInit.getLast();
                    String diceVal = last.diceString();
                    String chars = last.expressionChars() + (!last.expressionChars().isEmpty() ? "^" : "") + $exprXtra.exprChar;
                    String base = last.expressionBases() + (!last.expressionBases().isEmpty() ? "^" : "") + $exprXtra.baseName;
                    String op = last.expressionOperations() + (!last.expressionOperations().isEmpty() ? "^" : "") + $exprXtra.op; 
                    EffectParseRecord updated = last.amendExpression(diceVal, chars, base, op);
                    replaceLast(xtraEffectInit, updated);            
                }
            })+
        ;

file
        returns[List<TrapParseRecord> traps]
        :   { $traps = new ArrayList<>(); }
            (trapRecord { $traps.add($trapRecord.trap); })+ EOF
        ;
