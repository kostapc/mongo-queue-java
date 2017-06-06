package ru.infon.queue.mongo.engine;

/**
 * 28.03.2017
 * @author KostaPC
 * 2017 Infon ZED
 */
public interface QueueConsumer<T> {
    void onPacket(MessageContainer<T> message);
    String getConsumerId();
}
