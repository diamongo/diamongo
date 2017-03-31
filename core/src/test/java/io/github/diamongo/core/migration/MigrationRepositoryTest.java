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
package io.github.diamongo.core.migration;

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
import org.bson.conversions.Bson;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import static io.github.diamongo.core.migration.MigrationRepository.CHANGELOG_LOCK_COLLECTION;
import static io.github.diamongo.core.migration.MigrationRepository.LOCK_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class MigrationRepositoryTest {
    private Document lock = new Document("_id", LOCK_ID);

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

        MigrationRepository repo = new MigrationRepository(database);
        boolean actual = repo.tryLock();
        assertThat(actual).isTrue();

        new Verifications() {
            {
                collection.insertOne(withArgThat(new DocumentIdMatcher(lock)));
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

                collection.insertOne(withInstanceLike(lock));
                result = duplicateKeyError;

                collection.find(withInstanceOf(Bson.class)).first();
                result = lock;
            }
        };

        MigrationRepository repo = new MigrationRepository(database);
        boolean actual = repo.tryLock();
        assertThat(actual).isFalse();

        new Verifications() {
            {
                collection.insertOne(withArgThat(new DocumentIdMatcher(new Document("_id", LOCK_ID))));
            }
        };
    }

    @Test
    public void testReleaseLock() throws Exception {
        MigrationRepository repo = new MigrationRepository(database);
        repo.releaseLock();

        new Verifications() {
            {
                collection.deleteOne(withInstanceOf(Bson.class));
            }
        };
    }

    @Test
    public void testWithLock() throws Exception {
        MigrationRepository repo = new MigrationRepository(database);

        Runnable runnable = () -> {};
        new Expectations(runnable) {};

        boolean actual = repo.withLock(runnable);
        assertThat(actual).isTrue();

        new Verifications() {{
            runnable.run();
        }};
    }

    @Test
    public void testWithLockFailed() throws Exception {
        MigrationRepository repo = new MigrationRepository(database);

        new Expectations(repo) {
            {
                repo.tryLock();
                result = false;
            }
        };

        Runnable runnable = () -> {};
        new Expectations(runnable) {};

        boolean actual = repo.withLock(runnable);
        assertThat(actual).isFalse();

        new Verifications() {
            {
                runnable.run();
                maxTimes = 0;
            }
        };
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
