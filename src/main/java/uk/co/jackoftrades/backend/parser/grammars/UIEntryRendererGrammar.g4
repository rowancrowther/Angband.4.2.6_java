grammar UIEntryRendererGrammar;

@header
        {
            import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
            import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
            import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

            import java.util.ArrayList;
            import java.util.List;
        }

name
        returns[String nameStr]
        :   NAME LCASEWORD {
                $nameStr = $LCASEWORD.getText();
            }
        ;

code
        returns[UIEntryRendererEnum codeEnum]
        :   CODE UCASEWORD {
                String flag = "UI_ENTRY_RENDERER_" + $UCASEWORD.getText();
                $codeEnum = UIEntryRendererEnum.valueOf(flag);
            }
        ;

colours
        returns[String colourCharStr]
        :   COLOURS COLOURCHARS {
                $colourCharStr = $COLOURCHARS.getText();
            }
        ;

labelcolours
        returns[String colourCharStr]
        :   LABELCOLOURS COLOURCHARS {
                $colourCharStr = $COLOURCHARS.getText();
            }
        ;

symbols
        returns[String symbolCharsStr]
        :   SYMBOLS SYMBOLCHARS {
                $symbolCharsStr = $SYMBOLCHARS.getText();
            }
        ;

ndigit
        returns[int numDigitsInt]
        :   NDIGITS DIGIT {
                $numDigitsInt = Integer.parseInt($DIGIT.getText());
            }
        ;

sign
        returns[UIEntryEnum signEnum]
        :   SIGN UCASEWORD {
                String flag = "UI_ENTRY_" + $UCASEWORD.getText();
                $signEnum = UIEntryEnum.valueOf(flag);
            }
        ;

uiEntry
        returns[UIEntryRenderer renderer]
        @init {
            String nameInit = "";
            UIEntryRendererEnum codeInit = UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE;
            String colourCharsInit = "";
            String labelColourCharsInit = "";
            String symbolCharsInit = "";
            int nDigitInit = 1;
            UIEntryEnum signInit = UIEntryEnum.UI_ENTRY_NO_SIGN;
        }
        @after {
            $renderer = new UIEntryRenderer(nameInit,
                        codeInit, colourCharsInit,
                        labelColourCharsInit, symbolCharsInit,
                        nDigitInit, signInit);
        }
        :   name
            { nameInit = $name.nameStr; }
            code
            { codeInit = $code.codeEnum; }
            colours
            { colourCharsInit = $colours.colourCharStr; }
            labelcolours
            { labelColourCharsInit = $labelcolours.colourCharStr; }
            symbols
            { symbolCharsInit = $symbols.symbolCharsStr; }
            (ndigit
            { nDigitInit = $ndigit.numDigitsInt; })?
            (sign
            { signInit = $sign.signEnum; })?
        ;

file
        returns[List<UIEntryRenderer> renderers]
        @init {
            $renderers = new ArrayList<>();
        }
        :   (uiEntry
            {
                $renderers.add($uiEntry.renderer);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

CODE
        :   'code:'
        ;

COLOURS
        :   'colors:'
        ;

LABELCOLOURS
        :   'labelcolors:'
        ;

SYMBOLS
        :   'symbols:'
        ;

NDIGITS
        :   'ndigit:'
        ;

SIGN
        :   'sign:'
        ;

COLOURCHARS
        :    ('d'|'w'|'s'|'o'|'r'|'g'|'b'|'u'|'D'|'W'|'P'|'y'|'R'|'G'|'B'|'U'|'p'|'v'|'t'|'m'|'Y'|'i'|'T'|'V'|'I'|'M'|'z'|'Z')+
        ;

SYMBOLCHARS
        :   ('?'|' '|'.'|'s'|'*'|'='|'+'|'!'|'-'|'^'|'%'|'~')+
        ;

DIGIT
        :   ('0'..'9')
        ;

LCASEWORD
        :   ('a'..'z'|'1'|'_')+
        ;

UCASEWORD
        :   ('A'..'Z'|'_')+
        ;