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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.diamongo.core.util.ValidationUtils.checkNotNull;

/**
 * Runs database migrations.
 */
public class MigrationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationRunner.class);

    private final MigrationRepository repository;

    /**
     * @param repository provides MongoDB access
     */
    public MigrationRunner(MigrationRepository repository) {
        this.repository = checkNotNull(repository, "'repository' must not be null");
    }

    /**
     * Runs migrations represented by the given {@link MigrationWrappers} instance.
     */
    public void runMigration(MigrationWrappers migrationWrappers) {
        if (repository.runMigration(migrationWrappers)) {
            LOGGER.info("Migration executed successfully");
        } else {

            LOGGER.info("Lock held by another process. Do nothing.");
        }
    }
}
