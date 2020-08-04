package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Created by Proxy on 04.08.2020.
 */
public class JumpTrackCommandImpl implements Command {

    private final TrackScheduler scheduler;

    public JumpTrackCommandImpl(TrackScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(command -> scheduler.jumpTrack(Integer.parseInt(command.get(1)) - 1))
                .then();
    }
}
