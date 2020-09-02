package com.music.commands.impl.chat;

import com.music.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Created by Proxy on 29.07.2020.
 */
public class HelpChatCommandImpl implements Command {

    private static final String HELP_MESSAGE = "Введите -join, чтобы подключить меня на канал ублюдки тупые!\n" +
            "Введите -add (ссылка из ютуб), чтобы добавить песню или плейлист.\n" +
            "Введите -jump (номер трека в плейлисте), чтобы воспроизвести нужную тебе песню.\n" +
            "Введите -next, чтобы воспроизвести следующую песню.\n" +
            "Введите -previous, чтобы воспроизвести предыдущую песню.\n" +
            "Введите -loop ('p' - повтор плейлиста, 't' - повтор трека, 'n' - отключить повтор), чтобы настроить повтор.\n" +
            "Введите -remove (номер трека или номера треков через запятую (3,5,8) или через тире (1-5)), чтобы удалить треки.\n" +
            "Введите -clear, чтобы очистить плейлист.\n" +
            "Введите -exit, чтобы послать меня нахуй.\n" +
            "Не забудь убрать скобки и возможно кавычки долбаёб!";

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return event.getMessage().getChannel()
                .flatMap(channel ->
                        channel.createEmbed(embed ->
                                embed.setTitle(new String("Привет сучки!".getBytes(), StandardCharsets.UTF_8))
                                        .setDescription(new String(HELP_MESSAGE.getBytes(), StandardCharsets.UTF_8))))
                .then();
    }

    @Override
    public Mono<Void> message(MessageCreateEvent event) {
        return null;
    }
}
