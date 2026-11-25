# Code inventory (checklist edition)

**Date:** 2026-11-25

**What this is:** A package-by-package inventory of `src/main/java` (ANTLR-generated files excluded), built by a real Java parse. Level 1 is the package (one file per package, linked below), level 2 is the class file, and level 3 is every item in it: the type itself, nested types, constructors, methods, fields, enum constants and record components. Each item has a short outline, the dates read from its provenance stamp, and four tick boxes for the review passes. It sits beside the older `docs/code_inventory.md`, which it does not replace.

## Legend

- **Outline** is the first sentence of the item's Javadoc, with the provenance stamp removed. `No Javadoc.` means there is no block attached.
- **Coded** and **Commented** are read from the stamp (`coded on YYMMDD`, `coded before YYMMDD`, `commented in full on YYMMDD`). `unstamped` means a Javadoc block exists but carries no stamp.
- **Later edits** lists any further `YYMMDD` dates in the stamp paragraph (for example `updated on 261010`).
- Enum constants and record components are one grouped item per type, not one item each, with a count of how many are documented.
- Initialiser blocks are items too; they cannot carry Javadoc.
- Lambdas and anonymous-class members are not listed.

## Totals

- 631 files, 7114 items.

## Packages

- [ ] [`(source root)`](source_root.md) — 2 files, 12 items
- [ ] [`backend`](backend.md) — 1 files, 4 items
- [ ] [`backend/io`](backend_io.md) — 5 files, 66 items
- [ ] [`backend/io/bespokeexceptions`](backend_io_bespokeexceptions.md) — 1 files, 3 items
- [ ] [`backend/io/enums`](backend_io_enums.md) — 2 files, 4 items
- [ ] [`backend/io/savefiles`](backend_io_savefiles.md) — 3 files, 22 items
- [ ] [`backend/parser`](backend_parser.md) — 41 files, 204 items
- [ ] [`backend/parser/activation`](backend_parser_activation.md) — 2 files, 5 items
- [ ] [`backend/parser/artifact`](backend_parser_artifact.md) — 2 files, 7 items
- [ ] [`backend/parser/bloweffect`](backend_parser_bloweffect.md) — 2 files, 17 items
- [ ] [`backend/parser/blowmethod`](backend_parser_blowmethod.md) — 2 files, 5 items
- [ ] [`backend/parser/body`](backend_parser_body.md) — 2 files, 7 items
- [ ] [`backend/parser/brand`](backend_parser_brand.md) — 2 files, 5 items
- [ ] [`backend/parser/chesttrap`](backend_parser_chesttrap.md) — 2 files, 5 items
- [ ] [`backend/parser/curse`](backend_parser_curse.md) — 3 files, 8 items
- [ ] [`backend/parser/dungeonprofile`](backend_parser_dungeonprofile.md) — 5 files, 23 items
- [ ] [`backend/parser/egoitem`](backend_parser_egoitem.md) — 2 files, 7 items
- [ ] [`backend/parser/flavour`](backend_parser_flavour.md) — 4 files, 12 items
- [ ] [`backend/parser/gameconstants`](backend_parser_gameconstants.md) — 2 files, 31 items
- [ ] [`backend/parser/grammars`](backend_parser_grammars.md) — 4 files, 37 items
- [ ] [`backend/parser/hint`](backend_parser_hint.md) — 2 files, 4 items
- [ ] [`backend/parser/history`](backend_parser_history.md) — 2 files, 5 items
- [ ] [`backend/parser/itemobject`](backend_parser_itemobject.md) — 2 files, 5 items
- [ ] [`backend/parser/monster`](backend_parser_monster.md) — 2 files, 15 items
- [ ] [`backend/parser/monsterbase`](backend_parser_monsterbase.md) — 2 files, 5 items
- [ ] [`backend/parser/monsterspell`](backend_parser_monsterspell.md) — 3 files, 9 items
- [ ] [`backend/parser/names`](backend_parser_names.md) — 2 files, 5 items
- [ ] [`backend/parser/objectbase`](backend_parser_objectbase.md) — 2 files, 5 items
- [ ] [`backend/parser/objectproperty`](backend_parser_objectproperty.md) — 2 files, 24 items
- [ ] [`backend/parser/pain`](backend_parser_pain.md) — 2 files, 5 items
- [ ] [`backend/parser/pit`](backend_parser_pit.md) — 2 files, 5 items
- [ ] [`backend/parser/playerclass`](backend_parser_playerclass.md) — 10 files, 26 items
- [ ] [`backend/parser/playerproperty`](backend_parser_playerproperty.md) — 2 files, 6 items
- [ ] [`backend/parser/playerrace`](backend_parser_playerrace.md) — 2 files, 5 items
- [ ] [`backend/parser/playertimed`](backend_parser_playertimed.md) — 2 files, 10 items
- [ ] [`backend/parser/projection`](backend_parser_projection.md) — 2 files, 10 items
- [ ] [`backend/parser/quest`](backend_parser_quest.md) — 2 files, 10 items
- [ ] [`backend/parser/realm`](backend_parser_realm.md) — 2 files, 5 items
- [ ] [`backend/parser/roomprofile`](backend_parser_roomprofile.md) — 2 files, 5 items
- [ ] [`backend/parser/shape`](backend_parser_shape.md) — 2 files, 5 items
- [ ] [`backend/parser/slay`](backend_parser_slay.md) — 2 files, 5 items
- [ ] [`backend/parser/summon`](backend_parser_summon.md) — 2 files, 5 items
- [ ] [`backend/parser/terrainfeature`](backend_parser_terrainfeature.md) — 2 files, 5 items
- [ ] [`backend/parser/trap`](backend_parser_trap.md) — 2 files, 5 items
- [ ] [`backend/parser/vault`](backend_parser_vault.md) — 2 files, 16 items
- [ ] [`backend/parser/visuals`](backend_parser_visuals.md) — 4 files, 9 items
- [ ] [`backend/parser/world`](backend_parser_world.md) — 2 files, 14 items
- [ ] [`channel`](channel.md) — 8 files, 19 items
- [ ] [`channel/colour`](channel_colour.md) — 2 files, 17 items
- [ ] [`channel/corechannel`](channel_corechannel.md) — 2 files, 8 items
- [ ] [`channel/directories`](channel_directories.md) — 1 files, 29 items
- [ ] [`channel/enums`](channel_enums.md) — 8 files, 31 items
- [ ] [`channel/globals`](channel_globals.md) — 2 files, 12 items
- [ ] [`channel/messages`](channel_messages.md) — 3 files, 18 items
- [ ] [`channel/messages/data`](channel_messages_data.md) — 22 files, 137 items
- [ ] [`channel/parser`](channel_parser.md) — 7 files, 61 items
- [ ] [`channel/strings`](channel_strings.md) — 1 files, 17 items
- [ ] [`channel/uichannel`](channel_uichannel.md) — 5 files, 14 items
- [ ] [`channel/utils`](channel_utils.md) — 6 files, 117 items
- [ ] [`channel/utils/combiners`](channel_utils_combiners.md) — 10 files, 78 items
- [ ] [`frontend`](frontend.md) — 2 files, 31 items
- [ ] [`frontend/colour`](frontend_colour.md) — 5 files, 32 items
- [ ] [`frontend/entries`](frontend_entries.md) — 4 files, 116 items
- [ ] [`frontend/entries/enums`](frontend_entries_enums.md) — 2 files, 16 items
- [ ] [`frontend/events`](frontend_events.md) — 3 files, 15 items
- [ ] [`frontend/globals`](frontend_globals.md) — 1 files, 8 items
- [ ] [`frontend/inputfromuser`](frontend_inputfromuser.md) — 2 files, 19 items
- [ ] [`frontend/screen`](frontend_screen.md) — 5 files, 151 items
- [ ] [`frontend/screen/enums`](frontend_screen_enums.md) — 2 files, 4 items
- [ ] [`frontend/screen/grid`](frontend_screen_grid.md) — 5 files, 36 items
- [ ] [`frontend/screen/handlers`](frontend_screen_handlers.md) — 3 files, 46 items
- [ ] [`frontend/screen/hooks`](frontend_screen_hooks.md) — 6 files, 25 items
- [ ] [`frontend/screen/splash`](frontend_screen_splash.md) — 1 files, 12 items
- [ ] [`frontend/sounds`](frontend_sounds.md) — 1 files, 5 items
- [ ] [`frontend/ui`](frontend_ui.md) — 4 files, 209 items
- [ ] [`frontend/ui/entry`](frontend_ui_entry.md) — 2 files, 36 items
- [ ] [`frontend/ui/entry/assembler`](frontend_ui_entry_assembler.md) — 2 files, 11 items
- [ ] [`frontend/ui/entry/assembler/helperfunctions`](frontend_ui_entry_assembler_helperfunctions.md) — 3 files, 28 items
- [ ] [`frontend/ui/entry/reader`](frontend_ui_entry_reader.md) — 1 files, 5 items
- [ ] [`frontend/ui/entrybase/assembler`](frontend_ui_entrybase_assembler.md) — 2 files, 6 items
- [ ] [`frontend/ui/entrybase/reader`](frontend_ui_entrybase_reader.md) — 1 files, 5 items
- [ ] [`frontend/ui/entryrenderer/assembler`](frontend_ui_entryrenderer_assembler.md) — 2 files, 6 items
- [ ] [`frontend/ui/entryrenderer/reader`](frontend_ui_entryrenderer_reader.md) — 1 files, 6 items
- [ ] [`frontend/ui/globals`](frontend_ui_globals.md) — 2 files, 22 items
- [ ] [`frontend/ui/output`](frontend_ui_output.md) — 1 files, 15 items
- [ ] [`frontend/ui/player`](frontend_ui_player.md) — 2 files, 31 items
- [ ] [`middle`](middle.md) — 3 files, 49 items
- [ ] [`middle/cave`](middle_cave.md) — 17 files, 489 items
- [ ] [`middle/cave/chunkbuilders`](middle_cave_chunkbuilders.md) — 11 files, 28 items
- [ ] [`middle/cave/enums`](middle_cave_enums.md) — 7 files, 36 items
- [ ] [`middle/cave/profiles/dungeon`](middle_cave_profiles_dungeon.md) — 4 files, 69 items
- [ ] [`middle/cave/profiles/room`](middle_cave_profiles_room.md) — 1 files, 22 items
- [ ] [`middle/cave/profiles/vault`](middle_cave_profiles_vault.md) — 1 files, 32 items
- [ ] [`middle/cave/roombuilders`](middle_cave_roombuilders.md) — 21 files, 55 items
- [ ] [`middle/cave/store`](middle_cave_store.md) — 1 files, 3 items
- [ ] [`middle/combat`](middle_combat.md) — 3 files, 41 items
- [ ] [`middle/combat/enums`](middle_combat_enums.md) — 3 files, 6 items
- [ ] [`middle/effect`](middle_effect.md) — 7 files, 96 items
- [ ] [`middle/enums`](middle_enums.md) — 11 files, 52 items
- [ ] [`middle/game`](middle_game.md) — 4 files, 42 items
- [ ] [`middle/game/enums`](middle_game_enums.md) — 4 files, 12 items
- [ ] [`middle/game/event`](middle_game_event.md) — 4 files, 51 items
- [ ] [`middle/game/event/birthhandlers`](middle_game_event_birthhandlers.md) — 1 files, 7 items
- [ ] [`middle/game/event/eventhandlers`](middle_game_event_eventhandlers.md) — 2 files, 21 items
- [ ] [`middle/game/event/projection`](middle_game_event_projection.md) — 4 files, 44 items
- [ ] [`middle/game/gameengine`](middle_game_gameengine.md) — 13 files, 152 items
- [ ] [`middle/game/gameengine/argumentdata`](middle_game_gameengine_argumentdata.md) — 7 files, 34 items
- [ ] [`middle/game/globals`](middle_game_globals.md) — 1 files, 114 items
- [ ] [`middle/game/globals/cached`](middle_game_globals_cached.md) — 1 files, 13 items
- [ ] [`middle/game/globals/data`](middle_game_globals_data.md) — 18 files, 218 items
- [ ] [`middle/game/globals/loaders`](middle_game_globals_loaders.md) — 7 files, 55 items
- [ ] [`middle/game/globals/registry`](middle_game_globals_registry.md) — 10 files, 299 items
- [ ] [`middle/gameinput`](middle_gameinput.md) — 4 files, 49 items
- [ ] [`middle/magic`](middle_magic.md) — 4 files, 59 items
- [ ] [`middle/monsters`](middle_monsters.md) — 23 files, 362 items
- [ ] [`middle/monsters/enums`](middle_monsters_enums.md) — 13 files, 58 items
- [ ] [`middle/numerics`](middle_numerics.md) — 6 files, 90 items
- [ ] [`middle/objects`](middle_objects.md) — 35 files, 1052 items
- [ ] [`middle/objects/enums`](middle_objects_enums.md) — 24 files, 181 items
- [ ] [`middle/player`](middle_player.md) — 32 files, 895 items
- [ ] [`middle/player/enums`](middle_player_enums.md) — 14 files, 65 items
- [ ] [`middle/strings`](middle_strings.md) — 3 files, 35 items
- [ ] [`middle/utils`](middle_utils.md) — 2 files, 18 items
- [ ] [`middle/utils/quit`](middle_utils_quit.md) — 2 files, 7 items
- [ ] [`unused`](unused.md) — 7 files, 37 items

## Last merge

- 8 ticked boxes carried over onto matching items.
- 0 ticked items had no match in the new inventory (renamed, moved or deleted); their ticks were dropped.
- 0 ticked items matched but their outline or stamp dates have changed since they were ticked; the ticks were kept, so re-check these.
