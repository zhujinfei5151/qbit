/*
 * Copyright (c) 2015. Rick Hightower, Geoff Chandler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * QBit - The Microservice lib for Java : JSON, WebSocket, REST. Be The Web!
 */

package io.advantageous.qbit.queue;

import io.advantageous.qbit.GlobalConstants;
import io.advantageous.qbit.config.PropertyResolver;
import io.advantageous.qbit.queue.impl.BasicQueue;

import java.util.Properties;
import java.util.concurrent.*;

/**
 * Allows for the programmatic construction of a queue.
 * <p>
 * created by rhightower on 12/14/14.
 */
public class QueueBuilder implements Cloneable {

    public static final String QBIT_QUEUE_BUILDER = "qbit.queue.builder.";

    private int batchSize = GlobalConstants.BATCH_SIZE;
    private int pollWait = GlobalConstants.POLL_WAIT;
    private int size = GlobalConstants.NUM_BATCHES;
    private int checkEvery = 100;
    private boolean tryTransfer = false;
    private String name;
    private Class<? extends BlockingQueue> queueClass = ArrayBlockingQueue.class;
    private boolean checkIfBusy = false;
    private TimeUnit pollTimeUnit = TimeUnit.MILLISECONDS;
    private TimeUnit enqueueTimeoutTimeUnit = TimeUnit.SECONDS;
    private int enqueueTimeout = 1000;


    public QueueBuilder(PropertyResolver propertyResolver) {
        this.pollWait = propertyResolver
                .getIntegerProperty("pollWaitMS", GlobalConstants.POLL_WAIT);
        this.enqueueTimeout = propertyResolver
                .getIntegerProperty("enqueueTimeoutSeconds", 1000);
        this.batchSize = propertyResolver
                .getIntegerProperty("batchSize", GlobalConstants.BATCH_SIZE);
        this.checkEvery = propertyResolver
                .getIntegerProperty("checkEvery", 100);
        this.size = propertyResolver
                .getIntegerProperty("size", GlobalConstants.NUM_BATCHES);
        this.checkIfBusy = propertyResolver
                .getBooleanProperty("checkIfBusy", false);
        this.tryTransfer = propertyResolver
                .getBooleanProperty("tryTransfer", false);

        this.queueClass = propertyResolver
                .getGenericPropertyWithDefault("queueClass", ArrayBlockingQueue.class);

    }

    public TimeUnit getEnqueueTimeoutTimeUnit() {
        return enqueueTimeoutTimeUnit;
    }

    public QueueBuilder setEnqueueTimeoutTimeUnit(TimeUnit enqueueTimeoutTimeUnit) {
        this.enqueueTimeoutTimeUnit = enqueueTimeoutTimeUnit;
        return this;
    }

    public TimeUnit getPollTimeUnit() {
        return pollTimeUnit;
    }

    public QueueBuilder setPollTimeUnit(TimeUnit pollTimeUnit) {
        this.pollTimeUnit = pollTimeUnit;
        return this;
    }

    public int getEnqueueTimeout() {
        return enqueueTimeout;
    }

    public QueueBuilder setEnqueueTimeout(int enqueueTimeout) {
        this.enqueueTimeout = enqueueTimeout;
        return this;
    }

    public QueueBuilder() {
        this(PropertyResolver.createSystemPropertyResolver(QBIT_QUEUE_BUILDER));
    }


    public QueueBuilder(final Properties properties) {
        this(PropertyResolver.createPropertiesPropertyResolver(
                QBIT_QUEUE_BUILDER, properties));
    }


    public static QueueBuilder queueBuilder() {
        return new QueueBuilder();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getCheckEvery() {
        return checkEvery;
    }

    public QueueBuilder setCheckEvery(int checkEvery) {
        this.checkEvery = checkEvery;
        this.checkIfBusy = true;
        return this;
    }

    public boolean isTryTransfer() {
        return tryTransfer;
    }

    public QueueBuilder setTryTransfer(boolean tryTransfer) {
        this.tryTransfer = tryTransfer;
        return this;
    }

    public Class<? extends BlockingQueue> getQueueClass() {
        return queueClass;
    }

    public void setQueueClass(Class<? extends BlockingQueue> queueClass) {
        this.queueClass = queueClass;
    }

    public QueueBuilder setLinkedBlockingQueue() {
        queueClass = LinkedBlockingQueue.class;
        return this;
    }

    public QueueBuilder setArrayBlockingQueue() {
        if (size == -1) {
            size = 100_000;
        }

        queueClass = ArrayBlockingQueue.class;
        return this;
    }


    public QueueBuilder setLinkTransferQueue() {
        size = -1;
        queueClass = LinkedTransferQueue.class;
        return this;
    }


    public int getSize() {
        return size;
    }

    public QueueBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public boolean isCheckIfBusy() {
        return checkIfBusy;
    }

    public QueueBuilder setCheckIfBusy(boolean checkIfBusy) {
        this.checkIfBusy = checkIfBusy;
        return this;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public QueueBuilder setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public int getPollWait() {
        return pollWait;
    }

    public QueueBuilder setPollWait(int pollWait) {
        this.pollWait =  pollWait;
        return this;
    }



    public String getName() {
        return name;
    }

    public QueueBuilder setName(String name) {
        this.name = name;
        return this;

    }


    public <T> Queue<T> build() {
        return new BasicQueue<>(this.getName(), this.getPollWait(), this.getPollTimeUnit(),
                this.getEnqueueTimeout(),
                this.getEnqueueTimeoutTimeUnit(),
                this.getBatchSize(),
                this.queueClass, this.isCheckIfBusy(), this.getSize(), this.getCheckEvery(), this.isTryTransfer());
    }

}
