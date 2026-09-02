/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectDescription;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#printCustomMessage} - the port of C's {@code print_custom_message}
 * ({@code src/obj-util.c:1116}).
 *
 * <p>Every expected value below was read off that C rather than off the Java: the loop copies the
 * text before each {@code {}, looks the tag up with {@code msg_tag_lookup}, appends the
 * substitution and resumes after the closing brace, and an unclosed brace is simply skipped over.
 * The method's only output is the message it raises, so each test stands a capturing bus in front
 * of the real one and reads the text that arrives.
 *
 * <p>The tags divide on two inputs - whether there is an object at all, and whether it is a single
 * item or a pile - so {@link Tags} walks both for each of the four. {@link Malformed} covers what
 * the loop does with braces the data files should never contain, which is where the port's two
 * deliberate divergences from C show up, both pinned in {@link Divergences}.
 *
 * <p>{@code {name}} substitutes {@link ItemObject#description}, which is still a stub, so the
 * tests that reach it assert against whatever that method returns rather than against a literal.
 * They therefore keep testing the substitution, not the description, once the stub is filled in.
 *
 * <p>Class ItemObjectCustomMessageTest coded on 260829.
 *
 * @author Rowan Crowther
 */
@DisplayName("ItemObject.printCustomMessage")
class ItemObjectCustomMessageTest {

    /**
     * The bus this test listens on.
     */
    private CapturingBus bus;

    /**
     * The bus the engine had before, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * Builds an item of the given quantity.
     *
     * @param number how many the stack holds
     * @return the item
     */
    private static ItemObject item(int number) {
        ItemObject item = new ItemObject();
        item.setNumber(number);
        return item;
    }

    /**
     * Builds an item of the given quantity carrying a kind of the given name.
     *
     * @param number how many the stack holds
     * @param name   the kind's name template
     * @return the item
     * @throws Exception if the kind's name cannot be set
     */
    private static ItemObject itemOfKind(int number, String name) throws Exception {
        ObjectKind kind = new ObjectKind();

        Field nameField = ObjectKind.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(kind, name);
        kind.setAware(true);

        ItemObject item = item(number);
        item.setKind(kind);
        return item;
    }

    /**
     * The description {@code {name}} substitutes, asked of the method that produces it so that the
     * expectation follows the stub when it is replaced.
     *
     * @param item the item to describe
     * @return the text the tag should be replaced by
     */
    private static String descriptionOf(ItemObject item) {
        return item.description(new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_PREFIX,
                ObjectDescription.ODESC_BASE), null);
    }

    /**
     * Stands a capturing bus in front of the real one, and empties the message log.
     *
     * <p>The log has to be cleared because {@link uk.co.jackoftradesltd.middle.Message} coalesces a
     * message identical to the newest entry and decorates the text it signals with a
     * {@code " (x2)"} count. The log is static and outlives a test, so two tests that expect the
     * same text would otherwise fail on whichever ran second.
     *
     * @throws Exception if the log cannot be reached
     */
    @BeforeEach
    void setUp() throws Exception {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        Field log = uk.co.jackoftradesltd.middle.Message.class.getDeclaredField("messageLog");
        log.setAccessible(true);
        ((java.util.Deque<?>) log.get(null)).clear();
    }

    /**
     * Puts the real bus back.
     */
    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Runs a template through the method and returns the one message it raised.
     *
     * @param item     the object the message is about
     * @param template the message template
     * @param noObject whether to describe bare hands instead of the object
     * @return the message text
     */
    private String print(ItemObject item, String template, boolean noObject) {
        item.printCustomMessage(template, MessageType.MSG_GENERIC, null, noObject);

        assertEquals(1, bus.messages.size(), "expected exactly one message");
        return bus.messages.get(0);
    }

    /**
     * An event bus that records the messages the middle layer raises.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * The text of every message event seen, in order.
         */
        private final List<String> messages = new ArrayList<>();

        /**
         * The type of every message event seen, in order.
         */
        private final List<MessageType> types = new ArrayList<>();

        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
            if (data instanceof EventDataMessage message) {
                messages.add(message.message());
                types.add(message.type());
            }
        }
    }

    /**
     * The four tags C understands, each across the two inputs that decide what it becomes: whether
     * there is an object, and whether that object is a single item or a pile.
     */
    @Nested
    @DisplayName("the four tags")
    class Tags {

        /**
         * {@code {name}} becomes the object's full description.
         */
        @Test
        @DisplayName("{name} becomes the object's description")
        void nameBecomesDescription() {
            ItemObject item = item(1);

            assertEquals("Your " + descriptionOf(item) + " glows.",
                    print(item, "Your {name} glows.", false));
        }

        /**
         * With no object, {@code {name}} becomes the player's hands - C's {@code obj} is
         * {@code null} for an unarmed blow, and the message has to name something.
         */
        @Test
        @DisplayName("{name} with no object becomes hands")
        void nameWithoutObjectBecomesHands() {
            assertEquals("Your hands glow.", print(item(1), "Your {name} glow.", true));
        }

        /**
         * {@code {kind}} becomes the kind's name alone, with no quantity prefix even for a pile -
         * that is the whole reason C has a second name tag.
         *
         * @throws Exception if the kind cannot be built
         */
        @Test
        @DisplayName("{kind} becomes the bare kind name")
        void kindBecomesKindName() throws Exception {
            assertEquals("It is a Ruby.", print(itemOfKind(3, "Ruby"), "It is a {kind}.", false));
        }

        /**
         * With no object, {@code {kind}} becomes hands as well, and the kind is never asked for.
         */
        @Test
        @DisplayName("{kind} with no object becomes hands")
        void kindWithoutObjectBecomesHands() {
            assertEquals("It is your hands.", print(item(1), "It is your {kind}.", true));
        }

        /**
         * {@code {s}} is the third-person verb ending, and a single object takes it.
         */
        @Test
        @DisplayName("{s} adds the verb ending for a single object")
        void verbEndingForOne() {
            assertEquals("It glows brightly.", print(item(1), "It glow{s} brightly.", false));
        }

        /**
         * A pile is plural, so the ending is dropped - and, the point of the case, the tag goes
         * with it rather than being left in the text.
         */
        @Test
        @DisplayName("{s} adds nothing for a pile, and leaves nothing behind")
        void verbEndingForPile() {
            assertEquals("They glow brightly.", print(item(2), "They glow{s} brightly.", false));
        }

        /**
         * C's condition is {@code obj && obj->number == 1}, so no object is plural too.
         */
        @Test
        @DisplayName("{s} adds nothing when there is no object")
        void verbEndingWithoutObject() {
            assertEquals("They glow brightly.", print(item(1), "They glow{s} brightly.", true));
        }

        /**
         * {@code {is}} agrees with a single object.
         */
        @Test
        @DisplayName("{is} becomes is for a single object")
        void isForOne() {
            assertEquals("It is hot.", print(item(1), "It {is} hot.", false));
        }

        /**
         * A pile takes the plural.
         */
        @Test
        @DisplayName("{is} becomes are for a pile")
        void isForPile() {
            assertEquals("They are hot.", print(item(2), "They {is} hot.", false));
        }

        /**
         * So does no object, C's {@code (!obj) || (obj->number > 1)}.
         */
        @Test
        @DisplayName("{is} becomes are when there is no object")
        void isWithoutObject() {
            assertEquals("They are hot.", print(item(1), "They {is} hot.", true));
        }

        /**
         * Two tags in one message, which is the case that catches a tag consuming the wrong
         * number of characters: the second substitution only lands if the first left the loop
         * pointing just past its own closing brace.
         */
        @Test
        @DisplayName("two tags in one message are both substituted")
        void twoTags() {
            assertEquals("Your hands are hot.", print(item(3), "Your {name} {is} hot.", true));
        }

        /**
         * Two of the same tag, for the same reason - the verb ending is the one whose consumption
         * is not paired with its substitution.
         */
        @Test
        @DisplayName("a repeated tag is substituted every time")
        void repeatedTag() {
            assertEquals("It hits and misses.",
                    print(item(1), "It hit{s} and mis{s}es.", false));
        }

        /**
         * A message with no tags at all is passed through unchanged - the loop never runs.
         */
        @Test
        @DisplayName("a message with no tags is passed through")
        void noTags() {
            assertEquals("Nothing happens.", print(item(1), "Nothing happens.", false));
        }

        /**
         * The type the caller asks for travels with the message, since it is what lets the
         * front-end colour the line and sound it.
         */
        @Test
        @DisplayName("the message carries the type it was given")
        void typeIsCarried() {
            item(1).printCustomMessage("It glows.", MessageType.MSG_QUAFF, null, false);

            assertEquals(List.of(MessageType.MSG_QUAFF), bus.types);
        }
    }

    /**
     * Braces the data files should never contain. C's loop has an answer for each of them, and
     * none of the answers is a crash, so each is worth pinning.
     */
    @Nested
    @DisplayName("malformed templates")
    class Malformed {

        /**
         * There is no message at all when there is no template. C is called with the {@code msg:}
         * field of a property that need not declare one.
         */
        @Test
        @DisplayName("a null template prints nothing")
        void nullTemplatePrintsNothing() {
            item(1).printCustomMessage(null, MessageType.MSG_GENERIC, null, false);

            assertTrue(bus.messages.isEmpty());
        }

        /**
         * An unrecognised tag is dropped along with its braces, which is C's {@code default} arm
         * falling through to the string already advanced past the brace.
         */
        @Test
        @DisplayName("an unknown tag is dropped, braces and all")
        void unknownTagIsDropped() {
            assertEquals("It is  hot.", print(item(1), "It is {zzz} hot.", false));
        }

        /**
         * An unknown tag at the very end of the string is the case that runs the loop off the end
         * of the text, so it is the one that would throw if the tag consumed a character too many.
         */
        @Test
        @DisplayName("an unknown tag at the end of the string is dropped")
        void unknownTagAtEndIsDropped() {
            assertEquals("It is ", print(item(1), "It is {zzz}", false));
        }

        /**
         * Empty braces are an unknown tag with nothing in them, and go the same way.
         */
        @Test
        @DisplayName("empty braces are dropped")
        void emptyBracesAreDropped() {
            assertEquals("It is  hot.", print(item(1), "It is {} hot.", false));
        }

        /**
         * A brace that never closes is dropped and everything after it kept, because C resumes
         * from the character after the brace and finds no further brace to act on.
         */
        @Test
        @DisplayName("an unclosed brace running to the end of the string is dropped")
        void unclosedBraceToEndOfString() {
            assertEquals("a character", print(item(1), "a {character", false));
        }

        /**
         * The other way a tag can fail to close: the scan stops at a character that is neither a
         * letter nor a closing brace. The text is kept whole here too, spaces included.
         */
        @Test
        @DisplayName("an unclosed brace stopped by a non-letter is dropped")
        void unclosedBraceStoppedByNonLetter() {
            assertEquals("a char acter", print(item(1), "a {char acter", false));
        }

        /**
         * An unclosed brace does not swallow the tags after it - the loop resumes inside the text
         * it kept and goes on substituting.
         */
        @Test
        @DisplayName("a later tag still works after an unclosed brace")
        void tagAfterUnclosedBrace() {
            assertEquals("a char is hot", print(item(1), "a {char {is} hot", false));
        }

        /**
         * A closing brace on its own is ordinary text, since the loop only ever looks for the
         * opening one.
         */
        @Test
        @DisplayName("a lone closing brace is ordinary text")
        void loneClosingBrace() {
            assertEquals("It is } hot.", print(item(1), "It is } hot.", false));
        }
    }

    /**
     * The two places this deliberately does not match the C. Neither is reachable from the shipped
     * data files, and both are pinned here so a later change to either has to be a decision.
     */
    @Nested
    @DisplayName("divergences from the C")
    class Divergences {

        /**
         * Tag lookup matches the whole tag, where C's {@code msg_tag_lookup} uses {@code strncmp}
         * on its opening letters alone. C would read this as {@code {name}} and print the object's
         * description; here it is an unknown tag and is dropped.
         */
        @Test
        @DisplayName("a tag with a valid prefix is not the tag, where C says it is")
        void prefixIsNotAMatch() {
            assertEquals("Your  glows.", print(item(1), "Your {names} glows.", false));
        }

        /**
         * The same for the one-letter verb tag, where the prefix is easiest to hit by accident.
         */
        @Test
        @DisplayName("a longer tag beginning with s is not the verb ending")
        void verbPrefixIsNotAMatch() {
            assertEquals("It glow brightly.", print(item(1), "It glow{so} brightly.", false));
        }

        /**
         * There is no length limit. C builds the message in a 1024-byte buffer and truncates
         * silently at it; this builds a string, so a template longer than that survives whole.
         */
        @Test
        @DisplayName("a message longer than C's buffer is not truncated")
        void noTruncation() {
            String filler = "x".repeat(2000);

            assertEquals(filler + " is hot.", print(item(1), filler + " {is} hot.", false));
        }
    }
}
