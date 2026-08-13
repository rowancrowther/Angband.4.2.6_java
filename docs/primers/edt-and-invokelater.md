# Primer: the EDT and `invokeLater`

*A primer from the menu in `Architecture_migration.md`. Written at stage 2, against `SwingUI`,
`SplashScreen` and the `UiLoop` that stage 2 introduces.*

Swing has one thread that is allowed to touch components, and it is not a thread you created. Almost every Swing bug
that "only happens sometimes" is a violation of that one rule, in one of its two directions: touching Swing from another
thread, or blocking Swing's thread with work that isn't Swing's.

## What the EDT actually is

The event dispatch thread is a consumer loop — the same shape as `producer-consumer.md`'s, written by someone else in

1998. Its queue
is `java.awt.EventQueue`, and its items are things like "the mouse moved", "this component needs repainting",
"run this `Runnable`". It takes one, runs it to completion, takes the next.

That is the whole model, and two consequences follow immediately:

- **Everything on the EDT is serialised.** Component state is unsynchronised — no locks, no `volatile` — and is safe
  only because exactly one thread ever reads or writes it. The safety is a convention enforced by discipline, not by the
  type system, which is why the compiler will happily let you break it.
- **Anything slow on the EDT stops everything.** While your code runs, no repaint happens, no keystroke is delivered, no
  button un-presses. The application is frozen for exactly as long as you are in there, because the loop that would
  service those events is inside your call.

The EDT starts lazily, the first time something needs it — realistically, when `SwingUI`'s first `Window` becomes
displayable. It is non-daemon, which is why the JVM stays alive after `main` returns and why disposing every window is
what actually ends the process (`thread-lifecycle-and-shutdown.md`'s territory).

## Direction 1: painting must hop

Every Swing method that reads or writes component state must be called on the EDT. `Window.clear()`, laying the frame
out again, setting the character grid on a panel — all of it.

`SplashScreen`'s own Javadoc already confesses the problem:

> **Called on the game thread, and currently touches Swing directly.** The signal is raised from inside
> `GameConstants.init()`, which runs on the game thread … Only `repaint()` is documented as thread-safe.

That confession is worth reading closely, because it names the trap. `repaint()` *is* thread-safe — it just posts a
paint request to the event queue. So the call that looks most dangerous is the one call that's fine, and the
innocent-looking `setChars` beside it is the actual violation. You cannot spot these by which ones look scary.

What goes wrong when you ignore it is not an exception. It's a layout computed from half-updated state, a panel that
paints the previous frame's characters, a screen that is right nine runs in ten and wrong on the tenth — and wrong
differently on someone else's machine. There is no `IllegalStateException` to catch. (Java has no enforcement here;
JavaFX added `IllegalStateException: Not on FX application thread` precisely because Swing's silence proved so
expensive.)

The fix is to *hop*: package the work as a `Runnable` and post it to the event queue.

```java
SwingUtilities.invokeLater(() ->{
        activeWindow.

clear();
    mainPanel.

setChars(display);
    mainPanel.

repaint();
});
```

`invokeLater` returns immediately. The lambda runs later, on the EDT, in order relative to other posted work.

## `invokeLater` vs `invokeAndWait`

```java
SwingUtilities.invokeLater(runnable);      // post it, return now
SwingUtilities.invokeAndWait(runnable);    // post it, block until the EDT has run it
```

**Use `invokeLater`. Effectively always, in this port.**

`invokeAndWait` blocks the calling thread until the EDT finishes the task, which reintroduces the rendezvous the whole
channel design exists to remove — the core would once again run at the display's speed. It throws
`InterruptedException` and `InvocationTargetException`, so it litters call sites with handling. And it deadlocks if you
call it *from* the EDT (`Error: Cannot call invokeAndWait from the event dispatcher thread`), or if the EDT is meanwhile
waiting on something the caller holds.

The only honest use is "I need a value back from a component before I can continue", and the better answer to that is
usually to not need it — keep the state you need on your own side, and let the EDT be a place you write *to*.

`SwingUtilities.invokeLater` and `EventQueue.invokeLater` are the same method; `SwingUtilities` just forwards.
`SwingUtilities.isEventDispatchThread()` is the assertion to reach for when you want a test or a guard to say so out
loud.

## Direction 2: `take()` must not run on the EDT

The mirror-image error, and the one that will hang this application if it gets made.

`Receiver.receive()` is `queue.take()`. It blocks until a message arrives. Put that call on the EDT and:

1. The EDT is now inside your code and cannot service its queue.
2. So no repaints happen. No keystrokes are delivered. The window stops responding — on most platforms the OS greys it
   and offers to kill it.
3. And the message that would release it never helps, because even when `take()` returns and you paint, you are about to
   loop round and block again. Every `invokeLater` anyone posted, including your own painting, is stuck behind you in a
   queue that is not being drained.

The freeze is total and permanent. It's not a slow UI; it's a dead one.

This is why the design has **three** threads and not two:

| Thread         | Job                                      | Blocks on              |
|----------------|------------------------------------------|------------------------|
| `angband-core` | plays the game, produces `CoreMessage`s  | its own `receive()`    |
| `angband-ui`   | consumes messages, decides what to paint | `uiReceiver.receive()` |
| EDT            | paints, and nothing else                 | its own event queue    |

The UI thread exists precisely so that *something* can block on `take()`. It is the buffer between a channel that blocks
and a thread that must never block. Its loop body ends in `invokeLater` — it does the deciding, and hands the EDT the
doing:

```java
while(running){
ChannelMessage message = uiReceiver.receive();          // blocks — fine, not the EDT
    switch(message){
        case CoreMessage.

TextCoreMessage(var type, var text) ->
        SwingUtilities.

invokeLater(() ->splashScreen.

splashScreenNote(text));
        // …
        }
        }
```

Note what does *not* happen there: the UI thread does not wait for the painting to finish. It goes straight back to
`receive()`. Two hundred progress notes during data loading become two hundred posted runnables, coalescing naturally in
the paint pipeline, with the core never once slowed down.

## Traffic in the other direction: the EDT as a producer

The EDT is also where AWT events are born — `windowClosing` runs there. Stage 3 has it do exactly one thing: put a
`WindowCloseRequested` message on a channel and return.

That is the same rule in producer clothing. `send()` on an unbounded queue never blocks, so it is safe to do on the EDT;
anything *else* in that listener — saving a game, disposing windows, joining a thread — is not. The listener's job is to
translate an AWT event into a message and get off the EDT's back immediately. The decision about what to do lives on the
UI thread, which is allowed to take its time.

`UIChannel`'s Javadoc already flags the wrinkle: the UI half is really two senders, the UI thread and the EDT, and the
record does not tell them apart. Worth knowing while writing stage 3 — the containment for now is that the EDT never
gets handed a whole `UIChannel`.

## The rules, condensed

1. Touch a Swing component **only** on the EDT. `repaint()` is the exception; assume nothing else is.
2. Do **nothing** slow or blocking on the EDT. No `take()`, no file I/O, no `join()`, no `sleep`, no
   `invokeAndWait`.
3. `invokeLater` to get *onto* the EDT. Prefer it to `invokeAndWait` unless you can say precisely why not.
4. Post work, don't await it. If you need something back, make it a message on a channel.
5. Capture what the lambda needs as immutable locals — a lambda that reads a field it doesn't own is reading it on the
   wrong thread, at an unpredictable time.
6. Build the initial UI on the EDT too. Constructing a `JFrame` is component state like any other; the bootstrap belongs
   inside an `invokeLater` from the UI thread (stage 4's task).

## Where to look in the code

| Concept                                     | Where                                                             |
|---------------------------------------------|-------------------------------------------------------------------|
| The confession this primer answers          | `SplashScreen`'s class Javadoc                                    |
| The threading note on the boundary          | `StatusDisplay`'s Javadoc — "a Swing implementation … has to hop" |
| Swing bootstrap that will move onto the EDT | `Frontend.init`                                                   |
| The EDT as a producer, doing one thing only | `SwingUI`'s `windowListener` (stage 3)                            |
| The thread that blocks so the EDT doesn't   | stage 2's `UiLoop`                                                |

*See also: `producer-consumer.md`, `blocking-queue.md`, `thread-lifecycle-and-shutdown.md` (why disposing windows ends
the JVM).*