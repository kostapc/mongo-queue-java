package ru.infon.queue.mongo;

import ru.infon.queue.mongo.engine.*;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 23.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 *
 * Object must be singletone
 * TODO: select library name and add root interface for queue
 */
public class MongoQueue<T> {

    private QueueEngine<T> queue = null;
    private QueueBehave<T> behave = null;
    private ExecutorService executor = null;

    private final Properties properties;
    private final Class<T> packetClass;

    public MongoQueue(Properties properties, Class<T> packetCLass) {
        this.properties = properties;
        this.packetClass = packetCLass;
    }

    // TODO: chain of setters to provide behave, executor and other...
    public MongoQueue<T> withExecutorService(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public MongoQueue<T> withQueueBehave(QueueBehave<T> queueBehave) {
        this.behave = queueBehave;
        return this;
    }

    public void start() {
        if (behave==null) {
            this.behave = new RoutedQueueBehave<>(properties, packetClass);
        }
        if (this.executor==null) {
            this.executor = Executors.newFixedThreadPool(behave.getThreadsCount());
        }
        this.queue = new QueueEngine<>(behave, executor);
    }

    public void subscribe(QueueConsumer<T> consumer) {
        queue.registerConsumer(consumer);
    }

    public Future<T> queue(T message) {
        return executor.submit(()->{
            queue.queue(new MessageContainer<>(message));
            return message;
        });
    }

    public Future<T> queue(T message, int priority) {
        return executor.submit(()->{
            MessageContainer<T> messageContainer = new MessageContainer<>(message);
            messageContainer.setPriority(priority);
            queue.queue(messageContainer);
            return message;
        });
    }
}
