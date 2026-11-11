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

### A leading `-` on `aim:` is read as a sign, not refused as C refuses it

- **Java:** `ActivationAssembler.assemble` (`backend/parser/activation/`), through `ParseUtils.isTrue`. **C:**
  `parse_act_aim` (`obj-init.c`), registered as `aim uint aim`, with the `PARSE_T_UINT` branch of `parser_parse()` in
  `parser.c`.
- **Differs:** C refuses any `uint` token whose first character is `-` (`*tok == '-'`) as `PARSE_ERROR_NOT_NUMBER`, even
  `-0`, so `aim:-1` stops the load. Java has no such test for `aim:` (it has one for `power:`), and `isTrue` reads the
  sign and then the digits: `aim:-1` loads as aim true and `aim:-0` loads as aim false. `aim:-` with no digit, and
  `aim:-abc`, are still refused, because `isTrue` finds no number.
- **Reach:** hand-edited files only. No shipped record has a `-` on `aim:`, and a negative aim has no meaning.
- **Revisit:** if a modded file ever needs the load to stop on a negative `aim:`, add the same first-character test
  that `power:` has.
- **Accepted:** 2026-11-07. Decided to leave it.

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

### A record with no `base-object:` line is refused, where C loads it as tval 0

- **Java:** `ArtifactAssembler.assemble`, which throws "has no assigned bases" when the record has no
  `base-object:` line. **C:** `parse_artifact_name` and `parse_artifact_base_object` (`obj-init.c`).
- **Differs:** C zero-fills the new artifact in `parse_artifact_name`, so `tval` and `sval` start at 0.
  `parse_artifact_base_object` is the only handler that sets them, and nothing at the end of a record checks that it
  ran, so an artifact with no `base-object:` line loads with tval 0. Java ends the load with a clear error instead.
- **Also read:** `lookup_kind` (`obj-util.c`) prints "No object: 0:0" and returns `NULL` when no kind has that tval and
  sval. What the later handlers (weight, cost, graphics, `act:`, `time:`) do with that `NULL` was not traced.
- **Reach:** none from shipped data. `artifact.txt` has 138 `name:` lines and 138 `base-object:` lines, so every
  record has one (checked 2026-11-05). A hand-edited or modded file only, and there the throw names the artifact.
- **Revisit:** if a modded `artifact.txt` is expected to omit `base-object:`. Loading it as tval 0 needs a null-safe
  path through the kind lookup, the special-kind test and the glyph handling that follow it in `assemble`.
- **Accepted:** 2026-11-06. Marked in the `assemble` Javadoc.

### A numeric `base-object:` sval that names no kind would fail on a `NullPointerException`, but the grammar never passes one

- **Java:** `ArtifactAssembler.assemble` (the base-object loop and the lookup after it), with
  `ObjectRegistry.lookupObjectKind(TValue, String)` and `ObjectRegistry.addObjectKind`. **C:**
  `parse_artifact_base_object` (`obj-init.c`) and `lookup_sval` (`obj-util.c`).
- **Differs:** for `base-object:light:99`, `lookup_sval` reads an all-digit name as the sval itself and returns 99
  without needing a kind. C sets `a->sval = 99`, makes no dummy kind and loads the artifact. Given the same sval,
  Java's lookup returns `null` when no kind has sval 99, so the loop makes a dummy kind named `"99"`, which
  `addObjectKind` registers under the next free sval. The lookup after the loop asks for sval 99 again, gets `null`,
  and the special-kind test throws a `NullPointerException` rather than a `DataFileParseException`. A numeric sval that
  does name a kind resolves to the same kind in both.
- **Reach:** none from a file. The lexer reads a number between the colons as `DELIM_INTEGER`, which the `baseObject`
  rule does not take, so a numeric sval is a syntax error before the assembler runs
  (`ArtifactReaderTest.aNumericTvalOrSvalInBaseObjectIsASyntaxErrorHereWhereCAcceptsIt`; see `grammar_issues.md`). Only
  an `ArtifactParseRecord` built by hand reaches the assembler with one. Shipped `artifact.txt` has one numeric line,
  `#base-object:amulet:55`, and it is commented out (checked 2026-11-07).
- **Revisit:** if the grammar is changed to take a numeric sval. Then a numeric reference that finds no kind ends the
  load with a clear error, or is carried as a bare sval as C carries it.
- **Accepted:** 2026-11-07. Marked in the `assemble` Javadoc. Found in the stage 1 check of `/port
  ArtifactAssembler.java`, which first reported it as reachable from a modded file; the grammar had already closed it.

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

## Effect block

### Text that is not a dice string, on a `dice:` line before the first `effect:`, ends the load, where C drops it unread

- **Java:** `DICE_STRING_MODE` in `imports/EffectBlockLexer.g4` (its dice shapes come from `imports/DiceStrings.g4`).
  **C:** `parse_trap_dice` (`init.c`), `parse_mon_spell_dice` (`mon-init.c`) and `parse_curse_dice` (`obj-init.c`).
- **Differs:** each C parser returns `PARSE_ERROR_NONE` before it reads the string when the record has no effect yet
  ("assume that this is human and not parser error"), so `dice:garbage` ahead of the first `effect:` is ignored. After
  the first effect `dice_parse_string` fails and the load stops with `PARSE_ERROR_INVALID_DICE`. The Java lexer refuses
  any `dice:` text that is not a valid dice string (`token recognition error`) in every position, before a parser rule
  can decide to drop the line, so the early case stops the load too. After the first effect Java and C agree.
- **Why:** the fix is to let the lexer take any text on a `dice:` line and check the string in a parser action, and
  only when an effect exists. That reaches every importer of the shared block, moves the validation `DiceStrings.g4`
  does in the grammar into code, and changes the error message and its position. It costs more than the case is worth.
- **Reach:** none from shipped data. It takes a mistyped dice string placed before any `effect:` in a hand-edited file.
  A valid dice string in that place is dropped and loads, as in C.
- **Revisit:** if the lexer is ever changed to take any text on a `dice:` line. Then the pin below goes red, which is
  the prompt to take this entry out.
- **Pinned by:** `aDiceLineOfGarbageBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt` in `TrapReaderTest`, with the
  control `aValidDiceLineBeforeAnyEffectIsDroppedAsInC`.
- **Accepted:** 2026-11-11, Stage 6 item 2.vi of `docs/Issue_Approach_Order.md`.

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

## History

### A chart repeated with a different successor stops the load, where C accepts it

- **Java:** `HistoryAssembler.assemble`, with `PlayerHistoryChart.successorNumber` and `successor`. **C:**
  `parse_history_chart` and `finish_parse_history` (`init.c`), `get_history` (`player-birth.c`).
- **Differs:** C stores the successor (`isucc`, then `succ`) on every entry, `parse_history_chart` never compares it,
  and `get_history` follows the rolled entry's own `succ`, so one chart can lead to different charts. Java holds one
  successor per chart, set by the first line that names the chart, and throws on a later line that names a different
  one.
- **Reach:** none from shipped data. All 44 charts in `history.txt` (165 lines) give the same successor on every line.
  A modded `history.txt` that varies the successor within a chart only.
- **Revisit:** potential future work. If modded history files are ever supported, move `successorNumber` and
  `successor` from `PlayerHistoryChart` to `PlayerHistoryEntry`, read the successor off the chosen entry in
  `PlayerBirth.getHistory`, and drop the disagree-check in `HistoryAssembler`.
- **Pinned by:** `aChartRepeatedWithADifferentSuccessorStopsTheLoadHereWhereCAcceptsIt` in `HistoryReaderTest`.
- **Accepted:** 2026-11-08. Marked in the `HistoryAssembler` and `PlayerHistoryChart` Javadoc.

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
  `MonsterReaderTest.aGlyphLineBeforeBaseKeepsItsGlyphHereWhereCOverwritesItWithTheBases`. The second part is pinned by
  `MonsterReaderTest.aFlagsOffLineBeforeAFlagsLineNamingTheSameFlagLeavesItOffHereWhereCLeavesItOn` (added 2026-11-08).

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

## Projection

### A plain-integer `denominator:` above `INT_MAX` is clamped, where C refuses it

- **Java:** `ProjectionAssembler.assemble` (the `denominator:` block, all-digit branch), through `ParseUtils.parseIntString`
  with `Radix.DECIMAL`. **C:** `parse_projection_denominator` (`obj-init.c`) and `parse_random` (`parser.c`).
- **Differs:** `denominator:` is a `rand` field, not a `uint`. `parse_random` reads each number with `strtoul` in base 10
  and returns false when the result is above `INT_MAX`, so `parser_parse` returns `PARSE_ERROR_NOT_RANDOM` and the load
  quits. C does not wrap here. Java sends a denominator that is only digits (after an optional sign) straight to
  `parseIntString`, which clamps to the `int` range, so `2147483648` and `99999999999` load as `2147483647`, and
  `-2147483648` loads as `Integer.MIN_VALUE` where C refuses all three. In-range values agree: `2147483647` and
  `-2147483647` load in both, and `010` is 10 in both. A denominator in dice form goes to `Random.parseStr` and
  `RandomReader`, which were not examined in this pass.
- **Why:** this is the `ParseUtils` clamp applied to a field where C refuses rather than wraps. The result is a clamped
  value for a file C would reject; the file is already malformed, and no shipped value comes near 32 bits.
- **Reach:** none from shipped data, where every `denominator:` in `projection.txt` is a small number or a short dice
  string. The lexer accepts a digit string of any length (`DICE_INTEGER` in `DiceStrings.g4`), so a hand-edited file
  reaches it.
- **Revisit:** if a modded `projection.txt` ever needs C's refusal, or if the `ParseUtils` clamp is itself replaced by a
  refusal. Then the all-digit branch checks the value against `Integer.MAX_VALUE` before it is stored.
- **Accepted:** 2026-10-29, during the `ProjectionAssembler` port pass. Choice made over refusing the value, as C does.

## Slay

### A `base:` line in `slay.txt` is resolved, where C refuses every one

- **Java:** `SlayAssembler.assemble` (the `base:` branch), through `MonsterRegistry.lookupMonsterBase`. **C:**
  `parse_slay_base` (`obj-init.c`) and `lookup_monster_base` (`mon-util.c`).
- **Differs:** `parse_slay_base` refuses a base that `lookup_monster_base` cannot find. In `init.c` the parser table runs
  `slays` before `monster bases`, and `rb_info` is only filled by `finish_parse_mon_base` (`mon-init.c`), so at the time
  `slay.txt` is read every lookup returns `NULL` and every `base:` line ends the load with
  `PARSE_ERROR_INVALID_MONSTER_BASE`. Java loads the monster bases first (`GameConstants`, "Dependent on MonsterBases"),
  so a name that matches a base resolves and the slay loads with that base. An unknown name is refused in both.
- **Why:** the Java load order is deliberate, and a slay that targets a base is what the data format describes. Copying
  C's accident would make the `base:` directive unusable.
- **Reach:** none from shipped data, where no slay in `slay.txt` uses `base:`. A modded `slay.txt` that does would load
  in Java and stop C.
- **Revisit:** if the monster bases are ever loaded after the slays, or if a modded file needs C's refusal. Then the
  branch refuses every name.
- **Accepted:** 2026-10-29, during the `SlayAssembler` port pass. Marked in the `SlayAssembler` Javadoc. Pinned by
  `SlayAssemblerTest.aBaseThatMatchesALoadedMonsterBaseResolvesHereWhereCRefusesEveryBase`.

## Tval

### `TValue.changeArmour` rewrites the first `armour` in any case, where C rewrites the first lowercase one

- **Java:** `TValue.changeArmour`, reached from `TValue.fromName(String)` and so from `findIndex`, `tValSValCount` and
  `tvalSvalList`. **C:** `de_armour` (`obj-tval.c`), as called from `tval_find_idx`.
- **Differs:** C finds the first lowercase `armour` with `strstr`, rewrites it to `armor` and drops the rest of the
  text. Java finds the first `armour` in any case (`toUpperCase().indexOf("ARMOUR")`) after a guard that only tests for
  a lowercase one, so when a capitalised `Armour` comes before a lowercase `armour` it rewrites the wrong one.
  `"soft Armour armour"` is `"soft Armour armor"` in C, which matches no tval (-1), and `"soft Armor"` in Java, which
  matches `TV_SOFT_ARMOR`. A name containing a character whose upper-case form is longer (`"ß armour"`) shifts the index
  and throws `StringIndexOutOfBoundsException`, where C returns -1. Text with no lowercase `armour`, and text with only
  lowercase ones, behave as in C.
- **Reach:** none from shipped data. Every tval field that uses the British spelling writes it in lowercase
  (`soft armour`, `hard armour`, checked 2026-11-07 in `lib/gamedata`); the capitalised `Armour` there sits in object
  names, which never reach `fromName`.
- **Revisit:** if a modded file writes a tval with a capital and then a second `armour` after it, or non-ASCII text can
  reach `fromName`.
- **Accepted:** 2026-11-07, during the `TValue` port pass.

### `TValue.fromName` also accepts a constant's identifier, where C accepts only a number or a display name

- **Java:** `TValue.fromName(String)` (the last step, after the display-name loop). **C:** `tval_find_idx`
  (`obj-tval.c`).
- **Differs:** C accepts a number or a case-insensitive match on the `string_name` column of `list-tvals.h`, and
  returns -1 for anything else. Java adds a third form: the constant's name without `TV_`, in any case, with spaces
  read as underscores. `"digging"`, `"drag_armor"`, `"SOFT_ARMOR"` and `"soft_armor"` are -1 in C and a tval in Java,
  so a typo like that loads where C ends the load. `"TV_SWORD"` still resolves to nothing in both.
- **Why:** a convenience for port-side callers that hold a constant's name. It is marked in the `fromName` Javadoc.
- **Reach:** none from shipped data. No tval field in `lib/gamedata` uses an underscore, `digging` or `drag armor`
  (checked 2026-11-07). Hand-edited files only.
- **Revisit:** if the data-file parsers are meant to be as strict as C about tval text, or if no port-side caller turns
  out to need the identifier form.
- **Accepted:** 2026-11-07, during the `TValue` port pass.

## UI entry

### A record that repeats the previous record's name is not merged into it, where C carries on editing the same embryo

- **Java:** `UIEntryAssembler.assemble` and `parseEachEntry`, which build one entry per record. **C:**
  `parse_entry_name` and `parse_entry_parameter` (`ui-entry.c`).
- **Differs:** when a `name:` line equals the name of the embryo still open, `parse_entry_name` returns at once. It does
  not hatch the embryo or reset `param_index`, `psource_index` or `last_category_index`, so the second record's lines
  keep editing the first. If that embryo is new, a `parameter:` line is still allowed, and a second `parameter:` line
  simply overwrites the first. Java resolves each record on its own. If the first record had no parameter, its entry is
  already in the list, so a `parameter:` line on the second record ends the load as a pre-existing name, and any other
  line goes down the override path. If the first record had a parameter, only the `name<X>` entries exist, so the second
  record creates a separate entry under the bare name; a second `parameter:` line ends the load in the override path as a
  duplicate of a parameterized entry.
- **Agrees:** when the first embryo was an existing entry (`exists` is true), C refuses a `parameter:` line on the second
  record, and so does Java. A repeated name that is not adjacent is an ordinary override in both.
- **Why:** merging needs both records before the first is hatched, which does not fit the one-record-at-a-time loop. The
  parse record also loses where each `priority:` line sat among the categories, which the merge would need (see
  `docs/Changes_To_UIEntryAssembler.md` issues 4, 5, 10 and 15). The `#` header of `ui_entry.txt` says nothing about a
  repeated name; C's handling is described only by a comment in `parse_entry_name`.
- **Reach:** none from shipped data. Neither `ui_entry.txt` nor `ui_entry_base.txt` has two adjacent `name:` lines with
  the same name (checked 2026-11-01). Hand-edited files only.
- **Revisit:** if a modded `ui_entry.txt` ever repeats an adjacent name, or once the parse record keeps the `priority:`
  lines in order with their categories, which makes the merge cheap.
- **Accepted:** 2026-11-02, as item 13 of `docs/Changes_To_UIEntryAssembler.md`. Marked in the `assemble` Javadoc.
  Pinned in `UIEntryAssemblerTest` by
  `aParameterLineOnARepeatedNameEndsTheLoadHereWhereCAcceptsItOnANewEmbryo`,
  `aRepeatedNameAfterAParameterisedRecordMakesASeparateBareEntryHereWhereCEditsTheSameEmbryo` and
  `aSecondParameterLineOnARepeatedNameEndsTheLoadHereWhereCOverwritesTheFirst`; the case where Java agrees with C is
  pinned by `aParameterLineOnARepeatedNameOfAnEntryFromAnEarlierFileIsFatalAsInC`.

### A new record with no combiner fails the load at once, where C fails it when the record is hatched

- **Java:** `UIEntryAssembler.assemble` and `parseEachEntry`, which throw a `DataFileParseException` for the record as
  soon as the create path reports no combiner. **C:** `hatch_embryo`, `parse_entry_name` and `hatch_last_embryo`
  (`ui-entry.c`).
- **Differs:** C checks `combiner_index == 0` first thing in `hatch_embryo`, before any fan-out value is made, but
  `hatch_embryo` only runs once the whole record has been read. That is when the next `name:` line arrives
  (`parse_entry_name` returns `PARSE_ERROR_INVALID_VALUE`, so the error is reported against the next record's line) or
  at end of file (`hatch_last_embryo` returns 1, with no line at all). Java throws while handling the record itself and
  quotes that record's own line. For a `parameter:` record Java fails on the first fan-out value, where C fails the
  record once.
- **Agrees:** both refuse the record, and the load ends. A record that edits an existing entry needs no combiner in
  either.
- **Why:** Java resolves each record on its own, so there is no later point at which to run the check. The Java message
  names the offending record, which C's does not.
- **Reach:** none from shipped data. Every new record in `ui_entry.txt` and `ui_entry_base.txt` has a `combine:` line or
  a `template:` line, and C's `parse_entry_template` copies the template's combiner (checked 2026-11-02). Hand-edited
  files only, and then only the line number in the message differs.
- **Revisit:** if the load is ever made to continue past a bad record, since the reported line and the number of
  messages would then matter.
- **Accepted:** 2026-11-02. Marked in the `assemble` Javadoc.

### A category added to an existing entry gets a different interim priority, which the finishing pass overwrites

- **Java:** `UIEntryAssembler.parseEachEntry`, override path, where a record adds a category the existing entry doesn't
  have. **C:** `parse_entry_category` and `finish_parse_ui_entry` (`ui-entry.c`).
- **Differs:** C gives the new category the entry's `default_priority` as it stands when that `category:` line is read,
  with `priority_set = false`, so the value depends on whether a `priority:` line came before it. Java gives it
  `existing.getDefaultPriority()` when the record has no priority text and `0` otherwise, with `prioritySet=false`. The
  two values can differ.
- **Agrees:** the final value. Both leave the category unset, and `finish_parse_ui_entry` in C and `finalPass` in
  `UIDataLoader` then replace it with the entry's final default priority. Nothing reads the interim value in between,
  including a later template copy, which carries `prioritySet=false` too.
- **Why:** the interim value has no effect on the loaded data, and matching it needs the parse record to keep each
  `priority:` line in order with its categories, which is the change for `docs/Changes_To_UIEntryAssembler.md` issues 10
  and 15.
- **Reach:** none, from shipped or hand-edited data. The difference is never visible after the load.
- **Revisit:** if anything ever reads a category's priority before the finishing pass, or when issues 10 and 15 change
  the parse record anyway.
- **Accepted:** 2026-11-02, as item 4c of `docs/Changes_To_UIEntryAssembler.md`.

### The order of `template:`, `renderer:`, `combine:`, `flags:` and `priority:` lines in a record is not kept, where C applies them in file order

- **Java:** `UIEntryAssembler.parseEachEntry`, both paths, which apply the template first and the record's own values
  after it. **C:** `parse_entry_template`, `parse_entry_renderer`, `parse_entry_combine`, `parse_entry_flags` and
  `parse_entry_priority` (`ui-entry.c`).
- **Differs:** `parse_entry_template` overwrites `renderer_index`, `combiner_index`, `default_priority` and `flags` at
  the moment the `template:` line is read, so a `renderer:`, `combine:` or numeric `priority:` line written before it is
  lost, and a `flags:` line written before it is replaced. Java receives the record with the order gone, so the
  template always goes first and the record's own renderer, combiner, priority and flags always win or add to it.
- **Agrees:** when `template:` is the first of those lines, which is the only order shipped data uses. The result is
  then the same in both.
- **Why:** the parse record holds each directive as one field, not as a list in file order. Keeping the order is the
  same change as `docs/Changes_To_UIEntryAssembler.md` issues 10 and 15.
- **Reach:** none from shipped data. All 32 records in `ui_entry.txt` that have a `template:` line write it first
  (checked 2026-11-02). Hand-edited files only.
- **Revisit:** when the parse record is changed to keep its directives in order.
- **Accepted:** 2026-11-02. Pinned in `UIEntryAssemblerTest` by
  `aRecordsOwnRendererBeatsItsTemplateWhateverTheLineOrderHereWhereCTakesTheLastLine`.

### Only `label`, `label2` and `label5` are read, where C accepts `label1` to `label10`

- **Java:** `UIEntryGrammar.g4` (the `label`, `label5` and `label2` rules), `UIEntryParseRecord` (three separate label
  fields) and `UIEntryAssembler.assemble` and `registerLabel`. **C:** `init_parse_ui_entry` and
  `parse_entry_shortened_label` (`ui-entry.c`).
- **Differs:** C registers a `label<N>` line for every N from 1 to `MAX_SHORTENED` (10), in any order, and cuts each to
  N characters. Java's grammar takes `label`, `label5` and `label2` once each, in that fixed order, so the parse
  record has no place for `label1`, `label3`, `label4` or `label6` to `label10`. The entry itself holds ten slots.
- **Agrees:** `label`, `label2` and `label5` in that order, which is every shortened label shipped data uses. The
  finishing pass (`UIDataLoader.fillOutShortened`) fills the other slots from the nearest longer set slot or the full
  label, as `fill_out_shortened` does.
- **Why:** widening it means ten grammar rules, a parse record that holds a list or map of labels, and a change to the
  `UIEntry` constructor, for no loaded record. The parse record is due a reshape for the line-order issues anyway
  (`docs/Changes_To_UIEntryAssembler.md` issues 10 and 15), so one change can cover both. The grammar header already
  notes the gap (no `label3`).
- **Reach:** none from shipped data. `ui_entry.txt` uses only `label`, `label2` and `label5` (checked 2026-11-02).
  Hand-edited files only.
- **Revisit:** if a modded `ui_entry.txt` uses `label1` or `label3` to `label10`, or when the parse record is reshaped
  for the line-order issues.
- **Accepted:** 2026-11-02.

### A `priority:` of `-2147483648` or below loads as `-2147483647`, where C clamps it to `INT_MIN`

- **Java:** `UIEntryAssembler.assemble`, which strips the sign, reads the digits with `ParseUtils.parseIntString` and
  then multiplies by the sign. **C:** `parse_entry_priority` (`ui-entry.c`).
- **Differs:** C reads the whole text with `strtol` in base 10 and clamps the signed result, so `-2147483648` and
  anything below it give `INT_MIN`. Java clamps the unsigned digits to `INT_MAX` first and negates after, so the same
  text gives `-INT_MAX`, one above `INT_MIN`.
- **Agrees:** every value from `-2147483647` to `INT_MAX`, the clamp of anything above `INT_MAX`, and the refusal of
  text that is not a sign and digits (`5x`, `0x10`, `+-5`, `-`).
- **Why:** the digits are read by the shared `ParseUtils.parseIntString`, which clamps before any sign is applied. It
  is the same shape as the `bindui` limit in `PlayerPropertyAssembler`.
- **Reach:** none from shipped data. The lowest priority in `ui_entry.txt` is `-12` (checked 2026-11-02). Hand-edited
  files only, and a priority that low has no visible effect beyond ordering.
- **Revisit:** if `ParseUtils` is changed to clamp after the sign, since the assembler then needs no workaround.
- **Accepted:** 2026-11-02.

## Summon

### An unresolved `fallback:` keeps the record with a `null` fallback, where C fails in `strcmp`

- **Java:** `SummonAssembler.assemble`, second pass. **C:** `finish_parse_summon` and `summon_name_to_idx`
  (`mon-summon.c`).
- **Differs:** `finish_parse_summon` adds 1 to `summon_max` before it links the fallbacks, so `summons` ends in a blank
  slot whose `name` is NULL. `summon_name_to_idx` guards its argument (`name &&`) but not that field, so a non-NULL
  fallback name that matches no real summon reaches `strcmp(name, NULL)`: undefined behaviour, in practice a crash.
  Java keeps the summon, leaves its fallback `null` and reports nothing.
- **Agrees:** a fallback that names a summon (earlier, later, itself, or the first of two with the same name), exact
  and case-sensitive, and a summon with no `fallback:` line, which C passes as NULL and so reads as -1.
- **Why:** a defined result is better than C's undefined one, and an earlier note that C stores -1 silently was wrong.
- **Reach:** none from shipped data. Both `fallback:` lines in `summon.txt` name `HI_UNDEAD`.
- **Revisit:** if a modded `summon.txt` names a fallback that does not exist and a load error is wanted instead.
- **Accepted:** 2026-11-07. Marked in the `SummonAssembler` Javadoc.

## Brands and slays merge

### An exact multiplier tie keeps the item's own brand or slay, where C keeps the higher table index

- **Java:** `ObjectUtils.copyBrands` and `ObjectUtils.copySlays`. **C:** `copy_brands` and `copy_slays`
  (`obj-slays.c`).
- **Differs:** C ORs the source's flag array into the destination's, then compares every pair of set indices with
  `j < i` that share a name (brands) or kill the same monsters (slays). It clears `i` when `i` has the lower multiplier
  and otherwise clears `j`, so on an exact tie the higher index survives. Java scans the item's brands or slays for a
  match to each source entry and replaces the item's only when the source's multiplier is strictly higher, so on a tie
  the item's own survives. Java never reads a registry index here, and compares each source entry only against the
  item's, not every pair in the merged set.
- **Agrees:** every case where the two multipliers differ, which is every pair in `brand.txt` and `slay.txt`.
- **Why:** a tie needs two entries with the same name (or monsters) and the same multiplier, and the shipped data has
  none. `BrandSlayNoTieDataTest` fails if one is added.
- **Reach:** none from shipped data. A modded `brand.txt` or `slay.txt` with such a pair only.
- **Revisit:** if `BrandSlayNoTieDataTest` ever fails. The fix is a comparison of positions in
  `ObjectRegistry.getBrands()` or `getSlays()` inside the two methods, not an order on the sets.
- **Pinned by:** `tieKeepsDestsBrandHereWhereCKeepsTheHigherIndex` in `ObjectUtilsCopyBrandsTest` and
  `tieKeepsDestsSlayHereWhereCKeepsTheHigherIndex` in `ObjectUtilsCopySlaysTest`.
- **Accepted:** 2026-11-08. Marked in the `copyBrands` and `copySlays` Javadoc.

## Put off until later, accepted for now

Deviations you have accepted for the time being, with the fix agreed in principle and deferred. Each one is a real gap
to close, not a permanent choice, so it is worth a line in `docs/ROADMAP.md` too. The entries use the same fields as
above, and **Later** says what the fix is. When one is fixed, delete the entry and its pinning test's `WhereC` name.
Move an entry up into its own section instead if you decide to keep the deviation for good.

### World: a level, up or down name made only of digits is refused, where C keeps it

- **Java:** `WorldLexer.g4` (the `INTEGER` and `NAME` rules) and `WorldGrammar.g4` (the `levelName` and `upAndDown`
  rules). **C:** `parse_world_level` (`init.c`), registered as `level int depth sym name sym up sym down`.
- **Differs:** a `sym` in C is any text without a colon, so `level:5:7:None:None` loads a level named `7`. Java's `NAME`
  is `~('\r' | '\n' | ':')+`, which matches `7`, but `INTEGER` (`'-'? ('0'..'9')+`) matches it too, at the same length,
  and is listed first, so the lexer returns `INTEGER`. The grammar wants `NAME` in the name, up and down fields, so the
  line is a syntax error and the whole file is refused. The same goes for `-5`, `007` and any other name that is only an
  optional minus and digits, in any of the three fields. The depth field is not affected: it wants `INTEGER`.
- **Agrees:** a name with any other character in it (`Hell-hole`, `Angband 7`, `7th`), which lexes as `NAME`.
- **Reach:** none from shipped data. Every name in `world.txt` is `Town` or `Angband N`, with a word in it. A modded
  `world.txt` that names a level with a bare number only.
- **Later:** make `NAME` win for the name fields. The options are a lexer mode that switches after the depth field's
  colon, or a parser rule that takes `NAME | INTEGER` in `levelName` and `upAndDown` and reads `getText()`. The second
  needs no lexer change. Then flip the pinning test to expect `World(5, "7", "None", "None")`, rename it, and update
  the `WorldGrammar.g4` / `WorldLexer.g4` entry in `docs/grammar_issues.md`.
- **Pinned by:** `aNameOfOnlyDigitsIsHardHereWhereCTakesAnySymExceptColon` in `WorldReaderTest`. It goes red when the
  lexer is changed, which is the prompt to take this entry out.
- **Accepted:** 2026-11-09, found while bringing `WorldReaderTest` up to date after the `record-count:` header and the
  name restriction were removed. Written up in `docs/grammar_issues.md`.

## Pending your decision

- The five `objDescNameFormat` divergences (Part G; `docs/ItemObject_round2/part_G.md`). All unreachable from shipped
  data; four are already in the Javadoc and the fifth (a `~` inside a kept `|x|y|` alternative) is new. Not accepted
  until you say so.

## Call to action

- Say which of the pending `objDescNameFormat` cases to accept, and I will move them into the ItemObject section.
- Check `docs/ItemObject_round2/summary.md` item 4: it now carries both `ACCEPTED` and `FIXED` as its **Result**, and
  they cannot both be true.
