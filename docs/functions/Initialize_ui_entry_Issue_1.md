# Issue 1 — `UIPlayer.java → middle.game.gameengine.GameState`

Follow-up to `docs/functions/initialize_ui_entry_iterator.md`'s "Verification — current status" section, item 1 of the
three still-failing `BoundaryTest` methods: `frontend/ui/UIPlayer.java →
middle.game.gameengine.GameState` — pre-existing, unrelated to the UI-entry registry move.

## The crossing

`UIPlayer.haveValidCharSheetConfig()`
(`src/main/java/uk/co/jackoftradesltd/frontend/ui/UIPlayer.java:63-72`) calls
`GameState.getPlayer().getPlayerBody().getCount()` to decide whether the cached character-sheet layout is stale.
`GameState` is `middle.game.gameengine.GameState` — a `frontend` → `middle` import, disallowed by boundary rule 1
(`docs/Architecture_migration.md`).

## Question asked

What calls `display_player` in the C original, and is that call something that will end up being triggered by a message
across the core/UI channel — one that could carry the player-body data
`haveValidCharSheetConfig` needs, so `UIPlayer` wouldn't have to reach into `GameState` directly?

## C callers of `display_player` (`ui-player.c:889`)

Two different shapes:

1. **`do_cmd_change_name`** (`ui-player.c:1218`, calls `display_player(mode)` at `:1234`) — bound to the `'C'` key in
   the *info* command table, `ui-game.c:184`:
   ```c
   { "Character description", { 'C' }, CMD_NULL, do_cmd_change_name, NULL, 0, NULL, NULL, NULL, 0 },
   ```
   Also called directly from the store screen, `ui-store.c:852`.
2. **`update_player0_subwindow` / `update_player1_subwindow`** (`ui-display.c:1995`, `:2016`) — registered as handlers
   on `EVENT_INVENTORY`/`EVENT_EQUIPMENT` (`ui-display.c:2216`, `:2225`).

`death_info` (`ui-death.c:200`) and several birth-review screens (`ui-birth.c:890,1649,1727,1739,1751`)
are the same shape as (1): direct, synchronous calls, not event-driven.

## The decisive bit — `ui-game.c`'s dispatcher

`textui_process_command` (`ui-game.c:579-583`):

```c
if (cmd && cmd->hook) {
    /* UI command */
    cmd->hook();
} else if (cmd && cmd->cmd) {
    /* Game command */
    cmdq_push_repeat(cmd->cmd, count);
}
```

`do_cmd_change_name`'s table entry has `cmd_code == CMD_NULL` and a `hook` pointer, so it takes the
`hook()` branch — called directly, synchronously, on the same thread. It never reaches
`cmdq_push_repeat`, the actual core-command queue. C's own dispatcher draws this line explicitly:
`'C'` is a *UI command*, not a *game command*.

## Answer

**No** — path (1), the dominant one (the full character-sheet screen), is not triggered by a core-originated message.
Only path (2), the subwindow live-redraw, is event-driven.
`display_player`/`configure_char_sheet`/`have_valid_char_sheet_config` read the global `player`
struct directly from UI code, with no event or queue involved.

There is also no ready-made payload shape to lean on even for a hypothetical round trip:
`CoreMessage.java:49-110` only has `SimpleCoreMessage` / `TextCoreMessage` / `LifecycleCoreMessage` /
`GameEventCoreMessage`, and the stated design principle (`:36-41`) is that a message carries *the event*, not *the data
a redraw needs* — matching C, where even `EVENT_STATS`/`EVENT_RACE_CLASS`
(`GameEventType.java`) carry no payload at all; the handler just re-reads `player` itself.

## Conclusion

`UIPlayer.haveValidCharSheetConfig()`'s call to `GameState.getPlayer()` is the same shape of crossing as the
`ObjectPropertyAssembler`/`PlayerPropertyAssembler` → `UIRegistry` crossing already discussed in
`initialize_ui_entry_iterator.md` as "left alone, on purpose" — a spot where C's flat address space lets UI code reach
player data with no event in between, so there's no existing C-side message to port. Closing it means either a
documented `FRONTEND_BASELINE` exception, or inventing a request/reply pair the channel design has no precedent for —
not something the eventual `EVENT_RACE_CLASS`-style plumbing would hand over for free.
