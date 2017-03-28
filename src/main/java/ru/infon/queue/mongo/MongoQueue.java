package ru.infon.queue.mongo;

import com.mongodb.MongoClientURI;
import gaillard.mongo.MongoQueueCore;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * 23.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public class MongoQueue<T> {

    private final MongoQueueSerializer<T> serializer;
    private final MongoQueueCore queue;

    public MongoQueue(
            MongoClientURI mongoURI,
            Properties properties,
            MongoQueueSerializer<T> serializer
    ) {
        this.serializer = serializer;
        MongoConnection connection = new MongoConnection(
                properties
        );
        queue = new MongoQueueCore(
                connection.getDatabase().getCollection(connection.getMongoCollectionName())
        );
    }

    public void subscribe(Consumer<T> consumer) {
        // create thread that queue DB and fetch messages
    }

    public Future<T> queue(T message) {
        // insert in separeted threadpool
        throw new NotImplementedException();
    }
}
