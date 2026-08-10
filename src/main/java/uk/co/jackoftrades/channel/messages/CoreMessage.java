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

package uk.co.jackoftrades.channel.messages;

import uk.co.jackoftrades.channel.enums.CoreLIfecycleEvent;
import uk.co.jackoftrades.channel.enums.GameEventType;

public sealed interface CoreMessage extends ChannelMessage permits CoreMessage.SimpleCoreMessage,
        CoreMessage.TextCoreMessage, CoreMessage.StoppedCoreMessage, CoreMessage.LifecycleCoreMessage {
    record SimpleCoreMessage(GameEventType gameEventType) implements CoreMessage {
    }

    record TextCoreMessage(GameEventType gameEventType, String message) implements CoreMessage {
    }

    record StoppedCoreMessage() implements CoreMessage {
    }

    record LifecycleCoreMessage(CoreLIfecycleEvent event) implements CoreMessage {
    }
}
