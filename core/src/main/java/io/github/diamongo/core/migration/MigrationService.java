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

import io.github.diamongo.core.mongo.MongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static io.github.diamongo.core.migration.MigrationConstants.JAVA_MIGRATIONS_FQCN;

/**
 * Loads Java and Javascripts migrations.
 */
public class MigrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationService.class);

    private final URL additionalClassPath;
    private final List<Path> javascriptDirs;
    private final MongoRepository repository;

    /**
     * Creates a new {@link MigrationService}. Java migrations are loaded from the classpath. An optional additional
     * classpath may be specified. Javascript migrations are loaded from the filesystem recursively walking the
     * specified directories.
     *
     * @param repository provides MongoDB access
     * @param additionalClassPath an optional additional classpath for Java migrations
     * @param javascriptDirs a list of paths for Javascript migrations, may be empty but not null
     */
    public MigrationService(MongoRepository repository, URL additionalClassPath, List<Path> javascriptDirs) {
        if (repository == null) {
            throw new IllegalArgumentException("'repository' must not be null");
        }
        if (javascriptDirs == null) {
            throw new IllegalArgumentException("'javascriptDirs' must not be null. It may be empty but not null");
        }
        javascriptDirs.forEach(path -> {
            if (!Files.isDirectory(path)) {
                throw new MigrationException("Specified Javascript migration path is not a directory: " + path);
            }
        });

        this.repository = repository;
        this.additionalClassPath = additionalClassPath;
        this.javascriptDirs = Collections.unmodifiableList(javascriptDirs);
    }

    /**
     * Loads all available migrations into a {@link MigrationWrappers} instance. They are returned as is without and
     * sorting or validation performed on them.
     *
     * @return the {@link MigrationWrappers} containing all available migrations
     */
    public MigrationWrappers loadMigrationWrappers() {
        MigrationWrappers javaWrappers = loadJavaMigrationWrappers();
        MigrationWrappers javascriptWrappers = loadJavaScriptMigrationWrappers();

        return new MigrationWrappers.Builder()
                .addMigrationWrappers(javaWrappers)
                .addMigrationWrappers(javascriptWrappers)
                .build();
    }

    public void validateMigrationWrappers(MigrationWrappers migrationWrappers) {
        // TODO implement
    }

    public void runMigration(MigrationWrappers migrationWrappers) {
        if (repository.runMigration(migrationWrappers)) {
            LOGGER.info("Migration executed successfully");
        } else {
            LOGGER.info("Lock held by another process. Do nothing.");
        }
    }

    private MigrationWrappers loadJavaMigrationWrappers() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (additionalClassPath != null) {
                loader = new URLClassLoader(new URL[] {additionalClassPath}, loader);
            }

            Class<? extends Migrations> javaMigrationsClass =
                    Class.forName(JAVA_MIGRATIONS_FQCN, true, loader).asSubclass(Migrations.class);
            Migrations migrations = javaMigrationsClass.newInstance();
            return migrations.createWrappers();
        } catch (ReflectiveOperationException ex) {
            throw new MigrationException("Error loading generated class: " + JAVA_MIGRATIONS_FQCN, ex);
        }
    }

    private MigrationWrappers loadJavaScriptMigrationWrappers() {
        // TODO implement
        return new MigrationWrappers.Builder().build();
    }
}
