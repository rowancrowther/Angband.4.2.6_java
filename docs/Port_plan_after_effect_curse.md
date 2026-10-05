# Port plan — after the Effect/Curse work

Drafted 261005.

## Prerequisites

1. `docs/Curse_object_unflattening.md` — done.
2. `docs/Effect_time_migration.md` — done.

## Files to port (in order)

1. `src/main/java/uk/co/jackoftradesltd/middle/objects/ObjectKind.java`
2. `src/main/java/uk/co/jackoftradesltd/middle/objects/ItemObject.java`
3. `src/main/java/uk/co/jackoftradesltd/middle/player/PlayerClass.java`
4. `src/main/java/uk/co/jackoftradesltd/middle/player/Player.java`

Each goes through `/port <File>.java` (verify → Javadoc → tests → precis → C stamp).
