package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Proxy on 04.08.2020.
 */
public class JumpTrackCommandImpl implements Command {

    private final TrackScheduler scheduler;
    private String title;
    private String message;

    public JumpTrackCommandImpl(TrackScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(this::checkJump)
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(embed -> embed.setTitle(title).setDescription(message)).then())
                .then();
    }

    private void checkJump(List<String> command) {
        if (command.size() > 1) {
            title = "Jumped";
            message = scheduler.jumpTrack(Integer.parseInt(command.get(1)) - 1);
        } else {
            title = "Invalid jump";
            message = "Enter track number";
        }
    }
}
