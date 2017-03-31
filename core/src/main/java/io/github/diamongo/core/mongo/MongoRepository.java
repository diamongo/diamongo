/*
 * Copyright © 2017 The Diamongo authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.diamongo.core.mongo;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

import static com.mongodb.ErrorCategory.DUPLICATE_KEY;
import static com.mongodb.client.model.Filters.eq;

/**
 * Repository class for handling any MongoDB access.
 */
public class MongoRepository {

    static final String CHANGELOG_COLLECTION = "diamongoChangeLog";
    static final String CHANGELOG_LOCK_COLLECTION = "diamongoChangeLog.lock";
    static final ObjectId LOCK_ID = new ObjectId(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
    static final Bson ID_FILTER = eq("_id", LOCK_ID);

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoRepository.class);
    private final MongoDatabase database;

    /**
     * Creates a new instance.
     *
     * @param database provides MongoDB access
     */
    public MongoRepository(MongoDatabase database) {
        this.database = database;
    }

    /**
     * Tries to acquire a pessimistic lock.
     *
     * @return true, if the lock could be acquired
     */
    public boolean tryLock() {
        LOGGER.info("Trying to create lock...");
        MongoCollection<Document> collection = database.getCollection(CHANGELOG_LOCK_COLLECTION);

        try {
            Document lock = new Document("_id", LOCK_ID).append("timestamp", ZonedDateTime.now());
            collection.insertOne(lock);
            LOGGER.info("Lock successfully acquired: {}", lock);
            return true;
        } catch (MongoWriteException ex) {
            if (ex.getError().getCategory() == DUPLICATE_KEY) {
                Document lock = collection.find(ID_FILTER).first();
                LOGGER.info("Cannot acquire lock. Lock already held: {}", lock);
                return false;
            }

            throw ex;
        }
    }

    /**
     * Releases a pessimistic lock.
     */
    public void releaseLock() {
        LOGGER.info("Releasing lock...");

        database.getCollection(CHANGELOG_LOCK_COLLECTION).deleteOne(ID_FILTER);
    }

    /**
     * Runs a command using pessimistic locking in order to ensure no parallel executions of the command can happen.
     *
     * @param command the command to execute
     * @return true, if the lock could be acquired and the command was executed
     */
    public boolean withLock(Runnable command) {
        if (tryLock()) {
            try {
                command.run();
            } finally {
                releaseLock();
            }
            return true;
        }

        return false;
    }
}