package com.music.commands.impl.chat;

import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Created by Proxy on 29.07.2020.
 */
public class MessageChatCommandImpl implements Command {

    private static final String ROFL = "Тамур даун!";

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage(new String(ROFL.getBytes(), StandardCharsets.UTF_8)))
                .then();
    }
}
