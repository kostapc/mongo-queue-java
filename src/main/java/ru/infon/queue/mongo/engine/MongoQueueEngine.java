package ru.infon.queue.mongo.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * 28.03.2017
 * @author KostaPC
 * 2017 Infon ZED
 */
public class MongoQueueEngine<T> implements QueuePacketHolder<T> {

    private static final Log LOG = LogFactory.getLog(MongoQueueEngine.class);

    public static final int PRIORITY_HIGHT = 1;
    public static final int PRIORITY_NORMAL = 4; // default priority value from documentation
    public static final int PRIORITY_LOW = 10;
    public static final int PRIORITY_DEFAULT = PRIORITY_NORMAL;

    public static final String COLLECTION_QUEUE = "transport_queue";

    private Executor executor;

    private Map<String, QueueConsumerThread> listenerThreads = new ConcurrentHashMap<>();

    public void queue(T event) {
        queue(event, PRIORITY_DEFAULT);
    }

    public void queue(T event, int priority) {
        executor.execute(()->{
            MessageContainer<T> message = new MessageContainer<>(event);
            message.setPriority(priority);
            try {
                // TODO: queue event
            } catch (Exception e) {
                LOG.error("exception while queue to mongo", e);
            }
        });
    }

    @Override
    public Collection<MessageContainer<T>> fetch(TransportQueueConsumer consumer) {
        // here we have MessageContainer<T> getted from queue, deserializing them and return
        throw new NotImplementedException();
    }

    @Override
    public void ack(MessageContainer<T> packet) {
        // here message must be removed
    }

    private void startListener(TransportQueueConsumer<T> consumer) {
        if (listenerThreads.containsKey(consumer.getServiceId())) {
            throw new IllegalStateException("service with id \""+consumer.getServiceId()+"\" already registered");
        }
        QueueConsumerThread<T> consumerThread = new QueueConsumerThread<>(consumer, this);
        listenerThreads.put(consumer.getServiceId(), consumerThread);
        consumerThread.start();
    }

    public void registerConsumer(TransportQueueConsumer<T> consumer) {
        LOG.info(String.format(
                "registering consumer for source: %s",
                consumer.getServiceId()
        ));
        startListener(consumer);
    }
}
