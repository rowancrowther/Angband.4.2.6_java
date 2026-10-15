# Code inventory — `src/test/java`

*Generated 2026-10-14 from a real Java parse (JavaParser 3.27.1, Java 21 language level), not a regex sweep.*

## How to read this

- One section per package, one sub-section per file; paths are relative to `src/test/java/uk/co/jackoftradesltd/`.
- **Responsible for** is the first sentence of the top type's Javadoc; the 1 files with no class Javadoc (or no type at all, for `module-info.java`) have a hand-written summary.
- **Function** rows cover every method and constructor (including private and abstract ones, and those in nested types). Initialisers, lambdas and anonymous-class / enum-constant-body members are not listed.
- **Ported (coded)** is read from the house provenance stamp in the member's Javadoc (`coded on YYMMDD` = written that day; `coded before YYMMDD` = predates that day's stamping). `—` means the member carries no stamp. It is not read from `docs/ported_functions.md` (that file is keyed on C functions).
- **Javadoc** ✔ = a `/** … */` block is attached to the declaration. ✘ (override) = an `@Override` method with no block of its own. A block is counted regardless of its quality — see `docs/no_low_javadoc.md` for the thin-block sweep.
- Fields, enum constants and record components are shown as compact lists: ✔ documented, ✘ undocumented. Record components count as documented when the record's Javadoc has a matching `@param`.
- ANTLR-generated files (any file with a `// Generated from … by ANTLR` header) are excluded.

## Totals

| Measure | Count | Documented |
|---|---|---|
| Files | 666 | — |
| Types (class/interface/enum/record, incl. nested) | 1927 | 1763 with Javadoc |
| Functions (methods + constructors) | 11763 | 6404 with Javadoc |
| — of which stamped `coded on`/`coded before` | 156 | `coded on`: 155 |
| Fields | 2154 | 940 with Javadoc |
| Enum constants | 7 | 4 with Javadoc |
| Record components | 112 | 38 with `@param` |

## Packages

| Package | Files | Types | Types with Javadoc | Functions | Functions with Javadoc | Functions stamped |
|---|---|---|---|---|---|---|
| [`(source root)`](#source-root) | 2 | 11 | 11 | 56 | 51 | 0 |
| [`backend/io`](#backendio) | 2 | 2 | 2 | 28 | 1 | 0 |
| [`backend/io/savefiles`](#backendiosavefiles) | 2 | 2 | 2 | 12 | 0 | 0 |
| [`backend/parser`](#backendparser) | 61 | 66 | 66 | 1187 | 362 | 3 |
| [`backend/parser/grammars/gameconstants`](#backendparsergrammarsgameconstants) | 2 | 3 | 3 | 17 | 3 | 0 |
| [`backend/parser/grammars/imports/effectblock`](#backendparsergrammarsimportseffectblock) | 2 | 3 | 3 | 24 | 5 | 0 |
| [`backend/parser/grammars/projection`](#backendparsergrammarsprojection) | 2 | 3 | 3 | 14 | 2 | 0 |
| [`backend/parser/grammars/uientry`](#backendparsergrammarsuientry) | 1 | 2 | 2 | 16 | 1 | 0 |
| [`backend/parser/grammars/uientrybase`](#backendparsergrammarsuientrybase) | 2 | 3 | 3 | 17 | 2 | 0 |
| [`backend/parser/grammars/uientryrenderer`](#backendparsergrammarsuientryrenderer) | 2 | 3 | 3 | 17 | 2 | 0 |
| [`backend/parser/grammars/world`](#backendparsergrammarsworld) | 2 | 3 | 3 | 14 | 3 | 0 |
| [`backend/parser/playerclass`](#backendparserplayerclass) | 1 | 1 | 1 | 22 | 0 | 0 |
| [`backend/parser/playertimed`](#backendparserplayertimed) | 1 | 4 | 4 | 16 | 15 | 0 |
| [`backend/parser/quest`](#backendparserquest) | 1 | 1 | 1 | 24 | 17 | 0 |
| [`channel`](#channel) | 1 | 7 | 7 | 22 | 21 | 0 |
| [`channel/colour`](#channelcolour) | 1 | 4 | 4 | 23 | 0 | 0 |
| [`channel/directories`](#channeldirectories) | 1 | 1 | 1 | 11 | 10 | 0 |
| [`channel/enums`](#channelenums) | 1 | 6 | 6 | 15 | 12 | 0 |
| [`channel/globals`](#channelglobals) | 1 | 1 | 0 | 7 | 0 | 0 |
| [`channel/messages`](#channelmessages) | 1 | 3 | 3 | 20 | 15 | 0 |
| [`channel/messages/data`](#channelmessagesdata) | 3 | 13 | 13 | 61 | 53 | 0 |
| [`channel/parser`](#channelparser) | 2 | 2 | 2 | 10 | 0 | 0 |
| [`channel/strings`](#channelstrings) | 1 | 3 | 3 | 15 | 0 | 0 |
| [`channel/uichannel`](#channeluichannel) | 1 | 1 | 1 | 4 | 4 | 4 |
| [`channel/utils`](#channelutils) | 3 | 16 | 16 | 91 | 5 | 0 |
| [`channel/utils/combiners`](#channelutilscombiners) | 1 | 10 | 10 | 55 | 3 | 0 |
| [`frontend`](#frontend) | 4 | 4 | 4 | 26 | 19 | 0 |
| [`frontend/colour`](#frontendcolour) | 3 | 3 | 3 | 19 | 1 | 0 |
| [`frontend/entries`](#frontendentries) | 4 | 4 | 4 | 35 | 0 | 0 |
| [`frontend/entries/enums`](#frontendentriesenums) | 1 | 1 | 1 | 8 | 0 | 0 |
| [`frontend/events`](#frontendevents) | 5 | 6 | 6 | 30 | 19 | 0 |
| [`frontend/globals`](#frontendglobals) | 1 | 1 | 1 | 6 | 6 | 0 |
| [`frontend/inputfromuser`](#frontendinputfromuser) | 1 | 7 | 7 | 29 | 22 | 0 |
| [`frontend/screen`](#frontendscreen) | 14 | 18 | 14 | 130 | 96 | 0 |
| [`frontend/screen/enums`](#frontendscreenenums) | 1 | 4 | 2 | 16 | 0 | 0 |
| [`frontend/screen/grid`](#frontendscreengrid) | 5 | 16 | 6 | 79 | 19 | 0 |
| [`frontend/screen/handlers`](#frontendscreenhandlers) | 6 | 6 | 6 | 145 | 77 | 0 |
| [`frontend/screen/hooks`](#frontendscreenhooks) | 3 | 3 | 3 | 15 | 12 | 0 |
| [`frontend/sounds`](#frontendsounds) | 1 | 1 | 1 | 3 | 3 | 0 |
| [`frontend/splash`](#frontendsplash) | 1 | 1 | 1 | 21 | 1 | 0 |
| [`frontend/ui`](#frontendui) | 4 | 29 | 28 | 277 | 259 | 0 |
| [`frontend/ui/entry`](#frontenduientry) | 2 | 2 | 2 | 12 | 0 | 0 |
| [`frontend/ui/entry/assembler/helperfunctions`](#frontenduientryassemblerhelperfunctions) | 3 | 3 | 3 | 23 | 0 | 0 |
| [`frontend/ui/globals`](#frontenduiglobals) | 2 | 8 | 5 | 42 | 10 | 0 |
| [`frontend/ui/output`](#frontenduioutput) | 1 | 1 | 1 | 7 | 0 | 0 |
| [`frontend/ui/player`](#frontenduiplayer) | 2 | 2 | 2 | 22 | 21 | 0 |
| [`middle`](#middle) | 5 | 6 | 6 | 74 | 33 | 0 |
| [`middle/cave`](#middlecave) | 34 | 122 | 121 | 660 | 487 | 0 |
| [`middle/cave/chunkbuilders`](#middlecavechunkbuilders) | 1 | 1 | 1 | 6 | 0 | 0 |
| [`middle/cave/enums`](#middlecaveenums) | 5 | 9 | 9 | 39 | 5 | 0 |
| [`middle/cave/profiles/dungeon`](#middlecaveprofilesdungeon) | 4 | 4 | 4 | 16 | 16 | 0 |
| [`middle/cave/profiles/room`](#middlecaveprofilesroom) | 1 | 1 | 1 | 7 | 6 | 0 |
| [`middle/cave/profiles/vault`](#middlecaveprofilesvault) | 1 | 1 | 1 | 5 | 4 | 0 |
| [`middle/cave/roombuilders`](#middlecaveroombuilders) | 1 | 1 | 1 | 10 | 10 | 0 |
| [`middle/combat`](#middlecombat) | 3 | 3 | 3 | 48 | 10 | 0 |
| [`middle/combat/enums`](#middlecombatenums) | 1 | 1 | 1 | 8 | 2 | 0 |
| [`middle/effect`](#middleeffect) | 2 | 11 | 11 | 44 | 44 | 0 |
| [`middle/enums`](#middleenums) | 4 | 8 | 8 | 34 | 23 | 1 |
| [`middle/game`](#middlegame) | 5 | 20 | 20 | 181 | 168 | 0 |
| [`middle/game/enums`](#middlegameenums) | 4 | 4 | 4 | 16 | 0 | 0 |
| [`middle/game/event`](#middlegameevent) | 3 | 5 | 5 | 44 | 28 | 0 |
| [`middle/game/event/eventhandlers`](#middlegameeventeventhandlers) | 3 | 5 | 5 | 26 | 21 | 0 |
| [`middle/game/event/projection`](#middlegameeventprojection) | 3 | 7 | 3 | 31 | 11 | 11 |
| [`middle/game/gameengine`](#middlegamegameengine) | 16 | 42 | 38 | 252 | 114 | 0 |
| [`middle/game/gameengine/argumentdata`](#middlegamegameengineargumentdata) | 7 | 7 | 7 | 103 | 0 | 0 |
| [`middle/game/globals`](#middlegameglobals) | 4 | 4 | 4 | 40 | 30 | 0 |
| [`middle/game/globals/cached`](#middlegameglobalscached) | 1 | 5 | 1 | 42 | 6 | 0 |
| [`middle/game/globals/data`](#middlegameglobalsdata) | 1 | 4 | 4 | 26 | 26 | 26 |
| [`middle/game/globals/loaders`](#middlegameglobalsloaders) | 15 | 26 | 26 | 204 | 85 | 0 |
| [`middle/game/globals/registry`](#middlegameglobalsregistry) | 19 | 50 | 33 | 449 | 120 | 0 |
| [`middle/gameinput`](#middlegameinput) | 3 | 4 | 4 | 34 | 1 | 0 |
| [`middle/magic`](#middlemagic) | 4 | 9 | 7 | 61 | 35 | 0 |
| [`middle/monsters`](#middlemonsters) | 25 | 38 | 38 | 270 | 149 | 9 |
| [`middle/monsters/enums`](#middlemonstersenums) | 7 | 10 | 10 | 58 | 40 | 36 |
| [`middle/numerics`](#middlenumerics) | 7 | 41 | 41 | 226 | 63 | 0 |
| [`middle/objects`](#middleobjects) | 116 | 446 | 424 | 2304 | 1447 | 7 |
| [`middle/objects/enums`](#middleobjectsenums) | 22 | 33 | 33 | 187 | 124 | 48 |
| [`middle/player`](#middleplayer) | 156 | 648 | 564 | 3217 | 2049 | 10 |
| [`middle/player/enums`](#middleplayerenums) | 9 | 21 | 16 | 76 | 26 | 0 |
| [`middle/strings`](#middlestrings) | 2 | 6 | 6 | 34 | 4 | 0 |
| [`middle/utils`](#middleutils) | 4 | 26 | 26 | 128 | 8 | 0 |
| [`testsupport`](#testsupport) | 4 | 4 | 4 | 60 | 57 | 1 |

## (source root)

##### `BoundaryTest.java`

**Responsible for:** The boundary test: the architecture's one structural rule, enforced by reading the source rather than by remembering to.

**class `BoundaryTest`** — class Javadoc: ✔ · type stamp: —

- Fields (7/7 documented): ✔ `OURS`, `HALVES`, `REFERENCE`, `FRONTEND_BASELINE`, `MIDDLE_BASELINE`, `BACKEND_BASELINE`, `sources`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `projectRoot()` | — | ✔ |
| `sourceRoot()` | — | ✔ |
| `sources()` | — | ✔ |
| `violations(String, PermittedTarget)` | — | ✔ |
| `assertOnlyBaselineCrossings(String, String, PermittedTarget, Set<String>, String)` | — | ✔ |
| `reportArrived(String, List<Violation>, String)` | — | ✔ |
| `reportDeparted(List<String>, String)` | — | ✔ |

**interface `BoundaryTest.PermittedTarget`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `permits(String)` | — | ✔ |

**record `BoundaryTest.Reference`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `name`, `half`, `line`

**record `BoundaryTest.Violation`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `path`, `reference`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `key()` | — | ✔ |

**record `BoundaryTest.SourceFile`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `relativePath`, `half`, `references`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `read(Path, Path)` | — | ✔ |
| `referencesIn(String)` | — | ✔ |
| `lineOf(String, int)` | — | ✔ |
| `blankCommentsAndLiterals(String)` | — | ✔ |
| `blankTextBlock(char[], int)` | — | ✔ |
| `blankSimpleLiteral(char[], int, char)` | — | ✔ |

**class `BoundaryTest.TheScanItself`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `findsTheSourceTree()` | — | ✔ |
| `seesAllFourHalves()` | — | ✔ |
| `seesReferencesInCode()` | — | ✔ |
| `ignoresLineComments()` | — | ✔ |
| `ignoresBlockCommentsAndJavadoc()` | — | ✔ |
| `ignoresStringAndCharacterLiterals()` | — | ✔ |
| `isNotFooledByACommentMarkerInsideAString()` | — | ✔ |
| `keepsLineNumbersTrueThroughMultiLineConstructs()` | — | ✔ |
| `attributesNamesToTheHalfTheyPointAt()` | — | ✔ |
| `aBaselineKeyIgnoresTheLineTheCrossingSitsOn()` | — | ✔ |
| `everyBaselineEntryIsWellFormed()` | — | ✔ |

**class `BoundaryTest.TheFrontEndSeesOnlyTheChannel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `frontendNamesNothingOfOursOutsideChannelBeyondTheBaseline()` | — | ✘ |

**class `BoundaryTest.TheCoreDoesNotKnowTheFrontEnd`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `middleNamesNothingInFrontendBeyondTheBaseline()` | — | ✘ |
| `backendNamesNothingInFrontendBeyondTheBaseline()` | — | ✘ |

**class `BoundaryTest.TheRulesAreDocumented`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theMigrationDocumentStillStatesTheThreeRules()` | — | ✘ |
| `assumeDocumentExists(Path)` | — | ✔ |


##### `MainTest.java`

**Responsible for:** Unit tests for Main.checkDirectoryOption, the validation behind the -d = switch - C's rewriting of an ANGBAND_DIR_* buffer in main() ([C] src/main.c).

**class `MainTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aWellFormedOverrideOfAKnownDirectoryIsAccepted(Path)` | — | ✔ |
| `everyKnownDirectoryNameIsAcceptedOnTheCommandLine(AngbandDirs.ANGBAND_DIRS, Path)` | — | ✔ |
| `aKnownDirectoryNameIsMatchedCaseInsensitively(Path)` | — | ✔ |
| `aValidOptionChangesNothing(Path)` | — | ✔ |
| `anArgumentThatIsNotNameEqualsPathIsRejected(String)` | — | ✔ |
| `aPathContainingAnEqualsSignIsAccepted(Path)` | — | ✔ |
| `anEmptyPathIsRejectedRatherThanTakenAsTheWorkingDirectory(String)` | — | ✔ |
| `anUnknownDirectoryNameIsRejectedAndNamed(String)` | — | ✔ |
| `anEmptyDirectoryNameIsRejected()` | — | ✔ |
| `aPathThatDoesNotExistIsCreated(Path)` | — | ✔ |
| `theNameIsCheckedBeforeThePath(Path)` | — | ✔ |
| `aPlainFileIsRejectedWhereADirectoryIsRequired(Path)` | — | ✔ |
| `aTrailingCharacterAfterASingleLetterSwitchIsRejected(String)` | — | ✔ |

**class `MainTest.TheUiHalfsCrashPath`** — class Javadoc: ✔ · type stamp: —

- Fields (4/6 documented): ✔ `SHOULD_FINISH_MILLIS`, `NO_OPTIONS`, `escaped`, `framesBefore` — ✘ `channels`, `uiThread`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✔ |
| `startTheUiHalf()` | — | ✔ |
| `framesCreatedByThisTest()` | — | ✔ |
| `awaitTheGameWindow()` | — | ✔ |
| `awaitDisposalOf(Frame)` | — | ✔ |
| `crashTheLoop()` | — | ✔ |
| `awaitSaveAndStop()` | — | ✔ |
| `aCrashSendsSaveAndStopToTheCore()` | — | ✔ |
| `aCrashDisposesTheWindow()` | — | ✔ |
| `aCrashEndsTheThreadByReturningNotThrowing()` | — | ✔ |
| `theCleanPathDisposesTheWindowThroughTheSameFinally()` | — | ✔ |


## backend/io

##### `AngDirTest.java`

**Responsible for:** Unit tests for AngDir, the port of C's ang_dir/my_dopen/ alter_ang_dir_only_files/my_dread (src/z-file.c).

**class `AngDirTest`** — class Javadoc: ✔ · type stamp: on 260914

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `drain(AngDir)` | — | ✔ |
| `filesOnlyByDefaultSkipsSubdirectories(Path)` | — | ✘ |
| `readReturnsBareFileNamesNotFullPaths(Path)` | — | ✘ |
| `exhaustedDirectoryKeepsReturningEmptyString(Path)` | — | ✘ |
| `aBrokenSymlinkIsSkippedRatherThanReturned(Path)` | — | ✘ |
| `alterOnlyFilesReturnsThePreviousValue(Path)` | — | ✘ |
| `clearingOnlyFilesYieldsDotAndDotDotFirst(Path)` | — | ✘ |
| `clearingOnlyFilesAlsoYieldsSubdirectories(Path)` | — | ✘ |
| `angDirFactoryReturnsNullForAMissingDirectory(Path)` | — | ✘ |


##### `AngFileTest.java`

**Responsible for:** Unit tests for AngFile, the port of C's ang_file abstraction (src/z-file.c).

**class `AngFileTest`** — class Javadoc: ✔ · type stamp: on 260914

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `openCreatesAndOpensAMissingFileWhenModeIsWrite(Path)` | — | ✘ |
| `openCreatesAndOpensAMissingFileWhenModeIsAppend(Path)` | — | ✘ |
| `openFailsAndDoesNotCreateAMissingFileWhenModeIsRead(Path)` | — | ✘ |
| `openOpensAnExistingFileDirectlyWithoutRecreatingIt(Path)` | — | ✘ |
| `writeCharLazilyOpensWithThisFilesOwnModeNotAHardcodedOne(Path)` | — | ✘ |
| `writeCharRefusesAndTouchesNothingWhenModeIsRead(Path)` | — | ✘ |
| `readCharDistinguishesARealByteFromEndOfFile(Path)` | — | ✘ |
| `readCharDistinguishesANulByteFromEndOfFile(Path)` | — | ✘ |
| `readReturnsTheRequestedByteCountFromAnExistingFile(Path)` | — | ✘ |
| `writeThenGetLineRoundTrips(Path)` | — | ✘ |
| `writeRefusesAndTouchesNothingWhenModeIsRead(Path)` | — | ✘ |
| `putLineRefusesAndTouchesNothingWhenModeIsRead(Path)` | — | ✘ |
| `putFormattedLineFormatsThenWrites(Path)` | — | ✘ |
| `dirExistsIsFalseForAPlainFile(Path)` | — | ✘ |
| `dirExistsIsTrueForADirectory(Path)` | — | ✘ |
| `dirExistsIsFalseForAMissingPath(Path)` | — | ✘ |
| `dirCreateCreatesMissingParentDirectories(Path)` | — | ✘ |
| `fileSkipSkipsForwardPastBytesInAnOpenStream(Path)` | — | ✘ |
| `fileSkipCanSeekBackwardsLikeCsFseek(Path)` | — | ✘ |


## backend/io/savefiles

##### `SavefileDetailsTest.java`

**Responsible for:** Unit tests for SavefileDetails, the port of C's struct savefile_details (src/ui-game.h).

**class `SavefileDetailsTest`** — class Javadoc: ✔ · type stamp: on 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultConstructorMatchesTheZeroAllocatedCStruct()` | — | ✘ |
| `fileNameRoundTripsThroughSetterAndGetter()` | — | ✘ |
| `descriptionRoundTripsThroughSetterAndGetter()` | — | ✘ |
| `offsetRoundTripsThroughSetterAndGetter()` | — | ✘ |
| `offsetMatchesTheLengthOfTheSetgidUserPrefixWhenSet()` | — | ✘ |


##### `SavefileGetterImplTest.java`

**Responsible for:** Unit tests for SavefileGetterImpl, the port of C's struct savefile_getter_impl together with got_savefile() (src/ui-game.c).

**class `SavefileGetterImplTest`** — class Javadoc: ✔ · type stamp: on 260915

- Fields (0/1 documented): ✘ `originalSavePath`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointSaveDirAt(Path)` | — | ✘ |
| `rememberOriginalSavePath()` | — | ✘ |
| `restoreOriginalSavePath()` | — | ✘ |
| `gotSavefileReturnsFalseRepeatedlyWhenTheDirectoryCannotBeOpened(Path)` | — | ✘ |
| `gotSavefileReturnsFalseRepeatedlyForAnEmptyDirectory(Path)` | — | ✘ |
| `gotSavefileReturnsTrueOnceForEachSavefileThenFalse(Path)` | — | ✘ |
| `gotSavefileSkipsSubdirectoriesInTheSaveDirectory(Path)` | — | ✘ |


## backend/parser

##### `ActivationAssemblerTest.java`

**Responsible for:** Unit tests for ActivationAssembler (grammar-suite assembler track).

**class `ActivationAssemblerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rec(String, String, String, String, int)` | — | ✔ |
| `cleanRecordAssemblesToOneActivation()` | — | ✘ |
| `absentLevelAndPowerDefaultToZeroWithoutError()` | — | ✘ |
| `malformedLevelIsReportedAndRecordSkipped()` | — | ✘ |
| `malformedPowerIsReportedAndRecordSkipped()` | — | ✘ |
| `oneBadRecordDoesNotStopTheRest()` | — | ✘ |


##### `ActivationReaderTest.java`

**Responsible for:** Reader/assembly tests for ActivationReader (grammar-suite reader-track R4).

**class `ActivationReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `REAL_FILE` — ✘ `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✔ |
| `parseWithResultLoadsAllRecordsCleanly()` | — | ✘ |
| `parseShimReturnsTheItems()` | — | ✘ |
| `unresolvableEffectDropsTheWholeActivation()` | — | ✘ |
| `parseErrorYieldsNoItemsAndAReportedError()` | — | ✘ |
| `oversizedLevelIsReportedAsMalformedThroughTheFullPipeline()` | — | ✘ |
| `recordCountMismatchIsReportedButRecordsStillLoad()` | — | ✘ |


##### `ArtifactReaderTest.java`

**Responsible for:** End-to-end throughput tests for ArtifactReader: file text -> ArtifactLexer/ ArtifactGrammar -> uk.co.jackoftradesltd.backend.parser.artifact.ArtifactParseRecord -> uk.co.jackoftradesltd.backend.parser.artifact.ArtifactAssembler -> Artifact.

**class `ArtifactReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/27 documented): ✔ `ORDINARY_KIND_MAX`, `SYNTHESISED_KINDS`, `result` — ✘ `ARTIFACT_FILE`, `PAIN_FILE`, `BASE_FILE`, `SUMMON_FILE`, `SLAY_FILE`, `BRAND_FILE`, `OBJECT_BASE_FILE`, `OBJECT_FILE`, `SHAPE_FILE`, `CURSE_FILE`, `ACTIVATION_FILE`, `savedPains`, `savedBases`, `savedSummons`, `savedSlays`, `savedBrands`, `savedObjectBases`, `savedCurses`, `savedActivations`, `savedObjectKinds`, `savedKindsByTvalSval`, `savedObjectBaseKindMax`, `savedPlayerShapes`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✔ |
| `load(String, String)` | — | ✔ |
| `withHeader(int, String)` | — | ✘ |
| `hasError(ParseResult<Artifact>, String)` | — | ✔ |
| `setStatic(String, Object)` | — | ✘ |
| `field(Object, String)` | — | ✘ |
| `kindField(ObjectKind, String)` | — | ✔ |
| `byName(String)` | — | ✘ |
| `theRealFileLoadsAll138ArtifactsWithNoErrorsAndTheirFieldsAreCorrect()` | — | ✘ |
| `everyArtifactIgnoresTheFourBaseElements()` | — | ✔ |
| `aBaseElementResistMergesWithTheIgnoreSweepRatherThanReplacingIt()` | — | ✔ |
| `exactlyTheFourteenUnresolvableBaseObjectsGetASynthesisedKind()` | — | ✔ |
| `thePhialsSynthesisedKindTakesItsNameFromTheSvalAndItsFieldsFromTheArtifact()` | — | ✔ |
| `grondIsInstaArtButNotSynthesisedSoItsKindKeepsObjectTxtsWeightAndCost()` | — | ✔ |
| `everyReachableErrorSiteIsReportedAndTheBadArtifactDropped()` | — | ✘ |
| `twoBadFlagsOnOneArtifactAreBothReported()` | — | ✘ |


##### `BlowEffectReaderTest.java`

**Responsible for:** End-to-end throughput tests for BlowEffectReader: file text -&gt; BlowEffectLexer/BlowEffectGrammar -&gt; BlowEffectParseRecord -&gt; BlowEffectAssembler -&gt; BlowEffect.

**class `BlowEffectReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `REAL_FILE`, `PROJECTION_FILE`, `savedProjections`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✔ |
| `byName(List<BlowEffect>, String)` | — | ✘ |
| `realFile()` | — | ✘ |
| `cleanLoadOfTheRealFileYieldsThirtyBlowEffects()` | — | ✘ |
| `everyLashTypeInTheRealFileResolvesToAProjection()` | — | ✘ |
| `lashTypeResolvesThroughTheProjectionCodeNotTheLashDescription()` | — | ✘ |
| `unresolvableLashTypeFallsBackToMissileRatherThanErroring()` | — | ✘ |
| `elementEffectTypeResolvesResistToAnElement()` | — | ✘ |
| `flagEffectTypeResolvesResistToAnObjectFlag()` | — | ✘ |
| `everyElementAndFlagResistInTheRealFileResolves()` | — | ✘ |
| `allSustainsEffectTypeResolvesAndCarriesNoResist()` | — | ✘ |
| `recordWithNoEffectTypeKeepsNullTypeAndEmptyResists()` | — | ✘ |
| `powerAndEvalAreParsedAsIntegers()` | — | ✘ |


##### `BlowMethodReaderTest.java`

**Responsible for:** End-to-end throughput tests for BlowMethodReader: file text -&gt; BlowMethodLexer/BlowMethodGrammar -&gt; BlowMethodParseRecord -&gt; BlowMethodAssembler -&gt; BlowMethod.

**class `BlowMethodReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✔ |
| `entry(String, int, int, int, int, String, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `cleanLoadOfTheRealFileYieldsNineteenBlowMethods()` | — | ✘ |
| `firstMethodHitHasAllEightFieldsPopulated()` | — | ✘ |
| `everyMessageTypeInTheRealFileResolves()` | — | ✘ |
| `repeatedActLinesAllAccumulateInOrder()` | — | ✘ |
| `nonPhysicalNonMissingMethodReadsAllFourFlagsFalse()` | — | ✘ |
| `omittedMsgLineDefaultsToGeneric()` | — | ✘ |
| `bareMsgLineWithNoValueDefaultsToGeneric()` | — | ✘ |
| `unresolvableMsgIsASoftErrorAndTheRecordIsSkipped()` | — | ✘ |
| `oneBadMsgDoesNotStopLaterRecordsLoading()` | — | ✘ |
| `anyNonZeroValueMakesABooleanDirectiveTrue()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordsStillLoad()` | — | ✘ |
| `missingDescIsAHardError()` | — | ✘ |
| `outOfOrderDirectivesAreAHardError()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `zeroActLinesIsAccepted()` | — | ✘ |


##### `BodyReaderTest.java`

**Responsible for:** End-to-end throughput tests for BodyReader: file text -> BodyLexer/ BodyGrammar -> uk.co.jackoftradesltd.backend.parser.body.BodyParseRecord -> uk.co.jackoftradesltd.backend.parser.body.BodyAssembler -> PlayerBody.

**class `BodyReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `byName(List<PlayerBody>, String)` | — | ✘ |
| `realFileLoadsOneHumanoidBodyWithNoErrors()` | — | ✘ |
| `slotsKeepDeclaredOrderWithResolvedTypesAndNames()` | — | ✘ |
| `duplicateRingSlotsAreBothKeptDistinctByName()` | — | ✘ |
| `multipleBodiesEachLoadWithTheirOwnSlots()` | — | ✘ |
| `invalidSlotTypeRejectsTheWholeBody()` | — | ✘ |
| `oneBadBodyIsDroppedButOtherBodiesStillLoad()` | — | ✘ |
| `recordCountMismatchIsReportedButValidBodyStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `bodyWithNoSlotsIsAHardError()` | — | ✘ |
| `slotWithoutAPrecedingBodyIsAHardError()` | — | ✘ |


##### `BrandReaderTest.java`

**Responsible for:** End-to-end throughput tests for BrandReader: file text -> BrandLexer/ BrandGrammar -> BrandParseRecord -> BrandAssembler -> Brand.

**class `BrandReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✘ |
| `brand(String, String, String, String, String, String, String, String)` | — | ✔ |
| `validBrand(String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `realFileLoadsAllTenBrandsWithNoErrors()` | — | ✘ |
| `syntheticBrandWithVulnFlagAssembles()` | — | ✘ |
| `syntheticBrandWithoutVulnFlagUsesTheNoneSentinel()` | — | ✘ |
| `illegalResistFlagIsReportedAndSkipped()` | — | ✘ |
| `illegalVulnFlagIsReportedAndSkipped()` | — | ✘ |
| `overflowingMultiplierIsReportedAndSkipped()` | — | ✘ |
| `overflowingOMultiplierIsReportedAndSkipped()` | — | ✘ |
| `overflowingPowerIsReportedAndSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `missingCodeHeaderIsAHardError()` | — | ✘ |
| `twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads()` | — | ✘ |


##### `ChestTrapAssemblerTest.java`

**Responsible for:** Unit tests for ChestTrapAssembler, driven from hand-built ChestTrapParseRecords rather than through the grammar.

**class `ChestTrapAssemblerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `record(ChestTrapCode, String, int)` | — | ✔ |
| `validFile()` | — | ✔ |
| `errors()` | — | ✘ |
| `assemble(List<ChestTrapParseRecord>, List<String>)` | — | ✘ |
| `brokenBy(java.util.function.Consumer<List<ChestTrapParseRecord>>)` | — | ✘ |
| `aWellFormedRecordListAssemblesInOrder()` | — | ✘ |
| `anAbsentLevelBecomesZero()` | — | ✘ |
| `onlyTheLiteralOneSetsDestroyAndMagic()` | — | ✘ |
| `nullDestroyAndMagicAreFalseRatherThanThrowing()` | — | ✘ |
| `aNullCodeIsReportedAndRejectsTheFile()` | — | ✘ |
| `anEmptyCodeNeverBuildsATrapWithANullCode()` | — | ✘ |
| `aNullEffectListIsReportedAndRejectsTheFile()` | — | ✘ |
| `nullNameAndMessagesBecomeEmptyStrings()` | — | ✘ |
| `moreRecordsThanThereAreCodesIsReported()` | — | ✘ |
| `equalLevelsAreAllowedButDescendingOnesAreNot()` | — | ✘ |
| `everyCodeMustSitAtItsOwnOrdinal()` | — | ✘ |
| `anIncompleteFileIsRejectedEvenWhenEveryRecordIsValid()` | — | ✘ |
| `anEmptyRecordListYieldsAnEmptyResult()` | — | ✘ |
| `everyFailurePathReturnsAnEmptyListRatherThanNull()` | — | ✘ |


##### `ChestTrapReaderTest.java`

**Responsible for:** End-to-end throughput tests for ChestTrapReader: file text -> ChestTrapLexer/ ChestTrapGrammar -> ChestTrapParseRecord -> ChestTrapAssembler -> ChestTrap, the exact chain ObjectDataLoader.loadChestTraps() drives.

**class `ChestTrapReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/9 documented): ✔ `FULL_FILE` — ✘ `REAL_FILE`, `PAIN_FILE`, `BASE_FILE`, `SUMMON_FILE`, `savedPains`, `savedBases`, `savedSummons`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `loadReal()` | — | ✘ |
| `byCode(List<ChestTrap>, ChestTrapCode)` | — | ✘ |
| `tempFile(String, String)` | — | ✘ |
| `parse(String, String)` | — | ✘ |
| `realFileLoadsAll7TrapsWithNoErrors()` | — | ✘ |
| `codesAppearInEnumOrderOnTheRealFile()` | — | ✘ |
| `pvalsAreOneBitPerTrapInFileOrder()` | — | ✘ |
| `namesAreNotUniqueButCodesAre()` | — | ✘ |
| `levelsAreTakenFromTheFile()` | — | ✘ |
| `repeatedEffectLinesAccumulateIntoOneTrap()` | — | ✘ |
| `theLockedEntryHasNoEffectAndNoMessages()` | — | ✘ |
| `destroyAndMagicAreSetOnlyWhereTheFileSaysSo()` | — | ✘ |
| `messagesKeepTheirPunctuationAndSpacing()` | — | ✘ |
| `messageTextMayContainColonsAndIsNotSplit()` | — | ✘ |
| `commentsAndBlankLinesAreSkipped()` | — | ✘ |
| `directivesMayAppearInAnyOrderWithinARecord()` | — | ✘ |
| `aMissingCodeLineIsAHardParseErrorAndRejectsTheFile()` | — | ✘ |
| `recordCountMismatchIsASoftError()` | — | ✘ |
| `anUnknownCodeIsReportedAndRejectsTheFile()` | — | ✘ |
| `aFirstRecordThatIsNotNoTrapIsRejected()` | — | ✘ |
| `codesOutOfEnumOrderAreRejected()` | — | ✘ |
| `aDuplicatedCodeIsRejected()` | — | ✘ |
| `levelsThatDescendAreRejected()` | — | ✘ |
| `aValidPrefixOfTheFileIsStillRejectedAsIncomplete()` | — | ✘ |
| `anUnresolvableEffectDropsTheRecordAndSoRejectsTheFile()` | — | ✘ |


##### `CurseAssemblerTest.java`

**Responsible for:** Drives CurseAssembler#assemble directly with hand-built CurseParseRecords, so each case isolates one clause of the assembler without the lexer and grammar in the way (CurseReaderTest covers the file-to-curse path end to end).

**class `CurseAssemblerTest`** — class Javadoc: ✔ · type stamp: on 261005

- Fields (0/2 documented): ✘ `OBJECT_BASE_FILE`, `savedObjectBases`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✔ |
| `assemble(List<String>, Rec)` | — | ✘ |
| `only(List<Curse>)` | — | ✘ |
| `numbered(int)` | — | ✘ |
| `absentWeightIsZero()` | — | ✘ |
| `negativeWeightIsKept()` | — | ✘ |
| `weightBoundsAreInclusive()` | — | ✘ |
| `weightOutsideRangeIsRejected()` | — | ✘ |
| `malformedWeightIsRejected()` | — | ✘ |
| `multiplyWeightWithNegativeWeightIsRejected()` | — | ✘ |
| `multiplyWeightBoundaryCases()` | — | ✘ |
| `combatMapsInOrder()` | — | ✘ |
| `badCombatFieldIsRejected()` | — | ✘ |
| `flagsAndValuesShareOneElementInfo()` | — | ✘ |
| `badValueIntegerIsRejected()` | — | ✘ |
| `descLinesConcatenate()` | — | ✘ |
| `conflictFlagsLandOnTheCurse()` | — | ✘ |
| `unknownConflictFlagIsRejected()` | — | ✘ |
| `resultIsReversedAndIndexed()` | — | ✘ |
| `skippedRecordLeavesNoIndexGap()` | — | ✘ |
| `exactlyTheCapLoads()` | — | ✘ |
| `oneOverTheCapThrows()` | — | ✘ |
| `skippedRecordsDoNotCountTowardsTheCap()` | — | ✘ |
| `forwardConflictReferenceLinks()` | — | ✘ |
| `unknownConflictNameKeepsTheCurse()` | — | ✘ |

**class `CurseAssemblerTest.Rec`** — class Javadoc: ✔ · type stamp: —

- Fields (0/14 documented): ✘ `name`, `type`, `weight`, `toh`, `tod`, `toa`, `flags`, `values`, `message`, `desc`, `conflict`, `cFlag`, `time`, `line`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Rec(String)` | — | ✘ |
| `weight(String)` | — | ✘ |
| `flags(String)` | — | ✘ |
| `value(String, String)` | — | ✘ |
| `combat(String, String, String)` | — | ✘ |
| `desc(String)` | — | ✘ |
| `conflict(String)` | — | ✘ |
| `cFlag(String)` | — | ✘ |
| `line(int)` | — | ✘ |
| `build()` | — | ✘ |


##### `CurseReaderTest.java`

**Responsible for:** End-to-end throughput tests for CurseReader: file text -> CurseLexer/ CurseGrammar -> CurseParseRecord -> CurseAssembler -> Curse.

**class `CurseReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/10 documented): ✘ `REAL_FILE`, `PAIN_FILE`, `BASE_FILE`, `SUMMON_FILE`, `OBJECT_BASE_FILE`, `savedPains`, `savedBases`, `savedSummons`, `savedObjectBases`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `curse(String, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `byName(List<Curse>, String)` | — | ✘ |
| `realFileLoadsAllTwentySevenCursesWithNoErrors()` | — | ✘ |
| `combatPenaltiesAndAfflictableBasesResolve()` | — | ✘ |
| `valuesLineSplitsIntoModifiersAndResistancesOnTheRealFile()` | — | ✘ |
| `conflictingCursesLinkToTheSameInstancesBothWays()` | — | ✘ |
| `aStatModifierAndAResistanceOnOneLineSplitToTheRightCarriers()` | — | ✘ |
| `hatesAndIgnoreElementFlagsRouteToElInfoFlags()` | — | ✘ |
| `plainObjectFlagRoutesToObjectFlags()` | — | ✘ |
| `absentWeightAndCombatDefaultToZeroAndTheRecordStillLoads()` | — | ✘ |
| `timeAfterTheEffectReachesTheCurseObject()` | — | ✔ |
| `timeBeforeTheEffectReachesTheCurseObject()` | — | ✔ |
| `absentTimeIsZeroNotNull()` | — | ✔ |
| `unknownTypeIsReportedAndSkipped()` | — | ✘ |
| `unknownModifierIsReportedAndSkipped()` | — | ✘ |
| `unknownResistanceElementIsReportedAndSkipped()` | — | ✘ |
| `unknownObjectFlagIsReportedAndSkipped()` | — | ✘ |
| `unknownElementFlagIsReportedAndSkipped()` | — | ✘ |
| `unresolvableConflictIsReportedButTheRecordStillLoads()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `recordNotOpeningWithNameIsAHardError()` | — | ✘ |
| `twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads()` | — | ✘ |


##### `DungeonProfileReaderTest.java`

**Responsible for:** Reader/assembly tests for DungeonProfileReader: dungeon_profile.txt text -> DungeonProfileLexer/DungeonProfileGrammar -> DungeonProfileParseRecord -> DungeonProfileAssembler (plus its three sub-assemblers) -> CaveProfile.

**class `DungeonProfileReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/3 documented): ✔ `REAL_FILE`, `EXPECTED_PROFILES` — ✘ `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `byName(List<CaveProfile>, String)` | — | ✘ |
| `tempFile(String, String)` | — | ✔ |
| `cleanLoadOfTheRealFileReportsNoErrorsAndAllProfiles()` | — | ✘ |
| `townTakesItsDirectivesInAnyOrderAndHasNoTunnelOrRooms()` | — | ✔ |
| `classicResolvesEveryDirective()` | — | ✔ |
| `everyRoomNameInTheRealFileResolvesToARoomType()` | — | ✔ |
| `classicRoomsKeepFileOrderAndNarrowTheirFieldsCorrectly()` | — | ✔ |
| `minLevelIsReadWhenPresentAndDefaultsToZeroOtherwise()` | — | ✔ |
| `absentDirectivesLeaveSentinelsRatherThanZeroes()` | — | ✔ |
| `aRepeatedDirectiveIsOverwrittenByItsLastOccurrence()` | — | ✔ |
| `recordCountMismatchIsReportedButProfilesStillLoad()` | — | ✔ |
| `anOverflowingParamsValueDropsOnlyItsOwnProfile()` | — | ✔ |
| `anOverflowingAllocOrMinLevelDropsItsProfile()` | — | ✔ |
| `anOverflowingRoomFieldDropsOnlyThatRoom()` | — | ✔ |
| `unknownRoomNameLeavesANullRoomTypeAndNoError()` | — | ✔ |
| `missingRecordCountHeaderFailsClosed()` | — | ✔ |
| `aProfileWithNoDirectivesFailsClosed()` | — | ✔ |
| `commentsAndBlankLinesAreIgnored()` | — | ✔ |
| `parseReturnsTheSameItemsAsParseWithResults()` | — | ✔ |
| `aMissingFileThrows()` | — | ✘ |


##### `EgoItemReaderTest.java`

**Responsible for:** End-to-end throughput tests for EgoItemReader: file text -> EgoItemsLexer/ EgoItemsGrammar -> EgoItemParseRecord -> EgoItemAssembler -> EgoItem.

**class `EgoItemReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/22 documented): ✘ `REAL_FILE`, `PAIN_FILE`, `BASE_FILE`, `SUMMON_FILE`, `SLAY_FILE`, `BRAND_FILE`, `OBJECT_BASE_FILE`, `CURSE_FILE`, `SHAPE_FILE`, `OBJECT_FILE`, `ACTIVATION_FILE`, `savedPains`, `savedBases`, `savedSummons`, `savedSlays`, `savedBrands`, `savedObjectBases`, `savedCurses`, `savedShapes`, `savedObjectKinds`, `savedActivations`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✔ |
| `setStatic(String, Object)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `ego(String, String)` | — | ✔ |
| `loadOne(String, String)` | — | ✔ |
| `field(EgoItem, String)` | — | ✔ |
| `realFileLoadsAllEgosWithNoErrors()` | — | ✘ |
| `typeLineExpandsToEveryKindOfThatTval()` | — | ✘ |
| `multipleTypeLinesAccumulateIntoOneList()` | — | ✘ |
| `itemLineResolvesExactlyOneKindBySval()` | — | ✘ |
| `typeAndItemLinesMergeIntoTheSameList()` | — | ✘ |
| `unknownTypeTvalIsReportedAndSkipped()` | — | ✘ |
| `unknownItemTvalIsReportedAndSkipped()` | — | ✘ |
| `unknownItemSvalIsReportedAndSkipped()` | — | ✘ |
| `numericItemSvalIsRejected()` | — | ✘ |
| `flagsSplitAcrossObjectKindAndElementFlags()` | — | ✘ |
| `flagWithSpaceAfterColonStillResolves()` | — | ✘ |
| `unknownFlagIsReportedAndSkipped()` | — | ✘ |
| `flagsOffAreParsedAsObjectFlags()` | — | ✘ |
| `unknownFlagOffIsReportedAndSkipped()` | — | ✘ |
| `valuesSplitDiceModifiersFromIntegerResistances()` | — | ✘ |
| `minValuesStoredAsPlainInts()` | — | ✘ |
| `unknownModifierIsReportedAndSkipped()` | — | ✘ |
| `combatRandomFormsAreParsed()` | — | ✘ |
| `minCombat255SentinelIsPreserved()` | — | ✘ |
| `absentMinCombatDefaultsToNoMinimum()` | — | ✔ |
| `realAccuracyEgoKeepsItsMinimums()` | — | ✔ |
| `egoIndexIsFileOrderFromZero()` | — | ✔ |
| `curseWithNoPowerIsSkippedNotRejected()` | — | ✔ |
| `brandAndSlayAndCurseResolveAgainstTheirRegistries()` | — | ✘ |
| `unknownBrandIsReportedAndSkipped()` | — | ✘ |
| `unknownSlayIsReportedAndSkipped()` | — | ✘ |
| `unknownCurseIsReportedAndSkipped()` | — | ✘ |
| `descriptionLinesAreConcatenated()` | — | ✘ |
| `unknownActivationIsReportedAndSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidEgoStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `recordNotOpeningWithNameIsAHardError()` | — | ✘ |


##### `FlavourReaderTest.java`

**Responsible for:** End-to-end throughput tests for FlavourReader: file text -> FlavourLexer/ FlavourGrammar -> uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindParseRecord -> uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindAssembler (which delegates the per-record leg to uk.co.jackoftradesltd.backend...

**class `FlavourReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/26 documented): ✔ `result` — ✘ `FLAVOUR_FILE`, `PAIN_FILE`, `BASE_FILE`, `SUMMON_FILE`, `SLAY_FILE`, `BRAND_FILE`, `OBJECT_BASE_FILE`, `OBJECT_FILE`, `SHAPE_FILE`, `CURSE_FILE`, `ACTIVATION_FILE`, `ARTIFACT_FILE`, `savedPains`, `savedBases`, `savedSummons`, `savedSlays`, `savedBrands`, `savedObjectBases`, `savedCurses`, `savedActivations`, `savedObjectKinds`, `savedKindsByTvalSval`, `savedObjectBaseKindMax`, `savedPlayerShapes`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✔ |
| `load(String, String)` | — | ✔ |
| `withHeader(int, String)` | — | ✘ |
| `hasError(ParseResult<FlavourKind>, String)` | — | ✔ |
| `setStatic(String, Object)` | — | ✘ |
| `kind(TValue)` | — | ✔ |
| `byText(FlavourKind, String)` | — | ✔ |
| `theRealFileLoadsEightKindsAnd302FlavoursWithNoErrors()` | — | ✔ |
| `assertGlyphAndCount(TValue, char, int)` | — | ✘ |
| `fixedFlavourResolvesItsSvalAgainstTheSynthesisedKind()` | — | ✔ |
| `randomFlavourHasNoSvalSymbolAndUnknownSval()` | — | ✔ |
| `scrollFlavoursAreWhiteAndHaveNullText()` | — | ✔ |
| `unknownTvalDropsTheKindBlock()` | — | ✔ |
| `multiCharacterGlyphDropsTheKindBlock()` | — | ✔ |
| `unknownColourIsReported()` | — | ✔ |
| `malformedIndexIsReported()` | — | ✔ |
| `unresolvableFixedSvalDropsTheWholeBlock()` | — | ✔ |
| `wrongRecordCountIsReported()` | — | ✔ |
| `missingRecordCountHeaderIsAHardError()` | — | ✔ |


##### `GameConstantsParseResultTest.java`

**Responsible for:** Direct unit tests for GameConstantsParseResult, isolated from the ANTLR-backed GameConstantsReader pipeline that GameConstantsReaderTest exercises it through.

**class `GameConstantsParseResultTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `EMPTY_DATA`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `successfulResultHasNoErrorsAndExposesTheSameDataInstance()` | — | ✘ |
| `failedResultHasNullDataAndExposesTheSameErrorList()` | — | ✘ |


##### `GameConstantsReaderTest.java`

**Responsible for:** Reader/assembly tests for GameConstantsReader (grammar-suite reader-track R4).

**class `GameConstantsReaderTest`** — class Javadoc: ✔ · type stamp: before 261002

- Fields (1/2 documented): ✔ `REAL_FILE` — ✘ `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✔ |
| `realText()` | — | ✔ |
| `realTextWithout(String)` | — | ✔ |
| `lineCount(String)` | — | ✔ |
| `cleanLoadReportsNoErrorsAndPopulatesTheCarrier()` | — | ✘ |
| `twoDistinctErrorsAreBothReportedWithTheirLines()` | before 261002 | ✔ |
| `missingConstantIsLeftAtZero()` | on 261002 | ✔ |
| `duplicateScalarConstantOverwritesEarlierValue()` | on 261002 | ✔ |
| `nonIntegerValueIsReportedWithItsLine()` | — | ✘ |
| `wrongArityCriticalLevelLineIsReportedWithItsLine()` | — | ✘ |


##### `HintReaderTest.java`

**Responsible for:** End-to-end throughput test for HintReader: file text -&gt; HintLexer/ HintGrammar -&gt; uk.co.jackoftradesltd.backend.parser.hint.HintParseRecord -&gt; HintAssembler -&gt; Hint.

**class `HintReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/3 documented): ✔ `EXPECTED_HINTS` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `realFileLoadsAllHintsWithNoErrors()` | — | ✘ |
| `firstAndLastHintTextResolveVerbatim()` | — | ✘ |
| `hintTextPreservesPunctuationVerbatim()` | — | ✔ |
| `recordCountMismatchIsASoftError()` | — | ✘ |
| `missingRecordCountHeaderIsAHardErrorYieldingEmptyResult()` | — | ✔ |


##### `HistoryReaderTest.java`

**Responsible for:** End-to-end throughput tests for HistoryReader: file text -> HistoryLexer/ HistoryGrammar -> uk.co.jackoftradesltd.backend.parser.history.HistoryParseRecord -> uk.co.jackoftradesltd.backend.parser.history.HistoryAssembler -> PlayerHistoryChart.

**class `HistoryReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/4 documented): ✔ `REAL_RECORD_COUNT`, `REAL_CHART_COUNT` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✘ |
| `record(int, int, int, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `byNumber(List<PlayerHistoryChart>, int)` | — | ✘ |
| `entriesOf(PlayerHistoryChart)` | — | ✔ |
| `realFileLoadsAll44ChartsWithNoErrors()` | — | ✘ |
| `everyPhraseIsPreservedAsAnEntry()` | — | ✘ |
| `entriesAreGroupedByChartNumber()` | — | ✘ |
| `firstEntryOfChartOneHasExpectedRollAndText()` | — | ✘ |
| `successorReferencesAreResolvedToTheNextChart()` | — | ✘ |
| `terminalChartHasNullSuccessor()` | — | ✘ |
| `mismatchedSuccessorWithinChartIsReportedAndEntrySkipped()` | — | ✘ |
| `danglingSuccessorIsReportedButChartStillLoads()` | — | ✘ |
| `integerOverflowInPercentIsReportedAndRecordSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidChartStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `phraseWithoutAChartIsAHardError()` | — | ✘ |
| `chartWithoutAPhraseIsAHardError()` | — | ✘ |


##### `ItemObjectReaderTest.java`

**Responsible for:** End-to-end throughput tests for ItemObjectReader: file text -> ItemObjectLexer/ ItemObjectGrammar -> ItemObjectParseRecord -> ItemObjectAssembler -> ObjectKind.

**class `ItemObjectReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/19 documented): ✔ `HEAD` — ✘ `REAL_FILE`, `PAIN_FILE`, `BASE_FILE`, `SUMMON_FILE`, `SLAY_FILE`, `BRAND_FILE`, `OBJECT_BASE_FILE`, `CURSE_FILE`, `SHAPE_FILE`, `savedPains`, `savedBases`, `savedSummons`, `savedSlays`, `savedBrands`, `savedObjectBases`, `savedCurses`, `savedShapes`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `kindField(ObjectKind, String)` | — | ✔ |
| `withHeader(int, String)` | — | ✘ |
| `obj(String, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `realFileLoadsAllKindsWithNoErrors()` | — | ✘ |
| `unknownTypeIsReportedAndSkipped()` | — | ✘ |
| `unknownFlagIsReportedAndSkipped()` | — | ✘ |
| `unknownModifierIsReportedAndSkipped()` | — | ✘ |
| `unknownResistanceElementIsReportedAndSkipped()` | — | ✘ |
| `unknownBrandIsReportedAndSkipped()` | — | ✘ |
| `unknownSlayIsReportedAndSkipped()` | — | ✘ |
| `unknownCurseIsReportedAndSkipped()` | — | ✘ |
| `curseResolvesToAnEntryKeyedByCurseCarryingItsPower()` | — | ✔ |
| `powerZeroCurseIsNotRecorded()` | — | ✔ |
| `assertSameDice(Random, Random)` | — | ✔ |
| `timeAfterTheEffectReachesTheKind()` | — | ✔ |
| `timeWithSeveralEffectsReachesTheKindOnce()` | — | ✔ |
| `timeBeforeTheEffectReachesTheKind()` | — | ✔ |
| `absentTimeIsZeroNotNull()` | — | ✔ |
| `everyKindInTheRealFileHoldsItsOwnTime()` | — | ✔ |
| `twoDifferentSoftErrorsAreBothReportedAndTheValidKindStillLoads()` | — | ✘ |
| `recordCountMismatchIsReportedButValidKindStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `recordNotOpeningWithNameIsAHardError()` | — | ✘ |
| `kindFlagTokenReachesKindFlagsAndBaseFlagsAreUnioned()` | — | ✔ |
| `ignoreTokenSetsIgnoreOnThatElementOfTheKindOnly()` | — | ✔ |
| `severalIgnoreTokensAndAKindFlagShareOneLine()` | — | ✔ |
| `ignoreOnOneKindDoesNotReachItsBaseOrItsSiblings()` | — | ✔ |
| `bareElementNameIsNotAFlag()` | — | ✔ |
| `shortUnknownTokenIsASoftError()` | — | ✔ |
| `tokenWithWrongPrefixButAnElementSuffixIsNotAnIgnoreFlag()` | — | ✔ |
| `realFileLanternAndMithrilArrowIgnoreExactlyTheirElements()` | — | ✔ |


##### `LoreReaderTest.java`

**Responsible for:** Tests for LoreReader, which reads lib/user/lore.txt — the player's per-save monster-knowledge record, not a lib/gamedata file.

**class `LoreReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/10 documented): ✔ `REAL_FILE`, `EXPECTED_RECORDS`, `ONE_RECORD` — ✘ `tempDir`, `monsterRaces`, `savedRaces`, `blowMethods`, `savedBlowMethods`, `blowEffects`, `savedBlowEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(MonsterLore, String)` | — | ✘ |
| `seedTheRegistriesTheGrammarReachesInto()` | — | ✔ |
| `restoreTheRegistries()` | — | ✘ |
| `seedCombatTables()` | — | ✔ |
| `tempFile(String, String)` | — | ✔ |
| `theRealFileAlsoNeedsTheCombatTablesLoaded()` | — | ✔ |
| `parsingBeforeTheMonsterRacesAreLoadedThrows()` | — | ✔ |
| `aMissingFileThrows()` | — | ✘ |
| `theRealFileLoadsEveryRecord()` | — | ✘ |
| `countsAreReadAcrossInFieldOrder()` | — | ✘ |
| `repeatedFlagsLinesAccumulateIntoOneFlagSet()` | — | ✘ |
| `aBlowIsRecordedWithTheNumberOfTimesItWasSeen()` | — | ✘ |
| `eachRecordIsAttachedToItsMonsterRace()` | — | ✘ |
| `unknownFlagsAreReportedAsSoftErrorsRatherThanThrown()` | — | ✘ |


##### `MonsterAssemblerFrequencyTest.java`

**Responsible for:** Pins freq_innate and freq_spell as C's parse_monster_innate_freq, parse_monster_spell_freq and parse_monster_spells (mon-init.c) leave them.

**class `MonsterAssemblerFrequencyTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/4 documented): ✔ `INNATE` — ✘ `MONSTER_FILE`, `real`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✘ |
| `intField(MonsterRace, String)` | — | ✘ |

**record `MonsterAssemblerFrequencyTest.Expected`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/3 documented): ✘ `name`, `innate`, `spell`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `expectedFrom(List<String>)` | — | ✔ |
| `everyRealRaceMatchesC()` | — | ✘ |
| `load(String)` | — | ✘ |
| `percentageIsDividedInto100()` | — | ✘ |
| `boundaryPercentages()` | — | ✘ |
| `noSpellsMeansZero()` | — | ✘ |
| `onlyABreath()` | — | ✘ |
| `onlyABall()` | — | ✘ |
| `nonInnateBoltIsASpell()` | — | ✘ |
| `mixedSpellsDefaultBoth()` | — | ✘ |
| `matchingKindInTheMiddle()` | — | ✘ |
| `explicitOneDefaultsTheOther()` | — | ✘ |
| `outOfRangeIsAnError()` | — | ✘ |


##### `MonsterAssemblerHearingTest.java`

**Responsible for:** Pins hearing as C's parse_monster_hearing (mon-init.c) leaves it: the hearing: value times z_info->max_sight, divided by 20, in integer arithmetic.

**class `MonsterAssemblerHearingTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `loaded`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✘ |
| `dataField()` | — | ✘ |
| `constants()` | — | ✘ |
| `setConstants(GameConstantsData)` | — | ✘ |
| `withMaxSight(int)` | — | ✔ |
| `hearingField(MonsterRace)` | — | ✘ |
| `hearingFor(int, int)` | — | ✔ |
| `swapIsScopedToTheParse()` | — | ✘ |
| `standardMaxSightIsAPassThrough()` | — | ✘ |
| `thirtyAtTwentyFiveIsThirtySeven()` | — | ✘ |
| `smallerMaxSightShrinksHearing()` | — | ✘ |
| `multipliesBeforeDividing()` | — | ✘ |
| `truncatesLikeC()` | — | ✘ |


##### `MonsterBaseGrammarTest.java`

**Responsible for:** Grammar-level tests for the split MonsterBaseLexer/MonsterBaseGrammar pair, driving them straight into the MonsterBaseParseRecord list the file rule builds.

**class `MonsterBaseGrammarTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `FAIL_FAST` — ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✔ |
| `entry(String, char, int, String, String)` | — | ✔ |
| `parse(CharStream)` | — | ✘ |
| `parse(String)` | — | ✘ |
| `cleanLoadOfTheRealFileParsesAll56Templates()` | — | ✘ |
| `firstTemplateCapturesEveryFieldRaw()` | — | ✘ |
| `repeatedFlagsLinesAccumulateAcrossTheRecord()` | — | ✘ |
| `inGameNameCurrentlyMirrorsTheDescription()` | — | ✘ |
| `headerIsReadOnceAndEveryTemplateBecomesItsOwnRecord()` | — | ✘ |
| `aTemplateWithNoFlagsLinesStillParses()` | — | ✘ |
| `nameWithSpacesIsCapturedWhole()` | — | ✘ |
| `commentsAndBlankLinesAreSkippedAroundEntries()` | — | ✘ |


##### `MonsterBaseReaderTest.java`

**Responsible for:** End-to-end throughput tests for MonsterBaseReader: file text -> MonsterBaseLexer/ MonsterBaseGrammar -> MonsterBaseParseRecord -> MonsterBaseAssembler -> MonsterBase.

**class `MonsterBaseReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/4 documented): ✔ `savedMonsterPains` — ✘ `REAL_FILE`, `PAIN_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadPainSets()` | — | ✘ |
| `restorePainSets()` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `entry(String, char, int, String, String)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `cleanLoadOfTheRealFileYieldsAll56Bases()` | — | ✘ |
| `aWellFormedSyntheticRecordAssemblesWithNoErrors()` | — | ✘ |
| `unknownRaceFlagIsReportedAndRecordSkipped()` | — | ✘ |
| `unknownPainIndexIsReportedAndRecordSkipped()` | — | ✘ |
| `overflowingPainIndexIsReportedAndRecordSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `aTemplateMissingItsDescIsAHardError()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |


##### `MonsterReaderTest.java`

**Responsible for:** End-to-end tests for MonsterReader: data-file text -> MonsterLexer/ MonsterGrammar -> uk.co.jackoftradesltd.backend.parser.monster.MonsterParseRecord -> uk.co.jackoftradesltd.backend.parser.monster.MonsterAssembler -> MonsterRace.

**class `MonsterReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/4 documented): ✔ `EXPECTED_RACES`, `real` — ✘ `MONSTER_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✔ |
| `cleanup()` | — | ✔ |
| `rec(String, String)` | — | ✔ |
| `withHeader(int, String)` | — | ✘ |
| `load(String, String)` | — | ✔ |
| `hasError(ParseResult<MonsterRace>, String)` | — | ✘ |
| `byName(ParseResult<MonsterRace>, String)` | — | ✘ |
| `field(MonsterRace, String)` | — | ✘ |
| `declaredField(Object, String)` | — | ✔ |
| `theRealFileLoadsAll624MonstersWithNoErrors()` | — | ✘ |
| `aSimpleMonstersScalarFieldsFlagsAndSingleBlowAreAssembled()` | — | ✘ |
| `aMonsterWithTwoFlagLinesThreeFriendsAndMethodOnlyBlowsIsAssembled()` | — | ✘ |
| `aColourCyclingMonsterResolvesItsCycler()` | — | ✘ |
| `aMonsterInheritsItsBaseFlagsAndAddsItsOwn()` | — | ✘ |
| `aFlagsOffLineRemovesAnInheritedBaseFlagButKeepsTheOthers()` | — | ✘ |
| `aMonsterWithoutAGlyphLineInheritsItsBaseGlyph()` | — | ✘ |
| `anExplicitGlyphOverridesTheBaseGlyph()` | — | ✘ |
| `aMethodOnlyBlowGetsTheNoneEffectRatherThanNull()` | — | ✘ |
| `friendNamesResolveToRacesIncludingForwardRefsAndSame()` | — | ✘ |
| `aSameFriendIsSubstitutedToTheOwnerAndResolvesBackToIt()` | — | ✘ |
| `raceLookupIsCaseInsensitiveWithASubstringFallback()` | — | ✘ |
| `aSpecificRaceShapeResolvesToARaceAndNotABase()` | — | ✘ |
| `aBaseShapeResolvesToABaseAndNotARace()` | — | ✘ |
| `perSpellMessagesAreAssembledOntoTheOwningMonster()` | — | ✘ |
| `aSpecificDropKeepsItsKindWhileADropBaseHasNullKind()` | — | ✘ |
| `aFriendsBaseResolvesToItsBaseWithAnExplicitServantRole()` | — | ✘ |
| `anExplicitFriendRoleMapsToTheGroupRole()` | — | ✘ |
| `anOmittedBlowDamageDefaultsToAZeroDiceRandomNotNull()` | — | ✘ |
| `multipleBadRecordsInOneRunAreEachReportedAndDroppedWhileGoodOnesSurvive()` | — | ✘ |
| `anUnknownBaseIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownFlagIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownFlagsOffFlagIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownSpellIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownBlowMethodIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownBlowEffectIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownDropTvalIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownDropObjectNameIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownMimicTvalIsReportedAndTheRecordDropped()` | — | ✘ |
| `anUnknownMimicObjectNameIsReportedAndTheRecordDropped()` | — | ✘ |
| `aFriendChanceAboveOneHundredIsKeptAsWritten()` | — | ✘ |
| `anUnknownFriendRoleIsReported()` | — | ✘ |
| `anOverflowingIntegerIsReportedAsAMalformedInteger()` | — | ✘ |
| `aGlyphThatIsNotExactlyOneCharacterIsReportedAndTheRecordDropped()` | — | ✘ |
| `aWrongRecordCountHeaderIsReported()` | — | ✘ |


##### `MonsterSpellReaderTest.java`

**Responsible for:** End-to-end throughput tests for MonsterSpellReader: file text -&gt; MonsterSpellLexer/MonsterSpellGrammar -&gt; MonsterSpellParseRecord -&gt; MonsterSpellAssembler -&gt; MonsterSpellType.

**class `MonsterSpellReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/6 documented): ✔ `REAL_FILE_RECORDS` — ✘ `REAL_FILE`, `savedPains`, `savedBases`, `savedSummons`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `withHeader(int, String)` | — | ✔ |
| `entry(String, String, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `cleanLoadOfTheRealFileYieldsAllNinetyOneSpells()` | — | ✘ |
| `everySpellNameInTheRealFileResolvesToTheRsfEnum()` | — | ✘ |
| `omittedMsgtLineDoesNotDropTheRecord()` | — | ✘ |
| `presentMsgtLineIsStillResolvedAndValidated()` | — | ✘ |
| `unknownSpellNameIsReportedAndDropped()` | — | ✘ |
| `recordCountHeaderMismatchIsReported()` | — | ✘ |
| `teleportAwayAndTeleportToSelfBothResolve()` | — | ✘ |
| `teleportSubtypesAreNotInterchangeable()` | — | ✘ |
| `teleportWithNoSubtypeIsAccepted()` | — | ✘ |
| `unknownTeleportSubtypeIsReportedAndDropsTheSpell()` | — | ✘ |
| `loreBeforeAnyPowerCutoffFormsTheImplicitLevelZero()` | — | ✘ |
| `aPowerCutoffOpensAFurtherLevelWithoutDisturbingLevelZero()` | — | ✘ |
| `repeatedLoreAndMessageLinesDoNotBreakTheRecord()` | — | ✘ |
| `multipleSpellsInOneFileAreAllKept()` | — | ✘ |


##### `NamesReaderTest.java`

**Responsible for:** End-to-end throughput test for NamesReader: file text -&gt; NamesLexer/ NamesGrammar -&gt; NamesParseRecord -&gt; NamesAssembler -&gt; Name.

**class `NamesReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/5 documented): ✔ `EXPECTED_SECTIONS`, `TOLKIEN_WORDS` — ✘ `REAL_FILE`, `SCROLL_WORDS`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `realFileLoadsBothSectionsWithNoErrors()` | — | ✘ |
| `sectionsResolveToIntsWithCorrectWordCounts()` | — | ✔ |
| `firstAndLastWordsOfEachSectionSurviveInOrder()` | — | ✔ |
| `wordListIsUnmodifiable()` | — | ✔ |
| `multipleSectionsPartitionTheirWordsOnSectionBoundaries()` | — | ✔ |
| `wordValuePreservesCaseAndPunctuationVerbatim()` | — | ✔ |
| `negativeSectionLabelRoundTrips()` | — | ✔ |
| `recordCountMismatchIsASoftError()` | — | ✘ |
| `missingRecordCountHeaderIsAHardErrorYieldingEmptyResult()` | — | ✔ |
| `nonNumericSectionLabelIsAHardErrorYieldingEmptyResult()` | — | ✔ |
| `assemblerDropsRecordWithUnparseableSectionButKeepsTheRest()` | — | ✔ |


##### `ObjectBaseReaderTest.java`

**Responsible for:** Reader/assembly tests for ObjectBaseReader (grammar-suite reader track), driving ObjectBaseLexer/ObjectBaseGrammar through the shared GrammarDriver into ObjectBase objects.

**class `ObjectBaseReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/3 documented): ✔ `DEFAULTS` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✔ |
| `tempFile(String, String)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `declared(ParseResult<ObjectBase>)` | — | ✔ |
| `cleanLoadOfTheRealFileReportsNoErrorsAndAll34Bases()` | — | ✘ |
| `syntheticNoneBaseIsAppendedToEveryLoad()` | — | ✔ |
| `minimalRecordLoadsAndInheritsTheFileDefaults()` | — | ✘ |
| `explicitBreakAndMaxStackOverrideTheDefaults()` | — | ✘ |
| `dragonArmorResolvesToItsTVal()` | — | ✘ |
| `noPluralNameResolvesTheTVal()` | — | ✘ |
| `colourResolvesToTheNamedColourNotTheDarkDefault()` | — | ✘ |
| `repeatedFlagsLinesMergeRatherThanReplace()` | — | ✘ |
| `unknownTValIsReportedAndRecordSkipped()` | — | ✘ |
| `unknownKindFlagIsReportedAndRecordSkipped()` | — | ✘ |
| `unknownHatesFlagIsReportedAndRecordSkipped()` | — | ✘ |
| `overflowingBreakChanceIsReportedAndRecordSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `digitBearingFlagLexesAsASingleFlagAndReachesTheAssembler()` | — | ✘ |


##### `ObjectPropertyAssemblerTypeTest.java`

**Responsible for:** Pins how ObjectPropertyAssembler treats the type: token against parse_object_property_type, parse_object_property_code and parse_object_property_subtype in obj-init.c.

**class `ObjectPropertyAssemblerTypeTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `record(String, String, String, int)` | — | ✘ |
| `assemble(List<String>, ObjectPropertyParseRecord)` | — | ✘ |
| `everyRealType_assemblesAgainstItsOwnTable()` | — | ✔ |
| `maxType_isDroppedWithIllegalTypeError()` | — | ✔ |
| `emptyType_isDroppedWithNoTypeError()` | — | ✔ |
| `unrecognizedTypes_areDroppedWithIllegalTypeError()` | — | ✘ |
| `badTypeRecord_doesNotStopTheNextRecord()` | — | ✔ |
| `subtype_isAcceptedOnFlagOnly()` | — | ✔ |
| `codeFromTheWrongTable_isDropped()` | — | ✔ |


##### `ObjectPropertyReaderTest.java`

**Responsible for:** End-to-end tests for the object-property pipeline: text file → ANTLR lexer/parser (uk.co.jackoftradesltd.backend.parser.grammars.objectproperty.ObjectPropertyLexer / ObjectPropertyGrammar) → ObjectPropertyReader → uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyAssembler → resol...

**class `ObjectPropertyReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✘ |
| `field(ObjectProperty, String)` | — | ✔ |
| `byName(List<ObjectProperty>, String)` | — | ✘ |
| `bindings(ObjectProperty)` | — | ✘ |
| `realFileLoadsAll79RecordsWithCorrectShape()` | — | ✔ |
| `elementRelationTypesResolve()` | — | ✔ |
| `typeMultResolvesAgainstTvalNames()` | — | ✔ |
| `taggedBinduiCapturesTagAndAuxIsRead()` | — | ✔ |
| `binduiExplicitValueIsParsed()` | — | ✔ |
| `recordWithoutBinduiHasNoBindings()` | — | ✔ |
| `flagRecordResolvesSubtypeIdTypeAndCode()` | — | ✔ |
| `statRecordResolvesCodeToModifier()` | — | ✔ |
| `missingRecordCountHeaderFailsClosed()` | — | ✘ |
| `recordCountMismatchIsLoggedButValidRecordSurvives()` | — | ✘ |
| `unresolvableCodeIsLoggedAndRecordSkipped()` | — | ✘ |


##### `PainGrammarTest.java`

**Responsible for:** Grammar-level tests for the split PainLexer/PainGrammar pair, driving them straight into the PainParseRecord list the file rule builds.

**class `PainGrammarTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/3 documented): ✔ `SEVEN`, `FAIL_FAST` — ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✔ |
| `entry(int, String)` | — | ✔ |
| `parse(CharStream)` | — | ✘ |
| `parse(String)` | — | ✘ |
| `cleanLoadOfTheRealFileParsesAll12Sets()` | — | ✘ |
| `firstSetKeepsItsTypeThenSevenMessagesInFileOrder()` | — | ✘ |
| `pluralisationAndAlternationSyntaxSurvivesVerbatim()` | — | ✘ |
| `headerIsReadOnceAndEveryEntryBecomesItsOwnRecord()` | — | ✘ |
| `declaredCountIsCapturedRawAndNotValidatedByTheGrammar()` | — | ✘ |
| `aMessageContainingPipesAndBracketsLexesAsOneString()` | — | ✘ |
| `commentsAndBlankLinesAreSkippedAroundAndWithinEntries()` | — | ✘ |
| `crlfLineEndingsAreHandledAndNotCapturedInMessages()` | — | ✘ |


##### `PainReaderTest.java`

**Responsible for:** End-to-end throughput tests for PainReader: file text -> PainLexer/PainGrammar -> PainParseRecord -> PainAssembler -> MonsterPain.

**class `PainReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `REAL_FILE`, `tempDir`, `SEVEN`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✔ |
| `entry(int, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `cleanLoadOfTheRealFileYieldsTwelveMonsterPains()` | — | ✘ |
| `firstMonsterPainHasTheRightIndexAndGraduatedMessages()` | — | ✘ |
| `overflowingTypeSerialIsReportedAndRecordSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordsStillLoad()` | — | ✘ |
| `twoWellFormedEntriesBothAssemble()` | — | ✘ |
| `underDeclaredRecordCountIsReportedButAllRecordsLoad()` | — | ✘ |
| `negativeTypeSerialIsAcceptedWithoutValidation()` | — | ✘ |
| `tooFewMessagesIsAHardErrorAndLoadsNothing()` | — | ✘ |
| `tooManyMessagesIsAHardError()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `emptyMessageBodyIsAHardError()` | — | ✘ |


##### `ParseErrorsTest.java`

**Responsible for:** Unit tests for ParseErrors, the shared collect-and-report error listener.

**class `ParseErrorsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshCollector(String)` | — | ✔ |
| `parse(String, String)` | — | ✔ |
| `freshCollectorHasNoErrors()` | — | ✘ |
| `addRecordsAFormattedError()` | — | ✘ |
| `throwIfAnyDoesNotThrowWhenEmpty()` | — | ✘ |
| `throwIfAnyThrowsWhenErrorsPresent()` | — | ✘ |
| `cleanParseCollectsNothing()` | — | ✘ |
| `malformedParseCollectsAtLeastOneError()` | — | ✘ |
| `getErrorsReturnsAnUnmodifiableView()` | — | ✘ |


##### `PitReaderTest.java`

**Responsible for:** End-to-end throughput test for PitReader: file text -> PitLexer/PitGrammar -> PitParseRecord -> PitAssembler -> PitProfile.

**class `PitReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/4 documented): ✔ `EXPECTED_PITS`, `GOOD_RECORD` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✔ |
| `field(PitProfile, String)` | — | ✘ |
| `staticField(String)` | — | ✔ |
| `byName(List<PitProfile>, String)` | — | ✘ |
| `names(ParseResult<PitProfile>)` | — | ✔ |
| `tempFile(String, String)` | — | ✔ |
| `realFileLoadsAllPitsWithNoErrors()` | — | ✘ |
| `initPopulatesThePitRegistry()` | — | ✔ |
| `representativeRecordsResolveCorrectly()` | — | ✔ |
| `theFixtureRecordLoadsCleanly()` | — | ✔ |
| `anUnknownRequiredFlagDropsTheRecordAndReportsEveryBadCode()` | — | ✔ |
| `anUnknownBannedFlagDropsTheRecord()` | — | ✔ |
| `anUnknownSpellDropsTheRecordOnEitherSpellLine()` | — | ✔ |
| `unresolvableMonsterNamesDropTheRecord()` | — | ✔ |
| `innateFreqIsRangeCheckedAndStoredAsHundredOverPercent()` | — | ✔ |
| `anOverflowingNumberDropsTheRecord()` | — | ✔ |
| `directivesAreOptionalAndUnordered()` | — | ✔ |
| `anUnrecognisedRoomDigitFallsBackToNoneRatherThanErroring()` | — | ✔ |
| `aRecordWithNoDirectivesFailsClosed()` | — | ✔ |
| `recordCountMismatchIsReportedButPitsStillLoad()` | — | ✔ |
| `anUnknownColourCodeIsALexerErrorNotASoftOne()` | — | ✔ |
| `parseReturnsTheSameItemsAsParseWithResults()` | — | ✔ |
| `aMissingFileThrows()` | — | ✘ |


##### `PlayerClassReaderTest.java`

**Responsible for:** End-to-end throughput tests for PlayerClassReader: file text -> PlayerClassLexer/PlayerClassGrammar -> uk.co.jackoftradesltd.backend.parser.playerclass.PlayerClassParseRecord -> uk.co.jackoftradesltd.backend.parser.playerclass.PlayerClassAssembler -> PlayerClass.

**class `PlayerClassReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/10 documented): ✔ `result` — ✘ `CLASS_FILE`, `REALM_FILE`, `OBJECT_BASE_FILE`, `SHAPE_FILE`, `savedRealms`, `savedObjectBases`, `savedShapes`, `savedObjectKinds`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✔ |
| `equipKinds()` | — | ✔ |
| `load(String, String)` | — | ✔ |
| `withHeader(int, String)` | — | ✘ |
| `hasError(ParseResult<PlayerClass>, String)` | — | ✔ |
| `setStatic(String, Object)` | — | ✘ |
| `field(Object, Class<?>, String, Object)` | — | ✘ |
| `field(Object, Class<?>, String)` | — | ✘ |
| `field(PlayerClass, String)` | — | ✘ |
| `byName(List<PlayerClass>, String)` | — | ✘ |
| `byName(String)` | — | ✘ |
| `realFileLoadsAllNineClassesWithNoErrors()` | — | ✘ |
| `everyExpectedClassIsPresent()` | — | ✘ |
| `warriorIsANonCasterWithItsScalarsAndFlags()` | — | ✘ |
| `warriorStatsAndSkillsAreParsedIncludingNegatives()` | — | ✘ |
| `mageIsACasterWithAllFiveBooksAndTheirSpells()` | — | ✘ |
| `druidCastsAndItsShapechangeSpellsResolved()` | — | ✘ |
| `aSyntheticMinimalCasterLoadsCleanlyFromScratch()` | — | ✘ |
| `castMagic(ParseResult<PlayerClass>)` | — | ✘ |
| `unknownObjectFlagIsReportedAndTheClassDropped()` | — | ✘ |
| `unknownPlayerFlagIsReportedAndTheClassDropped()` | — | ✘ |
| `invalidEquipTvalIsReportedButTheClassStillLoads()` | — | ✘ |
| `nonBirthEquipOptionIsRejected()` | — | ✘ |
| `unknownEquipOptionIsRejected()` | — | ✘ |
| `badEquipOptionDropsTheWholeEntryAsCDoes()` | — | ✘ |
| `startItemsAreHeldInReverseFileOrder()` | — | ✘ |
| `unknownBookObjectBaseIsReportedAndTheBookDropped()` | — | ✘ |
| `unknownBookRealmIsReportedAndTheBookDropped()` | — | ✔ |
| `moreBooksThanDeclaredIsReported()` | — | ✔ |
| `declaredBookCountMatchingIsAccepted()` | — | ✔ |
| `fewerBooksThanDeclaredIsAcceptedAsInC()` | — | ✔ |
| `aDroppedBookDoesNotAlsoRaiseACountError()` | — | ✔ |
| `aDroppedBookHidesAnExtraBookFromTheCountCheck()` | — | ✔ |
| `unknownEffectTypeInASpellIsReported()` | — | ✘ |
| `unknownShapechangeTargetInASpellIsReported()` | — | ✘ |
| `invalidProjectionSubtypeInASpellIsReported()` | — | ✘ |
| `recordCountMismatchIsReportedButTheValidClassStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `aRecordWithNoNameLineIsAHardError()` | — | ✘ |
| `twoBadEffectsInOneBlockAreBothReported()` | — | ✘ |
| `twoBadEquipLinesInOneClassAreBothReported()` | — | ✘ |


##### `PlayerPropertyReaderTest.java`

**Responsible for:** End-to-end tests for the player-property pipeline: text file → ANTLR lexer/parser (uk.co.jackoftradesltd.backend.parser.grammars.playerproperty.PlayerPropertyLexer / PlayerPropertyGrammar) → PlayerPropertyReader → uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyAssembler → resol...

**class `PlayerPropertyReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/5 documented): ✔ `ELEMENT_COUNT` — ✘ `REAL_FILE`, `PROJECTION_FILE`, `savedProjections`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✔ |
| `tempFile(String, String)` | — | ✘ |
| `byName(List<PlayerProperty>, String)` | — | ✘ |
| `cleanLoadOfAllThreeRecordShapesResolvesWithoutErrors()` | — | ✘ |
| `realFileLoadsCleanlyAndExpandsElementRecords()` | — | ✔ |
| `elementWithNoMatchingProjectionIsSkippedNotFailed()` | — | ✔ |
| `missingRecordCountHeaderFailsClosed()` | — | ✘ |
| `recordCountMismatchIsLoggedButValidRecordSurvives()` | — | ✘ |
| `unresolvableCodeIsLoggedAndRecordSkipped()` | — | ✘ |
| `binduiIsCapturedVerbatimEvenWhenNoMatchingUiEntryExists()` | — | ✘ |
| `twoDifferentSoftErrorsAreBothReported()` | — | ✘ |


##### `PlayerRaceReaderTest.java`

**Responsible for:** End-to-end throughput tests for PlayerRaceReader: file text -> PlayerRaceLexer/ PlayerRaceGrammar -> uk.co.jackoftradesltd.backend.parser.playerrace.PlayerRaceParseRecord -> uk.co.jackoftradesltd.backend.parser.playerrace.PlayerRaceAssembler -> PlayerRace.

**class `PlayerRaceReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/6 documented): ✘ `REAL_FILE`, `BODY_FILE`, `HISTORY_FILE`, `savedBodies`, `savedHistories`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `race(String, String)` | — | ✔ |
| `byName(List<PlayerRace>, String)` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `field(PlayerRace, String)` | — | ✔ |
| `realFileLoadsAllElevenRacesWithNoErrors()` | — | ✘ |
| `scalarFieldsAreParsedIncludingNegatives()` | — | ✘ |
| `statsMapKeepsTheStrIntWisDexConOrderingAndNegatives()` | — | ✘ |
| `skillsResolveIncludingTheRenamedMeleeBowThrowDigKeys()` | — | ✘ |
| `objectFlagsGetTheOfPrefixAndSplitOnThePipe()` | — | ✘ |
| `playerFlagsGetThePfPrefix()` | — | ✘ |
| `resistValuesGetTheElemPrefixAndStoreTheirLevel()` | — | ✘ |
| `everyRaceGetsTheHumanoidBodyNotNull()` | — | ✘ |
| `historyChartResolvesToTheReferencedChart()` | — | ✘ |
| `unknownObjectFlagIsReportedAndTheRaceDropped()` | — | ✘ |
| `unknownPlayerFlagIsReportedAndTheRaceDropped()` | — | ✘ |
| `unknownResistNameIsReportedAndTheRaceDropped()` | — | ✘ |
| `badHistoryChartIsReportedAndTheRaceDropped()` | — | ✘ |
| `oneBadRaceIsDroppedButTheOthersStillLoad()` | — | ✘ |
| `digitBearingFlagLexesAsASingleTokenThenFailsEnumLookup()` | — | ✘ |
| `recordCountMismatchIsReportedButTheValidRaceStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `aRecordWithNoNameLineIsAHardError()` | — | ✘ |
| `reflect(PlayerRace, String)` | — | ✔ |


##### `PlayerTimedReaderTest.java`

**Responsible for:** End-to-end throughput tests for the player-timed pipeline: file text -> PlayerTimedLexer/ PlayerTimedGrammar -> PlayerTimedParseRecord -> PlayerTimedAssembler -> PlayerTimedEffect.

**class `PlayerTimedReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/8 documented): ✔ `savedConstants`, `seededConstants` — ✘ `REAL_FILE`, `BRAND_FILE`, `SLAY_FILE`, `savedBrands`, `savedSlays`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `constantsField()` | — | ✔ |
| `setStatic(String, Object)` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `byName(List<PlayerTimedEffect>, TimedEffect)` | — | ✘ |
| `realFileLoadsAllFiftyThreeEffectsWithNoErrors()` | — | ✘ |
| `gradesAreCountedAndOrdered()` | — | ✘ |
| `everyEffectIsHeadedByTheImplicitOffGrade()` | — | ✘ |
| `repeatedFailLinesAreAllRetained()` | — | ✘ |
| `failLinesAreWalkedInTheOrderCBuildsThem()` | — | ✔ |
| `brandAndSlayCodesResolveToRegistryEntries()` | — | ✘ |
| `resistElementResolves()` | — | ✘ |
| `flagSynonymSplitsIntoDupFlagAndExactness()` | — | ✘ |
| `nonStackingFlagIsRead()` | — | ✘ |
| `lowerBoundIsRead()` | — | ✘ |
| `beginAndEndEffectsAreResolvedOnlyWhenPresent()` | — | ✘ |
| `gradeStatusLabelIsPreserved()` | — | ✘ |
| `gradeUpAndDownMessagesGoToTheRightSlots()` | — | ✘ |
| `unknownTimedEffectNameIsReportedAndSkipped()` | — | ✘ |
| `unknownBrandIsReportedAndSkipped()` | — | ✘ |
| `unknownResistElementIsReportedAndSkipped()` | — | ✘ |
| `unknownMessageTypeIsReportedAndSkipped()` | — | ✘ |
| `validAndInvalidRecordsInOneFileReportTheBadAndKeepTheGood()` | — | ✘ |
| `gradeWithNoDownMessageParses()` | — | ✘ |
| `gradeWithEmptyTrailingDownMessageParses()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `recordNotOpeningWithNameIsAHardError()` | — | ✘ |


##### `ProjectionAssemblerTest.java`

**Responsible for:** Unit tests for ProjectionAssembler.

**class `ProjectionAssemblerTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (0/4 documented): ✘ `REAL_FILE`, `POIS`, `ARROW`, `MAKE_DOOR`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `record(String, String, int)` | — | ✔ |
| `validFile()` | — | ✔ |
| `with(ProjectionParseRecord, String, String, String, String, String, String, String, String)` | — | ✘ |
| `withNumerator(ProjectionParseRecord, String)` | — | ✘ |
| `withDenominator(ProjectionParseRecord, String)` | — | ✘ |
| `withDivisor(ProjectionParseRecord, String)` | — | ✘ |
| `withDamageCap(ProjectionParseRecord, String)` | — | ✘ |
| `withMsgt(ProjectionParseRecord, String)` | — | ✘ |
| `withObvious(ProjectionParseRecord, String)` | — | ✘ |
| `withWake(ProjectionParseRecord, String)` | — | ✘ |
| `withColour(ProjectionParseRecord, String)` | — | ✘ |
| `assemble(List<ProjectionParseRecord>, List<String>)` | — | ✘ |
| `countOfType(List<Projection>, ProjectionType)` | — | ✘ |
| `realFileLoadsAllFiftySixProjections()` | — | ✔ |
| `realFileAcidRecordMatchesTheDataFile()` | — | ✔ |
| `realFileLightWeakIsEnvirons()` | — | ✔ |
| `realFileMonCrushIsMonster()` | — | ✔ |
| `wellFormedHandBuiltFileLoadsWithNoErrors()` | — | ✘ |
| `absentOptionalFieldsLoadAsDefaults()` | — | ✔ |
| `diceDenominatorIsAccepted()` | — | ✘ |
| `swappedElementsThrowAtTheFirstMismatch()` | — | ✔ |
| `nonElementBeforeTheElementsThrows()` | — | ✔ |
| `missingElementThrows()` | — | ✔ |
| `unknownCodeInTheElementBlockStillCountsAsAPosition()` | — | ✔ |
| `positionExceptionCarriesEarlierErrors()` | — | ✔ |
| `extraElementTypedRecordIsRejected()` | — | ✔ |
| `environsRowRetypedElementIsRejected()` | — | ✘ |
| `elementRetypedEnvironsIsRejected()` | — | ✔ |
| `droppedElementRecordFailsTheFile()` | — | ✔ |
| `badFieldOnNonElementRowSkipsOnlyThatRow()` | — | ✔ |
| `unknownCodeAfterTheElementBlockIsReportedAndSkipped()` | — | ✔ |
| `unknownColourFallsBackToWhiteWithoutAnError()` | — | ✔ |
| `colourNamesAreCaseInsensitiveAndEmptyOrBlankIsAccepted()` | — | ✘ |
| `numericMsgtBoundsFollowMsgMax()` | — | ✔ |
| `msgtNamesAreCaseInsensitive()` | — | ✔ |
| `unknownMsgtNameIsReportedAndSkipped()` | — | ✘ |
| `negativeOrNonNumericUintFieldsSkipTheRow()` | — | ✔ |
| `booleanFieldsAcceptAnyNonNegativeNumber()` | — | ✔ |
| `typeStringsMapToTheirEnumConstants()` | — | ✘ |
| `emptyFileIsRejectedForHavingNoElements()` | — | ✘ |


##### `ProjectionReaderTest.java`

**Responsible for:** Reader/assembly tests for ProjectionReader (grammar-suite reader track R4).

**class `ProjectionReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/3 documented): ✔ `ONE_RECORD` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✘ |
| `cleanLoadOfTheRealFileReportsNoErrorsAndAllProjections()` | — | ✘ |
| `recordCountMismatchIsReportedAlongsideTheElementCountFailure()` | — | ✘ |
| `headerMismatchOnTheRealFileIsReportedButAllProjectionsStillLoad()` | — | ✘ |
| `unknownCodeIsReportedWithNoItems()` | — | ✘ |
| `missingRecordCountHeaderIsReportedWithNoItems()` | — | ✘ |
| `unknownCodeAfterTheElementBlockIsReportedAndTheRestLoads()` | — | ✘ |
| `unknownTypeIsNotAnError()` | — | ✘ |


##### `QuestReaderTest.java`

**Responsible for:** End-to-end throughput test for QuestReader: file text -&gt; QuestLexer/ QuestGrammar -&gt; uk.co.jackoftradesltd.backend.parser.quest.QuestParseRecord -&gt; QuestAssembler -&gt; Quest.

**class `QuestReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/3 documented): ✔ `EXPECTED_QUESTS` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✔ |
| `quest(String, String, String, String)` | — | ✔ |
| `withHeader(int, String)` | — | ✘ |
| `load(String, String)` | — | ✘ |
| `byName(List<Quest>, String)` | — | ✘ |
| `staticField(String)` | — | ✘ |
| `realFileLoadsBothQuestsWithNoErrors()` | — | ✘ |
| `realFileQuestFieldsResolveCorrectly()` | — | ✘ |
| `initChainPopulatesTheQuestRegistry()` | — | ✔ |
| `raceResolvesViaCaseInsensitiveSubstringFallback()` | — | ✔ |
| `unknownRaceAbortsTheLoad()` | — | ✔ |
| `negativeLevelAbortsTheLoad()` | — | ✔ |
| `levelBeyondIntWrapsToAByteAndIsKept()` | — | ✔ |
| `overflowingNumberIsASoftErrorAndDropsTheRecord()` | — | ✘ |
| `recordCountMismatchIsASoftError()` | — | ✘ |
| `indicesAreContiguousAfterADroppedRecord()` | — | ✔ |
| `missingRecordCountHeaderIsAHardErrorYieldingEmptyResult()` | — | ✔ |


##### `RealmReaderTest.java`

**Responsible for:** End-to-end throughput tests for RealmReader: file text -> RealmLexer/ RealmGrammar -> uk.co.jackoftradesltd.backend.parser.realm.RealmParseRecord -> uk.co.jackoftradesltd.backend.parser.realm.RealmAssembler -> MagicRealm.

**class `RealmReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load(String, String)` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `realm(String, String, String)` | — | ✔ |
| `byName(List<MagicRealm>, String)` | — | ✘ |
| `field(MagicRealm, String)` | — | ✘ |
| `realFileLoadsAllFourRealmsWithNoErrors()` | — | ✘ |
| `everyFieldResolvesForTheArcaneRealm()` | — | ✘ |
| `eachRealmKeepsItsOwnStatVerbNounsFromTheRealFile()` | — | ✘ |
| `unknownStatIsReportedAndTheRealmDropped()` | — | ✘ |
| `anyBookNounLoadsAsRawText()` | — | ✘ |
| `oneBadRealmIsDroppedButTheOthersStillLoad()` | — | ✘ |
| `recordCountMismatchIsReportedButTheValidRealmStillLoads()` | — | ✘ |
| `aMissingOptionalFieldDefaultsToEmpty()` | — | ✘ |
| `aRecordWithOnlyANameIsAHardError()` | — | ✘ |
| `commentsAndBlankLinesAreSkippedEvenMidRecord()` | — | ✘ |
| `crlfLineEndingsParseCleanly()` | — | ✘ |
| `freeTextValuesPreserveInternalSpacesAndCase()` | — | ✘ |
| `fieldsAreAcceptedInAnyOrderAfterName()` | — | ✘ |
| `anEmptyValueAfterADirectiveIsAHardError()` | — | ✘ |


##### `RegistrySeeding.java`

**Responsible for:** Finds the private static store behind a registry so a test can seed it directly.

**class `RegistrySeeding`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `REGISTRIES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RegistrySeeding()` | — | ✘ |
| `resolve(String)` | — | ✔ |


##### `RoomProfileReaderTest.java`

**Responsible for:** Reader/assembly tests for RoomProfileReader: room_template.txt text -> RoomProfileLexer/RoomProfileGrammar -> RoomProfileParseRecord -> RoomProfileAssembler -> RoomTemplate.

**class `RoomProfileReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/4 documented): ✔ `REAL_FILE`, `EXPECTED_TEMPLATES`, `GOOD_RECORD` — ✘ `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✔ |
| `cleanLoadOfTheRealFileReportsNoErrorsAndAllTemplates()` | — | ✘ |
| `theFirstTemplateResolvesEveryField()` | — | ✔ |
| `aNamedTvalAndAFlagsLineBothResolve()` | — | ✔ |
| `aMultiWordTvalNameResolves()` | — | ✔ |
| `everyTemplateInTheRealFileIsWithinBoundsAndRectangular()` | — | ✔ |
| `theFixtureRecordLoadsCleanly()` | — | ✔ |
| `rowsBeyondTheTemplateMaximumDropsOnlyThatRecord()` | — | ✔ |
| `columnsOutsideTheTemplateBoundsAreRejectedAtBothEnds()` | — | ✔ |
| `aRaggedMapRowDropsTheRecordAndReportsEveryBadRow()` | — | ✔ |
| `tooFewMapRowsDropsTheRecord()` | — | ✔ |
| `anUnknownTvalDropsTheRecord()` | — | ✔ |
| `anUnknownFlagDropsTheRecordAndReportsEveryBadFlag()` | — | ✔ |
| `recordCountMismatchIsReportedButTemplatesStillLoad()` | — | ✔ |
| `aMissingMandatoryDirectiveFailsClosed()` | — | ✔ |
| `aRecordWithNoMapFailsClosed()` | — | ✔ |
| `aHashInAMapRowIsGraniteNotAComment()` | — | ✔ |
| `parseReturnsTheSameItemsAsParseWithResults()` | — | ✔ |
| `aMissingFileThrows()` | — | ✘ |


##### `RuneInitTest.java`

**Responsible for:** Tests Rune#initRunes() against the real shipped data files, which is the only way to see whether the port agrees with the original: the rune list is entirely derived, so every count in it is a consequence of a rule ported from C's init_rune (src/obj-knowledge.c) meeting the data in lib/gamedata.

**class `RuneInitTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/27 documented): ✔ `EXCLUDED_SUBTYPES`, `CURSE_NAMES_IN_C_ORDER` — ✘ `UI_RENDERER_FILE`, `UI_BASE_FILE`, `UI_ENTRY_FILE`, `PROPERTY_FILE`, `PROJECTION_FILE`, `BRAND_FILE`, `SLAY_FILE`, `CURSE_FILE`, `PAIN_FILE`, `MONSTER_BASE_FILE`, `SUMMON_FILE`, `OBJECT_BASE_FILE`, `savedRenderers`, `savedUiBases`, `savedUiEntries`, `savedProperties`, `savedProjections`, `savedBrands`, `savedSlays`, `savedCurses`, `savedPains`, `savedMonsterBases`, `savedSummons`, `savedObjectBases`, `savedRunes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `loadedRunes()` | — | ✔ |
| `varietiesOf(RuneGroup)` | — | ✘ |
| `statBackedModifiersResolveThroughTheStatsCountAsModsPath()` | — | ✔ |
| `initRunesBuildsAListWithoutThrowing()` | — | ✘ |
| `everyRuneStartsWithNoAutoInscription()` | — | ✘ |
| `noTwoRunesShareASubject()` | — | ✘ |
| `initRunesIsIdempotent()` | — | ✘ |
| `runesAreGroupedInCsSectionOrderWithNoGroupSplitInTwo()` | — | ✔ |
| `combatRunesAreTheThreeEnchantmentsInOrderAndExcludeTheSentinel()` | — | ✘ |
| `everyModifierGetsExactlyOneRune()` | — | ✔ |
| `modifierRunesCarryTheirResolvedProperty()` | — | ✘ |
| `resistRunesCoverAcidThroughDisenchantmentAndNoFurther()` | — | ✔ |
| `resistRunesCarryTheMatchingProjection()` | — | ✘ |
| `brandsWithTheSameNameShareOneRune()` | — | ✔ |
| `eachBrandRuneHoldsTheLastEntryInTheFileForItsName()` | — | ✔ |
| `everyBrandNameInTheDataHasARune()` | — | ✘ |
| `slaysKillingTheSameMonstersShareOneRune()` | — | ✔ |
| `eachSlayRuneHoldsTheLastEntryInTheFileForItsMonsters()` | — | ✔ |
| `slayRunesPartitionTheSlaysExactly()` | — | ✔ |
| `everyCurseGetsItsOwnRune()` | — | ✘ |
| `flagRunesAreExactlyTheFlagsWithALearnableSubtype()` | — | ✔ |
| `theRuneListIsNinetyNineRunesLong()` | — | ✔ |
| `resistRuneNamesArePrefixedProjectionNames()` | — | ✔ |
| `theTwoElementsWhoseProjectionIsNamedDifferentlyUseTheProjectionName()` | — | ✔ |
| `brandAndSlayRuneNamesMatchCsFormats()` | — | ✔ |
| `curseRuneNamesAreTheDataNamesWithCurseAppended()` | — | ✔ |
| `theUnwrappedVarietiesReturnTheirSubjectsNameAsIs()` | — | ✔ |
| `everyRuneInTheListHasANonBlankName()` | — | ✔ |
| `noTwoRunesShareAName()` | — | ✔ |
| `theRuneAccessorsAgreeWithTheStoredList()` | — | ✔ |
| `getRunesHandsBackAnImmutableList()` | — | ✔ |
| `anEarlierResultIsUnaffectedByALaterSetRunes()` | — | ✔ |
| `getRunesReturnsTheSameListUntilTheRunesAreRebuilt()` | — | ✔ |
| `aSnapshotStillSeesLaterInscriptions()` | — | ✔ |
| `noFlagRuneCarriesAnExcludedSubtype()` | — | ✘ |
| `initRunesThrowsWhenAPropertyCannotBeResolved()` | — | ✔ |
| `initRunesThrowsWhenAnElementHasNoProjection()` | — | ✘ |
| `everyRuneIsFoundByTheSubjectItHolds()` | — | ✔ |
| `theFixedSubjectsAreFoundByTheirConstants()` | — | ✔ |
| `aBrandFindsItsRuneEvenWhenItIsNotTheOneHeld()` | — | ✔ |
| `aSlayFindsItsRuneEvenWhenItIsNotTheOneHeld()` | — | ✔ |
| `anElementWithNoResistRuneIsNotFound()` | — | ✔ |
| `aFlagWithNoRuneIsNotFound()` | — | ✔ |


##### `RuneVarietyTest.java`

**Responsible for:** Unit tests for the types uk.co.jackoftradesltd.middle.objects.Rune is built out of, needing no loaded data: the RuneVariety tagged union and its RuneGroup mapping, the element and combat-rune enums, and the two equality rules the rune de-duplication depends on.

**class `RuneVarietyTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `slay(String, String, MonsterRaceFlag, int)` | — | ✘ |
| `brand(String, String, int)` | — | ✘ |
| `curse(String)` | — | ✘ |
| `curse(String, String)` | — | ✔ |
| `property(ObjPropertyType, String)` | — | ✔ |
| `projection(ProjectionEnum, String)` | — | ✔ |
| `eachVarietyMapsToItsCGroup()` | — | ✔ |
| `runeGroupOrdinalsAndLabelsMatchTheCTables()` | — | ✔ |
| `varietyKeysWorkAsMapKeys()` | — | ✔ |
| `keysOfDifferentVarietiesAreNeverEqual()` | — | ✔ |
| `combatRunesAreDeclaredInCsOrderWithTheSentinelLast()` | — | ✔ |
| `combatRuneDescriptionsPortTheCTable()` | — | ✔ |
| `theFourWrappedVarietiesUseCsFormatStrings()` | — | ✔ |
| `combatRunesAreNamedFromTheCRuneTable()` | — | ✔ |
| `everyVarietyAnswersRuneNameThroughTheInterface()` | — | ✔ |
| `everyVariety()` | — | ✔ |
| `theSixDataDrivenDescriptionsUseCsFormatStrings()` | — | ✔ |
| `aResistDescriptionDoesNotRepeatTheTitlesPrefix()` | — | ✔ |
| `aWrappedTitleNeverLeaksIntoItsDescription()` | — | ✔ |
| `aModifierAndAFlagShareTheirUnadornedName()` | — | ✔ |
| `aCurseIsDescribedFromItsDescriptionNotItsName()` | — | ✔ |
| `theCombatDescriptionsPortCsThreeLiterals()` | — | ✔ |
| `onlyTheCombatDescriptionsEndWithoutAFullStop()` | — | ✔ |
| `everyVarietyAnswersRuneDescThroughTheInterface()` | — | ✔ |
| `theCountSentinelIsNotARuneAndHasNoDescription()` | — | ✔ |
| `aBrandAndAResistOfTheSameNameDescribeDifferentThings()` | — | ✔ |
| `exactlyAcidThroughDisenchantmentHaveResistRunes()` | — | ✔ |
| `baseElementsAreTheFourPhysicalTypesAndAllHaveResistRunes()` | — | ✔ |
| `everyElementHasAProjectionOfTheSameName(ElementEnum)` | — | ✔ |
| `getFromElementEnumReturnsNullRatherThanThrowingOnAMiss()` | — | ✘ |
| `slaysMatchOnRaceFlagAndBaseNotOnNameOrMultiplier()` | — | ✔ |
| `slaysWithDifferentNamesButTheSameTargetsStillMatch()` | — | ✘ |
| `slayMatchingIsReflexiveAndSymmetric()` | — | ✘ |
| `brandEqualityIsStricterThanTheRuneGroupingRule()` | — | ✔ |
| `equalBrandsShareAHashCode()` | — | ✘ |
| `propertyWrappersOfTheSamePayloadAreEqual()` | — | ✔ |
| `wrappersOverTheSameModifierDifferIfTaggedStatRatherThanMod()` | — | ✔ |
| `propertyWrappersOfDifferentPayloadsAreNotEqual()` | — | ✘ |
| `propertyWrappersAreNotUsableAsHashKeys()` | — | ✔ |


##### `ShapeReaderTest.java`

**Responsible for:** End-to-end throughput tests for ShapeReader: file text -> ShapeLexer/ ShapeGrammar -> ShapeParseRecord -> ShapeAssembler -> PlayerShape.

**class `ShapeReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withHeader(int, String)` | — | ✘ |
| `shape(String, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `byName(List<PlayerShape>, String)` | — | ✘ |
| `realFileLoadsAllNineShapesWithNoErrors()` | — | ✘ |
| `nameOnlyNormalShapeLoadsWithEmptyContributions()` | — | ✘ |
| `combatAndSkillsResolveOnTheRealFile()` | — | ✘ |
| `valuesLineSplitsIntoModifiersAndResistancesAndMergesRepeatedLines()` | — | ✘ |
| `objectFlagsAccumulateAcrossTwoLines()` | — | ✘ |
| `repeatedBlowsAreAllKeptWithDuplicates()` | — | ✘ |
| `effectsAreAssembledOnTheRealFile()` | — | ✘ |
| `unknownObjectFlagIsReportedAndSkipped()` | — | ✘ |
| `unknownPlayerFlagIsReportedAndSkipped()` | — | ✘ |
| `unknownModifierIsReportedAndSkipped()` | — | ✘ |
| `unknownResistanceElementIsReportedAndSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidShapeStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `recordNotOpeningWithNameIsAHardError()` | — | ✘ |


##### `SlayReaderTest.java`

**Responsible for:** End-to-end throughput tests for SlayReader: file text -> SlayLexer/ SlayGrammar -> SlayParseRecord -> SlayAssembler -> Slay.

**class `SlayReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/7 documented): ✔ `KNOWN_BASE` — ✘ `REAL_FILE`, `PAIN_FILE`, `BASE_FILE`, `savedPains`, `savedBases`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `slay(String, String, String, String, String)` | — | ✔ |
| `raceFlagSlay(String, String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `realFileLoadsAllElevenSlaysWithNoErrors()` | — | ✘ |
| `syntheticRaceFlagSlayAssembles()` | — | ✘ |
| `syntheticBaseSlayResolvesAgainstTheLoadedMonsterBases()` | — | ✘ |
| `bothRaceFlagAndBaseIsReportedAndSkipped()` | — | ✘ |
| `unknownRaceFlagIsReportedAndSkipped()` | — | ✘ |
| `unknownBaseIsReportedAndSkipped()` | — | ✘ |
| `neitherRaceFlagNorBaseIsReportedAndSkipped()` | — | ✘ |
| `overflowingMultiplierIsReportedAndSkipped()` | — | ✘ |
| `overflowingOMultiplierIsReportedAndSkipped()` | — | ✘ |
| `overflowingPowerIsReportedAndSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `missingCodeHeaderIsAHardError()` | — | ✘ |
| `twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads()` | — | ✘ |
| `raceFlagSlayWith(String, String, String, String, String)` | — | ✔ |


##### `SummonReaderTest.java`

**Responsible for:** End-to-end throughput tests for SummonReader: file text -> SummonLexer/ SummonGrammar -> SummonParseRecord -> SummonAssembler -> Summon.

**class `SummonReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/7 documented): ✔ `KNOWN_BASE` — ✘ `REAL_FILE`, `PAIN_FILE`, `BASE_FILE`, `savedPains`, `savedBases`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `withHeader(int, String)` | — | ✘ |
| `summon(String, String, String, String, String)` | — | ✔ |
| `plainSummon(String)` | — | ✔ |
| `load(String, String)` | — | ✘ |
| `byName(List<Summon>, String)` | — | ✘ |
| `realFileLoadsAllSeventeenSummonsWithNoErrors()` | — | ✘ |
| `absentRaceFlagResolvesToRfNoneAndTheRecordStillLoads()` | — | ✘ |
| `digitBearingRaceFlagResolves()` | — | ✘ |
| `baseResolvesAgainstTheLoadedMonsterBases()` | — | ✘ |
| `invalidMessageTypeIsReportedAndSkipped()` | — | ✘ |
| `presentButInvalidRaceFlagIsReportedAndSkipped()` | — | ✘ |
| `unknownBaseIsReportedAndSkipped()` | — | ✘ |
| `unresolvableFallbackIsReportedButTheRecordStillLoads()` | — | ✘ |
| `forwardFallbackReferenceResolvesToTheLaterRecord()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `missingRecordCountHeaderIsAHardError()` | — | ✘ |
| `recordNotOpeningWithNameIsAHardError()` | — | ✘ |
| `twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads()` | — | ✘ |


##### `TerrainFeatureAssemblerTest.java`

**Responsible for:** Unit tests for TerrainFeatureAssembler, driven from hand-built TerrainFeatureParseRecords rather than through the grammar.

**class `TerrainFeatureAssemblerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `record(String)` | — | ✔ |
| `full(String, String, String, String, List<String>, String, String, String, String, int)` | — | ✘ |
| `assemble(List<TerrainFeatureParseRecord>, List<String>)` | — | ✘ |
| `assemble(TerrainFeatureParseRecord, List<String>)` | — | ✘ |
| `fullRecordResolvesEveryDirective()` | — | ✔ |
| `absentOptionalDirectivesTakeTheZeroedDefaults()` | — | ✔ |
| `recordsComeBackInFileOrder()` | — | ✘ |
| `codeNoneIsValid()` | — | ✔ |
| `codeMaxIsRefusedAndTheRecordSkipped()` | — | ✔ |
| `refusedMaxDoesNotCollideWithRealNone()` | — | ✔ |
| `unknownCodeIsReportedWithItsLine()` | — | ✘ |
| `codeLookupIsCaseSensitive()` | — | ✔ |
| `repeatedCodeThrows()` | — | ✔ |
| `mimicNoneIsValidAndIsAMimic()` | — | ✔ |
| `mimicMaxIsRefusedAndTheRecordSkipped()` | — | ✔ |
| `unknownMimicIsReportedAndTheRecordSkipped()` | — | ✘ |
| `mimicLookupIsCaseSensitive()` | — | ✘ |
| `flagMaxIsRefusedAndTheRecordSkipped()` | — | ✔ |
| `flagNoneIsValid()` | — | ✔ |
| `unknownFlagIsReportedAndTheRecordSkipped()` | — | ✘ |
| `everyBadFlagOnTheLineIsReported()` | — | ✔ |
| `diggingInsideOneToFiveIsAccepted(String)` | — | ✔ |
| `diggingOutsideOneToFiveIsRefused(String)` | — | ✘ |
| `overflowingDiggingIsAFormatError()` | — | ✘ |
| `priorityZeroIsAccepted()` | — | ✘ |
| `negativePriorityIsRefused()` | — | ✔ |
| `overflowingPriorityIsAFormatError()` | — | ✘ |
| `lookStringsWithoutATrailingSpaceGetOne()` | — | ✔ |
| `lookStringsAlreadyEndingInASpaceAreLeftAlone()` | — | ✔ |
| `absentLookStringsStayEmpty()` | — | ✔ |
| `resistFlagResolvesIncludingDigitTails()` | — | ✘ |
| `resistFlagNoneIsValid()` | — | ✔ |
| `unknownResistFlagIsReportedAndTheRecordSkipped()` | — | ✘ |
| `unknownColourIsReportedAndTheRecordSkipped()` | — | ✔ |
| `badRecordsAreSkippedAndGoodOnesSurvive()` | — | ✔ |
| `emptyInputGivesEmptyResult()` | — | ✘ |


##### `TerrainReaderTest.java`

**Responsible for:** Reader/assembly tests for TerrainReader (grammar-suite reader track R4), driving TerrainFeatureLexer/TerrainFeatureGrammar through the shared GrammarDriver into Feature objects.

**class `TerrainReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/3 documented): ✔ `VALID_RECORD` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✘ |
| `cleanLoadOfTheRealFileReportsNoErrorsAndAll25Features()` | — | ✘ |
| `colonGlyphTerrainTokenisesAndLoads()` | — | ✔ |
| `missingRecordCountHeaderIsReportedWithNoItems()` | — | ✘ |
| `recordCountMismatchIsReportedButValidRecordStillLoads()` | — | ✘ |
| `unknownTerrainCodeIsReportedAndRecordSkipped()` | — | ✘ |
| `unknownMimicCodeIsReportedAndRecordSkipped()` | — | ✘ |
| `unknownTerrainFlagIsReportedAndRecordSkipped()` | — | ✘ |
| `unknownResistFlagIsReportedAndRecordSkipped()` | — | ✘ |
| `overflowingPriorityIsReportedAndRecordSkipped()` | — | ✘ |
| `overflowingDiggingIsReportedAndRecordSkipped()` | — | ✘ |
| `digitBearingFlagTokenisesAndResolves()` | — | ✔ |
| `twoDifferentErrorsAreEachLoggedAndGoodRecordSurvives()` | — | ✘ |


##### `TrapReaderTest.java`

**Responsible for:** End-to-end throughput tests for TrapReader: file text -> TrapLexer/ TrapGrammar -> TrapParseRecord -> TrapAssembler -> TrapKind, the exact chain TerrainDataLoader.loadTraps() drives.

**class `TrapReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/8 documented): ✘ `REAL_FILE`, `PAIN_FILE`, `BASE_FILE`, `SUMMON_FILE`, `savedPains`, `savedBases`, `savedSummons`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `loadReal()` | — | ✘ |
| `byDescription(List<TrapKind>, String)` | — | ✘ |
| `tempFile(String, String)` | — | ✘ |
| `realFileLoadsAll40TrapsWithNoErrors()` | — | ✘ |
| `crossoverDescriptionsResolveOnTheRealFile()` | — | ✘ |
| `textHoldsDescDirectiveWhileDescriptionHoldsNameField()` | — | ✘ |
| `saveFlagsAreObjectFlagsAndTrapFlagsAreSeparate()` | — | ✘ |
| `effectsAndXtraEffectsAccumulatePerRecord()` | — | ✘ |
| `trapKindIndicesAreDistinctAndFileOrdered()` | — | ✘ |
| `unknownTrapFlagIsSoftErrorAndDropsTheRecord()` | — | ✘ |
| `unknownSaveFlagIsSoftErrorAndDropsTheRecord()` | — | ✘ |


##### `UIEntryAssemblerTest.java`

**Responsible for:** Unit tests for UIEntryAssembler (grammar-suite assembler track).

**class `UIEntryAssemblerTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `KNOWN_RENDERER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistries()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `rec(String, String, String, String, String, String)` | — | ✔ |
| `full(String, String, String, String, List<String>, String, String, String, List<String>, List<String>)` | — | ✔ |
| `nameTagResolvesToTheConcreteElementParameter()` | — | ✘ |
| `resolvesAKnownRendererFromTheRegistry()` | — | ✘ |
| `unknownRendererIsSkippedAndReported()` | — | ✘ |
| `invalidCombineIsSkippedAndReported()` | — | ✘ |
| `unknownTemplateIsSkippedAndReported()` | — | ✘ |
| `illegalParameterKindIsSkippedAndReported()` | — | ✘ |
| `partialResultsSurviveABadRecord()` | — | ✘ |
| `overridePriorityIndexReadsTheExistingEntrysElementIndex()` | — | ✘ |
| `overridePriorityNegativeIndexReadsTheExistingEntrysElementIndex()` | — | ✘ |
| `statOrElementResolvesFromTheParameterKind()` | — | ✘ |
| `overridePriorityIndexReadsTheExistingEntrysStatIndex()` | — | ✘ |
| `overridePriorityIndexWithACategoryAttachesToTheCategoryNotTheEntry()` | — | ✘ |
| `createPathKeepsTheRecordsOwnFlagsOnEveryElementEntry()` | — | ✘ |
| `createPathWithNoFlagsLeavesTheFlagSetEmpty()` | — | ✘ |
| `newEntryWithNoCombinerIsRejectedAndNotAdded()` | — | ✘ |
| `elementLabelDefaultsToTheElementNameUnlessTheRecordGivesOne()` | — | ✘ |
| `statLabelDefaultsToTheStatName()` | — | ✘ |
| `shortenedLabelIsCopiedToEveryParameterisedEntry()` | — | ✘ |
| `indexSchemeGivesEachElementEntryItsIndexAsDefaultPriority()` | — | ✘ |
| `negativeIndexSchemeGivesEachStatEntryTheNegatedIndex()` | — | ✘ |
| `numericPriorityIsTheDefaultPriorityOfAnEntryWithNoCategories()` | — | ✘ |
| `categoryBeforeAPriorityLineTakesTheParameterisedPriority()` | — | ✘ |
| `categoryAfterAPriorityLineIsLeftForTheFinishingPass()` | — | ✘ |
| `templateSuppliesCombinerFlagsAndCategoriesAndDropsTemplateOnly()` | — | ✘ |
| `overrideOrsFlagsIntoTheExistingEntryAndKeepsThemWhenNoneAreGiven()` | — | ✘ |
| `overrideReplacesTheLabelAndCombinerWhenGiven()` | — | ✘ |
| `overrideAddsANewCategoryOnceAndNotAgainWhenAlreadyPresent()` | — | ✘ |
| `parameterOnAnAlreadyExistingNameIsRejected()` | — | ✘ |


##### `UIEntryBaseAssemblerTest.java`

**Responsible for:** Unit tests for UIEntryBaseAssembler (grammar-suite assembler track).

**class `UIEntryBaseAssemblerTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `KNOWN_RENDERER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRenderers()` | — | ✘ |
| `rec(String, String, String, String, String, List<String>)` | — | ✘ |
| `resolvesRendererAndCombineAndCarriesRawFieldsThrough()` | — | ✘ |
| `unknownRendererIsSkippedAndReported()` | — | ✘ |
| `invalidCombineIsSkippedAndReported()` | — | ✘ |
| `partialResultsSurviveABadRecord()` | — | ✘ |
| `createPathInsertsATemplateOnlyEntryWithUnsetCategoryPriorities()` | — | ✘ |
| `aRepeatedNameMergesNewCategoriesIntoTheExistingEntryRatherThanDuplicatingIt()` | — | ✘ |


##### `UIEntryBaseReaderTest.java`

**Responsible for:** End-to-end tests for the migrated UIEntryBase pipeline: text file → ANTLR lexer/parser → UIEntryBaseReader → UIEntryBaseAssembler → resolved UIEntryBase domain objects, wrapped in a ParseResult.

**class `UIEntryBaseReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRenderers()` | — | ✘ |
| `tempFile(String, String)` | — | ✘ |
| `record(String, String, String)` | — | ✔ |
| `cleanLoadOfTheRealFileResolvesAllTemplates()` | — | ✘ |
| `recordCountMismatchIsLoggedButValidRecordSurvives()` | — | ✘ |
| `unknownRendererIsLoggedAndRecordSkipped()` | — | ✘ |
| `missingRecordCountHeaderFailsClosed()` | — | ✘ |
| `recordMissingMandatoryDescFailsClosed()` | — | ✘ |
| `multipleDistinctFailuresAreLoggedAndGoodRecordsSurvive()` | — | ✘ |


##### `UIEntryReaderTest.java`

**Responsible for:** End-to-end tests for the UIEntry pipeline: text file → ANTLR lexer/parser → UIEntryReader → UIEntryAssembler → resolved UIEntry domain objects, wrapped in a ParseResult.

**class `UIEntryReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistries()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `tempFile(String, String)` | — | ✘ |
| `byName(List<UIEntry>, String)` | — | ✘ |
| `cleanLoadOfTheRealFileResolvesAllEntries()` | — | ✘ |
| `parameterAndNameTagSplitSurviveTheWholePipeline()` | — | ✘ |
| `recordCountMismatchIsLoggedButValidRecordSurvives()` | — | ✘ |
| `unknownRendererIsLoggedAndRecordSkipped()` | — | ✘ |
| `unknownTemplateIsLoggedAndRecordSkipped()` | — | ✘ |
| `illegalParameterKindIsLoggedAndRecordSkipped()` | — | ✘ |
| `recordCountMismatchIsReportedEvenWhenTheRecordIsSkipped()` | — | ✘ |
| `matchingRecordCountAddsNoCountError()` | — | ✘ |
| `parseReturnsTheSameItemsAsParseWithResults()` | — | ✘ |
| `missingFileIsAnIOException()` | — | ✘ |
| `syntaxErrorAfterAValidHeaderFailsClosedAndSkipsTheCountCheck()` | — | ✘ |
| `missingRecordCountHeaderFailsClosed()` | — | ✘ |


##### `UIEntryRendererAssemblerTest.java`

**Responsible for:** Unit tests for UIEntryRendererAssembler (grammar-suite assembler track).

**class `UIEntryRendererAssemblerTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `FLAG`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rec(String, String, String, String, String, String, String)` | — | ✘ |
| `resolvesAllFieldsWhenPresent()` | — | ✘ |
| `emptyOptionalFieldsFallBackToBackendDefaults()` | — | ✘ |
| `unknownCodeIsSkippedAndReported()` | — | ✘ |
| `presentButInvalidSignIsSkippedAndReported()` | — | ✘ |
| `partialResultsSurviveABadRecord()` | — | ✘ |


##### `UIEntryRendererReaderTest.java`

**Responsible for:** End-to-end tests for the migrated UIEntryRenderer pipeline: text file → ANTLR lexer/parser → UIEntryRendererReader → UIEntryRendererAssembler → resolved UIEntryRenderer domain objects, wrapped in a ParseResult.

**class `UIEntryRendererReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/4 documented): ✔ `FLAG`, `FLAG_HEADER` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✘ |
| `cleanLoadOfTheRealFileResolvesAllRenderers()` | — | ✘ |
| `omittedColourFieldsDefaultThroughTheWholePipeline()` | — | ✘ |
| `unknownCodeIsLoggedAndRecordSkipped()` | — | ✘ |
| `invalidSignIsLoggedAndRecordSkipped()` | — | ✘ |
| `recordCountMismatchIsLoggedButValidRecordSurvives()` | — | ✘ |
| `missingRecordCountHeaderFailsClosed()` | — | ✘ |
| `recordMissingMandatoryCodeFailsClosed()` | — | ✘ |
| `multipleDistinctFailuresAreEachLoggedAndGoodRecordsSurvive()` | — | ✘ |
| `loadOne(String)` | — | ✘ |
| `shortColoursAreToppedUpFromTheBackendDefaultAtTheSameIndex()` | — | ✘ |
| `shortLabelColoursAreToppedUpFromTheBackendDefaultAtTheSameIndex()` | — | ✘ |
| `shortSymbolsAreToppedUpFromTheBackendDefaultAtTheSameIndex()` | — | ✘ |
| `aPaletteEqualToTheDefaultLengthIsUnchanged()` | — | ✘ |
| `aPaletteLongerThanTheDefaultIsKeptWhole()` | — | ✘ |
| `palettesAreCappedAtSixtyFourEntries()` | — | ✘ |
| `explicitNdigitBelowOneIsRejectedButABackendDefaultOfZeroIsNot()` | — | ✘ |
| `explicitNdigitIsStoredAndOverridesTheBackendDefault()` | — | ✘ |
| `everySignNameResolvesToItsEnumValue()` | — | ✘ |
| `everyBackendDefaultsFromTheCHeaderWhenOnlyNameAndCodeAreGiven()` | — | ✘ |
| `codeNoneIsRejectedAsAnUnknownBackend()` | — | ✘ |
| `directivesAfterNameMayComeInAnyOrder()` | — | ✘ |
| `combineUnitsAndCombinedRendererDirectivesAreAccepted()` | — | ✘ |
| `aRepeatedNameMergesIntoOneRendererFieldByField()` | — | ✘ |


##### `VaultReaderTest.java`

**Responsible for:** End-to-end throughput test for VaultReader: file text -> VaultLexer/ VaultGrammar -> VaultParseRecord -> VaultAssembler -> Vault.

**class `VaultReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/6 documented): ✔ `EXPECTED_VAULTS`, `EXPECTED_FLAGGED`, `EXPECTED_UNCAPPED`, `GOOD_RECORD` — ✘ `REAL_FILE`, `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `byName(List<Vault>, String)` | — | ✔ |
| `bootstrap()` | — | ✔ |
| `cleanup()` | — | ✔ |
| `names(ParseResult<Vault>)` | — | ✔ |
| `tempFile(String, String)` | — | ✔ |
| `parsesEveryVaultWithoutError()` | — | ✘ |
| `readsTheFirstVaultsFields()` | — | ✘ |
| `rewritesAZeroMaxDepthToTheWorldMaximum()` | — | ✔ |
| `everyLayoutMatchesItsDeclaredDimensions()` | — | ✔ |
| `noVaultExceedsItsRoomTypeSizeCap()` | — | ✔ |
| `readsTheFlagsLines()` | — | ✘ |
| `resolvesEveryVaultType()` | — | ✔ |
| `theFixtureRecordLoadsCleanly()` | — | ✔ |
| `anUnknownVaultTypeDropsOnlyThatRecord()` | — | ✔ |
| `theVaultTypeNameIsMatchedCaseInsensitively()` | — | ✔ |
| `rowsOutsideTheTypesBoundsAreRejectedAtBothEnds()` | — | ✔ |
| `columnsOutsideTheTypesBoundsAreRejectedAtBothEnds()` | — | ✔ |
| `theSizeCapComesFromTheRecordsOwnType()` | — | ✔ |
| `aRaggedMapDropsTheRecord()` | — | ✔ |
| `tooFewMapRowsDropsTheRecord()` | — | ✔ |
| `unknownFlagsDropTheRecordAndReportEveryBadFlag()` | — | ✔ |
| `everyFlagsLineIsRead()` | — | ✔ |
| `anOverflowingIntegerDropsTheRecord()` | — | ✔ |
| `recordCountMismatchIsReportedButVaultsStillLoad()` | — | ✔ |
| `aMissingMandatoryDirectiveFailsClosed()` | — | ✔ |
| `directivesOutOfOrderFailClosed()` | — | ✔ |
| `aMapRowKeepsItsHashesAndItsTrailingPadding()` | — | ✔ |
| `parseReturnsTheSameItemsAsParseWithResults()` | — | ✔ |
| `aMissingFileThrows()` | — | ✘ |


##### `VisualsReaderTest.java`

**Responsible for:** Full test suite for the visuals.txt parser.

**class `VisualsReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `REAL_FILE`, `FANCY_CYCLES`, `FLICKER_GROUP_CYCLES`

**class `VisualsReaderTest.EndToEnd`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flickerBlock(char, String, char)` | — | ✔ |
| `cycleBlock(String, String, char)` | — | ✔ |
| `write(String, String)` | — | ✘ |
| `realFileLoadsCyclerWithNoErrorsAsASingleAggregate()` | — | ✘ |
| `realFileCyclerHasTheTwoShippedGroupsAtTheirExpectedSizes()` | — | ✘ |
| `realFileFancyGroupContainsANamedCycleFromTheFile()` | — | ✘ |
| `realFileLoadsFlickerTableWithNoErrorsAsASingleAggregate()` | — | ✘ |
| `neitherHalfReportsErrorsSoGameConstantsWouldPopulateBothTables()` | — | ✘ |
| `convenienceParseCyclerReturnsTheAggregateDirectly()` | — | ✘ |
| `convenienceParseFlickerReturnsTheAggregateDirectly()` | — | ✘ |
| `minimalSyntheticFileLoadsBothHalves()` | — | ✘ |
| `interleavedFlickerAndCycleBlocksBothCollect()` | — | ✘ |
| `flickerOnlyFileYieldsAnEmptyCyclerButNoErrors()` | — | ✘ |
| `cycleOnlyFileYieldsAFlickerTableWithNoErrors()` | — | ✘ |
| `flickerBlockWithNoColourLinesIsAHardError()` | — | ✘ |
| `cycleBlockWithNoColourLinesIsAHardError()` | — | ✘ |
| `invalidColourCodeIsAHardLexerError()` | — | ✘ |
| `fileWithNoBlocksIsAHardError()` | — | ✘ |

**class `VisualsReaderTest.CycleAssemblerUnit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `record(String, String, List<String>)` | — | ✘ |
| `groupsRecordsByGroupThenName()` | — | ✘ |
| `emptyColourStringIsASoftErrorAndSkipsTheRecord()` | — | ✘ |
| `oneGoodRecordSurvivesAlongsideOneSoftFailure()` | — | ✘ |
| `unknownColourCharIsASoftErrorAndSkipsTheRecord()` | — | ✘ |
| `emptyInputYieldsASingleEmptyCycler()` | — | ✘ |

**class `VisualsReaderTest.FlickerAssemblerUnit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `record(String, List<String>)` | — | ✘ |
| `producesExactlyOneTableWrappedInAList()` | — | ✘ |
| `emptyInputStillYieldsAPresentTable()` | — | ✘ |
| `twoRecordsSharingABaseAttributeDoNotThrow()` | — | ✘ |


##### `WorldReaderTest.java`

**Responsible for:** Reader/assembly tests for WorldReader (grammar-suite reader track R4).

**class `WorldReaderTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `REAL_FILE` — ✘ `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tempFile(String, String)` | — | ✔ |
| `cleanLoadOfTheRealFileReportsNoErrorsAndAllLevels()` | — | ✘ |
| `recordCountMismatchFailsClosed()` | — | ✘ |
| `badLevelNumberFailsClosed()` | — | ✘ |
| `missingRecordCountHeaderIsReportedWithNoItems()` | — | ✘ |
| `twoDifferentErrorsAreBothReportedAndFailClosed()` | — | ✘ |


## backend/parser/grammars/gameconstants

##### `GameConstantsGrammarTest.java`

**Responsible for:** Unit tests for the generated GameConstantsGrammar parser (grammar-track step 8, parser half; paired with GameConstantsLexer).

**class `GameConstantsGrammarTest`** — class Javadoc: ✔ · type stamp: —

**class `GameConstantsGrammarTest.Errors`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✘ (override) |
| `parser(String, Errors)` | — | ✔ |
| `scalarLineExtractsCategoryAndOrderedFields()` | — | ✘ |
| `fourFieldCriticalLevelLineKeepsEveryFieldInOrder()` | — | ✘ |
| `threeFieldCriticalLevelLineKeepsEveryFieldInOrder()` | — | ✘ |
| `multipleLinesPreserveOrderAndLineNumbersAcrossComments()` | — | ✘ |
| `emptyInputProducesNoRecords()` | — | ✘ |
| `categoryWithNoFieldsIsAParseError()` | — | ✘ |


##### `GameConstantsLexerTest.java`

**Responsible for:** Unit tests for the generated GameConstantsLexer (grammar-track step 8, lexer half).

**class `GameConstantsLexerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tokens(String)` | — | ✔ |
| `lexErrors(String)` | — | ✔ |
| `scalarDirectiveTokenises()` | — | ✘ |
| `hyphenatedNamesAreSingleTokens()` | — | ✘ |
| `negativeIntegerIsOneSignedToken()` | — | ✘ |
| `criticalLevelMessageNameIsAGcMsgToken()` | — | ✘ |
| `commentsAndBlankLinesAreSkipped()` | — | ✘ |
| `crlfLineEndingsTokeniseCleanly()` | — | ✘ |
| `unrecognisedCharacterRaisesLexerError()` | — | ✘ |


## backend/parser/grammars/imports/effectblock

##### `EffectBlockLexerTest.java`

**Responsible for:** Unit tests for the generated EffectBlockLexer.

**class `EffectBlockLexerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tokens(String)` | — | ✔ |
| `lexErrors(String)` | — | ✔ |
| `effectHeaderTokenises()` | — | ✘ |
| `simpleDiceValueIsTheSimpleToken()` | — | ✘ |
| `complexDiceValueIsTheComplexToken()` | — | ✘ |
| `timeUsesTheSimpleDiceToken()` | — | ✘ |
| `exprTokenisesIntoItsFields()` | — | ✘ |
| `effectMsgCapturesFreeText()` | — | ✘ |
| `invalidDiceValueRaisesALexerError()` | — | ✘ |
| `basePlusMBonusIsSimpleDiceToken()` | — | ✘ |
| `complexBasePlusMBonusIsComplexDiceToken()` | — | ✘ |
| `basePlusWithNoBonusRaisesALexerError()` | — | ✘ |


##### `EffectBlockTest.java`

**Responsible for:** Unit tests for the generated EffectBlock parser (paired with EffectBlockLexer).

**class `EffectBlockTest`** — class Javadoc: ✔ · type stamp: —

**class `EffectBlockTest.Errors`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✘ (override) |
| `parser(String, Errors)` | — | ✔ |
| `complexDiceWithTwoExprs()` | — | ✘ |
| `simpleDiceWithNoExpr()` | — | ✘ |
| `effectBlockStopsBeforeTime()` | — | ✔ |
| `standaloneTimeRuleCapturesDice()` | — | ✔ |
| `complexDiceWithoutExprIsRejected()` | — | ✘ |
| `effectMessageIsCaptured()` | — | ✘ |
| `negativeOtherParameterIsAccepted()` | — | ✘ |
| `effectYxIsYThenX()` | — | ✘ |
| `simpleBasePlusMBonusDice()` | — | ✘ |
| `complexBasePlusMBonusDice()` | — | ✘ |


## backend/parser/grammars/projection

##### `ProjectionGrammarTest.java`

**Responsible for:** Unit tests for the generated ProjectionGrammar parser (grammar-track step 8, parser half; paired with ProjectionLexer).

**class `ProjectionGrammarTest`** — class Javadoc: ✔ · type stamp: —

**class `ProjectionGrammarTest.Errors`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✘ (override) |
| `parser(String, Errors)` | — | ✘ |
| `mandatoryOnlyRecordFillsOptionalsWithEmptyAndAppendsLineNumber()` | — | ✘ |
| `fullRecordKeepsEveryFieldVerbatim()` | — | ✘ |
| `lineNumbersSurviveSkippedComments()` | — | ✘ |
| `missingRecordCountIsAParseError()` | — | ✘ |
| `recordMissingAMandatoryFieldIsAParseError()` | — | ✘ |


##### `ProjectionLexerTest.java`

**Responsible for:** Unit tests for the generated ProjectionLexer (grammar-track step 8, lexer half).

**class `ProjectionLexerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tokens(String)` | — | ✔ |
| `lexErrors(String)` | — | ✔ |
| `aWholeRecordExercisesTagFreeTextDiceAndInteger()` | — | ✘ |
| `freeTextCapturesSpacesAndPunctuationToEndOfLine()` | — | ✘ |
| `denominatorTakesBothABareNumberAndDiceNotation()` | — | ✘ |
| `recordCountAndSignedInteger()` | — | ✘ |
| `commentsAndBlankLinesAreSkippedAndCrlfIsClean()` | — | ✘ |


## backend/parser/grammars/uientry

##### `UIEntryGrammarTest.java`

**Responsible for:** Unit tests for the generated UIEntryGrammar parser (paired with UIEntryLexer).

**class `UIEntryGrammarTest`** — class Javadoc: ✔ · type stamp: —

**class `UIEntryGrammarTest.Errors`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✘ (override) |
| `parser(String, Errors)` | — | ✔ |
| `fullRecordKeepsEveryFieldVerbatimAndCapturesTheLineNumber()` | — | ✘ |
| `minimalRecordIsJustNameAndLeavesOptionalFieldsUnset()` | — | ✘ |
| `nameTagIsCapturedSeparatelyAndTheNameKeepsTheTag()` | — | ✘ |
| `parameterLineAndNameTagAreCapturedIndependently()` | — | ✘ |
| `categoriesAreKeptSeparateByPositionRelativeToPriority()` | — | ✘ |
| `flagListWithDigitBearingNamesIsKeptVerbatim()` | — | ✘ |
| `repeatedDescLinesAreConcatenatedInOrder()` | — | ✘ |
| `integerPriorityIsKeptAsAVerbatimString()` | — | ✘ |
| `recordCountIsCapturedAndMultipleRecordsAreCollectedAcrossSkippedComments()` | — | ✘ |
| `missingRecordCountIsAParseError()` | — | ✘ |
| `fileWithNoEntriesIsAParseError()` | — | ✘ |
| `uppercaseNameValueIsAParseError()` | — | ✘ |
| `unterminatedNameTagIsAParseError()` | — | ✘ |
| `twoMalformedRecordsBothReportedThenThrownTogether()` | — | ✘ |


## backend/parser/grammars/uientrybase

##### `UIEntryBaseGrammarTest.java`

**Responsible for:** Unit tests for the generated UIEntryBaseGrammar parser (paired with UIEntryBaseLexer).

**class `UIEntryBaseGrammarTest`** — class Javadoc: ✔ · type stamp: —

**class `UIEntryBaseGrammarTest.Errors`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✘ (override) |
| `parser(String, Errors)` | — | ✘ |
| `fullRecordKeepsEveryFieldVerbatimAndCapturesTheLineNumber()` | — | ✘ |
| `repeatedCategoryLinesAreCollectedInOrder()` | — | ✘ |
| `repeatedDescLinesAreConcatenatedInOrder()` | — | ✘ |
| `digitBearingCombineAndFlagsAreKeptVerbatim()` | — | ✘ |
| `recordCountIsCapturedAndMultipleRecordsAreCollectedInOrder()` | — | ✘ |
| `lineNumbersSurviveSkippedComments()` | — | ✘ |
| `missingRecordCountIsAParseError()` | — | ✘ |
| `recordMissingTheMandatoryDescIsAParseError()` | — | ✘ |


##### `UIEntryBaseLexerTest.java`

**Responsible for:** Unit tests for the generated UIEntryBaseLexer (grammar-suite step, lexer half).

**class `UIEntryBaseLexerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tokens(String)` | — | ✔ |
| `lexErrors(String)` | — | ✔ |
| `aWholeRecordExercisesEveryDirectiveAndAllThreeModes()` | — | ✘ |
| `combineAndFlagsPushIntoFlagModeAndFlagAdmitsADigitTail()` | — | ✘ |
| `categoryAndDescAreFreeTextToEndOfLine()` | — | ✘ |
| `nameAndRendererTakeAnLcaseTokenNotFreeText()` | — | ✘ |
| `commentsAndBlankLinesAreSkippedAndCrlfIsClean()` | — | ✘ |


## backend/parser/grammars/uientryrenderer

##### `UIEntryRendererGrammarTest.java`

**Responsible for:** Unit tests for the generated UIEntryRendererGrammar parser (grammar-track step 8, parser half; paired with UIEntryRendererLexer).

**class `UIEntryRendererGrammarTest`** — class Javadoc: ✔ · type stamp: —

**class `UIEntryRendererGrammarTest.Errors`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✘ (override) |
| `parser(String, Errors)` | — | ✘ |
| `fullRecordKeepsEveryFieldVerbatimAndAppendsLineNumber()` | — | ✘ |
| `absentNdigitAndSignAreEmptyStrings()` | — | ✘ |
| `digitBearingFlagIsKeptVerbatim()` | — | ✘ |
| `recordCountIsCapturedAndMultipleRecordsAreCollectedInOrder()` | — | ✘ |
| `lineNumbersSurviveSkippedComments()` | — | ✘ |
| `missingRecordCountIsAParseError()` | — | ✘ |
| `recordWithOnlyNameAndCodeParsesWithEveryOptionalFieldEmpty()` | — | ✘ |
| `recordMissingAMandatoryFieldIsAParseError()` | — | ✘ |


##### `UIEntryRendererLexerTest.java`

**Responsible for:** Unit tests for the generated UIEntryRendererLexer (grammar-track step 8, lexer half).

**class `UIEntryRendererLexerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tokens(String)` | — | ✔ |
| `lexErrors(String)` | — | ✔ |
| `aWholeRecordExercisesEveryDirectiveAndBothTextModes()` | — | ✘ |
| `codeAndSignPushIntoFlagModeAndFlagAcceptsADigitTail()` | — | ✘ |
| `coloursTakeAColourCodeRunFromTheSharedColoursImport()` | — | ✘ |
| `symbolsIsFreeTextCapturingSpacesAndPunctuationToEndOfLine()` | — | ✘ |
| `commentsAndBlankLinesAreSkippedAndCrlfIsClean()` | — | ✘ |


## backend/parser/grammars/world

##### `WorldGrammarTest.java`

**Responsible for:** Unit tests for the generated WorldGrammar parser (grammar-track step 8, parser half; paired with WorldLexer).

**class `WorldGrammarTest`** — class Javadoc: ✔ · type stamp: —

**class `WorldGrammarTest.Errors`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `syntaxError(Recognizer<?,?>, Object, int, int, String, RecognitionException)` | — | ✘ (override) |
| `parser(String, Errors)` | — | ✔ |
| `singleRecordExtractsAllFiveFieldsVerbatim()` | — | ✘ |
| `lineNumbersSurviveSkippedComments()` | — | ✘ |
| `missingRecordCountIsAParseError()` | — | ✘ |
| `recordMissingAFieldIsAParseError()` | — | ✘ |


##### `WorldLexerTest.java`

**Responsible for:** Unit tests for the generated WorldLexer (grammar-track step 8, lexer half).

**class `WorldLexerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tokens(String)` | — | ✔ |
| `lexErrors(String)` | — | ✔ |
| `recordCountLineTokenises()` | — | ✘ |
| `levelRecordTokenises()` | — | ✘ |
| `signedIntegerIsOneToken()` | — | ✘ |
| `commentsAndBlankLinesAreSkipped()` | — | ✘ |
| `crlfLineEndingsTokeniseCleanly()` | — | ✘ |
| `unrecognisedCharacterRaisesLexerError()` | — | ✘ |


## backend/parser/playerclass

##### `ClassEquipAssemblerTest.java`

**Responsible for:** Tests the quantity and ordering rules of ClassEquipAssembler against parse_class_equip() in init.c and the uint field parsing in parser.c.

**class `ClassEquipAssemblerTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/1 documented): ✘ `savedKinds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField(String)` | — | ✘ |
| `kind(TValue, String)` | — | ✘ |
| `seedKinds()` | — | ✘ |
| `restoreKinds()` | — | ✘ |
| `food(String, String)` | — | ✘ |
| `run(List<String>, ClassEquipParseRecord)` | — | ✘ |
| `validRanges()` | — | ✘ |
| `minAboveNinetyNineRejected()` | — | ✘ |
| `maxAboveNinetyNineRejected()` | — | ✘ |
| `negativeMinRejected()` | — | ✘ |
| `negativeMaxRejected()` | — | ✘ |
| `emptyMinRejected()` | — | ✘ |
| `emptyMaxRejected()` | — | ✘ |
| `zeroAndInvertedRangesKept()` | — | ✘ |
| `unknownSvalDropped()` | — | ✘ |
| `reverseFileOrder()` | — | ✘ |
| `withEopts(String)` | — | ✘ |
| `validEoptsKept()` | — | ✘ |
| `unknownEoptDropsEntry()` | — | ✘ |
| `nonBirthEoptDropsEntry()` | — | ✘ |
| `oneBadEoptAmongValidDropsEntry()` | — | ✘ |
| `badNegatedEoptDropsOnlyThatEntry()` | — | ✘ |


## backend/parser/playertimed

##### `PlayerTimedAssemblerGradeTest.java`

**Responsible for:** Tests the grade: handling in PlayerTimedAssembler — the port of C's parse_player_timed_grade (player-timed.c:243-336).

**class `PlayerTimedAssemblerGradeTest`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (2/2 documented): ✔ `FOOD_SCALE`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✘ |
| `grade(String, String)` | — | ✔ |
| `record(String, PlayerTimedParseRecord.PlayerTimedGradeParseRecord)` | — | ✔ |
| `assemble(PlayerTimedParseRecord, List<String>)` | — | ✔ |

**class `PlayerTimedAssemblerGradeTest.FoodScaling`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `foodGradesAreScaled()` | — | ✔ |
| `otherEffectsAreNotScaled()` | — | ✔ |

**class `PlayerTimedAssemblerGradeTest.OffGrade`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `headIsZeroedAndUnnamed()` | — | ✔ |
| `declaredBandsAreNumberedFromOne()` | — | ✔ |
| `eachEffectGetsItsOwnHead()` | — | ✔ |
| `headsZeroMaximumIsUnique()` | — | ✔ |

**class `PlayerTimedAssemblerGradeTest.Validation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rejectsNonPositive()` | — | ✔ |
| `ceilingAppliesAfterScaling()` | — | ✔ |
| `gradesMustAscend()` | — | ✔ |
| `badGradeDropsTheRecord()` | — | ✔ |
| `malformedMaximumIsReported()` | — | ✔ |


## backend/parser/quest

##### `QuestAssemblerTest.java`

**Responsible for:** Unit tests for QuestAssembler, feeding QuestParseRecords straight to assemble so the integer and race handling is exercised without the lexer in the way.

**class `QuestAssemblerTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (0/3 documented): ✘ `SAURON`, `MORGOTH`, `NO_SUCH_RACE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✘ |
| `record(String, String, String)` | — | ✘ |
| `assemble(List<String>, QuestParseRecord)` | — | ✘ |
| `single(String, String, String)` | — | ✘ |
| `ordinaryRecordAssemblesEveryField()` | — | ✔ |
| `emptyRecordListGivesEmptyQuestList()` | — | ✘ |
| `indicesFollowFileOrder()` | — | ✔ |
| `levelIsStoredModulo256()` | — | ✔ |
| `levelWrapsThroughUnsignedIntBeforeTheByte()` | — | ✔ |
| `levelBeyondIntButWithinLongStillWrapsAndIsKept()` | — | ✔ |
| `levelAboveLongMaxIsDroppedAsASoftError()` | — | ✔ |
| `levelWithLeadingZeroIsReadAsDecimalNotOctal()` | — | ✔ |
| `negativeLevelAbortsTheLoad()` | — | ✔ |
| `negativeNumberAbortsTheLoad()` | — | ✘ |
| `numberIsStoredAsParsed()` | — | ✔ |
| `numberAboveIntMaxIsDroppedAsASoftError()` | — | ✔ |
| `numberWithLeadingZeroIsReadAsDecimalNotOctal()` | — | ✔ |
| `raceResolvesCaseInsensitivelyAndBySubstring()` | — | ✔ |
| `unknownRaceAbortsTheLoadWithAPlainRuntimeException()` | — | ✔ |
| `levelIsCheckedBeforeRace()` | — | ✔ |
| `raceIsCheckedBeforeNumber()` | — | ✔ |
| `droppedLevelRecordIsNeverRaceChecked()` | — | ✔ |
| `indicesStayContiguousAcrossDroppedRecords()` | — | ✔ |


## channel

##### `ChannelsTest.java`

**Responsible for:** Unit tests for the transport itself — Channels and the five views onto its two queues.

**class `ChannelsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `NOT_COMING_MILLIS`, `ENTER_INIT`, `START`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `run(String, ThrowingRunnable)` | — | ✔ |
| `assertNothingArrives(Receiver<?>, String)` | — | ✔ |

**interface `ChannelsTest.ThrowingRunnable`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `run()` | — | ✘ |

**record `ChannelsTest.CoreSenderFixture`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/1 documented): ✘ `channels`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sendNotes(String)` | — | ✔ |

**class `ChannelsTest.Wiring`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `coreSenderReachesUiReceiver()` | — | ✔ |
| `uiSenderReachesCoreReceiver()` | — | ✔ |
| `aSenderDoesNotFeedItsOwnHalfsReceiver()` | — | ✔ |
| `uiSenderDoesNotFeedUiReceiver()` | — | ✔ |
| `separateChannelsAreIndependent()` | — | ✔ |
| `accessorsReturnTheSameViewsEachTime()` | — | ✔ |

**class `ChannelsTest.EdtView`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `edtSenderReachesUiReceiver()` | — | ✔ |
| `edtSenderDoesNotReachTheCore()` | — | ✔ |
| `bothWritersShareTheUiInboxAndKeepTheirOrder()` | — | ✔ |
| `theEdtIsGivenNoReceiver()` | — | ✔ |
| `theEdtSenderIsTheSameViewEachTime()` | — | ✔ |
| `postingFromTheEdtDoesNotWaitForAReader()` | — | ✔ |

**class `ChannelsTest.Delivery`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `messagesArriveInTheOrderSent()` | — | ✔ |
| `theUiInboxCarriesBothSenders()` | — | ✔ |
| `sendingDoesNotWaitForAReader()` | — | ✔ |

**class `ChannelsTest.AcrossTwoThreads`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `receiveBlocksUntilAMessageArrives()` | — | ✔ |
| `interruptingABlockedReceiverEndsTheWait()` | — | ✔ |
| `theShutdownHandshakeCompletes()` | — | ✔ |


## channel/colour

##### `ColourEnumTest.java`

**Responsible for:** Unit tests for ColourEnum and ColourTranslation, the port of the C source's colour tables (src/ui-term.c, list-attr-codes.h).

**class `ColourEnumTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `attributeToStringGivesTheHumanReadableName()` | — | ✘ |

**class `ColourEnumTest.Lookup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSingleCharacterResolvesToTheColourWithThatCode()` | — | ✘ |
| `codesAreCaseSensitiveBecauseCaseIsWhatDistinguishesLightFromDark()` | — | ✘ |
| `theShadeColourIsCodedAsASpace()` | — | ✘ |
| `anUnusedCharacterResolvesToNull()` | — | ✘ |
| `aOneCharacterStringIsTreatedAsACode()` | — | ✘ |
| `aLongerStringIsTreatedAsADisplayName()` | — | ✘ |
| `displayNamesAreMatchedIgnoringCase()` | — | ✘ |
| `anUnknownNameResolvesToNull()` | — | ✘ |
| `theEmptyStringTakesTheNameRouteAndFindsNothing()` | — | ✘ |
| `everyColourIsFoundByItsOwnDisplayName(ColourEnum)` | — | ✘ |

**class `ColourEnumTest.TranslationTables`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyColourHasADistinctDisplayName()` | — | ✘ |
| `everyEntryInEveryTranslationRowNamesARealColour(ColourEnum)` | — | ✘ |
| `theFullColourTranslationIsTheIdentity(ColourEnum)` | — | ✘ |
| `theTranslationIndicesAreTheNineTableColumnsInOrder()` | — | ✘ |

**class `ColourEnumTest.Translating`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aMonochromeDisplayCollapsesColoursOntoWhite()` | — | ✘ |
| `lightingBrightensAndShadowingDarkens()` | — | ✘ |
| `shadeIsUnchangedByEveryTranslation()` | — | ✘ |
| `translateColourAppliesTheStepTheGivenNumberOfTimes()` | — | ✘ |
| `translatingZeroTimesReturnsTheStartingColour()` | — | ✘ |
| `translatingANegativeNumberOfTimesIsAlsoANoOp()` | — | ✘ |
| `repeatedTranslationSettlesOnAFixedPoint()` | — | ✘ |
| `repeatedTranslationNeverFallsOffIntoNull(ColourEnum)` | — | ✘ |


## channel/directories

##### `AngbandDirsTest.java`

**Responsible for:** Unit tests for AngbandDirs - the port of C's ANGBAND_DIR_* globals ([C] src/init.c) and of the name/pointer table change_path_values ([C] src/main.c) that backs the -d switch.

**class `AngbandDirsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `savedPath` — ✘ `SEP`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theDefaultPathIsBuiltFromTheCDerivedSuffix(AngbandDirs.ANGBAND_DIRS)` | — | ✔ |
| `everyDefaultPathEndsInASeparator(AngbandDirs.ANGBAND_DIRS)` | — | ✔ |
| `prefsNameDiffersFromItsEnumConstantName()` | — | ✔ |
| `getDirectoryFindsEveryKnownNameCaseInsensitively(AngbandDirs.ANGBAND_DIRS)` | — | ✔ |
| `getDirectoryReturnsNullForAnUnknownName(String)` | — | ✔ |
| `containsIsTrueForEveryKnownName(AngbandDirs.ANGBAND_DIRS)` | — | ✔ |
| `containsIsFalseForAnUnknownName()` | — | ✔ |
| `restoreSave()` | — | ✘ |
| `setPathRepointsGetPath()` | — | ✔ |
| `setDirectoryWritesThroughTheMatchingConstant()` | — | ✔ |
| `setDirectoryMatchesTheNameCaseInsensitively()` | — | ✔ |


## channel/enums

##### `ChannelEnumsTest.java`

**Responsible for:** Unit tests for the vocabularies the channel messages are written in — the enums that say what a message means , as against the records that say what shape it is.

**class `ChannelEnumsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertNoDuplicates(Enum<?>[])` | — | ✔ |

**class `ChannelEnumsTest.GameEvents`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `thereAreAsManyEventTypesAsInTheCOriginalPlusOnePortOnlyAddition()` | — | ✔ |
| `theListStartsAndEndsWhereCsDoes()` | — | ✔ |
| `theEventsThisMigrationDependsOnExist()` | — | ✔ |
| `noEventTypeIsListedTwice()` | — | ✘ |

**class `ChannelEnumsTest.Projections`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theProjectionListIsCsPlusAZeroPlaceholder()` | — | ✔ |
| `theElementsComeFirstInTheirCOrder()` | — | ✔ |
| `noProjectionIsListedTwice()` | — | ✘ |
| `everyRealElementHasAMatchingProjection()` | — | ✔ |

**class `ChannelEnumsTest.UiEvents`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theUiEventKindsMatchTheCOriginal()` | — | ✔ |

**class `ChannelEnumsTest.Lifecycles`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theLifecycleVocabulariesAreMinimal()` | — | ✔ |

**class `ChannelEnumsTest.EntryFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `thereAreAsManyEntryFlagsAsInTheCOriginal()` | — | ✔ |
| `noEntryFlagIsListedTwice()` | — | ✘ |
| `theOnlySettableFlagResolvesByItsCDataFileName()` | — | ✔ |
| `theInternalOnlyFlagIsDistinctFromTheSettableOne()` | — | ✔ |


## channel/globals

##### `AngbandTest.java`

**Responsible for:** Tests that the version name, version string, build id and build version constants match C's values.

**class `AngbandTest`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `versionNameMatchesCVersionName()` | — | ✘ |
| `versionStringMatchesCFallback()` | — | ✘ |
| `buildIdIsNameAndVersionJoinedBySpace()` | — | ✘ |
| `buildVerIsVersionStringAlone()` | — | ✘ |
| `copyrightStartsWithCCopyrightNotice()` | — | ✘ |
| `copyrightAppendsJavaPortAttribution()` | — | ✘ |
| `copyrightHasNoTrailingContentAfterAttribution()` | — | ✘ |


## channel/messages

##### `ChannelMessageTest.java`

**Responsible for:** Unit tests for the channel protocol: ChannelMessage and its two branches, CoreMessage (what the core sends) and UIMessage (what the UI sends).

**class `ChannelMessageTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `permitted(Class<?>)` | — | ✔ |
| `names(Enum<?>[])` | — | ✔ |
| `channelMessageIsSealedOverExactlyTheTwoSenders()` | — | ✔ |
| `bothBranchesAreThemselvesSealed()` | — | ✔ |
| `everyLeafOfTheProtocolIsARecord()` | — | ✔ |
| `bothSendersFitThroughOneChannelInOrder()` | — | ✔ |
| `theUiChannelAcceptsOnlyUiMessages()` | — | ✔ |

**class `ChannelMessageTest.CoreMessages`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSimpleMessageCarriesItsEventTypeAndNothingElse()` | — | ✘ |
| `simpleMessagesDifferOnlyByEventType()` | — | ✔ |
| `aTextMessageCarriesItsEventTypeAndText()` | — | ✘ |
| `textMessagesAreDistinguishedByEventTypeAsWellAsText()` | — | ✔ |
| `theLifecycleMessageIsComparableByValue()` | — | ✔ |
| `aGameEventMessageCarriesItsEventTypeAndPolymorphicData()` | — | ✘ |
| `gameEventMessagesWithTheSameTypeButDifferentDataAreNotEqual()` | — | ✔ |
| `gameEventMessagesWithTheSameTypeAndEqualDataAreEqual()` | — | ✘ |

**class `ChannelMessageTest.UiMessages`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startAndSaveAndStopAreOneRecordDistinguishedByTheirEvent()` | — | ✔ |
| `eachHalfHasItsOwnLifecycleVocabulary()` | — | ✔ |
| `theWindowCloseRequestIsAnEmptyRecordComparableByValue()` | — | ✔ |
| `aSimpleUiMessageCarriesItsEventTypeAndNothingElse()` | — | ✔ |
| `simpleUiMessagesDifferOnlyByEventType()` | — | ✘ |


## channel/messages/data

##### `EventDataTest.java`

**Responsible for:** Unit tests for the GameEventData payload shapes — the port of the arms of C's game_event_data union (src/game-event.h).

**class `EventDataTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyShapeIsAGameEventData()` | — | ✔ |

**class `EventDataTest.Grids`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aGridIsRowThenColumn()` | — | ✔ |
| `aTransposedGridIsADifferentGrid()` | — | ✔ |
| `gridsAreComparedByValue()` | — | ✘ |
| `aGridIsItselfAPayload()` | — | ✔ |

**class `EventDataTest.Sizes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSizeIsHeightThenWidth()` | — | ✔ |
| `aTransposedSizeIsADifferentSize()` | — | ✘ |

**class `EventDataTest.SimplePayloads`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aStringPayloadCarriesItsText()` | — | ✘ |
| `stringPayloadsAreComparedByValue()` | — | ✘ |
| `aBooleanPayloadCarriesItsFlag()` | — | ✘ |
| `theDefaultBooleanPayloadIsFalse()` | — | ✔ |

**class `EventDataTest.StatPairs`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aStatPairCarriesCurrentThenOther()` | — | ✔ |
| `aTransposedStatPairIsADifferentPayload()` | — | ✘ |
| `anEqualPairIsStoredAsGiven()` | — | ✔ |
| `currentBelowOtherIsStoredAsGiven()` | — | ✔ |
| `currentAtZeroIsStoredUnchanged()` | — | ✔ |
| `statPairsAreComparedByValue()` | — | ✘ |

**class `EventDataTest.Strings`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `stringsCarryTheirTextInOrder()` | — | ✔ |
| `equalContentArraysAreNotEqualInstances()` | — | ✔ |
| `aSingleStringIsAValidPayload()` | — | ✔ |

**class `EventDataTest.Messages`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aMessagePayloadCarriesItsTypeAndText()` | — | ✘ |
| `theSameTextUnderADifferentTypeIsADifferentMessage()` | — | ✔ |

**class `EventDataTest.Bolts`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aBoltCarriesEveryComponentInOrder()` | — | ✔ |
| `swappingOriginAndCurrentIsADifferentBolt()` | — | ✔ |
| `theVisibilityFlagIsPartOfABoltsIdentity()` | — | ✔ |

**class `EventDataTest.Tunnels`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aTunnelCarriesItsCountsInOrder()` | — | ✔ |
| `aTunnelThatStoppedShortIsDistinguishableFromOneThatArrived()` | — | ✔ |

**class `EventDataTest.BirthPoints`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `birthPointsCarriesItsMapsAndRemainingInOrder()` | — | ✔ |
| `swappingPointsAndIncPointsIsADifferentPayload()` | — | ✔ |
| `gettersReturnTheSameMapInstancesGivenToTheConstructor()` | — | ✔ |
| `remainingAtTheFullBirthPointBudgetIsStoredUnchanged()` | — | ✔ |
| `remainingAtZeroIsStoredUnchanged()` | — | ✔ |

**class `EventDataTest.Explosions`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anExplosionCarriesEveryComponentInOrder()` | — | ✔ |
| `swappingProjTypeAndNumGridsIsADifferentPayload()` | — | ✔ |
| `gettersReturnTheSameListInstancesGivenToTheConstructor()` | — | ✔ |
| `theCentreGridIsStoredAtDistanceZero()` | — | ✔ |
| `explosionWith(ArrayList<Loc>, Loc)` | — | ✔ |
| `aNullBlastGridListIsRejectedByName()` | — | ✔ |
| `aNullCentreIsRejectedByName()` | — | ✔ |
| `aNullBlastGridElementIsRejectedWithItsIndex()` | — | ✔ |
| `theOriginIsAnAcceptedGridAndCentre()` | — | ✔ |


##### `PlayerEventStatusUpdateDefaultTest.java`

**Responsible for:** Tests the static initializer that seeds PlayerEventStatusUpdate's cachedPlayerStatusView and cachedPlayerCharSheetView fields at class load.

**class `PlayerEventStatusUpdateDefaultTest`** — class Javadoc: ✔ · type stamp: on 260912

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshlySeededPlayerStatusView()` | — | ✔ |
| `freshlySeededPlayerCharSheetView()` | — | ✔ |
| `staticInitializerSeedsAllDefaultSnapshot()` | — | ✔ |
| `staticInitializerSeedsAllDefaultCharSheetSnapshot()` | — | ✔ |


##### `PlayerEventStatusUpdateTest.java`

**Responsible for:** Tests PlayerEventStatusUpdate#getPlayerStatusView()/PlayerEventStatusUpdate#getPlayerCharSheetView(), PlayerEventStatusUpdate#updatePlayerStatusView/PlayerEventStatusUpdate#updatePlayerCharSheetView and the static updatePlayerStatus*/updatePlayerCharSheet* field setters that follow them.

**class `PlayerEventStatusUpdateTest`** — class Javadoc: ✔ · type stamp: on 260912

- Fields (3/3 documented): ✔ `savedPlayerStatusView`, `savedPlayerCharSheetView`, `CHAR_SHEET_PLACEHOLDER_RESET_FIELDS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseline()` | — | ✔ |
| `charSheetBaseline()` | — | ✔ |
| `invokeSetter(String, Class<?>, Object)` | — | ✔ |
| `assertOnlyFieldChanged(Record, Record, String, Object)` | — | ✔ |
| `assertOnlyFieldChanged(Record, Record, String, Object, Set<String>)` | — | ✔ |
| `fieldUpdaters()` | — | ✔ |
| `charSheetFieldUpdaters()` | — | ✔ |
| `saveSnapshots()` | — | ✔ |
| `restoreSnapshots()` | — | ✔ |
| `updatingOneFieldLeavesEveryOtherFieldUntouched(String, Class<?>, Object, String)` | — | ✔ |
| `updatingOneCharSheetFieldLeavesEveryOtherFieldUntouched(String, Class<?>, Object, String)` | — | ✔ |
| `negativeCurrentHpIsNotClamped()` | — | ✔ |
| `wholeViewSetIsReadableBackAsSameInstance()` | — | ✔ |
| `wholeCharSheetViewSetIsReadableBackAsSameInstance()` | — | ✔ |
| `secondWholeViewSetReplacesFirst()` | — | ✔ |
| `secondWholeCharSheetViewSetReplacesFirst()` | — | ✔ |


## channel/parser

##### `GrammarDriverTest.java`

**Responsible for:** Unit tests for GrammarDriver.

**class `GrammarDriverTest`** — class Javadoc: ✔ · type stamp: on 260915

- Fields (0/1 documented): ✘ `logger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchingCountAddsNoError()` | — | ✘ |
| `mismatchedCountAddsOneError()` | — | ✘ |
| `nonNumericDeclaredAddsExactlyOneFormatError()` | — | ✘ |
| `declaredNegativeOneIsTreatedAsARealMismatchNotASentinel()` | — | ✘ |
| `declaredNegativeOneMatchingActualAddsNoError()` | — | ✘ |
| `runPropagatesIOExceptionForAMissingFile()` | — | ✘ |


##### `ParseResultTest.java`

**Responsible for:** Direct unit tests for ParseResult, the generic result holder every list-shaped lib/gamedata reader wraps its output in.

**class `ParseResultTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resultWithItemsAndNoErrorsReportsNoErrors()` | — | ✘ |
| `resultWithErrorsReportsErrorsRegardlessOfItems()` | — | ✘ |
| `emptyItemsWithNoErrorsReportsNoErrors()` | — | ✘ |
| `emptyItemsWithErrorsStillReportsErrors()` | — | ✘ |


## channel/strings

##### `AngbandDisplayCharacterTest.java`

**Responsible for:** Unit tests for AngbandDisplayCharacter, the glyph-plus-colour pair the terminal draws - the port of the C source's attr/char couple.

**class `AngbandDisplayCharacterTest`** — class Javadoc: ✔ · type stamp: —

**class `AngbandDisplayCharacterTest.Construction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theColourEnumConstructorStoresBothHalves()` | — | ✘ |
| `theCharConstructorResolvesTheColourCode()` | — | ✘ |
| `theStringConstructorTakesASingleCharacterAsACode()` | — | ✘ |
| `theStringConstructorTakesALongerStringAsADisplayName()` | — | ✘ |
| `anUnrecognisedColourLeavesTheGlyphWithNoColourAtAll()` | — | ✘ |
| `theGlyphItselfIsStoredVerbatimIncludingUnusualCharacters()` | — | ✘ |

**class `AngbandDisplayCharacterTest.Equality`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `glyphsWithTheSameCharacterAndColourAreEqual()` | — | ✘ |
| `theSameCharacterInADifferentColourIsADifferentGlyph()` | — | ✘ |
| `aDifferentCharacterInTheSameColourIsADifferentGlyph()` | — | ✘ |
| `theTwoConstructorRoutesProduceEqualGlyphs()` | — | ✘ |
| `nullAndForeignTypesAreNotEqual()` | — | ✘ |
| `equalGlyphsHashAlike()` | — | ✘ |
| `glyphsWorkAsSetMembers()` | — | ✘ |
| `anUnrecognisedColourHashesAndComparesConsistently()` | — | ✘ |
| `everyColourOfOneGlyphIsADistinctValue()` | — | ✘ |


## channel/uichannel

##### `UISenderTest.java`

**Responsible for:** Unit tests for UISender in isolation, constructed directly against a raw queue rather than through uk.co.jackoftradesltd.channel.Channels.

**class `UISenderTest`** — class Javadoc: ✔ · type stamp: on 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sendPutsTheMessageOnTheQueue()` | on 260915 | ✔ |
| `repeatedSendsPreserveOrder()` | on 260915 | ✔ |
| `sendingNeverBlocksEvenUnread()` | on 260915 | ✔ |
| `sendWritesToTheConstructorSuppliedQueue()` | on 260915 | ✔ |


## channel/utils

##### `FlagTest.java`

**Responsible for:** Unit tests for Flag, the port of the C source's flag-array machinery (src/z-bitflag.c) onto an java.util.EnumSet.

**class `FlagTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `flags`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsOf(TestFlag)` | — | ✔ |
| `contentsOf(Flag<TestFlag>)` | — | ✔ |
| `setUp()` | — | ✘ |

**enum `FlagTest.TestFlag`** — class Javadoc: ✔ · type stamp: —

- Enum constants (4/4 documented): ✔ `ALPHA`, `BETA`, `GAMMA`, `DELTA`

**class `FlagTest.Population`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aFreshFlagSetIsEmpty()` | — | ✘ |
| `countTracksTheNumberOfFlagsSwitchedOn()` | — | ✘ |
| `aPartiallyPopulatedSetIsNeitherEmptyNorFull()` | — | ✘ |
| `setAllFillsTheSetAndWipeEmptiesIt()` | — | ✘ |

**class `FlagTest.SingleFlagMutation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `switchingAFlagOnReportsTheChangeOnlyTheFirstTime()` | — | ✘ |
| `switchingAFlagOffReportsTheChangeOnlyWhenItWasSet()` | — | ✘ |
| `hasIsFalseForFlagsThatWereNeverSet()` | — | ✘ |
| `negateSwapsEveryFlagForItsOpposite()` | — | ✘ |
| `negatingTwiceRestoresTheOriginal()` | — | ✘ |
| `negatingAnEmptySetFillsIt()` | — | ✘ |

**class `FlagTest.BulkMutation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setSwitchesSeveralFlagsOnAndReportsWhetherAnythingChanged()` | — | ✘ |
| `setReportsAChangeWhenEvenOneFlagIsNew()` | — | ✘ |
| `theListOverloadOfSetBehavesLikeTheVarargsOne()` | — | ✘ |
| `clearSwitchesSeveralFlagsOffAndReportsWhetherAnythingChanged()` | — | ✘ |
| `theListOverloadOfClearBehavesLikeTheVarargsOne()` | — | ✘ |
| `initReplacesTheContentsRatherThanAddingToThem()` | — | ✘ |
| `theListOverloadOfInitAlsoReplaces()` | — | ✘ |
| `initWithNothingEmptiesTheSet()` | — | ✘ |

**class `FlagTest.SetOperations`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unionAddsTheOtherSetsFlagsAndReportsWhetherAnythingChanged()` | — | ✘ |
| `unionLeavesTheOtherSetAlone()` | — | ✘ |
| `interKeepsOnlyTheFlagsPresentInBoth()` | — | ✘ |
| `interWithASupersetChangesNothing()` | — | ✘ |
| `interWithADisjointSetEmptiesTheReceiver()` | — | ✘ |
| `diffRemovesTheOtherSetsFlagsAndReportsWhetherAnythingChanged()` | — | ✘ |
| `maskKeepsOnlyTheListedFlags()` | — | ✘ |
| `theListOverloadOfMaskBehavesLikeTheVarargsOne()` | — | ✘ |
| `copyFromReplacesTheContentsWithTheOtherSets()` | — | ✘ |
| `copyFromProducesAnIndependentCopy()` | — | ✘ |

**class `FlagTest.ReadOnlyArguments`** — class Javadoc: ✔ · type stamp: on 260818

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unionAcceptsAViewTypedArgument()` | — | ✘ |
| `interAcceptsAViewTypedArgument()` | — | ✘ |
| `diffAcceptsAViewTypedArgument()` | — | ✘ |
| `copyFromAcceptsAViewTypedArgument()` | — | ✘ |
| `noneOfThemWritesToTheArgument()` | — | ✘ |
| `interReportsFalseWhenTheIntersectionClearsNothing()` | — | ✘ |
| `interWithItselfIsASafeNoOp()` | — | ✘ |
| `interWithAnEmptyViewEmptiesTheReceiverAndSaysSo()` | — | ✘ |
| `copyFromReplacesRatherThanMerging()` | — | ✘ |
| `copyFromItselfLeavesTheSetUnchanged()` | — | ✘ |
| `copyFromAnEmptySetEmptiesTheReceiver()` | — | ✘ |
| `copyFromAFullSetFillsTheReceiver()` | — | ✘ |
| `unionWithItselfChangesNothingAndSaysSo()` | — | ✘ |
| `diffWithItselfEmptiesTheReceiverAndSaysSo()` | — | ✘ |
| `diffOfAnEmptySetWithItselfReportsNoChange()` | — | ✘ |

**class `FlagTest.AndNot`** — class Javadoc: ✔ · type stamp: on 261007

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSetHoldingOnlyTheNamedFlagGivesFalse()` | — | ✘ |
| `aSetHoldingTheNamedFlagAndAnotherGivesTrue()` | — | ✘ |
| `aSetHoldingOnlyOtherFlagsGivesTrue()` | — | ✘ |
| `anEmptySetGivesFalse()` | — | ✘ |
| `theReceiverIsLeftUnchanged()` | — | ✘ |

**class `FlagTest.Predicates`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `isInterIsTrueWhenTheSetsShareAtLeastOneFlag()` | — | ✘ |
| `isInterIsFalseAgainstAnEmptySet()` | — | ✘ |
| `isSubsetAsksWhetherTheArgumentIsContainedInTheReceiver()` | — | ✘ |
| `everySetContainsTheEmptySet()` | — | ✘ |
| `isEqualIsContainmentInBothDirections()` | — | ✘ |
| `twoEmptySetsAreEqual()` | — | ✘ |
| `testIsAnAnyOfCheck()` | — | ✘ |
| `testOfNothingIsFalse()` | — | ✘ |
| `testAllIsAnAllOfCheck()` | — | ✘ |
| `testAllOfNothingIsVacuouslyTrue()` | — | ✘ |
| `theListOverloadOfTestBehavesLikeTheVarargsOne()` | — | ✘ |

**class `FlagTest.DebugSetterAndListView`** — class Javadoc: ✔ · type stamp: on 260904

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onDbgSetsAFlagAndReportsTheChangeOnlyTheFirstTime()` | — | ✘ |
| `onDbgAndOnLeaveTheSetInTheSameState()` | — | ✘ |
| `toListReturnsTheSetFlagsInEnumDeclarationOrder()` | — | ✘ |
| `toListOnAnEmptySetReturnsAnEmptyList()` | — | ✘ |
| `toListIsAnIndependentCopy()` | — | ✘ |

**class `FlagTest.Constructors`** — class Javadoc: ✔ · type stamp: on 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theSingleFlagConstructorStartsWithExactlyThatFlagSet()` | — | ✘ |
| `theVarargsConstructorStartsWithExactlyTheGivenFlags()` | — | ✘ |
| `theVarargsConstructorRejectsAnEmptyArgumentList()` | — | ✘ |

**class `FlagTest.DeprecatedNext`** — class Javadoc: ✔ · type stamp: on 260915

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nextReturnsTheFollowingSetFlag()` | — | ✘ |
| `nextReturnsTheLastEnumConstantWhenThereIsNoFlagAfterCurrent()` | — | ✘ |
| `nextReturnsTheLastEnumConstantWhenCurrentFlagIsNotSet()` | — | ✘ |

**class `FlagTest.Iteration`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `iterationYieldsTheSetFlagsInEnumDeclarationOrder()` | — | ✘ |
| `anEmptySetYieldsNothing()` | — | ✘ |
| `theIteratorDoesNotExposeTheUnderlyingSetForModification()` | — | ✘ |


##### `FlagViewTest.java`

**Responsible for:** Tests FlagView, the read-only face of a flag set, added on 260818.

**class `FlagViewTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (1/1 documented): ✔ `PERMITTED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsOf(TestFlag)` | — | ✔ |
| `theInterfaceExposesNoMutators()` | — | ✔ |
| `flagIsTheOnlyThingThatNeedsToImplementIt()` | — | ✘ |
| `aViewIsIterableSoAnEnhancedForLoopWorks()` | — | ✘ |
| `theQueriesAreAllReachableThroughTheInterface()` | — | ✘ |
| `theComparisonsTakeAndAcceptAnotherView()` | — | ✘ |
| `anEmptyViewReportsItself()` | — | ✘ |
| `aFullViewReportsItself()` | — | ✘ |
| `aViewTracksTheSetBehindItRatherThanFreezingIt()` | — | ✔ |

**enum `FlagViewTest.TestFlag`** — class Javadoc: ✔ · type stamp: —

- Enum constants (0/3 documented): ✘ `ALPHA`, `BETA`, `GAMMA`


##### `UIEntryCombinerStateTest.java`

**Responsible for:** Unit tests for UIEntryCombinerState, the port of the C original's struct ui_entry_combiner_state (ui-entry-combiner.h).

**class `UIEntryCombinerStateTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshInstanceStartsAllChannelsAtZero()` | — | ✘ |
| `accumRoundTrips()` | — | ✘ |
| `accumAuxRoundTrips()` | — | ✘ |
| `negAccumRoundTrips()` | — | ✘ |
| `negAccumAuxRoundTrips()` | — | ✘ |
| `allFourChannelsAreIndependent()` | — | ✘ |


## channel/utils/combiners

##### `CombinerTest.java`

**Responsible for:** Unit tests for the Combiner family, the port of the C source's character-sheet value combiners (src/ui-entry-combiner.c).

**class `CombinerTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `NOT_PRESENT`, `UNKNOWN`, `RES_VUL`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fold(Combiner, List<Integer>, List<Integer>)` | — | ✔ |
| `assertBothPathsGive(Combiner, List<Integer>, List<Integer>, int, int)` | — | ✔ |
| `allCombiners()` | — | ✔ |
| `theSharedContractCoversEveryStrategyInThePackage()` | — | ✘ |

**class `CombinerTest.SharedContract`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theThreeSentinelsAreDistinctAndSitAtTheTopOfTheRange()` | — | ✘ |
| `vecRejectsAListShorterThanTheRequestedCount(Combiner)` | — | ✘ |
| `vecOfNothingIsNotPresent(Combiner)` | — | ✘ |
| `vecOfASingleValueIsThatValue(Combiner)` | — | ✘ |
| `cloningAnUninitialisedCombinerGivesAFreshOneOfTheSameKind(Combiner)` | — | ✘ |
| `aCloneCarriesTheStateButAccumulatesIndependently(Combiner)` | — | ✘ |
| `finishReturnsADetachedStateRatherThanTheLiveOne(Combiner)` | — | ✘ |
| `anAbsentContributionIsSkippedEntirely(Combiner)` | — | ✘ |

**class `CombinerTest.Adding`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `valuesAndAuxiliariesAreSummedSeparately()` | — | ✘ |
| `negativeContributionsSubtract()` | — | ✘ |
| `anUnknownContributionOccupiesAnEmptyAccumulatorButNotAFullOne()` | — | ✘ |
| `aRealValueDisplacesAnUnknownAccumulator()` | — | ✘ |
| `additionSaturatesJustBelowTheSentinelRange()` | — | ✘ |
| `additionSaturatesAtTheBottomOfTheRangeToo()` | — | ✘ |

**class `CombinerTest.BitwiseOr`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bitsFromEveryContributionAreUnioned()` | — | ✘ |
| `orIsIdempotent()` | — | ✘ |
| `aRealValueDisplacesAnUnknownAccumulatorRatherThanBeingOredIntoIt()` | — | ✘ |

**class `CombinerTest.Positional`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstKeepsTheOpeningContributionAndIgnoresTheRest()` | — | ✘ |
| `lastKeepsTheClosingContribution()` | — | ✘ |
| `positionalPicksDoNotFilterSentinels()` | — | ✘ |

**class `CombinerTest.Extremes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `largestPicksTheMaximumOfEachChannel()` | — | ✘ |
| `smallestPicksTheMinimumOfEachChannel()` | — | ✘ |
| `extremesHandleNegativesByValueNotMagnitude()` | — | ✘ |
| `theUnknownSentinelIsNotTreatedAsAHugeNumber()` | — | ✘ |
| `theNotPresentSentinelIsNotTreatedAsAHugeNumberEither()` | — | ✘ |
| `anUnknownContributionFillsAnAbsentAccumulator()` | — | ✘ |
| `aRealValueReplacesAnUnknownAccumulator()` | — | ✘ |
| `aRealValueReplacesAnAbsentAccumulator()` | — | ✘ |

**class `CombinerTest.LogicalOr`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anyNonZeroContributionNormalisesTheResultToOne()` | — | ✘ |
| `allZeroContributionsStayZero()` | — | ✘ |
| `aNegativeContributionCountsAsPresent()` | — | ✘ |
| `theValueAndAuxiliaryChannelsAreIndependent()` | — | ✘ |
| `anUnknownContributionAloneStaysUnknownRatherThanBeingTreatedAsTrue()` | — | ✘ |
| `anUnknownContributionFillsAnAbsentAccumulator()` | — | ✘ |
| `aRealValueReplacesAnUnknownAccumulator()` | — | ✘ |
| `anUnknownContributionDoesNotDisturbAnAlreadySetAccumulator()` | — | ✘ |

**class `CombinerTest.LogicalOrWithCancel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `positiveContributionsAloneGiveOne()` | — | ✘ |
| `aNegativeContributionCancelsAPositiveOne()` | — | ✘ |
| `orderOfContributionsDoesNotMatter()` | — | ✘ |
| `negativeContributionsAloneAlsoGiveMinusOne()` | — | ✘ |
| `allZeroContributionsStayZero()` | — | ✘ |
| `theIntermediateBitPairNeverEscapesThroughFinish()` | — | ✘ |

**class `CombinerTest.Resist0`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resistanceAloneIsReportedAsItsLevel()` | — | ✘ |
| `theStrongestResistanceWins()` | — | ✘ |
| `vulnerabilityAloneIsReportedAsMinusOne()` | — | ✘ |
| `resistanceAndVulnerabilityTogetherCollapseToTheResVulSentinel()` | — | ✘ |
| `theResVulSentinelSurvivesBeingFedBackIn()` | — | ✘ |
| `immunityOutranksVulnerabilityInsteadOfCollapsing()` | — | ✘ |
| `finishClearsTheNegativeAccumulatorItUsedAsScratchSpace()` | — | ✘ |

**class `CombinerTest.State`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aFreshStateIsAllZeroes()` | — | ✘ |
| `eachChannelIsStoredAndReadBackIndependently()` | — | ✘ |


## frontend

##### `JPanelAreaDisplaySizeTest.java`

**Responsible for:** SwingUI.JPanelArea#getDisplayWidth and SwingUI.JPanelArea#getDisplayHeight, checked against C's Term_get_size ([C] src/ui-term.c), which reports the same two dimensions through out-parameters and falls back to 80x24 when its global Term pointer is unset - the same values these getters always retu...

**class `JPanelAreaDisplaySizeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `area`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAnArea()` | — | ✘ |
| `widthIsEightyColumns()` | — | ✔ |
| `heightIsTwentyFourRows()` | — | ✔ |
| `reflectsTheCurrentGridAfterSetChars()` | — | ✔ |


##### `JPanelAreaPutTest.java`

**Responsible for:** SwingUI.JPanelArea#put(int, int, char, ColourEnum) and SwingUI.JPanelArea#put(int, int, String, ColourEnum), checked against C's Term_putch and Term_putstr ([C] src/ui-term.c), by way of the Term_gotoxy/Term_addch/Term_addstr helpers both are built from.

**class `JPanelAreaPutTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `area`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `display(SwingUI.JPanelArea)` | — | ✘ |
| `assertBlank(AngbandDisplayCharacter[][], int, int)` | — | ✘ |
| `buildAnArea()` | — | ✘ |
| `putCharWritesTheCell()` | — | ✔ |
| `putCharDeclinesNegativeRow()` | — | ✔ |
| `putCharDeclinesRowAtHeight()` | — | ✔ |
| `putCharDeclinesNegativeColumn()` | — | ✔ |
| `putCharDeclinesColumnAtWidth()` | — | ✔ |
| `putStringWritesEachCell()` | — | ✔ |
| `putStringClipsAtTheRightEdge()` | — | ✔ |
| `putStringDeclinesNegativeColumn()` | — | ✔ |
| `putStringDeclinesColumnAtWidth()` | — | ✔ |


##### `SwingUIActiveTermDataTest.java`

**Responsible for:** SwingUI#terms, SwingUI#activeTermData and SwingUI#getActiveTermData() as the constructor leaves them: one TermData built, held in both terms and activeTermData at once - the state before anything plays the part of C's Term_activate ([C] src/ui-term.c).

**class `SwingUIActiveTermDataTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `terms(SwingUI)` | — | ✘ |
| `requireADisplay()` | — | ✘ |
| `getActiveTermDataIsNotNull()` | — | ✔ |
| `getActiveTermDataOwnsTheActiveWindow()` | — | ✔ |
| `termsHoldsExactlyTheActiveTermData()` | — | ✔ |


##### `SwingUITest.java`

**Responsible for:** What is left of SwingUI's shutdown once stage 3 has taken the rest away.

**class `SwingUITest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `realisedFrontEnd()` | — | ✔ |
| `requireADisplay()` | — | ✘ |
| `closeDownDisposesTheWindow()` | — | ✔ |
| `closeDownDoesNotExitTheProcess()` | — | ✔ |
| `closeDownIsSafeToRepeat()` | — | ✔ |


## frontend/colour

##### `ColourCycleTest.java`

**Responsible for:** Unit tests for ColourCycle, the port of C's visuals_color_cycle struct plus visuals_color_cycle_new and the array-indexing step of visuals_color_cycle_attr_for_frame (src/ui-visuals.c).

**class `ColourCycleTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `colourCycleRefusesToBuildWithNoSteps()` | — | ✘ |
| `colourCycleBuildsFromExactlyTheStepsGiven()` | — | ✘ |
| `singleStepCycleHasSizeOne()` | — | ✘ |
| `getColourThrowsPastTheLastStepInsteadOfReturningASentinel()` | — | ✘ |
| `mutatingTheOriginalListAfterConstructionDoesNotAffectTheCycle()` | — | ✘ |
| `colourCycleReturnsANewInstanceEachCall()` | — | ✘ |


##### `ColourTest.java`

**Responsible for:** Unit tests for Colour, the port of the C original's angband_color_table (src/z-color.c).

**class `ColourTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `colourRgbTriples()` | — | ✔ |
| `everyBasicColourMatchesItsCRow(ColourEnum, int, int, int)` | — | ✘ |
| `theFirstRowIsBlack()` | — | ✘ |
| `theLastNamedRowIsShade()` | — | ✘ |
| `reinitialisingLeavesEveryColourUnchanged()` | — | ✘ |


##### `MonsterRaceCyclerTest.java`

**Responsible for:** Unit tests for MonsterRaceCycler, the port of C's anonymous visuals_color_cycles_by_race table and its visuals_cycler_set_cycle_for_race / visuals_cycler_get_attr_for_race accessors (src/ui-visuals.c).

**class `MonsterRaceCyclerTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `frameWrapsAroundTheCycleLikeCsFrameModMaxSteps()` | — | ✘ |
| `singleStepCycleAlwaysReturnsThatStep()` | — | ✘ |
| `unregisteredRaceReturnsNullLikeCsBasicColorsSentinel()` | — | ✘ |
| `nullRaceIsIgnoredLikeCsNullGuard()` | — | ✘ |
| `nullCycleIsIgnoredLikeCsUnresolvedCycleGuard()` | — | ✘ |
| `reRegisteringARaceReplacesItsCycle()` | — | ✘ |
| `getCyclerReturnsTheRegisteredCycleUnchanged()` | — | ✘ |


## frontend/entries

##### `UIEntryBaseTest.java`

**Responsible for:** Unit tests for UIEntryBase, checked against the C original's parse_entry_desc (src/ui-entry.c).

**class `UIEntryBaseTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `base(Flag<ChannelEntryFlag>, String)` | — | ✘ |
| `flagsOf(ChannelEntryFlag)` | — | ✘ |
| `constructorRejectsANullDescription()` | — | ✘ |
| `constructorAcceptsANonNullDescriptionAndDoesNotExposeIt()` | — | ✘ |
| `aSingleFlagPassedInIsReturnedByGetFlags()` | — | ✘ |
| `multipleFlagsPassedInAreAllReturnedByGetFlags()` | — | ✘ |
| `anEmptyFlagSetPassedInIsReturnedEmpty()` | — | ✘ |
| `gettersReturnTheConstructorArguments()` | — | ✘ |


##### `UIEntryIteratorTest.java`

**Responsible for:** Unit tests for UIEntryIterator, checked against C's struct ui_entry_iterator and its free functions (src/ui-entry.c:41-44, 461-524).

**class `UIEntryIteratorTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `entry(String)` | — | ✘ |
| `entries(String)` | — | ✘ |
| `advanceReturnsEntriesInOrderStartingAtIndexZero()` | — | ✘ |
| `advanceIncrementsIndexAfterReading()` | — | ✘ |
| `advancePastTheEndReturnsNullRatherThanThrowing()` | — | ✘ |
| `advanceOnAnEmptyIteratorReturnsNullImmediately()` | — | ✘ |
| `getNumAtConstructionEqualsTheEntryCount()` | — | ✘ |
| `getNumFallsByOneWithEachAdvanceAndReachesZeroWhenExhausted()` | — | ✘ |
| `getNumOnAnEmptyIteratorIsZero()` | — | ✘ |
| `threeArgConstructorIgnoresTheSuppliedNumInFavourOfEntriesSize()` | — | ✘ |
| `threeArgConstructorHonoursAnArbitraryStartingIndex()` | — | ✘ |
| `noArgConstructorStartsEmptyAndAddEntryAppendsInCallOrder()` | — | ✘ |
| `getEntriesReturnsTheBackingListInEnumerationOrder()` | — | ✘ |


##### `UIEntryRendererTest.java`

**Responsible for:** Unit tests for UIEntryRenderer, checked against the finished, post-default struct renderer_info values C's finish_parse_ui_entry_renderer (src/ui-entry-renderers.c) produces and the UI_ENTRY_RENDERER table entries in src/list-ui-entry-renderers.h.

**class `UIEntryRendererTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `gettersReturnTheConstructorArgumentsForANumericRenderer()` | — | ✘ |
| `gettersReturnTheConstructorArgumentsForAZeroDigitRenderer()` | — | ✘ |
| `toStringIncludesTheNameAndDoesNotThrowWithNullFields()` | — | ✘ |


##### `UIEntryTest.java`

**Responsible for:** Unit tests for UIEntry, checked against the C original's UI-entry system (src/ui-entry.c).

**class `UIEntryTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `entry(List<UIEntryCategory>, Flag<ChannelEntryFlag>)` | — | ✘ |
| `constructorRejectsANullDescription()` | — | ✘ |
| `constructorAcceptsANonNullDescriptionAndDoesNotExposeIt()` | — | ✘ |
| `findsAKnownCategoryAtItsIndex()` | — | ✘ |
| `reportsAbsentForAnUnknownCategory()` | — | ✘ |
| `reportsAbsentWhenThereAreNoCategoriesAtAll()` | — | ✘ |
| `entryFlagHasReportsASetFlag()` | — | ✘ |
| `entryFlagHasReportsFalseWhenNoFlagsAreSet()` | — | ✘ |
| `fromValueResolvesTheStatAndElementParameterNames()` | — | ✘ |
| `fromValueResolvesTheEmptyStringToNone()` | — | ✘ |
| `fromValueReturnsNullForAnUnrecognisedName()` | — | ✘ |


## frontend/entries/enums

##### `UIEntryRendererEnumTest.java`

**Responsible for:** Unit tests for UIEntryRendererEnum, checked against the UI_ENTRY_RENDERER table entries in C's src/list-ui-entry-renderers.h, not against the Java source, so a transcription error in the enum's packed colour/symbol strings would still be visible here.

**class `UIEntryRendererEnumTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `compactResistRendererMatchesTheCTableRow()` | — | ✘ |
| `compactFlagRendererMatchesTheCTableRow()` | — | ✘ |
| `compactFlagWithCancelRendererMatchesTheCTableRow()` | — | ✘ |
| `numericAsSignRendererMatchesTheCTableRow()` | — | ✘ |
| `numericRendererMatchesTheCTableRow()` | — | ✘ |
| `numericRendererWithBoolAuxMatchesTheCTableRow()` | — | ✘ |
| `noneSentinelCarriesEmptyTables()` | — | ✘ |
| `allSevenConstantsExist()` | — | ✘ |


## frontend/events

##### `BirthEventsTest.java`

**Responsible for:** BirthEvents: character creation's two ends, while they are still stubs.

**class `BirthEventsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `log`, `birthEvents`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `enteringAnnouncesEnterBirth()` | — | ✔ |
| `leavingAnnouncesLeaveBirth()` | — | ✔ |
| `theTwoEndsAreDistinct()` | — | ✔ |


##### `MainEventsTest.java`

**Responsible for:** MainEvents: the front end's five phase transitions, while they are still stubs.

**class `MainEventsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/4 documented): ✔ `ANNOUNCERS`, `COVERED` — ✘ `log`, `mainEvents`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `eachTransitionAnnouncesItsOwnEvent()` | — | ✔ |
| `theFiveTransitionsAreDistinct()` | — | ✔ |
| `thePhaseEventsAreTheOnesCovered(GameEventType)` | — | ✔ |
| `theOtherStartUpEventsBelongElsewhere(GameEventType)` | — | ✔ |

**record `MainEventsTest.Announcer`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `event`, `call`


##### `PhaseEventRoutingTest.java`

**Responsible for:** Stage 5's split, end to end on the UI side: a phase event put on the core channel reaches the receiver that stands for it, having crossed a real queue on a real thread.

**class `PhaseEventRoutingTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/6 documented): ✔ `SHOULD_ARRIVE_MILLIS`, `uiFailure` — ✘ `channels`, `uiThread`, `mainLog`, `birthLog`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✔ |
| `coreReports(GameEventType)` | — | ✔ |
| `aPhaseEventReachesItsReceiver(GameEventType)` | — | ✔ |
| `aBirthEventReachesTheBirthReceiver(GameEventType)` | — | ✔ |
| `aPhaseEventLeavesTheLoopRunning()` | — | ✔ |
| `aWholeSessionOfTransitionsArrivesInOrder()` | — | ✔ |


##### `RecordingLog.java`

**Responsible for:** A log4j appender that keeps what was logged instead of writing it anywhere, attached to one class's logger for the length of one test.

**class `RecordingLog`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `SERIAL`, `lines`, `target`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingLog(Logger)` | — | ✘ |
| `attachedTo(Class<?>)` | — | ✔ |
| `detach()` | — | ✔ |
| `append(LogEvent)` | — | ✘ (override) |
| `lines()` | — | ✔ |
| `clear()` | — | ✔ |
| `await(String, long)` | — | ✔ |
| `startsWithPrefix(String)` | — | ✔ |


##### `UIEntryCategoryTest.java`

**Responsible for:** Unit tests for UIEntryCategory, checked against C's struct category_reference ([C] ui-entry.c:55-59: name, priority, priority_set) and the way finish_ui_entry ([C] ui-entry.c:1660-1680) fills those fields - the priority_set branch copies the resolved priority across and sets the flag true, the un...

**class `UIEntryCategoryTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constructorStoresAnExplicitlySetPriorityVerbatim()` | — | ✘ |
| `constructorStoresAnUnsetPriorityAsZeroWithTheFlagFalse()` | — | ✘ |
| `constructorAcceptsANegativePriorityFromTheNegativeIndexScheme()` | — | ✘ |
| `anExplicitZeroPriorityIsStillReportedAsSet()` | — | ✘ |


## frontend/globals

##### `UIGlobalsTest.java`

**Responsible for:** Tests UIGlobals' textOutIndent holder against the C original's int text_out_indent = 0; ([C] src/z-textblock.c:382), a plain, unbounded global that call sites read and write directly with no validation.

**class `UIGlobalsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resetIndent()` | — | ✔ |
| `defaultsToZero()` | — | ✔ |
| `setThenGetRoundTrips()` | — | ✔ |
| `repeatedSetsOverwrite()` | — | ✔ |
| `resetsToZero()` | — | ✔ |
| `negativeValueRoundTrips()` | — | ✔ |


## frontend/inputfromuser

##### `UILoopTest.java`

**Responsible for:** Stage 3's UI half: UILoop as the thread that turns window events into requests the core understands, and the core's reply into a closed window.

**class `UILoopTest`** — class Javadoc: ✔ · type stamp: before 260916

- Fields (5/5 documented): ✔ `NOT_COMING_MILLIS`, `SHOULD_FINISH_MILLIS`, `uiFailure`, `channels`, `uiThread`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `requireADisplay()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✔ |
| `startLoop(SwingUI)` | — | ✔ |
| `clickTheCloseButton()` | — | ✔ |
| `coreReportsStopped()` | — | ✔ |

**class `UILoopTest.RecordingSwingUI`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `closed`, `closedOnTheEdt`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingSwingUI()` | — | ✘ |
| `closeDown()` | — | ✘ (override) |
| `awaitClose()` | — | ✔ |

**class `UILoopTest.TranslatingWindowEvents`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aCloseRequestBecomesSaveAndStop()` | — | ✔ |
| `theRequestDoesNotComeBackRound()` | — | ✔ |
| `theLoopKeepsRunningAfterTheRequest()` | — | ✔ |

**class `UILoopTest.EndingOnStopped`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `stoppedDisposesTheWindowsAndEndsTheLoop()` | — | ✔ |
| `theDisposalHappensOnTheEdt()` | — | ✔ |
| `theFullHandshakeFromClickToClosedWindow()` | — | ✔ |
| `stoppedIsObeyedEvenWithoutACloseRequest()` | — | ✔ |

**class `UILoopTest.FailureAndOtherTraffic`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinaryCoreTrafficLeavesTheLoopRunning()` | — | ✔ |
| `aMisroutedUiMessageIsRejected()` | — | ✔ |
| `anInterruptEndsTheLoopWithoutDisposingAnything()` | — | ✔ |

**class `UILoopTest.RoutingGameEvents`** — class Javadoc: ✔ · type stamp: —

- Fields (1/4 documented): ✔ `savedCurrentHp` — ✘ `savedMaxHp`, `savedCurrentSp`, `savedMaxSp`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveSidebarModel()` | — | ✘ |
| `restoreSidebarModel()` | — | ✘ |
| `anEventHpMessageWritesIntoTheSidebarModel()` | — | ✔ |
| `anEventManaMessageWritesIntoTheSidebarModel()` | — | ✔ |

**class `UILoopTest.ReportingABrokenLibDirectory`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `savedScreensPath`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveScreensPath()` | — | ✘ |
| `restoreScreensPath()` | — | ✘ |
| `reportBrokenLib(String)` | — | ✔ |
| `theBlankLineFollowsWhyNotOneOfTheFixedLines()` | — | ✔ |
| `aMissingNewsFileIsReportedInCsWording()` | — | ✔ |
| `firstLineOf(String)` | — | ✔ |


## frontend/screen

##### `TermAddstrTest.java`

**Responsible for:** Term#putstr and Term#addstr, checked against C's Term_putstr and Term_addstr ([C] src/ui-term.c).

**class `TermAddstrTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/4 documented): ✘ `WIDTH`, `HEIGHT`, `term`, `grid`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildATenByFiveTerm()` | — | ✘ |
| `cell(int, int)` | — | ✘ |
| `addstrWritesAtTheCurrentCursorPositionWithNoPriorGotoXY()` | — | ✔ |
| `putstrMovesThenWrites()` | — | ✔ |
| `explicitLengthTruncatesTheString()` | — | ✔ |
| `explicitLengthZeroDrawsNothing()` | — | ✔ |
| `aWriteThatExactlyFillsToTheEdgeStillReturnsPositiveAndMarksTheCursorUnusable()` | — | ✔ |
| `overflowClipsToTheRemainingWidthAndReturnsTheClippedCount()` | — | ✔ |
| `cursorMarkedUnusableAfterAClipRejectsFurtherAddstrCalls()` | — | ✔ |
| `gotoXyResetsTheUnusableCursorAllowingRecovery()` | — | ✔ |
| `putstrPropagatesGotoXyFailureAndNeverCallsAddstr()` | — | ✔ |


##### `TermClearFromTest.java`

**Responsible for:** Term#clearFrom, checked against C's clear_from ([C] src/ui-input.c): for (y = row; y hgt; y++) Term_erase(0, y, 255); - every row from row up to but not including the terminal's height is blanked in white, and rows above row are left exactly as they were.

**class `TermClearFromTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/4 documented): ✘ `WIDTH`, `HEIGHT`, `term`, `grid`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAnEightyByTwentyFourTermWithEveryRowFilled()` | — | ✘ |
| `cell(int, int)` | — | ✘ |
| `clearingFromAMiddleRowBlanksItAndEveryRowBelow()` | — | ✔ |
| `rowsAboveTheRequestedRowAreUntouched()` | — | ✔ |
| `aRowOfZeroClearsTheWholeTerminal()` | — | ✔ |
| `aRowEqualToTheHeightClearsNothing()` | — | ✔ |
| `clearingFromTheLastRowClearsOnlyThatRow()` | — | ✔ |
| `aNegativeRowStillClearsEveryValidRow()` | — | ✔ |


##### `TermDataTest.java`

**Responsible for:** TermData#getTerm, TermData#setWindow, TermData#getWindow and TermData#dispose - all straight field access in C (td->t, td->w, [C] src/win/win-term.h) except TermData#dispose, which has no C counterpart of its own: it extracts the one per-window step - DestroyWindow(data[i].w) - from the shutdown ...

**class `TermDataTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `requireADisplay()` | — | ✘ |
| `newScreen()` | — | ✔ |
| `getTermReturnsTheLinkedTerm()` | — | ✔ |
| `getTermReturnsTheSelfBuiltTermWhenLinkedWithNull()` | — | ✔ |
| `termField(Term, String)` | — | ✔ |
| `termDataLinkEnablesSoftCursorComplexInputAndHigherPict()` | — | ✔ |
| `termDataLinkLeavesTheDoubleHeightHookNull()` | — | ✔ |
| `termDataLinkSharesOneHookInstanceAcrossTheDrawingRoles()` | — | ✔ |
| `termDataLinkGivesTheExtraHookItsOwnInstance()` | — | ✔ |
| `termDataLinkAttachesThisTermDataToTheTerm()` | — | ✔ |
| `termDataLinkWiresTheSelfBuiltTermTheSameWay()` | — | ✔ |
| `getWindowReturnsWhatWasSet()` | — | ✔ |
| `disposeDisposesTheAttachedWindow()` | — | ✔ |


##### `TermGotoXYTest.java`

**Responsible for:** Term#gotoXY, checked against C's Term_gotoxy ([C] src/ui-term.c): (x = w) and (y = h) reject the request, everything else moves the cursor and clears its "unused" flag.

**class `TermGotoXYTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/4 documented): ✘ `WIDTH`, `HEIGHT`, `term`, `scr`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(Object, String)` | — | ✘ |
| `buildAnEightyByTwentyFourTerm()` | — | ✘ |
| `seedCursor(int, int, boolean)` | — | ✘ |
| `cx()` | — | ✘ |
| `cy()` | — | ✘ |
| `cu()` | — | ✘ |
| `anOrdinaryMoveSucceedsAndPositionsTheCursor()` | — | ✔ |
| `theFirstCellIsInRange()` | — | ✔ |
| `theLastCellIsInRange()` | — | ✔ |
| `aNegativeColumnIsRejected()` | — | ✔ |
| `aNegativeRowIsRejected()` | — | ✔ |
| `aColumnEqualToTheWidthIsRejected()` | — | ✔ |
| `aRowEqualToTheHeightIsRejected()` | — | ✔ |
| `aRejectedRequestLeavesTheCursorWhereItWas()` | — | ✔ |


##### `TermPrtTest.java`

**Responsible for:** Term#cPrt and Term#prt, checked against C's c_prt and prt ([C] src/ui-output.c): both clear the rest of the line with Term_erase and then dump the string with Term_addstr directly - unlike c_put_str, there is no Term_gotoxy step, so an out-of-range row/col is never rejected up front.

**class `TermPrtTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/4 documented): ✘ `WIDTH`, `HEIGHT`, `term`, `grid`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAnEightyByTwentyFourTerm()` | — | ✘ |
| `cell(int, int)` | — | ✘ |
| `anOrdinaryWriteErasesToEndOfLineThenPlacesTheString()` | — | ✔ |
| `aCellBeforeTheWriteIsUntouched()` | — | ✔ |
| `prtWritesInWhite()` | — | ✔ |
| `theFirstCellIsInRange()` | — | ✔ |
| `theLastCellIsInRange()` | — | ✔ |
| `aNegativeRowDrawsNothing()` | — | ✔ |
| `aRowEqualToTheHeightDrawsNothing()` | — | ✔ |
| `aColumnEqualToTheWidthDrawsNothing()` | — | ✔ |
| `aNegativeColumnWritesAtTheStaleCursorPositionInstead()` | — | ✔ |


##### `TermPutStrTest.java`

**Responsible for:** Term#cPutStr and Term#putStr, checked against C's c_put_str and put_str ([C] src/ui-output.c): both delegate entirely to Term_putstr - move first, then dump the string - and put_str is just c_put_str fixed to COLOUR_WHITE.

**class `TermPutStrTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/4 documented): ✘ `WIDTH`, `HEIGHT`, `term`, `grid`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAnEightyByTwentyFourTerm()` | — | ✘ |
| `cell(int, int)` | — | ✘ |
| `anOrdinaryWriteSucceedsAndPlacesEveryCharacter()` | — | ✔ |
| `putStrWritesInWhite()` | — | ✔ |
| `theFirstCellIsInRange()` | — | ✔ |
| `theLastCellIsInRange()` | — | ✔ |
| `aNegativeColumnIsRejectedAndNothingIsWritten()` | — | ✔ |
| `aNegativeRowIsRejectedAndNothingIsWritten()` | — | ✔ |
| `aColumnEqualToTheWidthIsRejectedAndNothingIsWritten()` | — | ✔ |
| `aRowEqualToTheHeightIsRejectedAndNothingIsWritten()` | — | ✔ |
| `putStrRejectsOutOfRangeTheSameWay()` | — | ✔ |


##### `TermWinCursorTest.java`

**Responsible for:** TermWin#setCx, TermWin#setCy and TermWin#setCu: blind field writes, the port of C directly assigning scr->cx/cy/cu ([C] src/z-term.h).

**class `TermWinCursorTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `scr`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(String)` | — | ✘ |
| `buildATermWin()` | — | ✘ |
| `setCxStoresTheColumn()` | — | ✔ |
| `setCyStoresTheRow()` | — | ✔ |
| `setCuStoresTheFlag()` | — | ✔ |
| `setCxAcceptsAnOutOfRangeValueWithoutValidation()` | — | ✔ |
| `settingOneCoordinateLeavesTheOtherAlone()` | — | ✔ |


##### `TermWinGettersTest.java`

**Responsible for:** TermWin#getCu, TermWin#getCx and TermWin#getCy: the port of reading C's scr->cu/cx/cy directly ([C] src/ui-term.h).

**class `TermWinGettersTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `scr`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildATermWin()` | — | ✘ |
| `aFreshWindowHasTheCursorAtOriginAndUsable()` | — | ✔ |
| `getCxReturnsWhatWasSet()` | — | ✔ |
| `getCyReturnsWhatWasSet()` | — | ✔ |
| `getCuReturnsWhatWasSet()` | — | ✔ |


##### `TermWinInitTest.java`

**Responsible for:** TermWin#init: the port of C's term_win_init ([C] src/ui-term.c), which allocates each layer's flat height * width buffer and points its row array into it, leaving every cell zeroed - attr 0 (COLOUR_DARK) and character 0 ('\0').

**class `TermWinInitTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(String)` | — | ✘ |
| `layer(TermWin, String)` | — | ✘ |
| `initSizesEveryLayerHeightRowsByWidthColumnsWhenWiderThanTall()` | — | ✔ |
| `initSizesEveryLayerHeightRowsByWidthColumnsWhenTallerThanWide()` | — | ✔ |
| `initFillsEveryCellWithTheZeroedCState()` | — | ✔ |
| `initHandlesTheSmallestWindow()` | — | ✔ |


##### `TextOutETest.java`

**Responsible for:** TextOut#textOutE, checked against C's text_out_e ([C] src/z-textblock.c).

**class `TextOutETest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `hook`, `textOut`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plainTextWithNoTagsIsOneWhiteCall()` | — | ✔ |
| `aTagAtTheStartIsWrittenInThatColour()` | — | ✔ |
| `textAroundATagIsSplitIntoThreeCalls()` | — | ✔ |
| `twoAdjacentTagsProduceTwoCalls()` | — | ✔ |
| `anUnrecognisedTagNameFallsBackToWhite()` | — | ✔ |
| `anUnclosedTagIsOneWholeUntaggedCall()` | — | ✔ |
| `aStringOverTheBufferLimitIsTruncatedBeforeSplitting()` | — | ✔ |

**record `TextOutETest.Call`** — class Javadoc: ✘ · type stamp: —

- Record components (via `@param`) (0/2 documented): ✘ `colour`, `string`

**class `TextOutETest.RecordingTextOutHook`** — class Javadoc: ✘ · type stamp: —

- Fields (0/1 documented): ✘ `calls`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `output(ColourEnum, String)` | — | ✘ (override) |
| `out(ColourEnum, String, Object)` | — | ✘ (override) |
| `out(String, Object)` | — | ✘ (override) |


##### `TextOutNextSectionTest.java`

**Responsible for:** TextOut#nextSection, checked against C's next_section ([C] src/z-textblock.c).

**class `TextOutNextSectionTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `textOut`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anEmptySourceIsNotFound()` | — | ✔ |
| `plainTextWithNoTagsIsReturnedWhole()` | — | ✔ |
| `aTrailingOpenBraceWithNothingAfterItIsReturnedWhole()` | — | ✔ |
| `anOpenBraceWhoseTagRunsOffTheEndIsReturnedWhole()` | — | ✔ |
| `aTagAtTheStartClipsToTheClosingTag()` | — | ✔ |
| `aMultiWordTagNameIsCapturedInFull()` | — | ✔ |
| `plainTextBeforeATagReturnsOnlyThatChunk()` | — | ✔ |
| `aTagWithNoClosingTagIsOneWholeChunk()` | — | ✔ |
| `anInvalidTagBodyIsSkippedAndTreatedAsPlainText()` | — | ✔ |
| `anInvalidTagIsSkippedAndScanningContinuesToTheNextBrace()` | — | ✔ |
| `anInvalidTagWithLettersBeforeAnotherBraceStillFindsThatBrace()` | — | ✔ |
| `found(String)` | — | ✘ |
| `section(String)` | — | ✘ |
| `invoke(String)` | — | ✘ |
| `component(Object, String)` | — | ✘ |

**record `TextOutNextSectionTest.Section`** — class Javadoc: ✘ · type stamp: —

- Record components (via `@param`) (0/4 documented): ✘ `found`, `text`, `tag`, `next`


##### `TextOutTest.java`

**Responsible for:** TextOut#textOut and TextOut#textOutC, checked against C's text_out and text_out_c ([C] src/z-textblock.c): both format into a fixed char buf[1024] via vstrnfmt(buf, sizeof(buf), fmt, vp), which leaves output shorter than the buffer untouched and truncates only text that would overrun it, to 1023 ...

**class `TextOutTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `hook`, `textOut`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anOrdinaryStringIsWrittenUnchanged()` | — | ✔ |
| `textOutWritesInWhite()` | — | ✔ |
| `aPlainStringWithNoArgumentsIsWrittenAsIs()` | — | ✔ |
| `aStringOfMaxMinusOneCharactersIsUnchanged()` | — | ✔ |
| `aStringOfMaxCharactersLosesItsLastCharacter()` | — | ✔ |
| `aStringWellOverTheLimitIsTruncatedToMaxMinusOne()` | — | ✔ |

**class `TextOutTest.RecordingTextOutHook`** — class Javadoc: ✘ · type stamp: —

- Fields (0/3 documented): ✘ `colour`, `string`, `callCount`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `output(ColourEnum, String)` | — | ✘ (override) |
| `out(ColourEnum, String, Object)` | — | ✘ (override) |
| `out(String, Object)` | — | ✘ (override) |


##### `WindowCharacterSizeTest.java`

**Responsible for:** Window#getCharacterWidth() and Window#getCharacterHeight(), a pass-through to SwingUI.JPanelArea#getDisplayWidth()/getDisplayHeight() - already checked against C's Term_get_size ([C] src/ui-term.c) by JPanelAreaDisplaySizeTest.

**class `WindowCharacterSizeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `window`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAWindowWithAPanel()` | — | ✘ |
| `widthIsEightyColumns()` | — | ✔ |
| `heightIsTwentyFourRows()` | — | ✔ |
| `reflectsTheCurrentGridAfterSetChars()` | — | ✔ |


##### `WindowShowTest.java`

**Responsible for:** Window#show(Frame) has no C original to check against - Frame is new architecture, and show is the Java side of the boundary crossing it exists for.

**class `WindowShowTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `window`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `display(SwingUI.JPanelArea)` | — | ✘ |
| `cellsOf(CellGrid)` | — | ✘ |
| `buildAWindowWithAPanel()` | — | ✘ |
| `flushEventQueue()` | — | ✔ |
| `showPaintsTheFramesGrid()` | — | ✔ |
| `showSharesRowsRatherThanCopyingThem()` | — | ✔ |


## frontend/screen/enums

##### `CombinerNameTest.java`

**Responsible for:** Unit tests for CombinerName, the port of the C original's name-indexed combiners[] table (ui-entry-combiner.c) and the ui_entry_combiner_lookup/ui_entry_combiner_get_funcs pair that indexes it (ui-entry-combiner.h).

**class `CombinerNameTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyConstantInTheCsTableIsPresentPlusTheJavaOnlyNone()` | — | ✘ |

**class `CombinerNameTest.NoneHasNoPrototype`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `initThrowsRatherThanSilentlyFoldingNothing()` | — | ✘ |

**class `CombinerNameTest.InitResolvesAndSeedsTheRightStrategy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `add()` | — | ✘ |
| `bitwiseOr()` | — | ✘ |
| `first()` | — | ✘ |
| `largest()` | — | ✘ |
| `last()` | — | ✘ |
| `smallest()` | — | ✘ |
| `logicalOrNormalisesANonZeroFirstContributionToOne()` | — | ✘ |
| `logicalOrWithCancelAlsoNormalisesAPositiveFirstContributionToOne()` | — | ✘ |
| `logicalOrWithCancelEncodesANegativeFirstContributionAsMinusOne()` | — | ✘ |
| `resist0PassesAPositiveFirstContributionThroughUnchanged()` | — | ✘ |
| `resist0EncodesANegativeFirstContributionAsMinusOne()` | — | ✘ |
| `assertSeeds(CombinerName, Class<? extends Combiner>, int, int, int, int)` | — | ✘ |

**class `CombinerNameTest.EachCallIsIndependent`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `initHandsBackADistinctInstanceEveryTime(CombinerName)` | — | ✘ |
| `furtherAccumulationOnOneInstanceDoesNotLeakIntoAnother(CombinerName)` | — | ✘ |


## frontend/screen/grid

##### `CellGridTest.java`

**Responsible for:** Tests CellGrid against the decisions recorded in docs/UIPanelArchitecture.md - there is no C original to check against, since this is new architecture, not a port.

**class `CellGridTest`** — class Javadoc: ✔ · type stamp: on 260909

- Fields (0/4 documented): ✘ `ROWS`, `COLS`, `A`, `B`

**class `CellGridTest.Construction`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsConstructedSize()` | — | ✘ |
| `everyCellStartsNull()` | — | ✔ |
| `colsOnZeroRowGridThrows()` | — | ✔ |

**class `CellGridTest.Get`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsWhatWasSet()` | — | ✘ |
| `lastValidCellIsReadable()` | — | ✘ |
| `negativeRowReturnsNull()` | — | ✘ |
| `rowAtGridHeightReturnsNull()` | — | ✘ |
| `negativeColumnReturnsNull()` | — | ✘ |
| `columnAtGridWidthReturnsNull()` | — | ✘ |

**class `CellGridTest.Set`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `laterSetOverwritesEarlierOne()` | — | ✘ |
| `lastValidCellIsWritable()` | — | ✘ |
| `negativeRowThrows()` | — | ✘ |
| `rowAtGridHeightThrows()` | — | ✘ |
| `negativeColumnThrows()` | — | ✘ |
| `columnAtGridWidthThrows()` | — | ✘ |

**class `CellGridTest.Copy`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copyStartsEqualToOriginal()` | — | ✘ |
| `writingToOriginalAfterCopyingDoesNotChangeCopy()` | — | ✔ |
| `writingToCopyDoesNotChangeOriginal()` | — | ✘ |

**class `CellGridTest.GetCells`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsEveryCell()` | — | ✘ |
| `replacingReturnedRowDoesNotChangeGrid()` | — | ✔ |
| `laterSetIsVisibleThroughReturnedArray()` | — | ✔ |


##### `FrameTest.java`

**Responsible for:** Tests Frame against the boundary-crossing contract set out in docs/UIPanelArchitecture.md - there is no C original to check against, since this is new architecture, not a port.

**class `FrameTest`** — class Javadoc: ✔ · type stamp: on 260909

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aOneCellGrid(char)` | — | ✘ |
| `gridAccessorReturnsThePassedInstance()` | — | ✘ |
| `hotspotsAccessorReturnsThePassedList()` | — | ✘ |
| `framesOverSameGridAndEqualHotspotsAreEqual()` | — | ✘ |
| `framesOverDistinctButIdenticalGridsAreNotEqual()` | — | ✔ |
| `framesOverSameGridButDifferentHotspotsAreNotEqual()` | — | ✘ |


##### `HotspotTest.java`

**Responsible for:** Tests Hotspot against the boundary-crossing contract set out in docs/UIPanelArchitecture.md - there is no C original to check against, since this is new architecture, not a port.

**class `HotspotTest`** — class Javadoc: ✔ · type stamp: on 260909

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `accessorsReturnConstructorArguments()` | — | ✘ |
| `hotspotsWithEqualComponentsAreEqual()` | — | ✘ |
| `differingTopBreaksEquality()` | — | ✘ |
| `differingLeftBreaksEquality()` | — | ✘ |
| `differingRowsBreaksEquality()` | — | ✘ |
| `differingColsBreaksEquality()` | — | ✘ |
| `differingEventBreaksEquality()` | — | ✘ |
| `distinctPlainObjectEventsAreNotEqual()` | — | ✔ |


##### `RegionTest.java`

**Responsible for:** Tests Region against the clip contract set out in docs/UIPanelArchitecture.md - there is no C original to check against, since this is new architecture, not a port.

**class `RegionTest`** — class Javadoc: ✔ · type stamp: on 260909

- Fields (2/10 documented): ✔ `MARKER`, `BLANK` — ✘ `GRID_ROWS`, `GRID_COLS`, `TOP`, `LEFT`, `REGION_ROWS`, `REGION_COLS`, `grid`, `region`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAFullyMarkedGridAndAnOffsetRegion()` | — | ✘ |
| `assertGridMatches(Map<Coord,AngbandDisplayCharacter>)` | — | ✔ |
| `assertRectangleBlankedRestMarked(int, int, int, int)` | — | ✔ |

**record `RegionTest.Coord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/2 documented): ✘ `row`, `col`

**class `RegionTest.PutCharacter`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `writesToTheTranslatedCell()` | — | ✔ |
| `lastValidCellIsWritable()` | — | ✔ |
| `negativeRowIsANoOp()` | — | ✘ |
| `rowAtRegionHeightIsANoOp()` | — | ✘ |
| `negativeColumnIsANoOp()` | — | ✘ |
| `columnAtRegionWidthIsANoOp()` | — | ✘ |

**class `RegionTest.PutString`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `writesStringStartingAtGivenColumn()` | — | ✘ |
| `negativeRowDrawsNothing()` | — | ✘ |
| `rowAtRegionHeightDrawsNothing()` | — | ✘ |
| `negativeColumnDropsLeadingCharacters()` | — | ✔ |
| `negativeColumnDroppingWholeStringDrawsNothing()` | — | ✘ |
| `columnAtRegionWidthDrawsNothing()` | — | ✘ |
| `overrunningRightEdgeIsTruncated()` | — | ✔ |
| `bothClipRulesCanApplyToSameCall()` | — | ✔ |

**class `RegionTest.Erase`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `erasesOnlyTheRequestedRun()` | — | ✘ |
| `aRunPastTheRightEdgeIsClampedNotRejected()` | — | ✔ |
| `theLastColumnCanBeErasedAlone()` | — | ✘ |
| `aFullWidthRunErasesTheEntireRegionRow()` | — | ✘ |
| `zeroCellsErasesNothing()` | — | ✘ |
| `negativeCellCountErasesNothing()` | — | ✘ |
| `negativeColumnDropsTheOverhangInsteadOfBeingRejected()` | — | ✔ |
| `columnEntirelyOffTheLeftErasesNothing()` | — | ✘ |
| `negativeRowErasesNothing()` | — | ✘ |
| `rowAtRegionHeightErasesNothing()` | — | ✘ |
| `columnAtRegionWidthErasesNothing()` | — | ✘ |

**class `RegionTest.Clear`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `blanksEveryCellInTheRegionOnly()` | — | ✔ |

**class `RegionTest.Dimensions`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportTheRegionsOwnSize()` | — | ✔ |
| `colsAndRowsAreNotSwapped()` | — | ✘ |


##### `ScreenTest.java`

**Responsible for:** Tests Screen against the ownership and publication contract set out in docs/UIPanelArchitecture.md, and, for Screen#splashScreenNote(String), against the non-birth branch of splashscreen_note ([C] src/ui-display.c) it ports.

**class `ScreenTest`** — class Javadoc: ✔ · type stamp: on 260909

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rootWritesThroughToLiveGrid()` | — | ✘ |
| `rootIsSizedToExactlyCoverTheLiveGrid()` | — | ✘ |
| `frameCopiesTheLiveGrid()` | — | ✘ |
| `mutatingLiveGridAfterFrameLeavesFrameUnaffected()` | — | ✘ |
| `frameCopiesTheHotspotList()` | — | ✘ |
| `mutatingHotspotListAfterFrameLeavesFrameUnaffected()` | — | ✘ |
| `rowText(CellGrid, int)` | — | ✔ |
| `splashScreenNoteBracketsAndCentresTheMessage()` | — | ✘ |
| `splashScreenNoteTruncatesOddRemainderTowardZero()` | — | ✘ |
| `splashScreenNoteBracketsEmptyMessage()` | — | ✘ |
| `splashScreenNoteErasesRowBeforeWriting()` | — | ✘ |
| `splashScreenNoteLeavesOtherRowsUntouched()` | — | ✘ |
| `splashScreenNoteClipsAnOverwideMessage()` | — | ✘ |


## frontend/screen/handlers

##### `DepthRowTest.java`

**Responsible for:** HandlersHolder's prtDepth and fmtDepths and the RedrawRouter#setPlayerDepth payload that feeds them, checked against C's originals ([C] ui-display.c, functions prt_depth and fmt_depth).

**class `DepthRowTest`** — class Javadoc: ✔ · type stamp: on 260929

- Fields (0/6 documented): ✘ `WIDTH`, `HEIGHT`, `ROW`, `FIELD`, `grid`, `savedTerm`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `termField()` | — | ✘ |
| `prtDepth(int, int)` | — | ✘ |
| `fmtDepths(int)` | — | ✘ |
| `draw(int)` | — | ✘ |
| `field()` | — | ✘ |
| `buildTerm()` | — | ✘ |
| `restore()` | — | ✘ |
| `levelZeroIsTown()` | — | ✔ |
| `levelOne()` | — | ✔ |
| `levelTwelve()` | — | ✔ |
| `levelOneTwentySeven()` | — | ✔ |
| `returningToTownWipesThePreviousText()` | — | ✔ |
| `drawnInWhite()` | — | ✔ |
| `fmtDepthsIsUnpadded()` | — | ✔ |
| `beforeAnyMessageReadsTown()` | — | ✔ |
| `wrongShapedPayloadIsDropped()` | — | ✔ |


##### `HandlersHolderTest.java`

**Responsible for:** HandlersHolder's prt_field/prt_race/prt_class/prt_level/ prt_title/fmt_title/prt_gold/prt_stat/prt_ac port and its side_handlers[] table, checked against C's originals ([C] ui-display.c, functions prt_field, prt_race, prt_class, prt_level, prt_title, fmt_title, prt_gold, prt_stat, prt_ac and its ...

**class `HandlersHolderTest`** — class Javadoc: ✔ · type stamp: on 260927

- Fields (0/20 documented): ✘ `WIDTH`, `HEIGHT`, `grid`, `savedTerm`, `savedRaceName`, `savedClassName`, `savedTitle`, `savedShapeName`, `savedShapechanged`, `savedWizard`, `savedTotalWinner`, `savedLevel`, `savedMaxLevel`, `savedGold`, `savedAc`, `savedPyMaxLevel`, `savedEquipString`, `savedCurrentStats`, `savedMaxStats`, `savedUseStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `termField()` | — | ✘ |
| `prtFieldMethod()` | — | ✘ |
| `prtRaceMethod()` | — | ✘ |
| `prtClassMethod()` | — | ✘ |
| `prtLevelMethod()` | — | ✘ |
| `prtTitleMethod()` | — | ✘ |
| `fmtTitleMethod()` | — | ✘ |
| `prtGoldMethod()` | — | ✘ |
| `prtEquippyMethod()` | — | ✘ |
| `prtStrMethod()` | — | ✘ |
| `prtIntMethod()` | — | ✘ |
| `prtWisMethod()` | — | ✘ |
| `prtDexMethod()` | — | ✘ |
| `prtConMethod()` | — | ✘ |
| `prtStatMethod()` | — | ✘ |
| `prtAcMethod()` | — | ✘ |
| `setCurrentStatMethod()` | — | ✘ |
| `setMaxStatMethod()` | — | ✘ |
| `setUseStatMethod()` | — | ✘ |
| `setRaceNameMethod()` | — | ✘ |
| `setClassNameMethod()` | — | ✘ |
| `setTitleMethod()` | — | ✘ |
| `setShapeNameMethod()` | — | ✘ |
| `setWizardMethod()` | — | ✘ |
| `setTotalWinnerMethod()` | — | ✘ |
| `setLevelMethod()` | — | ✘ |
| `setMaxLevelMethod()` | — | ✘ |
| `setShapechangedMethod()` | — | ✘ |
| `setGoldMethod()` | — | ✘ |
| `setAcMethod()` | — | ✘ |
| `setEquippyStringMethod()` | — | ✘ |
| `cell(int, int)` | — | ✘ |
| `buildTermAndSaveState()` | — | ✘ |
| `restoreState()` | — | ✘ |
| `prtFieldClearsThirteenCellsThenWritesTheText()` | — | ✔ |
| `prtFieldWithEmptyTextLeavesTheWholeFieldBlank()` | — | ✔ |
| `prtRaceWritesTheRaceNameWhenNotShapechanged()` | — | ✔ |
| `prtRaceBlanksTheFieldWhenShapechanged()` | — | ✔ |
| `initHandlersRegistersAllFourteenRowsInTableOrder()` | — | ✔ |
| `prtEquippyDrawsEachGlyphAtItsOwnColumn()` | — | ✔ |
| `prtClassWritesTheClassNameWhenNotShapechanged()` | — | ✔ |
| `prtClassBlanksTheFieldWhenShapechanged()` | — | ✔ |
| `prtLevelWritesLevelInLightGreenWhenAtMaximum()` | — | ✔ |
| `prtLevelWritesLevelInYellowWhenBelowMaximum()` | — | ✔ |
| `prtTitleDrawsTheFormattedTitleText()` | — | ✔ |
| `fmtTitleReturnsWizardTextWhenWizard()` | — | ✔ |
| `fmtTitleReturnsWinnerTextWhenTotalWinner()` | — | ✔ |
| `fmtTitleReturnsWinnerTextWhenLevelExceedsCap()` | — | ✔ |
| `fmtTitleCapitalisesTheShapeNameWhenShapechanged()` | — | ✔ |
| `fmtTitleDoesNotThrowWhenShapeNameIsShorterThanSize()` | — | ✔ |
| `fmtTitleReturnsThePlainTitleWhenNoOtherCaseApplies()` | — | ✔ |
| `fmtTitleReturnsEmptyStringInShortModeWhenNoOtherCaseApplies()` | — | ✔ |
| `prtGoldWritesLabelAndFormattedFigure()` | — | ✔ |
| `prtGoldFormatsZeroAsNineWideField()` | — | ✔ |
| `prtAcWritesLabelAndFormattedFigure()` | — | ✔ |
| `prtAcFormatsZeroAsFiveWideField()` | — | ✔ |
| `prtStatWritesReducedNameAndUseValueWhenDrained()` | — | ✔ |
| `prtStatWritesNormalNameAndUseValueWhenFull()` | — | ✔ |
| `prtStatDrawsNaturalMaximumMarker()` | — | ✔ |
| `prtStatDrawsNoMarkerBelowNaturalMaximum()` | — | ✔ |
| `statWrappersEachReadTheirOwnIndex()` | — | ✔ |


##### `HealthBarTest.java`

**Responsible for:** HandlersHolder's prtHealth/prtHealthAux/monsterHealthAttr, and RedrawRouter#setMonsterHealth, which feeds them, checked against C's originals ([C] ui-display.c, functions prt_health, prt_health_aux and monster_health_attr).

**class `HealthBarTest`** — class Javadoc: ✔ · type stamp: on 260929

- Fields (0/6 documented): ✘ `WIDTH`, `HEIGHT`, `ROW`, `COL`, `grid`, `savedTerm`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `termField()` | — | ✘ |
| `invoke(String, int, int)` | — | ✘ |
| `attr()` | — | ✘ |
| `track(int, int)` | — | ✔ |
| `trackWith(int, int, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)` | — | ✘ |
| `untrack(boolean)` | — | ✘ |
| `stars()` | — | ✘ |
| `starColour()` | — | ✘ |
| `colourAt(int)` | — | ✘ |
| `buildTerm()` | — | ✘ |
| `restore()` | — | ✘ |
| `routerWritesEveryComponent()` | — | ✔ |
| `routerIgnoresOtherPayloads()` | — | ✔ |
| `notTrackingErasesTwelveCellsAndReturnsZero()` | — | ✔ |
| `notTrackingColourIsDark()` | — | ✔ |
| `unknownBarIsWhiteForUnseenHallucinatedAndDead()` | — | ✔ |
| `starCountFollowsCsThresholds()` | — | ✔ |
| `percentageTruncates()` | — | ✔ |
| `unfilledPartAndBracketsStayWhite()` | — | ✔ |
| `healthColourBoundaries()` | — | ✔ |
| `eachTimedEffectOverridesTheHealthColour()` | — | ✔ |
| `laterEffectWinsOverEarlier()` | — | ✔ |
| `effectsDoNotChangeStarCount()` | — | ✔ |
| `sideTableHoldsTwentyTwoRows()` | — | ✔ |


##### `HpSpRowsTest.java`

**Responsible for:** HandlersHolder's prtHp/prtSp and their colour helpers, and the RedrawRouter payloads that feed them, checked against C's originals ([C] ui-display.c, functions prt_hp and prt_sp; [C] player.c, functions player_hp_attr and player_sp_attr).

**class `HpSpRowsTest`** — class Javadoc: ✔ · type stamp: on 260928

- Fields (0/4 documented): ✘ `WIDTH`, `HEIGHT`, `grid`, `savedTerm`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `termField()` | — | ✘ |
| `invoke(String, int, int)` | — | ✘ |
| `term()` | — | ✘ |
| `cell(int, int)` | — | ✘ |
| `hp(int, int, int)` | — | ✘ |
| `sp(int, int, int, boolean)` | — | ✘ |
| `levelAndExp(int, long, long)` | — | ✘ |
| `hpColour(int, int, int)` | — | ✘ |
| `spColour(int, int, int)` | — | ✘ |
| `buildTerm()` | — | ✘ |
| `restore()` | — | ✘ |
| `hpIntPayloadWritesTheWarningOption()` | — | ✔ |
| `manaIntAndBooleanPayloadsLandInTheirOwnFields()` | — | ✔ |
| `prtHpLaysOutLabelAndFields()` | — | ✔ |
| `hpColourBoundaries()` | — | ✔ |
| `hpColourUsesIntegerDivision()` | — | ✔ |
| `hpColourWithWarningOff()` | — | ✔ |
| `spColourBoundaries()` | — | ✔ |
| `prtSpDrawsForACasterAtOrAboveFirstSpellLevel()` | — | ✔ |
| `prtSpDrawsNothingForAClassWithoutSpells()` | — | ✔ |
| `prtSpDrawsNothingBelowFirstSpellLevel()` | — | ✔ |
| `prtSpBlanksTheFieldWhenLevelDrained()` | — | ✔ |
| `prtSpDoesNotBlankADrainedClassWithoutSpells()` | — | ✔ |


##### `SideHandlerTest.java`

**Responsible for:** SideHandler holding exactly what its constructor was given and invoking its hook - the Java stand-in for C's anonymous side_handler_t struct literal ([C] ui-display.c), which has no behaviour of its own beyond storing the three values a side_handlers[] row is initialised with and calling through ...

**class `SideHandlerTest`** — class Javadoc: ✔ · type stamp: on 260927

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `priorityAndTypeRoundTrip()` | — | ✔ |
| `getResultInvokesTheHookWithTheGivenCoordinates()` | — | ✔ |


##### `SpeedRowTest.java`

**Responsible for:** HandlersHolder's prtSpeed and the RedrawRouter#setPlayerSpeed payload that feeds it, checked against C's originals ([C] ui-display.c, functions prt_speed and prt_speed_aux).

**class `SpeedRowTest`** — class Javadoc: ✔ · type stamp: on 260929

- Fields (0/5 documented): ✘ `WIDTH`, `HEIGHT`, `ROW`, `grid`, `savedTerm`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `termField()` | — | ✘ |
| `prtSpeed(int, int)` | — | ✘ |
| `draw(int, boolean, int, int)` | — | ✔ |
| `field()` | — | ✔ |
| `colourAt(int)` | — | ✘ |
| `buildTerm()` | — | ✘ |
| `restore()` | — | ✘ |
| `normalSpeedBlanksTheField()` | — | ✔ |
| `returningToNormalSpeedWipesThePreviousText()` | — | ✔ |
| `offsetFormFast()` | — | ✔ |
| `offsetFormSlow()` | — | ✘ |
| `oneStepEitherSideOfNormal()` | — | ✔ |
| `extremesOfTheSpeedRange()` | — | ✔ |
| `multiplierForm()` | — | ✔ |
| `multiplierTruncates()` | — | ✔ |
| `normalSpeedWithEffectiveOptionIsBlank()` | — | ✔ |
| `routerWritesAllFourParts()` | — | ✔ |
| `routerIgnoresOtherPayloads()` | — | ✔ |
| `registeredAtPriority13()` | — | ✔ |


## frontend/screen/hooks

##### `TermScreenHookTest.java`

**Responsible for:** TermScreenHook, checked directly rather than through uk.co.jackoftradesltd.frontend.screen.Term.

**class `TermScreenHookTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/5 documented): ✘ `WIDTH`, `HEIGHT`, `hook`, `grid`, `screen`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildATenByFiveHookOverAnEmptyGrid()` | — | ✘ |
| `negativeNDrawsTheWholeStringWithNoTruncation()` | — | ✔ |
| `negativeNReliesOnTheRegionsOwnRightEdgeClipping()` | — | ✔ |
| `nOfZeroDrawsNothing()` | — | ✔ |
| `explicitNTruncatesToExactlyNCharacters()` | — | ✔ |
| `eraseSwapsColRowIntoRegionsRowColOrder()` | — | ✔ |
| `oversizedNClipsToTheRowsRemainingColumns()` | — | ✔ |


##### `TermXtraWinDelayTest.java`

**Responsible for:** TermXtraWinDelay, exercising the guard C's Term_xtra_win_delay(int v) ([C] src/main-win.c) applies with if (v > 0) Sleep(v) - a positive value must actually pause the calling thread, while zero or negative values must return without sleeping - plus the restored-interrupt-status behaviour that has...

**class `TermXtraWinDelayTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `hook`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAHook()` | — | ✘ |
| `positiveValueSleepsForAtLeastThatManyMilliseconds()` | — | ✔ |
| `zeroValueReturnsWithoutAnyMeaningfulDelay()` | — | ✔ |
| `negativeValueReturnsImmediatelyWithoutSleepingOrThrowing()` | — | ✔ |
| `interruptedSleepLeavesTheThreadsInterruptStatusSet()` | — | ✔ |


##### `TermXtraWinTest.java`

**Responsible for:** TermXtraWin#termXtraWinNoise(uk.co.jackoftradesltd.frontend.sounds.MessageBoxFlags), reached only through TermXtraWin#doSomething.

**class `TermXtraWinTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `hook`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildAHook()` | — | ✘ |
| `noiseEventNeverThrowsRegardlessOfAudioAvailability()` | — | ✔ |
| `noiseEventIgnoresItsValueParameter()` | — | ✔ |


## frontend/sounds

##### `MessageBoxFlagsTest.java`

**Responsible for:** MessageBoxFlags, exercising the fact that C's Term_xtra_win_noise ([C] src/main-win.c) only ever calls MessageBeep(MB_ICONASTERISK) - no other MB_* flag is ever raised as a sound anywhere in the C original, so MessageBoxFlags#MB_ICONASTERISK must be the only constant carrying a real sound file, a...

**class `MessageBoxFlagsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onlyIconAsteriskCarriesASoundFile()` | — | ✔ |
| `iconAsteriskFileResolvesUnderTheSoundsDirectory()` | — | ✔ |
| `iconAsteriskFileExistsAndIsReadableAsAudio()` | — | ✔ |


## frontend/splash

##### `SplashScreenTest.java`

**Responsible for:** Tests SplashScreen against the two halves of show_splashscreen()/ splashscreen_note() ([C] src/ui-init.c, [C] src/ui-display.c) it ports: SplashScreen#readAndParse turning news.txt into a painted grid, SplashScreen#splashScreenNote(String, Screen) bracketing and centring a progress note, and Spla...

**class `SplashScreenTest`** — class Javadoc: ✔ · type stamp: on 260916

- Fields (0/1 documented): ✘ `tempDir`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newSplashScreen()` | — | ✘ |
| `rowText(CellGrid, int)` | — | ✔ |
| `resetBirthLine()` | — | ✘ |
| `panelDisplay(SwingUI.JPanelArea)` | — | ✘ |
| `stringFrom(AngbandDisplayCharacter[][], int, int)` | — | ✘ |
| `needsADisplay()` | — | ✘ |
| `newsFile(String)` | — | ✘ |
| `readAndParsePaintsPlainTextInWhite()` | — | ✘ |
| `readAndParseColoursTaggedSpan()` | — | ✘ |
| `readAndParseUnrecognisedColourDefaultsToWhite()` | — | ✘ |
| `readAndParseReplacesVersionTag()` | — | ✘ |
| `readAndParseDiscardsTextAfterVersionTag()` | — | ✘ |
| `readAndParseStopsAfter24Rows()` | — | ✘ |
| `readAndParseClipsLineWiderThan80Columns()` | — | ✘ |
| `splashScreenNoteBracketsAndCentresTheMessage()` | — | ✘ |
| `splashScreenNoteErasesRowBeforeWriting()` | — | ✘ |
| `splashScreenNoteLeavesOtherRowsUntouched()` | — | ✘ |
| `splashScreenNoteClipsAnOverwideMessage()` | — | ✘ |
| `splashScreenBirthNoteWritesFromColumnZero()` | — | ✘ |
| `splashScreenBirthNoteAdvancesOneRowPerCall()` | — | ✘ |
| `splashScreenBirthNoteWrapsAtTwentyFour()` | — | ✘ |


## frontend/ui

##### `RedrawRouterTest.java`

**Responsible for:** Every RedrawRouter setter against what its own payload record is documented to carry, not against re-reading RedrawRouter's own body.

**class `RedrawRouterTest`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (1/20 documented): ✔ `savedCurrentHp` — ✘ `savedMaxHp`, `savedCurrentSp`, `savedMaxSp`, `savedTitle`, `savedClassName`, `savedRaceName`, `savedName`, `savedShapeName`, `savedWizard`, `savedTotalWinner`, `savedShapechanged`, `savedLevel`, `savedMaxLevel`, `savedGold`, `savedAc`, `savedEquipString`, `savedCurrentStats`, `savedMaxStats`, `savedUseStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveModel()` | — | ✘ |
| `restoreModel()` | — | ✘ |
| `currentAndMaxAreNotSwapped()` | — | ✔ |
| `aSecondMessageOverwritesTheFirst()` | — | ✔ |
| `aNonStatPayloadLeavesTheModelUntouched()` | — | ✔ |
| `spCurrentAndMaxAreNotSwapped()` | — | ✔ |
| `aSecondSpMessageOverwritesTheFirst()` | — | ✔ |
| `aNonStatSpPayloadLeavesTheModelUntouched()` | — | ✔ |
| `titleWizardWinnerAndShapeAreNotSwapped()` | — | ✔ |
| `aNonStringsTitlePayloadLeavesTheModelUntouched()` | — | ✔ |
| `nameIsWrittenFromTheStringPayload()` | — | ✔ |
| `aNonStringNamePayloadLeavesTheModelUntouched()` | — | ✔ |
| `raceClassAndShapechangedAreNotSwapped()` | — | ✔ |
| `aNonStringsRaceClassPayloadLeavesTheModelUntouched()` | — | ✔ |
| `levelAndMaxLevelAreNotSwapped()` | — | ✔ |
| `aSecondLevelMessageOverwritesTheFirst()` | — | ✔ |
| `aNonStatLevelPayloadLeavesTheModelUntouched()` | — | ✔ |
| `goldIsWrittenFromTheLongPayload()` | — | ✔ |
| `aSecondGoldMessageOverwritesTheFirst()` | — | ✔ |
| `aNonLongGoldPayloadLeavesTheModelUntouched()` | — | ✔ |
| `acIsWrittenFromTheIntPayload()` | — | ✔ |
| `aSecondAcMessageOverwritesTheFirst()` | — | ✔ |
| `aNonIntAcPayloadLeavesTheModelUntouched()` | — | ✔ |
| `equippyIsWrittenFromTheColourStringPayload()` | — | ✔ |
| `aSecondEquippyMessageOverwritesTheFirst()` | — | ✔ |
| `aNonColourStringEquippyPayloadLeavesTheModelUntouched()` | — | ✔ |
| `statIndexCurrentMaxAndUseAreNotSwapped()` | — | ✔ |
| `onlyTheNamedStatIndexIsWritten()` | — | ✔ |
| `aSecondStatsMessageOverwritesTheFirstForTheSameIndex()` | — | ✔ |
| `aNonFullStatPayloadLeavesTheModelUntouched()` | — | ✔ |


##### `SidebarModelTest.java`

**Responsible for:** SidebarModel holding exactly what is written to it - the port has no C counterpart to diverge from here, since C's prt_hp/prt_sp ([C] ui-display.c, functions prt_hp and prt_sp) read player->chp/mhp and player->csp/msp straight off the shared global rather than through a model at all.

**class `SidebarModelTest`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (1/20 documented): ✔ `savedCurrentHp` — ✘ `savedMaxHp`, `savedCurrentSp`, `savedMaxSp`, `savedTitle`, `savedClassName`, `savedRaceName`, `savedName`, `savedShapechanged`, `savedWizard`, `savedTotalWinner`, `savedShapeName`, `savedLevel`, `savedMaxLevel`, `savedGold`, `savedAc`, `savedEquipString`, `savedCurrentStats`, `savedMaxStats`, `savedUseStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveModel()` | — | ✘ |
| `restoreModel()` | — | ✘ |
| `currentHpRoundTrips()` | — | ✔ |
| `maxHpRoundTrips()` | — | ✔ |
| `zeroRoundTripsForBothFields()` | — | ✔ |
| `currentSpRoundTrips()` | — | ✔ |
| `maxSpRoundTrips()` | — | ✔ |
| `zeroRoundTripsForBothSpFields()` | — | ✔ |
| `hpAndSpDoNotAliasEachOther()` | — | ✔ |
| `titleRoundTrips()` | — | ✔ |
| `classNameRoundTrips()` | — | ✔ |
| `raceNameRoundTrips()` | — | ✔ |
| `nameRoundTrips()` | — | ✔ |
| `playerIsShapechangedRoundTrips()` | — | ✔ |
| `wizardRoundTrips()` | — | ✔ |
| `totalWinnerRoundTrips()` | — | ✔ |
| `shapeNameRoundTrips()` | — | ✔ |
| `levelRoundTrips()` | — | ✔ |
| `maxLevelRoundTrips()` | — | ✔ |
| `goldRoundTrips()` | — | ✔ |
| `zeroGoldRoundTrips()` | — | ✔ |
| `acRoundTrips()` | — | ✔ |
| `zeroAcRoundTrips()` | — | ✔ |
| `equippyStringRoundTrips()` | — | ✔ |
| `currentStatRoundTrips()` | — | ✔ |
| `maxStatRoundTrips()` | — | ✔ |
| `useStatRoundTrips()` | — | ✔ |
| `distinctStatIndicesDoNotAliasEachOther()` | — | ✔ |
| `outOfRangeStatIndexReturnsZeroRatherThanThrowing()` | — | ✔ |
| `outOfRangeStatIndexIsIgnoredBySetters()` | — | ✔ |


##### `UIEntryCodeTest.java`

**Responsible for:** Tests UIEntryCode, the port of C's initialize_ui_entry_iterator and its private comparator cmp_desc_prio ([C] ui-entry.c:392-483).

**class `UIEntryCodeTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noFlags()` | — | ✘ |
| `templateOnlyFlag()` | — | ✘ |
| `entry(String, Flag<ChannelEntryFlag>, UIEntryCategory)` | — | ✘ |
| `resetRegistry()` | — | ✘ |

**class `UIEntryCodeTest.InitialiseUIEntryIterator`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `excludesTemplateOnlyEntries()` | — | ✘ |
| `excludesEntriesThePredicateRejects()` | — | ✘ |
| `ordersByNameWithNoSortCategory()` | — | ✘ |
| `ordersByDescendingCategoryPriority()` | — | ✘ |
| `categoryMemberSortsBeforeNonMember()` | — | ✘ |
| `tiedPriorityFallsBackToName()` | — | ✘ |
| `neitherCarryingCategoryFallsBackToName()` | — | ✘ |
| `closureIsPassedThroughToThePredicate()` | — | ✘ |
| `emptyRegistryProducesEmptyIterator()` | — | ✘ |


##### `UIPlayerTest.java`

**Responsible for:** Tests UIPlayer#haveValidCharSheetConfig() against C's have_valid_char_sheet_config ([C] src/ui-player.c:146-156).

**class `UIPlayerTest`** — class Javadoc: ✔ · type stamp: on 260911

- Fields (2/2 documented): ✔ `uiPlayer`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `setCachedConfig(CharSheetConfig)` | — | ✔ |
| `configWith(int, int)` | — | ✔ |
| `setPlayerBodySlotCount(int)` | — | ✔ |
| `invoke()` | — | ✔ |

**class `UIPlayerTest.NoCachedConfig`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsInvalid()` | — | ✔ |

**class `UIPlayerTest.MatchingCache`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsValid()` | — | ✔ |
| `reportsValidWithZeroLabelWidth()` | — | ✔ |

**class `UIPlayerTest.MismatchedCache`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsInvalidWhenTooHigh()` | — | ✔ |
| `reportsInvalidWhenTooLow()` | — | ✔ |
| `reportsInvalidAfterBodyChanges()` | — | ✔ |

**class `UIPlayerTest.GetUIEntryLabel`** — class Javadoc: ✔ · type stamp: on 260925

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `entryWithLabel(String)` | — | ✔ |
| `invoke(UIEntry, int, boolean, String)` | — | ✔ |
| `returnsFallbackWhenLengthZero()` | — | ✔ |
| `returnsFallbackWhenLengthNegative()` | — | ✔ |
| `returnsEmptyWhenLengthOne()` | — | ✔ |
| `usesShortenedLabelWithLeftPadding()` | — | ✔ |
| `usesShortenedLabelWithRightPadding()` | — | ✔ |
| `usesShortenedLabelAtBoundary()` | — | ✔ |
| `usesDefaultLabelPastBoundary()` | — | ✔ |
| `truncatesWhenSourceTooLong()` | — | ✔ |
| `returnsWholeTextAtExactWidth()` | — | ✔ |

**class `UIPlayerTest.ConfigureCharSheet`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (0/1 documented): ✘ `savedCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveGlobalState()` | — | ✔ |
| `restoreGlobalState()` | — | ✔ |
| `entry(String, UIEntryCategory)` | — | ✔ |
| `charScreen1()` | — | ✔ |
| `category(String, int)` | — | ✔ |
| `invoke()` | — | ✔ |
| `cachedConfig()` | — | ✔ |
| `nStatModEntries(CharSheetConfig)` | — | ✔ |
| `laysOutRegionsAndRetainsEveryEntry()` | — | ✔ |
| `clampsStatModEntriesToStatMax()` | — | ✔ |
| `clampsRegionEntryCountToFitDisplay()` | — | ✔ |
| `excludesEntriesMissingCharScreen1()` | — | ✔ |

**class `UIPlayerTest.CnvStat`** — class Javadoc: ✔ · type stamp: on 260925

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `invoke(int)` | — | ✔ |
| `formatsPlainNumberAtOrBelowEighteen()` | — | ✔ |
| `padsBonusBelowOneHundred()` | — | ✔ |
| `switchesToThreeDigitsAtOneHundred()` | — | ✔ |
| `formatsBonusAboveOneHundred()` | — | ✔ |
| `collapsesToCapAtTwoHundredTwenty()` | — | ✔ |
| `invoke(int, int)` | — | ✔ |
| `truncatesToLenMinusOne()` | — | ✔ |
| `truncatesAtLowestLengths()` | — | ✔ |
| `leavesResultUntouchedForLargeLen()` | — | ✔ |
| `throwsWhenLenIsZero()` | — | ✔ |

**class `UIPlayerTest.DisplayPlayerStatInfo`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (0/7 documented): ✘ `WIDTH`, `HEIGHT`, `ROW`, `COL`, `grid`, `savedStatusView`, `savedCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `arrange()` | — | ✔ |
| `restore()` | — | ✔ |
| `cell(int, int)` | — | ✘ |
| `text(int, int, int)` | — | ✔ |
| `invoke()` | — | ✔ |
| `writesColumnHeaders()` | — | ✔ |
| `usesUppercaseNameWithNoMarkerWhenUndrained()` | — | ✔ |
| `usesLowercaseNameWithMarkerWhenDrainedAtNaturalMaximum()` | — | ✔ |
| `usesLowercaseNameWithNoMarkerWhenDrainedBelowNaturalMaximum()` | — | ✔ |
| `formatsMaximumColumnsViaCnvStat()` | — | ✔ |
| `formatsBonusColumnsSigned()` | — | ✔ |
| `showsUsedValueColumnOnlyWhenDrained()` | — | ✔ |

**class `UIPlayerTest.PlayerDisplayModeEnum`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hasExactlyTwoModes()` | — | ✔ |
| `declaresFullBeforeExtra()` | — | ✔ |

**class `UIPlayerTest.PanelAllocate`** — class Javadoc: ✔ · type stamp: on 260925

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `invoke(int)` | — | ✔ |
| `readInt(Object, String)` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `setsMaxAndZeroesLen()` | — | ✔ |
| `initialisesEmptyLines()` | — | ✔ |
| `matchesEveryConstructorSize()` | — | ✔ |
| `returnsDistinctInstances()` | — | ✔ |
| `allocatesZeroCapacity()` | — | ✔ |

**class `UIPlayerTest.PanelLineMethod`** — class Javadoc: ✔ · type stamp: on 260925

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `allocate(int)` | — | ✔ |
| `invoke(Object, ColourEnum, String, String, Object)` | — | ✔ |
| `readLen(Object)` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `readAttribute(Object)` | — | ✔ |
| `readLabel(Object)` | — | ✔ |
| `readValue(Object)` | — | ✔ |
| `appendsRowWithAttributeLabelAndFormattedValue()` | — | ✔ |
| `appendsSuccessiveRowsInOrder()` | — | ✔ |
| `storesNineteenCharacterValueUnchanged()` | — | ✔ |
| `truncatesValueLongerThanNineteenCharacters()` | — | ✔ |
| `throwsOncePanelIsFull()` | — | ✔ |
| `throwsImmediatelyOnZeroCapacityPanel()` | — | ✔ |

**class `UIPlayerTest.SpaceMethod`** — class Javadoc: ✔ · type stamp: on 260926

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `allocate(int)` | — | ✔ |
| `invokePanelLine(Object, ColourEnum, String, String, Object)` | — | ✔ |
| `invokeSpace(Object)` | — | ✔ |
| `readLen(Object)` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `readLabel(Object)` | — | ✔ |
| `readValue(Object)` | — | ✔ |
| `appendsBlankRowAndAdvancesLen()` | — | ✔ |
| `keepsLaterRowsInOwnSlotAfterSpacer()` | — | ✔ |
| `throwsOncePanelIsFull()` | — | ✔ |
| `throwsImmediatelyOnZeroCapacityPanel()` | — | ✔ |

**class `UIPlayerTest.Constructor`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (0/3 documented): ✘ `savedStatusView`, `savedCharSheetView`, `savedMaxLevel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `installFixture()` | — | ✔ |
| `restoreFixture()` | — | ✔ |
| `panels()` | — | ✔ |
| `bounds(Object)` | — | ✔ |
| `alignLeft(Object)` | — | ✔ |
| `panel(Object)` | — | ✔ |
| `readInt(Object, String)` | — | ✔ |
| `buildsFivePairings()` | — | ✔ |
| `matchesRegionsInTableOrder()` | — | ✔ |
| `alignsOnlyTopLeftFlushLeft()` | — | ✔ |
| `pairsEachRegionWithItsOwnBuilder()` | — | ✔ |

**class `UIPlayerTest.WeightRemaining`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (0/1 documented): ✘ `savedCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveCharSheetView()` | — | ✔ |
| `restoreCharSheetView()` | — | ✔ |
| `installView(int, int)` | — | ✔ |
| `invoke()` | — | ✔ |
| `computesTheOrdinaryFormula()` | — | ✔ |
| `returnsZeroAtBoundary()` | — | ✔ |
| `goesNegativeWhenOverburdened()` | — | ✔ |

**class `UIPlayerTest.MaxColour`** — class Javadoc: ✔ · type stamp: on 260925

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `invoke(int, int)` | — | ✔ |
| `returnsYellowWhenBelowMax()` | — | ✔ |
| `returnsLightGreenWhenEqualToMax()` | — | ✔ |
| `returnsLightGreenWhenAboveMax()` | — | ✔ |
| `comparesNegativeValues()` | — | ✔ |

**class `UIPlayerTest.MaxColourLong`** — class Javadoc: ✔ · type stamp: on 260925

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `invoke(long, long)` | — | ✔ |
| `returnsYellowWhenBelowMax()` | — | ✔ |
| `returnsLightGreenWhenEqualToMax()` | — | ✔ |
| `returnsLightGreenWhenAboveMax()` | — | ✔ |
| `comparesNegativeValues()` | — | ✔ |
| `comparesOutOfIntRangeValues()` | — | ✔ |

**class `UIPlayerTest.GetPanelTopLeft`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (0/8 documented): ✘ `savedCurrentHp`, `savedMaxHp`, `savedCurrentSp`, `savedMaxSp`, `savedTitle`, `savedClassName`, `savedRaceName`, `savedName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `installFixture()` | — | ✔ |
| `restoreFixture()` | — | ✔ |
| `invoke()` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `readAttribute(Object)` | — | ✔ |
| `readLabel(Object)` | — | ✔ |
| `readValue(Object)` | — | ✔ |
| `buildsSixRowsInOrder()` | — | ✔ |
| `allocatesSixRowCapacity()` | — | ✔ |

**class `UIPlayerTest.GetPanelMisc`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (0/2 documented): ✘ `savedStatusView`, `savedCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `installFixture()` | — | ✔ |
| `restoreFixture()` | — | ✔ |
| `invoke()` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `readAttribute(Object)` | — | ✔ |
| `readLabel(Object)` | — | ✔ |
| `readValue(Object)` | — | ✔ |
| `buildsSevenRowsInOrder()` | — | ✔ |
| `allocatesSevenRowCapacity()` | — | ✔ |

**class `UIPlayerTest.ShowAdvanceExperience`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (0/3 documented): ✘ `savedStatusView`, `savedCharSheetView`, `savedMaxLevel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveGlobalState()` | — | ✔ |
| `restoreGlobalState()` | — | ✔ |
| `statusViewWithLevel(int)` | — | ✔ |
| `charSheetViewWith(long[], int)` | — | ✔ |
| `invoke()` | — | ✔ |
| `returnsScaledFigureBelowCap()` | — | ✔ |
| `indexesAtLevelMinusOne()` | — | ✔ |
| `truncatesScalingDivision()` | — | ✔ |
| `computesFigureOneLevelBelowCap()` | — | ✔ |
| `returnsPlaceholderAtCap()` | — | ✔ |

**class `UIPlayerTest.GetPanelMidLeft`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (0/3 documented): ✘ `savedStatusView`, `savedCharSheetView`, `savedMaxLevel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `installFixture()` | — | ✔ |
| `restoreFixture()` | — | ✔ |
| `invoke()` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `readAttribute(Object)` | — | ✔ |
| `readLabel(Object)` | — | ✔ |
| `readValue(Object)` | — | ✔ |
| `buildsNineRowsInOrder()` | — | ✔ |
| `allocatesNineRowCapacity()` | — | ✔ |

**class `UIPlayerTest.GetPanelCombat`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (0/2 documented): ✘ `savedStatusView`, `savedCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `installFixture()` | — | ✔ |
| `restoreFixture()` | — | ✔ |
| `invoke()` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `readAttribute(Object)` | — | ✔ |
| `readLabel(Object)` | — | ✔ |
| `readValue(Object)` | — | ✔ |
| `buildsNineRowsInOrder()` | — | ✔ |
| `allocatesNineRowCapacity()` | — | ✔ |

**class `UIPlayerTest.GetPanelSkills`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (0/2 documented): ✘ `savedStatusView`, `savedCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveViews()` | — | ✔ |
| `restoreViews()` | — | ✔ |
| `installFixture(int, int, int, int, int, int, int, int, boolean, int)` | — | ✔ |
| `invoke()` | — | ✔ |
| `readLines(Object)` | — | ✔ |
| `readAttribute(Object)` | — | ✔ |
| `readLabel(Object)` | — | ✔ |
| `readValue(Object)` | — | ✔ |
| `buildsEightRowsInOrder()` | — | ✔ |
| `allocatesEightRowCapacity()` | — | ✔ |
| `clampsSkillsToUpperBound()` | — | ✔ |
| `floorsDisarmSkillsAtTwo()` | — | ✔ |
| `floorsSaveAndSearchAtZero()` | — | ✔ |
| `doesNotClampMagicDevices()` | — | ✔ |
| `coloursSpeedRowByThreshold()` | — | ✔ |

**class `UIPlayerTest.ShowSpeed`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (0/1 documented): ✘ `savedCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveCharSheetView()` | — | ✔ |
| `restoreCharSheetView()` | — | ✔ |
| `installView(int, boolean)` | — | ✔ |
| `invoke()` | — | ✔ |
| `returnsNormalAtOneHundredTen()` | — | ✔ |
| `formatsEffectiveSpeedOnBranch()` | — | ✔ |
| `formatsEffectiveSpeedOffBranch()` | — | ✔ |

**class `UIPlayerTest.ShowDepth`** — class Javadoc: ✔ · type stamp: on 260926

- Fields (0/1 documented): ✘ `savedStatusView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveStatusView()` | — | ✔ |
| `restoreStatusView()` | — | ✔ |
| `statusViewWithMaxDepth(int)` | — | ✔ |
| `invoke()` | — | ✔ |
| `returnsTownAtDepthZero()` | — | ✔ |
| `formatsShallowestDepth()` | — | ✔ |
| `formatsOrdinaryDepth()` | — | ✔ |

**class `UIPlayerTest.Likert`** — class Javadoc: ✔ · type stamp: on 260926

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `invoke(int, int)` | — | ✔ |
| `readString(Object)` | — | ✔ |
| `readAttr(Object)` | — | ✔ |
| `assertRating(int, String, ColourEnum)` | — | ✔ |
| `ratesNegativeSkillAsVeryBad()` | — | ✔ |
| `treatsNonPositiveLevelAsOne()` | — | ✔ |
| `bucketsEveryRatingBand()` | — | ✔ |
| `dividesWithIntegerTruncation()` | — | ✔ |


## frontend/ui/entry

##### `EmbryonicCategoryReferenciesTest.java`

**Responsible for:** Unit tests for EmbryonicCategoryReferencies, checked against C's struct embryonic_category_reference ([C] ui-entry.c:163-168: name, psource_index, priority, priority_set) and the sorted-by-name invariant insert_embryo_category ([C] ui-entry.c:1447-1561) maintains over an entry's in-progress categ...

**class `EmbryonicCategoryReferenciesTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withCategoryName(String)` | — | ✘ |
| `constructorStoresASchemeDerivedPriorityVerbatim()` | — | ✘ |
| `constructorStoresALiteralPriorityWithNoScheme()` | — | ✘ |
| `constructorStoresAnUnsetPriorityAsPassedRatherThanNormalisingItToZero()` | — | ✘ |
| `compareToOrdersByCategoryNameAscending()` | — | ✘ |
| `compareToReportsEqualCategoryNamesAsZero()` | — | ✘ |
| `sortingByThisOrderReproducesTheArrayInvariantInsertEmbryoCategoryMaintains()` | — | ✘ |


##### `UIEntryEmbryoTest.java`

**Responsible for:** Unit tests for UIEntryEmbryo, checked against C's struct embryonic_ui_entry ([C] ui-entry.c:170-177: entry, categories, param_index, psource_index, last_category_index, exists) and the field assignments parse_entry_name ([C] ui-entry.c:1882-1943) makes when it opens a new record.

**class `UIEntryEmbryoTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshEntry(String)` | — | ✘ |
| `constructorStoresAllSixFieldsVerbatim()` | — | ✘ |
| `aFreshEmbryoMatchesCsNewEntryDefaults()` | — | ✘ |
| `settersOverwriteEachFieldIndependently()` | — | ✘ |
| `addCategoriesAppendsToTheAccumulatedList()` | — | ✘ |


## frontend/ui/entry/assembler/helperfunctions

##### `HelperFunctionsTest.java`

**Responsible for:** Unit tests for HelperFunctions, checked against C's name_parameters[] and priority_schemes[] function-pointer tables ([C] ui-entry.c:129-161) and the nine lookup functions they name ([C] ui-entry.c:1564-1653).

**class `HelperFunctionsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dummyParamCountIsOne()` | — | ✘ |
| `dummyParamNameIsAlwaysEmptyRegardlessOfIndex()` | — | ✘ |
| `elementCountMatchesTheCCountUnshifted()` | — | ✘ |
| `elementNameAtTheFirstValidIndexIsAcidMatchingCsElementNames0()` | — | ✘ |
| `elementNameAtTheLastValidIndexIsArrowMatchingCsElementNames24()` | — | ✘ |
| `elementNameRejectsIndexZeroWhichWouldBeTheNonePlaceholder()` | — | ✘ |
| `elementNameRejectsIndexTwentySixWhichWouldBeElemMax()` | — | ✘ |
| `elementNameCoversEveryResistableElementInEnumDeclarationOrder()` | — | ✘ |
| `statCountIsFiveMatchingCsStatNames()` | — | ✘ |
| `statNameReturnsTheFiveStatsInCsListStatsOrder()` | — | ✘ |
| `statNameRejectsIndexBelowZero()` | — | ✘ |
| `statNameRejectsIndexFiveWhichIsOneBeyondCon()` | — | ✘ |
| `dummyPriorityIsAlwaysZeroRegardlessOfIndex()` | — | ✘ |
| `priorityFromIndexReturnsTheIndexUnchanged()` | — | ✘ |
| `priorityFromNegativeIndexReturnsTheNegatedIndex()` | — | ✘ |


##### `UIEntryNameParameterTest.java`

**Responsible for:** Tests UIEntryNameParameter, the port of C's name_parameters[] table ([C] ui-entry.c:142-146).

**class `UIEntryNameParameterTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `namesMatchTheirDataFileDirectiveText()` | — | ✘ |
| `noneExpandsToExactlyOneUnnamedValue()` | — | ✘ |
| `statExpandsToFivePlayerStatsInOrder()` | — | ✘ |
| `elementCountMatchesTheCCountUnshiftedDespiteTwoJavaOnlyPlaceholders()` | — | ✘ |


##### `UIEntryPrioritySchemeTest.java`

**Responsible for:** Tests UIEntryPriorityScheme, the port of C's priority_schemes[] table ([C] ui-entry.c:157-161).

**class `UIEntryPrioritySchemeTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `namesMatchTheirDataFileDirectiveText()` | — | ✘ |
| `noneAlwaysResolvesToZero()` | — | ✘ |
| `indexResolvesToTheIndexUnchanged()` | — | ✘ |
| `negativeIndexResolvesToTheNegatedIndex()` | — | ✘ |


## frontend/ui/globals

##### `UIDataLoaderTest.java`

**Responsible for:** Unit tests for the private finishing steps UIDataLoader still owns - finalPass and fillOutShortened - checked against the C original's whole-file finishing loop ([C] ui-entry.c:2283-2332).

**class `UIDataLoaderTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `finalPassMethod()` | — | ✘ |
| `fillOutShortenedMethod()` | — | ✘ |
| `finalPass(List<UIEntry>)` | — | ✘ |
| `fillOutShortened(UIEntry)` | — | ✘ |
| `entry(String, String, String, String, int, List<UIEntryCategory>)` | — | ✘ |
| `finalPassBackfillsAnUnsetCategoryPriorityFromTheEntrysDefault()` | — | ✔ |
| `finalPassLeavesAnAlreadySetCategoryPriorityUntouched()` | — | ✔ |
| `finalPassOnlyBackfillsTheCategoriesThatAreUnset()` | — | ✘ |
| `finalPassDefaultsAnEmptyLabelToTheEntrysName()` | — | ✔ |
| `fillOutShortenedCascadesFromTheNearestLongerAbbreviationOrTheFullLabel()` | — | ✔ |
| `finalPassKeepsAnExistingLabel()` | — | ✔ |
| `finalPassFillsTheLabelBeforeTheShortenedLabelsAreDerived()` | — | ✔ |
| `finalPassReturnsEveryEntryInOrderAsTheSameObjects()` | — | ✔ |
| `finalPassOfAnEmptyListIsEmpty()` | — | ✘ |
| `fillOutShortenedNeverPadsALabelShorterThanTheWidth()` | — | ✔ |
| `fillOutShortenedTakesHigherWidthsFromTheFullLabelNotFromAFilledLowerOne()` | — | ✔ |
| `fillOutShortenedTreatsAnEmptyStringAsUnset()` | — | ✔ |
| `fillOutShortenedLeavesAnAlreadySetWidthUntouched()` | — | ✘ |


##### `UIRegistryTest.java`

**Responsible for:** Tests UIRegistry's name-based lookups, the port of C's ui_entry_lookup/ ui_entry_search ([C] ui-entry.c:1165-1208) as applied across the registry's three loaded lists.

**class `UIRegistryTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `renderer(String)` | — | ✘ |
| `base(String)` | — | ✘ |
| `entry(String)` | — | ✘ |
| `seedRegistry()` | — | ✘ |

**class `UIRegistryTest.GetUIEntryRenderer`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `findsALoadedRenderer()` | — | ✘ |
| `reportsAnUnknownName()` | — | ✘ |

**class `UIRegistryTest.GetUIEntryBase`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `findsALoadedBase()` | — | ✘ |
| `returnsNullForAnUnknownName()` | — | ✘ |

**class `UIRegistryTest.GetUIEntry`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `findsALoadedEntry()` | — | ✘ |
| `returnsNullForAnUnknownName()` | — | ✘ |

**class `UIRegistryTest.LookupEdges`** — class Javadoc: ✔ · type stamp: on 260929

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchIsCaseSensitive()` | — | ✘ |
| `matchIsWholeString()` | — | ✘ |
| `picksTheRightRecord()` | — | ✘ |
| `emptyRegistryReturnsNull()` | — | ✘ |
| `unloadedEntriesThrow()` | — | ✘ |
| `unloadedBasesThrow()` | — | ✘ |
| `unloadedRenderersReportThroughErrors()` | — | ✘ |

**class `UIRegistryTest.Constants`** — class Javadoc: ✔ · type stamp: on 260929

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `maxEntryLabel()` | — | ✘ |
| `maxShortened()` | — | ✘ |
| `maxPalette()` | — | ✘ |

**class `UIRegistryTest.StatNames`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (2/2 documented): ✔ `C_STAT_NAMES`, `C_STAT_NAMES_REDUCED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `statNamesMatchesC()` | — | ✘ |
| `statReducedNamesMatchesC()` | — | ✘ |
| `bothArraysAreSizedToStatMax()` | — | ✘ |
| `everyLabelIsFiveCharactersWide()` | — | ✘ |


## frontend/ui/output

##### `RegionTest.java`

**Responsible for:** Tests for Region, the port of C's struct region and SCREEN_REGION in ui-output.h.

**class `RegionTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `screenRegionIsAllZeros()` | — | ✘ |
| `screenRegionReturnsFreshCopyEachCall()` | — | ✘ |
| `mutatingOneScreenRegionDoesNotAffectLaterOnes()` | — | ✘ |
| `constructorStoresFieldsInDeclarationOrder()` | — | ✘ |
| `constructorStoresRelativeAndDefaultValuesRaw()` | — | ✘ |
| `settersChangeOnlyTheirOwnField()` | — | ✘ |
| `settersStoreNegativeValuesRaw()` | — | ✘ |


## frontend/ui/player

##### `CharSheetConfigTest.java`

**Responsible for:** Tests for CharSheetConfig, the Java form of C's struct char_sheet_config (ui-player.c).

**class `CharSheetConfigTest`** — class Javadoc: ✔ · type stamp: on 260929

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constructorPopulatesRegionsAndMaps()` | — | ✔ |
| `regionsAreDistinctObjects()` | — | ✔ |
| `layoutArithmeticMatchesC()` | — | ✔ |
| `resistCountsAreIndependentPerRegion()` | — | ✔ |
| `resistsByRegionStoreAndRetrieve()` | — | ✔ |
| `storedResistLabelIsMutableThroughConfig()` | — | ✔ |
| `statModEntriesAllocateAndFill()` | — | ✔ |
| `statModEntriesZeroLength()` | — | ✔ |
| `setStatModEntryIgnoresOutOfRangeIndex()` | — | ✔ |
| `setStatModEntryBeforeInitThrows()` | — | ✔ |
| `initStatModEntriesReplacesArray()` | — | ✔ |
| `setResRegionReplacesReference()` | — | ✔ |
| `instancesDoNotShareState()` | — | ✔ |


##### `CharSheetResistTest.java`

**Responsible for:** Tests for CharSheetResist, the Java form of C's struct char_sheet_resist (ui-player.c).

**class `CharSheetResistTest`** — class Javadoc: ✔ · type stamp: on 260929

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `makeEntry()` | — | ✘ |
| `constructorKeepsEntryAndEmptyLabel()` | — | ✔ |
| `nullEntryIsAccepted()` | — | ✔ |
| `labelRoundTripsVerbatim()` | — | ✔ |
| `setLabelDoesNotPadOrTruncate()` | — | ✔ |
| `setLabelReplacesPreviousValue()` | — | ✔ |
| `setLabelDoesNotTouchEntry()` | — | ✔ |
| `instancesAreIndependent()` | — | ✔ |
| `hasExactlyTheTwoStructMembers()` | — | ✔ |


## middle

##### `ActivationIndexTest.java`

**Responsible for:** Tests the activation index: Activation#setIndex(int) and the numbering ActivationAssembler does with it.

**class `ActivationIndexTest`** — class Javadoc: ✔ · type stamp: on 261013

- Fields (0/1 documented): ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `indexOf(Activation)` | — | ✔ |
| `rec(String, String, int)` | — | ✔ |
| `setIndexStoresTheValue()` | — | ✘ |
| `copyKeepsTheIndex()` | — | ✘ |
| `indexEqualsSlotOnTheRealFile()` | — | ✘ |
| `droppedRecordLeavesNoGap()` | — | ✘ |
| `emptyInputGivesEmptyList()` | — | ✘ |
| `realFileIsClean()` | — | ✘ |


##### `AllocEntryTest.java`

**Responsible for:** Tests for AllocEntry, the port of struct alloc_entry in the C original's alloc.h.

**class `AllocEntryTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshEntryIsZeroFilled()` | — | ✔ |
| `fieldsDoNotAlias()` | — | ✔ |
| `overwriteOneFieldOnly()` | — | ✔ |
| `everyFieldRoundTripsIntValues(int)` | — | ✔ |


##### `MessageAddTest.java`

**Responsible for:** Unit tests for Message#messageAdd(String, MessageType), the port of C's message_add (message.c).

**class `MessageAddTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `realBus`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `theFirstMessageIntoAnEmptyLogIsAnEntryWithCountOne()` | — | ✘ |
| `loggingIsSilent()` | — | ✔ |
| `repeatsCoalesceIntoOneEntry()` | — | ✘ |
| `aDifferentTypeStartsAFreshEntry()` | — | ✘ |
| `differentTextUnderTheSameTypeStartsAFreshEntry()` | — | ✘ |
| `onlyTheNewestEntryIsCompared()` | — | ✔ |
| `textIsStoredExactlyAsGiven()` | — | ✘ |
| `messageAddDoesNotTruncate()` | — | ✔ |
| `theLogFillsToTheCapWithoutLosingAnything()` | — | ✘ |
| `theEntryAfterTheCapDropsTheOldest()` | — | ✔ |
| `aRepeatAtFullCapacityEvictsNothing()` | — | ✔ |
| `theLogNeverGrowsPastTheCap()` | — | ✘ |


##### `MessageLogProbe.java`

**Responsible for:** Test-side window onto Message's private log, shared by MessageTest and MessageAddTest.

**class `MessageLogProbe`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `MessageLogProbe()` | — | ✘ |
| `log()` | — | ✘ |
| `clear()` | — | ✔ |
| `size()` | — | ✔ |
| `cap()` | — | ✔ |
| `entry(int)` | — | ✘ |
| `call(int, String)` | — | ✘ |
| `text(int)` | — | ✔ |
| `count(int)` | — | ✔ |
| `type(int)` | — | ✔ |
| `playerWithSound(boolean)` | — | ✔ |

**class `MessageLogProbe.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `types`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `payload(int)` | — | ✘ |
| `last()` | — | ✘ |


##### `MessageTest.java`

**Responsible for:** Unit tests for Message#message and Message#messageType, the ports of C's msg and msgt (message.c).

**class `MessageTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `realBus`, `realPlayer`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `playerWithSound(boolean)` | — | ✘ |
| `aPlainMessageIsLoggedAndSignalledAsGeneric()` | — | ✘ |
| `formatArgumentsAreSubstituted()` | — | ✘ |
| `textPassedAsAnArgumentSurvivesAStrayPercentSign()` | — | ✘ |
| `aPlainMessageNeverMakesASound()` | — | ✔ |
| `aTypedMessageKeepsItsTypeInTheLogAndTheEvent()` | — | ✘ |
| `aTypedMessageSubstitutesFormatArguments()` | — | ✘ |
| `aTypedMessageSoundsBeforeItIsSignalledWhenSoundIsOn()` | — | ✔ |
| `aTypedMessageIsSilentWhenSoundIsOff()` | — | ✘ |
| `aTypedMessageStillGoesOutWithNoPlayer()` | — | ✔ |
| `aBurstCoalescesInTheLogButEveryCallSignalsPlainText()` | — | ✔ |
| `theSameTextUnderADifferentTypeDoesNotCoalesce()` | — | ✘ |
| `aRecurrenceDoesNotCoalesceWithAnEarlierEntry()` | — | ✔ |
| `anInterruptedRunStartsCountingAgain()` | — | ✘ |
| `msgAndMsgtGenericShareAnEntry()` | — | ✔ |
| `doublePercentIsALiteralPercent()` | — | ✘ |
| `integerAndCharacterDirectivesMatchC()` | — | ✔ |
| `widthFlagsAndPrecisionMatchC()` | — | ✘ |
| `floatingPointDirectivesMatchC()` | — | ✘ |
| `anUnterminatedPercentIsLoggedAndSignalledAsAnEmptyMessage()` | — | ✔ |
| `anIllegalConversionIsAnEmptyMessage()` | — | ✔ |
| `aMissingArgumentIsAnEmptyMessage()` | — | ✔ |
| `aMalformedTypedMessageStillSoundsThenSignalsEmpty()` | — | ✔ |
| `consecutiveEmptyMessagesCoalesce()` | — | ✔ |
| `directivesOnlyCAcceptsBecomeEmptyMessages()` | — | ✔ |
| `textOf1023CharactersIsUntouched()` | — | ✘ |
| `textOf1024CharactersIsCutTo1023()` | — | ✔ |
| `aTypedMessageIsCutTheSameWayInLogAndEvent()` | — | ✘ |
| `linesThatDifferOnlyPastTheCutCoalesce()` | — | ✔ |


## middle/cave

##### `ChunkAddLightTest.java`

**Responsible for:** Tests Chunk.addLight, the port of C's add_light (cave-view.c).

**class `ChunkAddLightTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (5/5 documented): ✔ `WIDTH`, `HEIGHT`, `SOURCE`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `newLevel()` | — | ✔ |
| `open(Loc)` | — | ✔ |
| `wall(Loc)` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `addLight(Loc, int, int)` | — | ✔ |
| `light(Loc)` | — | ✔ |

**class `ChunkAddLightTest.OpenGround`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `contributionFallsOffWithDistance()` | — | ✔ |
| `cornersOfTheSweptSquareAreDark()` | — | ✔ |
| `gridsOutsideTheSweptSquareAreUntouched()` | — | ✔ |
| `contributionIsAdded()` | — | ✔ |
| `negativeIntensityDarkensWeakeningOutwards()` | — | ✔ |
| `negativeRadiusIsANoOp()` | — | ✔ |
| `sourceInCornerIsClippedToTheLevel()` | — | ✔ |

**class `ChunkAddLightTest.Walls`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lightDoesNotPassThroughAWall()` | — | ✔ |
| `wallLitOnAnUnseenFaceIsSkipped()` | — | ✔ |


##### `ChunkBecomeViewableTest.java`

**Responsible for:** Tests Chunk.becomeViewable, the port of C's become_viewable (cave-view.c).

**class `ChunkBecomeViewableTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (7/7 documented): ✔ `WIDTH`, `HEIGHT`, `PLAYER`, `TARGET`, `APPROACH`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `newLevel()` | — | ✔ |
| `open(Loc)` | — | ✔ |
| `wall(Loc)` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `becomeViewable(Loc, boolean)` | — | ✔ |
| `light(Loc, int)` | — | ✔ |
| `has(Loc, SquareEnum)` | — | ✔ |

**class `ChunkBecomeViewableTest.UnlitAndFar`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `viewIsSet()` | — | ✔ |
| `seenAndClosePlayerAreNotSet()` | — | ✔ |
| `neighbourIsUntouched()` | — | ✔ |

**class `ChunkBecomeViewableTest.Close`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `allThreeAreSet()` | — | ✔ |
| `unlitGridIsStillSeen()` | — | ✔ |
| `closeWallIsSeen()` | — | ✔ |

**class `ChunkBecomeViewableTest.Lit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `litFloorIsSeen()` | — | ✔ |
| `zeroLightIsDark()` | — | ✔ |
| `negativeLightIsDark()` | — | ✔ |
| `glowWithoutLightIsDark()` | — | ✔ |
| `lightWithoutGlowIsSeen()` | — | ✔ |

**class `ChunkBecomeViewableTest.LitWall`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `litApproachIsSeen()` | — | ✔ |
| `darkApproachIsUnseen()` | — | ✔ |
| `darkWallIsUnseen()` | — | ✔ |
| `bothCoordinatesStep()` | — | ✔ |
| `orthogonalNeighboursAreNotConsulted()` | — | ✔ |
| `sharedCoordinateDoesNotStep()` | — | ✔ |
| `wallOnPlayerGridConsultsItself()` | — | ✔ |

**class `ChunkBecomeViewableTest.AlreadyInView`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `notUpgradedToSeen()` | — | ✔ |
| `notUpgradedByLight()` | — | ✔ |
| `preMarkedGridIsUntouched()` | — | ✔ |


##### `ChunkCalcLightingTest.java`

**Responsible for:** Tests Chunk.calcLighting, the port of C's calc_lighting (cave-view.c).

**class `ChunkCalcLightingTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (7/7 documented): ✔ `WIDTH`, `HEIGHT`, `PLAYER`, `SUBJECT`, `savedConstants`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `raceWithLight(int)` | — | ✔ |
| `placeMonster(Chunk, int, Loc, int, MonsterFlag)` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `newLevel()` | — | ✔ |
| `newChunk(int, int)` | — | ✔ |
| `wall(Loc)` | — | ✔ |
| `bright(Loc)` | — | ✔ |
| `brightWall(Loc)` | — | ✔ |
| `glow(Loc)` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `setPlayerLight(int)` | — | ✔ |
| `calcLighting()` | — | ✔ |
| `calcLighting(Chunk)` | — | ✔ |
| `light(Loc)` | — | ✔ |

**class `ChunkCalcLightingTest.GlowBase`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `darkLevelStaysDark()` | — | ✔ |
| `glowingOpenGrid()` | — | ✔ |
| `baseOverwritesPreviousLevel()` | — | ✔ |
| `glowingWallWithNoIlluminationStaysDark()` | — | ✔ |
| `glowingWallUnderThePlayerIsLit()` | — | ✔ |
| `glowingWallLitFromTowardsThePlayer()` | — | ✔ |

**class `ChunkCalcLightingTest.BrightTerrain`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `brightNotGlowing()` | — | ✔ |
| `brightAndGlowing()` | — | ✔ |
| `neighboursTakeASpill()` | — | ✔ |
| `spillOntoLaterGridsIsOverwritten()` | — | ✔ |
| `spillDoesNotReachASecondRing()` | — | ✔ |
| `spillAccumulatesOntoGlow()` | — | ✔ |
| `wallNeighbourFacingAwayIsNotBrightened()` | — | ✔ |
| `wallNeighbourFacingThePlayerIsBrightened()` | — | ✔ |
| `brightWallStillScoresTwo()` | — | ✔ |
| `brightGridOnTheBorder()` | — | ✔ |

**class `ChunkCalcLightingTest.TwoBrightGrids`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `spillSurvivesOnlyBackwards()` | — | ✔ |
| `gridAboveTakesBothSpills()` | — | ✔ |
| `gridBelowKeepsNeitherSpill()` | — | ✔ |

**class `ChunkCalcLightingTest.PlayerLight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lightOfIntensityTwo()` | — | ✔ |
| `lightOfIntensityOne()` | — | ✔ |
| `noLightAddsNothing()` | — | ✔ |
| `lightAccumulatesOntoGlow()` | — | ✔ |
| `negativeLightDarkens()` | — | ✔ |
| `lightDoesNotLeakPastAWall()` | — | ✔ |

**class `ChunkCalcLightingTest.MonsterLight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `litMonsterLightsItsSurroundings()` | — | ✔ |
| `monsterWithNoLightContributesNothing()` | — | ✔ |
| `camouflagedMonsterContributesNothing()` | — | ✔ |
| `emptySlotsArePassedOver()` | — | ✔ |
| `darkeningMonsterLowersTheLight()` | — | ✔ |
| `severalSourcesAddUp()` | — | ✔ |
| `monsterJustInsideTheCutOff()` | — | ✔ |
| `monsterJustOutsideTheCutOff()` | — | ✔ |

**class `ChunkCalcLightingTest.LightIndicator`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unchangedLevelLeavesFlagDown()` | — | ✔ |
| `changedLevelRaisesFlag()` | — | ✔ |
| `unchangedNonZeroLevelLeavesFlagDown()` | — | ✔ |
| `droppingLevelRaisesFlag()` | — | ✔ |


##### `ChunkFindDecoyTest.java`

**Responsible for:** Tests Chunk#caveFindDecoy(), the port of C's cave_find_decoy() (cave.c), which is return c->decoy;.

**class `ChunkFindDecoyTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (1/1 documented): ✔ `chunk`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newChunk()` | — | ✔ |
| `placeDecoy(Loc)` | — | ✔ |
| `freshChunkHasNoDecoy()` | — | ✔ |
| `placedDecoyIsReturned()` | — | ✔ |
| `repeatCallsAgree()` | — | ✔ |
| `oneZeroCoordinateIsStillADecoy()` | — | ✔ |


##### `ChunkGeometryTest.java`

**Responsible for:** Tests Chunk's geometry, square lookup and the two heat maps — the port of the bounds and accessor macros around C's struct chunk (cave.h).

**class `ChunkGeometryTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `WIDTH`, `HEIGHT`, `chunk`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newChunk()` | — | ✔ |

**class `ChunkGeometryTest.Bounds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `middleIsInsideBoth()` | — | ✔ |
| `edgeIsInsideButNotFully()` | — | ✔ |
| `beyondEdgeIsOutside()` | — | ✔ |

**class `ChunkGeometryTest.Squares`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyGridHasASquare()` | — | ✔ |
| `offLevelAnswersNull()` | — | ✔ |
| `lookupIsStable()` | — | ✔ |
| `predicatesAreGuarded()` | — | ✔ |
| `predicatesReadTheSquare()` | — | ✔ |

**class `ChunkGeometryTest.Dimensions`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dimensionsAreNotCrossed()` | — | ✔ |
| `monsterArraySized()` | — | ✔ |
| `objectListIsReadOnly()` | — | ✔ |

**class `ChunkGeometryTest.HeatMaps`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `mapsAreSeparate()` | — | ✔ |
| `resetZeroesTheInterior()` | — | ✔ |
| `ageingOnlyAffectsTrails()` | — | ✔ |
| `boundaryIsSkipped()` | — | ✔ |


##### `ChunkGlowCanLightWallTest.java`

**Responsible for:** Tests Chunk.glowCanLightWall, the port of C's glow_can_light_wall (cave-view.c).

**class `ChunkGlowCanLightWallTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (5/5 documented): ✔ `WIDTH`, `HEIGHT`, `WALL`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `newLevel()` | — | ✔ |
| `open(Loc)` | — | ✔ |
| `wall(Loc)` | — | ✔ |
| `glow(Loc)` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `glowCanLight(Loc)` | — | ✔ |

**class `ChunkGlowCanLightWallTest.Coincident`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerInWallAlwaysSees()` | — | ✔ |

**class `ChunkGlowCanLightWallTest.TowardsPlayer`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `openGlowingStepLights()` | — | ✔ |
| `openGlowingDiagonalStepLights()` | — | ✔ |
| `glowingWallStepDoesNotLight()` | — | ✔ |

**class `ChunkGlowCanLightWallTest.DiagonalFlankers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rowFlankerLights()` | — | ✔ |
| `columnFlankerLights()` | — | ✔ |
| `distantGlowDoesNotLight()` | — | ✔ |
| `nothingGlowingIsFalse()` | — | ✔ |

**class `ChunkGlowCanLightWallTest.StraightFlankers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flankerAboveLights()` | — | ✔ |
| `flankerBelowLights()` | — | ✔ |
| `blockedFaceIsNotLit()` | — | ✔ |
| `verticalFlankerLights()` | — | ✔ |
| `noFlankerGlowingIsFalse()` | — | ✔ |

**class `ChunkGlowCanLightWallTest.Edges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `outOfBoundsFlankerSkipped()` | — | ✔ |
| `outOfBoundsFlankerIsNotLight()` | — | ✔ |
| `outOfBoundsVerticalFlankerSkipped()` | — | ✔ |


##### `ChunkMarkWasSeenTest.java`

**Responsible for:** Tests Chunk.markWasSeen, the port of C's mark_wasseen (cave-view.c).

**class `ChunkMarkWasSeenTest`** — class Javadoc: ✔ · type stamp: on 260827

- Fields (8/8 documented): ✔ `WIDTH`, `HEIGHT`, `SEEN`, `UNSEEN`, `PLAYER`, `savedConstants`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `newLevel()` | — | ✔ |
| `markWasSeen()` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `setFlag(Loc, SquareEnum)` | — | ✔ |
| `hasFlag(Loc, SquareEnum)` | — | ✔ |

**class `ChunkMarkWasSeenTest.LitPlayer`** — class Javadoc: ✔ · type stamp: on 260827

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getStateLight()` | — | ✔ |
| `hasPlayerFlag(PlayerFlag)` | — | ✔ |

**class `ChunkMarkWasSeenTest.Snapshot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seenGridIsRecorded()` | — | ✔ |
| `unseenGridIsNotRecorded()` | — | ✔ |
| `viewIsNotTheCondition()` | — | ✔ |
| `cornersAreSwept()` | — | ✔ |
| `snapshotIsNotTransposed()` | — | ✔ |

**class `ChunkMarkWasSeenTest.Wipe`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seenGridIsWiped()` | — | ✔ |
| `unseenGridIsWipedToo()` | — | ✔ |
| `otherFlagsSurvive()` | — | ✔ |
| `nothingSeenRecordsNothing()` | — | ✔ |

**class `ChunkMarkWasSeenTest.ThroughUpdateView`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wasSeenDoesNotSurviveThePass()` | — | ✔ |


##### `ChunkMemberAccessTest.java`

**Responsible for:** Tests the small Chunk accessors ported from fields of C's struct chunk (cave.h): width, height, mon_cnt, mon_current and feeling.

**class `ChunkMemberAccessTest`** — class Javadoc: ✔ · type stamp: on 260929

- Fields (4/4 documented): ✔ `WIDTH`, `HEIGHT`, `MON_MAX`, `chunk`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `monster()` | — | ✔ |
| `newChunk()` | — | ✔ |

**class `ChunkMemberAccessTest.Dimensions`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dimensions()` | — | ✔ |

**class `ChunkMemberAccessTest.MonsterCount`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyChunk()` | — | ✔ |
| `occupiedSlotsCount()` | — | ✔ |
| `clearedSlotFalls()` | — | ✔ |

**class `ChunkMemberAccessTest.MonCurrent`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nobodyActing()` | — | ✔ |
| `someoneActing()` | — | ✔ |

**class `ChunkMemberAccessTest.Feeling`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doesNotThrow()` | — | ✔ |

**class `ChunkMemberAccessTest.ConstructionAndLookup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newChunkSquaresAreZeroed()` | — | ✔ |
| `caveMonsterBoundaries()` | — | ✔ |


##### `ChunkNoLightTest.java`

**Responsible for:** Tests Chunk.noLight, the port of C's no_light (cave-view.c).

**class `ChunkNoLightTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (7/7 documented): ✔ `WIDTH`, `HEIGHT`, `SEEN`, `DARK`, `OUTSIDE`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newLevel()` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `markSeen(Loc)` | — | ✔ |
| `seenGridIsNotDark()` | — | ✔ |
| `unseenGridIsDark()` | — | ✔ |
| `seenGridElsewhereIsIgnored()` | — | ✔ |
| `answerFollowsThePlayer()` | — | ✔ |
| `outOfBoundsGridIsDark()` | — | ✔ |


##### `ChunkObjectDisposalTest.java`

**Responsible for:** Tests Chunk's object disposal — objectDelete, delistObject and squareExciseObject, the port of C's object_delete, delist_object and square_excise_object (obj-pile.c, cave.c).

**class `ChunkObjectDisposalTest`** — class Javadoc: ✔ · type stamp: —

- Fields (5/5 documented): ✔ `level`, `known`, `player`, `savedCave`, `savedPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `list(Chunk, ItemObject)` | — | ✔ |
| `knownObject()` | — | ✔ |
| `wireCaves()` | — | ✔ |
| `restoreGame()` | — | ✔ |
| `currentLevelIsRecorded()` | — | ✔ |

**class `ChunkObjectDisposalTest.Delete`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rememberedObjectIsOrphaned()` | — | ✔ |
| `unrememberedObjectIsDeleted()` | — | ✔ |
| `noRememberedLevelDeletes()` | — | ✔ |
| `orphanTestPrecedesRemoval()` | — | ✔ |
| `deletingTrackedObjectClearsTracking()` | — | ✔ |
| `deletedObjectLeavesItsPile()` | — | ✔ |
| `orphanedObjectLeavesItsPile()` | — | ✔ |
| `objectInOtherPileLeavesIt()` | — | ✔ |
| `objectInNoPileIsDeleted()` | — | ✔ |
| `deletingOtherObjectKeepsTracking()` | — | ✔ |

**class `ChunkObjectDisposalTest.Delist`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unlistedIsIgnored()` | — | ✔ |
| `listedIsRemoved()` | — | ✔ |
| `levelKeepsRememberedObject()` | — | ✔ |

**class `ChunkObjectDisposalTest.Excise`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `objectLeavesThePile()` | — | ✔ |
| `onlyThatSquareIsTouched()` | — | ✔ |
| `offLevelGridIsRefused()` | — | ✔ |


##### `ChunkSourceCanLightWallTest.java`

**Responsible for:** Tests Chunk.sourceCanLightWall, the port of C's source_can_light_wall (cave-view.c).

**class `ChunkSourceCanLightWallTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (5/5 documented): ✔ `WIDTH`, `HEIGHT`, `WALL`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `newLevel()` | — | ✔ |
| `open(Loc)` | — | ✔ |
| `wall(Loc)` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `canLight(Loc, Loc)` | — | ✔ |

**class `ChunkSourceCanLightWallTest.Coincident`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sourceInWallAlwaysLights()` | — | ✔ |
| `playerInWallAlwaysSees()` | — | ✔ |

**class `ChunkSourceCanLightWallTest.Bearings`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sameBearingLights()` | — | ✔ |
| `rangeAndObstructionAreNotConsulted()` | — | ✔ |
| `oppositeSidesDoNotLight()` | — | ✔ |
| `perpendicularBearingsDoNotLight()` | — | ✔ |

**class `ChunkSourceCanLightWallTest.BlockedFace`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sharedColumnOpenLights()` | — | ✔ |
| `sharedColumnBlockedDoesNotLight()` | — | ✔ |
| `sharedRowOpenLights()` | — | ✔ |
| `sharedRowBlockedDoesNotLight()` | — | ✔ |
| `sharedColumnIgnoresTheOtherAxis()` | — | ✔ |
| `sharedRowIgnoresTheOtherAxis()` | — | ✔ |
| `theGridBesideIsNotTransposed()` | — | ✔ |

**class `ChunkSourceCanLightWallTest.OutOfBounds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `offLevelGridDoesNotThrow()` | — | ✔ |


##### `ChunkSquareForgetTest.java`

**Responsible for:** Tests Chunk.squareForget, the port of C's square_forget (cave-square.c).

**class `ChunkSquareForgetTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (11/11 documented): ✔ `WIDTH`, `HEIGHT`, `GRID`, `OTHER`, `none`, `granite`, `floor`, `savedFeatures`, `level`, `memory`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedTerrain()` | — | ✔ |
| `restoreTerrain()` | — | ✔ |
| `currentFeatures()` | — | ✔ |
| `feature(TerrainFlags, String, TerrainFeatureFlags)` | — | ✔ |
| `newLevel()` | — | ✔ |
| `clearCave()` | — | ✔ |
| `lay(Chunk, Feature)` | — | ✔ |
| `forget(Chunk, Loc)` | — | ✔ |
| `featureAt(Chunk, Loc)` | — | ✔ |

**class `ChunkSquareForgetTest.TheWrite`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `memoryIsCleared()` | — | ✔ |
| `levelIsUntouched()` | — | ✔ |
| `neighboursAreUntouched()` | — | ✔ |
| `writeIsNotTransposed()` | — | ✔ |
| `forgettingIsIdempotent()` | — | ✔ |
| `anyTerrainIsForgotten()` | — | ✔ |

**class `ChunkSquareForgetTest.LiveLevelGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `memoryChunkDoesNothing()` | — | ✔ |
| `otherLevelDoesNothing()` | — | ✔ |
| `noLiveLevelDoesNothing()` | — | ✔ |


##### `ChunkSquareIsFeelTest.java`

**Responsible for:** Tests Chunk.squareIsFeel, the port of C's square_isfeel (cave-square.c).

**class `ChunkSquareIsFeelTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (7/7 documented): ✔ `WIDTH`, `HEIGHT`, `TRIGGER`, `PLAIN`, `FIRST`, `LAST`, `level`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newLevel()` | — | ✔ |
| `squareIsFeel(Loc)` | — | ✔ |
| `markFeel(Loc)` | — | ✔ |
| `markedGridIsFeel()` | — | ✔ |
| `unmarkedGridIsNotFeel()` | — | ✔ |
| `triggerElsewhereIsIgnored()` | — | ✔ |
| `otherFlagsDoNotTrigger()` | — | ✔ |
| `feelSurvivesOtherFlags()` | — | ✔ |
| `readingDoesNotClearTheFlag()` | — | ✔ |
| `cornerGridsAreInBounds()` | — | ✔ |
| `outOfBoundsGridsAreNotFeel()` | — | ✔ |


##### `ChunkTrapsAndStubsTest.java`

**Responsible for:** Tests Chunk#squareLightSpot, Chunk#decreaseTrapTimeout and the level-generation stubs beside them.

**class `ChunkTrapsAndStubsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (6/6 documented): ✔ `level`, `player`, `bus`, `realBus`, `savedPlayer`, `savedCave`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `liveRace()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `setUp()` | — | ✔ |

**class `ChunkTrapsAndStubsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `types`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `ChunkTrapsAndStubsTest.LightSpot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `gridOnLevelIsSignalled()` | — | ✔ |
| `gridOffLevelIsIgnored()` | — | ✔ |
| `eachGridSignalsSeparately()` | — | ✔ |

**class `ChunkTrapsAndStubsTest.TrapTimeout`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noTrapsSignalsNothing()` | — | ✔ |
| `sweepCoversTheBoundary()` | — | ✔ |

**class `ChunkTrapsAndStubsTest.Stubs`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `illuminateIsAStub()` | — | ✔ |
| `memorizeTrapsIsAStub()` | — | ✔ |
| `indexMoveIsAStub()` | — | ✔ |
| `distantMonsterIsAStub()` | — | ✔ |
| `displayFeelingIsAStub()` | — | ✔ |

**class `ChunkTrapsAndStubsTest.Compaction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anEmptySlotDropsTheMark()` | — | ✔ |
| `slotZeroIsNeverExcised()` | — | ✔ |
| `aLiveMonsterIsSkipped()` | — | ✔ |


##### `ChunkUpdateOneTest.java`

**Responsible for:** Tests Chunk.updateOne, the port of C's update_one (cave-view.c).

**class `ChunkUpdateOneTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (11/11 documented): ✔ `WIDTH`, `HEIGHT`, `GRID`, `FEELING_NEED`, `savedConstants`, `level`, `player`, `bus`, `realBus`, `savedPlayer`, `savedCave`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedConstants()` | — | ✔ |
| `setStatic(String, GameConstantsData)` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `updateOne(Loc)` | — | ✔ |
| `seenAndWasSeen(Loc, boolean, boolean)` | — | ✔ |
| `blind(int)` | — | ✔ |
| `onlyPartial(boolean)` | — | ✔ |
| `feelingSquaresField()` | — | ✔ |
| `feelingSquares()` | — | ✔ |
| `feelingSquares(int)` | — | ✔ |
| `hasInfo(Loc, SquareEnum)` | — | ✔ |
| `redrewMap()` | — | ✔ |
| `feelingRedrawRaised()` | — | ✔ |

**class `ChunkUpdateOneTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `types`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `ChunkUpdateOneTest.Blindness`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearsSeenAndClose()` | — | ✔ |
| `clearedGridRedraws()` | — | ✔ |
| `noCrossingWhileBlind()` | — | ✔ |

**class `ChunkUpdateOneTest.Crossings`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unseenToSeen()` | — | ✔ |
| `seenToUnseen()` | — | ✔ |
| `seenThroughout()` | — | ✔ |
| `unseenThroughout()` | — | ✔ |

**class `ChunkUpdateOneTest.Feeling`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `markedAndCrossing()` | — | ✔ |
| `countsAndClears()` | — | ✔ |
| `notCountedWithoutCrossing()` | — | ✔ |
| `unmarkedNotCounted()` | — | ✔ |
| `announcesAtThreshold()` | — | ✔ |
| `silentBelowThreshold()` | — | ✔ |
| `silentAboveThreshold()` | — | ✔ |
| `onlyPartialSuppresses()` | — | ✔ |

**class `ChunkUpdateOneTest.WasSeen`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `alwaysCleared()` | — | ✔ |


##### `ChunkUpdateViewOneTest.java`

**Responsible for:** Tests Chunk.updateViewOne, the port of C's update_view_one (cave-view.c).

**class `ChunkUpdateViewOneTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (6/6 documented): ✔ `WIDTH`, `HEIGHT`, `PLAYER`, `savedConstants`, `level`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `newLevel()` | — | ✔ |
| `open(Loc)` | — | ✔ |
| `wall(Loc)` | — | ✔ |
| `wallRow(int)` | — | ✔ |
| `set(Class<?>, String, Object)` | — | ✔ |
| `placePlayer(Loc)` | — | ✔ |
| `updateViewOne(Loc)` | — | ✔ |
| `has(Loc, SquareEnum)` | — | ✔ |
| `assertInViewOnly(Loc)` | — | ✔ |
| `assertClose(Loc)` | — | ✔ |
| `assertDropped(Loc)` | — | ✔ |

**class `ChunkUpdateViewOneTest.OpenGround`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `farGridIsInViewOnly()` | — | ✔ |
| `nearGridIsClose()` | — | ✔ |
| `gridAtLightRadiusIsNotClose()` | — | ✔ |
| `playersOwnGridIsClose()` | — | ✔ |

**class `ChunkUpdateViewOneTest.SightRange`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `gridAtMaxSightIsKept()` | — | ✔ |
| `gridPastMaxSightIsDropped()` | — | ✔ |

**class `ChunkUpdateViewOneTest.Blocked`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `openGridBehindWallIsDropped()` | — | ✔ |
| `blockingWallIsStillInView()` | — | ✔ |

**class `ChunkUpdateViewOneTest.WallLighting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `corridorWallIsInView()` | — | ✔ |
| `farSideOfDoubleWallIsDropped()` | — | ✔ |
| `nearSideOfDoubleWallIsInView()` | — | ✔ |
| `nearWallIsClose()` | — | ✔ |

**class `ChunkUpdateViewOneTest.Unlight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unlight(int, int)` | — | ✔ |
| `specialRadiusReachesFurther()` | — | ✔ |
| `gridAtSpecialRadiusIsNotClose()` | — | ✔ |
| `levelBonusIsIntegerDivision()` | — | ✔ |
| `lightSourceDisablesSpecialRadius()` | — | ✔ |
| `withoutTheFlagOrdinaryRadiusIsUsed()` | — | ✔ |


##### `ChunkUpdateViewTest.java`

**Responsible for:** Tests Chunk.updateView, the port of C's update_view (cave-view.c).

**class `ChunkUpdateViewTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (18/18 documented): ✔ `WIDTH`, `HEIGHT`, `PLAYER`, `WALL_ROW`, `HIDDEN`, `NEAR`, `none`, `floor`, `granite`, `savedFeatures`, `savedConstants`, `savedBus`, `savedPlayer`, `savedCave`, `level`, `memory`, `player`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `currentFeatures()` | — | ✔ |
| `feature(TerrainFlags, String, TerrainFeatureFlags)` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `seedGlobals()` | — | ✔ |
| `newLevel()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `lay(Chunk, Feature)` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `blind(int)` | — | ✔ |
| `has(Loc, SquareEnum)` | — | ✔ |
| `redrewMap()` | — | ✔ |

**class `ChunkUpdateViewTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `types`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `ChunkUpdateViewTest.PlayerGrid`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `viewWithoutSeenInTheDark()` | — | ✔ |
| `carriedLightMakesItSeen()` | — | ✔ |
| `litTerrainMakesItSeen()` | — | ✔ |
| `unlightMakesItSeen()` | — | ✔ |
| `blindnessClearsSeen()` | — | ✔ |

**class `ChunkUpdateViewTest.BlindMemory`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `forgetsMemoryThatSaysImpassable()` | — | ✔ |
| `keepsMemoryThatSaysPassable()` | — | ✔ |
| `sightedForgetsNothing()` | — | ✔ |
| `unknownGridIsLeftAlone()` | — | ✔ |

**class `ChunkUpdateViewTest.Sweeps`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reachableGridEntersView()` | — | ✔ |
| `staleFlagsAreWiped()` | — | ✔ |
| `gridGoneOutOfSightIsRedrawn()` | — | ✔ |
| `snapshotIsClearedEverywhere()` | — | ✔ |


##### `ChunkUtilsIsProjectableTest.java`

**Responsible for:** Tests ChunkUtils#isProjectable, the port of C's projectable (project.c).

**class `ChunkUtilsIsProjectableTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (6/6 documented): ✔ `SIZE`, `MAX_RANGE`, `level`, `player`, `realPlayer`, `realConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `at(int, int)` | — | ✔ |
| `set(ProjectEnum)` | — | ✔ |
| `fixture()` | — | ✔ |
| `restore()` | — | ✔ |
| `along(int, ProjectEnum)` | — | ✔ |
| `sameGrid()` | — | ✘ |
| `adjacent()` | — | ✘ |
| `openPaths()` | — | ✘ |
| `targetNotPassable()` | — | ✘ |
| `wallInTheWay()` | — | ✘ |
| `wallAsTarget()` | — | ✘ |
| `rangeLimit()` | — | ✘ |
| `shortenedRange()` | — | ✘ |
| `shortenedRangeNeedsBoth()` | — | ✘ |
| `shortenedRangeRounding()` | — | ✘ |
| `shortenedRangeAmongOtherFlags()` | — | ✘ |
| `thruOvershoots()` | — | ✘ |


##### `ChunkUtilsLosTest.java`

**Responsible for:** Tests ChunkUtils#los, the port of C's los (cave-view.c).

**class `ChunkUtilsLosTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (3/3 documented): ✔ `WIDTH`, `HEIGHT`, `level`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `at(int, int)` | — | ✔ |
| `newLevel()` | — | ✔ |
| `open(Loc)` | — | ✔ |
| `wall(Loc)` | — | ✔ |
| `los(int, int, int, int)` | — | ✔ |

**class `ChunkUtilsLosTest.Degenerate`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `identical()` | — | ✔ |
| `adjacent()` | — | ✔ |

**class `ChunkUtilsLosTest.Straight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `southClear()` | — | ✔ |
| `southBlocked()` | — | ✔ |
| `northBlocked()` | — | ✔ |
| `northClear()` | — | ✔ |
| `eastClear()` | — | ✔ |
| `eastBlocked()` | — | ✔ |
| `westBlocked()` | — | ✔ |

**class `ChunkUtilsLosTest.Knights`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tallKnightGranted()` | — | ✔ |
| `tallKnightBlocked()` | — | ✔ |
| `wideKnightGranted()` | — | ✔ |
| `wideKnightBlocked()` | — | ✔ |
| `notReflexive()` | — | ✔ |

**class `ChunkUtilsLosTest.Walk`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `diagonalBlocked()` | — | ✔ |
| `diagonalFlanksIgnored()` | — | ✔ |
| `diagonalEndpointsIgnored()` | — | ✔ |
| `shallowPathBlocks()` | — | ✔ |
| `shallowCornerBrushed()` | — | ✔ |
| `steeperCornerEntered()` | — | ✔ |
| `steeperPathBlocks()` | — | ✔ |
| `verticalPathBlocks()` | — | ✔ |
| `verticalClear()` | — | ✔ |
| `reflexive()` | — | ✔ |


##### `ChunkUtilsProjectionPathTest.java`

**Responsible for:** Tests ChunkUtils#projectionPath, the port of C's project_path (project.c).

**class `ChunkUtilsProjectionPathTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (3/3 documented): ✔ `SIZE`, `FAR`, `level`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `at(int, int)` | — | ✔ |
| `newLevel()` | — | ✔ |
| `wall(int, int)` | — | ✔ |
| `placeDecoy(Loc)` | — | ✔ |
| `path(int, int, int, int, int, ProjectEnum)` | — | ✔ |

**class `ChunkUtilsProjectionPathTest.Degenerate`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `equalGrids()` | — | ✔ |
| `adjacent()` | — | ✔ |

**class `ChunkUtilsProjectionPathTest.ExactLines`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `east()` | — | ✔ |
| `north()` | — | ✔ |
| `southEast()` | — | ✔ |
| `northWest()` | — | ✔ |

**class `ChunkUtilsProjectionPathTest.Slants`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `verticalTie()` | — | ✔ |
| `horizontalTie()` | — | ✔ |
| `horizontalNegative()` | — | ✔ |

**class `ChunkUtilsProjectionPathTest.RangeLimit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `straight()` | — | ✔ |
| `diagonal()` | — | ✔ |
| `slantedStopsEarly()` | — | ✔ |

**class `ChunkUtilsProjectionPathTest.Stopping`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `stopsAtFinish()` | — | ✔ |
| `thru()` | — | ✔ |
| `wallEndsPath()` | — | ✔ |
| `wallAtFirstGrid()` | — | ✔ |
| `rock()` | — | ✔ |
| `stopAtMonster()` | — | ✔ |
| `monsterIgnored()` | — | ✔ |
| `stopAtDecoy()` | — | ✔ |
| `decoyIgnored()` | — | ✔ |


##### `ClockwiseDirectionLoopTest.java`

**Responsible for:** Tests ClockwiseDirectionLoop against C's clockwise_grid[] and clockwise_ddd[] (cave.c).

**class `ClockwiseDirectionLoopTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `CLOCKWISE_GRID` — ✘ `loop`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newLoop()` | — | ✘ |
| `loopsAreIndependent()` | — | ✘ |
| `newLoopDoesNotResetOthers()` | — | ✘ |
| `startsAtNorth()` | — | ✘ |
| `fullLapMatchesC()` | — | ✘ |
| `centreWrapsToNorth()` | — | ✘ |
| `getGridPlacesXAndY()` | — | ✘ |
| `getGridReturnsFreshLoc()` | — | ✘ |
| `everyCompassEntryIsAdjacent()` | — | ✘ |


##### `FeatureTest.java`

**Responsible for:** Checks Feature against the C original: the feat_is_*() predicates in cave-square.c, the mimic handling in map_info() in cave-map.c, and the FEAT_PERM test the hallucination rolls make.

**class `FeatureTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (0/1 documented): ✘ `savedFeatures`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flags(TerrainFeatureFlags)` | — | ✘ |
| `feature(TerrainFlags, TerrainFlags, TerrainFeatureFlags)` | — | ✘ |
| `feature(TerrainFlags, String, TerrainFlags, int, TerrainFeatureFlags)` | — | ✘ |
| `withFlags(TerrainFeatureFlags)` | — | ✘ |
| `sharing(Flag<TerrainFeatureFlags>, MonsterRaceFlag, String, TerrainFlags, int)` | — | ✘ |
| `saveRegistry()` | — | ✔ |
| `restoreRegistry()` | — | ✘ |
| `singleFlagPredicates()` | — | ✘ |
| `stairFlagsAreSeparate()` | — | ✘ |
| `openDoorAndCloseableShareFlag()` | — | ✘ |
| `isRockIsGraniteWithoutDoors()` | — | ✘ |
| `isFullPermanentNeedsRock()` | — | ✘ |
| `isPermanentIsCodeTest()` | — | ✘ |
| `isNoFeat()` | — | ✘ |
| `isMimicingTestsNull()` | — | ✘ |
| `getMimicWithoutMimicReturnsThis()` | — | ✘ |
| `getMimicResolvesThroughRegistry()` | — | ✘ |
| `getMimicUnknownCodeIsNull()` | — | ✘ |
| `accessors()` | — | ✘ |
| `equalsAndHashCodeWithSharedFlags()` | — | ✘ |
| `equalsAcrossSeparateFlagObjects()` | — | ✔ |
| `toStringShowsFields()` | — | ✘ |

**record `FeatureTest.Pred`** — class Javadoc: ✘ · type stamp: —

- Record components (via `@param`) (0/3 documented): ✘ `name`, `flag`, `test`


##### `GenChunkTest.java`

**Responsible for:** Tests GenChunk, the port of the chunk_list_max counter from C's gen-chunk.c (gen-chunk.c:38).

**class `GenChunkTest`** — class Javadoc: ✔ · type stamp: on 260908

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resetChunkListMax()` | — | ✘ |
| `initialValueMatchesCInitializer()` | — | ✔ |
| `setThenGetRoundTripsTheValue()` | — | ✘ |
| `aLaterSetOverwritesAnEarlierOne()` | — | ✘ |
| `roundTripsTheTopOfCsUint16Range()` | — | ✔ |


##### `KeypadDirectionLoopTest.java`

**Responsible for:** Tests KeypadDirectionLoop against C's ddgrid_ddd[] (cave.c).

**class `KeypadDirectionLoopTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `DDGRID_DDD` — ✘ `loop`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newLoop()` | — | ✘ |
| `startsAtSouth()` | — | ✘ |
| `fullLapMatchesC()` | — | ✘ |
| `getGridMatchesTableEverywhere()` | — | ✘ |
| `centreWrapsToSouth()` | — | ✘ |
| `secondLapRepeats()` | — | ✘ |
| `loopsAreIndependent()` | — | ✘ |
| `newLoopDoesNotResetOthers()` | — | ✘ |
| `getGridPlacesXAndY()` | — | ✘ |
| `getGridReturnsFreshLoc()` | — | ✘ |
| `onlyTheNinthIsTheOrigin()` | — | ✘ |


##### `LocTest.java`

**Responsible for:** Unit tests for Loc, the port of the C source's grid coordinate type (src/z-type.c).

**class `LocTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertAt(int, int, Loc)` | — | ✔ |

**class `LocTest.Construction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rowThenColumnFixesTheCoordinatesInThatOrder()` | — | ✘ |
| `negativeCoordinatesAreAllowedBecauseOffsetsUseTheSameType()` | — | ✘ |
| `theSharedOriginIsAtZeroZero()` | — | ✘ |
| `aRowBuilderCanBeCompletedIndependentlyOfOtherBuilders()` | — | ✘ |

**class `LocTest.Arithmetic`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `summingAddsBothCoordinates()` | — | ✘ |
| `summingLeavesBothOperandsUntouched()` | — | ✘ |
| `differenceSubtractsTheArgumentFromTheReceiver()` | — | ✘ |
| `aDifferenceIsADisplacementAndMayBeNegative()` | — | ✘ |
| `offsetIsSumWithLooseCoordinates()` | — | ✘ |
| `offsettingByNothingGivesAnEqualLocation()` | — | ✘ |
| `summingWithTheOriginIsTheIdentity()` | — | ✘ |
| `aLocationDifferencedWithItselfIsTheOrigin()` | — | ✘ |

**class `LocTest.Stepping`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `steppingAppliesTheDirectionsDeltas()` | — | ✘ |
| `diagonalStepsMoveOnBothAxes()` | — | ✘ |
| `theNonDirectionsDoNotMove()` | — | ✘ |
| `steppingIsTheSameAsSummingTheDirectionsGrid(DirectionEnum)` | — | ✘ |
| `steppingPerformsNoBoundsChecking()` | — | ✘ |
| `eightStandardStepsFromOneGridReachEightDistinctNeighbours()` | — | ✘ |

**class `LocTest.EqualityAndHashing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `locationsWithTheSameCoordinatesAreEqual()` | — | ✘ |
| `transposedCoordinatesAreNotEqual()` | — | ✘ |
| `differingOnEitherAxisIsEnoughToBeUnequal()` | — | ✘ |
| `nullAndForeignTypesAreNotEqual()` | — | ✘ |
| `equalLocationsHashAlike()` | — | ✘ |
| `aDenseBlockOfGridsHashesWithoutCollision()` | — | ✘ |
| `locationsWorkAsHashSetMembers()` | — | ✘ |
| `locationsWorkAsHashMapKeys()` | — | ✘ |

**class `LocTest.Origin`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onlyTheOriginIsZero()` | — | ✘ |
| `arithmeticOnTheSharedOriginReturnsAFreshLocationRatherThanAliasingIt()` | — | ✘ |

**class `LocTest.RandomDisplacement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRandomLocationStaysWithinTheGivenSpreadOnBothAxes()` | — | ✘ |
| `aZeroSpreadGivesTheCentreBack()` | — | ✘ |
| `theDrawIsRectangularNotCircular()` | — | ✘ |

**class `LocTest.Bearing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aGridHasNoBearingTowardsItself()` | — | ✘ |
| `aSharedColumnGivesNorthOrSouth()` | — | ✘ |
| `aSharedRowGivesEastOrWest()` | — | ✘ |
| `differingOnBothAxesGivesADiagonal()` | — | ✘ |
| `onlyTheSignOfTheDifferenceMattersNotItsSize()` | — | ✘ |
| `theBearingIsTakenFromTheCallerNotTheArgument()` | — | ✘ |
| `everyNeighbourOfAGridIsTheDirectionThatStepsToIt()` | — | ✘ |
| `steppingOnTheBearingAlwaysClosesTheDistance()` | — | ✘ |
| `repeatedStepsArriveAtTheTarget()` | — | ✘ |
| `theBearingIsNeverANonDirection()` | — | ✘ |
| `chebyshev(Loc, Loc)` | — | ✔ |

**class `LocTest.Distance`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aGridIsNoDistanceFromItself()` | — | ✘ |
| `alongOneAxisTheDistanceIsExact()` | — | ✘ |
| `aDiagonalCostsOneAndAHalfPerStep()` | — | ✘ |
| `theSmallerComponentIsHalvedTowardsZero()` | — | ✘ |
| `theBranchTurnsOnAStrictGreaterThan()` | — | ✘ |
| `theApproximationOverEstimatesTheTrueDistance()` | — | ✘ |
| `negativeDifferencesAreTakenAbsolutely()` | — | ✘ |
| `theDistanceIsSymmetric()` | — | ✘ |
| `everyNearbyGridMatchesTheCFormula()` | — | ✘ |


##### `PointSetTest.java`

**Responsible for:** Unit tests for PointSet, the port of the C source's grid collection (struct point_set in z-type.c).

**class `PointSetTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `points`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `aFreshSetIsEmpty()` | — | ✘ |
| `anAddedGridIsFoundAgain()` | — | ✘ |
| `membershipIsByValueNotIdentity()` | — | ✘ |
| `aGridThatWasNeverAddedIsNotFound()` | — | ✘ |
| `transposedCoordinatesAreADifferentGrid()` | — | ✘ |
| `severalGridsAreAllHeldAtOnce()` | — | ✘ |
| `addingTheSameGridTwiceStoresItTwice()` | — | ✘ |
| `everyGridSurvivesPastCsGrowthPoints()` | — | ✘ |
| `theOriginIsAnOrdinaryMember()` | — | ✘ |
| `negativeCoordinatesAreHeldLikeAnyOther()` | — | ✘ |


##### `SquareHoldsObjectTest.java`

**Responsible for:** Tests Square#holdsObject, the port of C's square_holds_object (cave-square.c).

**class `SquareHoldsObjectTest`** — class Javadoc: ✔ · type stamp: on 260816

- Fields (0/1 documented): ✘ `square`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `objectOnSquareIsHeld()` | — | ✘ |
| `absentObjectIsNotHeld()` | — | ✘ |
| `emptySquareHoldsNothing()` | — | ✘ |
| `identicalButDistinctObjectIsNotHeld()` | — | ✔ |
| `allObjectsInAPileAreHeld()` | — | ✔ |
| `excisedObjectIsNotHeld()` | — | ✔ |


##### `SquareInfoFlagsTest.java`

**Responsible for:** Tests Square#sqInfoOn(SquareEnum) and Square#sqInfoOff(SquareEnum), the port of C's sqinfo_on and sqinfo_off (cave.h).

**class `SquareInfoFlagsTest`** — class Javadoc: ✔ · type stamp: on 260827

- Fields (1/1 documented): ✔ `square`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newSquare()` | — | ✔ |

**class `SquareInfoFlagsTest.InfoOn`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setsTheFlag()` | — | ✔ |
| `leavesOthersAlone()` | — | ✔ |
| `settingTwiceIsIdempotent()` | — | ✔ |
| `flagsAccumulate()` | — | ✔ |

**class `SquareInfoFlagsTest.InfoOff`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearsTheFlag()` | — | ✔ |
| `leavesOthersAlone()` | — | ✔ |
| `clearingUnsetFlagIsNoOp()` | — | ✔ |
| `clearingTwiceIsIdempotent()` | — | ✔ |
| `clearingOnFreshSquareIsSafe()` | — | ✔ |

**class `SquareInfoFlagsTest.ThePair`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onThenOffRoundTrips()` | — | ✔ |
| `offThenOnLeavesItSet()` | — | ✔ |
| `everyFlagRoundTrips()` | — | ✔ |
| `clearingFromFullFieldLeavesTheRest()` | — | ✔ |

**class `SquareInfoFlagsTest.ReportedChange`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsOnlyRealChanges()` | — | ✔ |
| `answerIsPerFlag()` | — | ✔ |
| `everyFlagReportsItsOwnChange()` | — | ✔ |


##### `SquareOccupancyTest.java`

**Responsible for:** Tests the four predicates Square answers about who is standing on it — Square#isPlayer, Square#isOccupied, Square#isFree and Square#isOpen — and the two built on top of them, Square#isEmpty and Square#isArrivable.

**class `SquareOccupancyTest`** — class Javadoc: ✔ · type stamp: on 260817

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `floor(int)` | — | ✔ |
| `wall(int)` | — | ✔ |

**class `SquareOccupancyTest.Occupancy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `negativeIsPlayer()` | — | ✔ |
| `occupiedCoversPlayer()` | — | ✔ |
| `freeIsNotOccupied()` | — | ✔ |

**class `SquareOccupancyTest.Derived`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `openIsEmptyFloor()` | — | ✔ |
| `nonFloorIsNotOpen()` | — | ✔ |
| `openAndOccupiedDisagree()` | — | ✔ |
| `emptyIsOpenAndObjectless()` | — | ✔ |
| `arrivableNeedsClearFloor()` | — | ✔ |


##### `SquareTerrainClassTest.java`

**Responsible for:** Runs every terrain class in terrain.txt that carries a rock-related flag through the six Square predicates whose C definitions read TF_ROCK, TF_GRANITE or both, and checks the answers against the definitions in cave-square.c.

**class `SquareTerrainClassTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (0/14 documented): ✘ `FLOOR`, `PASSABLE`, `WALL`, `ROCK`, `GRANITE`, `DOOR_ANY`, `DOOR_CLOSED`, `CLOSABLE`, `PERMANENT`, `MAGMA`, `QUARTZ`, `GOLD`, `STAIR`, `FIERY`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `row(String, boolean, boolean, boolean, boolean, boolean, boolean, TerrainFeatureFlags)` | — | ✘ |
| `rows()` | — | ✔ |
| `square(TerrainFeatureFlags)` | — | ✘ |
| `rockPredicatesMatchC()` | — | ✔ |
| `featureSeparatesTheTwoQuestions()` | — | ✔ |

**record `SquareTerrainClassTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (8/8 documented): ✔ `name`, `flags`, `isRock`, `isPerm`, `isRubble`, `secretDoor`, `seemsWall`, `mineral`


##### `SquareTrapAndDoorTest.java`

**Responsible for:** Tests the Square predicates that read the trap list and the door terrain — the trap scans Square#trapSpecific, Square#trapFlag and Square#trapTimeout, the lock predicates built on them, and the behaviour predicates that veto on a trap — against C's cave-square.c and trap.c.

**class `SquareTrapAndDoorTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (1/8 documented): ✔ `noTrap` — ✘ `web`, `decoy`, `lock`, `glyph`, `dartA`, `dartB`, `savedRegistry`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(String, String, int)` | — | ✘ |
| `set(Trap, String, Object)` | — | ✘ |
| `trap(TrapKind, int, int, TrapEnum)` | — | ✔ |
| `feature(TerrainFeatureFlags)` | — | ✘ |
| `square(Feature, boolean, Trap)` | — | ✔ |
| `floorWith(boolean, Trap)` | — | ✘ |
| `closedDoorWith(Trap)` | — | ✘ |
| `seedRegistry()` | — | ✔ |
| `restoreRegistry()` | — | ✔ |

**class `SquareTrapAndDoorTest.Scans`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `specificUsesIndex()` | — | ✔ |
| `specificScansAll()` | — | ✔ |
| `markerGatesSpecificAndFlag()` | — | ✔ |
| `trapFlags()` | — | ✔ |
| `flagScansAll()` | — | ✔ |
| `timeoutByKindIndex()` | — | ✔ |
| `timeoutIsNotListPosition()` | — | ✔ |
| `timeoutIgnoresMarker()` | — | ✔ |
| `empty()` | — | ✔ |
| `topTrap()` | — | ✔ |

**class `SquareTrapAndDoorTest.NamedTraps`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `byName()` | — | ✔ |
| `vetoes()` | — | ✔ |
| `decoyDoesNotVeto()` | — | ✔ |
| `objectMattersToEmptyOnly()` | — | ✔ |
| `stairsAndDoors()` | — | ✔ |

**class `SquareTrapAndDoorTest.Doors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `power()` | — | ✔ |
| `noLock()` | — | ✔ |
| `lockOnNonDoor()` | — | ✔ |
| `doorStates()` | — | ✔ |

**class `SquareTrapAndDoorTest.Behaviour`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `canPutItem()` | — | ✔ |
| `allowsFeel()` | — | ✔ |
| `topObject()` | — | ✔ |
| `nullFeature()` | — | ✔ |


##### `TerrainRegistryTest.java`

**Responsible for:** Tests TerrainRegistry against the C globals it stands in for: z_info->trap_max, set by finish_parse_trap() in init.c to the number of trap records, f_info (indexed by FEAT_ constant), and the index-0 skip in lookup_trap() in trap.c.

**class `TerrainRegistryTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/2 documented): ✘ `savedTraps`, `savedFeatures`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(String, String, int)` | — | ✘ |
| `feature(TerrainFlags, String)` | — | ✘ |
| `saveRegistry()` | — | ✘ |
| `restoreRegistry()` | — | ✘ |
| `trapMaxCountsEveryRecord()` | — | ✘ |
| `trapMaxFollowsSetTrapInfo()` | — | ✘ |
| `trapListsAreReadOnlyViews()` | — | ✘ |
| `lookupTrapSkipsIndexZero()` | — | ✘ |
| `lookupFeatureByCode()` | — | ✘ |
| `lookupFeatureBeforeLoad()` | — | ✘ |
| `featuresAreAReadOnlyView()` | — | ✘ |


##### `TrapKindTest.java`

**Responsible for:** Tests TrapKind#lookupTrap(String) against lookup_trap() in trap.c.

**class `TrapKindTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (0/10 documented): ✘ `saved`, `unnamed`, `noTrap`, `glyph`, `doorLock`, `pit`, `spikedPit`, `poisonPit`, `runeFoe`, `runeSummoning`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(String, String, int)` | — | ✘ |
| `installTable()` | — | ✘ |
| `restoreTable()` | — | ✘ |
| `exactMatch()` | — | ✘ |
| `exactBeatsEarlierClose()` | — | ✘ |
| `closeMatchFirstWins()` | — | ✘ |
| `exactIsCaseSensitive()` | — | ✘ |
| `noMatchIsNull()` | — | ✘ |
| `unnamedKindsSkipped()` | — | ✘ |
| `emptyStringReturnsFirstNamed()` | — | ✘ |
| `indexZeroIsSkipped()` | — | ✘ |
| `closeMatchSkipsIndexZero()` | — | ✘ |
| `indexZeroSkippedByIndexField()` | — | ✘ |
| `emptyTable()` | — | ✘ |
| `accessors()` | — | ✘ |


##### `TrapTest.java`

**Responsible for:** Tests the accessors of Trap against struct trap in trap.h and the timeout countdown in game-world.c.

**class `TrapTest`** — class Javadoc: ✔ · type stamp: on 260930

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(TrapEnum)` | — | ✘ |
| `set(Trap, String, Object)` | — | ✘ |
| `trap(TrapKind, int, int, TrapEnum)` | — | ✘ |
| `accessorsReturnFields()` | — | ✘ |
| `hasTrapReadsInstanceFlags()` | — | ✘ |
| `kindFlagsAreNotInstanceFlags()` | — | ✘ |
| `decrementFromOneReachesZero()` | — | ✘ |
| `decrementWalksDown()` | — | ✘ |
| `decrementTouchesOnlyTimeout()` | — | ✘ |


## middle/cave/chunkbuilders

##### `BuilderTypeTest.java`

**Responsible for:** Checks BuilderType against C's cave_builders[], which generate.c builds from list-dun-profiles.h.

**class `BuilderTypeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `C_NAMES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sameNumberOfEntriesAsC()` | — | ✘ |
| `namesAndOrderMatchC()` | — | ✘ |
| `namesAreUnique()` | — | ✘ |
| `multiWordNameKeepsItsSpace()` | — | ✘ |
| `lookupByNameFindsEveryEntryLikeParseProfileName()` | — | ✘ |
| `lookupIsExactLikeStreq()` | — | ✘ |


## middle/cave/enums

##### `DirectionEnumTest.java`

**Responsible for:** Unit tests for DirectionEnum, the port of the C source's direction tables (ddx/ddy/ddgrid/ddd in cave.c).

**class `DirectionEnumTest`** — class Javadoc: ✔ · type stamp: —

**class `DirectionEnumTest.KeyLookup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eachCompassDirectionIsFoundByItsKeypadDigit()` | — | ✘ |
| `theKeypadLayoutMatchesTheDigitsPositions(int)` | — | ✔ |
| `eachKeypadRowSharesARowOffsetAndEachColumnAColumnOffset()` | — | ✘ |
| `keyZeroIsTheUnknownDirection()` | — | ✘ |
| `theSharedMiddleKeyResolvesToWhicheverConstantIsDeclaredFirst()` | — | ✘ |
| `anUnusedKeyFallsBackToUnknownRatherThanNull()` | — | ✘ |
| `everyDirectionIsReachableFromItsOwnKeyOrSharesThatKey(DirectionEnum)` | — | ✘ |

**class `DirectionEnumTest.Deltas`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theEightStandardDirectionsHaveDistinctNonZeroDeltas()` | — | ✘ |
| `theNonDirectionsHaveNoDelta()` | — | ✘ |
| `everyDeltaIsASingleStep(DirectionEnum)` | — | ✘ |
| `ddgridPackagesTheSameDeltasAsDdxAndDdy(DirectionEnum)` | — | ✘ |
| `oppositeDirectionsCancel()` | — | ✘ |
| `theFourDiagonalsMoveOnBothAxesAndTheFourOrthogonalsOnOne()` | — | ✘ |

**class `DirectionEnumTest.StandardDirections`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `exactlyEightDirectionsAreStandard()` | — | ✘ |
| `theNonDirectionsAreNotStandard()` | — | ✘ |
| `aDirectionIsStandardExactlyWhenItActuallyMoves(DirectionEnum)` | — | ✘ |

**class `DirectionEnumTest.SurroundingDirections`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `cDdd`, `cDdgridDdd`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theRingFollowsCsLoopOrderByKey()` | — | ✘ |
| `theRingOffsetsMatchDdgridDddEntryForEntry()` | — | ✘ |
| `theRingHoldsOnlyStandardDirections()` | — | ✘ |
| `eachCallReturnsAFreshArray()` | — | ✘ |


##### `RoomFlagsTest.java`

**Responsible for:** Unit tests for RoomFlags, the port of list-room-flags.h and the room_flags[] name table in generate.c.

**class `RoomFlagsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCNameTableIndices()` | — | ✘ |
| `maxIsTheSizeOfTheFlagSet()` | — | ✘ |
| `helpStringsMatchHeader()` | — | ✘ |
| `namesResolveAsTheParsersLookThemUp()` | — | ✘ |


##### `SquareEnumTest.java`

**Responsible for:** Unit tests for SquareEnum, the port of list-square-flags.h and the SQUARE_* enum in cave.h.

**class `SquareEnumTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (1/1 documented): ✔ `HEADER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCEnumValues()` | — | ✘ |
| `maxIsTheSizeOfTheFlagSet()` | — | ✘ |
| `everyFlagHasTheHeadersNameAndDescriptionAtTheHeadersIndex()` | — | ✘ |
| `wasSeenDescriptionHasNoStrayPunctuation()` | — | ✘ |
| `sentinelsHaveEmptyDescriptions()` | — | ✘ |
| `namesResolveByValueOf()` | — | ✘ |


##### `TerrainFeatureFlagsTest.java`

**Responsible for:** Unit tests for TerrainFeatureFlags, the port of list-terrain-flags.h and the TF_* enum in cave.h.

**class `TerrainFeatureFlagsTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (1/1 documented): ✔ `HEADER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCEnumValues()` | — | ✘ |
| `maxIsTheSizeOfTheFlagSet()` | — | ✘ |
| `everyFlagHasTheHeadersNameAndDescriptionAtTheHeadersIndex()` | — | ✘ |
| `sentinelsHaveEmptyDescriptions()` | — | ✘ |
| `namesResolveByValueOf()` | — | ✘ |


##### `TerrainFlagsTest.java`

**Responsible for:** Unit tests for TerrainFlags, the port of the FEAT_* indexes in list-terrain.h and cave.h.

**class `TerrainFlagsTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (1/1 documented): ✔ `HEADER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCEnumValues()` | — | ✔ |
| `everyHeaderLineMatchesInOrder()` | — | ✔ |
| `sizeIsHeaderPlusSentinel()` | — | ✔ |
| `unknownNameIsRejected()` | — | ✔ |


## middle/cave/profiles/dungeon

##### `CaveProfileTest.java`

**Responsible for:** Tests for CaveProfile, the port of struct cave_profile in the C original's generate.h.

**class `CaveProfileTest`** — class Javadoc: ✔ · type stamp: on 260930

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `classicRecordRoundTripsEveryField()` | — | ✔ |
| `townRecordWithNoTunnelLineKeepsNullTun()` | — | ✔ |
| `missingStreamerLineKeepsNullStr()` | — | ✔ |
| `allocIsReturnedRaw(int)` | — | ✔ |
| `minLevelIsReturnedRaw(int)` | — | ✔ |
| `roomListKeepsFileOrder()` | — | ✔ |


##### `RoomProfileTest.java`

**Responsible for:** Tests for RoomProfile, the port of struct room_profile in the C original's generate.h.

**class `RoomProfileTest`** — class Javadoc: ✔ · type stamp: on 260930

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `monsterPitRoundTripsEveryField()` | — | ✔ |
| `roomTemplateKeepsRatingAndFullCutoff()` | — | ✔ |
| `staircaseRoomKeepsExtremeRarityAndZeroCutoff()` | — | ✔ |
| `roomTypeIsKeptByIdentityAndMayBeNull()` | — | ✔ |


##### `StreamerProfileTest.java`

**Responsible for:** Tests for StreamerProfile, the port of struct streamer_profile in the C original's generate.h.

**class `StreamerProfileTest`** — class Javadoc: ✔ · type stamp: on 260930

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `classicRecordRoundTripsEveryField()` | — | ✔ |
| `townRecordKeepsZeroCounts()` | — | ✔ |
| `valuesAreStoredRaw(int, int, int, int, int, int)` | — | ✔ |


##### `TunnelProfileTest.java`

**Responsible for:** Tests for TunnelProfile, the port of struct tunnel_profile in the C original's generate.h.

**class `TunnelProfileTest`** — class Javadoc: ✔ · type stamp: on 260930

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `classicRecordRoundTripsEveryField()` | — | ✔ |
| `argumentOrderMatchesTunnelLine()` | — | ✔ |
| `valuesAreStoredRaw(int, int, int, int, int)` | — | ✔ |


## middle/cave/profiles/room

##### `RoomTemplateTest.java`

**Responsible for:** Tests for RoomTemplate, the port of C's struct room_template (generate.h).

**class `RoomTemplateTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load()` | — | ✘ |
| `constructorArgumentsComeBackFromTheirOwnGetters()` | — | ✔ |
| `anAbsentTvalAndAnEmptyFlagSetAreCarriedAsGiven()` | — | ✔ |
| `everyRealTemplateTextIsHeightTimesWidthLong()` | — | ✔ |
| `everyRealTemplateIsTypeOne()` | — | ✔ |
| `theRatingHistogramMatchesTheCDataFile()` | — | ✔ |
| `theDoorHistogramMatchesTheCDataFileAndNeverHitsZero()` | — | ✔ |


## middle/cave/profiles/vault

##### `VaultTest.java`

**Responsible for:** Unit tests for the Vault holder, the port of C's struct vault in generate.h.

**class `VaultTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `ROWS` — ✘ `FLAT`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `vault(Flag<RoomFlags>, int, int)` | — | ✘ |
| `everyFieldRoundTrips()` | — | ✔ |
| `aZeroMinimumIsKept()` | — | ✔ |
| `noFlagsMeansAnEmptySetNotNull()` | — | ✔ |
| `flatAndPerRowLayoutsAgreeCellForCell()` | — | ✔ |


## middle/cave/roombuilders

##### `RoomTypeTest.java`

**Responsible for:** Tests for RoomType, the port of the room_builders[] table in the C original's generate.c and its three accessors.

**class `RoomTypeTest`** — class Javadoc: ✔ · type stamp: on 260930

- Fields (1/1 documented): ✔ `C_TABLE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `countIsNineteen()` | — | ✔ |
| `everyRowMatchesListRoomsH()` | — | ✔ |
| `indexFromNameFindsEveryName()` | — | ✔ |
| `indexFromNameIsCaseSensitive()` | — | ✔ |
| `indexFromNameRejectsUnknownNames()` | — | ✔ |
| `nameFromIndexReturnsEveryName()` | — | ✔ |
| `nameFromIndexIsNullOutOfRange()` | — | ✔ |
| `roomTypeFromNameAgreesWithIndex()` | — | ✔ |
| `roomTypeFromNameRejectsWrongNamesAndCase()` | — | ✔ |
| `onlyEightTypesHaveCaps()` | — | ✔ |


## middle/combat

##### `BlowMethodTest.java`

**Responsible for:** Tests for BlowMethod, the port of C's struct blow_method.

**class `BlowMethodTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `find(List<BlowMethod>, String)` | — | ✘ |
| `constructorStoresEveryFieldAsGiven()` | — | ✘ |
| `hitFlagsMatchDataFile()` | — | ✘ |
| `touchIsNonPhysicalButAnnouncesMisses()` | — | ✘ |
| `punchStunsButDoesNotCut()` | — | ✘ |
| `clawCutsButDoesNotStun()` | — | ✘ |
| `silentMethodsDoNotAnnounceMisses()` | — | ✘ |
| `actionCountMatchesC()` | — | ✘ |
| `everyMethodHasDescAndAction()` | — | ✘ |


##### `MeleeCriticalLevelDataTest.java`

**Responsible for:** Tests for the Java form of C's struct critical_level: MeleeCriticalLevelData and RangedCriticalLevelData, which replaced the unused CriticalLevel class.

**class `MeleeCriticalLevelDataTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load()` | — | ✘ |
| `meleeRowsKeepFileOrderAndValues()` | — | ✘ |
| `rangedRowsKeepFileOrderAndValues()` | — | ✘ |


##### `TargetTest.java`

**Responsible for:** Tests Target#setTargetMonster, Target#getTargetMonster, Target#targetOkay and the targetable they depend on, ports of target_set_monster, target_get_monster, target_okay and target_able in C's target.c.

**class `TargetTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (7/7 documented): ✔ `SIZE`, `MAX_RANGE`, `level`, `player`, `realPlayer`, `realCave`, `realConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `at(int, int)` | — | ✔ |
| `setField(String, Object)` | — | ✔ |
| `field(String)` | — | ✔ |
| `setFeature(Loc, Feature)` | — | ✔ |
| `monster(int, Loc, boolean, boolean, MonsterRace)` | — | ✔ |
| `good(int, Loc)` | — | ✔ |
| `fixture()` | — | ✔ |
| `restore()` | — | ✔ |
| `ordinaryTarget()` | — | ✘ |
| `indexComesFromMonster()` | — | ✘ |
| `nullResets()` | — | ✘ |
| `invisible()` | — | ✘ |
| `camouflaged()` | — | ✘ |
| `noRace()` | — | ✘ |
| `outOfRange()` | — | ✘ |
| `behindWall()` | — | ✘ |
| `hallucinating()` | — | ✘ |
| `fixedKeepsGrid()` | — | ✘ |
| `fixedNull()` | — | ✘ |
| `fixedStillRetargets()` | — | ✘ |
| `getTargetMonsterEmpty()` | — | ✘ |
| `rawTarget(boolean, int, Loc)` | — | ✔ |
| `okayUnset()` | — | ✘ |
| `okayMonsterRefreshesGrid()` | — | ✘ |
| `okayEmptySlotDoesNotFallBackToGrid()` | — | ✘ |
| `okayMonsterNotVisible()` | — | ✘ |
| `okayMonsterNotProjectable()` | — | ✘ |
| `okayHallucinating()` | — | ✘ |
| `okayGridBothNonZero()` | — | ✘ |
| `okayGridOnAnAxis()` | — | ✘ |
| `okayGridOrigin()` | — | ✘ |
| `okayGridNeverStored()` | — | ✘ |
| `setGridNullStoresOrigin()` | — | ✘ |
| `okayNegativeIndexUsesGrid()` | — | ✘ |
| `okayThroughTheSetter()` | — | ✘ |


## middle/combat/enums

##### `ProjectEnumTest.java`

**Responsible for:** Tests for ProjectEnum, the port of the anonymous PROJECT_* enum in project.h.

**class `ProjectEnumTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `C_TABLE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bitOf(ProjectEnum)` | — | ✔ |
| `sameNumberOfConstantsAsC()` | — | ✘ |
| `namesAndOrderMatchC()` | — | ✘ |
| `positionImpliesCBitValue()` | — | ✘ |
| `noneIsFirstAndRockIsLast()` | — | ✘ |
| `emptyFlagSetHasNoMembers()` | — | ✘ |
| `flagBuiltFromNoneIsNotEmpty()` | — | ✔ |
| `combinedFlagsAreIndependentMembers()` | — | ✘ |


## middle/effect

##### `EffectAccessorsTest.java`

**Responsible for:** Tests Effect's delegating accessors and its copy, along with Expression's dice string.

**class `EffectAccessorsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `effect(EffectEnum)` | — | ✔ |
| `aimAlone(Effect)` | — | ✔ |

**class `EffectAccessorsTest.IdentityBacked`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `realEffectAnswers()` | — | ✔ |
| `unaimedEffectReportsFalse()` | — | ✔ |
| `placeholderIsGuarded()` | — | ✔ |
| `sentinelIsGuarded()` | — | ✔ |

**class `EffectAccessorsTest.IsAimChain`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startIsInclusive()` | — | ✔ |
| `laterEffectCounts()` | — | ✔ |
| `earlierEffectIgnored()` | — | ✔ |
| `invalidHeadShortCircuits()` | — | ✔ |
| `invalidMiddleSkipped()` | — | ✔ |
| `noneAimed()` | — | ✔ |
| `notInList()` | — | ✔ |

**class `EffectAccessorsTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `diceDuplicated()` | — | ✔ |
| `identitySurvives()` | — | ✔ |
| `invalidStaysInvalid()` | — | ✔ |

**class `EffectAccessorsTest.ExpressionDiceString`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `diceStringOf(Expression)` | — | ✔ |
| `freshHasNone()` | — | ✔ |
| `diceStringStored()` | — | ✔ |
| `copyCarriesDiceString()` | — | ✔ |


##### `EffectSubTypeWrapperTest.java`

**Responsible for:** Tests EffectSubTypeWrapper, the port of the untagged union C keeps in an effect's subtype field.

**class `EffectSubTypeWrapperTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copyIsShallow()` | — | ✔ |
| `copyCarriesEverything()` | — | ✔ |

**class `EffectSubTypeWrapperTest.RoundTrips`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `projection()` | — | ✔ |
| `timed()` | — | ✔ |
| `nourish()` | — | ✔ |
| `monTimed()` | — | ✔ |
| `summonType()` | — | ✔ |
| `stat()` | — | ✔ |
| `enchant()` | — | ✔ |
| `earthquake()` | — | ✔ |
| `glyph()` | — | ✔ |
| `shape()` | — | ✔ |
| `summon()` | — | ✔ |

**class `EffectSubTypeWrapperTest.Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wrongAccessorThrows()` | — | ✔ |
| `projectionAccessorChecksField()` | — | ✔ |
| `nullDiscriminatorThrowsInvalidParameter()` | — | ✔ |
| `noneWrapperRefuses()` | — | ✔ |

**class `EffectSubTypeWrapperTest.Teleport`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `teleportTags()` | — | ✔ |
| `teleportToTags()` | — | ✔ |
| `teleportKindsAreDistinct()` | — | ✔ |

**class `EffectSubTypeWrapperTest.SetValue`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setValueTags()` | — | ✔ |
| `setValueRetags()` | — | ✔ |

**class `EffectSubTypeWrapperTest.Empty`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `declaredNone()` | — | ✔ |
| `unresolvedIsNull()` | — | ✔ |


## middle/enums

##### `EffectEnumTest.java`

**Responsible for:** Tests EffectEnum against the C original's list-effects.h and effects.c.

**class `EffectEnumTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (1/2 documented): ✔ `ROW` — ✘ `C_HEADER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oxford(String)` | on 261008 | ✔ |
| `everyCRowMatches()` | — | ✘ |
| `ordering()` | — | ✘ |
| `parenthesisedDescriptions()` | — | ✘ |
| `sentinels()` | — | ✘ |
| `subTypes()` | — | ✘ |
| `aimedSet()` | — | ✘ |


##### `MessageTypeTest.java`

**Responsible for:** Checks MessageType against list-message.h in the C original.

**class `MessageTypeTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (1/1 documented): ✔ `C_LIST`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `countIsCListPlusNone()` | — | ✔ |
| `noneIsFirstAndEmpty()` | — | ✔ |
| `matchesCList()` | — | ✔ |
| `maxIsLastAndNull()` | — | ✔ |
| `renamedKeys()` | — | ✔ |


##### `StatsTest.java`

**Responsible for:** Tests Stats' index and name lookups against C's originals.

**class `StatsTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (2/2 documented): ✔ `REAL_STATS`, `REAL_NAMES`

**class `StatsTest.Indices`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `realStatsCarryTheirIndices()` | — | ✔ |
| `maxCountsTheRealStats()` | — | ✔ |
| `noneCarriesMinusOne()` | — | ✔ |
| `sentinelsDoNotCollide()` | — | ✔ |

**class `StatsTest.ByIndex`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eachIndexAnswersItsStat()` | — | ✔ |
| `unknownIndexIsNull()` | — | ✔ |
| `invertsGetValue()` | — | ✔ |

**class `StatsTest.Names`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareNames()` | — | ✔ |
| `namesResolve()` | — | ✔ |
| `roundTripCloses()` | — | ✔ |
| `caseDoesNotMatter()` | — | ✔ |
| `maxResolves()` | — | ✔ |
| `noneDoesNotResolve()` | — | ✔ |
| `unrecognisedNamesAreNull()` | — | ✔ |

**class `StatsTest.LocaleIndependence`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `original`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `restoreLocale()` | — | ✔ |
| `turkishLocaleDoesNotBreakTheLookup()` | — | ✔ |
| `ordinaryLocaleStillWorks()` | — | ✔ |


##### `TrapEnumTest.java`

**Responsible for:** Checks TrapEnum against list-trap-flags.h in the C original.

**class `TrapEnumTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (1/2 documented): ✔ `C_LIST` — ✘ `C_HEADER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchesCTable()` | — | ✘ |
| `maxSentinel()` | — | ✘ |
| `magicalDescription()` | — | ✘ |
| `matchesRealHeader()` | — | ✘ |
| `valueOfPrefix()` | — | ✘ |


## middle/game

##### `GameWorldSetCharacterDungeonTest.java`

**Responsible for:** Tests GameWorld#setCharacterDungeon(boolean) - the port of the direct assignments C makes to its global character_dungeon (bool, game-world.c:47) at every call site (true at generate.c:1549 and load.c:1541; false at generate.c:1123, player-birth.c:1066, init.c:4529 and main-stats.c:1707), since C...

**class `GameWorldSetCharacterDungeonTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (1/1 documented): ✔ `savedCharacterDungeon`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveCharacterDungeon()` | — | ✔ |
| `restoreCharacterDungeon()` | — | ✔ |
| `settingTrueIsReadableBackAsTrue()` | — | ✔ |
| `settingFalseIsReadableBackAsFalse()` | — | ✔ |
| `secondWriteReplacesIndependentlyOfPrior()` | — | ✔ |
| `repeatingSameValueIsOrdinaryWrite()` | — | ✔ |


##### `GameWorldTest.java`

**Responsible for:** Tests the clock and per-turn machinery in GameWorld, the port of C's game-world.c.

**class `GameWorldTest`** — class Javadoc: ✔ · type stamp: on 261006

- Fields (15/15 documented): ✔ `C_EXTRACT_ENERGY`, `savedConstants`, `savedTurn`, `savedPlayer`, `savedCave`, `savedBus`, `savedDayCount`, `savedCharacterDungeon`, `savedTimedEffects`, `savedUtilsPlayer`, `savedQueue`, `player`, `level`, `world`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(Class<?>, String)` | — | ✔ |
| `shippedConstants()` | — | ✔ |
| `alterWorld(UnaryOperator<WorldData>)` | — | ✔ |
| `grade(int, int, String)` | — | ✔ |
| `effect(TimedEffect, int, TimedGrade)` | — | ✔ |
| `timedEffects()` | — | ✔ |
| `call(Object, String, Class<?>[], Object)` | — | ✔ |
| `turnEnergy(int)` | — | ✔ |
| `race(MonsterRaceFlag)` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `run(String)` | — | ✔ |
| `soundOn()` | — | ✔ |
| `monster(int, MonsterRace, MonsterFlag)` | — | ✔ |
| `scripted(int, int)` | — | ✔ |
| `eventsOf(GameEventType)` | — | ✔ |

**class `GameWorldTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `types`, `sounds`, `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `count(GameEventType)` | — | ✔ |

**class `GameWorldTest.ScriptedQueue`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `scriptedPlayer`, `repeats`, `uses`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ScriptedQueue(Player, int, int)` | — | ✔ |
| `commandPop(CommandContext)` | — | ✘ (override) |
| `getNrepeats()` | — | ✘ (override) |

**class `GameWorldTest.TurnEnergy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everySpeedMatchesTheTable()` | — | ✔ |
| `landmarkFigures()` | — | ✔ |
| `integerDivisionTruncates()` | — | ✔ |
| `largerMoveEnergyScalesUp()` | — | ✔ |
| `outOfRangeThrows()` | — | ✔ |

**class `GameWorldTest.IsDaytime`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `boundaries()` | — | ✔ |
| `largestTurn()` | — | ✔ |
| `shorterDay()` | — | ✔ |

**class `GameWorldTest.Construction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constructorZeroesDayCount()` | — | ✔ |
| `gameStateForwards()` | — | ✔ |
| `cachesPlayerAndCave()` | — | ✔ |
| `leavesCharacterDungeonAlone()` | — | ✔ |

**class `GameWorldTest.PlayAmbientSound`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `townDayAndNight()` | — | ✔ |
| `dungeonBands()` | — | ✔ |
| `dungeonIgnoresTimeOfDay()` | — | ✔ |
| `silentWhenOptionOff()` | — | ✔ |

**class `GameWorldTest.ProcessPlayerCleanup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `energyIsDeductedAndTotalled()` | — | ✔ |
| `noEnergyUsedLeavesFiguresAlone()` | — | ✔ |
| `skipCounterCountsDown()` | — | ✔ |
| `skipCounterResetToOne()` | — | ✔ |
| `dropStatusAlwaysReset()` | — | ✔ |
| `showAlwaysCleared()` | — | ✔ |
| `niceClearedOnlyOnTheFullArm()` | — | ✔ |
| `markDroppedOnlyWhenNotShown()` | — | ✔ |
| `markUntouchedWithoutEnergy()` | — | ✔ |
| `multiHuedMonstersShimmer()` | — | ✔ |
| `hallucinationRedrawsMap()` | — | ✔ |

**class `GameWorldTest.ProcessWorld`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resting()` | — | ✔ |
| `dungeonBanksADay()` | — | ✔ |
| `townBanksNothing()` | — | ✔ |
| `followsStoreTurns()` | — | ✔ |
| `townAnnouncesDawnAndDusk()` | — | ✔ |
| `dungeonHasNoSun()` | — | ✔ |
| `ambientEveryQuarterDay()` | — | ✔ |

**class `GameWorldTest.ProcessWorldFood`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fedAndResting()` | — | ✔ |
| `setSpeed(int)` | — | ✔ |
| `stateHas(ObjectFlag)` | — | ✔ |
| `normalDigestion()` | — | ✔ |
| `speedScalesDigestion()` | — | ✔ |
| `regenAndSlowDigest()` | — | ✔ |
| `slowDigestionAlone()` | — | ✔ |
| `minimalDigestion()` | — | ✔ |
| `gorgedDigestion()` | — | ✔ |
| `gorgedBoundary()` | — | ✔ |
| `fastMetabolism()` | — | ✔ |
| `healingSurvivesExactlyHungry()` | — | ✔ |
| `recallUp()` | — | ✔ |
| `recallDown()` | — | ✔ |
| `recallSuspendedInArena()` | — | ✔ |
| `noRecall()` | — | ✔ |
| `deepDescent()` | — | ✔ |
| `deepDescentWithStairSkip()` | — | ✔ |
| `deepDescentAtTheBottom()` | — | ✔ |
| `queuedCommands()` | — | ✔ |
| `updateFlags()` | — | ✔ |

**class `GameWorldTest.Recharge`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rod(int, int)` | — | ✔ |
| `other(TValue, int)` | — | ✔ |
| `carry(ItemObject)` | — | ✔ |
| `wear(ItemObject)` | — | ✔ |
| `drop(ItemObject)` | — | ✔ |
| `combineRaised()` | — | ✔ |
| `notifyOn()` | — | ✔ |
| `notice(ItemObject, boolean)` | — | ✔ |
| `carriedRodTicksQuietly()` | — | ✔ |
| `finishedRodRaisesFlags()` | — | ✔ |
| `carriedNonRodIsLeftAlone()` | — | ✔ |
| `wornItemRecharges()` | — | ✔ |
| `wornItemTicksQuietly()` | — | ✔ |
| `kindlessItemIsSkipped()` | — | ✔ |
| `floorRodsTick()` | — | ✔ |
| `noticeSilentByDefault()` | — | ✔ |
| `inscriptionRequestsNotice()` | — | ✔ |
| `optionAnnouncesEverything()` | — | ✔ |
| `wordingCases()` | — | ✔ |
| `singleItemIgnoresAll()` | — | ✔ |
| `artifactStackUsesStackWording()` | — | ✔ |
| `artifactInstance()` | — | ✔ |

**class `GameWorldTest.DecreaseTimeouts`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `ADJ_CON_FIX`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `conIndex(int)` | — | ✔ |
| `stateHas(PlayerFlag)` | — | ✔ |
| `ordinaryEffect()` | — | ✔ |
| `foodIsLeftAlone()` | — | ✔ |
| `conRecoveryRate()` | — | ✔ |
| `mortalWound()` | — | ✔ |
| `rockPlayer()` | — | ✔ |
| `curseCountsDown()` | — | ✔ |
| `curseFiresAndRerolls()` | — | ✔ |
| `dormantCurse()` | — | ✔ |
| `zeroTimeoutGoesNegative()` | — | ✔ |
| `carriedItemIsIgnored()` | — | ✔ |
| `everySlot()` | — | ✔ |
| `curse()` | — | ✔ |
| `wearCursed(Curse, CurseData)` | — | ✔ |

**class `GameWorldTest.NoiseAndScent`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerAt(int, int)` | — | ✔ |
| `terrain(TerrainFeatureFlags)` | — | ✔ |
| `terrainAt(int, int, Feature)` | — | ✔ |
| `read(uk.co.jackoftradesltd.middle.cave.Heatmap)` | — | ✔ |
| `assertGrid(int[][], uk.co.jackoftradesltd.middle.cave.Heatmap)` | — | ✔ |
| `openNoise()` | — | ✔ |
| `noiseThroughAGap()` | — | ✔ |
| `coveredTracksNoise()` | — | ✔ |
| `freshScent()` | — | ✔ |
| `noScentTerrain()` | — | ✔ |
| `secondPassSameGrid()` | — | ✔ |
| `movingLeavesATrail()` | — | ✔ |
| `coveredTracksScent()` | — | ✔ |

**class `GameWorldTest.LevelChange`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `arrivalSignals()` | — | ✔ |
| `energyFloor()` | — | ✔ |
| `energyFloorInTown()` | — | ✔ |
| `arenaKeepsEnergy()` | — | ✔ |
| `ambientSoundOnRealArrival()` | — | ✔ |
| `arenaStillSignals()` | — | ✔ |
| `maxLevelTracking()` | — | ✔ |
| `maxDepthTracking()` | — | ✔ |
| `equalDepthLeavesRecall()` | — | ✔ |
| `leaveFlushes()` | — | ✔ |

**class `GameWorldTest.ProcessPlayer`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playing()` | — | ✔ |
| `interruptCheckOnce()` | — | ✔ |
| `assumesFreeTurn()` | — | ✔ |
| `energySpendingCommand()` | — | ✔ |
| `stoppedGameSkipsCleanup()` | — | ✔ |
| `paralysisQueuesSleep()` | — | ✔ |
| `knockedOutQueuesSleep()` | — | ✔ |
| `repeatSignalsRepeat()` | — | ✔ |
| `monsterRecallRedraw()` | — | ✔ |
| `repeatSkipsRecall()` | — | ✔ |
| `queued()` | — | ✔ |

**class `GameWorldTest.RunGameLoop`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ready()` | — | ✔ |
| `energyToSpare()` | — | ✔ |
| `clockRunsUntilEnergyBanked()` | — | ✔ |
| `fasterPlayerWaitsLess()` | — | ✔ |
| `leftoverShortensWait()` | — | ✔ |
| `worldProcessedOnTens()` | — | ✔ |
| `startIsProcessedEndIsNot()` | — | ✔ |
| `stoppedGame()` | — | ✔ |
| `noEnergyHandsBack()` | — | ✔ |
| `levelChangeKeepsClock()` | — | ✔ |
| `leavesOldLevelOnlyIfThereIsOne()` | — | ✔ |
| `leavingTheArena()` | — | ✔ |
| `notTheArena()` | — | ✔ |


##### `HintTest.java`

**Responsible for:** Tests for Hint, the port of C struct hint (hint.h): one line of text, with the C next pointer replaced by list membership.

**class `HintTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getHintReturnsConstructorTextVerbatim()` | — | ✔ |
| `trailingWhitespaceIsPreserved()` | — | ✔ |
| `emptyTextIsKept()` | — | ✔ |
| `assemblerKeepsFileOrderAndDuplicates()` | — | ✔ |


##### `NameCreatorTest.java`

**Responsible for:** Tests the one property of NameCreator.randnameMake — C's randname_make in randname.c — that the older PlayerNameRandnameMakeTest cannot tell apart from a simpler generator: the context is the pair of preceding letters, not the last letter alone.

**class `NameCreatorTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (1/1 documented): ✔ `MANY`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `sharedMiddleLetterDoesNotMix()` | — | ✔ |


##### `NameTest.java`

**Responsible for:** Tests for Name, the per-section building block C assembles into name_sections in finish_parse_names (init.c).

**class `NameTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getSectionReturnsRawValue()` | — | ✘ |
| `getSectionAcceptsZero()` | — | ✘ |
| `getSectionAcceptsOutOfRange()` | — | ✘ |
| `getWordPreservesFileOrder()` | — | ✘ |
| `getWordEmptyForNoWords()` | — | ✘ |
| `getWordIsLiveUnmodifiableView()` | — | ✘ |


## middle/game/enums

##### `CommandArgumentTypeTest.java`

**Responsible for:** Tests for CommandArgumentType, the port of enum cmd_arg_type in cmd-core.h.

**class `CommandArgumentTypeTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `orderMatchesC()` | — | ✘ |
| `ordinalsMatchCValues()` | — | ✘ |
| `countMatchesC()` | — | ✘ |
| `namesRoundTrip()` | — | ✘ |


##### `CommandCodeTest.java`

**Responsible for:** Tests for CommandCode, the port of enum cmd_code in cmd-core.h.

**class `CommandCodeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `C_HEADER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `countMatchesC()` | — | ✘ |
| `spotOrdinalsMatchC()` | — | ✘ |
| `matchesCHeader()` | — | ✘ |
| `dispatchRowsMatchGameCmds()` | — | ✘ |


##### `CommandContextTest.java`

**Responsible for:** Tests for CommandContext, the port of enum cmd_context in cmd-core.h, where CTX_INIT is 0 and the rest follow in sequence.

**class `CommandContextTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `orderMatchesC()` | — | ✘ |
| `ordinalsMatchCValues()` | — | ✘ |
| `count()` | — | ✘ |


##### `CommandReturnCodesTest.java`

**Responsible for:** Tests for CommandReturnCodes, the port of enum cmd_return_codes in cmd-core.h, where CMD_OK is 0 and the three failures are -1, -2 and -3.

**class `CommandReturnCodesTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `valuesMatchC()` | — | ✘ |
| `count()` | — | ✘ |
| `roundTrip()` | — | ✘ |
| `fromValueKnown()` | — | ✘ |
| `fromValueOutOfRange()` | — | ✘ |


## middle/game/event

##### `EventDataMissileTest.java`

**Responsible for:** Unit tests for EventDataMissile, the port of the missile member of game_event_data (game-event.h) as filled by event_signal_missile() (game-event.c): the four fields obj, seen, y, x.

**class `EventDataMissileTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fieldsComeBackInSignalOrder()` | — | ✘ |
| `unseenIsPreserved()` | — | ✘ |
| `objectIsHeldByReferenceNotCopied()` | — | ✘ |
| `nullObjectIsAccepted()` | — | ✘ |
| `negativeAndLargeCoordinatesPassThrough()` | — | ✘ |


##### `EventsBusHandlerTest.java`

**Responsible for:** Unit tests for EventsBusHandler, the port of C's game event bus (src/game-event.c) — a registry of handlers keyed by event type, and a dispatch that calls every handler registered for the type being signalled.

**class `EventsBusHandlerTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `aRegisteredHandlerReceivesItsEvent()` | — | ✔ |
| `aHandlerHearsOnlyTheEventItRegisteredFor()` | — | ✔ |
| `everyHandlerOnAnEventFiresInRegistrationOrder()` | — | ✔ |
| `signallingAnEventWithNoHandlersDoesNothing()` | — | ✔ |
| `aNullPayloadIsDeliveredRatherThanFiltered()` | — | ✔ |
| `aRemovedHandlerStopsReceivingItsEvent()` | — | ✔ |
| `clearingOneEventTypeLeavesTheOthersAlone()` | — | ✔ |
| `removingAllHandlersClearsEveryEventType()` | — | ✔ |
| `aFreshBusHasNoSubscriptions()` | — | ✔ |
| `addingAHandlerToASetRegistersItForEveryTypeInTheSet()` | — | ✔ |
| `removingAHandlerFromASetUnregistersItEverywhereInTheSet()` | — | ✔ |
| `registeringTheSameHandlerTwiceCallsItTwice()` | — | ✔ |
| `aHandlerMayRegisterAnotherWhileBeingDispatchedTo()` | — | ✔ |

**class `EventsBusHandlerTest.RecordingHandler`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `types`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `calls()` | — | ✘ |


##### `EventsHandlerSignalTest.java`

**Responsible for:** Unit tests for the eventSignal* family on EventsHandler — the port of C's event_signal_* functions (src/game-event.c).

**class `EventsHandlerSignalTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `handler`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `aBareSignalCarriesTheEventTypeAndNoPayload()` | — | ✔ |
| `aFlagSignalWrapsItsBoolean()` | — | ✘ |
| `aStringSignalWrapsItsText()` | — | ✘ |
| `aStringsSignalWrapsItsTextInOrder()` | — | ✔ |
| `aMessageSignalWrapsItsTypeAndText()` | — | ✘ |
| `aPointSignalCrossesFromXyToRowColumn()` | — | ✔ |
| `bothPointOverloadsAgreeOnTheSameSquare()` | — | ✔ |
| `aNullPointIsRejectedAndNothingIsDispatched()` | — | ✔ |
| `aBlastSignalWithANullGridOrCentreIsRejectedAndNothingIsDispatched()` | — | ✔ |
| `blast(ArrayList<Loc>, Loc)` | — | ✘ |
| `aStatSignalWrapsCurrentAndOtherInOrder()` | — | ✔ |
| `aFullStatSignalWrapsIndexCurrentMaxAndUseInOrder()` | — | ✔ |
| `anIntSignalWrapsItsValue()` | — | ✔ |
| `aSizeSignalKeepsHeightFirst()` | — | ✔ |
| `aTunnelSignalKeepsItsCountsInOrder()` | — | ✔ |
| `aBoltSignalPassesEveryComponentThrough()` | — | ✔ |
| `aColourStringSignalWrapsItsArray()` | — | ✔ |
| `theEventTypeIsPassedThroughAlongsideThePayload()` | — | ✔ |

**class `EventsHandlerSignalTest.CapturingHandler`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `lastType`, `lastData`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


## middle/game/event/eventhandlers

##### `EnterInitWiringTest.java`

**Responsible for:** The whole core-side path, asserted end to end on the real transport: a bus signal from where GameConstants.init() raises it, through InitHandlers, onto a real Channels queue - and read back off the UI half's inbox exactly as UILoop would.

**class `EnterInitWiringTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `bus`, `channels`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `initSignalsArriveOnTheUiInboxAsMessages()` | — | ✔ |
| `unsubscribedEventsDoNotReachTheChannel()` | — | ✔ |
| `drain(int)` | — | ✔ |
| `drainAvailable()` | — | ✔ |


##### `InitHandlersTest.java`

**Responsible for:** Tests the start-up chain InitHandlers owns: bus signal, to handler, to the sending end of the core channel.

**class `InitHandlersTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `bus`, `sender`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `enterInitThenNotesReachTheSender()` | — | ✔ |
| `messagesCarryTheEventThatWasSignalled()` | — | ✔ |
| `noteBeforeEnterInitIsLost()` | — | ✔ |
| `bareEnterInitSignalSendsNothing()` | — | ✔ |
| `bareNoteSignalSendsNothing()` | — | ✔ |
| `everyPhaseTransitionIsForwarded()` | — | ✔ |
| `aForwardedTransitionCarriesItsOwnEvent()` | — | ✔ |

**class `InitHandlersTest.RecordingSender`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `sent`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `send(CoreMessage)` | — | ✘ (override) |


##### `RedrawHandlersTest.java`

**Responsible for:** Tests the redraw chain RedrawHandlers owns: bus signal, to handler, to the sending end of the core channel.

**class `RedrawHandlersTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `bus`, `sender`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `hpSignalReachesTheSenderAsAGameEventCoreMessage()` | — | ✔ |
| `bareHpSignalSendsNothing()` | — | ✔ |
| `manaSignalReachesTheSenderAsAGameEventCoreMessage()` | — | ✔ |
| `bareManaSignalSendsNothing()` | — | ✔ |
| `titleSignalReachesTheSenderAsAGameEventCoreMessage()` | — | ✔ |
| `bareTitleSignalSendsNothing()` | — | ✔ |
| `nameSignalReachesTheSenderAsAGameEventCoreMessage()` | — | ✔ |
| `bareNameSignalSendsNothing()` | — | ✔ |
| `raceClassSignalReachesTheSenderAsAGameEventCoreMessage()` | — | ✔ |
| `bareRaceClassSignalSendsNothing()` | — | ✔ |

**class `RedrawHandlersTest.RecordingSender`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `sent`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `send(CoreMessage)` | — | ✘ (override) |


## middle/game/event/projection

##### `ProjectionTest.java`

**Responsible for:** Tests Projection, the Java form of struct projection in project.h.

**class `ProjectionTest`** — class Javadoc: ✔ · type stamp: on 261001

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `acid()` | on 261001 | ✔ |
| `light()` | on 261001 | ✔ |
| `field(Projection, String)` | on 261001 | ✔ |
| `acidKeepsEveryMember()` | on 261001 | ✔ |
| `acidHasNoDiceDenominator()` | on 261001 | ✔ |
| `lightKeepsEveryMember()` | on 261001 | ✔ |
| `lightDiceDenominatorIsKeptAndIntegerIsZero()` | on 261001 | ✔ |
| `diceDenominatorIsStoredByReference()` | on 261001 | ✔ |
| `flagsAreIndependent()` | on 261001 | ✔ |
| `unsetRecordKeepsCZeroValues()` | on 261001 | ✔ |
| `toStringListsTheFields()` | on 261001 | ✔ |


##### `SourceTest.java`

**Responsible for:** Tests the six factory methods on Source, the port of C's source.c.

**class `SourceTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `monster()` | — | ✘ |
| `chestTrap()` | — | ✘ |

**class `SourceTest.PayloadFree`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sourceNone()` | — | ✘ |
| `sourcePlayer()` | — | ✘ |
| `noneIsNotPlayer()` | — | ✘ |

**class `SourceTest.WithPayload`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sourceTrap()` | — | ✘ |
| `sourceMonster()` | — | ✘ |
| `sourceObject()` | — | ✘ |
| `sourceChestTrap()` | — | ✘ |
| `trapIsNotChestTrap()` | — | ✘ |

**class `SourceTest.NullMonster`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullMonsterIsStillAMonsterSource()` | — | ✘ |
| `nullMonsterIsNotSourceNone()` | — | ✘ |

**class `SourceTest.Discriminant`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchC()` | — | ✘ |


##### `SourceWhichTest.java`

**Responsible for:** Tests the SourceWhich hierarchy, the port of the anonymous which union inside struct source in C's source.h.

**class `SourceWhichTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fourPermittedRecords()` | — | ✘ |
| `allRecords()` | — | ✘ |
| `keepsReference()` | — | ✘ |
| `nullPayloads()` | — | ✘ |
| `differentKindsNotEqual()` | — | ✘ |
| `sameKindEqual()` | — | ✘ |
| `exhaustiveSwitch()` | — | ✘ |


## middle/game/gameengine

##### `CommandArgumentDataTest.java`

**Responsible for:** Tests for CommandArgumentData, the port of C's union cmd_arg_data and the enum cmd_arg_type tag that travels with it (cmd-core.h).

**class `CommandArgumentDataTest`** — class Javadoc: ✔ · type stamp: on 261012

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sevenPermittedVariants()` | — | ✘ |
| `typeTagPerVariant()` | — | ✘ |
| `typeEnumOrdinalsMatchC()` | — | ✘ |
| `tagsAreDistinctAndNeverNone()` | — | ✘ |
| `directionAndTargetStayDistinct()` | — | ✘ |
| `stringCopy()` | — | ✘ |
| `stringCopyOfNull()` | — | ✘ |
| `targetCopy()` | — | ✘ |
| `copyKeepsVariantAndTag()` | — | ✘ |
| `itemCopySharesTheObject()` | — | ✘ |
| `itemCopyOfNull()` | — | ✘ |
| `pointCopyIsIndependent()` | — | ✘ |


##### `CommandArgumentTest.java`

**Responsible for:** Tests for CommandArgument, the port of C's struct cmd_arg (cmd-core.h).

**class `CommandArgumentTest`** — class Javadoc: ✔ · type stamp: on 261001

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `number(int, String)` | — | ✘ |
| `constructorStoresFields()` | — | ✘ |
| `updateReplacesAll()` | — | ✘ |
| `updateNullsKeepFields()` | — | ✘ |
| `copyIsIndependent()` | — | ✘ |
| `longNameIsKept()` | — | ✘ |


##### `CommandGetterHolderTest.java`

**Responsible for:** Tests for CommandGetterHolder, the Java home of C's file-scope errr (*cmd_get_hook)(cmd_context c) (declared in cmd-core.h, defined in cmd-core.c).

**class `CommandGetterHolderTest`** — class Javadoc: ✔ · type stamp: on 261014

**class `CommandGetterHolderTest.RecordingGetter`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (0/4 documented): ✘ `status`, `lastContext`, `lastQueue`, `calls`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingGetter(int)` | — | ✘ |
| `getCommand(CommandContext, CommandQueue)` | — | ✘ (override) |
| `startUnset()` | — | ✘ |
| `restoreDefault()` | — | ✘ |
| `startsAsDefault()` | — | ✘ |
| `defaultReportsFailureForEveryContext()` | — | ✘ |
| `setInstanceInstalls()` | — | ✘ |
| `installedGetterIsCalledThrough()` | — | ✘ |
| `nonZeroStatusPassesThrough()` | — | ✘ |
| `secondInstallReplacesFirst()` | — | ✘ |
| `nullIsRejectedAndInstanceKept()` | — | ✘ |
| `nullOnFreshHolderKeepsDefault()` | — | ✘ |
| `resetRestoresDefault()` | — | ✘ |
| `lockedDown()` | — | ✘ |


##### `CommandNullAndFiveTest.java`

**Responsible for:** Tests for the two places Command had to be made to follow C's integer view of directions and pointers: the value 5, and stored nulls.

**class `CommandNullAndFiveTest`** — class Javadoc: ✔ · type stamp: on 261012

- Fields (0/9 documented): ✘ `player`, `realPlayer`, `realQueue`, `cmd`, `prompts`, `repDir`, `aimDir`, `userString`, `userItem`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `liveGridTarget()` | — | ✔ |

**class `CommandNullAndFiveTest.Direction`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedNone()` | — | ✘ |
| `storedTarget()` | — | ✘ |
| `promptedReplacesStored()` | — | ✘ |
| `storedUnknown()` | — | ✘ |
| `storedRealDirections()` | — | ✘ |

**class `CommandNullAndFiveTest.TargetCode`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedUnknown()` | — | ✘ |
| `staleTargetCode()` | — | ✘ |
| `staleNoneCode()` | — | ✘ |
| `liveTargetCode()` | — | ✘ |
| `liveNoneCode()` | — | ✘ |
| `storedRealDirection()` | — | ✘ |
| `abortedAim()` | — | ✘ |

**class `CommandNullAndFiveTest.RefusedNull`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `directionSetter()` | — | ✘ |
| `targetSetter()` | — | ✘ |
| `directionOverwriteKeepsOld()` | — | ✘ |
| `targetOverwriteKeepsOld()` | — | ✘ |
| `everyConstantAccepted()` | — | ✘ |

**class `CommandNullAndFiveTest.AcceptedNull`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullItemEmpty()` | — | ✘ |
| `resetItemToNull()` | — | ✘ |
| `nullItemPrompts()` | — | ✘ |
| `nullStringEmpty()` | — | ✘ |
| `emptyStringPresent()` | — | ✘ |
| `nullOverwritesString()` | — | ✘ |
| `nullStringPrompts()` | — | ✘ |
| `nullsSurviveClone()` | — | ✘ |
| `nullKeepsItsType()` | — | ✘ |


##### `CommandProcessorTest.java`

**Responsible for:** Tests that CommandProcessor's static command table is actually populated, and that a representative sample of its rows carry the flags the C game_cmds[] table assigns them.

**class `CommandProcessorTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `table()` | — | ✔ |
| `tableIsPopulated()` | — | ✔ |
| `representativeCommandsPresent()` | — | ✔ |
| `keysMatchTheirCommandInfo()` | — | ✔ |
| `flagsMatchCTable()` | — | ✔ |
| `enumOnlyCodesAreAbsent()` | — | ✔ |
| `primePlayer()` | — | ✔ |
| `nonRepeatableCommandClearsNrepeats()` | — | ✔ |
| `processCommandSetsTheContext()` | — | ✔ |
| `repeating(CommandQueue)` | — | ✔ |
| `openCommand(int)` | — | ✔ |
| `autoRepeatArmsFromTheCommandTable()` | — | ✔ |
| `autoRepeatCountsDownThenTerminates()` | — | ✔ |
| `executeTerminatesOnAnAutoRepeatCommand()` | — | ✔ |


##### `CommandSpellItemRepeatTest.java`

**Responsible for:** Tests for the parts of Command that CommandTest leaves out: cmd_get_spell, the shapechange mask in cmd_get_item, the filter handling of a stored item, the repeat-cancel rule that separates cmd_get_direction from cmd_get_target, and what cmd_copy shares and does not share between two commands (cmd...

**class `CommandSpellItemRepeatTest`** — class Javadoc: ✔ · type stamp: on 261003

- Fields (0/12 documented): ✘ `ANY_SPELL`, `ANY_BOOK`, `ui`, `player`, `realPlayer`, `realQueue`, `cmd`, `spellA`, `spellB`, `spellC`, `spellD`, `book`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `spell(String)` | — | ✘ |
| `casterClass(MagicSpell, MagicSpell, MagicSpell, MagicSpell)` | — | ✔ |
| `classWith(ClassMagic)` | — | ✘ |
| `setClass(Player, PlayerClass)` | — | ✘ |
| `shape(String)` | — | ✘ |
| `everywhere()` | — | ✘ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `CommandSpellItemRepeatTest.ScriptedInput`** — class Javadoc: ✔ · type stamp: —

- Fields (0/9 documented): ✘ `picker`, `fromBook`, `item`, `repDir`, `aimDir`, `pickerCalls`, `fromBookCalls`, `itemCalls`, `modeSeen`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getSpell(Player, String, Predicate<ItemObject>, CommandCode, String, BiPredicate<Player,MagicSpell>, String, ItemObject)` | — | ✘ (override) |
| `getSpellFromBook(Player, String, ItemObject, String, BiPredicate<Player,MagicSpell>)` | — | ✘ (override) |
| `getItem(String, String, CommandCode, Predicate<ItemObject>, Flag<GetItemFlags>)` | — | ✘ (override) |
| `getRepDir(boolean)` | — | ✘ (override) |
| `getAimDir()` | — | ✘ (override) |

**class `CommandSpellItemRepeatTest.Spells`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedPasses()` | — | ✘ |
| `storedIndexCrossesBooks()` | — | ✘ |
| `storedRejectedByFilter()` | — | ✘ |
| `storedOutOfRange()` | — | ✘ |
| `bookQueued()` | — | ✘ |
| `bookQueuedAbort()` | — | ✘ |
| `pickerAbort()` | — | ✘ |
| `nonCaster()` | — | ✘ |
| `spellNotInClass()` | — | ✘ |

**class `CommandSpellItemRepeatTest.Items`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedPasses()` | — | ✘ |
| `nullFilterAccepts()` | — | ✘ |
| `storedRejected()` | — | ✘ |
| `storedRejectedThenAbort()` | — | ✘ |
| `shapechangedMask()` | — | ✘ |
| `normalShapeUnmasked()` | — | ✘ |
| `promptedIsStored()` | — | ✘ |

**class `CommandSpellItemRepeatTest.Repeats`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `execute(Command)` | — | ✔ |
| `directionAbortCancels()` | — | ✘ |
| `directionAnswerKeeps()` | — | ✘ |
| `storedDirectionKeeps()` | — | ✘ |
| `targetAbortKeeps()` | — | ✘ |

**class `CommandSpellItemRepeatTest.Clone`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `itemShared()` | — | ✘ |
| `allTypesCopied()` | — | ✘ |
| `scalarsCopied()` | — | ✘ |
| `argListIndependent()` | — | ✘ |

**class `CommandSpellItemRepeatTest.Readers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wrongTypes()` | — | ✘ |
| `itemRoundTrip()` | — | ✘ |


##### `CommandTest.java`

**Responsible for:** Tests for Command: the by-name argument store, and the "queued argument first, else prompt" getters (cmd_get_direction, cmd_get_target, cmd_get_quantity, cmd_get_string, cmd_get_item, cmd_get_effect_from_list) of cmd-core.c.

**class `CommandTest`** — class Javadoc: ✔ · type stamp: on 261001

- Fields (0/5 documented): ✘ `ui`, `player`, `realPlayer`, `realQueue`, `cmd`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `CommandTest.ScriptedInput`** — class Javadoc: ✔ · type stamp: —

- Fields (0/11 documented): ✘ `repDir`, `aimDir`, `quantity`, `string`, `item`, `effect`, `prompts`, `allow5Seen`, `initialSeen`, `modeSeen`, `maxSeen`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getRepDir(boolean)` | — | ✘ (override) |
| `getAimDir()` | — | ✘ (override) |
| `getQuantity(String, int)` | — | ✘ (override) |
| `getString(String, String)` | — | ✘ (override) |
| `getItem(String, String, CommandCode, Predicate<uk.co.jackoftradesltd.middle.objects.ItemObject>, Flag<GetItemFlags>)` | — | ✘ (override) |
| `getEffectFromList(String, List<Effect>, int, boolean)` | — | ✘ (override) |

**class `CommandTest.Store`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `roundTrip()` | — | ✘ |
| `notPresent()` | — | ✘ |
| `wrongType()` | — | ✘ |
| `overwrite()` | — | ✘ |
| `cloneIsIndependent()` | — | ✘ |

**class `CommandTest.Direction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `stored()` | — | ✘ |
| `storedNoneRePrompts()` | — | ✘ |
| `abort()` | — | ✘ |

**class `CommandTest.Target`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unknownRePrompts()` | — | ✘ |
| `stored()` | — | ✘ |
| `abort()` | — | ✘ |

**class `CommandTest.Quantity`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedNotChecked()` | — | ✘ |
| `prompted()` | — | ✘ |
| `promptedZero()` | — | ✘ |
| `uiAbort()` | — | ✘ |

**class `CommandTest.Strings`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `stored()` | — | ✘ |
| `emptyIsValid()` | — | ✘ |
| `abort()` | — | ✘ |
| `truncation()` | — | ✘ |
| `nullInitial()` | — | ✘ |

**class `CommandTest.Items`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `abort()` | — | ✘ |
| `modePassedThrough()` | — | ✘ |

**class `CommandTest.Effects`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `three`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedIndex()` | — | ✘ |
| `storedRandom()` | — | ✘ |
| `storedOutOfRange()` | — | ✘ |
| `explicitCount()` | — | ✘ |
| `promptedStored()` | — | ✘ |
| `promptedInvalid()` | — | ✘ |
| `uiAbort()` | — | ✘ |


##### `CoreHandleChannelOutputTest.java`

**Responsible for:** Core#handleChannelOutput(UIMessage) on its own: one message in, a verdict out, and whatever the core said back left on the front end's inbox.

**class `CoreHandleChannelOutputTest`** — class Javadoc: ✔ · type stamp: on 261010

- Fields (2/4 documented): ✔ `NO_OPTIONS`, `SENTINEL` — ✘ `channels`, `core`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `firstThingOnTheFrontEndsInbox()` | — | ✔ |
| `saveAndStopSendsStoppedAndSaysToEnd()` | — | ✔ |
| `startIsNotAStopAndSendsNothing()` | — | ✔ |
| `windowCloseRequestedIsIgnored()` | — | ✔ |
| `simpleUiMessagesAreIgnoredWhateverTheirType()` | — | ✔ |
| `aLateUiEntriesLoadedIsIgnored()` | — | ✔ |
| `eachCallIsJudgedOnItsOwn()` | — | ✔ |
| `getCoreChannelReturnsTheChannelItWasBuiltWith()` | — | ✔ |


##### `CoreTest.java`

**Responsible for:** Stage 3's core half: Core#gameLoop() as a receive loop, and the reply that ends it.

**class `CoreTest`** — class Javadoc: ✔ · type stamp: —

- Fields (5/7 documented): ✔ `LOAD_AND_REPLY_MILLIS`, `STILL_RUNNING_MILLIS`, `CORE_THREAD`, `NO_OPTIONS`, `coreThread` — ✘ `channels`, `runner`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `startCore()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `requestStop()` | — | ✔ |
| `poll(long)` | — | ✔ |
| `awaitStopped()` | — | ✔ |
| `awaitThreadEnd()` | — | ✔ |
| `coreThreadIsAlive()` | — | ✔ |
| `saveAndStopIsAnsweredWithStoppedAndThenTheThreadEnds()` | — | ✔ |
| `startDoesNotEndTheLoop()` | — | ✔ |
| `aMisroutedWindowEventIsIgnored()` | — | ✔ |


##### `GameEngineBootstrapTest.java`

**Responsible for:** Runs the game's start-up once, against the real data files, and checks what it leaves behind — the port of C's init_angband (init.c) reached through GameEngine#getGame.

**class `GameEngineBootstrapTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `savedInstance`, `savedConstants`, `savedPlayer`, `savedCave`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveGlobals()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `engineField()` | — | ✔ |
| `constantsField()` | — | ✔ |
| `caveProfileCount()` | — | ✔ |
| `buildingDoesNotLoad()` | — | ✔ |
| `loadingReadsTheDataFiles()` | — | ✔ |
| `secondCallReturnsTheSameEngine()` | — | ✔ |
| `constantsMatchTheFile()` | — | ✔ |
| `objectRegistryIsFilled()` | — | ✔ |
| `powerTablesAreBuilt()` | — | ✔ |
| `otherRegistriesAreFilled()` | — | ✔ |
| `playerAndLevelExist()` | — | ✔ |
| `profileLoaderReloadsProfiles()` | — | ✔ |
| `flavoursLoadedButNotAttached()` | — | ✔ |
| `elementPowerTableMatchesC()` | — | ✔ |


##### `GameEngineLoadTest.java`

**Responsible for:** GameEngine's own contract: the two-step start-up, the event bus it owns, and the order in which GameEngine#loadGameConstants(Core) runs what C's init_angband() (src/init.c) runs.

**class `GameEngineLoadTest`** — class Javadoc: ✔ · type stamp: on 261010

- Fields (1/1 documented): ✔ `NO_OPTIONS`

**record `GameEngineLoadTest.Seen`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `type`, `text`, `playerBuilt`
- Fields (2/9 documented): ✔ `seen`, `firstLoadPlayer` — ✘ `savedInstance`, `savedConstants`, `savedBus`, `savedPlayer`, `savedCave`, `savedQueue`, `playerBeforeLoad`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadOnceWithRecorder()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `field(Class<?>, String)` | — | ✘ |
| `coreWithEntriesLoaded()` | — | ✔ |
| `enterInitIsTheFirstEventOfTheLoad()` | — | ✔ |
| `templateParsersAnnounceThemselvesLastAndInOrder()` | — | ✔ |
| `playerIsBuiltAfterTheArraysAndBeforeTheTemplates()` | — | ✔ |
| `loadLeavesPlayerLevelAndQueue()` | — | ✔ |
| `freshPlayerHasNoTimedEffects()` | — | ✔ |
| `vaultsWithNoMaximumGetTheWorldDepth()` | — | ✔ |
| `templateRegistriesAreFilled()` | — | ✔ |
| `secondLoadReplacesPlayerLevelAndQueue()` | — | ✔ |
| `stopRequestAbandonsTheLoadBeforeAnythingIsBuilt()` | — | ✔ |
| `busRoundTripsThroughTheSetter()` | — | ✔ |
| `buildingAnEngineInstallsAFreshBus()` | — | ✔ |


##### `GameStateGetCharacterGeneratedTest.java`

**Responsible for:** Tests GameState#getCharacterGenerated() - the port of reading C's global character_generated (game-world.h:36, game-world.c:46), which C reads directly at every call site (e.g.

**class `GameStateGetCharacterGeneratedTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (1/1 documented): ✔ `savedValue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setCharacterGenerated(boolean)` | — | ✔ |
| `saveValue()` | — | ✔ |
| `restoreValue()` | — | ✔ |
| `reportsTrueWhenFieldIsTrue()` | — | ✔ |
| `reportsFalseWhenFieldIsFalse()` | — | ✔ |
| `secondWriteReplacesFirst()` | — | ✔ |
| `doesNotDisturbTurn()` | — | ✔ |


##### `GameStateSeedFlavourTest.java`

**Responsible for:** Tests GameState#setSeedFlavour(long) and GameState#getSeedFlavour() - the port of the direct reads and writes C makes to its global seed_flavor (uint32_t, game-world.c), since C keeps no accessor functions for the global to diff against.

**class `GameStateSeedFlavourTest`** — class Javadoc: ✔ · type stamp: on 260908

- Fields (1/1 documented): ✔ `savedSeedFlavour`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveSeedFlavour()` | — | ✔ |
| `restoreSeedFlavour()` | — | ✔ |
| `storesValueUnchanged()` | — | ✔ |
| `zeroIsAnOrdinaryWrite()` | — | ✔ |
| `secondWriteReplacesIndependentlyOfPrior()` | — | ✔ |
| `topOfBirthRangeSurvives()` | — | ✔ |
| `uint32RangeSurvivesWithoutTruncation()` | — | ✔ |
| `doesNotDisturbTurn()` | — | ✔ |


##### `GameStateSetCharacterGeneratedTest.java`

**Responsible for:** Tests GameState#setCharacterGenerated(boolean) - the port of the direct assignments C makes to its global character_generated (game-world.h:36) at every call site (player-birth.c:1329, savefile.c:653, ui-game.c:721), since C keeps no single setter function for the global to diff against.

**class `GameStateSetCharacterGeneratedTest`** — class Javadoc: ✔ · type stamp: on 260908

- Fields (1/1 documented): ✔ `savedValue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveValue()` | — | ✔ |
| `restoreValue()` | — | ✔ |
| `storesTrueUnchanged()` | — | ✔ |
| `storesFalseUnchanged()` | — | ✔ |
| `secondWriteReplacesIndependentlyOfPrior()` | — | ✔ |
| `doesNotDisturbTurn()` | — | ✔ |


##### `GameStateSetTurnTest.java`

**Responsible for:** Tests GameState#setTurn(int) - the port of the direct assignments C makes to its global turn (int32_t, game-world.c) at every call site, since C keeps no single setter function for the global to diff against.

**class `GameStateSetTurnTest`** — class Javadoc: ✔ · type stamp: on 260903

- Fields (1/1 documented): ✔ `savedTurn`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveTurn()` | — | ✔ |
| `restoreTurn()` | — | ✔ |
| `storesValueUnchanged()` | — | ✔ |
| `zeroIsAnOrdinaryWrite()` | — | ✔ |
| `secondWriteReplacesIndependentlyOfPrior()` | — | ✔ |
| `int32RangeSurvives()` | — | ✔ |
| `doesNotDisturbDaycount()` | — | ✔ |


##### `GameStateTest.java`

**Responsible for:** Tests the members of GameState that the single-method test classes beside it do not cover: the turn clock (GameState#getTurn(), GameState#incrementTurn(), GameState#resetTurnFromSave(int)), the day-count forwarder, and the player, level and CommandQueue slots.

**class `GameStateTest`** — class Javadoc: ✔ · type stamp: on 261006

- Fields (4/4 documented): ✔ `savedTurn`, `savedPlayer`, `savedCave`, `savedQueue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newLevel()` | — | ✔ |
| `saveState()` | — | ✔ |
| `restoreState()` | — | ✔ |

**class `GameStateTest.IncrementTurn`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addsOne()` | — | ✔ |
| `accumulates()` | — | ✔ |
| `wrapsAtInt32Max()` | — | ✔ |
| `doesNotDisturbDaycount()` | — | ✔ |

**class `GameStateTest.ResetTurnFromSave`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesSavedValue()` | — | ✔ |
| `storesBoundaryValuesUnchanged()` | — | ✔ |
| `clockContinuesFromRestoredValue()` | — | ✔ |

**class `GameStateTest.GetDaycount`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsGameWorldDaycount()` | — | ✔ |

**class `GameStateTest.PlayerSlot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsSamePlayer()` | — | ✔ |
| `secondPlayerReplacesFirst()` | — | ✔ |
| `nullClearsSlot()` | — | ✔ |
| `doesNotDisturbNeighbours()` | — | ✔ |

**class `GameStateTest.LevelSlot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsSameLevel()` | — | ✔ |
| `secondLevelReplacesFirst()` | — | ✔ |
| `nullClearsSlot()` | — | ✔ |
| `doesNotDisturbNeighbours()` | — | ✔ |

**class `GameStateTest.CommandQueueSlot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesAndReplaces()` | — | ✔ |
| `nullClearsSlot()` | — | ✔ |


## middle/game/gameengine/argumentdata

##### `ArgumentChoiceTest.java`

**Responsible for:** Tests for ArgumentChoice, the port of the int choice member of C's union cmd_arg_data and its arg_CHOICE tag (cmd-core.h).

**class `ArgumentChoiceTest`** — class Javadoc: ✔ · type stamp: on 261012

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `valueRoundTrips()` | — | ✘ |
| `valueIsUnchecked()` | — | ✘ |
| `typeIsChoice()` | — | ✘ |
| `choiceIsNotNumber()` | — | ✘ |
| `copyHoldsSameValue()` | — | ✘ |
| `equality()` | — | ✘ |
| `commandArgumentCopyKeepsChoice()` | — | ✘ |


##### `ArgumentDirectionTest.java`

**Responsible for:** Tests for ArgumentDirection, the port of the int direction member of C's union cmd_arg_data and its arg_DIRECTION tag (cmd-core.h).

**class `ArgumentDirectionTest`** — class Javadoc: ✔ · type stamp: on 261012

- Fields (0/3 documented): ✘ `realPlayer`, `realQueue`, `cmd`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `valueRoundTrips()` | — | ✘ |
| `typeIsDirection()` | — | ✘ |
| `noneAndTargetStayDistinct()` | — | ✘ |
| `unknownIsStorable()` | — | ✘ |
| `directionIsNotTarget()` | — | ✘ |
| `copyHoldsSameValue()` | — | ✘ |
| `equality()` | — | ✘ |
| `commandArgumentCopyKeepsDirection()` | — | ✘ |
| `commandRoundTripAndClone()` | — | ✘ |
| `commandDirectionAndTargetDoNotCross()` | — | ✘ |
| `commandDirectionIsNotAnInt()` | — | ✘ |


##### `ArgumentItemTest.java`

**Responsible for:** Tests for ArgumentItem, the port of the struct object *obj member of C's union cmd_arg_data and its arg_ITEM tag (cmd-core.h).

**class `ArgumentItemTest`** — class Javadoc: ✔ · type stamp: on 261012

- Fields (0/3 documented): ✘ `realPlayer`, `realQueue`, `cmd`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `valueRoundTrips()` | — | ✘ |
| `nullIsStorable()` | — | ✘ |
| `typeIsItem()` | — | ✘ |
| `equalityIsByReference()` | — | ✘ |
| `recordHoldsTheObjectItself()` | — | ✘ |
| `copyHoldsSameItem()` | — | ✘ |
| `copyOfNull()` | — | ✘ |
| `commandArgumentCopyKeepsItem()` | — | ✘ |
| `commandRoundTripAndClone()` | — | ✘ |
| `itemDoesNotCrossTypes()` | — | ✘ |
| `execute(Command)` | — | ✘ |
| `repeatAllowed()` | — | ✘ |
| `itemAt(int, int)` | — | ✘ |
| `floorItemDisablesRepeat()` | — | ✘ |
| `oneNonZeroCoordinateIsTheFloor()` | — | ✘ |
| `inventoryAndNullItemsKeepRepeat()` | — | ✘ |
| `laterArgumentIsInspected()` | — | ✘ |
| `gateAlreadyShutOrNoCommand()` | — | ✘ |


##### `ArgumentNumberTest.java`

**Responsible for:** Tests for ArgumentNumber, the port of the int number member of C's union cmd_arg_data and its arg_NUMBER tag (cmd-core.h).

**class `ArgumentNumberTest`** — class Javadoc: ✔ · type stamp: on 261012

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newCommand()` | — | ✘ |
| `valueRoundTrips()` | — | ✘ |
| `valueIsUnchecked()` | — | ✘ |
| `typeIsNumber()` | — | ✘ |
| `tagOrdinalMatchesC()` | — | ✘ |
| `numberIsNotChoice()` | — | ✘ |
| `copyHoldsSameValue()` | — | ✘ |
| `equality()` | — | ✘ |
| `commandArgumentCopyKeepsNumber()` | — | ✘ |
| `commandRoundTrip()` | — | ✘ |
| `commandReadRefusesOtherTags()` | — | ✘ |
| `commandOverwrite()` | — | ✘ |


##### `ArgumentPointTest.java`

**Responsible for:** Tests for ArgumentPoint, the port of the struct loc point member of C's union cmd_arg_data and its arg_POINT tag (cmd-core.h).

**class `ArgumentPointTest`** — class Javadoc: ✔ · type stamp: on 261012

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newCommand()` | — | ✘ |
| `at(int, int)` | — | ✘ |
| `valueRoundTrips()` | — | ✘ |
| `axesAreNotTransposed()` | — | ✘ |
| `valueIsUnchecked()` | — | ✘ |
| `typeIsPoint()` | — | ✘ |
| `tagOrdinalMatchesC()` | — | ✘ |
| `pointIsNotDirection()` | — | ✘ |
| `copyHoldsSameLocation()` | — | ✘ |
| `copyDoesNotShareLoc()` | — | ✘ |
| `equalityIgnoresLocIdentity()` | — | ✘ |
| `copyOfOriginKeepsOrigin()` | — | ✘ |
| `nullLocationBecomesOrigin()` | — | ✘ |
| `commandNullPointIsOrigin()` | — | ✘ |
| `equality()` | — | ✘ |
| `commandArgumentCopyKeepsPoint()` | — | ✘ |
| `commandRoundTrip()` | — | ✘ |
| `commandReadRefusesOtherTags()` | — | ✘ |
| `otherGettersRefusePoint()` | — | ✘ |
| `commandOverwrite()` | — | ✘ |
| `commandOverwriteChangesTag()` | — | ✘ |
| `commandCloneKeepsPoint()` | — | ✘ |


##### `ArgumentStringTest.java`

**Responsible for:** Tests for ArgumentString, the port of the const char *string member of C's union cmd_arg_data and its arg_STRING tag (cmd-core.h).

**class `ArgumentStringTest`** — class Javadoc: ✔ · type stamp: on 261012

- Fields (0/3 documented): ✘ `realPlayer`, `realQueue`, `cmd`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `valueRoundTrips()` | — | ✘ |
| `typeIsString()` | — | ✘ |
| `nullIsStorable()` | — | ✘ |
| `nullIsNotEmpty()` | — | ✘ |
| `equalityIsByContent()` | — | ✘ |
| `copyHoldsSameValue()` | — | ✘ |
| `copyKeepsEmptyAndNull()` | — | ✘ |
| `commandArgumentCopyKeepsString()` | — | ✘ |
| `commandRoundTripAndClone()` | — | ✘ |
| `commandEmptyVersusNull()` | — | ✘ |
| `commandOverwrite()` | — | ✘ |
| `commandStringIsNotAnyOtherKind()` | — | ✘ |
| `commandOtherKindsAreNotStrings()` | — | ✘ |


##### `ArgumentTargetTest.java`

**Responsible for:** Tests for ArgumentTarget, the port of C's arg_TARGET tag, which shares the int direction member of union cmd_arg_data (cmd-core.h).

**class `ArgumentTargetTest`** — class Javadoc: ✔ · type stamp: on 261012

- Fields (0/3 documented): ✘ `realPlayer`, `realQueue`, `cmd`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `valueRoundTrips()` | — | ✘ |
| `typeIsTarget()` | — | ✘ |
| `nullIsRefused()` | — | ✘ |
| `unknownIsStorable()` | — | ✘ |
| `noneAndTargetStayDistinct()` | — | ✘ |
| `targetIsNotDirection()` | — | ✘ |
| `copyHoldsSameValue()` | — | ✘ |
| `equality()` | — | ✘ |
| `commandArgumentCopyKeepsTarget()` | — | ✘ |
| `commandRoundTripAndClone()` | — | ✘ |
| `commandRefusesNull()` | — | ✘ |
| `commandTargetIsNotAnInt()` | — | ✘ |


## middle/game/globals

##### `GameConstantsAccessorsTest.java`

**Responsible for:** Tests GameConstants' accessors for the scalar tunables read from constants.txt.

**class `GameConstantsAccessorsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `setCounter(String, int)` | — | ✔ |
| `levelMaxConstants()` | — | ✔ |
| `monGenConstants()` | — | ✔ |
| `monPlayConstants()` | — | ✔ |
| `dunGenConstants()` | — | ✔ |
| `worldConstants()` | — | ✔ |
| `carryCapConstants()` | — | ✔ |
| `storeConstants()` | — | ✔ |
| `objMakeConstants()` | — | ✔ |
| `playerConstants()` | — | ✔ |
| `meleeCriticalConstants()` | — | ✔ |
| `oRangedCriticalConstants()` | — | ✔ |
| `nonDelegatingAccessors()` | — | ✔ |
| `unloadedHolderThrows()` | — | ✔ |


##### `GameConstantsBadNamesTest.java`

**Responsible for:** Tests that a names.txt with a section C rejects stops GameConstants.init, as C's PARSE_ERROR_OUT_OF_BOUNDS from parse_names_section (init.c) stops the game from starting.

**class `GameConstantsBadNamesTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `tempDir`, `savedGamedata`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtCopy()` | — | ✘ |
| `restore()` | — | ✘ |
| `core()` | — | ✘ |
| `sectionAtMarkerStopsStartUp()` | — | ✘ |
| `goodFileGetsPastTheNameLoad()` | — | ✘ |


##### `GameConstantsInitTablesTest.java`

**Responsible for:** Pins what a real GameConstants#init(Core) leaves behind that the other test classes for GameConstants do not: the three process-wide constants, the scalar tunables as they stand in C's own constants.txt, and the object-power tables C declares statically in obj-power.c.

**class `GameConstantsInitTablesTest`** — class Javadoc: ✔ · type stamp: on 261010

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✔ |
| `cleanup()` | — | ✔ |
| `headerConstants()` | — | ✔ |
| `monsterConstants()` | — | ✔ |
| `worldConstants()` | — | ✔ |
| `storeObjectAndPlayerConstants()` | — | ✔ |
| `criticalConstants()` | — | ✔ |
| `archeryTableMatchesC()` | — | ✔ |
| `flagSetTableMatchesC()` | — | ✔ |
| `elementSetTableMatchesC()` | — | ✔ |


##### `GameConstantsInitUIBindingTest.java`

**Responsible for:** Tests the bindui: wiring phase of GameConstants#init(Core) — the block that groups every loaded ObjectProperty/PlayerProperty by the UIEntrySpec its bindui: lines name and hands each group to UIEntryValueRegistry#addEntryBinding.

**class `GameConstantsInitUIBindingTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✔ |
| `bindingByEntry()` | — | ✔ |
| `binding(String)` | — | ✔ |
| `component(Object, String)` | — | ✔ |
| `objectPropertyReachesItsBoundEntry()` | — | ✘ |
| `statObjectPropertyReachesItsBoundEntry()` | — | ✘ |
| `playerPropertyReachesTheSameEntryAlongsideTheObjectHalf()` | — | ✘ |
| `anUnboundEntryHasNoBinding()` | — | ✘ |


## middle/game/globals/cached

##### `CachedPlayerDataTest.java`

**Responsible for:** Tests CachedPlayerData, the port of C's struct cached_player_data and of the if (*cache == NULL) block at the top of compute_ui_entry_values_for_player (ui-entry.c).

**class `CachedPlayerDataTest`** — class Javadoc: ✔ · type stamp: on 261006

- Fields (1/1 documented): ✔ `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField()` | — | ✔ |
| `definition(TimedEffect, ObjectFlag)` | — | ✔ |
| `loadPairings()` | — | ✔ |
| `player(Flag<ObjectFlag>, Flag<ObjectFlag>)` | — | ✔ |
| `plainPlayer()` | — | ✔ |
| `setOf(ObjectFlag)` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `CachedPlayerDataTest.Untimed`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceAndClassFlagsBothPresent()` | — | ✘ |
| `flagOnBothIsPresent()` | — | ✘ |
| `ungrantedFlagAbsent()` | — | ✘ |
| `bravery30AtThirty()` | — | ✘ |
| `bravery30AtTwentyNine()` | — | ✘ |
| `highLevelWithoutPflag()` | — | ✘ |
| `timedFlagNotInUntimed()` | — | ✘ |

**class `CachedPlayerDataTest.Timed`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `runningEffectDuplicatesFlag()` | — | ✘ |
| `dormantEffectContributesNothing()` | — | ✘ |
| `lastTurnStillContributes()` | — | ✘ |
| `twoEffectsBothContribute()` | — | ✘ |
| `untimedFlagNotInTimed()` | — | ✘ |
| `sameFlagInBothSets()` | — | ✘ |
| `trapSafeRunning()` | — | ✘ |
| `trapSafeLastTurn()` | — | ✘ |
| `trapSafeDormant()` | — | ✘ |
| `trapSafeNotInUntimed()` | — | ✘ |
| `trapImmuneFromRaceStaysUntimed()` | — | ✘ |

**class `CachedPlayerDataTest.FillOnce`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `secondCallDoesNotRefresh()` | — | ✘ |
| `secondCallWithOtherPlayerIgnored()` | — | ✘ |
| `setsKeptAcrossSecondCall()` | — | ✘ |
| `separateCachesIndependent()` | — | ✘ |
| `firstFillReplacesHandSetFlags()` | — | ✘ |
| `getterReferenceDetachedByFirstFill()` | — | ✘ |
| `handSetAfterFillKept()` | — | ✘ |

**class `CachedPlayerDataTest.Accessors`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onTimedReturnValue()` | — | ✘ |
| `onUntimedReturnValue()` | — | ✘ |
| `offTimedReturnValue()` | — | ✘ |
| `offUntimedReturnValue()` | — | ✘ |
| `emptyCacheReadsAndRemovesAnswerFalse()` | — | ✘ |
| `setsIndependent()` | — | ✘ |
| `gettersReturnLiveSets()` | — | ✘ |
| `gettersNonNullAndStable()` | — | ✘ |
| `gettersEmptyOnFreshCache()` | — | ✘ |


## middle/game/globals/data

##### `GameConstantsDataTest.java`

**Responsible for:** Tests GameConstantsData.GameConstantsBuilder against the C it ports: the parse_constants_*() handlers, check_critical_levels() and finish_parse_constants() in init.c, and the zeroed struct angband_constants that init_parse_constants() allocates.

**class `GameConstantsDataTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (0/4 documented): ✘ `MELEE_NOT_INCREASING`, `RANGED_NOT_INCREASING`, `MELEE_SENTINEL`, `RANGED_SENTINEL`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `melee(int)` | on 261002 | ✔ |
| `ranged(int)` | on 261002 | ✔ |
| `withMelee(int)` | on 261002 | ✔ |
| `withRanged(int)` | on 261002 | ✔ |

**class `GameConstantsDataTest.Build`** — class Javadoc: ✔ · type stamp: on 261002

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nothingSetGivesAllZero()` | on 261002 | ✔ |
| `anyErrorGivesNull()` | on 261002 | ✔ |
| `partialSetLeavesRestZero()` | on 261002 | ✔ |
| `everySetterLandsInItsOwnComponent()` | on 261002 | ✔ |
| `negativeCriticalConstantKept()` | on 261002 | ✔ |

**class `GameConstantsDataTest.LevelRows`** — class Javadoc: ✔ · type stamp: on 261002

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oMeleeRowsKeepFileOrder()` | on 261002 | ✔ |
| `oRangedRowsKeepFileOrder()` | on 261002 | ✔ |
| `zeroChanceRejected()` | on 261002 | ✔ |
| `negativeChanceOrDiceRejected()` | on 261002 | ✔ |
| `chanceOneDiceZeroAccepted()` | on 261002 | ✔ |
| `meleeAndRangedRowsUncheckedInOrder()` | on 261002 | ✔ |

**class `GameConstantsDataTest.CheckCriticalLevels`** — class Javadoc: ✔ · type stamp: on 261002

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `shippedListsPass()` | on 261002 | ✔ |
| `emptyPasses()` | on 261002 | ✔ |
| `oneOrTwoRowsAlwaysPass()` | on 261002 | ✔ |
| `lastCutoffIgnored()` | on 261002 | ✔ |
| `equalMiddleCutoffFails()` | on 261002 | ✔ |
| `firstCutoffIsComparedAgainst()` | on 261002 | ✔ |
| `laterDecreaseFails()` | on 261002 | ✔ |
| `meleeReportedBeforeRanged()` | on 261002 | ✔ |
| `rangedCheckedAfterGoodMelee()` | on 261002 | ✔ |
| `middleMinusOneRejectedDivergence()` | on 261002 | ✔ |
| `firstMinusOneNotSentinelChecked()` | on 261002 | ✔ |


## middle/game/globals/loaders

##### `DungeonLoaderTest.java`

**Responsible for:** Tests DungeonLoader, the port of C's generate_module init, run_template_parser() in generate.c: the dungeon-profile, room-template and vault parsers, run in that order.

**class `DungeonLoaderTest`** — class Javadoc: ✔ · type stamp: on 261013

- Fields (1/8 documented): ✔ `GOOD_VAULT` — ✘ `C_PROFILE_NAMES`, `tempDir`, `savedGamedata`, `savedProfiles`, `savedRooms`, `savedVaults`, `savedProfileMax`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtTempDir()` | — | ✔ |
| `restore()` | — | ✔ |
| `registryList(String)` | — | ✔ |
| `useRealGamedata()` | — | ✔ |
| `write(String, String)` | — | ✘ |

**record `DungeonLoaderTest.Case`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/2 documented): ✘ `file`, `loader`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toString()` | — | ✘ (override) |
| `loaders()` | — | ✘ |

**class `DungeonLoaderTest.RealData`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `bootstrapped`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✔ |
| `cleanup()` | — | ✔ |
| `profiles()` | — | ✘ |
| `rooms()` | — | ✔ |
| `vaults()` | — | ✔ |
| `gameConstantsLoaded()` | — | ✘ |
| `missingFileStopsTheLoad(Case)` | — | ✔ |
| `reloadReplaces()` | — | ✔ |
| `profileSoftErrorWithSurvivors()` | — | ✔ |
| `profileSoftErrorWithNoSurvivors()` | — | ✔ |
| `vaultExplicitDepths()` | — | ✔ |
| `vaultSoftErrorWithSurvivors()` | — | ✔ |
| `vaultSoftErrorWithNoSurvivors()` | — | ✔ |
| `roomSoftErrorWithNoSurvivors()` | — | ✔ |


##### `MiscDataLoaderFlavoursTest.java`

**Responsible for:** Tests MiscDataLoader.loadFlavours against C's flavor_parser (init.c).

**class `MiscDataLoaderFlavoursTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `tempDir`, `savedGamedata`, `savedFlavours`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtTempDir()` | — | ✘ |
| `restore()` | — | ✘ |
| `currentFlavours()` | — | ✔ |
| `writeFlavours(String)` | — | ✘ |
| `goodFileLoads()` | — | ✘ |
| `softErrorWithRecordsStillRegisters()` | — | ✘ |
| `missingFileThrows()` | — | ✘ |
| `hardGrammarErrorThrows()` | — | ✘ |
| `onlyBlockDroppedThrows()` | — | ✘ |


##### `MiscDataLoaderHintsTest.java`

**Responsible for:** Tests MiscDataLoader.loadHints against C's hints_parser (init.c).

**class `MiscDataLoaderHintsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `tempDir`, `savedGamedata`, `savedHints`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtTempDir()` | — | ✘ |
| `restore()` | — | ✘ |
| `currentHints()` | — | ✔ |
| `texts(List<Hint>)` | — | ✘ |
| `writeHints(String)` | — | ✘ |
| `goodFileLoads()` | — | ✘ |
| `emptyFileLoadsEmptyList()` | — | ✘ |
| `promisedHintsMissingThrows()` | — | ✘ |
| `softErrorWithRecordsStillRegisters()` | — | ✘ |
| `missingFileThrows()` | — | ✘ |
| `hardGrammarErrorThrows()` | — | ✘ |
| `shippedFileLoads()` | — | ✘ |


##### `MiscDataLoaderNamesTest.java`

**Responsible for:** Tests MiscDataLoader.loadNames: a good names.txt fills the registry, and a bad one stops start-up as C does, in three ways.

**class `MiscDataLoaderNamesTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `tempDir`, `savedGamedata`, `savedNames`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtTempDir()` | — | ✘ |
| `restore()` | — | ✘ |
| `safeNames()` | — | ✔ |
| `writeNames(String)` | — | ✘ |
| `goodFileLoads()` | — | ✘ |
| `emptyFileLoadsEmptySections()` | — | ✘ |
| `sectionWithNoWordsLoads()` | — | ✘ |
| `promisedSectionsMissingThrows()` | — | ✘ |
| `wordBeforeSectionThrows()` | — | ✘ |
| `sectionAtMarkerEscapes()` | — | ✘ |
| `sectionZeroEscapes()` | — | ✘ |
| `badSectionLeavesEarlierRecordsFiled()` | — | ✘ |
| `missingFileThrows()` | — | ✘ |
| `hardGrammarErrorThrows()` | — | ✘ |
| `softErrorWithRecordsStillRegisters()` | — | ✘ |
| `shippedFileLoads()` | — | ✘ |


##### `MonsterDataLoaderFailurePathsTest.java`

**Responsible for:** Tests how the nine loaders in MonsterDataLoader stop the load, against what C does at the same point.

**class `MonsterDataLoaderFailurePathsTest`** — class Javadoc: ✔ · type stamp: on 261013

- Fields (0/2 documented): ✘ `tempDir`, `savedGamedata`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtTempDir()` | — | ✔ |
| `restore()` | — | ✔ |

**record `MonsterDataLoaderFailurePathsTest.Case`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `file`, `loader`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toString()` | — | ✘ (override) |
| `loaders()` | — | ✔ |
| `missingFileStopsTheLoad(Case)` | — | ✔ |
| `errorsWithNoRecordsStopTheLoad()` | — | ✔ |
| `miscountWithRecordsRegistersThem()` | — | ✔ |
| `syntaxErrorStopsTheLoad()` | — | ✔ |
| `painSet(String)` | — | ✔ |


##### `MonsterDataLoaderLoreTest.java`

**Responsible for:** Tests that MonsterDataLoader#loadMonsters() gives every race its own blank MonsterLore, the Java form of the l_list allocation at the end of C's finish_parse_monster (mon-init.c).

**class `MonsterDataLoaderLoreTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✘ |
| `everyRaceHasLore()` | — | ✘ |
| `recordsAreNotShared()` | — | ✘ |
| `blowListMatchesRaceBlowCount()` | — | ✘ |
| `blowSlotsAreBlankBlows()` | — | ✘ |
| `recordsStartZeroed()` | — | ✘ |
| `longestListIsTheGlobalMaximum()` | — | ✘ |


##### `MonsterDataLoaderRegistryTest.java`

**Responsible for:** Tests what MonsterDataLoader leaves in MonsterRegistry after the whole init chain has run against the real lib/gamedata.

**class `MonsterDataLoaderRegistryTest`** — class Javadoc: ✔ · type stamp: on 261013

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✘ |
| `racesLoaded()` | — | ✘ |
| `longestBlowListIsTheHydras()` | — | ✘ |
| `playerRecordHasNoBlows()` | — | ✘ |
| `painSetsLoaded()` | — | ✘ |
| `countersAreRecordCounts()` | — | ✘ |
| `basesAndSummonsResolve()` | — | ✘ |
| `visualTablesLoaded()` | — | ✘ |


##### `ObjectDataLoaderCurseKindsTest.java`

**Responsible for:** Tests for ObjectDataLoader#parseCurseKinds() and its private worker, the port of C's write_curse_kinds in obj-init.c, which runs at the end of finish_parse_artifact.

**class `ObjectDataLoaderCurseKindsTest`** — class Javadoc: ✔ · type stamp: on 261005

- Fields (0/2 documented): ✘ `savedObjectKinds`, `savedCurses`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField(String)` | — | ✘ |
| `kind(TValue, String, int)` | — | ✔ |
| `curse(String, ItemObject, int)` | — | ✔ |
| `isolate()` | — | ✘ |
| `restore()` | — | ✘ |
| `ordinaryPath_objectAndKnownTakeKindAndSval()` | — | ✔ |
| `assessedIsOnTheKnownObjectOnly()` | — | ✔ |
| `existingKnownIsKeptAndUpdated()` | — | ✔ |
| `everyCurseIsWritten_includingIndexZero()` | — | ✔ |
| `lookupIsByTvalAndName()` | — | ✔ |
| `missingKind_nullKindAndSvalMinusOne_noThrow()` | — | ✔ |
| `noCurses_isANoOp()` | — | ✔ |


##### `ObjectDataLoaderFailurePathsTest.java`

**Responsible for:** Tests how the ten file loaders in ObjectDataLoader stop the load, against what C does at the same point.

**class `ObjectDataLoaderFailurePathsTest`** — class Javadoc: ✔ · type stamp: on 261013

- Fields (0/2 documented): ✘ `tempDir`, `savedGamedata`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtTempDir()` | — | ✔ |
| `restore()` | — | ✔ |

**record `ObjectDataLoaderFailurePathsTest.Case`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `file`, `loader`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toString()` | — | ✘ (override) |
| `loaders()` | — | ✔ |
| `missingFileStopsTheLoad(Case)` | — | ✔ |
| `invalidBrandFileStopsTheLoad()` | — | ✔ |
| `invalidSlayFileStopsTheLoad()` | — | ✔ |
| `invalidCurseFileIsWrapped()` | — | ✔ |


##### `ObjectDataLoaderRegistryTest.java`

**Responsible for:** Tests what ObjectDataLoader leaves in ObjectRegistry after the whole init chain has run against the real lib/gamedata.

**class `ObjectDataLoaderRegistryTest`** — class Javadoc: ✔ · type stamp: on 261013

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✘ |
| `recordCounts()` | — | ✘ |
| `countersAreRecordCounts()` | — | ✘ |
| `listOrderFollowsC()` | — | ✔ |
| `egoName(EgoItem)` | — | ✔ |
| `chestTrapsLoaded()` | — | ✘ |
| `kindTableAndCeiling()` | — | ✘ |
| `specialLightSvals()` | — | ✘ |
| `ordinaryBaseObjectIsReused()` | — | ✘ |
| `unknownSentinels()` | — | ✘ |
| `curseObjectsHaveKinds()` | — | ✘ |
| `secondArtifactRunIsIdempotent()` | — | ✘ |


##### `PlayerDataLoaderFailurePathsTest.java`

**Responsible for:** Tests how the eight loaders in PlayerDataLoader stop the load, against what C does at the same point.

**class `PlayerDataLoaderFailurePathsTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (0/3 documented): ✘ `tempDir`, `savedGamedata`, `savedShapes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pointAtTempDir()` | — | ✔ |
| `restore()` | — | ✔ |

**record `PlayerDataLoaderFailurePathsTest.Case`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (2/2 documented): ✔ `file`, `loader`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toString()` | — | ✘ (override) |
| `loaders()` | — | ✔ |
| `missingFileStopsTheLoad(Case)` | — | ✔ |
| `softErrorsWithNoRecordsStopTheLoad()` | — | ✔ |
| `softErrorsWithSomeRecordsRegisterThem()` | — | ✔ |
| `syntaxErrorStopsTheLoad()` | — | ✔ |
| `currentShapes()` | — | ✔ |


##### `PlayerDataLoaderInitializeExpLevelTest.java`

**Responsible for:** Tests PlayerDataLoader#initializeExpLevel() — the port of C's player_exp[PY_MAX_LEVEL] constant array (player.c:48-100).

**class `PlayerDataLoaderInitializeExpLevelTest`** — class Javadoc: ✔ · type stamp: on 260925

- Fields (3/3 documented): ✔ `EXPECTED`, `savedPlayerExperience`, `savedPlayerCharSheetView`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveGlobals()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `fillsAllFiftyLevels()` | — | ✔ |
| `matchesBothEnds()` | — | ✔ |
| `clearsPreExistingEntries()` | — | ✔ |
| `overwritesStaleInRangeValue()` | — | ✔ |
| `pushesTableToCharSheetCache()` | — | ✔ |
| `leavesOtherCharSheetFieldsUntouched()` | — | ✔ |


##### `TerrainDataLoaderFeaturesTest.java`

**Responsible for:** Tests TerrainDataLoader#loadTerrainFeatures(), the port of C's feat_parser (init_parse_feat, run_parse_feat and finish_parse_feat in init.c).

**class `TerrainDataLoaderFeaturesTest`** — class Javadoc: ✔ · type stamp: on 261013

- Fields (3/7 documented): ✔ `C_ORDER`, `C_SHOPS`, `C_DIGGING` — ✘ `tempDir`, `savedGamedata`, `savedFeatures`, `savedStoreMax`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `save()` | — | ✔ |
| `restore()` | — | ✔ |
| `useTempDir()` | — | ✘ |
| `shippedTerrain()` | — | ✘ |
| `writeTerrain(String)` | — | ✘ |
| `field(Feature, String)` | — | ✔ |
| `feature(TerrainFlags)` | — | ✘ |

**class `TerrainDataLoaderFeaturesTest.RealData`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load()` | — | ✘ |
| `loadsEveryCodeInOrder()` | — | ✘ |
| `shopNumbers()` | — | ✘ |
| `mimics()` | — | ✘ |
| `digging()` | — | ✘ |
| `priority()` | — | ✘ |
| `lookStrings()` | — | ✘ |
| `resistFlag()` | — | ✘ |
| `displayCharacter()` | — | ✘ |
| `reloadReplaces()` | — | ✘ |

**class `TerrainDataLoaderFeaturesTest.FailurePaths`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `sentinel`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plantSentinel()` | — | ✘ |
| `missingFile()` | — | ✘ |
| `everyRecordDropped()` | — | ✘ |
| `oneRecordDroppedLoadsTheRest()` | — | ✘ |
| `shortFileLoads()` | — | ✘ |
| `duplicateCodeIsFatal()` | — | ✘ |
| `droppedShopEntranceLowersStoreMax()` | — | ✘ |
| `storeMaxSurvivesAFailedLoad()` | — | ✘ |
| `installedList()` | — | ✔ |

**class `TerrainDataLoaderFeaturesTest.ColourCheck`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unknownColourIsRejected()` | — | ✘ |
| `colourNameResolves()` | — | ✘ |


##### `TerrainDataLoaderTrapsTest.java`

**Responsible for:** Tests TerrainDataLoader#loadTraps(), the Java guard that the kind at index 0 of trap.txt is no trap.

**class `TerrainDataLoaderTrapsTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/6 documented): ✘ `NO_TRAP`, `GLYPH`, `BAD_FLAG`, `tempDir`, `savedGamedata`, `savedTraps`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `badFlagRecord(String)` | — | ✘ |
| `pointAtTempDir()` | — | ✘ |
| `restore()` | — | ✘ |
| `writeTrapFile(int, String)` | — | ✘ |
| `noTrapFirstLoads()` | — | ✘ |
| `laterDroppedRecordIsTolerated()` | — | ✘ |
| `droppedNoTrapStopsTheLoad()` | — | ✘ |
| `otherFirstKindStopsTheLoad()` | — | ✘ |
| `emptyResultStopsTheLoad()` | — | ✘ |
| `loadedListFeedsLookupTrap()` | — | ✘ |
| `missingFileStopsTheLoad()` | — | ✘ |
| `reloadReplaces()` | — | ✘ |
| `shippedFile()` | — | ✘ |


##### `WorldDataLoaderTest.java`

**Responsible for:** Tests WorldDataLoader, the port of C's world_parser (init.c), projection_parser (obj-init.c) and quests_parser (player-quest.c).

**class `WorldDataLoaderTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (0/6 documented): ✘ `bootstrapped`, `tempDir`, `savedGamedata`, `savedWorlds`, `savedProjections`, `savedQuests`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✔ |
| `cleanup()` | — | ✔ |
| `monstersLoaded()` | — | ✘ |
| `pointAtTempDir()` | — | ✔ |
| `restore()` | — | ✔ |
| `registryList(String)` | — | ✔ |
| `useRealGamedata()` | — | ✔ |
| `write(String, String)` | — | ✘ |
| `level(int, String, String, String)` | — | ✘ |
| `worldFile(int, String)` | — | ✘ |

**record `WorldDataLoaderTest.Case`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/2 documented): ✘ `file`, `loader`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toString()` | — | ✘ (override) |
| `loaders()` | — | ✘ |

**class `WorldDataLoaderTest.RealData`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `world()` | — | ✔ |
| `projections()` | — | ✔ |
| `quests()` | — | ✔ |
| `missingFileStopsTheLoad(Case)` | — | ✔ |

**class `WorldDataLoaderTest.LevelReferences`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `forwardReferenceAndNone()` | — | ✔ |
| `selfReference()` | — | ✔ |
| `depthsAreNotChecked()` | — | ✔ |
| `missingPrevious()` | — | ✘ |
| `missingNext()` | — | ✘ |
| `noneIsCaseSensitive(String)` | — | ✔ |
| `exactNameMatch()` | — | ✔ |
| `notNone()` | — | ✘ |
| `reloadReplacesWorld()` | — | ✔ |
| `tooFewElements()` | — | ✔ |
| `emptyQuestFile()` | — | ✔ |
| `unknownRace()` | — | ✔ |
| `reloadReplacesQuests()` | — | ✔ |


## middle/game/globals/registry

##### `CachedPlayerDataProbeTest.java`

**Responsible for:** Probe for the cache-fill block of C's compute_ui_entry_values_for_player: when *cache is NULL it is filled once from player_flags, player_flags_timed and p->timed[TMD_TRAPSAFE] (non-zero only while the effect runs), and the caller keeps it.

**class `CachedPlayerDataProbeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `ENTRY`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerWithRaceFreeAct()` | — | ✔ |
| `plainShape()` | — | ✘ |
| `objectFlagPlayerProperty(ObjectFlag)` | — | ✘ |
| `reset()` | — | ✘ |
| `populateFillsUntimedFromRace()` | — | ✘ |
| `trapSafeAtZeroLeavesTimedEmpty()` | — | ✘ |
| `trapSafeRunningSetsTimedFlag()` | — | ✘ |
| `onTimedFlagBeforePopulateStillFills()` | — | ✘ |
| `getterBeforePopulateStillFills()` | — | ✘ |
| `filledOnce()` | — | ✘ |
| `freshCacheIsFilledByCompute()` | — | ✘ |
| `trapImmuneAuxIsZeroWhenNotRunning()` | — | ✘ |
| `nullCacheTrapImmuneAuxIsZeroWhenNotRunning()` | — | ✘ |
| `nullCacheTrapImmuneAuxIsOneWhenRunning()` | — | ✘ |
| `trapImmuneAuxIsOneWhenRunning()` | — | ✘ |


##### `CurseSparseElInfoProbeTest.java`

**Responsible for:** Probe, not a permanent test: C's el_info on a curse object is a zero-initialised array of ELEM_MAX, so compute_ui_entry_values_for_object reads a resistance of 0 for any element the curse does not mention.

**class `CurseSparseElInfoProbeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `ENTRY`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `elementProperty(ObjPropertyType, ElementEnum)` | — | ✘ |
| `computeWithSparseCurse(ObjPropertyType)` | — | ✘ |
| `reset()` | — | ✘ |
| `resistOnSparseCurse()` | — | ✘ |
| `ignoreOnSparseCurse()` | — | ✘ |


##### `DungeonRegistryTest.java`

**Responsible for:** Tests DungeonRegistry, the port's home for the three file-scope globals of C's generate.c: cave_profiles, room_templates and vaults.

**class `DungeonRegistryTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (0/3 documented): ✘ `savedProfiles`, `savedRooms`, `savedVaults`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveAndClear()` | — | ✔ |
| `restore()` | — | ✔ |
| `read(String)` | — | ✔ |
| `settersStoreByIdentity()` | — | ✘ |
| `secondCallReplaces()` | — | ✔ |
| `nullUnloads()` | — | ✔ |
| `listsAreIndependent()` | — | ✔ |
| `roomTemplatesAreInReverseFileOrder()` | — | ✔ |


##### `MiscRegistryHintsFlavoursTest.java`

**Responsible for:** Tests the hint and flavour holders of MiscRegistry: setHints and getHints, and setFlavours and getFlavours.

**class `MiscRegistryHintsFlavoursTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `savedHints`, `savedFlavours`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveRegistry()` | — | ✘ |
| `restoreRegistry()` | — | ✘ |
| `readField(String)` | — | ✘ |
| `writeField(String, Object)` | — | ✘ |
| `kind(TValue, char, String)` | — | ✘ |

**class `MiscRegistryHintsFlavoursTest.Hints`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedHintsComeBack()` | — | ✘ |
| `emptyListIsEmpty()` | — | ✘ |
| `secondLoadReplaces()` | — | ✘ |
| `listIsStoredByReference()` | — | ✘ |
| `viewIsReadOnly()` | — | ✘ |
| `hintsAreNotCopied()` | — | ✘ |
| `unloadedThrows()` | — | ✘ |

**class `MiscRegistryHintsFlavoursTest.Flavours`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedKindsComeBack()` | — | ✘ |
| `emptyListIsEmpty()` | — | ✘ |
| `secondLoadReplaces()` | — | ✘ |
| `listIsStoredByReference()` | — | ✘ |
| `viewIsReadOnly()` | — | ✘ |
| `kindsAreNotCopied()` | — | ✘ |
| `unloadedThrows()` | — | ✘ |

**class `MiscRegistryHintsFlavoursTest.Independence`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `settersDoNotDisturbEachOther()` | — | ✘ |


##### `MiscRegistryNameSectionsTest.java`

**Responsible for:** Tests MiscRegistry.setNames and MiscRegistry.getNameSection — together the port of C's finish_parse_names (src/init.c:1466) and of the name_sections[name_type] lookup that randname_make (src/randname.c:77) performs on the result.

**class `MiscRegistryNameSectionsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `TOLKIEN_SECTION`, `SCROLL_SECTION`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `name(int, String)` | — | ✘ |

**class `MiscRegistryNameSectionsTest.Filing`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sectionsAreSeparate()` | — | ✘ |
| `recordsForOneSectionAccumulate()` | — | ✘ |
| `duplicatesAreKept()` | — | ✘ |
| `unmentionedSectionIsEmpty()` | — | ✘ |
| `noRecordsLeavesEverySectionEmpty()` | — | ✘ |
| `recordsAreStored()` | — | ✘ |
| `reloadReplaces()` | — | ✘ |
| `reloadEmptiesSectionsNoLongerMentioned()` | — | ✘ |
| `wordsKeepFileOrder()` | — | ✘ |

**class `MiscRegistryNameSectionsTest.OutOfRange`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `markerSectionIsRejected()` | — | ✘ |
| `sectionAboveMarkerIsRejected()` | — | ✘ |
| `sectionZeroIsRejected()` | — | ✘ |
| `negativeSectionIsRejected()` | — | ✘ |
| `badSectionAmongGoodOnesIsRejected()` | — | ✘ |

**class `MiscRegistryNameSectionsTest.Reading`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `markerSectionIsEmpty()` | — | ✘ |
| `returnedListIsUnmodifiable()` | — | ✘ |
| `callerCannotEditTheStoredSectionAfterwards()` | — | ✘ |


##### `MonsterRegistryTest.java`

**Responsible for:** Tests the lookups and counters of MonsterRegistry, added on 261009.

**class `MonsterRegistryTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (2/2 documented): ✔ `FIELDS`, `saved`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(String)` | — | ✔ |
| `saveAndClear()` | — | ✔ |
| `restore()` | — | ✔ |
| `race(String, int)` | — | ✔ |
| `race(String)` | — | ✔ |
| `loadRaces(MonsterRace)` | — | ✔ |
| `pains(int)` | — | ✔ |

**class `MonsterRegistryTest.LookupRace`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load()` | — | ✔ |
| `exactAnyCase()` | — | ✘ |
| `substringFirst()` | — | ✘ |
| `exactBeatsEarlierSubstring()` | — | ✘ |
| `playerRecordSearched()` | — | ✘ |
| `emptyQuery()` | — | ✘ |
| `noMatch()` | — | ✘ |
| `spaceQuery()` | — | ✘ |
| `nullNameSkipped()` | — | ✘ |
| `nullElementSkipped()` | — | ✘ |
| `sameRecord()` | — | ✘ |
| `unloaded()` | — | ✘ |

**class `MonsterRegistryTest.RaceCounters`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceMax()` | — | ✘ |
| `blowsMax()` | — | ✘ |
| `blowsMaxEmpty()` | — | ✘ |
| `racesView()` | — | ✘ |
| `racesViewUnloaded()` | — | ✘ |

**class `MonsterRegistryTest.Pain`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `shippedIndices()` | — | ✘ |
| `painMax()` | — | ✘ |
| `indexZero()` | — | ✘ |
| `outOfRange()` | — | ✘ |
| `unloaded()` | — | ✘ |

**class `MonsterRegistryTest.Bases`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load()` | — | ✘ |
| `exact()` | — | ✘ |
| `caseSensitive()` | — | ✘ |
| `noSubstring()` | — | ✘ |
| `delegates()` | — | ✘ |
| `unloaded()` | — | ✘ |

**class `MonsterRegistryTest.Blows`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `method(String)` | — | ✘ |
| `effect(String)` | — | ✘ |
| `methodFirst()` | — | ✘ |
| `methodExact()` | — | ✘ |
| `methodMax()` | — | ✘ |
| `effectFirst()` | — | ✘ |
| `effectMax()` | — | ✘ |
| `unloaded()` | — | ✘ |

**class `MonsterRegistryTest.Summons`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `summon(String)` | — | ✘ |
| `exact()` | — | ✘ |
| `miss()` | — | ✘ |
| `unloaded()` | — | ✘ |
| `pitMax()` | — | ✔ |


##### `ObjectRegistryCReferenceTest.java`

**Responsible for:** Pins ObjectRegistry to figures read out of the C source, where the existing ObjectRegistryTest and ObjectRegistryTablesTest pin its own rules: the obj-power.h constants and the ability_power table (obj-power.c), the lookup_obj_property walk (obj-properties.c) including its "stats count as mods" r...

**class `ObjectRegistryCReferenceTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `TOUCHED`, `saved`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(String)` | — | ✘ |
| `get(String)` | — | ✘ |
| `set(String, Object)` | — | ✘ |
| `base(TValue, String)` | — | ✘ |
| `kind(String, ObjectBase, String)` | — | ✔ |
| `property(ObjPropertyType, ObjectPropertyTypeWrapper)` | — | ✘ |
| `saveAndIsolate()` | — | ✘ |
| `restore()` | — | ✘ |
| `thePowerConstantsMatchObjPowerH()` | — | ✔ |
| `theInhibitingThresholdsMatchObjPowerH()` | — | ✔ |
| `theAbilityPowerTableMatchesObjPowerC()` | — | ✔ |
| `anObjectPropertyIsFoundByTypeAndPayload()` | — | ✔ |
| `anEmptyPropertyListAnswersNull()` | — | ✔ |
| `aModRequestFindsAStatProperty()` | — | ✔ |
| `aStatRequestDoesNotFindAModProperty()` | — | ✔ |
| `theFirstQualifyingPropertyInListOrderWins()` | — | ✔ |
| `aRequestOfOneFamilyIgnoresPropertiesOfAnother()` | — | ✔ |
| `loadUnknownKindsResolvesBothSentinelsByTheirDataFileNames()` | — | ✔ |
| `loadUnknownKindsDoesNothingWhileTheTableIsEmpty()` | — | ✔ |
| `loadUnknownKindsLeavesNullWhenTheNamesAreAbsent()` | — | ✔ |
| `resetNullsTheSentinels()` | — | ✔ |
| `theRuneCountIsTheLengthOfTheList()` | — | ✔ |
| `theRuneListKeepsItsOrderAndIsNotAliasedToTheCallersList()` | — | ✔ |
| `aNumericReferenceMayCarryASignOrTrailingBlanks()` | — | ✔ |
| `aNumericReferenceCRefusesFindsNoKindHere()` | — | ✔ |
| `aNameReferenceIgnoresTheMarkersInTheKindsDataFileName()` | — | ✔ |


##### `ObjectRegistryTablesTest.java`

**Responsible for:** Tests ObjectRegistry's three loaded object tables — items, egos and artifacts — and the count fields the loaders leave behind.

**class `ObjectRegistryTablesTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `savedItems`, `savedEgos`, `savedArtifacts`, `savedEgoMax`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(String)` | — | ✔ |
| `setInt(String, int)` | — | ✔ |
| `ego(String)` | — | ✔ |
| `artifact(String)` | — | ✔ |
| `saveTables()` | — | ✔ |
| `restoreTables()` | — | ✔ |
| `objectBasesAreReadOnly()` | — | ✔ |
| `unloadedTableThrows()` | — | ✔ |

**class `ObjectRegistryTablesTest.Tables`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tablesRoundTrip()` | — | ✔ |
| `gettersAreReadOnly()` | — | ✔ |
| `viewFollowsTheTable()` | — | ✔ |
| `settingReplaces()` | — | ✔ |

**class `ObjectRegistryTablesTest.Counts`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `egoCountFollowsTheTable()` | — | ✔ |
| `artifactCountFollowsTheTable()` | — | ✔ |
| `countsReadTheirOwnFields()` | — | ✔ |


##### `ObjectRegistryTest.java`

**Responsible for:** Tests ObjectRegistry itself, rather than the readers that fill it: the sval allocation and indexing ObjectRegistry#addObjectKind performs, what ObjectRegistry#reset() does and deliberately does not do, the several lookupObjectKind overloads (including the lookup_sval reference form C uses in its ...

**class `ObjectRegistryTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/4 documented): ✔ `TOUCHED` — ✘ `saved`, `swordBase`, `bookBase`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `snapshot()` | — | ✘ |
| `restore()` | — | ✘ |
| `field(String)` | — | ✘ |
| `get(String)` | — | ✘ |
| `set(String, Object)` | — | ✘ |
| `base(TValue, String)` | — | ✘ |
| `kind(String, ObjectBase, String)` | — | ✔ |
| `slay(String, String)` | — | ✔ |
| `brand(String, String)` | — | ✘ |
| `curse(String)` | — | ✘ |
| `activation(String)` | — | ✘ |
| `isolate()` | — | ✔ |
| `svalsAreAllocatedFromOnePerBase()` | — | ✔ |
| `theKindIndexIsThePositionInTheWholeTable()` | — | ✔ |
| `everyRegisteredKindIsReachableThroughTheIndex()` | — | ✔ |
| `twoBasesSharingATvalCollideInTheIndex()` | — | ✔ |
| `resetClearsTheTableAndTheIndexTogether()` | — | ✔ |
| `resetLeavesTheBasesSvalCountersAlone()` | — | ✔ |
| `getObjectKindsIsAnUnmodifiableViewThatTracksTheTable()` | — | ✔ |
| `setObjectKindsBypassesTheIndex()` | — | ✔ |
| `updateObjectBaseKindMaxRecordsTheCountAtTheMomentItIsCalled()` | — | ✔ |
| `theNumericLookupMissesReturnNullRatherThanThrowing()` | — | ✘ |
| `theNameLookupIsExactAndCaseSensitive()` | — | ✘ |
| `theReferenceLookupTakesEitherAnSvalOrAnSvalName()` | — | ✔ |
| `theReferenceLookupReturnsNullWhenNeitherLegMatches()` | — | ✔ |
| `theTvalFanOutReturnsAModifiableCopyAndAnEmptyListForAMiss()` | — | ✔ |
| `everyKindLookupThrowsWhenTheTableIsEmpty()` | — | ✔ |
| `theOtherLookupsThrowWhenTheirListWasNeverSet()` | — | ✔ |
| `anEmptyButLoadedListAnswersNullWhereAnEmptyKindTableThrows()` | — | ✔ |
| `theBaseLookupsMatchOnNameTvalOrBoth()` | — | ✔ |
| `slaysAndBrandsMatchOnCodeWhileCursesAndActivationsMatchOnName()` | — | ✔ |
| `theSettersKeepTheirCountersInStepWithTheirLists()` | — | ✔ |


##### `ObjectValueCacheTest.java`

**Responsible for:** Tests ObjectValueCache, the port of C's struct cached_object_data and its once-only fill in compute_ui_entry_values_for_object (ui-entry.c:676-698).

**class `ObjectValueCacheTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertSameFlags(Flag<ObjectFlag>, Flag<ObjectFlag>)` | — | ✘ |
| `getResolvedFlagsIsNullBeforePopulateFlagsIsCalled()` | — | ✘ |
| `populateFlagsCachesRealFlagsWhenPlayerIsNull()` | — | ✘ |
| `populateFlagsCachesKnownFlagsWhenPlayerIsGiven()` | — | ✘ |
| `populateFlagsWithPlayerAndNoKnownCounterpartCachesNothing()` | — | ✘ |
| `populateFlagsIsANoOpOnceTheCacheIsFilled()` | — | ✘ |
| `setResolvedFlagsBypassesTheOnceOnlyGuard()` | — | ✘ |


##### `PlayerRegistryFoodThresholdsTest.java`

**Responsible for:** Tests PlayerRegistry#setPlayerTimedEffects(List) and the six getPyFood* accessors — the port of C filling its PY_FOOD_* globals from the FOOD timed effect (player-timed.c:321-336).

**class `PlayerRegistryFoodThresholdsTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (3/3 documented): ✔ `FIELDS`, `savedThresholds`, `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveGlobals()` | — | ✔ |
| `restoreEffects()` | — | ✔ |
| `field(String)` | — | ✔ |
| `grade(int, int, String)` | — | ✔ |
| `effect(TimedEffect, List<TimedGrade>)` | — | ✔ |
| `shippedFoodGrades()` | — | ✔ |
| `thresholds()` | — | ✔ |
| `clearThresholds()` | — | ✔ |
| `restoreThresholds()` | — | ✔ |

**class `PlayerRegistryFoodThresholdsTest.ShippedData`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fillsAllSix()` | — | ✔ |
| `registersTheEffects()` | — | ✔ |
| `thresholdsAscend()` | — | ✔ |

**class `PlayerRegistryFoodThresholdsTest.NameMatching`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `followsTheName()` | — | ✔ |
| `ignoresUnknownName()` | — | ✔ |
| `ignoresDummyName()` | — | ✔ |

**class `PlayerRegistryFoodThresholdsTest.OtherEffects`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leaveThresholdsAlone()` | — | ✔ |
| `noFoodEffect()` | — | ✔ |
| `emptyList()` | — | ✔ |


##### `PlayerRegistryLookupRealmTest.java`

**Responsible for:** Tests PlayerRegistry#lookupRealm, added on 260831.

**class `PlayerRegistryLookupRealmTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `saved`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field()` | — | ✔ |
| `realm(String)` | — | ✔ |
| `loadTheGameRealms()` | — | ✔ |
| `snapshot()` | — | ✘ |
| `restore()` | — | ✘ |
| `findsARealmSpeltExactlyAsItWasLoaded()` | — | ✘ |
| `findsARealmWhateverCaseTheCallerSpeltItIn()` | — | ✘ |
| `findsARealmWhateverCaseItWasLoadedIn()` | — | ✘ |
| `handsBackTheStoredInstanceRatherThanACopy()` | — | ✘ |
| `takesTheFirstMatchInLoadOrder()` | — | ✘ |
| `failsWithCsMessageWhenNoRealmBearsTheName()` | — | ✘ |
| `failsOnAPartialNameBecauseCComparesWholeStrings()` | — | ✘ |
| `failsWhenNoRealmsWereLoadedAtAll()` | — | ✘ |
| `refusesToReadTheRegistryBeforeItIsLoaded()` | — | ✘ |


##### `PlayerRegistryLookupsTest.java`

**Responsible for:** Tests the shape, body, race and history-chart side of PlayerRegistry, added on 261008: the two derived counters, the four lookups, and the list getters.

**class `PlayerRegistryLookupsTest`** — class Javadoc: ✔ · type stamp: on 261008

- Fields (4/4 documented): ✔ `FIELDS`, `saved`, `realBus`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(String)` | — | ✔ |
| `body(String, int)` | — | ✔ |
| `shape(String)` | — | ✔ |
| `theGameShapes()` | — | ✔ |
| `race(String)` | — | ✔ |
| `snapshot()` | — | ✘ |
| `restore()` | — | ✘ |
| `equipSlotsMaxIsTheShippedHumanoidsTwelve()` | — | ✘ |
| `equipSlotsMaxIsTheLargestCountWhereverItSits()` | — | ✘ |
| `equipSlotsMaxStartsFromZeroOnEveryLoadLikeC()` | — | ✘ |
| `lookupPlayerBodyHandsBackACopyNotTheSharedRecord()` | — | ✘ |
| `lookupPlayerBodyIndexesByPositionInLoadOrder()` | — | ✘ |
| `lookupPlayerBodyRejectsAnIndexOffEitherEnd()` | — | ✘ |
| `lookupPlayerBodyRefusesToReadBeforeTheBodiesAreLoaded()` | — | ✘ |
| `shapeMaxCountsTheNineShippedShapes()` | — | ✘ |
| `shapeMaxIsZeroForNoShapes()` | — | ✘ |
| `lookupPlayerShapeFindsTheStoredRecordAndSaysNothing()` | — | ✘ |
| `lookupPlayerShapeComparesCaseExactlyBecauseCUsesStreq()` | — | ✘ |
| `lookupPlayerShapeMissSendsCsMessage()` | — | ✘ |
| `lookupPlayerShapeTakesTheFirstMatch()` | — | ✘ |
| `lookupPlayerShapeRefusesToReadBeforeTheShapesAreLoaded()` | — | ✘ |
| `lookupPlayerRaceFindsAnExactName()` | — | ✘ |
| `lookupPlayerRaceComparesCaseExactlyAndWholeNames()` | — | ✘ |
| `lookupPlayerRaceTakesTheFirstMatch()` | — | ✘ |
| `lookupPlayerRaceRefusesToReadBeforeTheRacesAreLoaded()` | — | ✘ |
| `racesAreKeptInFileOrder()` | — | ✔ |
| `lookupPlayerHistoryChartFindsAChartByNumber()` | — | ✘ |
| `lookupPlayerHistoryChartReturnsNullForAnUnknownNumber()` | — | ✘ |
| `lookupPlayerHistoryChartTakesTheFirstMatch()` | — | ✘ |
| `lookupPlayerHistoryChartRefusesToReadBeforeTheChartsAreLoaded()` | — | ✘ |
| `theListGettersAreReadOnlyViews()` | — | ✘ |
| `theListGettersFailBeforeTheListIsLoaded()` | — | ✘ |

**class `PlayerRegistryLookupsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `types`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerRegistryTimedEffectTest.java`

**Responsible for:** Tests PlayerRegistry#lookupPlayerTimedEffect, added on 260818.

**class `PlayerRegistryTimedEffectTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (1/1 documented): ✔ `saved`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field()` | — | ✔ |
| `definitionFor(TimedEffect)` | — | ✔ |
| `load(TimedEffect)` | — | ✔ |
| `snapshot()` | — | ✘ |
| `restore()` | — | ✘ |
| `findsTheDefinitionForALoadedEffect()` | — | ✘ |
| `handsBackTheStoredInstanceRatherThanACopy()` | — | ✘ |
| `answersNullForAnEffectWithNoLoadedDefinition()` | — | ✘ |
| `answersNullForTheSentinelWhichIsNeverLoaded()` | — | ✘ |
| `answersNullWhenNothingHasBeenLoaded()` | — | ✘ |
| `findsTheFirstDefinitionWhenAnEffectIsLoadedTwice()` | — | ✘ |
| `throwsRatherThanReturningNullWhenTheRegistryIsUninitialised()` | — | ✔ |


##### `StatTablesTest.java`

**Responsible for:** Tests StatTables, the hand-transcribed adj_* arrays from src/player-calcs.c.

**class `StatTablesTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (2/2 documented): ✔ `STAT_RANGE`, `PROBES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `expectedProbes()` | — | ✔ |
| `tables()` | — | ✔ |

**class `StatTablesTest.Shape`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyTableHasOneEntryPerCompressedStatRung()` | — | ✘ |
| `everyTableCIsDeclaredHasBeenPorted()` | — | ✘ |

**class `StatTablesTest.Values`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyTableMatchesCAtTheProbedIndices()` | — | ✘ |
| `noTableEverDecreasesAsTheStatRises()` | — | ✔ |
| `theRegenerationTableStartsAtZeroSoTheWorstConStillRecovers()` | — | ✔ |


##### `TimedElementEffectProbeTest.java`

**Responsible for:** Throwaway probe for get_timed_element_effect via computeForPlayer.

**class `TimedElementEffectProbeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `ENTRY`, `RESIST`, `saved`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field()` | — | ✘ |
| `plainShape()` | — | ✘ |
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `aux(ElementEnum, TimedEffect)` | — | ✔ |
| `oppAcidResistsAcidNotElec()` | — | ✘ |
| `oppElecResistsElecNotAcid()` | — | ✘ |
| `eachOppEffectMapsToItsOwnElement()` | — | ✘ |
| `lastEffectInFileDoesNotThrow()` | — | ✘ |
| `nonResistingEffectsContributeNothing()` | — | ✘ |
| `noEffectsActive()` | — | ✘ |
| `definitionMissingDoesNotThrow()` | — | ✘ |


##### `UIEntryValueRegistryBoundariesTest.java`

**Responsible for:** Boundary cases for UIEntryValueRegistry that UIEntryValueRegistryTest does not reach: every object-property family in compute_ui_entry_values_for_object (modifier, stat, ignore, resist, immunity, vulnerability), the have_value override and the unknown sentinel for each, the auxiliary and ENTRY_FL...

**class `UIEntryValueRegistryBoundariesTest`** — class Javadoc: ✔ · type stamp: on 261006

- Fields (0/1 documented): ✘ `ENTRY`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noEntryFlags()` | — | ✘ |
| `timedAsAuxEntryFlags()` | — | ✘ |
| `modProperty(ObjPropertyType, ObjectModifier, Integer, boolean)` | — | ✘ |
| `flagProperty(ObjectFlag, Integer, boolean)` | — | ✘ |
| `elementProperty(ObjPropertyType, ElementEnum, Integer, boolean)` | — | ✘ |
| `playerFlagProperty(PlayerFlag, int, boolean, boolean)` | — | ✘ |
| `objectFlagPlayerProperty(ObjectFlag, boolean)` | — | ✘ |
| `elementPlayerProperty(ElementEnum, boolean)` | — | ✘ |
| `rawItem(Flag<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, ItemObject)` | — | ✘ |
| `plainItem()` | — | ✔ |
| `itemWithModifier(ObjectModifier, int)` | — | ✘ |
| `itemWithElement(ElementEnum, int, boolean)` | — | ✘ |
| `itemWithUnknownResist(ElementEnum, int)` | — | ✔ |
| `curse(int, ItemObject)` | — | ✘ |
| `carry(ItemObject, Curse, int)` | — | ✘ |
| `newPlayer()` | — | ✘ |
| `race(PlayerBody, int, Map<ElementEnum,ElementInfo>)` | — | ✘ |
| `plainShape()` | — | ✘ |
| `modifierPlayer(int)` | — | ✔ |
| `forObject(ItemObject, Player)` | — | ✘ |
| `forPlayer(Player)` | — | ✘ |
| `bindObject(CombinerName, Flag<ChannelEntryFlag>, ObjectProperty)` | — | ✘ |
| `bindPlayer(Flag<ChannelEntryFlag>, PlayerProperty)` | — | ✘ |
| `resetRegistry()` | — | ✘ |

**class `UIEntryValueRegistryBoundariesTest.ObjectFamilies`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `modifierReadsTheItemsValue()` | — | ✘ |
| `statReadsTheModifierTable()` | — | ✘ |
| `absentModifierContributesNothing()` | — | ✘ |
| `fixedValueOnAModifier()` | — | ✘ |
| `modifierKnowledge()` | — | ✘ |
| `ignoreReadsTheIgnoreFlag()` | — | ✘ |
| `ignoreWithAFixedValue()` | — | ✘ |
| `resistFamilyReadsTheResistanceLevel()` | — | ✘ |
| `fixedValueOnAResistance()` | — | ✘ |
| `elementKnowledge()` | — | ✘ |
| `curseModifierAdds()` | — | ✘ |
| `unpoweredCurseModifierIsIgnored()` | — | ✘ |
| `curseResistanceAdds()` | — | ✘ |
| `cursesAreVisitedInIndexOrder()` | — | ✘ |

**class `UIEntryValueRegistryBoundariesTest.ObjectAuxiliary`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `modifierAndSustainSplit()` | — | ✘ |
| `onlyAuxiliaryForcesMainToZero()` | — | ✘ |
| `unknownAuxiliary()` | — | ✘ |
| `timedAuxSkipsAuxiliaryProperties()` | — | ✘ |
| `timedAuxWithOnlyAuxiliaryProperties()` | — | ✘ |

**class `UIEntryValueRegistryBoundariesTest.PlayerAbilities`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentAbilityIsSkipped()` | — | ✘ |
| `boundValueIsUsedAsItStands()` | — | ✘ |
| `auxiliaryBoundValueSwaps()` | — | ✘ |
| `unhandledSpecialAbilityContributesNothing()` | — | ✘ |
| `timedAuxSkipsAnAuxiliaryAbility()` | — | ✘ |
| `timedAuxSkipsAnAuxiliaryObjectAbility()` | — | ✘ |
| `shapeFlagNeedsKnowledge()` | — | ✘ |
| `elementAbilityRaceAndShape()` | — | ✘ |
| `auxiliaryElementAbilitySwaps()` | — | ✘ |

**class `UIEntryValueRegistryBoundariesTest.PlayerModifiers`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bindSpeedAsTimedAux()` | — | ✘ |
| `timedSpeed(TimedEffect)` | — | ✘ |
| `timedSpeedTable()` | — | ✘ |
| `timedStealthInfraAndBlows()` | — | ✘ |
| `timedModifierDefaultsToZero()` | — | ✘ |
| `timedEffectIgnoredWithoutTheFlag()` | — | ✘ |
| `shapeModifierAndTimedSplit()` | — | ✘ |
| `timedAuxSkipsAnAuxiliaryModifier()` | — | ✘ |
| `auxiliaryModifierSwaps()` | — | ✘ |
| `nonModifierObjectPropertiesAreIgnored()` | — | ✘ |
| `racialSkillConversion()` | — | ✘ |
| `stealthTakesNoRacialSkill()` | — | ✘ |
| `auxiliaryRacialSkillSwaps()` | — | ✘ |

**class `UIEntryValueRegistryBoundariesTest.Registration`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullPlayerListKeepsTheExistingOne()` | — | ✘ |


##### `UIEntryValueRegistryTest.java`

**Responsible for:** Tests UIEntryValueRegistry, the registry of ObjectProperty/PlayerProperty bindings to named UI entries and the port of C's compute_ui_entry_values_for_object, compute_ui_entry_values_for_player and is_ui_entry_for_known_rune (ui-entry.c).

**class `UIEntryValueRegistryTest`** — class Javadoc: ✔ · type stamp: on 260924

- Fields (0/1 documented): ✘ `ENTRY`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noEntryFlags()` | — | ✘ |
| `timedAsAuxEntryFlags()` | — | ✘ |
| `uiBinding(Integer, boolean)` | — | ✘ |
| `statOrModProperty(ObjPropertyType, ObjectModifier, Integer, boolean)` | — | ✘ |
| `flagProperty(ObjectFlag, Integer, boolean)` | — | ✘ |
| `elementProperty(ObjPropertyType, ElementEnum, Integer, boolean)` | — | ✘ |
| `unrecognisedTypeProperty()` | — | ✘ |
| `playerFlagProperty(PlayerFlag, int, boolean, boolean)` | — | ✘ |
| `objectFlagPlayerProperty(ObjectFlag, boolean)` | — | ✘ |
| `elementPlayerProperty(ElementEnum, boolean)` | — | ✘ |
| `rawItem(Flag<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, LinkedHashMap<Curse,CurseData>, ItemObject)` | — | ✔ |
| `plainItem(ObjectFlag)` | — | ✘ |
| `itemWithUnknownFlag(ObjectFlag)` | — | ✔ |
| `curseWithFlag(ObjectFlag)` | — | ✘ |
| `curseWithFlag(ObjectFlag, int)` | — | ✔ |
| `classWithFlags(Flag<ObjectFlag>)` | — | ✔ |
| `newPlayer()` | — | ✘ |
| `race(PlayerBody, int, int)` | — | ✔ |
| `plainShape()` | — | ✔ |
| `resetRegistry()` | — | ✘ |

**class `UIEntryValueRegistryTest.UnregisteredEntry`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `computeForObjectNotPresent()` | — | ✘ |
| `computeForPlayerNotPresent()` | — | ✘ |
| `isKnownRuneTrue()` | — | ✘ |
| `isKnownRuneTrueForEmptyLists()` | — | ✘ |
| `isKnownRuneTrueForNullLists()` | — | ✘ |

**class `UIEntryValueRegistryTest.AddEntryBinding`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullObjectPropertiesPreservesTheExistingList()` | — | ✘ |
| `nonNullObjectPropertiesReplacesThePreviousList()` | — | ✘ |

**class `UIEntryValueRegistryTest.ClearEntryBindings`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `removesAPreviouslyRegisteredEntry()` | — | ✘ |

**class `UIEntryValueRegistryTest.IsKnownRune`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `trueWhenEverythingIsKnown()` | — | ✘ |
| `falseWhenStatOrModUnknown()` | — | ✘ |
| `falseWhenFlagUnknown()` | — | ✘ |
| `falseWhenResistFamilyUnknown()` | — | ✘ |
| `falseForUnrecognisedObjectPropertyType()` | — | ✘ |
| `playerTypePropertyIsSkipped()` | — | ✘ |
| `falseWhenObjectTypePlayerPropertyUnknown()` | — | ✘ |
| `falseWhenElementTypePlayerPropertyUnknown()` | — | ✘ |

**class `UIEntryValueRegistryTest.ComputeForObject`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullItemNotPresent()` | — | ✘ |
| `boundFlagContributesItsRealValue()` | — | ✘ |
| `fixedBoundValueOverridesTheRealOne()` | — | ✘ |
| `auxBoundPropertyRoutesToTheAuxiliaryTotal()` | — | ✘ |
| `curseWithNonZeroPowerContributes()` | — | ✘ |
| `curseWithZeroPowerContributesNothing()` | — | ✘ |
| `curseFlagAccumulatesWithTheBaseItem()` | — | ✘ |
| `onlyPoweredCursesAreCounted()` | — | ✘ |
| `partlyKnownCurse(ObjectFlag, boolean)` | — | ✔ |
| `shadowOnlyCurse(ObjectFlag)` | — | ✔ |
| `fullyKnownHost(LinkedHashMap<Curse,CurseData>)` | — | ✔ |
| `playerSeesACurseFlagItsShadowHas()` | — | ✘ |
| `playerReadsTheShadowNotTheRealFlag()` | — | ✘ |
| `playerNeedsTheRealFlagAsWellAsTheShadow()` | — | ✘ |
| `playerAssessingACurseWithABlankShadow()` | — | ✘ |
| `playerCannotYetSeeACurseFlag()` | — | ✘ |
| `unknownPropertyContributesTheUnknownSentinel()` | — | ✘ |

**class `UIEntryValueRegistryTest.ComputeForPlayer`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullPlayerNotPresent()` | — | ✘ |
| `fastShotContributesZeroWithNoLauncher()` | — | ✘ |
| `fastShotContributesLevelDividedByThreeWithALauncher()` | — | ✘ |
| `braveryThirtyContributesAtTheLevelThreshold()` | — | ✘ |
| `objectTypePropertySumsCacheAndShapeContributions()` | — | ✘ |
| `tunnelModifierAddsTheRacialSkillContribution()` | — | ✘ |
| `infraModifierAddsTheRacesInfravision()` | — | ✘ |
| `timedAsAuxSubstitutesTheTimedEffectAsTheAuxiliaryChannel()` | — | ✘ |


##### `WorldRegistryTest.java`

**Responsible for:** Tests WorldRegistry: the size accessors and the level and projection lookups.

**class `WorldRegistryTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/5 documented): ✘ `WORLD_FILE`, `PROJECTION_FILE`, `savedWorlds`, `savedProjections`, `savedQuests`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `field(String)` | — | ✘ |
| `loadWorlds()` | — | ✘ |
| `loadProjections()` | — | ✘ |
| `quest(int, String)` | — | ✘ |
| `maxRandDepthIsRecordCount()` | — | ✘ |
| `levelByNameTown()` | — | ✘ |
| `levelByNameDeepest()` | — | ✘ |
| `levelByNameMisses()` | — | ✘ |
| `levelByDepthHits()` | — | ✘ |
| `levelByDepthMisses()` | — | ✘ |
| `nameAndDepthAgree()` | — | ✘ |
| `worldsViewIsReadOnly()` | — | ✘ |
| `worldGuards()` | — | ✘ |
| `projMaxIsProjMax()` | — | ✘ |
| `projectionTypeCounts()` | — | ✘ |
| `byCodeHits()` | — | ✘ |
| `everyProjectionRoundTrips()` | — | ✘ |
| `byCodeMiss()` | — | ✘ |
| `byNameHits()` | — | ✘ |
| `byNameMisses()` | — | ✘ |
| `byTypeFirstInFile()` | — | ✘ |
| `byTypeNone()` | — | ✘ |
| `projectionsViewIsReadOnly()` | — | ✘ |
| `projectionGuards()` | — | ✘ |
| `questMaxIsRecordCount()` | — | ✘ |
| `questMaxEmpty()` | — | ✘ |
| `questsViewIsReadOnly()` | — | ✘ |


## middle/gameinput

##### `DefaultGameInputTest.java`

**Responsible for:** Tests for DefaultGameInput, the port of the absent-hook paths of game-input.c.

**class `DefaultGameInputTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (0/1 documented): ✘ `input`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `directionsAbort()` | — | ✘ |
| `itemAborts()` | — | ✘ |
| `quantityAborts()` | — | ✘ |
| `stringAborts()` | — | ✘ |
| `spellsAbort()` | — | ✘ |
| `effectFromListFallBack()` | — | ✘ |
| `checkDeclines()` | — | ✘ |
| `comAborts()` | — | ✘ |
| `curseAborts()` | — | ✘ |
| `panelReportsNothing()` | — | ✘ |
| `panelContainsEverything()` | — | ✘ |
| `mapIsVisible()` | — | ✘ |
| `viewAbilityMenuIsNoOp()` | — | ✘ |
| `confirmDebugIsInherited()` | — | ✘ |
| `stateless()` | — | ✘ |


##### `EffectChoiceTest.java`

**Responsible for:** Tests for EffectChoice, the Java replacement for the int that C's get_effect_from_list (game-input.c) returns: a list index when >= 0, -2 for the random option and -1 for a cancelled or invalid selection.

**class `EffectChoiceTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `asCInt(EffectChoice)` | — | ✔ |
| `decodesToCSentinels()` | — | ✘ |
| `indexKeepsValue()` | — | ✘ |
| `equality()` | — | ✘ |
| `indexIsNotASentinel()` | — | ✘ |
| `exactlyThreeCases()` | — | ✘ |
| `defaultFallbackAllowRandom()` | — | ✘ |
| `defaultFallbackNoRandom()` | — | ✘ |


##### `GameInputHolderTest.java`

**Responsible for:** Tests for GameInputHolder, the Java home of C's file-scope get_*_hook pointers in game-input.c.

**class `GameInputHolderTest`** — class Javadoc: ✔ · type stamp: on 261012

**class `GameInputHolderTest.NeverOnPanelInput`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `panelContains(Loc)` | — | ✘ (override) |
| `startUnset()` | — | ✘ |
| `restoreDefault()` | — | ✘ |
| `startsAsDefault()` | — | ✘ |
| `unsetHooksFallBackAsInC()` | — | ✘ |
| `effectFromListFallBack()` | — | ✘ |
| `setInstanceInstalls()` | — | ✘ |
| `installedFakeOverridesOneHook()` | — | ✘ |
| `secondInstallReplacesFirst()` | — | ✘ |
| `nullIsRejectedAndInstanceKept()` | — | ✘ |
| `resetRestoresDefault()` | — | ✘ |


## middle/magic

##### `ClassMagicTest.java`

**Responsible for:** Tests ClassMagic, the port of C's struct class_magic — when a class can cast, how much armour it may wear before its mana suffers, and the books it casts from.

**class `ClassMagicTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `spell(String)` | — | ✔ |
| `book(String, MagicSpell)` | — | ✔ |

**class `ClassMagicTest.FlattenedIndex`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `indicesRunAcrossBooks()` | — | ✔ |
| `roundTrip()` | — | ✔ |
| `outOfRangeIsNull()` | — | ✔ |
| `foreignSpellIsMinusOne()` | — | ✔ |
| `matchedByIdentity()` | — | ✔ |

**class `ClassMagicTest.IndexBoundaries`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lastIndexIsTotalMinusOne()` | — | ✔ |
| `bookStartIsEarlierTotal()` | — | ✔ |
| `emptyBookIsSkipped()` | — | ✔ |
| `emptyFirstBook()` | — | ✔ |
| `everyIndexRoundTrips()` | — | ✔ |
| `noBooksResolvesNothing()` | — | ✔ |

**class `ClassMagicTest.Profile`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `totalIsSummed()` | — | ✔ |
| `figuresKeptApart()` | — | ✔ |
| `booksAreCopied()` | — | ✔ |
| `numBooksIsListSize()` | — | ✔ |
| `bookOrderKept()` | — | ✔ |
| `casterButIlliterate()` | — | ✔ |
| `noneIsZeroFilled()` | — | ✔ |
| `noneSentinel()` | — | ✔ |


##### `MagicBookTest.java`

**Responsible for:** Tests MagicBook, the port of C's struct class_book (player.h) as filled in by parse_class_book, parse_class_spell and write_book_kind (init.c).

**class `MagicBookTest`** — class Javadoc: ✔ · type stamp: on 261010

- Fields (0/4 documented): ✘ `TOUCHED`, `saved`, `magicBase`, `prayerBase`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `snapshot()` | — | ✘ |
| `restore()` | — | ✘ |
| `isolate()` | — | ✘ |
| `registryField(String)` | — | ✘ |
| `base(TValue, String)` | — | ✘ |
| `register(String, ObjectBase)` | — | ✔ |
| `arcane()` | — | ✘ |
| `spells(int)` | — | ✘ |
| `book(TValue, String, boolean, List<MagicSpell>)` | — | ✘ |

**class `MagicBookTest.Construction`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `keepsTheClassBookFields()` | — | ✘ |
| `dungeonBook()` | — | ✘ |
| `numSpellsIsTheSpellCount()` | — | ✘ |
| `svalIsZeroBeforeSetSVal()` | — | ✘ |

**class `MagicBookTest.SetSVal`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstKindIsSvalOne()` | — | ✘ |
| `eachBookGetsItsOwnSval()` | — | ✘ |
| `svalsCountPerTval()` | — | ✘ |
| `sameNameSharesTheKind()` | — | ✘ |
| `unknownNameGivesMinusOne()` | — | ✘ |
| `nameUnderAnotherTvalGivesMinusOne()` | — | ✘ |
| `canBeRerun()` | — | ✘ |
| `unloadedRegistryThrows()` | — | ✘ |


##### `MagicRealmTest.java`

**Responsible for:** Tests for MagicRealm, the Java form of C's struct magic_realm.

**class `MagicRealmTest`** — class Javadoc: ✔ · type stamp: on 261014

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(MagicRealm, String)` | — | ✔ |
| `arcaneRealmKeepsEveryRealmTxtField()` | — | ✘ |
| `divineRealmIsWisdomDriven()` | — | ✘ |
| `bookNounIsStoredVerbatimWhateverItSays()` | — | ✘ |
| `nameIsKeptWithItsCaseSoLookupsDecideWhetherToFoldIt()` | — | ✘ |
| `nullTextFieldsStayNullLikeC()` | — | ✘ |
| `everyStatTheDataFilesCanNameIsKept()` | — | ✘ |


##### `MagicSpellTest.java`

**Responsible for:** Tests MagicSpell, the port of C's struct class_spell (player.h).

**class `MagicSpellTest`** — class Javadoc: ✔ · type stamp: on 261014

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `read(MagicSpell, String)` | — | ✔ |
| `realm(String)` | — | ✔ |
| `record(String, String, String, String, String)` | — | ✔ |
| `constructorMapsEveryMember()` | — | ✔ |
| `effectListIsNotCopied()` | — | ✔ |
| `realmStartsNull()` | — | ✔ |
| `setRealmStoresTheSameObject()` | — | ✔ |
| `setRealmReplacesAndAcceptsNull()` | — | ✔ |
| `spellsOfOneBookShareTheRealm()` | — | ✔ |
| `assemblerKeepsFileOrder()` | — | ✔ |
| `assemblerDoesNotSetTheRealm()` | — | ✔ |
| `malformedScalarDropsOnlyThatSpell()` | — | ✔ |


## middle/monsters

##### `BlowEffectTest.java`

**Responsible for:** Tests for BlowEffect, the Java port of struct blow_effect in mon-blows.h.

**class `BlowEffectTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/3 documented): ✘ `REAL_FILE`, `PROJECTION_FILE`, `savedProjections`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | on 261009 | ✔ |
| `restore()` | on 261009 | ✔ |
| `setProjections(Object)` | — | ✘ |
| `realFile()` | — | ✘ |
| `projection(ProjectionEnum)` | — | ✘ |
| `constructorStoresEveryField()` | — | ✘ |
| `absentValuesPassThrough()` | — | ✘ |
| `flagTypeLeavesElementOnSentinel()` | — | ✘ |
| `numericFieldsAreNotClamped()` | — | ✘ |

**record `BlowEffectTest.Expected`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/10 documented): ✘ `name`, `power`, `eval`, `desc`, `base`, `resistColour`, `immuneColour`, `type`, `resist`, `lash`
- Fields (1/6 documented): ✔ `EXPECTED` — ✘ `LGREEN`, `LRED`, `ORANGE`, `YELLOW`, `MISSILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `realFileHasThirtyRecordsInOrder()` | — | ✘ |
| `realFileScalarFieldsMatchTheCData()` | — | ✘ |
| `realFileResistSplitMatchesTheEffectType()` | — | ✘ |
| `realFileLashTypesMatchTheCData()` | — | ✘ |
| `shatterLashFallsBackToMissile()` | — | ✘ |
| `unsetResistColoursAreNullNotDark()` | — | ✘ |
| `onlyTheFourRawElementsHaveAnImmuneColour()` | — | ✘ |


##### `MonsterBaseTest.java`

**Responsible for:** Pins MonsterBase against struct monster_base in monster.h and the monster_base.txt records that C's parse_mon_base_* functions in mon-init.c turn into it.

**class `MonsterBaseTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `BASE_FILE`, `PAIN_FILE`

**record `MonsterBaseTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/4 documented): ✘ `name`, `glyph`, `pain`, `desc`
- Fields (2/3 documented): ✔ `C_RECORDS`, `savedMonsterPains` — ✘ `bases`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadBases()` | — | ✘ |
| `restorePainSets()` | — | ✘ |
| `readField(MonsterBase, String)` | — | ✘ |
| `named(String)` | — | ✘ |
| `codeNameOnlyConstructorGivesAZeroedStruct()` | — | ✘ |
| `fullConstructorStoresEveryFieldAndKeepsTheFlagSetByReference()` | — | ✘ |
| `theRealFileLoadsOneBasePerRecordInFileOrder()` | — | ✘ |
| `everyRecordCarriesItsGlyphPainSetAndDescText()` | — | ✘ |
| `flagsFromEveryFlagsLineAreOrredTogether()` | — | ✘ |
| `aSharedFlagOnTwoLinesIsSetOnce()` | — | ✘ |
| `aRecordWithNoFlagsLineHasAnEmptyFlagSet()` | — | ✘ |
| `namesKeepTheirCaseAndEmbeddedSpaces()` | — | ✘ |


##### `MonsterBlowConstructionTest.java`

**Responsible for:** Tests the three-argument MonsterBlow constructor and the known flag.

**class `MonsterBlowConstructionTest`** — class Javadoc: ✔ · type stamp: on 261011

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesDiceByReference()` | — | ✘ |
| `startsUnseenAndUnknown()` | — | ✘ |
| `nullPartsStayNull()` | — | ✘ |
| `blankBlowStartsUnknown()` | — | ✘ |
| `setKnownIsPlainAssignment()` | — | ✘ |
| `sightingDoesNotSetKnown()` | — | ✘ |


##### `MonsterBlowTimesSeenTest.java`

**Responsible for:** Tests the blank MonsterBlow and MonsterBlow#incrementBlowTimesSeen().

**class `MonsterBlowTimesSeenTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `blankBlowIsEmpty()` | — | ✘ |
| `countStopsAtUnsignedCharMax(int, int)` | — | ✘ |
| `blowsCountIndependently()` | — | ✘ |


##### `MonsterDropTest.java`

**Responsible for:** Tests MonsterDrop, the Java form of C's struct monster_drop in monster.h, and the two records that parse_monster_drop and parse_monster_drop_base build in mon-init.c.

**class `MonsterDropTest`** — class Javadoc: ✔ · type stamp: on 261014

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✔ |
| `cleanup()` | — | ✔ |
| `read(MonsterDrop, String)` | — | ✔ |
| `dropsOf(String)` | — | ✔ |
| `specificConstructorStoresEverything()` | — | ✘ |
| `baseConstructorLeavesTheKindNull()` | — | ✘ |
| `isBaseIsDecidedByTheKindAlone()` | — | ✘ |
| `boundaryFiguresAreStoredVerbatim()` | — | ✘ |
| `cutpurseDropLoadsAsASpecificDrop()` | — | ✘ |
| `rogueDropLoadsAsASpecificDrop()` | — | ✘ |
| `farmerMaggotDropLoadsAsABaseDrop()` | — | ✘ |
| `identicalDropBaseLinesAreBothKept()` | — | ✘ |
| `everyDropLineInTheFileIsLoaded()` | — | ✘ |


##### `MonsterFriendsTest.java`

**Responsible for:** Tests MonsterFriends, the Java form of C's struct monster_friends, and the MonsterFriends#setRace(String) step that stands in for the friends half of finish_parse_monster in mon-init.c.

**class `MonsterFriendsTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (1/1 documented): ✔ `savedRaces`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistry()` | — | ✔ |
| `restoreRegistry()` | — | ✔ |
| `read(MonsterFriends, String)` | — | ✔ |
| `named(String)` | — | ✔ |
| `member(String)` | — | ✔ |
| `register(String)` | — | ✔ |

**class `MonsterFriendsTest.Construction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `servantLine()` | — | ✔ |
| `roleIsKept()` | — | ✔ |
| `boundaryChances()` | — | ✔ |
| `raceStartsNull()` | — | ✔ |

**class `MonsterFriendsTest.SetRace`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `exactName()` | — | ✔ |
| `caseIgnored()` | — | ✔ |
| `substringFirstWins()` | — | ✔ |
| `exactBeatsEarlierSubstring()` | — | ✔ |
| `containmentOneWay()` | — | ✔ |
| `sameResolvesToOwner()` | — | ✔ |
| `emptyNameFindsNothing()` | — | ✔ |
| `unknownRefused()` | — | ✔ |
| `failureNullsRace()` | — | ✔ |
| `registryNotLoaded()` | — | ✔ |
| `resolutionKeepsOtherValues()` | — | ✔ |


##### `MonsterGridTest.java`

**Responsible for:** Tests the grid a Monster holds.

**class `MonsterGridTest`** — class Javadoc: ✔ · type stamp: on 261012

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `monsterAt(Loc)` | — | ✔ |
| `nullGridBecomesOrigin()` | — | ✘ |
| `givenGridIsKept()` | — | ✘ |
| `explicitOriginIsKept()` | — | ✘ |


##### `MonsterIsCamoflagedTest.java`

**Responsible for:** Tests for Monster#monsterIsCamouflaged(), the port of C's monster_is_camouflaged in mon-predicate.c.

**class `MonsterIsCamoflagedTest`** — class Javadoc: ✔ · type stamp: on 260828

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `monsterWith(MonsterFlag)` | — | ✔ |
| `flagSetIsCamouflaged()` | — | ✘ |
| `noFlagsIsNotCamouflaged()` | — | ✘ |
| `otherFlagsAreNotCamouflage()` | — | ✘ |
| `visibleAndCamouflaged()` | — | ✔ |
| `camouflageWithoutMimickedObject()` | — | ✔ |
| `clearingTheFlagRevealsIt()` | — | ✔ |


##### `MonsterIsSmartTest.java`

**Responsible for:** Tests for Monster.monsterIsSmart(), the port of C's monster_is_smart in mon-predicate.c.

**class `MonsterIsSmartTest`** — class Javadoc: ✔ · type stamp: on 260831

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceWith(MonsterRaceFlag)` | — | ✔ |
| `monsterOf(MonsterRace, MonsterRace)` | — | ✔ |
| `isSmart(Monster)` | — | ✔ |
| `flagSetIsSmart()` | — | ✘ |
| `noFlagsIsNotSmart()` | — | ✘ |
| `otherFlagsAreNotSmart()` | — | ✘ |
| `smartAndStupidAreIndependent()` | — | ✔ |
| `originalRaceSmartCurrentNot()` | — | ✔ |
| `currentRaceSmartOriginalNot()` | — | ✔ |
| `bothAndNeither()` | — | ✔ |
| `nullOriginalRaceFallsThrough()` | — | ✔ |
| `raceIsShared()` | — | ✔ |


##### `MonsterIsStupidTest.java`

**Responsible for:** Tests for Monster.monsterIsStupid(), the port of C's monster_is_stupid in mon-predicate.c.

**class `MonsterIsStupidTest`** — class Javadoc: ✔ · type stamp: on 260831

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceWith(MonsterRaceFlag)` | — | ✔ |
| `monsterOf(MonsterRace, MonsterRace)` | — | ✔ |
| `isStupid(Monster)` | — | ✔ |
| `flagSetIsStupid()` | — | ✘ |
| `noFlagsIsNotStupid()` | — | ✘ |
| `otherFlagsAreNotStupid()` | — | ✘ |
| `stupidAndSmartAreIndependent()` | — | ✔ |
| `currentRaceStupidOriginalNot()` | — | ✔ |
| `originalRaceStupidCurrentNot()` | — | ✔ |
| `raceIsShared()` | — | ✔ |


##### `MonsterLoreBlankRecordTest.java`

**Responsible for:** Tests the two MonsterLore constructors.

**class `MonsterLoreBlankRecordTest`** — class Javadoc: ✔ · type stamp: on 261011

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `countsStartAtZero()` | — | ✘ |
| `knownFlagsStartFalse()` | — | ✘ |
| `collectionsStartEmpty()` | — | ✘ |
| `baseStartsNull()` | — | ✘ |
| `blankRecordsAreIndependent()` | — | ✘ |
| `fullConstructorStoresEveryField()` | — | ✘ |
| `setAllKnownDoesNotRunLoreUpdate()` | — | ✘ |


##### `MonsterLoreCountersTest.java`

**Responsible for:** Tests MonsterLore#setPSkills(int) and MonsterLore#setThefts(int).

**class `MonsterLoreCountersTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zeroedLore()` | — | ✔ |
| `readIntField(MonsterLore, String)` | — | ✔ |
| `setPSkillsStoresValueUnchanged(int)` | — | ✘ |
| `setTheftsStoresValueUnchanged(int)` | — | ✘ |
| `countersAreIndependent()` | — | ✘ |


##### `MonsterLoreInitBlowsTest.java`

**Responsible for:** Tests MonsterLore#initblows(int), which gives a blank lore one blank MonsterBlow per blow its race has, the Java stand-in for C's zeroed lore->blows array.

**class `MonsterLoreInitBlowsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fillsBlankSlots(int)` | — | ✘ |
| `slotsAreDistinct()` | — | ✘ |
| `repeatCallIsIdempotent()` | — | ✘ |
| `mismatchedRepeatCallChangesNothing()` | — | ✘ |
| `negativeCountAddsNothing()` | — | ✘ |
| `nullListIsReplaced()` | — | ✘ |


##### `MonsterPainTest.java`

**Responsible for:** Pins MonsterPain against struct monster_pain in monster.h and the twelve type: blocks of 4.2.6's own lib/gamedata/pain.txt, which C's parse_pain_type and parse_pain_message in mon-init.c turn into it.

**class `MonsterPainTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/2 documented): ✔ `C_ROWS` — ✘ `PAIN_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seven()` | — | ✘ |
| `everyCRowReadsBackAtItsCLevel()` | — | ✔ |
| `loaderOutputMatchesCRows()` | — | ✔ |
| `levelZeroIsMildestAndSixIsWorst()` | — | ✔ |
| `pluralMarkersAreKeptUnresolved()` | — | ✔ |
| `levelOutsideZeroToSixIsRefused()` | — | ✔ |
| `eightMessagesAreRefused()` | — | ✔ |
| `fewerThanSevenMessagesAreRefused()` | — | ✔ |
| `refusalNamesThePainIndex()` | — | ✔ |
| `laterChangesToTheCallersListDoNotReachTheRecord()` | — | ✔ |
| `nullMessageIsRefused()` | — | ✔ |
| `painIndexIsKeptAsGiven()` | — | ✔ |
| `toStringNamesTheIndexAndEveryMessage()` | — | ✔ |


##### `MonsterPredicatesTest.java`

**Responsible for:** Tests for the small struct monster predicates and flag macros in Monster: isUnique (monster_is_unique), monsterIsVisible (monster_is_visible), isObvious (monster_is_obvious), and hasMonsterFlag, monsterFlagOn and monsterFlagOff (mflag_has, mflag_on, mflag_off).

**class `MonsterPredicatesTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceWith(MonsterRaceFlag)` | — | ✔ |
| `monster(MonsterRace, MonsterRace, MonsterFlag)` | — | ✔ |
| `monsterWith(MonsterFlag)` | — | ✘ |
| `uniqueWithoutOriginal()` | — | ✘ |
| `uniqueOriginalCommonShape()` | — | ✘ |
| `commonOriginalUniqueShape()` | — | ✘ |
| `uniqueBothOrNeither()` | — | ✘ |
| `uniqueIgnoresOtherFlags()` | — | ✘ |
| `visibleReadsOnlyVisibleFlag()` | — | ✘ |
| `obviousTruthTable()` | — | ✘ |
| `flagOn()` | — | ✘ |
| `flagOff()` | — | ✘ |
| `visibleToggles()` | — | ✘ |


##### `MonsterRaceCategoryTest.java`

**Responsible for:** Tests for MonsterRaceCategory, the port of C's enum monster_flag_type in monster.h.

**class `MonsterRaceCategoryTest`** — class Javadoc: ✔ · type stamp: on 261008

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsFollowCDeclarationOrder()` | on 261008 | ✔ |
| `flagCountsPerCategoryMatchC()` | on 261008 | ✔ |
| `spotChecksPinOneFlagPerCategory()` | on 261008 | ✔ |


##### `MonsterRaceFlagTableTest.java`

**Responsible for:** Tests for MonsterRaceFlag as the stand-in for C's struct monster_flag and monster_flag_table[] (monster.h, mon-util.c).

**class `MonsterRaceFlagTableTest`** — class Javadoc: ✔ · type stamp: on 261010

- Fields (1/1 documented): ✔ `EXPECTED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyRowMatchesTheCTable()` | on 261010 | ✔ |
| `placeholderRowIsIndexZero()` | on 261010 | ✔ |
| `descriptionIsEmptyExactlyForTheSilentCategories()` | on 261010 | ✔ |
| `lastRowIsNoSlowWithNoSentinel()` | on 261010 | ✔ |


##### `MonsterRaceGetLightTest.java`

**Responsible for:** Tests for MonsterRace#getLight().

**class `MonsterRaceGetLightTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceWithLight(int)` | — | ✔ |
| `returnsStoredValue(int)` | — | ✘ |
| `defaultsToZero()` | — | ✘ |
| `negativeLightIsPreserved()` | — | ✘ |
| `loreBandsAreSeparable()` | — | ✘ |
| `zeroIsTheOnlyInertValue()` | — | ✘ |


##### `MonsterRaceLookupAgreementTest.java`

**Responsible for:** Tests the parts of MonsterRace that MonsterRaceResolutionTest and the other MonsterRace*Test classes leave open: the plain accessors, the flag test, and the exact name-matching rules of MonsterRace#resolveFriends() and MonsterRace#resolveShapes().

**class `MonsterRaceLookupAgreementTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `savedRaces`, `savedBases`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `basesField()` | — | ✔ |
| `raceOf(MonsterFriends)` | — | ✔ |
| `race(String, int, Flag<MonsterRaceFlag>, List<MonsterBlow>, List<MonsterFriends>, List<MonsterShape>)` | — | ✔ |
| `named(String)` | — | ✔ |
| `friend(String)` | — | ✔ |
| `seedRegistry()` | — | ✔ |
| `restoreRegistry()` | — | ✔ |

**class `MonsterRaceLookupAgreementTest.Accessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nameAndLevel()` | — | ✔ |
| `levelZero()` | — | ✔ |
| `bareRaceHasNoName()` | — | ✔ |
| `blowsInOrder()` | — | ✔ |
| `noBlows()` | — | ✔ |

**class `MonsterRaceLookupAgreementTest.FlagTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setFlagTestsTrue()` | — | ✔ |
| `unsetFlagTestsFalse()` | — | ✔ |
| `bareRaceHasNoFlags()` | — | ✔ |
| `testingIsPure()` | — | ✔ |

**class `MonsterRaceLookupAgreementTest.FriendMatching`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `caseInsensitive()` | — | ✔ |
| `substringFallback()` | — | ✔ |
| `firstPartialMatchWins()` | — | ✔ |
| `exactBeatsEarlierPartial()` | — | ✔ |
| `sameResolvesToSelf()` | — | ✔ |
| `everyEntryResolved()` | — | ✔ |
| `emptyNameRefused()` | — | ✔ |

**class `MonsterRaceLookupAgreementTest.ShapeMatching`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addBase(MonsterBase)` | — | ✔ |
| `baseLookupIsCaseSensitive()` | — | ✔ |
| `baseLookupHasNoSubstringFallback()` | — | ✔ |
| `exactRaceBeatsPartial()` | — | ✔ |
| `mixedShapes()` | — | ✔ |
| `emptyShapeRefused()` | — | ✔ |


##### `MonsterRacePopulationAndLoreTest.java`

**Responsible for:** Tests MonsterRace#setCurNum(int), MonsterRace#setMaxNum(int) and MonsterRace#getLore().

**class `MonsterRacePopulationAndLoreTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `readIntField(MonsterRace, String)` | — | ✔ |
| `freshInstanceStartsAtZero()` | — | ✘ |
| `setCurNumStoresValueUnchanged(int)` | — | ✘ |
| `setMaxNumStoresValueUnchanged(int)` | — | ✘ |
| `curNumAndMaxNumAreIndependent()` | — | ✘ |
| `freshRaceHasNoLore()` | — | ✘ |
| `hasLoreTrueAfterSetLore()` | — | ✘ |
| `getLoreReturnsSameReference()` | — | ✘ |


##### `MonsterRaceResolutionTest.java`

**Responsible for:** Tests MonsterRace's second-pass resolution — the two methods that turn the names in monster.txt into the races and bases they refer to.

**class `MonsterRaceResolutionTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `savedRaces`, `savedBases`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `basesField()` | — | ✔ |
| `raceOf(MonsterFriends)` | — | ✔ |
| `race(String, List<MonsterFriends>, List<MonsterShape>)` | — | ✔ |
| `seedRegistry()` | — | ✔ |
| `restoreRegistry()` | — | ✔ |

**class `MonsterRaceResolutionTest.Friends`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `companionResolves()` | — | ✔ |
| `unknownCompanionRefused()` | — | ✔ |
| `noCompanions()` | — | ✔ |

**class `MonsterRaceResolutionTest.Shapes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseNameResolvesToBase()` | — | ✔ |
| `raceNameResolvesToRace()` | — | ✔ |
| `unknownShapeRefused()` | — | ✔ |
| `basePreferredOverRace()` | — | ✔ |

**class `MonsterRaceResolutionTest.SpellMessages`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `variantsAreDistinct()` | — | ✔ |
| `equalityIsByComponents()` | — | ✔ |


##### `MonsterShapeTest.java`

**Responsible for:** Pins MonsterShape against struct monster_shape in monster.h and C's parse_monster_shape in mon-init.c.

**class `MonsterShapeTest`** — class Javadoc: ✔ · type stamp: on 261011

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newShapeIsUnresolved()` | — | ✔ |
| `nameIsKeptVerbatim()` | — | ✔ |
| `setBaseLeavesRace()` | — | ✔ |
| `setRaceLeavesBase()` | — | ✔ |
| `settersAreIndependent()` | — | ✔ |
| `nullClears()` | — | ✔ |
| `laterSetReplaces()` | — | ✔ |
| `race(String)` | — | ✔ |


##### `MonsterTimedTest.java`

**Responsible for:** Tests for the timed-effect entry points in Monster: getMonTimed, clearTimed (mon_clear_timed) and decrementTimed (mon_dec_timed), both in mon-timed.c.

**class `MonsterTimedTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/1 documented): ✘ `NO_FLAGS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `monsterWith(MonTimed, int)` | — | ✘ |
| `getMonTimed()` | — | ✘ |
| `clearInactive()` | — | ✘ |
| `clearActive()` | — | ✘ |
| `decrementOrdinary()` | — | ✘ |
| `decrementFloor()` | — | ✘ |
| `decrementInactive()` | — | ✘ |

**class `MonsterTimedTest.Recording`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `calls`, `result`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `Recording(Map<MonTimed,Integer>)` | — | ✘ |
| `setTimed(MonTimed, int, Flag<MonTimedFlags>)` | — | ✘ (override) |


##### `MonsterUpdateSmartLearnTest.java`

**Responsible for:** Tests Monster#updateSmartLearn, the port of C's update_smart_learn (mon-util.c:790).

**class `MonsterUpdateSmartLearnTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (4/5 documented): ✔ `SEED_LEARNS`, `SEED_ONE_IN_TWO`, `SEED_RARE_FAILURE`, `known` — ✘ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceWith(MonsterRaceFlag)` | — | ✔ |
| `set(Object, String, Object)` | — | ✘ |
| `get(Object, String)` | — | ✘ |
| `poke(Object, String, Object)` | — | ✘ |
| `setUp()` | — | ✘ |
| `monsterOf(MonsterRace)` | — | ✔ |
| `smart()` | — | ✔ |
| `ordinary()` | — | ✔ |
| `stupid()` | — | ✔ |
| `turnLearningOff()` | — | ✔ |
| `equipOneUnknownItem()` | — | ✔ |
| `neitherFlagNorElement()` | — | ✔ |
| `playerFlagAloneIsNotEnough()` | — | ✔ |
| `elementMaxIsNotAnElement()` | — | ✔ |
| `flagAloneIsEnough()` | — | ✔ |
| `elementAloneIsEnough()` | — | ✔ |
| `flagIsLearnedBothWays()` | — | ✔ |
| `playerFlagIsLearnedBothWays()` | — | ✔ |
| `elementLevelIsCopied()` | — | ✔ |
| `allThreeAtOnce()` | — | ✔ |
| `neutralElementIsLearned()` | — | ✔ |
| `optionOffStopsLearning()` | — | ✔ |
| `stupidMonsterLearnsNothing()` | — | ✔ |
| `oneInTwoAppliesOnlyToTheNotSmart()` | — | ✔ |
| `ordinaryMonsterCanLearn()` | — | ✔ |
| `rareFailureCatchesSmartMonsters()` | — | ✔ |
| `playerLearnsThoughOptionIsOff()` | — | ✔ |
| `playerLearnsFromStupidMonster()` | — | ✔ |
| `sanityReturnStopsThePlayerToo()` | — | ✔ |


##### `SummonTest.java`

**Responsible for:** Tests for Summon, the Java port of struct summon in mon-summon.h.

**class `SummonTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/5 documented): ✘ `SUMMON_FILE`, `PAIN_FILE`, `BASE_FILE`, `savedPains`, `savedBases`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `summon(String, boolean, List<MonsterBase>, MonsterRaceFlag, Summon, String)` | — | ✘ |
| `constructorRoundTrip()` | — | ✘ |
| `setFallbackLeavesTheNameAlone()` | — | ✘ |
| `toStringOptionalParts()` | — | ✘ |
| `toStringFallbackComesFromTheReference()` | — | ✘ |

**class `SummonTest.Expected`** — class Javadoc: ✔ · type stamp: —

- Fields (0/7 documented): ✘ `name`, `msgt`, `uniques`, `bases`, `raceFlag`, `fallback`, `desc`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `readShippedFile()` | — | ✘ |
| `shippedFileMatchesTheDataFile()` | — | ✘ |
| `shippedFallbacksResolveByName()` | — | ✘ |
| `anyIsFirstAndUnrestricted()` | — | ✘ |


## middle/monsters/enums

##### `BlowEffectTypeTest.java`

**Responsible for:** Pins BlowEffectType against the effect-type: values in blow_effects.txt and the streq chain in blow_color() (mon-lore.c).

**class `BlowEffectTypeTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/1 documented): ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sevenConstantsInTheDataFilesOrderOfUse()` | — | ✘ |
| `eachSpellingResolvesToItsConstant()` | — | ✘ |
| `getTypeRoundTripsThroughGetFromString()` | — | ✘ |
| `hyphenAndUnderscoreAreNotInterchangeable()` | — | ✘ |
| `matchIsCaseSensitive()` | — | ✘ |
| `emptyNearMissAndNullInputsResolveToNull()` | — | ✘ |
| `everyEffectTypeInTheShippedDataFileResolves()` | — | ✘ |


##### `MonTimedFlagsTest.java`

**Responsible for:** Pins MonTimedFlags against the MON_TMD_FLG_* defines in mon-timed.h.

**class `MonTimedFlagsTest`** — class Javadoc: ✔ · type stamp: on 261010

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `exactlyThreeFlags()` | on 261010 | ✔ |
| `emptySetHasNoFlag()` | on 261010 | ✔ |
| `eachFlagIsIndependent()` | on 261010 | ✔ |
| `noMessageWithNoFail()` | on 261010 | ✔ |
| `settingTwiceIsIdempotent()` | on 261010 | ✔ |
| `clearingOneLeavesTheRest()` | on 261010 | ✔ |


##### `MonTimedTest.java`

**Responsible for:** Pins MonTimed against the rows of list-mon-timed.h, which C's effects[] table in mon-timed.c is built from.

**class `MonTimedTest`** — class Javadoc: ✔ · type stamp: on 261014

**record `MonTimedTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/8 documented): ✘ `name`, `save`, `stack`, `resist`, `time`, `begin`, `end`, `increase`
- Fields (1/1 documented): ✔ `C_ROWS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `column(MonTimed, String)` | — | ✔ |
| `constantsFollowTheHeaderOrder()` | on 261014 | ✔ |
| `maxCountsTheRealEffects()` | on 261014 | ✔ |
| `everyColumnMatchesTheHeader()` | on 261014 | ✔ |
| `onlySleepAndFearSaveAndRunLong()` | on 261014 | ✔ |
| `stackingRulesPartitionTheEffects()` | on 261014 | ✔ |
| `immunityFlagsIncludeTheOddOnes()` | on 261014 | ✔ |
| `messageColumnsHaveTheCGaps()` | on 261014 | ✔ |
| `capsFitTheTimer()` | on 261014 | ✔ |
| `effectLevelDivisorsFollowTheCaps()` | on 261014 | ✔ |
| `assertLevels(MonTimed, int[][])` | — | ✔ |
| `headerNamesResolveThroughThePrefix()` | on 261014 | ✔ |
| `sentinelRows()` | on 261014 | ✔ |


##### `MonsterFlagTest.java`

**Responsible for:** Pins MonsterFlag against the rows of list-mon-temp-flags.h, which C's MFLAG_* enum in monster.h is built from.

**class `MonsterFlagTest`** — class Javadoc: ✔ · type stamp: on 261014

**record `MonsterFlagTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/2 documented): ✘ `name`, `description`
- Fields (1/1 documented): ✔ `C_ROWS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `description(MonsterFlag)` | — | ✔ |
| `constantsFollowTheHeaderOrder()` | on 261014 | ✔ |
| `ordinalsAreCsIndices()` | on 261014 | ✔ |
| `descriptionsMatchTheHeader()` | on 261014 | ✔ |
| `visibleDescriptionKeepsItsQuotationMarks()` | on 261014 | ✔ |
| `onlyNoneAndMaxHaveNoDescription()` | on 261014 | ✔ |
| `flagsSetAndClearIndependently()` | on 261014 | ✔ |
| `aNewSetIsEmpty()` | on 261014 | ✔ |


##### `MonsterMessageTest.java`

**Responsible for:** Pins MonsterMessage against the rows of list-mon-message.h, which C's enum mon_messages and msg_repository[] in mon-msg.c are built from.

**class `MonsterMessageTest`** — class Javadoc: ✔ · type stamp: on 261014

**record `MonsterMessageTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/4 documented): ✘ `name`, `type`, `omitSubject`, `text`
- Fields (2/2 documented): ✔ `C_ROWS`, `PAIN_NAMES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `expand(String, boolean)` | — | ✔ |
| `count_and_order()` | — | ✘ |
| `every_row_matches_c()` | — | ✘ |
| `mon_msg_75_resolves_by_name()` | — | ✘ |
| `pain_rows()` | — | ✘ |
| `omit_subject_rows()` | — | ✘ |
| `category_rows()` | — | ✘ |
| `ordinary_expansion()` | — | ✘ |
| `moria_death_expansion()` | — | ✘ |
| `unbracketed_rows()` | — | ✘ |
| `templates_are_balanced()` | — | ✘ |
| `none_has_real_text()` | — | ✘ |


##### `MonsterSpellTableTest.java`

**Responsible for:** Tests for MonsterSpell as the stand-in for C's RSF_* list (list-mon-spells.h).

**class `MonsterSpellTableTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (1/1 documented): ✔ `EXPECTED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyRowMatchesTheCTable()` | on 261011 | ✔ |
| `sentinelRowsBracketTheTable()` | on 261011 | ✔ |
| `damageTypesAreBoltBallBreathDirect()` | on 261011 | ✔ |
| `typeCountsMatchTheCHeader()` | on 261014 | ✔ |
| `nameLookupMirrorsGrabName()` | on 261014 | ✔ |
| `typesOf(String)` | on 261011 | ✔ |


##### `MonsterSpellTypeEnumTest.java`

**Responsible for:** Pins MonsterSpellTypeEnum against enum mon_spell_type and the RST_DAMAGE macro in C's mon-spell.h, and the damage test C makes of a whole spell (mon_spell_has_damage() in mon-spell.c) against the per-type isDamage() here.

**class `MonsterSpellTypeEnumTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (3/3 documented): ✔ `C_ENUM`, `RST_DAMAGE`, `C_DAMAGING_SPELLS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsAreCsInCsOrder()` | on 261014 | ✔ |
| `ordinalGivesTheCBitValue()` | on 261014 | ✔ |
| `isDamageIsTheRstDamageMask()` | on 261014 | ✔ |
| `theMaskBoundariesAreNamed()` | on 261014 | ✔ |
| `damagingSpellsAreTheCHeadersFiftyFour()` | on 261014 | ✔ |
| `mixedTypeSpellsFollowTheirDamagingType()` | on 261014 | ✔ |


## middle/numerics

##### `RandomAccessorsTest.java`

**Responsible for:** Tests Random's accessors, guarded setters and evaluation — the port of C's random_value and randcalc (z-rand.h, z-rand.c).

**class `RandomAccessorsTest`** — class Javadoc: ✔ · type stamp: before 260815

- Fields (3/3 documented): ✔ `MAX_DEPTH`, `savedConstants`, `savedWorlds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `termsRoundTrip()` | — | ✔ |

**class `RandomAccessorsTest.Setters`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sensibleValuesStored()` | — | ✔ |
| `zeroIsKept()` | — | ✔ |
| `negativeCountsBecomeZero()` | — | ✔ |
| `negativeBaseIsKept()` | — | ✔ |
| `bonusIsStoredAsGiven()` | — | ✔ |

**class `RandomAccessorsTest.Presence`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportPerTerm()` | — | ✔ |
| `zeroIsNoBonus()` | — | ✔ |
| `emptyAndConstant()` | — | ✔ |

**class `RandomAccessorsTest.StringTerms`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `stringBase()` | — | ✔ |
| `stringSides()` | — | ✔ |

**class `RandomAccessorsTest.RandCalc`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `minimise()` | — | ✔ |
| `maximise()` | — | ✔ |
| `average()` | — | ✔ |
| `extremify()` | — | ✔ |
| `extremifyNegated()` | — | ✔ |
| `extremifyTie()` | — | ✔ |
| `averageAtDepth()` | — | ✔ |
| `averageTruncates()` | — | ✔ |
| `averageDiceTruncates()` | — | ✔ |
| `levelIgnoredAtTheEnds()` | — | ✔ |
| `randomiseStaysInRange()` | — | ✔ |
| `constantIsConstant()` | — | ✔ |

**class `RandomAccessorsTest.Range`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `endsAreInside()` | — | ✔ |
| `beyondEndsIsOutside()` | — | ✔ |
| `singleFaceDoesNotVary()` | — | ✔ |
| `constantDoesNotVary()` | — | ✔ |
| `bonusAloneVaries()` | — | ✔ |
| `workedOutRange()` | — | ✔ |
| `negatedRange()` | — | ✔ |

**class `RandomAccessorsTest.ParseAndCopy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `parsesDice()` | — | ✔ |
| `parsesNegated()` | — | ✔ |
| `emptyParsesToNull()` | — | ✔ |
| `loneMinusParsesToNull()` | — | ✔ |
| `negatedVariableParsesToNull()` | — | ✔ |
| `parsesFullForm()` | — | ✔ |
| `parsesConstant()` | — | ✔ |
| `parsesImpliedSingleDie()` | — | ✔ |
| `negatedBaseIsShifted()` | — | ✔ |
| `copyIsIndependent()` | — | ✔ |
| `copyOfNegatedKeepsBase()` | — | ✔ |

**class `RandomAccessorsTest.Negation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `negateFlipsTheRange()` | — | ✔ |
| `negationHappensOnce()` | — | ✔ |
| `constructorNegates()` | — | ✔ |
| `constructorLeavesBaseAlone()` | — | ✔ |
| `constructorNegationIsFinal()` | — | ✔ |


##### `RandomChanceTest.java`

**Responsible for:** Unit tests for RandomChance, the port of random_chance, random_chance_check and random_chance_scaled (z-rand.c).

**class `RandomChanceTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (0/1 documented): ✘ `SEED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedTheGenerator()` | — | ✘ |
| `scaleReproducesTheWorkedExamplesInCsComment()` | — | ✘ |
| `scaleOfEightInOneHundredAndTwentyFiveIsSixtyFourAtOneThousand()` | — | ✘ |
| `scaleTruncatesRatherThanRounds()` | — | ✘ |
| `scaleTruncatesTowardsZeroForNegatives()` | — | ✘ |
| `scaleOfZeroOrOfAWholeChance()` | — | ✘ |
| `scaleProductWrapsAs32BitIntegerLikeC()` | — | ✘ |
| `scaleWithZeroDenominatorThrows()` | — | ✘ |
| `scaleDoesNotRoll()` | — | ✘ |
| `aNumeratorEqualToTheDenominatorAlwaysSucceeds()` | — | ✘ |
| `aNumeratorAboveTheDenominatorAlwaysSucceeds()` | — | ✘ |
| `aZeroNumeratorNeverSucceedsWhenTheDenominatorExceedsOne()` | — | ✘ |
| `aNegativeNumeratorNeverSucceedsWhenTheDenominatorExceedsOne()` | — | ✘ |
| `denominatorOfOneRollsAFixedZero()` | — | ✘ |
| `denominatorOfZeroRollsAFixedZero()` | — | ✘ |
| `aNegativeDenominatorIsRejected()` | — | ✘ |
| `aDenominatorAboveTheRollLimitIsRejected()` | — | ✘ |
| `oneInTwoReachesBothOutcomes()` | — | ✘ |
| `successRateMatchesTheStatedChance()` | — | ✘ |


##### `RandomConstantsTest.java`

**Responsible for:** Tests Random#Zero() and Random#One(), the Java stand-ins for C's inline random_value literal { 0, 0, 0, 0 } (and, for One(), a 1d1 that the C source never names).

**class `RandomConstantsTest`** — class Javadoc: ✔ · type stamp: on 261003

- Fields (4/4 documented): ✔ `MAX_DEPTH`, `ASPECTS`, `savedConstants`, `savedWorlds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |

**class `RandomConstantsTest.Zero`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `termsAreZero()` | — | ✔ |
| `rollsZero()` | — | ✔ |
| `validAndVaries()` | — | ✔ |

**class `RandomConstantsTest.One`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `terms()` | — | ✔ |
| `rollsOne()` | — | ✔ |
| `validAndVaries()` | — | ✔ |

**class `RandomConstantsTest.Independence`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `distinctInstances()` | — | ✔ |
| `zeroIsolated()` | — | ✔ |
| `oneIsolated()` | — | ✔ |


##### `RandomValueUtilsDistributionTest.java`

**Responsible for:** Tests the parts of RandomValueUtils that RandomValueUtilsTest leaves out: the edges of the uniform draws, and the normal-distribution family (normal, sample, mBonus, mBonusCalc) that the port builds on Box-Muller where C uses a table.

**class `RandomValueUtilsDistributionTest`** — class Javadoc: ✔ · type stamp: on 261005

- Fields (2/2 documented): ✔ `MANY`, `SEED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertFractionBetween(double, double, double, String)` | — | ✔ |
| `seedTheGenerator()` | — | ✘ |

**class `RandomValueUtilsDistributionTest.UniformEdges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theLargestBoundCIsWillingToTakeStillDraws()` | — | ✘ |
| `aBoundAboveCsAssertLimitIsRejected()` | — | ✘ |
| `aZeroBoundGivesZeroOrOneAccordingToTheDrawAboveIt()` | — | ✘ |
| `oneInZeroAndOneInOneAlwaysFire()` | — | ✘ |
| `oneInTwoIsACoinFlip()` | — | ✘ |
| `oneInANegativeNumberIsRejected()` | — | ✘ |
| `aSpreadOfOneReachesExactlyThreeValues()` | — | ✘ |
| `aNegativeSpreadIsRejected()` | — | ✘ |
| `aSpreadIsTheSameDrawAsTheEquivalentRange()` | — | ✘ |
| `aRangeAcrossZeroReachesEveryValueIncludingBothEnds()` | — | ✘ |
| `aRangeOfTwoValuesReachesBoth()` | — | ✘ |
| `aRangeZeroToNMinusOneIsTheSameDrawAsRandInt0()` | — | ✘ |
| `damagingWithOneSidedDiceCountsTheDice()` | — | ✘ |
| `aDamageAspectWithNoDiceIsZeroExceptForTheAverage()` | — | ✘ |
| `averagingTruncatesAnOddTotal()` | — | ✘ |

**class `RandomValueUtilsDistributionTest.SimulatedDivision`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aHalfRemainderRoundsUpHalfTheTime()` | — | ✘ |
| `aQuarterRemainderRoundsUpAQuarterOfTheTime()` | — | ✘ |
| `aZeroDividendIsZero()` | — | ✘ |
| `aNegativeDividendNeverRoundsUp()` | — | ✘ |

**class `RandomValueUtilsDistributionTest.Normal`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSpreadBelowOneReturnsTheMeanUntouched()` | — | ✘ |
| `aWideDrawGoesBeyondTheNarrowCapNormalOnceHad()` | — | ✘ |
| `neverStraysFourStandardDeviationsFromTheMean()` | — | ✘ |
| `isCentredOnTheMean()` | — | ✘ |
| `putsAboutTwoThirdsWithinOneStandardDeviation()` | — | ✘ |
| `aStandardDeviationOfOneIsMostlyTheMean()` | — | ✘ |
| `fallsOnBothSidesOfTheMeanEvenly()` | — | ✘ |
| `isRepeatableFromASeed()` | — | ✘ |

**class `RandomValueUtilsDistributionTest.Sample`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `staysWithinFourStandardDeviationsOfEachHalf()` | — | ✘ |
| `reachesTheUpperBoundAtTwoStandardDeviations()` | — | ✘ |
| `scalesTheLowerHalfByItsOwnDeviation()` | — | ✘ |
| `isCentredOnTheMean()` | — | ✘ |
| `aLopsidedDistributionStretchesTheWiderHalf()` | — | ✘ |

**class `RandomValueUtilsDistributionTest.MBonus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `staysBetweenZeroAndTheMaximum()` | — | ✘ |
| `aMaximumOfZeroGivesZero()` | — | ✘ |
| `atLevelZeroMostRollsAreZero()` | — | ✘ |
| `atMidLevelTheMeanBonusIsHalfTheMaximum()` | — | ✘ |
| `atTheDepthCapMostlyTheMaximum()` | — | ✘ |
| `aLevelPastTheCapBehavesAsTheLevelJustBelowIt()` | — | ✘ |
| `aLargeMaximumUsesItsFullSpread()` | — | ✘ |
| `isRepeatableFromASeed()` | — | ✘ |

**class `RandomValueUtilsDistributionTest.MBonusCalc`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `maximisingAndExtremifyingGiveTheMaximum()` | — | ✘ |
| `minimisingGivesZero()` | — | ✘ |
| `averagingIsMaxTimesLevelOverTheDepthCapInIntegerArithmetic()` | — | ✘ |
| `averagingDoesNotCapTheLevelTheWayRandomisingDoes()` | — | ✘ |
| `randomisingRollsAnMBonus()` | — | ✘ |


##### `RandomValueUtilsTest.java`

**Responsible for:** Unit tests for RandomValueUtils, the port of the C source's random-number helpers (src/z-rand.c).

**class `RandomValueUtilsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `DRAWS`, `SEED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedTheGenerator()` | — | ✘ |

**class `RandomValueUtilsTest.UniformDraws`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `randDivStaysBelowItsBound()` | — | ✘ |
| `randDivReachesBothEndsOfItsRange()` | — | ✘ |
| `aDegenerateBoundAlwaysGivesZero()` | — | ✘ |
| `aNegativeBoundIsRejected()` | — | ✘ |
| `theRejectionReachesTheDelegatingDraws()` | — | ✘ |
| `minValueIsRejectedRatherThanWrapping()` | — | ✘ |
| `theSmallestLegalBoundStillDraws()` | — | ✘ |
| `randInt0IsRandDivUnderAnotherName()` | — | ✘ |
| `randInt1RollsFromOneUpToItsBoundInclusive()` | — | ✘ |
| `aOneSidedDieAlwaysRollsOne()` | — | ✘ |

**class `RandomValueUtilsTest.Spreads`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSpreadStaysWithinItsRadiusOfTheCentre()` | — | ✘ |
| `aSpreadReachesBothExtremesAndTheCentre()` | — | ✘ |
| `aZeroSpreadAlwaysGivesTheCentre()` | — | ✘ |
| `spreadsWorkAroundANegativeCentre()` | — | ✘ |

**class `RandomValueUtilsTest.OneIn`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oneInOneAlwaysFires()` | — | ✘ |
| `oneInNSometimesFiresAndSometimesDoesNot()` | — | ✘ |

**class `RandomValueUtilsTest.Damage`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aDamageRollStaysBetweenItsMinimumAndMaximum()` | — | ✘ |
| `aDamageRollReachesBothItsMinimumAndItsMaximum()` | — | ✘ |
| `rollingNoDiceOrDiceWithNoSidesGivesNoDamage()` | — | ✘ |
| `maximisingAndExtremifyingTakeEveryDieAtItsHighestFace()` | — | ✘ |
| `minimisingTakesEveryDieAtOne()` | — | ✘ |
| `averagingTakesTheMeanFaceAndTruncates()` | — | ✘ |
| `randomisingRollsWithinTheDiceRange()` | — | ✘ |
| `everyAspectResolvesBetweenTheMinimumAndMaximumRoll(DamageAspect)` | — | ✘ |

**class `RandomValueUtilsTest.RangesAndDivision`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRangeWithNoWidthGivesItsOnlyValue()` | — | ✘ |
| `anInvertedRangeIsRejected()` | — | ✘ |
| `aRangeDrawsInclusivelyBetweenItsEnds()` | — | ✘ |
| `anExactDivisionIsAlwaysExact()` | — | ✘ |
| `anInexactDivisionRoundsToOneOfTheTwoNeighbouringIntegers()` | — | ✘ |
| `anInexactDivisionRoundsBothWaysOverManyDraws()` | — | ✘ |
| `dividingByZeroIsAnArithmeticError()` | — | ✘ |

**class `RandomValueUtilsTest.Seeding`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theSameSeedReproducesTheSameSequence()` | — | ✘ |
| `differentSeedsGiveDifferentSequences()` | — | ✘ |
| `theArgumentlessSeedingDoesNotThrow()` | — | ✘ |


##### `RationalCParityTest.java`

**Responsible for:** Checks Rational against C's struct my_rational and its functions in z-util.c: my_rational_construct, my_rational_product, my_rational_sum and my_rational_to_uint.

**class `RationalCParityTest`** — class Javadoc: ✔ · type stamp: on 261014

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertFraction(long, long, Rational)` | — | ✔ |
| `assertNear(BigInteger, BigInteger, double, Rational)` | — | ✔ |

**class `RationalCParityTest.ConstructAgainstC`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reducesToLowestTerms(long, long, long, long)` | — | ✘ |

**class `RationalCParityTest.ProductAgainstC`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchesTheReturnedPair(long, long, long, long, long, long)` | — | ✘ |
| `zeroTimesAnythingIsTheCanonicalZeroWhicheverSideItIsOn()` | — | ✘ |

**class `RationalCParityTest.SumAgainstC`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchesTheReturnedPair(long, long, long, long, long, long)` | — | ✘ |

**class `RationalCParityTest.ToUintAgainstC`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsTheWholeNumberAndTheRemainder(long, long, int, long, long)` | — | ✘ |

**class `RationalCParityTest.WiderRange`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSumPastTheUnsignedMaximumIsExact()` | — | ✘ |
| `aProductPastTheUnsignedMaximumIsExact()` | — | ✘ |
| `aProductWhoseNumeratorPassesTheUnsignedMaximumIsExact()` | — | ✘ |
| `toUintDoesNotSaturateAtTheUnsignedMaximum()` | — | ✘ |

**class `RationalCParityTest.Approximation`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `P`, `Q`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aProductWithATinyFractionIsWithinOneStepOfTheExactValue()` | — | ✘ |
| `aSumWithATinyFractionIsWithinOneStepOfTheExactValue()` | — | ✘ |
| `aProductWithAWholePartKeepsAFractionOfTheRemainingPrecision()` | — | ✘ |
| `negatingTheOperandsNegatesTheApproximation()` | — | ✘ |

**class `RationalCParityTest.Saturation`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `MAX`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSumOverTheMaximumSaturates()` | — | ✘ |
| `aSumJustUnderTheMaximumIsExact()` | — | ✘ |
| `twoHalvesOfTheMaximumMakeTheMaximum()` | — | ✘ |
| `aProductOverTheMaximumSaturates()` | — | ✘ |
| `saturationKeepsTheSignOfTheResult()` | — | ✘ |
| `toUintSaturatesThroughTheProduct()` | — | ✘ |
| `aProductWholePartOfExactlyTheMaximumWithARoundingBitCannotBeHeld()` | — | ✘ |


##### `RationalTest.java`

**Responsible for:** Unit tests for Rational, the exact fraction type the game uses where floating-point drift would accumulate over many turns.

**class `RationalTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertFraction(int, int, Rational)` | — | ✔ |
| `toStringShowsBothHalvesOfTheReducedFraction()` | — | ✘ |

**class `RationalTest.Construction`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singleArgumentConstructorGivesADenominatorOfOne()` | — | ✘ |
| `constructionReducesToLowestTerms()` | — | ✘ |
| `alreadyReducedFractionsAreLeftAlone()` | — | ✘ |
| `zeroNumeratorNormalisesToZeroOverOne()` | — | ✘ |
| `suppressingSimplificationKeepsTheRawPair()` | — | ✘ |
| `signIsCarriedByTheNumeratorWhicheverArgumentItArrivedOn()` | — | ✘ |
| `twoNegativesCancelToAPositiveFraction()` | — | ✘ |
| `aZeroDenominatorIsRejected()` | — | ✘ |
| `aZeroDenominatorIsRejectedEvenWithSimplificationOff()` | — | ✘ |
| `theSharedConstantsHoldTheirDocumentedValues()` | — | ✘ |

**class `RationalTest.Arithmetic`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `additionOverACommonDenominator()` | — | ✘ |
| `additionSimplifiesItsResult()` | — | ✘ |
| `additionOfOppositesGivesCanonicalZero()` | — | ✘ |
| `additionLeavesBothOperandsUntouched()` | — | ✘ |
| `subtraction()` | — | ✘ |
| `multiplication()` | — | ✘ |
| `multiplicationByZeroCollapsesToZero()` | — | ✘ |
| `multiplicationByANegativeFlipsTheSign()` | — | ✘ |
| `division()` | — | ✘ |
| `divisionByANegativePutsTheSignOnTheNumerator()` | — | ✘ |
| `dividingByZeroIsRejected()` | — | ✘ |

**class `RationalTest.WholeAndFractionalParts`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `integerPartTruncatesTowardsZero()` | — | ✘ |
| `integerPartOfAWholeNumberIsItself()` | — | ✘ |
| `scalingBeforeTakingTheIntegerPart()` | — | ✘ |
| `scalingByZeroGivesZero()` | — | ✘ |
| `remainderIsWhatIsLeftAfterTheIntegerPart()` | — | ✘ |
| `remainderOfAWholeNumberIsZero()` | — | ✘ |
| `integerPartAndRemainderReassembleTheOriginal()` | — | ✘ |
| `toUintScalesThenTruncates()` | — | ✘ |

**class `RationalTest.Equality`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fractionsWrittenDifferentlyAreStillEqual()` | — | ✘ |
| `unsimplifiedFractionsCompareByValue()` | — | ✘ |
| `differentValuesAreNotEqual()` | — | ✘ |
| `equalityIsReflexive()` | — | ✘ |
| `nullAndForeignTypesAreNotEqual()` | — | ✘ |

**class `RationalTest.Hashing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `equalFractionsHashAlike()` | — | ✘ |
| `anUnsimplifiedFractionHashesAsItsReducedForm()` | — | ✘ |
| `signVariantsHashAlike()` | — | ✘ |
| `hashingIsRepeatable()` | — | ✘ |
| `hashingDoesNotDisturbTheValue()` | — | ✘ |
| `equalFractionsCollapseInAHashSet()` | — | ✘ |
| `aFractionFoundAsAMapKeyRegardlessOfHowItWasWritten()` | — | ✘ |
| `distinctValuesGetDistinctHashes()` | — | ✘ |


## middle/objects

##### `ArcheryTest.java`

**Responsible for:** Tests for Archery, the port of struct archery and its table in obj-power.c.

**class `ArcheryTest`** — class Javadoc: ✔ · type stamp: on 261007

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rowRoundTripsEveryField(TValue, int, int, int)` | — | ✔ |
| `distinctValuesLandInTheirOwnFields()` | — | ✔ |
| `multiplierDividesBackOutAsInC(TValue, int, int)` | — | ✔ |


##### `ArtifactAccessorsTest.java`

**Responsible for:** Tests Artifact's data-file accessors and its copy.

**class `ArtifactAccessorsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `artifact()` | — | ✔ |
| `elementInfoEntriesCopied()` | — | ✔ |

**class `ArtifactAccessorsTest.AllocationAndCost`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `figuresRoundTrip()` | — | ✔ |
| `depthBoundsOrdered()` | — | ✔ |

**class `ArtifactAccessorsTest.Descriptive`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `subTypeName()` | — | ✔ |
| `diceString()` | — | ✔ |

**class `ArtifactAccessorsTest.ActivationFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `activationComesBack()` | — | ✔ |
| `messageIsTheArtifacts()` | — | ✔ |

**class `ArtifactAccessorsTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `collectionsRebuilt()` | — | ✔ |
| `activationAndDiceDuplicated()` | — | ✔ |
| `brandMembersShared()` | — | ✔ |
| `scalarsSurvive()` | — | ✔ |


##### `ArtifactCopyActivationTest.java`

**Responsible for:** Tests the activation arm of Artifact#copy(): the activation, its message, and the recharge time.

**class `ArtifactCopyActivationTest`** — class Javadoc: ✔ · type stamp: on 261008

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `artifact(Activation, String, Random)` | — | ✔ |
| `activation()` | — | ✔ |
| `messageSurvives()` | — | ✔ |
| `nullMessageStaysNull()` | — | ✔ |
| `activationCopied()` | — | ✔ |
| `nullActivationStaysNull()` | — | ✔ |
| `timeKeepsTerms()` | — | ✔ |
| `nullTimeBecomesZero()` | — | ✔ |
| `originalUntouched()` | — | ✔ |
| `upkeepIsFresh()` | — | ✔ |


##### `ArtifactSetDataTest.java`

**Responsible for:** Tests ArtifactSetData's public accessors - the Java face of C's struct artifact_set_data (obj-randart.h), as built by artifact_set_data_new() in obj-randart.c.

**class `ArtifactSetDataTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (3/3 documented): ✔ `savedArtifacts`, `grond`, `ringil`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `artifact(String)` | — | ✔ |
| `artifactsField()` | — | ✔ |
| `loadTwoArtifacts()` | — | ✔ |
| `restoreArtifacts()` | — | ✔ |
| `scalarSettersRoundTrip()` | — | ✔ |
| `tvalSettersRoundTrip()` | — | ✔ |
| `artifactSettersRoundTrip()` | — | ✔ |
| `artProbsRoundTrip()` | — | ✔ |
| `accumulateAsGetThenSet()` | — | ✔ |
| `lateArtifactHasNoEntry()` | — | ✔ |

**class `ArtifactSetDataTest.Defaults`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startAndIncrementGetters()` | — | ✔ |
| `scalarGettersReturnZero()` | — | ✔ |
| `keyedGettersReturnZero()` | — | ✔ |


##### `ArtifactUpkeepTest.java`

**Responsible for:** Tests ArtifactUpkeep's three flags.

**class `ArtifactUpkeepTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshInstanceAllClear()` | — | ✔ |
| `flagsRoundTripIndependently()` | — | ✔ |
| `eachFlagClearsIndependently()` | — | ✔ |
| `instancesAreIndependent()` | — | ✔ |


##### `BonusSourceTest.java`

**Responsible for:** Tests BonusSource through both its implementations — ItemSource and CurseSource — the abstraction calcBonuses walks its equipment loop over.

**class `BonusSourceTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(TValue, ItemObject)` | — | ✔ |
| `knownObject()` | — | ✔ |
| `curse(List<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, int, int, int)` | — | ✔ |
| `curseWithKnown(List<ObjectFlag>, List<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, int, int, int, int, int, int)` | — | ✔ |
| `curseWithKnown(List<ObjectFlag>, List<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, int, int, int, int, int, int, Map<ElementEnum,ElementInfo>)` | — | ✔ |
| `res(int)` | — | ✔ |

**class `BonusSourceTest.ItemSourceTests`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `combatValues()` | — | ✔ |
| `knownCombatValues()` | — | ✔ |
| `noKnownCounterpart()` | — | ✔ |
| `modifiersAreRaw()` | — | ✔ |
| `resistanceLevels()` | — | ✔ |
| `diggerTest()` | — | ✔ |
| `flagSetIgnoresKnowledge()` | — | ✔ |
| `flagsAreDetached()` | — | ✔ |
| `flagsKnownIntersects()` | — | ✔ |

**class `BonusSourceTest.CurseSourceTests`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `combatValues()` | — | ✔ |
| `noKnownCounterpart()` | — | ✔ |
| `knownCombatValuesLearned()` | — | ✔ |
| `knownCombatValuesNotLearned()` | — | ✔ |
| `knownResistance()` | — | ✔ |
| `noBaseArmour()` | — | ✔ |
| `modifiers()` | — | ✔ |
| `resistanceLevels()` | — | ✔ |
| `neverADigger()` | — | ✔ |
| `flagsAreDetached()` | — | ✔ |
| `flagsKnownIsLearnedSubset()` | — | ✔ |
| `flagsKnownIsFreshAndEmpty()` | — | ✔ |


##### `BrandCopyTest.java`

**Responsible for:** Tests Brand#copy().

**class `BrandCopyTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copyIsEqualButNotSame()` | — | ✘ |
| `copyPreservesEveryField()` | — | ✘ |
| `copyToleratesNullFlags()` | — | ✘ |
| `copyHasSameHashCode()` | — | ✘ |


##### `BrandTest.java`

**Responsible for:** Tests Brand, the port of C's struct brand (object.h).

**class `BrandTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/1 documented): ✘ `REAL_FILE`

**record `BrandTest.Row`** — class Javadoc: ✘ · type stamp: —

- Record components (via `@param`) (0/8 documented): ✘ `code`, `name`, `verb`, `mult`, `oMult`, `power`, `resist`, `vuln`
- Fields (1/1 documented): ✔ `REAL_BRANDS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fire()` | — | ✘ |
| `realFileRecordsCarryEveryFieldAsCDoes()` | — | ✘ |
| `gettersReturnTheConstructorValues()` | — | ✘ |
| `multiplierAndPowerAreIndependentFields()` | — | ✘ |
| `equalWhenEveryFieldMatches()` | — | ✘ |
| `notEqualWhenAnySingleFieldDiffers()` | — | ✘ |
| `sameElementAtTwoStrengthsSharesAHashSetOnlyAsTwoMembers()` | — | ✘ |
| `notEqualToNullOrAnotherType()` | — | ✘ |
| `nullStringFieldsAreEqualAndHashSafely()` | — | ✘ |
| `toStringNamesEveryField()` | — | ✘ |


##### `ChestTrapTest.java`

**Responsible for:** Tests for ChestTrap, ChestTrapCode and the chest trap corner of ObjectRegistry - the three pieces that between them replace C's struct chest_trap and its global chest_traps list (object.h, obj-chest.c).

**class `ChestTrapTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `trap(ChestTrapCode)` | — | ✘ |
| `chestTraps()` | — | ✘ |
| `pvalsAreSuccessivePowersOfTwoFromOne()` | — | ✘ |
| `everyCodeOwnsADistinctBit()` | — | ✘ |
| `noTrapIsFirstAndOwnsBitOne()` | — | ✘ |
| `theCodesFitTheSixteenBitPvalWithRoomForItsSignAndLowBits()` | — | ✘ |
| `aChestPvalIsTheOrOfTheTrapsItCarries()` | — | ✘ |
| `gettersReturnWhatTheConstructorWasGiven()` | — | ✘ |
| `getPValDelegatesToTheCode()` | — | ✘ |
| `setChestTrapsCopiesAndReplaces()` | — | ✘ |
| `shippedTraps()` | — | ✔ |
| `trapName(List<ChestTrap>, int)` | — | ✔ |
| `candidates(List<ChestTrap>, int)` | — | ✔ |
| `disarmKind(List<ChestTrap>, int)` | — | ✘ |
| `chestTrapNameFollowsCsBranches()` | — | ✘ |
| `theLevelGateCountsTrapsAfterLockedWhoseLevelIsAtMostTheChests()` | — | ✘ |
| `springingTheTrapsWalksTheFileInOrderAndStopsAtADestroyer()` | — | ✘ |
| `theDisarmSkillIsChosenFromTheMagicFlagsOfTheCarriedTraps()` | — | ✘ |
| `onlyThePoisonNeedlesAndTheExplosionCarryADeathMessage()` | — | ✘ |


##### `CurseConstructionTest.java`

**Responsible for:** Tests what Curse's constructor does with the object it is handed, and the plain accessors that return the rest of what it was given.

**class `CurseConstructionTest`** — class Javadoc: ✔ · type stamp: on 261005

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curseAround(ItemObject)` | — | ✔ |
| `objectIsTheInstanceGiven()` | — | ✔ |
| `scalarsRoundTrip()` | — | ✔ |
| `weightIsReadLive()` | — | ✔ |
| `flagIsReadLive()` | — | ✔ |


##### `CurseDataFileConformanceTest.java`

**Responsible for:** Checks every curse the real curse.txt defines against what the loaded Curse's own ItemObject holds, after the full load path has run: CurseReader, then ObjectDataLoader.parseCurseKinds().

**class `CurseDataFileConformanceTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/5 documented): ✘ `CURSE_FILE`, `OBJECT_FILE`, `savedElsewhere`, `curses`, `curseKind`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `findField(String)` | — | ✘ |
| `setStatic(String, Object)` | — | ✘ |
| `readDataFile()` | — | ✔ |
| `read(Object, String)` | — | ✘ |
| `describe(Object)` | — | ✔ |
| `check(List<String>, String, Object, Object)` | — | ✘ |
| `everyCurseMatchesTheDataFile()` | — | ✘ |

**class `CurseDataFileConformanceTest.Expected`** — class Javadoc: ✔ · type stamp: —

- Fields (0/11 documented): ✘ `flags`, `values`, `effects`, `dice`, `message`, `name`, `weight`, `toHit`, `toDam`, `toAC`, `time`


##### `CurseDataTest.java`

**Responsible for:** Tests CurseData, the port of C's struct curse_data (src/object.h) — the power a curse has on one object and the countdown to its next effect.

**class `CurseDataTest`** — class Javadoc: ✔ · type stamp: on 260817

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(String)` | — | ✔ |

**class `CurseDataTest.Equality`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `powerAloneDecides()` | — | ✔ |
| `hashFollowsEquality()` | — | ✔ |
| `otherTypesAreNotEqual()` | — | ✔ |
| `mapComparisonUsesIt()` | — | ✔ |
| `zeroPowerEntryDiffersFromAbsentUnderMapEquals()` | — | ✔ |
| `tickingDoesNotMoveHash()` | — | ✔ |

**class `CurseDataTest.State`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `timeoutStepsAndRearms()` | — | ✔ |
| `timeoutIsNotClamped()` | — | ✔ |
| `copyIsIndependent()` | — | ✔ |
| `copyEqualsOriginal()` | — | ✔ |
| `constructorStoresUnchecked()` | — | ✔ |
| `setPowerLeavesTimeout()` | — | ✔ |
| `setTimeoutLeavesPower()` | — | ✔ |
| `countdownFiresOnZero()` | — | ✔ |
| `negativeTimeoutKeepsFalling()` | — | ✔ |


##### `CurseDefinitionTest.java`

**Responsible for:** Tests Curse's definition-side accessors — the ones describing what a curse may attach to and what it will not sit alongside, rather than what it does to an object once attached.

**class `CurseDefinitionTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `base(TValue, String)` | — | ✔ |
| `curse(List<ObjectBase>, List<String>)` | — | ✔ |

**class `CurseDefinitionTest.AllowedBases`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `basesRoundTrip()` | — | ✔ |
| `canAfflictIsMembership()` | — | ✔ |
| `emptyBasesAfflictNothing()` | — | ✔ |

**class `CurseDefinitionTest.Conflicts`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `namesBeforeResolution()` | — | ✔ |
| `resolutionStoresCurses()` | — | ✔ |
| `namesSurviveResolution()` | — | ✔ |
| `conflictFlagsAreSeparate()` | — | ✔ |


##### `CurseIndexAndRoundingTest.java`

**Responsible for:** The two corners of Curse that the other Curse*Test classes leave open: the rounding step sitting right under the 32767 ceiling of C's modify_weight_for_curse (obj-curse.c), and the index being replaceable through Curse#setIndex(int), which CurseAssembler relies on after it reverses its list into ...

**class `CurseIndexAndRoundingTest`** — class Javadoc: ✔ · type stamp: on 261008

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `multiplier(int)` | — | ✔ |
| `roundsUnderTheCeiling()` | — | ✔ |
| `setIndexReplacesIndex()` | — | ✔ |


##### `CurseIsFullyKnownTest.java`

**Responsible for:** Tests ItemObject#isFullyKnown() and ItemObject#hasStandardToH() on the object a Curse owns (Curse#getItemObject()), against the questions C asks of a curse object: object_fully_known, which is object_runes_known followed by object_effect_is_known, and object_non_curse_runes_known beneath it.

**class `CurseIsFullyKnownTest`** — class Javadoc: ✔ · type stamp: on 261004

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistry()` | — | ✔ |
| `curse(int, int, int)` | — | ✔ |
| `curse(int, int, int, Effect, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, Flag<ObjectFlag>)` | — | ✘ |
| `flags(ObjectFlag)` | — | ✘ |
| `effect()` | — | ✘ |

**class `CurseIsFullyKnownTest.StandardToH`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zero()` | — | ✘ |
| `nonZero()` | — | ✘ |
| `otherFiguresIgnored()` | — | ✘ |

**class `CurseIsFullyKnownTest.Combat`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bare()` | — | ✘ |
| `toHit()` | — | ✘ |
| `toDam()` | — | ✘ |
| `toAC()` | — | ✘ |
| `exactComparison()` | — | ✘ |

**class `CurseIsFullyKnownTest.Modifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyKnownMap()` | — | ✘ |
| `unlearned()` | — | ✘ |
| `knownButNotCarried()` | — | ✘ |
| `explicitZero()` | — | ✘ |
| `fullZeroMap()` | — | ✘ |
| `sentinelsIgnored()` | — | ✘ |

**class `CurseIsFullyKnownTest.Elements`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resist(int)` | — | ✘ |
| `noKnownEntry()` | — | ✘ |
| `knownAtZero()` | — | ✘ |
| `knownResistance()` | — | ✘ |
| `vulnerability()` | — | ✘ |
| `zeroLevelEntry()` | — | ✘ |

**class `CurseIsFullyKnownTest.Flags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unlearned()` | — | ✘ |
| `learned()` | — | ✘ |
| `knownSuperset()` | — | ✘ |
| `wrongFlag()` | — | ✘ |

**class `CurseIsFullyKnownTest.Effects`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unknown()` | — | ✘ |
| `sameInstance()` | — | ✘ |
| `distinctInstance()` | — | ✘ |
| `bothNull()` | — | ✘ |


##### `CurseKnownStateTest.java`

**Responsible for:** Tests the state a freshly built Curse starts in and the way the known half of its own object stores what it is given — the parts of C's curse->obj->known that CurseIsFullyKnownTest reaches only indirectly.

**class `CurseKnownStateTest`** — class Javadoc: ✔ · type stamp: on 261005

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistry()` | — | ✔ |
| `bare()` | — | ✔ |

**class `CurseKnownStateTest.Fresh`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyKnownView()` | — | ✔ |
| `ownObjectIsStable()` | — | ✔ |
| `conflictUnresolved()` | — | ✔ |
| `noEffect()` | — | ✔ |

**class `CurseKnownStateTest.Setters`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsAreCopied()` | — | ✔ |
| `flagsAreWiped()` | — | ✔ |
| `elInfoIsStored()` | — | ✔ |
| `elInfoReplaces()` | — | ✔ |
| `modifiersReplace()` | — | ✔ |


##### `CurseModifyWeightTest.java`

**Responsible for:** Tests Curse#modifyWeightForCurse(int), the port of C's modify_weight_for_curse() (obj-curse.c).

**class `CurseModifyWeightTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(int, boolean)` | — | ✔ |

**class `CurseModifyWeightTest.Additive`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addsAndSubtracts()` | — | ✔ |
| `clampsAtZero()` | — | ✔ |
| `saturatesAtShortMax()` | — | ✔ |

**class `CurseModifyWeightTest.Multiplicative`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `scalesByPercentage()` | — | ✔ |
| `roundsToNearest()` | — | ✔ |
| `coercesWeightlessItems()` | — | ✔ |
| `rejectsNegativeMultiplier()` | — | ✔ |
| `objectFlags(ObjectFlag)` | — | ✔ |


##### `CurseNameGuardTest.java`

**Responsible for:** Pins the guard that closes the cursesFactory comparator observation: a Curse can no longer be built with a null or empty name, so ItemObject#CURSE_ORDER never meets one.

**class `CurseNameGuardTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(String, int)` | — | ✘ |
| `nullNameRejected()` | — | ✘ |
| `emptyNameRejected()` | — | ✘ |
| `tiedIndexOrdersByName()` | — | ✘ |
| `indexBeatsName()` | — | ✘ |


##### `CurseNullMapsTest.java`

**Responsible for:** Tests that ItemObject#isFullyKnown() and the two map getters cope with a curse object given null modifier and element maps, as the guard on ItemObject#getElInfo() promises.

**class `CurseNullMapsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistry()` | — | ✔ |
| `curse(Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>)` | — | ✔ |
| `gettersAbsorbNull()` | — | ✘ |
| `fullyKnownWithNullMaps()` | — | ✘ |
| `unlearnedResistanceBlocks()` | — | ✘ |
| `learnedResistanceCompletes()` | — | ✘ |
| `nullKnownElInfoLeavesRealMapAlone()` | — | ✘ |
| `modifierKnowledge()` | — | ✘ |


##### `CurseObjectTimeTest.java`

**Responsible for:** Tests the read of C's curse->obj->time, which the port makes as curse.getItemObject().getTime().

**class `CurseObjectTimeTest`** — class Javadoc: ✔ · type stamp: on 261005

- Fields (2/2 documented): ✔ `savedConstants`, `savedWorlds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedDepthTables()` | — | ✔ |
| `restoreDepthTables()` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `bare()` | — | ✔ |

**class `CurseObjectTimeTest.WithDice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copyOfDice()` | — | ✔ |
| `handedInDiceAreCopied()` | — | ✔ |

**class `CurseObjectTimeTest.WithoutDice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `answersZeroForEveryAspect(DamageAspect)` | — | ✔ |
| `staysZeroAtDepth()` | — | ✔ |


##### `CurseWeightBoundariesTest.java`

**Responsible for:** The boundary values of Curse#modifyWeightForCurse(int) that CurseModifyWeightTest does not reach, each worked out by hand from C's modify_weight_for_curse in obj-curse.c rather than read off the port.

**class `CurseWeightBoundariesTest`** — class Javadoc: ✔ · type stamp: on 261005

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(int, boolean)` | — | ✔ |

**class `CurseWeightBoundariesTest.Multiplicative`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `coercionThreshold()` | — | ✔ |
| `roundingEitherSideOfFifty()` | — | ✔ |
| `zeroFactor()` | — | ✔ |
| `negativeIncomingWeight()` | — | ✔ |
| `saturation()` | — | ✔ |
| `heavyItemUnderFactor()` | — | ✔ |

**class `CurseWeightBoundariesTest.Additive`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ceilingComparison()` | — | ✔ |
| `zeroAddend()` | — | ✔ |
| `exactReduction()` | — | ✔ |
| `negativeIncomingWeight()` | — | ✔ |


##### `EgoHasIgnoreTypeTest.java`

**Responsible for:** Checks EgoItem#egoHasIgnoreType against C's ego_has_ignore_type in obj-ignore.c.

**class `EgoHasIgnoreTypeTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(String, TValue)` | — | ✘ |
| `egoOn(ObjectKind)` | — | ✘ |
| `emptyIdentifierMatchesAnyKindOfTval()` | — | ✘ |
| `unrelatedTypesAreFalse()` | — | ✘ |
| `namedIdentifierMustAppearInKindName()` | — | ✘ |
| `namedIdentifierFoundInsideLongerName()` | — | ✘ |
| `dragonArmourGroups()` | — | ✘ |
| `laterKindIsReached()` | — | ✘ |
| `noPossibleKinds()` | — | ✘ |
| `emptyKindNameDoesNotMatchNamedIdentifier()` | — | ✘ |


##### `EgoItemAccessorsTest.java`

**Responsible for:** Tests the EgoItem accessors the ego-recognition code reads through — isEverSeen, setEverSeen, getFlags, getModifier, getElInfo, getBrands, getSlays and getCurses.

**class `EgoItemAccessorsTest`** — class Javadoc: ✔ · type stamp: on 260816

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ego(Flag<ObjectFlag>, Map<ObjectModifier,Random>, Map<ElementEnum,ElementInfo>, Set<Brand>, Set<Slay>, Map<Curse,CurseData>, boolean)` | — | ✔ |
| `bareEgo()` | — | ✔ |
| `everSeenIsCarried()` | — | ✘ |
| `setEverSeenStoresValue()` | — | ✔ |
| `setEverSeenIsPerEgo()` | — | ✔ |
| `setEverSeenOnCopyIsIndependent()` | — | ✔ |
| `flagsAreShared()` | — | ✔ |
| `grantedModifierIsReturned()` | — | ✘ |
| `untouchedModifierIsNull()` | — | ✔ |
| `elInfoIsShared()` | — | ✘ |
| `collectionsAreShared()` | — | ✔ |
| `emptyCollectionsAreEmptyNotNull()` | — | ✔ |
| `sharingIsLive()` | — | ✔ |

**class `EgoItemAccessorsTest.SuppressionAndIgnoring`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `grantedAndSuppressedAreSeparate()` | — | ✔ |
| `markingRecordsTheCategory()` | — | ✔ |
| `everyCategoryAnswers()` | — | ✔ |
| `markingOneLeavesOthers()` | — | ✔ |


##### `EgoItemClearAndCopyTest.java`

**Responsible for:** Tests EgoItem#clearIgnoreType and EgoItem#copy.

**class `EgoItemClearAndCopyTest`** — class Javadoc: ✔ · type stamp: on 261014

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareEgo()` | — | ✘ |
| `curse()` | — | ✘ |
| `loadedEgo(ObjectKind, Curse)` | — | ✔ |

**class `EgoItemClearAndCopyTest.ClearIgnoreType`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearsMarkedCategory()` | — | ✔ |
| `leavesOtherCategories()` | — | ✔ |
| `clearingUnmarkedIsHarmless()` | — | ✔ |
| `markClearMark()` | — | ✔ |
| `endsOfTheEnumClear()` | — | ✔ |

**class `EgoItemClearAndCopyTest.Copy`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `scalarsCopied()` | — | ✘ |
| `flagSetsIndependent()` | — | ✔ |
| `modifiersCopied()` | — | ✘ |
| `elInfoCopied()` | — | ✘ |
| `curseDataRebuilt()` | — | ✔ |
| `brandAndSlaySetsIndependent()` | — | ✔ |
| `possibleItemsShareKinds()` | — | ✔ |
| `absentOptionalsDoNotStopTheCopy()` | — | ✔ |
| `ignoreMarksCarriedThenIndependent()` | — | ✔ |
| `copyOfCopy()` | — | ✘ |


##### `ElementInfoTest.java`

**Responsible for:** Tests ElementInfo, the port of C's struct element_info (src/object.h).

**class `ElementInfoTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startsEmpty()` | — | ✔ |

**class `ElementInfoTest.ResistanceLevel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `roundTripsPositive()` | — | ✘ |
| `keepsCsThreeLevels()` | — | ✔ |
| `doesNotNarrow()` | — | ✔ |
| `keepsNegative()` | — | ✘ |

**class `ElementInfoTest.Flags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onReportsNoveltyOnly()` | — | ✔ |
| `hasSeesWhatOnSet()` | — | ✘ |
| `flagsAreIndependent()` | — | ✔ |
| `enumOrderMatchesCBits()` | — | ✔ |
| `getFlagsIsLive()` | — | ✔ |

**class `ElementInfoTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copiesBothFields()` | — | ✘ |
| `copiesEveryFlag()` | — | ✔ |
| `flagSetIsNotShared()` | — | ✔ |
| `neitherSideLeaks()` | — | ✔ |
| `emptyCopyIsStillDetached()` | — | ✔ |


##### `ElementSetTest.java`

**Responsible for:** Pins the table that ElementSet rows are built into at start-up against C's element_sets[] in obj-power.c.

**class `ElementSetTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✔ |
| `cleanup()` | — | ✔ |
| `assertRow(ElementSet, ResType, int, int, int, int, String)` | — | ✔ |
| `threeRows()` | — | ✔ |
| `immunitiesRow()` | — | ✔ |
| `lowResistsRow()` | — | ✔ |
| `highResistsRow()` | — | ✔ |
| `rowsAreShared()` | — | ✔ |


##### `FlagSetTest.java`

**Responsible for:** FlagSet, the port of C's struct flag_set and its flag_sets[] table in obj-power.c, function flags_power().

**class `FlagSetTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cIncrement(FlagSet)` | — | ✔ |
| `row(String)` | — | ✘ |
| `incrementFollowsC(String, int, int)` | — | ✔ |
| `tableFigures()` | — | ✔ |
| `zeroingDiscardsPreviousObject()` | — | ✔ |


##### `FlavourKindTest.java`

**Responsible for:** Tests FlavourKind against the way C builds its flavour list (init.c, functions parse_flavor_kind and parse_flavor_flavor).

**class `FlavourKindTest`** — class Javadoc: ✔ · type stamp: on 261010

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `random(String, int)` | — | ✘ |
| `fixed(String, int)` | — | ✘ |

**class `FlavourKindTest.TvalAndGlyph`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `areStoredOnceForTheBlock()` | — | ✘ |
| `eachFileGlyphSurvivesUnchanged()` | — | ✘ |

**class `FlavourKindTest.FlavourOrder`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `runsLastFileEntryFirstAsCHeadInsertionLeavesIt()` | — | ✘ |
| `singleFlavourIsItsOwnReverse()` | — | ✘ |
| `emptyBlockHoldsNoFlavours()` | — | ✘ |
| `mixedFixedAndRandomEntriesKeepTheirRelativeReversedOrder()` | — | ✘ |
| `firstDrawOfTheRandomPassLandsOnTheLastFileEntry()` | — | ✘ |

**class `FlavourKindTest.Ownership`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyFlavourIsToldItsOwner()` | — | ✘ |
| `ownerReachesTheSharedGlyph()` | — | ✘ |
| `adoptingAFlavourReplacesItsPreviousOwner()` | — | ✘ |

**class `FlavourKindTest.SharingAndCopying`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `listPassedInCanBeChangedAfterwardsWithoutReachingTheBlock()` | — | ✘ |
| `flavoursAreTheCallersOwnObjectsNotCopies()` | — | ✘ |
| `flavoursStayMutableThroughTheBlock()` | — | ✘ |

**class `FlavourKindTest.FlavoursView`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cannotBeModified()` | — | ✘ |

**class `FlavourKindTest.NullList`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `throwsNullPointerException()` | — | ✘ |


##### `FlavourSetTextTest.java`

**Responsible for:** Tests Flavour#setText, the Java equivalent of the direct C struct-field write f->text = ...

**class `FlavourSetTextTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `overwritesExistingText()` | — | ✘ |
| `acceptsNull()` | — | ✘ |
| `leavesOtherFieldsUntouched()` | — | ✘ |


##### `FlavourTest.java`

**Responsible for:** Tests Flavour against struct flavor (object.h) and the parse_flavor_flavor parser (init.c).

**class `FlavourTest`** — class Javadoc: ✔ · type stamp: —

**class `FlavourTest.FixedConstructor`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesEveryFieldAndLeavesTheSvalUnresolved()` | — | ✘ |
| `nullTextIsKept()` | — | ✘ |

**class `FlavourTest.RandomConstructor`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hasNoSymbolAndAnUnknownSval()` | — | ✘ |
| `aScrollWithNoDescriptionHasNullText()` | — | ✔ |
| `indexZeroIsAcceptedAsAnOrdinaryIndex()` | — | ✘ |

**class `FlavourTest.SVal`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setsValWritesTheValueAndNothingElse()` | — | ✘ |
| `bindingARandomFlavourDoesNotMakeItFixed()` | — | ✔ |
| `resettingAFixedFlavourToZeroKeepsItFlaggedFixed()` | — | ✔ |

**class `FlavourTest.Ownership`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flavourKindConstructorAdoptsEveryFlavour()` | — | ✘ |
| `setFlavourKindReplacesTheOwner()` | — | ✘ |

**class `FlavourTest.Copy`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copiesARandomFlavourFieldForField()` | — | ✘ |
| `copiesAFixedFlavourFieldForField()` | — | ✘ |
| `keepsAResetFixedFlavourFixed()` | — | ✘ |
| `keepsNullText()` | — | ✘ |
| `laterChangesToTheCopyDoNotReachTheOriginal()` | — | ✘ |
| `theCopyIsNotAddedToTheOwnersList()` | — | ✘ |


##### `GrouperTest.java`

**Responsible for:** Tests Grouper, the port of C's struct grouper (z-type.h).

**class `GrouperTest`** — class Javadoc: ✔ · type stamp: on 261014

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ammoAndBows()` | — | ✔ |
| `headingRowKeepsTypeAndName()` | — | ✔ |
| `continuationRowKeepsNullName()` | — | ✔ |
| `emptyNameIsNotNull()` | — | ✔ |
| `sentinelTvalIsZero()` | — | ✔ |
| `storedTypeKeepsItsCValue()` | — | ✔ |
| `continuationRowsJoinTheGroupAbove()` | — | ✔ |
| `artifactArmourRowsHaveTwoHeadings()` | — | ✔ |
| `rowsAreIndependentPairs()` | — | ✔ |


##### `ItemObjectAbsorbPartialTest.java`

**Responsible for:** Tests ItemObject#objectAbsorbPartial, the port of C's object_absorb_partial (obj-pile.c, function object_absorb_partial) — the split that moves as much of one stack onto another as the limits allow, leaving both alive.

**class `ItemObjectAbsorbPartialTest`** — class Javadoc: ✔ · type stamp: on 260824

- Fields (6/10 documented): ✔ `SLOT_SIZE`, `THROWN_MULT`, `MAX_STACK`, `THROWN_LIMIT`, `kind`, `origin` — ✘ `savedPlayer`, `savedConstants`, `savedBodies`, `savedRaces`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✘ |
| `restoreGlobals()` | — | ✘ |
| `arrows(int)` | — | ✔ |
| `thrown(int)` | — | ✔ |
| `item(TValue, int, boolean)` | — | ✔ |
| `pack()` | — | ✔ |
| `quiver()` | — | ✔ |
| `store()` | — | ✔ |
| `mode(ObjectStackEnum)` | — | ✔ |
| `testRace(PlayerBody)` | — | ✔ |
| `registryField(String)` | — | ✔ |

**class `ItemObjectAbsorbPartialTest.PackToPack`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fillsToMaxStack()` | — | ✔ |
| `orderDoesNotMatter()` | — | ✔ |
| `countIsConserved()` | — | ✔ |

**class `ItemObjectAbsorbPartialTest.QuiverToQuiver`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ammoFillsSlot()` | — | ✔ |
| `thrownFillsSlot()` | — | ✔ |

**class `ItemObjectAbsorbPartialTest.MixedModes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `quiverReceiverTakesASlot()` | — | ✔ |
| `quiverDonorKeepsASlot()` | — | ✔ |
| `limitFollowsTheQuiverStack()` | — | ✔ |
| `packOverflowThrows()` | — | ✔ |
| `packOverflowThrowsEitherOrder()` | — | ✘ |

**class `ItemObjectAbsorbPartialTest.StoreMode`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storeFirstThrows()` | — | ✔ |
| `storeSecondThrows()` | — | ✘ |
| `refusedSplitChangesNothing()` | — | ✔ |


##### `ItemObjectAbsorbTest.java`

**Responsible for:** Tests ItemObject#objectAbsorb and ItemObject#objectSplit — the two halves of moving items between stacks, and the port of C's object_absorb, object_absorb_merge and object_split (obj-pile.c).

**class `ItemObjectAbsorbTest`** — class Javadoc: ✔ · type stamp: —

- Fields (7/7 documented): ✔ `MAX_STACK`, `level`, `known`, `player`, `savedCave`, `savedPlayer`, `kind`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wireCaves()` | — | ✔ |
| `restoreGame()` | — | ✔ |
| `stack(int)` | — | ✔ |
| `bareStack(int)` | — | ✔ |
| `bareLoaded(TValue, int)` | — | ✔ |
| `seedWorldDepth()` | — | ✔ |
| `restoreConstants(Object)` | — | ✔ |
| `listInLevel(ItemObject)` | — | ✔ |
| `listInKnown(ItemObject)` | — | ✔ |

**class `ItemObjectAbsorbTest.Absorb`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `countsAreAdded()` | — | ✔ |
| `totalIsCapped()` | — | ✔ |
| `absorbedLeavesTheLevel()` | — | ✔ |
| `knowledgeLeavesTheRememberedLevel()` | — | ✔ |
| `survivorKeepsItsKnowledge()` | — | ✔ |
| `unknownStackIsAbsorbed()` | — | ✔ |
| `stalePlayerIsRefreshed()` | — | ✔ |
| `absorbedInscriptionIsTaken()` | — | ✔ |
| `survivorInscriptionStays()` | — | ✔ |
| `wandChargesAreAdded()` | — | ✔ |
| `chargesAreCapped()` | — | ✔ |
| `goldIsAdded()` | — | ✔ |
| `rodTimeoutsAreAdded()` | — | ✔ |
| `ordinaryPvalIsNotPooled()` | — | ✔ |

**class `ItemObjectAbsorbTest.Split`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `countsMove()` | — | ✔ |
| `knowledgeFollowsTheCounts()` | — | ✔ |
| `newStackIsSeparate()` | — | ✔ |
| `wholeStackIsRefused()` | — | ✔ |
| `inscriptionIsCarried()` | — | ✔ |
| `unknownStackSplits()` | — | ✔ |
| `chargesAreShared()` | — | ✔ |
| `mostChargesMove()` | — | ✔ |
| `staleKnownCountIsAligned()` | — | ✔ |
| `rodSplitsThroughBareKnownHalf()` | — | ✔ |
| `knownInscriptionIsCarried()` | — | ✔ |


##### `ItemObjectAccessorsTest.java`

**Responsible for:** Tests ItemObject's construction and its plain accessors — the port of C's struct object (src/object.h) at rest, as opposed to the recharge arithmetic covered by ItemObjectRechargeTest and the stacking rules covered by ItemObjectSimilarTest.

**class `ItemObjectAccessorsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `set(ItemObject, String, Object)` | — | ✔ |
| `item(TValue, String, String)` | — | ✔ |
| `read(ItemObject, String)` | — | ✔ |
| `curse(String)` | — | ✔ |
| `artifact()` | — | ✔ |
| `elementMapIsKept()` | — | ✔ |
| `kindIsKept()` | — | ✘ |
| `numberIsKept()` | — | ✔ |

**class `ItemObjectAccessorsTest.Blank`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `isEmpty()` | — | ✘ |
| `isNotAnArtifact()` | — | ✔ |
| `hasNoCharge()` | — | ✔ |
| `originDefaultsToNone()` | — | ✔ |
| `bothConstructorsAgreeOnNoOrigin()` | — | ✔ |

**class `ItemObjectAccessorsTest.Parsing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyPValue()` | — | ✔ |
| `numericPValue()` | — | ✘ |
| `recheargeStringBecomesDice()` | — | ✔ |
| `keepsTheNote()` | — | ✘ |
| `uninscribedIsNull()` | — | ✔ |

**class `ItemObjectAccessorsTest.Accessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `gridRoundTrips()` | — | ✘ |
| `artifactIsDetected()` | — | ✘ |
| `noticeAccumulates()` | — | ✔ |
| `brandsAreLive()` | — | ✔ |
| `cursesAreAnUnmodifiableView()` | — | ✔ |
| `cursesAreEmptyWhenUnset()` | — | ✔ |
| `monsterIndicesRoundTrip()` | — | ✔ |
| `noticeOf(ItemObject)` | — | ✘ |


##### `ItemObjectClassificationTest.java`

**Responsible for:** Tests the ItemObject accessors that classify an item rather than store something about it — the ignore category it falls into, whether the player is aware of its flavour, whether it gives everything up at a glance, and whether it is fully known.

**class `ItemObjectClassificationTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(TValue, ObjectKind)` | — | ✔ |
| `kind(TValue, String)` | — | ✔ |
| `setKindFlag(ObjectKind, ObjectKindFlag)` | — | ✔ |
| `setAware(ObjectKind)` | — | ✔ |
| `removingSlayFromNoneIsHarmless()` | — | ✔ |

**class `ItemObjectClassificationTest.IgnoreCategory`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unqualifiedRowMatches()` | — | ✔ |
| `namedKindTakesTheQualifiedRow()` | — | ✔ |
| `launchersDifferByNameOnly()` | — | ✔ |
| `uncoveredTypeAnswersSentinel()` | — | ✔ |
| `unmatchedNameAnswersSentinel()` | — | ✔ |

**class `ItemObjectClassificationTest.Awareness`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `awarenessIsOnTheKind()` | — | ✔ |
| `noKindAnswersFalse()` | — | ✔ |
| `easyKnowNeedsBoth()` | — | ✔ |
| `noKnowledgeIsNotFullyKnown()` | — | ✔ |

**class `ItemObjectClassificationTest.Ego`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `egoRoundTrips()` | — | ✔ |
| `noEgoIsNotIgnoredByEgo()` | — | ✔ |


##### `ItemObjectConstructionTest.java`

**Responsible for:** Tests the construction, copy, wipe and collection-editing surface of ItemObject that round two of the ItemObject pass calls Part A: object_new, object_copy and object_wipe (obj-pile.c), the loop in copy_curses and the array edits of obj-curse.c that the curse methods stand for, and the bool array...

**class `ItemObjectConstructionTest`** — class Javadoc: ✔ · type stamp: on 261007

- Fields (0/3 documented): ✘ `item`, `low`, `high`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(String, int)` | — | ✔ |
| `brand(String)` | — | ✔ |
| `slay(String)` | — | ✔ |
| `nullField(ItemObject, String)` | — | ✘ |
| `setUp()` | — | ✘ |

**class `ItemObjectConstructionTest.Blank`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `numbersAreZero()` | — | ✘ |
| `pointersAreNull()` | — | ✘ |
| `gridIsLocZero()` | — | ✔ |
| `timeIsZeroRandom()` | — | ✘ |
| `collectionsAreEmpty()` | — | ✘ |
| `curseMapIsOrdered()` | — | ✘ |
| `blanksShareNothing()` | — | ✘ |

**class `ItemObjectConstructionTest.FullConstructor`** — class Javadoc: ✔ · type stamp: —

- Fields (0/7 documented): ✘ `callerCurses`, `callerFlags`, `callerModifiers`, `callerElInfo`, `callerBrands`, `callerSlays`, `callerEffect`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `callerCollections()` | — | ✘ |
| `build(String, String, Map<Curse,CurseData>, Flag<ObjectFlag>, Flag<ObjectNotice>)` | — | ✘ |
| `nullLocationBecomesOrigin()` | — | ✔ |
| `scalarsLand()` | — | ✘ |
| `emptyPValueIsZero()` | — | ✘ |
| `emptyTimeIsZeroRandom()` | — | ✘ |
| `curseMapIsCopied()` | — | ✘ |
| `curseMapIsOrdered()` | — | ✘ |
| `curseDataIsShared()` | — | ✘ |
| `nullCurseMapIsEmpty()` | — | ✘ |
| `nullFlagSetsBecomeEmpty()` | — | ✘ |
| `otherCollectionsAreShared()` | — | ✘ |
| `belongsToNoPile()` | — | ✘ |

**class `ItemObjectConstructionTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `modifierOrderIsKept()` | — | ✘ |
| `elementOrderIsKept()` | — | ✘ |
| `curseOrderIsKept()` | — | ✘ |
| `effectIsShared()` | — | ✘ |
| `activationIsShared()` | — | ✘ |
| `copyIsDetached()` | — | ✘ |
| `nullTimeCopiesToZero()` | — | ✘ |

**class `ItemObjectConstructionTest.Wipe`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wipedCurseMapIsOrdered()` | — | ✘ |
| `playerIsKept()` | — | ✘ |
| `initCursesDiscards()` | — | ✘ |

**class `ItemObjectConstructionTest.NullArguments`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `carryACurse()` | — | ✘ |
| `setCursesNullKeepsCurses()` | — | ✘ |
| `clearAndPutNullKeepsCurses()` | — | ✘ |
| `setCursesEmptyClears()` | — | ✘ |
| `setCursesOrders()` | — | ✘ |
| `addCursesNullThrows()` | — | ✘ |
| `nullCurseIsIgnored()` | — | ✘ |

**class `ItemObjectConstructionTest.Simplifications`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addCurseReplacesStrongerCurse()` | — | ✘ |
| `addCurseKeepsGivenTimeout()` | — | ✘ |
| `addCurseStoresPowerZero()` | — | ✘ |
| `removeCurseRemovesPowerZero()` | — | ✘ |
| `removingLastCurseEmptiesMap()` | — | ✘ |
| `setCursePowerAddsWithZeroTimeout()` | — | ✘ |

**class `ItemObjectConstructionTest.BrandsAndSlays`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addCreatesTheSet()` | — | ✘ |
| `addingTwiceIsOnce()` | — | ✘ |
| `removingAbsentIsQuiet()` | — | ✘ |
| `removeOnNullFieldMakesEmptySet()` | — | ✘ |
| `removeLeavesOthers()` | — | ✘ |
| `clearEmptiesTheSets()` | — | ✘ |
| `gettersAnswerImmutableEmpty()` | — | ✘ |
| `gettersAreLive()` | — | ✘ |
| `setSlaysStoresTheSet()` | — | ✘ |

**class `ItemObjectConstructionTest.FreeHelpers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `call(ItemObject, String)` | — | ✘ |
| `helpersEmptyTheCopyOnly()` | — | ✘ |
| `emptiedCopyTakesWrites()` | — | ✘ |


##### `ItemObjectCopyTest.java`

**Responsible for:** Tests ItemObject#copy(boolean), the port of C's object_copy (obj-pile.c).

**class `ItemObjectCopyTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `race`, `kind`, `curse`, `original`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse()` | — | ✔ |
| `buildOriginal()` | — | ✔ |

**class `ItemObjectCopyTest.Carried`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `scalarsAreCopied()` | — | ✔ |
| `templatesAreShared()` | — | ✔ |
| `copyIsSeparate()` | — | ✔ |

**class `ItemObjectCopyTest.Independence`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsAreIndependent()` | — | ✔ |
| `noticeIsIndependent()` | — | ✔ |
| `modifiersAreIndependent()` | — | ✔ |
| `elementInfoIsIndependent()` | — | ✔ |
| `curseDataIsIndependent()` | — | ✔ |
| `timeIsIndependent()` | — | ✔ |
| `locationIsIndependent()` | — | ✔ |
| `brandAndSlaySetsAreIndependent()` | — | ✔ |

**class `ItemObjectCopyTest.Absent`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `collectionsStayNull()` | — | ✔ |
| `diceStayNull()` | — | ✔ |
| `gridCopiesAsOrigin()` | — | ✔ |

**class `ItemObjectCopyTest.KnownHalf`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `counterpart`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `attachKnown()` | — | ✔ |
| `includingKnownCopiesIt()` | — | ✔ |
| `knownCopyIsOneLevel()` | — | ✔ |
| `excludingKnownLeavesItNull()` | — | ✔ |
| `unknownOriginalStaysUnknown()` | — | ✔ |


##### `ItemObjectCursesTest.java`

**Responsible for:** Tests the seven methods that change an ItemObject's curses — the write side of what C reaches by assigning into obj->curses[i].

**class `ItemObjectCursesTest`** — class Javadoc: ✔ · type stamp: on 260817

- Fields (0/3 documented): ✘ `item`, `siren`, `teleport`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(String)` | — | ✔ |
| `curse(String, int)` | on 261008 | ✔ |
| `setUp()` | on 260817 | ✔ |

**class `ItemObjectCursesTest.Adding`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `numericFormBuildsData()` | — | ✔ |
| `dataFormSharesInstance()` | — | ✔ |
| `addingTwiceReplaces()` | — | ✔ |
| `addCursesAccumulates()` | — | ✔ |
| `clearAndPutReplaces()` | — | ✔ |
| `writersCreateTheMapOnDemand()` | on 260817 | ✔ |

**class `ItemObjectCursesTest.Changing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setPowerLeavesTimeout()` | — | ✔ |
| `setPowerAddsAbsentCurse()` | on 260817 | ✔ |
| `setPowerZeroDoesNotAddAbsentCurse()` | — | ✔ |
| `setPowerNegativeRemoves()` | — | ✔ |
| `setPowerIgnoresNull()` | — | ✔ |
| `removeTakesTheEntryOut()` | — | ✔ |
| `removingAbsentCurseIsQuiet()` | — | ✔ |
| `clearEmpties()` | on 260817 | ✔ |

**class `ItemObjectCursesTest.Initializing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshObjectGetsEmptyMap()` | on 260817 | ✔ |
| `discardsExistingCurses()` | — | ✔ |

**class `ItemObjectCursesTest.ViewAndWrites`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `viewSeesLaterWrites()` | — | ✔ |
| `curseMapWalksInIndexOrder()` | on 260817 | ✔ |


##### `ItemObjectCustomMessageTest.java`

**Responsible for:** Tests ItemObject#printCustomMessage - the port of C's print_custom_message (src/obj-util.c:1116).

**class `ItemObjectCustomMessageTest`** — class Javadoc: ✔ · type stamp: on 260829

- Fields (2/2 documented): ✔ `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(int)` | — | ✔ |
| `itemOfKind(int, String)` | — | ✔ |
| `descriptionOf(ItemObject)` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `print(ItemObject, String, boolean)` | — | ✔ |

**class `ItemObjectCustomMessageTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `messages`, `types`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `ItemObjectCustomMessageTest.Tags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nameBecomesDescription()` | — | ✔ |
| `nameWithoutObjectBecomesHands()` | — | ✔ |
| `kindBecomesKindName()` | — | ✔ |
| `kindWithoutObjectBecomesHands()` | — | ✔ |
| `verbEndingForOne()` | — | ✔ |
| `verbEndingForPile()` | — | ✔ |
| `verbEndingWithoutObject()` | — | ✔ |
| `isForOne()` | — | ✔ |
| `isForPile()` | — | ✔ |
| `isWithoutObject()` | — | ✔ |
| `verbEndingForStackOfZero()` | — | ✔ |
| `isForStackOfZero()` | — | ✔ |
| `twoTags()` | — | ✔ |
| `repeatedTag()` | — | ✔ |
| `noTags()` | — | ✔ |
| `typeIsCarried()` | — | ✔ |

**class `ItemObjectCustomMessageTest.Malformed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullTemplatePrintsNothing()` | — | ✔ |
| `unknownTagIsDropped()` | — | ✔ |
| `unknownTagAtEndIsDropped()` | — | ✔ |
| `emptyBracesAreDropped()` | — | ✔ |
| `unclosedBraceToEndOfString()` | — | ✔ |
| `unclosedBraceStoppedByNonLetter()` | — | ✔ |
| `tagAfterUnclosedBrace()` | — | ✔ |
| `loneClosingBrace()` | — | ✔ |

**class `ItemObjectCustomMessageTest.Prefixes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `prefixIsAMatch()` | — | ✔ |
| `verbPrefixIsAMatch()` | — | ✔ |
| `longVerbPrefixLeavesNothing()` | — | ✔ |
| `isPrefixIsAMatch()` | — | ✔ |
| `isPrefixIsAMatchForPile()` | — | ✔ |
| `kindPrefixIsAMatch()` | — | ✔ |
| `shortPrefixIsNotAMatch()` | — | ✔ |

**class `ItemObjectCustomMessageTest.Divergences`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `truncatedTo1023()` | — | ✔ |


##### `ItemObjectDamagePowerTest.java`

**Responsible for:** Tests the damage components of the power calculation in ItemObject: slayPower, extraMightPower, extraShotsPower, extraBlowsPower, launcherAmmoDamagePower, bowMultiplier, ammoDamagePower, damageDicePower and toDamagePower, each with its plain and Curse overload where it has one.

**class `ItemObjectDamagePowerTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (1/2 documented): ✔ `savedPlayer` — ✘ `savedArchery`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedArchery()` | — | ✔ |
| `restoreArchery()` | — | ✔ |
| `invoke(Object, String, Class<?>[], Object)` | — | ✔ |
| `price(ItemObject, String, int)` | — | ✔ |
| `price(ItemObject, String, Curse, int)` | — | ✔ |
| `priceAlone(ItemObject, String)` | — | ✔ |
| `priceAlone(ItemObject, String, Curse)` | — | ✔ |
| `item(TValue)` | — | ✔ |
| `item(TValue, int, int, int)` | — | ✔ |
| `launcher(ObjectKindFlag)` | — | ✔ |
| `kindFiring(ObjectKindFlag)` | — | ✔ |
| `ego()` | — | ✔ |
| `with(ItemObject, ObjectModifier, int)` | — | ✔ |
| `curse(ObjectModifier, int, int)` | — | ✔ |
| `brand(String, int)` | — | ✔ |
| `slay(String, int, int)` | — | ✔ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |

**class `ItemObjectDamagePowerTest.ToDamage`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstLotOnly(TValue, int, int)` | — | ✔ |
| `secondLot(TValue, int, int)` | — | ✔ |
| `curseOverload(int, int)` | — | ✔ |
| `livePlayer()` | — | ✔ |
| `noPlayer()` | — | ✔ |

**class `ItemObjectDamagePowerTest.DamageDice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `diceFormula(TValue, int, int, int)` | — | ✔ |
| `weaponWithBrand()` | — | ✔ |
| `plainRing()` | — | ✔ |
| `ringWithBrandOrSlay()` | — | ✔ |
| `ringWithModifier(ObjectModifier, int, int)` | — | ✔ |
| `launcherIsZero()` | — | ✔ |
| `curseOverload(ObjectModifier, int, int)` | — | ✔ |
| `curseWithNothing()` | — | ✔ |
| `livePlayer()` | — | ✔ |

**class `ItemObjectDamagePowerTest.AmmoDamage`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `perAmmunition(ObjectKindFlag, int)` | — | ✔ |
| `order()` | — | ✔ |
| `firesNothing()` | — | ✔ |
| `notALauncher(TValue)` | — | ✔ |
| `noKind()` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectDamagePowerTest.LauncherAmmo`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `multiplied(TValue, int, int)` | — | ✔ |
| `egoAmmunition(TValue, int)` | — | ✔ |
| `notAmmunition(TValue)` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectDamagePowerTest.BowMultiplier`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bow(int)` | — | ✔ |
| `notABow(TValue)` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectDamagePowerTest.ExtraBlows`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `none()` | — | ✔ |
| `scaled(int, int, int)` | — | ✔ |
| `inhibit(int, int)` | — | ✔ |
| `curseOverload(int, int)` | — | ✔ |
| `curseWithNothing()` | — | ✔ |

**class `ItemObjectDamagePowerTest.ExtraShots`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `scaled(int, int, int)` | — | ✔ |
| `noneOrNegative(int)` | — | ✔ |
| `inhibit(int, int)` | — | ✔ |
| `curseOverload(int, int)` | — | ✔ |
| `curseWithNothing()` | — | ✔ |

**class `ItemObjectDamagePowerTest.ExtraMight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pair(int, int)` | — | ✘ |
| `read(Object, String)` | — | ✘ |
| `price(ItemObject, int, int, int)` | — | ✘ |
| `priceCurse(int, int, int)` | — | ✘ |
| `multiplies(int, int, int, int)` | — | ✔ |
| `inhibit(int, int)` | — | ✔ |
| `curseOverload(int, int, int, int)` | — | ✔ |

**class `ItemObjectDamagePowerTest.SlayPower`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `price(ItemObject, int, boolean, int)` | — | ✘ |
| `nothing()` | — | ✔ |
| `oneBrand()` | — | ✔ |
| `largerDice()` | — | ✔ |
| `bestOfSeveral()` | — | ✔ |
| `weakBrand()` | — | ✔ |
| `floorOfOne()` | — | ✔ |
| `multipleSlays()` | — | ✔ |
| `multipleBrands()` | — | ✔ |
| `slayAndBrand()` | — | ✔ |
| `multipleKills()` | — | ✔ |
| `killAndSlay()` | — | ✔ |
| `killBoundary(int, int)` | — | ✔ |
| `fullSlays(int, int)` | — | ✔ |
| `fullBrands(int, int)` | — | ✔ |
| `fullKills(int, int)` | — | ✔ |
| `zeroDice()` | — | ✔ |
| `curseOverload()` | — | ✔ |


##### `ItemObjectDescNameFormatTest.java`

**Responsible for:** Tests ItemObject.objDescNameFormat(String, String, boolean) — the port of C's obj_desc_name_format (obj-desc.c).

**class `ItemObjectDescNameFormatTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `item`, `objDescNameFormat`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newItem()` | — | ✔ |
| `format(String, String, boolean)` | — | ✔ |

**class `ItemObjectDescNameFormatTest.Ampersand`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leadingAmpersandGoes()` | — | ✘ |
| `midTemplateAmpersand()` | — | ✘ |
| `runOfAmpersands()` | — | ✘ |
| `noAmpersand()` | — | ✘ |

**class `ItemObjectDescNameFormatTest.Tilde`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singularDropsTilde()` | — | ✘ |
| `pluralAddsS()` | — | ✘ |
| `pluralAddsEsAfterS()` | — | ✘ |
| `pluralAddsEsAfterH()` | — | ✘ |
| `pluralAddsEsAfterX()` | — | ✘ |
| `tildeMidWord()` | — | ✘ |
| `foodHasNoPlural()` | — | ✘ |

**class `ItemObjectDescNameFormatTest.Bars`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `staffAndStaves()` | — | ✘ |
| `tailAfterClosingBarKept()` | — | ✘ |
| `twoTriples()` | — | ✘ |
| `tildeAfterBars()` | — | ✘ |

**class `ItemObjectDescNameFormatTest.Modifier`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flavourSubstituted()` | — | ✘ |
| `modifierAndTemplateBothFormat()` | — | ✘ |
| `trailingModifier()` | — | ✘ |
| `modifierWithOwnBars()` | — | ✘ |
| `nullModifierLeavesHash()` | — | ✘ |

**class `ItemObjectDescNameFormatTest.Divergences`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tildeAfterModifier()` | — | ✘ |
| `badBarCountRejected()` | — | ✘ |
| `leadingTildeReported()` | — | ✘ |
| `doubledTildeKeepsProgress()` | — | ✘ |
| `errorExitKeepsMarkers()` | — | ✘ |
| `tildeInKeptSingularAlternative()` | — | ✘ |
| `tildeInKeptPluralAlternative()` | — | ✘ |
| `tildeInDroppedAlternative()` | — | ✘ |


##### `ItemObjectEarlierObjectRulesTest.java`

**Responsible for:** Tests the pack-order rules of ItemObject#earlierObject that ItemObjectOrderingTest does not reach - usable ammunition, flavour awareness, light fuel and value - together with ItemObject#objectIsInQuiver and ItemObject#nullKnown.

**class `ItemObjectEarlierObjectRulesTest`** — class Javadoc: ✔ · type stamp: on 261007

- Fields (2/2 documented): ✔ `savedPlayer`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bookless()` | — | ✔ |
| `earlier(ItemObject, ItemObject, boolean)` | — | ✔ |
| `kind(TValue, int, boolean)` | — | ✔ |
| `item(TValue, ObjectKind, int, int)` | — | ✔ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |

**class `ItemObjectEarlierObjectRulesTest.UsableAmmunition`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `usableAmmoFirst()` | — | ✔ |
| `preferenceFollowsTheLauncher()` | — | ✔ |
| `storeKeepsTheAmmoRule()` | — | ✔ |
| `bothMustBeAmmo()` | — | ✔ |
| `noLauncher()` | — | ✔ |

**class `ItemObjectEarlierObjectRulesTest.FlavourAwareness`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unawareComesLast()` | — | ✔ |
| `storeIgnoresAwareness()` | — | ✔ |
| `awarenessBeforeSubType()` | — | ✔ |

**class `ItemObjectEarlierObjectRulesTest.LightFuel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `moreFuelFirst()` | — | ✔ |
| `equalFuel()` | — | ✔ |
| `storeIgnoresFuel()` | — | ✔ |
| `onlyLights()` | — | ✔ |

**class `ItemObjectEarlierObjectRulesTest.Value`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dearerFirst()` | — | ✔ |
| `cheaperAmmoFirst()` | — | ✔ |
| `equalValues()` | — | ✔ |
| `storeUsesValue()` | — | ✔ |

**class `ItemObjectEarlierObjectRulesTest.InQuiver`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `quiver()` | — | ✔ |
| `inSlot()` | — | ✘ |
| `lookalikeIsNot()` | — | ✔ |
| `emptyQuiver()` | — | ✔ |

**class `ItemObjectEarlierObjectRulesTest.NullKnown`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearsTheLink()` | — | ✔ |
| `harmlessWithoutKnown()` | — | ✔ |


##### `ItemObjectEquippyGlyphTest.java`

**Responsible for:** Tests ItemObject#getItemObjectADC() and the private useFlavourGlyph decision behind it, the port of C's object_char/object_attr calling use_flavor_glyph ([C] ui-object.c).

**class `ItemObjectEquippyGlyphTest`** — class Javadoc: ✔ · type stamp: on 260928

- Fields (0/3 documented): ✘ `KIND_GLYPH`, `FLAVOUR_GLYPH`, `FLAVOUR_COLOUR`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `poke(Object, String, Object)` | — | ✔ |
| `flavouredKind(TValue, boolean)` | — | ✔ |
| `item(ObjectKind)` | — | ✔ |
| `nonScrollUnawareUsesFlavourGlyph()` | — | ✘ |
| `nonScrollAwareUsesFlavourGlyph()` | — | ✘ |
| `scrollUnawareUsesFlavourGlyph()` | — | ✘ |
| `scrollAwareUsesKindsOwnGlyph()` | — | ✔ |
| `kindWithNoFlavourAlwaysUsesItsOwnGlyph()` | — | ✘ |


##### `ItemObjectFieldSurfaceTest.java`

**Responsible for:** Tests the parts of ItemObject's field surface that the other accessor tests leave open: what the two constructors leave in each field, what the whole-collection setters do with the collection they are handed and the one they replace, and the stat overload of getModifierValue.

**class `ItemObjectFieldSurfaceTest`** — class Javadoc: ✔ · type stamp: on 261002

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(String)` | — | ✔ |
| `curse(String, int)` | — | ✔ |
| `full(Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, Set<Brand>, Set<Slay>, LinkedHashMap<Curse,CurseData>, List<Effect>, String, String)` | — | ✔ |
| `loaded()` | — | ✔ |
| `brand(String)` | — | ✔ |
| `slay(String)` | — | ✔ |

**class `ItemObjectFieldSurfaceTest.Blank`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `typeIsNone()` | — | ✔ |
| `typeMatchesWipe()` | — | ✔ |
| `numbersAreZero()` | — | ✔ |
| `collectionsAreEmpty()` | — | ✔ |
| `modifiersReadZero()` | — | ✔ |

**class `ItemObjectFieldSurfaceTest.FullConstructor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesByReference()` | — | ✔ |
| `copiesTheCurseMap()` | — | ✔ |
| `keepsTheGrid()` | — | ✔ |
| `nullCollectionsAreAbsorbed()` | — | ✔ |
| `modifierOnNullMapIsZero()` | — | ✔ |
| `emptyTimeIsZero()` | — | ✔ |
| `flatTimeParses()` | — | ✘ |
| `badPValueThrows()` | — | ✔ |

**class `ItemObjectFieldSurfaceTest.WholeCollections`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `item`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `setModifiersSelfAssignment()` | — | ✔ |
| `setModifiersLeavesOldMap()` | — | ✔ |
| `setModifiersStoresAsGiven()` | — | ✘ |
| `setModifiersNull()` | — | ✘ |
| `setElInfoSelfAssignment()` | — | ✘ |
| `setElInfoLeavesOldMap()` | — | ✘ |
| `setElInfoNull()` | — | ✘ |
| `setEffectNullAndList()` | — | ✘ |
| `setSlaysStoresAndNull()` | — | ✘ |
| `slaysReplacedNotCleared()` | — | ✔ |
| `brandEditors()` | — | ✘ |

**class `ItemObjectFieldSurfaceTest.ReplacingCurses`** — class Javadoc: ✔ · type stamp: —

- Fields (0/3 documented): ✘ `item`, `siren`, `teleport`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `replaceWith(boolean, Map<Curse,CurseData>)` | — | ✔ |
| `disjointSetReplaces()` | — | ✘ |
| `samePowerNewTimeout()` | — | ✔ |
| `instanceIsShared()` | — | ✘ |
| `argumentIsCopied()` | — | ✔ |
| `ownViewIsSafe()` | — | ✔ |
| `emptySetClears()` | — | ✘ |
| `orderIsByIndex()` | — | ✔ |
| `nullMapIsReplaced()` | — | ✔ |
| `zeroPowerRemovesEntry()` | — | ✔ |

**class `ItemObjectFieldSurfaceTest.ByStat`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eachStatReadsItsOwn()` | — | ✔ |
| `sentinelsAnswerZero()` | — | ✔ |


##### `ItemObjectGetCursesTest.java`

**Responsible for:** Tests ItemObject#getCurses(), the port of reading C's obj->curses.

**class `ItemObjectGetCursesTest`** — class Javadoc: ✔ · type stamp: on 261003

- Fields (0/4 documented): ✘ `item`, `first`, `second`, `third`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(String, int)` | — | ✔ |
| `nullBackingMap(ItemObject)` | — | ✔ |
| `setUp()` | — | ✘ |
| `freshItemIsEmpty()` | — | ✘ |
| `wipedItemIsEmpty()` | — | ✘ |
| `nullBackingFieldIsAbsorbed()` | — | ✘ |
| `nullBackingFieldIsRebuilt()` | — | ✘ |
| `walksInIndexOrder()` | — | ✘ |
| `indexOrderSurvivesReplace()` | — | ✘ |
| `indexOrderSurvivesCopy()` | — | ✘ |
| `viewIsUnmodifiable()` | — | ✘ |
| `viewTracksLaterAdds()` | — | ✘ |
| `valuesAreLive()` | — | ✘ |
| `absentCurseIsAbsent()` | — | ✘ |
| `zeroPowerEntryIsReported()` | — | ✘ |
| `nullKeyLookup()` | — | ✘ |
| `notSharedBetweenItems()` | — | ✘ |


##### `ItemObjectKindNameTest.java`

**Responsible for:** Tests ItemObject.objectKindName(ObjectKind, boolean) — the port of C's object_kind_name (src/obj-desc.c:48).

**class `ItemObjectKindNameTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `item`, `objectKindName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newItem()` | — | ✔ |
| `kind(String, String, boolean)` | — | ✔ |
| `name(ObjectKind, boolean)` | — | ✔ |

**class `ItemObjectKindNameTest.Branch`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unawareFlavoured()` | — | ✔ |
| `unawareFlavouredEasyKnow()` | — | ✔ |
| `awareFlavoured()` | — | ✔ |
| `unawareUnflavoured()` | — | ✔ |
| `awareUnflavoured()` | — | ✔ |

**class `ItemObjectKindNameTest.Formatting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `articleStripped()` | — | ✔ |
| `tildeDropped()` | — | ✔ |
| `barsSingular()` | — | ✔ |
| `hashLeftInPlace()` | — | ✔ |

**class `ItemObjectKindNameTest.FlavourIsNotFormatted`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flavourVerbatim()` | — | ✔ |
| `nameFormatted()` | — | ✔ |


##### `ItemObjectKnowledgeIgnoreTest.java`

**Responsible for:** Tests the knowledge and ignoring queries of ItemObject that the older classification, quality and recharge tests leave open: ItemObject#flagsKnown(), the weapon branch of ItemObject#ignoreLevelOf() with its two private helpers, the ego and artifact overrides of that grading, ItemObject#egoIsIgnor...

**class `ItemObjectKnowledgeIgnoreTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `floor(int, int)` | — | ✔ |
| `swordKind(Random, Random, Random)` | — | ✔ |
| `plainSwordKind()` | — | ✔ |
| `sword(ObjectKind, int, int, int)` | — | ✔ |
| `withBonuses(ItemObject, int, int, int)` | — | ✔ |
| `ego(Flag<ObjectFlag>, Flag<ObjectFlag>)` | — | ✔ |
| `plainEgo()` | — | ✔ |
| `artifact()` | — | ✔ |
| `flagSet(ObjectFlag)` | — | ✔ |
| `members(FlagView<ObjectFlag>)` | — | ✔ |
| `makeAware(ObjectKind)` | — | ✔ |
| `makeEasyKnow(ObjectKind)` | — | ✔ |

**class `ItemObjectKnowledgeIgnoreTest.WeaponGrading`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `weightedSum(int, int, int, QualityValueEnum)` | — | ✔ |
| `kindFloor(int, int, int, QualityValueEnum)` | — | ✔ |
| `eachBonusHasItsOwnFloor(String)` | — | ✔ |
| `egoOverridesBad()` | — | ✔ |
| `artifactOverridesBad()` | — | ✔ |
| `egoBeatsArtifact()` | — | ✔ |
| `assessedArtifactIsMax()` | — | ✔ |
| `partlyKnownEgoIsMax()` | — | ✔ |

**class `ItemObjectKnowledgeIgnoreTest.JewelleryGrading`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ring()` | — | ✔ |
| `positiveFigure(String)` | — | ✔ |
| `negativeFigure(String)` | — | ✔ |
| `positiveBeatsNegativeAcrossFigures()` | — | ✔ |
| `negativeModifierAlone()` | — | ✔ |
| `knownHalfOnly()` | — | ✔ |

**class `ItemObjectKnowledgeIgnoreTest.FlagsKnown`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(ObjectKind, EgoItem, Flag<ObjectFlag>, Flag<ObjectFlag>)` | — | ✔ |
| `kindWith(Flag<ObjectFlag>)` | — | ✔ |
| `noKnownHalf()` | — | ✔ |
| `intersection()` | — | ✔ |
| `noKind()` | — | ✔ |
| `awareKindAddsItsFlags()` | — | ✔ |
| `awareIsNotEnoughForAnEgo()` | — | ✔ |
| `easyKnowFlagAloneIsNotEnough()` | — | ✔ |
| `easyKnownEgo()` | — | ✔ |
| `freshSet()` | — | ✔ |

**class `ItemObjectKnowledgeIgnoreTest.EgoIgnored`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `markedCategoryOnly()` | — | ✔ |
| `sharedAcrossItems()` | — | ✔ |

**class `ItemObjectKnowledgeIgnoreTest.Flavour`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `follows()` | — | ✔ |
| `throwsWithoutKind()` | — | ✔ |
| `flagWithoutAwareness()` | — | ✔ |

**class `ItemObjectKnowledgeIgnoreTest.QualityTable`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tableRows(TValue, String, IgnoreType)` | — | ✔ |


##### `ItemObjectMergeHelpersTest.java`

**Responsible for:** Tests the helpers a stack merge leans on: ItemObject's originCombine, distributeCharges, effectIsKnown and isFullyKnown, the ports of C's object_origin_combine, distribute_charges, object_effect_is_known and object_fully_known.

**class `ItemObjectMergeHelpersTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedWorldMaxDepth()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `call(ItemObject, String, Class<?>[], Object)` | — | ✘ |
| `originCombine(ItemObject, ItemObject)` | — | ✘ |
| `distributeCharges(ItemObject, ItemObject, int, boolean)` | — | ✘ |
| `effectIsKnown(ItemObject)` | — | ✘ |
| `item(TValue)` | — | ✘ |
| `race(boolean)` | — | ✘ |
| `withOrigin(ObjectOriginEnum, int, MonsterRace)` | — | ✘ |
| `effect()` | — | ✘ |

**class `ItemObjectMergeHelpersTest.OriginCombine`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sameOriginIsUnchanged()` | — | ✘ |
| `differentOriginIsMixed()` | — | ✘ |
| `differentDepthIsMixed()` | — | ✘ |
| `ownUniqueIsKept()` | — | ✘ |
| `otherUniqueIsTaken()` | — | ✘ |
| `bothNonUniqueIsMixed()` | — | ✘ |
| `bothUniqueIsMixed()` | — | ✘ |
| `raceAgainstNoRaceIsMixed()` | — | ✘ |

**class `ItemObjectMergeHelpersTest.DistributeCharges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wand(int, int)` | — | ✘ |
| `rod(int, int)` | — | ✘ |
| `partialMoveToNewStack()` | — | ✘ |
| `wholeMoveLeavesSource()` | — | ✘ |
| `existingDestinationAdds()` | — | ✘ |
| `shareRoundsDown()` | — | ✘ |
| `rodTimeoutCappedForNewStack()` | — | ✘ |
| `rodTimeoutBelowCapAllMoves()` | — | ✘ |
| `wholeRodMoveLeavesSource()` | — | ✘ |
| `rodTimeoutToExistingStack()` | — | ✘ |
| `rodTimeoutClampedToExistingCap()` | — | ✘ |
| `rodTimeoutAtCapMovesNothing()` | — | ✘ |
| `otherTypesUntouched()` | — | ✘ |

**class `ItemObjectMergeHelpersTest.EffectKnowledge`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noCounterpart()` | — | ✘ |
| `notFullyKnownWithoutCounterpart()` | — | ✘ |
| `emptyListsAreKnown()` | — | ✘ |
| `sharedEffectIsKnown()` | — | ✘ |
| `unlearnedEffectIsNotKnown()` | — | ✘ |
| `extraKnownEffectIsNotKnown()` | — | ✘ |


##### `ItemObjectMessagingTest.java`

**Responsible for:** Tests the two ItemObject methods that talk to the player: flagMessage, which announces a property giving itself away, and verifyObject, which asks a yes-or-no question.

**class `ItemObjectMessagingTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `bus`, `realBus`, `savedProperties`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `propertiesField()` | — | ✔ |
| `registerProperty(ObjectFlag, String)` | — | ✔ |
| `recordingInput(boolean, List<String>)` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |

**class `ItemObjectMessagingTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `ItemObjectMessagingTest.FlagMessages`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `messageIsAnnounced()` | — | ✔ |
| `noMessageIsSilent()` | — | ✔ |
| `emptyMessageIsSilent()` | — | ✔ |
| `missingPropertyAnnouncesNothing()` | — | ✔ |
| `sentinelsAnnounceNothing()` | — | ✔ |
| `untaggedMessagePassesThrough()` | — | ✔ |

**class `ItemObjectMessagingTest.Verify`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `answerIsReturned()` | — | ✔ |
| `promptNamesTheItem()` | — | ✔ |


##### `ItemObjectModMessageTest.java`

**Responsible for:** Tests ItemObject#modMessage(ObjectModifier), the port of mod_message in obj-knowledge.c.

**class `ItemObjectModMessageTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `SIGN_CASES`, `SILENT`, `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `itemWith(ObjectModifier, int)` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `positiveValuePrintsPositiveLine()` | — | ✔ |
| `negativeValuePrintsNegativeLine()` | — | ✔ |
| `magnitudeDoesNotMatter()` | — | ✔ |
| `zeroValueIsSilent()` | — | ✔ |
| `absentEntryIsSilent()` | — | ✔ |
| `otherModifiersAreNotRead()` | — | ✔ |
| `nullMapIsSilent()` | — | ✔ |
| `infraAlwaysPrints()` | — | ✔ |
| `lightAlwaysPrints()` | — | ✔ |
| `unnamedModifiersAreSilent()` | — | ✔ |
| `bookendsAreSilent()` | — | ✔ |
| `nullArgumentIsSilent()` | — | ✔ |
| `itemIsNotChanged()` | — | ✔ |

**record `ItemObjectModMessageTest.SignCase`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `mod`, `positive`, `negative`

**class `ItemObjectModMessageTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `ItemObjectMutatorsTest.java`

**Responsible for:** Tests ItemObject's mutators and the small accessors around them.

**class `ItemObjectMutatorsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `item`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadedItem()` | — | ✔ |
| `brand(String)` | — | ✔ |
| `brand(String, String, int, int)` | — | ✔ |
| `slay(String)` | — | ✔ |
| `slay(String, String, MonsterRaceFlag, int, int)` | — | ✔ |
| `newItem()` | — | ✔ |

**class `ItemObjectMutatorsTest.BrandsAndSlays`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareItemHasNone()` | — | ✔ |
| `addingCreatesTheSet()` | — | ✔ |
| `slaysAreIndependent()` | — | ✔ |
| `removingLeavesTheRest()` | — | ✔ |
| `clearingEmptiesTheSet()` | — | ✔ |
| `clearingBareItemIsHarmless()` | — | ✔ |

**class `ItemObjectMutatorsTest.AppendRule`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `brandWithNoRivalIsAdded()` | — | ✘ |
| `brandOnBareItemCreatesTheSet()` | — | ✘ |
| `strongerBrandReplaces()` | — | ✘ |
| `weakerBrandIsRefused()` | — | ✘ |
| `equalBrandIsRefused()` | — | ✘ |
| `sameBrandTwice()` | — | ✘ |
| `differentElementsCoexist()` | — | ✘ |
| `replacementLeavesOthers()` | — | ✘ |
| `brandsJudgedByMultiplier()` | — | ✘ |
| `slayWithNoRivalIsAdded()` | — | ✘ |
| `slayOnBareItemCreatesTheSet()` | — | ✘ |
| `strongerSlayReplaces()` | — | ✘ |
| `weakerSlayIsRefused()` | — | ✘ |
| `equalSlayIsRefused()` | — | ✘ |
| `differentMonstersCoexist()` | — | ✘ |
| `slaysAreRivalsByMonstersNotName()` | — | ✘ |
| `sameNameOnDifferentMonstersCoexist()` | — | ✘ |
| `slaysJudgedByMultiplier()` | — | ✘ |

**class `ItemObjectMutatorsTest.Flags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singleFlagReportsNovelty()` | — | ✔ |
| `setFlagsAdds()` | — | ✔ |
| `setFlagsToReplaces()` | — | ✔ |
| `setFlagsToCopiesIn()` | — | ✔ |

**class `ItemObjectMutatorsTest.Modifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentModifierIsZero()` | — | ✔ |
| `bareItemReadsModifiersAsZero()` | — | ✔ |
| `storingCreatesTheMap()` | — | ✔ |
| `statOverloadAgrees()` | — | ✔ |

**class `ItemObjectMutatorsTest.Scalars`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `scalarsAreDistinct()` | — | ✔ |
| `setOriginWrites()` | — | ✔ |

**class `ItemObjectMutatorsTest.Knowledge`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareItemIsUnknown()` | — | ✔ |
| `setKnownAttachesAndDetaches()` | — | ✔ |
| `clearingKnownMakesItUnknown()` | — | ✔ |
| `noticeStartsEmpty()` | — | ✔ |
| `noticeIsReadable()` | — | ✔ |

**class `ItemObjectMutatorsTest.ElementResistances`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareItemGetsTheMap()` | — | ✔ |
| `absentElementGetsAnEntry()` | — | ✔ |
| `existingEntryKeepsItsFlags()` | — | ✔ |
| `negativeLevelsArePassedThrough()` | — | ✔ |
| `zeroIsStored()` | — | ✔ |
| `otherElementsAreUntouched()` | — | ✔ |
| `createdMapStaysWritable()` | — | ✔ |

**class `ItemObjectMutatorsTest.RechargeTime`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setTimeStores()` | — | ✔ |
| `setTimeCopiesIn()` | — | ✔ |
| `getTimeCopiesOut()` | — | ✔ |
| `setTimeNullClears()` | — | ✔ |
| `setTimeoutWrites()` | — | ✔ |


##### `ItemObjectNameAndValueVisibilityTest.java`

**Responsible for:** Pins the linkage of the three methods ported from object_value (obj-power.c), object_kind_name and obj_desc_name_format (obj-desc.c).

**class `ItemObjectNameAndValueVisibilityTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `find(String, Class<?>)` | — | ✘ |
| `objectValueIsPublic()` | — | ✘ |
| `objectKindNameIsPublic()` | — | ✘ |
| `objDescNameFormatIsPublic()` | — | ✘ |


##### `ItemObjectNoticeHasTest.java`

**Responsible for:** Tests ItemObject#getNoticeHas, the port of C's obj->notice & OBJ_NOTICE_x read.

**class `ItemObjectNoticeHasTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `item`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `freshItemHasNone()` | — | ✘ |
| `raisedFlagTestsTrue()` | — | ✘ |
| `orNoticeTestsTrue()` | — | ✘ |
| `onlyTheRaisedFlagIsTrue()` | — | ✘ |
| `allFlagsTogether()` | — | ✘ |
| `loweredFlagTestsFalse()` | — | ✘ |
| `loweringOneKeepsAnother()` | — | ✘ |
| `readsOwnSetNotKnown()` | — | ✘ |
| `copyIsIndependent()` | — | ✘ |
| `wipeClearsFlags()` | — | ✘ |
| `getNoticeCopyDoesNotLeak()` | — | ✘ |


##### `ItemObjectNoticeMutatorsTest.java`

**Responsible for:** Tests ItemObject#setNoticeOn and ItemObject#setNoticeOff, the ports of C's obj->notice \|= flag and obj->notice &= ~flag.

**class `ItemObjectNoticeMutatorsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `item`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `onRaisesFlag()` | — | ✘ |
| `onTwiceIsIdempotent()` | — | ✘ |
| `onLeavesOthers()` | — | ✘ |
| `onEveryFlag()` | — | ✘ |
| `offClearsFlag()` | — | ✘ |
| `offOnDownFlag()` | — | ✘ |
| `offLeavesOthers()` | — | ✘ |
| `ignoreUnignoreRoundTrip()` | — | ✘ |
| `sharesSetWithOrNotice()` | — | ✘ |
| `getNoticeCopyIsDetached()` | — | ✘ |


##### `ItemObjectObjectFlagsTest.java`

**Responsible for:** Tests ItemObject#objectFlags(Flag) — the port of C's object_flags (src/obj-util.c:351), which is three clauses long: of_wipe(flags); if (!obj) return; of_copy(flags, obj-&gt;flags); The expected values below come from those clauses, not from the Java.

**class `ItemObjectObjectFlagsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `itemWithFlags(Flag<ObjectFlag>)` | — | ✔ |
| `flagsOf(ObjectFlag)` | — | ✔ |
| `copiesTheItemsFlagsIntoAnEmptySet()` | — | ✘ |
| `wipesTheCallersSetBeforeCopying()` | — | ✘ |
| `anItemWithNoFlagsEmptiesTheSet()` | — | ✘ |
| `reusingOneSetAcrossTwoItemsDoesNotAccumulate()` | — | ✘ |
| `theResultDoesNotAliasTheItemsOwnSet()` | — | ✘ |
| `agreesWithGetFlags()` | — | ✘ |


##### `ItemObjectOrderingTest.java`

**Responsible for:** Tests ItemObject#earlierObject, the port of C's earlier_object (player-calcs.c) — the comparison the pack ordering is built from.

**class `ItemObjectOrderingTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `READABLE_SVAL`, `savedPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `earlier(ItemObject, ItemObject, boolean)` | — | ✔ |
| `casterClass()` | — | ✔ |
| `item(TValue, int)` | — | ✔ |
| `kind(TValue, int)` | — | ✔ |
| `installCaster()` | — | ✔ |
| `restorePlayer()` | — | ✔ |
| `liveAcrossLatePlayer()` | — | ✔ |

**class `ItemObjectOrderingTest.Nulls`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullCandidateNeverWins()` | — | ✔ |
| `nullIncumbentAlwaysLoses()` | — | ✔ |

**class `ItemObjectOrderingTest.ReadableBooks`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `readableBookComesFirst()` | — | ✔ |
| `unreadableBookDoesNotDisplace()` | — | ✔ |
| `storeIgnoresThePreference()` | — | ✔ |
| `otherRealmWithSameSvalIsNotBrowsable()` | — | ✔ |
| `rightTvalWrongSvalIsNotBrowsable()` | — | ✔ |
| `aNonBookIsNotBrowsable()` | — | ✔ |

**class `ItemObjectOrderingTest.LaterComparisons`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `typeOrdersFirst()` | — | ✔ |
| `subTypeOrdersWithinAType()` | — | ✔ |
| `identicalObjectsHaveNoPreference()` | — | ✔ |


##### `ItemObjectPartCRound2Test.java`

**Responsible for:** Round 2 additions for Part C of ItemObject - the value methods and the power driver - covering what ItemObjectValueTest and ItemObjectPowerCursesTest leave open.

**class `ItemObjectPartCRound2Test`** — class Javadoc: ✔ · type stamp: on 261007

- Fields (0/10 documented): ✘ `savedRenderers`, `savedUiBases`, `savedUiEntries`, `savedProperties`, `savedFlagSets`, `savedElementSets`, `savedElementPowers`, `savedCurses`, `cloakKind`, `savedPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistries()` | — | ✔ |
| `restoreRegistries()` | — | ✔ |
| `curse(int, int, boolean, int)` | — | ✘ |
| `cloak(int, Object)` | — | ✔ |
| `cursePower(ItemObject, int)` | — | ✘ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |
| `multiplyHundredIsOrdinary()` | — | ✔ |
| `bothPasses()` | — | ✔ |
| `layingOrderIrrelevant()` | — | ✔ |


##### `ItemObjectPartDRound2Test.java`

**Responsible for:** The second-round tests for the power components of ItemObject, the ports of effects_power, element_power, flags_power, modifier_power, to_ac_power, ac_power, to_hit_power and rescale_bow_power (obj-power.c).

**class `ItemObjectPartDRound2Test`** — class Javadoc: ✔ · type stamp: on 261007

- Fields (1/8 documented): ✔ `savedPlayer` — ✘ `savedRenderers`, `savedUiBases`, `savedUiEntries`, `savedProperties`, `savedFlagSets`, `savedElementSets`, `savedElementPowers`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistries()` | — | ✔ |
| `restoreRegistries()` | — | ✔ |
| `invoke(Object, String, Class<?>[], Object)` | — | ✔ |
| `price(ItemObject, String, int)` | — | ✔ |
| `price(ItemObject, String, Curse, int)` | — | ✔ |
| `item(TValue)` | — | ✘ |
| `itemWith(TValue, ObjectFlag)` | — | ✘ |
| `flagsOf(ObjectFlag)` | — | ✘ |
| `curse(Flag<ObjectFlag>, int, int)` | — | ✔ |
| `resist(ItemObject, ElementEnum, int)` | — | ✘ |
| `armour(int, int, int)` | — | ✘ |
| `resistCurse(int, ElementEnum)` | — | ✘ |
| `modifierCurse(ObjectModifier, int)` | — | ✘ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |
| `zeroPowerActivationBlocksKind()` | — | ✔ |
| `negativeRatio()` | — | ✔ |
| `capBoundary(int, int)` | — | ✔ |
| `toAcEdges(int, int)` | — | ✔ |
| `toHitTruncation(int, int)` | — | ✔ |
| `bowBoundaries(int, int)` | — | ✔ |
| `twoImmunities()` | — | ✔ |
| `mixedImmunityAndResist()` | — | ✔ |
| `curseImmunities()` | — | ✔ |
| `rowsResetAcrossOverloads()` | — | ✔ |
| `flagsUnchanged()` | — | ✔ |
| `curseFamilies()` | — | ✔ |
| `flagRowsResetAcrossOverloads()` | — | ✔ |
| `curseThresholds(ObjectModifier, int, int)` | — | ✔ |


##### `ItemObjectPartERound2Test.java`

**Responsible for:** The second-round tests for the damage components of ItemObject, the ports of slay_power, extra_shots_power, extra_blows_power and launcher_ammo_damage_power (obj-power.c).

**class `ItemObjectPartERound2Test`** — class Javadoc: ✔ · type stamp: on 261007

- Fields (1/2 documented): ✔ `savedPlayer` — ✘ `savedArchery`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedArchery()` | — | ✔ |
| `restoreArchery()` | — | ✔ |
| `invoke(Object, String, Class<?>[], Object)` | — | ✘ |
| `price(ItemObject, String, int)` | — | ✘ |
| `price(ItemObject, String, Curse, int)` | — | ✘ |
| `slayPrice(ItemObject, int, int)` | — | ✘ |
| `item(TValue)` | — | ✘ |
| `curse(ObjectModifier, int)` | — | ✘ |
| `brand(String, int)` | — | ✘ |
| `slay(String, int, int)` | — | ✘ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |
| `weakBrandBelowBaseline(int, int)` | — | ✔ |
| `duplicateSlay()` | — | ✔ |
| `twoSlays()` | — | ✔ |
| `killAndBrand()` | — | ✔ |
| `negativeBlows(int, int)` | — | ✔ |
| `shotsTruncate(int, int, int)` | — | ✔ |
| `negativeAmmo(TValue, int)` | — | ✔ |


##### `ItemObjectPartFRound2Test.java`

**Responsible for:** Part F round-two checks on ItemObject: the inscription scan, the flag readers and the aliasing behaviour of objectFlags.

**class `ItemObjectPartFRound2Test`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `overlappingMatchesCounted()` | — | ✔ |
| `singleAbsentAndNoNote()` | — | ✔ |
| `hasFlagReadsOwnFlags()` | — | ✔ |
| `objectFlagsReplaces()` | — | ✔ |
| `objectFlagsAliasedEmpties()` | — | ✔ |


##### `ItemObjectPartHRound2Test.java`

**Responsible for:** Part H round-two checks on the ItemObject accessors and mutators: the never-null grid, the flag and notice accessors on a missing set, the self-copy case of setFlagsTo, the modifier readers, and setEffectMessage.

**class `ItemObjectPartHRound2Test`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullField(ItemObject, String)` | — | ✘ |
| `field(ItemObject, String)` | — | ✘ |
| `bareItemGridIsZero()` | — | ✔ |
| `gridSetClearWipe()` | — | ✔ |
| `scalarAccessorsDoNotCross()` | — | ✔ |
| `tValueDefault()` | — | ✔ |
| `originDefaultAndSet()` | — | ✔ |
| `flagAndNoticeGettersCopy()` | — | ✔ |
| `missingSetsReadAsEmpty()` | — | ✔ |
| `mutatorsOnMissingSets()` | — | ✔ |
| `setFlagsToReplaces()` | — | ✔ |
| `setFlagsToSelfEmpties()` | — | ✔ |
| `setFlagsToOwnCopyKeeps()` | — | ✔ |
| `setFlagsToNull()` | — | ✔ |
| `setFlagsAdds()` | — | ✔ |
| `getObjectFlagsIsLive()` | — | ✔ |
| `noticeFlagsIndependent()` | — | ✔ |
| `modifierValueZeros()` | — | ✔ |
| `putModifierNegative()` | — | ✔ |
| `modifiersLiveAndNull()` | — | ✔ |
| `setModifiersStores()` | — | ✔ |
| `timeCopies()` | — | ✔ |
| `effectStoresAndNullIsEmpty()` | — | ✔ |
| `effectMessage()` | — | ✔ |
| `bareItemNotArtifact()` | — | ✔ |
| `knownLinksOneWay()` | — | ✔ |
| `referenceFields()` | — | ✔ |


##### `ItemObjectPowerCursesTest.java`

**Responsible for:** Tests the driver and curse plumbing of the power calculation in ItemObject: both objectPower overloads, both nonStandardWeightPower overloads, both cursePower overloads, freeSlays, freeBrands, freeCurses and applyCurseAttributes.

**class `ItemObjectPowerCursesTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (2/10 documented): ✔ `cloakKind`, `savedPlayer` — ✘ `savedRenderers`, `savedUiBases`, `savedUiEntries`, `savedProperties`, `savedFlagSets`, `savedElementSets`, `savedElementPowers`, `savedCurses`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistries()` | — | ✔ |
| `restoreRegistries()` | — | ✔ |
| `curse(int, int, boolean, int, ObjectFlag)` | — | ✔ |
| `register(Curse)` | — | ✔ |
| `cloak(int, int, Object)` | — | ✔ |
| `invoke(Object, String, Class<?>[], Object)` | — | ✔ |
| `weightPower(ItemObject, int)` | — | ✘ |
| `cursePower(ItemObject, int)` | — | ✘ |
| `objectPower(ItemObject)` | — | ✘ |
| `applyCurses(ItemObject, Curse)` | — | ✘ |
| `withLevel(int)` | — | ✘ |
| `flagsOf(ObjectFlag)` | — | ✘ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |

**class `ItemObjectPowerCursesTest.NonStandardWeight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `uncursed()` | — | ✔ |
| `truncatesSmallDifference()` | — | ✔ |
| `heavierLosesOne()` | — | ✔ |
| `lighterGains()` | — | ✔ |
| `multiplyingCurse()` | — | ✔ |
| `inactiveCurse()` | — | ✔ |
| `baseArmourSkips()` | — | ✔ |
| `throwingDividesFirst()` | — | ✔ |
| `bothTerms()` | — | ✔ |
| `lighterThrowing()` | — | ✔ |
| `throwingFromCurse()` | — | ✔ |
| `saturates()` | — | ✔ |

**class `ItemObjectPowerCursesTest.ApplyCurseAttributes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noCurses()` | — | ✔ |
| `weightOrder()` | — | ✔ |
| `combatBonuses()` | — | ✔ |
| `skipped()` | — | ✔ |
| `ignoredByIndex()` | — | ✔ |
| `ignoredNull()` | — | ✔ |
| `flagsUnion()` | — | ✔ |
| `modifiers()` | — | ✔ |
| `resistanceTable(int, int, int)` | — | ✔ |
| `bothPersists()` | — | ✔ |
| `absentElement()` | — | ✔ |
| `impossibleLevel()` | — | ✔ |
| `ignoredIndexZero()` | — | ✔ |
| `hitAndDamageAdd()` | — | ✔ |
| `weightSaturates()` | — | ✔ |
| `leavesCurses()` | — | ✔ |

**class `ItemObjectPowerCursesTest.CursePower`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `uncursed()` | — | ✔ |
| `ordinaryDiscount()` | — | ✔ |
| `inactive()` | — | ✔ |
| `ordinaryPriced()` | — | ✔ |
| `weightPenaltyScaled()` | — | ✔ |
| `strengthClamped()` | — | ✔ |
| `truncatesTowardZero()` | — | ✔ |
| `benefitUnscaled()` | — | ✔ |
| `twoWeightCurses()` | — | ✔ |
| `mixed()` | — | ✔ |
| `ordinaryInScratchCopies()` | — | ✔ |
| `objectUnchanged()` | — | ✔ |

**class `ItemObjectPowerCursesTest.ObjectPower`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plainCloak()` | — | ✔ |
| `curseStep()` | — | ✔ |
| `weightStep()` | — | ✔ |
| `inhibitedReturnsEarly()` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectPowerCursesTest.Free`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptiesCopyOnly()` | — | ✔ |


##### `ItemObjectPropertyPowerTest.java`

**Responsible for:** Tests the property components of the power calculation in ItemObject: effectsPower, elementPower, flagsPower, modifierPower, jewelleryPower, toAcPower, acPower, toHitPower and rescaleBowPower, each with its plain and Curse overload.

**class `ItemObjectPropertyPowerTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (1/8 documented): ✔ `savedPlayer` — ✘ `savedRenderers`, `savedUiBases`, `savedUiEntries`, `savedProperties`, `savedFlagSets`, `savedElementSets`, `savedElementPowers`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistries()` | — | ✔ |
| `restoreRegistries()` | — | ✔ |
| `invoke(Object, String, Class<?>[], Object)` | — | ✔ |
| `price(ItemObject, String, int)` | — | ✔ |
| `price(ItemObject, String, Curse, int)` | — | ✔ |
| `item(TValue)` | — | ✔ |
| `itemWith(TValue, ObjectFlag)` | — | ✔ |
| `combatCurse(int, int)` | — | ✔ |
| `resist(ItemObject, ElementEnum, int)` | — | ✔ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |

**class `ItemObjectPropertyPowerTest.Effects`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withKindPower(int)` | — | ✘ |
| `activations(int)` | — | ✘ |
| `activationWins()` | — | ✔ |
| `kindFallback()` | — | ✔ |
| `nullActivation()` | — | ✔ |
| `nullEntry()` | — | ✔ |
| `firstOnly()` | — | ✔ |
| `nothing()` | — | ✔ |
| `kindless()` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectPropertyPowerTest.Elements`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cloak()` | — | ✘ |
| `singleResistance()` | — | ✔ |
| `singleImmunity()` | — | ✔ |
| `vulnerability()` | — | ✔ |
| `levelTwo()` | — | ✔ |
| `ignoreAndResist()` | — | ✔ |
| `ignoreWithoutPower()` | — | ✔ |
| `twoLowResists()` | — | ✔ |
| `fullLowResists()` | — | ✔ |
| `fullImmunities()` | — | ✔ |
| `fullHighResists()` | — | ✔ |
| `countsReset()` | — | ✔ |
| `runningTotal()` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectPropertyPowerTest.FlagsPowerCases`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `single()` | — | ✔ |
| `twoOfAFamily()` | — | ✔ |
| `fullSustains()` | — | ✔ |
| `protectionFactor()` | — | ✔ |
| `fullProtections()` | — | ✔ |
| `typeMultiplier()` | — | ✔ |
| `fullMisc()` | — | ✔ |
| `badFlag()` | — | ✔ |
| `zeroPower()` | — | ✔ |
| `unknownFlag()` | — | ✔ |
| `countsReset()` | — | ✔ |
| `curseOverload()` | — | ✔ |
| `flagsOf(ObjectFlag)` | — | ✘ |

**class `ItemObjectPropertyPowerTest.Modifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cloakWith(Object)` | — | ✘ |
| `none()` | — | ✔ |
| `small()` | — | ✔ |
| `tableBoundary()` | — | ✔ |
| `inhibitBoundary()` | — | ✔ |
| `negative()` | — | ✔ |
| `totalWithoutPower()` | — | ✔ |
| `summed()` | — | ✔ |
| `typeMultiplier()` | — | ✔ |
| `curseOverload()` | — | ✔ |
| `curseWithNone()` | — | ✔ |

**class `ItemObjectPropertyPowerTest.Jewellery`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onlyJewellery(TValue, int)` | — | ✔ |
| `runningTotal()` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectPropertyPowerTest.ToAc`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `priceFor(int)` | — | ✘ |
| `bands(int, int)` | — | ✔ |
| `runningTotal()` | — | ✔ |
| `curseOverload(int, int)` | — | ✔ |

**class `ItemObjectPropertyPowerTest.Ac`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `armour(int, int, int)` | — | ✘ |
| `noBase()` | — | ✔ |
| `scaled()` | — | ✔ |
| `toAcCounts()` | — | ✔ |
| `capped()` | — | ✔ |
| `truncates()` | — | ✔ |
| `weightless()` | — | ✔ |
| `cancelled()` | — | ✔ |
| `runningTotal()` | — | ✔ |
| `curseOverload()` | — | ✔ |

**class `ItemObjectPropertyPowerTest.ToHit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `priceFor(int)` | — | ✘ |
| `linear(int, int)` | — | ✔ |
| `runningTotal()` | — | ✔ |
| `curseOverload(int, int)` | — | ✔ |

**class `ItemObjectPropertyPowerTest.RescaleBow`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bowDivided(int, int)` | — | ✔ |
| `othersUnchanged(TValue)` | — | ✔ |
| `curseOverload()` | — | ✔ |


##### `ItemObjectQualityTest.java`

**Responsible for:** Tests ItemObject#ignoreLevelOf() and ItemObject#objectIsInQuiver(Player).

**class `ItemObjectQualityTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `savedConstants`, `savedPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `knownItem(TValue)` | — | ✔ |
| `partlyKnown(TValue)` | — | ✔ |

**class `ItemObjectQualityTest.Quality`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unknownAnswersMax()` | — | ✔ |
| `plainRingIsAverage()` | — | ✔ |
| `positiveModifierIsAverage()` | — | ✔ |
| `positiveCombatValueIsAverage()` | — | ✔ |
| `negativeCombatValueIsBad()` | — | ✔ |
| `positiveBeatsNegative()` | — | ✔ |
| `amuletIsJewellery()` | — | ✔ |
| `fullyKnownPlainWeaponIsAverage()` | — | ✔ |
| `fullyKnownGoodWeaponIsGood()` | — | ✔ |
| `partlyKnownUnassessedAnswersMax()` | — | ✔ |
| `partlyKnownAssessedAnswersAll()` | — | ✔ |

**class `ItemObjectQualityTest.InQuiver`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `looseItemIsNotInQuiver()` | — | ✔ |
| `quiveredItemIsInIt()` | — | ✔ |
| `identicalStackIsNotTheSame()` | — | ✔ |


##### `ItemObjectRechargeTest.java`

**Responsible for:** Unit tests for ItemObject#rechargeTimeout() and its helper ItemObject#numberCharging() — the ports of C's recharge_timeout and number_charging (obj-util.c).

**class `ItemObjectRechargeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedWorldMaxDepth()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `rod(int, int, int)` | — | ✔ |
| `set(ItemObject, String, Object)` | — | ✔ |

**class `ItemObjectRechargeTest.NothingCharging`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `readyItem()` | — | ✘ |
| `noTimeField()` | — | ✘ |
| `zeroChargeTime()` | — | ✘ |
| `negativeTimeout()` | — | ✘ |

**class `ItemObjectRechargeTest.SingleRod`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `midRecharge()` | — | ✘ |
| `finalTick()` | — | ✘ |
| `wholeRecharge()` | — | ✘ |

**class `ItemObjectRechargeTest.Stack`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `perRodDrain(int, int, int, boolean)` | — | ✔ |
| `clampedToStackSize()` | — | ✘ |
| `doesNotOvershoot()` | — | ✘ |
| `oneReportPerRod()` | — | ✘ |

**class `ItemObjectRechargeTest.DiceInterval`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `diceRod(int, int, int, int, int)` | — | ✔ |
| `oneDTen(int, int)` | — | ✔ |
| `baseAndDice(int, int)` | — | ✔ |
| `truncates()` | — | ✔ |
| `drain(int, int, int, boolean)` | — | ✔ |


##### `ItemObjectSimilarTest.java`

**Responsible for:** Tests ItemObject#similar, the port of C's object_similar (obj-util.c) — the rule deciding whether two items may occupy one stack.

**class `ItemObjectSimilarTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/9 documented): ✔ `savedBodies`, `kind`, `SIREN` — ✘ `savedPlayer`, `savedRaces`, `savedCurses`, `savedCurseMax`, `first`, `second`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✘ |
| `objectRegistryField(String)` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `testRace(PlayerBody)` | — | ✔ |
| `set(ItemObject, String, Object)` | — | ✘ |
| `elements()` | — | ✔ |
| `modifiers()` | — | ✔ |
| `weapon()` | — | ✔ |
| `item(TValue)` | — | ✔ |
| `curse(String)` | — | ✔ |
| `ego()` | — | ✔ |
| `mode(ObjectStackEnum)` | — | ✘ |
| `newPair()` | — | ✔ |

**class `ItemObjectSimilarTest.EarlyVetoes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseline()` | — | ✔ |
| `notWithItself()` | — | ✔ |
| `mimicsNeverStack()` | — | ✔ |
| `differentKinds()` | — | ✘ |
| `differentTvals()` | — | ✘ |
| `differentFlags()` | — | ✘ |
| `artifactsNeverStack()` | — | ✔ |

**class `ItemObjectSimilarTest.Elements`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `differentResistance()` | — | ✘ |
| `differentHates()` | — | ✘ |
| `differentIgnores()` | — | ✘ |
| `randomFlagIsIgnored()` | — | ✔ |
| `elementPresentOnOneSideOnly()` | — | ✔ |
| `absentElementEqualsDefaultEntry()` | — | ✔ |
| `elementOf(ItemObject, ElementEnum)` | — | ✘ |

**class `ItemObjectSimilarTest.TypeBranch`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `chests()` | — | ✔ |
| `consumables()` | — | ✔ |
| `chargesWithinRange()` | — | ✔ |
| `chargesOverflow()` | — | ✔ |

**class `ItemObjectSimilarTest.Wearables`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `differentAc()` | — | ✘ |
| `differentDice()` | — | ✘ |
| `differentSides()` | — | ✘ |
| `bonusesAreComparedByValue()` | — | ✔ |
| `eachBonusIsCompared()` | — | ✔ |
| `differentModifiers()` | — | ✘ |
| `modifierPresentOnOneSideOnly()` | — | ✔ |
| `zeroModifierEqualsAbsentModifier()` | — | ✔ |
| `modifiersPresentOnBothAreComparedByValue()` | — | ✔ |
| `zeroPowerCurseAgainstNoCurses()` | — | ✔ |
| `zeroPowerCurseEqualsAbsentCurseWhenBothCursed()` | — | ✔ |
| `curseOnOneSideOnly()` | — | ✔ |
| `unregisteredCurseIsIgnored()` | — | ✔ |
| `differentEgos()` | — | ✔ |
| `differentCurses()` | — | ✘ |
| `sameCurseDifferentTimeouts()` | — | ✔ |
| `sameCurseDifferentPowers()` | — | ✔ |
| `rechargingWearable()` | — | ✔ |
| `lightsWithEqualFuel()` | — | ✘ |
| `lightsWithDifferentFuel()` | — | ✘ |


##### `ItemObjectStackingTest.java`

**Responsible for:** Tests the three rules that decide whether two stacks may become one — checkForInscription, ItemObject#objectStackable and ItemObject#mergeable — the ports of C's check_for_inscrip (obj-util.c), object_stackable and object_mergeable (obj-pile.c).

**class `ItemObjectStackingTest`** — class Javadoc: ✔ · type stamp: on 260824

- Fields (5/14 documented): ✔ `SLOT_SIZE`, `THROWN_MULT`, `MAX_STACK`, `savedCurses`, `kind` — ✘ `savedPlayer`, `savedConstants`, `savedBodies`, `savedRaces`, `first`, `second`, `packMode`, `quiverMode`, `storeMode`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✔ |
| `restoreGlobals()` | — | ✘ |
| `arrows(int)` | — | ✔ |
| `thrown(int)` | — | ✔ |
| `item(TValue, int)` | — | ✔ |
| `mode(ObjectStackEnum)` | — | ✔ |
| `testRace(PlayerBody)` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `buildPair()` | — | ✘ |

**class `ItemObjectStackingTest.Inscriptions`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noNoteIsZero()` | — | ✘ |
| `absentTagIsZero()` | — | ✘ |
| `presentTagCountsOnce()` | — | ✘ |
| `repeatedTagCountsTwice()` | — | ✘ |
| `overlappingOccurrencesCount()` | — | ✔ |
| `tagAtStartCounts()` | — | ✘ |
| `emptyTagIsZero()` | — | ✔ |

**class `ItemObjectStackingTest.InscriptionCompatibility`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `twoUninscribedStack()` | — | ✔ |
| `inscribedStacksWithUninscribed()` | — | ✔ |
| `matchingInscriptionsStack()` | — | ✘ |
| `differingInscriptionsDoNotStack()` | — | ✔ |
| `emptyInscriptionConflicts()` | — | ✔ |
| `emptyInscriptionStacksWithNone()` | — | ✘ |

**class `ItemObjectStackingTest.Capacity`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `smallStacksMerge()` | — | ✘ |
| `packFillsToMaxStack()` | — | ✘ |
| `packRefusesOverMaxStack()` | — | ✘ |
| `ammoFillsQuiverSlot()` | — | ✔ |
| `ammoRefusedOverSlotSize()` | — | ✘ |
| `thrownFillsQuiverSlot()` | — | ✔ |
| `thrownRefusedOverSlotLimit()` | — | ✘ |
| `storeWaivesMaxStack()` | — | ✔ |
| `storeWaivesQuiverLimit()` | — | ✘ |
| `inscriptionStillApplies()` | — | ✔ |


##### `ItemObjectStandardToHTest.java`

**Responsible for:** Tests ItemObject#hasStandardToH(), the port of C's object_has_standard_to_h (obj-knowledge.c:580).

**class `ItemObjectStandardToHTest`** — class Javadoc: ✔ · type stamp: on 260815

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `poke(Object, String, Object)` | — | ✔ |
| `kindWithFixedToH(int)` | — | ✔ |
| `kindWithVaryingToH()` | — | ✔ |
| `item(TValue, ObjectKind, int)` | — | ✔ |

**class `ItemObjectStandardToHTest.NoKind`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noKindIsStandard()` | — | ✘ |
| `noKindIsStandardWhateverTheFigure()` | — | ✔ |
| `noKindIsAnsweredBeforeTheTypeIsRead()` | — | ✔ |

**class `ItemObjectStandardToHTest.BodyArmourWithFixedToH`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `atTheKindsBaseIsStandard()` | — | ✔ |
| `awayFromTheKindsBaseIsNotStandard()` | — | ✔ |
| `zeroIsNotStandardForPenalisedArmour()` | — | ✔ |
| `allThreeArmourTypesTakeTheBranch()` | — | ✔ |
| `unpenalisedArmourIsStandardAtZero()` | — | ✔ |

**class `ItemObjectStandardToHTest.BodyArmourWithVaryingToH`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `varyingKindFallsThroughToTheZeroTest()` | — | ✘ |
| `varyingKindDoesNotExcuseANonZeroFigure()` | — | ✔ |

**class `ItemObjectStandardToHTest.NotBodyArmour`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zeroIsStandard()` | — | ✘ |
| `aBonusIsNotStandard()` | — | ✘ |
| `aPenaltyIsNotStandard()` | — | ✔ |
| `theKindsFigureIsNotConsulted()` | — | ✔ |


##### `ItemObjectValueTest.java`

**Responsible for:** Tests the three pricing methods of ItemObject - objectValue, objectValueBase and ItemObject#objectValueReal(int) - the ports of C's object_value, object_value_base and object_value_real (obj-power.c).

**class `ItemObjectValueTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (4/11 documented): ✔ `PRICE_AT_20060`, `PRICE_AT_20030`, `savedRenderers`, `savedPlayer` — ✘ `savedUiBases`, `savedUiEntries`, `savedProperties`, `savedFlagSets`, `savedElementSets`, `savedElementPowers`, `savedArchery`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistries()` | — | ✔ |
| `restoreRegistries()` | — | ✔ |
| `kind(TValue, int, boolean)` | — | ✔ |
| `item(ObjectKind, int)` | — | ✔ |
| `withThreeBlows(ItemObject)` | — | ✔ |
| `call(ItemObject, String, int)` | — | ✔ |
| `installPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✔ |

**class `ItemObjectValueTest.Base`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unawareFiguresByType(TValue, int)` | — | ✔ |
| `awareIsKindCost()` | — | ✔ |

**class `ItemObjectValueTest.FixedPrice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `costTimesQuantity()` | — | ✔ |
| `rodHasNoChargeSurcharge()` | — | ✔ |
| `wandChargeSurcharge()` | — | ✔ |
| `staffChargeSurcharge()` | — | ✔ |
| `chargesRoundUp()` | — | ✔ |
| `surchargeTruncates()` | — | ✔ |
| `freeKind()` | — | ✔ |
| `negativeCostFloored()` | — | ✔ |

**class `ItemObjectValueTest.VariablePower`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `quadraticCurve()` | — | ✔ |
| `highToAc()` | — | ✔ |
| `zeroLiftedToOne()` | — | ✔ |
| `negativePowerFloored()` | — | ✔ |
| `ammunitionRescaled()` | — | ✔ |
| `smallAmmunitionLiftedToOne(int)` | — | ✔ |
| `negativeAmmunitionFloored()` | — | ✔ |
| `inhibitedPower()` | — | ✔ |
| `burningLightRescaled()` | — | ✔ |
| `otherLightsNotRescaled()` | — | ✔ |
| `inhibitedArrowRescaled()` | — | ✔ |

**class `ItemObjectValueTest.Routing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `knownHalfIsUsed()` | — | ✔ |
| `knownHalfIgnoresAwareness()` | — | ✔ |
| `awareFlavourPricedInFull()` | — | ✔ |
| `unawareFlavourGetsBase()` | — | ✔ |
| `fixedPriceIgnoresKnownHalf()` | — | ✔ |
| `awarePotion()` | — | ✔ |
| `unseenSwordWorthless()` | — | ✔ |
| `awareSwordWithoutKnownHalf()` | — | ✔ |
| `quantityArgument()` | — | ✔ |


##### `ItemObjectWeightTest.java`

**Responsible for:** Tests ItemObject#objectWeightOne(), the port of C's object_weight_one (obj-util.c) — one item's weight after its curses have had their say.

**class `ItemObjectWeightTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(int, boolean)` | — | ✔ |
| `curse(int, boolean, int)` | — | ✔ |
| `item(int, Map<Curse,CurseData>)` | — | ✔ |
| `set(ItemObject, String, Object)` | — | ✔ |

**class `ItemObjectWeightTest.Uncursed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `uncursedWeighsItsOwn()` | — | ✔ |
| `noCurseMapWeighsItsOwn()` | — | ✔ |
| `negativeBaseIsFloored()` | — | ✔ |

**class `ItemObjectWeightTest.Additive`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `positiveIsAdded()` | — | ✔ |
| `negativeSubtractsAndFloors()` | — | ✔ |

**class `ItemObjectWeightTest.Multiplying`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hundredIsNoChange()` | — | ✔ |
| `multiplierScales()` | — | ✔ |
| `divisionRoundsAtAHalf()` | — | ✔ |
| `heavierKeepsAMinimum()` | — | ✔ |

**class `ItemObjectWeightTest.Several`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `twoCursesCompose()` | — | ✔ |
| `indexOrderNotInsertionOrder()` | — | ✔ |
| `indexZeroApplies()` | — | ✔ |
| `zeroPowerCurseIsInactive()` | — | ✔ |
| `activeAppliesBesideInactive()` | — | ✔ |


##### `ItemObjectWieldSlotTest.java`

**Responsible for:** Tests ItemObject#wieldSlot(), the port of C's wield_slot (obj-gear.c).

**class `ItemObjectWieldSlotTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (2/2 documented): ✔ `savedPlayer`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(TValue)` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `indexOf(String)` | — | ✔ |
| `eachWearableTypeGoesToItsSlot()` | — | ✔ |
| `neverWornAnswersMinusOne()` | — | ✔ |
| `ringTakesFirstEmptyRingSlot()` | — | ✔ |
| `ringWithAllSlotsFullFallsBackToFirst()` | — | ✔ |
| `bodyWithoutTheSlotAnswersTheSlotCount()` | — | ✔ |
| `asksTheLivePlayer()` | — | ✔ |
| `itemBuiltBeforeAnyPlayer()` | — | ✔ |
| `noPlayerAtAll()` | — | ✔ |


##### `ItemObjectWipeTest.java`

**Responsible for:** Tests ItemObject#wipe() and ItemObject#getObjectFlags().

**class `ItemObjectWipeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `item`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `read(ItemObject, String)` | — | ✔ |
| `curse(String)` | — | ✔ |
| `artifact()` | — | ✔ |
| `ego()` | — | ✔ |
| `brand(String)` | — | ✔ |
| `slay(String)` | — | ✔ |
| `populatedItem()` | — | ✔ |
| `buildAndWipe()` | — | ✘ |
| `fixtureStartsPopulated()` | — | ✔ |
| `resetsScalarsAndReferences()` | — | ✔ |
| `resetsCollectionsToEmptyNotNull()` | — | ✔ |
| `resetsMimickingMIndex()` | — | ✔ |
| `resetsOriginToNoOrigin()` | — | ✔ |
| `resetsMapsToInsertionOrderedType()` | — | ✔ |
| `wipedMapsKeepInsertionOrder()` | — | ✔ |
| `replacesTheFlagSetRatherThanClearingIt()` | — | ✔ |
| `forgetsItsOwningPile()` | — | ✔ |
| `wipingOneItemDoesNotAffectAnother()` | — | ✔ |

**class `ItemObjectWipeTest.GetObjectFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsTheItemsFlags()` | — | ✘ |
| `returnsTheLiveSetNotACopy()` | — | ✔ |


##### `KnownArtifactTest.java`

**Responsible for:** Tests the two known-artifact predicates: ItemObject#isKnownArtifact(), the port of object_is_known_artifact in obj-knowledge.c, and ObjectUtils#objIsKnownArtifact, the port of obj_is_known_artifact in obj-util.c.

**class `KnownArtifactTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(Artifact, ItemObject)` | — | ✔ |
| `artifact()` | — | ✔ |
| `knownHalf(Artifact)` | — | ✔ |
| `isKnownNoKnownHalf()` | — | ✘ |
| `isKnownHalfWithoutArtifact()` | — | ✘ |
| `isKnownHalfWithArtifact()` | — | ✘ |
| `isKnownIgnoresRealArtifact()` | — | ✘ |
| `objNoRealArtifact()` | — | ✘ |
| `objKnownHalfOnlyIsFalse()` | — | ✘ |
| `objNoKnownHalf()` | — | ✘ |
| `objArtifactNotLearned()` | — | ✘ |
| `objBothHalves()` | — | ✘ |


##### `KnownObjectObjectElementIsKnownTest.java`

**Responsible for:** Tests KnownObject#objectElementIsKnown(Player, ItemObject, ElementEnum), the item-shaped counterpart to ObjectUtilsObjectElementIsKnownTest and the port of C's object_element_is_known (obj-knowledge.c:804).

**class `KnownObjectObjectElementIsKnownTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(TValue, ObjectKind, ItemObject)` | — | ✔ |
| `fullyKnownItem()` | — | ✔ |
| `notFullyKnownItem()` | — | ✔ |
| `elementInfo(int)` | — | ✔ |
| `playerWithItemKnowledge()` | — | ✘ |

**class `KnownObjectObjectElementIsKnownTest.SentinelElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `elemNoneReportsUnknown()` | — | ✘ |
| `elemMaxReportsUnknown()` | — | ✘ |

**class `KnownObjectObjectElementIsKnownTest.FullyKnownItem`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnownRegardlessOfTheOtherTwoChecks()` | — | ✘ |

**class `KnownObjectObjectElementIsKnownTest.PlayerKnowsTheElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `KnownObjectObjectElementIsKnownTest.ShadowKnowsTheElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `KnownObjectObjectElementIsKnownTest.NeitherKnowsTheElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsUnknown()` | — | ✘ |
| `checksOnlyTheNamedElement()` | — | ✘ |
| `aKnownButZeroShadowEntryReportsUnknown()` | — | ✘ |


##### `KnownObjectTest.java`

**Responsible for:** Tests KnownObject, the port of C's p->obj_k (src/player.h) and the store behind player_knows_rune and player_learn_rune (src/obj-knowledge.c).

**class `KnownObjectTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/13 documented): ✔ `SAVED_FIELDS`, `weakAcid`, `evil3`, `evilBased` — ✘ `SAVED`, `strongAcid`, `weakFire`, `strongFire`, `evil5`, `undead3`, `siren`, `teleportation`, `knowledge`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✘ |
| `field(String)` | — | ✔ |
| `brand(String, String)` | — | ✘ |
| `slay(String, String, MonsterBase, MonsterRaceFlag)` | — | ✘ |
| `curse(String)` | — | ✘ |
| `newKnowledge()` | — | ✔ |

**class `KnownObjectTest.Initial`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noCombatKnowledge()` | — | ✘ |
| `multipliersStartAtZero()` | — | ✔ |
| `nothingIsKnown()` | — | ✘ |

**class `KnownObjectTest.Multipliers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setDDRoundTrips()` | — | ✘ |
| `setDSRoundTrips()` | — | ✘ |
| `setACRoundTrips()` | — | ✘ |
| `areIndependent()` | — | ✔ |

**class `KnownObjectTest.CombatSetters`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setToHRoundTrips()` | — | ✘ |
| `setToDRoundTrips()` | — | ✘ |
| `setToARoundTrips()` | — | ✘ |
| `areIndependent()` | — | ✔ |

**class `KnownObjectTest.Combat`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toH()` | — | ✘ |
| `toD()` | — | ✘ |
| `toA()` | — | ✘ |
| `areIndependent()` | — | ✔ |

**class `KnownObjectTest.Resistances`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `learnsOnce()` | — | ✘ |
| `elementsAreSeparate()` | — | ✘ |
| `sentinelsAreRefused()` | — | ✔ |
| `everyRealElementIsReachable()` | — | ✔ |

**class `KnownObjectTest.FlagsAndModifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagLearnedOnce()` | — | ✘ |
| `modifierLearnedOnce()` | — | ✘ |
| `flagsAndModifiersAreSeparate()` | — | ✔ |
| `getFlagsReportsLearned()` | — | ✘ |
| `getFlagsIsACopy()` | — | ✔ |

**class `KnownObjectTest.Brands`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `learnsOnce()` | — | ✘ |
| `learningOneStrengthLearnsTheGroup()` | — | ✔ |
| `directionDoesNotMatter()` | — | ✔ |
| `otherNamesAreUntouched()` | — | ✔ |
| `secondMemberIsNotNews()` | — | ✔ |
| `unregisteredBrandLearnsItsGroup()` | — | ✔ |
| `unknownNameLearnsNothing()` | — | ✔ |

**class `KnownObjectTest.Slays`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `learnsOnce()` | — | ✘ |
| `learningOneStrengthLearnsTheGroup()` | — | ✘ |
| `differentRaceFlagIsSeparate()` | — | ✘ |
| `baseSeparatesGroupsThatShareAName()` | — | ✔ |
| `basedSlayHasItsOwnGroup()` | — | ✔ |
| `secondMemberIsNotNews()` | — | ✘ |

**class `KnownObjectTest.Curses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `learnsOnce()` | — | ✘ |
| `cursesAreIndividual()` | — | ✘ |
| `unregisteredCurseIsNotKnown()` | — | ✔ |
| `unregisteredCurseCanBeLearned()` | — | ✔ |


##### `ObjectBaseTest.java`

**Responsible for:** Tests for ObjectBase, the port of C's struct object_base (object.h).

**class `ObjectBaseTest`** — class Javadoc: ✔ · type stamp: on 261009

- Fields (0/1 documented): ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hates(ElementEnum)` | — | ✘ |
| `base(Flag<ElementEnum>, Flag<ObjectKindFlag>)` | — | ✘ |
| `readFileAsC()` | — | ✔ |
| `constructorStoresEveryScalarFieldAsGiven()` | — | ✘ |
| `freshBaseHasZeroSvalsAndEmptyObjectFlags()` | — | ✘ |
| `setNumSvalsOverwritesTheCount()` | — | ✘ |
| `noHatedElementsGivesAnEmptyElementMap()` | — | ✘ |
| `eachHatedElementGetsAHatesOnlyEntryWithNeutralResistance()` | — | ✘ |
| `anElementThatIsNotHatedHasNoEntry()` | — | ✘ |
| `hatesFlagIsOnlyReadNotRetained()` | — | ✘ |
| `kindFlagsAreStoredByReferenceNotCopied()` | — | ✘ |
| `setElementInfoReplacesTheTableWithoutMerging()` | — | ✘ |
| `toStringNamesTheScalarFields()` | — | ✘ |
| `everyShippedBaseMatchesAnIndependentReadingOfTheFile()` | — | ✘ |
| `spotCheckedRecordsMatchTheFileByHand()` | — | ✘ |

**record `ObjectBaseTest.Expected`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/6 documented): ✘ `tval`, `name`, `colour`, `breakPerc`, `maxStack`, `hated`


##### `ObjectGearInvenCarryTest.java`

**Responsible for:** Tests ObjectGear.invenCarry - the port of C's inven_carry (obj-gear.c).

**class `ObjectGearInvenCarryTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (3/20 documented): ✔ `PACK_SIZE`, `MAX_STACK`, `potionKind` — ✘ `QUIVER_SLOTS`, `SLOT_SIZE`, `THROWN_MULT`, `QUIVER_SIZE`, `savedConstants`, `savedBodies`, `savedRaces`, `savedCurses`, `savedTimedEffects`, `savedGamePlayer`, `savedGameCave`, `body`, `player`, `level`, `known`, `realBus`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✘ |
| `restoreGlobals()` | — | ✘ |
| `plainRace(PlayerBody, Flag<PlayerFlag>)` | — | ✔ |
| `registryField(String)` | — | ✘ |
| `objectRegistryField(String)` | — | ✘ |
| `buildPlayer()` | — | ✔ |
| `tearDown()` | — | ✘ |
| `grantRaceFlag(PlayerFlag)` | — | ✔ |
| `potion(int)` | — | ✔ |
| `mergeableStack(int)` | — | ✔ |
| `listInLevel(ItemObject)` | — | ✘ |
| `listInKnown(ItemObject)` | — | ✘ |
| `gear()` | — | ✘ |

**class `ObjectGearInvenCarryTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `types`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `lastMessage()` | — | ✘ |

**class `ObjectGearInvenCarryTest.Insertion`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `itemJoinsGearAndWeightIsAdded()` | — | ✘ |
| `cavePlacementClearedAndNoticeRaised()` | — | ✘ |
| `redrawFlagIsLeftSet()` | — | ✘ |

**class `ObjectGearInvenCarryTest.Paranoia`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `exactlyAtCapacityIsAccepted()` | — | ✘ |
| `overCapacityIsRefused()` | — | ✘ |

**class `ObjectGearInvenCarryTest.Combining`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchingStackIsMergedNotInserted()` | — | ✘ |
| `equippedMatchIsNotCombinedInto()` | — | ✘ |
| `paranoiaGuardIsSkippedWhenCombining()` | — | ✘ |

**class `ObjectGearInvenCarryTest.FlavourAwareOnPickup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hobbitIdentifiesUnknownMushroomAndMessages()` | — | ✘ |
| `mushroomAlreadyAwareIsNotReannounced()` | — | ✘ |
| `gnomeIdentifiesUnknownZapperSilently()` | — | ✘ |
| `withoutTheRaceFlagNothingHappens()` | — | ✘ |

**class `ObjectGearInvenCarryTest.AggregateMessage`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singleStackReportsWithoutThrowing()` | — | ✘ |
| `likeStacksReportFirstMarker()` | — | ✘ |
| `wandReportsOwnCountOnly()` | — | ✘ |
| `rodReportsOwnCountOnly()` | — | ✘ |
| `timeoutReportsOwnCountOnly()` | — | ✘ |
| `silentWhenMessageOff()` | — | ✘ |

**class `ObjectGearInvenCarryTest.KnownHalf`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `knownCountFollowsMergedCount()` | — | ✘ |
| `mergePastMaxStackIsClamped()` | — | ✘ |
| `overflowingStackIsNotMerged()` | — | ✘ |


##### `ObjectGearPackTotalTest.java`

**Responsible for:** Tests the private ObjectGear.objectPackTotal - the port of C's object_pack_total (obj-gear.c).

**class `ObjectGearPackTotalTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (5/12 documented): ✔ `PACK_SIZE`, `QUIVER_SLOTS`, `MAX_STACK`, `savedCurses`, `kind` — ✘ `SLOT_SIZE`, `THROWN_MULT`, `savedPlayer`, `savedConstants`, `savedBodies`, `savedRaces`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✘ |
| `restoreGlobals()` | — | ✘ |
| `potion(int)` | — | ✔ |
| `testRace(PlayerBody)` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `buildPlayer()` | — | ✔ |
| `gear()` | — | ✔ |
| `quiver()` | — | ✔ |
| `inventory()` | — | ✔ |
| `equip(ItemObject)` | — | ✔ |
| `packTotal(ItemObject, boolean)` | — | ✔ |

**class `ObjectGearPackTotalTest.Aggregation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyGearFindsNothing()` | — | ✘ |
| `singleMatchIsTotalledAndFirst()` | — | ✘ |
| `everyMatchContributesToTheTotal()` | — | ✘ |

**class `ObjectGearPackTotalTest.SelfMatch`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unequippedTemplateCountsItself()` | — | ✘ |
| `equippedTemplateExcludesItself()` | — | ✘ |

**class `ObjectGearPackTotalTest.IgnoreInscription`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `differingInscriptionsMatchWhenIgnored()` | — | ✘ |
| `differingInscriptionsBlockWhenConsidered()` | — | ✘ |

**class `ObjectGearPackTotalTest.LabelTieBreak`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lowerLetterWinsRegardlessOfEncounterOrder()` | — | ✔ |
| `quiverDigitOutranksPackLetterFoundFirst()` | — | ✘ |
| `quiverDigitOutranksPackLetterFoundLater()` | — | ✘ |

**class `ObjectGearPackTotalTest.FirstReset`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstIsFoundWhenUnlabelled()` | — | ✘ |


##### `ObjectIgnoreApplyAutoinscriptionTest.java`

**Responsible for:** Tests ObjectIgnore#applyAutoinscription, the port of C's apply_autoinscription (obj-ignore.c:242-288).

**class `ObjectIgnoreApplyAutoinscriptionTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (3/3 documented): ✔ `bus`, `realBus`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(boolean, String, String)` | — | ✔ |
| `item(ObjectKind)` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `apply(ItemObject)` | — | ✔ |

**class `ObjectIgnoreApplyAutoinscriptionTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `ObjectIgnoreApplyAutoinscriptionTest.Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noConfiguredNoteDoesNothing()` | — | ✔ |
| `alreadyInscribedIsUntouched()` | — | ✔ |
| `uncarriedObjectIsLeftAlone()` | — | ✔ |

**class `ObjectIgnoreApplyAutoinscriptionTest.Ignored`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ignoredItemIsNotInscribed()` | — | ✔ |
| `unignoringStillInscribesAnIgnoredItem()` | — | ✔ |

**class `ObjectIgnoreApplyAutoinscriptionTest.ClearOldNote`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `staleUnawareNoteIsClearedAndReplaced()` | — | ✔ |
| `customNoteThatWasNeverUnawareSurvives()` | — | ✔ |
| `unawareItemNeverClears()` | — | ✔ |

**class `ObjectIgnoreApplyAutoinscriptionTest.RuneOrdering`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `savedRunes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveRunes()` | — | ✘ |
| `restoreRunes()` | — | ✘ |
| `runeNoteAppliedDespiteNoKindNote()` | — | ✔ |
| `modRune(ObjectModifier, String)` | — | ✔ |

**class `ObjectIgnoreApplyAutoinscriptionTest.Success`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `carriedItemIsInscribed()` | — | ✔ |
| `emptyNoteStillReportsAndReturnsOne()` | — | ✔ |
| `emptyAwareNoteBehavesTheSame()` | — | ✔ |


##### `ObjectIgnoreAutoinscribeGroundTest.java`

**Responsible for:** Tests ObjectIgnore#autoinscribeGround, the port of C's autoinscribe_ground (obj-ignore.c:340-347).

**class `ObjectIgnoreAutoinscribeGroundTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (6/6 documented): ✔ `bus`, `realBus`, `player`, `level`, `savedCave`, `savedRunes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(String)` | — | ✔ |
| `item(ObjectKind)` | — | ✔ |
| `modRune(ObjectModifier, String)` | — | ✔ |
| `ringWith(ObjectModifier)` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `doesNothingOnAnEmptySquare()` | — | ✘ |

**class `ObjectIgnoreAutoinscribeGroundTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `ObjectIgnoreAutoinscribeGroundTest.Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aKindNoteIsNeverAppliedToAFloorItem()` | — | ✔ |

**class `ObjectIgnoreAutoinscribeGroundTest.Success`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRuneNoteReachesASingleFloorItem()` | — | ✔ |
| `everyItemOnTheFloorIsVisited()` | — | ✔ |

**class `ObjectIgnoreAutoinscribeGroundTest.Bounds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doesNothingWhenTheGridIsOutOfBounds()` | — | ✔ |


##### `ObjectIgnoreAutoinscribePackTest.java`

**Responsible for:** Tests ObjectIgnore#autoinscribePack, the port of C's autoinscribe_pack (obj-ignore.c:352-359).

**class `ObjectIgnoreAutoinscribePackTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (3/3 documented): ✔ `bus`, `realBus`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kind(String)` | — | ✔ |
| `item(ObjectKind)` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `doesNothingOnAnEmptyGear()` | — | ✘ |
| `inscribesASingleDueItem()` | — | ✘ |
| `inscribesEveryDueItemInTheGear()` | — | ✘ |
| `alreadyInscribedItemDoesNotStopTheNextItem()` | — | ✘ |
| `itemWithNoConfiguredNoteLeavesNothingBehind()` | — | ✘ |

**class `ObjectIgnoreAutoinscribePackTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `ObjectIgnoreBirthInitTest.java`

**Responsible for:** Tests ObjectIgnore#ignoreBirthInit — the port of C's ignore_birth_init (obj-ignore.c:143-159).

**class `ObjectIgnoreBirthInitTest`** — class Javadoc: ✔ · type stamp: on 260908

- Fields (0/3 documented): ✘ `savedKinds`, `savedEgos`, `savedIgnoreLevels`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rawEgoItems()` | — | ✔ |
| `restoreRawEgoItems(List<EgoItem>)` | — | ✔ |
| `rawObjectKinds()` | — | ✔ |
| `restoreRawObjectKinds(List<ObjectKind>)` | — | ✔ |
| `bareEgo()` | — | ✔ |
| `saveState()` | — | ✘ |
| `restoreState()` | — | ✘ |
| `wipesIgnoreFlagsOnEveryKind()` | — | ✔ |
| `resetsIgnoreLevelExceptSentinel()` | — | ✔ |
| `clearsEgoIgnoreTypesOnEveryEgo()` | — | ✔ |
| `leavesEgoSentinelSlotUntouched()` | — | ✔ |
| `resetsIgnoreLevelWithEmptyTables()` | — | ✔ |


##### `ObjectIgnoreGetAutoinscriptionTest.java`

**Responsible for:** Tests ObjectIgnore#getAutoinscription, the port of C's get_autoinscription (obj-ignore.c:229).

**class `ObjectIgnoreGetAutoinscriptionTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getAutoinscription(ObjectKind, boolean)` | — | ✔ |

**class `ObjectIgnoreGetAutoinscriptionTest.NullKind`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullKindAwareIsNull()` | — | ✘ |
| `nullKindUnawareIsNull()` | — | ✘ |

**class `ObjectIgnoreGetAutoinscriptionTest.RealKind`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `awareReturnsAwareNote()` | — | ✔ |
| `unawareReturnsUnawareNote()` | — | ✔ |
| `awareDoesNotFallBackToUnaware()` | — | ✔ |
| `unawareDoesNotFallBackToAware()` | — | ✔ |
| `bareKindAnswersNullBothWays()` | — | ✔ |


##### `ObjectIgnoreRunesAutoinscribeTest.java`

**Responsible for:** Tests ObjectIgnore#runesAutoinscribe and ObjectIgnore#runeAddAutoinscription, the ports of C's runes_autoinscribe and rune_add_autoinscription (obj-ignore.c:217-224 and obj-ignore.c:172-188).

**class `ObjectIgnoreRunesAutoinscribeTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (1/1 documented): ✔ `savedRunes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `runesAutoinscribe(Player, ItemObject)` | — | ✔ |
| `runeAddAutoinscription(ItemObject, Rune)` | — | ✔ |
| `item(Map<ObjectModifier,Integer>)` | — | ✔ |
| `player(KnownObject)` | — | ✔ |
| `modRune(ObjectModifier, String)` | — | ✔ |
| `saveRunes()` | — | ✘ |
| `restoreRunes()` | — | ✘ |

**class `ObjectIgnoreRunesAutoinscribeTest.Guard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `knownRuneOnItsOwnIsNotEnough()` | — | ✔ |
| `unknownToTheObjectIsNotEnough()` | — | ✔ |
| `knownAndCarriedIsInscribed()` | — | ✔ |
| `multipleQualifyingRunesAccumulate()` | — | ✔ |

**class `ObjectIgnoreRunesAutoinscribeTest.Note`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noRuneNoteLeavesUnsetNoteUnset()` | — | ✔ |
| `noRuneNoteLeavesExistingNoteUntouched()` | — | ✔ |
| `unsetNoteBecomesTheRuneNote()` | — | ✔ |
| `existingNoteHasRuneNoteAppended()` | — | ✔ |
| `alreadyPresentNoteIsNotDuplicated()` | — | ✔ |


##### `ObjectIgnoreTest.java`

**Responsible for:** Tests ObjectIgnore — the ignore decision, and the one action it takes.

**class `ObjectIgnoreTest`** — class Javadoc: ✔ · type stamp: —

- Fields (6/6 documented): ✔ `player`, `level`, `savedPlayer`, `savedCave`, `savedQueue`, `savedIgnoreLevels`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `feature(TerrainFeatureFlags)` | — | ✔ |
| `answering(boolean)` | — | ✔ |
| `potion()` | — | ✔ |
| `artifact()` | — | ✔ |
| `markIgnored(ItemObject)` | — | ✔ |
| `seedWorld()` | — | ✔ |
| `restoreWorld()` | — | ✔ |
| `queuedCommand()` | — | ✔ |

**class `ObjectIgnoreTest.ClosingFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyPackStillRaisesBothFlags()` | — | ✔ |

**class `ObjectIgnoreTest.Decision`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unmarkedPackIsUntouched()` | — | ✔ |
| `markedItemIsDropped()` | — | ✔ |
| `dropIsABackgroundCommand()` | — | ✔ |
| `unknownObjectIsNeverIgnored()` | — | ✔ |
| `kindIgnoredWhileUnawareIsDropped()` | — | ✔ |
| `awareChoiceDoesNotCoverAnUnawareItem()` | — | ✔ |
| `inscriptionProtectsAgainstTheKindChoice()` | — | ✔ |
| `blanketInscriptionProtectsToo()` | — | ✔ |
| `artifactEscapesTheSettings()` | — | ✔ |
| `markedArtifactIsIgnored()` | — | ✔ |
| `itemWithNoQualityCategoryIsSafe()` | — | ✔ |
| `assessedItemInAnIgnoreAllCategoryIsDropped()` | — | ✔ |
| `assessedItemIsKeptWhenTheCategoryIsUnset()` | — | ✔ |

**class `ObjectIgnoreTest.Blocks`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unignoringPlayerDropsNothing()` | — | ✔ |
| `nothingIsDroppedInAShop()` | — | ✔ |
| `noDropInscriptionKeepsTheItem()` | — | ✔ |

**class `ObjectIgnoreTest.Equipped`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wield(ItemObject)` | — | ✔ |
| `agreedDropGoesAhead()` | — | ✔ |
| `refusalInscribesNoDrop()` | — | ✔ |
| `refusalAppendsToAnExistingNote()` | — | ✔ |

**class `ObjectIgnoreTest.KnownItem`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `alwaysAnswersFalse()` | — | ✔ |
| `evenForAMarkedItem()` | — | ✔ |

**class `ObjectIgnoreTest.KindIsIgnoredUnaware`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `trueOnceUnawareFlagSet()` | — | ✘ |
| `falseOnAFreshKind()` | — | ✘ |
| `unmovedByTheAwareFlagAlone()` | — | ✘ |

**class `ObjectIgnoreTest.KindIgnoreWhenAware`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setsTheAwareIgnoreFlagOnAFreshKind()` | — | ✘ |
| `leavesTheUnawareIgnoreFlagUntouched()` | — | ✘ |
| `raisesPnIgnoreOnThePlayersUpkeep()` | — | ✘ |
| `isANoOpOnTheFlagWhenAlreadySet()` | — | ✘ |
| `stillRaisesPnIgnoreWhenTheFlagWasAlreadySet()` | — | ✘ |


##### `ObjectInfoTest.java`

**Responsible for:** Tests ObjectIgnoreTables - the Java home of quality_mapping[] and ignore_level[] from obj-ignore.c.

**class `ObjectInfoTest`** — class Javadoc: ✔ · type stamp: on 261010

- Fields (1/1 documented): ✔ `C_ROWS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ignoreTypeOf(TValue, String)` | — | ✔ |

**class `ObjectInfoTest.Rows`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rowCount()` | — | ✘ |
| `rowsMatchC()` | — | ✘ |
| `identifierRowsComeFirst()` | — | ✘ |
| `oneCatchAllPerTval()` | — | ✘ |
| `recordIsAValue()` | — | ✘ |

**class `ObjectInfoTest.Lookups`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dragonScaleMails()` | — | ✘ |
| `chaosByTval()` | — | ✘ |
| `cloaks()` | — | ✘ |
| `softArmour()` | — | ✘ |
| `caseSensitiveSubstring()` | — | ✘ |
| `noMatch()` | — | ✘ |
| `sharedAndPlainRows()` | — | ✘ |

**class `ObjectInfoTest.IgnoreLevel`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startsAtNone()` | — | ✘ |
| `oneSlotPerType()` | — | ✘ |
| `noneIsLowest()` | — | ✘ |


##### `ObjectKindAccessorsTest.java`

**Responsible for:** Tests ObjectKind's accessors — the template every object of a type is built from, and the port of C's object_kind (object.h).

**class `ObjectKindAccessorsTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `kind`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newKind()` | — | ✔ |

**class `ObjectKindAccessorsTest.Allocation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `allocationFiguresAreDistinct()` | — | ✔ |
| `kindIndexIsIndependent()` | — | ✔ |
| `costRoundTrips()` | — | ✔ |
| `intField(String)` | — | ✔ |

**class `ObjectKindAccessorsTest.CombatDice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareKindHasNoDice()` | — | ✔ |
| `diceAreDistinct()` | — | ✔ |
| `setDice(String, Random)` | — | ✔ |

**class `ObjectKindAccessorsTest.DamageDice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `damageDefaultsToOne()` | — | ✔ |
| `countAndSidesAreSeparate()` | — | ✔ |
| `setInt(String, int)` | — | ✔ |

**class `ObjectKindAccessorsTest.Presentation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `characterStored()` | — | ✔ |
| `bareKindHasNoFlavour()` | — | ✔ |
| `bareKindHasFlavourIsFalse()` | — | ✔ |
| `flavouredKindHasFlavourIsTrue()` | — | ✔ |
| `setFlavourStoresGiven()` | — | ✔ |
| `setFlavourNullClears()` | — | ✔ |
| `startsUnseen()` | — | ✔ |
| `everSeenStored()` | — | ✔ |
| `timeStored()` | — | ✔ |
| `startsUntried()` | — | ✔ |
| `triedStored()` | — | ✔ |
| `triedField()` | — | ✔ |
| `bareKindHasNoNotes()` | — | ✔ |
| `notesAreIndependent()` | — | ✔ |
| `unawareNoteAloneLeavesAwareNull()` | — | ✔ |

**class `ObjectKindAccessorsTest.WeightTimeChargeAndModifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareKindHasNoWeight()` | — | ✔ |
| `weightRoundTrips()` | — | ✔ |
| `timeRoundTrips()` | — | ✔ |
| `bareKindHasNoCharge()` | — | ✔ |
| `chargeStored()` | — | ✔ |
| `modifierPresentReturnsItsDice()` | — | ✔ |
| `modifierAbsentFallsBackToZeroDice()` | — | ✔ |

**class `ObjectKindAccessorsTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `halfBuiltKindCannotBeCopied()` | — | ✔ |
| `fullyBuiltKindCopies()` | — | ✔ |
| `fillDice(ObjectKind)` | — | ✔ |

**class `ObjectKindAccessorsTest.SlaysBrandsAndCurses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareKindHasEmptyCollections()` | — | ✔ |
| `getSlaysReadsBackStoredSet()` | — | ✔ |
| `getBrandsReadsBackStoredSet()` | — | ✔ |
| `getCursesReadsBackStoredMap()` | — | ✔ |

**class `ObjectKindAccessorsTest.WipeIgnoreFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wipingWithNoFlagsSetLeavesBothOff()` | — | ✔ |
| `wipingClearsLoneAwareFlag()` | — | ✔ |
| `wipingClearsBothFlags()` | — | ✔ |


##### `ObjectKindCanBrowseTest.java`

**Responsible for:** Tests ObjectKind#canBrowse(), the port of C's obj_kind_can_browse (obj-util.c).

**class `ObjectKindCanBrowseTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `savedPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `book(TValue, int)` | — | ✔ |
| `playAsClassWith(List<MagicBook>)` | — | ✔ |
| `kind(TValue, int)` | — | ✔ |
| `rememberPlayer()` | — | ✘ |
| `restorePlayer()` | — | ✘ |
| `matchOnBothHalves()` | — | ✘ |
| `wrongSval()` | — | ✘ |
| `wrongTval()` | — | ✘ |
| `matchOnLaterBook()` | — | ✘ |
| `emptyBookList()` | — | ✘ |
| `noMagic()` | — | ✘ |


##### `ObjectKindConstructorsTest.java`

**Responsible for:** Tests what ObjectKind's constructors set up, and what ObjectKind#copy carries across, against the C routines each one follows.

**class `ObjectKindConstructorsTest`** — class Javadoc: ✔ · type stamp: on 261008

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `base()` | — | ✔ |
| `book(ObjectBase, boolean)` | — | ✔ |
| `artifactKind(ObjectBase)` | — | ✔ |
| `longKind(String, Flag<ObjectKindFlag>)` | — | ✔ |

**class `ObjectKindConstructorsTest.PartlySpecified`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaults()` | — | ✔ |
| `diceAreZero()` | — | ✔ |
| `inheritsBaseKindFlags()` | — | ✔ |
| `kindFlagsAreItsOwn()` | — | ✔ |
| `nullBase()` | — | ✔ |
| `ordinaryBook()` | — | ✔ |
| `dungeonBook()` | — | ✔ |
| `dungeonBookKeepsInheritedFlags()` | — | ✔ |
| `ignoreStartsClear()` | — | ✔ |

**class `ObjectKindConstructorsTest.ArtifactConstructor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `naming()` | — | ✔ |
| `tvalAndBase()` | — | ✔ |
| `defaultGlyph()` | — | ✔ |
| `kindFlags()` | — | ✔ |
| `objectFlags()` | — | ✔ |
| `elementInfo()` | — | ✔ |
| `zeroed()` | — | ✔ |
| `ignoreStartsClear()` | — | ✔ |

**class `ObjectKindConstructorsTest.LongConstructor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `subTypeNameIsStripped()` | — | ✔ |
| `kindFlagsAsGiven()` | — | ✔ |
| `notRegisteredNotSpecial()` | — | ✔ |

**class `ObjectKindConstructorsTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `keepsArtifactMark()` | — | ✔ |
| `sharesBase()` | — | ✔ |
| `ignoreIsIndependent()` | — | ✔ |
| `elementInfoIsIndependent()` | — | ✔ |
| `awarenessIsASnapshot()` | — | ✔ |


##### `ObjectKindTest.java`

**Responsible for:** Tests the parts of ObjectKind that do work rather than assign — the curse copying in the long constructor, the special-artifact marker, and the two ignore settings.

**class `ObjectKindTest`** — class Javadoc: ✔ · type stamp: on 260817

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cursesOf(ObjectKind)` | — | ✔ |
| `kindWith(Map<Curse,CurseData>)` | — | ✔ |
| `artifactKind()` | — | ✔ |
| `base()` | — | ✔ |
| `curse(String)` | — | ✔ |

**class `ObjectKindTest.Curses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curseDataIsCopied()` | — | ✔ |
| `curseMapIsCopied()` | — | ✔ |

**class `ObjectKindTest.SpecialArtifact`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onlyArtifactKindsAreSpecial()` | — | ✔ |
| `artifactKindIsInstaArt()` | — | ✔ |

**class `ObjectKindTest.Ignore`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `settingsAreIndependent()` | — | ✔ |
| `settingsCanBeCleared()` | — | ✔ |
| `everyConstructorInitialisesIgnore()` | — | ✔ |
| `hasIgnoreFlagAgreesWithFlagSpecificAccessors()` | — | ✔ |
| `setIgnoreFlagOnlyTurnsItsOwnFlagOn()` | — | ✔ |


##### `ObjectKnowledgeTest.java`

**Responsible for:** Tests ObjectKnowledge#objectHasRune, the port of C's object_has_rune (obj-knowledge.c), and ObjectKnowledge#objectFlavourTried, the port of C's object_flavor_tried (obj-knowledge.c).

**class `ObjectKnowledgeTest`** — class Javadoc: ✔ · type stamp: on 260905

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `poke(ObjectKind, String, Object)` | — | ✔ |
| `peek(ObjectKind, String)` | — | ✔ |
| `kindWithFixedToH(int)` | — | ✔ |
| `item(TValue, ObjectKind)` | — | ✔ |
| `curse(String)` | — | ✔ |

**class `ObjectKnowledgeTest.CombatToAAndToD`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toArmourNonzero()` | — | ✘ |
| `toArmourNegative()` | — | ✘ |
| `toDamageNonzero()` | — | ✘ |
| `kindZero()` | — | ✘ |

**class `ObjectKnowledgeTest.CombatToH`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `toHitRune`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bodyArmourAtKindPenaltyHasNoRune()` | — | ✔ |
| `bodyArmourAwayFromKindPenaltyHasRune()` | — | ✘ |
| `nonArmourAtZeroHasNoRune()` | — | ✘ |
| `nonArmourNonzeroHasRune()` | — | ✘ |
| `noKindHasNoRuneWhateverTheFigure()` | — | ✔ |

**class `ObjectKnowledgeTest.ModifierRune`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentWhenUnset()` | — | ✘ |
| `presentWhenSet()` | — | ✘ |
| `checksTheNamedModifierOnly()` | — | ✘ |

**class `ObjectKnowledgeTest.ResistanceRune`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentAtZero()` | — | ✘ |
| `presentWhenResisted()` | — | ✘ |
| `presentWhenVulnerable()` | — | ✘ |
| `checksTheNamedElementOnly()` | — | ✘ |
| `absentWhenNoEntryForElement()` | — | ✘ |
| `absentWhenOnlyOtherElementHasEntry()` | — | ✘ |
| `absentWhenMapNeverAllocated()` | — | ✘ |

**class `ObjectKnowledgeTest.BrandRune`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentWithNoBrands()` | — | ✘ |
| `presentByNameNotIdentity()` | — | ✘ |
| `absentWhenNamesDiffer()` | — | ✘ |

**class `ObjectKnowledgeTest.SlayRune`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `slay(String, String, MonsterRaceFlag)` | — | ✘ |
| `absentWithNoSlays()` | — | ✘ |
| `presentBySameMonstersSlain()` | — | ✘ |
| `absentWhenMonstersDiffer()` | — | ✘ |

**class `ObjectKnowledgeTest.CurseRune`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentWithNoCurses()` | — | ✘ |
| `presentAtNonzeroPower()` | — | ✘ |
| `absentAtZeroPower()` | — | ✘ |
| `absentForADifferentCurseOfTheSameName()` | — | ✘ |

**class `ObjectKnowledgeTest.FlagRune`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentWhenUnset()` | — | ✘ |
| `presentWhenSet()` | — | ✘ |
| `checksTheNamedFlagOnly()` | — | ✘ |

**class `ObjectKnowledgeTest.ObjectFlavourTried`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `marksUntriedOrdinaryKindAsTried()` | — | ✘ |
| `leavesAlreadyTriedOrdinaryKindTried()` | — | ✘ |
| `doesNotMarkSpecialArtifactKindAsTried()` | — | ✘ |
| `doesNothingToAnAlreadyTriedSpecialArtifactKind()` | — | ✘ |
| `doesNotThrowForANullObject()` | — | ✘ |
| `doesNotThrowForAnObjectWithNoKind()` | — | ✘ |


##### `ObjectPropertyAccessorsTest.java`

**Responsible for:** Tests ObjectProperty's pricing accessors, and the multipliers on Brand and Slay that sit beside them.

**class `ObjectPropertyAccessorsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `property(int, int, Map<TValue,Integer>)` | — | ✔ |

**class `ObjectPropertyAccessorsTest.TypeMultiplier`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `namedTypeUsesItsFigure()` | — | ✔ |
| `unnamedTypeDefaultsToOne()` | — | ✔ |
| `noMultipliersIsNeutral()` | — | ✔ |
| `curseTypeUsesTheDefault()` | — | ✔ |

**class `ObjectPropertyAccessorsTest.StoredFigures`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `powerAndMultiplierAreDistinct()` | — | ✔ |
| `noticeMessage()` | — | ✔ |
| `payloadIdentifiesTheProperty()` | — | ✔ |

**class `ObjectPropertyAccessorsTest.RealData`** — class Javadoc: ✔ · type stamp: on 261005

- Fields (0/1 documented): ✘ `all`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load()` | — | ✔ |
| `named(String)` | — | ✔ |
| `extraBlows()` | — | ✔ |
| `strength()` | — | ✔ |
| `freeAction()` | — | ✔ |
| `multiplyWeight()` | — | ✔ |
| `sustainStrengthBinding()` | — | ✔ |
| `everyRecordIsComplete()` | — | ✔ |

**class `ObjectPropertyAccessorsTest.Bindings`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bindingCarriesItsParts()` | — | ✔ |
| `bindingsCompareByComponents()` | — | ✔ |

**class `ObjectPropertyAccessorsTest.Multipliers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `brandMultiplier()` | — | ✔ |
| `slayMultiplier()` | — | ✔ |


##### `ObjectPropertyStructFidelityTest.java`

**Responsible for:** Checks that ObjectProperty holds, field for field, what C's struct obj_property (obj-properties.h) holds after finish_parse_object_property (obj-init.c).

**class `ObjectPropertyStructFidelityTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (0/1 documented): ✘ `DATA`

**record `ObjectPropertyStructFidelityTest.Expected`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (5/5 documented): ✔ `name`, `power`, `mult`, `typeMults`, `msg`
- Fields (0/2 documented): ✘ `loaded`, `expected`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `load()` | — | ✔ |
| `readExpected()` | — | ✔ |
| `named(String)` | — | ✔ |
| `namesFollowTheFile()` | — | ✔ |
| `scalarsFollowTheFile()` | — | ✔ |
| `typeMultsFollowTheFile()` | — | ✔ |
| `negativePower()` | — | ✔ |
| `elementRelation()` | — | ✔ |
| `payloadKindFollowsType()` | — | ✔ |
| `bindingsFollowTheFile()` | — | ✔ |


##### `ObjectPropertyTypeWrapperTest.java`

**Responsible for:** Tests ObjectPropertyTypeWrapper, the typed stand-in for C's obj_property->index.

**class `ObjectPropertyTypeWrapperTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/7 documented): ✘ `STAT`, `MOD`, `FLAG`, `IGNORE`, `RESIST`, `VULN`, `IMM`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `property(ObjPropertyType, ObjectPropertyTypeWrapper)` | — | ✔ |

**class `ObjectPropertyTypeWrapperTest.Constructors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `statAndModStoreTheModifier()` | — | ✔ |
| `flagStoresTheFlag()` | — | ✔ |
| `eachElementRelationStoresTheElement()` | — | ✔ |
| `modifierConstructorRefusesEveryOtherType()` | — | ✔ |
| `flagConstructorRefusesEveryOtherType()` | — | ✔ |
| `elementConstructorRefusesEveryOtherType()` | — | ✔ |

**class `ObjectPropertyTypeWrapperTest.Getters`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `modifierGetterAcceptsStatAndModOnEitherWrapper()` | — | ✔ |
| `elementGetterAcceptsAnyElementRelation()` | — | ✔ |
| `gettersRefuseTypesOutsideTheirFamily()` | — | ✔ |
| `gettersAnswerNullForAnotherKindOfPayload()` | — | ✔ |

**class `ObjectPropertyTypeWrapperTest.Equality`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sameModifierEqualsWhetherTaggedStatOrMod()` | — | ✔ |
| `sameElementEqualsAcrossRelationsAndDifferentElementsDiffer()` | — | ✔ |
| `flagsCompareByFlag()` | — | ✔ |
| `differentFamiliesAreNeverEqual()` | — | ✔ |
| `reflexiveAndUnequalToNullAndForeignObjects()` | — | ✔ |

**class `ObjectPropertyTypeWrapperTest.Lookup`** — class Javadoc: ✔ · type stamp: —

- Fields (0/6 documented): ✘ `saved`, `strStat`, `stealthMod`, `sustStr`, `resistFire`, `immFire`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadProperties()` | — | ✘ |
| `restoreProperties()` | — | ✘ |
| `statIsFoundAsStatAndAsMod()` | — | ✔ |
| `modIsNotFoundAsStat()` | — | ✔ |
| `sameElementResolvesByRelation()` | — | ✔ |
| `flagFoundAndMissingFlagIsNull()` | — | ✔ |


##### `ObjectRandartArtifactSetDataNewTest.java`

**Responsible for:** Tests ObjectRandart#artifactSetDataNew() and the ArtifactSetData constructor it wraps — the port of C's artifact_set_data_new() (obj-randart.c).

**class `ObjectRandartArtifactSetDataNewTest`** — class Javadoc: ✔ · type stamp: on 260908

- Fields (1/1 documented): ✔ `savedArtifacts`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(Class<?>, String)` | — | ✔ |
| `newData()` | — | ✔ |
| `artifact(String)` | — | ✔ |
| `saveArtifacts()` | — | ✔ |
| `restoreArtifacts()` | — | ✔ |
| `startAndIncrementValuesMatchC()` | — | ✔ |
| `countersStartAtZero()` | — | ✔ |
| `artProbsIsFullyZeroed()` | — | ✔ |
| `unloadedRegistryIsACallerError()` | — | ✔ |
| `callsAreIndependent()` | — | ✔ |

**class `ObjectRandartArtifactSetDataNewTest.TvalKeyedMaps`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyTvalIsZeroed()` | — | ✔ |

**class `ObjectRandartArtifactSetDataNewTest.ArtifactKeyedMaps`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyArtifactIsZeroed()` | — | ✔ |
| `noArtifactsLoadedGivesEmptyMaps()` | — | ✔ |


##### `ObjectUtilsArtifactMarkersTest.java`

**Responsible for:** Tests ObjectUtils#markArtifactCreated and ObjectUtils#markArtifactSeen, the ports of C's mark_artifact_created and mark_artifact_seen (obj-util.c).

**class `ObjectUtilsArtifactMarkersTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `artifact()` | — | ✔ |
| `markingFreshArtifactDoesNotThrow()` | — | ✔ |
| `markArtifactCreatedTrue()` | — | ✔ |
| `markArtifactCreatedFalseOverwrites()` | — | ✔ |
| `markArtifactSeenTrue()` | — | ✔ |
| `markArtifactSeenFalseOverwrites()` | — | ✔ |
| `createdAndSeenAreIndependent()` | — | ✔ |


##### `ObjectUtilsCopyBrandsTest.java`

**Responsible for:** Tests ObjectUtils#copyBrands, the port of C's copy_brands (obj-slays.c).

**class `ObjectUtilsCopyBrandsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copyBrands(Set<Brand>, Set<Brand>)` | — | ✔ |
| `brand(String, String, int)` | — | ✔ |

**class `ObjectUtilsCopyBrandsTest.Adding`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addsToEmptyDest()` | — | ✔ |
| `distinctElementsCoexist()` | — | ✔ |

**class `ObjectUtilsCopyBrandsTest.SameName`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `strongerSourceReplacesWeakerDest()` | — | ✔ |
| `survivorIsACopyOfSource()` | — | ✔ |
| `weakerSourceLeavesStrongerDestAlone()` | — | ✔ |

**class `ObjectUtilsCopyBrandsTest.EmptySource`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leavesDestUnchanged()` | — | ✔ |


##### `ObjectUtilsCopyCursesTest.java`

**Responsible for:** Tests ObjectUtils#copyCurses, the port of C's copy_curses (obj-curse.c).

**class `ObjectUtilsCopyCursesTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `savedConstants`, `savedWorlds`, `savedCurses`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedDepthTables()` | — | ✔ |
| `restoreDepthTables()` | — | ✔ |
| `register(Curse)` | — | ✔ |
| `curseWithFixedTimeout(String, int)` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `curseWithFixedTimeout(String, int, int)` | — | ✔ |
| `emptyRegistry()` | — | ✔ |
| `rawCurses(ItemObject)` | — | ✔ |

**class `ObjectUtilsCopyCursesTest.Merging`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addsNewCurse()` | — | ✔ |
| `overwritesRegardlessOfWhichIsStronger()` | — | ✔ |
| `leavesUnnamedCurseAlone()` | — | ✔ |

**class `ObjectUtilsCopyCursesTest.RegistryWalk`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `powerZeroIsSkipped()` | — | ✔ |
| `addedInRegistryOrder()` | — | ✔ |
| `lowerIndexListsFirst()` | — | ✔ |
| `unregisteredCurseIsDropped()` | — | ✔ |

**class `ObjectUtilsCopyCursesTest.NullSource`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leavesDestUntouched()` | — | ✔ |

**class `ObjectUtilsCopyCursesTest.NeverInitialised`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doesNotThrowAndApplies()` | — | ✔ |
| `emptySourceDoesNotThrow()` | — | ✔ |


##### `ObjectUtilsCopySlaysTest.java`

**Responsible for:** Tests ObjectUtils#copySlays, the port of C's copy_slays (obj-slays.c).

**class `ObjectUtilsCopySlaysTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `EVIL`, `ORC`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copySlays(Set<Slay>, Set<Slay>)` | — | ✔ |
| `slay(String, String, MonsterRaceFlag, int)` | — | ✔ |

**class `ObjectUtilsCopySlaysTest.Adding`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addsToEmptyDest()` | — | ✔ |
| `distinctGroupsCoexist()` | — | ✔ |

**class `ObjectUtilsCopySlaysTest.SameGroup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `strongerSourceReplacesWeakerDest()` | — | ✔ |
| `weakerSourceLeavesStrongerDestAlone()` | — | ✔ |

**class `ObjectUtilsCopySlaysTest.EmptySource`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leavesDestUnchanged()` | — | ✔ |


##### `ObjectUtilsCurseObjectFlagIsKnownTest.java`

**Responsible for:** Tests ObjectUtils#curseObjectFlagIsKnown, the curse-shaped counterpart to ObjectUtils#objectFlagIsKnown and, through it, of C's object_flag_is_known (obj-knowledge.c).

**class `ObjectUtilsCurseObjectFlagIsKnownTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(int)` | — | ✔ |
| `fullyKnownCurse()` | — | ✔ |
| `notFullyKnownCurse()` | — | ✔ |
| `playerWithItemKnowledge()` | — | ✘ |

**class `ObjectUtilsCurseObjectFlagIsKnownTest.FullyKnownCurse`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnownRegardlessOfTheOtherTwoChecks()` | — | ✘ |

**class `ObjectUtilsCurseObjectFlagIsKnownTest.PlayerKnowsTheFlag`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `ObjectUtilsCurseObjectFlagIsKnownTest.ShadowKnowsTheFlag`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `ObjectUtilsCurseObjectFlagIsKnownTest.NeitherKnowsTheFlag`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsUnknown()` | — | ✘ |
| `checksOnlyTheNamedFlag()` | — | ✘ |


##### `ObjectUtilsFlavourAssignFixedTest.java`

**Responsible for:** Tests ObjectUtils#flavourAssignFixed, the port of C's flavor_assign_fixed (obj-util.c).

**class `ObjectUtilsFlavourAssignFixedTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `savedObjectKinds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `objectKindsField()` | — | ✘ |
| `flavourAssignFixed()` | — | ✔ |
| `resolvedFixedFlavour(String, int, int)` | — | ✔ |
| `unresolvedRandomFlavour(String, int)` | — | ✔ |
| `objectKind(TValue, int)` | — | ✘ |
| `isolate()` | — | ✘ |
| `restore()` | — | ✘ |

**class `ObjectUtilsFlavourAssignFixedTest.OrdinaryFixedFlavour`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `boundOntoMatchingKind()` | — | ✔ |
| `leavesNonMatchingKindsUntouched()` | — | ✔ |
| `overwritesExistingFlavour()` | — | ✔ |
| `bindsEveryMatchingKind()` | — | ✔ |

**class `ObjectUtilsFlavourAssignFixedTest.UnresolvedRandomFlavour`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `skippedRegardlessOfMatchingKind()` | — | ✘ |

**class `ObjectUtilsFlavourAssignFixedTest.MultipleKinds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyGroupIsWalked()` | — | ✘ |


##### `ObjectUtilsFlavourAssignRandomTest.java`

**Responsible for:** Tests ObjectUtils#flavourAssignRandom, the port of C's flavor_assign_random (obj-util.c).

**class `ObjectUtilsFlavourAssignRandomTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `savedObjectKinds`, `savedScrollAdj`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `objectKindsField()` | — | ✘ |
| `scrollAdjField()` | — | ✘ |
| `flavourAssignRandom(TValue)` | — | ✔ |
| `unresolvedRandomFlavour(String, int)` | — | ✔ |
| `resolvedFixedFlavour(String, int, int)` | — | ✔ |
| `objectKind(TValue, int)` | — | ✘ |
| `isolate()` | — | ✘ |
| `restore()` | — | ✘ |

**class `ObjectUtilsFlavourAssignRandomTest.OrdinaryPath`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bindsTheOnlyCandidate()` | — | ✘ |

**class `ObjectUtilsFlavourAssignRandomTest.OuterGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leavesWrongTvalKindUntouched()` | — | ✘ |
| `leavesAlreadyFlavouredKindUntouched()` | — | ✘ |

**class `ObjectUtilsFlavourAssignRandomTest.FixedFlavourExclusion`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `skippedFromCountAndDraw()` | — | ✘ |

**class `ObjectUtilsFlavourAssignRandomTest.ShrinkingPool`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyCandidateClaimedExactlyOnce()` | — | ✘ |

**class `ObjectUtilsFlavourAssignRandomTest.ScrollText`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `overwritesTextFromScrollAdj()` | — | ✘ |
| `leavesNonScrollTextAlone()` | — | ✘ |

**class `ObjectUtilsFlavourAssignRandomTest.MultipleFlavourKindGroups`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onlyMatchingGroupContributes()` | — | ✘ |


##### `ObjectUtilsFlavourInitTest.java`

**Responsible for:** Tests ObjectUtils#flavourInit(), the port of C's flavor_init (obj-util.c).

**class `ObjectUtilsFlavourInitTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/4 documented): ✘ `savedPlayer`, `savedTurn`, `savedObjectKinds`, `savedScrollAdj`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `objectKindsField()` | — | ✘ |
| `scrollAdjField()` | — | ✘ |
| `saveGameState()` | — | ✘ |
| `restoreGameState()` | — | ✘ |
| `playerWithOptions(PlayerOptions)` | — | ✘ |
| `switchOn(PlayerOptions, PlayerOptionEnum)` | — | ✘ |
| `unresolvedRandomFlavour(String, int)` | — | ✔ |
| `resolvedFixedFlavour(String, int, int)` | — | ✔ |
| `objectKind(TValue, int)` | — | ✘ |
| `namedKind(String, TValue, int)` | — | ✘ |
| `isolate()` | — | ✘ |
| `restore()` | — | ✘ |

**class `ObjectUtilsFlavourInitTest.RandartsGate`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `offKeepsTheFixedBinding()` | — | ✘ |
| `onReassignsByRandomDraw()` | — | ✘ |

**class `ObjectUtilsFlavourInitTest.ScrollTitleGeneration`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyTitleIsWellFormedAndUnique()` | — | ✘ |

**class `ObjectUtilsFlavourInitTest.ScrollFlavourWiring`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `scrollFlavourTextComesFromGeneratedTitles()` | — | ✘ |

**class `ObjectUtilsFlavourInitTest.AwareMarkingPass`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `marksOrdinaryUnflavouredKindAware()` | — | ✘ |
| `leavesNamelessKindUntouched()` | — | ✘ |
| `leavesSpecialArtifactKindUntouched()` | — | ✘ |


##### `ObjectUtilsFlavourResetFixedTest.java`

**Responsible for:** Tests ObjectUtils#flavourResetFixed, the port of C's flavor_reset_fixed (obj-util.c).

**class `ObjectUtilsFlavourResetFixedTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flavourResetFixed()` | — | ✔ |
| `resolvedFixedFlavour(String, int)` | — | ✔ |

**class `ObjectUtilsFlavourResetFixedTest.OrdinaryFixedFlavour`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `differentlyNamedRingFlavourIsReset()` | — | ✔ |
| `nonRingFlavourNamedPlainGoldIsStillReset()` | — | ✔ |

**class `ObjectUtilsFlavourResetFixedTest.OneRing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plainGoldRingFlavourIsPreserved()` | — | ✔ |

**class `ObjectUtilsFlavourResetFixedTest.MultipleKinds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyGroupIsWalked()` | — | ✔ |


##### `ObjectUtilsFlavourSetAllAwareTest.java`

**Responsible for:** Tests ObjectUtils#flavourSetAllAware, the port of C's flavor_set_all_aware (obj-util.c).

**class `ObjectUtilsFlavourSetAllAwareTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `savedObjectKinds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `objectKindsField()` | — | ✘ |
| `objectKind(String, Flavour, boolean)` | — | ✔ |
| `someFlavour()` | — | ✘ |
| `isolate()` | — | ✘ |
| `restore()` | — | ✘ |

**class `ObjectUtilsFlavourSetAllAwareTest.FlavouredKind`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `becomesAware()` | — | ✘ |

**class `ObjectUtilsFlavourSetAllAwareTest.UnflavouredKind`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `staysUnaware()` | — | ✘ |
| `staysAwareWhenAlreadyAware()` | — | ✘ |

**class `ObjectUtilsFlavourSetAllAwareTest.EmptySlot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `skippedRegardlessOfFlavour()` | — | ✘ |

**class `ObjectUtilsFlavourSetAllAwareTest.MultipleKinds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eachJudgedIndependently()` | — | ✘ |


##### `ObjectUtilsGearTest.java`

**Responsible for:** Tests the gear routines of ObjectUtils that had no direct coverage: combinePack and the partial-stacking check beneath it, gearInsertEnd, gearToLabel, isCarried, and the end-of-string case of preferredQuiverSlot.

**class `ObjectUtilsGearTest`** — class Javadoc: ✔ · type stamp: on 261005

- Fields (4/23 documented): ✔ `MAX_STACK`, `potionKind`, `otherKind`, `arrowKind` — ✘ `PACK_SIZE`, `QUIVER_SIZE`, `SLOT_SIZE`, `THROWN_MULT`, `savedConstants`, `savedBodies`, `savedRaces`, `savedCurses`, `savedTimedEffects`, `savedGamePlayer`, `savedGameCave`, `savedQueue`, `body`, `player`, `level`, `known`, `realBus`, `bus`, `queue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✘ |
| `restoreGlobals()` | — | ✘ |
| `plainRace(PlayerBody)` | — | ✘ |
| `registryField(String)` | — | ✘ |
| `buildPlayer()` | — | ✔ |
| `tearDown()` | — | ✘ |
| `stack(ObjectKind, int)` | — | ✔ |
| `listIn(Chunk, ItemObject)` | — | ✘ |
| `gear()` | — | ✘ |
| `combine()` | — | ✔ |

**class `ObjectUtilsGearTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `messages()` | — | ✘ |

**class `ObjectUtilsGearTest.WholeMerges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstAddedStackSurvives()` | — | ✘ |
| `threeStacksCollapseIntoTheFirst()` | — | ✘ |
| `unmergeableStackIsSkippedOver()` | — | ✘ |
| `wholeMergeIsAnnouncedAndDisablesRepeat()` | — | ✘ |

**class `ObjectUtilsGearTest.PartialMerges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `earlierStackIsMaximised()` | — | ✘ |
| `partialMergeIsSilent()` | — | ✘ |
| `threeThirtiesSettleInInsertionOrder()` | — | ✘ |
| `fullLeadingStackTakesNothing()` | — | ✘ |

**class `ObjectUtilsGearTest.KnownCountsAlign`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wholeMergeRealignsTheSurvivor()` | — | ✘ |
| `partialMergeRealignsBoth()` | — | ✘ |

**class `ObjectUtilsGearTest.NothingToDo`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyGearFinishes()` | — | ✘ |
| `singleStackFinishes()` | — | ✘ |
| `interleavedKindsPairOff()` | — | ✘ |

**class `ObjectUtilsGearTest.GearInsertEnd`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `laterInsertionsComeFirst()` | — | ✘ |
| `knownHalfJoinsTheParallelList()` | — | ✘ |

**class `ObjectUtilsGearTest.GearToLabel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wornItemTakesItsSlotLetter()` | — | ✘ |
| `quiverItemTakesADigit()` | — | ✘ |
| `packItemTakesALetter()` | — | ✘ |
| `roguelikeMovementKeysAreSkipped()` | — | ✘ |
| `lettersResumeAtM()` | — | ✘ |
| `absentItemHasNoLabel()` | — | ✘ |
| `nullHasNoLabel()` | — | ✘ |

**class `ObjectUtilsGearTest.IsCarried`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `carriedOnlyWhenInTheGear()` | — | ✘ |
| `identityNotLookalike()` | — | ✘ |

**class `ObjectUtilsGearTest.PreferredQuiverSlotEnd`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `arrows(String)` | — | ✘ |
| `completeTagNamesItsSlot()` | — | ✘ |
| `tagWithNoDigitAnswersMinusOne()` | — | ✘ |


##### `ObjectUtilsLookupKindTest.java`

**Responsible for:** Tests ObjectUtils#lookupKind(TValue, int) and ObjectUtils#lookupKind(TValue, String), the ports of C's lookup_kind and the lookup_kind(tval, lookup_sval(tval, name)) pattern C itself composes at some call sites (obj-util.c).

**class `ObjectUtilsLookupKindTest`** — class Javadoc: ✔ · type stamp: on 260904

- Fields (0/5 documented): ✘ `TOUCHED`, `saved`, `swordBase`, `realBus`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `snapshot()` | — | ✘ |
| `restore()` | — | ✘ |
| `field(String)` | — | ✘ |
| `get(String)` | — | ✘ |
| `set(String, Object)` | — | ✘ |
| `kind(String, ObjectBase, String)` | — | ✘ |
| `isolate()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `numericOverloadReturnsTheMatchingKindWithoutSignallingAMessage()` | — | ✘ |
| `numericOverloadMissReturnsNullAndSignalsANoObjectMessage()` | — | ✘ |
| `stringOverloadMatchesBySvalNameCaseInsensitively()` | — | ✘ |
| `stringOverloadTreatsAnAllDigitReferenceAsALiteralSval()` | — | ✘ |
| `stringOverloadMissReturnsNullAndSignalsANoObjectMessageWithTheRawReference()` | — | ✘ |

**class `ObjectUtilsLookupKindTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `types`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `lastMessage()` | — | ✘ |


##### `ObjectUtilsObjectElementIsKnownTest.java`

**Responsible for:** Tests ObjectUtils#objectElementIsKnown(Player, Curse, ElementEnum), the curse-shaped counterpart to C's object_element_is_known (obj-knowledge.c).

**class `ObjectUtilsObjectElementIsKnownTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curse(int)` | — | ✔ |
| `fullyKnownCurse()` | — | ✔ |
| `notFullyKnownCurse()` | — | ✔ |
| `elementInfo(int)` | — | ✔ |
| `putShadowElementInfo(Curse, ElementEnum, ElementInfo)` | — | ✔ |
| `playerWithItemKnowledge()` | — | ✘ |

**class `ObjectUtilsObjectElementIsKnownTest.SentinelElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `elemNoneReportsUnknown()` | — | ✘ |
| `elemMaxReportsUnknown()` | — | ✘ |

**class `ObjectUtilsObjectElementIsKnownTest.FullyKnownCurse`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnownRegardlessOfTheOtherTwoChecks()` | — | ✘ |

**class `ObjectUtilsObjectElementIsKnownTest.PlayerKnowsTheElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `ObjectUtilsObjectElementIsKnownTest.ShadowKnowsTheElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `ObjectUtilsObjectElementIsKnownTest.NeitherKnowsTheElement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsUnknown()` | — | ✘ |
| `checksOnlyTheNamedElement()` | — | ✘ |
| `aKnownButZeroShadowEntryReportsUnknown()` | — | ✘ |


##### `ObjectUtilsObjectFlagIsKnownTest.java`

**Responsible for:** Tests ObjectUtils#objectFlagIsKnown, the port of C's object_flag_is_known (obj-knowledge.c).

**class `ObjectUtilsObjectFlagIsKnownTest`** — class Javadoc: ✔ · type stamp: on 260924

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(TValue, ObjectKind, ItemObject)` | — | ✔ |
| `fullyKnownItem()` | — | ✔ |
| `notFullyKnownItem()` | — | ✔ |
| `playerWithItemKnowledge()` | — | ✘ |

**class `ObjectUtilsObjectFlagIsKnownTest.FullyKnownObject`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnownRegardlessOfTheOtherTwoChecks()` | — | ✘ |

**class `ObjectUtilsObjectFlagIsKnownTest.PlayerKnowsTheFlag`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `ObjectUtilsObjectFlagIsKnownTest.ShadowKnowsTheFlag`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsKnown()` | — | ✘ |

**class `ObjectUtilsObjectFlagIsKnownTest.NeitherKnowsTheFlag`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsUnknown()` | — | ✘ |
| `checksOnlyTheNamedFlag()` | — | ✘ |


##### `ObjectUtilsObjectPrepTest.java`

**Responsible for:** Tests ObjectUtils#objectPrep, the port of C's object_prep (obj-make.c).

**class `ObjectUtilsObjectPrepTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `savedConstants`, `savedCurses`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedFuelConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `anEffect()` | — | ✔ |
| `kind(TValue)` | — | ✔ |

**class `ObjectUtilsObjectPrepTest.PlainFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plainFieldsCopied()` | — | ✔ |
| `weaponHasNoPvalOrTimeout()` | — | ✔ |

**class `ObjectUtilsObjectPrepTest.FlagCopy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseFlagsAreNotApplied()` | — | ✔ |

**class `ObjectUtilsObjectPrepTest.Modifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `modifiersRollOrFallBackToZero()` | — | ✔ |

**class `ObjectUtilsObjectPrepTest.PvalBranches`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wandPvalFromCharge()` | — | ✔ |
| `potionPvalFromPVal()` | — | ✔ |

**class `ObjectUtilsObjectPrepTest.LightFuel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `torchGetsFuelTorchDefault()` | — | ✔ |
| `lampGetsDefaultLampDefault()` | — | ✔ |
| `unfuelledLightStaysAtZero()` | — | ✔ |

**class `ObjectUtilsObjectPrepTest.CombatBonuses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bonusesDoNotCrossOver()` | — | ✘ |

**class `ObjectUtilsObjectPrepTest.SlaysAndBrands`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `slaysAndBrandsCopiedFromKind()` | — | ✘ |

**class `ObjectUtilsObjectPrepTest.ElementInfo_`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `elementInfoUnionsKindAndBaseFlags()` | — | ✔ |


##### `PileInsertTest.java`

**Responsible for:** Tests Pile#insert(ItemObject).

**class `PileInsertTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `insertSetsOwningPileAndPlacesOnTop()` | — | ✘ |
| `insertPlacesEachNewItemOnTopInLifoOrder()` | — | ✘ |
| `insertRejectsItemAlreadyOwnedByThisPile()` | — | ✘ |
| `insertRejectsSoloMemberOfAnotherPile()` | — | ✘ |


##### `PileTest.java`

**Responsible for:** Tests the rest of Pile beyond Pile#insert, which PileInsertTest covers: insertEnd for one object and for a whole pile, excise, lastItem, contains, the index and bulk helpers, and hasArtifact.

**class `PileTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pileOf(ItemObject)` | — | ✔ |
| `headToTail(Pile)` | — | ✔ |
| `makeArtifact(ItemObject)` | — | ✔ |

**class `PileTest.InsertEndOne`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `intoEmptyPile()` | — | ✘ |
| `goesAfterTheTail()` | — | ✘ |
| `repeatedCallsKeepCallOrder()` | — | ✘ |
| `rejectsObjectInThisPile()` | — | ✘ |
| `rejectsMidListObject()` | — | ✘ |
| `rejectsSoloMemberOfAnotherPile()` | — | ✘ |

**class `PileTest.InsertEndPile`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `appendsAfterTheTail()` | — | ✘ |
| `movedObjectsAreOwnedByTheDestination()` | — | ✘ |
| `intoEmptyPile()` | — | ✘ |
| `emptyNewListChangesNothing()` | — | ✘ |
| `singleObjectList()` | — | ✘ |
| `matchesAppendingOneByOneHeadFirst()` | — | ✘ |
| `selfAppendThrows()` | — | ✘ |
| `sourcePileIsSpent()` | — | ✘ |

**class `PileTest.Excise`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `removesTheHead()` | — | ✘ |
| `removesTheMiddle()` | — | ✘ |
| `removesTheTail()` | — | ✘ |
| `removesTheOnlyObject()` | — | ✘ |
| `removedObjectCanBeReinserted()` | — | ✘ |
| `objectFromAnotherPileThrows()` | — | ✘ |
| `unownedObjectThrows()` | — | ✘ |

**class `PileTest.LastItem`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lastItemOfEmptyPileIsNull()` | — | ✘ |
| `lastItemIsTheTail()` | — | ✘ |
| `lastItemDoesNotRemove()` | — | ✘ |
| `singleObjectIsBothEnds()` | — | ✘ |
| `peekLastItemIsTheHead()` | — | ✘ |
| `peekLastItemOfEmptyPileThrows()` | — | ✘ |

**class `PileTest.Contains`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `findsEachObject()` | — | ✘ |
| `emptyPileContainsNothing()` | — | ✘ |
| `comparesIdentity()` | — | ✘ |
| `otherPilesObjectIsNotFound()` | — | ✘ |

**class `PileTest.Helpers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getCountsFromTheTail()` | — | ✘ |
| `iteratorWalksHeadFirst()` | — | ✘ |
| `iteratorOfEmptyPile()` | — | ✘ |
| `iteratorRunsFromHeadToTail()` | — | ✘ |
| `reversedWalksHeadFirst()` | — | ✘ |
| `sizeCountsObjects()` | — | ✘ |
| `removeIfRemovesAndClearsOwner()` | — | ✘ |
| `removeIfIgnoresAbsentObject()` | — | ✘ |
| `removeByIndex()` | — | ✘ |
| `removeByIndexOutOfRange()` | — | ✘ |
| `clearEmptiesAndClearsOwners()` | — | ✘ |

**class `PileTest.HasArtifact`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyPileHasNone()` | — | ✘ |
| `ordinaryObjectsHaveNone()` | — | ✘ |
| `findsArtifactAtTheHead()` | — | ✘ |
| `findsArtifactAtTheTail()` | — | ✘ |


##### `PlayerClassNoMagicTest.java`

**Responsible for:** Tests that PlayerClass turns a null spellcasting definition into ClassMagic#NONE, and that ObjectKind#canBrowse() therefore answers false for such a class rather than throwing.

**class `PlayerClassNoMagicTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `savedPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `classWith(ClassMagic)` | — | ✘ |
| `installPlayer()` | — | ✘ |
| `restorePlayer()` | — | ✘ |
| `nullBecomesNone()` | — | ✘ |
| `realMagicKept()` | — | ✘ |
| `copyKeepsNone()` | — | ✘ |
| `nonCasterBrowsesNothing()` | — | ✘ |


##### `PowerTablesTest.java`

**Responsible for:** Tests the four power lookup tables — Archery, ElementPowers, ElementSet and FlagSet — which are the port of the static tables at the head of C's obj-power.c.

**class `PowerTablesTest`** — class Javadoc: ✔ · type stamp: —

**class `PowerTablesTest.ArcheryRows`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fieldsRoundTrip()` | — | ✔ |
| `rowsMatchCsShape()` | — | ✔ |

**class `PowerTablesTest.ElementPowerRows`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lowResistRow()` | — | ✔ |
| `highResistRow()` | — | ✔ |
| `vulnerabilityIsNegative()` | — | ✔ |

**class `PowerTablesTest.ElementSetRows`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fieldsRoundTrip()` | — | ✔ |
| `immunitiesDemandLevelThree()` | — | ✔ |
| `countIsWorkingState()` | — | ✔ |

**class `PowerTablesTest.FlagSetRows`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fieldsRoundTrip()` | — | ✔ |
| `rowsMatchCsShape()` | — | ✔ |
| `countIsWorkingState()` | — | ✔ |


##### `SlayEqualsTest.java`

**Responsible for:** Tests Slay#copy(), Slay#equals and Slay#hashCode(), and how equals relates to Slay#sameMonsterSlain, the port of C's same_monsters_slain (obj-slays.c).

**class `SlayEqualsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `evilSlay()` | — | ✔ |
| `copyIsEqualButNotSame()` | — | ✘ |
| `reflexive()` | — | ✘ |
| `nullAndForeignTypes()` | — | ✘ |
| `setTreatsCopyAsSameEntry()` | — | ✘ |
| `anySingleFieldDifferenceBreaksEquality()` | — | ✘ |
| `baseParticipates()` | — | ✘ |
| `sameMonsterSlainIsLooserThanEquals()` | — | ✘ |
| `sameMonsterSlainNeedsSameRaceFlag()` | — | ✘ |
| `sameMonsterSlainBaseAgainstNone()` | — | ✘ |


##### `SlayTest.java`

**Responsible for:** Tests the construction, accessors, Slay#toString() and Slay#sameMonsterSlain of Slay, the port of C's struct slay (object.h) and of same_monsters_slain (obj-slays.c).

**class `SlayTest`** — class Javadoc: ✔ · type stamp: on 261005

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `evil2()` | — | ✔ |
| `accessorsReturnStoredValues()` | — | ✘ |
| `accessorsForAKill()` | — | ✘ |
| `constructorSplitsEveryShippedCode()` | — | ✘ |
| `constructorRejectsMalformedCodes()` | — | ✘ |
| `toStringListsFields()` | — | ✘ |
| `sameFlagNoBases()` | — | ✘ |
| `differentFlags()` | — | ✘ |
| `sameFlagSameBase()` | — | ✘ |
| `sameFlagDifferentBases()` | — | ✘ |
| `sameFlagOneBase()` | — | ✘ |
| `baseOnlySlays()` | — | ✘ |


## middle/objects/enums

##### `ArtifactIndexTest.java`

**Responsible for:** Unit tests for ArtifactIndex, the port of the ART_IDX_* enum that obj-randart.h builds from the X-macro list list-randart-properties.h.

**class `ArtifactIndexTest`** — class Javadoc: ✔ · type stamp: on 261002

- Fields (2/2 documented): ✔ `C_ORDER`, `C_SUPERCHARGED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsAreInTheCOrder()` | on 261002 | ✔ |
| `ordinalsMatchTheCIntegerValues()` | on 261002 | ✔ |
| `superchargeFlagMatchesTheCColumn(ArtifactIndex)` | on 261002 | ✔ |
| `superchargedRowsSitJustBeforeTheSentinel()` | on 261002 | ✔ |


##### `ChestQueryTest.java`

**Responsible for:** Tests ChestQuery, the port of C's enum chest_query (obj-chest.h:26-30).

**class `ChestQueryTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsMatchC()` | — | ✔ |


##### `ChestTrapCodeTest.java`

**Responsible for:** Tests for ChestTrapCode against the figures C produces: the code: lines of chest_trap.txt in file order, and the pval that parse_chest_trap_name in obj-chest.c gives each record (1 for the first, then double the previous one).

**class `ChestTrapCodeTest`** — class Javadoc: ✔ · type stamp: on 261008

- Fields (1/1 documented): ✔ `FILE_CODES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsAreTheFileCodesInFileOrder()` | — | ✘ |
| `eachConstantCarriesTheBitCGivesItsRecord()` | — | ✘ |
| `valueOfResolvesEveryFileCodeAndRejectsOthers()` | — | ✘ |
| `maxTrapsIsTheFourteenOfTheDataFileHeader()` | — | ✘ |
| `fourteenTrapsStillFitTheSignedSixteenBitPval()` | — | ✘ |
| `aChestPvalOfExactlyOneIsLockedAndUntrapped()` | — | ✘ |
| `allBitsOrTogetherToOneBelowTheNextPowerOfTwo()` | — | ✘ |


##### `CombatRunesTest.java`

**Responsible for:** Pins CombatRunes to enum combat_runes in obj-knowledge.h and the c_rune[] table in obj-knowledge.c.

**class `CombatRunesTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchTheCEnum()` | on 261009 | ✔ |
| `sentinelOrdinalIsTheRuneCount()` | on 261009 | ✔ |
| `namesAreTheCIdentifiers()` | on 261009 | ✔ |
| `descriptionsPortCRuneTable()` | on 261009 | ✔ |
| `sentinelHasEmptyDescription()` | on 261009 | ✔ |
| `realRunesHaveDistinctNonBlankDescriptions()` | on 261009 | ✔ |
| `valueOfIsExact()` | on 261009 | ✔ |


##### `ElementEnumTest.java`

**Responsible for:** Unit tests for ElementEnum — the damage elements, the port of C's ELEM_* list (src/list-elements.h).

**class `ElementEnumTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theListIsCsTwentyFiveElementsPlusItsTwoSentinels()` | — | ✔ |
| `theElementsAreInTheirCOrder()` | — | ✔ |
| `everyRealElementResolvesToItsProjection(ElementEnum)` | — | ✔ |
| `theWorkedExamplesFromTheJavadocHold()` | — | ✔ |
| `theSentinelsResolveOnlyAsFarAsTheirNamesExist()` | — | ✔ |
| `exactlyTheFourPhysicalElementsAreBaseElements()` | — | ✔ |
| `theResistableElementsAreTheContiguousRunUpToDisenchantment()` | — | ✔ |
| `everyBaseElementIsAlsoResistable()` | — | ✔ |


##### `EquipmentSlotsEnumTest.java`

**Responsible for:** Tests EquipmentSlotsEnum, the port of C's equipment-slot table (list-equip-slots.h, read into an enum at obj-gear.h:28-34 and a parallel slot_table at obj-gear.c:34-46).

**class `EquipmentSlotsEnumTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsMatchC()` | — | ✔ |
| `nonArmourSlots()` | — | ✔ |
| `combatExcludedSlots()` | — | ✔ |


##### `GetItemFlagsTest.java`

**Responsible for:** Tests for GetItemFlags against the twelve #defines under "Bit flags for get_item() function" in game-input.h: the names, their order, and the bit each one holds in C's int mode (0x0001 for USE_EQUIP doubling up to 0x0800 for SHOW_THROWING).

**class `GetItemFlagsTest`** — class Javadoc: ✔ · type stamp: on 261008

- Fields (2/2 documented): ✔ `HEADER_NAMES`, `HEADER_BITS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsAreTheHeaderNamesInHeaderOrder()` | — | ✘ |
| `ordinalIsTheBitPositionOfTheCDefine()` | — | ✘ |
| `twelveFlagsOccupyTheLowTwelveBitsOfTheCMode()` | — | ✘ |
| `valueOfRejectsOtherSpellings()` | — | ✘ |
| `shapechangedStripLeavesTheFloorAndTheDisplayBits()` | — | ✘ |


##### `IgnoreTypeTest.java`

**Responsible for:** Unit tests for IgnoreType, the port of C's ignore_type_t (src/list-ignore-types.h, expanded in src/obj-ignore.h).

**class `IgnoreTypeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `C_ROWS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theEnumIsCsTwentySevenRowsPlusTheMaxSentinel()` | — | ✔ |
| `everyRowSitsAtItsCIndexUnderItsCName()` | — | ✔ |
| `everyRowCarriesItsCDescription()` | — | ✔ |
| `maxSitsAfterTheLastRowAndEqualsTheRowCount()` | — | ✔ |
| `noneIsSlotZeroAndTheOnlyRowWithAnEmptyName()` | — | ✔ |
| `maxHasTheEmptyJavaFillerName()` | — | ✔ |
| `theShadowingTypesAreDistinctFromThePlainOnes()` | — | ✔ |

**record `IgnoreTypeTest.CRow`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/2 documented): ✘ `index`, `description`


##### `ObjPropertyTypeTest.java`

**Responsible for:** Pins ObjPropertyType against enum obj_property_type in obj-properties.h and the streq chain in parse_object_property_type (obj-init.c).

**class `ObjPropertyTypeTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCDeclarationOrder()` | — | ✘ |
| `everyCToken_resolvesToItsConstant()` | — | ✘ |
| `emptyToken_isRejectedNotNone()` | — | ✔ |
| `maxToken_isRejectedNotMax()` | — | ✔ |
| `placeholderTokens_neverResolve()` | — | ✔ |
| `unrecognizedTokens_returnNull()` | — | ✔ |
| `nullToken_returnsNull()` | — | ✔ |


##### `ObjectDescriptionTest.java`

**Responsible for:** Tests for ObjectDescription against the anonymous enum under "Modes for object_desc()" in obj-desc.h: the names, their order, and the bit each one holds in C's uint32_t mode.

**class `ObjectDescriptionTest`** — class Javadoc: ✔ · type stamp: on 261008

- Fields (2/2 documented): ✔ `HEADER_NAMES`, `HEADER_VALUES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsAreTheHeaderNamesInHeaderOrder()` | — | ✘ |
| `noConstantForOdescFull()` | — | ✘ |
| `ordinalAfterBaseIsOnePastTheBitPosition()` | — | ✘ |
| `baseIsTheOnlyConstantWithNoBit()` | — | ✘ |
| `elevenBitsOccupyTheLowElevenBitsOfTheCMode()` | — | ✘ |
| `odescFullIsCombatPlusExtra()` | — | ✘ |
| `emptySetIsNotASetHoldingOnlyBase()` | — | ✘ |
| `prefixWithBaseDoesNotSetTheCombatOrExtraBits()` | — | ✘ |
| `valueOfRejectsOtherSpellings()` | — | ✘ |


##### `ObjectFlagIDTest.java`

**Responsible for:** Pins ObjectFlagID against enum object_flag_id in obj-properties.h and the streq chain in parse_object_property_id_type (obj-init.c).

**class `ObjectFlagIDTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCDeclarationOrder()` | — | ✘ |
| `everyCToken_resolvesToItsConstant()` | — | ✘ |
| `getIdMethod_returnsTheCToken()` | — | ✘ |
| `getIdMethod_roundTripsEveryConstant()` | — | ✘ |
| `emptyToken_resolvesToNone()` | — | ✔ |
| `unrecognizedTokens_returnNull()` | — | ✔ |
| `nullToken_returnsNullWithoutThrowing()` | — | ✘ |
| `everyIdTypeInObjectPropertyTxt_resolves()` | — | ✔ |


##### `ObjectFlagTest.java`

**Responsible for:** Unit tests for ObjectFlag, the port of C's OF_* list in list-object-flags.h, and for ObjectFlag#getSustainStatFlag, the object-flag half of sustain_flag() in obj-properties.c.

**class `ObjectFlagTest`** — class Javadoc: ✔ · type stamp: on 261005

- Fields (1/1 documented): ✔ `C_TABLE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sentinelsBracketTheRealFlags()` | — | ✘ |
| `everyFlagSitsAtItsCIndexWithItsCLabel()` | — | ✘ |
| `sustainsAreTheFirstFiveRealFlagsInStatOrder()` | — | ✘ |
| `sustainStatFlagMapsEachStatToItsSustain()` | — | ✘ |
| `sustainStatFlagIndexIsStatIndexPlusOne()` | — | ✔ |
| `labelsAreFiveCharactersExceptBlessed()` | — | ✔ |
| `statsToSustainToFlagFollowsStatPlusOne()` | — | ✔ |
| `sustainStatFlagIsNullForTheSentinels()` | — | ✔ |


##### `ObjectFlagTypeTest.java`

**Responsible for:** Pins ObjectFlagType against enum object_flag_type in obj-properties.h and the streq chain in parse_object_property_subtype (obj-init.c).

**class `ObjectFlagTypeTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCDeclarationOrder()` | — | ✘ |
| `everyCToken_resolvesToItsConstant()` | — | ✘ |
| `getSubtypeText_roundTripsEveryRealGrouping()` | — | ✘ |
| `emptyToken_resolvesToNoneNotMax()` | — | ✔ |
| `unrecognizedTokens_returnNull()` | — | ✔ |


##### `ObjectKindFlagTest.java`

**Responsible for:** Unit tests for ObjectKindFlag, the port of C's KF_* list in list-kind-flags.h.

**class `ObjectKindFlagTest`** — class Javadoc: ✔ · type stamp: on 261012

- Fields (1/1 documented): ✔ `C_NAMES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hasExactlyTheEntriesOfTheCHeader()` | — | ✔ |
| `everyFlagIsInTheSamePositionAsInC()` | — | ✔ |
| `noneIsFlagZero()` | — | ✔ |
| `dataFileTokensResolveWithTheKfPrefix()` | — | ✔ |
| `ignoreTokensAreNotKindFlags()` | — | ✔ |
| `unknownTokenDoesNotResolve()` | — | ✔ |
| `everyMessageIsEmpty()` | — | ✔ |
| `flagSetHoldsAndUnionsKindFlags()` | — | ✔ |


##### `ObjectModifierTest.java`

**Responsible for:** Unit tests for ObjectModifier, the port of C's OBJ_MOD_* enum, which obj-properties.h builds from list-stats.h followed by list-object-modifiers.h.

**class `ObjectModifierTest`** — class Javadoc: ✔ · type stamp: on 261007

- Fields (1/1 documented): ✔ `C_ORDER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theListIsCsSixteenModifiersPlusItsTwoSentinels()` | — | ✔ |
| `theSentinelsBracketTheList()` | — | ✔ |
| `everyModifierSitsAtCsIndexPlusOne()` | — | ✔ |
| `theStatModifiersFollowOmStrInStatOrder()` | — | ✔ |
| `theObjModEntriesStartRightAfterTheStats()` | — | ✔ |
| `valueOfResolvesEveryRealName()` | — | ✔ |


##### `ObjectOriginEnumTest.java`

**Responsible for:** Pins ObjectOriginEnum against the rows of list-origins.h, which C builds both the ORIGIN_* enum in object.h and the origins[] table in obj-info.c from.

**class `ObjectOriginEnumTest`** — class Javadoc: ✔ · type stamp: on 261014

**record `ObjectOriginEnumTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/3 documented): ✘ `name`, `args`, `desc`
- Fields (1/1 documented): ✔ `C_ROWS`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `column(ObjectOriginEnum, String)` | — | ✔ |
| `args(ObjectOriginEnum)` | — | ✔ |
| `desc(ObjectOriginEnum)` | — | ✔ |
| `describe(ObjectOriginEnum, String, int)` | — | ✔ |
| `constantsFollowTheHeaderOrder()` | on 261014 | ✔ |
| `ordinalsAreCsEnumValues()` | on 261014 | ✔ |
| `everyRowMatchesTheHeader()` | on 261014 | ✔ |
| `cavernReadsOfACavern()` | on 261014 | ✔ |
| `theSentinelAndPlainRowsAreWhereCPutsThem()` | on 261014 | ✔ |
| `placeholderCountEqualsParameterCount()` | on 261014 | ✔ |
| `historyLinesComeOutAsCWouldPrintThem()` | on 261014 | ✔ |
| `theMonsterDropsTakeTwoParametersExceptTheUnknownOne()` | on 261014 | ✔ |


##### `QualityValueEnumTest.java`

**Responsible for:** Unit tests for QualityValueEnum - the quality bands of ignoring, the port of the anonymous IGNORE_* enum in obj-ignore.h.

**class `QualityValueEnumTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theListIsCsFiveBandsPlusItsMaxSentinel()` | — | ✔ |
| `ordinalsAreTheValuesCsHeaderGives()` | — | ✔ |
| `maxIsTheCountOfRealBands()` | — | ✔ |
| `menuRowsMapToBands()` | — | ✔ |
| `ordinalComparisonIgnoresAnItemOnlyWhenItsBandIsNotAboveTheSetting()` | — | ✔ |
| `maxIsAboveEverySetting()` | — | ✔ |


##### `ResTypeTest.java`

**Responsible for:** Pins ResType against the anonymous enum { T_LRES, T_HRES } in obj-power.c, and against the way the element_sets[] and el_powers[] tables in that file use it.

**class `ResTypeTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (1/1 documented): ✔ `C_EL_POWER_TYPES`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinalsMatchCDeclarationOrder()` | — | ✘ |
| `namesMatchC()` | — | ✘ |
| `unknownNameRejected()` | — | ✘ |
| `groupsAreDistinct()` | — | ✘ |
| `setAndPowerRowsJoinOnType()` | — | ✘ |
| `cElementPowerGroupsSplitFourAndNine()` | — | ✘ |


##### `RuneGroupTest.java`

**Responsible for:** Pins RuneGroup against C's rune_group_text[] (ui-knowledge.c) and the enum rune_variety (obj-knowledge.h) that indexes it.

**class `RuneGroupTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `C_GROUP_TEXT`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `headingsAreRuneGroupTextInOrder()` | — | ✘ |
| `thereAreSevenGroupsAndNoTerminatorEntry()` | — | ✘ |
| `ordinalsAreTheRuneVarietyEnumValues()` | — | ✘ |
| `flagVarietyIsLabelledOtherNotFlags()` | — | ✘ |
| `headingsAreDistinctSoNoTwoGroupsShareAPanelEntry()` | — | ✘ |
| `getNameIsTheDisplayTextNotTheConstantIdentifier()` | — | ✘ |


##### `RuneVarietyTest.java`

**Responsible for:** Pins RuneVariety#group(), RuneVariety#runeName() and RuneVariety#runeDesc() for each of the seven records against C's rune_name and rune_desc (obj-knowledge.c) and rune_group_text[] (ui-knowledge.c).

**class `RuneVarietyTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `projection(ProjectionEnum, String)` | — | ✘ |
| `property(String)` | — | ✘ |
| `acidBrand()` | — | ✘ |
| `animalSlay()` | — | ✘ |
| `vulnerability()` | — | ✘ |
| `groupsFollowRuneGroupTextOrder()` | — | ✘ |
| `combatNamesAreTheCRuneTableWithOxfordSpelling()` | — | ✘ |
| `combatDescriptionsHaveNoClosingFullStop()` | — | ✘ |
| `combatSentinelHasNoDescription()` | — | ✘ |
| `modifierNameIsUnwrappedAndDescriptionHasAFullStop()` | — | ✘ |
| `flagNameIsUnwrappedAndDescriptionHasAFullStop()` | — | ✘ |
| `resistNameIsPrefixedButDescriptionTakesTheBareProjectionName()` | — | ✘ |
| `resistReadsTheProjectionNameNotTheElementName()` | — | ✘ |
| `brandNameIsSuffixedAndDescriptionTakesTheBareName()` | — | ✘ |
| `slayNameIsPrefixedAndDescriptionTakesTheBareName()` | — | ✘ |
| `curseNameUsesTheNameAndDescriptionUsesTheDescField()` | — | ✘ |


##### `TValueClassificationTest.java`

**Responsible for:** Tests TValue's classification predicates — the port of the tval_is_* family in C's obj-tval.c.

**class `TValueClassificationTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertMembership(String, Predicate<TValue>, Set<TValue>)` | — | ✔ |
| `diggerIsAWeapon()` | — | ✔ |
| `findIndexIsTheOrdinal()` | — | ✔ |

**class `TValueClassificationTest.SingleType`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singleTypesAreExact()` | — | ✔ |
| `namesThatDoNotMatchTheirType()` | — | ✔ |

**class `TValueClassificationTest.Families`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `weaponFamilies()` | — | ✔ |
| `armourFamilies()` | — | ✔ |
| `consumableFamilies()` | — | ✔ |
| `otherFamilies()` | — | ✔ |

**class `TValueClassificationTest.WearableAndPower`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wearableMembership()` | — | ✔ |
| `variablePowerIsWearablePlusAmmo()` | — | ✔ |
| `ammunitionIsTheDifference()` | — | ✔ |

**class `TValueClassificationTest.RegistryLookups`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `savedKinds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedRegistry()` | — | ✔ |
| `restoreRegistry()` | — | ✔ |
| `kind(TValue, int)` | — | ✔ |
| `countsPerType()` | — | ✔ |
| `unknownNameCountsZero()` | — | ✔ |
| `listsSvalsInOrder()` | — | ✔ |
| `unknownNameListsNothing()` | — | ✔ |


##### `TValueLookupTest.java`

**Responsible for:** Tests TValue's lookups — TValue#fromName(String), TValue#fromName(int) and TValue#findIndex(String) — against C's tval_find_idx and its helper de_armour (obj-tval.c).

**class `TValueLookupTest`** — class Javadoc: ✔ · type stamp: on 261002

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertResolves(TValue, String)` | on 261002 | ✔ |
| `assertRejected(String)` | on 261002 | ✔ |
| `constantsAreInListTvalsOrder()` | on 261002 | ✔ |

**class `TValueLookupTest.Numbers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rangeEnds()` | on 261002 | ✔ |
| `outOfRange(String)` | on 261002 | ✔ |
| `signIsAccepted()` | on 261002 | ✔ |
| `leadingWhitespaceIsSkipped(String)` | on 261002 | ✔ |
| `trailingSpacesAndTabsAreAllowed(String)` | on 261002 | ✔ |
| `otherTrailingTextIsRejected(String)` | on 261002 | ✔ |
| `nonCWhitespaceIsNotSkipped()` | on 261002 | ✔ |
| `fromIntBounds()` | on 261002 | ✔ |

**class `TValueLookupTest.ArmourSpelling`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `britishSpellingResolves()` | on 261002 | ✔ |
| `americanSpellingResolves()` | on 261002 | ✔ |
| `prefixCaseDoesNotMatter()` | on 261002 | ✔ |
| `capitalisedArmourIsNotRewritten(String)` | on 261002 | ✔ |
| `textAfterArmourIsDropped()` | on 261002 | ✔ |
| `onlyTheFirstLowercaseMatchIsRewritten()` | on 261002 | ✔ |
| `bareArmourIsNotATval()` | on 261002 | ✔ |

**class `TValueLookupTest.DisplayNames`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyDisplayNameResolves()` | on 261002 | ✔ |
| `caseIsIgnored()` | on 261002 | ✔ |
| `noneIsATval()` | on 261002 | ✔ |
| `paddedNamesAreRejected(String)` | on 261002 | ✔ |
| `unknownTextIsRejected(String)` | on 261002 | ✔ |

**class `TValueLookupTest.FindName`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `namesInRange()` | on 261002 | ✔ |
| `outOfRangeIsUnknown(int)` | on 261002 | ✔ |
| `roundTripsWithFindIndex()` | on 261002 | ✔ |

**class `TValueLookupTest.Identifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `identifierResolves()` | on 261002 | ✔ |
| `prefixedIdentifierIsRejected()` | on 261002 | ✔ |
| `underscoredDisplayNameIsNotAnIdentifier()` | on 261002 | ✔ |


## middle/player

##### `BirtherTest.java`

**Responsible for:** Tests Birther, the port of C's struct birther (player-birth.c:93-109).

**class `BirtherTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (1/1 documented): ✔ `birther`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plainClass()` | — | ✔ |
| `build()` | — | ✘ |

**class `BirtherTest.FreshSnapshot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hasEmptyStatMap()` | — | ✔ |
| `otherwiseStartsAtDefaults()` | — | ✔ |

**class `BirtherTest.RaceAndClass`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raceStoredByReference()` | — | ✔ |
| `classStoredByReference()` | — | ✔ |

**class `BirtherTest.ScalarFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ageStoredAsGiven()` | — | ✔ |
| `weightStoredAsGiven()` | — | ✔ |
| `heightStoredAsGiven()` | — | ✔ |
| `scStoredAsGiven()` | — | ✔ |
| `auStoredAsGiven()` | — | ✔ |

**class `BirtherTest.StatMap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `holdsOneEntryPerStat()` | — | ✔ |
| `doesNotGuardSentinels()` | — | ✔ |

**class `BirtherTest.History`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedVerbatim()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |

**class `BirtherTest.Name`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storedVerbatimAtBufferLimit()` | — | ✔ |
| `storedVerbatimBeyondBufferLimit()` | — | ✔ |

**class `BirtherTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sharesRaceAndClassByReference()` | — | ✔ |
| `copiesStatMapIndependently()` | — | ✔ |
| `writeToSourceAfterCopyDoesNotReachResult()` | — | ✔ |
| `copiesScalarFieldsByValue()` | — | ✔ |
| `copiesHistoryAndName()` | — | ✔ |
| `returnsADifferentInstance()` | — | ✔ |


##### `EquipSlotConstructionTest.java`

**Responsible for:** Tests the construction and read side of EquipSlot: the constructor, getType, getName and getItem, against C's struct equip_slot (player.h).

**class `EquipSlotConstructionTest`** — class Javadoc: ✔ · type stamp: on 261011

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aNewSlotIsEmptyForEveryKind(EquipmentSlotsEnum)` | — | ✔ |
| `typeAndNameAreWhatWasGiven()` | — | ✔ |
| `twoSlotsOfOneTypeKeepTheirOwnNames()` | — | ✔ |
| `settingTheItemLeavesTypeAndNameAlone()` | — | ✔ |
| `aBodyCopyGivesEmptySlotsWithTheSameTypeAndName()` | — | ✔ |


##### `EquipSlotSetItemTest.java`

**Responsible for:** Tests EquipSlot#setItem, the port's encapsulation of C's plain field write to equip_slot.obj (player.h:154) — there is no C function of its own to compare against, only the many call sites that assign the field directly (e.g.

**class `EquipSlotSetItemTest`** — class Javadoc: ✔ · type stamp: on 260905

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesTheGivenInstance()` | — | ✔ |
| `nullEmptiesTheSlot()` | — | ✔ |
| `aSecondCallReplacesTheFirst()` | — | ✔ |
| `slotsAreIndependentOfOneAnother()` | — | ✔ |


##### `HistoryInfoTest.java`

**Responsible for:** Tests HistoryInfo — the port of C's struct history_info (src/player-history.h:47).

**class `HistoryInfoTest`** — class Javadoc: ✔ · type stamp: on 260831

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flags(PlayerHistoryType)` | — | ✔ |
| `artifact()` | — | ✔ |

**class `HistoryInfoTest.AnEntryHoldsWhatItWasBuiltWith`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theDepthAndCharacterLevelDoNotCrossOver()` | — | ✔ |
| `theTurnIsKeptAsGiven()` | — | ✔ |
| `theTextIsKeptAsGiven()` | — | ✔ |
| `aBirthEntryHoldsAllSixValues()` | — | ✔ |

**class `HistoryInfoTest.TheArtifactStandsInForAIdx`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noArtifactIsNull()` | — | ✔ |
| `anArtifactEntryHoldsTheArtifactItself()` | — | ✔ |

**class `HistoryInfoTest.TheTextIsNotBoundedAtEighty`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eightyCharactersSurviveIntact()` | — | ✔ |
| `longerTextIsNotTruncated()` | — | ✔ |

**class `HistoryInfoTest.TheTypeIsASetOfFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aMissedArtifactCarriesTwoTypesAtOnce()` | — | ✔ |
| `aLoggedEntrysFlagsCanStillBeRewritten()` | — | ✔ |


##### `PlayerAUBirthTest.java`

**Responsible for:** Tests Player#setAUBirth and Player#getAUBirth — the quickstart copy of C's p->au_birth (player.h:586).

**class `PlayerAUBirthTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |

**class `PlayerAUBirthTest.SetAndGetAUBirth`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `storesPlainRollerGold()` | — | ✔ |
| `storesPointRollerGold()` | — | ✔ |
| `lastWriteWins()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |
| `doesNotTouchWorkingGold()` | — | ✔ |
| `widensWithoutLoss()` | — | ✔ |


##### `PlayerAdjustLevelTest.java`

**Responsible for:** Tests Player's adjustLevel, the level arithmetic every experience change ends in.

**class `PlayerAdjustLevelTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `intField(String)` | — | ✔ |
| `longField(String)` | — | ✔ |
| `adjust(long, long, int, int, int, boolean)` | — | ✔ |
| `history()` | — | ✔ |

**class `PlayerAdjustLevelTest.Table`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `keyedFromZero()` | — | ✔ |

**class `PlayerAdjustLevelTest.Gaining`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshCharacterStaysAtLevelOne()` | — | ✔ |
| `thresholdIsInclusive()` | — | ✔ |
| `climbsSeveralLevels()` | — | ✔ |
| `stopsAtMaxLevel()` | — | ✔ |

**class `PlayerAdjustLevelTest.Scaling`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doubledFactorDoublesTheThreshold()` | — | ✔ |
| `scaledThresholdTruncates()` | — | ✔ |

**class `PlayerAdjustLevelTest.Losing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `totalDrainFallsToLevelOne()` | — | ✔ |
| `maxLevelSurvivesADrain()` | — | ✔ |
| `partialDrainLandsOnTheRightLevel()` | — | ✔ |

**class `PlayerAdjustLevelTest.HighWaterMark`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `climbsIndependentlyOfLevel()` | — | ✔ |

**class `PlayerAdjustLevelTest.Clamps`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `negativesAreFloored()` | — | ✔ |
| `totalsAreCapped()` | — | ✔ |
| `currentAboveMaximumRaisesIt()` | — | ✔ |

**class `PlayerAdjustLevelTest.Verbosity`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `verboseGainLogsHistory()` | — | ✔ |
| `silentGainLogsNothing()` | — | ✔ |
| `losingLevelsLogsNothing()` | — | ✔ |

**class `PlayerAdjustLevelTest.ThroughExpLose`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `permanentDrainDropsTheLevel()` | — | ✔ |
| `temporaryDrainLowersTheLevelAlone()` | — | ✔ |


##### `PlayerAdjustSkillScaleTest.java`

**Responsible for:** Tests PlayerCalcs#adjustSkillScale(int, int, int, int), the port of C's adjust_skill_scale (player-calcs.c) — the way every temporary skill bonus and penalty in calcBonuses is applied.

**class `PlayerAdjustSkillScaleTest`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buildPlayer()` | — | ✘ |

**class `PlayerAdjustSkillScaleTest.Bonuses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addsAFraction()` | — | ✔ |
| `bonusTruncates()` | — | ✔ |
| `minimumLiftsTheBase()` | — | ✔ |

**class `PlayerAdjustSkillScaleTest.Penalties`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `subtractsAFraction()` | — | ✔ |
| `penaltyRoundsUp()` | — | ✔ |
| `agreesAcrossTheRange()` | — | ✔ |
| `largerNumerator()` | — | ✔ |

**class `PlayerAdjustSkillScaleTest.NegativeSkills`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bonusMovesNegativeUpward()` | — | ✔ |
| `penaltyMovesNegativeDownward()` | — | ✔ |


##### `PlayerAgeTest.java`

**Responsible for:** Tests Player#setAge, Player#getAge, PlayerRace#getBaseAge and PlayerRace#getModAge — the pieces C's get_ahw needs to roll a starting age, plus the accessor pair around the stored value.

**class `PlayerAgeTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(String, int, int)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `age()` | — | ✔ |

**class `PlayerAgeTest.SetAge`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `storesTheValue()` | — | ✔ |
| `lastWriteWins()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |

**class `PlayerAgeTest.RaceAgeFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseAndModAreSeparate()` | — | ✔ |
| `elfBaseEqualsSpread()` | — | ✔ |
| `feedsBirthRollRange()` | — | ✔ |


##### `PlayerBackgroundAccessorTest.java`

**Responsible for:** Tests the two ends of a character's background: PlayerRace#getHistory(), the chart the text is generated from (p->race->history, player.h:200), and Player#setHistoryBirth(String), the finished text (p->history).

**class `PlayerBackgroundAccessorTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `chart(int, int, Object)` | — | ✔ |
| `race(String, PlayerHistoryChart)` | — | ✔ |
| `build()` | — | ✔ |
| `history()` | — | ✔ |

**class `PlayerBackgroundAccessorTest.RaceChart`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsTheChart()` | — | ✔ |
| `survivesACopy()` | — | ✔ |
| `mayBeAbsent()` | — | ✔ |

**class `PlayerBackgroundAccessorTest.BackgroundText`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startsUnset()` | — | ✔ |
| `storedVerbatim()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |

**class `PlayerBackgroundAccessorTest.ChartToText`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `generatesIntoTheCharacter()` | — | ✔ |
| `noChartYieldsEmpty()` | — | ✔ |


##### `PlayerBirthBirthStatCostsTest.java`

**Responsible for:** Tests PlayerBirth's birthStatCosts field, the port of C's birth_stat_costs (player-birth.c).

**class `PlayerBirthBirthStatCostsTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (1/1 documented): ✔ `EXPECTED`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `birthStatCosts()` | — | ✔ |
| `matchesTheCArray()` | — | ✔ |


##### `PlayerBirthBuyStatTest.java`

**Responsible for:** Tests PlayerBirth#buyStat(Stats, Map, Map, Map, int, boolean), the port of C's buy_stat (player-birth.c).

**class `PlayerBirthBuyStatTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (13/13 documented): ✔ `START_GOLD`, `MAX_BIRTH_POINTS`, `COST_FLAT`, `COST_AT_SEVENTEEN`, `COST_AT_EIGHTEEN`, `CHOICE_STR`, `playerBirth`, `player`, `realPlayer`, `bus`, `realBus`, `realCharacterGenerated`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✔ |
| `seedConstants(int)` | — | ✔ |
| `statsMap(int, int, int, int, int)` | — | ✔ |
| `pointsLeftOf(Object)` | — | ✔ |
| `succeededOf(Object)` | — | ✔ |
| `call(Stats, Map<Stats,Integer>, Map<Stats,Integer>, Map<Stats,Integer>, int, boolean)` | — | ✔ |
| `auBirth()` | — | ✔ |
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newCharacter()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |

**class `PlayerBirthBuyStatTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `events`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerBirthBuyStatTest.OrdinaryPurchase`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `raisesTheChosenStat()` | — | ✔ |
| `choiceFourRaisesConstitution()` | — | ✔ |

**class `PlayerBirthBuyStatTest.InvalidChoice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertRejectedCleanly(Stats)` | — | ✔ |
| `rejectsStatNone()` | — | ✔ |
| `rejectsStatMax()` | — | ✔ |

**class `PlayerBirthBuyStatTest.StatAlreadyCapped`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `refusesToRaisePast18()` | — | ✔ |

**class `PlayerBirthBuyStatTest.CostMismatch`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `throwsOnMismatch()` | — | ✔ |

**class `PlayerBirthBuyStatTest.InsufficientPoints`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `refusesWhenPointsAreShort()` | — | ✔ |

**class `PlayerBirthBuyStatTest.Boundary18`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `skipsTheIncrementCostRefresh()` | — | ✔ |

**class `PlayerBirthBuyStatTest.UpdateDisplayGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `signalsThenRecalculatesUsingThePostPurchaseTotal()` | — | ✔ |
| `doesNeitherWhenClear()` | — | ✔ |


##### `PlayerBirthDoBirthResetTest.java`

**Responsible for:** Tests the private PlayerBirth#doBirthReset, the port of C's do_birth_reset (player-birth.c) - the four-step reset the birth screen runs to undo every choice made so far: reload quickstart data if there is any to reload, rebuild the derived character fields, drop the character back to town, and re...

**class `PlayerBirthDoBirthResetTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (7/7 documented): ✔ `START_GOLD`, `FRESH_HISTORY`, `STARTING_DEPTH`, `player`, `realPlayer`, `savedConstants`, `realCharacterGenerated`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `setCharacterGenerated(boolean)` | — | ✔ |
| `seedConstants()` | — | ✔ |
| `race(String, int, int, String)` | — | ✔ |
| `testClass(int, int)` | — | ✔ |
| `quickstartBirther(PlayerRace, PlayerClass, String)` | — | ✔ |
| `reset(boolean, Birther)` | — | ✔ |
| `seedAndBuild()` | — | ✔ |
| `restore()` | — | ✔ |

**class `PlayerBirthDoBirthResetTest.OrdinaryReset`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resetsDepth()` | — | ✔ |
| `healsCharacter()` | — | ✔ |
| `regeneratesHistory()` | — | ✔ |
| `keepsRaceAndClass()` | — | ✔ |

**class `PlayerBirthDoBirthResetTest.QuickstartFlagFalseIgnoresPreviousLevel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `quickstartSnapshotNeverLoaded()` | — | ✔ |

**class `PlayerBirthDoBirthResetTest.QuickstartWithNoPreviousLevel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `behavesAsOrdinaryReset()` | — | ✔ |

**class `PlayerBirthDoBirthResetTest.QuickstartWithPreviousLevel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loadsQuickstartData()` | — | ✔ |
| `keepsQuickstartHistory()` | — | ✔ |
| `stillResetsDepthAndHeals()` | — | ✔ |


##### `PlayerBirthDoCmdAcceptCharacterTest.java`

**Responsible for:** Tests PlayerBirth#doCmdAcceptCharacter, the port of C's do_cmd_accept_character (player-birth.c) - the final step of character creation.

**class `PlayerBirthDoCmdAcceptCharacterTest`** — class Javadoc: ✔ · type stamp: on 260908

- Fields (0/8 documented): ✘ `player`, `realPlayer`, `realCommandQueue`, `realBus`, `realCharacterGenerated`, `bus`, `previousPrev`, `previousQuickstartPrev`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bootstrap()` | — | ✘ |
| `cleanup()` | — | ✔ |
| `humanRace()` | — | ✘ |
| `emptyStartItemsClass()` | — | ✔ |
| `birthOptions(Player)` | — | ✘ |
| `repeatPrevAllowed(CommandQueue)` | — | ✘ |
| `setRepeatPrevAllowed(CommandQueue, boolean)` | — | ✘ |
| `firstFlavouredKind()` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |

**class `PlayerBirthDoCmdAcceptCharacterTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `types`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `hasDispatch(GameEventType)` | — | ✘ |

**class `PlayerBirthDoCmdAcceptCharacterTest.OrdinaryPath`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `completesTheBirth()` | — | ✘ |
| `resetsDungeonAndRepeat()` | — | ✘ |
| `setsCombatValueHack()` | — | ✘ |
| `clearsCachedRollerHistory()` | — | ✘ |
| `addsBirthHistoryEntry()` | — | ✘ |

**class `PlayerBirthDoCmdAcceptCharacterTest.KnowRunesGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onLearnsEveryRune()` | — | ✘ |
| `offLeavesResistanceUnknown()` | — | ✘ |

**class `PlayerBirthDoCmdAcceptCharacterTest.KnowFlavorsGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onMarksFlavouredKindsAware()` | — | ✘ |
| `offLeavesFlavouredKindsUnaware()` | — | ✘ |

**class `PlayerBirthDoCmdAcceptCharacterTest.RandartsGuard`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `UNSET`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `onRunsWithoutThrowing()` | — | ✘ |
| `onStoresTheRolledSeed()` | — | ✘ |
| `offLeavesTheSeedAlone()` | — | ✘ |


##### `PlayerBirthDoCmdBirthInitTest.java`

**Responsible for:** Tests PlayerBirth#doCmdBirthInit, the port of C's do_cmd_birth_init (player-birth.c) - the CMD_BIRTH_INIT handler that starts the birth screen, either restoring a quickstart character or generating a fresh one from the first race and class.

**class `PlayerBirthDoCmdBirthInitTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (10/10 documented): ✔ `START_GOLD`, `player`, `realPlayer`, `savedConstants`, `previousRaces`, `previousClasses`, `realBus`, `bus`, `previousQuickstartPrev`, `previousQuickstartAllowed`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `playerRacesField()` | — | ✘ |
| `playerClassesField()` | — | ✘ |
| `seedConstants()` | — | ✔ |
| `race(String)` | — | ✔ |
| `testClass(String)` | — | ✔ |
| `seedBirthStats()` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `marksDungeonNotReady()` | — | ✔ |

**class `PlayerBirthDoCmdBirthInitTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `types`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `hasDispatch(GameEventType)` | — | ✘ |
| `lastType()` | — | ✘ |
| `lastOfType(Class<T>)` | — | ✘ |

**class `PlayerBirthDoCmdBirthInitTest.WithQuickstartData`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nameWithNoSuffixIsUntouched()` | — | ✔ |
| `validSuffixIsIncremented()` | — | ✔ |
| `emptySuffixFailsAndShowsMessage()` | — | ✔ |
| `growingSuffixIsWritten()` | — | ✔ |
| `thirtyOneCharacterSuccessorFits()` | — | ✔ |
| `thirtyTwoCharacterSuccessorDropsSuffix()` | — | ✔ |
| `shrinkingSuffixOnLongNameIsAccepted()` | — | ✔ |
| `signalsEnterBirthTrue()` | — | ✔ |

**class `PlayerBirthDoCmdBirthInitTest.WithNoQuickstartData`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `generatesFromFirstRaceAndClass()` | — | ✘ |
| `clearsQuickstartAllowed()` | — | ✘ |
| `neverSavesRollerData()` | — | ✘ |


##### `PlayerBirthDoCmdBirthResetTest.java`

**Responsible for:** Tests PlayerBirth#doCmdBirthReset, the port of C's do_cmd_birth_reset (player-birth.c) - the CMD_BIRTH_RESET handler that puts the character and its point-buy state back to how they'd be on entering the birth screen fresh.

**class `PlayerBirthDoCmdBirthResetTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (7/27 documented): ✔ `START_GOLD`, `MAX_BIRTH_POINTS`, `COST_AT_TEN`, `FRESH_HISTORY`, `DEFAULT_RACE_NAME`, `INITIAL_RACE_NAME`, `player` — ✘ `realPlayer`, `savedConstants`, `savedBodies`, `savedRaces`, `savedClasses`, `savedShapes`, `savedArtifacts`, `savedObjectKinds`, `savedCurses`, `savedMonsterRaces`, `savedQuests`, `realCharacterGenerated`, `savedTurn`, `savedRegistryStats`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedQuickstartAllowed`, `savedQuickstartPrev`, `savedRolledStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `setCharacterGenerated(boolean)` | — | ✘ |
| `readRawStatic(Class<?>, String)` | — | ✔ |
| `writeRawStatic(Class<?>, String, Object)` | — | ✔ |
| `seedConstants()` | — | ✘ |
| `race(String, int, int, String)` | — | ✔ |
| `testClass(int, int)` | — | ✔ |
| `normalShape()` | — | ✘ |
| `quickstartBirther(PlayerRace, PlayerClass, String)` | — | ✔ |
| `assertAllRealStats(Map<Stats,Integer>, int, String)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `resetsPointsLeftRegardlessOfWhatWasThere()` | — | ✔ |
| `resetsRegistryStatMaps()` | — | ✔ |
| `clearsRolledStats()` | — | ✔ |
| `ordinaryResetDefaultsToRegistryRace()` | — | ✔ |
| `loadsQuickstartDataAheadOfTheRegistrysDefaultRace()` | — | ✔ |


##### `PlayerBirthDoCmdBuyStatTest.java`

**Responsible for:** Tests PlayerBirth#doCmdBuyStat, the port of C's do_cmd_buy_stat (player-birth.c).

**class `PlayerBirthDoCmdBuyStatTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (4/14 documented): ✔ `START_GOLD`, `MAX_BIRTH_POINTS`, `COST_FLAT`, `player` — ✘ `realPlayer`, `bus`, `realBus`, `realCharacterGenerated`, `savedConstants`, `savedRegistryStats`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedRolledStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants()` | — | ✘ |
| `setCharacterGenerated(boolean)` | — | ✘ |
| `baseStats()` | — | ✔ |
| `zeroSpend()` | — | ✘ |
| `flatCosts()` | — | ✘ |
| `buyStatCommand(Integer)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `ordinaryChoiceBuysTheStatAndSignals()` | — | ✔ |
| `rolledStatsSkipsThePurchase()` | — | ✔ |
| `argMissingDoesNothing()` | — | ✔ |
| `choiceFarOutOfRangeDoesNothingRatherThanThrowing()` | — | ✔ |
| `sentinelChoiceIsRejectedByBuyStatNotByTheGuard()` | — | ✔ |

**class `PlayerBirthDoCmdBuyStatTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthDoCmdChooseClassTest.java`

**Responsible for:** Tests PlayerBirth#doCmdChooseClass, the port of C's do_cmd_choose_class (player-birth.c).

**class `PlayerBirthDoCmdChooseClassTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (6/16 documented): ✔ `START_GOLD`, `COST_FLAT`, `MAX_BIRTH_POINTS`, `OTHER_CLASS_NAME`, `CHOSEN_CLASS_NAME`, `player` — ✘ `realPlayer`, `realBus`, `realCharacterGenerated`, `savedConstants`, `savedClasses`, `savedRegistryStats`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedRolledStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants()` | — | ✘ |
| `registryField(String)` | — | ✘ |
| `writeInstance(Object, String, Object)` | — | ✘ |
| `testClass(String)` | — | ✔ |
| `chooseClassCommand(Integer)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `ordinaryChoiceAppliesClassAndRunsThePointBuyPipeline()` | — | ✔ |
| `wiresGenerateStatsPointsLeftBackToTheRegistry()` | — | ✔ |
| `clearsRolledStats()` | — | ✔ |
| `argMissingDoesNothing()` | — | ✔ |

**class `PlayerBirthDoCmdChooseClassTest.NoOpBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthDoCmdChooseHistoryTest.java`

**Responsible for:** Tests PlayerBirth#doCmdChooseHistory, the port of C's do_cmd_choose_history (player-birth.c).

**class `PlayerBirthDoCmdChooseHistoryTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (0/2 documented): ✘ `player`, `realPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `chooseHistoryCommand(String)` | — | ✘ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `ordinaryHistoryIsSet()` | — | ✔ |
| `previousHistoryIsReplaced()` | — | ✔ |
| `argMissingDoesNothing()` | — | ✔ |


##### `PlayerBirthDoCmdChooseNameTest.java`

**Responsible for:** Tests PlayerBirth#doCmdChooseName, the port of C's do_cmd_choose_name (player-birth.c).

**class `PlayerBirthDoCmdChooseNameTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (0/2 documented): ✘ `player`, `realPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `chooseNameCommand(String)` | — | ✘ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `ordinaryNameIsSet()` | — | ✔ |
| `overLengthNameIsTruncatedByDelegation()` | — | ✔ |
| `argMissingDoesNothing()` | — | ✔ |


##### `PlayerBirthDoCmdChooseRaceTest.java`

**Responsible for:** Tests PlayerBirth#doCmdChooseRace, the port of C's do_cmd_choose_race (player-birth.c).

**class `PlayerBirthDoCmdChooseRaceTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (6/16 documented): ✔ `START_GOLD`, `COST_FLAT`, `MAX_BIRTH_POINTS`, `OTHER_RACE_NAME`, `CHOSEN_RACE_NAME`, `player` — ✘ `realPlayer`, `realBus`, `realCharacterGenerated`, `savedConstants`, `savedRaces`, `savedRegistryStats`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedRolledStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants()` | — | ✘ |
| `registryField(String)` | — | ✘ |
| `writeInstance(Object, String, Object)` | — | ✘ |
| `race(String)` | — | ✔ |
| `warriorClass()` | — | ✔ |
| `chooseRaceCommand(Integer)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `ordinaryChoiceAppliesRaceAndRunsThePointBuyPipeline()` | — | ✔ |
| `wiresGenerateStatsPointsLeftBackToTheRegistry()` | — | ✔ |
| `clearsRolledStats()` | — | ✔ |
| `argMissingDoesNothing()` | — | ✔ |

**class `PlayerBirthDoCmdChooseRaceTest.NoOpBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthDoCmdPrevStatsTest.java`

**Responsible for:** Tests PlayerBirth#doCmdPrevStats, the port of C's do_cmd_prev_stats (player-birth.c).

**class `PlayerBirthDoCmdPrevStatsTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (3/8 documented): ✔ `START_GOLD`, `SENTINEL_CURRENT`, `player` — ✘ `realPlayer`, `bus`, `realBus`, `savedPrev`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants()` | — | ✘ |
| `distinctBirther(int)` | — | ✔ |
| `prevStatsCommand(Integer)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `zeroAgeSkipsSwapAndBonuses()` | — | ✔ |
| `nonZeroAgeSwapsAndRecalculatesBonuses()` | — | ✔ |
| `signalsEventsInCOrderRegardlessOfGuard()` | — | ✔ |
| `signalsEventsInCOrderWhenGuardSkipped()` | — | ✔ |
| `unusedChoiceArgIsIgnored()` | — | ✔ |

**class `PlayerBirthDoCmdPrevStatsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthDoCmdRefreshStatsTest.java`

**Responsible for:** Tests PlayerBirth#doCmdRefreshStats, the port of C's do_cmd_refresh_stats (player-birth.c).

**class `PlayerBirthDoCmdRefreshStatsTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (0/6 documented): ✘ `bus`, `realBus`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedRolledStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `refreshStatsCommand(Integer)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `pointBuyActiveResignalsCurrentTotals()` | — | ✔ |
| `rolledStatsActiveIsNoOp()` | — | ✔ |
| `unusedChoiceArgIsIgnored()` | — | ✔ |

**class `PlayerBirthDoCmdRefreshStatsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `events`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthDoCmdResetStatsTest.java`

**Responsible for:** Tests PlayerBirth#doCmdResetStats, the port of C's do_cmd_reset_stats (player-birth.c).

**class `PlayerBirthDoCmdResetStatsTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (4/14 documented): ✔ `START_GOLD`, `COST_FLAT`, `MAX_BIRTH_POINTS`, `player` — ✘ `realPlayer`, `bus`, `realBus`, `realCharacterGenerated`, `savedConstants`, `savedRegistryStats`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedRolledStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants()` | — | ✘ |
| `setCharacterGenerated(boolean)` | — | ✘ |
| `writeInstance(Object, String, Object)` | — | ✘ |
| `testClass()` | — | ✔ |
| `resetStatsCommand(Integer)` | — | ✔ |
| `assertAllRealStats(Map<Stats,Integer>, int, String)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `truthyChoiceResetsThenAutoGenerates()` | — | ✔ |
| `zeroChoiceResetsButSkipsAutoGenerate()` | — | ✔ |
| `argMissingStillResetsAndClearsRolledStats()` | — | ✔ |
| `resetStatsAlwaysUpdatesDisplayEvenWithoutAutoGenerate()` | — | ✔ |

**class `PlayerBirthDoCmdResetStatsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthDoCmdRollStatsTest.java`

**Responsible for:** Tests PlayerBirth#doCmdRollStats, the port of C's do_cmd_roll_stats (player-birth.c).

**class `PlayerBirthDoCmdRollStatsTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (5/15 documented): ✔ `START_GOLD`, `OLD_AGE`, `OLD_AU`, `OLD_HISTORY`, `player` — ✘ `realPlayer`, `bus`, `realBus`, `savedRegistryStats`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedRolledStats`, `savedPrev`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants()` | — | ✘ |
| `rollStatsCommand()` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `snapshotsThePreRollCharacter()` | — | ✔ |
| `zeroesPointsBookkeeping()` | — | ✔ |
| `marksRolledStatsTrue()` | — | ✔ |
| `signalsEventsInCOrder()` | — | ✔ |
| `rollsAFreshAge()` | — | ✔ |
| `overwritesHistoryFromTheRace()` | — | ✔ |
| `rollsFreshStatsIntoTheRegistry()` | — | ✔ |

**class `PlayerBirthDoCmdRollStatsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthDoCmdSellStatTest.java`

**Responsible for:** Tests PlayerBirth#doCmdSellStat, the port of C's do_cmd_sell_stat (player-birth.c).

**class `PlayerBirthDoCmdSellStatTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (4/14 documented): ✔ `START_GOLD`, `MAX_BIRTH_POINTS`, `COST_FLAT`, `player` — ✘ `realPlayer`, `bus`, `realBus`, `realCharacterGenerated`, `savedConstants`, `savedRegistryStats`, `savedRegistryPointsSpent`, `savedRegistryPointsInc`, `savedRegistryPointsLeft`, `savedRolledStats`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants()` | — | ✘ |
| `setCharacterGenerated(boolean)` | — | ✘ |
| `baseStatsWithOneStrPointBought()` | — | ✔ |
| `spendWithOneStrPointBought()` | — | ✔ |
| `flatCosts()` | — | ✘ |
| `sellStatCommand(Integer)` | — | ✔ |
| `seedFixture()` | — | ✘ |
| `restoreFixture()` | — | ✘ |
| `ordinaryChoiceSellsTheStatAndSignals()` | — | ✔ |
| `statAtFloorIsNotSoldBelowIt()` | — | ✔ |
| `rolledStatsSkipsTheSale()` | — | ✔ |
| `argMissingDoesNothing()` | — | ✔ |
| `choiceFarOutOfRangeDoesNothingRatherThanThrowing()` | — | ✔ |
| `sentinelChoiceIsRejectedBySellStatNotByTheGuard()` | — | ✔ |

**class `PlayerBirthDoCmdSellStatTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerBirthEmbodyTest.java`

**Responsible for:** Tests PlayerBirth#embody, the port of C's player_embody (player-birth.c), which gives a player the body their race is built with.

**class `PlayerBirthEmbodyTest`** — class Javadoc: ✔ · type stamp: on 260901

- Fields (5/5 documented): ✔ `PACK_SIZE`, `player`, `savedConstants`, `savedBodies`, `savedRaces`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `race(PlayerBody)` | — | ✔ |
| `seedRegistry()` | — | ✔ |
| `restoreRegistry()` | — | ✔ |
| `noRaceIsHarmless()` | — | ✔ |
| `bodyIsCopiedFromTheRace()` | — | ✔ |
| `layoutIsCarriedAcross()` | — | ✔ |
| `theCopyIsUnworn()` | — | ✔ |
| `twoCharactersOfARaceAreIndependent()` | — | ✔ |


##### `PlayerBirthGenerateStatsTest.java`

**Responsible for:** Tests the private PlayerBirth#generateStats, the port of C's generate_stats (player-birth.c), the point-buy birth screen's auto-generate heuristic.

**class `PlayerBirthGenerateStatsTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (8/8 documented): ✔ `COST_FLAT`, `START_GOLD`, `playerBirth`, `player`, `realPlayer`, `realBus`, `realCharacterGenerated`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants(int)` | — | ✘ |
| `accessibleField(Class<?>, String)` | — | ✘ |
| `writeInstance(Object, String, Object)` | — | ✘ |
| `statsMap(int, int, int, int, int)` | — | ✔ |
| `customClass(int, int, int, ClassMagic)` | — | ✔ |
| `magicWithStat(Stats)` | — | ✔ |
| `generate(Map<Stats,Integer>, Map<Stats,Integer>, Map<Stats,Integer>, int)` | — | ✔ |
| `newCharacter()` | — | ✔ |
| `restoreGlobals()` | — | ✘ |

**class `PlayerBirthGenerateStatsTest.NoOpBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerBirthGenerateStatsTest.CaseFourRegression`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `buysDexToItsAffordableCeiling()` | — | ✔ |

**class `PlayerBirthGenerateStatsTest.WarriorFullRun`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `runsTheWholeSequence()` | — | ✔ |

**class `PlayerBirthGenerateStatsTest.CasterFullRun`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `runsTheWholeSequence()` | — | ✔ |


##### `PlayerBirthGetAHWTest.java`

**Responsible for:** Tests PlayerBirth#getAHW, the port of C's get_ahw (player-birth.c): p->age = p->race->b_age + randint1(p->race->m_age); p->ht = p->ht_birth = Rand_normal(p->race->base_hgt, p->race->mod_hgt); p->wt = p->wt_birth = Rand_normal(p->race->base_wgt, p->race->mod_wgt); Two of those three lines are chai...

**class `PlayerBirthGetAHWTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(String, int, int, int, int, int, int)` | — | ✔ |
| `human()` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `field(String)` | — | ✔ |
| `giveRace(PlayerRace)` | — | ✔ |

**class `PlayerBirthGetAHWTest.Age`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ageRange()` | — | ✔ |
| `ageReachesBothEnds()` | — | ✔ |
| `ageWideSpreads()` | — | ✔ |
| `ageZeroSpread()` | — | ✔ |

**class `PlayerBirthGetAHWTest.HeightAndWeight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pairsAgree()` | — | ✔ |
| `heightAndWeightAreIndependent()` | — | ✔ |

**class `PlayerBirthGetAHWTest.CallSemantics`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `secondCallRerolls()` | — | ✔ |
| `noRaceThrows()` | — | ✔ |


##### `PlayerBirthGetBonusesTest.java`

**Responsible for:** Tests PlayerBirth#getBonuses(Player), the port of C's get_bonuses (player-birth.c).

**class `PlayerBirthGetBonusesTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (7/7 documented): ✔ `HP_PER_LEVEL`, `STALE_MAX`, `STARTING_CURRENT`, `FIRST_SPELL_LEVEL`, `CASTER_LEVEL`, `player`, `realCharacterGenerated`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `casterClass()` | — | ✔ |
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newCharacter()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `expectedMaxHP()` | — | ✔ |

**class `PlayerBirthGetBonusesTest.Flags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bothFlagsConsumed()` | — | ✔ |
| `pendingFlagSurvives()` | — | ✔ |
| `stateRecalculated()` | — | ✔ |

**class `PlayerBirthGetBonusesTest.FullyHealed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `healedToNewMaximum()` | — | ✔ |
| `notHealedToStaleMaximum()` | — | ✔ |
| `raisesRatherThanClamps()` | — | ✔ |

**class `PlayerBirthGetBonusesTest.FullyRested`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noRealmRestsToZero()` | — | ✔ |
| `casterRestsToItsMaximum()` | — | ✔ |
| `notRestedToStaleMaximum()` | — | ✔ |


##### `PlayerBirthGetHistoryTest.java`

**Responsible for:** Tests PlayerBirth#getHistory(PlayerHistoryChart), the port of C's get_history (player-birth.c): while (chart) { int roll = randint1(100); for (entry = chart->entries; entry; entry = entry->next) if (roll roll) break; assert(entry); res = string_append(res, entry->text); chart = entry->succ; } Thr...

**class `PlayerBirthGetHistoryTest`** — class Javadoc: ✔ · type stamp: on 260902

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `chart(int, int, Object)` | — | ✔ |
| `shippedChartOne()` | — | ✔ |
| `shippedChartTwo()` | — | ✔ |
| `rollFor(long)` | — | ✔ |
| `expectedPhrase(PlayerHistoryChart, int)` | — | ✔ |

**class `PlayerBirthGetHistoryTest.Selection`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singleCertainEntry()` | — | ✔ |
| `thresholdWalk()` | — | ✔ |
| `inclusiveAtEachCutoff()` | — | ✔ |
| `allEntriesReachable()` | — | ✔ |
| `seedRolling(int)` | — | ✔ |

**class `PlayerBirthGetHistoryTest.Chain`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `concatenatesInOrder()` | — | ✔ |
| `oneRollPerChart()` | — | ✔ |
| `successorIsPerChart()` | — | ✔ |
| `terminalChart()` | — | ✔ |

**class `PlayerBirthGetHistoryTest.Edges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullChart()` | — | ✔ |
| `rollRunsOffTheEnd()` | — | ✔ |
| `emptyChart()` | — | ✔ |


##### `PlayerBirthGetMoneyTest.java`

**Responsible for:** Tests PlayerBirth#getMoney, the port of C's get_money (player-birth.c), together with the three accessors it drives — Player#getAU(), Player#setAU(int) and Player#setAUBirth(int).

**class `PlayerBirthGetMoneyTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (4/4 documented): ✔ `START_GOLD`, `birth`, `player`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✔ |
| `seedConstants(int)` | — | ✔ |
| `auBirth()` | — | ✔ |
| `seedAndBuild()` | — | ✔ |
| `restoreConstants()` | — | ✔ |

**class `PlayerBirthGetMoneyTest.GetMoney`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `givesShippedStartGold()` | — | ✔ |
| `movesBothOffZero()` | — | ✔ |
| `writesOneFigureToBoth()` | — | ✔ |
| `readsFromTheConstantsTable()` | — | ✔ |
| `passesZeroThrough()` | — | ✔ |
| `carriesUint16Max()` | — | ✔ |
| `overwritesRatherThanAccumulates()` | — | ✔ |
| `eachCharacterHasTheirOwnPurse()` | — | ✔ |

**class `PlayerBirthGetMoneyTest.WorkingPurse`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `storesTheValue()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |
| `holdsTheInt32Range()` | — | ✔ |
| `feedsTheftArithmetic()` | — | ✔ |
| `supportsStoreSpending()` | — | ✔ |

**class `PlayerBirthGetMoneyTest.BirthPurse`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `doesNotTouchWorkingPurse()` | — | ✔ |
| `survivesLaterWorkingWrites()` | — | ✔ |
| `quickstartRestoresBirthCopyOnly()` | — | ✔ |
| `holdsPointBasedFigure()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |


##### `PlayerBirthGetStatsTest.java`

**Responsible for:** Tests PlayerBirth#getStats(Player, Map), the port of C's get_stats (player-birth.c), together with the five accessors it writes the character through — Player#setStatMax(Stats, int), Player#setCurrStatValue(Stats, int), Player#setCurrStatMap(Stats, Stats), Player#setStatBirth(Stats, int) and Play...

**class `PlayerBirthGetStatsTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (13/13 documented): ✔ `STAT_MAX`, `BASE`, `LOWEST_TOTAL`, `HIGHEST_TOTAL`, `LOWEST_STAT`, `HIGHEST_STAT`, `SAMPLES`, `TIMEOUT`, `REAL_STATS`, `RACE_ADJUST`, `CLASS_ADJUST`, `player`, `statUse`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(Map<Stats,Integer>)` | — | ✔ |
| `playerClass(Map<Stats,Integer>)` | — | ✔ |
| `expectedUse(int, int)` | — | ✔ |
| `newCharacter()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `map(String)` | — | ✔ |
| `acceptedTotal()` | — | ✔ |

**class `PlayerBirthGetStatsTest.Accessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setStatMaxWritesTheMaximum()` | — | ✔ |
| `statsAreSeparate()` | — | ✔ |
| `setCurrStatValueWritesTheCurrent()` | — | ✔ |
| `setCurrStatMapStoresTheTarget()` | — | ✔ |
| `setStatBirthIsSeparate()` | — | ✔ |

**class `PlayerBirthGetStatsTest.GetStats`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `statsAreRolledInRange()` | — | ✔ |
| `statsHoldTheAcceptedDice()` | — | ✔ |
| `windowBoundsAreReached()` | — | ✔ |
| `rerollTotalIsResetEachAttempt()` | — | ✔ |
| `currentValuesStartAtTheMaximum()` | — | ✔ |
| `scrambleMapIsTheIdentity()` | — | ✔ |
| `birthRecordMatchesTheRoll()` | — | ✔ |
| `bonusReachesStatUse()` | — | ✔ |
| `playerKeepsTheBareRoll()` | — | ✔ |
| `sentinelsAreSkipped()` | — | ✔ |
| `secondCallRerolls()` | — | ✔ |
| `fillsTheCallersMap()` | — | ✔ |
| `statMaxIsFive()` | — | ✔ |


##### `PlayerBirthIntToRomanTest.java`

**Responsible for:** Tests PlayerBirth.intToRoman(int), the port of C's int_to_roman (player-birth.c).

**class `PlayerBirthIntToRomanTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `intToRoman(int)` | — | ✘ |

**class `PlayerBirthIntToRomanTest.NonPositiveTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zeroAnswersEmptyString()` | — | ✘ |
| `negativeAnswersEmptyString()` | — | ✘ |

**class `PlayerBirthIntToRomanTest.ExactSymbolValueTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plainSymbolExactValueAnswersItself()` | — | ✘ |
| `subtractivePairExactValueAnswersItself()` | — | ✘ |

**class `PlayerBirthIntToRomanTest.CompositeValueTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `repeatedPlainSymbolsAccumulate()` | — | ✘ |
| `mixedValueCombinesPlainAndSubtractiveForms()` | — | ✘ |
| `largestTraditionalValueRepeatsLeadingSymbol()` | — | ✘ |


##### `PlayerBirthLoadRollerDataTest.java`

**Responsible for:** Tests the private PlayerBirth#LoadRollerData, the port of C's load_roller_data (player-birth.c) — the other half of the roller's undo, restoring a Birther snapshot onto the live player and, if the caller wants it, handing back what the player held before.

**class `PlayerBirthLoadRollerDataTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (6/6 documented): ✔ `REAL_STATS`, `START_GOLD`, `playerBirth`, `player`, `realPlayer`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✘ |
| `seedConstants(int)` | — | ✘ |
| `testClass()` | — | ✔ |
| `testRace()` | — | ✔ |
| `savedBirther(PlayerRace, PlayerClass)` | — | ✔ |
| `load(Birther, Birther)` | — | ✔ |
| `currentMap(Stats)` | — | ✔ |
| `seedPriorPlayerState(PlayerRace, PlayerClass)` | — | ✔ |
| `seedAndBuild()` | — | ✘ |
| `restore()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.OrdinaryLoad`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copiesEveryField()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.WeightAndHeightWriteBothFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `writeOneFigureToBoth()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.GoldIsReset`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `auBirthFromSavedAuFromConstants()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.StatsWriteAllFourTargets`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `writesMaxCurrentBirthAndIdentityMap()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.HistorySurvivesTheCapture`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `historyIsSavedsNotNull()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.PrevPlayerNull`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsNullAndStillLoads()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.PrevPlayerCapturesPriorState`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `prevPlayerHoldsThePriorState()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.SavedAndPrevPlayerAreTheSameObject`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `swapsContents()` | — | ✘ |

**class `PlayerBirthLoadRollerDataTest.ReturnsTheSameReference`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `returnsTheSameInstance()` | — | ✘ |


##### `PlayerBirthPlayerGenerateTest.java`

**Responsible for:** Tests PlayerBirth#playerGenerate(Player, PlayerRace, PlayerClass, boolean), the port of C's player_generate (player-birth.c).

**class `PlayerBirthPlayerGenerateTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (5/5 documented): ✔ `PY_FOOD_FULL`, `HUMAN_MHP`, `WARRIOR_MHP`, `savedFoodFull`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedFoodThreshold()` | — | ✔ |
| `restoreFoodThreshold()` | — | ✔ |
| `chart(int, int, Object)` | — | ✔ |
| `race(String, int, int, String)` | — | ✔ |
| `human()` | — | ✔ |
| `playerClass(String, int, int)` | — | ✔ |
| `warrior()` | — | ✔ |
| `build()` | — | ✔ |
| `intField(String)` | — | ✔ |
| `history()` | — | ✔ |

**class `PlayerBirthPlayerGenerateTest.FreshCharacter`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `generate()` | — | ✔ |
| `beginsAtLevelOne()` | — | ✔ |
| `takesExperienceFactor()` | — | ✔ |
| `takesHitDie()` | — | ✔ |
| `seedsTheHitPointTable()` | — | ✔ |
| `startsWithHitPoints()` | — | ✔ |
| `leavesTheRestUnfilled()` | — | ✔ |
| `startsWellFed()` | — | ✔ |
| `rollsAgeHeightWeight()` | — | ✔ |
| `generatesBackground()` | — | ✔ |

**class `PlayerBirthPlayerGenerateTest.RaceAndClass`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `areRecorded()` | — | ✔ |
| `areCopied()` | — | ✔ |
| `nullClassKeepsOwn()` | — | ✔ |
| `nullRaceKeepsOwn()` | — | ✔ |
| `nullPlayerRefused()` | — | ✔ |

**class `PlayerBirthPlayerGenerateTest.Regeneration`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `replacesDerivedFigures()` | — | ✔ |
| `rewritesTheTableEntry()` | — | ✔ |
| `overwritesARolledEntry()` | — | ✔ |

**class `PlayerBirthPlayerGenerateTest.OldHistory`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `keepsTheBackground()` | — | ✔ |
| `replacesTheBackground()` | — | ✔ |
| `doesNotHoldBackTheRest()` | — | ✔ |


##### `PlayerBirthPlayerInitTest.java`

**Responsible for:** Tests PlayerBirth.playerInit, the port of C's player_init (player-birth.c).

**class `PlayerBirthPlayerInitTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/15 documented): ✔ `SEEDED_FIELDS`, `savedStatics` — ✘ `savedMonsterRaces`, `savedTurn`, `player`, `sentinelKind`, `realKind`, `sentinelRace`, `normalRace`, `uniqueRace`, `sentinelLore`, `normalLore`, `uniqueLore`, `testArtifact`, `seededQuest`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `accessibleField(Class<?>, String)` | — | ✘ |
| `readStatic(Class<?>, String)` | — | ✘ |
| `writeStatic(Class<?>, String, Object)` | — | ✘ |
| `readInstance(Object, String)` | — | ✘ |
| `writeInstance(Object, String, Object)` | — | ✘ |
| `invokePlayerInit(Player)` | — | ✘ |
| `plainClass(String)` | — | ✘ |
| `normalShape()` | — | ✘ |
| `artifact()` | — | ✘ |
| `freshLore()` | — | ✘ |
| `makeUnique(MonsterRace)` | — | ✘ |
| `carryCapOnly()` | — | ✘ |
| `seedRegistriesAndRunPlayerInit()` | — | ✘ |
| `restoreRegistries()` | — | ✘ |
| `wipeClearsStaleFields()` | — | ✘ |
| `questsRebuiltFromRegistry()` | — | ✘ |
| `optionsPersistAcrossWipe()` | — | ✘ |
| `kindLoopSkipsIndexZero()` | — | ✘ |
| `kindLoopResetsRealKind()` | — | ✘ |
| `raceLoopSkipsIndexZero()` | — | ✘ |
| `raceLoopResetsNonUnique()` | — | ✘ |
| `raceLoopCapsUniqueAtOne()` | — | ✘ |
| `artifactLoopTouchesEveryEntry()` | — | ✘ |
| `upkeepSizedFromConstants()` | — | ✘ |
| `timedEffectsAllZero()` | — | ✘ |
| `itemKnowledgeRebuilt()` | — | ✘ |
| `turnResetToOne()` | — | ✘ |
| `energyAndRestingTurnReset()` | — | ✘ |
| `defaultsAssigned()` | — | ✔ |


##### `PlayerBirthPlayerMakeSimpleTest.java`

**Responsible for:** Tests PlayerBirth#playerMakeSimple(Player, String, String, String), the port of C's player_make_simple (player-birth.c).

**class `PlayerBirthPlayerMakeSimpleTest`** — class Javadoc: ✔ · type stamp: on 260904

- Fields (2/6 documented): ✔ `RACE_NAMES`, `CLASS_NAMES` — ✘ `savedRaces`, `savedClasses`, `savedCommandQueue`, `queue`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `accessibleField(String)` | — | ✘ |
| `race(String)` | — | ✘ |
| `playerClass(String)` | — | ✘ |
| `seed()` | — | ✘ |
| `restore()` | — | ✘ |
| `executed(CommandCode)` | — | ✔ |

**class `PlayerBirthPlayerMakeSimpleTest.RecordingCommandQueue`** — class Javadoc: ✔ · type stamp: —

- Fields (0/2 documented): ✘ `executedCommands`, `capturedContext`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingCommandQueue()` | — | ✘ |
| `execute(CommandContext)` | — | ✘ (override) |

**class `PlayerBirthPlayerMakeSimpleTest.CommandSequence`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pushesInOrder()` | — | ✘ |
| `executesUnderBirthContext()` | — | ✘ |
| `nullsDefaultToFirstEntryAndSimpleName()` | — | ✘ |
| `givenNameIsUsed()` | — | ✘ |

**class `PlayerBirthPlayerMakeSimpleTest.RaceResolution`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchedNameResolvesToItsIndex()` | — | ✘ |
| `lastEntryResolves()` | — | ✘ |
| `unmatchedNameRefusedBeforeQueueing()` | — | ✘ |

**class `PlayerBirthPlayerMakeSimpleTest.ClassResolution`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchedNameResolvesToItsIndex()` | — | ✘ |
| `matchesClassNameNotPlayerName()` | — | ✘ |
| `unmatchedNameRefusedBeforeQueueing()` | — | ✘ |
| `matchedRaceDoesNotRescueUnmatchedClass()` | — | ✘ |


##### `PlayerBirthPlayerOutfitTest.java`

**Responsible for:** Tests PlayerBirth#playerOutfit(Player), the port of C's player_outfit (player-birth.c).

**class `PlayerBirthPlayerOutfitTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (0/3 documented): ✘ `TOUCHED`, `saved`, `savedGamePlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `snapshot()` | — | ✘ |
| `restore()` | — | ✘ |
| `field(String)` | — | ✘ |
| `kind(TValue, String, int)` | — | ✔ |
| `startItem(TValue, String, int, int)` | — | ✔ |
| `classWithItems(List<StartItem>)` | — | ✔ |
| `setOption(Player, PlayerOptionEnum, boolean)` | — | ✔ |
| `gearContainsKind(Player, ObjectKind)` | — | ✘ |
| `gearItemOfKind(Player, ObjectKind)` | — | ✘ |
| `isolateRegistry()` | — | ✘ |
| `buildPlayer(List<StartItem>)` | — | ✔ |
| `obviousKnowledgeAlwaysSet()` | — | ✘ |
| `obviousFlagLearnedForMatchingSubtype()` | — | ✘ |
| `flagWithOtherSubtypeIsNotLearned()` | — | ✘ |
| `flagWithNoPropertyIsSkippedWithoutThrowing()` | — | ✘ |
| `ordinaryStartItemIsBoughtAndCarried()` | — | ✘ |
| `knownShadowCopiesPvalNotNumber()` | — | ✔ |
| `withoutStartKitOnlyFoodIsGrantedAtOne()` | — | ✘ |
| `goldClampedToZeroWhenKitIsExpensive()` | — | ✘ |
| `missingKindThrows()` | — | ✘ |

**class `PlayerBirthPlayerOutfitTest.ExclusionOptions`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `scrollKind`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registerScroll()` | — | ✘ |
| `buildWithExclusion(boolean, boolean)` | — | ✘ |
| `nonNegatedOptionSetIsExcluded()` | — | ✘ |
| `nonNegatedOptionUnsetIsIncluded()` | — | ✘ |
| `negatedOptionSetIsIncluded()` | — | ✘ |
| `negatedOptionUnsetIsExcluded()` | — | ✘ |


##### `PlayerBirthRecalculateStatsTest.java`

**Responsible for:** Tests PlayerBirth#recalculateStats(Map, int), the port of C's recalculate_stats (player-birth.c).

**class `PlayerBirthRecalculateStatsTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (8/8 documented): ✔ `START_GOLD`, `ORDINARY_STATS`, `player`, `realPlayer`, `bus`, `realBus`, `realCharacterGenerated`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✔ |
| `seedConstants(int)` | — | ✔ |
| `statsMap(int, int, int, int, int)` | — | ✔ |
| `auBirth()` | — | ✔ |
| `statsBirth()` | — | ✔ |
| `statMap()` | — | ✔ |
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newCharacter()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |

**class `PlayerBirthRecalculateStatsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerBirthRecalculateStatsTest.StatLoop`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `writesEachRealStat()` | — | ✔ |
| `skipsTheSentinels()` | — | ✔ |
| `ignoresSentinelEntriesEvenIfSupplied()` | — | ✔ |

**class `PlayerBirthRecalculateStatsTest.Gold`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `multipliesPointsLeftByFifty()` | — | ✔ |
| `zeroPointsLeftAddsNothing()` | — | ✔ |
| `onePointLeftIsTheAmbiguousCase()` | — | ✔ |
| `readsStartGoldFromConstants()` | — | ✔ |

**class `PlayerBirthRecalculateStatsTest.DownstreamCalls`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `callsGetBonuses()` | — | ✔ |
| `signalsFourEventsInOrder()` | — | ✔ |


##### `PlayerBirthResetStatsTest.java`

**Responsible for:** Tests PlayerBirth#resetStats(Map, Map, Map, int, boolean), the port of C's reset_stats (player-birth.c).

**class `PlayerBirthResetStatsTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (10/10 documented): ✔ `START_GOLD`, `MAX_BIRTH_POINTS`, `COST_AT_TEN`, `playerBirth`, `player`, `realPlayer`, `bus`, `realBus`, `realCharacterGenerated`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✔ |
| `seedConstants(int)` | — | ✔ |
| `statsMap(int, int, int, int, int)` | — | ✔ |
| `assertAllRealStats(Map<Stats,Integer>, int, String)` | — | ✔ |
| `auBirth()` | — | ✔ |
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newCharacter()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |

**class `PlayerBirthResetStatsTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `events`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerBirthResetStatsTest.StatReset`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `overwritesRegardlessOfLeftoverValues()` | — | ✔ |
| `skipsTheSentinels()` | — | ✔ |
| `leavesSentinelEntriesUntouchedIfPresent()` | — | ✔ |

**class `PlayerBirthResetStatsTest.PointsLeftReturn`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ignoresALowIncomingValue()` | — | ✔ |
| `ignoresAHighIncomingValue()` | — | ✔ |

**class `PlayerBirthResetStatsTest.UpdateDisplayGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `recalculatesAndSignalsUsingTheResetTotal()` | — | ✔ |
| `doesNeitherWhenClear()` | — | ✔ |


##### `PlayerBirthRollHPTest.java`

**Responsible for:** Tests PlayerBirth#rollHP(Player), the port of C's roll_hp (player-birth.c), together with the two accessors it drives — Player#getHitDie() and Player#setPlayerHitpoint(int, int).

**class `PlayerBirthRollHPTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (2/2 documented): ✔ `PY_MAX_LEVEL`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build()` | — | ✔ |
| `setHitDie(int)` | — | ✔ |
| `generate(int)` | — | ✔ |

**class `PlayerBirthRollHPTest.Accessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getHitDieReturnsTheDie()` | — | ✔ |
| `getHitDieStartsAtZero()` | — | ✔ |
| `setterAndGetterAgree()` | — | ✔ |
| `setterDoesNotAccumulate()` | — | ✔ |

**class `PlayerBirthRollHPTest.RollHP`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oneSidedDieIsDetermined()` | — | ✔ |
| `leavesIndexZeroAlone()` | — | ✔ |
| `doesNotWritePastTheLastLevel()` | — | ✔ |
| `everyStepIsOneRoll()` | — | ✔ |
| `topEntryLandsInTheWindow()` | — | ✔ |
| `tenSidedDieWindow()` | — | ✔ |
| `secondCallRerolls()` | — | ✔ |
| `maxLevelIsFifty()` | — | ✔ |


##### `PlayerBirthRomanToIntTest.java`

**Responsible for:** Tests PlayerBirth.value(char) and PlayerBirth.romanToInt(String), the port of C's single-letter lookup embedded in roman_token_vals[*][0] and of roman_to_int itself (player-birth.c).

**class `PlayerBirthRomanToIntTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `value(char)` | — | ✘ |
| `romanToInt(String)` | — | ✘ |

**class `PlayerBirthRomanToIntTest.ValueTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `canonicalLettersAnswerTheirValue()` | — | ✘ |
| `unrecognisedLetterAnswersMinusOne()` | — | ✘ |
| `lowercaseLetterAnswersMinusOne()` | — | ✘ |

**class `PlayerBirthRomanToIntTest.MalformedInputTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyStringAnswersMinusOne()` | — | ✘ |
| `invalidLetterAnswersMinusOne()` | — | ✘ |
| `lowercaseNumeralAnswersMinusOne()` | — | ✘ |

**class `PlayerBirthRomanToIntTest.BoundaryTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `romanToIntOfSingleLetterMatchesValue()` | — | ✘ |
| `romanToIntOfRepeatedNonPairLetters()` | — | ✘ |
| `nonPartnerFollowerAddsBothPlainly()` | — | ✘ |

**class `PlayerBirthRomanToIntTest.SubtractivePairTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `canonicalPairsSubtract()` | — | ✘ |
| `fullNumeralCombinesPairsAndPlainLetters()` | — | ✘ |

**class `PlayerBirthRomanToIntTest.NonPairLeadingLetterTests`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `vNeverLeadsAPair()` | — | ✘ |
| `lNeverLeadsAPair()` | — | ✘ |
| `iOnlySubtractsAgainstItsOwnTwoPartners()` | — | ✘ |


##### `PlayerBirthSaveRollerDataTest.java`

**Responsible for:** Tests the private PlayerBirth#saveRollerData, the port of C's save_roller_data (player-birth.c) — the snapshot the birth process keeps for undo and quickstart.

**class `PlayerBirthSaveRollerDataTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (4/4 documented): ✔ `REAL_STATS`, `playerBirth`, `player`, `realPlayer`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `testClass()` | — | ✔ |
| `accessibleField(Class<?>, String)` | — | ✘ |
| `writeInstance(Object, String, Object)` | — | ✘ |
| `save(Birther)` | — | ✔ |
| `seedBirthStats(int, int, int, int, int)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `restorePlayer()` | — | ✘ |

**class `PlayerBirthSaveRollerDataTest.OrdinaryPath`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copiesEveryField()` | — | ✘ |
| `returnsTheSameInstance()` | — | ✔ |

**class `PlayerBirthSaveRollerDataTest.BirthCopiesNotLive`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `weightIsTheBirthCopy()` | — | ✘ |
| `heightIsTheBirthCopy()` | — | ✘ |
| `goldIsTheBirthCopy()` | — | ✘ |
| `statsAreTheBirthCopy()` | — | ✘ |

**class `PlayerBirthSaveRollerDataTest.StatLoopBounds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copiesExactlyFiveRealStats()` | — | ✘ |

**class `PlayerBirthSaveRollerDataTest.HistoryHandoff`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `backgroundTextIsCopied()` | — | ✘ |
| `playersCopyIsNulledAfterward()` | — | ✘ |
| `aSecondRollIsStillCopied()` | — | ✔ |


##### `PlayerBirthSellStatTest.java`

**Responsible for:** Tests PlayerBirth#sellStat(Stats, Map, Map, Map, int, boolean), the port of C's sell_stat (player-birth.c).

**class `PlayerBirthSellStatTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (11/11 documented): ✔ `START_GOLD`, `MAX_BIRTH_POINTS`, `COST_FLAT`, `COST_AT_SEVENTEEN`, `COST_AT_EIGHTEEN`, `player`, `realPlayer`, `bus`, `realBus`, `realCharacterGenerated`, `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✔ |
| `seedConstants(int)` | — | ✔ |
| `statsMap(int, int, int, int, int)` | — | ✔ |
| `pointsLeftOf(Object)` | — | ✔ |
| `succeededOf(Object)` | — | ✔ |
| `call(Stats, Map<Stats,Integer>, Map<Stats,Integer>, Map<Stats,Integer>, int, boolean)` | — | ✔ |
| `auBirth()` | — | ✔ |
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newCharacter()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |

**class `PlayerBirthSellStatTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `events`, `payloads`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerBirthSellStatTest.OrdinaryPurchase`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lowersTheChosenStat()` | — | ✔ |

**class `PlayerBirthSellStatTest.StatTargeting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sellsConstitutionNotItsNeighbour()` | — | ✔ |

**class `PlayerBirthSellStatTest.InvalidChoice`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertRejectedCleanly(Stats)` | — | ✔ |
| `rejectsStatNone()` | — | ✔ |
| `rejectsStatMax()` | — | ✔ |

**class `PlayerBirthSellStatTest.StatAlreadyAtFloor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `refusesToSellBelowTen()` | — | ✔ |

**class `PlayerBirthSellStatTest.Boundary18`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sellsFromTheCapWithoutThrowing()` | — | ✔ |

**class `PlayerBirthSellStatTest.UpdateDisplayGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `signalsThenRecalculatesUsingThePostSaleTotal()` | — | ✔ |
| `doesNeitherWhenClear()` | — | ✔ |


##### `PlayerBirthStateRegistryTest.java`

**Responsible for:** Tests PlayerBirthStateRegistry, the port of the block of C statics at player-birth.c - stats, points_spent, points_inc, points_left, quickstart_allowed, rolled_stats, prev and quickstart_prev.

**class `PlayerBirthStateRegistryTest`** — class Javadoc: ✔ · type stamp: on 260907

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(String)` | — | ✔ |
| `resetRegistry()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.StatsMap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToMinusOne()` | — | ✔ |
| `roundTripsFromNull()` | — | ✔ |
| `keepsStatsIndependent()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.StatsWholeMap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lazilyCreatesEmptyMap()` | — | ✔ |
| `returnsTheSameInstance()` | — | ✔ |
| `roundTrips()` | — | ✔ |
| `reflectsSetStat()` | — | ✔ |
| `directMutationIsVisible()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.PointsSpentMap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToMinusOne()` | — | ✔ |
| `roundTripsFromNull()` | — | ✔ |
| `keepsStatsIndependent()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.PointsSpentWholeMap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lazilyCreatesEmptyMap()` | — | ✔ |
| `roundTrips()` | — | ✔ |
| `reflectsSingleStatWrite()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.PointsIncMap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToMinusOne()` | — | ✔ |
| `roundTripsFromNull()` | — | ✔ |
| `keepsStatsIndependent()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.PointsIncWholeMap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lazilyCreatesEmptyMap()` | — | ✔ |
| `roundTrips()` | — | ✔ |
| `reflectsSingleStatWrite()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.WholeMapSetterInitOrdering`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setStatsSurvivesLaterGetPointsInc()` | — | ✔ |
| `setPointsSpentSurvivesLaterGetStats()` | — | ✔ |
| `setPointsIncSurvivesLaterGetStats()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.PointsLeft`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToZero()` | — | ✔ |
| `roundTrips()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.QuickstartAllowed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToFalse()` | — | ✔ |
| `roundTripsTrue()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.RolledStats`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToFalse()` | — | ✔ |
| `roundTripsTrue()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.Init`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `createsTheMapsFreshOnFirstCall()` | — | ✔ |
| `secondCallDoesNotClearExistingData()` | — | ✔ |
| `leavesTheOtherFieldsUntouched()` | — | ✔ |
| `doesNotThrowFromNull()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.Prev`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToAFreshZeroAgeSnapshot()` | — | ✔ |
| `roundTrips()` | — | ✔ |

**class `PlayerBirthStateRegistryTest.QuickstartPrev`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `defaultsToAFreshZeroAgeSnapshot()` | — | ✔ |
| `roundTrips()` | — | ✔ |
| `independentOfPrev()` | — | ✔ |


##### `PlayerBirthWieldAllTest.java`

**Responsible for:** Tests PlayerBirth's wieldAll, the port of C's wield_all (player-birth.c).

**class `PlayerBirthWieldAllTest`** — class Javadoc: ✔ · type stamp: on 260905

- Fields (3/4 documented): ✔ `savedPlayer`, `player`, `savedProperties` — ✘ `savedPropertyMax`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `indexOfType(PlayerBody, EquipmentSlotsEnum)` | — | ✔ |
| `bodyWithout(EquipmentSlotsEnum)` | — | ✔ |
| `singleSlotBody(EquipmentSlotsEnum)` | — | ✔ |
| `sword()` | — | ✔ |
| `amulet()` | — | ✔ |
| `potion()` | — | ✔ |
| `lights(int)` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `wieldAll()` | — | ✔ |
| `wearsAWieldableItemIntoItsSlot()` | — | ✔ |
| `leavesAnUnwieldableItemInGear()` | — | ✔ |
| `skipsAnAlreadyOccupiedSlot()` | — | ✔ |
| `onlyOneOfTwoCompetingItemsEndsUpWorn()` | — | ✔ |
| `skipsAnItemWithNoMatchingSlotType()` | — | ✔ |
| `splitsAStackAndKeepsTheRemainderInGear()` | — | ✔ |


##### `PlayerBodyCopyTest.java`

**Responsible for:** Tests PlayerBody#copy, which stands in for the copying half of C's player_embody (player-birth.c:369).

**class `PlayerBodyCopyTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (1/1 documented): ✔ `source`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wear(EquipSlot, ItemObject)` | — | ✔ |
| `bodyOf(EquipSlot)` | — | ✔ |
| `setUp()` | — | ✘ |

**class `PlayerBodyCopyTest.TheLayoutIsCarriedAcross`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theNameIsCopied()` | — | ✘ |
| `theSlotCountIsCopied()` | — | ✘ |
| `eachSlotKeepsItsTypeAndName()` | — | ✘ |
| `theSlotOrderIsPreserved()` | — | ✔ |
| `aBodyLookedUpByNameOnTheCopyFindsTheSameSlot()` | — | ✘ |
| `aSingleSlotBodyCopiesAsWell()` | — | ✘ |

**class `PlayerBodyCopyTest.NothingIsSharedWithTheSource`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theCopyIsADifferentBody()` | — | ✘ |
| `everySlotIsANewObject()` | — | ✘ |
| `theSlotListIsNotShared()` | — | ✘ |
| `wearingOnTheCopyDoesNotAffectTheSource()` | — | ✔ |
| `wearingOnTheSourceDoesNotAffectAnEarlierCopy()` | — | ✔ |
| `twoCopiesOfOneTemplateAreIndependent()` | — | ✔ |
| `aCopyOfACopyIsStillIndependent()` | — | ✔ |
| `theNameMayBeSharedBecauseStringsAreImmutable()` | — | ✔ |

**class `PlayerBodyCopyTest.TheCopyIsUnworn`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everySlotOfACopyStartsEmpty()` | — | ✘ |
| `anItemInTheSourceIsNotCarriedOver()` | — | ✘ |
| `aFullyEquippedSourceCopiesToAnEmptyBody()` | — | ✔ |
| `anEquippedSourceStillCopiesItsLayout()` | — | ✘ |


##### `PlayerBodyEquippedSlotTest.java`

**Responsible for:** Tests PlayerBody#equippedItemSlot, the port of C's equipped_item_slot (player-calcs.c), and the null-item behaviour of PlayerBody#itemIsEquipped, the port of object_is_equipped (obj-gear.c).

**class `PlayerBodyEquippedSlotTest`** — class Javadoc: ✔ · type stamp: on 261010

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `slotOf(EquipmentSlotsEnum, String, ItemObject)` | — | ✘ |
| `bodyOf(EquipSlot)` | — | ✘ |
| `kitted(ItemObject, ItemObject, ItemObject, ItemObject)` | — | ✔ |
| `wornItemReturnsItsSlotIndex()` | — | ✘ |
| `emptySlotsStillCountTowardTheIndex()` | — | ✘ |
| `unwornItemReturnsCount()` | — | ✘ |
| `identicalTwinReturnsCount()` | — | ✘ |
| `nullItemReturnsCountDespiteEmptySlots()` | — | ✘ |
| `firstMatchWins()` | — | ✘ |
| `lastSlotIsDistinctFromNotWorn()` | — | ✘ |
| `singleSlotBody()` | — | ✘ |
| `nullIsEquippedWhenThereIsAGap()` | — | ✘ |
| `nullIsNotEquippedWhenFull()` | — | ✘ |
| `nullDisagreementIsPreserved()` | — | ✔ |
| `equippedAgreesWithSlotIndex()` | — | ✘ |
| `notWornIndexIsOutOfRangeForGetSlot()` | — | ✘ |


##### `PlayerBodyEquippedTest.java`

**Responsible for:** Tests PlayerBody#itemIsEquipped, the port of C's object_is_equipped (obj-gear.c).

**class `PlayerBodyEquippedTest`** — class Javadoc: ✔ · type stamp: on 260816

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wear(EquipSlot, ItemObject)` | — | ✔ |
| `emptySlot()` | — | ✔ |
| `bodyOf(EquipSlot)` | — | ✘ |
| `wornItemIsEquipped()` | — | ✘ |
| `unwornItemIsNotEquipped()` | — | ✘ |
| `emptySlotBeforeMatchIsSkipped()` | — | ✔ |
| `whollyEmptyBodyIsSurvivable()` | — | ✔ |
| `slotlessBodyIsRefused()` | — | ✔ |
| `matchInLastSlotIsFound()` | — | ✔ |
| `identicalButDistinctObjectIsNotEquipped()` | — | ✔ |


##### `PlayerBodySlotNameTest.java`

**Responsible for:** Tests PlayerBody#equippedItemBySlotName, the port of C's equipped_item_by_slot_name (obj-gear.c:127).

**class `PlayerBodySlotNameTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (1/1 documented): ✔ `body`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wear(EquipSlot, ItemObject)` | — | ✔ |
| `bodyOf(EquipSlot)` | — | ✔ |
| `setUp()` | — | ✘ |

**class `PlayerBodySlotNameTest.FindingAWornItem`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theItemInTheNamedSlotIsReturned()` | — | ✘ |
| `eachNameFindsItsOwnSlot()` | — | ✘ |
| `theItemIsReturnedByIdentity()` | — | ✔ |
| `aLaterNamedSlotIsFoundAsReadilyAsAnEarlyOne()` | — | ✘ |

**class `PlayerBodySlotNameTest.SlotsSharingAType`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theTwoRingSlotsAreDistinguishedByName()` | — | ✘ |
| `oneFilledRingSlotDoesNotAnswerForTheOther()` | — | ✘ |
| `theFirstOfTwoSameNamedSlotsWins()` | — | ✔ |

**class `PlayerBodySlotNameTest.TheNullAnswers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anEmptySlotAnswersNull()` | — | ✘ |
| `anUnknownNameAnswersNullRatherThanThrowing()` | — | ✘ |
| `aNullNameAnswersNullRatherThanThrowing()` | — | ✘ |
| `anUnknownNameAnswersNullEvenOnAFullyEquippedBody()` | — | ✔ |
| `anEmptyStringIsJustAnUnknownName()` | — | ✘ |

**class `PlayerBodySlotNameTest.MatchingIsExact`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theMatchIsCaseSensitive()` | — | ✘ |
| `aPrefixOfASlotNameDoesNotMatch()` | — | ✘ |
| `surroundingWhitespaceIsNotTrimmed()` | — | ✘ |
| `aMultiWordSlotNameMatchesInFull()` | — | ✘ |


##### `PlayerCalcBlowsTest.java`

**Responsible for:** Tests PlayerCalcs#calcBlows, the port of C's calc_blows (player-calcs.c): blows per turn, scaled by 100, from strength, dexterity, the weapon's weight and the class.

**class `PlayerCalcBlowsTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (2/2 documented): ✔ `player`, `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `indices(int, int)` | — | ✔ |
| `weapon(int)` | — | ✔ |
| `percentDamage()` | — | ✔ |

**class `PlayerCalcBlowsTest.TableLookup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unarmed()` | — | ✔ |
| `heavyWeapon()` | — | ✔ |
| `lowDexterity()` | — | ✔ |

**class `PlayerCalcBlowsTest.MinimumWeight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unarmedUsesTheMinimum()` | — | ✔ |
| `lighterThanTheMinimum()` | — | ✔ |
| `atTheMinimum()` | — | ✔ |
| `justOverTheMinimum()` | — | ✔ |
| `heavy()` | — | ✔ |

**class `PlayerCalcBlowsTest.Ceilings`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `strengthRungClamped()` | — | ✔ |
| `strongestIndexSameRow()` | — | ✔ |
| `classCeiling()` | — | ✔ |
| `extraBlowsPassTheCeiling()` | — | ✔ |

**class `PlayerCalcBlowsTest.ExtraAndFloor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `extraBlowAddsAHundred()` | — | ✔ |
| `floorIsOneBlow()` | — | ✔ |
| `percentDamageFloor()` | — | ✔ |
| `percentDamageLeavesLargeTotals()` | — | ✔ |


##### `PlayerCalcBonusesDerivedTest.java`

**Responsible for:** Tests the parts of PlayerCalcs#calcBonuses that turn on arithmetic rather than on what a single item contributes: the stat-to-index conversion, the speed penalty for weight, hunger, the timed statuses, vulnerabilities, and the launcher and weapon analysis.

**class `PlayerCalcBonusesDerivedTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (7/7 documented): ✔ `PROBE`, `AT_TEN`, `fixture`, `savedEffects`, `savedCharacterDungeon`, `FOOD_THRESHOLDS`, `savedThresholds`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `thresholdField(String)` | — | ✔ |
| `grade(int, int, String)` | — | ✔ |
| `effect(TimedEffect, ElementEnum, TimedGrade)` | — | ✔ |
| `classWith(Map<PlayerSkill,Integer>, Map<PlayerSkill,Integer>, PlayerFlag)` | — | ✔ |
| `probeClass(PlayerFlag)` | — | ✔ |
| `total(PlayerState, String)` | — | ✔ |
| `level(int)` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.StatIndex`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `indexOf(int)` | — | ✔ |
| `three()` | — | ✔ |
| `four()` | — | ✔ |
| `eighteen()` | — | ✔ |
| `firstBand()` | — | ✔ |
| `secondBand()` | — | ✔ |
| `lastBanded()` | — | ✔ |
| `top()` | — | ✔ |
| `useAndTop()` | — | ✔ |
| `bonusCrossesEighteen()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Hypothetical`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nudged()` | — | ✔ |
| `lowerClamp()` | — | ✔ |
| `upperClamp()` | — | ✔ |
| `realPassNotNudged()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.StatTables`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `strongAndNimble()` | — | ✔ |
| `weakAndClumsy()` | — | ✔ |
| `skillTables()` | — | ✔ |
| `perLevelSkills()` | — | ✔ |
| `stealthClamped()` | — | ✔ |
| `diggingFloor()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Weight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `speedAt(int)` | — | ✔ |
| `halfTheLimit()` | — | ✔ |
| `justOverHalf()` | — | ✔ |
| `onePoint()` | — | ✔ |
| `atTheLimit()` | — | ✔ |
| `floor()` | — | ✔ |
| `cap()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Vulnerabilities`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fireVulnerableRace()` | — | ✔ |
| `ringOfFire(int)` | — | ✔ |
| `itemVulnerability()` | — | ✔ |
| `racialVulnerability()` | — | ✔ |
| `cancelsResistance()` | — | ✔ |
| `immunityKept()` | — | ✔ |
| `countedOnce()` | — | ✔ |
| `betterOfTwo()` | — | ✔ |
| `unlightInDungeon()` | — | ✔ |
| `unlightBeforeDungeon()` | — | ✔ |
| `evilInDungeon()` | — | ✔ |
| `evilBeforeDungeon()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Hunger`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `at(int)` | — | ✔ |
| `fed()` | — | ✔ |
| `topOfHungry()` | — | ✔ |
| `lackSix()` | — | ✔ |
| `lackTen()` | — | ✔ |
| `lackEleven()` | — | ✔ |
| `lackFifteen()` | — | ✔ |
| `lackSixteen()` | — | ✔ |
| `lackEighteen()` | — | ✔ |
| `lackNineteen()` | — | ✔ |
| `starving()` | — | ✔ |
| `edgeOfFull()` | — | ✔ |
| `littleOverFed()` | — | ✔ |
| `maximum()` | — | ✔ |
| `vampire()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Statuses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `with(TimedEffect, int)` | — | ✔ |
| `blessed()` | — | ✔ |
| `heroism()` | — | ✔ |
| `compound()` | — | ✔ |
| `berserk()` | — | ✔ |
| `armourStatuses()` | — | ✔ |
| `speedStatuses()` | — | ✔ |
| `fastAndSprint()` | — | ✔ |
| `fastAndSlow()` | — | ✔ |
| `devicePenalties()` | — | ✔ |
| `bloodlust()` | — | ✔ |
| `stealth()` | — | ✔ |
| `dormant()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Stun`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `at(int)` | — | ✔ |
| `topOfStun()` | — | ✔ |
| `startOfHeavyStun()` | — | ✔ |
| `knockedOut()` | — | ✔ |
| `realPassCancelsFastCasting()` | — | ✔ |
| `hypotheticalLeavesFastCasting()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.TemporaryResistance`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ringOfFire(int)` | — | ✔ |
| `bare()` | — | ✔ |
| `stacksOnResistance()` | — | ✔ |
| `capAtTwo()` | — | ✔ |
| `immunityUntouched()` | — | ✔ |
| `dormant()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Fear`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `afraid()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Launcher`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bow(int, int)` | — | ✔ |
| `none()` | — | ✔ |
| `ordinaryBow()` | — | ✔ |
| `extras()` | — | ✔ |
| `fastShot()` | — | ✔ |
| `floor()` | — | ✔ |
| `justUnder()` | — | ✔ |
| `overTheHold()` | — | ✔ |

**class `PlayerCalcBonusesDerivedTest.Weapon`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sword(int)` | — | ✔ |
| `unarmed()` | — | ✔ |
| `usableWeapon()` | — | ✔ |
| `heavyWeapon()` | — | ✔ |
| `atTheHold()` | — | ✔ |
| `hafted()` | — | ✔ |
| `plainSword()` | — | ✔ |
| `blessedSword()` | — | ✔ |
| `noClassFlag()` | — | ✔ |


##### `PlayerCalcBonusesTest.java`

**Responsible for:** Tests PlayerCalcs#calcBonuses, the port of C's calc_bonuses (player-calcs.c) — the derivation every other number about a character rests on.

**class `PlayerCalcBonusesTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `combatTotal(PlayerState, String)` | — | ✔ |
| `curse(Map<ObjectModifier,Integer>, int, int, int)` | — | ✔ |

**class `PlayerCalcBonusesTest.Defaults`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseDefaults()` | — | ✔ |
| `nonCasterHasNoMana()` | — | ✔ |
| `recalculationIsIdempotent()` | — | ✔ |

**class `PlayerCalcBonusesTest.Equipment`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `modifiersNeedTheirRune()` | — | ✔ |
| `baseArmourAndBonusAreSeparate()` | — | ✔ |
| `weaponSlotWithholdsCombatBonuses()` | — | ✔ |
| `lightRadius()` | — | ✔ |
| `burntOutLightGivesNothing()` | — | ✔ |

**class `PlayerCalcBonusesTest.Curses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curseModifiersApply()` | — | ✔ |
| `zeroPowerCurseIsInert()` | — | ✔ |
| `itemPassStillRuns()` | — | ✔ |
| `curseUnderKnownOnly()` | — | ✔ |

**class `PlayerCalcBonusesTest.KnownOnly`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unidentifiedEnchantmentIsWithheld()` | — | ✔ |
| `learnedEnchantmentCounts()` | — | ✔ |


##### `PlayerCalcHitpointsTest.java`

**Responsible for:** Tests PlayerCalcs#calcHitpoints(), the port of C's calc_hitpoints (player-calcs.c) — the maximum every wound, potion and killing blow is measured against.

**class `PlayerCalcHitpointsTest`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (4/4 documented): ✔ `CON_WORST`, `CON_NEUTRAL`, `CON_STRONG`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `set(String, Object)` | — | ✔ |
| `read(String)` | — | ✔ |
| `character(int, int, int)` | — | ✔ |
| `hitPoints(int, int, int)` | — | ✔ |
| `repaintAsked()` | — | ✔ |
| `buildPlayer()` | — | ✘ |

**class `PlayerCalcHitpointsTest.Maximum`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `neutralConstitution()` | — | ✔ |
| `constitutionScalesWithLevel()` | — | ✔ |
| `negativeBonusTruncatesTowardZero()` | — | ✔ |
| `levelFloor()` | — | ✔ |

**class `PlayerCalcHitpointsTest.CurrentTotal`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `belowIsUntouched()` | — | ✔ |
| `aboveIsClamped()` | — | ✔ |
| `exactlyAtMaximumLosesItsFraction()` | — | ✔ |

**class `PlayerCalcHitpointsTest.ChangedGuard`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unchangedMaximumDoesNothing()` | — | ✔ |
| `changedMaximumAsksForARepaint()` | — | ✔ |


##### `PlayerCalcInventoryTest.java`

**Responsible for:** Tests PlayerCalcs#calcInventory, the port of C's calc_inventory (player-calcs.c): the rebuild of the pack and quiver views over the gear.

**class `PlayerCalcInventoryTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (7/7 documented): ✔ `PACK_SIZE`, `player`, `bus`, `realBus`, `savedPlayer`, `savedCharacterDungeon`, `fixture`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |

**class `PlayerCalcInventoryTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |
| `arrows(int, int)` | — | ✔ |
| `daggers(int)` | — | ✔ |
| `potion(int)` | — | ✔ |
| `carry(ItemObject)` | — | ✔ |

**class `PlayerCalcInventoryTest.Ammunition`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstSlot()` | — | ✔ |
| `sortedOrder()` | — | ✔ |
| `inscribedSlot()` | — | ✔ |
| `contestedSlot()` | — | ✔ |
| `oversizeStack()` | — | ✔ |
| `exactlyOneSlot()` | — | ✔ |
| `idempotent()` | — | ✔ |

**class `PlayerCalcInventoryTest.Thrown`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `uninscribed()` | — | ✔ |
| `inscribed()` | — | ✔ |
| `oversize()` | — | ✔ |
| `splitDoesNotSkipTheNextItem()` | — | ✔ |
| `ordinaryItem()` | — | ✔ |

**class `PlayerCalcInventoryTest.Pack`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sorted()` | — | ✔ |
| `equalItemsKeepGearOrder()` | — | ✔ |
| `equipmentExcluded()` | — | ✔ |
| `overfullByOne()` | — | ✔ |
| `overfullByTwo()` | — | ✔ |
| `tooMuchGear()` | — | ✔ |

**class `PlayerCalcInventoryTest.Messages`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `packReordered()` | — | ✔ |
| `packUnchanged()` | — | ✔ |
| `silentBeforeDungeon()` | — | ✔ |
| `countChanged()` | — | ✔ |
| `wieldedNotMoved()` | — | ✔ |
| `quiverReordered()` | — | ✔ |
| `quiverUnchanged()` | — | ✔ |


##### `PlayerCalcLightTest.java`

**Responsible for:** Tests PlayerCalcs#calcLight, the port of C's calc_light (player-calcs.c): the radius of light the player sheds, summed over everything worn.

**class `PlayerCalcLightTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (6/6 documented): ✔ `DAY_LENGTH`, `FIRST_NIGHT_TURN`, `fixture`, `state`, `savedConstants`, `savedTurn`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsField()` | — | ✔ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✔ |
| `calculate()` | — | ✔ |
| `light(int, ObjectFlag)` | — | ✔ |
| `ringOfLight(int)` | — | ✔ |

**class `PlayerCalcLightTest.EquipmentSum`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nothingWorn()` | — | ✔ |
| `lightTwo()` | — | ✔ |
| `lightThree()` | — | ✔ |
| `bothFlags()` | — | ✔ |
| `modifierAdds()` | — | ✔ |
| `ringOnly()` | — | ✔ |
| `slotsAdd()` | — | ✔ |
| `negativeModifier()` | — | ✔ |
| `totalCanBeNegative()` | — | ✔ |

**class `PlayerCalcLightTest.Unlight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `positiveModifierLosesAPoint()` | — | ✔ |
| `comesOffOnce()` | — | ✔ |
| `innateRadiusUntouched()` | — | ✔ |
| `negativeModifierNotSoftened()` | — | ✔ |

**class `PlayerCalcLightTest.Fuel`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `burntOutLosesEverything()` | — | ✔ |
| `oneTurnOfFuel()` | — | ✔ |
| `noFuelNeeded()` | — | ✔ |
| `nonLightIgnoresTimeout()` | — | ✔ |
| `otherSlotsSurvive()` | — | ✔ |

**class `PlayerCalcLightTest.TownByDay`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `inTownAt(int)` | — | ✔ |
| `daytimeGivesZero()` | — | ✔ |
| `lastTurnOfTheDay()` | — | ✔ |
| `firstTurnOfTheNight()` | — | ✔ |
| `cycleRepeats()` | — | ✔ |
| `dungeonIgnoresTheClock()` | — | ✔ |
| `hypotheticalIgnoresDaytime()` | — | ✔ |
| `changedRadiusRaisesFlags()` | — | ✔ |
| `unchangedRadiusRaisesNothing()` | — | ✔ |
| `noStateYet()` | — | ✔ |


##### `PlayerCalcManaTest.java`

**Responsible for:** Tests PlayerCalcs#calcMana, the port of C's calc_mana (player-calcs.c).

**class `PlayerCalcManaTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `SPELL_WEIGHT`, `FIRST_SPELL_LEVEL`, `player`, `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `casterClass(int)` | — | ✔ |
| `newCaster()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `intField(String)` | — | ✔ |
| `wearBodyArmour(int)` | — | ✔ |
| `wearWeapon(int)` | — | ✔ |
| `wear(EquipmentSlotsEnum, int)` | — | ✔ |

**class `PlayerCalcManaTest.NonCasters`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `spelllessClassHasNoMana()` | — | ✔ |
| `tooLowLevelHasNoMana()` | — | ✔ |
| `firstSpellLevelHasMana()` | — | ✔ |

**class `PlayerCalcManaTest.Encumbrance`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nothingWornIsNotEncumbering()` | — | ✔ |
| `lightArmourIsNotEncumbering()` | — | ✔ |
| `heavyArmourCostsMana()` | — | ✔ |
| `weaponIsExempt()` | — | ✔ |
| `manaNeverGoesNegative()` | — | ✔ |

**class `PlayerCalcManaTest.Updating`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withoutUpdatingNothingIsStored()` | — | ✔ |
| `encumbranceIsWrittenRegardless()` | — | ✔ |
| `changedMaximumAsksForRedraw()` | — | ✔ |
| `currentManaIsCapped()` | — | ✔ |


##### `PlayerCalcShapechangeTest.java`

**Responsible for:** Tests PlayerCalcs#calcShapechange, the port of C's calc_shapechange (player-calcs.c): what a shape adds to a state that the equipment walk has already filled.

**class `PlayerCalcShapechangeTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (2/2 documented): ✔ `state`, `vulnerabilities`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `total(String)` | — | ✔ |
| `shape(int, int, int, Map<PlayerSkill,Integer>, Flag<ObjectFlag>, Flag<PlayerFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>)` | — | ✔ |
| `withModifiers(Map<ObjectModifier,Integer>)` | — | ✔ |
| `withElements(Map<ElementEnum,Integer>)` | — | ✔ |

**class `PlayerCalcShapechangeTest.NoShape`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullShape()` | — | ✔ |
| `emptyShape()` | — | ✔ |

**class `PlayerCalcShapechangeTest.WhatItAdds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `combatBonusesAdd()` | — | ✔ |
| `skillsAdd()` | — | ✔ |
| `flagsUnion()` | — | ✔ |
| `statModifiersAdd()` | — | ✔ |
| `otherModifiers()` | — | ✔ |
| `extrasAdd()` | — | ✔ |

**class `PlayerCalcShapechangeTest.Resistances`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `betterResistanceTaken()` | — | ✔ |
| `worseResistanceIgnored()` | — | ✔ |
| `immunityTaken()` | — | ✔ |
| `vulnerabilityRemembered()` | — | ✔ |
| `neutralElement()` | — | ✔ |
| `unmentionedSurvive()` | — | ✔ |


##### `PlayerClassCopyTest.java`

**Responsible for:** Tests PlayerClass#copy(), the method PlayerBirth.playerGenerate hands a player its own class definition through (PlayerBirth.java:427).

**class `PlayerClassCopyTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (9/9 documented): ✔ `titles`, `stats`, `classSkills`, `extraSkills`, `startItems`, `oFlags`, `pFlags`, `magic`, `source`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(PlayerClass, String)` | — | ✔ |
| `setUp()` | — | ✔ |
| `everyFieldCarriedAcross()` | — | ✔ |
| `collectionsAreFreshObjects()` | — | ✔ |
| `copyIsUnaffectedByLaterSourceChanges()` | — | ✔ |
| `flagSetsAreIndependent()` | — | ✔ |
| `immutableElementsAreShared()` | — | ✔ |
| `emptyNonCasterCopies()` | — | ✔ |


##### `PlayerClassGetClassFromIndexTest.java`

**Responsible for:** Tests PlayerClass#getClassFromIndex(int), the port of C's player_id2class (player-class.c).

**class `PlayerClassGetClassFromIndexTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (1/1 documented): ✔ `previousClasses`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerClass(String)` | — | ✔ |
| `classesField()` | — | ✔ |
| `seedRegistry()` | — | ✘ |
| `restoreRegistry()` | — | ✘ |

**class `PlayerClassGetClassFromIndexTest.InRangeIndex`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstIndexAnswersFirstClass()` | — | ✘ |
| `lastIndexAnswersLastClass()` | — | ✘ |
| `middleIndexAnswersMiddleClass()` | — | ✘ |

**class `PlayerClassGetClassFromIndexTest.OutOfRangeHighIndex`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `indexEqualToSizeAnswersNull()` | — | ✘ |
| `indexWellPastSizeAnswersNull()` | — | ✘ |
| `indexZeroAgainstEmptyListAnswersNull()` | — | ✘ |

**class `PlayerClassGetClassFromIndexTest.NegativeIndex`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `minusOneAnswersNull()` | — | ✘ |
| `minValueAnswersNull()` | — | ✘ |


##### `PlayerClassGetNameTest.java`

**Responsible for:** Tests PlayerClass#getName(), the direct read of C's class->name.

**class `PlayerClassGetNameTest`** — class Javadoc: ✔ · type stamp: on 260904

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerClass(String)` | — | ✔ |
| `returnsConstructorName()` | — | ✔ |
| `nameIsReturnedVerbatim()` | — | ✔ |
| `returnsSameInstance()` | — | ✔ |


##### `PlayerClassGetStartItemsTest.java`

**Responsible for:** Tests PlayerClass#getStartItems(), the read of C's class->start_items chain (player.h).

**class `PlayerClassGetStartItemsTest`** — class Javadoc: ✔ · type stamp: on 260904

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `item(String)` | — | ✘ |
| `playerClass(List<StartItem>)` | — | ✔ |
| `emptyListIsReturnedAsEmpty()` | — | ✔ |
| `orderIsPreserved()` | — | ✔ |
| `returnsSameInstance()` | — | ✔ |


##### `PlayerClassGetTitleTest.java`

**Responsible for:** Tests PlayerClass#getTitle(int) and PlayerClass#getNoTitles(), the port of C's class->title[(lev - 1) / 5].

**class `PlayerClassGetTitleTest`** — class Javadoc: ✔ · type stamp: on 261010

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerClass(List<String>)` | — | ✔ |
| `tenTitles()` | — | ✔ |
| `bandsOfFive()` | — | ✔ |
| `belowLevelOne()` | — | ✔ |
| `aboveLevelFifty()` | — | ✔ |
| `shortList()` | — | ✔ |
| `noTitles()` | — | ✔ |
| `countsLoadedTitles()` | — | ✔ |


##### `PlayerClassMagicRealmTest.java`

**Responsible for:** Tests PlayerClass#magicRealm(), the port of C's class_magic_realms, and the accessors calcBonuses reads a class through.

**class `PlayerClassMagicRealmTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `realm(String, Stats)` | — | ✔ |
| `book(String, MagicRealm)` | — | ✔ |
| `playerClass(ClassMagic)` | — | ✔ |
| `duplicatesCollapse()` | — | ✔ |
| `deduplicatedByName()` | — | ✔ |
| `distinctRealmsKept()` | — | ✔ |
| `nonCasterHasNoRealms()` | — | ✔ |
| `realmsAreReversed()` | — | ✔ |
| `repeatedRealmKeepsFirstPosition()` | — | ✔ |
| `firstObjectWins()` | — | ✔ |
| `accessorsReadTheirOwnFields()` | — | ✔ |


##### `PlayerClassUnsetSlotsTest.java`

**Responsible for:** Tests that PlayerClass#getStatsAdj, PlayerClass#getSkill and PlayerClass#getXSkill answer 0 for a stat or skill the class does not name, and the scalar accessors PlayerClass#getMaxHitDie() and PlayerClass#getExpFactor().

**class `PlayerClassUnsetSlotsTest`** — class Javadoc: ✔ · type stamp: on 261010

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerClass(Map<Stats,Integer>, Map<PlayerSkill,Integer>, Map<PlayerSkill,Integer>)` | — | ✔ |
| `namedAndUnnamed()` | — | ✔ |
| `emptyMaps()` | — | ✔ |
| `hitDieAndExperienceFactor()` | — | ✔ |


##### `PlayerClearTimedTest.java`

**Responsible for:** Tests PlayerTimed.playerClearTimed, the port of C's player_clear_timed (src/player-timed.c:1127).

**class `PlayerClearTimedTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (4/4 documented): ✔ `EFFECT`, `player`, `realBus`, `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField()` | — | ✔ |
| `head()` | — | ✔ |
| `grade(int, int)` | — | ✔ |
| `load(int)` | — | ✔ |
| `loadPlain()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `startAt(int)` | — | ✔ |
| `clear(boolean, boolean)` | — | ✔ |
| `stored()` | — | ✔ |
| `anUnknownEffectThrows()` | — | ✔ |

**class `PlayerClearTimedTest.SilentBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerClearTimedTest.Clearing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRunningEffectGoesToZero()` | — | ✘ |
| `theLongestDurationClearsInOneCall()` | — | ✘ |
| `anIdleEffectStaysOff()` | — | ✘ |
| `canDisturbDoesNotAffectTheCounter()` | — | ✘ |

**class `PlayerClearTimedTest.LowerBound`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearingStopsAtTheFloor()` | — | ✘ |
| `clearingAtTheFloorIsANoChange()` | — | ✘ |

**class `PlayerClearTimedTest.Notification`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anAnnouncedClearAnswersTrue()` | — | ✘ |
| `aSilentClearIsNotOverridden()` | — | ✘ |
| `aSilentClearFromTheTopBandStaysSilent()` | — | ✘ |
| `clearingAnIdleEffectAnswersFalse()` | — | ✘ |
| `notifyIsIndependentOfCanDisturb()` | — | ✘ |


##### `PlayerCursesFindElementTest.java`

**Responsible for:** Tests Player's objectCursesFindElement, the port of C's object_curses_find_element (src/obj-knowledge.c:1748).

**class `PlayerCursesFindElementTest`** — class Javadoc: ✔ · type stamp: —

- Fields (7/12 documented): ✔ `SAVED_FIELDS`, `fireResisting`, `fireVulnerable`, `fireNeutral`, `coldResisting`, `elementless`, `unruned` — ✘ `SAVED`, `player`, `knowledge`, `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✘ |
| `curse(String, Map<ElementEnum,ElementInfo>)` | — | ✔ |
| `projection(ProjectionEnum, String)` | — | ✔ |
| `elementInfo(int)` | — | ✔ |
| `field(String)` | — | ✘ |
| `set(Player, String, Object)` | — | ✘ |
| `poke(Object, String, Object)` | — | ✔ |
| `itemWith(Map.Entry<Curse,CurseData>)` | — | ✔ |
| `cursed(Curse, int)` | — | ✔ |
| `clearMessageLog()` | — | ✔ |
| `findElement(ItemObject, ElementEnum)` | — | ✔ |
| `announced()` | — | ✘ |
| `glows()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `PlayerCursesFindElementTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerCursesFindElementTest.Found`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinary()` | — | ✘ |
| `vulnerability()` | — | ✘ |
| `alreadyKnown()` | — | ✘ |
| `twoCurses()` | — | ✘ |
| `curseWithoutRune()` | — | ✘ |

**class `PlayerCursesFindElementTest.NotFound`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zeroPower()` | — | ✘ |
| `zeroResistLevel()` | — | ✘ |
| `noElementData()` | — | ✘ |
| `differentElement()` | — | ✘ |
| `differentElementFound()` | — | ✘ |
| `noCurses()` | — | ✘ |


##### `PlayerCursesFindModifiersTest.java`

**Responsible for:** Tests PlayerKnowledge's cursesFindModifiers, the port of C's object_curses_find_modifiers (obj-knowledge.c).

**class `PlayerCursesFindModifiersTest`** — class Javadoc: ✔ · type stamp: —

- Fields (7/12 documented): ✔ `SAVED_FIELDS`, `weakness`, `feebleness`, `clumsiness`, `teleportation`, `neutral`, `unruned` — ✘ `SAVED`, `player`, `knowledge`, `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `seedRegistry()` | — | ✔ |
| `restore()` | — | ✘ |
| `curse(String, Map<ObjectModifier,Integer>, int)` | — | ✔ |
| `property(String)` | — | ✔ |
| `field(String)` | — | ✘ |
| `set(Player, String, Object)` | — | ✘ |
| `poke(Object, String, Object)` | — | ✔ |
| `itemWith(Map.Entry<Curse,CurseData>)` | — | ✔ |
| `cursed(Curse, int)` | — | ✔ |
| `clearMessageLog()` | — | ✔ |
| `weakItem(Curse)` | — | ✔ |
| `findModifiers(ItemObject)` | — | ✔ |
| `announced()` | — | ✘ |
| `startPlaying()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `PlayerCursesFindModifiersTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerCursesFindModifiersTest.Found`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinary()` | — | ✘ |
| `notPlaying()` | — | ✘ |
| `alreadyKnown()` | — | ✘ |
| `twoModifiers()` | — | ✘ |
| `twoCurses()` | — | ✘ |
| `figureFromCurse()` | — | ✘ |
| `curseWithoutRune()` | — | ✘ |

**class `PlayerCursesFindModifiersTest.NotFound`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zeroPower()` | — | ✘ |
| `absentCurse()` | — | ✘ |
| `absentAndCarried()` | — | ✘ |
| `noModifierData()` | — | ✘ |
| `zeroModifier()` | — | ✘ |
| `noCurses()` | — | ✘ |


##### `PlayerDecTimedTest.java`

**Responsible for:** Tests PlayerTimed.playerDecTimed, the port of C's player_dec_timed (src/player-timed.c:1097).

**class `PlayerDecTimedTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (4/4 documented): ✔ `EFFECT`, `player`, `realBus`, `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField()` | — | ✔ |
| `head()` | — | ✔ |
| `grade(int, int)` | — | ✔ |
| `loadPlain()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `startAt(int)` | — | ✔ |
| `dec(int, boolean, boolean)` | — | ✔ |
| `stored()` | — | ✔ |
| `anUnknownEffectThrows()` | — | ✔ |

**class `PlayerDecTimedTest.SilentBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerDecTimedTest.Decrease`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `subtractsFromTheCurrentDuration()` | — | ✘ |
| `aSingleTickShortensByOne()` | — | ✘ |
| `anExactDecrementEndsTheEffect()` | — | ✘ |
| `zeroChangesNothing()` | — | ✘ |
| `aNegativeAmountLengthens()` | — | ✘ |
| `theTotalIsCoercedFurtherDown()` | — | ✘ |

**class `PlayerDecTimedTest.Overshooting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anOvershootEndsTheEffect()` | — | ✘ |
| `anOvershootDoesNotStoreTheAmount()` | — | ✘ |
| `decrementingAnIdleEffectDoesNothing()` | — | ✘ |

**class `PlayerDecTimedTest.Notification`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSilentDecrementAnswersFalse()` | — | ✘ |
| `anAnnouncedDecrementAnswersTrue()` | — | ✘ |
| `finishingAnnouncesItselfAnyway()` | — | ✘ |
| `anOvershootIsAnnounced()` | — | ✘ |
| `theOverrideDoesNotAnnounceANonEvent()` | — | ✘ |
| `aBandChangeWithNoLapseMessageIsSilent()` | — | ✘ |


##### `PlayerEquipLearnElementTest.java`

**Responsible for:** Tests PlayerKnowledge#equipLearnElement, the port of C's equip_learn_element (src/obj-knowledge.c:2155).

**class `PlayerEquipLearnElementTest`** — class Javadoc: ✔ · type stamp: —

- Fields (2/8 documented): ✔ `SAVED_FIELDS`, `fireResisting` — ✘ `SAVED`, `player`, `knowledge`, `body`, `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✘ |
| `projection(ProjectionEnum, String)` | — | ✘ |
| `elementInfo(int)` | — | ✔ |
| `field(String)` | — | ✘ |
| `poke(Object, String, Object)` | — | ✔ |
| `item(Map<ElementEnum,ElementInfo>)` | — | ✔ |
| `makeFullyKnown(ItemObject)` | — | ✔ |
| `makeUnknown(ItemObject)` | — | ✔ |
| `curse(ItemObject, Curse, int)` | — | ✔ |
| `set(Player, String, Object)` | — | ✘ |
| `knownResLevel(ItemObject, ElementEnum)` | — | ✔ |
| `clearMessageLog()` | — | ✔ |
| `equip(ItemObject)` | — | ✔ |
| `announced()` | — | ✘ |
| `glows()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `PlayerEquipLearnElementTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerEquipLearnElementTest.Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sentinels()` | — | ✘ |
| `alreadyKnown()` | — | ✘ |
| `noItems()` | — | ✘ |
| `noKnownCounterpart()` | — | ✘ |

**class `PlayerEquipLearnElementTest.Affecting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinary()` | — | ✘ |
| `vulnerability()` | — | ✘ |
| `immunity()` | — | ✘ |
| `twoItems()` | — | ✘ |
| `counterpartUntouched()` | — | ✘ |

**class `PlayerEquipLearnElementTest.NotAffecting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `explicitZero()` | — | ✘ |
| `absentEntry()` | — | ✘ |
| `noElementData()` | — | ✘ |
| `flagsCopied()` | — | ✘ |
| `absentEntryLeavesFlagsEmpty()` | — | ✘ |
| `fullyKnown()` | — | ✘ |

**class `PlayerEquipLearnElementTest.Curses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `afterElseBranch()` | — | ✘ |
| `afterAffectingBranch()` | — | ✘ |
| `zeroPower()` | — | ✘ |


##### `PlayerExpFactorTest.java`

**Responsible for:** Tests PlayerRace#getExpFactor() and PlayerClass#getExpFactor(), the two halves of C's p->expfact = p->race->r_exp + p->class->c_exp (player-birth.c:997).

**class `PlayerExpFactorTest`** — class Javadoc: ✔ · type stamp: on 260902

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(String, int)` | — | ✔ |
| `playerClass(String, int)` | — | ✔ |

**class `PlayerExpFactorTest.RaceFactor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsShippedValues()` | — | ✔ |
| `copyKeepsFactor()` | — | ✔ |

**class `PlayerExpFactorTest.ClassFactor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zeroForShippedData()` | — | ✔ |
| `reportsNonZero()` | — | ✔ |
| `copyKeepsFactor()` | — | ✔ |

**class `PlayerExpFactorTest.Sum`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sumMatchesC()` | — | ✔ |


##### `PlayerExpGainTest.java`

**Responsible for:** Tests Player#playerExpGain(long), the route every experience award in the game arrives by.

**class `PlayerExpGainTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `intField(String)` | — | ✔ |
| `longField(String)` | — | ✔ |
| `state(long, long, int, int)` | — | ✔ |
| `history()` | — | ✔ |

**class `PlayerExpGainTest.Maximum`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `behindTheMaximumEarnsATenth()` | — | ✔ |
| `levelWithTheMaximumEarnsNothing()` | — | ✔ |
| `gainThatOvertakesTheMaximumEarnsNoTenth()` | — | ✔ |
| `smallAwardsAddNothingToTheMaximum()` | — | ✔ |
| `theTenthTruncates()` | — | ✔ |
| `zeroAwardChangesNothing()` | — | ✔ |

**class `PlayerExpGainTest.Edges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `negativeAwardTruncatesTowardsZero()` | — | ✔ |
| `awardPastTheCapIsClamped()` | — | ✔ |

**class `PlayerExpGainTest.Level`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `earnedLevelIsAnnounced()` | — | ✔ |
| `maxLevelFollowsTheMaximum()` | — | ✔ |
| `awardShortOfTheThresholdLeavesTheLevel()` | — | ✔ |


##### `PlayerExpLoseTest.java`

**Responsible for:** Tests Player#playerExpLose(long, boolean), the route every experience drain in the game arrives by.

**class `PlayerExpLoseTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `intField(String)` | — | ✔ |
| `longField(String)` | — | ✔ |
| `state(long, long, int, int)` | — | ✔ |
| `history()` | — | ✔ |

**class `PlayerExpLoseTest.Totals`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `temporaryLossKeepsTheMaximum()` | — | ✔ |
| `permanentLossTakesTheMaximum()` | — | ✔ |
| `zeroLossChangesNothing()` | — | ✔ |

**class `PlayerExpLoseTest.Cap`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oversizedLossStopsAtZero()` | — | ✔ |
| `capAppliesToTheMaximum()` | — | ✔ |
| `exactDrainLandsOnZero()` | — | ✔ |
| `negativeMaximumIsFloored()` | — | ✔ |
| `negativeAmountAdds()` | — | ✔ |

**class `PlayerExpLoseTest.Levels`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `temporaryDrainKeepsMaxLevel()` | — | ✔ |
| `losingLevelsIsSilent()` | — | ✔ |
| `permanentTotalDrainDropsToLevelOne()` | — | ✔ |


##### `PlayerFlagsTest.java`

**Responsible for:** Tests Player#playerFlags, the port of C's player_flags (player.c:290).

**class `PlayerFlagsTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (3/3 documented): ✔ `player`, `state`, `flags`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(Flag<ObjectFlag>)` | — | ✔ |
| `playerClass(Flag<ObjectFlag>)` | — | ✔ |
| `setOf(ObjectFlag)` | — | ✔ |
| `setField(Object, Class<?>, String, Object)` | — | ✔ |
| `givePFlag(PlayerState, PlayerFlag)` | — | ✔ |
| `giveRaceAndClass(Flag<ObjectFlag>, Flag<ObjectFlag>)` | — | ✔ |
| `setLevel(int)` | — | ✔ |
| `setUp()` | — | ✘ |

**class `PlayerFlagsTest.RaceAndClassContribute`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRaceFlagIsGathered()` | — | ✘ |
| `aClassFlagIsGathered()` | — | ✘ |
| `bothSourcesAreGatheredTogether()` | — | ✘ |
| `aFlagOnBothSourcesIsHeldOnce()` | — | ✔ |
| `aPlayerWithNoInnateFlagsGathersNothing()` | — | ✘ |
| `theRacesOwnSetIsNotModifiedByTheGather()` | — | ✔ |

**class `PlayerFlagsTest.TheCallersSetIsReplaced`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aFlagAlreadyInTheSetIsDiscarded()` | — | ✘ |
| `aPlayerWithNoInnateFlagsEmptiesTheSet()` | — | ✘ |
| `callingTwiceLeavesTheSameSet()` | — | ✘ |
| `aSecondCallDoesNotKeepTheFirstCallsFlags()` | — | ✔ |

**class `PlayerFlagsTest.BraveryAtLevelThirty`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theFlagAndTheLevelTogetherGrantFearProtection()` | — | ✘ |
| `theFlagWithoutTheLevelGrantsNothing()` | — | ✘ |
| `theLevelWithoutTheFlagGrantsNothing()` | — | ✘ |
| `theProtectionPersistsAboveLevelThirty()` | — | ✔ |
| `aFreshPlayerAtLevelZeroGetsNoProtection()` | — | ✘ |
| `anotherPlayerFlagDoesNotGrantFearProtection()` | — | ✔ |
| `fearProtectionFromTheRaceSurvivesAnUnqualifiedPlayer()` | — | ✔ |
| `braveryAddsToTheInnateFlagsRatherThanReplacingThem()` | — | ✘ |

**class `PlayerFlagsTest.TheStateParameterIsWhatIsRead`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aHypotheticalStateGrantsProtectionThePlayersOwnStateWouldNot()` | — | ✔ |
| `thePlayersOwnStateIsNotConsultedWhenAnotherIsPassed()` | — | ✔ |
| `theStatePassedInIsNotModified()` | — | ✘ |


##### `PlayerFlagsTimedTest.java`

**Responsible for:** Tests Player#flagsTimed, the port of C's player_flags_timed (player.c:310).

**class `PlayerFlagsTimedTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (3/3 documented): ✔ `player`, `flags`, `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField()` | — | ✔ |
| `definition(TimedEffect, ObjectFlag)` | — | ✔ |
| `load(PlayerTimedEffect)` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `setTimedValue(TimedEffect, int)` | — | ✔ |
| `flagCount()` | — | ✔ |

**class `PlayerFlagsTimedTest.RunningEffectsContribute`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRunningEffectAddsItsDuplicatedFlag()` | — | ✘ |
| `severalRunningEffectsEachAddTheirOwnFlag()` | — | ✘ |
| `onlyTheRunningOnesOfSeveralLoadedEffectsContribute()` | — | ✘ |
| `twoEffectsSharingAFlagAddItOnce()` | — | ✔ |
| `aNegativeCounterCountsAsRunning()` | — | ✔ |

**class `PlayerFlagsTimedTest.TheCallersSetIsPreserved`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsAlreadyInTheSetSurvive()` | — | ✘ |
| `aFlagFromGearIsNotRemovedWhenItsEffectIsDormant()` | — | ✘ |
| `nothingIsAddedWhenNoEffectIsRunning()` | — | ✘ |
| `aFreshPlayerContributesNothing()` | — | ✘ |
| `callingTwiceLeavesTheSameSet()` | — | ✘ |

**class `PlayerFlagsTimedTest.TrapSafeIsExcluded`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRunningTrapSafeDoesNotAddTrapImmunity()` | — | ✘ |
| `trapImmunityAlreadyInTheSetIsLeftAlone()` | — | ✔ |
| `otherEffectsStillContributeWhileTrapSafeRuns()` | — | ✘ |
| `anotherEffectNamingTrapImmunityIsNotExcluded()` | — | ✔ |

**class `PlayerFlagsTimedTest.NothingToContribute`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anEffectDuplicatingNoFlagAddsNothing()` | — | ✘ |
| `aRunningEffectWithNoDefinitionIsSkipped()` | — | ✔ |
| `anEmptyRegistryListAddsNothingRatherThanThrowing()` | — | ✘ |
| `theSentinelEffectContributesNothing()` | — | ✔ |
| `anEmptyTimedMapAddsNothingRatherThanThrowing()` | — | ✘ |


##### `PlayerFlavourAwareTest.java`

**Responsible for:** Tests PlayerKnowledge#flavourAware and ObjectUtils#isCarried — the ports of C's object_flavor_aware (obj-knowledge.c:2262) and object_is_carried (obj-util.c).

**class `PlayerFlavourAwareTest`** — class Javadoc: ✔ · type stamp: on 260816

- Fields (0/1 documented): ✘ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `poke(Class<?>, Object, String, Object)` | — | ✔ |
| `unknownPotion()` | — | ✔ |
| `flavourAware(ItemObject)` | — | ✔ |
| `setUp()` | — | ✘ |

**class `PlayerFlavourAwareTest.BecomingAware`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `kindBecomesAware()` | — | ✘ |
| `counterpartLearnsEffect()` | — | ✔ |
| `alreadyAwareKindReturnsEarly()` | — | ✔ |
| `missingPiecesAreSurvivable()` | — | ✔ |

**class `PlayerFlavourAwareTest.IgnoreFixup`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ignoreDecisionIsCarriedAcross()` | — | ✔ |
| `unignoredKindStaysUnignored()` | — | ✔ |
| `ignorePassIsRequested()` | — | ✔ |

**class `PlayerFlavourAwareTest.Carried`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `carrying(ItemObject)` | — | ✘ |
| `gearObjectIsCarried()` | — | ✘ |
| `otherObjectIsNotCarried()` | — | ✘ |
| `emptyGearCarriesNothing()` | — | ✔ |
| `identicalButDistinctObjectIsNotCarried()` | — | ✔ |
| `allGearObjectsAreCarried()` | — | ✔ |


##### `PlayerFullNameTest.java`

**Responsible for:** Tests Player#getFullName and Player#setFullName — the read and write halves of C's p->full_name (player.h:571, char[PLAYER_NAME_LEN], 32 bytes including the terminator).

**class `PlayerFullNameTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `newPlayerHasNoName()` | — | ✔ |
| `returnsStoredName()` | — | ✔ |
| `emptyNameIsStoredAsEmpty()` | — | ✔ |
| `thirtyOneCharacterNameIsNotTruncated()` | — | ✔ |
| `thirtyTwoCharacterNameIsTruncatedToThirtyOne()` | — | ✔ |
| `longNameIsTruncatedToFirstThirtyOneCharacters()` | — | ✔ |
| `lastWriteWins()` | — | ✔ |


##### `PlayerHandleStuffTest.java`

**Responsible for:** Tests PlayerCalcs#handleStuff(Player), the port of C's handle_stuff (player-calcs.c).

**class `PlayerHandleStuffTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (8/8 documented): ✔ `UPDATE_READ`, `REDRAW_READ`, `HP_PER_LEVEL`, `player`, `upkeep`, `bus`, `realBus`, `realCharacterGenerated`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `handleStuffAndTakeReadings()` | — | ✔ |

**class `PlayerHandleStuffTest.RecordingUpkeep`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `reads`, `cleared`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `updateOff(PlayerUpdateEnum)` | — | ✘ (override) |
| `getUpdate()` | — | ✘ (override) |
| `getRedrawFlags()` | — | ✘ (override) |

**class `PlayerHandleStuffTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerHandleStuffTest.Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nothingPending()` | — | ✔ |
| `updateOnly()` | — | ✔ |
| `redrawOnly()` | — | ✔ |
| `bothInOrder()` | — | ✔ |
| `anyUpdateFlag()` | — | ✔ |

**class `PlayerHandleStuffTest.Ordering`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `redrawRaisedByUpdateIsServiced()` | — | ✔ |
| `clearsNothingItself()` | — | ✔ |


##### `PlayerHeightTest.java`

**Responsible for:** Tests Player#setHeight, Player#setHeightBirth, Player#getHeightBirth, PlayerRace#getBaseHeight and PlayerRace#getModHeight — the pieces C's get_ahw needs to roll a starting height.

**class `PlayerHeightTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(String, int, int)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `htBirth()` | — | ✔ |

**class `PlayerHeightTest.SetHeight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `storesTheValue()` | — | ✔ |
| `lastWriteWins()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |
| `holdsInches()` | — | ✔ |

**class `PlayerHeightTest.SetHeightBirth`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `doesNotTouchWorkingHeight()` | — | ✔ |
| `survivesLaterWorkingWrite()` | — | ✔ |
| `birthAssignmentSetsBoth()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |

**class `PlayerHeightTest.RaceHeightFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseAndModAreSeparate()` | — | ✔ |
| `heightIsNotAge()` | — | ✔ |
| `baseIsReachable()` | — | ✔ |
| `spreadReachesFourDeviations()` | — | ✔ |
| `feedsOffsetArithmetic()` | — | ✔ |
| `reportsZeroSpread()` | — | ✔ |


##### `PlayerHistoryAddTest.java`

**Responsible for:** Tests PlayerHistory.historyAdd, PlayerHistory.historyAddWithFlags and PlayerHistory.historyAddFull — the ports of C's history_add, history_add_with_flags and history_add_full (src/player-history.c:76, :110, :127).

**class `PlayerHistoryAddTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `artifact()` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `entries()` | — | ✔ |
| `onlyEntry()` | — | ✔ |
| `historyAdd(String, PlayerHistoryType)` | — | ✔ |
| `historyAddWithFlags(String, Flag<PlayerHistoryType>, Artifact)` | — | ✔ |
| `historyAddFull(Flag<PlayerHistoryType>, Artifact, int, int, int, String)` | — | ✔ |

**class `PlayerHistoryAddTest.HistoryAddCarriesOneType`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theTypeAskedForIsRecorded()` | — | ✔ |
| `noOtherTypeIsOn()` | — | ✔ |
| `theEntryRelatesToNoArtifact()` | — | ✔ |

**class `PlayerHistoryAddTest.TheCircumstancesAreReadOffThePlayer`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `depthLevelAndTurnAreThePlayers()` | — | ✔ |
| `theArtifactIsPassedThrough()` | — | ✔ |
| `severalTypesArriveWhole()` | — | ✔ |

**class `PlayerHistoryAddTest.TheTurnIsEnergyOverAHundred`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `turnAt(int)` | — | ✔ |
| `noEnergyIsTurnZero()` | — | ✔ |
| `ninetyNineIsStillTurnZero()` | — | ✔ |
| `aHundredIsTurnOne()` | — | ✔ |
| `aHundredAndNinetyNineIsStillTurnOne()` | — | ✔ |

**class `PlayerHistoryAddTest.TheFlagsAreCopiedNotBorrowed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `switchingATypeOnDoesNotReachTheEntry()` | — | ✔ |
| `wipingTheCallersSetDoesNotEmptyTheEntry()` | — | ✔ |

**class `PlayerHistoryAddTest.TheLedgerIsWrittenInOrder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyFieldIsTheCallers()` | — | ✔ |
| `aNewPlayersLedgerIsEmpty()` | — | ✔ |
| `entriesKeepTheirOrder()` | — | ✔ |
| `theLedgerGrowsPastTwenty()` | — | ✔ |
| `allThreeReportSuccess()` | — | ✔ |


##### `PlayerHistoryChartTest.java`

**Responsible for:** Tests PlayerHistoryChart, the port of C's struct history_chart (player.h) as filled by parse_history_chart and finish_parse_history (init.c).

**class `PlayerHistoryChartTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (0/1 documented): ✘ `REAL_FILE`

**record `PlayerHistoryChartTest.RawRecord`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/4 documented): ✘ `chart`, `next`, `roll`, `phrase`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `readRawFile()` | — | ✔ |
| `byNumber(List<PlayerHistoryChart>, int)` | — | ✘ |

**class `PlayerHistoryChartTest.Construction`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `numbersRoundTrip()` | — | ✘ |
| `startsEmptyAndUnresolved()` | — | ✘ |
| `terminalChart()` | — | ✘ |

**class `PlayerHistoryChartTest.Successor`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resolvedSuccessor()` | — | ✘ |
| `unresolvedNonZeroSuccessor()` | — | ✘ |
| `chainWalk()` | — | ✘ |

**class `PlayerHistoryChartTest.Entries`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fileOrderIsKept()` | — | ✘ |
| `noSorting()` | — | ✘ |
| `liveList()` | — | ✘ |

**class `PlayerHistoryChartTest.ShippedData`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sameCharts()` | — | ✘ |
| `everyChartMatchesTheFile()` | — | ✘ |
| `successorIsUniformPerChart()` | — | ✘ |
| `rollsAreCumulativeThresholds()` | — | ✘ |
| `successorsResolve()` | — | ✘ |
| `humanChain()` | — | ✘ |
| `chartOneRolls()` | — | ✘ |


##### `PlayerHistoryEntryTest.java`

**Responsible for:** Tests PlayerHistoryEntry, the port of C's struct history_entry (player.h) as filled by parse_history_chart and parse_history_phrase (init.c) and read by get_history (player-birth.c).

**class `PlayerHistoryEntryTest`** — class Javadoc: ✔ · type stamp: on 261012

- Fields (0/1 documented): ✘ `REAL_FILE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `pick(List<PlayerHistoryEntry>, int)` | — | ✔ |
| `loadShipped()` | — | ✘ |

**class `PlayerHistoryEntryTest.Construction`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `roundTrip()` | — | ✘ |
| `textIsNotTrimmed()` | — | ✘ |
| `boundaryRolls()` | — | ✘ |
| `noRangeCheck()` | — | ✘ |
| `independentInstances()` | — | ✘ |

**class `PlayerHistoryEntryTest.ThresholdRule`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `chartOneBands()` | — | ✘ |
| `drawOnThresholdPicksThatEntry()` | — | ✘ |
| `everyDrawPicksSomething()` | — | ✘ |

**class `PlayerHistoryEntryTest.ShippedData`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rollsInRange()` | — | ✘ |
| `trailingSpaceCounts()` | — | ✘ |
| `unspacedPhrasesEndChains()` | — | ✘ |


##### `PlayerHistoryLedgerTest.java`

**Responsible for:** Tests PlayerHistory — the port of C's struct player_history (src/player-history.h:47) together with the memory management C keeps beside it in history_init and history_realloc (src/player-history.c:36, :46).

**class `PlayerHistoryLedgerTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `ledger`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `entry(String)` | — | ✔ |
| `newLedger()` | — | ✔ |

**class `PlayerHistoryLedgerTest.ANewLedgerIsEmpty`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `itHoldsNoEntries()` | — | ✔ |
| `theFirstEntryLandsAtTheFront()` | — | ✔ |

**class `PlayerHistoryLedgerTest.EntriesAreAppendedInOrder`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eachEntryFollowsTheLast()` | — | ✔ |
| `theEntryGivenIsTheEntryKept()` | — | ✔ |
| `anIdenticalEventLoggedTwiceIsLoggedTwice()` | — | ✔ |
| `theSameEntryObjectLoggedTwiceOccupiesTwoSlots()` | — | ✔ |

**class `PlayerHistoryLedgerTest.TheLedgerGrowsWithoutLosingEntries`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fill(int)` | — | ✔ |
| `twentyEntriesFitTheFirstBlock()` | — | ✔ |
| `theTwentyFirstEntryFollowsTheTwentieth()` | — | ✔ |
| `theFortyFirstEntryFollowsTheFortieth()` | — | ✔ |
| `aHundredEntriesAreAllPresentAndInOrder()` | — | ✔ |

**class `PlayerHistoryLedgerTest.EachLedgerIsItsOwnLog`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loggingToOneDoesNotReachTheOther()` | — | ✔ |

**class `PlayerHistoryLedgerTest.ClearEmptiesTheLedger`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearingAnAlreadyEmptyLedgerLeavesItEmpty()` | — | ✔ |
| `clearingRemovesEveryEntryPreviouslyLogged()` | — | ✔ |
| `theNextEntryLoggedAfterAClearLandsAtTheFront()` | — | ✔ |
| `aPreviouslyHeldReferenceToTheEntriesIsNotItselfEmptied()` | — | ✔ |


##### `PlayerHitDieTest.java`

**Responsible for:** Tests PlayerRace#getMaxHitDie(), PlayerClass#getMaxHitDie() and Player#setHitDie(int) — the two halves of C's p->hitdie = p->race->r_mhp + p->class->c_mhp (player-birth.c:1000) and the field the sum lands in.

**class `PlayerHitDieTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(String, int)` | — | ✔ |
| `playerClass(String, int)` | — | ✔ |
| `build()` | — | ✔ |

**class `PlayerHitDieTest.RaceContribution`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsShippedValues()` | — | ✔ |
| `survivesACopy()` | — | ✔ |

**class `PlayerHitDieTest.ClassContribution`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsShippedValues()` | — | ✔ |
| `survivesACopy()` | — | ✔ |

**class `PlayerHitDieTest.SummedDie`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startsAtZero()` | — | ✔ |
| `sumsTheTwoContributions()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |
| `sum(PlayerRace, PlayerClass)` | — | ✔ |


##### `PlayerIdentityAccessorTest.java`

**Responsible for:** Tests the five writers player_generate uses to stamp a character's identity onto a player: Player#setRace, Player#setClass, Player#setMaxLevel, Player#setLevel and Player#setExpFact.

**class `PlayerIdentityAccessorTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(String, int)` | — | ✔ |
| `playerClass(String, int)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `intField(String)` | — | ✔ |

**class `PlayerIdentityAccessorTest.RaceAndClass`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storeTheGivenInstance()` | — | ✔ |
| `areIndependent()` | — | ✔ |
| `deriveNothing()` | — | ✔ |

**class `PlayerIdentityAccessorTest.Levels`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `birthValues()` | — | ✔ |
| `areSeparateCounters()` | — | ✔ |
| `noClampOnTheWayIn()` | — | ✔ |
| `maximumDoesNotMoveTheLevel()` | — | ✔ |

**class `PlayerIdentityAccessorTest.ExpFactor`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesTheValue()` | — | ✔ |
| `birthSequence()` | — | ✔ |
| `independentOfLevels()` | — | ✔ |


##### `PlayerIncCheckTest.java`

**Responsible for:** Tests PlayerTimed#incCheck, the port of C's player_inc_check (src/player-timed.c:926).

**class `PlayerIncCheckTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/9 documented): ✔ `SEED`, `SUBJECT`, `REFERENT`, `savedEffects` — ✘ `player`, `state`, `known`, `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `save()` | — | ✘ |
| `restore()` | — | ✘ |
| `registryField()` | — | ✘ |
| `effect(TimedEffect, List<TimedFailure>)` | — | ✔ |
| `objectFlag(ObjectFlag)` | — | ✘ |
| `resist(ElementEnum)` | — | ✘ |
| `vulnerability(ElementEnum)` | — | ✘ |
| `playerFlag(PlayerFlag)` | — | ✘ |
| `timedEffect(TimedEffect)` | — | ✘ |
| `race()` | — | ✔ |
| `learnedAbout(ItemObject, ElementEnum)` | — | ✔ |
| `learnedAbout(ItemObject, ObjectFlag)` | — | ✔ |
| `set(Object, String, Object)` | — | ✘ |
| `get(Object, String)` | — | ✘ |
| `poke(Object, String, Object)` | — | ✘ |
| `clearMessageLog()` | — | ✘ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `conditions(TimedFailure)` | — | ✔ |
| `running(TimedEffect, int)` | — | ✔ |
| `unknownItem()` | — | ✔ |
| `caveWithoutActor()` | — | ✔ |
| `caveWithActor()` | — | ✔ |
| `chunk(int)` | — | ✘ |
| `announced()` | — | ✘ |
| `resistWasAnnounced()` | — | ✘ |

**class `PlayerIncCheckTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerIncCheckTest.TheWalk`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noConditions()` | — | ✘ |
| `allMustPass()` | — | ✘ |
| `lastConditionVetoes()` | — | ✘ |
| `vetoStopsTheWalk()` | — | ✔ |
| `reachedConditionLearns()` | — | ✔ |
| `walkRunsLastDeclaredFirst()` | — | ✔ |

**class `PlayerIncCheckTest.ObjectFlagCondition`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loreKnown()` | — | ✘ |
| `loreIgnoresCalculatedState()` | — | ✘ |
| `loreDoesNotLearn()` | — | ✘ |
| `liveHeld()` | — | ✘ |
| `liveIgnoresKnownState()` | — | ✘ |
| `liveLearns()` | — | ✘ |
| `silentWithoutAnActor()` | — | ✔ |
| `announcedWithAnActor()` | — | ✘ |
| `nothingAnnouncedWhenNotResisted()` | — | ✘ |

**class `PlayerIncCheckTest.ElementCondition`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resistLoreKnown()` | — | ✘ |
| `resistLoreIgnoresCalculatedState()` | — | ✘ |
| `resistLiveHeld()` | — | ✘ |
| `resistLiveAbsent()` | — | ✘ |
| `resistIsStrictlyPositive()` | — | ✔ |
| `vulnLoreKnown()` | — | ✘ |
| `vulnLoreIgnoresCalculatedState()` | — | ✘ |
| `vulnLiveHeld()` | — | ✘ |
| `vulnIsStrictlyNegative()` | — | ✔ |
| `neutralIsNeither()` | — | ✘ |
| `vulnLiveLearns()` | — | ✘ |

**class `PlayerIncCheckTest.PlayerFlagCondition`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `loreKnown()` | — | ✘ |
| `loreIgnoresCalculatedState()` | — | ✘ |
| `liveHeld()` | — | ✘ |
| `liveIgnoresKnownState()` | — | ✘ |
| `absent()` | — | ✘ |

**class `PlayerIncCheckTest.TimedEffectCondition`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dormantReferentAllows()` | — | ✔ |
| `runningReferentVetoes()` | — | ✘ |
| `loreMakesNoDifference()` | — | ✔ |
| `oneTurnIsRunning()` | — | ✘ |

**class `PlayerIncCheckTest.ShippedPoisonConditions`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `declareAsTheDataDoes()` | — | ✘ |
| `ordinaryCharacterIsPoisoned()` | — | ✘ |
| `permanentResistance()` | — | ✘ |
| `temporaryResistance()` | — | ✘ |
| `expiredTemporaryResistance()` | — | ✘ |

**class `PlayerIncCheckTest.NoCategory`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `throwsLikeCAsserts()` | — | ✘ |


##### `PlayerIncTimedTest.java`

**Responsible for:** Tests PlayerTimed.playerIncTimed, the port of C's player_inc_timed (src/player-timed.c:1053).

**class `PlayerIncTimedTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (5/5 documented): ✔ `EFFECT`, `REFERENT`, `player`, `realBus`, `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField()` | — | ✔ |
| `head()` | — | ✔ |
| `grade(int, int)` | — | ✔ |
| `load(boolean, List<TimedFailure>)` | — | ✔ |
| `loadPlain()` | — | ✔ |
| `blockedByReferent()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `startAt(TimedEffect, int)` | — | ✔ |
| `inc(int, boolean, boolean, boolean)` | — | ✔ |
| `stored()` | — | ✔ |
| `anUnknownEffectThrows()` | — | ✔ |
| `aMissingTimedTableThrows()` | — | ✔ |

**class `PlayerIncTimedTest.SilentBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerIncTimedTest.Increase`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addsToTheCurrentDuration()` | — | ✘ |
| `startsFromZero()` | — | ✘ |
| `aNegativeAmountShortens()` | — | ✘ |
| `zeroChangesNothing()` | — | ✘ |
| `theTotalIsCoercedFurtherDown()` | — | ✘ |
| `aNegativeTotalBecomesZero()` | — | ✘ |

**class `PlayerIncTimedTest.Check`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aVetoRefusesTheIncrease()` | — | ✘ |
| `aPassingCheckAllowsTheIncrease()` | — | ✘ |
| `checkFalseSkipsTheConditionsEntirely()` | — | ✘ |

**class `PlayerIncTimedTest.NonStacking`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aRunningNonStackingEffectIsBlocked()` | — | ✘ |
| `anIdleNonStackingEffectStarts()` | — | ✘ |
| `theBlockTestsTheCounterNotTheDefinition()` | — | ✘ |
| `aRunningNonStackingEffectRefusesANegativeAmount()` | — | ✘ |
| `aStackingEffectExtends()` | — | ✘ |
| `aVetoBeatsTheNonStackingBlock()` | — | ✘ |

**class `PlayerIncTimedTest.Returning`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aGradeChangeAnswersTrue()` | — | ✘ |
| `aSilentChangeAnswersFalse()` | — | ✘ |
| `anAnnouncedChangeAnswersTrue()` | — | ✘ |
| `aGradeChangeOverridesASilentCaller()` | — | ✘ |


##### `PlayerIsImmuneTest.java`

**Responsible for:** Tests PlayerUtils#playerIsImmune(Player, ElementEnum), the port of C's player_is_immune (player-util.c:1566).

**class `PlayerIsImmuneTest`** — class Javadoc: ✔ · type stamp: on 260829

- Fields (2/2 documented): ✔ `player`, `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `isImmune(ElementEnum)` | — | ✔ |
| `levelThreeIsImmune()` | — | ✔ |
| `levelsBelowThreeAreNotImmune()` | — | ✔ |
| `levelAboveThreeIsNotImmune()` | — | ✔ |
| `wipedStateIsImmuneToNothing()` | — | ✔ |
| `immunityIsPerElement()` | — | ✔ |
| `readsCalculatedStateNotKnownState()` | — | ✔ |


##### `PlayerKnowledgeCreateObjFlagMaskTest.java`

**Responsible for:** Tests PlayerKnowledge.createObjFlagMask, the port of C's create_obj_flag_mask (obj-properties.c) - the selection of every flag property whose id type, or whose subtype, is one of a list.

**class `PlayerKnowledgeCreateObjFlagMaskTest`** — class Javadoc: ✔ · type stamp: on 261003

- Fields (0/3 documented): ✘ `savedProperties`, `savedPropertyMax`, `method`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `saveAndReflect()` | — | ✘ |
| `restore()` | — | ✘ |
| `registryField(String)` | — | ✘ |
| `flag(ObjectFlag, ObjectFlagType, ObjectFlagID)` | — | ✘ |
| `call(boolean, Enum<?>)` | — | ✘ |
| `set(Flag<ObjectFlag>)` | — | ✘ |
| `mask(boolean, Enum<?>)` | — | ✘ |
| `loadProperties()` | — | ✔ |
| `byIdWield()` | — | ✘ |
| `byIdTimed()` | — | ✘ |
| `byIdNormal()` | — | ✘ |
| `byIdTwoValues()` | — | ✘ |
| `byIdNoneSkipsNonFlags()` | — | ✘ |
| `bySubtypeSustain()` | — | ✘ |
| `bySubtypeMisc()` | — | ✘ |
| `bySubtypeBad()` | — | ✘ |
| `bySubtypeSeveralValues()` | — | ✘ |
| `bySubtypeNoneSkipsNonFlags()` | — | ✘ |
| `bySubtypeSingletons()` | — | ✘ |
| `bySubtypeNoMatch()` | — | ✘ |
| `emptyList()` | — | ✘ |
| `terminatorMatchesNothing()` | — | ✘ |
| `duplicateValues()` | — | ✘ |
| `orderIndependent()` | — | ✘ |
| `classificationsPartitionTheFlags()` | — | ✔ |
| `wrongFamilySelectsNothing()` | — | ✘ |
| `mixedFamiliesInOneList()` | — | ✘ |
| `nullClassification()` | — | ✘ |
| `freshResultEachCall()` | — | ✘ |
| `emptyRegistry()` | — | ✘ |


##### `PlayerKnowledgeKnowCurseObjectTest.java`

**Responsible for:** Tests the curse overload of PlayerKnowledge.knowObject, the port of player_know_object() (obj-knowledge.c) run over a curse's own object, which is how update_player_object_knowledge() reaches curses[i].obj.

**class `PlayerKnowledgeKnowCurseObjectTest`** — class Javadoc: ✔ · type stamp: on 261007

- Fields (0/6 documented): ✘ `REAL_MODIFIERS`, `savedCurses`, `savedCave`, `savedBus`, `curse`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flags(ObjectFlag)` | — | ✘ |
| `resist(int)` | — | ✘ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✘ |
| `known()` | — | ✘ |
| `nothingKnown()` | — | ✘ |
| `combatRunesAreIndependent()` | — | ✘ |
| `modifiersAreGatedOnKnowledge()` | — | ✘ |
| `modifierMapExcludesSentinels()` | — | ✘ |
| `elementsAreGatedOnKnowledge()` | — | ✘ |
| `knownElementTheCurseOmitsIsBlank()` | — | ✘ |
| `flagsAreTheIntersection()` | — | ✘ |
| `repeatedUpdateTracksKnowledge()` | — | ✘ |

**class `PlayerKnowledgeKnowCurseObjectTest.QuietBus`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerKnowledgeKnowObjectTest.java`

**Responsible for:** Tests PlayerKnowledge#knowObject(Player, ItemObject), the port of C's player_know_object for real objects — the step that rewrites an object's known counterpart from what the player has learned to read.

**class `PlayerKnowledgeKnowObjectTest`** — class Javadoc: ✔ · type stamp: on 261003

- Fields (0/19 documented): ✘ `GRID`, `messages`, `acidWeak`, `acidStrong`, `fire`, `evil2`, `evil3`, `animal`, `siren`, `cowardice`, `savedBrands`, `savedSlays`, `savedCurses`, `savedCave`, `savedBus`, `kind`, `item`, `known`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flags(ObjectFlag)` | — | ✘ |
| `realElements()` | — | ✔ |
| `brandNames(ItemObject)` | — | ✘ |
| `slayCodes(ItemObject)` | — | ✘ |
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✘ |
| `buildItem(TValue)` | — | ✔ |

**class `PlayerKnowledgeKnowObjectTest.MessageBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerKnowledgeKnowObjectTest.Guards`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullItem()` | — | ✘ |
| `nullKnown()` | — | ✘ |
| `kindMismatch()` | — | ✘ |
| `distantObject()` | — | ✘ |

**class `PlayerKnowledgeKnowObjectTest.Combat`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dice()` | — | ✘ |
| `baseArmourClass()` | — | ✘ |
| `pval()` | — | ✘ |
| `bonuses()` | — | ✘ |
| `bonusIsRecomputedNotKept()` | — | ✘ |

**class `PlayerKnowledgeKnowObjectTest.Properties`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `modifiers()` | — | ✘ |
| `modifierSentinels()` | — | ✘ |
| `elements()` | — | ✘ |
| `elementTheItemLacks()` | — | ✘ |
| `flagsAreAnIntersection()` | — | ✘ |
| `playersFlagsSurvive()` | — | ✘ |
| `curseObjectStopsAfterFlags()` | — | ✘ |

**class `PlayerKnowledgeKnowObjectTest.Brands`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `run(Set<Brand>, Set<Brand>, Set<Brand>)` | — | ✘ |
| `carriedAndKnown()` | — | ✘ |
| `carriedNotKnown()` | — | ✘ |
| `brandFromAnEgoNotTheKind()` | — | ✘ |
| `oneOfTwoCarried()` | — | ✘ |
| `staleBrandIsRemoved()` | — | ✘ |
| `unknownBrandIsRemoved()` | — | ✘ |
| `removesOnlyTheUnknown()` | — | ✘ |
| `twinStrength()` | — | ✘ |
| `itemWithNoBrands()` | — | ✘ |
| `nullCounterpartSet()` | — | ✘ |
| `nullCounterpartSetNothingKnown()` | — | ✘ |

**class `PlayerKnowledgeKnowObjectTest.Slays`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `run(Set<Slay>, Set<Slay>, Set<Slay>)` | — | ✘ |
| `carriedAndKnown()` | — | ✘ |
| `carriedNotKnown()` | — | ✘ |
| `oneOfTwoCarried()` | — | ✘ |
| `staleSlayIsRemoved()` | — | ✘ |
| `unknownSlayIsRemoved()` | — | ✘ |
| `removesOnlyTheUnknown()` | — | ✘ |
| `twinStrength()` | — | ✘ |
| `itemWithNoSlays()` | — | ✘ |
| `nullCounterpartSet()` | — | ✘ |
| `nullCounterpartSetNothingKnown()` | — | ✘ |

**class `PlayerKnowledgeKnowObjectTest.Curses`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `recognisedCurse()` | — | ✘ |
| `unrecognisedCurse()` | — | ✘ |
| `powerZero()` | — | ✘ |
| `oneOfTwo()` | — | ✘ |
| `staleCurse()` | — | ✘ |

**class `PlayerKnowledgeKnowObjectTest.EffectAndAwareness`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unflavouredNonWearable()` | — | ✘ |
| `unawareWearable()` | — | ✘ |
| `fullyKnownCopiesElementFlags()` | — | ✘ |
| `notFullyKnownKeepsBlankElementFlags()` | — | ✘ |

**class `PlayerKnowledgeKnowObjectTest.Reporting`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unseenJewellery()` | — | ✔ |
| `levelWithItemUnderPlayer(boolean)` | — | ✘ |
| `onTheGround()` | — | ✘ |
| `playersKnownCaveIsIrrelevant()` | — | ✘ |
| `notOnTheGrid()` | — | ✘ |
| `noLevel()` | — | ✘ |
| `alreadySeen()` | — | ✘ |


##### `PlayerKnowledgeKnowsEgoTest.java`

**Responsible for:** Tests PlayerKnowledge#knowsEgo(Player, EgoItem, ItemObject), the port of C's player_knows_ego (obj-knowledge.c) — whether the player could now put a name to an ego from the properties they can already read.

**class `PlayerKnowledgeKnowsEgoTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (0/13 documented): ✘ `acidWeak`, `acidStrong`, `fire`, `evil2`, `evil3`, `animal`, `siren`, `cowardice`, `savedBrands`, `savedSlays`, `savedCurses`, `savedConstants`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flags(ObjectFlag)` | — | ✘ |
| `ego(Flag<ObjectFlag>, Map<ObjectModifier,Random>, Map<ElementEnum,ElementInfo>, Set<Brand>, Set<Slay>, Map<Curse,CurseData>)` | — | ✔ |
| `bareEgo()` | — | ✘ |
| `egoWithModifier(Random)` | — | ✘ |
| `egoWithElement(int)` | — | ✘ |
| `itemWithStrength(Integer)` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `PlayerKnowledgeKnowsEgoTest.NothingGranted`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noEgo()` | — | ✘ |
| `bareEgoIsKnown()` | — | ✘ |

**class `PlayerKnowledgeKnowsEgoTest.Flags`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagMustBeKnown()` | — | ✘ |
| `everyFlagMustBeKnown()` | — | ✘ |
| `unrelatedFlagIsIrrelevant()` | — | ✘ |

**class `PlayerKnowledgeKnowsEgoTest.Modifiers`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fixedPositive()` | — | ✘ |
| `fixedNegative()` | — | ✘ |
| `alwaysZero()` | — | ✘ |
| `absentModifier()` | — | ✘ |
| `straddlesZero()` | — | ✘ |
| `straddlesZeroKnown()` | — | ✘ |
| `minimumIsZero()` | — | ✘ |
| `maximumIsZero()` | — | ✘ |
| `levelBonusRange()` | — | ✘ |
| `wrongModifierLearned()` | — | ✘ |

**class `PlayerKnowledgeKnowsEgoTest.Elements`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resistanceMustBeKnown()` | — | ✘ |
| `vulnerabilityMustBeKnown()` | — | ✘ |
| `levelZeroIsIgnored()` | — | ✘ |
| `wrongElementLearned()` | — | ✘ |

**class `PlayerKnowledgeKnowsEgoTest.BrandsAndSlays`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `brandMustBeKnown()` | — | ✘ |
| `brandKnownThroughTwin()` | — | ✘ |
| `otherBrand()` | — | ✘ |
| `everyBrand()` | — | ✘ |
| `slayMustBeKnown()` | — | ✘ |
| `slayKnownThroughTwin()` | — | ✘ |
| `otherSlay()` | — | ✘ |

**class `PlayerKnowledgeKnowsEgoTest.Curses`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `egoWithCurse(Curse)` | — | ✘ |
| `curseMustBeKnown()` | — | ✘ |
| `otherCurse()` | — | ✘ |

**class `PlayerKnowledgeKnowsEgoTest.Combined`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `richEgo()` | — | ✔ |
| `learnEverythingExcept(String)` | — | ✘ |
| `allKnown()` | — | ✘ |
| `anyOneMissing()` | — | ✘ |


##### `PlayerKnowledgeLearnFlagTest.java`

**Responsible for:** Tests PlayerKnowledge#learnFlag, the port of C's player_learn_flag, on the one thing that distinguishes it from its sibling wrappers: it has no already-known guard, so C's trailing update_player_object_knowledge is the call that does the work whenever the rune was already known.

**class `PlayerKnowledgeLearnFlagTest`** — class Javadoc: ✔ · type stamp: on 261004

- Fields (0/11 documented): ✘ `messages`, `savedRunes`, `savedBrands`, `savedSlays`, `savedCurses`, `savedCave`, `savedBus`, `inventorySignals`, `player`, `item`, `known`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✔ |
| `tearDown()` | — | ✘ |
| `knownFlagRefreshesTheObject()` | — | ✔ |
| `knownFlagIsSilent()` | — | ✔ |
| `knownFlagUpdatesOnce()` | — | ✔ |
| `newFlagIsLearned()` | — | ✔ |
| `knownCopyIsIntersected()` | — | ✔ |
| `flagWithoutARune()` | — | ✔ |

**class `PlayerKnowledgeLearnFlagTest.CountingBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerKnowledgeNonCurseRunesKnownTest.java`

**Responsible for:** Tests PlayerKnowledge#nonCurseRunesKnown(ItemObject), the port of C's object_non_curse_runes_known (obj-knowledge.c).

**class `PlayerKnowledgeNonCurseRunesKnownTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (0/4 documented): ✘ `FIRE`, `ANIMAL`, `item`, `known`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshPair()` | — | ✔ |
| `modifier(ObjectModifier, int)` | — | ✘ |
| `element(ElementEnum, int)` | — | ✘ |
| `flags(ObjectFlag)` | — | ✘ |
| `blankPairIsKnown()` | — | ✘ |
| `noCounterpart()` | — | ✘ |

**class `PlayerKnowledgeNonCurseRunesKnownTest.CombatBonuses`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `toAC()` | — | ✘ |
| `toHit()` | — | ✘ |
| `toDam()` | — | ✘ |
| `counterpartHasMore()` | — | ✘ |

**class `PlayerKnowledgeNonCurseRunesKnownTest.Modifiers`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `hidden()` | — | ✘ |
| `shown()` | — | ✘ |
| `wrongValue()` | — | ✘ |
| `extraOnCounterpart()` | — | ✘ |
| `zeroEqualsAbsent()` | — | ✘ |

**class `PlayerKnowledgeNonCurseRunesKnownTest.Elements`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noEntry()` | — | ✘ |
| `entryAtZero()` | — | ✘ |
| `shown()` | — | ✘ |
| `anyNonZeroCovers()` | — | ✘ |
| `objectLevelZero()` | — | ✘ |
| `extraOnCounterpart()` | — | ✘ |
| `wrongElement()` | — | ✘ |

**class `PlayerKnowledgeNonCurseRunesKnownTest.Containments`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `brandHidden()` | — | ✘ |
| `brandShown()` | — | ✘ |
| `slayHidden()` | — | ✘ |
| `slayShown()` | — | ✘ |
| `flagHidden()` | — | ✘ |
| `flagShown()` | — | ✘ |
| `flagPartlyCovered()` | — | ✘ |
| `cursesAreIgnored()` | — | ✘ |


##### `PlayerKnowledgeObjectLearnOnWieldTest.java`

**Responsible for:** Tests PlayerKnowledge#objectLearnOnWield, the port of C's object_learn_on_wield (obj-knowledge.c).

**class `PlayerKnowledgeObjectLearnOnWieldTest`** — class Javadoc: ✔ · type stamp: on 261004

- Fields (7/15 documented): ✔ `fear`, `sustainStrength`, `impairHp`, `cowardice`, `weakness`, `vulnerability`, `burning` — ✘ `SAVED_FIELDS`, `SAVED`, `TREMBLE`, `strength`, `player`, `knowledge`, `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✘ |
| `seedRegistry()` | — | ✔ |
| `restore()` | — | ✘ |
| `flagProperty(ObjectFlag, ObjectFlagType, ObjectFlagID, String, String)` | — | ✘ |
| `curse(String, Flag<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, int, int)` | — | ✘ |
| `flags(ObjectFlag)` | — | ✘ |
| `elementInfo(int)` | — | ✘ |
| `field(String)` | — | ✘ |
| `poke(Object, String, Object)` | — | ✘ |
| `clearMessageLog()` | — | ✘ |
| `ring(ObjectFlag)` | — | ✔ |
| `ringOfStrength(int, ObjectFlag)` | — | ✔ |
| `cursedRing(Curse)` | — | ✘ |
| `announced()` | — | ✘ |
| `startPlaying()` | — | ✘ |
| `wield(ItemObject)` | — | ✘ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |

**class `PlayerKnowledgeObjectLearnOnWieldTest.CapturingBus`** — class Javadoc: ✘ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerKnowledgeObjectLearnOnWieldTest.FlagsAndModifiers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unknownFlagIsLearned()` | — | ✘ |
| `knownFlagIsSilent()` | — | ✘ |
| `notPlayingIsQuiet()` | — | ✘ |
| `notObviousIsNotLearned()` | — | ✘ |
| `positiveModifier()` | — | ✘ |
| `negativeModifier()` | — | ✘ |
| `knownModifierIsSilent()` | — | ✘ |
| `absentModifier()` | — | ✘ |
| `sustainFollowsModifier()` | — | ✘ |
| `sustainNeedsTheFlag()` | — | ✘ |
| `sustainWithoutModifier()` | — | ✘ |
| `itemFlagsSurvive()` | — | ✘ |

**class `PlayerKnowledgeObjectLearnOnWieldTest.OncePerItem`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `marks()` | — | ✘ |
| `secondCallReturnsEarly()` | — | ✘ |
| `alreadyWorn()` | — | ✘ |
| `noKnownCounterpart()` | — | ✘ |

**class `PlayerKnowledgeObjectLearnOnWieldTest.Curses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curseFlagRoute()` | — | ✘ |
| `curseModifierRoute()` | — | ✘ |
| `curseToARoute()` | — | ✘ |
| `elementRouteKnownElement()` | — | ✘ |
| `elementRouteUnknownElement()` | — | ✘ |
| `uncursed()` | — | ✘ |


##### `PlayerKnowledgeObjectSetBaseKnownTest.java`

**Responsible for:** Tests PlayerKnowledge#objectSetBaseKnown(Player, ItemObject), the port of C's object_set_base_known (obj-knowledge.c) — what a player is entitled to know about an object from recognizing what kind of thing it is, before any of its own enchantment is learned.

**class `PlayerKnowledgeObjectSetBaseKnownTest`** — class Javadoc: ✔ · type stamp: on 261011

- Fields (0/7 documented): ✘ `savedBrands`, `savedSlays`, `savedCurses`, `player`, `kind`, `item`, `known`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `effect()` | — | ✘ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `build(TValue)` | — | ✔ |

**class `PlayerKnowledgeObjectSetBaseKnownTest.AlwaysCopied`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `identity()` | — | ✘ |
| `missing()` | — | ✘ |

**class `PlayerKnowledgeObjectSetBaseKnownTest.DiceAndArmourClass`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `multipliedByKnowledge()` | — | ✘ |
| `nothingKnown()` | — | ✘ |
| `alreadyHeldIsKept()` | — | ✘ |
| `kindNotObject()` | — | ✘ |

**class `PlayerKnowledgeObjectSetBaseKnownTest.ToHit`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `standardOverwrites()` | — | ✘ |
| `nonStandardUntouched()` | — | ✘ |
| `bodyArmourStandard()` | — | ✘ |
| `bodyArmourNonStandard()` | — | ✘ |

**class `PlayerKnowledgeObjectSetBaseKnownTest.PvalAndEffect`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `launcherPval()` | — | ✘ |
| `wearablePvalHidden()` | — | ✘ |
| `unflavouredNonWearable()` | — | ✘ |
| `awareFlavour()` | — | ✘ |
| `unawareFlavour()` | — | ✘ |
| `wearableActivation()` | — | ✘ |
| `wearableActivationUnaware()` | — | ✘ |
| `wearableKindWithoutEffect()` | — | ✘ |


##### `PlayerKnowsBrandSlayTest.java`

**Responsible for:** Tests Player#playerKnowsBrand and Player#playerKnowsSlay, the ports of C's player_knows_brand and player_knows_slay (obj-knowledge.c).

**class `PlayerKnowsBrandSlayTest`** — class Javadoc: ✔ · type stamp: —

- Fields (3/9 documented): ✔ `SAVED_FIELDS`, `SAVED`, `player` — ✘ `weakAcid`, `strongAcid`, `weakFire`, `evil3`, `evil5`, `undead3`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✔ |
| `field(String)` | — | ✔ |
| `newPlayer()` | — | ✔ |

**class `PlayerKnowsBrandSlayTest.Nothing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `knowsNoBrand()` | — | ✘ |
| `knowsNoSlay()` | — | ✘ |

**class `PlayerKnowsBrandSlayTest.Learned`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `knowsTheWholeBrandGroup()` | — | ✔ |
| `knowsTheWholeSlayGroup()` | — | ✔ |
| `brandsAndSlaysAreIndependent()` | — | ✔ |
| `isPerPlayer()` | — | ✔ |
| `followsReplacement()` | — | ✔ |

**class `PlayerKnowsBrandSlayTest.FreshPlayer`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ownsEmptyKnowledge()` | — | ✘ |
| `brandIsFalse()` | — | ✘ |
| `slayIsFalse()` | — | ✘ |

**class `PlayerKnowsBrandSlayTest.NoKnowledgeObject`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `brandThrows()` | — | ✘ |
| `slayThrows()` | — | ✘ |


##### `PlayerNameBuildProbsTest.java`

**Responsible for:** Tests NameCreator#buildProbs — the port of C's build_prob (src/randname.c:44), which turns a list of words into the Markov counts that randname_make later draws letters from.

**class `PlayerNameBuildProbsTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (3/3 documented): ✔ `S_WORD`, `E_WORD`, `TOTAL`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cell(int, int, int)` | — | ✔ |
| `toMap(int[][][])` | — | ✔ |
| `sum(int[][][])` | — | ✔ |
| `assertOnly(int[][][], Map<String,Integer>)` | — | ✔ |
| `build(String)` | — | ✔ |

**class `PlayerNameBuildProbsTest.OneLetterWord`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singleLetter()` | — | ✔ |

**class `PlayerNameBuildProbsTest.TwoLetterWord`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `twoLetters()` | — | ✔ |
| `doubledLetter()` | — | ✔ |

**class `PlayerNameBuildProbsTest.LongerWord`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sixLetters()` | — | ✔ |

**class `PlayerNameBuildProbsTest.WordList`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `twoWordsDoNotChain()` | — | ✔ |
| `repeatedWord()` | — | ✔ |
| `emptyList()` | — | ✔ |

**class `PlayerNameBuildProbsTest.CharacterHandling`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `mixedCase()` | — | ✔ |

**class `PlayerNameBuildProbsTest.GeneratorInvariants`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `totalIsTheRowSum()` | — | ✔ |
| `startRowIsPopulated()` | — | ✔ |
| `countsPerWord()` | — | ✔ |

**class `PlayerNameBuildProbsTest.TableShape`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `dimensions()` | — | ✔ |
| `freshEachCall()` | — | ✔ |

**class `PlayerNameBuildProbsTest.EmptyWord`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyWordSkipped()` | — | ✔ |


##### `PlayerNameFindRomanSuffixStartTest.java`

**Responsible for:** Tests PlayerName.findRomanSuffixStart — the port of C's find_roman_suffix_start (src/player-birth.c:1335), which locates the Roman numeral on the end of a player name so a quickstart birth can increment it.

**class `PlayerNameFindRomanSuffixStartTest`** — class Javadoc: ✔ · type stamp: on 260831

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `find(String)` | — | ✔ |

**class `PlayerNameFindRomanSuffixStartTest.ValidSuffix`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinary()` | — | ✔ |
| `everySymbol()` | — | ✔ |
| `lastSpaceWins()` | — | ✔ |
| `repeatedSpaces()` | — | ✔ |
| `leadingSpace()` | — | ✔ |
| `singleCharacter()` | — | ✔ |

**class `PlayerNameFindRomanSuffixStartTest.InvalidSuffix`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `badCharacterLast()` | — | ✔ |
| `badCharacterFirst()` | — | ✔ |
| `plainSurname()` | — | ✔ |
| `lowerCase()` | — | ✔ |
| `arabicNumeral()` | — | ✔ |

**class `PlayerNameFindRomanSuffixStartTest.NoSpace`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `singleWord()` | — | ✔ |
| `bareNumeral()` | — | ✔ |
| `emptyName()` | — | ✔ |

**class `PlayerNameFindRomanSuffixStartTest.EmptySuffix`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `trailingSpace()` | — | ✔ |
| `onlySpace()` | — | ✔ |
| `numeralThenSpace()` | — | ✔ |


##### `PlayerNamePlayerRandomNameTest.java`

**Responsible for:** Tests PlayerName.playerRandomName — the port of C's player_random_name (src/player.c), the name a character is born with.

**class `PlayerNamePlayerRandomNameTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/2 documented): ✔ `MANY` — ✘ `playerName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tolkienish()` | — | ✔ |
| `load(String)` | — | ✔ |
| `load(List<String>)` | — | ✔ |
| `setUp()` | — | ✘ |

**class `PlayerNamePlayerRandomNameTest.Capitalisation`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstLetterOnly()` | — | ✔ |
| `restStaysLowerCase()` | — | ✔ |

**class `PlayerNamePlayerRandomNameTest.LengthBounds`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withinFourToEight()` | — | ✔ |
| `bothEdgesReachable()` | — | ✔ |

**class `PlayerNamePlayerRandomNameTest.Section`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tolkienSectionOnly()` | — | ✔ |

**class `PlayerNamePlayerRandomNameTest.TheName`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `variesBetweenCalls()` | — | ✔ |
| `alwaysContainsAVowel()` | — | ✔ |


##### `PlayerNamePlayerSafeNameTest.java`

**Responsible for:** Tests PlayerName.playerSafeName — the port of C's player_safe_name (src/player.c:392), which reduces a player name to something that can be used as a savefile name.

**class `PlayerNamePlayerSafeNameTest`** — class Javadoc: ✔ · type stamp: on 260901

- Fields (1/1 documented): ✔ `ROOMY`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `safe(int, String)` | — | ✘ |

**class `PlayerNamePlayerSafeNameTest.SuffixStripping`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinary()` | — | ✔ |
| `trailingSpace()` | — | ✔ |
| `twoWordName()` | — | ✔ |
| `nothingButSuffix()` | — | ✔ |

**class `PlayerNamePlayerSafeNameTest.Sanitising`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `alphanumericSurvives()` | — | ✔ |
| `pathCharacters()` | — | ✔ |
| `allSymbols()` | — | ✔ |

**class `PlayerNamePlayerSafeNameTest.Truncation`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cutsLongName()` | — | ✔ |
| `cutsAfterStripping()` | — | ✔ |
| `exactFit()` | — | ✔ |
| `zeroBuffer()` | — | ✔ |

**class `PlayerNamePlayerSafeNameTest.Fallback`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nullName()` | — | ✔ |
| `emptyName()` | — | ✔ |
| `oneByteBuffer()` | — | ✔ |
| `truncatesToBuffer()` | — | ✔ |
| `oneShortOfWhole()` | — | ✔ |
| `wholeWordAtTheBoundary()` | — | ✔ |
| `roomyBuffer()` | — | ✔ |

**class `PlayerNamePlayerSafeNameTest.SuffixFlagIsIgnored`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bothValuesAgree()` | — | ✔ |

**class `PlayerNamePlayerSafeNameTest.NonAscii`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `accentedLetter()` | — | ✔ |
| `nonAsciiDigit()` | — | ✔ |


##### `PlayerNameRandnameMakeTest.java`

**Responsible for:** Tests PlayerName.randnameMake — the port of C's randname_make (src/randname.c), W.

**class `PlayerNameRandnameMakeTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (2/3 documented): ✔ `VOWELS`, `MANY` — ✘ `playerName`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `tolkienish()` | — | ✔ |
| `load(String)` | — | ✔ |
| `load(List<String>)` | — | ✔ |
| `setUp()` | — | ✘ |

**class `PlayerNameRandnameMakeTest.SingleWalk`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `deterministicWalk()` | — | ✔ |
| `oneLetterWord()` | — | ✔ |
| `doubledLetter()` | — | ✔ |

**class `PlayerNameRandnameMakeTest.RejectingAnEnding`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `vowelIsRequired()` | — | ✔ |
| `minimumLengthIsRequired()` | — | ✔ |
| `bothEndingsLegal()` | — | ✔ |

**class `PlayerNameRandnameMakeTest.RestartingAnAttempt`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lengthIsPerAttempt()` | — | ✔ |
| `recoversFromOverlongAttempt()` | — | ✔ |

**class `PlayerNameRandnameMakeTest.LengthBounds`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withinBoundsInclusive()` | — | ✔ |
| `minEqualsMax()` | — | ✔ |
| `wordOfExactlyMaxLetters()` | — | ✔ |

**class `PlayerNameRandnameMakeTest.GeneratedWord`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startsFromTheMarkerPair()` | — | ✔ |
| `lowerCaseLettersOnly()` | — | ✔ |
| `alwaysContainsAVowel()` | — | ✔ |
| `generatesNewWords()` | — | ✔ |

**class `PlayerNameRandnameMakeTest.NameType`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `drawsFromTheGivenSection()` | — | ✔ |


##### `PlayerNoticeChainTest.java`

**Responsible for:** Tests PlayerCalcs#noticeStuff(Player) and the inventory rebuild it ends with — the port of C's notice_stuff (player-calcs.c) and the top of calc_inventory.

**class `PlayerNoticeChainTest`** — class Javadoc: ✔ · type stamp: —

- Fields (9/9 documented): ✔ `PACK_SIZE`, `player`, `level`, `savedConstants`, `savedPlayer`, `savedCave`, `savedBodies`, `savedRaces`, `savedCurses`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `curseField()` | — | ✔ |
| `constantsField()` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `race(PlayerBody)` | — | ✔ |
| `item()` | — | ✔ |
| `seedWorld()` | — | ✔ |
| `restoreWorld()` | — | ✔ |
| `confusedPlayerCannotAttack()` | — | ✔ |
| `unconfusedPlayerFindsNothing()` | — | ✔ |

**class `PlayerNoticeChainTest.Dispatcher`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nothingPendingDoesNothing()` | — | ✔ |
| `combineIsCarriedOutAndCleared()` | — | ✔ |
| `ignoreCombineRunsImmediately()` | — | ✔ |
| `monsterMessageFlagIsCleared()` | — | ✔ |
| `allThreeAreDealtWith()` | — | ✔ |

**class `PlayerNoticeChainTest.Inventory`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyGearGivesEmptyPack()` | — | ✔ |
| `carriedItemReachesThePack()` | — | ✔ |
| `packIsRebuilt()` | — | ✔ |


##### `PlayerOfHasNotTimedTest.java`

**Responsible for:** Tests PlayerUtils#playerHasObjectFlagIgnoringTimed(Player, ObjectFlag), the port of C's player_of_has_not_timed (player-timed.c:747).

**class `PlayerOfHasNotTimedTest`** — class Javadoc: ✔ · type stamp: on 260829

- Fields (3/3 documented): ✔ `player`, `state`, `body`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsOf(ObjectFlag)` | — | ✔ |
| `setField(Object, Class<?>, String, Object)` | — | ✔ |
| `race(Flag<ObjectFlag>)` | — | ✔ |
| `playerClass(Flag<ObjectFlag>)` | — | ✔ |
| `itemWithFlags(Flag<ObjectFlag>)` | — | ✔ |
| `wear(String, ItemObject)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `has(ObjectFlag)` | — | ✔ |
| `barePlayerHasNothing()` | — | ✔ |
| `racialFlagCounts()` | — | ✔ |
| `classFlagCounts()` | — | ✔ |
| `braveryIsLevelGated()` | — | ✔ |
| `wornItemFlagCounts()` | — | ✔ |
| `lateSlotIsReached()` | — | ✔ |
| `flagsFromSeveralItemsAccumulate()` | — | ✔ |
| `innateAndWornCombine()` | — | ✔ |
| `timedFlagInStateDoesNotCount()` | — | ✔ |
| `runningTimedEffectGrantsNothing()` | — | ✔ |


##### `PlayerOptionsCopyTest.java`

**Responsible for:** Tests PlayerOptions#copy, the port of the plain struct assignment C uses to save and restore player->opts around birth (opts_save = p->opts, player-birth.c:403).

**class `PlayerOptionsCopyTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (0/1 documented): ✘ `source`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagOf(PlayerOptions)` | — | ✘ |
| `setIntField(PlayerOptions, String, int)` | — | ✘ |
| `getIntField(PlayerOptions, String)` | — | ✘ |
| `setUp()` | — | ✘ |

**class `PlayerOptionsCopyTest.TheSettingsAreCarriedAcross`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `optionsThatAreOnAreCopiedOn()` | — | ✘ |
| `optionsThatAreOffAreCopiedOff()` | — | ✘ |
| `theNumericSettingsAreCopied()` | — | ✘ |

**class `PlayerOptionsCopyTest.NothingIsSharedWithTheSource`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theCopyIsADifferentObject()` | — | ✘ |
| `theFlagSetIsNotShared()` | — | ✘ |
| `switchingAnOptionOnTheCopyDoesNotAffectTheSource()` | — | ✘ |
| `switchingAnOptionOnTheSourceDoesNotAffectAnEarlierCopy()` | — | ✘ |
| `twoCopiesOfOneSourceAreIndependent()` | — | ✘ |
| `aCopyOfACopyIsStillIndependent()` | — | ✘ |


##### `PlayerOptionsInitCheatTest.java`

**Responsible for:** Tests PlayerOptions.optionsInitCheat(), the port of C's options_init_cheat (option.c:133-143).

**class `PlayerOptionsInitCheatTest`** — class Javadoc: ✔ · type stamp: on 260907

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagOf(PlayerOptions)` | — | ✘ |
| `setAllOn(PlayerOptions)` | — | ✘ |

**class `PlayerOptionsInitCheatTest.ItClearsEachCheatScorePair`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearsTheCheatOptionItself(PlayerOptionEnum)` | — | ✘ |
| `clearsScoreHearWhenCheatHearIsOn()` | — | ✘ |
| `clearsScoreRoomWhenCheatRoomIsOn()` | — | ✘ |
| `clearsScoreXtraWhenCheatXtraIsOn()` | — | ✘ |
| `clearsScoreLiveWhenCheatLiveIsOn()` | — | ✘ |
| `clearsAScoreOptionEvenWhenItsCheatOptionWasAlreadyOff()` | — | ✘ |

**class `PlayerOptionsInitCheatTest.ItLeavesUnrelatedPagesAlone`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doesNotTouchTheInterfaceOptionImmediatelyBeforeTheFirstCheatPair()` | — | ✘ |
| `doesNotTouchTheBirthOptionImmediatelyAfterTheLastScorePair()` | — | ✘ |
| `doesNotTouchOtherInterfaceOptions()` | — | ✘ |
| `doesNotTouchOtherBirthOptions()` | — | ✘ |

**class `PlayerOptionsInitCheatTest.ItIsIdempotentWhenAlreadyClear`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `doingNothingWhenAllCheatAndScoreOptionsAreAlreadyOff()` | — | ✘ |


##### `PlayerOptionsInitDefaultsTest.java`

**Responsible for:** Tests PlayerOptions.initDefaults(), the port of C's options_init_defaults (option.c).

**class `PlayerOptionsInitDefaultsTest`** — class Javadoc: ✔ · type stamp: on 261010

- Fields (1/3 documented): ✔ `NORMALLY_ON` — ✘ `originalUserDir`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagOf(PlayerOptions)` | — | ✘ |
| `setIntField(PlayerOptions, String, int)` | — | ✘ |
| `getIntField(PlayerOptions, String)` | — | ✘ |
| `writeCustomizedFile(String, String)` | — | ✘ |
| `redirectUserDirAndBus(Path)` | — | ✘ |
| `restoreUserDirAndBus()` | — | ✘ |
| `everyOptionTakesItsNormalValueWhenThereAreNoCustomizedFiles()` | — | ✘ |
| `theDelayFactorBecomesFortyAndTheHitpointWarningThree()` | — | ✘ |
| `theMovementDelayAndNameSuffixAreLeftAlone()` | — | ✔ |
| `earlierSettingsAreReplacedNotKept()` | — | ✘ |
| `aCustomizedBirthFileOverridesTheBirthPageOnly()` | — | ✘ |
| `aCustomizedInterfaceFileOverridesTheInterfacePageOnly()` | — | ✘ |
| `aCustomizedCheatFileIsNeverRead()` | — | ✔ |
| `theCheatAndScorePagesStayOff()` | — | ✘ |
| `anUnreadableCustomizedFileDoesNotStopTheNumericDefaults()` | — | ✔ |

**class `PlayerOptionsInitDefaultsTest.SilentBus`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerOptionsRestoreCustomTest.java`

**Responsible for:** Tests PlayerOptions.restoreCustom(Flag, PlayerOptionTypes), the port of C's options_restore_custom (option.c).

**class `PlayerOptionsRestoreCustomTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (0/3 documented): ✘ `originalUserDir`, `realBus`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `restoreCustom(PlayerOptions, PlayerOptionTypes)` | — | ✘ |
| `flagOf(PlayerOptions)` | — | ✘ |
| `switchOn(PlayerOptions, PlayerOptionEnum)` | — | ✘ |
| `customizedFile(PlayerOptionTypes)` | — | ✘ |
| `writeCustomizedFile(PlayerOptionTypes, String)` | — | ✔ |
| `redirectUserDirAndBus(Path)` | — | ✘ |
| `restoreUserDirAndBus()` | — | ✘ |

**class `PlayerOptionsRestoreCustomTest.WhenTheCustomizedFileIsMissing`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reportsSuccessWhenNoFileExists()` | — | ✔ |
| `fallsBackToTheMaintainerDefaults()` | — | ✘ |
| `leavesOtherPagesUntouched()` | — | ✘ |
| `saysNothingToThePlayer()` | — | ✔ |

**class `PlayerOptionsRestoreCustomTest.WhenTheFileIsWellFormed`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aYesLineTurnsAnOptionOn()` | — | ✘ |
| `aNoLineTurnsAnOptionOff()` | — | ✔ |
| `aBirthLineWorksOnTheBirthPage()` | — | ✘ |
| `anOptionNotMentionedInTheFileKeepsItsCurrentValue()` | — | ✔ |
| `aLaterLineOverridesAnEarlierOneForTheSameOption()` | — | ✘ |
| `blankLinesAndCommentsAreSkippedWithoutAMessage()` | — | ✘ |
| `aFileInTheShapeCWritesItIsReadWithoutAMessage()` | — | ✔ |

**class `PlayerOptionsRestoreCustomTest.WhenTheFileHasStrayWhitespace`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leadingSpacesBeforeTheTagAreAllowed()` | — | ✘ |
| `aLeadingTabIsAllowed()` | — | ✘ |
| `trailingSpacesAfterTheValueAreAllowed()` | — | ✘ |
| `anIndentedCommentIsSkippedSilently()` | — | ✘ |

**class `PlayerOptionsRestoreCustomTest.WhenTheFileHasBadLines`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertSkippedWithOneMessage(String, PlayerOptionEnum)` | — | ✘ |
| `aLineWithoutTheOptionTagIsSkipped()` | — | ✘ |
| `aLineWithOnlyTheTagAndNoNameIsSkipped()` | — | ✘ |
| `aTextLineBeforeACommentIsSkipped()` | — | ✘ |
| `anExtraColonAfterTheValueIsSkipped()` | — | ✘ |
| `aTrailingColonIsSkipped()` | — | ✔ |
| `aValueThatIsNotYesOrNoIsSkipped()` | — | ✘ |
| `anEmptyValueIsSkipped()` | — | ✘ |
| `anUpperCaseValueIsSkipped()` | — | ✔ |
| `aValueWithTextAfterItIsSkipped()` | — | ✘ |
| `aSpaceBeforeTheValueIsSkipped()` | — | ✘ |
| `anUnknownOptionNameIsSkipped()` | — | ✘ |
| `aPartOfAnOptionNameIsSkipped()` | — | ✔ |
| `aNameWithAnExtraPrefixIsSkipped()` | — | ✘ |
| `anEmptyNameIsSkipped()` | — | ✘ |
| `anUpperCaseNameIsSkipped()` | — | ✔ |
| `aBirthOptionInTheInterfaceFileIsSkipped()` | — | ✔ |
| `anInterfaceOptionInTheBirthFileIsSkipped()` | — | ✘ |
| `aCheatOptionInTheInterfaceFileIsSkipped()` | — | ✘ |
| `thePlaceholderOptionNoneIsSkipped()` | — | ✔ |
| `everyBadLineGetsItsOwnMessage()` | — | ✘ |

**class `PlayerOptionsRestoreCustomTest.WhenTheFileCannotBeRead`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anUnreadableFileFailsTheLoad()` | — | ✔ |
| `aFileThatIsNotValidUtf8FailsTheLoad()` | — | ✔ |

**class `PlayerOptionsRestoreCustomTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |


##### `PlayerOptionsRestoreMaintainerTest.java`

**Responsible for:** Tests PlayerOptions.restoreMaintainer(Flag, PlayerOptionTypes), the port of C's options_restore_maintainer (option.c).

**class `PlayerOptionsRestoreMaintainerTest`** — class Javadoc: ✔ · type stamp: on 260907

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `restoreMaintainer(PlayerOptions, PlayerOptionTypes)` | — | ✘ |
| `flagOf(PlayerOptions)` | — | ✘ |

**class `PlayerOptionsRestoreMaintainerTest.ItResetsEachOptionToItsOwnNormalValue`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aNormallyOnOptionIsSwitchedOnFromEmpty()` | — | ✘ |
| `aNormallyOffOptionThatWasSwitchedOnIsSwitchedBackOff()` | — | ✘ |
| `aNormallyOnOptionThatWasSwitchedOffIsSwitchedBackOn()` | — | ✘ |
| `birthOptionsResetToTheirOwnNormals()` | — | ✘ |

**class `PlayerOptionsRestoreMaintainerTest.ItLeavesOtherPagesAlone`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resettingInterfaceDoesNotTouchBirthOptions()` | — | ✘ |
| `resettingBirthDoesNotTouchInterfaceOptions()` | — | ✘ |


##### `PlayerProgressionTest.java`

**Responsible for:** Tests Player's options, gear and timed-effect reads, and the several methods still awaiting their subsystems.

**class `PlayerProgressionTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `intField(String)` | — | ✔ |

**class `PlayerProgressionTest.Timed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `absentEffectReadsZero()` | — | ✔ |
| `presentEffectReadsItsTurns()` | — | ✔ |
| `theRemainingTimedStubsChangeNothing()` | — | ✔ |
| `everyEffectHasAnEntry()` | — | ✔ |

**class `PlayerProgressionTest.Accessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `optionsAnswer()` | — | ✔ |
| `gearStartsEmpty()` | — | ✔ |
| `classStartsUnset()` | — | ✔ |
| `coercionSkipStartsAtZero()` | — | ✔ |

**class `PlayerProgressionTest.Stubs`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setRecallDepthIsAStub()` | — | ✔ |
| `unportedTurnLoopEntryPointsAreStubbed()` | — | ✔ |

**class `PlayerProgressionTest.Light`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `savedConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyHandedPlayerHasNoLight()` | — | ✔ |
| `seedWorld()` | — | ✔ |
| `restoreWorld()` | — | ✔ |


##### `PlayerPropertyAbilityTest.java`

**Responsible for:** Tests the ability half of PlayerProperty - classHasAbility, raceHasAbility and the group field - against C's class_has_ability and race_has_ability (player-properties.c) and the PLAYER_FLAG_* enum (player-properties.h).

**class `PlayerPropertyAbilityTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `property(PlayerPropertyType, PlayerFlag, ObjectFlag, ElementEnum, int)` | — | ✔ |
| `classWith(Flag<ObjectFlag>, Flag<PlayerFlag>)` | — | ✔ |
| `raceWith(Flag<ObjectFlag>, Flag<PlayerFlag>, ElementEnum, int)` | — | ✔ |
| `classHas(PlayerClass, PlayerProperty)` | — | ✔ |
| `raceHas(PlayerRace, PlayerProperty)` | — | ✔ |
| `invoke(String, Class<?>, Object, PlayerProperty)` | — | ✔ |
| `playerFlagTypeOrdinalsMatchC()` | on 261009 | ✔ |
| `newPropertyGroupIsNone()` | on 261009 | ✔ |
| `setGroupReplacesTheValue()` | on 261009 | ✔ |
| `classPlayerFlagProperty()` | on 261009 | ✔ |
| `classObjectFlagProperty()` | on 261009 | ✔ |
| `classNeverHasElementProperty()` | on 261009 | ✔ |
| `raceFlagProperties()` | on 261009 | ✔ |
| `raceElementPropertyIsExactEquality()` | on 261009 | ✔ |
| `raceElementPropertyIsPerElement()` | on 261009 | ✔ |
| `raceUnrecognizedTypeMatchesNothing()` | on 261009 | ✔ |


##### `PlayerQuestResetTest.java`

**Responsible for:** Tests PlayerQuest#playerQuestsReset and Quest#copy() together, the ports of C's player_quests_reset and the four field assignments in its copy loop (player-quest.c:158-172).

**class `PlayerQuestResetTest`** — class Javadoc: ✔ · type stamp: on 260903

- Fields (2/2 documented): ✔ `player`, `savedQuests`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `questsField()` | — | ✔ |
| `quest(int, String, int, MonsterRace, int, int)` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `copiesEveryStandardQuest()` | — | ✔ |
| `carriesTheDescriptiveFieldsOver()` | — | ✔ |
| `raceIsSharedNotCopied()` | — | ✔ |
| `indexAndCurrentNumberAreZeroed()` | — | ✔ |
| `producesIndependentQuestObjects()` | — | ✔ |
| `resettingTwiceReplacesRatherThanAppends()` | — | ✔ |


##### `PlayerQuestsAndUpkeepAccessorTest.java`

**Responsible for:** Tests Player#setQuests(ArrayList) and Player#setUpkeep(PlayerUpkeep) — the ports of the plain field writes C makes to p->quests and p->upkeep.

**class `PlayerQuestsAndUpkeepAccessorTest`** — class Javadoc: ✔ · type stamp: on 260903

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build()` | — | ✔ |

**class `PlayerQuestsAndUpkeepAccessorTest.SetQuests`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesByIdentity()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |
| `acceptsEmptyList()` | — | ✔ |

**class `PlayerQuestsAndUpkeepAccessorTest.SetUpkeep`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesByIdentity()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |


##### `PlayerQuiverCapacityTest.java`

**Responsible for:** Tests the three capacity helpers behind ObjectUtils#combinePack — ObjectUtils#packSlotsUsed, ObjectUtils#preferredQuiverSlot and quiverAbsorbNum — the ports of C's pack_slots_used (obj-gear.c), preferred_quiver_slot (obj-gear.c) and quiver_absorb_num (obj-gear.c).

**class `PlayerQuiverCapacityTest`** — class Javadoc: ✔ · type stamp: on 260824

- Fields (4/11 documented): ✔ `PACK_SIZE`, `QUIVER_SLOTS`, `SLOT_SIZE`, `THROWN_MULT` — ✘ `savedPlayer`, `savedConstants`, `savedBodies`, `savedRaces`, `savedCurses`, `kind`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedGlobals()` | — | ✘ |
| `restoreGlobals()` | — | ✘ |
| `arrows(int)` | — | ✔ |
| `thrown(int)` | — | ✔ |
| `potion(int)` | — | ✔ |
| `inscribed(ItemObject, String)` | — | ✔ |
| `item(TValue, int, boolean)` | — | ✔ |
| `testRace(PlayerBody)` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `buildPlayer()` | — | ✔ |
| `packSlotsUsed()` | — | ✔ |
| `preferredQuiverSlot(ItemObject)` | — | ✔ |
| `quiverAbsorbNum(ItemObject, int, int)` | — | ✔ |
| `gear()` | — | ✔ |
| `quiver()` | — | ✔ |
| `switchOn(PlayerOptionEnum)` | — | ✔ |
| `equip(ItemObject)` | — | ✔ |

**class `PlayerQuiverCapacityTest.PackSlots`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptyGearUsesNothing()` | — | ✘ |
| `ordinaryItemsCostOneEach()` | — | ✘ |
| `equippedItemsCostNothing()` | — | ✔ |
| `fullQuiverSlotCostsOne()` | — | ✔ |
| `partUsedQuiverSlotCostsOne()` | — | ✔ |
| `looseAmmoCostsOneSlot()` | — | ✔ |
| `thrownChargedAtMultiplier()` | — | ✔ |
| `invenCanStackPartial(ItemObject, ItemObject, ObjectStackEnum, ObjectStackEnum)` | — | ✔ |

**class `PlayerQuiverCapacityTest.QuiverAbsorb`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `nonQuiverItemRefused()` | — | ✔ |
| `onePackSlotBuysOneQuiverSlot()` | — | ✔ |
| `noPackSlotsAdmitsNothing()` | — | ✔ |
| `partUsedSlotIsFree()` | — | ✔ |
| `neverMoreThanTheStack()` | — | ✔ |
| `thrownAdmittedAtMultiplier()` | — | ✔ |
| `uninscribedThrownHasNoSlot()` | — | ✔ |
| `fullQuiverHasNoRoom()` | — | ✔ |
| `overfullSlotThrows()` | — | ✔ |
| `fullSlotIsNotAnError()` | — | ✔ |
| `invenCanStackPartial(ItemObject, ItemObject, Flag<ObjectStackEnum>, Flag<ObjectStackEnum>)` | — | ✔ |

**class `PlayerQuiverCapacityTest.PreferredSlot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `uninscribedPrefersNothing()` | — | ✘ |
| `fireTagNamesSlot()` | — | ✘ |
| `throwTagNamesSlot()` | — | ✘ |
| `laterTagIsFound()` | — | ✔ |
| `unrelatedInscriptionPrefersNothing()` | — | ✘ |
| `roguelikeKeysetSwapsTheFireKey()` | — | ✔ |
| `nonQuiverItemPrefersNothing()` | — | ✔ |
| `digitIsTakenRaw()` | — | ✔ |
| `unrelatedTagIsSkipped()` | — | ✔ |
| `danglingAtSignAsksForNothing()` | — | ✔ |
| `roguelikeKeysetKeepsTheThrowKey()` | — | ✔ |

**class `PlayerQuiverCapacityTest.PartialStackLimits`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fullPackStackTakesNothing()` | — | ✔ |
| `shortPackStackCanTakeMore()` | — | ✘ |
| `storeWaivesTheLimit()` | — | ✔ |
| `fullQuiverStackTakesNothing()` | — | ✔ |
| `shortQuiverStackCanTakeMore()` | — | ✘ |

**class `PlayerQuiverCapacityTest.PartialQuiverRoomCheck`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ask(ObjectStackEnum)` | — | ✘ |
| `quiverAloneIsNotChecked()` | — | ✔ |
| `packAloneIsChecked()` | — | ✘ |
| `packWithQuiverIsChecked()` | — | ✔ |
| `emptyModeIsNotChecked()` | — | ✔ |


##### `PlayerRaceBodyAccessorTest.java`

**Responsible for:** Tests Player#getRace and Player#setBody — the two ends of the ownership rule that PlayerBirth.embody sits between.

**class `PlayerRaceBodyAccessorTest`** — class Javadoc: ✔ · type stamp: on 260901

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |

**class `PlayerRaceBodyAccessorTest.Race`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshPlayerHasNoRace()` | — | ✔ |
| `raceReadBackIsTheObjectSet()` | — | ✔ |
| `racelessPlayerAnswersNull()` | — | ✔ |

**class `PlayerRaceBodyAccessorTest.Body`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bodySetIsBodyHeld()` | — | ✔ |
| `bodyReplacesTheConstructorBody()` | — | ✔ |
| `constructorBodyIsNotTheRegistrys()` | — | ✔ |


##### `PlayerRaceCopyTest.java`

**Responsible for:** Tests PlayerRace#copy(), the method PlayerBirth.playerGenerate hands a player its own race definition through (PlayerBirth.java:428).

**class `PlayerRaceCopyTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (9/9 documented): ✔ `statsAdj`, `skillsAdj`, `oFlags`, `pFlags`, `fireInfo`, `resists`, `body`, `history`, `source`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `field(PlayerRace, String)` | — | ✔ |
| `setUp()` | — | ✔ |
| `aCopyOfACopyIsAlsoIndependent()` | — | ✔ |

**class `PlayerRaceCopyTest.TheValuesAreCarriedAcross`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theScalarsAreCopied()` | — | ✘ |
| `theAdjustmentsAreCopied()` | — | ✘ |
| `theFlagsAreCopied()` | — | ✘ |
| `theResistancesAreCopied()` | — | ✘ |
| `theBodyPlanIsCopied()` | — | ✘ |

**class `PlayerRaceCopyTest.NothingMutableIsShared`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theCopyIsADifferentObject()` | — | ✘ |
| `theStatMapIsNotShared()` | — | ✘ |
| `theSkillMapIsNotShared()` | — | ✘ |
| `theObjectFlagsAreNotShared()` | — | ✘ |
| `thePlayerFlagsAreNotShared()` | — | ✘ |
| `theResistMapIsNotShared()` | — | ✘ |
| `theElementInfosAreNotShared()` | — | ✔ |
| `theBodyIsNotShared()` | — | ✘ |

**class `PlayerRaceCopyTest.WhatIsSharedIsSharedOnPurpose`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theHistoryChartIsShared()` | — | ✘ |


##### `PlayerRaceGetRaceFromIndexTest.java`

**Responsible for:** Tests PlayerRace#getRaceFromIndex(int), the port of C's player_id2race (player-race.c:21-28).

**class `PlayerRaceGetRaceFromIndexTest`** — class Javadoc: ✔ · type stamp: on 260907

- Fields (1/1 documented): ✔ `previousRaces`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `playerRace(String)` | — | ✔ |
| `racesField()` | — | ✔ |
| `seedRegistry()` | — | ✘ |
| `restoreRegistry()` | — | ✘ |

**class `PlayerRaceGetRaceFromIndexTest.InRangeIndex`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstIndexAnswersFirstRace()` | — | ✘ |
| `lastIndexAnswersLastRace()` | — | ✘ |
| `middleIndexAnswersMiddleRace()` | — | ✘ |

**class `PlayerRaceGetRaceFromIndexTest.OutOfRangeHighIndex`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `indexEqualToSizeAnswersNull()` | — | ✘ |
| `indexWellPastSizeAnswersNull()` | — | ✘ |
| `indexZeroAgainstEmptyListAnswersNull()` | — | ✘ |

**class `PlayerRaceGetRaceFromIndexTest.NegativeIndex`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `minusOneAnswersNull()` | — | ✘ |
| `minValueAnswersNull()` | — | ✘ |


##### `PlayerRaceKnowledgeTest.java`

**Responsible for:** Tests the three PlayerRace accessors that Player#learnInnate() drives — the two knowledge questions and the display name.

**class `PlayerRaceKnowledgeTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(Map<ElementEnum,ElementInfo>, Flag<ObjectFlag>)` | — | ✔ |
| `resistMap(ElementEnum, int)` | — | ✔ |
| `flags(ObjectFlag)` | — | ✘ |
| `name()` | — | ✘ |

**class `PlayerRaceKnowledgeTest.Resists`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `resisted()` | — | ✘ |
| `vulnerability()` | — | ✔ |
| `explicitZero()` | — | ✔ |
| `absentElement()` | — | ✔ |
| `emptyMap()` | — | ✔ |
| `everyElement()` | — | ✔ |

**class `PlayerRaceKnowledgeTest.Flags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `conferred()` | — | ✘ |
| `notConferred()` | — | ✘ |
| `none()` | — | ✘ |
| `sentinels()` | — | ✔ |
| `several()` | — | ✘ |


##### `PlayerRedrawStuffTest.java`

**Responsible for:** Tests PlayerCalcs#redrawStuff(), the port of C's redraw_stuff (player-calcs.c).

**class `PlayerRedrawStuffTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (6/6 documented): ✔ `C_TABLE`, `C_SUBWINDOW`, `player`, `bus`, `realBus`, `realCharacterGenerated`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `raise(PlayerRedraw)` | — | ✔ |
| `pending()` | — | ✔ |
| `setCounter(String, int)` | — | ✔ |

**class `PlayerRedrawStuffTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `events`, `data`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerRedrawStuffTest.HiddenMapInput`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `mapIsVisible()` | — | ✘ (override) |

**class `PlayerRedrawStuffTest.Signalling`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noFlagsMeansNoEvents()` | — | ✔ |
| `raisedFlagsSignalTheirEvents()` | — | ✔ |
| `everyFlagMapsToItsCEvent()` | — | ✔ |
| `mapThenEnd()` | — | ✔ |
| `mapCarriesWholeMapSentinel()` | — | ✔ |
| `hpCarriesCurrentAndMaxHitPoints()` | — | ✔ |
| `levelCarriesCurrentAndMaxLevel()` | — | ✔ |
| `equipCarriesOneGlyphPerSlotBlankForEmpty()` | — | ✔ |
| `titleArmDoesNotNpeWithNoShape()` | — | ✔ |
| `titleArmCarriesTheRealShapeName()` | — | ✔ |
| `actedFlagsAreCleared()` | — | ✔ |
| `flagRaisedDuringPassSurvives()` | — | ✔ |
| `snapshotIsACopy()` | — | ✔ |

**class `PlayerRedrawStuffTest.Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ungeneratedCharacterDoesNothing()` | — | ✔ |
| `hiddenMapDoesSubwindowsOnly()` | — | ✔ |
| `hiddenMapWithNoSubwindowFlags()` | — | ✔ |

**class `PlayerRedrawStuffTest.RestingHack`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `midRestSkipsTheRedraw()` | — | ✔ |
| `midRunSkipsTheRedraw()` | — | ✔ |
| `hundredthTurnRedraws()` | — | ✔ |
| `negativeRestSentinelSkipsTheRedraw()` | — | ✔ |
| `pendingMessageOverridesTheHack()` | — | ✔ |
| `pendingMapOverridesTheHack()` | — | ✔ |
| `hiddenMapMasksTheOverridesFirst()` | — | ✔ |
| `standingStillAlwaysRedraws()` | — | ✔ |


##### `PlayerRestingCountTest.java`

**Responsible for:** Tests PlayerUtils#playerRestingCount(Player), the port of C's player_resting_count in player-util.c.

**class `PlayerRestingCountTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `setResting(int)` | — | ✔ |
| `restingCount()` | — | ✔ |
| `newPlayerIsNotResting()` | — | ✔ |
| `positiveCountsAreReturnedUnchanged()` | — | ✔ |
| `sentinelsAreReturnedUnchanged()` | — | ✔ |
| `sixteenBitExtremesSurvive()` | — | ✔ |
| `readingDoesNotConsume()` | — | ✔ |
| `countIsPerPlayer()` | — | ✔ |


##### `PlayerRuneLearningTest.java`

**Responsible for:** Tests Player's object-knowledge path — PlayerKnowledge#learnRune, the wrappers over it, and the two small high-water-mark updaters that sit beside them.

**class `PlayerRuneLearningTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/18 documented): ✔ `SAVED_FIELDS` — ✘ `SAVED`, `weakAcid`, `strongAcid`, `evil3`, `evil5`, `siren`, `vulnerability`, `enveloping`, `cowardice`, `strengthProperty`, `sustainProperty`, `fearProperty`, `impairProperty`, `player`, `knowledge`, `bus`, `realBus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seed()` | — | ✔ |
| `restore()` | — | ✘ |
| `field(String)` | — | ✘ |
| `set(Player, String, Object)` | — | ✘ |
| `get(Player, String)` | — | ✘ |
| `poke(Object, String, Object)` | — | ✔ |
| `cursed(Curse, int)` | — | ✔ |
| `itemWith(Map.Entry<Curse,CurseData>)` | — | ✔ |
| `itemFlagged(ObjectFlag)` | — | ✔ |
| `itemCursed(Map.Entry<Curse,CurseData>)` | — | ✔ |
| `giveKnownCounterpart(ItemObject)` | — | ✔ |
| `itemWithCombat(int, int, int)` | — | ✔ |
| `weaponWithToHit(int)` | — | ✔ |
| `bodyWearing(ItemObject)` | — | ✔ |
| `emptyBody()` | — | ✔ |
| `shapeWithToAc(int)` | — | ✔ |
| `shapeWith(int, int, int)` | — | ✔ |
| `bodyOf(List<EquipmentSlotsEnum>, ItemObject)` | — | ✔ |
| `withToDRune()` | — | ✔ |
| `announced()` | — | ✘ |
| `startPlaying()` | — | ✔ |
| `clearMessageLog()` | — | ✔ |
| `tearDown()` | — | ✘ |
| `setUp()` | — | ✔ |

**class `PlayerRuneLearningTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerRuneLearningTest.Dispatch`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `combat()` | — | ✘ |
| `combatVarietiesAreDistinct()` | — | ✘ |
| `combatSentinel()` | — | ✔ |
| `modifier()` | — | ✘ |
| `resist()` | — | ✘ |
| `flag()` | — | ✘ |
| `brand()` | — | ✔ |
| `slay()` | — | ✘ |
| `curse()` | — | ✘ |
| `nullRune()` | — | ✔ |

**class `PlayerRuneLearningTest.Announcement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `namesTheRune()` | — | ✘ |
| `usesTheVarietyName()` | — | ✘ |
| `notRepeated()` | — | ✔ |
| `notRepeatedForTheGroup()` | — | ✔ |
| `suppressed()` | — | ✔ |
| `objectFlags(ObjectFlag)` | — | ✔ |

**class `PlayerRuneLearningTest.KnowsRune`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `combat()` | — | ✘ |
| `combatRunesAreDistinct()` | — | ✔ |
| `combatSentinel()` | — | ✔ |
| `modifier()` | — | ✘ |
| `resist()` | — | ✘ |
| `flag()` | — | ✘ |
| `curse()` | — | ✘ |
| `brandGroup()` | — | ✔ |
| `slayGroup()` | — | ✘ |
| `varietiesDoNotCrossTalk()` | — | ✔ |

**class `PlayerRuneLearningTest.Innate`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(Map<ElementEnum,ElementInfo>, ObjectFlag)` | — | ✘ |
| `resistMap(ElementEnum, int)` | — | ✘ |
| `resistance()` | — | ✘ |
| `vulnerability()` | — | ✔ |
| `flag()` | — | ✘ |
| `both()` | — | ✘ |
| `silent()` | — | ✔ |
| `nothingInnate()` | — | ✔ |
| `zeroLevel()` | — | ✔ |
| `nothingElse()` | — | ✔ |
| `elementWithoutARune()` | — | ✔ |

**class `PlayerRuneLearningTest.CursesFindToA`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `teachesBothRunes()` | — | ✔ |
| `signDoesNotMatter()` | — | ✔ |
| `curseWithoutArmourChange()` | — | ✔ |
| `zeroPowerIsAbsent()` | — | ✔ |
| `curseNotOnTheItem()` | — | ✔ |
| `uncursedItem()` | — | ✘ |
| `twoQualifyingCurses()` | — | ✔ |

**class `PlayerRuneLearningTest.EquipLearnOnDefend`** — class Javadoc: ✔ · type stamp: before 260815

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptySlotsAreSkipped()` | — | ✔ |
| `cursedItemTeaches()` | — | ✘ |
| `uncursedItemWithNoBonusTeachesNothing()` | — | ✔ |
| `uncursedItemWithABonusTeaches()` | — | ✔ |
| `aPenaltyTeachesToo()` | — | ✔ |
| `alreadyKnownStopsAtTheDoor()` | — | ✔ |
| `stopsAtTheFirstSlotThatTeaches()` | — | ✔ |
| `shapeTeaches()` | — | ✔ |
| `shapeWithoutArmourClass()` | — | ✘ |
| `noShape()` | — | ✔ |

**class `PlayerRuneLearningTest.CursesFindToH`** — class Javadoc: ✔ · type stamp: on 260815

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `teachesBothRunes()` | — | ✘ |
| `aToAOnlyCurseIsSilent()` | — | ✔ |
| `zeroPowerIsNotACurse()` | — | ✔ |
| `uncursedItemIsSilent()` | — | ✘ |

**class `PlayerRuneLearningTest.CursesFindToD`** — class Javadoc: ✔ · type stamp: on 260815

- Fields (0/1 documented): ✘ `previousRunes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addRune()` | — | ✘ |
| `removeRune()` | — | ✘ |
| `teachesBothRunes()` | — | ✘ |
| `aToAOnlyCurseIsSilent()` | — | ✘ |
| `zeroPowerIsNotACurse()` | — | ✘ |

**class `PlayerRuneLearningTest.EquipLearnOnRangedAttack`** — class Javadoc: ✔ · type stamp: on 260815

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptySlotsAreSkipped()` | — | ✘ |
| `theWeaponSlotIsSkipped()` | — | ✔ |
| `theShootingSlotIsSkipped()` | — | ✔ |
| `aCursedRingTeaches()` | — | ✔ |
| `anItemAtItsKindsToHitTeachesNothing()` | — | ✔ |
| `anItemOffItsKindsToHitTeaches()` | — | ✔ |
| `alreadyKnownStopsAtTheDoor()` | — | ✔ |
| `stopsAtTheFirstSlotThatTeaches()` | — | ✔ |
| `shapeTeaches()` | — | ✔ |
| `shapeToDamageIsNotLearned()` | — | ✔ |
| `noShape()` | — | ✘ |

**class `PlayerRuneLearningTest.EquipLearnOnMeleeAttack`** — class Javadoc: ✔ · type stamp: on 260815

- Fields (0/1 documented): ✘ `previousRunes`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addRune()` | — | ✘ |
| `removeRune()` | — | ✘ |
| `emptySlotsAreSkipped()` | — | ✘ |
| `theShootingSlotIsSkipped()` | — | ✔ |
| `theWeaponSlotIsRead()` | — | ✔ |
| `toDamageAloneTeachesOnlyItsOwnRune()` | — | ✔ |
| `aToDamagePenaltyTeaches()` | — | ✔ |
| `anUnremarkableItemTeachesNothing()` | — | ✔ |
| `toHitAloneTeachesOnlyItsOwnRune()` | — | ✔ |
| `anItemAtItsKindsToHitTeachesNothing()` | — | ✔ |
| `oneRuneKnownIsNotEnoughToStop()` | — | ✔ |
| `theOtherRuneKnownIsNotEnoughEither()` | — | ✔ |
| `bothKnownStopsAtTheDoor()` | — | ✔ |
| `stopsOnceBothAreKnown()` | — | ✔ |
| `aPartialSlotDoesNotEndTheWalk()` | — | ✔ |
| `shapeTeachesBoth()` | — | ✔ |
| `shapeWithOneFigureTeachesOnlyIt()` | — | ✔ |
| `noShape()` | — | ✘ |

**class `PlayerRuneLearningTest.Wrappers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `learnBrand()` | — | ✘ |
| `learnBrandResolvesTheGroup()` | — | ✔ |
| `learnBrandIsIdempotent()` | — | ✘ |
| `knowsBrand()` | — | ✘ |
| `learnCurse()` | — | ✘ |
| `learnCurseMatchesByName()` | — | ✔ |
| `learnCurseWithoutARune()` | — | ✔ |
| `learnSlay()` | — | ✘ |
| `learnSlayResolvesTheGroup()` | — | ✔ |
| `learnSlayIsIdempotent()` | — | ✘ |
| `knowsSlay()` | — | ✘ |
| `knowsCurse()` | — | ✘ |
| `knowsCurseIsNotShared()` | — | ✔ |
| `learnFlag()` | — | ✘ |
| `learnFlagIsIdempotent()` | — | ✔ |
| `learnFlagWithoutARune()` | — | ✔ |
| `learnFlagIsNarrow()` | — | ✘ |

**class `PlayerRuneLearningTest.EquipLearnFlag`** — class Javadoc: ✔ · type stamp: on 260815

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sentinelsAreRefused()` | — | ✔ |
| `emptySlotsAreSkipped()` | — | ✘ |
| `anItemWithTheFlagTeaches()` | — | ✔ |
| `theFlagIsAnnouncedOnce()` | — | ✔ |
| `anAlreadyKnownFlagIsSilent()` | — | ✔ |
| `anItemWithoutTheFlagRecordsTheAbsence()` | — | ✔ |
| `aCounterpartlessItemIsSurvivable()` | — | ✔ |
| `cursesAreConsultedToo()` | — | ✔ |
| `theCurseSetHoldsOnlyThisFlag()` | — | ✔ |
| `everySlotIsVisited()` | — | ✔ |

**class `PlayerRuneLearningTest.LearnAllRunes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `learnsEverything()` | — | ✔ |
| `onlyWhatTheRegistryHolds()` | — | ✔ |
| `saysNothing()` | — | ✔ |
| `isIdempotent()` | — | ✘ |

**class `PlayerRuneLearningTest.HighWaterMarks`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `maxLevelRises()` | — | ✘ |
| `maxLevelDoesNotFall()` | — | ✔ |
| `depthMovesRecall()` | — | ✘ |
| `climbingLeavesBoth()` | — | ✔ |
| `equalDepthIsNotARecord()` | — | ✔ |

**class `PlayerRuneLearningTest.CursesFindFlags`** — class Javadoc: ✔ · type stamp: on 260815

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `testing(ObjectFlag)` | — | ✔ |
| `teachesBothRunes()` | — | ✔ |
| `theIntersectionIsRespected()` | — | ✔ |
| `theCurseDefinitionIsNotMutated()` | — | ✔ |
| `theTestSetIsNotMutated()` | — | ✔ |
| `aMissTeachesNothingAtAll()` | — | ✔ |
| `aKnownFlagStillTeachesTheCurse()` | — | ✔ |
| `zeroPowerIsNotACurse()` | — | ✔ |
| `uncursedItemIsSilent()` | — | ✘ |
| `theMessageIsSuppressedBeforePlay()` | — | ✔ |
| `theMessageIsSentDuringPlay()` | — | ✔ |
| `aFlagWithNoMessageIsStillLearned()` | — | ✔ |


##### `PlayerScalarStateTest.java`

**Responsible for:** Tests Player's scalar state — the counters, flags and countdowns the turn loop reads and writes, and the accessors that delegate to the calculated PlayerState.

**class `PlayerScalarStateTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |

**class `PlayerScalarStateTest.Energy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `energyCountersAreSeparate()` | — | ✔ |

**class `PlayerScalarStateTest.Countdowns`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wordRecallCountsDown()` | — | ✔ |
| `deepDescentCountsDown()` | — | ✔ |
| `decrementDoesNotFloor()` | — | ✔ |

**class `PlayerScalarStateTest.StateOfPlay`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerStartsAtZero()` | — | ✔ |
| `hitPointsAreSeparate()` | — | ✔ |
| `newPlayerIsIdle()` | — | ✔ |
| `setIsDeadMarksAndRevives()` | — | ✔ |
| `normalShapeIsNotChanged()` | — | ✔ |
| `newPlayerHasNoShape()` | — | ✔ |
| `shapeIsHandedBackByIdentity()` | — | ✔ |
| `shapeNamed(String)` | — | ✔ |

**class `PlayerScalarStateTest.Cave`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerHasNoCave()` | — | ✔ |
| `caveRoundTrips()` | — | ✔ |

**class `PlayerScalarStateTest.Delegating`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerHasNoState()` | — | ✔ |
| `flagTestsReadTheState()` | — | ✔ |

**class `PlayerScalarStateTest.ExperienceAndFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `expAndMaxExpAreSeparate()` | — | ✔ |
| `newPlayerIsNeitherWizardNorWinner()` | — | ✔ |
| `wizardAndWinnerAreIndependent()` | — | ✔ |
| `getMaxLevelReadsTheField()` | — | ✔ |

**class `PlayerScalarStateTest.RunesKnown`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noKnowledgeIsNotKnown()` | — | ✔ |


##### `PlayerSetOptionsTest.java`

**Responsible for:** Tests Player#setOptions(PlayerOptions) - the port of the plain field write C makes to p->opts in player_generate (player-birth.c:445), which copies opts_save back onto the freshly wiped player so options persist across a birth/restart.

**class `PlayerSetOptionsTest`** — class Javadoc: ✔ · type stamp: on 260903

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build()` | — | ✔ |
| `storesByIdentity()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |


##### `PlayerSetRestingTurnTest.java`

**Responsible for:** Tests Player#setRestingTurn(int) - the port of the plain field write C makes to p->resting_turn (player.h:554, uint32_t) both at birth, player_generate's p->resting_turn = 0 (player-birth.c:450), and on load, rd_u32b(&player->resting_turn) (load.c:834).

**class `PlayerSetRestingTurnTest`** — class Javadoc: ✔ · type stamp: on 260903

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build()` | — | ✔ |
| `restingTurn()` | — | ✔ |
| `newPlayerIsZero()` | — | ✔ |
| `storesValueUnchanged()` | — | ✔ |
| `zeroIsAnOrdinaryWrite()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |
| `unsignedOverflowSurvivesBitForBit()` | — | ✔ |


##### `PlayerSetShapeTest.java`

**Responsible for:** Tests Player#setShape(PlayerShape) - the port of the plain field write C makes to p->shape at each of its assignment sites: birth's lookup_player_shape("normal") (player-birth.c:457), the shapechange effect (effect-handler-general.c:3453), returning to normal form (player-util.c:1053), and load r...

**class `PlayerSetShapeTest`** — class Javadoc: ✔ · type stamp: on 260903

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `shape(String)` | — | ✔ |
| `build()` | — | ✔ |
| `storesByIdentity()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |
| `nullWriteClears()` | — | ✔ |


##### `PlayerSetTimedTest.java`

**Responsible for:** Tests PlayerTimed.setTimed, the port of C's player_set_timed (src/player-timed.c:787).

**class `PlayerSetTimedTest`** — class Javadoc: ✔ · type stamp: on 260829

- Fields (5/5 documented): ✔ `EFFECT`, `player`, `bus`, `realBus`, `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField()` | — | ✔ |
| `head()` | — | ✔ |
| `grade(int, int, String, String)` | — | ✔ |
| `load(String, String, String, int, TimedGrade)` | — | ✔ |
| `load(String, String, String, int, ObjectFlag, boolean, ElementEnum, TimedGrade)` | — | ✔ |
| `loadLadder()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `startAt(int)` | — | ✔ |
| `set(int, boolean, boolean)` | — | ✔ |
| `stored()` | — | ✔ |
| `anUnknownEffectThrows()` | — | ✔ |

**class `PlayerSetTimedTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `messages`, `types`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerSetTimedTest.Bounds`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lowerBoundRaisesTheValue()` | — | ✘ |
| `aNegativeRequestBecomesZero()` | — | ✘ |
| `settingTheCurrentValueIsNoChange()` | — | ✘ |
| `theLowerBoundIsAppliedBeforeTheNoChangeTest()` | — | ✘ |
| `aRequestAboveTheTopIsCapped()` | — | ✘ |
| `alreadyAtTheTopIsNoChange()` | — | ✘ |
| `belowTheTopStillMovesToIt()` | — | ✘ |

**class `PlayerSetTimedTest.Grades`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `goingUpAlwaysSpeaks()` | — | ✘ |
| `theUpMessageIsTheDestinationBands()` | — | ✘ |
| `skippingBandsSpeaksOnce()` | — | ✘ |
| `goingDownSpeaksWhenTheBandHasAMessage()` | — | ✘ |
| `goingDownIsSilentWithoutAMessage()` | — | ✘ |
| `zeroIsTheHeadBand()` | — | ✘ |
| `aBoundaryValueStaysInTheLowerBand()` | — | ✘ |
| `onePastABoundaryEntersTheNextBand()` | — | ✘ |
| `aBandChangeWithNoUpMessageStillNotifies()` | — | ✘ |

**class `PlayerSetTimedTest.WithinABand`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `reachingZeroSpeaksTheEndMessage()` | — | ✘ |
| `fallingSpeaksTheDecreaseMessage()` | — | ✘ |
| `risingSpeaksTheIncreaseMessage()` | — | ✘ |
| `silentWhenNotAskedToNotify()` | — | ✘ |
| `aMissingDecreaseMessageSaysNothing()` | — | ✘ |
| `aMissingIncreaseMessageSaysNothing()` | — | ✘ |
| `zeroIsTestedBeforeTheDirection()` | — | ✘ |

**class `PlayerSetTimedTest.Weapon`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `wieldWeapon(ItemObject)` | — | ✔ |
| `bareHandsArePlural()` | — | ✘ |
| `aWieldedWeaponIsSingular()` | — | ✘ |

**class `PlayerSetTimedTest.AlreadyKnown`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `installKnowledge()` | — | ✔ |
| `installState()` | — | ✔ |
| `installEmptyClass()` | — | ✔ |
| `aKnownImmunitySilencesTheChange()` | — | ✘ |
| `knowledgeWithoutImmunityDoesNotSilence()` | — | ✘ |
| `immunityWithoutKnowledgeDoesNotSilence()` | — | ✘ |
| `aKnownDuplicatedFlagSilencesTheChange()` | — | ✘ |
| `anInexactSynonymDoesNotSilence()` | — | ✘ |
| `aFlagTheyDoNotActuallyHaveDoesNotSilence()` | — | ✘ |
| `aBandChangeOverridesSuppression()` | — | ✘ |
| `installRaceWith(ObjectFlag)` | — | ✔ |

**class `PlayerSetTimedTest.Returning`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSilentRealChangeReturnsFalse()` | — | ✘ |
| `aNotifyingChangeWithNoMessageReturnsTrue()` | — | ✘ |

**class `PlayerSetTimedTest.Tail`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `notifyingRaisesStatus()` | — | ✘ |
| `silentRaisesNothing()` | — | ✘ |
| `theValueIsStoredEitherWay()` | — | ✘ |


##### `PlayerShapeModifierTest.java`

**Responsible for:** Tests PlayerShape#getModifier(Stats), the port of reading C's shape->modifiers[i] for a stat index (the stats loop in calc_shapechange, player-calcs.c).

**class `PlayerShapeModifierTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `shape(Map<ObjectModifier,Integer>)` | — | ✔ |
| `eachStatResolvesToItsOwnModifier()` | — | ✔ |
| `unmodifiedStatIsZero()` | — | ✔ |
| `statLookupsDoNotReachOtherModifiers()` | — | ✔ |
| `negativeModifiers()` | — | ✔ |


##### `PlayerShapeTest.java`

**Responsible for:** Tests the data holder PlayerShape, the port of C's struct player_shape (player.h).

**class `PlayerShapeTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fullShape()` | — | ✔ |
| `constructorRoundTrip()` | — | ✔ |
| `blows()` | — | ✔ |
| `noBlows()` | — | ✔ |
| `gettersAreNotCopies()` | — | ✔ |
| `emptyModifiersReadZero()` | — | ✔ |
| `sentinelsReadZero()` | — | ✔ |


##### `PlayerSlotLookupTest.java`

**Responsible for:** Tests Player's equipment slot lookups — the port of C's slot_by_type, slot_by_name and the index helpers around them (obj-gear.c:71).

**class `PlayerSlotLookupTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `humanoidBody()` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `setBody(PlayerBody)` | — | ✔ |
| `wear(int, ItemObject)` | — | ✔ |

**class `PlayerSlotLookupTest.ByType`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `emptySlotFound()` | — | ✔ |
| `fullSlotFound()` | — | ✔ |
| `firstMatchingSlotWins()` | — | ✔ |
| `fallbackIsTheFirstOfTheType()` | — | ✔ |
| `missingTypeAnswersPastTheEnd()` | — | ✔ |
| `emptyBodyIsRefused()` | — | ✔ |

**class `PlayerSlotLookupTest.ByName`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `knownNameAnswersIndex()` | — | ✔ |
| `ringSlotsAreDistinct()` | — | ✔ |
| `unknownNameThrows()` | — | ✔ |

**class `PlayerSlotLookupTest.Indexes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `numberAnswersSlot()` | — | ✔ |
| `slotAnswersNumber()` | — | ✔ |
| `foreignSlotAnswersPastTheEnd()` | — | ✔ |
| `helpersAreInverses()` | — | ✔ |


##### `PlayerSpellsInitTest.java`

**Responsible for:** Tests Player#initSpellOrder(int, int) and PlayerMagic#playerSpellsInit(Player), the port of C's player_spells_init in player-spell.c.

**class `PlayerSpellsInitTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `spell(String)` | — | ✔ |
| `book(int)` | — | ✔ |
| `casterClass(int)` | — | ✔ |
| `multiBookClass(int)` | — | ✔ |
| `classWith(ClassMagic)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `listField(String)` | — | ✔ |

**class `PlayerSpellsInitTest.CasterPath`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fillsBothListsToTotalSpells()` | — | ✔ |
| `oneSpellClassAllocatesOneSlot()` | — | ✔ |
| `multiBookClassIsSizedToTheSum()` | — | ✔ |
| `repeatCallResizesBothLists()` | — | ✔ |

**class `PlayerSpellsInitTest.NonCasterPath`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `leavesListsEmpty()` | — | ✔ |
| `earlierContentsSurvive()` | — | ✔ |

**class `PlayerSpellsInitTest.RepeatedAllocation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `secondSmallerCallReplacesTheFirst()` | — | ✔ |
| `staleFlagIsResetByNextAllocation()` | — | ✔ |


##### `PlayerStatBirthTest.java`

**Responsible for:** Tests Player#setStatBirth and Player#getStatBirth — the quickstart copy of C's p->stat_birth[stat].

**class `PlayerStatBirthTest`** — class Javadoc: ✔ · type stamp: on 260906

- Fields (2/2 documented): ✔ `REAL_STATS`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |

**class `PlayerStatBirthTest.SetAndGetStatBirth`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesTheValue()` | — | ✔ |
| `statsAreIndependent()` | — | ✔ |
| `lastWriteWins()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |
| `survivesMaxStatDrain()` | — | ✔ |
| `unsetStatReadsZero()` | — | ✔ |
| `sentinelsThrow()` | — | ✔ |


##### `PlayerStatDecTest.java`

**Responsible for:** Tests Player.statDec, the port of C's player_stat_dec (src/player.c:171).

**class `PlayerStatDecTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `read(String)` | — | ✔ |
| `stat(Stats, int, int)` | — | ✔ |
| `cur(Stats)` | — | ✔ |
| `max(Stats)` | — | ✔ |
| `flagsRaised()` | — | ✔ |
| `noFlagsRaised()` | — | ✔ |

**class `PlayerStatDecTest.TheCsOwnWalk`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theFloorReportsNoChange()` | — | ✔ |
| `temporaryThenPermanent()` | — | ✔ |
| `downThroughTheTail()` | — | ✔ |

**class `PlayerStatDecTest.TheBands`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theBoundaryAtTwentyEight()` | — | ✔ |
| `theBoundaryAtEighteen()` | — | ✔ |
| `theCeilingIsNotSpecial()` | — | ✔ |
| `theFloorHolds()` | — | ✔ |

**class `PlayerStatDecTest.ThePermanentFlagReplacesTheAnswer`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anImmovableMaximumDiscardsTheFall()` | — | ✔ |
| `temporarilyTheSameCharacterFalls()` | — | ✔ |
| `aMovingMaximumReportsTrue()` | — | ✔ |
| `eachFallsByItsOwnBand()` | — | ✔ |

**class `PlayerStatDecTest.SideEffects`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aDrainSchedulesBoth()` | — | ✔ |
| `temporaryLeavesTheMaximum()` | — | ✔ |
| `onlyTheNamedStatMoves()` | — | ✔ |


##### `PlayerStatIncTest.java`

**Responsible for:** Tests Player.playerStatInc, the port of C's player_stat_inc (src/player.c:145).

**class `PlayerStatIncTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cGain(int)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `read(String)` | — | ✔ |
| `stat(Stats, int)` | — | ✔ |
| `cur(Stats)` | — | ✔ |
| `max(Stats)` | — | ✔ |

**class `PlayerStatIncTest.AtTheCeiling`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theCeilingItselfRefuses()` | — | ✔ |
| `aboveTheCeilingRefuses()` | — | ✔ |
| `oneBelowTheCeilingStillGains()` | — | ✔ |

**class `PlayerStatIncTest.BelowEighteen`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oneAtATime()` | — | ✔ |
| `theBottomOfTheScale()` | — | ✔ |
| `theBoundaryAtEighteen()` | — | ✔ |

**class `PlayerStatIncTest.RolledBand`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withinTheCsBounds()` | — | ✔ |
| `aRollAlwaysMoves()` | — | ✔ |
| `theGainShrinks()` | — | ✔ |
| `theBandNeverReachesTheClampLetAloneTheCeiling()` | — | ✔ |

**class `PlayerStatIncTest.TopBand`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theBoundaryAtNinety()` | — | ✔ |
| `oneStepToTheCeiling()` | — | ✔ |

**class `PlayerStatIncTest.SideEffects`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theMaximumFollows()` | — | ✔ |
| `aDrainedMaximumIsLeftAlone()` | — | ✔ |
| `aGainSchedulesTheRecalculation()` | — | ✔ |
| `onlyTheNamedStatMoves()` | — | ✔ |


##### `PlayerStatValueAccessorTest.java`

**Responsible for:** Tests Player#getCurStatValue(Stats) and Player#getMaxStatValue(Stats), the two reads that stand in for C's p->stat_cur[stat] and p->stat_max[stat].

**class `PlayerStatValueAccessorTest`** — class Javadoc: ✔ · type stamp: on 260901

- Fields (2/2 documented): ✔ `REAL_STATS`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `stats(Map<Stats,Integer>, Map<Stats,Integer>)` | — | ✔ |
| `allStats(int, int)` | — | ✔ |
| `set(String, Object)` | — | ✔ |

**class `PlayerStatValueAccessorTest.Reading`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `undrainedStatReadsAlike()` | — | ✔ |
| `drainedStatReadsBelowItsMaximum()` | — | ✔ |
| `eachStatIsIndependent()` | — | ✔ |
| `changingOneStatLeavesTheOthers()` | — | ✔ |

**class `PlayerStatValueAccessorTest.Encoding`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `floorReadsBack()` | — | ✔ |
| `eighteenReadsBack()` | — | ✔ |
| `firstPercentileReadsBack()` | — | ✔ |
| `ceilingReadsBack()` | — | ✔ |

**class `PlayerStatValueAccessorTest.AfterDrain`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `temporaryDrainMovesOnlyCurrent()` | — | ✔ |
| `permanentDrainMovesBoth()` | — | ✔ |

**class `PlayerStatValueAccessorTest.NoSlot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `sentinelsThrow()` | — | ✔ |


##### `PlayerStatePFlagOnOffTest.java`

**Responsible for:** Tests PlayerState#playerFlagOn(PlayerFlag) and PlayerState#playerFlagOff(PlayerFlag), the ports of C's pf_on and pf_off (player.h:60-61) over state.pflags.

**class `PlayerStatePFlagOnOffTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `assertEqualsCount(int, PlayerState)` | — | ✘ |
| `playerFlagOnReportsChange()` | — | ✔ |
| `playerFlagOffReportsChange()` | — | ✔ |
| `playerFlagOffOnUnsetFlag()` | — | ✔ |
| `neighbouringFlagsAreUndisturbed()` | — | ✔ |
| `writesThroughToTheLiveSet()` | — | ✔ |
| `wipeResetsTheChangeAnswer()` | — | ✔ |


##### `PlayerStateTest.java`

**Responsible for:** Tests PlayerState, the port of C's struct player_state — everything about a character that is derived from race, class, gear and statuses rather than stored.

**class `PlayerStateTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `filled()` | — | ✔ |

**class `PlayerStateTest.Wipe`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyFieldZeroed()` | — | ✔ |
| `elementsRepopulated()` | — | ✔ |
| `statsRepopulated()` | — | ✔ |
| `skillsReadZeroWhileEmpty()` | — | ✔ |
| `constructorMatchesWipe()` | — | ✔ |

**class `PlayerStateTest.Copy`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `read(PlayerState, String)` | — | ✔ |
| `carriesEveryValue()` | — | ✔ |
| `movesAreNotBlows()` | — | ✔ |
| `duplicateIsIndependent()` | — | ✔ |
| `originalIsIndependent()` | — | ✔ |
| `elementEntriesAreNotShared()` | — | ✔ |
| `copiesAFreshState()` | — | ✔ |

**class `PlayerStateTest.Accumulators`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `repeatedCallsAccumulate()` | — | ✔ |
| `negativeAmountsSubtract()` | — | ✔ |
| `untouchedReadAsZero()` | — | ✔ |
| `setterReplaces()` | — | ✔ |

**class `PlayerStateTest.Resistances`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bothSettersWrite()` | — | ✔ |
| `settersDifferOnMissingEntries()` | — | ✔ |
| `mapViewIsUnmodifiable()` | — | ✔ |

**class `PlayerStateTest.Flags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `copyReplacesUnionAdds()` | — | ✔ |
| `objectFlagsAccumulate()` | — | ✔ |
| `setOFlagSets()` | — | ✔ |
| `setOFlagReportsChange()` | — | ✔ |
| `setOFlagComposesWithUnion()` | — | ✔ |
| `setOFlagIsWiped()` | — | ✔ |
| `flagGettersAreLive()` | — | ✔ |
| `oFlagOffReportsChange()` | — | ✔ |
| `oFlagOffOnAbsentFlag()` | — | ✔ |
| `oFlagOffLeavesOthersAlone()` | — | ✔ |
| `oFlagOnOffRoundTrip()` | — | ✔ |
| `oFlagOffClearsAUnionedFlag()` | — | ✔ |

**class `PlayerStateTest.WeightLimit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `limitIsTableValueNotIndex()` | — | ✔ |
| `onlyStrengthMatters()` | — | ✔ |

**class `PlayerStateTest.PlainAccessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `settersRoundTrip()` | — | ✔ |
| `startsAtZeroBlows()` | — | ✔ |
| `booleansAreIndependent()` | — | ✔ |

**class `PlayerStateTest.AmmoType`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshStateNamesNoAmmo()` | — | ✔ |
| `ammoTypeRoundTrips()` | — | ✔ |
| `wipeClearsAmmoType()` | — | ✔ |
| `copyOfFreshStateHasTvNone()` | — | ✔ |
| `explicitTvNoneEqualsDefault()` | — | ✔ |
| `copyCarriesAmmoType()` | — | ✔ |


##### `PlayerTimedAccessorTest.java`

**Responsible for:** Tests Player#getTimedEffectOrDefault(TimedEffect, int) and Player#putTimed(TimedEffect, int), the two raw reaches into the timed-effect table that stand in for C's p->timed[idx] and p->timed[idx] = value.

**class `PlayerTimedAccessorTest`** — class Javadoc: ✔ · type stamp: on 260901

- Fields (2/2 documented): ✔ `MISSING`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `emptyTable()` | — | ✔ |
| `table()` | — | ✔ |

**class `PlayerTimedAccessorTest.Reading`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `notRunningReadsZero()` | — | ✔ |
| `runningReadsItsCount()` | — | ✔ |
| `effectsAreIndependent()` | — | ✔ |
| `absentEntryReadsTheDefault()` | — | ✔ |
| `presentEntryBeatsTheDefault()` | — | ✔ |
| `zeroDefaultMatchesGetTimedEffect()` | — | ✔ |
| `wrapperAgreesWithPlayerAccessor()` | — | ✔ |
| `absentTableThrows()` | — | ✔ |

**class `PlayerTimedAccessorTest.Writing`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesTheCount()` | — | ✔ |
| `zeroClearsAnEffect()` | — | ✔ |
| `writeReplaces()` | — | ✔ |
| `largeCountSurvives()` | — | ✔ |
| `writeCreatesAnAbsentEntry()` | — | ✔ |
| `writeRaisesNoFlags()` | — | ✔ |
| `writeTouchesOneSlot()` | — | ✔ |

**class `PlayerTimedAccessorTest.AsUsedByCalcBonuses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearingFastcastReadsAsNotRunning()` | — | ✔ |


##### `PlayerTimedAndItemKnowledgeAccessorTest.java`

**Responsible for:** Tests Player#setTimed(Map) and Player#setItemKnowledge(KnownObject) / Player#getItemKnowledge() — the ports of the plain field write and read C makes on p->timed (player.c:497, player-birth.c:437) and p->obj_k (player.c:498, player-birth.c:438).

**class `PlayerTimedAndItemKnowledgeAccessorTest`** — class Javadoc: ✔ · type stamp: on 260903

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `build()` | — | ✔ |

**class `PlayerTimedAndItemKnowledgeAccessorTest.SetTimed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesByIdentity()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |
| `absentEffectReadsAsZero()` | — | ✔ |

**class `PlayerTimedAndItemKnowledgeAccessorTest.SetItemKnowledge`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `storesByIdentity()` | — | ✔ |
| `secondWriteReplaces()` | — | ✔ |


##### `PlayerTimedEffectTest.java`

**Responsible for:** Tests PlayerTimedEffect, the port of C's struct timed_effect_data (player-timed.h).

**class `PlayerTimedEffectTest`** — class Javadoc: ✔ · type stamp: on 260818

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `definitionFor(TimedEffect, TimedGrade)` | — | ✔ |
| `contentsOf(Iterable<E>)` | — | ✔ |

**class `PlayerTimedEffectTest.DerivedFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theCommonCaseTakesTheStatusLineAndTheBonusRecalculation()` | — | ✘ |
| `blindnessArrivesCarryingTheMapRepaintAndViewRecomputation()` | — | ✔ |
| `hallucinationArrivesCarryingAllThreeOfItsRepaints()` | — | ✘ |
| `theSentinelArrivesCarryingNothing()` | — | ✘ |
| `twoDefinitionsOfTheSameEffectAgreeOnTheirFlags()` | — | ✘ |

**class `PlayerTimedEffectTest.CTable`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyEffectOutsideTheFourExceptionsRaisesStatusAndBonus()` | — | ✘ |
| `seeInvisibleAndInfravisionAlsoRefreshMonsterVisibility()` | — | ✘ |
| `hallucinationRaisesOnlyTheBonusRecalculation()` | — | ✘ |

**class `PlayerTimedEffectTest.StoredFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aFullySpecifiedDefinitionRoundTrips()` | — | ✘ |
| `theDefaultsMatchWhatCLeavesForAnOmittedLine()` | — | ✔ |
| `theLowerBoundAcceptsCsUpperLimit()` | — | ✘ |

**class `PlayerTimedEffectTest.Accessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theFlagsAreTheEnumConstantsOwnSetRatherThanACopy()` | — | ✔ |
| `theIdentityIsWhatTheDefinitionWasBuiltFor()` | — | ✘ |
| `theParsedFieldsRoundTrip()` | — | ✘ |
| `theGradesComeBackInTheOrderTheyWereGiven()` | — | ✘ |


##### `PlayerTimedGradeEqTest.java`

**Responsible for:** Tests PlayerTimed#timedGradeEq, the port of C's player_timed_grade_eq (player-timed.c:734).

**class `PlayerTimedGradeEqTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (2/2 documented): ✔ `player`, `savedEffects`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `registryField()` | — | ✔ |
| `grade(int, int, String)` | — | ✔ |
| `loadEffect(TimedEffect, TimedGrade)` | — | ✔ |
| `loadStunGrades()` | — | ✔ |
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `setTimedValue(TimedEffect, int)` | — | ✔ |

**class `PlayerTimedGradeEqTest.BandSelection`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aValueInsideTheFirstBandMatchesThatBandsName()` | — | ✘ |
| `aValueInsideTheSecondBandMatchesThatBandsName()` | — | ✘ |
| `aValueInsideTheThirdBandMatchesThatBandsName()` | — | ✘ |
| `aValueExactlyOnABoundaryStaysInTheLowerBand()` | — | ✘ |
| `oneAboveABoundaryMovesToTheNextBand()` | — | ✘ |

**class `PlayerTimedGradeEqTest.OnlyTheActiveBandIsCompared`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aLowValueDoesNotMatchAStrongerBandsName()` | — | ✘ |
| `aHighValueDoesNotMatchAWeakerBandsName()` | — | ✘ |
| `twoBandsSharingANameStillOnlyMatchTheActiveOne()` | — | ✘ |

**class `PlayerTimedGradeEqTest.Guards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anEffectAtZeroIsNeverAtAnyGrade()` | — | ✔ |
| `aFreshPlayerHasEveryEffectDormant()` | — | ✘ |
| `anEffectWithNoLoadedDefinitionAnswersFalse()` | — | ✘ |
| `aNameThatMatchesNoGradeAnswersFalse()` | — | ✘ |
| `anUnnamedBandAnswersFalseRatherThanThrowing()` | — | ✘ |
| `anEffectWithNoGradesAtAllAnswersFalse()` | — | ✘ |
| `aValueAboveEveryBandAnswersFalse()` | — | ✔ |
| `aNegativeValueLandsInTheFirstBand()` | — | ✔ |
| `theSentinelEffectIsNeverAtAGrade()` | — | ✘ |
| `anEmptyRegistryListAnswersFalseRatherThanThrowing()` | — | ✘ |
| `aMissingEffectFromTheMapAnswersFalse()` | — | ✘ |


##### `PlayerTimedTableAccessorTest.java`

**Responsible for:** Tests the three accessors Player exposes over its timed-effect table: Player#getTimedEffectOrDefault(TimedEffect, int), Player#playerTimedContains(TimedEffect) and Player#playerHasTimed().

**class `PlayerTimedTableAccessorTest`** — class Javadoc: ✔ · type stamp: on 260901

- Fields (2/2 documented): ✔ `MISSING`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `setTable(HashMap<TimedEffect,Integer>)` | — | ✔ |

**class `PlayerTimedTableAccessorTest.Reading`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `notRunningReadsZero()` | — | ✔ |
| `runningReadsItsCount()` | — | ✔ |
| `extremeCountsReadBackUnchanged()` | — | ✔ |
| `effectsAreIndependent()` | — | ✔ |
| `absentEntryReadsTheDefault()` | — | ✔ |
| `presentEntryBeatsTheDefault()` | — | ✔ |
| `storedZeroBeatsTheDefault()` | — | ✔ |
| `zeroDefaultMatchesGetTimedEffect()` | — | ✔ |
| `readingDoesNotCreateTheEntry()` | — | ✔ |

**class `PlayerTimedTableAccessorTest.Containment`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshCharacterHasEveryEffect()` | — | ✔ |
| `zeroCountIsStillPresent()` | — | ✔ |
| `emptiedTableContainsNothing()` | — | ✔ |
| `writeAddsOnlyItsOwnEntry()` | — | ✔ |

**class `PlayerTimedTableAccessorTest.TablePresence`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `freshCharacterHasATable()` | — | ✔ |
| `emptyTableIsStillATable()` | — | ✔ |
| `absentTableAnswersFalse()` | — | ✔ |


##### `PlayerUpdateBonusesTest.java`

**Responsible for:** Tests PlayerCalcs#updateBonuses(), the port of C's update_bonuses (player-calcs.c) — the method that recalculates the whole derived state and then reports what moved.

**class `PlayerUpdateBonusesTest`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (3/6 documented): ✔ `STRONG_CON`, `UNLIFTABLE`, `fixture` — ✘ `player`, `realBus`, `bus`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |
| `tearDown()` | — | ✘ |
| `updateAsked(PlayerUpdateEnum)` | — | ✔ |
| `redrawAsked(PlayerRedraw)` | — | ✔ |
| `clearFlags()` | — | ✔ |
| `partialMode()` | — | ✔ |
| `uncalculatedCharacter()` | — | ✔ |
| `state(String)` | — | ✔ |

**class `PlayerUpdateBonusesTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `messages`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerUpdateBonusesTest.FirstCalculation`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `survivesAnUncalculatedCharacter()` | — | ✔ |
| `reportsEveryStatAsChanged()` | — | ✔ |
| `installsTheDerivedStates()` | — | ✔ |

**class `PlayerUpdateBonusesTest.NoticingChanges`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constitutionChangeQueuesHitPoints()` | — | ✔ |
| `otherStatsLeaveHitPointsAlone()` | — | ✔ |
| `unchangedRecalculationAsksForNothing()` | — | ✔ |
| `speedChangeAsksForRedraw()` | — | ✔ |
| `telepathyAsksForMonsterUpdate()` | — | ✔ |
| `lightChangeAsksForViewUpdate()` | — | ✔ |
| `armourChangeAsksForRedraw()` | — | ✔ |
| `strengthChangeAsksForInventoryRedraw()` | — | ✔ |

**class `PlayerUpdateBonusesTest.Messages`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `heavyWeaponIsAnnounced()` | — | ✔ |
| `partialModeSuppressesMessagesOnly()` | — | ✔ |


##### `PlayerUpdateObjectKnowledgeTest.java`

**Responsible for:** Tests the three populations PlayerKnowledge#updateObjectKnowledge can currently reach — the objects lying on the level, the ones the player is carrying, and the curse definitions in the registry — and the events it always signals.

**class `PlayerUpdateObjectKnowledgeTest`** — class Javadoc: ✔ · type stamp: on 260815

- Fields (4/7 documented): ✔ `visited`, `visitOrder`, `realCurses`, `realCave` — ✘ `bus`, `realBus`, `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `poke(Object, String, Object)` | — | ✔ |
| `peekStatic(Class<?>, String)` | — | ✔ |
| `pokeStatic(Class<?>, String, Object)` | — | ✔ |
| `curseWithModifiers(String, Map<ObjectModifier,Integer>)` | — | ✔ |
| `itemInGear()` | — | ✔ |
| `setUp()` | — | ✘ |
| `registryHolding(Curse)` | — | ✔ |
| `tearDown()` | — | ✘ |
| `carrying(ItemObject)` | — | ✔ |
| `levelHolding(ItemObject)` | — | ✔ |

**class `PlayerUpdateObjectKnowledgeTest.RecordingList`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingList(ItemObject)` | — | ✘ |
| `iterator()` | — | ✘ (override) |

**class `PlayerUpdateObjectKnowledgeTest.RecordingPile`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingPile(ItemObject)` | — | ✘ |
| `getIterator()` | — | ✘ (override) |

**class `PlayerUpdateObjectKnowledgeTest.RecordingCurseList`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingCurseList(Curse)` | — | ✘ |
| `isCalledFromTheWalk()` | — | ✔ |
| `iterator()` | — | ✘ (override) |

**class `PlayerUpdateObjectKnowledgeTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `signalled`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerUpdateObjectKnowledgeTest.Populations`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `levelObjectsAreVisited()` | — | ✘ |
| `gearObjectsAreVisited()` | — | ✔ |
| `levelComesBeforeGear()` | — | ✔ |
| `anObjectInBothPopulationsIsVisitedFromEach()` | — | ✔ |
| `emptyPopulationsVisitNothing()` | — | ✘ |

**class `PlayerUpdateObjectKnowledgeTest.NullGuards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noCaveIsSurvivable()` | — | ✔ |
| `noGearIsSurvivable()` | — | ✔ |
| `neitherIsSurvivable()` | — | ✔ |
| `noItemKnowledgeIsSurvivable()` | — | ✔ |

**class `PlayerUpdateObjectKnowledgeTest.Signals`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bothEventsAreSignalled()` | — | ✘ |
| `eventsFireWithNothingToDo()` | — | ✔ |
| `eventsAreNotPerObject()` | — | ✔ |

**class `PlayerUpdateObjectKnowledgeTest.Curses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyCurseIsVisited()` | — | ✘ |
| `cursesComeLast()` | — | ✔ |
| `emptyRegistryVisitsNothing()` | — | ✔ |
| `aReadableCurseBecomesFullyKnown()` | — | ✔ |
| `anUnreadableCurseStaysUnknown()` | — | ✔ |
| `eventsAreUnaffectedByTheCurseWalk()` | — | ✔ |


##### `PlayerUpdateStuffTest.java`

**Responsible for:** Tests PlayerCalcs#updateStuff(Player), the port of C's update_stuff (player-calcs.c).

**class `PlayerUpdateStuffTest`** — class Javadoc: ✔ · type stamp: on 260828

- Fields (10/10 documented): ✔ `HP_PER_LEVEL`, `HP_AFTER_BONUSES`, `HP_ON_A_RAW_STATE`, `player`, `upkeep`, `level`, `bus`, `realBus`, `realCharacterGenerated`, `realCave`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setCharacterGenerated(boolean)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `restoreGlobals()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `giveClass(int)` | — | ✔ |
| `raise(PlayerUpdateEnum)` | — | ✔ |

**class `PlayerUpdateStuffTest.RecordingUpkeep`** — class Javadoc: ✔ · type stamp: —

- Fields (3/3 documented): ✔ `queries`, `cleared`, `pretendStale`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `getUpdate()` | — | ✘ (override) |
| `updateHas(PlayerUpdateEnum)` | — | ✘ (override) |
| `updateOff(PlayerUpdateEnum)` | — | ✘ (override) |

**class `PlayerUpdateStuffTest.RecordingChunk`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `viewUpdates`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `RecordingChunk(Player)` | — | ✔ |
| `updateView(Player)` | — | ✘ (override) |

**class `PlayerUpdateStuffTest.CapturingBus`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `events`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eventAddHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandler(GameEventType, EventHandlerInterface)` | — | ✘ (override) |
| `eventRemoveHandlerType(GameEventType)` | — | ✘ (override) |
| `gameEventDispatch(GameEventType, GameEventData)` | — | ✘ (override) |

**class `PlayerUpdateStuffTest.HiddenMapInput`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `mapIsVisible()` | — | ✘ (override) |

**class `PlayerUpdateStuffTest.Dispatch`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noFlagsMeansNoWork()` | — | ✔ |
| `allClausesRunInOrder()` | — | ✔ |
| `flagsAreCleared()` | — | ✔ |
| `eachClauseClearsItsOwn()` | — | ✔ |
| `oneFlagRunsOneCalculation()` | — | ✔ |
| `bonusClauseRunsAndFeedsTheRest()` | — | ✔ |

**class `PlayerUpdateStuffTest.Spells`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `casterRecalculatesSpells()` | — | ✔ |
| `nonCasterClearsTheFlagAnyway()` | — | ✔ |

**class `PlayerUpdateStuffTest.MapGuards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ungeneratedCharacterStopsAtTheMap()` | — | ✔ |
| `hiddenMapStopsAtTheMap()` | — | ✔ |
| `mapFlagsSurviveTheGuard()` | — | ✔ |

**class `PlayerUpdateStuffTest.Monsters`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `distanceSubsumesMonsters()` | — | ✔ |
| `distanceAloneIsFull()` | — | ✔ |
| `monstersAloneIsPartial()` | — | ✔ |

**class `PlayerUpdateStuffTest.MapClauses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `viewClauseRebuilds()` | — | ✔ |
| `panelClauseSignals()` | — | ✔ |


##### `PlayerUpkeepGearViewsTest.java`

**Responsible for:** Tests the PlayerUpkeep members around the pack and quiver: the notice-flag request queue (PlayerUpkeep#orNoticeFlag), the two live views (PlayerUpkeep#getQuiver, PlayerUpkeep#getInventory), and the two wholesale replacements (PlayerUpkeep#setInventory, PlayerUpkeep#setQuiverObjects).

**class `PlayerUpkeepGearViewsTest`** — class Javadoc: ✔ · type stamp: on 260816

- Fields (0/1 documented): ✘ `upkeep`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |

**class `PlayerUpkeepGearViewsTest.NoticeFlags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `firstSetIsNew()` | — | ✘ |
| `secondSetIsNotNew()` | — | ✔ |
| `flagsDoNotDisplaceEachOther()` | — | ✔ |
| `freshUpkeepHasNoNotices()` | — | ✔ |

**class `PlayerUpkeepGearViewsTest.GearViews`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `quiverStartsAsEmptySlots()` | — | ✔ |
| `inventoryStartsEmpty()` | — | ✔ |
| `quiverIsLive()` | — | ✔ |
| `inventoryIsLive()` | — | ✔ |
| `readsAreStable()` | — | ✔ |
| `orderIsPreserved()` | — | ✔ |
| `viewsAreDistinct()` | — | ✔ |

**class `PlayerUpkeepGearViewsTest.GearReplacement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setInventoryInstallsTheArray()` | — | ✔ |
| `setQuiverObjectsInstallsTheArray()` | — | ✔ |
| `oldReferenceGoesStale()` | — | ✔ |
| `setInventoryAcceptsNull()` | — | ✔ |
| `setQuiverObjectsAcceptsAnyLength()` | — | ✘ |


##### `PlayerUpkeepRedrawFlagsTest.java`

**Responsible for:** Tests the redraw-flag half of PlayerUpkeep — the port of C's p->upkeep->redraw and the take/clear pair redraw_stuff uses on it (player-calcs.c:2678-2711).

**class `PlayerUpkeepRedrawFlagsTest`** — class Javadoc: ✔ · type stamp: on 260818

- Fields (1/1 documented): ✔ `upkeep`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsOf(PlayerRedraw)` | — | ✔ |
| `setUp()` | — | ✘ |

**class `PlayerUpkeepRedrawFlagsTest.Snapshot`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aFreshUpkeepHasNothingPending()` | — | ✘ |
| `raisingAFlagShowsUpInTheSnapshot()` | — | ✘ |
| `raisingAFlagTwiceReportsNoChangeTheSecondTime()` | — | ✘ |
| `eachCallHandsBackADistinctSnapshot()` | — | ✘ |
| `narrowingTheSnapshotLeavesThePendingSetAlone()` | — | ✔ |
| `aSnapshotDoesNotTrackLaterChanges()` | — | ✘ |

**class `PlayerUpkeepRedrawFlagsTest.DifferencingClear`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clearingWhatWasHandledLeavesTheRestPending()` | — | ✘ |
| `aFlagRaisedDuringThePassSurvivesTheClear()` | — | ✔ |
| `aFlagNarrowedOutOfTheSnapshotStaysPending()` | — | ✔ |
| `clearingFlagsThatWereNotPendingReportsNoChange()` | — | ✘ |
| `clearingAnEmptySetIsANoOp()` | — | ✘ |
| `clearingLeavesTheArgumentAlone()` | — | ✘ |
| `theSingleFlagClearStillWorksAlongsideIt()` | — | ✘ |


##### `PlayerUpkeepStateTest.java`

**Responsible for:** Tests PlayerUpkeep's per-turn bookkeeping — the port of C's player_upkeep (player.h).

**class `PlayerUpkeepStateTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `PACK_SIZE`, `QUIVER_SIZE`, `savedConstants`, `upkeep`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `newUpkeep()` | — | ✔ |

**class `PlayerUpkeepStateTest.Fresh`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `startsEmpty()` | — | ✔ |
| `tracksNothing()` | — | ✔ |

**class `PlayerUpkeepStateTest.Paired`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `energyPair()` | — | ✔ |
| `healthPair()` | — | ✔ |

**class `PlayerUpkeepStateTest.TurnState`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `flagsAreIndependent()` | — | ✔ |
| `playingRoundTrips()` | — | ✔ |
| `countsRoundTrip()` | — | ✔ |
| `commandWorkRoundTrips()` | — | ✔ |
| `totalWeightRoundTrips()` | — | ✔ |
| `equipCountRoundTrips()` | — | ✔ |
| `autosaveIsIndependent()` | — | ✔ |

**class `PlayerUpkeepStateTest.ObjectTracking`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `trackedObjectRoundTrips()` | — | ✔ |
| `trackingCanBeCleared()` | — | ✔ |

**class `PlayerUpkeepStateTest.Flags`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `noticeRaised()` | — | ✔ |
| `bothSpellingsAgree()` | — | ✔ |
| `clearingEmptiesTheSet()` | — | ✔ |
| `clearingOneLeavesTheRest()` | — | ✔ |
| `updateFlagsSinglyOrTogether()` | — | ✔ |
| `updateFlags()` | — | ✔ |


##### `PlayerUpkeepStructTest.java`

**Responsible for:** Tests that PlayerUpkeep models C's struct player_upkeep (player.h) field for field, and the two writes the existing upkeep tests leave open: the bare resting-counter write and the bare health-bar write.

**class `PlayerUpkeepStructTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `PACK_SIZE`, `QUIVER_SIZE`, `savedConstants`, `upkeep`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `seedConstants()` | — | ✔ |
| `restoreConstants()` | — | ✔ |
| `newUpkeep()` | — | ✔ |
| `monster()` | — | ✔ |
| `read(String)` | — | ✔ |

**class `PlayerUpkeepStructTest.Struct`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `fieldSetMatchesC()` | — | ✔ |
| `scalarsStartZeroed()` | — | ✔ |
| `flagSetsStartEmpty()` | — | ✔ |
| `pathDestinationIsOrigin()` | — | ✔ |
| `stepsStartNull()` | — | ✔ |
| `trackeesStartNull()` | — | ✔ |
| `gearArraySizes()` | — | ✔ |
| `arraysAreNotShared()` | — | ✔ |

**class `PlayerUpkeepStructTest.RestingCounter`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `countRoundTrips()` | — | ✔ |
| `zeroIsWritten()` | — | ✔ |
| `specialCodesStoredVerbatim()` | — | ✔ |
| `noClamp()` | — | ✔ |
| `leavesOthersAlone()` | — | ✔ |

**class `PlayerUpkeepStructTest.HealthBar`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bareWriteRaisesNothing()` | — | ✔ |
| `healthTrackDoesBoth()` | — | ✔ |
| `healthTrackAdds()` | — | ✔ |
| `trackNullClears()` | — | ✔ |
| `secondReplacesFirst()` | — | ✔ |


##### `PlayerUpkeepUpdateOnTest.java`

**Responsible for:** Tests PlayerUpkeep.updateOn, the port of C's p->upkeep->update \|= (PU_X).

**class `PlayerUpkeepUpdateOnTest`** — class Javadoc: ✔ · type stamp: on 260831

- Fields (1/1 documented): ✔ `upkeep`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newUpkeep()` | — | ✔ |
| `raisingMakesItPending()` | — | ✔ |
| `raisingOneLeavesTheRest()` | — | ✔ |
| `raisingIsIdempotent()` | — | ✔ |
| `theAnswerMirrorsUpdateOff()` | — | ✔ |
| `theTwoRaisingFormsAgree()` | — | ✔ |
| `aRaiseMakesTheSetPending()` | — | ✔ |
| `everyFlagCanBeRaised()` | — | ✔ |


##### `PlayerUtilsModifyStatValueTest.java`

**Responsible for:** Tests PlayerUtils#modifyStatValue, the port of C's modify_stat_value (player-util.c:339).

**class `PlayerUtilsModifyStatValueTest`** — class Javadoc: ✔ · type stamp: on 260818

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `cReference(int, int)` | — | ✔ |

**class `PlayerUtilsModifyStatValueTest.GainsBelowEighteen`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aPointIsWorthAPoint(int, int, int)` | — | ✘ |
| `aBonusReachingEighteenStopsThereWithoutJumping()` | — | ✘ |

**class `PlayerUtilsModifyStatValueTest.GainsAtAndAboveEighteen`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `thePointAfterEighteenIsWorthTen()` | — | ✘ |
| `eachFurtherPointAddsAnotherTen(int, int, int)` | — | ✘ |
| `aBonusCrossingTheBoundaryChangesStepPartWayThrough()` | — | ✔ |
| `theResultIsNotAClosedFormOfValueAndAmount()` | — | ✔ |
| `gainsAreNotCappedByThisMethod()` | — | ✘ |

**class `PlayerUtilsModifyStatValueTest.Losses`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `belowTheBoundaryAPointCostsAPoint(int, int, int)` | — | ✘ |
| `atOrAboveTwentyEightAPointCostsTen(int, int, int)` | — | ✘ |
| `aLossCrossingTheBoundaryChangesStepPartWayThrough()` | — | ✔ |
| `lossesStopAtThree()` | — | ✘ |
| `aValueBelowTheFloorIsNotRaisedToIt()` | — | ✔ |
| `aLossToTheFloorIsNotReversedByTheSameGain()` | — | ✔ |

**class `PlayerUtilsModifyStatValueTest.ValuesBetweenTheSteps`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aSinglePointOfLossSnapsBackToEighteen(int)` | — | ✘ |
| `theRemainingPenaltyIsSpentFromEighteen()` | — | ✔ |
| `theSnapBoundarySitsBetweenTwentySevenAndTwentyEight()` | — | ✔ |
| `aGainFromBetweenTheStepsAddsTenWithoutSnapping()` | — | ✔ |

**class `PlayerUtilsModifyStatValueTest.ZeroAndTheSweep`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `anAmountOfZeroReturnsTheValueUnchanged(int)` | — | ✘ |
| `theSweepAgreesWithTheCReference()` | — | ✔ |


##### `PlayerVitalsAccessorTest.java`

**Responsible for:** Tests the accessors PlayerCalcs writes the recalculated character through: the hit-point and spell-point triples, the rolled hit-point table, and the two calculated states.

**class `PlayerVitalsAccessorTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `get(String)` | — | ✔ |

**class `PlayerVitalsAccessorTest.SpellPoints`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerHasNoMana()` | — | ✔ |
| `manaFieldsAreSeparate()` | — | ✔ |
| `currentManaIsNotClamped()` | — | ✔ |

**class `PlayerVitalsAccessorTest.HitPoints`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerHasAnEmptyRolledTable()` | — | ✔ |
| `rolledTableIsZeroBased()` | — | ✔ |
| `readingPastTheTableThrows()` | — | ✔ |
| `maximumDoesNotWriteTheRolledTable()` | — | ✔ |
| `hitPointFieldsAreSeparate()` | — | ✔ |
| `currentHitPointsAreNotClamped()` | — | ✔ |

**class `PlayerVitalsAccessorTest.CalculatedStates`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerHasNeitherState()` | — | ✔ |
| `statesAreStoredByIdentity()` | — | ✔ |
| `realAndKnownStatesAreIndependent()` | — | ✔ |
| `statesMayBeCleared()` | — | ✔ |


##### `PlayerWeightTest.java`

**Responsible for:** Tests Player#setWeight, Player#getWeight, Player#setWeightBirth, Player#getWeightBirth, PlayerRace#getBaseWeight and PlayerRace#getModWeight — the pieces C's get_ahw needs to roll a starting weight, and the getter play reads it back through.

**class `PlayerWeightTest`** — class Javadoc: ✔ · type stamp: on 260902

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `race(String, int, int)` | — | ✔ |
| `newPlayer()` | — | ✔ |
| `wtBirth()` | — | ✔ |

**class `PlayerWeightTest.SetWeight`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `storesTheValue()` | — | ✔ |
| `lastWriteWins()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |
| `holdsPounds()` | — | ✔ |
| `feedsBashQuality()` | — | ✔ |

**class `PlayerWeightTest.SetWeightBirth`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayerIsZero()` | — | ✔ |
| `doesNotTouchWorkingWeight()` | — | ✔ |
| `survivesLaterWorkingWrite()` | — | ✔ |
| `birthAssignmentSetsBoth()` | — | ✔ |
| `survivesQuickstartRoundTrip()` | — | ✔ |
| `doesNotClamp()` | — | ✔ |

**class `PlayerWeightTest.RaceWeightFields`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `baseAndModAreSeparate()` | — | ✔ |
| `weightIsNotHeightOrAge()` | — | ✔ |
| `baseIsReachable()` | — | ✔ |
| `spreadReachesFourDeviations()` | — | ✔ |
| `feedsOffsetArithmetic()` | — | ✔ |
| `reportsZeroSpread()` | — | ✔ |


##### `PlayerWipeTest.java`

**Responsible for:** Tests Player#wipe() and the constructor it repeats, against the C they stand in for: the memset(p, 0, sizeof(struct player)) at the head of player_init (player-birth.c) and the allocations of init_player (player.c).

**class `PlayerWipeTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `player`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `newPlayer()` | — | ✔ |
| `read(String)` | — | ✔ |
| `write(String, Object)` | — | ✔ |
| `dirty()` | — | ✔ |

**class `PlayerWipeTest.Zeroed`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `identityIsCleared()` | — | ✔ |
| `progressionIsCleared()` | — | ✔ |
| `vitalsAreCleared()` | — | ✔ |
| `positionAndPurseAreCleared()` | — | ✔ |
| `countersAreCleared()` | — | ✔ |
| `flagsAreCleared()` | — | ✔ |
| `hitPointTableIsCleared()` | — | ✔ |
| `statsAreCleared()` | — | ✔ |
| `spellListsAreCleared()` | — | ✔ |

**class `PlayerWipeTest.Rebuilt`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `timedTableIsFullAndZero()` | — | ✔ |
| `containersAreReplaced()` | — | ✔ |
| `bodyIsPrivateCopy()` | — | ✔ |
| `optionsGoBackToDefault()` | — | ✔ |

**class `PlayerWipeTest.LeftForPlayerInit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `unassignedMembersAreNull()` | — | ✔ |
| `raceIsCleared()` | — | ✔ |
| `wipedPlayerIsNotShapechanged()` | — | ✔ |

**class `PlayerWipeTest.NoRaces`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `withRaces(List<PlayerRace>, Runnable)` | — | ✔ |
| `constructorToleratesAnEmptyList()` | — | ✔ |
| `wipeToleratesAnEmptyList()` | — | ✔ |


##### `QuestTest.java`

**Responsible for:** Tests Quest on its own: the constructor and getters, which carry the six fields of C's struct quest (player.h), and Quest#copy(), the port of the four field assignments in player_quests_reset (player-quest.c).

**class `QuestTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constructorAndGettersRoundTrip()` | — | ✔ |
| `copyOfFreshTemplate()` | — | ✔ |
| `indexIsNotCopied()` | — | ✔ |
| `currentNumberIsNotCopied()` | — | ✔ |
| `maxNumberAboveOne()` | — | ✔ |
| `zeroLevelIsCopied()` | — | ✔ |
| `raceIsShared()` | — | ✔ |
| `copyIsAFreshObject()` | — | ✔ |
| `copyOfCopyIsStable()` | — | ✔ |


##### `StartItemTest.java`

**Responsible for:** Tests StartItem, the port of C's struct start_item (player.h).

**class `StartItemTest`** — class Javadoc: ✔ · type stamp: on 261009

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rationOfFoodFields()` | — | ✔ |
| `unconstrainedItemHasEmptyExclusions()` | — | ✔ |
| `wordOfRecallExclusion()` | — | ✔ |
| `mixedExclusionsKeepOrderAndSign()` | — | ✔ |
| `exclusionListIsNotCopied()` | — | ✔ |
| `boundaryQuantities()` | — | ✔ |
| `nullExclusionsStoredAsNull()` | — | ✔ |


##### `TimedFailureTest.java`

**Responsible for:** Checks TimedFailure against C's struct timed_failure (player-timed.h) and the fail: lines of player_timed.txt that parse_player_timed_fail reads.

**class `TimedFailureTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (0/2 documented): ✘ `ONE_OF_EACH`, `EVERY_CODE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theCodesAreCsFiveFlagValues()` | — | ✔ |
| `codesOutsideOneToFiveAreNotFailureCategories()` | — | ✔ |
| `anObjectFlagEntryHoldsTheFlagAndItsCode()` | — | ✘ |
| `aResistEntryHoldsTheElementAndItsCode()` | — | ✘ |
| `aVulnerabilityEntryHoldsTheElementAndItsCode()` | — | ✘ |
| `aPlayerFlagEntryHoldsTheFlagAndItsCode()` | — | ✘ |
| `aTimedEffectEntryHoldsTheEffectAndItsCode()` | — | ✘ |
| `theObjectFlagConstructorRejectsEveryCodeButItsOwn()` | — | ✘ |
| `theElementConstructorAcceptsResistAndVulnAndNothingElse()` | — | ✘ |
| `thePlayerFlagConstructorRejectsEveryCodeButItsOwn()` | — | ✘ |
| `theTimedEffectConstructorRejectsEveryCodeButItsOwn()` | — | ✘ |
| `aRejectedConstructionNamesTheCategoryItExpected()` | — | ✔ |
| `everyAccessorThrowsForEveryEntryOfAnotherCategory()` | — | ✔ |
| `bothElementCategoriesCanReadTheElement()` | — | ✔ |
| `anAccessorRefusalNamesTheCategoryItExpected()` | — | ✔ |
| `everyBuiltEntryHasARealCategory()` | — | ✔ |
| `everyFailLineInCsPlayerTimedDataBuildsAndReadsBack()` | — | ✔ |


## middle/player/enums

##### `PlayerCalcsFlagEnumsTest.java`

**Responsible for:** Tests PlayerNotice and PlayerUpdateEnum, the ports of C's PN_* and PU_* bitflag families (player-calcs.h:28-45).

**class `PlayerCalcsFlagEnumsTest`** — class Javadoc: ✔ · type stamp: on 260818

**class `PlayerCalcsFlagEnumsTest.Notices`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `holdsCsThreeNoticesInCsOrder()` | — | ✘ |
| `hasNoExtraConstants()` | — | ✘ |

**class `PlayerCalcsFlagEnumsTest.Updates`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `holdsCsTenUpdatesInCsOrder()` | — | ✘ |
| `hasNoExtraConstants()` | — | ✘ |

**class `PlayerCalcsFlagEnumsTest.AsFlagDomains`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aNoticeSetStartsEmptyAndTakesEveryConstant()` | — | ✘ |
| `anUpdateSetStartsEmptyAndTakesEveryConstant()` | — | ✘ |
| `raisingSeveralUpdatesLeavesEarlierOnesRaised()` | — | ✔ |


##### `PlayerHistoryTypeTest.java`

**Responsible for:** Tests PlayerHistoryType — the port of the history type enum in C's player-history.h, generated from list-history-types.h.

**class `PlayerHistoryTypeTest`** — class Javadoc: ✔ · type stamp: —

**class `PlayerHistoryTypeTest.Constants`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `orderMatchesC()` | — | ✔ |
| `numbering()` | — | ✔ |

**class `PlayerHistoryTypeTest.Descriptions`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchHeader()` | — | ✔ |
| `endMarkerHasNoText()` | — | ✔ |

**class `PlayerHistoryTypeTest.AsFlags`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `bitsAreIndependent()` | — | ✔ |


##### `PlayerOptionEnumTest.java`

**Responsible for:** Tests PlayerOptionEnum against the rows of C's list-options.h.

**class `PlayerOptionEnumTest`** — class Javadoc: ✔ · type stamp: —

**record `PlayerOptionEnumTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (0/4 documented): ✘ `name`, `description`, `type`, `normal`
- Fields (1/6 documented): ✔ `C_TABLE` — ✘ `SPECIAL`, `INTERFACE`, `CHEAT`, `SCORE`, `BIRTH`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `rowCount()` | — | ✘ |
| `orderAndNames()` | — | ✘ |
| `attributesMatchC()` | — | ✘ |
| `noneSentinel()` | — | ✘ |
| `defaultOnSet()` | — | ✘ |
| `isCheatMatchesOptionIsCheat()` | — | ✘ |
| `cheatFollowedByScore()` | — | ✘ |
| `typeCounts()` | — | ✘ |
| `typeNames()` | — | ✘ |


##### `PlayerOptionTypesTest.java`

**Responsible for:** Tests PlayerOptionTypes against option.h, option.c and the type column of list-options.h.

**class `PlayerOptionTypesTest`** — class Javadoc: ✔ · type stamp: —

- Fields (4/4 documented): ✔ `OPT_PAGE_MAX`, `OPT_PAGE_BIRTH`, `OP_MAX`, `C_OPTIONS_BY_TYPE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `optionPage(int)` | — | ✔ |
| `typeCount()` | — | ✘ |
| `ordinalsMatchC()` | — | ✘ |
| `names()` | — | ✘ |
| `namesDistinct()` | — | ✘ |
| `pageTypes()` | — | ✘ |
| `birthPage()` | — | ✘ |
| `interfacePage()` | — | ✘ |
| `birthPageContents()` | — | ✘ |
| `cheatPage()` | — | ✘ |
| `unpagedTypes()` | — | ✘ |
| `groupsCoverEveryOption()` | — | ✘ |


##### `PlayerOverExertionTest.java`

**Responsible for:** Tests PlayerOverExertion - the port of the PY_EXERT_* enum in player-util.h.

**class `PlayerOverExertionTest`** — class Javadoc: ✔ · type stamp: —

- Fields (0/1 documented): ✘ `C_ORDER`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `matchesCDeclarationOrder()` | — | ✘ |
| `ordinalTracksCBitPosition()` | — | ✘ |
| `emptyFlagIsNone()` | — | ✘ |
| `bloodlustCombination()` | — | ✘ |
| `singleConstant()` | — | ✘ |


##### `PlayerRedrawTest.java`

**Responsible for:** Tests PlayerRedraw's group membership — the port of C's PR_BASIC, PR_EXTRA and PR_SUBWINDOW masks (player-calcs.h:81-96).

**class `PlayerRedrawTest`** — class Javadoc: ✔ · type stamp: on 260820

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `group(java.util.function.Predicate<PlayerRedraw>)` | — | ✔ |
| `basicGroup()` | — | ✔ |
| `extraGroup()` | — | ✔ |
| `subwindowGroup()` | — | ✔ |
| `groupsAreDisjoint()` | — | ✔ |
| `someFlagsAreUngrouped()` | — | ✔ |


##### `RandnameTypeTest.java`

**Responsible for:** Tests RandnameType — the port of C's randname_type (src/randname.h:25).

**class `RandnameTypeTest`** — class Javadoc: ✔ · type stamp: —

**class `RandnameTypeTest.Constants`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `constantsMatchC()` | — | ✔ |
| `ordinalsAreCValuesLessOne()` | — | ✔ |
| `valuesLengthIsCsNumTypes()` | — | ✔ |

**class `RandnameTypeTest.FromIndex`** — class Javadoc: ✘ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `mapsTheRealSections()` | — | ✔ |
| `rejectsTheMarker()` | — | ✔ |
| `rejectsPastTheMarker()` | — | ✔ |
| `rejectsZero()` | — | ✔ |
| `rejectsNegative()` | — | ✔ |
| `roundTripsRealSections()` | — | ✔ |


##### `SustainStatTest.java`

**Responsible for:** Tests SustainStat - the port of C's sustain_flag() (obj-properties.c).

**class `SustainStatTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `realStatsMapToSustains()` | — | ✘ |
| `valueIsIndexPlusOne()` | — | ✘ |
| `stat_and_int_routes_agree()` | — | ✘ |
| `lowestRealIndex()` | — | ✘ |
| `highestRealIndex()` | — | ✘ |
| `belowRangeAnswersNone()` | — | ✘ |
| `atOrAboveMaxAnswersNone()` | — | ✘ |
| `neverAnswersMax()` | — | ✘ |
| `nullStatThrows()` | — | ✘ |
| `constantValues()` | — | ✘ |
| `valuesLineUpWithObjectFlags()` | — | ✘ |


##### `TimedEffectTest.java`

**Responsible for:** Tests TimedEffect, the port of C's TMD_* table (list-player-timed.h).

**class `TimedEffectTest`** — class Javadoc: ✔ · type stamp: on 260818

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `plain(TimedEffect)` | — | ✔ |
| `expected()` | — | ✔ |
| `contentsOf(Iterable<E>)` | — | ✔ |

**record `TimedEffectTest.Row`** — class Javadoc: ✔ · type stamp: —

- Record components (via `@param`) (3/3 documented): ✔ `effect`, `redraws`, `updates`

**class `TimedEffectTest.CTable`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `everyEffectDeclaresTheRedrawFlagsCListsForIt()` | — | ✘ |
| `everyEffectDeclaresTheUpdateFlagsCListsForIt()` | — | ✘ |
| `theEnumHoldsCsEffectsInCsOrderAfterThePortsOwnSentinel()` | — | ✘ |
| `theSentinelCarriesNoFlagsAtAll()` | — | ✘ |
| `onlyBlindAndHallucinationRepaintSomethingOtherThanTheStatusLine()` | — | ✘ |

**class `TimedEffectTest.Accessors`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `eachCallHandsBackTheConstantsOwnSetRatherThanACopy()` | — | ✔ |
| `differentConstantsDoNotShareASet()` | — | ✘ |
| `readingOneConstantsViewDoesNotDisturbAnother()` | — | ✘ |
| `theViewReportsTheSameContentsOnEveryCall()` | — | ✘ |


## middle/strings

##### `MessageTagTest.java`

**Responsible for:** Tests MessageTag#getTag against C's msg_tag_lookup, which compares with strncmp over the length of each name in the order name, kind, s, is.

**class `MessageTagTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `exactTags()` | — | ✘ |
| `prefixesMatch()` | — | ✘ |
| `shortTagsMatchNothing()` | — | ✘ |
| `unknownTag()` | — | ✘ |
| `caseSensitive()` | — | ✘ |
| `openingLettersDecide()` | — | ✔ |
| `sizes()` | — | ✔ |
| `constantOrder()` | — | ✔ |
| `orderDoesNotStealIs()` | — | ✔ |


##### `QuarkTest.java`

**Responsible for:** Unit tests for Quark, the port of the C source's string-interning table (src/z-quark.c).

**class `QuarkTest`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `quark`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `setUp()` | — | ✘ |

**class `QuarkTest.Interning`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aFreshTableIsEmpty()` | — | ✘ |
| `addingAStringReturnsAHandleThatFetchesItBack()` | — | ✘ |
| `handlesStartAtOneSoZeroStaysFreeForNoQuark()` | — | ✘ |
| `handlesAreIssuedInSequence()` | — | ✘ |
| `everyHandleKeepsPointingAtItsOwnString()` | — | ✘ |
| `addingTheSameTextTwiceReturnsTheSameHandle()` | — | ✘ |
| `aRepeatedAddDoesNotConsumeAHandle()` | — | ✘ |
| `theMatchIsCaseSensitiveAndWholeString()` | — | ✘ |
| `theEmptyStringDeduplicatesToo()` | — | ✘ |
| `aMatchIsFoundAmongManyEntries()` | — | ✘ |
| `aRepeatedAddAfterAMergeStillFindsTheMergedText()` | — | ✘ |
| `theEmptyStringIsStorableLikeAnyOther()` | — | ✘ |
| `anUnissuedHandleFetchesNull()` | — | ✘ |

**class `QuarkTest.Searching`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `containsTextFindsAStoredStringExactly()` | — | ✘ |
| `containsTextIsCaseSensitiveAndDoesNotMatchSubstrings()` | — | ✘ |
| `containsTextIsFalseOnAnEmptyTable()` | — | ✘ |

**class `QuarkTest.Merging`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `mergingBringsTheOtherTablesStringsAcross()` | — | ✘ |
| `mergingOffsetsTheIncomingHandlesSoNothingIsOverwritten()` | — | ✘ |
| `mergingLeavesTheSourceTableAlone()` | — | ✘ |
| `mergingNullOrAnEmptyTableIsANoOp()` | — | ✘ |

**class `QuarkTest.Lifecycle`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `initNamesTheModule()` | — | ✘ |
| `anUninitialisedTableRefusesToStoreOrFetch()` | — | ✘ |
| `cleanupReleasesTheStorage()` | — | ✘ |
| `reinitialisingAfterCleanupGivesAnEmptyTable()` | — | ✘ |


## middle/utils

##### `NumberUtilsCParityTest.java`

**Responsible for:** NumberUtils against the values the compiled C of z-util.c returned, rather than against values worked out from the Java.

**class `NumberUtilsCParityTest`** — class Javadoc: ✔ · type stamp: on 261014

- Fields (2/4 documented): ✔ `GUARD_32`, `GUARD_16` — ✘ `MAX_I`, `MIN_I`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `listOf(int)` | — | ✔ |
| `cValue(int, long, long)` | — | ✔ |

**class `NumberUtilsCParityTest.Guards32`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addGuardIMatchesCOverTheBoundaryGrid()` | — | ✘ |
| `subGuardIMatchesCOverTheBoundaryGrid()` | — | ✘ |

**class `NumberUtilsCParityTest.Guards16`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `addGuardI16MatchesCOverTheBoundaryGrid()` | — | ✘ |
| `subGuardI16MatchesCOverTheBoundaryGrid()` | — | ✘ |
| `anArgumentOutsideSixteenBitsIsNotNarrowedAsCWouldNarrowIt()` | — | ✔ |

**class `NumberUtilsCParityTest.Mean`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `aWholeMean()` | — | ✘ |
| `aMeanWithAFractionIsTheFlooredIntegerPlusTheFraction()` | — | ✘ |
| `aNegativeFractionalMeanIsFlooredAndTheFractionStaysPositive()` | — | ✘ |
| `aZeroMeanFromMixedSigns()` | — | ✘ |
| `aFractionBelowOneWholeNumber()` | — | ✘ |
| `theExtremesOfTheIntRangeSumWithoutOverflow()` | — | ✘ |
| `onlyTheFirstSizeEntriesAreUsed()` | — | ✘ |
| `aSizeOfZeroOrLessIsZeroAsInC()` | — | ✘ |

**class `NumberUtilsCParityTest.Variance`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oneToFiveInEveryForm()` | — | ✘ |
| `oneToFourInEveryForm()` | — | ✘ |
| `theTextbookSampleInEveryForm()` | — | ✘ |
| `aNegativeSampleInEveryForm()` | — | ✘ |
| `aMixedSignSampleInEveryForm()` | — | ✘ |
| `aConstantSampleHasNoVarianceWhateverItsSize()` | — | ✘ |
| `valuesNextToTheIntLimitsStillGiveExactSmallVariances()` | — | ✘ |
| `aWholeNumberVarianceJustBelowTheCapIsKept()` | — | ✘ |
| `aVarianceAboveIntMaxIsCappedWithNoFraction()` | — | ✘ |
| `onlyTheFirstSizeEntriesAreUsed()` | — | ✘ |
| `aSizeOfOneOrLessIsZeroAsInC()` | — | ✘ |

**class `NumberUtilsCParityTest.LargeLists`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `lcgList(int, int)` | — | ✔ |
| `aTwoThousandEntryMeanAndVarianceAgreeWithC()` | — | ✘ |
| `aThreeThousandEntryMeanAgreesWithC()` | — | ✘ |
| `aFractionPastUintMaxIsExactWhereCApproximates()` | — | ✔ |
| `aFractionTooBigForALongThrowsWhereCApproximates()` | — | ✔ |

**class `NumberUtilsCParityTest.Cmp`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `givesExactlyMinusOneZeroOrOne()` | — | ✘ |
| `theExtremesDoNotOverflowIntoTheWrongSign()` | — | ✘ |


##### `NumberUtilsTest.java`

**Responsible for:** Unit tests for NumberUtils, the port of the arithmetic helpers in the C source's src/z-util.c.

**class `NumberUtilsTest`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `listOf(int)` | — | ✔ |
| `assertFraction(int, int, Rational)` | — | ✔ |

**class `NumberUtilsTest.IntegerGuards`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinaryArithmeticIsUnaffected()` | — | ✘ |
| `additionSaturatesAtTheTopOfTheRange()` | — | ✘ |
| `additionSaturatesAtTheBottomOfTheRange()` | — | ✘ |
| `theLargestExactAdditionsAreStillComputed()` | — | ✘ |
| `mixedSignAdditionCanNeverOverflowAndIsPassedStraightThrough()` | — | ✘ |
| `subtractionSaturatesAtTheTopOfTheRange()` | — | ✘ |
| `subtractionSaturatesAtTheBottomOfTheRange()` | — | ✘ |
| `theLargestExactSubtractionsAreStillComputed()` | — | ✘ |
| `zeroIsAnIdentityForBothGuards()` | — | ✘ |

**class `NumberUtilsTest.SixteenBitGuards`** — class Javadoc: ✔ · type stamp: —

- Fields (2/2 documented): ✔ `MAX_I16`, `MIN_I16`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `ordinaryArithmeticIsUnaffected()` | — | ✘ |
| `additionClampsToTheSixteenBitLimits()` | — | ✘ |
| `subtractionClampsToTheSixteenBitLimits()` | — | ✘ |
| `theLimitsThemselvesAreReachableExactly()` | — | ✘ |
| `mixedSignsPassStraightThroughWhenTheyStayInRange()` | — | ✘ |

**class `NumberUtilsTest.Mean`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `meanOfAWholeList()` | — | ✘ |
| `aFractionalMeanIsKeptExact()` | — | ✘ |
| `onlyTheFirstSizeElementsAreUsed()` | — | ✘ |
| `anOversizedSizeIsTruncatedToTheListLength()` | — | ✘ |
| `aSizeOfZeroOrAnEmptyListGivesZero()` | — | ✘ |
| `negativeValuesAreAveragedNormally()` | — | ✘ |
| `theInputListIsNotModified()` | — | ✘ |

**class `NumberUtilsTest.Variance`** — class Javadoc: ✔ · type stamp: —

- Fields (1/1 documented): ✔ `SAMPLE`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `biasedVarianceDividesBySize()` | — | ✘ |
| `unbiasedVarianceDividesBySizeMinusOne()` | — | ✘ |
| `varianceOfTheMeanDividesByTheSizeAgain()` | — | ✘ |
| `aConstantSampleHasNoVariance()` | — | ✘ |
| `aSizeOfOneOrLessGivesZeroRatherThanDividingByZero()` | — | ✘ |
| `onlyTheFirstSizeElementsAreUsed()` | — | ✘ |
| `theInputListIsNotModified()` | — | ✘ |


##### `StringUtilsAsciiHelpersTest.java`

**Responsible for:** Tests the five ASCII helpers ported from C's h-basic.h and : C2I and I2C for the macros A2I (src/h-basic.h:189) and I2A (src/h-basic.h:190), and isAlpha, isDigit and isSpace for the C library's isalpha, isdigit and isspace as they behave in the "C" locale the game runs in.

**class `StringUtilsAsciiHelpersTest`** — class Javadoc: ✔ · type stamp: —

**class `StringUtilsAsciiHelpersTest.C2I`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theAlphabetMapsOntoZeroToTwentyFive()` | — | ✘ |
| `charactersOutsideTheContractStillSubtract()` | — | ✘ |

**class `StringUtilsAsciiHelpersTest.I2C`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `zeroToTwentyFiveMapsOntoTheAlphabet()` | — | ✘ |
| `i2cUndoesC2IAcrossTheRange(int)` | — | ✘ |
| `anIndexPastTheAlphabetAddsOnRegardless()` | — | ✘ |

**class `StringUtilsAsciiHelpersTest.IsAlpha`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `asciiLettersOfBothCasesAreLetters(char)` | — | ✘ |
| `nonLettersAreRejected(char)` | — | ✘ |
| `theBracketingCharactersAreExcluded()` | — | ✘ |
| `nonAsciiLettersAreRejected()` | — | ✘ |

**class `StringUtilsAsciiHelpersTest.IsDigit`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theAsciiTenAreDigits(char)` | — | ✘ |
| `nonDigitsAreRejected(char)` | — | ✘ |
| `theBracketingCharactersAreExcluded()` | — | ✘ |
| `nonAsciiDigitsAreRejected()` | — | ✘ |

**class `StringUtilsAsciiHelpersTest.IsSpace`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `theSixCCharactersAreWhitespace(char)` | — | ✘ |
| `ordinaryCharactersAreRejected(char)` | — | ✘ |
| `theBracketingCharactersAreExcluded()` | — | ✘ |
| `asciiSeparatorControlsAreRejected()` | — | ✘ |


##### `StringUtilsTest.java`

**Responsible for:** Unit tests for StringUtils, the port of the C source's string helpers (src/z-util.c and src/z-form.c).

**class `StringUtilsTest`** — class Javadoc: ✔ · type stamp: —

**class `StringUtilsTest.Formatting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `strnfcatAppendsTheFormattedTailToTheHead()` | — | ✘ |
| `strnfcatWithNoPlaceholdersIsAPlainConcatenation()` | — | ✘ |
| `formatAndVformatAreStraightDelegationsToStringFormat()` | — | ✘ |
| `theBoundedFormattersKeepMaxMinusOneCharacters()` | — | ✘ |
| `theBoundedFormattersSubstituteBeforeTheyTruncate()` | — | ✘ |

**class `StringUtilsTest.Agreement`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `oneItemTakesNoPluralSuffix()` | — | ✘ |
| `anythingOtherThanOneTakesAnS(int)` | — | ✘ |
| `verbAgreementPicksTheSingularOnlyForOne()` | — | ✘ |

**class `StringUtilsTest.BoundedCopies`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `clipToTakesTheFirstNCharacters()` | — | ✘ |
| `strCpyTakesTheFirstNCharacters()` | — | ✘ |
| `strCpyStopsAtTheEndOfAShortSource()` | — | ✘ |
| `strCpyOfANegativeLengthIsEmpty()` | — | ✘ |
| `strCatJoinsThenTruncates()` | — | ✘ |
| `strCatReturnsEverythingWhenTheBoundIsGenerous()` | — | ✘ |
| `strCatOfANegativeSizeIsEmpty()` | — | ✘ |
| `strCatWithEmptyOperands()` | — | ✘ |

**class `StringUtilsTest.Comparison`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `streqIsPlainCaseSensitiveEquality()` | — | ✘ |
| `strICmpIgnoresCaseAndOrdersAlphabetically()` | — | ✘ |
| `strICmpOrdersAPrefixBeforeTheLongerString()` | — | ✘ |
| `strNICmpComparesOnlyTheFirstNCharacters()` | — | ✘ |
| `strNICmpNeverReadsPastTheShorterString()` | — | ✘ |
| `strIStrReturnsTheTailFromTheFirstCaseInsensitiveMatch()` | — | ✘ |
| `strIStrFallsBackToTheNeedleWhenThereIsNoMatch()` | — | ✘ |
| `textSchrFindsACharacterAnywhereInTheString()` | — | ✘ |
| `stringLengthCountsCharacters()` | — | ✘ |

**class `StringUtilsTest.Affixes`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `suffixMatchesTheEndOfTheString()` | — | ✘ |
| `suffixIsCaseSensitiveButSuffixIIsNot()` | — | ✘ |
| `aWholeStringIsASuffixOfItself()` | — | ✘ |
| `anEmptySuffixAlwaysMatches()` | — | ✘ |
| `anEmptyStringHasNoSuffixAtAll()` | — | ✘ |
| `aSuffixLongerThanTheStringCannotMatch()` | — | ✘ |
| `prefixMatchesTheStartOfTheString()` | — | ✘ |
| `prefixIsCaseSensitiveButPrefixIIsNot()` | — | ✘ |
| `anEmptyPrefixAlwaysMatchesAndAWholeStringIsItsOwnPrefix()` | — | ✘ |

**class `StringUtilsTest.CharactersAndNumbers`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `isPrintReturnsPrintableAsciiUnchanged()` | — | ✘ |
| `isPrintReturnsNulForControlAndNonAsciiCharacters()` | — | ✘ |
| `vowelsAreRecognisedInEitherCase(char)` | — | ✘ |
| `nonVowelsAreRejected(char)` | — | ✘ |
| `hexDigitsConvertInEitherCase()` | — | ✘ |
| `nonHexCharactersConvertToMinusOne()` | — | ✘ |
| `hexStringsConvertInEitherCase()` | — | ✘ |
| `aMalformedHexStringConvertsToMinusOne()` | — | ✘ |
| `containsOnlySpacesTreatsAllWhitespaceAlike()` | — | ✘ |

**class `StringUtilsTest.Rewriting`** — class Javadoc: ✔ · type stamp: —

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `strCapUppercasesTheFirstCharacterOnly()` | — | ✘ |
| `strCapLeavesNonLettersAndTheEmptyStringAlone()` | — | ✘ |
| `strSkipRemovesRunsOfTheTargetCharacter()` | — | ✘ |
| `strSkipSpareTheOccurrenceThatFollowsTheEscapeCharacter()` | — | ✘ |
| `strSkipLeavesAStringWithNoOccurrencesUntouched()` | — | ✘ |


## testsupport

##### `CalcBonusesFixture.java`

**Responsible for:** A builder for the character calcBonuses needs, and the state it fills.

**class `CalcBonusesFixture`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (4/4 documented): ✔ `NEUTRAL_STAT`, `player`, `body`, `state`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CalcBonusesFixture(Player)` | — | ✔ |
| `plainCharacter()` | — | ✔ |
| `plainCharacter(Player)` | on 260901 | ✔ |
| `plainClass()` | — | ✔ |
| `item(TValue)` | — | ✔ |
| `item(TValue, int)` | — | ✔ |
| `identifiedItem(TValue)` | — | ✔ |
| `learnCombatValues(ItemObject)` | — | ✔ |
| `listOf(T)` | — | ✔ |
| `level(int)` | — | ✔ |
| `depth(int)` | — | ✔ |
| `stat(Stats, int)` | — | ✔ |
| `race(PlayerRace)` | — | ✔ |
| `playerClass(PlayerClass)` | — | ✔ |
| `shape(PlayerShape)` | — | ✔ |
| `timed(TimedEffect, int)` | — | ✔ |
| `knows(ObjectModifier)` | — | ✔ |
| `wear(String, ItemObject)` | — | ✔ |
| `calculate()` | — | ✔ |
| `calculateKnownOnly()` | — | ✔ |
| `calculateHypothetical()` | — | ✔ |
| `player()` | — | ✔ |
| `body()` | — | ✔ |
| `set(String, Object)` | — | ✔ |
| `read(String)` | — | ✔ |


##### `CurseFixture.java`

**Responsible for:** The one place a test builds a Curse.

**class `CurseFixture`** — class Javadoc: ✔ · type stamp: on 261005

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `CurseFixture()` | — | ✘ |
| `curse(String, List<ObjectBase>, int, Effect, Flag<ObjectFlag>, Map<ObjectModifier,Integer>, Map<ElementEnum,ElementInfo>, int, int, int, List<String>, Flag<ObjectFlag>, String, String, int)` | — | ✔ |


##### `ItemFixture.java`

**Responsible for:** A builder for a loaded ItemObject — one that has been all the way through the parser and so has every collection and every dice field filled in.

**class `ItemFixture`** — class Javadoc: ✔ · type stamp: on 260827

- Fields (14/14 documented): ✔ `tValue`, `flags`, `curses`, `kind`, `number`, `timeout`, `ego`, `artifact`, `location`, `origin`, `originDepth`, `originRace`, `known`, `fullyKnown`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| ctor `ItemFixture(TValue)` | — | ✔ |
| `item(TValue)` | — | ✔ |
| `allElements()` | — | ✔ |
| `allModifiers()` | — | ✔ |
| `kindWithDice(TValue)` | — | ✔ |
| `kindWithBase(TValue, String, int)` | — | ✔ |
| `loadedKind(TValue, String, int)` | — | ✔ |
| `set(Object, String, Object)` | — | ✔ |
| `read(Object, String)` | — | ✔ |
| `setStatic(Class<?>, String, Object)` | — | ✔ |
| `listOf(T)` | — | ✔ |
| `mutableSet()` | — | ✔ |
| `kind(ObjectKind)` | — | ✔ |
| `number(int)` | — | ✔ |
| `timeout(int)` | — | ✔ |
| `flags(ObjectFlag)` | — | ✔ |
| `ego(EgoItem)` | — | ✔ |
| `artifact(Artifact)` | — | ✔ |
| `curse(Curse, CurseData)` | — | ✔ |
| `location(Loc)` | — | ✔ |
| `origin(ObjectOriginEnum, int, MonsterRace)` | — | ✔ |
| `known(ItemObject)` | — | ✔ |
| `fullyKnown()` | — | ✔ |
| `build()` | — | ✔ |
| `assemble(ItemObject)` | — | ✔ |


##### `SeededPlayerRegistry.java`

**Responsible for:** A JUnit extension that guarantees the registries hold the entries a uk.co.jackoftradesltd.middle.player.Player needs — a body, a race, a curse list and a timed-effect list — so that a test class can build one, and calculate with it, without depending on another suite having run first.

**class `SeededPlayerRegistry`** — class Javadoc: ✔ · type stamp: on 260820

- Fields (5/5 documented): ✔ `seededBodies`, `seededRaces`, `seededCurses`, `seededTimedEffects`, `seededConstants`

| Function | Ported (coded) | Javadoc |
|---|---|---|
| `humanoidBody()` | — | ✔ |
| `plainRace(PlayerBody)` | — | ✔ |
| `registryField(String)` | — | ✔ |
| `objectRegistryField(String)` | — | ✔ |
| `carryCapOnly()` | — | ✔ |
| `constantsField()` | — | ✔ |
| `beforeAll(ExtensionContext)` | — | ✘ (override) |
| `afterAll(ExtensionContext)` | — | ✘ (override) |

