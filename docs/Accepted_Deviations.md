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

### A blank or bar before the first flag on a `flags:` line

- **Java:** the `flags` rule in `MonsterBaseGrammar.g4`, with `FLAG_OR` in `MonsterBaseLexer.g4`. **C:**
  `parse_mon_base_flags` (`mon-init.c`), which splits the value with `strtok(flags, " |")`.
- **Differs:** `strtok` skips delimiters at the start of the value as well as between names, so C reads `flags: ANIMAL`,
  `flags:| ANIMAL` and `flags: | ANIMAL` as the one flag. The Java rule takes a flag or the end of the line straight
  after `flags:`, and the lexer makes the leading blank or bar a `FLAG_OR`, so each of these is a syntax error and the
  load stops.
- **Reach:** none from shipped data, where every `flags:` value starts with its first name. A hand-edited file with a
  blank after the colon. A blank or bar elsewhere in the list, and after the last name, loads as in C.
- **Revisit:** if the `flags` rule is ever changed to take a `FLAG_OR` straight after `FLAGS`. Then the pin below goes
  red, which is the prompt to take this entry out. The same decision applies to the other flag lists when they are
  brought to the same shape (Stage 7d of `docs/Issue_Approach_Order.md`).
- **Pinned by:** `aBlankOrBarBeforeTheFirstFlagIsAHardErrorHereWhereCSkipsIt` in `MonsterBaseReaderTest`.
- **Accepted:** 2026-11-13, decided with you. A list of bars and blanks with no name (`flags:|`) is not part of this
  deviation: the rule takes it as no flags, as C does, and `aFlagListOfOnlyBarsLoadsWithNoFlagsAsInC` pins that. A
  `flags:` line with no final newline is not part of it either: the rule ends in `(FLAG_EOL | EOF)`, and
  `aFlagsLineWithNoFinalNewlineLoadsAsInC` pins it (Stage 7d of `docs/Issue_Approach_Order.md`).

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

### A special artifact's kind takes the artifact's finished level, where C's is 0 [<span style="color:#006400">**CLOSED**</span> 26-10-23]

- **Closed:** the difference is gone, so this is no longer a deviation. `ArtifactGrammar.g4` now gives each
  `base-object:` line an `ArtifactBaseObjectParseRecord` holding the `level:` text in force at that line, and
  `ArtifactAssembler.assemble` gives a new special kind that level through `ObjectKind.setLevel`, as C's
  `write_dummy_object_record` copies `art->level` when `parse_artifact_base_object` runs. Shipped `artifact.txt` puts
  `level:` after `base-object:`, so all 14 special kinds are level 0 in both, the Phial's included. A file that puts
  `level:` first gives the kind that level, in both. The `ObjectKind(Artifact, String, ObjectBase)` constructor this entry
  named no longer exists.
- **Pinned by:** `ArtifactKindLevelOrderTest` (nine tests across the line orders, octal and negative levels, an
  ordinary kind and a second run) and the Phial assertion in `ArtifactReaderTest`.
- **Originally accepted:** 2026-10-22, when the parse record could not see line order. Kept as a record of the change;
  delete it whenever you tidy this file.

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

### A numeric `base-object:` sval that names no kind would fail on a `NullPointerException`, but the grammar never passes one [<span style="color:#006400">**CLOSED**</span> 26-11-21]

- **Closed:** the difference is gone, so this is no longer a deviation. The grammar now passes a numeric sval, and
  `ArtifactAssembler.assemble` reads it as C's `lookup_sval` does: an sval that is a whole number below `INT_MAX` is the
  sval itself, and when no kind has it no kind is made, as C's `write_dummy_object_record` does not run. Anything else
  is a name, and a name that matches no kind gets a dummy kind (`-1`, `2147483647`, `12abc` and `0x10` each make one).
  `ObjectRegistry.lookupObjectKind(TValue, String)` applies the same test, so the kind made is the kind found afterwards.
  The `NullPointerException` cannot happen: a null kind is now either skipped or refused by name, see "A numeric
  `base-object:` sval that names no kind refuses a `weight:`, `cost:` or `graphics:` line" below.
- **Pinned by:** `ArtifactNumericSvalTest` (twelve tests across the numbers C reads as svals, the ones it reads as names,
  the dummy kind's directives, both tvals and a second parse) and `aNumericTvalOrSvalInBaseObjectLoadsAsInC` in
  `ArtifactReaderTest`.
- **Originally accepted:** 2026-11-07, when the lexer refused a number between the colons. Kept as a record of the change;
  delete it whenever you tidy this file.

### A numeric `base-object:` sval that names no kind refuses a `weight:`, `cost:` or `graphics:` line, where C crashes

- **Java:** `ArtifactAssembler.assemble` (the weight, cost, activation, time and glyph blocks after the base-object
  loop). **C:** `parse_artifact_weight`, `parse_artifact_cost`, `parse_artifact_graphics`, `parse_artifact_act` and
  `parse_artifact_time` (`obj-init.c`), and `lookup_kind` (`obj-util.c`).
- **Differs:** for `base-object:light:99`, `lookup_sval` returns 99 without needing a kind, so C stores `a->sval = 99`
  and makes no dummy kind. `lookup_kind` then finds nothing, prints "No object" and returns `NULL`.
  `parse_artifact_weight`, `parse_artifact_cost` and `parse_artifact_graphics` `assert(k)` and stop.
  `parse_artifact_act` and `parse_artifact_time` read `k->kidx` only when the tval is a light, because the `&&` ahead of
  it stops for any other tval, so on a light they read through the null pointer and on any other tval the record loads.
  Java throws a `DataFileParseException` that names the directive and its value for exactly the cases where C crashes,
  and only when the record has that line.
- **Agrees:** a bare numeric sval loads in both, with no kind made. `act:` or `time:` on a tval that is not a light loads
  in both. A numeric sval that names a kind, or a name that gets a dummy kind, takes all five directives in both.
- **Why:** this is the standing rule for every place C crashes (see "Where C crashes, Java throws" below).
- **Reach:** none from shipped data, which loads in C. `artifact.txt` has one numeric `base-object:` line,
  `#base-object:amulet:55`, and it is commented out (checked 2026-11-07). A hand-edited file only.
- **Revisit:** never expected. C cannot load such a file either.
- **Pinned by:** `aWeightCostOrGraphicsLineOnANumericSvalWithNoKindIsRefusedHereWhereCCrashes` and
  `anActOrTimeLineOnALightWithANumericSvalAndNoKindIsRefusedHereWhereCCrashes` in `ArtifactNumericSvalTest`, with
  `anActOrTimeLineOnANonLightWithANumericSvalAndNoKindLoadsAsInC` for the agreeing case.
- **Accepted:** 2026-11-21, found while closing Stage 7h for `base-object:`.

### A second parse finds the dummy kind that `-1` or `12abc` made, where C makes another each time

- **Java:** `ObjectRegistry.lookupObjectKind(TValue, String)`, called by `ArtifactAssembler.assemble` for each
  `base-object:` line. **C:** `lookup_sval` (`obj-util.c`) and `write_dummy_object_record` (`obj-init.c`).
- **Differs:** for an sval that `strtoul` reads digits from but that is not a whole number below `INT_MAX` (`-1`,
  `2147483647`, `12abc`, `0x10`), `lookup_sval` answers -1 at once and never tries the kind names. So
  `parse_artifact_base_object` calls `write_dummy_object_record`, which adds a new dummy kind every time the line is
  parsed, and birth parses `artifact.txt` a second time to restore the standard artifacts (the second parse
  `aSecondParseKeepsTheSpecialLightsActivationAndTimeOnTheKindAsInC` in `ArtifactReaderTest` models), so each such
  line adds one more kind per new character. Java matches the text against the kind names, finds the dummy the first
  parse made, and adds none.
- **Agrees:** a name that starts with a letter, such as `Phial`, is matched by name in C too, so the second parse finds
  the dummy and makes none. A number below `INT_MAX` makes no kind in either, on every parse.
- **Why:** copying it would mean two changes, a lookup that skips the names for such text and an `Artifact` that keeps
  the dummy's allocated sval as C's `art->sval` does, to reproduce a kind that is made again and never used again.
  Java's one kind is the better behaviour.
- **Reach:** none from shipped data, whose `base-object:` svals are all names that start with a letter (`Phial`,
  `Star`, `Arkenstone` and the others) or kinds that exist. A hand-edited file only.
- **Revisit:** if `Artifact` is changed to hold the sval number C holds, or if `makeFakeArtifact` and the birth
  re-parse are ported and a test shows the kind count depending on it.
- **Pinned by:** `aSecondParseOfADigitLedNonNumberFindsItsDummyKindHereWhereCMakesAnother` in
  `ArtifactNumericSvalTest`, with `aSecondParseOfANameFindsItsDummyKindAsInC` for the agreeing case.
- **Accepted:** 2026-11-21, raised while checking whether Java ever uses the dummy kind it makes.

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

### An empty `values:` bracket, such as `STR[]`, is a lexer error, where C reads it as 0

- **Java:** the `VALUE_INT` rule in `CurseLexer.g4`, which wants at least one digit between `[` and `]`. **C:**
  `find_value_arg` (`datafile.c`), called by `grab_index_and_int` for `parse_curse_values` (`obj-init.c`).
- **Differs:** C calls `strtol` in base 10 and only tests that the character it stopped on is `]`. With no digits
  `strtol` stops at once on the `]`, so `STR[]` is `STR` with 0. Java has no token for an empty bracket, so the file
  loads as nothing.
- **Why:** an empty bracket is a typing slip in a hand-edited file, and reading it as 0 would hide a value the author
  forgot to write.
- **Reach:** none from shipped data. Every `values:` entry in `curse.txt` has a number.
- **Revisit:** if a modded file is ever expected to leave a value out. Then `VALUE_INT` becomes optional and the
  assembler reads an absent value as 0.
- **Pinned by:** `anEmptyValueBracketIsAHardErrorHereWhereCReadsZero` in `CurseReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `UINT`/`INTEGER` rules of `CurseLexer.g4`.

### A blank between `[` and the number in a `values:` entry is a lexer error, where C skips it

- **Java:** the `VALUE_INT` rule in `CurseLexer.g4`, which has no rule for a leading blank. **C:** `find_value_arg`
  (`datafile.c`), which calls `strtol`, and `strtol` skips leading white space.
- **Differs:** `STR[ 5]` is `STR` with 5 in C. In Java the blank has no rule and the file loads as nothing. This is the
  "A blank between a directive's colon and its number" entry under "Numbers in every data file", seen from the
  bracket instead of the colon.
- **Why:** the same reason as that entry: a typing slip in a hand-edited file, not something the shipped file writes.
- **Reach:** none from shipped data. No `values:` entry in `curse.txt` has a blank after `[`.
- **Revisit:** with the general leading-blank entry. If a numeric rule is ever given a leading-blank allowance, add
  this one with it.
- **Pinned by:** `aBlankBeforeAValueIsAHardErrorHereWhereCSkipsIt` in `CurseReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `UINT`/`INTEGER` rules of `CurseLexer.g4`.

### Text after the closing `]` of a `values:` entry is a lexer error, where C ignores it

- **Java:** the `R_BRACKET` and `SWITCH_VALUE` rules in `CurseLexer.g4`, which expect a `|` or the end of the line
  after `]`. **C:** `find_value_arg` (`datafile.c`) and `grab_index_and_int`.
- **Differs:** C reads the number up to the first `]`, cuts the name at the `[`, and never looks past the `]`. So
  `STR[5]x` and `STR[5]]` are `STR` with 5. In Java the extra text has no rule and the file loads as nothing. A second
  `values:` entry after a `|` is a separate case and loads in both.
- **Why:** text after a `]` is a typing slip in a hand-edited file, and a lexer that swallowed it would hide a
  mistyped `|` between two entries.
- **Reach:** none from shipped data. Every `values:` entry in `curse.txt` ends in `]` or is followed by a `|`.
- **Revisit:** if a modded file is ever expected to carry stray text after a bracket.
- **Pinned by:** `textAfterTheClosingBracketIsAHardErrorHereWhereCIgnoresIt` in `CurseReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `UINT`/`INTEGER` rules of `CurseLexer.g4`.

### A `values:` number at or beyond the `int` range is clamped or kept, where C refuses it

- **Java:** `CurseAssembler.assemble` (the `values` loop), reading the text through `ParseUtils.parseIntString`.
  **C:** `find_value_arg` (`datafile.c`).
- **Differs:** C refuses a value with `lv <= INT_MIN` or `lv >= INT_MAX`, so the load stops on `STR[2147483648]`,
  `STR[2147483647]` and `STR[-2147483648]`. Java clamps `2147483648` to 2147483647 and keeps `-2147483648`, and loads
  both. This is a narrow case of the general rule that numbers outside the `int` range are handled by
  `parseIntString` and not by C's wrapping.
- **Why:** no modifier or resistance level comes near these values, and shipped data cannot reach them.
- **Reach:** none from shipped data. The largest magnitude in `curse.txt` is 10.
- **Revisit:** if a range check is ever added to `parseIntString`. Then these two pins flip.
- **Pinned by:** `aValueOutsideTheIntRangeIsClampedHereWhereCRefusesIt` in `CurseReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `UINT`/`INTEGER` rules of `CurseLexer.g4`.

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

### Text after the glyph's colon on a `kind:` line is refused by the assembler, where C ignores it

- **Java:** `FlavourKindAssembler` (the glyph check), fed by the `GLYPH` token in `FlavourLexer.g4`. **C:**
  `parse_flavor_kind` in `init.c`, with `parser_parse` in `parser.c` reading the `char` field.
- **Differs:** C takes one character, skips a colon after it and ignores the rest of the line, so `kind:ring:=:extra` has
  the glyph `=`. Java reads the whole rest of the line as the glyph, so the assembler sees `=:extra` and throws "illegal
  glyph value". A second character that is not a colon is refused by both.
- **Reach:** none from shipped data. All 8 `kind:` lines in `flavor.txt` end at the glyph.
- **Revisit:** if a modded `flavor.txt` ever puts a note after the glyph.
- **Accepted:** 2026-11-22, closing the Flavour bullet of Stage 7i of `docs/Issue_Approach_Order.md`; the exception comes
  from the assembler, not the parser. Pinned by `textAfterTheGlyphIsRefusedByTheAssemblerHereWhereCIgnoresIt` in
  `FlavourReaderTest`.

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

## Object base

### A `default:` line after the first `name:` ends the load, where C applies it to the records that follow

- **Java:** the `file` rule in `ObjectBaseGrammar.g4`, which takes its `default:` lines first and the records after
  them, and the `object_base` rule, whose directive loop has no `default:` alternative. **C:**
  `parse_object_base_defaults` and `parse_object_base_name` (`obj-init.c`).
- **Differs:** C's `default:` handler writes one field of the parser's running defaults and needs no record to exist,
  and `parse_object_base_name` copies those defaults into each record as it starts it. So a `default:` line anywhere
  changes only the records whose `name:` comes after it. Two shapes follow from that, both legal in C and both a
  syntax error at the `default:` token in Java, which stops the load:
  - **Between records.** `default:break-chance:99` after the `sword` record and before the `shot` record leaves `sword`
    on the earlier defaults and gives `shot` a break chance of 99.
  - **Inside a record.** `default:break-chance:99` between `name:sword` and its `graphics:` line changes nothing for
    `sword`, whose defaults were copied at `name:`. The `graphics:` line still belongs to `sword`, because C's current
    record has not changed.
- **Why:** the running defaults in the `file` rule are already folded into each record in file order, so the
  between-records shape is only a change to the loop shape. The inside shape is harder: the fold would have to read the
  defaults as they stood at the `name:`, where it reads them now when the record ends. Neither was judged worth doing
  now.
- **Reach:** none from shipped data. `object_base.txt` has both of its `default:` lines (`break-chance` and
  `max-stack`) before the first `name:`. It takes a hand-edited file that moves one.
- **Revisit:** planned for a later version. The between-records shape first, as one loop of `default_value` and
  `object_base` alternatives in the `file` rule with the fold unchanged, then the inside shape. When either lands its
  pin goes red, which is the prompt to take that half of this entry out.
- **Pinned by:** `aDefaultLineAfterARecordIsAHardErrorHereWhereCAcceptsIt` (between records) and
  `aDefaultLineInsideARecordIsAHardErrorHereWhereCAcceptsIt` (inside a record), both in `ObjectBaseReaderTest`. The
  `default:` lines before the first record load as in C and are pinned by
  `aFileWithNoDefaultLineLoadsWithZeroDefaultsAsInC` and `aFileWithOnlyOneDefaultLineLeavesTheOtherAtZeroAsInC`.
- **Accepted:** 2026-11-13, decided with you, to be worked on in a later version. Listed in `docs/Parked.md`.

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

### A `type:` line that follows a `code:` line is refused when the carried index is past the new type's table, where C loads it

- **Java:** `ObjectProperty2Assembler.parseType` and `parsePrevCode`. **C:** `parse_object_property_type` and
  `parse_object_property_code` (`obj-init.c`).
- **Differs:** C's `type:` handler writes only `prop->type`. It never looks at `prop->index`, so a `code:` line followed
  by a `type:` line in a different table leaves the same integer index and reads it through the new table. A `code:` is
  resolved only at its own line, against the type current then, and a bare `type:` line with no `code:` leaves the zeroed
  index. Java holds the enum constant rather than an integer, so on every `type:` line it rebuilds the payload from the
  carried C index (`prevCode`) in the new type's table. `prevCode` is the C index, not the Java ordinal: for
  `ObjectModifier` and `ElementEnum` it is the ordinal minus 1, because those enums carry an `OM_NONE`/`ELEM_NONE`
  placeholder at ordinal 0 that C's tables do not have, and for `ObjectFlag` it is the ordinal, because `OF_NONE` lines
  up with C's leading `"NONE"`. That agrees with C whenever the index fits: stat and mod share `obj_mods`, the four
  element types share `element_names`, a mod or element index fits in the larger flag table, and retyping away and back
  restores the original code. It differs when the index is past the new table. A flag index of 17 or more retyped to
  `stat`/`mod`, a flag index of 26 or more retyped to an element type, and an element index of 17 or more retyped to
  `stat`/`mod`, are refused with a `DataFileParseException` that names the line and says the value is out of range.
  C stores the index and the record loads.
- **Also accepted:** the last slot is the one C's table ends on, not the last real entry. `obj_mods` has 16 entries
  (index 0 to 15) and `element_names` has 25 (0 to 24), but the Java enums close with `OM_MAX` and `ELEM_MAX`, so index
  16 retyped to `stat`/`mod` (from a flag or an element) loads with `OM_MAX` as its modifier, and flag index 25 retyped
  to an element type loads with `ELEM_MAX`. C's tables have no entry there, so C stores an index that points at the NULL
  terminator.
- **Why:** the payload is a typed enum, and there is no constant to hold an index that names nothing in its table.
  Throwing at the `type:` line is the nearest Java can get to C storing a number that only means something later.
- **Reach:** none from shipped data. `object_property.txt` puts `type:` before `code:` in all 79 records (checked
  when `ObjectProperty2Grammar.g4` was written), and none of them retypes after a code. It takes a hand-edited file.
- **Revisit:** if a modded `object_property.txt` ever retypes after a `code:`, or the payload is made to carry a bare
  index.
- **Pinned by:** `retypeWithAnIndexPastTheNewTableIsRefused` and `elementRetypedToModPastTheTableIsRefused`, in
  `ObjectProperty2AssemblerTest`. The in-range half is pinned as C does it by `typeOnlyRecordHoldsIndexZeroUnderItsType`,
  `statRetypedToModKeepsTheModifier`, `elementRetypedToAnotherElementTypeKeepsTheElement`,
  `crossTableRetypeKeepsTheIndexWhenItFits`, `retypingAwayAndBackKeepsTheOriginalCode`,
  `aCodeAfterARetypeResolvesInTheNewTable` and `theCarriedCodeDoesNotLeakIntoTheNextRecord`. The `OM_MAX` landing is
  pinned by `aRetypeLandingOnOmMaxLoads`, and the edge of each target table (loads at the last slot, refused one past)
  by `theEdgeOfEachRetypeTargetTable`.
- **Corrected:** 2026-11-14, after the `prevCode` index/ordinal fix. The cutoffs above were 18, 27 and 18, and the
  landing index 17, when `prevCode` held the Java ordinal.
- **Accepted:** 2026-11-14, found while checking which of `code:` and `type:` may come first in
  `ObjectProperty2Assembler`.

### A `bindui:` entry name with a stray `<` or `>`, or with an empty tag, is refused or renamed, where C takes the whole name

- **Java:** `BINDUI_STRING`, `BINDUI_LTHAN`, `BINDUI_TAG` and `BINDUI_GTHAN` in `ObjectProperty2Lexer.g4`, the `bindui`
  rule of `ObjectProperty2Grammar.g4`, and `ObjectProperty2Assembler.parseBindui`. **C:** `parse_object_property_bindui`
  (`obj-init.c`) and `parser_parse` (`parser.c`).
- **Differs:** C registers `bindui` as `sym ui`, `int aux`, `?int uival`. A `sym` is cut with `strtok(sp, ":")`, so the
  entry name is any text up to the next `:`, and `<` and `>` are ordinary characters in it. The name goes to
  `ui_entry_search`, and a name that matches no entry is dropped without an error. Java reads the name up to a `<`, then
  a tag up to a `>`, then expects the aux figure, and `parseBindui` joins the pieces back as `name<TAG>`. It agrees with
  C on a plain name and on one tag, whatever the case of the tag. It differs in these shapes:
  - `bindui:resist<FIRE:0`    C loads, Java refuses (a tag with no `>` meets the `:`, which the tag mode has no rule for)
  - `bindui:resist<FIRE>x:0`    C loads, Java refuses (text after the `>` meets a mode that expects the aux figure)
  - `bindui:a<B>c<D>:0`    C loads, Java refuses (a second `<` meets the same missing rule)
  - `bindui:<:0`    C loads, Java refuses (a `<` with no tag after it)
  - `bindui::x:0`    C loads as `x` (`strtok` skips the leading `:`), Java refuses (the first `:` ends the name, and `x` is not
    an aux figure)
  - `bindui:x<>:0`    C loads the name `x<>`, Java loads the name `x` (an empty tag gives no tag token, so no `<>` is put back)
- **Same in both:** `bindui:Resist-UI:0`, `bindui:resist<fire>:0`, `bindui:resist_ui_compact_0<FIRE>:0` and `:1:5`, and
  `bindui:resist ui:0` load. `bindui:x`, `bindui:x:` and `bindui::0` are refused. `bindui:x::0` and `bindui:x:0::5`
  load. The shipped file (79 records, 66 `bindui` lines) parses with no errors.
- **Why:** making every shape load means taking the whole name to the colon and dropping the tag rules, which the
  shipped file does not need. The refused shapes are typing slips in a hand-edited file.
- **Reach:** none from shipped data. Every shipped `bindui:` line is a plain lower-case name or one `<UPPER>` tag. The
  `x<>` rename binds a different entry only if a modded `ui_entry.txt` has an entry named `x`.
- **Revisit:** if a modded file is ever expected to carry a `<` or `>` in an entry name. Then `BINDUI_STRING` takes
  everything up to the colon, and the tag rules in the lexer and grammar go.
- **Pinned by:** `scratch/ObjectProperty2BinduiGrammarCheck.java`, which runs 19 lines through the regenerated lexer
  and parser (lexer errors count as refusals) and parses the shipped file. Gradle does not compile `src/main/gen`, so
  the suite cannot see this half, and `ObjectProperty2AssemblerTest` covers only the assembler.
- **Accepted:** 2026-11-14, decided with you, while checking item 8 of `scratch/IssuesWithObjectProperty2.md`.

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

### A `denominator:` with a lower-case `m` loads, and one with a leading blank is a lexer error, where C does the opposite

- **Java:** `DICE_M` in `imports/DiceStrings.g4`, and `DICE_MODE` in `ProjectionLexer.g4`, which has no rule for a blank.
  **C:** `parse_random` (`parser.c`), called by `parser_parse` for a `rand` field.
- **Differs:** two opposite directions.
  - **Lower-case `m`: Java is more lenient.** `parse_random` treats only an upper-case `M` as the bonus marker. A
    lower-case `m` reaches `strtoul`, converts nothing, fails the trailing-text test and gives `PARSE_ERROR_NOT_RANDOM`.
    `DICE_M` is `'M' | 'm'`, so `denominator:5+1d4m2` lexes, and it loads with the values 5, 1, 4 and 2. `DiceStrings.g4` is shared, so every lexer that imports it takes a lower-case `m` too.
  - **Leading blank: Java is stricter.** `parse_random` reads the number with `strtoul`, which skips leading blanks, so
    `denominator: 3` is 3. `DICE_MODE` has no rule for a blank, so the same line is a hard error and the file loads as
    nothing.
- **Why:** the lower-case `m` is also correct for `dice:` and `time:` lines, which `z-dice.c` reads with `m` as a
  second marker (it carries a note that `m` ought to be the only one). `DiceStrings.g4` serves both C parsers, so
  narrowing `DICE_M` would break the second to tidy the first. A leading blank needs a hand-edited file.
- **Reach:** none from shipped data, whose denominators are `3` and `8+1d4`. It takes a modded file that writes `m` or a
  blank after the colon.
- **Revisit:** if `DICE_M` is ever split into an upper-case-only form for the `rand` fields, or `DICE_MODE` is given a
  leading-blank rule. Then the pin below goes red, which is the prompt to take this entry out.
- **Pinned by:** `aLeadingBlankInADenominatorIsAHardErrorAndALowerCaseMIsAcceptedWhereCTakesTheOpposite` in
  `ProjectionReaderTest`. The agreeing forms are pinned by `denominatorFormsGiveTheValuesOfCsParseRandom`.
- **Accepted:** 2026-11-12, Stage 6 item 3 of `docs/Issue_Approach_Order.md`.

## Shape

The four entries below are the `shape.txt` `values:` bracket, the same four cases the Curse section records for
`curse.txt`. Both files are read in C by `find_value_arg` (`datafile.c`), through `grab_int_value` and
`grab_index_and_int`. A hex or letter-only figure is not here because Java now agrees with C: `VALUE_INT` in
`ShapeLexer.g4` takes decimal digits only, as `strtol` in base 10 does.

### An empty `values:` bracket, such as `STR[]`, is a lexer error, where C reads it as 0

- **Java:** the `VALUE_INT` rule in `ShapeLexer.g4`, which wants at least one digit between `[` and `]`. **C:**
  `find_value_arg` (`datafile.c`), called by `parse_shape_values` (`init.c`).
- **Differs:** C calls `strtol` in base 10 and only tests that the character it stopped on is `]`. With no digits
  `strtol` stops at once on the `]`, so `STR[]` is `STR` with 0. Java has no token for an empty bracket, so the file
  loads as nothing.
- **Why:** an empty bracket is a typing slip in a hand-edited file, and reading it as 0 would hide a value the author
  forgot to write.
- **Reach:** none from shipped data. Every `values:` entry in `shape.txt` has a number.
- **Revisit:** if a modded file is ever expected to leave a value out. Then `VALUE_INT` becomes optional and the
  assembler reads an absent value as 0.
- **Pinned by:** `anEmptyValuesBracketIsAHardErrorHereWhereCReadsZero` in `ShapeReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `values:` modes of `ShapeLexer.g4`.

### A blank between `[` and the number in a `values:` entry is a lexer error, where C skips it

- **Java:** the `VALUE_INT` rule in `ShapeLexer.g4`, which has no rule for a leading blank. **C:** `find_value_arg`
  (`datafile.c`), which calls `strtol`, and `strtol` skips leading white space.
- **Differs:** `STR[ 3]` is `STR` with 3 in C. In Java the blank has no rule and the file loads as nothing. This is the
  "A blank between a directive's colon and its number" entry under "Numbers in every data file", seen from the
  bracket instead of the colon.
- **Why:** the same reason as that entry: a typing slip in a hand-edited file, not something the shipped file writes.
- **Reach:** none from shipped data. No `values:` entry in `shape.txt` has a blank after `[`.
- **Revisit:** with the general leading-blank entry. If a numeric rule is ever given a leading-blank allowance, add
  this one with it.
- **Pinned by:** `aBlankBeforeAValuesFigureIsAHardErrorHereWhereCSkipsIt` in `ShapeReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `values:` modes of `ShapeLexer.g4`.

### Text after the closing `]` of a `values:` entry is a lexer error, where C ignores it

- **Java:** the `VALUE_R_BRACKET` and `VALUE_BREAK` rules in `ShapeLexer.g4`, which expect a `|` or the end of the line
  after `]`. **C:** `find_value_arg` (`datafile.c`) and `grab_index_and_int`.
- **Differs:** C reads the number up to the first `]`, cuts the name at the `[`, and never looks past the `]`. So
  `STR[3]x` and `STR[3]]` are `STR` with 3. In Java the extra text has no rule and the file loads as nothing. A second
  `values:` entry after a `|` is a separate case and loads in both.
- **Why:** text after a `]` is a typing slip in a hand-edited file, and a lexer that swallowed it would hide a
  mistyped `|` between two entries.
- **Reach:** none from shipped data. Every `values:` entry in `shape.txt` ends in `]` or is followed by a `|`.
- **Revisit:** if a modded file is ever expected to carry stray text after a bracket.
- **Pinned by:** `textAfterTheClosingBracketIsAHardErrorHereWhereCIgnoresIt` in `ShapeReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `values:` modes of `ShapeLexer.g4`.

### A `values:` number at or beyond the `int` range is clamped or kept, where C refuses it

- **Java:** `ShapeAssembler.assemble` (the `values` loop), reading the text through `ParseUtils.parseIntString`.
  **C:** `find_value_arg` (`datafile.c`).
- **Differs:** C refuses a value with `lv <= INT_MIN` or `lv >= INT_MAX`, so the load stops on `STR[99999999999]`,
  `STR[2147483647]` and `STR[-2147483648]`. Java clamps `99999999999` to 2147483647 and keeps `2147483647` and
  `-2147483648`, and loads all three. This is a narrow case of the general rule that numbers outside the `int` range are
  handled by `parseIntString` and not by C's wrapping.
- **Why:** no modifier or resistance level comes near these values, and shipped data cannot reach them.
- **Reach:** none from shipped data. The largest magnitude in `shape.txt` is 10.
- **Revisit:** if a range check is ever added to `parseIntString`. Then the pin below flips.
- **Pinned by:** `aValuesFigureAtOrBeyondTheIntRangeIsKeptOrClampedWhereCRefusesIt` in `ShapeReaderTest`.
- **Accepted:** 2026-11-12, raised while checking the `values:` modes of `ShapeLexer.g4`.

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

### Blanks after a `race-flag:` value are skipped, where C refuses the flag

- **Java:** `SlayLexer.g4` (`FLAG_MODE`, `FLAG`) and `SlayAssembler.assemble`. **C:** `parser_parse` (`parser.c`),
  `parse_slay_race_flag` (`obj-init.c`) and `lookup_flag` (`datafile.c`).
- **Differs:** C cuts a `sym` field at `:` with `strtok` and nothing strips the end of the line, so
  `race-flag:EVIL` followed by blanks passes `"EVIL  "` to `lookup_flag`, which compares with `streq` and returns
  `FLAG_END`. The handler returns `PARSE_ERROR_INVALID_FLAG` and the load stops. Java lexes `EVIL` as the `FLAG` and the
  default mode skips the blanks, so the slay loads with `RF_EVIL`.
- **Why:** a trailing blank is invisible in an editor, and the shipped files do carry them on other directives. Refusing
  the flag for text nobody can see helps no one.
- **Reach:** none from shipped data, where no `race-flag:` line has trailing blanks. A hand-edited `slay.txt` only.
- **Revisit:** if a modded file needs C's refusal. `FLAG_MODE` would then have to take the blanks into the token.
- **Accepted:** 2026-11-12, during the `SlayLexer` / `SlayGrammar` review. Pinned by
  `SlayReaderTest.blanksAfterARaceFlagLoadHereWhereCRefusesThem`.

### A `base:` value runs to the end of the line, where C cuts it at the first colon

- **Java:** `SlayLexer.g4` (`BASE`, `FREE_TEXT_MODE`, `STRING`) and `SlayAssembler.assemble`. **C:**
  `parser_parse` (`parser.c`) and `parse_slay_base` (`obj-init.c`), registered as `base sym base`.
- **Differs:** the C field is a `sym`, tokenized on `:`, and text after the last registered field is dropped without
  error. `base:ancient dragon:junk` looks up `ancient dragon` and loads. Java reads the whole line as one `STRING`, so the
  lookup is for `ancient dragon:junk`, which matches no base, and the load stops.
- **Why:** `BASE` shares `FREE_TEXT_MODE` with the real `str` directives (`name:`, `melee-verb:`), which do take the
  colon. Giving a base a mode of its own for a case no file contains is not worth a second mode.
- **Reach:** none from shipped data, where no slay uses `base:` at all. Also needs a modded file with a stray colon.
- **Revisit:** if `base:` is ever used in shipped data, or a modded file with a trailing `:field` needs to load.
- **Accepted:** 2026-11-12, during the `SlayLexer` / `SlayGrammar` review. Pinned by
  `SlayReaderTest.aBaseValueRunsPastAColonWhereCCutsItThere`.

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

## Object modifiers

### `ObjectModifier.lookup` can return the `OM_NONE` and `OM_MAX` sentinels, where C's table has no such names

- **Java:** `ObjectModifier.lookup(String)`, called from `ObjectProperty2Assembler` for `stat` and `mod` properties.
  **C:** `code_index_in_array(obj_mods, code)` (`datafile.c`), as called from `parse_object_property_code`
  (`obj-init.c`).
- **Differs:** C's `obj_mods` table holds only the sixteen real names, `STR` to `MOVES`, so `NONE` and `MAX` find
  nothing and the load fails with an unknown code. Java returns `OM_NONE` for `NONE` and `OM_NONE`, and `OM_MAX` for
  `MAX` and `OM_MAX`, and leaves the caller to refuse them. `ObjectProperty2Assembler` does, but words the refusal as an
  illegal sentinel rather than an unknown name.
- **Why:** the sentinels are real constants of the enum, and a lookup that finds by name finds them too.
- **Reach:** none from shipped data. No `code:` line in `lib/gamedata/object_property.txt` names a sentinel (checked
  2026-11-15). Hand-edited files only, and the load still fails, with a different message.
- **Revisit:** if the message wording needs to match C, or a second caller of `lookup` forgets to refuse the sentinels.
- **Accepted:** 2026-11-15, during the `ObjectModifier` port pass. Pinned by `lookupFindsTheSentinels` in
  `ObjectModifierTest`.

### `ObjectModifier.lookup` takes the `OM_` prefix as optional, where C finds only the bare name

- **Java:** `ObjectModifier.lookup(String)`. **C:** `code_index_in_array(obj_mods, code)` (`datafile.c`).
- **Differs:** C finds `STR` and refuses `OM_STR`. Java adds `OM_` when it is missing, so `STR` and `OM_STR` both return
  `OM_STR`, and a data file that writes `code:OM_STR` loads where C ends the load. `OM_` on its own, `OM_OM_STR` and any
  wrong-case form find nothing in both.
- **Why:** the same convenience `ElementEnum.lookup` has, for port-side callers that hold a constant's name.
- **Reach:** none from shipped data. All sixteen `stat` and `mod` codes in `lib/gamedata/object_property.txt` are bare
  names (checked 2026-11-15). Hand-edited files only.
- **Revisit:** if the data-file parsers are meant to be as strict as C about code names.
- **Accepted:** 2026-11-15, during the `ObjectModifier` port pass. Pinned by `lookupAcceptsTheOptionalPrefix` in
  `ObjectModifierTest`.

## Object flags

### `ObjectFlag.lookup` takes the `OF_` prefix as optional, where C finds only the bare name

- **Java:** `ObjectFlag.lookup(String)`, called from `ObjectProperty2Assembler` for `flag` properties. **C:**
  `code_index_in_array(obj_flags, code)` (`datafile.c`), as called from `parse_object_property_code` (`obj-init.c`).
  C's `obj_flags` holds `NONE`, the real flag names and `MAX`, all bare.
- **Differs:** C finds `FREE_ACT` and refuses `OF_FREE_ACT`. Java adds `OF_` when it is missing, so both return
  `OF_FREE_ACT`. `OF_OF_FREE_ACT`, wrong-case and partial names find nothing in both.
  - `type:flag code:OF_FREE_ACT`    C refuses, Java accepts
  - `type:flag code:OF_NONE` and `code:OF_MAX`    C refuses, Java accepts
  - `type:flag code:NONE` and `code:MAX`    C accepts, Java accepts
  - `type:flag code:FREE_ACT`    C accepts, Java accepts
- **Why:** the port's constants carry the prefix, and one lookup serves callers that hold the constant's name as well as
  callers that read a data file.
- **Reach:** none from shipped data. None of the 79 `code:` lines in `lib/gamedata/object_property.txt` has a prefix
  (checked 2026-11-16). Hand-edited files only. No C code looks these names up after loading: the only name-to-index
  searches of `obj_flags` are parser handlers (checked 2026-11-16).
- **Revisit:** if the data-file parsers are meant to be as strict as C about code names.
- **Accepted:** 2026-11-15, during the `ObjectFlag` port pass; written up here 2026-11-16. Pinned by
  `lookupAcceptsTheNameWithOrWithoutThePrefix` in `ObjectFlagTest`, and by `flagNoneLoadsAsIndexZero` and
  `flagMaxLoadsAsTheLastIndex` in `ObjectProperty2AssemblerTest`.

## Elements

### `ElementEnum.lookup` takes the `ELEM_` prefix as optional, where C finds only the bare name

- **Java:** `ElementEnum.lookup(String)`, called from `ObjectProperty2Assembler` for the `ignore`, `resistance`,
  `vulnerability` and `immunity` properties, and from `ParseUtils` and `ItemObjectAssembler`. **C:**
  `code_index_in_array(element_names, code)` (`datafile.c`), as called from `parse_object_property_code`
  (`obj-init.c`), and the `element_names` search in `grab_element_flag` (`obj-init.c`).
- **Differs:** C finds `FIRE` and refuses `ELEM_FIRE`. Java adds `ELEM_` when it is missing, so both return
  `ELEM_FIRE`. The placeholders `NONE` and `MAX` are found by both forms in Java, and the caller refuses them, so an
  element type with `code:NONE` or `code:MAX` is refused in both.
  - `type:resistance code:ELEM_FIRE`    C refuses, Java accepts
  - `type:resistance code:FIRE`    C accepts, Java accepts
  - `type:resistance code:NONE` and `code:MAX`    C refuses, Java refuses
  - `type:resistance code:ELEM_ELEM_FIRE` and `code:fire`    C refuses, Java refuses
- **Why:** callers such as `ParseUtils.grabElementFlag` and `ItemObjectAssembler` hold or build the prefixed name.
- **Reach:** none from shipped data. No `code:` line in `lib/gamedata/object_property.txt` has a prefix (checked
  2026-11-16). Hand-edited files only. No C code looks these names up after loading: the only name-to-index searches of
  `element_names` are parser handlers (checked 2026-11-16).
- **Revisit:** if the data-file parsers are meant to be as strict as C about code names.
- **Accepted:** 2026-11-15, during the `ElementEnum` port pass; written up here 2026-11-16. Pinned by
  `aNameWithoutThePrefixFindsTheSameConstant` and `aBareNameIsStillMatchedExactly` in `ElementEnumLookupTest`.

## Blow effect

### Five directives carry a different name in the parse record from the keyword in the data file

- **Java:** `BlowEffect2Grammar.g4`, which names each `Directive` it builds, and `BlowEffect2Assembler`, which switches
  on that name. **C:** `init_parse_eff` (`mon-init.c`), which registers the keywords the data file uses.
- **Differs:** five of the ten directive names are not C's keywords. The other five (`name`, `power`, `eval`, `desc`,
  `resist`) are the same.
  - `lore-color-base:`    C keyword `lore-color-base`, Java directive name `base-colour`
  - `lore-color-resist:`    C keyword `lore-color-resist`, Java directive name `resist-colour`
  - `lore-color-immune:`    C keyword `lore-color-immune`, Java directive name `immune-colour`
  - `effect-type:`    C keyword `effect-type`, Java directive name `effect`
  - `lash-type:`    C keyword `lash-type`, Java directive name `lash`
- **Why:** the name is an internal label between the grammar and the assembler. The data file's own text is still the
  only thing the lexer accepts, so `lore-color-base:` loads and `base-colour:` is refused. The `colour` spelling follows
  the house Oxford spelling, and `effect` and `lash` are the shorter rule names.
- **Reach:** none from a data file; nobody outside the parser reads the name. It only shows in an
  `unknown directive` message from the assembler's `default` branch, and that branch cannot fire for a name the grammar
  built.
- **Revisit:** if a second reader of `BlowEffect2ParseRecord` is written that expects C's names, or if the
  `BlowEffect2ParseRecord` Javadoc is read as a promise of them (it currently says the names are C's).
- **Pinned by:** `everyDirectiveKeywordGivesItsParseRecordName`, `directivesKeepFileOrderNotCanonicalOrder` and
  `realFilePoisonRecordKeepsEveryDirectiveInFileOrder` in `BlowEffect2GrammarTest`.
- **Accepted:** 2026-11-16, found when the first `BlowEffect2GrammarTest` asserted C's names.

### A `resist:` line before the record's `effect-type:` line is refused, where C crashes

- **Java:** `BlowEffect2Assembler.assemble` and `parseResist`, which handle a record's directives in file order and
  throw a `DataFileParseException` when a `resist:` line finds no effect type yet. **C:** `parse_eff_name` and
  `parse_eff_resist` (`mon-init.c`).
- **Differs:** `parse_eff_name` creates each record with `mem_zalloc`, so `effect_type` is a null pointer until an
  `effect-type:` line is read. `parse_eff_resist` compares it with `streq` at once, and `streq` is `!strcmp` (`z-util.h`),
  so a `resist:` line ahead of `effect-type:` calls `strcmp` on null and crashes. Java quotes the `resist:` line in its
  message and ends the load.
- **Agrees:** both resolve a `resist:` against the type read last before it, and each `resist:` replaces the one
  before. Type A, Resist 1, Type B, Resist 2, Resist 3 ends as type B with Resist 3 resolved under B. A `resist:` on any
  type other than `element` or `flag` is refused in both (`PARSE_ERROR_MISSING_BLOW_EFF_TYPE` in C). A directive ahead
  of the first `name:` is refused in Java, and in C it asserts, or for the three colour lines returns
  `PARSE_ERROR_MISSING_RECORD_HEADER`.
- **Why:** C cannot handle the input at all, so refusing it with a line number is the useful behaviour. Resolving
  `resist:` after the whole record instead would differ from C whenever the type changes after the last `resist:`.
- **Reach:** none from shipped data. All 16 `resist:` lines in `blow_effects.txt` follow an `effect-type:` in their own
  record (checked 2026-11-17). Hand-edited files only.
- **Revisit:** if a data file is ever shipped with `resist:` ahead of `effect-type:`, which C could not load either.
- **Pinned by:** `resistBeforeEffectTypeInTheSameRecordEndsTheLoad`,
  `aResistWithNoEffectTypeInTheMiddleRecordEndsTheWholeLoad` and `resistWithNoEffectTypeEndsTheLoad` in
  `BlowEffectReaderTest`.
- **Accepted:** 2026-11-17. Marked in the `BlowEffect2Grammar.g4` header comment.

### An unknown `effect-type:` value is refused, where C stores the string

- **Java:** `BlowEffect2Assembler.parseEffect`, which resolves the value with `BlowEffectType.getFromString` and throws a
  `DataFileParseException` when nothing matches. **C:** `parse_eff_effect_type` (`mon-init.c`) and `blow_color`
  (`mon-lore.c`).
- **Differs:** `parse_eff_effect_type` stores whatever string follows `effect-type:` with no check. `blow_color` is the
  only reader, and it compares the string against `theft`, `drain`, `eat-food`, `eat-light`, `element`, `flag` and
  `all_sustains`. A string that matches none falls out of the chain and returns the base colour, so `effect-type:BOGUS`
  loads and recall shows the base colour. Java refuses it at the line, quoting the line number. The match is exact and
  case-sensitive in both, so `Element`, `FLAG` and `BET_FLAG` are unknown to each.
- **Agrees:** all seven spellings the shipped file uses resolve, and a `resist:` on a type other than `element` or
  `flag` is refused in both (`PARSE_ERROR_MISSING_BLOW_EFF_TYPE` in C).
- **Why:** `BlowEffectType` is a closed enum, so it cannot hold an arbitrary string. Refusing at load names the bad line,
  where C's only trace is a base colour in recall.
- **Reach:** none from shipped data. The 24 records of `blow_effects.txt` that have an `effect-type:` line use
  `element` (3), `flag` (13), `theft` (2), `drain`, `eat-food`, `eat-light` and `all_sustains` (checked 2026-11-17).
  Hand-edited files only.
- **Revisit:** if a modded `blow_effects.txt` ever needs an effect type C does not read, which would also need
  `blow_color` to change.
- **Pinned by:** `anUnknownEffectTypeEndsTheLoadWhereCStoresTheString` and
  `everySpellingTheRealFileUsesIsAKnownEffectType` in `BlowEffectReaderTest`.
- **Accepted:** 2026-11-17.

### An unknown `resist:` name is stored as "no resist", where C stores -1

- **Java:** `BlowEffect2Assembler.parseResist`, which stores `ObjectFlag.OF_NONE`, or `ElementEnum.ELEM_NONE` and
  `ProjectionEnum.PROJ_NONE`, when the name is not found. **C:** `parse_eff_resist` and `flag_index_by_name`
  (`mon-init.c`), and `proj_name_to_idx` (`project.c`).
- **Differs:** both C lookups return -1 for a name they do not know, and `parse_eff_resist` stores it unchecked in the
  effect's one `resist` int. So `effect-type:element` with `resist:BOGUS`, or with `resist:NONE` (the projection name
  list has no `NONE` entry), loads in C with -1 stored. So does `effect-type:flag` with `resist:BOGUS`, or with
  `resist:free_act`, since the flag match is exact. Java stores the `NONE` constants, which are the typed form of
  "nothing resists this".
- **Agrees:** `resist:NONE` under `effect-type:flag` finds index 0 in C (`obj_flags[0]` is `"NONE"`) and `OF_NONE` in
  Java. Element names match ignoring case in both, and flag names match exactly in both.
- **Why:** -1 is not a valid index into either list, and `blow_color` (`mon-lore.c`), the only reader, would use it as
  an array index or a flag number. No real data file could rely on it, so the `NONE` constants lose nothing.
- **Reach:** none from shipped data. All 13 distinct `resist:` values in `blow_effects.txt` resolve (checked
  2026-11-17). Hand-edited files only.
- **Revisit:** if a lookup that tells "no match" apart from `NONE` is ever needed.
- **Pinned by:** `anUnknownElementResistIsElemNone`, `anUnknownOrNoneElementNameStoresNoProjection`,
  `anElementResistNamedNoneStoresNoResistWhereCStoresMinusOne`, `aFlagResistMatchesExactly` and
  `anUnknownFlagAfterAnElementResistLeavesNoElementResistBehind` in `BlowEffectReaderTest`. The reach is pinned by
  `everyElementAndFlagResistInTheRealFileResolves`.
- **Accepted:** 2026-11-17.

### A `MAX` name finds C's trailing name-list entry, where the port finds no record

- **Java:** `BlowEffect2Assembler.parseLash` and `parseResist`, with `WorldRegistry.lookupProjectionByLash`. **C:**
  `parse_eff_lash_type` and `parse_eff_resist` (`mon-init.c`), and `proj_name_to_idx` with `proj_name_list`
  (`project.c`).
- **Differs:** `proj_name_list` ends with `"MAX"` ahead of its `NULL`, so `proj_name_to_idx("MAX")` returns the
  `PROJ_MAX` index, not -1. `lash-type:MAX` therefore stores `PROJ_MAX` as the lash type in C, and the missile fallback
  does not fire. `effect-type:element` with `resist:MAX` stores the `PROJ_MAX` index. Java resolves `MAX` to
  `ProjectionEnum.PROJ_MAX` too, but `projection.txt` has no `code:MAX` record, so `lookupProjectionByLash` returns null
  and the effect's lash type is null. For the resist, the projection field holds `PROJ_MAX` and the element field is
  `ELEM_NONE`, since there is no element called `MAX`.
- **Agrees:** every other name C does not know falls back to `PROJ_MISSILE` for a lash in both, `lash-type:NONE`
  included. The one exception is a `PROJ_`-prefixed name, which Java finds; see the `PROJ_` prefix entry below.
- **Why:** `MAX` is a count marker, not a projection. The index C stores is the number of projections, so it names none
  of them.
- **Reach:** none from shipped data. `blow_effects.txt` has no `lash-type:MAX` or `resist:MAX` line (checked
  2026-11-17). Hand-edited files only.
- **Revisit:** if a data file ever names `MAX`, or if a projection record for `PROJ_MAX` is ever added.
- **Pinned by:** `lashTypeMaxFindsNoProjectionRecordSoTheLashIsNull`, `lashTypeNoneIsMissileLikeAnyOtherNameCDoesNotKnow`
  and `resistMaxStoresTheProjectionMaxIndex` in `BlowEffectReaderTest`.
- **Accepted:** 2026-11-17.

### Changing `effect-type:` clears the resist the new type does not use, where C keeps the number

- **Java:** `BlowEffect2Assembler.parseEffect`, which clears the resist fields the new type does not use. **C:**
  `parse_eff_effect_type` and `parse_eff_resist` (`mon-init.c`), and `blow_color` (`mon-lore.c`).
- **Differs:** C keeps one int per effect for the resist, and `parse_eff_effect_type` only replaces the type string. An
  `effect-type:` line after a `resist:` therefore leaves the number in place, and the new type reads it in its own list.
  `effect-type:element`, `resist:FIRE`, `effect-type:flag` leaves 2, which is FIRE's place in the projection name list
  and, as an object flag, `SUST_INT`. Going from `flag` to `element` reads the flag index as a projection index. Java
  keeps a typed field per list, and the new type's field starts at `NONE`, so both swaps end with no resist. Changing
  to `drain`, `theft`, `eat-food`, `eat-light` or `all_sustains` leaves the number in C and clears it in Java, which
  shows no difference, because `blow_color` reads the number only for `element` and `flag`.
- **Agrees:** repeating the same type keeps the resist in both: `element`, `resist:FIRE`, `element` ends with FIRE, and
  likewise for `flag`. A `resist:` after the new type replaces whatever was there in both.
- **Why:** after a swap the number C ends with names an unrelated entry in the other list, so there is nothing worth
  reproducing. Leaving the old typed field in place would give a `flag` effect an element resist.
- **Reach:** none from shipped data. None of the 30 records in `blow_effects.txt` has two `effect-type:` lines, so no
  type ever follows a resist (checked 2026-11-17). Hand-edited files only.
- **Revisit:** if a data file ever changes the type partway through a record.
- **Pinned by:** `aRepeatedElementTypeKeepsTheElementResist`, `aRepeatedFlagTypeKeepsTheFlagResist`,
  `anElementToFlagSwapClearsTheElementResistWhereCReadsItsNumberAsAFlag`,
  `aFlagToElementSwapClearsTheFlagResistWhereCReadsItsNumberAsAProjection` and
  `aTypeThatTakesNoResistClearsEveryResist` in `BlowEffectReaderTest`.
- **Accepted:** 2026-11-17.

### A `PROJ_`-prefixed projection name is found, where C finds only the bare name

- **Java:** `ProjectionEnum.lookup`, which `BlowEffect2Assembler.parseLash` and `parseResist` call. **C:**
  `proj_name_to_idx` with `proj_name_list` (`project.c`), which `parse_eff_lash_type` and `parse_eff_resist`
  (`mon-init.c`) call.
- **Differs:** C's name list holds bare names (`"FIRE"`), so `proj_name_to_idx("PROJ_FIRE")` is -1. `lash-type:PROJ_FIRE`
  therefore stores `PROJ_MISSILE`, and `effect-type:element` with `resist:PROJ_FIRE` stores -1. Java puts `PROJ_` on a
  name that lacks it and takes one that already has it, so the lash is FIRE. The resist fills the projection field with
  `PROJ_FIRE` and leaves the element field at `ELEM_NONE`, because the element lookup then searches for `ELEM_PROJ_FIRE`,
  which no element has.
- **Agrees:** the match ignores case in both, and the prefix test is case-sensitive, so `proj_fire` is an unknown name
  and a lash of `PROJ_MISSILE` in both. `NONE` and `MAX` are covered by their own entries.
- **Why:** the port's constants carry the prefix, and `ProjectionEnum.lookup` was written to take either spelling, as
  `ElementEnum.lookup`, `ObjectFlag.lookup` and `ObjectModifier.lookup` do. `ProjectionEnumLookupTest` checks all 56 C
  names bare and prefixed.
- **Reach:** none from shipped data. `blow_effects.txt` has no `PROJ_` text at all (checked 2026-11-17, and identical to
  the upstream 4.2.6 file). Hand-edited files only.
- **Revisit:** if the data-file parsers are meant to be as strict as C about code names.
- **Pinned by:** `aPrefixedLashNameFindsItsProjectionWhereCFallsBackToMissile` and
  `aPrefixedElementResistFillsOnlyTheProjectionField` in `BlowEffectReaderTest`. The lookup itself is pinned in
  `ProjectionEnumLookupTest`.
- **Accepted:** 2026-11-17, during the `BlowEffect2Assembler` port pass.

### A colour followed by a colon and more text is an unknown colour, where C drops the text

- **Java:** `BlowEffect2Lexer.g4`, where `lore-color-base:`, `lore-color-resist:` and `lore-color-immune:` push into the
  free-text mode and `STRING` takes the rest of the line, read by `BlowEffect2Assembler.parseColour`. **C:**
  `parse_eff_lore_color`, `parse_eff_lore_color_resist` and `parse_eff_lore_color_immune` (`mon-init.c`), registered by
  `init_parse_eff` as `sym color`, and `parser_parse` (`parser.c`).
- **Differs:** `parser_parse` cuts a `sym` field with `strtok(sp, ":")`, and text after the last registered field is
  dropped without an error. `lore-color-base:Light Green:x` reads `Light Green` and `lore-color-base:G:x` reads `G`, so
  both are Light Green in C. Java's value is the whole rest of the line, `Light Green:x` or `G:x`, which names no colour
  and is white.
- **Agrees:** a colour with no colon is the same in both. `effect-type:`, `resist:` and `lash-type:` are `str` fields in
  C, which take the rest of the line whole, so a colon in those agrees.
- **Why:** the colour directives share the lexer's free-text rule with the `str` directives, and a colon in a colour is
  not something the data file has.
- **Reach:** none from shipped data. No `lore-color-*` line in `blow_effects.txt` has a second colon (checked
  2026-11-17). Hand-edited files only.
- **Revisit:** if a data file ever carries text after a colour.
- **Pinned by:** `aColourFollowedByAColonAndTextIsWhiteWhereCDropsTheText` in `BlowEffectReaderTest`.
- **Accepted:** 2026-11-17, during the `BlowEffect2Assembler` port pass.

### A single high-bit colour character is white, where C refuses it on a signed-`char` platform

- **Java:** `BlowEffect2Assembler.parseColour`. **C:** `parse_eff_lore_color`, `parse_eff_lore_color_resist` and
  `parse_eff_lore_color_immune` (`mon-init.c`), and `color_char_to_attr` (`z-color.c`).
- **Differs:** a colour of one character goes to `color_char_to_attr`, which hands a negative `char` straight back, and
  the handler then returns `PARSE_ERROR_INVALID_COLOR` for any negative result. A lone byte with the high bit set is
  therefore a refusal where `char` is signed, and white where it is unsigned. Java works on decoded characters, so it has
  no negative character: the single character a decoder gives for such a byte (`U+00E9` from Latin-1, `U+FFFD` from
  UTF-8) matches no colour and is white.
- **Agrees:** an unknown ASCII character or name is white in both, and C never refuses a name, because
  `color_text_to_attr` cannot return a negative number.
- **Why:** the data files are ASCII, and C's own answer depends on the platform, so there is no single behaviour to
  copy.
- **Reach:** none from shipped data. `blow_effects.txt` has no non-ASCII character (checked 2026-11-17). Hand-edited
  files only.
- **Revisit:** if a data file ever carries non-ASCII colour text.
- **Pinned by:** `aHighBitCharacterIsWhiteWhereCRefusesOnASignedCharPlatform` in `BlowEffect2AssemblerTest`.
- **Accepted:** 2026-11-17, during the `BlowEffect2Assembler` port pass.

### Building a `BlowEffect` needs the projections loaded, where C's zeroed struct needs nothing

- **Java:** the no-argument `BlowEffect` constructor, which `BlowEffect2Assembler.assemble` calls for every record, and
  `WorldRegistry.lookupProjectionByCode`. **C:** `parse_eff_name` (`mon-init.c`), which creates the record with
  `mem_zalloc`, and `struct blow_effect` (`mon-blows.h`).
- **Differs:** a record with no `lash-type:` line keeps `lash_type` 0 in C, which is the `ACID` entry of `proj_name_list`
  (`project.c`) and the first record of `projection.txt`. Java stores a `Projection` object rather than an index, so the
  constructor looks up the ACID record to hold the same default. If the projections are not loaded,
  `lookupProjectionByCode` throws an `IllegalStateException`, and that surfaces before the assembler has read a single
  directive. C stores only the index, so it has no such dependency.
- **Agrees:** a record with no `lash-type:` ends with the ACID lash in both, which is what `effect-handler-attack.c` and
  `mon-spell.c` read through `blow[0].effect`. A `lash-type:` line replaces the default in both.
- **Why:** the default has to be the ACID projection to match C, and the projections are Java objects held by
  `WorldRegistry`, so the lookup is the only way to build one. The projections load ahead of the blow effects in
  `GameConstants`, so the load order holds.
- **Reach:** none from the game's own load order. Only a test, or a caller, that builds blow effects with the projection
  table empty reaches it.
- **Revisit:** if blow effects are ever loaded before projections, or built where `WorldRegistry` is not set up.
- **Pinned by:** `aRecordBeforeTheProjectionsAreLoadedIsAnIllegalState` in `BlowEffect2AssemblerTest`, and
  `recordWithNoEffectTypeKeepsNullTypeAndEmptyResists` and `everyLashTypeInTheRealFileResolvesToAProjection` in
  `BlowEffectReaderTest`.
- **Accepted:** 2026-11-18, during the `BlowEffect` port pass.

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

## Numbers in every data file

These three entries cover every game data file and its lexer, not one. They are about the text of a number field, which
C reads with `strtol` or `strtoul` and the lexers read with a token rule, and about the `:` that separates fields.

### A blank between a directive's colon and its number is a lexer error, where C skips it

- **Java:** every numeric token rule in the lexers under `parser/grammars` (the `INTEGER`, `INT` and `UINT` families,
  and `PIT_INTEGER`). None has a rule for a leading blank. **C:** `parser_parse` (`parser.c`), which cuts an `int` or
  `uint` field at the next `:` with `strtok` and then calls `strtol` or `strtoul` in base 0. Both skip leading white
  space.
- **Differs:** `room: 3`, `hit: 50`, `power-cutoff: 10` and the like are 3, 50 and 10 in C. In Java the blank has no
  rule, so the lexer reports a token recognition error and the file loads as nothing.
- **Why:** the lexers are deliberately strict about what may follow a keyword's colon, and a blank there is a typing
  slip in a hand-edited file, not something the shipped files do.
- **Reach:** none from shipped data. A search of `lib/gamedata` on 2026-11-12 found no numeric directive with a blank
  after its colon (text keys such as `desc:` and `name:` excluded, as is the unused `old_class.txt`). It takes a modded
  file. The `Projection` entry for `denominator:` is this deviation seen from one file.
- **Checked:** the rule bodies of the `INTEGER`, `INT` and `UINT` families listed by name. The numeric tokens defined
  through a fragment (`INT_FRAGMENT`, `INTEGER_FRAGMENT`, `SIMPLE_DICE_STRING`) were not read one by one.
- **Revisit:** if a numeric rule is ever given a leading-blank allowance. Then add one for every lexer, not one file.
- **Pinned by:** `aBlankBeforeANumberIsAHardErrorWhereCSkipsIt` in `PitReaderTest`, for the four `UINT`-family rules
  and `PIT_INTEGER`; and `aLeadingBlankOrAnExtraColonFieldInAnIntegerIsAHardErrorWhereCAcceptsIt` in
  `ProjectionReaderTest`, for `ProjectionLexer.g4`'s `INTEGER` (its `" 1"` case). The `Projection` pin named in that
  entry covers `denominator:`.
- **Accepted:** 2026-11-12, raised while checking the `UINT` and `PIT_INTEGER` rules of `PitLexer.g4`.

### Text after a number, or an extra `:` field, is an error, where C ignores it

- **Java:** the numeric token rules, and in the four lexers that have a `UINT` tail (`PitLexer.g4`,
  `PainLexer.g4`, `BlowMethodLexer.g4`, `MonsterSpellLexer.g4`) the `:` that ends that tail. **C:** `parser_parse`
  (`parser.c`). It cuts each `int` or `uint` field at a `:`, `strtol` and `strtoul` stop at the first character they
  cannot use, and once the last registered field is read, anything left on the line is dropped without an error.
- **Differs:** two shapes.
  - **Text on the number itself:** `5xyz` is 5 in C. The four `UINT` rules take the whole field up to a `:` or the end
    of the line, so they agree, as does `ProjectionLexer.g4`'s `INTEGER` (2026-11-12). A rule with no tail (`PIT_INTEGER`, and any `'-'? ('0'..'9')+` rule that remains) leaves the
    `xyz` to the next rule, which has none for it, and the load stops.
  - **A `:` after the last field:** `room:3:x`, `obj-rarity:5:x` and `alloc:1:2:3` read as 3, 5 and 1 and 2 in C. In
    Java the tail stops at the `:`, which has no rule in that mode (or, after `alloc:`, is read as one more field), so
    the load stops.
- **Why:** shipped files never write either, and a lexer that swallowed the rest of the line would hide a typo in a
  field that a modder meant to be read.
- **Reach:** none from shipped data. `pit.txt`'s 115 numeric lines are plain digits, and no numeric directive in
  `lib/gamedata` has letters after its number. A trailing blank or tab after a number, which shipped files do have
  (`numerator:1  ` in `projection.txt`, `armor:0:0 ` in `artifact.txt`), is not part of this entry: C reads
  it, and a Java reader that refused it would fail on shipped data, which is a bug, not a deviation.
- **Revisit:** if a modded file is ever expected to carry stray text after a number. Then the tail rule and the `:`
  handling change together, in every lexer that has one.
- **Pinned by:** `aTailOnAUintLoadsAsCDoesButOneOnInnateFreqIsAHardError` and `anExtraColonFieldIsAHardErrorWhereCIgnoresIt`
  in `PitReaderTest`, and the `"1:2"` case of `aLeadingBlankOrAnExtraColonFieldInAnIntegerIsAHardErrorWhereCAcceptsIt`
  in `ProjectionReaderTest`. `ProjectionLexer.g4`'s `INTEGER` now takes a tail, so it agrees with C on text within a
  field (`1abc` is 1), pinned by `aPlusSignHexPrefixOrTrailingTextOnAUintLoadsAsCDoes`; only the `:` is left of it here.
  `aTrailingBlankAfterANumberLoadsAsInC` in `PitReaderTest` pins the boundary: a trailing
  blank is not part of the deviation. MonsterSpell is pinned (2026-11-12) by `aBlankBeforeAHitOrAColonFieldAfterItIsRefusedWhereCAcceptsIt`,
  `aPowerCutoffWithNoDigitIsRefusedAsInC`, `anEffectYxWithABadOrMissingFieldIsRefusedAsInC` and
  `anEffectRadiusWithNoDigitOrAnExtraFieldIsRefused` in `MonsterSpellReaderTest`, with
  `aTrailingBlankAfterAHitLoadsAsInC` as its boundary. The other two `UINT`-tail lexers (Pain, BlowMethod) are not
  pinned.
- **Accepted:** 2026-11-12, raised while checking the `UINT` and `PIT_INTEGER` rules of `PitLexer.g4`.

### An empty `:` field on an effect line, trailing or doubled, is an error, where C skips it

- **Java:** the `effect` rule of `EffectBlock.g4`, which expects a `UCASE` or `EFFECT_INTEGER` after every `:`. **C:**
  `parser_parse` (`parser.c`). It cuts each `sym`, `int` and `uint` field with `strtok(sp, ":")`, and `strtok` skips
  empty tokens, so a `:` that ends the line, or two in a row, produces no field. A field marked optional
  (`effect sym eff ?sym type ?int radius ?int other`) is then simply absent.
- **Differs:** in C `effect:BOLT:ARROW:5:` is radius 5 with no other, `effect:BOLT:ARROW:` and `effect:BOLT:` are an
  effect with no radius or no sub-type, `effect:BOLT:ARROW:5::3` is radius 5 and other 3, `effect:BOLT:ARROW::3` is
  radius 3, and `effect:BOLT::ARROW:5` is sub-type ARROW and radius 5. In Java each of the six is a syntax error and the
  file loads as nothing. A field that is not optional is refused by both (`effect-yx:1:` is a missing field in C).
- **Why:** an empty field is a typing slip in a hand-edited file, and reading `::3` as "radius 3" would move a value
  into a different field from the one the author's colon count says.
- **Reach:** none from shipped data. A search of `lib/gamedata` on 2026-11-12 found no `effect:` line with a trailing or
  doubled `:`. Checked through the monster spell reader only; the other files that use `EffectBlock.g4` share the rule
  but were not run.
- **Revisit:** if a modded file is ever expected to carry an empty field. Then the `effect` rule changes once, for
  every file that imports `EffectBlock.g4`.
- **Pinned by:** `anEmptyColonFieldOnAnEffectLineIsRefusedWhereCSkipsIt` in `MonsterSpellReaderTest`.
- **Accepted:** 2026-11-12, raised while checking `EFFECT_INTEGER` and `HIT_UINT` in `MonsterSpellLexer.g4`.

## Quest

### A `level:`, `race:` or `number:` line before the file's first `name:` is refused, where C crashes

- **Java:** `QuestGrammar.g4` (the `quest` rule starts with `name`) and `QuestReader`, which throw a
  `ParseCancellationException` for a syntax error. **C:** `parse_quest_level`, `parse_quest_race` and
  `parse_quest_number` (`player-quest.c`).
- **Differs:** `init_parse_quest` starts with `parser_setpriv(p, NULL)`, and only `parse_quest_name` replaces it with a
  new quest. The other three handlers read the quest back with `parser_priv(p)` and then `assert(q)`. So a
  `level:` line ahead of the first `name:` fails the assert, and in a build with asserts compiled out it writes through
  a null pointer. Java ends the load and names the offending line.
- **Agrees:** a `level:`, `race:` or `number:` after a `name:` belongs to the record that `name:` opened, in any order and
  any number of times, and the last of each kind wins. Comments and blank lines ahead of the first `name:` load in both.
- **Why:** this is the standing rule for every place C crashes (see "Where C crashes, Java throws" below).
- **Reach:** none from shipped data. `quest.txt` opens with `name:Sauron`. A hand-edited file only.
- **Revisit:** never expected. C cannot load such a file either.
- **Pinned by:** `aDirectiveBeforeTheFirstNameIsRefusedHereWhereCCrashes` in `QuestReaderTest`.
- **Accepted:** 2026-11-18, found while checking `QuestGrammar.g4` against Stage 7a.

## Where C crashes, Java throws

### Every input that crashes C is refused in Java with a message that names the line

- **Java:** the assembler or grammar for the file in question throws a `DataFileParseException` or, for a syntax error,
  a `ParseCancellationException`. **C:** an `assert`, a `strcmp` on a null pointer or a write through one.
- **Differs:** C has no defined behaviour for these inputs: the game aborts, or the process faults, usually with no
  message that points at the data file. Java ends the load with an error that quotes the line and the file.
- **Rule:** decided 2026-11-18. Wherever C crashes on a data file, Java throws. Do not try to copy the crash, and do not
  load the record in some invented way. Each case still gets its own entry, so the list of them is findable, and its own
  `...HereWhereCCrashes` pin.
- **Cases so far:** a directive ahead of the first `name:` in `quest.txt` (above); a `resist:` ahead of `effect-type:` in
  `blow_effects.txt` and a directive ahead of the first `name:` there (see "Blow effect"); a `cycle-color:` ahead of any
  `cycle:` in `visuals.txt`, which `docs/grammar_issues.md` says dereferences a null `cycles[0]` (not re-checked in
  Java today); a `weight:`, `cost:`, `graphics:` (and, on a light, `act:` or `time:`) line on a numeric `base-object:`
  sval that names no kind in `artifact.txt` (see "Artifact").
- **Not covered:** an unresolved `fallback:` in `summon.txt`, which is "Summon" above. C's `strcmp(name, NULL)` is
  undefined behaviour and Java keeps the record instead of throwing. That entry predates this rule; decide whether to
  make it throw.
- **Reach:** none from shipped data, by definition: the shipped files load in C.
- **Accepted:** 2026-11-18.

## Put off until later, accepted for now

Deviations you have accepted for the time being, with the fix agreed in principle and deferred. Each one is a real gap
to close, not a permanent choice, so it is worth a line in `docs/ROADMAP.md` too. The entries use the same fields as
above, and **Later** says what the fix is. When one is fixed, delete the entry and its pinning test's `WhereC` name.
Move an entry up into its own section instead if you decide to keep the deviation for good.

No entries at present. The World entry (a level, up or down name made only of digits) was closed on 2026-11-22 and
taken out; see `docs/Issue_Approach_Order.md`, 7i.

## Pending your decision

- The five `objDescNameFormat` divergences (Part G; `docs/ItemObject_round2/part_G.md`). All unreachable from shipped
  data; four are already in the Javadoc and the fifth (a `~` inside a kept `|x|y|` alternative) is new. Not accepted
  until you say so.

## Call to action

- Say which of the pending `objDescNameFormat` cases to accept, and I will move them into the ItemObject section.
- Check `docs/ItemObject_round2/summary.md` item 4: it now carries both `ACCEPTED` and `FIXED` as its **Result**, and
  they cannot both be true.
