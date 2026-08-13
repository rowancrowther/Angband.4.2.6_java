# Primer: thread lifecycle and shutdown

*A primer from the menu in `Architecture_migration.md`. Written at the end of stage 4, against `Main`, `Core`,
`UILoop` and the handshake stage 3 built.*

Every other primer is about how the two halves talk while the game is running. This one is about how the program
*stops* — which turns out to be the same question as "what keeps a JVM alive?", asked backwards.

The reason it deserves a page: the port used to end with `System.exit(0)`, one line, obviously correct-looking. It was
hiding a race. What replaced it is four messages and two `join`s, and every part of that is load-bearing.

## 1. What keeps a JVM alive

**The JVM exits when its last non-daemon thread finishes.** Not when `main()` returns — that is the single most common
misreading. `main` is just a thread like any other; it happens to be the one the JVM started for you.

```java
Thread t = new Thread(body, "worker");
t.setDaemon(true);    // "don't count me" - must be set BEFORE start()
t.start();
```

A daemon thread is one the JVM is willing to abandon mid-stride. Everything else is a reason the process is still
running. From the thread dump taken while the game sat at the title screen on 2026-08-13, the non-daemon threads were
exactly five:

```
"main"              in Object.wait()   ← Thread.join, Main.main:226
"angband-ui"        parked             ← LinkedBlockingQueue.take, UILoop.loop:190
"angband-core"      parked             ← LinkedBlockingQueue.take, Core.gameLoop:215
"AWT-EventQueue-0"  parked             ← Swing's own loop
"AWT-Shutdown"      parked             ← AWT's own bookkeeping
```

(Everything else in the dump — GC threads, JIT compiler threads — is VM-internal and does not count.)

So the program ends when all five agree to end, and the design's job is to make that happen in the right order.

**The EDT is the awkward one, because you did not start it and cannot join it.** AWT starts it on demand — the first
time anything realises a window — and keeps it alive while any displayable window exists. Dispose the last window and
AWT lets the EDT die on its own. That is why `SwingUI.closeDown()` is the *last* step of the handshake and why it needs
no `exit` after it: disposing the windows is how you release the thread you were never handed.

## 2. A thread's life, in four states

```java
Thread t = new Thread(runnable, "angband-core");   // NEW      - an object, nothing more
t.start();                                          // RUNNABLE - the OS now has a thread
                                                    // ...runnable.run() executes here...
                                                    // TERMINATED when run() returns or throws
t.join();                                           // the caller waits for TERMINATED
```

Three traps worth naming once:

- **`start()` versus `run()`.** Calling `run()` directly is not an error and does not start anything — it just invokes a
  method on the calling thread. The compiler will not save you. `Main` never does this; tests sometimes legitimately do,
  to run a loop body inline.
- **`setDaemon` after `start()` throws.** Daemon status is fixed at start.
- **A thread cannot be restarted.** `TERMINATED` is final; `start()` on a finished thread throws
  `IllegalThreadStateException`. "Restart the core" means "build a new `Core` and a new `Thread`".

**Starting a thread publishes what you wrote before it.** Everything the starting thread did before `start()` is visible
to the new thread — a happens-before edge you get for free. It is why `Core`'s fields are not `volatile`
despite being written on `main`'s thread and read on `angband-core`, and why `Main` no longer needs the essay the static
`StartupOptions` field used to carry: passing a reference to a thread before starting it is publication.

## 3. `join`, and why "main waits" is a guarantee

```java
uiThread.join();      // returns only once uiThread is TERMINATED
coreThread.join();
```

`join()` is `wait()` on the thread object, woken by the JVM when the thread dies. It gives you two things:

1. **Ordering.** Everything the joined thread did happens-before `join()` returns. Whatever the core wrote before ending
   is visible to `main` afterwards, with no further synchronisation.
2. **A place to stand.** `main` outliving both halves is what makes "the save finished before the process did" a
   *guarantee* rather than a hope.

That second point is the whole of `Architecture.md`'s step 4, and it is worth seeing why the old shape could not provide
it. `closeDown()` used to call `System.exit(0)` from the EDT while the game thread was mid-stride:

```
  EDT: requestStop() ──▶ (game thread wakes... eventually)
  EDT: System.exit(0) ──▶ JVM begins shutdown  ✗ game thread killed wherever it was
```

`System.exit` does not ask. It runs shutdown hooks and halts; every other thread stops between instructions. With
nothing to save this was invisible — the race existed but had no stakes. Chapter 8 gives it stakes: a core half-way
through writing a savefile is a corrupt savefile. The bug was going to arrive years after the line that caused it was
written, which is the kind this design is meant to prevent structurally.

The order of the two `join`s does not matter. Neither thread can finish before the other has played its part in the
handshake, so whichever is waited on first, the other has already ended or is about to.

## 4. The poison pill

**A sentinel message that means "no more messages".** It travels the same queue as the ordinary traffic, and that is the
entire point.

```java
case SAVE_AND_STOP -> {
    coreChannel.coreSender()
            .send(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
    return;                       // reply first, then leave
}
```

Compare it with the obvious alternative, a flag:

```java
private volatile boolean running = true;      // ✗ don't
while (running) { handle(receiver.receive()); }
```

The flag is wrong in two ways at once. It **has no position in the stream**: setting it from another thread says
"stop soon", and the consumer may abandon three messages still on the queue — the last progress note vanishes, the final
frame is eaten. And it **cannot wake a blocked thread**: the loop is parked in `receive()` and will not look at the flag
until something else arrives, which may be never.

A sentinel has neither problem. It is *in* the queue, so everything sent before it is delivered first, and its arrival
is itself the wake-up. Ordering and liveness, from the property the queue already had.

The port's handshake is four messages, and each hop crosses a thread boundary:

```
   EDT            UI thread                core thread
    │                 │                         │
    │ WindowCloseRequested ──▶ (core channel)   │       1. the EDT states a fact
    │                 │                         │
    │                 │ ── SAVE_AND_STOP ─────▶ │       2. the UI thread translates it
    │                 │                         │
    │                 │ ◀──── STOPPED ───────── │       3. the core replies, then returns
    │                 │                         ✝
    │ ◀── invokeLater(closeDown) ──             │       4. windows disposed; loop returns
    ✝                 ✝                                     ...and then main's joins return
```

Note step 1. The EDT never decides anything, because every interesting answer involves waiting for the core, and a
listener that waits is a frozen window. It states what happened; the UI thread, which is allowed to block, runs the
exchange.

## 5. Interruption is not the shutdown mechanism

`Thread.interrupt()` sets a flag and makes blocking calls throw `InterruptedException`. It is the JDK's generic
"stop waiting" signal, and it *would* work as a shutdown: stage 3 deleted exactly that arrangement.

It was dropped because it cannot carry the reply. An interrupt has no payload and no ordering — it arrives *now*, in the
middle of whatever the thread was doing, and the interrupted thread cannot distinguish "please save and stop" from
"something has gone wrong". The handshake needs to say which, and needs the answer to come back. So in this codebase an
`InterruptedException` means something unexpected, and both loops log it and give up rather than treating it as a
request. `Core.gameLoop`'s Javadoc records the consequence honestly: it gives up *without*
sending `STOPPED`, so the UI would wait forever. That is a real gap, and the reason nothing interrupts these threads.

If you ever do catch an interrupt somewhere it should propagate, restore the flag:

```java
catch (InterruptedException e) {
    Thread.currentThread().interrupt();   // the exception cleared it; put it back
}
```

## 6. The crash path, which the handshake does not cover

Everything above assumes both halves reach their `return` statements. On 2026-08-13 a run started from the wrong working
directory proved what happens when one doesn't: `AngbandDirs.BASE_DIR` is `user.dir`, so the data files were not where
the paths said, and both halves died on exceptions inside their set-up — before either loop was entered.

The result was not a crash. It was a **hang**: `main`'s joins returned promptly (both threads were, after all,
terminated), but the EDT was still holding a realised window that nobody disposed, so the JVM stayed up around a dead
program until it was killed.

An exception that escapes `run()` has nowhere to propagate — the thread that called `start()` moved on long ago and no
stack connects them. So the JVM consults a handler instead:

```java
public class Thread {
    @FunctionalInterface
    public interface UncaughtExceptionHandler {
        void uncaughtException(Thread t, Throwable e);
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh);
    public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler eh);
}
```

It is looked up in three places, in order: the thread's own handler, its thread group's, then the process-wide one from
`Thread.setDefaultUncaughtExceptionHandler`. The stock `ThreadGroup` implementation prints
`Exception in thread "angband-core" …` to `System.err` — the line that appeared in that run was a handler doing its job,
not the exception escaping by accident.

Three properties to hold on to:

- it runs **on the dying thread**, after `run()` has unwound, and `t` is that thread;
- it **cannot resurrect** anything — when it returns, the thread terminates;
- if the handler itself throws, the JVM discards it silently, so log before you do anything that can fail.

Which makes it the natural place to send the message the dead thread never got to send — routing a crash out through the
shutdown path that already exists, rather than building a second one. That is what `Main` now does for the core:

```java
coreThread.setUncaughtExceptionHandler((t, e) -> {
    logger.fatal("{} died.", t.getName(), e);
    channels.coreChannel().coreSender()
            .send(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
});
```

**Install it before `start()`.** A handler set afterwards is a race — a thread that dies immediately dies before its
handler exists, and the default one prints to `System.err` instead. That is the whole reason the `new Thread`
lines and the `start()` lines are separated in `main`.

### The UI half takes the opposite shape, and the reason is scope

A dead UI half owes *two* messages, not one: `SAVE_AND_STOP` to unpark the core, and a disposal to release the EDT. But
the front end it has to dispose is a local inside `startSwingUI`'s lambda, built after `main` handed the work over, so a
handler installed on the thread from `main` would have nothing to call. The catch therefore goes where the object is:

```java
return () -> {
    SwingUI swingUI = new SwingUI(uiChannel, edtChannel, startupOptions);
    try {
        SwingUtilities.invokeLater(swingUI::init);
        swingUI.startLoop();
    } catch (RuntimeException e) {
        logger.fatal("angband-ui died.", e);
        uiChannel.uiSender().send(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP));
    } finally {
        SwingUtilities.invokeLater(swingUI::closeDown);
    }
};
```

Catching it here also means the thread ends by *returning* rather than by throwing, so there is nothing left for a
handler to do. Neither shape is the correct one in general: each is simply where the thing that needs shutting down is
in scope.

Three details in that block worth reading deliberately:

- **Nothing waits for the reply.** The `STOPPED` the core sends back lands on an inbox whose reader has died, and rests
  there unread. That is fine, and it is the unbounded queue paying off: a send that cannot block cannot deadlock a
  thread that is already on its way out.
- **The disposal is in `finally` because it is owed on the clean path too**, where `startLoop()` returns normally after
  `UILoop` has queued a `closeDown` of its own. Disposing twice does nothing; disposing never hangs the program. The
  duplicate is the right way to be wrong.
- **`RuntimeException`, not `Throwable`.** An `Error` still escapes and still hangs. That is a considered line: a JVM
  that has just thrown `OutOfMemoryError` cannot be relied on to send a message or paint a frame.

One question this does *not* settle, and C asks it too: whether a `STOPPED` that follows a crash should look identical
to one that follows a clean quit. Today it does. C keeps the distinction in `quit_aux`, and Chapter 8 — where
`SAVE_AND_STOP` acquires a real save to perform — is when the port will have to have an opinion.

## 7. What C does instead, briefly

C has one thread, so none of this exists. `quit()` (`[C] src/z-util.c`) calls the front end's cleanup hook and then
`exit()`; there is no other thread to be mid-write, because there is no other thread. The port's four messages are not a
translation of anything in C — they are the price of the threads Swing forces on us, and the reason it is a price worth
paying is `producer-consumer.md`'s argument: one hard boundary instead of hundreds of soft ones.

## Where to look in the code

| Concept                                       | Where                                                              |
|-----------------------------------------------|--------------------------------------------------------------------|
| Threads created, named, started and joined    | `Main.main` — `angband-ui`, `angband-core`                         |
| The poison pill, sent                         | `Core.gameLoop`, the `SAVE_AND_STOP` arm                           |
| The poison pill, obeyed                       | `UILoop.loop`, the `STOPPED` arm                                   |
| The EDT's one job in shutdown                 | `SwingUI`'s `windowListener.windowClosing`                         |
| Releasing the thread you cannot join          | `SwingUI.closeDown` — dispose every window, and no `System.exit`   |
| Why `main` outliving both matters             | `Main`'s class Javadoc, "Waiting is the point, not politeness"     |
| The handshake, tested in both orders          | `UILoopTest.TranslatingWindowEvents`, `UILoopTest.EndingOnStopped` |
| The interrupt gap, recorded rather than fixed | `Core.gameLoop`'s Javadoc, last paragraph                          |

*See also: `blocking-queue.md` (what `take()` does while parked), `producer-consumer.md` (the sentinel as one of the
consumer loop's four decisions), `edt-and-invokelater.md`.*
