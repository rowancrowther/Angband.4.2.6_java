# Primer: the embryonic UI entry

*Covers `src/ui-entry.c` in the original C (`parse_entry_name`, `hatch_embryo`, and the
`name_parameters`/`priority_schemes` tables). Written against the port's
`UIEntryAssembler`/`UIEntryReader` (`uk.co.jackoftradesltd.frontend.ui.entry`), which is where this mechanism needs a
home.*

A `ui_entry.txt` record is not written to the real entries table line by line as it's read. Each record is built up in a
scratch struct first — the *embryo* — and only turned into one or more real entries once the parser knows the record is
finished. That single design choice is what lets a later "specialization" block edit an entry an earlier "generator"
block already created, instead of colliding with it.

## The embryo in one paragraph

The parser holds exactly one embryo at a time, in its private state. Every field directive (`parameter:`, `renderer:`,
`combine:`, `priority:`, `category:`, `label:`, `template:`, `flags:`,
`desc:`) just mutates whichever embryo is currently in flight. Nothing is finalized, and nothing is visible to the rest
of the table, until the embryo is *hatched*.

```
   name: A          field lines mutate        name: B (different)         hatch
  ──────────▶  [ embryo for A in flight ] ───────────────────────▶  [ hatch A ] ──▶ [ embryo for B ]
                                                                          │
                                            one or more real entries ◀───┘
                                              land in the table here
```

## What a `name:` line does

Firing a new `name:` line is the trigger that closes off whatever embryo came before it (see *Hatching*, below) and
opens the next one. Opening the next one means:

- **Look the name up in the already-committed entries table.**
    - *Found* → pull that real entry back into embryo form and mark it `exists`. This is the specialization path — a
      block further down the file that's about to edit something a generator already produced.
    - *Not found* → allocate a blank entry and mark it not-`exists`.
- **Reset the embryo-only scratch** — its working category list, the parameter index, the priority-scheme index. None of
  that bookkeeping survives past one record; it's rebuilt from nothing every time a name line is seen.

Two odd cases sit either side of this: the *very first* `name:` line has no prior embryo to close, and a `name:` line
repeating the *same* name as the embryo already in flight is treated as a no-op continuation rather than a new record.

## Hatching

Hatching runs once per record, triggered by the next differently-named `name:` line or by end of file. What it does
branches three ways on state the embryo has been carrying all along:

- **Already `exists`.** The fields just parsed were mutating the same struct that's already sitting in the entries
  table — there is nothing left to insert. The category list is finalized in place and hatching is done. This is the
  merge step: it's *why* a specialization block can add a label and a category to an entry a generator already made,
  instead of producing a second, competing entry with the same name.
- **Not `exists`, unparameterized.** The single new entry is finalized as itself and inserted once.
- **Not `exists`, parameterized** (`parameter:stat` / `parameter:element`). The record expands:
    - Loop over every name the parameter kind supplies (the stat names, or the element names).
    - For all but the last: clone the embryo's fields into a *brand-new* entry named `base<TAG>`, give it its own
      priority (from the priority scheme) and its own category list, and insert it immediately.
    - For the last one: reuse the embryo struct itself — rename it to `base<LAST_TAG>` rather than allocating one more
      copy — and insert that.

Either way, the embryo's scratch category list is freed once hatching finishes.

## Category name lookup: two structs, never `entry.name`

Categories get their own name-lookup, separate from the entry-name lookup in *What a `name:` line does* above, and it's
easy to conflate the two. There is no struct called `embryonic_ui_category` in the C source — the one in play is
`struct embryonic_category_reference` (`ui-entry.c:163-168`):

```c
struct embryonic_category_reference {
    const char *name;
    int psource_index;
    int priority;
    bool priority_set;
};
```

It does carry a `name` field (line 164) — one per *category*, not the entry's own name. The already- hatched side has a
second, distinct struct for the same purpose, `struct category_reference`
(`ui-entry.c:55-59`), also with its own `name` field.

`search_embryo_categories` (`ui-entry.c:1377-1408`) is where a category name gets matched, and it branches on the same
`embryo->exists` flag hatching does:

- **Not `exists`** (a fresh, unhatched embryo — the generator path): binary-searches directly over
  `embryo->categories[imid].name` (`ui-entry.c:1397`) — the `embryonic_category_reference` array's own `name` field.
- **Already `exists`** (editing an already-committed entry — the specialization path): delegates to
  `ui_entry_search_categories(embryo->entry, name, ind)` (`ui-entry.c:1383`), which binary-searches
  `entry->categories[imid].name` (`ui-entry.c:1264`) — the *real*, hatched entry's
  `category_reference` array.

Either branch compares one category name against another category name. Neither ever touches the entry's own `name`
field — that field belongs to a completely different lookup,
`ui_entry_lookup(name)` in `parse_entry_name` (`ui-entry.c:1906`), which is what finds the *entry*
itself (the mechanism in the previous section), not one of its categories.

The port already has the post-hatch half of this split: `UIEntry.uiEntrySearchCategories` (ported from
`ui_entry_search_categories`) walks `UIEntryCategory` objects by their own name field, same as
`category_reference.name` here. It's the *embryo*-side counterpart (`embryonic_category_reference`/pre-hatch) that has
no Java equivalent yet — consistent with the gap below, since neither half of the embryo mechanism has been ported.

## Why this matters for the port

This lookup-and-merge step is the piece that makes generator + specialization records resolve to *one* entry apiece in
C. A generator (`parameter:element`, no tag) expands into one entry per element; a specialization further down the file
(`name:base<TAG>`, no `parameter:` line) is meant to find the entry the generator already made for that tag and layer
its own label/category on top — never to create a second entry under the same name.

The current port assembles each `UIEntryParseRecord` independently, with no name lookup against already-produced
entries. Running the real `lib/gamedata/ui_entry.txt` through it confirms the gap directly: every resist-rune element
(`ACID`, `ELEC`, `FIRE`, `COLD`, `POIS`, `LIGHT`, `DARK`,
`SOUND`, `SHARD`, `NEXUS`, `NETHER`, `CHAOS`, `DISEN`) ends up as **two** `UIEntry` objects with the same name — one
from the generator's expansion (renderer/combiner set, no label), one from the specialization (label/category set, no
renderer/combiner) — where C would have merged them into one. Nothing currently plays the role of `exists`
/lookup-and-edit.

## Where to look

| Concept                                              | C (`src/ui-entry.c`)                                               |
|------------------------------------------------------|--------------------------------------------------------------------|
| The embryo struct and its lifetime                   | `struct embryonic_ui_entry`, `parse_entry_name` (1881–1942)        |
| Lookup-and-edit-existing (`exists`)                  | `parse_entry_name`, the `ui_entry_lookup(name)` branch (1906–1913) |
| Hatching and its three branches                      | `hatch_embryo` (1755–1862)                                         |
| The `""`/`"stat"`/`"element"` parameter table        | `name_parameters[]` (142–146)                                      |
| The `""`/`"index"`/`"negative_index"` priority table | `priority_schemes[]` (157 onward)                                  |
| Pre-hatch category name + priority                   | `struct embryonic_category_reference` (163–168)                    |
| Post-hatch category name + priority                  | `struct category_reference` (55–59)                                |
| Category name lookup, both sides of `exists`         | `search_embryo_categories` (1377–1408)                             |
| The real entry's own category-name search            | `ui_entry_search_categories` (1250–1275)                           |

There is no equivalent table on the Java side yet — `UIEntryAssembler`/`UIEntryReader` is where the lookup-and-edit step
would need to land.
