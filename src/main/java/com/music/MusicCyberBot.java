package com.music;

import com.music.audio.LavaPlayerAudioProvider;
import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import com.music.commands.impl.audio.ExitAudioCommandImpl;
import com.music.commands.impl.audio.JoinAudioCommandImpl;
import com.music.commands.impl.chat.HelpChatCommandImpl;
import com.music.commands.impl.chat.MessageChatCommandImpl;
import com.music.commands.impl.chat.SuperUserChatCommandImpl;
import com.music.commands.impl.track.*;
import com.music.events.GatewayEvent;
import com.music.events.impl.MessageCreateEventImpl;
import com.music.events.impl.ReactionAddEventImpl;
import com.music.listeners.Listener;
import com.music.listeners.impl.ListListener;
import com.music.listeners.impl.LoopListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.voice.AudioProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Proxy on 29.07.2020.
 */
@Slf4j
public class MusicCyberBot {

    private static final Map<String, GatewayEvent> events = new HashMap<>();
    private static final Map<String, Command> commands = new HashMap<>();
    private static final Map<String, Listener> listeners = new HashMap<>();
    private static final AudioPlayerManager playerManager;
    private static final AudioPlayer player;
    private static final AudioProvider provider;
    private static final TrackScheduler scheduler;
    private static final MusicBotConfig config;
    private static final DiscordClient client;
    private static final GatewayDiscordClient gateway;
    private static boolean isSuperUser;

    static {
        config = new MusicBotConfig("application.properties");
        client = DiscordClient.create(config.getToken());
        gateway = client.login().block();
        playerManager = new DefaultAudioPlayerManager();

        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);

        player = playerManager.createPlayer();
        provider = new LavaPlayerAudioProvider(player);
        scheduler = new TrackScheduler(player);

        addEvents();
        addListeners();
        addChatCommand();
        addAudioCommand();
        addTrackCommand();
    }

    public static void main(String[] args) {
        gateway.on(MessageCreateEvent.class)
                .flatMap(event -> events.get(event.getClass().getSimpleName()).event(event))
                .subscribe();
        gateway.on(ReactionAddEvent.class)
                .doOnNext(event -> events.get(event.getClass().getSimpleName()).event(event))
                .subscribe();
        gateway.onDisconnect().block();
    }

    private static void addEvents() {
        events.put("MessageCreateEvent", new MessageCreateEventImpl(config, commands));
        events.put("ReactionAddEvent", new ReactionAddEventImpl(listeners));
    }

    private static void addChatCommand() {
        commands.put("rofl", new MessageChatCommandImpl());
        commands.put("help", new HelpChatCommandImpl());
        commands.put("superUser", new SuperUserChatCommandImpl(config));
    }

    private static void addAudioCommand() {
        commands.put("join", new JoinAudioCommandImpl(provider));
        commands.put("exit", new ExitAudioCommandImpl());
    }

    private static void addTrackCommand() {
        commands.put("add", new AddTrackCommandImpl(scheduler, playerManager));
        commands.put("clear", new ClearTracksCommandImpl(player, scheduler));
        commands.put("jump", new JumpTrackCommandImpl(scheduler));
        commands.put("loop", new LoopTrackCommandImpl(scheduler));
        commands.put("queue", new QueueTracksCommandImpl(scheduler));
    }

    private static void addListeners() {
        listeners.put("Invalid loop", new LoopListener());
        listeners.put("List", new ListListener());
    }
}
