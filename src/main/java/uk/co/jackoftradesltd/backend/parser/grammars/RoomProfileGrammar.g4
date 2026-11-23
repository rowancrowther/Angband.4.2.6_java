/*
 * @author Rowan Crowther
 *
 * Parser for room_template.txt, paired with RoomProfileLexer via tokenVocab. Each rule below
 * covers one directive and hands its raw text back via a `returns[...]` value; roomProfile
 * stitches one record's worth of those together into a RoomProfileParseRecord, and file
 * repeats that for the whole document. Every field stays text here (no int/enum parsing) —
 * that happens downstream in RoomProfileAssembler, which is also where flags:/tval:/rows:/
 * columns: get validated against C's parse_room_* semantics.
 *
 * After name: the other eight directives come in any order, each optional and repeatable, as the
 * handlers in C's init_parse_room do. A repeated type:/rating:/rows:/columns:/doors:/tval: keeps
 * only its last text here, so the assembler never sees an earlier value that C would have refused
 * as it was read. Keeping one Directive per line, as VaultGrammar does, is parked (docs/Parked.md).
 */
parser grammar RoomProfileGrammar;

options { tokenVocab = RoomProfileLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

/**
 * The room's {@code name:} directive. {@code line} is captured here (from {@code $start}, the
 * NAME token itself) because it becomes the whole record's {@code profileLineNo} in
 * {@link #roomProfile} — the one point in a record with a stable, always-present line number to
 * anchor error messages to.
 */
name
        returns[String nameStr, int line]
        :   NAME n=STRING { $nameStr = $n.getText(); $line = $start.getLine(); }
        ;

/** The room's {@code type:} directive, as text. */
type
        returns[String typeInt]
        :   TYPE t=INTEGER { $typeInt = $t.getText(); }
        ;

/** The room's {@code rating:} directive, as text — what a dungeon profile selects templates by. */
rating
        returns[String ratingInt]
        :   RATING r=INTEGER { $ratingInt = $r.getText(); }
        ;

/** The room's {@code rows:} directive, as text. */
rows
        returns[String rowsInt]
        :   ROWS r=INTEGER { $rowsInt = $r.getText(); }
        ;

/** The room's {@code columns:} directive, as text. */
columns
        returns[String columnsInt]
        :   COLUMNS c=INTEGER { $columnsInt = $c.getText(); }
        ;

/** The room's {@code doors:} directive, as text. */
doors
        returns[String doorsInt]
        :   DOORS d=INTEGER { $doorsInt = $d.getText(); }
        ;

/**
 * The room's {@code tval:} directive, as text — numeric ({@code "0"}) or a name ({@code "rod"},
 * {@code "wand"}, ...) depending on the record. Resolving either form to a tval is
 * RoomProfileAssembler's job, not the grammar's.
 */
tval    returns[String tvalStr]
        :   TVAL t=TVAL_TO_COLON { $tvalStr = $t.getText(); }
        ;

/**
 * One {@code flags:} directive — a list of flag names split on any run of blanks and bars, as
 * {@code strtok(s, " |")} splits it in {@code parse_room_flags}. A run of separators with no
 * name, or a first name, may follow {@code FLAGS} directly, and the loop after it takes a
 * separator with or without a name, so a leading run before the first name, a trailing run and
 * two {@code FLAG_OR} tokens in a row all load as in C. A bare {@code flags:} loads with no
 * flags here, where C refuses it as a missing required {@code str} (parked in
 * {@code docs/Parked.md}). Like every directive after {@code name:}, it is optional and
 * repeatable at the {@link #roomProfile} call site: the real data has records with no
 * {@code flags:} line at all, and a second line adds its flags to the first, as
 * {@code parse_room_flags} ORs into the template's flags. When absent, this rule simply doesn't
 * run and the caller's flag list stays empty.
 */
flags   returns[List<String> flagList]
        @init {
            $flagList = new ArrayList<>();
        }
        :   FLAGS ( FLAG_OR+
        |   f1=FLAG {
                $flagList.add($f1.getText());
            })? (FLAG_OR (f2=FLAG {
                $flagList.add($f2.getText());
            })?)*
        ;

/** One {@code D:} line of a room's layout, with its own source line number for diagnostics. */
dLine
        returns[String roomMapLine, int lineNo]
        :   DLINE l=STRING { $roomMapLine = $l.getText(); $lineNo = $start.getLine(); }
        ;

/**
 * All of a room's {@code D:} lines, in file order, plus the source line of the first one
 * ({@code firstLine}) for callers that want to anchor a map-specific error to where the map
 * actually starts rather than to the record's {@code name:} line.
 */
roomMap
        returns[List<String> roomMapList, int firstLine]
        @init {
            $roomMapList = new ArrayList<>();
            $firstLine = -1;
        }
        :   (dLine {
                if ($firstLine == -1) $firstLine = $dLine.lineNo;
                if ($dLine.lineNo < $firstLine) $firstLine = $dLine.lineNo;
                $roomMapList.add($dLine.roomMapLine);
            })+
        ;

/**
 * One full room record — a {@code name:} directive, then any number of the other eight in any
 * order, stitched into a {@link RoomProfileParseRecord}. The alternatives are wrapped in
 * {@code (...)*}, so each is optional and may repeat. A directive left out stays {@code null} (an
 * empty list for {@code flags:} and {@code D:}), and the assembler turns that into C's zero. A
 * repeated number or {@code tval:} line overwrites the earlier text, while {@code flags:} and
 * {@code D:} lines accumulate in file order.
 */
roomProfile
        returns[RoomProfileParseRecord profile]
        @init {
            String nameInit = "";
            String typeInit = null;
            String ratingInit = null;
            String rowsInit = null;
            String columnsInit = null;
            String doorsInit = null;
            String tvalInit = null;
            List<String> flagsInit = new ArrayList<>();
            List<String> roomInit = new ArrayList<>();
            int profileLineNo = 0;
            int roomMapLineNo = -1;
        }
        @after {
            $profile = new RoomProfileParseRecord(nameInit, typeInit, ratingInit,
                rowsInit, columnsInit, doorsInit, tvalInit, flagsInit, roomInit,
                profileLineNo, roomMapLineNo);
        }
        :   name { nameInit = $name.nameStr; profileLineNo = $name.line; }
        (   type { typeInit = $type.typeInt; }
        |   rating { ratingInit = $rating.ratingInt; }
        |   rows { rowsInit = $rows.rowsInt; }
        |   columns { columnsInit = $columns.columnsInt; }
        |   doors { doorsInit = $doors.doorsInt; }
        |   tval { tvalInit = $tval.tvalStr; }
        |   flags { flagsInit.addAll($flags.flagList); }
        |   roomMap { 
                roomInit.addAll($roomMap.roomMapList);
                if (roomMapLineNo == -1)
                    roomMapLineNo = $roomMap.firstLine; 
            } )*
        ;

/**
 * The whole file: any number of room records, including none.
 */
file
        returns[List<RoomProfileParseRecord> records]
        @init {
            $records = new ArrayList<>();
        }
        :   (roomProfile { $records.add($roomProfile.profile); })* EOF
        ;
