# Stage 1 follow-up — GameConstants load order and UIEntryAssembler expansion

Answers to three questions raised after the 2026-09-18 stage-1 verification pass over
`ChannelEntryFlag.java`, `CombinerName.java`, `Core.java`, `GameConstants.java`,
`KnownObject.java`, `ObjectProperty.java`, `ObjectValueCache.java`, `UIEntry.java`,
`UIEntryAssembler.java`, `UIEntryBase.java`, `UIEntryBaseAssembler.java`,
`UIEntryBaseParseRecord.java`, `UIEntryCode.java`, `UIEntryRendererEnum.java`,
`UIEntrySpec.java`, `UIEntryValue.java`, `UIEntryValueShapshot.java`, `UILoop.java`,
`UIMessage.java`. `ChannelEntryFlag`'s `ENTRY_FLAG_TEMPLATE_ONLY` placement is settled — staying in the same enum — and
is not revisited here.

## 1) Does the pain/monster_base/slay/brand load order make any difference to the data?

No, not with the game data currently shipped — but it is a real ordering deviation, not a false positive.

- C's loader table (`init.c:4348-4394`, `pl[]`) runs `slays` → `brands` →
  `monster pain messages` → `monster bases`.
- `GameConstants.java:245-256` (`init()`) runs them the other way round: `pain.txt` →
  `monster_base.txt` → `slay.txt` → `brand.txt`.
- The only place load order could matter is `parse_slay_base`
  (`obj-init.c:714-730`), which calls `lookup_monster_base(base_name)` while parsing a slay's `base:` line — if monster
  bases haven't been loaded yet, a `base:` line would fail to resolve. That's the one dependency the C order (slay
  before monster base)
  would, on the face of it, get backwards.
- In practice it doesn't come up: `lib/gamedata/slay.txt` (both the upstream C copy and the Java repo's copy) contains
  no `base:` lines at all — every slay entry uses
  `race-flag:` instead. `parse_slay_base` is registered but never invoked by the shipped data.

So today the reversed order is inert. It would only start to matter if a future
`slay.txt` edit added a `base:` line — at that point the Java loader would need
`monster_base.txt` loaded first, exactly as C requires, and the current order would break it silently (parse error)
rather than the reverse. Worth keeping in mind if
`slay.txt` is ever extended, but not something that needs fixing on the strength of the data as it stands.

## 2) Why should the UIEntryAssembler `parameter` be expanded?

Because `parameter:element` is live, in-use game data, not a theoretical case like the
`stat` expansion already gets — and without it, the resistance panel's UI entries don't get created.

C's `name_parameters[]` table (`ui-entry.c:142-146`) has three parameterisation modes: no expansion (dummy),
`"element"`, and `"stat"`. `hatch_embryo` (`ui-entry.c:1755-1855`)
expands whichever mode a record names into one `ui_entry` per item the mode's
`count_func`/`ith_name_func` pair produces, each named `<entry-name><PARAM>`. The port already does this for `stat`
(`UIEntryAssembler.java:198-215`, one entry per player stat,
`<STR>` through `<CON>`) but treats anything that isn't `stat` — including `element` — as unparameterised
(`UIEntryAssembler.java:189`, the `if (statElemType != STAT)` branch), so it produces exactly one un-tagged entry
instead.

The live case this breaks: `lib/gamedata/ui_entry.txt:85-86` declares

```
name:resist_ui_compact_0
parameter:element
renderer:char_screen1_resist_renderer
combine:RESIST_0
priority:negative_index
category:resistances
flags:TIMED_AS_AUX
```

In C, this expands into one entry per resistible element — `resist_ui_compact_0<ACID>`,
`<ELEC>`, `<FIRE>`, `<COLD>`, `<POIS>`, `<LIGHT>`, `<DARK>`, `<SOUND>`, `<SHARD>`,
`<NEXUS>`, `<NETHER>`, `<CHAOS>`, `<DISEN>` (13 of the 25 entries in
`list-elements.h`/`ElementEnum`, matching `ELEM_HIGH_MAX`). The file then immediately specialises each of those 13
generated entries by name — `ui_entry.txt:101` onward has a
`name:resist_ui_compact_0<ACID>` block that sets its label, categories, etc., and the same for each other element. This
is exactly the pattern the class Javadoc already describes for `stat`: "a later `bindui` look-up by the full tagged name
resolve[s]" against the expanded, tagged entry.

Without element expansion, `resist_ui_compact_0` in the Java port produces a single entry named `resist_ui_compact_0`
(no tag). The 13 `name:resist_ui_compact_0<ACID>`-style specialisation records that follow it in the same file have
nothing matching to attach to — there is no `resist_ui_compact_0<ACID>` entry for them to find. That's the second
character screen's resistance table (13 rows, one per displayed element): as things stand it has no backing `UIEntry`
objects to bind against, which is a functional gap, not just a documentation one. The class Javadoc already flags this
honestly as "not yet ported" rather than claiming full coverage, which is why stage 1 treated it as a known,
already-declared gap rather than a silent one.

## 3) The stat-expansion loop guard, expanded

`UIEntryAssembler.java:196-198`:

```java
for (int i = 0; i < 5; i++) {
    int newPriorityNum;
    if (!PlayerEventStatusUpdate.getPlayerStatusView().statString()[i].isEmpty()) {
        ...
    }
}
```

C's equivalent (`hatch_embryo`, `ui-entry.c:1770-1782`) has no such check — it expands unconditionally for `i = 0` to
`get_stat_count() - 1` (5), calling
`priority_schemes[...].priority(i)` and `name_parameters[...].ith_name_func(i)` every time, with no way for an iteration
to be skipped. The Java loop adds an
`if (!statString()[i].isEmpty())` condition around entry creation that has no counterpart in the C source at all.

Whether this is currently a problem depends entirely on `statString()`, so I checked it:
`PlayerEventStatusUpdate.java:69` seeds `cachedPlayerStatusView` at class load with a hardcoded literal,
`{"STR", "INT", "WIS", "DEX", "CON"}` — always exactly 5 entries, none ever empty, and every other read in that file
(`PlayerEventStatusUpdate.java:141` onward, 14+ call sites) reads the same cached array back unchanged. So
`statString()[i]` is never empty for `i` in `0..4`, and the guard never actually skips an iteration today — all 5 stat
entries get created every time, same as C.

The gap is structural rather than a live bug: the guard is a condition C doesn't have, sitting on top of an otherwise
exact port of the priority-scheme logic (`get_priority_from_index`/`get_priority_from_negative_index`,
`ui-entry.c:1639-1652`, confirmed identical). It would only start to diverge from C if
`statString()` ever became data-driven or otherwise capable of holding a blank entry — at that point C would still emit
an (unlabelled) entry for that stat and Java would silently drop it. Not a blocker against the current hardcoded data,
but worth knowing the guard is there if `PlayerEventStatusUpdate`'s stat-name source ever changes.
