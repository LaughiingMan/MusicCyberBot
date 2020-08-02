package com.music;

import com.music.audio.LavaPlayerAudioProvider;
import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import com.music.commands.impl.*;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Proxy on 29.07.2020.
 */
public class MusicCyberBot {

    private static final Map<String, Command> commands = new HashMap<>();
    private static final AudioPlayerManager playerManager;
    private static final MusicBotConfig config;
    private static final DiscordClient client;
    private static final GatewayDiscordClient gateway;

    static {
        playerManager = new DefaultAudioPlayerManager();
        config = new MusicBotConfig("application.properties");
        client = DiscordClient.create(config.getToken());
        gateway = client.login().block();
        commands.put("rofl", new MessageCommandImpl());
        commands.put("help", new HelpCommandImpl());
    }

    public static void main(String[] args) {
        audioChannel();
        messageChannel();
        gateway.onDisconnect().block();
    }

    private static void messageChannel() {
        gateway.on(MessageCreateEvent.class)
                .flatMap(event -> Mono.just(event.getMessage().getContent())
                        .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                .filter(entry -> content.startsWith('-' + entry.getKey()))
                                .flatMap(entry -> entry.getValue().execute(event))
                                .next()))
                .subscribe();
    }

    private static void audioChannel() {
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
        final AudioPlayer player = playerManager.createPlayer();
        final AudioProvider provider = new LavaPlayerAudioProvider(player);
        final TrackScheduler scheduler = new TrackScheduler(player);
        commands.put("join", new JoinAudioCommandImpl(provider));
        commands.put("add", new AddAudioCommandImpl(scheduler, playerManager));
        commands.put("destroy", new DestroyPlayerCommandImpl(playerManager));
        commands.put("clear", new ClearAudioCommandImpl(player, scheduler));
    }
}
