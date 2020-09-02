package com.music.utils;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import lombok.experimental.UtilityClass;

/**
 * Created by Proxy on 31.08.2020.
 */
@UtilityClass
public class EventUtil {

    public User getUser(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ReactionAddEvent":
                return ((ReactionAddEvent)event).getUser().block();
            case "ReactionRemoveEvent":
                return ((ReactionRemoveEvent)event).getUser().block();
        }
        return null;
    }

    public Message getMessage(Event event) {
        switch (event.getClass().getSimpleName()) {
            case "ReactionAddEvent":
                return ((ReactionAddEvent)event).getMessage().block();
            case "ReactionRemoveEvent":
                return ((ReactionRemoveEvent)event).getMessage().block();
        }
        return null;
    }
}
