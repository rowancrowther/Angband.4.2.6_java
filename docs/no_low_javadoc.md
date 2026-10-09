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

Grouped by file, sorted by total findings (most work first; ties broken by more MISSING, then path). Each entry is
`line: kind name - STATUS` and, for LOW entries, the first line of
the existing doc comment for context.

### `middle/objects/enums/QualityValueEnum.java`

6 missing, 0 low

- L20: enum `QualityValueEnum` - MISSING
- L21: enum-const `IGNORE_NONE` - MISSING
- L22: enum-const `IGNORE_BAD` - MISSING
- L23: enum-const `IGNORE_AVERAGE` - MISSING
- L24: enum-const `IGNORE_GOOD` - MISSING
- L25: enum-const `IGNORE_ALL` - MISSING

### `middle/objects/enums/CombatRunes.java`

5 missing, 1 low

- L43: method `COMBAT_RUNE_MAX` - MISSING
- L43: enum-const `COMBAT_RUNE_TO_A` - MISSING
- L44: enum-const `COMBAT_RUNE_TO_H` - MISSING
- L45: enum-const `COMBAT_RUNE_TO_D` - MISSING
- L46: enum-const `COMBAT_RUNE_MAX` - MISSING
- L66: method `getDescription` - LOW

### `middle/player/PlayerUpkeep.java`

2 missing, 4 low

- L270: method `getPile` - LOW
- L286: method `getCommand_wrk` - LOW
- L372: method `isPlaying` - LOW
- L531: method `getRestingCounter` - LOW
- L965: method `setHealthWho` - MISSING
- L972: method `setRestingCounter` - MISSING

### `middle/game/globals/registry/WorldRegistry.java`

0 missing, 6 low

- L76: method `getWorlds` - LOW
- L90: method `getProjections` - LOW
- L104: method `getQuests` - LOW
- L195: method `getQuestMax` - LOW
- L202: method `getProjMax` - LOW
- L209: method `getMaxRandDepth` - LOW

### `middle/objects/ArtifactUpkeep.java`

0 missing, 6 low

- L53: method `isCreated` - LOW
- L60: method `setCreated` - LOW
- L67: method `isSeen` - LOW
- L74: method `setSeen` - LOW
- L81: method `isEverseen` - LOW
- L88: method `setEverseen` - LOW

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

### `middle/player/Quest.java`

0 missing, 6 low

- L79: method `getIndex` - LOW
- L86: method `getName` - LOW
- L93: method `getLevel` - LOW
- L100: method `getRace` - LOW
- L107: method `getCurrentNumber` - LOW
- L114: method `getMaxNumber` - LOW

### `middle/player/enums/PlayerOptionEnum.java`

5 missing, 0 low

- L38: method `OP_birth_percent_damage` - MISSING
- L153: method `getDescription` - MISSING
- L157: method `getPlayerOptionType` - MISSING
- L161: method `isNormal` - MISSING
- L165: method `isCheat` - MISSING

### `middle/game/globals/registry/MiscRegistry.java`

2 missing, 3 low

- L52: field `logger` - MISSING
- L54: field `nameSections` - MISSING
- L72: method `getHints` - LOW
- L82: method `getNames` - LOW
- L92: method `getFlavours` - LOW

### `middle/game/gameengine/GameState.java`

0 missing, 5 low

- L70: method `getTurn` - LOW
- L92: method `getDaycount` - LOW
- L122: method `getPlayer` - LOW
- L138: method `getCave` - LOW
- L163: method `getCommandQueue` - LOW

### `middle/objects/Brand.java`

0 missing, 5 low

- L94: method `getName` - LOW
- L101: method `getCode` - LOW
- L108: method `toString` - LOW
- L146: method `hashCode` - LOW
- L162: method `getMultiplier` - LOW

### `middle/player/StartItem.java`

0 missing, 5 low

- L74: method `gettValue` - LOW
- L81: method `getsValue` - LOW
- L88: method `getMin` - LOW
- L95: method `getMax` - LOW
- L102: method `geteOpts` - LOW

### `middle/gameinput/EffectChoice.java`

4 missing, 0 low

- L20: interface `EffectChoice` - MISSING
- L22: record `Index` - MISSING
- L25: record `Random` - MISSING
- L28: record `Aborted` - MISSING

### `middle/monsters/MonsterFlag.java`

4 missing, 0 low

- L23: class `MonsterFlag` - MISSING
- L24: field `index` - MISSING
- L25: field `type` - MISSING
- L26: field `desc` - MISSING

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

### `middle/game/globals/GameConstants.java`

1 missing, 3 low

- L156: field `storeMax` - MISSING
- L478: method `getStoreMax` - LOW
- L485: method `getRandartActivationsMax` - LOW
- L492: method `getCaveProfileMax` - LOW

### `middle/objects/Slay.java`

1 missing, 3 low

- L112: method `getCode` - LOW
- L119: method `getName` - LOW
- L126: method `toString` - LOW
- L180: method `copy` - MISSING

### `middle/magic/MagicBook.java`

0 missing, 4 low

- L182: method `getNumOfSpells` - LOW
- L191: method `getBookName` - LOW
- L198: method `getBookTValue` - LOW
- L214: method `isDungeon` - LOW

### `middle/monsters/MonsterBase.java`

0 missing, 4 low

- L99: method `toString` - LOW
- L113: method `getCodeName` - LOW
- L120: method `getFlags` - LOW
- L127: method `getDefaultMonsterChar` - LOW

### `middle/objects/Archery.java`

0 missing, 4 low

- L91: method `getAmmoType` - LOW
- L98: method `getAmmoDamage` - LOW
- L105: method `getLaunchDamage` - LOW
- L113: method `getLaunchMult` - LOW - "it back out"

### `middle/objects/FlavourKind.java`

0 missing, 4 low

- L57: constructor `FlavourKind` - LOW
- L66: method `getValue` - LOW
- L73: method `getGlyph` - LOW
- L80: method `getFlavours` - LOW

### `middle/player/PlayerBody.java`

0 missing, 4 low

- L158: method `getName` - LOW
- L165: method `getCount` - LOW
- L172: method `getSlots` - LOW
- L180: method `getSlot` - LOW

### `unused/Rect.java`

0 missing, 4 low

- L64: method `getLeft` - LOW
- L71: method `getTop` - LOW
- L78: method `getRight` - LOW
- L85: method `getBottom` - LOW

### `middle/game/gameengine/Core.java`

3 missing, 0 low

- L68: field `logger` - MISSING
- L224: method `handleChannelOutput` - MISSING
- L264: method `getCoreChannel` - MISSING

### `middle/monsters/enums/MonTimedFlags.java`

3 missing, 0 low

- L20: enum `MonTimedFlags` - MISSING
- L21: enum-const `MON_TMD_FLG_NOTIFY` - MISSING
- L22: enum-const `MON_TMD_FLG_NOMESSAGE` - MISSING

### `middle/objects/ObjectInfo.java`

3 missing, 0 low

- L27: class `ObjectInfo` - MISSING
- L69: field `ignoreLevel` - MISSING
- L78: record `QualityMapping` - MISSING

### `middle/objects/ObjectName.java`

3 missing, 0 low

- L26: class `ObjectName` - MISSING
- L28: field `nameSections` - MISSING
- L30: method `randnameMake` - MISSING

### `middle/player/PlayerOptions.java`

3 missing, 0 low

- L52: field `logger` - MISSING
- L87: constructor `PlayerOptions` - MISSING
- L104: method `initDefaults` - MISSING

### `middle/player/PlayerClass.java`

2 missing, 1 low

- L158: method `getMagic` - LOW
- L453: method `getTitle` - MISSING
- L468: method `getNoTitles` - MISSING

### `middle/monsters/enums/MonsterRaceFlag.java`

1 missing, 2 low

- L31: method `RF_NO_SLOW` - MISSING
- L140: method `getCategory` - LOW
- L147: method `getDescription` - LOW

### `middle/objects/Rune.java`

1 missing, 2 low

- L49: field `logger` - MISSING
- L219: method `getNote` - LOW
- L236: method `getVariety` - LOW

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

### `middle/objects/ObjectProperty.java`

0 missing, 3 low

- L152: method `getType` - LOW
- L167: method `getPayload` - LOW
- L174: method `getName` - LOW

### `middle/player/EquipSlot.java`

0 missing, 3 low

- L55: method `getItem` - LOW
- L62: method `getType` - LOW
- L69: method `getName` - LOW

### `middle/player/PlayerHistoryChart.java`

0 missing, 3 low

- L104: method `getChartNumber` - LOW
- L111: method `getSuccessorNumber` - LOW
- L118: method `getSuccessor` - LOW

### `middle/game/globals/loaders/PlayerDataLoader.java`

2 missing, 0 low

- L47: field `logger` - MISSING
- L49: method `initialiseExpLevel` - MISSING

### `middle/objects/ObjectGear.java`

2 missing, 0 low

- L41: class `ObjectGear` - MISSING
- L42: field `logger` - MISSING

### `middle/objects/ObjectKnowledge.java`

2 missing, 0 low

- L26: class `ObjectKnowledge` - MISSING
- L27: field `logger` - MISSING

### `middle/objects/ObjectMake.java`

2 missing, 0 low

- L23: class `ObjectMake` - MISSING
- L24: method `makeFakeArtifact` - MISSING

### `middle/objects/enums/ResType.java`

2 missing, 0 low

- L20: enum `ResType` - MISSING
- L21: enum-const `T_LRES` - MISSING

### `middle/player/PlayerBirth.java`

2 missing, 0 low

- L76: field `logger` - MISSING
- L78: field `MAX_BIRTH_POINTS` - MISSING

### `middle/player/PlayerKnowledge.java`

2 missing, 0 low

- L87: field `logger` - MISSING
- L1885: method `objectLearnOnWield` - MISSING

### `middle/player/PlayerName.java`

2 missing, 0 low

- L24: class `PlayerName` - MISSING
- L25: field `logger` - MISSING

### `middle/player/enums/PlayerOptionTypes.java`

2 missing, 0 low

- L52: field `name` - MISSING
- L58: method `getName` - MISSING

### `middle/gameinput/GameInputHolder.java`

1 missing, 1 low

- L43: constructor `GameInputHolder` - MISSING
- L67: method `getInstance` - LOW

### `middle/objects/enums/ObjectFlag.java`

1 missing, 1 low

- L32: method `OF_MAX` - MISSING
- L91: method `getFlag` - LOW

### `middle/objects/enums/ObjectKindFlag.java`

1 missing, 1 low

- L33: method `KF_MAX` - MISSING
- L75: method `getFlag` - LOW

### `middle/strings/Quark.java`

1 missing, 1 low

- L145: method `getName` - LOW
- L171: method `containsText` - MISSING

### `middle/magic/ClassMagic.java`

0 missing, 2 low

- L84: method `isCaster` - LOW
- L186: method `getNumBooks` - LOW

### `middle/objects/CurseData.java`

0 missing, 2 low

- L76: method `getPower` - LOW
- L83: method `getTimeout` - LOW

### `middle/player/PlayerHistoryEntry.java`

0 missing, 2 low

- L66: method `getText` - LOW
- L73: method `getRoll` - LOW

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

### `middle/game/globals/loaders/TerrainDataLoader.java`

1 missing, 0 low

- L46: field `logger` - MISSING

### `middle/game/globals/loaders/WorldDataLoader.java`

1 missing, 0 low

- L55: field `logger` - MISSING

### `middle/game/globals/registry/DungeonRegistry.java`

1 missing, 0 low

- L38: field `logger` - MISSING

### `middle/gameinput/DefaultGameInput.java`

1 missing, 0 low

- L55: field `logger` - MISSING

### `middle/magic/MagicSpell.java`

1 missing, 0 low

- L87: method `getSpellName` - MISSING

### `middle/monsters/MonsterFriends.java`

1 missing, 0 low

- L35: field `logger` - MISSING

### `middle/monsters/enums/MonTimed.java`

1 missing, 0 low

- L30: method `MON_TMD_MAX` - MISSING

### `middle/monsters/enums/MonsterFlag.java`

1 missing, 0 low

- L30: method `MFLAG_MAX` - MISSING

### `middle/monsters/enums/MonsterMessage.java`

1 missing, 0 low

- L33: method `MON_MSG_MAX` - MISSING

### `middle/objects/EgoItem.java`

1 missing, 0 low

- L402: method `clearIgnoreType` - MISSING

### `middle/objects/ObjectIgnore.java`

1 missing, 0 low

- L65: field `logger` - MISSING

### `middle/objects/enums/ObjectOriginEnum.java`

1 missing, 0 low

- L31: method `ORIGIN_MAX` - MISSING

### `middle/player/PlayerCalcs.java`

1 missing, 0 low

- L83: field `logger` - MISSING

### `middle/player/PlayerMagic.java`

1 missing, 0 low

- L20: class `PlayerMagic` - MISSING

### `middle/player/PlayerTimed.java`

1 missing, 0 low

- L82: field `logger` - MISSING

### `middle/player/TimedFailure.java`

1 missing, 0 low

- L250: method `getCode` - MISSING

### `middle/utils/NumberUtils.java`

1 missing, 0 low

- L216: method `cmp` - MISSING

### `middle/game/gameengine/CommandGetterHolder.java`

0 missing, 1 low

- L56: method `getInstance` - LOW

### `middle/game/globals/Food.java`

0 missing, 1 low

- L92: method `getFoodValue` - LOW

### `middle/magic/MagicRealm.java`

0 missing, 1 low

- L77: method `getName` - LOW

### `middle/monsters/MonsterDrop.java`

0 missing, 1 low

- L89: method `isBase` - LOW

### `middle/monsters/enums/MonsterSpell.java`

0 missing, 1 low

- L171: method `getTypes` - LOW

### `middle/monsters/enums/MonsterSpellTypeEnum.java`

0 missing, 1 low

- L85: method `isDamage` - LOW

### `middle/numerics/RandomChance.java`

0 missing, 1 low

- L39: constructor `RandomChance` - LOW - "Constructor"

### `middle/objects/ElementInfo.java`

0 missing, 1 low

- L70: method `getResLevel` - LOW

### `middle/objects/Grouper.java`

0 missing, 1 low

- L45: constructor `Grouper` - LOW - "Constructor"

### `middle/objects/enums/ObjectFlagID.java`

0 missing, 1 low

- L69: method `getIdMethod` - LOW

### `middle/strings/TextBlock.java`

0 missing, 1 low

- L52: constructor `TextBlock` - LOW - "Constructor"

### `middle/utils/quit/Quit.java`

0 missing, 1 low

- L87: constructor `GameQuitException` - LOW
