package ru.infon.queuebox.mongo;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import ru.infon.queuebox.QueueSerializer;

/**
 * 27.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public class MongoJacksonSerializer<T> implements QueueSerializer<T> {

    private Class<T> objectClass;
    private ObjectMapper mapper;

    public MongoJacksonSerializer(Class<T> objectClass) {
        this.objectClass = objectClass;
        mapper = new ObjectMapper();
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
