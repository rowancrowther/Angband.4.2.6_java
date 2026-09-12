# Front end → core data crossings in the C original

A census of every place Angband's front end (`ui-*.c`) reads core game state directly, taken to size the population
before deciding whether the Java port needs a channel request/reply roundtrip or should take
`FRONTEND_BASELINE` exceptions one at a time.

Prompted by issue 1 of `docs/functions/Initialize_ui_entry_Issue_1.md` — `UIPlayer.java →
middle.game.gameengine.GameState` — where `UIPlayer.haveValidCharSheetConfig()` needs
`player->body.count` and there is no C-side message carrying it.

Method: every `CMD_NULL`-plus-`hook` entry in the `cmd_info` tables (`ui-game.c:116-330`) and the menu-dispatched
equivalents in `ui-store.c`, `ui-birth.c`, `ui-death.c`, `ui-knowledge.c` and
`ui-options.c`; each function body plus the helpers it calls scanned for reads of `player->`, `cave`,
`z_info->`, `stores`, `f_info`, `r_info`, `k_info`, `a_info`, `turn` and `OPT()`; every
`event_add_handler` / `register_or_deregister` registration in `ui-*.c` cross-referenced against the
`game_event_data` union (`game-event.h:186-260`) and the `event_signal_*` variants (`game-event.h:217-248`) to mark each
event payload-carrying or bare-signal.

## Findings

**The raw population is 564 `player->` reads across 22 `ui-*.c` files, plus 123 `cave` references and 90 `z_info->`
references** — roughly 777 individual core-state reads sitting in front-end code. Only 28 of the 564 are `player->opts`,
which already lives UI-side in the port, so ~536 are genuine crossings.

Concentrated by file (`player->` counts): `ui-object.c` 109, `ui-display.c` 108, `ui-player.c` 91,
`ui-context.c` 33, `ui-knowledge.c` 23, `ui-options.c` 22, `ui-store.c` 18, `ui-target.c` 16,
`ui-death.c` 16, with the remaining 13 files under a dozen each.

**Category split, by call site rather than by read:**

| Category                                                   | Count | What it means                                            |
|------------------------------------------------------------|-------|----------------------------------------------------------|
| (a) payload-carrying event, handler needs nothing else     | 4     | Maps straight onto an existing `CoreMessage` shape       |
| (b) event-driven, but the handler still reads core globals | 35    | `CoreMessage` arrives, front end reads state anyway      |
| (c) pure `hook`, no event at all                           | 29    | The `display_player` shape — no C-side message precedent |

Of 32 non-debug hook-dispatched commands, **29 read core state**; only `textui_cmd_rest`
(`ui-command.c:191-222`), `do_cmd_message_one` (`ui-knowledge.c:3709-3713`) and `do_cmd_pref`
(`ui-prefs.c:1473-1485`) are clean. `textui_cmd_rest` is the shape the port wants everywhere: prompt for input,
`cmdq_push(CMD_REST)`, read nothing.

**The finding that matters most is not the count — it is that category (a) is nearly empty.** Only nine event types
carry any payload at all (`EVENT_MAP`, `EVENT_EXPLOSION`, `EVENT_BOLT`, `EVENT_MISSILE`,
`EVENT_MESSAGE`, `EVENT_BELL`, `EVENT_SOUND`, `EVENT_INITSTATUS`, `EVENT_BIRTHPOINTS`, plus the
`EVENT_GEN_*` introspection set and flag-carrying `EVENT_ENTER_BIRTH`). Every other event is a bare
"something changed" signal. And even among the payload-carrying ones, the handler almost always reads core state anyway:
`display_explosion` (`ui-display.c:1559-1640`) gets a full blast struct and still reads `player` three times;
`update_maps` (`ui-display.c:1347-1419`) gets a `struct loc` and still walks
`cave` twenty-six times. Enriching the existing events would not close these — the payload was never meant to be
sufficient, because in C the handler can just look.

**The reads concentrate hard, which is what makes this tractable.** By field: `player->upkeep` 200,
`player->grid` 59, `player->body` 34, `player->opts` 28, `player->lev` 25, `player->state` 24,
`player->class` 19, `player->full_name` 17, `player->died_from` 14, `player->timed` 13. Eleven fields account for well
over half of all reads.

**`player->upkeep->command_wrk` alone is 83 of the 200 `upkeep` hits** — that is the item-menu's current display mode,
UI state that C parked on the player struct because it had nowhere else to put it. It is not a crossing to solve; it is
front-end state that should simply live in the front end in the port. Discounting it drops the population by roughly 15%
before any design work happens.

## Recommendation

**Neither a generic request/reply roundtrip nor 64 individual baseline exceptions.** A third shape fits the evidence
better: a read-only player/world **view record in `channel`**, built by the core and pushed to the front end when state
changes, which the UI reads locally at display time.

The reasoning is in the shape of the reads. These are overwhelmingly *display-time snapshots of slow-changing scalars* —
level, class, name, body slot count, stat array — not queries whose answers depend on when they are asked. A
request/reply roundtrip prices every one of those 536 reads as a blocking call into a core that may be mid-turn, which
is both a deadlock surface and a latency cost paid hundreds of times per redraw. A pushed snapshot pays nothing at read
time and matches what C's events already mean: `EVENT_STATS` fires *because the stats changed*, and the only reason it
carries no payload is that C had no boundary to carry it across.

**The trade-off to weigh against that:** a snapshot type is a second place where player state is written down, and it
has to be kept honest — every field the UI wants must be added to it and refreshed on the right event, or the front end
silently renders stale values. A roundtrip cannot go stale by construction. Staleness is a real and recurring bug class;
the port would be trading a latency and deadlock problem for a cache-coherence one. Which is the better trade depends on
how much of the character sheet turns out to need per-frame freshness, which this scrape does not answer.

The immediate case, `UIPlayer.haveValidCharSheetConfig()` needing `player->body.count`, sits at the easy end either way:
body shape changes only on a race switch, so it is the least staleness-prone field in the whole population.

Rowan's call.

## The table

Sorted by category, then by file. "Core globals read" merges direct reads with those reached through helpers the
function calls.

### Category (a) — payload-carrying event, handler needs nothing further

| C function                  | file:lines               | Trigger                     | Core globals read          | Cat |
|-----------------------------|--------------------------|-----------------------------|----------------------------|-----|
| `repeated_command_display`  | `ui-display.c:2497-2505` | `EVENT_COMMAND_REPEAT`      | none                       | a   |
| `splashscreen_note`         | `ui-display.c:2405-2425` | `EVENT_INITSTATUS` (string) | `k_info` via `init.c` only | a   |
| `trace_map_updates`         | `ui-display.c:1334-1341` | `EVENT_MAP` (point)         | none                       | a   |
| `update_messages_subwindow` | `ui-display.c:1886-1942` | `EVENT_STATE`               | none                       | a   |

### Category (b) — event-driven, handler still reads core globals

| C function                        | file:lines               | Trigger                                        | Core globals read                         | Cat |
|-----------------------------------|--------------------------|------------------------------------------------|-------------------------------------------|-----|
| `point_based_misc`                | `ui-birth.c:1044-1048`   | `EVENT_GOLD`                                   | `player->` x1                             | b   |
| `point_based_points`              | `ui-birth.c:1056-1084`   | `EVENT_BIRTHPOINTS` (payload)                  | none directly; payload sufficient here    | b   |
| `point_based_stats`               | `ui-birth.c:1033-1037`   | `EVENT_STATS`                                  | `player->` x11                            | b   |
| `ui_enter_birthscreen`            | `ui-birth.c:1784-1791`   | `EVENT_ENTER_BIRTH` (flag)                     | `player->` x4                             | b   |
| `ui_leave_birthscreen`            | `ui-birth.c:1794-1802`   | `EVENT_LEAVE_BIRTH`                            | `player->` x1                             | b   |
| `animate`                         | `ui-display.c:1492-1495` | `EVENT_ANIMATE`                                | `cave` x5, `player->` x1                  | b   |
| `check_panel`                     | `ui-display.c:2578-2581` | `EVENT_PLAYERMOVED`                            | `player->` x4, `OPT()`                    | b   |
| `cheat_death`                     | `ui-display.c:2570-2576` | `EVENT_CHEAT_DEATH`                            | `player` x14, `player->` x15              | b   |
| `display_bolt`                    | `ui-display.c:1645-1691` | `EVENT_BOLT` (payload)                         | `player` x3, `player->` x3, `cave` x2     | b   |
| `display_explosion`               | `ui-display.c:1559-1640` | `EVENT_EXPLOSION` (payload)                    | `player` x3, `player->` x3, `cave->` x4   | b   |
| `display_missile`                 | `ui-display.c:1696-1719` | `EVENT_MISSILE` (payload)                      | `player` x3, `player->` x3, `cave` x2     | b   |
| `hp_colour_change`                | `ui-display.c:897-902`   | `EVENT_HP`                                     | `player->` x1, `cave` x1, `OPT()`         | b   |
| `new_level_display_update`        | `ui-display.c:2510-2563` | `EVENT_NEW_LEVEL_DISPLAY`                      | `player->` x9, `z_info->` x2, `turn`      | b   |
| `refresh`                         | `ui-display.c:2485-2495` | `EVENT_REFRESH`                                | `player` x2, `cave->` x2, `OPT()`         | b   |
| `see_floor_items`                 | `ui-display.c:2583-2653` | `EVENT_SEEFLOOR`                               | `player` x4, `player->` x8, `z_info->` x6 | b   |
| `ui_enter_game`                   | `ui-display.c:2862-2876` | `EVENT_ENTER_GAME`                             | `player` x2                               | b   |
| `ui_enter_world`                  | `ui-display.c:2723-2790` | `EVENT_ENTER_WORLD`                            | `player` x4, `player->` x1                | b   |
| `ui_leave_world`                  | `ui-display.c:2793-2859` | `EVENT_LEAVE_WORLD`                            | `player` x3                               | b   |
| `update_equip_subwindow`          | `ui-display.c:1752-1770` | `EVENT_EQUIPMENT`                              | `player->` x11, `z_info->` x6             | b   |
| `update_inven_subwindow`          | `ui-display.c:1732-1750` | `EVENT_INVENTORY`                              | `player->` x11, `z_info->` x6             | b   |
| `update_itemlist_subwindow`       | `ui-display.c:1810-1825` | `EVENT_ITEMLIST`                               | `cave->` x3, `player->` x3, `turn`        | b   |
| `update_maps`                     | `ui-display.c:1347-1419` | `EVENT_MAP` (point)                            | `cave` x26, `f_info` x7, `player->` x11   | b   |
| `update_minimap_subwindow`        | `ui-display.c:1950-1989` | `EVENT_MAP`, `EVENT_DUNGEONLEVEL`, `EVENT_END` | `cave` x20, `cave->` x14, `f_info` x8     | b   |
| `update_monlist_subwindow`        | `ui-display.c:1827-1842` | `EVENT_MONSTERLIST`                            | `cave` x11, `player->` x3, `turn`         | b   |
| `update_monster_subwindow`        | `ui-display.c:1845-1863` | `EVENT_MONSTERTARGET`                          | `player->` x3                             | b   |
| `update_object_subwindow`         | `ui-display.c:1866-1883` | `EVENT_OBJECTTARGET`                           | `player->` x4, `z_info->` x7              | b   |
| `update_player0_subwindow`        | `ui-display.c:1995-2011` | `player_events` set (13 events)                | `player->` x27 via `ui-player.c`          | b   |
| `update_player1_subwindow`        | `ui-display.c:2016-2032` | `player_events` set (13 events)                | `player->` x27 via `ui-player.c`          | b   |
| `update_player_compact_subwindow` | `ui-display.c:2059-2111` | `player_events` set                            | `player->` x37, `player` x7               | b   |
| `update_topbar_subwindow`         | `ui-display.c:2034-2054` | `player_events` + `statusline_events`          | `player->` x28, `cave` x1                 | b   |
| `bell_message`                    | `ui-input.c:597-604`     | `EVENT_BELL` (payload)                         | `player->` x1, `OPT()`                    | b   |
| `display_message`                 | `ui-input.c:484-592`     | `EVENT_MESSAGE` (payload)                      | `k_info` x3, `player`, `OPT()`            | b   |
| `message_flush`                   | `ui-input.c:609-635`     | `EVENT_MESSAGE_FLUSH`                          | `player` x1, `OPT()`                      | b   |
| `check_for_player_interrupt`      | `ui-game.c:627-648`      | `EVENT_CHECK_INTERRUPT`                        | `player` x7, `player->` x3, `turn`        | b   |
| `enter_store`                     | `ui-store.c:1255-1269`   | `EVENT_ENTER_STORE`                            | `cave`, `player->`, `stores`, `f_info`    | b   |
| `leave_store`                     | `ui-store.c:1314-1332`   | `EVENT_LEAVE_STORE`                            | `player->` x3                             | b   |
| `refresh_stock`                   | `ui-store.c:1239-1250`   | `EVENT_STORECHANGED`                           | `stores` x2, `player->` x5, `z_info->`    | b   |
| `use_store`                       | `ui-store.c:1274-1312`   | `EVENT_USE_STORE`                              | `player->` x7, `stores`, `z_info->` x2    | b   |

### Category (c) — pure `hook`, no event, no C-side message precedent

| C function                       | file:lines                   | Trigger                    | Core globals read                                 | Cat |
|----------------------------------|------------------------------|----------------------------|---------------------------------------------------|-----|
| `do_cmd_feeling`                 | `cmd-cave.c:1777-1780`       | `KTRL('F')`                | `cave->` x3, `player->` x1, `z_info->`            | c   |
| `do_cmd_note`                    | `cmd-misc.c:88-116`          | `:`                        | `player->` x2                                     | c   |
| `do_cmd_wizard`                  | `cmd-misc.c:37-68`           | `KTRL('W')`                | `player->` x7                                     | c   |
| `do_cmd_fire_at_nearest`         | `player-attack.c:1412-1446`  | `h` / TAB                  | `player->` x5, `z_info->` x1                      | c   |
| `do_cmd_abilities`               | `player-properties.c:96-102` | `S`                        | `player->` x2                                     | c   |
| `do_cmd_redraw`                  | `ui-command.c:64-117`        | `KTRL('R')`                | `player->` x7 direct, x6 via helpers              | c   |
| `do_cmd_save_screen`             | `ui-command.c:540-561`       | `)`                        | `player->` x7, `f_info`, `k_info`, `r_info`       | c   |
| `do_cmd_version`                 | `ui-command.c:143-157`       | `V`                        | `player->` x1                                     | c   |
| `do_cmd_xxx_options`             | `ui-command.c:124-128`       | `=`                        | `player->` x10, `player` x7                       | c   |
| `textui_cmd_retire`              | `ui-command.c:162-186`       | `Q`                        | `player->` x1                                     | c   |
| `textui_quit`                    | `ui-command.c:228-231`       | `KTRL('X')`                | `player->` x1                                     | c   |
| `death_examine`                  | `ui-death.c:303-325`         | death menu `x`             | `player` x10, `player->` x1                       | c   |
| `death_file`                     | `ui-death.c:162-188`         | death menu `f`             | `player->` x1, `player` x2                        | c   |
| `death_history`                  | `ui-death.c:331-334`         | death menu `h`             | `player` x3, `player->` x1, `turn`                | c   |
| `death_info`                     | `ui-death.c:193-278`         | death menu `i`             | `player->` x41, `stores`, `f_info`, `z_info->` x7 | c   |
| `death_messages`                 | `ui-death.c:283-288`         | death menu `m`             | `player` x3, `player->` x1                        | c   |
| `death_scores`                   | `ui-death.c:293-298`         | death menu `v`             | `player` x3, `player->` x2                        | c   |
| `death_spoilers`                 | `ui-death.c:339-342`         | death menu `s`             | `player` x1, `player->` x1                        | c   |
| `toggle_inven_equip`             | `ui-display.c:1775-1808`     | `KTRL('E')`                | `player->` x11, `z_info->` x6                     | c   |
| `save_game`                      | `ui-game.c:1016-1019`        | `KTRL('S')`                | `player` x8, `player->` x4                        | c   |
| `do_cmd_help`                    | `ui-help.c:470-481`          | `?`                        | `player->` x1, `k_info` x3, `OPT()`               | c   |
| `do_cmd_center_map`              | `ui-knowledge.c:4447-4450`   | `KTRL('L')` / `@`          | `player->` x4                                     | c   |
| `do_cmd_equip`                   | `ui-knowledge.c:3959-4002`   | `e`                        | `player->` x4                                     | c   |
| `do_cmd_inven`                   | `ui-knowledge.c:3913-3953`   | `i`                        | `player->` x3                                     | c   |
| `do_cmd_itemlist`                | `ui-knowledge.c:4472-4481`   | `]`                        | `cave->` x3, `player->` x4, `turn`                | c   |
| `do_cmd_locate`                  | `ui-knowledge.c:4070-4151`   | `L` / `W`                  | `player->` x5, `cave->` x2, `OPT()` x2            | c   |
| `do_cmd_look`                    | `ui-knowledge.c:4057-4064`   | `l` / `x`                  | `cave` x30, `player->` x26, `z_info->` x7         | c   |
| `do_cmd_messages`                | `ui-knowledge.c:3731-3903`   | `KTRL('P')`                | `player->` x3, `k_info` x3, `OPT()` x2            | c   |
| `do_cmd_monlist`                 | `ui-knowledge.c:4457-4466`   | `[`                        | `cave` x4, `player->` x4, `turn` x3               | c   |
| `do_cmd_query_symbol`            | `ui-knowledge.c:4283-4442`   | `/`                        | `r_info` x2, `k_info` x5, `z_info->` x3           | c   |
| `do_cmd_quiver`                  | `ui-knowledge.c:4008-4051`   | `\|`                       | `player->` x4                                     | c   |
| `textui_browse_knowledge`        | `ui-knowledge.c:3641-3698`   | `~`                        | `a_info` x4, `k_info` x3, `z_info->` x7           | c   |
| `textui_browse_object_knowledge` | `ui-knowledge.c:2062-2091`   | options menu `{`           | `k_info` x2, `a_info`, `z_info->` x3              | c   |
| `do_cmd_view_map`                | `ui-map.c:894-939`           | `M`                        | `cave->` x11, `cave` x5, `player->` x5            | c   |
| `textui_cmd_ignore`              | `ui-object.c:1825-1837`      | `k` / `KTRL('D')`          | `player->` x5 via `obj-ignore.c`                  | c   |
| `textui_cmd_toggle_ignore`       | `ui-object.c:1839-1844`      | `K` / `O`                  | `player->` x3                                     | c   |
| `textui_obj_examine`             | `ui-object.c:1669-1693`      | `I`                        | `player->` x1                                     | c   |
| `do_cmd_delay`                   | `ui-options.c:1054-1077`     | options menu `d`           | `player->` x3 (`opts`)                            | c   |
| `do_cmd_hp_warn`                 | `ui-options.c:1119-1153`     | options menu `h`           | `player->` x4 (`opts`)                            | c   |
| `do_cmd_lazymove_delay`          | `ui-options.c:1159-1187`     | options menu `m`           | `player->` x4 (`opts`)                            | c   |
| `do_cmd_options_item`            | `ui-options.c:2005-2024`     | options menu `i`           | `player->` x1                                     | c   |
| `do_cmd_options_win`             | `ui-options.c:386-521`       | options menu `w`           | `player` x8, `player->` x3, `k_info` x3           | c   |
| `do_cmd_sidebar_mode`            | `ui-options.c:1082-1113`     | options menu `o`           | `player` x3, `player->` x1                        | c   |
| `do_dump_autoinsc`               | `ui-options.c:1253-1255`     | options menu `t`           | `player->` x1                                     | c   |
| `do_dump_charscreen_opt`         | `ui-options.c:1260-1262`     | options menu `u`           | `player->` x1                                     | c   |
| `do_dump_options`                | `ui-options.c:1246-1248`     | options menu `s`           | `player->` x1                                     | c   |
| `option_toggle_menu`             | `ui-options.c:324-372`       | options menu `a`/`b`/`x`   | `player->` x1, `player` x3                        | c   |
| `do_cmd_change_name`             | `ui-player.c:1218-1309`      | `C` (and `ui-store.c:852`) | `player->` x32, `player` x10, `OPT()` x2          | c   |
| `textui_spell_browse`            | `ui-spell.c:334-349`         | `b` / `P`                  | `player->` x1, `player` x4                        | c   |
| `textui_target`                  | `ui-target.c:1023-1029`      | `*`                        | `player->` x20, `cave` x18, `z_info->` x3         | c   |
| `textui_target_closest`          | `ui-target.c:1037-1054`      | `'`                        | `player->` x8, `cave` x4, `z_info->` x4           | c   |

### Clean — hooks that read nothing from the core

| C function           | file:lines                 | Trigger     | Core globals read                          | Cat |
|----------------------|----------------------------|-------------|--------------------------------------------|-----|
| `textui_cmd_rest`    | `ui-command.c:191-222`     | `R`         | none — prompts, then `cmdq_push(CMD_REST)` | —   |
| `do_cmd_message_one` | `ui-knowledge.c:3709-3713` | `KTRL('O')` | none — message log only                    | —   |
| `do_cmd_pref`        | `ui-prefs.c:1473-1485`     | `"`         | none — pref file only                      | —   |
