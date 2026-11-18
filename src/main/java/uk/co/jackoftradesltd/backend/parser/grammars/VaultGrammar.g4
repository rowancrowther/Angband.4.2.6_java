/*
 * @author Rowan Crowther
 *
 * Parser for vault.txt, paired with VaultLexer via tokenVocab. Each rule below covers one
 * directive and hands its raw text back via a `returns[...]` value; profileRecord stitches one
 * record's worth of those together into a VaultParseRecord, and file repeats that for the whole
 * document. Every field stays text here (no int/enum parsing) — that happens downstream in
 * VaultAssembler, which is also where type:/rows:/columns:/max-depth:/flags: get validated
 * against C's parse_vault_* handlers in generate.c.
 *
 * The layout stays one Directive per D: line, in file order, and the grammar does no joining.
 * The assembler checks each line's length against columns: as it reads it, and builds both the
 * list of rows and the flat string C keeps in vault.text (indexed there as text[y * wid + x],
 * built by repeated string_append).
 *
 * Directive order is free after name:, as it is in C's parser: any mix of the other eight
 * directives, in any order, each optional and repeatable. C depends on the file's ordering anyway
 * (rows:/columns: look up room_builders by v->typ, so they need type: to have arrived first, and
 * the D: length check needs columns:), and so does the assembler, which handles the directives
 * in file order and refuses a rows:/columns: with no known type, and a D: line of the wrong
 * length for the columns: seen so far. The grammar's only structural rule is that every record
 * opens with name:, which is what starts a vault in C.
 */
parser grammar VaultGrammar;

options { tokenVocab = VaultLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.vault.VaultParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

    import java.util.ArrayList;
    import java.util.List;
}

/**
 * The vault's {@code name:} directive. Its line is captured from {@code $start}, the NAME token
 * itself. The record carries no line number of its own, so this directive's line is the one that
 * identifies the record, and the one point in a record that is always present to anchor error
 * messages to.
 */
name
        returns[Directive nameDirective]
        :   NAME n=STRING {
                String value = $n.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $nameDirective = new Directive("name", values, $start.getLine());
            }
        ;

/**
 * The vault's {@code type:} directive, as text — a room-builder name such as
 * {@code "Lesser vault"}, {@code "Interesting room"} or {@code "Greater vault (new)"} (the data
 * uses seven; C's list has nineteen). Matching that to a {@code RoomType} is VaultAssembler's job,
 * not the grammar's.
 */
type
        returns[Directive typeDirective]
        :   TYPE t=STRING { 
                String value = $t.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $typeDirective = new Directive("type", values, $start.getLine()); 
            }
        ;

/**
 * The vault's {@code rating:} directive, as text — what a level adds to its danger component when
 * it places this vault.
 */
rating
        returns[Directive ratingDirective]
        :   RATING r=INTEGER {
                String value = $r.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $ratingDirective = new Directive("rating", values, $start.getLine());
            }
        ;

/** The vault's {@code rows:} directive, as text — the declared height of its layout. */
rows
        returns[Directive rowsDirective]
        :   ROWS r=INTEGER {
                String value = $r.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $rowsDirective = new Directive("rows", values, $start.getLine());
            }
        ;

/**
 * The vault's {@code columns:} directive, as text — the declared width of its layout, and the
 * length every {@code D:} line must have.
 */
columns
        returns[Directive colsDirective]
        :   COLUMNS c=INTEGER {
                String value = $c.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $colsDirective = new Directive("columns", values, $start.getLine());
            }
        ;

/** The vault's {@code min-depth:} directive, as text; 0 means no minimum. */
minDepth
        returns[Directive minDepthDirective]
        :   MIN_DEPTH m=INTEGER {
                String value = $m.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $minDepthDirective = new Directive("min-depth", values, $start.getLine());
            }
        ;

/**
 * The vault's {@code max-depth:} directive, as text. A declared 0 means "no maximum" and C
 * rewrites it to {@code z_info->max_depth} while parsing (generate.c, parse_vault_max_depth); that
 * rewrite is left to VaultAssembler, which is the layer that knows the game constants.
 */
maxDepth
        returns[Directive maxDepthDirective]
        :   MAX_DEPTH m=INTEGER {
                String value = $m.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $maxDepthDirective = new Directive("max-depth", values, $start.getLine());        
            }
        ;

/**
 * One {@code flags:} directive — a {@code |}-separated list of flag names, kept as one value per
 * name under the directive name {@code "flag"}. Optional and repeatable at the
 * {@link #profileRecord} call site, like every directive after {@code name:}: most records carry
 * no {@code flags:} line at all, and the file's own header says as many may be used as are needed,
 * which matches C setting each line's flags in the same bitflag (generate.c, parse_vault_flags).
 */
flag
        returns[Directive flagDirective]
        @init { List<String> flags = new ArrayList<>(); }
        @after {
                $flagDirective = new Directive("flag", flags, $start.getLine());
        }
        :   FLAGS f1=FLAG { flags.add($f1.getText()); }
            (OR f2=FLAG { flags.add($f2.getText()); })*
        ;

/**
 * One {@code D:} line of the vault's layout, as text, under the directive name {@code "map"}; a
 * record's layout is one such directive per row, in file order. Each keeps its trailing padding,
 * since that padding is what makes the row reach the declared width.
 */
d
        returns[Directive mapDirective]
        :   D m=STRING {
                String value = $m.getText();
                List<String> values = new ArrayList<>();
                values.add(value);
                $mapDirective = new Directive("map", values, $start.getLine());
            }
        ;

/**
 * One full vault record — a {@code name:} directive, then any number of the other eight
 * directives in any order, stitched into a {@link VaultParseRecord} as one list in the order the
 * file wrote them. The alternatives are wrapped in {@code (...)*}, so each is optional and may
 * repeat; whether a record that lacks {@code rows:} or {@code type:}, or repeats one, is
 * acceptable is for VaultAssembler to decide.
 *
 * <p>The layout is not joined here: each {@code D:} line is its own {@code map} directive, and
 * VaultAssembler builds the per-row list and the flat string C keeps in {@code vault.text}.
 */
profileRecord
        returns[List<Directive> vault]
        @init {
            $vault = new ArrayList<Directive>();
        }
        :   name { $vault.add($name.nameDirective); }
        (   type { $vault.add($type.typeDirective); }
        |   rating { $vault.add($rating.ratingDirective); }
        |   rows { $vault.add($rows.rowsDirective); }
        |   columns { $vault.add($columns.colsDirective); }
        |   minDepth { $vault.add($minDepth.minDepthDirective); }
        |   maxDepth { $vault.add($maxDepth.maxDepthDirective); }
        |   flag { $vault.add($flag.flagDirective); }
        |   d { $vault.add($d.mapDirective); })*
        ;

/**
 * The whole file: any number of vault records, including none.
 */
file
        returns[List<VaultParseRecord> profiles]
        @init { $profiles = new ArrayList<>(); }
        :   (profileRecord { $profiles.add(new VaultParseRecord($profileRecord.vault)); })* EOF
        ;
