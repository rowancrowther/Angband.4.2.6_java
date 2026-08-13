### How does Angband run in Java ###

*Written 2026-08-13, at the end of stage 4 of `Architecture_migration.md`. This replaces the startup narrative in
`Old_java_map.md`, which is kept as the record of how the program ran before the two-channel migration — everything from
its step 5 onwards describes a program that no longer exists.*

It assumes that it can be launched from a terminal.

1. Main sets some basic options, to be overwritten depending on incoming parameters.
2. Main sorts through the incoming parameters, and processes them, displaying for the -l (list characters) and default
   (show usage) in windows which occur prior to there being a core thread or a UI thread.
3. It should be noted that the incoming parameter '-d' (change one of the directories) can cause errors, which are
   logged and then sent to System.err, followed by a System.exit (1). This is the only System.exit left in the program,
   and it is only reachable before either thread exists.
4. Main builds the startup options, as a local rather than a field: both halves are handed their own reference, so
   nothing needs to be published across threads afterwards.
5. Main creates the channels (Channels.create ()). This is the only place the two queues come into existence, and so the
   only way a sender and its matching receiver can be guaranteed to be looking at the same one. Three views are handed
   out from those two queues:
    1. uiChannel — the UI thread's pair: the inbox it receives on, and the sender it reaches the core with.
    2. coreChannel — the core's pair: the inbox it receives on, and the sender it reaches the UI with.
    3. edtChannel — a send-only view for the event dispatch thread, which writes to the UI thread's inbox. Send-only on
       purpose: a listener holding a receiver could park the EDT, and there is no method here to do it with.
6. Main creates the two threads, angband-ui and angband-core, wrapping a Runnable around each half. Neither half is
   constructed by the other; Main is the only class that names both.
7. Main installs an uncaught exception handler on the core thread — before starting it, since a handler installed
   afterwards can be missed by a thread that dies immediately. The handler sends the "stopped" the dying thread did not
   live to send, so a crash leaves by the shutdown path that already exists. The UI half's equivalent is a try/catch
   inside its own body (step 8), because the front end it must shut down is not visible from here.
8. Main starts both threads, and then joins both. Main outlives the whole session and does no game work itself. Waiting
   is what makes "the core finished before the process did" a guarantee rather than a hope, and it is why nothing on
   either half calls System.exit: the program ends by running out of threads.

#### The UI thread (angband-ui) ####

9. The UI body creates the SwingUI, which creates a new ArrayList for windows, creates a new main window and adds it to
   the list, then sets activeWindow to be the main window. It also builds the UILoop it will later become. It holds no
   handle on the core, and has no way to obtain one.
10. The UI body queues SwingUI.init on the EDT (SwingUtilities.invokeLater) and does not wait for it.
11. The EDT initialises the activeWindow.
    1. It sets the colour system, chooses the font (TerminalVector if present, else the platform monospace) and sizes
       the window as 80x24 cells of that font's 'M'.
    2. It sets the window to DO_NOTHING_ON_CLOSE, sets the title, registers the window listener, adds a main panel,
       packs and shows the window.
    3. On its last line it sends "start" to the core. The display is up, so the core may begin; nothing enforces that
       ordering and nothing needs to, since the channel buffers.
12. Meanwhile the UI body calls startLoop, and the thread spends the rest of the session inside UILoop.loop, blocked on
    its inbox. Two writers reach that inbox — the core, and the EDT — so it is the one place where a raw AWT event
    becomes a message the core understands.
    1. On EVENT_ENTER_INIT it reads and parses lib/screens/news.txt into a SplashScreen and paints the title screen.
    2. On EVENT_INITSTATUS it paints a progress note onto that same grid.
    3. On WindowCloseRequested it sends "saveAndStop" to the core. This is the whole of the translation the EDT was kept
       from doing itself.
    4. On "stopped" it queues the window disposal onto the EDT and returns, ending the thread.
    5. A UIMessage that is not a close request means the wiring is wrong, so it throws rather than being dropped
       silently.
    6. The painting is not done here. Window.display and SplashScreen's methods each do their own invokeLater, so the
       hop onto the EDT is a property of the painting call rather than something this loop has to remember.
13. If anything the loop throws escapes, the UI body catches it, sends "saveAndStop" so the core is not left waiting for
    a front end that no longer exists, and disposes the windows in a finally — which is also what disposes them on the
    ordinary path. Disposing twice is harmless; not disposing at all leaves the EDT holding a dead program open.

#### The core thread (angband-core) ####

14. The core body creates the Core with its channel ends and the startup options, and calls gameLoop. Everything the
    core needs is set up inside that one method, so there is no ordering for a caller to get wrong.
15. Core.gameLoop sets the gameEngine to the sole instance of the GameEngine. Building the engine replaces the event
    bus, which is why it has to happen before anything registers on it.
16. It then puts a ChannelStatusDisplay into the StatusDisplayHolder. The core still reports its start-up progress
    through that boundary, but what the boundary now does is put a message on the channel — the front end is no longer
    on the other side of it. (Stage 5 retires the holder and lets the handlers reach the channel directly.)
17. It then initialises the 6 handlers (EVENT_ENTER_INIT, EVENT_LEAVE_INIT, EVENT_ENTER_GAME, EVENT_LEAVE_GAME,
    EVENT_ENTER_WORLD, EVENT_LEAVE_WORLD) and then the two BirthStateHandlers (EVENT_ENTER_BIRTH & EVENT_LEAVE_BIRTH).
    These have to be initialised before the gameEngine loads, as the first thing the load does is call
    GameConstants.init, which signals EVENT_ENTER_INIT.
    1. At the moment only EVENT_ENTER_INIT and EVENT_INITSTATUS are live, the others exist for when the port is expanded
       to cover those events. The handlers are plugged in now.
18. The core thread then runs gameEngine.loadGameConstants (), which runs:
    1. GameConstants.init () — initialising the game constants and ~40 data arrays in the registry. One datafile,
       lib/user/lore.txt, isn't loaded yet as it needs the game save system to be able to be parsed correctly.
    2. It then creates a player and its sub parts (currently a stub creation).
    3. It then creates a stub cave, to give the game somewhere to stand until level creation is ported — needs to be
       replaced.
    4. It then runs the dungeon profile loader, the room template loader and the vault template loader.
    5. It then sets the commandQueue up, passing in the main player, to receive commands and send them through to itself
       from the UI thread.
19. The core thread then blocks on its own inbox. It is parked, not spinning: an idle game costs no CPU, and there is no
    longer any way to reach the core except by sending it a message.
    1. On "start" it logs and carries on. Character birth lands here in Chapter 3.
    2. On "saveAndStop" it sends "stopped" and returns, in that order, so the reply is on the queue before the thread
       stops existing. There is nothing to save yet; when there is, it goes between those two lines and the guarantee is
       already in place for it.
    3. A WindowCloseRequested is ignored — the EDT posts those to the UI thread's inbox, not to this one.

#### Shutting down ####

20. The player closes the window. The listener runs on the EDT and does one thing: it puts a WindowCloseRequested on the
    UI thread's inbox. It decides nothing, because every interesting answer involves waiting for the core, and a
    listener that waits is a frozen window.
21. The UI thread turns that into "saveAndStop" and sends it to the core.
22. The core stops its loop, sends "stopped", and its thread ends.
23. The UI thread receives "stopped", queues the disposal of every window onto the EDT, and its thread ends.
24. With no displayable window left, the EDT ends on its own. Main's two joins return, main returns, and the JVM exits
    because nothing non-daemon is left. Exit code 0, with no System.exit anywhere on the path.

### How does it differ from the C code ###

#### The code differs in the following ways ####

The java code splits the game into two threads (three, counting Swing's event dispatch thread), forcing a clear
differentiation between the core and the UI. The C has the split, which it differentiates by prefixing the files that
deal with the UI with ui (i.e. ui-display.c), but there is no thread other than the main one running at any time. C's
split between core and ui is purely a naming convention. Java's is enforced. You can check the Java's UI/Core boundary
isn't being breached by reading the import statements on a UI class — and from stage 5 a test does that walk for you.

In C the main control of the game loop continues in a while (1) loop, that sits inside play_game () for the entire game.
In Java, main starts both halves and then blocks in join for the whole session; the JVM stays up because those threads
are running, and ends when they finish. The window no longer keeps it alive by accident — it keeps the EDT alive
deliberately, and disposing it is a step of the shutdown rather than a side effect of exiting.

The Event Bus is created and used entirely on the core thread. Its initialisation order — engine first, then handlers,
then the load — is still important, and is documented both in the code and in step 17 above.

In C every event_add_handler is registered in a UI file, and called in a core file. Core signals, UI registers. In Java,
every eventAddHandler is registered and called in a core file, and what the handler does is put a message on the core's
channel. The call still goes core → UI, but it goes as data rather than as a method call, and the UI half decides what
it means. (Stage 5 splits the handlers so the deciding half lives UI-side, which is C's own arrangement.)

Dependency wise, neither half now points at the other. Both depend on the channel package — the transport plus the
shared vocabulary — and on nothing else of each other's. You can delete the UI and the core would continue to run,
sending into a queue nobody drains; you can replace it wholesale without the core noticing, which is the point.

It should be noted that this is the same way that the big_map ends, showing that these two documents are compatible.

#### What is not here yet ####

- **Birth.** The core logs "start" and does nothing with it. C's interactive birth needs the core to ask the UI
  questions and wait for answers, which is a request/response protocol over the two channels — Chapter 3's design set
  piece, along with the input boundaries (CommandGetterHolder, GameInputHolder) that are still holder-shaped and still
  uncalled.
- **The game loop proper.** Step 19 handles two lifecycle messages and nothing else, because nothing else is sent yet.
  Keystrokes, commands and map redraws arrive in Chapter 5, as further arms of the same switch.
- **Saving.** "saveAndStop" is payload-free and saves nothing. Chapter 8 gives it something to do, and the shutdown
  ordering it needs is already in place.
- **The startup options.** Both halves are handed them; neither reads them yet.
