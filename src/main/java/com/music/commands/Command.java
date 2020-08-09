package com.music.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 29.07.2020.
 */
public interface Command {

    Mono<Void> execute(MessageCreateEvent event);

    Mono<Void> message(MessageCreateEvent event);
}
