package com.music.events.impl;

import com.music.events.GatewayEvent;
import com.music.listeners.Listener;
import com.music.utils.EventUtil;
import discord4j.core.event.domain.Event;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Created by Proxy on 18.08.2020.
 */
@Data
public class ReactionEventsImpl implements GatewayEvent {

    private final Map<String, Listener> listeners;

    @Override
    public Mono<Void> event(Event event) {
        User user = EventUtil.getUser(event);
        Message message = EventUtil.getMessage(event);
        if (user != null && message != null && !message.getEmbeds().isEmpty()) {
            Embed embed = message.getEmbeds().get(0);
            message.getAuthor().ifPresent(author -> {
                if (author.isBot() && !user.isBot()) {
                    embed.getTitle().ifPresent(title -> {
                        Listener listener = listeners.get(title);
                        if (listener != null) {
                            listener.execute(message, title, event);
                        }
                    });
                }
            });
        }
        return null;
    }
}
