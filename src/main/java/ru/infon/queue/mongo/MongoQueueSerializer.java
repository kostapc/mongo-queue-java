package ru.infon.queue.mongo;

import org.bson.Document;

/**
 * 27.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public interface MongoQueueSerializer<T> {

    /**
     * initialize serializer on client associated with
     * @param client - current client instance
     */
    void init(MongoConnection client);

    Document serialize(T object);
    T deserialize(Document document);
    Class<T> getObjectClass();

}
