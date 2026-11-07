// Reader for lib/gamedata/blow_methods.txt - the catalogue of the ways a monster can
// deliver a blow (HIT, BITE, CLAW, GAZE, INSULT, ...), each with the side effects it
// can cause (cut/stun), whether a miss is announced to the player, whether it does
// physical damage, the message channel it is reported on, the flavour action strings
// used to narrate it, and the short description used in monster lore.
//
// Cf. src/mon-init.c: init_parse_meth() registers the directive table
// (name/cut/stun/miss/phys/msg/act/desc -> parse_meth_*), and struct blow_method lives
// in src/mon-blows.h. This grammar does EXTRACTION ONLY: every action stashes raw text
// into a BlowMethodParseRecord; the 0/1 -> boolean conversion and the message-name
// lookup both happen in BlowMethodAssembler.
//
// The real data (19 records) always gives the eight directives in the same order, but C
// does not require it: each directive is its own handler, so after name: the others may
// come in any order, any may be missing, and act: and desc: may repeat. The blow rule is
// therefore a name: followed by a set of directives, as C reads them. act: normally
// repeats (INSULT and MOAN ship eight apiece; most methods ship one).
parser grammar BlowMethodGrammar;

options { tokenVocab = BlowMethodLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.blowmethod.BlowMethodParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// name: opens a record. The line number is captured here (rather than anywhere else in
// the block) so that an error raised later by the assembler can point at the line
// the reader would recognize as the start of the offending method.
name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

// cut:/stun:/miss:/phys: are 0-or-1 flags in the data file, but are carried as raw text
// and converted by the assembler, per this grammar's extraction-only rule. C treats them
// as C truthiness (`val ? true : false`), so ANY non-zero is true - see BlowMethodAssembler.
cut
        returns[String cutVal]
        :   CUT INTEGER { $cutVal = $INTEGER.getText(); }
        ;

stun
        returns[String stunVal]
        :   STUN INTEGER { $stunVal = $INTEGER.getText(); }
        ;

miss
        returns[String missVal]
        :   MISS INTEGER { $missVal = $INTEGER.getText(); }
        ;

phys
        returns[String physVal]
        :   PHYS INTEGER { $physVal = $INTEGER.getText(); }
        ;

// msg: names the message channel the blow is reported on, resolved against MessageType
// by the assembler. The STRING is optional because C registers this directive as
// "msg ?str msg": parse_meth_message_type leaves msgt untouched when no value is present,
// so the record keeps the zeroed default, MSG_GENERIC.
//
// That gives msg: TWO distinct "absent" shapes, and both must reach MSG_GENERIC:
//   - the line omitted entirely  -> this rule never runs, and blow's @init default holds
//   - a bare "msg:" with no text -> this rule runs but $msgStr is left NULL
// The null is why the assembler's guard must check for null BEFORE it checks isEmpty.
msg
        returns[String msgStr]
        :   MSG (STRING { $msgStr = $STRING.getText(); })?
        ;

// act: is the flavour text narrating the blow ("hits {target}"). The braces are
// placeholders expanded at display time, and are carried through verbatim here.
act
        returns[String actStr]
        :   ACT STRING { $actStr = $STRING.getText(); }
        ;

// desc: is the short phrase used in monster lore ("hit", "drool on you").
desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

// One complete blow-method block. The @init/@after pair is the suite's standard
// record-building idiom: @init seeds a local per field so that an absent optional
// directive still has a defined value, the rule body overwrites those locals as each
// directive matches, and @after assembles the immutable parse record in one go.
//
// Every directive after name: is optional, as in C, where a missing line leaves the
// mem_zalloc'd zero. The seeded defaults carry that. cut/stun/miss/phys start as null,
// which the assembler reads as false. msgInit starts as "" (not null), which is the
// omitted-line case; a bare "msg:" instead assigns null over it. Both are handled
// downstream, but they are genuinely different values arriving in the same field.
//
// act: may repeat, so actInit is a list appended to on each match, preserving file order.
// C prepends instead (parse_meth_act_msg pushes onto the head of meth->messages), so its
// list ends up reversed, and mon-blows.c picks a message by walking randint0(num_messages)
// steps along it. The order matters to that pick, so BlowMethodAssembler reverses the list.
//
// desc: may repeat too. C's parse_meth_desc does string_append, so the lines join end to
// end with nothing between them; descInit does the same. A record with no desc: carries
// "" where C holds NULL.
//
// A repeated cut:/stun:/miss:/phys:/msg: line overwrites the earlier one, as C's handlers do.
blow
        returns[BlowMethodParseRecord record]
        @init {
            String nameInit = "";
            String cutInit = null;
            String stunInit = null;
            String missInit = null;
            String physInit = null;
            String msgInit = "";
            List<String> actInit = new ArrayList<>();
            StringBuilder descInit = new StringBuilder();
            int line = 0;
        }
        @after {
            $record = new BlowMethodParseRecord(nameInit,
                cutInit, stunInit, missInit, physInit,
                msgInit, actInit, descInit.toString(), line);
        }
        :   name { line     = $name.line; nameInit = $name.nameStr.trim(); }
        (   cut  { cutInit  = $cut.cutVal; }
        |   stun { stunInit = $stun.stunVal; }
        |   miss { missInit = $miss.missVal; }
        |   phys { physInit = $phys.physVal; }
        |   msg { msgInit  = $msg.msgStr; }
        |   act { actInit.add($act.actStr); }
        |   desc { descInit.append($desc.descStr); } )*
        ;

// Entry rule: one or more blow-method blocks.
//
// The trailing EOF is what forces the whole file to be consumed, so trailing junk after
// the last block is a syntax error rather than being silently ignored.
file
        returns[List<BlowMethodParseRecord> records]
        @init {
            $records = new ArrayList<>();
        }
        :   (blow { $records.add($blow.record); })+ EOF
        ;
