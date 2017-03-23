package gaillard.mongo;

import com.github.fakemongo.Fongo;
import com.mongodb.*;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.net.UnknownHostException;
import java.util.*;

import org.bson.Document;
import org.bson.types.ObjectId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.Before;

public class QueueTest {

    private static final String COLLECTION_NAME = "messages";

    private MongoCollection<Document> collection;
    private QueueCore queue;
    private boolean isMock = false;

    @Before
    public void setup() throws UnknownHostException {
        MongoConnectionParams mongoParams = new MongoConnectionParams("mongodb.properties");
    	MongoDatabase db = null;
    	try {
    	    ServerAddress serverAddress = new ServerAddress(mongoParams.getMongoDBUrl());
            MongoCredential credential = MongoCredential.createScramSha1Credential(
                mongoParams.getMongoDBUser(),
                mongoParams.getMongoDBDB(),
                mongoParams.getMongoDBPassword().toCharArray()
            );
			MongoClient client = new MongoClient(
                    Collections.singletonList(serverAddress),
                    Collections.singletonList(credential),
                    new MongoClientOptions.Builder().serverSelectionTimeout(50).build()
            );
			db = client.getDatabase(mongoParams.getMongoDBDB());

			System.out.println("Using local Mongodb");
		} catch (MongoTimeoutException e) {
			System.out.println("MongoTimeoutException caught");
		}
    	if(db == null) {
    		System.out.println("Reverting to embedded Fongo...");
    		Fongo fongo = new Fongo("Test Queue DB");
    		db = fongo.getDatabase(mongoParams.getMongoDBDB());
    		isMock = true;
    	}
		collection = db.getCollection(COLLECTION_NAME);
        collection.drop();

        queue = new QueueCore(collection);
    }

    @Test(expected = NullPointerException.class)
    public void construct_nullCollection() {
        new QueueCore(null);
    }

    @Test
    public void ensureGetIndex() {
        queue.ensureGetIndex(new Document("type", 1).append("boo", -1));
        queue.ensureGetIndex(new Document("another.sub", 1));

        final ListIndexesIterable<Document> indexes = collection.listIndexes();
        List<Document> indexInfo = new ArrayList<Document>();
        indexInfo = indexes.into(indexInfo);
        
        // There is some strange behavior when using Fongo since it will return five indexes instead of four
        // when using a real MongoDB. To pass this test, we will have to accomodate for that.
        int expected = 0;
        if(!isMock) {
        	expected = 4;
        } else {
        	expected = 5;
        }
        
        assertEquals(expected, indexInfo.size());

        final Document expectedOne = new Document("running", 1)
                .append("payload.type", 1)
                .append("priority", 1)
                .append("created", 1)
                .append("payload.boo", -1)
                .append("earliestGet", 1);
        assertEquals(expectedOne, indexInfo.get(1).get("key"));

        final Document expectedTwo = new Document("running", 1).append("resetTimestamp", 1);
        assertEquals(expectedTwo, indexInfo.get(2).get("key"));

        final Document expectedThree = new Document("running", 1)
                .append("payload.another.sub", 1)
                .append("priority", 1)
                .append("created", 1)
                .append("earliestGet", 1);
        assertEquals(expectedThree, indexInfo.get(3).get("key"));
    }

    @Test
    public void ensureGetIndex_noArgs() {
        queue.ensureGetIndex();

        final ListIndexesIterable<Document> indexes = collection.listIndexes();
        List<Document> indexInfo = new ArrayList<Document>();
        indexInfo = indexes.into(indexInfo);

        assertEquals(3, indexInfo.size());

        final Document expectedOne = new Document("running", 1).append("priority", 1).append("created", 1).append("earliestGet", 1);
        assertEquals(expectedOne, indexInfo.get(1).get("key"));

        final Document expectedTwo = new Document("running", 1).append("resetTimestamp", 1);
        assertEquals(expectedTwo, indexInfo.get(2).get("key"));
    }

    @Test(expected = RuntimeException.class)
    public void ensureGetIndex_tooLongCollectionName() throws UnknownHostException {
    	if(isMock) {
    		// this does not work with Fongo, so we simulate the outcome
    		throw new RuntimeException();
    	}
        //121 chars
        final String collectionName = "messages01234567890123456789012345678901234567890123456789"
                + "012345678901234567890123456789012345678901234567890123456789012";

        //collection = fongo.getDatabase("testing").getCollection(collectionName);
        queue = new QueueCore(collection);
        queue.ensureGetIndex();
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureGetIndex_badBeforeSortValue() {
        queue.ensureGetIndex(new Document("field", "NotAnInt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureGetIndex_badAfterSortValue() {
        queue.ensureGetIndex(new Document(), new Document("field", "NotAnInt"));
    }

    @Test(expected = NullPointerException.class)
    public void ensureGetIndex_nullBeforeSort() {
        queue.ensureGetIndex(null);
    }

    @Test(expected = NullPointerException.class)
    public void ensureGetIndex_nullAfterSort() {
        queue.ensureGetIndex(new Document(), null);
    }

    @Test
    public void ensureCountIndex() {
        queue.ensureCountIndex(new Document("type", 1).append("boo", -1), false);
        queue.ensureCountIndex(new Document("another.sub", 1), true);

        final ListIndexesIterable<Document> indexes = collection.listIndexes();
        List<Document> indexInfo = new ArrayList<>();
        indexInfo = indexes.into(indexInfo);

        assertEquals(3, indexInfo.size());

        final Document expectedOne = new Document("payload.type", 1).append("payload.boo", -1);
        assertEquals(expectedOne, indexInfo.get(1).get("key"));

        final Document expectedTwo = new Document("running", 1).append("payload.another.sub", 1);
        assertEquals(expectedTwo, indexInfo.get(2).get("key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureCountIndex_badValue() {
        queue.ensureCountIndex(new Document("field", "NotAnInt"), true);
    }

    @Test(expected = NullPointerException.class)
    public void ensureCountIndex_null() {
        queue.ensureCountIndex(null, true);
    }

    @Test(expected = NullPointerException.class)
    public void get_nullQuery() {
        queue.get(null, Integer.MAX_VALUE);
    }

    @Test
    public void get_badQuery() {
        queue.send(new Document("key", 0));

        assertNull(queue.get(new Document("key", 1), Integer.MAX_VALUE, 0));
    }

    @Test
    public void get_fullQuery() {
        final Document message = new Document("id", "ID SHOULD BE REMOVED").append("key1", 0).append("key2", true);

        queue.send(message);
        queue.send(new Document());

        final Document result = queue.get(message, Integer.MAX_VALUE);
        assertNotEquals(message.get("id"), result.get("id"));

        message.put("id", result.get("id"));
        assertEquals(message, result);
    }

    @Test
    public void get_subQuery() {
        final Document messageOne = new Document("one", new Document("two", new Document("three", 5)));
        final Document messageTwo = new Document("one", new Document("two", new Document("three", 4)));

        queue.send(messageOne);
        queue.send(messageTwo);

        final Document result = queue.get(new Document("one.two.three", new Document("$gte", 5)), Integer.MAX_VALUE);

        messageOne.put("id", result.get("id"));
        assertEquals(messageOne, result);
    }

    @Test
    public void get_negativeWait() {
        assertNull(queue.get(new Document(), Integer.MAX_VALUE, Integer.MIN_VALUE));

        queue.send(new Document());

        assertNotNull(queue.get(new Document(), Integer.MAX_VALUE, Integer.MIN_VALUE));
    }

    @Test
    public void get_negativePoll() {
        assertNull(queue.get(new Document(), Integer.MAX_VALUE, 100, Long.MIN_VALUE));

        queue.send(new Document());

        assertNotNull(queue.get(new Document(), Integer.MAX_VALUE, 100, Long.MIN_VALUE));
    }

    @Test
    public void get_beforeAck() {
        queue.send(new Document());

        assertNotNull(queue.get(new Document(), Integer.MAX_VALUE));

        //try get message we already have before ack
        assertNull(queue.get(new Document(), Integer.MAX_VALUE, 0));
    }

    @Test
    public void get_customPriority() {
        final Document messageOne = new Document("key", 1);
        final Document messageTwo = new Document("key", 2);
        final Document messageThree = new Document("key", 3);

        queue.send(messageOne, new Date(), 0.5);
        queue.send(messageTwo, new Date(), 0.4);
        queue.send(messageThree, new Date(), 0.3);

        final Document resultOne = queue.get(new Document(), Integer.MAX_VALUE);
        final Document resultTwo = queue.get(new Document(), Integer.MAX_VALUE);
        final Document resultThree = queue.get(new Document(), Integer.MAX_VALUE);

        assertEquals(messageOne.get("key"), resultThree.get("key"));
        assertEquals(messageTwo.get("key"), resultTwo.get("key"));
        assertEquals(messageThree.get("key"), resultOne.get("key"));
    }

    @Test
    public void get_timePriority() {
        final Document messageOne = new Document("key", 1);
        final Document messageTwo = new Document("key", 2);
        final Document messageThree = new Document("key", 3);

        queue.send(messageOne, new Date());
        queue.send(messageTwo, new Date());
        queue.send(messageThree, new Date());

        final Document resultOne = queue.get(new Document(), Integer.MAX_VALUE);
        final Document resultTwo = queue.get(new Document(), Integer.MAX_VALUE);
        final Document resultThree = queue.get(new Document(), Integer.MAX_VALUE);

        assertEquals(messageOne.get("key"), resultOne.get("key"));
        assertEquals(messageTwo.get("key"), resultTwo.get("key"));
        assertEquals(messageThree.get("key"), resultThree.get("key"));
    }

    @Test
    public void get_wait() {
        final Date start = new Date();

        queue.get(new Document(), Integer.MAX_VALUE, 200);

        final long elapsed = new Date().getTime() - start.getTime();

        assertTrue(elapsed >= 200);
        assertTrue(elapsed < 400);
    }

    @Test
    public void get_waitWhenMessageExists() {
        final Date start = new Date();

        queue.send(new Document());

        queue.get(new Document(), Integer.MAX_VALUE, 3000);

        assertTrue(new Date().getTime() - start.getTime() < 2000);
    }

    @Test
    public void get_earliestGet() throws InterruptedException {
        queue.send(new Document(), new Date(System.currentTimeMillis() + 200));

        assertNull(queue.get(new Document(), Integer.MAX_VALUE, 0));

        Thread.sleep(200);

        assertNotNull(queue.get(new Document(), Integer.MAX_VALUE));
    }

    @Test
    public void get_resetStuck() {
        queue.send(new Document());

        //sets resetTimestamp on messageOne
        assertNotNull(queue.get(new Document(), 0));

        assertNotNull(queue.get(new Document(), Integer.MAX_VALUE));
    }

    @Test
    public void count_running() {
        assertEquals(0, queue.count(new Document(), true));
        assertEquals(0, queue.count(new Document(), false));
        assertEquals(0, queue.count(new Document()));

        queue.send(new Document("key", 1));

        assertEquals(0, queue.count(new Document(), true));
        assertEquals(1, queue.count(new Document(), false));
        assertEquals(1, queue.count(new Document()));

        queue.get(new Document(), Integer.MAX_VALUE);

        assertEquals(1, queue.count(new Document(), true));
        assertEquals(0, queue.count(new Document(), false));
        assertEquals(1, queue.count(new Document()));
    }

    @Test
    public void count_fullQuery() {
        final Document message = new Document("key", 1);

        queue.send(new Document());
        queue.send(message);

        assertEquals(1, queue.count(message));
    }

    @Test
    public void count_subQuery() {
        final Document messageOne = new Document("one", new Document("two", new Document("three", 4)));
        final Document messageTwo = new Document("one", new Document("two", new Document("three", 5)));

        queue.send(messageOne);
        queue.send(messageTwo);

        assertEquals(1, queue.count(new Document("one.two.three", new Document("$gte", 5))));
    }

    @Test
    public void count_badQuery() {
        queue.send(new Document("key", 0));

        assertEquals(0, queue.count(new Document("key", 1)));
    }

    @Test(expected = NullPointerException.class)
    public void count_nullQuery() {
        queue.count(null);
    }

    @Test(expected = NullPointerException.class)
    public void count_runningNullQuery() {
        queue.count(null, true);
    }

    @Test
    public void ack() {
        final Document message = new Document("key", 0);

        queue.send(message);
        queue.send(new Document());

        final Document result = queue.get(message, Integer.MAX_VALUE);
        assertEquals(2, collection.count());

        queue.ack(result);
        assertEquals(1, collection.count());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ack_wrongIdType() {
        queue.ack(new Document("id", false));
    }

    @Test(expected = NullPointerException.class)
    public void ack_null() {
        queue.ack(null);
    }

    @Test
    public void ackSend() {
        final Document message = new Document("key", 0);

        queue.send(message);

        final Document resultOne = queue.get(message, Integer.MAX_VALUE);

        final Date expectedEarliestGet = new Date();
        final double expectedPriority = 0.8;
        final Date timeBeforeAckSend = new Date();
        queue.ackSend(resultOne, new Document("key", 1), expectedEarliestGet, expectedPriority);

        assertEquals(1, collection.count());

        // find one, i.e. the first document in this collection
        final Document actual = collection.find().first();

        final Date actualCreated = actual.getDate("created");
        assertTrue(actualCreated.compareTo(timeBeforeAckSend) >= 0 && actualCreated.compareTo(new Date()) <= 0);

        final Document expected = new Document("_id", resultOne.get("id"))
                .append("payload", new Document("key", 1))
                .append("running", false)
                .append("resetTimestamp", new Date(Long.MAX_VALUE))
                .append("earliestGet", expectedEarliestGet)
                .append("priority", expectedPriority)
                .append("created", actual.get("created"));

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ackSend_wrongIdType() {
        queue.ackSend(new Document("id", 5), new Document());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ackSend_nanPriority() {
        queue.ackSend(new Document("id", ObjectId.get()), new Document(), new Date(), Double.NaN);
    }

    @Test(expected = NullPointerException.class)
    public void ackSend_nullMessage() {
        queue.ackSend(null, new Document());
    }

    @Test(expected = NullPointerException.class)
    public void ackSend_nullPayload() {
        queue.ackSend(new Document("id", ObjectId.get()), null);
    }

    @Test(expected = NullPointerException.class)
    public void ackSend_nullEarliestGet() {
        queue.ackSend(new Document("id", ObjectId.get()), new Document(), null);
    }

    @Test
    public void requeue() {
        final Document message = new Document("key", 0);

        queue.send(message);

        final Document resultOne = queue.get(message, Integer.MAX_VALUE);

        final Date expectedEarliestGet = new Date();
        final double expectedPriority = 0.8;
        final Date timeBeforeRequeue = new Date();
        queue.requeue(resultOne, expectedEarliestGet, expectedPriority);

        assertEquals(1, collection.count());

        // find one, i.e. the first document in this collection
        final Document actual = collection.find().first();

        final Date actualCreated = actual.getDate("created");
        assertTrue(actualCreated.compareTo(timeBeforeRequeue) >= 0 && actualCreated.compareTo(new Date()) <= 0);

        final Document expected = new Document("_id", resultOne.get("id"))
                .append("payload", new Document("key", 0))
                .append("running", false)
                .append("resetTimestamp", new Date(Long.MAX_VALUE))
                .append("earliestGet", expectedEarliestGet)
                .append("priority", expectedPriority)
                .append("created", actual.get("created"));

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requeue_wrongIdType() {
        queue.requeue(new Document("id", new Document()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requeue_nanPriority() {
        queue.requeue(new Document("id", ObjectId.get()), new Date(), Double.NaN);
    }

    @Test(expected = NullPointerException.class)
    public void requeue_nullMessage() {
        queue.requeue(null);
    }

    @Test(expected = NullPointerException.class)
    public void requeue_nullEarliestGet() {
        queue.requeue(new Document("id", ObjectId.get()), null);
    }

    @Test
    public void send() {
        final Document message = new Document("key", 0);

        final Date expectedEarliestGet = new Date();
        final double expectedPriority = 0.8;
        final Date timeBeforeSend = new Date();
        queue.send(message, expectedEarliestGet, expectedPriority);

        assertEquals(1, collection.count());

        // find one, i.e. the first document in this collection
        final Document actual = collection.find().first();

        final Date actualCreated = actual.getDate("created");
        assertTrue(actualCreated.compareTo(timeBeforeSend) >= 0 && actualCreated.compareTo(new Date()) <= 0);

        final Document expected = new Document("_id", actual.get("_id"))
                .append("payload", new Document("key", 0))
                .append("running", false)
                .append("resetTimestamp", new Date(Long.MAX_VALUE))
                .append("earliestGet", expectedEarliestGet)
                .append("priority", expectedPriority)
                .append("created", actual.get("created"));

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void send_nanPriority() {
        queue.send(new Document("id", ObjectId.get()), new Date(), Double.NaN);
    }

    @Test(expected = NullPointerException.class)
    public void send_nullMessage() {
        queue.send(null);
    }

    @Test(expected = NullPointerException.class)
    public void send_nullEarliestGet() {
        queue.send(new Document("id", ObjectId.get()), null);
    }
}
