package com.music;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Proxy on 29.07.2020.
 */
public class MusicBotConfig {

    private String token;

    public MusicBotConfig(String fileName) {
        Properties properties = load(fileName);
        if (properties != null) {
            this.token = properties.getProperty("music.bot.token");
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

    public String getToken() {
        return token;
    }
}