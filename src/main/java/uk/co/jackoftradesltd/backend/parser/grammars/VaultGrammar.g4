/*
 * @author Rowan Crowther
 *
 * Parser for vault.txt, paired with VaultLexer via tokenVocab. Each rule below covers one
 * directive and hands its raw text back via a `returns[...]` value; profileRecord stitches one
 * record's worth of those together into a VaultParseRecord, and file repeats that for the whole
 * document. Every field stays text here (no int/enum parsing) — that happens downstream in
 * VaultAssembler, which is also where type:/rows:/columns:/max-depth:/flags: get validated
 * against C's parse_vault_* semantics ([C] src/generate.c:479-593).
 *
 * The one place this grammar does more than collect text is the layout: profileRecord keeps the
 * D: lines both as a list (one string per row) and concatenated into a single flat string. The
 * flat form is what C stores in vault.text and indexes as text[y * wid + x], built there by
 * repeated string_append ([C] src/generate.c:591); the list is kept alongside it because the
 * assembler needs per-row lengths to check them against columns:.
 *
 * Directive order is fixed here rather than free. C's parser accepts them in any order but then
 * depends on the file's ordering anyway — rows:/columns: look up room_builders by v->typ, so they
 * need type: to have arrived first, and the D: length check needs columns:. Requiring the data
 * file's actual order makes that dependency explicit instead of latent.
 */
parser grammar VaultGrammar;

options { tokenVocab = VaultLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.vault.VaultParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

/** The file's {@code record-count:} header, kept as text for {@code file} to validate later. */
recordCount
        returns[String count]
        :   RECORD_COUNT c=INTEGER { $count = $c.getText(); }
        ;

/**
 * The vault's {@code name:} directive. {@code line} is captured here (from {@code $start}, the
 * NAME token itself) because it becomes the whole record's line number in {@link #profileRecord}
 * — the one point in a record with a stable, always-present line number to anchor error messages
 * to.
 */
name
        returns[String nameStr, int line]
        :   NAME n=STRING { $nameStr = $n.getText(); $line = $start.getLine(); }
        ;

/**
 * The vault's {@code type:} directive, as text — one of the seven room-builder names the data
 * uses ({@code "Lesser vault"}, {@code "Interesting room"}, {@code "Greater vault (new)"}, ...).
 * Matching that to a {@code RoomType} is VaultAssembler's job, not the grammar's.
 */
type
        returns[String typeStr]
        :   TYPE t=STRING { $typeStr = $t.getText(); }
        ;

/**
 * The vault's {@code rating:} directive, as text — what a level adds to its danger component when
 * it places this vault.
 */
rating
        returns[String ratingStr]
        :   RATING r=INTEGER { $ratingStr = $r.getText(); }
        ;

/** The vault's {@code rows:} directive, as text — the declared height of its layout. */
rows
        returns[String rowsStr]
        :   ROWS r=INTEGER { $rowsStr = $r.getText(); }
        ;

/**
 * The vault's {@code columns:} directive, as text — the declared width of its layout, and the
 * length every {@code D:} line must have.
 */
columns
        returns[String columnsStr]
        :   COLUMNS c=INTEGER { $columnsStr = $c.getText(); }
        ;

/** The vault's {@code min-depth:} directive, as text; 0 means no minimum. */
minDepth
        returns[String minDepthStr]
        :   MIN_DEPTH m=INTEGER { $minDepthStr = $m.getText(); }
        ;

/**
 * The vault's {@code max-depth:} directive, as text. A declared 0 means "no maximum" and C
 * rewrites it to {@code z_info->max_depth} while parsing ([C] src/generate.c:561); that rewrite is
 * left to VaultAssembler, which is the layer that knows the game constants.
 */
maxDepth
        returns[String maxDepthStr]
        :   MAX_DEPTH m=INTEGER { $maxDepthStr = $m.getText(); }
        ;

/**
 * One {@code flags:} directive — a {@code |}-separated list of flag names. Made optional and
 * repeatable at the {@link #profileRecord} call site: most records carry no {@code flags:} line at
 * all, and the file's own header says as many may be used as are needed, which matches C ORing
 * each line into the same bitflag ([C] src/generate.c:565).
 */
flag
        returns[List<String> flags]
        @init { $flags = new ArrayList<>(); }
        :   FLAGS f1=FLAG { $flags.add($f1.getText()); }
            (OR f2=FLAG { $flags.add($f2.getText()); })*
        ;

/**
 * All of a vault's {@code D:} lines, in file order, one string per row. Each keeps its trailing
 * padding, since that padding is what makes the row reach the declared width.
 */
d
        returns[List<String> map]
        @init { $map = new ArrayList<>(); }
        :   (D m=STRING { $map.add($m.getText()); })+
        ;

/**
 * One full vault record — every directive in {@code name:}...{@code D:} order, stitched into a
 * {@link VaultParseRecord}. {@code flag} is wrapped in {@code (...)*} because {@code flags:} is
 * the one directive that is both optional and repeatable in the data.
 *
 * <p>The layout is stored twice over: {@code map} as one entry per row, and {@code mapText} as
 * those rows concatenated — the flat {@code wid * hgt} string C keeps in {@code vault.text}.
 */
profileRecord
        returns[VaultParseRecord record]
        @init {
            String nameInit = "";
            String typeInit = "";
            String ratingInit = "";
            String rowsInit = "";
            String columnsInit = "";
            String minDepthInit = "";
            String maxDepthInit = "";
            List<String> flags = new ArrayList<>();
            String mapText = "";
            List<String> map = new ArrayList<>();
            int line = 0;
        }
        @after {
            $record = new VaultParseRecord(nameInit, typeInit, mapText, map, ratingInit,
                rowsInit, columnsInit, minDepthInit, maxDepthInit, flags, line);
        }
        :   name {
                line = $name.line;
                nameInit = $name.nameStr;
            }
            type { typeInit = $type.typeStr; }
            rating { ratingInit = $rating.ratingStr; }
            rows { rowsInit = $rows.rowsStr; }
            columns { columnsInit = $columns.columnsStr; }
            minDepth { minDepthInit = $minDepth.minDepthStr; }
            maxDepth { maxDepthInit = $maxDepth.maxDepthStr; }
            (flag { flags.addAll($flag.flags); })*
            d {
                map.addAll($d.map);
                StringBuilder sb = new StringBuilder();
                for (String mapLine : map) {
                    sb.append(mapLine);
                }
                mapText = sb.toString();
            }
        ;

/**
 * The whole file: the {@code record-count:} header followed by one or more vault records.
 * {@code declaredRecordCount} is kept as text rather than validated here — VaultReader compares it
 * against {@code profiles.size()} after the parse completes, since a mismatch is a soft error, not
 * a reason to fail the parse.
 */
file
        returns[List<VaultParseRecord> profiles, String declaredRecordCount]
        @init { $profiles = new ArrayList<>(); }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (profileRecord { $profiles.add($profileRecord.record); })+ EOF
        ;
