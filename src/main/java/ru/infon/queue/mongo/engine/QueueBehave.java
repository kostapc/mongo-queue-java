package ru.infon.queue.mongo.engine;

import java.util.Collection;

/**
 * 06.06.2017
 * @author KostaPC
 * 2017 Infon ZED
 *
 * Class implemented whis interface provide in fact queue storage and storage behave.
 * TODO: change class and packages names to avoid fixing on just Mongo storage;
 **/
public interface QueueBehave<T> {

    int getThreadsCount();
    void put(MessageContainer<T> event);
    Collection<MessageContainer<T>> find(QueueConsumer<T> consumer);
    void remove(MessageContainer<T> packet);
    void reset(MessageContainer<T> packet);

}
