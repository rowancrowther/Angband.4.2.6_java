# Things not to drop, up to 2026-10-16

Collected from the "Things not to drop" section of every precis in `docs/precis/` (260815 to 261016).
Boxes are all unticked. Items the precis marked DONE or FIXED were removed from here and from the precis.
Items deferred to a later chapter were moved out to `Chapter_4_Worklist.md` and `Chapter_7_Worklist.md`.
Wording is the precis's own, so line numbers and "today" refer to the date in each heading.

## 2026-08-18 (precis 260818.md)

- [ ] `GameWorld.decreaseTimeouts` and the curse timeout loop have no tests; both are private and need a live cave, an equipped body and the monster subsystem to reach, so they rest on the line-by-line comparison against `game-world.c:280-365` and nothing else.
- [ ] `PlayerUpkeep.getRedrawFlags` and `clearRedrawFlags` have no callers yet — `redrawStuff` is still a stub — so the tests are the only thing exercising the contract they were designed around.
- [ ] `PlayerTimedEffect.getGrade` still hands out the live list where the flag accessors were narrowed to views.
- [ ] The port's grade list has no equivalent of C's implicit zero grade, so `TimedGrade.Grade` runs one lower than C's numbering throughout; `timedGradeEq` no longer cares, but `player_set_timed` compares grade numbers and will.
- [ ] `docs/Chapter_3_Roadmap.md:194` still says `Flag<PlayerUpkeepEnum>` after the rename to `PlayerUpdateEnum`.
- [ ] A `TimedEffectData.java` was staged as a new file at some point today and does not exist on disk; worth checking what is staged for it before committing.
- [ ] `setTimed`, `incTimed`, `clearTimed`, `updateStuff`, `redrawStuff` and `noticeStuff` are all still stubs, which is why the timed-effect tests reach the counter reflectively.
- [ ] `Flag.onDbg` duplicates `on` exactly and carries no Javadoc. C's `flag_has_dbg` differs from `flag_has` only by a bounds check on `size`, which catches passing a flag of one family with the size of another — impossible in `Flag<E>`, where the family is the type parameter and there is no `size` argument to disagree with anything. The deprecation-note treatment given to `next` may be the honest answer.
- [ ] `flagsTimed` has no callers yet: `calc_bonuses` is not ported, so its tests are the only thing exercising it.
- [ ] `PlayerState.wipe` calls `clear()` on its two flag sets, but `Flag.clear` is a varargs method taking the flags to clear, so a no-argument call clears nothing. Nothing calls `wipe` yet; `calcBonuses` will.
- [ ] `flags` has no callers yet either, for the same reason `flagsTimed` does not: `calc_bonuses` is not ported.
- [ ] `modifyStatValue` is the only non-static member of `PlayerUtils`, whose class doc says the file is static methods over a passed-in player; its tests have to construct an instance to reach it.
- [ ] `Player`'s constructor reads `PlayerRegistry` for a default body and race and throws if races are not loaded, so any test that builds a `Player` must seed the registry first.
- [ ] `PlayerBody.setName` has no callers now that `embody` replaces the whole object.
- [ ] `calcBonuses` stops at "analyze equipment" and has no Javadoc; `state` and `knownState` are still null on a new `Player`, which `hasPlayerFlag` and `hasObjectFlag` will trip on.
- [ ] The reply-format rule set today is in memory: short bullets with blank lines between them, one idea each, no paragraphs under a bullet.

## 2026-08-19 (precis 260819.md)

- [ ] `PlayerState` has an `ac` field but no accessor to accumulate into it, so an item's base armour has nowhere to go once the two armour lines in `calcBonuses` are put back the right way round.
- [ ] The item speed modifier in `calcBonuses` is written into see_infra, and the two armour lines apply the enchantment where the base value belongs and vice versa.
- [ ] The `TMD_SHERO` block is missing from `calcBonuses` — melee to-hit, an armour penalty and a device scale — though both the effect and the skill exist.
- [ ] The curse walk in the equipment loop is still a TODO, and with it every curse's flags, modifiers, resistances and combat bonuses.
- [ ] `calcBonuses` excludes two slot types from the to-hit and to-damage bonuses that C does not; worth a comment if it is deliberate.
- [ ] `getCurses` returns `Map` while the field is a `LinkedHashMap`; the ordering `objectWeightOne` relies on needs to be stated in the Javadoc or in the type before something quietly undoes it.
- [ ] The stat-index fallback in `PlayerState.weightLimit` answers 3 where an index of 0 is the value matching a strength of 3.
- [ ] `averageSpellStat` throws on a class with no realms; both call sites guard it, so the precondition wants a Javadoc line rather than a check.

## 2026-08-20 (precis 260820.md)

- [ ] `Food.java`'s comment block was replaced without being read first, so whatever was written there earlier in the day is gone; the code is untouched but the prose is worth checking before it is committed.
- [ ] `docs/Workpoints_CalcBonuses.md` still presents all nine work points as open when every one of them is closed.
- [ ] `PlayerState` has no getters for the three combat totals, so two tests reach the fields reflectively rather than have production accessors added for a test's benefit.
- [ ] `ItemObject` capitalises the armour-class family where `PlayerState` does not, so one line of the equipment loop carries three spellings of the same idea.
- [ ] The test support reaches private fields on the player, the equipment slot and both registries, because character creation and the wield command do not exist yet; a field rename will break tests rather than compilation.
- [ ] `calcMana`, `calcBlows`, `calcLight` and `calcShapechange` are exercised only through `calcBonuses`, never directly.
- [ ] The port's grade list still has no equivalent of C's implicit zero grade, so grade numbers run one lower throughout.
- [ ] `PlayerUtils.search` and the resting helpers remain declared stubs, as do the registry setters and most of the game world, none of which has tests.
- [ ] The only getter in `PlayerState` without a `get` prefix is the percentage damage reduction.
- [ ] `PlayerUpkeep.getRedrawFlags` returns a snapshot rather than the live set, so clearing has to go through `clearRedrawFlags`; wiping what the getter returns clears nothing and reads as a passing test.
- [ ] `PlayerUpkeep` exposes no reader for the update flags and no setter for partial mode, so the new tests reach both reflectively.
- [ ] The alias case is covered by argument rather than by a mutation run, since demonstrating it would mean breaking production code on purpose.

## 2026-08-21 (precis 260821.md)

- [ ] The reply record settled today carries a boolean, so the getters that can also abort need a payload shape that distinguishes "aborted" from "answered" before they can use the same route.
- [ ] The primer menu entry still describes the four options without recording which one was chosen.
- [ ] `ItemObject.checkForInscription` returns a boolean where the original returns a count of overlapping matches, which the repeated-confirmation call sites rely on.
- [ ] The inscription helper that reads a trailing integer, used by the auto-pickup limit, has no port yet.
- [ ] `Player.ignoreDrop`'s shop test and ignore filter are now correct; what remains of it is listed below.
- [ ] `noticeStuff` is missing its third block, the monster-message dump, and its Javadoc still calls the whole method a stub.
- [ ] `combinePack` never breaks out of the inner scan after a merge, so an absorbed stack would be offered for merging again.
- [ ] `combinePack` sets both of its message flags and reads neither, so the pack-combined message, the repeat-disable and the two gear redraw signals are all absent.
- [ ] `combinePack` has no partial-stack branch, which in the original is the larger half of the function.
- [ ] `combinePack` iterates the gear with a for-each while the absorb it calls will remove from that same list.
- [ ] The inner loop bound in `combinePack` is one past the end, reachable only if an item is missing from the gear it came from.
- [ ] `mergeable` applies the quiver limit outside the store exemption, where the original nests it inside, and takes its stack limit and ammo test from the wrong one of the two objects.
- [ ] `objectStackable` tests its two inscriptions for non-null where the original tests for null, inverting the whole compatibility rule.
- [ ] `ItemObject.setNote` does not exist, so the refusal path in `ignoreDrop` cannot compile, and appending to a null note would read as the word "null" followed by the inscription.
- [ ] `ignoreDrop` returns from the whole method when the drop command is missing, skipping the two flags the original always reaches.

## 2026-08-26 (precis 260826.md)

- [ ] The two review documents are the detail for everything below; the precis deliberately does not repeat their line references.
- [ ] Three findings block `calcInventory` from running at all — the unsized pack list, the two gear walks that run past the end of the list, and the comparator returning the wrong answer for a null argument.
- [ ] The pack loop runs one iteration short of the original, which is where the deliberate "overfull by one" slot comes from, and the pack re-arrange message is missing a negation.
- [ ] `ItemObject.copy` has three separate problems: it dereferences four nullable fields, it deep-copies the kind and ego and artifact templates the original shares, and it drops the origin depth.
- [ ] Both pricing functions have their own faults — a saturation constant with the wrong sign, and a clamp that lifts negative values where the original lifts only zero.
- [ ] The three arithmetic slips in the power subtree are the slay term's divide, the weight term's constant, and the element term's nested loop.
- [ ] A further group of power findings are null and unboxing hazards rather than wrong arithmetic — the modifier map, the curse element map, the activation list, and the curse and brand and slay collections the original guards and the port does not.
- [ ] The curse attribute merge counts a curse's to-armour twice and silently drops any resistance for an element the base object says nothing about.
- [ ] `Player.slotByType` never reaches its fallback, which is reachable from anything that asks an object which slot it is worn in, not just from the power code.
- [ ] `flagsPower` looks its flags up in the modifier table rather than the flag table, and should fail loudly rather than quietly.
- [ ] `bowMulitplier` is spelled with the letters transposed, which is why it stayed invisible to the first review's searches.
- [ ] The `objectPower` subtree review did not audit the loaded registry tables themselves, only the code that reads them; they are the next place to look if the numbers still disagree afterwards.

## 2026-08-27 (precis 260827.md)

- [ ] `Player.setCave` and `Chunk.setCurrentLevel` are wired but uncalled; both calls belong in the port of `prepare_next_level`, at the point the known level is allocated.
- [ ] The known level is replaced on every level change and nulled across the persistent-level swap, so this subsystem must keep tolerating an absent one rather than assuming it exists once generated.
- [ ] The test suite is 2808 tests and green; the whole of it must compile before any of it runs, so drift in one test file blocks the lot.
- [ ] Chapter 7 of the roadmap now carries everything deferred today — the spellbook's missing call site, the four pieces of flavour assignment, the flavour seed's two blockers, and the unguarded flavour copy — and is the only place they are tracked.
- [ ] The flavour copy is the one of those that is wrong today rather than blocked on anything, so it is the obvious first move next session.
- [ ] `MagicBook.setSVal` is now correct and public but still called from nowhere, so every book's sub-type reads zero until the call site lands.
- [ ] Compaction's move and the deletion beneath it are both still stubs, so its tests can watch the high-water mark and nothing else.
- [ ] `testsupport/ItemFixture` is the one place a loaded item gets built; a new test that needs one should reach for it rather than write another reflective builder.
- [ ] A comment describing behaviour is a claim: three of mine were wrong until a test checked them.

## 2026-09-01 (precis 260901.md)

- [ ] `learnRune` is public. The split plan wanted it package-private, because C keeps it static and it is the single guarded entry that fires the second update and the group fan-out.
- [ ] The 63 fields are package-private, so anything added to `middle.player` sees all of them; the package boundary is the whole of the protection now.
- [ ] `PlayerTimed.clearTimed` and `PlayerTimed.incTimed` are superseded stubs that take no player and can never work; one caller in `GameWorld`, paralysing the player, is all that keeps them alive.
- [ ] A test double that subclasses `Player` can no longer observe anything that moved, because the moved methods are static; the collection a walk iterates is the boundary to record on instead.
- [ ] `updateMaxLevel` and `updateDungeonDepth` have no C original found and stay on `Player` until their provenance is established.
- [ ] `ported_functions.md` lists only full ports, so the stubs left in `ObjectUtils` and `ObjectIgnore`, and the partially implemented knowledge refresh, are deliberately absent from it.
- [ ] `find_roman_suffix_start` is ported, listed and stamped, but its roadmap box under stage H is unticked.
- [ ] Chapter 3 stages A to F are complete; stage G, `player_generate` and its helpers, is next.
- [ ] Dropping the allocation note from `PlayerHistory` leaves the reason C's list-grow function has no counterpart recorded nowhere.
- [ ] `Curse.hasStandardToH` returns `combatToHit == 0`, where C returns true for any curse whatever its to-hit; the three curses affected are *enveloping*, *irritation* and *air swing*.
- [ ] The effect assignment and fully-known block at the end of the curse `knowObject` have no counterpart in C, which returns before both, and the effect assignment being unconditional makes `Curse.isFullyKnown`'s effect test always pass.
- [ ] `Curse.isFullyKnown` compares only the modifiers the curse's data lines name, where C compares every slot of a dense array; the two agree only while the known map is built solely from the real one.
- [ ] `updateObjectKnowledge` throws on a player whose item knowledge is null as soon as any curse is loaded, where before it ran harmlessly; the curse loop has no equivalent of the early returns that protect the two object loops.
- [ ] The `updateObjectKnowledge` Javadoc and the `PlayerKnowledge` class Javadoc still describe the curse population as waiting for somewhere to put its answer, which the code no longer does.

## 2026-09-05 (precis 260905.md)

- [ ] The `RuneInitTest` bug was a general shape, not a one-off: a test that saves/restores its *input* registries but writes to an *output* one it never protects. Worth a glance if another reader/parser suite starts flaking.
- [ ] Point-buy's remaining pieces are still open under roadmap stage H: `reset_stats`, `buy_stat`, `sell_stat`, `generate_stats`, quickstart save/load, the 13 birth command handlers, and `PlayerHistory`'s opening entry.

## 2026-09-07 (precis 260907.md)

- [ ] ignoreLevel exists in both ObjectInfo and ObjectIgnore - need to consolidate into one (also listed in 260908)

## 2026-09-12 (precis 260912.md)

- [ ] `studyStatus`, `studyConditions` and `detectionStatus` are still unwired in the `PlayerStatusView` cache - no source field exists for any of the three yet, per `docs/functions/player_status_view_setters.md`.

## 2026-09-13 (precis 260913.md)

- [ ] The raw entry names now sitting in `ObjectProperty`/`PlayerProperty` aren't resolved anywhere yet - no `UIRegistryLoader` class exists in `src/main` today. That lookup is the actual Chapter 3 UI work; today's change only cleared the backend/middle side of the crossing.

## 2026-09-15 (precis 260915.md)

- [ ] `SavefileGetterImpl.gotSavefile` has no equivalent of C's SETGID-prefix filtering (`ui-game.c:1204-1209` sets up `uid_c`, `ui-game.c:1231-1234` skips any `fname` that doesn't start with it) - not a bug, since SETGID doesn't apply to this port, but the "only show this player's own savefiles" behaviour it provided has no Java replacement yet if a shared/multi-user save directory is still wanted.
- [ ] `ChestTrapAssembler.assemble` is all-or-nothing, unlike every sibling assembler in the parser package - one bad record zeroes the entire chest-trap list rather than dropping just that record (`backend/parser/chesttrap/ChestTrapAssembler.java`).
- [ ] `ObjectPropertyAssembler.java:212` and `PlayerPropertyAssembler.java:130-135` both carry dead, commented-out `UIRegistry.getUIEntry(...)` lookups - in both cases a binding whose UI-entry currently never gets resolved or validated.
- [ ] `ObjectBaseAssembler.assemble` appends a synthetic `TV_NONE` sentinel `ObjectBase` after the loop, unconditionally, regardless of what was actually parsed from the file.
- [ ] `PlayerRaceAssembler.java:150` always looks up player body index 0 (`PlayerRegistry.lookupPlayerBody(0)`) regardless of which race is being assembled.
- [ ] `ProjectionAssembler.assemble` silently falls back to sentinel `-1` on a parse failure for `numerator`/`divisor`/ `damageCap`, with no error reported - unlike every other numeric field in the same method, which reports and skips.
- [ ] `VaultAssembler`'s `logger` field is declared but never invoked anywhere in the class.
- [ ] `TrapAssembler.assemble` computes `trapIndexStr` from `record.index()` but never uses it - the real index is the loop counter.

## 2026-09-18 (precis 260918.md)

- [ ] The create/override split in `assemble` is the piece everything else in the entry parse hangs off; the lookup has to come before the field interpretation, not after.
- [ ] `UIEntryPriorityScheme`'s `"negative index"` string and `ParseUtils`' element index/count disagreement are both latent — nothing reads them on the current path, but both are wrong.

## 2026-09-19 (precis 260919.md)

- [ ] `get_stat_name` in the C tree still carries a `/* Ported to Java 2026-09-19 */` stamp from before the Java method was removed. The edit to take it out was refused by the tool's destructive-edit guard, so it needs doing by hand — until then the C tree claims a port that no longer exists.
- [ ] The algorithm block in `260918.md` cites `run_parse_ui_entry` as `ui-entry.c:1700` in its header. It is at 2262, and the same document has it right twice elsewhere.
- [ ] `portUIEntryBasesToUIEntries` is a stepping stone, not the destination. Under the one-list decision the base records should go through the same assembler as the entry records, with the method disappearing rather than being maintained alongside it.

## 2026-09-20 (precis 260920.md)

- [ ] The override path is live, not theoretical: the 13 `resist_ui_compact_0<…>` specializations in `ui_entry.txt` reach it through names the fan-out created, and they have no `combine:` of their own.
- [ ] `template:` is an ordinary in-file-order directive that replaces four fields, not a preamble applied before the rest of the record.
- [ ] Bind a `priority:` to the category *object* it follows, not to an index into the sorted category list.

## 2026-09-22 (precis 260922.md)

- [ ] `UIEntryAssembler.java`'s `buildCategories`/`assemble` (and the `default_priority` it feeds into `parseEachEntry`'s create path) don't reproduce `parse_entry_priority`'s split in C (`ui-entry.c:2139-2191`): when a record's `priority:` line has no category before it, C sets the record's own `default_priority`; when a category *does* precede it, C leaves `default_priority` at 0 and attaches the priority to that category only. Java always does both — the same value lands on the record's own priority *and* the preceding category — so any category listed *after* the `priority:` line would wrongly inherit the earlier category's priority instead of 0. Documented on `buildCategories`, `assemble` and `parseEachEntry` in `UIEntryAssembler.java` (260922). Latent, not live: no shipped `ui_entry.txt` record has a `category:` line before a `priority:` line — including the 13 `resist_ui_compact_0<TAG>` records that exercise the *override* path (`docs/precis/260920.md`, "Correction: the create path is not the common case"), none of which carries a `priority:` line at all. Not fixed; Rowan's to decide whether it's worth fixing ahead of the data ever needing it.

## 2026-09-24 (precis 260924.md)

- [ ] `UIEntryBase` is still a separate live type, not folded away as the 260919 "one list" decision called for: `UIRegistry.java:57` keeps a second `uiEntryBases` list alongside `uiEntries`; `UIEntry.java:62` still has a `private UIEntryBase template` field; `UIEntryAssembler.java:295` resolves `template:` against the separate `UIRegistry.getUIEntryBase(name)` list rather than the shared registry; and `UIEntryBase.java:104`'s `categories` is still `List<String>` with no priority field to copy onto a child entry.
- [ ] `UIEntryAssembler.java:434-441` and `:539-549` — template categories are built with the new entry's own priority and `prioritySet=false`, instead of copying the template's own stored category priority and `priority_set` the way C's `parse_entry_template` does (`ui-entry.c:1983-1988`). Latent: no shipped `ui_entry_base.txt` record combines `category:` with `priority:`, and issue 1 means `UIEntryBase` can't hold a per-category priority yet anyway.
- [ ] `UIEntryBaseAssembler.java:137` — `assemble()` still builds and returns an unused `List<UIEntryBase>` per record, separate from the plain `UIEntry` it actually feeds to `UIRegistry.setUIEntries()` at line 148. Looks like leftover from before the 260919 rework rather than a live behaviour.
- [ ] `UIEntryAssembler.java:113-123` (`buildCategories`) — an after-priority category inherits the before category's priority instead of 0, when both are present on the same record. Latent: no shipped record has a `category:` line before a `priority:` line.
- [ ] `HelperFunctions.getElementName` (`UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT`'s `ith_name_func`) returns `ElementEnum.values()[index].name()` (e.g. `"ELEM_ACID"`) rather than C's bare `get_element_name` token (e.g. `"ACID"`, `ui-entry.c:1606-1610`). Found 260924 while writing `UIEntryNameParameterTest`. Inert: nothing calls `UIEntryNameParameter.getParamName` for the element scheme today, since `UIEntryAssembler`/`UIEntryBaseAssembler` expand `parameter:element` with their own loop over `ElementEnum.values()` that strips the `ELEM_` prefix itself.

## 2026-09-28 (precis 260928.md)

- [ ] `PlayerRedrawStuffTest.everyFlagMapsToItsCEvent` NPEs on a missing `PlayerState` stub: `PlayerCalcs.redrawStuff`'s `PR_STATS` arm reads `player.getPlayerState().getStatUse(stat)`, but the test's fixture player has no `PlayerState`, so it needs a stub added before the test goes green again.
- [ ] `EVENT_EXPERIENCE` carries two dispatches for now: `PlayerCalcs.redrawStuff`'s `PR_EXP` arm sends `eventSignalLongStat` and `eventSignalLong` both under it, so `RedrawRouter.setExperience` pattern-matches on payload shape and `PlayerRedrawStuffTest.everyFlagMapsToItsCEvent` expects the repeat explicitly; worth reconsidering later by splitting onto two event types.

## 2026-09-29 (precis 260929.md)

- [ ] `Chunk.delistObject` and `Chunk.objectDelete` test whether the player's list holds the same `ItemObject`, where C tests the known object at the same index, so they wait on the KnownObject design.
- [ ] `MonsterGroup.monsterGroupChangeIndex` is a stub that returns true, so the group step in `monsterIndexMove` does nothing yet and will throw once a real port can return false.
- [ ] `monsterIndexMove` has an empty Javadoc block, and `Chunk.java` still needs its stage 2 to 5 pass once the two open divergences are closed.
- [ ] `Monster.setHp`/`setMaxHp`/`updateCached` still write the legacy `PlayerEventStatusUpdate` cache; they go when the cache-to-messages migration removes it.
- [ ] `UIEntryAssembler.parseEachEntry` has latent divergences from C that no shipped `ui_entry.txt` record reaches: on the override path, a `priority:` before any category sets a category's priority instead of the default, `index` on a non-parameterised entry gives 0 where C gives -1, and template flags are never copied.
- [ ] On the create path, the template's default priority and each template category's priority and `priority_set` are replaced by the record's own values, and a template's flags overwrite the record's own instead of being OR'd with them.
- [ ] `UIEntryAssembler` also keeps categories in insertion order where C sorts by name, throws on an out-of-range `priority:` where C clamps, emits one "no combiner" error per element where C emits one, and quotes `getFirst()` in the bad-flag message.
- [ ] The `ui_entry_renderer.txt` grammar drops three directives C parses - `combine`, `units` and `combined-renderer` - so a file using any of them fails closed with token errors and loses every record, and `UIEntryRendererParseRecord` has no fields to carry them; the shipped file uses none.
- [ ] The renderer grammar also fixes the directive order (`colors`, `labelcolors`, `symbols`, `ndigit`, `sign` after `name` and `code`) where C accepts any order, and it makes `code` mandatory where C leaves the backend unset and skips the renderer.
- [ ] C merges a repeated `name:` into the existing renderer field by field, but `UIEntryRendererAssembler` has no merge (a commented-out sketch only) and the grammar rejects a second `name:` block that omits `code` before the assembler sees it.
- [ ] `UIEntryRendererAssembler` accepts `code:NONE`, resolving it to the Java-only sentinel with empty palettes, where C rejects it as an unknown backend.
- [ ] A record with no `code` reaches the assembler only when it is called directly, and there `ndigit` and `sign` are not validated, the file's `colors`, `labelcolors` and `symbols` are dropped, and `nDigit` is fixed at 1, where C validates and stores them whatever the backend.
- [ ] Palette lengths count UTF-16 units where C's `text_mbstowcs` counts wide characters, so the 64-symbol cap and top-up index could differ for a symbol outside the basic plane; every shipped symbol is ASCII.
- [ ] `ItemObject.printCustomMessage`'s Javadoc says the Java version cannot truncate where C cuts at its 1024-byte buffer, but `Message.messageType` now cuts the text at 1023 characters, so the Javadoc is wrong.
- [ ] `ItemObject.printCustomMessage` hands its built text to `Message.messageType` as the format pattern where C's `msgt` receives it as a `"%s"` argument, so a `%` in a message template would be read as a format directive.

## 2026-09-30 (precis 260930.md)

- [ ] `Feature.equals` and `hashCode` treat `flags` and `resistFlag` by identity, because `Flag` defines no `equals` or `hashCode`; `FeatureTest` has a disabled spec that expects value equality.
- [ ] `TerrainFeatureAssembler` does not set the C `store_max` that `finish_parse_feat` derives from the shop count.

## 2026-10-02 (precis 261002.md)

- [ ] `ItemObject` batch 1 differences from C: `checkElementStacking` is stricter than C when an element is recorded on one object and absent on the other, because the element maps are sparse.
- [ ] `cursesAreEqual` treats an empty curse map and a map of zero-power entries as equal, where C treats a null curse array and an all-zero one as different.
- [ ] `similar` assumes both modifier maps exist and, in `OSTACK_LIST` mode, that both items have a known counterpart; C also dereferences `known`, but it has no null maps.
- [ ] `earlierObject`, `similar`, `objectAbsorb` and `wieldSlot` overwrite the receiver's `player` on every call, while the partial absorb still reads the snapshot taken at construction.
- [ ] `earlierObject` is an instance method only to refresh `player`, so `calcInventory` calls it on a throwaway `new ItemObject()` and the receiver plays no part in the ordering.
- [ ] `similar` carries an explicit `tValue` equality test that C lacks, which the kind test already implies.
- [ ] The `removeCurse` Javadoc in `ItemObject` still says nothing is stored at power zero and credits that for how `cursesAreEqual` works, which is no longer true.
- [ ] `ItemObject` batch 2 differences from C: `copy(false)` leaves `known` null where C's `object_copy` copies the pointer, and `objectAbsorbMerge` ignores an empty absorbed note, which C does not test for.
- [ ] `objectWeightOne` would apply a curse twice if two curses on one item shared an index; real curses never do.

## 2026-10-03 (precis 261003.md)

- [ ] `Random.randCalc` with the randomised aspect reads the world list through `WorldRegistry.getMaxRandDepth()`, where C uses the fixed `MAX_RAND_DEPTH`, so `Random.Zero()` cannot be rolled randomised until the world data has loaded; `RandomConstantsTest` seeds the list to get past it.

## 2026-10-05 (precis 261005.md)

- [ ] Curses are numbered from 0 in `CurseAssembler.java` and `ObjectRegistry.curseMax` is the count, where C leaves slot 0 as a dummy and `curse_max` is the count plus one, so decide the convention before porting the random curse picks in `effect-handler-general.c` and the wizard curse command, or `vulnerability` will never be chosen.
- [ ] `GameWorld.java` and `ObjectUtils.java` now read a curse's timing from `curse.getItemObject().getTime()`, but `CurseAssembler.java` writes no time onto that object, so every real curse rolls a timeout of zero until the move in `Effect_time_migration.md` lands.
- [ ] `Effect.java` has no `equals`, so `ItemObject.effectIsKnown` compares effects by identity as C compares pointers, and adding one would turn the "equal but distinct effect" case in `CurseIsFullyKnownTest` from not-known to known.
- [ ] Step 5 of `Curse_object_unflattening.md` is still to do: the Javadoc in `Curse.java`, `CurseSource.java`, `BonusSource.java` and `PlayerKnowledge.java` still describes a flattened curse.

## 2026-10-06 (precis 261006.md)

- [ ] `GameWorld.processWorld` reads `Food.PY_FOOD_STARVING` and `Food.PY_FOOD_HUNGRY`, which are hardcoded to 100 and 1500, where C reads the thresholds loaded from `player_timed.txt` and held in `PlayerRegistry`; the two agree for the shipped data but would drift if `player_timed.txt` or `player:food-value` changed.
- [ ] Decide whether `PlayerTimedAssembler` should build the `fail:` list last-first like C, which prepends each line, because today `incCheck` meets POISONED's poison resistance before its `OPP_POIS` check and may identify a poison-resistant item that C never reaches; if yes, remove the `@Disabled` on `failLinesAreWalkedInTheOrderCBuildsThem` in `PlayerTimedReaderTest` as the check.

## 2026-10-07 (precis 261007.md)

- [ ] C's `object_learn_unknown_rune`, `object_find_unknown_rune`, `object_learn_on_use`, `shape_learn_on_assume` and `missile_learn_on_ranged_attack` have no Java port yet, and `equip_learn_after_time` exists only as a stub in `ObjectUtils`.
- [ ] `applyCurseAttributes` uses (`curse == curseToIgnore`), while its neighbours go through `CURSE_ORDER`, Flagged in part C.
- [ ] C line numbers remain in the Javadoc of `ObjectRegistry.java`, `FlagSet.java`, `ElementSet.java` and `ElementPowers.java` (Part D's report lists them); strip them when you next touch those files.
- [ ] Open decisions from the `ItemObject` second pass (`docs/ItemObject_round2/summary.md`), none made yet: whether `applyCurseAttributes` should compare through `CURSE_ORDER` (the bullet above); whether the `Curse` overloads of `objectPower`, `nonStandardWeightPower` and `cursePower` stay hand-reduced or call `objectPower` on `Curse.getItemObject()` as C does; and whether the five `objDescNameFormat` divergences join the batch 8 accepted list.

## 2026-10-08 (precis 261008.md)

- [ ] `PlayerTimedAssembler` does not reject a `lower-bound:` outside 0 to 32767, as C's parser does; a negative or oversized bound reaches `PlayerTimedEffect` unchecked.
- [ ] `PlayerBirth.playerInit` still says in its Javadoc that nothing calls it, but `doCmdBirthReset` does.
- [ ] `PlayerUtils.restingIsSpecial` is a stub that always answers `false`, so `Player.isResting` only counts a positive resting counter.
- [ ] `PlayerBirth.playerInit` hands the registry's own first race and class to `setRace` and `setClass` rather than copies, which those setters document as the normal expectation.
- [ ] `ChestTrap.java` Javadoc cites C line numbers (the class block and the `level` field).
- [ ] `ChestTrapTest.java` class Javadoc cites C line numbers for `struct chest_trap`, `chest_traps` and the pval doubling.
- [ ] `GameWorld.java` builds its recharge description from an empty `Flag<ObjectDescription>`, while the other callers set `ODESC_BASE`; both mean C's 0x00, but the port of `object_desc` must not treat `ODESC_BASE` as a mode bit.

## 2026-10-09 (precis 261009.md)

- [ ] `MonsterRegistry.java`: accepted deviation, "for the time being". C builds each table with a blank slot, and its `z_info` counter is one more than the record count. The blank slot is slot 0 for pain messages and blow methods, and the last slot for races, blow effects, pit profiles and summons. The races differ from the usual picture because `r_info[0]` is the real `<player>` record. Java lists are base 0, hold only real records, and their `*Max` counters equal the size, so each is one lower than C's. A lookup of the blank index 0 finds nothing, where C returns an empty record. Any port that loops to `r_max`, `mp_max`, `pit_max`, `blow_methods_max` or `blow_effects_max`, or indexes by pain 0, must allow for this.
- [ ] `MonsterRegistry.java`: `lookupMonsterPain` rejects an index above the list size, which equals C's `mp_max` test only while the pain indices run 1 to n without a gap, as the shipped twelve do. `getPainFromIndex` has no such test.
- [ ] `MonsterRegistry.java`: `setMonsterRaces` throws a `NullPointerException` for a race with no blow list or a null list element, so the null guards in `lookupMonsterRace` can only be reached by seeding the list directly.
- [ ] `mon-util.c`: `lookup_monster` already carried a "Ported to Java 2026-08-30" stamp, so it was not stamped again.
- [ ] `ObjectPropertyAssembler.java`: the `default` branch of the category `switch` can no longer be reached, and if it ever were it would add the record with a null index rather than dropping it.
- [ ] `ObjectBaseAssembler.java`: a non-`HATES_` token is resolved only as an `ObjectKindFlag`, so an `OF_` or `IGNORE_` token in `object_base.txt` is a soft error where C accepts it; the shipped file uses neither.
- [ ] `TrapAssembler.java`: `index` now advances only for kept records, so a record dropped on a soft error shifts every later `trapKindIndex` down by one , where C aborts on a bad record; `TerrainDataLoader.loadTraps` now stops the load if the first kind is not `no trap`, so only a later dropped record is tolerated.
- [ ] `PlayerProperty.java`: `do_cmd_abilities`, `textui_view_ability_menu` and the `view_ability_menu` call have no Java port yet. Until the call is restored `viewAbilities` builds its list and discards it, so its class-then-race grouping and the double listing of an ability both have are untested.
- [ ] `PlayerPropertyAssembler.java`: the class Javadoc still cites C line numbers (`init.c`, `z-util.c`) in the `finish_parse_player_prop` and `my_strcap` references; `PlayerProperty.PlayerPropertyValue` has no use left in `src/main`.
- [ ] `BlowEffectReaderTest.java`: the lash-type fallback comment cites a C line number for `mon-init.c`; it should name the file and `parse_eff_lash_type` instead.
- [ ] `PlayerUtils.java`: `overExert` is still a stub with no player parameter, so the bloodlust call in `GameWorld.java` and every other caller currently does nothing.- `PlayerShape.java`: `sidx` is not held, and `shape_name_to_idx` and `player_shape_by_idx` have no Java port; shapes are found by name through `PlayerRegistry.lookupPlayerShape`. Any port that needs a shape index must settle how to number them.
- [ ] `ShapeAssembler.java`: blow names are kept in file order, where C pushes each `blow:` line onto the head of its chain and so holds them in reverse. The random blow-name choice in `py_attack_real` picks by position, so the same roll names a different blow than C does; the distribution is the same.
- [ ] `PlayerShape.java`: `getPlayerBlow` has no caller in `src/main`; the shapechanged blow-name substitution in `py_attack_real` is not ported yet.
- [ ] `SummonAssembler.java`: the `uniques:` token sets the flag only for `1`, where C sets `unique_allowed` for any non-zero integer; the shipped `summon.txt` uses only `0` and `1`.
- [ ] `SummonAssembler.java`: a record with no `msgt:` line is dropped as an invalid message type, where C keeps it with message type 0; every record in the shipped `summon.txt` has one.
- [ ] `ChestTrapAssembler.java`: `destroy:` and `magic:` are set only for the value `1`, where C sets them for any non-zero integer; the shipped `chest_trap.txt` uses only `1`.
- [ ] `ChestTrapReaderTest.java`: a comment in `pvalsAreOneBitPerTrapInFileOrder` cites a C line number for `obj-chest.c`; it should name `parse_chest_trap_name` instead.
- [ ] `BlowEffectAssembler.java`: a record with no `desc:` line gets `""` where C holds `NULL` (`NONE` is the only one in the shipped file), so anything testing for a missing description must compare against the empty string.
- [ ] `BlowEffectAssembler.java`: an absent lore colour or `lash-type:` is left `null` where C holds zero (`COLOUR_DARK`, and projection index 0), so a lash spell whose first blow is `NONE` would get a null projection where C gets acid. `lore-color-resist:Dark` would also read as set here and as unset in C; the shipped file uses neither.
- [ ] `BlowEffectAssembler.java`: the class Javadoc and two comments cite C line numbers (`mon-init.c`, `project.c`); they should name the file and function instead.
- [ ] `PlayerRace.java`: the `getResistanceLevel` and `getpFlags` Javadoc blocks cite C line numbers for `player-calcs.c`; they should name the file and `calc_bonuses` instead.
- [ ] `Pile.java`: `lastItem` can return null but has no `@Nullable`, `peekLastItem` is `@Nullable` yet throws on an empty pile, and the private `pop` has no caller.
- [ ] `quality_values` and `quality_choices` in `obj-ignore.c`: nothing in Java builds the table of menu names for the quality bands ("no ignore", "bad", "average", "good", "non-artifact"), so it still has to be created when the ignore menu in `ui-options.c` and the "All ..." entry in `ui-object.c` are ported. `IgnoreType` already carries its own names, which stand in for `quality_choices`.
- [ ] `PlayerUpkeep.java`: `setHealthWho` and `setRestingCounter` still write the static `PlayerEventStatusUpdate` cache, to be replaced by the message scheme. The resting text is a placeholder that is never cleared when the counter returns to zero, and constructing any extra upkeep clears the live tracked flag.
- [ ] `Monster.java`: `updateCached` also writes the tracked flag into the same cache, so two places publish it until the message scheme replaces both.
- [ ] `PlayerUpkeepStateTest.java`: several Javadoc blocks cite C line numbers (`player-birth.c`, `savefile.c`, `ui-command.c`, `ui-signals.c`); they should name the file and function instead.
- [ ] `ProjectionAssembler.java`: a projection with no `lash-desc:` line appears to load with `""` where C holds `NULL`, so `WorldRegistry.lookupProjectionByName("")` matches the first such record (`ARROW`); nothing calls it that way yet.
- [ ] `WorldRegistry.java`: `getMaxRandDepth` is the length of `world.txt` while C's `z_info->max_depth` is `world:max-depth` in `constants.txt`; the two agree at 128 only while the files do. `getMaxRandDepth`, `getProjMax`, `getQuestMax`, `lookupProjectionByName` and `lookupProjectionByType` have no caller outside the registry.
- [ ] `PlayerQuest.java`: `quest_check` and `build_quest_stairs` are not ported, so nothing ever credits a kill or zeroes a quest's level, and `Quest` has no setters for them.
- [ ] `PlayerQuest.java`: the `isQuest` and `playerQuestsReset` Javadoc cite C line numbers (`player-quest.c`, `player_birth.c`, `load.c`); they should name the file and function instead.
- [ ] `PlayerQuestResetTest.java`: the class and method Javadoc cite C line numbers in `player-quest.c`; they should name the file and function instead.
- [ ] `MiscRegistryNameSectionsTest.java`: the class Javadoc cites C line numbers (`init.c`, `randname.c`, `randname.h`); it should name the file and function instead.
- [ ] `MiscRegistry.java`: `getHints` has no caller in `src/main`; `random_hint` and the store greeting in `ui-store.c` that read C's `hints` are not ported yet.
- [ ] `CommandTest.java` and `CommandSpellItemRepeatTest.java`: the class Javadoc still names `GameState.targetOkay` as a stub; it is `Target.targetOkay` now, and the stale-target branch of `getTarget` stays untested until it is corrected.

## 2026-10-10 (precis 261010.md)

- [ ] `mon-util.c`: `describe_race_flag` and `create_mon_flag_mask` have no Java port yet. Both read the flag table, `mon-lore.c` builds its masks through `create_mon_flag_mask`, and `describe_race_flag` returns an empty string for an out-of-range flag where `getDescription` does no range check.
- [ ] `SlayAssembler.java`: a `slay.txt` record with neither `race-flag` nor `base` is rejected, where C's `parse_slay_race_flag` and `parse_slay_base` accept it and only forbid naming both.
- [ ] `ClassSpellBookAssembler.java`: the graphics and properties of a book are applied only when its kind is first created, where C's `parse_class_book_graphics` and `parse_class_book_properties` rewrite the shared kind each time a class lists the book.
- [ ] `PlayerBody.java`: the constructor pushes the slot count to the character sheet for every body built, including each `body.txt` template and each `copy()`, where C reads `player->body.count` on demand; `Player.java` pushes it again after the race body is set, so the constructor push is redundant and leaves the sheet holding the last template built until then.
- [ ] `GameState.java`: `initGameState` is an empty method that nothing calls, and its Javadoc already says it can be deleted.
- [ ] `PlayerBirth.java`: the birth code calls `initBrands`, `initSlays` and `initCurses` on a new `KnownObject`, whose constructor already calls them.
- [ ] `Player.java`: about eighty Javadoc blocks still quote C line numbers, which go stale as soon as a function is stamped.
- [ ] `PlayerOptions.java`: `restoreCustom` and `restoreMaintainer` are private, where C's `ui-options.c` calls both from the options menu, so they need widening when that menu is ported.
- [ ] `PlayerOptions.java`: `lazymoveDelay`, `delayFactor` and `nameSuffix` have no accessors, and `option_set` and `options_save_custom` are not ported yet.
- [ ] `RuneVariety.java`: the `BrandKey` block says C "stores just the name" and matches with `streq`, where C stores the array index of the first same-named brand and its callers resolve a brand to that index before `rune_index`.
- [ ] `PainAssembler.java`: the class and `assemble` blocks say an out-of-range serial skips the record, but the code only rejects a non-numeric one.
- [ ] `PainGrammar.g4`: a header comment cites a C line number for `init_parse_pain` in `mon-init.c`, which goes stale as soon as that file is stamped.
- [ ] `MonsterAssembler.java`: `freqInnate` and `freqSpell` are stored as the raw `innate-freq:` and `spell-freq:` values and default to 4 when absent, where C's `parse_monster_innate_freq` and `parse_monster_spell_freq` store `100 / pct` and leave an absent line at zero.
- [ ] `MonsterAssembler.java`: a friends or friends-base line with an empty or malformed number leaves `numberDie` null and then calls `numberDie.getDice()`, so the soft error is recorded but the assembler throws.
- [ ] `MonsterDataLoader.java`: nothing gives a loaded race a `MonsterLore`, where C's `finish_parse_monster` allocates one per race, so `PlayerBirth.java` throws on `lore.setPSkills` for the first real race.
- [ ] `MonsterAssembler.java`: the friends, friends-base, drops, mimic and shape lists are built in file order where C builds them in reverse, which matters once `place_friends` and the drop code consume random numbers as they walk them.
- [ ] `MonsterRace.java`: the `flagsOff` field and the nested `MonsterSpellMessages` record are dead, and nothing in the main code reads or builds either.

## 2026-10-11 (precis 261011.md)

- [ ] `MonsterSpell.java`: `RSF_ACID` to `RSF_MANA` (19 breath entries) duplicate `RSF_BR_ACID` to `RSF_BR_MANA` and have no counterpart in `list-mon-spells.h`, so the enum has 112 constants where C has 93 plus `NONE` and `MAX`, and every later ordinal differs from C's.
- [ ] `MonsterAssembler.java`: `hearing` is stored as written in `monster.txt`, where C's `parse_monster_hearing` multiplies it by `z_info->max_sight / 20`; the two agree only while `max_sight` is 20.
- [ ] `MonsterAssembler.java`: the friends, friends-base, drops, mimic and shape lists are built in file order where C builds them in reverse, which matters once `place_friends` and the drop code consume random numbers as they walk them.
- [ ] `LoreGrammar.g4`: `monsterLore` ends with `race.setLore($lore)`, which replaces the race's lore record where C's `lore_parser` fills `l_list[race->ridx]` in place, so once blank records exist a lore entry that omits a field would drop the blank's value for it (noted in `LoreReaderTest.java`'s class comment, parked with the reader).
- [ ] Savefile chapter: `src/main` has no counterpart of `wr_monster_memory` in `save.c` or `rd_monster_memory` in `load.c` (searched 26-10-11), so when it is ported, write only races with non-zero `pkills` or `thefts`, reset every `max_num` from race index 1 to 100 (1 for uniques) before reading, restore both counts, set `max_num` to 0 for a unique with a kill count, and check whether the reader's `continue` on an unknown race name skips the next `rd_string`.
- [ ] `LoreGrammar.g4`: the `@after` block of `monsterLore` builds `MonsterLore` with a 19-argument list that matches no constructor in `MonsterLore.java`, so the grammar and the class have drifted apart (parked with the reader).
- [ ] `LoreGrammar.g4` and `MonsterLore.java`: the lore reader will size blows per race where C's `parse_lore_blow` accepts any blow index below `mon_blows_max` and `finish_parse_lore` zeroes the count for a non-blow, so a hand-edited lore file with a high index would behave differently.
- [ ] `MonsterLore.java`: `caseInnate` is a slip for C's `cast_innate`, a rename for whenever the field is next touched.
- [ ] `ObjectPropertyAssembler.java`: a `subtype:` line on a non-flag record is rejected, where C's `parse_object_property_subtype` accepts it on any type; no shipped record does this, so it is inert for now.
- [ ] `ObjectPropertyAssembler.java`: a record with no `code:` line is reported as an invalid code and dropped, where C keeps it with index 0; every shipped record has a code, so it is inert for now.
- [ ] `ObjectProperty.java`: `adjective`, `negAdjective` and `description` have no getters, for `desc_stat` in `effect-handler-general.c` and the menus in `ui-knowledge.c` to use once they are ported.
- [ ] `BodyAssembler.java`: a `slot:NONE:` or `slot:MAX:` line is accepted because `EquipmentSlotsEnum.valueOf` finds both, where C's `lookup_flag` starts at index 1 and its table has no MAX, so `parse_body_slot` rejects `NONE` and cannot see `MAX`; no shipped body does this, so it is inert for now.
- [ ] `HistoryAssembler.java`: a non-zero successor that names no chart is reported and the chart keeps a null successor, where C's `finish_parse_history` fails the whole load; so a bad `history.txt` would load and end a biography early rather than stop the game. No shipped chart does this, so it is inert for now.
- [ ] `PlayerDataLoaderInitializeExpLevelTest.java`: the class and `EXPECTED` comments cite C line ranges in `player.c`, which should name the file and `player_exp` instead.
- [ ] `MonsterDataLoader.java`, `ObjectDataLoader.java`, `MiscDataLoader.java` and `DungeonLoader.java`: apply the same changes the player-slice loaders got in `PlayerDataLoader.java`: throw a `RuntimeException` naming the file on an `IOException` (several loaders still log and swallow it where C quits on a missing file) and on a `false` return from `ErrorParsing.reportAndCheck`, narrow any `catch (Exception e)` to `IOException`, drop any `throws IOException` that can no longer fire, and rewrite the Javadoc that still says "swallowed" (seen by search only, not read in full).
- [ ] `GameConstants.java`: the experience table is built after every data file, where C has it from compile time, and `Player.java` reads a missing level as 0 with `getOrDefault`, so any read before `initializeExpLevel` would silently see zero.
- [ ] `ObjectUtils.java`: `objectDesc` is a stub with no parameter for C's `ODESC_ALTNUM` count, so `ObjectGear.invenCarry` sets the flag but cannot pass the aggregate total, and the pickup message will show the stack's own count once the stub is ported.
- [ ] `PlayerBirth.java`: `playerMakeSimple` declares `raceNum` and `classNum` and never reads them.
- [ ] `PlayerCalcs.java` and `ItemSource.java`: the `calcBonuses` block still says a `Curse` has no object of its own, which `Curse.getItemObject()` contradicts, and both it and the `ItemSource` class block cite C line numbers in `player-calcs.c`.
- [ ] `ItemObject.java`: the `description` stub does not mark the ego and the kind `everseen` as `object_desc` in `obj-desc.c` does, so until it is ported the "You have ..." and "On the ground: ..." report in `PlayerKnowledge.knowObject` repeats on every knowledge update for an unseen ego or jewellery kind.
- [ ] `ObjectUtils.java`: `equipLearnAfterTime` is a stub in the wrong package; C's `equip_learn_after_time` calls `object_curses_find_flags` and `create_obj_flag_mask`, which are package-private and private in `PlayerKnowledge.java`, so it belongs there when it is ported.
- [ ] `obj-knowledge.c` and `player-util.c`: `object_find_unknown_rune`, `object_learn_unknown_rune`, `shape_learn_on_assume`, `object_learn_on_use`, `missile_learn_on_ranged_attack` and `death_knowledge` have no Java counterpart (searched 26-10-11), and `death_knowledge` is the second caller of `learnAllRunes`.
- [ ] `CurseData.java`: the `setPower` block says setting the power to zero is how a curse is removed, but `ItemObject.removeCurse` and `ItemObject.setCursePower` delete the map entry and `setPower` on its own leaves a zero entry in the map, which is why the `cursesFind*` finders still test the power.
- [ ] `PlayerFlavourAwareTest.java` and `docs/implementation/260816_functions_implemented.md`: both still call `object_set_base_known` "needs work" and name `Player.setBaseKnown`, though `PlayerKnowledge.objectSetBaseKnown` now matches C and has its own test.
- [ ] `PlayerKnowledge.java`: `cursesFindToA` is `public` where `cursesFindToD`, `cursesFindToH` and `cursesFindFlags` are package-private, though C keeps all four file-static.

## 2026-10-12 (precis 261012.md)

- [ ] `Quark.java` `merge` leaves a gap in the handles (the other table's handle 1 lands at the highest handle plus 2) and does not deduplicate text held by both tables. It has no C counterpart, so decide what it is for before any caller uses it.
- [ ] `Quark.java` `merge`, `size` and `containsText` throw a bare `NullPointerException` on an uninitialized table, without the logged message that `add` and `getQuark` give.
- [ ] `ObjectBaseAssembler.java` does not handle `IGNORE_` tokens, which C's `parse_object_base_flags` sends through `grab_element_flag`; they would be rejected as invalid flags. `object_base.txt` has none today, so this is dormant.
- [ ] `ItemObjectAssembler.java` does not handle `HATES_` tokens on an object kind, which C's `parse_object_flags` accepts. `object.txt` has none today, so this is dormant.
- [ ] The options-menu or `init_options` port needs a Java stand-in for C's `OPT_PAGE_MAX`, which is defined as `OP_SCORE` and so only works because of the type order; use an explicit page list (interface, birth, cheat) in whichever class holds `option_page`, not a constant on `PlayerOptionTypes`.
- [ ] `ClassMagicAssembler.java` now rejects more `book:` lines than declared, as C's `parse_class_book` does, but a book dropped for its own fault also drops the whole `magic:` record, so one typo reports two errors and the class loads as a non-caster.
- [ ] Decide whether any error in parsing should be fatal: C quits on every parse error (`run_parser` in `datafile.c`, `lookup_realm` in `player.c`), while the Java loaders collect soft errors and carry on, which is deliberate for now.
- [ ] `HistoryGrammar.g4` takes exactly one `phrase:` line per `chart:` line, where C's `parse_history_phrase` appends repeated phrase lines and accepts a chart line with none. The shipped `history.txt` has neither, so this is dormant.

## 2026-10-13 (precis 261013.md)

- [ ] `MonsterDataLoader.java`: the nine loaders read only the gamedata directory, where C's `parse_file` tries a customised copy in the user directory first.
- [ ] `GameConstants.java`: the comment on the `loadPitProfiles` call says it depends on the spell types, but the pit assembler resolves `spell-req:` and `spell-ban:` against the `MonsterSpell` enum.
- [ ] `DungeonLoader.java`: the three loaders read only the gamedata directory, where C's `parse_file` tries a customised copy in the user directory first, so a hand-edited file there is ignored.
- [ ] `MiscDataLoader.java`: the same user-directory lookup is missing from the three misc loaders.
- [ ] `NamesGrammar.g4`: a `word:` line before any `section:` line is still a hard error, where C files it under section zero.
- [ ] `SlayAssembler.java` and `BrandAssembler.java`: neither enforces C's 254-entry limit that `finish_parse_slay` and `finish_parse_brand` apply; `CurseAssembler.java` does.
- [ ] `ObjectRegistry.java`: `loadUnknownKinds` resolves the two sentinels but there is no Java `pile_kind`, which C sets in `finish_parse_artifact` and `ui-map.c` reads.
- [ ] `ObjectRegistry.java`: the chest traps list is private with no getter.
- [ ] `ObjectBaseAssembler.java`: appends a synthetic `none` base, so the base list is one longer than the 34 records in `object_base.txt`.
- [ ] `TerrainDataLoader.java`: the two loaders read only the gamedata directory, where C's `parse_file` tries a customised copy in the user directory first.
- [ ] `TerrainFeatureGrammar.g4`: a repeated `walk-msg`, `run-msg`, `hurt-msg`, `die-msg`, `confused-msg`, `look-prefix` or `look-in-preposition` line overwrites the earlier one, where C's `string_append` concatenates them as the grammar already does for `desc`.
- [ ] `TerrainFeatureGrammar.g4`: a record must give `code`, `name` and `graphics` in that order, where C needs only `code` first and takes the other directives in any order.
- [ ] `TerrainRegistry.java`: the `trapMax` Javadoc says a dropped trap record still uses up its index, but `TrapAssembler.java` numbers only the records that assemble; the blank-slot deviation is also not recorded there.
- [ ] `ColourEnum.java`: the `fromCode(String)` Javadoc says C's `color_text_to_attr` answers -1 for an unknown name, but the C source returns white.
- [ ] `MonsterRegistry.java`: `setMonsterPains` throws a `NullPointerException` on `null`, unlike `setSummons` which accepts it, so a test cannot restore an unloaded registry through the setter.
- [ ] `Accepted_Deviations.md`: the trap index-0 guard, the short terrain file with no entry for the missing code, and the fatal repeated code are in the Javadoc but not in the register, as you have not yet accepted them.

## 2026-10-14 (precis 261014.md)

- [ ] `PlayerQuest.java`, `TrapKind.java` and `ChunkUtils.java` import `DungeonRegistry` without using it.
- [ ] `DungeonRegistry.java`: the `logger` field is declared and never used.
- [ ] `WorldDataLoader.java`: the three loaders read only the gamedata directory, where C's `parse_file` tries a customised copy in the user directory first.
- [ ] `QuestGrammar.g4`: `(quest)+` needs at least one quest, so a `quest.txt` holding only its `record-count:` header is a syntax error, where C's `finish_parse_quest` loads an empty list with `quest_max` of 0; `WorldDataLoaderTest.emptyQuestFile` pins the current throw and should be flipped if the grammar changes.
- [ ] `DefaultGameInput.java`: the `logger` field is declared and never used.
- [ ] `MagicSpell.java`: no getter reads `realm`, `level`, `mana`, `fail`, `exp`, `effects` or `spellDescription` yet, so `spell_chance` and `spell_cast` have nothing to call.
- [ ] `MonsterFriends.java`: no getter reads `race`, `role`, `percentChance`, `numberDice` or `numberSides` yet, so `place_new_monster` and `lore_append_friends` have nothing to call.
- [ ] `MonsterAssembler.java`: a friends or friends-base die that `Random.parseStr` returns `null` for is reported as an error but the entry is still built, so `numberDie.getDice()` would throw a `NullPointerException`; the `FRIEND_DICE` token probably stops it reaching there.
- [ ] `MonsterAssembler.java`: a friends-base chance above 100 is still rejected, where C's `parse_monster_friends_base` accepts any unsigned value (it only ever behaves as 100).
- [ ] `MonTimedStack.java`: `MTS_MAX` is documented as a count sentinel and the class comment leaves out the "take the larger" rule, but `MonTimed` uses it as C's `STACK_MAX` for six effects.
- [ ] `EffectAssembler.java`: the `EST_MON_TMD` case uses `valueOf("MON_TMD_" + value)`, so `NONE` and `MAX` resolve as effects, where C's `mon_timed_name_to_idx` stops below `MON_TMD_MAX` and has no `NONE`.
- [ ] `MonTimed.java`: no getter reads any of its seven columns yet, so `mon_set_timed`, `mon_inc_timed`, `does_resist` and `monster_effect_level` have nothing to call.
- [ ] `EgoItem.java`: no getter reads `name`, `text`, `cost`, `kindFLags`, `minModifiers`, `rating`, the allocation figures, `possItems`, the three combat dice, the three minimums, `activation` or `time` yet, so `ego_apply_magic` and the ego allocation table have nothing to call.
- [ ] `EgoItem.java`: nothing calls `setEverSeen`, so `object_desc` and the savefile reader have not yet marked an ego as seen.
- [ ] `EgoItemAssembler.java`: a record dropped for an error does not use up an `egoIndex`, so the egos after it are numbered one lower than C would number them (C abandons the whole parse on an error).
- [ ] `ObjectIgnore.java`: `ignoreKnownItemOk` is a stub that returns `false` and has no player argument, so `ignore_known_item_ok` waits for the cave's object index.
- [ ] `ObjectIgnore.java`: no Java counterpart found for `init_ignore`, `cleanup_ignore`, `rune_autoinscribe`, `add_autoinscription`, `remove_autoinscription`, `object_ignore_flavor_of`, `kind_ignore_clear`, `ego_ignore`, `ego_ignore_clear`, `ego_ignore_toggle`, `kind_ignore_when_unaware` or `ignore_name_for_type`.
- [ ] `Player.java`: `playerFlags` reads `PF_BRAVERY_30` from the state being built, where C's `player_has()` reads the previous `p->state`, so the two differ only on the first calculation after birth.
- [ ] `PlayerCalcs.java`: `calcInventory` allocates a throwaway `new ItemObject()` for every `earlierObject` comparison.
- [ ] `PlayerCalcs.java`: `calcSpells` is a stub, so `PU_SPELLS` clears its flag and does nothing else until the magic subsystem is ported.
- [ ] `PlayerCalcInventoryTest.java`: the refusal to split a quiver stack once the pack is full is untested, because it turns on `packSlotsUsed`.
- [ ] `PlayerUpdateObjectKnowledgeTest.java`: the class comment still cites a C line number.
- [ ] `PlayerUtils.java`: the `playerHasObjectFlagIgnoringTimed` Javadoc still cites a C line number for `player_of_has_not_timed`.
- [ ] `PlayerClearTimedTest.java`, `PlayerDecTimedTest.java`, `PlayerIncTimedTest.java`, `PlayerSetTimedTest.java`, `PlayerIncCheckTest.java`, `PlayerTimedGradeEqTest.java`, `PlayerOfHasNotTimedTest.java` and `PlayerTimedAccessorTest.java`: the class and nested comments cite C line numbers.
- [ ] `GameWorld.java`: `onLeaveLevel`, `processWorld` and `decreaseTimeouts` still call the stub `PlayerTimed.clearTimed` and `incTimed`, so those three C calls do nothing until they move to the `player*` methods.
- [ ] `Effect.effectDo` and `PlayerUtils.disturb` are stubs, so the begin and end effect chains and the disturbance in `setTimed` are called but not yet observable.
- [ ] `PlayerTimedAssembler.java`: the `fail:` cases use `valueOf` on the enum, so a data line naming `NONE` or `MAX` for an element, or `NONE` for a player flag, object flag or timed effect, builds an entry, where C's `parse_player_timed_fail` rejects any name not in its list.
- [ ] `NumberUtils.java`: `variance` throws an `ArithmeticException` when the reduced fraction does not fit in a `long` (for example 5000 entries from `Random(1)` over +/-3,000,000 with `ofMean`), where C approximates and carries on; undecided, pinned by `NumberUtilsCParityTest`.
- [ ] `NumberUtils.java`: nothing calls `mean` or `variance` yet, so `obj-randart.c` has nothing to call.
- [ ] `ItemObject.java`: `compareObjectTrait` needs `NumberUtils.cmp` to give exactly -1, 0 or 1; `Integer.compare` does in OpenJDK but only promises a sign.
- [ ] `CommandGetter.java`: the Javadoc says `getCommand` returns 0 when a command was obtained, but C's `textui_get_cmd` returns 1 on every path, including after `textui_process_command` has queued one.
- [ ] `CommandGetterHolder.java`: nothing in `src/main` calls `getInstance()` yet, so `cmd_get_hook(CTX_GAME)` in `play_game()` has no Java counterpart.
- [ ] `MagicRealm.java`: the field `book` (a `TValue`) is declared but never assigned or read, and the `TValue` import goes with it.
- [ ] `MagicRealm.java`: there are no getters for the verb, spell noun or book noun, which C reads in `cmd-obj.c`, `player-calcs.c`, `player-util.c` and `ui-spell.c`.
- [ ] `RealmAssembler.java`: a record with no `stat:` line is dropped for an unknown stat, where C loads it with stat 0 (strength).
- [ ] `RealmGrammar.g4`: an absent `verb:`, `spell-noun:` or `book-noun:` line gives `""` where C leaves `NULL`.
- [ ] `MonsterDrop.java`: no getter reads `tVal`, `kind`, `percentChance`, `min` or `max` yet, so `mon_create_drop_count`, `mon_create_drop` and the lore save have nothing to call.
- [ ] `MonsterAssembler.java`: every `drop:` entry is added before every `drop-base:` entry, where C chains them in reverse file order whichever kind they are; no race in `monster.txt` has both, so only a custom file would show it.
- [ ] `MonsterAssembler.java`: a `drop:` or `drop-base:` min or max above 99 is accepted, where C's `parse_monster_drop` and `parse_monster_drop_base` reject it with `PARSE_ERROR_INVALID_ITEM_NUMBER`.

## 2026-10-15 (precis 261015.md)

- [ ] `TextBlock.java`: there are no accessors for the text and colours of a single cell, so `textblock_text` and `textblock_attrs` have no counterpart beyond `toString`, and the `ui-output.c` port will need per-cell access.
- [ ] `TextBlock.java`: nothing in `src/main` calls `calculateLines` or `toFile` yet, so `object_info_chardump`, `object_info_spoil` and `textui_textblock_show` have nothing to call.
- [ ] `Main.java`: both crash handlers identify a clean quit by `getMessage() == null` rather than by `GameQuitException`, so any other message-less `RuntimeException` would exit with status 0 and no log.
- [ ] `Main.java`: both handlers call `System.exit` straight after posting the shutdown message, so nothing waits for the other half and the UI `finally` never runs; the `Main` Javadoc now describes this rather than denying it.
- [ ] `ControlUtils.java`: `quit()` is still described as taking a "blank quit string" though it now passes `null`, and every quit helper hard-wires `new Quit()`, so `QuitAux` is never swapped as C's `quit_aux` is.

## 2026-10-16 (precis 261016.md)

- [ ] `FileHandler.java`: there is no counterpart for `file_lock`, `file_unlock` or `file_open_hook`.
- [ ] `FileHandler.java`: `file_lock`'s only caller is `highscore_write` in `score.c`, so decide when the *Death & scores* port starts whether to add a lock to `FileHandler` (with a thin `AngFile` method over it, opening the write side through a `FileChannel`) or to record its absence as an accepted deviation; `file_unlock` has no caller and need not be ported, since closing the lock file releases it.
- [ ] `FileHandler.java`: `getLine` keeps a NUL byte in the returned `String`, where C's buffer is a C string and the line ends at the NUL; shipped data has none.
- [ ] `FileHandler.java`: `file_get_tempfile` has no Java counterpart and needs porting when its callers come up in the roadmap (`z-textblock.c` and `ui-prefs.c`); it builds `base.ext` from a base name and an extension, and `FileHandler` is where it belongs next to `fileGetSavefile`.
- [ ] `GameConstants.java`: `getValues` logs the message with `logger.error` and then throws `InvalidTokenFoundDuringParse`, whose constructor logs it again, so the failure is recorded twice.
- [ ] `O_CriticalLevel.java`: the constructor logs each bad argument several times (the exception's constructor, then `logger.error(message, e)`, and for a bad `chance` a second exception is built and thrown after the first was discarded); one `throw new InvalidTokenFoundDuringParse(message)` is enough.

## Days with nothing recorded

2026-09-06, 2026-09-09, 2026-09-10, 2026-09-11, 2026-09-14, 2026-09-16, 2026-09-17, 2026-09-25, 2026-09-26, 2026-09-27, 2026-10-01, 2026-10-04; 260814 has no such section at all.
