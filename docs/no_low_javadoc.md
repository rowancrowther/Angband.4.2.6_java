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

### `middle/monsters/BlowEffect.java`

0 missing, 7 low

- L126: method `getName` - LOW
- L133: method `getPower` - LOW
- L140: method `getEval` - LOW
- L147: method `getDesc` - LOW
- L154: method `getLoreAttr` - LOW
- L161: method `getLoreAttrResist` - LOW
- L168: method `getLoreAttrImmune` - LOW

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

### `middle/player/PlayerProperty.java`

0 missing, 7 low

- L111: method `getPlayerPropertyType` - LOW
- L118: method `getpCode` - LOW
- L125: method `getoCode` - LOW
- L132: method `getEntries` - LOW
- L139: method `getName` - LOW
- L146: method `getDescription` - LOW
- L153: method `getValue` - LOW

### `unused/EventDataBirthStage.java`

0 missing, 7 low

- L92: method `isReset` - LOW
- L99: method `getHint` - LOW
- L106: method `getnChoices` - LOW
- L113: method `getInitialChoice` - LOW
- L120: method `getChoices` - LOW
- L127: method `getHelpTexts` - LOW
- L134: method `getXtra` - LOW

### `middle/objects/Pile.java`

6 missing, 0 low

- L239: method `insertEnd` - MISSING
- L316: method `size` - MISSING
- L320: method `get` - MISSING
- L324: method `reversed` - MISSING
- L328: method `removeIf` - MISSING
- L332: method `remove` - MISSING

### `middle/objects/enums/IgnoreTypeEnum.java`

6 missing, 0 low

- L20: enum `IgnoreTypeEnum` - MISSING
- L21: enum-const `IGNORE_NONE` - MISSING
- L22: enum-const `IGNORE_BAD` - MISSING
- L23: enum-const `IGNORE_AVERAGE` - MISSING
- L24: enum-const `IGNORE_GOOD` - MISSING
- L25: enum-const `IGNORE_ALL` - MISSING

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
