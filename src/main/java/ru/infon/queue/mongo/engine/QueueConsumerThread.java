package ru.infon.queue.mongo.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Supplier;

/**
 * 29.03.2017
 * @author KostaPC
 * 2017 Infon ZED
 */
public class QueueConsumerThread<T> {

    private static final Log LOG = LogFactory.getLog(QueueConsumerThread.class);

    // TODO: move propery to external param
    private static final int PROPERTY_DEFAULT_FETCH_DELAY_MILLS = 100;

    private Executor executor;

    private TransportQueueConsumer<T> consumer;
    private QueuePacketHolder<T> packetHolder;
    private Timer timer = new Timer();

    public QueueConsumerThread(
            TransportQueueConsumer<T> consumer,
            QueuePacketHolder<T> packetHolder
    ) {
        this.consumer = consumer;
        this.packetHolder = packetHolder;
    }

    void start() {
        LOG.info(String.format(
                "starting QueueConsumerThread for %s",
                consumer.getServiceId()
        ));
        //consumerExecutor = new BoundedThreadPoolExecutor("bounded-"+consumer.getServiceId(), props.getFetchLimit());
        executor.execute(()-> runTask(this::payload));
    }

    public Collection<MessageContainer<T>> payload() {
        return packetHolder.fetch(consumer);
    }

    public void onComplete(Collection<MessageContainer<T>> result) {
        LOG.info(String.format(
            "received %d events for service %s",
            result.size(), consumer.getServiceId()
        ));
        if(result.size()==0) {
            schedule(()-> runTask(this::payload), PROPERTY_DEFAULT_FETCH_DELAY_MILLS);
        } else {
            // run task async and ACK when complete
            Iterator<MessageContainer<T>> it = result.iterator();
            while (!result.isEmpty()) {
                if(!it.hasNext()) {
                    it = result.iterator();
                }
                // if consumer has no free threads - process will wait for
                MessageContainer<T> p = it.next();
                try {
                    executor.execute(() -> {
                        consumer.onPacket(p.getMessage());
                        packetHolder.ack(p);

                    });
                    it.remove();
                } catch (RejectedExecutionException rejected) {
                    LOG.warn(String.format(
                            "[%s] task was rejected... trying again",
                            p.getId()
                    ));
                }
            }
            LOG.info("processing events done for "+consumer.getServiceId());
            runTask(this::payload);
        }
    }

    public void runTask(Supplier<Collection<MessageContainer<T>>> payload) {
        CompletableFuture.supplyAsync(payload,executor).thenAccept(this::onComplete);
    }

    void schedule(Runnable runnable, long delay) {
        timer.schedule(new LambdaTimerTask(runnable), delay);
    }

    private class LambdaTimerTask extends TimerTask {

        private Runnable runnable;

        public LambdaTimerTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }
}
