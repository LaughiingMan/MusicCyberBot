package com.music.commands.impl.audio;

import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 29.07.2020.
 */
public class DestroyAudioCommandImpl implements Command {

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(VoiceChannel::sendDisconnectVoiceState)
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(embed -> embed.setTitle("Exit").setDescription("WTF")).then())
                .then();
    }
}
