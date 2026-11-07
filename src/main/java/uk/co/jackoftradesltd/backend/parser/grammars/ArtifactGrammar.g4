parser grammar ArtifactGrammar;

options { tokenVocab = ArtifactLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.artifact.ArtifactParseRecord;
    import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

baseObject
        returns[ArtifactParseRecord.ArtifactBaseObjectParseRecord objectBase]
        :   BASE_OBJECT t=STRING_BETWEEN_COLONS
            DELIM_COLON s=STRING_BETWEEN_COLONS
            { String tVal = $t.getText(); 
              String sVal = $s.getText(); 
              $objectBase = new ArtifactParseRecord.ArtifactBaseObjectParseRecord(tVal, sVal, ""); }
        ;

graphics
        returns[String glyph, String colour]
        :   GRAPHICS g=GLYPH_VALUES GLYPH_COLON_SWITCH c=COLOUR_VALUES
            { $glyph = $g.getText(); $colour = $c.getText(); }
        ;

level
        returns[String levelVal]
        :   LEVEL l=ARTIFACT_INTEGER { $levelVal = $l.getText(); }
        ;

weight
        returns[String weightVal]
        :   WEIGHT w=ARTIFACT_INTEGER {$weightVal = $w.getText(); }
        ;

cost
        returns[String costVal]
        :   COST c=ARTIFACT_INTEGER {$costVal = $c.getText(); }
        ;

alloc
        returns[String commonness, String min, String max]
        :   ALLOC c=ALLOC_INT ALLOC_COLON mn=ALLOC_INT ALLOC_TO mx=ALLOC_INT
            { $commonness = $c.getText(); $min = $mn.getText(); $max = $mx.getText(); }
        ;

attack
        returns[String attackBase, String toh, String tod]
        :   ATTACK b=(SIMPLE_DICE_STRING | ARTIFACT_INTEGER)
                     ARTIFACT_COLON h=ARTIFACT_INTEGER
                     ARTIFACT_COLON d=ARTIFACT_INTEGER
            { $attackBase = $b.getText();
              $toh        = $h.getText();
              $tod        = $d.getText(); }
        ;

armour
        returns[String baseAC, String toa]
        :   ARMOUR b=ARTIFACT_INTEGER ARTIFACT_COLON a=ARTIFACT_INTEGER
            { $baseAC = $b.getText();
              $toa    = $a.getText(); }
        ;

flags
        returns[List<String> flagList]
        @init {
            $flagList = new ArrayList<>();
        }
        :   FLAGS f1=FLAG {
                $flagList.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $flagList.add($f2.getText());
            })* FLAG_OR?
        ;

act
        returns[String activation]
        :   ACT a=STRING { $activation = $a.getText(); }
        ;

time
        returns[String timeStr]
        :   TIME t=(SIMPLE_DICE_STRING | ARTIFACT_INTEGER) { $timeStr = $t.getText(); }
        ;

msg
        returns[String msgLine]
        :   MSG m=STRING { $msgLine = $m.getText(); }
        ;

values
        returns[Map<String, String> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   VALUES k1=VALUES_STRING VALUES_LBRACKET v1=VALUES_INTEGER VALUES_RBRACKET {
                $valueMap.put($k1.getText(), $v1.getText());
            }
            (VALUES_OR k2=VALUES_STRING VALUES_LBRACKET v2=VALUES_INTEGER VALUES_RBRACKET
            {
                $valueMap.put($k2.getText(), $v2.getText());
            })* VALUES_OR?
        ;

brand
        returns[String brandLine]
        :   BRAND STRING { $brandLine = $STRING.getText(); }
        ;

slay
        returns[String slayLine]
        :   SLAY STRING { $slayLine = $STRING.getText(); }
        ;

curse
        returns[FullCurseDataParseRecord curseData]
        :   CURSE k=STRING_BETWEEN_COLONS DELIM_COLON v=DELIM_INTEGER {
            String curseName = $k.getText();
            String cursePower = $v.getText();
            $curseData = new FullCurseDataParseRecord(curseName, cursePower, "0"); }
        ;

desc
        returns[String descLine]
        :   DESC d=STRING { $descLine = $d.getText(); }
        ;

artifact
        returns[ArtifactParseRecord artifactRecord]
        @init {
            String nameInit = "";
            String glyphInit = "";
            String colourInit = "";
            List<ArtifactParseRecord.ArtifactBaseObjectParseRecord> objectBaseInit = new ArrayList<>();
            String levelInit = "";
            String weightInit = "";
            String costInit = "";
            String commonnessInit = "";
            String minInit = "";
            String maxInit = "";
            String baseDamageInit = "";
            String tohInit = "";
            String todInit = "";
            String baseACInit = "";
            String toaInit = "";
            List<String> flagListInit = new ArrayList<>();
            String activationInit = "";
            String timeInit = "";
            String msgInit = "";
            Map<String, String> valuesMapInit = new HashMap<>();
            List<String> brandInit = new ArrayList<>();
            List<String> slayInit = new ArrayList<>();
            List<FullCurseDataParseRecord> curseInit = new ArrayList<>();
            String descInit = null;
            int line = 0;
            StringBuilder msgSB = new StringBuilder();
            StringBuilder descSB = new StringBuilder();
        }
        @after {
            msgInit = msgSB.toString();
            descInit = descSB.isEmpty() ? null : descSB.toString();
            $artifactRecord = new ArtifactParseRecord(nameInit, objectBaseInit,
                glyphInit, colourInit, levelInit, weightInit, costInit, commonnessInit,
                minInit, maxInit, baseDamageInit, tohInit, todInit, baseACInit, toaInit,
                flagListInit, activationInit, timeInit, msgInit, valuesMapInit,
                brandInit, slayInit, curseInit, descInit, line);
        }
        :   name { line = $name.line; nameInit = $name.nameStr; }
        (   baseObject { if ($baseObject.objectBase != null) {
                            String tValueInit = $baseObject.objectBase.tValue(); 
                            String sValueInit = $baseObject.objectBase.sValue();
                            objectBaseInit.add(new ArtifactParseRecord.ArtifactBaseObjectParseRecord(tValueInit, sValueInit, levelInit)); }}
        |   graphics { glyphInit = $graphics.glyph; colourInit = $graphics.colour; }
        |   level { levelInit = $level.levelVal; }
        |   weight { weightInit = $weight.weightVal; }
        |   cost { costInit = $cost.costVal; }
        |   alloc { commonnessInit = $alloc.commonness;
                    minInit = $alloc.min;
                    maxInit = $alloc.max; }
        |   attack { baseDamageInit = $attack.attackBase;
                     tohInit = $attack.toh;
                     todInit = $attack.tod; }
        |   armour { baseACInit = $armour.baseAC;
                     toaInit = $armour.toa; }
        |   flags { flagListInit.addAll($flags.flagList); }
        |   act { activationInit = $act.activation; }
        |   time { timeInit = $time.timeStr; }
        |   msg { msgSB.append($msg.msgLine); }
        |   values { valuesMapInit.putAll($values.valueMap); }
        |   brand { brandInit.add($brand.brandLine); }
        |   slay { slayInit.add($slay.slayLine); }
        |   curse { curseInit.add($curse.curseData); }
        |   desc { descSB.append($desc.descLine); }
        )+;

file
        returns[List<ArtifactParseRecord> records]
        @init {
            $records = new ArrayList<>();
        }
        :   (artifact { $records.add($artifact.artifactRecord); })+ EOF
        ;
