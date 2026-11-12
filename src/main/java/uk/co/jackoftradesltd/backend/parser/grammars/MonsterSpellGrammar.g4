// Parser for lib/gamedata/monster_spell.txt (tokens from MonsterSpellLexer; the effect, effect-yx,
// dice and expr rules come from the imported EffectBlock). Each rule collects one directive's
// value(s) as raw text into a MonsterSpellParseRecord; no lookup, conversion or validation
// happens here - that is MonsterSpellAssembler's job. See MonsterSpellParseRecord for the
// field-by-field meaning of what these rules capture.
//
// The C original is init_parse_mon_spell in src/mon-init.c. It keeps a "current spell" in the
// parser's private data and walks to the last entry of the spell's level chain for every lore,
// colour and message line. Here the current spell is the monsterSpell rule's locals, and the
// tiers are collected as the levelDirectives and powerCutoffBlock runs below.
parser grammar MonsterSpellGrammar;

options { tokenVocab = MonsterSpellLexer; }

import EffectBlock;

// The record types the actions build, and the lists they fill.
@header {
    import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord;
    import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord.MonsterSpellLevelParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

// Members of the generated parser, used by the actions in monsterSpell below.
@members {
             // EffectParseRecord is a record, so "updating" the last effect means
             // swapping in a copy with one or more fields changed
             void replaceLast(List<EffectParseRecord> list, EffectParseRecord updated) {
                 list.set(list.size() - 1, updated);
             }
         }

// "name:<spell>" - opens a record. The source line is captured too so the assembler can
// attribute any error to the right line in the file.
//
// Rule name coded before 261112, commented in full on 261112.
name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

// "msgt:<type>" - the message type for the spell's visible, invisible and miss messages, kept
// as text. C reads it as a "sym" (up to the next ':'); here it is the rest of the line.
//
// Rule msgt coded before 261112, commented in full on 261112.
msgt
        returns[String msgTStr]
        :   MSGT STRING { $msgTStr = $STRING.getText(); }
        ;

// "hit:<n>" - the base chance to hit, kept as text (C: "uint"); the assembler turns it into a
// number and refuses a '-'.
//
// Rule hit coded before 261112, commented in full on 261112.
hit
        returns[String hitVal]
        :   HIT HIT_UINT { $hitVal = $HIT_UINT.getText(); }
        ;

// "power-cutoff:<n>" - the spell power at which a new tier of lore and messages starts, kept
// as text. C reads an "int". The EFFECT_INTEGER token is the one shared with the effect rules:
// an optional '+' or '-', an optional 0x prefix, digits (a-f included) and any tail up to a ':'
// or the end of the line, all kept as text. The assembler reads it as strtol in base 0 does
// (hex with 0x, octal with a leading 0) and refuses one with no digit in it.
//
// Rule powerCutoff coded before 261112, commented in full on 261112, updated on 261112 to
// describe EFFECT_INTEGER as the lexer reads it.
powerCutoff
        returns [String cutoffVal]
        :   POWER_CUTOFF EFFECT_INTEGER { $cutoffVal = $EFFECT_INTEGER.getText(); }
        ;

// "lore:<text>" - one line of the tier's lore description. Repeated lines are joined by
// levelDirectives.
//
// Rule lore coded before 261112, commented in full on 261112.
lore
        returns[String loreStr]
        :   LORE STRING { $loreStr = $STRING.getText(); }
        ;

// "lore-color-base:<colour>" - the tier's lore colour, a code or a name, kept as text for the
// assembler to resolve.
//
// Rule loreColourBase coded before 261112, commented in full on 261112.
loreColourBase
        returns[String colourBase]
        :   LORE_COLOUR_BASE STRING { $colourBase = $STRING.getText(); }
        ;

// "lore-color-resist:<colour>" - the tier's lore colour when the spell is resisted. Same shape
// as loreColourBase.
//
// Rule loreColourResist coded before 261112, commented in full on 261112.
loreColourResist
        returns[String colourResist]
        :   LORE_COLOUR_RESIST STRING { $colourResist = $STRING.getText(); }
        ;

// "lore-color-immune:<colour>" - the tier's lore colour when the player is immune. Same shape
// as loreColourBase.
//
// Rule loreColourImmune coded before 261112, commented in full on 261112.
loreColourImmune
        returns[String colourImmune]
        :   LORE_COLOUR_IMMUNE STRING { $colourImmune = $STRING.getText(); }
        ;

// "message-save:<text>" - one line of the message shown when the player saves. Repeated lines
// are joined by levelDirectives.
//
// Rule messageSave coded before 261112, commented in full on 261112.
messageSave
        returns[String saveMsg]
        :   MESSAGE_SAVE STRING { $saveMsg = $STRING.getText(); }
        ;

// "message-vis:<text>" - one line of the message shown when the cast is seen. Repeated lines
// are joined by levelDirectives.
//
// Rule messageVis coded before 261112, commented in full on 261112.
messageVis
        returns[String visMsg]
        :   MESSAGE_VIS STRING { $visMsg = $STRING.getText(); }
        ;

// "message-invis:<text>" - one line of the message shown when the cast is not seen. Repeated
// lines are joined by levelDirectives.
//
// Rule messageInvis coded before 261112, commented in full on 261112.
messageInvis
        returns[String invisMsg]
        :   MESSAGE_INVIS STRING { $invisMsg = $STRING.getText(); }
        ;

// "message-miss:<text>" - one line of the message shown when the spell misses. Repeated lines
// are joined by levelDirectives.
//
// Rule messageMiss coded before 261112, commented in full on 261112.
messageMiss
        returns[String missMsg]
        :   MESSAGE_MISS STRING { $missMsg = $STRING.getText(); }
        ;

// One run of one or more lore, colour and message lines, in any order - the contents of one
// tier. Mirrors C, where each such line edits the last tier in the spell's chain:
//  - lore and the four message directives are joined with the text already there, as C's
//    string_append does, so a repeated line adds to the text rather than replacing it;
//  - the three colour directives replace each other, so the last one written wins, as in C;
//  - a directive the run omits keeps its starting value: "" for the text fields, null for the
//    colours (the assembler turns an absent colour into the dark colour).
// The rule also returns the line the run began on, for error messages.
//
// A run on its own, outside a power-cutoff:, is the tier before the first cutoff. A run split
// by effect lines gives more than one record here; the level assembler merges a record with
// no power into the one before it, which is how C ends up with one tier.
//
// Rule levelDirectives coded before 261112, commented in full on 261112.
levelDirectives                                                            
          returns[String loreStr, String loreColourBaseStr, String loreColourResistStr,
                  String loreColourImmuneStr, String messageVisStr, String messageInvisStr,
                  String messageMissStr, String messageSaveStr, Integer line]
          : {     $line = $start.getLine();
                  $loreStr = "";
                  $loreColourBaseStr = null;
                  $loreColourResistStr = null;
                  $loreColourImmuneStr = null;
                  $messageVisStr = "";
                  $messageInvisStr = "";
                  $messageMissStr = "";
                  $messageSaveStr = ""; }
              (   lore { $loreStr = $loreStr + $lore.loreStr; }
              |   loreColourBase { $loreColourBaseStr = $loreColourBase.colourBase; }
              |   loreColourResist { $loreColourResistStr = $loreColourResist.colourResist; }
              |   loreColourImmune { $loreColourImmuneStr =  $loreColourImmune.colourImmune; }
              |   messageVis { $messageVisStr = $messageVisStr + $messageVis.visMsg; }
              |   messageInvis { $messageInvisStr = $messageInvisStr + $messageInvis.invisMsg; }
              |   messageMiss { $messageMissStr = $messageMissStr + $messageMiss.missMsg; }
              |   messageSave { $messageSaveStr = $messageSaveStr + $messageSave.saveMsg; }
              )+
          ;

// "power-cutoff:<n>" with the optional run of lore, colour and message lines that follows it:
// one tier with a power. The text and colour fields start as in levelDirectives and are copied
// from the run when there is one, so a power-cutoff: with nothing after it gives a tier of
// empty text and null colours. The line returned is that of the power-cutoff: line.
//
// Rule powerCutoffBlock coded before 261112, commented in full on 261112.
powerCutoffBlock
          returns[String powerCutOffVal, Integer line,
                  String loreStr, String loreColourBaseStr, String loreColourResistStr,
                  String loreColourImmuneStr, String messageVisStr, String messageInvisStr,
                  String messageMissStr, String messageSaveStr]
          :     { $loreStr = "";
                  $loreColourBaseStr = null;
                  $loreColourResistStr = null;
                  $loreColourImmuneStr = null;
                  $messageVisStr = "";
                  $messageInvisStr = "";
                  $messageMissStr = "";
                  $messageSaveStr = ""; }
                  powerCutoff { $powerCutOffVal = $powerCutoff.cutoffVal;
                            $line = $powerCutoff.start.getLine(); }
              (   levelDirectives { $loreStr = $levelDirectives.loreStr;
                                    $loreColourBaseStr = $levelDirectives.loreColourBaseStr;
                                    $loreColourResistStr = $levelDirectives.loreColourResistStr;
                                    $loreColourImmuneStr = $levelDirectives.loreColourImmuneStr;
                                    $messageVisStr = $levelDirectives.messageVisStr;
                                    $messageInvisStr = $levelDirectives.messageInvisStr;
                                    $messageMissStr = $levelDirectives.messageMissStr;
                                    $messageSaveStr = $levelDirectives.messageSaveStr; }
              )?                                                             
          ;

// One whole spell record: a name: line followed by its directives in any order.
// @init seeds the scalars with empty defaults so a directive the record omits still has a
// well-formed value, and creates the two lists the alternatives fill. msgt: and hit: keep the
// last value written (C overwrites the field). @after packs the values into the
// MonsterSpellParseRecord, in field order.
//
// The alternatives:
//  - effectBlock: an effect: line with its effect-yx: and dice: (and the expr: lines under a
//    complex dice), taken together. It adds a new EffectParseRecord to the effects, in file
//    order. Monster spells have no effect message, so that field is null.
//  - effectYX, dice, expr: one of those lines met on its own, which amends the most recent
//    effect. An EffectParseRecord is immutable, so the last one is swapped for an amended copy
//    (see replaceLast). As in C, a line with no effect before it is dropped without an error
//    ("assume that this is human and not parser error"), and an expr: with no dice string on
//    that effect is dropped too. A later dice: replaces the earlier dice string and its
//    expressions, as C replaces the dice object; a later expr: is joined to the expressions
//    already there with a '^', which keeps the characters, bases and operations in step.
//  - powerCutoffBlock, levelDirectives: add a tier to the levels list, in file order. A tier
//    from levelDirectives has a null power.
//
// Rule monsterSpell coded before 261112, commented in full on 261112.
 monsterSpell                                                               
        returns[MonsterSpellParseRecord spell]
        @init {
          String nameStrInit = "";
          String msgtStrInit = "";
          String hitInit = "";
          int lineInit = 0;
          List<EffectParseRecord> effects = new ArrayList<>();
          List<MonsterSpellParseRecord.MonsterSpellLevelParseRecord> levels = new ArrayList<>();
        }
        @after {
          $spell = new MonsterSpellParseRecord(nameStrInit, msgtStrInit, hitInit,                                                                   
                  effects, levels, lineInit);
        }
        :   name { nameStrInit = $name.nameStr; lineInit = $name.line; }
        (   msgt { msgtStrInit = $msgt.msgTStr; }
        |   hit { hitInit = $hit.hitVal; }
        |   effectBlock { effects.add(new EffectParseRecord($effectBlock.typeInit,
                  $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                  $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                  $effectBlock.expressionChars, $effectBlock.expressionBase,
                  $effectBlock.expressionOperation, null,
                  $effectBlock.start.getLine())); }
        |   effectYX {
                if (!effects.isEmpty()) {
                    EffectParseRecord last = effects.getLast();
                    EffectParseRecord updated = last.amendYX($effectYX.y, $effectYX.x);
                    replaceLast(effects, updated);
                }
            }
        |   dice {
                if (!effects.isEmpty()) {
                    EffectParseRecord last = effects.getLast();
                    String diceStr = $dice.diceString;
                    EffectParseRecord updated = last.amendExpression(diceStr,
                                                                     $dice.exprChar,
                                                                     $dice.baseName,
                                                                     $dice.operation);
                    replaceLast(effects, updated);
                }
            }
        |   expr {
                if (!effects.isEmpty() && !effects.getLast().diceString().isEmpty()) {
                    EffectParseRecord last = effects.getLast();
                    String lastDiceStr  = last.diceString();
                    String lastExprChar = last.expressionChars().isEmpty() 
                                        ? $expr.exprChar
                                        : last.expressionChars() + "^" + $expr.exprChar;
                    String lastExprBase = last.expressionBases().isEmpty() 
                                        ? $expr.baseName
                                        : last.expressionBases() + "^" + $expr.baseName;
                    String lastExprOp   = last.expressionOperations().isEmpty() 
                                        ? $expr.operation
                                        : last.expressionOperations() + "^" + $expr.operation;
                    EffectParseRecord updated = last.amendExpression(lastDiceStr,
                                                lastExprChar,
                                                lastExprBase,
                                                lastExprOp);
                    replaceLast(effects, updated);
                }
            }
        |   powerCutoffBlock { levels.add(new MonsterSpellParseRecord.MonsterSpellLevelParseRecord(
                  $powerCutoffBlock.powerCutOffVal, $powerCutoffBlock.loreStr,
                  $powerCutoffBlock.loreColourBaseStr, $powerCutoffBlock.loreColourResistStr,
                  $powerCutoffBlock.loreColourImmuneStr, $powerCutoffBlock.messageVisStr,
                  $powerCutoffBlock.messageInvisStr, $powerCutoffBlock.messageMissStr,
                  $powerCutoffBlock.messageSaveStr, $powerCutoffBlock.line)); }
        |   levelDirectives { levels.add(new MonsterSpellParseRecord.MonsterSpellLevelParseRecord(
                  null, $levelDirectives.loreStr,
                  $levelDirectives.loreColourBaseStr, $levelDirectives.loreColourResistStr,
                  $levelDirectives.loreColourImmuneStr, $levelDirectives.messageVisStr,
                  $levelDirectives.messageInvisStr, $levelDirectives.messageMissStr,
                  $levelDirectives.messageSaveStr, $levelDirectives.line)); } )+                                                                 
        ;
        
// The whole file: one or more spell records, then EOF (which forces the parser to consume every
// record rather than stopping early).
//
// Rule file coded before 261112, commented in full on 261112.
file
        returns[List<MonsterSpellParseRecord> monsterSpells]
        :   { $monsterSpells = new ArrayList<>(); }
            (monsterSpell { $monsterSpells.add($monsterSpell.spell); } )+ EOF
        ;
