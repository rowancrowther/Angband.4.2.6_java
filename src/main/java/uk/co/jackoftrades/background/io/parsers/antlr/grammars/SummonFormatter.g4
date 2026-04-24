grammar SummonFormatter;

@header     {   import uk.co.jackoftrades.middle.monsters.Summon;
                import uk.co.jackoftrades.middle.monsters.MonsterBase;
                import uk.co.jackoftrades.middle.monsters.MonsterBases;

                import java.util.ArrayList;
            }

word        returns[String wrd] : WORD
            { $wrd = $WORD.toString();
            }
            ;

tagText     returns[String txt]
            @init {
                StringBuilder sb = new StringBuilder();
            }
            : word1=WORD { sb.append($word1.getText());
            }
            (SPACE word2=WORD
            { sb.append(" ").append($word2.getText());
            })?
            { $txt = sb.toString(); }
            ;

one_or_zero returns[boolean unique]: BOOLEAN
            { $unique = (Integer.parseInt($BOOLEAN.getText()) == 1);
            }
            ;

summon      returns[Summon smn]
            @init {
                String nameTag = "";
                String msgTag = "";
                boolean unique = false;
                MonsterBases mb = new MonsterBases();
                String raceFlag = "";
                String fallBack = "";
                String desc = "";
            }
            @after {
                $smn = new Summon(nameTag, msgTag, unique, mb, raceFlag, fallBack, desc);
            }
            : NAMETAG word { nameTag = $word.wrd; } EOL
              MSGTTAG word { msgTag = $word.wrd; } EOL
              UNIQUESTAG one_or_zero { unique = $one_or_zero.unique; } EOL
              (BASETAG tagText {
                    String baseTag = $tagText.txt;
                    MonsterBase base = new MonsterBase(baseTag);
                    mb.put(base);
              } EOL)*
              (RACEFLAG word { raceFlag = $word.wrd; } EOL)?
              (FALLBACKTAG word { fallBack = $word.wrd; } EOL)?
              DESCTAG tagText { desc = $tagText.txt; }
            ;

file        returns[ArrayList<Summon> summons]
            @init {
                $summons = new ArrayList<>();
            }
            : summ1=summon { $summons.add($summ1.smn); } (EOL+ summ2=summon { $summons.add($summ2.smn); })* EOL* EOF
            ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip;

WORD    :   ('A'..'Z' | 'a'..'z' | '_')+ ;

SPACE   :   ' '     ;

EOL     :   '\r'?'\n'    ;

NAMETAG :   'n' 'a' 'm' 'e' ':' ;

MSGTTAG :   'm' 's' 'g' 't' ':' ;

UNIQUESTAG : 'u' 'n' 'i' 'q' 'u' 'e' 's' ':' ;

BASETAG :   'b' 'a' 's' 'e' ':' ;

RACEFLAG : 'r' 'a' 'c' 'e' '-' 'f' 'l' 'a' 'g' ':' ;

FALLBACKTAG : 'f' 'a' 'l' 'l' 'b' 'a' 'c' 'k' ':' ;

DESCTAG : 'd' 'e' 's' 'c' ':' ;

BOOLEAN : ('1' | '0') ;