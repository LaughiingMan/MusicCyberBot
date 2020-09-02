package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Data;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 02.09.2020.
 */
@Data
public class NextTrackCommandImpl implements Command {

    private final TrackScheduler scheduler;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .doOnNext(command -> scheduler.nextTrack())
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(embed -> embed
                        .setTitle("Track")
                        .setDescription(scheduler.currentTrack()))
                        .then())
                .then();
    }
}
