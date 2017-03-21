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

import com.mongodb.client.MongoDatabase;

/**
 * Java migrations must implement this interface.
 */
public interface MongoDatabaseMigration extends Migration<MongoDatabase> {

    /**
     * Executes a Java migration.
     * @param database provides MongoDB access
     */
    void migrate(MongoDatabase database);
}
