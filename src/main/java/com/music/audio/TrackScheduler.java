package com.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Proxy on 29.07.2020.
 */
public class TrackScheduler implements AudioLoadResultHandler {

    private static Logger logger = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer player;
    private final List<AudioTrack> playlists;
    private AudioEventListener audioListener;
    private int count = 0;
    private String loop = "n";

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        playlists = new ArrayList<>();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        playlists.add(track);
        startTrack();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        playlists.addAll(playlist.getTracks());
        startTrack();
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException exception) {

    }

    private void startTrack() {
        if (audioListener == null) {
            player.startTrack(playlists.get(count), true);
            audioListener = event -> {
                switch (getLoop()) {
                    case "p":
                        if (playlists.size() <= count + 1) {
                            count = 0;
                        }
                        addTrack();
                        break;
                    case "t":
                        if (player.getPlayingTrack() == null) {
                            player.playTrack(playlists.get(count));
                        }
                        break;
                    case "n":
                        addTrack();
                        break;
                }
            };
            player.addListener(audioListener);
        } else {
            addTrack();
        }
    }

    private void addTrack() {
        if (player.getPlayingTrack() == null && playlists.size() > count + 1) {
            count++;
            player.startTrack(playlists.get(count), true);
        }
    }

    public void jumpTrack(int index) {
        if (playlists.size() > index) {
            count = index;
            player.playTrack(playlists.get(count));
        }
    }

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }

    public void removePlayerListener() {
        if (audioListener != null) {
            player.removeListener(audioListener);
            audioListener = null;
            count = 0;
        }
    }

    public void cleanPlaylists() {
        playlists.clear();
    }
}
