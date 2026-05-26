grammar ObjectBase;

@header {
    import java.util.List;
    import java.util.ArrayList;

    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
}

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

name
        returns[TValue tValue, String nameStr]
        :   (NAME tval1=TEXT COLON nameIn=TEXT) {
                String raw1 = $tval1.getText().toUpperCase().trim();
                // Deal with the two special flags
                if (raw1.equals("DIGGER"))
                    raw1 = "DIGGING";
                if (raw1.equals("DRAGON ARMOR"))
                    raw1 = "DRAG ARMOR";
                raw1 = raw1.replace(' ', '_');
                $tValue = TValue.valueOf("TV_" + raw1);
                $nameStr = $nameIn.getText();
            }
        |   (NAME tval2=TEXT) {
                String raw2 = $tval2.getText().toUpperCase().trim();
                // Deal with the two special flags
                if (raw2.equals("DIGGER"))
                    raw2 = "DIGGING";
                if (raw2.equals("DRAGON ARMOR"))
                    raw2 = "DRAG ARMOR";
                raw2 = raw2.replace(' ', '_');
                $tValue = TValue.valueOf("TV_" + raw2);
            }
        ;

graphics
        returns[ColourType graphicsColour]
        :   GRAPHICS TEXT {
                $graphicsColour = ColourType.getColourType($TEXT.getText());
            }
        ;

break_chance
        returns[int breakChance]
        :   BREAK NUMBER {
                $breakChance = Integer.parseInt($NUMBER.getText());
            }
        ;

max_stack
        returns[int maxStack]
        :   MAX_STACK NUMBER {
                $maxStack = Integer.parseInt($NUMBER.getText());
            }
        ;

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

NUMBER
        :   ('0'..'9')+
        ;

COLON
        :   ':'
        ;

TEXT
        :   ('a'..'z' | 'A'..'Z' | ' ' | '_' | '-' | '~')+
        ;