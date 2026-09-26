Short version: the change would make the code simpler and fix a threading bug, but only if you send a message at redraw
time, not on every value change.

- It fits the architecture you already chose
    - The two caches are the one place where the core and the UI still share memory. Everything else crosses a channel.
    - If the frontend owns the state and only messages cross the boundary, this finishes the CSP design in
      docs/Architecture.md instead of working around it.
    - Call to action: treat this as a migration step, not a refactor, and give it a line in
      docs/Architecture_migration.md.
- It removes a data race
    - PlayerEventStatusUpdate.java:58 and :72 are plain static fields. The core thread writes them and the UI thread
      reads them, with no volatile and no lock.
    - Without a happens-before edge, Java doesn't guarantee the UI ever sees a write. You're relying on the JVM
      happening to behave.
    - The records also hold int[]s. A record doesn't copy arrays, so the UI can see an array the core is still filling
      in. A message sent over a BlockingQueue channel gives you that edge for free.
- Don't send one message per field
    - Today there are about 50 write sites. For example, Monster.java:541-547 writes seven flags in a row, and
      Player.java:1623-1624 writes AC and speed together.
    - One message per write would flood the UI's inbox. The UI would also repaint half-updated states.
    - C already solves this. The core only sets PR_* flags. redraw_stuff (player-calcs.c:2696) then fires one event per
      flag, and only once per batch.
    - It even throttles while resting or running (player-calcs.c:2712).
    - Call to action: send the messages from your ported redraw_stuff, not from the setters.
- Message shapes change
    - Your migration note has EVENT_HP/EVENT_GOLD/EVENT_AC sharing one payload-free record. That only worked because C's
      UI reads the player global.
    - With push, each event has to carry its values. For example, EVENT_HP needs chp/mhp, and EVENT_MONSTERHEALTH needs
      health plus the seven MON_TMD flags.
    - You can still follow "split by shape, not occasion". The shapes just aren't empty any more.
    - Call to action: group the payloads by C event (the table at player-calcs.c:2662), not by record field.
- How many frontend classes
    - Group them by what the UI reads, which is how C's handlers are split. That gives about three: the sidebar (prt_*),
      the monster health bar, and the character sheet (display_panel).
    - Each can be a plain mutable class owned by the UI half, with no records rebuilt.
    - That means the rebuild-one-field setters go. PlayerEventStatusUpdate.java is 3202 lines of them.
- There's still a thread question on the UI side
    - The mutable state must be written and read on the same thread.
    - If the UI loop updates it but Swing paints on the EDT, you get the race from (2) back, just one level down.
    - Call to action: decide which thread owns these classes before you write them.
- Things that move or break
    - statString isn't player state. It's C's stat_names[] (ui-display.c:99), so it becomes a frontend constant and
      never goes over the channel.
    - ChannelRegistry.java:24 gets STAT_MAX from the cache, so it needs a new source.
    - These readers go through the static getter today: UIEntryAssembler.java:305/:314, UIEntryBaseAssembler.java:315,
      HelperFunctions.java:157, and 65 calls in UIPlayer.java.
    - Deltas assume the UI already has a starting state. At birth or load, the core must send everything once, like
      setting every PR_* flag.
    - The PlayerEventStatusUpdate*Test classes would be retired or rewritten.
- Decision for you
    - The main thing to settle is when messages are sent: from the setters, or from redraw_stuff.
    - I recommend redraw_stuff. It's C's behaviour, and it avoids the flooding in (3).

### Message sent at redraw_stuff ###

The sketch has three parts: payload records in channel, one new core-side class that sends at redraw time, and three
UI-side model classes. PlayerCalcs.redrawStuff itself stays as it is.

- Overall shape
    - Core side: PlayerCalcs.redrawStuff (PlayerCalcs.java:882) keeps firing one bus event per PR_* flag, exactly as
      now.
    - A new core-side RedrawHandlers listens for those events, reads Player, and sends one GameEventCoreMessage per
      event. It follows the pattern InitHandlers.java:127 already uses.
    - UI side: a router takes each message apart and writes the values into one of three models.

redrawStuff ─bus→ RedrawHandlers ─coreChannel→ UI loop ─→ RedrawRouter ─→   
SidebarModel                                                                
(core)          (core)                                   (UI)            
MonsterHealthModel

CharSheetModel

- Payloads, in channel.messages.data
    - There's one record per shape, not one per event.
    - Three existing records already fit: EventDataStat (int current, int other), EventDataString (String string) and
      EventDataBoolean (boolean value).
    - EventDataStat covers HP, mana, level and depth, since each is a current/max pair.
    - These are the new records:

    public record EventDataLongPair(long current, long max) implements          
    GameEventData { }      // EXPERIENCE                                        
    public record EventDataLong(long value) implements GameEventData { }        
                // GOLD                                                       
    public record EventDataInt(int value) implements GameEventData { }          
                // AC, PLAYERSPEED, LIGHT                                     
                                                                              
    public record EventDataStats(int[] use, int[] cur, int[] max) implements    
    GameEventData {   // STATS                                                  
        public EventDataStats {           // copy, so the core can't change what
    the UI holds                                                                
            use = use.clone(); cur = cur.clone(); max = max.clone();            
        }                                                                       
    }                                                                           
                                                                              
    public record EventDataRaceClass(String name, String race, String className)
                 // RACE_CLASS                                                  
            implements GameEventData { }

    public record EventDataRaceClass(String name, String race, String className)
                 // RACE_CLASS                                                  
            implements GameEventData { }                                        
                                                                              
    public record EventDataMonsterHealth(boolean tracked, boolean visible,      
                // MONSTERHEALTH                                                
                                         boolean hallucinating, int hp, int     
    maxHp,                                                                      
                                         boolean fear, boolean disen, boolean   
    command,                                                                    
                                         boolean conf, boolean stun, boolean    
    sleep,                                                                      
                                         boolean hold) implements GameEventData 
    { }                                                                         
                                                                              
    public record EventDataCharSheet(/* today's PlayerCharSheetView fields */)  
                // see (6)                                                      
            implements GameEventData { }

-
    - For EventDataStats, the copy in the compact constructor is the fix for the array aliasing from the last answer.
    - For the status lines (study, DTrap, state, feeling, light), send the raw values and let the UI format them. For
      example, send lightLevel as an int, not the finished string. In C, prt_* does the formatting on the UI side.
- Core side: RedrawHandlers

    public class RedrawHandlers {
    private final Sender<CoreMessage> coreSender;
    private final Player player;

    public void register(EventsBusHandler bus) {
        bus.eventAddHandler(GameEventType.EVENT_HP,            this::hp);
        bus.eventAddHandler(GameEventType.EVENT_STATS,         this::stats);
        bus.eventAddHandler(GameEventType.EVENT_MONSTERHEALTH, this::monsterHealth);
        // … one line per sidebar event in PlayerRedraw
    }

    private void hp(GameEventType type) {
        coreSender.send(new CoreMessage.GameEventCoreMessage(type,
                new EventDataStat(player.getCurrentHP(), player.getMaxHP())));
    }
    // …
    }

-
    - It reads the values straight from Player at redraw time, the same way C's prt_* reads player.
    - So the ~50 PlayerEventStatusUpdate.update* calls in Player, Monster, PlayerUpkeep and Chunk are deleted, not
      moved.
    - The monster health handler reads player.getPlayerUpkeep ()'s tracked monster (health_who). Monster.java no longer
      writes anything itself.
- UI side: the three models

    public final class SidebarModel {            // C: prt_* in ui-display.c
        private String name, title, race, className;
        private int level, maxLevel, chp, mhp, csp, msp, ac, speed, depth, maxDepth, light;
        private long exp, maxExp, gold;
        private int[] statUse, statCur, statMax;
        // study / dtrap / state / feeling raw values
    
        void onHp(EventDataStat d)        { chp = d.current(); mhp = d.other(); }
        void onStats(EventDataStats d)    { statUse = d.use(); statCur = d.cur(); statMax = d.max(); }
        // … one onX per event; getters for UIPlayer
    }

    public final class MonsterHealthModel {      // C: prt_health / monster_health_attr
        private EventDataMonsterHealth current;  // whole record, since it always arrives whole
        void onMonsterHealth(EventDataMonsterHealth d) { current = d; }
    }

    public final class CharSheetModel {          // C: display_panel family in ui-player.c
        private EventDataCharSheet current;
        void onCharSheet(EventDataCharSheet d) { current = d; }
    }

-
    - These classes are mutable, and each field is set in one line. There's no rebuild-the-record step, so the 3202
      lines of PlayerEventStatusUpdate.java go away.
    - Monster health and the character sheet arrive whole, so their models just hold the latest record.
- UI side: the router

    public final class RedrawRouter {                                           
          private final SidebarModel sidebar;                                     
          private final MonsterHealthModel monster;                               
          private final CharSheetModel charSheet;                                 
                                                                                  
          public void route(CoreMessage.GameEventCoreMessage m) {                 
              switch (m.type()) {                                                 
                  case EVENT_HP            -> sidebar.onHp((EventDataStat)        
      m.data());                                                                  
                  case EVENT_STATS         -> sidebar.onStats((EventDataStats)    
      m.data());                                                                  
                  case EVENT_MONSTERHEALTH ->                                     
      monster.onMonsterHealth((EventDataMonsterHealth) m.data());                 
                  // …                                                            
                  default -> { }                                                  
              }                                                                   
              // then mark the matching UIPlayer region dirty                     
          }                                                                       
      } 

-
    - The casts exist because GameEventData isn't sealed. It's a plain interface.
    - If you seal it, you can switch on m.data () by pattern instead, and the compiler checks every shape again.
- The character sheet doesn't fit redraw_stuff
    - No PR_* flag covers the character sheet. In C, display_player reads player when it's asked, either from the C
      command or when a subwindow repaints.
    - So the character sheet is naturally a request and a reply: the UI sends a request message and the core replies
      with EventDataCharSheet.
    - That's the same request/response pattern your migration memory sets aside for Chapter 3, birth.
    - Call to action: decide whether the character sheet waits for that pattern or rides along on EVENT_STATS for now.
- Supporting changes
    - statString becomes a frontend constant, a port of C's stat_names[] from ui-display.c:99:

      public final class StatNames {                                              
          public static final String[] FULL    = {"STR", "INT", "WIS", "DEX",     
      "CON"};                                                                     
          public static final String[] REDUCED = {"Str", "Int", "Wis", "Dex",     
      "Con"};                                                                     
      }

-
    - ChannelRegistry.java:24's STAT_MAX takes its value from the stats count, not from the cache.
    - For the starting state, set every sidebar PR_* flag once the character is generated. The first redrawStuff call
      then sends a full set, as C does.
- Thread ownership is still open
    - The models must be written and read on one thread only.
    - If the UI loop writes them but Swing paints on the EDT, hand the EDT a copy. Don't let it read the live models.
    - Call to action: settle which thread owns the models before you write SidebarModel. Everything else here works
      either way.

### The UI thread owns the models ###

That works cleanly, because the code already has a safe handoff point to the EDT. It also means one piece of my last
sketch was unnecessary.

- The EDT handoff already exists
    - The UI thread draws into Screen, then posts an immutable snapshot of it to the EDT. That snapshot comes from
      Screen.frame () (Screen.java:122), which copies the grid and the hotspots.
    - The models only need to be read while that drawing happens, so the EDT never sees them. It only sees the copied
      snapshot.
    - So the models need no volatile, no locks, and no copying for the EDT.
- The rule this sets
    - Only RedrawRouter writes to the models. Only the UI thread's drawing code reads them.
    - Nothing that runs on the EDT may touch a model. That includes Swing listeners and anything SwingUI queues with
      invokeLater.
    - This matches your existing rule that EDT events go to the UI thread through its single inbox.
    - SwingUI.java has no references to UIPlayer or UIRegistry today, so nothing breaks this yet.
    - Call to action: make the model setters package-private, next to RedrawRouter, so the compiler enforces "only the
      router writes".
- Correction to my last sketch
    - The StatNames class Iistry.java:282 alreadyhas statNames, and UIRegistry.java:295 has statReducedNames, which
      ports C's stat_names/stat_n
    - The four readers of getPlayerStatusView ().statString () can switch to UIRegistry.statNames.ava:305/:
      314,UIEntryBaseAssembler.java:315 and HelperFunctions.java:157.
    - The labels there incler that expects just "STR" will need to trim it or change its format.
- Where the models live
    - The UI thread owns them, so they belong in frontend, next to UIPlayer, not in channel.
    - Only the EventData* payload records stay in channel.
    - Call to action: startEVENT_HP end to end. That one event exercises every part of the design.