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

8. `SlayReader`
9. `BrandReader`
10. `ObjectBaseReader`
11. `CurseReader`
12. `ActivationReader`
13. `ObjectPropertyReader`
14. `EgoItemReader`
15. `ArtifactReader`
16. `ItemObjectReader`
17. `ProjectionReader`

## Batch 3 — `init.c`

18. `RealmReader`
19. `ShapeReader`
20. `HintReader`
21. `FlavourReader`
22. `NamesReader`
23. `HistoryReader`
24. `BodyReader`
25. `PlayerRaceReader`
26. `PlayerClassReader`
27. `TrapReader`
28. `PlayerPropertyReader`
29. `TerrainReader`
30. `WorldReader`
31. `GameConstantsReader`

## Batch 4 — other C files (confirm in stage 1)

32. `QuestReader`
33. `VaultReader`
34. `RoomProfileReader`
35. `DungeonProfileReader`
36. `ChestTrapReader`
37. `PlayerTimedReader`
38. `RandomReader`
39. `VisualsReader`

## Last

40. `LoreReader` — its parsing is deliberately parked, so port only what exists.
