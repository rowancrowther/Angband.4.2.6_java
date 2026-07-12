#!/usr/bin/env bash
#
# Copyright (c) 1987-2022 Angband contributors.
#
# This work is free software; you can redistribute it and/or modify it
# under the terms of either:
#
# a) the GNU General Public License as published by the Free Software
#    Foundation, version 2, or
#
# b) the Angband licence:
#    This software may be copied and distributed for educational, research,
#    and not for profit purposes provided that this copyright and statement
#    are included in all such copies.  Other copyrights may also apply.
#
#    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
#

# Usage (from project root): bash antlr4_gen.sh <GrammarName> <outputDir>
# Example: bash antlr4_gen.sh Activations activations
#
# Generates <GrammarName>Lexer.g4 (if present) then <GrammarName>Grammar.g4 into
# src/main/java/uk/co/jackoftrades/backend/parser/grammars/<outputDir>/
# with package uk.co.jackoftrades.backend.parser.grammars.<outputDir>

set -e

GRAMMAR_NAME="$1"
DIR_NAME="$2"

if [ -z "$GRAMMAR_NAME" ] || [ -z "$DIR_NAME" ]; then
    echo "Usage: bash antlr4_gen.sh <GrammarName> <outputDir>"
    exit 1
fi

JAVA="/d/Program Files/JetBrains/IntelliJ IDEA 2026.1/jbr/bin/java.exe"
M2=~/.m2/repository
CP="$M2/org/antlr/antlr4/4.13.2/antlr4-4.13.2.jar;\
$M2/org/antlr/antlr4-runtime/4.13.2/antlr4-runtime-4.13.2.jar;\
$M2/org/antlr/antlr-runtime/3.5.3/antlr-runtime-3.5.3.jar;\
$M2/org/antlr/ST4/4.3.4/ST4-4.3.4.jar;\
$M2/org/abego/treelayout/org.abego.treelayout.core/1.0.3/org.abego.treelayout.core-1.0.3.jar;\
$M2/com/ibm/icu/icu4j/77.1/icu4j-77.1.jar"

G=src/main/java/uk/co/jackoftrades/backend/parser/grammars
PKG="uk.co.jackoftrades.backend.parser.grammars.$DIR_NAME"
OUT="$G/$DIR_NAME"
LIB="$G/imports"

mkdir -p "$OUT"

LEXER_FILE="$G/${GRAMMAR_NAME}Lexer.g4"
PARSER_FILE="$G/${GRAMMAR_NAME}Grammar.g4"

if [ ! -f "$PARSER_FILE" ]; then
    echo "Error: $PARSER_FILE not found. Run from project root."
    exit 1
fi

if [ -f "$LEXER_FILE" ]; then
    echo "Generating ${GRAMMAR_NAME}Lexer..."
    "$JAVA" -cp "$CP" org.antlr.v4.Tool -package "$PKG" -o "$OUT" -lib "$LIB" "$LEXER_FILE"
fi

echo "Generating ${GRAMMAR_NAME}Grammar..."
"$JAVA" -cp "$CP" org.antlr.v4.Tool -package "$PKG" -o "$OUT" -lib "$LIB" "$PARSER_FILE"

# The authoritative <GrammarName>Lexer.tokens lives next to the .g4 in $G.
# ANTLR also drops a copy into the output dir — remove the duplicate.
DUPE="$OUT/${GRAMMAR_NAME}Lexer.tokens"
if [ -f "$DUPE" ]; then
    rm "$DUPE"
    echo "Removed duplicate ${GRAMMAR_NAME}Lexer.tokens from output dir."
fi

echo "Done. Output in $OUT/"
