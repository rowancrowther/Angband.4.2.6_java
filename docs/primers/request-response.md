# Primer: request–response over a one-way pair of channels

*Primer from `Architecture_migration.md`, written against `uk.co.jackoftrades.channel` and the
`GameInput` / `CommandGetter` boundary. Raised on 260821 while porting `ignore_drop`, whose confirmation prompt is the
smallest instance of the problem.*

`producer-consumer.md` covered the one-way shape: the producer says "here is an item" and resumes immediately. Every
message in the port so far fits that shape. `get_check` does not, and this page is about what changes when the sender
needs an answer back.

## Why this is a different problem

C's front end talks to the core in two unrelated ways, and only one of them is a bus:

```
  event_signal(EVENT_HP)          get_check("Really drop? ")
  game-event.c                    game-input.c:91
  core ──── notify ────▶ UI       core ──── call ────▶ UI
  no return value                 core ◀─── bool ─────┘
  fire and forget                 core is stopped in the middle of a statement
```

`event_signal` is a publication: `redraw_stuff` raises it and keeps going. `get_check` is an ordinary C function call
through a hook pointer — `if (get_check_hook) return get_check_hook(prompt);` — so the core's stack is *parked inside
the UI* until the player presses a key. On one thread that is free. On two threads it has to be built.

And it cannot be dropped, because callers use the answer to make core-side decisions immediately.
`obj-ignore.c:664-676` writes `!d` onto the object when the player declines, so the prompt is not repeated;
`cmd-obj.c:323-331` loops `while (n--)` and returns early on the first refusal. The value has to come back before the
next statement runs.

## What the port has today

```
  coreQueue                        uiQueue
  core receives ◀── UISender ───   UI receives ◀── CoreSender ──
                    (UI thread)                     (core thread)
                 ◀── EDTSender ──
                    (EDT)
```

`Channels.create()` (`Channels.java:80-87`) already gives both directions, so nothing new has to be built to *carry* a
reply. The whole difficulty is that `coreQueue` is a **shared inbox**: the core's
`receive()` returns the next message on it, which need not be the reply it is waiting for. While the core blocks on a
prompt, the UI thread may put a queued command on it, and the EDT's forwarded
`WindowCloseRequested` may already be sitting in front of the answer.

The four options below are four answers to *that*, not to "how do I send two messages".

## Option A — correlation id on the shared inbox

Each request carries a token; the reply echoes it. The core loops on `receive()`, matches the token, and does something
with everything else it pulls out on the way.

```java
record Prompt(long id, String text)  // CoreMessage
record Answer(long id, boolean yes)  // UIMessage
```

**Buys:** one queue, no new plumbing, and it generalises to every getter in `GameInput` at once —
`getQuantity`, `getItem`, `getCurse` all become `record Answer<T>`-shaped.

**Costs:** "something with everything else" is the whole design. The non-matching messages are real work that has
arrived early, so the core must either buffer them and replay them after the prompt (order preserved, but now there are
two places messages come from) or handle them inline (re-entrancy:
a command handled inside a prompt can raise another prompt). Neither is hard; both are a decision that has to be written
down, because getting it wrong shows up as a rare lost keystroke, not a crash.

## Option B — a dedicated reply channel per boundary

A second `Sender`/`Receiver` pair used only for answers. The core's prompt call receives on the reply channel, so
nothing else can ever be on it.

**Buys:** the matching problem disappears — the next message on that channel *is* the answer, by construction. It is
also the option a test double likes best: a fake UI is "a thing that puts answers on the reply channel", with no
protocol to imitate.

**Costs:** a third queue, and `Channels` stops being a two-field record. More importantly it splits the ordering
guarantee: a command sent on `coreQueue` and an answer sent on the reply channel have no defined order relative to each
other, so anything that depends on "the player pressed n *before* they pressed d" needs care. For yes/no prompts that
dependency does not exist; for a prompt that can be answered by a keystroke that is *also* a command, it might.

## Option C — a rendezvous handed over inside the request

The request carries the reply slot: a `SynchronousQueue<Boolean>` (or `CompletableFuture<Boolean>`)
created by the core, put into the message, and taken from by the core immediately after sending.

```java
record Prompt(String text, SynchronousQueue<Boolean> reply)  // CoreMessage
```

**Buys:** the least code by a wide margin, and no ambiguity at all — the slot is private to this one exchange, so there
is no matching, no buffering, and no third queue. It is the closest mechanical equivalent to what C's hook call already
does.

**Costs:** it puts a mutable, thread-coordinating object into the message protocol, and the protocol is the one part of
this design that has stayed pure data. A record that carries a queue is no longer a description of *what happened* — it
is a callback in a trench coat, and the sealed-interface-as-wire- protocol property (the primer menu's "records + sealed
interfaces as a wire protocol", not yet written) quietly weakens: you can no longer log a message, serialise it, or
replay it in a test. Worth naming that cost explicitly rather than discovering it at stage 5's boundary test.

## Option D — don't block: re-issue the command

The architecturally honest option, and the one that is not a variant of the other three. The core does not wait.
`ignore_drop` sends the prompt and *returns*; the answer arrives later as an ordinary command, which re-enters the
operation with the decision already made.

C has the machinery for this and uses it elsewhere — `cmd_get_arg` / `cmd_set_arg` and `cmdq_push`
exist precisely so a command can be re-run with an argument the player has since supplied, which is why
`CommandCode` carries `cmd_arg` slots at all.

**Buys:** no blocking, so no shared-inbox problem, no deadlock, and the core stays responsive to everything else while a
prompt is on screen. It is what a CSP purist would write.

**Costs:** it inverts every call site. `if (!get_check(...)) return;` in the middle of a loop over the gear becomes a
state machine that remembers which slot it had reached — and there are ~30 `get_check`
call sites in the C, plus every other getter. This is a rewrite of the ported logic, not a change to the transport, and
it makes the Java stop resembling the C it is a port of.

## The hazards all four share

**Closing the window during a prompt.** The player answers by closing the window. Under A and B the core is blocked on a
receive that will never complete unless close is handled on that path; under C the
`SynchronousQueue` never fills; under D it is a non-issue. Whatever the choice, the UI half must have a defined answer
to "a prompt is open and `WindowCloseRequested` arrives", and it probably is "answer no, then proceed with shutdown".

**Deadlock by symmetry.** If the UI ever blocks waiting on the core while the core is blocked waiting on the UI, both
halves stop and `main`'s joins never return. Today the UI never asks the core for anything, which is what makes A, B and
C safe — that asymmetry is load-bearing and should be stated as an invariant before a second `getCheck`-shaped path
appears in the other direction.

**The absent-hook fall-back is not a stub.** `DefaultGameInput.getCheck` returning `false`
(`DefaultGameInput.java:128`) is a faithful port of `game-input.c:96-97`, not a placeholder. Headless tests and the
pre-UI phases genuinely run through it, so whatever replaces it must be *swapped in*
behind `GameInputHolder` rather than replacing the interface — the same move `ChannelStatusDisplay`
made at stage 2.

**One implementation, not one per call site.** All the channel code belongs in a single
`ChannelGameInput implements GameInput`. If channel types appear in a `cmd-*` port, the boundary has leaked.

## Where this leaves the decision

A and B differ only in whether the reply shares the inbox; C is A with the matching done by object identity instead of a
token; D is a different architecture. The honest reading is that D is right and too expensive to adopt wholesale, so the
question is which of A–C is the least bad blocking bridge — and that answer probably depends on whether the birth
sequence (Chapter 3's other request/response customer) needs to stay responsive while a prompt is up.

`get_check` is the smallest useful case to settle it on: one boolean, no abort channel, and a live caller in
`ignore_drop`. It was settled on exactly that, the same day — see below.

## Settled — 260821: option A, with pushback buffering

**The decision: correlation id on the shared inbox, non-matching messages buffered, not bounced.**

### The null-id objection, and why it does not apply

The obvious con of A is that a correlation id sounds like a field every message has to carry and almost every message
has to leave null. It is not, because the protocol is sealed records rather than a tagged union:

```java
// in CoreMessage
record Prompt(long id, String text) implements CoreMessage {}

// in UIMessage
record Answer(long id, boolean yes) implements UIMessage {}
```

The id lives on those two shapes only. `SimpleCoreMessage`, `TextCoreMessage` and the lifecycle pair do not grow a
field, so there are no nulls to thread anywhere. The core matches on the type first and the id second —
`case UIMessage.Answer a when a.id() == awaiting ->` — and a message without an id cannot reach that arm at all.

This is the same payoff that `Architecture_migration.md` records for rejecting
`record CoreMessage(GameEventType, Object payload)`: with sealed records, "carries a correlation id" is a property of a
*shape*, not a nullable column on every message.

### The id is smaller than it looks

The core blocks while waiting, so **at most one request is ever outstanding**. A monotonic `long` from a plain field is
enough — one requester thread, no concurrency on the counter, no uniqueness requirement beyond "different from the last
one".

That narrows what the id is actually for. It is not for interleaving concurrent requests; it is for telling the answer
you are waiting for apart from a **stale** answer to a prompt that was already abandoned (window closed, prompt
cancelled). Stale answers are discarded, not buffered — buffering them would deliver a yes/no to whatever asks next.

### Why not bounce non-matching messages back

Returning an unwanted message to the other side with a "resend this" attachment was considered and rejected, on four
counts:

- **It reorders.** A returned message rejoins the back of the far queue, behind everything sent since. Two bounced
  messages can come back swapped.
- **It is a hot loop.** The UI resends at once, the core is still prompting, it bounces again — two threads spinning for
  as long as the prompt is on screen. Containing that means owning a back-off or retry-count policy that nothing else in
  this design needs.
- **It makes each half handle its own messages.** A `Resend` wrapper means the UI needs `case` arms for messages it
  sent, and must be careful not to re-run their side effects. That is new protocol surface on the half that should not
  need any.
- **It has no termination story.** If the prompt ends by the window closing, whatever is mid-bounce is in flight between
  two halves that are already shutting down.

### What buffering costs

One `ArrayDeque<ChannelMessage>` on the core side, plus a single receive helper: take from the deque when it is
non-empty, otherwise from `CoreReceiver`. Every other core-side receive goes through that one method and is otherwise
unchanged.

- Order is preserved exactly — it is a FIFO in front of a FIFO.
- Nothing is delayed that could have run anyway. The core is blocked for the duration of the prompt either way, so the
  buffered messages had no chance of being processed during it.

Handling non-matching messages *inline* — running them re-entrantly inside the prompt — was rejected:
a command handled inside a prompt can raise another prompt, and the re-entrancy that follows is not worth the queue it
saves.

### The one thing that must not be buffered

`WindowCloseRequested`. If it goes into the deque, closing the window during a prompt blocks the core forever and `main`
's joins never return — the same shape as the crash path recorded in
`thread-lifecycle-and-shutdown.md`, where the JVM stays alive with a dead window on screen.

So the wait loop keeps a small allow-list handled *during* the wait: close and lifecycle. This is narrower than inline
handling in the rejected sense — it does not run commands re-entrantly, it answers the prompt with its absent-hook
default and lets shutdown proceed.

### The wait loop, as four outcomes

| message                      | action                                     |
|------------------------------|--------------------------------------------|
| `Answer` with the awaited id | return it — the prompt is done             |
| `Answer` with any other id   | stale; discard                             |
| close / lifecycle            | default the prompt to "no", then shut down |
| anything else                | push to the deque, keep waiting            |

The absent-hook default in row three is not an invention: it is `game-input.c:96-97`, the same `false`
that `DefaultGameInput.getCheck` (`DefaultGameInput.java:128`) already returns.

### What this does not settle

Only the transport. The `get_*` getters that can abort (`getItem`, `getQuantity`, `getCurse` — every
`Optional` return in `GameInput`) need a reply shape that distinguishes "aborted" from "answered", which
`Answer(long, boolean)` cannot carry. The correlation mechanism above is unaffected by that; only the payload record
changes.
