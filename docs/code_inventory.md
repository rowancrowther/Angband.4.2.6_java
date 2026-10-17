# Code inventory — `src/main/java`

*Generated 2026-10-14 from a real Java parse (JavaParser 3.27.1, Java 21 language level), not a regex sweep.*

## How to read this

- One section per package, one sub-section per file; paths are relative to `src/main/java/uk/co/jackoftradesltd/`.
- **Responsible for** is the first sentence of the top type's Javadoc; the 14 files with no class Javadoc (or no type at all, for `module-info.java`) have a hand-written summary.
- **Function** rows cover every method and constructor (including private and abstract ones, and those in nested types). Initialisers, lambdas and anonymous-class / enum-constant-body members are not listed.
- **Ported (coded)** is read from the house provenance stamp in the member's Javadoc (`coded on YYMMDD` = written that day; `coded before YYMMDD` = predates that day's stamping). `—` means the member carries no stamp. It is not read from `Old Docs/ported_functions.md` (that file is keyed on C functions).
- **Javadoc** ✔ = a `/** … */` block is attached to the declaration. ✘ (override) = an `@Override` method with no block of its own. A block is counted regardless of its quality — see `Old Docs/no_low_javadoc.md` for the thin-block sweep.
- Fields, enum constants and record components are shown as compact lists: ✔ documented, ✘ undocumented. Record components count as documented when the record's Javadoc has a matching `@param`.
- ANTLR-generated files (any file with a `// Generated from … by ANTLR` header) are excluded.

## Totals

| Measure | Count | Documented |
|---|---|---|
| Files | 624 | — |
| Types (class/interface/enum/record, incl. nested) | 696 | 682 with Javadoc |
| Functions (methods + constructors) | 3908 | 3862 with Javadoc |
| — of which stamped `coded on`/`coded before` | 2867 | `coded on`: 755 |
| Fields | 1906 | 1882 with Javadoc |
| Enum constants | 1901 | 1530 with Javadoc |
| Record components | 957 | 919 with `@param` |

## Packages

| Package | Files | Types | Types with Javadoc | Functions | Functions with Javadoc | Functions stamped |
|---|---|---|---|---|---|---|
| [`(source root)`](#source-root) | 2 | 2 | 2 | 7 | 7 | 0 |
| [`backend`](#backend) | 1 | 1 | 1 | 3 | 3 | 0 |
| [`backend/io`](#backendio) | 5 | 5 | 4 | 40 | 39 | 20 |
| [`backend/io/bespokeexceptions`](#backendiobespokeexceptions) | 1 | 1 | 1 | 1 | 1 | 0 |
| [`backend/io/enums`](#backendioenums) | 2 | 2 | 2 | 0 | 0 | 0 |
| [`backend/io/savefiles`](#backendiosavefiles) | 3 | 3 | 2 | 10 | 9 | 9 |
| [`backend/parser`](#backendparser) | 42 | 42 | 42 | 125 | 125 | 70 |
| [`backend/parser/activation`](#backendparseractivation) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/artifact`](#backendparserartifact) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/bloweffect`](#backendparserbloweffect) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/blowmethod`](#backendparserblowmethod) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/body`](#backendparserbody) | 2 | 3 | 3 | 1 | 1 | 1 |
| [`backend/parser/brand`](#backendparserbrand) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/chesttrap`](#backendparserchesttrap) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/curse`](#backendparsercurse) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/dungeonprofile`](#backendparserdungeonprofile) | 5 | 9 | 9 | 5 | 5 | 0 |
| [`backend/parser/egoitem`](#backendparseregoitem) | 2 | 3 | 3 | 1 | 1 | 1 |
| [`backend/parser/flavour`](#backendparserflavour) | 4 | 4 | 4 | 2 | 2 | 2 |
| [`backend/parser/gameconstants`](#backendparsergameconstants) | 2 | 2 | 2 | 24 | 24 | 0 |
| [`backend/parser/grammars`](#backendparsergrammars) | 2 | 2 | 2 | 4 | 4 | 3 |
| [`backend/parser/hint`](#backendparserhint) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/history`](#backendparserhistory) | 2 | 2 | 2 | 1 | 1 | 0 |
| [`backend/parser/itemobject`](#backendparseritemobject) | 2 | 2 | 2 | 1 | 1 | 0 |
| [`backend/parser/monster`](#backendparsermonster) | 2 | 7 | 7 | 1 | 1 | 1 |
| [`backend/parser/monsterbase`](#backendparsermonsterbase) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/monsterspell`](#backendparsermonsterspell) | 3 | 4 | 4 | 2 | 2 | 2 |
| [`backend/parser/names`](#backendparsernames) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/objectbase`](#backendparserobjectbase) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/objectproperty`](#backendparserobjectproperty) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/pain`](#backendparserpain) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/pit`](#backendparserpit) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/playerclass`](#backendparserplayerclass) | 10 | 10 | 10 | 6 | 6 | 6 |
| [`backend/parser/playerproperty`](#backendparserplayerproperty) | 2 | 2 | 2 | 2 | 2 | 2 |
| [`backend/parser/playerrace`](#backendparserplayerrace) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/playertimed`](#backendparserplayertimed) | 2 | 4 | 4 | 1 | 1 | 1 |
| [`backend/parser/projection`](#backendparserprojection) | 2 | 2 | 2 | 5 | 5 | 5 |
| [`backend/parser/quest`](#backendparserquest) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/realm`](#backendparserrealm) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/roomprofile`](#backendparserroomprofile) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/shape`](#backendparsershape) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/slay`](#backendparserslay) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/summon`](#backendparsersummon) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/terrainfeature`](#backendparserterrainfeature) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/trap`](#backendparsertrap) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/vault`](#backendparservault) | 2 | 2 | 2 | 1 | 1 | 1 |
| [`backend/parser/visuals`](#backendparservisuals) | 4 | 4 | 4 | 2 | 2 | 2 |
| [`backend/parser/world`](#backendparserworld) | 2 | 2 | 2 | 7 | 7 | 1 |
| [`channel`](#channel) | 8 | 8 | 8 | 5 | 5 | 0 |
| [`channel/colour`](#channelcolour) | 2 | 2 | 1 | 8 | 6 | 0 |
| [`channel/corechannel`](#channelcorechannel) | 2 | 2 | 2 | 4 | 4 | 0 |
| [`channel/directories`](#channeldirectories) | 1 | 2 | 2 | 7 | 7 | 0 |
| [`channel/enums`](#channelenums) | 8 | 8 | 8 | 8 | 7 | 5 |
| [`channel/globals`](#channelglobals) | 2 | 2 | 0 | 2 | 0 | 0 |
| [`channel/messages`](#channelmessages) | 3 | 11 | 10 | 0 | 0 | 0 |
| [`channel/messages/data`](#channelmessagesdata) | 22 | 22 | 22 | 83 | 63 | 62 |
| [`channel/parser`](#channelparser) | 6 | 7 | 7 | 16 | 16 | 5 |
| [`channel/strings`](#channelstrings) | 1 | 1 | 1 | 7 | 7 | 0 |
| [`channel/uichannel`](#channeluichannel) | 5 | 5 | 5 | 4 | 4 | 0 |
| [`channel/utils`](#channelutils) | 6 | 6 | 5 | 98 | 92 | 37 |
| [`channel/utils/combiners`](#channelutilscombiners) | 10 | 11 | 11 | 55 | 54 | 2 |
| [`frontend`](#frontend) | 2 | 3 | 3 | 14 | 14 | 5 |
| [`frontend/colour`](#frontendcolour) | 5 | 5 | 5 | 15 | 15 | 11 |
| [`frontend/entries`](#frontendentries) | 5 | 5 | 5 | 71 | 68 | 67 |
| [`frontend/entries/enums`](#frontendentriesenums) | 2 | 2 | 2 | 6 | 6 | 6 |
| [`frontend/events`](#frontendevents) | 3 | 3 | 3 | 9 | 9 | 0 |
| [`frontend/globals`](#frontendglobals) | 1 | 1 | 1 | 4 | 2 | 2 |
| [`frontend/inputfromuser`](#frontendinputfromuser) | 2 | 2 | 2 | 13 | 13 | 5 |
| [`frontend/screen`](#frontendscreen) | 5 | 7 | 6 | 55 | 55 | 38 |
| [`frontend/screen/enums`](#frontendscreenenums) | 2 | 2 | 2 | 0 | 0 | 0 |
| [`frontend/screen/grid`](#frontendscreengrid) | 5 | 5 | 5 | 20 | 20 | 19 |
| [`frontend/screen/handlers`](#frontendscreenhandlers) | 3 | 5 | 4 | 34 | 33 | 33 |
| [`frontend/screen/hooks`](#frontendscreenhooks) | 6 | 6 | 6 | 17 | 17 | 7 |
| [`frontend/screen/splash`](#frontendscreensplash) | 1 | 2 | 2 | 5 | 5 | 5 |
| [`frontend/sounds`](#frontendsounds) | 1 | 1 | 1 | 2 | 2 | 1 |
| [`frontend/ui`](#frontendui) | 4 | 9 | 9 | 137 | 137 | 137 |
| [`frontend/ui/entry`](#frontenduientry) | 2 | 2 | 2 | 24 | 24 | 24 |
| [`frontend/ui/entry/assembler`](#frontenduientryassembler) | 2 | 2 | 2 | 3 | 3 | 3 |
| [`frontend/ui/entry/assembler/helperfunctions`](#frontenduientryassemblerhelperfunctions) | 3 | 3 | 3 | 16 | 16 | 16 |
| [`frontend/ui/entry/reader`](#frontenduientryreader) | 1 | 1 | 1 | 3 | 3 | 3 |
| [`frontend/ui/entrybase/assembler`](#frontenduientrybaseassembler) | 2 | 2 | 2 | 2 | 2 | 2 |
| [`frontend/ui/entrybase/reader`](#frontenduientrybasereader) | 1 | 1 | 1 | 3 | 3 | 0 |
| [`frontend/ui/entryrenderer/assembler`](#frontenduientryrendererassembler) | 2 | 2 | 2 | 1 | 1 | 0 |
| [`frontend/ui/entryrenderer/reader`](#frontenduientryrendererreader) | 1 | 1 | 1 | 4 | 4 | 4 |
| [`frontend/ui/globals`](#frontenduiglobals) | 2 | 2 | 2 | 14 | 14 | 14 |
| [`frontend/ui/output`](#frontenduioutput) | 1 | 1 | 1 | 10 | 10 | 10 |
| [`frontend/ui/player`](#frontenduiplayer) | 2 | 2 | 2 | 19 | 19 | 19 |
| [`middle`](#middle) | 3 | 4 | 4 | 26 | 26 | 2 |
| [`middle/cave`](#middlecave) | 17 | 20 | 20 | 325 | 324 | 292 |
| [`middle/cave/chunkbuilders`](#middlecavechunkbuilders) | 11 | 11 | 11 | 13 | 13 | 0 |
| [`middle/cave/enums`](#middlecaveenums) | 7 | 7 | 7 | 14 | 14 | 12 |
| [`middle/cave/profiles/dungeon`](#middlecaveprofilesdungeon) | 4 | 4 | 4 | 34 | 34 | 34 |
| [`middle/cave/profiles/room`](#middlecaveprofilesroom) | 1 | 1 | 1 | 11 | 11 | 11 |
| [`middle/cave/profiles/vault`](#middlecaveprofilesvault) | 1 | 1 | 1 | 11 | 11 | 11 |
| [`middle/cave/roombuilders`](#middlecaveroombuilders) | 21 | 21 | 21 | 29 | 29 | 0 |
| [`middle/cave/store`](#middlecavestore) | 1 | 1 | 1 | 2 | 2 | 1 |
| [`middle/combat`](#middlecombat) | 3 | 3 | 3 | 20 | 20 | 16 |
| [`middle/combat/enums`](#middlecombatenums) | 3 | 3 | 3 | 0 | 0 | 0 |
| [`middle/effect`](#middleeffect) | 7 | 7 | 7 | 53 | 53 | 49 |
| [`middle/enums`](#middleenums) | 11 | 11 | 11 | 18 | 18 | 18 |
| [`middle/game`](#middlegame) | 4 | 4 | 4 | 26 | 26 | 24 |
| [`middle/game/enums`](#middlegameenums) | 4 | 4 | 4 | 3 | 3 | 0 |
| [`middle/game/event`](#middlegameevent) | 4 | 4 | 4 | 41 | 41 | 14 |
| [`middle/game/event/birthhandlers`](#middlegameeventbirthhandlers) | 1 | 1 | 1 | 4 | 4 | 0 |
| [`middle/game/event/eventhandlers`](#middlegameeventeventhandlers) | 2 | 2 | 2 | 16 | 16 | 0 |
| [`middle/game/event/projection`](#middlegameeventprojection) | 4 | 8 | 8 | 14 | 14 | 14 |
| [`middle/game/gameengine`](#middlegamegameengine) | 13 | 13 | 13 | 104 | 102 | 74 |
| [`middle/game/gameengine/argumentdata`](#middlegamegameengineargumentdata) | 7 | 7 | 7 | 18 | 18 | 18 |
| [`middle/game/globals`](#middlegameglobals) | 1 | 2 | 2 | 94 | 94 | 94 |
| [`middle/game/globals/cached`](#middlegameglobalscached) | 1 | 1 | 1 | 9 | 9 | 9 |
| [`middle/game/globals/data`](#middlegameglobalsdata) | 18 | 19 | 19 | 91 | 91 | 91 |
| [`middle/game/globals/loaders`](#middlegameglobalsloaders) | 7 | 7 | 7 | 41 | 41 | 41 |
| [`middle/game/globals/registry`](#middlegameglobalsregistry) | 10 | 12 | 12 | 151 | 151 | 127 |
| [`middle/gameinput`](#middlegameinput) | 4 | 9 | 9 | 35 | 35 | 20 |
| [`middle/magic`](#middlemagic) | 4 | 4 | 4 | 24 | 24 | 24 |
| [`middle/monsters`](#middlemonsters) | 22 | 23 | 23 | 142 | 142 | 129 |
| [`middle/monsters/enums`](#middlemonstersenums) | 13 | 13 | 13 | 16 | 16 | 16 |
| [`middle/numerics`](#middlenumerics) | 6 | 6 | 5 | 64 | 63 | 60 |
| [`middle/objects`](#middleobjects) | 35 | 40 | 39 | 653 | 651 | 531 |
| [`middle/objects/enums`](#middleobjectsenums) | 24 | 31 | 31 | 94 | 94 | 87 |
| [`middle/player`](#middleplayer) | 32 | 37 | 36 | 562 | 562 | 321 |
| [`middle/player/enums`](#middleplayerenums) | 14 | 14 | 13 | 24 | 24 | 16 |
| [`middle/strings`](#middlestrings) | 3 | 3 | 3 | 23 | 23 | 11 |
| [`middle/utils`](#middleutils) | 2 | 2 | 2 | 14 | 14 | 8 |
| [`middle/utils/quit`](#middleutilsquit) | 2 | 3 | 3 | 3 | 3 | 0 |
| [`unused`](#unused) | 3 | 3 | 2 | 13 | 13 | 0 |

## (source root)

##### `module-info.java`

**Responsible for:** The Java module descriptor: declares module `uk.co.jackoftradesltd`, the libraries it requires (log4j, ANTLR runtime, JNA, commons, Swing) and the two packages it exports.

##### `Main.java`

**Responsible for:** The entry point: parse the command line, then either answer it and stop, or build the two halves of the game, start them, and wait for both to finish.

##### `AngbandModule.java`

**Responsible for:** Common lifecycle contract for the game's pluggable subsystems (quark tables, data stores, etc.).

##### `AngDir.java`

**Responsible for:** A thin wrapper around a single filesystem directory handle, opened once and read one entry at a time.

##### `AngFile.java`

**Responsible for:** High-level handle for a single game file, pairing a fixed access FileModeEnum with the lower-level FileHandler that performs the actual stream I/O.

##### `AngPath.java`

**Responsible for:** This is a stem class

##### `Datafile.java`

**Responsible for:** Stub home for data-file helpers; currently only a no-op `deactivateRandartFile` (C's randart-file switch-off), awaiting its real port.

##### `FileHandler.java`

**Responsible for:** The interface class between the file system and Angband.

## backend/io/bespokeexceptions

##### `InvalidTokenFoundDuringParse.java`

**Responsible for:** Unchecked exception thrown when one of the data-file parsers encounters a token it cannot interpret.

## backend/io/enums

##### `FileModeEnum.java`

**Responsible for:** The type of access required for a file as used in fileOpen()

##### `FileTypeEnum.java`

**Responsible for:** Specifies what type a file is when writing.

## backend/io/savefiles

##### `Savefile.java`

**Responsible for:** Stub home for save-file helpers; currently only `savefileGetDescription`, which returns an empty string until save files are ported.

**class `Savefile`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `savefileGetDescription(Path)` | — | ✘ |


##### `SavefileDetails.java`

**Responsible for:** Holds the information that can be read back from a SavefileGetterImpl for one savefile found while enumerating the savefile directory.

**class `SavefileDetails`** — class Javadoc: ✔ · type stamp: before 260915

- Fields (3/3 documented): ✔ `fileName`, `description`, `offset`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `SavefileDetails()` | before 260915 | ✔ |
| `getFileName()` | before 260915 | ✔ |
| `setFileName(String)` | before 260915 | ✔ |
| `getDescription()` | before 260915 | ✔ |
| `setDescription(String)` | before 260915 | ✔ |
| `getOffset()` | before 260915 | ✔ |
| `setOffset(int)` | before 260915 | ✔ |


##### `SavefileGetterImpl.java`

**Responsible for:** Enumerates the savefiles available to the current player, one call to #gotSavefile() at a time.

**class `SavefileGetterImpl`** — class Javadoc: ✔ · type stamp: on 260915

- Fields (5/6 documented): ✔ `directory`, `details`, `haveDetails`, `haveSaveDir`, `directoryExhausted` — ✘ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `SavefileGetterImpl()` | on 260915 | ✔ |
| `gotSavefile()` | on 260915 | ✔ |


## backend/parser

##### `ActivationReader.java`

**Responsible for:** Loads the relevant data-file entries into Activation objects by driving the matching ANTLR-generated lexer/parser.

**class `ActivationReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(ActivationsGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResult(String)` | before 260915 | ✔ |


##### `ArtifactReader.java`

**Responsible for:** Loads the relevant data-file entries into the corresponding data objects objects by driving the matching ANTLR-generated lexer/parser.

**class `ArtifactReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(ArtifactGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `BlowEffectReader.java`

**Responsible for:** Loads the relevant data-file entries into BlowEffect objects by driving the matching ANTLR-generated lexer/parser.

**class `BlowEffectReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(BlowEffectGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `BlowMethodReader.java`

**Responsible for:** Loads the relevant data-file entries into BlowMethod objects by driving the matching ANTLR-generated lexer/parser.

**class `BlowMethodReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(BlowMethodGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `BodyReader.java`

**Responsible for:** Loads the relevant data-file entries into PlayerBody objects by driving the matching ANTLR-generated lexer/parser.

**class `BodyReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(BodyGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `BrandReader.java`

**Responsible for:** Loads brand.txt into Brand objects, driving the ANTLR-generated BrandLexer/BrandGrammar through the shared GrammarDriver pipeline: lex and parse to BrandParseRecords, which BrandAssembler then resolves into domain brands.

**class `BrandReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(BrandGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `ChestTrapReader.java`

**Responsible for:** Reader for lib/gamedata/chest_trap.txt, producing one ChestTrap per record (the "locked" no-trap entry, gas traps, poison needles, summoning runes, the explosion device).

**class `ChestTrapReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `extract(ChestTrapGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parse(String)` | before 260915 | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `CurseReader.java`

**Responsible for:** Loads the relevant data-file entries into Curse objects by driving the matching ANTLR-generated lexer/parser.

**class `CurseReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(CurseGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `DungeonProfileReader.java`

**Responsible for:** Reads lib/gamedata/dungeon_profile.txt into CaveProfiles.

**class `DungeonProfileReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(DungeonProfileGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `EgoItemReader.java`

**Responsible for:** Loads the relevant data-file entries into EgoItem objects by driving the matching ANTLR-generated lexer/parser.

**class `EgoItemReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(EgoItemsGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `FlavourReader.java`

**Responsible for:** Reads flavor.txt into a list of FlavourKinds, wiring the FlavourLexer/FlavourGrammar to the FlavourKindAssembler through the shared GrammarDriver.

**class `FlavourReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(FlavourGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `GameConstantsParseResult.java`

**Responsible for:** Holder class for the results from the Game Constants Parse

**class `GameConstantsParseResult`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `data`, `errors`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `GameConstantsParseResult(GameConstantsData, List<String>)` | — | ✔ |
| `hasErrors()` | — | ✔ |
| `getData()` | — | ✔ |
| `getErrors()` | — | ✔ |


##### `GameConstantsReader.java`

**Responsible for:** Class to read in constants.txt file and pass back to the GameConstants object.

**class `GameConstantsReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `HintReader.java`

**Responsible for:** Reads hints.txt into a list of Hints.

**class `HintReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(HintGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `HistoryReader.java`

**Responsible for:** Loads the relevant data-file entries into PlayerHistoryChart objects by driving the matching ANTLR-generated lexer/parser.

**class `HistoryReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(HistoryGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `ItemObjectReader.java`

**Responsible for:** Loads the relevant data-file entries into ObjectKind objects by driving the matching ANTLR-generated lexer/parser.

**class `ItemObjectReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(ItemObjectGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `LoreReader.java`

**Responsible for:** Loads the relevant data-file entries into MonsterLore objects by driving the matching ANTLR-generated lexer/parser.

**class `LoreReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |


##### `MonsterBaseReader.java`

**Responsible for:** Loads the relevant data-file entries into MonsterBase objects by driving the matching ANTLR-generated lexer/parser.

**class `MonsterBaseReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(MonsterBaseGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `MonsterReader.java`

**Responsible for:** Loads the relevant data-file entries into MonsterRace objects by driving the matching ANTLR-generated lexer/parser.

**class `MonsterReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(MonsterGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `MonsterSpellReader.java`

**Responsible for:** Loads the relevant data-file entries into MonsterSpellType objects by driving the matching ANTLR-generated lexer/parser.

**class `MonsterSpellReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(MonsterSpellGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `NamesReader.java`

**Responsible for:** Reads names.txt — the random-name fragment lists used to generate monster and artifact names — into Name records via the Reader contract, for assembly by NamesAssembler.

**class `NamesReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(NamesGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `ObjectBaseReader.java`

**Responsible for:** Loads the relevant data-file entries into ObjectBase objects by driving the matching ANTLR-generated lexer/parser.

**class `ObjectBaseReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(ObjectBaseGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `ObjectPropertyReader.java`

**Responsible for:** Loads the relevant data-file entries into ObjectProperty objects by driving the matching ANTLR-generated lexer/parser.

**class `ObjectPropertyReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(ObjectPropertyGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `PainReader.java`

**Responsible for:** Loads lib/gamedata/pain.txt into MonsterPain objects - the monster "pain message" sets keyed by the index a monster base references.

**class `PainReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(PainGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `PitReader.java`

**Responsible for:** Loads the relevant data-file entries into PitProfile objects by driving the matching ANTLR-generated lexer/parser.

**class `PitReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(PitGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `PlayerClassReader.java`

**Responsible for:** Loads the relevant data-file entries into PlayerClass objects by driving the matching ANTLR-generated lexer/parser.

**class `PlayerClassReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(PlayerClassGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `PlayerPropertyReader.java`

**Responsible for:** Loads player_property.txt into PlayerProperty objects by driving the matching ANTLR-generated lexer/parser.

**class `PlayerPropertyReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(PlayerPropertyGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `PlayerRaceReader.java`

**Responsible for:** Loads the relevant data-file entries into PlayerRace objects by driving the matching ANTLR-generated lexer/parser.

**class `PlayerRaceReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(PlayerRaceGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `PlayerTimedReader.java`

**Responsible for:** Loads the relevant data-file entries into PlayerTimedEffect objects by driving the matching ANTLR-generated lexer/parser.

**class `PlayerTimedReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(PlayerTimedGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `ProjectionReader.java`

**Responsible for:** Loads the relevant data-file entries into Projection objects by driving the matching ANTLR-generated lexer/parser.

**class `ProjectionReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(ProjectionGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


##### `QuestReader.java`

**Responsible for:** Reads quest.txt into a list of Quests.

**class `QuestReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(QuestGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `RandomReader.java`

**Responsible for:** Loads the relevant data-file entries into Random objects by driving the matching ANTLR-generated lexer/parser.

**class `RandomReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |


##### `RealmReader.java`

**Responsible for:** Loads the relevant data-file entries into MagicRealm objects by driving the matching ANTLR-generated lexer/parser.

**class `RealmReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(RealmGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `RoomProfileReader.java`

**Responsible for:** Reads room_template.txt into a list of RoomTemplates.

**class `RoomProfileReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(RoomProfileGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `ShapeReader.java`

**Responsible for:** Loads the relevant data-file entries into PlayerShape objects by driving the matching ANTLR-generated lexer/parser.

**class `ShapeReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(ShapeGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `SlayReader.java`

**Responsible for:** Loads slay.txt into Slay objects, driving the ANTLR-generated SlayLexer/SlayGrammar through the shared GrammarDriver pipeline: lex and parse to SlayParseRecords, which SlayAssembler then resolves into domain slays.

**class `SlayReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(SlayGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `SummonReader.java`

**Responsible for:** Loads the relevant data-file entries into Summon objects by driving the matching ANTLR-generated lexer/parser.

**class `SummonReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(SummonGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `TerrainReader.java`

**Responsible for:** Loads lib/gamedata/terrain.txt into Feature objects, the Java port of the C feat_parser in src/init.c.

**class `TerrainReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(TerrainFeatureGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `TrapReader.java`

**Responsible for:** Reader for lib/gamedata/trap.txt, producing one TrapKind per record (traps, runes/glyphs, decoys, door locks, …).

**class `TrapReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(TrapGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `VaultReader.java`

**Responsible for:** Reads vault.txt into a list of Vaults.

**class `VaultReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(VaultGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |


##### `VisualsReader.java`

**Responsible for:** Loads visuals.txt into its two colour-cycling tables by driving the ANTLR-generated VisualsLexer/VisualsGrammar.

**class `VisualsReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parseCycler(String)` | — | ✔ |
| `parseFlicker(String)` | — | ✔ |
| `extractCycles(VisualsGrammar, ParseErrors, List<String>)` | — | ✔ |
| `extractFlickers(VisualsGrammar, ParseErrors, List<String>)` | — | ✔ |
| `parseCyclerWithResults(String)` | — | ✔ |
| `parseFlickerWithResults(String)` | — | ✔ |


##### `WorldReader.java`

**Responsible for:** Loads the data-file lib/gamedata/world.txt entries into the corresponding data objects by driving the matching ANTLR-generated lexer/parser.

**class `WorldReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | before 260915 | ✔ |
| `extract(WorldGrammar, ParseErrors, List<String>)` | before 260915 | ✔ |
| `parseWithResults(String)` | before 260915 | ✔ |


## backend/parser/activation

##### `ActivationAssembler.java`

**Responsible for:** Turns the raw ActivationParseRecords produced by the grammar into resolved domain Activations (Java port of parse_activation_* in src/obj-init.c).

**class `ActivationAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ActivationParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ActivationParseRecord.java`

**Responsible for:** The raw, still-textual result of parsing a single activation.txt block, as produced by the grammar and handed to ActivationAssembler for resolution into a domain uk.co.jackoftradesltd.middle.Activation.

**record `ActivationParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `name`, `aim`, `level`, `power`, `message`, `desc`, `effects`, `line`


## backend/parser/artifact

##### `ArtifactAssembler.java`

**Responsible for:** Assembles the raw ArtifactParseRecords parsed from artifact.txt into the finished Artifact domain objects, resolving names, base kinds, curses and element info as it goes (reader &rarr; ParseRecord &rarr; assembler &rarr; domain).

**class `ArtifactAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ArtifactParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ArtifactParseRecord.java`

**Responsible for:** Immutable extraction record for one artifact.txt entry: the raw, still-unresolved fields parsed by the grammar, later turned into the Artifact domain type by ArtifactAssembler.

**record `ArtifactParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (26/26 documented): ✔ `name`, `tValue`, `sValue`, `glyph`, `colour`, `level`, `weight`, `cost`, `commonness`, `min`, `max`, `baseDamage`, `toh`, `tod`, `baseAC`, `toa`, `flagList`, `activation`, `time`, `msg`, `values`, `brand`, `slay`, `curse`, `desc`, `line`


## backend/parser/bloweffect

##### `BlowEffectAssembler.java`

**Responsible for:** Turns the raw BlowEffectParseRecords produced by the blow_effects.txt grammar into domain BlowEffects, resolving every string the data file carries into the enum or object it names.

**class `BlowEffectAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<BlowEffectParseRecord>, List<String>)` | before 260915 | ✔ |


##### `BlowEffectParseRecord.java`

**Responsible for:** One blow_effects.txt entry exactly as the grammar read it, before any of it has been resolved into enums or domain objects.

**record `BlowEffectParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (11/11 documented): ✔ `name`, `power`, `eval`, `desc`, `loreColourBase`, `loreColourResist`, `loreColourImmune`, `effectType`, `resist`, `lashType`, `line`


## backend/parser/blowmethod

##### `BlowMethodAssembler.java`

**Responsible for:** Turns the raw BlowMethodParseRecords the grammar produced into domain BlowMethod objects - the interpretation half of the blow_methods.txt pipeline, where the deliberately unconverted token text finally becomes booleans and a MessageType.

**class `BlowMethodAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<BlowMethodParseRecord>, List<String>)` | before 260915 | ✔ |


##### `BlowMethodParseRecord.java`

**Responsible for:** One raw blow_methods.txt record, exactly as the grammar found it - the inert hand-off between BlowMethodGrammar and BlowMethodAssembler.

**record `BlowMethodParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (9/9 documented): ✔ `name`, `cut`, `stun`, `miss`, `phys`, `msg`, `act`, `desc`, `line`


## backend/parser/body

##### `BodyAssembler.java`

**Responsible for:** Turns each BodyParseRecord parsed from body.txt into a PlayerBody — a named, ordered set of equipment slots.

**class `BodyAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<BodyParseRecord>, List<String>)` | before 260915 | ✔ |


##### `BodyParseRecord.java`

**Responsible for:** Immutable extraction record for one body.txt entry: the raw, still-unresolved fields (body name plus its ordered equip slots) parsed by the grammar, later turned into the PlayerBody domain type by BodyAssembler.

**record `BodyParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `bodyName`, `slots`, `line`

**record `BodyParseRecord.BodySlotRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `slotType`, `slotName`


## backend/parser/brand

##### `BrandAssembler.java`

**Responsible for:** Turns the raw BrandParseRecords from BrandGrammar into domain Brand objects, resolving the fields the grammar left as strings: the resist and vulnerability monster race flags, and the numeric multipliers and power.

**class `BrandAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<BrandParseRecord>, List<String>)` | before 260915 | ✔ |


##### `BrandParseRecord.java`

**Responsible for:** The raw parse record for one entry in brand.txt - an unresolved snapshot of the directive values produced by BrandGrammar and consumed by BrandAssembler.

**record `BrandParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (9/9 documented): ✔ `code`, `name`, `multiplier`, `oMultiplier`, `power`, `verb`, `resistFlag`, `vulnFlag`, `line`


## backend/parser/chesttrap

##### `ChestTrapAssembler.java`

**Responsible for:** Turns the raw ChestTrapParseRecords produced by ChestTrapGrammar into domain ChestTrap objects.

**class `ChestTrapAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ChestTrapParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ChestTrapParseRecord.java`

**Responsible for:** Immutable extraction record for one chest_trap.txt record, as captured by ChestTrapGrammar: the raw, still-unresolved fields, later turned into a uk.co.jackoftradesltd.middle.objects.ChestTrap by ChestTrapAssembler.

**record `ChestTrapParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (9/9 documented): ✔ `name`, `code`, `level`, `effect`, `destroy`, `magic`, `msg`, `msgDeath`, `line`


## backend/parser/curse

##### `CurseAssembler.java`

**Responsible for:** Turns the raw CurseParseRecords produced by the grammar into fully resolved Curse domain objects — the assembler half of the curse loader (Java port of parse_curse_* in src/obj-init.c).

**class `CurseAssembler`** — class Javadoc: ✔ · type stamp: before 260915

- Fields (1/1 documented): ✔ `MAX_CURSES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<CurseParseRecord>, List<String>)` | before 260915 | ✔ |


##### `CurseParseRecord.java`

**Responsible for:** Immutable extraction record for one curse.txt entry: the raw, still-unresolved fields parsed by the grammar, later turned into the Curse domain type by CurseAssembler.

**record `CurseParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (15/15 documented): ✔ `name`, `type`, `weightAdjustment`, `combatToH`, `combatToD`, `combatToA`, `effects`, `flags`, `values`, `message`, `desc`, `conflict`, `cFlag`, `time`, `line`


## backend/parser/dungeonprofile

##### `DungeonProfileAssembler.java`

**Responsible for:** Turns parsed dungeon_profile.txt records into the CaveProfiles the level generator selects between.

**class `DungeonProfileAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<DungeonProfileParseRecord>, List<String>)` | — | ✔ |


##### `DungeonProfileParseRecord.java`

**Responsible for:** One profile record as it comes off the parser, before any of it means anything.

**record `DungeonProfileParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `profileName`, `params`, `tunnel`, `streamer`, `alloc`, `minLevel`, `rooms`, `lineNo`

**record `DungeonProfileParseRecord.Params`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `blockSize`, `rooms`, `unusual`, `rarity`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toString()` | — | ✔ |

**record `DungeonProfileParseRecord.Tunnel`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `randomChance`, `directionChangeChance`, `concludeChance`, `doorAtRoomEntranceChance`, `junctionChance`, `lineNo`

**record `DungeonProfileParseRecord.Streamer`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (7/7 documented): ✔ `density`, `range`, `magmaStreamersPerLevel`, `magmaTreasureChance`, `quartzStreamersPerLevel`, `quartzTreasureChance`, `lineNo`

**record `DungeonProfileParseRecord.Room`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (9/9 documented): ✔ `roomName`, `rating`, `height`, `width`, `level`, `pit`, `rarity`, `cutoff`, `lineNo`


##### `RoomAssembler.java`

**Responsible for:** Turns the room: lines of one profile into RoomProfiles.

**class `RoomAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<DungeonProfileParseRecord.Room>, List<String>)` | — | ✔ |


##### `StreamerAssembler.java`

**Responsible for:** Turns one profile's streamer: line into a StreamerProfile.

**class `StreamerAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<DungeonProfileParseRecord.Streamer>, List<String>)` | — | ✔ |


##### `TunnelAssembler.java`

**Responsible for:** Turns one profile's tunnel: line into a TunnelProfile.

**class `TunnelAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<DungeonProfileParseRecord.Tunnel>, List<String>)` | — | ✔ |


## backend/parser/egoitem

##### `EgoItemAssembler.java`

**Responsible for:** Assembles the raw EgoItemParseRecords parsed from ego_item.txt into the finished EgoItem domain objects (reader &rarr; ParseRecord &rarr; assembler &rarr; domain).

**class `EgoItemAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<EgoItemParseRecord>, List<String>)` | before 260915 | ✔ |


##### `EgoItemParseRecord.java`

**Responsible for:** Immutable extraction record for one ego_item.txt entry: the raw, still-unresolved fields parsed by the grammar, later turned into the EgoItem domain type by EgoItemAssembler.

**record `EgoItemParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (25/25 documented): ✔ `name`, `cost`, `rating`, `commonness`, `lower`, `upper`, `toH`, `toD`, `toA`, `minToH`, `minToD`, `minToA`, `tVals`, `itemRefs`, `flags`, `flagsOff`, `values`, `minValues`, `act`, `timeout`, `brands`, `slays`, `curses`, `desc`, `line`

**record `EgoItemParseRecord.ItemRef`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `tVal`, `sVal`


## backend/parser/flavour

##### `FlavourAssembler.java`

**Responsible for:** Turns the raw FlavourParseRecords of one kind: block into Flavour domain objects.

**class `FlavourAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<FlavourParseRecord>, List<String>)` | before 260915 | ✔ |


##### `FlavourKindAssembler.java`

**Responsible for:** Turns the raw FlavourKindParseRecords of flavor.txt into FlavourKind domain objects.

**class `FlavourKindAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<FlavourKindParseRecord>, List<String>)` | before 260915 | ✔ |


##### `FlavourKindParseRecord.java`

**Responsible for:** The raw form of one kind: block from flavor.txt: an object type, the glyph its flavours draw with, and the run of flavor:/fixed: lines that follow it.

**record `FlavourKindParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `tVal`, `glyph`, `records`, `line`


##### `FlavourParseRecord.java`

**Responsible for:** The raw, unresolved form of one flavor: or fixed: line from flavor.txt, as produced by FlavourGrammar.

**record `FlavourParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `index`, `sVal`, `attr`, `text`, `line`


## backend/parser/gameconstants

##### `GameConstantsAssembler.java`

**Responsible for:** 'Assembles' the final data from the list of GameConstantsParseRecords, and returns a GameConstantsData record to pass back to the calling GameConstants class.

**class `GameConstantsAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<GameConstantsParseRecord>, List<String>)` | — | ✔ |
| `dispatch(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `oRangedCriticalLevel(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `oRangedCritical(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `oMeleeCriticalLevel(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `oMeleeCritical(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `rangedCriticalLevel(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `rangedCritical(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `meleeCriticalLevel(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `meleeCritical(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `player(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `objGen(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `store(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `carryCap(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `world(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `dunGen(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `monPlay(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `monGen(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `levelMax(GameConstantsParseRecord, GameConstantsData.GameConstantsBuilder, List<String>)` | — | ✔ |
| `coerceInt(String, int, List<String>)` | — | ✔ |


##### `GameConstantsParseRecord.java`

**Responsible for:** Thin parse-DTO produced by the GameConstantsGrammar grammar for a single category/field values in lib/gamedata/constants.txt.

**class `GameConstantsParseRecord`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `lineNumber`, `category`, `fields`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `GameConstantsParseRecord(String, List<String>, int)` | — | ✔ |
| `getLineNumber()` | — | ✔ |
| `getCategory()` | — | ✔ |
| `getFields()` | — | ✔ |


## backend/parser/grammars

##### `EffectAssembler.java`

**Responsible for:** Resolves the raw EffectParseRecords captured from an effect: block into domain Effects.

**class `EffectAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<EffectParseRecord>, List<String>)` | — | ✔ |
| `assembleOne(EffectParseRecord, List<String>, int)` | before 260915 | ✔ |
| `getExpressions(String, String, String, int, List<String>)` | before 260915 | ✔ |
| `getWrapperSubType(EffectSubTypeEnum, String, List<String>, int)` | before 260915 | ✔ |


##### `EffectParseRecord.java`

**Responsible for:** Immutable extraction record for one parsed effect: block (the shared effect/dice grammar reused across many data files): the raw, still-unresolved fields, later turned into the Effect domain type by EffectAssembler.

**record `EffectParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (12/12 documented): ✔ `typeInit`, `subTypeWrapper`, `radius`, `other`, `diceString`, `yVal`, `xVal`, `expressionChars`, `expressionBases`, `expressionOperations`, `effectMessage`, `line`


## backend/parser/hint

##### `HintAssembler.java`

**Responsible for:** Turns the raw HintParseRecords from hints.txt into domain Hints.

**class `HintAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<HintParseRecord>, List<String>)` | before 260915 | ✔ |


##### `HintParseRecord.java`

**Responsible for:** The raw form of a single hints.txt record - one H: line - produced by HintGrammar and consumed by HintAssembler.

**record `HintParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `hint`, `line`


## backend/parser/history

##### `HistoryAssembler.java`

**Responsible for:** Turns the flat stream of HistoryParseRecords parsed from history.txt into the PlayerHistoryChart graph the game walks when generating a character's background.

**class `HistoryAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<HistoryParseRecord>, List<String>)` | — | ✔ |


##### `HistoryParseRecord.java`

**Responsible for:** Immutable extraction record for one history.txt entry: the raw, still-unresolved fields of a background-history chart link, later turned into the PlayerHistoryChart domain type by HistoryAssembler.

**record `HistoryParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `currentChart`, `nextChart`, `percentage`, `phrase`, `line`


## backend/parser/itemobject

##### `ItemObjectAssembler.java`

**Responsible for:** Turns the raw ItemObjectParseRecords from ItemObjectGrammar into domain ObjectKinds — C's struct object_kind, the item template loaded from object.txt (Java port of parse_object_* in obj-init.c).

**class `ItemObjectAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ItemObjectParseRecord>, List<String>)` | — | ✔ |


##### `ItemObjectParseRecord.java`

**Responsible for:** The raw, un-interpreted capture of one object.txt record, produced by ItemObjectGrammar and consumed by ItemObjectAssembler.

**record `ItemObjectParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (31/31 documented): ✔ `name`, `glyph`, `colour`, `tValue`, `level`, `weight`, `cost`, `attackBaseDamage`, `attackToh`, `attackTod`, `armourBaseAC`, `armourToa`, `allocCommon`, `allocLower`, `allocUpper`, `charges`, `pileChance`, `pileNumber`, `power`, `message`, `visMessage`, `effects`, `flags`, `values`, `brand`, `slay`, `curse`, `pVal`, `desc`, `time`, `line`


## backend/parser/monster

##### `MonsterAssembler.java`

**Responsible for:** Turns the raw MonsterParseRecords parsed from monster.txt into fully-formed MonsterRace domain objects.

**class `MonsterAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<MonsterParseRecord>, List<String>)` | before 260915 | ✔ |


##### `MonsterParseRecord.java`

**Responsible for:** The raw, unresolved result of parsing one monster.txt record: every field is captured as the plain text the grammar read, and cross-references (base, blows, drops, mimics, friends, shapes) are left as names or strings for the MonsterAssembler to resolve into domain objects.

**record `MonsterParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (35/35 documented): ✔ `name`, `plural`, `base`, `glyph`, `colour`, `speed`, `hitPoints`, `light`, `hearing`, `smell`, `shape`, `colourCycleGroup`, `colourCycleName`, `armourClass`, `sleepiness`, `depth`, `rarity`, `experience`, `blows`, `flagsOn`, `flagsOff`, `innateFreq`, `spellFreq`, `spellPower`, `spells`, `messageVis`, `messageInvis`, `messageMiss`, `desc`, `drops`, `dropsBase`, `mimics`, `friends`, `friendsBase`, `line`

**record `MonsterParseRecord.MonsterMimicParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `tVal`, `sVal`

**record `MonsterParseRecord.MonsterBlowParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `method`, `effect`, `damage`

**record `MonsterParseRecord.MonsterDropParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `type`, `name`, `chance`, `min`, `max`

**record `MonsterParseRecord.MonsterDropBaseParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `type`, `chance`, `min`, `max`

**record `MonsterParseRecord.MonsterFriendsParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `chance`, `number`, `name`, `role`


## backend/parser/monsterbase

##### `MonsterBaseAssembler.java`

**Responsible for:** Assembles the raw MonsterBaseParseRecords parsed from monster_base.txt into the finished MonsterBase domain objects (reader &rarr; ParseRecord &rarr; assembler &rarr; domain).

**class `MonsterBaseAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<MonsterBaseParseRecord>, List<String>)` | before 260915 | ✔ |


##### `MonsterBaseParseRecord.java`

**Responsible for:** Immutable extraction record for one monster_base.txt entry: the raw, still-unresolved fields parsed by the grammar, later turned into the MonsterBase domain type by MonsterBaseAssembler.

**record `MonsterBaseParseRecord`** — class Javadoc: ✔ · type stamp: before 261010

- Record components (via `@param`) (7/7 documented): ✔ `codeName`, `inGameName`, `flags`, `glyph`, `pain`, `description`, `line`


## backend/parser/monsterspell

##### `MonsterSpellAssembler.java`

**Responsible for:** Turns the raw MonsterSpellParseRecords the grammar produced into domain MonsterSpellType objects - the interpretation half of the monster_spell.txt pipeline, where deliberately unconverted token text becomes enums, ints and effects.

**class `MonsterSpellAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<MonsterSpellParseRecord>, List<String>)` | before 260915 | ✔ |


##### `MonsterSpellLevelAssembler.java`

**Responsible for:** Turns the per-power blocks of a monster spell into domain MonsterSpellLevel objects.

**class `MonsterSpellLevelAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<MonsterSpellParseRecord.MonsterSpellLevelParseRecord>, List<String>)` | before 260915 | ✔ |


##### `MonsterSpellParseRecord.java`

**Responsible for:** One name: block of monster_spell.txt, exactly as the grammar read it.

**record `MonsterSpellParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `name`, `messageType`, `hit`, `effects`, `levels`, `line`

**record `MonsterSpellParseRecord.MonsterSpellLevelParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (10/10 documented): ✔ `power`, `loreDesc`, `loreBaseColour`, `loreResistColour`, `loreImmuneColour`, `visMessage`, `invisMessage`, `missMessage`, `saveMessage`, `pcLine`


## backend/parser/names

##### `NamesAssembler.java`

**Responsible for:** Assembles the raw NamesParseRecords parsed from names.txt into the finished Name domain objects — the random-name fragment lists (reader &rarr; ParseRecord &rarr; assembler &rarr; domain).

**class `NamesAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<NamesParseRecord>, List<String>)` | before 260915 | ✔ |


##### `NamesParseRecord.java`

**Responsible for:** Immutable extraction record for one names.txt section: a random-name fragment list, later turned into the Name domain type by NamesAssembler.

**record `NamesParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `section`, `word`


## backend/parser/objectbase

##### `ObjectBaseAssembler.java`

**Responsible for:** Assembles the raw ObjectBaseParseRecords parsed from object_base.txt into the finished ObjectBase domain objects (reader &rarr; ParseRecord &rarr; assembler &rarr; domain).

**class `ObjectBaseAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ObjectBaseParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ObjectBaseParseRecord.java`

**Responsible for:** Immutable extraction record for one object_base.txt entry: the raw, still-unresolved fields parsed by the grammar, later turned into the ObjectBase domain type by ObjectBaseAssembler.

**record `ObjectBaseParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (7/7 documented): ✔ `name`, `tVal`, `colour`, `flags`, `breakChance`, `maxStack`, `line`


## backend/parser/objectproperty

##### `ObjectPropertyAssembler.java`

**Responsible for:** Turns the raw ObjectPropertyParseRecords from ObjectPropertyGrammar into resolved ObjectProperty domain objects — the type-checking half of the object-property pipeline (reader → ParseRecord → Assembler), and the Java port of C's parse_object_property_* directive handlers (obj-init.c).

**class `ObjectPropertyAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ObjectPropertyParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ObjectPropertyParseRecord.java`

**Responsible for:** The raw, unresolved fields of one object_property.txt record, as pulled straight from the parse tree by ObjectPropertyGrammar before any type-checking.

**record `ObjectPropertyParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (17/17 documented): ✔ `name`, `type`, `subtype`, `idType`, `code`, `power`, `mult`, `typeMult`, `adjective`, `negAdjective`, `msg`, `bindui`, `tag`, `value`, `aux`, `desc`, `line`


## backend/parser/pain

##### `PainAssembler.java`

**Responsible for:** Turns the raw PainParseRecords the grammar produces into domain MonsterPain objects.

**class `PainAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<PainParseRecord>, List<String>)` | before 260915 | ✔ |


##### `PainParseRecord.java`

**Responsible for:** Raw parse record for one type: block of pain.txt, produced by PainGrammar and consumed by PainAssembler.

**record `PainParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `messages`, `line`


## backend/parser/pit

##### `PitAssembler.java`

**Responsible for:** Turns the raw PitParseRecords parsed from pit.txt into finished PitProfiles, resolving each record's text fields to their typed forms: room-type digits to PitRoomType, numeric fields to int, flag/spell codes to their enums, colour codes to ColourEnum, and monster names to their MonsterBase/Monste...

**class `PitAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<PitParseRecord>, List<String>)` | before 260915 | ✔ |


##### `PitParseRecord.java`

**Responsible for:** The raw, unresolved carrier for a single pit.txt record, produced by PitGrammar and consumed by uk.co.jackoftradesltd.backend.parser.pit.PitAssembler.

**record `PitParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (14/14 documented): ✔ `name`, `pitRoomType`, `rarity`, `averageDepth`, `objRarity`, `flags`, `bannedFlags`, `innateFreq`, `spells`, `bannedSpells`, `colours`, `monsterBases`, `bannedMonsterBases`, `line`


## backend/parser/playerclass

##### `ClassEquipAssembler.java`

**Responsible for:** Turns the raw ClassEquipParseRecords captured from a class's equip: lines into domain StartItems.

**class `ClassEquipAssembler`** — class Javadoc: ✔ · type stamp: before 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ClassEquipParseRecord>, List<String>)` | before 260915 | ✔ |
| `parseEopts(String, List<String>)` | before 260915 | ✔ |


##### `ClassEquipParseRecord.java`

**Responsible for:** The raw, unvalidated capture of a single equip: line from class.txt — one piece of a class's starting equipment.

**record `ClassEquipParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `tValue`, `sValue`, `min`, `max`, `eopts`, `line`


##### `ClassMagicAssembler.java`

**Responsible for:** Turns a class's raw ClassMagicParseRecord into a domain ClassMagic — the caster profile.

**class `ClassMagicAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ClassMagicParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ClassMagicParseRecord.java`

**Responsible for:** The raw capture of a class's magic: line together with all the book: blocks that follow it — the caster half of a class definition.

**record `ClassMagicParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `firstSpell`, `spellWeight`, `numBooks`, `books`, `line`


##### `ClassSpellAssembler.java`

**Responsible for:** Turns the raw ClassSpellParseRecords of one spellbook into domain MagicSpells.

**class `ClassSpellAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ClassSpellParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ClassSpellBookAssembler.java`

**Responsible for:** Turns the ClassSpellBookParseRecords of one class into the MagicBooks of its uk.co.jackoftradesltd.middle.magic.ClassMagic, and registers the ObjectKind that backs each book.

**class `ClassSpellBookAssembler`** — class Javadoc: ✔ · type stamp: before 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ClassSpellBookParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ClassSpellBookParseRecord.java`

**Responsible for:** The raw capture of one spellbook definition from class.txt — the book: line plus its optional book-graphics:/book-properties: lines and the spell: blocks that belong to it.

**record `ClassSpellBookParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (13/13 documented): ✔ `oBase`, `locFrom`, `name`, `noOfSpells`, `realm`, `glyph`, `colour`, `cost`, `commonness`, `min`, `max`, `spells`, `line`


##### `ClassSpellParseRecord.java`

**Responsible for:** The raw capture of one spell: block from class.txt, together with the desc: text and effect: blocks that follow it.

**record `ClassSpellParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `name`, `level`, `mana`, `fail`, `desc`, `effects`, `exp`, `line`


##### `PlayerClassAssembler.java`

**Responsible for:** The top-level assembler for class.txt: it turns each raw PlayerClassParseRecord into a domain PlayerClass, delegating the nested structures to the specialized assemblers — ClassEquipAssembler for starting gear and ClassMagicAssembler (which fans out to books, spells and effects) for casters.

**class `PlayerClassAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<PlayerClassParseRecord>, List<String>)` | before 260915 | ✔ |


##### `PlayerClassParseRecord.java`

**Responsible for:** The raw, unvalidated capture of one whole class entry from class.txt — the root DTO the grammar hands to PlayerClassAssembler to become a uk.co.jackoftradesltd.middle.player.PlayerClass.

**record `PlayerClassParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (15/15 documented): ✔ `name`, `stats`, `baseSkills`, `skillIncrements`, `hitdie`, `exp`, `maxAttacks`, `minWeight`, `strengthMult`, `equipment`, `oFlags`, `pFlags`, `titles`, `magic`, `line`


## backend/parser/playerproperty

##### `PlayerPropertyAssembler.java`

**Responsible for:** Second stage of the player_property.txt pipeline: turns the raw string PlayerPropertyParseRecords emitted by the PlayerPropertyGrammar parser into fully resolved PlayerProperty domain objects.

**class `PlayerPropertyAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<PlayerPropertyParseRecord>, List<String>)` | before 260915 | ✔ |
| `spreadPlayerPropertyOut(PlayerProperty, ElementEnum)` | on 260924 | ✔ |


##### `PlayerPropertyParseRecord.java`

**Responsible for:** Raw, unresolved parse record for one player_property.txt entry, as emitted by the PlayerPropertyGrammar parser.

**record `PlayerPropertyParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (7/7 documented): ✔ `type`, `code`, `bindui`, `name`, `desc`, `value`, `line`


## backend/parser/playerrace

##### `PlayerRaceAssembler.java`

**Responsible for:** Assembles the raw PlayerRaceParseRecords parsed from p_race.txt into the finished PlayerRace domain objects (reader &rarr; ParseRecord &rarr; assembler &rarr; domain).

**class `PlayerRaceAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<PlayerRaceParseRecord>, List<String>)` | before 260915 | ✔ |


##### `PlayerRaceParseRecord.java`

**Responsible for:** Immutable extraction record for one p_race.txt entry: the raw, still-unresolved fields parsed by the grammar, later turned into the PlayerRace domain type by PlayerRaceAssembler.

**record `PlayerRaceParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (17/17 documented): ✔ `name`, `stats`, `playerSkills`, `hitdie`, `exp`, `infraVision`, `history`, `ageBase`, `ageModifier`, `baseHeight`, `heightModifier`, `baseWeight`, `weightModifier`, `objectFlags`, `playerFlags`, `values`, `line`


## backend/parser/playertimed

##### `PlayerTimedAssembler.java`

**Responsible for:** Resolves the raw PlayerTimedParseRecords captured from player_timed.txt into domain PlayerTimedEffects — the assembler half of the reader pipeline, porting the interpretation the C parse_player_timed_* handlers do in player-timed.c.

**class `PlayerTimedAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<PlayerTimedParseRecord>, List<String>)` | before 260915 | ✔ |


##### `PlayerTimedParseRecord.java`

**Responsible for:** The raw, still-textual capture of one name: record in player_timed.txt, exactly as the grammar read it and before any resolution to domain types.

**record `PlayerTimedParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (18/18 documented): ✔ `name`, `description`, `grades`, `onEnd`, `onIncrease`, `onDecrease`, `msgT`, `fail`, `onBeginEffect`, `onEndEffect`, `resist`, `brand`, `slay`, `flagSynonymFlag`, `flagSynonymValue`, `lowerBound`, `flags`, `line`

**record `PlayerTimedParseRecord.PlayerTimedGradeParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `colour`, `max`, `status`, `msgDown`, `msgUp`

**record `PlayerTimedParseRecord.FailureParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `type`, `value`


## backend/parser/projection

##### `ProjectionAssembler.java`

**Responsible for:** Turns the raw ProjectionParseRecords from projection.txt into domain Projections - the Java form of the parse_projection_* functions and finish_parse_projection() in the C original's obj-init.c.

**class `ProjectionAssembler`** — class Javadoc: ✔ · type stamp: before 260915

- Fields (2/2 documented): ✔ `logger`, `EXPECTED_ELEMENT_COUNT`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ProjectionParseRecord>, List<String>)` | before 260915 | ✔ |
| `resolveCode(int, String, List<String>)` | before 260915 | ✔ |
| `resolveType(int, String, String, List<String>)` | before 260915 | ✔ |
| `resolveMsgt(int, String, String, List<String>)` | before 260915 | ✔ |
| `resolveColour(int, String, String, List<String>)` | before 260915 | ✔ |


##### `ProjectionParseRecord.java`

**Responsible for:** The raw, still-textual capture of one projection.txt record, produced by the grammar and resolved into a uk.co.jackoftradesltd.middle.game.event.projection.Projection by ProjectionAssembler.

**record `ProjectionParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (16/16 documented): ✔ `code`, `name`, `type`, `desc`, `playerDesc`, `blindDesc`, `lashDesc`, `numerator`, `denominator`, `divisor`, `damageCap`, `msgt`, `obvious`, `willWake`, `colour`, `lineNumber`


## backend/parser/quest

##### `QuestAssembler.java`

**Responsible for:** Turns the raw QuestParseRecords from quest.txt into domain Quests.

**class `QuestAssembler`** — class Javadoc: ✔ · type stamp: before 261013

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<QuestParseRecord>, List<String>)` | before 260915 | ✔ |


##### `QuestParseRecord.java`

**Responsible for:** The raw, still-textual form of a single quest.txt record, produced by QuestGrammar and consumed by QuestAssembler.

**record `QuestParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `name`, `level`, `race`, `number`, `line`


## backend/parser/realm

##### `RealmAssembler.java`

**Responsible for:** Turns the raw RealmParseRecords from realm.txt into typed MagicRealm domain objects — the port's equivalent of C's parse_realm_* handlers in init.c.

**class `RealmAssembler`** — class Javadoc: ✔ · type stamp: before 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<RealmParseRecord>, List<String>)` | before 260915 | ✔ |


##### `RealmParseRecord.java`

**Responsible for:** The flat, all-strings capture of one realm record from realm.txt, produced by uk.co.jackoftradesltd.backend.parser.grammars.realm.RealmGrammar before any validation.

**record `RealmParseRecord`** — class Javadoc: ✔ · type stamp: before 260915

- Record components (via `@param`) (6/6 documented): ✔ `name`, `stat`, `verb`, `spellNoun`, `bookNoun`, `line`


## backend/parser/roomprofile

##### `RoomProfileAssembler.java`

**Responsible for:** Turns the raw, text-typed RoomProfileParseRecords from room_template.txt into typed RoomTemplates — the port of the validation C spreads across parse_room_type/parse_room_rating/parse_room_height/ parse_room_width/parse_room_doors/parse_room_tval/parse_room_flags/ parse_room_d in generate.c.

**class `RoomProfileAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<RoomProfileParseRecord>, List<String>)` | before 260915 | ✔ |


##### `RoomProfileParseRecord.java`

**Responsible for:** One raw room record straight out of uk.co.jackoftradesltd.backend.parser.grammars.roomprofiler.RoomProfileGrammar — everything the grammar could pull out of a room_template.txt entry, still in its as-written text form.

**record `RoomProfileParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (11/11 documented): ✔ `profileName`, `type`, `rating`, `rows`, `columns`, `doors`, `tval`, `flags`, `roomMap`, `profileLineNo`, `roomMapLineNo`


## backend/parser/shape

##### `ShapeAssembler.java`

**Responsible for:** Turns the raw ShapeParseRecords from ShapeGrammar into domain PlayerShapes (Java port of parse_shape_* in init.c).

**class `ShapeAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<ShapeParseRecord>, List<String>)` | before 260915 | ✔ |


##### `ShapeParseRecord.java`

**Responsible for:** The raw, un-interpreted capture of one shape.txt record, produced by ShapeGrammar and consumed by ShapeAssembler.

**record `ShapeParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (18/18 documented): ✔ `name`, `combatToh`, `combatTod`, `combatToa`, `skillDisarmPhys`, `skillDisarmMagic`, `skillSave`, `skillStealth`, `skillSearch`, `skillMelee`, `skillThrow`, `skillDig`, `oFlags`, `pFlags`, `values`, `effects`, `blowMethod`, `line`


## backend/parser/slay

##### `SlayAssembler.java`

**Responsible for:** Turns the raw SlayParseRecords from SlayGrammar into domain Slay objects, resolving the fields the grammar left as strings: the target monster race flag or monster base, and the numeric multipliers and power.

**class `SlayAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<SlayParseRecord>, List<String>)` | before 260915 | ✔ |


##### `SlayParseRecord.java`

**Responsible for:** The raw parse record for one entry in slay.txt - an unresolved snapshot of the directive values produced by SlayGrammar and consumed by SlayAssembler.

**record `SlayParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (10/10 documented): ✔ `code`, `name`, `raceFlag`, `base`, `mult`, `oMult`, `power`, `meleeVerb`, `rangeVerb`, `line`


## backend/parser/summon

##### `SummonAssembler.java`

**Responsible for:** Turns the raw SummonParseRecords produced by SummonGrammar into resolved Summon domain objects, resolving each record's message type, monster bases, race flag and fallback cross-reference.

**class `SummonAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<SummonParseRecord>, List<String>)` | before 260915 | ✔ |


##### `SummonParseRecord.java`

**Responsible for:** The raw parse record for one entry in summon.txt - an unresolved snapshot of the directive values produced by SummonGrammar and consumed by SummonAssembler.

**record `SummonParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `name`, `msgType`, `uniques`, `bases`, `raceFlag`, `fallback`, `desc`, `line`


## backend/parser/terrainfeature

##### `TerrainFeatureAssembler.java`

**Responsible for:** Turns the raw TerrainFeatureParseRecords produced by TerrainFeatureGrammar into game-ready Feature objects - the interpretation half of the terrain pipeline, kept out of the reader so the reader is pure lex/parse/collect.

**class `TerrainFeatureAssembler`** — class Javadoc: ✔ · type stamp: before 260915

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<TerrainFeatureParseRecord>, List<String>)` | before 260915 | ✔ |


##### `TerrainFeatureParseRecord.java`

**Responsible for:** The raw parse record for a single terrain.txt block - one-to-one with the directives the grammar saw, before any interpretation.

**record `TerrainFeatureParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (18/18 documented): ✔ `code`, `name`, `desc`, `mimic`, `priority`, `dig`, `flags`, `glyph`, `colour`, `walkMsg`, `runMsg`, `hurtMsg`, `dieMsg`, `confusedMsg`, `lookPrefix`, `lookInPreposition`, `resistFlags`, `line`


## backend/parser/trap

##### `TrapAssembler.java`

**Responsible for:** Turns the raw TrapParseRecords from TrapGrammar into domain TrapKinds — C's struct trap_kind, the trap template loaded from trap.txt (Java port of the parse_trap_* handlers in src/init.c).

**class `TrapAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<TrapParseRecord>, List<String>)` | before 260915 | ✔ |


##### `TrapParseRecord.java`

**Responsible for:** The raw, pre-assembly capture of one trap.txt record — every field a String (or list of strings) exactly as the grammar read it, before TrapAssembler resolves colours, flags, dice and effects into a uk.co.jackoftradesltd.middle.cave.TrapKind.

**record `TrapParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (18/18 documented): ✔ `name`, `text`, `description`, `message`, `saveMessage`, `failMessage`, `xtraMessage`, `glyph`, `colour`, `rarity`, `minDepth`, `maxNum`, `power`, `flags`, `saveFlags`, `effects`, `xtraEffects`, `line`


## backend/parser/vault

##### `VaultAssembler.java`

**Responsible for:** Turns the raw, text-typed VaultParseRecords from vault.txt into typed Vaults — the port of the validation C spreads across parse_vault_type/ parse_vault_rating/parse_vault_rows/parse_vault_columns/ parse_vault_min_depth/parse_vault_max_depth/parse_vault_flags/ parse_vault_d in generate.c ([C] src...

**class `VaultAssembler`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<VaultParseRecord>, List<String>)` | before 260915 | ✔ |


##### `VaultParseRecord.java`

**Responsible for:** One raw vault record straight out of uk.co.jackoftradesltd.backend.parser.grammars.vault.VaultGrammar — everything the grammar could pull out of a vault.txt entry, still in its as-written text form.

**record `VaultParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (11/11 documented): ✔ `name`, `type`, `mapLines`, `map`, `rating`, `rows`, `cols`, `minLevel`, `maxLevel`, `flags`, `line`


## backend/parser/visuals

##### `VisualsCycleAssembler.java`

**Responsible for:** Folds the parsed cycle: blocks into a single VisualsCycler, bucketed group-then-name.

**class `VisualsCycleAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<VisualsCycleParseRecord>, List<String>)` | before 260915 | ✔ |


##### `VisualsCycleParseRecord.java`

**Responsible for:** The raw, unassembled form of one cycle: block from visuals.txt - the grammar's output before any colour resolution.

**record `VisualsCycleParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `group`, `name`, `colours`, `line`


##### `VisualsFlickerAssembler.java`

**Responsible for:** Folds the parsed flicker: blocks into a single FlickerTable.

**class `VisualsFlickerAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<VisualsFlickerParseRecord>, List<String>)` | before 260915 | ✔ |


##### `VisualsFlickerParseRecord.java`

**Responsible for:** The raw, unassembled form of one flicker: block from visuals.txt.

**record `VisualsFlickerParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `name`, `colourChar`, `colours`, `line`


## backend/parser/world

##### `WorldAssembler.java`

**Responsible for:** Turns the raw WorldParseRecords from world.txt into World level objects — Java port of parse_world_level in src/init.c.

**class `WorldAssembler`** — class Javadoc: ✔ · type stamp: before 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<WorldParseRecord>, List<String>)` | before 260915 | ✔ |


##### `WorldParseRecord.java`

**Responsible for:** Thin parse-DTO produced by the World reader for a single level's values in lib/gamedata/world.txt.

**class `WorldParseRecord`** — class Javadoc: ✔ · type stamp: —

- Fields (5/5 documented): ✔ `lineNumber`, `levelNumber`, `levelName`, `up`, `down`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `WorldParseRecord(String, String, String, String, String)` | — | ✔ |
| `getLineNumber()` | — | ✔ |
| `getLevelNumber()` | — | ✔ |
| `getLevelName()` | — | ✔ |
| `getUp()` | — | ✔ |
| `getDown()` | — | ✔ |


## channel

##### `Channels.java`

**Responsible for:** The two queues the halves of the game talk over, and the only place they are created.

**record `Channels`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `coreChannel`, `uiChannel`, `edtChannel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `create()` | — | ✔ |


##### `CoreChannel.java`

**Responsible for:** Everything the core is allowed to do with the channels, and nothing else.

**record `CoreChannel`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `coreReceiver`, `coreSender`


##### `EDTChannel.java`

**Responsible for:** Everything the EDT is allowed to do with the channels: post a message, and nothing else.

**record `EDTChannel`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `edtSender`


##### `EDTSender.java`

**Responsible for:** The EDT's writing end of the UI thread's inbox: the one way a Swing listener can say anything to the rest of the program.

**class `EDTSender`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `queue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EDTSender(LinkedBlockingQueue<ChannelMessage>)` | — | ✔ |
| `send(UIMessage)` | — | ✔ |


##### `Receiver.java`

**Responsible for:** A receive-only view of one channel, typed to everything that can arrive on it.

**interface `Receiver`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `receive()` | — | ✔ |


##### `Sender.java`

**Responsible for:** A send-only view of one channel, narrowed to the messages its holder is entitled to send.

**interface `Sender`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `send(T)` | — | ✔ |


##### `StartupOptions.java`

**Responsible for:** The command line, parsed: everything - switches said, in one immutable value.

**record `StartupOptions`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (7/7 documented): ✔ `selectSavefile`, `startNewCharacter`, `resurrectDeadCharacter`, `requestGraphicsMode`, `useSpecificCharacter`, `useSoundModule`, `useModuleSystem`


##### `UIChannel.java`

**Responsible for:** Everything the UI half is allowed to do with the channels, and nothing else.

**record `UIChannel`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `uiReceiver`, `uiSender`


## channel/colour

##### `ColourEnum.java`

**Responsible for:** The Angband colour palette as an enum: each constant carries its colour code letter, display name and the nine per-translation substitute codes; also converts between codes, names and colours and applies a `ColourTranslation`.

**enum `ColourEnum`** — class Javadoc: ✘ · type stamp: —

- Enum constants (0/29 documented): ✘ `COLOUR_DARK`, `COLOUR_WHITE`, `COLOUR_SLATE`, `COLOUR_ORANGE`, `COLOUR_RED`, `COLOUR_GREEN`, `COLOUR_BLUE`, `COLOUR_UMBER`, `COLOUR_LIGHT_DARK`, `COLOUR_LIGHT_SLATE`, `COLOUR_LIGHT_PURPLE`, `COLOUR_YELLOW`, `COLOUR_LIGHT_RED`, `COLOUR_LIGHT_GREEN`, `COLOUR_LIGHT_BLUE`, `COLOUR_LIGHT_UMBER`, `COLOUR_PURPLE`, `COLOUR_VIOLET`, `COLOUR_TEAL`, `COLOUR_MUD`, `COLOUR_LIGHT_YELLOW`, `COLOUR_MAGENTA_PINK`, `COLOUR_LIGHT_TEAL`, `COLOUR_LIGHT_VIOLET`, `COLOUR_LIGHT_PINK`, `COLOUR_MUSTARD`, `COLOUR_BLUE_SLATE`, `COLOUR_DEEP_LIGHT_BLUE`, `COLOUR_SHADE`
- Fields (3/4 documented): ✔ `colourCharacter`, `colourName`, `colourTranslate` — ✘ `basicColours`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ColourEnum(char, String, char[])` | — | ✔ |
| `attributeToString(ColourEnum)` | — | ✔ |
| `fromCode(String)` | — | ✔ |
| `fromCode(char)` | — | ✔ |
| `forTranslation(ColourTranslation)` | — | ✘ |
| `translateColour(ColourEnum, ColourTranslation, int)` | — | ✘ |


##### `ColourTranslation.java`

**Responsible for:** Colour translation enum used to support environments with a limited colour depth, as well as translating colours to alternates for, for example, menu highlighting

**enum `ColourTranslation`** — class Javadoc: ✔ · type stamp: —

- Enum constants (9/9 documented): ✔ `ATTR_FULL`, `ATTR_MONO`, `ATTR_VGA`, `ATTR_BLIND`, `ATTR_LIGHT`, `ATTR_DARK`, `ATTR_HIGH`, `ATTR_METAL`, `ATTR_MISC`
- Fields (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ColourTranslation(int)` | — | ✔ |
| `getValue()` | — | ✔ |


## channel/corechannel

##### `CoreReceiver.java`

**Responsible for:** The core's reading end of its own inbox: everything the front end has to say to the game.

**class `CoreReceiver`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `queue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CoreReceiver(LinkedBlockingQueue<UIMessage>)` | — | ✔ |
| `receive()` | — | ✔ |


##### `CoreSender.java`

**Responsible for:** The core's writing end of the UI thread's inbox: the one way the game can say anything to the front end.

**class `CoreSender`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `queue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CoreSender(LinkedBlockingQueue<ChannelMessage>)` | — | ✔ |
| `send(CoreMessage)` | — | ✔ |


## channel/directories

##### `AngbandDirs.java`

**Responsible for:** Where the game's files live: one entry per data directory, each overridable from the command line.

**class `AngbandDirs`** — class Javadoc: ✔ · type stamp: —

- Fields (17/17 documented): ✔ `libPath`, `userPath`, `logger`, `BASE_DIR`, `ANGBAND_DIR_ICONS`, `ANGBAND_DIR_SOUNDS`, `ANGBAND_DIR_TILES`, `ANGBAND_DIR_FONTS`, `ANGBAND_DIR_SCREENS`, `ANGBAND_DIR_HELP`, `ANGBAND_DIR_GAMEDATA`, `ANGBAND_DIR_CUSTOMIZE`, `ANGBAND_DIR_USER`, `ANGBAND_DIR_PANIC`, `ANGBAND_DIR_SAVE`, `ANGBAND_DIR_SCORES`, `ANGBAND_DIR_ARCHIVE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setDirectory(String, String)` | — | ✔ |

**enum `AngbandDirs.ANGBAND_DIRS`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/13 documented): ✘ `SCORES`, `GAMEDATA`, `SCREENS`, `HELP`, `PREF`, `FONTS`, `TILES`, `SOUNDS`, `ICONS`, `USER`, `SAVE`, `PANIC`, `ARCHIVE`
- Fields (2/2 documented): ✔ `name`, `path`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ANGBAND_DIRS(String, String)` | — | ✔ |
| `getDirectory(String)` | — | ✔ |
| `contains(String)` | — | ✔ |
| `getName()` | — | ✔ |
| `getPath()` | — | ✔ |
| `setPath(String)` | — | ✔ |


## channel/enums

##### `ChannelEntryFlag.java`

**Responsible for:** The per-entry configuration flags a UI entry's flags field can carry — the Java form of C's anonymous entry_flag enum (ui-entry.c:76-81: ENTRY_FLAG_TIMED_AUX = 1, ENTRY_FLAG_TEMPLATE_ONLY = (1UL Held as a {@link uk.co.jackoftradesltd.channel.utils.Flag of this enum rather than as raw bits, so — u...

**enum `ChannelEntryFlag`** — class Javadoc: ✔ · type stamp: before 260919

- Enum constants (2/2 documented): ✔ `ENTRY_FLAG_TEMPLATE_ONLY`, `ENTRY_FLAG_TIMED_AS_AUX`


##### `CoreLifecycleEvent.java`

**Responsible for:** What the core can report about its own life, as opposed to the game's.

**enum `CoreLifecycleEvent`** — class Javadoc: ✔ · type stamp: —

- Enum constants (1/1 documented): ✔ `STOPPED`


##### `ElementEnum.java`

**Responsible for:** The damage elements as referenced by object code (resistances, brands, slays).

**enum `ElementEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/27 documented): ✘ `ELEM_NONE`, `ELEM_ACID`, `ELEM_ELEC`, `ELEM_FIRE`, `ELEM_COLD`, `ELEM_POIS`, `ELEM_LIGHT`, `ELEM_DARK`, `ELEM_SOUND`, `ELEM_SHARD`, `ELEM_NEXUS`, `ELEM_NETHER`, `ELEM_CHAOS`, `ELEM_DISEN`, `ELEM_WATER`, `ELEM_ICE`, `ELEM_GRAVITY`, `ELEM_INERTIA`, `ELEM_FORCE`, `ELEM_TIME`, `ELEM_PLASMA`, `ELEM_METEOR`, `ELEM_MISSILE`, `ELEM_MANA`, `ELEM_HOLY_ORB`, `ELEM_ARROW`, `ELEM_MAX`
- Fields (3/3 documented): ✔ `isBase`, `isHigh`, `hasResistRune`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ElementEnum(boolean, boolean, boolean)` | before 260817 | ✔ |
| `isBase()` | before 260817 | ✔ |
| `isHigh()` | on 260915 | ✔ |
| `isHasResistRune()` | before 260817 | ✔ |
| `getProjectionEnum()` | — | ✔ |


##### `GameEventType.java`

**Responsible for:** Every kind of game event the UI/event system can broadcast — display updates (map, stats, HP, …), gameplay notifications (player moved, explosion, message), context transitions (enter/leave birth, game, world, store, death) and dungeon-generation introspection hooks.

**enum `GameEventType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (66/66 documented): ✔ `EVENT_MAP`, `EVENT_STATS`, `EVENT_HP`, `EVENT_MANA`, `EVENT_AC`, `EVENT_EXPERIENCE`, `EVENT_PLAYERLEVEL`, `EVENT_PLAYERTITLE`, `EVENT_GOLD`, `EVENT_MONSTERHEALTH`, `EVENT_DUNGEONLEVEL`, `EVENT_PLAYERSPEED`, `EVENT_RACE_CLASS`, `EVENT_PLAYER_NAME`, `EVENT_STUDYSTATUS`, `EVENT_STATUS`, `EVENT_DETECTIONSTATUS`, `EVENT_FEELING`, `EVENT_LIGHT`, `EVENT_STATE`, `EVENT_PLAYERMOVED`, `EVENT_SEEFLOOR`, `EVENT_EXPLOSION`, `EVENT_BOLT`, `EVENT_MISSILE`, `EVENT_INVENTORY`, `EVENT_EQUIPMENT`, `EVENT_ITEMLIST`, `EVENT_MONSTERLIST`, `EVENT_MONSTERTARGET`, `EVENT_OBJECTTARGET`, `EVENT_MESSAGE`, `EVENT_SOUND`, `EVENT_BELL`, `EVENT_USE_STORE`, `EVENT_STORECHANGED`, `EVENT_INPUT_FLUSH`, `EVENT_MESSAGE_FLUSH`, `EVENT_CHECK_INTERRUPT`, `EVENT_REFRESH`, `EVENT_NEW_LEVEL_DISPLAY`, `EVENT_COMMAND_REPEAT`, `EVENT_ANIMATE`, `EVENT_CHEAT_DEATH`, `EVENT_INITSTATUS`, `EVENT_BIRTHPOINTS`, `EVENT_ENTER_INIT`, `EVENT_LEAVE_INIT`, `EVENT_ENTER_BIRTH`, `EVENT_LEAVE_BIRTH`, `EVENT_ENTER_GAME`, `EVENT_LEAVE_GAME`, `EVENT_ENTER_WORLD`, `EVENT_LEAVE_WORLD`, `EVENT_ENTER_STORE`, `EVENT_LEAVE_STORE`, `EVENT_ENTER_DEATH`, `EVENT_LEAVE_DEATH`, `EVENT_GEN_LEVEL_START`, `EVENT_GEN_LEVEL_END`, `EVENT_GEN_ROOM_START`, `EVENT_GEN_ROOM_CHOOSE_SIZE`, `EVENT_GEN_ROOM_CHOOSE_SUBTYPE`, `EVENT_GEN_ROOM_END`, `EVENT_GEN_TUNNEL_FINISHED`, `EVENT_END`


##### `ProjectionEnum.java`

**Responsible for:** The complete set of projection ("GF") types — every kind of bolt, ball, beam or area effect the game can resolve, from elemental damage (acid, fire, …) through terrain alteration (kill/make wall, door, trap) to monster-targeting effects (teleport away, turn, dispel, sleep, polymorph, …).

**enum `ProjectionEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/57 documented): ✘ `PROJ_NONE`, `PROJ_ACID`, `PROJ_ELEC`, `PROJ_FIRE`, `PROJ_COLD`, `PROJ_POIS`, `PROJ_LIGHT`, `PROJ_DARK`, `PROJ_SOUND`, `PROJ_SHARD`, `PROJ_NEXUS`, `PROJ_NETHER`, `PROJ_CHAOS`, `PROJ_DISEN`, `PROJ_WATER`, `PROJ_ICE`, `PROJ_GRAVITY`, `PROJ_INERTIA`, `PROJ_FORCE`, `PROJ_TIME`, `PROJ_PLASMA`, `PROJ_METEOR`, `PROJ_MISSILE`, `PROJ_MANA`, `PROJ_HOLY_ORB`, `PROJ_ARROW`, `PROJ_LIGHT_WEAK`, `PROJ_DARK_WEAK`, `PROJ_KILL_WALL`, `PROJ_KILL_DOOR`, `PROJ_KILL_TRAP`, `PROJ_MAKE_DOOR`, `PROJ_MAKE_TRAP`, `PROJ_AWAY_UNDEAD`, `PROJ_AWAY_SPIRIT`, `PROJ_AWAY_EVIL`, `PROJ_AWAY_ALL`, `PROJ_TURN_UNDEAD`, `PROJ_TURN_EVIL`, `PROJ_TURN_LIVING`, `PROJ_TURN_ALL`, `PROJ_DISP_UNDEAD`, `PROJ_DISP_EVIL`, `PROJ_DISP_ALL`, `PROJ_SLEEP_UNDEAD`, `PROJ_SLEEP_EVIL`, `PROJ_SLEEP_ALL`, `PROJ_MON_CLONE`, `PROJ_MON_POLY`, `PROJ_MON_HEAL`, `PROJ_MON_SPEED`, `PROJ_MON_SLOW`, `PROJ_MON_CONF`, `PROJ_MON_HOLD`, `PROJ_MON_STUN`, `PROJ_MON_DRAIN`, `PROJ_MON_CRUSH`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getFromElementEnum(ElementEnum)` | — | ✔ |


##### `StatElemType.java`

**Responsible for:** Whether a UI entry's parameter refers to a player stat or a damage element.

**enum `StatElemType`** — class Javadoc: ✔ · type stamp: before 260916

- Enum constants (3/3 documented): ✔ `NONE`, `STAT`, `ELEMENT`
- Fields (0/1 documented): ✘ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `StatElemType(String)` | — | ✘ |
| `fromValue(String)` | before 260916 | ✔ |


##### `UILifecycleEvent.java`

**Responsible for:** What the front end can ask of the core about its life, as opposed to about the game.

**enum `UILifecycleEvent`** — class Javadoc: ✔ · type stamp: —

- Enum constants (2/2 documented): ✔ `START`, `SAVE_AND_STOP`


##### `UiEventType.java`

**Responsible for:** The kinds of UI event the front end can deliver to the game, mirroring the EVT_* event types of the C original's user-interface event system (src/ui-event.h).

**enum `UiEventType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (9/9 documented): ✔ `EVT_NONE`, `EVT_KBRD`, `EVT_MOUSE`, `EVT_RESIZE`, `EVT_BUTTON`, `EVT_ESCAPE`, `EVT_MOVE`, `EVT_SELECT`, `EVT_SWITCH`


## channel/globals

##### `Angband.java`

**Responsible for:** Holds the game's version name/string, build id and copyright text (C's `version.h`/`buildid` constants).

**class `Angband`** — class Javadoc: ✘ · type stamp: —

- Fields (5/5 documented): ✔ `versionName`, `versionString`, `buildId`, `buildVer`, `copyright`


##### `ChannelRegistry.java`

**Responsible for:** Static holder for a few shared game constants the channel layer needs (`STAT_MAX`, the `extractEnergy` table, `PY_MAX_LEVEL`).

**class `ChannelRegistry`** — class Javadoc: ✘ · type stamp: —

- Fields (0/3 documented): ✘ `STAT_MAX`, `extractEnergy`, `PY_MAX_LEVEL`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getPYMaxLevel()` | — | ✘ |
| `setPYMaxLevel(int)` | — | ✘ |


## channel/messages

##### `ChannelMessage.java`

**Responsible for:** The root of the protocol: anything that can cross between the two halves of the game.

**interface `ChannelMessage`** — class Javadoc: ✔ · type stamp: —


##### `CoreMessage.java`

**Responsible for:** Everything the core can say to the front end.

**interface `CoreMessage`** — class Javadoc: ✔ · type stamp: —

**record `CoreMessage.SimpleCoreMessage`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `gameEventType`

**record `CoreMessage.TextCoreMessage`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `gameEventType`, `message`

**record `CoreMessage.LifecycleCoreMessage`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `event`

**record `CoreMessage.GameEventCoreMessage`** — class Javadoc: ✔ · type stamp: before 260909

- Record components (via `@param`) (2/2 documented): ✔ `type`, `data`


##### `UIMessage.java`

**Responsible for:** Everything the UI side can say, to the core and to itself.

**interface `UIMessage`** — class Javadoc: ✔ · type stamp: —

**record `UIMessage.LifecycleUIMessage`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `event`

**record `UIMessage.WindowCloseRequested`** — class Javadoc: ✔ · type stamp: —

**record `UIMessage.SimpleUIMessage`** — class Javadoc: ✔ · type stamp: before 260915

- Record components (via `@param`) (1/1 documented): ✔ `type`

**record `UIMessage.UIEntriesLoaded`** — class Javadoc: ✘ · type stamp: —

- Record components (via `@param`) (0/1 documented): ✘ `entrySpecs`


## channel/messages/data

##### `EventDataBirthPoints.java`

**Responsible for:** GameEventData payload for the point-buy stage of character creation — the points currently spent per stat, the cost to increment each stat, and the points still available.

**class `EventDataBirthPoints`** — class Javadoc: ✔ · type stamp: before 260915

- Fields (3/3 documented): ✔ `points`, `incPoints`, `remaining`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EventDataBirthPoints(Map<Stats,Integer>, Map<Stats,Integer>, int)` | before 260915 | ✔ |
| `getPoints()` | before 260915 | ✔ |
| `getIncPoints()` | before 260915 | ✔ |
| `getRemaining()` | before 260915 | ✔ |


##### `EventDataBolt.java`

**Responsible for:** GameEventData payload describing one frame of a bolt travelling between two grids — the port of C's game_event_data.bolt (src/game-event.h), signalled by event_signal_bolt.

**record `EventDataBolt`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `projectionType`, `drawing`, `seen`, `beam`, `origin`, `current`


##### `EventDataBoolean.java`

**Responsible for:** GameEventData payload carrying a single yes/no answer — the port of the flag arm of C's game_event_data union (src/game-event.h).

**record `EventDataBoolean`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EventDataBoolean()` | — | ✔ |


##### `EventDataColourString.java`

**Responsible for:** Payload for a redraw event that carries an array of AngbandDisplayCharacters, one per displayed cell — used today only by EVENT_EQUIPMENT, the equippy row HandlersHolder.prtEquippy draws.

**record `EventDataColourString`** — class Javadoc: ✔ · type stamp: on 260927

- Record components (via `@param`) (1/1 documented): ✔ `string`


##### `EventDataExplosion.java`

**Responsible for:** GameEventData payload describing an explosion ("ball") effect — the projection type, the affected grids with their distances from the centre and per-grid visibility, and the blast centre.

**class `EventDataExplosion`** — class Javadoc: ✔ · type stamp: before 260915

- Fields (7/8 documented): ✔ `projType`, `numGrids`, `distanceToGrid`, `drawing`, `playerSeesGrid`, `blastGrid`, `centre` — ✘ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EventDataExplosion(int, int, ArrayList<Integer>, boolean, ArrayList<Boolean>, ArrayList<Loc>, Loc)` | before 260915 | ✔ |
| `getProjType()` | before 260915 | ✔ |
| `getNumGrids()` | before 260915 | ✔ |
| `getDistanceToGrid()` | before 260915 | ✔ |
| `isDrawing()` | before 260915 | ✔ |
| `getPlayerSeesGrid()` | before 260915 | ✔ |
| `getBlastGrid()` | before 260915 | ✔ |
| `getCentre()` | before 260915 | ✔ |


##### `EventDataFullStat.java`

**Responsible for:** GameEventData payload carrying one stat row's full redraw state — the Java stand-in for C's prt_stat ([C] ui-display.c, function prt_stat) reading player->stat_cur[stat], player->stat_max[stat] and player->state.stat_use[stat] directly off the shared player global.

**record `EventDataFullStat`** — class Javadoc: ✔ · type stamp: on 260927

- Record components (via `@param`) (4/4 documented): ✔ `statIndex`, `current`, `max`, `use`


##### `EventDataGrid.java`

**Responsible for:** GameEventData payload carrying one map square.

**record `EventDataGrid`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `row`, `col`


##### `EventDataInt.java`

**Responsible for:** GameEventData payload carrying a single int-wide number — the int-valued sibling of EventDataLong, for a redraw whose figure fits an int and so does not need the wider type.

**record `EventDataInt`** — class Javadoc: ✔ · type stamp: on 260927

- Record components (via `@param`) (1/1 documented): ✔ `data`


##### `EventDataLong.java`

**Responsible for:** GameEventData payload carrying a single number too wide for EventDataStat's int pair — today, the figure EVENT_EXPERIENCE sends alongside its EventDataLongStat pair: the experience needed to reach the next level, or the current total at level fifty.

**record `EventDataLong`** — class Javadoc: ✔ · type stamp: on 260927

- Record components (via `@param`) (1/1 documented): ✔ `value`


##### `EventDataLongStat.java`

**Responsible for:** GameEventData payload carrying a pair of numbers too wide for EventDataStat's int pair — the long-valued sibling of that record, and today the (current, maximum) experience pair EVENT_EXPERIENCE sends: C's player->exp and player->max_exp, both held as long here because Player.exp/ maxExp are, per...

**record `EventDataLongStat`** — class Javadoc: ✔ · type stamp: on 260927

- Record components (via `@param`) (2/2 documented): ✔ `value`, `other`


##### `EventDataMessage.java`

**Responsible for:** GameEventData payload carrying a line of text for the player and the category it falls under — the port of C's game_event_data.message (src/game-event.h), signalled from msgt (src/message.c).

**record `EventDataMessage`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `type`, `message`


##### `EventDataMonsterInfo.java`

**Responsible for:** GameEventData payload carrying everything the sidebar's monster health bar needs about the tracked monster - the Java stand-in for C's prt_health_aux and monster_health_attr ([C] ui-display.c) reading player->upkeep->health_who and, through it, mon->hp, mon->maxhp, monster_is_visible(mon) and mon...

**record `EventDataMonsterInfo`** — class Javadoc: ✔ · type stamp: on 260929

- Record components (via `@param`) (12/12 documented): ✔ `monHP`, `monMaxHP`, `monsterExists`, `monsterVisible`, `feared`, `disen`, `command`, `conf`, `stuned`, `slept`, `held`, `playerTmdImage`


##### `EventDataPlayerSpeed.java`

**Responsible for:** The payload of an EVENT_PLAYERSPEED message - everything C's prt_speed_aux ([C] ui-display.c, function prt_speed_aux) reads at draw time, packed so the front end never has to follow a pointer back into the core.

**record `EventDataPlayerSpeed`** — class Javadoc: ✔ · type stamp: on 260929

- Record components (via `@param`) (4/4 documented): ✔ `speed`, `optEffectiveSpeed`, `extractEnergy`, `extractEnergyNormal`


##### `EventDataSize.java`

**Responsible for:** GameEventData payload carrying a pair of dimensions — the port of C's game_event_data.size (src/game-event.h), signalled by event_signal_size.

**record `EventDataSize`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `height`, `width`


##### `EventDataStat.java`

**Responsible for:** GameEventData payload carrying a pair of numbers — the value a redraw is really about, and whatever second number the event pairs it with for display.

**record `EventDataStat`** — class Javadoc: ✔ · type stamp: before 260915

- Record components (via `@param`) (2/2 documented): ✔ `current`, `other`


##### `EventDataString.java`

**Responsible for:** GameEventData payload carrying a single piece of text — the port of the string arm of C's game_event_data union (src/game-event.h), signalled by event_signal_string.

**record `EventDataString`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `string`


##### `EventDataStrings.java`

**Responsible for:** GameEventData payload carrying several pieces of text at once — the multi-value sibling of EventDataString, used where an event needs more than one label in the same signal, such as a race name paired with a class name.

**record `EventDataStrings`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `strings`


##### `EventDataTunnel.java`

**Responsible for:** GameEventData payload reporting how one tunnel turned out during level generation — the port of C's game_event_data.tunnel (src/game-event.h).

**record `EventDataTunnel`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `nStep`, `nPierce`, `nDug`, `dStart`, `dEnd`, `early`


##### `GameEventData.java`

**Responsible for:** Marker interface for the payload carried by a GameEventType event.

**interface `GameEventData`** — class Javadoc: ✔ · type stamp: —


##### `PlayerCharSheetView.java`

**Responsible for:** Immutable snapshot of the character-sheet values the still-unported display_panel family in src/ui-player.c reads directly from player/player->upkeep — the same boundary-crossing shape as PlayerStatusView, split out into its own record because these fields have nothing to do with the prt_* sideba...

**record `PlayerCharSheetView`** — class Javadoc: ✔ · type stamp: on 260925

- Record components (via `@param`) (9/33 documented): ✔ `bodyCount`, `playerIsPlaying`, `playerRaceStatBonuses`, `playerClassStatBonuses`, `playerEquipStatBonuses`, `playerTotalStatBonuses`, `playerCurrModStat`, `totalWeight`, `weightLimit` — ✘ `expToLevel`, `expFactor`, `height`, `weight`, `age`, `toA`, `toD`, `toH`, `meleeSkill`, `shootSkill`, `bthPlusAdj`, `meleeDice`, `meleeSides`, `numBlows`, `numShots`, `saveSkill`, `stealthSkill`, `disarmPhysSkill`, `disarmMagicSkill`, `deviceSkill`, `searchSkill`, `infra`, `calcSpeed`, `optionEffectiveSpeed`


##### `PlayerEventStatusUpdate.java`

**Responsible for:** The UI thread's cached snapshot of the player-status sidebar and the character sheet — the two-channel migration's stand-in for C's live reads of the player/cave globals inside the prt_* family in ui-display.c and the display_panel family in ui-player.c.

**class `PlayerEventStatusUpdate`** — class Javadoc: ✔ · type stamp: before 260912

- Fields (2/2 documented): ✔ `cachedPlayerStatusView`, `cachedPlayerCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `updatePlayerStatusView(PlayerStatusView)` | before 260912 | ✔ |
| `getPlayerStatusView()` | before 260912 | ✔ |
| `updatePlayerCharSheetView(PlayerCharSheetView)` | on 260925 | ✔ |
| `getPlayerCharSheetView()` | on 260925 | ✔ |
| `updatePlayerStatusPlayerName(String)` | before 260912 | ✔ |
| `updatePlayerStatusPlayerTitle(String)` | before 260912 | ✔ |
| `updatePlayerStatusRaceName(String)` | before 260912 | ✔ |
| `updatePlayerStatusClassName(String)` | before 260912 | ✔ |
| `updatePlayerStatusLevel(int)` | before 260912 | ✔ |
| `updatePlayerStatusMaxLevel(int)` | — | ✘ |
| `updatePlayerStatusExperience(long)` | before 260912 | ✔ |
| `updatePlayerStatusMaxExperience(long)` | before 260912 | ✔ |
| `updatePlayerStatusGold(long)` | before 260912 | ✔ |
| `updatePlayerStatusCurrentHP(int)` | before 260912 | ✔ |
| `updatePlayerStatusMaxHP(int)` | before 260912 | ✔ |
| `updatePlayerStatusCurrentSP(int)` | before 260912 | ✔ |
| `updatePlayerStatusMaxSP(int)` | before 260912 | ✔ |
| `updatePlayerStatusArmourClass(int)` | before 260912 | ✔ |
| `updatePlayerStatusSpeed(int)` | before 260912 | ✔ |
| `updatePlayerStatusCurrentStats(int[])` | before 260912 | ✔ |
| `updatePlayerStatusMaxStats(int[])` | before 260912 | ✔ |
| `updatePlayerStatusStatsString(String[])` | before 260912 | ✔ |
| `updatePlayerCharSheetBodyCount(int)` | on 260925 | ✔ |
| `updatePlayerStatusMonsterHealth(int)` | before 260912 | ✔ |
| `updatePlayerStatusMaxMonsterHealth(int)` | before 260912 | ✔ |
| `updatePlayerStatusMonsterVisible(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusPlayerHallucinating(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonsterTracked(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonTmdFear(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonTmdDisen(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonTmdCommand(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonTmdConf(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonTmdStun(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonTmdSleep(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusMonTmdHold(boolean)` | before 260912 | ✔ |
| `updatePlayerStatusDepth(int)` | before 260912 | ✔ |
| `updatePlayerStatusMaxDepth(int)` | before 260912 | ✔ |
| `updatePlayerStatusStudyStatus(String)` | before 260912 | ✔ |
| `updatePlayerStatusStudyConditions(String)` | before 260912 | ✔ |
| `updatePlayerStatusDetectionStatus(String)` | before 260912 | ✔ |
| `updatePlayerStatusRestingRepeatStatus(String)` | before 260912 | ✔ |
| `updatePlayerStatusLevelFeeling(String)` | before 260912 | ✔ |
| `updatePlayerStatusLightLevel(String)` | before 260912 | ✔ |
| `updatePlayerStatusEquipSlotCount(int)` | before 260912 | ✔ |
| `updatePlayerStatusTurn(int)` | — | ✘ |
| `updatePlayerStatusTotalEnergy(int)` | — | ✘ |
| `updatePlayerStatusRestingEnergy(int)` | — | ✘ |
| `updatePlayerCharSheetIsPlaying(boolean)` | on 260925 | ✔ |
| `updatePlayerCharSheetRaceStatBonuses(int[])` | on 260925 | ✔ |
| `updatePlayerCharSheetClassStatBonuses(int[])` | on 260925 | ✔ |
| `updatePlayerCharSheetEquipStatBonuses(int[])` | on 260925 | ✔ |
| `updatePlayerCharSheetTotalStatBonuses(int[])` | on 260925 | ✔ |
| `updatePlayerCharSheetCurrentStatBonuses(int[])` | on 260925 | ✔ |
| `updatePlayerCharSheetTotalWeight(int)` | on 260925 | ✔ |
| `updatePlayerCharSheetWeightLimit(int)` | — | ✘ |
| `updatePlayerCharSheetExpToLevel(long[])` | — | ✘ |
| `updatePlayerCharSheetExpFactor(int)` | — | ✘ |
| `updatePlayerCharSheetHeight(int)` | — | ✘ |
| `updatePlayerCharSheetWeight(int)` | — | ✘ |
| `updatePlayerCharSheetAge(int)` | — | ✘ |
| `updatePlayerCharSheetToA(int)` | — | ✘ |
| `updatePlayerCharSheetToD(int)` | — | ✘ |
| `updatePlayerCharSheetToH(int)` | — | ✘ |
| `updatePlayerCharSheetMeleeSkill(int)` | — | ✘ |
| `updatePlayerCharSheetShootSkill(int)` | — | ✘ |
| `updatePlayerCharSheetBthPlusAdj(int)` | — | ✘ |
| `updatePlayerCharSheetMeleeDice(int)` | — | ✘ |
| `updatePlayerCharSheetMeleeSides(int)` | — | ✘ |
| `updatePlayerCharSheetnumBlows(int)` | — | ✘ |
| `updatePlayerCharSheetNumShots(int)` | — | ✘ |


##### `PlayerStatusView.java`

**Responsible for:** Immutable snapshot of the player-status sidebar — the two-channel migration's stand-in for C's direct reads of the player/cave globals scattered across the prt_* family in src/ui-display.c.

**record `PlayerStatusView`** — class Javadoc: ✔ · type stamp: before 260912

- Record components (via `@param`) (37/42 documented): ✔ `name`, `title`, `raceName`, `className`, `level`, `experience`, `maxExperience`, `gold`, `chp`, `mhp`, `csp`, `msp`, `armourClass`, `speed`, `currentStats`, `maxStats`, `statString`, `monsterHealth`, `maxMonsterHealth`, `monsterVisible`, `playerHallucinating`, `monsterTracked`, `monsterTmdFear`, `monsterTmdDisen`, `monsterTmdCommand`, `monsterTmdConf`, `monsterTmdStun`, `monsterTmdSleep`, `monsterTmdHold`, `depth`, `studyStatus`, `studyConditions`, `detectionStatus`, `restingRepeatingState`, `levelFeeling`, `lightLevel`, `equipmentSlotCount` — ✘ `maxLevel`, `maxDepth`, `turn`, `totalEnergy`, `restingTurn`


## channel/parser

##### `Assembler.java`

**Responsible for:** Assembler interface Functional interface for the final step of a parser boundary: it takes the raw parse-records a Reader has accumulated from a lib/gamedata file, plus any error messages collected along the way, and combines them into the finished result type the rest of the game uses.

**interface `Assembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<R>, List<String>)` | — | ✔ |


##### `ErrorParsing.java`

**Responsible for:** Reporting sink for the soft errors a data-file parse collects.

**class `ErrorParsing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportAndCheck(String, ParseResult<?>, Logger)` | — | ✔ |


##### `GrammarDriver.java`

**Responsible for:** The single shared skeleton every list-shaped data-file reader delegates to.

**class `GrammarDriver`** — class Javadoc: ✔ · type stamp: on 260911

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `GrammarDriver()` | on 260911 | ✔ |
| `run(String, Function<CharStream,L>, Function<CommonTokenStream,P>, Extractor<P,R>, Assembler<R,List<T>>, Logger)` | on 260911 | ✔ |
| `checkRecordCount(String, int, List<String>)` | on 260911 | ✔ |

**interface `GrammarDriver.Extractor`** — class Javadoc: ✔ · type stamp: on 260911

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `extract(P, ParseErrors, List<String>)` | on 260911 | ✔ |


##### `ParseErrors.java`

**Responsible for:** Class to collect the errors occurring during the parsing of game data files and store them to throw them in one list at the end.

**class `ParseErrors`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `logger`, `errors`, `sourceName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ParseErrors(String)` | — | ✔ |
| `install(Lexer, Parser, String)` | — | ✔ |
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✔ |
| `throwIfAny()` | — | ✔ |
| `add(int, String)` | — | ✔ |
| `add(String)` | — | ✔ |
| `hasErrors()` | — | ✔ |
| `getErrors()` | — | ✔ |


##### `ParseResult.java`

**Responsible for:** A generic holder for the outcome of parsing one lib/gamedata file: the items successfully assembled from it, plus any soft errors gathered along the way.

**record `ParseResult`** — class Javadoc: ✔ · type stamp: on 260911

- Record components (via `@param`) (2/2 documented): ✔ `items`, `errors`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hasErrors()` | on 260911 | ✔ |


##### `Reader.java`

**Responsible for:** Reader interface Interface for all the ANTLR4 parsers.

**interface `Reader`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | — | ✔ |


## channel/strings

##### `AngbandDisplayCharacter.java`

**Responsible for:** Immutable pairing of a display glyph and the colour it is drawn in - one screen cell's worth of data.

**class `AngbandDisplayCharacter`** — class Javadoc: ✔ · type stamp: —

- Fields (2/3 documented): ✔ `character`, `attributeColour` — ✘ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `AngbandDisplayCharacter(char, ColourEnum)` | — | ✔ |
| ctor `AngbandDisplayCharacter(char, char)` | — | ✔ |
| ctor `AngbandDisplayCharacter(char, String)` | — | ✔ |
| `getCharacter()` | — | ✔ |
| `getAttributeColour()` | — | ✔ |
| `equals(Object)` | — | ✔ |
| `hashCode()` | — | ✔ |


## channel/uichannel

##### `UIEntrySpec.java`

**Responsible for:** The slice of a uk.co.jackoftradesltd.frontend.entries.UIEntry the core needs once loading finishes, carried across the channel inside UIMessage.UIEntriesLoaded.

**record `UIEntrySpec`** — class Javadoc: ✔ · type stamp: before 260924

- Record components (via `@param`) (3/3 documented): ✔ `entryName`, `combinerName`, `entryFlags`


##### `UIEntryValue.java`

**Responsible for:** A UI entry's computed display value, the Java form of the (val, auxval) out-parameter pair C's compute_ui_entry_values_for_object and compute_ui_entry_values_for_player ([C] ui-entry.c:672, 875) each write into, bundled as one return value instead of two pointers.

**record `UIEntryValue`** — class Javadoc: ✔ · type stamp: before 260924

- Record components (via `@param`) (3/3 documented): ✔ `val`, `auxVal`, `knownRune`


##### `UIEntryValueSnapshot.java`

**Responsible for:** A point-in-time snapshot of every named UI entry's computed UIEntryValue, carried across the channel as one GameEventData payload rather than one message per entry.

**record `UIEntryValueSnapshot`** — class Javadoc: ✔ · type stamp: before 260924

- Record components (via `@param`) (1/1 documented): ✔ `byEntryName`


##### `UIReceiver.java`

**Responsible for:** The UI thread's reading end of its own inbox, and the queue it spends most of its life blocked on.

**class `UIReceiver`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `queue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIReceiver(LinkedBlockingQueue<ChannelMessage>)` | — | ✔ |
| `receive()` | — | ✔ |


##### `UISender.java`

**Responsible for:** The UI thread's writing end of the core's inbox: the one way the front end can say anything to the game.

**class `UISender`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `queue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UISender(LinkedBlockingQueue<UIMessage>)` | — | ✔ |
| `send(UIMessage)` | — | ✔ |


## channel/utils

##### `Combiner.java`

**Responsible for:** The contract every "combining algorithm" implements: the rule Angband uses to fold several equipment slots' contributions to one displayed property (a resistance, a stat bonus, a sustain flag) down to the single figure shown on the character sheet.

**interface `Combiner`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `UI_ENTRY_UNKNOWN_VALUE`, `UI_ENTRY_VALUE_NOT_PRESENT`, `UI_ENTRY_RESIST0_RES_VUL`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `clone()` | — | ✔ |


##### `Flag.java`

**Responsible for:** A type-safe wrapper around an EnumSet that emulates Angband's C "flag set" bit-arrays (the flag_* family of macros in src/z-bitflag.c).

**class `Flag`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (3/3 documented): ✔ `flagSet`, `all`, `eClass`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Flag(Class<E>)` | before 260815 | ✔ |
| ctor `Flag(Class<E>, E)` | before 260815 | ✔ |
| ctor `Flag(Class<E>, E)` | before 260815 | ✔ |
| `has(E)` | before 260815 | ✔ |
| `next(E)` | before 260815 | ✔ |
| `iterator()` | before 260815 | ✔ |
| `count()` | before 260815 | ✔ |
| `isEmpty()` | before 260815 | ✔ |
| `isFull()` | before 260815 | ✔ |
| `isInter(FlagView<E>)` | before 260815 | ✔ |
| `isSubset(FlagView<E>)` | before 260815 | ✔ |
| `isEqual(FlagView<E>)` | before 260815 | ✔ |
| `on(E)` | before 260815 | ✔ |
| `off(E)` | on 260830 | ✔ |
| `wipe()` | before 260815 | ✔ |
| `setAll()` | before 260815 | ✔ |
| `negate()` | before 260815 | ✔ |
| `copyFrom(FlagView<E>)` | — | ✔ |
| `union(FlagView<E>)` | — | ✔ |
| `inter(FlagView<E>)` | — | ✔ |
| `diff(FlagView<E>)` | — | ✔ |
| `test(E)` | before 260815 | ✔ |
| `test(List<E>)` | before 260815 | ✔ |
| `testAll(E)` | before 260815 | ✔ |
| `testAll(List<E>)` | before 260815 | ✔ |
| `clear(E)` | before 260815 | ✔ |
| `clear(List<E>)` | before 260815 | ✔ |
| `set(E)` | before 260815 | ✔ |
| `set(List<E>)` | before 260815 | ✔ |
| `init(E)` | before 260815 | ✔ |
| `init(List<E>)` | before 260815 | ✔ |
| `mask(E)` | before 260815 | ✔ |
| `mask(List<E>)` | before 260815 | ✔ |
| `onDbg(E)` | on 260904 | ✔ |
| `toList()` | on 260904 | ✔ |
| `andNot(E)` | on 261007 | ✔ |


##### `FlagView.java`

**Responsible for:** The read-only half of a flag set — everything that can be asked of a Flag without being able to change it.

**interface `FlagView`** — class Javadoc: ✔ · type stamp: on 260818

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `has(E)` | — | ✔ |
| `next(E)` | — | ✔ |
| `count()` | — | ✔ |
| `isEmpty()` | — | ✔ |
| `isFull()` | — | ✔ |
| `isInter(FlagView<E>)` | — | ✔ |
| `isSubset(FlagView<E>)` | — | ✔ |
| `isEqual(FlagView<E>)` | — | ✔ |
| `test(List<E>)` | — | ✔ |
| `testAll(List<E>)` | — | ✔ |


##### `StringUtils.java`

**Responsible for:** Static string-manipulation helpers ported from the original C source's string utilities (src/z-util.c / src/z-form.c).

**class `StringUtils`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `logger`, `programmeName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `strnfcat(String, String, Object)` | — | ✔ |
| `vstrnfmt(int, String, Object)` | — | ✔ |
| `vformat(String, Object)` | — | ✔ |
| `strnfmt(String, int, Object)` | — | ✔ |
| `format(String, Object)` | — | ✔ |
| `plural(int)` | — | ✔ |
| `verbAgreement(int, String, String)` | — | ✔ |
| `clipTo(String, int)` | — | ✔ |
| `streq(String, String)` | — | ✔ |
| `isPrint(char)` | — | ✔ |
| `textSchr(String, char)` | — | ✔ |
| `stringLength(String)` | — | ✔ |
| `strICmp(String, String)` | — | ✔ |
| `strNICmp(String, String, int)` | — | ✔ |
| `strIStr(String, String)` | — | ✔ |
| `strCpy(String, int)` | — | ✔ |
| `strCat(String, String, int)` | — | ✔ |
| `strCap(String)` | — | ✔ |
| `suffix(String, String)` | — | ✔ |
| `suffixI(String, String)` | — | ✔ |
| `prefix(String, String)` | — | ✔ |
| `prefixI(String, String)` | — | ✔ |
| `strSkip(String, char, char)` | — | ✔ |
| `strEscape(String, char)` | — | ✔ |
| `hexCharToInt(char)` | — | ✔ |
| `hexStrToInt(String)` | — | ✔ |
| `containsOnlySpaces(String)` | — | ✔ |
| `isVowel(char)` | — | ✔ |
| `C2I(char)` | on 260901 | ✔ |
| `I2C(int)` | on 260901 | ✔ |
| `isAlpha(char)` | on 260901 | ✔ |
| `isDigit(char)` | on 260901 | ✔ |
| `isSpace(char)` | on 260910 | ✔ |


##### `UIEntryCombinerState.java`

**Responsible for:** The running fold state passed between the four methods of a Combiner: the Java shape of the C original's struct ui_entry_combiner_state (ui-entry-combiner.h), which holds a value channel and an auxiliary channel accumulator (accum, accum_aux) plus a void *work scratch pointer.

**class `UIEntryCombinerState`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `negAccum`, `negAccumAux`, `accum`, `accumAux`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryCombinerState()` | — | ✘ |
| ctor `UIEntryCombinerState(int, int, int, int)` | — | ✘ |
| `getNegAccum()` | — | ✔ |
| `setNegAccum(int)` | — | ✔ |
| `getNegAccumAux()` | — | ✔ |
| `setNegAccumAux(int)` | — | ✔ |
| `getAccum()` | — | ✔ |
| `setAccum(int)` | — | ✔ |
| `getAccumAux()` | — | ✔ |
| `setAccumAux(int)` | — | ✔ |


##### `UIEntryValuesHolder.java`

**Responsible for:** Static holder of the current snapshot of UI-entry values keyed by entry name, with add/remove/lookup/clear operations.

**class `UIEntryValuesHolder`** — class Javadoc: ✘ · type stamp: —

- Fields (0/1 documented): ✘ `snapshot`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addEntry(String, UIEntryValue)` | — | ✘ |
| `removeEntry(String)` | — | ✘ |
| `getEntryByName(String)` | — | ✘ |
| `clear()` | — | ✘ |


## channel/utils/combiners

##### `AddCombiner.java`

**Responsible for:** The ADD combiner: folds several contributing values into their running sum.

**class `AddCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `addCombineAccumHelp(int, int)` | — | ✔ |
| `clone()` | — | ✔ |


##### `BitwiseOrCombiner.java`

**Responsible for:** The BITWISE_OR combiner: folds several contributing values together with the bitwise-OR operator, so the result carries every bit set by any contributor.

**class `BitwiseOrCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `clone()` | — | ✔ |
| `bitwiseOrCombineAccumHelp(int, int)` | — | ✔ |


##### `CombinerName.java`

**Responsible for:** The name-indexed table of value-combining algorithms a UI entry can bind to, one constant per way several contributing values (a resistance, a stat bonus, a sustain flag, ...) fold down into the single figure actually displayed for it.

**enum `CombinerName`** — class Javadoc: ✔ · type stamp: before 260916

- Enum constants (10/10 documented): ✔ `NONE`, `ADD`, `BITWISE_OR`, `FIRST`, `LARGEST`, `LAST`, `LOGICAL_OR`, `LOGICAL_OR_WITH_CANCEL`, `RESIST_0`, `SMALLEST`
- Fields (1/1 documented): ✔ `combiner`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CombinerName(Combiner)` | before 260916 | ✔ |
| `init(int, int)` | before 260916 | ✔ |
| `getCombiner()` | — | ✘ |


##### `FirstCombiner.java`

**Responsible for:** The FIRST combiner: keeps the first contribution and discards every later one.

**class `FirstCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `clone()` | — | ✔ |


##### `LargestCombiner.java`

**Responsible for:** The LARGEST combiner: folds several contributions down to the largest of them.

**class `LargestCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clone()` | — | ✔ |
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `largestCombineAccumHelp(int, int)` | — | ✔ |


##### `LastCombiner.java`

**Responsible for:** The LAST combiner: keeps the most recent contribution and discards every earlier one.

**class `LastCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `clone()` | — | ✔ |


##### `LogicalOrCombiner.java`

**Responsible for:** The LOGICAL_OR combiner: reduces several contributions to a single boolean - true if any contributor is true.

**class `LogicalOrCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `logicalOrCombineAccumHelp(int, int)` | — | ✔ |
| `clone()` | — | ✔ |


##### `LogicalOrWithCancelCombiner.java`

**Responsible for:** The LOGICAL_OR_WITH_CANCEL combiner: a logical OR in which any negative contribution overrides every positive one.

**class `LogicalOrWithCancelCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `logicalOrWithCancelCombineAccumHelp(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `clone()` | — | ✔ |


##### `Resist0Combiner.java`

**Responsible for:** The RESIST_0 combiner: the rule Angband uses to fold several elemental resistances into the single figure shown on the character sheet.

**class `Resist0Combiner`** — class Javadoc: ✔ · type stamp: —

**record `Resist0Combiner.TwoValues`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `pos`, `neg`
- Fields (1/1 documented): ✔ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `resist0CombineAccumHelp(int, TwoValues)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `clone()` | — | ✔ |


##### `SmallestCombiner.java`

**Responsible for:** The SMALLEST combiner: reports the least of the values contributed, so that a property held at several strengths shows the weakest.

**class `SmallestCombiner`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | — | ✔ |
| `accum(int, int)` | — | ✔ |
| `smallestCombineAccumHelp(int, int)` | — | ✔ |
| `finish()` | — | ✔ |
| `vec(int, List<Integer>, List<Integer>)` | — | ✔ |
| `clone()` | — | ✔ |


## frontend

##### `SoundInterface.java`

**Responsible for:** Placeholder for the front-end sound subsystem.

**class `SoundInterface`** — class Javadoc: ✔ · type stamp: —


##### `SwingUI.java`

**Responsible for:** The Swing front end: owns the game's windows and nothing else.

**class `SwingUI`** — class Javadoc: ✔ · type stamp: —

- Fields (9/9 documented): ✔ `logger`, `uiChannel`, `edtChannel`, `terms`, `activeTermData`, `uiLoop`, `startupOptions`, `screen`, `windowListener`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `SwingUI(UIChannel, EDTChannel, StartupOptions)` | — | ✔ |
| `closeDown()` | — | ✔ |
| `sendStartToCore()` | — | ✔ |
| `getActiveWindow()` | — | ✔ |
| `getActiveTermData()` | on 260909 | ✔ |
| `init()` | — | ✔ |
| `startLoop()` | — | ✔ |

**class `SwingUI.JPanelArea`** — class Javadoc: ✔ · type stamp: —

- Fields (5/5 documented): ✔ `font`, `charWidth`, `charHeight`, `charAscent`, `display`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getDisplayWidth()` | on 260909 | ✔ |
| `getDisplayHeight()` | on 260909 | ✔ |
| ctor `JPanelArea()` | — | ✔ |
| `setChars(AngbandDisplayCharacter[][])` | — | ✔ |
| `paintComponent(Graphics)` | — | ✔ |
| `put(int, int, char, ColourEnum)` | on 260830 | ✔ |
| `put(int, int, String, ColourEnum)` | on 260830 | ✔ |


## frontend/colour

##### `Colour.java`

**Responsible for:** Runtime colour table for the display.

**class `Colour`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (5/5 documented): ✔ `maxColours`, `basicColours`, `colourTable`, `originalColours`, `currentColours`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Colour()` | on 260902 | ✔ |
| `init()` | on 260902 | ✔ |
| `getColour(ColourEnum)` | on 260902 | ✔ |


##### `ColourCycle.java`

**Responsible for:** One animated colour cycle: an ordered list of ColourEnum frames plus a name.

**class `ColourCycle`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (2/2 documented): ✔ `name`, `steps`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ColourCycle(String, List<ColourEnum>)` | before 260916 | ✔ |
| `colourCycle(String, List<ColourEnum>)` | before 260916 | ✔ |
| `getColour(int)` | before 260916 | ✔ |
| `getSize()` | before 260916 | ✔ |


##### `FlickerTable.java`

**Responsible for:** The flicker table: colour cycles keyed by base attribute .

**class `FlickerTable`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `byAttr`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `FlickerTable(Map<ColourEnum,ColourCycle>)` | — | ✔ |


##### `MonsterRaceCycler.java`

**Responsible for:** Per-race colour-cycle lookup, keyed by race name.

**class `MonsterRaceCycler`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `monsterCyclerByRace`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterRaceCycler()` | on 260902 | ✔ |
| `addCycler(String, ColourCycle)` | on 260902 | ✔ |
| `getCycler(String)` | on 260902 | ✔ |
| `getAttrForRace(String, int)` | on 260916 | ✔ |


##### `VisualsCycler.java`

**Responsible for:** The named-cycle table: colour cycles keyed by (group, name).

**class `VisualsCycler`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `byGroupThenName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `VisualsCycler(Map<String,Map<String,ColourCycle>>)` | — | ✔ |
| `getKeys()` | — | ✔ |
| `getByGroup(String)` | — | ✔ |


## frontend/entries

##### `UIEntry.java`

**Responsible for:** A single entry in the player status display (one stat or resistance line), ported from the C original's UI-entry system (src/ui-entry.c).

**class `UIEntry`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (13/13 documented): ✔ `name`, `template`, `elementParameter`, `statOrElementParameter`, `statParameter`, `renderer`, `combineType`, `priorityNum`, `priorityString`, `entryFlag`, `label`, `shortenedLabels`, `categories`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntry(String, UIEntryBase, ElementEnum, StatElemType, UIEntryRenderer, CombinerName, List<UIEntryCategory>, int, String, Flag<ChannelEntryFlag>, String, String, String, String)` | before 260916 | ✔ |
| `getName()` | before 260916 | ✔ |
| `getEntryFlag()` | before 260916 | ✔ |
| `uiEntryHasCategory(String)` | before 260916 | ✔ |
| `uiEntrySearchCategories(String)` | before 260916 | ✔ |
| `toString()` | before 260916 | ✔ |
| `getDefaultPriority()` | before 260916 | ✔ |
| `setDefaultPriority(int)` | before 260916 | ✔ |
| `getParameter()` | before 260916 | ✔ |
| `getRenderer()` | before 260916 | ✔ |
| `getCombineType()` | before 260916 | ✔ |
| `getPriorityNum()` | before 260916 | ✔ |
| `getStatOrElement()` | before 260916 | ✔ |
| `getLabel()` | before 260916 | ✔ |
| `entryFlagHas(ChannelEntryFlag)` | before 260916 | ✔ |
| `getLabel2()` | before 260916 | ✔ |
| `getCategories()` | before 260916 | ✔ |
| `getLabel5()` | before 260916 | ✔ |
| `setName(String)` | before 260916 | ✔ |
| `setPriorityNum(int)` | before 260916 | ✔ |
| `setLabel(String)` | before 260916 | ✔ |
| `setShortenedLabel(int, String)` | before 260916 | ✔ |
| `getShortenedLabel(int)` | before 260916 | ✔ |
| `setRenderer(UIEntryRenderer)` | before 260916 | ✔ |
| `setCombinerType(CombinerName)` | before 260916 | ✔ |
| `setParamIndex(int)` | before 260916 | ✔ |
| `setEntryFlags(FlagView<ChannelEntryFlag>)` | before 260916 | ✔ |
| `getNCategory()` | before 260916 | ✔ |
| `getStatParameter()` | before 260916 | ✔ |
| `setStatParameter(int)` | before 260916 | ✔ |
| `setElementParameter(ElementEnum)` | before 260916 | ✔ |
| `setStatOrElement(StatElemType)` | before 260916 | ✔ |
| `setLabel2(String)` | before 260916 | ✔ |
| `setLabel5(String)` | before 260916 | ✔ |
| `copy()` | before 260916 | ✔ |
| `setCategores(List<UIEntryCategory>)` | before 260916 | ✔ |
| `getTemplate()` | before 260916 | ✔ |
| `setTemplate(UIEntryBase)` | before 260916 | ✔ |
| `getPriorityString()` | before 260916 | ✔ |
| `setPriorityString(String)` | before 260916 | ✔ |


##### `UIEntryBase.java`

**Responsible for:** A reusable template for UIEntry definitions: the shared defaults (renderer, combiner, categories, flags) that concrete entries inherit from.

**class `UIEntryBase`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (6/6 documented): ✔ `logger`, `name`, `renderer`, `combine`, `categories`, `flags`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryBase(String, UIEntryRenderer, CombinerName, List<String>, Flag<ChannelEntryFlag>, String)` | before 260916 | ✔ |
| `getName()` | before 260916 | ✔ |
| `toString()` | before 260916 | ✔ |
| `getRenderer()` | before 260916 | ✔ |
| `getCombine()` | before 260916 | ✔ |
| `getCategories()` | before 260916 | ✔ |
| `getFlags()` | — | ✔ |


##### `UIEntryCategory.java`

**Responsible for:** One resolved (category name, priority) pairing for a uk.co.jackoftradesltd.frontend.entries.UIEntry, the Java form of C's struct category_reference ([C] ui-entry.c:55-59: name, priority, priority_set).

**class `UIEntryCategory`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (3/3 documented): ✔ `name`, `priority`, `prioritySet`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryCategory(String, int, boolean)` | before 260916 | ✔ |
| `getName()` | before 260916 | ✔ |
| `getPriority()` | before 260916 | ✔ |
| `isPrioritySet()` | before 260916 | ✔ |
| `setName(String)` | — | ✘ |
| `setPriority(int)` | — | ✘ |
| `setPrioritySet(boolean)` | — | ✘ |


##### `UIEntryIterator.java`

**Responsible for:** Walks a subset of the registered UIEntry elements in priority order — the port of C's struct ui_entry_iterator (ui-entry.c:41-44) together with its free functions initialize_ui_entry_iterator, reset_ui_entry_iterator, count_ui_entry_iterator, and advance_ui_entry_iterator (ui-entry.c:461-524).

**class `UIEntryIterator`** — class Javadoc: ✔ · type stamp: on 260916

- Fields (2/2 documented): ✔ `entries`, `index`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryIterator(List<UIEntry>, int, int)` | on 260916 | ✔ |
| ctor `UIEntryIterator(List<UIEntry>)` | on 260916 | ✔ |
| ctor `UIEntryIterator()` | on 260916 | ✔ |
| `getEntries()` | on 260916 | ✔ |
| `getNum()` | on 260916 | ✔ |
| `getIndex()` | on 260916 | ✔ |
| `addEntry(UIEntry)` | on 260916 | ✔ |
| `advance()` | on 260916 | ✔ |


##### `UIEntryRenderer.java`

**Responsible for:** A concrete, named renderer instance for a UI status entry — the data-file counterpart of the built-in UIEntryRendererEnum defaults.

**class `UIEntryRenderer`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (7/7 documented): ✔ `name`, `code`, `colours`, `labelColours`, `symbols`, `nDigit`, `sign`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryRenderer(String, UIEntryRendererEnum, String, String, String, int, UIEntryEnum)` | before 260916 | ✔ |
| `toString()` | before 260916 | ✔ |
| `getName()` | before 260916 | ✔ |
| `getCode()` | before 260916 | ✔ |
| `getColours()` | before 260916 | ✔ |
| `getLabelColours()` | before 260916 | ✔ |
| `getSymbols()` | before 260916 | ✔ |
| `getnDigit()` | before 260916 | ✔ |
| `getSign()` | before 260916 | ✔ |


## frontend/entries/enums

##### `UIEntryEnum.java`

**Responsible for:** Sign-display modes for a UI entry's numeric value, choosing whether and when a +/- is shown.

**enum `UIEntryEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `UI_ENTRY_NO_SIGN`, `UI_ENTRY_ALWAYS_SIGN`, `UI_ENTRY_NEGATIVE_SIGN`, `UI_ENTRY_SIGN_DEFAULT`


##### `UIEntryRendererEnum.java`

**Responsible for:** The built-in renderers used to draw UI status entries, one constant per row of the C original's list-ui-entry-renderers.h table (compiled into the backends[] array of struct backend_info in ui-entry-renderers.c), plus a Java-only #UI_ENTRY_RENDERER_NONE sentinel with no C row of its own.

**enum `UIEntryRendererEnum`** — class Javadoc: ✔ · type stamp: before 260916

- Enum constants (0/7 documented): ✘ `UI_ENTRY_RENDERER_NONE`, `UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX`, `UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX`, `UI_ENTRY_RENDERER_COMPACT_FLAG_WITH_CANCEL_RENDERER_WITH_COMBINED_AUX`, `UI_ENTRY_RENDERER_NUMERIC_AS_SIGN_RENDERER_WITH_COMBINED_AUX`, `UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_COMBINED_AUX`, `UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX`
- Fields (6/6 documented): ✔ `combiner`, `defaultColours`, `defaultLabelColours`, `defaultSymbols`, `defaultDigits`, `entry`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryRendererEnum(CombinerName, String, String, String, int, UIEntryEnum)` | before 260916 | ✔ |
| `getDefaultDigits()` | before 260916 | ✔ |
| `getEntry()` | before 260916 | ✔ |
| `getDefaultColours()` | before 260916 | ✔ |
| `getDefaultLabelColours()` | before 260916 | ✔ |
| `getDefaultSymbols()` | before 260916 | ✔ |


## frontend/events

##### `BirthEvents.java`

**Responsible for:** The front end's half of character creation's two ends: putting the birth screen up, and taking it down once a character exists.

**class `BirthEvents`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `BirthEvents()` | — | ✔ |
| `enterBirth(GameEventData)` | — | ✔ |
| `leaveBirth()` | — | ✔ |


##### `Event.java`

**Responsible for:** Marker/base type for UI events, carrying the set of UiEventTypes an event may represent.

**interface `Event`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `uiEventType`


##### `MainEvents.java`

**Responsible for:** The front end's half of the session's phase transitions: what the screen does when the data load ends, when a character starts playing, and when a level is entered or left.

**class `MainEvents`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MainEvents()` | — | ✔ |
| `leaveInit()` | — | ✔ |
| `enterGame()` | — | ✔ |
| `leaveGame()` | — | ✔ |
| `enterWorld()` | — | ✔ |
| `leaveWorld()` | — | ✔ |


## frontend/globals

##### `UIGlobals.java`

**Responsible for:** Static holder for the front end's globally-shared UI state, the Java port of the C original's module-scope text_out_indent global in z-textblock.c ([C] src/z-textblock.h:57-61).

**class `UIGlobals`** — class Javadoc: ✔ · type stamp: before 260911

- Fields (1/2 documented): ✔ `textOutIndent` — ✘ `activeTermData`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getActiveTermData()` | — | ✘ |
| `setActiveTermData(TermData)` | — | ✘ |
| `getTextOutIndent()` | before 260911 | ✔ |
| `setTextOutIndent(int)` | before 260911 | ✔ |


## frontend/inputfromuser

##### `TextUIHook.java`

**Responsible for:** The boundary through which the game engine asks the user interface for an interactive value - the port of C's get_*_hook indirection in game-input.c.

**class `TextUIHook`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getRepDir(boolean)` | — | ✔ |
| `getItem(String, String, CommandCode, Predicate<ItemObject>, Flag<GetItemFlags>)` | — | ✔ |
| `getAimDir()` | — | ✔ |
| `getQuantity(String, int)` | — | ✔ |
| `getDirection(String, DirectionEnum)` | — | ✔ |
| `getItem(String, ItemObject)` | — | ✔ |
| `getString(String, String)` | — | ✔ |
| `getTarget(String, int)` | — | ✔ |


##### `UILoop.java`

**Responsible for:** The UI half's body: the loop that drains this half's inbox and turns each message the core sent into painting.

**class `UILoop`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (4/4 documented): ✔ `logger`, `uiChannel`, `swingUI`, `screen`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UILoop(UIChannel, SwingUI, Screen)` | before 260916 | ✔ |
| `initAngbandAux(String)` | before 260916 | ✔ |
| `loop()` | before 260916 | ✔ |
| `writeInitString(String)` | before 260916 | ✔ |
| `onEventDispatchThread(Runnable)` | before 260916 | ✔ |


## frontend/screen

##### `Term.java`

**Responsible for:** The abstract terminal: a platform-independent model of a text display, ported from the C original's term struct ([C] src/ui-term.h).

**class `Term`** — class Javadoc: ✔ · type stamp: —

- Fields (47/47 documented): ✔ `user`, `data`, `userFlag`, `dataFlag`, `activeFlag`, `mappedFlag`, `totalErase`, `fixedShape`, `ickyCorner`, `softCursor`, `alwaysPict`, `higherPict`, `alwaysText`, `neverBored`, `neverFrosh`, `sidebarMode`, `complexInput`, `keyQueue`, `keyHead`, `keyTail`, `keyXtra`, `keySize`, `wid`, `hgt`, `y1`, `y2`, `x1`, `x2`, `offsetX`, `offsetY`, `old`, `scr`, `tmp`, `mem`, `saved`, `initHook`, `nukeHook`, `xtraHook`, `cursHook`, `bigcursHook`, `wipeHook`, `textHook`, `pictHook`, `viewMapHook`, `dblhHook`, `outputHook`, `owner`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `termInit(int, int, int, TermData, Screen)` | before 260929 | ✔ |
| `setSoftCursor(boolean)` | — | ✔ |
| `setHigherPict(boolean)` | — | ✔ |
| `setComplexInput(boolean)` | — | ✔ |
| `setXtraHook(TermEventHook)` | — | ✔ |
| `setCursHook(TermEventHook)` | — | ✔ |
| `setBigcursHook(TermEventHook)` | — | ✔ |
| `setWipeHook(TermEventHook)` | — | ✔ |
| `setTextHook(TermEventHook)` | — | ✔ |
| `setPictHook(TermEventHook)` | — | ✔ |
| `setDblhHook(TermEventHook)` | — | ✔ |
| `setViewMapHook(TermEventHook)` | — | ✔ |
| `setData(TermData)` | — | ✔ |
| `getTermData()` | — | ✔ |
| `gotoXY(int, int)` | on 260909 | ✔ |
| `cPutStr(ColourEnum, String, int, int)` | on 260910 | ✔ |
| `putStr(String, int, int)` | on 260910 | ✔ |
| `prt(String, int, int)` | on 260910 | ✔ |
| `cPrt(ColourEnum, String, int, int)` | on 260910 | ✔ |
| `termErase(int, int, int)` | on 260910 | ✔ |
| `putstr(int, int, int, ColourEnum, String)` | on 260910 | ✔ |
| `addstr(int, ColourEnum, String)` | on 260910 | ✔ |
| `clearFrom(int)` | on 260910 | ✔ |


##### `TermData.java`

**Responsible for:** Per-window front-end state for one terminal, pairing a logical Term with the Window that draws it and all the platform geometry (position, size, fonts, tile sizes) needed to lay it out.

**class `TermData`** — class Javadoc: ✔ · type stamp: before 260909

- Fields (27/27 documented): ✔ `screenTitle`, `t`, `window`, `screen`, `keys`, `rows`, `cols`, `posX`, `posY`, `sizeWidth`, `sizeHeight`, `sizeOW1`, `sizeOH1`, `sizeOW2`, `sizeOH2`, `sizeHack`, `xtraHack`, `visible`, `maximized`, `bizarre`, `fontWidth`, `fontHeight`, `tileWidth`, `tileHeight`, `mapTileWidth`, `mapTileHeight`, `mapActive`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TermData(Screen)` | before 260916 | ✔ |
| `isMapActive()` | before 260916 | ✔ |
| `getTileWidth()` | before 260916 | ✔ |
| `getTileHeight()` | before 260916 | ✔ |
| `getSizeOH2()` | before 260916 | ✔ |
| `getSizeOW2()` | before 260916 | ✔ |
| `getSizeOH1()` | before 260916 | ✔ |
| `getSizeOW1()` | before 260916 | ✔ |
| `termDataLink(Term)` | on 260916 | ✔ |
| `getTerm()` | on 260909 | ✔ |
| `getWindow()` | on 260909 | ✔ |
| `setWindow(Window)` | on 260909 | ✔ |
| `dispose()` | on 260909 | ✔ |


##### `TermWin.java`

**Responsible for:** One terminal's content buffer: the cursor state plus the grids of coloured characters that make up the display.

**class `TermWin`** — class Javadoc: ✔ · type stamp: —

- Fields (10/10 documented): ✔ `cu`, `cv`, `cx`, `cy`, `cnx`, `cny`, `a`, `va`, `ta`, `vta`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `init(int, int)` | on 260916 | ✔ |
| `getCu()` | on 260910 | ✔ |
| `setCu(boolean)` | on 260909 | ✔ |
| `getCx()` | on 260910 | ✔ |
| `setCx(int)` | on 260909 | ✔ |
| `getCy()` | on 260910 | ✔ |
| `setCy(int)` | on 260909 | ✔ |


##### `TextOut.java`

**Responsible for:** Formats and colours text before handing it to a TextOutHook sink, the Java port of the C original's text_out, text_out_c and text_out_e (z-textblock.c), and of the next_section helper the last of them uses.

**class `TextOut`** — class Javadoc: ✔ · type stamp: on 260910

- Fields (2/2 documented): ✔ `term`, `textOutHook`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TextOut(Term, TextOutHook)` | on 260910 | ✔ |
| `textOut(String, Object)` | on 260910 | ✔ |
| `textOutC(ColourEnum, String, Object)` | on 260910 | ✔ |
| `textOutE(String, Object)` | on 260910 | ✔ |
| `nextSection(String, int, String)` | on 260910 | ✔ |

**enum `TextOut.TagState`** — class Javadoc: ✔ · type stamp: on 260910

- Enum constants (4/4 documented): ✔ `START_TAG`, `END_TAG`, `IN_TAG_TEXT`, `IN_NORMAL_TEXT`

**record `TextOut.SectionDetails`** — class Javadoc: ✔ · type stamp: on 260910

- Record components (via `@param`) (4/4 documented): ✔ `found`, `text`, `tag`, `next`


##### `Window.java`

**Responsible for:** The Swing `JFrame` the front end draws into; keeps a reference to the `SwingUI.JPanelArea` added to it and shows/titles the frame on the EDT.

**class `Window`** — class Javadoc: ✘ · type stamp: —

- Fields (1/1 documented): ✔ `area`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Window()` | — | ✔ |
| `displayString(String)` | — | ✔ |
| `add(Component)` | — | ✔ |
| `getArea()` | — | ✔ |
| `show(Frame)` | before 260916 | ✔ |
| `getCharacterWidth()` | on 260909 | ✔ |
| `getCharacterHeight()` | on 260909 | ✔ |


## frontend/screen/enums

##### `Sidebar.java`

**Responsible for:** The placement options for the player status sidebar, mirroring the SIDEBAR_* layout modes of the C original.

**enum `Sidebar`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `SIDEBAR_LEFT`, `SIDEBAR_TOP`, `SIDEBAR_NONE`, `SIDEBAR_MAX`


##### `TermXtraEventEnum.java`

**Responsible for:** The set of "extra" terminal actions the core can request of a front end, mirroring the TERM_XTRA_* constants of the C original's terminal layer (src/z-term.h).

**enum `TermXtraEventEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (12/12 documented): ✔ `TERM_XTRA_EVENT`, `TERM_XTRA_FLUSH`, `TERM_XTRA_CLEAR`, `TERM_XTRA_SHAPE`, `TERM_XTRA_FROSH`, `TERM_XTRA_FRESH`, `TERM_XTRA_NOISE`, `TERM_XTRA_BORED`, `TERM_XTRA_REACT`, `TERM_XTRA_ALIVE`, `TERM_XTRA_LEVEL`, `TERM_XTRA_DELAY`


## frontend/screen/grid

##### `CellGrid.java`

**Responsible for:** The screen's data — a plain grid of coloured cells, with no Swing and no drawing logic of its own.

**class `CellGrid`** — class Javadoc: ✔ · type stamp: on 260909

- Fields (1/1 documented): ✔ `cells`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CellGrid(int, int)` | on 260909 | ✔ |
| `rows()` | on 260909 | ✔ |
| `cols()` | on 260909 | ✔ |
| `get(int, int)` | on 260909 | ✔ |
| `set(int, int, AngbandDisplayCharacter)` | on 260909 | ✔ |
| `copy()` | on 260909 | ✔ |
| `getCells()` | on 260909 | ✔ |


##### `Frame.java`

**Responsible for:** An immutable snapshot of the screen, handed from the UI thread to the event dispatch thread - the boundary crossing this drawing architecture is built around, set out in docs/UIPanelArchitecture.md.

**record `Frame`** — class Javadoc: ✔ · type stamp: on 260909

- Record components (via `@param`) (2/2 documented): ✔ `grid`, `hotspots`


##### `Hotspot.java`

**Responsible for:** A clickable rectangle on the screen, published alongside the Frame it was drawn into.

**record `Hotspot`** — class Javadoc: ✔ · type stamp: on 260909

- Record components (via `@param`) (5/5 documented): ✔ `top`, `left`, `rows`, `cols`, `event`


##### `Region.java`

**Responsible for:** A rectangle into a CellGrid that translates and clips coordinates - the operations layer of the drawing architecture set out in docs/UIPanelArchitecture.md.

**class `Region`** — class Javadoc: ✔ · type stamp: on 260909

- Fields (2/5 documented): ✔ `grid`, `top` — ✘ `left`, `rows`, `cols`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Region(CellGrid, int, int, int, int)` | on 260909 | ✔ |
| `put(int, int, AngbandDisplayCharacter)` | on 260909 | ✔ |
| `put(int, int, char, ColourEnum)` | on 260909 | ✔ |
| `put(int, int, String, ColourEnum)` | on 260909 | ✔ |
| `erase(int, int, int)` | on 260909 | ✔ |
| `clear()` | on 260909 | ✔ |
| `cols()` | on 260909 | ✔ |
| `rows()` | on 260909 | ✔ |


##### `Screen.java`

**Responsible for:** The owner of the screen's live state, and the only route out of it — the third of the three concerns recorded in docs/UIPanelArchitecture.md: CellGrid is the data, Region is the operations, and this class is the boundary between the thread that composes a screen and the thread that paints it.

**class `Screen`** — class Javadoc: ✔ · type stamp: on 260909

- Fields (3/3 documented): ✔ `live`, `hotspots`, `frame`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Screen(CellGrid, List<Hotspot>)` | on 260909 | ✔ |
| `root()` | on 260909 | ✔ |
| `frame()` | on 260909 | ✔ |
| `splashScreenNote(String)` | on 260910 | ✔ |
| `onEventDispatchThread(Runnable)` | — | ✔ |


## frontend/screen/handlers

##### `HandlerFunctions.java`

**Responsible for:** Empty placeholder class for screen-handler functions; no members yet.

**class `HandlerFunctions`** — class Javadoc: ✘ · type stamp: —


##### `HandlersHolder.java`

**Responsible for:** The Java port of C's side_handlers[] table ([C] ui-display.c) - the sidebar rows that redraw themselves in response to a game_event_type flag, as opposed to the "short" topbar path (update_topbar, [C] ui-display.c), which this class does not cover.

**class `HandlersHolder`** — class Javadoc: ✔ · type stamp: on 260927

- Fields (2/2 documented): ✔ `sideHandlers`, `term`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `initHandlers()` | on 260927 | ✔ |
| `prtDepth(int, int)` | on 260929 | ✔ |
| `fmtDepths(int)` | on 260929 | ✔ |
| `prtSpeed(int, int)` | on 260929 | ✔ |
| `prtSpeedAux(int)` | on 260929 | ✔ |
| `prtHealth(int, int)` | before 260929 | ✔ |
| `prtHealthAux(int, int)` | on 260915 | ✔ |
| `monsterHealthAttr()` | on 260915 | ✔ |
| `prtSp(int, int)` | on 260928 | ✔ |
| `playerSPAttr()` | on 260928 | ✔ |
| `prtHp(int, int)` | on 260928 | ✔ |
| `playerHpAttr()` | on 260928 | ✔ |
| `prtAc(int, int)` | on 260927 | ✔ |
| `prtStr(int, int)` | on 260927 | ✔ |
| `prtInt(int, int)` | on 260927 | ✔ |
| `prtWis(int, int)` | on 260927 | ✔ |
| `prtDex(int, int)` | on 260927 | ✔ |
| `prtCon(int, int)` | on 260927 | ✔ |
| `prtStat(int, int, int)` | on 260927 | ✔ |
| `prtEquippy(int, int)` | on 260927 | ✔ |
| `prtGold(int, int)` | on 260927 | ✔ |
| `prtExp(int, int)` | on 260927 | ✔ |
| `prtLevel(int, int)` | on 260927 | ✔ |
| `prtTitle(int, int)` | on 260927 | ✔ |
| `prtClass(int, int)` | on 260927 | ✔ |
| `fmtTitle(int, boolean)` | on 260927 | ✔ |
| `prtRace(int, int)` | on 260927 | ✔ |
| `prtField(String, int, int)` | on 260927 | ✔ |
| `setTermData(TermData)` | on 260927 | ✔ |

**record `HandlersHolder.ColourAndString`** — class Javadoc: ✔ · type stamp: on 260929

- Record components (via `@param`) (2/2 documented): ✔ `colour`, `string`

**interface `HandlersHolder.prtFunction`** — class Javadoc: ✔ · type stamp: on 260927

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `apply(T, U)` | — | ✘ |


##### `SideHandler.java`

**Responsible for:** The Java port of C's anonymous side_handler_t struct ([C] ui-display.c), which pairs a sidebar-row drawing hook with the priority that decides whether it is drawn when space is tight and the game_event_type that triggers it.

**class `SideHandler`** — class Javadoc: ✔ · type stamp: on 260927

- Fields (3/3 documented): ✔ `hook`, `priority`, `type`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `SideHandler(HandlersHolder.prtFunction<Integer,Integer>, int, GameEventType)` | on 260927 | ✔ |
| `getPriority()` | on 260927 | ✔ |
| `getResult(Integer, Integer)` | on 260927 | ✔ |
| `getType()` | on 260927 | ✔ |


## frontend/screen/hooks

##### `TermEventHook.java`

**Responsible for:** Callback contract for handling a terminal "extra" event.

**interface `TermEventHook`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doSomething(TermXtraEventEnum, int)` | — | ✔ |


##### `TermScreenHook.java`

**Responsible for:** The live TermTextHook implementation, painting directly onto a Screen's root uk.co.jackoftradesltd.frontend.screen.grid.Region.

**class `TermScreenHook`** — class Javadoc: ✔ · type stamp: on 260910

- Fields (1/1 documented): ✔ `screen`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TermScreenHook(Screen)` | on 260910 | ✔ |
| `putStr(int, int, int, ColourEnum, String)` | on 260910 | ✔ |
| `erase(int, int, int)` | on 260910 | ✔ |


##### `TermTextHook.java`

**Responsible for:** The Java counterpart to a C struct term's hook table ([C] src/ui-term.h), narrowed to the two low-level driver primitives that table actually exposes: text_hook (this interface's #putStr) and wipe_hook (#erase).

**interface `TermTextHook`** — class Javadoc: ✔ · type stamp: on 260910

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `putStr(int, int, int, ColourEnum, String)` | on 260910 | ✔ |
| `erase(int, int, int)` | on 260910 | ✔ |


##### `TermXtraWin.java`

**Responsible for:** Windows-style implementation of the TermEventHook "extra event" handler — the Java/JavaFX port of the C original's Term_xtra_win (src/main-win.c).

**class `TermXtraWin`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doSomething(TermXtraEventEnum, int)` | — | ✔ |
| `termXtraDelay(int)` | — | ✔ |
| `termXtraWinReact()` | — | ✔ |
| `termXtraWinClear()` | — | ✔ |
| `termXtraWinFlush()` | — | ✔ |
| `termXtraWinEvent(int)` | — | ✔ |
| `termXtraWinNoise(MessageBoxFlags)` | on 260916 | ✔ |


##### `TermXtraWinDelay.java`

**Responsible for:** A TermEventHook implementation dedicated to delay handling only, the Java counterpart to C's Term_xtra_win_delay(int v) ([C] src/main-win.c), the handler Term_xtra_win dispatches to for TERM_XTRA_DELAY.

**class `TermXtraWinDelay`** — class Javadoc: ✔ · type stamp: on 260916

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doSomething(TermXtraEventEnum, int)` | on 260916 | ✔ |


##### `TextOutHook.java`

**Responsible for:** Sink for coloured text emitted by the game.

**interface `TextOutHook`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `output(ColourEnum, String)` | — | ✔ |
| `out(ColourEnum, String, Object)` | — | ✔ |
| `out(String, Object)` | — | ✔ |


## frontend/screen/splash

##### `SplashScreen.java`

**Responsible for:** The title screen: the front end's main window, which paints lib/screens/news.txt into the game window while the data files load.

**class `SplashScreen`** — class Javadoc: ✔ · type stamp: on 260813

- Fields (4/4 documented): ✔ `logger`, `birthLine`, `activeWindow`, `display`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `SplashScreen(SwingUI)` | on 260813 | ✔ |
| `readAndParse(Path, Screen)` | on 260909 | ✔ |
| `onEventDispatchThread(Runnable)` | on 260813 | ✔ |
| `splashScreenNote(String, Screen)` | on 260909 | ✔ |
| `splashScreenBirthNote(String)` | on 260813 | ✔ |

**enum `SplashScreen.SplashScreenState`** — class Javadoc: ✔ · type stamp: on 260813

- Enum constants (0/5 documented): ✘ `START_TAG`, `END_TAG`, `IN_COLOUR_TEXT`, `IN_NORMAL_TEXT`, `VERSION`


## frontend/sounds

##### `MessageBoxFlags.java`

**Responsible for:** Mirrors the Win32 MessageBox MB_* flag constants, carried over from the original Windows front end.

**enum `MessageBoxFlags`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/29 documented): ✘ `MB_OK`, `MB_OKCANCEL`, `MB_ABORTRETRYIGNORE`, `MB_YESNOCANCEL`, `MB_YESNO`, `MB_RETRYCANCEL`, `MB_CANCELTRYCONTINUE`, `MB_ICONHAND`, `MB_ICONQUESTION`, `MB_ICONEXCLAMATION`, `MB_ICONASTERISK`, `MB_USERICON`, `MB_ICONWARNING`, `MB_ICONERROR`, `MB_ICONINFORMATION`, `MB_ICONSTOP`, `MB_DEFBUTTON1`, `MB_DEFBUTTON2`, `MB_DEFBUTTON3`, `MB_DEFBUTTON4`, `MB_APPLMODAL`, `MB_SYSTEMMODAL`, `MB_TASKMODAL`, `MB_HELP`, `MB_SETFOREGROUND`, `MB_DEFAULT_DESKTOP_ONLY`, `MB_TOPMOST`, `MB_RIGHT`, `MB_RTLREADING`
- Fields (1/1 documented): ✔ `file`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MessageBoxFlags(String)` | — | ✔ |
| `getFileName()` | before 260916 | ✔ |


## frontend/ui

##### `RedrawRouter.java`

**Responsible for:** The UI half's dispatcher from a decoded redraw payload to the model that owns the value it carries.

**class `RedrawRouter`** — class Javadoc: ✔ · type stamp: on 260926

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setHP(GameEventData)` | on 260926 | ✔ |
| `setSP(GameEventData)` | on 260926 | ✔ |
| `setTitle(GameEventData)` | before 260926 | ✔ |
| `setName(GameEventData)` | before 260926 | ✔ |
| `setRaceClass(GameEventData)` | before 260926 | ✔ |
| `setPlayerLevel(GameEventData)` | on 260927 | ✔ |
| `setExperience(GameEventData)` | on 260927 | ✔ |
| `setGold(GameEventData)` | on 260927 | ✔ |
| `setEquippy(GameEventData)` | on 260927 | ✔ |
| `setStats(GameEventData)` | on 260927 | ✔ |
| `setAC(GameEventData)` | on 260927 | ✔ |
| `setMonsterHealth(GameEventData)` | on 260929 | ✔ |
| `setPlayerSpeed(GameEventData)` | on 260929 | ✔ |
| `setPlayerDepth(GameEventData)` | on 260929 | ✔ |


##### `SidebarModel.java`

**Responsible for:** The UI-owned model for the character sidebar's hit-point row — the front end's replacement for reading player->chp/mhp straight off the shared player global, the way C's prt_hp ([C] ui-display.c, function prt_hp) and get_panel_topleft ([C] ui-player.c, function get_panel_topleft) both do at their...

**class `SidebarModel`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (44/44 documented): ✔ `logger`, `currentHP`, `maxHP`, `currentSP`, `maxSP`, `title`, `className`, `raceName`, `name`, `wizard`, `isTotalWinner`, `shapeName`, `level`, `maxLevel`, `experience`, `xpToLevel`, `maxXp`, `playerIsShapechanged`, `gold`, `equipString`, `currentStats`, `maxStats`, `useStats`, `ac`, `playerOptHPWarn`, `magic`, `firstSpell`, `monHealth`, `monMaxHealth`, `monExists`, `monVisible`, `monFeared`, `monDisen`, `monCommand`, `monConf`, `monStun`, `monSlept`, `monHeld`, `playerTmdImage`, `playerSpeed`, `playerEffectiveSpeed`, `energy`, `energyNormal`, `playerDepth`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getPlayerDepth()` | on 260929 | ✔ |
| `setPlayerDepth(int)` | on 260929 | ✔ |
| `getPlayerEffectiveSpeed()` | on 260929 | ✔ |
| `setPlayerEffectiveSpeed(boolean)` | on 260929 | ✔ |
| `getEnergy()` | on 260929 | ✔ |
| `setEnergy(int)` | on 260929 | ✔ |
| `getEnergyNormal()` | on 260929 | ✔ |
| `setEnergyNormal(int)` | on 260929 | ✔ |
| `getPlayerSpeed()` | on 260929 | ✔ |
| `setPlayerSpeed(int)` | on 260929 | ✔ |
| `isPlayerTmdImage()` | on 260929 | ✔ |
| `setPlayerTmdImage(boolean)` | on 260929 | ✔ |
| `isMonHeld()` | on 260929 | ✔ |
| `setMonHeld(boolean)` | on 260929 | ✔ |
| `isMonSlept()` | on 260929 | ✔ |
| `setMonSlept(boolean)` | on 260929 | ✔ |
| `isMonStunned()` | on 260929 | ✔ |
| `setMonStunned(boolean)` | on 260929 | ✔ |
| `isMonConf()` | on 260929 | ✔ |
| `setMonConf(boolean)` | on 260929 | ✔ |
| `isMonCommand()` | on 260929 | ✔ |
| `setMonCommand(boolean)` | on 260929 | ✔ |
| `isMonDisen()` | on 260929 | ✔ |
| `setMonDisen(boolean)` | on 260929 | ✔ |
| `isMonFeared()` | on 260929 | ✔ |
| `setMonFeared(boolean)` | on 260929 | ✔ |
| `isMonVisible()` | on 260929 | ✔ |
| `setMonVisible(boolean)` | on 260929 | ✔ |
| `monExists()` | on 260929 | ✔ |
| `setMonExists(boolean)` | on 260929 | ✔ |
| `getMonMaxHealth()` | on 260929 | ✔ |
| `setMonMaxHealth(int)` | on 260929 | ✔ |
| `getMonHealth()` | on 260929 | ✔ |
| `setMonHealth(int)` | on 260929 | ✔ |
| `getFirstSpell()` | on 260928 | ✔ |
| `setFirstSpell(int)` | on 260928 | ✔ |
| `hasMagic()` | on 260928 | ✔ |
| `setMagic(boolean)` | on 260928 | ✔ |
| `getPlayerOptHPWarn()` | on 260928 | ✔ |
| `setPlayerOptHPWarn(int)` | on 260928 | ✔ |
| `getAc()` | on 260927 | ✔ |
| `setAc(int)` | on 260927 | ✔ |
| `getMaxStat(int)` | on 260927 | ✔ |
| `getCurrentStat(int)` | on 260927 | ✔ |
| `getUseStat(int)` | on 260927 | ✔ |
| `setUseStat(int, int)` | on 260927 | ✔ |
| `setCurrentStat(int, int)` | on 260927 | ✔ |
| `setMaxStat(int, int)` | on 260927 | ✔ |
| `getEquippyString()` | on 260927 | ✔ |
| `setEquippyString(AngbandDisplayCharacter[])` | on 260927 | ✔ |
| `getGold()` | on 260927 | ✔ |
| `setGold(long)` | on 260927 | ✔ |
| `getMaxXp()` | on 260927 | ✔ |
| `setMaxXp(long)` | on 260927 | ✔ |
| `getXpToLevel()` | on 260927 | ✔ |
| `setXpToLevel(long)` | on 260927 | ✔ |
| `getExperience()` | on 260927 | ✔ |
| `setExperience(long)` | on 260927 | ✔ |
| `getMaxLevel()` | on 260927 | ✔ |
| `setMaxLevel(int)` | on 260927 | ✔ |
| `getLevel()` | on 260927 | ✔ |
| `setLevel(int)` | on 260927 | ✔ |
| `getShapeName()` | on 260927 | ✔ |
| `setShapeName(String)` | on 260927 | ✔ |
| `isTotalWinner()` | on 260927 | ✔ |
| `setTotalWinner(boolean)` | on 260927 | ✔ |
| `isWizard()` | on 260927 | ✔ |
| `setWizard(boolean)` | on 260927 | ✔ |
| `getCurrentHP()` | on 260926 | ✔ |
| `setCurrentHP(int)` | on 260926 | ✔ |
| `getMaxHP()` | on 260926 | ✔ |
| `setMaxHP(int)` | on 260926 | ✔ |
| `getCurrentSP()` | on 260926 | ✔ |
| `setCurrentSP(int)` | on 260926 | ✔ |
| `getMaxSP()` | on 260926 | ✔ |
| `setMaxSP(int)` | on 260926 | ✔ |
| `getClassName()` | before 260926 | ✔ |
| `setClassName(String)` | before 260926 | ✔ |
| `getName()` | before 260926 | ✔ |
| `setName(String)` | before 260926 | ✔ |
| `getRaceName()` | before 260926 | ✔ |
| `setRaceName(String)` | before 260926 | ✔ |
| `getTitle()` | before 260926 | ✔ |
| `setTitle(String)` | before 260926 | ✔ |
| `isPlayerIsShapechanged()` | on 260927 | ✔ |
| `setPlayerIsShapechanged(boolean)` | on 260927 | ✔ |


##### `UIEntryCode.java`

**Responsible for:** Builds UIEntryIterators over UIRegistry's entries - the port of C's initialize_ui_entry_iterator and its private sort comparator cmp_desc_prio ([C] ui-entry.c:392-483).

**class `UIEntryCode`** — class Javadoc: ✔ · type stamp: before 260924

- Fields (1/1 documented): ✔ `sortCategoryName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `initializeUIEntryIterator(BiPredicate<String[],UIEntry>, String[], String)` | before 260924 | ✔ |
| `sortFunction(UIEntry, UIEntry)` | before 260924 | ✔ |


##### `UIPlayer.java`

**Responsible for:** Character-sheet display driver — the partial Java port of C's ui-player.c ("character screens and dumps").

**class `UIPlayer`** — class Javadoc: ✔ · type stamp: before 260925

- Fields (6/6 documented): ✔ `firstCallOfGetEntryLabel`, `spc`, `colourTable`, `panels`, `cachedConfig`, `termData`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIPlayer()` | on 260925 | ✔ |
| `checkForTwoCategories(String[], UIEntry)` | before 260925 | ✔ |
| `setTermData(TermData)` | before 260925 | ✔ |
| `cnvStat(int, int)` | before 260925 | ✔ |
| `panelAllocate(int)` | before 260925 | ✔ |
| `getPanelTopLeft()` | on 260925 | ✔ |
| `getPanelMisc()` | on 260926 | ✔ |
| `showDepth()` | before 260925 | ✔ |
| `showAdvanceExperience()` | before 260925 | ✔ |
| `getPanelMidLeft()` | before 260925 | ✔ |
| `getPanelCombat()` | on 260926 | ✔ |
| `showSpeed()` | before 260926 | ✔ |
| `likert(int, int)` | on 260926 | ✔ |
| `getPanelSkills()` | before 260926 | ✔ |
| `maxColour(int, int)` | on 260925 | ✔ |
| `maxColour(long, long)` | on 260925 | ✔ |
| `weightRemaining()` | on 260925 | ✔ |
| `displayPlayer(PlayerDisplayMode)` | before 260925 | ✔ |
| `displayPlayerStatInfo()` | before 260925 | ✔ |
| `configureCharSheet()` | before 260925 | ✔ |
| `getUIEntryLabel(UIEntry, int, boolean, String)` | before 260925 | ✔ |
| `haveValidCharSheetConfig()` | on 260911 | ✔ |

**enum `UIPlayer.PlayerDisplayMode`** — class Javadoc: ✔ · type stamp: before 260925

- Enum constants (2/2 documented): ✔ `DISPLAY_FULL`, `DISPLAY_EXTRA`

**record `UIPlayer.StringAndColour`** — class Javadoc: ✔ · type stamp: on 260926

- Record components (via `@param`) (0/2 documented): ✘ `string`, `attr`

**class `UIPlayer.Panel`** — class Javadoc: ✔ · type stamp: before 260925

- Fields (4/4 documented): ✔ `logger`, `len`, `max`, `lines`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setLen(int)` | before 260925 | ✔ |
| `setMax(int)` | before 260925 | ✔ |
| `initLines()` | before 260925 | ✔ |
| `panelLine(ColourEnum, String, String, Object)` | on 260925 | ✔ |
| `space()` | on 260926 | ✔ |

**class `UIPlayer.PanelLine`** — class Javadoc: ✔ · type stamp: before 260925

- Fields (3/3 documented): ✔ `attribute`, `label`, `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PanelLine(ColourEnum, String, String)` | on 260925 | ✔ |
| `getAttribute()` | before 260925 | ✔ |
| `getLabel()` | before 260925 | ✔ |
| `getValue()` | before 260925 | ✔ |

**class `UIPlayer.PanelRegions`** — class Javadoc: ✔ · type stamp: before 260925

- Fields (3/3 documented): ✔ `bounds`, `alignLeft`, `panelFunc`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PanelRegions(Region, boolean, Supplier<Panel>)` | before 260925 | ✔ |
| `getBounds()` | before 260925 | ✔ |
| `isAlignLeft()` | before 260925 | ✔ |
| `getPanel()` | before 260925 | ✔ |


## frontend/ui/entry

##### `EmbryonicCategoryReferencies.java`

**Responsible for:** One in-progress (category, priority) pairing for a ui_entry.txt/ui_entry_base.txt record that has not yet finished parsing, the Java form of C's struct embryonic_category_reference ([C] ui-entry.c:163-168: name, psource_index, priority, priority_set).

**class `EmbryonicCategoryReferencies`** — class Javadoc: ✔ · type stamp: before 260920

- Fields (4/4 documented): ✔ `prioritySet`, `category`, `pSourceIndex`, `priority`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EmbryonicCategoryReferencies(UIEntryCategory, UIEntryPriorityScheme, int, boolean)` | before 260920 | ✔ |
| `compareTo(EmbryonicCategoryReferencies)` | before 260920 | ✔ |
| `getCategory()` | before 260920 | ✔ |
| `getpSourceIndex()` | before 260920 | ✔ |
| `getPriority()` | before 260920 | ✔ |
| `isPrioritySet()` | before 260920 | ✔ |
| `setPrioritySet(boolean)` | before 260920 | ✔ |
| `setCategory(UIEntryCategory)` | before 260920 | ✔ |
| `setpSourceIndex(UIEntryPriorityScheme)` | before 260920 | ✔ |
| `setPriority(int)` | before 260920 | ✔ |


##### `UIEntryEmbryo.java`

**Responsible for:** A ui_entry.txt/ui_entry_base.txt record still being assembled, the Java form of C's struct embryonic_ui_entry ([C] ui-entry.c:170-177: entry, categories, param_index, psource_index, last_category_index, exists).

**class `UIEntryEmbryo`** — class Javadoc: ✔ · type stamp: before 260920

- Fields (6/6 documented): ✔ `uiEntry`, `categories`, `parmIndex`, `pSourceIndex`, `lastCategoryIndex`, `exists`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryEmbryo(UIEntry, List<EmbryonicCategoryReferencies>, UIEntryNameParameter, UIEntryPriorityScheme, EmbryonicCategoryReferencies, boolean)` | before 260920 | ✔ |
| `getUiEntry()` | before 260920 | ✔ |
| `setUiEntry(UIEntry)` | before 260920 | ✔ |
| `getCategories()` | before 260920 | ✔ |
| `setCategories(List<EmbryonicCategoryReferencies>)` | before 260920 | ✔ |
| `addCategories(EmbryonicCategoryReferencies)` | before 260920 | ✔ |
| `getParmIndex()` | before 260920 | ✔ |
| `setParmIndex(UIEntryNameParameter)` | before 260920 | ✔ |
| `getPSourceIndex()` | before 260920 | ✔ |
| `setpSourceIndex(UIEntryPriorityScheme)` | before 260920 | ✔ |
| `getLastCategoryIndex()` | before 260920 | ✔ |
| `setLastCategoryIndex(EmbryonicCategoryReferencies)` | before 260920 | ✔ |
| `exists()` | before 260920 | ✔ |
| `setExists(boolean)` | before 260920 | ✔ |


## frontend/ui/entry/assembler

##### `UIEntryAssembler.java`

**Responsible for:** Second stage of the ui_entry.txt pipeline: turns the raw string UIEntryParseRecords emitted by the grammar into fully resolved UIEntry objects and folds them into the single shared registry list, alongside the ui_entry_base.txt-derived placeholders UIEntryBaseAssembler already produced there - on...

**class `UIEntryAssembler`** — class Javadoc: ✔ · type stamp: before 260922

- Fields (2/2 documented): ✔ `logger`, `embryo`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildCategories(List<String>, List<String>, int, int)` | before 260922 | ✔ |
| `assemble(List<UIEntryParseRecord>, List<String>)` | before 260922 | ✔ |
| `parseEachEntry(List<UIEntry>, UIEntry)` | before 260922 | ✔ |


##### `UIEntryParseRecord.java`

**Responsible for:** Immutable data-transfer object for a single name: block of lib/gamedata/ui_entry.txt, produced by the UIEntryGrammar parser and consumed by UIEntryAssembler.

**record `UIEntryParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (15/15 documented): ✔ `name`, `template`, `label`, `label5`, `label2`, `categoriesBeforePriority`, `parameter`, `renderer`, `combine`, `priority`, `categoriesAfterPriority`, `flags`, `desc`, `nameTag`, `line`


## frontend/ui/entry/assembler/helperfunctions

##### `HelperFunctions.java`

**Responsible for:** Static holders for the small lookup functions uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryNameParameter and uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme wrap, the Java form of the count_func/ith_name_func pairs in C's name_pa...

**class `HelperFunctions`** — class Javadoc: ✔ · type stamp: before 260920

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getDummyParamName(int)` | before 260920 | ✔ |
| `getDummyParamCount()` | before 260920 | ✔ |
| `getElementName(int)` | before 260920 | ✔ |
| `getElementCount()` | before 260920 | ✔ |
| `getStatName(int)` | before 260920 | ✔ |
| `getStatCount()` | before 260920 | ✔ |
| `getDummyPriority(int)` | before 260920 | ✔ |
| `getPriorityFromIndex(int)` | before 260920 | ✔ |
| `getPriorityFromNegativeIndex(int)` | before 260920 | ✔ |


##### `UIEntryNameParameter.java`

**Responsible for:** The name-parameterization schemes a ui_entry.txt record's parameter: directive can select, one constant per row of C's name_parameters[] table ([C] ui-entry.c:142-146), matched by the directive's raw text against #getName().

**enum `UIEntryNameParameter`** — class Javadoc: ✔ · type stamp: before 260924

- Enum constants (3/3 documented): ✔ `ENTRY_NAME_PARAMETER_NONE`, `ENTRY_NAME_PARAMETER_ELEMENT`, `ENTRY_NAME_PARAMETER_STAT`
- Fields (3/3 documented): ✔ `name`, `nameResolver`, `countResolver`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryNameParameter(String, Supplier<Integer>, Function<Integer,String>)` | before 260924 | ✔ |
| `getName()` | before 260924 | ✔ |
| `getCount()` | before 260924 | ✔ |
| `getParamName(int)` | before 260924 | ✔ |


##### `UIEntryPriorityScheme.java`

**Responsible for:** The automatic-priority schemes a priority: directive can select instead of a literal number, one constant per row of C's priority_schemes[] table ([C] ui-entry.c:157-161), matched by the directive's raw text against #getName().

**enum `UIEntryPriorityScheme`** — class Javadoc: ✔ · type stamp: before 260924

- Enum constants (3/3 documented): ✔ `PRIORITY_SCHEME_NONE`, `PRIORITY_SCHEME_INDEX`, `PRIORITY_SCHEME_NEGATIVE_INDEX`
- Fields (2/2 documented): ✔ `name`, `priorityResolver`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIEntryPriorityScheme(String, Function<Integer,Integer>)` | before 260924 | ✔ |
| `getName()` | before 260924 | ✔ |
| `getPriority(int)` | before 260924 | ✔ |


## frontend/ui/entry/reader

##### `UIEntryReader.java`

**Responsible for:** Loads lib/gamedata/ui_entry.txt into UIEntry objects, the thin hand-written bridge between the generated grammar code and the game and the Java port of the equivalent C data-file parser.

**class `UIEntryReader`** — class Javadoc: ✔ · type stamp: before 260929

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `extract(UIEntryGrammar, ParseErrors, List<String>)` | before 260929 | ✔ |
| `parse(String)` | before 260929 | ✔ |
| `parseWithResults(String)` | before 260929 | ✔ |


## frontend/ui/entrybase/assembler

##### `UIEntryBaseAssembler.java`

**Responsible for:** Second stage of the ui_entry_base.txt pipeline: turns the raw string UIEntryBaseParseRecords emitted by the grammar into fully resolved UIEntryBase template objects.

**class `UIEntryBaseAssembler`** — class Javadoc: ✔ · type stamp: before 260920

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<UIEntryBaseParseRecord>, List<String>)` | before 260920 | ✔ |
| `parseEachEntry(List<UIEntry>, UIEntry)` | before 260920 | ✔ |


##### `UIEntryBaseParseRecord.java`

**Responsible for:** Immutable carrier for one raw ui_entry_base.txt template record, as produced by UIEntryBaseGrammar's entryBase rule.

**record `UIEntryBaseParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (7/7 documented): ✔ `name`, `renderer`, `combine`, `flags`, `desc`, `categories`, `lineNumber`


## frontend/ui/entrybase/reader

##### `UIEntryBaseReader.java`

**Responsible for:** Loads the relevant data-file entries into UIEntryBase objects by driving the matching ANTLR-generated lexer/parser.

**class `UIEntryBaseReader`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parse(String)` | — | ✔ |
| `parseWithResults(String)` | — | ✔ |
| `extract(UIEntryBaseGrammar, ParseErrors, List<String>)` | — | ✔ |


## frontend/ui/entryrenderer/assembler

##### `UIEntryRendererAssembler.java`

**Responsible for:** Assembler class - takes a list of UIEntryRendererParseRecords and converts them to a List of UIEntryRenderers.

**class `UIEntryRendererAssembler`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assemble(List<UIEntryRendererParseRecord>, List<String>)` | — | ✔ |


##### `UIEntryRendererParseRecord.java`

**Responsible for:** Thin parse-DTO produced by the UIEntryRenderer reader for a single UIEntryRenderer value in lib/gamedata/ui_entry_renderer.txt.

**record `UIEntryRendererParseRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `lineNumber`, `name`, `code`, `colours`, `labelColours`, `symbols`, `nDigits`, `sign`


## frontend/ui/entryrenderer/reader

##### `UIEntryRendererReader.java`

**Responsible for:** First stage of the ui_entry_renderer.txt pipeline: drives the ANTLR-generated UIEntryRendererLexer and UIEntryRendererGrammar, checks the declared record-count, and hands the raw text of each record to the UIEntryRendererAssembler as a UIEntryRendererParseRecord.

**class `UIEntryRendererReader`** — class Javadoc: ✔ · type stamp: before 260929

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `extract(UIEntryRendererGrammar, ParseErrors, List<String>)` | before 260929 | ✔ |
| `getUiEntryRendererParseRecord(List<String>)` | before 260929 | ✔ |
| `parse(String)` | before 260929 | ✔ |
| `parseWithResults(String)` | before 260929 | ✔ |


## frontend/ui/globals

##### `UIDataLoader.java`

**Responsible for:** Startup loader for the UI slice: parses the UI-entry gamedata files (ui_entry_renderer.txt, ui_entry_base.txt, ui_entry.txt) and populates UIRegistry through its setters.

**class `UIDataLoader`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadUIEntries()` | before 260916 | ✔ |
| `loadUIEntryBases()` | before 260916 | ✔ |
| `loadUIEntryRenderers()` | before 260916 | ✔ |
| `finalPass(List<UIEntry>)` | before 260919 | ✔ |
| `fillOutShortened(UIEntry)` | before 260919 | ✔ |


##### `UIRegistry.java`

**Responsible for:** Runtime holder for the UI-entry game data — the entry renderers, entry bases, and entries that drive the character-screen and property displays — plus the name lookups used to resolve them.

**class `UIRegistry`** — class Javadoc: ✔ · type stamp: before 260924

- Fields (9/9 documented): ✔ `MAX_ENTRY_LABEL`, `MAX_SHORTENED`, `logger`, `uiEntryRenderers`, `uiEntryBases`, `uiEntries`, `statNames`, `statReducedNames`, `MAX_PALETTE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getUIEntryRenderers()` | before 260924 | ✔ |
| `setUIEntryRenderers(List<UIEntryRenderer>)` | before 260924 | ✔ |
| `getUIEntryBases()` | before 260924 | ✔ |
| `setUIEntryBases(List<UIEntryBase>)` | before 260924 | ✔ |
| `getUIEntries()` | before 260924 | ✔ |
| `setUIEntries(List<UIEntry>)` | before 260924 | ✔ |
| `getUIEntryRenderer(String, List<String>)` | before 260924 | ✔ |
| `getUIEntryBase(String)` | before 260924 | ✔ |
| `getUIEntry(String)` | before 260924 | ✔ |


## frontend/ui/output

##### `Region.java`

**Responsible for:** A rectangle on the screen bound to a panel or subpanel - the port of C's struct region in ui-output.h.

**class `Region`** — class Javadoc: ✔ · type stamp: on 260911

- Fields (4/4 documented): ✔ `col`, `row`, `width`, `pageRows`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `screenRegion()` | on 260929 | ✔ |
| ctor `Region(int, int, int, int)` | on 260911 | ✔ |
| `getCol()` | on 260911 | ✔ |
| `getRow()` | on 260911 | ✔ |
| `getWidth()` | on 260911 | ✔ |
| `getPageRows()` | on 260911 | ✔ |
| `setCol(int)` | on 260929 | ✔ |
| `setRow(int)` | on 260929 | ✔ |
| `setWidth(int)` | on 260929 | ✔ |
| `setPageRows(int)` | on 260929 | ✔ |


## frontend/ui/player

##### `CharSheetConfig.java`

**Responsible for:** Cached layout for the character sheet's resistance panel — the Java form of C's struct char_sheet_config ([C] ui-player.c), currently used only for that one panel since C notes the rest of the sheet is "no longer hardwired".

**class `CharSheetConfig`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (8/8 documented): ✔ `stat_mod_entries`, `resRegions`, `resistsByRegion`, `nResistsByRegion`, `nStatModEntries`, `resCols`, `resRows`, `resNLabel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CharSheetConfig()` | on 260925 | ✔ |
| `getResistsByRegion(int, int)` | on 260925 | ✔ |
| `setResistsByRegion(int, int, CharSheetResist)` | on 260925 | ✔ |
| `getResCols()` | on 260925 | ✔ |
| `setResCols(int)` | on 260925 | ✔ |
| `getResRows()` | on 260925 | ✔ |
| `setResRows(int)` | on 260925 | ✔ |
| `getResNLabel()` | on 260925 | ✔ |
| `setNStatModEntries(int)` | on 260925 | ✔ |
| `initStatModEntries(int)` | on 260925 | ✔ |
| `setStatModEntry(int, UIEntry)` | on 260925 | ✔ |
| `setResNlabel(int)` | on 260925 | ✔ |
| `getResRegion(int)` | on 260925 | ✔ |
| `setResRegion(int, Region)` | on 260925 | ✔ |
| `getnResistsByRegion(int)` | on 260925 | ✔ |
| `setnResistsByRegion(int, int)` | on 260925 | ✔ |


##### `CharSheetResist.java`

**Responsible for:** One resistance-panel entry: the UIEntry it displays and that entry's rendered label — the Java form of C's struct char_sheet_resist in ui-player.c.

**class `CharSheetResist`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (2/2 documented): ✔ `entry`, `label`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CharSheetResist(UIEntry)` | on 260925 | ✔ |
| `getLabel()` | on 260925 | ✔ |
| `setLabel(String)` | on 260925 | ✔ |


## middle

##### `Activation.java`

**Responsible for:** A named, reusable activation (as loaded from activation.txt) — the triggerable power shared by artifacts and some items, defined by the list of Effects it runs plus its level, power, aim requirement and flavour messages.

**class `Activation`** — class Javadoc: ✔ · type stamp: before 261013

- Fields (8/8 documented): ✔ `name`, `index`, `aim`, `level`, `power`, `effects`, `message`, `desc`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Activation(String, int, boolean, int, int, List<Effect>, String, String)` | — | ✔ |
| `getName()` | — | ✔ |
| `copy()` | — | ✔ |
| `getPower()` | — | ✔ |
| `setIndex(int)` | on 261013 | ✔ |


##### `AllocEntry.java`

**Responsible for:** One entry in the object/monster allocation table: a plain data holder with no behaviour of its own.

**class `AllocEntry`** — class Javadoc: ✔ · type stamp: —

- Fields (5/5 documented): ✔ `Index`, `level`, `prob1`, `prob2`, `prob3`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getIndex()` | — | ✔ |
| `setIndex(int)` | — | ✔ |
| `getLevel()` | — | ✔ |
| `setLevel(int)` | — | ✔ |
| `getProb1()` | — | ✔ |
| `setProb1(int)` | — | ✔ |
| `getProb2()` | — | ✔ |
| `setProb2(int)` | — | ✔ |
| `getProb3()` | — | ✔ |
| `setProb3(int)` | — | ✔ |


##### `Message.java`

**Responsible for:** The engine's outbound message channel - the port of C's msg/msgt family (src/message.c).

**class `Message`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `logger`, `queueSize`, `messageLog`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Message()` | — | ✔ |
| `message(String, Object)` | — | ✔ |
| `messageType(MessageType, String, Object)` | — | ✔ |
| `sound(MessageType, Player)` | — | ✔ |
| `messageAdd(String, MessageType)` | on 260908 | ✔ |
| `size(String, int)` | — | ✔ |

**class `Message.MessageT`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `text`, `type`, `count`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MessageT(int, String, MessageType)` | — | ✔ |
| `incrementCount()` | — | ✔ |
| `getCount()` | — | ✔ |
| `getText()` | — | ✔ |
| `getType()` | — | ✔ |


## middle/cave

##### `Chunk.java`

**Responsible for:** A whole level (or self-contained piece of one): the 2D grid of Squares plus everything that lives on it — monsters, objects, noise/scent flow maps, generation metadata and level feeling.

**class `Chunk`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (27/27 documented): ✔ `logger`, `name`, `turn`, `depth`, `feeling`, `objectRating`, `monsterRating`, `goodItem`, `height`, `width`, `feelingSquares`, `featCount`, `squares`, `noise`, `scent`, `decoy`, `objects`, `objMax`, `monsters`, `monMax`, `monCnt`, `monCurrent`, `numRepro`, `monsterGroups`, `join`, `player`, `currentLevel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Chunk(String, int, int, int, int, int, boolean, int, int, int, int, int, int, int, int, Player)` | before 260930 | ✔ |
| `getCurrentLevel()` | before 260930 | ✔ |
| `setCurrentLevel(Chunk)` | — | ✔ |
| `inBounds(Loc)` | before 260930 | ✔ |
| `inBoundsFully(Loc)` | before 260930 | ✔ |
| `squareIsMarked(Loc)` | before 260930 | ✔ |
| `squareIsGlow(Loc)` | before 260930 | ✔ |
| `squareIsDamaging(Loc)` | before 260930 | ✔ |
| `squareIsVault(Loc)` | before 260930 | ✔ |
| `squareIsSeen(Loc)` | before 260930 | ✔ |
| `squareIsNoFlow(Loc)` | before 260930 | ✔ |
| `squareIsNoScent(Loc)` | before 260930 | ✔ |
| `squareIsView(Loc)` | before 260930 | ✔ |
| `squareWasSeen(Loc)` | before 260930 | ✔ |
| `squareIsTrap(Loc)` | before 260930 | ✔ |
| `squareIsInvis(Loc)` | before 260930 | ✔ |
| `getPileIterator(Loc)` | before 260930 | ✔ |
| `squareIsVisibleTrap(Loc)` | before 260930 | ✔ |
| `squareIsSecretTrap(Loc)` | before 260930 | ✔ |
| `squareIsDisabledTrap(Loc)` | before 260930 | ✔ |
| `squareIsDisarmableTrap(Loc)` | before 260930 | ✔ |
| `squareChangeable(Loc)` | before 260930 | ✔ |
| `squareDTrapEdge(Loc)` | before 260930 | ✔ |
| `squareIsWallInner(Loc)` | before 260930 | ✔ |
| `squareIsWallOuter(Loc)` | before 260930 | ✔ |
| `squareIsWallSolid(Loc)` | before 260930 | ✔ |
| `squareIsMonRestrict(Loc)` | before 260930 | ✔ |
| `squareIsNoTeleport(Loc)` | before 260930 | ✔ |
| `squareIsNoMap(Loc)` | before 260930 | ✔ |
| `squareIsNoESP(Loc)` | before 260930 | ✔ |
| `squareIsProject(Loc)` | before 260930 | ✔ |
| `squareIsDTrap(Loc)` | before 260930 | ✔ |
| `squareIsNoStairs(Loc)` | before 260930 | ✔ |
| `squareIsOpen(Loc)` | before 260930 | ✔ |
| `squareIsWarded(Loc)` | before 260930 | ✔ |
| `squareIsDecoyed(Loc)` | before 260930 | ✔ |
| `squareIsWebbed(Loc)` | before 260930 | ✔ |
| `squareSeemsLikeWall(Loc)` | before 260930 | ✔ |
| `squareIsInteresting(Loc)` | before 260930 | ✔ |
| `squareTrapFlag(Loc, TrapEnum)` | before 260930 | ✔ |
| `squareIsLockedDoor(Loc)` | before 260930 | ✔ |
| `squareIsUnlockedDoor(Loc)` | before 260930 | ✔ |
| `squareIsPlayerTrap(Loc)` | before 260930 | ✔ |
| `squareIsPlayer(Loc)` | before 260930 | ✔ |
| `squareIsEmpty(Loc)` | before 260930 | ✔ |
| `squareIsArrivable(Loc)` | before 260930 | ✔ |
| `squareCanPutItem(Loc)` | before 260930 | ✔ |
| `squareIsDiggable(Loc)` | before 260930 | ✔ |
| `squareIsFloor(Loc)` | before 260930 | ✔ |
| `squareIsWebbable(Loc)` | before 260930 | ✔ |
| `squareIsMonsterWalkable(Loc)` | before 260930 | ✔ |
| `squareIsPassable(Loc)` | before 260930 | ✔ |
| `squareIsProjectable(Loc)` | before 260930 | ✔ |
| `squareAllowsFeel(Loc)` | before 260930 | ✔ |
| `squareAllowsLOS(Loc)` | before 260930 | ✔ |
| `squareIsStrongWall(Loc)` | before 260930 | ✔ |
| `squareIsBright(Loc)` | before 260930 | ✔ |
| `squareIsFiery(Loc)` | before 260930 | ✔ |
| `squareIsBelievedWall(Loc)` | before 260930 | ✔ |
| `isKnownPassible(Loc)` | before 260930 | ✔ |
| `squareSuitsStairsWell(Loc)` | before 260930 | ✔ |
| `squareSuitsStairsOK(Loc)` | before 260930 | ✔ |
| `squareAllowsSummoning(Loc)` | before 260930 | ✔ |
| `squareNumWallsAdjacent(Loc)` | before 260930 | ✔ |
| `squareNumWallsDiagonal(Loc)` | before 260930 | ✔ |
| `getSquare(Loc)` | before 260930 | ✔ |
| `squareFeature(Loc)` | before 260930 | ✔ |
| `squareLight(Loc)` | before 260930 | ✔ |
| `squareMonster(Loc)` | before 260930 | ✔ |
| `caveMonster(int)` | before 260930 | ✔ |
| `getMonster(Loc)` | before 260930 | ✔ |
| `squareHasInfoFlag(Loc, SquareEnum)` | before 260930 | ✔ |
| `squareIsLit(Loc)` | before 260930 | ✔ |
| `squareObject(Loc)` | before 260930 | ✔ |
| `squareTrap(Loc)` | before 260930 | ✔ |
| `squareHoldsObject(Loc, ItemObject)` | before 260930 | ✔ |
| `getWidth()` | before 260929 | ✔ |
| `getHeight()` | before 260929 | ✔ |
| `squareExciseObject(Loc, ItemObject)` | before 260930 | ✔ |
| `objectDelete(Chunk, ItemObject)` | before 260930 | ✔ |
| `delistObject(ItemObject)` | before 260930 | ✔ |
| `squareMemorize(Loc)` | before 260930 | ✔ |
| `squareSetKnownFeat(Loc, Feature)` | before 260930 | ✔ |
| `isKnown(Loc)` | before 260930 | ✔ |
| `getMonMax()` | before 260930 | ✔ |
| `getName()` | before 260930 | ✔ |
| `getMonsters()` | before 260930 | ✔ |
| `squareLightSpot(Loc)` | before 260930 | ✔ |
| `monsterCount()` | before 260929 | ✔ |
| `compactMonsters(int)` | before 260930 | ✔ |
| `caveMonsterMax()` | before 260930 | ✔ |
| `monsterIndexMove(int, int)` | before 260930 | ✔ |
| `squareSetMon(Loc, int)` | before 260930 | ✔ |
| `deleteMonster(Loc)` | before 260930 | ✔ |
| `deleteMonsterIndex(int)` | — | ✔ |
| `illuminate(boolean)` | — | ✔ |
| `pickAndPlaceDistantMonster(Loc, int, boolean, int)` | — | ✔ |
| `squareMemorizeTraps(Loc)` | — | ✔ |
| `decreaseTrapTimeout()` | before 260930 | ✔ |
| `getObjects()` | before 260929 | ✔ |
| `resetNoise()` | — | ✔ |
| `updateScent()` | before 260930 | ✔ |
| `getNoise()` | before 260930 | ✔ |
| `getScent()` | before 260930 | ✔ |
| `displayFeeling(boolean)` | — | ✔ |
| `updateView(Player)` | before 260828 | ✔ |
| `updateOne(Loc, Player)` | on 260828 | ✔ |
| `squareNoteSpot(Loc)` | — | ✔ |
| `squareIsFeel(Loc)` | on 260828 | ✔ |
| `squareRevealTrap(Loc, boolean, boolean)` | — | ✔ |
| `noLight(Player)` | on 260828 | ✔ |
| `updateViewOne(Loc, Player)` | before 260828 | ✔ |
| `becomeViewable(Loc, Player, boolean)` | before 260828 | ✔ |
| `squareForget(Loc)` | before 260828 | ✔ |
| `calcLighting(Player)` | before 260828 | ✔ |
| `addLight(Player, Loc, int, int)` | before 260828 | ✔ |
| `glowCanLightWall(Player, Loc)` | before 260828 | ✔ |
| `sourceCanLightWall(Player, Loc, Loc)` | before 260828 | ✔ |
| `markWasSeen()` | before 260827 | ✔ |
| `getMonCurrent()` | before 260929 | ✔ |
| `setFeeling(int)` | before 260929 | ✔ |
| `caveFindDecoy()` | before 260930 | ✔ |


##### `ChunkUtils.java`

**Responsible for:** Static geometry helpers over a Chunk that answer questions about a level without belonging to any one grid.

**class `ChunkUtils`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `los(Chunk, Loc, Loc)` | on 260828 | ✔ |
| `isProjectable(Chunk, Loc, Loc, Flag<ProjectEnum>)` | on 261001 | ✔ |
| `projectionPath(Chunk, int, Loc, Loc, Flag<ProjectEnum>)` | on 261001 | ✔ |


##### `ClockwiseDirectionLoop.java`

**Responsible for:** A circular, singly-linked ring of the eight compass directions plus the centre, ordered clockwise starting at north (N, NE, E, SE, S, SW, W, NW, centre, then back to N).

**class `ClockwiseDirectionLoop`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (2/2 documented): ✔ `north`, `keypadDirection`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ClockwiseDirectionLoop()` | on 260930 | ✔ |
| `createAndLinkKeypadDirection()` | on 260930 | ✔ |
| `getXOffset()` | on 260930 | ✔ |
| `getYOffset()` | on 260930 | ✔ |
| `getGrid()` | on 260930 | ✔ |
| `moveNext()` | on 260930 | ✔ |

**class `ClockwiseDirectionLoop.DirectionNode`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (4/4 documented): ✔ `dir`, `xOff`, `yOff`, `next`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `DirectionNode(DirectionEnum, int, int)` | — | ✔ |
| `setNext(ClockwiseDirectionLoop.DirectionNode)` | — | ✔ |
| `getNext()` | — | ✔ |


##### `Connector.java`

**Responsible for:** A connection point between dungeon pieces during level generation — the grid to join, the terrain feature to place there, and extra info flags.

**class `Connector`** — class Javadoc: ✔ · type stamp: —

- Fields (2/3 documented): ✔ `grid`, `feat` — ✘ `info`


##### `Feature.java`

**Responsible for:** The definition of one terrain feature type (as loaded from terrain.txt) — its code, name, display glyph, the TerrainFeatureFlags that give it its behaviour, and the various messages shown when the player interacts with it.

**class `Feature`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (18/18 documented): ✔ `flags`, `code`, `name`, `description`, `featureIndex`, `mimic`, `priority`, `shopNum`, `dig`, `displayCharacter`, `walkMsg`, `runMsg`, `hurtMsg`, `dieMsg`, `confusedMsg`, `lookPrefix`, `lookInPreposition`, `resistFlag`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Feature(TerrainFlags, String, String, TerrainFlags, int, int, Flag<TerrainFeatureFlags>, AngbandDisplayCharacter, String, String, String, String, String, String, String, MonsterRaceFlag)` | before 260930 | ✔ |
| `getTerrainFlag()` | before 260930 | ✔ |
| `isMimicing()` | before 260930 | ✔ |
| `isMagma()` | before 260930 | ✔ |
| `isQuartz()` | before 260930 | ✔ |
| `isGranite()` | before 260930 | ✔ |
| `isTreasure()` | before 260930 | ✔ |
| `isWall()` | before 260930 | ✔ |
| `isFloor()` | before 260930 | ✔ |
| `isTrapHolding()` | before 260930 | ✔ |
| `isObjectHolding()` | before 260930 | ✔ |
| `isMonsterWalkable()` | before 260930 | ✔ |
| `isShop()` | before 260930 | ✔ |
| `isLos()` | before 260930 | ✔ |
| `isPassable()` | before 260930 | ✔ |
| `isProjectable()` | before 260930 | ✔ |
| `isTorch()` | before 260930 | ✔ |
| `isBright()` | before 260930 | ✔ |
| `isFiery()` | before 260930 | ✔ |
| `isNoFlow()` | before 260930 | ✔ |
| `isNoScent()` | before 260930 | ✔ |
| `isSmooth()` | before 260930 | ✔ |
| `hasAnyDoor()` | before 260930 | ✔ |
| `isFullPermanent()` | on 260930 | ✔ |
| `isPermanent()` | on 260930 | ✔ |
| `isRock()` | before 260930 | ✔ |
| `fullRock()` | before 260930 | ✔ |
| `isOpenDoor()` | before 260930 | ✔ |
| `isClosedDoor()` | before 260930 | ✔ |
| `isCloseable()` | before 260930 | ✔ |
| `isStair()` | before 260930 | ✔ |
| `isUpStair()` | before 260930 | ✔ |
| `isDownStair()` | before 260930 | ✔ |
| `isNoFeat()` | before 260930 | ✔ |
| `isInteresting()` | before 260930 | ✔ |
| `getName()` | before 260930 | ✔ |
| `getCodeFlags()` | before 260930 | ✔ |
| `getMimic()` | before 260930 | ✔ |
| `toString()` | before 260930 | ✔ |
| `equals(Object)` | before 260930 | ✔ |
| `hashCode()` | before 260930 | ✔ |
| `setShopNum(int)` | — | ✘ |


##### `GenChunk.java`

**Responsible for:** Java port of the chunk-list bookkeeping counter from C's gen-chunk.c.

**class `GenChunk`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `chunkListMax`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getChunkListMax()` | on 260908 | ✔ |
| `setChunkListMax(int)` | on 260908 | ✔ |


##### `Generate.java`

**Responsible for:** Dungeon-level generation — the port of C's generate.c.

**class `Generate`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `prepareNextLevel(Player)` | — | ✔ |


##### `GridData.java`

**Responsible for:** The fully-resolved "what should be drawn here" view of a single dungeon grid: the visible monster (or none), terrain feature, object(s), traps and lighting, taking into account the player's knowledge, line of sight and hallucination.

**class `GridData`** — class Javadoc: ✔ · type stamp: —

- Fields (14/14 documented): ✔ `logger`, `monster`, `monsterIndex`, `feature`, `firstKind`, `objectKindList`, `trapList`, `multipleObjects`, `unseenObjects`, `unseenMoney`, `lighting`, `inView`, `isPlayer`, `hallucinate`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `mapInfo(Loc)` | — | ✔ |


##### `Heatmap.java`

**Responsible for:** A grid of integer "distances"/intensities over the whole level, used for flow-based monster pathfinding (how far each grid is from the player along passable terrain).

**class `Heatmap`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `grids`, `width`, `height`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Heatmap(int, int)` | — | ✔ |
| `getValue(int, int)` | — | ✔ |
| `setValue(int, int, int)` | — | ✔ |
| `getValue(Loc)` | — | ✔ |
| `setValue(Loc, int)` | — | ✔ |


##### `KeypadDirectionLoop.java`

**Responsible for:** A circular, singly-linked ring of the eight compass directions (plus centre) in the C original's "keypad" scan order (the ddd ordering: S, N, E, W, SE, SW, NE, NW, then centre).

**class `KeypadDirectionLoop`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (2/2 documented): ✔ `south`, `keypadDirection`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `KeypadDirectionLoop()` | on 260930 | ✔ |
| `createAndLinkKeypadDirection()` | on 260930 | ✔ |
| `getXOffset()` | on 260930 | ✔ |
| `getYOffset()` | on 260930 | ✔ |
| `getGrid()` | on 260930 | ✔ |
| `moveNext()` | on 260930 | ✔ |

**class `KeypadDirectionLoop.DirectionNode`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (4/4 documented): ✔ `dir`, `xOff`, `yOff`, `next`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `DirectionNode(DirectionEnum, int, int)` | — | ✔ |
| `setNext(DirectionNode)` | — | ✔ |
| `getNext()` | — | ✔ |


##### `Loc.java`

**Responsible for:** An immutable grid coordinate (column x, row y) in the dungeon.

**class `Loc`** — class Javadoc: ✔ · type stamp: on 260830

- Fields (3/3 documented): ✔ `zero`, `x`, `y`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Loc(int, int)` | on 260830 | ✔ |
| `row(int)` | on 260830 | ✔ |
| `getY()` | on 260830 | ✔ |
| `getX()` | on 260830 | ✔ |
| `nextGrid(DirectionEnum)` | on 260830 | ✔ |
| `equals(Object)` | on 260830 | ✔ |
| `isZero()` | on 260830 | ✔ |
| `sum(Loc)` | on 260830 | ✔ |
| `diff(Loc)` | on 260830 | ✔ |
| `rand(int, int)` | on 260830 | ✔ |
| `offset(int, int)` | on 260830 | ✔ |
| `copy()` | — | ✔ |
| `motionDir(Loc)` | before 260828 | ✔ |
| `distance(Loc)` | on 260828 | ✔ |
| `hashCode()` | on 260830 | ✔ |

**class `Loc.RowBuilder`** — class Javadoc: ✔ · type stamp: on 260830

- Fields (1/1 documented): ✔ `y`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `col(int)` | on 260830 | ✔ |


##### `PitProfile.java`

**Responsible for:** The recipe for a themed monster pit or nest (as loaded from pit.txt): which monsters may appear (by allowed/forbidden race flags, spell flags and base types), how deep/rare it is, and the colours used for its theme.

**class `PitProfile`** — class Javadoc: ✔ · type stamp: —

- Fields (13/13 documented): ✔ `name`, `roomType`, `ave`, `rarity`, `objectRarity`, `flags`, `forbiddenFlags`, `freqInnate`, `spellsFlags`, `forbiddenSpellFlags`, `bases`, `colours`, `forbiddenMonsters`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PitProfile(String, PitRoomType, int, int, int, Flag<MonsterRaceFlag>, Flag<MonsterRaceFlag>, int, Flag<MonsterSpell>, Flag<MonsterSpell>, List<MonsterBase>, List<ColourEnum>, List<MonsterRace>)` | before 261011 | ✔ |


##### `PointSet.java`

**Responsible for:** An ordered collection of grid locations, collected so that a set of changes can be applied to them afterwards (targeting uses it to gather candidate grids).

**class `PointSet`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (1/1 documented): ✔ `points`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PointSet()` | before 260930 | ✔ |
| `add(Loc)` | before 260930 | ✔ |
| `contains(Loc)` | before 260930 | ✔ |
| `size()` | before 260930 | ✔ |


##### `Square.java`

**Responsible for:** A single dungeon grid's contents: its terrain Feature, the per-grid SquareEnum info flags, lighting, the occupying monster (or player), the object Pile and any Traps.

**class `Square`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (6/6 documented): ✔ `info`, `feat`, `light`, `monsterIndex`, `objectPile`, `traps`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Square(Feature, int, int)` | before 260930 | ✔ |
| `hasInfoFlag(SquareEnum)` | before 260930 | ✔ |
| `pileExcise(ItemObject)` | before 260930 | ✔ |
| `getTopObject()` | before 260930 | ✔ |
| `getTrap()` | before 260930 | ✔ |
| `getLight()` | before 260930 | ✔ |
| `holdsObject(ItemObject)` | on 260816 | ✔ |
| `isLit()` | before 260930 | ✔ |
| `isFloor()` | before 260930 | ✔ |
| `isTrappable()` | before 260930 | ✔ |
| `isObjectHolding()` | before 260930 | ✔ |
| `isRock()` | before 260930 | ✔ |
| `featSeemsLikeWall()` | before 260930 | ✔ |
| `featIsIntersting()` | before 260930 | ✔ |
| `isGranite()` | before 260930 | ✔ |
| `isPerm()` | before 260930 | ✔ |
| `hasObjectArtifact()` | before 260930 | ✔ |
| `isMagma()` | before 260930 | ✔ |
| `isQuartz()` | before 260930 | ✔ |
| `isMineral()` | before 260930 | ✔ |
| `hasGoldVein()` | before 260930 | ✔ |
| `isRubble()` | before 260930 | ✔ |
| `getSquarePileIterator()` | before 260930 | ✔ |
| `isSecretDoor()` | before 260930 | ✔ |
| `isOpenDoor()` | before 260930 | ✔ |
| `isClosedDoor()` | before 260930 | ✔ |
| `isBrokenDoor()` | before 260930 | ✔ |
| `isLockedDoor()` | before 260930 | ✔ |
| `isUnlockedDoor()` | before 260930 | ✔ |
| `squareDoorPower()` | before 260930 | ✔ |
| `isDoor()` | before 260930 | ✔ |
| `isStairs()` | before 260930 | ✔ |
| `isUpStairs()` | before 260930 | ✔ |
| `isDownStairs()` | before 260930 | ✔ |
| `isShop()` | before 260930 | ✔ |
| `isPlayer()` | before 260930 | ✔ |
| `isOccupied()` | before 260930 | ✔ |
| `isFree()` | before 260930 | ✔ |
| `isMemoryBad(Chunk, Loc)` | before 260930 | ✔ |
| `isMark()` | before 260930 | ✔ |
| `isGlow()` | before 260930 | ✔ |
| `isVault()` | before 260930 | ✔ |
| `isRoom()` | before 260930 | ✔ |
| `isSeen()` | before 260930 | ✔ |
| `isView()` | before 260930 | ✔ |
| `wasSeen()` | before 260930 | ✔ |
| `isFeel()` | before 260930 | ✔ |
| `isTrap()` | before 260930 | ✔ |
| `getTraps()` | before 260930 | ✔ |
| `isInvis()` | before 260930 | ✔ |
| `isWallInner()` | before 260930 | ✔ |
| `isWallOuter()` | before 260930 | ✔ |
| `isWallSolid()` | before 260930 | ✔ |
| `isMonRestrict()` | before 260930 | ✔ |
| `isNoTeleport()` | before 260930 | ✔ |
| `isNoMap()` | before 260930 | ✔ |
| `isNoEsp()` | before 260930 | ✔ |
| `isProject()` | before 260930 | ✔ |
| `isDTrap()` | before 260930 | ✔ |
| `isNoStairs()` | before 260930 | ✔ |
| `isPlayerTrap()` | before 260930 | ✔ |
| `isWebbed()` | before 260930 | ✔ |
| `isDecoyed()` | before 260930 | ✔ |
| `isWarded()` | before 260930 | ✔ |
| `trapSpecific(TrapKind)` | before 260930 | ✔ |
| `isVisibleTrap()` | before 260930 | ✔ |
| `trapFlag(TrapEnum)` | before 260930 | ✔ |
| `trapTimeout(int)` | before 260930 | ✔ |
| `isOpen()` | before 260930 | ✔ |
| `isEmpty()` | before 260930 | ✔ |
| `isArrivable()` | before 260930 | ✔ |
| `featIsMonsterWalkable()` | before 260930 | ✔ |
| `featIsPassable()` | before 260930 | ✔ |
| `featIsProjectable()` | before 260930 | ✔ |
| `featAllowsLOS()` | before 260930 | ✔ |
| `featIsWall()` | before 260930 | ✔ |
| `featIsBright()` | before 260930 | ✔ |
| `featIsFiery()` | before 260930 | ✔ |
| `featIsNoFlow()` | before 260930 | ✔ |
| `featIsNoScent()` | before 260930 | ✔ |
| `canPutItem()` | before 260930 | ✔ |
| `isDamaging()` | before 260930 | ✔ |
| `allowsFeel()` | before 260930 | ✔ |
| `getFeature()` | before 260930 | ✔ |
| `setFeature(Feature)` | before 260930 | ✔ |
| `getMonsterIndex()` | before 260930 | ✔ |
| `setUpTest(boolean)` | before 260930 | ✔ |
| `getObjectPile()` | — | ✔ |
| `lightSpot()` | before 260817 | ✔ |
| `sqInfoOn(SquareEnum)` | before 260827 | ✔ |
| `sqInfoOff(SquareEnum)` | before 260827 | ✔ |
| `setLight(int)` | before 260828 | ✔ |
| `setMon(int)` | before 260930 | ✔ |


##### `Trap.java`

**Responsible for:** A trap instance placed on a specific dungeon grid — distinct from its TrapKind template.

**class `Trap`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (5/5 documented): ✔ `kind`, `grid`, `power`, `timeout`, `flags`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getKind()` | before 260930 | ✔ |
| `hasTrap(TrapEnum)` | before 260930 | ✔ |
| `getPower()` | before 260930 | ✔ |
| `getTimeout()` | before 260930 | ✔ |
| `decrementTimeout()` | before 260930 | ✔ |


##### `TrapKind.java`

**Responsible for:** The immutable template describing one type of trap (as loaded from trap.txt) — its display glyph, depth/rarity, power, effect(s) and the messages shown when it is saved against or triggers.

**class `TrapKind`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (17/17 documented): ✔ `trapKindName`, `text`, `description`, `message`, `messageOnSave`, `messageOnFailure`, `messageOnExtraEffect`, `trapKindIndex`, `angbandDisplayCharacter`, `rarity`, `minDepth`, `maxNum`, `power`, `flags`, `saveFlags`, `effect`, `effectXtra`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TrapKind(String, String, String, String, String, String, String, int, AngbandDisplayCharacter, int, int, int, Random, Flag<TrapEnum>, Flag<ObjectFlag>, List<Effect>, List<Effect>)` | — | ✔ |
| `lookupTrap(String)` | before 260930 | ✔ |
| `getDescription()` | before 260930 | ✔ |
| `getTrapKindName()` | before 260930 | ✔ |
| `getTrapKindIndex()` | before 261009 | ✔ |
| `getEffectXtra()` | — | ✔ |
| `getEffect()` | — | ✔ |
| `getFlags()` | — | ✔ |
| `getSaveFlags()` | — | ✔ |
| `getText()` | — | ✔ |
| `equals(Object)` | — | ✔ |
| `hashCode()` | — | ✔ |


##### `World.java`

**Responsible for:** A World record used to store the details of each level of the dungeon, including the Town (C: struct level in game-world.h, filled by parse_world_level in init.c).

**record `World`** — class Javadoc: ✔ · type stamp: before 261014

- Record components (via `@param`) (4/4 documented): ✔ `levelNumber`, `levelName`, `prevLevel`, `nextLevel`


## middle/cave/chunkbuilders

##### `BuilderType.java`

**Responsible for:** The level builders the generator can choose between — the port of C's cave_builders[] table, which is itself built from list-dun-profiles.h.

**enum `BuilderType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (9/9 documented): ✔ `TOWN`, `MODIFIED`, `MORIA`, `LAIR`, `GAUNTLET`, `HARD_CENTRE`, `LABYRINTH`, `CAVERN`, `CLASSIC`
- Fields (2/2 documented): ✔ `name`, `caveBuilder`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `BuilderType(String, CaveBuilder)` | — | ✔ |
| `getName()` | — | ✔ |
| `build(Player, int, int)` | — | ✔ |


##### `CaveBuilder.java`

**Responsible for:** A level builder — the port of C's cave_builder function pointer.

**interface `CaveBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `CavernBuilder.java`

**Responsible for:** Builds a cavern level — the port of C's cavern_gen.

**class `CavernBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `ClassicBuilder.java`

**Responsible for:** Builds a classic dungeon level — the port of C's classic_gen.

**class `ClassicBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `GauntletBuilder.java`

**Responsible for:** Builds a gauntlet level — the port of C's gauntlet_gen.

**class `GauntletBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `HardCentreBuilder.java`

**Responsible for:** Builds a hard centre level — the port of C's hard_centre_gen.

**class `HardCentreBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `LabyrinthBuilder.java`

**Responsible for:** Builds a labyrinth level — the port of C's labyrinth_gen.

**class `LabyrinthBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `LairBuilder.java`

**Responsible for:** Builds a lair level — the port of C's lair_gen.

**class `LairBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `ModifiedBuilder.java`

**Responsible for:** Builds a standard dungeon level — the port of C's modified_gen.

**class `ModifiedBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `MoriaBuilder.java`

**Responsible for:** Builds an Oangband-style moria level — the port of C's moria_gen.

**class `MoriaBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


##### `TownBuilder.java`

**Responsible for:** Builds the town — the port of C's town_gen.

**class `TownBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Player, int, int)` | — | ✔ |


## middle/cave/enums

##### `DirectionEnum.java`

**Responsible for:** The movement directions, each tied to its numeric-keypad key and its (x, y) step offset.

**enum `DirectionEnum`** — class Javadoc: ✔ · type stamp: before 260828

- Enum constants (11/11 documented): ✔ `DIR_UNKNOWN`, `DIR_NW`, `DIR_N`, `DIR_NE`, `DIR_W`, `DIR_TARGET`, `DIR_NONE`, `DIR_E`, `DIR_SW`, `DIR_S`, `DIR_SE`
- Fields (4/4 documented): ✔ `key`, `xOffset`, `yOffset`, `standard`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `DirectionEnum(int, int, int, boolean)` | before 260828 | ✔ |
| `fromKey(int)` | before 260828 | ✔ |
| `surroundingDirections()` | before 260828 | ✔ |
| `ddx()` | before 260828 | ✔ |
| `ddy()` | before 260828 | ✔ |
| `ddgrid()` | before 260828 | ✔ |
| `getKey()` | before 260828 | ✔ |
| `isStandard()` | before 260828 | ✔ |


##### `GridLightLevel.java`

**Responsible for:** The lighting states a dungeon grid can be drawn in, mirroring the LIGHTING_* constants of the C original.

**enum `GridLightLevel`** — class Javadoc: ✔ · type stamp: —

- Enum constants (5/5 documented): ✔ `LIGHTING_LOS`, `LIGHTING_TORCH`, `LIGHTING_LIT`, `LIGHTING_DARK`, `LIGHTING_MAX`


##### `PitRoomType.java`

**Responsible for:** The kinds of "pit"/"nest" themed monster rooms the level generator can build, mirroring the pit-type categories of the C original.

**enum `PitRoomType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `PIT_TYPE_NONE`, `PIT_TYPE_PIT`, `PIT_TYPE_NEST`, `PIT_TYPE_OTHER`


##### `RoomFlags.java`

**Responsible for:** Flags a room type can carry — the port of C's list-room-flags.h, folded together with the room_flags[] name table in generate.c.

**enum `RoomFlags`** — class Javadoc: ✔ · type stamp: —

- Enum constants (3/3 documented): ✔ `NONE`, `FEW_ENTRANCES`, `MAX`
- Fields (1/1 documented): ✔ `helpString`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RoomFlags(String)` | — | ✔ |
| `getHelpString()` | — | ✔ |


##### `SquareEnum.java`

**Responsible for:** The per-grid boolean flags a dungeon square can carry — memory, visibility, room/vault membership, generation hints, movement/teleport restrictions, etc.

**enum `SquareEnum`** — class Javadoc: ✔ · type stamp: before 260930

- Enum constants (23/23 documented): ✔ `SQUARE_NONE`, `SQUARE_MARK`, `SQUARE_GLOW`, `SQUARE_VAULT`, `SQUARE_ROOM`, `SQUARE_SEEN`, `SQUARE_VIEW`, `SQUARE_WASSEEN`, `SQUARE_FEEL`, `SQUARE_TRAP`, `SQUARE_INVIS`, `SQUARE_WALL_INNER`, `SQUARE_WALL_OUTER`, `SQUARE_WALL_SOLID`, `SQUARE_MON_RESTRICT`, `SQUARE_NO_TELEPORT`, `SQUARE_NO_MAP`, `SQUARE_NO_ESP`, `SQUARE_PROJECT`, `SQUARE_DTRAP`, `SQUARE_NO_STAIRS`, `SQUARE_CLOSE_PLAYER`, `SQUARE_MAX`
- Fields (1/1 documented): ✔ `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `SquareEnum(String)` | before 260930 | ✔ |
| `getDescription()` | before 260930 | ✔ |


##### `TerrainFeatureFlags.java`

**Responsible for:** The intrinsic property flags of a terrain feature — line of sight, passability, whether it can hold a trap or object, door/wall/stair classification, lighting, and so on.

**enum `TerrainFeatureFlags`** — class Javadoc: ✔ · type stamp: before 260930

- Enum constants (33/33 documented): ✔ `TF_NONE`, `TF_LOS`, `TF_PROJECT`, `TF_PASSABLE`, `TF_INTERESTING`, `TF_PERMANENT`, `TF_EASY`, `TF_TRAP`, `TF_NO_SCENT`, `TF_NO_FLOW`, `TF_OBJECT`, `TF_TORCH`, `TF_HIDDEN`, `TF_GOLD`, `TF_CLOSABLE`, `TF_FLOOR`, `TF_WALL`, `TF_ROCK`, `TF_GRANITE`, `TF_DOOR_ANY`, `TF_DOOR_CLOSED`, `TF_SHOP`, `TF_DOOR_JAMMED`, `TF_DOOR_LOCKED`, `TF_MAGMA`, `TF_QUARTZ`, `TF_STAIR`, `TF_UPSTAIR`, `TF_DOWNSTAIR`, `TF_SMOOTH`, `TF_BRIGHT`, `TF_FIERY`, `TF_MAX`
- Fields (1/1 documented): ✔ `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TerrainFeatureFlags(String)` | before 260930 | ✔ |
| `getDescription()` | before 260930 | ✔ |


##### `TerrainFlags.java`

**Responsible for:** The set of distinct terrain feature types a dungeon grid can be — floor, the various doors, staircases, the eight store entrances, walls/veins, rubble, lava, etc.

**enum `TerrainFlags`** — class Javadoc: ✔ · type stamp: before 260930

- Enum constants (26/26 documented): ✔ `FEAT_NONE`, `FEAT_FLOOR`, `FEAT_CLOSED`, `FEAT_OPEN`, `FEAT_BROKEN`, `FEAT_LESS`, `FEAT_MORE`, `FEAT_STORE_GENERAL`, `FEAT_STORE_ARMOR`, `FEAT_STORE_WEAPON`, `FEAT_STORE_BOOK`, `FEAT_STORE_ALCHEMY`, `FEAT_STORE_MAGIC`, `FEAT_STORE_BLACK`, `FEAT_HOME`, `FEAT_SECRET`, `FEAT_RUBBLE`, `FEAT_MAGMA`, `FEAT_QUARTZ`, `FEAT_MAGMA_K`, `FEAT_QUARTZ_K`, `FEAT_GRANITE`, `FEAT_PERM`, `FEAT_LAVA`, `FEAT_PASS_RUBBLE`, `FEAT_MAX`


## middle/cave/profiles/dungeon

##### `CaveProfile.java`

**Responsible for:** One style of level and everything that governs how it is built — the port of C's struct cave_profile in generate.h, loaded from dungeon_profile.txt.

**class `CaveProfile`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (10/10 documented): ✔ `name`, `blockSize`, `dunRooms`, `dunUnusual`, `maxRarity`, `tun`, `str`, `roomProfiles`, `minLevel`, `alloc`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CaveProfile(String, int, int, int, int, TunnelProfile, StreamerProfile, List<RoomProfile>, int, int)` | before 260930 | ✔ |
| `getName()` | before 260930 | ✔ |
| `getBlockSize()` | before 260930 | ✔ |
| `getDunRooms()` | before 260930 | ✔ |
| `getDunUnusual()` | before 260930 | ✔ |
| `getMaxRarity()` | before 260930 | ✔ |
| `getTun()` | before 260930 | ✔ |
| `getStr()` | before 260930 | ✔ |
| `getRoomProfiles()` | before 260930 | ✔ |
| `getMinLevel()` | before 260930 | ✔ |
| `getAlloc()` | before 260930 | ✔ |


##### `RoomProfile.java`

**Responsible for:** One kind of room a level style may contain, and the terms on which it may appear — the port of C's struct room_profile (generate.h), loaded from a room: line of dungeon_profile.txt.

**class `RoomProfile`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (9/9 documented): ✔ `name`, `roomType`, `rating`, `height`, `width`, `level`, `pit`, `rarity`, `cutoff`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RoomProfile(String, RoomType, int, int, int, int, boolean, int, int)` | before 260930 | ✔ |
| `getName()` | before 260930 | ✔ |
| `getRoomType()` | before 260930 | ✔ |
| `getRating()` | before 260930 | ✔ |
| `getHeight()` | before 260930 | ✔ |
| `getWidth()` | before 260930 | ✔ |
| `getLevel()` | before 260930 | ✔ |
| `isPit()` | before 260930 | ✔ |
| `getRarity()` | before 260930 | ✔ |
| `getCutoff()` | before 260930 | ✔ |


##### `StreamerProfile.java`

**Responsible for:** The mineral veins running through one style of level — the port of C's struct streamer_profile in generate.h, loaded from the streamer: line of dungeon_profile.txt (fields in file order: den : rng : mag : mc : qua : qc).

**class `StreamerProfile`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (6/6 documented): ✔ `den`, `rng`, `mam`, `mc`, `qua`, `qc`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `StreamerProfile(int, int, int, int, int, int)` | before 260930 | ✔ |
| `getDen()` | before 260930 | ✔ |
| `getRng()` | before 260930 | ✔ |
| `getMam()` | before 260930 | ✔ |
| `getMc()` | before 260930 | ✔ |
| `getQua()` | before 260930 | ✔ |
| `getQc()` | before 260930 | ✔ |


##### `TunnelProfile.java`

**Responsible for:** How corridors are dug on one style of level — the port of C's struct tunnel_profile (generate.h), loaded from the tunnel: line of dungeon_profile.txt.

**class `TunnelProfile`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (5/5 documented): ✔ `rnd`, `chg`, `con`, `pen`, `jct`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TunnelProfile(int, int, int, int, int)` | on 260930 | ✔ |
| `getChg()` | on 260930 | ✔ |
| `getCon()` | on 260930 | ✔ |
| `getJct()` | on 260930 | ✔ |
| `getPen()` | on 260930 | ✔ |
| `getRnd()` | on 260930 | ✔ |


## middle/cave/profiles/room

##### `RoomTemplate.java`

**Responsible for:** A room template loaded from room_template.txt — the port of C's struct room_template (generate.h).

**class `RoomTemplate`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (10/10 documented): ✔ `name`, `mapText`, `map`, `flags`, `type`, `rating`, `height`, `width`, `doors`, `tval`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RoomTemplate(String, String, List<String>, Flag<RoomFlags>, int, int, int, int, int, TValue)` | before 260930 | ✔ |
| `getName()` | before 260930 | ✔ |
| `getMapText()` | before 260930 | ✔ |
| `getMap()` | before 260930 | ✔ |
| `getFlags()` | before 260930 | ✔ |
| `getType()` | before 260930 | ✔ |
| `getRating()` | before 260930 | ✔ |
| `getHeight()` | before 260930 | ✔ |
| `getWidth()` | before 260930 | ✔ |
| `getDoors()` | before 260930 | ✔ |
| `getTval()` | before 260930 | ✔ |


## middle/cave/profiles/vault

##### `Vault.java`

**Responsible for:** One vault the game may place — the port of C's struct vault in generate.h.

**class `Vault`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (10/10 documented): ✔ `name`, `type`, `mapLines`, `map`, `flags`, `rating`, `height`, `width`, `minLevel`, `maxLevel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Vault(String, RoomType, String, List<String>, Flag<RoomFlags>, int, int, int, int, int)` | before 260930 | ✔ |
| `getName()` | before 260930 | ✔ |
| `getType()` | before 260930 | ✔ |
| `getMapLines()` | before 260930 | ✔ |
| `getMap()` | before 260930 | ✔ |
| `getFlags()` | before 260930 | ✔ |
| `getRating()` | before 260930 | ✔ |
| `getHeight()` | before 260930 | ✔ |
| `getWidth()` | before 260930 | ✔ |
| `getMinLevel()` | before 260930 | ✔ |
| `getMaxLevel()` | before 260930 | ✔ |


## middle/cave/roombuilders

##### `CircularRoom.java`

**Responsible for:** Builds a circular room — the port of C's build_circular.

**class `CircularRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `CrossedRoom.java`

**Responsible for:** Builds a cross-shaped room — the port of C's build_crossed.

**class `CrossedRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `GreaterVault.java`

**Responsible for:** Builds a greater vault — the port of C's build_greater_vault.

**class `GreaterVault`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `HugeRoom.java`

**Responsible for:** Builds a huge room — the port of C's build_huge.

**class `HugeRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `InterestingRoom.java`

**Responsible for:** Builds an interesting room — the port of C's build_interesting.

**class `InterestingRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `LargeRoom.java`

**Responsible for:** Builds a large room with an inner room — the port of C's build_large.

**class `LargeRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `LesserVault.java`

**Responsible for:** Builds a lesser vault — the port of C's build_lesser_vault.

**class `LesserVault`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `MediumVault.java`

**Responsible for:** Builds a medium vault — the port of C's build_medium_vault.

**class `MediumVault`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `MoriaRoom.java`

**Responsible for:** Builds a moria room — the port of C's build_moria, taken from Oangband.

**class `MoriaRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `NestRoom.java`

**Responsible for:** Builds a monster nest — the port of C's build_nest.

**class `NestRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `NewGreaterVault.java`

**Responsible for:** Builds a greater vault of the newer style — the port of C's build_greater_new_vault.

**class `NewGreaterVault`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `NewLesserVault.java`

**Responsible for:** Builds a lesser vault of the newer style — the port of C's build_lesser_new_vault.

**class `NewLesserVault`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `NewMediumVault.java`

**Responsible for:** Builds a medium vault of the newer style — the port of C's build_medium_new_vault.

**class `NewMediumVault`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `OverlapRoom.java`

**Responsible for:** Builds a pair of overlapping rectangles — the port of C's build_overlap.

**class `OverlapRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `PitRoom.java`

**Responsible for:** Builds a monster pit — the port of C's build_pit.

**class `PitRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `RoomBuilder.java`

**Responsible for:** A room builder — the port of C's room_builder function pointer.

**interface `RoomBuilder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `RoomOfChambers.java`

**Responsible for:** Builds a room of chambers — the port of C's build_room_of_chambers.

**class `RoomOfChambers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `RoomType.java`

**Responsible for:** The room types a level may contain — the port of C's room_builders[] table, which is itself built from list-rooms.h.

**enum `RoomType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (19/19 documented): ✔ `STAIRCASE`, `SIMPLE`, `MORIA`, `LARGE`, `CROSSED`, `CIRCULAR`, `OVERLAP`, `TEMPLATE`, `INTERESTING`, `PIT`, `NEST`, `HUGE`, `CHAMBERS`, `LESSER_VAULT`, `MEDIUM_VAULT`, `GREATER_VAULT`, `NEW_LESSER_VAULT`, `NEW_MEDIUM_VAULT`, `NEW_GREATER_VAULT`
- Fields (4/4 documented): ✔ `name`, `maxHeight`, `maxWidth`, `builder`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RoomType(String, int, int, RoomBuilder)` | — | ✔ |
| `build(Chunk, Loc, int)` | — | ✔ |
| `getIndexFromName(String)` | — | ✔ |
| `getNameFromIndex(int)` | — | ✔ |
| `getRoomBuilderCount()` | — | ✔ |
| `getRoomTypeFromName(String)` | — | ✔ |
| `getName()` | — | ✔ |
| `getMaxHeight()` | — | ✔ |
| `getMaxWidth()` | — | ✔ |


##### `SimpleRoom.java`

**Responsible for:** Builds a plain rectangular room — the port of C's build_simple.

**class `SimpleRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `StaircaseRoom.java`

**Responsible for:** Builds a staircase room — the port of C's build_staircase.

**class `StaircaseRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


##### `TemplateRoom.java`

**Responsible for:** Builds a room from a template — the port of C's build_template.

**class `TemplateRoom`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build(Chunk, Loc, int)` | — | ✔ |


## middle/cave/store

##### `Store.java`

**Responsible for:** The town stores and their periodic restocking.

**class `Store`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storeUpdate()` | — | ✔ |
| `storeReset()` | on 260908 | ✔ |


## middle/combat

##### `BlowMethod.java`

**Responsible for:** The definition of a monster attack "method" (as loaded from blow_methods.txt) — how a blow is delivered (hit, bite, claw, …), whether it can cut/stun/miss/is physical, and the flavour messages used to describe it.

**class `BlowMethod`** — class Javadoc: ✔ · type stamp: before 260930

- Fields (8/8 documented): ✔ `name`, `cut`, `stun`, `miss`, `phys`, `mesgT`, `blowMessage`, `desc`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `BlowMethod(String, boolean, boolean, boolean, boolean, MessageType, List<String>, String)` | before 260930 | ✔ |
| `getName()` | before 260930 | ✔ |
| `isCut()` | before 260930 | ✔ |
| `isStun()` | before 260930 | ✔ |
| `isMiss()` | before 260930 | ✔ |
| `isPhys()` | before 260930 | ✔ |
| `getMesgT()` | before 260930 | ✔ |
| `getBlowMessage()` | before 260930 | ✔ |
| `getDesc()` | before 260930 | ✔ |


##### `O_CriticalLevel.java`

**Responsible for:** One level in the O-combat critical-hit table: a 1/chance probability paired with the number of extra damage dice and the message shown.

**class `O_CriticalLevel`** — class Javadoc: ✔ · type stamp: —

- Fields (5/5 documented): ✔ `tag`, `chance`, `addedDice`, `msgt`, `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `O_CriticalLevel(int, int, MessageType)` | — | ✔ |
| `getChance()` | — | ✔ |
| `getAddedDice()` | — | ✔ |
| `getMsgt()` | — | ✔ |


##### `Target.java`

**Responsible for:** The player's current attack/spell target: either a specific Monster or a bare grid Loc the player is aiming at.

**class `Target`** — class Javadoc: ✔ · type stamp: before 261001

- Fields (5/5 documented): ✔ `grid`, `targetFixed`, `targetSet`, `monster`, `targetMonsterIndex`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setTargetMonster(Monster)` | before 261001 | ✔ |
| `getTargetMonster(Chunk)` | before 261001 | ✔ |
| `targetable(Monster)` | before 261001 | ✔ |
| `isTargetSet()` | before 261009 | ✔ |
| `setGrid(Loc)` | before 261009 | ✔ |
| `getGrid()` | before 261009 | ✔ |
| `targetOkay()` | before 260903 | ✔ |


## middle/combat/enums

##### `HitType.java`

**Responsible for:** The escalating quality tiers of a critical hit, used to choose the "good hit"/"great hit"/… message and damage multiplier.

**enum `HitType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (5/5 documented): ✔ `HIT_GOOD`, `HIT_GREAT`, `HIT_SUPERB`, `HIT_HI_GREAT`, `HIT_HI_SUPERB`


##### `ProjectEnum.java`

**Responsible for:** The flags passed to projectable() and project() to say how a projection behaves.

**enum `ProjectEnum`** — class Javadoc: ✔ · type stamp: before 261001

- Enum constants (17/17 documented): ✔ `PROJECT_NONE`, `PROJECT_JUMP`, `PROJECT_BEAM`, `PROJECT_THRU`, `PROJECT_STOP`, `PROJECT_GRID`, `PROJECT_ITEM`, `PROJECT_KILL`, `PROJECT_HIDE`, `PROJECT_AWARE`, `PROJECT_SAFE`, `PROJECT_ARC`, `PROJECT_PLAY`, `PROJECT_INFO`, `PROJECT_SHORT`, `PROJECT_SELF`, `PROJECT_ROCK`


##### `ProjectionType.java`

**Responsible for:** The broad categories a ProjectionEnum projection falls into, mirroring the C original's projection-type grouping.

**enum `ProjectionType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `PT_NONE`, `PT_ELEMENT`, `PT_ENVIRONS`, `PT_MONSTER`


## middle/effect

##### `Earthquake.java`

**Responsible for:** Targeting mode for an earthquake effect: centred on the caster, or centred on a chosen target.

**enum `Earthquake`** — class Javadoc: ✔ · type stamp: —

- Enum constants (2/2 documented): ✔ `QUAKE_NONE`, `QUAKE_TARGETED`


##### `Effect.java`

**Responsible for:** One configured effect instance — the runtime pairing of an EffectEnum with the concrete parameters it needs: dice for its magnitude, a sub-type payload (EffectSubTypeWrapper), radius, target offset, timing and any Expressions that scale values at evaluation time.

**class `Effect`** — class Javadoc: ✔ · type stamp: before 261001

- Fields (11/11 documented): ✔ `expression`, `index`, `dice`, `diceString`, `y`, `x`, `subType`, `value`, `radius`, `otherParameter`, `msg`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Effect(EffectEnum, Random, String, int, int, EffectSubTypeEnum, EffectSubTypeWrapper, int, int, List<Expression>, String)` | before 261001 | ✔ |
| `isValid()` | before 261001 | ✔ |
| `isAim(List<Effect>)` | on 261001 | ✔ |
| `getInfo()` | before 261001 | ✔ |
| `getDescription()` | before 261001 | ✔ |
| `copy()` | before 261001 | ✔ |
| `effectDo(Source, ItemObject, boolean, boolean, DirectionEnum, int, int, Command)` | on 261001 | ✔ |


##### `EffectSubTypeEnum.java`

**Responsible for:** The sub-type families an uk.co.jackoftradesltd.middle.enums.EffectEnum effect can belong to — projection, timed effect, nourishment, summon, stat, enchant, shapechange, earthquake, glyph, teleport, etc.

**enum `EffectSubTypeEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/15 documented): ✘ `EST_NONE`, `EST_PROJ`, `EST_TMD`, `EST_NOURISH`, `EST_MON_TMD`, `EST_SUMMON`, `EST_SUMMON_SPEC`, `EST_STAT`, `EST_ENCHANT`, `EST_SHAPECHANGE`, `EST_EARTHQUAKE`, `EST_GLYPH`, `EST_TELEPORT`, `EST_TELEPORT_TO`, `EST_MAX`


##### `EffectSubTypeWrapper.java`

**Responsible for:** A type-safe tagged union for an effect's sub-type parameter.

**class `EffectSubTypeWrapper`** — class Javadoc: ✔ · type stamp: before 261001

- Fields (16/16 documented): ✔ `logger`, `subType`, `nullValue`, `projectionWrapper`, `timedWrapper`, `nourishWrapper`, `monTimedWrapper`, `summonWrapper`, `summonTypeWrapper`, `statsWrapper`, `enchantWrapper`, `shapeWrapper`, `quakeWrapper`, `glyphType`, `teleportMonsterMayCast`, `teleportToMonsterMayCast`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EffectSubTypeWrapper()` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(EffectSubTypeEnum)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(GlyphType)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(Earthquake)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(PlayerShape)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(EffectEnchant)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(Stats)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(Summon)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(SummonType)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(MonTimed)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(EffectNourish)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(ProjectionEnum)` | before 261001 | ✔ |
| ctor `EffectSubTypeWrapper(TimedEffect)` | before 261001 | ✔ |
| `teleport(boolean)` | before 261001 | ✔ |
| `teleportTo(boolean)` | before 261001 | ✔ |
| `setValue(boolean, boolean)` | before 261001 | ✔ |
| `getTeleportMonsterMayCast()` | before 261001 | ✔ |
| `getTeleportToMonsterMayCast()` | before 261001 | ✔ |
| `setValue(GlyphType)` | before 261001 | ✔ |
| `setValue(Earthquake)` | before 261001 | ✔ |
| `setValue(PlayerShape)` | before 261001 | ✔ |
| `setValue(EffectEnchant)` | before 261001 | ✔ |
| `setValue(Stats)` | before 261001 | ✔ |
| `setValue(Summon)` | before 261001 | ✔ |
| `setValue(SummonType)` | before 261001 | ✔ |
| `setValue(MonTimed)` | before 261001 | ✔ |
| `setValue(EffectNourish)` | before 261001 | ✔ |
| `setValue(ProjectionEnum)` | before 261001 | ✔ |
| `setValue(TimedEffect)` | before 261001 | ✔ |
| `getSubType()` | before 261001 | ✔ |
| `getProjectionWrapper()` | before 261001 | ✔ |
| `getTimedWrapper()` | before 261001 | ✔ |
| `getNourishWrapper()` | before 261001 | ✔ |
| `getMonTimedWrapper()` | before 261001 | ✔ |
| `getSummonWrapper()` | before 261001 | ✔ |
| `getSummonTypeWrapper()` | before 261001 | ✔ |
| `getStatsWrapper()` | before 261001 | ✔ |
| `getEnchantWrapper()` | before 261001 | ✔ |
| `getShapeWrapper()` | before 261001 | ✔ |
| `getQuakeWrapper()` | before 261001 | ✔ |
| `getGlyphType()` | before 261001 | ✔ |
| `copy()` | before 261001 | ✔ |


##### `EffectUtil.java`

**Responsible for:** Free-standing helpers for firing effects — the port landing spot for the convenience wrappers in C's effects.c.

**class `EffectUtil`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `effectSimple(EffectEnum, Source, String, EffectSubTypeWrapper, int, int, int, int, Boolean)` | — | ✔ |


##### `Expression.java`

**Responsible for:** A named, game-state-driven scaling expression attached to an Effect.

**class `Expression`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `codeLetter`, `baseType`, `operations`, `diceString`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Expression(char, EffectBaseType, String)` | — | ✔ |
| `setDiceString(String)` | — | ✔ |
| `copy()` | — | ✔ |


##### `SummonType.java`

**Responsible for:** The categories of monster a summon effect can call — kin, animals, spiders, hounds, dragons, undead, demons, uniques and so on, including the "high" variants.

**enum `SummonType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/17 documented): ✘ `SUM_NONE`, `SUM_KIN`, `SUM_MONSTER`, `SUM_MONSTERS`, `SUM_ANIMAL`, `SUM_SPIDER`, `SUM_HOUND`, `SUM_HYDRA`, `SUM_AINU`, `SUM_DEMON`, `SUM_UNDEAD`, `SUM_DRAGON`, `SUM_HI_DEMON`, `SUM_HI_UNDEAD`, `SUM_HI_DRAGON`, `SUM_WRAITH`, `SUM_UNIQUE`


## middle/enums

##### `DamageAspect.java`

**Responsible for:** Describes how a RandomValue/dice expression should be resolved into a concrete number.

**enum `DamageAspect`** — class Javadoc: ✔ · type stamp: —

- Enum constants (5/5 documented): ✔ `MINIMIZE`, `AVERAGE`, `MAXIMIZE`, `EXTREMIFY`, `RANDOMIZE`


##### `EffectBaseType.java`

**Responsible for:** The base quantities an effect's magnitude can scale from (spell power, player or dungeon level, weapon damage, the player's HP, etc.).

**enum `EffectBaseType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/9 documented): ✘ `EFB_NONE`, `EFB_SPELL_POWER`, `EFB_PLAYER_LEVEL`, `EFB_DUNGEON_LEVEL`, `EFB_MAX_SIGHT`, `EFB_WEAPON_DAMAGE`, `EFB_PLAYER_HP`, `EFB_MONSTER_PERCENT_HP_GONE`, `EFB_MAX`


##### `EffectEnchant.java`

**Responsible for:** Which enchantment bonus an "enchant item" effect targets — to-hit, to-damage, both, or armour class.

**enum `EffectEnchant`** — class Javadoc: ✔ · type stamp: —

- Enum constants (5/5 documented): ✔ `EE_NONE`, `EE_TOBOTH`, `EE_TOHIT`, `EE_TODAM`, `EE_TOAC`


##### `EffectEnum.java`

**Responsible for:** The master table of every effect the game can apply (damage, heal, cure, teleport, summon, detect, the various bolt/ball/beam projections, …).

**enum `EffectEnum`** — class Javadoc: ✔ · type stamp: before 260815

- Enum constants (114/114 documented): ✔ `EF_NONE`, `EF_RANDOM`, `EF_DAMAGE`, `EF_HEAL_HP`, `EF_MON_HEAL_HP`, `EF_MON_HEAL_KIN`, `EF_NOURISH`, `EF_CRUNCH`, `EF_CURE`, `EF_TIMED_SET`, `EF_TIMED_INC`, `EF_TIMED_INC_NO_RES`, `EF_MON_TIMED_INC`, `EF_TIMED_DEC`, `EF_GLYPH`, `EF_WEB`, `EF_RESTORE_STAT`, `EF_DRAIN_STAT`, `EF_LOSE_RANDOM_STAT`, `EF_GAIN_STAT`, `EF_RESTORE_EXP`, `EF_GAIN_EXP`, `EF_DRAIN_LIGHT`, `EF_DRAIN_MANA`, `EF_RESTORE_MANA`, `EF_REMOVE_CURSE`, `EF_RECALL`, `EF_DEEP_DESCENT`, `EF_ALTER_REALITY`, `EF_MAP_AREA`, `EF_READ_MINDS`, `EF_DETECT_TRAPS`, `EF_DETECT_DOORS`, `EF_DETECT_STAIRS`, `EF_DETECT_ORE`, `EF_SENSE_GOLD`, `EF_DETECT_GOLD`, `EF_SENSE_OBJECTS`, `EF_DETECT_OBJECTS`, `EF_DETECT_LIVING_MONSTERS`, `EF_DETECT_VISIBLE_MONSTERS`, `EF_DETECT_INVISIBLE_MONSTERS`, `EF_DETECT_FEARFUL_MONSTERS`, `EF_IDENTIFY`, `EF_DETECT_EVIL`, `EF_DETECT_SOUL`, `EF_CREATE_STAIRS`, `EF_DISENCHANT`, `EF_ENCHANT`, `EF_RECHARGE`, `EF_PROJECT_LOS`, `EF_PROJECT_LOS_AWARE`, `EF_ACQUIRE`, `EF_WAKE`, `EF_SUMMON`, `EF_BANISH`, `EF_MASS_BANISH`, `EF_PROBE`, `EF_TELEPORT`, `EF_TELEPORT_TO`, `EF_TELEPORT_LEVEL`, `EF_RUBBLE`, `EF_GRANITE`, `EF_DESTRUCTION`, `EF_EARTHQUAKE`, `EF_LIGHT_LEVEL`, `EF_DARKEN_LEVEL`, `EF_LIGHT_AREA`, `EF_DARKEN_AREA`, `EF_SPOT`, `EF_SPHERE`, `EF_BALL`, `EF_BREATH`, `EF_ARC`, `EF_SHORT_BEAM`, `EF_LASH`, `EF_SWARM`, `EF_STRIKE`, `EF_STAR`, `EF_STAR_BALL`, `EF_BOLT`, `EF_BEAM`, `EF_BOLT_OR_BEAM`, `EF_LINE`, `EF_ALTER`, `EF_BOLT_STATUS`, `EF_BOLT_STATUS_DAM`, `EF_BOLT_AWARE`, `EF_TOUCH`, `EF_TOUCH_AWARE`, `EF_CURSE_ARMOR`, `EF_CURSE_WEAPON`, `EF_BRAND_WEAPON`, `EF_BRAND_AMMO`, `EF_BRAND_BOLTS`, `EF_CREATE_ARROWS`, `EF_TAP_DEVICE`, `EF_TAP_UNLIFE`, `EF_SHAPECHANGE`, `EF_CURSE`, `EF_COMMAND`, `EF_JUMP_AND_BITE`, `EF_MOVE_ATTACK`, `EF_SINGLE_COMBAT`, `EF_MELEE_BLOWS`, `EF_SWEEP`, `EF_BIZARRE`, `EF_WONDER`, `EF_SELECT`, `EF_SET_VALUE`, `EF_CLEAR_VALUE`, `EF_SCRAMBLE_STATS`, `EF_UNSCRAMBLE_STATS`, `EF_MAX`
- Fields (6/7 documented): ✔ `subType`, `requiresAiming`, `infoLabel`, `effectInfoEnum`, `description`, `menuFormat` — ✘ `numberOfArguments`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EffectEnum(EffectSubTypeEnum, boolean, String, int, EffectInfoEnum, String, String)` | before 260815 | ✔ |
| `getSubType()` | before 260815 | ✔ |
| `getAim()` | before 260815 | ✔ |
| `getInfoLabel()` | before 260815 | ✔ |
| `getNumberOfArguments()` | before 260815 | ✔ |
| `getEffectInfo()` | before 260815 | ✔ |
| `getDescription()` | before 260815 | ✔ |
| `getMenuFormat()` | before 260815 | ✔ |


##### `EffectInfoEnum.java`

**Responsible for:** The formatting categories used when building an effect's description string — each value tells the description code how to interpret an effect's parameters (as dice, healing, a cure, a timed effect, a summon, a breath, a bolt, …).

**enum `EffectInfoEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/20 documented): ✘ `EFINFO_NONE`, `EFINFO_DICE`, `EFINFO_HEAL`, `EFINFO_CONST`, `EFINFO_FOOD`, `EFINFO_CURE`, `EFINFO_TIMED`, `EFINFO_STAT`, `EFINFO_SEEN`, `EFINFO_SUMM`, `EFINFO_TELE`, `EFINFO_QUAKE`, `EFINFO_BALL`, `EFINFO_SPOT`, `EFINFO_BREATH`, `EFINFO_SHORT`, `EFINFO_LASH`, `EFINFO_BOLT`, `EFINFO_BOLTD`, `EFINFO_TOUCH`


##### `EffectNourish.java`

**Responsible for:** How a "nourish" (food) effect changes the player's food level — increase by, decrease by, set to, or increase up to a value.

**enum `EffectNourish`** — class Javadoc: ✔ · type stamp: —

- Enum constants (5/5 documented): ✔ `EN_NONE`, `EN_INC_BY`, `EN_DEC_BY`, `EN_SET_TO`, `EN_INC_TO`


##### `ElementInfoEnum.java`

**Responsible for:** Flags describing how an object kind relates to a damage element, mirroring the C original's EL_INFO_* flags (src/object.h) — whether the material is destroyed by, ignores, or is randomly affected by that element.

**enum `ElementInfoEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (3/3 documented): ✔ `EL_INFO_HATES`, `EL_INFO_IGNORE`, `EL_INFO_RANDOM`


##### `GlyphType.java`

**Responsible for:** The possible types of Glyph in the game

**enum `GlyphType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (3/3 documented): ✔ `GLYPH_NONE`, `GLYPH_WARDING`, `GLYPH_DECOY`


##### `MessageType.java`

**Responsible for:** Every kind of in-game message/sound event, each paired with the short string key used to look up its configured sound.

**enum `MessageType`** — class Javadoc: ✔ · type stamp: before 260815

- Enum constants (155/155 documented): ✔ `MSG_NONE`, `MSG_GENERIC`, `MSG_BIRTH`, `MSG_HIT`, `MSG_MISS`, `MSG_FLEE`, `MSG_DROP`, `MSG_KILL`, `MSG_LEVEL`, `MSG_DEATH`, `MSG_STUDY`, `MSG_TELEPORT`, `MSG_SHOOT`, `MSG_QUAFF`, `MSG_ZAP_ROD`, `MSG_WALK`, `MSG_TPOTHER`, `MSG_HITWALL`, `MSG_EAT`, `MSG_STORE1`, `MSG_STORE2`, `MSG_STORE3`, `MSG_STORE4`, `MSG_DIG`, `MSG_OPENDOOR`, `MSG_SHUTDOOR`, `MSG_TPLEVEL`, `MSG_BELL`, `MSG_NOTHING_TO_OPEN`, `MSG_LOCKPICK_FAIL`, `MSG_STAIRS_DOWN`, `MSG_HITPOINT_WARN`, `MSG_ACT_ARTIFACT`, `MSG_USE_STAFF`, `MSG_DESTROY`, `MSG_MON_HIT`, `MSG_MON_TOUCH`, `MSG_MON_PUNCH`, `MSG_MON_KICK`, `MSG_MON_CLAW`, `MSG_MON_BITE`, `MSG_MON_STING`, `MSG_MON_BUTT`, `MSG_MON_CRUSH`, `MSG_MON_ENGULF`, `MSG_MON_CRAWL`, `MSG_MON_DROOL`, `MSG_MON_SPIT`, `MSG_MON_GAZE`, `MSG_MON_WAIL`, `MSG_MON_SPORE`, `MSG_MON_BEG`, `MSG_MON_INSULT`, `MSG_MON_MOAN`, `MSG_RECOVER`, `MSG_BLIND`, `MSG_CONFUSED`, `MSG_POISONED`, `MSG_AFRAID`, `MSG_PARALYZED`, `MSG_DRUGGED`, `MSG_SPEED`, `MSG_SLOW`, `MSG_SHIELD`, `MSG_BLESSED`, `MSG_HERO`, `MSG_BERSERK`, `MSG_BOLD`, `MSG_PROT_EVIL`, `MSG_INVULN`, `MSG_SEE_INVIS`, `MSG_INFRARED`, `MSG_RES_ACID`, `MSG_RES_ELEC`, `MSG_RES_FIRE`, `MSG_RES_COLD`, `MSG_RES_POIS`, `MSG_STUN`, `MSG_CUT`, `MSG_STAIRS_UP`, `MSG_STORE_ENTER`, `MSG_STORE_LEAVE`, `MSG_STORE_HOME`, `MSG_MONEY1`, `MSG_MONEY2`, `MSG_MONEY3`, `MSG_SHOOT_HIT`, `MSG_STORE5`, `MSG_LOCKPICK`, `MSG_DISARM`, `MSG_IDENT_BAD`, `MSG_IDENT_EGO`, `MSG_IDENT_ART`, `MSG_BR_ELEMENTS`, `MSG_BR_FROST`, `MSG_BR_ELEC`, `MSG_BR_ACID`, `MSG_BR_GAS`, `MSG_BR_FIRE`, `MSG_BR_DISEN`, `MSG_BR_CHAOS`, `MSG_BR_SHARDS`, `MSG_BR_SOUND`, `MSG_BR_LIGHT`, `MSG_BR_DARK`, `MSG_BR_NETHER`, `MSG_BR_NEXUS`, `MSG_BR_TIME`, `MSG_BR_INERTIA`, `MSG_BR_GRAVITY`, `MSG_BR_PLASMA`, `MSG_BR_FORCE`, `MSG_SUM_MONSTER`, `MSG_SUM_AINU`, `MSG_SUM_UNDEAD`, `MSG_SUM_ANIMAL`, `MSG_SUM_SPIDER`, `MSG_SUM_HOUND`, `MSG_SUM_HYDRA`, `MSG_SUM_DEMON`, `MSG_SUM_DRAGON`, `MSG_SUM_HI_UNDEAD`, `MSG_SUM_HI_DRAGON`, `MSG_SUM_HI_DEMON`, `MSG_SUM_WRAITH`, `MSG_SUM_UNIQUE`, `MSG_WIELD`, `MSG_QUIVER`, `MSG_CURSED`, `MSG_RUNE`, `MSG_HUNGRY`, `MSG_NOTICE`, `MSG_AMBIENT_DAY`, `MSG_AMBIENT_NITE`, `MSG_AMBIENT_DNG1`, `MSG_AMBIENT_DNG2`, `MSG_AMBIENT_DNG3`, `MSG_AMBIENT_DNG4`, `MSG_AMBIENT_DNG5`, `MSG_CREATE_TRAP`, `MSG_SHRIEK`, `MSG_CAST_FEAR`, `MSG_HIT_GOOD`, `MSG_HIT_GREAT`, `MSG_HIT_SUPERB`, `MSG_HIT_HI_GREAT`, `MSG_HIT_HI_SUPERB`, `MSG_SPELL`, `MSG_PRAYER`, `MSG_KILL_UNIQUE`, `MSG_KILL_KING`, `MSG_DRAIN_STAT`, `MSG_MULTIPLY`, `MSG_SCRAMBLE`, `MSG_MAX`
- Fields (1/1 documented): ✔ `messageString`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MessageType(String)` | before 260815 | ✔ |
| `getMessageString()` | before 260815 | ✔ |


##### `Stats.java`

**Responsible for:** The player's primary statistics, mirroring the C original's STAT_* constants, generated in C from list-stats.h (player.h).

**enum `Stats`** — class Javadoc: ✔ · type stamp: on 260831

- Enum constants (7/7 documented): ✔ `STAT_NONE`, `STAT_STR`, `STAT_INT`, `STAT_WIS`, `STAT_DEX`, `STAT_CON`, `STAT_MAX`
- Fields (2/2 documented): ✔ `value`, `statString`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Stats(int, String)` | on 260831 | ✔ |
| `statNameToIdx(String)` | on 260831 | ✔ |
| `getStats(int)` | on 260831 | ✔ |
| `statIdxToName(Stats)` | on 260831 | ✔ |
| `getStatString()` | on 260831 | ✔ |
| `getValue()` | on 260831 | ✔ |


##### `TrapEnum.java`

**Responsible for:** The trap flags, mirroring the C original's TRF_* constants, generated in C from list-trap-flags.h (trap.h).

**enum `TrapEnum`** — class Javadoc: ✔ · type stamp: on 261001

- Enum constants (15/16 documented): ✔ `TRF_NONE`, `TRF_GLYPH`, `TRF_TRAP`, `TRF_VISIBLE`, `TRF_FLOOR`, `TRF_DOWN`, `TRF_PIT`, `TRF_ONETIME`, `TRF_MAGICAL`, `TRF_SAVE_THROW`, `TRF_SAVE_ARMOR`, `TRF_LOCK`, `TRF_DELAY`, `TRF_WEB`, `TRF_MAX` — ✘ `TRF_INVISIBLE`
- Fields (1/1 documented): ✔ `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TrapEnum(String)` | on 261001 | ✔ |
| `getDescription()` | on 261001 | ✔ |


## middle/game

##### `GameWorld.java`

**Responsible for:** The game-clock and turn-loop machinery — the port of C's game-world.c.

**class `GameWorld`** — class Javadoc: ✔ · type stamp: before 261006

- Fields (5/5 documented): ✔ `extractEnergy`, `characterDungeon`, `dayCount`, `player`, `currentCave`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `GameWorld()` | before 261006 | ✔ |
| `turnEnergy(int)` | before 261006 | ✔ |
| `setCharacterDungeon(boolean)` | before 260907 | ✔ |
| `getDaycount()` | — | ✔ |
| `hasCharacterDungeon()` | — | ✔ |
| `isDaytime()` | before 261006 | ✔ |
| `runGameLoop()` | before 261006 | ✔ |
| `rechargeObjects()` | before 261006 | ✔ |
| `rechargedNotice(ItemObject, boolean)` | before 261006 | ✔ |
| `onNewLevel()` | before 261006 | ✔ |
| `onLeaveLevel()` | before 261006 | ✔ |
| `processPlayerCleanup()` | before 261006 | ✔ |
| `processWorld()` | before 261006 | ✔ |
| `decreaseTimeouts()` | before 261006 | ✔ |
| `playAmbientSound()` | before 261006 | ✔ |
| `makeNoise()` | before 261006 | ✔ |
| `updateScent()` | before 261006 | ✔ |
| `processPlayer()` | before 261006 | ✔ |


##### `Hint.java`

**Responsible for:** A single piece of gameplay advice, loaded from hints.txt.

**class `Hint`** — class Javadoc: ✔ · type stamp: before 261001

- Fields (1/1 documented): ✔ `hint`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Hint(String)` | before 261001 | ✔ |
| `getHint()` | before 261001 | ✔ |


##### `Name.java`

**Responsible for:** One section of the name file — the domain form a NamesParseRecord becomes once uk.co.jackoftradesltd.backend.parser.names.NamesAssembler has parsed its section number into an int and copied its words.

**class `Name`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (2/2 documented): ✔ `section`, `word`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Name(int, List<String>)` | on 260902 | ✔ |
| `getSection()` | on 260902 | ✔ |
| `getWord()` | on 260902 | ✔ |


##### `NameCreator.java`

**Responsible for:** Random word generation for player names, scroll titles and artifact names — the port of C's randname.c, W.

**class `NameCreator`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (4/4 documented): ✔ `logger`, `S_WORD`, `E_WORD`, `TOTAL`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `randnameMake(RandnameType, int, int)` | on 260831 | ✔ |
| `playerRandomName()` | on 260831 | ✔ |
| `buildProbs(int, int, int, RandnameType)` | on 260831 | ✔ |


## middle/game/enums

##### `CommandArgumentType.java`

**Responsible for:** The kind of value a command argument carries - the port of C's enum cmd_arg_type (cmd-core.h).

**enum `CommandArgumentType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (8/8 documented): ✔ `arg_NONE`, `arg_STRING`, `arg_CHOICE`, `arg_ITEM`, `arg_NUMBER`, `arg_DIRECTION`, `arg_TARGET`, `arg_POINT`


##### `CommandCode.java`

**Responsible for:** Every command the game engine can be asked to perform - the port of C's enum cmd_code (cmd-core.h).

**enum `CommandCode`** — class Javadoc: ✔ · type stamp: before 260815

- Enum constants (116/116 documented): ✔ `CMD_NULL`, `CMD_LOADFILE`, `CMD_NEWGAME`, `CMD_BIRTH_INIT`, `CMD_BIRTH_RESET`, `CMD_CHOOSE_RACE`, `CMD_CHOOSE_CLASS`, `CMD_BUY_STAT`, `CMD_SELL_STAT`, `CMD_RESET_STATS`, `CMD_REFRESH_STATS`, `CMD_ROLL_STATS`, `CMD_PREV_STATS`, `CMD_NAME_CHOICE`, `CMD_HISTORY_CHOICE`, `CMD_ACCEPT_CHARACTER`, `CMD_GO_UP`, `CMD_GO_DOWN`, `CMD_WALK`, `CMD_JUMP`, `CMD_PATHFIND`, `CMD_INSCRIBE`, `CMD_UNINSCRIBE`, `CMD_AUTOINSCRIBE`, `CMD_TAKEOFF`, `CMD_WIELD`, `CMD_DROP`, `CMD_BROWSE_SPELL`, `CMD_STUDY`, `CMD_CAST`, `CMD_USE_STAFF`, `CMD_USE_WAND`, `CMD_USE_ROD`, `CMD_ACTIVATE`, `CMD_EAT`, `CMD_QUAFF`, `CMD_READ_SCROLL`, `CMD_REFILL`, `CMD_USE`, `CMD_FIRE`, `CMD_THROW`, `CMD_PICKUP`, `CMD_AUTOPICKUP`, `CMD_IGNORE`, `CMD_DISARM`, `CMD_REST`, `CMD_TUNNEL`, `CMD_OPEN`, `CMD_CLOSE`, `CMD_RUN`, `CMD_EXPLORE`, `CMD_NAVIGATE_UP`, `CMD_NAVIGATE_DOWN`, `CMD_HOLD`, `CMD_ALTER`, `CMD_STEAL`, `CMD_SLEEP`, `CMD_SELL`, `CMD_BUY`, `CMD_STASH`, `CMD_RETRIEVE`, `CMD_SPOIL_ARTIFACT`, `CMD_SPOIL_MON`, `CMD_SPOIL_MON_BRIEF`, `CMD_SPOIL_OBJ`, `CMD_WIZ_ACQUIRE`, `CMD_WIZ_ADVANCE`, `CMD_WIZ_BANISH`, `CMD_WIZ_CHANGE_ITEM_QUANTITY`, `CMD_WIZ_COLLECT_DISCONNECT_STATS`, `CMD_WIZ_COLLECT_OBJ_MON_STATS`, `CMD_WIZ_COLLECT_PIT_STATS`, `CMD_WIZ_CREATE_ALL_ARTIFACT`, `CMD_WIZ_CREATE_ALL_ARTIFACT_FROM_TVAL`, `CMD_WIZ_CREATE_ALL_OBJ`, `CMD_WIZ_CREATE_ALL_OBJ_FROM_TVAL`, `CMD_WIZ_CREATE_ARTIFACT`, `CMD_WIZ_CREATE_OBJ`, `CMD_WIZ_CREATE_TRAP`, `CMD_WIZ_CURE_ALL`, `CMD_WIZ_CURSE_ITEM`, `CMD_WIZ_DETECT_ALL_LOCAL`, `CMD_WIZ_DETECT_ALL_MONSTERS`, `CMD_WIZ_DISPLAY_KEYLOG`, `CMD_WIZ_DUMP_LEVEL_MAP`, `CMD_WIZ_EDIT_PLAYER_EXP`, `CMD_WIZ_EDIT_PLAYER_GOLD`, `CMD_WIZ_EDIT_PLAYER_START`, `CMD_WIZ_EDIT_PLAYER_STAT`, `CMD_WIZ_HIT_ALL_LOS`, `CMD_WIZ_INCREASE_EXP`, `CMD_WIZ_JUMP_LEVEL`, `CMD_WIZ_LEARN_OBJECT_KINDS`, `CMD_WIZ_MAGIC_MAP`, `CMD_WIZ_PEEK_NOISE_SCENT`, `CMD_WIZ_PERFORM_EFFECT`, `CMD_WIZ_PLAY_ITEM`, `CMD_WIZ_PUSH_OBJECT`, `CMD_WIZ_QUERY_FEATURE`, `CMD_WIZ_QUERY_SQUARE_FLAG`, `CMD_WIZ_QUIT_NO_SAVE`, `CMD_WIZ_RECALL_MONSTER`, `CMD_WIZ_RERATE`, `CMD_WIZ_REROLL_ITEM`, `CMD_WIZ_STAT_ITEM`, `CMD_WIZ_SUMMON_NAMED`, `CMD_WIZ_SUMMON_RANDOM`, `CMD_WIZ_TELEPORT_RANDOM`, `CMD_WIZ_TELEPORT_TO`, `CMD_WIZ_TWEAK_ITEM`, `CMD_WIZ_WIPE_RECALL`, `CMD_WIZ_WIZARD_LIGHT`, `CMD_RETIRE`, `CMD_HELP`, `CMD_REPEAT`, `CMD_COMMAND_MONSTER`


##### `CommandContext.java`

**Responsible for:** The situation a command is issued in - the port of C's enum cmd_context (cmd-core.h).

**enum `CommandContext`** — class Javadoc: ✔ · type stamp: —

- Enum constants (5/5 documented): ✔ `CTX_INIT`, `CTX_BIRTH`, `CTX_GAME`, `CTX_STORE`, `CTX_DEATH`


##### `CommandReturnCodes.java`

**Responsible for:** The outcome of trying to read a named argument off a command - the port of C's cmd_return_codes (cmd-core.h).

**enum `CommandReturnCodes`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `CMD_OK`, `CMD_ARG_NOT_PRESENT`, `CMD_ARG_WRONG_TYPE`, `CMD_ARG_ABORTED`
- Fields (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CommandReturnCodes(int)` | — | ✔ |
| `fromValue(int)` | — | ✔ |
| `getValue()` | — | ✔ |


## middle/game/event

##### `EventDataMissile.java`

**Responsible for:** GameEventData payload for EVENT_MISSILE: a thrown or fired object in flight, whether the player can see it, and the grid it currently occupies.

**class `EventDataMissile`** — class Javadoc: ✔ · type stamp: on 260929

- Fields (4/4 documented): ✔ `itemObject`, `seen`, `y`, `x`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EventDataMissile(ItemObject, boolean, int, int)` | on 260929 | ✔ |
| `getItemObject()` | on 260929 | ✔ |
| `isSeen()` | on 260929 | ✔ |
| `getY()` | on 260929 | ✔ |
| `getX()` | on 260929 | ✔ |


##### `EventHandlerInterface.java`

**Responsible for:** Callback contract for a registered game-event handler.

**interface `EventHandlerInterface`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dispatch(GameEventType, GameEventData)` | — | ✔ |


##### `EventsBusHandler.java`

**Responsible for:** The concrete event bus: the EventsHandler implementation that holds the per-type registry of EventHandlerInterface listeners and supplies the four abstract primitives (#eventAddHandler, #eventRemoveHandler, #eventRemoveHandlerType and #gameEventDispatch) the interface's default convenience layer ...

**class `EventsBusHandler`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `handlers`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EventsBusHandler()` | — | ✔ |
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✔ |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✔ |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✔ |
| `eventRemoveHandlerType(GameEventType)` | — | ✔ |
| `init()` | — | ✔ |


##### `EventsHandler.java`

**Responsible for:** The event bus abstraction: the interface through which game logic broadcasts GameEventType notifications to whatever listeners - a front-end, a test spy, or nothing at all - have registered for them.

**interface `EventsHandler`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✔ |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✔ |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✔ |
| `eventRemoveHandlerType(GameEventType)` | — | ✔ |
| `init()` | — | ✔ |
| `eventRemoveAllHandlers()` | — | ✔ |
| `eventAddHandlerSet(List<GameEventType>, EventHandlerInterface)` | — | ✔ |
| `eventRemoveHandlerSet(List<GameEventType>, EventHandlerInterface)` | — | ✔ |
| `eventSignal(GameEventType)` | — | ✔ |
| `eventSignalColourString(GameEventType, AngbandDisplayCharacter[])` | on 260927 | ✔ |
| `eventSignalFullStat(GameEventType, int, int, int, int)` | on 260927 | ✔ |
| `eventSignalStat(GameEventType, int, int)` | before 260926 | ✔ |
| `eventSignalFlag(GameEventType, boolean)` | — | ✔ |
| `eventSignalPoint(GameEventType, int, int)` | — | ✔ |
| `eventSignalPoint(GameEventType, Loc)` | — | ✔ |
| `eventSignalString(GameEventType, String)` | — | ✔ |
| `eventSignalStrings(GameEventType, String)` | before 260926 | ✔ |
| `eventSignalMessage(GameEventType, MessageType, String)` | — | ✔ |
| `eventSignalBirthpoints(GameEventType, Map<Stats,Integer>, Map<Stats,Integer>, int)` | — | ✔ |
| `eventSignalBlast(GameEventType, int, int, ArrayList<Integer>, boolean, ArrayList<Boolean>, ArrayList<Loc>, Loc)` | — | ✔ |
| `eventSignalBolt(GameEventType, ProjectionEnum, boolean, boolean, boolean, EventDataGrid, EventDataGrid)` | — | ✔ |
| `eventSignalMissile(GameEventType, ItemObject, boolean, int, int)` | — | ✔ |
| `eventSignalSize(GameEventType, int, int)` | — | ✔ |
| `eventSignalTunnel(GameEventType, int, int, int, int, int, boolean)` | — | ✔ |
| `eventSignalLongStat(GameEventType, long, long)` | on 260927 | ✔ |
| `eventSignalLong(GameEventType, long)` | on 260927 | ✔ |
| `eventSignalInt(GameEventType, int)` | on 260927 | ✔ |
| `eventSignalMonInfo(GameEventType, int, int, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)` | on 260929 | ✔ |
| `eventSignalPlayerSpeed(GameEventType, int, boolean, int, int)` | on 260929 | ✔ |


## middle/game/event/birthhandlers

##### `UIBirth.java`

**Responsible for:** The birth-screen event handlers: the enter/leave pair for character creation, and their registration.

**class `UIBirth`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `logger`, `coreSender`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `UIBirth(Sender<CoreMessage>)` | — | ✔ |
| `uiInitBirthstateHandlers()` | — | ✔ |
| `uiEnterBirthscreen(GameEventType, GameEventData)` | — | ✔ |
| `uiLeaveBirthscreen(GameEventType, GameEventData)` | — | ✔ |


## middle/game/event/eventhandlers

##### `InitHandlers.java`

**Responsible for:** The middle end's start-up event handlers, and the one place they are registered.

**class `InitHandlers`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `logger`, `coreSender`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `InitHandlers(Sender<CoreMessage>)` | — | ✔ |
| `initHandlers()` | — | ✔ |
| `enterInit(GameEventType, GameEventData)` | — | ✔ |
| `splashScreenNote(GameEventType, GameEventData)` | — | ✔ |
| `leaveInit(GameEventType, GameEventData)` | — | ✔ |
| `enterGame(GameEventType, GameEventData)` | — | ✔ |
| `leaveGame(GameEventType, GameEventData)` | — | ✔ |
| `enterWorld(GameEventType, GameEventData)` | — | ✔ |
| `leaveWorld(GameEventType, GameEventData)` | — | ✔ |


##### `RedrawHandlers.java`

**Responsible for:** The core-side handlers that forward uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw events across the core-to-front-end boundary as CoreMessage.GameEventCoreMessages.

**class `RedrawHandlers`** — class Javadoc: ✔ · type stamp: before 260926

- Fields (1/1 documented): ✔ `coreSender`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RedrawHandlers(Sender<CoreMessage>)` | — | ✔ |
| `initHandlers()` | — | ✔ |
| `eventHP(GameEventType, GameEventData)` | — | ✔ |
| `eventSP(GameEventType, GameEventData)` | — | ✔ |
| `eventName(GameEventType, GameEventData)` | — | ✔ |
| `eventRaceClass(GameEventType, GameEventData)` | — | ✔ |
| `eventTitle(GameEventType, GameEventData)` | — | ✔ |


## middle/game/event/projection

##### `Projection.java`

**Responsible for:** The definition of one projection type, as loaded from projection.txt — its identity, the descriptions used in various contexts, the damage-scaling fraction, divisor and cap, the message and display colour, and whether it is obvious or wakes monsters.

**class `Projection`** — class Javadoc: ✔ · type stamp: before 261001

- Fields (16/16 documented): ✔ `projection`, `name`, `type`, `description`, `playerDescription`, `blindDescription`, `lashDescription`, `numerator`, `denominator`, `diceDenominator`, `divisor`, `damageCap`, `msgt`, `isObvious`, `willWake`, `colour`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Projection(ProjectionEnum, String, ProjectionType, String, String, String, String, int, Random, int, int, MessageType, boolean, boolean, ColourEnum)` | before 261001 | ✔ |
| ctor `Projection(ProjectionEnum, String, ProjectionType, String, String, String, String, int, int, int, int, MessageType, boolean, boolean, ColourEnum)` | before 261001 | ✔ |
| `getName()` | before 261001 | ✔ |
| `getType()` | before 261001 | ✔ |
| `getLashDescription()` | before 261001 | ✔ |
| `getProjection()` | before 261001 | ✔ |
| `getPlayerDescription()` | before 261001 | ✔ |
| `toString()` | before 261001 | ✔ |


##### `Source.java`

**Responsible for:** Where an effect came from.

**record `Source`** — class Javadoc: ✔ · type stamp: on 260829

- Record components (via `@param`) (2/2 documented): ✔ `what`, `which`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sourceNone()` | on 260829 | ✔ |
| `sourceTrap(Trap)` | on 260829 | ✔ |
| `sourceMonster(Monster)` | on 260829 | ✔ |
| `sourcePlayer()` | on 260829 | ✔ |
| `sourceObject(ItemObject)` | on 260829 | ✔ |
| `sourceChestTrap(ChestTrap)` | on 260829 | ✔ |


##### `SourceWhat.java`

**Responsible for:** Discriminator that says what kind of thing an effect came from.

**enum `SourceWhat`** — class Javadoc: ✔ · type stamp: on 260829

- Enum constants (6/6 documented): ✔ `SRC_NONE`, `SRC_TRAP`, `SRC_PLAYER`, `SRC_MONSTER`, `SRC_OBJECT`, `SRC_CHEST_TRAP`


##### `SourceWhich.java`

**Responsible for:** The payload of a Source: the one thing the effect came from.

**interface `SourceWhich`** — class Javadoc: ✔ · type stamp: on 260829

**record `SourceWhich.TrapRecord`** — class Javadoc: ✔ · type stamp: on 260829

- Record components (via `@param`) (1/1 documented): ✔ `trap`

**record `SourceWhich.MonsterRecord`** — class Javadoc: ✔ · type stamp: on 260829

- Record components (via `@param`) (1/1 documented): ✔ `monster`

**record `SourceWhich.ObjectRecord`** — class Javadoc: ✔ · type stamp: on 260829

- Record components (via `@param`) (1/1 documented): ✔ `object`

**record `SourceWhich.ChestTrapRecord`** — class Javadoc: ✔ · type stamp: on 260829

- Record components (via `@param`) (1/1 documented): ✔ `chestTrap`


## middle/game/gameengine

##### `Command.java`

**Responsible for:** A single queued command: what to do, where, how many times, and with which arguments - the port of C's struct command (cmd-core.h).

**class `Command`** — class Javadoc: ✔ · type stamp: on 260830

- Fields (7/7 documented): ✔ `CMD_MAX_ARGS`, `context`, `code`, `nrepeats`, `backgroundCommand`, `args`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Command(CommandContext, CommandCode, int, int, List<CommandArgument>)` | on 260830 | ✔ |
| `clone()` | on 260830 | ✔ |
| `getContext()` | on 260830 | ✔ |
| `setContext(CommandContext)` | on 260830 | ✔ |
| `getCode()` | on 260830 | ✔ |
| `getNrepeats()` | on 260830 | ✔ |
| `setNrepeats(int)` | on 260830 | ✔ |
| `repeated()` | on 260830 | ✔ |
| `getBackgroundCommand()` | on 260830 | ✔ |
| `getArgs()` | on 260830 | ✔ |
| `setArgTarget(String, DirectionEnum)` | on 260830 | ✔ |
| `setArgString(String, String)` | on 260830 | ✔ |
| `setArgPoint(String, Loc)` | on 260830 | ✔ |
| `setArgNumber(String, int)` | on 260830 | ✔ |
| `setArgItem(String, ItemObject)` | on 260830 | ✔ |
| `setArgDirection(String, DirectionEnum)` | on 260830 | ✔ |
| `setArgChoice(String, int)` | on 260830 | ✔ |
| `setArg(String, CommandArgumentType, CommandArgumentData)` | on 260830 | ✔ |
| `getArgChoice(String)` | on 260830 | ✔ |
| `getArgDirection(String)` | on 260830 | ✔ |
| `getArgItem(String)` | on 260830 | ✔ |
| `getArgNumber(String)` | on 260830 | ✔ |
| `getArgPoint(String)` | on 260830 | ✔ |
| `getArgString(String)` | on 260830 | ✔ |
| `getArgTarget(String)` | on 260830 | ✔ |
| `getDirection(String, boolean)` | on 260830 | ✔ |
| `getItem(String, String, String, Predicate<ItemObject>, Flag<GetItemFlags>)` | on 260830 | ✔ |
| `getString(String, String, String, String)` | on 260830 | ✔ |
| `getTarget(String)` | on 260830 | ✔ |
| `getQuantity(String, int)` | on 260830 | ✔ |
| `getSpell(String, String, Predicate<ItemObject>, String, BiPredicate<Player,MagicSpell>, String)` | on 260830 | ✔ |
| `findArg(String)` | on 260830 | ✔ |
| `getSpellFromBook(Player, String, ItemObject, String, BiPredicate<Player,MagicSpell>)` | on 260830 | ✔ |
| `getEffectFromList(String, String, List<Effect>, int, boolean)` | on 260830 | ✔ |
| `playerObjectToBook(Player, ItemObject)` | on 260830 | ✔ |
| `setBackgroundCommand(int)` | on 260830 | ✔ |


##### `CommandArgument.java`

**Responsible for:** One named argument attached to a Command - the port of C's struct cmd_arg (cmd-core.h).

**class `CommandArgument`** — class Javadoc: ✔ · type stamp: before 261001

- Fields (3/3 documented): ✔ `type`, `data`, `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CommandArgument(CommandArgumentType, CommandArgumentData, String)` | before 261001 | ✔ |
| `getName()` | before 261001 | ✔ |
| `getData()` | before 261001 | ✔ |
| `getType()` | before 261001 | ✔ |
| `update(CommandArgumentType, CommandArgumentData, String)` | before 261001 | ✔ |
| `copy()` | before 261001 | ✔ |


##### `CommandArgumentData.java`

**Responsible for:** The value carried by a command argument - the port of C's union cmd_arg_data (cmd-core.h), reworked from an untagged union into a type-safe tagged one.

**interface `CommandArgumentData`** — class Javadoc: ✔ · type stamp: before 261012

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `type()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |


##### `CommandGetter.java`

**Responsible for:** The boundary through which the game loop asks the user interface to fetch a command - the port of C's cmd_get_hook function pointer (cmd-core.c).

**interface `CommandGetter`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getCommand(CommandContext, CommandQueue)` | — | ✔ |


##### `CommandGetterHolder.java`

**Responsible for:** The static switchboard for the CommandGetter boundary: it holds the one implementation currently installed and hands it to callers.

**class `CommandGetterHolder`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (2/2 documented): ✔ `logger`, `instance`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CommandGetterHolder()` | before 261014 | ✔ |
| `getInstance()` | before 261014 | ✔ |
| `setInstance(CommandGetter)` | before 261014 | ✔ |
| `resetInstance()` | before 261014 | ✔ |


##### `CommandHandlerFunction.java`

**Responsible for:** The code that performs a command - the port of C's cmd_handler_fn function-pointer type (cmd-core.h, typedef void (*cmd_handler_fn)(struct command *cmd)).

**interface `CommandHandlerFunction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `handle(Command)` | — | ✔ |


##### `CommandInfo.java`

**Responsible for:** One row of the command dispatch table: a command code wired to the handler that performs it, plus the flags governing how it repeats and spends energy - the port of C's struct command_info (cmd-core.c).

**record `CommandInfo`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `command`, `verb`, `function`, `repeatAllowed`, `canUseEnergy`, `autoRepeatNumber`


##### `CommandProcessor.java`

**Responsible for:** Dispatches commands to their handlers - the port of the command-execution core in C's cmd-core.c, and the owner of the game_cmds[] table.

**class `CommandProcessor`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `gameCommands`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildTable()` | — | ✔ |
| `containsCommand(CommandCode)` | — | ✔ |
| `processCommand(CommandContext, Command, CommandQueue)` | — | ✔ |
| `getCommandInfo(CommandCode)` | — | ✔ |
| `put(Map<CommandCode,CommandInfo>, CommandInfo)` | — | ✔ |
| `getCommandVerb(CommandCode)` | — | ✔ |


##### `CommandQueue.java`

**Responsible for:** The FIFO queue of commands waiting to be carried out - the port of the command-queue half of C's cmd-core.c (cmd_queue plus its push/pop functions).

**class `CommandQueue`** — class Javadoc: ✔ · type stamp: —

- Fields (6/6 documented): ✔ `repeatPrevAllowed`, `repeating`, `commandQueue`, `lastCommand`, `currentCommand`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CommandQueue(Player)` | — | ✔ |
| `setPlayer(Player)` | — | ✔ |
| `getNextCommand()` | — | ✔ |
| `viewPrevCommand()` | — | ✔ |
| `commandQueuePeek()` | — | ✔ |
| `push(CommandCode, int)` | — | ✔ |
| `push(CommandCode)` | — | ✔ |
| `push(Command)` | — | ✔ |
| `execute(CommandContext)` | — | ✔ |
| `flush()` | — | ✔ |
| `release()` | — | ✔ |
| `commandPop(CommandContext)` | — | ✔ |
| `setRepeat(int)` | — | ✔ |
| `setRepeating(boolean)` | — | ✔ |
| `setRepeatPrevAllowed(boolean)` | — | ✔ |
| `cancelRepeat()` | — | ✔ |
| `getNrepeats()` | — | ✔ |
| `disableRepeat()` | — | ✔ |
| `disableRepeatFloorItem()` | — | ✔ |


##### `Core.java`

**Responsible for:** The middle end's body: what the game thread runs, kept clear of Swing's event dispatch thread (EDT) so game work never blocks repainting and input never blocks the game.

**class `Core`** — class Javadoc: ✔ · type stamp: before 260813

- Fields (5/5 documented): ✔ `logger`, `gameConstantsFinishedLoading`, `gameEngine`, `coreChannel`, `startupOptions`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getGameEngine()` | before 260813 | ✔ |
| ctor `Core(CoreChannel, StartupOptions)` | before 260813 | ✔ |
| `gameLoop()` | before 260813 | ✔ |
| `handleChannelOutput(UIMessage)` | before 261010 | ✔ |
| `getCoreChannel()` | before 261010 | ✔ |


##### `DefaultCommandGetter.java`

**Responsible for:** The always-fail CommandGetter installed when no front-end has set a real one - the port of C's unset cmd_get_hook and of textui_get_cmd's "haven't got a command" path, which likewise returns errr 1.

**class `DefaultCommandGetter`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getCommand(CommandContext, CommandQueue)` | — | ✔ |


##### `GameEngine.java`

**Responsible for:** The top-level game runtime: a singleton that performs middle-end start-up - game state, the event bus and the game constants - roughly the Java counterpart of the C original's initialization bootstrap, init_angband() (src/init.c) as called from main() (src/main.c).

**class `GameEngine`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (3/3 documented): ✔ `logger`, `eventsBusHandler`, `instance`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `GameEngine()` | before 261010 | ✔ |
| `getGame()` | before 261010 | ✔ |
| `loadGameConstants(Core)` | before 261010 | ✔ |
| `getEventsBusHandler()` | before 261010 | ✔ |
| `setEventsBusHandler(EventsHandler)` | before 261010 | ✔ |
| `initGame()` | before 261010 | ✔ |


##### `GameState.java`

**Responsible for:** The mutable state of the game currently in progress — the port's home for the scattered file-scope globals that C uses as its single implicit "current game" object.

**class `GameState`** — class Javadoc: ✔ · type stamp: before 260903

- Fields (7/7 documented): ✔ `mainPlayer`, `cave`, `commandQueue`, `turn`, `seedRandart`, `seedFlavour`, `characterGenerated`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getTurn()` | before 260903 | ✔ |
| `setTurn(int)` | on 260903 | ✔ |
| `getDaycount()` | before 260903 | ✔ |
| `incrementTurn()` | before 260903 | ✔ |
| `resetTurnFromSave(int)` | before 260903 | ✔ |
| `getPlayer()` | before 260903 | ✔ |
| `setPlayer(Player)` | before 260903 | ✔ |
| `getCave()` | before 260903 | ✔ |
| `setCave(Chunk)` | before 260903 | ✔ |
| `getCommandQueue()` | before 260903 | ✔ |
| `setCommandQueue(CommandQueue)` | before 260903 | ✔ |
| `getCharacterGenerated()` | on 260907 | ✔ |
| `setCharacterGenerated(boolean)` | on 260908 | ✔ |
| `getSeedFlavour()` | on 260908 | ✔ |
| `setSeedFlavour(long)` | on 260908 | ✔ |
| `getSeedRandart()` | — | ✘ |
| `setSeedRandart(long)` | — | ✘ |


## middle/game/gameengine/argumentdata

##### `ArgumentChoice.java`

**Responsible for:** A command-argument value holding a menu/selection index - the arg_CHOICE variant of CommandArgumentData, from the int choice member of C's union cmd_arg_data (cmd-core.h).

**record `ArgumentChoice`** — class Javadoc: ✔ · type stamp: before 261012

- Record components (via `@param`) (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `type()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |


##### `ArgumentDirection.java`

**Responsible for:** A command-argument value holding a movement direction - the arg_DIRECTION variant of CommandArgumentData, from the int direction member of C's union cmd_arg_data (cmd-core.h).

**record `ArgumentDirection`** — class Javadoc: ✔ · type stamp: before 261012

- Record components (via `@param`) (1/1 documented): ✔ `value`
- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `type()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |
| ctor `ArgumentDirection(DirectionEnum)` | on 261012 | ✔ |


##### `ArgumentItem.java`

**Responsible for:** A command-argument value referring to a game object - the arg_ITEM variant of CommandArgumentData, from the struct object *obj member of C's union cmd_arg_data (cmd-core.h).

**record `ArgumentItem`** — class Javadoc: ✔ · type stamp: before 261012

- Record components (via `@param`) (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `type()` | before 261012 | ✔ |
| `getValue()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |


##### `ArgumentNumber.java`

**Responsible for:** A command-argument value holding a plain number - the arg_NUMBER variant of CommandArgumentData, from the int number member of C's union cmd_arg_data (cmd-core.h).

**record `ArgumentNumber`** — class Javadoc: ✔ · type stamp: before 261012

- Record components (via `@param`) (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `type()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |


##### `ArgumentPoint.java`

**Responsible for:** A command-argument value holding a grid location - the arg_POINT variant of CommandArgumentData, from the struct loc point member of C's union cmd_arg_data (cmd-core.h).

**record `ArgumentPoint`** — class Javadoc: ✔ · type stamp: before 261012

- Record components (via `@param`) (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ArgumentPoint(Loc)` | on 261012 | ✔ |
| `type()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |


##### `ArgumentString.java`

**Responsible for:** A command-argument value holding a text string - the arg_STRING variant of CommandArgumentData, from the const char *string member of C's union cmd_arg_data (cmd-core.h).

**record `ArgumentString`** — class Javadoc: ✔ · type stamp: before 261012

- Record components (via `@param`) (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `type()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |


##### `ArgumentTarget.java`

**Responsible for:** A command-argument value holding a target - the arg_TARGET variant of CommandArgumentData.

**record `ArgumentTarget`** — class Javadoc: ✔ · type stamp: before 261012

- Record components (via `@param`) (1/1 documented): ✔ `value`
- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `type()` | before 261012 | ✔ |
| `copy()` | before 261012 | ✔ |
| ctor `ArgumentTarget(DirectionEnum)` | on 261012 | ✔ |


## middle/game/globals

##### `GameConstants.java`

**Responsible for:** The global holder for the game's tunable constants and the startup entry point that loads all game data — the Java port's equivalent of the C original's z_info constants (struct angband_constants, init.h) and its init_game_constants / init_arrays bootstrap in init.c.

**class `GameConstants`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setCaveProfileMax(int)` | before 261010 | ✔ |

**record `GameConstants.NameValuePair`** — class Javadoc: ✔ · type stamp: before 261010

- Record components (via `@param`) (2/2 documented): ✔ `name`, `value`
- Fields (13/13 documented): ✔ `MAX_PVAL`, `MAX_COMMAND_ARGUMENTS`, `ANGBAND_INI`, `ANGBAND_SYS`, `iniFile`, `caveProfileMax`, `data`, `gameInProgress`, `initialized`, `screensaverActive`, `storeMax`, `canUseGraphics`, `changeTileSize`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadGameConstants()` | before 261010 | ✔ |
| `init(Core)` | before 261010 | ✔ |
| ctor `GameConstants()` | before 261010 | ✔ |
| `getValues(String, String)` | before 261010 | ✔ |
| `getStoreMax()` | before 261010 | ✔ |
| `setStoreMax(int)` | before 261010 | ✔ |
| `getRandartActivationsMax()` | before 261010 | ✔ |
| `getCaveProfileMax()` | before 261010 | ✔ |
| `getLevelMaxMonsters()` | before 261010 | ✔ |
| `getMonGenChance()` | before 261010 | ✔ |
| `getMonGenLevelMin()` | before 261010 | ✔ |
| `getMonGenTownDay()` | before 261010 | ✔ |
| `getMonGenTownNight()` | before 261010 | ✔ |
| `getMonGenReproMax()` | before 261010 | ✔ |
| `getMonGenOodChance()` | before 261010 | ✔ |
| `getMonGenOodAmount()` | before 261010 | ✔ |
| `getMonGenGroupMax()` | before 261010 | ✔ |
| `getMonGenGroupDist()` | before 261010 | ✔ |
| `getMonPlayBreakGlyph()` | before 261010 | ✔ |
| `getMonPlayMultRate()` | before 261010 | ✔ |
| `getMonPlayLifeDrain()` | before 261010 | ✔ |
| `getMonPlayFleeRange()` | before 261010 | ✔ |
| `getMonPlayTurnRange()` | before 261010 | ✔ |
| `getDunGenCentMax()` | before 261010 | ✔ |
| `getDunGenDoorMax()` | before 261010 | ✔ |
| `getDunGenWallMax()` | before 261010 | ✔ |
| `getDunGenTunnMax()` | before 261010 | ✔ |
| `getDunGenAmtRoom()` | before 261010 | ✔ |
| `getDunGenAmtItem()` | before 261010 | ✔ |
| `getDunGenAmtGold()` | before 261010 | ✔ |
| `getDunGenPitMax()` | before 261010 | ✔ |
| `getWorldMaxDepth()` | before 261010 | ✔ |
| `getWorldDayLength()` | before 261010 | ✔ |
| `getWorldDungeonHgt()` | before 261010 | ✔ |
| `getWorldDungeonWid()` | before 261010 | ✔ |
| `getWorldTownHgt()` | before 261010 | ✔ |
| `getWorldTownWid()` | before 261010 | ✔ |
| `getWorldFeelingTotal()` | before 261010 | ✔ |
| `getWorldFeelingNeed()` | before 261010 | ✔ |
| `getWorldStairSkip()` | before 261010 | ✔ |
| `getWorldMoveEnergy()` | before 261010 | ✔ |
| `getCarryCapPackSize()` | before 261010 | ✔ |
| `getCarryCapQuiverSize()` | before 261010 | ✔ |
| `getCarryCapQuiverSlotSize()` | before 261010 | ✔ |
| `getCarryCapThrownQuiverMult()` | before 261010 | ✔ |
| `getCarryCapFloorSize()` | before 261010 | ✔ |
| `getStoreInvenMax()` | before 261010 | ✔ |
| `getStoreTurns()` | before 261010 | ✔ |
| `getStoreShuffle()` | before 261010 | ✔ |
| `getStoreMagicLevel()` | before 261010 | ✔ |
| `getObjectMakeMaxDepth()` | before 261010 | ✔ |
| `getObjectMakeGreatObj()` | before 261010 | ✔ |
| `getObjectMakeGreatEgo()` | before 261010 | ✔ |
| `getObjectMakeFuelTorch()` | before 261010 | ✔ |
| `getObjectMakeFuelLamp()` | before 261010 | ✔ |
| `getObjectMakeDefaultLamp()` | before 261010 | ✔ |
| `getPlayerMaxSight()` | before 261010 | ✔ |
| `getPlayerMaxRange()` | before 261010 | ✔ |
| `getPlayerStartGold()` | before 261010 | ✔ |
| `getPlayerFoodValue()` | before 261010 | ✔ |
| `getMeleeCriticalDebuffToh()` | before 261010 | ✔ |
| `getMeleeCriticalChanceWeightScale()` | before 261010 | ✔ |
| `getMeleeCriticalChanceTohScale()` | before 261010 | ✔ |
| `getMeleeCriticalChanceLevelScale()` | before 261010 | ✔ |
| `getMeleeCriticalChanceTohSkillScale()` | before 261010 | ✔ |
| `getMeleeCriticalChanceOffset()` | before 261010 | ✔ |
| `getMeleeCriticalChanceRange()` | before 261010 | ✔ |
| `getMeleeCriticalPowerWeightScale()` | before 261010 | ✔ |
| `getMeleeCriticalPowerRandom()` | before 261010 | ✔ |
| `getRangedCriticalDebuffToh()` | before 261010 | ✔ |
| `getRangedCriticalChanceWeightScale()` | before 261010 | ✔ |
| `getRangedCriticalChanceTohScale()` | before 261010 | ✔ |
| `getRangedCriticalChanceLevelScale()` | before 261010 | ✔ |
| `getRangedCriticalChanceLaunchedTohSkillScale()` | before 261010 | ✔ |
| `getRangedCriticalChanceThrownTohSkillScale()` | before 261010 | ✔ |
| `getRangedCriticalChanceOffset()` | before 261010 | ✔ |
| `getRangedCriticalChanceRange()` | before 261010 | ✔ |
| `getRangedCriticalPowerWeightScale()` | before 261010 | ✔ |
| `getRangedCriticalPowerRandom()` | before 261010 | ✔ |
| `getOMeleeCriticalDebuffToh()` | before 261010 | ✔ |
| `getOMeleeCriticalPowerTohScaleNumerator()` | before 261010 | ✔ |
| `getOMeleeCriticalPowerTohScaleDenominator()` | before 261010 | ✔ |
| `getOMeleeCriticalChancePowerScaleNumerator()` | before 261010 | ✔ |
| `getOMeleeCriticalChancePowerScaleDenominator()` | before 261010 | ✔ |
| `getOMeleeCriticalChanceAddDenominator()` | before 261010 | ✔ |
| `getORangedCriticalDebuffToh()` | before 261010 | ✔ |
| `getORangedCriticalPowerLaunchedTohScaleNumerator()` | before 261010 | ✔ |
| `getORangedCriticalPowerLaunchedTohScaleDenominator()` | before 261010 | ✔ |
| `getORangedCriticalPowerThrownTohScaleNumerator()` | before 261010 | ✔ |
| `getORangedCriticalPowerThrownTohScaleDenominator()` | before 261010 | ✔ |
| `getORangedCriticalChancePowerScaleNumerator()` | before 261010 | ✔ |
| `getORangedCriticalChancePowerScaleDenominator()` | before 261010 | ✔ |
| `getORangedCriticalChanceAddDenominator()` | before 261010 | ✔ |


## middle/game/globals/cached

##### `CachedPlayerData.java`

**Responsible for:** The player-level flag cache reused across a batch of UI-entry evaluations — the port of C's struct cached_player_data (ui-entry.c).

**class `CachedPlayerData`** — class Javadoc: ✔ · type stamp: before 261006

- Fields (3/3 documented): ✔ `untimed`, `timed`, `flagsSet`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `populateFlags(Player)` | before 261006 | ✔ |
| `onTimedFlag(ObjectFlag)` | before 261006 | ✔ |
| `onUntimedFlag(ObjectFlag)` | before 261006 | ✔ |
| `offTimedFlag(ObjectFlag)` | before 261006 | ✔ |
| `offUntimedFlag(ObjectFlag)` | before 261006 | ✔ |
| `hasTimedFlag(ObjectFlag)` | before 261006 | ✔ |
| `hasUntimedFlag(ObjectFlag)` | before 261006 | ✔ |
| `getUntimedFlags()` | before 261006 | ✔ |
| `getTimedFlags()` | before 261006 | ✔ |


## middle/game/globals/data

##### `CarryCapData.java`

**Responsible for:** Record for storing data to do with carrying capacity

**record `CarryCapData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `packSize`, `quiverSize`, `quiverSlotSize`, `thrownQuiverMult`, `floorSize`


##### `DunGenData.java`

**Responsible for:** Holder for data dealing with dungeon generation

**record `DunGenData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `centMax`, `doorMax`, `wallMax`, `tunnMax`, `amtRoom`, `amtItem`, `amtGold`, `pitMax`


##### `GameConstantsData.java`

**Responsible for:** Every game constant read from constants.txt, grouped by the directive that sets it.

**record `GameConstantsData`** — class Javadoc: ✔ · type stamp: before 261002

- Record components (via `@param`) (17/17 documented): ✔ `levelMax`, `monGen`, `monPlay`, `dunGen`, `world`, `carryCap`, `store`, `objMake`, `player`, `meleeCritical`, `meleeCriticalLevel`, `rangedCritical`, `rangedCriticalLevel`, `oMeleeCritical`, `oMeleeCriticalLevel`, `oRangedCritical`, `oRangedCriticalLevel`

**class `GameConstantsData.GameConstantsBuilder`** — class Javadoc: ✔ · type stamp: before 261002

- Fields (89/89 documented): ✔ `meleeCriticalLevelDataList`, `rangedCriticalLevelDataList`, `oMeleeCriticalLevelDataList`, `oRangedCriticalLevelDataList`, `levelMaxMonsters`, `monGenChance`, `monGenLevelMin`, `monGenTownDay`, `monGenTownNight`, `monGenReproMax`, `monGenOodChance`, `monGenOodAmount`, `monGenGroupMax`, `monGenGroupDist`, `monPlayBreakGlyph`, `monPlayMultRate`, `monPlayLifeDrain`, `monPlayFleeRange`, `monPlayTurnRange`, `dunGenCentMax`, `dunGenDoorMax`, `dunGenWallMax`, `dunGenTunnMax`, `dunGenAmtRoom`, `dunGenAmtItem`, `dunGenAmtGold`, `dunGenPitMax`, `worldMaxDepth`, `worldDayLength`, `worldDungeonHgt`, `worldDungeonWid`, `worldTownHgt`, `worldTownWid`, `worldFeelingTotal`, `worldFeelingNeed`, `worldStairSkip`, `worldMoveEnergy`, `carryCapPackSize`, `carryCapQuiverSize`, `carryCapQuiverSlotSize`, `carryCapThrownQuiverMult`, `carryCapFloorSize`, `storeInvenMax`, `storeTurns`, `storeShuffle`, `storeMagicLevel`, `objMakeMaxDepth`, `objMakeGreatObj`, `objMakeGreatEgo`, `objMakeFuelTorch`, `objMakeFuelLamp`, `objMakeDefaultLamp`, `playerMaxSight`, `playerMaxRange`, `playerStartGold`, `playerFoodValue`, `meleeCriticalDebuffToh`, `meleeCriticalChanceWeightScale`, `meleeCriticalChanceTohScale`, `meleeCriticalChanceLevelScale`, `meleeCriticalChanceTohSkillScale`, `meleeCriticalChanceOffset`, `meleeCriticalChanceRange`, `meleeCriticalPowerWeightScale`, `meleeCriticalPowerRandom`, `rangedCriticalDebuffToh`, `rangedCriticalChanceWeightScale`, `rangedCriticalChanceTohScale`, `rangedCriticalChanceLevelScale`, `rangedCriticalChanceLaunchedTohSkillScale`, `rangedCriticalChanceThrownTohSkillScale`, `rangedCriticalChanceOffset`, `rangedCriticalChanceRange`, `rangedCriticalPowerWeightScale`, `rangedCriticalPowerRandom`, `oMeleeCriticalDebuffToh`, `oMeleeCriticalPowerTohScaleNumerator`, `oMeleeCriticalPowerTohScaleDenominator`, `oMeleeCriticalChancePowerScaleNumerator`, `oMeleeCriticalChancePowerScaleDenominator`, `oMeleeCriticalChanceAddDenominator`, `oRangedCriticalDebuffToh`, `oRangedCriticalPowerLaunchedTohScaleNumerator`, `oRangedCriticalPowerLaunchedTohScaleDenominator`, `oRangedCriticalPowerThrownTohScaleNumerator`, `oRangedCriticalPowerThrownTohScaleDenominator`, `oRangedCriticalChancePowerScaleNumerator`, `oRangedCriticalChancePowerScaleDenominator`, `oRangedCriticalChanceAddDenominator`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addORangedCriticalLevel(ORangedCriticalLevelData)` | before 261002 | ✔ |
| `setORangedCriticalChanceAddDenominator(Integer)` | before 261002 | ✔ |
| `setORangedCriticalChancePowerScaleDenominator(Integer)` | before 261002 | ✔ |
| `setORangedCriticalChancePowerScaleNumerator(Integer)` | before 261002 | ✔ |
| `setORangedCriticalPowerThrownTohScaleDenominator(Integer)` | before 261002 | ✔ |
| `setORangedCriticalPowerThrownTohScaleNumerator(Integer)` | before 261002 | ✔ |
| `setORangedCriticalPowerLaunchedTohScaleDenominator(Integer)` | before 261002 | ✔ |
| `setORangedCriticalPowerLaunchedTohScaleNumerator(Integer)` | before 261002 | ✔ |
| `setORangedCriticalDebuffToh(Integer)` | before 261002 | ✔ |
| `addOMeleeCriticalLevelData(OMeleeCriticalLevelData)` | before 261002 | ✔ |
| `setOMeleeCriticalChanceAddDenominator(Integer)` | before 261002 | ✔ |
| `setOMeleeCriticalChancePowerScaleDenominator(Integer)` | before 261002 | ✔ |
| `setOMeleeCriticalChancePowerScaleNumerator(Integer)` | before 261002 | ✔ |
| `setOMeleeCriticalPowerTohScaleDenominator(Integer)` | before 261002 | ✔ |
| `setOMeleeCriticalPowerTohScaleNumerator(Integer)` | before 261002 | ✔ |
| `setOMeleeCriticalDebuffToh(Integer)` | before 261002 | ✔ |
| `addRangedCriticalLevelData(RangedCriticalLevelData)` | before 261002 | ✔ |
| `setRangedCriticalPowerRandom(Integer)` | before 261002 | ✔ |
| `setRangedCriticalPowerWeightScale(Integer)` | before 261002 | ✔ |
| `setRangedCriticalChanceRange(Integer)` | before 261002 | ✔ |
| `setRangedCriticalChanceOffset(Integer)` | before 261002 | ✔ |
| `setRangedCriticalChanceThrownTohSkillScale(Integer)` | before 261002 | ✔ |
| `setRangedCriticalChanceLaunchedTohSkillScale(Integer)` | before 261002 | ✔ |
| `setRangedCriticalChanceLevelScale(Integer)` | before 261002 | ✔ |
| `setRangedCriticalChanceTohScale(Integer)` | before 261002 | ✔ |
| `setRangedCriticalChanceWeightScale(Integer)` | before 261002 | ✔ |
| `setRangedCriticalDebuffToh(Integer)` | before 261002 | ✔ |
| `addMeleeCriticalLevelData(MeleeCriticalLevelData)` | before 261002 | ✔ |
| `checkCriticalLevelDataLists()` | before 261002 | ✔ |
| `setMeleeCriticalPowerRandom(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalPowerWeightScale(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalChanceRange(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalChanceOffset(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalChanceTohSkillScale(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalChanceLevelScale(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalChanceTohScale(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalChanceWeightScale(Integer)` | before 261002 | ✔ |
| `setMeleeCriticalDebuffToh(Integer)` | before 261002 | ✔ |
| `setPlayerFoodValue(Integer)` | before 261002 | ✔ |
| `setPlayerStartGold(Integer)` | before 261002 | ✔ |
| `setPlayerMaxRange(Integer)` | before 261002 | ✔ |
| `setPlayerMaxSight(Integer)` | before 261002 | ✔ |
| `setObjDefaultLamp(Integer)` | before 261002 | ✔ |
| `setObjFuelLamp(Integer)` | before 261002 | ✔ |
| `setObjFuelTorch(Integer)` | before 261002 | ✔ |
| `setObjGreatEgo(Integer)` | before 261002 | ✔ |
| `setObjGreatObj(Integer)` | before 261002 | ✔ |
| `setObjMakeMaxDepth(Integer)` | before 261002 | ✔ |
| `setStoreMagicLevel(Integer)` | before 261002 | ✔ |
| `setStoreShuffle(Integer)` | before 261002 | ✔ |
| `setStoreTurns(Integer)` | before 261002 | ✔ |
| `setStoreInvenMax(Integer)` | before 261002 | ✔ |
| `setCarryCapFloorSize(Integer)` | before 261002 | ✔ |
| `setCarryCapThrownQuiverMult(Integer)` | before 261002 | ✔ |
| `setCarryCapQuiverSlotSize(Integer)` | before 261002 | ✔ |
| `setCarryCapQuiverSize(Integer)` | before 261002 | ✔ |
| `setCarryCapPackSize(Integer)` | before 261002 | ✔ |
| `setWorldMoveEnergy(Integer)` | before 261002 | ✔ |
| `setWorldStairSkip(Integer)` | before 261002 | ✔ |
| `setWorldFeelingNeed(Integer)` | before 261002 | ✔ |
| `setWorldFeelingTotal(Integer)` | before 261002 | ✔ |
| `setWorldTownWid(Integer)` | before 261002 | ✔ |
| `setWorldTownHgt(Integer)` | before 261002 | ✔ |
| `setWorldDungeonWid(Integer)` | before 261002 | ✔ |
| `setWorldDungeonHgt(Integer)` | before 261002 | ✔ |
| `setWorldDayLength(Integer)` | before 261002 | ✔ |
| `setWorldMaxDepth(Integer)` | before 261002 | ✔ |
| `setDunGenPitMax(Integer)` | before 261002 | ✔ |
| `setDunGenAmtGold(Integer)` | before 261002 | ✔ |
| `setDunGenAmtItem(Integer)` | before 261002 | ✔ |
| `setDunGenAmtRoom(Integer)` | before 261002 | ✔ |
| `setDunGenTunnMax(Integer)` | before 261002 | ✔ |
| `setDunGenWallMax(Integer)` | before 261002 | ✔ |
| `setDunGenDoorMax(Integer)` | before 261002 | ✔ |
| `setDunGenCentMax(Integer)` | before 261002 | ✔ |
| `setMonPlayTurnRange(Integer)` | before 261002 | ✔ |
| `setMonPlayFleeRange(Integer)` | before 261002 | ✔ |
| `setMonPlayLifeDrain(Integer)` | before 261002 | ✔ |
| `setMonPlayMultRate(Integer)` | before 261002 | ✔ |
| `setMonPlayBreakGlyph(Integer)` | before 261002 | ✔ |
| `setMonGenGroupDist(Integer)` | before 261002 | ✔ |
| `setMonGenGroupMax(Integer)` | before 261002 | ✔ |
| `setMonGenOodAmount(Integer)` | before 261002 | ✔ |
| `setMonGenOodChance(Integer)` | before 261002 | ✔ |
| `setMonGenReproMax(Integer)` | before 261002 | ✔ |
| `setMonGenTownNight(Integer)` | before 261002 | ✔ |
| `setMonGenTownDay(Integer)` | before 261002 | ✔ |
| `setMonGenLevelMin(Integer)` | before 261002 | ✔ |
| `setLevelMaxMonsters(Integer)` | before 261002 | ✔ |
| `setMonGenChance(Integer)` | before 261002 | ✔ |
| `build(List<String>)` | before 261002 | ✔ |


##### `LevelMaxData.java`

**Responsible for:** Record storing the level maximums - currently only stores the maximum number of monsters on the level

**record `LevelMaxData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (1/1 documented): ✔ `monsters`


##### `MeleeCriticalData.java`

**Responsible for:** Constants for normal (non-O) melee critical calculations Generally these calculations compute the chance for a critical based on a linear combination of the to-hit bonus, the players to-hit skill, the players level and the weapon/missile's weight.

**record `MeleeCriticalData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (9/9 documented): ✔ `debuffToh`, `chanceWeightScale`, `chanceTohScale`, `chanceLevelScale`, `chanceTohSkillScale`, `chanceOffset`, `chanceRange`, `powerWeightScale`, `powerRandom`


##### `MeleeCriticalLevelData.java`

**Responsible for:** Melee critical level, used to determine the level of the critical, and extra damage it does.

**record `MeleeCriticalLevelData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `powerCutoff`, `damageMultiplier`, `damageAddition`, `messageType`


##### `MonGenData.java`

**Responsible for:** Data record for monster generation

**record `MonGenData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (9/9 documented): ✔ `chance`, `levelMin`, `townDay`, `townNight`, `reproMax`, `oodChance`, `oodAmount`, `groupMax`, `groupDist`


##### `MonPlayData.java`

**Responsible for:** Data structure for storing monster gameplay data

**record `MonPlayData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `breakGlyph`, `multRate`, `lifeDrain`, `fleeRange`, `turnRange`


##### `OMeleeCriticalData.java`

**Responsible for:** Constants for a non-normal (O) critical calculation These calculations in general compute a 'power' of the critical by scaling the chance of a hit.

**record `OMeleeCriticalData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `debuffToh`, `powerTohScaleNumerator`, `powerTohScaleDenominator`, `chancePowerScaleNumerator`, `chancePowerScaleDenominator`, `chanceAddDenominator`


##### `OMeleeCriticalLevelData.java`

**Responsible for:** Level for non-standard (O) melee criticals.

**record `OMeleeCriticalLevelData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `chance`, `dice`, `messageType`


##### `ORangedCriticalData.java`

**Responsible for:** Constants for a non-normal O critical calculation.

**record `ORangedCriticalData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `debuffToh`, `powerLaunchedTohScaleNumerator`, `powerLaunchedTohScaleDenominator`, `powerThrownTohScaleNumerator`, `powerThrownTohScaleDenominator`, `chancePowerScaleNumerator`, `chancePowerScaleDenominator`, `chanceAddDenominator`


##### `ORangedCriticalLevelData.java`

**Responsible for:** Each of the levels of a ranged critical.

**record `ORangedCriticalLevelData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `chance`, `dice`, `messageType`


##### `ObjMakeData.java`

**Responsible for:** Record to hold constants dealing with object generation

**record `ObjMakeData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (6/6 documented): ✔ `maxDepth`, `greatObj`, `greatEgo`, `fuelTorch`, `fuelLamp`, `defaultLamp`


##### `PlayerData.java`

**Responsible for:** Record to hold player constants

**record `PlayerData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `maxSight`, `maxRange`, `startGold`, `foodValue`


##### `RangedCriticalData.java`

**Responsible for:** Constants for normal (non-O) ranged critical calculations Generally these calculations compute the chance for a critical based on a linear combination of the to-hit bonus, the players to-hit skill, the players level and the weapon/missile's weight.

**record `RangedCriticalData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (10/10 documented): ✔ `debuffToh`, `chanceWeightScale`, `chanceTohScale`, `chanceLevelScale`, `chanceLaunchedTohSkillScale`, `chanceThrownTohSkillScale`, `chanceOffset`, `chanceRange`, `powerWeightScale`, `powerRandom`


##### `RangedCriticalLevelData.java`

**Responsible for:** Ranged critical level, used to determine the level of the critical, and extra damage it does.

**record `RangedCriticalLevelData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `powerCutoff`, `damageMultiplier`, `damageAddition`, `messageType`


##### `StoreData.java`

**Responsible for:** Data record for storing 'store' generation data

**record `StoreData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (4/4 documented): ✔ `invenMax`, `turns`, `shuffle`, `magicLevel`


##### `WorldData.java`

**Responsible for:** Record to hold game world constants

**record `WorldData`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (10/10 documented): ✔ `maxDepth`, `dayLength`, `dungeonHgt`, `dungeonWid`, `townHgt`, `townWid`, `feelingTotal`, `feelingNeed`, `stairSkip`, `moveEnergy`


## middle/game/globals/loaders

##### `DungeonLoader.java`

**Responsible for:** Loads the level-generation data files into their registries at start-up.

**class `DungeonLoader`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadDungeonProfiles()` | before 261010 | ✔ |
| `loadRoomTemplates()` | before 261010 | ✔ |
| `loadVaultTemplates()` | before 261010 | ✔ |


##### `MiscDataLoader.java`

**Responsible for:** Startup loader for the misc slice: parses the three otherwise-homeless gamedata files (flavor.txt, hints.txt, names.txt) and populates uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry through its setters.

**class `MiscDataLoader`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadFlavours()` | before 261009 | ✔ |
| `loadHints()` | before 261009 | ✔ |
| `loadNames()` | before 261009 | ✔ |


##### `MonsterDataLoader.java`

**Responsible for:** Startup loader for the monster slice: parses the monster-domain gamedata files (pain.txt, monster_base.txt, summon.txt, monster_spell.txt, blow_methods.txt, blow_effects.txt, visuals.txt, monster.txt, pit.txt) and populates uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry throug...

**class `MonsterDataLoader`** — class Javadoc: ✔ · type stamp: before 261011

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadMonsters()` | before 261011 | ✔ |
| `loadPitProfiles()` | before 261011 | ✔ |
| `loadVisualTables()` | before 261011 | ✔ |
| `loadMonsterSpellTypes()` | before 261011 | ✔ |
| `loadBlowEffects()` | before 261011 | ✔ |
| `loadBlowMethods()` | before 261011 | ✔ |
| `loadSummons()` | before 261011 | ✔ |
| `loadMonsterBases()` | before 261011 | ✔ |
| `loadPain()` | before 261011 | ✔ |


##### `ObjectDataLoader.java`

**Responsible for:** Startup loader for the object slice: parses the object-domain gamedata files (object_base.txt, slay.txt, brand.txt, curse.txt, object.txt, activation.txt, ego_item.txt, artifact.txt, object_property.txt, chest_trap.txt) and populates uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegist...

**class `ObjectDataLoader`** — class Javadoc: ✔ · type stamp: before 261005

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadObjectProperties()` | before 261005 | ✔ |
| `loadChestTraps()` | before 261005 | ✔ |
| `loadArtifacts()` | before 261005 | ✔ |
| `loadEgoItems()` | before 261005 | ✔ |
| `loadActivations()` | before 261005 | ✔ |
| `loadItemObjects()` | before 261005 | ✔ |
| `loadCurses()` | before 261005 | ✔ |
| `loadBrands()` | before 261005 | ✔ |
| `loadSlays()` | before 261005 | ✔ |
| `loadObjectBases()` | before 261005 | ✔ |
| `parseCurseKinds()` | on 261005 | ✔ |
| `writeCurseKinds(ObjectKind)` | on 261005 | ✔ |


##### `PlayerDataLoader.java`

**Responsible for:** Startup loader for the player slice: parses the player-domain gamedata files (player_property.txt, shape.txt, history.txt, body.txt, p_race.txt, realm.txt, class.txt, player_timed.txt) and populates uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry through its setters.

**class `PlayerDataLoader`** — class Javadoc: ✔ · type stamp: before 261011

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `initializeExpLevel()` | on 260925 | ✔ |
| `loadPlayerTimedProperties()` | before 261011 | ✔ |
| `loadPlayerClasses()` | before 261011 | ✔ |
| `loadMagicRealms()` | before 261011 | ✔ |
| `loadPlayerRaces()` | before 261011 | ✔ |
| `loadBodies()` | before 261011 | ✔ |
| `loadPlayerHistories()` | before 261011 | ✔ |
| `loadPlayerShapes()` | before 261011 | ✔ |
| `loadPlayerProperties()` | before 261011 | ✔ |


##### `TerrainDataLoader.java`

**Responsible for:** Startup loader for the terrain slice: parses the terrain-domain gamedata files (terrain.txt, trap.txt) and populates uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry through its setters.

**class `TerrainDataLoader`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadTraps()` | before 261009 | ✔ |
| `loadTerrainFeatures()` | before 261009 | ✔ |


##### `WorldDataLoader.java`

**Responsible for:** Startup loader for the world slice: parses the world/level-generation gamedata files (world.txt, projection.txt, quest.txt) and populates uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry through its setters.

**class `WorldDataLoader`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadWorld()` | before 261014 | ✔ |
| `loadProjections()` | before 261014 | ✔ |
| `loadQuests()` | before 261014 | ✔ |


## middle/game/globals/registry

##### `DungeonRegistry.java`

**Responsible for:** Holds the level-generation data loaded from lib/gamedata for the rest of the game to read — the port's home for what C keeps in the file-scope globals of generate.c.

**class `DungeonRegistry`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (4/4 documented): ✔ `logger`, `caveProfiles`, `roomTemplates`, `vaultTemplates`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setCaveProfiles(List<CaveProfile>)` | before 261014 | ✔ |
| `setRoomTemplates(List<RoomTemplate>)` | before 261014 | ✔ |
| `setVaultTemplates(List<Vault>)` | before 261014 | ✔ |


##### `MiscRegistry.java`

**Responsible for:** Runtime holder for the loose "misc" game data that does not belong to any of the larger domain slices — the loading hints, the random name lists, and the object flavours (the randomized appearance descriptions for unidentified potions, rings, and the like).

**class `MiscRegistry`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (5/5 documented): ✔ `logger`, `nameSections`, `hints`, `names`, `flavours`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getHints()` | before 261009 | ✔ |
| `getNames()` | before 261009 | ✔ |
| `getFlavours()` | before 261009 | ✔ |
| `setHints(List<Hint>)` | before 261009 | ✔ |
| `setNames(List<Name>)` | on 260831 | ✔ |
| `getNameSection(RandnameType)` | on 260831 | ✔ |
| `setFlavours(List<FlavourKind>)` | before 261009 | ✔ |


##### `MonsterRegistry.java`

**Responsible for:** Runtime holder for all monster-domain game data — races, bases, pain messages, summons, blow methods and effects, spell types, pit profiles, lore, and the visuals cycler/flicker tables — together with the derived *Max counters and the name/index lookups the running game queries against.

**class `MonsterRegistry`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (17/17 documented): ✔ `logger`, `monsterRaces`, `monsterRaceMax`, `monsterPainMsgMax`, `monsterPitTypeMax`, `monsterBlowsMax`, `monsterBlowsMethodsMax`, `monsterBlowsEffectsMax`, `monsterPains`, `monsterBases`, `summons`, `blowMethods`, `blowEffects`, `monsterSpellTypes`, `monsterPitProfiles`, `visualsCyclerTable`, `visualsFlickerTable`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setVisualsCyclerTable(VisualsCycler)` | before 261009 | ✔ |
| `setVisualsFlickerTable(FlickerTable)` | before 261009 | ✔ |
| `setMonsterPains(List<MonsterPain>)` | before 261009 | ✔ |
| `setMonsterBases(List<MonsterBase>)` | before 261009 | ✔ |
| `setSummons(List<Summon>)` | before 261009 | ✔ |
| `setBlowMethods(List<BlowMethod>)` | before 261009 | ✔ |
| `setBlowEffects(List<BlowEffect>)` | before 261009 | ✔ |
| `getMonsterRaces()` | before 261009 | ✔ |
| `setMonsterRaces(List<MonsterRace>)` | before 261009 | ✔ |
| `setMonsterPitProfiles(List<PitProfile>)` | before 261009 | ✔ |
| `setMonsterSpellTypes(List<MonsterSpellType>)` | before 261009 | ✔ |
| `lookupMonsterRace(String)` | before 261009 | ✔ |
| `lookupSummon(String)` | before 261009 | ✔ |
| `lookupMonsterBase(String)` | before 261009 | ✔ |
| `lookupMonsterPain(int)` | before 261009 | ✔ |
| `lookupBlowEffect(String)` | before 261009 | ✔ |
| `lookupBlowMethod(String)` | before 261009 | ✔ |
| `getPainFromIndex(int)` | before 261009 | ✔ |
| `getMonsterRaceMax()` | before 261009 | ✔ |
| `getMonsterPainMsgMax()` | before 261009 | ✔ |
| `getMonsterPitTypeMax()` | before 261009 | ✔ |
| `getMonsterBlowsMax()` | before 261009 | ✔ |
| `getMonsterBlowsMethodsMax()` | before 261009 | ✔ |
| `getMonsterBlowsEffectsMax()` | before 261009 | ✔ |
| `getBaseFromName(String)` | before 261009 | ✔ |
| `getVisualsCyclerTable()` | before 261009 | ✔ |
| `setMonsterBlowsMax(int)` | before 261011 | ✔ |


##### `ObjectRegistry.java`

**Responsible for:** Runtime holder for all object-domain game data — object bases, object kinds, slays, brands, curses, item objects, activations, ego items, artifacts, chest traps, and object properties — together with the derived *Max counters and the lookups the running game queries.

**class `ObjectRegistry`** — class Javadoc: ✔ · type stamp: before 261008

- Fields (54/54 documented): ✔ `NONWEAP_DAMAGE`, `WEAP_DAMAGE`, `BASE_JEWELERY_POWER`, `BASE_ARMOUR_POWER`, `DAMAGE_POWER`, `TO_HIT_POWER`, `BASE_AC_POWER`, `TO_AC_POWER`, `MAX_BLOWS`, `WGT_POWER_NUM_NOBASEAC`, `WGT_POWER_DEN_NOBASEAC`, `WGT_POWER_NUM_THROW`, `WGT_POWER_DEN_THROW`, `INHIBIT_POWER`, `INHIBIT_BLOWS`, `INHIBIT_MIGHT`, `INHIBIT_SHOTS`, `HIGH_TO_AC`, `VERYHIGH_TO_AC`, `INHIBIT_AC`, `HIGH_TO_HIT`, `VERYHIGH_TO_HIT`, `HIGH_TO_DAM`, `VERYHIGH_TO_DAM`, `AMMO_RESCALER`, `logger`, `chestTraps`, `archery`, `flagSets`, `elementSets`, `elementPowers`, `unknownGoldKind`, `unknownItemKind`, `abilityPower`, `objectBaseKindMax`, `artifactKindMax`, `egoItemKindMax`, `randartActivationsMax`, `curseMax`, `slayMax`, `brandMax`, `objectPropertyMax`, `objectBases`, `slays`, `brands`, `curses`, `itemObjects`, `activations`, `egoItems`, `artifacts`, `objectProperties`, `kindsByTvalSval`, `objectKinds`, `allRunes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setChestTraps(List<ChestTrap>)` | before 261008 | ✔ |
| `getRunes()` | before 261008 | ✔ |
| `setRunes(List<Rune>)` | before 261008 | ✔ |
| `getMaxRunes()` | before 261008 | ✔ |
| `updateObjectBaseKindMax()` | before 261008 | ✔ |
| `getArtifactKindMax()` | before 261008 | ✔ |
| `getEgoItemKindMax()` | before 261008 | ✔ |
| `getRandartActivationsMax()` | before 261008 | ✔ |
| `getCurseMax()` | before 261008 | ✔ |
| `getSlayMax()` | before 261008 | ✔ |
| `getBrandMax()` | before 261008 | ✔ |
| `getObjectsPropertyMax()` | before 261008 | ✔ |
| `getObjectKindCount()` | before 261008 | ✔ |
| `getObjectKinds()` | before 261008 | ✔ |
| `setObjectKinds(List<ObjectKind>)` | before 261008 | ✔ |
| `getObjectBaseKindMax()` | before 261008 | ✔ |
| `getObjectBases()` | before 261008 | ✔ |
| `setObjectBases(List<ObjectBase>)` | before 261008 | ✔ |
| `getSlays()` | before 261008 | ✔ |
| `setSlays(List<Slay>)` | before 261008 | ✔ |
| `getBrands()` | before 261008 | ✔ |
| `setBrands(List<Brand>)` | before 261008 | ✔ |
| `getCurses()` | before 261008 | ✔ |
| `setCurses(List<Curse>)` | before 261008 | ✔ |
| `getItemObjects()` | before 261008 | ✔ |
| `setItemObjects(List<ItemObject>)` | before 261008 | ✔ |
| `getActivations()` | before 261008 | ✔ |
| `setActivations(List<Activation>)` | before 261008 | ✔ |
| `getEgoItems()` | before 261008 | ✔ |
| `setEgoItems(List<EgoItem>)` | before 261008 | ✔ |
| `getArtifacts()` | before 261008 | ✔ |
| `setArtifacts(List<Artifact>)` | before 261008 | ✔ |
| `getObjectProperties()` | before 261008 | ✔ |
| `setObjectProperties(List<ObjectProperty>)` | before 261008 | ✔ |
| `getKindsByTvalSval()` | before 261008 | ✔ |
| `loadUnknownKinds()` | before 261008 | ✔ |
| `reset()` | before 261008 | ✔ |
| `lookupObjectKind(TValue, int)` | before 261008 | ✔ |
| `lookupObjectKind(String)` | before 261008 | ✔ |
| `lookupObjectKind(TValue, String)` | before 261008 | ✔ |
| `lookupObjectKind(TValue)` | before 261008 | ✔ |
| `lookupSlay(String)` | before 261008 | ✔ |
| `lookupCurse(String)` | before 261008 | ✔ |
| `addObjectKind(ObjectKind)` | before 261008 | ✔ |
| `lookupObjectBase(String)` | before 261008 | ✔ |
| `lookupObjectBase(String, TValue)` | before 261008 | ✔ |
| `getBaseFromTVal(TValue)` | before 261008 | ✔ |
| `lookupBrandCode(String)` | before 261008 | ✔ |
| `lookupActivation(String)` | before 261008 | ✔ |
| `lookupObjectProperty(ObjPropertyType, ObjectPropertyTypeWrapper)` | before 261008 | ✔ |


##### `ObjectValueCache.java`

**Responsible for:** The object-level flag cache reused across a batch of UI-entry evaluations for one item — the port of C's struct cached_object_data (ui-entry.c:46-48).

**class `ObjectValueCache`** — class Javadoc: ✔ · type stamp: before 260924

- Fields (1/1 documented): ✔ `resolvedFlags`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `populateFlags(ItemObject, Player)` | before 260924 | ✔ |
| `getResolvedFlags()` | before 260924 | ✔ |
| `setResolvedFlags(Flag<ObjectFlag>)` | before 260924 | ✔ |


##### `PlayerRegistry.java`

**Responsible for:** Runtime holder for all player-domain game data — properties, shapes, history charts, bodies, races, magic realms, classes, and timed effects — together with the derived *Max counters and the name/index lookups the running game queries.

**class `PlayerRegistry`** — class Javadoc: ✔ · type stamp: —

- Fields (21/21 documented): ✔ `PY_MAX_EXP`, `PY_MAX_LEVEL`, `PY_KNOW_LEVEL`, `logger`, `PY_FOOD_STARVE`, `PY_FOOD_FAINT`, `PY_FOOD_WEAK`, `PY_FOOD_HUNGRY`, `PY_FOOD_FULL`, `PY_FOOD_MAX`, `playerExperience`, `playerEquipmentSlotsMax`, `playerShapeMax`, `playerShapes`, `playerHistoryCharts`, `playerBodies`, `playerRaces`, `realms`, `playerClasses`, `playerTimedEffects`, `playerProperties`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getPyFoodStarve()` | before 260902 | ✔ |
| `getPyFoodFaint()` | before 260902 | ✔ |
| `getPyFoodWeak()` | before 260902 | ✔ |
| `getPyFoodHungry()` | — | ✔ |
| `getPyFoodMax()` | — | ✔ |
| `getPyFoodFull()` | — | ✔ |
| `getPlayerProperties()` | — | ✔ |
| `setPlayerProperties(List<PlayerProperty>)` | — | ✔ |
| `setPlayerShape(List<PlayerShape>)` | — | ✔ |
| `getPlayerShapes()` | — | ✔ |
| `getPlayerHistoryCharts()` | — | ✔ |
| `setPlayerHistoryCharts(List<PlayerHistoryChart>)` | — | ✔ |
| `getPlayerBodies()` | — | ✔ |
| `setPlayerBodies(List<PlayerBody>)` | — | ✔ |
| `getPlayerRaces()` | — | ✔ |
| `setPlayerRaces(List<PlayerRace>)` | — | ✔ |
| `setMagicRealm(List<MagicRealm>)` | — | ✔ |
| `getMagicRealms()` | — | ✔ |
| `getPlayerClasses()` | — | ✔ |
| `setPlayerClasses(List<PlayerClass>)` | — | ✔ |
| `getPlayerTimedEffects()` | — | ✔ |
| `setPlayerTimedEffects(List<PlayerTimedEffect>)` | before 261008 | ✔ |
| `lookupRealm(String)` | on 260831 | ✔ |
| `lookupPlayerHistoryChart(int)` | — | ✔ |
| `lookupPlayerRace(String)` | — | ✔ |
| `lookupPlayerBody(int)` | — | ✔ |
| `lookupPlayerShape(String)` | — | ✔ |
| `getPlayerEquipmentSlotsMax()` | — | ✔ |
| `getPlayerShapeMax()` | — | ✔ |
| `lookupPlayerTimedEffect(TimedEffect)` | on 260818 | ✔ |


##### `StatTables.java`

**Responsible for:** Hard-coded stat-adjustment lookup tables — the Java port of the adj_* arrays in the C original's src/player-calcs.c.

**class `StatTables`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (20/20 documented): ✔ `STAT_RANGE`, `adjConFix`, `adjIntDev`, `adjWisSav`, `adjDexDis`, `adjIntDis`, `adjDexTa`, `adjStrTd`, `adjDexTh`, `adjStrTh`, `adjStrWgt`, `adjStrHold`, `adjStrDig`, `adjStrBlow`, `adjDexBlow`, `adjDexSafe`, `adjConMhp`, `adjMagStudy`, `adjMagMana`, `blowsTable`


##### `TerrainRegistry.java`

**Responsible for:** The read side of the terrain slice: the loaded terrain features and trap kinds, plus the trap count, held as static state.

**class `TerrainRegistry`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (4/4 documented): ✔ `logger`, `trapMax`, `features`, `trapInfo`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getFeatures()` | before 261009 | ✔ |
| `setFeatures(List<Feature>)` | before 261009 | ✔ |
| `getTrapInfo()` | before 261009 | ✔ |
| `setTrapInfo(List<TrapKind>)` | before 261009 | ✔ |
| `lookupFeature(TerrainFlags)` | before 261009 | ✔ |
| `lookupTrap(String)` | before 261009 | ✔ |
| `getTrapMax()` | before 261009 | ✔ |
| `getTrapKinds()` | before 261009 | ✔ |


##### `UIEntryValueRegistry.java`

**Responsible for:** The runtime lookup table binding ObjectProperty/PlayerProperty pairs to named UI entries and computing each entry's displayed value for an object or a player — the Java port of the entry-bookkeeping half of C's ui-entry.c: bind_object_property_to_ui_entry_by_name, bind_player_ability_to_ui_entry_...

**class `UIEntryValueRegistry`** — class Javadoc: ✔ · type stamp: before 260924

- Fields (2/2 documented): ✔ `logger`, `bindingByEntry`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearEntryBindings()` | before 260924 | ✔ |
| `addEntryBinding(String, List<ObjectProperty>, List<PlayerProperty>, CombinerName, FlagView<ChannelEntryFlag>)` | before 260924 | ✔ |
| `computeForObject(String, ItemObject, Player, ObjectValueCache)` | before 260924 | ✔ |
| `computeForPlayer(String, Player, CachedPlayerData)` | before 260924 | ✔ |
| `modifierToSkill(ObjectProperty)` | before 260924 | ✔ |
| `getTimedModifierEffect(Player, ObjectModifier)` | before 260924 | ✔ |
| `getTimedElementEffect(Player, ElementEnum)` | before 260924 | ✔ |
| `isKnownRune(String, Player)` | before 260924 | ✔ |

**record `UIEntryValueRegistry.PlayerSkillAndTwoInts`** — class Javadoc: ✔ · type stamp: before 260924

- Record components (via `@param`) (3/3 documented): ✔ `skill`, `skillToModNum`, `skillToModDen`

**record `UIEntryValueRegistry.EntryBinding`** — class Javadoc: ✔ · type stamp: before 260924

- Record components (via `@param`) (4/4 documented): ✔ `objectProperties`, `playerProperties`, `combinerName`, `entryFlags`


##### `WorldRegistry.java`

**Responsible for:** Runtime holder for the world/level-generation game data — the World levels (the tower of dungeon depths), the Projection types (how damage and effects travel), and the Quest definitions — plus the derived size accessors and the projection lookups the running game queries.

**class `WorldRegistry`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (4/4 documented): ✔ `logger`, `worlds`, `projections`, `quests`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getWorlds()` | before 261009 | ✔ |
| `setWorlds(List<World>)` | before 261009 | ✔ |
| `getProjections()` | before 261009 | ✔ |
| `setProjections(List<Projection>)` | before 261009 | ✔ |
| `getQuests()` | before 261009 | ✔ |
| `setQuests(List<Quest>)` | before 261009 | ✔ |
| `lookupProjectionByLash(ProjectionEnum)` | before 261009 | ✔ |
| `lookupProjectionByName(String)` | before 261009 | ✔ |
| `lookupProjectionByType(ProjectionType)` | before 261009 | ✔ |
| `lookupProjectionByCode(ProjectionEnum)` | before 261009 | ✔ |
| `getQuestMax()` | before 261009 | ✔ |
| `getProjMax()` | before 261009 | ✔ |
| `getMaxRandDepth()` | before 261009 | ✔ |
| `getLevelByName(String)` | on 260830 | ✔ |
| `getLevelByDepth(int)` | on 260830 | ✔ |


## middle/gameinput

##### `DefaultGameInput.java`

**Responsible for:** The always-installed default GameInput: every hook aborts (returns Optional#empty()).

**class `DefaultGameInput`** — class Javadoc: ✔ · type stamp: before 261012

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getRepDir(boolean)` | before 261012 | ✔ |
| `getAimDir()` | before 261012 | ✔ |
| `getItem(String, String, CommandCode, Predicate<ItemObject>, Flag<GetItemFlags>)` | before 261012 | ✔ |
| `getQuantity(String, int)` | before 261012 | ✔ |
| `getString(String, String)` | before 261012 | ✔ |
| `getSpellFromBook(Player, String, ItemObject, String, BiPredicate<Player,MagicSpell>)` | before 261012 | ✔ |
| `getSpell(Player, String, Predicate<ItemObject>, CommandCode, String, BiPredicate<Player,MagicSpell>, String, ItemObject)` | before 261012 | ✔ |
| `getEffectFromList(String, List<Effect>, int, boolean)` | before 261009 | ✔ |
| `getCheck(String)` | before 261012 | ✔ |
| `getCom(String)` | before 261012 | ✔ |
| `getCurse(ItemObject, String)` | before 261012 | ✔ |
| `getPanel()` | before 261012 | ✔ |
| `panelContains(Loc)` | before 261012 | ✔ |
| `mapIsVisible()` | before 261012 | ✔ |
| `viewAbilityMenu(List<PlayerProperty>)` | before 261012 | ✔ |


##### `EffectChoice.java`

**Responsible for:** The outcome of asking the player to pick one effect from a list.

**interface `EffectChoice`** — class Javadoc: ✔ · type stamp: before 261009

**record `EffectChoice.Index`** — class Javadoc: ✔ · type stamp: before 261009

- Record components (via `@param`) (1/1 documented): ✔ `value`

**record `EffectChoice.Random`** — class Javadoc: ✔ · type stamp: before 261009

**record `EffectChoice.Aborted`** — class Javadoc: ✔ · type stamp: before 261009


##### `GameInput.java`

**Responsible for:** The boundary through which the game engine asks the user interface for an interactive value - the port of C's get_*_hook indirection in game-input.c, gathered into a single interface.

**interface `GameInput`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `viewAbilityMenu(List<PlayerProperty>)` | before 261012 | ✔ |

**record `GameInput.SpellSelection`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `book`, `spell`

**record `GameInput.Rectangle`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `topLeft`, `bottomRight`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getCheck(String)` | — | ✔ |
| `getCom(String)` | — | ✔ |
| `getCurse(ItemObject, String)` | — | ✔ |
| `getPanel()` | — | ✔ |
| `panelContains(Loc)` | — | ✔ |
| `mapIsVisible()` | — | ✔ |
| `confirmDebug()` | — | ✔ |
| `getRepDir(boolean)` | — | ✔ |
| `getAimDir()` | — | ✔ |
| `getItem(String, String, CommandCode, Predicate<ItemObject>, Flag<GetItemFlags>)` | — | ✔ |
| `getQuantity(String, int)` | — | ✔ |
| `getString(String, String)` | — | ✔ |
| `getSpellFromBook(Player, String, ItemObject, String, BiPredicate<Player,MagicSpell>)` | — | ✔ |
| `getSpell(Player, String, Predicate<ItemObject>, CommandCode, String, BiPredicate<Player,MagicSpell>, String, ItemObject)` | — | ✔ |
| `getEffectFromList(String, List<Effect>, int, boolean)` | — | ✔ |


##### `GameInputHolder.java`

**Responsible for:** The static switchboard for the GameInput boundary: it holds the one implementation currently installed and hands it to callers.

**class `GameInputHolder`** — class Javadoc: ✔ · type stamp: before 261012

- Fields (2/2 documented): ✔ `logger`, `instance`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `GameInputHolder()` | before 261012 | ✔ |
| `setInstance(GameInput)` | before 261012 | ✔ |
| `resetInstance()` | before 261012 | ✔ |
| `getInstance()` | before 261012 | ✔ |


## middle/magic

##### `ClassMagic.java`

**Responsible for:** A player class's spellcasting profile: the level at which it can first cast, the armour weight it may wear before its mana suffers, and the MagicBooks it casts from.

**class `ClassMagic`** — class Javadoc: ✔ · type stamp: before 261012

- Fields (6/6 documented): ✔ `firstSpellLevel`, `spellWeight`, `numBooks`, `magicBooks`, `totalSpells`, `NONE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `isCaster()` | before 261012 | ✔ |
| ctor `ClassMagic(int, int, List<MagicBook>)` | before 261012 | ✔ |
| `getMagicBooks()` | before 261012 | ✔ |
| `indexOfSpell(MagicSpell)` | before 261012 | ✔ |
| `spellByIndex(int)` | on 260830 | ✔ |
| `getTotalSpells()` | before 261012 | ✔ |
| `getSpellFirst()` | before 261012 | ✔ |
| `getNumBooks()` | before 261012 | ✔ |
| `getSpellWeight()` | before 261012 | ✔ |


##### `MagicBook.java`

**Responsible for:** One spellbook of a class: the Java copy of C's struct class_book (player.h), which a class lists under magic.books.

**class `MagicBook`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (12/12 documented): ✔ `bookType`, `sVal`, `bookName`, `dungeon`, `numOfSpells`, `realm`, `character`, `cost`, `commonness`, `min`, `max`, `spells`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MagicBook(TValue, String, boolean, MagicRealm, AngbandDisplayCharacter, int, int, int, int, List<MagicSpell>)` | before 261010 | ✔ |
| `setSVal()` | before 261010 | ✔ |
| `getNumOfSpells()` | before 261010 | ✔ |
| `getBookName()` | before 261010 | ✔ |
| `getBookTValue()` | before 261010 | ✔ |
| `isDungeon()` | before 261010 | ✔ |
| `getSpells()` | before 261010 | ✔ |
| `getSval()` | before 261010 | ✔ |
| `getRealm()` | before 261010 | ✔ |


##### `MagicRealm.java`

**Responsible for:** A school of magic — one of the four spellcasting "realms" defined by realm.txt (arcane, divine, nature, shadow) — bundling the flavour that varies between them: the Stats stat that governs casting, the verb used ("cast", "recite", …), the noun for a casting ("spell", "prayer", …) and the noun for...

**class `MagicRealm`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (5/5 documented): ✔ `name`, `stat`, `verb`, `spellNoun`, `bookNoun`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MagicRealm(String, Stats, String, String, String)` | before 261010 | ✔ |
| `getName()` | before 261010 | ✔ |
| `getStat()` | before 261010 | ✔ |


##### `MagicSpell.java`

**Responsible for:** A single spell: its identity, the MagicRealm it belongs to, the Effects it produces when cast, and its casting parameters (required level, mana cost, base failure rate and the experience awarded for first casting it).

**class `MagicSpell`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (8/8 documented): ✔ `spellName`, `spellDescription`, `effects`, `level`, `mana`, `fail`, `exp`, `realm`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MagicSpell(String, int, int, int, int, List<Effect>, String)` | before 261014 | ✔ |
| `setRealm(MagicRealm)` | on 261014 | ✔ |
| `getSpellName()` | before 261014 | ✔ |


## middle/monsters

##### `BlowEffect.java`

**Responsible for:** The definition of a monster blow effect (as loaded from blow_effects.txt) — what a successful melee blow does, its power and evaluation weight, the lore description and colours, the effect/resist types and the projection used for a lash form.

**class `BlowEffect`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (11/11 documented): ✔ `name`, `power`, `eval`, `desc`, `loreAttr`, `loreAttrResist`, `loreAttrImmune`, `effectType`, `objectFlagResist`, `elementEnumResist`, `lashType`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `BlowEffect(String, int, int, String, ColourEnum, ColourEnum, ColourEnum, BlowEffectType, ObjectFlag, ElementEnum, Projection)` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |
| `getPower()` | before 261009 | ✔ |
| `getEval()` | before 261009 | ✔ |
| `getDesc()` | before 261009 | ✔ |
| `getLoreAttr()` | before 261009 | ✔ |
| `getLoreAttrResist()` | before 261009 | ✔ |
| `getLoreAttrImmune()` | before 261009 | ✔ |
| `getEffectType()` | before 261009 | ✔ |
| `getObjectFlagResist()` | before 261009 | ✔ |
| `getElementEnumResist()` | before 261009 | ✔ |
| `getLashType()` | before 261009 | ✔ |


##### `MonGroupListEntry.java`

**Responsible for:** One node in a monster group's member list, holding the monster index of a group member.

**class `MonGroupListEntry`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `mIdx`


##### `Monster.java`

**Responsible for:** A live monster on the current level — an instance of a MonsterRace with its own position, hit points, timed effects, speed/energy, status flags, held and mimicked objects, target, group membership and flow heatmap.

**class `Monster`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (20/20 documented): ✔ `monIndex`, `monsterRace`, `originalRace`, `grid`, `hp`, `maxHp`, `mTimed`, `mSpeed`, `energy`, `cDistance`, `monsterFlag`, `mimickedObject`, `heldObject`, `colourAttr`, `knownPState`, `target`, `groupInfo`, `heatmap`, `minRange`, `bestRange`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Monster(MonsterRace, MonsterRace, Loc, int, int, Map<MonTimed,Integer>, int, int, int, Flag<MonsterFlag>, ItemObject, List<ItemObject>, ColourEnum, PlayerState, Target, List<MonsterGroupInfo>, Heatmap, int, int)` | before 261009 | ✔ |
| `getMonsterRace()` | before 261009 | ✔ |
| `hasMonsterFlag(MonsterFlag)` | before 261009 | ✔ |
| `monsterFlagOff(MonsterFlag)` | before 261009 | ✔ |
| `getGrid()` | before 261009 | ✔ |
| `getcDistance()` | before 261009 | ✔ |
| `isUnique()` | before 261009 | ✔ |
| `getMonTimed(MonTimed)` | before 261009 | ✔ |
| `clearTimed(MonTimed, Flag<MonTimedFlags>)` | on 260830 | ✔ |
| `setTimed(MonTimed, int, Flag<MonTimedFlags>)` | before 261009 | ✔ |
| `decrementTimed(MonTimed, int, Flag<MonTimedFlags>)` | on 260830 | ✔ |
| `monsterIsCamouflaged()` | on 260828 | ✔ |
| `updateSmartLearn(Player, ObjectFlag, PlayerFlag, ElementEnum)` | on 260831 | ✔ |
| `monsterIsSmart()` | on 260831 | ✔ |
| `monsterIsStupid()` | on 260831 | ✔ |
| `getHp()` | before 260929 | ✔ |
| `setHp(int)` | before 260929 | ✔ |
| `getMaxHp()` | before 260929 | ✔ |
| `setMaxHp(int)` | before 260929 | ✔ |
| `monsterFlagOn(MonsterFlag)` | before 260929 | ✔ |
| `setMonsterTracked(boolean)` | before 260929 | ✔ |
| `updateCached(Boolean)` | before 260929 | ✔ |
| `getHeldObjects()` | before 261009 | ✔ |
| `getMimickedObject()` | before 261009 | ✔ |
| `isObvious()` | before 261009 | ✔ |
| `monsterIsVisible()` | before 261009 | ✔ |
| `getMonIndex()` | before 261009 | ✔ |
| `setMonIndex(int)` | before 261009 | ✔ |


##### `MonsterAltmsg.java`

**Responsible for:** An alternate spell-cast message for a monster: the message text, which situation it applies to (MonsterAltmsgType — seen/unseen/miss) and the MonsterSpell it overrides.

**class `MonsterAltmsg`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `message`, `msgType`, `index`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterAltmsg(String, MonsterAltmsgType, MonsterSpell)` | — | ✔ |


##### `MonsterBase.java`

**Responsible for:** A monster "base" template (as loaded from monster_base.txt) — a family of monsters (e.g.

**class `MonsterBase`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (6/6 documented): ✔ `codeName`, `inGameName`, `flags`, `defaultMonsterChar`, `pain`, `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterBase(String)` | before 261010 | ✔ |
| ctor `MonsterBase(String, String, Flag<MonsterRaceFlag>, char, MonsterPain, String)` | before 261010 | ✔ |
| `toString()` | before 261010 | ✔ |
| `getCodeName()` | before 261010 | ✔ |
| `getFlags()` | before 261010 | ✔ |
| `getDefaultMonsterChar()` | before 261010 | ✔ |


##### `MonsterBlow.java`

**Responsible for:** One melee attack ("blow") of a monster: how it is delivered (BlowMethod), what it does (BlowEffect), its damage dice, and how many times the player has observed it (for lore).

**class `MonsterBlow`** — class Javadoc: ✔ · type stamp: before 261011

- Fields (6/6 documented): ✔ `MAX_TIMES_SEEN`, `method`, `effect`, `dice`, `timesSeen`, `known`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterBlow(BlowMethod, BlowEffect, Random)` | before 261011 | ✔ |
| ctor `MonsterBlow()` | before 261011 | ✔ |
| `getDice()` | before 261011 | ✔ |
| `getEffect()` | before 261011 | ✔ |
| `getMethod()` | before 261011 | ✔ |
| `getTimesSeen()` | before 261011 | ✔ |
| `incrementBlowTimesSeen()` | before 261011 | ✔ |
| `isKnown()` | before 261011 | ✔ |
| `setKnown(boolean)` | before 261011 | ✔ |


##### `MonsterDrop.java`

**Responsible for:** One possible death drop for a monster race: an item type (and optionally a specific ObjectKind), the percentage chance it drops, and the quantity range.

**class `MonsterDrop`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (5/5 documented): ✔ `tVal`, `kind`, `percentChance`, `min`, `max`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterDrop(TValue, ObjectKind, int, int, int)` | before 261014 | ✔ |
| ctor `MonsterDrop(TValue, int, int, int)` | before 261014 | ✔ |
| `isBase()` | before 261014 | ✔ |


##### `MonsterFriends.java`

**Responsible for:** A "friends" entry for a monster race: another specific MonsterRace that may be generated alongside it, with the group role they take, the chance they appear, and a dice expression for how many.

**class `MonsterFriends`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (7/7 documented): ✔ `logger`, `name`, `race`, `role`, `percentChance`, `numberDice`, `numberSides`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterFriends(String, MonsterGroupRole, int, int, int)` | before 261014 | ✔ |
| `setRace(String)` | before 261014 | ✔ |
| `getName()` | before 261014 | ✔ |


##### `MonsterFriendsBase.java`

**Responsible for:** Like MonsterFriends, but the companions are chosen by monster base type rather than a specific race — a MonsterBase that may be generated alongside the monster, with role, chance and a dice count.

**class `MonsterFriendsBase`** — class Javadoc: ✔ · type stamp: —

- Fields (5/5 documented): ✔ `base`, `role`, `percentChance`, `numberDice`, `numberSides`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterFriendsBase(MonsterBase, MonsterGroupRole, int, int, int)` | — | ✔ |


##### `MonsterGroup.java`

**Responsible for:** A live group/pack of monsters on the level — its identifier, the monster index of its leader, and the list of members.

**class `MonsterGroup`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `index`, `leader`, `memberList`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `monsterGroupChangeIndex(Chunk, int, int)` | — | ✔ |


##### `MonsterGroupInfo.java`

**Responsible for:** A monster's membership info for its group: the group index it belongs to and the MonsterGroupRole it plays.

**class `MonsterGroupInfo`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `index`, `role`


##### `MonsterLore.java`

**Responsible for:** The player's accumulated knowledge ("lore") about a monster race — what has been observed through play and is therefore shown in the monster recall: kill and sighting counts, observed drops/spells/blows, and the subset of flags the player has learned.

**class `MonsterLore`** — class Javadoc: ✔ · type stamp: before 261011

- Fields (26/26 documented): ✔ `logger`, `sightings`, `deaths`, `pkills`, `thefts`, `tkills`, `wake`, `ignore`, `dropGold`, `dropItem`, `caseInnate`, `castSpell`, `monsterBase`, `blows`, `flags`, `spellFlags`, `drops`, `friends`, `friendsBase`, `mimicKinds`, `allKnown`, `armourKnown`, `dropKnown`, `sleepKnown`, `spellFreqKnown`, `innateFreqKnown`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterLore()` | before 261011 | ✔ |
| ctor `MonsterLore(boolean, boolean, List<MonsterBlow>, int, int, int, int, int, boolean, List<MonsterDrop>, Flag<MonsterRaceFlag>, List<MonsterFriends>, List<MonsterFriendsBase>, int, boolean, List<MonsterMimic>, MonsterBase, int, int, boolean, Flag<MonsterSpell>, boolean, int, int, int)` | before 261011 | ✔ |
| `initblows(int)` | before 261011 | ✔ |
| `setPSkills(int)` | on 260903 | ✔ |
| `setThefts(int)` | on 260903 | ✔ |
| `isAllKnown()` | before 261011 | ✔ |
| `setAllKnown(boolean)` | before 261011 | ✔ |
| `isArmourKnown()` | before 261011 | ✔ |
| `getBlows()` | before 261011 | ✔ |
| `getCaseInnate()` | before 261011 | ✔ |
| `getCastSpell()` | before 261011 | ✔ |
| `getDeaths()` | before 261011 | ✔ |
| `getDropGold()` | before 261011 | ✔ |
| `getDropItem()` | before 261011 | ✔ |
| `isDropKnown()` | before 261011 | ✔ |
| `getDrops()` | before 261011 | ✔ |
| `getFlags()` | before 261011 | ✔ |
| `getFriends()` | before 261011 | ✔ |
| `getFriendsBase()` | before 261011 | ✔ |
| `getIgnore()` | before 261011 | ✔ |
| `isInnateFreqKnown()` | before 261011 | ✔ |
| `getMimicKinds()` | before 261011 | ✔ |
| `getMonsterBase()` | before 261011 | ✔ |
| `getPkills()` | before 261011 | ✔ |
| `getSightings()` | before 261011 | ✔ |
| `isSleepKnown()` | before 261011 | ✔ |
| `getSpellFlags()` | before 261011 | ✔ |
| `isSpellFreqKnown()` | before 261011 | ✔ |
| `getThefts()` | before 261011 | ✔ |
| `getTkills()` | before 261011 | ✔ |
| `getWake()` | before 261011 | ✔ |


##### `MonsterMimic.java`

**Responsible for:** The set of object kinds a "mimic" monster can disguise itself as on the floor (so it looks like an item until disturbed).

**class `MonsterMimic`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `kinds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterMimic(List<ObjectKind>)` | — | ✔ |


##### `MonsterPain.java`

**Responsible for:** One set of "pain" messages, the Java copy of one type: block of pain.txt and of struct monster_pain in monster.h.

**class `MonsterPain`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (3/3 documented): ✔ `logger`, `painIndex`, `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterPain(int, List<String>)` | before 261010 | ✔ |
| `getMessage(int)` | before 261010 | ✔ |
| `toString()` | before 261010 | ✔ |
| `getPainIndex()` | before 261010 | ✔ |


##### `MonsterRace.java`

**Responsible for:** The full definition of a kind of monster (as loaded from monster.txt) — name and flavour text, its MonsterBase, combat/sense stats, spell and blow lists, generation level/rarity, display glyph, drops, companions, mimic kinds, shapes and the accumulated MonsterLore.

**class `MonsterRace`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copy()` | — | ✔ |
| `setCurNum(int)` | on 260903 | ✔ |
| `setMaxNum(int)` | on 260903 | ✔ |
| `getBlows()` | before 261010 | ✔ |

**record `MonsterRace.MonsterSpellMessages`** — class Javadoc: ✔ · type stamp: before 261010

- Record components (via `@param`) (3/3 documented): ✔ `visible`, `invisible`, `miss`
- Fields (33/33 documented): ✔ `name`, `text`, `plural`, `base`, `averageHP`, `ac`, `sleep`, `hearing`, `smell`, `speed`, `light`, `mexp`, `freqInnate`, `freqSpell`, `spellPower`, `flags`, `flagsOff`, `spells`, `blow`, `level`, `rarity`, `adc`, `maxNum`, `curNum`, `spellMessages`, `drops`, `friends`, `friendsBase`, `mimicKinds`, `shapes`, `numShapes`, `lore`, `cycler`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterRace(String, String, String, MonsterBase, int, int, int, int, int, int, int, int, int, int, int, Flag<MonsterRaceFlag>, Flag<MonsterSpell>, List<MonsterBlow>, int, int, AngbandDisplayCharacter, Map<String,MonsterSpellMessage>, List<MonsterDrop>, List<MonsterFriends>, List<MonsterFriendsBase>, List<ObjectKind>, List<MonsterShape>, int, ColourCycle)` | before 261010 | ✔ |
| `setLore(MonsterLore)` | before 261010 | ✔ |
| `getLore()` | on 260903 | ✔ |
| ctor `MonsterRace()` | before 261010 | ✔ |
| `getFlags()` | before 261010 | ✔ |
| `hasMonsterRaceFlag(MonsterRaceFlag)` | before 261010 | ✔ |
| `getName()` | before 261010 | ✔ |
| `resolveFriends()` | before 261010 | ✔ |
| `resolveShapes()` | before 261010 | ✔ |
| `getLevel()` | before 261010 | ✔ |
| `getLight()` | on 260828 | ✔ |
| `hasLore()` | before 261011 | ✔ |


##### `MonsterShape.java`

**Responsible for:** One shape a monster can change into, the Java form of C's struct monster_shape (monster.h).

**class `MonsterShape`** — class Javadoc: ✔ · type stamp: before 261011

- Fields (3/3 documented): ✔ `name`, `race`, `base`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterShape(String)` | before 261011 | ✔ |
| `getName()` | before 261011 | ✔ |
| `getRace()` | before 261011 | ✔ |
| `getBase()` | before 261011 | ✔ |
| `setRace(MonsterRace)` | before 261011 | ✔ |
| `setBase(MonsterBase)` | before 261011 | ✔ |


##### `MonsterSpellLevel.java`

**Responsible for:** One power tier of a monster spell: the minimum power at which it applies, the lore description and its colours (normal / when resisted / when immune), and the messages shown when it is cast (seen, blind, miss, save).

**class `MonsterSpellLevel`** — class Javadoc: ✔ · type stamp: —

- Fields (9/9 documented): ✔ `power`, `loreDesc`, `loreAttr`, `loreAttrResist`, `loreAttrImmune`, `message`, `blindMessage`, `missMessage`, `saveMessage`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterSpellLevel(int, String, ColourEnum, ColourEnum, ColourEnum, String, String, String, String)` | — | ✔ |


##### `MonsterSpellMessage.java`

**Responsible for:** The alternate messages a monster shows when it casts a particular spell, overriding the spell's default text: what the player sees when the caster is visible (vis), invisible (invis), or when the spell misses/fails (miss).

**record `MonsterSpellMessage`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `vis`, `invis`, `miss`


##### `MonsterSpellType.java`

**Responsible for:** A monster spell definition (as loaded from monster_spell.txt): which MonsterSpell it is, its cast message type, its hit chance, the Effects it produces, and its per-power MonsterSpellLevels.

**class `MonsterSpellType`** — class Javadoc: ✔ · type stamp: —

- Fields (5/5 documented): ✔ `index`, `msgT`, `hit`, `effects`, `levels`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterSpellType(MonsterSpell, MessageType, int, List<Effect>, List<MonsterSpellLevel>)` | — | ✔ |


##### `MonsterTurn.java`

**Responsible for:** The monster turn engine — the entry points of C's mon-move.c.

**class `MonsterTurn`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `processMonsters(int)` | — | ✔ |
| `resetMonsters()` | — | ✔ |


##### `MonsterUtils.java`

**Responsible for:** Free-standing helper routines for the monster subsystem — a port landing spot for the utility corners of C's mon-util.c and friends.

**class `MonsterUtils`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `killArenaMonster(Monster)` | — | ✔ |
| `updateMonster(Monster, Chunk, boolean)` | — | ✔ |
| `getCommandMonster()` | — | ✔ |
| `showMonsterMessages()` | — | ✔ |
| `updateMonsters(boolean)` | before 260828 | ✔ |


##### `Summon.java`

**Responsible for:** One summon type from summon.txt - the kind of monster a "summon" effect may call up (e.g.

**class `Summon`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (8/8 documented): ✔ `name`, `messageType`, `uniquesAllowed`, `bases`, `raceFlag`, `fallback`, `fallbackName`, `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Summon(String, MessageType, boolean, List<MonsterBase>, MonsterRaceFlag, Summon, String, String)` | before 261009 | ✔ |
| `setFallback(Summon)` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |
| `getMessageType()` | before 261009 | ✔ |
| `isUniquesAllowed()` | before 261009 | ✔ |
| `getBases()` | before 261009 | ✔ |
| `getRaceFlag()` | before 261009 | ✔ |
| `getFallback()` | before 261009 | ✔ |
| `getFallbackName()` | before 261009 | ✔ |
| `getDescription()` | before 261009 | ✔ |
| `toString()` | before 261009 | ✔ |


## middle/monsters/enums

##### `BlowEffectType.java`

**Responsible for:** What kind of player attribute protects against a monster blow effect, as named by the effect-type: directive in blow_effects.txt.

**enum `BlowEffectType`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (7/7 documented): ✔ `BET_ELEMENT`, `BET_FLAG`, `BET_DRAIN`, `BET_THEFT`, `BET_EAT_FOOD`, `BET_EAT_LIGHT`, `BET_ALL_SUSTAINS`
- Fields (1/1 documented): ✔ `type`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `BlowEffectType(String)` | before 261009 | ✔ |
| `getFromString(String)` | before 261009 | ✔ |
| `getType()` | before 261009 | ✔ |


##### `MonTimed.java`

**Responsible for:** The timed status effects a monster can carry (sleep, stun, confusion, fear, slow, haste, hold, disenchantment, command and shapechange), each bundled with the rules that govern it: the Java form of the static effects[] array of struct mon_timed_effect in mon-timed.c, which C builds from the rows ...

**enum `MonTimed`** — class Javadoc: ✔ · type stamp: before 261014

- Enum constants (12/12 documented): ✔ `MON_TMD_NONE`, `MON_TMD_SLEEP`, `MON_TMD_STUN`, `MON_TMD_CONF`, `MON_TMD_FEAR`, `MON_TMD_SLOW`, `MON_TMD_FAST`, `MON_TMD_HOLD`, `MON_TMD_DISEN`, `MON_TMD_COMMAND`, `MON_TMD_CHANGED`, `MON_TMD_MAX`
- Fields (7/7 documented): ✔ `saveThrow`, `stackStyle`, `resistFlag`, `time`, `messageBegin`, `messageEnd`, `messageIncrease`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonTimed(boolean, MonTimedStack, MonsterRaceFlag, int, MonsterMessage, MonsterMessage, MonsterMessage)` | before 261014 | ✔ |


##### `MonTimedFlags.java`

**Responsible for:** The behaviour flags passed to the monster timed-effect functions: Monster.setTimed, Monster.decrementTimed and Monster.clearTimed, the ports of C's mon_set_timed, mon_dec_timed and mon_clear_timed in mon-timed.c.

**enum `MonTimedFlags`** — class Javadoc: ✔ · type stamp: before 260815

- Enum constants (3/3 documented): ✔ `MON_TMD_FLG_NOTIFY`, `MON_TMD_FLG_NOMESSAGE`, `MON_TMD_FLG_NOFAIL`


##### `MonTimedStack.java`

**Responsible for:** How a monster timed effect stacks when re-applied — refuse to stack, or increase the duration.

**enum `MonTimedStack`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `MTS_NONE`, `MTS_NO`, `MTS_INCR`, `MTS_MAX`


##### `MonsterAltmsgType.java`

**Responsible for:** Selects which alternate spell-cast message a monster uses, depending on whether the monster is seen, unseen, or the spell missed.

**enum `MonsterAltmsgType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (3/3 documented): ✔ `MON_ALTMSG_SEEN`, `MON_ALTMSG_UNSEEN`, `MON_ALTMSG_MISS`


##### `MonsterFlag.java`

**Responsible for:** The transient per-monster status flags (in view, active, visible, aware, handled this turn, tracking, …): the Java form of C's MFLAG_* constants, which C builds from the rows of list-mon-temp-flags.h in the anonymous enum of monster.h.

**enum `MonsterFlag`** — class Javadoc: ✔ · type stamp: before 261014

- Enum constants (12/12 documented): ✔ `MFLAG_NONE`, `MFLAG_VIEW`, `MFLAG_ACTIVE`, `MFLAG_NICE`, `MFLAG_SHOW`, `MFLAG_MARK`, `MFLAG_VISIBLE`, `MFLAG_CAMOUFLAGE`, `MFLAG_AWARE`, `MFLAG_HANDLED`, `MFLAG_TRACKING`, `MFLAG_MAX`
- Fields (1/1 documented): ✔ `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterFlag(String)` | before 261014 | ✔ |


##### `MonsterGroupRole.java`

**Responsible for:** The role a monster plays within its group/pack, mirroring the C original's group-role constants — used by group AI to decide behaviour (e.g.

**enum `MonsterGroupRole`** — class Javadoc: ✔ · type stamp: —

- Enum constants (6/6 documented): ✔ `MON_GROUP_NONE`, `MON_GROUP_LEADER`, `MON_GROUP_SERVANT`, `MON_GROUP_BODYGUARD`, `MON_GROUP_MEMBER`, `MON_GROUP_SUMMON`


##### `MonsterGroupType.java`

**Responsible for:** Distinguishes a monster's primary (originally-generated) group from a group formed by summoning.

**enum `MonsterGroupType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (3/3 documented): ✔ `PRIMARY_GROUP`, `SUMMON_GROUP`, `GROUP_MAX`


##### `MonsterMessage.java`

**Responsible for:** The set of templated messages describing what happens to a monster (dies, resists, wakes up, flees, is held, …), each with its MessageType category, whether the monster's name is omitted, and a template using [singular\|plural] markers.

**enum `MonsterMessage`** — class Javadoc: ✔ · type stamp: before 261014

- Enum constants (63/63 documented): ✔ `MON_MSG_NONE`, `MON_MSG_DIE`, `MON_MSG_DESTROYED`, `MON_MSG_RESIST_A_LOT`, `MON_MSG_HIT_HARD`, `MON_MSG_RESIST`, `MON_MSG_IMMUNE`, `MON_MSG_RESIST_SOMEWHAT`, `MON_MSG_UNAFFECTED`, `MON_MSG_SPAWN`, `MON_MSG_HEALTHIER`, `MON_MSG_FALL_ASLEEP`, `MON_MSG_WAKES_UP`, `MON_MSG_CRINGE_LIGHT`, `MON_MSG_SHRIVEL_LIGHT`, `MON_MSG_LOSE_SKIN`, `MON_MSG_DISSOLVE`, `MON_MSG_CATCH_FIRE`, `MON_MSG_BADLY_FROZEN`, `MON_MSG_SHUDDER`, `MON_MSG_CHANGE`, `MON_MSG_DISAPPEAR`, `MON_MSG_MORE_DAZED`, `MON_MSG_DAZED`, `MON_MSG_NOT_DAZED`, `MON_MSG_MORE_CONFUSED`, `MON_MSG_CONFUSED`, `MON_MSG_NOT_CONFUSED`, `MON_MSG_MORE_SLOWED`, `MON_MSG_SLOWED`, `MON_MSG_NOT_SLOWED`, `MON_MSG_MORE_HASTED`, `MON_MSG_HASTED`, `MON_MSG_NOT_HASTED`, `MON_MSG_MORE_AFRAID`, `MON_MSG_FLEE_IN_TERROR`, `MON_MSG_NOT_AFRAID`, `MON_MSG_HELD`, `MON_MSG_NOT_HELD`, `MON_MSG_DISEN`, `MON_MSG_NOT_DISEN`, `MON_MSG_COMMAND`, `MON_MSG_NOT_COMMAND`, `MON_MSG_SHAPE_FAIL`, `MON_MSG_MORIA_DEATH`, `MON_MSG_DISINTEGRATES`, `MON_MSG_FREEZE_SHATTER`, `MON_MSG_MANA_DRAIN`, `MON_MSG_BRIEF_PUZZLE`, `MON_MSG_MAINTAIN_SHAPE`, `MON_MSG_UNHARMED`, `MON_MSG_APPEAR`, `MON_MSG_HIT_AND_RUN`, `MON_MSG_QUAKE_DEATH`, `MON_MSG_QUAKE_HURT`, `MON_MSG_95`, `MON_MSG_75`, `MON_MSG_50`, `MON_MSG_35`, `MON_MSG_20`, `MON_MSG_10`, `MON_MSG_0`, `MON_MSG_MAX`
- Fields (3/3 documented): ✔ `type`, `omitMonsterName`, `message`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterMessage(MessageType, boolean, String)` | before 261014 | ✔ |
| `getMessage()` | before 261014 | ✔ |
| `isOmitMonsterName()` | before 261014 | ✔ |
| `getType()` | before 261014 | ✔ |


##### `MonsterRaceCategory.java`

**Responsible for:** The categories used to group monster race flags by purpose (obvious property, display, generation, lore note, behaviour, drops, detection, environment alteration, race type, vulnerability/resistance, …).

**enum `MonsterRaceCategory`** — class Javadoc: ✔ · type stamp: before 261008

- Enum constants (16/16 documented): ✔ `RFT_NONE`, `RFT_OBV`, `RFT_DISP`, `RFT_GEN`, `RFT_NOTE`, `RFT_BEHAV`, `RFT_DROP`, `RFT_DET`, `RFT_ALTER`, `RFT_RACE_N`, `RFT_RACE_A`, `RFT_VULN`, `RFT_VULN_I`, `RFT_RES`, `RFT_PROT`, `RFT_MAX`


##### `MonsterRaceFlag.java`

**Responsible for:** The fixed intrinsic flags of a monster race (unique, male/female, group AI, forced depth, multiply, resistances, immunities, status protections, race type descriptors, …), each tagged with its MonsterRaceCategory and an optional description.

**enum `MonsterRaceFlag`** — class Javadoc: ✔ · type stamp: before 261010

- Enum constants (0/85 documented): ✘ `RF_NONE`, `RF_UNIQUE`, `RF_QUESTOR`, `RF_MALE`, `RF_FEMALE`, `RF_GROUP_AI`, `RF_NAME_COMMA`, `RF_CHAR_CLEAR`, `RF_ATTR_RAND`, `RF_ATTR_CLEAR`, `RF_ATTR_MULTI`, `RF_ATTR_FLICKER`, `RF_FORCE_DEPTH`, `RF_FORCE_SLEEP`, `RF_FORCE_EXTRA`, `RF_SEASONAL`, `RF_UNAWARE`, `RF_MULTIPLY`, `RF_REGENERATE`, `RF_FRIGHTENED`, `RF_NEVER_BLOW`, `RF_NEVER_MOVE`, `RF_RAND_25`, `RF_RAND_50`, `RF_MIMIC_INV`, `RF_STUPID`, `RF_SMART`, `RF_SPIRIT`, `RF_POWERFUL`, `RF_ONLY_GOLD`, `RF_ONLY_ITEM`, `RF_DROP_40`, `RF_DROP_60`, `RF_DROP_1`, `RF_DROP_2`, `RF_DROP_3`, `RF_DROP_4`, `RF_DROP_GOOD`, `RF_DROP_GREAT`, `RF_DROP_20`, `RF_INVISIBLE`, `RF_COLD_BLOOD`, `RF_EMPTY_MIND`, `RF_WEIRD_MIND`, `RF_OPEN_DOOR`, `RF_BASH_DOOR`, `RF_PASS_WALL`, `RF_KILL_WALL`, `RF_SMASH_WALL`, `RF_MOVE_BODY`, `RF_KILL_BODY`, `RF_TAKE_ITEM`, `RF_KILL_ITEM`, `RF_CLEAR_WEB`, `RF_PASS_WEB`, `RF_ORC`, `RF_TROLL`, `RF_GIANT`, `RF_DRAGON`, `RF_DEMON`, `RF_ANIMAL`, `RF_EVIL`, `RF_UNDEAD`, `RF_NONLIVING`, `RF_METAL`, `RF_HURT_LIGHT`, `RF_HURT_ROCK`, `RF_HURT_FIRE`, `RF_HURT_COLD`, `RF_IM_ACID`, `RF_IM_ELEC`, `RF_IM_FIRE`, `RF_IM_COLD`, `RF_IM_POIS`, `RF_IM_NETHER`, `RF_IM_WATER`, `RF_IM_PLASMA`, `RF_IM_NEXUS`, `RF_IM_DISEN`, `RF_NO_FEAR`, `RF_NO_STUN`, `RF_NO_CONF`, `RF_NO_SLEEP`, `RF_NO_HOLD`, `RF_NO_SLOW`
- Fields (2/2 documented): ✔ `category`, `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterRaceFlag(MonsterRaceCategory, String)` | before 261010 | ✔ |
| `getCategory()` | before 261010 | ✔ |
| `getDescription()` | before 261010 | ✔ |


##### `MonsterSpell.java`

**Responsible for:** The complete set of monster spells/abilities (bolts, balls, breaths, summons, teleports, annoyances, …), each tagged with the MonsterSpellTypeEnum categories it belongs to (stored as a Flag set so a spell can have several).

**enum `MonsterSpell`** — class Javadoc: ✔ · type stamp: before 261011

- Enum constants (93/93 documented): ✔ `RSF_NONE`, `RSF_SHRIEK`, `RSF_WHIP`, `RSF_SPIT`, `RSF_SHOT`, `RSF_ARROW`, `RSF_BOLT`, `RSF_BR_ACID`, `RSF_BR_ELEC`, `RSF_BR_FIRE`, `RSF_BR_COLD`, `RSF_BR_POIS`, `RSF_BR_NETH`, `RSF_BR_LIGHT`, `RSF_BR_DARK`, `RSF_BR_SOUN`, `RSF_BR_CHAO`, `RSF_BR_DISE`, `RSF_BR_NEXU`, `RSF_BR_TIME`, `RSF_BR_INER`, `RSF_BR_GRAV`, `RSF_BR_SHAR`, `RSF_BR_PLAS`, `RSF_BR_WALL`, `RSF_BR_MANA`, `RSF_BOULDER`, `RSF_WEAVE`, `RSF_BA_ACID`, `RSF_BA_ELEC`, `RSF_BA_FIRE`, `RSF_BA_COLD`, `RSF_BA_POIS`, `RSF_BA_SHAR`, `RSF_BA_NETH`, `RSF_BA_WATE`, `RSF_BA_MANA`, `RSF_BA_HOLY`, `RSF_BA_DARK`, `RSF_BA_LIGHT`, `RSF_STORM`, `RSF_DRAIN_MANA`, `RSF_MIND_BLAST`, `RSF_BRAIN_SMASH`, `RSF_WOUND`, `RSF_BO_ACID`, `RSF_BO_ELEC`, `RSF_BO_FIRE`, `RSF_BO_COLD`, `RSF_BO_POIS`, `RSF_BO_NETH`, `RSF_BO_WATE`, `RSF_BO_MANA`, `RSF_BO_PLAS`, `RSF_BO_ICE`, `RSF_MISSILE`, `RSF_BE_ELEC`, `RSF_BE_NETH`, `RSF_SCARE`, `RSF_BLIND`, `RSF_CONF`, `RSF_SLOW`, `RSF_HOLD`, `RSF_HASTE`, `RSF_HEAL`, `RSF_HEAL_KIN`, `RSF_BLINK`, `RSF_TPORT`, `RSF_TELE_TO`, `RSF_TELE_SELF_TO`, `RSF_TELE_AWAY`, `RSF_TELE_LEVEL`, `RSF_DARKNESS`, `RSF_TRAPS`, `RSF_FORGET`, `RSF_SHAPECHANGE`, `RSF_S_KIN`, `RSF_S_HI_DEMON`, `RSF_S_MONSTER`, `RSF_S_MONSTERS`, `RSF_S_ANIMAL`, `RSF_S_SPIDER`, `RSF_S_HOUND`, `RSF_S_HYDRA`, `RSF_S_AINU`, `RSF_S_DEMON`, `RSF_S_UNDEAD`, `RSF_S_DRAGON`, `RSF_S_HI_UNDEAD`, `RSF_S_HI_DRAGON`, `RSF_S_WRAITH`, `RSF_S_UNIQUE`, `RSF_MAX`
- Fields (1/1 documented): ✔ `spellTypes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterSpell(MonsterSpellTypeEnum[])` | before 261011 | ✔ |
| `getTypes()` | before 261011 | ✔ |


##### `MonsterSpellTypeEnum.java`

**Responsible for:** The categories a monster spell can belong to, the Java form of C's enum mon_spell_type (the RST_* bit flags) in mon-spell.h.

**enum `MonsterSpellTypeEnum`** — class Javadoc: ✔ · type stamp: before 261014

- Enum constants (14/14 documented): ✔ `RST_NONE`, `RST_BOLT`, `RST_BALL`, `RST_BREATH`, `RST_DIRECT`, `RST_ANNOY`, `RST_HASTE`, `RST_HEAL`, `RST_HEAL_OTHER`, `RST_TACTIC`, `RST_ESCAPE`, `RST_SUMMON`, `RST_INNATE`, `RST_ARCHERY`
- Fields (1/1 documented): ✔ `isDamage`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MonsterSpellTypeEnum(boolean)` | before 261014 | ✔ |
| `isDamage()` | before 261014 | ✔ |


## middle/numerics

##### `Dice.java`

**Responsible for:** Empty placeholder class for dice handling; no members yet (dice live elsewhere in `middle/numerics`).

**class `Dice`** — class Javadoc: ✘ · type stamp: —


##### `Guards.java`

**Responsible for:** Integer arithmetic that saturates instead of wrapping — the port of C's add_guardi and sub_guardi (z-util.c:937, z-util.c:949).

**class `Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addGuardI(int, int)` | — | ✔ |
| `addGuardI16(int, int)` | — | ✘ |
| `subGuardI(int, int)` | — | ✔ |


##### `Random.java`

**Responsible for:** A parameterized random value of the classic Angband form base + dice 'd' sides + m_bonus, where the m_bonus term is a ceiling on a bonus that grows with dungeon level.

**class `Random`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (11/11 documented): ✔ `ZERO`, `ONE`, `logger`, `baseStr`, `base`, `dice`, `sidesStr`, `sides`, `mBonus`, `toNegate`, `negated`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Random(int, int, int, int, boolean)` | before 260815 | ✔ |
| ctor `Random(String, int, int, int)` | before 260815 | ✔ |
| ctor `Random(int, int, int, String)` | before 260815 | ✔ |
| `Zero()` | on 261003 | ✔ |
| `One()` | on 261003 | ✔ |
| `parseStr(String)` | before 260815 | ✔ |
| `setToNegate(boolean)` | before 260815 | ✔ |
| `getBase()` | before 260815 | ✔ |
| `setBase(int)` | before 260815 | ✔ |
| `getDice()` | before 260815 | ✔ |
| `setDice(int)` | before 260815 | ✔ |
| `getSides()` | before 260815 | ✔ |
| `setSides(int)` | before 260815 | ✔ |
| `getMBonus()` | before 260815 | ✔ |
| `setMBonus(int)` | before 260815 | ✔ |
| `negate()` | before 260815 | ✔ |
| `toString()` | before 260815 | ✔ |
| `randCalc(int, DamageAspect)` | before 260815 | ✔ |
| `isValid(int)` | before 260815 | ✔ |
| `varies()` | before 260815 | ✔ |
| `hasBase()` | before 260815 | ✔ |
| `hasDice()` | before 260815 | ✔ |
| `hasSides()` | before 260815 | ✔ |
| `hasBonus()` | before 260815 | ✔ |
| `copy()` | — | ✔ |


##### `RandomChance.java`

**Responsible for:** An "X in Y" chance of success, such as 8 in 125 (6.4%), the Java form of C's random_chance struct (z-rand.h) with its two functions random_chance_check and random_chance_scaled (z-rand.c).

**class `RandomChance`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (2/2 documented): ✔ `numerator`, `denominator`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RandomChance(int, int)` | before 261014 | ✔ |
| `scale(int)` | before 261014 | ✔ |
| `check()` | before 261014 | ✔ |


##### `RandomValueUtils.java`

**Responsible for:** Static collection of the game's random-number helpers — the Java port of the standard Angband RNG utilities in the original C source (src/z-rand.c: randint0, randint1, rand_spread, one_in_, Rand_normal, damroll, m_bonus and friends).

**class `RandomValueUtils`** — class Javadoc: ✔ · type stamp: before 261005

- Fields (3/3 documented): ✔ `logger`, `MAX_RAND_DEPTH`, `random`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `randDiv(int)` | on 260830 | ✔ |
| `randInt0(int)` | before 261005 | ✔ |
| `randInt1(int)` | before 261005 | ✔ |
| `randSpread(int, int)` | before 261005 | ✔ |
| `oneIn(int)` | before 261005 | ✔ |
| `stateInit(long)` | before 261005 | ✔ |
| `stateInit()` | before 261005 | ✔ |
| `normal(int, float)` | before 261005 | ✔ |
| `sample(int, int, int, float, float)` | before 261005 | ✔ |
| `damRoll(int, int)` | before 261005 | ✔ |
| `damCalc(int, int, DamageAspect)` | before 261005 | ✔ |
| `randRange(int, int)` | before 261005 | ✔ |
| `simulateDivision(int, int)` | before 261005 | ✔ |
| `mBonus(int, int)` | before 261005 | ✔ |
| `mBonusCalc(int, int, DamageAspect)` | before 261005 | ✔ |


##### `Rational.java`

**Responsible for:** An exact fraction (numerator / denominator) kept in lowest terms, used where the game needs precise fractional arithmetic without floating-point drift — for example accumulating fractional regeneration or growth rates that must stay exact over many turns.

**class `Rational`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (4/4 documented): ✔ `denominator`, `numerator`, `zero`, `one`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getNumerator()` | before 261014 | ✔ |
| `getDenominator()` | before 261014 | ✔ |
| ctor `Rational(long)` | before 261014 | ✔ |
| `multi(Rational)` | before 261014 | ✔ |
| `div(Rational)` | before 261014 | ✔ |
| `add(Rational)` | before 261014 | ✔ |
| ctor `Rational(long, long)` | before 261014 | ✔ |
| ctor `Rational(long, long, boolean)` | before 261014 | ✔ |
| `equals(Object)` | before 261014 | ✔ |
| `getGCD(long, long)` | before 261014 | ✔ |
| `simplify()` | before 261014 | ✔ |
| `getIntegerPart()` | before 261014 | ✔ |
| `getIntegerPart(int)` | before 261014 | ✔ |
| `sub(Rational)` | before 261014 | ✔ |
| `getRemainder()` | before 261014 | ✔ |
| `toString()` | before 261014 | ✔ |
| `toUint(int)` | before 261014 | ✔ |
| `hashCode()` | before 261014 | ✔ |


## middle/objects

##### `Archery.java`

**Responsible for:** One row of the launcher-and-ammo assumptions the power calculation prices missile weapons by — the port of C's struct archery and its three-row table (obj-power.c).

**class `Archery`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `ammoType`, `ammoDamage`, `launchDamage`, `launchMult`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Archery(TValue, int, int, int)` | — | ✔ |
| `getAmmoType()` | on 260827 | ✔ |
| `getAmmoDamage()` | on 260827 | ✔ |
| `getLaunchDamage()` | on 260827 | ✔ |
| `getLaunchMult()` | on 260827 | ✔ |


##### `Artifact.java`

**Responsible for:** A unique artifact definition (as loaded from artifact.txt) — a one-of-a-kind item with fixed bonuses, flags, modifiers, brands/slays/curses, an optional activation, and allocation parameters.

**class `Artifact`** — class Javadoc: ✔ · type stamp: before 260827

- Fields (25/25 documented): ✔ `name`, `text`, `tValue`, `sValue`, `toHit`, `toDam`, `toAC`, `ac`, `diceString`, `weight`, `cost`, `flags`, `modifiers`, `elInfo`, `brands`, `slays`, `curses`, `level`, `allocProb`, `allocMin`, `allocMax`, `activation`, `activationMessage`, `time`, `aupInfo`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Artifact(String, String, TValue, String, int, int, int, int, String, int, int, Flag<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, Set<Brand>, Set<Slay>, Map<Curse,CurseData>, int, int, int, int, Activation, String, Random)` | before 260827 | ✔ |
| `getName()` | before 260827 | ✔ |
| `getText()` | before 260827 | ✔ |
| `gettValue()` | before 260827 | ✔ |
| `getsValue()` | before 260827 | ✔ |
| `getToHit()` | before 260827 | ✔ |
| `getToDam()` | before 260827 | ✔ |
| `getToAC()` | before 260827 | ✔ |
| `getAc()` | before 260827 | ✔ |
| `getDiceString()` | before 260827 | ✔ |
| `getWeight()` | before 260827 | ✔ |
| `getCost()` | before 260827 | ✔ |
| `getFlags()` | before 260827 | ✔ |
| `getModifiers()` | before 260827 | ✔ |
| `getElInfo()` | before 260827 | ✔ |
| `getBrands()` | before 260827 | ✔ |
| `getSlays()` | before 260827 | ✔ |
| `getCurses()` | before 260827 | ✔ |
| `getLevel()` | before 260827 | ✔ |
| `getAllocProb()` | before 260827 | ✔ |
| `getAllocMin()` | before 260827 | ✔ |
| `getAllocMax()` | before 260827 | ✔ |
| `getActivation()` | before 260827 | ✔ |
| `getActivationMessage()` | before 260827 | ✔ |
| `getTime()` | before 260827 | ✔ |
| `copy()` | on 260827 | ✔ |
| `setArtifactUpkeep(ArtifactUpkeep)` | on 260902 | ✔ |
| `getAup()` | on 260902 | ✔ |


##### `ArtifactSetData.java`

**Responsible for:** The statistics the random-artifact generator learns from one set of artifacts - the port of C's struct artifact_set_data (obj-randart.h), built by artifact_set_data_new() in obj-randart.c.

**class `ArtifactSetData`** — class Javadoc: ✔ · type stamp: on 260908

- Fields (32/32 documented): ✔ `hitIncrement`, `damIncrement`, `hitStartVal`, `damStartVal`, `acStartVal`, `acIncrement`, `artProbs`, `tvProbs`, `tvNum`, `bowTotal`, `meleeTotal`, `bootTotal`, `gloveTotal`, `headgearTotal`, `shieldTotal`, `cloakTotal`, `armourTotal`, `otherTotal`, `total`, `negPowerTotal`, `tvFreq`, `basePower`, `maxPower`, `minPower`, `avgPower`, `varPower`, `avgTvPower`, `minTvPower`, `maxTvPower`, `baseItemLevel`, `baseItemProb`, `baseArtAlloc`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ArtifactSetData()` | on 260908 | ✔ |
| `getHitIncrement()` | on 260908 | ✔ |
| `setHitIncrement(int)` | on 260908 | ✔ |
| `getDamIncrement()` | on 260908 | ✔ |
| `setDamIncrement(int)` | on 260908 | ✔ |
| `getHitStartVal()` | on 260908 | ✔ |
| `setHitStartVal(int)` | on 260908 | ✔ |
| `getDamStartVal()` | on 260908 | ✔ |
| `setDamStartVal(int)` | on 260908 | ✔ |
| `getAcStartVal()` | on 260908 | ✔ |
| `setAcStartVal(int)` | on 260908 | ✔ |
| `getAcIncrement()` | on 260908 | ✔ |
| `setAcIncrement(int)` | on 260908 | ✔ |
| `getBowTotal()` | on 260908 | ✔ |
| `setBowTotal(int)` | on 260908 | ✔ |
| `getMeleeTotal()` | on 260908 | ✔ |
| `setMeleeTotal(int)` | on 260908 | ✔ |
| `getBootTotal()` | on 260908 | ✔ |
| `setBootTotal(int)` | on 260908 | ✔ |
| `getGloveTotal()` | on 260908 | ✔ |
| `setGloveTotal(int)` | on 260908 | ✔ |
| `getHeadgearTotal()` | on 260908 | ✔ |
| `setHeadgearTotal(int)` | on 260908 | ✔ |
| `getShieldTotal()` | on 260908 | ✔ |
| `setShieldTotal(int)` | on 260908 | ✔ |
| `getCloakTotal()` | on 260908 | ✔ |
| `setCloakTotal(int)` | on 260908 | ✔ |
| `getArmourTotal()` | on 260908 | ✔ |
| `setArmourTotal(int)` | on 260908 | ✔ |
| `getOtherTotal()` | on 260908 | ✔ |
| `setOtherTotal(int)` | on 260908 | ✔ |
| `getTotal()` | on 260908 | ✔ |
| `setTotal(int)` | on 260908 | ✔ |
| `getNegPowerTotal()` | on 260908 | ✔ |
| `setNegPowerTotal(int)` | on 260908 | ✔ |
| `getMaxPower()` | on 260908 | ✔ |
| `setMaxPower(int)` | on 260908 | ✔ |
| `getMinPower()` | on 260908 | ✔ |
| `setMinPower(int)` | on 260908 | ✔ |
| `getAvgPower()` | on 260908 | ✔ |
| `setAvgPower(int)` | on 260908 | ✔ |
| `getVarPower()` | on 260908 | ✔ |
| `setVarPower(int)` | on 260908 | ✔ |
| `getArtProbs(ArtifactIndex)` | on 260908 | ✔ |
| `setArtProbs(ArtifactIndex, int)` | on 260908 | ✔ |
| `getTvProbs(TValue)` | on 260908 | ✔ |
| `setTvProbs(TValue, int)` | on 260908 | ✔ |
| `getTvNum(TValue)` | on 260908 | ✔ |
| `setTvNum(TValue, int)` | on 260908 | ✔ |
| `getTvFreq(TValue)` | on 260908 | ✔ |
| `setTvFreq(TValue, int)` | on 260908 | ✔ |
| `getBasePower(Artifact)` | on 260908 | ✔ |
| `setBasePower(Artifact, int)` | on 260908 | ✔ |
| `getAvgTvPower(TValue)` | on 260908 | ✔ |
| `setAvgTvPower(TValue, int)` | on 260908 | ✔ |
| `getMinTvPower(TValue)` | on 260908 | ✔ |
| `setMinTvPower(TValue, int)` | on 260908 | ✔ |
| `getMaxTvPower(TValue)` | on 260908 | ✔ |
| `setMaxTvPower(TValue, int)` | on 260908 | ✔ |
| `getBaseItemLevel(Artifact)` | on 260908 | ✔ |
| `setBaseItemLevel(Artifact, int)` | on 260908 | ✔ |
| `getBaseItemProb(Artifact)` | on 260908 | ✔ |
| `setBaseItemProb(Artifact, int)` | on 260908 | ✔ |
| `getBaseArtAlloc(Artifact)` | on 260908 | ✔ |
| `setBaseArtAlloc(Artifact, int)` | on 260908 | ✔ |


##### `ArtifactUpkeep.java`

**Responsible for:** Per-artifact state that changes over the course of play — the port of C's struct artifact_upkeep (object.h), which C keeps as one parallel array (aup_info) indexed alongside a_info.

**class `ArtifactUpkeep`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (3/3 documented): ✔ `created`, `seen`, `everseen`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `isCreated()` | on 260902 | ✔ |
| `setCreated(boolean)` | on 260902 | ✔ |
| `isSeen()` | on 260902 | ✔ |
| `setSeen(boolean)` | on 260902 | ✔ |
| `isEverseen()` | on 260902 | ✔ |
| `setEverseen(boolean)` | on 260902 | ✔ |


##### `BonusSource.java`

**Responsible for:** One contributor of bonuses to calcBonuses' equipment walk — either a worn item or one of the curses on it.

**interface `BonusSource`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flags()` | on 260820 | ✔ |
| `modifier(ObjectModifier)` | on 260820 | ✔ |
| `resLevel(ElementEnum)` | on 260820 | ✔ |
| `knownResLevel(ElementEnum)` | on 260820 | ✔ |
| `baseAC()` | on 260820 | ✔ |
| `toAC()` | on 260820 | ✔ |
| `knownToAC()` | on 260820 | ✔ |
| `toHit()` | on 260820 | ✔ |
| `knownToHit()` | on 260820 | ✔ |
| `toDam()` | on 260820 | ✔ |
| `knownToDam()` | on 260820 | ✔ |
| `isDigger()` | on 260820 | ✔ |
| `flagsKnown()` | on 260820 | ✔ |
| `flagSet(ObjectFlag)` | on 260820 | ✔ |


##### `Brand.java`

**Responsible for:** A weapon/ammo brand (as loaded from brand.txt) — extra elemental damage a weapon deals, with the damage verb, the monster flags that resist or are vulnerable to it, the damage multipliers (standard and O-combat) and a power rating.

**class `Brand`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (8/8 documented): ✔ `code`, `name`, `verb`, `resistFlag`, `vulnerableFlag`, `multiplier`, `oMultiplier`, `power`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Brand(String, String, String, MonsterRaceFlag, MonsterRaceFlag, int, int, int)` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |
| `getCode()` | before 261009 | ✔ |
| `toString()` | before 261009 | ✔ |
| `equals(Object)` | before 261009 | ✔ |
| `hashCode()` | before 261009 | ✔ |
| `getPower()` | before 261009 | ✔ |
| `getMultiplier()` | before 261009 | ✔ |
| `copy()` | on 260904 | ✔ |


##### `ChestTrap.java`

**Responsible for:** One kind of chest trap, loaded from chest_trap.txt.

**class `ChestTrap`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (8/8 documented): ✔ `name`, `code`, `level`, `effect`, `destroy`, `magic`, `message`, `messageDeath`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ChestTrap(String, ChestTrapCode, int, List<Effect>, boolean, boolean, String, String)` | before 260815 | ✔ |
| `getName()` | before 260815 | ✔ |
| `getPVal()` | before 260815 | ✔ |
| `getCode()` | before 260815 | ✔ |
| `getLevel()` | before 260815 | ✔ |
| `getEffect()` | before 260815 | ✔ |
| `isDestroy()` | before 260815 | ✔ |
| `isMagic()` | before 260815 | ✔ |
| `getMessage()` | before 260815 | ✔ |
| `getMessageDeath()` | before 260815 | ✔ |


##### `Curse.java`

**Responsible for:** The definition of a curse (as loaded from curse.txt) — a negative property that can attach to objects.

**class `Curse`** — class Javadoc: ✔ · type stamp: before 261005

- Fields (9/9 documented): ✔ `logger`, `name`, `index`, `objectBases`, `itemObject`, `conflictNames`, `conflictFlags`, `description`, `conflict`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Curse(String, List<ObjectBase>, ItemObject, List<String>, Flag<ObjectFlag>, String, int)` | before 261005 | ✔ |
| `getItemObject()` | before 261005 | ✔ |
| `getIndex()` | before 261003 | ✔ |
| `getName()` | before 261005 | ✔ |
| `getObjectBases()` | before 261005 | ✔ |
| `getConflict()` | before 261005 | ✔ |
| `setConflict(List<Curse>)` | before 261005 | ✔ |
| `getConflictFlags()` | before 261005 | ✔ |
| `getDescription()` | before 261005 | ✔ |
| `getConflictNames()` | before 261005 | ✔ |
| `setIndex(int)` | before 261005 | ✔ |
| `canAfflict(ObjectBase)` | before 261005 | ✔ |
| `modifyWeightForCurse(int)` | before 260820 | ✔ |


##### `CurseData.java`

**Responsible for:** The per-object instance data for an applied Curse — its current power and the timeout until its next effect.

**class `CurseData`** — class Javadoc: ✔ · type stamp: before 260817

- Fields (2/2 documented): ✔ `power`, `timeout`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CurseData(int, int)` | before 260817 | ✔ |
| ctor `CurseData(CurseData)` | on 260817 | ✔ |
| `getPower()` | before 260817 | ✔ |
| `getTimeout()` | before 260817 | ✔ |
| `setPower(int)` | before 260817 | ✔ |
| `decrementTimeout()` | before 260817 | ✔ |
| `setTimeout(int)` | before 260817 | ✔ |
| `equals(Object)` | on 260817 | ✔ |
| `hashCode()` | on 260817 | ✔ |


##### `CurseSource.java`

**Responsible for:** The BonusSource view of one Curse on a worn item — the later passes of calcBonuses' equipment walk, C's obj = curses[index].obj in calc_bonuses() (player-calcs.c).

**class `CurseSource`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (1/1 documented): ✔ `curse`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CurseSource(Curse)` | on 260820 | ✔ |
| `flags()` | on 260820 | ✔ |
| `modifier(ObjectModifier)` | on 260820 | ✔ |
| `resLevel(ElementEnum)` | on 260820 | ✔ |
| `knownResLevel(ElementEnum)` | on 260820 | ✔ |
| `baseAC()` | on 260820 | ✔ |
| `toAC()` | on 260820 | ✔ |
| `knownToAC()` | on 260820 | ✔ |
| `toHit()` | on 260820 | ✔ |
| `knownToHit()` | on 260820 | ✔ |
| `toDam()` | on 260820 | ✔ |
| `knownToDam()` | on 260820 | ✔ |
| `isDigger()` | on 260820 | ✔ |
| `flagsKnown()` | on 260820 | ✔ |
| `flagSet(ObjectFlag)` | on 260820 | ✔ |


##### `EgoItem.java`

**Responsible for:** An ego item template (as loaded from ego_item.txt) — a named modifier applied to a base item to make a "magical" variant (e.g.

**class `EgoItem`** — class Javadoc: ✔ · type stamp: before 260816

- Fields (28/28 documented): ✔ `name`, `text`, `possItems`, `egoIndex`, `cost`, `flags`, `flagsOff`, `kindFLags`, `modifiers`, `minModifiers`, `elInfo`, `brands`, `slays`, `curses`, `rating`, `allocProb`, `allocMin`, `allocMax`, `toHit`, `toDam`, `toAC`, `minToHit`, `minToDam`, `minToAC`, `activation`, `time`, `everSeen`, `ignoreTypes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EgoItem(String, String, int, int, Flag<ObjectFlag>, Flag<ObjectFlag>, Flag<ObjectKindFlag>, Map<ObjectModifier,Random>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, Set<Brand>, Set<Slay>, Map<Curse,CurseData>, int, int, int, int, List<ObjectKind>, Random, Random, Random, int, int, int, Activation, Random, boolean)` | before 260817 | ✔ |
| `isEverSeen()` | — | ✔ |
| `setEverSeen(boolean)` | on 261014 | ✔ |
| `getFlags()` | — | ✔ |
| `getModifier(ObjectModifier)` | — | ✔ |
| `getElInfo()` | before 260816 | ✔ |
| `getBrands()` | before 260816 | ✔ |
| `getSlays()` | before 260816 | ✔ |
| `getCurses()` | before 260816 | ✔ |
| `getOffFlags()` | before 260820 | ✔ |
| `getIgnoreType(IgnoreType)` | — | ✔ |
| `setIgnoreType(IgnoreType)` | before 260827 | ✔ |
| `clearIgnoreType(IgnoreType)` | before 261014 | ✔ |
| `copy()` | — | ✔ |
| `egoHasIgnoreType(IgnoreType)` | — | ✔ |


##### `ElementInfo.java`

**Responsible for:** How something relates to a single damage element: the ElementInfoEnum flags (hates/ignores/random) plus a resistance level.

**class `ElementInfo`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (2/2 documented): ✔ `flags`, `resLevel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ElementInfo()` | before 261009 | ✔ |
| `getResLevel()` | before 261009 | ✔ |
| `setResLevel(int)` | before 261009 | ✔ |
| `getFlags()` | before 261009 | ✔ |
| `copy()` | before 261009 | ✔ |
| `on(ElementInfoEnum)` | before 261009 | ✔ |
| `has(ElementInfoEnum)` | before 261009 | ✔ |


##### `ElementPowers.java`

**Responsible for:** What one element is worth to an object that ignores, resists, is immune to, or is vulnerable to it — the port of C's struct element_powers and its thirteen-row el_powers[] table (obj-power.c).

**class `ElementPowers`** — class Javadoc: ✔ · type stamp: before 260827

- Fields (7/7 documented): ✔ `element`, `name`, `type`, `ignorePower`, `vulnPower`, `resPower`, `imPower`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ElementPowers(ElementEnum, String, ResType, int, int, int, int)` | before 260827 | ✔ |
| `getElement()` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |
| `getType()` | before 261009 | ✔ |
| `getIgnorePower()` | before 261009 | ✔ |
| `getVulnPower()` | before 261009 | ✔ |
| `getResPower()` | before 261009 | ✔ |
| `getImPower()` | before 261009 | ✔ |


##### `ElementSet.java`

**Responsible for:** One combination of elemental protections that is worth more together than separately — the port of C's struct element_set and its three-row table element_sets[] (obj-power.c).

**class `ElementSet`** — class Javadoc: ✔ · type stamp: before 260827

- Fields (7/7 documented): ✔ `type`, `resLevel`, `factor`, `bonus`, `size`, `count`, `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ElementSet(ResType, int, int, int, int, int, String)` | before 260827 | ✔ |
| `getType()` | before 261009 | ✔ |
| `getResLevel()` | before 261009 | ✔ |
| `getFactor()` | before 261009 | ✔ |
| `getBonus()` | before 261009 | ✔ |
| `getSize()` | before 261009 | ✔ |
| `getCount()` | before 261009 | ✔ |
| `setCount(int)` | before 261009 | ✔ |
| `getDescription()` | before 261009 | ✔ |


##### `FlagSet.java`

**Responsible for:** One family of object flags that is worth more held together than separately — the port of C's struct flag_set and its three-row table (obj-power.c).

**class `FlagSet`** — class Javadoc: ✔ · type stamp: on 260827

- Fields (6/6 documented): ✔ `type`, `factor`, `bonus`, `size`, `count`, `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `FlagSet(ObjectFlagType, int, int, int, int, String)` | on 260827 | ✔ |
| `getType()` | on 260827 | ✔ |
| `getFactor()` | on 260827 | ✔ |
| `getBonus()` | on 260827 | ✔ |
| `getSize()` | on 260827 | ✔ |
| `getCount()` | on 260827 | ✔ |
| `setCount(int)` | on 260827 | ✔ |
| `getDescription()` | on 260827 | ✔ |


##### `Flavour.java`

**Responsible for:** One flavour: the disguise (a potion's colour word, a ring's gem) an object kind is shown under while the player is unaware of it.

**class `Flavour`** — class Javadoc: ✔ · type stamp: —

- Fields (7/7 documented): ✔ `text`, `sValStr`, `sVal`, `colour`, `index`, `isFixed`, `flavourKind`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Flavour(String, String, ColourEnum, int)` | — | ✔ |
| ctor `Flavour(String, ColourEnum, int)` | — | ✔ |
| `setsVal(int)` | — | ✔ |
| `getFlavourKind()` | on 260827 | ✔ |
| `setFlavourKind(FlavourKind)` | on 260827 | ✔ |
| `getText()` | — | ✔ |
| `getsValStr()` | — | ✔ |
| `getsVal()` | — | ✔ |
| `getColour()` | — | ✔ |
| `getIndex()` | — | ✔ |
| `isFixed()` | — | ✔ |
| `copy()` | — | ✔ |
| `setText(String)` | on 260907 | ✔ |


##### `FlavourKind.java`

**Responsible for:** One kind: block from flavor.txt: an object type together with the display glyph and the set of Flavours that can disguise objects of that type.

**class `FlavourKind`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (3/3 documented): ✔ `value`, `glyph`, `flavours`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `FlavourKind(TValue, char, List<Flavour>)` | before 261010 | ✔ |
| `getValue()` | before 261010 | ✔ |
| `getGlyph()` | before 261010 | ✔ |
| `getFlavours()` | before 261010 | ✔ |


##### `Grouper.java`

**Responsible for:** A TValue-to-name pairing used to group and label related object types in lists and menus.

**class `Grouper`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (2/2 documented): ✔ `tValue`, `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Grouper(TValue, String)` | before 261014 | ✔ |
| `getName()` | before 261014 | ✔ |
| `gettValue()` | before 261014 | ✔ |


##### `ItemObject.java`

**Responsible for:** A concrete item instance in the game — a specific sword, potion, etc.

**class `ItemObject`** — class Javadoc: ✔ · type stamp: —

- Fields (40/40 documented): ✔ `logger`, `CURSE_ORDER`, `player`, `kind`, `ego`, `artifact`, `known`, `location`, `tValue`, `sValue`, `pValue`, `VULN_AND_RES`, `damageDice`, `damageSides`, `baseDamage`, `weight`, `baseAC`, `toDam`, `toHit`, `toAC`, `flags`, `modifiers`, `elInfo`, `brands`, `slays`, `effect`, `effectMessage`, `activation`, `time`, `timeout`, `number`, `notice`, `heldMIndex`, `mimickingMIndex`, `origin`, `originDepth`, `curses`, `note`, `originRace`, `owningPile`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ItemObject()` | before 260904 | ✔ |
| ctor `ItemObject(ObjectKind, EgoItem, Artifact, ItemObject, Loc, TValue, int, String, int, int, int, int, int, String, int, int, Flag<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, Set<Brand>, Set<Slay>, Map<Curse,CurseData>, List<Effect>, String, List<Activation>, String, int, int, Flag<ObjectNotice>, int, int, ObjectOriginEnum, int, MonsterRace, String)` | before 260904 | ✔ |
| `cursesFactory()` | — | ✔ |
| `earlierObject(ItemObject, ItemObject, boolean)` | — | ✔ |
| `getGrid()` | before 260904 | ✔ |
| `setGrid(Loc)` | before 260904 | ✔ |
| `orNotice(ObjectNotice)` | before 260904 | ✔ |
| `isKnownArtifact()` | on 261008 | ✔ |
| `isArtifact()` | before 260904 | ✔ |
| `getKind()` | before 260904 | ✔ |
| `setKind(ObjectKind)` | before 260904 | ✔ |
| `setHeldMIndex(int)` | before 260904 | ✔ |
| `similar(ItemObject, Flag<ObjectStackEnum>)` | before 260822 | ✔ |
| `checkElementStacking(ItemObject, ItemObject)` | on 260817 | ✔ |
| `isFullyKnown()` | before 260815 | ✔ |
| `cursesAreEqual(ItemObject)` | before 261002 | ✔ |
| `runesKnown()` | before 261002 | ✔ |
| `effectIsKnown()` | before 261002 | ✔ |
| `originCombine(ItemObject)` | before 261002 | ✔ |
| `distributeCharges(ItemObject, int, boolean)` | before 261002 | ✔ |
| `getCurses()` | before 260817 | ✔ |
| `setCurses(Map<Curse,CurseData>)` | before 261002 | ✔ |
| `addCurse(Curse, int, int)` | before 260817 | ✔ |
| `addCurse(Curse, CurseData)` | before 260817 | ✔ |
| `addCurses(Map<Curse,CurseData>)` | before 260817 | ✔ |
| `clearAndPutCurses(Map<Curse,CurseData>)` | before 260817 | ✔ |
| `clearCurses()` | on 260817 | ✔ |
| `setCursePower(Curse, int)` | on 260817 | ✔ |
| `removeCurse(Curse)` | on 260817 | ✔ |
| `rechargeTimeout()` | before 261002 | ✔ |
| `numberCharging()` | before 261002 | ✔ |
| `getNumber()` | before 260904 | ✔ |
| `setNumber(int)` | before 260904 | ✔ |
| `gettValue()` | before 260904 | ✔ |
| `settValue(TValue)` | before 260904 | ✔ |
| `getTimeout()` | before 260904 | ✔ |
| `getToHit()` | on 260815 | ✔ |
| `getToDam()` | on 260815 | ✔ |
| `getToAC()` | on 260815 | ✔ |
| `isKnown()` | before 261002 | ✔ |
| `hasStandardToH()` | on 260815 | ✔ |
| `hasFlag(ObjectFlag)` | on 260815 | ✔ |
| `description(Flag<ObjectDescription>, Player)` | on 260815 | ✔ |
| `flagMessage(ObjectFlag, String)` | on 260815 | ✔ |
| `getFlags()` | — | ✔ |
| `objectFlags(Flag<ObjectFlag>)` | on 260829 | ✔ |
| `getKnown()` | — | ✔ |
| `getNotice()` | — | ✔ |
| `getTime()` | — | ✔ |
| `setTime(Random)` | before 260904 | ✔ |
| `setNoticeOn(ObjectNotice)` | before 261003 | ✔ |
| `setTimeout(int)` | before 260904 | ✔ |
| `getNote()` | before 260904 | ✔ |
| `setNoticeOff(ObjectNotice)` | before 261003 | ✔ |
| `getsValue()` | before 260904 | ✔ |
| `setsValue(int)` | before 260904 | ✔ |
| `getWeight()` | before 260904 | ✔ |
| `setWeight(int)` | before 260904 | ✔ |
| `getDamageDice()` | before 260904 | ✔ |
| `setDamageDice(int)` | before 260904 | ✔ |
| `getDamageSides()` | before 260904 | ✔ |
| `setDamageSides(int)` | before 260904 | ✔ |
| `getBaseAC()` | before 260904 | ✔ |
| `setBaseAC(int)` | before 260904 | ✔ |
| `setToHit(int)` | before 260904 | ✔ |
| `getpValue()` | before 260904 | ✔ |
| `setpValue(int)` | before 260904 | ✔ |
| `setToAC(int)` | before 260904 | ✔ |
| `setToDam(int)` | before 260904 | ✔ |
| `getModifiers()` | — | ✔ |
| `setModifiers(Map<ObjectModifier,Integer>)` | before 260904 | ✔ |
| `getElInfo()` | — | ✔ |
| `setElInfo(Map<ElementEnum,ElementInfo>)` | before 260904 | ✔ |
| `setElInfoResLevel(ElementEnum, int)` | — | ✔ |
| `putElInfo(ElementEnum, ElementInfo)` | on 260817 | ✔ |
| `putModifier(ObjectModifier, int)` | — | ✔ |
| `getEffect()` | — | ✔ |
| `setEffect(List<Effect>)` | before 260904 | ✔ |
| `getNoticeHas(ObjectNotice)` | before 261003 | ✔ |
| `setSlays(Set<Slay>)` | before 260904 | ✔ |
| `getEgo()` | — | ✔ |
| `setEgo(EgoItem)` | before 260904 | ✔ |
| `setFlags(Flag<ObjectFlag>)` | on 260816 | ✔ |
| `setFlag(ObjectFlag)` | on 260816 | ✔ |
| `setFlagsTo(Flag<ObjectFlag>)` | on 260816 | ✔ |
| `appendBrand(Brand)` | on 260817 | ✔ |
| `removeBrand(Brand)` | on 260817 | ✔ |
| `clearBrands()` | on 260817 | ✔ |
| `appendSlay(Slay)` | on 260817 | ✔ |
| `removeSlay(Slay)` | on 260817 | ✔ |
| `clearSlays()` | on 260817 | ✔ |
| `flavourIsAware()` | — | ✔ |
| `easyKnow()` | — | ✔ |
| `flagsKnown()` | — | ✔ |
| `getModifierValue(Stats)` | — | ✔ |
| `getModifierValue(ObjectModifier)` | — | ✔ |
| `objectWeightOne()` | on 260820 | ✔ |
| `checkForInscription(String)` | before 260822 | ✔ |
| `verifyObject(String, Player)` | before 260822 | ✔ |
| `setNote(String)` | before 260822 | ✔ |
| `getIgnoreTypeOf()` | on 260822 | ✔ |
| `isEgo()` | before 261002 | ✔ |
| `egoIsIgnored(IgnoreType)` | — | ✔ |
| `ignoreLevelOf()` | on 260822 | ✔ |
| `isGood()` | — | ✔ |
| `compareObjectTrait(int, Random)` | — | ✔ |
| `objectIsInQuiver(Player)` | before 260827 | ✔ |
| `mergeable(ItemObject, Flag<ObjectStackEnum>)` | on 260822 | ✔ |
| `objectStackable(ItemObject, Flag<ObjectStackEnum>)` | on 260822 | ✔ |
| `objectAbsorb(ItemObject)` | on 260822 | ✔ |
| `getBrands()` | before 260817 | ✔ |
| `nullKnown()` | before 260827 | ✔ |
| `getSlays()` | — | ✔ |
| `objectSplit(int)` | before 260827 | ✔ |
| `objectAbsorbMerge(ItemObject, Player, boolean)` | on 260822 | ✔ |
| `objectValue(int)` | before 260827 | ✔ |
| `objectValueBase()` | before 260827 | ✔ |
| `objectValueReal(int)` | before 260827 | ✔ |
| `objectPower(boolean, String)` | — | ✔ |
| `nonStandardWeightPower(int)` | — | ✔ |
| `cursePower(int, boolean, String)` | — | ✔ |
| `freeSlays()` | — | ✔ |
| `freeBrands()` | — | ✔ |
| `objectAbsorbPartial(ItemObject, Flag<ObjectStackEnum>, Flag<ObjectStackEnum>)` | on 260822 | ✔ |
| `applyCurseAttributes(Curse)` | before 261007 | ✔ |
| `effectsPower(int)` | before 260827 | ✔ |
| `elementPower(int)` | before 260827 | ✔ |
| `flagsPower(int)` | before 260827 | ✔ |
| `modifierPower(int)` | before 260827 | ✔ |
| `jewelleryPower(int)` | before 260827 | ✔ |
| `toAcPower(int)` | before 260827 | ✔ |
| `acPower(int)` | before 260827 | ✔ |
| `toHitPower(int)` | before 260827 | ✔ |
| `rescaleBowPower(int)` | before 260827 | ✔ |
| `slayPower(int, boolean, int)` | before 260827 | ✔ |
| `extraMightPower(PowerAndMult)` | before 260827 | ✔ |
| `extraShotsPower(int)` | before 260827 | ✔ |
| `extraBlowsPower(int)` | before 260827 | ✔ |
| `launcherAmmoDamagePower(int)` | before 260827 | ✔ |
| `bowMultiplier()` | before 260827 | ✔ |
| `ammoDamagePower(int)` | before 260827 | ✔ |
| `damageDicePower()` | before 260827 | ✔ |
| `toDamagePower()` | before 260827 | ✔ |
| `wieldSlot()` | — | ✔ |
| `objectFlavourIsAware()` | — | ✔ |
| `canBrowse()` | — | ✔ |
| `copy(boolean)` | before 260827 | ✔ |
| `objectKindName(ObjectKind, boolean)` | — | ✔ |
| `objDescNameFormat(String, String, boolean)` | — | ✔ |
| `getObjectFlags()` | before 260904 | ✔ |
| `freeCurses()` | — | ✔ |
| `printCustomMessage(String, MessageType, Player, boolean)` | — | ✔ |
| `wipe()` | before 260904 | ✔ |
| `initCurses()` | before 260904 | ✔ |
| `setOrigin(ObjectOriginEnum)` | before 260904 | ✔ |
| `setKnown(ItemObject)` | before 260904 | ✔ |
| `getOwningPile()` | before 260904 | ✔ |
| `setOwningPile(Pile)` | before 260904 | ✔ |
| `getMimickingMIndex()` | before 261002 | ✔ |
| `getItemObjectADC()` | on 260927 | ✔ |
| `objectKindChar()` | on 260927 | ✔ |
| `objectKindAttr()` | on 260927 | ✔ |
| `useFlavourGlyph()` | on 260927 | ✔ |
| `setMimickingMIndex(int)` | before 260904 | ✔ |
| `modMessage(ObjectModifier)` | on 261004 | ✔ |
| `setEffectMessage(String)` | before 261007 | ✔ |

**record `ItemObject.PowerAndMult`** — class Javadoc: ✔ · type stamp: before 260827

- Record components (via `@param`) (2/2 documented): ✔ `power`, `mult`


##### `ItemSource.java`

**Responsible for:** The BonusSource view of a worn ItemObject — the first pass of calcBonuses' equipment walk, C's obj = slot_object(p, i) (player-calcs.c:1927).

**class `ItemSource`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (1/1 documented): ✔ `item`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ItemSource(ItemObject)` | — | ✔ |
| `flags()` | — | ✔ |
| `modifier(ObjectModifier)` | — | ✔ |
| `resLevel(ElementEnum)` | — | ✔ |
| `knownResLevel(ElementEnum)` | — | ✔ |
| `baseAC()` | — | ✔ |
| `toAC()` | — | ✔ |
| `knownToAC()` | — | ✔ |
| `toHit()` | — | ✔ |
| `knownToHit()` | — | ✔ |
| `toDam()` | — | ✔ |
| `knownToDam()` | — | ✔ |
| `isDigger()` | — | ✔ |
| `flagsKnown()` | — | ✔ |
| `flagSet(ObjectFlag)` | — | ✔ |


##### `KnownObject.java`

**Responsible for:** Everything the player has learned about object properties in general — which runes they can read.

**class `KnownObject`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (12/12 documented): ✔ `modifierFlag`, `objectFlags`, `elementResistInfo`, `toH`, `toD`, `toA`, `curses`, `brands`, `slays`, `ac`, `dd`, `ds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `KnownObject()` | before 261009 | ✔ |
| `initCurses()` | before 261009 | ✔ |
| `curseIsKnown(Curse)` | before 261009 | ✔ |
| `learnCurse(Curse)` | before 261009 | ✔ |
| `initToValues()` | before 261009 | ✔ |
| `toHIsKnown()` | before 261009 | ✔ |
| `learnToH()` | before 261009 | ✔ |
| `toDIsKnown()` | before 261009 | ✔ |
| `learnToD()` | before 261009 | ✔ |
| `toAIsKnown()` | before 261009 | ✔ |
| `learnToA()` | before 261009 | ✔ |
| `initResistances()` | before 261009 | ✔ |
| `resistanceIsKnown(ElementEnum)` | before 261009 | ✔ |
| `learnResistance(ElementEnum)` | before 261009 | ✔ |
| `initObjectFlags()` | before 261009 | ✔ |
| `flagIsKnown(ObjectFlag)` | before 261009 | ✔ |
| `learnFlag(ObjectFlag)` | before 261009 | ✔ |
| `getFlags()` | before 261009 | ✔ |
| `initModifiers()` | before 261009 | ✔ |
| `modifierIsKnown(ObjectModifier)` | before 261009 | ✔ |
| `learnModifier(ObjectModifier)` | before 261009 | ✔ |
| `brandIsKnown(Brand)` | before 261009 | ✔ |
| `learnBrand(Brand)` | before 261009 | ✔ |
| `initBrands()` | before 261009 | ✔ |
| `slayIsKnown(Slay)` | before 261009 | ✔ |
| `learnSlay(Slay)` | before 261009 | ✔ |
| `initSlays()` | before 261009 | ✔ |
| `getAc()` | before 261009 | ✔ |
| `getDd()` | before 261009 | ✔ |
| `getDs()` | before 261009 | ✔ |
| `getToH()` | before 261009 | ✔ |
| `getToD()` | before 261009 | ✔ |
| `getElementResistInfo()` | before 260816 | ✔ |
| `getToA()` | before 261009 | ✔ |
| `setDD(int)` | before 260904 | ✔ |
| `setDS(int)` | before 260904 | ✔ |
| `setAC(int)` | before 260904 | ✔ |
| `setToH(int)` | before 260908 | ✔ |
| `setToD(int)` | before 260908 | ✔ |
| `setToA(int)` | before 260908 | ✔ |
| `objectElementIsKnown(Player, ItemObject, ElementEnum)` | before 260924 | ✔ |


##### `ObjectBase.java`

**Responsible for:** The base type of a family of object kinds (as loaded from object_base.txt) — e.g.

**class `ObjectBase`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (9/9 documented): ✔ `name`, `tVal`, `attr`, `elementInfo`, `flags`, `kindFlags`, `breakPerc`, `maxStack`, `numSvals`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectBase(TValue, String, ColourEnum, Flag<ObjectKindFlag>, Flag<ElementEnum>, int, int)` | before 261009 | ✔ |
| `setElementInfo(Map<ElementEnum,ElementInfo>)` | before 261009 | ✔ |
| `getNumSvals()` | before 261009 | ✔ |
| `setNumSvals(int)` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |
| `getElementMap()` | before 261009 | ✔ |
| `gettVal()` | before 261009 | ✔ |
| `getAttr()` | before 261009 | ✔ |
| `getBreakPerc()` | before 261009 | ✔ |
| `getMaxStack()` | before 261009 | ✔ |
| `getKindFlags()` | before 261009 | ✔ |
| `getFlags()` | before 261009 | ✔ |
| `toString()` | before 261009 | ✔ |


##### `ObjectGear.java`

**Responsible for:** Static helpers for the player's gear list - the pack, quiver and equipment held in Player#getGear() - ported from obj-gear.c.

**class `ObjectGear`** — class Javadoc: ✔ · type stamp: before 260905

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `invenCarry(Player, ItemObject, boolean, boolean)` | before 260905 | ✔ |
| `objectPackTotal(Player, ItemObject, boolean)` | before 260905 | ✔ |

**record `ObjectGear.ItemObjectAndInt`** — class Javadoc: ✔ · type stamp: before 260905

- Record components (via `@param`) (2/2 documented): ✔ `obj`, `number`


##### `ObjectIgnore.java`

**Responsible for:** The ignore and autoinscription half of the object subsystem - the port of the functions in C's obj-ignore.c that decide whether an object is hidden, act on that decision, and put inscriptions on objects.

**class `ObjectIgnore`** — class Javadoc: ✔ · type stamp: on 260822

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ignoreKnownItemOk(ItemObject)` | — | ✔ |
| `ignoreDrop(Player)` | on 260822 | ✔ |
| `ignoreItemOK(Player, ItemObject)` | on 260822 | ✔ |
| `isIgnored(ItemObject)` | on 260822 | ✔ |
| `applyAutoinscription(Player, ItemObject)` | on 260905 | ✔ |
| `runesAutoinscribe(Player, ItemObject)` | on 260905 | ✔ |
| `runeAddAutoinscription(ItemObject, Rune)` | on 260905 | ✔ |
| `getAutoinscription(ObjectKind, boolean)` | on 260905 | ✔ |
| `kindIsIgnoredUnaware(ObjectKind)` | on 260905 | ✔ |
| `kindIgnoreWhenAware(ObjectKind, Player)` | on 260905 | ✔ |
| `autoinscribePack(Player)` | on 260905 | ✔ |
| `autoinscribeGround(Player)` | on 260905 | ✔ |
| `ignoreBirthInit()` | on 260907 | ✔ |


##### `ObjectIgnoreTables.java`

**Responsible for:** Static tables for the object-ignore machinery, the Java home of the data C keeps at the top of obj-ignore.c.

**class `ObjectIgnoreTables`** — class Javadoc: ✔ · type stamp: before 261008

- Fields (2/2 documented): ✔ `qualityMapping`, `ignoreLevel`

**record `ObjectIgnoreTables.QualityMapping`** — class Javadoc: ✔ · type stamp: before 261008

- Record components (via `@param`) (3/3 documented): ✔ `ignoreType`, `tval`, `identifier`


##### `ObjectKind.java`

**Responsible for:** The template for a kind of object (as loaded from object.txt) — e.g.

**class `ObjectKind`** — class Javadoc: ✔ · type stamp: before 261008

- Fields (45/46 documented): ✔ `name`, `text`, `base`, `kindIndex`, `tValue`, `sValueName`, `sVal`, `toH`, `toD`, `toA`, `ac`, `baseDamage`, `damageDice`, `damageSides`, `weight`, `cost`, `flags`, `kindFlags`, `modifiers`, `elInfo`, `brands`, `slays`, `curses`, `character`, `alloc_prob`, `alloc_min`, `alloc_max`, `level`, `activations`, `effect`, `power`, `effectMessage`, `visMessage`, `charge`, `genMultProb`, `stackSize`, `flavour`, `noteAware`, `noteUnaware`, `aware`, `tried`, `ignore`, `everseen`, `isSpecialArtifactKind`, `time` — ✘ `pVal`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectKind()` | before 261008 | ✔ |
| ctor `ObjectKind(AngbandDisplayCharacter, int, int, int, int, String, TValue, String, ObjectBase, boolean)` | before 261008 | ✔ |
| ctor `ObjectKind(String, String, ObjectBase, int, Random, Random, Random, Random, int, Random, int, int, int, int, Flag<ObjectFlag>, Flag<ObjectKindFlag>, Map<ObjectModifier,Random>, Map<ElementEnum,ElementInfo>, Set<Brand>, Set<Slay>, Map<Curse,CurseData>, AngbandDisplayCharacter, int, int, int, int, List<Activation>, List<Effect>, String, String, Random, Random, int, Random, Flavour, String, String, boolean, boolean, Flag<IgnoreFlag>, boolean, TValue, int)` | before 261008 | ✔ |
| ctor `ObjectKind(Artifact, String, ObjectBase)` | before 261008 | ✔ |
| `setCharacter(AngbandDisplayCharacter)` | before 261008 | ✔ |
| `stripToRawSval(String)` | — | ✔ |
| `getsVal()` | — | ✔ |
| `setsVal(int)` | — | ✔ |
| `getName()` | — | ✔ |
| `getBase()` | — | ✔ |
| `setAlloc_prob(int)` | — | ✔ |
| `setAlloc_min(int)` | — | ✔ |
| `setAlloc_max(int)` | — | ✔ |
| `getActivations()` | — | ✔ |
| `setTime(Random)` | — | ✔ |
| `getsValueName()` | — | ✔ |
| `getKindFlags()` | — | ✔ |
| `gettValue()` | — | ✔ |
| `getKindIndex()` | — | ✔ |
| `setKindIndex(int)` | — | ✔ |
| `getToH()` | on 260815 | ✔ |
| `getAc()` | — | ✔ |
| `isAware()` | — | ✔ |
| `getToD()` | — | ✔ |
| `setAware(boolean)` | — | ✔ |
| `getFlavour()` | — | ✔ |
| `getEffect()` | — | ✔ |
| `setIgnoredAware(boolean)` | — | ✔ |
| `isSpecialArtifactKind()` | — | ✔ |
| `isEverseen()` | — | ✔ |
| `isIgnoredUnaware()` | — | ✔ |
| `setIgnoredUnaware(boolean)` | — | ✔ |
| `isIgnoredAware()` | — | ✔ |
| `getFlags()` | — | ✔ |
| `getToA()` | — | ✔ |
| `getDamageDice()` | on 260816 | ✔ |
| `getDamageSides()` | on 260816 | ✔ |
| `copy()` | — | ✔ |
| `getPower()` | — | ✔ |
| `getCost()` | — | ✔ |
| `canBrowse()` | before 260827 | ✔ |
| `setCost(int)` | — | ✔ |
| `getWeight()` | — | ✔ |
| `setTried(boolean)` | — | ✔ |
| `setWeight(int)` | — | ✔ |
| `getTime()` | — | ✔ |
| `getModifier(ObjectModifier)` | — | ✔ |
| `getCharge()` | — | ✔ |
| `getSlays()` | — | ✔ |
| `getBrands()` | — | ✔ |
| `getCurses()` | — | ✔ |
| `getElInfo(ElementEnum)` | — | ✔ |
| `getPVal()` | — | ✔ |
| `hasFlavour()` | before 260904 | ✔ |
| `getNoteAware()` | on 260905 | ✔ |
| `getNoteUnaware()` | on 260905 | ✔ |
| `hasIgnoreFlag(IgnoreFlag)` | on 260905 | ✔ |
| `setEverSeen(boolean)` | — | ✔ |
| `setIgnoreFlag(IgnoreFlag)` | — | ✔ |
| `wipeIgnoreFlags()` | on 260907 | ✔ |
| `setFlavour(Flavour)` | before 260908 | ✔ |
| `getCharacter()` | before 260904 | ✔ |


##### `ObjectKnowledge.java`

**Responsible for:** Questions about what an individual item carries and what handling it can teach, ported from the object half of C's obj-knowledge.c: #objectHasRune (does this item have the property a rune stands for) and #objectFlavourTried (mark an item's kind as tried).

**class `ObjectKnowledge`** — class Javadoc: ✔ · type stamp: before 260905

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `objectHasRune(ItemObject, Rune)` | before 260905 | ✔ |
| `objectFlavourTried(ItemObject)` | before 260905 | ✔ |


##### `ObjectMake.java`

**Responsible for:** Stub home for object creation; currently only `makeFakeArtifact`, which returns false pending its port.

**class `ObjectMake`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `makeFakeArtifact(ItemObject, Artifact)` | — | ✘ |


##### `ObjectProperty.java`

**Responsible for:** A generic object property definition (as loaded from object_property.txt) — describing one property an object can have (a flag, stat/modifier, or element relation, captured by ObjectPropertyTypeWrapper) along with its power, value multipliers, the adjectives/messages used to describe it, and the ...

**class `ObjectProperty`** — class Javadoc: ✔ · type stamp: before 260827

- Fields (14/14 documented): ✔ `logger`, `type`, `subtype`, `idType`, `payload`, `power`, `mult`, `typeMults`, `name`, `adjective`, `negAdjective`, `message`, `description`, `boundEntries`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectProperty(ObjPropertyType, ObjectFlagType, ObjectFlagID, ObjectPropertyTypeWrapper, int, int, Map<TValue,Integer>, String, String, String, String, String, List<UIBinding>)` | before 260827 | ✔ |
| `getType()` | before 260827 | ✔ |
| `getSubtype()` | before 260827 | ✔ |
| `getPayload()` | before 260827 | ✔ |
| `getName()` | before 260827 | ✔ |
| `getNoticeMessage()` | before 260827 | ✔ |
| `getMultiplier()` | before 260827 | ✔ |
| `getPower()` | before 260827 | ✔ |
| `getBoundEntries()` | before 260919 | ✔ |
| `getTypeMult(TValue)` | before 260827 | ✔ |
| `getIdType()` | before 260827 | ✔ |

**record `ObjectProperty.UIBinding`** — class Javadoc: ✔ · type stamp: before 260919

- Record components (via `@param`) (3/3 documented): ✔ `entry`, `value`, `aux`


##### `ObjectPropertyTypeWrapper.java`

**Responsible for:** The typed form of C's obj_property->index.

**class `ObjectPropertyTypeWrapper`** — class Javadoc: ✔ · type stamp: before 260827

- Fields (5/5 documented): ✔ `logger`, `type`, `flag`, `modifier`, `element`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectPropertyTypeWrapper(ObjPropertyType, ObjectModifier)` | before 260827 | ✔ |
| ctor `ObjectPropertyTypeWrapper(ObjPropertyType, ObjectFlag)` | before 260827 | ✔ |
| ctor `ObjectPropertyTypeWrapper(ObjPropertyType, ElementEnum)` | before 260827 | ✔ |
| `getType()` | before 260827 | ✔ |
| `getModifier(ObjPropertyType)` | before 260827 | ✔ |
| `getElement(ObjPropertyType)` | before 260827 | ✔ |
| `getFlag(ObjPropertyType)` | before 260827 | ✔ |
| `equals(Object)` | before 260827 | ✔ |


##### `ObjectRandart.java`

**Responsible for:** The random-artifact generator - the port of C's obj-randart.c.

**class `ObjectRandart`** — class Javadoc: ✔ · type stamp: on 260908

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doRandart(long, boolean)` | — | ✔ |
| `storeBasePower(ArtifactSetData, String)` | — | ✔ |
| `artifactPower(String, Artifact, String, boolean)` | — | ✔ |
| `artifactSetDataNew()` | on 260908 | ✔ |


##### `ObjectUtils.java`

**Responsible for:** Free-standing helper routines for the object/inventory subsystem — the port's landing spot for the gear-management corners of C's obj-gear.c, the kind-lookup, flavour and artifact-marker helpers of obj-util.c, object_prep from obj-make.c, the slay, brand and curse merges of obj-slays.c and obj-cu...

**class `ObjectUtils`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `logger`, `MAX_TITLES`, `maxTitleLength`, `scrollAdj`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `packOverflow(ItemObject)` | before 261005 | ✔ |
| `equipLearnAfterTime(Player)` | before 261005 | ✔ |
| `objectDesc(ItemObject, Flag<ObjectDescription>, Player)` | before 261005 | ✔ |
| `doCurseEffect(Curse, ItemObject)` | before 260817 | ✔ |
| `combinePack(Player)` | on 260822 | ✔ |
| `invenCanStackPartial(Player, ItemObject, ItemObject, Flag<ObjectStackEnum>, Flag<ObjectStackEnum>)` | on 260822 | ✔ |
| `quiverAbsorbNum(Player, ItemObject, SplitBetweenPackAndQuiver)` | on 260822 | ✔ |
| `preferredQuiverSlot(Player, ItemObject)` | on 260822 | ✔ |
| `packSlotsUsed(Player)` | on 260822 | ✔ |
| `gearInsertEnd(Player, ItemObject)` | before 261005 | ✔ |
| `slotByType(Player, EquipmentSlotsEnum, boolean)` | — | ✔ |
| `slotByName(Player, String)` | before 261005 | ✔ |
| `slotByNumber(Player, int)` | — | ✔ |
| `numberFromSlot(Player, EquipSlot)` | — | ✔ |
| `gearToLabel(Player, ItemObject)` | before 260817 | ✔ |
| `isCarried(Player, ItemObject)` | on 260816 | ✔ |
| `markArtifactCreated(Artifact, boolean)` | on 260903 | ✔ |
| `markArtifactSeen(Artifact, boolean)` | on 260903 | ✔ |
| `lookupKind(TValue, int)` | on 260904 | ✔ |
| `lookupKind(TValue, String)` | on 260904 | ✔ |
| `objectPrep(ItemObject, ObjectKind, int, DamageAspect)` | before 260904 | ✔ |
| `copyCurses(ItemObject, Map<Curse,CurseData>)` | before 260904 | ✔ |
| `copyBrands(Set<Brand>, Set<Brand>)` | before 260904 | ✔ |
| `copySlays(Set<Slay>, Set<Slay>)` | before 260904 | ✔ |
| `flavourInit()` | on 260908 | ✔ |
| `flavourResetFixed()` | on 260908 | ✔ |
| `flavourAssignFixed()` | before 260908 | ✔ |
| `flavourAssignRandom(TValue)` | before 260908 | ✔ |
| `flavourSetAllAware()` | before 260908 | ✔ |
| `objectFlagIsKnown(Player, ItemObject, ObjectFlag)` | before 260924 | ✔ |
| `curseObjectFlagIsKnown(Player, Curse, ObjectFlag)` | before 260924 | ✔ |
| `objectElementIsKnown(Player, Curse, ElementEnum)` | before 260924 | ✔ |
| `objIsKnownArtifact(ItemObject)` | on 261008 | ✔ |

**record `ObjectUtils.SplitBetweenPackAndQuiver`** — class Javadoc: ✔ · type stamp: on 260822

- Record components (via `@param`) (2/2 documented): ✔ `numToQuiver`, `noToPack`


##### `Pile.java`

**Responsible for:** A pile of objects: the port of the doubly linked list of struct object that C threads through obj->prev and obj->next and passes around as a struct object * head pointer (obj-pile.c).

**class `Pile`** — class Javadoc: ✔ · type stamp: before 260905

- Fields (2/2 documented): ✔ `logger`, `pile`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Pile()` | before 260905 | ✔ |
| `peek()` | before 260905 | ✔ |
| `isEmpty()` | before 260905 | ✔ |
| `push(ItemObject)` | before 260905 | ✔ |
| `insert(ItemObject)` | — | ✔ |
| `insertEnd(ItemObject)` | before 260905 | ✔ |
| `insertEnd(Pile)` | — | ✔ |
| `hasArtifact()` | before 260905 | ✔ |
| `excise(ItemObject)` | before 260905 | ✔ |
| `lastItem()` | before 260905 | ✔ |
| `contains(ItemObject)` | before 260905 | ✔ |
| `getIterator()` | before 260905 | ✔ |
| `clear()` | before 260905 | ✔ |
| `size()` | before 260905 | ✔ |
| `get(int)` | before 260905 | ✔ |
| `reversed()` | before 260905 | ✔ |
| `removeIf(ItemObject)` | before 260905 | ✔ |
| `remove(int)` | before 260905 | ✔ |
| `peekLastItem()` | before 260905 | ✔ |


##### `Rune.java`

**Responsible for:** One rune in the rune-based identification system — a single object property the player can learn, after which every object carrying that property is known to carry it.

**class `Rune`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (3/3 documented): ✔ `logger`, `variety`, `note`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Rune(RuneVariety)` | before 261010 | ✔ |
| `initRunes()` | — | ✘ |
| `getNote()` | before 261010 | ✔ |
| `setNote(String)` | before 261010 | ✔ |
| `getVariety()` | before 261010 | ✔ |
| `runeIndex(CombatRunes)` | before 261010 | ✔ |
| `runeIndex(ObjectModifier)` | before 261010 | ✔ |
| `runeIndex(ElementEnum)` | before 261010 | ✔ |
| `runeIndex(Brand)` | before 261010 | ✔ |
| `runeIndex(Slay)` | before 261010 | ✔ |
| `runeIndex(Curse)` | before 261010 | ✔ |
| `runeIndex(ObjectFlag)` | before 261010 | ✔ |


##### `Slay.java`

**Responsible for:** One entry of slay.txt: a bonus-damage multiplier that a weapon or launcher applies against a class of monster, with the verbs used when it triggers and a power rating for object valuation.

**class `Slay`** — class Javadoc: ✔ · type stamp: before 261005

- Fields (11/11 documented): ✔ `code`, `monsterType`, `monsterLevel`, `name`, `base`, `meleeVerb`, `rangedVerb`, `raceFlag`, `multiplier`, `oMultiplier`, `power`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Slay(String, String, MonsterBase, String, String, MonsterRaceFlag, int, int, int)` | before 261005 | ✔ |
| `getCode()` | before 261005 | ✔ |
| `getName()` | before 261005 | ✔ |
| `toString()` | before 261005 | ✔ |
| `sameMonsterSlain(Slay)` | before 261005 | ✔ |
| `getMultiplier()` | before 261005 | ✔ |
| `getPower()` | before 261005 | ✔ |
| `copy()` | before 261003 | ✔ |
| `equals(Object)` | before 261003 | ✔ |
| `hashCode()` | before 261003 | ✔ |


## middle/objects/enums

##### `ArtifactIndex.java`

**Responsible for:** The learned-probability slots used by random artifact generation.

**enum `ArtifactIndex`** — class Javadoc: ✔ · type stamp: before 261002

- Enum constants (95/95 documented): ✔ `ART_IDX_BOW_SHOTS`, `ART_IDX_BOW_MIGHT`, `ART_IDX_BOW_BRAND`, `ART_IDX_BOW_SLAY`, `ART_IDX_WEAPON_HIT`, `ART_IDX_WEAPON_DAM`, `ART_IDX_NONWEAPON_HIT`, `ART_IDX_NONWEAPON_DAM`, `ART_IDX_NONWEAPON_HIT_DAM`, `ART_IDX_NONWEAPON_BRAND`, `ART_IDX_NONWEAPON_SLAY`, `ART_IDX_NONWEAPON_BLOWS`, `ART_IDX_NONWEAPON_SHOTS`, `ART_IDX_MELEE_BLESS`, `ART_IDX_MELEE_BRAND`, `ART_IDX_MELEE_SLAY`, `ART_IDX_MELEE_SINV`, `ART_IDX_MELEE_BLOWS`, `ART_IDX_MELEE_AC`, `ART_IDX_MELEE_DICE`, `ART_IDX_MELEE_WEIGHT`, `ART_IDX_MELEE_TUNN`, `ART_IDX_ALLARMOR_WEIGHT`, `ART_IDX_BOOT_AC`, `ART_IDX_BOOT_FEATHER`, `ART_IDX_BOOT_STEALTH`, `ART_IDX_BOOT_TRAP_IMM`, `ART_IDX_BOOT_SPEED`, `ART_IDX_BOOT_MOVES`, `ART_IDX_GLOVE_AC`, `ART_IDX_GLOVE_FA`, `ART_IDX_GLOVE_DEX`, `ART_IDX_GLOVE_HIT_DAM`, `ART_IDX_HELM_AC`, `ART_IDX_HELM_RBLIND`, `ART_IDX_HELM_ESP`, `ART_IDX_HELM_SINV`, `ART_IDX_HELM_WIS`, `ART_IDX_HELM_INT`, `ART_IDX_SHIELD_AC`, `ART_IDX_SHIELD_LRES`, `ART_IDX_CLOAK_AC`, `ART_IDX_CLOAK_STEALTH`, `ART_IDX_ARMOR_AC`, `ART_IDX_ARMOR_STEALTH`, `ART_IDX_ARMOR_HLIFE`, `ART_IDX_ARMOR_CON`, `ART_IDX_ARMOR_LRES`, `ART_IDX_ARMOR_ALLRES`, `ART_IDX_ARMOR_HRES`, `ART_IDX_GEN_STAT`, `ART_IDX_GEN_SUST`, `ART_IDX_GEN_STEALTH`, `ART_IDX_GEN_SEARCH`, `ART_IDX_GEN_INFRA`, `ART_IDX_GEN_SPEED`, `ART_IDX_GEN_IMMUNE`, `ART_IDX_GEN_FA`, `ART_IDX_GEN_HLIFE`, `ART_IDX_GEN_FEATHER`, `ART_IDX_GEN_LIGHT`, `ART_IDX_GEN_SINV`, `ART_IDX_GEN_ESP`, `ART_IDX_GEN_SDIG`, `ART_IDX_GEN_REGEN`, `ART_IDX_GEN_LRES`, `ART_IDX_GEN_RPOIS`, `ART_IDX_GEN_RFEAR`, `ART_IDX_GEN_RLIGHT`, `ART_IDX_GEN_RDARK`, `ART_IDX_GEN_RBLIND`, `ART_IDX_GEN_RCONF`, `ART_IDX_GEN_RSOUND`, `ART_IDX_GEN_RSHARD`, `ART_IDX_GEN_RNEXUS`, `ART_IDX_GEN_RNETHER`, `ART_IDX_GEN_RCHAOS`, `ART_IDX_GEN_RDISEN`, `ART_IDX_GEN_AC`, `ART_IDX_GEN_TUNN`, `ART_IDX_GEN_ACTIV`, `ART_IDX_GEN_PSTUN`, `ART_IDX_GEN_DAM_RED`, `ART_IDX_GEN_MOVES`, `ART_IDX_GEN_TRAP_IMM`, `ART_IDX_WEAPON_AGGR`, `ART_IDX_NONWEAPON_AGGR`, `ART_IDX_MELEE_DICE_SUPER`, `ART_IDX_BOW_SHOTS_SUPER`, `ART_IDX_BOW_MIGHT_SUPER`, `ART_IDX_GEN_SPEED_SUPER`, `ART_IDX_MELEE_BLOWS_SUPER`, `ART_IDX_MELEE_AC_SUPER`, `ART_IDX_GEN_AC_SUPER`, `ART_IDX_TOTAL`
- Fields (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ArtifactIndex(boolean)` | before 261002 | ✔ |


##### `ChestQuery.java`

**Responsible for:** The kind of chest a grid search is looking for — the port of C's enum chest_query (obj-chest.h:26-30).

**enum `ChestQuery`** — class Javadoc: ✔ · type stamp: —

- Enum constants (3/3 documented): ✔ `CHEST_ANY`, `CHEST_OPENABLE`, `CHEST_TRAPPED`


##### `ChestTrapCode.java`

**Responsible for:** The identity of each kind of chest trap, and through its position the bit that marks the trap in a chest's pval.

**enum `ChestTrapCode`** — class Javadoc: ✔ · type stamp: before 260815

- Enum constants (7/7 documented): ✔ `NO_TRAP`, `POISON`, `LOSE_STR`, `LOSE_CON`, `SUMMON`, `PARALYZE`, `EXPLODE`
- Fields (2/2 documented): ✔ `MAX_TRAPS`, `pval`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ChestTrapCode()` | before 260815 | ✔ |
| `getMaxTraps()` | before 260815 | ✔ |
| `getPval()` | before 260815 | ✔ |


##### `CombatRunes.java`

**Responsible for:** The three fixed combat runes — the enchantments to armour, to-hit and to-damage that any object can carry.

**enum `CombatRunes`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (4/4 documented): ✔ `COMBAT_RUNE_TO_A`, `COMBAT_RUNE_TO_H`, `COMBAT_RUNE_TO_D`, `COMBAT_RUNE_MAX`
- Fields (1/1 documented): ✔ `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CombatRunes(String)` | before 261009 | ✔ |
| `getDescription()` | before 261009 | ✔ |


##### `EquipmentSlotsEnum.java`

**Responsible for:** The kinds of equipment slot a player's body can have (weapon, bow, rings, amulet, light, armour pieces), each carrying whether items worn there need checking for acid damage, whether the slot's own name is mentioned when something happens to the item, and the phrases used to describe wearing or w...

**enum `EquipmentSlotsEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (13/13 documented): ✔ `EQUIP_NONE`, `EQUIP_WEAPON`, `EQUIP_BOW`, `EQUIP_RING`, `EQUIP_AMULET`, `EQUIP_LIGHT`, `EQUIP_BODY_ARMOR`, `EQUIP_CLOAK`, `EQUIP_SHIELD`, `EQUIP_HAT`, `EQUIP_GLOVES`, `EQUIP_BOOTS`, `EQUIP_MAX`
- Fields (5/5 documented): ✔ `acidResistant`, `mentionName`, `mentionString`, `heavyDescribe`, `describe`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EquipmentSlotsEnum(boolean, boolean, String, String, String)` | — | ✔ |


##### `GetItemFlags.java`

**Responsible for:** The mode bits a caller passes to get_item() to say where the item may come from and how the list is drawn.

**enum `GetItemFlags`** — class Javadoc: ✔ · type stamp: before 260815

- Enum constants (12/12 documented): ✔ `USE_EQUIP`, `USE_INVEN`, `USE_FLOOR`, `USE_QUIVER`, `IS_HARMLESS`, `SHOW_PRICES`, `SHOW_FAIL`, `SHOW_QUIVER`, `SHOW_EMPTY`, `QUIVER_TAGS`, `SHOW_RECHARGE`, `SHOW_THROWING`


##### `IgnoreFlag.java`

**Responsible for:** The two senses in which the player can choose to ignore an object kind — the port of C's IGNORE_IF_AWARE and IGNORE_IF_UNAWARE (obj-ignore.h), held on uk.co.jackoftradesltd.middle.objects.ObjectKind as a flag set rather than C's packed byte.

**enum `IgnoreFlag`** — class Javadoc: ✔ · type stamp: on 260816

- Enum constants (2/2 documented): ✔ `IGNORE_IF_AWARE`, `IGNORE_IF_UNAWARE`


##### `IgnoreType.java`

**Responsible for:** The kinds of item that quality and ego ignoring work by.

**enum `IgnoreType`** — class Javadoc: ✔ · type stamp: before 261002

- Enum constants (28/28 documented): ✔ `ITYPE_NONE`, `ITYPE_SHARP`, `ITYPE_BLUNT`, `ITYPE_GREAT`, `ITYPE_SLING`, `ITYPE_BOW`, `ITYPE_CROSSBOW`, `ITYPE_SHOT`, `ITYPE_ARROW`, `ITYPE_BOLT`, `ITYPE_ROBE`, `ITYPE_BODY_ARMOR`, `ITYPE_BASIC_DRAGON_ARMOR`, `ITYPE_MULTI_DRAGON_ARMOR`, `ITYPE_HIGH_DRAGON_ARMOR`, `ITYPE_BALANCE_DRAGON_ARMOR`, `ITYPE_POWER_DRAGON_ARMOR`, `ITYPE_CLOAK`, `ITYPE_ELVEN_CLOAK`, `ITYPE_SHIELD`, `ITYPE_HEADGEAR`, `ITYPE_HANDGEAR`, `ITYPE_FEET`, `ITYPE_DIGGER`, `ITYPE_RING`, `ITYPE_AMULET`, `ITYPE_LIGHT`, `ITYPE_MAX`
- Fields (1/1 documented): ✔ `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `IgnoreType(String)` | before 261002 | ✔ |
| `getName()` | before 261002 | ✔ |


##### `ObjPropertyType.java`

**Responsible for:** The categories an object property can belong to — a stat boost, a modifier, a flag, an ignore, a resistance, a vulnerability or an immunity.

**enum `ObjPropertyType`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (9/9 documented): ✔ `OBJ_PROPERTY_NONE`, `OBJ_PROPERTY_STAT`, `OBJ_PROPERTY_MOD`, `OBJ_PROPERTY_FLAG`, `OBJ_PROPERTY_IGNORE`, `OBJ_PROPERTY_RESIST`, `OBJ_PROPERTY_VULN`, `OBJ_PROPERTY_IMM`, `OBJ_PROPERTY_MAX`
- Fields (2/2 documented): ✔ `logger`, `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjPropertyType(String)` | before 261009 | ✔ |
| `fromValue(String)` | before 261009 | ✔ |
| `getValue()` | before 261009 | ✔ |


##### `ObjectDescription.java`

**Responsible for:** The mode bits a caller passes to object_desc() to say how much of an object's name to build.

**enum `ObjectDescription`** — class Javadoc: ✔ · type stamp: before 260815

- Enum constants (12/12 documented): ✔ `ODESC_BASE`, `ODESC_COMBAT`, `ODESC_EXTRA`, `ODESC_STORE`, `ODESC_PLURAL`, `ODESC_SINGULAR`, `ODESC_SPOIL`, `ODESC_PREFIX`, `ODESC_CAPITAL`, `ODESC_TERSE`, `ODESC_NOEGO`, `ODESC_ALTNUM`


##### `ObjectFlag.java`

**Responsible for:** The boolean property flags an object can carry (sustains, protections, telepathy, free action, light, digging, throwing, curses, …), each with the short label C uses in its object flag display.

**enum `ObjectFlag`** — class Javadoc: ✔ · type stamp: before 261005

- Enum constants (40/40 documented): ✔ `OF_NONE`, `OF_SUST_STR`, `OF_SUST_INT`, `OF_SUST_WIS`, `OF_SUST_DEX`, `OF_SUST_CON`, `OF_PROT_FEAR`, `OF_PROT_BLIND`, `OF_PROT_CONF`, `OF_PROT_STUN`, `OF_SLOW_DIGEST`, `OF_FEATHER`, `OF_REGEN`, `OF_TELEPATHY`, `OF_SEE_INVIS`, `OF_FREE_ACT`, `OF_HOLD_LIFE`, `OF_IMPACT`, `OF_BLESSED`, `OF_BURNS_OUT`, `OF_TAKES_FUEL`, `OF_NO_FUEL`, `OF_IMPAIR_HP`, `OF_IMPAIR_MANA`, `OF_AFRAID`, `OF_NO_TELEPORT`, `OF_AGGRAVATE`, `OF_DRAIN_EXP`, `OF_STICKY`, `OF_FRAGILE`, `OF_LIGHT_2`, `OF_LIGHT_3`, `OF_DIG_1`, `OF_DIG_2`, `OF_DIG_3`, `OF_EXPLODE`, `OF_TRAP_IMMUNE`, `OF_THROWING`, `OF_MULTIPLY_WEIGHT`, `OF_MAX`
- Fields (1/1 documented): ✔ `flag`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectFlag(String)` | before 261005 | ✔ |
| `getSustainStatFlag(SustainStat)` | before 261005 | ✔ |
| `getFlag()` | before 261005 | ✔ |


##### `ObjectFlagID.java`

**Responsible for:** How an object flag becomes known to the player — never, by normal use, after a timed delay, or immediately on wielding.

**enum `ObjectFlagID`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `OFID_NONE`, `OFID_NORMAL`, `OFID_TIMED`, `OFID_WIELD`
- Fields (1/1 documented): ✔ `idMethod`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectFlagID(String)` | — | ✔ |
| `getIdMethod()` | — | ✔ |
| `getFlagID(String)` | — | ✔ |


##### `ObjectFlagType.java`

**Responsible for:** The grouping of object flags by purpose — sustains, protections, light, melee, "bad" (negative) flags, digging, throwing, curse-only, etc.

**enum `ObjectFlagType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (11/11 documented): ✔ `OFT_NONE`, `OFT_SUST`, `OFT_PROT`, `OFT_MISC`, `OFT_LIGHT`, `OFT_MELEE`, `OFT_BAD`, `OFT_DIG`, `OFT_THROW`, `OFT_CURSE_ONLY`, `OFT_MAX`
- Fields (1/1 documented): ✔ `subtypeText`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectFlagType(String)` | — | ✔ |
| `getFlagTypeFromSubtype(String)` | — | ✔ |
| `getSubtypeText()` | — | ✔ |


##### `ObjectFloorEnum.java`

**Responsible for:** Filtering options for listing objects on the floor (test against a tester, sensed/known only, top item only, visible only).

**enum `ObjectFloorEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (5/5 documented): ✔ `OFLOOR_NONE`, `OFLOOR_TEST`, `OFLOOR_SENSE`, `OFLOOR_TOP`, `OFLOOR_VISIBLE`


##### `ObjectKindFlag.java`

**Responsible for:** Flags applied to an object kind (the template), to an ego item or to an artifact's kind — never to an individual object.

**enum `ObjectKindFlag`** — class Javadoc: ✔ · type stamp: before 261012

- Enum constants (16/16 documented): ✔ `KF_NONE`, `KF_RAND_HI_RES`, `KF_RAND_SUSTAIN`, `KF_RAND_POWER`, `KF_INSTA_ART`, `KF_QUEST_ART`, `KF_EASY_KNOW`, `KF_GOOD`, `KF_SHOW_DICE`, `KF_SHOW_MULT`, `KF_SHOOTS_SHOTS`, `KF_SHOOTS_ARROWS`, `KF_SHOOTS_BOLTS`, `KF_RAND_BASE_RES`, `KF_RAND_RES_POWER`, `KF_MAX`
- Fields (1/1 documented): ✔ `flag`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectKindFlag(String)` | before 261012 | ✔ |
| `getFlag()` | before 261012 | ✔ |


##### `ObjectModifier.java`

**Responsible for:** The numeric modifiers an object can grant: the five stat bonuses, then stealth, searching skill, infravision, tunnelling, speed, extra blows, shooting speed, shooting power, light radius, damage reduction and extra moves.

**enum `ObjectModifier`** — class Javadoc: ✔ · type stamp: before 261007

- Enum constants (18/18 documented): ✔ `OM_NONE`, `OM_STR`, `OM_INT`, `OM_WIS`, `OM_DEX`, `OM_CON`, `OM_STEALTH`, `OM_SEARCH`, `OM_INFRA`, `OM_TUNNEL`, `OM_SPEED`, `OM_BLOWS`, `OM_SHOTS`, `OM_MIGHT`, `OM_LIGHT`, `OM_DAM_RED`, `OM_MOVES`, `OM_MAX`


##### `ObjectNotice.java`

**Responsible for:** Per-object "notice" flags tracking what the player has learned/decided about an item — that it has been worn, assessed, marked to ignore, or is merely imagined (hallucinated).

**enum `ObjectNotice`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `OBJ_NOTICE_WORN`, `OBJ_NOTICE_ASSESSED`, `OBJ_NOTICE_IGNORE`, `OBJ_NOTICE_IMAGINED`


##### `ObjectOriginEnum.java`

**Responsible for:** Where an object came from, used to build the "history" line in an item's description (found on the floor, dropped by a monster, bought, an inheritance, …).

**enum `ObjectOriginEnum`** — class Javadoc: ✔ · type stamp: before 261014

- Enum constants (27/27 documented): ✔ `ORIGIN_NONE`, `ORIGIN_FLOOR`, `ORIGIN_CHEST`, `ORIGIN_SPECIAL`, `ORIGIN_PIT`, `ORIGIN_VAULT`, `ORIGIN_LABYRINTH`, `ORIGIN_CAVERN`, `ORIGIN_RUBBLE`, `ORIGIN_MIXED`, `ORIGIN_DROP`, `ORIGIN_DROP_SPECIAL`, `ORIGIN_DROP_PIT`, `ORIGIN_DROP_VAULT`, `ORIGIN_STATS`, `ORIGIN_ACQUIRE`, `ORIGIN_STORE`, `ORIGIN_STOLEN`, `ORIGIN_BIRTH`, `ORIGIN_CHEAT`, `ORIGIN_DROP_BREED`, `ORIGIN_DROP_SUMMON`, `ORIGIN_DROP_UNKNOWN`, `ORIGIN_DROP_POLY`, `ORIGIN_DROP_MIMIC`, `ORIGIN_DROP_WIZARD`, `ORIGIN_MAX`
- Fields (2/2 documented): ✔ `numParameters`, `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ObjectOriginEnum(int, String)` | before 261014 | ✔ |


##### `ObjectStackEnum.java`

**Responsible for:** The contexts in which objects stack, which governs the maximum stack size and stacking rules (store, pack/home, object list, monster inventory, floor, quiver).

**enum `ObjectStackEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (7/7 documented): ✔ `OSTACK_NONE`, `OSTACK_STORE`, `OSTACK_PACK`, `OSTACK_LIST`, `OSTACK_MONSTER`, `OSTACK_FLOOR`, `OSTACK_QUIVER`


##### `QualityValueEnum.java`

**Responsible for:** The quality bands that quality ignoring works by.

**enum `QualityValueEnum`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (6/6 documented): ✔ `IGNORE_NONE`, `IGNORE_BAD`, `IGNORE_AVERAGE`, `IGNORE_GOOD`, `IGNORE_ALL`, `IGNORE_MAX`


##### `ResType.java`

**Responsible for:** Which group of elements an element row belongs to when object power is priced — the port of the anonymous enum { T_LRES, T_HRES } in obj-power.c.

**enum `ResType`** — class Javadoc: ✔ · type stamp: on 261009

- Enum constants (2/2 documented): ✔ `T_LRES`, `T_HRES`


##### `RuneGroup.java`

**Responsible for:** The headings the knowledge menu groups runes under.

**enum `RuneGroup`** — class Javadoc: ✔ · type stamp: before 260814

- Enum constants (7/7 documented): ✔ `COMBAT`, `MODIFIERS`, `RESIST`, `BRAND`, `SLAY`, `CURSE`, `OTHER`
- Fields (1/1 documented): ✔ `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RuneGroup(String)` | before 260814 | ✔ |
| `getName()` | before 260814 | ✔ |


##### `RuneVariety.java`

**Responsible for:** What a uk.co.jackoftradesltd.middle.objects.Rune is a rune of — a tagged union over the seven kinds of object property the player can learn, each carrying the thing it refers to.

**interface `RuneVariety`** — class Javadoc: ✔ · type stamp: before 260814

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |

**record `RuneVariety.CombatKey`** — class Javadoc: ✔ · type stamp: before 260814

- Record components (via `@param`) (1/1 documented): ✔ `key`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |

**record `RuneVariety.ModKey`** — class Javadoc: ✔ · type stamp: before 260814

- Record components (via `@param`) (2/2 documented): ✔ `key`, `property`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |

**record `RuneVariety.ResistKey`** — class Javadoc: ✔ · type stamp: before 260814

- Record components (via `@param`) (2/2 documented): ✔ `key`, `projection`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |

**record `RuneVariety.BrandKey`** — class Javadoc: ✔ · type stamp: before 260814

- Record components (via `@param`) (1/1 documented): ✔ `key`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |

**record `RuneVariety.SlayKey`** — class Javadoc: ✔ · type stamp: before 260814

- Record components (via `@param`) (1/1 documented): ✔ `key`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |

**record `RuneVariety.CurseKey`** — class Javadoc: ✔ · type stamp: before 260814

- Record components (via `@param`) (1/1 documented): ✔ `key`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |

**record `RuneVariety.FlagKey`** — class Javadoc: ✔ · type stamp: before 260814

- Record components (via `@param`) (2/2 documented): ✔ `key`, `property`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group()` | before 260814 | ✔ |
| `runeName()` | before 260814 | ✔ |
| `runeDesc()` | before 260814 | ✔ |


##### `TValue.java`

**Responsible for:** The item "type value" (tval) of an object — its broad category (chest, weapon, armour, ring, potion, spellbook, …).

**enum `TValue`** — class Javadoc: ✔ · type stamp: before 260827

- Enum constants (36/36 documented): ✔ `TV_NONE`, `TV_CHEST`, `TV_SHOT`, `TV_ARROW`, `TV_BOLT`, `TV_BOW`, `TV_DIGGING`, `TV_HAFTED`, `TV_POLEARM`, `TV_SWORD`, `TV_BOOTS`, `TV_GLOVES`, `TV_HELM`, `TV_CROWN`, `TV_SHIELD`, `TV_CLOAK`, `TV_SOFT_ARMOR`, `TV_HARD_ARMOR`, `TV_DRAG_ARMOR`, `TV_LIGHT`, `TV_AMULET`, `TV_RING`, `TV_STAFF`, `TV_WAND`, `TV_ROD`, `TV_SCROLL`, `TV_POTION`, `TV_FLASK`, `TV_FOOD`, `TV_MUSHROOM`, `TV_MAGIC_BOOK`, `TV_PRAYER_BOOK`, `TV_NATURE_BOOK`, `TV_SHADOW_BOOK`, `TV_OTHER_BOOK`, `TV_GOLD`
- Fields (2/2 documented): ✔ `logger`, `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TValue(String)` | before 260827 | ✔ |
| `findIndex(String)` | before 260827 | ✔ |
| `findName(int)` | on 261002 | ✔ |
| `fromName(String)` | before 260827 | ✔ |
| `changeArmour(String)` | on 261002 | ✔ |
| `removeTabsAndSpaces(String)` | on 261002 | ✔ |
| `fromName(int)` | before 260827 | ✔ |
| `tValSValCount(String)` | before 260830 | ✔ |
| `tvalSvalList(String)` | before 260830 | ✔ |
| `getName()` | before 260827 | ✔ |
| `isStaff()` | before 260827 | ✔ |
| `isWand()` | before 260827 | ✔ |
| `isRod()` | before 260827 | ✔ |
| `isPotion()` | before 260827 | ✔ |
| `isScroll()` | before 260827 | ✔ |
| `isFood()` | before 260827 | ✔ |
| `isMushroom()` | before 260827 | ✔ |
| `isLight()` | before 260827 | ✔ |
| `isRing()` | before 260827 | ✔ |
| `isChest()` | before 260827 | ✔ |
| `isFuel()` | before 260827 | ✔ |
| `isMoney()` | before 260827 | ✔ |
| `isDigger()` | before 260830 | ✔ |
| `canHaveNourishment()` | before 260827 | ✔ |
| `canHaveCharges()` | before 260827 | ✔ |
| `canHaveTimeout()` | before 260827 | ✔ |
| `isBodyArmour()` | before 260827 | ✔ |
| `isHeadArmour()` | before 260827 | ✔ |
| `isAmmo()` | before 260827 | ✔ |
| `isSharpMissile()` | before 260827 | ✔ |
| `isBolt()` | before 260827 | ✔ |
| `isLauncher()` | before 260827 | ✔ |
| `isUseable()` | before 260827 | ✔ |
| `canHaveFailure()` | before 260827 | ✔ |
| `isWeapon()` | before 260827 | ✔ |
| `isArmour()` | before 260827 | ✔ |
| `isMeleeWeapon()` | before 260827 | ✔ |
| `hasVariablePower()` | before 260827 | ✔ |
| `isWearable()` | before 260827 | ✔ |
| `isEdible()` | before 260827 | ✔ |
| `canHaveFlavour()` | before 260827 | ✔ |
| `isBook()` | before 260827 | ✔ |
| `isZapper()` | before 260827 | ✔ |
| `isJewellery()` | before 260827 | ✔ |


## middle/player

##### `Birther.java`

**Responsible for:** A snapshot of a character mid-roll, kept so the birth screen can step back to it - the port of C's struct birther (player-birth.c:93-109).

**class `Birther`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (10/10 documented): ✔ `race`, `playerClass`, `age`, `weight`, `height`, `sc`, `au`, `stat`, `history`, `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Birther()` | on 260906 | ✔ |
| `getRace()` | — | ✔ |
| `setRace(PlayerRace)` | — | ✔ |
| `getPlayerClass()` | — | ✔ |
| `setPlayerClass(PlayerClass)` | — | ✔ |
| `getAge()` | — | ✔ |
| `setAge(int)` | — | ✔ |
| `getWeight()` | — | ✔ |
| `setWeight(int)` | — | ✔ |
| `getHeight()` | — | ✔ |
| `setHeight(int)` | — | ✔ |
| `getSc()` | — | ✔ |
| `setSc(int)` | — | ✔ |
| `getAu()` | — | ✔ |
| `setAu(long)` | — | ✔ |
| `getStat()` | — | ✔ |
| `setStat(Stats, int)` | — | ✔ |
| `getHistory()` | — | ✔ |
| `setHistoryBirth(String)` | — | ✔ |
| `getName()` | — | ✔ |
| `setName(String)` | — | ✔ |
| `copy()` | on 260906 | ✔ |


##### `BoundPlayerAbility.java`

**Responsible for:** One binding of a PlayerProperty to a UI entry, with the value that binding carries — the port of C's struct bound_player_ability (ui-entry.c:69-73).

**class `BoundPlayerAbility`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `ability`, `value`, `haveValue`, `isAux`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `BoundPlayerAbility(PlayerProperty, int, boolean, boolean)` | — | ✔ |
| `getAbility()` | — | ✔ |
| `getValue()` | — | ✔ |
| `isHaveValue()` | — | ✔ |
| `isAux()` | — | ✔ |


##### `EquipSlot.java`

**Responsible for:** One equipment slot in a PlayerBody — a wearable position (weapon, body armour, a ring finger, …) that may hold a single item.

**class `EquipSlot`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (3/3 documented): ✔ `type`, `name`, `item`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EquipSlot(EquipmentSlotsEnum, String)` | before 260815 | ✔ |
| `getItem()` | before 260815 | ✔ |
| `getType()` | before 260815 | ✔ |
| `getName()` | before 260815 | ✔ |
| `setItem(ItemObject)` | on 260905 | ✔ |


##### `HistoryInfo.java`

**Responsible for:** One event in the player's history ledger — the port of C's struct history_info (player-history.h).

**class `HistoryInfo`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (6/6 documented): ✔ `type`, `dLevel`, `cLevel`, `artifact`, `turn`, `historyText`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `HistoryInfo(Flag<PlayerHistoryType>, int, int, Artifact, long, String)` | on 260831 | ✔ |


##### `Player.java`

**Responsible for:** The player - the port of C's struct player (player.h), and the central mutable object of a game in progress.

**class `Player`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (61/63 documented): ✔ `logger`, `itemKnowledge`, `curSp`, `race`, `playerClass`, `grid`, `oldGrid`, `hitDie`, `expFact`, `age`, `height`, `weight`, `au`, `maxDepth`, `recallDepth`, `depth`, `maxLevel`, `level`, `maxExp`, `exp`, `expFrac`, `maxHP`, `currentHP`, `chpFrac`, `maxSP`, `gear`, `cspFrac`, `statMax`, `statCur`, `statMap`, `timed`, `wordRecall`, `deepDescent`, `energy`, `totalEnergy`, `restingTurn`, `food`, `unignoring`, `skipCmdCoercion`, `fullName`, `diedFrom`, `historyBirth`, `quests`, `totalWinner`, `noScore`, `isDead`, `isWizard`, `playerHP`, `auBirth`, `statsBirth`, `htBirth`, `wtBirth`, `options`, `playerHistory`, `body`, `shape`, `gearKnown`, `cave`, `state`, `knownState`, `playerUpkeep` — ✘ `spellFlags`, `spellOrder`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Player()` | before 260815 | ✔ |
| `wipe()` | before 260815 | ✔ |
| `setAge(int)` | — | ✔ |
| `setCave(Chunk)` | — | ✔ |
| `getCurSp()` | before 260815 | ✔ |
| `setCurSp(int)` | — | ✔ |
| `setCspFrac(int)` | — | ✔ |
| `getRace()` | — | ✔ |
| `getShape()` | — | ✔ |
| `setBody(PlayerBody)` | — | ✔ |
| `getTimedEffect(TimedEffect)` | before 260815 | ✔ |
| `getCave()` | before 260815 | ✔ |
| `getPlayerUpkeep()` | before 260815 | ✔ |
| `getPlayerBody()` | before 260815 | ✔ |
| `isShapeChanged()` | before 260815 | ✔ |
| `hasPlayerFlag(PlayerFlag)` | before 260815 | ✔ |
| `hasObjectFlag(ObjectFlag)` | before 260815 | ✔ |
| `getStateLight()` | before 260815 | ✔ |
| `getTimedEffectOrDefault(TimedEffect, int)` | on 260901 | ✔ |
| `putTimed(TimedEffect, int)` | before 260815 | ✔ |
| `getCurStatValue(Stats)` | before 260815 | ✔ |
| `opt(PlayerOptionEnum)` | before 260815 | ✔ |
| `getSkipCmdCoercion()` | before 260815 | ✔ |
| `setSkipCmdCoercion(int)` | before 260815 | ✔ |
| `getMaxStatValue(Stats)` | before 260815 | ✔ |
| `getMaxSP()` | before 260815 | ✔ |
| `setMaxSP(int)` | — | ✔ |
| `getPlayerClass()` | before 260815 | ✔ |
| `getEnergy()` | before 260815 | ✔ |
| `setEnergy(int)` | before 260815 | ✔ |
| `isDead()` | before 260815 | ✔ |
| `playerTimedContains(TimedEffect)` | on 260901 | ✔ |
| `playerHasTimed()` | on 260901 | ✔ |
| `getPlayerState()` | before 260815 | ✔ |
| `getTotalEnergy()` | before 260815 | ✔ |
| `setTotalEnergy(int)` | before 260815 | ✔ |
| `getGrid()` | before 260815 | ✔ |
| `getDepth()` | before 260815 | ✔ |
| `setDepth(int)` | before 260815 | ✔ |
| `statDec(Stats, boolean)` | on 260831 | ✔ |
| `getCurrentHP()` | before 260815 | ✔ |
| `getExp()` | before 260831 | ✔ |
| `getPlayerHistory()` | before 260901 | ✔ |
| `getGearKnown()` | before 260901 | ✔ |
| `adjustLevel(boolean)` | on 260831 | ✔ |
| `getMaxHP()` | before 260815 | ✔ |
| `isResting()` | before 260815 | ✔ |
| `getWordRecall()` | before 260815 | ✔ |
| `decrementWordRecall()` | before 260815 | ✔ |
| `getRecallDepth()` | before 260815 | ✔ |
| `getDeepDescent()` | before 260815 | ✔ |
| `decrementDeepDescent()` | before 260815 | ✔ |
| `getMaxDepth()` | before 260815 | ✔ |
| `getGear()` | before 260815 | ✔ |
| `getPlayerOptions()` | before 260815 | ✔ |
| `updateMaxLevel()` | before 260815 | ✔ |
| `updateDungeonDepth()` | before 260815 | ✔ |
| `flagsTimed(Flag<ObjectFlag>)` | on 260818 | ✔ |
| `playerFlags(PlayerState, Flag<ObjectFlag>)` | on 260818 | ✔ |
| `getPlayerHP(int)` | — | ✔ |
| `setPlayerMaxHP(int)` | — | ✔ |
| `setCurrentHP(int)` | — | ✔ |
| `setChpFrac(int)` | — | ✔ |
| `setState(PlayerState)` | — | ✔ |
| `getKnownState()` | — | ✔ |
| `setKnownState(PlayerState)` | — | ✔ |
| `getLevel()` | — | ✔ |
| `isUnignoring()` | — | ✔ |
| `getQuests()` | — | ✔ |
| `playerStatInc(Stats)` | on 260831 | ✔ |
| `playerExpGain(long)` | on 260831 | ✔ |
| `playerExpLose(long, boolean)` | on 260831 | ✔ |
| `getHeight()` | — | ✔ |
| `setHeight(int)` | — | ✔ |
| `setHeightBirth(int)` | — | ✔ |
| `getWeight()` | — | ✔ |
| `setWeight(int)` | — | ✔ |
| `setWeightBirth(int)` | — | ✔ |
| `getAU()` | — | ✔ |
| `setAU(long)` | — | ✔ |
| `getHitDie()` | — | ✔ |
| `setHitDie(int)` | — | ✔ |
| `setPlayerHitpoint(int, int)` | — | ✔ |
| `setStatMax(Stats, int)` | — | ✔ |
| `setCurrStatValue(Stats, int)` | — | ✔ |
| `setCurrStatMap(Stats, Stats)` | — | ✔ |
| `getStatMax(Stats)` | — | ✔ |
| `setStatBirth(Stats, int)` | — | ✔ |
| `setClass(PlayerClass)` | — | ✔ |
| `setRace(PlayerRace)` | — | ✔ |
| `setMaxLevel(int)` | — | ✔ |
| `setLevel(int)` | — | ✔ |
| `setExpFact(int)` | — | ✔ |
| `setPlayerHistory(PlayerHistory)` | — | ✔ |
| `getAUBirth()` | — | ✔ |
| `setQuests(ArrayList<Quest>)` | — | ✔ |
| `setUpkeep(PlayerUpkeep)` | — | ✔ |
| `setTimed(Map<TimedEffect,Integer>)` | — | ✔ |
| `setItemKnowledge(KnownObject)` | — | ✔ |
| `setOptions(PlayerOptions)` | — | ✔ |
| `setRestingTurn(int)` | — | ✔ |
| `setShape(PlayerShape)` | — | ✔ |
| `getItemKnowledge()` | — | ✔ |
| `getAge()` | — | ✔ |
| `setAUBirth(long)` | — | ✔ |
| `getWeightBirth()` | — | ✔ |
| `getHeightBirth()` | — | ✔ |
| `getStatBirth(Stats)` | — | ✔ |
| `getFullName()` | — | ✔ |
| `getHistoryBirth()` | — | ✔ |
| `setFullName(String)` | on 260907 | ✔ |
| `setHistoryBirth(String)` | — | ✔ |
| `initSpellOrder(int, int)` | on 260908 | ✔ |
| `setIsDead(boolean)` | on 260908 | ✔ |
| `setExp(long)` | — | ✔ |
| `getMaxExp()` | — | ✔ |
| `setMaxExp(long)` | — | ✔ |
| `isWizard()` | — | ✔ |
| `isWinner()` | — | ✔ |
| `getMaxLevel()` | — | ✔ |
| `getExpFact()` | — | ✔ |
| `playerKnowsBrand(Brand)` | before 261003 | ✔ |
| `playerKnowsSlay(Slay)` | before 261003 | ✔ |


##### `PlayerBirth.java`

**Responsible for:** The character-creation machinery - the port of C's player-birth.c, the game-mechanical half of the birth process.

**class `PlayerBirth`** — class Javadoc: ✔ · type stamp: on 260901

- Fields (4/4 documented): ✔ `logger`, `MAX_BIRTH_POINTS`, `MAX_PLAYER_NAME_LENGTH`, `birthStatCosts`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `embody(Player)` | — | ✔ |
| `getAHW(Player)` | on 260902 | ✔ |
| `getHistory(PlayerHistoryChart)` | on 260902 | ✔ |
| `getMoney(Player)` | on 260902 | ✔ |
| `rollHP(Player)` | on 260902 | ✔ |
| `getBonuses(Player)` | on 260902 | ✔ |
| `getStats(Player, Map<Stats,Integer>)` | — | ✔ |
| `playerGenerate(Player, PlayerRace, PlayerClass, boolean)` | — | ✔ |
| `playerInit(Player)` | on 260903 | ✔ |
| `playerMakeSimple(Player, String, String, String)` | on 260903 | ✔ |
| `playerOutfit(Player)` | on 260905 | ✔ |
| `wieldAll(Player)` | on 260819 | ✔ |
| `recalculateStats(Map<Stats,Integer>, int)` | — | ✔ |
| `resetStats(Map<Stats,Integer>, Map<Stats,Integer>, Map<Stats,Integer>, int, boolean)` | on 260906 | ✔ |
| `buyStat(Stats, Map<Stats,Integer>, Map<Stats,Integer>, Map<Stats,Integer>, int, boolean)` | on 260906 | ✔ |
| `sellStat(Stats, Map<Stats,Integer>, Map<Stats,Integer>, Map<Stats,Integer>, int, boolean)` | on 260906 | ✔ |
| `generateStats(Map<Stats,Integer>, Map<Stats,Integer>, Map<Stats,Integer>, int)` | on 260906 | ✔ |
| `saveRollerData(Birther)` | on 260906 | ✔ |
| `LoadRollerData(Birther, Birther)` | on 260906 | ✔ |
| `doBirthReset(boolean, Birther)` | on 260906 | ✔ |
| `doCmdBirthInit(Command)` | on 260907 | ✔ |
| `intToRoman(int)` | on 260907 | ✔ |
| `value(char)` | on 260907 | ✔ |
| `romanToInt(String)` | on 260907 | ✔ |
| `doCmdBirthReset(Command)` | on 260906 | ✔ |
| `doCmdChooseRace(Command)` | on 260906 | ✔ |
| `doCmdChooseClass(Command)` | on 260907 | ✔ |
| `doCmdBuyStat(Command)` | on 260907 | ✔ |
| `doCmdSellStat(Command)` | on 260907 | ✔ |
| `doCmdResetStats(Command)` | on 260907 | ✔ |
| `doCmdRefreshStats(Command)` | on 260907 | ✔ |
| `doCmdRollStats(Command)` | on 260907 | ✔ |
| `doCmdPrevStats(Command)` | on 260907 | ✔ |
| `doCmdChooseName(Command)` | on 260907 | ✔ |
| `doCmdChooseHistory(Command)` | on 260907 | ✔ |
| `doCmdAcceptCharacter(Command)` | on 260908 | ✔ |

**record `PlayerBirth.IntAndBoolean`** — class Javadoc: ✔ · type stamp: on 260906

- Record components (via `@param`) (2/2 documented): ✔ `value`, `bool`


##### `PlayerBirthStateRegistry.java`

**Responsible for:** The birth-state variables kept alongside the point-buy stat roller - the port of the block of C statics near the top of player-birth.c (stats, points_spent, points_inc, points_left, quickstart_allowed, rolled_stats, prev and quickstart_prev).

**class `PlayerBirthStateRegistry`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (9/9 documented): ✔ `inited`, `stats`, `pointsSpent`, `pointsInc`, `pointsLeft`, `quickstartAllowed`, `rolledStats`, `prev`, `quickstartPrev`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getStats()` | on 260907 | ✔ |
| `setStats(Map<Stats,Integer>)` | on 260907 | ✔ |
| `getPointsSpent()` | on 260907 | ✔ |
| `setPointsSpent(Map<Stats,Integer>)` | on 260907 | ✔ |
| `getPointsInc()` | on 260907 | ✔ |
| `setPointsInc(Map<Stats,Integer>)` | on 260907 | ✔ |
| `getPrev()` | on 260907 | ✔ |
| `setPrev(Birther)` | on 260907 | ✔ |
| `getQuickstartPrev()` | on 260907 | ✔ |
| `setQuickstartPrev(Birther)` | on 260907 | ✔ |
| `initPlayerBirthStateRegistry()` | on 260907 | ✔ |
| `getStat(Stats)` | on 260907 | ✔ |
| `setStat(Stats, int)` | on 260907 | ✔ |
| `getPointsSpent(Stats)` | on 260907 | ✔ |
| `setPointsSpent(Stats, int)` | on 260907 | ✔ |
| `getPointsInc(Stats)` | on 260907 | ✔ |
| `setPointsInc(Stats, int)` | on 260907 | ✔ |
| `getPointsLeft()` | on 260907 | ✔ |
| `setPointsLeft(int)` | on 260907 | ✔ |
| `isQuickstartAllowed()` | on 260907 | ✔ |
| `setQuickstartAllowed(boolean)` | on 260907 | ✔ |
| `isRolledStats()` | on 260907 | ✔ |
| `setRolledStats(boolean)` | on 260907 | ✔ |


##### `PlayerBlow.java`

**Responsible for:** A single named bare-handed melee attack ("blow") available to a player class.

**class `PlayerBlow`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `blowName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerBlow(String)` | — | ✔ |
| `getBlowName()` | — | ✔ |


##### `PlayerBody.java`

**Responsible for:** A body layout — a named, ordered set of equipment slots a creature presents.

**class `PlayerBody`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (3/3 documented): ✔ `logger`, `name`, `slots`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerBody(String, List<EquipSlot>)` | before 260815 | ✔ |
| `copy()` | on 260818 | ✔ |
| `itemIsEquipped(ItemObject)` | before 260816 | ✔ |
| `getName()` | before 260815 | ✔ |
| `getCount()` | before 260815 | ✔ |
| `getSlots()` | before 260815 | ✔ |
| `getSlot(int)` | before 260815 | ✔ |
| `equippedItemSlot(ItemObject)` | before 260817 | ✔ |
| `equippedItemBySlotName(String)` | on 260818 | ✔ |


##### `PlayerCalcs.java`

**Responsible for:** The derivation engine and the pipeline that decides when it runs - the port of C's player-calcs.c.

**class `PlayerCalcs`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `calcBonuses(Player, PlayerState, boolean, boolean)` | — | ✔ |
| `noticeStuff(Player)` | on 260822 | ✔ |
| `handleStuff(Player)` | before 260828 | ✔ |
| `updateStuff(Player)` | before 260828 | ✔ |
| `redrawStuff(Player)` | on 260828 | ✔ |
| `calcMana(Player, PlayerState, boolean)` | — | ✔ |
| `averageSpellStat(Player, PlayerState)` | — | ✔ |
| `calcBlows(Player, ItemObject, PlayerState, int)` | — | ✔ |
| `calcShapechange(PlayerState, Map<ElementEnum,Boolean>, PlayerShape, Extras)` | — | ✔ |
| `calcLight(Player, PlayerState, boolean)` | — | ✔ |
| `adjustSkillScale(int, int, int, int)` | — | ✔ |
| `calcHitpoints(Player)` | — | ✔ |
| `updateBonuses(Player)` | — | ✔ |
| `calcInventory(Player)` | on 260826 | ✔ |
| `calcSpells()` | — | ✔ |

**record `PlayerCalcs.Extras`** — class Javadoc: ✔ · type stamp: before 261014

- Record components (via `@param`) (4/4 documented): ✔ `blows`, `shots`, `might`, `moves`


##### `PlayerClass.java`

**Responsible for:** A playable character class — Warrior, Mage, Priest, Ranger and so on — and the abilities, proficiencies and combat parameters it grants.

**class `PlayerClass`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (15/15 documented): ✔ `logger`, `name`, `titles`, `stats`, `classSkills`, `extraSkills`, `hpAdj`, `expAdj`, `oFlags`, `pFlags`, `maxAttacks`, `minWeight`, `attMultiplier`, `startItems`, `magic`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerClass(String, List<String>, Map<Stats,Integer>, Map<PlayerSkill,Integer>, Map<PlayerSkill,Integer>, int, int, Flag<ObjectFlag>, Flag<PlayerFlag>, int, int, int, List<StartItem>, ClassMagic)` | — | ✔ |
| `getAttMultiply()` | — | ✔ |
| `getMagic()` | — | ✔ |
| `getoFlags()` | — | ✔ |
| `getSkill(PlayerSkill)` | — | ✔ |
| `getpFlags()` | — | ✔ |
| `getStatsAdj(Stats)` | — | ✔ |
| `getMinWeight()` | — | ✔ |
| `getMaxAttacks()` | — | ✔ |
| `magicRealm()` | — | ✔ |
| `getXSkill(PlayerSkill)` | — | ✔ |
| `copy()` | — | ✔ |
| `getExpFactor()` | — | ✔ |
| `getMaxHitDie()` | — | ✔ |
| `getName()` | — | ✔ |
| `getStartItems()` | — | ✔ |
| `getClassFromIndex(int)` | before 260907 | ✔ |
| `getTitle(int)` | before 261010 | ✔ |
| `getNoTitles()` | before 261010 | ✔ |


##### `PlayerHistory.java`

**Responsible for:** The player's history ledger — the port of C's struct player_history and the p->hist it is held in (player-history.h).

**class `PlayerHistory`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (2/2 documented): ✔ `logger`, `entries`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerHistory()` | on 260831 | ✔ |
| `historyAdd(Player, String, PlayerHistoryType)` | on 260831 | ✔ |
| `historyAddWithFlags(Player, String, Flag<PlayerHistoryType>, Artifact)` | on 260831 | ✔ |
| `historyAddFull(Player, Flag<PlayerHistoryType>, Artifact, int, int, int, String)` | on 260831 | ✔ |
| `addEntry(HistoryInfo)` | on 260831 | ✔ |
| `clear()` | on 260908 | ✔ |


##### `PlayerHistoryChart.java`

**Responsible for:** A numbered chart of background-history options — one node in the history-generation graph, holding the PlayerHistoryEntry rows that may be rolled at this step.

**class `PlayerHistoryChart`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (5/5 documented): ✔ `logger`, `chartNumber`, `successor`, `successorNumber`, `entries`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerHistoryChart(int, int)` | on 260902 | ✔ |
| `addEntry(PlayerHistoryEntry)` | on 260902 | ✔ |
| `setSuccessor(PlayerHistoryChart)` | on 260902 | ✔ |
| `getChartNumber()` | on 260902 | ✔ |
| `getSuccessorNumber()` | on 260902 | ✔ |
| `getSuccessor()` | on 260902 | ✔ |
| `getEntries()` | on 260902 | ✔ |


##### `PlayerHistoryEntry.java`

**Responsible for:** One option in the player's background-history generation — a single weighted line of backstory rolled at a PlayerHistoryChart.

**class `PlayerHistoryEntry`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (3/3 documented): ✔ `logger`, `roll`, `text`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerHistoryEntry(int, String)` | on 260902 | ✔ |
| `getText()` | on 260902 | ✔ |
| `getRoll()` | on 260902 | ✔ |


##### `PlayerKnowledge.java`

**Responsible for:** The object-knowledge machinery - the port of C's obj-knowledge.c, minus its parsing and its display.

**class `PlayerKnowledge`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `knowObject(Player, ItemObject)` | before 260815 | ✔ |
| `nonCurseRunesKnown(ItemObject)` | before 260817 | ✔ |
| `objectSetBaseKnown(Player, ItemObject)` | before 260817 | ✔ |
| `flavourAware(Player, ItemObject)` | on 260816 | ✔ |
| `knowsEgo(Player, EgoItem, ItemObject)` | before 260817 | ✔ |
| `learnCurse(Player, Curse)` | before 261004 | ✔ |
| `learnBrand(Player, Brand)` | before 261004 | ✔ |
| `learnSlay(Player, Slay)` | before 261004 | ✔ |
| `knowsBrand(Player, Brand)` | before 261004 | ✔ |
| `learnFlag(Player, ObjectFlag)` | before 261004 | ✔ |
| `knowsRune(Player, Rune)` | before 261004 | ✔ |
| `knowsSlay(Player, Slay)` | before 261004 | ✔ |
| `knowsCurse(Player, Curse)` | before 261004 | ✔ |
| `learnRune(Player, Rune, boolean)` | before 260815 | ✔ |
| `updateObjectKnowledge(Player)` | before 260815 | ✔ |
| `knowObject(Player, Curse)` | before 260901 | ✔ |
| `learnInnate(Player)` | before 261004 | ✔ |
| `learnAllRunes(Player)` | before 261004 | ✔ |
| `equipLearnOnDefend(Player)` | before 260815 | ✔ |
| `equipLearnOnRangedAttack(Player)` | on 260815 | ✔ |
| `equipLearnOnMeleeAttack(Player)` | on 260815 | ✔ |
| `equipLearnFlag(Player, ObjectFlag)` | on 260815 | ✔ |
| `cursesFindToA(Player, ItemObject)` | before 260815 | ✔ |
| `cursesFindToD(Player, ItemObject)` | on 260815 | ✔ |
| `cursesFindToH(Player, ItemObject)` | on 260815 | ✔ |
| `cursesFindFlags(Player, ItemObject, FlagView<ObjectFlag>)` | on 260815 | ✔ |
| `equipLearnElement(Player, ElementEnum)` | — | ✔ |
| `objectCursesFindElement(Player, ItemObject, ElementEnum)` | — | ✔ |
| `objectLearnOnWield(Player, ItemObject)` | before 261004 | ✔ |
| `cursesFindModifiers(Player, ItemObject)` | before 261004 | ✔ |
| `createObjFlagMask(boolean, Enum)` | before 261003 | ✔ |


##### `PlayerMagic.java`

**Responsible for:** Player-side spell bookkeeping - the port of the player-owned parts of C's player-spell.c.

**class `PlayerMagic`** — class Javadoc: ✔ · type stamp: on 260908

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerSpellsInit(Player)` | on 260908 | ✔ |


##### `PlayerName.java`

**Responsible for:** Player-name helpers: finds a trailing Roman-numeral suffix and produces a filesystem-safe version of a player name (C's `player-util` name handling).

**class `PlayerName`** — class Javadoc: ✘ · type stamp: —

- Fields (0/1 documented): ✘ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `findRomanSuffixStart(String)` | on 260831 | ✔ |
| `playerSafeName(int, String, boolean)` | on 260831 | ✔ |


##### `PlayerOptions.java`

**Responsible for:** The player's option settings — the port of C's struct player_options (option.h), reached in C as player->opts, together with the option.c functions that reset and restore them.

**class `PlayerOptions`** — class Javadoc: ✔ · type stamp: before 261010

- Fields (6/6 documented): ✔ `logger`, `options`, `hitpointWarn`, `lazymoveDelay`, `delayFactor`, `nameSuffix`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerOptions()` | before 261010 | ✔ |
| `has(PlayerOptionEnum)` | before 261010 | ✔ |
| `initDefaults()` | before 261010 | ✔ |
| `restoreCustom(Flag<PlayerOptionEnum>, PlayerOptionTypes)` | before 260907 | ✔ |
| `restoreMaintainer(Flag<PlayerOptionEnum>, PlayerOptionTypes)` | before 260907 | ✔ |
| `copy()` | before 260907 | ✔ |
| `optionsInitCheat()` | before 260907 | ✔ |
| `hitPointWarn()` | before 261010 | ✔ |


##### `PlayerProperty.java`

**Responsible for:** The definition of one player property — a named characteristic (a player flag, an object flag, or an elemental resistance) together with how it is presented in the UI.

**class `PlayerProperty`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (1/1 documented): ✔ `logger`

**record `PlayerProperty.BindUI`** — class Javadoc: ✔ · type stamp: before 261009

- Record components (via `@param`) (4/4 documented): ✔ `uiEntry`, `value`, `special`, `aux`
- Fields (10/10 documented): ✔ `playerPropertyType`, `group`, `pCode`, `oCode`, `eCode`, `omFlag`, `entries`, `name`, `description`, `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerProperty(PlayerPropertyType, PlayerFlag, ObjectFlag, ElementEnum, ObjectModifier, List<BindUI>, String, String, int)` | before 261009 | ✔ |
| `getPlayerPropertyType()` | before 261009 | ✔ |
| `getpCode()` | before 261009 | ✔ |
| `getoCode()` | before 261009 | ✔ |
| `geteCode()` | before 261009 | ✔ |
| `getomCode()` | before 261009 | ✔ |
| `getEntries()` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |
| `getDescription()` | before 261009 | ✔ |
| `viewAbilities()` | on 261009 | ✔ |
| `getValue()` | before 261009 | ✔ |
| `getGroup()` | before 261009 | ✔ |
| `setGroup(PlayerFlagType)` | before 261009 | ✔ |
| `raceHasAbility(PlayerRace, PlayerProperty)` | on 261009 | ✔ |
| `classHasAbility(PlayerClass, PlayerProperty)` | on 261009 | ✔ |
| `setoCode(ObjectFlag)` | before 261009 | ✔ |
| `setpCode(PlayerFlag)` | before 261009 | ✔ |
| `seteCode(ElementEnum)` | before 261009 | ✔ |

**enum `PlayerProperty.PlayerPropertyType`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (5/5 documented): ✔ `PROP_TYPE_PLAYER`, `PROP_TYPE_OBJECT`, `PROP_TYPE_ELEMENT`, `PROP_TYPE_PROPERTY`, `PROP_TYPE_OBJECT_MODIFIER`

**enum `PlayerProperty.PlayerPropertyValue`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (4/4 documented): ✔ `NONE`, `VULNERABILITY`, `RESISTANCE`, `IMMUNITY`


##### `PlayerQuest.java`

**Responsible for:** The quest machinery - the port of C's player-quest.c, minus its parser.

**class `PlayerQuest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `isQuest(Player, int)` | — | ✔ |
| `playerQuestsReset(Player)` | on 260903 | ✔ |


##### `PlayerRace.java`

**Responsible for:** A playable character race — Human, Half-Troll, High-Elf and so on — and the innate modifiers it confers.

**class `PlayerRace`** — class Javadoc: ✔ · type stamp: —

- Fields (19/19 documented): ✔ `logger`, `name`, `rIndex`, `raceMhp`, `raceExp`, `baseAge`, `modAge`, `baseHeight`, `modHeight`, `baseWeight`, `modWeight`, `infravision`, `body`, `statsAdj`, `skillsAdj`, `oFlags`, `pFlags`, `history`, `resists`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerRace(String, int, int, int, int, int, int, int, int, int, int, PlayerBody, Map<Stats,Integer>, Map<PlayerSkill,Integer>, Flag<ObjectFlag>, Flag<PlayerFlag>, PlayerHistoryChart, Map<ElementEnum,ElementInfo>)` | — | ✔ |
| `getName()` | — | ✔ |
| `getResistKnowledge(ElementEnum)` | — | ✔ |
| `getObjectFlagKnowledge(ObjectFlag)` | — | ✔ |
| `getoFlags()` | — | ✔ |
| `getInfravision()` | — | ✔ |
| `getSkill(PlayerSkill)` | — | ✔ |
| `getResistanceLevel(ElementEnum)` | — | ✔ |
| `getpFlags()` | — | ✔ |
| `getBody()` | — | ✔ |
| `getStatAdjust(Stats)` | — | ✔ |
| `getBaseAge()` | — | ✔ |
| `getModAge()` | — | ✔ |
| `getBaseHeight()` | — | ✔ |
| `getModHeight()` | — | ✔ |
| `getModWeight()` | — | ✔ |
| `getBaseWeight()` | — | ✔ |
| `copy()` | on 260902 | ✔ |
| `getExpFactor()` | — | ✔ |
| `getMaxHitDie()` | — | ✔ |
| `getHistory()` | — | ✔ |
| `getRaceFromIndex(int)` | before 260907 | ✔ |


##### `PlayerShape.java`

**Responsible for:** An alternative form the player can take on — a shapechange such as bat, vampire or werewolf — bundling the combat bonuses, skills, flags, resistances, value modifiers and unarmed blows that apply while the form is held.

**class `PlayerShape`** — class Javadoc: ✔ · type stamp: before 260820

- Fields (13/13 documented): ✔ `logger`, `name`, `toAc`, `toHit`, `toDam`, `skills`, `flags`, `pflags`, `objectValueModifiers`, `elementValueModifiers`, `effect`, `numBlows`, `playerBlow`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerShape(String, int, int, int, Map<PlayerSkill,Integer>, Flag<ObjectFlag>, Flag<PlayerFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, List<Effect>, int, List<PlayerBlow>)` | before 260820 | ✔ |
| `toString()` | before 260820 | ✔ |
| `getName()` | before 260820 | ✔ |
| `getToAc()` | before 260820 | ✔ |
| `getToHit()` | before 260820 | ✔ |
| `getToDam()` | before 260820 | ✔ |
| `getSkills()` | before 260820 | ✔ |
| `getFlags()` | before 260820 | ✔ |
| `getPflags()` | before 260820 | ✔ |
| `getObjectValueModifiers()` | before 260820 | ✔ |
| `getElementValueModifiers()` | before 260820 | ✔ |
| `getEffect()` | before 260820 | ✔ |
| `getNumBlows()` | before 260820 | ✔ |
| `getPlayerBlow()` | before 260820 | ✔ |
| `getModifier(Stats)` | before 260820 | ✔ |


##### `PlayerState.java`

**Responsible for:** The player's fully calculated combat and character state - the port of C's struct player_state (player.h).

**class `PlayerState`** — class Javadoc: ✔ · type stamp: —

- Fields (26/26 documented): ✔ `statAdd`, `statInd`, `statUse`, `statTop`, `skills`, `speed`, `numBlows`, `numShots`, `numMoves`, `ammoMult`, `ammoTVal`, `ac`, `damRed`, `perDamRed`, `toA`, `toH`, `toD`, `seeInfra`, `curLight`, `heavyWield`, `heavyShoot`, `blessWield`, `cumberArmour`, `flags`, `pflags`, `elInfo`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerState()` | — | ✔ |
| `wipe()` | — | ✔ |
| `hasPFlag(PlayerFlag)` | — | ✔ |
| `hasOFlag(ObjectFlag)` | — | ✔ |
| `getStatInd(Stats)` | — | ✔ |
| `getCurLight()` | — | ✔ |
| `setCurLight(int)` | — | ✔ |
| `getSpeed()` | — | ✔ |
| `setSpeed(int)` | — | ✔ |
| `perDamRed()` | — | ✔ |
| `setSeeInfra(int)` | — | ✔ |
| `setStateSkill(PlayerSkill, int)` | — | ✔ |
| `setElInfo(ElementEnum, int)` | — | ✔ |
| `copyPlayerFlag(FlagView<PlayerFlag>)` | — | ✔ |
| `unionPlayerFlags(FlagView<PlayerFlag>)` | — | ✔ |
| `unionObjectFlags(FlagView<ObjectFlag>)` | — | ✔ |
| `statAdd(Stats, int)` | — | ✔ |
| `getStatAdd(Stats)` | — | ✔ |
| `skillAdd(PlayerSkill, int)` | — | ✔ |
| `infraAdd(int)` | — | ✔ |
| `getDamRed()` | — | ✔ |
| `setDamRed(int)` | — | ✔ |
| `getElInfo()` | — | ✔ |
| `toAcAdd(int)` | — | ✔ |
| `toHitAdd(int)` | — | ✔ |
| `toDamAdd(int)` | — | ✔ |
| `setStatTop(Stats, int)` | — | ✔ |
| `setStatUse(Stats, int)` | — | ✔ |
| `setStatInd(Stats, int)` | — | ✔ |
| `getPlayerSkill(PlayerSkill)` | — | ✔ |
| `getPlayerFlag()` | — | ✔ |
| `playerFlagOn(PlayerFlag)` | on 260831 | ✔ |
| `playerFlagOff(PlayerFlag)` | on 260831 | ✔ |
| `getObjectFlag()` | — | ✔ |
| `weightLimit()` | — | ✔ |
| `setAmmoTValue(TValue)` | — | ✔ |
| `getNumShots()` | — | ✔ |
| `setNumShots(int)` | — | ✔ |
| `isHeavyWield()` | — | ✔ |
| `setHeavyWield(boolean)` | — | ✔ |
| `setNumMoves(int)` | — | ✔ |
| `isHeavyShoot()` | — | ✔ |
| `setHeavyShoot(boolean)` | — | ✔ |
| `getAmmoMult()` | — | ✔ |
| `setAmmoMult(int)` | — | ✔ |
| `getBaseAc()` | — | ✔ |
| `setBaseAc(int)` | — | ✔ |
| `setResLevel(ElementEnum, int)` | — | ✔ |
| `getResLevel(ElementEnum)` | — | ✔ |
| `getToAc()` | — | ✔ |
| `isBlessWield()` | — | ✔ |
| `setBlessWield(boolean)` | — | ✔ |
| `getStatTop(Stats)` | — | ✔ |
| `getStatUse(Stats)` | — | ✔ |
| `isCumberArmour()` | — | ✔ |
| `setCumberArmour(boolean)` | — | ✔ |
| `copy()` | — | ✔ |
| `getAmmoTval()` | — | ✔ |
| `oFlagOn(ObjectFlag)` | — | ✔ |
| `getNumBlows()` | — | ✔ |
| `oFlagOff(ObjectFlag)` | — | ✔ |
| `setNumBlows(int)` | — | ✔ |


##### `PlayerTimed.java`

**Responsible for:** The timed-effect machinery - the port of the effect-handling half of C's player-timed.c.

**class `PlayerTimed`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearTimed(TimedEffect, boolean, boolean)` | — | ✔ |
| `getTimedEffectOrDefault(Player, TimedEffect, int)` | on 260901 | ✔ |
| `incTimed(TimedEffect, int, boolean, boolean, boolean)` | — | ✔ |
| `incCheck(Player, TimedEffect, boolean)` | on 260831 | ✔ |
| `setTimed(Player, TimedEffect, int, boolean, boolean)` | on 260829 | ✔ |
| `timedGradeEq(Player, TimedEffect, String)` | on 260818 | ✔ |
| `playerIncTimed(Player, TimedEffect, int, boolean, boolean, boolean)` | on 260831 | ✔ |
| `playerDecTimed(Player, TimedEffect, int, boolean, boolean)` | on 260831 | ✔ |
| `playerClearTimed(Player, TimedEffect, boolean, boolean)` | on 260831 | ✔ |


##### `PlayerTimedEffect.java`

**Responsible for:** The static definition of a single timed player status — all the data describing how one TMD_* effect behaves, as distinct from the live counter that tracks how long an instance has left to run.

**class `PlayerTimedEffect`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (19/19 documented): ✔ `name`, `flagRedraw`, `flagUpdate`, `description`, `onEnd`, `onIncrease`, `onDecrease`, `msgT`, `fail`, `grade`, `onBeginEffect`, `onEndEffect`, `nonStacking`, `lowerBound`, `oFlagDup`, `oFlagExactlySyn`, `tempResist`, `tempBrand`, `tempSlay`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerTimedEffect(TimedEffect, String, String, String, String, MessageType, List<TimedFailure>, List<TimedGrade>, Effect, Effect, boolean, int, ObjectFlag, boolean, ElementEnum, Brand, Slay)` | before 260815 | ✔ |
| `getName()` | before 260815 | ✔ |
| `getFlagRedraw()` | — | ✔ |
| `getFlagUpdate()` | — | ✔ |
| `getDescription()` | before 260815 | ✔ |
| `getOnEnd()` | before 260815 | ✔ |
| `getOnIncrease()` | before 260815 | ✔ |
| `getOnDecrease()` | before 260815 | ✔ |
| `getMsgT()` | before 260815 | ✔ |
| `getFail()` | before 260815 | ✔ |
| `getGrade()` | before 260815 | ✔ |
| `getOnBeginEffect()` | before 260815 | ✔ |
| `getOnEndEffect()` | before 260815 | ✔ |
| `isNonStacking()` | before 260815 | ✔ |
| `getLowerBound()` | before 260815 | ✔ |
| `getoFlagDup()` | before 260815 | ✔ |
| `isoFlagExactlySyn()` | before 260815 | ✔ |
| `getTempResist()` | before 260815 | ✔ |
| `getTempBrand()` | before 260815 | ✔ |
| `getTempSlay()` | before 260815 | ✔ |


##### `PlayerUpkeep.java`

**Responsible for:** The player's transient runtime bookkeeping — the recomputed-each-session state that is not part of the saved character, as opposed to the persistent data held on Player.

**class `PlayerUpkeep`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (32/32 documented): ✔ `playing`, `autosave`, `generateLevel`, `onlyPartial`, `dropping`, `energyUse`, `newSpells`, `healthWho`, `monsterRace`, `object`, `objectKind`, `noticeFlags`, `updateFlags`, `redrawFlags`, `command_wrk`, `createUpStair`, `createDownStair`, `lightLevel`, `arenaLevel`, `restingCounter`, `runningCounter`, `runningFirstStep`, `quiverObjects`, `inventoryObjects`, `totalWeight`, `inventoryCount`, `equipmentCount`, `quiverCount`, `rechargePower`, `stepCount`, `steps`, `pathDestination`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerUpkeep()` | — | ✔ |
| `setCommand_wrk(int)` | before 261009 | ✔ |
| `getCommand_wrk()` | before 261009 | ✔ |
| `getRedrawFlags()` | — | ✔ |
| `clearRedrawFlags(FlagView<PlayerRedraw>)` | on 260818 | ✔ |
| `setRedrawFlagsOn(PlayerRedraw)` | before 261009 | ✔ |
| `setRedrawFlagsOff(PlayerRedraw)` | before 261009 | ✔ |
| `isPlaying()` | before 261009 | ✔ |
| `energyUse()` | before 261009 | ✔ |
| `setEnergyUse(int)` | before 261009 | ✔ |
| `getEnergyUse()` | before 261009 | ✔ |
| `getDropping()` | before 261009 | ✔ |
| `setDropping(boolean)` | before 261009 | ✔ |
| `generateLevel()` | before 261009 | ✔ |
| `setGenerateLevel(boolean)` | before 261009 | ✔ |
| `isArenaLevel()` | before 261009 | ✔ |
| `setArenaLevel(boolean)` | before 261009 | ✔ |
| `healthWho()` | before 261009 | ✔ |
| `getHealthWho()` | before 261009 | ✔ |
| `getMonsterRace()` | before 261009 | ✔ |
| `setUpdateFlagOn(PlayerUpdateEnum)` | before 261009 | ✔ |
| `setUpdateFlagsOn(PlayerUpdateEnum)` | — | ✔ |
| `noticeFlagOn(PlayerNotice)` | before 261009 | ✔ |
| `getRestingCounter()` | before 261009 | ✔ |
| `setAutosave(boolean)` | before 261009 | ✔ |
| `healthTrack(Monster)` | — | ✔ |
| `orNoticeFlag(PlayerNotice)` | on 260816 | ✔ |
| `getQuiver()` | on 260816 | ✔ |
| `getInventory()` | on 260816 | ✔ |
| `getTotalWeight()` | — | ✔ |
| `isOnlyPartial()` | before 261009 | ✔ |
| `isNotice()` | — | ✔ |
| `getNoticeFlags()` | before 261009 | ✔ |
| `setNoticeFlagOff(PlayerNotice)` | — | ✔ |
| `setNoticeFlagOn(PlayerNotice)` | — | ✔ |
| `getInventoryCount()` | before 261009 | ✔ |
| `setInventoryCount(int)` | before 261009 | ✔ |
| `getQuiverCount()` | before 261009 | ✔ |
| `setQuiverCount(int)` | before 261009 | ✔ |
| `getObject()` | before 261009 | ✔ |
| `setObject(ItemObject)` | before 261009 | ✔ |
| `getUpdate()` | — | ✔ |
| `updateHas(PlayerUpdateEnum)` | — | ✔ |
| `updateOff(PlayerUpdateEnum)` | — | ✔ |
| `updateOn(PlayerUpdateEnum)` | — | ✔ |
| `getRunning()` | before 261009 | ✔ |
| `setInventory(ItemObject[])` | — | ✔ |
| `setQuiverObjects(ItemObject[])` | — | ✔ |
| `setTotalWeight(int)` | — | ✔ |
| `getEquipCount()` | — | ✔ |
| `setEquipCount(int)` | — | ✔ |
| `setPlaying(boolean)` | — | ✔ |
| `setHealthWho(Monster)` | before 261009 | ✔ |
| `setRestingCounter(int)` | before 261009 | ✔ |


##### `PlayerUtils.java`

**Responsible for:** Free-standing player helper routines — the port of C's player-util.c.

**class `PlayerUtils`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `statBigStep`, `statNormalMax`, `statNormalMin`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `restingCompleteSpecial()` | — | ✔ |
| `takeTerrainDamage(Loc)` | — | ✔ |
| `takeHit(int, String)` | — | ✔ |
| `applyDamageReduction(int)` | — | ✔ |
| `overExert(Flag<PlayerOverExertion>, int, int)` | — | ✔ |
| `disturb()` | — | ✔ |
| `regenHP()` | — | ✔ |
| `regenMana()` | — | ✔ |
| `updateLight()` | — | ✔ |
| `dungeonChangeLevel(int)` | — | ✔ |
| `dungeonGetNextLevel(int, int)` | — | ✔ |
| `search()` | — | ✔ |
| `modifyStatValue(int, int)` | on 260818 | ✔ |
| `attackRandomMonster(Player)` | — | ✔ |
| `playerRestingCount(Player)` | on 260828 | ✔ |
| `playerHasObjectFlagIgnoringTimed(Player, ObjectFlag)` | on 260829 | ✔ |
| `playerIsImmune(Player, ElementEnum)` | on 260829 | ✔ |
| `setRecallDepth()` | — | ✔ |
| `restingIsSpecial(int)` | — | ✔ |


##### `Quest.java`

**Responsible for:** A quest the player can undertake — slay a target number of a specific monster race — tracked from acceptance through to completion.

**class `Quest`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (6/6 documented): ✔ `index`, `name`, `level`, `race`, `currentNumber`, `maxNumber`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Quest(int, String, int, MonsterRace, int, int)` | before 261009 | ✔ |
| `getIndex()` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |
| `getLevel()` | before 261009 | ✔ |
| `getRace()` | before 261009 | ✔ |
| `getCurrentNumber()` | before 261009 | ✔ |
| `getMaxNumber()` | before 261009 | ✔ |
| `copy()` | on 260903 | ✔ |


##### `StartItem.java`

**Responsible for:** One entry in a class's starting-equipment list — a kind of item the character is granted at birth, with a randomized quantity and optional birth-option exclusions.

**class `StartItem`** — class Javadoc: ✔ · type stamp: before 261009

- Fields (5/5 documented): ✔ `tValue`, `sValue`, `min`, `max`, `eOpts`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `StartItem(TValue, String, int, int, List<StartOptionExclusion>)` | before 261009 | ✔ |
| `gettValue()` | before 261009 | ✔ |
| `getsValue()` | before 261009 | ✔ |
| `getMin()` | before 261009 | ✔ |
| `getMax()` | before 261009 | ✔ |
| `geteOpts()` | before 261009 | ✔ |


##### `StartOptionExclusion.java`

**Responsible for:** A single birth-option constraint on a piece of starting equipment (the eopts field of a class.txt equip: line).

**record `StartOptionExclusion`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `option`, `negated`


##### `TimedFailure.java`

**Responsible for:** One condition that stops a timed effect from being increased — an object flag the player has, an element they resist or are vulnerable to, a player flag they have, or another timed effect that is running.

**class `TimedFailure`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (6/6 documented): ✔ `logger`, `index`, `oFlag`, `element`, `pFlag`, `effect`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TimedFailure(TimedEffect, TimedEffectReasonType)` | before 261014 | ✔ |
| ctor `TimedFailure(ObjectFlag, TimedEffectReasonType)` | before 261014 | ✔ |
| ctor `TimedFailure(ElementEnum, TimedEffectReasonType)` | before 261014 | ✔ |
| ctor `TimedFailure(PlayerFlag, TimedEffectReasonType)` | before 261014 | ✔ |
| `getObjFlagCode()` | before 261014 | ✔ |
| `getElementCode()` | before 261014 | ✔ |
| `getPlayerFlagCode()` | before 261014 | ✔ |
| `getEffectCode()` | before 261014 | ✔ |
| `getCode()` | before 261014 | ✔ |


##### `TimedGrade.java`

**Responsible for:** One severity grade of a timed effect — a named band within a status's range, with its own colour and onset/fade messages.

**record `TimedGrade`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/6 documented): ✘ `Grade`, `colour`, `max`, `status`, `upMsg`, `downMsg`


## middle/player/enums

##### `PlayerFlag.java`

**Responsible for:** Per-class innate ability flags — the PF_* set marking the special powers and quirks a uk.co.jackoftradesltd.middle.player.PlayerClass grants (and which a few races contribute to).

**enum `PlayerFlag`** — class Javadoc: ✔ · type stamp: —

- Enum constants (18/18 documented): ✔ `PF_NONE`, `PF_FAST_SHOT`, `PF_BRAVERY_30`, `PF_BLESS_WEAPON`, `PF_ZERO_FAIL`, `PF_BEAM`, `PF_CHOOSE_SPELLS`, `PF_KNOW_MUSHROOM`, `PF_KNOW_ZAPPER`, `PF_SEE_ORE`, `PF_NO_MANA`, `PF_CHARM`, `PF_UNLIGHT`, `PF_ROCK`, `PF_STEAL`, `PF_SHIELD_BASH`, `PF_EVIL`, `PF_COMBAT_REGEN`


##### `PlayerFlagType.java`

**Responsible for:** Enum of where a player flag comes from: none, special, race or class.

**enum `PlayerFlagType`** — class Javadoc: ✘ · type stamp: —

- Enum constants (0/4 documented): ✘ `PLAYER_FLAG_NONE`, `PLAYER_FLAG_SPECIAL`, `PLAYER_FLAG_RACE`, `PLAYER_FLAG_CLASS`


##### `PlayerHistoryType.java`

**Responsible for:** The kinds of entry the character history can hold — the port of C's anonymous history type enum (player-history.h), whose constants are generated from the HIST() lines of list-history-types.h.

**enum `PlayerHistoryType`** — class Javadoc: ✔ · type stamp: before 261008

- Enum constants (12/12 documented): ✔ `HIST_NONE`, `HIST_PLAYER_BIRTH`, `HIST_ARTIFACT_UNKNOWN`, `HIST_ARTIFACT_KNOWN`, `HIST_ARTIFACT_LOST`, `HIST_PLAYER_DEATH`, `HIST_SLAY_UNIQUE`, `HIST_USER_INPUT`, `HIST_SAVEFILE_IMPORT`, `HIST_GAIN_LEVEL`, `HIST_GENERIC`, `HIST_MAX`
- Fields (1/1 documented): ✔ `description`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerHistoryType(String)` | before 261008 | ✔ |
| `getDescription()` | before 261008 | ✔ |


##### `PlayerNotice.java`

**Responsible for:** "Notice" flags identifying pending housekeeping the engine must carry out before the next command — the PN_* set from the C original (player-calcs.h).

**enum `PlayerNotice`** — class Javadoc: ✔ · type stamp: —

- Enum constants (3/3 documented): ✔ `PN_COMBINE`, `PN_IGNORE`, `PN_MON_MESSAGE`


##### `PlayerOptionEnum.java`

**Responsible for:** The set of user-configurable game options, each pairing a human-readable description with the option category it belongs to and its default ("normal") value.

**enum `PlayerOptionEnum`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (0/46 documented): ✘ `OP_none`, `OP_rogue_like_commands`, `OP_autoexplore_commands`, `OP_use_sound`, `OP_show_damage`, `OP_use_old_target`, `OP_pickup_always`, `OP_pickup_inven`, `OP_show_flavors`, `OP_show_target`, `OP_highlight_player`, `OP_disturb_near`, `OP_solid_walls`, `OP_hybrid_walls`, `OP_view_yellow_light`, `OP_animate_flicker`, `OP_center_player`, `OP_purple_uniques`, `OP_auto_more`, `OP_hp_changes_color`, `OP_mouse_movement`, `OP_notify_recharge`, `OP_effective_speed`, `OP_cheat_hear`, `OP_score_hear`, `OP_cheat_room`, `OP_score_room`, `OP_cheat_xtra`, `OP_score_xtra`, `OP_cheat_live`, `OP_score_live`, `OP_birth_randarts`, `OP_birth_connect_stairs`, `OP_birth_force_descend`, `OP_birth_no_recall`, `OP_birth_no_artifacts`, `OP_birth_stacking`, `OP_birth_lose_arts`, `OP_birth_feelings`, `OP_birth_no_selling`, `OP_birth_start_kit`, `OP_birth_ai_learn`, `OP_birth_know_runes`, `OP_birth_know_flavors`, `OP_birth_levels_persist`, `OP_birth_percent_damage`
- Fields (3/3 documented): ✔ `description`, `playerOptionType`, `normal`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerOptionEnum(String, PlayerOptionTypes, boolean)` | before 261009 | ✔ |
| `getDescription()` | before 261009 | ✔ |
| `getPlayerOptionType()` | before 261009 | ✔ |
| `isNormal()` | before 261009 | ✔ |
| `isCheat()` | before 261009 | ✔ |


##### `PlayerOptionTypes.java`

**Responsible for:** The categories a PlayerOptionEnum can belong to, controlling how and where an option is presented and how it is treated by birth and scoring.

**enum `PlayerOptionTypes`** — class Javadoc: ✔ · type stamp: before 261009

- Enum constants (5/5 documented): ✔ `INTERFACE`, `BIRTH`, `CHEAT`, `SCORE`, `SPECIAL`
- Fields (1/1 documented): ✔ `name`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerOptionTypes(String)` | before 261009 | ✔ |
| `getName()` | before 261009 | ✔ |


##### `PlayerOverExertion.java`

**Responsible for:** The penalties player_over_exert() can inflict on the player — the port of the anonymous PY_EXERT_* enum in player-util.h.

**enum `PlayerOverExertion`** — class Javadoc: ✔ · type stamp: on 261009

- Enum constants (9/9 documented): ✔ `PY_EXERT_NONE`, `PY_EXERT_CON`, `PY_EXERT_FAINT`, `PY_EXERT_SCRAMBLE`, `PY_EXERT_CUT`, `PY_EXERT_CONF`, `PY_EXERT_HALLU`, `PY_EXERT_SLOW`, `PY_EXERT_HP`


##### `PlayerRedraw.java`

**Responsible for:** "Redraw" flags identifying which on-screen region has become stale and needs repainting — the PR_* set from the C original (player-calcs.h).

**enum `PlayerRedraw`** — class Javadoc: ✔ · type stamp: —

- Enum constants (26/26 documented): ✔ `PR_MISC`, `PR_TITLE`, `PR_LEV`, `PR_EXP`, `PR_STATS`, `PR_ARMOR`, `PR_HP`, `PR_MANA`, `PR_GOLD`, `PR_HEALTH`, `PR_SPEED`, `PR_STUDY`, `PR_DEPTH`, `PR_STATUS`, `PR_DTRAP`, `PR_STATE`, `PR_MAP`, `PR_INVEN`, `PR_EQUIP`, `PR_MESSAGE`, `PR_MONSTER`, `PR_OBJECT`, `PR_MONLIST`, `PR_ITEMLIST`, `PR_FEELING`, `PR_LIGHT`
- Fields (4/4 documented): ✔ `basic`, `extra`, `subwindow`, `eventType`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `PlayerRedraw(boolean, boolean, boolean, GameEventType)` | — | ✔ |
| `isBasic()` | — | ✔ |
| `isExtra()` | — | ✔ |
| `isSubwindow()` | — | ✔ |
| `getEventType()` | — | ✔ |


##### `PlayerSkill.java`

**Responsible for:** The player's skill axes — the SKILL_* proficiencies that race, class and level contribute to and that are resolved into concrete combat/utility bonuses.

**enum `PlayerSkill`** — class Javadoc: ✔ · type stamp: —

- Enum constants (12/12 documented): ✔ `SKILL_NONE`, `SKILL_DISARM_PHYS`, `SKILL_DISARM_MAGIC`, `SKILL_DEVICE`, `SKILL_SAVE`, `SKILL_SEARCH`, `SKILL_STEALTH`, `SKILL_TO_HIT_MELEE`, `SKILL_TO_HIT_BOW`, `SKILL_TO_HIT_THROW`, `SKILL_DIGGING`, `SKILL_MAX`


##### `PlayerUpdateEnum.java`

**Responsible for:** "Update" flags identifying which derived player quantity has gone stale and must be recalculated — the PU_* set from the C original (player-calcs.h).

**enum `PlayerUpdateEnum`** — class Javadoc: ✔ · type stamp: —

- Enum constants (10/10 documented): ✔ `PU_BONUS`, `PU_TORCH`, `PU_HP`, `PU_MANA`, `PU_SPELLS`, `PU_UPDATE_VIEW`, `PU_MONSTERS`, `PU_DISTANCE`, `PU_PANEL`, `PU_INVEN`


##### `RandnameType.java`

**Responsible for:** The kinds of name the random name generator can make — the port of C's randname_type (randname.h).

**enum `RandnameType`** — class Javadoc: ✔ · type stamp: on 260831

- Enum constants (3/3 documented): ✔ `RANDNAME_TOLKIEN`, `RANDNAME_SCROLL`, `RANDNAME_NUM_TYPES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fromIndex(int)` | on 260831 | ✔ |


##### `SustainStat.java`

**Responsible for:** The five sustain flags, one per stat, named by the stat they protect.

**enum `SustainStat`** — class Javadoc: ✔ · type stamp: on 261004

- Enum constants (0/7 documented): ✘ `SUS_STAT_NONE`, `SUS_STAT_STR`, `SUS_STAT_INT`, `SUS_STAT_WIS`, `SUS_STAT_DEX`, `SUS_STAT_CON`, `SUS_STAT_MAX`
- Fields (1/1 documented): ✔ `sustainValue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `SustainStat(int)` | on 261004 | ✔ |
| `getSustainFromStat(Stats)` | on 261004 | ✔ |
| `getSustainFromInt(int)` | on 261004 | ✔ |
| `getSustainValue()` | on 261004 | ✔ |


##### `TimedEffect.java`

**Responsible for:** The catalogue of timed player statuses — the TMD_* effects such as haste, fear, poison, temporary resistances and the brand/attack buffs — each recording the screen redraws and model recalculations its onset or expiry should trigger.

**enum `TimedEffect`** — class Javadoc: ✔ · type stamp: on 260817

- Enum constants (54/54 documented): ✔ `TMD_NONE`, `TMD_FAST`, `TMD_SLOW`, `TMD_BLIND`, `TMD_PARALYZED`, `TMD_CONFUSED`, `TMD_AFRAID`, `TMD_IMAGE`, `TMD_POISONED`, `TMD_CUT`, `TMD_STUN`, `TMD_FOOD`, `TMD_PROTEVIL`, `TMD_INVULN`, `TMD_HERO`, `TMD_SHERO`, `TMD_SHIELD`, `TMD_BLESSED`, `TMD_SINVIS`, `TMD_SINFRA`, `TMD_OPP_ACID`, `TMD_OPP_ELEC`, `TMD_OPP_FIRE`, `TMD_OPP_COLD`, `TMD_OPP_POIS`, `TMD_OPP_CONF`, `TMD_AMNESIA`, `TMD_TELEPATHY`, `TMD_STONESKIN`, `TMD_TERROR`, `TMD_SPRINT`, `TMD_BOLD`, `TMD_SCRAMBLE`, `TMD_TRAPSAFE`, `TMD_FASTCAST`, `TMD_ATT_ACID`, `TMD_ATT_ELEC`, `TMD_ATT_FIRE`, `TMD_ATT_COLD`, `TMD_ATT_POIS`, `TMD_ATT_CONF`, `TMD_ATT_EVIL`, `TMD_ATT_DEMON`, `TMD_ATT_VAMP`, `TMD_HEAL`, `TMD_COMMAND`, `TMD_ATT_RUN`, `TMD_COVERTRACKS`, `TMD_POWERSHOT`, `TMD_TAUNT`, `TMD_BLOODLUST`, `TMD_BLACKBREATH`, `TMD_STEALTH`, `TMD_FREE_ACT`
- Fields (2/2 documented): ✔ `redrawFlags`, `updateFlags`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TimedEffect(PlayerRedraw[], PlayerUpdateEnum[])` | — | ✔ |
| `getRedrawFlags()` | on 260817 | ✔ |
| `getUpdateFlags()` | on 260817 | ✔ |


##### `TimedEffectReasonType.java`

**Responsible for:** Discriminator describing what kind of game condition a single uk.co.jackoftradesltd.middle.player.TimedFailure entry refers to.

**enum `TimedEffectReasonType`** — class Javadoc: ✔ · type stamp: —

- Enum constants (6/6 documented): ✔ `TYPE_NONE`, `TYPE_OBJECT_FLAG`, `TYPE_RESIST`, `TYPE_VULN`, `TYPE_PLAYER_FLAG`, `TYPE_TIMED_EFFECT`
- Fields (1/1 documented): ✔ `value`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TimedEffectReasonType(int)` | — | ✔ |
| `fromValue(int)` | — | ✔ |


## middle/strings

##### `MessageTag.java`

**Responsible for:** The tags that can appear in braces in a custom message, the port of C's msg_tag_t (obj-util.c), as read by ItemObject#printCustomMessage.

**enum `MessageTag`** — class Javadoc: ✔ · type stamp: on 261003

- Enum constants (5/5 documented): ✔ `MSG_TAG_NONE`, `MSG_TAG_NAME`, `MSG_TAG_KIND`, `MSG_TAG_VERB`, `MSG_TAG_VERB_IS`
- Fields (1/1 documented): ✔ `size`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MessageTag(int)` | on 261003 | ✔ |
| `getTag(String)` | on 261003 | ✔ |
| `getSize()` | on 261003 | ✔ |


##### `Quark.java`

**Responsible for:** The "quark" table: a deduplicating store that maps small integer keys to strings.

**class `Quark`** — class Javadoc: ✔ · type stamp: before 261012

- Fields (3/3 documented): ✔ `name`, `quarks`, `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `add(String)` | on 261012 | ✔ |
| `merge(Quark)` | before 261012 | ✔ |
| `getQuark(int)` | before 261012 | ✔ |
| `size()` | before 261012 | ✔ |
| `getName()` | before 261012 | ✔ |
| `init()` | before 261012 | ✔ |
| `cleanup()` | before 261012 | ✔ |
| `containsText(String)` | before 261012 | ✔ |


##### `TextBlock.java`

**Responsible for:** T block of text to be displayed straight to the screen.

**class `TextBlock`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `textAndAttributes`, `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `TextBlock()` | ✔ | ✔ |
| `convertToString()` | ✔ | ✔ |
| `append(char, ColourEnum)` | ✔ | ✔ |
| `append(AngbandDisplayCharacter)` | ✔ | ✔ |
| `append(TextBlock)` | ✔ | ✔ |
| `append(String, Object)` | ✔ | ✔ |
| `append(ColourEnum, String, Object)` | ✔ | ✔ |
| `length()` | ✔ | ✔ |
| `calculateLines(int)` | ✔ | ✔ |
| `append(String, List<ColourEnum>)` | ✔ | ✔ |
| `toFile(AngFile, int, int)` | ✔ | ✔ |
| `toString()` | ✔ | ✔ |


## middle/utils

##### `ControlUtils.java`

**Responsible for:** Static control/messaging helpers ported from the original C source's plog/quit family (src/z-util.c).

**class `ControlUtils`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plogFmt(String)` | — | ✔ |
| `plogFmt(String, Object)` | — | ✔ |
| `plog(String)` | — | ✔ |
| `quitFmt(String, Object)` | — | ✔ |
| `quit()` | — | ✔ |
| `quit(String)` | — | ✔ |


##### `NumberUtils.java`

**Responsible for:** Static integer and statistics helpers, the Java form of the arithmetic utilities in z-util.c: the four saturating "guard" functions (add_guardi, sub_guardi, add_guardi16, sub_guardi16), mean and variance.

**class `NumberUtils`** — class Javadoc: ✔ · type stamp: before 261014

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `NumberUtils()` | before 261014 | ✔ |
| `addGuardI(int, int)` | on 260830 | ✔ |
| `subGuardI(int, int)` | on 260830 | ✔ |
| `addGuardI16(int, int)` | before 261014 | ✔ |
| `subGuardI16(int, int)` | before 261014 | ✔ |
| `mean(ArrayList<Integer>, int)` | before 261014 | ✔ |
| `variance(ArrayList<Integer>, int, boolean, boolean)` | before 261014 | ✔ |
| `cmp(int, int)` | before 261014 | ✔ |


## middle/utils/quit

##### `Quit.java`

**Responsible for:** Default QuitAux implementation: the standard way the game terminates.

**class `Quit`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `quit(String)` | ✔ | ✔ |

**class `Quit.GameQuitException`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `GameQuitException(String)` | ✔ | ✔ |


##### `QuitAux.java`

**Responsible for:** Strategy interface for terminating the game, mirroring the swappable quit_aux function pointer in the original C source.

**interface `QuitAux`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `quit(String)` | — | ✔ |


## unused

##### `EventDataBirthStage.java`

**Responsible for:** GameEventData payload describing one stage of character creation ("birth") — a hint plus the list of choices (with help text) the player must pick from, the default selection, and any extra stage-specific data.

**class `EventDataBirthStage`** — class Javadoc: ✔ · type stamp: —

- Fields (7/7 documented): ✔ `reset`, `hint`, `nChoices`, `initialChoice`, `choices`, `helpTexts`, `xtra`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `EventDataBirthStage(boolean, String, int, int, ArrayList<String>, ArrayList<String>, Object)` | — | ✔ |
| `isReset()` | — | ✔ |
| `getHint()` | — | ✔ |
| `getnChoices()` | — | ✔ |
| `getInitialChoice()` | — | ✔ |
| `getChoices()` | — | ✔ |
| `getHelpTexts()` | — | ✔ |
| `getXtra()` | — | ✔ |


##### `Rect.java`

**Responsible for:** An immutable axis-aligned rectangle described by its four edge coordinates, used by the screen/terminal layer to express regions of the display.

**class `Rect`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `left`, `top`, `right`, `bottom`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Rect(long, long, long, long)` | — | ✔ |
| `getLeft()` | — | ✔ |
| `getTop()` | — | ✔ |
| `getRight()` | — | ✔ |
| `getBottom()` | — | ✔ |


##### `UIEntryPredicate.java`

**Responsible for:** Empty marker interface left in the `unused` package; no members.

**interface `UIEntryPredicate`** — class Javadoc: ✘ · type stamp: —

