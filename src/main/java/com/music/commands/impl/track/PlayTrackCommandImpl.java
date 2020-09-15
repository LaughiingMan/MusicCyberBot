package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.Data;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 16.09.2020.
 */
@Data
public class PlayTrackCommandImpl implements Command {

    private final TrackScheduler scheduler;
    private final AudioPlayer player;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .doOnNext(command -> player.setPaused(false))
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(embed -> embed
                        .setTitle("Play")
                        .setDescription(scheduler.currentTrack()))
                        .then())
                .then();
    }

}
