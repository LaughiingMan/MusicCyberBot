package com.music;

import com.music.audio.LavaPlayerAudioProvider;
import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import com.music.commands.impl.audio.DestroyAudioCommandImpl;
import com.music.commands.impl.audio.JoinAudioCommandImpl;
import com.music.commands.impl.chat.HelpChatCommandImpl;
import com.music.commands.impl.chat.MessageChatCommandImpl;
import com.music.commands.impl.track.*;
import com.music.listeners.Listener;
import com.music.listeners.impl.EmojiListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Proxy on 29.07.2020.
 */
public class MusicCyberBot {

    private static final Map<String, Command> commands = new HashMap<>();
    private static final Map<String, Listener> listeners = new HashMap<>();
    private static final AudioPlayerManager playerManager;
    private static final MusicBotConfig config;
    private static final DiscordClient client;
    private static final GatewayDiscordClient gateway;

    static {
        playerManager = new DefaultAudioPlayerManager();
        config = new MusicBotConfig("application.properties");
        client = DiscordClient.create(config.getToken());
        gateway = client.login().block();
        addListeners();
    }

    public static void main(String[] args) {
        audioChannel();
        messageChannel();
    }

    private static void audioChannel() {
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
        final AudioPlayer player = playerManager.createPlayer();
        final AudioProvider provider = new LavaPlayerAudioProvider(player);
        final TrackScheduler scheduler = new TrackScheduler(player);
        addAudioCommand(provider);
        addTrackCommand(player, scheduler);
    }

    private static void messageChannel() {
        addChatCommand();
        gateway.on(MessageCreateEvent.class)
                .flatMap(event -> Mono.just(event.getMessage().getContent())
                        .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                .filter(entry -> content.startsWith(config.getPrefix() + entry.getKey()))
                                .flatMap(entry -> entry.getValue().execute(event))
                                .next()))
                .subscribe();
        gateway.on(ReactionAddEvent.class)
                .doOnNext(event -> {
                    User user = event.getUser().block();
                    Message message = event.getMessage().block();
                    if (user != null && message != null && !message.getEmbeds().isEmpty()) {
                        Embed embed = message.getEmbeds().get(0);
                        message.getAuthor().ifPresent(author -> {
                            if (author.isBot() && !user.isBot()) {
                                embed.getTitle().ifPresent(title -> {
                                    Listener listener = listeners.get(title);
                                    if (listener != null) {
                                        listener.execute(message, title);
                                    }
                                });
                            }
                        });
                    }
                })
                .subscribe();
        gateway.onDisconnect().block();
    }

    private static void addChatCommand() {
        commands.put("rofl", new MessageChatCommandImpl());
        commands.put("help", new HelpChatCommandImpl());
    }

    private static void addAudioCommand(AudioProvider provider) {
        commands.put("join", new JoinAudioCommandImpl(provider));
        commands.put("destroy", new DestroyAudioCommandImpl(playerManager));
    }

    private static void addTrackCommand(AudioPlayer player, TrackScheduler scheduler) {
        commands.put("add", new AddTrackCommandImpl(scheduler, playerManager));
        commands.put("clear", new ClearTracksCommandImpl(player, scheduler));
        commands.put("jump", new JumpTrackCommandImpl(scheduler));
        commands.put("loop", new LoopTrackCommandImpl(scheduler));
    }

    private static void addListeners() {
        listeners.put("Invalid loop", new EmojiListener());
    }
}
