package io.github.diamongo.core.mongo;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteError;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.bson.BsonDocument;
import org.bson.Document;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import static com.mongodb.ErrorCategory.DUPLICATE_KEY;
import static io.github.diamongo.core.mongo.MongoRepository.CHANGELOG_LOCK_COLLECTION;
import static io.github.diamongo.core.mongo.MongoRepository.LOCK_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class MongoRepositoryTest {

    Document document = new Document("_id", LOCK_ID);

    @Mocked
    private MongoDatabase database;

    @Mocked
    private MongoCollection<Document> collection;

    @Test
    public void testTryLock() throws Exception {
        new Expectations() {
            {
                database.getCollection(CHANGELOG_LOCK_COLLECTION);
                result = collection;
            }
        };

        MongoRepository repo = new MongoRepository(database);
        boolean actual = repo.tryLock();
        assertThat(actual).isTrue();

        new Verifications() {
            {
                collection.insertOne(withArgThat(new DocumentIdMatcher(document)));
            }
        };
    }

    @Test
    public void testTryLockFailed() throws Exception {
        MongoWriteException duplicateKeyError = new MongoWriteException(
                new WriteError(11000, "message", new BsonDocument()), new ServerAddress());

        new Expectations() {
            {
                database.getCollection(CHANGELOG_LOCK_COLLECTION);
                result = collection;

                collection.insertOne(withInstanceLike(document));
                result = duplicateKeyError;
            }
        };

        MongoRepository repo = new MongoRepository(database);
        boolean actual = repo.tryLock();
        assertThat(actual).isFalse();

        new Verifications() {
            {
                collection.insertOne(withArgThat(new DocumentIdMatcher(new Document("_id", LOCK_ID))));
                ErrorCategory category = duplicateKeyError.getError().getCategory();
                assertThat(category).isEqualTo(DUPLICATE_KEY);
            }
        };
    }

    @Test
    public void testReleaseLock() throws Exception {

    }

    @Test
    public void testWithLock() throws Exception {

    }

    static class DocumentIdMatcher extends BaseMatcher<Document> {

        private Document document;

        DocumentIdMatcher(Document document) {
            this.document = document;
        }

        @Override
        public boolean matches(Object argValue) {
            return argValue instanceof Document && ((Document) argValue).getObjectId("_id").equals(LOCK_ID);
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(document.getObjectId("_id"));
        }
    }
}
