package com.saritasa.teamDevelopment.common.exceptions;

/**
 * Plugin exception class
 */
public class PluginException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public PluginException(String message) {
        super(message);
    }
}
