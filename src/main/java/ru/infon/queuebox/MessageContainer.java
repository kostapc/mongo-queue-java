package ru.infon.queuebox;

import java.util.function.Consumer;

import static ru.infon.queuebox.QueueBox.PRIORITY_DEFAULT;

/**
 * 2017-04-09
 * @author KostaPC
 * c0f3.net
 */
public class MessageContainer<T> {

    private final T message;
    private int priority = PRIORITY_DEFAULT;
    private Object id;
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

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
