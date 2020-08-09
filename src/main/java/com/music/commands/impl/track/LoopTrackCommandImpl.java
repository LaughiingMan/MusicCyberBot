package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by Proxy on 06.08.2020.
 */
public class LoopTrackCommandImpl implements Command {

    private final TrackScheduler scheduler;
    private String title;
    private String message;
    private String emoji;

    public LoopTrackCommandImpl(TrackScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(this::checkLoop)
                .then(message(event));
    }

    private void checkLoop(List<String> command) {
        if (command.size() > 1) {
            scheduler.setLoop(command.get(1));
            title = "Loop installed";
            message = "Success";
            switch (scheduler.getLoop()) {
                case "p":
                    emoji = "\uD83D\uDD01";
                    break;
                case "t":
                    emoji = "\uD83D\uDD02";
                    break;
                case "n":
                    emoji = "\uD83D\uDEAB";
                    break;
                default:
                    title = "Invalid loop";
                    message = "Click the button for more information.";
                    emoji = "\uD83D\uDCDD";
                    break;
            }
        } else {
            title = "Invalid loop";
            message = "Click the button for more information.";
            emoji = "\uD83D\uDCDD";
        }
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> {
                    Message text = channel.createEmbed(embed -> embed.setTitle(title)
                            .setDescription(message))
                            .block();
                    return Objects.requireNonNull(text)
                            .addReaction(ReactionEmoji.unicode(emoji))
                            .then();
                })
                .then();
    }
}
