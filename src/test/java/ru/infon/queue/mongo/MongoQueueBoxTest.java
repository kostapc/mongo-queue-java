package ru.infon.queue.mongo;

import gaillard.mongo.MongoConnectionParams;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.infon.queue.mongo.engine.MessageContainer;
import ru.infon.queue.mongo.engine.QueueConsumer;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 07.06.2017
 * @author KostaPC
 * 2017 Infon ZED
 **/
public class MongoQueueBoxTest {

    private MongoConnectionParams mongoParams;

    @Before
    public void setup() throws UnknownHostException {
        mongoParams = new MongoConnectionParams("mongodb.properties");
        MongoConnection mongoConnection = new MongoConnection(mongoParams.getProperties());
        mongoConnection.getMongoCollection(Document.class).deleteMany(new Document());
    }

    @Test
    public void testPutAndGet() throws InterruptedException {
        final int iterations = 10;
        final String defaultSource = "just_source";
        final String defaultDestination = "just_destination";

        MongoRoutedQueueBox<JustPojoRouted> queueBox = new MongoRoutedQueueBox<>(
                mongoParams.getProperties(),
                JustPojoRouted.class
        );
        queueBox.start();
        Map<Integer, JustPojoRouted> sendPojos = new HashMap<>();
        final List<Future> futures = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            JustPojoRouted pojo = new JustPojoRouted(i, "_" + i + "_!");
            pojo.setSource(defaultSource);
            pojo.setDestination(defaultDestination);
            sendPojos.put(i,pojo);
            futures.add(queueBox.queue(pojo));
        }

        long count = 0;
        while (count<futures.size()) {
            count = futures.stream().filter(Future::isDone).count();
        }
        System.out.println("ALL INSERTED!");


        CountDownLatch door = new CountDownLatch(iterations);
        queueBox.subscribe(new QueueConsumer<JustPojoRouted>() {
            @Override
            public void onPacket(MessageContainer<JustPojoRouted> message) {
                JustPojoRouted recvPojo = message.getMessage();
                JustPojoRouted sendPojo = sendPojos.get(recvPojo.getIntValue());
                Assert.assertNotNull(recvPojo);
                Assert.assertNotNull(sendPojo);
                Assert.assertEquals(sendPojo, recvPojo);
                message.done();
                door.countDown();
            }

            @Override
            public String getConsumerId() {
                return defaultDestination;
            }
        });

        door.await(60000, TimeUnit.MILLISECONDS);

        Assert.assertEquals(0,door.getCount());
        MongoConnection mongoConnection = new MongoConnection(mongoParams.getProperties());
        Assert.assertEquals(
                0,
                mongoConnection.getMongoCollection(Document.class).count()
        );

        System.out.println("ALL DONE!");

    }

}
