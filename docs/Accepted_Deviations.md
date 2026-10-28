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

## ParseUtils

### An integer too big for its C type is clamped, where C wraps it

- **Java:** `ParseUtils.parseIntString` and `ParseUtils.parseString`, and every assembler that reads an integer
  through them. **C:** the `int` and `uint` parsing in `parser.c`, read into each `parse_*` handler's field.
- **Scope:** every integer field in every data file, unless a field is listed under **Excluded** below. A field is
  covered without being named here; only an exclusion needs an entry.
- **Excluded:** none.
- **Differs:** C reads the number with `strtol` (or `strtoul` for a `uint` field), which saturates at 64 bits, and
  stores it in a 32-bit field with no range check, so the low 32 bits are kept: `level:3000000000` becomes
  -1294967296 with no error, and `aim:4294967296` or `destroy:4294967296` becomes 0, so false. Java clamps to
  `Integer.MIN_VALUE` and `Integer.MAX_VALUE`, so the first becomes 2147483647 and the other two are true. A wrapped
  C value can be negative or tiny, and nothing downstream checks it. Examples from the activation fields:
  `get_use_device_chance` (`obj-util.c`) overflows its own arithmetic on a large `level`, `effects_power`
  (`obj-power.c`) adds a wrapped negative `power` to the item's power rating, and the random-artifact picker
  (`obj-randart.c`) lets a wrapped negative `power` past its `INHIBIT_POWER` filter.
- **Reach:** none from shipped data, where no integer comes near 32 bits. Hand-edited files only.
- **Revisit:** if a modded file ever needs C's exact wrapped value for a field, which is unlikely, since the wrapped
  value is the less useful one. Add that field under **Excluded** and handle it in its assembler.
- **Accepted:** 2026-10-22 for `activation.txt` (`aim:`, `level:`, `power:`), extended that day to every integer the
  activation assembler reads. Extended 2026-10-23 to every data file, by exclusion rather than inclusion, after the
  `ChestTrapAssembler` port pass found the same clamping on `level:`, `destroy:` and `magic:`. Marked in the
  `ParseUtils` class Javadoc.

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

## Activation

### A record with two faults reports the one the assembler reaches first, not the one nearest the top of the block

- **Java:** `ActivationAssembler.assemble` (`backend/parser/activation/`). **C:** `parse_act_*` in `obj-init.c`, with
  `print_error` in `datafile.c`.
- **Differs:** C parses `activation.txt` one line at a time and quits on the first line that fails, so within a record
  the fault reported is the one nearest the top of the block, and a `level:` line after a bad `effect:` line is never
  reached. Java collects the whole block first and then checks the records in file order, and within one record checks
  `aim:`, then `level:`, then `power:`, then the effects, whatever order the lines came in. Across records the two agree: the first
  bad record in the file stops the load.
- **Reach:** only a record with two or more faults, which stops the load either way. The load stops at the same record,
  and only the fault named in the message can differ. None on shipped data.
- **Revisit:** if the parse records ever carry the line of each directive, so a message can name the line C names.
- **Accepted:** 2026-10-22. The order is the order the records are read from the file.

### A fault is reported against the line the record starts on, not the line of the bad directive

- **Java:** `ActivationAssembler.assemble`, `ActivationParseRecord` and the `activation` rule in `ActivationsGrammar.g4`.
  **C:** `parse_act_*` in `obj-init.c`, with `print_error` in `datafile.c`.
- **Differs:** C parses one line at a time, so `print_error` names the line of the directive that failed: the
  `level:` line for a bad level. Java keeps one line number per activation parse record, the line of its `name:`, and
  every message says "beginning at line" with that number, whichever directive was at fault. Effect faults are the
  exception: `EffectParseRecord` carries its own line, so `EffectAssembler` names the effect's line.
- **Reach:** only the text of a message when the load stops. Which record stops the load is the same in both. A
  record has about five lines, so the number is at most a few lines out.
- **Revisit:** if a stop-the-load message is ever judged too hard to act on, give the record a line per directive.
- **Accepted:** 2026-10-22. Decided to keep one line per record. Related to the ordering entry above, which also
  names this as its revisit trigger.

## Artifact

### A special artifact's kind takes the artifact's finished level, where C's is 0

- **Java:** `ObjectKind(Artifact, String, ObjectBase)`, called from `ArtifactAssembler.assemble`. **C:**
  `write_dummy_object_record` (`obj-init.c`), called from `parse_artifact_base_object`.
- **Differs:** C sets `dummy->level = art->level` while the `base-object:` line is being read. At that point the
  artifact is a zeroed record holding only its name, because `level:` has not been read yet, so the kind's level is 0.
  In every record of `artifact.txt`, `level:` comes after `base-object:`, so all 14 special kinds (three lights, six
  rings and five amulets) get level 0 in C. Java assembles from a finished record, so it cannot see the line order, and
  the constructor copies the artifact's final level: the Phial's kind is level 5 in Java and 0 in C. C would copy the
  level only if a file put `level:` before `base-object:`.
- **Reach:** only code that reads the kind's level of a special artifact directly. C's artifact paths for activation
  level, device failure and the activation boost in the object description all test `obj->artifact` first, so they are
  unaffected. The one reader found is `object_learn_on_use` (`obj-knowledge.c`), whose experience gain uses
  `obj->kind->level`, so a special light, ring or amulet learned on use gives different experience. The two
  `obj->kind->level` reads in `effect-handler-general.c` were not traced.
- **Revisit:** if the experience from learning a special artifact on use needs to match C exactly, or when those
  `effect-handler-general.c` reads are ported and reach an artifact. Then the kind is built with level 0 instead of the
  artifact's.
- **Accepted:** 2026-10-22. The constructor Javadoc still says the level is copied "as C does", which is true of C's
  intent but not of its value; to be corrected to point here.

## Chest trap

### `chest_trap.txt` must match `ChestTrapCode` record for record, with levels that never descend

- **Java:** `ChestTrapAssembler.assemble`. **C:** the `parse_chest_trap_*` handlers in `obj-chest.c`.
- **Differs:** C stores the `code:` string and never reads it again, gives each trap its pval bit from its position in
  the file (the doubling in `parse_chest_trap_name`), and checks neither order, count nor level, so it loads any
  number of traps in any order. Java takes the pval bit from `ChestTrapCode.ordinal()`, so it ends the load on a code
  that is not a `ChestTrapCode` constant, a code out of declaration order, more or fewer records than there are
  constants, or a level lower than the record before it (equal levels pass, and the first record's level is checked
  against 0, so a negative one is refused).
- **Why:** the rules at the top of `chest_trap.txt` (the first entry is the no-trap, levels ascend, at most 14 traps)
  are ones C relies on without checking: `pick_one_chest_trap` always skips the first record, picks by position among
  the traps it counted by level, so a descending level lets a too-deep trap be picked, and a 15th trap's bit is the
  sign bit of the `int16_t` pval.
- **Reach:** none from shipped data, whose seven records are in `ChestTrapCode` order with levels 1, 1, 2, 3, 15, 19,
  25. A modded file that adds, removes or reorders a trap is refused whole, so a new trap needs a new `ChestTrapCode`
  constant and a rebuild.
- **Revisit:** if chest traps are to be moddable from data alone. Then `ChestTrap` stores its pval from its position,
  as C's struct does, and `code` becomes a label.
- **Accepted:** 2026-10-23, during the `ChestTrapAssembler` port pass.

## Curse

### A `conflict:` name that matches no curse ends the load, where C loads it and never matches it

- **Java:** `CurseAssembler.assemble` (the second pass). **C:** `parse_curse_conflict` (`obj-init.c`) and
  `curses_conflict` (`obj-curse.c`).
- **Differs:** C appends each `conflict:` name to the curse's `conflict` string as `|name|` and never checks it at load.
  `curses_conflict` later looks for `|name|` in that string with `strstr`, so an unknown name loads cleanly and simply
  never matches. Java links each name to a `Curse` instance after every curse is built, and ends the load with a
  `DataFileParseException` naming the curse and the unknown name when none matches.
- **Why:** the port stores conflicts as resolved `Curse` references rather than a name string, so an unknown name has
  nothing to resolve to, and a typo in a modded file is caught at load instead of silently disabling the conflict.
- **Reach:** none from shipped data. The four `conflict:` lines in `curse.txt` name `anti-teleportation`,
  `teleportation`, `chilled to the bone` and `burning up`, and all four are curses in the file.
- **Revisit:** if curses are to load modded files that name curses from elsewhere, or if conflicts go back to being
  matched by name.
- **Accepted:** 2026-10-23, during the `CurseAssembler` port pass. Marked in the `CurseAssembler` class Javadoc.

## Flavour

### A `fixed:` sval with trailing spaces or tabs is looked up as a name, where C reads it as a number

- **Java:** `FlavourKindAssembler.assemble` (the `fixed:` sval resolution). **C:** `lookup_sval` (`obj-util.c`), as
  called from `parse_flavor_flavor` (`init.c`).
- **Differs:** C reads the symbol with `strtoul` in base 10 and, if any digits were read, accepts a tail of spaces and
  tabs (`contains_only_spaces`), so `5 ` is 5. Java reads it with `Integer.parseInt`, which throws on the tail, so the
  symbol is matched against kind names instead, finds none, and the flavour stores 255 (C's -1 in a `uint8_t`).
- **Reach:** none from shipped data. All 8 `fixed:` lines in `flavor.txt` name their kind in words.
- **Revisit:** if a modded `flavor.txt` ever pins a flavour by number with a space or tab after it.
- **Accepted:** 2026-10-25, during the `FlavourKindAssembler` port pass. Pinned by
  `FlavourKindAssemblerNumericSvalTest.aTailOfSpacesOrTabsMakesTheSymbolANameNotANumber`.

## Monster

### A `friends:` name of `same` is resolved by name, so it finds the first race of that name, where C points at the race itself

- **Java:** `MonsterAssembler.assemble` (the `friends:` loop), with `MonsterRace.resolveFriends` and
  `MonsterFriends.setRace` doing the second pass. **C:** `finish_parse_monster` (`mon-init.c`).
- **Differs:** C tests each friend name with `my_stricmp(f->name, "same")` and, on a match, sets `f->race` to the race
  being converted. Any other name goes through `lookup_monster`. Java swaps `same` (any case) for the owning race's name
  in the assembler, and the second pass then looks that name up like any other, which returns the first race whose name
  matches. Where two races share a name, the later one's `same` friend resolves to the earlier race.
- **Why:** `MonsterFriends` carries only a name until every race exists, so a self-reference has nothing else to hold,
  and substituting the name keeps the second pass a single lookup.
- **Reach:** none from shipped data. `monster.txt` has no two races with the same name; `giant white ant` and
  `old forest tree` are the only races with a `same` friend, and each is the only race of its name.
- **Revisit:** if a modded `monster.txt` ever repeats a race name, or the friend is made to keep a reference to its
  owner.
- **Accepted:** 2026-10-26, during the `MonsterAssembler` port pass.

### A `flags-off:` or `glyph:` line before `base:` is not overridden by the base, where C overrides it

- **Java:** `MonsterAssembler.assemble` (the flag seeding and the glyph choice). **C:** `parse_monster_base`,
  `parse_monster_glyph` and `parse_monster_flags_off` (`mon-init.c`).
- **Differs:** C reads the file a line at a time. `parse_monster_base` copies the base's glyph into `d_char` and unions
  the base's flags into the race when it reads `base:`, so a `glyph:` line before `base:` is overwritten by the base's
  glyph, and a `flags-off:` line before `base:` removes nothing and has the base add the flag back. (A `flags:` line
  before `base:` ends the same either way, because both only add.) Java assembles from the finished record, which keeps
  no line order, so the base is applied first: an explicit `glyph:` always wins, and `flags-off:` always removes.
- **Differs, second part:** the same lost order shows between `flags:` and `flags-off:` lines. `parse_monster_flags`
  and `parse_monster_flags_off` act on the race one line at a time, so `flags-off:X` followed later by `flags:X` leaves X
  on, and `flags:X` followed by `flags-off:X` leaves it off. The grammar collects the two directives into two lists, and
  `MonsterAssembler.assemble` applies every `flags:` entry and then every `flags-off:` entry, so X always ends off.
- **Why:** the parse record holds each directive once, in no order. Java's reading is the one the file's author meant,
  and keeping line order would mean carrying every directive's position through the grammar for no gain on real data.
- **Reach:** none from shipped data. `base:` follows `name:` (or `plural:`) in all 624 records, ahead of every `glyph:`,
  `flags:` and `flags-off:` line. Only 9 records have a `flags-off:` line, none has more than one, none has one before a
  `flags:` line, and none names a flag in both (checked 2026-10-26). Modded files only.
- **Revisit:** if a modded `monster.txt` ever puts `base:` after a `glyph:` or `flags-off:` line, writes `flags-off:`
  before a `flags:` line that names the same flag, or the record is made to keep line order (one ordered list of flag
  actions in `MonsterParseRecord` and `MonsterGrammar.g4`). The `spell-power:` before `depth:` case is not part of this:
  `MonsterGrammar.g4` reproduces C's order for it.
- **Accepted:** 2026-10-26, during the `MonsterAssembler` port pass; the `flags:`/`flags-off:` order part folded in the
  same day. Marked in the `MonsterAssembler` comments at the flag seeding and the glyph choice. Pinned by
  `MonsterReaderTest.aFlagsOffLineBeforeBaseRemovesTheBaseFlagHereWhereCAddsItBack` and
  `MonsterReaderTest.aGlyphLineBeforeBaseKeepsItsGlyphHereWhereCOverwritesItWithTheBases`. The new part has no test yet.

### A second `base:` line replaces the first, where C keeps the flags of both

- **Java:** `MonsterAssembler.assemble` (the flag seeding) and the `monster` rule in `MonsterGrammar.g4`, which keeps one
  `base:` string per record. **C:** `parse_monster_base` (`mon-init.c`).
- **Differs:** C runs `parse_monster_base` for every `base:` line, so a record with two of them unions both bases' flags
  into the race and takes the glyph of the last one. Java keeps only the last `base:` string, so only that base's flags
  and glyph are applied and the first base's flags are lost.
- **Why:** the parse record holds each directive once. A race with two bases is not something the data format means.
- **Reach:** none from shipped data. All 624 records in `monster.txt` have at most one `base:` line (checked 2026-10-26).
- **Revisit:** if a modded `monster.txt` ever gives one race two `base:` lines, or the record is made to keep line order.
- **Accepted:** 2026-10-26, during the `MonsterAssembler` port pass.

### A `color-cycle:` line gives its race the cycle, where C stores every cycle in slot 0

- **Java:** `MonsterAssembler.assemble` (the `color-cycle:` block), passing the cycle to the `MonsterRace` constructor.
  **C:** `parse_monster_color_cycle` (`mon-init.c`) and `visuals_cycler_set_cycle_for_race` (`ui-visuals.c`).
- **Differs:** C calls `visuals_cycler_set_cycle_for_race` while the race is still being parsed, and that function stores
  the cycle at `race->ridx`. `ridx` is 0 for every race until `finish_parse_monster` sets it, so all nine `color-cycle:`
  lines in `monster.txt` write slot 0 and the last one wins. `visuals_cycler_get_attr_for_race` (called from
  `ui-display.c`) then finds nothing for any real race, so no monster flickers through its cycle. Java gives each race
  the cycle its own line names.
- **Why:** the cycle table is keyed by race index, which does not exist at assembly time, and C's result is a bug in the
  lookup order, not a behaviour the data means. Each race carrying its own cycle is what the data files intend.
- **Reach:** the nine races in `monster.txt` with a `color-cycle:` line (`fancy:crystal`, `storm`, `ice`, `swamp`,
  `hell`, `bile`, `thunder`, `law`, `balance`) cycle colours in Java and stay fixed in C.
- **Revisit:** if upstream fixes the ordering, or the visuals port is made to key cycles by race index as C does.
- **Accepted:** 2026-10-26, during the `MonsterAssembler` port pass. Marked in the `MonsterAssembler` comment above the
  cycle lookup. Pinned by `MonsterReaderTest` (the `fancy:crystal` case, which expects a non-null cycler).

### A `spell-power:` line with a minus is accepted when a later `depth:` line overwrites it, where C refuses it

- **Java:** `MonsterAssembler.assemble` (the spell power block) and the `monster` rule in `MonsterGrammar.g4`, which keeps
  the text of the last `depth:` or `spell-power:` line in `spellPower` and tags which one it was in
  `spellPowerOrDepth`. **C:** `parse_monster_depth` and `parse_monster_spell_power` (`mon-init.c`), and the `uint` branch
  of `parser_parse` (`parser.c`).
- **Differs:** C refuses a `uint` token that starts with `-` (`-0` included) as the line is read, whatever follows, so
  `spell-power:-1` ends the load even if a `depth:` line comes later. Java tests the sign only on the winning line, so
  `spell-power:-1` then `depth:20` loads with spell power 20. A negative `depth:` on its own loads in both (C stores the
  signed level and copies it to `spell_power`), and a negative winning `spell-power:` is refused in both.
- **Why:** the parse record holds one spell-power text per race, so a line that was overwritten is gone before the
  assembler runs. Refusing it would mean the grammar tracking every `spell-power:` line.
- **Reach:** none from shipped data, which has no negative `depth:` and no negative `spell-power:`. Modded files only.
- **Revisit:** if a modded `monster.txt` ever writes a negative `spell-power:` before a `depth:` line, or the record is
  made to keep line order.
- **Accepted:** 2026-10-26, during the `MonsterAssembler` port pass. Marked in the `MonsterAssembler` comment above the
  spell power block. Not pinned by a test yet.

## Object property

### A `type-mult:` tval written twice under two spellings can keep the earlier line, where C keeps the later one

- **Java:** `ObjectPropertyAssembler.assemble` (the `type-mult:` loop) and the `objectProperty` rule in
  `ObjectPropertyGrammar.g4`, which keeps the lines in a `LinkedHashMap` keyed by the raw tval text. **C:**
  `parse_object_property_type_mult` (`obj-init.c`).
- **Differs:** C resolves each line to a tval with `tval_find_idx` as it reads it and writes `type_mult[tval]` at once,
  so the last line for a tval wins whatever its spelling. Java merges repeats by spelling first and resolves to a tval
  afterwards, in the order each spelling first appeared. `soft armor:2`, then `soft armour:3`, then `soft armor:4`
  ends at 4 in C and at 3 in Java, because the repeated `soft armor` keeps its first slot and `soft armour` is
  resolved after it. The same spelling repeated with no alias between them is last-wins in both, and so are two
  spellings each written once.
- **Why:** the parse record holds one multiplier per spelling, so the file order across spellings is gone before the
  assembler runs. Keeping it would mean the grammar resolving tvals, or the record carrying the lines as a list.
- **Reach:** none from shipped data, where no record names a tval twice. Modded files only.
- **Revisit:** if a modded `object_property.txt` ever repeats a tval under two spellings, or the record is made to keep
  line order.
- **Accepted:** 2026-10-28, during the `ObjectPropertyAssembler` port pass. Marked in the `ObjectPropertyAssembler`
  comment above the `type-mult:` loop. Pinned by
  `ObjectPropertyAssemblerTest.aRepeatedSpellingAfterAnAliasKeepsTheAliasHereWhereCKeepsTheLastLine`.

## Pending your decision

- The five `objDescNameFormat` divergences (Part G; `docs/ItemObject_round2/part_G.md`). All unreachable from shipped
  data; four are already in the Javadoc and the fifth (a `~` inside a kept `|x|y|` alternative) is new. Not accepted
  until you say so.

## Call to action

- Say which of the pending `objDescNameFormat` cases to accept, and I will move them into the ItemObject section.
- Check `docs/ItemObject_round2/summary.md` item 4: it now carries both `ACCEPTED` and `FIXED` as its **Result**, and
  they cannot both be true.
