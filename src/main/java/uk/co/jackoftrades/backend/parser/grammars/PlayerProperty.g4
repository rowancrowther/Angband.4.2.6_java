// Parser+lexer for lib/gamedata/player_property.txt - the catalogue of every
// player/object flag and elemental resist/vuln/immunity, each with display
// name, description, and an optional binding to a UI element (ui_entry.txt).
// Cf. src/init.c: struct file_parser player_property_parser (init.c:1406),
// directive table registered in init_parse_player_prop() (init.c:1308-1314):
// type/code/desc/name/value/bindui -> parse_player_prop_type/_code/_desc/
// _name/_value/_bindui.
//
// Verified against real data: `code`'s player-vs-object dispatch, `bindui`'s
// TAG -> ElementEnum-vs-Stat dispatch (e.g. "bindui:resist_ui_compact_0<DARK>..."
// under type:player resolves the tag as an ElementEnum; "bindui:stat_mod_ui_
// compact_0<STR>..." under type:object resolves it as a Stat), and `value`'s
// -1/1/3 switch all match the file's actual content and its own header
// documentation - no bugs found there.
//
// POTENTIAL PROBLEMS:
//
//   1. The file's own header says bindui: "can appear multiple times for
//      the same entry to bind it to multiple user interface elements", and
//      parse_player_prop_bindui (init.c:1262) allocates a fresh
//      player_bound_ui node every call (consistent with a list the C side
//      can append to repeatedly) - but `property` below matches `bindui`
//      with `?` (at most once). No current player_property.txt entry
//      actually has two bindui: lines back to back, so this is latent
//      rather than active.
//
//   2. Worth double-checking rather than a confirmed bug: the C parser
//      registers bindui's first field as a single sym ("bindui sym ui int
//      aux sym uival", init.c:1313), so the whole "resist_ui_compact_0
//      <DARK>" text - tag included - is one string on the C side. This
//      grammar instead lexes that into two tokens, TEXT ("resist_ui_
//      compact_0") and TAG ("<DARK>"), and looks up the UI entry via
//      GameConstants.getUIEntry() on the TEXT portion alone. Splitting
//      pre-lookup matches the documented intent ("the name will be
//      parameterized with the element name"), but I couldn't find where
//      the C side's combined string actually gets the tag stripped/
//      substituted (no `player_bound_ui`/`bound_ui` usage outside init.c in
//      this checkout) to confirm the two behave the same way.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar PlayerProperty;

@header {
    import uk.co.jackoftrades.middle.player.PlayerProperty;
    import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyType;
    import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyValue;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum
    import uk.co.jackoftrades.frontend.entries.UIEntry;

    import java.util.List;
    import java.util.ArrayList;
}

// Shared across one whole `property` match - set by `type` before `code`/
// `bindui` read it, so this works correctly entry-to-entry as long as
// `type` is always matched first within a record (which `property` enforces).
@members { String typeOption; }

// "type:player|object|element" - which kind of property this record is.
type
        :   TYPE TYPE_OPTIONS { typeOption = $TYPE_OPTIONS.getText(); }
        ;

// "code:<NAME>" - the engine-internal flag name (PF_*/OF_* depending on
// `type`); absent for type:element records (see player_property.txt's own
// "ElementEnum codes are templates" note). Cf. parse_player_prop_code (init.c).
code
        returns[ObjectFlag oFlag, PlayerFlag pFlag]
        @init {
            $oFlag = ObjectFlag.OF_NONE;
            $pFlag = PlayerFlag.PF_NONE;
        }
        :   CODE TEXT {
            String raw = $TEXT.getText();
            if (typeOption.equals("player"))
                $pFlag = PlayerFlag.valueOf("PF_" + raw);
            else if (typeOption.equals("object"))
                $oFlag = ObjectFlag.valueOf("OF_" + raw);
        };

// "bindui:<ui entry name>[<TAG>]:<aux 0|1>:<value|special>" - binds this
// property to a UI element in ui_entry.txt; under type:player the optional
// <TAG> names an ElementEnum, under type:object it names a Stat (see top-of-
// file note #2 re: the TEXT/TAG split). Cf. parse_player_prop_bindui
// (init.c:1262). See top-of-file note #1 re: the multi-occurrence gap.
bindui
        returns[UIEntry entry, ElementEnum elemEnum, Stats statEnum, String bindUIVal]
        @init{
            $elemEnum = ElementEnum.ELEM_NONE;
            $statEnum = Stats.STAT_NONE;
        }
        :   BINDUI TEXT {
            $entry = GameConstants.getUIEntry($TEXT.getText());
        } (TAG {
            String raw = $TAG.getText();
            String flag;
            if (raw.length() < 3) {
                flag = "ERROR";
            } else {
                flag = raw.substring(1, raw.length() -1);
                if (typeOption.equals("player"))
                    $elemEnum = ElementEnum.valueOf("ELEM_" + flag);
                else if (typeOption.equals("object"))
                    $statEnum = Stats.valueOf("STAT_" + flag);
            }
        })? BINDUIVAL {
            $bindUIVal = $BINDUIVAL.getText();
        };

// "name:<display name>" - cf. parse_player_prop_name (init.c).
name
        returns[String nameStr]
        :   NAME TEXT {$nameStr = $TEXT.getText(); }
        ;

// "desc:<text>" - can repeat to build up a multi-line description (see
// `property`'s `(desc {...})*`) - cf. parse_player_prop_desc (init.c).
desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

// "value:-1|1|3" - vulnerability/resistance/immunity, for type:element
// records only - cf. parse_player_prop_value (init.c).
value
        returns[int valueInt]
        :   VALUE INTEGER {
            $valueInt = Integer.parseInt($INTEGER.getText());
        };

// One full property record: type, then any mix of code/bindui/name/desc*/
// value in the order they appear in the file.
property
        returns[PlayerProperty playerProp]
        @init {
            PlayerProperty.PlayerPropertyType typeInit;

            ObjectFlag oFlagInit = ObjectFlag.OF_NONE;
            PlayerFlag pFlagInit = PlayerFlag.PF_NONE;
            UIEntry entryInit = null;
            Stats statInit = Stats.STAT_NONE;
            ElementEnum elemInit = ElementEnum.ELEM_NONE;
            String bindUIValInit = "";
            String nameInit = "";
            String descInit = "";
            PlayerPropertyValue valueInit = PlayerPropertyValue.NONE;
        }
        @after {
            $playerProp = new PlayerProperty(typeInit, pFlagInit, oFlagInit,
                                            entryInit, statInit, elemInit,
                                            bindUIValInit, nameInit, descInit,
                                            valueInit);
        }
        :   type {
                if (typeOption.equals("player"))
                    typeInit = PlayerPropertyType.PROP_TYPE_PLAYER;
                else if (typeOption.equals("object"))
                    typeInit = PlayerPropertyType.PROP_TYPE_OBJECT;
                else
                    typeInit = PlayerPropertyType.PROP_TYPE_ELEMENT;
            }
            (code {
                oFlagInit = $code.oFlag;
                pFlagInit = $code.pFlag;
            })?
            (bindui {
                entryInit = $bindui.entry;
                elemInit = $bindui.elemEnum;
                statInit = $bindui.statEnum;
                bindUIValInit = $bindui.bindUIVal;
            })?
            name { nameInit = $name.nameStr; }
            (desc { descInit = descInit + $desc.descStr; })*
            (value {
                switch ($value.valueInt) {
                    case -1:
                        valueInit = PlayerPropertyValue.VULNERABILITY;
                        break;
                    case 1:
                        valueInit = PlayerPropertyValue.RESISTANCE;
                        break;
                    case 3:
                        valueInit = PlayerPropertyValue.IMMUNITY;
                        break;
                    default:
                        valueInit = PlayerPropertyValue.NONE;
                }
            })?
        ;

// Top-level rule: the whole file is one or more property records.
file
        returns[List<PlayerProperty> properties]
        @init {
            $properties = new ArrayList<>();
        }
        :   (property {
                $properties.add($property.playerProp);
            })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

TYPE
        :   'type:'
        ;

CODE
        :   'code:'
        ;

BINDUI
        :   'bindui:'
        ;

NAME
        :   'name:'
        ;

DESC
        :   'desc:'
        ;

VALUE
        :   'value:'
        ;

// The value of a type: line.
TYPE_OPTIONS
        :   'player' | 'object' | 'element'
        ;

// A (possibly negative) literal integer - used by value:.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Free-running text used for code:/name:/desc:/bindui:'s UI-entry-name
// field - letters, digits, spaces, and the punctuation that shows up in
// descriptions/UI entry names.
TEXT
        :   ('A'..'Z' | 'a'..'z' | ' ' | '_' | '.' | '0'..'9' | ',' | '(' | ')' | ';' | '[' | ']')+
        ;

// A "<ELEMENT>"/"<STAT>"-style parameterization tag suffixed onto a
// bindui: UI entry name (e.g. "<DARK>", "<STR>") - see top-of-file note #2.
TAG
        : '<' ('A'..'Z')+ '>'
        ;

// The trailing ":<aux 0|1>:<value|special>" portion of a bindui: line.
BINDUIVAL
        :   ':' ('0' | '1') ':' (('-'? ('0'..'9')) | 'special')
        ;

// POTENTIAL SOLUTIONS
//
//   1. Change `property`'s `(bindui {...})?` to `(bindui {...})*`, and
//      thread the results into a List<UIEntry>-style field on
//      PlayerProperty instead of singletons, so a future entry with two
//      bindui: lines doesn't silently lose the second binding.
//
//   2. Confirm (e.g. by checking how player_property.txt's parsed bindui
//      data is actually consumed for display) that splitting the tag out
//      before the UI-entry lookup is equivalent to whatever the C side
//      does with the combined string - if a corresponding "look up the
//      base name and substitute the tag separately" step exists on the
//      Java side too, this is fine as documented; if not, double check the
//      UI ends up pointing at the same element/stat-specific entry either
//      way.