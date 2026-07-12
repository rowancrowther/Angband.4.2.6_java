parser grammar EgoItemsGrammar;

options { tokenVocab = EgoItemsLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.egoitem.EgoItemParseRecord;
    import uk.co.jackoftrades.backend.parser.egoitem.EgoItemParseRecord.ItemRef;

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
        returns[String nameStr, int lineNo]
        :   NAME STRING { $nameStr = $STRING.getText();
                          $lineNo = $start.getLine(); }
        ;

info
        returns[String cost, String rating]
        :   INFO c=INTEGER COLON r=INTEGER {
                $cost = $c.getText();
                $rating = $r.getText();
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

combat
        returns[String toh, String tod, String toa]
        :   COMBAT h=COMBAT_DICE COMBAT_COLON d=COMBAT_DICE COMBAT_COLON a=COMBAT_DICE {
                $toh = $h.getText();
                $tod = $d.getText();
                $toa = $a.getText();
            }
        ;

minCombat
        returns[String minToh, String minTod, String minToa]
        :   MIN_COMBAT h=INTEGER COLON d=INTEGER COLON a=INTEGER {
                $minToh = $h.getText();
                $minTod = $d.getText();
                $minToa = $a.getText();
            }
        ;

type
        returns[String tVal]
        :   TYPE STRING { $tVal = $STRING.getText(); }
        ;

item
        returns[String tVal, String sVal]
        :   ITEM t=ITEM_STRING ITEM_COLON s=ITEM_STRING {
                $tVal = $t.getText();
                $sVal = $s.getText();
            }
        ;

flags
        returns[List<String> flagList]
        :   { $flagList = new ArrayList<>(); }
            FLAGS f1=FLAG {
               $flagList.add($f1.getText().trim());
            } (FLAG_OR f2=FLAG {
               $flagList.add($f2.getText().trim());
            })* FLAG_OR?
        ;

flagsOff
        returns[List<String> flagList]
        :   { $flagList = new ArrayList<>(); }
            FLAGS_OFF f1=FLAG {
               $flagList.add($f1.getText().trim());
            } (FLAG_OR f2=FLAG {
               $flagList.add($f2.getText().trim());
            })* FLAG_OR?
        ;

values
        returns[Map<String, String> valueMap]
        :   { $valueMap = new HashMap<>(); }
            VALUES l1=VALUE_FLAG VALUE_LBRACKET v1=VALUE_DICE VALUE_RBRACKET {
                $valueMap.put($l1.getText(), $v1.getText());
            }
            (VALUE_OR l2=VALUE_FLAG VALUE_LBRACKET v2=VALUE_DICE VALUE_RBRACKET {
                $valueMap.put($l2.getText(), $v2.getText());
            })*
        ;

minValues
        returns[Map<String, String> valueMap]
        :   { $valueMap = new HashMap<>(); }
            MIN_VALUES l1=VALUE_FLAG VALUE_LBRACKET v1=VALUE_DICE VALUE_RBRACKET {
                $valueMap.put($l1.getText(), $v1.getText());
            }
            (VALUE_OR l2=VALUE_FLAG VALUE_LBRACKET v2=VALUE_DICE VALUE_RBRACKET {
                $valueMap.put($l2.getText(), $v2.getText());
            })*
        ;

act
        returns[String activation]
        :   ACT STRING { $activation = $STRING.getText(); }
        ;

time
        returns[String timeStr]
        :   TIME t=COMBAT_DICE { $timeStr = $t.getText(); }
        ;

brand
        returns[String brandStr]
        :   BRAND STRING { $brandStr = $STRING.getText(); }
        ;

slay
        returns[String slayStr]
        :   SLAY STRING { $slayStr = $STRING.getText(); }
        ;

curse
        returns[String curseName, String cursePower]
        :   CURSE n=CURSE_NAME CURSE_COLON p=CURSE_NAME {
                $curseName = $n.getText();
                $cursePower = $p.getText();
            }
        ;

desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

egoItem
        returns[EgoItemParseRecord record]
        @init {
            String nameInit = "";
            String costInit = "";
            String ratingInit = "";
            String commonInit = "";
            String lowerInit = "";
            String upperInit = "";
            String tohInit = "";
            String todInit = "";
            String toaInit = "";
            String minTohInit = "";
            String minTodInit = "";
            String minToaInit = "";
            List<String> tValsInit = new ArrayList<>();
            List<EgoItemParseRecord.ItemRef> itemRefsInit = new ArrayList<>();
            List<String> flagsInit = new ArrayList<>();
            List<String> flagsOffInit = new ArrayList<>();
            Map<String, String> valuesInit = new HashMap<>();
            Map<String, String> minValuesInit = new HashMap<>();
            String actInit = "";
            String timeoutInit = "";
            List<String> brandsInit = new ArrayList<>();
            List<String> slaysInit = new ArrayList<>();
            Map<String, String> cursesInit = new HashMap<>();
            String descInit = "";
            StringBuilder sb = new StringBuilder();
            int line = 0;
        }
        @after {
            descInit = sb.toString();
            $record = new EgoItemParseRecord(nameInit,
            costInit, ratingInit, commonInit, lowerInit, upperInit,
            tohInit, todInit, toaInit, minTohInit, minTodInit, minToaInit,
            tValsInit, itemRefsInit, flagsInit, flagsOffInit,
            valuesInit, minValuesInit, actInit, timeoutInit,
            brandsInit, slaysInit, cursesInit, descInit, line);
        }
        :   name { line = $name.lineNo;
                   nameInit = $name.nameStr; }
        (   info { costInit = $info.cost;
                   ratingInit = $info.rating; }
        |   alloc { commonInit = $alloc.common;
                    lowerInit = $alloc.lower;
                    upperInit = $alloc.upper; }
        |   combat { tohInit = $combat.toh;
                     todInit = $combat.tod;
                     toaInit = $combat.toa; }
        |   minCombat { minTohInit = $minCombat.minToh;
                        minTodInit = $minCombat.minTod;
                        minToaInit = $minCombat.minToa; }
        |   type { tValsInit.add($type.tVal); }
        |   item { ItemRef ref = new ItemRef($item.tVal, $item.sVal);
                   itemRefsInit.add(ref); }
        |   flags { flagsInit.addAll($flags.flagList);  }
        |   flagsOff { flagsOffInit.addAll($flagsOff.flagList); }
        |   values { valuesInit.putAll($values.valueMap); }
        |   minValues { minValuesInit.putAll($minValues.valueMap); }
        |   act { actInit = $act.activation; }
        |   time { timeoutInit = $time.timeStr; }
        |   brand { brandsInit.add($brand.brandStr); }
        |   slay { slaysInit.add($slay.slayStr); }
        |   curse { cursesInit.put($curse.curseName, $curse.cursePower); }
        |   desc { sb.append($desc.descStr); })+
        ;

file
        returns[List<EgoItemParseRecord> items, String declaredRecordCount]
        :   recordCount { $declaredRecordCount = $recordCount.count;
                          $items = new ArrayList<>(); }
            ( egoItem { $items.add($egoItem.record); })+ EOF
        ;
