# No / low Javadoc sweep

Full sweep of `src/main/java` for classes, interfaces, enums, records, methods, fields, constructors and enum constants
that carry no Javadoc, or a Javadoc block thin enough to add little (e.g. a bare `@return`/`@param` tag with no
descriptive sentence, or a one-line stub).

**Method**: a heuristic brace-depth parser (not a real Java parser) walked every `.java` file under `src/main/java`,
tracking whether each declaration sits directly in a type body versus inside a method/lambda/initializer block, then
checked for an immediately preceding `/** ... */` block.

**Excluded from the sweep:**

- ANTLR-generated sources - any file carrying a `// Generated from ... by ANTLR` header, wherever it lives (the
  `antlr4/` packages, and the older `backend/parser/grammars/**` layout alike). Generated output is not authored code.
- `{@inheritDoc}` blocks - treated as adequate documentation, not low.
- Enum constants in enums whose class-level Javadoc explicitly says the constants are self-describing and documented
  collectively rather than individually (e.g. `ProjectionEnum`, `ObjectFlag`, `MonsterFlag` and 14 others) - those enums
  keep their class-level doc checked, but the per-constant check is skipped since the file itself declares that
  convention.

**Caveats:** this is a regex/brace heuristic, not `javac`. It can miscount in unusual formatting (e.g. multiple
declarations crammed on one line, exotic generics), and the "low" bar is a rough one (any block whose non-tag prose is
under ~20 characters, or empty). Treat the counts as a strong signal for where to look, not a precise audit - re-check
anything before writing to a `src/main/**` file, per the standing rule.

## Summary

- Files scanned (excluding generated sources): 597
- Files with at least one finding: 297
- Total findings: 1833 (1189 missing, 644 low)

By declaration kind:

| Kind        | Missing | Low | Total |
|-------------|---------|-----|-------|
| class       | 30      | 0   | 30    |
| interface   | 5       | 0   | 5     |
| enum        | 16      | 1   | 17    |
| record      | 19      | 0   | 19    |
| constructor | 16      | 24  | 40    |
| method      | 315     | 615 | 930   |
| field       | 290     | 3   | 293   |
| enum-const  | 498     | 1   | 499   |

## By file

Grouped by file, sorted alphabetically. Each entry is `line: kind name - STATUS` and, for LOW entries, the first line of
the existing doc comment for context.

### `Main.java`

2 missing, 0 low

- L70: field `logger` - MISSING
- L301: method `checkDirectoryOption` - MISSING

**COMPLETED 2026-09-14**

### `backend/AngbandModule.java`

0 missing, 1 low

- L32: method `getName` - LOW

**COMPLETED 2026-09-14**

### `backend/io/AngDir.java`

1 missing, 1 low

- L45: field `logger` - MISSING
- L78: constructor `AngDir` - LOW - "Constructor"

**COMPLETED 2026-09-14**

### `backend/io/AngFile.java`

0 missing, 1 low

- L60: constructor `AngFile` - LOW - "Constructor"

**COMPLETED 2026-09-14**

### `backend/io/Datafile.java`

2 missing, 0 low

- L20: class `Datafile` - MISSING
- L22: method `deactivateRandartFile` - MISSING

**COMPLETED 2026-09-14**

### `backend/io/savefiles/SavefileDetails.java`

11 missing, 0 low

- L20: class `SavefileDetails` - MISSING
- L21: field `fileName` - MISSING
- L22: field `description` - MISSING
- L23: field `offset` - MISSING
- L25: constructor `SavefileDetails` - MISSING
- L31: method `getFileName` - MISSING
- L35: method `getDescription` - MISSING
- L39: method `getOffset` - MISSING
- L43: method `setFileName` - MISSING
- L47: method `setDescription` - MISSING
- L51: method `setOffset` - MISSING

**COMPLETED 2026-09-15**

### `backend/io/savefiles/SavefileGetterImpl.java`

7 missing, 0 low

- L23: class `SavefileGetterImpl` - MISSING
- L24: field `directory` - MISSING
- L25: field `details` - MISSING
- L26: field `haveDetails` - MISSING
- L27: field `haveSaveDir` - MISSING
- L29: constructor `SavefileGetterImpl` - MISSING
- L34: method `gotSavefile` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/ActivationReader.java`

1 missing, 0 low

- L79: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/ArtifactReader.java`

2 missing, 0 low

- L61: method `parseWithResults` - MISSING
- L69: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/ChestTrapReader.java`

1 missing, 0 low

- L52: field `logger` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/CurseReader.java`

2 missing, 0 low

- L62: method `parseWithResults` - MISSING
- L70: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/DungeonProfileReader.java`

1 missing, 0 low

- L48: field `logger` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/EgoItemReader.java`

2 missing, 0 low

- L62: method `parseWithResults` - MISSING
- L70: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/FlavourReader.java`

1 missing, 0 low

- L47: field `logger` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/GameConstantsParseResult.java`

0 missing, 3 low

- L46: constructor `GameConstantsParseResult` - LOW - "Constructor"
- L65: method `getData` - LOW - "Accessor"
- L74: method `getErrors` - LOW - "The list of errors"

**COMPLETED 2026-09-15**

### `backend/parser/GameConstantsReader.java`

1 missing, 0 low

- L50: method `parse` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/HintReader.java`

1 missing, 0 low

- L49: field `logger` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/MonsterBaseReader.java`

2 missing, 0 low

- L63: method `parseWithResults` - MISSING
- L71: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/MonsterReader.java`

2 missing, 0 low

- L62: method `parseWithResults` - MISSING
- L70: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/MonsterSpellReader.java`

2 missing, 0 low

- L62: method `parseWithResults` - MISSING
- L70: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/NamesReader.java`

3 missing, 0 low

- L45: field `logger` - MISSING
- L58: method `parseWithResults` - MISSING
- L66: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/ObjectBaseReader.java`

2 missing, 0 low

- L62: method `parseWithResults` - MISSING
- L70: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/ObjectPropertyReader.java`

2 missing, 0 low

- L61: method `parseWithResults` - MISSING
- L69: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/PlayerClassReader.java`

1 missing, 0 low

- L80: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/PlayerRaceReader.java`

2 missing, 0 low

- L62: method `parseWithResults` - MISSING
- L70: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/PlayerTimedReader.java`

2 missing, 0 low

- L62: method `parseWithResults` - MISSING
- L70: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/ProjectionReader.java`

1 missing, 0 low

- L84: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/QuestReader.java`

1 missing, 0 low

- L49: field `logger` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/RoomProfileReader.java`

1 missing, 1 low

- L46: field `logger` - MISSING
- L52: method `parse` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/TrapReader.java`

1 missing, 0 low

- L47: field `logger` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/VaultReader.java`

1 missing, 1 low

- L50: field `logger` - MISSING
- L56: method `parse` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/WorldReader.java`

1 missing, 0 low

- L83: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/artifact/ArtifactAssembler.java`

0 missing, 1 low

- L48: method `assemble` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/body/BodyParseRecord.java`

1 missing, 0 low

- L32: record `BodySlotRecord` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/chesttrap/ChestTrapAssembler.java`

2 missing, 0 low

- L30: class `ChestTrapAssembler` - MISSING
- L32: method `assemble` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/egoitem/EgoItemAssembler.java`

0 missing, 1 low

- L47: method `assemble` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/egoitem/EgoItemParseRecord.java`

1 missing, 0 low

- L49: record `ItemRef` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/gameconstants/GameConstantsParseRecord.java`

1 missing, 2 low

- L76: method `getLineNumber` - LOW - "Accessor"
- L86: method `getCategory` - MISSING
- L95: method `getFields` - LOW - "Accessor"

**COMPLETED 2026-09-15**

### `backend/parser/grammars/EffectAssembler.java`

3 missing, 0 low

- L90: method `assembleOne` - MISSING
- L186: method `getExpressions` - MISSING
- L231: method `getWrapperSubType` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/monster/MonsterParseRecord.java`

5 missing, 0 low

- L68: record `MonsterMimicParseRecord` - MISSING
- L72: record `MonsterBlowParseRecord` - MISSING
- L77: record `MonsterDropParseRecord` - MISSING
- L84: record `MonsterDropBaseParseRecord` - MISSING
- L90: record `MonsterFriendsParseRecord` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/monsterbase/MonsterBaseAssembler.java`

0 missing, 1 low

- L45: method `assemble` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/names/NamesAssembler.java`

0 missing, 1 low

- L41: method `assemble` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/objectbase/ObjectBaseAssembler.java`

0 missing, 1 low

- L46: method `assemble` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/playerrace/PlayerRaceAssembler.java`

0 missing, 1 low

- L53: method `assemble` - LOW

**COMPLETED 2026-09-15**

### `backend/parser/projection/ProjectionAssembler.java`

1 missing, 0 low

- L34: class `ProjectionAssembler` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/vault/VaultAssembler.java`

1 missing, 0 low

- L53: field `logger` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/world/WorldAssembler.java`

1 missing, 0 low

- L27: class `WorldAssembler` - MISSING

**COMPLETED 2026-09-15**

### `backend/parser/world/WorldParseRecord.java`

0 missing, 3 low

- L123: method `getLevelName` - LOW - "Accessor"
- L132: method `getUp` - LOW - "Accessor"
- L141: method `getDown` - LOW - "Accessor"

**COMPLETED 2026-09-15**

### `channel/EDTSender.java`

0 missing, 1 low

- L54: constructor `EDTSender` - LOW

**COMPLETED 2026-09-15**

### `channel/colour/ColourEnum.java`

4 missing, 0 low

- L25: enum `ColourEnum` - MISSING
- L26: method `COLOUR_SHADE` - MISSING
- L185: method `forTranslation` - MISSING
- L192: method `translateColour` - MISSING

**COMPLETED 2026-09-15**

### `channel/colour/ColourTranslation.java`

0 missing, 1 low

- L91: method `getValue` - LOW

**COMPLETED 2026-09-15**

### `channel/corechannel/CoreReceiver.java`

0 missing, 1 low

- L44: constructor `CoreReceiver` - LOW

**COMPLETED 2026-09-15**

### `channel/corechannel/CoreSender.java`

0 missing, 1 low

- L48: constructor `CoreSender` - LOW

**COMPLETED 2026-09-15**

### `channel/directories/AngbandDirs.java`

17 missing, 2 low

- L69: field `libPath` - MISSING
- L70: field `ANGBAND_DIR_ICONS` - MISSING
- L71: field `ANGBAND_DIR_SOUNDS` - MISSING
- L72: field `ANGBAND_DIR_TILES` - MISSING
- L73: field `ANGBAND_DIR_FONTS` - MISSING
- L74: field `ANGBAND_DIR_SCREENS` - MISSING
- L75: field `ANGBAND_DIR_HELP` - MISSING
- L76: field `ANGBAND_DIR_GAMEDATA` - MISSING
- L77: field `configPath` - MISSING
- L78: field `ANGBAND_DIR_CUSTOMIZE` - MISSING
- L79: field `userPath` - MISSING
- L80: field `ANGBAND_DIR_USER` - MISSING
- L81: field `ANGBAND_DIR_PANIC` - MISSING
- L82: field `ANGBAND_DIR_SAVE` - MISSING
- L83: field `ANGBAND_DIR_SCORES` - MISSING
- L84: field `ANGBAND_DIR_ARCHIVE` - MISSING
- L134: method `ARCHIVE` - MISSING
- L170: method `getName` - LOW
- L177: method `getPath` - LOW

**COMPLETED 2026-09-15**

### `channel/enums/ElementEnum.java`

1 missing, 2 low

- L39: method `ELEM_MAX` - MISSING
- L121: method `isBase` - LOW
- L128: method `isHasResistRune` - LOW

**COMPLETED 2026-09-15**

### `channel/enums/GameEventType.java`

1 missing, 0 low

- L260: enum-const `EVENT_ENTER_WORLD` - MISSING

**COMPLETED 2026-09-15**

### `channel/globals/Angband.java`

1 missing, 0 low

- L20: class `Angband` - MISSING

**COMPLETED 2026-09-15**

### `channel/messages/UIMessage.java`

1 missing, 0 low

- L91: record `SimpleUIMessage` - MISSING

**COMPLETED 2026-09-15**

### `channel/messages/data/EventDataBirthPoints.java`

0 missing, 3 low

- L62: method `getPoints` - LOW
- L69: method `getIncPoints` - LOW
- L76: method `getRemaining` - LOW

**COMPLETED 2026-09-15**

### `channel/messages/data/EventDataExplosion.java`

0 missing, 7 low

- L87: method `getProjType` - LOW
- L94: method `getNumGrids` - LOW
- L101: method `getDistanceToGrid` - LOW
- L108: method `isDrawing` - LOW
- L115: method `getPlayerSeesGrid` - LOW
- L122: method `getBlastGrid` - LOW
- L129: method `getCentre` - LOW

**COMPLETED 2026-09-15**

### `channel/messages/data/EventDataStat.java`

1 missing, 0 low

- L20: record `EventDataStat` - MISSING

**COMPLETED 2026-09-15**

### `channel/messages/data/PlayerEventStatusUpdate.java`

2 missing, 0 low

- L20: class `PlayerEventStatusUpdate` - MISSING
- L21: field `cachedPlayerStatusView` - MISSING

**COMPLETED 2026-09-15**

### `channel/messages/data/PlayerStatusView.java`

1 missing, 0 low

- L20: record `PlayerStatusView` - MISSING

**COMPLETED 2026-09-15**

### `channel/parser/Assembler.java`

0 missing, 1 low

- L38: method `assemble` - LOW

**COMPLETED 2026-09-15**

### `channel/parser/GrammarDriver.java`

1 missing, 0 low

- L91: method `extract` - MISSING

**COMPLETED 2026-09-15**

### `channel/parser/ParseResult.java`

1 missing, 0 low

- L32: method `hasErrors` - MISSING

**COMPLETED 2026-09-15**

### `channel/strings/AngbandDisplayCharacter.java`

1 missing, 1 low

- L31: field `logger` - MISSING
- L60: constructor `AngbandDisplayCharacter` - LOW - "Constructor"

**COMPLETED 2026-09-15**

### `channel/uichannel/UIReceiver.java`

0 missing, 1 low

- L46: constructor `UIReceiver` - LOW

**COMPLETED 2026-09-15**

### `channel/uichannel/UISender.java`

0 missing, 1 low

- L45: constructor `UISender` - LOW

**COMPLETED 2026-09-15**

### `channel/utils/Combiner.java`

9 missing, 0 low

- L22: interface `Combiner` - MISSING
- L23: field `UI_ENTRY_UNKNOWN_VALUE` - MISSING
- L24: field `UI_ENTRY_VALUE_NOT_PRESENT` - MISSING
- L25: field `UI_ENTRY_RESIST0_RES_VUL` - MISSING
- L27: method `init` - MISSING
- L29: method `accum` - MISSING
- L31: method `finish` - MISSING
- L33: method `vec` - MISSING
- L35: method `clone` - MISSING

**COMPLETED 2026-09-15**

### `channel/utils/Flag.java`

2 missing, 0 low

- L66: constructor `Flag` - MISSING
- L72: constructor `Flag` - MISSING

**COMPLETED 2026-09-15**

### `channel/utils/UIEntryCombinerState.java`

13 missing, 0 low

- L20: class `UIEntryCombinerState` - MISSING
- L21: field `negAccum` - MISSING
- L22: field `negAccumAux` - MISSING
- L23: field `accum` - MISSING
- L24: field `accumAux` - MISSING
- L26: method `getNegAccum` - MISSING
- L30: method `setNegAccum` - MISSING
- L34: method `getNegAccumAux` - MISSING
- L38: method `setNegAccumAux` - MISSING
- L42: method `getAccum` - MISSING
- L46: method `setAccum` - MISSING
- L50: method `getAccumAux` - MISSING
- L54: method `setAccumAux` - MISSING

**COMPLETED 2026-09-15**

### `channel/utils/combiners/AddCombiner.java`

1 missing, 0 low

- L50: field `state` - MISSING

### `channel/utils/combiners/BitwiseOrCombiner.java`

1 missing, 0 low

- L48: field `state` - MISSING

### `channel/utils/combiners/FirstCombiner.java`

1 missing, 0 low

- L41: field `state` - MISSING

### `channel/utils/combiners/LargestCombiner.java`

1 missing, 0 low

- L85: field `state` - MISSING

### `channel/utils/combiners/LastCombiner.java`

1 missing, 0 low

- L41: field `state` - MISSING

### `channel/utils/combiners/LogicalOrCombiner.java`

1 missing, 0 low

- L57: field `state` - MISSING

### `channel/utils/combiners/LogicalOrWithCancelCombiner.java`

1 missing, 0 low

- L69: field `state` - MISSING

### `frontend/SwingUI.java`

3 missing, 0 low

- L78: field `logger` - MISSING
- L143: field `screen` - MISSING
- L525: field `frame` - MISSING

### `frontend/colour/Colour.java`

1 missing, 0 low

- L105: method `getColour` - MISSING

### `frontend/colour/MonsterRaceCycler.java`

5 missing, 0 low

- L23: class `MonsterRaceCycler` - MISSING
- L24: field `monsterCyclerByRace` - MISSING
- L26: constructor `MonsterRaceCycler` - MISSING
- L29: method `addCycler` - MISSING
- L33: method `getCycler` - MISSING

### `frontend/entries/UIEntry.java`

18 missing, 2 low

- L143: method `getName` - LOW
- L147: method `uiEntryHasCategory` - MISSING
- L151: method `uiEntrySearchCategories` - MISSING
- L167: method `ELEMENT` - MISSING
- L167: enum-const `NONE` - MISSING
- L177: field `value` - MISSING
- L183: method `fromValue` - MISSING
- L192: method `toString` - LOW
- L211: method `getParameter` - MISSING
- L215: method `getStatOrElement` - MISSING
- L219: method `getRenderer` - MISSING
- L223: method `getCombineType` - MISSING
- L227: method `getPriorityNum` - MISSING
- L231: method `entryFlagHas` - MISSING
- L235: method `getDescription` - MISSING
- L239: method `getLabel` - MISSING
- L243: method `getLabel2` - MISSING
- L247: method `getLabel5` - MISSING
- L251: method `getCategories` - MISSING
- L255: method `getTemplate` - MISSING

### `frontend/entries/UIEntryBase.java`

5 missing, 1 low

- L96: method `toString` - LOW
- L108: method `getRenderer` - MISSING
- L112: method `getCombine` - MISSING
- L116: method `getCategories` - MISSING
- L120: method `getFlags` - MISSING
- L124: method `getDesc` - MISSING

### `frontend/entries/UIEntryIterator.java`

11 missing, 0 low

- L23: class `UIEntryIterator` - MISSING
- L24: field `entries` - MISSING
- L25: field `index` - MISSING
- L27: constructor `UIEntryIterator` - MISSING
- L32: constructor `UIEntryIterator` - MISSING
- L37: constructor `UIEntryIterator` - MISSING
- L42: method `getEntries` - MISSING
- L46: method `getNum` - MISSING
- L50: method `getIndex` - MISSING
- L54: method `addEntry` - MISSING
- L58: method `advance` - MISSING

### `frontend/entries/UIEntryRenderer.java`

6 missing, 2 low

- L91: method `toString` - LOW
- L107: method `getName` - LOW
- L111: method `getCode` - MISSING
- L115: method `getColours` - MISSING
- L119: method `getLabelColours` - MISSING
- L123: method `getSymbols` - MISSING
- L127: method `getnDigit` - MISSING
- L131: method `getSign` - MISSING

### `frontend/entries/enums/UIEntryRendererEnum.java`

1 missing, 0 low

- L36: method `UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX` - MISSING

### `frontend/events/UIEntryCategory.java`

8 missing, 0 low

- L20: class `UIEntryCategory` - MISSING
- L21: field `name` - MISSING
- L22: field `priority` - MISSING
- L23: field `prioritySet` - MISSING
- L25: constructor `UIEntryCategory` - MISSING
- L31: method `getName` - MISSING
- L35: method `getPriority` - MISSING
- L39: method `isPrioritySet` - MISSING

### `frontend/inputfromuser/UILoop.java`

3 missing, 0 low

- L107: field `screen` - MISSING
- L327: method `writeInitString` - MISSING
- L333: method `onEventDispatchThread` - MISSING

### `frontend/screen/Term.java`

2 missing, 1 low

- L246: field `outputHook` - MISSING
- L248: field `owner` - MISSING
- L430: method `getTermData` - LOW

### `frontend/screen/TermData.java`

1 missing, 7 low

- L50: field `screen` - MISSING
- L169: method `isMapActive` - LOW
- L176: method `getTileWidth` - LOW
- L183: method `getTileHeight` - LOW
- L190: method `getSizeOH2` - LOW
- L197: method `getSizeOW2` - LOW
- L204: method `getSizeOH1` - LOW
- L211: method `getSizeOW1` - LOW

### `frontend/screen/TermWin.java`

0 missing, 1 low

- L52: field `cy` - LOW - "Cursor row."

### `frontend/screen/TextOut.java`

5 missing, 1 low

- L52: constructor `TextOut` - LOW
- L238: enum `TagState` - MISSING
- L239: enum-const `START_TAG` - MISSING
- L239: enum-const `END_TAG` - MISSING
- L239: enum-const `IN_TAG_TEXT` - MISSING
- L243: record `SectionDetails` - MISSING

### `frontend/screen/Window.java`

1 missing, 0 low

- L105: method `show` - MISSING

### `frontend/screen/enums/CombinerName.java`

3 missing, 0 low

- L56: field `combiner` - MISSING
- L58: constructor `CombinerName` - MISSING
- L62: method `init` - MISSING

### `frontend/screen/grid/Region.java`

3 missing, 0 low

- L57: field `left` - MISSING
- L58: field `rows` - MISSING
- L59: field `cols` - MISSING

### `frontend/screen/grid/Screen.java`

2 missing, 0 low

- L65: field `frame` - MISSING
- L117: method `splashScreenNote` - MISSING

### `frontend/screen/hooks/TermScreenHook.java`

3 missing, 0 low

- L23: class `TermScreenHook` - MISSING
- L24: field `screen` - MISSING
- L26: constructor `TermScreenHook` - MISSING

### `frontend/screen/hooks/TermTextHook.java`

4 missing, 0 low

- L22: interface `TermTextHook` - MISSING
- L23: method `putStr` - MISSING
- L25: method `cPrt` - MISSING
- L27: method `erase` - MISSING

### `frontend/screen/hooks/TermXtraWinDelay.java`

0 missing, 1 low

- L35: method `doSomething` - LOW

### `frontend/sounds/MessageBoxFlags.java`

29 missing, 1 low

- L35: method `MB_RTLREADING` - MISSING
- L35: enum-const `MB_OK` - MISSING
- L36: enum-const `MB_OKCANCEL` - MISSING
- L37: enum-const `MB_ABORTRETRYIGNORE` - MISSING
- L38: enum-const `MB_YESNOCANCEL` - MISSING
- L39: enum-const `MB_YESNO` - MISSING
- L40: enum-const `MB_RETRYCANCEL` - MISSING
- L41: enum-const `MB_CANCELTRYCONTINUE` - MISSING
- L42: enum-const `MB_ICONHAND` - MISSING
- L43: enum-const `MB_ICONQUESTION` - MISSING
- L44: enum-const `MB_ICONEXCLAMATION` - MISSING
- L46: enum-const `MB_USERICON` - MISSING
- L47: enum-const `MB_ICONWARNING` - MISSING
- L48: enum-const `MB_ICONERROR` - MISSING
- L49: enum-const `MB_ICONINFORMATION` - MISSING
- L50: enum-const `MB_ICONSTOP` - MISSING
- L51: enum-const `MB_DEFBUTTON1` - MISSING
- L52: enum-const `MB_DEFBUTTON2` - MISSING
- L53: enum-const `MB_DEFBUTTON3` - MISSING
- L54: enum-const `MB_DEFBUTTON4` - MISSING
- L55: enum-const `MB_APPLMODAL` - MISSING
- L56: enum-const `MB_SYSTEMMODAL` - MISSING
- L57: enum-const `MB_TASKMODAL` - MISSING
- L58: enum-const `MB_HELP` - MISSING
- L59: enum-const `MB_SETFOREGROUND` - MISSING
- L60: enum-const `MB_DEFAULT_DESKTOP_ONLY` - MISSING
- L61: enum-const `MB_TOPMOST` - MISSING
- L62: enum-const `MB_RIGHT` - MISSING
- L63: enum-const `MB_RTLREADING` - MISSING
- L83: method `getFileName` - LOW

### `frontend/splash/SplashScreen.java`

4 missing, 0 low

- L347: enum-const `START_TAG` - MISSING
- L347: enum-const `END_TAG` - MISSING
- L347: enum-const `IN_COLOUR_TEXT` - MISSING
- L348: enum-const `IN_NORMAL_TEXT` - MISSING

### `frontend/ui/UIEntryCode.java`

4 missing, 0 low

- L29: class `UIEntryCode` - MISSING
- L30: field `sortCategoryName` - MISSING
- L32: method `initialiseUIEntryIterator` - MISSING
- L52: method `sortFunction` - MISSING

### `frontend/ui/UIEntryPredicate.java`

1 missing, 0 low

- L20: interface `UIEntryPredicate` - MISSING

### `frontend/ui/UIPlayer.java`

7 missing, 0 low

- L30: class `UIPlayer` - MISSING
- L31: field `cachedConfig` - MISSING
- L33: method `displayPlayer` - MISSING
- L39: method `configureCharSheet` - MISSING
- L60: method `CheckForTwoCategories` - MISSING
- L100: enum `PlayerDisplayMode` - MISSING
- L101: enum-const `DISPLAY_FULL` - MISSING

### `frontend/ui/entry/assembler/UIEntryAssembler.java`

1 missing, 0 low

- L211: method `buildCategories` - MISSING

### `frontend/ui/entry/reader/UIEntryReader.java`

1 missing, 0 low

- L53: field `logger` - MISSING

### `frontend/ui/entryrenderer/reader/UIEntryRendererReader.java`

1 missing, 0 low

- L86: method `extract` - MISSING

### `frontend/ui/globals/UIDataLoader.java`

1 missing, 0 low

- L48: field `logger` - MISSING

### `frontend/ui/globals/UIRegistry.java`

1 missing, 3 low

- L44: field `logger` - MISSING
- L62: method `getUIEntryRenderers` - LOW
- L76: method `getUIEntryBases` - LOW
- L90: method `getUIEntries` - LOW

### `frontend/ui/output/Region.java`

1 missing, 0 low

- L36: method `Region` - MISSING

### `frontend/ui/player/CharSheetConfig.java`

18 missing, 0 low

- L23: class `CharSheetConfig` - MISSING
- L24: field `stat_mod_entries` - MISSING
- L25: field `resRegions` - MISSING
- L26: field `resistsByRegion` - MISSING
- L27: field `nResistsByRegion` - MISSING
- L28: field `nStatModEntries` - MISSING
- L29: field `resCols` - MISSING
- L30: field `resRows` - MISSING
- L31: field `resNLabel` - MISSING
- L33: method `getResCols` - MISSING
- L37: method `getResRows` - MISSING
- L41: method `getResNLabel` - MISSING
- L45: method `setNStatModEntries` - MISSING
- L49: method `initStatModEntries` - MISSING
- L53: method `setStatModEntry` - MISSING
- L59: method `setResNlabel` - MISSING
- L63: method `setResCols` - MISSING
- L67: method `setResRows` - MISSING

### `frontend/ui/player/CharSheetResist.java`

3 missing, 0 low

- L22: class `CharSheetResist` - MISSING
- L23: field `entry` - MISSING
- L24: field `label` - MISSING

### `middle/AllocEntry.java`

0 missing, 10 low

- L54: method `getIndex` - LOW
- L61: method `setIndex` - LOW
- L68: method `getLevel` - LOW
- L75: method `setLevel` - LOW
- L82: method `getProb1` - LOW
- L89: method `setProb1` - LOW
- L96: method `getProb2` - LOW
- L103: method `setProb2` - LOW
- L110: method `getProb3` - LOW
- L117: method `setProb3` - LOW

### `middle/Message.java`

1 missing, 4 low

- L70: constructor `Message` - MISSING
- L247: constructor `MessageT` - LOW - "Build a log entry."
- L264: method `getCount` - LOW
- L271: method `getText` - LOW
- L278: method `getType` - LOW

### `middle/cave/Chunk.java`

2 missing, 4 low

- L1238: method `getWidth` - LOW - "Getter"
- L1248: method `getHeight` - LOW - "Getter"
- L1408: method `monsterCount` - LOW
- L1599: method `getObjects` - LOW
- L2593: method `getMonCurrent` - MISSING
- L2597: method `setFeeling` - MISSING

### `middle/cave/ClockwiseDirectionLoop.java`

0 missing, 5 low

- L57: method `getLoop` - LOW
- L89: method `getXOffset` - LOW
- L96: method `getYOffset` - LOW
- L103: method `getGrid` - LOW
- L165: method `getNext` - LOW

### `middle/cave/Feature.java`

0 missing, 7 low

- L178: method `getTerrainFlag` - LOW
- L200: method `isMagma` - LOW - "Tests for Magma"
- L211: method `isQuartz` - LOW - "Tests for Quartz"
- L222: method `isGranite` - LOW - "Test for Granite"
- L310: method `isLos` - LOW - "Test line of sight"
- L546: method `getCodeFlags` - LOW
- L562: method `toString` - LOW

### `middle/cave/KeypadDirectionLoop.java`

0 missing, 5 low

- L57: method `getLoop` - LOW
- L89: method `getXOffset` - LOW
- L96: method `getYOffset` - LOW
- L103: method `getGrid` - LOW
- L165: method `getNext` - LOW

### `middle/cave/Loc.java`

0 missing, 2 low

- L79: method `getY` - LOW - "Getter for y"
- L88: method `getX` - LOW - "Getter for x"

### `middle/cave/PointSet.java`

0 missing, 1 low

- L38: constructor `PointSet` - LOW - "Constructor"

### `middle/cave/Square.java`

0 missing, 5 low

- L297: method `isQuartz` - LOW - "Tests for Quartz"
- L308: method `isMineral` - LOW - "Tests for minerals"
- L1096: method `getFeature` - LOW - "Getter"
- L1107: method `getMonsterIndex` - LOW - "Getter"
- L1118: method `setFeature` - LOW - "Setter"

### `middle/cave/Trap.java`

0 missing, 3 low

- L66: method `getKind` - LOW
- L96: method `getTrapIndex` - LOW
- L105: method `getTimeout` - LOW

### `middle/cave/TrapKind.java`

0 missing, 6 low

- L170: method `getDescription` - LOW
- L188: method `getTrapKindIndex` - LOW - "{@code tidx}"
- L203: method `getEffect` - LOW
- L210: method `getFlags` - LOW
- L220: method `getSaveFlags` - LOW - "trap"
- L229: method `getText` - LOW

### `middle/cave/chunkbuilders/BuilderType.java`

0 missing, 1 low

- L99: method `getName` - LOW

### `middle/cave/enums/DirectionEnum.java`

1 missing, 4 low

- L77: field `standard` - MISSING
- L96: method `ddx` - LOW
- L103: method `ddy` - LOW
- L110: method `ddgrid` - LOW
- L117: method `getKey` - LOW

### `middle/cave/enums/RoomFlags.java`

0 missing, 1 low

- L67: method `getHelpString` - LOW

### `middle/cave/enums/SquareEnum.java`

24 missing, 1 low

- L31: method `SQUARE_MAX` - MISSING
- L31: enum-const `SQUARE_NONE` - MISSING
- L32: enum-const `SQUARE_MARK` - MISSING
- L33: enum-const `SQUARE_GLOW` - MISSING
- L34: enum-const `SQUARE_VAULT` - MISSING
- L35: enum-const `SQUARE_ROOM` - MISSING
- L36: enum-const `SQUARE_SEEN` - MISSING
- L37: enum-const `SQUARE_VIEW` - MISSING
- L38: enum-const `SQUARE_WASSEEN` - MISSING
- L39: enum-const `SQUARE_FEEL` - MISSING
- L40: enum-const `SQUARE_TRAP` - MISSING
- L41: enum-const `SQUARE_INVIS` - MISSING
- L42: enum-const `SQUARE_WALL_INNER` - MISSING
- L43: enum-const `SQUARE_WALL_OUTER` - MISSING
- L44: enum-const `SQUARE_WALL_SOLID` - MISSING
- L45: enum-const `SQUARE_MON_RESTRICT` - MISSING
- L46: enum-const `SQUARE_NO_TELEPORT` - MISSING
- L47: enum-const `SQUARE_NO_MAP` - MISSING
- L48: enum-const `SQUARE_NO_ESP` - MISSING
- L49: enum-const `SQUARE_PROJECT` - MISSING
- L50: enum-const `SQUARE_DTRAP` - MISSING
- L51: enum-const `SQUARE_NO_STAIRS` - MISSING
- L52: enum-const `SQUARE_CLOSE_PLAYER` - MISSING
- L53: enum-const `SQUARE_MAX` - MISSING
- L72: method `getDescription` - LOW

### `middle/cave/enums/TerrainFeatureFlags.java`

34 missing, 1 low

- L31: method `TF_MAX` - MISSING
- L31: enum-const `TF_NONE` - MISSING
- L32: enum-const `TF_LOS` - MISSING
- L33: enum-const `TF_PROJECT` - MISSING
- L34: enum-const `TF_PASSABLE` - MISSING
- L35: enum-const `TF_INTERESTING` - MISSING
- L36: enum-const `TF_PERMANENT` - MISSING
- L37: enum-const `TF_EASY` - MISSING
- L38: enum-const `TF_TRAP` - MISSING
- L39: enum-const `TF_NO_SCENT` - MISSING
- L40: enum-const `TF_NO_FLOW` - MISSING
- L41: enum-const `TF_OBJECT` - MISSING
- L42: enum-const `TF_TORCH` - MISSING
- L43: enum-const `TF_HIDDEN` - MISSING
- L44: enum-const `TF_GOLD` - MISSING
- L45: enum-const `TF_CLOSABLE` - MISSING
- L46: enum-const `TF_FLOOR` - MISSING
- L47: enum-const `TF_WALL` - MISSING
- L48: enum-const `TF_ROCK` - MISSING
- L49: enum-const `TF_GRANITE` - MISSING
- L50: enum-const `TF_DOOR_ANY` - MISSING
- L51: enum-const `TF_DOOR_CLOSED` - MISSING
- L52: enum-const `TF_SHOP` - MISSING
- L53: enum-const `TF_DOOR_JAMMED` - MISSING
- L54: enum-const `TF_DOOR_LOCKED` - MISSING
- L55: enum-const `TF_MAGMA` - MISSING
- L56: enum-const `TF_QUARTZ` - MISSING
- L57: enum-const `TF_STAIR` - MISSING
- L58: enum-const `TF_UPSTAIR` - MISSING
- L59: enum-const `TF_DOWNSTAIR` - MISSING
- L60: enum-const `TF_SMOOTH` - MISSING
- L61: enum-const `TF_BRIGHT` - MISSING
- L62: enum-const `TF_FIERY` - MISSING
- L63: enum-const `TF_MAX` - MISSING
- L82: method `getDescription` - LOW

### `middle/cave/enums/TerrainFlags.java`

25 missing, 0 low

- L30: enum-const `FEAT_NONE` - MISSING
- L31: enum-const `FEAT_FLOOR` - MISSING
- L32: enum-const `FEAT_CLOSED` - MISSING
- L33: enum-const `FEAT_OPEN` - MISSING
- L34: enum-const `FEAT_BROKEN` - MISSING
- L35: enum-const `FEAT_LESS` - MISSING
- L36: enum-const `FEAT_MORE` - MISSING
- L37: enum-const `FEAT_STORE_GENERAL` - MISSING
- L38: enum-const `FEAT_STORE_ARMOR` - MISSING
- L39: enum-const `FEAT_STORE_WEAPON` - MISSING
- L40: enum-const `FEAT_STORE_BOOK` - MISSING
- L41: enum-const `FEAT_STORE_ALCHEMY` - MISSING
- L42: enum-const `FEAT_STORE_MAGIC` - MISSING
- L43: enum-const `FEAT_STORE_BLACK` - MISSING
- L44: enum-const `FEAT_HOME` - MISSING
- L45: enum-const `FEAT_SECRET` - MISSING
- L46: enum-const `FEAT_RUBBLE` - MISSING
- L47: enum-const `FEAT_MAGMA` - MISSING
- L48: enum-const `FEAT_QUARTZ` - MISSING
- L49: enum-const `FEAT_MAGMA_K` - MISSING
- L50: enum-const `FEAT_QUARTZ_K` - MISSING
- L51: enum-const `FEAT_GRANITE` - MISSING
- L52: enum-const `FEAT_PERM` - MISSING
- L53: enum-const `FEAT_LAVA` - MISSING
- L54: enum-const `FEAT_PASS_RUBBLE` - MISSING

### `middle/cave/profiles/dungeon/CaveProfile.java`

0 missing, 10 low

- L105: constructor `CaveProfile` - LOW
- L123: method `getName` - LOW
- L130: method `getBlockSize` - LOW
- L137: method `getDunRooms` - LOW
- L144: method `getDunUnusual` - LOW
- L151: method `getMaxRarity` - LOW
- L158: method `getTun` - LOW
- L165: method `getStr` - LOW
- L172: method `getRoomProfiles` - LOW
- L179: method `getMinLevel` - LOW

### `middle/cave/profiles/dungeon/RoomProfile.java`

9 missing, 1 low

- L96: constructor `RoomProfile` - LOW
- L109: method `getName` - MISSING
- L113: method `getRoomType` - MISSING
- L117: method `getRating` - MISSING
- L121: method `getHeight` - MISSING
- L125: method `getWidth` - MISSING
- L129: method `getLevel` - MISSING
- L133: method `isPit` - MISSING
- L137: method `getRarity` - MISSING
- L141: method `getCutoff` - MISSING

### `middle/cave/profiles/dungeon/StreamerProfile.java`

0 missing, 7 low

- L73: constructor `StreamerProfile` - LOW
- L85: method `getDen` - LOW
- L92: method `getRng` - LOW
- L99: method `getMam` - LOW
- L106: method `getMc` - LOW
- L113: method `getQua` - LOW
- L120: method `getQc` - LOW

### `middle/cave/profiles/dungeon/TunnelProfile.java`

0 missing, 6 low

- L67: constructor `TunnelProfile` - LOW
- L78: method `getChg` - LOW
- L85: method `getCon` - LOW
- L92: method `getJct` - LOW
- L99: method `getPen` - LOW
- L106: method `getRnd` - LOW

### `middle/cave/profiles/room/RoomTemplate.java`

0 missing, 11 low

- L81: constructor `RoomTemplate` - LOW
- L97: method `getName` - LOW
- L102: method `getMapText` - LOW
- L107: method `getMap` - LOW
- L112: method `getFlags` - LOW
- L117: method `getType` - LOW
- L122: method `getRating` - LOW
- L127: method `getHeight` - LOW
- L132: method `getWidth` - LOW
- L137: method `getDoors` - LOW
- L142: method `getTval` - LOW

### `middle/cave/profiles/vault/Vault.java`

0 missing, 10 low

- L113: constructor `Vault` - LOW - "world maximum"
- L130: method `getName` - LOW
- L137: method `getType` - LOW
- L152: method `getMap` - LOW
- L159: method `getFlags` - LOW
- L166: method `getRating` - LOW
- L173: method `getHeight` - LOW
- L180: method `getWidth` - LOW
- L187: method `getMinLevel` - LOW
- L194: method `getMaxLevel` - LOW

### `middle/cave/roombuilders/RoomType.java`

2 missing, 2 low

- L171: method `getName` - LOW
- L213: method `getRoomBuilderCount` - LOW
- L237: method `getMaxHeight` - MISSING
- L241: method `getMaxWidth` - MISSING

### `middle/combat/BlowMethod.java`

0 missing, 5 low

- L94: method `getName` - LOW
- L101: method `isCut` - LOW
- L108: method `isStun` - LOW
- L126: method `isPhys` - LOW
- L156: method `getDesc` - LOW

### `middle/combat/CriticalLevel.java`

0 missing, 4 low

- L68: method `getCutOff` - LOW
- L75: method `getMult` - LOW
- L82: method `getAdd` - LOW
- L89: method `getMsgt` - LOW

### `middle/combat/Target.java`

1 missing, 0 low

- L40: method `setMonster` - MISSING

### `middle/effect/Effect.java`

1 missing, 0 low

- L234: method `effectDo` - MISSING

### `middle/effect/EffectSubTypeWrapper.java`

0 missing, 13 low

- L203: method `getTeleportMonsterMayCast` - LOW
- L218: method `getTeleportToMonsterMayCast` - LOW
- L467: method `getSubType` - LOW
- L493: method `getTimedWrapper` - LOW
- L508: method `getNourishWrapper` - LOW
- L523: method `getMonTimedWrapper` - LOW
- L538: method `getSummonWrapper` - LOW
- L553: method `getSummonTypeWrapper` - LOW
- L568: method `getStatsWrapper` - LOW
- L583: method `getEnchantWrapper` - LOW
- L598: method `getShapeWrapper` - LOW
- L613: method `getQuakeWrapper` - LOW
- L628: method `getGlyphType` - LOW

### `middle/enums/EffectEnum.java`

1 missing, 7 low

- L36: method `EF_MAX` - MISSING
- L211: method `getSubType` - LOW
- L219: method `getAim` - LOW
- L227: method `getInfoLabel` - LOW
- L235: method `getNumberOfArguments` - LOW
- L243: method `getEffectInfo` - LOW
- L251: method `getDescription` - LOW
- L259: method `getMenuFormat` - LOW

### `middle/enums/MessageType.java`

1 missing, 0 low

- L30: method `MSG_MAX` - MISSING

### `middle/enums/Stats.java`

3 missing, 0 low

- L48: field `value` - MISSING
- L49: field `statString` - MISSING
- L56: method `getStatString` - MISSING

### `middle/enums/TrapEnum.java`

3 missing, 1 low

- L25: enum `TrapEnum` - LOW - "The trap list"
- L101: enum-const `TRF_MAX` - MISSING
- L103: field `description` - MISSING
- L105: constructor `TrapEnum` - MISSING

### `middle/game/Hint.java`

0 missing, 1 low

- L49: method `getHint` - LOW

### `middle/game/Name.java`

2 missing, 0 low

- L49: field `section` - MISSING
- L50: field `word` - MISSING

### `middle/game/NameCreator.java`

5 missing, 0 low

- L29: class `NameCreator` - MISSING
- L30: field `logger` - MISSING
- L32: field `S_WORD` - MISSING
- L33: field `E_WORD` - MISSING
- L34: field `TOTAL` - MISSING

### `middle/game/bespokeexceptions/CommandArgumentWrongTypeException.java`

0 missing, 1 low

- L35: constructor `CommandArgumentWrongTypeException` - LOW

### `middle/game/enums/CommandArgumentType.java`

7 missing, 0 low

- L39: enum-const `arg_NONE` - MISSING
- L40: enum-const `arg_STRING` - MISSING
- L41: enum-const `arg_CHOICE` - MISSING
- L42: enum-const `arg_ITEM` - MISSING
- L43: enum-const `arg_NUMBER` - MISSING
- L44: enum-const `arg_DIRECTION` - MISSING
- L45: enum-const `arg_TARGET` - MISSING

### `middle/game/enums/CommandCode.java`

115 missing, 0 low

- L45: enum-const `CMD_NULL` - MISSING
- L50: enum-const `CMD_LOADFILE` - MISSING
- L51: enum-const `CMD_NEWGAME` - MISSING
- L56: enum-const `CMD_BIRTH_INIT` - MISSING
- L57: enum-const `CMD_BIRTH_RESET` - MISSING
- L58: enum-const `CMD_CHOOSE_RACE` - MISSING
- L59: enum-const `CMD_CHOOSE_CLASS` - MISSING
- L60: enum-const `CMD_BUY_STAT` - MISSING
- L61: enum-const `CMD_SELL_STAT` - MISSING
- L62: enum-const `CMD_RESET_STATS` - MISSING
- L63: enum-const `CMD_REFRESH_STATS` - MISSING
- L64: enum-const `CMD_ROLL_STATS` - MISSING
- L65: enum-const `CMD_PREV_STATS` - MISSING
- L66: enum-const `CMD_NAME_CHOICE` - MISSING
- L67: enum-const `CMD_HISTORY_CHOICE` - MISSING
- L68: enum-const `CMD_ACCEPT_CHARACTER` - MISSING
- L73: enum-const `CMD_GO_UP` - MISSING
- L74: enum-const `CMD_GO_DOWN` - MISSING
- L75: enum-const `CMD_WALK` - MISSING
- L76: enum-const `CMD_JUMP` - MISSING
- L77: enum-const `CMD_PATHFIND` - MISSING
- L79: enum-const `CMD_INSCRIBE` - MISSING
- L80: enum-const `CMD_UNINSCRIBE` - MISSING
- L81: enum-const `CMD_AUTOINSCRIBE` - MISSING
- L82: enum-const `CMD_TAKEOFF` - MISSING
- L83: enum-const `CMD_WIELD` - MISSING
- L84: enum-const `CMD_DROP` - MISSING
- L85: enum-const `CMD_BROWSE_SPELL` - MISSING
- L86: enum-const `CMD_STUDY` - MISSING
- L87: enum-const `CMD_CAST` - MISSING
- L88: enum-const `CMD_USE_STAFF` - MISSING
- L89: enum-const `CMD_USE_WAND` - MISSING
- L90: enum-const `CMD_USE_ROD` - MISSING
- L91: enum-const `CMD_ACTIVATE` - MISSING
- L92: enum-const `CMD_EAT` - MISSING
- L93: enum-const `CMD_QUAFF` - MISSING
- L94: enum-const `CMD_READ_SCROLL` - MISSING
- L95: enum-const `CMD_REFILL` - MISSING
- L96: enum-const `CMD_USE` - MISSING
- L97: enum-const `CMD_FIRE` - MISSING
- L98: enum-const `CMD_THROW` - MISSING
- L99: enum-const `CMD_PICKUP` - MISSING
- L100: enum-const `CMD_AUTOPICKUP` - MISSING
- L101: enum-const `CMD_IGNORE` - MISSING
- L102: enum-const `CMD_DISARM` - MISSING
- L103: enum-const `CMD_REST` - MISSING
- L104: enum-const `CMD_TUNNEL` - MISSING
- L105: enum-const `CMD_OPEN` - MISSING
- L106: enum-const `CMD_CLOSE` - MISSING
- L107: enum-const `CMD_RUN` - MISSING
- L108: enum-const `CMD_EXPLORE` - MISSING
- L109: enum-const `CMD_NAVIGATE_UP` - MISSING
- L110: enum-const `CMD_NAVIGATE_DOWN` - MISSING
- L111: enum-const `CMD_HOLD` - MISSING
- L112: enum-const `CMD_ALTER` - MISSING
- L113: enum-const `CMD_STEAL` - MISSING
- L114: enum-const `CMD_SLEEP` - MISSING
- L117: enum-const `CMD_SELL` - MISSING
- L118: enum-const `CMD_BUY` - MISSING
- L119: enum-const `CMD_STASH` - MISSING
- L120: enum-const `CMD_RETRIEVE` - MISSING
- L123: enum-const `CMD_SPOIL_ARTIFACT` - MISSING
- L124: enum-const `CMD_SPOIL_MON` - MISSING
- L125: enum-const `CMD_SPOIL_MON_BRIEF` - MISSING
- L126: enum-const `CMD_SPOIL_OBJ` - MISSING
- L129: enum-const `CMD_WIZ_ACQUIRE` - MISSING
- L130: enum-const `CMD_WIZ_ADVANCE` - MISSING
- L131: enum-const `CMD_WIZ_BANISH` - MISSING
- L132: enum-const `CMD_WIZ_CHANGE_ITEM_QUANTITY` - MISSING
- L133: enum-const `CMD_WIZ_COLLECT_DISCONNECT_STATS` - MISSING
- L134: enum-const `CMD_WIZ_COLLECT_OBJ_MON_STATS` - MISSING
- L135: enum-const `CMD_WIZ_COLLECT_PIT_STATS` - MISSING
- L136: enum-const `CMD_WIZ_CREATE_ALL_ARTIFACT` - MISSING
- L137: enum-const `CMD_WIZ_CREATE_ALL_ARTIFACT_FROM_TVAL` - MISSING
- L138: enum-const `CMD_WIZ_CREATE_ALL_OBJ` - MISSING
- L139: enum-const `CMD_WIZ_CREATE_ALL_OBJ_FROM_TVAL` - MISSING
- L140: enum-const `CMD_WIZ_CREATE_ARTIFACT` - MISSING
- L141: enum-const `CMD_WIZ_CREATE_OBJ` - MISSING
- L142: enum-const `CMD_WIZ_CREATE_TRAP` - MISSING
- L143: enum-const `CMD_WIZ_CURE_ALL` - MISSING
- L144: enum-const `CMD_WIZ_CURSE_ITEM` - MISSING
- L145: enum-const `CMD_WIZ_DETECT_ALL_LOCAL` - MISSING
- L146: enum-const `CMD_WIZ_DETECT_ALL_MONSTERS` - MISSING
- L147: enum-const `CMD_WIZ_DISPLAY_KEYLOG` - MISSING
- L148: enum-const `CMD_WIZ_DUMP_LEVEL_MAP` - MISSING
- L149: enum-const `CMD_WIZ_EDIT_PLAYER_EXP` - MISSING
- L150: enum-const `CMD_WIZ_EDIT_PLAYER_GOLD` - MISSING
- L151: enum-const `CMD_WIZ_EDIT_PLAYER_START` - MISSING
- L152: enum-const `CMD_WIZ_EDIT_PLAYER_STAT` - MISSING
- L153: enum-const `CMD_WIZ_HIT_ALL_LOS` - MISSING
- L154: enum-const `CMD_WIZ_INCREASE_EXP` - MISSING
- L155: enum-const `CMD_WIZ_JUMP_LEVEL` - MISSING
- L156: enum-const `CMD_WIZ_LEARN_OBJECT_KINDS` - MISSING
- L157: enum-const `CMD_WIZ_MAGIC_MAP` - MISSING
- L158: enum-const `CMD_WIZ_PEEK_NOISE_SCENT` - MISSING
- L159: enum-const `CMD_WIZ_PERFORM_EFFECT` - MISSING
- L160: enum-const `CMD_WIZ_PLAY_ITEM` - MISSING
- L161: enum-const `CMD_WIZ_PUSH_OBJECT` - MISSING
- L162: enum-const `CMD_WIZ_QUERY_FEATURE` - MISSING
- L163: enum-const `CMD_WIZ_QUERY_SQUARE_FLAG` - MISSING
- L164: enum-const `CMD_WIZ_QUIT_NO_SAVE` - MISSING
- L165: enum-const `CMD_WIZ_RECALL_MONSTER` - MISSING
- L166: enum-const `CMD_WIZ_RERATE` - MISSING
- L167: enum-const `CMD_WIZ_REROLL_ITEM` - MISSING
- L168: enum-const `CMD_WIZ_STAT_ITEM` - MISSING
- L169: enum-const `CMD_WIZ_SUMMON_NAMED` - MISSING
- L170: enum-const `CMD_WIZ_SUMMON_RANDOM` - MISSING
- L171: enum-const `CMD_WIZ_TELEPORT_RANDOM` - MISSING
- L172: enum-const `CMD_WIZ_TELEPORT_TO` - MISSING
- L173: enum-const `CMD_WIZ_TWEAK_ITEM` - MISSING
- L174: enum-const `CMD_WIZ_WIPE_RECALL` - MISSING
- L175: enum-const `CMD_WIZ_WIZARD_LIGHT` - MISSING
- L178: enum-const `CMD_RETIRE` - MISSING
- L180: enum-const `CMD_HELP` - MISSING
- L181: enum-const `CMD_REPEAT` - MISSING

### `middle/game/enums/CommandContext.java`

4 missing, 0 low

- L33: enum-const `CTX_INIT` - MISSING
- L34: enum-const `CTX_BIRTH` - MISSING
- L35: enum-const `CTX_GAME` - MISSING
- L36: enum-const `CTX_STORE` - MISSING

### `middle/game/enums/CommandReturnCodes.java`

5 missing, 0 low

- L43: method `CMD_ARG_ABORTED` - MISSING
- L43: enum-const `CMD_OK` - MISSING
- L44: enum-const `CMD_ARG_NOT_PRESENT` - MISSING
- L45: enum-const `CMD_ARG_WRONG_TYPE` - MISSING
- L46: enum-const `CMD_ARG_ABORTED` - MISSING

### `middle/game/event/EventDataMissile.java`

0 missing, 4 low

- L69: method `getItemObject` - LOW
- L76: method `isSeen` - LOW
- L83: method `getY` - LOW
- L90: method `getX` - LOW

### `middle/game/event/projection/Projection.java`

0 missing, 5 low

- L213: method `getName` - LOW - "displayed under"
- L220: method `getType` - LOW
- L227: method `getLashDescription` - LOW
- L234: method `getProjection` - LOW
- L241: method `toString` - LOW

### `middle/game/event/projection/SourceWhat.java`

6 missing, 0 low

- L20: enum `SourceWhat` - MISSING
- L21: enum-const `SRC_NONE` - MISSING
- L22: enum-const `SRC_TRAP` - MISSING
- L23: enum-const `SRC_PLAYER` - MISSING
- L24: enum-const `SRC_MONSTER` - MISSING
- L25: enum-const `SRC_OBJECT` - MISSING

### `middle/game/event/projection/SourceWhich.java`

5 missing, 0 low

- L25: interface `SourceWhich` - MISSING
- L27: record `TrapRecord` - MISSING
- L30: record `MonsterRecord` - MISSING
- L33: record `ObjectRecord` - MISSING
- L36: record `ChestTrapRecord` - MISSING

### `middle/game/gameengine/Command.java`

1 missing, 5 low

- L143: method `getContext` - LOW
- L160: method `getCode` - LOW
- L167: method `getNrepeats` - LOW
- L190: method `getBackgroundCommand` - LOW
- L197: method `getArgs` - LOW
- L801: method `setBacgroundCommand` - MISSING

### `middle/game/gameengine/CommandArgument.java`

0 missing, 3 low

- L67: method `getName` - LOW
- L74: method `getData` - LOW
- L81: method `getType` - LOW

### `middle/game/gameengine/CommandGetterHolder.java`

0 missing, 1 low

- L56: method `getInstance` - LOW

### `middle/game/gameengine/Core.java`

3 missing, 0 low

- L68: field `logger` - MISSING
- L224: method `handleChannelOutput` - MISSING
- L264: method `getCoreChannel` - MISSING

### `middle/game/gameengine/GameState.java`

0 missing, 5 low

- L70: method `getTurn` - LOW
- L92: method `getDaycount` - LOW
- L122: method `getPlayer` - LOW
- L138: method `getCave` - LOW
- L163: method `getCommandQueue` - LOW

### `middle/game/gameengine/argumentdata/ArgumentChoice.java`

1 missing, 0 low

- L34: method `type` - MISSING

### `middle/game/gameengine/argumentdata/ArgumentDirection.java`

1 missing, 0 low

- L36: method `type` - MISSING

### `middle/game/gameengine/argumentdata/ArgumentItem.java`

1 missing, 0 low

- L46: method `getValue` - MISSING

### `middle/game/gameengine/argumentdata/ArgumentNumber.java`

1 missing, 0 low

- L35: method `type` - MISSING

### `middle/game/gameengine/argumentdata/ArgumentPoint.java`

1 missing, 0 low

- L35: method `type` - MISSING

### `middle/game/gameengine/argumentdata/ArgumentString.java`

1 missing, 0 low

- L34: method `type` - MISSING

### `middle/game/gameengine/argumentdata/ArgumentTarget.java`

1 missing, 0 low

- L38: method `type` - MISSING

### `middle/game/globals/Food.java`

0 missing, 1 low

- L92: method `getFoodValue` - LOW

### `middle/game/globals/GameConstants.java`

1 missing, 3 low

- L156: field `storeMax` - MISSING
- L478: method `getStoreMax` - LOW
- L485: method `getRandartActivationsMax` - LOW
- L492: method `getCaveProfileMax` - LOW

### `middle/game/globals/data/GameConstantsData.java`

89 missing, 0 low

- L75: field `levelMaxMonsters` - MISSING
- L77: field `monGenChance` - MISSING
- L78: field `monGenLevelMin` - MISSING
- L79: field `monGenTownDay` - MISSING
- L80: field `monGenTownNight` - MISSING
- L81: field `monGenReproMax` - MISSING
- L82: field `monGenOodChance` - MISSING
- L83: field `monGenOodAmount` - MISSING
- L84: field `monGenGroupMax` - MISSING
- L85: field `monGenGroupDist` - MISSING
- L87: field `monPlayBreakGlyph` - MISSING
- L88: field `monPlayMultRate` - MISSING
- L89: field `monPlayLifeDrain` - MISSING
- L90: field `monPlayFleeRange` - MISSING
- L91: field `monPlayTurnRange` - MISSING
- L93: field `dunGenCentMax` - MISSING
- L94: field `dunGenDoorMax` - MISSING
- L95: field `dunGenWallMax` - MISSING
- L96: field `dunGenTunnMax` - MISSING
- L97: field `dunGenAmtRoom` - MISSING
- L98: field `dunGenAmtItem` - MISSING
- L99: field `dunGenAmtGold` - MISSING
- L100: field `dunGenPitMax` - MISSING
- L102: field `worldMaxDepth` - MISSING
- L103: field `worldDayLength` - MISSING
- L104: field `worldDungeonHgt` - MISSING
- L105: field `worldDungeonWid` - MISSING
- L106: field `worldTownHgt` - MISSING
- L107: field `worldTownWid` - MISSING
- L108: field `worldFeelingTotal` - MISSING
- L109: field `worldFeelingNeed` - MISSING
- L110: field `worldStairSkip` - MISSING
- L111: field `worldMoveEnergy` - MISSING
- L113: field `carryCapPackSize` - MISSING
- L114: field `carryCapQuiverSize` - MISSING
- L115: field `carryCapQuiverSlotSize` - MISSING
- L116: field `carryCapThrownQuiverMult` - MISSING
- L117: field `carryCapFloorSize` - MISSING
- L119: field `storeInvenMax` - MISSING
- L120: field `storeTurns` - MISSING
- L121: field `storeShuffle` - MISSING
- L122: field `storeMagicLevel` - MISSING
- L124: field `objMakeMaxDepth` - MISSING
- L125: field `objMakeGreatObj` - MISSING
- L126: field `objMakeGreatEgo` - MISSING
- L127: field `objMakeFuelTorch` - MISSING
- L128: field `objMakeFuelLamp` - MISSING
- L129: field `objMakeDefaultLamp` - MISSING
- L131: field `playerMaxSight` - MISSING
- L132: field `playerMaxRange` - MISSING
- L133: field `playerStartGold` - MISSING
- L134: field `playerFoodValue` - MISSING
- L136: field `meleeCriticalDebuffToh` - MISSING
- L137: field `meleeCriticalChanceWeightScale` - MISSING
- L138: field `meleeCriticalChanceTohScale` - MISSING
- L139: field `meleeCriticalChanceLevelScale` - MISSING
- L140: field `meleeCriticalChanceTohSkillScale` - MISSING
- L141: field `meleeCriticalChanceOffset` - MISSING
- L142: field `meleeCriticalChanceRange` - MISSING
- L143: field `meleeCriticalPowerWeightScale` - MISSING
- L144: field `meleeCriticalPowerRandom` - MISSING
- L146: field `meleeCriticalLevelDataList` - MISSING
- L148: field `rangedCriticalDebuffToh` - MISSING
- L149: field `rangedCriticalChanceWeightScale` - MISSING
- L150: field `rangedCriticalChanceTohScale` - MISSING
- L151: field `rangedCriticalChanceLevelScale` - MISSING
- L152: field `rangedCriticalChanceLaunchedTohSkillScale` - MISSING
- L153: field `rangedCriticalChanceThrownTohSkillScale` - MISSING
- L154: field `rangedCriticalChanceOffset` - MISSING
- L155: field `rangedCriticalChanceRange` - MISSING
- L156: field `rangedCriticalPowerWeightScale` - MISSING
- L157: field `rangedCriticalPowerRandom` - MISSING
- L159: field `rangedCriticalLevelDataList` - MISSING
- L161: field `oMeleeCriticalDebuffToh` - MISSING
- L162: field `oMeleeCriticalPowerTohScaleNumerator` - MISSING
- L163: field `oMeleeCriticalPowerTohScaleDenominator` - MISSING
- L164: field `oMeleeCriticalChancePowerScaleNumerator` - MISSING
- L165: field `oMeleeCriticalChancePowerScaleDenominator` - MISSING
- L166: field `oMeleeCriticalChanceAddDenominator` - MISSING
- L168: field `oMeleeCriticalLevelDataList` - MISSING
- L170: field `oRangedCriticalDebuffToh` - MISSING
- L171: field `oRangedCriticalPowerLaunchedTohScaleNumerator` - MISSING
- L172: field `oRangedCriticalPowerLaunchedTohScaleDenominator` - MISSING
- L173: field `oRangedCriticalPowerThrownTohScaleNumerator` - MISSING
- L174: field `oRangedCriticalPowerThrownTohScaleDenominator` - MISSING
- L175: field `oRangedCriticalChancePowerScaleNumerator` - MISSING
- L176: field `oRangedCriticalChancePowerScaleDenominator` - MISSING
- L177: field `oRangedCriticalChanceAddDenominator` - MISSING
- L179: field `oRangedCriticalLevelDataList` - MISSING

### `middle/game/globals/loaders/DungeonLoader.java`

1 missing, 0 low

- L44: field `logger` - MISSING

### `middle/game/globals/loaders/MiscDataLoader.java`

1 missing, 0 low

- L51: field `logger` - MISSING

### `middle/game/globals/loaders/MonsterDataLoader.java`

1 missing, 0 low

- L51: field `logger` - MISSING

### `middle/game/globals/loaders/ObjectDataLoader.java`

1 missing, 0 low

- L50: field `logger` - MISSING

### `middle/game/globals/loaders/PlayerDataLoader.java`

2 missing, 0 low

- L47: field `logger` - MISSING
- L49: method `initialiseExpLevel` - MISSING

### `middle/game/globals/loaders/TerrainDataLoader.java`

1 missing, 0 low

- L46: field `logger` - MISSING

### `middle/game/globals/loaders/WorldDataLoader.java`

1 missing, 0 low

- L55: field `logger` - MISSING

### `middle/game/globals/registry/DungeonRegistry.java`

1 missing, 0 low

- L38: field `logger` - MISSING

### `middle/game/globals/registry/MiscRegistry.java`

2 missing, 3 low

- L52: field `logger` - MISSING
- L54: field `nameSections` - MISSING
- L72: method `getHints` - LOW
- L82: method `getNames` - LOW
- L92: method `getFlavours` - LOW

### `middle/game/globals/registry/MonsterRegistry.java`

1 missing, 8 low

- L50: field `logger` - MISSING
- L175: method `getMonsterRaces` - LOW
- L367: method `getMonsterRaceMax` - LOW
- L374: method `getMonsterPainMsgMax` - LOW
- L381: method `getMonsterPitTypeMax` - LOW
- L388: method `getMonsterBlowsMax` - LOW
- L395: method `getMonsterBlowsMethodsMax` - LOW
- L402: method `getMonsterBlowsEffectsMax` - LOW
- L425: method `getVisualsCyclerTable` - LOW

### `middle/game/globals/registry/ObjectRegistry.java`

0 missing, 22 low

- L422: method `getArtifactKindMax` - LOW
- L431: method `getEgoItemKindMax` - LOW
- L440: method `getRandartActivationsMax` - LOW
- L449: method `getCurseMax` - LOW
- L458: method `getSlayMax` - LOW
- L467: method `getBrandMax` - LOW
- L476: method `getObjectsPowerCalculationMax` - LOW
- L485: method `getObjectsPropertyMax` - LOW
- L494: method `getObjectsInObject_txt` - LOW
- L503: method `getObjectKindCount` - LOW
- L512: method `getObjectKinds` - LOW
- L522: method `getObjectBaseKindMax` - LOW
- L531: method `getObjectBases` - LOW
- L548: method `getSlays` - LOW
- L566: method `getBrands` - LOW
- L584: method `getCurses` - LOW
- L602: method `getItemObjects` - LOW
- L619: method `getActivations` - LOW
- L636: method `getEgoItems` - LOW
- L654: method `getArtifacts` - LOW
- L671: method `getObjectProperties` - LOW
- L689: method `getKindsByTvalSval` - LOW

### `middle/game/globals/registry/PlayerRegistry.java`

8 missing, 11 low

- L57: field `PY_MAX_LEVEL` - MISSING
- L58: field `PY_KNOW_LEVEL` - MISSING
- L78: field `PY_FOOD_FAINT` - MISSING
- L79: field `PY_FOOD_WEAK` - MISSING
- L80: field `PY_FOOD_HUNGRY` - MISSING
- L81: field `PY_FOOD_FULL` - MISSING
- L82: field `PY_FOOD_MAX` - MISSING
- L159: field `playerExperience` - MISSING
- L222: method `getPlayerProperties` - LOW
- L237: method `getPlayerShapes` - LOW
- L251: method `getPlayerHistoryCharts` - LOW
- L265: method `getPlayerBodies` - LOW
- L279: method `getPlayerRaces` - LOW
- L293: method `getMagicRealms` - LOW
- L307: method `getPlayerClasses` - LOW
- L366: method `getPlayerTimedEffects` - LOW
- L531: method `getMagicSpellMax` - LOW
- L538: method `getPlayerEquipmentSlotsMax` - LOW
- L545: method `getPlayerShapeMax` - LOW

### `middle/game/globals/registry/TerrainRegistry.java`

9 missing, 1 low

- L33: class `TerrainRegistry` - MISSING
- L34: field `logger` - MISSING
- L36: field `trapMax` - MISSING
- L37: field `features` - MISSING
- L38: field `trapInfo` - MISSING
- L40: method `getFeatures` - MISSING
- L44: method `setFeatures` - MISSING
- L48: method `getTrapInfo` - MISSING
- L52: method `setTrapInfo` - MISSING
- L114: method `getTrapMax` - LOW

### `middle/game/globals/registry/WorldRegistry.java`

0 missing, 6 low

- L76: method `getWorlds` - LOW
- L90: method `getProjections` - LOW
- L104: method `getQuests` - LOW
- L195: method `getQuestMax` - LOW
- L202: method `getProjMax` - LOW
- L209: method `getMaxRandDepth` - LOW

### `middle/gameinput/DefaultGameInput.java`

1 missing, 0 low

- L55: field `logger` - MISSING

### `middle/gameinput/EffectChoice.java`

4 missing, 0 low

- L20: interface `EffectChoice` - MISSING
- L22: record `Index` - MISSING
- L25: record `Random` - MISSING
- L28: record `Aborted` - MISSING

### `middle/gameinput/GameInputHolder.java`

1 missing, 1 low

- L43: constructor `GameInputHolder` - MISSING
- L67: method `getInstance` - LOW

### `middle/magic/ClassMagic.java`

0 missing, 2 low

- L84: method `isCaster` - LOW
- L186: method `getNumBooks` - LOW

### `middle/magic/MagicBook.java`

0 missing, 4 low

- L182: method `getNumOfSpells` - LOW
- L191: method `getBookName` - LOW
- L198: method `getBookTValue` - LOW
- L214: method `isDungeon` - LOW

### `middle/magic/MagicRealm.java`

0 missing, 1 low

- L77: method `getName` - LOW

### `middle/magic/MagicSpell.java`

1 missing, 0 low

- L87: method `getSpellName` - MISSING

### `middle/monsters/BlowEffect.java`

0 missing, 7 low

- L126: method `getName` - LOW
- L133: method `getPower` - LOW
- L140: method `getEval` - LOW
- L147: method `getDesc` - LOW
- L154: method `getLoreAttr` - LOW
- L161: method `getLoreAttrResist` - LOW
- L168: method `getLoreAttrImmune` - LOW

### `middle/monsters/Monster.java`

7 missing, 4 low

- L195: method `getMonsterRace` - LOW
- L225: method `getGrid` - LOW
- L232: method `getcDistance` - LOW
- L249: method `getMonTimed` - LOW
- L502: method `getHp` - MISSING
- L506: method `setHp` - MISSING
- L513: method `getMaxHp` - MISSING
- L517: method `setMaxHp` - MISSING
- L524: method `monsterFlagOn` - MISSING
- L531: method `setMonsterTracked` - MISSING
- L535: method `updateCached` - MISSING

### `middle/monsters/MonsterBase.java`

0 missing, 4 low

- L99: method `toString` - LOW
- L113: method `getCodeName` - LOW
- L120: method `getFlags` - LOW
- L127: method `getDefaultMonsterChar` - LOW

### `middle/monsters/MonsterDrop.java`

0 missing, 1 low

- L89: method `isBase` - LOW

### `middle/monsters/MonsterFlag.java`

4 missing, 0 low

- L23: class `MonsterFlag` - MISSING
- L24: field `index` - MISSING
- L25: field `type` - MISSING
- L26: field `desc` - MISSING

### `middle/monsters/MonsterFriends.java`

1 missing, 0 low

- L35: field `logger` - MISSING

### `middle/monsters/MonsterPain.java`

0 missing, 3 low

- L76: method `getMessage` - LOW - "Getter for message"
- L86: method `toString` - LOW
- L105: method `getPainIndex` - LOW

### `middle/monsters/MonsterRace.java`

0 missing, 3 low

- L380: method `getFlags` - LOW
- L397: method `getName` - LOW
- L442: method `getLevel` - LOW

### `middle/monsters/MonsterShape.java`

0 missing, 3 low

- L55: method `getName` - LOW
- L62: method `getRace` - LOW
- L69: method `getBase` - LOW

### `middle/monsters/Summon.java`

0 missing, 8 low

- L123: method `getName` - LOW
- L130: method `getMessageType` - LOW
- L137: method `isUniquesAllowed` - LOW
- L144: method `getBases` - LOW
- L151: method `getRaceFlag` - LOW
- L158: method `getFallback` - LOW
- L173: method `getDescription` - LOW
- L180: method `toString` - LOW

### `middle/monsters/enums/BlowEffectType.java`

8 missing, 1 low

- L40: method `BET_ALL_SUSTAINS` - MISSING
- L40: enum-const `BET_ELEMENT` - MISSING
- L41: enum-const `BET_FLAG` - MISSING
- L42: enum-const `BET_DRAIN` - MISSING
- L43: enum-const `BET_THEFT` - MISSING
- L44: enum-const `BET_EAT_FOOD` - MISSING
- L45: enum-const `BET_EAT_LIGHT` - MISSING
- L46: enum-const `BET_ALL_SUSTAINS` - MISSING
- L63: method `getType` - LOW

### `middle/monsters/enums/MonTimed.java`

1 missing, 0 low

- L30: method `MON_TMD_MAX` - MISSING

### `middle/monsters/enums/MonTimedFlags.java`

3 missing, 0 low

- L20: enum `MonTimedFlags` - MISSING
- L21: enum-const `MON_TMD_FLG_NOTIFY` - MISSING
- L22: enum-const `MON_TMD_FLG_NOMESSAGE` - MISSING

### `middle/monsters/enums/MonsterFlag.java`

1 missing, 0 low

- L30: method `MFLAG_MAX` - MISSING

### `middle/monsters/enums/MonsterMessage.java`

1 missing, 0 low

- L33: method `MON_MSG_MAX` - MISSING

### `middle/monsters/enums/MonsterRaceCategory.java`

15 missing, 0 low

- L30: enum-const `RFT_NONE` - MISSING
- L31: enum-const `RFT_OBV` - MISSING
- L32: enum-const `RFT_DISP` - MISSING
- L33: enum-const `RFT_GEN` - MISSING
- L34: enum-const `RFT_NOTE` - MISSING
- L35: enum-const `RFT_BEHAV` - MISSING
- L36: enum-const `RFT_DROP` - MISSING
- L37: enum-const `RFT_DET` - MISSING
- L38: enum-const `RFT_ALTER` - MISSING
- L39: enum-const `RFT_RACE_N` - MISSING
- L40: enum-const `RFT_RACE_A` - MISSING
- L41: enum-const `RFT_VULN` - MISSING
- L42: enum-const `RFT_VULN_I` - MISSING
- L43: enum-const `RFT_RES` - MISSING
- L44: enum-const `RFT_PROT` - MISSING

### `middle/monsters/enums/MonsterRaceFlag.java`

1 missing, 2 low

- L31: method `RF_NO_SLOW` - MISSING
- L140: method `getCategory` - LOW
- L147: method `getDescription` - LOW

### `middle/monsters/enums/MonsterSpell.java`

0 missing, 1 low

- L171: method `getTypes` - LOW

### `middle/monsters/enums/MonsterSpellTypeEnum.java`

0 missing, 1 low

- L85: method `isDamage` - LOW

### `middle/numerics/RandomChance.java`

0 missing, 1 low

- L39: constructor `RandomChance` - LOW - "Constructor"

### `middle/objects/Archery.java`

0 missing, 4 low

- L91: method `getAmmoType` - LOW
- L98: method `getAmmoDamage` - LOW
- L105: method `getLaunchDamage` - LOW
- L113: method `getLaunchMult` - LOW - "it back out"

### `middle/objects/Artifact.java`

2 missing, 26 low

- L87: field `weight` - LOW - "Weight."
- L92: field `cost` - LOW - "Cost/value."
- L153: field `aupInfo` - MISSING
- L228: method `getName` - LOW
- L235: method `getText` - LOW
- L242: method `gettValue` - LOW
- L249: method `getsValue` - LOW
- L256: method `getToHit` - LOW
- L263: method `getToDam` - LOW
- L270: method `getToAC` - LOW
- L277: method `getAc` - LOW
- L284: method `getDiceString` - LOW
- L291: method `getWeight` - LOW
- L298: method `getCost` - LOW
- L305: method `getFlags` - LOW
- L312: method `getModifiers` - LOW
- L319: method `getElInfo` - LOW
- L326: method `getBrands` - LOW
- L333: method `getSlays` - LOW
- L340: method `getCurses` - LOW
- L347: method `getLevel` - LOW
- L354: method `getAllocProb` - LOW
- L361: method `getAllocMin` - LOW
- L368: method `getAllocMax` - LOW
- L375: method `getActivation` - LOW
- L382: method `getActivationMessage` - LOW
- L389: method `getTime` - LOW
- L457: method `getAup` - MISSING

### `middle/objects/ArtifactSetData.java`

98 missing, 0 low

- L28: class `ArtifactSetData` - MISSING
- L30: field `hitIncrement` - MISSING
- L31: field `damIncrement` - MISSING
- L32: field `hitStartVal` - MISSING
- L33: field `damStartVal` - MISSING
- L34: field `acStartVal` - MISSING
- L35: field `acIncrement` - MISSING
- L38: field `artProbs` - MISSING
- L39: field `tvProbs` - MISSING
- L40: field `tvNum` - MISSING
- L41: field `bowTotal` - MISSING
- L42: field `meleeTotal` - MISSING
- L43: field `bootTotal` - MISSING
- L44: field `gloveTotal` - MISSING
- L45: field `headgearTotal` - MISSING
- L46: field `shieldTotal` - MISSING
- L47: field `cloakTotal` - MISSING
- L48: field `armourTotal` - MISSING
- L49: field `otherTotal` - MISSING
- L50: field `total` - MISSING
- L51: field `negPowerTotal` - MISSING
- L54: field `tvFreq` - MISSING
- L57: field `basePower` - MISSING
- L58: field `maxPower` - MISSING
- L59: field `minPower` - MISSING
- L60: field `avgPower` - MISSING
- L61: field `varPower` - MISSING
- L62: field `avgTvPower` - MISSING
- L63: field `minTvPower` - MISSING
- L64: field `maxTvPower` - MISSING
- L67: field `baseItemLevel` - MISSING
- L70: field `baseItemProb` - MISSING
- L73: field `baseArtAlloc` - MISSING
- L75: constructor `ArtifactSetData` - MISSING
- L131: method `getHitIncrement` - MISSING
- L135: method `setHitIncrement` - MISSING
- L139: method `getDamIncrement` - MISSING
- L143: method `setDamIncrement` - MISSING
- L147: method `getHitStartVal` - MISSING
- L151: method `setHitStartVal` - MISSING
- L155: method `getDamStartVal` - MISSING
- L159: method `setDamStartVal` - MISSING
- L163: method `getAcStartVal` - MISSING
- L167: method `setAcStartVal` - MISSING
- L171: method `getAcIncrement` - MISSING
- L175: method `setAcIncrement` - MISSING
- L179: method `getBowTotal` - MISSING
- L183: method `setBowTotal` - MISSING
- L187: method `getMeleeTotal` - MISSING
- L191: method `setMeleeTotal` - MISSING
- L195: method `getBootTotal` - MISSING
- L199: method `setBootTotal` - MISSING
- L203: method `getGloveTotal` - MISSING
- L207: method `setGloveTotal` - MISSING
- L211: method `getHeadgearTotal` - MISSING
- L215: method `setHeadgearTotal` - MISSING
- L219: method `getShieldTotal` - MISSING
- L223: method `setShieldTotal` - MISSING
- L227: method `getCloakTotal` - MISSING
- L231: method `setCloakTotal` - MISSING
- L235: method `getArmourTotal` - MISSING
- L239: method `setArmourTotal` - MISSING
- L243: method `getOtherTotal` - MISSING
- L247: method `setOtherTotal` - MISSING
- L251: method `getTotal` - MISSING
- L255: method `setTotal` - MISSING
- L259: method `getNegPowerTotal` - MISSING
- L263: method `setNegPowerTotal` - MISSING
- L267: method `getMaxPower` - MISSING
- L271: method `setMaxPower` - MISSING
- L275: method `getMinPower` - MISSING
- L279: method `setMinPower` - MISSING
- L283: method `getAvgPower` - MISSING
- L287: method `setAvgPower` - MISSING
- L291: method `getVarPower` - MISSING
- L295: method `setVarPower` - MISSING
- L299: method `getArtProbs` - MISSING
- L303: method `setArtProbs` - MISSING
- L307: method `getTvProbs` - MISSING
- L311: method `setTvProbs` - MISSING
- L315: method `getTvNum` - MISSING
- L319: method `setTvNum` - MISSING
- L323: method `getTvFreq` - MISSING
- L327: method `setTvFreq` - MISSING
- L331: method `getBasePower` - MISSING
- L335: method `setBasePower` - MISSING
- L339: method `getAvgTvPower` - MISSING
- L343: method `setAvgTvPower` - MISSING
- L347: method `getMinTvPower` - MISSING
- L351: method `setMinTvPower` - MISSING
- L355: method `getMaxTvPower` - MISSING
- L359: method `setMaxTvPower` - MISSING
- L363: method `getBaseItemLevel` - MISSING
- L367: method `setBaseItemLevel` - MISSING
- L371: method `getBaseItemProb` - MISSING
- L375: method `setBaseItemProb` - MISSING
- L379: method `getBaseArtAlloc` - MISSING
- L383: method `setBaseArtAlloc` - MISSING

### `middle/objects/ArtifactUpkeep.java`

0 missing, 6 low

- L53: method `isCreated` - LOW
- L60: method `setCreated` - LOW
- L67: method `isSeen` - LOW
- L74: method `setSeen` - LOW
- L81: method `isEverseen` - LOW
- L88: method `setEverseen` - LOW

### `middle/objects/Brand.java`

0 missing, 5 low

- L94: method `getName` - LOW
- L101: method `getCode` - LOW
- L108: method `toString` - LOW
- L146: method `hashCode` - LOW
- L162: method `getMultiplier` - LOW

### `middle/objects/ChestTrap.java`

0 missing, 8 low

- L110: method `getName` - LOW
- L128: method `getCode` - LOW
- L135: method `getLevel` - LOW
- L142: method `getEffect` - LOW
- L149: method `isDestroy` - LOW
- L156: method `isMagic` - LOW
- L163: method `getMessage` - LOW
- L170: method `getMessageDeath` - LOW

### `middle/objects/Curse.java`

7 missing, 14 low

- L169: field `knownCombatToHit` - MISSING
- L170: field `knownCombatToDam` - MISSING
- L171: field `knownCombatToAC` - MISSING
- L173: field `knownModifiers` - MISSING
- L175: field `knownElInfo` - MISSING
- L177: field `knownObjectFlags` - MISSING
- L179: field `knownEffect` - MISSING
- L241: method `getName` - LOW
- L248: method `getObjectBases` - LOW
- L255: method `getWeight` - LOW
- L262: method `getEffect` - LOW
- L269: method `getObjectFlags` - LOW
- L292: method `getCombatToHit` - LOW
- L299: method `getCombatDam` - LOW
- L306: method `getCombatAC` - LOW
- L321: method `getConflictFlags` - LOW
- L328: method `getDescription` - LOW
- L335: method `getMessage` - LOW
- L343: method `getConflictNames` - LOW - "resolution"
- L361: method `canAfflict` - LOW
- L368: method `toString` - LOW

### `middle/objects/CurseData.java`

0 missing, 2 low

- L76: method `getPower` - LOW
- L83: method `getTimeout` - LOW

### `middle/objects/EgoItem.java`

1 missing, 0 low

- L402: method `clearIgnoreType` - MISSING

### `middle/objects/ElementInfo.java`

0 missing, 1 low

- L70: method `getResLevel` - LOW

### `middle/objects/ElementPowers.java`

0 missing, 7 low

- L111: method `getElement` - LOW
- L118: method `getName` - LOW
- L125: method `getType` - LOW
- L132: method `getIgnorePower` - LOW
- L139: method `getVulnPower` - LOW
- L146: method `getResPower` - LOW
- L153: method `getImPower` - LOW

### `middle/objects/ElementSet.java`

0 missing, 7 low

- L110: method `getType` - LOW
- L117: method `getResLevel` - LOW
- L124: method `getFactor` - LOW
- L131: method `getBonus` - LOW
- L138: method `getSize` - LOW
- L145: method `getCount` - LOW
- L162: method `getDescription` - LOW

### `middle/objects/FlagSet.java`

0 missing, 6 low

- L98: method `getType` - LOW
- L105: method `getFactor` - LOW
- L112: method `getBonus` - LOW
- L119: method `getSize` - LOW
- L126: method `getCount` - LOW
- L143: method `getDescription` - LOW

### `middle/objects/Flavour.java`

0 missing, 6 low

- L116: method `getText` - LOW
- L124: method `getsValStr` - LOW - "for a random one"
- L132: method `getsVal` - LOW - "random flavour)"
- L139: method `getColour` - LOW
- L146: method `getIndex` - LOW
- L153: method `isFixed` - LOW

### `middle/objects/FlavourKind.java`

0 missing, 4 low

- L57: constructor `FlavourKind` - LOW
- L66: method `getValue` - LOW
- L73: method `getGlyph` - LOW
- L80: method `getFlavours` - LOW

### `middle/objects/Grouper.java`

0 missing, 1 low

- L45: constructor `Grouper` - LOW - "Constructor"

### `middle/objects/ItemObject.java`

4 missing, 26 low

- L311: field `owningPile` - MISSING
- L528: method `getGrid` - LOW
- L545: method `isArtifact` - LOW
- L552: method `getKind` - LOW
- L1113: method `getNumber` - LOW
- L1120: method `gettValue` - LOW
- L1127: method `getTimeout` - LOW
- L1468: method `getsValue` - LOW
- L1475: method `setsValue` - LOW
- L1482: method `getWeight` - LOW
- L1489: method `setWeight` - LOW
- L1508: method `settValue` - LOW
- L1515: method `setNumber` - LOW
- L1522: method `getDamageDice` - LOW
- L1539: method `getDamageSides` - LOW
- L1546: method `setDamageSides` - LOW
- L1553: method `getBaseAC` - LOW
- L1583: method `getpValue` - LOW
- L1590: method `setpValue` - LOW
- L1597: method `setToAC` - LOW
- L1604: method `setToDam` - LOW
- L1630: method `setModifiers` - LOW
- L1658: method `setElInfo` - LOW
- L1768: method `setEffect` - LOW
- L1792: method `setSlays` - LOW
- L1813: method `setEgo` - LOW
- L2242: method `isEgo` - LOW - "{@code obj->ego}"
- L5429: method `setCurses` - MISSING
- L5462: method `getOwningPile` - MISSING
- L5466: method `setOwningPile` - MISSING

### `middle/objects/KnownObject.java`

1 missing, 8 low

- L88: field `noticeFlags` - MISSING
- L228: method `toHIsKnown` - LOW
- L247: method `toDIsKnown` - LOW
- L265: method `toAIsKnown` - LOW
- L524: method `getDd` - LOW
- L532: method `getDs` - LOW
- L540: method `getToH` - LOW
- L548: method `getToD` - LOW
- L575: method `getToA` - LOW

### `middle/objects/ObjectBase.java`

2 missing, 9 low

- L66: field `flags` - MISSING
- L127: method `getNumSvals` - LOW
- L144: method `getName` - LOW
- L151: method `getElementMap` - LOW
- L158: method `gettVal` - LOW
- L165: method `getAttr` - LOW
- L172: method `getBreakPerc` - LOW
- L179: method `getMaxStack` - LOW
- L186: method `getKindFlags` - LOW
- L190: method `getFlags` - MISSING
- L197: method `toString` - LOW

### `middle/objects/ObjectGear.java`

2 missing, 0 low

- L41: class `ObjectGear` - MISSING
- L42: field `logger` - MISSING

### `middle/objects/ObjectIgnore.java`

1 missing, 0 low

- L65: field `logger` - MISSING

### `middle/objects/ObjectInfo.java`

3 missing, 0 low

- L27: class `ObjectInfo` - MISSING
- L69: field `ignoreLevel` - MISSING
- L78: record `QualityMapping` - MISSING

### `middle/objects/ObjectKind.java`

0 missing, 18 low

- L538: method `getsVal` - LOW
- L554: method `getName` - LOW
- L561: method `getBase` - LOW
- L568: method `setAlloc_prob` - LOW
- L575: method `setAlloc_min` - LOW
- L582: method `setAlloc_max` - LOW
- L589: method `setCost` - LOW
- L596: method `setWeight` - LOW
- L603: method `getActivations` - LOW
- L610: method `setTime` - LOW
- L617: method `gettValue` - LOW
- L624: method `getsValueName` - LOW
- L631: method `getKindFlags` - LOW
- L638: method `getKindIndex` - LOW
- L645: method `setKindIndex` - LOW
- L685: method `getAc` - LOW
- L742: method `getEffect` - LOW
- L1058: method `getWeight` - LOW

### `middle/objects/ObjectKnowledge.java`

2 missing, 0 low

- L26: class `ObjectKnowledge` - MISSING
- L27: field `logger` - MISSING

### `middle/objects/ObjectMake.java`

2 missing, 0 low

- L23: class `ObjectMake` - MISSING
- L24: method `makeFakeArtifact` - MISSING

### `middle/objects/ObjectName.java`

3 missing, 0 low

- L26: class `ObjectName` - MISSING
- L28: field `nameSections` - MISSING
- L30: method `randnameMake` - MISSING

### `middle/objects/ObjectProperty.java`

0 missing, 3 low

- L152: method `getType` - LOW
- L167: method `getPayload` - LOW
- L174: method `getName` - LOW

### `middle/objects/ObjectRandart.java`

4 missing, 0 low

- L27: class `ObjectRandart` - MISSING
- L29: method `doRandart` - MISSING
- L34: method `storeBasePower` - MISSING
- L39: method `artifactPower` - MISSING

### `middle/objects/ObjectUtils.java`

4 missing, 0 low

- L75: field `logger` - MISSING
- L77: field `MAX_TITLES` - MISSING
- L78: field `maxTitleLength` - MISSING
- L80: field `scrollAdj` - MISSING

### `middle/objects/Pile.java`

6 missing, 0 low

- L239: method `insertEnd` - MISSING
- L316: method `size` - MISSING
- L320: method `get` - MISSING
- L324: method `reversed` - MISSING
- L328: method `removeIf` - MISSING
- L332: method `remove` - MISSING

### `middle/objects/Rune.java`

1 missing, 2 low

- L49: field `logger` - MISSING
- L219: method `getNote` - LOW
- L236: method `getVariety` - LOW

### `middle/objects/Slay.java`

1 missing, 3 low

- L112: method `getCode` - LOW
- L119: method `getName` - LOW
- L126: method `toString` - LOW
- L180: method `copy` - MISSING

### `middle/objects/enums/ArtifactIndex.java`

98 missing, 0 low

- L20: enum `ArtifactIndex` - MISSING
- L21: method `ART_IDX_TOTAL` - MISSING
- L21: enum-const `ART_IDX_BOW_SHOTS` - MISSING
- L22: enum-const `ART_IDX_BOW_MIGHT` - MISSING
- L23: enum-const `ART_IDX_BOW_BRAND` - MISSING
- L24: enum-const `ART_IDX_BOW_SLAY` - MISSING
- L25: enum-const `ART_IDX_WEAPON_HIT` - MISSING
- L26: enum-const `ART_IDX_WEAPON_DAM` - MISSING
- L27: enum-const `ART_IDX_NONWEAPON_HIT` - MISSING
- L28: enum-const `ART_IDX_NONWEAPON_DAM` - MISSING
- L29: enum-const `ART_IDX_NONWEAPON_HIT_DAM` - MISSING
- L30: enum-const `ART_IDX_NONWEAPON_BRAND` - MISSING
- L31: enum-const `ART_IDX_NONWEAPON_SLAY` - MISSING
- L32: enum-const `ART_IDX_NONWEAPON_BLOWS` - MISSING
- L33: enum-const `ART_IDX_NONWEAPON_SHOTS` - MISSING
- L34: enum-const `ART_IDX_MELEE_BLESS` - MISSING
- L35: enum-const `ART_IDX_MELEE_BRAND` - MISSING
- L36: enum-const `ART_IDX_MELEE_SLAY` - MISSING
- L37: enum-const `ART_IDX_MELEE_SINV` - MISSING
- L38: enum-const `ART_IDX_MELEE_BLOWS` - MISSING
- L39: enum-const `ART_IDX_MELEE_AC` - MISSING
- L40: enum-const `ART_IDX_MELEE_DICE` - MISSING
- L41: enum-const `ART_IDX_MELEE_WEIGHT` - MISSING
- L42: enum-const `ART_IDX_MELEE_TUNN` - MISSING
- L43: enum-const `ART_IDX_ALLARMOR_WEIGHT` - MISSING
- L44: enum-const `ART_IDX_BOOT_AC` - MISSING
- L45: enum-const `ART_IDX_BOOT_FEATHER` - MISSING
- L46: enum-const `ART_IDX_BOOT_STEALTH` - MISSING
- L47: enum-const `ART_IDX_BOOT_TRAP_IMM` - MISSING
- L48: enum-const `ART_IDX_BOOT_SPEED` - MISSING
- L49: enum-const `ART_IDX_BOOT_MOVES` - MISSING
- L50: enum-const `ART_IDX_GLOVE_AC` - MISSING
- L51: enum-const `ART_IDX_GLOVE_FA` - MISSING
- L52: enum-const `ART_IDX_GLOVE_DEX` - MISSING
- L53: enum-const `ART_IDX_GLOVE_HIT_DAM` - MISSING
- L54: enum-const `ART_IDX_HELM_AC` - MISSING
- L55: enum-const `ART_IDX_HELM_RBLIND` - MISSING
- L56: enum-const `ART_IDX_HELM_ESP` - MISSING
- L57: enum-const `ART_IDX_HELM_SINV` - MISSING
- L58: enum-const `ART_IDX_HELM_WIS` - MISSING
- L59: enum-const `ART_IDX_HELM_INT` - MISSING
- L60: enum-const `ART_IDX_SHIELD_AC` - MISSING
- L61: enum-const `ART_IDX_SHIELD_LRES` - MISSING
- L62: enum-const `ART_IDX_CLOAK_AC` - MISSING
- L63: enum-const `ART_IDX_CLOAK_STEALTH` - MISSING
- L64: enum-const `ART_IDX_ARMOR_AC` - MISSING
- L65: enum-const `ART_IDX_ARMOR_STEALTH` - MISSING
- L66: enum-const `ART_IDX_ARMOR_HLIFE` - MISSING
- L67: enum-const `ART_IDX_ARMOR_CON` - MISSING
- L68: enum-const `ART_IDX_ARMOR_LRES` - MISSING
- L69: enum-const `ART_IDX_ARMOR_ALLRES` - MISSING
- L70: enum-const `ART_IDX_ARMOR_HRES` - MISSING
- L71: enum-const `ART_IDX_GEN_STAT` - MISSING
- L72: enum-const `ART_IDX_GEN_SUST` - MISSING
- L73: enum-const `ART_IDX_GEN_STEALTH` - MISSING
- L74: enum-const `ART_IDX_GEN_SEARCH` - MISSING
- L75: enum-const `ART_IDX_GEN_INFRA` - MISSING
- L76: enum-const `ART_IDX_GEN_SPEED` - MISSING
- L77: enum-const `ART_IDX_GEN_IMMUNE` - MISSING
- L78: enum-const `ART_IDX_GEN_FA` - MISSING
- L79: enum-const `ART_IDX_GEN_HLIFE` - MISSING
- L80: enum-const `ART_IDX_GEN_FEATHER` - MISSING
- L81: enum-const `ART_IDX_GEN_LIGHT` - MISSING
- L82: enum-const `ART_IDX_GEN_SINV` - MISSING
- L83: enum-const `ART_IDX_GEN_ESP` - MISSING
- L84: enum-const `ART_IDX_GEN_SDIG` - MISSING
- L85: enum-const `ART_IDX_GEN_REGEN` - MISSING
- L86: enum-const `ART_IDX_GEN_LRES` - MISSING
- L87: enum-const `ART_IDX_GEN_RPOIS` - MISSING
- L88: enum-const `ART_IDX_GEN_RFEAR` - MISSING
- L89: enum-const `ART_IDX_GEN_RLIGHT` - MISSING
- L90: enum-const `ART_IDX_GEN_RDARK` - MISSING
- L91: enum-const `ART_IDX_GEN_RBLIND` - MISSING
- L92: enum-const `ART_IDX_GEN_RCONF` - MISSING
- L93: enum-const `ART_IDX_GEN_RSOUND` - MISSING
- L94: enum-const `ART_IDX_GEN_RSHARD` - MISSING
- L95: enum-const `ART_IDX_GEN_RNEXUS` - MISSING
- L96: enum-const `ART_IDX_GEN_RNETHER` - MISSING
- L97: enum-const `ART_IDX_GEN_RCHAOS` - MISSING
- L98: enum-const `ART_IDX_GEN_RDISEN` - MISSING
- L99: enum-const `ART_IDX_GEN_AC` - MISSING
- L100: enum-const `ART_IDX_GEN_TUNN` - MISSING
- L101: enum-const `ART_IDX_GEN_ACTIV` - MISSING
- L102: enum-const `ART_IDX_GEN_PSTUN` - MISSING
- L103: enum-const `ART_IDX_GEN_DAM_RED` - MISSING
- L104: enum-const `ART_IDX_GEN_MOVES` - MISSING
- L105: enum-const `ART_IDX_GEN_TRAP_IMM` - MISSING
- L106: enum-const `ART_IDX_WEAPON_AGGR` - MISSING
- L107: enum-const `ART_IDX_NONWEAPON_AGGR` - MISSING
- L108: enum-const `ART_IDX_MELEE_DICE_SUPER` - MISSING
- L109: enum-const `ART_IDX_BOW_SHOTS_SUPER` - MISSING
- L110: enum-const `ART_IDX_BOW_MIGHT_SUPER` - MISSING
- L111: enum-const `ART_IDX_GEN_SPEED_SUPER` - MISSING
- L112: enum-const `ART_IDX_MELEE_BLOWS_SUPER` - MISSING
- L113: enum-const `ART_IDX_MELEE_AC_SUPER` - MISSING
- L114: enum-const `ART_IDX_GEN_AC_SUPER` - MISSING
- L115: enum-const `ART_IDX_TOTAL` - MISSING
- L117: field `value` - MISSING

### `middle/objects/enums/ChestTrapCode.java`

12 missing, 0 low

- L20: enum `ChestTrapCode` - MISSING
- L21: enum-const `NO_TRAP` - MISSING
- L22: enum-const `POISON` - MISSING
- L23: enum-const `LOSE_STR` - MISSING
- L24: enum-const `LOSE_CON` - MISSING
- L25: enum-const `SUMMON` - MISSING
- L26: enum-const `PARALYZE` - MISSING
- L27: enum-const `EXPLODE` - MISSING
- L29: field `MAX_TRAPS` - MISSING
- L30: field `pval` - MISSING
- L36: method `getMaxTraps` - MISSING
- L40: method `getPval` - MISSING

### `middle/objects/enums/CombatRunes.java`

5 missing, 1 low

- L43: method `COMBAT_RUNE_MAX` - MISSING
- L43: enum-const `COMBAT_RUNE_TO_A` - MISSING
- L44: enum-const `COMBAT_RUNE_TO_H` - MISSING
- L45: enum-const `COMBAT_RUNE_TO_D` - MISSING
- L46: enum-const `COMBAT_RUNE_MAX` - MISSING
- L66: method `getDescription` - LOW

### `middle/objects/enums/GetItemFlags.java`

12 missing, 0 low

- L20: enum `GetItemFlags` - MISSING
- L21: enum-const `USE_EQUIP` - MISSING
- L22: enum-const `USE_INVEN` - MISSING
- L23: enum-const `USE_FLOOR` - MISSING
- L24: enum-const `USE_QUIVER` - MISSING
- L25: enum-const `IS_HARMLESS` - MISSING
- L26: enum-const `SHOW_PRICES` - MISSING
- L27: enum-const `SHOW_FAIL` - MISSING
- L28: enum-const `SHOW_QUIVER` - MISSING
- L29: enum-const `SHOW_EMPTY` - MISSING
- L30: enum-const `QUIVER_TAGS` - MISSING
- L31: enum-const `SHOW_RECHARGE` - MISSING

### `middle/objects/enums/IgnoreType.java`

32 missing, 0 low

- L20: enum `IgnoreType` - MISSING
- L21: method `ITYPE_MAX` - MISSING
- L21: enum-const `ITYPE_NONE` - MISSING
- L22: enum-const `ITYPE_SHARP` - MISSING
- L23: enum-const `ITYPE_BLUNT` - MISSING
- L24: enum-const `ITYPE_GREAT` - MISSING
- L25: enum-const `ITYPE_SLING` - MISSING
- L26: enum-const `ITYPE_BOW` - MISSING
- L27: enum-const `ITYPE_CROSSBOW` - MISSING
- L28: enum-const `ITYPE_SHOT` - MISSING
- L29: enum-const `ITYPE_ARROW` - MISSING
- L30: enum-const `ITYPE_BOLT` - MISSING
- L31: enum-const `ITYPE_ROBE` - MISSING
- L32: enum-const `ITYPE_BODY_ARMOR` - MISSING
- L33: enum-const `ITYPE_BASIC_DRAGON_ARMOR` - MISSING
- L34: enum-const `ITYPE_MULTI_DRAGON_ARMOR` - MISSING
- L35: enum-const `ITYPE_HIGH_DRAGON_ARMOR` - MISSING
- L36: enum-const `ITYPE_BALANCE_DRAGON_ARMOR` - MISSING
- L37: enum-const `ITYPE_POWER_DRAGON_ARMOR` - MISSING
- L38: enum-const `ITYPE_CLOAK` - MISSING
- L39: enum-const `ITYPE_ELVEN_CLOAK` - MISSING
- L40: enum-const `ITYPE_SHIELD` - MISSING
- L41: enum-const `ITYPE_HEADGEAR` - MISSING
- L42: enum-const `ITYPE_HANDGEAR` - MISSING
- L43: enum-const `ITYPE_FEET` - MISSING
- L44: enum-const `ITYPE_DIGGER` - MISSING
- L45: enum-const `ITYPE_RING` - MISSING
- L46: enum-const `ITYPE_AMULET` - MISSING
- L47: enum-const `ITYPE_LIGHT` - MISSING
- L48: enum-const `ITYPE_MAX` - MISSING
- L50: field `name` - MISSING
- L56: method `getName` - MISSING

### `middle/objects/enums/IgnoreTypeEnum.java`

6 missing, 0 low

- L20: enum `IgnoreTypeEnum` - MISSING
- L21: enum-const `IGNORE_NONE` - MISSING
- L22: enum-const `IGNORE_BAD` - MISSING
- L23: enum-const `IGNORE_AVERAGE` - MISSING
- L24: enum-const `IGNORE_GOOD` - MISSING
- L25: enum-const `IGNORE_ALL` - MISSING

### `middle/objects/enums/ObjPropertyType.java`

10 missing, 1 low

- L44: method `OBJ_PROPERTY_MAX` - MISSING
- L44: enum-const `OBJ_PROPERTY_NONE` - MISSING
- L45: enum-const `OBJ_PROPERTY_STAT` - MISSING
- L46: enum-const `OBJ_PROPERTY_MOD` - MISSING
- L47: enum-const `OBJ_PROPERTY_FLAG` - MISSING
- L48: enum-const `OBJ_PROPERTY_IGNORE` - MISSING
- L49: enum-const `OBJ_PROPERTY_RESIST` - MISSING
- L50: enum-const `OBJ_PROPERTY_VULN` - MISSING
- L51: enum-const `OBJ_PROPERTY_IMM` - MISSING
- L52: enum-const `OBJ_PROPERTY_MAX` - MISSING
- L71: method `getValue` - LOW

### `middle/objects/enums/ObjectDescription.java`

12 missing, 0 low

- L20: enum `ObjectDescription` - MISSING
- L21: enum-const `ODESC_BASE` - MISSING
- L22: enum-const `ODESC_COMBAT` - MISSING
- L23: enum-const `ODESC_EXTRA` - MISSING
- L24: enum-const `ODESC_STORE` - MISSING
- L25: enum-const `ODESC_PLURAL` - MISSING
- L26: enum-const `ODESC_SINGULAR` - MISSING
- L27: enum-const `ODESC_SPOIL` - MISSING
- L28: enum-const `ODESC_PREFIX` - MISSING
- L29: enum-const `ODESC_CAPITAL` - MISSING
- L30: enum-const `ODESC_TERSE` - MISSING
- L31: enum-const `ODESC_NOEGO` - MISSING

### `middle/objects/enums/ObjectFlag.java`

1 missing, 1 low

- L32: method `OF_MAX` - MISSING
- L91: method `getFlag` - LOW

### `middle/objects/enums/ObjectFlagID.java`

0 missing, 1 low

- L69: method `getIdMethod` - LOW

### `middle/objects/enums/ObjectFlagType.java`

12 missing, 1 low

- L44: method `OFT_MAX` - MISSING
- L44: enum-const `OFT_NONE` - MISSING
- L45: enum-const `OFT_SUST` - MISSING
- L46: enum-const `OFT_PROT` - MISSING
- L47: enum-const `OFT_MISC` - MISSING
- L48: enum-const `OFT_LIGHT` - MISSING
- L49: enum-const `OFT_MELEE` - MISSING
- L50: enum-const `OFT_BAD` - MISSING
- L51: enum-const `OFT_DIG` - MISSING
- L52: enum-const `OFT_THROW` - MISSING
- L53: enum-const `OFT_CURSE_ONLY` - MISSING
- L54: enum-const `OFT_MAX` - MISSING
- L74: method `getSubtypeText` - LOW

### `middle/objects/enums/ObjectKindFlag.java`

1 missing, 1 low

- L33: method `KF_MAX` - MISSING
- L75: method `getFlag` - LOW

### `middle/objects/enums/ObjectOriginEnum.java`

1 missing, 0 low

- L31: method `ORIGIN_MAX` - MISSING

### `middle/objects/enums/QualityValueEnum.java`

6 missing, 0 low

- L20: enum `QualityValueEnum` - MISSING
- L21: enum-const `IGNORE_NONE` - MISSING
- L22: enum-const `IGNORE_BAD` - MISSING
- L23: enum-const `IGNORE_AVERAGE` - MISSING
- L24: enum-const `IGNORE_GOOD` - MISSING
- L25: enum-const `IGNORE_ALL` - MISSING

### `middle/objects/enums/ResType.java`

2 missing, 0 low

- L20: enum `ResType` - MISSING
- L21: enum-const `T_LRES` - MISSING

### `middle/objects/enums/RuneGroup.java`

8 missing, 1 low

- L40: method `OTHER` - MISSING
- L40: enum-const `COMBAT` - MISSING
- L41: enum-const `MODIFIERS` - MISSING
- L42: enum-const `RESIST` - MISSING
- L43: enum-const `BRAND` - MISSING
- L44: enum-const `SLAY` - MISSING
- L45: enum-const `CURSE` - MISSING
- L46: enum-const `OTHER` - MISSING
- L65: method `getName` - LOW

### `middle/objects/enums/RuneVariety.java`

21 missing, 1 low

- L55: method `group` - LOW
- L132: method `group` - MISSING
- L136: method `runeName` - MISSING
- L141: method `runeDesc` - MISSING
- L171: method `group` - MISSING
- L175: method `runeName` - MISSING
- L180: method `runeDesc` - MISSING
- L209: method `group` - MISSING
- L213: method `runeName` - MISSING
- L218: method `runeDesc` - MISSING
- L242: method `group` - MISSING
- L246: method `runeName` - MISSING
- L251: method `runeDesc` - MISSING
- L274: method `group` - MISSING
- L278: method `runeName` - MISSING
- L283: method `runeDesc` - MISSING
- L303: method `group` - MISSING
- L307: method `runeName` - MISSING
- L312: method `runeDesc` - MISSING
- L332: method `group` - MISSING
- L336: method `runeName` - MISSING
- L341: method `runeDesc` - MISSING

### `middle/objects/enums/TValue.java`

0 missing, 34 low

- L70: enum-const `TV_BOW` - LOW - "A bow"
- L256: method `isStaff` - LOW
- L263: method `isWand` - LOW
- L270: method `isRod` - LOW
- L277: method `isPotion` - LOW
- L284: method `isScroll` - LOW
- L291: method `isFood` - LOW
- L298: method `isMushroom` - LOW
- L305: method `isLight` - LOW
- L312: method `isRing` - LOW
- L319: method `isChest` - LOW
- L326: method `isFuel` - LOW
- L333: method `isMoney` - LOW
- L340: method `isDigger` - LOW
- L347: method `canHaveNourishment` - LOW
- L355: method `canHaveCharges` - LOW
- L362: method `canHaveTimeout` - LOW
- L369: method `isBodyArmour` - LOW
- L379: method `isHeadArmour` - LOW
- L386: method `isAmmo` - LOW
- L396: method `isSharpMissile` - LOW
- L406: method `isBolt` - LOW
- L413: method `isLauncher` - LOW
- L420: method `isUseable` - LOW
- L430: method `canHaveFailure` - LOW
- L461: method `isWeapon` - LOW
- L472: method `isArmour` - LOW
- L483: method `isMeleeWeapon` - LOW
- L493: method `hasVariablePower` - LOW
- L505: method `isWearable` - LOW
- L517: method `isEdible` - LOW
- L536: method `isBook` - LOW
- L546: method `isZapper` - LOW
- L696: method `isJewellery` - LOW

### `middle/player/EquipSlot.java`

0 missing, 3 low

- L55: method `getItem` - LOW
- L62: method `getType` - LOW
- L69: method `getName` - LOW

### `middle/player/Player.java`

4 missing, 19 low

- L431: method `wipe` - MISSING
- L565: method `getCurSp` - LOW
- L698: method `getCave` - LOW
- L705: method `getPlayerUpkeep` - LOW
- L712: method `getPlayerBody` - LOW
- L753: method `hasObjectFlag` - LOW
- L930: method `getPlayerClass` - LOW
- L937: method `getEnergy` - LOW
- L953: method `isDead` - LOW
- L998: method `getPlayerState` - LOW
- L1024: method `getGrid` - LOW
- L1031: method `getDepth` - LOW
- L1038: method `getCurrentHP` - LOW
- L1280: method `getMaxHP` - LOW
- L1295: method `getWordRecall` - LOW
- L1321: method `getRecallDepth` - LOW
- L1328: method `getDeepDescent` - LOW
- L1342: method `getMaxDepth` - LOW
- L1349: method `getGear` - LOW
- L1356: method `getPlayerOptions` - LOW
- L2836: method `setExp` - MISSING
- L2843: method `getMaxExp` - MISSING
- L2847: method `setMaxExp` - MISSING

### `middle/player/PlayerAbility.java`

9 missing, 0 low

- L24: class `PlayerAbility` - MISSING
- L25: field `indexPlayerFlag` - MISSING
- L26: field `indexObjectFlag` - MISSING
- L27: field `indexElementEnum` - MISSING
- L29: field `type` - MISSING
- L30: field `name` - MISSING
- L31: field `desc` - MISSING
- L32: field `group` - MISSING
- L33: field `value` - MISSING

### `middle/player/PlayerBirth.java`

2 missing, 0 low

- L76: field `logger` - MISSING
- L78: field `MAX_BIRTH_POINTS` - MISSING

### `middle/player/PlayerBody.java`

0 missing, 4 low

- L158: method `getName` - LOW
- L165: method `getCount` - LOW
- L172: method `getSlots` - LOW
- L180: method `getSlot` - LOW

### `middle/player/PlayerCalcs.java`

1 missing, 0 low

- L83: field `logger` - MISSING

### `middle/player/PlayerClass.java`

2 missing, 1 low

- L158: method `getMagic` - LOW
- L453: method `getTitle` - MISSING
- L468: method `getNoTitles` - MISSING

### `middle/player/PlayerHistoryChart.java`

0 missing, 3 low

- L104: method `getChartNumber` - LOW
- L111: method `getSuccessorNumber` - LOW
- L118: method `getSuccessor` - LOW

### `middle/player/PlayerHistoryEntry.java`

0 missing, 2 low

- L66: method `getText` - LOW
- L73: method `getRoll` - LOW

### `middle/player/PlayerKnowledge.java`

2 missing, 0 low

- L87: field `logger` - MISSING
- L1885: method `objectLearnOnWield` - MISSING

### `middle/player/PlayerMagic.java`

1 missing, 0 low

- L20: class `PlayerMagic` - MISSING

### `middle/player/PlayerName.java`

2 missing, 0 low

- L24: class `PlayerName` - MISSING
- L25: field `logger` - MISSING

### `middle/player/PlayerOptions.java`

3 missing, 0 low

- L52: field `logger` - MISSING
- L87: constructor `PlayerOptions` - MISSING
- L104: method `initDefaults` - MISSING

### `middle/player/PlayerProperty.java`

0 missing, 7 low

- L111: method `getPlayerPropertyType` - LOW
- L118: method `getpCode` - LOW
- L125: method `getoCode` - LOW
- L132: method `getEntries` - LOW
- L139: method `getName` - LOW
- L146: method `getDescription` - LOW
- L153: method `getValue` - LOW

### `middle/player/PlayerShape.java`

0 missing, 9 low

- L172: method `getName` - LOW
- L179: method `getToAc` - LOW
- L186: method `getToHit` - LOW
- L193: method `getToDam` - LOW
- L200: method `getSkills` - LOW
- L207: method `getFlags` - LOW
- L214: method `getPflags` - LOW
- L237: method `getEffect` - LOW
- L252: method `getPlayerBlow` - LOW

### `middle/player/PlayerState.java`

1 missing, 17 low

- L225: method `hasOFlag` - LOW
- L255: method `getSpeed` - LOW
- L262: method `perDamRed` - LOW
- L269: method `setSpeed` - LOW
- L276: method `setNumBlows` - LOW
- L375: method `getStatAdd` - LOW
- L410: method `setDamRed` - LOW
- L459: method `setCurLight` - LOW
- L601: method `getNumShots` - LOW
- L608: method `setNumShots` - LOW
- L623: method `isHeavyWield` - LOW
- L646: method `setNumMoves` - LOW
- L653: method `isHeavyShoot` - LOW
- L668: method `getAmmoMult` - LOW
- L675: method `setAmmoMult` - LOW
- L690: method `setBaseAc` - LOW
- L713: method `getResLevel` - LOW
- L908: method `updateLightLevel` - MISSING

### `middle/player/PlayerTimed.java`

1 missing, 0 low

- L82: field `logger` - MISSING

### `middle/player/PlayerTimedEffect.java`

0 missing, 17 low

- L161: method `getName` - LOW
- L200: method `getDescription` - LOW
- L207: method `getOnEnd` - LOW
- L214: method `getOnIncrease` - LOW
- L221: method `getOnDecrease` - LOW
- L228: method `getMsgT` - LOW
- L235: method `getFail` - LOW
- L242: method `getGrade` - LOW
- L249: method `getOnBeginEffect` - LOW
- L256: method `getOnEndEffect` - LOW
- L263: method `isNonStacking` - LOW
- L270: method `getLowerBound` - LOW
- L277: method `getoFlagDup` - LOW
- L284: method `isoFlagExactlySyn` - LOW
- L291: method `getTempResist` - LOW
- L298: method `getTempBrand` - LOW
- L305: method `getTempSlay` - LOW

### `middle/player/PlayerUpkeep.java`

2 missing, 4 low

- L270: method `getPile` - LOW
- L286: method `getCommand_wrk` - LOW
- L372: method `isPlaying` - LOW
- L531: method `getRestingCounter` - LOW
- L965: method `setHealthWho` - MISSING
- L972: method `setRestingCounter` - MISSING

### `middle/player/Quest.java`

0 missing, 6 low

- L79: method `getIndex` - LOW
- L86: method `getName` - LOW
- L93: method `getLevel` - LOW
- L100: method `getRace` - LOW
- L107: method `getCurrentNumber` - LOW
- L114: method `getMaxNumber` - LOW

### `middle/player/StartItem.java`

0 missing, 5 low

- L74: method `gettValue` - LOW
- L81: method `getsValue` - LOW
- L88: method `getMin` - LOW
- L95: method `getMax` - LOW
- L102: method `geteOpts` - LOW

### `middle/player/TimedFailure.java`

1 missing, 0 low

- L250: method `getCode` - MISSING

### `middle/player/enums/PlayerHistoryType.java`

16 missing, 0 low

- L20: enum `PlayerHistoryType` - MISSING
- L22: method `HIST_MAX` - MISSING
- L22: enum-const `HIST_NONE` - MISSING
- L23: enum-const `HIST_PLAYER_BIRTH` - MISSING
- L24: enum-const `HIST_ARTIFACT_UNKNOWN` - MISSING
- L25: enum-const `HIST_ARTIFACT_KNOWN` - MISSING
- L26: enum-const `HIST_ARTIFACT_LOST` - MISSING
- L27: enum-const `HIST_PLAYER_DEATH` - MISSING
- L28: enum-const `HIST_SLAY_UNIQUE` - MISSING
- L29: enum-const `HIST_USER_INPUT` - MISSING
- L30: enum-const `HIST_SAVEFILE_IMPORT` - MISSING
- L31: enum-const `HIST_GAIN_LEVEL` - MISSING
- L32: enum-const `HIST_GENERIC` - MISSING
- L33: enum-const `HIST_MAX` - MISSING
- L35: field `description` - MISSING
- L41: method `getDescription` - MISSING

### `middle/player/enums/PlayerOptionEnum.java`

5 missing, 0 low

- L38: method `OP_birth_percent_damage` - MISSING
- L153: method `getDescription` - MISSING
- L157: method `getPlayerOptionType` - MISSING
- L161: method `isNormal` - MISSING
- L165: method `isCheat` - MISSING

### `middle/player/enums/PlayerOptionTypes.java`

2 missing, 0 low

- L52: field `name` - MISSING
- L58: method `getName` - MISSING

### `middle/player/enums/PlayerOverExertion.java`

9 missing, 0 low

- L20: enum `PlayerOverExertion` - MISSING
- L21: enum-const `PY_EXERT_NONE` - MISSING
- L22: enum-const `PY_EXERT_CON` - MISSING
- L23: enum-const `PY_EXERT_FAINT` - MISSING
- L24: enum-const `PY_EXERT_SCRAMBLE` - MISSING
- L25: enum-const `PY_EXERT_CUT` - MISSING
- L26: enum-const `PY_EXERT_CONF` - MISSING
- L27: enum-const `PY_EXERT_HALLU` - MISSING
- L28: enum-const `PY_EXERT_SLOW` - MISSING

### `middle/strings/MessageTag.java`

10 missing, 0 low

- L20: enum `MessageTag` - MISSING
- L21: method `MSG_TAG_VERB_IS` - MISSING
- L21: enum-const `MSG_TAG_NONE` - MISSING
- L22: enum-const `MSG_TAG_NAME` - MISSING
- L23: enum-const `MSG_TAG_KIND` - MISSING
- L24: enum-const `MSG_TAG_VERB` - MISSING
- L25: enum-const `MSG_TAG_VERB_IS` - MISSING
- L27: field `size` - MISSING
- L33: method `getTag` - MISSING
- L46: method `getSize` - MISSING

### `middle/strings/Quark.java`

1 missing, 1 low

- L145: method `getName` - LOW
- L171: method `containsText` - MISSING

### `middle/strings/TextBlock.java`

0 missing, 1 low

- L52: constructor `TextBlock` - LOW - "Constructor"

### `middle/utils/NumberUtils.java`

1 missing, 0 low

- L216: method `cmp` - MISSING

### `middle/utils/quit/Quit.java`

0 missing, 1 low

- L87: constructor `GameQuitException` - LOW

### `unused/EventDataBirthStage.java`

0 missing, 7 low

- L92: method `isReset` - LOW
- L99: method `getHint` - LOW
- L106: method `getnChoices` - LOW
- L113: method `getInitialChoice` - LOW
- L120: method `getChoices` - LOW
- L127: method `getHelpTexts` - LOW
- L134: method `getXtra` - LOW

### `unused/Rect.java`

0 missing, 4 low

- L64: method `getLeft` - LOW
- L71: method `getTop` - LOW
- L78: method `getRight` - LOW
- L85: method `getBottom` - LOW

