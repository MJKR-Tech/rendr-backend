package com.mjkrt.rendr.utils;

import java.util.logging.Logger;

/**
 * LogsCenter.
 * 
 * This class provides relevant loggers for the various classes used in this application.
 */
public class LogsCenter {

    /**
     * Provides a Logger given a string name.
     * 
     * @param name name of instance to log under
     * @return a Logger instance
     */
    private static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }

    /**
     * Provides a Logger given a class type.
     * 
     * @param clazz class instance
     * @param <T> parameterised class instance type
     * @return a Logger based on fed class type
     */
    public static <T> Logger getLogger(Class<T> clazz) {
        if (clazz == null) {
            return Logger.getLogger("");
        }
        return getLogger(clazz.getSimpleName());
    }
}
