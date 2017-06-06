package ru.infon.queue.mongo.engine;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * 2017-04-09
 * @author KostaPC
 * c0f3.net
 */
public class MessageContainer<T> {

    public static final int PRIORITY_HIGHT = 1;
    public static final int PRIORITY_NORMAL = 4; // default priority value from documentation
    public static final int PRIORITY_LOW = 10;
    public static final int PRIORITY_DEFAULT = PRIORITY_NORMAL;

    private final T message;
    private int priority = PRIORITY_DEFAULT;
    private UUID id;
    private Consumer<MessageContainer<T>> onDone;
    private Consumer<MessageContainer<T>> onFail;

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

    void setCallback(Consumer<MessageContainer<T>> onDone, Consumer<MessageContainer<T>> onFail) {
        this.onDone = onDone;
        this.onFail = onFail;
    }

    public void done() {
        onDone.accept(this);
    }

    public void fail() {
        onFail.accept(this);
    }

    UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }
}
