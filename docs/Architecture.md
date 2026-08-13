## Principles

- [ ] The user interface (UI) and the core should have no direct knowledge of each other.
- [x] The architectural approach here is similar to CAR Hoare's CSP (Communicating Sequential Processes).
   - [x] The two subsystems should communicate solely via exactly two thread-safe queues, each named for **whose inbox
     it is**: `uiQueue`, the UI thread's inbox, carrying core→UI; and `coreQueue`, the core's inbox, carrying UI→core.
     The core should never send on `coreQueue`, and the UI thread should never send on `uiQueue` (however, the EDT does
     send UIMessages on `uiQueue` — see below).
- [x] Each thread is handed exactly one channel, holding that thread's own ends: the core a
  `CoreChannel(CoreReceiver, CoreSender)`, the UI thread a `UIChannel(UIReceiver, UISender)`, and the EDT an
  `EDTChannel(EDTSender)` — a sender and deliberately no receiver, since a listener that blocked would freeze the window
  it was trying to close. So a channel is named for the thread that **owns** it rather than for a direction, and the two
  full ones span both directions. Neither queue is exposed directly; a thread only ever sees its own ends.
- [x] The Core sends CoreMessages through its CoreChannel, onto `uiQueue`, for the UI thread.
- [x] The UI thread sends UIMessages through its UIChannel, onto `coreQueue`, for the core.
- [x] The EDT sends UIMessages through its EDTChannel, onto `uiQueue`, for pickup by the UI thread. It cannot receive
  any messages.
- [x] Only the UI thread receives from `uiQueue`; only the core receives from `coreQueue`; the core sends only
  `CoreMessage`s.

## Sequencing

### Startup

The startup in main ():

1. [x] creates the two queues;
2. [x] Sets up a handler if the CoreThread closes unexpectedly
3. [x] starts the core, handing it its CoreChannel (receiving from `coreQueue`, sending on `uiQueue`) and a copy
   of the startup arguments;
4. [x] starts the user interface, handing it its UIChannel (receiving from `uiQueue`, sending on `coreQueue`) and a copy
   of the startup arguments;
5. [x] waits for both the UI and the core threads to exit;
6. [x] exits.

### Core

1. When its thread starts, the core should:
   1. [x] set up anything it can (for example reading data files);
   2. [x] wait for commands on its CoreChannel.
2. When the core receives a "start" command, it:
   1. [x] Logs the situation (A placeholder for birth state entry).
   2. [ ] prepares to enter the birth state for the player, which will lead to many messages being sent on its
      CoreChannel to the UI;
   3. [ ] receives and process any birth related commands on its CoreChannel;
   4. [ ] finalises the player birth process
   5. [ ] runs the game loop.
3. The game loop is similar to the existing C game loop, repeatedly:
   1. [ ] receiving a message on its CoreChannel;
   2. [ ] processing the message;
   3. [ ] sending zero or more (usually one or more) messages on its CoreChannel to the UI.
4. When the core receives a "saveAndStop" command on its CoreChannel, it:
   1. [x] stops the game loop;
   2. [x] saves the game if necessary;
   3. [x] sends "stopped" on its CoreChannel to the UI;
   4. [x] and the thread exits.

### User interface

1. When its thread starts, the user interface should:
   1. [x] set up anything it can (notably including the UI Angband colour system, and a main window extending a JFrame);
   2. [x] prepare an 80x24 array of AngbandDisplayCharacters to hold the screen text and colours;
   3. [x] register a AWT event handler on the window close java event of the main window;
   4. [x] send "start" on its UIChannel.
2. The user interface then waits on its UIChannel for a) AWT events forwarded by the EDT and also b) items from the
   core. Either or both might happen — both arrive on `uiQueue`, which is why one blocking receive is enough.
   - [x] UILoop handles this
3. Each time the user interface receives a forwarded AWT event or an entry from the core, it processes that. Such
   processing might involve sending items on its UIChannel, or making short requests of AWT / Swing via
   SwingUtilities.invokeLater ().
4. [x] When the user interface receives a window close event, it sends a "saveAndStop" event on its UIChannel.
5. When the user interface receives a "stopped" item on its UIChannel, it:
   1. [x] tidies up its state;
   2. [x] closes the main window;
   3. [x] and the thread exits.
