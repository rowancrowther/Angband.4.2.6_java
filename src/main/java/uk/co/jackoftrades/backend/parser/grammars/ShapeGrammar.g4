parser grammar ShapeGrammar;

options { tokenVocab = ShapeLexer; }

import EffectBlock;

@header {
    import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
    import uk.co.jackoftrades.backend.parser.shape.ShapeParseRecord;

    import java.util.List;
    import java.util.Map;
    import java.util.ArrayList;
    import java.util.HashMap;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameStr, int lineNo]
        :   NAME STRING { $nameStr = $STRING.getText();
                          $lineNo = $start.getLine(); }
        ;

combat
        returns[String toh, String tod, String toa]
        :   COMBAT h=INTEGER COLON d=INTEGER COLON a=INTEGER {
                $toh = $h.getText();
                $tod = $d.getText();
                $toa = $a.getText();
            }
        ;

skillDisarmP
        returns[String skillVal]
        :   SKILL_D_P INTEGER { $skillVal = $INTEGER.getText(); }
        ;

skillDisarmM
        returns[String skillVal]
        :   SKILL_D_M INTEGER { $skillVal = $INTEGER.getText(); }
        ;

skillSave
        returns[String skillVal]
        :   SKILL_SAVE INTEGER { $skillVal = $INTEGER.getText(); }
        ;

skillStealth
        returns[String skillVal]
        :   SKILL_STEALTH INTEGER { $skillVal = $INTEGER.getText(); }
        ;

skillSearch
        returns[String skillVal]
        :   SKILL_SEARCH INTEGER { $skillVal = $INTEGER.getText(); }
        ;

skillMelee
        returns[String skillVal]
        :   SKILL_MELEE INTEGER { $skillVal = $INTEGER.getText(); }
        ;

skillThrow
        returns[String skillVal]
        :   SKILL_THROW INTEGER { $skillVal = $INTEGER.getText(); }
        ;

skillDig
        returns[String skillVal]
        :   SKILL_DIG INTEGER { $skillVal = $INTEGER.getText(); }
        ;

objFlags
        returns[List<String> flags]
        :   { $flags = new ArrayList<>(); }
            OBJ_FLAGS f1=FLAG {
                $flags.add($f1.getText());
            } (FLAG_OR f2=FLAG{
                $flags.add($f2.getText());
            })*
        ;

playerFlags
        returns[List<String> flags]
        :   { $flags = new ArrayList<>(); }
            PLAYER_FLAGS f1=FLAG {
                $flags.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $flags.add($f2.getText());
            })*
        ;

values
        returns[Map<String, String> valueMap]
        :   {
            $valueMap = new HashMap<>();
        } VALUES vm1=VALUE_MOD LBRACKET vi1=VALUE_INT RBRACKET {
            $valueMap.put($vm1.getText(), $vi1.getText());
        } (VALUE_OR vm2=VALUE_MOD LBRACKET vi2=VALUE_INT RBRACKET {
            $valueMap.put($vm2.getText(), $vi2.getText());
        })*
        ;

blow
        returns[String blowStr]
        :   BLOW STRING { $blowStr = $STRING.getText(); }
        ;

shape
        returns[ShapeParseRecord shapeInit]
        @init {
            String nameInit = "";
            String combatTohInit = "";
            String combatTodInit = "";
            String combatToaInit = "";
            String skillDisarmPhysInit = "";
            String skillDisarmMagicInit = "";
            String skillSaveInit = "";
            String skillStealthInit = "";
            String skillSearchInit = "";
            String skillMeleeInit = "";
            String skillThrowInit = "";
            String skillDigInit = "";
            List<String> oFlagsInit = new ArrayList<>();
            List<String> pFlagsInit = new ArrayList<>();
            Map<String, String> valuesInit = new HashMap<>();
            List<EffectParseRecord> effectsInit = new ArrayList<>();
            List<String> blowsInit = new ArrayList<>();
            int line = 0;
        }
        @after {
            $shapeInit = new ShapeParseRecord(nameInit,
                combatTohInit, combatTodInit, combatToaInit,
                skillDisarmPhysInit, skillDisarmMagicInit,
                skillSaveInit, skillStealthInit, skillSearchInit,
                skillMeleeInit, skillThrowInit, skillDigInit,
                oFlagsInit, pFlagsInit, valuesInit,
                effectsInit, blowsInit, line);
        }
        :   name { nameInit = $name.nameStr;
                   line = $name.lineNo; }
        (   combat {
                combatTohInit = $combat.toh;
                combatTodInit = $combat.tod;
                combatToaInit = $combat.toa;
            }
        |   skillDisarmP { skillDisarmPhysInit = $skillDisarmP.skillVal; }
        |   skillDisarmM { skillDisarmMagicInit= $skillDisarmM.skillVal; }
        |   skillSave { skillSaveInit = $skillSave.skillVal; }
        |   skillStealth { skillStealthInit = $skillStealth.skillVal; }
        |   skillSearch { skillSearchInit = $skillSearch.skillVal; }
        |   skillMelee { skillMeleeInit = $skillMelee.skillVal; }
        |   skillThrow { skillThrowInit = $skillThrow.skillVal; }
        |   skillDig { skillDigInit = $skillDig.skillVal; }
        |   objFlags { oFlagsInit.addAll($objFlags.flags); }
        |   playerFlags { pFlagsInit.addAll($playerFlags.flags); }
        |   values { valuesInit.putAll($values.valueMap); }
        |   effectBlock {  effectsInit.add(new EffectParseRecord($effectBlock.typeInit,
                                          $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                                          $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                                          $effectBlock.expressionChars, $effectBlock.expressionBase,
                                          $effectBlock.expressionOperation, $effectBlock.timeDiceString,
                                          $effectBlock.effectMessage, $effectBlock.start.getLine())); }
        |   blow { blowsInit.add($blow.blowStr); })*
        ;

file
        returns[List<ShapeParseRecord> shapes, String declaredRecordCount]
        :   recordCount { $declaredRecordCount = $recordCount.count;
                          $shapes = new ArrayList<>(); }
            (shape { $shapes.add($shape.shapeInit); })+ EOF
        ;