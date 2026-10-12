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

39
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
