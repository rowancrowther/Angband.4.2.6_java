How does Angband run.

1) Add the 6 main & 2 birth event handlers (Enter Init, Leave Init, Enter Game, Leave Game, Enter World, Leave World,
   Enter Birth, Leave Birth)
2) Trigger the Enter Init event\
   1 Show the splash screen\
   2 Add the Event Initstatus handler
3) Initialises the game modules\
   1 Init game constants\
   2 quarks\
   3 messages\
   4 ui visuals\
   5 arrays – pulls in all the data from the ~30 gamedata/user files. This is covered in Chapter 1.\
   6 player\
   7 generation room and level templates\
   8 rune initialisation\
   9 object allocation/ego allocation/money types\
   10 create a map of enum to boolean (probably better as an array of flags?) for the ignore function\
   11 monster allocation table\
   12 store production – parse store.txt\
   13 options – initialise the pages of options that the user can choose before starting a game\
   14 ui player module – does nothing!\
   15 ui equipment module – does nothing!
4) Sets the monster and object windows to null
5) Seeds the RNG

---

6) Loads the preferences file

---

7) Initialise the command list
8) initialise knowledge
9) initialise input hooks
10) initialise visual preferences
11) Do something with the screens update status
12) Verify that there is a main window and make it the active window.
13) Confirm it is the correct size (80 cols x 24 rows), hide the cursor
14) Update the terminals for preference changes (step 8?)
15) Redraw all the windows
16) Initialise and set up the sub-terminals
17) Signal that set-up is complete and await input

---

18) Run play_game (…) ← GAME_NEW chosen as an option for this document

---

19) Do the birth subsystem to create a new character
21) Signal leaving Init, entering game and entering world.
22) If there is no character eye-view of the dungeon, prepare the next level for the player
23) housekeeping (on_new_level ())

---

24) Pre-turn game refresh
25) Get a command from the player
26) Process the command
    1) Clean-up player after previous command
    2) Process the player until they use energy or another command is required
        1) Process the player
    3) While the player has enough energy to move, give the player another action
        1) Signal Event Animate
        2) All monsters with more energy than the player act (does this include decrementing their energy?) Check to see
           if the player is dead, no longer playing, or we are generating a new level
        3) The player acts (the command is run)
            1) Process the player until they use energy or another command is required
        4) While true
            1) Notice stuff
            2) Handle stuff
            3) Signal Event Refresh
            4) If the player is dead or no longer playing jump to 26
            5) All the other monsters act (does this include decrementing their energy?)
            6) All the monsters are reset.
            7) Notice stuff
            8) Handle Stuff
            9) Signal Event Refresh
            10) The world is processed every 10 turns, or on a new dungeon level
            11) Increment the players energy
            12) Increment the turn counter
        5) Generate a new level if requested
            1) If there is a dungeon level attached to the player, do the level leave clean up.
            2) Check to see if it is an arena level we are going to
            3) Prepare the next level for the player
            4) Run the new level housekeeping
            5) Set the generate_level flag
            6) If it is an arena
                1) Kill the arena monsters
        6) If the player has enough energy to move then
            1) Monsters with more energy than the player move first
            2) If the player is dead/no longer playing/on a new level then break
            3) Process the player until they use energy or another command is required
        7) Check to see if the player is not dead and still playing. If so loop back to 24
    8) If not fire off the EVENT_LEAVE_WORLD and EVENT_LEAVE_GAME event signals.
27) If the player wants to play again, loop back to 2 with a guard on to skip levels 7-10

---

28) Clean up the game

---

The event bus is a hashmap of GameEventType to a List of GameEventHandlers. Each handler consists of code which takes
data from the relevant game message (GameEventType, GameEventData), and runs this code. Normally this running of code
results in a message being put on a channel, as outlined below. The event bus is purely on the Core side of the system.

The old UI→core path — three static holders and the UI calling the core's methods directly — has been totally replaced.
The UI thread now puts bespoke UIMessages on coreQueue, to be picked up by the core and processed there. (CommandQueue
is a different thing entirely and is unchanged: it is the port of C's cmdq, core-internal, and holds Commands awaiting
dispatch inside the core once Chapter 5 gives it callers.)

The new state of Angband due to the port has introduced a 'channel' system allowing messages to be passed between
threads running the game. There are two different threads, in addition to the EDT, `core` and `UI`. The core thread is
responsible for running everything in the backend/middle packages (namely the working guts of the game, in charge of
responding to the user input). The UI thread is responsible for running the UI system, displaying the windows, when the
sound system is implemented it will be in charge of making sounds.

The two threads communicate with each other via two queues, two message receivers and 3 message senders. Each queue goes
from one thread to another (the UIQueue goes from the Core thread to the UI thread, and receives messages from the
CoreSender, and the EDTSender; the CoreQueue goes from the UI thread to the Core thread, and receives messages from the
UISender).

In the current state of the port, Angband.java is in package uk.co.jackoftradesltd.channel.globals, part of the game
that is used by both the UI and the core, not in the UI (package uk.co.jackoftradesltd.frontend). I would argue that it
is the
right side of the design, as it is UI-agnostic, but contains data that both the UI and the core need access to, so it
fulfils the "only put in the channels things to do with the communication between the two areas, or common
nomenclature".