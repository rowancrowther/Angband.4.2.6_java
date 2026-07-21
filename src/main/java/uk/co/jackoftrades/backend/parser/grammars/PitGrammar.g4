// Parser for lib/gamedata/pit.txt (tokens from PitLexer). Each rule collects
// one directive's value(s) as raw text into a PitParseRecord; no validation or
// enum/registry resolution happens here - that is PitAssembler's job. See
// PitParseRecord for the field-by-field meaning of what these rules capture.
parser grammar PitGrammar;

options { tokenVocab = PitLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.pit.PitParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// "record-count:<n>" header - the count is returned as text and checked (softly)
// by the reader against the number of records actually parsed.
recordCount
        returns[String count]
        :   RECORD_COUNT PIT_INTEGER { $count = $PIT_INTEGER.getText(); }
        ;

// "name:<text>" - opens a record. The source line is captured too so the
// assembler can attribute any error to the right line in the file.
name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText();
                          $line = $start.getLine(); }
        ;

// "room:<digit>" - the room-type digit, kept as text.
room
        returns[String roomType]
        :   ROOM PIT_INTEGER { $roomType = $PIT_INTEGER.getText(); }
        ;

// "alloc:<rarity>:<level>" - both fields returned as text (ALLOC_COLON is the
// lexer's mid-value separator, distinct from the keyword-ending ':').
alloc
        returns[String rarity, String averageLevel]
        :   ALLOC r=ALLOC_INTEGER
            ALLOC_COLON l=ALLOC_INTEGER
            { $rarity = $r.getText();
              $averageLevel = $l.getText(); }
        ;

// "obj-rarity:<n>".
objRarity
        returns[String objectRarity]
        :   OBJ_RARITY PIT_INTEGER { $objectRarity = $PIT_INTEGER.getText(); }
        ;

// "color:<code>" - the single colour-code character.
colour
        returns[String colourChar]
        :   COLOUR COLOUR_CHAR { $colourChar = $COLOUR_CHAR.getText(); }
        ;

// "mon-base:<name>" - one allowed monster-base name (verbatim).
monBase
        returns[String monsterBase]
        :   MON_BASE STRING { $monsterBase = $STRING.getText(); }
        ;

// "flags-req:<FLAG> | ..." - the required race-flag codes on one line.
// @init allocates the list: ANTLR leaves a `returns` value null until assigned,
// so without this the first $flags.add(...) would NPE.
flagsReq
        returns[List<String> flags]
        @init {
            $flags = new ArrayList<>();
        }
        :   FLAGS_REQ f1=FLAG { $flags.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $flags.add($f2.getText()); })*
        ;

// "flags-ban:<FLAG> | ..." - the forbidden race-flag codes (same shape).
flagsBan
        returns[List<String> flags]
        @init {
            $flags = new ArrayList<>();
        }
        :   FLAGS_BAN f1=FLAG { $flags.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $flags.add($f2.getText()); })*
        ;

// "innate-freq:<n>".
innateFreq
        returns[String innFreq]
        :   INNATE_FREQ f=PIT_INTEGER { $innFreq = $f.getText(); }
        ;

// "spell-req:<FLAG> | ..." - required spell-flag codes. @init for the same
// null-list reason as flagsReq; pitRecord accumulates across repeated lines.
spellReq
        returns[List<String> spells]
        @init {
            $spells = new ArrayList<>();
        }
        :   SPELL_REQ s1=FLAG { $spells.add($s1.getText()); }
            (FLAG_OR s2=FLAG { $spells.add($s2.getText()); })*
        ;

// "spell-ban:<FLAG> | ..." - forbidden spell-flag codes.
spellBan
        returns[List<String> spells]
        @init {
            $spells = new ArrayList<>();
        }
        :   SPELL_BAN s1=FLAG { $spells.add($s1.getText()); }
            (FLAG_OR s2=FLAG { $spells.add($s2.getText()); })*
        ;

// "mon-ban:<name>" - one forbidden monster name (verbatim).
monBan
        returns[String monName]
        :   MON_BAN STRING { $monName = $STRING.getText(); }
        ;

// One whole pit record: a name: line followed by its directives in any order.
// @init seeds every field to an empty/neutral default so a directive the record
// omits still has a well-formed value; the alternatives below overwrite scalars
// and append to the lists (so repeated color:/mon-base:/spell-req: lines merge).
// @after packs the accumulated values into the PitParseRecord in field order.
pitRecord
        returns[PitParseRecord pit]
        @init {
            String nameInit = "";
            String pitRoomTypeInit = "";
            String rarityInit = "";
            String depthInit = "";
            String objRarityInit = "";
            List<String> flags = new ArrayList<>();
            List<String> bannedFlags = new ArrayList<>();
            String innateFreqInit = "";
            List<String> spells = new ArrayList<>();
            List<String> bannedSpells = new ArrayList<>();
            List<String> colours = new ArrayList<>();
            List<String> monsterBases = new ArrayList<>();
            List<String> bannedMonsterBases = new ArrayList<>();
            int line = 0;
        }
        @after {
            $pit = new PitParseRecord(nameInit, pitRoomTypeInit,
            rarityInit, depthInit, objRarityInit, flags,
            bannedFlags, innateFreqInit, spells, bannedSpells,
            colours, monsterBases, bannedMonsterBases, line);
        }
        :   name { nameInit = $name.nameStr; line = $name.line; }
        (   room { pitRoomTypeInit = $room.roomType; }
        |   alloc { rarityInit = $alloc.rarity;
                    depthInit = $alloc.averageLevel; }
        |   objRarity { objRarityInit = $objRarity.objectRarity; }
        |   colour { colours.add($colour.colourChar); }
        |   monBase { monsterBases.add($monBase.monsterBase); }
        |   monBan { bannedMonsterBases.add($monBan.monName); }
        |   flagsReq { flags.addAll($flagsReq.flags); }
        |   flagsBan { bannedFlags.addAll($flagsBan.flags); }
        |   innateFreq { innateFreqInit = $innateFreq.innFreq; }
        |   spellReq { spells.addAll($spellReq.spells); }
        |   spellBan { bannedSpells.addAll($spellBan.spells); })+
        ;

// The whole file: the count header, then one or more records, then EOF (which
// forces the parser to consume every record rather than stopping early).
file
        returns[String declaredRecordCount, List<PitParseRecord> pits]
        @init { $pits = new ArrayList<>(); }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (pitRecord { $pits.add($pitRecord.pit); })+ EOF
        ;
