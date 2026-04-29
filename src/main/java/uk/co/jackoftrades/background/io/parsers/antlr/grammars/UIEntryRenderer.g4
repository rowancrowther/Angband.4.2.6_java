grammar UIEntryRenderer;

@header
        {
            import java.util.ArrayList;

            import uk.co.jackoftrades.frontend.screen.enums.UIEntryRenderer;
            import uk.co.jackoftrades.frontend.screen.enums.UIEntryEnum;
            import uk.co.jackoftrades.frontend.screen.UIEntry;
        }

name
        returns[String uiName]
        :   NAME LCASE
            {
                $uiName = $LCASE.getText();
            }
        ;

code    returns[UIEntryRenderer flag]
        :   CODE UCASE
            {
                String flagString = "UI_ENTRY_RENDERER_" + $UCASE.getText();
                $flag = UIEntryRenderer.valueOf(flagString);
            }
        ;

colours
        returns[String colourString]
        :   COLOURS COLOURCHAR
            {
                $colourString = $COLOURCHAR.getText();
            }
        ;

labelColours
        returns[String labelColourString]
        :   LABELCOLOURS COLOURCHAR
            {
                $labelColourString = $COLOURCHAR.getText();
            }
        ;

symbols
        returns[String symbolString]
        :   SYMBOLS SYMBOLCHAR
            {
                $symbolString = $SYMBOLCHAR.getText();
            }
        ;

nDigit
        returns[int numDigits]
        :   NDIGIT NUMBER
            {
                $numDigits = Integer.parseInt($NUMBER.getText());
            }
        ;

sign
        returns[UIEntryEnum entryEnum]
        :   SIGN UCASE
            {
                $entryEnum = UIEntryEnum.valueOf("UI_ENTRY_" + $UCASE.getText());
            }
        ;

entry
        returns[UIEntry uiEntry]
        :   name
            code
            colours
            labelColours
            symbols
            nDigit?
            sign?
            {
                String initName = $name.uiName;
                UIEntryRenderer initCode = $code.flag;
                String initColour = $colours.colourString;
                String initLColour = $labelColours.labelColourString;
                String initSymbols = $symbols.symbolString;
                int initNumDigits;
                if ($nDigit.ctx == null)
                    initNumDigits = 0;
                else
                    initNumDigits = $nDigit.numDigits;
                UIEntryEnum initSign;
                if ($sign.ctx == null)
                    initSign = UIEntryEnum.UI_ENTRY_SIGN_DEFAULT;
                else
                    initSign = $sign.entryEnum;

                $uiEntry = new UIEntry(initName,
                                       initCode,
                                       initColour,
                                       initLColour,
                                       initSymbols,
                                       initNumDigits,
                                       initSign);
            }
        ;

entries
        returns[ArrayList<UIEntry> allEntries]
        @init
        {
            $allEntries = new ArrayList<>();
        }
        :   (entry
            {
                $allEntries.add($entry.uiEntry);
            })+
            EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   ' '* '\r'? '\n' -> skip
        ;

NAME    :   'name:'
        ;

CODE    :   'code:'
        ;

COLOURS :   'colors:'
        ;

LABELCOLOURS
        :   'labelcolors:'
        ;

SYMBOLS :   'symbols:'
        ;

NDIGIT  :   'ndigit:'
        ;

SIGN    :   'sign:'
        ;

COLOURCHAR
        :   [dwsorgbuDWSORGBUpvtmYiTVIMzZ]+
        ;

SYMBOLCHAR
        :   [?.s*=+!^%~\u0020-]+
        ;

NUMBER  :   [0-9]+
        ;

LCASE   :   ('a'..'z'|'0'..'9'|'_')+
        ;

UCASE   :   ('A'..'Z'|'_')+
        ;