package ru.infon.queue.mongo.engine;

import java.util.Collection;

/**
 * 03.04.2017
 * @author KostaPC
 * 2017 Infon ZED
 **/
public interface QueuePacketHolder<T> {
    Collection<MessageContainer<T>> fetch(TransportQueueConsumer consumer);
    void ack(MessageContainer<T> packet);
}
