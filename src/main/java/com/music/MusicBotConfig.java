package com.music;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Proxy on 29.07.2020.
 */
public class MusicBotConfig {

    @Getter private String token;
    private String prefix;

    public MusicBotConfig(String fileName) {
        Properties properties = load(fileName);
        if (properties != null) {
            this.token = properties.getProperty("music.bot.token");
            this.prefix = properties.getProperty("music.bot.prefix");
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
