package com.music.commands.impl;

import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Created by Proxy on 29.07.2020.
 */
public class HelpCommandImpl implements Command {

    private static final String HELP_MESSAGE = "Привет сучки!\n" +
            "Введите -join, чтобы подключить меня на канал ублюдки тупые!\n" +
            "Введите -play (ссылка из ютуб), чтобы добавить песню или плейлист.\n" +
            "Не забудь убрать скобки долбаёб!";

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage(new String(HELP_MESSAGE.getBytes(), StandardCharsets.UTF_8)))
                .then();
    }
}
