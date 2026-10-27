parser grammar MonsterSpellGrammar;

options { tokenVocab = MonsterSpellLexer; }

import EffectBlock;

@header {
    import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord;
    import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord.MonsterSpellLevelParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER {$count = $INTEGER.getText(); }
        ;

name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

msgt
        returns[String msgTStr]
        :   MSGT STRING { $msgTStr = $STRING.getText(); }
        ;

hit
        returns[String hitVal]
        :   HIT INTEGER { $hitVal = $INTEGER.getText(); }
        ;

powerCutoff
        returns [String cutoffVal]
        :   POWER_CUTOFF INTEGER { $cutoffVal = $INTEGER.getText(); }
        ;

lore
        returns[String loreStr]
        :   LORE STRING { $loreStr = $STRING.getText(); }
        ;

loreColourBase
        returns[String colourBase]
        :   LORE_COLOUR_BASE STRING { $colourBase = $STRING.getText(); }
        ;

loreColourResist
        returns[String colourResist]
        :   LORE_COLOUR_RESIST STRING { $colourResist = $STRING.getText(); }
        ;

loreColourImmune
        returns[String colourImmune]
        :   LORE_COLOUR_IMMUNE STRING { $colourImmune = $STRING.getText(); }
        ;

messageSave
        returns[String saveMsg]
        :   MESSAGE_SAVE STRING { $saveMsg = $STRING.getText(); }
        ;

messageVis
        returns[String visMsg]
        :   MESSAGE_VIS STRING { $visMsg = $STRING.getText(); }
        ;

messageInvis
        returns[String invisMsg]
        :   MESSAGE_INVIS STRING { $invisMsg = $STRING.getText(); }
        ;

messageMiss
        returns[String missMsg]
        :   MESSAGE_MISS STRING { $missMsg = $STRING.getText(); }
        ;

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
                      $effectBlock.expressionOperation, $effectBlock.effectMessage,
                      $effectBlock.start.getLine())); }
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
        
file
        returns[String declaredRecordCount, List<MonsterSpellParseRecord> monsterSpells]
        :   recordCount { $declaredRecordCount = $recordCount.count;
                          $monsterSpells = new ArrayList<>(); }
            (monsterSpell { $monsterSpells.add($monsterSpell.spell); } )+ EOF
        ;
