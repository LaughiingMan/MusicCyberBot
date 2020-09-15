package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Proxy on 15.08.2020.
 */
@Data
public class QueueTracksCommandImpl implements Command {

    private final TrackScheduler scheduler;
    private String message;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .doOnNext(command -> fillQueue())
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> {
                    Message text = channel.createEmbed(embed -> embed
                            .setTitle("List")
                            .setDescription(message)
                            .setFooter("Number of tracks: " + scheduler.getCountTracks(), null))
                            .block();
                    Objects.requireNonNull(text)
                            .addReaction(ReactionEmoji.unicode("\uD83D\uDD3C"))
                            .subscribe();
                    return Objects.requireNonNull(text)
                            .addReaction(ReactionEmoji.unicode("\uD83D\uDD3D"))
                            .then();
                })
                .then();
    }

    private void fillQueue() {
        List<String> playlists = scheduler.getPlaylists();

        Optional<String> currentTrack = playlists.stream()
                .filter(trackName -> trackName.contains("\uD83C\uDFB5"))
                .findFirst();

        int range = currentTrack.isPresent() && playlists.indexOf(currentTrack.get()) > 4 ? playlists.indexOf(currentTrack.get()) - 5 : 0;

        message = scheduler.getQueue(range);
    }
}
