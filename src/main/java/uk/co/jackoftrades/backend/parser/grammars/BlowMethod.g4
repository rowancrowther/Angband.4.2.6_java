// Parser+lexer for lib/gamedata/blow_methods.txt - every monster attack
// method (HIT, TOUCH, PUNCH, ...): whether it can cut/stun, whether a miss
// is announced, whether it's physical, its message type, one-or-more
// action-string variants, and a monster-lore description. Cf.
// src/mon-init.c: struct file_parser meth_parser (mon-init.c:265).
//
// No problems found - `blowMethod`'s strict sequence
// "name cut stun miss phys msg act+ desc" matches both shapes actually
// used across all 19 records in blow_methods.txt (most have exactly one
// act: line; INSULT and MOAN have 8 each, confirming the `+` is needed and
// no record needs more flexibility than this fixed order provides).

grammar BlowMethod;

@header {
    import uk.co.jackoftrades.middle.combat.BlowMethod;
    import uk.co.jackoftrades.middle.enums.MessageType;

    import java.util.List;
    import java.util.ArrayList;
}

// "name:<CODE>" - starts a new blow method record.
name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

// "cut:0|1" - whether this method can inflict a cut.
cut
        returns[boolean cutBool]
        :   CUT BOOLEAN { $cutBool = $BOOLEAN.getText().equals("1"); }
        ;

// "stun:0|1" - whether this method can stun.
stun
        returns[boolean stunBool]
        :   STUN BOOLEAN { $stunBool = $BOOLEAN.getText().equals("1"); }
        ;

// "miss:0|1" - whether the player is told when this blow misses.
miss
        returns[boolean missBool]
        :   MISS BOOLEAN { $missBool = $BOOLEAN.getText().equals("1"); }
        ;

// "phys:0|1" - whether this method does physical damage.
phys
        returns[boolean physBool]
        :   PHYS BOOLEAN { $physBool = $BOOLEAN.getText().equals("1"); }
        ;

// "msg:<MSG_TYPE>" - message type/sound to display.
msg
        returns[MessageType message]
        :   MSG UCASE {
                String raw = $UCASE.getText();
                $message = MessageType.valueOf("MSG_" + raw);
            }
        ;

// "act:<text>" - one action-string variant (e.g. "hits {target}"); can
// repeat - the engine picks one at random for flavour.
act
        returns[String actStr]
        :   ACT LCASE { $actStr = $LCASE.getText(); }
        ;

// "desc:<text>" - monster-lore description of this attack method.
desc
        returns[String descStr]
        :   DESC LCASE { $descStr = $LCASE.getText(); }
        ;

// One full blow method record: a fixed sequence of name/cut/stun/miss/
// phys/msg, one-or-more act: lines, then desc.
blowMethod
        returns[BlowMethod method]
        @init {
            String nameInit = "";
            boolean cutInit = false;
            boolean stunInit = false;
            boolean missInit = false;
            boolean physInit = false;
            MessageType msgInit = MessageType.MSG_NONE;
            List<String> actInit = new ArrayList<>();
            String descInit = "";
        }
        @after {
            $method = new BlowMethod(nameInit, cutInit, stunInit, missInit, physInit,
                                     msgInit, actInit, descInit);
        }
        :   name { nameInit = $name.nameStr; }
            cut { cutInit = $cut.cutBool; }
            stun { stunInit = $stun.stunBool; }
            miss { missInit = $miss.missBool; }
            phys { physInit = $phys.physBool; }
            msg { msgInit = $msg.message; }
            (act { actInit.add($act.actStr); })+
            desc { descInit = $desc.descStr; }
        ;

// Top-level rule: the whole file is one or more blow method records.
file
        returns[List<BlowMethod> methods]
        @init {
            $methods = new ArrayList<>();
        }
        :   (blowMethod { $methods.add($blowMethod.method); })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

CUT
        :   'cut:'
        ;

STUN
        :   'stun:'
        ;

MISS
        :   'miss:'
        ;

PHYS
        :   'phys:'
        ;

MSG
        :   'msg:'
        ;

ACT
        :   'act:'
        ;

DESC
        :   'desc:'
        ;

// A single 0/1 digit - used for cut:/stun:/miss:/phys:.
BOOLEAN
        :   ('0' | '1')
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for name:/msg:.
UCASE
        :   ('A'..'Z' | '_')+
        ;

// Free-running text - used for act:/desc:, including the "{target}"
// placeholder syntax act: strings use.
LCASE
        :   ('a'..'z' | '{' | '}' | ' ' | '!' | '\'' | 'A'..'Z' | '?')+
        ;