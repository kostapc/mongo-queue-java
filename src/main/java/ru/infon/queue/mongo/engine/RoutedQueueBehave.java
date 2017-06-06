package ru.infon.queue.mongo.engine;

import gaillard.mongo.MongoQueueCore;
import org.bson.Document;
import ru.infon.queue.mongo.MongoConnection;
import ru.infon.queue.mongo.MongoJacksonSerializer;
import ru.infon.queue.mongo.QueueSerializer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

/**
 * 06.06.2017
 * @author KostaPC
 * 2017 Infon ZED
 **/
public class RoutedQueueBehave<T> implements QueueBehave<T> {

    private static final int DEFAULT_THREAD_COUNT = 100;

    private final QueueSerializer<T> serializer;
    private final MongoConnection connection;
    private final MongoQueueCore mongoQueueCore;

    public RoutedQueueBehave(Properties properties, Class<T> packetClass) {
        this.serializer = new MongoJacksonSerializer<>(packetClass);
        this.connection = new MongoConnection(properties);
        this.mongoQueueCore = new MongoQueueCore(
                connection.getMongoCollection(Document.class)
        );
    }

    @Override
    public int getThreadsCount() {
        return this.connection.getThreadsCount();
    }

    @Override
    public void put(MessageContainer<T> event) {
        this.mongoQueueCore.send(
                serializer.serialize(event.getMessage()),
                new Date(),
                event.getPriority()
        );
    }

    @Override
    public Collection<MessageContainer<T>> find(QueueConsumer<T> consumer) {
        Document query = new Document();
        // TODO: select by destination
        // get() many with updateMany with random key and then select by this key;
        throw new NotImplementedException();
    }

    @Override
    public void remove(MessageContainer<T> packet) {
        // TODO: store original Document in MessageContainer for deleting it by _id
        throw new NotImplementedException();
    }

    @Override
    public void reset(MessageContainer<T> packet) {
        // TODO: store original Document in MessageContainer for returning it to queue
        throw new NotImplementedException();
    }
}
