package com.music.commands.impl.chat;

import com.music.MusicBotConfig;
import com.music.commands.Command;
import com.music.utils.StringUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Proxy on 24.08.2020.
 */
@RequiredArgsConstructor
public class SuperUserChatCommandImpl implements Command {

    private static final String STOP = "Пнх! Не имеешь права трогать эту команду!";

    @NonNull private final MusicBotConfig config;

    private String title;
    private String message;

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        Optional<User> user = event.getMessage().getAuthor();

        if (user.isPresent() && !config.getSuperUser().isEmpty() && !config.getSuperUser().contains(user.get().getId().asString())) {
            return event.getMessage().getChannel()
                    .flatMap(channel -> channel.createMessage(new String(STOP.getBytes(), StandardCharsets.UTF_8)))
                    .then();
        }

        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(this::checkSuperUser)
                .then(message(event));
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createEmbed(embed -> embed.setTitle(title).setDescription(message)).then())
                .then();
    }

    private void checkSuperUser(List<String> command) {
        switch (StringUtil.hasIndex(command,1)) {
            case "on":
                title = "WTF";
                message = "Super user enable";
                config.setSuperUser(true);
                break;
            case "off":
                title = "PHEW";
                message = "Super user disabled";
                config.setSuperUser(false);
                break;
            default:
                title = "Invalid command";
                message = "on - enable super user \n" +
                        "off - disable super user \n";
                break;
        }
    }
}
