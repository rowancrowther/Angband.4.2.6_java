# `initialize_ui_entry_iterator`

C source: `src/ui-entry.c:459-479` (doc comment `:452-458`), declared `src/ui-entry.h:26-27`.

## Purpose

Builds a filtered, sorted *view* over the global UI-entry table — not a copy of the data, a list of pointers into it —
for one particular screen/query. The caller gets an opaque
`struct ui_entry_iterator*`, walks it, then releases it.

Three inputs shape the view:

- **predicate** — which entries qualify
- **closure** — context the predicate needs to decide
- **sortcategory** — which category's priority number governs display order

## Signature

```c
struct ui_entry_iterator *initialize_ui_entry_iterator(
    ui_entry_predicate predicate, void *closure, const char *sortcategory)
```

`ui_entry_predicate` is `typedef bool (*)(const struct ui_entry *entry, void *closure)`
(`src/ui-entry.h:15`).

## Step by step — `src/ui-entry.c:459-479`

1. `mem_alloc` the iterator struct itself, then `mem_alloc` its `entries` array sized to `n_entry` — the *total* count
   of entries ever registered (`src/ui-entry.c:182`), an upper bound, not the eventual size. Allocate worst-case once;
   track the real count separately in `result->n` rather than reallocating.
2. `result->n = 0; result->i = 0;` — `n` is "how many survived the filter", `i` is "how far the caller has advanced".
   `reset_ui_entry_iterator` (`:497-500`) just resets `i` back to 0, without re-filtering.
3. Loop over every registered entry (`entries[i]`, `i` from 0 to `n_entry`):
    - Skip it outright if it carries `ENTRY_FLAG_TEMPLATE_ONLY` (`:469`) — template-only entries exist to be inherited
      from, not displayed, so they're never eligible regardless of what the predicate says.
    - Otherwise call `(*predicate)(entries[i], closure)`. If true, copy the pointer into
      `result->entries[result->n]` and increment `result->n`.
4. Set the file-scope `category_for_cmp_desc_prio = sortcategory` (`:475`) — this is how
   `sortcategory` reaches the comparator, since C's `qsort`-style `sort()` only accepts a two-argument compare function
   with no extra context parameter. This function primes the global right before sorting.
5. `sort(result->entries, result->n, ...)` — sorts just the live prefix (`result->n` elements), using `cmp_desc_prio`.
6. Return the iterator. The caller drives it with `count_ui_entry_iterator` (elements left,
   `n - i`) and `advance_ui_entry_iterator` (returns `entries[i]`, then `++i`) until exhausted, then must call
   `release_ui_entry_iterator` to free both allocations.

## The sort — `cmp_desc_prio`, `src/ui-entry.c:389-437`

- For each of the two entries being compared, it searches *that entry's own category list* for one named `sortcategory`
  (`ui_entry_search_categories`, `:120` / `:1247`) and, if found, reads that category's `priority` field.
- Higher priority sorts first (descending) — C convention: `cmp` returns `-1` when *left* should come first, so "left
  priority > right priority" → `-1`.
- An entry that isn't even *in* `sortcategory` is pushed to the end (treated as lower priority than any entry that is);
  two entries with no priority distinction (or neither in the category)
  tie-break alphabetically by `entry->name` (`strcmp`).
- This is the mechanism behind the per-entry `struct category_reference` array (`:55-59`,
  `name`/`priority`/`priority_set`) — the same entry can rank differently depending on *which*
  category you sort by, because each category it belongs to carries its own priority number.

## Real caller — `src/ui-player.c:176-243`

- `check_for_two_categories` is the predicate: closure is a 2-element `const char*[]`, returning true only if the entry
  belongs to *both* named categories (e.g. `"CHAR_SCREEN1"` and
  `"stat_modifiers"`).
- `sortcategory` passed in is the *second* of those two names — so the call is "give me everything tagged for this
  screen AND this subgroup, ordered by how that subgroup ranks it." The same predicate/closure pattern is called four
  times in a loop with `region_categories[i]` (`:236`) — one iterator per screen region (resistances, abilities,
  hindrances, modifiers), each sorted by its own region's priority scheme.

## Java port — open design points

Not yet implemented; stubbed at `UIEntryCode.initialiseUIEntryIterator()`
(`src/main/java/uk/co/jackoftradesltd/frontend/ui/UIEntryCode.java` — moved here from
`frontend/ui/entry/` during the registry move below; `UIEntryPredicate` moved the same way, to
`frontend/ui/UIEntryPredicate.java`), called with no arguments from `UIPlayer.configureCharSheet()`
(`src/main/java/uk/co/jackoftradesltd/frontend/ui/UIPlayer.java`).

- **Entry pool**: `UIRegistry.getUIEntries()`
  (`src/main/java/uk/co/jackoftradesltd/middle/game/globals/registry/UIRegistry.java:90`) is the Java stand-in for C's
  static `entries`/`n_entry` globals — the method should read from there rather than take the entry list as a parameter.
- **Template-only check**: `UIEntry.getEntryFlag()` is a single `EntryFlag`, not a bitmask, so the check is
  `== EntryFlag.ENTRY_FLAG_TEMPLATE_ONLY`, not a bitwise test.
- **Predicate type**: `UIEntryPredicate` currently exists as an empty interface stub
  (`src/main/java/uk/co/jackoftradesltd/frontend/ui/entry/UIEntryPredicate.java`). A generic
  `boolean test(UIEntry entry, T closure)` would let the closure be concretely typed per call site (e.g.
  `UIEntryPredicate<String[]>`), which is safer than C's `void *` cast.
- **Blocker**: `cmp_desc_prio` sorts by a priority *specific to `sortcategory`*, looked up per-entry via
  `struct category_reference` (`src/ui-entry.c:55-59`). `UIEntry` currently models categories as `List<String>` with one
  flat `priorityNum`
  (`src/main/java/uk/co/jackoftradesltd/frontend/entries/UIEntry.java:60,84`) — there is no per-category priority to
  look up, so two entries sorted under two different `sortcategory` values can't come out in different relative orders
  yet. This is a data-model decision on `UIEntry`/its parser, not something this function's signature can work around,
  and needs resolving before the sort step can be wired up correctly.

## Java port — should `UIRegistry` move to `channel`?

`UIEntryCode.initialiseUIEntryIterator()` needs to read the registered entries — C's static
`entries`/`n_entry` (`src/ui-entry.c:182-184`). The Java equivalent is `UIRegistry.getUIEntries()`
(`src/main/java/uk/co/jackoftradesltd/middle/game/globals/registry/UIRegistry.java:90`), but
`UIRegistry` lives in `middle`, and `frontend` may only import `channel`
(`docs/Architecture_migration.md`, stage 5, boundary rule 1). Moving `UIRegistry` into `channel` to close that gap is
the wrong fix.

**`channel` is wire vocabulary, not "anything both halves want".** The project's own test
(`docs/Architecture_migration.md:718-720`): does the type belong in `channel` because it is *"a word the messages are
written in — something the UI must understand to render"*? `ColourEnum` and
`MessageType` pass; `UIRegistry` never crosses a channel message at all, so it fails outright.

**This exact situation is already diagnosed, with a named fix.** `BoundaryTest.java:169-182` (the
`MIDDLE_BASELINE` doc comment) states it directly: C does not have this problem because
`ui_entry.txt` is parsed *inside* `ui-entry.c` — `struct ui_entry` is an incomplete type in
`ui-entry.h`, so nothing outside the front end can see its fields even in principle. *"Moving the readers, assemblers
and registries to match is the fix for this entire baseline, and it is chapter-sized."*

Concretely, `UIRegistry`, its loader (`middle/game/globals/loaders/UIDataLoader.java`), and the six readers/assemblers
(`backend/parser/UIEntry{,Base,Renderer}Reader.java`,
`backend/parser/uientry{,base,renderer}/…Assembler.java`) all still sit in `middle`/`backend` purely because that's
where they were ported to first, not because the core needs them — every one of them already names
`frontend.entries.UIEntry`/`UIEntryBase`/`UIEntryRenderer`, and each crossing is individually listed as an accepted,
dated entry in `MIDDLE_BASELINE`/`BACKEND_BASELINE`
(`BoundaryTest.java:184-231`). The domain classes have already made the move — `UIEntry`,
`UIEntryBase`, `UIEntryRenderer`, `UIEntryIterator` all live under `frontend.entries` today. The registry and its
loader/readers/assemblers are the piece left behind, and `Architecture_migration.md`
had already parked it as an unstarted marker (`:676-679`) before this function surfaced it as a live blocker.

**Why this is live, not just tidy.** `UIEntryCode` calling `UIRegistry.getUIEntries()` as currently planned would be a
*new* rule-1 crossing (frontend naming middle) with no entry in
`FRONTEND_BASELINE` (`BoundaryTest.java:160-167`) — it would fail the boundary test outright, not just look
architecturally odd. Moving `UIRegistry` to `channel` would make the test pass by misclassifying frontend-only data as
shared vocabulary, which the project's own test for `channel`
membership already rules out.

**Indicated move**: `UIRegistry` + `UIDataLoader` + the six readers/assemblers into `frontend`, alongside the domain
classes they already build — closing the `MIDDLE_BASELINE`/`BACKEND_BASELINE`
entries for UI-entry data and letting `UIEntryCode` read the registry with no crossing at all, matching C's opaque
`struct ui_entry`. Not yet done; a chapter-sized move across many files, not something to fold into this one function's
port.

## Plan — moving the UI-entry registry stack to `frontend.ui.entry`

### Scope as named

Eight files, all currently in `middle`/`backend`:

- `middle/game/globals/registry/UIRegistry.java`
- `middle/game/globals/loaders/UIDataLoader.java`
- `backend/parser/UIEntryReader.java`, `UIEntryBaseReader.java`, `UIEntryRendererReader.java`
- `backend/parser/uientry/UIEntryAssembler.java`, `uientrybase/UIEntryBaseAssembler.java`,
  `uientryrenderer/UIEntryRendererAssembler.java`

### A complication found while checking the imports

Moving exactly these eight to `frontend.ui.entry` does not close the boundary — it swaps one crossing for another.
Checked this turn, by reading the actual `import` lists:

- **The three `*Reader` classes depend on generic parsing infrastructure**, not UI-entry-specific code: `Reader<T>`
  (`backend/parser/Reader.java`, **40 implementers** across the codebase — every data-file domain), `Assembler<X,Y>`
  (`backend/parser/Assembler.java`, **51 implementers**),
  `GrammarDriver`, and `ParseResult<T>` — all in `backend.parser`. If the readers move to
  `frontend` unchanged, `frontend` starts importing these `backend` interfaces, which is the same rule-1 violation as
  importing `middle`, just against a different package.
- **`UIDataLoader` depends on `ErrorParsing`** (`middle/game/globals/loaders/ErrorParsing.java`, 9 callers, all loaders
  in that package) for its soft-error reporting. Moving `UIDataLoader` alone would leave it calling back into `middle`
  for this.
- **Each reader also names its own ANTLR grammar classes** — `backend.parser.grammars.uientry.*`,
  `.uientrybase.*`, `.uientryrenderer.*` (`UIEntry{,Base,Renderer}Lexer`/`Grammar`). Unlike
  `Reader`/`Assembler`, these *are* UI-entry-specific — no other domain's reader touches them — so they are not the same
  kind of problem, but they still have to go somewhere.
- **The three `*ParseRecord` classes** (`uientry/UIEntryParseRecord.java` etc.) import nothing but
  `java.util.List` and are used only by their matching reader/assembler pair — clean to move with them, no complication.

So the honest scope is bigger than eight files, and it splits into two different kinds of move:

| Kind                                                | Files                                                                            | Where                                                                                                  |
|-----------------------------------------------------|----------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| UI-entry-specific                                   | the 8 named + 3 `*ParseRecord` + 3 ANTLR grammar sub-packages                    | `frontend.ui.entry` (or a sub-package per grammar, mirroring `backend.parser.grammars.uientry*` today) |
| Generic, domain-agnostic, now needed by both halves | `Reader<T>`, `Assembler<X,Y>`, `GrammarDriver`, `ParseResult<T>`, `ErrorParsing` | candidate for `channel` — see below                                                                    |

### Why the generic plumbing is a `channel` question, not a `backend` question

`Architecture_migration.md:722-738` already established that `channel` membership has a second door besides "wire
payload vocabulary": *shared vocabulary both halves must agree on* — that's how
`AngbandDirs` and `Angband` got there, neither of which rides a message either. `Reader`,
`Assembler`, `GrammarDriver`, `ParseResult`, and `ErrorParsing` fit that door once `frontend` has its own readers: every
domain's parser, on both sides of the boundary, needs to agree on the same parsing contract. This mirrors C more closely
than it first looks — `ui-entry.c`'s own file parser is registered into `init.c`'s dispatch table and built on the same
shared `parser.c` primitives every other C data-file parser uses; the front end doing its own IO was never meant to mean
the front end reinventing IO.

Leaving these five in `backend` and granting `frontend` a named exception to import them would also work and costs less
motion — but it reopens the "backend is the IO layer" rule (`Architecture_migration.md` note 6) for a boundary-rule
carve-out rather than a `channel` move, and the project has consistently preferred moving the type over widening the
rule. Rowan's call to make before starting, not assumed here.

### Proposed steps

Modelled on stage 0's own playbook (`Architecture_migration.md:173-229`), which already did a move of this shape once,
successfully:

1. **Decide the package layout** — flat `frontend.ui.entry`, or sub-packages mirroring today's split
   (`frontend.ui.entry.reader`, `.assembler`, `.grammars.uientry` etc.)? `UIEntryCode` and
   `UIEntryPredicate` already sit at the flat `frontend.ui.entry` level; `UIEntry`/`UIEntryBase`/
   `UIEntryRenderer`/`UIEntryIterator` sit one level up, in `frontend.entries`. Worth deciding whether the registry
   stack joins them there instead, so UI-entry data has one home rather than two neighbouring ones.
2. **Decide where the five generic-plumbing types land** — `channel` (this section's proposal) or stay in `backend`/
   `middle` with a documented `FRONTEND_BASELINE` exception. This gates step 3.
3. **Move**, via IDE "Move Class" (stage 0's method — a pure move, verified by compiling, no logic touched): the 8 named
   files + 3 `ParseRecord`s + 3 grammar sub-packages to their decided new homes; the 5 generic-plumbing types to theirs.
4. **Fix the call sites** that named the old locations — `GameConstants.java` (calls
   `UIDataLoader.loadUIEntry*`), `ObjectPropertyAssembler.java` and `PlayerPropertyAssembler.java`
   (call `UIRegistry.getUIEntry(name)` — see "left alone" below).
5. **Claude: move the matching tests** to mirror the new packages — `UIEntryReaderTest`,
   `UIEntryBaseReaderTest`, `UIEntryRendererReaderTest`, `UIEntryAssemblerTest`,
   `UIEntryBaseAssemblerTest`, `UIEntryRendererAssemblerTest`, the two grammar tests (`UIEntryGrammarTest`,
   `UIEntryBaseGrammarTest`), and `RegistrySeeding.java` (a shared test helper naming `UIRegistry` directly).
6. **Update `BoundaryTest.java`**: delete the now-closed `MIDDLE_BASELINE`/`BACKEND_BASELINE`
   entries for `UIRegistry`, `UIDataLoader`, and the three assemblers (`:187-194`, `:216-226`); add whatever
   `FRONTEND_BASELINE` entries step 2's decision requires (none, if the generic plumbing moves to `channel`).
7. **Claude: verify** — full compile, full test run, and
   `grep -rn "jackoftradesltd\.\(middle\|backend\)" src/main/java/uk/co/jackoftradesltd/frontend` returns nothing for
   the moved files (stage 0's own verification command, `:221-226`, adapted).

### Left alone, on purpose

`ObjectPropertyAssembler.java` and `PlayerPropertyAssembler.java` keep calling
`UIRegistry.getUIEntry(name)` after the move — that crossing doesn't close, and shouldn't. It is the Java shape of C's
`bind_object_property_to_ui_entry_by_name`/`bind_player_ability_to_ui_entry_by_name`
(`src/ui-entry.h:23-25`), which C itself calls from the object/player property parsers by name, never by exposing
`struct ui_entry`. Their `MIDDLE_BASELINE`/`BACKEND_BASELINE` entries (`ObjectProperty.java`, `PlayerProperty.java`, and
the two assemblers naming `UIEntry`) stay — just re-pointed at `frontend.ui.entry.UIRegistry`/`UIEntry` instead of
`middle`'s copies.

## Verification — current status (superseding the sections above on specific paths)

Checked fresh this turn — `compileJava`/`compileTestJava` green, `BoundaryTest` run directly, actual package layout read
off disk with `find`. Everything below reflects what is really on disk now, not what was planned; where it disagrees
with "Plan" or "should `UIRegistry` move to `channel`?" above, this section wins. Those earlier sections are kept as the
reasoning record, not as current fact.

### The actual layout that landed

Different in three ways from the plan's proposal, all reasonable, all Rowan's call:

- `UIRegistry`, `UIDataLoader` → `frontend/ui/globals/` (not `frontend/ui/entry/`).
- The base and renderer readers/assemblers each got their own sibling package —
  `frontend/ui/entrybase/{reader,assembler}/`, `frontend/ui/entryrenderer/{reader,assembler}/` — rather than sharing
  `frontend/ui/entry/`. `frontend/ui/entry/{reader,assembler}/` holds only the plain-entry
  reader/assembler/parse-record.
- The three ANTLR grammar sub-packages moved too (not left in `backend`, and not deferred) — into
  `frontend/ui/{entry,entrybase,entryrenderer}/antlr4/{uientry,uientrybase,uientryrenderer}/`, one level under each
  domain's own package rather than under a shared `frontend.ui.entry.grammar`.
- `UIEntryCode` and `UIEntryPredicate` sit at the top, in `frontend.ui` directly (not
  `frontend.ui.entry` as this doc originally guessed when it was written against the stub).
- All 6 generic-plumbing types — `Reader`, `Assembler`, `GrammarDriver`, `ParseResult`,
  `ErrorParsing`, **and `ParseErrors`** (a sixth one, missed in the original plan of 5 — an ANTLR hard-error/listener
  helper distinct from `ErrorParsing`, used by 10+ other readers codebase-wide)
  — are in `channel/parser/`.
- `frontend.entries` (`UIEntry`, `UIEntryBase`, `UIEntryRenderer`, `UIEntryIterator`) is untouched.

### `BoundaryTest`: 15 tests, 3 failing (was 4)

`TheScanItself.everyBaselineEntryIsWellFormed()` now **passes** — the 20 stale baseline entries (6 `MIDDLE_BASELINE`, 14
`BACKEND_BASELINE`) have been deleted from `BoundaryTest.java`, and no
"baseline entry no longer crosses" failure remains anywhere. That was the one item this doc asked to be fixed, and it's
done and confirmed by test run, not just by inspection.

The three still-failing methods are all **new crossings needing a baseline entry or a real fix** — none of them
stale-baseline noise:

1. `frontend/ui/UIPlayer.java → middle.game.gameengine.GameState` — pre-existing, unrelated to this move (this file
   wasn't touched by it).
2. `frontend/ui/entry/assembler/UIEntryAssembler.java → middle.enums.Stats`,
   `middle.game.globals.GameConstants`, `middle.objects.enums.ElementEnum` — **the one open design question left from
   this move.** These imports existed before the move too, but were invisible to
   `BoundaryTest` (backend→middle is unrestricted, `Architecture_migration.md:208-209`, and the file was backend then).
   Moving the file to `frontend` turned three previously-silent imports into three new rule-1 violations. Per import:
   flatten (matching the `ElementEnum`-stays-in-`middle`
   reasoning already at `Architecture_migration.md:483-499`), or baseline for now.
3. `backend/parser/objectproperty/ObjectPropertyAssembler.java` and
   `backend/parser/playerproperty/PlayerPropertyAssembler.java → frontend.ui.globals.UIRegistry` — the "left alone, on
   purpose" crossing from the plan, working exactly as expected. Just needs a
   `BACKEND_BASELINE` entry at the new path (the old one, pointing at `middle`'s copy, was one of the 20 just deleted).
4. `middle/game/globals/GameConstants.java → frontend.ui.globals.UIDataLoader` — new `middle →
   frontend` crossing. `GameConstants.init()` still drives UI-entry loading order by calling the loader directly. Needs
   a decision: baseline it, or find a way for `frontend` to trigger its own startup load instead.

Items 2 and 3 from the *previous* version of this section — `ParseErrors` needing to join
`channel.parser`, and the three grammar sub-packages needing to move — are **done**; both landed (concurrently with the
baseline fix above) and no longer appear in `BoundaryTest`'s output at all.

### What would close it out

Three and 4 are one-line `BACKEND_BASELINE`/`MIDDLE_BASELINE` additions if accepted as-is. Item 2 is the only one that
needs an actual decision before it can be closed either way.
