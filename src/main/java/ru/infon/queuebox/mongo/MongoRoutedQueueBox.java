package ru.infon.queuebox.mongo;

import ru.infon.queuebox.QueueBox;
import ru.infon.queuebox.RoutedMessage;

import java.util.Properties;
import java.util.concurrent.Executors;

/**
 * 07.06.2017
 * @author KostaPC
 * 2017 Infon ZED
 **/
public class MongoRoutedQueueBox<T extends RoutedMessage> extends QueueBox<T> {

    public MongoRoutedQueueBox(Properties properties, Class<T> packetCLass) {
        super(properties, packetCLass);
    }

    @Override
    public void start() {
        if (behave==null) {
            this.withQueueBehave(new RoutedQueueBehave<>(properties, packetClass));
        }
        if (this.executor==null) {
            this.executor = Executors.newFixedThreadPool(behave.getThreadsCount());
        }
        super.start();
    }
}
