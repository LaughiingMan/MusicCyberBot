package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Proxy on 03.09.2020.
 */
@Data
public class RemoveTrackCommandImpl implements Command {

    private final TrackScheduler scheduler;
    private String title;
    private String message;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(this::checkRemove)
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(embed -> embed
                        .setTitle(title)
                        .setDescription(message))
                        .then())
                .then();
    }

    private void checkRemove(List<String> command) {
        if (command.size() == 2) {
            List<String> numbers = Arrays.asList(command.get(1).split(","));
            scheduler.removeTrack(numbers);
            title = "Removed";
            message = "Remove tracks: " + numbers.toString();
        } else if (command.size() > 2) {
            title = "Invalid remove";
            message = "Remove extra spaces";
        } else {
            title = "Invalid remove";
            message = "Enter track number";
        }
    }
}
