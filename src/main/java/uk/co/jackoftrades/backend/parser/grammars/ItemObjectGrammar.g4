parser grammar ItemObjectGrammar;

options { tokenVocab = ItemObjectLexer; }

import EffectBlock;

@header {
    import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
    import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectParseRecord;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameStr, int line]
        :   NAME OBJECT_STRING { $nameStr = $OBJECT_STRING.getText();
                                 $line = $start.getLine(); }
        ;

tval
        returns[String tvalStr]
        :   TYPE OBJECT_STRING { $tvalStr = $OBJECT_STRING.getText(); }
        ;

graphics
        returns[String glyph, String colour]
        :   GRAPHICS g=GLYPH_VALUES GLYPH_COLON_SWITCH c=COLOUR_VALUES {
                $glyph  = $g.getText();
                $colour = $c.getText();
            }
        ;

level
        returns[String levelStr]
        :   LEVEL INTEGER { $levelStr = $INTEGER.getText(); }
        ;

weight
        returns[String weightStr]
        :   WEIGHT INTEGER { $weightStr = $INTEGER.getText(); }
        ;

cost
        returns[String costStr]
        :   COST INTEGER { $costStr = $INTEGER.getText(); }
        ;

attack
        returns[String base, String toh, String tod]
        :   ATTACK b=RAND_VALUE RAND_COLON h=RAND_VALUE RAND_COLON d=RAND_VALUE {
                $base = $b.getText();
                $toh  = $h.getText();
                $tod  = $d.getText();
            }
        ;

armour
        returns[String base, String toa]
        :   ARMOUR b=RAND_VALUE RAND_COLON a=RAND_VALUE {
                $base = $b.getText();
                $toa  = $a.getText();
            }
        ;

alloc
        returns[String common, String lower, String upper]
        :   ALLOC c=ALLOC_INT ALLOC_COLON l=ALLOC_INT ALLOC_TO u=ALLOC_INT {
                $common = $c.getText();
                $lower  = $l.getText();
                $upper  = $u.getText();
            }
        ;

charges
        returns[String chargesRnd]
        :   CHARGES c=RAND_VALUE { $chargesRnd = $c.getText(); }
        ;

pile
        returns[String chance, String noOfItems]
        :   PILE c=RAND_VALUE RAND_COLON n=RAND_VALUE {
                $chance    = $c.getText();
                $noOfItems = $n.getText();
            }
        ;

power
        returns[String powerInt]
        :   POWER INTEGER { $powerInt = $INTEGER.getText(); }
        ;

msg
        returns[String message]
        :   MSG OBJECT_STRING { $message = $OBJECT_STRING.getText(); }
        ;

visMsg
        returns[String vMessage]
        :   VIS_MSG OBJECT_STRING { $vMessage = $OBJECT_STRING.getText(); }
        ;

flags
        returns[List<String> flagList]
        :   { $flagList = new ArrayList<>(); }
            FLAGS f1=FLAG { $flagList.add($f1.getText()); } (
            FLAG_OR f2=FLAG { $flagList.add($f2.getText()); }
            )* FLAG_OR?
        ;

values
        returns[Map<String, String> valueMap]
        :   { $valueMap = new HashMap<>(); }
            VALUES vt1=VALUE_TYPE VALUE_LBRACKET vi1=VALUE_INT VALUE_RBRACKET {
                $valueMap.put($vt1.getText(), $vi1.getText());
            }(VALUE_OR vt2=VALUE_TYPE VALUE_LBRACKET vi2=VALUE_INT VALUE_RBRACKET {
                    $valueMap.put($vt2.getText(), $vi2.getText());
                }
            )*
        ;

brand
        returns[String brandStr]
        :   BRAND OBJECT_STRING { $brandStr = $OBJECT_STRING.getText(); }
        ;

slay
        returns[String slayStr]
        :   SLAY OBJECT_STRING { $slayStr = $OBJECT_STRING.getText(); }
        ;

curse
        returns[String curseName, String cursePower]
        :   CURSE n=CURSE_STRING CURSE_COLON p=CURSE_INT {
                $curseName  = $n.getText();
                $cursePower = $p.getText();
            }
        ;

pval
        returns[String pvalInt]
        :   PVAL RAND_VALUE { $pvalInt = $RAND_VALUE.getText(); }
        ;

desc
        returns[String descStr]
        :   DESC OBJECT_STRING { $descStr = $OBJECT_STRING.getText(); }
        ;

itemObject
        returns[ItemObjectParseRecord object]
        @init {
            String nameInit = "";
            int line = 0;
            String typeInit = "";
            String glyphInit = "";
            String colourInit = "";
            String levelInit = "";
            String weightInit = "";
            String costInit = "";
            String atBaseInit = "";
            String atTohInit = "";
            String atTodInit = "";
            String arBaseInit = "";
            String arToaInit = "";
            String alCommonInit = "";
            String alLowerInit = "";
            String alUpperInit = "";
            String chargesInit = "";
            String pileChanceInit = "";
            String pileItemsInit = "";
            String powerInit = "";
            String msgInit = "";
            String visMsgInit = "";
            List<EffectParseRecord> effectsInit = new ArrayList<>();
            List<String> flagListInit = new ArrayList<>();
            Map<String, String> valuesInit = new HashMap<>();
            List<String> brandInit = new ArrayList<>();
            List<String> slayInit = new ArrayList<>();
            Map<String, String> curseInit = new HashMap<>();
            String pValInit = "";
            StringBuilder dsb = new StringBuilder();
            StringBuilder msb = new StringBuilder();
            StringBuilder vsb = new StringBuilder();
            String descInit = "";
        }
        @after {
            descInit = dsb.toString();
            msgInit = msb.toString();
            visMsgInit = vsb.toString();
            $object = new ItemObjectParseRecord(nameInit, glyphInit, colourInit, typeInit,
                    levelInit, weightInit, costInit, atBaseInit, atTohInit, atTodInit,
                    arBaseInit, arToaInit, alCommonInit, alLowerInit, alUpperInit,
                    chargesInit, pileChanceInit, pileItemsInit, powerInit, msgInit,
                    visMsgInit, effectsInit, flagListInit, valuesInit, brandInit, slayInit,
                    curseInit, pValInit, descInit, line);
        }
        :   name { nameInit = $name.nameStr;
                   line = $name.line; }
        (   tval { typeInit = $tval.tvalStr; }
        |   graphics { glyphInit = $graphics.glyph;
                       colourInit = $graphics.colour; }
        |   level { levelInit = $level.levelStr; }
        |   weight { weightInit = $weight.weightStr; }
        |   cost { costInit = $cost.costStr; }
        |   attack { atBaseInit = $attack.base;
                     atTohInit = $attack.toh;
                     atTodInit = $attack.tod; }
        |   armour { arBaseInit = $armour.base;
                     arToaInit = $armour.toa; }
        |   alloc { alCommonInit = $alloc.common;
                    alLowerInit = $alloc.lower;
                    alUpperInit = $alloc.upper; }
        |   charges { chargesInit = $charges.chargesRnd; }
        |   pile { pileChanceInit = $pile.chance;
                   pileItemsInit = $pile.noOfItems; }
        |   power { powerInit = $power.powerInt; }
        |   msg { msb.append($msg.message); }
        |   visMsg { vsb.append($visMsg.vMessage); }
        |   effectBlock { effectsInit.add(new EffectParseRecord($effectBlock.typeInit,
                                        $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                                        $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                                        $effectBlock.expressionChars, $effectBlock.expressionBase,
                                        $effectBlock.expressionOperation, $effectBlock.timeDiceString,
                                        $effectBlock.effectMessage, $effectBlock.start.getLine())); }
        |   flags { flagListInit.addAll($flags.flagList); }
        |   values { valuesInit.putAll($values.valueMap); }
        |   brand { brandInit.add($brand.brandStr); }
        |   slay { slayInit.add($slay.slayStr); }
        |   curse { curseInit.put($curse.curseName, $curse.cursePower); }
        |   pval { pValInit = $pval.pvalInt; }
        |   desc { dsb.append($desc.descStr); }
        )+
        ;

file
        returns[String declaredRecordCount, List<ItemObjectParseRecord> itemObjects]
        :   recordCount { $itemObjects = new ArrayList<>();
                          $declaredRecordCount = $recordCount.count; }
            (itemObject { $itemObjects.add($itemObject.object); } )+ EOF
        ;
