# Issues to deal with #

## Present code ##

### StatusDisplayHolder ###

[A] 260809: Should instance be made volatile - why?

_volatile on StatusDisplayHolder.instance? Yes._

_Your own Javadoc at StatusDisplayHolder.java:34-39 already makes the case, and it's correct: safe today only because
Frontend.init registers at line 199 and gameRunner.start () follows at line 201, so Thread.start ()'s happens-before
edge publishes it. That's safety by line ordering in a different class — exactly the kind of thing that breaks silently
when someone reorders two statements that look independent._

_Cost is nothing here. The field is read a handful of times during start-up, not in a hot loop._

_Be clear about what it does and doesn't buy you, though:_

    Fixes: a late registration becoming visible to the game thread at all.
    Without volatile the game thread may read the stale 
    DefaultStatusDisplay forever.

    Does not fix: the race itself. If the game thread calls 
    getInstance() before the frontend registers, it gets the 
    no-op default and that note is simply lost — volatile makes
    the handoff eventually visible, not ordered. The Javadoc's 
    "read at the point of use rather than cached, which is what
    lets a front end register after the handlers are wired" is 
    relying on that, so it's worth saying plainly that a note 
    signalled in the gap is dropped.

_And if you do it, do all four. The same accident-of-ordering holds for CommandGetterHolder.instance (:45),
GameInputHolder.instance (:41), and — the one most likely to bite — GameEngine.eventsBusHandler, which is written on the
EDT inside getGameEngine () and read on the game thread by InitHandlers.initHandlers (). Same thread.start () edge, same
fragility. Leaving three plain and one volatile is worse than leaving all four plain, because it implies the other three
were considered and found safe._

[ ] 260809: Should there be a change to the Javadoc re Frontend importing StatusDisplayHolder as well as GameRunner?

### Main.java ###

[ ] 260809: Action _TODO:_ Change output of -d errors to be both to a logger, and to System.err.println, instead of
System.out.println.

[ ] 260809: The "does not exist" message is now stale. Main.java:147 checks isDirectory (), but Main.java:148 still says
"invalid directory path does not exist". A path that exists and is a regular file is now rejected with a message telling
the player it is missing, which sends them looking for the wrong problem. Wording wants to be "is not a directory", or
to distinguish the two cases.

[ ] 260809: A path containing an '=' cannot be used. checkDirectoryOption splits on "=" with no limit, so
-dsave=/tmp/a=b yields three parts and is rejected as malformed. Legal path, refused override. split ("=", 2) would
accept it. C has the same limitation. Pinned as current behaviour by MainTest.aPathContainingAnEqualsSignIsRejected -
flip that test if the decision changes.

[A] 260809: System.exit (1) inside main made the three -d rejection branches untestable - a test reaching one would take
the Gradle test worker down. Resolved by extracting checkDirectoryOption (@VisibleForTesting, package-private), which
returns the message instead of acting on it; main keeps the log/print/exit. MainTest covers all three branches, 28
cases.

## Javadoc to add ##
 