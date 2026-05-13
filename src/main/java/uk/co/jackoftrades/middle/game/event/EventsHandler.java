package uk.co.jackoftrades.middle.game.event;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.objects.ItemObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsHandler {
    private record EventRecord(EventHandlerInterface handler, EventUser user) {
    }

    private static final HashMap<GameEventType, ArrayList<EventHandlerInterface>> handlers = new HashMap<>();

    private static EventsHandler instance;

    private EventsHandler() {
        for (GameEventType eventType : GameEventType.values()) {
            handlers.put(eventType, new ArrayList<>());
        }
    }

    private static void initialise() {
        if (instance == null) instance = new EventsHandler();
    }

    /**
     * Get an instance of this events handler - note there is only one.
     *
     * @return The instance of the events handler
     */
    public static EventsHandler getInstance() {
        if (instance == null)
            instance = new EventsHandler();

        return instance;
    }

    /**
     * Add a handler to the events handler
     *
     * @param eventType The event type that we are listening for
     * @param handler   the EventHandlerInterface which will handle the event type
     */
    public static void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        ArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        currentList.add(handler);
    }

    /**
     * Removes a handler from the events table
     *
     * @param eventType The event type we are going to remove a handler from
     * @param handler   The event handler we are removing
     */
    public static void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        ArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        currentList.remove(handler);
    }

    /**
     * Dispatch an event down the handlers for it, sending the event type and some incoming data
     *
     * @param eventType The event time we are triggering
     * @param data      The data we are sending to the events
     * @param user      The caller of this event
     */
    public static void gameEventDispatch(GameEventType eventType, GameEventData data, EventUser user) {
        ArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        for (EventHandlerInterface handler : currentList) {
            handler.dispatch(eventType, data, user);
        }
    }

    /**
     * Dispatch an event down the handlers for it, sending the event type and some incoming data
     *
     * @param eventType The event time we are triggering
     * @param data      The data we are sending to the events
     */
    public static void gameEventDispatch(GameEventType eventType, GameEventData data) {
        gameEventDispatch(eventType, data, null);
    }

    /**
     * Remove all the handlers of a specified type
     *
     * @param eventType The event type we are removing the handlers of
     */
    public static void eventRemoveHandlerType(GameEventType eventType) {
        ArrayList<EventHandlerInterface> newList = new ArrayList<>();
        handlers.put(eventType, newList);
    }

    /**
     * Clear down all event handlers
     */
    public static void eventRemoveAllHandlers() {
        for (GameEventType eventType : GameEventType.values()) {
            eventRemoveHandlerType(eventType);
        }
    }

    /**
     * Add an ArrayList of EventHandlerInterfaces with a given EventUser to the relavant Event queue in the Handler
     *
     * @param eventType The event type that we are adding this ArrayList to
     * @param records   An ArrayList of EventHandlerInterfaces
     * @param user      NOT USED - TODO: Find out if we need to use this
     */
    public static void eventAddHandlerSet(GameEventType eventType, @NotNull ArrayList<EventHandlerInterface> records, EventUser user) {
        for (EventHandlerInterface record : records) {
            eventAddHandler(eventType, record);
        }
    }

    /**
     * Remove all the events in a given list
     *
     * @param eventType The event type of the events we are going to remove
     * @param records   An ArrayList of EventHandlerInterfaces
     * @param user      NOT USED - TODO: Find out if we need to use this
     */
    public static void eventRemoveHandlerSet(GameEventType eventType, @NotNull ArrayList<EventHandlerInterface> records, EventUser user) {
        for (EventHandlerInterface record : records) {
            eventRemoveHandler(eventType, record);
        }
    }

    /**
     * Send the signal to dispatch all the events for a given event type
     *
     * @param eventType The event type we are signalling
     */
    public static void eventSignal(GameEventType eventType) {
        gameEventDispatch(eventType, null);
    }

    /**
     * Send a signal to dispatch all the events of a given type with a boolean data type
     *
     * @param eventType The event type we are signalling
     * @param flag      The boolean value we are sending
     */
    public static void eventSignalFLag(GameEventType eventType, boolean flag) {
        gameEventDispatch(eventType, new EventDataBoolean(flag));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a Loc data type determined by its x and y
     * coordinates
     *
     * @param eventType The event type we are signalling
     * @param x         The x coordinate of the Loc
     * @param y         The y coordinate of the Loc
     */
    public static void eventSignalPoint(GameEventType eventType, int x, int y) {
        gameEventDispatch(eventType, new EventDataPoint(x, y));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a Loc data type
     *
     * @param eventType The event type we are signalling
     * @param point     The Loc we are using to signal the event
     */
    public static void eventSignalPoint(GameEventType eventType, Loc point) {
        gameEventDispatch(eventType, new EventDataPoint(point));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a String data type
     *
     * @param eventType The event type we are signalling
     * @param string    The String we are using in signalling the event
     */
    public static void eventSignalString(GameEventType eventType, String string) {
        gameEventDispatch(eventType, new EventDataString(string));
    }

    /**
     * Send a signal to dispatch all events of a given type with a Message data type
     *
     * @param eventType The event type we are signalling
     * @param message   The Message we are using in signalling the event
     */
    public static void eventSignalMessage(GameEventType eventType, int type, String message) {
        gameEventDispatch(eventType, new EventDataMessage(type, message));
    }

    /**
     * Send a signal to dispatch all events of a given type with Birthpoint data
     *
     * @param eventType The event we are signalling
     * @param stats     A HashMap of Stats to amount of points already spent for each stat
     * @param incPoints A HashMap of Stats to the amount it would take to increase the stat by a further point for each
     *                  stat
     * @param remaining The remaining number of points to spend
     */
    public static void eventSignalBirthpoints(GameEventType eventType,
                                        HashMap<Stats, Integer> stats,
                                        HashMap<Stats, Integer> incPoints,
                                        int remaining) {
        gameEventDispatch(eventType, new EventDataBirthPoints(stats, incPoints, remaining));
    }

    /**
     * Send a signal to dispatch all events of a given type with an Explosion
     *
     * @param eventType      The event we are signalling
     * @param projType       The projection type - TODO: currently integer, probably will change to an enum
     * @param numGrids       The number of grids affected by the explosion
     * @param distanceToGrid The distance to the grids from the Loc of the player
     * @param drawing        Whether we are drawing the explosion?
     * @param playerSeesGrid Whether the player sees the explosion on a particular grid
     * @param blastGrid      The grids we are blasting with this explosion
     * @param centre         The centre of the explosion
     */
    public static void eventSignalBlast(GameEventType eventType,
                                  int projType,
                                  int numGrids,
                                  ArrayList<Integer> distanceToGrid,
                                  boolean drawing,
                                  ArrayList<Boolean> playerSeesGrid,
                                  ArrayList<Loc> blastGrid,
                                  Loc centre) {
        gameEventDispatch(eventType, new EventDataExplosion(projType, numGrids, distanceToGrid, drawing, playerSeesGrid,
                blastGrid, centre));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a Bolt
     *
     * @param eventType The event we are signalling
     * @param projType  The projection type - TODO: currently integer, probably will change to an enum
     * @param drawing   Whether we are drawing the bolt?
     * @param seen      Whether the bolt is seen?
     * @param beam      Whether the bolt is a beam?
     * @param oy        The origin-Y of the bolt?
     * @param ox        The origin-X of the bolt?
     * @param y         The target-Y of the bolt?
     * @param x         The target-X of the bolt
     */
    public static void eventSignalBolt(GameEventType eventType,
                                 int projType,
                                 boolean drawing,
                                 boolean seen,
                                 boolean beam,
                                 int oy,
                                 int ox,
                                 int y,
                                 int x) {
        gameEventDispatch(eventType, new EventDataBolt(projType, drawing, seen, beam, oy, ox, y, x));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a Missile
     *
     * @param eventType  The event we are signalling
     * @param itemObject The object which is the missile?
     * @param seen       Whether the missile is seen?
     * @param y          The y location of the missile (start/end/current)?
     * @param x          The x location of the missile (start/end/current)?
     */
    public static void eventSignalMissile(GameEventType eventType,
                                    ItemObject itemObject,
                                    boolean seen,
                                    int y,
                                    int x) {
        gameEventDispatch(eventType, new EventDataMissile(itemObject, seen, y, x));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a size
     *
     * @param eventType The event type we are signalling
     * @param height    The height of the area/size
     * @param width     The width of the area/size
     */
    public static void eventSignalSize(GameEventType eventType, int height, int width) {
        gameEventDispatch(eventType, new EventDataSize(height, width));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a tunnel
     *
     * @param eventType The event type we are signalling
     * @param nStep     The number of steps in the tunnel
     * @param nPierce   The number of wall piercings
     * @param nDug      The number of spaces dug ignoring wall piercings
     * @param dStart    The city block distance from the start of the tunnel
     * @param dEnd      The city block distance to the goal of the tunnel
     * @param early     Whether the tunnelling has been stopped early
     */
    public static void eventSignalTunnel(GameEventType eventType, int nStep, int nPierce, int nDug,
                                   int dStart, int dEnd, boolean early) {
        gameEventDispatch(eventType, new EventDataTunnel(nStep, nPierce, nDug, dStart, dEnd, early));
    }
}