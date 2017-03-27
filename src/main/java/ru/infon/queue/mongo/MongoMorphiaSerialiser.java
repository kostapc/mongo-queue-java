package ru.infon.queue.mongo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

/**
 * 27.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public class MongoMorphiaSerialiser<T> implements MongoQueueSerializer<T> {

    private Class<T> objectClass;
    private ObjectMapper mapper;

    MongoMorphiaSerialiser(Class<T> objectClass) {
        this.objectClass = objectClass;
        mapper = new ObjectMapper();
    }

    @Override
    public void init(MongoConnection client) {

    }

    @Override
    public Document serialize(T object) {
        JsonNode node = mapper.valueToTree(object);
        return mapper.convertValue(node, Document.class);
    }

    @Override
    public T deserialize(Document document) {

        return mapper.convertValue(document, objectClass);
    }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }
}
