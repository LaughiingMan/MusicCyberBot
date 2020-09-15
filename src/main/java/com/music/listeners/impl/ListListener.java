package com.music.listeners.impl;

import com.music.audio.TrackScheduler;
import com.music.listeners.Listener;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Data;

import java.util.Optional;

/**
 * Created by Proxy on 16.08.2020.
 */
@Data
public class ListListener implements Listener {

    private final TrackScheduler scheduler;
    private String description;
    private int rangeTrack;

    @Override
    public void execute(Message message, String title, Event event) {
        message.getEmbeds().get(0).getDescription().ifPresent(item -> rangeTrack = Integer.parseInt(item.split("\\.")[0]));

        switch (event.getClass().getSimpleName()) {
            case "ReactionAddEvent":
                ReactionAddEvent addEvent = (ReactionAddEvent) event;
                checkEmoji(addEvent.getEmoji());
                break;
            case "ReactionRemoveEvent":
                ReactionRemoveEvent removeEvent = (ReactionRemoveEvent) event;
                checkEmoji(removeEvent.getEmoji());
                break;
        }

        if (!description.isEmpty()) {
            message.edit(spec ->
                    spec.setEmbed(newEmbed ->
                            newEmbed.setTitle(title)
                                    .setDescription(description)
                                    .setFooter("Number of tracks: " + scheduler.getCountTracks(), null)))
                    .subscribe();
        }
    }

    private void checkEmoji(ReactionEmoji emoji) {
        Optional<ReactionEmoji.Unicode> item = emoji.asUnicodeEmoji();
        if (item.isPresent() && item.get().equals(ReactionEmoji.unicode("\uD83D\uDD3C"))) {
            description = scheduler.getQueue(rangeTrack - 11);
        } else {
            description = scheduler.getQueue(rangeTrack + 9);
        }
    }
}
