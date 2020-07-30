package com.music.commands.impl;

import com.music.audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Created by Proxy on 29.07.2020.
 */
public class AddTrackAudioCommandImpl implements Command {

    private final TrackScheduler scheduler;
    private final AudioPlayerManager playerManager;

    public AddTrackAudioCommandImpl(TrackScheduler scheduler, AudioPlayerManager playerManager) {
        this.scheduler = scheduler;
        this.playerManager = playerManager;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(command -> playerManager.loadItem(command.get(1), scheduler))
                .then();
    }
}