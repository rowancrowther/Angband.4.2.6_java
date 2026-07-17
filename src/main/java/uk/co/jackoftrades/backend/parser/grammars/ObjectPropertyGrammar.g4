parser grammar ObjectPropertyGrammar;

options { tokenVocab = ObjectPropertyLexer; }

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameString, int lineNo]
        :   NAME LCASE { $nameString = $LCASE.getText(); $lineNo = $start.getLine(); }
        ;

type
        returns[String typeStr]
        :   TYPE LCASE { $typeStr = $LCASE.getText(); }
        ;

subtype
        returns[String subTypeStr]
        :   SUBTYPE LCASE { $subTypeStr = $LCASE.getText();}
        ;

idType
        returns[String idTypeStr]
        :   ID_TYPE STRING { $itTypeStr = $STRING.getText(); }
        ;

codeVal
        returns[String codeStr]
        :   CODE UCASE { $codeStr = $UCASE.getString(); }
        ;

power
        returns[String powerVal]
        :   POWER INTEGER { $powerVal = $INTEGER.getText(); }
        ;

mult
        returns[String multVal]
        :   MULT INTEGER { $multVal = $INTEGER.getString(); }
        ;

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

negAdjecive
        returns[String negAdj]
        :   NEG_ADJECTIVE LCASE { $negAdj = $LCASE.getText(); }
        ;

msg
        returns[String msgLine]
        :   MSG STRING { $msgLine = $STRING.getText(); }
        ;

bindui
        returns[String binding, String tag, String value]
        @init {
            $tag = "";
        }
        :   BINDUI b=LCASE { $binding = $b.getText(); }
            (LTHAN t=UCASE GTHAN { $tag = $t.getText(); })?
             COLON v=INTEGER {$value = $v.getText(); }
        ;

desc
        returns[String line]
        :   DESC STRING { $line = $STRING.getText(); }
        ;

objectPproperty
        returns[ObjectPropertyParseRecord property]
        :   name
            type
        (   subtype
        |   idType
        |   codeVal
        |   power
        |   mult
        |   typeMult
        |   adjective
        |   negAdjecive
        |   msg
        |   bindui
        |   desc)+
        ;

file
        returns[String declaredRecordCount, List<ObjectPropertyParseRecord> properties]
        @init {
            properties = new ArrayList<>();
        }
        :   recordCount {$declaredRecordCount = $recordCount.count; }
            (objectPproperty {$properties.add($objectProperty.property); })+ EOF
        ;
