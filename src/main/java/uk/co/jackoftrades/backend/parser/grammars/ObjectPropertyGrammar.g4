// Reader for lib/gamedata/object_property.txt - the catalogue of every generic
// object property (stat, modifier, flag, and elemental ignore/resist/vuln/immunity),
// each with a power/value used in item valuation, optional per-item-type multipliers,
// the adjectives/messages/description used to present it, and an optional binding to
// a UI element (ui_entry.txt).
//
// Cf. src/obj-init.c: init_parse_object_property() registers the directive table
// (name/type/subtype/id-type/code/power/mult/type-mult/adjective/neg-adjective/msg/
// desc/bindui -> parse_object_property_*), and struct obj_property lives in
// src/obj-properties.h. This grammar does EXTRACTION ONLY: every action stashes raw
// text into an ObjectPropertyParseRecord; all resolution to enums/ints and the UI
// lookup happen in ObjectPropertyAssembler.
//
// Verified against the real data (79 records): type: precedes code: in every record
// (C requires it, since code resolves against a table chosen by type); subtype: and
// id-type: appear only on flags; msg:/desc:/bindui: appear at most once per record.
//
// NOTE: msg:/desc: are accumulated below via StringBuilder.append, whereas C REPLACES
// (string_free + string_make). Harmless as long as neither repeats within a record
// (verified true), but it is a divergence from C's semantics.
parser grammar ObjectPropertyGrammar;

options { tokenVocab = ObjectPropertyLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.objectproperty.ObjectPropertyParseRecord;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameString, int lineNo]
        :   NAME STRING { $nameString = $STRING.getText(); $lineNo = $start.getLine(); }
        ;

type
        returns[String typeStr]
        :   TYPE LCASE { $typeStr = $LCASE.getText(); }
        ;

subtype
        returns[String subTypeStr]
        :   SUBTYPE STRING { $subTypeStr = $STRING.getText();}
        ;

idType
        returns[String idTypeStr]
        :   ID_TYPE STRING { $idTypeStr = $STRING.getText(); }
        ;

codeVal
        returns[String codeStr]
        :   CODE UCASE { $codeStr = $UCASE.getText(); }
        ;

power
        returns[String powerVal]
        :   POWER INTEGER { $powerVal = $INTEGER.getText(); }
        ;

mult
        returns[String multVal]
        :   MULT INTEGER { $multVal = $INTEGER.getText(); }
        ;

// type-mult:<tval>:<mult> - the tval symbol (which may contain a space, e.g.
// "dragon armor") and the multiplier arrive as separate DELIMITER-mode tokens.
typeMult
        returns[String typeTval, String multVal]
        :   TYPE_MULT t=STRING_BETWEEN_COLONS DELIM_COLON v=DELIM_INTEGER {
                $typeTval = $t.getText();
                $multVal  = $v.getText(); }
        ;

adjective
        returns[String adj]
        :   ADJECTIVE LCASE { $adj = $LCASE.getText(); }
        ;

negAdjective
        returns[String negAdj]
        :   NEG_ADJECTIVE LCASE { $negAdj = $LCASE.getText(); }
        ;

msg
        returns[String msgLine]
        :   MSG STRING { $msgLine = $STRING.getText(); }
        ;

// bindui:<name>[<TAG>]:<aux>[:<uival>] - binds the property to a UI entry.
//   binding = the entry base name; tag = the optional <TAG> (part of the entry's
//   full name, not a value); aux = mandatory second param (auxiliary flag);
//   uiVal = optional third param (explicit value to present).
// $uiVal defaults to null (not "") so the assembler can distinguish "no explicit
// value, use the natural one" from a real 0 - C's parser_hasval distinction.
bindui
        returns[String binding, String tag, String uiVal, String aux]
        @init {
            $tag = "";
            $uiVal = null;
        }
        :   BINDUI b=LCASE { $binding = $b.getText(); }
            (LTHAN t=UCASE GTHAN { $tag = $t.getText(); })?
             COLON a=INTEGER {$aux = $a.getText(); }
            (COLON u=INTEGER {$uiVal = $u.getText(); })?
        ;

desc
        returns[String line]
        :   DESC STRING { $line = $STRING.getText(); }
        ;

// One record: name and type first (in that order), then any mix of the remaining
// directives. Locals accumulate each field; @after packs them into the raw record.
// msg/desc use StringBuilders (append, not replace - see the file-level NOTE).
objectProperty
        returns[ObjectPropertyParseRecord property]
        @init {
            String nameInit = "";
            String typeInit = "";
            String subTypeInit = "";
            String idTypeInit = "";
            String codeInit = "";
            String powerInit = "";
            String multInit = "";
            Map<String, String> typeMultInit = new HashMap<>();
            String adjectiveInit = "";
            String negAdjectiveInit = "";
            String msgInit = "";
            String binduiInit = "";
            String tagInit = "";
            String valueInit = null;
            String auxInit = "";
            String descInit = "";
            int line = 0;
            StringBuilder msgSB = new StringBuilder();
            StringBuilder descSB = new StringBuilder();
        }
        @after {
            descInit = descSB.toString();
            msgInit = msgSB.toString();
            $property = new ObjectPropertyParseRecord(nameInit, typeInit,
                    subTypeInit, idTypeInit, codeInit, powerInit, multInit,
                    typeMultInit, adjectiveInit, negAdjectiveInit,
                    msgInit, binduiInit, tagInit, valueInit, auxInit,
                    descInit, line);
        }
        :   name { line = $name.lineNo; nameInit = $name.nameString; }
            type { typeInit = $type.typeStr; }
        (   subtype { subTypeInit = $subtype.subTypeStr; }
        |   idType { idTypeInit = $idType.idTypeStr; }
        |   codeVal { codeInit = $codeVal.codeStr; }
        |   power { powerInit = $power.powerVal; }
        |   mult { multInit = $mult.multVal; }
        |   typeMult { typeMultInit.put($typeMult.typeTval, $typeMult.multVal); }
        |   adjective { adjectiveInit = $adjective.adj; }
        |   negAdjective { negAdjectiveInit = $negAdjective.negAdj; }
        |   msg { msgSB.append($msg.msgLine); }
        |   bindui { binduiInit = $bindui.binding;
                     tagInit = $bindui.tag;
                     valueInit = $bindui.uiVal;
                     auxInit = $bindui.aux; }
        |   desc { descSB.append($desc.line); })+
        ;

// Whole file: the mandatory record-count header (validated softly by the reader
// against the actual count) followed by one or more records.
file
        returns[String declaredRecordCount, List<ObjectPropertyParseRecord> properties]
        @init {
            $properties = new ArrayList<>();
        }
        :   recordCount {$declaredRecordCount = $recordCount.count; }
            (objectProperty {$properties.add($objectProperty.property); })+ EOF
        ;
