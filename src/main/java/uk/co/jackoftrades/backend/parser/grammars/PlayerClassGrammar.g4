// Parser for class.txt. Each rule captures one directive's fields as raw strings into a `returns`
// attribute; the playerClass rule collects them (via @init/@after) into a PlayerClassParseRecord,
// and the nested magicBlock/bookBlock/spellBlock rules build the caster sub-records. All resolution
// (enum names, integers, cross-file references) is deferred to the assemblers — this grammar only
// captures text. Effect blocks are handled by the imported EffectBlock grammar.
parser grammar PlayerClassGrammar;

options { tokenVocab = PlayerClassLexer; }

import EffectBlock;

@header {
    import uk.co.jackoftrades.backend.parser.playerclass.*;
    import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

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
        returns[String nameStr, int lineNo]
        :   NAME STRING { $nameStr = $STRING.getText(); $lineNo = $start.getLine(); }
        ;

stats
        returns[String strs, String ints, String wiss, String dexs, String cons]
        :   STATS s=INTEGER COLON i=INTEGER COLON w=INTEGER COLON d=INTEGER COLON c=INTEGER {
                $strs = $s.getText();
                $ints = $i.getText();
                $wiss = $w.getText();
                $dexs = $d.getText();
                $cons = $c.getText();
            }
        ;

skillDisarmPhys
        returns[String skillVal, String skillInc]
        :   SKILL_DISARM_PHYS v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillDisarmMagic
        returns[String skillVal, String skillInc]
        :   SKILL_DISARM_MAGIC v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillDevice
        returns[String skillVal, String skillInc]
        :   SKILL_DEVICE v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillSave
        returns[String skillVal, String skillInc]
        :   SKILL_SAVE v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillStealth
        returns[String skillVal, String skillInc]
        :   SKILL_STEALTH v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillSearch
        returns[String skillVal, String skillInc]
        :   SKILL_SEARCH v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillMelee
        returns[String skillVal, String skillInc]
        :   SKILL_MELEE v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillShoot
        returns[String skillVal, String skillInc]
        :   SKILL_SHOOT v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillThrow
        returns[String skillVal, String skillInc]
        :   SKILL_THROW v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

skillDig
        returns[String skillVal, String skillInc]
        :   SKILL_DIG v=INTEGER COLON i=INTEGER{
                $skillVal = $v.getText();
                $skillInc = $i.getText();
           }
        ;

hitdie
        returns[String increment]
        :   HITDIE INTEGER { $increment = $INTEGER.getText(); }
        ;

maxAttacks
        returns[String maxAtt]
        :   MAX_ATTACKS INTEGER { $maxAtt = $INTEGER.getText(); }
        ;

minWeight
        returns[String minWgt]
        :   MIN_WEIGHT INTEGER { $minWgt = $INTEGER.getText(); }
        ;

strengthMultiplier
        returns[String strMult]
        :   STRENGTH INTEGER { $strMult = $INTEGER.getText(); }
        ;

title
        returns[String titleStr]
        :   TITLE STRING { $titleStr = $STRING.getText(); }
        ;

equip
        returns[String tValue, String sValue, String min, String max, String eopts, int line]
        :   EQUIP t=STRING_BETWEEN_COLONS DELIM_COLON s=STRING_BETWEEN_COLONS DELIM_COLON
                mn=DELIM_INTEGER DELIM_COLON mx=DELIM_INTEGER DELIM_COLON e=STRING_BETWEEN_COLONS {
                $tValue = $t.getText();
                $sValue = $s.getText();
                $min    = $mn.getText();
                $max    = $mx.getText();
                $eopts  = $e.getText();
                $line   = $start.getLine();
            }
        ;

objFlag
        returns[List<String> oFlags]
        :   OBJ_FLAGS f1=FLAG {
                $oFlags = new ArrayList<>();
                $oFlags.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $oFlags.add($f2.getText());
            })*
        ;

playerFlags
        returns[List<String> pFlags]
        :   PLAYER_FLAGS f1=FLAG {
                $pFlags = new ArrayList<>();
                $pFlags.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $pFlags.add($f2.getText());
            })*
        ;

exp
        returns[String expStr]
        :   EXP INTEGER { $expStr = $INTEGER.getText(); }
        ;

magic
        returns[String firstSpell, String spellWeight, String noOfBooks, int line]
        :   MAGIC f=INTEGER COLON s=INTEGER COLON n=INTEGER {
                $firstSpell  = $f.getText();
                $spellWeight = $s.getText();
                $noOfBooks   = $n.getText();
                $line        = $start.getLine();
            }
        ;

magicBlock[List<ClassEquipParseRecord> equipInit]
        returns[String firstSpell, String spellWeight, String noOfBooks, int line,
                List<ClassSpellBookParseRecord> spellBooks]
        :   magic { $firstSpell = $magic.firstSpell;
                    $spellWeight = $magic.spellWeight;
                    $noOfBooks = $magic.noOfBooks;
                    $line = $magic.line;
                    $spellBooks = new ArrayList<>(); }
            (bookBlock[equipInit] { $spellBooks.add(new ClassSpellBookParseRecord(
                    $bookBlock.oBase, $bookBlock.locFrom, $bookBlock.nameStr,
                    $bookBlock.noOfSpells, $bookBlock.realm, $bookBlock.glyph,
                    $bookBlock.colour, $bookBlock.cost, $bookBlock.commonness,
                    $bookBlock.min, $bookBlock.max, $bookBlock.spells,
                    $bookBlock.line)); } )+
        ;

book
        returns[String oBase, String location, String bookName,
                String noOfSpells, String realm, int line]
        :   BOOK t=STRING_BETWEEN_COLONS DELIM_COLON
                 l=STRING_BETWEEN_COLONS DELIM_COLON
                 n=STRING_BETWEEN_COLONS DELIM_COLON
                 s=DELIM_INTEGER DELIM_COLON
                 r=STRING_BETWEEN_COLONS {
                    $oBase      = $t.getText();
                    $location   = $l.getText();
                    $bookName   = $n.getText();
                    $noOfSpells = $s.getText();
                    $realm      = $r.getText();
                    $line       = $start.getLine();
                 }
        ;

bookGraphics
        returns[String glyph, String colour]
        :   BOOK_GRAPHICS g=GLYPH_VALUES GLYPH_COLON_SWITCH c=COLOUR_VALUES {
                $glyph  = $g.getText();
                $colour = $c.getText();
            }
        ;

bookProperties
        returns[String cost, String commonness, String min, String max]
        :   BOOK_PROPERTIES c=PROP_INT PROP_COLON comm=PROP_INT PROP_COLON
                mn=PROP_INT PROP_TO mx=PROP_INT {
                $cost = $c.getText();
                $commonness = $comm.getText();
                $min = $mn.getText();
                $max = $mx.getText();
            }
        ;

bookBlock[List<ClassEquipParseRecord> equipInit]
        returns[String oBase, String locFrom, String nameStr,
            String noOfSpells, String realm, String glyph,
            String colour, String cost, String commonness,
            String min, String max,
            List<ClassSpellParseRecord> spells, int line]
        // Seed the optional book-graphics / book-properties fields to "" up front: a book that
        // re-references one defined by an earlier class omits those lines, and the assembler treats
        // "" as "absent". Without these defaults the fields would be null and NPE the assembler.
        :   book { $glyph = "";
                   $colour = "";
                   $cost = "";
                   $commonness = "";
                   $min = "";
                   $max = "";
                   $oBase = $book.oBase;
                   $locFrom = $book.location;
                   $nameStr = $book.bookName;
                   $noOfSpells = $book.noOfSpells;
                   $realm = $book.realm;
                   $line = $book.line;
                   $spells = new ArrayList<>(); }
            (bookGraphics { $glyph = $bookGraphics.glyph;
                           $colour = $bookGraphics.colour; })?
            (bookProperties { $cost = $bookProperties.cost;
                             $commonness = $bookProperties.commonness;
                             $min = $bookProperties.min;
                             $max = $bookProperties.max; })?
            (equip {equipInit.add(new ClassEquipParseRecord($equip.tValue,
                                            $equip.sValue, $equip.min, $equip.max,
                                            $equip.eopts, $equip.line)); })?
            (spellBlock { $spells.add(new ClassSpellParseRecord($spellBlock.spellName,
                    $spellBlock.level, $spellBlock.mana, $spellBlock.fail, $spellBlock.spellDesc,
                    $spellBlock.effects, $spellBlock.exper, $spellBlock.line)); })+
        ;

spell
        returns[String spellname, String level, String mana, String fail, String exper, int line]
        :   SPELL s=STRING_BETWEEN_COLONS DELIM_COLON l=DELIM_INTEGER
            DELIM_COLON m=DELIM_INTEGER DELIM_COLON f=DELIM_INTEGER
            DELIM_COLON e=DELIM_INTEGER {
               $spellname = $s.getText();
               $level     = $l.getText();
               $mana      = $m.getText();
               $fail      = $f.getText();
               $exper     = $e.getText();
               $line      = $start.getLine();
            }
        ;

desc
        returns[String description]
        :   DESC STRING { $description = $STRING.getText(); }
        ;

spellBlock
        returns[String spellName, String level, String mana,
            String fail, String spellDesc, String exper, int line,
            List<EffectParseRecord> effects]
        :   spell { $spellDesc = "";
                    $effects = new ArrayList<>();
                    $spellName = $spell.spellname;
                    $level = $spell.level;
                    $mana = $spell.mana;
                    $fail = $spell.fail;
                    $exper = $spell.exper;
                    $line = $spell.line; }
            ((effectBlock { $effects.add(new EffectParseRecord($effectBlock.typeInit,
                        $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                        $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                        $effectBlock.expressionChars, $effectBlock.expressionBase,
                        $effectBlock.expressionOperation, $effectBlock.timeDiceString,
                        $effectBlock.effectMessage, $effectBlock.start.getLine())); })
        |   (desc { $spellDesc = $spellDesc + $desc.description; }))*
        ;

playerClass
        returns[PlayerClassParseRecord pClass]
        @init {
            int line;
            String nameInit = "";
            Map<String, String> statsInit = new HashMap<>();
            Map<String, String> classSkills = new HashMap<>();
            Map<String, String> extraSkills = new HashMap<>();
            String hitdieInit = "";
            String expInit = "";
            String maxAttInit = "";
            String minWghtInit = "";
            String strMultInit = "";
            List<String> titlesInit = new ArrayList<>();
            List<ClassEquipParseRecord> equipInit = new ArrayList<>();
            List<String> oFlagInit = new ArrayList<>();
            List<String> pFlagInit = new ArrayList<>();
            ClassMagicParseRecord magicInit = null;
        }
        @after {
            $pClass = new PlayerClassParseRecord(nameInit, statsInit, classSkills,
                extraSkills, hitdieInit, expInit, maxAttInit, minWghtInit,
                strMultInit, equipInit, oFlagInit, pFlagInit, titlesInit,
                magicInit, line);
        }
        :   name { line = $name.lineNo; nameInit = $name.nameStr; }
        (   stats {
                statsInit.put("STAT_STR", $stats.strs);
                statsInit.put("STAT_INT", $stats.ints);
                statsInit.put("STAT_WIS", $stats.wiss);
                statsInit.put("STAT_DEX", $stats.dexs);
                statsInit.put("STAT_CON", $stats.cons);
            }
        |   skillDisarmPhys { classSkills.put("SKILL_DISARM_PHYS", $skillDisarmPhys.skillVal);
                extraSkills.put("SKILL_DISARM_PHYS", $skillDisarmPhys.skillInc); }
        |   skillDisarmMagic { classSkills.put("SKILL_DISARM_MAGIC", $skillDisarmMagic.skillVal);
                extraSkills.put("SKILL_DISARM_MAGIC", $skillDisarmMagic.skillInc); }
        |   skillSave { classSkills.put("SKILL_SAVE", $skillSave.skillVal);
                extraSkills.put("SKILL_SAVE", $skillSave.skillInc); }
        |   skillDevice { classSkills.put("SKILL_DEVICE", $skillDevice.skillVal);
                extraSkills.put("SKILL_DEVICE", $skillDevice.skillInc); }
        |   skillStealth { classSkills.put("SKILL_STEALTH", $skillStealth.skillVal);
                extraSkills.put("SKILL_STEALTH", $skillStealth.skillInc); }
        |   skillSearch { classSkills.put("SKILL_SEARCH", $skillSearch.skillVal);
                extraSkills.put("SKILL_SEARCH", $skillSearch.skillInc); }
        |   skillMelee { classSkills.put("SKILL_TO_HIT_MELEE", $skillMelee.skillVal);
                extraSkills.put("SKILL_TO_HIT_MELEE", $skillMelee.skillInc); }
        |   skillShoot { classSkills.put("SKILL_TO_HIT_BOW", $skillShoot.skillVal);
                extraSkills.put("SKILL_TO_HIT_BOW", $skillShoot.skillInc); }
        |   skillThrow { classSkills.put("SKILL_TO_HIT_THROW", $skillThrow.skillVal);
                extraSkills.put("SKILL_TO_HIT_THROW", $skillThrow.skillInc); }
        |   skillDig { classSkills.put("SKILL_DIGGING", $skillDig.skillVal);
                extraSkills.put("SKILL_DIGGING", $skillDig.skillInc); }
        |   hitdie { hitdieInit = $hitdie.increment; }
        |   maxAttacks { maxAttInit = $maxAttacks.maxAtt; }
        |   minWeight { minWghtInit = $minWeight.minWgt; }
        |   strengthMultiplier { strMultInit = $strengthMultiplier.strMult; }
        |   exp { expInit = $exp.expStr; }
        |   title { titlesInit.add($title.titleStr); }
        |   equip { equipInit.add(new ClassEquipParseRecord($equip.tValue,
                        $equip.sValue, $equip.min, $equip.max,
                        $equip.eopts, $equip.line)); }
        |   objFlag { oFlagInit.addAll($objFlag.oFlags); }
        |   playerFlags { pFlagInit.addAll($playerFlags.pFlags); }
        |   (magicBlock[equipInit] { magicInit = new ClassMagicParseRecord($magicBlock.firstSpell,
                    $magicBlock.spellWeight, $magicBlock.noOfBooks,
                    $magicBlock.spellBooks, $magicBlock.line); }))+
        ;

file
        returns[String declaredRecordCount, List<PlayerClassParseRecord> playerClasses]
        :   recordCount { $playerClasses = new ArrayList<>();
                          $declaredRecordCount = $recordCount.count; }
            (playerClass { $playerClasses.add($playerClass.pClass); })+ EOF
        ;
