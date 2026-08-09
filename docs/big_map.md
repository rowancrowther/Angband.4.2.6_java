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

The UI is handled by two main ‘classes’ the command queue, and the event handler bus. Events are registered on the event
handler bus by event type a game event handler function, and a user. (In the port the user is removed and added as flat
data to the event handler function). When a particular event type is signalled by the bus to all the event handlers

The event bus is core side, with the registration table being owned on the core side. Event signals come from the core
side, and only ever a type of event, not a specific event. The functions registered with the events are all registered
on the UI side (?), and all run UI side (?), so the event bus forms a acceptable hole through the boundary between the
UI and the core. It is that which makes the core UI independent – if you replaced it with a different UI that took the
same events, it would still work. This makes the core UI-agnostic. The event passes flat data through to the UI side via
a small number of functions on the screen (change this character here and redraw the screen, change the entire screen
and redraw, …). Core signals, UI registers, and the dependency arrow only ever points UI → core.

In the current state of the port, Angband.java is in package uk.co.jackoftrades.middle.game, and as such is in the
middle part of the core, not in the UI (package uk.co.jackoftrades.frontend). I would argue that it is the right side of
the design, as it is UI-agnostic, containing data.