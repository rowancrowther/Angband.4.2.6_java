# Usage (from project root): .\antlr4_gen.ps1 <GrammarName> <outputDir>
# Example: .\antlr4_gen.ps1 Activations activations
param(
    [Parameter(Mandatory=$true)][string]$GrammarName,
    [Parameter(Mandatory=$true)][string]$DirName
)

$ErrorActionPreference = "Stop"

$M2 = "$env:USERPROFILE\.m2\repository"
$CP  = "$M2\org\antlr\antlr4\4.13.2\antlr4-4.13.2.jar"
$CP += ";$M2\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar"
$CP += ";$M2\org\antlr\antlr-runtime\3.5.3\antlr-runtime-3.5.3.jar"
$CP += ";$M2\org\antlr\ST4\4.3.4\ST4-4.3.4.jar"
$CP += ";$M2\org\abego\treelayout\org.abego.treelayout.core\1.0.3\org.abego.treelayout.core-1.0.3.jar"
$CP += ";$M2\com\ibm\icu\icu4j\77.1\icu4j-77.1.jar"

$Root   = (Get-Location).Path
$G      = "$Root\src\main\java\uk\co\jackoftrades\backend\parser\grammars"
$PKG    = "uk.co.jackoftrades.backend.parser.grammars.$DirName"
$OUT    = "$G\$DirName"
$LIB    = "$G\imports"

New-Item -ItemType Directory -Force -Path $OUT | Out-Null

if (-not (Test-Path "$G\${GrammarName}.g4")) {
    Write-Error "$G\${GrammarName}.g4 not found - run from project root."
    exit 1
}

# cd to the grammars folder so ANTLR receives bare filenames and does not
# mirror the relative input path inside the output directory.
Push-Location $G

if (Test-Path "${GrammarName}Lexer.g4") {
    Write-Host "Generating ${GrammarName}Lexer..."
    & java -cp $CP org.antlr.v4.Tool -package $PKG -o $OUT -lib $LIB "${GrammarName}Lexer.g4"
    if ($LASTEXITCODE -ne 0) { Pop-Location; exit $LASTEXITCODE }
}

Write-Host "Generating ${GrammarName}..."
& java -cp $CP org.antlr.v4.Tool -package $PKG -o $OUT -lib $LIB "${GrammarName}.g4"
if ($LASTEXITCODE -ne 0) { Pop-Location; exit $LASTEXITCODE }

Pop-Location

# The authoritative <GrammarName>Lexer.tokens lives in $G next to the .g4.
# ANTLR drops a copy into the output dir too - remove the duplicate.
$Dupe = "$OUT\${GrammarName}Lexer.tokens"
if (Test-Path $Dupe) {
    Remove-Item $Dupe
    Write-Host "Removed duplicate ${GrammarName}Lexer.tokens from output dir."
}

Write-Host "Done. Output in $OUT\"
