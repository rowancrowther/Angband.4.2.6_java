// Standalone lexer grammar for the two skip-rules duplicated near-
// byte-for-byte across most of this repo's 39 grammar files in the parent
// grammars/ directory: the '#'-comment line and the blank/trailing-
// whitespace end-of-line. Unlike UCASE/LCASE (which drift per file in
// exactly which characters they allow - see scratch/
// CommonStringTokens.scratch.g4 at the repo root for that writeup),
// COMMENT and EOL show essentially zero divergence across the files that
// have them, which is why these two get a real, importable grammar file
// here while the rest of the "common string tokens" pattern stays a
// documentation-only sketch for now.
//
// Representative citations (same evidence gathered for the scratch file):
// TrapLexer.g4:37-44, PlayerRace.g4:302-309, UIEntry.g4:210-217,
// EgoItem.g4:416-423, MonsterSpell.g4:413-420, PlayerClass.g4:850-857,
// ObjectProperty.g4:277-284, Summon.g4:112-119, UIEntryBase.g4:125-132,
// Curse.g4:273-280, Monster.g4:501-508, ObjectBase.g4:165-171,
// Slay.g4:134-141, UIEntryRendererGrammar.g4:147-154. (History.g4:123-125
// and ItemObject.g4:740-742 have the same EOL shape minus the `-> skip`
// action - a small pre-existing inconsistency in those two files, noted
// but not touched here.)
//
// Not wired into any existing grammar, reader, or build script - this is
// new, standalone, illustrative infrastructure living entirely under
// restructured/, same as DiceStrings.g4/EffectBlockLexer.g4/EffectBlock.g4
// in this same package.

lexer grammar CommentsAndEol;

// Comment line: '#' to end of line, plus any blank lines right after it.
COMMENT
        :   '#' (~'\n')* '\n' -> skip
        ;

// @Author CLaudeCode
// Trailing spaces then a newline - end of a normal (non-comment) line.

// @Author Rowan Crowther
// Can be overwritten with a local token definition if a file needs a
// blank line between records.
EOL
        :   ' '* '\r'? '\n' -> skip
        ;
