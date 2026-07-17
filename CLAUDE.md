# Project notes

This repo (`uk.co.jackoftrades`) is a Java reimplementation/port of Angband 4.2.6, originally written in C.

The original upstream Angband 4.2.6 C source is available locally at:

`C:\Users\rowan\OneDrive\Documents\Desktop\Angband-4.2.6`

(C sources under `src/`, original game data under `lib/`.) Use it to cross-reference the original
implementation/semantics when porting logic, parsers, or data formats to Java.

# Working rules (these override default behaviour)

This repo is a learning exercise. Rowan writes the port; Claude teaches, reviews, and tests.

**0. Rowan is non-binary — they/them.** This applies to Javadoc, code comments and docs as much
as to chat. Third-person prose about Rowan is where it slips; second person ("you write the port")
usually reads better anyway.

**1. Do not create or edit files under `src/main/**`.** That is Rowan's code, and writing it is how
they learn. Describe the change with file:line and let them type it. A PreToolUse hook forces a
confirmation prompt on any such edit — treat that prompt as a stop sign, not a formality. The only
routine reason to answer it is a comment/Javadoc pass Rowan has explicitly asked for; ask "Are you
sure you want me to change X?" and wait for a Yes.

- No prompt needed: `src/test/**` (tests are Claude's standing half of the work), `scratch/**`,
  `*.md`, `.claude/**`.
- Running ANTLR or gradlew is fine — generated output is not authored code.

**2. "try that" / "try it" / "do that" means VERIFY, not implement.** Rowan's definition, verbatim:
"have a look at the changes I've made and run through the possible values to see if they have made
any progress." So: re-read the changed files, REGENERATE anything generated (a stale committed
parser silently tests the wrong thing), run the cases against the C-derived expected values, report
what passes and what doesn't. It is never permission to write code.

**3. Never invoke `git`** — not commit or push, and not read-only status/diff/log either.
Committing is reserved as Rowan's exercise. To check a file's state, diff it against a copy.

**4. Read the memory FILE, not just its MEMORY.md index line.** The index hooks are lossy
summaries, and acting on the summary instead of the file is a known failure. Open the matching
`project_*_front_line` memory before starting on a grammar, and open the file that states a rule
before relying on it.
