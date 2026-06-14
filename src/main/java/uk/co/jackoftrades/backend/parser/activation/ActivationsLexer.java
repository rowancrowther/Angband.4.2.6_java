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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Activations.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.activation;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ActivationsLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, AIM = 4, LEVEL = 5, POWER = 6, EFFECT = 7, EFFECT_YX = 8,
            DICE = 9, EXPR = 10, MSG = 11, DESC = 12, NUMBER = 13, UCASE = 14, COLON = 15, EXPR_OPERATORS = 16,
            STRING = 17;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "AIM", "LEVEL", "POWER", "EFFECT", "EFFECT_YX",
                "DICE", "EXPR", "MSG", "DESC", "NUMBER", "UCASE", "COLON", "EXPR_OPERATORS",
                "STRING"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'aim:'", "'level:'", "'power:'", "'effect:'",
                "'effect-yx:'", "'dice:'", "'expr:'", "'msg:'", "'desc:'", null, null,
                "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "AIM", "LEVEL", "POWER", "EFFECT", "EFFECT_YX",
                "DICE", "EXPR", "MSG", "DESC", "NUMBER", "UCASE", "COLON", "EXPR_OPERATORS",
                "STRING"
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


    public ActivationsLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Activations.g4";
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
            "\u0004\u0000\u0011\u0097\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
                    "\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000" +
                    "\u0005\u0000&\b\u0000\n\u0000\f\u0000)\t\u0000\u0001\u0000\u0004\u0000" +
                    ",\b\u0000\u000b\u0000\f\u0000-\u0001\u0000\u0001\u0000\u0001\u0001\u0005" +
                    "\u00013\b\u0001\n\u0001\f\u00016\t\u0001\u0001\u0001\u0003\u00019\b\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\f\u0004\f\u0083\b\f\u000b\f\f\f\u0084\u0001\r\u0004\r\u0088\b\r\u000b" +
                    "\r\f\r\u0089\u0001\u000e\u0001\u000e\u0001\u000f\u0004\u000f\u008f\b\u000f" +
                    "\u000b\u000f\f\u000f\u0090\u0001\u0010\u0004\u0010\u0094\b\u0010\u000b" +
                    "\u0010\f\u0010\u0095\u0000\u0000\u0011\u0001\u0001\u0003\u0002\u0005\u0003" +
                    "\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015" +
                    "\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011\u0001" +
                    "\u0000\u0004\u0001\u0000\n\n\u0003\u000009AZ__\u0003\u0000  *+/9\b\u0000" +
                    " !$%\')+.09AZa{}}\u009e\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003" +
                    "\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007" +
                    "\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001" +
                    "\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000" +
                    "\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000" +
                    "\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000" +
                    "\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000" +
                    "\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000" +
                    "\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0001#\u0001\u0000\u0000\u0000" +
                    "\u00034\u0001\u0000\u0000\u0000\u0005>\u0001\u0000\u0000\u0000\u0007D" +
                    "\u0001\u0000\u0000\u0000\tI\u0001\u0000\u0000\u0000\u000bP\u0001\u0000" +
                    "\u0000\u0000\rW\u0001\u0000\u0000\u0000\u000f_\u0001\u0000\u0000\u0000" +
                    "\u0011j\u0001\u0000\u0000\u0000\u0013p\u0001\u0000\u0000\u0000\u0015v" +
                    "\u0001\u0000\u0000\u0000\u0017{\u0001\u0000\u0000\u0000\u0019\u0082\u0001" +
                    "\u0000\u0000\u0000\u001b\u0087\u0001\u0000\u0000\u0000\u001d\u008b\u0001" +
                    "\u0000\u0000\u0000\u001f\u008e\u0001\u0000\u0000\u0000!\u0093\u0001\u0000" +
                    "\u0000\u0000#\'\u0005#\u0000\u0000$&\b\u0000\u0000\u0000%$\u0001\u0000" +
                    "\u0000\u0000&)\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000\'(\u0001" +
                    "\u0000\u0000\u0000(+\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000" +
                    "*,\u0005\n\u0000\u0000+*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000" +
                    "-+\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000./\u0001\u0000\u0000" +
                    "\u0000/0\u0006\u0000\u0000\u00000\u0002\u0001\u0000\u0000\u000013\u0005" +
                    " \u0000\u000021\u0001\u0000\u0000\u000036\u0001\u0000\u0000\u000042\u0001" +
                    "\u0000\u0000\u000045\u0001\u0000\u0000\u000058\u0001\u0000\u0000\u0000" +
                    "64\u0001\u0000\u0000\u000079\u0005\r\u0000\u000087\u0001\u0000\u0000\u0000" +
                    "89\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:;\u0005\n\u0000\u0000" +
                    ";<\u0001\u0000\u0000\u0000<=\u0006\u0001\u0000\u0000=\u0004\u0001\u0000" +
                    "\u0000\u0000>?\u0005n\u0000\u0000?@\u0005a\u0000\u0000@A\u0005m\u0000" +
                    "\u0000AB\u0005e\u0000\u0000BC\u0005:\u0000\u0000C\u0006\u0001\u0000\u0000" +
                    "\u0000DE\u0005a\u0000\u0000EF\u0005i\u0000\u0000FG\u0005m\u0000\u0000" +
                    "GH\u0005:\u0000\u0000H\b\u0001\u0000\u0000\u0000IJ\u0005l\u0000\u0000" +
                    "JK\u0005e\u0000\u0000KL\u0005v\u0000\u0000LM\u0005e\u0000\u0000MN\u0005" +
                    "l\u0000\u0000NO\u0005:\u0000\u0000O\n\u0001\u0000\u0000\u0000PQ\u0005" +
                    "p\u0000\u0000QR\u0005o\u0000\u0000RS\u0005w\u0000\u0000ST\u0005e\u0000" +
                    "\u0000TU\u0005r\u0000\u0000UV\u0005:\u0000\u0000V\f\u0001\u0000\u0000" +
                    "\u0000WX\u0005e\u0000\u0000XY\u0005f\u0000\u0000YZ\u0005f\u0000\u0000" +
                    "Z[\u0005e\u0000\u0000[\\\u0005c\u0000\u0000\\]\u0005t\u0000\u0000]^\u0005" +
                    ":\u0000\u0000^\u000e\u0001\u0000\u0000\u0000_`\u0005e\u0000\u0000`a\u0005" +
                    "f\u0000\u0000ab\u0005f\u0000\u0000bc\u0005e\u0000\u0000cd\u0005c\u0000" +
                    "\u0000de\u0005t\u0000\u0000ef\u0005-\u0000\u0000fg\u0005y\u0000\u0000" +
                    "gh\u0005x\u0000\u0000hi\u0005:\u0000\u0000i\u0010\u0001\u0000\u0000\u0000" +
                    "jk\u0005d\u0000\u0000kl\u0005i\u0000\u0000lm\u0005c\u0000\u0000mn\u0005" +
                    "e\u0000\u0000no\u0005:\u0000\u0000o\u0012\u0001\u0000\u0000\u0000pq\u0005" +
                    "e\u0000\u0000qr\u0005x\u0000\u0000rs\u0005p\u0000\u0000st\u0005r\u0000" +
                    "\u0000tu\u0005:\u0000\u0000u\u0014\u0001\u0000\u0000\u0000vw\u0005m\u0000" +
                    "\u0000wx\u0005s\u0000\u0000xy\u0005g\u0000\u0000yz\u0005:\u0000\u0000" +
                    "z\u0016\u0001\u0000\u0000\u0000{|\u0005d\u0000\u0000|}\u0005e\u0000\u0000" +
                    "}~\u0005s\u0000\u0000~\u007f\u0005c\u0000\u0000\u007f\u0080\u0005:\u0000" +
                    "\u0000\u0080\u0018\u0001\u0000\u0000\u0000\u0081\u0083\u000209\u0000\u0082" +
                    "\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084" +
                    "\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085" +
                    "\u001a\u0001\u0000\u0000\u0000\u0086\u0088\u0007\u0001\u0000\u0000\u0087" +
                    "\u0086\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000\u0089" +
                    "\u0087\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a" +
                    "\u001c\u0001\u0000\u0000\u0000\u008b\u008c\u0005:\u0000\u0000\u008c\u001e" +
                    "\u0001\u0000\u0000\u0000\u008d\u008f\u0007\u0002\u0000\u0000\u008e\u008d" +
                    "\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090\u008e" +
                    "\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091 \u0001" +
                    "\u0000\u0000\u0000\u0092\u0094\u0007\u0003\u0000\u0000\u0093\u0092\u0001" +
                    "\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u0093\u0001" +
                    "\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\"\u0001\u0000" +
                    "\u0000\u0000\t\u0000\'-48\u0084\u0089\u0090\u0095\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
	}
}