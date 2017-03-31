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

public class MigrationService {
    private final MigrationLoader loader;
    private final MigrationValidator validator;
    private final MigrationRunner runner;

    public MigrationService(MigrationLoader loader, MigrationValidator validator, MigrationRunner runner) {
        this.loader = loader;
        this.validator = validator;
        this.runner = runner;
    }

    public void migrate() {
        MigrationWrappers migrationWrappers = loader.loadMigrationWrappers();
        validator.validateMigrationWrappers(migrationWrappers);
        runner.runMigration(migrationWrappers);
    }
}
