package ru.infon.queue.mongo.engine;

import java.util.UUID;

/**
 * 2017-04-09
 * @author KostaPC
 * c0f3.net
 */
public class MessageContainer<T> {

    private final T message;
    private int priority;
    private UUID id;

    public MessageContainer(T message) {
        this.message = message;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public T getMessage() {
        return message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
