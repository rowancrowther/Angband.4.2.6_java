## Principles

- The user interface (UI) and the core should have no direct knowledge of each other.
- The architectural approach here is similar to CAR Hoare's CSP (Communicating Sequential Processes).
- ~~- The two subsystems should communicate solely via exactly two thread-safe queues: one ("display queue") from core
  to UI and one ("command queue") from UI to core. The core should never send on the UI-to-core queue, and the UI should
  never send on the core-to-UI queue.~~
- Only the UI thread receives on the display channel; only the core receives on the command channel; the core sends only
  display message

## Sequencing

### Startup

The startup in main ():

1. creates the two queues;
2. starts the core setting it to be able to receive from the command queue, send on the display queue, and have a copy
   of the startup arguments;
3. starts the user interface setting it to be able to receive from the display queue, send on the command queue, and
   have a copy of the startup arguments;
4. waits for both the UI and the core threads to exit;
5. exits.

### Core

1. When its thread starts, the core should:
    1. parse its arguments; set up anything it can (for example reading data files);
    2. wait for commands on the command queue.
2. When the core receives a "start" command, it:
    1. prepares to enter the birth state for the player, which will lead to many messages being sent to the display
       queue;
    2. receives and process any birth related commands from the command queue;
    4. finalises the player birth process
    3. runs the game loop.
3. The game loop is similar to the existing C game loop, repeatedly:
    1. receiving a message from the command queue;
    2. processing the message;
    3. sending zero or more (usually one or more) messages to the display queue.
4. When the core receives a "saveAndStop" command on the command queue, it:
    1. stops the game loop;
    2. saves the game if necessary;
    3. sends "stopped" to the display queue;
    4. and the thread exits.

### User interface

1. When its thread starts, the user interface should:
    1. parse its arguments;
    2. set up anything it can (notably including the UI Angband colour system, and a main window extending a JFrame);
    3. prepare an 80x24 array of AngbandDisplayCharacters to hold the screen text and colours;
    4. register a JWT event handler on the window close java event of the main window;
    5. send "start" to the command queue.
2. The user interface then waits for a) JWT events and also b) items from the display queue. Either or both might
   happen.
3. Each time the user interface receives a JWT event or an entry on the display queue, it processes that. Such
   processing might involve sending items on the command queue, or making short requests of JWT / Swing via
   SwingUtilities.invokeLater ().
4. When the user interface receives a window close event, it sends a "saveAndStop" event on the command queue.
5. When the user interface receives a "stopped" item on the display queue, it:
    1. tidies up its state;
    2. closes the main window;
    3. and the thread exits.