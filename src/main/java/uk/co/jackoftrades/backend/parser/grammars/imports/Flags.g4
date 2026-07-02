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
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

/*
 * @author Rowan Crowther
 *
 * Standalone lexer grammar for the Angband "flag" vocabulary - the upper-case
 * symbolic names that stand in for an enum flag value: either a single enum
 * field (e.g. a message type, projection code or blow code) or one member of a
 * Flag bitwise-enum collection (e.g. a monster race flag, object flag or
 * monster spell flag). These appear all over the gamedata after directives
 * such as flags:/flags-off:/spells:/code:/resist:/name:/msgt:.
 *
 * It has no @header instructions or java-domain-type coupling - it exports
 * only a fragment, so importing it contributes NO token types of its own.
 * Each consumer wraps FLAG_BODY in whatever token shape it needs (a default-
 * mode `FLAG : FLAG_BODY ;`, or a mode-scoped `X_FLAG : FLAG_BODY -> popMode ;`),
 * matching the pattern the DiceStrings and Colours imports use.
 *
 * Shape: a flag name always starts with an upper-case letter and may then
 * contain further upper-case letters, underscores and digits. Digits in the
 * tail are common and real - e.g. the monster flags RAND_50 / DROP_4, the
 * object-property codes DIG_1 / LIGHT_2, and the brand/slay codes ACID_3 /
 * DEMON_5. (Verified against the whole gamedata: of 12,558 flag values, every
 * one starts with a letter and 769 contain a digit.)
 *
 * Because the leading character must be a letter, FLAG_BODY can NEVER match a
 * bare run of digits, so it does not collide with an INTEGER token: a consumer
 * can declare FLAG and INTEGER in either order without a longest-match tie on
 * numeric input. (Contrast the digit-anywhere form, which would have forced
 * INTEGER to be declared first.)
 */
lexer grammar Flags;

/*
 * @author Rowan Crowther
 *
 * The body of a flag name: a leading upper-case letter followed by any mix of
 * upper-case letters, underscores and digits. Exposed as a fragment (see
 * top-of-file comment) so every grammar can reuse this exact character class in
 * its own token/mode wrapper without redeclaring it.
 */
fragment FLAG_BODY
        :   ('A'..'Z') ('A'..'Z' | '_' | '0'..'9')*
        ;
