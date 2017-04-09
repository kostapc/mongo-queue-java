package ru.infon.queue.mongo.engine;

/**
 * 28.03.2017
 * @author KostaPC
 * 2017 Infon ZED
 */
public interface TransportQueueConsumer<T> {
    void onPacket(T message);
    String getServiceId();
}
