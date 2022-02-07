package com.mjkrt.rendr.utils;

import java.util.logging.Logger;

public class LogsCenter {

    private static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }

    public static <T> Logger getLogger(Class<T> clazz) {
        if (clazz == null) {
            return Logger.getLogger("");
        }
        return getLogger(clazz.getSimpleName());
    }
}
