# Player view cache — requirements and proposed structure

Follow-up to `docs/functions/frontend_core_data_crossings.md`'s recommendation: neither a channel request/reply
roundtrip nor 64 individual `FRONTEND_BASELINE` exceptions, but a read-only player/world view record in `channel`, built
by the core and pushed to the front end on state change, read locally at display time. This document scopes that record
to the **player-status**
slice only — the cluster driving Issue 1 — and proposes a structure for it. Cave/monster-list/store data (123 + 90
further reads in the census) is deliberately left out; its refresh cadence is per-move and per-transaction rather than
per-status-change, which is a different design question, not answered here.

## Requirements

1. **Field coverage follows C's own existing boundary, not a fresh guess.** C already drew this exact line itself:
   `player_events[]` (`ui-display.c:67-84`, 13 event types — `EVENT_RACE_CLASS`,
   `EVENT_PLAYERTITLE`, `EVENT_EXPERIENCE`, `EVENT_PLAYERLEVEL`, `EVENT_GOLD`, `EVENT_EQUIPMENT`,
   `EVENT_STATS`, `EVENT_HP`, `EVENT_MANA`, `EVENT_AC`, `EVENT_MONSTERHEALTH`,
   `EVENT_PLAYERSPEED`, `EVENT_DUNGEONLEVEL`) and `statusline_events[]` (`ui-display.c:86-94`, 6 —
   `EVENT_STUDYSTATUS`, `EVENT_STATUS`, `EVENT_DETECTIONSTATUS`, `EVENT_STATE`, `EVENT_FEELING`,
   `EVENT_LIGHT`) are the set `update_player0_subwindow`/`update_player1_subwindow`/
   `update_player_compact_subwindow`/`update_topbar_subwindow` already register against (`ui-display.c:1995-2111`,
   `frontend_core_data_crossings.md`'s rows for those four). The cache's field list should be exactly what these 19
   event types' handlers read, not a re-derivation.
2. **One push per event, not one write per field.** A category- (b) redraw function typically reads several fields in
   one pass (`update_player_compact_subwindow`: `player->` x37 in one call,
   `frontend_core_data_crossings.md:135`). If the cache were mutated field-by-field the UI could observe a torn read —
   level updated but XP not yet. The core must build one immutable snapshot per relevant event and publish it as a unit.
3. **The read path must be synchronous and non-blocking.** Category- (c) hook commands (`do_cmd_change_name`,
   `death_info`, `do_cmd_look`, etc. — `frontend_core_data_crossings.md`'s category- (c) table) run entirely on the UI
   thread mid-command, with no opportunity to wait on a reply from a core that may be mid-turn. Reading the cache must
   be a plain field access, never a send-and-wait.
4. **Cross-thread publication must be safe without locking the read side.** Core thread writes, UI thread reads, at
   arbitrary times relative to each other — the shape calls for a single immutable object published through a volatile
   reference (or `AtomicReference`), replaced wholesale on each push, not a mutable object touched by both sides.
5. **The record type and its message wrapper belong in `channel`; the holder that remembers "the current one" belongs in
   `frontend`.** Per boundary rule 1, only `frontend` may read it and only
   `middle`/`backend` (via `channel`) may produce it — matching how `CoreMessage`'s existing members are structured
   (`CoreMessage.java:49-110`).
6. **Scope discipline: only fields a category- (b)/ (c) function has actually been shown to need.**
   Two exclusions the census already justifies: `player->opts` (28 of the 564 raw reads) already lives UI-side in the
   port and needs no cache entry; `player->upkeep->command_wrk` (83 of 200
   `upkeep` hits) is front-end menu state C had nowhere else to park and should simply live in
   `frontend`, not ride this cache at all (`frontend_core_data_crossings.md:57-60`).
7. **A write-back path is needed for the one field the UI mutates directly in C.**
   `do_cmd_change_name` doesn't just read `player->full_name` — it writes it, directly, with no event fired:
   `my_strcpy(player->full_name, namebuf, sizeof(player->full_name));`
   (`ui-player.c:1255-1256`). A read-only cache can't serve this; the front end needs a `UIMessage`
   asking the core to rename the player, with the core updating its own `Player` and re-publishing a refreshed snapshot
   in response. This is a second, smaller piece of design the recommendation didn't call out by name.
8. **Not every field needs the same refresh cadence.** `player->died_from` (14 reads, all in the death screens —
   `death_examine`, `death_file`, `death_history`, `death_info`,
   `death_messages`, `death_scores` in `frontend_core_data_crossings.md`'s category- (c) rows) is write-once,
   read-only-after-death; it doesn't need to ride the same per-status-change publish as HP or AC. It can be carried on
   `EVENT_ENTER_DEATH`'s own message rather than the ongoing status-view record.
9. **The cache must exist before the first read.** Something must publish an initial snapshot at
   `EVENT_ENTER_GAME`/`EVENT_ENTER_WORLD`, before any hook command has a chance to run against an empty cache.
10. **Staleness tolerance is uneven across fields, and that has to stay visible.** Body-slot count (`player->body`, 34
    reads — the field driving Issue 1) changes only on a race switch and is the least staleness-prone field in the whole
    population (`Initialize_ui_entry_Issue_1.md`'s conclusion). HP is not. Any future addition to this cache should be
    checked against how often its source event actually fires, not assumed safe by analogy to body count.

## Proposed structure

**`channel.messages.data.PlayerStatusView`** — one record, fields grouped by the `player_events`/
`statusline_events` boundary above:

- identity: `name`, `title`, `race`, `class`
- progression: `level`, `experience`, `maxExperience`, `gold`
- vitals: `hp`, `maxHp`, `sp`, `maxSp`, `armourClass`, `speed`
- stats: the five current/max pairs (`EVENT_STATS`)
- monster health: whatever `EVENT_MONSTERHEALTH` currently feeds `prt_health`
- depth: `dungeonLevel`
- status line: study status, status conditions, detection status, resting/repeating state, level feeling, light level
- body: equipment-slot count (piggybacks on `EVENT_RACE_CLASS`, since body shape only changes alongside race — no
  dedicated event needed per requirement 10)

`died_from` is **not** a field on this record — see requirement 8.

**`CoreMessage.PlayerStatusCoreMessage(PlayerStatusView view)`** — a new member of the sealed
`CoreMessage` interface (`CoreMessage.java:49-50`), following the existing one-record-per-payload- shape rule. Pushed by
the core whenever any of the 19 source events fire.

**A holder on the UI side** (e.g. `frontend.ui.PlayerStatusCache`) holding a single
`volatile PlayerStatusView current`, replaced wholesale whenever a `PlayerStatusCoreMessage`
arrives on `uiQueue`. `UIPlayer.haveValidCharSheetConfig()` and every other category- (b)/ (c)
display function reads `current` directly — a field access, not a channel operation.

**Write-back**: a new `UIMessage` (e.g. `RenamePlayerUIMessage(String name)`) for the one mutation case in requirement
7, sent on `coreQueue`, handled by the core exactly where C's
`do_cmd_change_name` writes `player->full_name` today, followed by a fresh
`PlayerStatusCoreMessage` publish.

Left open, and Rowan's call: whether `EVENT_ENTER_DEATH` gets its own small payload for
`died_from`, or whether death-screen fields deserve a second, separate view record entirely rather than a special case
bolted onto this one.
