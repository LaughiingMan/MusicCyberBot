package com.music.events;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 18.08.2020.
 */
public interface GatewayEvent {

    Mono<Void> event(Event event);
}
