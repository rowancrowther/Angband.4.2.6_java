// Parser for lib/gamedata/artifact.txt (tokens from ArtifactLexer). Each rule collects one
// directive's value(s) as raw text; the artifact rule gathers them into an ArtifactParseRecord. No
// lookup, conversion or validation happens here - that is ArtifactAssembler's job. See
// ArtifactParseRecord for the field-by-field meaning of what these rules capture.
//
// The C original is init_parse_artifact in src/obj-init.c. It keeps a "current artifact" in the
// parser's private data, which name: starts and every other directive writes to. Here the current
// artifact is the artifact rule's locals: name: first, then any directive in any order, as in C.
// A directive that is repeated follows C too. Most replace the earlier one; flags:, brand:, slay:,
// curse: and base-object: add to a list, msg: and desc: append their text, and values: adds its
// labels to one map.
parser grammar ArtifactGrammar;

options { tokenVocab = ArtifactLexer; }

// The record types the actions build, and the collections they fill.
@header {
    import uk.co.jackoftradesltd.backend.parser.artifact.ArtifactParseRecord;
    import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

// "name:<text>" - opens a record. The source line is captured too so the assembler can attribute
// any error to the right line in the file.
//
// Rule name coded before 261112, commented in full on 261112.
name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

// "base-object:<tval>:<sval>" - the type and sub-type of the item the artifact is built on, kept
// as the two names. Both are STRING_BETWEEN_COLONS, so a name the lexer calls a DELIM_INTEGER,
// one made of nothing but digits or the hex letters a to f, is a syntax error here. C reads a
// leading decimal number as an index (tval_find_idx and lookup_sval both use strtoul in base
// 10), and reads a name of hex letters as an ordinary name. The level is left "" in this rule:
// the artifact rule, which knows the level read so far, makes the finished record.
//
// Rule baseObject coded before 261112, commented in full on 261112.
baseObject
        returns[ArtifactParseRecord.ArtifactBaseObjectParseRecord objectBase]
        :   BASE_OBJECT t=STRING_BETWEEN_COLONS
            DELIM_COLON s=STRING_BETWEEN_COLONS
            { String tVal = $t.getText(); 
              String sVal = $s.getText(); 
              $objectBase = new ArtifactParseRecord.ArtifactBaseObjectParseRecord(tVal, sVal, ""); }
        ;

// "graphics:<glyph>:<colour>" - the glyph and colour of a special artifact's kind. The tokens
// come from the imported AngbandDisplayCharacter: GLYPH_VALUES is the one glyph character and
// COLOUR_VALUES the rest of the line, a one-letter colour code or a colour name. Both parts are
// required here, so a glyph with no colour is a syntax error.
//
// Rule graphics coded before 261112, commented in full on 261112.
graphics
        returns[String glyph, String colour]
        :   GRAPHICS g=GLYPH_VALUES GLYPH_COLON_SWITCH c=COLOUR_VALUES
            { $glyph = $g.getText(); $colour = $c.getText(); }
        ;

// "level:<n>" - the difficulty level for activation, kept as the ARTIFACT_INTEGER text.
//
// Rule level coded before 261112, commented in full on 261112.
level
        returns[String levelVal]
        :   LEVEL l=ARTIFACT_INTEGER { $levelVal = $l.getText(); }
        ;

// "weight:<n>" - the weight in tenths of a pound, kept as the ARTIFACT_INTEGER text.
//
// Rule weight coded before 261112, commented in full on 261112.
weight
        returns[String weightVal]
        :   WEIGHT w=ARTIFACT_INTEGER {$weightVal = $w.getText(); }
        ;

// "cost:<n>" - the artifact's value in gold, kept as the ARTIFACT_INTEGER text.
//
// Rule cost coded before 261112, commented in full on 261112.
cost
        returns[String costVal]
        :   COST c=ARTIFACT_INTEGER {$costVal = $c.getText(); }
        ;

// "alloc:<commonness>:<min> to <max>" - the chance of generation and the depth range, each kept
// as text. The assembler reads the commonness as an int and checks that each end of the range
// is between 0 and 255.
//
// Rule alloc coded before 261112, commented in full on 261112.
alloc
        returns[String commonness, String min, String max]
        :   ALLOC c=ALLOC_COMMONNESS_INT ALLOC_COLON mn=ALLOC_INT ALLOC_TO mx=ALLOC_INT
            { $commonness = $c.getText(); $min = $mn.getText(); $max = $mx.getText(); }
        ;

// "attack:<dice>:<to-hit>:<to-dam>" - the base damage dice string, kept verbatim, and the two
// bonuses, kept as text. The dice string is the simple form only, with no '$' variables.
//
// Rule attack coded before 261112, commented in full on 261112.
attack
        returns[String attackBase, String toh, String tod]
        :   ATTACK b=BASE_DICE_STRING
                     ARTIFACT_COLON h=ARTIFACT_INTEGER
                     ARTIFACT_COLON d=ARTIFACT_INTEGER
            { $attackBase = $b.getText();
              $toh        = $h.getText();
              $tod        = $d.getText(); }
        ;

// "armor:<ac>:<to-ac>" - the base armour class and the armour bonus, kept as text. The keyword
// is spelled "armor" in the data file; the rule is spelled the British way.
//
// Rule armour coded before 261112, commented in full on 261112.
armour
        returns[String baseAC, String toa]
        :   ARMOUR b=ARTIFACT_INTEGER ARTIFACT_COLON a=ARTIFACT_INTEGER
            { $baseAC = $b.getText();
              $toa    = $a.getText(); }
        ;

// "flags:<flag> | <flag> ..." - the flag names in file order, with a bar between each pair and
// one allowed after the last. The list needs a first flag, so a bare "flags:" is a syntax error
// here, where C registers the value as optional and takes a bare line as no flags. The names are
// not told apart here: the assembler splits the object flags from the IGNORE_ and HATES_
// element flags.
//
// Rule flags coded before 261112, commented in full on 261112.
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

// "act:<name>" - the activation name, as written. The assembler looks it up.
//
// Rule act coded before 261112, commented in full on 261112.
act
        returns[String activation]
        :   ACT a=STRING { $activation = $a.getText(); }
        ;

// "time:<dice>" - the recharge dice string, kept verbatim. It is the simple form only, with no
// '$' variables.
//
// Rule time coded before 261112, commented in full on 261112.
time
        returns[String timeStr]
        :   TIME t=SIMPLE_DICE_TIME_STRING { $timeStr = $t.getText(); }
        ;

// "msg:<text>" - one line of the activation message. The artifact rule appends each line to the
// last, with nothing between them, as C's string_append does.
//
// Rule msg coded before 261112, commented in full on 261112.
msg
        returns[String msgLine]
        :   MSG m=STRING { $msgLine = $m.getText(); }
        ;

// "values:<LABEL>[<n>] | <LABEL>[<n>] ..." - the label and figure pairs, each figure kept as
// text, in a map from label to figure. A bar is allowed after the last pair. A label that appears
// twice keeps the later figure, as C's later write replaces the earlier. The map is a HashMap, so
// it does not keep file order. The assembler tells a RES_ resistance from a modifier.
//
// Rule values coded before 261112, commented in full on 261112.
values
        returns[Map<String, String> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   VALUES k1=VALUES_STRING v1=VALUES_INTEGER {
                $valueMap.put($k1.getText(), $v1.getText());
            }
            (VALUES_OR k2=VALUES_STRING v2=VALUES_INTEGER
            {
                $valueMap.put($k2.getText(), $v2.getText());
            })* VALUES_OR?
        ;

// "brand:<code>" - one brand code, as written. The line may be repeated; the artifact rule adds
// each code to a list, and the assembler looks each up.
//
// Rule brand coded before 261112, commented in full on 261112.
brand
        returns[String brandLine]
        :   BRAND STRING { $brandLine = $STRING.getText(); }
        ;

// "slay:<code>" - one slay code, as written. The line may be repeated; the artifact rule adds
// each code to a list, and the assembler looks each up.
//
// Rule slay coded before 261112, commented in full on 261112.
slay
        returns[String slayLine]
        :   SLAY STRING { $slayLine = $STRING.getText(); }
        ;

// "curse:<name>:<power>" - a curse name and its power, kept as text in a FullCurseDataParseRecord.
// An artifact's curse has no timeout, so the record's timeout is the fixed text "0". The name
// is a STRING_BETWEEN_COLONS, so a name the lexer calls a DELIM_INTEGER is refused here, as in
// baseObject.
//
// Rule curse coded before 261112, commented in full on 261112.
curse
        returns[FullCurseDataParseRecord curseData]
        :   CURSE k=STRING_BETWEEN_COLONS DELIM_COLON v=DELIM_INTEGER {
            String curseName = $k.getText();
            String cursePower = $v.getText();
            $curseData = new FullCurseDataParseRecord(curseName, cursePower, "0"); }
        ;

// "desc:<text>" - one line of the description. The artifact rule appends each line to the last,
// with nothing between them, as C's string_append does.
//
// Rule desc coded before 261112, commented in full on 261112.
desc
        returns[String descLine]
        :   DESC d=STRING { $descLine = $d.getText(); }
        ;

// One record: "name:" followed by one or more directives in any order. Each directive's value is
// kept in a local that starts at the grammar's default - "" for a text or number, empty for a
// list or map - which stands for the zero or NULL that C's zeroed artifact would hold. Two
// defaults differ: the description starts null, not "", as C's text starts NULL, and the message
// ends "" where C's alt_msg is NULL.
//
// The @after block makes the ArtifactParseRecord from the locals. msg: and desc: lines are
// gathered in StringBuilders and joined there. A base-object: line is made into a record here,
// not in baseObject, because only this rule knows the level: text read so far, which the record
// keeps for the assembler, as C copies the artifact's level at the moment it reads base-object:.
// The record's line is the line of its name: directive.
//
// A record with a name: and nothing else is a syntax error, because the loop needs at least one
// directive.
//
// Rule artifact coded before 261112, commented in full on 261112.
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

// The whole file: one or more records, then the end of the file. An empty file is a syntax error.
//
// Rule file coded before 261112, commented in full on 261112.
file
        returns[List<ArtifactParseRecord> records]
        @init {
            $records = new ArrayList<>();
        }
        :   (artifact { $records.add($artifact.artifactRecord); })+ EOF
        ;
