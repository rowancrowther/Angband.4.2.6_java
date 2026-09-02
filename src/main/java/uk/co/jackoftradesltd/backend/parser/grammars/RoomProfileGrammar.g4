/*
 * @author Rowan Crowther
 *
 * Parser for room_template.txt, paired with RoomProfileLexer via tokenVocab. Each rule below
 * covers one directive and hands its raw text back via a `returns[...]` value; roomProfile
 * stitches one record's worth of those together into a RoomProfileParseRecord, and file
 * repeats that for the whole document. Every field stays text here (no int/enum parsing) —
 * that happens downstream in RoomProfileAssembler, which is also where flags:/tval:/rows:/
 * columns: get validated against C's parse_room_* semantics.
 */
parser grammar RoomProfileGrammar;

options { tokenVocab = RoomProfileLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

/** The file's {@code record-count:} header, kept as text for {@code file} to validate later. */
recordCount
        returns[String count]
        :   RECORD_COUNT c=INTEGER { $count = $c.getText(); }
        ;

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
        :   TVAL t=STRING { $tvalStr = $t.getText(); }
        ;

/**
 * The room's (optional) {@code flags:} directive — a {@code |}-separated list of flag names. Made
 * optional at the {@link #roomProfile} call site, since the real data has records with no
 * {@code flags:} line at all; when absent, this rule simply doesn't run and {@code flagList}
 * stays empty on the caller's side.
 */
flags   returns[List<String> flagList]
        @init {
            $flagList = new ArrayList<>();
        }
        :   FLAGS f1=FLAG {
                $flagList.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $flagList.add($f2.getText());
            })*
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
 * One full room record — every directive in {@code name:}...{@code D:} order, stitched into a
 * {@link RoomProfileParseRecord}. {@code flags} is wrapped in {@code (...)?} because it's the one
 * directive that's genuinely optional in the data.
 */
roomProfile
        returns[RoomProfileParseRecord profile]
        @init {
            String nameInit = "";
            String typeInit = "";
            String ratingInit = "";
            String rowsInit = "";
            String columnsInit = "";
            String doorsInit = "";
            String tvalInit = "";
            List<String> flagsInit = new ArrayList<>();
            List<String> roomInit = new ArrayList<>();
            int profileLineNo = 0;
            int roomMapLineNo = 0;
        }
        @after {
            $profile = new RoomProfileParseRecord(nameInit, typeInit, ratingInit,
                rowsInit, columnsInit, doorsInit, tvalInit, flagsInit, roomInit,
                profileLineNo, roomMapLineNo);
        }
        :   name { nameInit = $name.nameStr; profileLineNo = $name.line; }
            type { typeInit = $type.typeInt; }
            rating { ratingInit = $rating.ratingInt; }
            rows { rowsInit = $rows.rowsInt; }
            columns { columnsInit = $columns.columnsInt; }
            doors { doorsInit = $doors.doorsInt; }
            tval { tvalInit = $tval.tvalStr; }
            (flags { flagsInit.addAll($flags.flagList); })?
            roomMap { roomInit.addAll($roomMap.roomMapList); roomMapLineNo = $roomMap.firstLine; }
        ;

/**
 * The whole file: the {@code record-count:} header followed by one or more room records.
 * {@code declaredCount} is kept as text rather than validated here — RoomProfileReader compares
 * it against {@code records.size()} after the parse completes, since a mismatch is a soft error,
 * not a reason to fail the parse.
 */
file
        returns[String declaredCount, List<RoomProfileParseRecord> records]
        @init {
            $records = new ArrayList<>();
        }
        :   recordCount { $declaredCount = $recordCount.count; }
            (roomProfile { $records.add($roomProfile.profile); })+ EOF
        ;
