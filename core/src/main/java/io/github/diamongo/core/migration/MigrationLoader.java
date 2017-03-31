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

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.github.diamongo.core.migration.MigrationConstants.JAVA_MIGRATIONS_FQCN;
import static io.github.diamongo.core.util.ValidationUtils.checkNotNull;

/**
 * Loads Java and Javascripts migrations.
 */
public class MigrationLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationLoader.class);

    private final MigrationRepository repository;
    private final URL additionalClassPath;
    private final List<Path> javascriptDirs;

    /**
     * Creates a new {@link MigrationLoader}. Java migrations are loaded from the classpath. An optional additional
     * classpath may be specified. Javascript migrations are loaded from the filesystem recursively walking the
     * specified directories.
     *
     * @param repository provides MongoDB access
     * @param additionalClassPath an optional additional classpath for Java migrations
     * @param javascriptDirs a list of paths for Javascript migrations, may be empty but not null
     */
    public MigrationLoader(MigrationRepository repository, URL additionalClassPath, List<Path> javascriptDirs) {
        this.repository = checkNotNull(repository,"'repository' must not be null");

        this.javascriptDirs = checkNotNull(javascriptDirs,"'javascriptDirs' must not be null. It may be empty but not null");
        javascriptDirs.forEach(path -> {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Specified Javascript migration path is not a directory: " + path);
            }
        });

        this.additionalClassPath = additionalClassPath;
    }

    /**
     * Loads all available migrations into a {@link MigrationWrappers} instance. They are returned as is without any
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

    private MigrationWrappers loadJavaMigrationWrappers() {
        try {
            LOGGER.info("Loading Java migrations...");

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
        LOGGER.info("Loading Javascript migrations...");
        // TODO implement
        return new MigrationWrappers.Builder().build();
    }
}
