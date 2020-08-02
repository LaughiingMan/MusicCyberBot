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

    private void startTrack() {
        if (audioListener == null) {
            player.startTrack(playlists.get(count), true);
            audioListener = event -> {
                if (player.getPlayingTrack() == null) {
                    count++;
                    player.startTrack(playlists.get(count), true);
                }
            };
            player.addListener(audioListener);
        }
    }

    public void removePlayerListener() {
        if (audioListener != null) {
            player.removeListener(audioListener);
            audioListener = null;
        }
    }

    public void cleanPlaylists() {
        playlists.clear();
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException exception) {

    }
}
