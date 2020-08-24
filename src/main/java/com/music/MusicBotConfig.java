package com.music;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Proxy on 29.07.2020.
 */
@Getter
public class MusicBotConfig {

    @Setter private boolean isSuperUser;

    private String token;
    private String prefix;
    private String superUser;

    public MusicBotConfig(String fileName) {
        Properties properties = load(fileName);
        if (properties != null) {
            this.token = properties.getProperty("music.bot.token");
            this.prefix = properties.getProperty("music.bot.prefix");
            this.superUser = properties.getProperty("music.bot.superuser");
        }
    }

    private Properties load(String fileName) {
        try (InputStream input = MusicCyberBot.class.getClassLoader().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPrefix() {
        return prefix != null ? prefix : "-";
    }
}
