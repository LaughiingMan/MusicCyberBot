package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

/**
 * Created by Proxy on 02.08.2020.
 */
public class ClearTracksCommandImpl implements Command {

    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private String title;
    private String message;

    public ClearTracksCommandImpl(AudioPlayer player, TrackScheduler scheduler) {
        this.player = player;
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .doOnNext(command -> clear())
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(embed -> embed.setTitle(title).setDescription(message)).then())
                .then();
    }

    private void clear() {
        scheduler.removePlayerListener();
        scheduler.cleanPlaylists();
        player.destroy();
        title = "Cleared";
        message = "Playlist cleared";
    }
}
