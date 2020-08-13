package com.music.commands.impl.audio;

import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 29.07.2020.
 */
public class JoinAudioCommandImpl implements Command {

    private final AudioProvider provider;

    public JoinAudioCommandImpl(final AudioProvider provider) {
        this.provider = provider;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)))
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel ->
                        channel.createEmbed(embed ->
                                embed.setTitle("Hey")
                                        .setDescription(
                                                "I am connected\n" +
                                                "Enter the command:\n" +
                                                "-help"
                                        ))
                                .then())
                .then();
    }
}
