# Primer: `BlockingQueue` mechanics

*Primer 3 from the menu in `Architecture_migration.md`. Written at stage 1, tied to
`uk.co.jackoftrades.channel`.*

A `BlockingQueue<E>` is a queue that knows how to make a thread wait. That is the whole idea. Everything else — the four
method families, the bounded/unbounded distinction — follows from one question: **what should happen when you can't do
the thing right now?**

There are only two ways to be stuck. You want to take from a queue that is empty, or you want to put into a queue that
is full. For each, the JDK offers four answers, and the method you pick *is* the answer.

## The four families

| Answer          | Insert              | Remove          | On failure                                         |
|-----------------|---------------------|-----------------|----------------------------------------------------|
| Wait forever    | `put(e)`            | `take()`        | never fails; blocks until it can proceed           |
| Give up at once | `offer(e)`          | `poll()`        | returns `false` / returns `null`                   |
| Wait a while    | `offer(e, t, unit)` | `poll(t, unit)` | returns `false` / `null` after the timeout         |
| Throw           | `add(e)`            | `remove()`      | `IllegalStateException` / `NoSuchElementException` |

Read the columns rather than memorising the names: `put`/`take` are the blocking pair, `offer`/`poll` the non-blocking
pair, and `add`/`remove` are the inherited `Collection` methods that throw — the ones you almost never want here,
because "the queue was full" is not exceptional.

The trap is `poll()` returning `null` when empty. It looks like a message that didn't arrive, and it is very easy to
write a loop that treats `null` as "nothing yet" and spins at 100% CPU asking again. That loop is exactly what
`take()` exists to replace.

## Why blocking is the feature

The instinct is that a blocked thread is a stuck thread. It isn't. A thread inside `take()` is *descheduled* — it
consumes no CPU, and the OS wakes it when something arrives. Compare the alternatives for a UI thread waiting on the
core:

- **Poll in a loop**: burns a core doing nothing, and still adds latency equal to your sleep interval.
- **Callbacks**: the core now runs your UI code on the core's thread, which is the coupling the whole migration exists
  to remove.
- **`take()`**: the thread sleeps until there is work, then runs it on its own thread.

So in this port, the UI thread spends nearly all of its life parked in `UIReceiver.receive()`, which is
`queue.take()`. While the player is thinking, that thread costs nothing. That is why `Receiver` has exactly one method
and why it blocks.

## What this codebase chose, and why

Both queues are `LinkedBlockingQueue` created with **no capacity argument**, which means unbounded — effectively
`Integer.MAX_VALUE`. That single decision explains the asymmetry in the two interfaces:

- `Receiver.receive()` → `take()`. Blocking is wanted, and `take` declares `InterruptedException`, so `receive`
  does too.
- `Sender.send()` → `offer()`. On an unbounded queue a put can never wait, so there is nothing to block on and nothing
  to interrupt. `offer` returns `boolean`, always `true` here, and discarding it is honest. `send` therefore declares no
  checked exception, which matters because sending happens at hundreds of call sites and `put` would have forced a `try`
  around every one.

If the queues ever become bounded — the realistic reason would be back-pressure, stopping the core outrunning the
display during level generation — that reasoning inverts. `offer` would start returning `false` and silently dropping
game events, which is the worst kind of bug: no exception, just a display that is subtly wrong. Bounding the queues
means revisiting `Sender.send` in the same commit, not afterwards.

## Interruption

`take()` throws `InterruptedException`. This is not an error condition; it is how you ask a parked thread to stop
waiting. Someone calls `thread.interrupt()`, the JVM wakes the thread inside `take()`, and the exception is how it finds
out.

Two rules, both violated constantly in the wild:

1. **Don't swallow it.** `catch (InterruptedException e) {}` turns a shutdown request into a thread that keeps waiting
   forever. If you catch it and can't act, restore the flag with `Thread.currentThread().interrupt()` so the next
   blocking call sees it.
2. **Catching it means you were asked to stop.** The right response is usually to finish up and return from your loop,
   not to go back around and call `take()` again.

In this port the interrupt path is tested directly — `ChannelsTest.interruptingABlockedReceiverEndsTheWait` — for
precisely this reason: it is behaviour that is easy to lose in a refactor and impossible to notice by playing the game.

## The guarantee you get for free

This is the part that is easy to miss, and it is the reason a queue is a better answer than a shared field with a lock
around it.

`BlockingQueue` establishes a **happens-before** relationship: everything a thread did *before* it put an element in is
guaranteed visible to the thread that takes that element out. Without such an edge, the Java memory model allows one
thread's writes to be invisible to another indefinitely — not merely late, but never arriving, because the compiler and
CPU are free to keep values in registers and reorder stores.

So when the core builds a `TextCoreMessage` and sends it, the UI thread is guaranteed to see the fully-constructed
record, not a half-initialised one. You get that without writing a single `synchronized` or `volatile`. It is also why
the records being immutable matters: the guarantee covers the handover, and immutability means there is no *second*
handover to worry about afterwards.

## Pitfalls worth naming

- **Never call `take()` on the EDT.** It freezes the entire UI, including repaints, and no message will ever arrive to
  unfreeze it because the thread that would deliver it can't get scheduled work through a frozen event queue. This is
  primer 4's territory, but it is the single most likely way to hang this application.
- **`size()` is a snapshot and a lie.** By the time you act on it, another thread may have changed it. Don't branch on
  it; there is no correct "if not empty then take" — that's what `poll()` is for.
- **Unbounded means unbounded.** A producer that never stops and a consumer that never runs is an
  `OutOfMemoryError`, arriving slowly.
- **Iterating a queue is not draining it.** `for (E e : queue)` is a weakly-consistent view and removes nothing. Use
  `drainTo` if you actually want the contents.

## Where to look in the code

| Concept                                     | Where                                    |
|---------------------------------------------|------------------------------------------|
| The two queues, created in one place        | `Channels.create()`                      |
| `take()`, and why the exception escapes     | `Receiver`, `UIReceiver`, `CoreReceiver` |
| `offer()`, and why the `boolean` is dropped | `Sender`, `CoreSender`, `UISender`       |
| Blocking, waking and interruption, proved   | `ChannelsTest.AcrossTwoThreads`          |