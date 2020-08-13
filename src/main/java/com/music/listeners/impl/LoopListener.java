package com.music.listeners.impl;

import com.music.listeners.Listener;
import discord4j.core.object.entity.Message;

/**
 * Created by Proxy on 09.08.2020.
 */
public class LoopListener implements Listener {

    @Override
    public void execute(Message message, String title) {
        message.edit(spec ->
                spec.setEmbed(newEmbed ->
                        newEmbed.setTitle(title)
                                .setDescription(
                                        "p - loop playlist \n" +
                                        "t - loop track \n" +
                                        "n - loop off"
                                )))
                .subscribe();
    }
}
