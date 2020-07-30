package com.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import java.util.Iterator;

/**
 * Created by Proxy on 29.07.2020.
 */
public class TrackScheduler implements AudioLoadResultHandler {

    private final AudioPlayer player;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        player.startTrack(track,true);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        Iterator<AudioTrack> iterator = playlist.getTracks().iterator();
        AudioTrack currentTrack = iterator.next();
        player.startTrack(currentTrack,true);
        while (iterator.hasNext()) {
            if (currentTrack.getState() == AudioTrackState.FINISHED) {
                currentTrack = iterator.next();
                player.startTrack(currentTrack,true);
            }
        }
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException exception) {

    }
}
