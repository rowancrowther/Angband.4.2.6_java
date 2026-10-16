# Accepted deviations from C

One register of every place the Java port knowingly behaves differently from Angband 4.2.6, and you have decided to
leave it. Add an entry when you accept a divergence; do not add findings you have not decided on.

Each entry gives the Java symbol, the C counterpart, what differs, why it cannot bite today, and when to revisit. No
line numbers (C or Java), as the house rule says; the Javadoc on the symbol carries the detail.

Seeded 2026-10-07 from `docs/ItemObject_round2/summary.md` (the "What was accepted as a divergence" list). The older
batch lists in `docs/ItemObject_port_batches.md` are not in the repo, so entries that only the summary names are
recorded here as the summary states them; fill in the detail when you next touch the symbol.

## ItemObject

### `copyCurses` with a non-null all-power-0 source

- **Java:** `ObjectUtils.copyCurses`. **C:** `copy_curses` and `curses_are_equal` (`obj-curse.c`).
- **Differs:** C allocates a zeroed `obj->curses` before skipping power-0 entries, and `curses_are_equal` treats that
  array as different from `NULL`. Java leaves an empty map, which `cursesAreEqual` reads as `NULL`, so it says equal.
- **Reach:** none from shipped data (no power-0 `curse:` line in `object.txt`, `ego_item.txt` or `artifact.txt`).
  Hand-built sources only.
- **Revisit:** if a fixture or generator ever builds a power-0 curse entry.
- **Accepted:** 2026-10-07. Marked in the `copyCurses` Javadoc.

### `addCurse` and `removeCurse` are simplifications

- **Java:** `ItemObject.addCurse`, `removeCurse`. **C:** `append_object_curse`, `remove_object_curse`.
- **Differs:** deliberate simplifications, not ports (Part A).
- **Revisit:** when curse application (`apply_curse`) is ported.

### `copy(false)` leaves `known` null

- **Java:** `ItemObject.copy`. **Source:** batch 2 list, as the summary records it.

### `flavourIsAware` on a kindless item

- **Java:** `flavourIsAware` against C's `object_flavor_is_aware`.
- **Differs:** only the null-kind answer differs (Part F). Batch list, as the summary records it.

### `hasFlag` ignores curse flags

- **Java:** `ItemObject.hasFlag`. **C:** `obj_has_flag`.
- **Differs:** no curse check.
- **Reach:** C's only caller is `obj_can_takeoff`, which is not ported.
- **Revisit:** when `obj_can_takeoff` is ported.

## Message

### The message log always exists, so C's `if (!messages) return;` has no counterpart

- **Java:** `Message.message`, `Message.messageType`. **C:** `msg`, `msgt` (`message.c`).
- **Differs:** C's `messages` pointer starts `NULL`, is allocated by `messages_init` and freed by `messages_free`, and
  `msg` and `msgt` return silently while it is `NULL`. Java's `messageLog` is a `static` field initialised at
  declaration and never reassigned, so there is no "store not loaded" state, and neither `message` nor `messageType`
  guards on it. There is no port of `messages_init` or `messages_free`.
- **Reach:** none in play. Messages load at startup, before any item can be learned, so `modMessage` and every other
  caller run with the log present in both languages.
- **Revisit:** if `messages_init` and `messages_free` are ported with the init modules. Then `messageLog` starts `null`
  and both `message` and `messageType` guard on it.
- **Accepted:** 2026-10-08. From `docs/ItemObject_round2/summary.md` item 12 (Part G). Marked in the `message` and
  `messageType` Javadoc.

## Monster base

### `MonsterBase` holds C's one `text` member twice

- **Java:** `MonsterBase.inGameName` and `MonsterBase.description`; `MonsterBaseParseRecord.inGameName` and
  `description`; the `monsterBase` rule in `MonsterBaseGrammar.g4`. **C:** `monster_base->text` (`monster.h`), filled
  by `parse_mon_base_desc` (`mon-init.c`).
- **Differs:** C has one `text` member, which the C comment calls the in-game name and the data file calls the
  description. Java stores the same `desc:` text in two fields, and the grammar passes it for both.
- **Reach:** none. Both fields are always equal for a base read from `monster_base.txt`. Neither has an accessor yet.
- **Revisit:** when the first accessor is added, or when the record is next reworked. Then drop one component from the
  record, the grammar, the assembler and the `MonsterBase` constructor.
- **Accepted:** 2026-10-10. Marked in the `MonsterBase` and `MonsterBaseParseRecord` Javadoc.

## Terrain

### An unknown terrain colour is rejected, where C reads it as white

- **Java:** the `COLOUR_VALUES` token in `AngbandDisplayCharacter.g4` (used by `TerrainFeatureGrammar.g4`) and
  `TerrainFeatureAssembler.assemble`, through `ColourEnum.fromCode`. **C:** `parse_feat_graphics` (`init.c`), with
  `color_text_to_attr` and `color_char_to_attr` (`z-color.c`).
- **Differs:** both C look-ups fall back to `COLOUR_WHITE` for a colour they do not know, so a `graphics:` line never
  fails in practice and `PARSE_ERROR_INVALID_COLOR` is unreachable. Java has lexer tokens only for the colours in C's
  table, so an unknown colour is a hard grammar error that fails the whole file. If a token ever got past the lexer,
  the assembler drops the record with an `Unknown colour` soft error, and the load carries on without an entry for
  that code.
- **Reach:** none from shipped data; every `graphics:` line in `terrain.txt` names a colour in C's table. Hand-edited
  files only.
- **Revisit:** if a hand-edited terrain file is ever meant to load with a white fallback, or if the colour handling of
  the other readers is brought back into line with C.
- **Accepted:** 2026-10-13. Pinned by the `ColourCheck` tests in `TerrainDataLoaderFeaturesTest`; marked in the
  `TerrainDataLoader` Javadoc.

## Rational

### `Rational` holds signed 63-bit values, where C's `my_rational` holds unsigned 32-bit ones

- **Java:** `Rational` (`multi`, `add`, `toUint`, and the constructors). **C:** `struct my_rational`,
  `my_rational_construct`, `my_rational_product`, `my_rational_sum` and `my_rational_to_uint` (`z-util.c`, `z-util.h`).
- **Differs:** C stores two `unsigned int` and, when an exact result will not fit, approximates and saturates at
  `UINT_MAX`. Java stores two `long`, runs the same approximation algorithm at `Long.MAX_VALUE`, and saturates at
  `Long.MAX_VALUE`. Results agree exactly while every numerator and denominator stays below 2^32; above that Java is
  exact where C has approximated (`4294967295 + 1` is `4294967296/1` in Java and `4294967295/1` in C). `toUint` does not
  saturate at `UINT_MAX` either. Java also takes negative values and a negative denominator, and throws
  `IllegalArgumentException` on a zero denominator where C asserts.
- **Reach:** none from the game. C's callers (`obj-info.c`, `player-path.c`, `obj-randart.c`, `wiz-stats.c`, and `mean`
  and `variance` in `z-util.c`) work on dice, counts and turn figures far below 2^32.
- **Revisit:** if a port ever needs C's exact `UINT_MAX` saturation, for example to reproduce a displayed figure.
- **Accepted:** 2026-10-14. Marked in the `Rational` Javadoc.

## NumberUtils

### `variance` saturates at `INT_MAX` for a value between `INT_MAX` and `INT_MAX + 1`

- **Java:** `NumberUtils.variance`. **C:** `variance` (`z-util.c`).
- **Differs:** C returns `INT_MAX` with a zero fraction only when the whole part of the variance is above `INT_MAX`,
  that is when the value is at least `INT_MAX + 1`. A value such as `INT_MAX + 1/2` comes back as `INT_MAX` with the
  fraction kept. Java returns one `Rational` and tests whether the value is above `Integer.MAX_VALUE`, so that
  `INT_MAX + 1/2` becomes `INT_MAX` exactly and the fraction is lost. Values up to `INT_MAX` and values from
  `INT_MAX + 1` upward agree.
- **Reach:** none shown. A differential run of 80,000 random cases against the compiled C found no difference, and a
  search for a small integer input inside the range found none (n = 2 cannot produce one).
- **Revisit:** if a displayed or compared variance ever needs the fraction of a value just above `INT_MAX`.
- **Accepted:** 2026-10-14. To be marked in the `variance` Javadoc by the stage 2 pass.

## FileHandler

### `getLine` does not cut a line inside a multi-byte UTF-8 character

- **Java:** `FileHandler.getLine`. **C:** `file_getl` (`z-file.c`).
- **Differs:** C fills a byte buffer and stops at `len - 1` bytes (or at the tab `break`), whatever the bytes are, so a
  multi-byte character can be cut in half and its first bytes returned as stray bytes. Java collects the same bytes,
  but when the line stopped because the buffer was full and the last bytes are an incomplete UTF-8 sequence, it drops
  them from the line and pushes them back onto the stream, so the next call starts on a whole character. The line can
  come back up to 3 bytes shorter than `len - 1`. The test is made on the bytes (lead byte against bytes present), never
  by looking for `�`, and never at a newline or end of file. Everything else (the byte counting, the tab stops, the
  `\r` rules) follows C.
- **Differs, second part:** `getLine` also refuses a `len` below 5 by throwing `IllegalArgumentException`, checked before
  anything else, so it does not depend on whether the handler is open. A buffer of `len - 1` bytes under 4 cannot hold
  the longest UTF-8 character, and the pushback above would then hand back an empty line without consuming a byte, so a
  caller looping on `getLine` would never finish. C accepts any `len`; `getLine(1)` to `getLine(4)` work there. The
  refusal throws rather than returning `null`, because `null` already means end of file.
- **Reach:** none from shipped data. Only a line whose `len - 1` cut-off falls inside a multi-byte character can show
  the pushback, and nothing passes a `len` under 5. The one caller, `AngFile.getLine`, passes 1024, and the longest line
  in any `.txt` or `.prf` file under `lib/` is 194 bytes (checked 2026-10-16). Hand-edited files only.
- **Revisit:** if a caller ever needs the line split at exactly `len - 1` bytes, as C does, or needs a `len` under 5.
- **Accepted:** 2026-10-16. Decided in 2026-10-16. The `len < 5` refusal was added the same day (option D of five
  considered) and is pinned by `lenBelowFiveThrows` and its neighbours in `FileHandlerGetLineTest`. To be marked in
  the `getLine` Javadoc by the stage 2 pass.

### `skipBytes` refuses a negative count

- **Java:** `FileHandler.skipBytes`, reached through `AngFile.fileSkip`. **C:** `file_skip` (`z-file.c`), which is
  `fseek(f->fh, bytes, SEEK_CUR)`.
- **Differs:** `fseek` seeks backwards for a negative offset. Java's read side is a `PushbackInputStream` over a
  `BufferedInputStream`, which cannot move backwards, so a negative count returns `false` and leaves the position where
  it was. This is the only deviation. Every forward case follows C: a zero count returns `true` and does not move, a
  skip of exactly the remaining bytes returns `true` and leaves the stream at end of file, and a skip past the end of
  the file also returns `true`, as `fseek` does on a regular file, with the next read reporting end of file.
- **Reach:** none. The only C caller is `savefile.c`, which skips forward over a block that exists.
- **Revisit:** if a port ever needs a backward skip. The route is a `FileChannel` plus a running count of the bytes
  consumed, rebuilding the stream chain after each seek.
- **Accepted:** 2026-10-16. The refusal is pinned by `fileSkipRefusesANegativeCountAndLeavesThePosition` in
  `AngFileTest`; the forward cases that agree with C are pinned by `fileSkipOfZeroSucceedsAndDoesNotMove` there and by
  `fileSkipOfExactlyTheRemainingBytesSucceedsAndLeavesEndOfFile` and
  `fileSkipPastTheEndReturnsTrueOnceTheStreamHasBeenRead` in `AngFileOpenReadTest`. To be marked in the `skipBytes`
  Javadoc when it is written.

### `fileNewer` compares modified times at full precision, where C compares whole seconds

- **Java:** `FileHandler.fileNewer`. **C:** `file_newer` (`z-file.c`), which compares `st_mtime`.
- **Differs:** C's `st_mtime` is whole seconds, so two files modified within the same second are never newer than
  each other. Java compares the two `FileTime` values at the finest precision the file system reports, so two files
  modified 0.3s apart in the same second give true where C gives false. The existence rules (a missing first file is
  false, a missing second file is true) are the same, and so is the strict "later than" test.
- **Reach:** only a pair of files whose modified times fall in the same second. C's callers are the panic-save check in
  `ui-game.c`, which asks whether the panic file is newer than the savefile, and the tile-set check in `main-win.c`.
  Where the two were written in one second, C deletes the panic save as out of date and Java offers it. Hand-timed
  files only, and the offer is the safer way round.
- **Revisit:** if a caller ever needs C's whole-second answer, for example to reproduce a C save-and-panic ordering
  exactly. The route is to truncate both times to seconds (`FileTime.to(TimeUnit.SECONDS)`) before the comparison.
- **Accepted:** 2026-10-16. Already stated in the `fileNewer` Javadoc ("at the full precision the file system reports,
  with no rounding to whole seconds"). Open issue 11 in `docs/FileHandler_open_issues.md` is ticked on this basis.

### `fileGetSavefile` can repeat a name within one millisecond, and does not cut a long name

- **Java:** `FileHandler.fileGetSavefile`. **C:** `file_get_savefile` (`z-file.c`), which draws from `Rand_simple`
  (`z-rand.c`).
- **Differs:** `Rand_simple` keeps a static seed that evolves from one call to the next, so two calls in quick
  succession give different numbers. Java builds a new `java.util.Random` on every call and seeds it from
  `System.currentTimeMillis()`, so two calls in the same millisecond, with no file created between them, draw the same
  number and return the same name. The draw still comes from its own generator, so naming a file never disturbs the
  game's RNG, as in C. When `debug` is true the seed is not read from the clock but taken from the `seed` field, so a
  test can fix the sequence.
- **Differs, second part:** C takes a `max` (the size of the caller's buffer) and `strnfmt` cuts the name to
  `max - 1` characters, so a very long `base` loses the end of the name, number and extension included. Java has no
  `max` and returns the full-length name as a `String`.
- **Reach:** none. The only C caller, `savefile.c`, asks for two names back to back, but with the extensions `old`
  and `new`, so an identical number still gives two different names. The retry loop (up to 100 times while the
  name exists) covers a name that is already on disk. The same caller passes a 1024-byte buffer, so the cut would
  apply only to a `base` of about 1000 characters or more, and Java's full name is the better answer there.
- **Revisit:** if a caller ever asks for two names with the same extension before creating either. The route is a
  persistent `Random` field on the handler, or a `Thread.sleep(1)` after the clock read. If a name ever has to fit a
  fixed length, add a `max` and cut as `strnfmt` does.
- **Accepted:** 2026-10-16. Decided in 2026-10-16. To be marked in the `fileGetSavefile` Javadoc by the stage 2 pass.

### `open` for read refuses a directory

- **Java:** `FileHandler.open` with `MODE_READ`, through `openInputStream`. **C:** `file_open` (`z-file.c`), which is
  `fopen(path, "rb")` for read.
- **Differs:** on Linux `fopen` of a directory succeeds, and only the first read fails, so `file_open` hands back a
  handle and `file_getl` then returns false at once. Java's `FileInputStream` refuses a directory at the open, so `open`
  returns false and no handle exists.
- **Reach:** only a data, prefs or config path that names a directory by mistake. C callers (`datafile.c`, `option.c`,
  `ui-prefs.c`, `savefile.c` and the rest) then see an empty file where Java callers see a missing one, and both take
  their "nothing to read" branch.
- **Revisit:** if a caller ever has to tell a directory from a missing file by whether the open succeeded.
- **Accepted:** 2026-10-16. Pinned by `openReadOnADirectoryFails` in `FileHandlerPathAndModeTest`, and marked in the
  `open` and `openInputStream` Javadoc.

### A handler opened for append is write-only

- **Java:** `FileHandler.open` with `MODE_APPEND`. **C:** `file_open` (`z-file.c`), which is `fopen(path, "a+")` for
  append.
- **Differs:** `"a+"` opens the file for reading as well as appending, so C could read from an append handle. Java opens
  the write side only; `readCharacter` returns -1, `getLine` and `read` return `null`, and `skipBytes` returns false.
- **Reach:** none. Every C caller that opens for append (`ui-prefs.c`, `borg/borg-log.c` and the pricing log in
  `obj-power.c`) only writes.
- **Revisit:** if a caller ever reads back from a handle it opened for append.
- **Accepted:** 2026-10-16. Pinned by `appendHandlerCannotRead` in `FileHandlerPathAndModeTest`, and marked in the
  `open` Javadoc.

### `read` throws on a negative size

- **Java:** `FileHandler.read`, reached through `AngFile.read`. **C:** `file_read` (`z-file.c`), whose size is a
  `size_t`.
- **Differs:** a `size_t` cannot be negative, so C has no such call. A negative Java `size` is refused by `readNBytes`
  with an `IllegalArgumentException`, and not with the `null` that stands for C's -1.
- **Reach:** none. The C callers (`savefile.c`, `score.c`, `main-spoil.c`) pass `sizeof` values and block sizes.
- **Revisit:** if a size is ever computed in Java and could go below zero, in which case the caller should check it.
- **Accepted:** 2026-10-16. Pinned by `readOfANegativeSizeThrows` in `FileHandlerPathAndModeTest`, and marked in the
  `read` Javadoc.

## AngFile

### `dirCreate` returns true for a fresh path given with a trailing slash

- **Java:** `AngFile.dirCreate`. **C:** `dir_create` (`z-file.c`).
- **Differs:** for a path that does not exist yet and ends in `/` (say `x/y/`), C's loop creates `x/y` at the trailing
  separator, and the final `mkdir("x/y/")` then fails with `EEXIST`, so C returns false although every directory now
  exists. Java returns true, because the directories were created. A path that already exists returns true in both
  (C's first line is `dir_exists`), and a fresh path with no trailing slash returns true in both.
- **Reach:** `create_needed_dirs` in `init.c` calls `dir_create` through `path_build(..., "")`, which hands back the
  base unchanged, so the result depends on whether the `ANGBAND_DIR_*` strings end in a separator (not checked). Where
  they do, a first run in C would stop with `Cannot create` even though the directory was made; Java carries on.
- **Revisit:** if a caller ever needs C's false, for example to detect a path it had to create.
- **Accepted:** 2026-10-16. To be marked in the `dirCreate` Javadoc when it is written.

### `dirCreate` creates directories with the platform's default permissions, not `0755`

- **Java:** `AngFile.dirCreate`. **C:** `dir_create` (`z-file.c`), which calls `my_mkdir(path, 0755)` for every
  directory it makes.
- **Differs:** C asks for mode `0755`, which the process umask then trims. Java calls `Files.createDirectories` with no
  attributes, so on a POSIX system each directory gets `0777` minus the umask. The two agree under the common umask of
  `022` (both give `755`). Under `002` Java makes `775` (group-writable), and under `077` both end up no more open than
  `700`. Passing an explicit permission attribute would reproduce C, but it works only on POSIX file systems and throws
  on Windows, which would break the port's OS neutrality.
- **Reach:** only a game directory created on a POSIX system with a umask looser than `022`. It affects who else can
  write to the user, save, panic, scores and archive directories, not whether the game runs.
- **Revisit:** if the game is ever run as a shared or setuid install, where those directories must not be
  group-writable. The route is to apply the permissions only when the file system supports POSIX attributes.
- **Accepted:** 2026-10-16. To be marked in the `dirCreate` Javadoc when it is written.

### `putFormattedLine` does not cut a line inside a multi-byte UTF-8 character

- **Java:** `AngFile.putFormattedLine`. **C:** `file_vputf` (`z-file.c`), which formats into a 1024-byte buffer.
- **Differs:** C keeps the first 1023 bytes of the formatted text whatever they are, so a multi-byte character that
  straddles the cut-off is written as stray leading bytes. Java encodes the text as UTF-8 first, then cuts at 1023 bytes,
  and if the first dropped byte is a continuation byte (`(b & 0xC0) == 0x80`) it steps the cut back to the lead byte so
  the whole character is dropped. The line can come back up to 3 bytes shorter than C's. A line of 1023 bytes or fewer
  is written whole, and a long line with no multi-byte character at the cut-off is cut at exactly 1023 bytes, as in C.
- **Reach:** only a formatted line over 1023 bytes whose cut-off falls inside a multi-byte character. Not checked
  against the `file_putf` callers.
- **Revisit:** if a caller ever needs the line cut at exactly 1023 bytes, as C does.
- **Accepted:** 2026-10-16. To be marked in the `putFormattedLine` Javadoc when it is written. Same shape as the
  `FileHandler.getLine` deviation above.

### `putFormattedLine` reads Java format strings, not the game's own `strnfmt` syntax

- **Java:** `AngFile.putFormattedLine`. **C:** `file_putf` and `file_vputf` (`z-file.c`), which format with `vstrnfmt`
  (`z-form.c`), the game's own replacement for `vsnprintf`.
- **Differs:** the format string is read by `String.format`, not by `vstrnfmt`. The two syntaxes overlap for `%s`, `%d`,
  `%c` and `%%`, but `vstrnfmt` also accepts the `l` modifier (`%ld`, `%lu`, `%lx`, `%lX`, `%lo`), the `*` width taken
  from an argument (`%*s`, `%*c`), and the game's `%v` callback. `String.format` has none of these and throws
  `UnknownFormatConversionException` for `%ld` and `%lu`. `putFormattedLine` catches every exception and returns `false`,
  so a C-style format string writes nothing and gives the caller no reason. The Java side has its own forms the C one
  lacks (`%n$` argument indexes, `%b`, `%,d`), and `%n` means a line separator rather than "save the length".
- **Reach:** none yet; no Java code outside `AngFile` calls `putFormattedLine`. In the C source, `file_putf` call sites use
  `%lu`, `%ld`, `%08lx`, `%*s` or `%*c` (`borg/borg-log.c`, `obj-randart.c`, `ui-player.c`, `ui-prefs.c`,
  `wiz-spoil.c`, `wiz-stats.c`, `z-textblock.c`), so each such site needs its format string rewritten as it is ported.
- **Revisit:** if a port wants to keep C's format strings unchanged, route `putFormattedLine` through a port of
  `vstrnfmt` instead of `String.format`.
- **Accepted:** 2026-10-16. To be marked in the `putFormattedLine` Javadoc when it is written, alongside the UTF-8 cut
  deviation above.

## Pending your decision

- The five `objDescNameFormat` divergences (Part G; `docs/ItemObject_round2/part_G.md`). All unreachable from shipped
  data; four are already in the Javadoc and the fifth (a `~` inside a kept `|x|y|` alternative) is new. Not accepted
  until you say so.

## Call to action

- Say which of the pending `objDescNameFormat` cases to accept, and I will move them into the ItemObject section.
- Check `docs/ItemObject_round2/summary.md` item 4: it now carries both `ACCEPTED` and `FIXED` as its **Result**, and
  they cannot both be true.
