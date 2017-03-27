package ru.infon.queue.mongo;

import java.util.function.Consumer;

/**
 * 23.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public class MongoQueue<T> {

    private MongoQueueSerializer<T> serializer;

    public MongoQueue(MongoQueueSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public void subscribe(Consumer<T> consumer) {

    }
}
