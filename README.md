#mongo-queue-java
[![Build Status](https://travis-ci.org/gaillard/mongo-queue-java.png)](https://travis-ci.org/gaillard/mongo-queue-java)

Java message queue using MongoDB as a backend.

This version is a fork from the original version authored by [Gaillard](https://github.com/gaillard) from [here](https://github.com/gaillard/mongo-queue-java).

This fork contains an upgrade to use the latest MongoDB version with the latest Java Driver (version 3.2.2). Consequently, the code has also been upgraded to the new V3.0 Java API of MongoDB.

Additionally, I have also added the Fake MongoDB project ([Fongo](https://github.com/fakemongo/fongo)) to be used in the unit tests. Fongo is a standalone mock server simulating 90% of MongoDB completely developed in Java. While it aims to be 100% compatible, this is obviously not easy to achieve and some incompatibilities exist which are also reflected in two unit tests here.

If you want to use the mongo-queue-java together with MongoDB 3 please clone this project and checkout the 'mongo3' branch.

##Features

 * Message selection and/or count via MongoDB query
 * Distributes across machines via MongoDB
 * Multi language support through the [specification](https://github.com/dominionenterprises/mongo-queue-specification)
 * Message priority
 * Delayed messages
 * Running message timeout and redeliver
 * Atomic acknowledge and send together
 * Easy index creation based only on payload
 * Upgraded to work with the latest MongoDB 3.2 (checkout branch 'mongo3')

##Simplest use

```java
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import gaillard.mongo.Queue;
import java.net.UnknownHostException;

public final class Main {

    public static void main(final String[] args) throws UnknownHostException {
        final Queue queue = new Queue(new MongoClient().getDB("testing").getCollection("messages"));
        queue.send(new BasicDBObject());
        final BasicDBObject message = queue.get(new BasicDBObject(), 60);
        queue.ack(message);
    }
}
```

##Jar

To add the library as a jar simply [Build](#project-build) the project and use the `mongo-queue-java-1.1.0-SNAPSHOT.jar` from the created
`target` directory!

**TODO:** Promote code to version 1.1.0.

##Maven (TODO: Add project to Sonar OSS repo)

To add the library as a local, per-project dependency use [Maven](http://maven.apache.org)! Simply add a dependency on
to your project's `pom.xml` file such as:

```xml

<dependency>
    <groupId>gaillard</groupId>
    <artifactId>mongo-queue-java</artifactId>
    <version>1.1.0-SNAPSHOT</version>
</dependency>

```

##Documentation

Found in the [source](src/main/java/gaillard/mongo/Queue.java) itself, take a look!

##Contact

Developers may be contacted at:

 * [Pull Requests](https://github.com/gaillard/mongo-queue-java/pulls)
 * [Issues](https://github.com/gaillard/mongo-queue-java/issues)

##Project Build

Install and start [mongodb](http://www.mongodb.org).
With a checkout of the code get [Maven](http://maven.apache.org) in your PATH and run:

```bash
mvn clean install
```

Alternatively the `mvn clean install` can also be run without a local MongoDB thanks to Fongo.
