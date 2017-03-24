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
        LOGGER.debug("Trying to create lock...");
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
        LOGGER.debug("Releasing lock...");

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
