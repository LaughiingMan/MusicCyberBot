package com.music.commands.impl.track;

import com.music.audio.TrackScheduler;
import com.music.commands.Command;
import com.music.utils.StringUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by Proxy on 06.08.2020.
 */
@Data
public class LoopTrackCommandImpl implements Command {

    private final TrackScheduler scheduler;
    private String title;
    private String message;
    private String emoji;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(this::checkLoop)
                .then(message(event));
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

    private void checkLoop(List<String> command) {
        String loop = StringUtil.hasIndex(command,1);
        message = "Loop installed";
        switch (loop) {
            case "p":
                scheduler.setLoop(loop);
                title = "Loop playlist";
                emoji = "\uD83D\uDD01";
                break;
            case "t":
                scheduler.setLoop(loop);
                title = "Loop track";
                emoji = "\uD83D\uDD02";
                break;
            case "n":
                scheduler.setLoop(loop);
                title = "Loop off";
                emoji = "\uD83D\uDEAB";
                break;
            default:
                title = "Invalid loop";
                message = "Click the button for more information.";
                emoji = "\uD83D\uDCDD";
                break;
        }
    }
}
