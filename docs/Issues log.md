# Issues to deal with #

## To deal with ##

## Dealt with ##

- [X] 26-11-02 Fold `UIEntryBase` into `UIEntry`, as decided 26-09-19 (`docs/precis/260918.md`, section "The registry is
shared across both files"). C keeps one `entries[]` array, so `name:` and `template:` both search the same list. The type
is deleted, `UIRegistry` holds the one list, `UIEntry.template` is a `UIEntry`, and `UIEntryBaseAssembler` and
`UIEntryBaseReader` return `List<UIEntry>`, with `UIDataLoader.loadUIEntryBases` doing the registering. Checklist and
remaining detail in `docs/UIEntryBase_Removal.md`. `/port` of `UIRegistry.java`, which failed stage 1 on 26-09-29 because of
the split, was re-run on 26-11-02.

- [X] 26-08-10-12-16 Moving `GameEventType` into channel is a bigger move than it looks. It's referenced across middle, so
it's a stage-0-style import churn on top of stage 1's new code. Worth doing as its own commit before the records, so a
compile failure tells you which of the two broke.

- [X] 26-10-11 Store a pit's `innate-freq:` as `100 / pct`, as C does. `parse_pit_innate_freq` (`mon-init.c`) rejects a
percentage outside 1 to 100 and stores `100 / pct` in `pit_profile.freq_innate`, the same convention as a race's
`freq_innate`. `PitAssembler.java` parses the integer and passes it to `PitProfile` as written, with no range check and
no division. Found while fixing the race side of the same rule in `MonsterAssembler.java`.

- [X] 26-10-11 Scale a race's `hearing:` by `max_sight / 20`. `parse_monster_hearing` (`mon-init.c`) stores
`parser_getint(p, "hearing") * z_info->max_sight / 20`, with a comment that it assumes `max_sight` is 20. The Java
`MonsterAssembler.java` parses the value and passes it to `MonsterRace` unscaled. The two agree only while
`player:max-sight` in `constants.txt` is 20, which it is in the shipped file.

- [X] 26-10-11 `MonsterSpell.java` has 19 breath constants that C does not. `RSF_ACID` to `RSF_MANA` repeat the breaths
that `RSF_BR_ACID` to `RSF_BR_MANA` already hold, with identical types (`RST_BREATH`, `RST_INNATE`), and nothing in
the main code reads the short names: the monster and pit assemblers build the constant as `"RSF_" + code`, so
`BR_ACID` in a data file always reaches the `RSF_BR_` one. The enum has 112 constants where `list-mon-spells.h` has 93
plus `NONE` and `MAX`, so every ordinal after the block is 19 too high.

- [X] 26-10-10 Implement the lore structure that sits behind the lore file. C's `finish_parse_monster` (`mon-init.c`)
  allocates `l_list`, one zeroed `monster_lore` per race with its `blows` and `blow_known` arrays, and `get_lore`
  (`mon-lore.c`) returns a record from it for every race. The Java has no such allocation: nothing in main code calls
  `MonsterRace.setLore`, so `getLore()` returns null for every race loaded from `monster.txt`. Found by `/port` of
  `MonsterRace.java` on 26-10-10.