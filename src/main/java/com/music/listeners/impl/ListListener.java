package com.music.listeners.impl;

import com.music.listeners.Listener;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.Message;

/**
 * Created by Proxy on 16.08.2020.
 */
public class ListListener implements Listener {

    @Override
    public void execute(Message message, String title, Event event) {
        String description = "";
        switch (event.getClass().getSimpleName()) {
            case "ReactionAddEvent":
                description = "up";
                break;
            case "ReactionRemoveEvent":
                description = "down";
                break;
        }
        message.edit(spec ->
                spec.setEmbed(newEmbed ->
                        newEmbed.setTitle(title)
                                .setDescription(
                                        "p - loop playlist \n" +
                                                "t - loop track \n" +
                                                "n - loop off"
                                )))
                .subscribe();
    }
}
