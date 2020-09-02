package com.music.events.impl;

import com.music.MusicBotConfig;
import com.music.commands.Command;
import com.music.events.GatewayEvent;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Proxy on 18.08.2020.
 */
@Data
public class MessageCreateEventImpl implements GatewayEvent {

    private static final String STOP = "Пнх! Не имеешь права трогать меня!";

    private final MusicBotConfig config;
    private final Map<String, Command> commands;

    @Override
    public Mono<Void> event(Event event) {
        MessageCreateEvent messageCreateEvent = (MessageCreateEvent) event;
        return Mono.just(messageCreateEvent.getMessage().getContent())
                .flatMap(content -> Flux.fromIterable(commands.entrySet())
                        .filter(entry -> content.startsWith(config.getPrefix() + entry.getKey()))
                        .flatMap(entry -> getExecute(messageCreateEvent, entry))
                        .next());
    }

    private Mono<Void> getExecute(MessageCreateEvent messageCreateEvent, Map.Entry<String, Command> entry) {
        if (config.isSuperUser()) {
            Optional<User> user = messageCreateEvent.getMessage().getAuthor();
            if (user.isPresent() && !config.getSuperUser().isEmpty() && !config.getSuperUser().contains(user.get().getId().asString())) {
                return messageCreateEvent.getMessage().getChannel()
                        .flatMap(channel -> channel.createMessage(new String(STOP.getBytes(), StandardCharsets.UTF_8)))
                        .then();
            }
        }
        return entry.getValue().execute(messageCreateEvent);
    }
}
