# Chapter 7 worklist — Objects in hand

Items deferred to Chapter 7 from the day-to-day lists. Each was moved here from
`ThingsNotToDrop_UPTO_20261016.md`, with the wording of the original precis; "today" and line numbers
refer to the date given.

- [ ] `ItemObject.description` is deferred to Chapter 7; it is recorded in the Javadoc of everything that waits on it. (From 2026-08-16, precis 260816.md.)
- [ ] `equipLearnElement`, like the other messages here, prints `{DESCRIPTION_TAG}` for the item name until `ItemObject.description` is ported in Chapter 7, and `ObjectUtils.objectDesc` is a second stub returning an empty string that nothing in `PlayerKnowledge` calls any more; decide which of the two survives when the description subsystem is ported. (From 2026-10-07, precis 261007.md.)
- [ ] `equippedItemSlot` reports "not equipped" as a slot number in range, faithfully to C, so any future caller needs the guard C's own `inven_takeoff` carries and its `cmd-obj.c` caller does not. (From 2026-08-16, precis 260816.md.)
