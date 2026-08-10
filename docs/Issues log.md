# Issues to deal with #

## Present code ##

### StatusDisplayHolder ###

#### All overridden by change to architecture proposed on 260809 ####

~~[A] 260809: Should instance be made volatile - why?~~

~~_volatile on StatusDisplayHolder.instance? Yes._~~

~~_Your own Javadoc at StatusDisplayHolder.java:34-39 already makes the case, and it's correct: safe today only because
Frontend.init registers at line 199 and gameRunner.start () follows at line 201, so Thread.start ()'s happens-before
edge publishes it. That's safety by line ordering in a different class — exactly the kind of thing that breaks silently
when someone reorders two statements that look independent._~~

~~_Cost is nothing here. The field is read a handful of times during start-up, not in a hot loop._~~

~~_Be clear about what it does and doesn't buy you, though:_~~

~~Fixes: a late registration becoming visible to the game thread at all.
    Without volatile the game thread may read the stale DefaultStatusDisplay forever.~~

~~Does not fix: the race itself. If the game thread calls getInstance() before the frontend registers, it gets the 
    no-op default and that note is simply lost — volatile makes
    the handoff eventually visible, not ordered. The Javadoc's 
    "read at the point of use rather than cached, which is what
    lets a front end register after the handlers are wired" is 
    relying on that, so it's worth saying plainly that a note signalled in the gap is dropped.~~

~~_And if you do it, do all four. The same accident-of-ordering holds for CommandGetterHolder.instance (:45),
GameInputHolder.instance (:41), and — the one most likely to bite — GameEngine.eventsBusHandler, which is written on the
EDT inside getGameEngine () and read on the game thread by InitHandlers.initHandlers (). Same thread.start () edge, same
fragility. Leaving three plain and one volatile is worse than leaving all four plain, because it implies the other three
were considered and found safe._~~

~~[ ] 260809: Should there be a change to the Javadoc re Frontend importing StatusDisplayHolder as well as GameRunner?~~

### Main.java ###

[X] ~~260809: Action _TODO:_ Change output of -d errors to be both to a logger, and to System.err.println, instead of
System.out.println.~~

[X] ~~260809: The "does not exist" message is now stale. Main.java:147 checks isDirectory (), but Main.java:148 still
says
"invalid directory path does not exist". A path that exists and is a regular file is now rejected with a message telling
the player it is missing, which sends them looking for the wrong problem. Wording wants to be "is not a directory", or
to distinguish the two cases.~~

[X] ~~260809: A path containing an '=' cannot be used. checkDirectoryOption splits on "=" with no limit, so
-dsave=/tmp/a=b yields three parts and is rejected as malformed. Legal path, refused override. split ("=", 2) would
accept it. C has the same limitation. Pinned as current behaviour by MainTest.aPathContainingAnEqualsSignIsRejected -
flip that test if the decision changes.~~

[X] ~~260809: System.exit (1) inside main made the three -d rejection branches untestable - a test reaching one would
take
the Gradle test worker down. Resolved by extracting checkDirectoryOption (@VisibleForTesting, package-private), which
returns the message instead of acting on it; main keeps the log/print/exit. MainTest covers all three branches, 28
cases.~~

### ObjectProperty / ObjectPropertyTypeWrapper — the stat/mod lookup bug ###

~~[ ] 260810: **Agreed fix: relax the wrapper's equality so the tag is compared once, by the lookup.** This supersedes
the "Option A / Option B" pair the roadmap's Chapter 2 blocker refers to, and also supersedes the sealed-interface plan
first written here — see _Scope_ below. 20 red tests (19 in RuneInitTest, all dying in the same `loadedRunes`
setup, plus RuneVarietyTest.wrappersOverTheSameModifierDifferIfTaggedStatRatherThanMod) point at this.~~

~~_Diagnosis._ C keeps the tag and the subject as two independent fields and compares them independently (
`src/obj-properties.c:32`):~~

~~if ((prop->type == type) && (prop->index == index)) return prop; /* Special case - stats count as mods */ if ((type ==
OBJ_PROPERTY_MOD) && (prop->type == OBJ_PROPERTY_STAT)
&& (prop->index == index)) return prop;~~

~~`index` is a bare int, carrying no tag. The port folded the tag *into* the payload, so
ObjectRegistry.lookupObjectProperty (:758-766) compares it twice — once as `property.getType ()`, and again inside
`getPayload ().equals (payload)`, because ObjectPropertyTypeWrapper.equals (:223) opens with
`other.type != this.type`. The special-case branch is therefore dead code: the second half of its own condition
re-imposes the tag equality the first half just relaxed. The discriminator is stored in two places, so relaxing it in
one has no effect. Symptom is `No property found for Object Modifier: OM_STR` from Rune.initRunes (:130), because the
data declares STR as `type:stat` while initRunes (:123) asks for `OBJ_PROPERTY_MOD`.~~

~~_The change._ Delete one line — ObjectPropertyTypeWrapper.java:223, `if (other.type != this.type) return false;`. The
wrapper then compares subjects only, which is what C's `prop->index == index` compares, and the tag is compared exactly
once, by lookupObjectProperty, which is where C compares it. The special case becomes live for the first time. Nothing
else in main moves: ObjectRegistry:758-766 already reads the way C does once the wrapper stops second-guessing it.~~

~~What this leaves behind is a wrapper whose `type` field is validation-only — it no longer takes part in identity. That
is worth a Javadoc line on both the field and `equals`, because a field that is checked on construction and ignored on
comparison looks like an oversight otherwise.~~

~~_Verified 260810_ against the real `object_property.txt`, by simulating the fixed lookup in a throwaway probe rather
than editing main: every ObjectModifier resolves (the five stats through the special case), every ObjectFlag still
resolves, the four ACID properties still come back distinct, and LIGHT still separates `mod` from `resistance`.~~

~~_Scope: flags stay out of it._ The extension to ObjectFlag was never a second bug — nothing in the flag path is
broken. C relaxes the tag for mod/stat and nothing else; all twelve of its flag call sites pass `OBJ_PROPERTY_FLAG`
with a flag index; and no flag code is declared twice in the data. The only duplicated codes are ACID/ELEC/FIRE/COLD
across the four element types, and LIGHT across `mod` and `resistance` — and that last pair is safe by payload type,
`ObjectModifier.OM_LIGHT` being a different object from `ElementEnum.ELEM_LIGHT`.~~

~~ObjectFlag was in the permits list only because the sealed interface would have retyped `ObjectProperty.payload`, and
that field holds flags as well as modifiers and elements; leaving flags on the wrapper would have meant two payload
mechanisms in one class. So it was a consequence of that design, not a reason for it. With the one-line fix the
interface is not needed, and neither is the flag question.~~

~~_Deferred, not discarded._ The sealed `ObjectPropertySubject` still buys two things the one-line fix does not: a
working hashCode, and compiler-checked exhaustiveness at the call sites. Neither pays for six touches in main today —
there are two call sites, both in initRunes. Revisit it if a hash-keyed property lookup is ever wanted;
`obj-power.c:549,598` runs the same lookups per object per power calculation, which is the place a linear scan will
first start to hurt.~~

~~Note for whenever it is revisited: an enum **cannot** name a superclass — every enum implicitly extends
java.lang.Enum — so it has to be an interface rather than a shared parent class.~~

~~_The trap to avoid._ The tag leaves the wrapper's `equals`, but it must not leave the lookup, so do **not** drop the
`property.getType ().equals (type)` half of the first branch. Element codes are not unique —
`lib/gamedata/object_property.txt` declares ACID four times, as
`ignore` (:772), `resistance` (:792), `vulnerability` (:870) and `immunity` (:894), and likewise ELEC, FIRE and COLD.
Those are four distinct properties with four distinct names and UI entries, separated only by the tag. Match on subject
alone and they collapse, `lookupObjectProperty` returns whichever is first in file order, initRunes builds the wrong
resist runes, and nothing currently fails — no test pins which of the four comes back. Modifiers are the opposite case:
no modifier code appears twice across `stat` and `mod` (STR/INT/WIS/DEX/CON are stat, the rest mod), which is precisely
why C can afford the special case for mods and not for elements.~~

~~_Does not change._ The wrapper's constructors still validate that tag and payload are compatible — an ElementEnum
cannot be tagged OBJ_PROPERTY_FLAG (ObjectPropertyTypeWrapper.java:140-204) — and that is now the `type` field's whole
job. The missing hashCode also stays missing, so propertyWrappersAreNotUsableAsHashKeys stays true and
lookupObjectProperty stays a linear scan.~~

~~_Blast radius._ main: ObjectPropertyTypeWrapper only, one line plus Javadoc. test: RuneVarietyTest —
wrappersOverTheSameModifierDifferIfTaggedStatRatherThanMod needs renaming, its assertion already expecting the fixed
behaviour while its name still describes the bug. Claude's to fix, along with a new test pinning all four ACID
properties as distinct, which is the case a subject-only match could quietly break.~~

## Javadoc to add ##
 