# FileHandler.java — `/port` stage 1 findings (2026-10-16)

Checked against `z-file.c`. The file is `src/main/java/uk/co/jackoftradesltd/backend/io/FileHandler.java`.

The earlier six points (`skipBytes` in-bounds and past-the-end, the two stale-stream opens, `fileGetSavefile`'s RNG and
locale) are closed in the code. `fileGetSavefile`'s `debug` and `seed` fields are test hooks, by Rowan's decision.

Points are sorted by line number, highest first, so fixing one does not move the line numbers of the ones still to do.
The numbers were read on 2026-10-16. Once the file has been edited further, trust the order and search for the named code.
Stages 2 to 5 of `/port` run only after these are cleared.

## (1) A write on a handle opened for reading appends instead of failing

- **Line:** `FileHandler.java:577`, `FileHandler.java:441` and `FileHandler.java:392`.
- **Explanation:** `putLine`, `write` and `writeCharacter` open the file in append mode when `outputStream` is null.
  On a handle opened with `MODE_READ` that closes the reader and appends to the file.
- **Explanation:** C's `file_put`, `file_write` and `file_writec` return false there. `AngFile` refuses the write before it
  gets this far, so nothing reaches it today. `FileHandlerLazyOpenTest` pins the lazy open, but `docs/Accepted_Deviations.md`
  does not record it.
- **Call to action:** record the lazy open as an accepted deviation, or make the three writers refuse when the handle is in read mode.

## (2) A savefile is created with default permissions, not `0600`

- **Line:** `FileHandler.java:293`.
- **Explanation:** C opens a `FTYPE_SAVE` write with `O_CREAT | O_EXCL` and mode `S_IRUSR | S_IWUSR` in `file_open`.
  `openOutputStream(CREATE_NEW, WRITE)` passes no permission attribute, so the file gets `0666` minus the umask, typically `0644`.
- **Explanation:** `docs/Accepted_Deviations.md` records the same kind of difference for `dirCreate` (`0755`), but nothing for savefiles.
- **Call to action:** apply `0600` where the file system supports POSIX attributes, or record the deviation as the `dirCreate` one is.

## (3) The `skipBytes` deviation entry is out of date

- **Line:** `docs/Accepted_Deviations.md:153-165` (the `skipBytes` entry).
- **Explanation:** the entry says a forward skip that falls short of the end of the file returns false.
  `FileHandler.java:324-327` now returns true there, which matches C's `fseek`.
- **Explanation:** only the negative-count refusal (`FileHandler.java:328`) is still a deviation.
- **Call to action:** reword the entry, and its heading, to cover the negative count only.

## (4) `FileHandlerSavefileNameTest` tests the old design

- **Line:** `FileHandlerSavefileNameTest.java:104`, `FileHandlerSavefileNameTest.java:108`, `FileHandlerSavefileNameTest.java:125`,
  `FileHandlerSavefileNameTest.java:129`, `FileHandlerSavefileNameTest.java:149` and `FileHandlerSavefileNameTest.java:157`.
- **Explanation:** the tests seed with `RandomValueUtils.stateInit`, which `fileGetSavefile` no longer reads.
  `exhaustedRetriesReturnLastCandidate` fails in the `backend.io` run; the others pass only because the clock-seeded names rarely collide.
- **Call to action:** stage 3 rewrites them to set `debug` to true and `seed` to a chosen value.

## Out of scope

- **Line:** `FileHandler.java:575-589`.
- **Explanation:** `putLine` writes a NUL character if the string holds one, where C's `file_put` stops at it. Shipped data has none.
- **Call to action:** none needed unless a caller can pass one.
