package com.music.utils;

import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Created by Proxy on 25.08.2020.
 */
@UtilityClass
public class StringUtil {

    public String hasIndex(List<String> command, int index) {
        return command.size() > index ? command.get(index) : "";
    }
}
