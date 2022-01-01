package io.sharpink.api.resource.forumThread.persistence;

/**
 * Strategy to decide if messages of a forum thread should be loaded when loading a forum thread.
 */
public enum MessagesLoadingStrategy {
    ENABLED, DISABLED
}
