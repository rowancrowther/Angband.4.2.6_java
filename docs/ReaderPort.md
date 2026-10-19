# Reader port — targets

Drafted 261017. One `/port` run per reader, one precis point per reader.

Each target is `src/main/java/uk/co/jackoftradesltd/backend/parser/<Name>.java`, run as
`/port backend/parser/<Name>.java`. The C file is where the parser is expected to live; stage 1 confirms it.

`BlowEffectReader` was ported on 261017 and is not listed.

## Batch 1 — `mon-init.c`

 1. [X] `PainReader`
 2. [X] `BlowMethodReader`
 3. [X] `SummonReader`
 4. [X] `PitReader`
 5. [X] `MonsterSpellReader`
 6. [X] `MonsterBaseReader`
 7. [X] `MonsterReader`

## Batch 2 — `obj-init.c`

 1. [X] `SlayReader`
 2. [X] `BrandReader`
 3. [X] `ObjectBaseReader`
 4. [X] `CurseReader`
 5. [X] `ActivationReader`
 6. [X] `ObjectPropertyReader`
 7. [X] `EgoItemReader`
 8. [X] `ArtifactReader`
 9. [X] `ItemObjectReader`
10. [X] `ProjectionReader`

## Batch 3 — `init.c`

 1. [X] `RealmReader`
 2. [X] `ShapeReader`
 3. [X] `HintReader`
 4. [X] `FlavourReader`
 5. [X] `NamesReader`
 6. [X] `HistoryReader`
 7. [X] `BodyReader`
 8. [X] `PlayerRaceReader`
 9. [X] `PlayerClassReader`
10. [X] `TrapReader`
11. [X] `PlayerPropertyReader`
12. [X] `TerrainReader`
13. [X] `WorldReader`
14. `GameConstantsReader`

## Batch 4 — other C files (confirm in stage 1)

1. `QuestReader`
2. `VaultReader`
3. `RoomProfileReader`
4. `DungeonProfileReader`
5. `ChestTrapReader`
6. `PlayerTimedReader`
7. `RandomReader`
8. `VisualsReader`

## Last

1. `LoreReader` — its parsing is deliberately parked, so port only what exists.
