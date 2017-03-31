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
package io.github.diamongo.core;

import com.mongodb.MongoClient;
import io.github.diamongo.core.config.DiamongoConfig;
import io.github.diamongo.core.migration.MigrationService;
import io.github.diamongo.core.migration.MigrationWrappers;
import io.github.diamongo.core.mongo.MongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrypoint for Diamongo.
 */
public final class Diamongo {
    private static final Logger LOGGER = LoggerFactory.getLogger(Diamongo.class);

    private final DiamongoConfig config;
    private final MigrationService migrationService;

    public Diamongo(MongoClient mongoClient, DiamongoConfig config) {
        this.config = config;
        MongoRepository repository = new MongoRepository(mongoClient.getDatabase(config.getDatabase()));
        migrationService = new MigrationService(repository, config.getAdditionalClasspath(),
                config.getJavascriptDirs());
    }

    /**
     * Starts the database migration.
     */
    public void migrate() {
        MigrationWrappers migrationWrappers = migrationService.loadMigrationWrappers();
        migrationService.validateMigrationWrappers(migrationWrappers);

        // TODO
        // load existing migration
        // validate checksums
        // compute migrations to run
        // print status

        migrationService.runMigration(migrationWrappers);
    }

    /**
     * Retrieves the information about all the migrations.
     */
    public void status() {
        // NO-OP
    }

    /**
     * Validates migrations.
     */
    public void validate() {
        // NO-OP
    }

    /**
     * Clears all migrations.
     */
    public void clear() {
        // NO-OP
    }
}
