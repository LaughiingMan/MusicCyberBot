package com.music.commands.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 29.07.2020.
 */
public class DestroyPlayerCommandImpl implements Command {

    private final AudioPlayerManager playerManager;

    public DestroyPlayerCommandImpl(AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .doOnNext(command -> playerManager.shutdown())
                .then();
    }
}
