package com.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Proxy on 29.07.2020.
 */
@Slf4j
@Data
public class TrackScheduler implements AudioLoadResultHandler {

    private final List<AudioTrack> playlists = new ArrayList<>();
    private final AudioPlayer player;
    private AudioEventListener audioListener;
    private String loop = "n";
    private int count = 0;

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
                            count = -1;
                        }
                        addTrack();
                        break;
                    case "t":
                        if (player.getPlayingTrack() == null) {
                            player.playTrack(playlists.get(count).makeClone());
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
            player.startTrack(playlists.get(count).makeClone(), true);
        }
    }

    public String jumpTrack(int index) {
        if (playlists.size() > index && !(index < 0)) {
            count = index;
            player.playTrack(playlists.get(count).makeClone());
            AudioTrackInfo info = player.getPlayingTrack().getInfo();
            return info.title + "\n" + info.uri;
        }
        return "Track missing";
    }

    public void removePlayerListener() {
        if (audioListener != null) {
            player.removeListener(audioListener);
            audioListener = null;
            count = 0;
        }
    }

    public List<String> getPlaylists() {
        return playlists.stream()
                .map(track -> {
                    long index = playlists.indexOf(track) + 1;
                    if (index == count + 1) {
                        return String.format("%s. \uD83C\uDFB5 %s \uD83C\uDFB5", index, track.getInfo().title);
                    }
                    return String.format("%s. %s", index, track.getInfo().title);
                })
                .collect(Collectors.toList());
    }

    public void clearPlaylists() {
        playlists.clear();
    }
}
