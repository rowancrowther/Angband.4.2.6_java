// Parser for lib/gamedata/object_property.txt - the catalogue of every generic
// object property (stat, modifier, flag, and elemental ignore/resist/vuln/immunity),
// each with a power/value used in item valuation, optional per-item-type multipliers,
// the adjectives/messages/description used to present it, and an optional binding to
// a UI element (ui_entry.txt).
//
// Cf. src/obj-init.c: init_parse_object_property() registers the directive table
// (name/code/type/subtype/id-type/power/mult/type-mult/adjective/neg-adjective/msg/
// desc/bindui -> parse_object_property_*), and struct obj_property lives in
// src/obj-properties.h.
//
// This grammar does EXTRACTION ONLY, and it keeps the file's order. Every directive line
// becomes one Directive (the directive's name, its values as raw text, and its line), and
// a record is the list of those Directives in the order they were read. Nothing is
// resolved or parsed here: the assembler turns the text into enums and integers, builds the
// bindui binding (the UI entry itself is matched later, by GameConstants), and applies
// C's rules line by line.
//
// Why a list. C's handlers read the record as it stands when each line arrives. code: and
// bindui: look at the type held at that moment and fail with
// PARSE_ERROR_MISSING_OBJ_PROP_TYPE while it is still 0, code: is resolved against the
// table that type selects at that line, and a later type: only overwrites the type. A
// record that kept one final value per field lost that order. A list keeps it, and keeps
// a repeated line whole, so the assembler can do what C does with each directive: a
// repeated msg: or desc: replaces the earlier text, and every bindui: binds.
//
// Record shape: name: first, then any directive in any order, any number of times, or
// none at all. type: is optional, as in C, where a record with no type: keeps
// OBJ_PROPERTY_NONE, so an absent directive is simply not in the list. A directive before
// the first name: is a syntax error here, as it is an error in C
// (PARSE_ERROR_MISSING_RECORD_HEADER). A file may hold no records, as C loads nothing
// from one.
//
// Values of each Directive (raw text; nothing is parsed here):
//   name, type, subtype, id-type, code, power, mult, adjective, neg-adjective, msg, desc:
//       one value, the text after the colon.
//   type-mult:
//       two values, the tval name as written (it may contain a space) and the multiplier.
//   bindui:
//       four values, always: the entry name, the <TAG> or "" if there is none, the aux
//       figure, and the explicit value or null if there is none.
//
// Verified against the real data (79 records) on 261115: type: precedes code: in every record
// (C requires it, since code resolves against a table chosen by type); subtype: and
// id-type: appear only on flags (38 records); msg:/desc:/bindui: appear at most once per
// record (21, 22 and 66 records). No directive line has a trailing blank.
//
// Trailing blanks. C's str fields keep the rest of the line, blanks included, and its int fields
// are read with strtol, which ignores trailing text. The lexer follows that: a blank after the
// value of type: or code: is refused; one after name:, subtype:, id-type:, adjective:,
// neg-adjective:, msg: or desc: is part of the value (and the assembler then refuses a subtype: or
// id-type: that no longer matches its table); one after the figure of power:, mult:, type-mult: or
// bindui: is ignored, including on a last line with no final newline.
parser grammar ObjectProperty2Grammar;

options { tokenVocab = ObjectProperty2Lexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectProperty2ParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.LinkedHashMap;
}

// One rule per directive. Each action builds the Directive for its line: the directive's
// name as C spells it, its values as raw text, and the line it started on
// ($start.getLine()). The directive rules do no checking, so a bad value is the
// assembler's to refuse.
//
// "name:<text>" - opens a record. The one value is the rest of the line as written (STRING, from the
// free-text mode). C's handler is parse_object_property_name, which also starts a new struct; here
// the objectProperty rule starts the record.
//
// Rule name coded on 261114, commented in full on 261114.
name
        returns[Directive nameDirective]
        :   NAME STRING {
                String nameString = $STRING.getText();
                int lineNo = $start.getLine(); 
                List<String> values = new ArrayList<>();
                values.add(nameString);
                $nameDirective = new Directive("name", values, lineNo);
            }
        ;

// "type:<word>" - the property's category (stat, mod, flag, ignore, resistance, vulnerability or
// immunity). The one value is a single lower-case word (LCASE, read in the lexer's LCASE_MODE);
// whether it is one of the seven is the assembler's to decide. A blank or tab after the word is a
// lexer error, so the file is refused, as C refuses "flag " (its field is a str, which keeps the blank).
//
// Rule type coded on 261114, commented in full on 261114 (updated 261115 for the trailing blank).
type
        returns[Directive typeDirective]
        :   TYPE LCASE { 
                String typeStr = $LCASE.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(typeStr);
                $typeDirective = new Directive("type", values, lineNo);
            }
        ;

// "subtype:<text>" - the grouping of a flag (e.g. "sustain", "misc ability", "curse-only"). The
// one value is the rest of the line (STRING), because it may contain a space or a hyphen.
//
// Rule subtype coded on 261114, commented in full on 261114.
subtype
        returns[Directive subTypeDirective]
        :   SUBTYPE STRING { 
                String subTypeStr = $STRING.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(subTypeStr);
                $subTypeDirective = new Directive("subtype", values, lineNo);
            }
        ;

// "id-type:<text>" - how a property is identified (on effect, timed or on wield; the file says only
// flags use it). The one value is the rest of the line (STRING), because it contains spaces. The
// Directive is named "id-type" as C spells it.
//
// Rule idType coded on 261114, commented in full on 261114.
idType
        returns[Directive idTypeDirective]
        :   ID_TYPE STRING { 
                String idTypeStr = $STRING.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(idTypeStr);
                $idTypeDirective = new Directive("id-type", values, lineNo);
            }
        ;

// "code:<NAME>" - the stat, modifier, flag or element the property stands for. The one value is an
// upper-case name (UCASE, read in the lexer's UCASE_MODE, so digits are allowed, as in DIG_1). The rule
// is called codeVal because code is not available as a rule name; the Directive is named "code".
// Which table the name is looked up in depends on the type at that line, so the assembler resolves it.
// A blank or tab after the name is a lexer error, so the file is refused, as C refuses "FREE_ACT ".
//
// Rule codeVal coded on 261114, commented in full on 261114 (updated 261115 for the trailing blank).
codeVal
        returns[Directive codeDirective]
        :   CODE UCASE {
                String codeStr = $UCASE.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(codeStr);
                $codeDirective = new Directive("code", values, lineNo);
            }
        ;

// "power:<n>" - the value given to the property in object power calculations. The one value is the
// INTEGER text exactly as written (sign, 0x prefix and all); the assembler reads it.
//
// Rule power coded on 261114, commented in full on 261114.
power
        returns[Directive powerDirective]
        :   POWER INTEGER {
                String powerVal = $INTEGER.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(powerVal);
                $powerDirective = new Directive("power", values, lineNo); 
            }
        ;

// "mult:<n>" - the property's relative value in power calculations, as opposed to the per-tval
// figures of type-mult:. The one value is the INTEGER text exactly as written; the assembler reads it.
//
// Rule mult coded on 261114, commented in full on 261114.
mult
        returns[Directive multDirective]
        :   MULT INTEGER {
                String multVal = $INTEGER.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(multVal);
                $multDirective = new Directive("mult", values, lineNo); 
            }
        ;

// type-mult:<tval>:<mult> - the tval symbol (which may contain a space, e.g.
// "dragon armor", or a bare number) and the multiplier arrive as two separate tokens: DELIM_STRING,
// read in the lexer's DELIMITER_INTRO mode, and DELIM_INTEGER, read in DELIMITER_POWER. The
// Directive holds the two as its values, in that order, as raw text.
//
// Rule typeMult coded on 261114, commented in full on 261114 (the token names corrected 261115).
typeMult
        returns[Directive typeMultDirective]
        :   TYPE_MULT t=DELIM_STRING v=DELIM_INTEGER {
                String typeTval = $t.getText();
                String multVal  = $v.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(typeTval);
                values.add(multVal);
                $typeMultDirective = new Directive("type-mult", values, lineNo); 
            }
        ;

// "adjective:<word>" - an adjective describing the property as a player attribute (the file says
// only stats use it). The one value is the rest of the line as written (STRING), because C's
// field is a str: capitals, blanks and hyphens are all kept, and a trailing blank is part of the value.
//
// Rule adjective coded on 261114, commented in full on 261114.
adjective
        returns[Directive adjDirective]
        :   ADJECTIVE STRING {
                String adj = $STRING.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(adj);
                $adjDirective = new Directive("adjective", values, lineNo); 
            }
        ;

// "neg-adjective:<word>" - an adjective describing the opposite of the property as a player
// attribute. The one value is the rest of the line as written (STRING), as for adjective:. The
// Directive is named "neg-adjective" as C spells it.
//
// Rule negAdjective coded on 261114, commented in full on 261114.
negAdjective
        returns[Directive negAdjDirective]
        :   NEG_ADJECTIVE STRING {
                String negAdj = $STRING.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(negAdj);
                $negAdjDirective = new Directive("neg-adjective", values, lineNo);
            }
        ;

// "msg:<text>" - the message printed on noticing the property. The one value is the rest of the
// line (STRING). A repeated msg: is a separate Directive; the assembler keeps the last, as C does.
//
// Rule msg coded on 261114, commented in full on 261114.
msg
        returns[Directive msgLineDirective]
        :   MSG STRING { 
                String msgLine = $STRING.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(msgLine);
                $msgLineDirective = new Directive("msg", values, lineNo);
            }
        ;

// bindui:<name>[<TAG>]:<aux>[:<uival>] - binds the property to a UI entry.
//   binding = the entry base name; tag = the optional <TAG> (part of the entry's
//   full name, not a value); aux = mandatory second param (auxiliary flag);
//   uiVal = optional third param (explicit value to present).
// The Directive always holds four values, in that order. tag defaults to "" and uiVal to
// null (not ""), so the assembler can tell "no explicit value, use the natural one" from
// a real 0 - C's parser_hasval distinction. Each is assigned inside its own optional
// group, so the action never reads a label that did not match.
//
// Rule bindui coded on 261114, commented in full on 261114.
bindui
        returns[Directive binduiDirective]
        @init {
            String binding = "";
            String tag = "";
            String aux = "";
            String uiVal = null;
        }
        :   BINDUI b=BINDUI_STRING
            (t=BINDUI_TAG { tag = $t.getText(); })?
             a=BINDUI_INTEGER
            (u=BINDUI_INTEGER { uiVal = $u.getText(); })?
            {
                binding = $b.getText();
                aux = $a.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(binding);
                values.add(tag);
                values.add(aux);
                values.add(uiVal);
                $binduiDirective = new Directive("bindui", values, lineNo);
            }
        ;

// "desc:<text>" - the description shown on the object information screens. The one value is the
// rest of the line (STRING). A repeated desc: is a separate Directive; the assembler keeps the
// last, as C does.
//
// Rule desc coded on 261114, commented in full on 261114.
desc
        returns[Directive lineDirective]
        :   DESC STRING {
                String line = $STRING.getText();
                int lineNo = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(line);
                $lineDirective = new Directive("desc", values, lineNo); 
            }
        ;

// One record: name first, then any mix of the other directives, type included, in any
// order and any number of times. Each directive's Directive is added to the local list as
// its line is read, so the list is in file order, and @after wraps it in the raw record.
// Nothing is merged or dropped here: a repeated line is a separate entry, and a record
// with no type: simply has no type entry.
//
// Rule objectProperty coded on 261114, commented in full on 261114.
objectProperty
        returns[ObjectProperty2ParseRecord property]
        @init {
            List<Directive> directives = new ArrayList<>();
        }
        @after {
            $property = new ObjectProperty2ParseRecord(directives);
        }
        :   name { directives.add($name.nameDirective); }
        (   type { directives.add($type.typeDirective); }
        |   subtype { directives.add($subtype.subTypeDirective); }
        |   idType { directives.add($idType.idTypeDirective); }
        |   codeVal { directives.add($codeVal.codeDirective); }
        |   power { directives.add($power.powerDirective); }
        |   mult { directives.add($mult.multDirective); }
        |   typeMult { directives.add($typeMult.typeMultDirective); }
        |   adjective { directives.add($adjective.adjDirective); }
        |   negAdjective { directives.add($negAdjective.negAdjDirective); }
        |   msg { directives.add($msg.msgLineDirective); }
        |   bindui { directives.add($bindui.binduiDirective); }
        |   desc { directives.add($desc.lineDirective); })*
        ;

// Whole file: zero or more records, as in C, which loads nothing from an empty file. The
// records come back in file order.
//
// Rule file coded on 261114, commented in full on 261114.
file
        returns[List<ObjectProperty2ParseRecord> properties]
        @init {
            $properties = new ArrayList<>();
        }
        :   (objectProperty {$properties.add($objectProperty.property); })* EOF
        ;
