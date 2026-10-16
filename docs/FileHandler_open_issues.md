# FileHandler.java — open issues (2026-10-15 `/port` stage 1)

Checked against `z-file.c`. All paths are `src/main/java/uk/co/jackoftradesltd/backend/io/`.

## Open

- [X] (1)  `FileHandler.java:409-412`: `getLine` reads ahead through a `BufferedReader`, so `readCharacter` and `skipBytes` after a `getLine` lose their position. C builds `file_getl` on `file_readc`, so the two mix freely.
  - **Decided 2026-10-16 (Option B):** `inputStream` becomes the one shared buffered stream (a `PushbackInputStream` over a `BufferedInputStream`), and `readCharacter`, `skipBytes`, `read` and `getLine` all go through it. `getLine` becomes a byte-by-byte port of `file_getl`, collecting into a byte array and decoding once as UTF-8 at the end. `bufferedReader` and `savedLine` go. This also removes the three line-ending quirks below.
  - **Deliberate divergence from C:** when a line stops because the buffer is full (or the tab `break`) and the last bytes are an incomplete UTF-8 sequence, `getLine` drops those bytes from the line and pushes them back, so the next call starts on a whole character. C would return the cut character as stray bytes; here the line may be up to 3 bytes shorter than `len - 1`. The check is made on the bytes (lead byte against bytes present), never by looking for `�`, and never at a newline or end of file. Mark this in the Javadoc as a divergence when the code is written.
  - Tab stops and the `len - 1` cut-off count bytes, as in C.
- [X] (2)  `FileHandler.java:209`, `FileHandler.java:226`, `FileHandler.java:91-92`: the other stream is closed but never nulled, and `close()` (`FileHandler.java:294-304`) closes only one side. After `open(WRITE)` then `open(READ)`, `close()` returns true and `isOpen()` stays true.
- [X] (3)  `FileHandler.java:246`, `FileHandler.java:251`, `FileHandler.java:256`: `open` returns true even when the stream failed to open. C returns NULL.
- [X] (4)  `FileHandler.java:240`: `open` ignores `type`. C opens `FTYPE_SAVE` writes with `O_EXCL`, so an existing savefile makes the open fail; here it is truncated.
- [X] (5)  `FileHandler.java:225`: `toRealPath()` needs the file to exist, so `open(MODE_WRITE)` and `open(MODE_APPEND)` on a missing file fail. C's `"wb"` and `"a+"` create it.
- [X] (6)  `FileHandler.java:361`: `read` decodes bytes with the default charset, so bytes above 127 are mangled. C's `file_read` returns raw bytes and a count.
- [X] (7)  `FileHandler.java:107`: `fileMove` refuses an existing target. C's `rename` replaces it on POSIX.
- [X] (8)  `FileHandler.java:162-164`: `fileGetSavefile` returns `base.ext`, which is what C's `file_get_tempfile` does. C's `file_get_savefile` adds `Rand_simple(1000000)` and retries up to 100 times while the name exists.
- [X] (9)  `FileHandler.java:454`: `putLine` appends `"\n"`. C's `file_put` writes the buffer verbatim, and `file_putf` callers supply their own newline.
- [X] (10)  `FileHandler.java:338`: `writeCharacter` opens lazily with truncation (`append=false`), while `write` (`FileHandler.java:375`) and `putLine` (`FileHandler.java:448`) append. C has no lazy open.
- [X] (11)  `FileHandler.java:191-193`: `fileNewer` compares at full precision. C compares `st_mtime` in whole seconds.
- [X] (12)  `FileHandler.java:172-174`: the `fileNewer` Javadoc still says "earlier" although the code now returns true when the first file is newer.

## Line-ending quirks in `getLine`

- `FileHandler.java:412`: a lone `\r` at end of file returns an empty line. C returns false.
- `FileHandler.java:412`: `a\r\r\nb` returns `a` then an empty line. C swallows the extra `\r` and returns `a` then `b`.
- `FileHandler.java:395-405`, `FileHandler.java:423-430`: C leaves a line of exactly `len-1` characters followed by an empty line for the leftover newline; here no empty line is produced. Only matters if you want C exactly.

## Out of scope

- `AngFile.java:265`, `AngFile.java:272`: `AngFile.getLine()` calls `fileHandler.getLine()` with no argument, so the project does not compile until it passes a length.
- `FileHandler.java` has no counterpart for `file_lock`, `file_unlock`, `file_get_tempfile` or `file_open_hook`.
- `z-file.c` already carries `/* Ported to Java 2026-09-14 */` on `file_close`, `file_skip`, `file_readc`, `file_writec`, `file_read`, `file_getl`, `file_put` and `file_putf`; the open items above mean those stamps are ahead of the Java.
