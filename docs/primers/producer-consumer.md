# Primer: producer–consumer as a pattern

*Stage 2 primer from `Architecture_migration.md`. Written against `uk.co.jackoftrades.channel` and the
`StatusDisplay` → `UiLoop` path that stage 2 builds.*

`blocking-queue.md` covered the mechanism — what `take()` does when the queue is empty. This one covers the *shape*:
what you get
when you arrange two threads as a producer and a consumer rather than as two threads that call each other.

## The pattern in one paragraph

One thread makes work items. Another thread does something with them. Between them sits a queue, and **neither thread
holds a reference to the other**. The producer's whole vocabulary is "here is an item"; the consumer's is
"give me the next item". Nothing else crosses. That is the entire pattern, and every property below falls out of that
one restriction.

```
   core thread                    queue                    UI thread
  ┌──────────┐                 ┌───┬───┬───┐             ┌──────────┐
  │ produce  │ ──── send ────▶ │ m │ m │ m │ ─── take ──▶│ consume  │
  └──────────┘                 └───┴───┴───┘             └──────────┘
      knows: the Sender             ▲                      knows: the Receiver
      knows nothing of the UI       │                      knows nothing of the core
                               the only shared thing
```

## What the queue buys you

**Decoupling in time.** A direct call is a rendezvous: the caller stands still until the callee returns. A queue is a
handover: the producer resumes the instant the item is on the queue. `GameConstants.init()` can report "loading
object.txt" and immediately start loading it, without waiting for a font to be measured and a panel to be repainted.
Under the old design that call *was* the painting.

**Decoupling in identity.** The producer names a `Sender<CoreMessage>`, not a `SplashScreen`. Swap in a different
consumer, or three consumers, or a test double that records everything, and the producer's code does not change and does
not know. This is exactly why stage 2 is small: `StatusDisplayHolder` already lets you swap the implementation, so
replacing `SplashScreen` with a `ChannelStatusDisplay` is a one-line change at the registration site.

**Decoupling in rate.** The producer and consumer run at whatever speed each can manage. A burst of two hundred progress
notes during data loading does not slow the core down to the display's frame rate; the notes pile up and drain. (An
unbounded queue means the pile has no ceiling — see "Back-pressure" below.)

**A serialisation point you didn't have to write.** Items come off the queue one at a time, in order, on one thread. The
consumer body therefore needs no locks over anything only it touches — not because you were careful, but because there
is only ever one thread in there. Most of the thread-safety you *don't* have to think about in this design is this
property, quietly doing its job.

## The consumer loop, and its four decisions

The consumer is always the same shape, and each line of it is a decision worth being able to defend:

```java
public void run() {
    try {
        boolean running = true;
        while (running) {
            ChannelMessage message = receiver.receive();   // blocks; costs nothing while idle
            switch (message) {                             // exhaustive over the sealed type
                case CoreMessage.LifecycleCoreMessage(var event)
                        when event == CoreLifecycleEvent.STOPPED -> running = false;
                case CoreMessage core -> handle(core);
                case UIMessage ui -> handle(ui);
            }
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();                // restore the flag, then leave
    }
}
```

1. **`while` around a blocking receive**, never a poll-and-sleep. The loop spends its life parked.
2. **`switch` over the sealed hierarchy**, so the compiler enforces that every message shape is handled — the point of
   the sealed-records protocol, cashed in here. A new record on the protocol becomes a compile error in the consumer,
   which is where you want
   to find out.
3. **A sentinel ends the loop**, not an interrupt and not a `stop` flag. `Lifecycle(STOPPED)` arrives *in order*, after
   every message sent before it — so the last progress note is painted before the window closes. A flag checked at the
   top of the loop has no such ordering, which is how you get a shutdown that eats the final frame. (This is the
   poison-pill pattern; `thread-lifecycle-and-shutdown.md` does it properly.)
4. **Interruption exits the loop.** It does not go back round and call `receive()` again — being interrupted is a
   request to stop waiting, and continuing to wait is the one response that is definitely wrong.

## The producer side is deliberately dull

```java
public void splashScreenNote(String message) {
    sender.send(new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, message));
}
```

That is the whole of a producer method, and it should stay that whole. Two temptations to name and refuse:

- **Waiting for a reply.** "Send, then block until the consumer acknowledges" reintroduces the rendezvous and, with it,
  the possibility of deadlock — the two halves can end up each blocked on the other's queue. If a producer needs an
  answer, that answer is a *message coming back*, handled by that thread's own loop, not a return value.
- **Sending a mutable object.** The handover guarantee (`blocking-queue.md`) covers everything written *before* the
  send. It does
  not cover what you do to the object *after*. Records with immutable components make that a non-question, which is why
  the protocol is records all the way down, and why `EventDataMissile` carrying a live `ItemObject` is called out in the
  migration doc as a shape that has to flatten before it can cross.

## Back-pressure, and why there is none here

A bounded queue makes the producer block when the consumer falls behind. That is *back-pressure*: the slow end throttles
the fast end. It's the right answer when a runaway producer would exhaust memory.

These channels are unbounded, so there is no back-pressure at all, and that is a considered choice: the core must never
be made to wait on the display, and the traffic is one message per data file or per player action rather than per pixel.
The failure mode you've accepted is unbounded growth if a consumer dies while a producer keeps going — which stage 3's
shutdown handshake exists to make orderly. If that ever changes, `blocking-queue.md`'s note applies: bounding the queue
and
revisiting `Sender.send`'s use of `offer` are the same commit.

## Why this is the shape the port needs

C's front end is *called*: `ui-display.c` registers a function pointer and the core invokes it, on the core's stack,
mid-loop. Everything the UI does happens inside the core's call. That works because C has one thread.

Java hands us two threads whether we want them or not — the EDT exists as soon as a window does. Given two threads,
"call the other half directly" means every call site is a threading question, forever. Producer–consumer answers the
threading question **once, at one place**, and the rest of the code goes back to being ordinary single-threaded code on
both sides. That is the trade the migration is buying: one hard boundary instead of hundreds of soft ones.

The consumer in this port also has a second job that the plain pattern doesn't mention: it takes work off the queue and
then hands the painting part to a *third* thread. That is `edt-and-invokelater.md`, and the reason for it is that
Swing's thread is not one we own.

## Where to look in the code

| Concept                                      | Where                                                                                        |
|----------------------------------------------|----------------------------------------------------------------------------------------------|
| The queue, and the only place both ends meet | `Channels.create()`                                                                          |
| Producer's view — send and forget            | `Sender`, `CoreSender`; stage 2's `ChannelStatusDisplay`                                     |
| Consumer's view — block and handle           | `Receiver`, `UIReceiver`; stage 2's `UiLoop`                                                 |
| The protocol the `switch` is exhaustive over | `ChannelMessage`, `CoreMessage`, `UIMessage`                                                 |
| The rendezvous being removed                 | `SplashScreen`'s Javadoc — "called on the game thread, and currently touches Swing directly" |

*See also: the sealed-messages primer (unwritten), `blocking-queue.md`, `edt-and-invokelater.md`,
`thread-lifecycle-and-shutdown.md`.*