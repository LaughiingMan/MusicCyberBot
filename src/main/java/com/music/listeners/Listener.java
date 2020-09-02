package com.music.listeners;

import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.Message;

/**
 * Created by Proxy on 09.08.2020.
 */
public interface Listener {

    void execute(Message message, String title, Event event);
}
