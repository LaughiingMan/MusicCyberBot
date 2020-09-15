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

    public void nextTrack() {
        if (playlists.size() > count + 1) {
            count++;
            player.playTrack(playlists.get(count).makeClone());
        } else {
            count = 0;
            player.playTrack(playlists.get(count).makeClone());
        }
    }

    public void previousTrack() {
        if (count - 1 == -1) {
            count = playlists.size() - 1;
            player.playTrack(playlists.get(count).makeClone());
        } else {
            count--;
            player.playTrack(playlists.get(count).makeClone());
        }
    }

    public String currentTrack() {
        AudioTrackInfo info = player.getPlayingTrack().getInfo();
        return info.title + "\n" + info.uri;
    }

    public void removeTrack(List<String> numbers) {
        numbers.forEach(item -> {
            if (item.contains("-")) {
                String[] multipleNumbers = item.split("-");
                int n1 = Integer.parseInt(multipleNumbers[0]);
                int n2 = Integer.parseInt(multipleNumbers[1]);
                for (int i = n1 - 1; i < n2; i++) {
                    checkCount(n1 - 1);
                    playlists.remove(n1 - 1);
                }
            } else {
                int number = Integer.parseInt(item);
                checkCount(number - 1);
                playlists.remove(number - 1);
            }
        });
    }

    private void checkCount(int number) {
        if (number < count) count--;
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

    public String getQueue(int range) {
        if (range < 0) range = 0;

        StringBuilder builder = new StringBuilder();
        List<String> queue = getPlaylists();

        for (int i = range; i < range + 10; i++) {
            if (i >= queue.size()) break;
            builder.append(queue.get(i)).append("\n");
        }

        return builder.toString();
    }

    public int getCountTracks() {
        return getPlaylists().size();
    }
}
