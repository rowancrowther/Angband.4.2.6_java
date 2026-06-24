// Parser+lexer for lib/gamedata/object_base.txt - the tval-level defaults
// every object kind of that base type inherits (display colour, breakage
// chance, max stack size, base-level flags like HATES_ACID/SHOW_DICE).
// Cf. src/obj-init.c: struct file_parser object_base_parser
// (obj-init.c:657), directives "default:break-chance:"/"default:max-stack:"
// /name/graphics/break/max-stack/flags -> parse_object_base_default_*/
// _name/_graphics/_break/_max_stack/_flags.
//
// No significant problems found. Worth noting since it looks suspicious at
// a glance: `object_base` matches `(flags {...})*` and several real bases
// (arrow, bolt, bow, ...) genuinely have two flags: lines - but unlike
// TerrainFeature.g4's equivalent situation, this is NOT a bug here, because
// `$objectBase.setFlags(...)` (ObjectBase.java:52) turns flags on via
// `Flag.on()` against the base's existing flag set rather than replacing
// it, so calling it once per flags: line correctly accumulates.

grammar ObjectBase;

@header {
    import java.util.List;
    import java.util.ArrayList;

    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
}

// "default:break-chance:<value>" or "default:max-stack:<value>" - file-wide
// fallback values applied (in `file`) to any base that doesn't set its own.
default_value
        returns[int maxStackNum, int breakChanceNum]
        @init {
            $maxStackNum = -1;
            $breakChanceNum = -1;
        }
        :   DEFAULT_BREAK_CHANCE val1=NUMBER {
                $breakChanceNum = Integer.parseInt($val1.getText());
            }
        |   DEFAULT_MAX_STACK val2=NUMBER {
                $maxStackNum = Integer.parseInt($val2.getText());
            }
        ;

// "name:<tval>[:<plural name>]" - starts a new base record; tval text is
// upper-cased/underscored into a TV_* constant (with "DRAGON ARMOR"
// special-cased to TValue's "DRAG ARMOR" alias).
name
        returns[TValue tValue, String nameStr]
        :   (NAME tval1=TEXT COLON nameIn=TEXT) {
                String raw1 = $tval1.getText().toUpperCase().trim();
                // Deal with the two special flags
                if (raw1.equals("DRAGON ARMOR"))
                    raw1 = "DRAG ARMOR";
                raw1 = raw1.replace(' ', '_');
                $tValue = TValue.valueOf("TV_" + raw1);
                $nameStr = $nameIn.getText();
            }
        |   (NAME tval2=TEXT) {
                String raw2 = $tval2.getText().toUpperCase().trim();
                // Deal with the two special flags
                if (raw2.equals("DRAGON ARMOR"))
                    raw2 = "DRAG ARMOR";
                raw2 = raw2.replace(' ', '_');
                $tValue = TValue.valueOf("TV_" + raw2);
            }
        ;

// "graphics:<colour name>" - default inventory-display colour for this base.
graphics
        returns[ColourType graphicsColour]
        :   GRAPHICS TEXT {
                $graphicsColour = ColourType.getColourType($TEXT.getText());
            }
        ;

// "break:<percent>" - breakage chance when thrown.
break_chance
        returns[int breakChance]
        :   BREAK NUMBER {
                $breakChance = Integer.parseInt($NUMBER.getText());
            }
        ;

// "max-stack:<count>" - maximum stack size for items of this base.
max_stack
        returns[int maxStack]
        :   MAX_STACK NUMBER {
                $maxStack = Integer.parseInt($NUMBER.getText());
            }
        ;

// "flags:<FLAG> [| <FLAG> ...]" - base-level flags (HATES_ACID/_FIRE map to
// element-vulnerability flags, everything else to ObjectKindFlag - see
// ObjectBase.setFlags()). Can repeat per base (see top-of-file note).
flags
        returns[List<String> flagsList]
        @init {
            $flagsList = new ArrayList<>();
        }
        :   FLAGS flag1=TEXT {
                $flagsList.add($flag1.getText().trim());
            }
            ('|' flag2=TEXT {
                $flagsList.add($flag2.getText().trim());
            })*
        ;

// One full base record: name/graphics header, then optional break/max-stack
// (left at -1 if absent, to be filled from the file-wide defaults below)
// and zero or more flags: lines.
object_base
        returns[ObjectBase objectBase]
        @init {
            $objectBase = new ObjectBase();
        }
        :   name {
                $objectBase.setName($name.nameStr);
                $objectBase.settVal($name.tValue);
            }
            graphics {
                $objectBase.setAttr($graphics.graphicsColour);
            }
            (break_chance {
                $objectBase.setBreakPerc($break_chance.breakChance);
            })?
            (max_stack {
                $objectBase.setMaxStack($max_stack.maxStack);
            })?
            (flags {
                $objectBase.setFlags($flags.flagsList);
            })*
        ;

// Top-level rule: the file-wide default: lines, then one or more base
// records, applying the defaults to any base that left break/max-stack at -1.
file
        returns[List<ObjectBase> objectBaseList]
        @init {
            $objectBaseList = new ArrayList<>();
            int defaultMaxStack = 0;
            int defaultBreakChance = 0;
        }
        :   (default_value {
                if ($default_value.maxStackNum == -1)
                    defaultBreakChance = $default_value.breakChanceNum;
                else
                    defaultMaxStack = $default_value.maxStackNum;
            })+


            (object_base {
                ObjectBase base = $object_base.objectBase;
                // put in the default values if required
                if (base.getBreakPerc() == -1)
                    base.setBreakPerc(defaultBreakChance);

                if (base.getMaxStack() == -1)
                    base.setMaxStack(defaultMaxStack);

                // add it to the list
                $objectBaseList.add(base);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

DEFAULT_MAX_STACK
        :   'default:max-stack:'
        ;

DEFAULT_BREAK_CHANCE
        :   'default:break-chance:'
        ;

// NAME through FLAGS below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

GRAPHICS
        :   'graphics:'
        ;

BREAK
        :   'break:'
        ;

MAX_STACK
        :   'max-stack:'
        ;

FLAGS
        :   'flags:'
        ;

// A bare non-negative integer.
NUMBER
        :   ('0'..'9')+
        ;

// Field separator within a name: line.
COLON
        :   ':'
        ;

// Free-running text - tval/base names, colour names, and flag names
// (including the '~' plural-marker suffix used in object names like "Chest~").
TEXT
        :   ('a'..'z' | 'A'..'Z' | ' ' | '_' | '-' | '~')+
        ;