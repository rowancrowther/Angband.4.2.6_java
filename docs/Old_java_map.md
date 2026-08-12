### How does Angband run in Java ###

It assumes that it can be launched from a terminal.

1. Main sets some basic options, to be overwritten depending on incoming parameters.
2. Main sorts through the incoming parameters, and process them, displaying for the -l (list characters) and default
   (show usage) in windows which occur prior to there being a core thread or a UI thread.
3. It should be noted that the incoming parameter '-d' (change one of the directories) can cause errors, which are
   logged and then sent to System.err, followed by a System.exit (1).
4. Main updates the startup options.
5. Main queues a Runnable (SwingUtilities.invokeLater (startFrontend)), and ceases to exist
6. startFrontend.run () executes on the EDT getting a new gameRunner, and passing it through to a new swingUI.
    1. When the swingUI is created, it creates a new ArrayList for windows, creates a new main window and adds it to the
       list, then sets activeWindow to be the main window, and sets the swingUI gameRunner to be the game runner
       passed in in step 5.
7. The EDT initialises based on the startup options.
    1. Frontend initialises the activeWindow
        1. Frontend sets the colour, font, and sizes the window based on that font
        2. Frontend sets the title of the window, sets a main panel, adds the main panel to the active window and shows
           the active window.
        3. Frontend registers itself via the SplashScreen object as the StatusDisplayHolder.
        4. Frontend then starts the gameRunner.
    2. GameRunner is running on the EDT at this point.
        1. GameRunner creates a new thread (GameRunner.gameLoop) to become the core thread, and sets the running flag to
           true.
        2. GameRunner sets the gameEngine to the sole instance of the GameEngine
        3. GameRunner starts this core thread
    3. The thread initialises the 6 handlers (EVENT_ENTER_INIT, EVENT_LEAVE_INIT, EVENT_ENTER_GAME, EVENT_LEAVE_GAME,
       EVENT_ENTER_WORLD, EVENT_LEAVE_WORLD) and then the two BirthStateHandlers (EVENT_ENTER_BIRTH &
       EVENT_LEAVE_BIRTH). These have to be initialised before the gameEngine loads, as the first thing the gameEngine
       does is call GameConstants.init, which signals the EVENT_ENTER_INIT event to display some text on the main
       window.
        1. At the moment only EVENT_ENTER_INIT and EVENT_INITSTATUS are live, the others exist for when the port is
           expanded to cover those events. The handlers are plugged in now.
    4. Core thread then runs gameEngine.loadGameConstants (), which runs:
        1. GameConstants.init () - initialising the game constants and ~40 data arrays in the registry (loading up the
           data to allow the system to run, One datafile, lib/user/lore.txt, isn't loaded yet as it needs the game save
           system to be able to be parsed correctly).
        2. It then creates a player and it's sub parts (currently a stub creation).
        3. It then creates a stub cave, to give the game somewhere to stand until level creation is ported - needs to be
           replaced
        4. It then runs the dungeon profile loader, the room template loader and the vault template loader
        5. It then sets the commandQueue up, passing in the main player, to receive commands and send them through to
           itself from the UI thread.
    5. The game thread is sleeping for 5 milliseconds, and if the running flag is still true, continues.
    6. If the game thread is interrupted it throws a RuntimeException
    7. If the game thread is stopped it exits
    8. The UI thread is kept alive by the main window. The main window has an event handler on it so that when it closes
       it sends a signal to the gameRunner to request stop on the core thread, and then exits with a value of 0.
    9. The request stop on the core thread sets the running flag to false, and interrupts the thread's execution, and
       then exits.

### How does it differ from the C code ###

#### The code differs in the following ways ####

The java code splits the game into two Threads, forcing a clear differentiation between the backend/core and the
swingUI. The C has the split, which it differentiates by prefixing the classes that deal with the UI with ui (i.e.
ui-display.c), but there is no thread other than the main one running at any time. C's split between core and ui is
purely a naming convention. Java's is enforced. You can check the Java's UI/Core boundary isn't being breached by
reading the import statements on a UI class.

In C the main control of the game loop continues in a while (1) loop, that sits inside play_game () for the entire game.
In Java, the main function closes after starting the UI runnable, and the JVM stays up due to the shown window.

The Event Bus is created on the UI and used on the core thread. Its initialisation order, before the thread.start (), is
important and this has been documented in the code.

In C every event_add_handler is registered in a UI file, and called in a core file. Core signals, UI registers. In Java,
every eventAddHandler is registered and called in a core file, with there being only one corridor that the event message
can get through to the UI (StatusDisplayHolder.getInstance ()) Call goes core → UI.

However, dependency wise, the arrow goes UI to Core - the UI is dependent on the core, and not the other way round.
Roughly speaking you can delete the UI, and the core would continue to run. This is intentional - if at some future date
you want to put a different UI on there, you can replace the current UI and not worry about the core at all.

It should be noted that this is the same way that the big_map ends, showing that these two documents are compatible.