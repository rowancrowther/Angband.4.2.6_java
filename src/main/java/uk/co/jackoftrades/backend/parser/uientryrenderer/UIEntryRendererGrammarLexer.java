/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryRendererGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.uientryrenderer;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryRendererGrammarLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, CODE = 4, COLOURS = 5, LABELCOLOURS = 6, SYMBOLS = 7,
            NDIGITS = 8, SIGN = 9, COLOURCHARS = 10, SYMBOLCHARS = 11, DIGIT = 12, LCASEWORD = 13,
            UCASEWORD = 14;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "CODE", "COLOURS", "LABELCOLOURS", "SYMBOLS",
                "NDIGITS", "SIGN", "COLOURCHARS", "SYMBOLCHARS", "DIGIT", "LCASEWORD",
                "UCASEWORD"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'code:'", "'colors:'", "'labelcolors:'",
                "'symbols:'", "'ndigit:'", "'sign:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "CODE", "COLOURS", "LABELCOLOURS", "SYMBOLS",
                "NDIGITS", "SIGN", "COLOURCHARS", "SYMBOLCHARS", "DIGIT", "LCASEWORD",
                "UCASEWORD"
        };
    }

    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }


    public UIEntryRendererGrammarLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "UIEntryRendererGrammar.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\u0004\u0000\u000e\u0086\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000" +
                    "\u0005\u0000 \b\u0000\n\u0000\f\u0000#\t\u0000\u0001\u0000\u0004\u0000" +
                    "&\b\u0000\u000b\u0000\f\u0000\'\u0001\u0000\u0001\u0000\u0001\u0001\u0005" +
                    "\u0001-\b\u0001\n\u0001\f\u00010\t\u0001\u0001\u0001\u0003\u00013\b\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0004\tr\b\t" +
                    "\u000b\t\f\ts\u0001\n\u0004\nw\b\n\u000b\n\f\nx\u0001\u000b\u0001\u000b" +
                    "\u0001\f\u0004\f~\b\f\u000b\f\f\f\u007f\u0001\r\u0004\r\u0083\b\r\u000b" +
                    "\r\f\r\u0084\u0000\u0000\u000e\u0001\u0001\u0003\u0002\u0005\u0003\u0007" +
                    "\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b" +
                    "\u0017\f\u0019\r\u001b\u000e\u0001\u0000\u0005\u0001\u0000\n\n\u0011\u0000" +
                    "BBDDGGIIMMPPRRTWYZbbddggiimmoprwyz\t\u0000 !%%*+-.==??^^ss~~\u0003\u0000" +
                    "11__az\u0002\u0000AZ__\u008d\u0000\u0001\u0001\u0000\u0000\u0000\u0000" +
                    "\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000" +
                    "\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b" +
                    "\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001" +
                    "\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001" +
                    "\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001" +
                    "\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001" +
                    "\u0000\u0000\u0000\u0001\u001d\u0001\u0000\u0000\u0000\u0003.\u0001\u0000" +
                    "\u0000\u0000\u00058\u0001\u0000\u0000\u0000\u0007>\u0001\u0000\u0000\u0000" +
                    "\tD\u0001\u0000\u0000\u0000\u000bL\u0001\u0000\u0000\u0000\rY\u0001\u0000" +
                    "\u0000\u0000\u000fb\u0001\u0000\u0000\u0000\u0011j\u0001\u0000\u0000\u0000" +
                    "\u0013q\u0001\u0000\u0000\u0000\u0015v\u0001\u0000\u0000\u0000\u0017z" +
                    "\u0001\u0000\u0000\u0000\u0019}\u0001\u0000\u0000\u0000\u001b\u0082\u0001" +
                    "\u0000\u0000\u0000\u001d!\u0005#\u0000\u0000\u001e \b\u0000\u0000\u0000" +
                    "\u001f\u001e\u0001\u0000\u0000\u0000 #\u0001\u0000\u0000\u0000!\u001f" +
                    "\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000\"%\u0001\u0000\u0000" +
                    "\u0000#!\u0001\u0000\u0000\u0000$&\u0005\n\u0000\u0000%$\u0001\u0000\u0000" +
                    "\u0000&\'\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000\'(\u0001" +
                    "\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0006\u0000\u0000\u0000" +
                    "*\u0002\u0001\u0000\u0000\u0000+-\u0005 \u0000\u0000,+\u0001\u0000\u0000" +
                    "\u0000-0\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001\u0000" +
                    "\u0000\u0000/2\u0001\u0000\u0000\u00000.\u0001\u0000\u0000\u000013\u0005" +
                    "\r\u0000\u000021\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000034\u0001" +
                    "\u0000\u0000\u000045\u0005\n\u0000\u000056\u0001\u0000\u0000\u000067\u0006" +
                    "\u0001\u0000\u00007\u0004\u0001\u0000\u0000\u000089\u0005n\u0000\u0000" +
                    "9:\u0005a\u0000\u0000:;\u0005m\u0000\u0000;<\u0005e\u0000\u0000<=\u0005" +
                    ":\u0000\u0000=\u0006\u0001\u0000\u0000\u0000>?\u0005c\u0000\u0000?@\u0005" +
                    "o\u0000\u0000@A\u0005d\u0000\u0000AB\u0005e\u0000\u0000BC\u0005:\u0000" +
                    "\u0000C\b\u0001\u0000\u0000\u0000DE\u0005c\u0000\u0000EF\u0005o\u0000" +
                    "\u0000FG\u0005l\u0000\u0000GH\u0005o\u0000\u0000HI\u0005r\u0000\u0000" +
                    "IJ\u0005s\u0000\u0000JK\u0005:\u0000\u0000K\n\u0001\u0000\u0000\u0000" +
                    "LM\u0005l\u0000\u0000MN\u0005a\u0000\u0000NO\u0005b\u0000\u0000OP\u0005" +
                    "e\u0000\u0000PQ\u0005l\u0000\u0000QR\u0005c\u0000\u0000RS\u0005o\u0000" +
                    "\u0000ST\u0005l\u0000\u0000TU\u0005o\u0000\u0000UV\u0005r\u0000\u0000" +
                    "VW\u0005s\u0000\u0000WX\u0005:\u0000\u0000X\f\u0001\u0000\u0000\u0000" +
                    "YZ\u0005s\u0000\u0000Z[\u0005y\u0000\u0000[\\\u0005m\u0000\u0000\\]\u0005" +
                    "b\u0000\u0000]^\u0005o\u0000\u0000^_\u0005l\u0000\u0000_`\u0005s\u0000" +
                    "\u0000`a\u0005:\u0000\u0000a\u000e\u0001\u0000\u0000\u0000bc\u0005n\u0000" +
                    "\u0000cd\u0005d\u0000\u0000de\u0005i\u0000\u0000ef\u0005g\u0000\u0000" +
                    "fg\u0005i\u0000\u0000gh\u0005t\u0000\u0000hi\u0005:\u0000\u0000i\u0010" +
                    "\u0001\u0000\u0000\u0000jk\u0005s\u0000\u0000kl\u0005i\u0000\u0000lm\u0005" +
                    "g\u0000\u0000mn\u0005n\u0000\u0000no\u0005:\u0000\u0000o\u0012\u0001\u0000" +
                    "\u0000\u0000pr\u0007\u0001\u0000\u0000qp\u0001\u0000\u0000\u0000rs\u0001" +
                    "\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000" +
                    "t\u0014\u0001\u0000\u0000\u0000uw\u0007\u0002\u0000\u0000vu\u0001\u0000" +
                    "\u0000\u0000wx\u0001\u0000\u0000\u0000xv\u0001\u0000\u0000\u0000xy\u0001" +
                    "\u0000\u0000\u0000y\u0016\u0001\u0000\u0000\u0000z{\u000209\u0000{\u0018" +
                    "\u0001\u0000\u0000\u0000|~\u0007\u0003\u0000\u0000}|\u0001\u0000\u0000" +
                    "\u0000~\u007f\u0001\u0000\u0000\u0000\u007f}\u0001\u0000\u0000\u0000\u007f" +
                    "\u0080\u0001\u0000\u0000\u0000\u0080\u001a\u0001\u0000\u0000\u0000\u0081" +
                    "\u0083\u0007\u0004\u0000\u0000\u0082\u0081\u0001\u0000\u0000\u0000\u0083" +
                    "\u0084\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000\u0000\u0000\u0084" +
                    "\u0085\u0001\u0000\u0000\u0000\u0085\u001c\u0001\u0000\u0000\u0000\t\u0000" +
                    "!\'.2sx\u007f\u0084\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}