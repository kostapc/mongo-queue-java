package ru.infon.queue.mongo;

import org.bson.Document;

/**
 * 27.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public interface QueueSerializer<T> {

    Document serialize(T object);
    T deserialize(Document document);
    Class<T> getObjectClass();

}
